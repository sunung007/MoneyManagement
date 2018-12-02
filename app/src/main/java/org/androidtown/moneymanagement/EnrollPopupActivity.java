package org.androidtown.moneymanagement;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Window;
import android.widget.Toast;

import java.util.ArrayList;

public class EnrollPopupActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManage;
    private RecyclerView.Adapter adapter;

    private ArrayList<StudentInfo> students;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_enroll_popup);

        String tmp;
        Intent intent = getIntent();

        try {
            students = intent.getParcelableArrayListExtra("students");
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Loading Failed", Toast.LENGTH_SHORT).show();
            finish();
        }


        mRecyclerView = findViewById(R.id.already_enrolleds_students);
        mRecyclerView.setHasFixedSize(false);


        mLayoutManage = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManage);
        adapter = new EnrollAlreadyStudentsListAdapter(students);
        mRecyclerView.setAdapter(adapter);

    }


}
