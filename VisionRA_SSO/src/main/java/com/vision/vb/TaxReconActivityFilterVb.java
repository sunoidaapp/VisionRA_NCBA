package com.vision.vb;

import java.util.List;

public class TaxReconActivityFilterVb extends CommonVb {

	private String ruleId = "";
	private int ruleIdAt = 7008;
	private String conditionType = "";

	private int filterSequence ;
	private String lhsFilterTable = "";
	private String lhsFilterColumn = "";
	private String rhsFilterTable = "";
	private String rhsFilterColumn = "";
	private String conditionOperation = "";
	
	private int fromTableId;
	private int toTableId;
	private int joinTypeNt = 41;
	private int joinType;
	private int tabStatusNt = 1;
	private int tabStatus;
	private String filterTableId;

	private String joinString1 = "";
	private String joinString2 = "";
	private String joinString3 = "";
	private String joinString4 = "";
	private String joinString5 = "";

	private String filterConditon1 = "";
	private String filterConditon2 = "";
	private String filterConditon3 = "";
	private String filterConditon4 = "";
	private String filterConditon5 = "";

	private String conditionValue1 = "";
	private String conditionValue2 = "";

	private int filterStatusNt = 1;
	private int filterStatus;
	private int refNo = 0;

	List<TaxReconActivityFilterVb> joinsLst = null;
	private String lhsFilterColType = "C";
	private String rhsFilterColType = "C";
	
	public String getRuleId() {
		return ruleId;
	}

	public void setRuleId(String ruleId) {
		this.ruleId = ruleId;
	}
	public int getRuleIdAt() {
		return ruleIdAt;
	}

	public void setRuleIdAt(int ruleIdAt) {
		this.ruleIdAt = ruleIdAt;
	}

	public String getConditionType() {
		return conditionType;
	}

	public void setConditionType(String conditionType) {
		this.conditionType = conditionType;
	}

	public int getFilterSequence() {
		return filterSequence;
	}

	public void setFilterSequence(int filterSequence) {
		this.filterSequence = filterSequence;
	}

	public String getLhsFilterTable() {
		return lhsFilterTable;
	}

	public void setLhsFilterTable(String lhsFilterTable) {
		this.lhsFilterTable = lhsFilterTable;
	}

	public String getLhsFilterColumn() {
		return lhsFilterColumn;
	}

	public void setLhsFilterColumn(String lhsFilterColumn) {
		this.lhsFilterColumn = lhsFilterColumn;
	}

	public String getRhsFilterTable() {
		return rhsFilterTable;
	}

	public void setRhsFilterTable(String rhsFilterTable) {
		this.rhsFilterTable = rhsFilterTable;
	}

	public String getRhsFilterColumn() {
		return rhsFilterColumn;
	}

	public void setRhsFilterColumn(String rhsFilterColumn) {
		this.rhsFilterColumn = rhsFilterColumn;
	}

	public String getConditionOperation() {
		return conditionOperation;
	}

	public void setConditionOperation(String conditionOperation) {
		this.conditionOperation = conditionOperation;
	}

	public String getConditionValue1() {
		return conditionValue1;
	}

	public void setConditionValue1(String conditionValue1) {
		this.conditionValue1 = conditionValue1;
	}

	public String getConditionValue2() {
		return conditionValue2;
	}

	public void setConditionValue2(String conditionValue2) {
		this.conditionValue2 = conditionValue2;
	}

	public int getFilterStatusNt() {
		return filterStatusNt;
	}

	public void setFilterStatusNt(int filterStatusNt) {
		this.filterStatusNt = filterStatusNt;
	}

	public int getFilterStatus() {
		return filterStatus;
	}

	public void setFilterStatus(int filterStatus) {
		this.filterStatus = filterStatus;
	}

	public List<TaxReconActivityFilterVb> getJoinsLst() {
		return joinsLst;
	}

	public void setJoinsLst(List<TaxReconActivityFilterVb> joinsLst) {
		this.joinsLst = joinsLst;
	}

	public int getFromTableId() {
		return fromTableId;
	}

	public void setFromTableId(int fromTableId) {
		this.fromTableId = fromTableId;
	}

	public int getToTableId() {
		return toTableId;
	}

	public void setToTableId(int toTableId) {
		this.toTableId = toTableId;
	}

	public int getJoinTypeNt() {
		return joinTypeNt;
	}

	public void setJoinTypeNt(int joinTypeNt) {
		this.joinTypeNt = joinTypeNt;
	}

	public int getJoinType() {
		return joinType;
	}

	public void setJoinType(int joinType) {
		this.joinType = joinType;
	}

	public int getTabStatusNt() {
		return tabStatusNt;
	}

	public void setTabStatusNt(int tabStatusNt) {
		this.tabStatusNt = tabStatusNt;
	}

	public int getTabStatus() {
		return tabStatus;
	}

	public void setTabStatus(int tabStatus) {
		this.tabStatus = tabStatus;
	}

	public String getJoinString1() {
		return joinString1;
	}

	public void setJoinString1(String joinString1) {
		this.joinString1 = joinString1;
	}

	public String getJoinString2() {
		return joinString2;
	}

	public void setJoinString2(String joinString2) {
		this.joinString2 = joinString2;
	}

	public String getJoinString3() {
		return joinString3;
	}

	public void setJoinString3(String joinString3) {
		this.joinString3 = joinString3;
	}

	public String getJoinString4() {
		return joinString4;
	}

	public void setJoinString4(String joinString4) {
		this.joinString4 = joinString4;
	}

	public String getJoinString5() {
		return joinString5;
	}

	public void setJoinString5(String joinString5) {
		this.joinString5 = joinString5;
	}

	public String getFilterConditon1() {
		return filterConditon1;
	}

	public void setFilterConditon1(String filterConditon1) {
		this.filterConditon1 = filterConditon1;
	}

	public String getFilterConditon2() {
		return filterConditon2;
	}

	public void setFilterConditon2(String filterConditon2) {
		this.filterConditon2 = filterConditon2;
	}

	public String getFilterConditon3() {
		return filterConditon3;
	}

	public void setFilterConditon3(String filterConditon3) {
		this.filterConditon3 = filterConditon3;
	}

	public String getFilterConditon4() {
		return filterConditon4;
	}

	public void setFilterConditon4(String filterConditon4) {
		this.filterConditon4 = filterConditon4;
	}

	public String getFilterConditon5() {
		return filterConditon5;
	}

	public void setFilterConditon5(String filterConditon5) {
		this.filterConditon5 = filterConditon5;
	}

	public String getFilterTableId() {
		return filterTableId;
	}

	public void setFilterTableId(String filterTableId) {
		this.filterTableId = filterTableId;
	}

	public int getRefNo() {
		return refNo;
	}

	public void setRefNo(int refNo) {
		this.refNo = refNo;
	}

	public String getLhsFilterColType() {
		return lhsFilterColType;
	}

	public void setLhsFilterColType(String lhsFilterColType) {
		this.lhsFilterColType = lhsFilterColType;
	}

	public String getRhsFilterColType() {
		return rhsFilterColType;
	}

	public void setRhsFilterColType(String rhsFilterColType) {
		this.rhsFilterColType = rhsFilterColType;
	}
}
