package com.vision.vb;

import java.util.List;

public class EtlOperationManagerHeaderVb extends CommonVb{
	private String posBusDate = "";
	private String country = "";
	private String leBook = "";
	private String extractionFrequency = "";
	private String extractionProcess = "";
	private String etlInitiated = "";
	private String etlReinitiated = "";
	private String yetToStart = "";
	private String inProgress = "";
	private String completed = "";
	private String errored = "";
	private String dateType = "";
	private String autoRefreshTime = "";

	List<EtlOperationDetailVb> etlOperationDetailLst = null;
	
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
	public String getExtractionProcess() {
		return extractionProcess;
	}
	public void setExtractionProcess(String extractionProcess) {
		this.extractionProcess = extractionProcess;
	}
	public String getEtlInitiated() {
		return etlInitiated;
	}
	public void setEtlInitiated(String etlInitiated) {
		this.etlInitiated = etlInitiated;
	}
	public String getEtlReinitiated() {
		return etlReinitiated;
	}
	public void setEtlReinitiated(String etlReinitiated) {
		this.etlReinitiated = etlReinitiated;
	}
	public String getYetToStart() {
		return yetToStart;
	}
	public void setYetToStart(String yetToStart) {
		this.yetToStart = yetToStart;
	}
	public String getInProgress() {
		return inProgress;
	}
	public void setInProgress(String inProgress) {
		this.inProgress = inProgress;
	}
	public String getErrored() {
		return errored;
	}
	public void setErrored(String errored) {
		this.errored = errored;
	}
	public List<EtlOperationDetailVb> getEtlOperationDetailLst() {
		return etlOperationDetailLst;
	}
	public void setEtlOperationDetailLst(List<EtlOperationDetailVb> etlOperationDetailLst) {
		this.etlOperationDetailLst = etlOperationDetailLst;
	}
	public String getCompleted() {
		return completed;
	}
	public void setCompleted(String completed) {
		this.completed = completed;
	}
	public String getDateType() {
		return dateType;
	}
	public void setDateType(String dateType) {
		this.dateType = dateType;
	}
	public String getPosBusDate() {
		return posBusDate;
	}
	public void setPosBusDate(String posBusDate) {
		this.posBusDate = posBusDate;
	}
	public String getAutoRefreshTime() {
		return autoRefreshTime;
	}
	public void setAutoRefreshTime(String autoRefreshTime) {
		this.autoRefreshTime = autoRefreshTime;
	}

}
