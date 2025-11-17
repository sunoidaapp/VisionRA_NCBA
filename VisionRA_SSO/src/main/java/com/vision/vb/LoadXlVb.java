package com.vision.vb;

public class LoadXlVb extends CommonVb {
	private int numberOfSheets = 0 ;
	private String sheetName = "";
	private int numberOfRows = 0;
	
	
	public int getNumberOfSheets() {
		return numberOfSheets;
	}
	public void setNumberOfSheets(int numberOfSheets) {
		this.numberOfSheets = numberOfSheets;
	}
	public String getSheetName() {
		return sheetName;
	}
	public void setSheetName(String sheetName) {
		this.sheetName = sheetName;
	}
	public int getNumberOfRows() {
		return numberOfRows;
	}
	public void setNumberOfRows(int numberOfRows) {
		this.numberOfRows = numberOfRows;
	}
}
