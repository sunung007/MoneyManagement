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

import org.androidtown.moneymanagement.Common.Special;
import org.androidtown.moneymanagement.Common.Student;
import org.androidtown.moneymanagement.R;

import java.util.ArrayList;
import java.util.Objects;


public class SearchOnlyResultFragment extends Fragment {

    public String mSid;
    private String mName;
    private String mTargetInfo;
    private String mResultAll;

    private int mNumber;
    private int mSupportNumber;

    private ArrayList<Student> students;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_only_result, container, false);

        Bundle bundle = getArguments();
        assert bundle != null;
        students = bundle.getParcelableArrayList("students");


        if (students == null) {
            Special.printMessage(Objects.requireNonNull(getContext()), R.string.caution_db_load_fail);

            FragmentManager fm = getFragmentManager();
            assert fm != null;
            FragmentTransaction ft = fm.beginTransaction();
            Fragment fragment = fm.findFragmentById(R.id.fragment_search_only_result);

            if (fragment != null && fragment.isVisible()) {
                ft.hide(fragment);
                ft.commit();
            }

            return view;
        }


        mSupportNumber = 0;
        mNumber = students.size();
        mSid = students.get(0).sid;
        mName = students.get(0).name;

        countStudents();
        setResult();

        TextView mTitleView = view.findViewById(R.id.text_search_only_result_title);
        mTitleView.setText(mTargetInfo);

        TextView mAllResultView = view.findViewById(R.id.text_search_only_result_all);
        mAllResultView.setGravity(Gravity.CENTER_HORIZONTAL);
        mAllResultView.setText(mResultAll);

        RecyclerView mRecyclerView = view.findViewById(R.id.recycler_search_only_result);
        RecyclerView.LayoutManager mLayoutManage = new LinearLayoutManager(getContext());
        RecyclerView.Adapter adapter = new SearchOnlyResultListAdapter(students);

        mRecyclerView.setHasFixedSize(false);
        mRecyclerView.setLayoutManager(mLayoutManage);
        mRecyclerView.setAdapter(adapter);

        SearchOnlyActivity.mNameText.getText().clear();

        return view;
    }

    public void countStudents() {
        for (int i = 0; i < mNumber; i++) {
            if (students.get(i).support.equals("YES"))
                mSupportNumber++;
        }
    }


    // TODO: 코드 수정
    private void setResult() {
        mTargetInfo = mName + " 전체 검색결과";
        mResultAll = "총 \"" + mNumber + "\" 명 검색되었습니다.\n";

        if (mNumber == 1) {
            if (mSupportNumber == mNumber) {
                mResultAll += "해당 학우는 지원 대상입니다.";
            } else {
                mResultAll += "해당 학우는 지원 대상이 아닙니다.";
            }
        } else {
            if (mSupportNumber == mNumber) {
                mResultAll += "전원 지원 대상입니다.";
            } else {
                mResultAll += "지원 대상이 아닌 동명이인이 있습니다.\n" +
                        "총 " + mNumber + "명 중 " + mSupportNumber + "명 지원 대상입니다.";
            }
        }
    }
}
