package com.vision.vb;

import java.util.List;

public class ConcessionConfigDetailsVb extends CommonVb{

	private String country = "";
	private String leBook = "";
	private String concessionId = "";
	private String effectiveDate = "";
	private String tranCcy = "";
	private String postingCcy = "";
	private String ccyConversionType = "";
	private int ccyConversionTypeAt = 1106;
	private String feeAmt = "";
	private String feePercentage = "";
	private int interestBasis = 365;
	private String feeType = "";
	private String feeBasis = "";
	private String feeBasisAt = "7032";
	private String lookupAmountType = "";
	private String lookupAmountTypeAt = "7071";
	private String percentAmountType = "";
	private String percentAmountTypeAt = "7071";
	private String transCCYDesc="";
	private String postingCCYDesc="";
	private String ccyConversionTypeDesc="";
	private String feesBasisDesc="";
	private String lookupAmountTypeDesc="";
	private String percentAmountTypeDesc="";
	private int roundOffBasisAt = 7072;
	private String roundOffBasis = "N";
	private String roundOffBasisDesc = "";
	private int roundOffBasisDecimal = 0;
	private String minMaxCcyType = "";
	private String minMaxCcyTypeDesc = "";
	
	List<ConcessionConfigTierVb> concessionTierlst = null;

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

	public String getEffectiveDate() {
		return effectiveDate;
	}

	public void setEffectiveDate(String effectiveDate) {
		this.effectiveDate = effectiveDate;
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

	public String getCcyConversionType() {
		return ccyConversionType;
	}

	public void setCcyConversionType(String ccyConversionType) {
		this.ccyConversionType = ccyConversionType;
	}

	public int getCcyConversionTypeAt() {
		return ccyConversionTypeAt;
	}

	public void setCcyConversionTypeAt(int ccyConversionTypeAt) {
		this.ccyConversionTypeAt = ccyConversionTypeAt;
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

	public String getFeeBasisAt() {
		return feeBasisAt;
	}

	public void setFeeBasisAt(String feeBasisAt) {
		this.feeBasisAt = feeBasisAt;
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

	public String getTransCCYDesc() {
		return transCCYDesc;
	}

	public void setTransCCYDesc(String transCCYDesc) {
		this.transCCYDesc = transCCYDesc;
	}

	public String getPostingCCYDesc() {
		return postingCCYDesc;
	}

	public void setPostingCCYDesc(String postingCCYDesc) {
		this.postingCCYDesc = postingCCYDesc;
	}

	public String getCcyConversionTypeDesc() {
		return ccyConversionTypeDesc;
	}

	public void setCcyConversionTypeDesc(String ccyConversionTypeDesc) {
		this.ccyConversionTypeDesc = ccyConversionTypeDesc;
	}

	public String getFeesBasisDesc() {
		return feesBasisDesc;
	}

	public void setFeesBasisDesc(String feesBasisDesc) {
		this.feesBasisDesc = feesBasisDesc;
	}

	public String getLookupAmountTypeDesc() {
		return lookupAmountTypeDesc;
	}

	public void setLookupAmountTypeDesc(String lookupAmountTypeDesc) {
		this.lookupAmountTypeDesc = lookupAmountTypeDesc;
	}

	public String getPercentAmountTypeDesc() {
		return percentAmountTypeDesc;
	}

	public void setPercentAmountTypeDesc(String percentAmountTypeDesc) {
		this.percentAmountTypeDesc = percentAmountTypeDesc;
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

	public List<ConcessionConfigTierVb> getConcessionTierlst() {
		return concessionTierlst;
	}

	public void setConcessionTierlst(List<ConcessionConfigTierVb> concessionTierlst) {
		this.concessionTierlst = concessionTierlst;
	}

	public String getFeeType() {
		return feeType;
	}

	public void setFeeType(String feeType) {
		this.feeType = feeType;
	}
	
	
}