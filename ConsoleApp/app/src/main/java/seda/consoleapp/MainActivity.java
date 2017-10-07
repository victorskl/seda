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
import java.util.HashMap;

import static org.opencv.core.Core.FONT_HERSHEY_TRIPLEX;

//The car detection code is inspired from openCV face detection sample code
//https://github.com/opencv/opencv/tree/master/samples/android/face-detection

public class MainActivity extends Activity implements CvCameraViewListener2 {

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

    private File mCascadeFile;
    private CascadeClassifier mJavaDetector;
    private float mRelativeFaceSize = 0.2f;
    private int mAbsoluteFaceSize = 0;
    private static final Scalar FACE_RECT_COLOR = new Scalar(0, 255, 0, 255);
    private long lastCarTooCloseAdvicePlayTime = System.currentTimeMillis();
    private int carTooCloseAdviceCount = 0;

    private long prevLaneChangeTime = System.currentTimeMillis();
    private long prevHeadCheckTime = System.currentTimeMillis();

    private static final int LANE_DETECTION_RGB = 1;
    private static final int LANE_DETECTION_CANNY = 2;
    private SensorManager sensorManager;
    private Sensor gyroscopeSensor;
    private SensorEventListener gyroscopeEventLisetner;


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

//     Create a BroadcastReceiver for ACTION_FOUND.
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

