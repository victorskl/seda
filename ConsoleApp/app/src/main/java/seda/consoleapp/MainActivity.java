package seda.consoleapp;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothDevice bluetoothDevice;
    private HashMap<String, BluetoothDevice> bluetoothDeviceHashMap = new HashMap<String, BluetoothDevice>();
    private String bluetoothServerDeviceName = "SmartisanBing";
    private AsyncTask<Void, Void, Void> startBluetoothConnection;
    
    public static final String TAG = "SEDA";

    // Create a BroadcastReceiver for ACTION_FOUND.
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                Log.wtf(TAG, " BluetoothDevice.ACTION_FOUND.equals(action) -> " + BluetoothDevice.ACTION_FOUND.equals(action));

                bluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                Log.wtf(TAG, " bluetoothDevice " + bluetoothDevice);

                final String deviceName = bluetoothDevice.getName();
                Log.wtf(TAG, " deviceName " + deviceName);

                bluetoothDeviceHashMap.put(deviceName, bluetoothDevice);
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Toast.makeText(getApplicationContext(), "Found -> " + deviceName, Toast.LENGTH_LONG).show();
                    }
                });


//              found server device, start connecting to server, assume once found server is already up
                if (deviceName != null && deviceName.equalsIgnoreCase(bluetoothServerDeviceName))
                {
                    Log.d(TAG, "start bluetooth connection");
                    startBluetoothConnection = new BluetoothConnectionAsync((AppCompatActivity) context, bluetoothAdapter, bluetoothDeviceHashMap.get(bluetoothServerDeviceName));
                    startBluetoothConnection.execute();

                }

                String deviceHardwareAddress = bluetoothDevice.getAddress(); // MAC address
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        Bluetooth setting up
//        Permission requesting code
        int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 2;

//        Requsting COARSE_LOCATION which is needed for bluetooth
//        Need to request it on run time
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);

//      bluetooth https://www.tutorialspoint.com/android/android_bluetooth.htm

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

//      these two line ask request for blue tooth
        Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        int turnOnBluetoothRequestCode = 0;
        startActivityForResult(turnOn, turnOnBluetoothRequestCode);


//         make this device discoverable in 300s
        Intent discoverableIntent =
                new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);

        startActivityForResult(discoverableIntent, 1);

        // Register for broadcasts when a device is discovered.
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter);

    }

//    All the request of requsting system will come to this callback

    //    listen for registered message
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        // means request bluetooth successfully
        if (requestCode == 0)
        {
            if (resultCode == RESULT_OK)
            {
                Log.wtf(TAG, "request bluetooth successfully");
            }
            else
            {
                Log.wtf(TAG, "request bluetooth failed");
            }
        }

        if (requestCode == 1)
        {

            //if dicoverable failed it will return cancel
            //seemed that you need to make sure bluetooth is opened then start discoverying other devices
            //otherwise, the dicovery process wont start.
            if (resultCode != RESULT_CANCELED)
            {
                if (bluetoothAdapter.isDiscovering()) {
                    bluetoothAdapter.cancelDiscovery();
                }

                bluetoothAdapter.startDiscovery();

                Log.wtf(TAG, "dic request bluetooth successfully -> result code -> " + resultCode);
            }
            else
            {
                Log.wtf(TAG, "dic request bluetooth failed -> result code -> " + requestCode);
            }

        }

    }
}
