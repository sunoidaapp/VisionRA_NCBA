package com.vision.dao;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.UncategorizedSQLException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.vision.exception.ExceptionCode;
import com.vision.exception.RuntimeCustomException;
import com.vision.util.Constants;
import com.vision.vb.CommonVb;
import com.vision.vb.EtlPostingsVb;
@Component
public class EtlServiceDao extends AbstractDao<CommonVb>{
	
	@Value("${app.databaseType}")
	private String databaseType;
	@Value("${app.productName}")
	private String productName;
	@Autowired
	CommonDao commonDao;
	
	
	
	public ExceptionCode getEtlPostngDetailGenBuild(){
		ExceptionCode exceptionCode = new ExceptionCode();
		HashMap<String,String> postingData = new HashMap<String,String>();
		try
		{	
			String orginalQuery= "";
			if ("ORACLE".equalsIgnoreCase(databaseType)) {
				orginalQuery = " SELECT COUNTRY,LE_BOOK,EXTRACTION_FREQUENCY,EXTRACTION_SEQUENCE,EXTRACTION_FEED_ID, "+
						" POSTING_SEQUENCE,BUSINESS_DATE,POSTING_DATE,POSTING_FT_DATE,EXTRACTION_ENGINE FROM( "+
						" SELECT ROWNUM RN,COUNTRY,LE_BOOK,EXTRACTION_FREQUENCY,EXTRACTION_SEQUENCE,EXTRACTION_FEED_ID, "+
						" POSTING_SEQUENCE,TO_CHAR(TO_DATE(BUSINESS_DATE),'yyyy-MM-dd') BUSINESS_DATE,TO_CHAR(POSTING_DATE,'DD-Mon-RRRR HH24:MI:SS') POSTING_DATE,"+
						" TO_CHAR(TO_DATE(POSTING_DATE),'yyyy-MM-dd') POSTING_FT_DATE,EXTRACTION_ENGINE FROM "+
						" ( "+
						" SELECT * "+
						" FROM RA_TRN_POSTING "+
						" WHERE TO_DATE(SCHEDULE_TIME) <= TO_DATE(SYSDATE) "+
						" AND EXTRACTION_ENGINE IN ('BLD','CHG','RCY','REC','MST') "+
						" AND DEPENDENT_FLAG = 'N' "+
						" AND POSTED_STATUS = 'P' "+
						" UNION ALL "+
						" SELECT * "+
						" FROM RA_TRN_POSTING "+
						" WHERE TO_DATE(SCHEDULE_TIME) <= TO_DATE(SYSDATE) "+
						" AND EXTRACTION_ENGINE IN (SELECT ALPHA_SUB_TAB FROM ALPHA_SUB_TAB WHERE ALPHA_TAB = 7035 AND ALPHA_SUB_TAB!='ETL') "+
						" AND DEPENDENT_FLAG = 'Y' "+
						" AND POSTED_STATUS = 'P' "+
						" AND DEPENDENT_FEED_ID NOT IN (SELECT EXTRACTION_FEED_ID FROM RA_TRN_POSTING) "+
						" ) T1 ORDER BY COUNTRY,LE_BOOK,EXTRACTION_FREQUENCY,EXTRACTION_SEQUENCE,EXTRACTION_FEED_ID, "+
						" POSTING_SEQUENCE) "+
						" WHERE RN <=1";
			}else if ("MSSQL".equalsIgnoreCase(databaseType)){
				orginalQuery = " SELECT top 1.COUNTRY,LE_BOOK,EXTRACTION_FREQUENCY,EXTRACTION_SEQUENCE,EXTRACTION_FEED_ID, "+ 
						" POSTING_SEQUENCE,CONVERT(DATE,BUSINESS_DATE) BUSINESS_DATE,POSTING_DATE,FORMAT(POSTING_DATE,'yyyy-MM-dd') POSTING_FT_DATE,EXTRACTION_ENGINE FROM  "+
						" ( "+
						" SELECT *  "+
						" FROM RA_TRN_POSTING "+
						" WHERE CONVERT(VARCHAR,SCHEDULE_TIME,108) <= CONVERT(VARCHAR,getdate(),108) "+
						" AND EXTRACTION_ENGINE IN (SELECT ALPHA_SUB_TAB FROM ALPHA_SUB_TAB WHERE ALPHA_TAB = 7035 AND ALPHA_SUB_TAB!='ETL') "+
						" AND DEPENDENT_FLAG = 'N'  "+
						" AND POSTED_STATUS = 'P' "+
						" UNION ALL "+
						" SELECT * "+
						" FROM RA_TRN_POSTING S1 "+
						" WHERE CONVERT(VARCHAR,SCHEDULE_TIME,108) <= CONVERT(VARCHAR,getdate(),108) "+
						" AND EXTRACTION_ENGINE IN ('BLD','CHG','RCY','REC') "+
						" AND DEPENDENT_FLAG = 'Y' "+
						" AND POSTED_STATUS = 'P' "+
						" AND DEPENDENT_FEED_ID IN (SELECT S2.EXTRACTION_FEED_ID FROM RA_TRN_POSTING_HISTORY S2 WHERE "+
						"     S2.POSTING_SEQUENCE = S1.POSTING_SEQUENCE AND S2.POSTED_STATUS ='C' "+
						"	 AND S2.POSTING_DATE = S1.POSTING_DATE AND S2.BUSINESS_DATE = S1.BUSINESS_DATE "+
						"	 ) "+
						" ) T1 ORDER BY COUNTRY,LE_BOOK,EXTRACTION_FREQUENCY,EXTRACTION_SEQUENCE,EXTRACTION_FEED_ID, "+
						" POSTING_SEQUENCE";
			}
			
			ResultSetExtractor mapper = new ResultSetExtractor() {
				@Override
				public Object extractData(ResultSet rs)  throws SQLException, DataAccessException {
					ResultSetMetaData metaData = rs.getMetaData();
					int colCount = metaData.getColumnCount();
					Boolean dataPresent = false;
					while(rs.next()){
						dataPresent = true;
						for(int cn = 1;cn <= colCount;cn++) {
							String columnName = metaData.getColumnName(cn);
							postingData.put(columnName.toUpperCase(), rs.getString(columnName));
						}
					}
					if(dataPresent) {
						exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
						//System.out.println("Feed ID["+postingData.get("EXTRACTION_FEED_ID")+"] picked upto Process!!!");
						exceptionCode.setResponse(postingData);
					}else {
						exceptionCode.setErrorCode(Constants.NO_RECORDS_FOUND);
					}
					return exceptionCode;
				}
			};
			return (ExceptionCode)getJdbcTemplate().query(orginalQuery, mapper);
		}catch(Exception e) {
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg(e.getMessage());
			return exceptionCode;
		}
	}
	public ExceptionCode getEtlPostngDetailAdfBuild(){
		ExceptionCode exceptionCode = new ExceptionCode();
		HashMap<String,String> postingData = new HashMap<String,String>();
		try
		{	
			String orginalQuery= "";
			if ("ORACLE".equalsIgnoreCase(databaseType)) {
				orginalQuery = " SELECT COUNTRY,LE_BOOK,EXTRACTION_FREQUENCY,EXTRACTION_SEQUENCE,EXTRACTION_FEED_ID, "+
						" POSTING_SEQUENCE,BUSINESS_DATE,POSTING_DATE FROM( "+
						" SELECT ROWNUM RN,COUNTRY,LE_BOOK,EXTRACTION_FREQUENCY,EXTRACTION_SEQUENCE,EXTRACTION_FEED_ID, "+
						" POSTING_SEQUENCE,TO_CHAR(BUSINESS_DATE,'DD-Mon-RRRR') BUSINESS_DATE,TO_CHAR(POSTING_DATE,'DD-Mon-RRRR HH24:MI:SS') POSTING_DATE FROM "+
						" ("+
						" SELECT * "+
						" FROM RA_TRN_POSTING "+
						" WHERE TO_CHAR(SCHEDULE_TIME,'HH24:MI:SS') <= TO_CHAR(SYSDATE,'HH24:MI:SS') "+
						" AND EXTRACTION_ENGINE = 'ETL' "+
						" AND DEPENDENT_FLAG = 'N' "+
						" AND POSTED_STATUS = 'P' "+
						" UNION ALL "+
						" SELECT * "+
						" FROM RA_TRN_POSTING "+
						" WHERE TO_CHAR(SCHEDULE_TIME,'HH24:MI:SS') <= TO_CHAR(SYSDATE,'HH24:MI:SS') "+
						" AND EXTRACTION_ENGINE = 'ETL' "+
						" AND DEPENDENT_FLAG = 'Y' "+
						" AND POSTED_STATUS = 'P' "+
						" AND DEPENDENT_FEED_ID NOT IN (SELECT EXTRACTION_FEED_ID FROM RA_TRN_POSTING) "+
						" ) T1 ORDER BY COUNTRY,LE_BOOK,EXTRACTION_FREQUENCY,EXTRACTION_SEQUENCE,EXTRACTION_FEED_ID, "+
						" POSTING_SEQUENCE)"+
						" WHERE RN <=1";
			}else if ("MSSQL".equalsIgnoreCase(databaseType)){
				orginalQuery = " SELECT top 1.COUNTRY,LE_BOOK,EXTRACTION_FREQUENCY,EXTRACTION_SEQUENCE,EXTRACTION_FEED_ID, "+ 
						" POSTING_SEQUENCE,FORMAT(BUSINESS_DATE,'dd-MMM-yyyy') BUSINESS_DATE,POSTING_DATE FROM  "+
						" ( "+
						" SELECT *  "+
						" FROM RA_TRN_POSTING "+
						" WHERE CONVERT(VARCHAR,SCHEDULE_TIME,108) <= CONVERT(VARCHAR,getdate(),108) "+
						" AND EXTRACTION_ENGINE = 'ETL' "+
						" AND DEPENDENT_FLAG = 'N'  "+
						" AND POSTED_STATUS = 'P' "+
						" UNION ALL "+
						" SELECT * "+
						" FROM RA_TRN_POSTING S1 "+
						" WHERE CONVERT(VARCHAR,SCHEDULE_TIME,108) <= CONVERT(VARCHAR,getdate(),108) "+
						" AND EXTRACTION_ENGINE = 'ETL' "+
						" AND DEPENDENT_FLAG = 'Y' "+
						" AND POSTED_STATUS = 'P' "+
						" AND DEPENDENT_FEED_ID IN (SELECT S2.EXTRACTION_FEED_ID FROM RA_TRN_POSTING_HISTORY S2 WHERE "+
						"     S2.POSTING_SEQUENCE = S1.POSTING_SEQUENCE AND S2.POSTED_STATUS ='C' "+
						"	 AND S2.POSTING_DATE = S1.POSTING_DATE AND S2.BUSINESS_DATE = S1.BUSINESS_DATE "+
						"	 ) "+
						" ) T1 ORDER BY COUNTRY,LE_BOOK,EXTRACTION_FREQUENCY,EXTRACTION_SEQUENCE,EXTRACTION_FEED_ID, "+
						" POSTING_SEQUENCE";
			}
			
			ResultSetExtractor mapper = new ResultSetExtractor() {
				@Override
				public Object extractData(ResultSet rs)  throws SQLException, DataAccessException {
					ResultSetMetaData metaData = rs.getMetaData();
					int colCount = metaData.getColumnCount();
					Boolean dataPresent = false;
					while(rs.next()){
						dataPresent = true;
						for(int cn = 1;cn <= colCount;cn++) {
							String columnName = metaData.getColumnName(cn);
							postingData.put(columnName.toUpperCase(), rs.getString(columnName));
						}
					}
					if(dataPresent) {
						exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
						exceptionCode.setResponse(postingData);
					}else {
						exceptionCode.setErrorCode(Constants.NO_RECORDS_FOUND);
					}
					return exceptionCode;
				}
			};
			return (ExceptionCode)getJdbcTemplate().query(orginalQuery, mapper);
		}catch(Exception e) {
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg(e.getMessage());
			return exceptionCode;
		}
	}
	public int doUpdatePostings(String country,String leBook,String extractionFrequency,String extractionSequence,String extractionFeedId,String postingSequence,
			String business_date,String postingDate,String postingStatus){
		String dateUpdate = " ";
		if("I".equalsIgnoreCase(postingStatus)) {
			dateUpdate = " ,START_TIME= "+getDbFunction("SYSDATE")+" ";
		}else {
			dateUpdate = " ,END_TIME = "+getDbFunction("SYSDATE")+" ";			
		}
		String query= "";
		if ("ORACLE".equalsIgnoreCase(databaseType)) {
			query = " Update RA_TRN_POSTING SET POSTED_STATUS = ? "+dateUpdate+" WHERE COUNTRY = ? AND LE_BOOK = ? "+
					 " AND EXTRACTION_FREQUENCY = ? AND EXTRACTION_SEQUENCE = ? AND EXTRACTION_FEED_ID = ? "+
					" AND POSTING_SEQUENCE = ? AND BUSINESS_DATE =TO_DATE(?,'RRRR-MM-DD') AND POSTING_DATE = TO_DATE(?,'DD-MM-RRRR HH24:MI:SS') AND POSTED_STATUS <> 'T' ";
		}else {
			query = " Update RA_TRN_POSTING SET POSTED_STATUS = ? "+dateUpdate+" WHERE COUNTRY = ? AND LE_BOOK = ? "+
					 " AND EXTRACTION_FREQUENCY = ? AND EXTRACTION_SEQUENCE = ? AND EXTRACTION_FEED_ID = ? "+
					" AND POSTING_SEQUENCE = ? AND BUSINESS_DATE = ? AND POSTING_DATE = ? AND POSTED_STATUS <> 'T'";
		}
		
		Object[] args = {postingStatus,country,leBook,extractionFrequency,extractionSequence,extractionFeedId,postingSequence,business_date,postingDate};
		return getJdbcTemplate().update(query,args);
	}
	public int doUpdatePostingsHistory(String country,String leBook,String extractionFrequency,String extractionSequence,String extractionFeedId,String postingSequence,
			String business_date,String postingDate,String postingStatus){
		String dateUpdate = " ";
		if("I".equalsIgnoreCase(postingStatus)) {
			dateUpdate = " ,START_TIME= "+getDbFunction("SYSDATE")+" ";
		}else {
			dateUpdate = " ,END_TIME = "+getDbFunction("SYSDATE")+" ";			
		}
		String query = "";
		if ("ORACLE".equalsIgnoreCase(databaseType)) {
			query = " Update RA_TRN_POSTING_HISTORY SET POSTED_STATUS = ? "+dateUpdate+" WHERE COUNTRY = ? AND LE_BOOK = ? "+
					 " AND EXTRACTION_FREQUENCY = ? AND EXTRACTION_SEQUENCE = ? AND EXTRACTION_FEED_ID = ? "+
					 " AND POSTING_SEQUENCE = ? AND BUSINESS_DATE = TO_DATE(?,'RRRR-MM-DD') AND POSTING_DATE = TO_DATE(?,'DD-MM-RRRR HH24:MI:SS')  ";
		}else {
			query = " Update RA_TRN_POSTING_HISTORY SET POSTED_STATUS = ? "+dateUpdate+" WHERE COUNTRY = ? AND LE_BOOK = ? "+
					 " AND EXTRACTION_FREQUENCY = ? AND EXTRACTION_SEQUENCE = ? AND EXTRACTION_FEED_ID = ? "+
					 " AND POSTING_SEQUENCE = ? AND BUSINESS_DATE = ? AND POSTING_DATE = ? ";
		}
		 
		Object[] args = {postingStatus,country,leBook,extractionFrequency,extractionSequence,extractionFeedId,postingSequence,business_date,postingDate};
		return getJdbcTemplate().update(query,args);
	}
	public int doDeletePostings(String country,String leBook,String extractionFrequency,String extractionSequence,String extractionFeedId,String postingSequence,
			String business_date,String postingDate,String postingStatus){
		String query = "";
		if ("ORACLE".equalsIgnoreCase(databaseType)) {
			query = " DELETE FROM RA_TRN_POSTING WHERE COUNTRY = ? AND LE_BOOK = ? "+
					 " AND EXTRACTION_FREQUENCY = ? AND EXTRACTION_SEQUENCE = ? AND EXTRACTION_FEED_ID = ? "+
					 " AND POSTING_SEQUENCE = ? AND BUSINESS_DATE = TO_DATE(?,'RRRR-MM-DD') AND POSTING_DATE = TO_DATE(?,'DD-MM-RRRR HH24:MI:SS') ";
		}else {
			query = " DELETE FROM RA_TRN_POSTING WHERE COUNTRY = ? AND LE_BOOK = ? "+
					 " AND EXTRACTION_FREQUENCY = ? AND EXTRACTION_SEQUENCE = ? AND EXTRACTION_FEED_ID = ? "+
					 " AND POSTING_SEQUENCE = ? AND BUSINESS_DATE = ? AND POSTING_DATE = ? ";
		}
		Object[] args = {country,leBook,extractionFrequency,extractionSequence,extractionFeedId,postingSequence,business_date,postingDate};
		return getJdbcTemplate().update(query,args);
	}
	public int updateLogFileName(String country,String leBook,String extractionFrequency,String extractionSequence,String extractionFeedId,String postingSequence,
			String business_date,String postingDate,String logFileName) {
		String query = "";
		if ("ORACLE".equalsIgnoreCase(databaseType)) {
			query = " Update RA_TRN_POSTING SET LOG_FILE_NAME =? WHERE COUNTRY = ? AND LE_BOOK = ? " +
					 " AND EXTRACTION_FREQUENCY = ? AND EXTRACTION_SEQUENCE = ? AND EXTRACTION_FEED_ID = ? "+
					 " AND POSTING_SEQUENCE = ? AND BUSINESS_DATE = TO_DATE(?,'RRRR-MM-DD') AND POSTING_DATE = TO_DATE(?,'DD-MM-RRRR HH24:MI:SS') ";
		}else {
			query = " Update RA_TRN_POSTING SET LOG_FILE_NAME =? WHERE COUNTRY = ? AND LE_BOOK = ? "
					+
					 " AND EXTRACTION_FREQUENCY = ? AND EXTRACTION_SEQUENCE = ? AND EXTRACTION_FEED_ID = ? "+
					 " AND POSTING_SEQUENCE = ? AND BUSINESS_DATE = ? AND POSTING_DATE = ? ";
		}
		Object[] args = {logFileName,country,leBook,extractionFrequency,extractionSequence,extractionFeedId,postingSequence,business_date,postingDate};
		return getJdbcTemplate().update(query,args);
	}
	public int updateLogFileNameHistory(String country,String leBook,String extractionFrequency,String extractionSequence,String extractionFeedId,String postingSequence,
			String business_date,String postingDate,String logFileName) {
		String query = "";
		if ("ORACLE".equalsIgnoreCase(databaseType)) {
			query = " Update RA_TRN_POSTING_HISTORY SET LOG_FILE_NAME =? WHERE COUNTRY = ? AND LE_BOOK = ? " +
					 " AND EXTRACTION_FREQUENCY = ? AND EXTRACTION_SEQUENCE = ? AND EXTRACTION_FEED_ID = ? "+
					 " AND POSTING_SEQUENCE = ? AND BUSINESS_DATE = TO_DATE(?,'RRRR-MM-DD') AND POSTING_DATE = TO_DATE(?,'DD-MM-RRRR HH24:MI:SS') ";
		}else {
			query = " Update RA_TRN_POSTING_HISTORY SET LOG_FILE_NAME =? WHERE COUNTRY = ? AND LE_BOOK = ? "
					+
					 " AND EXTRACTION_FREQUENCY = ? AND EXTRACTION_SEQUENCE = ? AND EXTRACTION_FEED_ID = ? "+
					 " AND POSTING_SEQUENCE = ? AND BUSINESS_DATE = ? AND POSTING_DATE = ? ";
		}
		Object[] args = {logFileName,country,leBook,extractionFrequency,extractionSequence,extractionFeedId,postingSequence,business_date,postingDate};
		return getJdbcTemplate().update(query,args);
	}
	public ExceptionCode doSelectAdfAcquisition(String country,String leBook,String  feedId,String postingSeq,String businessDate){
		ExceptionCode exceptionCode = new ExceptionCode();
		HashMap<String,String> acqData = new HashMap<String,String>();
		try
		{	
			String orginalQuery= "";
			if ("ORACLE".equalsIgnoreCase(databaseType)) {
				orginalQuery = " SELECT COUNTRY,LE_BOOK,'' BUSINESS_DATE,TEMPLATE_NAME,FILE_PATTERN, "+
						" EXCEL_FILE_PATTERN,EXCEL_TEMPLATE_ID,'' NEXT_PROCESS_TIME, "+
						" 'ADF' ACQUISTION_PROCESS_TYPE,CONNECTIVITY_TYPE,CONNECTIVITY_DETAILS, "+ 
						" DATABASE_TYPE,DATABASE_CONNECTIVITY_DETAILS, "+
						" SOURCE_SCRIPT_TYPE,SOURCE_SERVER_SCRIPTS,"+
						" TARGET_SCRIPT_TYPE,"+
						" TARGET_SERVER_SCRIPTS,READINESS_SCRIPTS_TYPE,ACQUISITION_READINESS_SCRIPTS, "+
						" '' ACQU_PROCESSCONTROL_STATUS,'' ADF_NUMBER,SUB_ADF_NUMBER,PREACTIVITY_SCRIPTS FROM ADF_DATA_ACQUISITION "+
						" WHERE TEMPLATE_NAME = '"+feedId+"' ";
			}else if ("MSSQL".equalsIgnoreCase(databaseType)){
				orginalQuery = " SELECT COUNTRY,LE_BOOK,'' BUSINESS_DATE,TEMPLATE_NAME,FILE_PATTERN, "+
						" EXCEL_FILE_PATTERN,EXCEL_TEMPLATE_ID,'' NEXT_PROCESS_TIME, "+
						" 'ADF' ACQUISTION_PROCESS_TYPE,CONNECTIVITY_TYPE,CONNECTIVITY_DETAILS, "+ 
						" DATABASE_TYPE,DATABASE_CONNECTIVITY_DETAILS, "+
						" SOURCE_SCRIPT_TYPE,SOURCE_SERVER_SCRIPTS,"+
						" TARGET_SCRIPT_TYPE,"+
						" TARGET_SERVER_SCRIPTS,READINESS_SCRIPTS_TYPE,ACQUISITION_READINESS_SCRIPTS, "+
						" '' ACQU_PROCESSCONTROL_STATUS,'' ADF_NUMBER,SUB_ADF_NUMBER,PREACTIVITY_SCRIPT_TYPE,PREACTIVITY_SCRIPTS FROM ADF_DATA_ACQUISITION "+
						" WHERE TEMPLATE_NAME = '"+feedId+"' AND COUNTRY ='"+country+"' AND LE_BOOK ='"+leBook+"'  ";
			}
			
			ResultSetExtractor mapper = new ResultSetExtractor() {
				@Override
				public Object extractData(ResultSet rs)  throws SQLException, DataAccessException {
					ResultSetMetaData metaData = rs.getMetaData();
					int colCount = metaData.getColumnCount();
					Boolean dataPresent = false;
					while(rs.next()){
						dataPresent = true;
						for(int cn = 1;cn <= colCount;cn++) {
							String columnName = metaData.getColumnName(cn);
							acqData.put(columnName.toUpperCase(), rs.getString(columnName));
						}
					}
					if(dataPresent) {
						int recCnt = checkProcessControlDataExists(country,leBook,feedId,businessDate);
						if(recCnt > 0) {
							deleteProcessControlDataExists(country, leBook, feedId, businessDate);
						}
						ExceptionCode exceptionCodePr = doInsertAdfProcessControl(acqData,postingSeq,businessDate);
						if(exceptionCodePr.getErrorCode() != Constants.SUCCESSFUL_OPERATION) {
							exceptionCode.setErrorMsg(exceptionCodePr.getErrorMsg());
							exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
						}else {
							exceptionCode.setResponse(getAdfProcessControlInfo(country, leBook, feedId, businessDate));
							exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);	
						}
						//exceptionCode.setResponse(acqData);
					}else {
						exceptionCode.setErrorMsg("ADF Data Acquisition not maintained for country["+country+"]LEBook["+leBook+"]FeedId["+feedId+"]");
						exceptionCode.setErrorCode(Constants.NO_RECORDS_FOUND);
					}
					return exceptionCode;
				}
			};
			return (ExceptionCode)getJdbcTemplate().query(orginalQuery, mapper);
		}catch(Exception e) {
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg(e.getMessage());
			return exceptionCode;
		}
	}
	protected ExceptionCode doInsertAdfProcessControl(HashMap<String,String> insertMap,String postingSeq,String businessDate){
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			String query =  " Insert Into ADF_PROCESS_CONTROL(COUNTRY,LE_BOOK,BUSINESS_DATE,TEMPLATE_NAME,FILE_PATTERN, "+
					" EXCEL_FILE_PATTERN,EXCEL_TEMPLATE_ID,NEXT_PROCESS_TIME, "+
					" acquisition_process_type,CONNECTIVITY_TYPE,CONNECTIVITY_DETAILS,"+ 
					" DATABASE_TYPE,DATABASE_CONNECTIVITY_DETAILS,"+
					" SOURCE_SCRIPT_TYPE,SOURCE_SERVER_SCRIPTS,"+
					" TARGET_SCRIPT_TYPE,"+
					" TARGET_SERVER_SCRIPTS,READINESS_SCRIPTS_TYPE,ACQUISITION_READINESS_SCRIPTS,"+
					" ACQU_PROCESSCONTROL_STATUS,ADF_NUMBER,SUB_ADF_NUMBER,"+
					" START_TIME,END_TIME,ALERT1_TIMESLOT,RECORD_INDICATOR,"+
					" MAKER,VERIFIER,DATE_CREATION,DATE_LAST_MODIFIED,PREACTIVITY_SCRIPTS) "+
			" Values ("
				+ "'"+insertMap.get("COUNTRY")+"',"
				+ "'"+insertMap.get("LE_BOOK")+"',"
				+ "'"+businessDate+"',"
				+ "'"+insertMap.get("TEMPLATE_NAME")+"',"
				+ "'"+insertMap.get("FILE_PATTERN")+"',"
				+ "'"+insertMap.get("EXCEL_FILE_PATTERN")+"',"
				+ "'"+insertMap.get("EXCEL_TEMPLATE_ID")+"',"
				+ ""+getDbFunction("SYSDATE")+", "
				+ "'ADF',"
				+ "'"+insertMap.get("CONNECTIVITY_TYPE")+"',"
				+ "'"+insertMap.get("CONNECTIVITY_DETAILS")+"'," 
				+ "'"+insertMap.get("DATABASE_TYPE")+"',"
				+ "'"+insertMap.get("DATABASE_CONNECTIVITY_DETAILS")+"',"
				+ "'"+insertMap.get("SOURCE_SCRIPT_TYPE")+"',"
				+ "'"+insertMap.get("SOURCE_SERVER_SCRIPTS")+"',"
				+ "'"+insertMap.get("TARGET_SCRIPT_TYPE")+"',"
				+ "'"+insertMap.get("TARGET_SERVER_SCRIPTS")+"',"
				+ "'"+insertMap.get("READINESS_SCRIPTS_TYPE")+"',"
				+ "'"+insertMap.get("ACQUISITION_READINESS_SCRIPTS")+"',"
				+ "'1',"
				+ "'0',"
				+ "'"+insertMap.get("SUB_ADF_NUMBER")+"',"
				+ ""+getDbFunction("SYSDATE")+", "
				+ ""+getDbFunction("SYSDATE")+", "
				+ ""+getDbFunction("SYSDATE")+", "
				+ "'0', "
				+ "'9999', "
				+ "'9999', "
				+ ""+getDbFunction("SYSDATE")+", "
				+ ""+getDbFunction("SYSDATE")+", "
				+ "'"+insertMap.get("PREACTIVITY_SCRIPTS")+"'"
				+ ")";
	
			query = replaceHashPrompts(insertMap,postingSeq,businessDate,query);
			int i = getJdbcTemplate().update(query);
			exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
		}catch(Exception e) {
			//System.out.println("Error while inserting ADF Process Control Data!!!!!");
			//System.out.println(e.getMessage());
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg(e.getMessage());
		}
		return exceptionCode;
	}
	private int checkProcessControlDataExists(String country,String leBook,String feedId,String businessDate) {
		int cnt = 0;
		try {
			String query = "SELECT COUNT(1) FROM ADF_PROCESS_CONTROL WHERE COUNTRY = ? AND LE_BOOK = ? "+
					" AND TEMPLATE_NAME = ? AND BUSINESS_DATE = ? ";
			
			Object[] args = {country,leBook,feedId,businessDate};
			cnt = getJdbcTemplate().queryForObject(query,args,Integer.class);
			return cnt; 
		}catch(Exception e) {
			return 0;
		}
	}
	public int deleteProcessControlDataExists(String country,String leBook,String feedId,String businessDate) {
		int cnt = 0;
		try {
			String query = "DELETE FROM ADF_PROCESS_CONTROL WHERE COUNTRY = ? AND LE_BOOK = ? "+
					" AND TEMPLATE_NAME = ? AND BUSINESS_DATE = ? ";
			
			Object[] args = {country,leBook,feedId,businessDate};
			return getJdbcTemplate().update(query,args);
		}catch(Exception e) {
			return 0;
		}
	}
	public String replaceHashPrompts(HashMap<String,String> insertMap,String postingSeq,String businessDate,String insertQuery) {
		try {
			if(insertMap != null) {
				insertQuery = insertQuery.replaceAll("#COUNTRY#", insertMap.get("COUNTRY"));
				insertQuery = insertQuery.replaceAll("#LE_BOOK#", insertMap.get("LE_BOOK"));
				insertQuery = insertQuery.replaceAll("#TEMPLATE_NAME#", insertMap.get("TEMPLATE_NAME"));
			}
			insertQuery = insertQuery.replaceAll("#BUSINESS_DATE#", businessDate);
			insertQuery = insertQuery.replaceAll("#POSTING_SEQ#", postingSeq);
			return insertQuery;
		}catch(Exception e) {
			e.printStackTrace();
			return insertQuery;
		}
	}
	public HashMap<String,String> getAdfProcessControlInfo(String country,String leBook,String feedId,String businessDate){
		ExceptionCode exceptionCode = new ExceptionCode();
		HashMap<String,String> postingData = new HashMap<String,String>();
		try
		{	
			String orginalQuery = "SELECT FILE_PATTERN,TARGET_SCRIPT_TYPE,TARGET_SERVER_SCRIPTS,READINESS_SCRIPTS_TYPE,ACQUISITION_READINESS_SCRIPTS, "+
					" PREACTIVITY_SCRIPT_TYPE,PREACTIVITY_SCRIPTS "+
					" FROM ADF_PROCESS_CONTROL WHERE COUNTRY = '"+country+"' AND LE_BOOK = '"+leBook+"' "+
					" AND TEMPLATE_NAME = '"+feedId+"' AND BUSINESS_DATE = '"+businessDate+"' ";
			
			ResultSetExtractor mapper = new ResultSetExtractor() {
				@Override
				public Object extractData(ResultSet rs)  throws SQLException, DataAccessException {
					ResultSetMetaData metaData = rs.getMetaData();
					int colCount = metaData.getColumnCount();
					Boolean dataPresent = false;
					while(rs.next()){
						dataPresent = true;
						for(int cn = 1;cn <= colCount;cn++) {
							String columnName = metaData.getColumnName(cn);
							postingData.put(columnName.toUpperCase(), rs.getString(columnName));
						}
					}
					return postingData;
				}
			};
			return (HashMap<String,String>)getJdbcTemplate().query(orginalQuery, mapper);
		}catch(Exception e) {
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg(e.getMessage());
			return null;
		}
	}
	@Transactional(rollbackForClassName={"com.vision.exception.RuntimeCustomException"})
	public ExceptionCode sqlBulkInsert(String targetScript,String adfDelimiter,String dataFile) throws RuntimeCustomException {
		ExceptionCode exceptionCode = new ExceptionCode();
		strErrorDesc  = "";
		strCurrentOperation = Constants.ADD;
		setServiceDefaults();
		try {
			dataFile = dataFile.replaceAll("\\\\", "\\\\\\\\");
			targetScript = targetScript.replaceAll("#UPLOAD_FILE_PATH#", dataFile);
			
			int retVal = getJdbcTemplate().update(targetScript);
			exceptionCode.setResponse(retVal);
			exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
			return exceptionCode;
		}catch (RuntimeCustomException rcException) {
			throw rcException;
		}catch (UncategorizedSQLException uSQLEcxception) {
			strErrorDesc = parseErrorMsg(uSQLEcxception);
			exceptionCode = getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION);
			throw buildRuntimeCustomException(exceptionCode);
		}catch(Exception ex){
			strErrorDesc = ex.getMessage();
			exceptionCode = getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION);
			throw buildRuntimeCustomException(exceptionCode);
		}
	}
	public ExceptionCode executeTargetScript(String sql) {
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			int i = getJdbcTemplate().update(sql);
			if(i == Constants.SUCCESSFUL_OPERATION) {
				exceptionCode.setErrorCode(i);	
			}else {
				exceptionCode = getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION);
				throw buildRuntimeCustomException(exceptionCode);
			}
		}catch(Exception e) {
			exceptionCode = getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION);
			throw buildRuntimeCustomException(exceptionCode);
		}
		return exceptionCode;
	}
	public int executePreactivityScript(String sql) {
		try {
			return getJdbcTemplate().update(sql);
		}catch(Exception e) {
			return 0;
		}
	}
	@Transactional(rollbackForClassName={"com.vision.exception.RuntimeCustomException"})
	public ExceptionCode sqlLoader(String targetScript,String adfDelimiter,String adfDataFilePath,String filePattern) throws RuntimeCustomException {
		ExceptionCode exceptionCode = new ExceptionCode();
		strErrorDesc  = "";
		strCurrentOperation = Constants.ADD;
		setServiceDefaults();
		try {
			String logPath =  commonDao.findVisionVariableValue("RA_SERV_LOGPATH");
			String userName =  System.getenv("VISION_USER_NAME");
			String password =  System.getenv("VISION_PASSWORD");
			String dbStr = "sqlldr vision/vision123@10.16.1.222:1521/vsnnxt control="+adfDataFilePath+filePattern+".ctl log="+logPath+filePattern+"_Loader.log";

	        Process proc;
			proc = Runtime.getRuntime().exec(dbStr);
			proc.waitFor();
	        InputStream in = proc.getInputStream();
	        InputStreamReader isr = new InputStreamReader(in);
	        BufferedReader br = new BufferedReader(isr);
	        String line = null;
	        StringBuffer sb = new StringBuffer();
			while( (line = br.readLine()) != null ) {
			    sb.append(line+"\n");
			}
	        InputStream inE = proc.getErrorStream();
	        InputStreamReader iser = new InputStreamReader(inE);
	        BufferedReader erbr = new BufferedReader(iser);
			while( (line = erbr.readLine()) != null ){
			    sb.append( "\nError stream:" );
			    sb.append(line);
			}
			int eValue = proc.waitFor();
	        ////System.out.println(eValue);
			if(eValue != 0) {
				exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			}else {
				exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
			}
			return exceptionCode;
		}catch (RuntimeCustomException rcException) {
			throw rcException;
		}catch (UncategorizedSQLException uSQLEcxception) {
			strErrorDesc = parseErrorMsg(uSQLEcxception);
			exceptionCode = getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION);
			throw buildRuntimeCustomException(exceptionCode);
		}catch(Exception ex){
			strErrorDesc = ex.getMessage();
			exceptionCode = getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION);
			throw buildRuntimeCustomException(exceptionCode);
		}
	}
	public ExceptionCode getEtlPostngDependentBuild(String country,String leBook,String extractionFrequency,String extractionSequence,String extractionFeedId,String postingSequence,
			String business_date,String postingDate){
		ExceptionCode exceptionCode = new ExceptionCode();
		ArrayList dependlst = new ArrayList();
		try
		{	
			String orginalQuery = "";
			if ("ORACLE".equalsIgnoreCase(databaseType)) {
				orginalQuery = " SELECT COUNTRY,LE_BOOK,EXTRACTION_FREQUENCY,EXTRACTION_SEQUENCE,EXTRACTION_FEED_ID, "+ 
						" POSTING_SEQUENCE,CONVERT(DATE,BUSINESS_DATE) BUSINESS_DATE,POSTING_DATE,  "+
						" TO_CHAR(TO_DATE(POSTING_DATE),'yyyy-MM-dd') POSTING_FT_DATE FROM RA_TRN_POSTING "+
						" WHERE TO_DATE(SCHEDULE_TIME,24HH:MM:SS) <= TO_DATE(SYSDATE,24HH:MM:SS) "+
						" AND EXTRACTION_ENGINE = 'BLD' "+
						" AND DEPENDENT_FLAG = 'Y'  "+
						" AND COUNTRY = ? AND LE_BOOK = ? "+
						" AND EXTRACTION_FREQUENCY = ? AND EXTRACTION_SEQUENCE = ? AND DEPENDENT_FEED_ID = ? "+
						" AND POSTING_SEQUENCE = ? AND BUSINESS_DATE = ? AND POSTING_DATE = ? ";	
			}else {
				orginalQuery = " SELECT COUNTRY,LE_BOOK,EXTRACTION_FREQUENCY,EXTRACTION_SEQUENCE,EXTRACTION_FEED_ID, "+ 
						" POSTING_SEQUENCE,CONVERT(DATE,BUSINESS_DATE) BUSINESS_DATE,POSTING_DATE,  "+
						" FORMAT(POSTING_DATE,'yyyy-MM-dd') POSTING_FT_DATE FROM RA_TRN_POSTING "+
						" WHERE CONVERT(VARCHAR,SCHEDULE_TIME,108) <= CONVERT(VARCHAR,getdate(),108) "+
						" AND EXTRACTION_ENGINE = 'BLD' "+
						" AND DEPENDENT_FLAG = 'Y'  "+
						" AND COUNTRY = ? AND LE_BOOK = ? "+
						" AND EXTRACTION_FREQUENCY = ? AND EXTRACTION_SEQUENCE = ? AND DEPENDENT_FEED_ID = ? "+
						" AND POSTING_SEQUENCE = ? AND BUSINESS_DATE = ? AND POSTING_DATE = ? ";
			}
			
			
			Object[] args = {country,leBook,extractionFrequency,extractionSequence,extractionFeedId,postingSequence,business_date,postingDate};
			
			ResultSetExtractor mapper = new ResultSetExtractor() {
				@Override
				public Object extractData(ResultSet rs)  throws SQLException, DataAccessException {
					ResultSetMetaData metaData = rs.getMetaData();
					int colCount = metaData.getColumnCount();
					Boolean dataPresent = false;
					while(rs.next()){
						HashMap<String,String> postingData = new HashMap<String,String>();
						dataPresent = true;
						for(int cn = 1;cn <= colCount;cn++) {
							String columnName = metaData.getColumnName(cn);
							postingData.put(columnName.toUpperCase(), rs.getString(columnName));
						}
						dependlst.add(postingData);
					}
					if(dataPresent) {
						exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
						exceptionCode.setResponse(dependlst);
					}else {
						exceptionCode.setErrorCode(Constants.NO_RECORDS_FOUND);
					}
					return exceptionCode;
				}
			};
			return (ExceptionCode)getJdbcTemplate().query(orginalQuery,args,mapper);
		}catch(Exception e) {
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg(e.getMessage());
			return exceptionCode;
		}
	}
	public int doUpdateDependentFeed(String country,String leBook,String extractionFrequency,String extractionSequence,String extractionFeedId,String postingSequence,
			String business_date,String postingDate,String postingStatus){
		/*if("I".equalsIgnoreCase(postingStatus)) {
			dateUpdate = " ,START_TIME= "+getDbFunction("SYSDATE")+" ";
		}else {
			dateUpdate = " ,END_TIME = "+getDbFunction("SYSDATE")+" ";			
		}*/
		String query = " Update RA_TRN_POSTING_HISTORY SET POSTED_STATUS = ? WHERE COUNTRY = ? AND LE_BOOK = ? "+
				 " AND EXTRACTION_FREQUENCY = ? AND EXTRACTION_SEQUENCE = ? AND EXTRACTION_FEED_ID = ? "+
				 " AND POSTING_SEQUENCE = ? AND BUSINESS_DATE = ? AND POSTING_DATE = ? ";
		Object[] args = {postingStatus,country,leBook,extractionFrequency,extractionSequence,extractionFeedId,postingSequence,business_date,postingDate};
		return getJdbcTemplate().update(query,args);
	}
	public int doDeleteDependentFeed(String country,String leBook,String extractionFrequency,String extractionSequence,String extractionFeedId,String postingSequence,
			String business_date,String postingDate){

		String query = " DELETE FROM RA_TRN_POSTING WHERE COUNTRY = ? AND LE_BOOK = ? "+
				 " AND EXTRACTION_FREQUENCY = ? AND EXTRACTION_SEQUENCE = ? AND EXTRACTION_FEED_ID = ? "+
				 " AND POSTING_SEQUENCE = ? AND BUSINESS_DATE = ? AND POSTING_DATE = ? ";
		Object[] args = {country,leBook,extractionFrequency,extractionSequence,extractionFeedId,postingSequence,business_date,postingDate};
		return getJdbcTemplate().update(query,args);
	}
	public ExceptionCode chargeCalculationProcCall(String country,String leBook,String businessDate,String extractionFeedId){
		ExceptionCode exceptionCode = new ExceptionCode();
		Connection con = null;
		CallableStatement cs =  null;
		try{
			con = getConnection();
			cs = con.prepareCall("{call PR_RA_CHARGE_POSTING(?,?,?,?,?,?)}");
			cs.setString(1, country);
	        cs.setString(2, leBook);
	        cs.setString(3, businessDate);
	        cs.setString(4, extractionFeedId);
        	cs.registerOutParameter(5, java.sql.Types.VARCHAR); //Status
	        cs.registerOutParameter(6, java.sql.Types.VARCHAR); //Error Message
	        cs.execute();
	        exceptionCode.setErrorCode(Integer.parseInt(cs.getString(5)));
	        exceptionCode.setErrorMsg(cs.getString(6));
            cs.close();
		}catch(Exception ex){
			ex.printStackTrace();
			strErrorDesc = ex.getMessage().trim();
		}finally{
			JdbcUtils.closeStatement(cs);
			DataSourceUtils.releaseConnection(con, getDataSource());
		}
		return exceptionCode;
	}
	public ExceptionCode ReconProcCall(String country,String leBook,String businessDate,String extractionFeedId,String reconEngine){
		ExceptionCode exceptionCode = new ExceptionCode();
		Connection con = null;
		CallableStatement cs =  null;
		try{
			String procedure = "";
			if("REC".equalsIgnoreCase(reconEngine))
				procedure = "PR_RA_RECON_POSTING";
			else if("TRC".equalsIgnoreCase(reconEngine))
				procedure  = "PR_RA_RECON_POSTING_TAX";
			con = getConnection();
			cs = con.prepareCall("{call "+procedure+"(?,?,?,?,?,?)}");
			cs.setString(1, country);
	        cs.setString(2, leBook);
	        cs.setString(3, businessDate);
	        cs.setString(4, extractionFeedId);
        	cs.registerOutParameter(5, java.sql.Types.VARCHAR); //Status
	        cs.registerOutParameter(6, java.sql.Types.VARCHAR); //Error Message
	        cs.execute();
	        exceptionCode.setErrorCode(Integer.parseInt(cs.getString(5)));
	        exceptionCode.setErrorMsg(cs.getString(6));
            cs.close();
		}catch(Exception ex){
			ex.printStackTrace();
			strErrorDesc = ex.getMessage().trim();
		}finally{
			JdbcUtils.closeStatement(cs);
			DataSourceUtils.releaseConnection(con, getDataSource());
		}
		return exceptionCode;
	}
	public ArrayList<String> getCronAudit(String country,String leBook,String businessDate,String feedId){
		ExceptionCode exceptionCode = new ExceptionCode();
		try
		{	
			String orginalQuery = "SELECT LOG_MSG FROM RA_CRON_AUDIT_TRAIL WHERE COUNTRY = '"+country+"' AND LE_BOOK = '"+leBook+"' "+
					" AND FEED_ID = '"+feedId+"' AND BUSINESS_DATE = '"+businessDate+"' ORDER BY LOG_SEQUENCE ";
			
			ResultSetExtractor mapper = new ResultSetExtractor() {
				@Override
				public Object extractData(ResultSet rs)  throws SQLException, DataAccessException {
					ArrayList<String> logMsglst = new ArrayList<String>();
					ResultSetMetaData metaData = rs.getMetaData();
					while(rs.next()){
						logMsglst.add(rs.getString("LOG_MSG"));
					}
					return logMsglst;
				}
			};
			return (ArrayList<String>)getJdbcTemplate().query(orginalQuery, mapper);
		}catch(Exception e) {
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg(e.getMessage());
			return null;
		}
	}
	public int deleteChargeAudit(String country,String leBook,String businessDate,String feedId) {
		String query = "DELETE FROM RA_CRON_AUDIT_TRAIL WHERE COUNTRY = '"+country+"' AND LE_BOOK = '"+leBook+"' "+
					" AND FEED_ID = '"+feedId+"' AND BUSINESS_DATE = '"+businessDate+"' ";
		return getJdbcTemplate().update(query);
	}

	public ExceptionCode getEtlPostngHeaderGenBuild() {
		ExceptionCode exceptionCode = new ExceptionCode();
		HashMap<String, String> postingHeaderData = new HashMap<String, String>();
		try {
			String orginalQuery = "";
			if ("ORACLE".equalsIgnoreCase(databaseType)) {
				orginalQuery = "SELECT COUNTRY,                                               "
						+ "       LE_BOOK,                                                    "
						+ "       EXTRACTION_FREQUENCY,                                       "
						+ "       EXTRACTION_SEQUENCE,                                        "
						+ "       TO_CHAR(BUSINESS_DATE,'DD-Mon-RRRR') BUSINESS_DATE,         "
						+ "       EXTRACTION_ENGINE,SUBMIT_TYPE                               "
						+ "  FROM (  SELECT ROWNUM RN,                                        "
						+ "                 COUNTRY,                                          "
						+ "                 LE_BOOK,                                          "
						+ "                 EXTRACTION_FREQUENCY,                             "
						+ "                 EXTRACTION_SEQUENCE,                              "
						+ "                 BUSINESS_DATE,                                    "
						+ "                 EXTRACTION_ENGINE,SUBMIT_TYPE                     "
						+ "            FROM (SELECT * FROM (SELECT *                          "
						+ "                    FROM RA_TRN_POSTING_HEADER T1                  "
						+ "                   WHERE     DEPENDENT_SEQUENCE = 0                "
						+ "                         AND POSTING_STATUS = 'P'                  "
						+ "                         AND EXTRACTION_SEQUENCE =                 "
						+ "                                (SELECT MIN (EXTRACTION_SEQUENCE)  "
						+ "                                   FROM RA_TRN_POSTING_HEADER S1   "
						+ "                                  WHERE     DEPENDENT_SEQUENCE = 0 "
						+ "                                        AND POSTING_STATUS = 'P'	  "
						+ "  			AND S1.COUNTRY = T1.COUNTRY								"
						+ "  			AND S1.LE_BOOK = T1.LE_BOOK								"
						+ "					)  													"
						+ "                  UNION ALL                                        "
						+ " SELECT T1.*                                                                  "
						+ "                     FROM RA_TRN_POSTING_HEADER T1,                           "
						+ "                          RA_TRN_POSTING_HEADER T2                            "
						+ "                    WHERE T1.DEPENDENT_SEQUENCE != 0                          "
						+ "                          AND T1.POSTING_STATUS = 'P'                         "
						+ "                          AND T2.POSTING_STATUS = 'C'                         "
						+ "                          AND T2.EXTRACTION_SEQUENCE= T1.DEPENDENT_SEQUENCE   "
						+ " 						 AND T1.BUSINESS_DATE= T2.BUSINESS_DATE"
						+ "                          AND T2.COUNTRY = T1.COUNTRY                         "
						+ "                          AND T2.LE_BOOK = T1.LE_BOOK                         "
						+ "                          AND T2.EXTRACTION_FREQUENCY =T1.EXTRACTION_FREQUENCY"
						+ "                          AND T1.EXTRACTION_SEQUENCE =                        "
						+ "                                 (SELECT MIN (EXTRACTION_SEQUENCE)            "
						+ "                                    FROM RA_TRN_POSTING_HEADER S1             "
						+ "                                   WHERE     DEPENDENT_SEQUENCE != 0          "
						+ "                                         AND POSTING_STATUS = 'P'"
						+ "									AND S1.COUNTRY = 	T1.COUNTRY					"
						+ "									AND T1.LE_BOOK = T1.LE_BOOK				"
						+ "							)) ORDER BY BUSINESS_DATE,EXTRACTION_SEQUENCE )            "
						+ "        ORDER BY EXTRACTION_SEQUENCE)                              "
						+ " WHERE RN <= 1                                                     ";
			} else if ("MSSQL".equalsIgnoreCase(databaseType)) {
				orginalQuery = "SELECT TOP 1.COUNTRY,LE_BOOK,EXTRACTION_FREQUENCY,EXTRACTION_SEQUENCE,FORMAT(BUSINESS_DATE,'dd-MMM-yyyy') BUSINESS_DATE, EXTRACTION_ENGINE,SUBMIT_TYPE "
						+ " FROM (  SELECT "
						+ " ROW_NUMBER() OVER(ORDER BY BUSINESS_DATE,EXTRACTION_SEQUENCE ASC) AS Rn, "
						+ " COUNTRY,LE_BOOK, EXTRACTION_FREQUENCY,EXTRACTION_SEQUENCE, "
						+ " BUSINESS_DATE, EXTRACTION_ENGINE, SUBMIT_TYPE              "
						+ " FROM  (                                                   "
						+ " SELECT  *  FROM  (                                        "
						+ " SELECT *  FROM                                            "
						+ " RA_TRN_POSTING_HEADER T1                                  "
						+ " WHERE                                                     "
						+ " DEPENDENT_SEQUENCE = 0                                    "
						+ " AND POSTING_STATUS = 'P'                                  "
						+ " AND EXTRACTION_SEQUENCE =                                 "
						+ " (                                                         "
						+ " SELECT MIN (EXTRACTION_SEQUENCE)                          "
						+ " FROM                                                      "
						+ " RA_TRN_POSTING_HEADER S1                               	"
						+ "  WHERE                                                     "
						+ "  DEPENDENT_SEQUENCE = 0                                 	"
						+ "  AND POSTING_STATUS = 'P'                               	"
						+ "  AND S1.COUNTRY = T1.COUNTRY								"
						+ "  AND S1.LE_BOOK = T1.LE_BOOK								"
						+ "  )                                                        "
						+ "  UNION ALL                                                "
						+ "  SELECT  T1.*                                             "
						+ "  FROM                                                     "
						+ "  RA_TRN_POSTING_HEADER T1,                                "
						+ "  RA_TRN_POSTING_HEADER T2                                 "
						+ "  WHERE                                                    "
						+ "  T1.DEPENDENT_SEQUENCE != 0                               "
						+ "  AND T1.POSTING_STATUS = 'P'                              "
						+ "  AND T2.POSTING_STATUS = 'C'                              "
						+ "  AND T2.EXTRACTION_SEQUENCE = T1.DEPENDENT_SEQUENCE       "
						+ "  AND T1.BUSINESS_DATE= T2.BUSINESS_DATE"
						+ "  AND T2.COUNTRY = T1.COUNTRY                              "
						+ "  AND T2.LE_BOOK = T1.LE_BOOK                              "
						+ "  AND T2.EXTRACTION_FREQUENCY = T1.EXTRACTION_FREQUENCY    "
						+ "  AND T1.EXTRACTION_SEQUENCE =                             "
						+ "  (                                                        "
						+ "  SELECT MIN (EXTRACTION_SEQUENCE)                         "
						+ "  FROM                                                     "
						+ "  RA_TRN_POSTING_HEADER S1                               	"
						+ "  WHERE                                                    "
						+ "  DEPENDENT_SEQUENCE != 0                                	"
						+ "  AND POSTING_STATUS = 'P'                               	"
						+ "  AND S1.COUNTRY = T1.COUNTRY 								"
						+ "  AND S1.LE_BOOK = T1.LE_BOOK								"
						+ " )                                                         "
						+ " ) t1                                                      "
						+ " ) t2                                                      " +
						// " --ORDER BY EXTRACTION_SEQUENCE "+
						" ) t3                                                      "
						+ " ORDER BY EXTRACTION_SEQUENCE                              ";

			}

			ResultSetExtractor mapper = new ResultSetExtractor() {
				@Override
				public Object extractData(ResultSet rs) throws SQLException, DataAccessException {
					ResultSetMetaData metaData = rs.getMetaData();
					int colCount = metaData.getColumnCount();
					Boolean dataPresent = false;
					while (rs.next()) {
						dataPresent = true;
						for (int cn = 1; cn <= colCount; cn++) {
							String columnName = metaData.getColumnName(cn);
							postingHeaderData.put(columnName.toUpperCase(), rs.getString(columnName));
						}
					}
					if (dataPresent) {
						exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
						exceptionCode.setResponse(postingHeaderData);
					} else {
						exceptionCode.setErrorCode(Constants.NO_RECORDS_FOUND);
					}
					return exceptionCode;
				}
			};
			return (ExceptionCode) getJdbcTemplate().query(orginalQuery, mapper);
		} catch (Exception e) {
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg(e.getMessage());
			return exceptionCode;
		}
	}

	public ExceptionCode getEtlPostngDetailGenBuildNew(String country, String leBook, String frequency,
			String extractionSeq, String businessDate) {
		ExceptionCode exceptionCode = new ExceptionCode();
		HashMap<String, String> postingData = new HashMap<String, String>();
		try {
			String orginalQuery = "";
			if ("ORACLE".equalsIgnoreCase(databaseType)) {
				orginalQuery = " SELECT COUNTRY,LE_BOOK,EXTRACTION_FREQUENCY,EXTRACTION_SEQUENCE,EXTRACTION_FEED_ID, "
						+ " POSTING_SEQUENCE,BUSINESS_DATE,POSTING_DATE,POSTING_FT_DATE,EXTRACTION_ENGINE ,POSTED_STATUS FROM( "
						+ " SELECT ROWNUM RN,COUNTRY,LE_BOOK,EXTRACTION_FREQUENCY,EXTRACTION_SEQUENCE,EXTRACTION_FEED_ID, "
						+ " POSTING_SEQUENCE,TO_CHAR(TO_DATE(BUSINESS_DATE),'yyyy-MM-dd') BUSINESS_DATE,TO_CHAR(POSTING_DATE,'DD-Mon-RRRR HH24:MI:SS') POSTING_DATE,"
						+ " TO_CHAR(TO_DATE(POSTING_DATE),'yyyy-MM-dd') POSTING_FT_DATE,EXTRACTION_ENGINE ,POSTED_STATUS FROM "
						+ " ( "
						+ " SELECT * FROM (SELECT * FROM RA_TRN_POSTING WHERE  "
						+ " EXTRACTION_ENGINE IN (SELECT ALPHA_SUB_TAB FROM ALPHA_SUB_TAB WHERE ALPHA_TAB = 7035 AND ALPHA_SUB_TAB!='ETL') " + " AND DEPENDENT_FLAG = 'N' "
						+ " AND POSTED_STATUS = 'P' ORDER BY POSTING_SEQUENCE) " + " UNION ALL " + " SELECT * "
						+ " FROM RA_TRN_POSTING S1 "
						+ " WHERE  "
						+ " EXTRACTION_ENGINE IN (SELECT ALPHA_SUB_TAB FROM ALPHA_SUB_TAB WHERE ALPHA_TAB = 7035 AND ALPHA_SUB_TAB!='ETL') "
						+ " AND DEPENDENT_FLAG = 'Y' " + " AND POSTED_STATUS = 'P' "
						+ " AND DEPENDENT_FEED_ID IN (SELECT EXTRACTION_FEED_ID FROM RA_TRN_POSTING WHERE POSTED_STATUS ='C' AND BUSINESS_DATE = S1.BUSINESS_DATE ) "
						+ " ) T1 WHERE COUNTRY = '" + country + "' AND LE_BOOK='" + leBook
						+ "' AND EXTRACTION_FREQUENCY = '" + frequency + "' " + " AND EXTRACTION_SEQUENCE = '"
						+ extractionSeq + "' "
						+ " AND BUSINESS_DATE = '" + businessDate
						+ "' ORDER BY COUNTRY,LE_BOOK,EXTRACTION_FREQUENCY,EXTRACTION_SEQUENCE,EXTRACTION_FEED_ID, "
						+ " POSTING_SEQUENCE) WHERE RN <= 1 ";
			} else if ("MSSQL".equalsIgnoreCase(databaseType)) {
				orginalQuery = "SELECT													"
						+ "   TOP 1.COUNTRY,                                     "
						+ "   LE_BOOK,                                           "
						+ "   EXTRACTION_FREQUENCY,                              "
						+ "   EXTRACTION_SEQUENCE,                               "
						+ "   EXTRACTION_FEED_ID,                                "
						+ "   POSTING_SEQUENCE,                                  "
						+ "   CONVERT(DATE, BUSINESS_DATE) BUSINESS_DATE,        "
						+ "   POSTING_DATE,                                      "
						+ "   FORMAT(POSTING_DATE, 'yyyy-MM-dd') POSTING_FT_DATE,"
						+ "   EXTRACTION_ENGINE                                  "
						+ "FROM                                                  "
						+ "   (                                                  "
						+ "		SELECT                                         "
						+ "               *                                      "
						+ "            FROM                                      "
						+ "               RA_TRN_POSTING                         "
						+ "            WHERE                                     "
						+ "                EXTRACTION_ENGINE IN                  "
						+ "               (                                      "
						+ "                  SELECT                              "
						+ "                     ALPHA_SUB_TAB                    "
						+ "                  FROM                                "
						+ "                     ALPHA_SUB_TAB                    "
						+ "                  WHERE                               "
						+ "                     ALPHA_TAB = 7035                 "
						+ "                     AND ALPHA_SUB_TAB != 'ETL'       "
						+ "               )                                      "
						+ "               AND DEPENDENT_FLAG = 'N'               "
						+ "               AND POSTED_STATUS = 'P'                "
						+ "      UNION ALL                                       "
						+ "      SELECT                                          "
						+ "         *                                            "
						+ "      FROM                                            "
						+ "         RA_TRN_POSTING S1                            "
						+ "      WHERE                                           "
						+ "         EXTRACTION_ENGINE IN                         "
						+ "         (                                      "
						+ "                  SELECT                              "
						+ "                     ALPHA_SUB_TAB                    "
						+ "                  FROM                                "
						+ "                     ALPHA_SUB_TAB                    "
						+ "                  WHERE                               "
						+ "                     ALPHA_TAB = 7035                 "
						+ "                     AND ALPHA_SUB_TAB != 'ETL'       "
						+ "               )                                            "
						+ "         AND DEPENDENT_FLAG = 'Y'                     "
						+ "         AND POSTED_STATUS = 'P'                      "
						+ "         AND DEPENDENT_FEED_ID IN                     "
						+ "         (                                            "
						+ "            SELECT                                    "
						+ "               EXTRACTION_FEED_ID                     "
						+ "            FROM                                      "
						+ "               RA_TRN_POSTING                         "
						+ "            WHERE                                     "
						+ "               POSTED_STATUS = 'C'                    "
						+ "               AND BUSINESS_DATE = S1.BUSINESS_DATE   "
						+ "         )                                            "
						+ "   )                                                  "
						+ "   T1                                                 "
						+ "WHERE    COUNTRY = '" + country+"' "
						+ " AND LE_BOOK = '" + leBook + "' " + " AND EXTRACTION_FREQUENCY = '" + frequency + "' "
						+ " AND EXTRACTION_SEQUENCE = '" + extractionSeq + "' "
						+ " AND BUSINESS_DATE = '"+businessDate+"'                  "
						+ "ORDER BY                                              "
						+ "BUSINESS_DATE,POSTING_SEQUENCE                        ";
			}

			ResultSetExtractor mapper = new ResultSetExtractor() {
				@Override
				public Object extractData(ResultSet rs) throws SQLException, DataAccessException {
					ResultSetMetaData metaData = rs.getMetaData();
					int colCount = metaData.getColumnCount();
					Boolean dataPresent = false;
					while (rs.next()) {
						dataPresent = true;
						for (int cn = 1; cn <= colCount; cn++) {
							String columnName = metaData.getColumnName(cn);
							postingData.put(columnName.toUpperCase(), rs.getString(columnName));
						}
					}
					if (dataPresent) {
						exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
						exceptionCode.setResponse(postingData);
					} else {
						exceptionCode.setErrorCode(Constants.NO_RECORDS_FOUND);
					}
					return exceptionCode;
				}
			};
			return (ExceptionCode) getJdbcTemplate().query(orginalQuery, mapper);
		} catch (Exception e) {
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg(e.getMessage());
			return exceptionCode;
		}
	}

	public int etlHeaderStatusUpdate(String etlStatusId, String country, String leBook, String extSeq, String extFreq,
			int cnt, String busDate) {
		String query = "";
		int retVal = 0;
		String timeUpdate = "";
		if ("I".equalsIgnoreCase(etlStatusId)) {
			if (cnt == 0)
				timeUpdate = " ,START_TIME = " + getDbFunction("SYSDATE") + " ";
		} else {
			timeUpdate = " ,END_TIME = " + getDbFunction("SYSDATE") + " ";
		}

		try {
			query = "UPDATE RA_TRN_POSTING_HEADER SET POSTING_STATUS = ? " + timeUpdate
					+ " WHERE COUNTRY = ? AND LE_BOOK = ? "
					+ " AND EXTRACTION_SEQUENCE = ?  AND EXTRACTION_FREQUENCY = ? AND BUSINESS_DATE = ? ";

			String query1 = "UPDATE RA_TRN_POSTING_HISTORY_HEADER SET POSTING_STATUS = ? " + timeUpdate
					+ " WHERE COUNTRY = ? AND LE_BOOK = ? "
					+ " AND EXTRACTION_SEQUENCE = ?  AND EXTRACTION_FREQUENCY = ? AND BUSINESS_DATE = ? ";
			Object[] args = { etlStatusId, country, leBook, extSeq, extFreq, busDate };
			retVal = getJdbcTemplate().update(query, args);
			retVal = getJdbcTemplate().update(query1, args);
			return retVal;
		} catch (Exception e) {
			return retVal;
		}
	}

	public int raCronUpdate(String cronType, int runThread) {
		String query = "";
		int retVal = 0;
		try {
			if (runThread == -1)
				runThread = 0;
			query = "UPDATE RA_CRON_CONTROL SET RUN_THREAD = ? WHERE CRON_TYPE = ? ";
			Object[] args = { runThread, cronType };
			retVal = getJdbcTemplate().update(query, args);
			return retVal;
		} catch (Exception e) {
			return retVal;
		}
	}

	public List<EtlPostingsVb> getScheduleList() {
		List<EtlPostingsVb> collTemp = new ArrayList<>();
		String query = "";
		if ("ORACLE".equalsIgnoreCase(databaseType)) {
			query = "SELECT (SELECT TO_CHAR(BUSINESS_DATE,'DD-Mon-RRRR') BUSINESS_DATE FROM VISION_BUSINESS_DAY WHERE COUNTRY= T1.COUNTRY AND LE_BOOK = T1.LE_BOOK AND APPLICATION_ID ='"+productName+"' ) BUSINESS_DATE,"
					+ "T1.COUNTRY,T1.LE_BOOK,T1.EXTRACTION_SEQUENCE,T1.EXTRACTION_FREQUENCY,'9999' SUBMITTER_ID,'I' POSTING_TYPE,EVENT_ID "
					+ "FROM RA_ETL_SCHEDULE T1  WHERE NEXT_SCHEDULE_DATE <= SYSDATE AND"
					+ " T1.SCHEDULE_START_DATE <= TRUNC(SYSDATE) AND "
					+ "	T1.SCHEDULE_END_DATE  >=  TRUNC(SYSDATE) AND "
					+ " (LAST_RUN_STATUS != 'E'  OR LAST_RUN_STATUS IS NULL)";
		} else if ("MSSQL".equalsIgnoreCase(databaseType)) {
			query = "SELECT (SELECT FORMAT(BUSINESS_DATE,'dd-MMM-yyyy') BUSINESS_DATE FROM VISION_BUSINESS_DAY WHERE COUNTRY= T1.COUNTRY AND LE_BOOK = T1.LE_BOOK AND APPLICATION_ID ='"+productName+"') BUSINESS_DATE,"
					+ "T1.COUNTRY,T1.LE_BOOK,T1.EXTRACTION_SEQUENCE,T1.EXTRACTION_FREQUENCY,'9999' SUBMITTER_ID,'I' POSTING_TYPE,EVENT_ID "
					+ "FROM RA_ETL_SCHEDULE T1  WHERE NEXT_SCHEDULE_DATE <= GetDate()  AND "
					+ "   T1.SCHEDULE_START_DATE <= CAST(GETDATE() AS DATE) "
					+ "    AND T1.SCHEDULE_END_DATE >= CAST(GETDATE() AS DATE) "
					+ " AND (LAST_RUN_STATUS != 'E'  OR LAST_RUN_STATUS IS NULL)";
		}
		try {
			collTemp = getJdbcTemplate().query(query.toString(), getScheduleMapper());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return collTemp;
	}

	protected RowMapper getScheduleMapper() {
		RowMapper mapper = new RowMapper() {
			@Override
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				EtlPostingsVb vObject = new EtlPostingsVb();
				vObject.setCountry(rs.getString("COUNTRY"));
				vObject.setLeBook(rs.getString("LE_BOOK"));
				vObject.setExtractionFrequency(rs.getString("EXTRACTION_FREQUENCY"));
				vObject.setExtractionSequence(rs.getString("EXTRACTION_SEQUENCE"));
				vObject.setBusinessDate(rs.getString("BUSINESS_DATE"));
				vObject.setPostedBy(rs.getString("SUBMITTER_ID"));
				vObject.setPostingType(rs.getString("POSTING_TYPE"));
				vObject.setEventName(rs.getString("EVENT_ID"));
				return vObject;
			}
		};
		return mapper;
	}

	public int updateEtlSchedule(String country, String leBook, String frequency, String Sequence,
			String status) {
		int retVal = 0;
		try {
			String query = "SELECT SCHEDULE_FREQUENCY FROM RA_ETL_SCHEDULE WHERE COUNTRY = ? AND LE_BOOK = ? AND EXTRACTION_FREQUENCY = ? AND EXTRACTION_SEQUENCE = ? ";
			Object[] args = { country, leBook, frequency, Sequence };
			int scheduleFrequency = getJdbcTemplate().queryForObject(query, args, Integer.class);
			if("ORACLE".equalsIgnoreCase(databaseType)) {
				query = "UPDATE RA_ETL_SCHEDULE SET LAST_SCHEDULE_DATE = " + getDbFunction("SYSDATE")
						+ " , LAST_RUN_STATUS = ?, NEXT_SCHEDULE_DATE =  TO_TIMESTAMP(SCHEDULE_START_DATE||TO_CHAR(SCHEDULE_TIME,'HH.MI.SS AM'), 'DD-Mon-RR HH.MI.SS AM') + "
						+ scheduleFrequency + " "
					+ " WHERE COUNTRY = ? AND LE_BOOK = ? AND EXTRACTION_FREQUENCY = ? AND EXTRACTION_SEQUENCE = ? ";
			}else if("MSSQL".equalsIgnoreCase(databaseType)) {
				query = "UPDATE RA_ETL_SCHEDULE SET LAST_SCHEDULE_DATE = " + getDbFunction("SYSDATE")
						+ " , LAST_RUN_STATUS = ?, NEXT_SCHEDULE_DATE = CONVERT(DATETIME,CONCAT(CAST(getDAte()+ "
						+ scheduleFrequency + " AS DATE),' ', CAST(SCHEDULE_TIME as time(0))))  "
					+ " WHERE COUNTRY = ? AND LE_BOOK = ? AND EXTRACTION_FREQUENCY = ? AND EXTRACTION_SEQUENCE = ? ";
			}
			

			Object[] args1 = { status, country, leBook, frequency, Sequence };
			retVal = getJdbcTemplate().update(query, args1);
			return retVal;
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}

	}

	public List getCompletedPosting() {
		List collTemp = null;
		try {
			String query = "select COUNTRY,LE_BOOK,EXTRACTION_SEQUENCE,EXTRACTION_FREQUENCY,POSTING_STATUS from ra_trn_posting_header where posting_status = 'C'";
			collTemp = getJdbcTemplate().queryForList(query);
		} catch (Exception e) {

		}
		return collTemp;
	}

	@Scheduled(cron = " 0 0 23 * * ? ") // everyday 11 P.M
	public int clearPostingTableOnDaily() {
		String query = " DELETE FROM RA_TRN_POSTING WHERE  POSTED_STATUS = 'C' ";
		Object[] args = null;
		getJdbcTemplate().update(query, args);
		query = " DELETE FROM RA_TRN_POSTING_HEADER WHERE  POSTING_STATUS = 'C' ";
		getJdbcTemplate().update(query, args);
		return 0;
	}

	public int doDeletePostingsCron(EtlPostingsVb vObject) {
		String query = "";
		if ("ORACLE".equalsIgnoreCase(databaseType)) {
			query = " DELETE FROM RA_TRN_POSTING WHERE COUNTRY = ? AND LE_BOOK = ? "
					+ " AND EXTRACTION_FREQUENCY = ? AND EXTRACTION_SEQUENCE = ? AND EXTRACTION_FEED_ID = ? "
					+ " AND POSTING_SEQUENCE = ? AND BUSINESS_DATE = TO_DATE(?,'RRRR-MM-DD') ";
		} else {
			query = " DELETE FROM RA_TRN_POSTING WHERE COUNTRY = ? AND LE_BOOK = ? "
					+ " AND EXTRACTION_FREQUENCY = ? AND EXTRACTION_SEQUENCE = ? AND EXTRACTION_FEED_ID = ? "
					+ " AND POSTING_SEQUENCE = ? AND BUSINESS_DATE = ?";
		}
		Object[] args = { vObject.getCountry(), vObject.getLeBook(), vObject.getExtractionFrequency(),
				vObject.getExtractionSequence(), vObject.getBusinessDate() };
		return getJdbcTemplate().update(query, args);
	}

	public int checkInProgressFeed(String country, String leBook, String frequency, String extractionSeq,
			String businessDate) {
		int cnt = 0;
		try {
			String query = "";
			query = " Select Count(1) from RA_TRN_POSTING where COUNTRY = ? AND LE_BOOK=? "
					+ " AND EXTRACTION_FREQUENCY = ? AND EXTRACTION_SEQUENCE = ? "
					+ " AND BUSINESS_DATE = ? and Posted_Status = 'I' ";

			Object[] args = { country, leBook, frequency, extractionSeq, businessDate };
			cnt = getJdbcTemplate().queryForObject(query, args, Integer.class);
			return cnt;
		} catch (Exception e) {
			e.printStackTrace();
			return cnt;
		}
	}
	public int updateErrorStatusInEtlSchedule(String country, String leBook, String frequency, String Sequence) {
		int retVal = 0;
		String query = "";
		try {
			query = "UPDATE RA_ETL_SCHEDULE SET  LAST_RUN_STATUS = 'E' "
					+ " WHERE COUNTRY = ? AND LE_BOOK = ? AND EXTRACTION_FREQUENCY = ? AND EXTRACTION_SEQUENCE = ? ";
			Object[] args = { country, leBook, frequency, Sequence };
			retVal = getJdbcTemplate().update(query, args);
			return retVal;
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}
}