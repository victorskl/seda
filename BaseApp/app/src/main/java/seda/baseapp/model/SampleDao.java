package seda.baseapp.model;

import android.content.Context;

import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.http.OkHttpClientFactory;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;
import com.squareup.okhttp.OkHttpClient;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static com.microsoft.windowsazure.mobileservices.table.query.QueryOperations.val;

public class SampleDao {

    private static final String ENDPOINT_URL = "http://sedabackend.azurewebsites.net";
    public MobileServiceClient mClient;
    private MobileServiceTable<Sample> mSampleTable;

    public SampleDao(Context activity) {
        try {

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

    public void checkItemInTable(Sample item) throws ExecutionException, InterruptedException {
        mSampleTable.update(item).get();
    }

    public Sample addItemInTable(Sample item) throws ExecutionException, InterruptedException {
        Sample entity = mSampleTable.insert(item).get();
        return entity;
    }

    private List<Sample> refreshItemsFromMobileServiceTable() throws ExecutionException, InterruptedException {
        return mSampleTable.where().field("complete").
                eq(val(false)).execute().get();
    }
}
