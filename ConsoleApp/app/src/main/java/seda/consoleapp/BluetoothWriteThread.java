/**
 *
 * BluetoothWriteThread is a thread class which is used to send the JSON data as a string to the
 * Bluetooth server.
 *
 * @author  San Kho Lin (829463), Bingfeng Liu (639187), Yixin Chen(522819)
 * @version 1.0
 * @since   2017-09-15
 */
package seda.consoleapp;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.concurrent.LinkedBlockingDeque;


public class BluetoothWriteThread extends Thread {

    private static final String TAG = "BluetoothWriteThread";

//  This is the message queue for storing JSON messages that is ready to be sent.
    private LinkedBlockingDeque<String> sendQueue = new LinkedBlockingDeque();

//  The MainActivity instance
    private Activity activity;

//  This is the output stream used to write to the bluetooth socket.
    private BufferedWriter out;

//  The BluetoothWriterThread constructor
    BluetoothWriteThread(Activity activity, BufferedWriter out) {
        this.activity = activity;
        this.out = out;

//        test send to server
//        addSendMessageToQueue("Hello From Client");

    }

    /**
     * This method is used to to send the JSON message as string to the Bluetooth server,
     * by pushing the mssage string into the blocking queue.
     * It will added a newline at the end of the message string.
     * Since the server side use newline to distinguish each message.
     * @param message This is the JSON data string
     * @return void
     */
    public void addSendMessageToQueue(String message) {
        sendQueue.add(message + "\n");
    }

    /**
     * This thread run method will call the take method of the blocking queue for getting newest
     * message string to write to the output socket.
     * After every successful send will trigger the Toast to show the sent message.
     * @return void
     */
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
