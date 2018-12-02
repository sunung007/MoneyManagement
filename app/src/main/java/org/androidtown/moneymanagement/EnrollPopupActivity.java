package org.androidtown.moneymanagement;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class EnrollPopupActivity extends AppCompatActivity {

    private TextView mTitle;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManage;
    private RecyclerView.Adapter adapter;

    private ArrayList<StudentInfo> alreadyStudents;
    private StudentInfo newStudent;
    private int totalNum;
    private String title;

    private DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference conditionRef = mRootRef.child("student");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_enroll_popup);

        // Receive the data from enroll fragment.
        Intent intent = getIntent();
        try {
            alreadyStudents = intent.getParcelableArrayListExtra("already_students");
            newStudent = intent.getParcelableExtra("new_student");
            totalNum = intent.getIntExtra("total_num", -1);
            title = intent.getStringExtra("title");
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Loading Failed", Toast.LENGTH_SHORT).show();
            finish();
        }

        mTitle = findViewById(R.id.title_detail_info);
        mTitle.setText(title);

        mRecyclerView = findViewById(R.id.already_enrolleds_students);
        mRecyclerView.setHasFixedSize(false);


        mLayoutManage = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManage);
        adapter = new EnrollAlreadyStudentsListAdapter(alreadyStudents);
        mRecyclerView.setAdapter(adapter);

        Button buttonEnroll = findViewById(R.id.button_detail_modify);
        Button buttonCancle = findViewById(R.id.button_detail_ok);

        buttonEnroll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(totalNum < 0) return;
                String totalNumIndex = String.valueOf(totalNum);
                conditionRef.child(totalNumIndex).setValue(totalNumIndex);

                conditionRef.child(totalNumIndex).child("Sname").setValue(newStudent.Sname);
                conditionRef.child(totalNumIndex).child("Sid").setValue(newStudent.Sid);
                conditionRef.child(totalNumIndex).child("Pamount").setValue(newStudent.Pamount);
                conditionRef.child(totalNumIndex).child("Pyear").setValue(newStudent.Pyear);
                conditionRef.child(totalNumIndex).child("Ptype").setValue(newStudent.Ptype);

                Toast toast = Toast.makeText(getApplicationContext(),"등록되었습니다.", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                toast.show();

                finish();
            }
        });

        buttonCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }


}
