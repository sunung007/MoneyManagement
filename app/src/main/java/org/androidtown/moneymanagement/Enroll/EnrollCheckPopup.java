package org.androidtown.moneymanagement.Enroll;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.androidtown.moneymanagement.Common.DBHelper;
import org.androidtown.moneymanagement.Common.Mode;
import org.androidtown.moneymanagement.R;
import org.androidtown.moneymanagement.Common.Student;

import java.util.ArrayList;

public class EnrollCheckPopup extends AppCompatActivity {

    private int totalNum;
    private String title;
    private Student newStudent;
    private ArrayList<Student> alreadyStudents;

    @SuppressLint("StaticFieldLeak")
    private static ProgressBar mProgressBar;
    @SuppressLint("StaticFieldLeak")
    public static Activity thisActivity;
    private static Window thisWindow;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.popup_enroll);

        mProgressBar = findViewById(R.id.enroll_popup_progressBar);
        mProgressBar.setVisibility(View.GONE);

        ConstraintLayout mActivity = findViewById(R.id.enroll_popup_entire);

        thisActivity = EnrollCheckPopup.this;
        thisWindow = getWindow();

        Intent intent = getIntent();
        try {
            alreadyStudents = intent.getParcelableArrayListExtra("already_students");
            newStudent = intent.getParcelableExtra("new_student");
            totalNum = intent.getIntExtra("total_num", -1);
            title = intent.getStringExtra("title");
        } catch (Exception e) {
            String message = getResources().getString(R.string.caution_db_load_fail);
            Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
            toast.show();
            finish();
        }

        if(title.contains(getResources().getString(R.string.title_enroll_check))) {
            mActivity.setBackground(getDrawable(R.drawable.round_button));
            mActivity.setPadding(0, 0, 0, 0);
        }

        TextView mTitle = findViewById(R.id.title_detail_info);
        mTitle.setText(title);

        RecyclerView mRecyclerView = findViewById(R.id.already_enrolleds_students);
        mRecyclerView.setHasFixedSize(false);

        RecyclerView.LayoutManager mLayoutManage = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManage);
        RecyclerView.Adapter adapter = new EnrollAlreadyListAdapter(alreadyStudents);
        mRecyclerView.setAdapter(adapter);

        Button buttonEnroll = findViewById(R.id.button_search_result_ok);
        Button buttonCancel = findViewById(R.id.button_detail_ok);

        buttonEnroll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                mProgressBar.setVisibility(View.VISIBLE);

                DBHelper.EnrollTask enrollTask
                        = new DBHelper.EnrollTask(totalNum, newStudent, Mode.ENROLL_CHECK_POPUP);
                enrollTask.execute((Void) null);
            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }


    public static void onPost(boolean result) {
        mProgressBar.setVisibility(View.GONE);
        thisWindow.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        String message = thisActivity.getResources()
                .getString(result ? R.string.caution_enroll_success : R.string.caution_enroll_fail);
        Toast toast = Toast.makeText(thisActivity.getApplicationContext(), message, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.show();

        thisActivity.finish();
    }

}
