package org.androidtown.moneymanagement;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class SearchStudentListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView mPyearView;
        private TextView mPtypeView;
        private TextView mPamountView;
        private TextView mSupportView;

        MyViewHolder(View view) {
            super(view);
            mPyearView = (TextView) view.findViewById(R.id.result_pyear);
            mPtypeView = (TextView) view.findViewById(R.id.result_ptype);
            mPamountView = (TextView) view.findViewById(R.id.result_pamount);
            mSupportView = (TextView) view.findViewById(R.id.result_support);
        }
    }

    private ArrayList<StudentInfo> students;
    private int sNumber;

    SearchStudentListAdapter(ArrayList<StudentInfo> scr) {
        sNumber = scr.size();
        students = new ArrayList<>(scr);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.search_result_lists, parent, false);

        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MyViewHolder myViewHolder = (MyViewHolder) holder;
        StudentInfo studentInfo = students.get(position);

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
