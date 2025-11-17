package com.vision.vb;

import java.util.List;

public class FeesConfigHeaderVb extends CommonVb{
	private String country = "";
	private String leBook = "";
	private String businessLineId = "";
	private String businessLineIdDesc = "";
	
	private String effectiveDate = "";
	private String rateEffectiveDate = "";
	private int feesLineStatusNt = 1;
	private int feesLineStatus = 0 ;
	private String feesLineStatusDesc = "";
	
	private int feeBasisAt = 7032;
	private String feeBasis = "";
	private String feeBasisDesc = "";
	private int feeTypeAt = 7033;
	private String feeType = "";
	private int tierTypeAt = 7034;
	private String tierType = "";
	private String transLineId = "";
	private int refNo = 0;
	private String feeTypeDesc = "";
	private String tierTypeDesc = "";
	
	private String rateEffectiveEndDate="";
	
	List<SmartSearchVb> smartSearchOpt = null;
	List<FeesConfigDetailsVb> feesConfigDetaillst = null;
	
	private String postingCcy = "";
	private String pricing = "";
	private boolean feesFlag = false;
	
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
	public String getBusinessLineId() {
		return businessLineId;
	}
	public void setBusinessLineId(String businessLineId) {
		this.businessLineId = businessLineId;
	}
	public String getBusinessLineIdDesc() {
		return businessLineIdDesc;
	}
	public void setBusinessLineIdDesc(String businessLineIdDesc) {
		this.businessLineIdDesc = businessLineIdDesc;
	}
	public String getEffectiveDate() {
		return effectiveDate;
	}
	public void setEffectiveDate(String effectiveDate) {
		this.effectiveDate = effectiveDate;
	}
	public int getFeesLineStatusNt() {
		return feesLineStatusNt;
	}
	public void setFeesLineStatusNt(int feesLineStatusNt) {
		this.feesLineStatusNt = feesLineStatusNt;
	}
	public int getFeesLineStatus() {
		return feesLineStatus;
	}
	public void setFeesLineStatus(int feesLineStatus) {
		this.feesLineStatus = feesLineStatus;
	}
	public String getFeesLineStatusDesc() {
		return feesLineStatusDesc;
	}
	public void setFeesLineStatusDesc(String feesLineStatusDesc) {
		this.feesLineStatusDesc = feesLineStatusDesc;
	}
	public int getFeeBasisAt() {
		return feeBasisAt;
	}
	public void setFeeBasisAt(int feeBasisAt) {
		this.feeBasisAt = feeBasisAt;
	}
	public String getFeeBasis() {
		return feeBasis;
	}
	public void setFeeBasis(String feeBasis) {
		this.feeBasis = feeBasis;
	}
	public String getFeeBasisDesc() {
		return feeBasisDesc;
	}
	public void setFeeBasisDesc(String feeBasisDesc) {
		this.feeBasisDesc = feeBasisDesc;
	}
	public int getFeeTypeAt() {
		return feeTypeAt;
	}
	public void setFeeTypeAt(int feeTypeAt) {
		this.feeTypeAt = feeTypeAt;
	}
	public String getFeeType() {
		return feeType;
	}
	public void setFeeType(String feeType) {
		this.feeType = feeType;
	}
	public int getTierTypeAt() {
		return tierTypeAt;
	}
	public void setTierTypeAt(int tierTypeAt) {
		this.tierTypeAt = tierTypeAt;
	}
	public String getTierType() {
		return tierType;
	}
	public void setTierType(String tierType) {
		this.tierType = tierType;
	}
	public String getTransLineId() {
		return transLineId;
	}
	public void setTransLineId(String transLineId) {
		this.transLineId = transLineId;
	}
	public List<SmartSearchVb> getSmartSearchOpt() {
		return smartSearchOpt;
	}
	public void setSmartSearchOpt(List<SmartSearchVb> smartSearchOpt) {
		this.smartSearchOpt = smartSearchOpt;
	}
	public List<FeesConfigDetailsVb> getFeesConfigDetaillst() {
		return feesConfigDetaillst;
	}
	public void setFeesConfigDetaillst(List<FeesConfigDetailsVb> feesConfigDetaillst) {
		this.feesConfigDetaillst = feesConfigDetaillst;
	}
	public int getRefNo() {
		return refNo;
	}
	public void setRefNo(int refNo) {
		this.refNo = refNo;
	}
	public String getRateEffectiveDate() {
		return rateEffectiveDate;
	}
	public void setRateEffectiveDate(String rateEffectiveDate) {
		this.rateEffectiveDate = rateEffectiveDate;
	}
	public String getFeeTypeDesc() {
		return feeTypeDesc;
	}
	public void setFeeTypeDesc(String feeTypeDesc) {
		this.feeTypeDesc = feeTypeDesc;
	}
	public String getTierTypeDesc() {
		return tierTypeDesc;
	}
	public void setTierTypeDesc(String tierTypeDesc) {
		this.tierTypeDesc = tierTypeDesc;
	}
	public String getPostingCcy() {
		return postingCcy;
	}
	public void setPostingCcy(String postingCcy) {
		this.postingCcy = postingCcy;
	}
	public String getPricing() {
		return pricing;
	}
	public void setPricing(String pricing) {
		this.pricing = pricing;
	}
	public String getRateEffectiveEndDate() {
		return rateEffectiveEndDate;
	}
	public void setRateEffectiveEndDate(String rateEffectiveEndDate) {
		this.rateEffectiveEndDate = rateEffectiveEndDate;
	}
	public boolean isFeesFlag() {
		return feesFlag;
	}
	public void setFeesFlag(boolean feesFlag) {
		this.feesFlag = feesFlag;
	}
	
}
