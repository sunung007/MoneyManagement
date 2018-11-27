package org.androidtown.moneymanagement;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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

    private DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference conditionRef = mRootRef.child("student");
    private ArrayList<StudentInfo> students = new ArrayList<>();

    public ManageStudentFragment() {
        // Required empty public constructor
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

//        loadStudentsList();

        return view;
    }

    public void loadStudentsList() {
        conditionRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                // Load all children process.
                Iterator<DataSnapshot> child = dataSnapshot.getChildren().iterator();
                DataSnapshot ds;
                String tName, tId, tAmount, tType, tYear, cSupport;
                int i = 0;
                int currentYear, tmpAll;
                int tmpYear, tmpType;
                currentYear = Calendar.getInstance().get(Calendar.YEAR)
                        + Calendar.getInstance().get(Calendar.MONTH)/6;

                students.clear();

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

                    students.add(i, new StudentInfo(tAmount, tType, tYear, tId, tName, cSupport));
                    i++;
                }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

}
