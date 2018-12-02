package org.androidtown.moneymanagement;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EnrollFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EnrollFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ValueEventListener valueEventListener;
    private SearchTask mAuthTask;

    private String mSid;
    private String mSname;
    private String mPyear;
    private String mPtype;
    private String mPamount;

    private EditText mSnameView;
    private Spinner mSidView;
    private Spinner mPyearView;
    private Spinner mPtypeView;
    private Spinner mPamountView;
    private View mEnrollView;
    private ProgressBar mProgressBar;

    private int totalNum;

    private ArrayList<StudentInfo> target = new ArrayList<>();
    private String title;

    private DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference conditionRef = mRootRef.child("student");

    public EnrollFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EnrollFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EnrollFragment newInstance(String param1, String param2) {
        EnrollFragment fragment = new EnrollFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_enroll, container, false);
        Button studentSearchButton = (Button) view.findViewById(R.id.enroll_button);

        mSnameView = (EditText) view.findViewById(R.id.enroll_name);
        mSidView = (Spinner) view.findViewById(R.id.enroll_sid);
        mPyearView = (Spinner) view.findViewById(R.id.enroll_pyear);
        mPtypeView = (Spinner) view.findViewById(R.id.enroll_ptype);
        mPamountView = (Spinner) view.findViewById(R.id.enroll_pamount);
        mEnrollView = view.findViewById(R.id.student_enroll_view);
        mProgressBar = (ProgressBar) view.findViewById(R.id.enroll_progressBar);

        mProgressBar.setVisibility(View.GONE);

        // When search button is pushed.
        studentSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // If there is already child fragment, remove it.
                FragmentManager fm = getChildFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                Fragment fragment = fm.findFragmentById(R.id.student_search_result);

                if(fragment != null) {
                    ft.remove(fragment);
                    ft.commit();
                }
                searchStudent();
            }
        });

        // When user push enter.
        mSnameView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN)
                        && (i == KeyEvent.KEYCODE_ENTER)) {
                    searchStudent();
                    return true;
                }
                return false;
            }
        });

        // When this fragment is opened, close soft key.
        mEnrollView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                InputMethodManager inputMethodManager =
                        (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

                if(inputMethodManager.isActive()) {
                    inputMethodManager.hideSoftInputFromWindow(
                            getActivity().getCurrentFocus().getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });

        return view;
    }

    private void searchStudent() {
        mProgressBar.setVisibility(View.VISIBLE);

        // Reset errors.
        mSnameView.setError(null);

        // Store values at the time of the search attempt.
        mSid = mSidView.getSelectedItem().toString().substring(0, 2);
        mSname = mSnameView.getText().toString().trim();
        mPtype = mPtypeView.getSelectedItem().toString().trim();
        mPamount = mPamountView.getSelectedItem().toString().trim();
        mPyear = mPyearView.getSelectedItem().toString().trim();

        boolean cancel = false;
        View focusView = null;

        // Check for a nonempty name.
        if (TextUtils.isEmpty(mSname)) {
            mSnameView.setError(getString(R.string.error_field_required));
            focusView = mSnameView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            mProgressBar.setVisibility(View.GONE);
            focusView.requestFocus();
        } else {
            mAuthTask = new SearchTask(mSid, mSname);
            mAuthTask.execute((Void) null);
        }
    }

