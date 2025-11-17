package com.vision.vb;

import java.io.Serializable;

public class CurrencyDetailsVb implements Serializable{
	
	public String cleb = "";
	public String ccyConversionType = "";
	public String fromCcy = "";
	public String toCcy = "";
	public String crossCcyRate = "";
	public int decimals = 2;
	public String bookCcy = "";
	
	public String getCcyConversionType() {
		return ccyConversionType;
	}
	public void setCcyConversionType(String ccyConversionType) {
		this.ccyConversionType = ccyConversionType;
	}
	public String getFromCcy() {
		return fromCcy;
	}
	public void setFromCcy(String fromCcy) {
		this.fromCcy = fromCcy;
	}
	public String getToCcy() {
		return toCcy;
	}
	public void setToCcy(String toCcy) {
		this.toCcy = toCcy;
	}
	public String getCrossCcyRate() {
		return crossCcyRate;
	}
	public void setCrossCcyRate(String crossCcyRate) {
		this.crossCcyRate = crossCcyRate;
	}
	public int getDecimals() {
		return decimals;
	}
	public void setDecimals(int decimals) {
		this.decimals = decimals;
	}
	public String getCleb() {
		return cleb;
	}
	public void setCleb(String cleb) {
		this.cleb = cleb;
	}
	public String getBookCcy() {
		return bookCcy;
	}
	public void setBookCcy(String bookCcy) {
		this.bookCcy = bookCcy;
	}
}
