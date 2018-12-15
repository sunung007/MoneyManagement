package org.androidtown.moneymanagement;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class SearchResultPopup extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManage;
    private RecyclerView.Adapter adapter;

    private ArrayList<StudentInfo> resultStudent;
    private int mSnumber, mSupportNumber = 0;
    private String mSid;
    private String mSname;
    private String mTargetInfo;
    private String mResultAll;

    private TextView mTargetInfoView;
    private TextView mResultAllView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result_popup);

        Intent intent = getIntent();

        try {
            resultStudent = intent.getParcelableArrayListExtra("result");
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Loading Failed", Toast.LENGTH_SHORT).show();
            finish();
        }

        mSnumber = resultStudent.size();
        mSid = resultStudent.get(0).Sid;
        mSname = resultStudent.get(0).Sname;
        mTargetInfo = mSid + " " + mSname + " 검색결과";

        countStudents();

        if(mSnumber == 1) {
            if(mSupportNumber == mSnumber) {
                mResultAll = "해당 학우는 지원 대상입니다.";
            } else {
                mResultAll = "해당 학우는 지원 대상이 아닙니다.";
            }
        } else {
            if(mSupportNumber == mSupportNumber) {
                mResultAll = "전원 지원 대상입니다.";
            } else {
                mResultAll = "지원 대상이 아닌 동명이인이 있습니다.\n" +
                        "총 " + mSnumber + "명 중 " + mSupportNumber + "명 지원 대상입니다.";
            }
        }

        mTargetInfoView = (TextView) findViewById(R.id.target_info);
        mResultAllView = (TextView) findViewById(R.id.result_all);
        mResultAllView.setGravity(Gravity.CENTER_HORIZONTAL);
        mTargetInfoView.setText(mTargetInfo);
        mResultAllView.setText(mResultAll);

        mRecyclerView = findViewById(R.id.students_list);
        mRecyclerView.setHasFixedSize(false);

        mLayoutManage = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManage);
        adapter = new SearchStudentListAdapter(resultStudent);
        mRecyclerView.setAdapter(adapter);


        Button buttonOk = findViewById(R.id.button_search_result_ok);
        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    public void countStudents() {
        for(int i = 0 ; i < mSnumber ; i++) {
            if(resultStudent.get(i).Csupport.equals("YES")) {
                mSupportNumber++;
            }
        }
    }
}
