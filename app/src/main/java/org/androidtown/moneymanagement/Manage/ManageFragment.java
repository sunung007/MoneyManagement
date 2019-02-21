package org.androidtown.moneymanagement.Manage;


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.androidtown.moneymanagement.DBHelper;
import org.androidtown.moneymanagement.MainActivity;
import org.androidtown.moneymanagement.Mode;
import org.androidtown.moneymanagement.R;
import org.androidtown.moneymanagement.Student;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;
import java.util.Objects;


public class ManageFragment extends Fragment {

    public static int sNumber;
    public static boolean isLoading;
    private static ArrayList<Student> students;

    @SuppressLint("StaticFieldLeak")
    private static RecyclerView mRecyclerView;
    private static RecyclerView.Adapter adapter;

    private static FragmentManager thisFragmentManager;
    public static Fragment thisFragment;

    private EditText mSearchView;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    @SuppressLint("StaticFieldLeak")
    private static ProgressBar mProgressBar;
    @SuppressLint("StaticFieldLeak")
    private static View thisView;
    @SuppressLint("StaticFieldLeak")
    private static Context thisContext;


    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manage, container, false);

        thisView = view;
        thisContext = getContext();

        sNumber = 0;

        mProgressBar = view.findViewById(R.id.manage_progressBar);
        mProgressBar.setVisibility(View.GONE);

        thisFragmentManager = getFragmentManager();
        thisFragment = this;
        isLoading = false;

        mSearchView = view.findViewById(R.id.manage_searchBar);

        mRecyclerView = view.findViewById(R.id.all_students_list);
        mRecyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager mLayoutManage = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManage);
        mRecyclerView.setClickable(true);

        mSearchView.setCursorVisible(false);
        mSearchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSearchView.setCursorVisible(true);
            }
        });

        mSearchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {

                String query = mSearchView.getText().toString().toLowerCase(Locale.getDefault());

                ((ManageListAdapter) adapter).filter(query);
            }
        });


        mSwipeRefreshLayout = view.findViewById(R.id.manage_swipe_refresh);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSearchView.setText("");

                InputMethodManager inputMethodManager = (InputMethodManager) Objects
                        .requireNonNull(getActivity()).getSystemService(Context.INPUT_METHOD_SERVICE);

                assert inputMethodManager != null;
                if (inputMethodManager.isActive()) {
                    inputMethodManager.hideSoftInputFromWindow(Objects.requireNonNull(
                            getActivity().getCurrentFocus()).getWindowToken(), 0);
                }

                mSearchView.setCursorVisible(false);

                loadStudentsList();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

        loadStudentsList();

        return view;
    }

    public static void refreshFragment () {
        FragmentManager fm = thisFragmentManager;
        FragmentTransaction ft = fm.beginTransaction();
        Fragment fragment = new ManageFragment();
        ft.replace(R.id.main_fragment, fragment);
        ft.commit();
    }

    public void loadStudentsList() {
        MainActivity.screenUntouchable();
        mProgressBar.setVisibility(View.VISIBLE);
        isLoading = true;

        DBHelper.SearchTask searchTask = new DBHelper.SearchTask(true, Mode.MANAGE_FRAGMENT);
        searchTask.execute((Void) null);
    }


    public static void onPost(ArrayList<Student> _students, boolean result) {
        mProgressBar.setVisibility(View.GONE);
        MainActivity.screenTouchable();
        isLoading = false;

        if (result) {
            students = new ArrayList<>(_students);

            Collections.sort(students, new Student.SortStudents());

            adapter = new ManageListAdapter(students);
            mRecyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();

        } else {
            String message = thisView.getResources().getString(R.string.caution_db_load_fail);
            Toast toast = Toast.makeText(thisContext, message, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
            toast.show();
        }
    }
}