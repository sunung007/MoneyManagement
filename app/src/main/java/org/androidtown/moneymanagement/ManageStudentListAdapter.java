package org.androidtown.moneymanagement;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.androidtown.moneymanagement.R;
import org.androidtown.moneymanagement.StudentInfo;

import java.util.ArrayList;

public class ManageStudentListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView mSidView;
        private TextView mNameView;
        private TextView mYearView;
        private TextView mTypeView;
        private TextView mAmountView;
        private TextView mSupportView;

        MyViewHolder(View view) {
            super(view);
            mSidView = (TextView) view.findViewById(R.id.sid);
            mNameView = (TextView) view.findViewById(R.id.name);
            mYearView = (TextView) view.findViewById(R.id.year);
            mTypeView = (TextView) view.findViewById(R.id.type);
            mAmountView = (TextView) view.findViewById(R.id.amount);
            mSupportView = (TextView) view.findViewById(R.id.support);
        }
    }

    private ArrayList<StudentInfo> students;
    private int sNumber;

    public ManageStudentListAdapter(ArrayList<StudentInfo> scr) {
        sNumber = scr.size();
        students = new ArrayList<>(scr);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_manage_student, parent, false);

        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MyViewHolder myViewHolder = (MyViewHolder) holder;
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
