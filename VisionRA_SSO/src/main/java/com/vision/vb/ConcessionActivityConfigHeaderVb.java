package com.vision.vb;

import java.util.List;

public class ConcessionActivityConfigHeaderVb extends CommonVb{
	
	private String country = "";
	private String leBook = "";
	private String activityId = "";
	private String activityDesc = "";
	private String transLineId = "";
	private String actSourceTable = "";
	private String aggFun = "";
	private String aggCol ="";
	private int timeSeriesTypeAT = 7075;
	private String timeSeriesType = "";
	private String timeSeriesTypeDesc = "";
	private int activityTypeAt = 7076;
	private String activityType = "";
	private String activityTypeDesc = "";
	private String rangeFrom = "";
	private String rangeTo = "";
	private String sortColumns = "";
	private int activityStatusNt = 1;
	private int activityStatus = 0;
	private String activityStatusDesc = "";
	List<SmartSearchVb> smartSearchOpt = null;

	List<ConcessionActivityFilterVb> concessionFilterLst = null;
	List<ConcessionActivityFilterVb> concessionJoinLst = null;

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

	public String getActivityId() {
		return activityId;
	}

	public void setActivityId(String activityId) {
		this.activityId = activityId;
	}

	public String getActivityDesc() {
		return activityDesc;
	}

	public void setActivityDesc(String activityDesc) {
		this.activityDesc = activityDesc;
	}

	public String getTransLineId() {
		return transLineId;
	}

	public void setTransLineId(String transLineId) {
		this.transLineId = transLineId;
	}

	public String getActSourceTable() {
		return actSourceTable;
	}

	public void setActSourceTable(String actSourceTable) {
		this.actSourceTable = actSourceTable;
	}

	public String getAggFun() {
		return aggFun;
	}

	public void setAggFun(String aggFun) {
		this.aggFun = aggFun;
	}

	public String getAggCol() {
		return aggCol;
	}

	public void setAggCol(String aggCol) {
		this.aggCol = aggCol;
	}

	public int getTimeSeriesTypeAT() {
		return timeSeriesTypeAT;
	}

	public void setTimeSeriesTypeAT(int timeSeriesTypeAT) {
		this.timeSeriesTypeAT = timeSeriesTypeAT;
	}

	public String getTimeSeriesType() {
		return timeSeriesType;
	}

	public void setTimeSeriesType(String timeSeriesType) {
		this.timeSeriesType = timeSeriesType;
	}

	public String getTimeSeriesTypeDesc() {
		return timeSeriesTypeDesc;
	}

	public void setTimeSeriesTypeDesc(String timeSeriesTypeDesc) {
		this.timeSeriesTypeDesc = timeSeriesTypeDesc;
	}

	public int getActivityTypeAt() {
		return activityTypeAt;
	}

	public void setActivityTypeAt(int activityTypeAt) {
		this.activityTypeAt = activityTypeAt;
	}

	public String getActivityType() {
		return activityType;
	}

	public void setActivityType(String activityType) {
		this.activityType = activityType;
	}

	public String getSortColumns() {
		return sortColumns;
	}

	public void setSortColumns(String sortColumns) {
		this.sortColumns = sortColumns;
	}

	public int getActivityStatusNt() {
		return activityStatusNt;
	}

	public void setActivityStatusNt(int activityStatusNt) {
		this.activityStatusNt = activityStatusNt;
	}

	public int getActivityStatus() {
		return activityStatus;
	}

	public void setActivityStatus(int activityStatus) {
		this.activityStatus = activityStatus;
	}

	public String getActivityStatusDesc() {
		return activityStatusDesc;
	}

	public void setActivityStatusDesc(String activityStatusDesc) {
		this.activityStatusDesc = activityStatusDesc;
	}

	public List<SmartSearchVb> getSmartSearchOpt() {
		return smartSearchOpt;
	}

	public void setSmartSearchOpt(List<SmartSearchVb> smartSearchOpt) {
		this.smartSearchOpt = smartSearchOpt;
	}

	public String getActivityTypeDesc() {
		return activityTypeDesc;
	}

	public void setActivityTypeDesc(String activityTypeDesc) {
		this.activityTypeDesc = activityTypeDesc;
	}

	public List<ConcessionActivityFilterVb> getConcessionFilterLst() {
		return concessionFilterLst;
	}

	public void setConcessionFilterLst(List<ConcessionActivityFilterVb> concessionFilterLst) {
		this.concessionFilterLst = concessionFilterLst;
	}

	public List<ConcessionActivityFilterVb> getConcessionJoinLst() {
		return concessionJoinLst;
	}

	public void setConcessionJoinLst(List<ConcessionActivityFilterVb> concessionJoinLst) {
		this.concessionJoinLst = concessionJoinLst;
	}

	public String getRangeFrom() {
		return rangeFrom;
	}

	public void setRangeFrom(String rangeFrom) {
		this.rangeFrom = rangeFrom;
	}

	public String getRangeTo() {
		return rangeTo;
	}

	public void setRangeTo(String rangeTo) {
		this.rangeTo = rangeTo;
	}

	
	
}