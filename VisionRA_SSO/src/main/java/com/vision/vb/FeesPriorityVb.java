package com.vision.vb;

import java.util.ArrayList;
import java.util.List;

public class FeesPriorityVb extends CommonVb{

	private String country = "";
	private String leBook = "";
	private String dimensionName = "";
	private String businessLineId = "";
	private String businessLineDesc = "";
	private String effectiveDate = "";
	private int priorityOrder = 0 ;
	private int channelTypeAt = 7042 ;
	private String channelType = "";
	private String channelTypeDesc = "";
	private int weightage = 0 ;
	private String priorityType = "" ;
	List<SmartSearchVb> smartSearchOpt = null;
	private String columnAlias = "";
	List<FeesPriorityVb> feePriorityLst = new ArrayList<FeesPriorityVb>();
	
	public void setCountry(String country) {
		this.country = country; 
	}

	public String getCountry() {
		return country; 
	}
	public void setLeBook(String leBook) {
		this.leBook = leBook; 
	}

	public String getLeBook() {
		return leBook; 
	}
	public void setDimensionName(String dimensionName) {
		this.dimensionName = dimensionName; 
	}

	public String getDimensionName() {
		return dimensionName; 
	}
	public void setPriorityOrder(int priorityOrder) {
		this.priorityOrder = priorityOrder; 
	}

	public int getPriorityOrder() {
		return priorityOrder; 
	}
	public void setChannelType(String channelType) {
		this.channelType = channelType; 
	}

	public String getChannelType() {
		return channelType; 
	}
	public void setWeightage(int weightage) {
		this.weightage = weightage; 
	}

	public int getWeightage() {
		return weightage; 
	}

	public List<SmartSearchVb> getSmartSearchOpt() {
		return smartSearchOpt;
	}
	
	public void setSmartSearchOpt(List<SmartSearchVb> smartSearchOpt) {
		this.smartSearchOpt = smartSearchOpt;
	}

	public String getBusinessLineId() {
		return businessLineId;
	}

	public void setBusinessLineId(String businessLineId) {
		this.businessLineId = businessLineId;
	}

	public String getBusinessLineDesc() {
		return businessLineDesc;
	}

	public void setBusinessLineDesc(String businessLineDesc) {
		this.businessLineDesc = businessLineDesc;
	}

	public String getEffectiveDate() {
		return effectiveDate;
	}

	public void setEffectiveDate(String effectiveDate) {
		this.effectiveDate = effectiveDate;
	}

	public String getChannelTypeDesc() {
		return channelTypeDesc;
	}

	public void setChannelTypeDesc(String channelTypeDesc) {
		this.channelTypeDesc = channelTypeDesc;
	}

	public List<FeesPriorityVb> getFeePriorityLst() {
		return feePriorityLst;
	}

	public void setFeePriorityLst(List<FeesPriorityVb> feePriorityLst) {
		this.feePriorityLst = feePriorityLst;
	}

	public void setPriorityType(String priorityType) {
		this.priorityType = priorityType;
	}

	public String getPriorityType() {
		return priorityType;
	}

	public int getChannelTypeAt() {
		return channelTypeAt;
	}

	public void setChannelTypeAt(int channelTypeAt) {
		this.channelTypeAt = channelTypeAt;
	}

	public String getColumnAlias() {
		return columnAlias;
	}

	public void setColumnAlias(String columnAlias) {
		this.columnAlias = columnAlias;
	}

}