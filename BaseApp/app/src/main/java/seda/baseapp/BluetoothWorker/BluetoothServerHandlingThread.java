package seda.baseapp.BluetoothWorker;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

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

import seda.baseapp.model.SampleDao;

/**
 * Created by liubingfeng on 8/10/2017.
 */

public class BluetoothServerHandlingThread extends Thread
{
//    since bluetooth does not allow forever discoverable, so this server will remind robust
//    for accepting client's connection as along as it is discoverable

    private  static final String TAG = "BluetoothServerHandling";
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

    private boolean isInOutThreadAlive = true;

    private SampleDao sampleDao;


    public BluetoothServerHandlingThread(BluetoothAdapter bluetoothAdapter, AppCompatActivity activity, SampleDao sampleDao)
    {
        this.sampleDao = sampleDao;
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

        Future<?> f;
        while (true)
        {

            try
            {
                Log.wtf(TAG, "Accepting client");
                clientSocket = mmServerSocket.accept();
                isInOutThreadAlive = true;
                Log.wtf(TAG, "Accepted one client");

            }
            catch (IOException e)
            {

                Log.wtf(TAG, "Socket's accept() method failed", e);
                break;
            }

            if (clientSocket != null)
            {
                // A connection was accepted. Perform work associated with
                // the connection in a separate thread.
//                    manageMyConnectedSocket(socket);
                try
                {
//                        echo server
                    out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
                    in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                } catch (Exception e)
                {
                    e.printStackTrace();
                }
                curReadRunnable = new BluetoothReadRunnable(in, activity, sampleDao);
                curWriteRunnable = new BluetoothWriteRunnable(out, activity);

//                  Inspired from: https://stackoverflow.com/questions/33845405/how-to-check-if-all-tasks-running-on-executorservice-are-completed

                f = fixedThreadPool.submit(curReadRunnable);

                taskFutures.add(f);

                f = fixedThreadPool.submit(curWriteRunnable);

                taskFutures.add(f);

                while (isInOutThreadAlive)
                {
                    Log.wtf(TAG, "Checking client alive");
                    for (Future<?> future : taskFutures)
                    {
                        if (future.isDone() || future.isCancelled())
                        {
                            Log.wtf(TAG, "Future done ~= Either read or write runnable failed, start new server");
                            try
                            {
                                cleanUp();
                                mmServerSocket = bluetoothAdapter.listenUsingRfcommWithServiceRecord(name, resultUUID);
                                isInOutThreadAlive = false;
                                break;

                            } catch (IOException e)
                            {
                                Log.wtf(TAG, "Fail to clean up");
                                e.printStackTrace();
                            }

                            break;
                        }
                    }
                    try
                    {
                        Thread.sleep(1000);
                    } catch (Exception e)
                    {
                        Log.wtf(TAG, "Server handling thread sleep failed");
                    }
                }

            }

        }
    }

    private void cleanUp() throws IOException
    {
        mmServerSocket.close();
        taskFutures.clear();
        in.close();
        out.close();
        in = null;
        out = null;
        curReadRunnable = null;
        curWriteRunnable.addSendMessageQueue("close");
        curWriteRunnable = null;

    }
}
