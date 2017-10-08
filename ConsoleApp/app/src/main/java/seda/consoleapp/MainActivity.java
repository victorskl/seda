/**
 *
 * MainActivity is used to capture frames from the camera and processing the real time frame
 * to perform car detection, lane detection.
 * The gyroscope sensor data is also collected in here to see if the driver make the right action
 * or not.
 *
 * The data processing ideas with OpenCV is inspired with following online resources:
 * https://github.com/opencv/opencv/tree/master/samples/android
 *
 * We also have a Github repository for Android OpenCV tute in following link:
 * https://github.com/victorskl/android-opencv-tute
 *
 * The car training xml files are from https://github.com/ramitix/Car_lane_sign_detection
 * @author  San Kho Lin (829463), Bingfeng Liu (639187), Yixin Chen(522819)
 * @version 1.0
 * @since   2017-09-15
 */
package seda.consoleapp;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
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
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Set;

import static org.opencv.core.Core.FONT_HERSHEY_TRIPLEX;

public class MainActivity extends Activity implements CvCameraViewListener2 {

//  tmp_count is used to record the number of actions for different sample data
//  For example CAR_DISTANCE_NEG_CNT (number of time driver's car is too close to the front car)
//  is recorded temporarily in temp_count
    private int tmp_count = 0;

    private static final String TAG = "MainActivity";
//  The request code for enabling bluetooth
    private static final int REQUEST_ENABLE_BT = 1;
//  The permission request code for enabling coarse location
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 2;
//  Used to control device bluetooth function such as start discovering
    private BluetoothAdapter bluetoothAdapter;
//  The remote bluetooth device
    private BluetoothDevice bluetoothDevice;

    private HashMap<String, BluetoothDevice> bluetoothDeviceHashMap = new HashMap<>();
//    private String bluetoothServerDeviceName = "HUAWEI BTV";
//  Bluetooth server device's name. Must know the name in advance otherwise hard to know which
//  device is the server
    private String bluetoothServerDeviceName = "SmartisanBing";

//  Async task instance to start connection with server
    private BluetoothConnectionAsync startBluetoothConnection;

//  The video view mode, for example it is in car detection mode or not.
    private ViewMode mViewMode = ViewMode.RGBA;

//  The frame matrix which are needed for OpenCV to process
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

    private File mCascadeFile;
    private CascadeClassifier mJavaDetector;

//  Relative size of the car
    private float mRelativeFaceSize = 0.2f;
    private int mAbsoluteFaceSize = 0;

    private static final Scalar FACE_RECT_COLOR = new Scalar(0, 255, 0, 255);

//  These three times are used to know the last advice played for event like car is too close
    private long lastCarTooCloseAdvicePlayTime = System.currentTimeMillis();
    private long prevLaneChangeTime = System.currentTimeMillis();
    private long prevHeadCheckTime = System.currentTimeMillis();

    private static final int LANE_DETECTION_RGB = 1;
    private static final int LANE_DETECTION_CANNY = 2;
    private SensorManager sensorManager;
    private Sensor gyroscopeSensor;
    private SensorEventListener gyroscopeEventLisetner;



//  Callback used to show the view only when the OpenCV is loaded
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

    //The receive callback executed when found the discoverable bluetooth devices.
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

                    startBluetoothConnection = new BluetoothConnectionAsync((Activity) context,
                            bluetoothAdapter, bluetoothDeviceHashMap.get(bluetoothServerDeviceName));

