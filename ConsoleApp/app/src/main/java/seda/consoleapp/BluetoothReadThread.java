/**
 *
 * BluetoothReadThread is a read thread class which is used to read message from the server
 *
 * @author  San Kho Lin (829463), Bingfeng Liu (639187), Yixin Chen(522819)
 * @version 1.0
 * @since   2017-09-15
 */
package seda.consoleapp;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;

public class BluetoothReadThread extends Thread {

    private static final String TAG = "BluetoothReadThread";

//  MainActivity instance
    private Activity activity;

//  server socket input stream
    private BufferedReader in;

    BluetoothReadThread(Activity activity, BufferedReader in) {
        this.activity = activity;
        this.in = in;
    }



    /**
     * This is run method will listen the message from server and show it on Toast.
     * @return void
     */

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
                break;
            }
        }

    }
}
