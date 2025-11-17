package com.vision.vb;

import java.util.List;

public class RaTellerFreeVb extends CommonVb{

	private String country = "";
	private String leBook = "";
	private String effectiveDateStart = "";
	private String effectiveDateEnd = "";
	private String customerId = "";
	private String customerName = "";
	private int tellerBucketAt = 7060;
	private String tellerBucket = "";
	private String tellerBucketDesc = "";
	private int	tellerFreeStatusNt = 1;
	private int	tellerFreeStatus = 0;
	private String tellerFreeStatusDesc = "";
	List<SmartSearchVb> smartSearchOpt = null;
	
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getLeBook() {
		return leBook;
	}
	public void setLeBook(String leBook) {
		this.leBook = leBook;
	}
	public String getEffectiveDateStart() {
		return effectiveDateStart;
	}
	public void setEffectiveDateStart(String effectiveDateStart) {
		this.effectiveDateStart = effectiveDateStart;
	}
	public String getEffectiveDateEnd() {
		return effectiveDateEnd;
	}
	public void setEffectiveDateEnd(String effectiveDateEnd) {
		this.effectiveDateEnd = effectiveDateEnd;
	}
	public String getCustomerId() {
		return customerId;
	}
	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}
	public String getCustomerName() {
		return customerName;
	}
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
	public int getTellerBucketAt() {
		return tellerBucketAt;
	}
	public void setTellerBucketAt(int tellerBucketAt) {
		this.tellerBucketAt = tellerBucketAt;
	}
	public String getTellerBucket() {
		return tellerBucket;
	}
	public void setTellerBucket(String tellerBucket) {
		this.tellerBucket = tellerBucket;
	}
	public String getTellerBucketDesc() {
		return tellerBucketDesc;
	}
	public void setTellerBucketDesc(String tellerBucketDesc) {
		this.tellerBucketDesc = tellerBucketDesc;
	}
	public int getTellerFreeStatusNt() {
		return tellerFreeStatusNt;
	}
	public void setTellerFreeStatusNt(int tellerFreeStatusNt) {
		this.tellerFreeStatusNt = tellerFreeStatusNt;
	}
	public int getTellerFreeStatus() {
		return tellerFreeStatus;
	}
	public void setTellerFreeStatus(int tellerFreeStatus) {
		this.tellerFreeStatus = tellerFreeStatus;
	}
	public String getTellerFreeStatusDesc() {
		return tellerFreeStatusDesc;
	}
	public void setTellerFreeStatusDesc(String tellerFreeStatusDesc) {
		this.tellerFreeStatusDesc = tellerFreeStatusDesc;
	}
	public List<SmartSearchVb> getSmartSearchOpt() {
		return smartSearchOpt;
	}
	public void setSmartSearchOpt(List<SmartSearchVb> smartSearchOpt) {
		this.smartSearchOpt = smartSearchOpt;
	}
}
