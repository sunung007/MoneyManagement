package org.androidtown.moneymanagement;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;


public class SearchResultFragment extends Fragment {

    private ArrayList<StudentInfo> students;
    private int number;
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


    public static SearchResultFragment newInstance(ArrayList<StudentInfo> src) {
        SearchResultFragment fragment = new SearchResultFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList("students", src);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            students = savedInstanceState.getParcelableArrayList("students");
            number = students.size();
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

        students = ((SearchStudentFragment)getParentFragment()).getTarget();
        number = students.size();
        mSid = students.get(0).Sid;
        mSname = students.get(0).Sname;
        mTargetInfo = mSid + " " + mSname;

        // Later, change this line to "총 ~명 중 ~명의 지원대상이 있습니다"
        mResultAll = "총 " + number + "명 있습니다";

//        countStudents();


        mTargetInfoView = (TextView) view.findViewById(R.id.target_info);
        mResultAllView = (TextView) view.findViewById(R.id.result_all);
        mTargetInfoView.setText(mTargetInfo);
        mResultAllView.setText(mResultAll);


        // Recycler view setting.
        mRecyclerview = view.findViewById(R.id.students_list);
//        mSearchResultListView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(view.getContext());
        mRecyclerview.setLayoutManager(mLayoutManager);

        SearchStudentListAdapter adapter = new SearchStudentListAdapter(students);
        mRecyclerview.setAdapter(adapter);

        return view;
    }

//    private void countStudents() {
//        Calendar calendar = Calendar.getInstance();
////        int currentYear =   calendar.get(Calendar.YEAR);
//        int currentYear = 2017;
//
//        Toast.makeText(getContext(), String.valueOf(currentYear), Toast.LENGTH_SHORT).show();
//
//        for(int i = 0 ; i < number ; i++) {
//            if(students.get(i).Pyear.contains("전액")) {
//                students.get(i).Csupport = "YES";
//                continue;
//            }
//
//            int sYear = Integer.parseInt(students.get(i).Pyear);
//            int sType = Integer.parseInt(students.get(i).Ptype);
//            sYear = sYear + (int) (sType / 2);
//
//            // The condition is about whether a person can support by student's money.
//            // Pyear + Ptype/2 is supposed to be limit year for support.
//            students.get(i).Csupport = (sYear >= currentYear) ? "YES" : "NO";
//        }
//    }

    @Override
    public void onAttachFragment(Fragment childFragment) {
        super.onAttachFragment(childFragment);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }
}
