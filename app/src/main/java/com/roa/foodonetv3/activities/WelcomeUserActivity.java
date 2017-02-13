package com.roa.foodonetv3.activities;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.roa.foodonetv3.R;
import com.roa.foodonetv3.commonMethods.ReceiverConstants;
import com.roa.foodonetv3.model.User;
import com.roa.foodonetv3.services.FoodonetService;
import com.roa.foodonetv3.services.GetDataService;

import de.hdodenhof.circleimageview.CircleImageView;

public class WelcomeUserActivity extends AppCompatActivity {

    private static final String TAG = "WelcomeUserActivity";
    private Button finishRegisterationButton;
    private TextView userNameTxt;
    private FirebaseUser mFirebaseUser;
    private FirebaseAuth mFirebaseAuth;
    private EditText userPhoneNumber;
    private String userName = "";
    private CircleImageView circleImageView;
    private SharedPreferences preferences;
    private FoodonetReceiver receiver;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_user);

        setTitle(R.string.foodonet);

        finishRegisterationButton = (Button) findViewById(R.id.buttonFinishRegistration);
        userNameTxt = (TextView) findViewById(R.id.textUserName);
        userPhoneNumber = (EditText) findViewById(R.id.editUserPhoneNumber);
        circleImageView = (CircleImageView) findViewById(R.id.circleImageUser);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        // Initialize Firebase Auth
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        if (mFirebaseUser != null) {
            userName = mFirebaseUser.getDisplayName();
            //load the photo from fireBase
            Uri userPhotoUrl = mFirebaseUser.getPhotoUrl();
            if(userPhotoUrl!= null){
                Glide.with(this).load(userPhotoUrl).into(circleImageView);
            } else{
                Glide.with(this).load(R.drawable.foodonet_image).into(circleImageView);
            }
            if(userName!= null){
                userNameTxt.setText(userName);
            } else{
                // TODO: 28/11/2016 add logic
            }
            finishRegisterationButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                String phoneNumber = userPhoneNumber.getText().toString();
                if(isLegalNumber(phoneNumber)) {
                    /** save user phone number to sharedPreferences */
                    preferences.edit().putString(User.PHONE_NUMBER, phoneNumber).apply();

                    /** sign in the user to foodonet server and get his new (or existing) id and save it to the shared preferences through the service */
                    String uuid = preferences.getString(User.ACTIVE_DEVICE_DEV_UUID,null);
                    String providerId = "";
                    String userEmail = mFirebaseUser.getEmail();
                    for (UserInfo userInfo : mFirebaseUser.getProviderData()) {
    //                        String mail = userInfo.getEmail();
                        String tempProviderId = userInfo.getProviderId();
                        if(tempProviderId.equals("google.com")){
                            providerId = "google";
                        }
                        if (tempProviderId.equals("facebook.com")) {
                            providerId = "facebook";
                        }
                        Toast.makeText(WelcomeUserActivity.this, userEmail, Toast.LENGTH_SHORT).show();
                    }
                    User user = new User(providerId,mFirebaseUser.getUid(),"token1",phoneNumber,userEmail,mFirebaseUser.getDisplayName(),true,uuid);

                    Intent i = new Intent(WelcomeUserActivity.this, FoodonetService.class);
                    i.putExtra(ReceiverConstants.ACTION_TYPE, ReceiverConstants.ACTION_ADD_USER);
                    i.putExtra(ReceiverConstants.JSON_TO_SEND,user.getUserJson().toString());
                    WelcomeUserActivity.this.startService(i);
                    dialog = new ProgressDialog(WelcomeUserActivity.this);
                    dialog.show();

                    String message = "user: "+user.getUserJson().toString();
                    Log.d(TAG,message);
                }
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        receiver = new FoodonetReceiver();
        IntentFilter filter = new IntentFilter(ReceiverConstants.BROADCAST_FOODONET);
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver,filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        if(dialog!=null){
            dialog.dismiss();
        }
    }

    public Boolean isLegalNumber(String number){
        // TODO: 21/11/2016 currently just checking israeli mobile phone numbers, should allow line phones as well, fix!
        if(number.length()<10){
            Toast.makeText(this, "invalid phone number", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(number.length()>10){
            Toast.makeText(this, "invalid phone number", Toast.LENGTH_SHORT).show();
            return false;
        }

        String numForCheck = number.substring(0,2);
        if (!numForCheck.equals("05")){
            Toast.makeText(this, "your area code is incorrect", Toast.LENGTH_SHORT).show();
            return false;
        }
        numForCheck = number.substring(0,3);
        char[] d= numForCheck.toCharArray();
        if ((d[2]!='0')&&(d[2]!='2')&&(d[2]!='3')&&(d[2]!='4')&&(d[2]!='5')&&(d[2]!='6')&&(d[2]!='8')){
            Toast.makeText(this, "your area code is incorrect 05-?" +d[2], Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private class FoodonetReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getIntExtra(ReceiverConstants.ACTION_TYPE,-1)== ReceiverConstants.ACTION_ADD_USER){
                /** receiver from the foodonet server of creating the new user */
                if(dialog!=null){
                    dialog.dismiss();
                }
                if(intent.getBooleanExtra(ReceiverConstants.SERVICE_ERROR,false)){
                    // TODO: 27/11/2016 add logic if fails
                    Toast.makeText(context, "service failed", Toast.LENGTH_SHORT).show();
                } else{
                    /** user successfully added, finish the activity*/
                    Intent getDataIntent = new Intent(WelcomeUserActivity.this, GetDataService.class);
                    getDataIntent.putExtra(ReceiverConstants.ACTION_TYPE,ReceiverConstants.ACTION_GET_GROUPS);
                    startService(getDataIntent);
                    Intent startActivityIntent = new Intent(WelcomeUserActivity.this, MainActivity.class);
                    startActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(startActivityIntent);
                    finish();
                }
            }
        }
    }
}
