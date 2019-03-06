package org.androidtown.moneymanagement.Search;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.androidtown.moneymanagement.R;
import org.androidtown.moneymanagement.Common.Student;

import java.util.ArrayList;

public class SearchOnlyResultListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView mSidView;
        TextView mNameView;
        TextView mYearView;
        TextView mTypeView;
        TextView mAmountView;
        TextView mSupportView;

        MyViewHolder(View view) {
            super(view);

            mSidView = view.findViewById(R.id.text_list_search_only_result_sid);
            mNameView = view.findViewById(R.id.text_list_search_only_result_name);
            mYearView = view.findViewById(R.id.text_list_search_only_result_year);
            mTypeView = view.findViewById(R.id.text_list_search_only_result_type);
            mAmountView = view.findViewById(R.id.text_list_search_only_result_amount);
            mSupportView = view.findViewById(R.id.text_list_search_only_result_support);
        }
    }

    private ArrayList<Student> students;

    SearchOnlyResultListAdapter(ArrayList<Student> scr) {
        students = new ArrayList<>(scr);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.list_search_only_result, parent, false);

        return new SearchOnlyResultListAdapter.MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        SearchOnlyResultListAdapter.MyViewHolder myViewHolder
                = (SearchOnlyResultListAdapter.MyViewHolder) holder;

        Student student = students.get(position);

        myViewHolder.mSidView.setText(student.sid);
        myViewHolder.mNameView.setText(student.name);
        myViewHolder.mYearView.setText(student.year);
        myViewHolder.mTypeView.setText(student.type);
        myViewHolder.mAmountView.setText(student.amount);
        myViewHolder.mSupportView.setText(student.support);
    }

    @Override
    public int getItemCount() {
        return students.size();
    }

}
