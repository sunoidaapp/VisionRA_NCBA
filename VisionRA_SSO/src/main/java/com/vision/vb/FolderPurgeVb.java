package com.vision.vb;

import org.springframework.stereotype.Component;

@Component
public class FolderPurgeVb extends CommonVb{
String purgeType = "";
String folderPath = "";
String filePattern = "";
String tableName = "";
int purgeDays ;
String lastPurgeDate = "";
int numberOfPurgeFiles ;
int numberOfPurgeTables ;
String archiveFlag = "";
String archivePath = "";
String archiveName = "";

public String getPurgeType() {
	return purgeType;
}
public void setPurgeType(String purgeType) {
	this.purgeType = purgeType;
}
public String getFolderPath() {
	return folderPath;
}
public void setFolderPath(String folderPath) {
	this.folderPath = folderPath;
}
public String getFilePattern() {
	return filePattern;
}
public void setFilePattern(String filePattern) {
	this.filePattern = filePattern;
}
public String getTableName() {
	return tableName;
}
public void setTableName(String tableName) {
	this.tableName = tableName;
}
public int getPurgeDays() {
	return purgeDays;
}
public void setPurgeDays(int purgeDays) {
	this.purgeDays = purgeDays;
}
public String getLastPurgeDate() {
	return lastPurgeDate;
}
public void setLastPurgeDate(String lastPurgeDate) {
	this.lastPurgeDate = lastPurgeDate;
}
public int getNumberOfPurgeFiles() {
	return numberOfPurgeFiles;
}
public void setNumberOfPurgeFiles(int numberOfPurgeFiles) {
	this.numberOfPurgeFiles = numberOfPurgeFiles;
}
public int getNumberOfPurgeTables() {
	return numberOfPurgeTables;
}
public void setNumberOfPurgeTables(int numberOfPurgeTables) {
	this.numberOfPurgeTables = numberOfPurgeTables;
}
public String getArchiveFlag() {
	return archiveFlag;
}
public void setArchiveFlag(String archiveFlag) {
	this.archiveFlag = archiveFlag;
}
public String getArchivePath() {
	return archivePath;
}
public void setArchivePath(String archivePath) {
	this.archivePath = archivePath;
}
public String getArchiveName() {
	return archiveName;
}
public void setArchiveName(String archiveName) {
	this.archiveName = archiveName;
}

}
