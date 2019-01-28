package org.androidtown.moneymanagement;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Locale;


public class ManageStudentFragment extends Fragment {

    LoadAllStudents mAuthTask;

    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference conditionRef = mRootRef.child("student");
    ValueEventListener valueEventListener;

    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManage;
    RecyclerView.Adapter adapter;

    static FragmentManager thisFragmentManager;
    static Fragment thisFragment;

    ProgressBar mProgressBar;
    EditText mSearchView;
    SwipeRefreshLayout mSwipeRefreshLayout;

    ArrayList<StudentInfo> students;
    int sNumber = 0;

    static boolean isLoading;

    public ManageStudentFragment() {
        students = new ArrayList<>();
    }

    public void setStudents(ArrayList<StudentInfo> _students) {
        while(!students.isEmpty())
            students.clear();

        students.addAll(_students);
        sNumber = _students.size();
    }

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_manage_student, container, false);

        mProgressBar = view.findViewById(R.id.manage_progressBar);
        mProgressBar.setVisibility(View.GONE);

        thisFragmentManager = getFragmentManager();
        thisFragment = this;
        isLoading = false;

        mSearchView = view.findViewById(R.id.manage_searchBar);

        mSearchView.setCursorVisible(false);
        mSearchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSearchView.setCursorVisible(true);
            }
        });

        mSearchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {

                String query = mSearchView.getText().toString().toLowerCase(Locale.getDefault());

                ((ManageStudentListAdapter) adapter).filter(query);
            }
        });


        mSwipeRefreshLayout = view.findViewById(R.id.manage_swipe_refresh);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSearchView.setText("");

                InputMethodManager inputMethodManager =
                        (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

                if (inputMethodManager.isActive()) {
                    inputMethodManager.hideSoftInputFromWindow(
                            getActivity().getCurrentFocus().getWindowToken(), 0);
                }

                mSearchView.setCursorVisible(false);

                loadStudentsList();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

        loadStudentsList();

        return view;
    }

    public static void refreshFragment () {
        FragmentManager fm = thisFragmentManager;
        FragmentTransaction ft = fm.beginTransaction();
        Fragment fragment = new ManageStudentFragment();
        ft.replace(R.id.main_fragment, fragment);
        ft.commit();
    }

    public void loadStudentsList() {
        mProgressBar.setVisibility(View.VISIBLE);
        isLoading = true;

        mAuthTask = new LoadAllStudents();
        mAuthTask.execute((Void) null);
    }

    public class LoadAllStudents extends AsyncTask<Void, Void, Boolean> {

        ArrayList<StudentInfo> studentInfos;
        int totalNum;

        private LoadAllStudents() {
            studentInfos = new ArrayList<>();
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            // Initialize array.
            totalNum = 0;
            while(!studentInfos.isEmpty())
                studentInfos.clear();

            valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    // Start loading process.
                    Iterator<DataSnapshot> child = dataSnapshot.getChildren().iterator();
                    DataSnapshot ds;

                    String index;
                    String tName, tId, tAmount, tType, tYear, cSupport;

                    double currentYear, tmpAll;
                    int tmpYear, tmpType;
                    currentYear = Calendar.getInstance().get(Calendar.YEAR)
                            + (Calendar.getInstance().get(Calendar.MONTH) + 1.0)/12;

                    totalNum = (int) dataSnapshot.getChildrenCount();

                    while(child.hasNext()) {
                        ds = child.next();

                        index = ds.getKey();
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

                                tmpAll = tmpYear + tmpType/2.0;

                                cSupport = (tmpAll >= currentYear) ? "YES" : "NO";
                            } catch (Exception e) {
                                cSupport = "UNKNOWN";
                            }
                        } else {
                            cSupport = "UNKNOWN";
                        }

                        studentInfos.add(new StudentInfo(index, tAmount, tType, tYear, tId, tName, cSupport));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    while(!studentInfos.isEmpty())
                        studentInfos.clear();
                }
            };

            conditionRef.addListenerForSingleValueEvent(valueEventListener);

            try {
                long startTime = System.currentTimeMillis();
                long progressTime;

                while (studentInfos.isEmpty())
                    Thread.sleep(500);

                while (studentInfos.size() != totalNum) {
                    Thread.sleep(500);

                    progressTime = System.currentTimeMillis();
                    if (progressTime - startTime > 2500) {
                        return false;
                    }
                }

                return true;
            } catch (Exception e) {
                return false;
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            mProgressBar.setVisibility(View.GONE);

            if (success) {
                Collections.sort(studentInfos, new SortStudents());
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
                String message = "데이터베이스에 이상이 있습니다.";
                Toast toast = Toast.makeText(getContext(), message, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                toast.show();
            }

            conditionRef.removeEventListener(valueEventListener);
            isLoading = false;

        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            mProgressBar.setVisibility(View.GONE);
        }
    }


    public class SortStudents implements Comparator<StudentInfo> {

        @Override
        public int compare(StudentInfo s1, StudentInfo s2) {
            int ret = 0;

            if (Integer.parseInt(s1.Sid) > Integer.parseInt(s2.Sid)) {
                ret = -1;
            } else if (Integer.parseInt(s1.Sid) == Integer.parseInt(s2.Sid)) {
                ret = s1.Sname.compareTo(s2.Sname);

                if (ret < 0) ret = -1;
                else if (ret > 0) ret = 1;

            } else if (Integer.parseInt(s1.Sid) < Integer.parseInt(s2.Sid)) {
                ret = 1;
            }

            return ret;
        }
    }

}