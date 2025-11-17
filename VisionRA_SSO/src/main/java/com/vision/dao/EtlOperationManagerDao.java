package com.vision.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.vision.authentication.CustomContextHolder;
import com.vision.exception.ExceptionCode;
import com.vision.util.Constants;
import com.vision.vb.EtlOperationDetailVb;
import com.vision.vb.EtlOperationManagerHeaderVb;
@Component
public class EtlOperationManagerDao extends AbstractDao<EtlOperationManagerHeaderVb>{
	
	@Autowired
	CommonDao commonDao;
	@Value("${app.databaseType}")
	private String databaseType;
	
	@Autowired
	EtlServiceDao etlServiceDao;
	
	protected RowMapper getHeaderMapper(){
		RowMapper mapper = new RowMapper() {
			@Override
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				EtlOperationManagerHeaderVb vObject = new EtlOperationManagerHeaderVb();
				vObject.setPosBusDate(rs.getString("POSBUS_DATE"));
				vObject.setCountry(rs.getString("COUNTRY"));
				vObject.setLeBook(rs.getString("LE_BOOK"));
				vObject.setExtractionFrequency(rs.getString("EXTRACTION_FREQUENCY"));
				vObject.setExtractionProcess(rs.getString("EXTRACTION_PROCESS"));
				vObject.setEtlInitiated(rs.getString("ETL_INITIATED"));
				vObject.setEtlReinitiated(rs.getString("ETL_REINITIATED"));
				vObject.setYetToStart(rs.getString("YET_TO_START"));
				vObject.setInProgress(rs.getString("IN_PROGRESS"));
				vObject.setCompleted(rs.getString("COMPLETED"));
				vObject.setErrored(rs.getString("ERRORED"));
				return vObject;
			}
		};
		return mapper;
	}
	protected RowMapper getDetailMapper(){
		RowMapper mapper = new RowMapper() {
			@Override
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				EtlOperationDetailVb vObject = new EtlOperationDetailVb();
				vObject.setPosBusDate(rs.getString("POSBUS_DATE"));
				vObject.setCountry(rs.getString("COUNTRY"));
				vObject.setLeBook(rs.getString("LE_BOOK"));
				vObject.setFrequency(rs.getString("FREQUENCY"));
				vObject.setExtractionName(rs.getString("EXTRACTION_NAME"));
				vObject.setFeedId(rs.getString("EXTRACTION_FEED_ID"));
				vObject.setProcessId(rs.getString("SYSTEM_PROCESS_ID"));
				vObject.setExtractionSequence(rs.getString("EXTRACTION_SEQUENCE"));
				vObject.setPostingSequence(rs.getString("POSTING_SEQUENCE"));
				vObject.setEtlCategory(rs.getString("ETL_CATEGORY"));
				vObject.setFeedDescription(rs.getString("FEED_DESCRIPTION"));
				vObject.setStartTime(rs.getString("START_TIME"));
				vObject.setEndTime(rs.getString("END_TIME"));
				vObject.setDuration(calcDuration(rs.getString("DURATION")));
				vObject.setEtlStatus(rs.getString("ETL_STATUS"));
				vObject.setExecutionType(rs.getString("EXECUTION_TYPE"));
				vObject.setLogFile(rs.getString("LOG_FILE"));
				vObject.setEtlCategoryId(rs.getString("FEED_CATEGORY"));
				vObject.setEtlStatusId(rs.getString("POSTED_STATUS"));
				vObject.setSelectedDate(rs.getString("SELECTED_DATE"));
				return vObject;
			}
		};
		return mapper;
	}
	public String calcDuration(String durationSec) {
		int seconds = Integer.parseInt(durationSec) % 60;
        int minutes = Integer.parseInt(durationSec) / 60;
        if(minutes > 1 || minutes == 1)
        	durationSec = minutes+" Min(s) "+seconds+" Sec(s) ";
        else 
        	durationSec = seconds+" Sec(s) ";
        return durationSec;
	}
	
	public List<EtlOperationManagerHeaderVb> getQueryHeaderResults(EtlOperationManagerHeaderVb dObj){
		List<EtlOperationManagerHeaderVb> collTemp = null;
		setServiceDefaults();
		String sqlQuery = null;
		String selectedDateField = "";
		String resultDateField = "";
		if("ORACLE".equalsIgnoreCase(databaseType)) {
			if("P".equalsIgnoreCase(dObj.getDateType())) {
				selectedDateField = "POSTING_DATE";
				resultDateField = "BUSINESS_DATE";
			}else {
				selectedDateField = "BUSINESS_DATE";
				resultDateField = "POSTING_DATE";
			}
		} else if("MSSQL".equalsIgnoreCase(databaseType)) {
			if("P".equalsIgnoreCase(dObj.getDateType())) {
				selectedDateField = ""+getDbFunction("CONVERT")+"(DATE,POSTING_DATE)";
				resultDateField = ""+getDbFunction("CONVERT")+"(DATE,BUSINESS_DATE)";
			}else {
				selectedDateField = ""+getDbFunction("CONVERT")+"(DATE,BUSINESS_DATE)";
				resultDateField = ""+getDbFunction("CONVERT")+"(DATE,POSTING_DATE)";
			}
		}
		if("ORACLE".equalsIgnoreCase(databaseType)) {
			sqlQuery = new String("WITH 	"+
					" T1 AS (SELECT COUNT(DISTINCT "+getDbFunction("DATEFUNC")+"("+resultDateField+", '"+getDbFunction("DATEFORMAT")+"')) POSBUS_DATE"+
					" FROM RA_TRN_POSTING_HISTORY WHERE COUNTRY IN ("+dObj.getCountry()+") AND LE_BOOK IN ("+dObj.getLeBook()+") AND EXTRACTION_FREQUENCY IN ("+dObj.getExtractionFrequency()+") AND TRUNC("+selectedDateField+") IN ("+dObj.getPosBusDate()+")),"+
					" T2 AS (SELECT COUNT(DISTINCT COUNTRY) COUNTRY FROM RA_TRN_POSTING_HISTORY "+
					" WHERE COUNTRY IN ("+dObj.getCountry()+") AND LE_BOOK IN ("+dObj.getLeBook()+") AND EXTRACTION_FREQUENCY IN ("+dObj.getExtractionFrequency()+") AND TRUNC("+selectedDateField+") IN ("+dObj.getPosBusDate()+")),"+
					" T3 AS (SELECT COUNT(DISTINCT LE_BOOK) LE_BOOK FROM RA_TRN_POSTING_HISTORY"+ 
					" WHERE COUNTRY IN ("+dObj.getCountry()+") AND LE_BOOK IN ("+dObj.getLeBook()+") AND EXTRACTION_FREQUENCY IN ("+dObj.getExtractionFrequency()+") AND TRUNC("+selectedDateField+") IN ("+dObj.getPosBusDate()+")),"+
					" T4 AS (SELECT COUNT(DISTINCT EXTRACTION_FREQUENCY) EXTRACTION_FREQUENCY FROM RA_TRN_POSTING_HISTORY "+
					" WHERE COUNTRY IN ("+dObj.getCountry()+") AND LE_BOOK IN ("+dObj.getLeBook()+") AND EXTRACTION_FREQUENCY IN ("+dObj.getExtractionFrequency()+") AND TRUNC("+selectedDateField+") IN ("+dObj.getPosBusDate()+")),"+
					" T5 AS (SELECT COUNT(DISTINCT EXTRACTION_SEQUENCE) EXTRACTION_PROCESS FROM RA_TRN_POSTING_HISTORY"+
					" WHERE COUNTRY IN ("+dObj.getCountry()+") AND LE_BOOK IN ("+dObj.getLeBook()+") AND EXTRACTION_FREQUENCY IN ("+dObj.getExtractionFrequency()+") AND TRUNC("+selectedDateField+") IN ("+dObj.getPosBusDate()+")),"+
					" T6 AS (SELECT COUNT(*) ETL_INITIATED FROM RA_TRN_POSTING_HISTORY"+
					" WHERE COUNTRY IN ("+dObj.getCountry()+") AND LE_BOOK IN ("+dObj.getLeBook()+") AND EXTRACTION_FREQUENCY IN ("+dObj.getExtractionFrequency()+") AND TRUNC("+selectedDateField+") IN ("+dObj.getPosBusDate()+")),"+
					" T7 AS (SELECT COUNT(*) ETL_REINITIATED FROM RA_TRN_POSTING_HISTORY"+
					" WHERE COUNTRY IN ("+dObj.getCountry()+") AND LE_BOOK IN ("+dObj.getLeBook()+") AND EXTRACTION_FREQUENCY IN ("+dObj.getExtractionFrequency()+") AND TRUNC("+selectedDateField+") IN ("+dObj.getPosBusDate()+") AND POSTING_SEQUENCE >1),"+
					" T8 AS (SELECT "+
					" SUM(CASE WHEN POSTED_STATUS = 'P' THEN 1 ELSE 0 END) YET_TO_START,"+
					" SUM(CASE WHEN POSTED_STATUS = 'I' THEN 1 ELSE 0 END) IN_PROGRESS,"+
					" SUM(CASE WHEN POSTED_STATUS = 'C' THEN 1 ELSE 0 END) COMPLETED,"+
					" SUM(CASE WHEN POSTED_STATUS = 'E' THEN 1 ELSE 0 END) ERRORED"+
					" FROM RA_TRN_POSTING_HISTORY"+
					" WHERE COUNTRY IN ("+dObj.getCountry()+") AND LE_BOOK IN ("+dObj.getLeBook()+") AND EXTRACTION_FREQUENCY IN ("+dObj.getExtractionFrequency()+") AND TRUNC("+selectedDateField+") IN ("+dObj.getPosBusDate()+"))"+
					" SELECT * FROM T1, T2, T3, T4, T5, T6, T7, T8	");
		}else if("MSSQL".equalsIgnoreCase(databaseType)) {
			sqlQuery = new String("WITH 	"+
					" T1 AS (SELECT COUNT(DISTINCT "+getDbFunction("DATEFUNC")+"("+resultDateField+", '"+getDbFunction("DATEFORMAT")+"')) POSBUS_DATE"+
					" FROM RA_TRN_POSTING_HISTORY WHERE COUNTRY IN ("+dObj.getCountry()+") AND LE_BOOK IN ("+dObj.getLeBook()+") AND EXTRACTION_FREQUENCY IN ("+dObj.getExtractionFrequency()+") AND "+selectedDateField+" IN ("+dObj.getPosBusDate()+")),"+
					" T2 AS (SELECT COUNT(DISTINCT COUNTRY) COUNTRY FROM RA_TRN_POSTING_HISTORY "+
					" WHERE COUNTRY IN ("+dObj.getCountry()+") AND LE_BOOK IN ("+dObj.getLeBook()+") AND EXTRACTION_FREQUENCY IN ("+dObj.getExtractionFrequency()+") AND "+selectedDateField+" IN ("+dObj.getPosBusDate()+")),"+
					" T3 AS (SELECT COUNT(DISTINCT LE_BOOK) LE_BOOK FROM RA_TRN_POSTING_HISTORY"+ 
					" WHERE COUNTRY IN ("+dObj.getCountry()+") AND LE_BOOK IN ("+dObj.getLeBook()+") AND EXTRACTION_FREQUENCY IN ("+dObj.getExtractionFrequency()+") AND "+selectedDateField+" IN ("+dObj.getPosBusDate()+")),"+
					" T4 AS (SELECT COUNT(DISTINCT EXTRACTION_FREQUENCY) EXTRACTION_FREQUENCY FROM RA_TRN_POSTING_HISTORY "+
					" WHERE COUNTRY IN ("+dObj.getCountry()+") AND LE_BOOK IN ("+dObj.getLeBook()+") AND EXTRACTION_FREQUENCY IN ("+dObj.getExtractionFrequency()+") AND "+selectedDateField+" IN ("+dObj.getPosBusDate()+")),"+
					" T5 AS (SELECT COUNT(DISTINCT EXTRACTION_SEQUENCE) EXTRACTION_PROCESS FROM RA_TRN_POSTING_HISTORY"+
					" WHERE COUNTRY IN ("+dObj.getCountry()+") AND LE_BOOK IN ("+dObj.getLeBook()+") AND EXTRACTION_FREQUENCY IN ("+dObj.getExtractionFrequency()+") AND "+selectedDateField+" IN ("+dObj.getPosBusDate()+")),"+
					" T6 AS (SELECT COUNT(*) ETL_INITIATED FROM RA_TRN_POSTING_HISTORY"+
					" WHERE COUNTRY IN ("+dObj.getCountry()+") AND LE_BOOK IN ("+dObj.getLeBook()+") AND EXTRACTION_FREQUENCY IN ("+dObj.getExtractionFrequency()+") AND "+selectedDateField+" IN ("+dObj.getPosBusDate()+")),"+
					" T7 AS (SELECT COUNT(*) ETL_REINITIATED FROM RA_TRN_POSTING_HISTORY"+
					" WHERE COUNTRY IN ("+dObj.getCountry()+") AND LE_BOOK IN ("+dObj.getLeBook()+") AND EXTRACTION_FREQUENCY IN ("+dObj.getExtractionFrequency()+") AND "+selectedDateField+" IN ("+dObj.getPosBusDate()+") AND POSTING_SEQUENCE >1),"+
					" T8 AS (SELECT "+
					" SUM(CASE WHEN POSTED_STATUS = 'P' THEN 1 ELSE 0 END) YET_TO_START,"+
					" SUM(CASE WHEN POSTED_STATUS = 'I' THEN 1 ELSE 0 END) IN_PROGRESS,"+
					" SUM(CASE WHEN POSTED_STATUS = 'C' THEN 1 ELSE 0 END) COMPLETED,"+
					" SUM(CASE WHEN POSTED_STATUS = 'E' THEN 1 ELSE 0 END) ERRORED"+
					" FROM RA_TRN_POSTING_HISTORY"+
					" WHERE COUNTRY IN ("+dObj.getCountry()+") AND LE_BOOK IN ("+dObj.getLeBook()+") AND EXTRACTION_FREQUENCY IN ("+dObj.getExtractionFrequency()+") AND "+selectedDateField+" IN ("+dObj.getPosBusDate()+"))"+
					" SELECT * FROM T1, T2, T3, T4, T5, T6, T7, T8	");
		}
		try
		{
				logger.info("Executing approved query");
				collTemp = getJdbcTemplate().query(sqlQuery.toString(),getHeaderMapper());
			return collTemp;
		}catch(Exception ex){
			ex.printStackTrace();
			logger.error("Error: getQueryResults Exception :   ");
				//logger.error(((sqlQuery == null) ? "sqlQuery is Null" : sqlQuery.toString()));
			/*if (objParams != null)
				for(int i=0 ; i< objParams.length; i++)
					//logger.error("objParams[" + i + "]" + objParams[i].toString());*/
			return null;
		}
	}

	public List<EtlOperationDetailVb> getQueryDetailResults(EtlOperationManagerHeaderVb dObj){
		List<EtlOperationDetailVb> collTemp = null;
		final int intKeyFieldsCount = 4;
		setServiceDefaults();
		String sqlQuery = null;
		String orderBy = "";
		String selectedDateField = "";
		String resultDateField = "";
		if("ORACLE".equalsIgnoreCase(databaseType)) {
			if("P".equalsIgnoreCase(dObj.getDateType())) {
				selectedDateField = "T1.POSTING_DATE";
				resultDateField = "T1.BUSINESS_DATE";
			}else {
				selectedDateField = "T1.BUSINESS_DATE";
				resultDateField = "T1.POSTING_DATE";
			}
		} else if("MSSQL".equalsIgnoreCase(databaseType)) {
			if("P".equalsIgnoreCase(dObj.getDateType())) {
				selectedDateField = "CONVERT(DATE,T1.POSTING_DATE)";
				resultDateField = "CONVERT(DATE,T1.BUSINESS_DATE)";
			}else {
				selectedDateField = "CONVERT(DATE,T1.BUSINESS_DATE)";
				resultDateField = "CONVERT(DATE,T1.POSTING_DATE)";
			}
		}
			
		if ("ORACLE".equalsIgnoreCase(databaseType)) {
			sqlQuery = new String("SELECT  "+	
					" TO_CHAR("+resultDateField+",'DD-Mon-RRRR') POSBUS_DATE,  "+
					" " + dObj.getPosBusDate() + " SELECTED_DATE," +
					" T1.COUNTRY,  "+ 
					" T1.LE_BOOK,  "+
					" T1.EXTRACTION_FREQUENCY FREQUENCY,  "+
					" EXTRACTION_DESCRIPTION EXTRACTION_NAME, "+
					" T1.EXTRACTION_FEED_ID, "+
					" T1.SYSTEM_PROCESS_ID, "+
					" T1.EXTRACTION_SEQUENCE, "+
					" T1.POSTING_SEQUENCE, "+
					" FEED_CATEGORY, " + " POSTED_STATUS, " +
					" (SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB = FEED_CATEGORY_AT AND ALPHA_SUB_TAB = FEED_CATEGORY) ETL_CATEGORY, "+
					" CASE WHEN T2.EXTRACTION_ENGINE IN ('BLD','RCY','MST') THEN (SELECT PROGRAM_DESCRIPTION FROM PROGRAMS WHERE PROGRAM = EXTRACTION_FEED_ID) "+
					" WHEN T2.EXTRACTION_ENGINE IN ('CHG','REC') THEN (SELECT BUSINESS_LINE_ID||' - '||BUSINESS_LINE_DESCRIPTION FROM RA_MST_BUSINESS_LINE_HEADER WHERE BUSINESS_LINE_ID = EXTRACTION_FEED_ID) "+
					" ELSE (SELECT GENERAL_DESCRIPTION FROM Template_Names WHERE TEMPLATE_NAME = EXTRACTION_FEED_ID) END FEED_DESCRIPTION, "+
					" TO_CHAR(T1.START_TIME,'HH24:MI:SS') START_TIME,TO_CHAR(T1.END_TIME,'HH24:MI:SS') END_TIME,"+
					" CASE WHEN START_TIME = TO_TIMESTAMP('1900-01-01 00:00:00','RRRR-MM-DD HH24:MI:SS') THEN '0' "+
					" WHEN END_TIME = TO_TIMESTAMP('1900-01-01 00:00:00','RRRR-MM-DD HH24:MI:SS') THEN "+
		            " TO_CHAR (ROUND(TO_NUMBER (START_TIME - SYSDATE) * 24 * 60 * 60),2)"+
		            " ELSE "+
		            " TO_CHAR(ROUND(NVL (TO_CHAR (TO_NUMBER (END_TIME - START_TIME) * 24 * 60 * 60),"+
		            " 0),2))END DURATION, "+
					" (SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB = 7039 AND ALPHA_SUB_TAB = T1.POSTED_STATUS)  ETL_STATUS, "+
					" CASE WHEN T1.POSTING_SEQUENCE =1 THEN 'New Initiate' ELSE 'Re-Initiate' END EXECUTION_TYPE, "+
					" T1.LOG_FILE_NAME LOG_FILE "+
					" FROM 	 "+
					" RA_TRN_POSTING_HISTORY T1, "+
					" RA_MST_EXTRACTION_HEADER T2, "+
					" RA_MST_EXTRACTION_DETAIL T3 "+
					" WHERE 	 "+
					" T1.COUNTRY = T2.COUNTRY "+
					" AND T1.LE_BOOK = T2.LE_BOOK "+
					" AND T1.EXTRACTION_FREQUENCY = T2.EXTRACTION_FREQUENCY "+
					" AND T1.EXTRACTION_SEQUENCE = T2.EXTRACTION_SEQUENCE "+
					" AND T1.COUNTRY = T3.COUNTRY "+
					" AND T1.LE_BOOK = T3.LE_BOOK "+
					" AND T1.EXTRACTION_FREQUENCY = T3.EXTRACTION_FREQUENCY "+
					" AND T1.EXTRACTION_SEQUENCE = T3.EXTRACTION_SEQUENCE "+
					" AND T3.FEED_ID = T1.EXTRACTION_FEED_ID "+
					" AND T1.COUNTRY IN ("+dObj.getCountry()+")  "+
					" AND T1.LE_BOOK IN ("+dObj.getLeBook()+")  "+
					" AND T1.EXTRACTION_FREQUENCY IN ("+dObj.getExtractionFrequency()+") "+
					" AND TRUNC("+selectedDateField+") IN ("+dObj.getPosBusDate()+")");
		}else if ("MSSQL".equalsIgnoreCase(databaseType)) {
		sqlQuery = new String("SELECT  "+	
				" Format("+resultDateField+",'dd-MMM-yyyy') POSBUS_DATE,  "+
					" " + dObj.getPosBusDate() + " SELECTED_DATE," +
				" T1.COUNTRY,  "+ 
				" T1.LE_BOOK,  "+
				" T1.EXTRACTION_FREQUENCY FREQUENCY,  "+
				" EXTRACTION_DESCRIPTION EXTRACTION_NAME, "+
				" T1.EXTRACTION_FEED_ID, " + 
				" T1.SYSTEM_PROCESS_ID, " + 
				" T1.EXTRACTION_SEQUENCE, " +
				" T1.POSTING_SEQUENCE, " +
					" FEED_CATEGORY, " + " POSTED_STATUS, "
					+
				" (SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB = FEED_CATEGORY_AT AND ALPHA_SUB_TAB = FEED_CATEGORY) ETL_CATEGORY, "+
				" CASE WHEN T2.EXTRACTION_ENGINE IN ('BLD','RCY') THEN (SELECT PROGRAM_DESCRIPTION FROM PROGRAMS WHERE PROGRAM = EXTRACTION_FEED_ID) "+
				" WHEN T2.EXTRACTION_ENGINE IN ('CHG','REC') THEN (SELECT BUSINESS_LINE_ID+' - '+BUSINESS_LINE_DESCRIPTION FROM RA_MST_BUSINESS_LINE_HEADER WHERE BUSINESS_LINE_ID = EXTRACTION_FEED_ID) "+
				" ELSE (SELECT GENERAL_DESCRIPTION FROM Template_Names WHERE TEMPLATE_NAME = EXTRACTION_FEED_ID) END FEED_DESCRIPTION, "+
				" CONVERT(varchar,T1.START_TIME, 108) START_TIME,CONVERT(varchar,T1.END_TIME, 108) END_TIME,"+
				" CASE "+
				" WHEN START_TIME = '1900-01-01 00:00:00.000' THEN "+
				" 	'0' "+
				" WHEN END_TIME = '1900-01-01 00:00:00.000' THEN  "+
				" 	CAST(DATEDIFF(SECOND, START_TIME, getdate()) AS VARCHAR) "+
				" ELSE "+
				" 	CAST(DATEDIFF(SECOND, START_TIME, END_TIME) AS VARCHAR) "+
				" END DURATION, "+
				" (SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB = 7039 AND ALPHA_SUB_TAB = T1.POSTED_STATUS)  ETL_STATUS, "+
				" CASE WHEN T1.POSTING_SEQUENCE =1 THEN 'New Initiate' ELSE 'Re-Initiate' END EXECUTION_TYPE, "+
				" T1.LOG_FILE_NAME LOG_FILE "+
				" FROM 	 "+
				" RA_TRN_POSTING_HISTORY T1, "+
				" RA_MST_EXTRACTION_HEADER T2, "+
				" RA_MST_EXTRACTION_DETAIL T3 "+
				" WHERE 	 "+
				" T1.COUNTRY = T2.COUNTRY "+
				" AND T1.LE_BOOK = T2.LE_BOOK "+
				" AND T1.EXTRACTION_FREQUENCY = T2.EXTRACTION_FREQUENCY "+
				" AND T1.EXTRACTION_SEQUENCE = T2.EXTRACTION_SEQUENCE "+
				" AND T3.FEED_ID = T1.EXTRACTION_FEED_ID "+
				" AND T1.COUNTRY = T3.COUNTRY "+
				" AND T1.LE_BOOK = T3.LE_BOOK "+
				" AND T1.EXTRACTION_FREQUENCY = T3.EXTRACTION_FREQUENCY "+
				" AND T1.EXTRACTION_SEQUENCE = T3.EXTRACTION_SEQUENCE "+
				" AND T1.COUNTRY IN ("+dObj.getCountry()+")  "+
				" AND T1.LE_BOOK IN ("+dObj.getLeBook()+")  "+
				" AND T1.EXTRACTION_FREQUENCY IN ("+dObj.getExtractionFrequency()+") "+
				" AND "+selectedDateField+" IN ("+dObj.getPosBusDate()+")");
		if("P".equalsIgnoreCase(dObj.getDateType())) {
			orderBy = "ORDER BY T1.BUSINESS_DATE DESC,T1.POSTING_SEQUENCE DESC,FEED_ID, COUNTRY,LE_BOOK,FREQUENCY,EXTRACTION_NAME,ETL_CATEGORY";
		}else
			orderBy = "ORDER BY T1.POSTING_DATE DESC,T1.POSTING_SEQUENCE DESC,FEED_ID, COUNTRY,LE_BOOK,FREQUENCY,EXTRACTION_NAME,ETL_CATEGORY";
		
		sqlQuery = sqlQuery +orderBy; 
		}
		try
		{
				logger.info("Executing approved query");
				collTemp = getJdbcTemplate().query(sqlQuery.toString(),getDetailMapper());
			return collTemp;
		}catch(Exception ex){
			ex.printStackTrace();
			logger.error("Error: getQueryResults Exception :   ");
				//logger.error(((sqlQuery == null) ? "sqlQuery is Null" : sqlQuery.toString()));
				return null;
		}
	}
	/*public String getVisionBusinessDay(String coun) {
		String sql = "";
		if ("ORACLE".equalsIgnoreCase(databaseType)) {
			sql = "select TO_CHAR(BUSINESS_DATE,'DD-Mon-RRRR') BUSINESS_DATE  from VISION_BUSINESS_DAY";
		} else if ("MSSQL".equalsIgnoreCase(databaseType)) {
			sql = "	SELECT distinct FORMAT(BUSINESS_DATE,'dd-MMM-yyyy') BUSINESS_DATE FROM VISION_BUSINESS_DAY";
			
		}
		return getJdbcTemplate().queryForObject(sql,String.class);
	}*/
	public ExceptionCode killProcess(EtlOperationDetailVb vObject) {
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			Runtime.getRuntime().exec("taskkill /F /PID " + vObject.getProcessId() + "");
			
			doUpdatePostingsTerminate(vObject);
			
			doUpdatePostingsTermHistory(vObject);
			
			exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
			exceptionCode.setErrorMsg("Process Terminated Successfully["+vObject.getFeedId()+"]");
			exceptionCode.setOtherInfo(vObject);
		}catch (Exception e) {
			exceptionCode.setErrorMsg(e.getMessage());
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setOtherInfo(vObject);
		}
		return exceptionCode;
	}

	public int doUpdatePostingsTerminate(EtlOperationDetailVb vObject) {
		String query = "";
		String resdate = "";
		String seldate = "";
		if ("ORACLE".equalsIgnoreCase(databaseType)) {
			if ("P".equalsIgnoreCase(vObject.getDateType())) {
				resdate = "BUSINESS_DATE = TO_DATE('" + vObject.getPosBusDate() + "','RRRR-MM-DD')";
				seldate = "POSTING_DATE = TO_DATE('" + vObject.getSelectedDate() + "','DD-MM-RRRR HH24:MI:SS')";
			} else {
				resdate = "POSTING_DATE = TO_DATE('" + vObject.getPosBusDate() + "','DD-MM-RRRR HH24:MI:SS')";
				seldate = "BUSINESS_DATE = TO_DATE('" + vObject.getSelectedDate() + "','RRRR-MM-DD')";
			}
		} else if ("MSSQL".equalsIgnoreCase(databaseType)) {
			if ("P".equalsIgnoreCase(vObject.getDateType())) {
				resdate = "BUSINESS_DATE = CONVERT(varchar,'" + vObject.getPosBusDate() + "',103)";
				seldate = "POSTING_DATE = CONVERT(datetime,'" + vObject.getSelectedDate() + "',103)";
			} else {
				resdate = "POSTING_DATE = CONVERT(datetime,'" + vObject.getPosBusDate() + "',103)";
				seldate = "BUSINESS_DATE = CONVERT(varchar,'" + vObject.getSelectedDate() + "',103)";
			}
		}
		try {
			query = " Update RA_TRN_POSTING SET POSTED_STATUS = ?,TERMINATED_USER='" + intCurrentUserId + "' "
					+ " END_TIME = " + getDbFunction("SYSDATE") + " WHERE COUNTRY = ? AND LE_BOOK = ? "
					+ " AND EXTRACTION_FREQUENCY = ? AND EXTRACTION_SEQUENCE = ? AND EXTRACTION_FEED_ID = ? "
					+ " AND POSTING_SEQUENCE = ? AND " + resdate + " AND " + seldate + "  ";

			Object[] args = { "T", vObject.getCountry(), vObject.getLeBook(), vObject.getFrequency(),
					vObject.getExtractionSequence(), vObject.getFeedId(), vObject.getPostingSequence() };
			return getJdbcTemplate().update(query, args);
		} catch (Exception e) {
			return 0;
		}
	}

	public int doUpdatePostingsTermHistory(EtlOperationDetailVb vObject) {
		String query = "";
		String resdate = "";
		String seldate = "";
		intCurrentUserId = CustomContextHolder.getContext().getVisionId();
		if ("ORACLE".equalsIgnoreCase(databaseType)) {
			if ("P".equalsIgnoreCase(vObject.getDateType())) {
				resdate = "BUSINESS_DATE = TO_DATE('" + vObject.getPosBusDate() + "','DD-Mon-RRRR')";
				seldate = "TRUNC(POSTING_DATE) = TO_DATE('" + vObject.getSelectedDate() + "','DD-MM-RRRR HH24:MI:SS')";
			} else {
				resdate = "TRUNC(POSTING_DATE) = TO_DATE('" + vObject.getPosBusDate() + "','DD-MM-RRRR HH24:MI:SS')";
				seldate = "BUSINESS_DATE = TO_DATE('" + vObject.getSelectedDate() + "','DD-Mon-RRRR')";
			}
		} else if ("MSSQL".equalsIgnoreCase(databaseType)) {
			if ("P".equalsIgnoreCase(vObject.getDateType())) {
				resdate = "BUSINESS_DATE = CONVERT(varchar,'" + vObject.getPosBusDate() + "',103)";
				seldate = "POSTING_DATE = CONVERT(datetime,'" + vObject.getSelectedDate() + "',103)";
			} else {
				resdate = "POSTING_DATE = CONVERT(datetime,'" + vObject.getPosBusDate() + "',103)";
				seldate = "BUSINESS_DATE = CONVERT(varchar,'" + vObject.getSelectedDate() + "',103)";
			}
		}
		try {
			query = " Update RA_TRN_POSTING_HISTORY SET POSTED_STATUS = ?,TERMINATED_USER='" + intCurrentUserId
					+ "',END_TIME = " + getDbFunction("SYSDATE") + " WHERE COUNTRY = ? AND LE_BOOK = ? "
					+ " AND EXTRACTION_FREQUENCY = ? AND EXTRACTION_SEQUENCE = ? AND EXTRACTION_FEED_ID = ? "
					+ " AND POSTING_SEQUENCE = ?  AND " + resdate + " AND " + seldate + " ";
			Object[] args = { "T", vObject.getCountry(), vObject.getLeBook(), vObject.getFrequency(),
					vObject.getExtractionSequence(), vObject.getFeedId(), vObject.getPostingSequence() };
			return getJdbcTemplate().update(query, args);
		} catch (Exception e) {
			return 0;
		}
	}
}
