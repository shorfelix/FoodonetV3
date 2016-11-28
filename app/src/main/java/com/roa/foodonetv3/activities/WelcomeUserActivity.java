package com.roa.foodonetv3.activities;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
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
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserInfo;
import com.roa.foodonetv3.R;
import com.roa.foodonetv3.commonMethods.StartServiceMethods;
import com.roa.foodonetv3.model.User;
import com.roa.foodonetv3.services.FoodonetService;

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
    private GetUserReceiver receiver;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_user);

        finishRegisterationButton = (Button) findViewById(R.id.finishRegisterationButton);
        userNameTxt = (TextView) findViewById(R.id.userNameTxt);
        userPhoneNumber = (EditText) findViewById(R.id.userPhoneNumberTxt);
        circleImageView = (CircleImageView) findViewById(R.id.circleImage);
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
                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(WelcomeUserActivity.this);
                    /** save user phone number to sharedPreferences */
                    sharedPreferences.edit().putString(User.PHONE_NUMBER, phoneNumber).apply();

                    /** sign in the user to foodonet server and get his new (or old) id and save it to the shared preferences through the service */
                    String uuid = sharedPreferences.getString(User.ACTIVE_DEVICE_DEV_UUID,null);
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
                    i.putExtra(StartServiceMethods.ACTION_TYPE,StartServiceMethods.ACTION_ADD_USER);
                    i.putExtra(FoodonetService.JSON_TO_SEND,user.getUserJson().toString());
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
        receiver = new GetUserReceiver();
        IntentFilter filter = new IntentFilter(FoodonetService.BROADCAST_FOODONET_SERVER_FINISH);
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
        // TODO: 21/11/2016 currently just checking israeli mobile phone numbers, should allow line phones as well
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

    public class GetUserReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getIntExtra(StartServiceMethods.ACTION_TYPE,-1)==StartServiceMethods.ACTION_ADD_USER){
                if(dialog!=null){
                    dialog.dismiss();
                }
                if(intent.getBooleanExtra(FoodonetService.SERVICE_ERROR,false)){
                    // TODO: 27/11/2016 add logic if fails
                    Toast.makeText(context, "service failed", Toast.LENGTH_SHORT).show();
                } else{
                    /** user successfully added, finish the activity*/
                    Intent a = new Intent(WelcomeUserActivity.this, MainDrawerActivity.class);
                    a.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(a);
                    finish();
                }
            }
        }
    }
}
