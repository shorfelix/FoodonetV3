package com.roa.foodonetv3.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.v4.content.FileProvider;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.google.firebase.auth.FirebaseUser;
import com.roa.foodonetv3.DatePickerDialog;
import com.roa.foodonetv3.R;
import com.roa.foodonetv3.commonMethods.CommonMethods;
import com.roa.foodonetv3.model.Publication;
import com.roa.foodonetv3.services.AddPublicationService;
import com.squareup.picasso.Picasso;
import java.io.File;
import java.io.IOException;

public class AddPublicationActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, DatePickerDialog.EndDateDialogListener{

    private static final String TAG = "AddPublicationActivity";
    private static final int INTENT_TAKE_PICTURE = 1;
    private EditText editTextTitleAddPublication,editTextLocationAddPublication,editTextPriceAddPublication,editTextShareWithAddPublication,editTextDetailsAddPublication;
    private TextView textEndDate;
    private long endingDate;
    private String mCurrentPhotoPath;
    private ImageView imagePictureAddPublication;

    /**The TransferUtility is the primary class for managing transfer to S3*/
    private TransferUtility transferUtility;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_publication);

        /** instantiate the transfer utility for the s3*/
        transferUtility = CommonMethods.getTransferUtility(this);
        /** local image path that will be used for saving locally and uploading the file name to the server*/
        mCurrentPhotoPath = "";

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        editTextTitleAddPublication = (EditText) findViewById(R.id.editTextTitleAddPublication);
        editTextLocationAddPublication = (EditText) findViewById(R.id.editTextLocationAddPublication);
        editTextShareWithAddPublication = (EditText) findViewById(R.id.editTextShareWithAddPublication);
        editTextDetailsAddPublication = (EditText) findViewById(R.id.editTextDetailsAddPublication);
        editTextPriceAddPublication = (EditText) findViewById(R.id.editTextPriceAddPublication);
        textEndDate = (TextView) findViewById(R.id.textEndDate);
        textEndDate.setOnClickListener(this);
        findViewById(R.id.imageTakePictureAddPublication).setOnClickListener(this);
        imagePictureAddPublication = (ImageView) findViewById(R.id.imagePictureAddPublication);

        /** temporary button to add a test publication to the server */
        findViewById(R.id.buttonTestAdd).setOnClickListener(this);
    }

    @Override
    public void OnEndDatePicked(long endingDate, String date) {
        // TODO: 09/11/2016 the ios server string for dates are totally different...
        textEndDate.setText(date);
        this.endingDate = endingDate;
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        /** handle the navigation actions in the common methods class */
        CommonMethods.navigationItemSelectedAction(this,item.getItemId());

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.buttonTestAdd:
                /** button for uploading the publication to the server, if an image was taken,
                 *  start uploading to the s3 server as well, currently no listener for s3 finished upload*/
                uploadPublicationToServer();
                if(!mCurrentPhotoPath.equals("")){
                    beginS3Upload("file:"+mCurrentPhotoPath);
                } else{
                    Toast.makeText(this, "no photo path", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.textEndDate:
                /** starts the date picker dialog for the end date */
                DatePickerDialog datePickerDialog = new DatePickerDialog(this);
                datePickerDialog.show();
                break;
            case R.id.imageTakePictureAddPublication:
                /** starts the image taking intent through the default app*/
                dispatchTakePictureIntent();
                break;
        }
    }

    private void dispatchTakePictureIntent() {
        /** starts the image taking intent through the default app*/
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = CommonMethods.createImageFile(this);
                mCurrentPhotoPath = photoFile.getPath();
                Log.d(TAG,"photo path: " + mCurrentPhotoPath);
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.e(TAG,ex.getMessage());
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.roa.foodonetv3.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, INTENT_TAKE_PICTURE);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode){
                case INTENT_TAKE_PICTURE:
                    /** an image was successfully taken, since we have the path already,
                     *  we'll run the editOverwriteImage method that scales down, shapes and overwrites the images in the path
                     *  returns true if successful*/
                    if(CommonMethods.editOverwriteImage(this,mCurrentPhotoPath)){
                        /** let picasso handle the caching and scaling to the imageView */
                        Picasso.with(this)
                                    .load("file:"+mCurrentPhotoPath)
                                    .resize(imagePictureAddPublication.getWidth(),imagePictureAddPublication.getHeight())
                                    .centerCrop()
                                    .into(imagePictureAddPublication);
    //                    imagePictureAddPublication.setImageBitmap(scaledBitmap);
                    }
                    break;
            }
        }
    }

//    @Override
    public void uploadPublicationToServer() {
        /** upload the publication to the foodonet server */
        FirebaseUser user = MainDrawerActivity.getFireBaseUser();
        String title = editTextTitleAddPublication.getText().toString();
        String location = editTextLocationAddPublication.getText().toString();
        String priceS = editTextPriceAddPublication.getText().toString();
        String shareWith = editTextShareWithAddPublication.getText().toString();
        String details = editTextDetailsAddPublication.getText().toString();
        long localPublicationID = CommonMethods.getNewLocalPublicationID();
        if(title.equals("") || location.equals("") || shareWith.equals("")){
            Toast.makeText(this, R.string.post_please_enter_all_fields, Toast.LENGTH_SHORT).show();
        } else {
            double price;
            if (priceS.equals("")) {
                price = 0.0;
            } else {
                try {
                    price = Double.parseDouble(priceS);
                } catch (NumberFormatException e) {
                    Log.e("AddPublicationActivity", e.getMessage());
                    Toast.makeText(this, R.string.post_toast_please_enter_a_price_in_numbers, Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            // TODO: 08/11/2016 repair starting time and ending time. also currently some fields are hard coded for testing
            Publication publication = new Publication(localPublicationID, -1, title, details, location, (short) 2, 32.0907185, 34.873032,
                    String.valueOf(System.currentTimeMillis()/1000), String.valueOf(endingDate/1000),
                    "0500000000", true, CommonMethods.getDeviceUUID(this), CommonMethods.getFileNameFromPath(mCurrentPhotoPath), 16, 0, user.getDisplayName(), price, "");
            Intent i = new Intent(this, AddPublicationService.class);
            i.putExtra(Publication.PUBLICATION_KEY, Publication.getPublicationJson(publication).toString());
            i.putExtra(Publication.PUBLICATION_UNIQUE_ID_KEY, publication.getId());
            startService(i);
            finish();
        }
    }

    /*
     * Begins to upload the file specified by the file path.
     */
    private void beginS3Upload(String filePath) {
        /** upload the file to the S3 server */
        if (filePath == null) {
            Toast.makeText(this, "Could not find the filepath of the selected file",
                    Toast.LENGTH_LONG).show();
            return;
        }
        String[] split = filePath.split(":");
        File file = new File(split[1]);
        transferUtility.upload(getResources().getString(R.string.amazon_bucket), file.getName(),file);
        // TODO: 09/11/2016 add logic to completion or failure of upload image
        /*
         * Note that usually we set the transfer listener after initializing the
         * transfer. However it isn't required in this sample app. The flow is
         * click upload button -> start an activity for image selection
         * startActivityForResult -> onActivityResult -> beginS3Upload -> onResume
         * -> set listeners to in progress transfers.
         */
        // observer.setTransferListener(new UploadListener());
    }
}
