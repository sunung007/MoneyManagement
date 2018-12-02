package org.androidtown.moneymanagement;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class EnrollAlreadyStudentsListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<StudentInfo> students;

    public static class MyEnrollViewHolder extends RecyclerView.ViewHolder {

        public TextView mSidNameView;
        public TextView mTypeView;
        public TextView mSupportView;

        public MyEnrollViewHolder(View view) {
            super(view);

            mSidNameView = (TextView) view.findViewById(R.id.enroll_already_students_sid_name);
            mTypeView = (TextView) view.findViewById(R.id.enroll_already_students_type);
            mSupportView = (TextView) view.findViewById(R.id.enroll_already_students_support);
        }
    }

    public EnrollAlreadyStudentsListAdapter(ArrayList<StudentInfo> scr) {
        students = new ArrayList<>(scr);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.enroll_already_student_list, parent, false);

        return new EnrollAlreadyStudentsListAdapter.MyEnrollViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        EnrollAlreadyStudentsListAdapter.MyEnrollViewHolder myViewHolder =
                (EnrollAlreadyStudentsListAdapter.MyEnrollViewHolder) holder;
        StudentInfo studentInfo = students.get(position);

        myViewHolder.mSidNameView.setText(studentInfo.Sid + " " + studentInfo.Sname);
        myViewHolder.mTypeView.setText(studentInfo.Ptype);
        myViewHolder.mSupportView.setText(studentInfo.Csupport);
    }

    @Override
    public int getItemCount() {
        return students.size();
    }
}
