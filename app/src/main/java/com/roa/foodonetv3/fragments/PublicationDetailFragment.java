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
import android.support.transition.Fade;
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
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.roa.foodonetv3.R;
import com.roa.foodonetv3.activities.FullscreenActivityImage;
import com.roa.foodonetv3.activities.SignInActivity;
import com.roa.foodonetv3.adapters.ReportsRecyclerAdapter;
import com.roa.foodonetv3.commonMethods.CommonMethods;
import com.roa.foodonetv3.commonMethods.ReceiverConstants;
import com.roa.foodonetv3.model.Publication;
import com.roa.foodonetv3.model.RegistrationPublication;
import com.roa.foodonetv3.model.PublicationReport;
import com.roa.foodonetv3.services.FoodonetService;
import com.squareup.picasso.Picasso;
import java.io.File;
import java.util.ArrayList;
import java.util.Locale;
import de.hdodenhof.circleimageview.CircleImageView;

public class PublicationDetailFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "PublicationDetailFrag";
    private TextView textCategory,textTimeRemaining,textJoined,textTitlePublication,textPublicationAddress,textPublicationRating,textPublisherName,textPublicationPrice,textPublicationDetails;
    private ImageView imagePicturePublication,imageActionPublicationJoin,imageActionPublicationReport,imageActionPublicationPhone,imageActionPublicationMap;
    private CircleImageView imagePublisherUser;
    private Publication publication;
    private ReportsRecyclerAdapter adapter;
    private FoodonetReceiver receiver;
    private boolean isAdmin;
    private boolean isRegistered;
    private AlertDialog alertDialog;

    public PublicationDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /** get the user's ID */
        long userID = CommonMethods.getMyUserID(getContext());

        /** get the publication from the intent */
        publication = getArguments().getParcelable(Publication.PUBLICATION_KEY);

        /** check if the user is the admin of the publication */
        isAdmin = publication != null && publication.getPublisherID() == userID;
        if(isAdmin) {
            /** if the user is the admin, show the menu for editing, deleting or taking the publication offline */
            setHasOptionsMenu(true);
        } else{
            /** the user is not the admin, check if he's a registered user for the publication */
            isRegistered = publication.getRegisteredUsers().contains(userID);
        }

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
        if(alertDialog!=null && alertDialog.isShowing()){
            alertDialog.dismiss();
        }
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
        if(alertDialog!=null && alertDialog.isShowing()){
            alertDialog.dismiss();
        }
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(receiver);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.detail_options,menu);
    }

    /** menu for a publication the user is the admin of */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(!isAdmin)return false;
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
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext())
                                .setTitle("Are you sure?")
//                                .setMessage()
                                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        String[] args = {String.valueOf(publication.getId())};
                                        Intent deleteIntent = new Intent(getContext(),FoodonetService.class);
                                        deleteIntent.putExtra(ReceiverConstants.ACTION_TYPE,ReceiverConstants.ACTION_DELETE_PUBLICATION);
                                        deleteIntent.putExtra(ReceiverConstants.ADDRESS_ARGS,args);
                                        getContext().startService(deleteIntent);
                                    }
                                })
                                .setNegativeButton(R.string.no, null);
                alertDialog = alertDialogBuilder.show();
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
        textJoined.setText(String.format(Locale.US,"%1$s : %2$d",getResources().getString(R.string.joined),publication.getRegisteredUsersCount()));
        textTitlePublication.setText(publication.getTitle());
        textPublicationAddress.setText(publication.getAddress());
        // TODO: 13/11/2016 get rating through reports
        float rating = 4.2f;
        textPublicationRating.setText(String.valueOf(rating));
        textPublisherName.setText(publication.getIdentityProviderUserName());
        String priceS;
        if(publication.getPrice()==0){
            priceS = getResources().getString(R.string.free);
        } else{
            priceS = String.valueOf(publication.getPrice());
        }
        textPublicationPrice.setText(priceS);
        textPublicationDetails.setText(publication.getSubtitle());
        File mCurrentPhotoFile = new File(CommonMethods.getPhotoPathByID(getContext(),publication.getId()));
        if(!publication.getPhotoURL().equals("") && mCurrentPhotoFile.isFile()){
            /** there's an image path, try to load from file */
            Log.d(TAG,"layout size: "+imagePicturePublication.getWidth()+","+imagePicturePublication.getHeight());
            // TODO: 13/11/2016 can't get width and height
            Picasso.with(getContext())
                    .load(mCurrentPhotoFile)
//                    .resize(imagePicturePublication.getWidth(),imagePicturePublication.getHeight())
//                    .centerCrop()
                    .into(imagePicturePublication);
        } else{
            /** load default image */
            Picasso.with(getContext())
                    .load(R.drawable.foodonet_image)
                    .into(imagePicturePublication);
        }
    }

    @Override
    public void onClick(View v) {
        Intent i;
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user==null){
            /** if the user is not signed in, all buttons are disabled, take him to the sign in activity */
            i = new Intent(getContext(), SignInActivity.class);
            startActivity(i);
        } else{
            switch (v.getId()){
                /** join publication */
                case R.id.imageActionPublicationJoin:
                    // TODO: 21/12/2016 add logic for if the user is already signed to the publication
                    RegistrationPublication registrationPublication = new RegistrationPublication(publication.getId(),CommonMethods.getCurrentTimeSeconds(),
                            CommonMethods.getDeviceUUID(getContext()),publication.getVersion(),user.getDisplayName(),CommonMethods.getMyUserPhone(getContext()),
                            CommonMethods.getMyUserID(getContext()));
                    String registration = registrationPublication.getJsonForRegistration().toString();
                    String[] registrationArgs = {String.valueOf(publication.getId())};
                    i = new Intent(getContext(),FoodonetService.class);
                    i.putExtra(ReceiverConstants.ACTION_TYPE, ReceiverConstants.ACTION_REGISTER_TO_PUBLICATION);
                    i.putExtra(ReceiverConstants.ADDRESS_ARGS,registrationArgs);
                    i.putExtra(ReceiverConstants.JSON_TO_SEND,registration);
                    getContext().startService(i);
                    break;

                /** should be for sending SMS, currently used for adding report */
                case R.id.imageActionPublicationSMS:
                    // TODO: 14/11/2016 test for adding a report, hard coded
                    long currentTime = (long) CommonMethods.getCurrentTimeSeconds();
                    PublicationReport publicationReport = new PublicationReport(-1,publication.getId(),publication.getVersion(), (short) 3,CommonMethods.getDeviceUUID(getContext()),
                            //"","",
                            String.valueOf(currentTime),user.getDisplayName(),
                            CommonMethods.getMyUserPhone(getContext()),CommonMethods.getMyUserID(getContext()),4);
                    String reportJson = publicationReport.getAddReportJson().toString();
                    Log.d(TAG,"report json:"+reportJson);
                    i = new Intent(getContext(),FoodonetService.class);
                    i.putExtra(ReceiverConstants.ACTION_TYPE, ReceiverConstants.ACTION_ADD_REPORT);
                    String[] reportArgs = {String.valueOf(publication.getId())};
                    i.putExtra(ReceiverConstants.ADDRESS_ARGS,reportArgs);
                    i.putExtra(ReceiverConstants.JSON_TO_SEND,reportJson);
                    getContext().startService(i);
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
                    i = new Intent(getContext(), FullscreenActivityImage.class);
                    startActivity(i);
                    break;
            }
        }
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
                        ArrayList<PublicationReport> reports = intent.getParcelableArrayListExtra(PublicationReport.REPORT_KEY);
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
                        // TODO: 21/12/2016 add logic
                        Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    }
}
