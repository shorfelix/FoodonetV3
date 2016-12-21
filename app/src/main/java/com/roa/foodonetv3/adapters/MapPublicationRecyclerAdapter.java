package com.roa.foodonetv3.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.google.android.gms.maps.model.LatLng;
import com.roa.foodonetv3.R;
import com.roa.foodonetv3.commonMethods.CommonMethods;
import com.roa.foodonetv3.model.Publication;
import com.squareup.picasso.Picasso;
import java.io.File;
import java.util.ArrayList;

public class MapPublicationRecyclerAdapter extends RecyclerView.Adapter<MapPublicationRecyclerAdapter.PublicationHolder> {

    private static final String TAG = "MapPubsRecyclerAdapter";
    private static final int publicationImageSize = 120;
    private Context context;
    private ArrayList<Publication> publications = new ArrayList<>();
    private TransferUtility transferUtility;
    private OnImageAdapterClickListener listener;


    public MapPublicationRecyclerAdapter(Context context){
        this.context = context;
        transferUtility = CommonMethods.getTransferUtility(context);
        listener = (OnImageAdapterClickListener) context;

    }

    public void updatePublications(ArrayList<Publication> publications){
        this.publications = publications;
        notifyDataSetChanged();
    }

    @Override
    public PublicationHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.map_recycler_adapter, parent, false);
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

        private ImageView mapRecyclerImageView;
        private File mCurrentPhotoFile;
        private int observerId;

        PublicationHolder(View itemView) {
            super(itemView);
            mapRecyclerImageView = (ImageView) itemView.findViewById(R.id.mapRecyclerImageView);
            mapRecyclerImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onImageAdapterClicked(new LatLng(publications.get(getLayoutPosition()).getLat(),publications.get(getLayoutPosition()).getLng()));
                }
            });
        }

        void bindPublication(Publication publication){
            //add photo here
            if(publication.getPhotoURL().equals("")){
                /** no image saved, display default image */
                Picasso.with(context)
                        .load(R.drawable.foodonet_image)
                        .resize(publicationImageSize,publicationImageSize)
                        .centerCrop()
                        .into(mapRecyclerImageView);
                // TODO: 10/11/2016 display default image

            }else{
                /** there is an image available to download or is currently on the device */
                // TODO: 10/11/2016 check version of the publication as well
                /** check if the image is already saved on the device */
                mCurrentPhotoFile = new File(CommonMethods.getPhotoPathByID(context,publication.getId()));
                if (mCurrentPhotoFile.isFile()) {
                    /** image was found and is the same as the publication id */

                    Picasso.with(context)
                            .load(mCurrentPhotoFile)
                            .resize(publicationImageSize,publicationImageSize)
                            .centerCrop()
                            .into(mapRecyclerImageView);
                } else {
                    /** image ready to download, not on the device */
                    TransferObserver observer = transferUtility.download(context.getResources().getString(R.string.amazon_bucket),
                            publication.getPhotoURL(), mCurrentPhotoFile
                    );
                    observer.setTransferListener(this);
                    observerId = observer.getId();
                }
            }
        }

        @Override
        public void onStateChanged(int id, TransferState state) {
            /** listener for the s3 server download, needs to be class wide since it's currently keeps using the same image in different layout */
            // TODO: 09/11/2016 check picasso adapter for the images and using the s3 observer on an adapter scale
            Log.d(TAG,"amazon onStateChanged " + id + " "  + state.toString());
            if(state == TransferState.COMPLETED){
                if(observerId==id){
                    Picasso.with(context)
                            .load(mCurrentPhotoFile)
                            .resize(publicationImageSize,publicationImageSize)
                            .centerCrop()
                            .into(mapRecyclerImageView);
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

    }
    public interface OnImageAdapterClickListener{
        void onImageAdapterClicked(LatLng latLng);
    }
}
