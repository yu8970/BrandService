package com.brandservice;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class BrandResult implements Parcelable {

    private Long traceId;

    private TaskResult detectResult;

    private TaskResult searchResult;

    private Integer totalCostTime;

    private String remark;

    private String createTime;

    protected BrandResult(Parcel in) {
        if (in.readByte() == 0) {
            traceId = null;
        } else {
            traceId = in.readLong();
        }
        detectResult = in.readParcelable(TaskResult.class.getClassLoader());
        searchResult = in.readParcelable(TaskResult.class.getClassLoader());
        if (in.readByte() == 0) {
            totalCostTime = null;
        } else {
            totalCostTime = in.readInt();
        }
        remark = in.readString();
        createTime = in.readString();
    }

    public static final Creator<BrandResult> CREATOR = new Creator<BrandResult>() {
        @Override
        public BrandResult createFromParcel(Parcel in) {
            return new BrandResult(in);
        }

        @Override
        public BrandResult[] newArray(int size) {
            return new BrandResult[size];
        }
    };

    public Long getTraceId() {
        return traceId;
    }

    public void setTraceId(Long traceId) {
        this.traceId = traceId;
    }

    public TaskResult getDetectResult() {
        return detectResult;
    }

    public void setDetectResult(TaskResult detectResult) {
        this.detectResult = detectResult;
    }

    public TaskResult getSearchResult() {
        return searchResult;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public void setSearchResult(TaskResult searchResult) {
        this.searchResult = searchResult;
    }

    public Integer getTotalCostTime() {
        return totalCostTime;
    }

    public void setTotalCostTime(Integer totalCostTime) {
        this.totalCostTime = totalCostTime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public BrandResult(){}


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        if (traceId == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeLong(traceId);
        }
        parcel.writeParcelable(detectResult, i);
        parcel.writeParcelable(searchResult, i);
        if (totalCostTime == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(totalCostTime);
        }
        parcel.writeString(remark);
        parcel.writeString(createTime);

    }

    @Override
    public String toString() {
        return "BrandResult{" +
                "traceId=" + traceId +
                ", detectorResult=" + detectResult +
                ", searchResult=" + searchResult +
                ", totalCostTime=" + totalCostTime +
                ", remark='" + remark + '\'' +
                ", createTime='" + createTime + '\'' +
                '}';
    }
}
