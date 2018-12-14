package org.androidtown.moneymanagement;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class DetailStudentInfoPopup extends AppCompatActivity {

    private StudentInfo studentInfo;
    private int position;
    private int totalNum;

    private TextView mTitleView;
    private TextView mYearView;
    private TextView mTypeView;
    private TextView mAmountView;
    private TextView mSupportView;

    public static Activity mActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_detail_student_info_popup);

        mActivity = DetailStudentInfoPopup.this;
        Intent intent = getIntent();

        try {
            studentInfo = intent.getParcelableExtra("student");
            position = intent.getIntExtra("position", -1);
            totalNum = intent.getIntExtra("size", -1);

            if(position < 0 || totalNum < 1) {
                throw new Exception();
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(),
                    "Loading failed", Toast.LENGTH_SHORT).show();

            finish();
        }

        mTitleView = findViewById(R.id.title_detail_info);
        mYearView = findViewById(R.id.detail_year);
        mTypeView = findViewById(R.id.detail_type);
        mAmountView = findViewById(R.id.detail_amount);
        mSupportView = findViewById(R.id.detail_support);

        mTitleView.setText(studentInfo.Sid + " " + studentInfo.Sname + " 상세정보");
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
                Toast.makeText(getApplicationContext(),
                        "아직 준비되지 않았습니다.", Toast.LENGTH_SHORT).show();
            }
        });
        mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), DetailStudentDeleteCheck.class);
                intent.putExtra("student", studentInfo);
                intent.putExtra("position", position);
                intent.putExtra("size", totalNum);
                startActivity(intent);
            }
        });
        mOkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    public Context getContext() {
        return this.getApplicationContext();
    }
}
