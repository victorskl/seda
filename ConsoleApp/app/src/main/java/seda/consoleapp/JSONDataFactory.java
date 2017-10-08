package seda.consoleapp;

import org.json.JSONObject;

import java.util.HashMap;

public class JSONDataFactory {

    // in
    public static JSONObject getSettingJSON() {
        HashMap<String, String> setting = new HashMap<>();

        setting.put("user_name", "");
        setting.put("setting_id", "");
        setting.put("setting_name", "");
        setting.put("optimize_collision_detection", "");
        setting.put("collision_detection_sensitivity", "");
        setting.put("lane_departure_detection_sensitivity", "");
        setting.put("meter_to_alert_collision", "");

        return new JSONObject(setting);
    }

    // out
    public static JSONObject getProfileJSON() {
        HashMap<String, String> profile = new HashMap<>();

//        profile.put("user_name", "");
//        profile.put("profile_id", "");
//        profile.put("route_name", "");
        profile.put("start_time", "");
        profile.put("end_time", "");
        profile.put("sensor_stat", "");
        profile.put("collision_warning_count", "");
        profile.put("gyro_head_move_error_count", "");

        return new JSONObject(profile);
    }
}
