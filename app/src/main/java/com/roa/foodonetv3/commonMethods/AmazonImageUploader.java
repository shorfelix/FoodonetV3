package com.roa.foodonetv3.commonMethods;

import android.content.Context;
import android.util.Log;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.roa.foodonetv3.R;

import java.io.File;

//import FooDoNetServiceUtil.ServicesBroadcastReceiver;

public class AmazonImageUploader {
    private static final String MY_TAG = "food_amazonUploader";

    private static final int MODE_PUBLICATION = 1;
    private static final int MODE_AVATAR = 2;

    private int currentMode;

    private CognitoCachingCredentialsProvider credentialsProvider;

    private IAmazonFinishedCallback listener;
    private Context context;

    public AmazonImageUploader(Context context){
        this.context = context;
        listener = (IAmazonFinishedCallback) context;
    }


    public void uploadPublicationImageToAmazon(File imgFile, boolean isEdit) {
        currentMode = MODE_PUBLICATION;
        registerAWSS3();
        File imageToSave = imgFile;
        String imageName = imgFile.getName();
        uploadPhotoForPublication(imageToSave, imageName, isEdit, context.getString(R.string.amazon_sub_path_publication_images));
    }

    public void uploadUserAvatarToAmazon(File imgFile){
        String imageName = context.getString(R.string.amazon_user_avatar_image_name)
                .replace("{0}", String.valueOf(CommonMethods.getMyUserID(context)));
        currentMode = MODE_AVATAR;
        registerAWSS3();
        File imageToSave = imgFile;
        uploadPhotoForPublication(imageToSave, imageName, false, context.getString(R.string.amazon_sub_path_user_images));
    }

    private void registerAWSS3() {
        // initialize a credentials provider object with your Activity’s context and
        // the values from your identity pool
        credentialsProvider = new CognitoCachingCredentialsProvider(
                context, // get the context for the current activity
                "458352772906", // your AWS Account id
                "us-east-1:ec4b269f-88a9-471d-b548-7886e1f9f2d7", // your identity pool id
                "arn:aws:iam::458352772906:role/Cognito_food_collector_poolUnauth_DefaultRole", // an unauthenticated role ARN
                "arn:aws:iam::458352772906:role/Cognito_food_collector_poolAuth_DefaultRole",// an authenticated role ARN
                Regions.US_EAST_1 //Region
        );
        Log.i(MY_TAG, "succesfully registered to amazon");
    }

    private void uploadPhotoForPublication(File sourceFile, String fileName, final boolean isEdit, String amazonSubPath) {
        // Create an S3 client
        AmazonS3 s3 = new AmazonS3Client(credentialsProvider);

        // Set the region of your S3 bucket
        //s3.setRegion(Region.getRegion(Regions.US_EAST_1));

        TransferUtility transferUtility = new TransferUtility(s3, context);

        TransferObserver observer = transferUtility.upload(
                amazonSubPath,     /* The bucket to upload to */
                fileName,          /* The key for the uploaded object */
                sourceFile        /* The file where the data to upload exists */
        );

        observer.setTransferListener(new TransferListener() {

            @Override
            public void onStateChanged(int id, TransferState state) {
                // do something
                Log.d(MY_TAG,"AMAZON UPLOAD STATE IS ---> " + state);
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                int percentage = (int) (bytesCurrent / bytesTotal * 100);
                //Display percentage transferred to user
                Log.d(MY_TAG, "AMAZON PROGRESS OF UPLOAD PICTURE IS ---> " + percentage);
                if (percentage >= 99) {
                    switch (currentMode){
                        case MODE_AVATAR:
//                            listener.notifyToBListenerAboutEvent(
//                                    ServicesBroadcastReceiver.ACTION_CODE_SAVE_NEW_PUB_SUCCESS);
                            listener.notifyToBListenerAboutEvent(MODE_AVATAR);
                            // TODO: 06/11/2016  hard coded for test
                            break;
                        case MODE_PUBLICATION:
//                            listener.notifyToBListenerAboutEvent(isEdit
//                                    ? ServicesBroadcastReceiver.ACTION_CODE_SAVE_EDITED_PUB_SUCCESS
//                                    : ServicesBroadcastReceiver.ACTION_CODE_SAVE_NEW_PUB_SUCCESS);
                            // TODO: 06/11/2016 hard coded for test
                            listener.notifyToBListenerAboutEvent(MODE_PUBLICATION);
                            break;
                    }
                }
            }

            @Override
            public void onError(int id, Exception ex) {
                // do something
                ex.printStackTrace();
                Log.d(MY_TAG, "OOOOPPPSSSS - UPLOAD DIDN'T WORK WELL ---> " + ex.getMessage());
            }

        });

    }

    public interface IAmazonFinishedCallback {
        void notifyToBListenerAboutEvent(int eventCode);
    }
}

