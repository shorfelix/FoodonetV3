package com.roa.foodonetv3.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class PublicationReport implements Parcelable {
    private static final String TAG = "PublicationReport";

    public static final String REPORT_KEY = "publication_report";
    private static final String REPORT_PUBLICATION_ID = "publication_id";
    private static final String REPORT_PUBLICATION_VERSION = "publication_version";
    private static final String REPORT_REPORT = "report";
    private static final String REPORT_DATE_OF_REPORT = "date_of_report";
    private static final String REPORT_ACTIVE_DEVICE_DEV_UUID = "active_device_dev_uuid";
    private static final String REPORT_USER_NAME = "report_user_name";
    private static final String REPORT_CONTACT_INFO = "report_contact_info";
    private static final String REPORT_RATING = "rating";
    private static final String REPORT_REPORTER_USER_ID = "reporter_user_id";

    private long reportID, publicationID, reportUserID;
    private int publicationVersion, rating;
    private short reportType;
    private String active_device_dev_uuid,dateOfReport, reportUserName, reportContactInfo;// createdDate, updateDate, ;

    public PublicationReport(long reportID, long publicationID, int publicationVersion, short reportType, String active_device_dev_uuid,
                             //String createdDate, String updateDate,
                             String dateOfReport, String reportUserName,
                             String reportContactInfo, long reportUserID, int rating) {
        this.active_device_dev_uuid = active_device_dev_uuid;
//        this.createdDate = createdDate;
        this.dateOfReport = dateOfReport;
        this.publicationID = publicationID;
        this.publicationVersion = publicationVersion;
        this.rating = rating;
        this.reportContactInfo = reportContactInfo;
        this.reportID = reportID;
        this.reportType = reportType;
        this.reportUserID = reportUserID;
        this.reportUserName = reportUserName;
//        this.updateDate = updateDate;
    }

    protected PublicationReport(Parcel in) {
        reportID = in.readLong();
        publicationID = in.readLong();
        publicationVersion = in.readInt();
        reportType = (short) in.readInt();
        reportUserID = in.readInt();
        rating = in.readInt();
        active_device_dev_uuid = in.readString();
//        createdDate = in.readString();
//        updateDate = in.readString();
        dateOfReport = in.readString();
        reportUserName = in.readString();
        reportContactInfo = in.readString();
    }

    public static final Creator<PublicationReport> CREATOR = new Creator<PublicationReport>() {
        @Override
        public PublicationReport createFromParcel(Parcel in) {
            return new PublicationReport(in);
        }

        @Override
        public PublicationReport[] newArray(int size) {
            return new PublicationReport[size];
        }
    };

    /** creates a json object to be sent to the server */
    public JSONObject getAddReportJson() {
        /** creates a json object from the publication as to be sent to the server */
        JSONObject reportJsonRoot = new JSONObject();
        JSONObject reportJson = new JSONObject();
        try {
            reportJson.put(REPORT_PUBLICATION_ID, getPublicationID());
            reportJson.put(REPORT_PUBLICATION_VERSION, getPublicationVersion());
            reportJson.put(REPORT_REPORT, getReportType());
            reportJson.put(REPORT_DATE_OF_REPORT, getDateOfReport());
            reportJson.put(REPORT_ACTIVE_DEVICE_DEV_UUID,getActive_device_dev_uuid());
            reportJson.put(REPORT_USER_NAME,getReportUserName());
            reportJson.put(REPORT_CONTACT_INFO,getReportContactInfo());
            reportJson.put(REPORT_RATING,getRating());
            reportJson.put(REPORT_REPORTER_USER_ID,getReportUserID());

            reportJsonRoot.put(REPORT_KEY, reportJson);
        } catch (JSONException e) {
            Log.e(TAG,e.getMessage());
        }
        return reportJsonRoot;
    }

    public static float getRatingFromReports(ArrayList<PublicationReport> reports){
        if(reports.size()==0){
            return -1;
        }
        int sum = 0;
        for (int i = 0; i < reports.size(); i++) {
            sum+= reports.get(i).getRating();
        }
        return sum/(float)reports.size();
    }

    public String getActive_device_dev_uuid() {
        return active_device_dev_uuid;
    }

    public void setActive_device_dev_uuid(String active_device_dev_uuid) {
        this.active_device_dev_uuid = active_device_dev_uuid;
    }

//    public String getCreatedDate() {
//        return createdDate;
//    }
//
//    public void setCreatedDate(String createdDate) {
//        this.createdDate = createdDate;
//    }

    public String getDateOfReport() {
        return dateOfReport;
    }

    public void setDateOfReport(String dateOfReport) {
        this.dateOfReport = dateOfReport;
    }

    public long getPublicationID() {
        return publicationID;
    }

    public void setPublicationID(long publicationID) {
        this.publicationID = publicationID;
    }

    public int getPublicationVersion() {
        return publicationVersion;
    }

    public void setPublicationVersion(int publicationVersion) {
        this.publicationVersion = publicationVersion;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getReportContactInfo() {
        return reportContactInfo;
    }

    public void setReportContactInfo(String reportContactInfo) {
        this.reportContactInfo = reportContactInfo;
    }

    public long getReportID() {
        return reportID;
    }

    public void setReportID(long reportID) {
        this.reportID = reportID;
    }

    public short getReportType() {
        return reportType;
    }

    public void setReportType(short reportType) {
        this.reportType = reportType;
    }

    public long getReportUserID() {
        return reportUserID;
    }

    public void setReportUserID(long reportUserID) {
        this.reportUserID = reportUserID;
    }

    public String getReportUserName() {
        return reportUserName;
    }

    public void setReportUserName(String reportUserName) {
        this.reportUserName = reportUserName;
    }

//    public String getUpdateDate() {
//        return updateDate;
//    }
//
//    public void setUpdateDate(String updateDate) {
//        this.updateDate = updateDate;
//    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(reportID);
        dest.writeLong(publicationID);
        dest.writeInt(publicationVersion);
        dest.writeInt(reportType);
        dest.writeLong(reportUserID);
        dest.writeInt(rating);
        dest.writeString(active_device_dev_uuid);
//        dest.writeString(createdDate);
//        dest.writeString(updateDate);
        dest.writeString(dateOfReport);
        dest.writeString(reportUserName);
        dest.writeString(reportContactInfo);
    }
}
