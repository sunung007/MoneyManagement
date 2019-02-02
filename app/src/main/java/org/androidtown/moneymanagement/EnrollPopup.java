package org.androidtown.moneymanagement;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class EnrollPopup extends AppCompatActivity {

    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference conditionRef = mRootRef.child("student");

    TextView mTitle;
    ConstraintLayout mActivity;
    ProgressBar mProgressBar;

    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManage;
    RecyclerView.Adapter adapter;

    ArrayList<StudentInfo> alreadyStudents;
    StudentInfo newStudent;
    int totalNum;
    String title;

    static Activity thisActivity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.activity_enroll_popup);

        mProgressBar = findViewById(R.id.enroll_popup_progressBar);
        mProgressBar.setVisibility(View.GONE);

        mActivity = findViewById(R.id.enroll_popup_entire);

        thisActivity = EnrollPopup.this;

        // Receive the data from enroll fragment.
        Intent intent = getIntent();
        try {
            alreadyStudents = intent.getParcelableArrayListExtra("already_students");
            newStudent = intent.getParcelableExtra("new_student");
            totalNum = intent.getIntExtra("total_num", -1);
            title = intent.getStringExtra("title");
        } catch (Exception e) {
            String message = "Loading Failed.";
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            finish();
        }

        if(title.contains("등록하시겠습니까")) {
            mActivity.setBackground(getDrawable(R.drawable.round_button));
            mActivity.setPadding(0, 0, 0, 0);
        }

        mTitle = findViewById(R.id.title_detail_info);
        mTitle.setText(title);

        mRecyclerView = findViewById(R.id.already_enrolleds_students);
        mRecyclerView.setHasFixedSize(false);

        mLayoutManage = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManage);
        adapter = new EnrollAlreadyStudentsListAdapter(alreadyStudents);
        mRecyclerView.setAdapter(adapter);

        Button buttonEnroll = findViewById(R.id.button_search_result_ok);
        Button buttonCancel = findViewById(R.id.button_detail_ok);

        buttonEnroll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                mProgressBar.setVisibility(View.VISIBLE);

                EnrollStudent enrollStudent = new EnrollStudent();
                enrollStudent.execute((Void) null);
            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    public class EnrollStudent extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... voids) {
            if(totalNum < 0)
                return false;

            DBCheckBeforeAction.EntireNumberCheck entireNumberCheck
                    = new DBCheckBeforeAction.EntireNumberCheck(totalNum);

            if(!entireNumberCheck.doInBackground())
                return false;

            String totalNumIndex = String.valueOf(totalNum);
            conditionRef.child(totalNumIndex).setValue(totalNumIndex);

            conditionRef.child(totalNumIndex).child("Sname").setValue(newStudent.Sname);
            conditionRef.child(totalNumIndex).child("Sid").setValue(newStudent.Sid);
            conditionRef.child(totalNumIndex).child("Pamount").setValue(newStudent.Pamount);
            conditionRef.child(totalNumIndex).child("Pyear").setValue(newStudent.Pyear);
            conditionRef.child(totalNumIndex).child("Ptype").setValue(newStudent.Ptype);

            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            mProgressBar.setVisibility(View.GONE);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

            String resultMessage;

            if(aBoolean) {
                resultMessage = "등록되었습니다.";
            }
            else {
                resultMessage = "등록에 실패했습니다.";
            }

            Toast toast = Toast.makeText(getApplicationContext(), resultMessage, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
            toast.show();

            finish();
        }
    }

}
