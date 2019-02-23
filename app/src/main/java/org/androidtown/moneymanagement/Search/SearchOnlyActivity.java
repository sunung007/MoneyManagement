package org.androidtown.moneymanagement.Search;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.androidtown.moneymanagement.Common.DBHelper;
import org.androidtown.moneymanagement.Common.Mode;
import org.androidtown.moneymanagement.R;
import org.androidtown.moneymanagement.Common.Student;

import java.util.ArrayList;
import java.util.Objects;

public class SearchOnlyActivity extends AppCompatActivity {

    public int studentNumber;
    private static boolean isLoading;

    private static ArrayList<Student> students;
    private ArrayList<Student> searchResult;

    @SuppressLint("StaticFieldLeak")
    private static ProgressBar mProgressBar;
    @SuppressLint("StaticFieldLeak")
    public static EditText mSnameView;
    @SuppressLint("StaticFieldLeak")
    private static Context thisContext;
    private static Window thisWindow;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_only);

        Toolbar mToolbar = findViewById(R.id.search_only_toolbar);
        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        thisContext = getApplicationContext();
        thisWindow = getWindow();

        students = new ArrayList<>();
        searchResult = new ArrayList<>();
        isLoading = false;

        mProgressBar = findViewById(R.id.search_only_progressBar);
        mProgressBar.setVisibility(View.GONE);

        mSnameView = findViewById(R.id.search_only_sname);
        mSnameView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if(i == KeyEvent.KEYCODE_ENTER) {
                    searchStudent(view);
                    return true;
                }
                return false;
            }
        });

        Button mSearchButton = findViewById(R.id.search_only_search_button);
        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchStudent(view);
            }
        });

        setAllStudent();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search_only, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home) {
            if(isLoading) {
                String message = getResources().getString(R.string.caution_db_on_load);
                Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM, 0, 0);
                toast.show();
            }
            else {
                finish();
                return true;
            }
        }
        else if(id == R.id.search_only_toolbar_refresh) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
            assert imm != null;
            imm.hideSoftInputFromWindow(mSnameView.getWindowToken(), 0);

            mSnameView.getText().clear();

            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            Fragment fragment = fm.findFragmentById(R.id.search_only_result_fragment);

            if(fragment != null && fragment.isVisible()) {
                ft.remove(fragment);
                ft.commit();
            }

            setAllStudent();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if(isLoading) {
            String message = getResources().getString(R.string.caution_db_on_load);
            Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM, 0, 0);
            toast.show();
        }
        else {
            super.onBackPressed();
        }
    }

    private void setAllStudent () {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        mProgressBar.setVisibility(View.VISIBLE);

        DBHelper.SearchTask searchTask = new DBHelper.SearchTask(true, Mode.SEARCH_ONLY_ACTIVITY);
        searchTask.execute((Void) null);
    }

    public void searchStudent (View view) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment fragment = fm.findFragmentById(R.id.search_only_result_fragment);

        if(fragment != null && fragment.isVisible()) {
            ft.remove(fragment);
            ft.commit();
        }

        boolean cancel = false;

        studentNumber = students.size();
        mSnameView.setError(null);
        searchResult.clear();
        String mSname = mSnameView.getText().toString().trim();


        if (TextUtils.isEmpty(mSname)) {
            mSnameView.setError(getString(R.string.error_field_required));
            cancel = true;
        }

        if (cancel) {
            mProgressBar.setVisibility(View.GONE);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

            mSnameView.requestFocus();
        }
        else {
            for(Student iter : students) {
                if(iter.name.equals(mSname)) {
                    searchResult.add(iter);
                }
            }

            if(searchResult.size() > 0) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                assert imm != null;
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                fragment = new SearchOnlyResultFragment();
                fm = getSupportFragmentManager();
                ft = fm.beginTransaction();

                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("students", searchResult);
                fragment.setArguments(bundle);

                ft.replace(R.id.search_only_result_fragment, fragment);
                ft.commit();

            }
            else {
                String message = getResources().getString(R.string.caution_no_student);
                Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.BOTTOM, 0, 0);
                toast.show();
            }
        }
    }


    public static void onPost(ArrayList<Student> _students, int _totalNum) {
        students = new ArrayList<>(_students);
        mProgressBar.setVisibility(View.GONE);
        thisWindow.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        isLoading = false;

        InputMethodManager imm = (InputMethodManager) thisContext.getSystemService(Activity.INPUT_METHOD_SERVICE);
        assert imm != null;
        imm.showSoftInput(mSnameView, 0);

        if(students == null || students.size() != _totalNum) {
            String message = thisContext.getResources().getString(R.string.caution_db_load_fail);
            Toast toast = Toast.makeText(thisContext, message, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.BOTTOM, 0, 0);
            toast.show();
        }
    }

}
