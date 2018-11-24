package org.androidtown.moneymanagement;

import android.os.Bundle;
import android.support.annotation.NonNull;
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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;


public class SearchStudentFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

//    private View mProgressView;

    private Spinner mSidView;
    private EditText mSnameView;
    private View mSearchView;

    private String mSid;
    private String mSname;

    private DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference conditionRef = mRootRef.child("student");

    ArrayList<StudentInfo> target = new ArrayList<>();



    public SearchStudentFragment() {
        // Required empty public constructor
    }

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
        mSid = mSidView.getSelectedItem().toString();
        mSname = mSnameView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a nonempty name.
        if (TextUtils.isEmpty(mSid)) {
            mSnameView.setError(getString(R.string.error_field_required));
            focusView = mSnameView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            conditionRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Iterator<DataSnapshot> child = dataSnapshot.getChildren().iterator();
                    DataSnapshot ds;
                    String tName, tId, tAmount, tType, tYear;
                    target.clear();

                    while(child.hasNext()) {
                        ds = child.next();
                        tName = ds.child("Sname").getValue().toString();
                        tId = ds.child("Sid").getValue().toString();

                        if(mSname.equals(tName) && mSid.equals(tId)) {
                            tAmount = ds.child("Pamount").getValue().toString();
                            tType = ds.child("Ptype").getValue().toString();
                            tYear = ds.child("Pyear").getValue().toString();

                            target.add(new StudentInfo(tAmount, tType, tYear, tId, tName));
                        }
                    }

                    int tmp = target.size();
                    String tmp2 = null;
                    if(tmp > 0) {tmp2 = target.get(0).Sname;}
                    Toast toast = Toast.makeText(getContext(), "FALI: " + tmp + tmp2, Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                    toast.show();

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
//                    Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                }
            });
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