package com.roa.foodonetv3.fragments;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.roa.foodonetv3.R;
import com.roa.foodonetv3.activities.PublicationActivity;
import com.roa.foodonetv3.activities.SignInActivity;
import com.roa.foodonetv3.adapters.ReportsRecyclerAdapter;
import com.roa.foodonetv3.commonMethods.CommonMethods;
import com.roa.foodonetv3.commonMethods.ReceiverConstants;
import com.roa.foodonetv3.db.PublicationsDBHandler;
import com.roa.foodonetv3.db.RegisteredUsersDBHandler;
import com.roa.foodonetv3.dialogs.ReportDialog;
import com.roa.foodonetv3.model.Publication;
import com.roa.foodonetv3.model.RegisteredUser;
import com.roa.foodonetv3.model.PublicationReport;
import com.roa.foodonetv3.services.FoodonetService;
import java.io.File;
import java.util.ArrayList;
import java.util.Locale;
import de.hdodenhof.circleimageview.CircleImageView;

public class PublicationDetailFragment extends Fragment implements View.OnClickListener, ReportDialog.OnReportCreateListener{
    private static final String TAG = "PublicationDetailFrag";
    private TextView textCategory,textTimeRemaining,textJoined,textTitlePublication,textPublicationAddress,textPublicationRating,textPublisherName,textPublicationPrice,textPublicationDetails;
    private ImageView imagePicturePublication,imageActionPublicationJoin,imageActionPublicationReport,imageActionPublicationPhone,imageActionPublicationMap;
    private CircleImageView imagePublisherUser;
    private Publication publication;
    private ReportsRecyclerAdapter adapter;
    private FoodonetReceiver receiver;
    private int countRegisteredUsers;
    private long userID;
    private boolean isAdmin;
    private boolean isRegistered;
    private ArrayList<PublicationReport> reports;
    private AlertDialog alertDialog;
    private ReportDialog reportDialog;

    public PublicationDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /** get the user's ID */
        userID = CommonMethods.getMyUserID(getContext());

        /** get the publication from the intent */
        publication = getArguments().getParcelable(Publication.PUBLICATION_KEY);

        /** check if the user is the admin of the publication */
        isAdmin = publication != null && publication.getPublisherID() == userID;
        RegisteredUsersDBHandler registeredUsersDBHandler = new RegisteredUsersDBHandler(getContext());
        if(isAdmin) {
            /** if the user is the admin, show the menu for editing, deleting or taking the publication offline */
            setHasOptionsMenu(true);
        } else{
            /** the user is not the admin, check if he's a registered user for the publication */
            // TODO: 13/01/2017 add db get

            isRegistered = registeredUsersDBHandler.isUserRegistered(publication.getId());
            /** if the user is registered to the publication, show menu for unregistering */
            if(isRegistered){
                setHasOptionsMenu(true);
            }
        }
        /** get the number of users registered for this publication */
        countRegisteredUsers = registeredUsersDBHandler.getRegisteredUsersCount(publication.getId());

