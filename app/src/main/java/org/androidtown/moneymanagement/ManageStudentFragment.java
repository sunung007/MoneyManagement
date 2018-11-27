package org.androidtown.moneymanagement;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

    private DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference conditionRef = mRootRef.child("student");

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManage;

    private ArrayList<StudentInfo> students = new ArrayList<>();
    private int sNumber = 0;

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

        loadStudentsList();

        String tmp = "" + sNumber;
        Toast.makeText(getContext(), tmp, Toast.LENGTH_SHORT).show();

        mRecyclerView = (RecyclerView) view.findViewById(R.id.all_students_list);
        mLayoutManage = new LinearLayoutManager(view.getContext());
        mRecyclerView.setLayoutManager(mLayoutManage);

        ManageStudentListAdapter adapter = new ManageStudentListAdapter(students);
        mRecyclerView.setAdapter(adapter);

        return view;
    }

    public void loadStudentsList() {
        conditionRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Load all children process.
                students.clear();

                Iterator<DataSnapshot> child = dataSnapshot.getChildren().iterator();
                DataSnapshot ds;
                String tName, tId, tAmount, tType, tYear, cSupport;
                int i = 0;
                int currentYear, tmpAll;
                int tmpYear, tmpType;
                currentYear = Calendar.getInstance().get(Calendar.YEAR)
                        + Calendar.getInstance().get(Calendar.MONTH)/6;

                try {
                    ds = child.next();
                    sNumber = Integer.parseInt(ds.child("Sid").getValue().toString());
                } catch (Exception e) {
                    sNumber = -1;
                }

//                while(child.hasNext()) {
//                    ds = child.next();
//
//                    tName = ds.child("Sname").getValue().toString();
//                    tId = ds.child("Sid").getValue().toString();
//                    tAmount = ds.child("Pamount").getValue().toString();
//                    tType = ds.child("Ptype").getValue().toString();
//                    tYear = ds.child("Pyear").getValue().toString();
//
//                    if(tType.contains("전액")) {
//                        cSupport = "YES";
//                    } else if(tType.contains("미납")) {
//                        cSupport = "NO";
//                    } else if(tType.contains("학기")) {
//                        // The condition is about whether a person can support by student's money.
//                        try {
//                            tmpYear = Integer.parseInt(tYear.substring(0, 2), 10) + 2000;
//                            tmpType = Integer.parseInt(tAmount.substring(0, 1), 10);
//
//                            tmpAll = tmpYear + tmpType/2;
//
//                            cSupport = (tmpAll >= currentYear) ? "YES" : "NO";
//                        } catch (Exception e) {
//                            cSupport = "UNKNOWN";
//                        }
//                    } else {
//                        cSupport = "UNKNOWN";
//                    }
//
//                    students.add(i, new StudentInfo(tAmount, tType, tYear, tId, tName, cSupport));
//                    i++;
//                }
//
//                sNumber = students.size();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
