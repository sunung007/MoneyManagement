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
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import org.androidtown.moneymanagement.Common.DBHelper;
import org.androidtown.moneymanagement.Common.Mode;
import org.androidtown.moneymanagement.Common.Special;
import org.androidtown.moneymanagement.Common.Student;
import org.androidtown.moneymanagement.R;

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
    public static EditText mNameText;
    @SuppressLint("StaticFieldLeak")
    private static Context thisContext;
    private static Window thisWindow;
    @SuppressLint("StaticFieldLeak")
    private static Activity thisActivity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_only);

        Toolbar mToolbar = findViewById(R.id.toolbar_search_only);
        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        thisContext = getApplicationContext();
        thisWindow = getWindow();
        thisActivity = this;

        students = new ArrayList<>();
        searchResult = new ArrayList<>();
        isLoading = false;

        mProgressBar = findViewById(R.id.progressBar_search_only);
        mProgressBar.setVisibility(View.GONE);

        mNameText = findViewById(R.id.edit_search_only_name);
        mNameText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if(i == KeyEvent.KEYCODE_ENTER) {
                    searchStudent(view);
                    return true;
                }
                return false;
            }
        });

        Button mSearchButton = findViewById(R.id.button_search_only);
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
                Special.printMessage(getApplicationContext(), R.string.caution_db_on_load);
            }
            else {
                finish();
                return true;
            }
        }
        else if(id == R.id.search_only_toolbar_refresh) {
            Special.closeKeyboard(this);

            mNameText.getText().clear();

            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            Fragment fragment = fm.findFragmentById(R.id.fragment_search_only_result);

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
        if(isLoading)
            Special.printMessage(getApplicationContext(), R.string.caution_db_on_load);
        else super.onBackPressed();
    }

    private void setAllStudent () {
        isLoading = Special.startLoad(getWindow(), mProgressBar);

        DBHelper.SearchTask searchTask = new DBHelper.SearchTask(true, Mode.SEARCH_ONLY_ACTIVITY);
        searchTask.execute((Void) null);
    }

    public void searchStudent (View view) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment fragment = fm.findFragmentById(R.id.fragment_search_only_result);

        if(fragment != null && fragment.isVisible()) {
            ft.remove(fragment);
            ft.commit();
        }


        studentNumber = students.size();
        mNameText.setError(null);
        searchResult.clear();
        String mName = mNameText.getText().toString().trim();


        if (TextUtils.isEmpty(mName)) {
            isLoading = Special.finishLoad(getWindow(), mProgressBar);
            mNameText.setError(getString(R.string.error_field_required));
            mNameText.requestFocus();
        } else {
            for(Student iter : students) {
                if(iter.name.equals(mName))
                    searchResult.add(iter);
            }

            if(searchResult.size() > 0) {
                Special.closeKeyboard(this);

                fragment = new SearchOnlyResultFragment();
                fm = getSupportFragmentManager();
                ft = fm.beginTransaction();

                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("students", searchResult);
                fragment.setArguments(bundle);

                ft.replace(R.id.fragment_search_only_result, fragment);
                ft.commit();

            }
            else Special.printMessage(getApplicationContext(), R.string.caution_no_student);
        }
    }


    public static void onPost(ArrayList<Student> _students, int _totalNum) {
        isLoading = Special.finishLoad(thisWindow, mProgressBar);
        students = new ArrayList<>(_students);

        Special.closeKeyboard(thisActivity);

        if(students == null || students.size() != _totalNum) {
            Special.printMessage(thisContext, R.string.caution_db_load_fail);
        }
    }

}
