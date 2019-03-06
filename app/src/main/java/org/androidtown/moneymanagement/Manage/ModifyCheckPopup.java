package org.androidtown.moneymanagement.Manage;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.androidtown.moneymanagement.Common.DBHelper;
import org.androidtown.moneymanagement.Common.Special;
import org.androidtown.moneymanagement.Common.Student;
import org.androidtown.moneymanagement.R;

public class ModifyCheckPopup extends AppCompatActivity {

    private Student beforeStudent;
    private static Student changedStudent;

    @SuppressLint("StaticFieldLeak")
    private static ProgressBar mProgressBar;
    @SuppressLint("StaticFieldLeak")
    private static Context thisContext;
    @SuppressLint("StaticFieldLeak")
    private static Activity thisActivity;

    private static DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    private static DatabaseReference conditionRef = mRootRef.child("student");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.popup_modify_check);

        thisContext = getApplicationContext();
        thisActivity = this;

        mProgressBar = findViewById(R.id.progressBar_modify_check);
        mProgressBar.setVisibility(View.GONE);
        mProgressBar.bringToFront();

        Intent intent = getIntent();
        changedStudent = intent.getParcelableExtra("changed_student");
        beforeStudent = intent.getParcelableExtra("before_student");


        Button mModifyButton = findViewById(R.id.button_modify_check_modify);
        Button mCancelButton = findViewById(R.id.button_modify_check_cancel);

        mModifyButton.setOnClickListener(mModifyButtonOnClickListener);
        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    private View.OnClickListener mModifyButtonOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Special.startLoad(getWindow(), mProgressBar);

            DBHelper.PositionCheck positionCheck = new DBHelper.PositionCheck(beforeStudent);
            positionCheck.execute((Void) null);
        }
    };

    public static void onPost(boolean result) {
        String message;

        if (result) {
            String index = changedStudent.index;

            conditionRef.child(index).child("Sname").setValue(changedStudent.name);
            conditionRef.child(index).child("Sid").setValue(changedStudent.sid);
            conditionRef.child(index).child("Pamount").setValue(changedStudent.amount);
            conditionRef.child(index).child("Pyear").setValue(changedStudent.year);
            conditionRef.child(index).child("Ptype").setValue(changedStudent.type);

            message = thisContext.getResources().getString(R.string.caution_modify_success);
            thisActivity.setResult(RESULT_OK);
        }
        else message = thisContext.getResources().getString(R.string.caution_modify_fail);

        Special.printMessage(thisActivity.getApplicationContext(), message);
        Special.finishLoad(thisActivity.getWindow(), mProgressBar);

        thisActivity.finish();
    }

}
