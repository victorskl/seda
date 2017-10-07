package seda.consoleapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

public class MainActivity extends AppCompatActivity implements CvCameraViewListener2 {

    private static final String TAG = "MainActivity";

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


    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "called onCreate");
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_main);

        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.main_activity_surface_view);
        mOpenCvCameraView.setVisibility(CameraBridgeViewBase.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);
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
        }
        else if (item == mItemPreviewCanny) {
            mViewMode = ViewMode.CANNY;
        }
        else if (item == mItemHeadCheck) {
            mViewMode = ViewMode.HEAD_CHECK;
        }
        else if (item == mItemLaneDetection) {
            mViewMode = ViewMode.LANE_DETECTION;
        }
        else if (item == mItemLaneDetectionCanny) {
            mViewMode = ViewMode.LANE_DETECTION_CANNY;
        }
        else if (item == mItemCarDetection) {
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
}