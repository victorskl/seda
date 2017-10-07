package seda.consoleapp;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Toast;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements CvCameraViewListener2 {

    private static final String TAG = "MainActivity";

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothDevice bluetoothDevice;
    private HashMap<String, BluetoothDevice> bluetoothDeviceHashMap = new HashMap<>();
    private String bluetoothServerDeviceName = "SmartisanBing";
    private AsyncTask<Void, Void, Void> startBluetoothConnection;

    private ViewMode mViewMode = ViewMode.RGBA;
    private Mat mRgba;
    private Mat mIntermediateMat;
    private Mat mGray;

    private MenuItem mItemPreviewRGBA;
    private MenuItem mItemPreviewCanny;
    private MenuItem mItemHeadCheck;
    private MenuItem mItemLaneDetection;
    private MenuItem mItemLaneDetectionCanny;
    private MenuItem mItemCarDetection;

    private CameraBridgeViewBase mOpenCvCameraView;

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    Log.i(TAG, "OpenCV loaded successfully");

                    initCascadeTrainingData();
                    mOpenCvCameraView.enableView();
                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };

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
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Found -> " + deviceName, Toast.LENGTH_LONG).show();
                    }
                });


//              found server device, start connecting to server, assume once found server is already up
                if (deviceName != null && deviceName.equalsIgnoreCase(bluetoothServerDeviceName)) {
                    Log.d(TAG, "start bluetooth connection");
                    startBluetoothConnection = new BluetoothConnectionAsync((AppCompatActivity) context, bluetoothAdapter, bluetoothDeviceHashMap.get(bluetoothServerDeviceName));
                    startBluetoothConnection.execute();

                }

                String deviceHardwareAddress = bluetoothDevice.getAddress(); // MAC address
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "called onCreate");
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_main);

        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.main_activity_surface_view);
        mOpenCvCameraView.setVisibility(CameraBridgeViewBase.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);


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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.i(TAG, "called onCreateOptionsMenu");
        mItemPreviewRGBA = menu.add(ViewMode.RGBA.getLongName());
        mItemPreviewCanny = menu.add(ViewMode.CANNY.getLongName());
        mItemHeadCheck = menu.add(ViewMode.HEAD_CHECK.getLongName());
        mItemLaneDetection = menu.add(ViewMode.LANE_DETECTION.getLongName());
        mItemLaneDetectionCanny = menu.add(ViewMode.LANE_DETECTION_CANNY.getLongName());
        mItemCarDetection = menu.add(ViewMode.CAR_DETECTION.getLongName());
        return true;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_3_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    // OpenCV

    public void onCameraViewStarted(int width, int height) {
        mRgba = new Mat(height, width, CvType.CV_8UC4);
        mIntermediateMat = new Mat(height, width, CvType.CV_8UC4);
        mGray = new Mat(height, width, CvType.CV_8UC1);
    }

    public void onCameraViewStopped() {
        mRgba.release();
        mGray.release();
        mIntermediateMat.release();
    }

    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
        //final ViewMode viewMode = mViewMode;
        switch (mViewMode) {
            case RGBA:
                // input frame has RBGA format
                mRgba = inputFrame.rgba();
                break;
            case CANNY:
                // input frame has gray scale format
                mRgba = inputFrame.rgba();
                Imgproc.Canny(inputFrame.gray(), mIntermediateMat, 80, 100);
                Imgproc.cvtColor(mIntermediateMat, mRgba, Imgproc.COLOR_GRAY2RGBA, 4);
                break;
            case HEAD_CHECK:
                performHeadCheck();
                break;
            case LANE_DETECTION:
                performLaneDetection();
                break;
            case LANE_DETECTION_CANNY:
                performLaneDetectionCanny();
                break;
            case CAR_DETECTION:
                performCarDetection();
                break;
        }

        return mRgba;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        Log.i(TAG, "called onOptionsItemSelected; selected item: " + item);

        if (item == mItemPreviewRGBA) {
            mViewMode = ViewMode.RGBA;
        } else if (item == mItemPreviewCanny) {
            mViewMode = ViewMode.CANNY;
        } else if (item == mItemHeadCheck) {
            mViewMode = ViewMode.HEAD_CHECK;
        } else if (item == mItemLaneDetection) {
            mViewMode = ViewMode.LANE_DETECTION;
        } else if (item == mItemLaneDetectionCanny) {
            mViewMode = ViewMode.LANE_DETECTION_CANNY;
        } else if (item == mItemCarDetection) {
            mViewMode = ViewMode.CAR_DETECTION;
        }

        return true;
    }

    // initialize Haar Cascade
    private void initCascadeTrainingData() {

    }


    // Core routines

    private void performCarDetection() {

    }

    private void performLaneDetectionCanny() {

    }

    private void performLaneDetection() {

    }

    private void performHeadCheck() {

    }


    //    All the request of requsting system will come to this callback
    //    listen for registered message
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // means request bluetooth successfully
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                Log.wtf(TAG, "request bluetooth successfully");
            } else {
                Log.wtf(TAG, "request bluetooth failed");
            }
        }

        if (requestCode == 1) {

            //if dicoverable failed it will return cancel
            //seemed that you need to make sure bluetooth is opened then start discoverying other devices
            //otherwise, the dicovery process wont start.
            if (resultCode != RESULT_CANCELED) {
                if (bluetoothAdapter.isDiscovering()) {
                    bluetoothAdapter.cancelDiscovery();
                }

                bluetoothAdapter.startDiscovery();

                Log.wtf(TAG, "dic request bluetooth successfully -> result code -> " + resultCode);
            } else {
                Log.wtf(TAG, "dic request bluetooth failed -> result code -> " + requestCode);
            }
        }
    }
}