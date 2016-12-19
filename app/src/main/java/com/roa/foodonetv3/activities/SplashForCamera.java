package com.roa.foodonetv3.activities;

import android.animation.ObjectAnimator;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Window;
import android.widget.TextView;

import com.roa.foodonetv3.R;

public class SplashForCamera extends AppCompatActivity {
    private static final String TAG = "SplashForCamera";
    public static final int TIMER = 3000;
    private TextView textSplashMessageForCamera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash_for_camera);

        textSplashMessageForCamera = (TextView) findViewById(R.id.textSplashMessageForCamera);
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(textSplashMessageForCamera,"rotation",90);
        objectAnimator.setDuration(TIMER/2);
        objectAnimator.start();
        /** how to set the size of the activity*/
//        DisplayMetrics dm = new DisplayMetrics();
//        getWindowManager().getDefaultDisplay().getMetrics(dm);
//
//        int width = dm.widthPixels;
//        int height = dm.heightPixels;
//        getWindow().setLayout((int)(width*0.8),(int)(height*0.8));

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, TIMER);
    }
}
