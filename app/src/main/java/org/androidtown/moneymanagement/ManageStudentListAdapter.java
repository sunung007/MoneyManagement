package org.androidtown.moneymanagement;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class ManageStudentListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<StudentInfo> students;

    public static class MyManageViewHolder extends RecyclerView.ViewHolder {

        public TextView mSidNameView;
        public TextView mTypeView;
        public TextView mSupportView;
        public CardView mListView;

        public MyManageViewHolder(View view) {
            super(view);

            mSidNameView = view.findViewById(R.id.manager_sid_name);
            mTypeView = view.findViewById(R.id.manager_type);
            mSupportView = view.findViewById(R.id.manager_support);
            mListView = view.findViewById(R.id.manager_student_list);
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
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        MyManageViewHolder myViewHolder = (MyManageViewHolder) holder;

        final StudentInfo studentInfo = students.get(position);

        myViewHolder.mSidNameView.setText(studentInfo.Sid + " " + studentInfo.Sname);
        myViewHolder.mTypeView.setText(studentInfo.Ptype);
        myViewHolder.mSupportView.setText(studentInfo.Csupport);

        myViewHolder.mListView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), DetailStudentInfoPopup.class);
                intent.putExtra("student", studentInfo);
                intent.putExtra("position", position);
                intent.putExtra("size", getItemCount());
                view.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return students.size();
    }
}