                    startBluetoothConnection.execute();

                }

                // TODO to handle specific SEDA Bluetooth Server
                else {
                    if (deviceName != null) {
                        Log.wtf(TAG, "onReceive: found but not SEDA Bluetooth Server: " + deviceName);
                    }
                    Log.wtf(TAG, "onReceive: cancel Bluetooth Server discovery as SEDA Server not found");
                    bluetoothAdapter.cancelDiscovery();
                }

                String deviceHardwareAddress = bluetoothDevice.getAddress(); // MAC address
            }
        }
    };

    /**
     * Used to init MainActivity when it is created
     * @param savedInstanceState Unused.
     * @return void
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "called onCreate");
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_main);

        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.main_activity_surface_view);
        mOpenCvCameraView.setVisibility(CameraBridgeViewBase.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);

//      Setting up bluetooth like turning it on.
        initBluetooth();

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
    }

    /**
     * This is the method used to set up bluetooth connection.
     * For example turning bluetooth on and ask necessary permission for using bluetooth.
     * @return void
     */

    private void initBluetooth() {

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();


//      If bluetooth is not on
        if (!bluetoothAdapter.isEnabled()) {
        Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//            need this to discover other device, simply sending intent does not mean it is ready
//        int turnOnBluetoothRequestCode = 1;
//        startActivityForResult(turnOn, turnOnBluetoothRequestCode);
            startActivityForResult(turnOn, REQUEST_ENABLE_BT);
        } else {
//          If bluetooth is already on, select server from the paired device
            performBluetoothPairedOrDiscover();
        }

//      Register for broadcasts when a device is discovered.
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter);
    }

    /**
     * This method is finding the server device from the paired bluetooth device
     * @return BluetoothDevice the sever devices
     */
    private BluetoothDevice findPairedDevice() {
        BluetoothDevice bluetoothDevice = null;
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            // There are paired devices. Get the name and address of each paired device.

            for (BluetoothDevice device : pairedDevices) {
                final String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
                if (deviceName.equalsIgnoreCase(bluetoothServerDeviceName)) {
                    bluetoothDevice = device;
                }
            }
        }
        return bluetoothDevice;
    }

    /**
     * This method is finding the server device from the paired bluetooth device
     * and start the connection async instance.
     * @return BluetoothDevice the sever devices
     */
    private void performBluetoothPairedOrDiscover() {
        final BluetoothDevice bluetoothDevice = findPairedDevice();
        if (bluetoothDevice != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "Found Paired -> "
                            + bluetoothDevice.getName(), Toast.LENGTH_LONG).show();
                }
            });

            Log.wtf(TAG, " deviceName " + bluetoothDevice.getName());
            bluetoothDeviceHashMap.put(bluetoothDevice.getName(), bluetoothDevice);

            startBluetoothConnection = new BluetoothConnectionAsync(this,
                    bluetoothAdapter, bluetoothDeviceHashMap.get(bluetoothServerDeviceName));
            startBluetoothConnection.execute();
        }

        else {

            if (bluetoothAdapter.isDiscovering()) {
                bluetoothAdapter.cancelDiscovery();
            }

            Toast.makeText(getApplicationContext(), "start discovering", Toast.LENGTH_LONG).show();
            bluetoothAdapter.startDiscovery();
        }
    }


    /**
     * This method is the call back function for the result of the system request like enabling
     * bluetooth
     * @return void
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // means request bluetooth successfully

//        if (requestCode == 0) {
//            if (resultCode == RESULT_OK) {
//                Log.wtf(TAG, "request bluetooth successfully");
//            } else {
//                Log.wtf(TAG, "request bluetooth failed");
//            }
//        }

        if (requestCode == REQUEST_ENABLE_BT) {


            //if dicoverable failed it will return cancel
            //seemed that you need to make sure bluetooth is opened then start discoverying other devices
            //otherwise, the dicovery process wont start.
            if (resultCode == RESULT_OK) {

                performBluetoothPairedOrDiscover();

                Log.wtf(TAG, "dic request bluetooth successfully -> result code -> " + resultCode);
            } else {
                Log.wtf(TAG, "dic request bluetooth failed -> result code -> " + requestCode);
            }
        }
    }


    /**
     * This method is setting up the options on the top left menus.
     * @return boolean
     */
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

    /**
     * This method is performing actions for activity pause that disabling the camera view.
     * @return void
     */
    @Override
    public void onPause() {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
        sensorManager.unregisterListener(gyroscopeEventLisetner);
    }

    /**
     * This method is use to reload OpenCV
     * @return void
     */
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
        sensorManager.registerListener(gyroscopeEventLisetner, gyroscopeSensor, SensorManager.SENSOR_DELAY_FASTEST);
    }

    /**
     * When destory should disable camera view and unregister the receiver event.
     * @return void
     */
    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null) {
            mOpenCvCameraView.disableView();
        }

        // Don't forget to unregister the ACTION_FOUND receiver.
        unregisterReceiver(mReceiver);
    }

    // OpenCV
    /**
     * This method is initializing the frame matrix.
     * @return BluetoothDevice the sever devices
     */
    public void onCameraViewStarted(int width, int height) {
        mRgba = new Mat(height, width, CvType.CV_8UC4);
        mIntermediateMat = new Mat(height, width, CvType.CV_8UC4);
        mGray = new Mat(height, width, CvType.CV_8UC1);
    }

    /**
     * This method is finding the server device from the paired bluetooth device
     * @return BluetoothDevice the sever devices
     */
    public void onCameraViewStopped() {
        mRgba.release();
        mGray.release();
        mIntermediateMat.release();
    }

    /**
     * This method is use to decide how to process the current frame with different view mode e.g.
     * Use this frame to perform Lane detection.
     * @return Mat used to render the camera view.
     */
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
            case LANE_DETECTION:
                mRgba = inputFrame.rgba();
                mGray = inputFrame.gray();
                performLaneDetection(LANE_DETECTION_RGB);
                break;
            case LANE_DETECTION_CANNY:
                mRgba = inputFrame.rgba();
                mGray = inputFrame.gray();
                performLaneDetection(LANE_DETECTION_CANNY);
                break;
            case CAR_DETECTION:
                mRgba = inputFrame.rgba();
                mGray = inputFrame.gray();
                performCarDetection();
                break;
            case HEAD_CHECK:
                mRgba = inputFrame.rgba();
                break;
        }

        return mRgba;
    }

    /**
     * This method is set view mode depending on which options is touched by the user from the top
     * right menu button.
     * @param item which is the menu option.
     * @return boolean
     */
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.i(TAG, "called onOptionsItemSelected; selected item: " + item);
        sensorManager.unregisterListener(gyroscopeEventLisetner);

