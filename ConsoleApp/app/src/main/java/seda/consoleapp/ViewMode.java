/**
 *
 * ViewMode is used to indicate the current view mode of the camera frame (i.e. how to process
 * the frame)
 *
 * @author  San Kho Lin (829463), Bingfeng Liu (639187), Yixin Chen(522819)
 * @version 1.0
 * @since   2017-09-15
 */
package seda.consoleapp;

public enum ViewMode {
    RGBA("RGBA"),
    CANNY("Canny"),
    HEAD_CHECK("Head Check"),
    LANE_DETECTION("Lane Detection"),
    LANE_DETECTION_CANNY("Lane Detection Canny"),
    CAR_DETECTION("Car Detection");

    private String longName;

    ViewMode(String s) {
        this.longName = (String) s;
    }

    public String getLongName() {
        return longName;
    }
}
