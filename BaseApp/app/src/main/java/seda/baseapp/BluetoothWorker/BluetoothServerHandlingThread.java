package seda.baseapp.BluetoothWorker;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by liubingfeng on 8/10/2017.
 */

public class BluetoothServerHandlingThread extends Thread
{

    private  static final String TAG = "BluetoothServerHandlingThread";
    private  String name = "server1";

    private String aString="OUR_SECRET";
    private UUID resultUUID = UUID.nameUUIDFromBytes(aString.getBytes());

    private BluetoothAdapter bluetoothAdapter;

    private BluetoothServerSocket mmServerSocket;
    private AppCompatActivity activity;

    private List<Future<?>> taskFutures = new ArrayList<>();
//    read and write runnable
    private ExecutorService fixedThreadPool = Executors.newFixedThreadPool(2);

    private BluetoothWriteRunnable curWriteRunnable;
    private BluetoothReadRunnable curReadRunnable;

    private  BufferedWriter out;
    private  BufferedReader in;

    private BluetoothSocket clientSocket = null;


    BluetoothServerHandlingThread(BluetoothAdapter bluetoothAdapter, AppCompatActivity activity)
    {
        this.bluetoothAdapter = bluetoothAdapter;
        this.activity = activity;


        try {
            // MY_UUID is the app's UUID string, also used by the client code.
            mmServerSocket = bluetoothAdapter.listenUsingRfcommWithServiceRecord(name, resultUUID);
        } catch (IOException e) {
            Log.wtf(TAG, "Socket's listen() method failed", e);
        }
    }


    @Override
    public void run()
    {

        while (true)
        {

            try {
                clientSocket = mmServerSocket.accept();
            } catch (IOException e) {
                Log.wtf("bingfengappserver", "Socket's accept() method failed", e);
                break;
            }

            if (clientSocket != null) {
                // A connection was accepted. Perform work associated with
                // the connection in a separate thread.
//                    manageMyConnectedSocket(socket);
                try
                {
//                        echo server
                    out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
                    in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    String inputLine = null;
                    while (true)
                    {
                        out.write("" + System.currentTimeMillis() + "\n");
                        out.flush();
                        inputLine = in.readLine();
                        Log.wtf("bingfengappservice", "From Client -> " + inputLine);
//                            displayBluetoothTextView.setText(inputLine);

//                            this code is ugly will refactor it later. just want to show msg on the UI
                        final String finalInputLine = inputLine;
                        activity.runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                Toast.makeText(activity.getApplicationContext(), "From Client -> " + finalInputLine, Toast.LENGTH_LONG).show();
                            }
                        });

                        Thread.sleep(1000);

                    }
                } catch (Exception e)
                {
                    e.printStackTrace();
                }
                finally
                {
                    try
                    {
                        mmServerSocket.close();
                    } catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
                break;
            }


        }



    }




}
