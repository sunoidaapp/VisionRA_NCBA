package com.vision.vb;

import java.util.List;

public class TaxLineConfigDetailsVb extends CommonVb{
	
	private String country = "";
	private String leBook = "";
	private String taxLineId = "";
	private String effectiveDate = "";
	private int productIdAt = 171;
	private String productId = "";
	private String productType = "";
	private String tranCcy = "";
	private String taxAmt = "";
	private String taxPercentage = "";
	private String taxType = "";
	private String plGL = "";
	private String officeAccount = "";
	private String lookupAmountType = "";
	private int lookupAmountTypeAt = 7071;
	private String lookupAmountTypeDesc = "";

	List<TaxConfigTierVb> taxTierlst = null;

	
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
	public String getEffectiveDate() {
		return effectiveDate;
	}
	public void setEffectiveDate(String effectiveDate) {
		this.effectiveDate = effectiveDate;
	}
	public int getProductIdAt() {
		return productIdAt;
	}
	public void setProductIdAt(int productIdAt) {
		this.productIdAt = productIdAt;
	}
	public String getProductId() {
		return productId;
	}
	public void setProductId(String productId) {
		this.productId = productId;
	}
	public String getProductType() {
		return productType;
	}
	public void setProductType(String productType) {
		this.productType = productType;
	}
	public String getTranCcy() {
		return tranCcy;
	}
	public void setTranCcy(String tranCcy) {
		this.tranCcy = tranCcy;
	}
	
	public String getTaxAmt() {
		return taxAmt;
	}
	public void setTaxAmt(String taxAmt) {
		this.taxAmt = taxAmt;
	}
	public String getTaxPercentage() {
		return taxPercentage;
	}
	public void setTaxPercentage(String taxPercentage) {
		this.taxPercentage = taxPercentage;
	}
	public String getTaxType() {
		return taxType;
	}
	public void setTaxType(String taxType) {
		this.taxType = taxType;
	}
	public String getPlGL() {
		return plGL;
	}
	public void setPlGL(String plGL) {
		this.plGL = plGL;
	}
	public String getOfficeAccount() {
		return officeAccount;
	}
	public void setOfficeAccount(String officeAccount) {
		this.officeAccount = officeAccount;
	}
	public List<TaxConfigTierVb> getTaxTierlst() {
		return taxTierlst;
	}
	public void setTaxTierlst(List<TaxConfigTierVb> taxTierlst) {
		this.taxTierlst = taxTierlst;
	}
	public String getLookupAmountType() {
		return lookupAmountType;
	}
	public void setLookupAmountType(String lookupAmountType) {
		this.lookupAmountType = lookupAmountType;
	}
	public int getLookupAmountTypeAt() {
		return lookupAmountTypeAt;
	}
	public void setLookupAmountTypeAt(int lookupAmountTypeAt) {
		this.lookupAmountTypeAt = lookupAmountTypeAt;
	}
	public String getLookupAmountTypeDesc() {
		return lookupAmountTypeDesc;
	}
	public void setLookupAmountTypeDesc(String lookupAmountTypeDesc) {
		this.lookupAmountTypeDesc = lookupAmountTypeDesc;
	}
	
}
