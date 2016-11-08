package com.roa.foodonetv3.commonMethods;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.cognito.CognitoSyncManager;
import com.amazonaws.mobileconnectors.cognito.Dataset;
import com.amazonaws.regions.Regions;

/**
 * Created by Owner on 07/11/2016.
 */

public class UploadImageToAmazon {

//    // Initialize the Amazon Cognito credentials provider
//    CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
//            getApplicationContext(),
//            "us-west-2:1c8ccda3-58ac-4763-a991-a8888834f37f", // Identity Pool ID
//            Regions.US_WEST_2 // Region
//    );
//
//    // Initialize the Cognito Sync client
//    CognitoSyncManager syncClient = new CognitoSyncManager(
//            getApplicationContext(),
//            Regions.US_WEST_2, // Region
//            credentialsProvider);
//
//    // Create a record in a dataset and synchronize with the server
//    Dataset dataset = syncClient.openOrCreateDataset("myDataset");
//    dataset.put("myKey", "myValue");
//    dataset.synchronize(new DefaultSyncCallback() {
//        @Override
//        public void onSuccess(Dataset dataset, List newRecords) {
//            //Your handler code here
//        }
//    });
}
