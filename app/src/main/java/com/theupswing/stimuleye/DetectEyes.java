package com.theupswing.stimuleye;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;


public class DetectEyes extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {

    JavaCameraView javaCameraView;
    File cascFile;
    CascadeClassifier eyeDetector;
    CountDownTimer mCountDownTimer;
    Boolean timerIsOn = false;
    int stopTimer = 0;

    private Mat mRgba, mGray;  // Similar to python

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detect_eyes);

        javaCameraView = findViewById(R.id.java_cam_view); // this needs to be before the if else statement or the app crashes

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
    }

    @Override
    public void onCameraViewStopped() {
        // Close them
        mRgba.release();
        mGray.release();
    }

    // This is similar to Python
    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        // put them in the frame
        mRgba = inputFrame.rgba();
        mGray = inputFrame.gray();
        // detect face

        final MatOfRect eyeDetections = new MatOfRect();
        //eyeDetector.detectMultiScale(mRgba, eyeDetections);
        eyeDetector.detectMultiScale(mRgba, eyeDetections, 1.5, 11);

        final TextView textPrompt = findViewById(R.id.text_prompt);
        final ImageView eyeImage = findViewById(R.id.eye);

        int x = 30;
        int y = 200;
        int side = 500;
        Imgproc.rectangle(mRgba, new Point(x, y), new Point(x + side, y + side), new Scalar(255, 0, 0), 10);


        runOnUiThread(new Runnable() {
        @Override
        public void run() {

            int minAcceptedSide = 450;

            if (eyeDetections.toArray().length != 0) {
                //for (Rect rect : eyeDetections.toArray()) {}
                Rect rect = eyeDetections.toArray()[0];


                //if (rect.x < 300 || rect.x > 900) {   <- old position
                if (rect.x > 80) {
                    stopTimer();
                    if(!timerIsOn) {
                        textPrompt.setText("Align eye with the image above");
                        eyeImage.setBackgroundResource(R.drawable.eye_blue);
                    }
                } else if (rect.width < 450 || rect.height < 450) {
                    stopTimer();
                    if(!timerIsOn) {
                        textPrompt.setText("Move closer");
                        eyeImage.setBackgroundResource(R.drawable.eye_blue);
                    }
                } else if (rect.width > 450 && rect.height > 450) {
                    textPrompt.setText("Test will begin in 3 seconds");
                    eyeImage.setBackgroundResource(R.drawable.eye_green);
                    startTimer();
                    //Imgproc.rectangle(mRgba, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(255, 0, 0), 10);
                }

            } else {
                stopTimer();
                if(!timerIsOn) {
                    textPrompt.setText("Eye not detected");
                    eyeImage.setBackgroundResource(R.drawable.eye_gray);
                }
            }

        }
    });

        return mRgba;
    }

    private void startTimer() {
        if(!timerIsOn) {
            mCountDownTimer = new CountDownTimer(3500, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    TextView timer = findViewById(R.id.timer);
                    String time = (millisUntilFinished / 1000) +"";
                    timer.setText(time);
                }

                @Override
                public void onFinish() {
                    Intent intent = new Intent(DetectEyes.this, PupilDilationTest.class);
                    startActivity(intent);
                }
            }.start();

            timerIsOn = true;
        }

    }

    private void stopTimer(){
        if(timerIsOn){
            stopTimer++;
            if(stopTimer >= 20) {
                mCountDownTimer.cancel();
                TextView timer = findViewById(R.id.timer);
                timer.setText("");
                timerIsOn = false;
                stopTimer = 0;
            }
        }
    }

    private BaseLoaderCallback baseCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) throws IOException {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                    InputStream is = getResources().openRawResource(R.raw.haarcascade_eye);
                    File cascadeDir = getDir("cascade", Context.MODE_PRIVATE); // creates a directory
                    cascFile = new File(cascadeDir, "haarcascade_eye.xml"); // creates file in directory

                    FileOutputStream fos = new FileOutputStream(cascFile);

                    byte[] buffer = new byte[4096];
                    int bytesRead;

                    while ((bytesRead = is.read(buffer)) != -1) {
                        fos.write(buffer, 0, bytesRead);
                    }

                    is.close();
                    fos.close();

                    eyeDetector = new CascadeClassifier(cascFile.getAbsolutePath()); // this part is similar to python

                    if (eyeDetector.empty()) {
                        eyeDetector = null;
                    } else {
                        cascadeDir.delete();
                    }

                    javaCameraView.enableView();

                    break;


                default:
                    super.onManagerConnected(status); // Android studio will give you this line when it implements the method
                    break;
            }

        }
    };

}
