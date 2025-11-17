package com.vision.vb;

public class EtlPostingsVb extends CommonVb {
    private String postingType = "";
    private int postingTypeAt = 7038;
	private String postingDate = "";
	private String businessDate = "";
	private String country = "";
	private String leBook = "";
	private String extractionFrequency = "";
	private String extractionFrequencyDesc = "";
	private String extractionSequence = "";
	private String extractionFeedId = "";
	private String extractionFeedIdDesc = "";
	private String postingSequence = "";
	private int extractionEngineAt = 7035;
	private String extractionEngine = "";
	private String extractionEngineDesc = "";
	private String exrtactionType = "";
	private String exrtactionTypeDesc = "";
	private String scheduleType = "";
	private String scheduleTypeDesc = "";
	private String scheduleTime = "";
	private int eventNameAt = 7036;
	private String eventName = "";
	private String eventNameDesc = "";
	private String startTime = "";
	private String endTime = "";
	private String postedStatus = "";
	private String postedBy = "";
	private String postedTime = "";
	private String logFileName = "";
	private String extractionDescription = "";
	private String feedCategory = "";
	private String feedCategoryDesc = "";
	private String debugMode = "";
	private String runIt = "";
	private String dependentFlag = "";
	private String dependentFeedId = "";
	private String dependentBuildSeq = "";
	private String dependentFlagChkBox = "";
	
	public String getPostingType() {
		return postingType;
	}
	public void setPostingType(String postingType) {
		this.postingType = postingType;
	}
	public String getPostingDate() {
		return postingDate;
	}
	public void setPostingDate(String postingDate) {
		this.postingDate = postingDate;
	}
	public String getBusinessDate() {
		return businessDate;
	}
	public void setBusinessDate(String businessDate) {
		this.businessDate = businessDate;
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
	public String getExtractionFrequency() {
		return extractionFrequency;
	}
	public void setExtractionFrequency(String extractionFrequency) {
		this.extractionFrequency = extractionFrequency;
	}
	public String getPostingSequence() {
		return postingSequence;
	}
	public void setPostingSequence(String postingSequence) {
		this.postingSequence = postingSequence;
	}
	public String getExtractionEngine() {
		return extractionEngine;
	}
	public void setExtractionEngine(String extractionEngine) {
		this.extractionEngine = extractionEngine;
	}
	public String getExrtactionType() {
		return exrtactionType;
	}
	public void setExrtactionType(String exrtactionType) {
		this.exrtactionType = exrtactionType;
	}
	public String getScheduleType() {
		return scheduleType;
	}
	public void setScheduleType(String scheduleType) {
		this.scheduleType = scheduleType;
	}
	public String getScheduleTime() {
		return scheduleTime;
	}
	public void setScheduleTime(String scheduleTime) {
		this.scheduleTime = scheduleTime;
	}
	public String getEventName() {
		return eventName;
	}
	public void setEventName(String eventName) {
		this.eventName = eventName;
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
	public String getPostedStatus() {
		return postedStatus;
	}
	public void setPostedStatus(String postedStatus) {
		this.postedStatus = postedStatus;
	}
	public String getPostedBy() {
		return postedBy;
	}
	public void setPostedBy(String postedBy) {
		this.postedBy = postedBy;
	}
	public String getPostedTime() {
		return postedTime;
	}
	public void setPostedTime(String postedTime) {
		this.postedTime = postedTime;
	}
	public String getLogFileName() {
		return logFileName;
	}
	public void setLogFileName(String logFileName) {
		this.logFileName = logFileName;
	}
	public int getPostingTypeAt() {
		return postingTypeAt;
	}
	public void setPostingTypeAt(int postingTypeAt) {
		this.postingTypeAt = postingTypeAt;
	}
	public int getExtractionEngineAt() {
		return extractionEngineAt;
	}
	public void setExtractionEngineAt(int extractionEngineAt) {
		this.extractionEngineAt = extractionEngineAt;
	}
	public int getEventNameAt() {
		return eventNameAt;
	}
	public void setEventNameAt(int eventNameAt) {
		this.eventNameAt = eventNameAt;
	}
	public String getExtractionDescription() {
		return extractionDescription;
	}
	public void setExtractionDescription(String extractionDescription) {
		this.extractionDescription = extractionDescription;
	}
	public String getFeedCategory() {
		return feedCategory;
	}
	public void setFeedCategory(String feedCategory) {
		this.feedCategory = feedCategory;
	}
	public String getDebugMode() {
		return debugMode;
	}
	public void setDebugMode(String debugMode) {
		this.debugMode = debugMode;
	}
	public String getRunIt() {
		return runIt;
	}
	public void setRunIt(String runIt) {
		this.runIt = runIt;
	}
	public String getExtractionFrequencyDesc() {
		return extractionFrequencyDesc;
	}
	public void setExtractionFrequencyDesc(String extractionFrequencyDesc) {
		this.extractionFrequencyDesc = extractionFrequencyDesc;
	}
	public String getExtractionEngineDesc() {
		return extractionEngineDesc;
	}
	public void setExtractionEngineDesc(String extractionEngineDesc) {
		this.extractionEngineDesc = extractionEngineDesc;
	}
	public String getExrtactionTypeDesc() {
		return exrtactionTypeDesc;
	}
	public void setExrtactionTypeDesc(String exrtactionTypeDesc) {
		this.exrtactionTypeDesc = exrtactionTypeDesc;
	}
	public String getScheduleTypeDesc() {
		return scheduleTypeDesc;
	}
	public void setScheduleTypeDesc(String scheduleTypeDesc) {
		this.scheduleTypeDesc = scheduleTypeDesc;
	}
	public String getEventNameDesc() {
		return eventNameDesc;
	}
	public void setEventNameDesc(String eventNameDesc) {
		this.eventNameDesc = eventNameDesc;
	}
	public String getFeedCategoryDesc() {
		return feedCategoryDesc;
	}
	public void setFeedCategoryDesc(String feedCategoryDesc) {
		this.feedCategoryDesc = feedCategoryDesc;
	}
	public String getExtractionFeedId() {
		return extractionFeedId;
	}
	public void setExtractionFeedId(String extractionFeedId) {
		this.extractionFeedId = extractionFeedId;
	}
	public String getExtractionFeedIdDesc() {
		return extractionFeedIdDesc;
	}
	public void setExtractionFeedIdDesc(String extractionFeedIdDesc) {
		this.extractionFeedIdDesc = extractionFeedIdDesc;
	}
	public String getDependentFlag() {
		return dependentFlag;
	}
	public void setDependentFlag(String dependentFlag) {
		this.dependentFlag = dependentFlag;
	}
	public String getDependentFeedId() {
		return dependentFeedId;
	}
	public void setDependentFeedId(String dependentFeedId) {
		this.dependentFeedId = dependentFeedId;
	}

	public String getExtractionSequence() {
		return extractionSequence;
	}

	public void setExtractionSequence(String extractionSequence) {
		this.extractionSequence = extractionSequence;
	}

	public String getDependentBuildSeq() {
		return dependentBuildSeq;
	}

	public void setDependentBuildSeq(String dependentBuildSeq) {
		this.dependentBuildSeq = dependentBuildSeq;
	}

	public String getDependentFlagChkBox() {
		return dependentFlagChkBox;
	}

	public void setDependentFlagChkBox(String dependentFlagChkBox) {
		this.dependentFlagChkBox = dependentFlagChkBox;
	}
}
