package org.androidtown.moneymanagement;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ModifyStudentModifyCheckPopup extends AppCompatActivity {

    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference conditionRef = mRootRef.child("student");

    StudentInfo studentInfo;

    ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.activity_modify_student_modify_check_popup);

        mProgressBar = findViewById(R.id.modify_check_progressBar);
        mProgressBar.setVisibility(View.GONE);

        try {
            Intent intent = getIntent();
            studentInfo = intent.getParcelableExtra("student");
        } catch (Exception e) {
            String message = "Modify failed.";
            Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.BOTTOM, 0, 0);
            toast.show();

            finish();
        }

        Button mModifyButton = findViewById(R.id.button_detail_modify_modify);
        Button mCancelButton = findViewById(R.id.button_detail_modify_cancel);

        mModifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mProgressBar.setVisibility(View.VISIBLE);
                modifyStudent();
            }
        });

        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    public void modifyStudent() {
        String index = studentInfo.index;

        conditionRef.child(index).child("Sname").setValue(studentInfo.Sname);
        conditionRef.child(index).child("Sid").setValue(studentInfo.Sid);
        conditionRef.child(index).child("Pamount").setValue(studentInfo.Pamount);
        conditionRef.child(index).child("Pyear").setValue(studentInfo.Pyear);
        conditionRef.child(index).child("Ptype").setValue(studentInfo.Ptype);

        setResult(RESULT_OK);
        finish();
    }
}
