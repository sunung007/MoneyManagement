package org.androidtown.moneymanagement.Enroll;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.androidtown.moneymanagement.Common.DBHelper;
import org.androidtown.moneymanagement.Common.Mode;
import org.androidtown.moneymanagement.Common.Special;
import org.androidtown.moneymanagement.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Objects;

public class EnrollNewbieActivity extends AppCompatActivity {

    private int year;
    private long backKeyPressedTime = 0;
    private ArrayList<String> students;

    @SuppressLint("StaticFieldLeak")
    private static EditText mStudentsText;

    @SuppressLint("StaticFieldLeak")
    private static ProgressBar mProgressBar;
    private static Window thisWindow;
    @SuppressLint("StaticFieldLeak")
    private static Activity thisActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enroll_newbie);

        String mSid;
        String mYear;

        Toolbar mToolbar;
        TextView mSidView;
        TextView mYearView;
        Button mEnrollButton;


        mToolbar = findViewById(R.id.toolbar_enroll_newbie);
        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        year = Calendar.getInstance().get(Calendar.YEAR) - 2000;
        students = new ArrayList<>();
        thisWindow = getWindow();
        thisActivity = this;

        mSid = year + getString(R.string.unit_sid);
        mSidView = findViewById(R.id.text_enroll_newbie_sid);
        mSidView.setText(mSid);

        mYear = year + getString(R.string.unit_year);
        mYearView = findViewById(R.id.text_enroll_newbie_year);
        mYearView.setText(mYear);

        mStudentsText = findViewById(R.id.edit_enroll_newbie_students);
        mEnrollButton = findViewById(R.id.button_enroll_newbie_enroll);
        mEnrollButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enrollStudents();
            }
        });

        mProgressBar = findViewById(R.id.progressBar_enroll_newbie);
        mProgressBar.setVisibility(View.GONE);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            if (mStudentsText.getText().length() > 0
                    && System.currentTimeMillis() - backKeyPressedTime >= 2000) {
                // TODO: 경고창
                backKeyPressedTime = System.currentTimeMillis();
                Special.printMessage(getApplicationContext(), R.string.caution_enroll_newbie_no_save);
            } else {
                finish();
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (mStudentsText.getText().length() > 0) {
            // TODO: 경고창
            if (System.currentTimeMillis() - backKeyPressedTime >= 2000) {
                backKeyPressedTime = System.currentTimeMillis();
                Special.printMessage(getApplicationContext(), R.string.caution_enroll_newbie_no_save);
            } else finish();
        } else {
            super.onBackPressed();
        }
    }

    public void enrollStudents() {
        students.clear();
        mStudentsText.setError(null);
        if (mStudentsText.getText().length() < 1) {
            mStudentsText.setError(getResources().getString(R.string.error_field_required));
            mStudentsText.requestFocus();
        } else {
            Special.startLoad(thisWindow, mProgressBar);

            String studentList = mStudentsText.getText().toString().trim();
            Collections.addAll(students, studentList.split("\n"));

            for(String iter : students) {
                if(iter.length() < 1)
                    students.remove(iter);
            }

            // TODO: 총 ~명의 학생이 등록됩니다.

            DBHelper.EnrollMultiTask enrollMultiTask
                    = new DBHelper.EnrollMultiTask(year, students, Mode.ENROLL_NEWBIE_ACTIVITY);
            enrollMultiTask.execute((Void) null);
        }
    }


    public static void onPost(boolean result) {
        Special.finishLoad(thisWindow, mProgressBar);

        if (result) {
            Special.printMessage(thisActivity, R.string.caution_enroll_success);
            mStudentsText.getText().clear();
        } else {
            Special.printMessage(thisActivity, R.string.caution_enroll_fail);
        }

    }
}
