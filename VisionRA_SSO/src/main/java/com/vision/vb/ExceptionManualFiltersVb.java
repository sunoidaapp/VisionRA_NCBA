package com.vision.vb;

import java.util.List;

public class ExceptionManualFiltersVb {
	
	private String tableName = "";
	private String sourceTable = "";
	private String colName = "";
	private String aliasName = "";
	private String formatType = "";
	private String magEnableFlag = "";
	private String magType = "";
	private String magSelectionType = "";
	private String magQueryId = "";
	private String filterTable = "";
	private String filterColumn = "";
	private String conditionOperation = "";
	private String conditionValue1 = "";
	private String conditionValue2 = "";
	private String genericCondProduct = "";
	private String genericCondService = "";
	private String specialFilter = "";

	List<ExceptionManualFiltersVb> children = null;
	
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public String getSourceTable() {
		return sourceTable;
	}
	public void setSourceTable(String sourceTable) {
		this.sourceTable = sourceTable;
	}
	public String getColName() {
		return colName;
	}
	public void setColName(String colName) {
		this.colName = colName;
	}
	public String getAliasName() {
		return aliasName;
	}
	public void setAliasName(String aliasName) {
		this.aliasName = aliasName;
	}
	public String getFormatType() {
		return formatType;
	}
	public void setFormatType(String formatType) {
		this.formatType = formatType;
	}
	public String getMagEnableFlag() {
		return magEnableFlag;
	}
	public void setMagEnableFlag(String magEnableFlag) {
		this.magEnableFlag = magEnableFlag;
	}
	public String getMagType() {
		return magType;
	}
	public void setMagType(String magType) {
		this.magType = magType;
	}
	public String getMagSelectionType() {
		return magSelectionType;
	}
	public void setMagSelectionType(String magSelectionType) {
		this.magSelectionType = magSelectionType;
	}
	public String getMagQueryId() {
		return magQueryId;
	}
	public void setMagQueryId(String magQueryId) {
		this.magQueryId = magQueryId;
	}
	public String getFilterTable() {
		return filterTable;
	}
	public void setFilterTable(String filterTable) {
		this.filterTable = filterTable;
	}
	
	public String getFilterColumn() {
		return filterColumn;
	}
	public void setFilterColumn(String filterColumn) {
		this.filterColumn = filterColumn;
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
	public List<ExceptionManualFiltersVb> getChildren() {
		return children;
	}
	public void setChildren(List<ExceptionManualFiltersVb> children) {
		this.children = children;
	}
	public String getGenericCondProduct() {
		return genericCondProduct;
	}
	public void setGenericCondProduct(String genericCondProduct) {
		this.genericCondProduct = genericCondProduct;
	}
	public String getGenericCondService() {
		return genericCondService;
	}
	public void setGenericCondService(String genericCondService) {
		this.genericCondService = genericCondService;
	}
	public String getSpecialFilter() {
		return specialFilter;
	}
	public void setSpecialFilter(String specialFilter) {
		this.specialFilter = specialFilter;
	}
}
