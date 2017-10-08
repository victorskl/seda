/**
 *
 * This class is used to manage the Azure database e.g. making query and retriving data.
 *
 * @author  San Kho Lin (829463), Bingfeng Liu (639187), Yixin Chen(522819)
 * @version 1.0
 * @since   2017-09-15
 */

package seda.baseapp.model;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.http.OkHttpClientFactory;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;
import com.microsoft.windowsazure.mobileservices.table.query.ExecutableQuery;
import com.squareup.okhttp.OkHttpClient;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import seda.baseapp.MainActivity;

import static com.microsoft.windowsazure.mobileservices.table.query.QueryOperations.minute;
import static com.microsoft.windowsazure.mobileservices.table.query.QueryOperations.val;

public class SampleDao {

    public static final String TAG = "SampleDao";

    private static final String ENDPOINT_URL = "http://sedabackend.azurewebsites.net";
    public MobileServiceClient mClient;
    private MobileServiceTable<Sample> mSampleTable;
    private Activity activity;

    public SampleDao(Activity activity) {
        try {
            this.activity = activity;
            mClient = new MobileServiceClient(ENDPOINT_URL, activity);

            // Extend timeout from default of 10s to 20s
            mClient.setAndroidHttpClientFactory(new OkHttpClientFactory() {
                @Override
                public OkHttpClient createOkHttpClient() {
                    OkHttpClient client = new OkHttpClient();
                    client.setReadTimeout(20, TimeUnit.SECONDS);
                    client.setWriteTimeout(20, TimeUnit.SECONDS);
                    return client;
                }
            });

            mSampleTable = mClient.getTable(Sample.class);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * This is used to update the row of Sample table
     * @param item
     * @return void
     */
    public void checkItemInTable(Sample item) throws ExecutionException, InterruptedException {
        mSampleTable.update(item).get();
    }

    /**
     * This is used to add the row of Sample table
     * @param item
     * @return void
     */
    public Sample addItemInTable(Sample item) throws ExecutionException, InterruptedException {
        Sample entity = mSampleTable.insert(item).get();
        return entity;
    }

    /**
     * This is used to retrive the row of Sample table
     * @return List<Sample> is all rows from the Sample Table
     */
    private List<Sample> refreshItemsFromMobileServiceTable() throws ExecutionException, InterruptedException {
        return mSampleTable.select("*").execute().get();
    }

    /**
     * This is used to retrive the row of Sample table and update the PublicProfile view.
     * @return void
     */
    public void refreshItemsFromTable() {

        // Get the items that weren't marked as completed and add them in the
        // adapter

        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {

                try {
                    final List<Sample> results = refreshItemsFromMobileServiceTable();
                    ((MainActivity) activity).sampleConcurrentLinkedDeque.add(results);

                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ((MainActivity) activity).updatePublicProfileFragmentAdapter(results);
                        }
                    });
                } catch (final Exception e) {
                    //createAndShowDialogFromTask(e, "Error");
                    e.printStackTrace();
                }

                return null;
            }
        };

        task.execute();
    }
}
