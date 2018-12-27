package org.androidtown.moneymanagement;

import android.os.Parcel;
import android.os.Parcelable;

public class StudentInfo implements Parcelable {
    public String Pamount;
    public String Ptype;
    public String Pyear;
    public String Sid;
    public String Sname;
    public String Csupport = "UNKNOWN";

    public StudentInfo() {
    }

    public StudentInfo(Parcel in) {
        readFromParcel(in);
    }

    public StudentInfo(String _Pamount, String _Ptype, String _Pyear,
                       String _Sid, String _Sname) {
        Pamount = _Pamount;
        Ptype = _Ptype;
        Pyear = _Pyear;
        Sid = _Sid;
        Sname = _Sname;
    }

    public StudentInfo(String _Pamount, String _Ptype, String _Pyear,
                       String _Sid, String _Sname, String _Csupport) {
        Pamount = _Pamount;
        Ptype = _Ptype;
        Pyear = _Pyear;
        Sid = _Sid;
        Sname = _Sname;
        Csupport = _Csupport;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(Pamount);
        parcel.writeString(Ptype);
        parcel.writeString(Pyear);
        parcel.writeString(Sid);
        parcel.writeString(Sname);
        parcel.writeString(Csupport);
    }

    public void writeFromParcel(Parcel dest, int flags) {
        dest.writeString(Pamount);
        dest.writeString(Ptype);
        dest.writeString(Pyear);
        dest.writeString(Sid);
        dest.writeString(Sname);
        dest.writeString(Csupport);
    }

    public void readFromParcel(Parcel in) {
        Pamount = in.readString();
        Ptype = in.readString();
        Pyear = in.readString();
        Sid = in.readString();
        Sname = in.readString();
        Csupport = in.readString();
    }

    @SuppressWarnings("rawtypes")
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        @Override
        public StudentInfo createFromParcel(Parcel in) {
            return new StudentInfo(in);
        }

        @Override
        public StudentInfo[] newArray(int size) {
            return new StudentInfo[size];
        }
    };
}
