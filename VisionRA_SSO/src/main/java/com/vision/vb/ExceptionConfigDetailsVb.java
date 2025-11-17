package com.vision.vb;

import java.util.List;

public class ExceptionConfigDetailsVb extends CommonVb{

	private String country = "";
	private String leBook = "";
	private String exceptionReference = "";
	private String exceptionReferenceDesc = "";
	private String businessDate = "";
	private String transLineId = "";
	private String transLineDescription = "";
	private int transSequence = 0;
	private String businessLineId = "";
	private String businessLineDescription = "";
	private String exceptionDate = "";
	private int exceptionTypeAT = 7078;
	private String exceptionType = "";
	private String exceptionTypeDesc = "";		
	private String exceptionTypeRemarks = "";		
	
	private String exceptionAmountLcy = "";
	private String exceptionAmountFcy = "";
	private String exceptionCount = "";
	
	private String actualAmountLcy = "";
	private String actualAmountFcy = "";
	private String actualCount = "";
	
	private String expectedAmountLcy = "";
	private String expectedAmountFcy = "";
	private String expectedCount = "";
	
	private String exceptionIEAmountLcy = "";
	private String exceptionIEAmountFcy = "";
	private String exceptionIECount = "";
	
	private String orginalLeakageLcy = "";
	private String orginalLeakageFcy = "";
	private String originalLeakageCount = "";
	
	private String mrbusinessDate = "";
	private String mrtransLineId = "";
	private String mrtransLineDescription = "";
	private int mrTransSequence = 0;
	private String mrbusinessLineId = "";
	private String mrRemarks = "";
	
	public String channelCode = "";
	public String channelType = "";
	public String businessVertical = "";
	public String customerID = "";
	public String contractID = "";
	public String productType = "";
	public String productID = "";
	private String businessDateFrom = "";
	private String businessDateTo="";
	private String transLineType = "";

	private String lekageAmountLcy = "";
	private String lekageAmountFcy = "";
	private String leakageCount = "";
	private String basicFilterStr = "";
	private String category = "";
	private String exceptionFlag = "";
	private String exceptionCategory = "";
	private String exceptionCategoryDesc = "";
	
	List<SmartSearchVb> smartSearchOpt = null;
	List<ExceptionManualFiltersVb> exceptionUpdateFilterlst = null;
	
	private String postedDate = "";
	private String postingCcy = "";
	private String fxRate = "";
	private String tranCcy = "";
	
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
	public String getBusinessDate() {
		return businessDate;
	}
	public void setBusinessDate(String businessDate) {
		this.businessDate = businessDate;
	}
	public String getTransLineId() {
		return transLineId;
	}
	public void setTransLineId(String transLineId) {
		this.transLineId = transLineId;
	}
	public String getTransLineDescription() {
		return transLineDescription;
	}
	public void setTransLineDescription(String transLineDescription) {
		this.transLineDescription = transLineDescription;
	}
	public int getTransSequence() {
		return transSequence;
	}
	public void setTransSequence(int transSequence) {
		this.transSequence = transSequence;
	}
	public String getBusinessLineId() {
		return businessLineId;
	}
	public void setBusinessLineId(String businessLineId) {
		this.businessLineId = businessLineId;
	}
	public String getBusinessLineDescription() {
		return businessLineDescription;
	}
	public void setBusinessLineDescription(String businessLineDescription) {
		this.businessLineDescription = businessLineDescription;
	}
	public String getExceptionDate() {
		return exceptionDate;
	}
	public void setExceptionDate(String exceptionDate) {
		this.exceptionDate = exceptionDate;
	}
	public int getExceptionTypeAT() {
		return exceptionTypeAT;
	}
	public void setExceptionTypeAT(int exceptionTypeAT) {
		this.exceptionTypeAT = exceptionTypeAT;
	}
	public String getExceptionType() {
		return exceptionType;
	}
	public void setExceptionType(String exceptionType) {
		this.exceptionType = exceptionType;
	}
	public String getExceptionTypeDesc() {
		return exceptionTypeDesc;
	}
	public void setExceptionTypeDesc(String exceptionTypeDesc) {
		this.exceptionTypeDesc = exceptionTypeDesc;
	}
	public String getExceptionTypeRemarks() {
		return exceptionTypeRemarks;
	}
	public void setExceptionTypeRemarks(String exceptionTypeRemarks) {
		this.exceptionTypeRemarks = exceptionTypeRemarks;
	}
	
