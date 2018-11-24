package org.androidtown.moneymanagement;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private Students mAuthTask = null;

    private Spinner mSidView;
    private EditText mSnameView;
    private View mProgressView;
    private View mSearchView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mSidView = (Spinner) findViewById(R.id.sid);
        mSnameView = (EditText) findViewById(R.id.sname);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Button studentSearchButton = (Button) findViewById(R.id.student_search_button);
        studentSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchStudent();
            }
        });

        mSearchView = findViewById(R.id.search_form);
        mProgressView = findViewById(R.id.search_progress);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        if (id == R.id.nav_enroll) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.developer) {
            fragmentTransaction.replace(R.id.developer, new DevelopersFragment());
            fragmentTransaction.commit();

        } else if (id == R.id.logout) {
            Toast toast = Toast.makeText(getApplicationContext(), "로그아웃", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.BOTTOM, 0, 0);
            toast.show();

            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);

            startActivity(intent);
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void searchStudent() {
        // Reset errors.
        mSnameView.setError(null);

        // Store values at the time of the search attempt.
        String sid = mSidView.getSelectedItem().toString();
        String sname = mSnameView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a nonempty name.
        if (TextUtils.isEmpty(sid)) {
            mSnameView.setError(getString(R.string.error_field_required));
            focusView = mSnameView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the search attempt.
            showProgress(true);
            mAuthTask = new Students(sid, sname);
            mAuthTask.execute((Void) null);
        }
    }

    /**
     * Shows the progress UI and hides the login form.
     */

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mSearchView.setVisibility(show ? View.GONE : View.VISIBLE);
            mSearchView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mSearchView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mSearchView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }



    public class Students extends AsyncTask<Void, Void, Boolean> {

        private final String mSid;
        private final String mSname;

        private DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
        private DatabaseReference conditionRef = mRootRef.child("student");

        ArrayList<StudentInfo> target = new ArrayList<>();

        Students(String _sid, String _sname) {
            this.mSid = _sid;
            this.mSname = _sname;
        }


        @Override
        protected Boolean doInBackground(Void... params) {
            // Check whether the student is in DB.

            conditionRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    target.clear();
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        if(snapshot.child("Sid").getValue().toString().equals(mSid)
                                && snapshot.child("Sname").getValue().equals(mSname)) {
                            StudentInfo st = snapshot.getValue(StudentInfo.class);
                            target.add(st);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
//                    Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                }
            });

            if(target.size() > 0) return true;
            else return false;
//
//            if(conditionRef.child("Sid").equals(mSid)) {
//
//                return true;
//            } else return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            Toast.makeText(getApplicationContext(), "mSid: " + mSid + "\nmSname: " + mSname, Toast.LENGTH_LONG).show();

            if (success) {
                // I must change this line

//                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//                startActivity(intent);

//                finish();
            } else {
                String tmp = conditionRef.child("0").child("Sname").getRef().toString();
                Toast toast = Toast.makeText(getApplicationContext(), tmp, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                toast.show();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
//            showProgress(false);
        }

    }

    public class StudentInfo {
        private String mPamount;
        private String mPtype;
        private String mPyear;

        private String mSid;
        private String mSname;

        StudentInfo(String _Pamount, String _Ptype, String _Pyear, String _Sid, String _Sname) {
            mPamount = _Pamount;
            mPtype = _Ptype;
            mPyear = _Pyear;
            mSid = _Sid;
            mSname = _Sname;
        }

        public void setmPamount(String mPamount) {
            this.mPamount = mPamount;
        }
        public void setmPtype(String mPtype) {
            this.mPtype = mPtype;
        }
        public void setmPyear(String mPyear) {
            this.mPyear = mPyear;
        }
        public void setmSid(String mSid) {
            this.mSid = mSid;
        }
        public void setmSname(String mSname) {
            this.mSname = mSname;
        }

        public String getmPamount() {
            return mPamount;
        }
        public String getmPtype() {
            return mPtype;
        }
        public String getmPyear() {
            return mPyear;
        }
        public String getmSid() {
            return mSid;
        }
        public String getmSname() {
            return mSname;
        }
    }
}


