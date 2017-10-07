package seda.consoleapp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.UUID;

/**
 * Created by liubingfeng on 7/10/2017.
 */

public class BluetoothConnectionAsync extends AsyncTask<Void, Void, Void> {

    private static final String TAG = "BluetoothConnectionAsync";

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothSocket mmSocket;
    private BluetoothDevice mmDevice;
    private BufferedWriter out;
    private BufferedReader in;
    private BluetoothDevice device;
    private AppCompatActivity activity;
    private Thread sendThread;
    private Thread readThread;

    String aString = "OUR_SECRET";
    UUID resultUUID = UUID.nameUUIDFromBytes(aString.getBytes());

    BluetoothConnectionAsync(AppCompatActivity activity, BluetoothAdapter bluetoothAdapter, BluetoothDevice device) {
        this.activity = activity;
        this.bluetoothAdapter = bluetoothAdapter;
        this.device = device;

    }


    @Override
    protected Void doInBackground(Void... voids) {

        try {
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

    protected void sendData(String sendString) {


    }


}
