package com.vision.vb;

public class TransLineSbuVb extends CommonVb{

	private String country = "";
	private String leBook = "";
	private String BusinessVerticalAT = null;
	private String transLineId = "";
	private String BusinessVertical = "";
	private String BusinessVerticalDesc = "";
	private int transLineSbuStatusNT = 1;
	private int transLineSbuStatus = 0;

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

	public String getTransLineId() {
		return transLineId;
	}

	public void setTransLineId(String transLineId) {
		this.transLineId = transLineId;
	}

	public String getBusinessVertical() {
		return BusinessVertical;
	}

	public void setBusinessVertical(String businessVertical) {
		BusinessVertical = businessVertical;
	}

	public int getTransLineSbuStatusNT() {
		return transLineSbuStatusNT;
	}

	public void setTransLineSbuStatusNT(int transLineSbuStatusNT) {
		this.transLineSbuStatusNT = transLineSbuStatusNT;
	}

	public int getTransLineSbuStatus() {
		return transLineSbuStatus;
	}

	public void setTransLineSbuStatus(int transLineSbuStatus) {
		this.transLineSbuStatus = transLineSbuStatus;
	}

	public String getBusinessVerticalAT() {
		return BusinessVerticalAT;
	}

	public void setBusinessVerticalAT(String businessVerticalAT) {
		BusinessVerticalAT = businessVerticalAT;
	}

	public String getBusinessVerticalDesc() {
		return BusinessVerticalDesc;
	}

	public void setBusinessVerticalDesc(String businessVerticalDesc) {
		BusinessVerticalDesc = businessVerticalDesc;
	}
}
