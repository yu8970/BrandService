package com.brandservice;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class TaskTarget implements Parcelable {
    private String id;

    private String name;

    private Bitmap image;

    private float score;

    private String remark;

    protected TaskTarget(Parcel in) {
        id = in.readString();
        name = in.readString();
        image = in.readParcelable(Bitmap.class.getClassLoader());
        score = in.readFloat();
        remark = in.readString();
    }

    public static final Creator<TaskTarget> CREATOR = new Creator<TaskTarget>() {
        @Override
        public TaskTarget createFromParcel(Parcel in) {
            return new TaskTarget(in);
        }

        @Override
        public TaskTarget[] newArray(int size) {
            return new TaskTarget[size];
        }
    };

    @Override
    public String toString() {
        return "SearchTarget{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", image=" + image +
                ", score=" + score +
                ", remark='" + remark + '\'' +
                '}';
    }

    public TaskTarget(){
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeParcelable(image, flags);
        dest.writeFloat(score);
        dest.writeString(remark);
    }
}
