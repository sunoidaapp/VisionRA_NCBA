package com.vision.vb;

import java.util.List;

public class TaxLineConfigHeaderVb extends CommonVb{

	private String country = "";
	private String leBook = "";
	private String taxLineId = "";
	private String taxLineDescription = "";
	private String effectiveDate = null;
	
	private int taxCcyAT = 7030;
	private String taxCcy = "";
	private String taxCcyDesc = "";

	private int taxBasisAT = 7032;
	private String taxBasis = "";
	private String taxBasisDesc = "";

	private int taxChargeTypeAT = 7033;
	private String taxChargeType = "F";
	private String taxChargeTypeDesc = "";

	private int tierTypeAT = 7034;
	private String tierType = "F";
	private String tierTypeDesc = "";
	

	private int taxLineStatusNT = 1;
	private int taxLineStatus = 0;
	private String taxLineStatusDesc = "";
	
	List<SmartSearchVb> smartSearchOpt = null;
	List<TaxLineConfigDetailsVb> taxLineConfigDetaillst = null;
	List<TaxConfigTierVb> taxTierlst = null;
	List<TaxLinkVb> taxLinklst = null;

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

	public String getTaxLineId() {
		return taxLineId;
	}

	public void setTaxLineId(String taxLineId) {
		this.taxLineId = taxLineId;
	}

	public String getTaxLineDescription() {
		return taxLineDescription;
	}

	public void setTaxLineDescription(String taxLineDescription) {
		this.taxLineDescription = taxLineDescription;
	}

	public String getEffectiveDate() {
		return effectiveDate;
	}

	public void setEffectiveDate(String effectiveDate) {
		this.effectiveDate = effectiveDate;
	}

	public int getTaxCcyAT() {
		return taxCcyAT;
	}

	public void setTaxCcyAT(int taxCcyAT) {
		this.taxCcyAT = taxCcyAT;
	}

	public String getTaxCcy() {
		return taxCcy;
	}

	public void setTaxCcy(String taxCcy) {
		this.taxCcy = taxCcy;
	}

	public int getTaxBasisAT() {
		return taxBasisAT;
	}

	public void setTaxBasisAT(int taxBasisAT) {
		this.taxBasisAT = taxBasisAT;
	}

	public String getTaxBasis() {
		return taxBasis;
	}

	public void setTaxBasis(String taxBasis) {
		this.taxBasis = taxBasis;
	}

	public int getTaxChargeTypeAT() {
		return taxChargeTypeAT;
	}

	public void setTaxChargeTypeAT(int taxChargeTypeAT) {
		this.taxChargeTypeAT = taxChargeTypeAT;
	}

	public String getTaxChargeType() {
		return taxChargeType;
	}

	public void setTaxChargeType(String taxChargeType) {
		this.taxChargeType = taxChargeType;
	}

	public int getTierTypeAT() {
		return tierTypeAT;
	}

	public void setTierTypeAT(int tierTypeAT) {
		this.tierTypeAT = tierTypeAT;
	}

	public String getTierType() {
		return tierType;
	}

	public void setTierType(String tierType) {
		this.tierType = tierType;
	}

	public int getTaxLineStatusNT() {
		return taxLineStatusNT;
	}

	public void setTaxLineStatusNT(int taxLineStatusNT) {
		this.taxLineStatusNT = taxLineStatusNT;
	}

	public int getTaxLineStatus() {
		return taxLineStatus;
	}

	public void setTaxLineStatus(int taxLineStatus) {
		this.taxLineStatus = taxLineStatus;
	}

	public String getTaxLineStatusDesc() {
		return taxLineStatusDesc;
	}

	public void setTaxLineStatusDesc(String taxLineStatusDesc) {
		this.taxLineStatusDesc = taxLineStatusDesc;
	}

	public List<SmartSearchVb> getSmartSearchOpt() {
		return smartSearchOpt;
	}

	public String getTaxCcyDesc() {
		return taxCcyDesc;
	}

	public void setTaxCcyDesc(String taxCcyDesc) {
		this.taxCcyDesc = taxCcyDesc;
	}

	public String getTaxBasisDesc() {
		return taxBasisDesc;
	}

	public void setTaxBasisDesc(String taxBasisDesc) {
		this.taxBasisDesc = taxBasisDesc;
	}

	public String getTaxChargeTypeDesc() {
		return taxChargeTypeDesc;
	}

	public void setTaxChargeTypeDesc(String taxChargeTypeDesc) {
		this.taxChargeTypeDesc = taxChargeTypeDesc;
	}

	public String getTierTypeDesc() {
		return tierTypeDesc;
	}

	public void setTierTypeDesc(String tierTypeDesc) {
		this.tierTypeDesc = tierTypeDesc;
	}

	public void setSmartSearchOpt(List<SmartSearchVb> smartSearchOpt) {
		this.smartSearchOpt = smartSearchOpt;
	}

	public List<TaxLineConfigDetailsVb> getTaxLineConfigDetaillst() {
		return taxLineConfigDetaillst;
	}

	public void setTaxLineConfigDetaillst(List<TaxLineConfigDetailsVb> taxLineConfigDetaillst) {
		this.taxLineConfigDetaillst = taxLineConfigDetaillst;
	}

	public List<TaxConfigTierVb> getTaxTierlst() {
		return taxTierlst;
	}

	public void setTaxTierlst(List<TaxConfigTierVb> taxTierlst) {
		this.taxTierlst = taxTierlst;
	}

	public List<TaxLinkVb> getTaxLinklst() {
		return taxLinklst;
	}

	public void setTaxLinklst(List<TaxLinkVb> taxLinklst) {
		this.taxLinklst = taxLinklst;
	}
}
