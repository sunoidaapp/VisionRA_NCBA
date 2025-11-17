package com.vision.vb;

import java.util.List;

public class EtlManagerVb extends CommonVb{
	private String country = "";
	private String leBook= "";
	private String extractionFrequency= "";
	private String extractionSequence= "";
	private String extractionDateType= "";
	private String extractionDay= "";
	private String holidayFlag= "";
	private String extractionDescription= "";
	private int extractionEngineAt = 7035 ;
	private String extractionEngine= "";
	private String extractionEngineDesc= "";
	private String exrtactionType= "";
	private String scheduleType= "";
	private String schPeriodFrom= "";
	private String schPeriodTo= "";
	private String scheduleTime= "";
	private int eventNameAt= 7036;
	private String eventName= "";
	private String eventNameDesc= "";
	private String extractionStartDate= "";
	private int extractionStatusNt= 1;
	private int extractionStatus= 0;
	private String extractionStatusDesc= "";
	private String lastExecutedDate= "";
	private String scheduleStatus = "";
	private String lastExecutedStatusDesc= "";
	private String dependentBuildSeq = "";
	
	List<SmartSearchVb> smartSearchOpt = null;
	List<EtlManagerDetailsVb> etlManagerDetaillst = null;
	
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
	public String getExtractionDateType() {
		return extractionDateType;
	}
	public void setExtractionDateType(String extractionDateType) {
		this.extractionDateType = extractionDateType;
	}
	public String getExtractionDay() {
		return extractionDay;
	}
	public void setExtractionDay(String extractionDay) {
		this.extractionDay = extractionDay;
	}
	public String getHolidayFlag() {
		return holidayFlag;
	}
	public void setHolidayFlag(String holidayFlag) {
		this.holidayFlag = holidayFlag;
	}
	public String getExtractionDescription() {
		return extractionDescription;
	}
	public void setExtractionDescription(String extractionDescription) {
		this.extractionDescription = extractionDescription;
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
	public String getSchPeriodFrom() {
		return schPeriodFrom;
	}
	public void setSchPeriodFrom(String schPeriodFrom) {
		this.schPeriodFrom = schPeriodFrom;
	}
	public String getSchPeriodTo() {
		return schPeriodTo;
	}
	public void setSchPeriodTo(String schPeriodTo) {
		this.schPeriodTo = schPeriodTo;
	}
	public String getScheduleTime() {
		return scheduleTime;
	}
	public void setScheduleTime(String scheduleTime) {
		this.scheduleTime = scheduleTime;
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
	public String getEventNameDesc() {
		return eventNameDesc;
	}
	public void setEventNameDesc(String eventNameDesc) {
		this.eventNameDesc = eventNameDesc;
	}
	public String getExtractionStartDate() {
		return extractionStartDate;
	}
	public void setExtractionStartDate(String extractionStartDate) {
		this.extractionStartDate = extractionStartDate;
	}
	public int getExtractionStatusNt() {
		return extractionStatusNt;
	}
	public void setExtractionStatusNt(int extractionStatusNt) {
		this.extractionStatusNt = extractionStatusNt;
	}
	public String getLastExecutedDate() {
		return lastExecutedDate;
	}
	public void setLastExecutedDate(String lastExecutedDate) {
		this.lastExecutedDate = lastExecutedDate;
	}
	public String getLastExecutedStatusDesc() {
		return lastExecutedStatusDesc;
	}
	public void setLastExecutedStatusDesc(String lastExecutedStatusDesc) {
		this.lastExecutedStatusDesc = lastExecutedStatusDesc;
	}
	public List<SmartSearchVb> getSmartSearchOpt() {
		return smartSearchOpt;
	}
	public void setSmartSearchOpt(List<SmartSearchVb> smartSearchOpt) {
		this.smartSearchOpt = smartSearchOpt;
	}
	public List<EtlManagerDetailsVb> getEtlManagerDetaillst() {
		return etlManagerDetaillst;
	}
	public void setEtlManagerDetaillst(List<EtlManagerDetailsVb> etlManagerDetaillst) {
		this.etlManagerDetaillst = etlManagerDetaillst;
	}
	public int getExtractionStatus() {
		return extractionStatus;
	}
	public void setExtractionStatus(int extractionStatus) {
		this.extractionStatus = extractionStatus;
	}
	public String getExtractionStatusDesc() {
		return extractionStatusDesc;
	}
	public void setExtractionStatusDesc(String extractionStatusDesc) {
		this.extractionStatusDesc = extractionStatusDesc;
	}

	public String getDependentBuildSeq() {
		return dependentBuildSeq;
	}

	public void setDependentBuildSeq(String dependentBuildSeq) {
		this.dependentBuildSeq = dependentBuildSeq;
	}

	public String getScheduleStatus() {
		return scheduleStatus;
	}

	public void setScheduleStatus(String scheduleStatus) {
		this.scheduleStatus = scheduleStatus;
	}
}
