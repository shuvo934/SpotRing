package ttit.com.shuvo.spotring.geofences.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class CustomRepetitionDataList implements Parcelable {
    private String begin_date;
    private String end_date;
    private String begin_time;
    private String end_time;
    private boolean updated;

    public CustomRepetitionDataList(String begin_date, String end_date, String begin_time, String end_time, boolean updated) {
        this.begin_date = begin_date;
        this.end_date = end_date;
        this.begin_time = begin_time;
        this.end_time = end_time;
        this.updated = updated;
    }

    protected CustomRepetitionDataList(Parcel in) {
        begin_date = in.readString();
        end_date = in.readString();
        begin_time = in.readString();
        end_time = in.readString();
        updated = in.readByte() != 0;
    }

    public String getBegin_date() {
        return begin_date;
    }

    public void setBegin_date(String begin_date) {
        this.begin_date = begin_date;
    }

    public String getEnd_date() {
        return end_date;
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }

    public String getBegin_time() {
        return begin_time;
    }

    public void setBegin_time(String begin_time) {
        this.begin_time = begin_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public boolean isUpdated() {
        return updated;
    }

    public void setUpdated(boolean updated) {
        this.updated = updated;
    }

    public static final Creator<CustomRepetitionDataList> CREATOR = new Creator<>() {
        @Override
        public CustomRepetitionDataList createFromParcel(Parcel in) {
            return new CustomRepetitionDataList(in);
        }

        @Override
        public CustomRepetitionDataList[] newArray(int size) {
            return new CustomRepetitionDataList[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(begin_date);
        parcel.writeString(end_date);
        parcel.writeString(begin_time);
        parcel.writeString(end_time);
        parcel.writeByte((byte) (updated ? 1 : 0));
    }
}
