package com.roa.foodonetv3.adapters;

import android.content.Context;
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
import com.roa.foodonetv3.R;
import com.roa.foodonetv3.commonMethods.CommonMethods;
import com.roa.foodonetv3.model.Publication;
import com.squareup.picasso.Picasso;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class PublicationsRecyclerAdapter extends RecyclerView.Adapter<PublicationsRecyclerAdapter.PublicationHolder> {
    /** recycler adapter for publication */
    private static final String TAG = "PubsRecyclerAdapter";

    private Context context;
    private ArrayList<Publication> publications = new ArrayList<>();
    private TransferUtility transferUtility;

    public PublicationsRecyclerAdapter(Context context) {
        this.context = context;
        transferUtility = CommonMethods.getTransferUtility(context);
    }

    public void updatePublications(ArrayList<Publication> publications){
        this.publications = publications;
        notifyDataSetChanged();
    }

    @Override
    public PublicationHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.publication_list_item,parent,false);
        return new PublicationHolder(v);
    }

    @Override
    public void onBindViewHolder(PublicationHolder holder, int position) {
        holder.bindPublication(publications.get(position));
    }

    @Override
    public int getItemCount() {
        return publications.size();
    }

    class PublicationHolder extends RecyclerView.ViewHolder implements TransferListener {
        private Publication publication;
        private ImageView imagePublication,imagePublicationGroup;
        private TextView textPublicationTitle, textPublicationAddressDistance;
        private File mCurrentPhotoFile;


        PublicationHolder(View itemView) {
            super(itemView);
            imagePublication = (ImageView) itemView.findViewById(R.id.imagePublication);
            imagePublicationGroup = (ImageView) itemView.findViewById(R.id.imagePublicationGroup);
            textPublicationTitle = (TextView) itemView.findViewById(R.id.textPublicationTitle);
            textPublicationAddressDistance = (TextView) itemView.findViewById(R.id.textPublicationAddressDistance);
        }

        private void bindPublication(Publication publication) {
            this.publication = publication;
            // TODO: add image logic, add distance logic, number of users who joined, currently hard coded
            textPublicationTitle.setText(publication.getTitle());
            String addressDistance = CommonMethods.getRoundedStringFromNumber(15.7f);
            textPublicationAddressDistance.setText(addressDistance);
            if(!publication.getPhotoURL().equals("")){
                try {
                    mCurrentPhotoFile = CommonMethods.createImageFile(context,publication.getId());
                    TransferObserver observer = transferUtility.download(context.getResources().getString(R.string.amazon_bucket),
                            publication.getPhotoURL(), mCurrentPhotoFile
                            );
                    observer.setTransferListener(this);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onStateChanged(int id, TransferState state) {
            /** listener for the s3 server download, needs to be adapter wide since it's currently keeps using the same image in different layout */
            // TODO: 09/11/2016 check picasso adapter for the images and using the s3 observer on an adapter scale
            Log.d(TAG,"amazon onStateChanged" + id + " "  + state.toString());
            if(state == TransferState.COMPLETED){
                Picasso.with(context)
                        .load(mCurrentPhotoFile)
                        .resize(imagePublication.getWidth(),imagePublication.getHeight())
                        .centerCrop()
                        .into(imagePublication);
            }
        }
        @Override
        public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
//            Log.d(TAG,"amazon onProgressChanged" + id + " " + bytesCurrent+ "/" + bytesTotal);
        }
        @Override
        public void onError(int id, Exception ex) {
            Log.d(TAG,"amazon onError" + id + " " + ex.toString());
        }
    }
}

