package org.androidtown.moneymanagement;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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

    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManage;
    RecyclerView.Adapter adapter;

    TextView mTargetInfoView, mResultAllView;

    ArrayList<StudentInfo> resultStudent;

    String mSid, mSname, mTargetInfo, mResultAll;
    int mSnumber, mSupportNumber = 0;
    int searchType;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result_popup);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Intent intent = getIntent();

        try {
            resultStudent = intent.getParcelableArrayListExtra("result");
            searchType = intent.getIntExtra("type", 0);
        } catch (Exception e) {
            String message = "Loading Failed.";
            Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.BOTTOM, 0, 0);
            toast.show();

            finish();
        }

        mSnumber = resultStudent.size();
        mSid = resultStudent.get(0).Sid;
        mSname = resultStudent.get(0).Sname;

        countStudents();

        if(searchType == 0) {
            mTargetInfo = mSid + " " + mSname + " 검색결과";
            mResultAll = "";
        } else {
            mTargetInfo = mSname + " 전체 검색결과";
            mResultAll = "총 \"" + mSnumber + "\" 명 검색되었습니다.\n";
        }

        if(mSnumber == 1) {
            if(mSupportNumber == mSnumber) {
                mResultAll += "해당 학우는 지원 대상입니다.";
            } else {
                mResultAll += "해당 학우는 지원 대상이 아닙니다.";
            }
        } else {
            if(mSupportNumber == mSnumber) {
                mResultAll += "전원 지원 대상입니다.";
            } else {
                mResultAll += "지원 대상이 아닌 동명이인이 있습니다.\n" +
                        "총 " + mSnumber + "명 중 " + mSupportNumber + "명 지원 대상입니다.";
            }
        }

        mTargetInfoView = findViewById(R.id.target_info);
        mResultAllView = findViewById(R.id.result_all);
        mResultAllView.setGravity(Gravity.CENTER_HORIZONTAL);
        mTargetInfoView.setText(mTargetInfo);
        mResultAllView.setText(mResultAll);

        mRecyclerView = findViewById(R.id.students_list);
        mRecyclerView.setHasFixedSize(false);

        mLayoutManage = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManage);

        if(searchType == 0) {
            adapter = new SearchStudentListAdapter(resultStudent);
            mRecyclerView.setAdapter(adapter);
        } else {
            adapter = new SearchStudentDifferentListAdapter(resultStudent);
            mRecyclerView.setAdapter(adapter);
        }


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
