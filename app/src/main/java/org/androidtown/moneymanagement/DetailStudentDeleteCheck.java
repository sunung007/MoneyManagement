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
//                mAuthTask.execute((Void) null);
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
            try {
                String curIter, nextIter;
                StudentInfo curStu = new StudentInfo();
                StudentInfo nextStu = new StudentInfo();

                valueEventListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Iterator<DataSnapshot> child = dataSnapshot.getChildren().iterator();
                        DataSnapshot ds;
                        String tName, tId, tAmount, tType, tYear, cSupport;

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                };
                for(int i = index ; i < totalIndex ; i++) {
                    curIter = String.valueOf(i);
                    nextIter = String.valueOf(i + 1);
                    nextStu.Pamount = conditionRef.child(nextIter).child("Pamount").getKey();
                    nextStu.Ptype = conditionRef.child(nextIter).child("Ptype").getKey();
                    nextStu.Pyear = conditionRef.child(nextIter).child("Pyear").getKey();
                    nextStu.Sid = conditionRef.child(nextIter).child("Sid").getKey();
                    nextStu.Sname = conditionRef.child(nextIter).child("Sname").getKey();
//                    conditionRef.child(curIter).child("Pamount")
//                            .setValue(conditionRef.child(nextIter).child("Pamount"));
//                    conditionRef.child(curIter).child("Ptype")
//                            .setValue(conditionRef.child(nextIter).child("Ptype"));
//                    conditionRef.child(curIter).child("Pyear")
//                            .setValue(conditionRef.child(nextIter).child("Pyear"));
//                    conditionRef.child(curIter).child("Sid")
//                            .setValue(conditionRef.child(nextIter).child("Sid"));
//                    conditionRef.child(curIter).child("Sname")
//                            .setValue(conditionRef.child(nextIter).child("Sname"));
//                    conditionRef.child(curIter).setValue(
//                            conditionRef.child(nextIter));
                }

                tmp = nextStu.Sname;

//                conditionRef.child(endIndex).removeValue();
            } catch (Exception e) {
                return false;
            }

            try {
                Thread.sleep(2000);
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
                Toast.makeText(getApplicationContext(), tmp, Toast.LENGTH_SHORT).show();
//                Toast.makeText(getApplicationContext(), "삭제되었습니다", Toast.LENGTH_SHORT).show();
                previousActivity.finish();



//                finish();

//                Fragment fragment = new ManageStudentFragment();
//                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//                ft.replace(R.id.main_fragment, fragment);
//                ft.commit();
//                ft.detach(fragment).attach(fragment).commit();

            } else {
                Toast.makeText(getApplicationContext(), "Delete failed", Toast.LENGTH_SHORT).show();
            }

            finish();

        }

        @Override
        protected void onCancelled() {
            mProgressBar.setVisibility(View.GONE);
            mAuthTask = null;
        }
    }
}
