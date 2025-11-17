package com.vision.vb;

public class EtlPostingsHeaderVb extends CommonVb {
	
	private String country = "";
	private String leBook = "";
	private String extractionFrequency = "";
	private String extractionFrequencyDesc = "";
	private int extractionSequence = 0;
	private int dependentSequence = 0;
	private String extractionSeqDescription= "";
	private String businessDate = "";
	private int extractionEngineAt = 7035;
	private String extractionEngine = "";
	private String extractionEngineDesc = "";
	private String startTime = "";
	private String endTime = "";
	private String node = "";
	private String postedStatus = "";
	private String submitType = "";
	private String submitterId = "";
	private int postingStatusNt= 7039;
	private String postingStatus="P";
	private String postingStatusDesc = "";
	private String postingType = "";
	
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
	public String getExtractionFrequency() {
		return extractionFrequency;
	}
	public void setExtractionFrequency(String extractionFrequency) {
		this.extractionFrequency = extractionFrequency;
	}
	public String getExtractionFrequencyDesc() {
		return extractionFrequencyDesc;
	}
	public void setExtractionFrequencyDesc(String extractionFrequencyDesc) {
		this.extractionFrequencyDesc = extractionFrequencyDesc;
	}
	public String getExtractionSeqDescription() {
		return extractionSeqDescription;
	}
	public void setExtractionSeqDescription(String extractionSeqDescription) {
		this.extractionSeqDescription = extractionSeqDescription;
	}
	public String getBusinessDate() {
		return businessDate;
	}
	public void setBusinessDate(String businessDate) {
		this.businessDate = businessDate;
	}
	public int getExtractionEngineAt() {
		return extractionEngineAt;
	}
	public void setExtractionEngineAt(int extractionEngineAt) {
		this.extractionEngineAt = extractionEngineAt;
	}
	public String getExtractionEngine() {
		return extractionEngine;
	}
	public void setExtractionEngine(String extractionEngine) {
		this.extractionEngine = extractionEngine;
	}
	public String getExtractionEngineDesc() {
		return extractionEngineDesc;
	}
	public void setExtractionEngineDesc(String extractionEngineDesc) {
		this.extractionEngineDesc = extractionEngineDesc;
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	public String getNode() {
		return node;
	}
	public void setNode(String node) {
		this.node = node;
	}
	public String getPostedStatus() {
		return postedStatus;
	}
	public void setPostedStatus(String postedStatus) {
		this.postedStatus = postedStatus;
	}
	public String getSubmitType() {
		return submitType;
	}
	public void setSubmitType(String submitType) {
		this.submitType = submitType;
	}
	public String getSubmitterId() {
		return submitterId;
	}
	public void setSubmitterId(String submitterId) {
		this.submitterId = submitterId;
	}
	public int getPostingStatusNt() {
		return postingStatusNt;
	}
	public void setPostingStatusNt(int postingStatusNt) {
		this.postingStatusNt = postingStatusNt;
	}
	public String getPostingStatus() {
		return postingStatus;
	}
	public void setPostingStatus(String postingStatus) {
		this.postingStatus = postingStatus;
	}
	public int getDependentSequence() {
		return dependentSequence;
	}
	public void setDependentSequence(int dependentSequence) {
		this.dependentSequence = dependentSequence;
	}
	public int getExtractionSequence() {
		return extractionSequence;
	}
	public void setExtractionSequence(int extractionSequence) {
		this.extractionSequence = extractionSequence;
	}

	public String getPostingStatusDesc() {
		return postingStatusDesc;
	}

	public void setPostingStatusDesc(String postingStatusDesc) {
		this.postingStatusDesc = postingStatusDesc;
	}

	public String getPostingType() {
		return postingType;
	}

	public void setPostingType(String postingType) {
		this.postingType = postingType;
	}
}
