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
