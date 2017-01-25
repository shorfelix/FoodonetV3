package com.roa.foodonetv3.adapters;

import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.v4.util.LongSparseArray;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.bumptech.glide.Glide;
import com.google.android.gms.maps.model.LatLng;
import com.roa.foodonetv3.R;
import com.roa.foodonetv3.activities.PublicationActivity;
import com.roa.foodonetv3.commonMethods.CommonConstants;
import com.roa.foodonetv3.commonMethods.CommonMethods;
import com.roa.foodonetv3.db.PublicationsDBHandler;
import com.roa.foodonetv3.db.RegisteredUsersDBHandler;
import com.roa.foodonetv3.model.Publication;
import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

/** recycler adapter for publications */
public class PublicationsRecyclerAdapter extends RecyclerView.Adapter<PublicationsRecyclerAdapter.PublicationHolder> {
    private static final String TAG = "PubsRecyclerAdapter";

    private Context context;
    private ArrayList<Publication> filteredPublications = new ArrayList<>();
    private ArrayList<Publication> publications = new ArrayList<>();
    private LongSparseArray<Integer> registeredUsersArray = new LongSparseArray<>();
    private TransferUtility transferUtility;
    private LatLng userLatLng;
    private static final double LOCATION_NOT_FOUND = -9999;
    private PublicationsDBHandler publicationsDBHandler;

    public PublicationsRecyclerAdapter(Context context) {
        this.context = context;
        /** get the S3 utility */
        transferUtility = CommonMethods.getTransferUtility(context);
        userLatLng = new LatLng(Double.valueOf(PreferenceManager.getDefaultSharedPreferences(context).getString(CommonConstants.USER_LATITUDE,String.valueOf(LOCATION_NOT_FOUND))),
                Double.valueOf(PreferenceManager.getDefaultSharedPreferences(context).getString(CommonConstants.USER_LONGITUDE,String.valueOf(LOCATION_NOT_FOUND))));
    }

    /** updates the recycler */
    public void updatePublications(ArrayList<Publication> publications, LongSparseArray<Integer> registeredUsersArray){
        this.registeredUsersArray = registeredUsersArray;
        filteredPublications.clear();
        filteredPublications.addAll(publications);
        this.publications = publications;
        notifyDataSetChanged();
    }

    /** updates the recycler */
    public void updatePublications(int typeFilter){
        if(publicationsDBHandler == null){
            publicationsDBHandler = new PublicationsDBHandler(context);
        }
        RegisteredUsersDBHandler registeredUsersDBHandler = new RegisteredUsersDBHandler(context);
        ArrayList<Publication> publications = publicationsDBHandler.getPublications(typeFilter);
        registeredUsersArray = registeredUsersDBHandler.getAllRegisteredUsersCount();
        filteredPublications.clear();
        filteredPublications.addAll(publications);
        this.publications = publications;
        notifyDataSetChanged();
    }

    /** filter through the search in the action bar */
    public void filter(String text) {
        filteredPublications.clear();
        if(text.isEmpty()){
            filteredPublications.addAll(publications);
        } else{
            text = text.toLowerCase();
            for(Publication publication: publications){
                if(publication.getTitle().toLowerCase().contains(text) || publication.getAddress().toLowerCase().contains(text)){
                    filteredPublications.add(publication);
                }
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public PublicationHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.item_publication_list,parent,false);
        return new PublicationHolder(v);
    }

    @Override
    public void onBindViewHolder(PublicationHolder holder, int position) {
        holder.bindPublication(filteredPublications.get(position));
    }

    @Override
    public int getItemCount() {
        return filteredPublications.size();
    }

    class PublicationHolder extends RecyclerView.ViewHolder implements TransferListener, View.OnClickListener {
        private Publication publication;
        private ImageView imagePublicationGroup;
        private CircleImageView imagePublication;
        private TextView textPublicationTitle, textPublicationAddressDistance,textPublicationUsers;
        private File mCurrentPhotoFile;
        private int observerId;
        private int publicationImageSize;


        PublicationHolder(View itemView) {
            super(itemView);
            imagePublication = (CircleImageView) itemView.findViewById(R.id.imagePublication);
            imagePublicationGroup = (ImageView) itemView.findViewById(R.id.imagePublicationGroup);
            textPublicationTitle = (TextView) itemView.findViewById(R.id.textPublicationTitle);
            textPublicationAddressDistance = (TextView) itemView.findViewById(R.id.textPublicationAddressDistance);
            textPublicationUsers = (TextView) itemView.findViewById(R.id.textPublicationUsers);
            publicationImageSize = (int)context.getResources().getDimension(R.dimen.image_size_68);
            itemView.setOnClickListener(this);
        }

        private void bindPublication(Publication publication) {
            this.publication = publication;
            // TODO: add image logic, add distance logic, number of users who joined, currently hard coded
            textPublicationTitle.setText(publication.getTitle());
            if(userLatLng.latitude != LOCATION_NOT_FOUND && userLatLng.longitude != LOCATION_NOT_FOUND){
                double distance = CommonMethods.distance(userLatLng.latitude,userLatLng.longitude,publication.getLat(),publication.getLng());
                String addressDistance = String.format(Locale.US,"%1$s %2$s",CommonMethods.getRoundedStringFromNumber(distance),context.getResources().getString(R.string.km));
                textPublicationAddressDistance.setText(addressDistance);
            } else{
                textPublicationAddressDistance.setText("");
            }
            Integer numberRegisteredUsers = registeredUsersArray.get(publication.getId());
            if(numberRegisteredUsers== null){
                numberRegisteredUsers = 0;
            }
            String registeredUsers = String.format(Locale.US,"%1$d %2$s", numberRegisteredUsers,context.getResources().getString(R.string.users_joined));
            textPublicationUsers.setText(registeredUsers);
            //add photo here
            mCurrentPhotoFile = new File(CommonMethods.getPhotoPathByID(context,publication.getId(),publication.getVersion()));
            if(mCurrentPhotoFile.isFile()){
                Glide.with(context).load(mCurrentPhotoFile).centerCrop().into(imagePublication);
            } else{
                String imagePath = CommonMethods.getFileNameFromPublicationID(publication.getId(),publication.getVersion());
                TransferObserver observer = transferUtility.download(context.getResources().getString(R.string.amazon_publications_bucket),
                        imagePath, mCurrentPhotoFile);
                        observer.setTransferListener(this);
                        observerId = observer.getId();
            }
        }

        @Override
        public void onStateChanged(int id, TransferState state) {
            /** listener for the s3 server download, needs to be adapter wide since it's currently keeps using the same image in different layout */
            // TODO: 09/11/2016 check picasso adapter for the images and using the s3 observer on an adapter scale
            Log.d(TAG,"amazon onStateChanged " + id + " "  + state.toString());
            if(state == TransferState.COMPLETED){
                if(observerId==id){
                    Glide.with(context).load(mCurrentPhotoFile).centerCrop().into(imagePublication);
                }

            }
        }
        @Override
        public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
        }
        @Override
        public void onError(int id, Exception ex) {
            Log.d(TAG,"amazon onError" + id + " " + ex.toString());
        }

        @Override
        public void onClick(View v) {
            Intent i = new Intent(context, PublicationActivity.class);
            i.putExtra(PublicationActivity.ACTION_OPEN_PUBLICATION, PublicationActivity.PUBLICATION_DETAIL_TAG);
            i.putExtra(Publication.PUBLICATION_KEY,publication);
            context.startActivity(i);
        }
    }
}

