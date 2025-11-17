package com.vision.vb;

import java.util.ArrayList;
import java.util.List;

public class ExceptionConfigHeaderVb extends CommonVb{
	
	private String country = "";
	private String leBook = "";
	private String exceptionReference = "";
	private String exceptionReferenceDesc = "";

	private int exceptionFlagAT = 7077;
	private String exceptionFlag = "";
	private String exceptionFlagDesc = "";
	private int postedFlagAT = 207;
	private String postedFlag = "N";
	private String postedFlagDesc = "";
	private String startDate = "";
	private String endDate = "";
	private int transLineTypeAt = 7006;
	private String transLineType = "";
	private String transLineTypeDesc = "";	
	private String latestPostedDate = "";
	private int categoryAt = 7079;
	private String category = "";
	private String categoryDesc = "";
	private int detailCnt = 0;

	private String autoExceptionDate = "";
	private int autoExceptionTypeAT = 7078;
	private String autoExceptionType = "";
	private String autoExceptionTypeDesc = "";
	private String autoExceptionRemarks = "";
	private int feeBasisAt = 7032;
	private String feeBasis = "";
	private String feeBasisDesc = "";
	private String autoExceptionFeeAmt = "";
	private String autoExceptionFeePercentage = "";
	
	private int exceptionStatusNt = 1;
	private int exceptionStatus = 0;
	private String exceptionStatusDesc = "";
	private String raSocRestriction = ""; 
	
	List<SmartSearchVb> smartSearchOpt = null;

