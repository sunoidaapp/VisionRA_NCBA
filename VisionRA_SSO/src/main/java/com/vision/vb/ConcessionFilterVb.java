package com.vision.vb;

import java.util.List;

public class ConcessionFilterVb extends CommonVb{
	private String country = "";
	private String leBook = "";
	private String concessionId = "";
	private String effectiveDate = "";
	private int FilterSequence = 0;
	private String FilterTable = "";
	private String FilterColumn = "";
	private String ConditionOperation = "";
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

	public String getConcessionId() {
		return concessionId;
	}

	public void setConcessionId(String concessionId) {
		this.concessionId = concessionId;
	}

	public String getEffectiveDate() {
		return effectiveDate;
	}

	public void setEffectiveDate(String effectiveDate) {
		this.effectiveDate = effectiveDate;
	}

	public int getFilterSequence() {
		return FilterSequence;
	}

	public void setFilterSequence(int filterSequence) {
		FilterSequence = filterSequence;
	}

	public String getFilterTable() {
		return FilterTable;
	}

	public void setFilterTable(String filterTable) {
		FilterTable = filterTable;
	}

	public String getFilterColumn() {
		return FilterColumn;
	}

	public void setFilterColumn(String filterColumn) {
		FilterColumn = filterColumn;
	}

	public String getConditionOperation() {
		return ConditionOperation;
	}

	public void setConditionOperation(String conditionOperation) {
		ConditionOperation = conditionOperation;
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

	public int getFilterStatusNt() {
		return FilterStatusNt;
	}

	public void setFilterStatusNt(int filterStatusNt) {
		FilterStatusNt = filterStatusNt;
	}
}
