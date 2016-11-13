package com.roa.foodonetv3.model;

/**
 * Created by roa on 13/11/2016.
 */

public class ReportFromServer {

    private long reportId, publicationId;
    private int publicationVersion, reportType, reportUserId, rating;
    private String active_device_dev_uuid, createdDate, updateDate, dateOfReport, reportUserName,
    reportContactInfo;

    public ReportFromServer(long reportId, long publicationId, int publicationVersion, int reportType, String active_device_dev_uuid,
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
}
