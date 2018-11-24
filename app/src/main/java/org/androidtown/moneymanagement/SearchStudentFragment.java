package org.androidtown.moneymanagement;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SearchStudentFragment.OnFragmentInteractionListener} interface
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

    private Spinner mSidView;
    private EditText mSnameView;
//    private View mProgressView;
    private View mSearchView;



//    private OnFragmentInteractionListener mListener;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_search_student, container, false);
        Button studentSearchButton = (Button) view.findViewById(R.id.student_search_button);
        studentSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(), "Clicked", Toast.LENGTH_LONG);
//                searchStudent();
            }
        });

        mSearchView = view.findViewById(R.id.search_form);
//        mProgressView = findViewById(R.id.search_progress);


        return view;

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed() {
        Toast.makeText(getActivity(), "Clicked", Toast.LENGTH_LONG);
    }
//
//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
//    }
//
//    @Override
//    public void onDetach() {
//        super.onDetach();
//        mListener = null;
//    }
//
//    /**
//     * This interface must be implemented by activities that contain this
//     * fragment to allow an interaction in this fragment to be communicated
//     * to the activity and potentially other fragments contained in that
//     * activity.
//     * <p>
//     * See the Android Training lesson <a href=
//     * "http://developer.android.com/training/basics/fragments/communicating.html"
//     * >Communicating with Other Fragments</a> for more information.
//     */
//    public interface OnFragmentInteractionListener {
//        // TODO: Update argument type and name
//        void onFragmentInteraction(Uri uri);
//    }

    /*
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
            showProgress(true);
            mAuthTask = new Students(sid, sname);
            mAuthTask.execute((Void) null);
        }
    }
*/
}
