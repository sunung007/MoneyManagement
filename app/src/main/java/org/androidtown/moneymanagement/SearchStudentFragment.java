package org.androidtown.moneymanagement;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
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

    Spinner mSidView;
    EditText mSnameView;
    View mSearchView;
    ProgressBar mProgressBar;

    SearchTask mAuthTask;

    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference conditionRef = mRootRef.child("student");
    ValueEventListener valueEventListener;

    String mSid, mSname;

    ArrayList<StudentInfo> target = new ArrayList<>();

    public SearchStudentFragment() {
        // Required empty public constructor
    }


    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_search_student, container, false);
        Button studentSearchButton = view.findViewById(R.id.student_search_button);

        // Set views.
        mSidView = view.findViewById(R.id.sid);
        mSnameView = view.findViewById(R.id.sname);
        mSearchView = view.findViewById(R.id.student_search_view);
        mProgressBar = view.findViewById(R.id.search_progressBar);

        mProgressBar.setVisibility(View.GONE);

        // When search button is pushed.
        studentSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                }

                return false;
            }
        });

        // When this fragment is opened, close soft key.
        mSearchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager inputMethodManager =
                        (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

                if (inputMethodManager.isActive()) {
                    inputMethodManager.hideSoftInputFromWindow(
                            getActivity().getCurrentFocus().getWindowToken(), 0);
                }
            }
        });


        return view;
    }


    private void searchStudent() {
        // Set progress bar to visible.
        mProgressBar.setVisibility(View.VISIBLE);

        // Reset errors and array.
        mSnameView.setError(null);
        target.clear();

        // Store values at the time of the search attempt.
        mSid = mSidView.getSelectedItem().toString().substring(0, 2);
        mSname = mSnameView.getText().toString().trim();

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
        }
        else {
            mAuthTask = new SearchTask();
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


    public class SearchTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {

            // Initialize array.
            target.clear();

            if(mSid.equals("전체")) {
                valueEventListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

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
                            tName = ds.child("Sname").getValue().toString().trim();
                            tId = ds.child("Sid").getValue().toString().trim();

                            if(mSname.equals(tName)) {
                                tAmount = ds.child("Pamount").getValue().toString();
                                tType = ds.child("Ptype").getValue().toString();
                                tYear = ds.child("Pyear").getValue().toString();

                                if(tType.contains("전액")) {
                                    cSupport = "YES";
                                }
                                else if(tType.contains("미납")) {
                                    cSupport = "NO";
                                }
                                else if(tType.contains("학기")) {
                                    // The condition is about whether a person can support by student's money.
                                    try {
                                        tmpYear = Integer.parseInt(tYear.substring(0, 2), 10) + 2000;
                                        tmpType = Integer.parseInt(tAmount.substring(0, 1), 10);

                                        tmpAll = tmpYear + tmpType/2;

                                        cSupport = (tmpAll >= currentYear) ? "YES" : "NO";
                                    } catch (Exception e) {
                                        cSupport = "UNKNOWN";
                                    }
                                }
                                else {
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

            }
            else {
                valueEventListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        // Start searching process.
                        Iterator<DataSnapshot> child = dataSnapshot.getChildren().iterator();
                        DataSnapshot ds;
                        String index;
                        String tName, tId, tAmount, tType, tYear, cSupport;

                        int currentYear, tmpAll;
                        int tmpYear, tmpType;
                        currentYear = Calendar.getInstance().get(Calendar.YEAR)
                                + Calendar.getInstance().get(Calendar.MONTH)/6;

                        while(child.hasNext()) {
                            ds = child.next();

                            index = ds.getKey().toString();
                            tName = ds.child("Sname").getValue().toString().trim();
                            tId = ds.child("Sid").getValue().toString().trim();

                            if(mSname.equals(tName) && mSid.equals(tId)) {
                                tAmount = ds.child("Pamount").getValue().toString();
                                tType = ds.child("Ptype").getValue().toString();
                                tYear = ds.child("Pyear").getValue().toString();

                                if(tType.contains("전액")) {
                                    cSupport = "YES";
                                }
                                else if(tType.contains("미납")) {
                                    cSupport = "NO";
                                }
                                else if(tType.contains("학기")) {
                                    // The condition is about whether a person can support by student's money.
                                    try {
                                        tmpYear = Integer.parseInt(tYear.substring(0, 2), 10) + 2000;
                                        tmpType = Integer.parseInt(tAmount.substring(0, 1), 10);

                                        tmpAll = tmpYear + tmpType/2;

                                        cSupport = (tmpAll >= currentYear) ? "YES" : "NO";
                                    } catch (Exception e) {
                                        cSupport = "UNKNOWN";
                                    }
                                }
                                else {
                                    cSupport = "UNKNOWN";
                                }

                                target.add(new StudentInfo(index, tAmount, tType, tYear, tId, tName, cSupport));
                            }
                        }
                    }

                    // Just formal.
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        target.clear();
                    }

                };
            }

            // Add event listener of DB.
            conditionRef.addListenerForSingleValueEvent(valueEventListener);

            // Sync the code with DB server.
            try {
                long startTime = System.currentTimeMillis();
                long progressTime;

                Thread.sleep(500);

                while(true) {
                    if(!target.isEmpty()) {
                        return true;
                    }

                    Thread.sleep(500);
                    progressTime = System.currentTimeMillis();
                    if(progressTime - startTime > 2500) {
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

            // Whether work in doInBackground() is success.
            if (success) {
                // If the result array list is not empty, close keypad and change fragment.
                // Close keypad.
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);

                // Open popup activity.
                Intent intent = new Intent(getActivity().getApplicationContext(), SearchResultPopup.class);
                intent.putParcelableArrayListExtra("result", target);

                if(mSid.equals("전체")) {
                    intent.putExtra("type", 1);
                }
                else {
                    intent.putExtra("type", 0);
                }

                startActivityForResult(intent, 1);

            }
            else {
                // If the result array list is empty, which means
                // the student that user put in is not in DB, just float Toast.
                String message = "찾는 대상이 납부자 명단에 없습니다.";
                Toast toast = Toast.makeText(getContext(), message, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.BOTTOM, 0, 0);
                toast.show();
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
    }
}