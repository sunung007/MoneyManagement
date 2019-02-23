package org.androidtown.moneymanagement.Enroll;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import org.androidtown.moneymanagement.Common.Array;
import org.androidtown.moneymanagement.Common.DBHelper;
import org.androidtown.moneymanagement.Common.Mode;
import org.androidtown.moneymanagement.Common.Student;
import org.androidtown.moneymanagement.MainActivity;
import org.androidtown.moneymanagement.QuestionPopup;
import org.androidtown.moneymanagement.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;


public class EnrollFragment extends Fragment {

    private static String mSid;
    private static String mSname;
    private static String mPyear;
    private static String mPtype;
    private static String mPamount;

    public static int size;

    private static ArrayList<Student> students;

    private Spinner mSidView;
    private Spinner mPyearView;
    private Spinner mPtypeView;
    private Spinner mPamountView;

    private EditText mSnameView;
    @SuppressLint("StaticFieldLeak")
    private static ProgressBar mProgressBar;
    @SuppressLint("StaticFieldLeak")
    private static Activity thisActivity;


    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_enroll, container, false);

        thisActivity = getActivity();
        students = new ArrayList<>();

        Array spinnerArray;
        SpinnerAdapter spinnerAdapter;

        spinnerArray = new Array(Mode.SID_ENROLL, getContext());
        spinnerAdapter = new ArrayAdapter<>(Objects.requireNonNull(getContext()),
                R.layout.spinner_custom, spinnerArray.getArrayList());
        ((ArrayAdapter) spinnerAdapter).setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);

        mSidView = view.findViewById(R.id.enroll_sid);
        mSidView.setAdapter(spinnerAdapter);

        spinnerArray = new Array(Mode.YEAR, getContext());
        spinnerAdapter = new ArrayAdapter<>(Objects.requireNonNull(getContext()),
                R.layout.spinner_custom, spinnerArray.getArrayList());
        ((ArrayAdapter) spinnerAdapter).setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);

        mPyearView = view.findViewById(R.id.enroll_pyear);
        mPyearView.setAdapter(spinnerAdapter);

        mPtypeView = view.findViewById(R.id.enroll_ptype);
        mPamountView = view.findViewById(R.id.enroll_pamount);
        mProgressBar = view.findViewById(R.id.enroll_progressBar);
        mProgressBar.setVisibility(View.GONE);


        Button studentSearchButton = view.findViewById(R.id.enroll_button);
        studentSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchStudent();
            }
        });


        mSnameView = view.findViewById(R.id.enroll_name);
        mSnameView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)) {
                    searchStudent();
                }
                return false;
            }
        });


        View mEnrollView = view.findViewById(R.id.student_enroll_view);
        mEnrollView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                InputMethodManager inputMethodManager
                        = (InputMethodManager) Objects.requireNonNull(getActivity())
                        .getSystemService(Context.INPUT_METHOD_SERVICE);

                assert inputMethodManager != null;
                if(inputMethodManager.isActive()) {
                    inputMethodManager.hideSoftInputFromWindow(
                            Objects.requireNonNull(getActivity().getCurrentFocus()).getWindowToken(), 0);
                }
                return false;
            }
        });


        ImageButton mQuestionEnrollButton = view.findViewById(R.id.question_enroll);
        mQuestionEnrollButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), QuestionPopup.class);

                String title = getString(R.string.title_caution);
                String content = getString(R.string.attention_enroll);
                intent.putExtra("title", title);
                intent.putExtra("content", content);

                startActivity(intent);
            }
        });

        return view;
    }


    private void searchStudent() {
        MainActivity.screenUntouchable();
        mProgressBar.setVisibility(View.VISIBLE);

        mSnameView.setError(null);
        mSid = mSidView.getSelectedItem().toString().substring(0, 2);
        mSname = mSnameView.getText().toString().trim();
        mPtype = mPtypeView.getSelectedItem().toString().trim();
        mPamount = mPamountView.getSelectedItem().toString().trim();
        mPyear = mPyearView.getSelectedItem().toString().trim();

        boolean cancel = false;

        if (TextUtils.isEmpty(mSname)) {
            mSnameView.setError(getString(R.string.error_field_required));
            cancel = true;
        }

        if (cancel) {
            mProgressBar.setVisibility(View.GONE);
            MainActivity.screenTouchable();

            mSnameView.requestFocus();
        } else {
            DBHelper.SearchTask searchTask = new DBHelper.SearchTask(mSname, mSid, Mode.ENROLL_FRAGMENT);
            searchTask.execute((Void) null);
        }
    }

    static Student setNewStudent() {
        Student student = new Student(mPamount, mPtype, mPyear, mSid, mSname);

        String cSupport;
        double currentYear, tmpAll;
        int tmpYear, tmpType;
        currentYear = Calendar.getInstance().get(Calendar.YEAR)
                + (Calendar.getInstance().get(Calendar.MONTH) + 1.0)/12;

        if (mPtype.contains("전액")) {
            cSupport = "YES";
        } else if (mPtype.contains("미납")) {
            cSupport = "NO";
        } else if (mPtype.contains("학기")) {
            // The condition is about whether a person can support by student's money.
            try {
                tmpYear = Integer.parseInt(mPyear.substring(0, 2), 10) + 2000;
                tmpType = Integer.parseInt(mPamount.substring(0, 1), 10);

                tmpAll = tmpYear + tmpType/2.0;

                cSupport = (tmpAll >= currentYear) ? "YES" : "NO";
            } catch (Exception e) {
                cSupport = "UNKNOWN";
            }
        } else {
            cSupport = "UNKNOWN";
        }
        student.support = cSupport;

        return student;
    }

    public static void onPost(int _totalNum, ArrayList<Student> _students) {
        students = new ArrayList<>(_students);
        Student student = new Student(setNewStudent());

        mProgressBar.setVisibility(View.GONE);
        MainActivity.screenTouchable();

        Intent intent = new Intent(thisActivity.getApplicationContext(), EnrollCheckPopup.class);

        String title;
        if (students.size() > 0) {
            title = thisActivity.getResources().getString(R.string.caution_enroll_already);

            intent.putParcelableArrayListExtra("already_students", students);
            intent.putExtra("new_student", student);
            intent.putExtra("total_num", _totalNum);
            intent.putExtra("title", title);

            thisActivity.startActivityForResult(intent, 1);
        } else {
            title = thisActivity.getResources().getString(R.string.title_enroll_check);

            intent.putParcelableArrayListExtra("already_students", students);
            intent.putExtra("new_student", student);
            intent.putExtra("total_num", _totalNum);
            intent.putExtra("title", title);

            thisActivity.startActivityForResult(intent, 1);
        }
    }
}
