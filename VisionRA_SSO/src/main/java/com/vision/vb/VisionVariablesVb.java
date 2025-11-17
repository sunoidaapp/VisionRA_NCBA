/**
 * 
 */
package com.vision.vb;

import java.util.List;

public class VisionVariablesVb extends CommonVb{

	private static final long serialVersionUID = 2606607394848409278L;
	private String variable = "";//VARIABLE - Key Field
	private String value = "";//VALUE
	private int	variableStatusNt =  0;//VARIABLE_STATUS_NT
	private int	variableStatus =  -1;//VARIABLE_STATUS
	private String readOnly = "N";
	private int vvCategoryAt = 8;
	private String categoryDescription;
	private String vvCategory = "-1";
	List<SmartSearchVb> smartSearchOpt = null;
	
	
	public String getCategoryDescription() {
		return categoryDescription;
	}
	public void setCategoryDescription(String categoryDescription) {
		this.categoryDescription = categoryDescription;
	}
	public int getVvCategoryAt() {
		return vvCategoryAt;
	}
	public void setVvCategoryAt(int vvCategoryAt) {
		this.vvCategoryAt = vvCategoryAt;
	}
	public String getVvCategory() {
		return vvCategory;
	}
	public void setVvCategory(String vvCategory) {
		this.vvCategory = vvCategory;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public String getVariable() {
		return variable;
	}
	public void setVariable(String variable) {
		this.variable = variable;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public int getVariableStatusNt() {
		return variableStatusNt;
	}
	public void setVariableStatusNt(int variableStatusNt) {
		this.variableStatusNt = variableStatusNt;
	}
	public int getVariableStatus() {
		return variableStatus;
	}
	public void setVariableStatus(int variableStatus) {
		this.variableStatus = variableStatus;
	}
	public String getReadOnly() {
		return readOnly;
	}
	public void setReadOnly(String readOnly) {
		this.readOnly = readOnly;
	}
	public List<SmartSearchVb> getSmartSearchOpt() {
		return smartSearchOpt;
	}
	public void setSmartSearchOpt(List<SmartSearchVb> smartSearchOpt) {
		this.smartSearchOpt = smartSearchOpt;
	}
}
