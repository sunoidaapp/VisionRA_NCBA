package com.vision.vb;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class ConcessionBlLinkVb extends CommonVb {
	private String country = "";
	private String leBook = "";
	private String businessLineId = "";
	private String concessionId = "";
	private String concessionDesc = "";
	private String concessionPriority = "";
	private String concessionSubPriority = "";
	private String concessionAgg = "";
	private int StatusNt = 1;
	private int Status = 0;
	private String StatusDesc = "";
	List<ConcessionBlLinkVb> concessionPriorityList = new ArrayList<>();
	LinkedHashMap<Integer, List<ConcessionBlLinkVb>> subPriorityMap = new LinkedHashMap<>();

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

	public String getConcessionId() {
		return concessionId;
	}

	public void setConcessionId(String concessionId) {
		this.concessionId = concessionId;
	}

	public String getConcessionPriority() {
		return concessionPriority;
	}

	public void setConcessionPriority(String concessionPriority) {
		this.concessionPriority = concessionPriority;
	}

	public String getConcessionSubPriority() {
		return concessionSubPriority;
	}

	public void setConcessionSubPriority(String concessionSubPriority) {
		this.concessionSubPriority = concessionSubPriority;
	}

	public String getConcessionAgg() {
		return concessionAgg;
	}

	public void setConcessionAgg(String concessionAgg) {
		this.concessionAgg = concessionAgg;
	}

	public List<ConcessionBlLinkVb> getConcessionPriorityList() {
		return concessionPriorityList;
	}

	public void setConcessionPriorityList(List<ConcessionBlLinkVb> concessionPriorityList) {
		this.concessionPriorityList = concessionPriorityList;
	}

	public String getConcessionDesc() {
		return concessionDesc;
	}

	public void setConcessionDesc(String concessionDesc) {
		this.concessionDesc = concessionDesc;
	}

	public int getStatusNt() {
		return StatusNt;
	}

	public void setStatusNt(int statusNt) {
		StatusNt = statusNt;
	}

	public int getStatus() {
		return Status;
	}

	public void setStatus(int status) {
		Status = status;
	}

	public String getStatusDesc() {
		return StatusDesc;
	}

	public void setStatusDesc(String statusDesc) {
		StatusDesc = statusDesc;
	}

	public LinkedHashMap<Integer, List<ConcessionBlLinkVb>> getSubPriorityMap() {
		return subPriorityMap;
	}

	public void setSubPriorityMap(LinkedHashMap<Integer, List<ConcessionBlLinkVb>> subPriorityMap) {
		this.subPriorityMap = subPriorityMap;
	}
}
