package org.androidtown.moneymanagement.Search;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.androidtown.moneymanagement.R;
import org.androidtown.moneymanagement.Student;

import java.util.ArrayList;

public class SearchResultListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView mPyearView, mPtypeView, mPamountView, mSupportView;

        MyViewHolder(View view) {
            super(view);
            mPyearView = view.findViewById(R.id.result_pyear);
            mPtypeView = view.findViewById(R.id.result_ptype);
            mPamountView = view.findViewById(R.id.result_pamount);
            mSupportView = view.findViewById(R.id.result_support);
        }
    }

    private ArrayList<Student> students;

    SearchResultListAdapter(ArrayList<Student> scr) {
        students = new ArrayList<>(scr);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_search_result, parent, false);

        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MyViewHolder myViewHolder = (MyViewHolder) holder;
        Student student = students.get(position);

        myViewHolder.mPamountView.setText(student.amount);
        myViewHolder.mPtypeView.setText(student.type);
        myViewHolder.mPyearView.setText(student.year);
        myViewHolder.mSupportView.setText(student.support);
    }

    @Override
    public int getItemCount() {
        return students.size();
    }

}
