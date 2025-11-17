package com.vision.vb;

import java.util.List;

public class ConcessionHeaderDetailVb extends CommonVb{
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
	
	private String tranCcy = "";
	private String postingCcy = "";
	private String feeAmt = "";
	private String feePercentage = "";
	private int ccyConversionTypeAt = 1106;
	private String ccyConversionType = "";
	private String ccyConversionTypeDesc="";
	private int interestBasis = 365;
	private String feeBasis = "";
	private int roundOffBasisAt = 7072;
	private String roundOffBasis = "N";
	private String roundOffBasisDesc = "";
	private int roundOffBasisDecimal = 0;
	private int minMaxCcyTypeAt = 7074;
	private String minMaxCcyType = "";
	private String minMaxCcyTypeDesc = "";
	private String lookupAmountType = "";
	private String lookupAmountTypeAt = "7071";
	private String percentAmountType = "";
	private String percentAmountTypeAt = "7071";
	
	List<SmartSearchVb> smartSearchOpt = null;
	List<ConcessionConfigTierVb> concessionTierlst = null;
	
	public List<ConcessionConfigTierVb> getConcessionTierlst() {
		return concessionTierlst;
	}
	public void setConcessionTierlst(List<ConcessionConfigTierVb> concessionTierlst) {
		this.concessionTierlst = concessionTierlst;
	}
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
	public String getTranCcy() {
		return tranCcy;
	}
	public void setTranCcy(String tranCcy) {
		this.tranCcy = tranCcy;
	}
	public String getPostingCcy() {
		return postingCcy;
	}
	public void setPostingCcy(String postingCcy) {
		this.postingCcy = postingCcy;
	}
	public String getFeeAmt() {
		return feeAmt;
	}
	public void setFeeAmt(String feeAmt) {
		this.feeAmt = feeAmt;
	}
	public String getFeePercentage() {
		return feePercentage;
	}
	public void setFeePercentage(String feePercentage) {
		this.feePercentage = feePercentage;
	}
	public int getCcyConversionTypeAt() {
		return ccyConversionTypeAt;
	}
	public void setCcyConversionTypeAt(int ccyConversionTypeAt) {
		this.ccyConversionTypeAt = ccyConversionTypeAt;
	}
	public String getCcyConversionType() {
		return ccyConversionType;
	}
	public void setCcyConversionType(String ccyConversionType) {
		this.ccyConversionType = ccyConversionType;
	}
	public String getCcyConversionTypeDesc() {
		return ccyConversionTypeDesc;
	}
	public void setCcyConversionTypeDesc(String ccyConversionTypeDesc) {
		this.ccyConversionTypeDesc = ccyConversionTypeDesc;
	}
	public int getInterestBasis() {
		return interestBasis;
	}
	public void setInterestBasis(int interestBasis) {
		this.interestBasis = interestBasis;
	}
	public String getFeeBasis() {
		return feeBasis;
	}
	public void setFeeBasis(String feeBasis) {
		this.feeBasis = feeBasis;
	}
	public int getRoundOffBasisAt() {
		return roundOffBasisAt;
	}
	public void setRoundOffBasisAt(int roundOffBasisAt) {
		this.roundOffBasisAt = roundOffBasisAt;
	}
	public String getRoundOffBasis() {
		return roundOffBasis;
	}
	public void setRoundOffBasis(String roundOffBasis) {
		this.roundOffBasis = roundOffBasis;
	}
	public String getRoundOffBasisDesc() {
		return roundOffBasisDesc;
	}
	public void setRoundOffBasisDesc(String roundOffBasisDesc) {
		this.roundOffBasisDesc = roundOffBasisDesc;
	}
	public int getRoundOffBasisDecimal() {
		return roundOffBasisDecimal;
	}
	public void setRoundOffBasisDecimal(int roundOffBasisDecimal) {
		this.roundOffBasisDecimal = roundOffBasisDecimal;
	}
	public int getMinMaxCcyTypeAt() {
		return minMaxCcyTypeAt;
	}
	public void setMinMaxCcyTypeAt(int minMaxCcyTypeAt) {
		this.minMaxCcyTypeAt = minMaxCcyTypeAt;
	}
	public String getMinMaxCcyType() {
		return minMaxCcyType;
	}
	public void setMinMaxCcyType(String minMaxCcyType) {
		this.minMaxCcyType = minMaxCcyType;
	}
	public String getMinMaxCcyTypeDesc() {
		return minMaxCcyTypeDesc;
	}
	public void setMinMaxCcyTypeDesc(String minMaxCcyTypeDesc) {
		this.minMaxCcyTypeDesc = minMaxCcyTypeDesc;
	}
	public String getLookupAmountType() {
		return lookupAmountType;
	}
	public void setLookupAmountType(String lookupAmountType) {
		this.lookupAmountType = lookupAmountType;
	}
	public String getLookupAmountTypeAt() {
		return lookupAmountTypeAt;
	}
	public void setLookupAmountTypeAt(String lookupAmountTypeAt) {
		this.lookupAmountTypeAt = lookupAmountTypeAt;
	}
	public String getPercentAmountType() {
		return percentAmountType;
	}
	public void setPercentAmountType(String percentAmountType) {
		this.percentAmountType = percentAmountType;
	}
	public String getPercentAmountTypeAt() {
		return percentAmountTypeAt;
	}
	public void setPercentAmountTypeAt(String percentAmountTypeAt) {
		this.percentAmountTypeAt = percentAmountTypeAt;
	}
	public List<SmartSearchVb> getSmartSearchOpt() {
		return smartSearchOpt;
	}
	public void setSmartSearchOpt(List<SmartSearchVb> smartSearchOpt) {
		this.smartSearchOpt = smartSearchOpt;
	}
	public String getDiscountTypeDesc() {
		return discountTypeDesc;
	}
	public void setDiscountTypeDesc(String discountTypeDesc) {
		this.discountTypeDesc = discountTypeDesc;
	}
}
