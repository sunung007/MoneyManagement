package org.androidtown.moneymanagement;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
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

public class DetailStudentDeleteCheck extends AppCompatActivity {

    private DeleteStudent mAuthTask;
    private DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference conditionRef = mRootRef.child("student");
    private ValueEventListener valueEventListener;

    private int position;
    private int totalNum;
    private StudentInfo studentInfo;

    public DetailStudentInfoPopup previousActivity;

    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_detail_student_delete_check);

        previousActivity = (DetailStudentInfoPopup) DetailStudentInfoPopup.mActivity;

        try {
            Intent intent = getIntent();
            studentInfo = intent.getParcelableExtra("student");
            position = intent.getIntExtra("position", -1);
            totalNum = intent.getIntExtra("size", -1);

            if(position < 0 || totalNum < 1) {
                throw new Exception();
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(),
                    "Delete failed", Toast.LENGTH_SHORT).show();
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

    public class DeleteStudent extends AsyncTask<Void, Void, Boolean> {
        private String curIndex;
        private String endIndex;
        private String tmp;
        private int index, totalIndex;

        public DeleteStudent (int _position, int _totalNum) {
            index = _position;
            totalIndex = _totalNum - 1;
            curIndex = String.valueOf(_position);
            endIndex = String.valueOf(_totalNum - 1);

        }
        @Override
        protected Boolean doInBackground(Void... voids) {

            valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Iterator<DataSnapshot> child = dataSnapshot.getChildren().iterator();
                    DataSnapshot ds;
                    DatabaseReference curRef;

                    int i;
                    for(i = 0 ; i <= index ; i++) {
                        ds = child.next();
                    }

                    i = index;
                    while(child.hasNext()) {
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
                public void onCancelled(@NonNull DatabaseError databaseError) { }
            };

            conditionRef.addListenerForSingleValueEvent(valueEventListener);


            try {
                Thread.sleep(500);
            } catch (Exception e) {
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            mProgressBar.setVisibility(View.GONE);
            mAuthTask = null;

            if (success) {
                Toast.makeText(getApplicationContext(), "삭제되었습니다", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Delete failed", Toast.LENGTH_SHORT).show();
            }

            conditionRef.removeEventListener(valueEventListener);

            previousActivity.finish();
            finish();
        }

        @Override
        protected void onCancelled() {
            mProgressBar.setVisibility(View.GONE);
            mAuthTask = null;
        }
    }
}
