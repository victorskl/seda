package seda.baseapp.BluetoothWorker;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.UUID;

/**
 * Created by liubingfeng on 8/10/2017.
 */

public class BluetoothReadRunnable implements Runnable
{
    private static final String TAG  = "BluetoothReadRunnable";
    private BufferedReader in;
    private AppCompatActivity activity;




    BluetoothReadRunnable(BufferedReader in, AppCompatActivity activity)
    {
        this.in = in;
        this.activity = activity;
    }


    @Override
    public void run()
    {
        String msg = null;
        while (true)
        {
            try
            {
                msg = in.readLine();
                final String finalMsg= msg;
                Log.d(TAG, "Msg received from client -> " + msg);
                activity.runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Toast.makeText(activity.getApplicationContext(), "From Client -> " + finalMsg, Toast.LENGTH_LONG).show();
                    }
                });
            } catch (Exception e)
            {
                e.printStackTrace();
                break;
            }


        }

    }
}
