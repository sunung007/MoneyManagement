package org.androidtown.moneymanagement.Enroll;

import android.annotation.SuppressLint;
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

import org.androidtown.moneymanagement.Manage.DetailInfoPopup;
import org.androidtown.moneymanagement.R;
import org.androidtown.moneymanagement.Common.Student;

import java.util.ArrayList;

public class EnrollAlreadyListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<Student> students;

    public static class MyEnrollViewHolder extends RecyclerView.ViewHolder {

        TextView mSidView;
        TextView mNameView;
        TextView mTypeView;
        TextView mSupportView;
        CardView mListView;

        MyEnrollViewHolder(View view) {
            super(view);

            mSidView = view.findViewById(R.id.text_list_enroll_already_sid);
            mNameView = view.findViewById(R.id.text_list_enroll_already_name);
            mTypeView = view.findViewById(R.id.text_list_enroll_already_type);
            mSupportView = view.findViewById(R.id.text_list_enroll_already_support);
            mListView = view.findViewById(R.id.cardView_list_enroll_already);
        }
    }

    EnrollAlreadyListAdapter(ArrayList<Student> src) {
        students = new ArrayList<>();
        students.addAll(src);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.list_enroll_already, parent, false);

        return new EnrollAlreadyListAdapter.MyEnrollViewHolder(v);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        final EnrollAlreadyListAdapter.MyEnrollViewHolder myViewHolder =
                (EnrollAlreadyListAdapter.MyEnrollViewHolder) holder;
        final Student student = students.get(position);

        myViewHolder.mSidView.setText(student.sid);
        myViewHolder.mNameView.setText(student.name);
        myViewHolder.mTypeView.setText(student.type);
        myViewHolder.mSupportView.setText(student.support);

        myViewHolder.mListView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), DetailInfoPopup.class);
                intent.putExtra("student", student);
                intent.putExtra("mode", 1);

                view.getContext().startActivity(intent);
            }
        });

        
        myViewHolder.mListView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        myViewHolder.mListView.setCardBackgroundColor(Color.LTGRAY);
                        break;

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
