package com.vision.vb;

import java.util.List;

public class FeesConfigDetailsVb extends CommonVb{
	private String country = "";
	private String leBook = "";
	private String businessLineId = "";
	private int feeConfigTypeAt = 7029;
	private String feeConfigType="";
	private String feeConfigTypeDesc="";
	private String feeConfigCode = "";
	private String effectiveDate = "";
	private int productIdAt = 171;
	private String productId = "";
	private String productType = "";
	private String tranCcy = "";
	private String feeAmt = "";
	private String feePercentage = "";
	private String minFee= "";
	private String maxFee= "";
	private String feeType = "";
	private String channelCode = "";
	private String businessVertical = "";
	private String contractId = "";
	private String customerId = "";
	private String feeDetailAttribute1 = "";
	private String postingCcy = "";
	private String ccyConversionType = "";
	private int ccyConversionTypeAt = 1106;
	private int interestBasis = 365;
	private String feeBasis = "";
	private int refNo = 0;
	
	List<FeesConfigTierVb> feesTierlst = null;
	
	private String feesDetailsAttribute1 = "";
	private String feesDetailsAttribute2 = "";
	private String feesDetailsAttribute3 = "";
	private String feesDetailsAttribute4 = "";
	private String feesDetailsAttribute5 = "";
	private String feesDetailsAttribute6 = "";
	private String feesDetailsAttribute7 = "";
	private String feesDetailsAttribute8 = "";
	private String feesDetailsAttribute9 = "";
	private String feesDetailsAttribute10 = "";
	private String feesDetailsAttribute11 = "";
	private String feesDetailsAttribute12 = "";
	private String feesDetailsAttribute13 = "";
	private String feesDetailsAttribute14 = "";
	private String feesDetailsAttribute15 = "";
	private String feesDetailsAttribute16 = "";
	private String feesDetailsAttribute17 = "";
	private String feesDetailsAttribute18 = "";
	private String feesDetailsAttribute19 = "";
	private String feesDetailsAttribute20 = "";

