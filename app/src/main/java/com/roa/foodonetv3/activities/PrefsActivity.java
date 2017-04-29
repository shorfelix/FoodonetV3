package com.roa.foodonetv3.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.roa.foodonetv3.R;
import com.roa.foodonetv3.commonMethods.ReceiverConstants;
import com.roa.foodonetv3.fragments.PrefsFragment;
import com.roa.foodonetv3.services.GetDataService;

public class PrefsActivity extends AppCompatActivity implements PrefsFragment.OnSignOutClickListener {
    /** preferences menu */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prefs);
    }

    @Override
    public void onSignOutClick() {
        Intent intent = new Intent(this,GetDataService.class);
        intent.putExtra(ReceiverConstants.ACTION_TYPE,ReceiverConstants.ACTION_SIGN_OUT);
        this.startService(intent);
        finish();
    }
}
