package com.theupswing.stimuleye;

import android.content.Intent;
import android.os.StrictMode;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.theupswing.stimuleye.DetectEyes;
import com.theupswing.stimuleye.R;

public class Results extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        ConstraintLayout failLayout = findViewById(R.id.fail_layout);
        ConstraintLayout passLayout = findViewById(R.id.pass_layout);

        Intent intent = getIntent();
        String result = intent.getStringExtra("result");

        if(result.equals("fail")){
            failLayout.setVisibility(View.VISIBLE);
            sendEmail();
        }
        else{
            passLayout.setVisibility(View.VISIBLE);
        }
    }

    public void testAgain(View view){
        Intent testAgainIntent = new Intent(Results.this, DetectEyes.class);
        startActivity(testAgainIntent);
    }

    private void sendEmail(){
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);


        try{
        GMailSender sender = new GMailSender("stimuleyeapp@gmail.com", "Alpha1cse308");
        sender.sendMail(Results.this,
                "Urgent: Concussion Detected",
                "Dear Nicolas Elia,\nYou are receiving this email because you are listed in our system as an emergency contact for John Smith.\nWe wanted to inform you that John has just been diagnosed with a concussion.\n\nThank you,\nStimulEye Team",
                "stimuleyeapp@gmail.com",
                "nickelia98@gmail.com");
        } catch (Exception e) {
            Log.e("SendMail", e.getMessage(), e);
        }



    }
}
