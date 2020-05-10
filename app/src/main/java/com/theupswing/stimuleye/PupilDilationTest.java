package com.theupswing.stimuleye;

import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;

import java.io.IOException;

import static java.lang.Math.sqrt;
import static org.opencv.imgproc.Imgproc.COLOR_BGR2GRAY;
import static org.opencv.imgproc.Imgproc.COLOR_GRAY2BGRA;
import static org.opencv.imgproc.Imgproc.MORPH_DILATE;
import static org.opencv.imgproc.Imgproc.THRESH_BINARY_INV;
import static org.opencv.imgproc.Imgproc.dilate;
import static org.opencv.imgproc.Imgproc.getStructuringElement;
import static org.opencv.imgproc.Imgproc.moments;

public class PupilDilationTest extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {

    private Mat mRgba, mGray;
    JavaCameraView javaCameraView;
    private double diameter;
    private DatabaseHelper database;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pupil_dilation_test);

        database = new DatabaseHelper(PupilDilationTest.this);
        database.clearTable();

        javaCameraView = findViewById(R.id.pdt_cam_view);

        if (!OpenCVLoader.initDebug()) {
            // This means that the library isn't working.

            // Fix:
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_4_0, this, baseCallback); // he had it as version 3.0.0 for some reason (he was actually using 3.4.3 as well)
            //baseCallback is implemented privately at the bottom
        } else {
            try {
                baseCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        javaCameraView.setCvCameraViewListener(this);

    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        mRgba = new Mat();
        mGray = new Mat();
        //setUpFlashIntervals(4, 2);
        //javaCameraView.turnOnTheFlash();
        newSetUpFlash();
    }

    @Override
    public void onCameraViewStopped() {
        mRgba.release();
        mGray.release();
    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        mRgba = inputFrame.rgba();
        int x = 30;
        int y = 200;
        int side = 500;
        Rect roi = new Rect(x, y, side, side);
        //Imgproc.rectangle(mRgba,new Point(x,y),new Point(x+side, y+side),new Scalar(255,0,0), 5);

        Mat gray = new Mat();
        Mat eqimg = new Mat();
        Mat thimg = new Mat();
        Mat eroded_img = new Mat();

        Mat croppedImage = mRgba.submat(roi);
        Imgproc.cvtColor(croppedImage, gray,COLOR_BGR2GRAY);
        Imgproc.equalizeHist(gray,eqimg);
        Imgproc.threshold(eqimg,thimg,20,255,THRESH_BINARY_INV);

        Mat kernel = getStructuringElement(MORPH_DILATE, new Size(3,3));
        dilate(thimg, eroded_img, kernel);

        Moments m_th = moments(eroded_img, true);
        diameter = 2 * sqrt(m_th.m00 / 3.14159);
        Log.i("ErrorX", diameter+"");

        Imgproc.cvtColor(eroded_img, eroded_img, COLOR_GRAY2BGRA);
        eroded_img.copyTo(mRgba.submat(roi));
        return mRgba;
    }

    private BaseLoaderCallback baseCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) throws IOException {
            if (status == BaseLoaderCallback.SUCCESS) {
                javaCameraView.enableView();
            } else {
                super.onManagerConnected(status);
            }
        }
    };

    private void newSetUpFlash(){
        CountDownTimer mCountDownTimer = new CountDownTimer(10 * 1000, 1000) {

            boolean flashIsOn = false;

            @Override
            public void onTick(long millisUntilFinished) {
                long timeInSeconds = millisUntilFinished/1000;
                Log.i("ErrorX", timeInSeconds + "");
                if(timeInSeconds > 6){
                    if(!flashIsOn) {
                        javaCameraView.turnOnTheFlash();
                        flashIsOn = true;
                    }
                } else if(timeInSeconds <= 6 && timeInSeconds >= 3){
                    if(flashIsOn) {
                        javaCameraView.turnOffTheFlash();
                        flashIsOn = false;
                    }
                } else{
                    if(!flashIsOn) {
                        javaCameraView.turnOnTheFlash();
                        flashIsOn = true;
                    }
                }

                database.insertData(10 - (int)timeInSeconds, diameter, flashIsOn? 1:0);
            }

            @Override
            public void onFinish() {
                javaCameraView.turnOffTheFlash();
                Intent intent = new Intent(PupilDilationTest.this, DisplayData.class);
                startActivity(intent);
            }
        }.start();
    }

}