	List<ExceptionConfigDetailsVb> manualExceptionConfigDetaillst = null;
	List<ExceptionFilterVb> manualExceptionFilterlst = null;
	private String comments = "";
	List<ExceptionConfigCommentsVb> exceptionCommentsLst = new ArrayList<>();
	
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
	public String getExceptionReference() {
		return exceptionReference;
	}
	public void setExceptionReference(String exceptionReference) {
		this.exceptionReference = exceptionReference;
	}
	public int getExceptionFlagAT() {
		return exceptionFlagAT;
	}
	public void setExceptionFlagAT(int exceptionFlagAT) {
		this.exceptionFlagAT = exceptionFlagAT;
	}
	public String getExceptionFlag() {
		return exceptionFlag;
	}
	public void setExceptionFlag(String exceptionFlag) {
		this.exceptionFlag = exceptionFlag;
	}
	public String getExceptionFlagDesc() {
		return exceptionFlagDesc;
	}
	public void setExceptionFlagDesc(String exceptionFlagDesc) {
		this.exceptionFlagDesc = exceptionFlagDesc;
	}
	public int getPostedFlagAT() {
		return postedFlagAT;
	}
	public void setPostedFlagAT(int postedFlagAT) {
		this.postedFlagAT = postedFlagAT;
	}
	public String getPostedFlag() {
		return postedFlag;
	}
	public void setPostedFlag(String postedFlag) {
		this.postedFlag = postedFlag;
	}
	public String getPostedFlagDesc() {
		return postedFlagDesc;
	}
	public void setPostedFlagDesc(String postedFlagDesc) {
		this.postedFlagDesc = postedFlagDesc;
	}
	public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	public int getTransLineTypeAt() {
		return transLineTypeAt;
	}
	public void setTransLineTypeAt(int transLineTypeAt) {
		this.transLineTypeAt = transLineTypeAt;
	}
	public String getTransLineType() {
		return transLineType;
	}
	public void setTransLineType(String transLineType) {
		this.transLineType = transLineType;
	}
	public String getTransLineTypeDesc() {
		return transLineTypeDesc;
	}
	public void setTransLineTypeDesc(String transLineTypeDesc) {
		this.transLineTypeDesc = transLineTypeDesc;
	}
	public String getLatestPostedDate() {
		return latestPostedDate;
	}
	public void setLatestPostedDate(String latestPostedDate) {
		this.latestPostedDate = latestPostedDate;
	}
	public int getCategoryAt() {
		return categoryAt;
	}
	public void setCategoryAt(int categoryAt) {
		this.categoryAt = categoryAt;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getAutoExceptionDate() {
		return autoExceptionDate;
	}
	public void setAutoExceptionDate(String autoExceptionDate) {
		this.autoExceptionDate = autoExceptionDate;
	}
	public int getAutoExceptionTypeAT() {
		return autoExceptionTypeAT;
	}
	public void setAutoExceptionTypeAT(int autoExceptionTypeAT) {
		this.autoExceptionTypeAT = autoExceptionTypeAT;
	}
	public String getAutoExceptionType() {
		return autoExceptionType;
	}
	public void setAutoExceptionType(String autoExceptionType) {
		this.autoExceptionType = autoExceptionType;
	}
	public String getAutoExceptionTypeDesc() {
		return autoExceptionTypeDesc;
	}
	public void setAutoExceptionTypeDesc(String autoExceptionTypeDesc) {
		this.autoExceptionTypeDesc = autoExceptionTypeDesc;
	}
	public String getAutoExceptionRemarks() {
		return autoExceptionRemarks;
	}
	public void setAutoExceptionRemarks(String autoExceptionRemarks) {
		this.autoExceptionRemarks = autoExceptionRemarks;
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
	public String getAutoExceptionFeeAmt() {
		return autoExceptionFeeAmt;
	}
	public void setAutoExceptionFeeAmt(String autoExceptionFeeAmt) {
		this.autoExceptionFeeAmt = autoExceptionFeeAmt;
	}
	public String getAutoExceptionFeePercentage() {
		return autoExceptionFeePercentage;
	}
	public void setAutoExceptionFeePercentage(String autoExceptionFeePercentage) {
		this.autoExceptionFeePercentage = autoExceptionFeePercentage;
	}
	public int getExceptionStatusNt() {
		return exceptionStatusNt;
	}
	public void setExceptionStatusNt(int exceptionStatusNt) {
		this.exceptionStatusNt = exceptionStatusNt;
	}
	public int getExceptionStatus() {
		return exceptionStatus;
	}
	public void setExceptionStatus(int exceptionStatus) {
		this.exceptionStatus = exceptionStatus;
	}
	public String getExceptionStatusDesc() {
		return exceptionStatusDesc;
	}
	public void setExceptionStatusDesc(String exceptionStatusDesc) {
		this.exceptionStatusDesc = exceptionStatusDesc;
	}
	public List<SmartSearchVb> getSmartSearchOpt() {
		return smartSearchOpt;
	}
	public void setSmartSearchOpt(List<SmartSearchVb> smartSearchOpt) {
		this.smartSearchOpt = smartSearchOpt;
	}
	public List<ExceptionConfigDetailsVb> getManualExceptionConfigDetaillst() {
		return manualExceptionConfigDetaillst;
	}
	public void setManualExceptionConfigDetaillst(List<ExceptionConfigDetailsVb> manualExceptionConfigDetaillst) {
		this.manualExceptionConfigDetaillst = manualExceptionConfigDetaillst;
	}
	public List<ExceptionFilterVb> getManualExceptionFilterlst() {
		return manualExceptionFilterlst;
	}
	public void setManualExceptionFilterlst(List<ExceptionFilterVb> manualExceptionFilterlst) {
		this.manualExceptionFilterlst = manualExceptionFilterlst;
	}
	public String getCategoryDesc() {
		return categoryDesc;
	}
	public void setCategoryDesc(String categoryDesc) {
		this.categoryDesc = categoryDesc;
	}
	public int getDetailCnt() {
		return detailCnt;
	}
	public void setDetailCnt(int detailCnt) {
		this.detailCnt = detailCnt;
	}
	public String getExceptionReferenceDesc() {
		return exceptionReferenceDesc;
	}
	public void setExceptionReferenceDesc(String exceptionReferenceDesc) {
		this.exceptionReferenceDesc = exceptionReferenceDesc;
	}
	public String getRaSocRestriction() {
		return raSocRestriction;
	}
	public void setRaSocRestriction(String raSocRestriction) {
		this.raSocRestriction = raSocRestriction;
	}
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}
	public List<ExceptionConfigCommentsVb> getExceptionCommentsLst() {
		return exceptionCommentsLst;
	}
	public void setExceptionCommentsLst(List<ExceptionConfigCommentsVb> exceptionCommentsLst) {
		this.exceptionCommentsLst = exceptionCommentsLst;
	}
}