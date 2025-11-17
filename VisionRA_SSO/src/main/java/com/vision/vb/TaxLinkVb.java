package com.vision.vb;

public class TaxLinkVb  extends CommonVb{
	private String country = "";
	private String leBook = "";
	private String taxLineId = "";

	
	private String taxLineDescription = "";
	private int linkStatusNt=1;
	private int linkStatus=0;
	private String businessLineId="";
	private String businessLineDesc="";
	
	private int matchRuleAt=7008;
	private String matchRule="";
	private int postingTypeAt=7007;
	private String postingType="";
	
	
	
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
	public String getBusinessLineId() {
		return businessLineId;
	}
	public void setBusinessLineId(String businessLineId) {
		this.businessLineId = businessLineId;
	}
	
	public String getMatchRule() {
		return matchRule;
	}
	public void setMatchRule(String matchRule) {
		this.matchRule = matchRule;
	}
	
	public String getPostingType() {
		return postingType;
	}
	public void setPostingType(String postingType) {
		this.postingType = postingType;
	}
	public int getLinkStatusNt() {
		return linkStatusNt;
	}
	public void setLinkStatusNt(int linkStatusNt) {
		this.linkStatusNt = linkStatusNt;
	}
	public int getLinkStatus() {
		return linkStatus;
	}
	public void setLinkStatus(int linkStatus) {
		this.linkStatus = linkStatus;
	}
	public int getMatchRuleAt() {
		return matchRuleAt;
	}
	public void setMatchRuleAt(int matchRuleAt) {
		this.matchRuleAt = matchRuleAt;
	}
	public int getPostingTypeAt() {
		return postingTypeAt;
	}
	public void setPostingTypeAt(int postingTypeAt) {
		this.postingTypeAt = postingTypeAt;
	}
	

	public String getBusinessLineDesc() {
		return businessLineDesc;
	}
	public void setBusinessLineDesc(String businessLineDesc) {
		this.businessLineDesc = businessLineDesc;
	}
}
