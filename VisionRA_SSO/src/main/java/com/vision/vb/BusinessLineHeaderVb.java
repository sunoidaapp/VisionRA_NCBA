package com.vision.vb;

import java.util.List;

public class BusinessLineHeaderVb extends CommonVb{

	private String country = "";
	private String leBook = "";
	private String businessLineId = "";
	private String businessLineDescription = "";
	private String transLineId = "";
	private String transLineIdDesc = "";
	private int transLineTypeAT = 7006;
	private String transLineType = "";
	private String transLineTypeDesc = "";
	private String tranLineGrp = "";
	private String tranLineGrpDesc = "";	
	private int businessLineTypeAT = 7028;
	private String businessLineType = "";
	private String businessLineTypeDesc = "";
	private int IncomeExpenseTypeAT = 7013;
	private String IncomeExpenseType = "";
	private String IncomeExpenseTypeDesc = "";
	private int businessLineStatusNT = 1;
	private int businessLineStatus = 0;
	private String businessLineStatusDesc = "";
	private String actualIePosting = "";
	private String actualIeMatchRule = "";
	private String actualIePostingDesc = "";
	private String actualIeMatchRuleDesc = "";
	private String feeCalcTimeStampFlag = "N";
	private boolean feesFlag = false;
	private boolean businessFlag = false;

	
	List<BusinessLineGLVb> BusinessLineGllst = null;
	List<SmartSearchVb> smartSearchOpt = null;
	private String feeLineCount ="";
	List<BlReconRuleVb> blReconRulelst = null;
	List<BlReconRuleVb> taxBlReconRulelst = null;
	List<FeesConfigHeaderVb> feeConfiglst = null;

	
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
	public String getBusinessLineId() {
		return businessLineId;
	}
	public void setBusinessLineId(String businessLineId) {
		this.businessLineId = businessLineId;
	}
	public String getBusinessLineDescription() {
		return businessLineDescription;
	}
	public void setBusinessLineDescription(String businessLineDescription) {
		this.businessLineDescription = businessLineDescription;
	}
	public int getTransLineTypeAT() {
		return transLineTypeAT;
	}
	public void setTransLineTypeAT(int transLineTypeAT) {
		this.transLineTypeAT = transLineTypeAT;
	}
	public String getTransLineType() {
		return transLineType;
	}
	public void setTransLineType(String transLineType) {
		this.transLineType = transLineType;
	}
	public String getTransLineTypeDesc() {
		return transLineTypeDesc;
	}
	public void setTransLineTypeDesc(String transLineTypeDesc) {
		this.transLineTypeDesc = transLineTypeDesc;
	}
	public int getBusinessLineTypeAT() {
		return businessLineTypeAT;
	}
	public void setBusinessLineTypeAT(int businessLineTypeAT) {
		this.businessLineTypeAT = businessLineTypeAT;
	}
	public String getBusinessLineType() {
		return businessLineType;
	}
	public void setBusinessLineType(String businessLineType) {
		this.businessLineType = businessLineType;
	}
	public String getBusinessLineTypeDesc() {
		return businessLineTypeDesc;
	}
	public void setBusinessLineTypeDesc(String businessLineTypeDesc) {
		this.businessLineTypeDesc = businessLineTypeDesc;
	}
	public int getIncomeExpenseTypeAT() {
		return IncomeExpenseTypeAT;
	}
	public void setIncomeExpenseTypeAT(int incomeExpenseTypeAT) {
		IncomeExpenseTypeAT = incomeExpenseTypeAT;
	}
	public String getIncomeExpenseType() {
		return IncomeExpenseType;
	}
	public void setIncomeExpenseType(String incomeExpenseType) {
		IncomeExpenseType = incomeExpenseType;
	}
	public String getIncomeExpenseTypeDesc() {
		return IncomeExpenseTypeDesc;
	}
	public void setIncomeExpenseTypeDesc(String incomeExpenseTypeDesc) {
		IncomeExpenseTypeDesc = incomeExpenseTypeDesc;
	}
	public int getBusinessLineStatusNT() {
		return businessLineStatusNT;
	}
	public void setBusinessLineStatusNT(int businessLineStatusNT) {
		this.businessLineStatusNT = businessLineStatusNT;
	}
	public int getBusinessLineStatus() {
		return businessLineStatus;
	}
	public void setBusinessLineStatus(int businessLineStatus) {
		this.businessLineStatus = businessLineStatus;
	}
	public String getBusinessLineStatusDesc() {
		return businessLineStatusDesc;
	}
	public void setBusinessLineStatusDesc(String businessLineStatusDesc) {
		this.businessLineStatusDesc = businessLineStatusDesc;
	}
	public List<BusinessLineGLVb> getBusinessLineGllst() {
		return BusinessLineGllst;
	}
	public void setBusinessLineGllst(List<BusinessLineGLVb> businessLineGllst) {
		BusinessLineGllst = businessLineGllst;
	}
	public List<SmartSearchVb> getSmartSearchOpt() {
		return smartSearchOpt;
	}
	public void setSmartSearchOpt(List<SmartSearchVb> smartSearchOpt) {
		this.smartSearchOpt = smartSearchOpt;
	}
	public String getTransLineId() {
		return transLineId;
	}
	public void setTransLineId(String transLineId) {
		this.transLineId = transLineId;
	}
	public String getTransLineIdDesc() {
		return transLineIdDesc;
	}
	public void setTransLineIdDesc(String transLineIdDesc) {
		this.transLineIdDesc = transLineIdDesc;
	}
	public String getTranLineGrp() {
		return tranLineGrp;
	}
	public void setTranLineGrp(String tranLineGrp) {
		this.tranLineGrp = tranLineGrp;
	}
	public String getTranLineGrpDesc() {
		return tranLineGrpDesc;
	}
	public void setTranLineGrpDesc(String tranLineGrpDesc) {
		this.tranLineGrpDesc = tranLineGrpDesc;
	}
	public String getActualIePosting() {
		return actualIePosting;
	}
	public void setActualIePosting(String actualIePosting) {
		this.actualIePosting = actualIePosting;
	}
	public String getActualIeMatchRule() {
		return actualIeMatchRule;
	}
	public void setActualIeMatchRule(String actualIeMatchRule) {
		this.actualIeMatchRule = actualIeMatchRule;
	}
	public String getFeeLineCount() {
		return feeLineCount;
	}
	public void setFeeLineCount(String feeLineCount) {
		this.feeLineCount = feeLineCount;
	}
	public List<BlReconRuleVb> getBlReconRulelst() {
		return blReconRulelst;
	}
	public void setBlReconRulelst(List<BlReconRuleVb> blReconRulelst) {
		this.blReconRulelst = blReconRulelst;
	}
	public String getFeeCalcTimeStampFlag() {
		return feeCalcTimeStampFlag;
	}
	public void setFeeCalcTimeStampFlag(String feeCalcTimeStampFlag) {
		this.feeCalcTimeStampFlag = feeCalcTimeStampFlag;
	}
	public List<FeesConfigHeaderVb> getFeeConfiglst() {
		return feeConfiglst;
	}
	public void setFeeConfiglst(List<FeesConfigHeaderVb> feeConfiglst) {
		this.feeConfiglst = feeConfiglst;
	}
	public String getActualIePostingDesc() {
		return actualIePostingDesc;
	}
	public void setActualIePostingDesc(String actualIePostingDesc) {
		this.actualIePostingDesc = actualIePostingDesc;
	}
	public String getActualIeMatchRuleDesc() {
		return actualIeMatchRuleDesc;
	}
	public void setActualIeMatchRuleDesc(String actualIeMatchRuleDesc) {
		this.actualIeMatchRuleDesc = actualIeMatchRuleDesc;
	}
	public boolean isFeesFlag() {
		return feesFlag;
	}
	public void setFeesFlag(boolean feesFlag) {
		this.feesFlag = feesFlag;
	}
	public boolean isBusinessFlag() {
		return businessFlag;
	}
	public void setBusinessFlag(boolean businessFlag) {
		this.businessFlag = businessFlag;
	}
	public void setTaxBlReconRulelst(List<BlReconRuleVb> taxBlReconRulelst) {
		this.taxBlReconRulelst = taxBlReconRulelst;
	}
	
		public List<BlReconRuleVb> getTaxBlReconRulelst() {
		return taxBlReconRulelst;
	}
}
