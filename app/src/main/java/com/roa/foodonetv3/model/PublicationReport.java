package com.roa.foodonetv3.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;

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

    private long reportId, publicationId;
    private int publicationVersion, reportType, reportUserId, rating;
    private String active_device_dev_uuid, createdDate, updateDate, dateOfReport, reportUserName,
    reportContactInfo;

    public PublicationReport(long reportId, long publicationId, int publicationVersion, int reportType, String active_device_dev_uuid,
                             String createdDate, String updateDate, String dateOfReport, String reportUserName,
                             String reportContactInfo, int reportUserId, int rating) {
        this.active_device_dev_uuid = active_device_dev_uuid;
        this.createdDate = createdDate;
        this.dateOfReport = dateOfReport;
        this.publicationId = publicationId;
        this.publicationVersion = publicationVersion;
        this.rating = rating;
        this.reportContactInfo = reportContactInfo;
        this.reportId = reportId;
        this.reportType = reportType;
        this.reportUserId = reportUserId;
        this.reportUserName = reportUserName;
        this.updateDate = updateDate;
    }

    protected PublicationReport(Parcel in) {
        reportId = in.readLong();
        publicationId = in.readLong();
        publicationVersion = in.readInt();
        reportType = in.readInt();
        reportUserId = in.readInt();
        rating = in.readInt();
        active_device_dev_uuid = in.readString();
        createdDate = in.readString();
        updateDate = in.readString();
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
            reportJson.put(REPORT_PUBLICATION_ID, getPublicationId());
            reportJson.put(REPORT_PUBLICATION_VERSION, getPublicationVersion());
            reportJson.put(REPORT_REPORT, getReportType());
            reportJson.put(REPORT_DATE_OF_REPORT, getDateOfReport());
            reportJson.put(REPORT_ACTIVE_DEVICE_DEV_UUID,getActive_device_dev_uuid());
            reportJson.put(REPORT_USER_NAME,getReportUserName());
            reportJson.put(REPORT_CONTACT_INFO,getReportContactInfo());
            reportJson.put(REPORT_RATING,getRating());
            reportJson.put(REPORT_REPORTER_USER_ID,getReportUserId());

            reportJsonRoot.put(REPORT_KEY, reportJson);
        } catch (JSONException e) {
            Log.e(TAG,e.getMessage());
        }
        return reportJsonRoot;
    }

    public String getActive_device_dev_uuid() {
        return active_device_dev_uuid;
    }

    public void setActive_device_dev_uuid(String active_device_dev_uuid) {
        this.active_device_dev_uuid = active_device_dev_uuid;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getDateOfReport() {
        return dateOfReport;
    }

    public void setDateOfReport(String dateOfReport) {
        this.dateOfReport = dateOfReport;
    }

    public long getPublicationId() {
        return publicationId;
    }

    public void setPublicationId(long publicationId) {
        this.publicationId = publicationId;
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

    public long getReportId() {
        return reportId;
    }

    public void setReportId(long reportId) {
        this.reportId = reportId;
    }

    public int getReportType() {
        return reportType;
    }

    public void setReportType(int reportType) {
        this.reportType = reportType;
    }

    public int getReportUserId() {
        return reportUserId;
    }

    public void setReportUserId(int reportUserId) {
        this.reportUserId = reportUserId;
    }

    public String getReportUserName() {
        return reportUserName;
    }

    public void setReportUserName(String reportUserName) {
        this.reportUserName = reportUserName;
    }

    public String getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(reportId);
        dest.writeLong(publicationId);
        dest.writeInt(publicationVersion);
        dest.writeInt(reportType);
        dest.writeInt(reportUserId);
        dest.writeInt(rating);
        dest.writeString(active_device_dev_uuid);
        dest.writeString(createdDate);
        dest.writeString(updateDate);
        dest.writeString(dateOfReport);
        dest.writeString(reportUserName);
        dest.writeString(reportContactInfo);
    }
}
