package org.androidtown.moneymanagement.Common;

import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.androidtown.moneymanagement.Enroll.EnrollCheckPopup;
import org.androidtown.moneymanagement.Enroll.EnrollFragment;
import org.androidtown.moneymanagement.Manage.DeleteCheckPopup;
import org.androidtown.moneymanagement.Manage.ManageFragment;
import org.androidtown.moneymanagement.Manage.ModifyCheckPopup;
import org.androidtown.moneymanagement.Search.SearchFragment;
import org.androidtown.moneymanagement.Search.SearchOnlyActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Objects;

public class DBHelper {

    private static DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    private static DatabaseReference conditionRef = mRootRef.child("student");
    private static ValueEventListener valueEventListener;


    public static class SearchTask extends AsyncTask<Void, Void, Boolean> {
        private String name;
        private String sid;

        private int totalNum;

        private boolean isAllStudents;  // 전체 학생 로드
        private boolean isAllID;        // 전체 학번 검색
        private ArrayList<Student> students;

        private Mode mode;

        public SearchTask(boolean _isAllStudents, Mode _mode) {
            isAllID = true;
            isAllStudents = _isAllStudents;
            mode = _mode;
            students = new ArrayList<>();
        }

        public SearchTask(String _name, String _sid, Mode _mode) {
            name = _name;
            sid = _sid;
            mode = _mode;
            students = new ArrayList<>();

            isAllStudents = false;
            isAllID = sid.contains("전체");
        }

        @Override
        protected void onPreExecute() {
            while(!students.isEmpty())
                students.clear();
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Iterator<DataSnapshot> child = dataSnapshot.getChildren().iterator();
                    DataSnapshot ds;

                    totalNum = (int) dataSnapshot.getChildrenCount();

                    String index;
                    String tName, tId, tAmount, tType, tYear, cSupport;

                    double curYear, tmpAll;
                    int tmpYear, tmpType;
                    curYear = Calendar.getInstance().get(Calendar.YEAR)
                            + (Calendar.getInstance().get(Calendar.MONTH) + 1.0)/12;

                    while(child.hasNext()) {
                        ds = child.next();

                        index = ds.getKey();
                        tName = Objects.requireNonNull(ds.child("Sname").getValue()).toString().trim();
                        tId = Objects.requireNonNull(ds.child("Sid").getValue()).toString().trim();

                        if(isAllStudents || (name.equals(tName) && (isAllID || sid.equals(tId)))) {
                            tAmount = Objects.requireNonNull(ds.child("Pamount").getValue()).toString();
                            tType = Objects.requireNonNull(ds.child("Ptype").getValue()).toString();
                            tYear = Objects.requireNonNull(ds.child("Pyear").getValue()).toString();

                            assert tType != null;
                            if(tType.contains("전액")) {
                                cSupport = "YES";
                            }
                            else if(tType.contains("미납")) {
                                cSupport = "NO";
                            }
                            else if(tType.contains("학기")) {
                                try {
                                    assert tYear != null;
                                    assert tAmount != null;
                                    tmpYear = Integer.parseInt(tYear.substring(0, 2), 10) + 2000;
                                    tmpType = Integer.parseInt(tAmount.substring(0, 1), 10);

                                    tmpAll = tmpYear + tmpType/2.0;

                                    cSupport = (tmpAll >= curYear) ? "YES" : "NO";
                                } catch (Exception e) {
                                    cSupport = "UNKNOWN";
                                }
                            }
                            else {
                                cSupport = "UNKNOWN";
                            }

                            students.add(new Student(index, tAmount, tType, tYear, tId, tName, cSupport));
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    while(!students.isEmpty())
                        students.clear();
                }

            };

            conditionRef.addListenerForSingleValueEvent(valueEventListener);

            try {
                long startTime = System.currentTimeMillis();
                long progressTime;

                while(students.isEmpty()) {

                    Thread.sleep(500);
                    progressTime = System.currentTimeMillis();
                    if(progressTime - startTime > 2500) {
                        return false;
                    }
                }

                if(isAllStudents) {
                    while (students.size() != totalNum) {
                        Thread.sleep(500);

                        progressTime = System.currentTimeMillis();
                        if (progressTime - startTime > 2500) {
                            return false;
                        }
                    }
                }
            } catch (Exception e) {
                return false;
            }

            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            conditionRef.removeEventListener(valueEventListener);

            if (mode == Mode.SEARCH_STUDENT_FRAGMENT) {
                SearchFragment.onPost(students);
            } else if (mode == Mode.SEARCH_ONLY_ACTIVITY) {
                SearchOnlyActivity.onPost(students, totalNum);
            } else if (mode == Mode.ENROLL_FRAGMENT) {
                EnrollFragment.onPost(totalNum, students);
            } else if (mode == Mode.MANAGE_FRAGMENT) {
                ManageFragment.onPost(students, result);
            }

        }
    }

    public static class EnrollTask extends AsyncTask<Void, Void, Boolean> {
        private int totalNum;
        private Student newStudent;
        private Mode mode;

