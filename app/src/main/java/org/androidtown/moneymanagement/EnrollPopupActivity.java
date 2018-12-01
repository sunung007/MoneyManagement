package org.androidtown.moneymanagement;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

public class EnrollPopupActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManage;
    private RecyclerView.Adapter adapter;

    private ArrayList<StudentInfo> students;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enroll_popup);

        mRecyclerView = findViewById(R.id.already_enrolleds_students);
        mRecyclerView.setHasFixedSize(false);
        mLayoutManage = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManage);

        adapter = new ManageStudentListAdapter(students);
        mRecyclerView.setAdapter(adapter);

    }


}
