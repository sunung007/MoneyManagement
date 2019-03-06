package org.androidtown.moneymanagement.Manage;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import org.androidtown.moneymanagement.Common.Array;
import org.androidtown.moneymanagement.Common.Mode;
import org.androidtown.moneymanagement.Common.Special;
import org.androidtown.moneymanagement.Common.Student;
import org.androidtown.moneymanagement.R;

import java.util.Objects;

public class ModifyInfoPopup extends AppCompatActivity {

    private Student student;
    private Student changedInfo;

    private EditText mSidView;
    private EditText mNameView;

    private Spinner mTypeSpinner;
    private Spinner mAmountSpinner;
    private Spinner mYearSpinner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.popup_modify_info);


        Intent intent = getIntent();
        student = new Student();
        student = intent.getParcelableExtra("student");


        mSidView = findViewById(R.id.text_modify_info_sid);
        mSidView.setText(student.sid);

        mNameView = findViewById(R.id.text_modify_info_name);
        mNameView.setText(student.name);

        mTypeSpinner = findViewById(R.id.spinner_modify_info_type);
        if(student.type.contains("전액")) mTypeSpinner.setSelection(0);
        else if(student.type.contains("학기")) {
            int typeID = Integer.parseInt(student.type.substring(0, 1), 10);
            mTypeSpinner.setSelection(typeID);
        }


        Array spinnerArray = new Array(Mode.YEAR, getApplicationContext());
        SpinnerAdapter spinnerAdapter = new ArrayAdapter<>(
                Objects.requireNonNull(getApplicationContext()),
                R.layout.spinner_custom,
                spinnerArray.getArrayList());
        ((ArrayAdapter) spinnerAdapter).setDropDownViewResource(
                R.layout.support_simple_spinner_dropdown_item);

        mYearSpinner = findViewById(R.id.spinner_modify_info_year);
        mYearSpinner.setAdapter(spinnerAdapter);
        int yearID = 0;
        for (int i = 0 ; i < mYearSpinner.getCount() ; i++) {
            if(mYearSpinner.getItemAtPosition(i).toString().equals(student.year)) {
                yearID = i;
                break;
            }
        }
        mYearSpinner.setSelection(yearID);


        mAmountSpinner = findViewById(R.id.spinner_modify_info_amount);
        int amountID = Integer.parseInt(mAmountSpinner
                .getItemAtPosition(0)
                .toString()
                .substring(0, 1), 10)
                - Integer.parseInt(student
                .amount
                .substring(0, 1), 10);
        mAmountSpinner.setSelection(amountID);


        Button mModifyButton = findViewById(R.id.button_modify_info_modify);
        mModifyButton.setOnClickListener(mModifyButtonOnClickListener);

        Button mCancelButton = findViewById(R.id.button_modify_info_cancel);
        mCancelButton.setOnClickListener(mCancelButtonOnClickListener);

    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        Rect dialogBounds = new Rect();
        getWindow().getDecorView().getHitRect(dialogBounds);

        if (!dialogBounds.contains((int) ev.getX(), (int) ev.getY())) {
            Special.closeKeyboard(this);

            return false;
        }

        return super.dispatchTouchEvent(ev);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == Mode.MODIFY.ordinal()
                && resultCode == RESULT_OK) {
            setResult(RESULT_OK);
            finish();
        }
    }

    private View.OnClickListener mModifyButtonOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            saveChangedStudent();

            Intent intent = new Intent(getApplicationContext(), ModifyCheckPopup.class);
            intent.putExtra("changed_student", changedInfo);
            intent.putExtra("before_student", student);

            startActivityForResult(intent, Mode.MODIFY.ordinal());
        }
    };

    private View.OnClickListener mCancelButtonOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            finish();
        }
    };


    private void saveChangedStudent() {
        changedInfo = new Student();

        changedInfo.index = student.index;
        changedInfo.sid = mSidView.getText().toString();
        changedInfo.name = mNameView.getText().toString();
        changedInfo.type = mTypeSpinner.getSelectedItem().toString();
        changedInfo.year = mYearSpinner.getSelectedItem().toString();
        changedInfo.amount = mAmountSpinner.getSelectedItem().toString();
    }

}
