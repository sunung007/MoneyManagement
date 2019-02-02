package org.androidtown.moneymanagement;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ModifyStudentModifyCheckPopup extends AppCompatActivity {

    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference conditionRef = mRootRef.child("student");

    StudentInfo beforeStudent;
    StudentInfo changedStudent;

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
            changedStudent = intent.getParcelableExtra("changed_student");
            beforeStudent = intent.getParcelableExtra("before_student");
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
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                mProgressBar.setVisibility(View.VISIBLE);

                ModifyStudent modifyStudent = new ModifyStudent();
                modifyStudent.execute((Void) null);
            }
        });

        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    public class ModifyStudent extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... voids) {
            DBCheckBeforeAction.PositionCheck positionCheck
                    = new DBCheckBeforeAction.PositionCheck(beforeStudent);

            return positionCheck.doInBackground();
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            mProgressBar.setVisibility(View.GONE);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

            String resultMessage;

            if(aBoolean) {
                String index = changedStudent.index;

                conditionRef.child(index).child("Sname").setValue(changedStudent.Sname);
                conditionRef.child(index).child("Sid").setValue(changedStudent.Sid);
                conditionRef.child(index).child("Pamount").setValue(changedStudent.Pamount);
                conditionRef.child(index).child("Pyear").setValue(changedStudent.Pyear);
                conditionRef.child(index).child("Ptype").setValue(changedStudent.Ptype);

                resultMessage = "수정했습니다.";
                setResult(RESULT_OK);
            }
            else {
                resultMessage = "수정에 실패했습니다.";
            }

            Toast toast = Toast.makeText(getApplicationContext(), resultMessage, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
            toast.show();

            finish();
        }
    }

}
