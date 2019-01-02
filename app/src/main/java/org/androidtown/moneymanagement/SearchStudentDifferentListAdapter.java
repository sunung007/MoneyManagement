package org.androidtown.moneymanagement;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class SearchStudentDifferentListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView mSidView, mSnameView;
        TextView mPyearView, mPtypeView, mPamountView, mSupportView;

        MyViewHolder(View view) {
            super(view);

            mSidView = view.findViewById(R.id.result_different_sid);
            mSnameView = view.findViewById(R.id.result_different_sname);

            mPyearView = view.findViewById(R.id.result_different_pyear);
            mPtypeView = view.findViewById(R.id.result_different_ptype);
            mPamountView = view.findViewById(R.id.result_different_pamount);
            mSupportView = view.findViewById(R.id.result_different_support);
        }
    }

    private ArrayList<StudentInfo> students;

    SearchStudentDifferentListAdapter(ArrayList<StudentInfo> scr) {
        students = new ArrayList<>(scr);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.search_result_different_lists, parent, false);

        return new SearchStudentDifferentListAdapter.MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        SearchStudentDifferentListAdapter.MyViewHolder myViewHolder
                = (SearchStudentDifferentListAdapter.MyViewHolder) holder;

        StudentInfo studentInfo = students.get(position);

        myViewHolder.mSidView.setText(studentInfo.Sid);
        myViewHolder.mSnameView.setText(studentInfo.Sname);

        myViewHolder.mPamountView.setText(studentInfo.Pamount);
        myViewHolder.mPtypeView.setText(studentInfo.Ptype);
        myViewHolder.mPyearView.setText(studentInfo.Pyear);
        myViewHolder.mSupportView.setText(studentInfo.Csupport);
    }

    @Override
    public int getItemCount() {
        return students.size();
    }

}
