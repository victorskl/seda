/**
 *
 * Setting class is used to customise the Console App.
 *
 * The car training xml files are from https://github.com/ramitix/Car_lane_sign_detection
 * @author  San Kho Lin (829463), Bingfeng Liu (639187), Yixin Chen(522819)
 * @version 1.0
 * @since   2017-09-15
 */
package seda.consoleapp;

public class Setting {
    private static final Setting ourInstance = new Setting();

    public static Setting getInstance() {
        return ourInstance;
    }

    private Setting() {
    }


}
