package com.roa.foodonetv3.model;

import android.os.Parcel;
import android.os.Parcelable;

public class SavedPlace implements Parcelable {
    private String address;
    private double lat, lng;

    public SavedPlace(String address, double lat, double lng) {
        this.address = address;
        this.lat = lat;
        this.lng = lng;
    }

    protected SavedPlace(Parcel in) {
        address = in.readString();
        lat = in.readDouble();
        lng = in.readDouble();
    }

    public static final Creator<SavedPlace> CREATOR = new Creator<SavedPlace>() {
        @Override
        public SavedPlace createFromParcel(Parcel in) {
            return new SavedPlace(in);
        }

        @Override
        public SavedPlace[] newArray(int size) {
            return new SavedPlace[size];
        }
    };

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(address);
        parcel.writeDouble(lat);
        parcel.writeDouble(lng);
    }
}
