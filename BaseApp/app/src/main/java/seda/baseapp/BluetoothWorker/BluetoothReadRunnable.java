/**
 *
 * BluetoothReadRunnable is used to manage the data reading from client socket
 * @author  San Kho Lin (829463), Bingfeng Liu (639187), Yixin Chen(522819)
 * @version 1.0
 * @since   2017-09-15
 */
package seda.baseapp.BluetoothWorker;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

import seda.baseapp.model.Sample;
import seda.baseapp.model.SampleDao;
import seda.baseapp.model.SampleType;

public class BluetoothReadRunnable implements Runnable {
    private static final String TAG = "BluetoothReadRunnable";
    private BufferedReader in;
    private AppCompatActivity activity;
    private boolean runGuard = true;
    private SampleDao sampleDao;

    BluetoothReadRunnable(BufferedReader in, AppCompatActivity activity, SampleDao sampleDao) {
        this.sampleDao = sampleDao;
        this.in = in;
        this.activity = activity;
    }

    /**
     * This method is responsible of deserializing the JSON string and send the data to the
     * cloud for storing and processing.
     * @param msg is the JSON data string (Sample) from the client
     * @return void
     */
    private void addSampleDataToDB(String msg) {
        try {
            JSONObject jsonObject = new JSONObject(msg);

            Sample sample = new Sample();

            sample.setCount(jsonObject.getInt("count"));
            sample.setEndTime(jsonObject.getLong("endTime"));
            sample.setStartTime(jsonObject.getLong("startTime"));

            if (jsonObject.getString("sampleType")
                    .equalsIgnoreCase(SampleType.HEAD_CHECK_POS_CNT.toString())) {
                sample.setSampleType(1);
            }

            if (jsonObject.getString("sampleType")
                    .equalsIgnoreCase(SampleType.HEAD_CHECK_NEG_CNT.toString())) {
                sample.setSampleType(2);
            }

            if (jsonObject.getString("sampleType")
                    .equalsIgnoreCase(SampleType.CAR_DISTANCE_NEG_CNT.toString())) {
                sample.setSampleType(3);
            }

            sampleDao.addItemInTable(sample);

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    /**
     * This run method listening the data sent from the client.
     * @return void
     */
    @Override
    public void run() {
        String msg = null;
        while (runGuard) {
            try {
                msg = in.readLine();
                final String finalMsg = msg;
                Log.d(TAG, "Msg received from client -> " + msg);
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(activity.getApplicationContext(),
                                "From Client -> " + finalMsg, Toast.LENGTH_LONG).show();
                    }
                });

                addSampleDataToDB(msg);

            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }
    }
}
