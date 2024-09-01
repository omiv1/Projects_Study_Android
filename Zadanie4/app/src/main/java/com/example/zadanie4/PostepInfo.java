package com.example.zadanie4;

import android.os.Parcel;
import android.os.Parcelable;

public class PostepInfo implements Parcelable {
    long mPobranychBajtow;
    long mRozmiar;
    int mStatus;

    public PostepInfo(long pobranychBajtow, long rozmiar, int status) {
        mPobranychBajtow = pobranychBajtow;
        mRozmiar = rozmiar;
        mStatus = status;
    }

    protected PostepInfo(Parcel in) {
        mPobranychBajtow = in.readLong();
        mRozmiar = in.readLong();
        mStatus = in.readInt();
    }

    public static final Creator<PostepInfo> CREATOR = new Creator<PostepInfo>() {
        @Override
        public PostepInfo createFromParcel(Parcel in) {
            return new PostepInfo(in);
        }

        @Override
        public PostepInfo[] newArray(int size) {
            return new PostepInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(mPobranychBajtow);
        parcel.writeLong(mRozmiar);
        parcel.writeInt(mStatus);
    }
}