package com.vision.vb;

import java.util.List;

public class ConcessionConfigHeaderVb extends CommonVb{
	
	private String country = "";
	private String leBook = "";
	private String concessionId = "";
	private String concessionDesc = "";
	private String effectiveDate = "";
	private String endDate = "";
	private String concessionAmountCalc = "";
	private int discountTypeAT = 7032;
	private String discountType = "";
	private String discountTypeDesc = "";
	private int feeTypeAt = 7033;
	private String feeType = "";
	private int tierTypeAt = 7034;
	private String tierType = "";
	private String activityLinkFlag = "N";
	private int concessionStatusNt = 1;
	private int concessionStatus = 0;
	private String concessionStatusDesc = "";
	private String feeTypeDesc = "";
	private String tierTypeDesc = "";
	private String activityId = "";
	

	List<SmartSearchVb> smartSearchOpt = null;

	List<ConcessionConfigDetailsVb> concessionConfigDetaillst = null;
	List<ConcessionFilterVb> concessionFilterlst = null;
	List<ConcessionConfigHeaderVb> concessionActivitylst = null;

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
	public String getConcessionId() {
		return concessionId;
	}
	public void setConcessionId(String concessionId) {
		this.concessionId = concessionId;
	}
	public String getConcessionDesc() {
		return concessionDesc;
	}
	public void setConcessionDesc(String concessionDesc) {
		this.concessionDesc = concessionDesc;
	}
	public String getEffectiveDate() {
		return effectiveDate;
	}
	public void setEffectiveDate(String effectiveDate) {
		this.effectiveDate = effectiveDate;
	}
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	public String getConcessionAmountCalc() {
		return concessionAmountCalc;
	}
	public void setConcessionAmountCalc(String concessionAmountCalc) {
		this.concessionAmountCalc = concessionAmountCalc;
	}
	public int getDiscountTypeAT() {
		return discountTypeAT;
	}
	public void setDiscountTypeAT(int discountTypeAT) {
		this.discountTypeAT = discountTypeAT;
	}
	public String getDiscountType() {
		return discountType;
	}
	public void setDiscountType(String discountType) {
		this.discountType = discountType;
	}
	public String getDiscountTypeDesc() {
		return discountTypeDesc;
	}
	public void setDiscountTypeDesc(String discountTypeDesc) {
		this.discountTypeDesc = discountTypeDesc;
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
	public String getActivityLinkFlag() {
		return activityLinkFlag;
	}
	public void setActivityLinkFlag(String activityLinkFlag) {
		this.activityLinkFlag = activityLinkFlag;
	}
	public int getConcessionStatusNt() {
		return concessionStatusNt;
	}
	public void setConcessionStatusNt(int concessionStatusNt) {
		this.concessionStatusNt = concessionStatusNt;
	}
	public int getConcessionStatus() {
		return concessionStatus;
	}
	public void setConcessionStatus(int concessionStatus) {
		this.concessionStatus = concessionStatus;
	}
	public String getConcessionStatusDesc() {
		return concessionStatusDesc;
	}
	public void setConcessionStatusDesc(String concessionStatusDesc) {
		this.concessionStatusDesc = concessionStatusDesc;
	}
	
	public List<SmartSearchVb> getSmartSearchOpt() {
		return smartSearchOpt;
	}
	public void setSmartSearchOpt(List<SmartSearchVb> smartSearchOpt) {
		this.smartSearchOpt = smartSearchOpt;
	}
	public List<ConcessionConfigDetailsVb> getConcessionConfigDetaillst() {
		return concessionConfigDetaillst;
	}
	public void setConcessionConfigDetaillst(List<ConcessionConfigDetailsVb> concessionConfigDetaillst) {
		this.concessionConfigDetaillst = concessionConfigDetaillst;
	}
	public List<ConcessionFilterVb> getConcessionFilterlst() {
		return concessionFilterlst;
	}
	public void setConcessionFilterlst(List<ConcessionFilterVb> concessionFilterlst) {
		this.concessionFilterlst = concessionFilterlst;
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
	public String getActivityId() {
		return activityId;
	}
	public void setActivityId(String activityId) {
		this.activityId = activityId;
	}
	

	public List<ConcessionConfigHeaderVb> getConcessionActivitylst() {
		return concessionActivitylst;
	}
	public void setConcessionActivitylst(List<ConcessionConfigHeaderVb> concessionActivitylst) {
		this.concessionActivitylst = concessionActivitylst;
	}
}