package com.vision.vb;

import java.util.List;

public class ConcessionActivityFilterVb extends CommonVb{

	private String country = "";
	private String leBook = "";
	private String activityId = "";
	private String ConditionType = "";
	
	private int FilterSequence = 1;
	private String LhsFilterTable = "";
	private String LhsFilterColumn = "";
	private String RhsFilterTable = "";
	private String RhsFilterColumn = "";
	private String ConditionOperation = "";
	// Filers Start
	
	private String ConditionValue1 = "";
	private String ConditionValue2 = "";
	


	private int FilterStatusNt = 1;
	private int FilterStatus = 0;
	
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

	public String getActivityId() {
		return activityId;
	}

	public void setActivityId(String activityId) {
		this.activityId = activityId;
	}

	public int getFilterSequence() {
		return FilterSequence;
	}

	public void setFilterSequence(int filterSequence) {
		FilterSequence = filterSequence;
	}

	public String getLhsFilterTable() {
		return LhsFilterTable;
	}

	public void setLhsFilterTable(String lhsFilterTable) {
		LhsFilterTable = lhsFilterTable;
	}

	public String getLhsFilterColumn() {
		return LhsFilterColumn;
	}

	public void setLhsFilterColumn(String lhsFilterColumn) {
		LhsFilterColumn = lhsFilterColumn;
	}

	public String getConditionOperation() {
		return ConditionOperation;
	}

	public void setConditionOperation(String conditionOperation) {
		ConditionOperation = conditionOperation;
	}

	public String getConditionType() {
		return ConditionType;
	}

	public void setConditionType(String conditionType) {
		ConditionType = conditionType;
	}

	public String getRhsFilterTable() {
		return RhsFilterTable;
	}

	public void setRhsFilterTable(String rhsFilterTable) {
		RhsFilterTable = rhsFilterTable;
	}

	public String getRhsFilterColumn() {
		return RhsFilterColumn;
	}

	public void setRhsFilterColumn(String rhsFilterColumn) {
		RhsFilterColumn = rhsFilterColumn;
	}

	public String getConditionValue1() {
		return ConditionValue1;
	}

	public void setConditionValue1(String conditionValue1) {
		ConditionValue1 = conditionValue1;
	}

	public String getConditionValue2() {
		return ConditionValue2;
	}

	public void setConditionValue2(String conditionValue2) {
		ConditionValue2 = conditionValue2;
	}

	public int getFilterStatusNt() {
		return FilterStatusNt;
	}

	public void setFilterStatusNt(int filterStatusNt) {
		FilterStatusNt = filterStatusNt;
	}

	public int getFilterStatus() {
		return FilterStatus;
	}

	public void setFilterStatus(int filterStatus) {
		FilterStatus = filterStatus;
	}

	public List<SmartSearchVb> getSmartSearchOpt() {
		return smartSearchOpt;
	}

	public void setSmartSearchOpt(List<SmartSearchVb> smartSearchOpt) {
		this.smartSearchOpt = smartSearchOpt;
	}

}
