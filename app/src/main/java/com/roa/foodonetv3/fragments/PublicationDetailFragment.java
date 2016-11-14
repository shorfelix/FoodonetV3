package com.roa.foodonetv3.fragments;


import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.roa.foodonetv3.R;
import com.roa.foodonetv3.activities.MainDrawerActivity;
import com.roa.foodonetv3.commonMethods.CommonMethods;
import com.roa.foodonetv3.model.Publication;
import com.roa.foodonetv3.model.ReportFromServer;
import com.roa.foodonetv3.services.AddReportService;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.nio.charset.MalformedInputException;
import java.util.Locale;

public class PublicationDetailFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "PublicationDetailFrag";
    private TextView textCategory,textTimeRemaining,textJoined,textTitlePublication,textPublicationAddress,textPublicationRating,textPublisherName,textPublicationPrice,textPublicationDetails;
    private ImageView imagePicturePublication,imagePublisherUser,imageActionPublicationJoin,imageActionPublicationReport,imageActionPublicationPhone,imageActionPublicationMap;
    private Publication publication;


    public PublicationDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        publication = getArguments().getParcelable(Publication.PUBLICATION_KEY);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_publication_detail, container, false);

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
        imagePublisherUser = (ImageView) v.findViewById(R.id.imagePublisherUser);
        imageActionPublicationJoin = (ImageView) v.findViewById(R.id.imageActionPublicationJoin);
        imageActionPublicationReport = (ImageView) v.findViewById(R.id.imageActionPublicationReport);
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
        super.onResume();
        initViews();
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
        String timeRemaining = "10h 25min";
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
        switch (v.getId()){
            case R.id.imageActionPublicationJoin:
                // TODO: 13/11/2016 add join logic
                break;
            case R.id.imageActionPublicationReport:
                ReportFromServer reportFromServer = new ReportFromServer(-1,publication.getId(),publication.getVersion(),3,publication.getActiveDeviceDevUUID(),
                        "","",String.valueOf(System.currentTimeMillis()), MainDrawerActivity.getFireBaseUser().getDisplayName(),
                        "0500000000",999,4);
                String reportJson = reportFromServer.getAddReportJson().toString();
                Log.d(TAG,"report json:"+reportJson);
                i = new Intent(getContext(),AddReportService.class);
                i.putExtra(ReportFromServer.REPORT_KEY,reportJson);
                i.putExtra(Publication.PUBLICATION_UNIQUE_ID_KEY,publication.getId());
                getContext().startService(i);
                break;
            case R.id.imageActionPublicationPhone:
                if (publication.getContactInfo().matches("[0-9]+") && publication.getContactInfo().length() > 2) {
                    i = new Intent(Intent.ACTION_VIEW, Uri.parse("tel:" + publication.getContactInfo()));
                    startActivity(i);
                }
                break;
            case R.id.imageActionPublicationMap:
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
