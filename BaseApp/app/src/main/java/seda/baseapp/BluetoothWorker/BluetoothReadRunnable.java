package seda.baseapp.BluetoothWorker;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

import seda.baseapp.model.SampleDao;
import seda.baseapp.todo.SampleType;
import seda.baseapp.todo.ToDoItem;

/**
 * Created by liubingfeng on 8/10/2017.
 */

public class BluetoothReadRunnable implements Runnable
{
    private static final String TAG  = "BluetoothReadRunnable";
    private BufferedReader in;
    private AppCompatActivity activity;
    private boolean runGuard = true;
    private SampleDao sampleDao;




    BluetoothReadRunnable(BufferedReader in, AppCompatActivity activity, SampleDao sampleDao)
    {
        this.sampleDao = sampleDao;
        this.in = in;
        this.activity = activity;
    }

    private void addSampleDataToDB(String msg)
    {
        try {
            JSONObject jsonObject = new JSONObject(msg);

            ToDoItem toDoItem = new ToDoItem();
            toDoItem.setText("haha");
            toDoItem.setComplete(false);

            toDoItem.setCount(jsonObject.getInt("count"));
            toDoItem.setEndTime(jsonObject.getLong("endTime"));
            toDoItem.setStartTime(jsonObject.getLong("startTime"));

            if (jsonObject.getString("sampleType")
                    .equalsIgnoreCase(SampleType.HEAD_CHECK_POS_CNT.toString())) {
                toDoItem.setSampleType(1);
            }

            if (jsonObject.getString("sampleType")
                    .equalsIgnoreCase(SampleType.HEAD_CHECK_NEG_CNT.toString())) {
                toDoItem.setSampleType(2);
            }

            if (jsonObject.getString("sampleType")
                    .equalsIgnoreCase(SampleType.CAR_DISTANCE_NEG_CNT.toString())) {
                toDoItem.setSampleType(3);
            }

            sampleDao.addItemInTable(toDoItem);


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


    @Override
    public void run()
    {
        String msg = null;
        while (runGuard)
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

                addSampleDataToDB(msg);

            }
            catch (IOException e)
            {
                e.printStackTrace();
                break;
            }


        }

    }
}