//    public class PushTask extends AsyncTask<Void, Void, Boolean>{
//
//    }


    public class SearchTask extends AsyncTask<Void, Void, Boolean> {

        private final String sid;
        private final String name;

        SearchTask(String _sid, String _name) {
            sid = _sid;
            name = _name;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    // Initialize array.
                    target.clear();

                    // Start searching process.
                    Iterator<DataSnapshot> child = dataSnapshot.getChildren().iterator();
                    DataSnapshot ds;
                    String tName, tId, tAmount, tType, tYear, cSupport;

                    totalNum = (int) dataSnapshot.getChildrenCount();

                    int currentYear, tmpAll;
                    int tmpYear, tmpType;
                    currentYear = Calendar.getInstance().get(Calendar.YEAR)
                            + Calendar.getInstance().get(Calendar.MONTH) / 6;

                    while (child.hasNext()) {
                        ds = child.next();
                        tName = ds.child("Sname").getValue().toString().trim();
                        tId = ds.child("Sid").getValue().toString().trim();

                        if (mSname.equals(tName) && mSid.equals(tId)) {
                            tAmount = ds.child("Pamount").getValue().toString();
                            tType = ds.child("Ptype").getValue().toString();
                            tYear = ds.child("Pyear").getValue().toString();

                            if (tType.contains("전액")) {
                                cSupport = "YES";
                            } else if (tType.contains("미납")) {
                                cSupport = "NO";
                            } else if (tType.contains("학기")) {
                                // The condition is about whether a person can support by student's money.
                                try {
                                    tmpYear = Integer.parseInt(tYear.substring(0, 2), 10) + 2000;
                                    tmpType = Integer.parseInt(tAmount.substring(0, 1), 10);

                                    tmpAll = tmpYear + tmpType / 2;

                                    cSupport = (tmpAll >= currentYear) ? "YES" : "NO";
                                } catch (Exception e) {
                                    cSupport = "UNKNOWN";
                                }
                            } else {
                                cSupport = "UNKNOWN";
                            }

                            target.add(new StudentInfo(tAmount, tType, tYear, tId, tName, cSupport));
                        }
                    }
                }

                // Just formal.
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    target.clear();
                }

            };

            // Add event listener of DB.
            conditionRef.addListenerForSingleValueEvent(valueEventListener);

            // Sync the code with DB server.
            try {
                long startTime = System.currentTimeMillis();
                long progressTime;

                Thread.sleep(500);

                while (true) {
                    if (!target.isEmpty()) {
                        return true;
                    }

                    Thread.sleep(500);
                    progressTime = System.currentTimeMillis();
                    if (progressTime - startTime > 2500) {
                        return false;
                    }
                }
            } catch (Exception e) {
                return false;
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            mProgressBar.setVisibility(View.GONE);

            // Close keypad.
            InputMethodManager imm = (InputMethodManager) getActivity().
                    getSystemService(Activity.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);

            // Set intent.
            Intent intent = new Intent(getActivity().getApplicationContext(),
                    EnrollPopupActivity.class);

            StudentInfo studentInfo = setNewStudent();

            // Whether work in doInBackground() is success, which is
            // the same students were already in DB.
            if (success) {
                title = "이미 등록된 학우가 있습니다";

                intent.putParcelableArrayListExtra("already_students", target);
                intent.putExtra("new_student", studentInfo);
                intent.putExtra("total_num", totalNum);
                intent.putExtra("title", title);


                startActivityForResult(intent, 1);
            } else {
                // If the result array list is empty, which means
                // the student that user put in is not in DB, that is new student.
                title = "등록하시겠습니까";

                intent.putParcelableArrayListExtra("already_students", target);
                intent.putExtra("new_student", studentInfo);
                intent.putExtra("total_num", totalNum);
                intent.putExtra("title", title);

                startActivityForResult(intent, 1);
            }

            // Remove event listener to reuse another fragments.
            conditionRef.removeEventListener(valueEventListener);
        }


        // Just formal.
        @Override
        protected void onCancelled() {
            mAuthTask = null;
            mProgressBar.setVisibility(View.GONE);
        }

        public StudentInfo setNewStudent() {
            StudentInfo studentInfo = new StudentInfo(mPamount, mPtype, mPyear, mSid, mSname);

            String cSupport;
            int currentYear, tmpAll;
            int tmpYear, tmpType;
            currentYear = Calendar.getInstance().get(Calendar.YEAR)
                    + Calendar.getInstance().get(Calendar.MONTH) / 6;

            if (mPtype.contains("전액")) {
                cSupport = "YES";
            } else if (mPtype.contains("미납")) {
                cSupport = "NO";
            } else if (mPtype.contains("학기")) {
                // The condition is about whether a person can support by student's money.
                try {
                    tmpYear = Integer.parseInt(mPyear.substring(0, 2), 10) + 2000;
                    tmpType = Integer.parseInt(mPamount.substring(0, 1), 10);

                    tmpAll = tmpYear + tmpType / 2;

                    cSupport = (tmpAll >= currentYear) ? "YES" : "NO";
                } catch (Exception e) {
                    cSupport = "UNKNOWN";
                }
            } else {
                cSupport = "UNKNOWN";
            }

            studentInfo.Csupport = cSupport;

            return studentInfo;
        }
    }
}