        receiver = new FoodonetReceiver();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_publication_detail, container, false);

        /** set title */
        getActivity().setTitle(publication.getTitle());

        /** set recycler view */
        RecyclerView recyclerPublicationReport = (RecyclerView) v.findViewById(R.id.recyclerPublicationReport);
        recyclerPublicationReport.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ReportsRecyclerAdapter(getContext());
        recyclerPublicationReport.setAdapter(adapter);

        /** set views */
        textCategory = (TextView) v.findViewById(R.id.textCategory);
        textTimeRemaining = (TextView) v.findViewById(R.id.textTimeRemaining);
        textJoined = (TextView) v.findViewById(R.id.textJoined);
        textTitlePublication = (TextView) v.findViewById(R.id.textTitlePublication);
        textPublicationAddress = (TextView) v.findViewById(R.id.textPublicationAddress);
        textPublicationRating = (TextView) v.findViewById(R.id.textPublicationRating);
        textPublisherName = (TextView) v.findViewById(R.id.textPublisherName);
        textPublicationPrice = (TextView) v.findViewById(R.id.textPublicationPrice);
        textPublicationDetails = (TextView) v.findViewById(R.id.textPublicationDetails);
        imagePicturePublication = (ImageView) v.findViewById(R.id.imagePicturePublication);
        imagePicturePublication.setOnClickListener(this);
        imagePublisherUser = (CircleImageView) v.findViewById(R.id.imagePublisherUser);
        imageActionPublicationJoin = (ImageView) v.findViewById(R.id.imageActionPublicationJoin);
        imageActionPublicationReport = (ImageView) v.findViewById(R.id.imageActionPublicationSMS);
        imageActionPublicationPhone = (ImageView) v.findViewById(R.id.imageActionPublicationPhone);
        imageActionPublicationMap = (ImageView) v.findViewById(R.id.imageActionPublicationMap);
        /** if the user is the admin, don't show the communication buttons */
        if (isAdmin) {
            imageActionPublicationJoin.setVisibility(View.GONE);
            imageActionPublicationReport.setVisibility(View.GONE);
            imageActionPublicationPhone.setVisibility(View.GONE);
            imageActionPublicationMap.setVisibility(View.GONE);
        } else {
            imageActionPublicationJoin.setOnClickListener(this);
            imageActionPublicationReport.setOnClickListener(this);
            imageActionPublicationPhone.setOnClickListener(this);
            imageActionPublicationMap.setOnClickListener(this);
        }

        return v;
    }

    @Override
    public void onResume() {
        // TODO: 21/11/2016 load from server every resume?
        super.onResume();
        IntentFilter filter = new IntentFilter(ReceiverConstants.BROADCAST_FOODONET);
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(receiver,filter);

        // TODO: 21/12/2016 should be from db
        Intent intent = new Intent(getContext(),FoodonetService.class);
        intent.putExtra(ReceiverConstants.ACTION_TYPE, ReceiverConstants.ACTION_GET_REPORTS);
        String[] args = {String.valueOf(publication.getId()),String.valueOf(publication.getVersion())};
        intent.putExtra(ReceiverConstants.ADDRESS_ARGS,args);
        getContext().startService(intent);

        /** initialize the views */
        initViews();
    }

    @Override
    public void onPause() {
        super.onPause();
        if(reportDialog!=null && reportDialog.isShowing()){
            reportDialog.dismiss();
        }
        if(alertDialog!=null && alertDialog.isShowing()){
            alertDialog.dismiss();
        }
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(receiver);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        if(isAdmin){
            inflater.inflate(R.menu.detail_options_admin,menu);
        } else{
            inflater.inflate(R.menu.detail_options_registered,menu);
        }
    }

    /** menu for a publication the user is the admin of */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.detail_edit:
                // TODO: 19/12/2016 add logic
                Toast.makeText(getContext(), "edit", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.detail_take_offline:
                // TODO: 19/12/2016 add logic
                Toast.makeText(getContext(), "take offline", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.detail_delete:
                AlertDialog.Builder alertDialogDeletePublication = new AlertDialog.Builder(getContext())
                        .setTitle("Are you sure?")
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String[] args = {String.valueOf(publication.getId())};
                                Intent deleteIntent = new Intent(getContext(),FoodonetService.class);
                                deleteIntent.putExtra(ReceiverConstants.ACTION_TYPE,ReceiverConstants.ACTION_DELETE_PUBLICATION);
                                deleteIntent.putExtra(ReceiverConstants.ADDRESS_ARGS,args);
                                getContext().startService(deleteIntent);
                                PublicationsDBHandler handler = new PublicationsDBHandler(getContext());
                                handler.deletePublication(publication.getId());
                                getActivity().finish();
                            }
                        })
                        .setNegativeButton(R.string.no, null);
                alertDialog = alertDialogDeletePublication.show();
                return true;
            case R.id.detail_unregister:
                AlertDialog.Builder alertDialogUnregisterPublication = new AlertDialog.Builder(getContext())
                        .setTitle("Are you sure?")
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String[] args = {String.valueOf(publication.getId()),String.valueOf(publication.getVersion()),
                                        String.valueOf(CommonMethods.getDeviceUUID(getContext()))};
                                Intent unregisterIntent = new Intent(getContext(),FoodonetService.class);
                                unregisterIntent.putExtra(ReceiverConstants.ACTION_TYPE,ReceiverConstants.ACTION_UNREGISTER_FROM_PUBLICATION);
                                unregisterIntent.putExtra(ReceiverConstants.ADDRESS_ARGS,args);
                                getContext().startService(unregisterIntent);
                            }
                        })
                        .setNegativeButton(R.string.no, null);
                alertDialog = alertDialogUnregisterPublication.show();
                return true;
        }
        return false;
    }

    /** set the views */
    private void initViews(){
        if(publication.getAudience()==0){
            /** audience is public */
            textCategory.setText(getResources().getString(R.string.audience_public));
        } else{
            /** audience is a specific group */
            // TODO: 13/11/2016 get group name through the server 
            textCategory.setText("test group");
            Drawable group = getResources().getDrawable(R.drawable.group);
            // TODO: 13/11/2016 check if this code works 
            textCategory.setCompoundDrawables(group,null,null,null);
        }

        String timeRemaining = String.format(Locale.US, "%1$s",
                CommonMethods.getTimeDifference(getContext(),CommonMethods.getCurrentTimeSeconds(),Double.parseDouble(publication.getEndingDate())));

        textTimeRemaining.setText(timeRemaining);
        textJoined.setText(String.format(Locale.US,"%1$s : %2$d",getResources().getString(R.string.joined),countRegisteredUsers));
        textTitlePublication.setText(publication.getTitle());
        textPublicationAddress.setText(publication.getAddress());
        textPublisherName.setText(publication.getIdentityProviderUserName());
        String priceS;
        if(publication.getPrice()==0){
            priceS = getResources().getString(R.string.free);
        } else{
            priceS = String.valueOf(publication.getPrice());
        }
        textPublicationPrice.setText(priceS);
        textPublicationDetails.setText(publication.getSubtitle());
        File mCurrentPhotoFile = new File(CommonMethods.getPhotoPathByID(getContext(),publication.getId(),publication.getVersion()));
        if(mCurrentPhotoFile.isFile()){
            /** there's an image path, try to load from file */
            // TODO: 13/11/2016 can't get width and height
            Glide.with(this).load(mCurrentPhotoFile).centerCrop().into(imagePicturePublication);
        } else{
            /** load default image */
            Glide.with(this).load(R.drawable.foodonet_image).centerCrop().into(imagePicturePublication);
        }
    }

    @Override
    public void onClick(View v) {
        Intent i;
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null || userID==-1){
            /** if the user is not signed in, all buttons are disabled, take him to the sign in activity */
            i = new Intent(getContext(), SignInActivity.class);
            startActivity(i);
        } else{
            switch (v.getId()){
                /** join publication */
                case R.id.imageActionPublicationJoin:
                    /** if the user is registered to the publication the button is a report button */
                    if(isRegistered){
                        /** if reports is null, we haven't received the reports yet and therefor, can't add a new report yet, since the user might have already sent one */
                        if(reports== null){
                            Toast.makeText(getContext(), getResources().getString(R.string.please_wait), Toast.LENGTH_SHORT).show();
                        }
                        /** if the reports were received, check if the user has previously added a report, only allow to send a new one if the user hasn't before */
                        else{
                            boolean found = false;
                            PublicationReport report;
                            for (int j = 0; j < reports.size(); j++) {
                                report = reports.get(j);
                                if(userID == report.getReportUserID()){
                                    found = true;
                                    break;
                                }
                            }
                            /** the user has reported previously */
                            if(found){
                                Toast.makeText(getContext(), getResources().getString(R.string.you_can_only_report_once), Toast.LENGTH_SHORT).show();
                            }
                            /** the user can add a new report */
                            else{
                                reportDialog = new ReportDialog(getContext(),this,publication.getTitle());
                                reportDialog.show();
                            }
                        }

                    }
                    /** if the user is not registered, the button is to join the publication */
                    else{
                        RegisteredUser registeredUser = new RegisteredUser(publication.getId(),CommonMethods.getCurrentTimeSeconds(),
                                CommonMethods.getDeviceUUID(getContext()),publication.getVersion(),user.getDisplayName(),CommonMethods.getMyUserPhone(getContext()),
                                CommonMethods.getMyUserID(getContext()));
                        String registration = registeredUser.getJsonForRegistration().toString();
                        String[] registrationArgs = {String.valueOf(publication.getId())};
                        i = new Intent(getContext(),FoodonetService.class);
                        i.putExtra(ReceiverConstants.ACTION_TYPE, ReceiverConstants.ACTION_REGISTER_TO_PUBLICATION);
                        i.putExtra(ReceiverConstants.ADDRESS_ARGS,registrationArgs);
                        i.putExtra(ReceiverConstants.JSON_TO_SEND,registration);
                        getContext().startService(i);
                        isRegistered = true;
                    }
                    break;

                /** send SMS with message body*/
                case R.id.imageActionPublicationSMS:
                    // TODO: 23/01/2017 add SMS intent

                    break;

                /** simple intent to put the phone number in the phone's default dialer */
                case R.id.imageActionPublicationPhone:
                    if (publication.getContactInfo().matches("[0-9]+") && publication.getContactInfo().length() > 2) {
                        i = new Intent(Intent.ACTION_VIEW, Uri.parse("tel:" + publication.getContactInfo()));
                        startActivity(i);
                    }
                    break;

                /** intent to open navigation apps */
                case R.id.imageActionPublicationMap:
                    // TODO: 22/11/2016 fix to allow both waze and google maps to work
                    if(publication.getLat()!=0 && publication.getLng()!= 0){
                        i = new Intent(Intent.ACTION_VIEW,
                                Uri.parse("geo:" + publication.getLat() + "," +
                                                publication.getLng()
//                                    + "?q=" + getStreet() + "+" +
//                                    getHousenumber() + "+" + getPostalcode() + "+" +
//                                    getCity()
                                ));
                        startActivity(i);
                    }
                    break;

                /** pressing on the publication image - open full screen view of the image */
                case R.id.imagePicturePublication:
                    // TODO: 16/01/2017 add image view logic
                    break;
            }
        }
    }

    @Override
    public void onReportCreate(int rating, short typeOfReport) {
        /** send the report */
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        long currentTime = (long) CommonMethods.getCurrentTimeSeconds();
        PublicationReport publicationReport = new PublicationReport(-1,publication.getId(),publication.getVersion(), typeOfReport,
                CommonMethods.getDeviceUUID(getContext()),String.valueOf(currentTime),user.getDisplayName(),
                CommonMethods.getMyUserPhone(getContext()),CommonMethods.getMyUserID(getContext()),rating);
        String reportJson = publicationReport.getAddReportJson().toString();
        Log.d(TAG,"report json:"+reportJson);
        Intent i = new Intent(getContext(),FoodonetService.class);
        i.putExtra(ReceiverConstants.ACTION_TYPE, ReceiverConstants.ACTION_ADD_REPORT);
        String[] reportArgs = {String.valueOf(publication.getId())};
        i.putExtra(ReceiverConstants.ADDRESS_ARGS,reportArgs);
        i.putExtra(ReceiverConstants.JSON_TO_SEND,reportJson);
        getContext().startService(i);
    }

    private class FoodonetReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            /** receiver for reports got from the service */
            int action = intent.getIntExtra(ReceiverConstants.ACTION_TYPE,-1);
            switch (action){
                /** got reports */
                case ReceiverConstants.ACTION_GET_REPORTS:
                    if(intent.getBooleanExtra(ReceiverConstants.SERVICE_ERROR,false)){
                        // TODO: 27/11/2016 add logic if fails
                        Toast.makeText(context, "service failed", Toast.LENGTH_SHORT).show();
                    } else{
                        reports = intent.getParcelableArrayListExtra(PublicationReport.REPORT_KEY);
                        float rating = PublicationReport.getRatingFromReports(reports);
                        if(rating==-1){
                            textPublicationRating.setText(R.string.not_rated);
                        } else{
                            textPublicationRating.setText(CommonMethods.getRoundedStringFromNumber(rating));
                        }
                        adapter.updateReports(reports);
                    }
                    break;

                /** registered to a publication */
                case ReceiverConstants.ACTION_REGISTER_TO_PUBLICATION:
                    if(intent.getBooleanExtra(ReceiverConstants.SERVICE_ERROR,false)){
                        // TODO: 27/11/2016 add logic if fails
                        Toast.makeText(context, "service failed", Toast.LENGTH_SHORT).show();
                    } else{
                        /** registered successfully */
                        Snackbar.make(imagePicturePublication,getResources().getString(R.string.successfully_registered),Snackbar.LENGTH_LONG).show();
                    }
                    break;

                /** report added */
                case ReceiverConstants.ACTION_ADD_REPORT:
                    if(intent.getBooleanExtra(ReceiverConstants.SERVICE_ERROR,false)){
                        // TODO: 27/11/2016 add logic if fails
                        Toast.makeText(context, "service failed", Toast.LENGTH_SHORT).show();
                    } else{
                        /** registered successfully */
                        Snackbar.make(imagePicturePublication,getResources().getString(R.string.report_added),Snackbar.LENGTH_LONG).show();
                        // TODO: 23/01/2017 add logic to limit
                    }
                    break;

                /** publication deleted */
                case ReceiverConstants.ACTION_DELETE_PUBLICATION:
                    if(alertDialog!=null && alertDialog.isShowing()){
                        alertDialog.dismiss();
                    }
                    if(intent.getBooleanExtra(ReceiverConstants.SERVICE_ERROR,false)){
                        // TODO: 19/12/2016 add logic if fails
                    } else{
                        Toast.makeText(context, getResources().getString(R.string.deleted), Toast.LENGTH_SHORT).show();
                        Intent openMyPublicationsIntent = new Intent(getContext(), PublicationActivity.class);
                        openMyPublicationsIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        getContext().startActivity(openMyPublicationsIntent);
                    }
                    break;
            }
        }
    }
}
