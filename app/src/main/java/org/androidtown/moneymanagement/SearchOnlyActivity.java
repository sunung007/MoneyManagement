package org.androidtown.moneymanagement;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;

public class SearchOnlyActivity extends AppCompatActivity {

    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference conditionRef = mRootRef.child("student");
    ValueEventListener valueEventListener;

    Toolbar mToolbar;
    ProgressBar mProgressBar;
    EditText mSnameView;

    LoadingAllStudetnTask mAuthTask = null;

    String mSname;
    int studentNumber;

    ArrayList<StudentInfo> allStudentInfos;
    ArrayList<StudentInfo> searchStudentInfos;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_only);

        mToolbar = findViewById(R.id.search_only_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        allStudentInfos = new ArrayList<>();
        searchStudentInfos = new ArrayList<>();

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
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void setAllStudent () {
        // Set progress bar to visible.
        mProgressBar.setVisibility(View.VISIBLE);

        mAuthTask = new LoadingAllStudetnTask();
        mAuthTask.execute((Void) null);
    }

    public void searchStudent (View view) {
        {
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            Fragment fragment = fm.findFragmentById(R.id.search_only_result_fragment);

            if(fragment != null && fragment.isVisible()) {
                ft.remove(fragment);
                ft.commit();
            }
        }

        studentNumber = allStudentInfos.size();

        // Reset errors and array.
        mSnameView.setError(null);
        searchStudentInfos.clear();

        mSname = mSnameView.getText().toString().trim();

        boolean cancel = false;
        View focusView = null;

        // Check for a nonempty name.
        if (TextUtils.isEmpty(mSname)) {
            mSnameView.setError(getString(R.string.error_field_required));
            focusView = mSnameView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            mProgressBar.setVisibility(View.GONE);
            focusView.requestFocus();
        }
        else {
            for(StudentInfo iter : allStudentInfos) {
                if(iter.Sname.equals(mSname)) {
                    searchStudentInfos.add(iter);
                }
            }

            if(searchStudentInfos.size() > 0) {
                // Close keypad.
                InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                Fragment fragment = new SearchOnlyResultFragment();
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();

                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("students", searchStudentInfos);
                fragment.setArguments(bundle);

                ft.replace(R.id.search_only_result_fragment, fragment);
                ft.commit();
            }
            else {
                // If the result array list is empty, which means
                // the student that user put in is not in DB, just float Toast.
                String message = "찾는 대상이 납부자 명단에 없습니다.";
                Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.BOTTOM, 0, 0);
                toast.show();
            }
        }
    }

    public class LoadingAllStudetnTask extends AsyncTask<Void, Void, Boolean> {

        int totalNum;

        private LoadingAllStudetnTask () {
            totalNum = 0;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            // Initialize array.
            totalNum = 0;
            while(!allStudentInfos.isEmpty())
                allStudentInfos.clear();

            valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    // Start loading process.
                    Iterator<DataSnapshot> child = dataSnapshot.getChildren().iterator();
                    DataSnapshot ds;

                    String index;
                    String tName, tId, tAmount, tType, tYear, cSupport;

                    double currentYear, tmpAll;
                    int tmpYear, tmpType;
                    currentYear = Calendar.getInstance().get(Calendar.YEAR)
                            + (Calendar.getInstance().get(Calendar.MONTH) + 1.0)/12;

                    totalNum = (int) dataSnapshot.getChildrenCount();

                    while(child.hasNext()) {
                        ds = child.next();

                        index = ds.getKey().toString();
                        tName = ds.child("Sname").getValue().toString();
                        tId = ds.child("Sid").getValue().toString();

                        tAmount = ds.child("Pamount").getValue().toString();
                        tType = ds.child("Ptype").getValue().toString();
                        tYear = ds.child("Pyear").getValue().toString();

                        if(tType.contains("전액")) {
                            cSupport = "YES";
                        } else if(tType.contains("미납")) {
                            cSupport = "NO";
                        } else if(tType.contains("학기")) {
                            // The condition is about whether a person can support by student's money.
                            try {
                                tmpYear = Integer.parseInt(tYear.substring(0, 2), 10) + 2000;
                                tmpType = Integer.parseInt(tAmount.substring(0, 1), 10);

                                tmpAll = tmpYear + tmpType/2.0;

                                cSupport = (tmpAll >= currentYear) ? "YES" : "NO";
                            } catch (Exception e) {
                                cSupport = "UNKNOWN";
                            }
                        } else {
                            cSupport = "UNKNOWN";
                        }

                        allStudentInfos.add(new StudentInfo(index, tAmount, tType, tYear, tId, tName, cSupport));

                    }
                }

                // Just formal.
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    while(!allStudentInfos.isEmpty())
                        allStudentInfos.clear();
                }

            };


            // Add event listener of DB.
            conditionRef.addListenerForSingleValueEvent(valueEventListener);

            // Sync the code with DB server.
            try {
                long startTime = System.currentTimeMillis();
                long progressTime;

                while (allStudentInfos.isEmpty())
                    Thread.sleep(500);

                while (allStudentInfos.size() != totalNum) {
                    Thread.sleep(500);

                    progressTime = System.currentTimeMillis();
                    if (progressTime - startTime > 2500) {
                        return false;
                    }
                }

                return true;
            } catch (Exception e) {
                return false;
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            mProgressBar.setVisibility(View.GONE);

            if (!success) {
                // If the result array list is empty, which means
                // the student that user put in is not in DB, just float Toast.
                String message = "Loading Failed.";
                Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.BOTTOM, 0, 0);
                toast.show();

                finish();
            }

            // Remove event listener to reuse another fragments.
            conditionRef.removeEventListener(valueEventListener);
        }

        // Just formal.
        @Override
        protected void onCancelled() {
            mAuthTask = null;
            mProgressBar.setVisibility(View.GONE);
        }
    }

}
