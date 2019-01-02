package org.androidtown.moneymanagement;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class EnrollAlreadyStudentsListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<StudentInfo> students;

    public static class MyEnrollViewHolder extends RecyclerView.ViewHolder {

        TextView mSidView;
        TextView mSnameView;
        TextView mTypeView;
        TextView mSupportView;
        CardView mListView;

        MyEnrollViewHolder(View view) {
            super(view);

            mSidView = view.findViewById(R.id.enroll_already_students_sid);
            mSnameView = view.findViewById(R.id.enroll_already_students_sname);
            mTypeView = view.findViewById(R.id.enroll_already_students_type);
            mSupportView = view.findViewById(R.id.enroll_already_students_support);
            mListView = view.findViewById(R.id.enroll_already_student_list);
        }
    }

    EnrollAlreadyStudentsListAdapter(ArrayList<StudentInfo> scr) {
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
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        final EnrollAlreadyStudentsListAdapter.MyEnrollViewHolder myViewHolder =
                (EnrollAlreadyStudentsListAdapter.MyEnrollViewHolder) holder;
        final StudentInfo studentInfo = students.get(position);

        myViewHolder.mSidView.setText(studentInfo.Sid);
        myViewHolder.mSnameView.setText(studentInfo.Sname);
        myViewHolder.mTypeView.setText(studentInfo.Ptype);
        myViewHolder.mSupportView.setText(studentInfo.Csupport);

        myViewHolder.mListView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), DetailStudentInfoPopup.class);
                intent.putExtra("student", studentInfo);
                intent.putExtra("size", EnrollFragment.size);
                intent.putExtra("mode", 1);

                view.getContext().startActivity(intent);
            }
        });

        
        myViewHolder.mListView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    // When pressed.
                    case MotionEvent.ACTION_DOWN:
                        myViewHolder.mListView.setCardBackgroundColor(Color.LTGRAY);
                        break;

                    // when does not pressed.
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        myViewHolder.mListView.setCardBackgroundColor(Color.parseColor("#e6e6e6"));
                        myViewHolder.mListView.setCardElevation(0);
                        break;
                }
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return students.size();
    }
}
