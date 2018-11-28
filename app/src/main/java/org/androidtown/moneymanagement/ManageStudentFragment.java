package org.androidtown.moneymanagement;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;


public class ManageStudentFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private LoadAllStudents mAuthTask;

    private DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference conditionRef = mRootRef.child("student");
    private ValueEventListener valueEventListener;

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManage;
    private RecyclerView.Adapter adapter;
    private ProgressBar mProgressBar;
    private SearchView mSearchView;

    private ArrayList<StudentInfo> students;
    private int sNumber = 0;

    public ManageStudentFragment() {
        // Required empty public constructor
    }

    public void setStudents(ArrayList<StudentInfo> _students) {
        students = new ArrayList<>(_students);
        sNumber = _students.size();
    }

    public static ManageStudentFragment newInstance(String param1, String param2) {
        ManageStudentFragment fragment = new ManageStudentFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_manage_student, container, false);

        mProgressBar = (ProgressBar) view.findViewById(R.id.manage_progressBar);
        mProgressBar.setVisibility(View.GONE);
        mSearchView = (SearchView) view.findViewById(R.id.manage_searchBar);
        // Write codes about search view

        loadStudentsList();

        return view;
    }

    public void loadStudentsList() {
        mProgressBar.setVisibility(View.VISIBLE);

        mAuthTask = new LoadAllStudents();
        mAuthTask.execute((Void) null);
    }

    public class LoadAllStudents extends AsyncTask<Void, Void, Boolean> {
        private ArrayList<StudentInfo> studentInfos = new ArrayList<>();

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    // Initialize array.
                    studentInfos.clear();

                    // Start loading process.
                    Iterator<DataSnapshot> child = dataSnapshot.getChildren().iterator();
                    DataSnapshot ds;
                    String tName, tId, tAmount, tType, tYear, cSupport;

                    int i = 0;
                    int currentYear, tmpAll;
                    int tmpYear, tmpType;
                    currentYear = Calendar.getInstance().get(Calendar.YEAR)
                            + Calendar.getInstance().get(Calendar.MONTH)/6;

                    while(child.hasNext()) {
                        ds = child.next();
                        tName = ds.child("Sname").getValue().toString();
                        tId = ds.child("Sid").getValue().toString();

                        tAmount = ds.child("Pamount").getValue().toString();
                        tType = ds.child("Ptype").getValue().toString();
                        tYear = ds.child("Pyear").getValue().toString();

                        if(tType.contains("전액")) {
                            cSupport = "YES";
                        } else if(tType.contains("미납")) {
                            cSupport = "NO";
                        } else if(tType.contains("학기")) {
                            // The condition is about whether a person can support by student's money.
                            try {
                                tmpYear = Integer.parseInt(tYear.substring(0, 2), 10) + 2000;
                                tmpType = Integer.parseInt(tAmount.substring(0, 1), 10);

                                tmpAll = tmpYear + tmpType/2;

                                cSupport = (tmpAll >= currentYear) ? "YES" : "NO";
                            } catch (Exception e) {
                                cSupport = "UNKNOWN";
                            }
                        } else {
                            cSupport = "UNKNOWN";
                        }

                        studentInfos.add(i, new StudentInfo(tAmount, tType, tYear, tId, tName, cSupport));
                        i++;
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    studentInfos.clear();
                }

            };

            conditionRef.addListenerForSingleValueEvent(valueEventListener);

            try {
                // Simulate network access.
                while(true) {
                    if(!studentInfos.isEmpty()) {
                        return true;
                    }
                }
            } catch (Exception e) {
                return false;
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            mProgressBar.setVisibility(View.GONE);

            if (success) {
                setStudents(studentInfos);
                mRecyclerView = getView().findViewById(R.id.all_students_list);
                mRecyclerView.setHasFixedSize(false);
                mLayoutManage = new LinearLayoutManager(getContext());
                mRecyclerView.setLayoutManager(mLayoutManage);

                adapter = new ManageStudentListAdapter(studentInfos);
                mRecyclerView.setAdapter(adapter);

            } else {
                // If the result array list is empty, which means
                // the student that user put in is not in DB, just float Toast.
                Toast toast = Toast.makeText(getContext(), "데이터베이스에 이상이 있습니다.", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                toast.show();
            }

            conditionRef.removeEventListener(valueEventListener);
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            mProgressBar.setVisibility(View.GONE);
        }
    }

}
