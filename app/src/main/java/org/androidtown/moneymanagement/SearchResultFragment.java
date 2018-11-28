package org.androidtown.moneymanagement;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;


public class SearchResultFragment extends Fragment {

    private ArrayList<StudentInfo> students;
    private int mSnumber, mSupportNumber = 0;
    private String mSid;
    private String mSname;
    private String mTargetInfo;
    private String mResultAll;

    private TextView mTargetInfoView;
    private TextView mResultAllView;

    private RecyclerView mRecyclerview;
    private RecyclerView.LayoutManager mLayoutManager;


    public SearchResultFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            students = savedInstanceState.getParcelableArrayList("students");
            mSnumber = students.size();
            mSid = students.get(0).Sid;
            mSname = students.get(0).Sname;
            mTargetInfo = mSid + " " + mSname;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_search_result, container, false);

        students = new ArrayList<>(((SearchStudentFragment)getParentFragment()).getTarget());

        mSnumber = students.size();
        mSid = students.get(0).Sid;
        mSname = students.get(0).Sname;
        mTargetInfo = mSid + " " + mSname;

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

        mTargetInfoView = (TextView) view.findViewById(R.id.target_info);
        mResultAllView = (TextView) view.findViewById(R.id.result_all);
        mResultAllView.setGravity(Gravity.CENTER_HORIZONTAL);
        mTargetInfoView.setText(mTargetInfo);
        mResultAllView.setText(mResultAll);


        // Recycler view setting.
        mRecyclerview = view.findViewById(R.id.students_list);
        mLayoutManager = new LinearLayoutManager(view.getContext());
        mRecyclerview.setLayoutManager(mLayoutManager);

        SearchStudentListAdapter adapter = new SearchStudentListAdapter(students);
        mRecyclerview.setAdapter(adapter);

        return view;
    }

    public void countStudents() {
        for(int i = 0 ; i < mSnumber ; i++) {
            if(students.get(i).Csupport.equals("YES")) {
                mSupportNumber++;
            }
        }
    }

    @Override
    public void onAttachFragment(Fragment childFragment) {
        super.onAttachFragment(childFragment);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }
}
