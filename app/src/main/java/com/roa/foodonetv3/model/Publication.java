package com.roa.foodonetv3.model;

/**
 * Created by Owner on 05/10/2016.
 */

public class Publication {
    private long id;
    private int version;
    private String title,subtitle,address,startingDate,endingDate,contactInfo;
    private short typeOfCollecting;
    private double lat, lng;
    private boolean isOnAir;
    /** stopped at isOnAir, needs to be completed */

    public Publication(long id, int version, String title, String subtitle, String address, short typeOfCollecting,
                       double lat, double lng, String startingDate, String endingDate, String contactInfo, boolean isOnAir) {
        this.id = id;
        this.version = version;
        this.title = title;
        this.subtitle = subtitle;
        this.address = address;
        this.typeOfCollecting = typeOfCollecting;
        this.lat = lat;
        this.lng = lng;
        this.startingDate = startingDate;
        this.endingDate = endingDate;
        this.contactInfo = contactInfo;
        this.isOnAir = isOnAir;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getStartingDate() {
        return startingDate;
    }

    public void setStartingDate(String startingDate) {
        this.startingDate = startingDate;
    }

    public String getEndingDate() {
        return endingDate;
    }

    public void setEndingDate(String endingDate) {
        this.endingDate = endingDate;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }

    public short getTypeOfCollecting() {
        return typeOfCollecting;
    }

    public void setTypeOfCollecting(short typeOfCollecting) {
        this.typeOfCollecting = typeOfCollecting;
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

    public boolean isOnAir() {
        return isOnAir;
    }

    public void setOnAir(boolean onAir) {
        isOnAir = onAir;
    }
}

