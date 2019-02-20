package org.androidtown.moneymanagement.Manage;

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
import android.widget.TextView;
import android.widget.Toast;

import org.androidtown.moneymanagement.Enroll.EnrollCheckPopup;
import org.androidtown.moneymanagement.R;
import org.androidtown.moneymanagement.Student;

public class DetailInfoPopup extends AppCompatActivity {

    private int totalNum;
    private int mode;

    private Student student;
    public Activity mActivity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.activity_detail_student_info_popup);

        mActivity = DetailInfoPopup.this;
        Intent intent = getIntent();

        try {
            student = intent.getParcelableExtra("student");
            mode = intent.getIntExtra("mode", 0);
            totalNum = intent.getIntExtra("size", 1);
        } catch (Exception e) {
            String message = "Loading failed.";
            Toast.makeText(getApplicationContext(), message,Toast.LENGTH_SHORT).show();
            finish();
        }

        TextView mTitleView = findViewById(R.id.title_detail_info);
        TextView mYearView = findViewById(R.id.detail_year);
        TextView mTypeView = findViewById(R.id.detail_type);
        TextView mAmountView = findViewById(R.id.detail_amount);
        TextView mSupportView = findViewById(R.id.detail_support);

        String title = student.sid + " " + student.name + " 상세정보";
        mTitleView.setText(title);
        mYearView.setText(student.year);
        mTypeView.setText(student.type);
        mAmountView.setText(student.amount);
        mSupportView.setText(student.support);

        Button mModifyButton = findViewById(R.id.button_search_result_ok);
        Button mOkButton = findViewById(R.id.button_detail_ok);
        Button mDeleteButton = findViewById(R.id.button_detail_delete);

        mModifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ModifyInfoPopup.class);
                intent.putExtra("student", student);
                intent.putExtra("size", totalNum);
                startActivityForResult(intent, 2);
            }
        });

        mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), DeleteCheckPopup.class);
                intent.putExtra("student", student);
                intent.putExtra("size", totalNum);
                startActivityForResult(intent, 1);
            }
        });

        mOkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK) {
            finish();

            // mode 0 is from ManageListAdapter.
            // mode 1 is from EnrollFragment.
            if(mode == 0) {
                ManageFragment.refreshFragment();
            } else {
                EnrollCheckPopup enrollPopup = (EnrollCheckPopup) EnrollCheckPopup.thisActivity;
                enrollPopup.finish();
            }
        }
    }

    public Context getContext() {
        return this.getApplicationContext();
    }
}