//      Stop each sample collecting, and send it to server for cloud processing
        stopRecording();

        if (item == mItemPreviewRGBA) {
            mViewMode = ViewMode.RGBA;
        }

        else if (item == mItemPreviewCanny) {
            mViewMode = ViewMode.CANNY;
        }

        else if (item == mItemHeadCheck) {
            mViewMode = ViewMode.HEAD_CHECK;

            startRecording(SampleType.HEAD_CHECK_POS_CNT);

            initHeadCheck();
            sensorManager.registerListener(gyroscopeEventLisetner, gyroscopeSensor, SensorManager.SENSOR_DELAY_FASTEST);
        }

        else if (item == mItemLaneDetection) {

            startRecording(SampleType.HEAD_CHECK_NEG_CNT);

            mViewMode = ViewMode.LANE_DETECTION;
        }

        else if (item == mItemLaneDetectionCanny) {

            startRecording(SampleType.HEAD_CHECK_NEG_CNT);

            mViewMode = ViewMode.LANE_DETECTION_CANNY;
        }

        else if (item == mItemCarDetection) {

            startRecording(SampleType.CAR_DISTANCE_NEG_CNT);

            mViewMode = ViewMode.CAR_DETECTION;
        }

        return true;
    }

    Sample sample;


    /**
     * This method is used to refresh the sample object, to recored data from corresponding view
     * mode.
     * @param sampleType is the type of this sample e.g. for head check or car too close warning.
     * @return void
     */
    private void startRecording(SampleType sampleType) {
        sample = new Sample();
        sample.setStartTime(Calendar.getInstance().getTime());
        sample.setSampleType(sampleType);
    }

    /**
     * This method is used to send the collected sample from last view mode to Base app for cloud
     * processing
     * @return void
     */
    private void stopRecording() {
        if (sample == null) return;

        sample.setCount(tmp_count);
        sample.setEndTime(Calendar.getInstance().getTime());

        Log.i(TAG, "Sample created and sending to BaseApp: "
                + " st: " + sample.getStartTime()
                + " et: " + sample.getEndTime()
                + " ty: " + sample.getSampleType().toString()
                + " ct: " + sample.getCount());

        startBluetoothConnection.sendData(sample.toJSONObject().toString());

        // reset
        tmp_count = 0;
        sample = null;
    }

    /**
     * This is the main method is used to load the car cascade xml file from resource to
     * currentn app path and get the path string for the CascadeClassifier to create new cascade
     * classifier instance
     * @return void
     */
    private void initCascadeTrainingData() {

        try {

            // load cascade file from application resources
//                        car2 seemed to be more accurated -> does not detect squares
            InputStream is = getResources().openRawResource(R.raw.cars2);
            File cascadeDir = getDir("cascade", Context.MODE_PRIVATE);
            mCascadeFile = new File(cascadeDir, "car.xml");
            FileOutputStream os = new FileOutputStream(mCascadeFile);

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            is.close();
            os.close();

            mJavaDetector = new CascadeClassifier(mCascadeFile.getAbsolutePath());
            if (mJavaDetector.empty()) {
                Log.e(TAG, "Failed to load cascade classifier");
                mJavaDetector = null;
            } else
                Log.i(TAG, "Loaded cascade classifier from " + mCascadeFile.getAbsolutePath());

            cascadeDir.delete();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * This method is used to perform the car detection and the front car distance detection,
     * by using OpenCV to process the frame matrix with Cascade classifier and using the
     * marked car rectangle size to estimate the distance between driver's car and the front car.
     * @return void
     */
    private void performCarDetection() {


        if (mAbsoluteFaceSize == 0) {
            int height = mGray.rows();
            if (Math.round(height * mRelativeFaceSize) > 0) {
                mAbsoluteFaceSize = Math.round(height * mRelativeFaceSize);
            }
        }

//      The marked car rectangles will be stored here
        MatOfRect faces = new MatOfRect();

        if (mJavaDetector != null) {
            mJavaDetector.detectMultiScale(mGray, faces, 1.1, 2, 2, // TODO: objdetect.CV_HAAR_SCALE_IMAGE
                    new Size(mAbsoluteFaceSize, mAbsoluteFaceSize), new Size());
        } else {
            Log.e(TAG, "Detection method is not selected!");
        }


//      could have many detections, so we have multiple rectangles here
        Rect[] facesArray = faces.toArray();

//      Draw detected car rectangles on the camera frame matrix one by one
//      Also calculate each car's distance to our driver's car and also draw distance on camera
//      frame
        for (int i = 0; i < facesArray.length; i++) {
            Imgproc.rectangle(mRgba, facesArray[i].tl(), facesArray[i].br(), FACE_RECT_COLOR, 3);
            int distance = (int) Math.round((0.0397 * 2) / ((facesArray[i].width) * 0.00002));
//      If distance is less than 20m play the advice sound and make the rectangle blue
            if (distance < 20) {
                //            Log.d("rect box", "" facesArray[i].width);
                Imgproc.putText(mRgba, "" + distance + "m" + " " + facesArray[i].x + ", " + facesArray[i].y, new Point(facesArray[i].x, facesArray[i].y),
                        FONT_HERSHEY_TRIPLEX, 5.0, new Scalar(0, 0, 255));


//                if half of the bottom right line of the detecting box is on the right side of the road. Play audio
//                this is used to eliminate the car driving from the opposite direction
//                facesArray[i].br() -> bottom right coner of the detecting box
//                depending on the rule left or right driving.
//                Log.d("sound", "" + (facesArray[i].br().x- facesArray[i].width/2)+ ", " + mRgba.width()/2);
//              Only play the advice sound in 10s gap
                if (System.currentTimeMillis() - lastCarTooCloseAdvicePlayTime > 10000 && (facesArray[i].br().x - facesArray[i].width / 2) >= mRgba.width() / 2) {

                    playCarCloseAudio();
                    lastCarTooCloseAdvicePlayTime = System.currentTimeMillis();
                    tmp_count += 1;
                }
            } else {
                //            Log.d("rect box", "" facesArray[i].width);
                Imgproc.putText(mRgba, "" + distance + "m", new Point(facesArray[i].x, facesArray[i].y),
                        FONT_HERSHEY_TRIPLEX, 5.0, new Scalar(255, 0, 0));
            }


        }
//      Draw a line in the middle of the frame as a reference
        Imgproc.line(mRgba, new Point(mRgba.width() / 2, 0), new Point(mRgba.width() / 2, mRgba.height()), new Scalar(255, 0, 0));
    }


    /**
     * This method is used to highly the lines on the road and base on the line number to detect
     * whether the drivers are changing lanes and give them advice on this action.
     * @param  outputMode is the different way of processing the frame matrix to detect lane change
     * @return void
     */
    private void performLaneDetection(int outputMode) {

        Mat lines = new Mat();

        //procedure: blur -> canny(edges) -> lines -> filter lines -> draw lines
        Imgproc.GaussianBlur(mGray, mGray, new Size(11, 11), 0);
        Imgproc.Canny(mGray, mIntermediateMat, 80, 100);
        Imgproc.HoughLinesP(mIntermediateMat, lines, 1, Math.PI / 180, 40, 30, 100);

        int count = 0;

        //decide output canny or normal
        //If canny, convert the gray scaled mIntermediateMat to mrgba object
        if (outputMode == LANE_DETECTION_CANNY) {
            Imgproc.cvtColor(mIntermediateMat, mRgba, Imgproc.COLOR_GRAY2RGBA, 4);
        }

        //lines.rows() is the number of lines produced by houghlinesP
        for (int i = 0; i < lines.rows(); i++) {

            double[] val = lines.get(i, 0);
            double dx = val[2] - val[0];
            double dy = val[3] - val[1];
            double angle = Math.atan2(dy, dx) * (180 / Math.PI);

//          Filter out the noisy lines, only focus on the road lines.
            if ((angle > 30 && angle < 45) || (angle > -45 && angle < -30)) {
                count++;
                Imgproc.line(mRgba, new Point(val[0], val[1]), new Point(val[2], val[3]), new Scalar(0, 0, 255), 2);
            }
        }

//      if the lines detect on the road is less than 4 and the time gap is 5s, play the advice
//      to driver to do head check.
        if (count < 4 && (System.currentTimeMillis() - prevLaneChangeTime) > 5000) {
            MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.headcheck1);
            mp.start();
            prevLaneChangeTime = System.currentTimeMillis();

            tmp_count++;
        }
    }

    /**
     * This method is used to detect the head movements from the gyroscope sensor in order
     * to tell whether the driver made head check or not.
     * @return void
     */
    private void initHeadCheck() {
        if (gyroscopeEventLisetner != null) return;

        gyroscopeEventLisetner = new SensorEventListener() {

            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {

                if ((sensorEvent.values[0] > 0.8f) || (sensorEvent.values[0] < -0.8f)) {
                    if ((System.currentTimeMillis() - prevHeadCheckTime) > 8000) {
                        MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.headcheck2);
                        mp.start();
                        prevHeadCheckTime = System.currentTimeMillis();

                        tmp_count++;
                    }
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {
                //
            }
        };

    }

    /**
     * This method is used to play the advice audio file. It is inspired from
     * https://stackoverflow.com/questions/7291731/how-to-play-audio-file-in-android
     * @return void
     */
    public void playCarCloseAudio() {
        //set up MediaPlayer
        MediaPlayer mp;
        try {
            mp = MediaPlayer.create(getApplicationContext(), R.raw.close_car);
            mp.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}