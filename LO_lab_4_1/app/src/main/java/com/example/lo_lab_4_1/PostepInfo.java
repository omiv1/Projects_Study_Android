package com.example.lo_lab_4_1;

import android.os.Parcel;
import android.os.Parcelable;

public class PostepInfo implements Parcelable {
    public long mPobranychBajtow;
    public long mRozmiar;
    public String mStatus;

    public PostepInfo() {
        mPobranychBajtow = 0;
        mRozmiar = 0;
        mStatus = "Downloading";
    }

    protected PostepInfo(Parcel in) {
        mPobranychBajtow = in.readLong();
        mRozmiar = in.readLong();
        mStatus = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(mPobranychBajtow);
        dest.writeLong(mRozmiar);
        dest.writeString(mStatus);
    }

    @Override
    public int describeContents() {
        return 0;
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
}
