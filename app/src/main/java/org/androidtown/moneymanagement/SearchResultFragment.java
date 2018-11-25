package org.androidtown.moneymanagement;

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
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final ArrayList<StudentInfo> STUDENTS = null;
    private static final int NUMBER = 0;

    private ArrayList<StudentInfo> students;
    private int number;
    private String mSid;
    private String mSname;
    private String mTargetInfo;
    private String mResultAll;

    private TextView mTargetInfoView;
    private TextView mResultAllView;

    private RecyclerView mSearchResultList;
    private RecyclerView.LayoutManager mLayoutManager;

//    private OnFragmentInteractionListener mListener;

    public SearchResultFragment() {
        // Required empty public constructor
    }

//    // TODO: Rename and change types and number of parameters
//    public static SearchResultFragment newInstance(SearchStudentFragment.org.androidtown.moneymanagement.StudentInfo _studentInfo) {
//        SearchResultFragment fragment = new SearchResultFragment();
//        Bundle args = new Bundle();
////        args.putString(ARG_PARAM1, param1);
////        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
//        return fragment;
//    }

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

        if (getArguments() != null) {
            students = savedInstanceState.getParcelableArrayList("students");
            number = students.size();
            mSid = students.get(0).Sid;
            mSname = students.get(0).Sname;
            mTargetInfo = mSid + " " + mSname;
        }

        // Later, change this line to "학생회비 지원대상이 ~명있습니다"
        mResultAll = "검색결과 " + number + "명 있습니다";

        mTargetInfoView = (TextView) view.findViewById(R.id.target_info);
        mResultAllView = (TextView) view.findViewById(R.id.result_all);
        mTargetInfoView.setText(mTargetInfo);
        mResultAllView.setText(mResultAll);

        mSearchResultList = view.findViewById(R.id.students_list);
        mSearchResultList.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(view.getContext());
        mSearchResultList.setLayoutManager(mLayoutManager);

        return view;
    }

//    // TODO: Rename method, update argument and hook method into UI event
//    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
//    }
//
//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
//    }
//
//    @Override
//    public void onDetach() {
//        super.onDetach();
//        mListener = null;
//    }
//
//    /**
//     * This interface must be implemented by activities that contain this
//     * fragment to allow an interaction in this fragment to be communicated
//     * to the activity and potentially other fragments contained in that
//     * activity.
//     * <p>
//     * See the Android Training lesson <a href=
//     * "http://developer.android.com/training/basics/fragments/communicating.html"
//     * >Communicating with Other Fragments</a> for more information.
//     */
//    public interface OnFragmentInteractionListener {
//        // TODO: Update argument type and name
//        void onFragmentInteraction(Uri uri);
//    }
}
