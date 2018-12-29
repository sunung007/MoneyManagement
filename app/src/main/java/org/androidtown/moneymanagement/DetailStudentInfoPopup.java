package org.androidtown.moneymanagement;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class DetailStudentInfoPopup extends AppCompatActivity {

    StudentInfo studentInfo;
    int totalNum;
    int mode;

    TextView mTitleView;
    TextView mYearView;
    TextView mTypeView;
    TextView mAmountView;
    TextView mSupportView;

    public Activity mActivity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.activity_detail_student_info_popup);

        mActivity = DetailStudentInfoPopup.this;
        Intent intent = getIntent();

        try {
            studentInfo = intent.getParcelableExtra("student");
            mode = intent.getIntExtra("mode", 0);
            totalNum = intent.getIntExtra("size", 1);
        } catch (Exception e) {
            String message = "Loading failed.";
            Toast.makeText(getApplicationContext(), message,Toast.LENGTH_SHORT).show();
            finish();
        }

        mTitleView = findViewById(R.id.title_detail_info);
        mYearView = findViewById(R.id.detail_year);
        mTypeView = findViewById(R.id.detail_type);
        mAmountView = findViewById(R.id.detail_amount);
        mSupportView = findViewById(R.id.detail_support);

        String title = studentInfo.Sid + " " + studentInfo.Sname + " 상세정보";
        mTitleView.setText(title);
        mYearView.setText(studentInfo.Pyear);
        mTypeView.setText(studentInfo.Ptype);
        mAmountView.setText(studentInfo.Pamount);
        mSupportView.setText(studentInfo.Csupport);

        Button mModifyButton = findViewById(R.id.button_search_result_ok);
        Button mOkButton = findViewById(R.id.button_detail_ok);
        Button mDeleteButton = findViewById(R.id.button_detail_delete);

        mModifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = "아직 준비되지 않았습니다.";
                Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.BOTTOM, 0, 0);
                toast.show();
            }
        });
        mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), DetailStudentDeleteCheckPopup.class);
                intent.putExtra("student", studentInfo);
                intent.putExtra("size", totalNum);
                intent.putExtra("mode", mode);
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
        if(requestCode == 1 && resultCode == RESULT_OK) {
            finish();

            // mode 0 is from ManageStudentListAdapter.
            // mode 1 is from EnrollFragment.
            if(mode == 0) {
                ManageStudentFragment.refreshFragment();
            } else {
                EnrollPopup enrollPopup = (EnrollPopup) EnrollPopup.thisActivity;
                enrollPopup.finish();
            }
        }
    }

    public Context getContext() {
        return this.getApplicationContext();
    }
}
