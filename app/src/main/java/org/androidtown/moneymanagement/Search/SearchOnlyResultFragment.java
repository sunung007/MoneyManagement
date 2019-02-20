package org.androidtown.moneymanagement.Search;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.androidtown.moneymanagement.R;
import org.androidtown.moneymanagement.Student;

import java.util.ArrayList;


public class SearchOnlyResultFragment extends Fragment {

    public String mSid;

    private int mSnumber;
    private int mSupportNumber = 0;

    private ArrayList<Student> students;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_only_result, container, false);

        try {
            Bundle bundle = getArguments();
            assert bundle != null;
            students = bundle.getParcelableArrayList("students");
        } catch (Exception e) {
            String message = "Loading Failed.";
            Toast toast = Toast.makeText(getContext(), message, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.BOTTOM, 0, 0);
            toast.show();

            FragmentManager fm = getFragmentManager();
            assert fm != null;
            FragmentTransaction ft = fm.beginTransaction();
            Fragment fragment = fm.findFragmentById(R.id.search_only_result_fragment);

            if(fragment != null && fragment.isVisible()) {
                ft.hide(fragment);
                ft.commit();
            }
        }

        TextView mTitleView = view.findViewById(R.id.search_only_result_title);
        TextView mAllResultView = view.findViewById(R.id.search_only_all_result);
        mAllResultView.setGravity(Gravity.CENTER_HORIZONTAL);

        mSnumber = students.size();
        mSid = students.get(0).sid;
        String mSname = students.get(0).name;

        countStudents();


        String mTargetInfo = mSname + " 전체 검색결과";
        String mResultAll = "총 \"" + mSnumber + "\" 명 검색되었습니다.\n";

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

        mTitleView.setText(mTargetInfo);
        mAllResultView.setText(mResultAll);

        RecyclerView mRecyclerView = view.findViewById(R.id.search_only_result_list);
        mRecyclerView.setHasFixedSize(false);

        RecyclerView.LayoutManager mLayoutManage = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManage);

        RecyclerView.Adapter adapter = new SearchOnlyResultListAdapter(students);
        mRecyclerView.setAdapter(adapter);

        SearchOnlyActivity.mSnameView.getText().clear();

        return view;
    }

    public void countStudents() {
        for(int i = 0 ; i < mSnumber ; i++) {
            if(students.get(i).support.equals("YES")) {
                mSupportNumber++;
            }
        }
    }
}
