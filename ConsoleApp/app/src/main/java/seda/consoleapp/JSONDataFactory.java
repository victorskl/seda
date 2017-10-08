package seda.consoleapp;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by liubingfeng on 7/10/2017.
 */

//
//"setting":{
//        "user_name":"",
//        "setting_id":0,
//        "setting_name":"",
//        "optimize_collision_detection":true,
//        "collision_detection_sensitivity":5,
//        "lane_departure_detection_sensitivity":5,
//        "meter_to_alert_collision":20
//        }
//
//        "profile":{
//        "user_name":"",
//        "profile_id":0,
//        "route_name":"",
//        "start_time":0,
//        "end_time":0,
//        "sensor_stat":{
//        "collision_warning_count":0,
//        "lane_departure_warning_count":0,
//        "gyro_head_move_error_count":0
//        }

public class JSONdataFactory
{
    public static JSONObject getSettingJSON()
    {
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

    public static JSONObject getProfileJSON()
    {
        HashMap<String, String> profile = new HashMap<>();

        profile.put("user_name", "");
        profile.put("profile_id", "");
        profile.put("route_name", "");
        profile.put("start_time", "");
        profile.put("end_time", "");
        profile.put("sensor_stat", "");
        profile.put("collision_warning_count", "");
        profile.put("gyro_head_move_error_count", "");

        return new JSONObject(profile);

    }
}
