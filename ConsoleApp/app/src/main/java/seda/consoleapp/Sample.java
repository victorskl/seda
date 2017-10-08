/**
 *
 * Sample is used to store the sample data in different view mode.
 *
 * The car training xml files are from https://github.com/ramitix/Car_lane_sign_detection
 * @author  San Kho Lin (829463), Bingfeng Liu (639187), Yixin Chen(522819)
 * @version 1.0
 * @since   2017-09-15
 */
package seda.consoleapp;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;

public class Sample implements Serializable {

    private Date startTime;
    private Date endTime;
    private SampleType sampleType;
    private int count;

    public Sample() {
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public SampleType getSampleType() {
        return sampleType;
    }

    public void setSampleType(SampleType sampleType) {
        this.sampleType = sampleType;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public JSONObject toJSONObject()
    {
        HashMap<String, String> data = new HashMap<>();
        data.put("count", "" + count);
        data.put("startTime", "" + startTime.getTime());
        data.put("endTime", "" + endTime.getTime());
        data.put("sampleType", "" + sampleType);

        return new JSONObject(data);
    }


}
