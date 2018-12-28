package org.androidtown.moneymanagement;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
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

    private LoadAllStudents mAuthTask;

    private DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference conditionRef = mRootRef.child("student");
    private ValueEventListener valueEventListener;

    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManage;
    RecyclerView.Adapter adapter;
    ProgressBar mProgressBar;
    SearchView mSearchView;
    SwipeRefreshLayout mSwipeRefreshLayout;

    private ArrayList<StudentInfo> students;
    private int sNumber = 0;

    public ManageStudentFragment() {
        // Required empty public constructor
    }

    public void setStudents(ArrayList<StudentInfo> _students) {
        students = new ArrayList<>(_students);
        sNumber = _students.size();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_manage_student, container, false);

        mProgressBar = view.findViewById(R.id.manage_progressBar);
        mProgressBar.setVisibility(View.GONE);

        mSearchView = view.findViewById(R.id.manage_searchBar);

        // Test
        mSearchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "Clicked", Toast.LENGTH_SHORT).show();
            }
        });

        mSwipeRefreshLayout = view.findViewById(R.id.manage_swipe_refresh);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadStudentsList();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

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

            // Initialize array.
            studentInfos.clear();

            valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

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

                        studentInfos.add(Integer.parseInt(ds.getKey()),
                                new StudentInfo(tAmount, tType, tYear, tId, tName, cSupport));
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
                mRecyclerView.setClickable(true);

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
