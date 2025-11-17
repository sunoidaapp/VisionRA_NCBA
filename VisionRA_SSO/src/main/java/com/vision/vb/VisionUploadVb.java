package com.vision.vb;

import java.util.List;

public class VisionUploadVb extends CommonVb {

	private static final long serialVersionUID = 1L;
	private String tableName = "";
	private String fileName = "";
	private String uploadSequence = "";
	private String uploadDate = "";
	private int uploadStatusNt = 0;
	private int uploadStatus = -1;
	private int uploadSequenceOld = -1;
	private Boolean checkUploadInterval = false; 
	private String sheetName ="";
	private String verification = "";
	private String logFile = "";
	private String userId = "";
	List<SmartSearchVb> smartSearchOpt = null;
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getUploadSequence() {
		return uploadSequence;
	}
	public void setUploadSequence(String uploadSequence) {
		this.uploadSequence = uploadSequence;
	}
	public String getUploadDate() {
		return uploadDate;
	}
	public void setUploadDate(String uploadDate) {
		this.uploadDate = uploadDate;
	}
	public int getUploadStatusNt() {
		return uploadStatusNt;
	}
	public void setUploadStatusNt(int uploadStatusNt) {
		this.uploadStatusNt = uploadStatusNt;
	}
	public int getUploadStatus() {
		return uploadStatus;
	}
	public void setUploadStatus(int uploadStatus) {
		this.uploadStatus = uploadStatus;
	}
	public int getUploadSequenceOld() {
		return uploadSequenceOld;
	}
	public void setUploadSequenceOld(int uploadSequenceOld) {
		this.uploadSequenceOld = uploadSequenceOld;
	}
	public Boolean getCheckUploadInterval() {
		return checkUploadInterval;
	}
	public void setCheckUploadInterval(Boolean checkUploadInterval) {
		this.checkUploadInterval = checkUploadInterval;
	}
	public String getSheetName() {
		return sheetName;
	}
	public void setSheetName(String sheetName) {
		this.sheetName = sheetName;
	}
	public String getVerification() {
		return verification;
	}
	public void setVerification(String verification) {
		this.verification = verification;
	}
	public String getLogFile() {
		return logFile;
	}
	public void setLogFile(String logFile) {
		this.logFile = logFile;
	}
	public List<SmartSearchVb> getSmartSearchOpt() {
		return smartSearchOpt;
	}
	public void setSmartSearchOpt(List<SmartSearchVb> smartSearchOpt) {
		this.smartSearchOpt = smartSearchOpt;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	


}
