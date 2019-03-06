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

import org.androidtown.moneymanagement.Common.Mode;
import org.androidtown.moneymanagement.Common.Student;
import org.androidtown.moneymanagement.Enroll.EnrollCheckPopup;
import org.androidtown.moneymanagement.R;

public class DetailInfoPopup extends AppCompatActivity {

    private int mode;

    private Student student;
    public Activity mActivity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.popup_detail_info);

        mActivity = DetailInfoPopup.this;

        Intent intent = getIntent();
        student = intent.getParcelableExtra("student");
        mode = intent.getIntExtra("mode", 0);


        TextView mTitleView = findViewById(R.id.text_detail_info_title);
        TextView mYearView = findViewById(R.id.text_detail_info_year);
        TextView mTypeView = findViewById(R.id.text_detail_info_type);
        TextView mAmountView = findViewById(R.id.text_detail_info_amount);
        TextView mSupportView = findViewById(R.id.text_detail_info_support);

        String title = student.sid + " " + student.name + " 상세정보";
        mTitleView.setText(title);
        mYearView.setText(student.year);
        mTypeView.setText(student.type);
        mAmountView.setText(student.amount);
        mSupportView.setText(student.support);


        Button mModifyButton = findViewById(R.id.button_detail_info_modify);
        mModifyButton.setOnClickListener(mModifyButtonOnClickListener);

        Button mDeleteButton = findViewById(R.id.button_detail_info_delete);
        mDeleteButton.setOnClickListener(mDeleteButtonOnClickListener);

        Button mOkButton = findViewById(R.id.button_detail_info_ok);
        mOkButton.setOnClickListener(mOkButtonOnClickListener);
    }


    //TODO: modify
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK) {
            finish();

            // mode 0 is from ManageListAdapter.
            // mode 1 is from EnrollFragment.
            if(mode == 0) {
                ManageFragment.refreshFragment();
            } else if(mode == 1){
                EnrollCheckPopup enrollPopup = (EnrollCheckPopup) EnrollCheckPopup.thisActivity;
                enrollPopup.finish();
            }
        }
    }

    public Context getContext() {
        return this.getApplicationContext();
    }

    private View.OnClickListener mModifyButtonOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(getApplicationContext(), ModifyInfoPopup.class);
            intent.putExtra("student", student);
            startActivityForResult(intent, Mode.DETAIL_INFO_POPUP.ordinal());
        }
    };

    private View.OnClickListener mDeleteButtonOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(getApplicationContext(), DeleteCheckPopup.class);
            intent.putExtra("student", student);
            startActivityForResult(intent, Mode.DETAIL_INFO_POPUP.ordinal());
        }
    };

    private View.OnClickListener mOkButtonOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            finish();
        }
    };
}
