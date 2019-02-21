package org.androidtown.moneymanagement.Manage;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.androidtown.moneymanagement.DBHelper;
import org.androidtown.moneymanagement.Mode;
import org.androidtown.moneymanagement.R;
import org.androidtown.moneymanagement.Student;

public class DeleteCheckPopup extends AppCompatActivity {

    private int position;
    private int totalNum;
    private Student student;

    @SuppressLint("StaticFieldLeak")
    private static ProgressBar mProgressBar;
    @SuppressLint("StaticFieldLeak")
    private static Activity thisActivity;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.activity_detail_delete_check);

        thisActivity = this;

        try {
            Intent intent = getIntent();
            student = intent.getParcelableExtra("student");
            totalNum = intent.getIntExtra("size", 1);

            position = Integer.parseInt(student.index);
        } catch (Exception e) {
            String message = "Delete failed.";
            Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.BOTTOM, 0, 0);
            toast.show();

            finish();
        }

        Button mDeleteButton = findViewById(R.id.button_detail_delete_delete);
        Button mCancelButton = findViewById(R.id.button_detail_delete_cancel);

        mProgressBar = findViewById(R.id.delete_check_progressBar);
        mProgressBar.bringToFront();
        mProgressBar.setVisibility(View.GONE);

        mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                mProgressBar.setVisibility(View.VISIBLE);

                DBHelper.DeleteTask deleteTask
                        = new DBHelper.DeleteTask(student, position, totalNum, Mode.DELETE_CHECK_POPUP);
                deleteTask.execute((Void) null);
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
            return false;
        }

        return super.dispatchTouchEvent(ev);
    }


    public static void onPost(Boolean result) {
        mProgressBar.setVisibility(View.GONE);
        thisActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        String message = thisActivity.getResources()
                .getString(result ? R.string.caution_delete_success : R.string.caution_delete_fail);
        Toast toast = Toast.makeText(thisActivity.getApplicationContext(), message, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.show();

        thisActivity.setResult(RESULT_OK);
        thisActivity.finish();
    }

}
