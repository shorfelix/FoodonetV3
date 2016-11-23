package com.roa.foodonetv3.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.Profile;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.roa.foodonetv3.R;
import com.roa.foodonetv3.model.User;
import com.roa.foodonetv3.services.AddUserToServerService;

public class WelcomeUserActivity extends AppCompatActivity {

    private static final String TAG = "WelcomeUserActivity";
    private ImageView userImageView;
    private Button finishRegisterationButton;
    private TextView userNameTxt;
    private FirebaseUser mFirebaseUser;
    private FirebaseAuth mFirebaseAuth;
    private EditText userPhoneNumber;
    private String userName = "";
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_user);

        userImageView = (ImageView) findViewById(R.id.userImageView);
        finishRegisterationButton = (Button) findViewById(R.id.finishRegisterationButton);
        userNameTxt = (TextView) findViewById(R.id.userNameTxt);
        userPhoneNumber = (EditText) findViewById(R.id.userPhoneNumberTxt);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        // Initialize Firebase Auth
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        userName = mFirebaseUser.getDisplayName();

        //load the photo from fireBase
        Glide.with(this).load(mFirebaseUser.getPhotoUrl()).into(userImageView);
        userNameTxt.setText(userName);

        finishRegisterationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber = userPhoneNumber.getText().toString();
                if(isLegalNumber(phoneNumber)) {
                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(WelcomeUserActivity.this);
                    /** save user phone number to sharePreferences */
                    sharedPreferences.edit().putString(User.PHONE_NUMBER, phoneNumber).apply();

                    /** sign in the user to foodonet server and get his new (or old) id and save it to the shared preferences through the service */
                    String uuid = sharedPreferences.getString(User.ACTIVE_DEVICE_DEV_UUID,null);
                    String providerId = "";
                    for (UserInfo userInfo : mFirebaseUser.getProviderData()) {
                        String tempProviderId = userInfo.getProviderId();
                        if(tempProviderId.equals("google.com")){
                            providerId = "google";
                        }
                        if (tempProviderId.equals("facebook.com")) {
                            providerId = "facebook";
                        }
                    }
                    User user = new User(providerId,mFirebaseUser.getUid(),"token1",phoneNumber,mFirebaseUser.getEmail(),mFirebaseUser.getDisplayName(),true,uuid);

                    Intent i = new Intent(WelcomeUserActivity.this, AddUserToServerService.class);
                    i.putExtra(User.USER_KEY,user.getUserJson().toString());
                    WelcomeUserActivity.this.startService(i);

                    String message = "user: "+user.getUserJson().toString();
                    Log.d(TAG,message);
                    finish();
                }
            }
        });

    }

    public Boolean isLegalNumber(String number){
        // TODO: 21/11/2016 currently just checking israeli mobile phone numbers, should allow line phones as well
        if(number.length()<10){
            Toast.makeText(this, "you miss digit", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(number.length()>10){
            Toast.makeText(this, "you have too much digits", Toast.LENGTH_SHORT).show();
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
}
