package org.androidtown.moneymanagement;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class ModifyStudentInfo extends AppCompatActivity {

    EditText mSidView;
    EditText mSnameView;

    Spinner mTypeView, mAmountView, mYearView;

    StudentInfo studentInfo = new StudentInfo();
    StudentInfo changedInfo;
    int totalNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.activity_modify_student_info);

        Intent intent = getIntent();

        try {
            studentInfo = intent.getParcelableExtra("student");
            totalNum = intent.getIntExtra("size", 1);
        } catch (Exception e) {
            String message = "Loading failed.";
            Toast.makeText(getApplicationContext(), message,Toast.LENGTH_SHORT).show();
            finish();
        }


        mSidView = findViewById(R.id.modify_sid);
        mSnameView = findViewById(R.id.modify_name);

        mTypeView = findViewById(R.id.modify_type);
        mAmountView = findViewById(R.id.modify_amount);
        mYearView = findViewById(R.id.modify_year);

        mSidView.setText(studentInfo.Sid);
        mSnameView.setText(studentInfo.Sname);

        if(studentInfo.Ptype.contains("전액") || studentInfo.Ptype.contains("학기")) {
            int typeID = 0;
            if (studentInfo.Ptype.contains("학기")) {
                typeID = Integer.parseInt(studentInfo.Ptype.substring(0, 1), 10);
            }

            mTypeView.setSelection(typeID);
        }

        int amountID = Integer.parseInt(mAmountView.getItemAtPosition(0).toString().
                substring(0, 1), 10)
                - Integer.parseInt(studentInfo.Pamount.substring(0,1), 10);
        mAmountView.setSelection(amountID);

        int yearID = 0;
        int i;
        for (i = 0 ; i < mYearView.getCount() ; i++) {
            if(mYearView.getItemAtPosition(i).toString().equals(studentInfo.Pyear)) {
                yearID = i;
                break;
            }
        }
        mYearView.setSelection(yearID);


        Button mModifyButton = findViewById(R.id.button_modify_modify);
        Button mCancelButton = findViewById(R.id.button_modify_cancel);

        mModifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveChangedStudent();

                Intent intent = new Intent(getApplicationContext(), ModifyStudentModifyCheckPopup.class);
                intent.putExtra("changed_student", changedInfo);
                intent.putExtra("before_student", studentInfo);

                startActivityForResult(intent, 3);
            }
        });

        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        Rect dialogBounds = new Rect();
        getWindow().getDecorView().getHitRect(dialogBounds);

        if (!dialogBounds.contains((int) ev.getX(), (int) ev.getY())) {
            // Close soft key.
            InputMethodManager inputMethodManager =
                    (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

            if(inputMethodManager.isActive()) {
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }

            return false;
        }

        return super.dispatchTouchEvent(ev);
    }


    public void saveChangedStudent() {
        changedInfo = new StudentInfo();

        changedInfo.index = studentInfo.index;

        changedInfo.Sid = mSidView.getText().toString();
        changedInfo.Sname = mSnameView.getText().toString();
        changedInfo.Ptype = mTypeView.getSelectedItem().toString();
        changedInfo.Pamount = mAmountView.getSelectedItem().toString();
        changedInfo.Pyear = mYearView.getSelectedItem().toString();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 3 && resultCode == RESULT_OK) {
            setResult(RESULT_OK);
            finish();
        }
    }
}