	private String lookupAmountType = "";
	private String lookupAmountTypeAt = "7071";
	private String percentAmountType = "";
	private String percentAmountTypeAt = "7071";
	private String channelCodeDesc="";
	private String businessVerticalDesc="";
	private String productTypeDesc="";
	private String productIdDesc="";
	private String transCCYDesc="";
	private String postingCCYDesc="";
	private String ccyConversionTypeDesc="";
	private String feesBasisDesc="";
	private String lookupAmountTypeDesc="";
	private String percentAmountTypeDesc="";
	private String channelType = "";
	private String channelTypeDesc = "";
	private int roundOffBasisAt = 7072;
	private String roundOffBasis = "N";
	private String roundOffBasisDesc = "";
	private int roundOffBasisDecimal = 0;
	private String minMaxCcyType = "";
	private String minMaxCcyTypeDesc = "";
	private String rateEffectiveDate = "";
	private int feeDetailStatusNt = 1;
	private int feeDetailStatus = 0;
	private String feeDetailStatusDesc = "";
	private String contractName = "";
	private String customerName = "";
	List<SmartSearchVb> smartSearchOpt = null;
	private String pricing="";
	private String pricingDesc="";
	private int pricingAt=7087;
	private String rateEffectiveEndDate="";
	public String getChannelCodeDesc() {
		return channelCodeDesc;
	}
	public void setChannelCodeDesc(String channelCodeDesc) {
		this.channelCodeDesc = channelCodeDesc;
	}
	public String getBusinessVerticalDesc() {
		return businessVerticalDesc;
	}
	public void setBusinessVerticalDesc(String businessVerticalDesc) {
		this.businessVerticalDesc = businessVerticalDesc;
	}
	public String getProductTypeDesc() {
		return productTypeDesc;
	}
	public void setProductTypeDesc(String productTypeDesc) {
		this.productTypeDesc = productTypeDesc;
	}
	public String getProductIdDesc() {
		return productIdDesc;
	}
	public void setProductIdDesc(String productIdDesc) {
		this.productIdDesc = productIdDesc;
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
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getBusinessLineId() {
		return businessLineId;
	}
	public void setBusinessLineId(String businessLineId) {
		this.businessLineId = businessLineId;
	}
	public int getProductIdAt() {
		return productIdAt;
	}
	public void setProductIdAt(int productIdAt) {
		this.productIdAt = productIdAt;
	}
	public String getProductId() {
		return productId;
	}
	public void setProductId(String productId) {
		this.productId = productId;
	}
	public String getTranCcy() {
		return tranCcy;
	}
	public void setTranCcy(String tranCcy) {
		this.tranCcy = tranCcy;
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
	public String getMinFee() {
		return minFee;
	}
	public void setMinFee(String minFee) {
		this.minFee = minFee;
	}
	public String getMaxFee() {
		return maxFee;
	}
	public void setMaxFee(String maxFee) {
		this.maxFee = maxFee;
	}
	public List<FeesConfigTierVb> getFeesTierlst() {
		return feesTierlst;
	}
	public void setFeesTierlst(List<FeesConfigTierVb> feesTierlst) {
		this.feesTierlst = feesTierlst;
	}
	public String getLeBook() {
		return leBook;
	}
	public void setLeBook(String leBook) {
		this.leBook = leBook;
	}
	public String getProductType() {
		return productType;
	}
	public void setProductType(String productType) {
		this.productType = productType;
	}
	public int getFeeConfigTypeAt() {
		return feeConfigTypeAt;
	}
	public void setFeeConfigTypeAt(int feeConfigTypeAt) {
		this.feeConfigTypeAt = feeConfigTypeAt;
	}
	public String getFeeConfigType() {
		return feeConfigType;
	}
	public void setFeeConfigType(String feeConfigType) {
		this.feeConfigType = feeConfigType;
	}
	public String getFeeConfigTypeDesc() {
		return feeConfigTypeDesc;
	}
	public void setFeeConfigTypeDesc(String feeConfigTypeDesc) {
		this.feeConfigTypeDesc = feeConfigTypeDesc;
	}
	public String getFeeConfigCode() {
		return feeConfigCode;
	}
	public void setFeeConfigCode(String feeConfigCode) {
		this.feeConfigCode = feeConfigCode;
	}
	public String getEffectiveDate() {
		return effectiveDate;
	}
	public void setEffectiveDate(String effectiveDate) {
		this.effectiveDate = effectiveDate;
	}
	public String getFeeType() {
		return feeType;
	}
	public void setFeeType(String feeType) {
		this.feeType = feeType;
	}

	public String getChannelCode() {
		return channelCode;
	}

	public void setChannelCode(String channelCode) {
		this.channelCode = channelCode;
	}

	public String getBusinessVertical() {
		return businessVertical;
	}

	public void setBusinessVertical(String businessVertical) {
		this.businessVertical = businessVertical;
	}

	public String getContractId() {
		return contractId;
	}

	public void setContractId(String contractId) {
		this.contractId = contractId;
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public String getFeeDetailAttribute1() {
		return feeDetailAttribute1;
	}

	public void setFeeDetailAttribute1(String feeDetailAttribute1) {
		this.feeDetailAttribute1 = feeDetailAttribute1;
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
	public String getFeesDetailsAttribute1() {
		return feesDetailsAttribute1;
	}
	public void setFeesDetailsAttribute1(String feesDetailsAttribute1) {
		this.feesDetailsAttribute1 = feesDetailsAttribute1;
	}
	public String getFeesDetailsAttribute2() {
		return feesDetailsAttribute2;
	}
	public void setFeesDetailsAttribute2(String feesDetailsAttribute2) {
		this.feesDetailsAttribute2 = feesDetailsAttribute2;
	}
	public String getFeesDetailsAttribute3() {
		return feesDetailsAttribute3;
	}
	public void setFeesDetailsAttribute3(String feesDetailsAttribute3) {
		this.feesDetailsAttribute3 = feesDetailsAttribute3;
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
	public String getChannelType() {
		return channelType;
	}
	public void setChannelType(String channelType) {
		this.channelType = channelType;
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
	public int getRoundOffBasisDecimal() {
		return roundOffBasisDecimal;
	}
	public void setRoundOffBasisDecimal(int roundOffBasisDecimal) {
		this.roundOffBasisDecimal = roundOffBasisDecimal;
	}
	public String getChannelTypeDesc() {
		return channelTypeDesc;
	}
	public void setChannelTypeDesc(String channelTypeDesc) {
		this.channelTypeDesc = channelTypeDesc;
	}
	public String getRoundOffBasisDesc() {
		return roundOffBasisDesc;
	}
	public void setRoundOffBasisDesc(String roundOffBasisDesc) {
		this.roundOffBasisDesc = roundOffBasisDesc;
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
	public String getFeesDetailsAttribute4() {
		return feesDetailsAttribute4;
	}
	public void setFeesDetailsAttribute4(String feesDetailsAttribute4) {
		this.feesDetailsAttribute4 = feesDetailsAttribute4;
	}
	public String getFeesDetailsAttribute5() {
		return feesDetailsAttribute5;
	}
	public void setFeesDetailsAttribute5(String feesDetailsAttribute5) {
		this.feesDetailsAttribute5 = feesDetailsAttribute5;
	}
	public String getFeesDetailsAttribute6() {
		return feesDetailsAttribute6;
	}
	public void setFeesDetailsAttribute6(String feesDetailsAttribute6) {
		this.feesDetailsAttribute6 = feesDetailsAttribute6;
	}
	public String getFeesDetailsAttribute7() {
		return feesDetailsAttribute7;
	}
	public void setFeesDetailsAttribute7(String feesDetailsAttribute7) {
		this.feesDetailsAttribute7 = feesDetailsAttribute7;
	}
	public String getFeesDetailsAttribute8() {
		return feesDetailsAttribute8;
	}
	public void setFeesDetailsAttribute8(String feesDetailsAttribute8) {
		this.feesDetailsAttribute8 = feesDetailsAttribute8;
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
	public int getFeeDetailStatusNt() {
		return feeDetailStatusNt;
	}
	public void setFeeDetailStatusNt(int feeDetailStatusNt) {
		this.feeDetailStatusNt = feeDetailStatusNt;
	}
	public int getFeeDetailStatus() {
		return feeDetailStatus;
	}
	public void setFeeDetailStatus(int feeDetailStatus) {
		this.feeDetailStatus = feeDetailStatus;
	}
	public String getFeeDetailStatusDesc() {
		return feeDetailStatusDesc;
	}
	public void setFeeDetailStatusDesc(String feeDetailStatusDesc) {
		this.feeDetailStatusDesc = feeDetailStatusDesc;
	}
	public String getContractName() {
		return contractName;
	}
	public void setContractName(String contractName) {
		this.contractName = contractName;
	}
	public String getCustomerName() {
		return customerName;
	}
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
	public List<SmartSearchVb> getSmartSearchOpt() {
		return smartSearchOpt;
	}
	public void setSmartSearchOpt(List<SmartSearchVb> smartSearchOpt) {
		this.smartSearchOpt = smartSearchOpt;
	}
	public String getPricing() {
		return pricing;
	}
	public void setPricing(String pricing) {
		this.pricing = pricing;
	}
	public String getPricingDesc() {
		return pricingDesc;
	}
	public void setPricingDesc(String pricingDesc) {
		this.pricingDesc = pricingDesc;
	}
	public int getPricingAt() {
		return pricingAt;
	}
	public void setPricingAt(int pricingAt) {
		this.pricingAt = pricingAt;
	}
	public String getFeesDetailsAttribute9() {
		return feesDetailsAttribute9;
	}
	public void setFeesDetailsAttribute9(String feesDetailsAttribute9) {
		this.feesDetailsAttribute9 = feesDetailsAttribute9;
	}
	public String getFeesDetailsAttribute10() {
		return feesDetailsAttribute10;
	}
	public void setFeesDetailsAttribute10(String feesDetailsAttribute10) {
		this.feesDetailsAttribute10 = feesDetailsAttribute10;
	}
	public String getFeesDetailsAttribute11() {
		return feesDetailsAttribute11;
	}
	public void setFeesDetailsAttribute11(String feesDetailsAttribute11) {
		this.feesDetailsAttribute11 = feesDetailsAttribute11;
	}
	public String getFeesDetailsAttribute12() {
		return feesDetailsAttribute12;
	}
	public void setFeesDetailsAttribute12(String feesDetailsAttribute12) {
		this.feesDetailsAttribute12 = feesDetailsAttribute12;
	}
	public String getFeesDetailsAttribute13() {
		return feesDetailsAttribute13;
	}
	public void setFeesDetailsAttribute13(String feesDetailsAttribute13) {
		this.feesDetailsAttribute13 = feesDetailsAttribute13;
	}
	public String getFeesDetailsAttribute14() {
		return feesDetailsAttribute14;
	}
	public void setFeesDetailsAttribute14(String feesDetailsAttribute14) {
		this.feesDetailsAttribute14 = feesDetailsAttribute14;
	}
	public String getFeesDetailsAttribute15() {
		return feesDetailsAttribute15;
	}
	public void setFeesDetailsAttribute15(String feesDetailsAttribute15) {
		this.feesDetailsAttribute15 = feesDetailsAttribute15;
	}
	public String getFeesDetailsAttribute16() {
		return feesDetailsAttribute16;
	}
	public void setFeesDetailsAttribute16(String feesDetailsAttribute16) {
		this.feesDetailsAttribute16 = feesDetailsAttribute16;
	}
	public String getFeesDetailsAttribute17() {
		return feesDetailsAttribute17;
	}
	public void setFeesDetailsAttribute17(String feesDetailsAttribute17) {
		this.feesDetailsAttribute17 = feesDetailsAttribute17;
	}
	public String getFeesDetailsAttribute18() {
		return feesDetailsAttribute18;
	}
	public void setFeesDetailsAttribute18(String feesDetailsAttribute18) {
		this.feesDetailsAttribute18 = feesDetailsAttribute18;
	}
	public String getFeesDetailsAttribute19() {
		return feesDetailsAttribute19;
	}
	public void setFeesDetailsAttribute19(String feesDetailsAttribute19) {
		this.feesDetailsAttribute19 = feesDetailsAttribute19;
	}
	public String getFeesDetailsAttribute20() {
		return feesDetailsAttribute20;
	}
	public void setFeesDetailsAttribute20(String feesDetailsAttribute20) {
		this.feesDetailsAttribute20 = feesDetailsAttribute20;
	}
	public String getRateEffectiveEndDate() {
		return rateEffectiveEndDate;
	}
	public void setRateEffectiveEndDate(String rateEffectiveEndDate) {
		this.rateEffectiveEndDate = rateEffectiveEndDate;
	}

	
}
