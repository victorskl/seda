/**
 *
 * BluetoothWriteRunnable is used to manage the data writing to the client socket
 * @author  San Kho Lin (829463), Bingfeng Liu (639187), Yixin Chen(522819)
 * @version 1.0
 * @since   2017-09-15
 */
package seda.baseapp.BluetoothWorker;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.LinkedBlockingDeque;

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

    /**
     * This method is responsible of adding ready to send data to the blocking queue
     * @param msg is the JSON data string (Sample) from the server
     * @return void
     */
    public void addSendMessageQueue(String msg)
    {
        sendMsgDeque.add(msg + "\n");
    }

    /**
     * This run method will keep taking message for sent from the blocking queue and send it to
     * client side.
     * @return void
     */
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

    /**
     * This method is used to set the runGuard
     * @param runGuard is used to controling the loop of the run method
     * @return void
     */
    public void setRunGuard(boolean runGuard)
    {
        this.runGuard = runGuard;
    }
}
