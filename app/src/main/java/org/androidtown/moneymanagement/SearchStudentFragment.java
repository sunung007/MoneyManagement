package org.androidtown.moneymanagement;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * @link SearchStudentFragment.OnFragmentInteractionListener interface
 * to handle interaction events.
 * Use the {@link SearchStudentFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchStudentFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Students mAuthTask = null;
//    private View mProgressView;

    private Spinner mSidView;
    private EditText mSnameView;
    private View mSearchView;


    public SearchStudentFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SearchStudentFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchStudentFragment newInstance(String param1, String param2) {
        SearchStudentFragment fragment = new SearchStudentFragment();
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
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_search_student, container, false);

        mSidView = (Spinner) view.findViewById(R.id.sid);
        mSnameView = (EditText) view.findViewById(R.id.sname);

        Button studentSearchButton = (Button) view.findViewById(R.id.student_search_button);
        studentSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchStudent();
            }
        });

        mSearchView = view.findViewById(R.id.search_form);
//        mProgressView = findViewById(R.id.search_progress);

        return view;
    }



    private void searchStudent() {
        // Reset errors.
        mSnameView.setError(null);

        // Store values at the time of the search attempt.
        String sid = mSidView.getSelectedItem().toString();
        String sname = mSnameView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a nonempty name.
        if (TextUtils.isEmpty(sid)) {
            mSnameView.setError(getString(R.string.error_field_required));
            focusView = mSnameView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the search attempt.
//            showProgress(true);
            mAuthTask = new Students(sid, sname);
            mAuthTask.execute((Void) null);
        }
    }

    public class Students extends AsyncTask<Void, Void, Boolean> {

        private final String mSid;
        private final String mSname;

        private DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference("student");
//        private DatabaseReference conditionRef = mRootRef.child("student");

        ArrayList<StudentInfo> target = new ArrayList<>();

        Students(String _sid, String _sname) {
            this.mSid = _sid;
            this.mSname = _sname;
        }


        @Override
        protected Boolean doInBackground(Void... params) {
            // Check whether the student is in DB.

            return false;

//            if(!target.isEmpty()) return true;
//            else return false;

        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
//            showProgress(false);

            Toast.makeText(getContext(), "mSid: " + mSid + "\nmSname: " + mSname, Toast.LENGTH_SHORT).show();

            if (success) {
                // I must change this line

                Toast toast = Toast.makeText(getContext(), "SUCCESS", Toast.LENGTH_SHORT);

//                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//                startActivity(intent);

//                finish();
            } else {
                StudentInfo studentInfo = new StudentInfo("1학기", "1학기만",
                        "2019", mSid, mSname);
                mRootRef.setValue(studentInfo);

//                conditionRef.addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                        StudentInfo studentInfo = dataSnapshot.getValue(StudentInfo.class);
//                        Toast.makeText(getContext(), studentInfo.toString(), Toast.LENGTH_SHORT);
//
//                        target.clear();
//
//                        for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                            studentInfo = snapshot.getValue(StudentInfo.class);
////                            if(studentInfo.Sname.equals(mSname) && studentInfo.Sid.equals(mSid)) {
//                                target.add(studentInfo);
////                            }
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
////                    Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
//                    }
//                });

                int tmp = target.size();
                Toast toast = Toast.makeText(getContext(), "FALI: " + tmp, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                toast.show();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
//            showProgress(false);
        }
    }

    public class StudentInfo {
        public String Pamount;
        public String Ptype;
        public String Pyear;

        public String Sid;
        public String Sname;

        StudentInfo(String _Pamount, String _Ptype, String _Pyear, String _Sid, String _Sname) {
            Pamount = _Pamount;
            Ptype = _Ptype;
            Pyear = _Pyear;
            Sid = _Sid;
            Sname = _Sname;
        }
    }


}
