package com.roa.foodonetv3.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
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

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.google.firebase.auth.FirebaseUser;
import com.roa.foodonetv3.DatePickerDialog;
import com.roa.foodonetv3.R;
import com.roa.foodonetv3.commonMethods.AmazonImageUploader;
import com.roa.foodonetv3.commonMethods.CommonMethods;
import com.roa.foodonetv3.model.Publication;
import com.roa.foodonetv3.services.AddPublicationService;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AddPublicationActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, DatePickerDialog.EndDateDialogListener{

    private static final String TAG = "AddPublicationActivity";
    private static final int ACTION_TAKE_PICTURE = 1;
    private static final int ACTION_GET_PATH = 2;
    private EditText editTextTitleAddPublication,editTextLocationAddPublication,editTextPriceAddPublication,editTextShareWithAddPublication,editTextDetailsAddPublication;
    private TextView textEndDate;
    private long endingDate;
    private String mCurrentPhotoPath;
    private ImageView imagePictureAddPublication;

    // The TransferUtility is the primary class for managing transfer to S3
    private TransferUtility transferUtility;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_publication);

        transferUtility = CommonMethods.getTransferUtility(this);
        mCurrentPhotoPath = "";
//        TransferListener listener = new UploadListener();

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
////                AmazonImageUploader uploader = new AmazonImageUploader(this);
////                // TODO: 06/11/2016 currently hard coded
////                uploader.uploadPublicationImageToAmazon(new File(mCurrentPhotoPath),false);
//                // temp button until we get a button in the toolbar !!!!
//                // most of the information is hard coded right now, I'm just testing the layout and activation of the add function to the server
//                Intent intent = new Intent();
//                if (Build.VERSION.SDK_INT >= 19) {
//                    // For Android versions of KitKat or later, we use a
//                    // different intent to ensure
//                    // we can get the file path from the returned intent URI
//                    intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
//                    intent.addCategory(Intent.CATEGORY_OPENABLE);
//                    intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
//                } else {
//                    intent.setAction(Intent.ACTION_GET_CONTENT);
//                }

//                intent.setType("image/*");
//                startActivityForResult(intent, 0);
                if(mCurrentPhotoPath!= null){
                    uploadPublicationToServer();
                    if(!mCurrentPhotoPath.equals("")){
                        beginUpload(mCurrentPhotoPath);
                    }
                } else{
                    Toast.makeText(this, "no photo path", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.textEndDate:
                DatePickerDialog datePickerDialog = new DatePickerDialog(this);
                datePickerDialog.show();
                break;
            case R.id.imageTakePictureAddPublication:
                dispatchTakePictureIntent();
                break;
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.roa.foodonetv3.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, ACTION_TAKE_PICTURE);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
//        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode){
                case ACTION_TAKE_PICTURE:
//                    Bundle extras = data.getExtras();
//                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    Picasso.with(this).load(mCurrentPhotoPath).into(imagePictureAddPublication);
//                    imagePictureAddPublication.setImageBitmap(imageBitmap);
                    break;
                case ACTION_GET_PATH:
//                    Uri uri = data.getData();
//                    try {
//                        String path = mCurrentPhotoPath;
//                        beginUpload(path); // /storage/emulated/0/DCIM/Camera/IMG_20161024_163813.jpg , from example
//                    } catch (URISyntaxException e) {
//                        Toast.makeText(this,
//                                "Unable to get the file from the given URI.  See error log for details",
//                                Toast.LENGTH_LONG).show();
//                        Log.e(TAG, "Unable to upload file from the given uri", e);
//                    }
                    break;
            }
        }
    }

