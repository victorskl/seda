package seda.baseapp.model;

import android.content.Context;

import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.http.OkHttpClientFactory;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;
import com.squareup.okhttp.OkHttpClient;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import seda.baseapp.todo.ToDoItem;

import static com.microsoft.windowsazure.mobileservices.table.query.QueryOperations.val;

public class SampleDao {

    private static final String ENDPOINT_URL = "https://victortodo.azurewebsites.net";
    public MobileServiceClient mClient;
    private MobileServiceTable<ToDoItem> mToDoTable;


    public SampleDao(Context activity) {
        try {
            // Create the Mobile Service Client instance, using the provided

            // Mobile Service URL and key
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

            // Get the Mobile Service Table instance to use

            mToDoTable = mClient.getTable(ToDoItem.class);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void checkItemInTable(ToDoItem item) throws ExecutionException, InterruptedException
    {
        mToDoTable.update(item).get();
    }

    public ToDoItem addItemInTable(ToDoItem item) throws ExecutionException, InterruptedException
    {
        ToDoItem entity = mToDoTable.insert(item).get();
        return entity;
    }

    private List<ToDoItem> refreshItemsFromMobileServiceTable() throws ExecutionException, InterruptedException {
        return mToDoTable.where().field("complete").
                eq(val(false)).execute().get();
    }


}
