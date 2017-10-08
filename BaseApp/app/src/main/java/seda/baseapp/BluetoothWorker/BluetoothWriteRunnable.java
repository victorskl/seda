package seda.baseapp.BluetoothWorker;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Created by liubingfeng on 8/10/2017.
 */

public class BluetoothWriteRunnable implements Runnable
{
    private static final String TAG = "BluetoothWriteRunnable";
    private LinkedBlockingDeque<String> sendMsgDeque = new LinkedBlockingDeque<>();
    private BufferedWriter out;
    private AppCompatActivity activity;
    private  boolean runGuard = true;


    BluetoothWriteRunnable(BufferedWriter out, AppCompatActivity activity)
    {
        this.out = out;
        this.activity = activity;
        addSendMessageQueue("Hello From Server");
    }

    public void addSendMessageQueue(String msg)
    {
        sendMsgDeque.add(msg + "\n");
    }

    @Override
    public void run()
    {
        String msg = null;
        while (runGuard)
        {
            try
            {
                msg = sendMsgDeque.take();
                out.write(msg);
                out.flush();
                Log.d(TAG, "Message sends to client -> " + msg);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                break;
            }

        }

    }

    public void setRunGuard(boolean runGuard)
    {
        this.runGuard = runGuard;
    }
}