//    @Override
    public void uploadPublicationToServer() {
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
            // TODO: 08/11/2016 repair starting time and ending time
            Publication publication = new Publication(localPublicationID, -1, title, details, location, (short) 2, 32.0907185, 34.873032,
                    String.valueOf(System.currentTimeMillis()/1000), String.valueOf(endingDate/1000),
                    "0500000000", true, CommonMethods.getDeviceUUID(this), mCurrentPhotoPath, 16, 0, user.getDisplayName(), price, "");
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
    private void beginUpload(String filePath) {
        if (filePath == null) {
            Toast.makeText(this, "Could not find the filepath of the selected file",
                    Toast.LENGTH_LONG).show();
            return;
        }
        File file = new File(filePath);
        transferUtility.upload(getResources().getString(R.string.amazon_bucket), file.getName(),file);
        /*
         * Note that usually we set the transfer listener after initializing the
         * transfer. However it isn't required in this sample app. The flow is
         * click upload button -> start an activity for image selection
         * startActivityForResult -> onActivityResult -> beginUpload -> onResume
         * -> set listeners to in progress transfers.
         */
        // observer.setTransferListener(new UploadListener());
    }

//    /*
//     * Gets the file path of the given Uri.
//     */
//    @SuppressLint("NewApi")
//    private String getPath(Uri uri) throws URISyntaxException {
//        final boolean needToCheckUri = Build.VERSION.SDK_INT >= 19;
//        String selection = null;
//        String[] selectionArgs = null;
//        // Uri is different in versions after KITKAT (Android 4.4), we need to
//        // deal with different Uris.
//        if (needToCheckUri && DocumentsContract.isDocumentUri(getApplicationContext(), uri)) {
//            if (isExternalStorageDocument(uri)) {
//                final String docId = DocumentsContract.getDocumentId(uri);
//                final String[] split = docId.split(":");
//                return Environment.getExternalStorageDirectory() + "/" + split[1];
//            } else if (isDownloadsDocument(uri)) {
//                final String id = DocumentsContract.getDocumentId(uri);
//                uri = ContentUris.withAppendedId(
//                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
//            } else if (isMediaDocument(uri)) {
//                final String docId = DocumentsContract.getDocumentId(uri);
//                final String[] split = docId.split(":");
//                final String type = split[0];
//                if ("image".equals(type)) {
//                    uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
//                } else if ("video".equals(type)) {
//                    uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
//                } else if ("audio".equals(type)) {
//                    uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
//                }
//                selection = "_id=?";
//                selectionArgs = new String[] {
//                        split[1]
//                };
//            }
//        }
//        if ("content".equalsIgnoreCase(uri.getScheme())) {
//            String[] projection = {
//                    MediaStore.Images.Media.DATA
//            };
//            Cursor cursor = null;
//            try {
//                cursor = getContentResolver()
//                        .query(uri, projection, selection, selectionArgs, null);
//                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//                if (cursor.moveToFirst()) {
//                    return cursor.getString(column_index);
//                }
//            } catch (Exception e) {
//            }
//        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
//            return uri.getPath(); //content://media/external/images/media , from example
//        }
//        return null;
//    }

//    /**
//     * @param uri The Uri to check.
//     * @return Whether the Uri authority is ExternalStorageProvider.
//     */
//    public static boolean isExternalStorageDocument(Uri uri) {
//        return "com.android.externalstorage.documents".equals(uri.getAuthority());
//    }
//
//    /**
//     * @param uri The Uri to check.
//     * @return Whether the Uri authority is DownloadsProvider.
//     */
//    public static boolean isDownloadsDocument(Uri uri) {
//        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
//    }
//
//    /**
//     * @param uri The Uri to check.
//     * @return Whether the Uri authority is MediaProvider.
//     */
//    public static boolean isMediaDocument(Uri uri) {
//        return "com.android.providers.media.documents".equals(uri.getAuthority());
//    }

    private class UploadListener implements TransferListener {

        // Simply updates the UI list when notified.
        @Override
        public void onError(int id, Exception e) {
            Log.e(TAG, "Error during upload: " + id, e);
//            updateList();
        }

        @Override
        public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
            Log.d(TAG, String.format("onProgressChanged: %d, total: %d, current: %d",
                    id, bytesTotal, bytesCurrent));
//            updateList();
        }

        @Override
        public void onStateChanged(int id, TransferState newState) {
            Log.d(TAG, "onStateChanged: " + id + ", " + newState);
//            updateList();
        }
    }
}