        //monitoring headcheck via gyroscope
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        gyroscopeSensor =sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        performHeadCheck();
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
        sensorManager.unregisterListener(gyroscopeEventLisetner);
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
        sensorManager.registerListener(gyroscopeEventLisetner, gyroscopeSensor,SensorManager.SENSOR_DELAY_FASTEST);
    }

    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
        {
            mOpenCvCameraView.disableView();
        }

        // Don't forget to unregister the ACTION_FOUND receiver.
        unregisterReceiver(mReceiver);
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

        try
        {

            // load cascade file from application resources
//                        car2 seemed to be more accurated -> does not detect squares
            InputStream is = getResources().openRawResource(R.raw.cars2);
            File cascadeDir = getDir("cascade", Context.MODE_PRIVATE);
            mCascadeFile = new File(cascadeDir, "car.xml");
            FileOutputStream os = new FileOutputStream(mCascadeFile);

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1)
            {
                os.write(buffer, 0, bytesRead);
            }
            is.close();
            os.close();

            mJavaDetector = new CascadeClassifier(mCascadeFile.getAbsolutePath());
            if (mJavaDetector.empty())
            {
                Log.e(TAG, "Failed to load cascade classifier");
                mJavaDetector = null;
            } else
                Log.i(TAG, "Loaded cascade classifier from " + mCascadeFile.getAbsolutePath());

            cascadeDir.delete();
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

    }


    // Core routines

    private void performCarDetection() {


        if (mAbsoluteFaceSize == 0) {
            int height = mGray.rows();
            if (Math.round(height * mRelativeFaceSize) > 0) {
                mAbsoluteFaceSize = Math.round(height * mRelativeFaceSize);
            }
        }

        MatOfRect faces = new MatOfRect();


        if (mJavaDetector != null)
        {
            mJavaDetector.detectMultiScale(mGray, faces, 1.1, 2, 2, // TODO: objdetect.CV_HAAR_SCALE_IMAGE
                    new Size(mAbsoluteFaceSize, mAbsoluteFaceSize), new Size());
        }
        else
        {
            Log.e(TAG, "Detection method is not selected!");
        }


//        could have many detections
        Rect[] facesArray = faces.toArray();


        for (int i = 0; i < facesArray.length; i++)
        {
            Imgproc.rectangle(mRgba, facesArray[i].tl(), facesArray[i].br(), FACE_RECT_COLOR, 3);
            int distance = (int) Math.round((0.0397*2)/((facesArray[i].width)*0.00002));

            if (distance < 20)
            {
                //            Log.d("rect box", "" facesArray[i].width);
                Imgproc.putText(mRgba, ""+ distance +"m" + " " + facesArray[i].x + ", " + facesArray[i].y, new Point(facesArray[i].x,facesArray[i].y),
                        FONT_HERSHEY_TRIPLEX, 5.0 ,new Scalar(0,0,255));


//                if half of the bottom right line of the detecting box is on the right side of the road. Play audio
//                this is used to eliminate the car driving from the opposite direction
//                facesArray[i].br() -> bottom right coner of the detecting box
//                depending on the rule left or right driving.
//                Log.d("sound", "" + (facesArray[i].br().x- facesArray[i].width/2)+ ", " + mRgba.width()/2);
                if(System.currentTimeMillis() - lastCarTooCloseAdvicePlayTime > 10000 && (facesArray[i].br().x- facesArray[i].width/2) >= mRgba.width()/2)
                {

                    playCarCloseAudio();
                    lastCarTooCloseAdvicePlayTime = System.currentTimeMillis();
                    carTooCloseAdviceCount += 1;
                }
            }
            else
            {
                //            Log.d("rect box", "" facesArray[i].width);
                Imgproc.putText(mRgba, ""+ distance +"m", new Point(facesArray[i].x,facesArray[i].y),
                        FONT_HERSHEY_TRIPLEX, 5.0 ,new  Scalar(255,0,0));
            }


        }
        Imgproc.line(mRgba, new Point(mRgba.width()/2, 0), new Point(mRgba.width()/2, mRgba.height()), new Scalar(255,0,0));
    }

    private void performLaneDetection(int outputMode) {
        Mat lines = new Mat();

        //procedure: blur -> canny(edges) -> lines -> filter lines -> draw lines
        Imgproc.GaussianBlur(mGray, mGray, new Size(11,11),0);
        Imgproc.Canny(mGray, mIntermediateMat, 80, 100);
        Imgproc.HoughLinesP(mIntermediateMat, lines, 1, Math.PI/180, 40, 30, 100);
        int count = 0;

        //decide output canny or normal
        //If canny, convert the gray scaled mIntermediateMat to mrgba object
        if(outputMode == LANE_DETECTION_CANNY) {
            Imgproc.cvtColor(mIntermediateMat, mRgba, Imgproc.COLOR_GRAY2RGBA, 4);
        }


        //lines.rows() is the number of lines produced by houghlinesP
        for (int i = 0; i < lines.rows(); i++) {

            double[] val = lines.get(i, 0);
            double dx = val[2] - val[0];
            double dy = val[3] - val[1];
            double angle = Math.atan2(dy, dx)*(180/Math.PI);

            if((angle > 30 && angle < 45) || (angle > -45 && angle < -30)) {
                count++;
                Imgproc.line(mRgba, new Point(val[0], val[1]), new Point(val[2], val[3]), new Scalar(0, 0, 255), 2);
            }
        }

        if (count < 4 && (System.currentTimeMillis() - prevLaneChangeTime) > 5000)
        {

            MediaPlayer mp=MediaPlayer.create(getApplicationContext(),R.raw.headcheck1);// the song is a filename which i have pasted inside a folder **raw** created under the **res** folder.//
            mp.start();

            prevLaneChangeTime = System.currentTimeMillis();

        }
    }

    private void performHeadCheck() {
        gyroscopeEventLisetner = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {

                if ((sensorEvent.values[0]>0.8f) || (sensorEvent.values[0]<-0.8f)) {
                    if ((System.currentTimeMillis() - prevHeadCheckTime) > 8000)
                    {

                        MediaPlayer mp=MediaPlayer.create(getApplicationContext(),R.raw.headcheck2);// the song is a filename which i have pasted inside a folder **raw** created under the **res** folder.//
                        mp.start();

                        prevHeadCheckTime = System.currentTimeMillis();

                    }

                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        };

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

//  This playing audio code is inspired from
//
// https://stackoverflow.com/questions/7291731/how-to-play-audio-file-in-android

    public void playCarCloseAudio(){
        //set up MediaPlayer
        MediaPlayer mp;

        try {

            mp=MediaPlayer.create(getApplicationContext(),R.raw.close_car);
            mp.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}