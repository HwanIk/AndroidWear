package com.example.hwanik.Yosee;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by hwanik on 2015. 5. 23..
 */
public class Materials implements Parcelable {
    public String mName;
    public String mNum;
    public String mUnit;


    public Materials(String mName, String mNum, String mUnit){
        this.mName=mName;
        this.mNum=mNum;
        this.mUnit=mUnit;
    }
    public Materials(Parcel in){
        mName=in.readString();
        mNum=in.readString();
        mUnit=in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mName);
        parcel.writeString(mNum);
        parcel.writeString(mUnit);
    }
    public static final Parcelable.Creator<Materials> CREATOR = new Parcelable.Creator<Materials>() {
        public Materials createFromParcel(Parcel in) {
            return new Materials(in);
        }

        public Materials[] newArray(int size) {
            return new Materials[size];
        }
    };
}
