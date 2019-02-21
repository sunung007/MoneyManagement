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

public class SearchOnlyResultListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView mSidView, mSnameView;
        TextView mPyearView, mPtypeView, mPamountView, mSupportView;

        MyViewHolder(View view) {
            super(view);

            mSidView = view.findViewById(R.id.result_different_sid);
            mSnameView = view.findViewById(R.id.result_different_sname);

            mPyearView = view.findViewById(R.id.result_different_pyear);
            mPtypeView = view.findViewById(R.id.result_different_ptype);
            mPamountView = view.findViewById(R.id.result_different_pamount);
            mSupportView = view.findViewById(R.id.result_different_support);
        }
    }

    private ArrayList<Student> students;

    SearchOnlyResultListAdapter(ArrayList<Student> scr) {
        students = new ArrayList<>(scr);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_search_only_result, parent, false);

        return new SearchOnlyResultListAdapter.MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        SearchOnlyResultListAdapter.MyViewHolder myViewHolder
                = (SearchOnlyResultListAdapter.MyViewHolder) holder;

        Student student = students.get(position);

        myViewHolder.mSidView.setText(student.sid);
        myViewHolder.mSnameView.setText(student.name);

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
