/**
 *
 * BluetoothConnectionAsync is a async thread class which is used to set up the read and write
 * thread for writing and reading from the connected socket between console app (client)
 * and base app (server)
 *
 * @author  San Kho Lin (829463), Bingfeng Liu (639187), Yixin Chen(522819)
 * @version 1.0
 * @since   2017-09-15
 */

package seda.consoleapp;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.UUID;

public class BluetoothConnectionAsync extends AsyncTask<Void, Void, Void> {

    private static final String TAG = "BluetoothConnectionAsync";

//  Bluetooth adapter used to get the bluetooth device
    private BluetoothAdapter bluetoothAdapter;
//  The bluetooth socket used to connect with server side
    private BluetoothSocket mmSocket;
    private BluetoothDevice mmDevice;
//  The BufferedWriter used to write to the socket and send to the server
    private BufferedWriter out;
//  The BufferedReader used to read the message from the server
    private BufferedReader in;
//  The BluetoothDevice instance is used to hold the paired server device
    private BluetoothDevice device;
//  The MainActivity instance
    private Activity activity;
//  The send
    private BluetoothWriteThread sendThread;
    private BluetoothReadThread readThread;

//  This string is used to form the unique but same UUID with the server bluetooth socket
//  Since the Bluetooth socket connection relied on the same UUID between client and server.
    String aString = "OUR_SECRET";
    UUID resultUUID = UUID.nameUUIDFromBytes(aString.getBytes());

    BluetoothConnectionAsync(Activity activity, BluetoothAdapter bluetoothAdapter, BluetoothDevice device) {
        this.activity = activity;
        this.bluetoothAdapter = bluetoothAdapter;
        this.device = device;
    }





    /**
     * This method is used to establishing the connection with Bluetooth server socket
     * @param voids no input
     * @return Void
     */

    @Override
    protected Void doInBackground(Void... voids) {

        try {
            // Cancel discovery because it otherwise slows down the connection.
            bluetoothAdapter.cancelDiscovery();

            // Get a BluetoothSocket to connect with the given BluetoothDevice.
            // MY_UUID is the app's UUID string, also used in the server code.
            mmSocket = device.createRfcommSocketToServiceRecord(resultUUID);
            mmSocket.connect();

            out = new BufferedWriter(new OutputStreamWriter(mmSocket.getOutputStream()));
            in = new BufferedReader(new InputStreamReader(mmSocket.getInputStream()));
            readThread = new BluetoothReadThread(activity, in);
            sendThread = new BluetoothWriteThread(activity, out);
            readThread.start();
            sendThread.start();
        } catch (IOException e) {
            Log.wtf(TAG, "Socket's create() method failed", e);
        }
        //client connecting to server sockt
        return null;
    }

    /**
     * This method is used to send data string.
     * @param sendString data string
     * @return void
     */

    public void sendData(String sendString) {
        try {
            sendThread.addSendMessageToQueue(sendString);
        } catch (Exception e) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(activity, "Sending data to BaseApp failed! Seem a Paired Bluetooth Server is down."
                            , Toast.LENGTH_LONG).show();
                }
            });

            Log.wtf(TAG, "sendData: Sending data to BaseApp failed! Seem a Paired Bluetooth Server is down.", e);
        }
    }
}
