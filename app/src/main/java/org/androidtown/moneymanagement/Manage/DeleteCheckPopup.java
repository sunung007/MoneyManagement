package org.androidtown.moneymanagement.Manage;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;

import org.androidtown.moneymanagement.Common.DBHelper;
import org.androidtown.moneymanagement.Common.Mode;
import org.androidtown.moneymanagement.Common.Special;
import org.androidtown.moneymanagement.Common.Student;
import org.androidtown.moneymanagement.R;

public class DeleteCheckPopup extends AppCompatActivity {

    private int position;
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
        setContentView(R.layout.popup_delete_check);

        thisActivity = this;

        Intent intent = getIntent();
        student = intent.getParcelableExtra("student");
        position = Integer.parseInt(student.index);


        mProgressBar = findViewById(R.id.progressBar_detail_delete);
        mProgressBar.setVisibility(View.GONE);
        mProgressBar.bringToFront();


        Button mDeleteButton = findViewById(R.id.button_delete_delete);
        mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Special.startLoad(getWindow(), mProgressBar);

                DBHelper.DeleteTask deleteTask =
                        new DBHelper.DeleteTask(student, position, Mode.DELETE_CHECK_POPUP);
                deleteTask.execute((Void) null);
            }
        });

        Button mCancelButton = findViewById(R.id.button_delete_cancel);
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

        if (!dialogBounds.contains((int) ev.getX(), (int) ev.getY()))
            return false;

        return super.dispatchTouchEvent(ev);
    }


    public static void onPost(Boolean result) {
        String message = thisActivity.getResources()
                .getString(result ? R.string.caution_delete_success : R.string.caution_delete_fail);

        Special.finishLoad(thisActivity.getWindow(), mProgressBar);
        Special.printMessage(thisActivity.getApplicationContext(), message);

        thisActivity.setResult(RESULT_OK);
        thisActivity.finish();
    }

}
