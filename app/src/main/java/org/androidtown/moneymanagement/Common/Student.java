package org.androidtown.moneymanagement.Common;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Comparator;

public class Student implements Parcelable {

    public String index = "0";
    public String amount;
    public String type;
    public String year;
    public String sid;
    public String name;
    public String support = "UNKNOWN";

    public Student () {
    }

    public Student (Student _student) {
        index = _student.index;
        amount = _student.amount;
        type = _student.type;
        year = _student.year;
        sid = _student.sid;
        name = _student.name;
        support = _student.support;
    }

    public Student (Parcel in) {
        readFromParcel(in);
    }

    public Student (String _amount, String _type, String _year, String _sid,
                    String _name) {
        amount = _amount;
        type = _type;
        year = _year;
        sid = _sid;
        name = _name;
    }

    public Student (String _index, String _amount, String _type, String _year,
                    String _sid, String _name) {
        index = _index;
        amount = _amount;
        type = _type;
        year = _year;
        sid = _sid;
        name = _name;
    }

    public Student (String _index, String _amount, String _type, String _year,
                    String _sid, String _name, String _support) {
        index = _index;
        amount = _amount;
        type = _type;
        year = _year;
        sid = _sid;
        name = _name;
        support = _support;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(index);
        parcel.writeString(amount);
        parcel.writeString(type);
        parcel.writeString(year);
        parcel.writeString(sid);
        parcel.writeString(name);
        parcel.writeString(support);
    }

    private void readFromParcel(Parcel in) {
        index = in.readString();
        amount = in.readString();
        type = in.readString();
        year = in.readString();
        sid = in.readString();
        name = in.readString();
        support = in.readString();
    }


    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        @Override
        public Student createFromParcel(Parcel in) {
            return new Student(in);
        }

        @Override
        public Student[] newArray(int size) {
            return new Student[size];
        }
    };

    public static class SortStudents implements Comparator<Student> {

        @Override
        public int compare(Student s1, Student s2) {
            int ret = 0;

            if (Integer.parseInt(s1.sid) > Integer.parseInt(s2.sid)) {
                ret = -1;
            } else if (Integer.parseInt(s1.sid) == Integer.parseInt(s2.sid)) {
                ret = s1.name.compareTo(s2.name);

                if (ret < 0) ret = -1;
                else if (ret > 0) ret = 1;

            } else if (Integer.parseInt(s1.sid) < Integer.parseInt(s2.sid)) {
                ret = 1;
            }

            return ret;
        }
    }


}
