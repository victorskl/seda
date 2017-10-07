package seda.consoleapp;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * Created by liubingfeng on 7/10/2017.
 */

public class BluetoothReadThread extends Thread {

    private static final String TAG = "BluetoothReadThread";

    private AppCompatActivity activity;
    BufferedReader in;

    BluetoothReadThread(AppCompatActivity activity, BufferedReader in) {
        this.activity = activity;
        this.in = in;
    }

    @Override
    public void run() {
        String inputLine = null;
        while (true) {
            Log.wtf(TAG, "start reading");
            try {
                inputLine = in.readLine();

                Log.wtf(TAG, "from server -> " + inputLine);

                //                    displayBluetoothTextView.setText(inputLine);

                //this code is ugly will refactor it later. just want to show msg on the UI
                final String finalInputLine = inputLine;
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(activity.getApplicationContext(), "From server -> " + finalInputLine, Toast.LENGTH_LONG).show();
                    }
                });

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
