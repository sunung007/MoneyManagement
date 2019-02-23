package org.androidtown.moneymanagement.Manage;

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

import org.androidtown.moneymanagement.R;
import org.androidtown.moneymanagement.Common.SoundSearcher;
import org.androidtown.moneymanagement.Common.Student;

import java.util.ArrayList;
import java.util.Locale;

public class ManageListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<Student> students;
    private ArrayList<Student> searchList = new ArrayList<>();

    public static class MyManageViewHolder extends RecyclerView.ViewHolder {

        TextView mSidView, mSnameView, mTypeView, mSupportView;
        CardView mListView;

        MyManageViewHolder(View view) {
            super(view);

            mSidView = view.findViewById(R.id.manager_sid);
            mSnameView = view.findViewById(R.id.manager_name);
            mTypeView = view.findViewById(R.id.manager_type);
            mSupportView = view.findViewById(R.id.manager_support);
            mListView = view.findViewById(R.id.manager_student_list);
        }
    }

    ManageListAdapter(ArrayList<Student> scr) {
        students = new ArrayList<>(scr);
        searchList.addAll(students);
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_manage, parent, false);

        return new MyManageViewHolder(v);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        final MyManageViewHolder myViewHolder = (MyManageViewHolder) holder;

        final Student student = searchList.get(position);

        myViewHolder.mSidView.setText(student.sid);
        myViewHolder.mSnameView.setText(student.name);
        myViewHolder.mTypeView.setText(student.type);
        myViewHolder.mSupportView.setText(student.support);

        myViewHolder.mListView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), DetailInfoPopup.class);
                intent.putExtra("student", student);
                intent.putExtra("size", getItemCount());
                intent.putExtra("mode", 0);
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
                        myViewHolder.mListView.setCardBackgroundColor(Color.WHITE);
                        break;
                }
                return false;
            }
        });
    }

    void filter(String query) {
        query = query.toLowerCase(Locale.getDefault());
        searchList.clear();

        if (query.length() == 0) {
            searchList.addAll(students);
        } else {
            for (Student iter : students) {
                String name = iter.name;

                if (SoundSearcher.matchString(name, query)) {
                    searchList.add(iter);
                }
                else if (name.toLowerCase().contains(query)) {
                    searchList.add(iter);
                }
            }
        }

        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return searchList.size();
    }

}