	public String getMrbusinessDate() {
		return mrbusinessDate;
	}
	public void setMrbusinessDate(String mrbusinessDate) {
		this.mrbusinessDate = mrbusinessDate;
	}
	public String getMrtransLineId() {
		return mrtransLineId;
	}
	public void setMrtransLineId(String mrtransLineId) {
		this.mrtransLineId = mrtransLineId;
	}
	public String getMrtransLineDescription() {
		return mrtransLineDescription;
	}
	public void setMrtransLineDescription(String mrtransLineDescription) {
		this.mrtransLineDescription = mrtransLineDescription;
	}
	public int getMrTransSequence() {
		return mrTransSequence;
	}
	public void setMrTransSequence(int mrTransSequence) {
		this.mrTransSequence = mrTransSequence;
	}
	public String getMrbusinessLineId() {
		return mrbusinessLineId;
	}
	public void setMrbusinessLineId(String mrbusinessLineId) {
		this.mrbusinessLineId = mrbusinessLineId;
	}
	public String getMrRemarks() {
		return mrRemarks;
	}
	public void setMrRemarks(String mrRemarks) {
		this.mrRemarks = mrRemarks;
	}
	public String getExceptionReference() {
		return exceptionReference;
	}
	public void setExceptionReference(String exceptionReference) {
		this.exceptionReference = exceptionReference;
	}
	public String getChannelCode() {
		return channelCode;
	}
	public void setChannelCode(String channelCode) {
		this.channelCode = channelCode;
	}
	public String getChannelType() {
		return channelType;
	}
	public void setChannelType(String channelType) {
		this.channelType = channelType;
	}
	public String getBusinessVertical() {
		return businessVertical;
	}
	public void setBusinessVertical(String businessVertical) {
		this.businessVertical = businessVertical;
	}
	public String getCustomerID() {
		return customerID;
	}
	public void setCustomerID(String customerID) {
		this.customerID = customerID;
	}
	public String getContractID() {
		return contractID;
	}
	public void setContractID(String contractID) {
		this.contractID = contractID;
	}
	public String getProductType() {
		return productType;
	}
	public void setProductType(String productType) {
		this.productType = productType;
	}
	public String getProductID() {
		return productID;
	}
	public void setProductID(String productID) {
		this.productID = productID;
	}
	public String getBusinessDateFrom() {
		return businessDateFrom;
	}
	public void setBusinessDateFrom(String businessDateFrom) {
		this.businessDateFrom = businessDateFrom;
	}
	public String getBusinessDateTo() {
		return businessDateTo;
	}
	public void setBusinessDateTo(String businessDateTo) {
		this.businessDateTo = businessDateTo;
	}
	public String getTransLineType() {
		return transLineType;
	}
	public void setTransLineType(String transLineType) {
		this.transLineType = transLineType;
	}
	
