package org.androidtown.moneymanagement;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;


public class SearchStudentFragment extends Fragment {
    private Spinner mSidView;
    private EditText mSnameView;
    private View mSearchView;
    private ProgressBar mProgressBar;

    private SearchTask mAuthTask;

    private String mSid;
    private String mSname;

    private DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference conditionRef = mRootRef.child("student");
    private ValueEventListener valueEventListener;

    private ArrayList<StudentInfo> target = new ArrayList<>();

    public SearchStudentFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_search_student, container, false);
        Button studentSearchButton = (Button) view.findViewById(R.id.student_search_button);

        mSidView = (Spinner) view.findViewById(R.id.sid);
        mSnameView = (EditText) view.findViewById(R.id.sname);
        mSearchView = view.findViewById(R.id.student_search_view);
        mProgressBar = (ProgressBar) view.findViewById(R.id.search_progressBar);

        mProgressBar.setVisibility(View.GONE);

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

                mProgressBar.setVisibility(View.VISIBLE);

                searchStudent();
            }
        });

        // If user push enter.
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

        mSearchView.setOnKeyListener(new View.OnKeyListener() {
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
        // Reset errors.
        mSnameView.setError(null);

        // Store values at the time of the search attempt.
        mSid = mSidView.getSelectedItem().toString().substring(0, 2);
        mSname = mSnameView.getText().toString();

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
            focusView.requestFocus();
        } else {
            mAuthTask = new SearchTask(mSid, mSname);
            mAuthTask.execute((Void) null);
        }
    }


    @Override
    public void onAttachFragment(Fragment childFragment) {
        super.onAttachFragment(childFragment);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    public ArrayList<StudentInfo> getTarget() {
        return target;
    }

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

                    int currentYear, tmpAll;
                    int tmpYear, tmpType;
                    currentYear = Calendar.getInstance().get(Calendar.YEAR)
                            + Calendar.getInstance().get(Calendar.MONTH)/6;

                    while(child.hasNext()) {
                        ds = child.next();
                        tName = ds.child("Sname").getValue().toString();
                        tId = ds.child("Sid").getValue().toString();

                        if(mSname.equals(tName) && mSid.equals(tId)) {
                            tAmount = ds.child("Pamount").getValue().toString();
                            tType = ds.child("Ptype").getValue().toString();
                            tYear = ds.child("Pyear").getValue().toString();

                            if(tType.contains("전액")) {
                                cSupport = "YES";
                            } else if(tType.contains("미납")) {
                                cSupport = "NO";
                            } else if(tType.contains("학기")) {
                                // The condition is about whether a person can support by student's money.
                                try {
                                    tmpYear = Integer.parseInt(tYear.substring(0, 2), 10) + 2000;
                                    tmpType = Integer.parseInt(tAmount.substring(0, 1), 10);

                                    tmpAll = tmpYear + tmpType/2;

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

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }

            };

            conditionRef.addListenerForSingleValueEvent(valueEventListener);

            try {
                // Simulate network access.
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                return false;
            }

            if(!target.isEmpty()) {
                return true;
            }
            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            mProgressBar.setVisibility(View.GONE);

            if (success) {
                // If the result array list is not empty, close keypad and change fragment.
                // Close keypad.
                InputMethodManager imm = (InputMethodManager) getActivity().
                        getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);

                // Change fragment.
                Fragment fragment = new SearchResultFragment();
                FragmentTransaction ft = getChildFragmentManager().beginTransaction();
                ft.replace(R.id.student_search_result, fragment);
                ft.commit();

            } else {
                // If the result array list is empty, which means
                // the student that user put in is not in DB, just float Toast.
                Toast toast = Toast.makeText(getContext(), "찾는 대상이 납부자 명단에 없습니다.", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                toast.show();
            }

        conditionRef.removeEventListener(valueEventListener);
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            mProgressBar.setVisibility(View.GONE);
        }
    }
}