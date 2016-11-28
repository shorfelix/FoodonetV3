package com.roa.foodonetv3.fragments;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.roa.foodonetv3.R;
import com.roa.foodonetv3.activities.SignInActivity;
import com.roa.foodonetv3.adapters.ReportsRecyclerAdapter;
import com.roa.foodonetv3.commonMethods.CommonMethods;
import com.roa.foodonetv3.commonMethods.StartServiceMethods;
import com.roa.foodonetv3.model.Publication;
import com.roa.foodonetv3.model.RegistrationPublication;
import com.roa.foodonetv3.model.ReportFromServer;
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
    private GetReportsReceiver receiver;


    public PublicationDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        publication = getArguments().getParcelable(Publication.PUBLICATION_KEY);
        receiver = new GetReportsReceiver();
        IntentFilter filter = new IntentFilter(FoodonetService.BROADCAST_FOODONET_SERVER_FINISH);
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(receiver,filter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_publication_detail, container, false);

        RecyclerView recyclerPublicationReport = (RecyclerView) v.findViewById(R.id.recyclerPublicationReport);
        recyclerPublicationReport.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ReportsRecyclerAdapter(getContext());
        recyclerPublicationReport.setAdapter(adapter);


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
        imagePublisherUser = (CircleImageView) v.findViewById(R.id.imagePublisherUser);
        imageActionPublicationJoin = (ImageView) v.findViewById(R.id.imageActionPublicationJoin);
        imageActionPublicationReport = (ImageView) v.findViewById(R.id.imageActionPublicationSMS);
        imageActionPublicationPhone = (ImageView) v.findViewById(R.id.imageActionPublicationPhone);
        imageActionPublicationMap = (ImageView) v.findViewById(R.id.imageActionPublicationMap);
        imageActionPublicationJoin.setOnClickListener(this);
        imageActionPublicationReport.setOnClickListener(this);
        imageActionPublicationPhone.setOnClickListener(this);
        imageActionPublicationMap.setOnClickListener(this);

        return v;
    }

    @Override
    public void onResume() {
        // TODO: 21/11/2016 load from server every resume?
        super.onResume();
        Intent intent = new Intent(getContext(),FoodonetService.class);
        intent.putExtra(StartServiceMethods.ACTION_TYPE,StartServiceMethods.ACTION_GET_REPORTS);
        String[] args = {String.valueOf(publication.getId()),String.valueOf(publication.getVersion())};
        intent.putExtra(FoodonetService.ADDRESS_ARGS,args);
        getContext().startService(intent);
        initViews();
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(receiver);
    }

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
        // TODO: 13/11/2016 add a method to get remaining time till end

        String timeRemaining = String.format(Locale.US, "%1$s",
                CommonMethods.getTimeDifference(getContext(),CommonMethods.getCurrentTimeSeconds(),Double.parseDouble(publication.getEndingDate())));
        textTimeRemaining.setText(timeRemaining);
        // TODO: 13/11/2016 get number of users who have joined this publication, currently hard coded
        textJoined.setText(String.format(Locale.US,"%1$s : %2$d",getResources().getString(R.string.joined),5));
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
                case R.id.imageActionPublicationJoin:
                    // TODO: 13/11/2016 add join logic
//                    Snackbar.make(imageActionPublicationJoin,"Currently not implemented",Snackbar.LENGTH_LONG).setAction("ACTION",null).show();
                    // TODO: 27/11/2016 test for registering to publication
                    RegistrationPublication registrationPublication = new RegistrationPublication(publication.getId(),CommonMethods.getCurrentTimeSeconds(),
                            CommonMethods.getDeviceUUID(getContext()),publication.getVersion(),user.getDisplayName(),CommonMethods.getMyUserPhone(getContext()),
                            CommonMethods.getMyUserID(getContext()));
                    String registration = registrationPublication.getJsonForRegistration().toString();
                    String[] registrationArgs = {String.valueOf(publication.getId())};
                    i = new Intent(getContext(),FoodonetService.class);
                    i.putExtra(StartServiceMethods.ACTION_TYPE,StartServiceMethods.ACTION_REGISTER_TO_PUBLICATION);
                    i.putExtra(FoodonetService.ADDRESS_ARGS,registrationArgs);
                    i.putExtra(FoodonetService.JSON_TO_SEND,registration);
                    getContext().startService(i);
                    break;
                case R.id.imageActionPublicationSMS:
                    // TODO: 14/11/2016 not working! test for adding a report, hard coded
//                    Snackbar.make(imageActionPublicationReport,"Currently not implemented",Snackbar.LENGTH_LONG).setAction("ACTION",null).show();
                    ReportFromServer reportFromServer = new ReportFromServer(-1,publication.getId(),publication.getVersion(),3,CommonMethods.getDeviceUUID(getContext()),
                            "","",String.valueOf(CommonMethods.getCurrentTimeSeconds()),user.getDisplayName(),
                            CommonMethods.getMyUserPhone(getContext()),CommonMethods.getMyUserID(getContext()),4);
                    String reportJson = reportFromServer.getAddReportJson().toString();
                    Log.d(TAG,"report json:"+reportJson);
                    i = new Intent(getContext(),FoodonetService.class);
                    i.putExtra(StartServiceMethods.ACTION_TYPE,StartServiceMethods.ACTION_ADD_REPORT);
                    String[] reportArgs = {String.valueOf(publication.getId())};
                    i.putExtra(FoodonetService.ADDRESS_ARGS,reportArgs);
                    i.putExtra(FoodonetService.JSON_TO_SEND,reportJson);
                    getContext().startService(i);
                    break;
                case R.id.imageActionPublicationPhone:
                    if (publication.getContactInfo().matches("[0-9]+") && publication.getContactInfo().length() > 2) {
                        i = new Intent(Intent.ACTION_VIEW, Uri.parse("tel:" + publication.getContactInfo()));
                        startActivity(i);
                    }
                    break;
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
            }
        }
    }

    public class GetReportsReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            /** receiver for reports got from the service */
            if(intent.getIntExtra(StartServiceMethods.ACTION_TYPE,-1)==StartServiceMethods.ACTION_GET_REPORTS){
                if(intent.getBooleanExtra(FoodonetService.SERVICE_ERROR,false)){
                    // TODO: 27/11/2016 add logic if fails
                    Toast.makeText(context, "service failed", Toast.LENGTH_SHORT).show();
                } else{
                    ArrayList<ReportFromServer> reports = intent.getParcelableArrayListExtra(ReportFromServer.REPORT_KEY);
                    adapter.updateReports(reports);
                }
            }
        }
    }
}
