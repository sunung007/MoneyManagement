package org.androidtown.moneymanagement.Search;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import org.androidtown.moneymanagement.DBHelper;
import org.androidtown.moneymanagement.MainActivity;
import org.androidtown.moneymanagement.Mode;
import org.androidtown.moneymanagement.QuestionPopup;
import org.androidtown.moneymanagement.R;
import org.androidtown.moneymanagement.Student;

import java.util.ArrayList;
import java.util.Objects;


public class SearchFragment extends Fragment {

    private static String mSid;

    private Spinner mSidSpinner;

    @SuppressLint("StaticFieldLeak")
    private static EditText mSnameEdit;
    @SuppressLint("StaticFieldLeak")
    private static ProgressBar mProgressBar;
    @SuppressLint("StaticFieldLeak")
    private static View thisView;
    @SuppressLint("StaticFieldLeak")
    private static Activity thisActivity;


    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        thisView = view;
        thisActivity = getActivity();

        mSidSpinner = view.findViewById(R.id.sid);
        mSnameEdit = view.findViewById(R.id.sname);
        View mSearchView = view.findViewById(R.id.student_search_view);

        mProgressBar = view.findViewById(R.id.search_progressBar);
        mProgressBar.setVisibility(View.GONE);

        Button mStudentSearchButton = view.findViewById(R.id.student_search_button);
        Button mSearchingOnlyButton = view.findViewById(R.id.search_searching_only_button);

        ImageButton mQuestionSearchButton = view.findViewById(R.id.question_search);
        ImageButton mQuestionSearchOnlyTitleButton = view.findViewById(R.id.question_search_only_title);

        mStudentSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchStudent();
            }
        });

        mSearchingOnlyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), SearchOnlyActivity.class);
                startActivity(intent);
            }
        });


        mSnameEdit.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)) {
                    searchStudent();
                }

                return false;
            }
        });

        mSearchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager inputMethodManager =
                        (InputMethodManager) Objects.requireNonNull(getActivity())
                                .getSystemService(Context.INPUT_METHOD_SERVICE);

                assert inputMethodManager != null;
                if (inputMethodManager.isActive()) {
                    inputMethodManager.hideSoftInputFromWindow(
                            Objects.requireNonNull(getActivity().getCurrentFocus()).getWindowToken(), 0);
                }
            }
        });


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

        return view;
    }


    private void searchStudent() {
        boolean cancel = false;

        MainActivity.screenUntouchable();
        mProgressBar.setVisibility(View.VISIBLE);
        mSnameEdit.setError(null);

        mSid = mSidSpinner.getSelectedItem().toString().substring(0, 2);
        String mSname = mSnameEdit.getText().toString().trim();

        if (TextUtils.isEmpty(mSname)) {
            mSnameEdit.setError(getString(R.string.error_field_required));
            cancel = true;
        }

        if (cancel) {
            mProgressBar.setVisibility(View.GONE);
            MainActivity.screenTouchable();
            mSnameEdit.requestFocus();
        } else {
            DBHelper.SearchTask searchTask = new DBHelper.SearchTask(mSname, mSid, Mode.SEARCH_STUDENT_FRAGMENT);
            searchTask.execute((Void) null);
        }
    }

    public static void onPost(ArrayList<Student> _students) {
        ArrayList<Student> students = new ArrayList<>(_students);

        mProgressBar.setVisibility(View.GONE);
        MainActivity.screenTouchable();

       if(students.size() > 0) {
            InputMethodManager imm = (InputMethodManager) Objects.requireNonNull(thisActivity)
                    .getSystemService(Activity.INPUT_METHOD_SERVICE);
            assert imm != null;
            imm.hideSoftInputFromWindow(Objects.requireNonNull(thisView).getWindowToken(), 0);

            mSnameEdit.getText().clear();

            Intent intent = new Intent(thisActivity.getApplicationContext(), SearchResultPopup.class);
            intent.putParcelableArrayListExtra("result", students);

            if(mSid.contains("전체")) {
                intent.putExtra("type", 1);
            }
            else {
                intent.putExtra("type", 0);
            }

            thisActivity.startActivityForResult(intent, 1);
        } else {
            String message = thisView.getResources().getString(R.string.caution_no_student);
            Toast toast = Toast.makeText(thisView.getContext(), message, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.BOTTOM, 0, 0);
            toast.show();
        }
    }
}