package org.androidtown.moneymanagement.Manage;


import android.annotation.SuppressLint;
import android.app.Activity;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ProgressBar;

import org.androidtown.moneymanagement.Common.DBHelper;
import org.androidtown.moneymanagement.Common.Mode;
import org.androidtown.moneymanagement.Common.Special;
import org.androidtown.moneymanagement.Common.Student;
import org.androidtown.moneymanagement.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;
import java.util.Objects;


public class ManageFragment extends Fragment {

    public static int sNumber;
    public static boolean isLoading;

    @SuppressLint("StaticFieldLeak")
    private static RecyclerView mRecyclerView;
    private static RecyclerView.Adapter adapter;

    private static FragmentManager thisFragmentManager;
    public static Fragment thisFragment;

    private EditText mSearchText;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    @SuppressLint("StaticFieldLeak")
    private static ProgressBar mProgressBar;
    @SuppressLint("StaticFieldLeak")
    private static Context thisContext;
    private Activity thisActivity;
    private static Window thisWindow;


    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manage, container, false);

        thisContext = getContext();
        thisWindow = Objects.requireNonNull(getActivity()).getWindow();
        thisActivity = getActivity();

        mProgressBar = view.findViewById(R.id.progressBar_manage);
        mProgressBar.setVisibility(View.GONE);

        thisFragmentManager = getFragmentManager();
        thisFragment = this;
        isLoading = false;
        sNumber = 0;


        RecyclerView.LayoutManager mLayoutManage = new LinearLayoutManager(getContext());
        mRecyclerView = view.findViewById(R.id.recycler_manage);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLayoutManage);
        mRecyclerView.setClickable(true);


        mSearchText = view.findViewById(R.id.searchBar_manage);
        mSearchText.setCursorVisible(false);
        mSearchText.setOnClickListener(mSearchTextOnClickListener);
        mSearchText.addTextChangedListener(mSearchTextTextChangedListener);

        mSwipeRefreshLayout = view.findViewById(R.id.refresh_manage);
        mSwipeRefreshLayout.setOnRefreshListener(mSwipeRefreshLayoutOnRefreshListener);

        loadStudentsList();

        return view;
    }


    public static void refreshFragment() {
        FragmentManager fm = thisFragmentManager;
        FragmentTransaction ft = fm.beginTransaction();
        Fragment fragment = new ManageFragment();
        ft.replace(R.id.fragment_main, fragment);
        ft.commit();
    }

    public void loadStudentsList() {
        isLoading = Special.startLoad(thisWindow, mProgressBar);

        DBHelper.SearchTask searchTask = new DBHelper.SearchTask(true, Mode.MANAGE_FRAGMENT);
        searchTask.execute((Void) null);
    }

    public static void onPost(ArrayList<Student> _students, boolean result) {
        isLoading = Special.finishLoad(thisWindow, mProgressBar);

        if (result) {
            ArrayList<Student> students = new ArrayList<>(_students);
            Collections.sort(students, new Student.SortStudents());

            adapter = new ManageListAdapter(students);
            mRecyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        } else Special.printMessage(thisContext, R.string.caution_db_load_fail);
    }


    private View.OnClickListener mSearchTextOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mSearchText.setCursorVisible(true);
        }
    };

    private TextWatcher mSearchTextTextChangedListener = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void afterTextChanged(Editable editable) {

            String query = mSearchText.getText().toString().toLowerCase(Locale.getDefault());

            ((ManageListAdapter) adapter).filter(query);
        }
    };

    private SwipeRefreshLayout.OnRefreshListener mSwipeRefreshLayoutOnRefreshListener =
            new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    mSearchText.setText("");
                    mSearchText.setCursorVisible(false);

                    Special.closeKeyboard(thisActivity);

                    loadStudentsList();
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            };
}