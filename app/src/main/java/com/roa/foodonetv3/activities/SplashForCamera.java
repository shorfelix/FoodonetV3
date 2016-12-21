package com.roa.foodonetv3.activities;

import android.animation.ObjectAnimator;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;
import com.roa.foodonetv3.R;

public class SplashForCamera extends AppCompatActivity {
    private static final String TAG = "SplashForCamera";
    public static final int TIMER = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash_for_camera);

        /** set animation of message to rotate the phone to take the picture, and rotate the message */
        TextView textSplashMessageForCamera = (TextView) findViewById(R.id.textSplashMessageForCamera);
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(textSplashMessageForCamera,"rotation",90);
        objectAnimator.setDuration(TIMER/2);
        objectAnimator.start();

        /** exit after timer ends */
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, TIMER);
    }
}
