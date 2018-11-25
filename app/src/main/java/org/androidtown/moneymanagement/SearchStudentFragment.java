package org.androidtown.moneymanagement;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
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
    private Spinner mSidView;
    private EditText mSnameView;
    private View mSearchView;

    private String mSid;
    private String mSname;

    private DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference conditionRef = mRootRef.child("student");

    private ArrayList<StudentInfo> target = new ArrayList<>();

    public SearchStudentFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_search_student, container, false);

        mSidView = (Spinner) view.findViewById(R.id.sid);
        mSnameView = (EditText) view.findViewById(R.id.sname);
        mSearchView = view.findViewById(R.id.search_form);

        Button studentSearchButton = (Button) view.findViewById(R.id.student_search_button);
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

                searchStudent(view);
            }
        });


        return view;
    }


    private void searchStudent(View view) {
        // Reset errors.
        mSnameView.setError(null);

        // Store values at the time of the search attempt.
        mSid = mSidView.getSelectedItem().toString();
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
            searchProcess(view);
        }
    }

    private void searchProcess(final View view) {
        conditionRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                // Start searching process.
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

                // Check whether the result is empty. If the result is empty,
                // then there is not the student who has Sid, and Sname user put in.
                if(!target.isEmpty()) {

                    // Code about Toast is just for test.
//                    String tmp2 = target.get(0).Sname;
//                    Toast toast = Toast.makeText(getContext(), "SUCCESS: " + tmp2, Toast.LENGTH_SHORT);
//                    toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
//                    toast.show();


                    // From this line, the code is for real use.
                    // If the result array list is not empty, close keypad and change fragment.
                    // Close keypad.
                    InputMethodManager imm = (InputMethodManager) getActivity().
                            getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

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
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
//                    Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }

        });
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

}