        public EnrollTask(int _totalNum, Student _newStudent, Mode _mode) {
            totalNum = _totalNum;
            newStudent = new Student(_newStudent);
            mode = _mode;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            if(totalNum < 0)
                return false;

            DBHelper.EntireNumberCheck entireNumberCheck
                    = new DBHelper.EntireNumberCheck(totalNum);

            if(!entireNumberCheck.doInBackground())
                return false;

            String totalNumIndex = String.valueOf(totalNum);
            conditionRef.child(totalNumIndex).setValue(totalNumIndex);

            conditionRef.child(totalNumIndex).child("Sname").setValue(newStudent.name);
            conditionRef.child(totalNumIndex).child("Sid").setValue(newStudent.sid);
            conditionRef.child(totalNumIndex).child("Pamount").setValue(newStudent.amount);
            conditionRef.child(totalNumIndex).child("Pyear").setValue(newStudent.year);
            conditionRef.child(totalNumIndex).child("Ptype").setValue(newStudent.type);

            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if(mode == Mode.ENROLL_CHECK_POPUP) {
                EnrollCheckPopup.onPost(aBoolean);
            }
        }
    }

    public static class DeleteTask extends AsyncTask<Void, Void, Boolean> {
        private int curIndex;
        private int endIndex;

        private Student student;
        private Mode mode;

        public DeleteTask (Student _student, int _position, Mode _mode) {
            student = _student;
            curIndex = _position;
            mode = _mode;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {

            DBHelper.PositionCheck positionCheck = new DBHelper.PositionCheck(student);

            if(!positionCheck.doInBackground())
                return false;

            try {
                valueEventListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Iterator<DataSnapshot> child = dataSnapshot.getChildren().iterator();
                        DataSnapshot ds;
                        DatabaseReference curRef;

                        endIndex = (int) dataSnapshot.getChildrenCount() - 1;

                        int i;
                        for (i = 0; i <= curIndex && child.hasNext(); ++i)
                            child.next();

                        i = curIndex;
                        while (child.hasNext()) {
                            ds = child.next();

                            curRef = conditionRef.child(String.valueOf(i++));

                            curRef.child("Sname").setValue(Objects.requireNonNull(ds.child("Sname").getValue()).toString());
                            curRef.child("Sid").setValue(Objects.requireNonNull(ds.child("Sid").getValue()).toString());
                            curRef.child("Pamount").setValue(Objects.requireNonNull(ds.child("Pamount").getValue()).toString());
                            curRef.child("Ptype").setValue(Objects.requireNonNull(ds.child("Ptype").getValue()).toString());
                            curRef.child("Pyear").setValue(Objects.requireNonNull(ds.child("Pyear").getValue()).toString());

                        }

                        curRef = conditionRef.child(String.valueOf(endIndex));
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
        protected void onPostExecute(Boolean aBoolean) {
            if(mode == Mode.DELETE_CHECK_POPUP) {
                DeleteCheckPopup.onPost(aBoolean);
            }
        }
    }


    public static class PositionCheck extends AsyncTask<Void, Void, Boolean> {

        Student student;
        int index;

        public PositionCheck(Student src) {
            student = src;
            index = Integer.parseInt(src.index);
        }

        @Override
        public Boolean doInBackground(Void... voids) {
            final Student dbStudent = new Student();

            try {
                valueEventListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Iterator<DataSnapshot> child = dataSnapshot.getChildren().iterator();
                        DataSnapshot ds;

                        for (int i = 0; i < index && child.hasNext(); i++)
                            child.next();

                        if (child.hasNext()) {
                            ds = child.next();

                            dbStudent.sid = Objects.requireNonNull(ds.child("Sid").getValue()).toString();
                            dbStudent.name = Objects.requireNonNull(ds.child("Sname").getValue()).toString();
                            dbStudent.amount = Objects.requireNonNull(ds.child("Pamount").getValue()).toString();
                            dbStudent.type = Objects.requireNonNull(ds.child("Ptype").getValue()).toString();
                            dbStudent.year = Objects.requireNonNull(ds.child("Pyear").getValue()).toString();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        while(!dbStudent.name.equals("")) {
                            dbStudent.sid = dbStudent.name = "";
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

                while(dbStudent.name == null) {
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

            if(dbStudent.isNull()) return false;
            return dbStudent.sid.equals(student.sid)
                    && dbStudent.name.equals(student.name)
                    && dbStudent.amount.equals(student.amount)
                    && dbStudent.type.equals(student.type)
                    && dbStudent.year.equals(student.year);
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            ModifyCheckPopup.onPost(aBoolean);
        }
    }

    public static class EntireNumberCheck extends AsyncTask<Void, Void, Boolean> {

        int beforeEntireNumber;
        int afterEntireNumber;

        EntireNumberCheck(int src) {
            beforeEntireNumber = src;
            afterEntireNumber = 0;
        }

        @Override
        public Boolean doInBackground(Void... voids) {

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
