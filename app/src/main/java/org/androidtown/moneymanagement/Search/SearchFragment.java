package org.androidtown.moneymanagement.Search;

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
import java.util.Objects;


public class SearchFragment extends Fragment {

    private static String mSid;
    private Spinner mSidSpinner;

    @SuppressLint("StaticFieldLeak")
    private static EditText mNameText;
    @SuppressLint("StaticFieldLeak")
    private static ProgressBar mProgressBar;

    @SuppressLint("StaticFieldLeak")
    private static View thisView;
    @SuppressLint("StaticFieldLeak")
    private static Activity thisActivity;
    private static Window thisWindow;


    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        thisView = view;
        thisActivity = getActivity();
        thisWindow = Objects.requireNonNull(getActivity()).getWindow();

        Array spinnerArray;
        SpinnerAdapter spinnerAdapter;

        spinnerArray = new Array(Mode.SID_SEARCH, getContext());
        spinnerAdapter = new ArrayAdapter<>(
                Objects.requireNonNull(getContext()),
                R.layout.spinner_custom,
                spinnerArray.getArrayList());
        ((ArrayAdapter) spinnerAdapter).setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);

        mSidSpinner = view.findViewById(R.id.spinner_search_sid);
        mSidSpinner.setAdapter(spinnerAdapter);

        mProgressBar = view.findViewById(R.id.progressBar_search);
        mProgressBar.setVisibility(View.GONE);


        // TODO: 코드 정리
        mNameText = view.findViewById(R.id.edit_search_name);
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


        Button mStudentSearchButton = view.findViewById(R.id.button_search);
        mStudentSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchStudent();
            }
        });

        Button mSearchingOnlyButton = view.findViewById(R.id.button_search_only);
        mSearchingOnlyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), SearchOnlyActivity.class);
                startActivity(intent);
            }
        });


        ImageButton mQuestionSearchButton = view.findViewById(R.id.question_search);
        mQuestionSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), QuestionPopup.class);

                String title = getString(R.string.title_caution);
                String content = getString(R.string.attention_search);
                intent.putExtra("title", title);
                intent.putExtra("content", content);

                startActivity(intent);
            }
        });

        ImageButton mQuestionSearchOnlyTitleButton = view.findViewById(R.id.question_search_only);
        mQuestionSearchOnlyTitleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), QuestionPopup.class);

                String title = getString(R.string.title_searching_only);
                String content = getString(R.string.attention_search_searching_only);
                intent.putExtra("title", title);
                intent.putExtra("content", content);

                startActivity(intent);
            }
        });


        View mSearchView = view.findViewById(R.id.constraint_search);
        mSearchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Special.closeKeyboard(thisActivity);
            }
        });

        return view;
    }


    private void searchStudent() {
        Special.startLoad(thisWindow, mProgressBar);
        mNameText.setError(null);

        String mName = mNameText.getText().toString().trim();
        mSid = mSidSpinner.getSelectedItem().toString().substring(0, 2);

        if (TextUtils.isEmpty(mName)) {
            Special.finishLoad(thisWindow, mProgressBar);

            mNameText.setError(getString(R.string.error_field_required));
            mNameText.requestFocus();
        } else {
            DBHelper.SearchTask searchTask = new DBHelper.SearchTask(mName, mSid, Mode.SEARCH_STUDENT_FRAGMENT);
            searchTask.execute((Void) null);
        }
    }

    public static void onPost(ArrayList<Student> _students) {
        Special.finishLoad(thisWindow, mProgressBar);

        ArrayList<Student> students = new ArrayList<>(_students);

        if(students.size() > 0) {
            mNameText.getText().clear();

            Special.closeKeyboard(thisActivity);


            Intent intent = new Intent(thisActivity.getApplicationContext(), SearchResultPopup.class);
            intent.putParcelableArrayListExtra("result", students);

            if (mSid.contains("전체")) intent.putExtra("type", 1);
            else intent.putExtra("type", 0);

            thisActivity.startActivityForResult(intent, 1);
        }
        else Special.printMessage(thisActivity.getApplicationContext(), R.string.caution_no_student);
    }
}