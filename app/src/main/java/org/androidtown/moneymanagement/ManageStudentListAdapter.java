package org.androidtown.moneymanagement;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class ManageStudentListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<StudentInfo> students;

    public static class MyManageViewHolder extends RecyclerView.ViewHolder {

        public TextView mSidView;
        public TextView mNameView;
        public TextView mYearView;
        public TextView mTypeView;
        public TextView mAmountView;
        public TextView mSupportView;

        public MyManageViewHolder(View view) {
            super(view);

            mSidView = (TextView) view.findViewById(R.id.manager_sid);
            mNameView = (TextView) view.findViewById(R.id.manager_name);
            mYearView = (TextView) view.findViewById(R.id.manager_year);
            mTypeView = (TextView) view.findViewById(R.id.manager_type);
            mAmountView = (TextView) view.findViewById(R.id.manager_amount);
            mSupportView = (TextView) view.findViewById(R.id.manager_support);
        }
    }

    public ManageStudentListAdapter(ArrayList<StudentInfo> scr) {
        students = new ArrayList<>(scr);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.manage_student_list, parent, false);

        return new MyManageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MyManageViewHolder myViewHolder = (MyManageViewHolder) holder;
        StudentInfo studentInfo = students.get(position);

        myViewHolder.mSidView.setText(studentInfo.Sid);
        myViewHolder.mNameView.setText(studentInfo.Sname);
        myViewHolder.mYearView.setText(studentInfo.Pyear);
        myViewHolder.mTypeView.setText(studentInfo.Ptype);
        myViewHolder.mAmountView.setText(studentInfo.Pamount);
        myViewHolder.mSupportView.setText(studentInfo.Csupport);
    }

    @Override
    public int getItemCount() {
        return students.size();
    }
}
