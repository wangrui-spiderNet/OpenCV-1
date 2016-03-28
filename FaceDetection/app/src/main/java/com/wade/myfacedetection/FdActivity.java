package com.wade.myfacedetection;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static java.lang.Math.round;


public class FdActivity extends Activity  implements CvCameraViewListener2{
    private Activity mainAct;
    private static final String    TAG                     = "FaceDetect";
    private static final Scalar    FACE_RECT_COLOR         = new Scalar(0, 255, 0, 255);
    private static final Scalar    EYE_RECT_COLOR          = new Scalar(255, 0, 0, 255);

    private Mat                    mRgba;
    private Mat                    mGray;
    private File                   mCascadeFile,mEyeFile;
    private CascadeClassifier      mFaceDetector, mEyeDetector;

    private float                  mRelativeFaceSize       = 0.2f;
    private int                    mAbsoluteFaceSize       = 0;


    private CameraBridgeViewBase   mOpenCvCameraView;

    static {
        OpenCVLoader.initDebug();
        System.loadLibrary("opencv_java3");
    }

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status){
            MainActivity caller = new MainActivity();

            int[] rawResources = new int[]{
                    R.raw.lbpcascade_frontalface,
                    R.raw.haarcascade_profileface,
                    R.raw.haarcascade_frontalcatface_extended,
                    R.raw.haarcascade_frontalcatface,
                    R.raw.haarcascade_frontalface_alt2,
                    R.raw.haarcascade_frontalface_alt_tree,
                    R.raw.haarcascade_frontalface_alt,
                    R.raw.haarcascade_frontalface_default,
                    R.raw.haarcascade_lowerbody,
                    R.raw.haarcascade_upperbody,
                    R.raw.haarcascade_fullbody,
                    R.raw.haarcascade_eye_tree_eyeglasses,
                    R.raw.haarcascade_eye,
                    R.raw.haarcascade_lefteye_2splits,
                    R.raw.haarcascade_righteye_2splits,
                    R.raw.haarcascade_licence_plate_rus_16stages,
                    R.raw.haarcascade_russian_plate_number,
                    R.raw.haarcascade_smile
            };

            String[] haarXML = new String[]{
                    "lbpcascade_frontalface.xml",
                    "haarcascade_profileface.xml",
                    "haarcascade_frontalcatface_extended.xml",
                    "haarcascade_frontalcatface.xml",
                    "haarcascade_frontalface_alt2.xml",
                    "haarcascade_frontalface_alt_tree.xml",
                    "haarcascade_frontalface_alt.xml",
                    "haarcascade_frontalface_default.xml",
                    "haarcascade_lowerbody.xml",
                    "haarcascade_upperbody.xml",
                    "haarcascade_fullbody.xml",
                    "haarcascade_eye_tree_eyeglasses.xml",
                    "haarcascade_eye.xml",
                    "haarcascade_lefteye_2splits.xml",
                    "haarcascade_righteye_2splits.xml",
                    "haarcascade_licence_plate_rus_16stages.xml",
                    "haarcascade_russian_plate_number.xml",
                    "haarcascade_smile.xml"
            };
            switch(status){
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV Loaded Successfully");
                    setMinFaceSize(0.2f);

                    try{
                        File cascadeDir = getDir("cascade", Context.MODE_PRIVATE);
                        InputStream is;
                        int mode = caller.getStartMode();
                        Log.i(TAG, "OpenCV start mode " + mode);
                        is = getResources().openRawResource(rawResources[mode]);
                        mCascadeFile = new File(cascadeDir, haarXML[mode]);
                        FileOutputStream os = new FileOutputStream(mCascadeFile);

                        byte[] buffer = new byte[4096];
                        int bytesRead;
                        while ((bytesRead = is.read(buffer)) != -1) {
                            os.write(buffer, 0, bytesRead);
                        }
                        is.close();
                        os.close();

                        mFaceDetector = new CascadeClassifier(mCascadeFile.getAbsolutePath());
                        if (mFaceDetector.empty()) {
                            Log.e(TAG, "Failed to load cascade classifier " + mCascadeFile.toString());
                            mFaceDetector = null;
                        } else
                            Log.i(TAG, "Loaded cascade classifier from " + mCascadeFile.getAbsolutePath());

                        is = getResources().openRawResource(rawResources[11]); // see mItemEye in MainActivity
                        mCascadeFile = new File(cascadeDir, haarXML[11]);
                        os = new FileOutputStream(mCascadeFile);
                        while ((bytesRead = is.read(buffer)) != -1) {
                            os.write(buffer, 0, bytesRead);
                        }
                        is.close();
                        os.close();
                        mEyeDetector = new CascadeClassifier(mCascadeFile.getAbsolutePath());
                        if (mEyeDetector.empty()) {
                            Log.e(TAG, "Failed to load cascade classifier " + mCascadeFile.toString());
                            mEyeDetector = null;
                        } else
                            Log.i(TAG, "Loaded cascade classifier from " + mCascadeFile.getAbsolutePath());

                        cascadeDir.delete();
                    }catch(IOException e){
                        e.printStackTrace();
                        Log.i(TAG, "Failed to load cascade. Exception thrown: " + e);
                    }

                    mOpenCvCameraView.enableView();

                } break;
                default:
                {
                    super.onManagerConnected(status);
                }break;
            }//switch
        }//onManagerConnected
    };//BaseLoaderCallback

    public FdActivity(){
    }

    /** Called when the activity is first created. **/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "called onCreate");
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);

        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.fd_activity_surface_view);
        mOpenCvCameraView.setCvCameraViewListener(this);
        mainAct = MainActivity.mMainActivity;
    }

    @Override
    public void onPause(){
        super.onPause();
        if(mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onResume(){
        super.onResume();
        mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
    }

    public void onDestroy(){
        super.onDestroy();
        mOpenCvCameraView.disableView();
    }

    public void onCameraViewStarted(int width, int height){
        mGray = new Mat();
        mRgba = new Mat();
    }

    public void onCameraViewStopped(){
        mGray.release();
        mRgba.release();
    }

    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {

        mRgba = inputFrame.rgba();
        mGray = inputFrame.gray();

        if (mAbsoluteFaceSize == 0) {
            int height = mGray.rows();
            if (round(height * mRelativeFaceSize) > 0) {
                mAbsoluteFaceSize = round(height * mRelativeFaceSize);
            }
        }

        MatOfRect faces = new MatOfRect();
        MatOfRect eyes  = new MatOfRect();

        if (mFaceDetector != null) {
            mFaceDetector.detectMultiScale(mGray, faces, 1.3, 5, 3,
                    new Size(mAbsoluteFaceSize, mAbsoluteFaceSize), new Size());

            if (!faces.empty()) {
                Rect[] facesArray = faces.toArray();
                Log.d(TAG, "Faces detected: " + facesArray.length);
                for (int i = 0; i < facesArray.length; i++) {
                    Point center = new Point((facesArray[i].x + facesArray[i].width/2), (facesArray[i].y + facesArray[i].height/2));
                    Imgproc.ellipse(mRgba, center, new Size(facesArray[i].width/2, facesArray[i].height/2),
                            0, 0, 360, new Scalar(255,0,255), 4, 8, 0);
                    Mat faceROI = new Mat(mGray, facesArray[i]);
                    mEyeDetector.detectMultiScale(faceROI, eyes);
                    Rect[] eyesArray = eyes.toArray();
                    for (int j=0; j<eyesArray.length; j++) {
                        Point eyeCenter = new Point(facesArray[i].x + eyesArray[j].x + eyesArray[j].width/2,
                                facesArray[i].y + eyesArray[j].y + eyesArray[j].height/2);
                        int radius = (int) round((eyesArray[j].width + eyesArray[j].height)*0.25);
                        Imgproc.circle(mRgba, eyeCenter, radius, new Scalar(255,0,0), 4, 8, 0);
                    }
                }
            }
        }
        else {
            Log.e(TAG, "Detection method is not selected!");
        }
        return mRgba;
    }

    private void setMinFaceSize(float faceSize){
        mRelativeFaceSize = faceSize;
        mAbsoluteFaceSize = 0;
    }
}
