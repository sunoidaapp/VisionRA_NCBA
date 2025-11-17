package com.vision.vb;

import java.util.ArrayList;
import java.util.List;

public class ReconHeaderVb extends CommonVb {
	private	String tableName = "";
	private String tableDescription = "";
	private int ruleIdAt = 7008;
	private String ruleId = "";
	private int tableId = 0;
	private String aliasName = "";
	private int reconStatusNt = 1;
	private int reconStatus ;
	private String reconStatusDesc ;
	
	private String colName ="";
	private int colId = 0;
	private String colType ;
	private String colAliasName = "";
	private String aggFunc ="";
	private String groupBy ="";
	private String filterConditon1 ="";
	private String joinString2 ="";
	private String dataType ="";
	private int refNo = 0;
	private String query = "";
	
	
	private ArrayList<ReconHeaderVb> tableNamesList = new ArrayList<>();
	private	List<ReconHeaderVb> children = new ArrayList<>();
        private List<ReconHeaderVb> selectionCriteriaLst = null;
	private List<ReconActivityFilterVb> relationJoinsLst = null;
	List<ReconActivityFilterVb> filtersLst = new ArrayList<ReconActivityFilterVb>();
	
	private String orderBy = "0";

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getTableDescription() {
		return tableDescription;
	}

	public void setTableDescription(String tableDescription) {
		this.tableDescription = tableDescription;
	}


	public List<ReconHeaderVb> getChildren() {
		return children;
	}

	public void setChildren(List<ReconHeaderVb> children) {
		this.children = children;
	}

	public int getRuleIdAt() {
		return ruleIdAt;
	}

	public void setRuleIdAt(int ruleIdAt) {
		this.ruleIdAt = ruleIdAt;
	}

	public String getRuleId() {
		return ruleId;
	}

	public void setRuleId(String ruleId) {
		this.ruleId = ruleId;
	}

	public int getTableId() {
		return tableId;
	}

	public void setTableId(int tableId) {
		this.tableId = tableId;
	}

	public String getAliasName() {
		return aliasName;
	}

	public void setAliasName(String aliasName) {
		this.aliasName = aliasName;
	}

	public int getReconStatusNt() {
		return reconStatusNt;
	}

	public void setReconStatusNt(int reconStatusNt) {
		this.reconStatusNt = reconStatusNt;
	}

	public int getReconStatus() {
		return reconStatus;
	}

	public void setReconStatus(int reconStatus) {
		this.reconStatus = reconStatus;
	}

	public String getColName() {
		return colName;
	}

	public void setColName(String colName) {
		this.colName = colName;
	}

	public int getColId() {
		return colId;
	}

	public void setColId(int colId) {
		this.colId = colId;
	}

	

	

	public String getAggFunc() {
		return aggFunc;
	}

	public void setAggFunc(String aggFunc) {
		this.aggFunc = aggFunc;
	}

	public String getGroupBy() {
		return groupBy;
	}

	public void setGroupBy(String groupBy) {
		this.groupBy = groupBy;
	}

	public String getColAliasName() {
		return colAliasName;
	}

	public void setColAliasName(String colAliasName) {
		this.colAliasName = colAliasName;
	}

	public String getColType() {
		return colType;
	}

	public void setColType(String colType) {
		this.colType = colType;
	}

	public List<ReconHeaderVb> getSelectionCriteriaLst() {
		return selectionCriteriaLst;
	}

	public void setSelectionCriteriaLst(List<ReconHeaderVb> selectionCriteriaLst) {
		this.selectionCriteriaLst = selectionCriteriaLst;
	}

	public List<ReconActivityFilterVb> getFiltersLst() {
		return filtersLst;
	}

	public void setFiltersLst(List<ReconActivityFilterVb> filtersLst) {
		this.filtersLst = filtersLst;
	}

	public List<ReconActivityFilterVb> getRelationJoinsLst() {
		return relationJoinsLst;
	}

	public void setRelationJoinsLst(List<ReconActivityFilterVb> relationJoinsLst) {
		this.relationJoinsLst = relationJoinsLst;
	}

	public String getReconStatusDesc() {
		return reconStatusDesc;
	}

	public void setReconStatusDesc(String reconStatusDesc) {
		this.reconStatusDesc = reconStatusDesc;
	}

	public ArrayList<ReconHeaderVb> getTableNamesList() {
		return tableNamesList;
	}

	public void setTableNamesList(ArrayList<ReconHeaderVb> tableNamesList) {
		this.tableNamesList = tableNamesList;
	}

	public String getFilterConditon1() {
		return filterConditon1;
	}

	public void setFilterConditon1(String filterConditon1) {
		this.filterConditon1 = filterConditon1;
	}

	public String getJoinString2() {
		return joinString2;
	}

	public void setJoinString2(String joinString2) {
		this.joinString2 = joinString2;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public int getRefNo() {
		return refNo;
	}

	public void setRefNo(int refNo) {
		this.refNo = refNo;
	}

	public String getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

}
