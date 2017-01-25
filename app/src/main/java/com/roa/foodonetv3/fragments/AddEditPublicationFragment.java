package com.roa.foodonetv3.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v4.content.LocalBroadcastManager;
import android.text.InputFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.roa.foodonetv3.R;
import com.roa.foodonetv3.activities.PlacesActivity;
import com.roa.foodonetv3.activities.PublicationActivity;
import com.roa.foodonetv3.activities.SplashForCamera;
import com.roa.foodonetv3.commonMethods.CommonConstants;
import com.roa.foodonetv3.commonMethods.CommonMethods;
import com.roa.foodonetv3.commonMethods.DecimalDigitsInputFilter;
import com.roa.foodonetv3.commonMethods.ReceiverConstants;
import com.roa.foodonetv3.db.GroupsDBHandler;
import com.roa.foodonetv3.model.Group;
import com.roa.foodonetv3.model.Publication;
import com.roa.foodonetv3.model.SavedPlace;
import com.roa.foodonetv3.model.User;
import com.roa.foodonetv3.services.FoodonetService;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class AddEditPublicationFragment extends Fragment implements View.OnClickListener {
    public static final String TAG = "AddEditPublicationFrag";
    private static final int INTENT_TAKE_PICTURE = 1;
    private static final int INTENT_PICK_PICTURE = 2;
    public static final int TYPE_NEW_PUBLICATION = 1;
    public static final int TYPE_EDIT_PUBLICATION = 2;
    private EditText editTextTitleAddPublication, editTextPriceAddPublication, editTextDetailsAddPublication;
    private Spinner spinnerShareWith;
    private TextView textLocationAddPublication, textInfo;
    private long endingDate;
    private String mCurrentPhotoPath, pickPhotoPath;
    private ImageView imagePictureAddPublication;
    private SavedPlace place;
    private Publication publication;
    private boolean isEdit;
    private ArrayList<Group> groups;
    private ArrayAdapter<String> spinnerAdapter;
    private FoodonetReceiver receiver;
    private View layoutInfo;


    public AddEditPublicationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        /** instantiate the transfer utility for the s3*/
//        transferUtility = CommonMethods.getTransferUtility(getContext());
        /** local image path that will be used for saving locally and uploading the file name to the server*/
        mCurrentPhotoPath = "";

        receiver = new FoodonetReceiver();
        isEdit = getArguments().getInt(TAG, TYPE_NEW_PUBLICATION) != TYPE_NEW_PUBLICATION;

        if (isEdit) {
            /** if there's a publication in the intent - it is an edit of an existing publication */
            if (savedInstanceState == null) {
                /** also check if there's a savedInstanceState, if there isn't - load the publication, if there is - load from savedInstanceState */
                publication = getArguments().getParcelable(Publication.PUBLICATION_KEY);
                place = new SavedPlace(publication.getAddress(),publication.getLat(),publication.getLng());
            } else {
                // TODO: 19/11/2016 add savedInstanceState reader
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_add_edit_publication, container, false);

        getActivity().setTitle(R.string.new_share);

        /** set layouts */
        editTextTitleAddPublication = (EditText) v.findViewById(R.id.editTextTitleAddPublication);
        textLocationAddPublication = (TextView) v.findViewById(R.id.textLocationAddPublication);
        textLocationAddPublication.setOnClickListener(this);
        spinnerShareWith = (Spinner) v.findViewById(R.id.spinnerShareWith);
        spinnerAdapter = new ArrayAdapter<>(getContext(),R.layout.item_spinner_groups,R.id.textGroupName);
        spinnerShareWith.setAdapter(spinnerAdapter);
        editTextDetailsAddPublication = (EditText) v.findViewById(R.id.editTextDetailsAddPublication);
        editTextPriceAddPublication = (EditText) v.findViewById(R.id.editTextPriceAddPublication);
        editTextPriceAddPublication.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(5,2)});
        v.findViewById(R.id.imageTakePictureAddPublication).setOnClickListener(this);
        imagePictureAddPublication = (ImageView) v.findViewById(R.id.imagePictureAddPublication);

        layoutInfo = v.findViewById(R.id.layoutInfo);
        layoutInfo.setBackgroundColor(getResources().getColor(R.color.fooLightGrey));
        layoutInfo.setVisibility(View.GONE);
        textInfo = (TextView) v.findViewById(R.id.textInfo);
        textInfo.setText(R.string.start_sharing_by_adding_an_image_of_the_food_you_wish_to_share);
        textInfo.setTextSize(getResources().getDimension(R.dimen.text_size_12));

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter(ReceiverConstants.BROADCAST_FOODONET);
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(receiver,filter);

        GroupsDBHandler handler = new GroupsDBHandler(getContext());
        groups = handler.getAllGroupsWithPublic();
        String[] groupsNames = new String[groups.size()];
        Group group;
        String groupName;
        for (int i = 0; i < groups.size(); i++) {
            group = groups.get(i);
            groupName = group.getGroupName();
            groupsNames[i] = groupName;
        }
        spinnerAdapter.addAll(groupsNames);

        if (mCurrentPhotoPath == null|| mCurrentPhotoPath.equals("")) {
            layoutInfo.setVisibility(View.VISIBLE);
            imagePictureAddPublication.setVisibility(View.GONE);
        } else{
            layoutInfo.setVisibility(View.GONE);
            imagePictureAddPublication.setVisibility(View.VISIBLE);
        }
        if (isEdit) {
            loadPublicationIntoViews();
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(receiver);
    }

    // TODO: 19/11/2016 not tested yet
    public void loadPublicationIntoViews() {
        editTextTitleAddPublication.setText(publication.getTitle());
        textLocationAddPublication.setText(publication.getAddress());
        // TODO: 29/12/2016 add logic to spinner
//        spinnerShareWith.set
        editTextDetailsAddPublication.setText(publication.getSubtitle());
        editTextPriceAddPublication.setText(String.valueOf(publication.getPrice()));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageTakePictureAddPublication:
                AlertDialog.Builder dialog = new AlertDialog.Builder(getContext())
                        .setTitle(R.string.add_image)
                        .setPositiveButton(R.string.camera, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                /** start the popup activity*/
                                getContext().startActivity(new Intent(getContext(), SplashForCamera.class));
                                /**wait for the popup activity before starting the camera */
                                Thread thread = new Thread() {
                                    @Override
                                    public void run() {
                                        super.run();
                                        synchronized (getContext()) {
                                            try {
                                                getContext().wait(CommonConstants.SPLASH_CAMERA_TIME);
                                                /** starts the image taking intent through the default app*/
                                                dispatchTakePictureIntent();
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                };
                                thread.start();
                            }
                        })
                        .setNegativeButton(R.string.gallery, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                /** get image from gallery */
                                dispatchPickPictureIntent();
                            }
                        });
                dialog.show();

                break;
            case R.id.textLocationAddPublication:
                /** start the google places select activity */
                startActivityForResult(new Intent(getContext(), PlacesActivity.class), PlacesActivity.REQUEST_PLACE_PICKER);
                break;
        }
    }

    private void dispatchTakePictureIntent() {
        /** starts the image taking intent through the default app*/
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getContext().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = CommonMethods.createImageFile(getContext());
                pickPhotoPath = photoFile.getPath();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.e(TAG, ex.getMessage());
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getContext(),
                        "com.roa.foodonetv3.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, INTENT_TAKE_PICTURE);
            }
        }
    }

    /** get image from gallery app installed */
    private void dispatchPickPictureIntent() {
        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getIntent.setType("image/*");

        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");

        Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});

        File photoFile = null;
        try {
            photoFile = CommonMethods.createImageFile(getContext());
            pickPhotoPath = photoFile.getPath();
            Log.d(TAG, "photo path: " + pickPhotoPath);
            startActivityForResult(chooserIntent, INTENT_PICK_PICTURE);
        } catch (IOException ex) {
            // Error occurred while creating the File
            Log.e(TAG, ex.getMessage());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            mCurrentPhotoPath = pickPhotoPath;
            switch (requestCode) {

                /** an image was successfully taken, since we have the path already,
                 *  we'll run the editOverwriteImage method that scales down, shapes and overwrites the images in the path
                 *  returns true if successful*/
                case INTENT_TAKE_PICTURE:
                    if (CommonMethods.editOverwriteImage(getContext(), mCurrentPhotoPath)) {
                        /** let glide handle the caching and scaling to the imageView */
                        Glide.with(this).load(mCurrentPhotoPath).centerCrop().into(imagePictureAddPublication);
                    }
                    break;
                /** an image was successfully picked, since we have the path already,
                 *  we'll run the editOverwriteImage method that scales down, shapes and overwrites the images in the path
                 *  returns true if successful*/
                case INTENT_PICK_PICTURE:
                    Uri uri = data.getData();

                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), uri);
                        if (CommonMethods.editOverwriteImage(mCurrentPhotoPath,bitmap)) {
                            /** let glide handle the caching and scaling to the imageView */
                            Glide.with(this).load(mCurrentPhotoPath).centerCrop().into(imagePictureAddPublication);
                        }
                    } catch (IOException e) {
                        Log.e(TAG,e.getMessage());
                    }
                    break;
                case PlacesActivity.REQUEST_PLACE_PICKER:
                    place = data.getParcelableExtra("place");
                    textLocationAddPublication.setText(place.getAddress());
                    break;
            }
        }
    }

    public void uploadPublicationToServer() {
        /** upload the publication to the foodonet server */
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String contactInfo = PreferenceManager.getDefaultSharedPreferences(getContext()).getString(User.PHONE_NUMBER, "");
        String title = editTextTitleAddPublication.getText().toString();
        String location = textLocationAddPublication.getText().toString();
        String priceS = editTextPriceAddPublication.getText().toString();
        String details = editTextDetailsAddPublication.getText().toString();
        /** currently starting time is now */
        long startingDate = System.currentTimeMillis()/1000;
        if (endingDate == 0) {
            /** default ending date is 2 days after creation */
            endingDate = startingDate + 172800;
        }
        long localPublicationID;
        if (!isEdit) {
            localPublicationID = CommonMethods.getNewLocalPublicationID();
        } else {
            localPublicationID = publication.getId();
        }
        String photoPath;
        if(mCurrentPhotoPath==null || mCurrentPhotoPath.equals("")){
            photoPath = null;
        } else{
            photoPath = "file:"+mCurrentPhotoPath;
        }
        if (title.equals("") || location.equals("") || place.getLat()== CommonConstants.LATLNG_ERROR || place.getLng()==CommonConstants.LATLNG_ERROR
                || photoPath == null) {
            Toast.makeText(getContext(), R.string.post_please_enter_all_fields, Toast.LENGTH_SHORT).show();
        } else {
            double price;
            if (priceS.equals("")) {
                price = 0.0;
            } else {
                try {
                    price = Double.parseDouble(priceS);
                } catch (NumberFormatException e) {
                    Log.e("PublicationActivity", e.getMessage());
                    Toast.makeText(getContext(), R.string.post_toast_please_enter_a_price_in_numbers, Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            // TODO: 08/11/2016 currently some fields are hard coded for testing

            publication = new Publication(localPublicationID, -1, title, details, location, (short) 2, place.getLat(), place.getLng(),
                    String.valueOf(startingDate), String.valueOf(endingDate), contactInfo, true, CommonMethods.getDeviceUUID(getContext()),
                    //CommonMethods.getFileNameFromPath(mCurrentPhotoPath),
                    photoPath,
                    CommonMethods.getMyUserID(getContext()),
                    groups.get(spinnerShareWith.getSelectedItemPosition()).getGroupID() , user.getDisplayName(), price, "");
            ArrayList<Parcelable> data = new ArrayList<>();
            data.add(publication);
            // TODO: 27/11/2016 currently just adding publications, no logic for edit yet
            Intent i = new Intent(getContext(), FoodonetService.class);
            i.putExtra(ReceiverConstants.ACTION_TYPE, ReceiverConstants.ACTION_ADD_PUBLICATION);
            i.putExtra(ReceiverConstants.JSON_TO_SEND, publication.getPublicationJson().toString());
            i.putExtra(ReceiverConstants.DATA,data);
            getContext().startService(i);
        }
    }

    private class FoodonetReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            /** receiver for reports got from the service */
            int action = intent.getIntExtra(ReceiverConstants.ACTION_TYPE, -1);
            switch (action) {
                case ReceiverConstants.ACTION_FAB_CLICK:
                    /** button for uploading the publication to the server */
                    if (intent.getBooleanExtra(ReceiverConstants.SERVICE_ERROR, false)) {
                        // TODO: 18/12/2016 add logic if fails
                        Toast.makeText(context, "fab failed", Toast.LENGTH_SHORT).show();
                    } else {
                        if (intent.getIntExtra(ReceiverConstants.FAB_TYPE, -1) == ReceiverConstants.FAB_TYPE_SAVE_NEW_PUBLICATION) {
                            uploadPublicationToServer();
                        }
                    }
                    break;

                case ReceiverConstants.ACTION_ADD_PUBLICATION:
                    /** added new publication */
                    Intent i = new Intent(getContext(), PublicationActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    i.putExtra(PublicationActivity.ACTION_OPEN_PUBLICATION,PublicationActivity.MY_PUBLICATIONS_TAG);
                    getActivity().startActivity(i);
            }
        }
    }
}
