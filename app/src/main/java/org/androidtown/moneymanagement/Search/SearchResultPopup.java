package org.androidtown.moneymanagement.Search;

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

import org.androidtown.moneymanagement.Common.Student;
import org.androidtown.moneymanagement.R;

import java.util.ArrayList;

public class SearchResultPopup extends AppCompatActivity {

    private int mNumber;
    private int searchType;
    private int mSupportNumber = 0;

    private String mSid;
    private String mName;
    private String mTargetInfo;
    private String mResultAll;

    private ArrayList<Student> resultStudent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_search_result);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Intent intent = getIntent();
        resultStudent = intent.getParcelableArrayListExtra("result");
        searchType = intent.getIntExtra("type", 0);

        mNumber = resultStudent.size();
        mSid = resultStudent.get(0).sid;
        mName = resultStudent.get(0).name;

        countStudents();
        setResult();


        TextView mTargetInfoView = findViewById(R.id.text_search_result_target_info);
        mTargetInfoView.setText(mTargetInfo);

        TextView mResultAllView = findViewById(R.id.text_search_result_all);
        mResultAllView.setGravity(Gravity.CENTER_HORIZONTAL);
        mResultAllView.setText(mResultAll);


        RecyclerView.LayoutManager mLayoutManage = new LinearLayoutManager(getApplicationContext());
        RecyclerView mRecyclerView = findViewById(R.id.recycler_search_result);
        RecyclerView.Adapter adapter;

        if (searchType == 0)
            adapter = new SearchResultListAdapter(resultStudent);
        else
            adapter = new SearchOnlyResultListAdapter(resultStudent);

        mRecyclerView.setHasFixedSize(false);
        mRecyclerView.setLayoutManager(mLayoutManage);
        mRecyclerView.setAdapter(adapter);


        Button mOkButton = findViewById(R.id.button_search_result);
        mOkButton.setOnClickListener(mOkButtonOnClickListener);
    }

    View.OnClickListener mOkButtonOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            finish();
        }
    };

    public void countStudents() {
        for(int i = 0 ; i < mNumber ; i++) {
            if(resultStudent.get(i).support.equals("YES"))
                mSupportNumber++;
        }
    }


    // TODO: 코드 수정
    private void setResult() {
        if(searchType == 0) {
            mTargetInfo = mSid + " " + mName + " 검색결과";
            mResultAll = "";
        } else {
            mTargetInfo = mName + " 전체 검색결과";
            mResultAll = "총 \"" + mNumber + "\" 명 검색되었습니다.\n";
        }

        if(mNumber == 1) {
            if (mSupportNumber == mNumber)
                mResultAll += "해당 학우는 지원 대상입니다.";
            else
                mResultAll += "해당 학우는 지원 대상이 아닙니다.";
        }
        else {
            if(mSupportNumber == mNumber)
                mResultAll += "전원 지원 대상입니다.";
            else {
                mResultAll += "지원 대상이 아닌 동명이인이 있습니다.\n"
                        + "총 "
                        + mNumber + "명 중 "
                        + mSupportNumber
                        + "명 지원 대상입니다.";
            }
        }
    }
}
