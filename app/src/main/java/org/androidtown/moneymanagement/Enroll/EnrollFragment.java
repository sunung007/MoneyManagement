package org.androidtown.moneymanagement.Enroll;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
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
import org.androidtown.moneymanagement.Common.Special;
import org.androidtown.moneymanagement.Common.Student;
import org.androidtown.moneymanagement.QuestionPopup;
import org.androidtown.moneymanagement.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;


public class EnrollFragment extends Fragment {

    public static int size;

    private static String mSid;
    private static String mName;
    private static String mYear;
    private static String mType;
    private static String mAmount;

    private static ArrayList<Student> students;

    private Spinner mSidSpinner;
    private Spinner mYearSpinner;
    private Spinner mTypeSpinner;
    private Spinner mAmountSpinner;

    private EditText mNameText;
    @SuppressLint("StaticFieldLeak")
    private static ProgressBar mProgressBar;
    @SuppressLint("StaticFieldLeak")
    private static Activity thisActivity;
    private static Window thisWindow;


    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_enroll, container, false);

        thisActivity = getActivity();
        students = new ArrayList<>();
        thisWindow = Objects.requireNonNull(getActivity()).getWindow();

        Array spinnerArray = new Array(Mode.SID_ENROLL, getContext());
        SpinnerAdapter spinnerAdapter =  new ArrayAdapter<>(
                        Objects.requireNonNull(getContext()),
                        R.layout.spinner_custom,
                        spinnerArray.getArrayList());
        ((ArrayAdapter) spinnerAdapter).setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);

        mSidSpinner = view.findViewById(R.id.spinner_enroll_sid);
        mSidSpinner.setAdapter(spinnerAdapter);


        spinnerArray = new Array(Mode.YEAR, getContext());
        spinnerAdapter = new ArrayAdapter<>(
                Objects.requireNonNull(getContext()),
                R.layout.spinner_custom,
                spinnerArray.getArrayList());
        ((ArrayAdapter) spinnerAdapter).setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);

        mYearSpinner = view.findViewById(R.id.spinner_enroll_year);
        mYearSpinner.setAdapter(spinnerAdapter);


        mTypeSpinner = view.findViewById(R.id.spinner_enroll_type);
        mAmountSpinner = view.findViewById(R.id.spinner_enroll_amount);

        mProgressBar = view.findViewById(R.id.progressBar_enroll);
        mProgressBar.setVisibility(View.GONE);


        Button studentSearchButton = view.findViewById(R.id.button_enroll);
        studentSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchStudent();
            }
        });

        Button mNewbieEnrollButton = view.findViewById(R.id.button_enroll_newbie);
        mNewbieEnrollButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: 설정
                Intent intent = new Intent(getContext(), EnrollNewbieActivity.class);
                startActivity(intent);
            }
        });


        // TODO: code edit
        mNameText = view.findViewById(R.id.edit_enroll_name);
        mNameText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN)
                        && (i == KeyEvent.KEYCODE_ENTER)) {
                    searchStudent();
                }
                return false;
            }
        });


        View mEnrollView = view.findViewById(R.id.constraint_enroll);
        mEnrollView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                Special.closeKeyboard(thisActivity);
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

        ImageButton mQuestionNewbieEnrollButton = view.findViewById(R.id.question_enroll_newbie);
        mQuestionNewbieEnrollButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), QuestionPopup.class);

                String title = getString(R.string.title_enroll_newbie);
                String content = getString(R.string.attention_enroll_newbie);
                intent.putExtra("title", title);
                intent.putExtra("content", content);

                startActivity(intent);
            }
        });

        return view;
    }


    private void searchStudent() {
        Special.startLoad(thisWindow, mProgressBar);

        mNameText.setError(null);

        mName = mNameText.getText().toString().trim();
        mSid = mSidSpinner.getSelectedItem().toString().substring(0, 2);
        mType = mTypeSpinner.getSelectedItem().toString().trim();
        mAmount = mAmountSpinner.getSelectedItem().toString().trim();
        mYear = mYearSpinner.getSelectedItem().toString().trim();

        if (TextUtils.isEmpty(mName)) {
            Special.finishLoad(thisWindow, mProgressBar);
            mNameText.setError(getString(R.string.error_field_required));
            mNameText.requestFocus();
        } else {
            DBHelper.SearchTask searchTask = new DBHelper.SearchTask(mName, mSid, Mode.ENROLL_FRAGMENT);
            searchTask.execute((Void) null);
        }
    }

    private static Student setNewStudent() {
        Student student = new Student(mAmount, mType, mYear, mSid, mName);

        // TODO: 필요 없을텐데?
        String cSupport;
        double currentYear, tmpAll;
        int tmpYear, tmpType;
        currentYear = Calendar.getInstance().get(Calendar.YEAR)
                + (Calendar.getInstance().get(Calendar.MONTH) + 1.0)/12;

        if (mType.contains("전액")) {
            cSupport = "YES";
        } else if (mType.contains("미납")) {
            cSupport = "NO";
        } else if (mType.contains("학기")) {
            try {
                tmpYear = Integer.parseInt(mYear.substring(0, 2), 10) + 2000;
                tmpType = Integer.parseInt(mAmount.substring(0, 1), 10);

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
        Special.finishLoad(thisWindow, mProgressBar);

        students = new ArrayList<>(_students);
        Student student = new Student(setNewStudent());


        Intent intent = new Intent(thisActivity.getApplicationContext(), EnrollCheckPopup.class);

        String title;
        if (students.size() > 0) {
            title = thisActivity.getResources().getString(R.string.caution_enroll_already);

            intent.putParcelableArrayListExtra("already_students", students);
            intent.putExtra("new_student", student);
            intent.putExtra("total_num", _totalNum);
            intent.putExtra("title", title);

            thisActivity.startActivity(intent);
        } else {
            title = thisActivity.getResources().getString(R.string.title_enroll_check);

            intent.putParcelableArrayListExtra("already_students", students);
            intent.putExtra("new_student", student);
            intent.putExtra("total_num", _totalNum);
            intent.putExtra("title", title);

            thisActivity.startActivity(intent);
        }
    }
}
