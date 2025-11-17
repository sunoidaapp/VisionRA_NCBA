package com.vision.vb;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MenuVb extends CommonVb implements Serializable{

	private static final long serialVersionUID = -2010835009684844752L;
	private String menuProgram = "";
	private String menuName = "";
	private int menuSequence = 0;
	private int parentSequence = 0;
	private String separator = "-1";
	private int menuGroupNt = 6002;
	private int menuGroup = -1;
	private String menuGroupName = "";
	private int menuStatusNt = 1;
	private int menuStatus = -1;
	private ArrayList<MenuVb> children = null;
	private String menuNodeAvailable = "";
	private String applicationAccessAt = "5000";
	private String applicationAccess = "";
	private String profileAdd  = "N";
	private String profileDelete = "N";
	private String profileView = "N";
	private String profileVerification = "N"; 
	private String profileModify = "N";
	private String profileUpload = "N";
	private String profileDownload = "N";
	private String menuIcon = "default";
	
	private List<MenuVb> subMenulst = null;
	
	public String getMenuProgram() {
		return menuProgram;
	}
	public void setMenuProgram(String menuProgram) {
		this.menuProgram = menuProgram;
	}
	public String getMenuName() {
		return menuName;
	}
	public void setMenuName(String menuName) {
		this.menuName = menuName;
	}
	public int getMenuSequence() {
		return menuSequence;
	}
	public void setMenuSequence(int menuSequence) {
		this.menuSequence = menuSequence;
	}
	public int getParentSequence() {
		return parentSequence;
	}
	public void setParentSequence(int parentSequence) {
		this.parentSequence = parentSequence;
	}
	public String getSeparator() {
		return separator;
	}
	public void setSeparator(String separator) {
		this.separator = separator;
	}
	public int getMenuGroupNt() {
		return menuGroupNt;
	}
	public void setMenuGroupNt(int menuGroupNt) {
		this.menuGroupNt = menuGroupNt;
	}
	public int getMenuGroup() {
		return menuGroup;
	}
	public void setMenuGroup(int menuGroup) {
		this.menuGroup = menuGroup;
	}
	public int getMenuStatusNt() {
		return menuStatusNt;
	}
	public void setMenuStatusNt(int menuStatusNt) {
		this.menuStatusNt = menuStatusNt;
	}
	public int getMenuStatus() {
		return menuStatus;
	}
	public void setMenuStatus(int menuStatus) {
		this.menuStatus = menuStatus;
		
	}
	public ArrayList<MenuVb> getChildren() {
		return children;
	}
	public void setChildren(ArrayList<MenuVb> children) {
		this.children = children;
	}
	public String getMenuNodeAvailable() {
		return menuNodeAvailable;
	}
	public void setMenuNodeAvailable(String menuNodeAvailable) {
		this.menuNodeAvailable = menuNodeAvailable;
	}
	public String getApplicationAccessAt() {
		return applicationAccessAt;
	}
	public void setApplicationAccessAt(String applicationAccessAt) {
		this.applicationAccessAt = applicationAccessAt;
	}
	public String getApplicationAccess() {
		return applicationAccess;
	}
	public void setApplicationAccess(String applicationAccess) {
		this.applicationAccess = applicationAccess;
	}
	public String getMenuGroupName() {
		return menuGroupName;
	}
	public void setMenuGroupName(String menuGroupName) {
		this.menuGroupName = menuGroupName;
	}
	public List<MenuVb> getSubMenulst() {
		return subMenulst;
	}
	public void setSubMenulst(List<MenuVb> subMenulst) {
		this.subMenulst = subMenulst;
	}
	public String getProfileAdd() {
		return profileAdd;
	}
	public void setProfileAdd(String profileAdd) {
		this.profileAdd = profileAdd;
	}
	public String getProfileDelete() {
		return profileDelete;
	}
	public void setProfileDelete(String profileDelete) {
		this.profileDelete = profileDelete;
	}
	public String getProfileView() {
		return profileView;
	}
	public void setProfileView(String profileView) {
		this.profileView = profileView;
	}
	public String getProfileVerification() {
		return profileVerification;
	}
	public void setProfileVerification(String profileVerification) {
		this.profileVerification = profileVerification;
	}
	public String getProfileModify() {
		return profileModify;
	}
	public void setProfileModify(String profileModify) {
		this.profileModify = profileModify;
	}
	public String getProfileUpload() {
		return profileUpload;
	}
	public void setProfileUpload(String profileUpload) {
		this.profileUpload = profileUpload;
	}
	public String getProfileDownload() {
		return profileDownload;
	}
	public void setProfileDownload(String profileDownload) {
		this.profileDownload = profileDownload;
	}
	public String getMenuIcon() {
		return menuIcon;
	}
	public void setMenuIcon(String menuIcon) {
		this.menuIcon = menuIcon;
	}
}
