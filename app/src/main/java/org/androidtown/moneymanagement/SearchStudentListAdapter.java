package org.androidtown.moneymanagement;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

public class SearchStudentListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView mPyearView;
        private TextView mPtypeView;
        private TextView mPamountView;
        private TextView mSupportView;

        MyViewHolder(View view) {
            super(view);
            mPyearView = (TextView) view.findViewById(R.id.result_pyear);
            mPtypeView = (TextView) view.findViewById(R.id.result_ptype);
            mPamountView = (TextView) view.findViewById(R.id.result_pamount);
            mSupportView = (TextView) view.findViewById(R.id.result_support);
        }
    }

    private ArrayList<StudentInfo> students;
    private int sNumber;

    SearchStudentListAdapter(ArrayList<StudentInfo> scr) {
        sNumber = scr.size();
        students = new ArrayList<>(scr);

        countStudents();
    }

    private void countStudents() {
        Calendar calendar = new GregorianCalendar(Locale.KOREA);
        int currentYear = calendar.get(Calendar.YEAR);
        for(int i = 0 ; i < sNumber ; i++) {
            if(students.get(i).Pyear.contains("전액")) {
                students.get(i).Csupport = "YES";
                continue;
            }
            int sYear = Integer.parseInt(students.get(i).Pyear);
            int sType = Integer.parseInt(students.get(i).Ptype);
            sYear = sYear + (int) (sType / 2);

            if(sYear >= currentYear) {
                students.get(i).Csupport = "YES";
            } else {
                students.get(i).Csupport = "NO";
            }
        }
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_result_lists, parent, false);

        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MyViewHolder myViewHolder = (MyViewHolder) holder;

        myViewHolder.mPamountView.setText(students.get(position).Pamount);
        myViewHolder.mPtypeView.setText(students.get(position).Ptype);
        myViewHolder.mPyearView.setText(students.get(position).Pyear);
        myViewHolder.mSupportView.setText(students.get(position).Csupport);
    }

    @Override
    public int getItemCount() {
        return students.size();
    }


}
