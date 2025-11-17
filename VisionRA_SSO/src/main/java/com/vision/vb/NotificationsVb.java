package com.vision.vb;

public class NotificationsVb extends CommonVb{
	private String sequence = "";
	private String notificationDate = "";
	private String cifID = "";
	private String cifName = "";
	private String visionSbu = "";
	private String visionOuc = "";
	private String DealNo = "";
	private String DealStage = "";
	private String actionDate = "";
	private String notificationType = "";
	private Boolean dealReminder = false;
	private String readFlag = "";
	
	public String getSequence() {
		return sequence;
	}
	public void setSequence(String sequence) {
		this.sequence = sequence;
	}
	public String getNotificationDate() {
		return notificationDate;
	}
	public void setNotificationDate(String notificationDate) {
		this.notificationDate = notificationDate;
	}
	public String getCifID() {
		return cifID;
	}
	public void setCifID(String cifID) {
		this.cifID = cifID;
	}
	public String getVisionSbu() {
		return visionSbu;
	}
	public void setVisionSbu(String visionSbu) {
		this.visionSbu = visionSbu;
	}
	public String getVisionOuc() {
		return visionOuc;
	}
	public void setVisionOuc(String visionOuc) {
		this.visionOuc = visionOuc;
	}
	public String getDealNo() {
		return DealNo;
	}
	public void setDealNo(String dealNo) {
		DealNo = dealNo;
	}
	public String getDealStage() {
		return DealStage;
	}
	public void setDealStage(String dealStage) {
		DealStage = dealStage;
	}
	public String getActionDate() {
		return actionDate;
	}
	public void setActionDate(String actionDate) {
		this.actionDate = actionDate;
	}
	public String getCifName() {
		return cifName;
	}
	public void setCifName(String cifName) {
		this.cifName = cifName;
	}
	public String getNotificationType() {
		return notificationType;
	}
	public void setNotificationType(String notificationType) {
		this.notificationType = notificationType;
	}
	public Boolean getDealReminder() {
		return dealReminder;
	}
	public void setDealReminder(Boolean dealReminder) {
		this.dealReminder = dealReminder;
	}
	public String getReadFlag() {
		return readFlag;
	}
	public void setReadFlag(String readFlag) {
		this.readFlag = readFlag;
	}
}
