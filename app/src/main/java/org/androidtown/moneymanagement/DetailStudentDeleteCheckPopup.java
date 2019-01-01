package org.androidtown.moneymanagement;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Iterator;

public class DetailStudentDeleteCheckPopup extends AppCompatActivity {

    DeleteStudent mAuthTask;
    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference conditionRef = mRootRef.child("student");
    ValueEventListener valueEventListener;

    int position, totalNum;
    StudentInfo studentInfo;

    private ProgressBar mProgressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.activity_detail_student_delete_check);


        try {
            Intent intent = getIntent();
            studentInfo = intent.getParcelableExtra("student");
            totalNum = intent.getIntExtra("size", 1);

            position = Integer.parseInt(studentInfo.index);
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
                mProgressBar.setVisibility(View.VISIBLE);

                mAuthTask = new DeleteStudent(position, totalNum);
                mAuthTask.execute((Void) null);
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

    public class DeleteStudent extends AsyncTask<Void, Void, Boolean> {
        String curIndex, endIndex;
        int index, totalIndex;

        DeleteStudent (int _position, int _totalNum) {
            index = _position;
            totalIndex = _totalNum - 1;
            curIndex = String.valueOf(_position);
            endIndex = String.valueOf(_totalNum - 1);

        }
        @Override
        protected Boolean doInBackground(Void... voids) {

            DBCheckBeforeAction.PositionCheck positionCheck
                    = new DBCheckBeforeAction.PositionCheck(studentInfo);

            if(!positionCheck.doInBackground())
                return false;

            try {
                valueEventListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Iterator<DataSnapshot> child = dataSnapshot.getChildren().iterator();
                        DataSnapshot ds;
                        DatabaseReference curRef;

                        int i;
                        for (i = 0; i <= index; i++) {
                            child.next();
                        }

                        i = index;
                        while (child.hasNext()) {
                            ds = child.next();

                            curRef = conditionRef.child(String.valueOf(i));

                            curRef.child("Sname").setValue(ds.child("Sname").getValue().toString());
                            curRef.child("Sid").setValue(ds.child("Sid").getValue().toString());
                            curRef.child("Pamount").setValue(ds.child("Pamount").getValue().toString());
                            curRef.child("Ptype").setValue(ds.child("Ptype").getValue().toString());
                            curRef.child("Pyear").setValue(ds.child("Pyear").getValue().toString());

                            i++;
                        }

                        curRef = conditionRef.child(String.valueOf(totalIndex));
                        curRef.removeValue();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                };

                conditionRef.addListenerForSingleValueEvent(valueEventListener);

            } catch (Exception e) {
                conditionRef.removeEventListener(valueEventListener);
                return false;
            }

            try {
                Thread.sleep(500);
            } catch (Exception e) {
                return false;
            } finally {
                conditionRef.removeEventListener(valueEventListener);
            }

            return true;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            mProgressBar.setVisibility(View.GONE);
            mAuthTask = null;
            String resultMessage;

            if (success) {
                resultMessage = "삭제되었습니다.";
            } else {
                resultMessage = "삭제에 실패했습니다.";
            }

            Toast toast = Toast.makeText(getApplicationContext(), resultMessage, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
            toast.show();

            setResult(RESULT_OK);
            finish();

        }

        @Override
        protected void onCancelled() {
            mProgressBar.setVisibility(View.GONE);
            mAuthTask = null;
        }
    }
}
