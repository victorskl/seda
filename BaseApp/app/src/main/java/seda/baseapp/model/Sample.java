package seda.baseapp.model;

public class Sample {

    @com.google.gson.annotations.SerializedName("text")
    private String mText;

    @com.google.gson.annotations.SerializedName("id")
    private String mId;

    @com.google.gson.annotations.SerializedName("complete")
    private boolean mComplete;

    // SEDA data sampling

    @com.google.gson.annotations.SerializedName("startTime")
    private long startTime;

    @com.google.gson.annotations.SerializedName("endTime")
    private long endTime;

    @com.google.gson.annotations.SerializedName("sampleType")
    private int sampleType;

    @com.google.gson.annotations.SerializedName("count")
    private int count;

    public Sample() {
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public int getSampleType() {
        return sampleType;
    }

    public void setSampleType(int sampleType) {
        this.sampleType = sampleType;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getText() {
        return mText;
    }

    public final void setText(String text) {
        mText = text;
    }

    public String getId() {
        return mId;
    }

    public final void setId(String id) {
        mId = id;
    }

    public boolean isComplete() {
        return mComplete;
    }

    public void setComplete(boolean complete) {
        mComplete = complete;
    }

    @Override
    public String toString() {
        return "Sample{" +
                "mText='" + mText + '\'' +
                ", mId='" + mId + '\'' +
                ", mComplete=" + mComplete +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", sampleType=" + sampleType +
                ", count=" + count +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Sample && ((Sample) o).mId == mId;
    }
}