	public String getExceptionAmountLcy() {
		return exceptionAmountLcy;
	}
	public void setExceptionAmountLcy(String exceptionAmountLcy) {
		this.exceptionAmountLcy = exceptionAmountLcy;
	}
	public String getExceptionAmountFcy() {
		return exceptionAmountFcy;
	}
	public void setExceptionAmountFcy(String exceptionAmountFcy) {
		this.exceptionAmountFcy = exceptionAmountFcy;
	}
	public String getLekageAmountLcy() {
		return lekageAmountLcy;
	}
	public void setLekageAmountLcy(String lekageAmountLcy) {
		this.lekageAmountLcy = lekageAmountLcy;
	}
	public String getLekageAmountFcy() {
		return lekageAmountFcy;
	}
	public void setLekageAmountFcy(String lekageAmountFcy) {
		this.lekageAmountFcy = lekageAmountFcy;
	}
	public List<SmartSearchVb> getSmartSearchOpt() {
		return smartSearchOpt;
	}
	public void setSmartSearchOpt(List<SmartSearchVb> smartSearchOpt) {
		this.smartSearchOpt = smartSearchOpt;
	}
	public List<ExceptionManualFiltersVb> getExceptionUpdateFilterlst() {
		return exceptionUpdateFilterlst;
	}
	public void setExceptionUpdateFilterlst(List<ExceptionManualFiltersVb> exceptionUpdateFilterlst) {
		this.exceptionUpdateFilterlst = exceptionUpdateFilterlst;
	}
	public String getBasicFilterStr() {
		return basicFilterStr;
	}
	public void setBasicFilterStr(String basicFilterStr) {
		this.basicFilterStr = basicFilterStr;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getExceptionFlag() {
		return exceptionFlag;
	}
	public void setExceptionFlag(String exceptionFlag) {
		this.exceptionFlag = exceptionFlag;
	}
	public String getExceptionCategory() {
		return exceptionCategory;
	}
	public void setExceptionCategory(String exceptionCategory) {
		this.exceptionCategory = exceptionCategory;
	}
	public String getExceptionCategoryDesc() {
		return exceptionCategoryDesc;
	}
	public void setExceptionCategoryDesc(String exceptionCategoryDesc) {
		this.exceptionCategoryDesc = exceptionCategoryDesc;
	}
	public String getExceptionReferenceDesc() {
		return exceptionReferenceDesc;
	}
	public void setExceptionReferenceDesc(String exceptionReferenceDesc) {
		this.exceptionReferenceDesc = exceptionReferenceDesc;
	}
	public String getPostedDate() {
		return postedDate;
	}
	public void setPostedDate(String postedDate) {
		this.postedDate = postedDate;
	}
	public String getActualAmountLcy() {
		return actualAmountLcy;
	}
	public void setActualAmountLcy(String actualAmountLcy) {
		this.actualAmountLcy = actualAmountLcy;
	}
	public String getActualAmountFcy() {
		return actualAmountFcy;
	}
	public void setActualAmountFcy(String actualAmountFcy) {
		this.actualAmountFcy = actualAmountFcy;
	}
	public String getExpectedAmountLcy() {
		return expectedAmountLcy;
	}
	public void setExpectedAmountLcy(String expectedAmountLcy) {
		this.expectedAmountLcy = expectedAmountLcy;
	}
	public String getExpectedAmountFcy() {
		return expectedAmountFcy;
	}
	public void setExpectedAmountFcy(String expectedAmountFcy) {
		this.expectedAmountFcy = expectedAmountFcy;
	}
	public String getExceptionIEAmountLcy() {
		return exceptionIEAmountLcy;
	}
	public void setExceptionIEAmountLcy(String exceptionIEAmountLcy) {
		this.exceptionIEAmountLcy = exceptionIEAmountLcy;
	}
	public String getExceptionIEAmountFcy() {
		return exceptionIEAmountFcy;
	}
	public void setExceptionIEAmountFcy(String exceptionIEAmountFcy) {
		this.exceptionIEAmountFcy = exceptionIEAmountFcy;
	}
	public String getOrginalLeakageLcy() {
		return orginalLeakageLcy;
	}
	public void setOrginalLeakageLcy(String orginalLeakageLcy) {
		this.orginalLeakageLcy = orginalLeakageLcy;
	}
	public String getOrginalLeakageFcy() {
		return orginalLeakageFcy;
	}
	public void setOrginalLeakageFcy(String orginalLeakageFcy) {
		this.orginalLeakageFcy = orginalLeakageFcy;
	}
	public String getPostingCcy() {
		return postingCcy;
	}
	public void setPostingCcy(String postingCcy) {
		this.postingCcy = postingCcy;
	}
	public String getFxRate() {
		return fxRate;
	}
	public void setFxRate(String fxRate) {
		this.fxRate = fxRate;
	}
	public String getTranCcy() {
		return tranCcy;
	}
	public void setTranCcy(String tranCcy) {
		this.tranCcy = tranCcy;
	}
	public String getExceptionCount() {
		return exceptionCount;
	}
	public void setExceptionCount(String exceptionCount) {
		this.exceptionCount = exceptionCount;
	}
	public String getActualCount() {
		return actualCount;
	}
	public void setActualCount(String actualCount) {
		this.actualCount = actualCount;
	}
	public String getExpectedCount() {
		return expectedCount;
	}
	public void setExpectedCount(String expectedCount) {
		this.expectedCount = expectedCount;
	}
	public String getExceptionIECount() {
		return exceptionIECount;
	}
	public void setExceptionIECount(String exceptionIECount) {
		this.exceptionIECount = exceptionIECount;
	}
	public String getOriginalLeakageCount() {
		return originalLeakageCount;
	}
	public void setOriginalLeakageCount(String originalLeakageCount) {
		this.originalLeakageCount = originalLeakageCount;
	}
	public String getLeakageCount() {
		return leakageCount;
	}
	public void setLeakageCount(String leakageCount) {
		this.leakageCount = leakageCount;
	}
}