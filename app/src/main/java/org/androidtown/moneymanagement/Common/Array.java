package org.androidtown.moneymanagement.Common;

import android.content.Context;

import org.androidtown.moneymanagement.R;

import java.util.ArrayList;
import java.util.Calendar;

public class Array {
    private ArrayList<String> arrayList;

    private Mode mode;
    private Context context;

    public Array(Mode _mode, Context _context) {
        mode = _mode;
        context = _context;
        arrayList = new ArrayList<>();

        createArray();
    }

    public ArrayList<String> getArrayList() {
        return arrayList;
    }

    private void createArray() {
        int tmpYear;
        String unit;

        tmpYear = Calendar.getInstance().get(Calendar.YEAR);

        switch (mode) {
            case YEAR:
                unit = context.getResources().getString(R.string.unit_year);
                break;

            case SID_ENROLL:
            case SID_SEARCH:
            default:
                unit = context.getResources().getString(R.string.unit_sid);
                break;
        }


        for(int i = tmpYear - 2017 ; i >= 0 ; --i) {
            String newEntry = String.valueOf(17 + i) + unit;
            arrayList.add(newEntry);
        }

        if(mode == Mode.SID_SEARCH) {
            String newEntry = context.getResources().getString(R.string.additional_sid);
            arrayList.add(newEntry);
        }
    }

}
