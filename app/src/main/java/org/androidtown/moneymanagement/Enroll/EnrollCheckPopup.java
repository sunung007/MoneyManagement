package org.androidtown.moneymanagement.Enroll;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.androidtown.moneymanagement.Common.DBHelper;
import org.androidtown.moneymanagement.Common.Mode;
import org.androidtown.moneymanagement.Common.Special;
import org.androidtown.moneymanagement.Common.Student;
import org.androidtown.moneymanagement.R;

import java.util.ArrayList;

public class EnrollCheckPopup extends AppCompatActivity {

    private int totalNum;
    private Student newStudent;

    @SuppressLint("StaticFieldLeak")
    private static ProgressBar mProgressBar;
    @SuppressLint("StaticFieldLeak")
    public static Activity thisActivity;
    private static Window thisWindow;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.popup_enroll_check);

        thisActivity = EnrollCheckPopup.this;
        thisWindow = getWindow();

        mProgressBar = findViewById(R.id.progressBar_enroll_check);
        mProgressBar.setVisibility(View.GONE);


        Intent intent = getIntent();
        ArrayList<Student> alreadyStudents = intent.getParcelableArrayListExtra("already_students");
        String mTitle = intent.getStringExtra("title");
        newStudent = intent.getParcelableExtra("new_student");
        totalNum = intent.getIntExtra("total_num", -1);


        if(mTitle.contains(getResources().getString(R.string.title_enroll_check))) {
            ConstraintLayout mActivity = findViewById(R.id.constraint_enroll_check);
            mActivity.setBackground(getDrawable(R.drawable.round_button));
            mActivity.setPadding(0, 0, 0, 0);
        }

        TextView mTitleView = findViewById(R.id.text_enroll_check_title);
        mTitleView.setText(mTitle);

        RecyclerView.LayoutManager mLayoutManage = new LinearLayoutManager(getApplicationContext());
        RecyclerView.Adapter adapter = new EnrollAlreadyListAdapter(alreadyStudents);
        RecyclerView mRecyclerView = findViewById(R.id.recycler_enroll_check);
        mRecyclerView.setHasFixedSize(false);
        mRecyclerView.setLayoutManager(mLayoutManage);
        mRecyclerView.setAdapter(adapter);


        Button mEnrollButton = findViewById(R.id.button_enroll_check_ok);
        mEnrollButton.setOnClickListener(mEnrollButtonOnClickListener);

        Button mCancelButton = findViewById(R.id.button_enroll_check_cancel);
        mCancelButton.setOnClickListener(mCancelButtonOnClickListener);
    }


    private View.OnClickListener mEnrollButtonOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Special.startLoad(getWindow(), mProgressBar);

            DBHelper.EnrollTask enrollTask
                    = new DBHelper.EnrollTask(totalNum, newStudent, Mode.ENROLL_CHECK_POPUP);
            enrollTask.execute((Void) null);
        }
    };

    private View.OnClickListener mCancelButtonOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            finish();
        }
    };


    public static void onPost(boolean result) {
        String message = thisActivity.getResources()
                .getString(result ? R.string.caution_enroll_success : R.string.caution_enroll_fail);

        Special.finishLoad(thisWindow, mProgressBar);
        Special.printMessage(thisActivity.getApplicationContext(), message);

        thisActivity.finish();
    }

}
