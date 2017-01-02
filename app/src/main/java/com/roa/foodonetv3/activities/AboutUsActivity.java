package com.roa.foodonetv3.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import com.roa.foodonetv3.R;

public class AboutUsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        TextView textAppVersion = (TextView) findViewById(R.id.textAppVersion);
        textAppVersion.setText(String.format("v %1$s",getResources().getString(R.string.app_version)));

        // TODO: 21/12/2016 add facebook button for "Like"
    }
}
