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
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import org.androidtown.moneymanagement.Common.Array;
import org.androidtown.moneymanagement.Common.Mode;
import org.androidtown.moneymanagement.Common.Student;
import org.androidtown.moneymanagement.R;

import java.util.Objects;

public class ModifyInfoPopup extends AppCompatActivity {

    private Student student;
    private Student changedInfo;

    private EditText mSidView;
    private EditText mSnameView;

    private Spinner mTypeView;
    private Spinner mAmountView;
    private Spinner mYearView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.popup_modify_info);

        student = new Student();

        Intent intent = getIntent();

        try {
            student = intent.getParcelableExtra("student");
        } catch (Exception e) {
            String message = getResources().getString(R.string.caution_db_load_fail);
            Toast.makeText(getApplicationContext(), message,Toast.LENGTH_SHORT).show();
            finish();
        }


        mSidView = findViewById(R.id.modify_sid);
        mSidView.setText(student.sid);

        mSnameView = findViewById(R.id.modify_name);
        mSnameView.setText(student.name);

        mTypeView = findViewById(R.id.modify_type);
        mAmountView = findViewById(R.id.modify_amount);

        Array spinnerArray;
        SpinnerAdapter spinnerAdapter;

        spinnerArray = new Array(Mode.YEAR, getApplicationContext());
        spinnerAdapter = new ArrayAdapter<>(Objects.requireNonNull(getApplicationContext()),
                R.layout.spinner_custom, spinnerArray.getArrayList());
        ((ArrayAdapter) spinnerAdapter).setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);

        mYearView = findViewById(R.id.modify_year);
        mYearView.setAdapter(spinnerAdapter);


        if(student.type.contains("전액") || student.type.contains("학기")) {
            int typeID = 0;
            if (student.type.contains("학기")) {
                typeID = Integer.parseInt(student.type.substring(0, 1), 10);
            }

            mTypeView.setSelection(typeID);
        }

        int amountID = Integer.parseInt(mAmountView.getItemAtPosition(0).toString().substring(0, 1), 10)
                - Integer.parseInt(student.amount.substring(0,1), 10);
        mAmountView.setSelection(amountID);

        int yearID = 0;
        for (int i = 0 ; i < mYearView.getCount() ; i++) {
            if(mYearView.getItemAtPosition(i).toString().equals(student.year)) {
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

                Intent intent = new Intent(getApplicationContext(), ModifyCheckPopup.class);
                intent.putExtra("changed_student", changedInfo);
                intent.putExtra("before_student", student);

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
            InputMethodManager inputMethodManager =
                    (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

            assert inputMethodManager != null;
            if(inputMethodManager.isActive()) {
                inputMethodManager.hideSoftInputFromWindow(Objects
                        .requireNonNull(getCurrentFocus()).getWindowToken(), 0);
            }

            return false;
        }

        return super.dispatchTouchEvent(ev);
    }


    public void saveChangedStudent() {
        changedInfo = new Student();

        changedInfo.index = student.index;

        changedInfo.sid = mSidView.getText().toString();
        changedInfo.name = mSnameView.getText().toString();
        changedInfo.type = mTypeView.getSelectedItem().toString();
        changedInfo.amount = mAmountView.getSelectedItem().toString();
        changedInfo.year = mYearView.getSelectedItem().toString();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 3 && resultCode == RESULT_OK) {
            setResult(RESULT_OK);
            finish();
        }
    }
}
