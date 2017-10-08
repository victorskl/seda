package seda.consoleapp;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Created by liubingfeng on 7/10/2017.
 */

public class BluetoothWriteThread extends Thread {

    private static final String TAG = "BluetoothWriteThread";

    private LinkedBlockingDeque<String> sendQueue = new LinkedBlockingDeque();
    private AppCompatActivity activity;
    private BufferedWriter out;

    BluetoothWriteThread(AppCompatActivity activity, BufferedWriter out) {
        this.activity = activity;
        this.out = out;

//        test send to server
//        addSendMessageToQueue("Hello From Client");

    }

    public void addSendMessageToQueue(String message) {
        sendQueue.add(JSONDataFactory.getProfileJSON().toString() + "\n");
    }


    @Override
    public void run() {
        String message = null;
        while (true) {
            try {
                message = sendQueue.take();
                Log.d(TAG, "sending msg -> " + message);

                final String finalMessage = message;
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(activity.getApplicationContext(), "Send to server -> " + finalMessage, Toast.LENGTH_LONG).show();
                    }
                });
                out.write(message);
                out.flush();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }
    }
}
