package org.androidtown.moneymanagement;

import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Iterator;

public class DBCheckBeforeAction {

    private static DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    private static DatabaseReference conditionRef = mRootRef.child("student");
    private static ValueEventListener valueEventListener;


    public static class PositionCheck extends AsyncTask<Void, Void, Boolean> {

        StudentInfo studentInfo;
        int index;

        public PositionCheck(StudentInfo src) {
            studentInfo = src;
            index = Integer.parseInt(src.index);
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            final StudentInfo dbStudent = new StudentInfo();

            try {
                valueEventListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Iterator<DataSnapshot> child = dataSnapshot.getChildren().iterator();
                        DataSnapshot ds;

                        for (int i = 1; i <= index && child.hasNext(); i++)
                            child.next();

                        if (child.hasNext()) {
                            ds = child.next();

                            dbStudent.Sid = ds.child("Sid").getValue().toString();
                            dbStudent.Sname = ds.child("Sname").getValue().toString();
                            dbStudent.Pamount = ds.child("Pamount").getValue().toString();
                            dbStudent.Ptype = ds.child("Ptype").getValue().toString();
                            dbStudent.Pyear = ds.child("Pyear").getValue().toString();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        while(dbStudent.Sname != null) {
                            dbStudent.Sid = dbStudent.Sname = null;
                        }
                    }
                };

                conditionRef.addListenerForSingleValueEvent(valueEventListener);

            } catch (Exception e) {
                conditionRef.removeEventListener(valueEventListener);
                return false;
            }

            try {
                long startTime = System.currentTimeMillis();
                long progressTime;

                while(dbStudent.Sname == null) {
                    progressTime = System.currentTimeMillis();
                    if(progressTime - startTime > 2500) {
                        return false;
                    }
                }
            } catch (Exception e) {
                return false;
            } finally {
                conditionRef.removeEventListener(valueEventListener);
            }

            return dbStudent.Sid.equals(studentInfo.Sid)
                    && dbStudent.Sname.equals(studentInfo.Sname)
                    && dbStudent.Pamount.equals(studentInfo.Pamount)
                    && dbStudent.Ptype.equals(studentInfo.Ptype)
                    && dbStudent.Pyear.equals(studentInfo.Pyear);
        }
    }

    public static class EntireNumberCheck extends AsyncTask<Void, Void, Boolean> {

        int beforeEntireNumber;
        int afterEntireNumber;

        public EntireNumberCheck(int src) {
            beforeEntireNumber = src;
            afterEntireNumber = 0;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {

            try {
                valueEventListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        afterEntireNumber = (int) dataSnapshot.getChildrenCount();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        afterEntireNumber = 0;
                    }
                };

                conditionRef.addListenerForSingleValueEvent(valueEventListener);

            } catch (Exception e) {
                conditionRef.removeEventListener(valueEventListener);
                return false;
            }

            try {
                long startTime = System.currentTimeMillis();
                long progressTime;

                while (afterEntireNumber <= 0) {
                    Thread.sleep(500);
                    progressTime = System.currentTimeMillis();
                    if (progressTime - startTime > 2500) {
                        return false;
                    }
                }
            } catch (Exception e) {
                return false;
            } finally {
                conditionRef.removeEventListener(valueEventListener);
            }

            return beforeEntireNumber == afterEntireNumber;
        }
    }


}
