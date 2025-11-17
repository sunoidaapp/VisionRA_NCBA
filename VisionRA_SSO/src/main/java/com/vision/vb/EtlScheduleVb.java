package com.vision.vb;

import java.util.List;

public class EtlScheduleVb extends CommonVb{
	private String country = "";
	private String leBook= "";
	private String extractionFrequency= "";
	private String extractionSequence= "";
	private String extractionDescription= "";
	private String extractionEngine="";
	private String scheduleType= "";
	private String scheduleFrequency= "";
	private String scheduleStartDate= "";
	private String scheduleEndDate= "";
	private String scheduleTime= "";
	private String lastScheduleDate ="";
	private String nextScheduleDate ="";
	private String lastRunStatus = "";
	private String lastRunStatusDesc = "";
	private int eventNameAt= 7036;
	private String eventName= "";
	private int scheduleStatusNt= 1;
	private int scheduleStatus= 0;
	private String node= "";
	
	List<SmartSearchVb> smartSearchOpt = null;

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

	public String getExtractionSequence() {
		return extractionSequence;
	}

	public void setExtractionSequence(String extractionSequence) {
		this.extractionSequence = extractionSequence;
	}

	public String getExtractionEngine() {
		return extractionEngine;
	}

	public void setExtractionEngine(String extractionEngine) {
		this.extractionEngine = extractionEngine;
	}

	public String getScheduleType() {
		return scheduleType;
	}

	public void setScheduleType(String scheduleType) {
		this.scheduleType = scheduleType;
	}

	public String getScheduleStartDate() {
		return scheduleStartDate;
	}

	public void setScheduleStartDate(String scheduleStartDate) {
		this.scheduleStartDate = scheduleStartDate;
	}

	public String getScheduleEndDate() {
		return scheduleEndDate;
	}

	public void setScheduleEndDate(String scheduleEndDate) {
		this.scheduleEndDate = scheduleEndDate;
	}

	public String getScheduleTime() {
		return scheduleTime;
	}

	public void setScheduleTime(String scheduleTime) {
		this.scheduleTime = scheduleTime;
	}

	public String getLastScheduleDate() {
		return lastScheduleDate;
	}

	public void setLastScheduleDate(String lastScheduleDate) {
		this.lastScheduleDate = lastScheduleDate;
	}

	public String getNextScheduleDate() {
		return nextScheduleDate;
	}

	public void setNextScheduleDate(String nextScheduleDate) {
		this.nextScheduleDate = nextScheduleDate;
	}

	public String getLastRunStatus() {
		return lastRunStatus;
	}

	public void setLastRunStatus(String lastRunStatus) {
		this.lastRunStatus = lastRunStatus;
	}

	public int getEventNameAt() {
		return eventNameAt;
	}

	public void setEventNameAt(int eventNameAt) {
		this.eventNameAt = eventNameAt;
	}

	public String getEventName() {
		return eventName;
	}

	public void setEventName(String eventName) {
		this.eventName = eventName;
	}

	public int getScheduleStatusNt() {
		return scheduleStatusNt;
	}

	public void setScheduleStatusNt(int scheduleStatusNt) {
		this.scheduleStatusNt = scheduleStatusNt;
	}

	public int getScheduleStatus() {
		return scheduleStatus;
	}

	public void setScheduleStatus(int scheduleStatus) {
		this.scheduleStatus = scheduleStatus;
	}

	public List<SmartSearchVb> getSmartSearchOpt() {
		return smartSearchOpt;
	}

	public void setSmartSearchOpt(List<SmartSearchVb> smartSearchOpt) {
		this.smartSearchOpt = smartSearchOpt;
	}

	public String getExtractionDescription() {
		return extractionDescription;
	}

	public void setExtractionDescription(String extractionDescription) {
		this.extractionDescription = extractionDescription;
	}

	public String getScheduleFrequency() {
		return scheduleFrequency;
	}

	public void setScheduleFrequency(String scheduleFrequency) {
		this.scheduleFrequency = scheduleFrequency;
	}

	public String getNode() {
		return node;
	}

	public void setNode(String node) {
		this.node = node;
	}

	public String getLastRunStatusDesc() {
		return lastRunStatusDesc;
	}

	public void setLastRunStatusDesc(String lastRunStatusDesc) {
		this.lastRunStatusDesc = lastRunStatusDesc;
	}
}
