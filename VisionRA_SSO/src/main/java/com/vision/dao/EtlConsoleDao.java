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
import com.vision.util.ValidationUtil;
import com.vision.vb.CommonApiModel;
import com.vision.vb.EtlConsoleDetailVb;
import com.vision.vb.EtlConsoleHeaderVb;
import com.vision.vb.EtlPostingsHeaderVb;
import com.vision.vb.EtlPostingsVb;

@Component
public class EtlConsoleDao extends AbstractDao<EtlPostingsHeaderVb> {

	@Autowired
	CommonDao commonDao;
	@Value("${app.databaseType}")
	private String databaseType;
	
	@Value("${app.clientName}")
	private String clientName;
	@Autowired
	CommonApiDao commonApiDao;

	@Autowired
	EtlServiceDao etlServiceDao;
	@Autowired
	EtlPostingsDao etlPostingsDao;
	

	protected RowMapper getSummaryMapper() {
		RowMapper mapper = new RowMapper() {
			@Override
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				EtlConsoleHeaderVb vObject = new EtlConsoleHeaderVb();
				vObject.setBusinessDate(rs.getString("POSBUS_DATE"));
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

	protected RowMapper getHeaderMapper() {
		RowMapper mapper = new RowMapper() {
			@Override
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				EtlPostingsHeaderVb vObject = new EtlPostingsHeaderVb();
				vObject.setCountry(rs.getString("COUNTRY"));
				vObject.setLeBook(rs.getString("LE_BOOK"));
				vObject.setCountryLebDesc(vObject.getCountry() + " - " + vObject.getLeBook());
				vObject.setExtractionSequence(rs.getInt("EXTRACTION_SEQUENCE"));
				vObject.setExtractionSeqDescription(rs.getString("EXTRACTION_SEQUENCE_DESC"));
				vObject.setExtractionFrequency(rs.getString("EXTRACTION_FREQUENCY"));
				vObject.setExtractionFrequencyDesc(rs.getString("EXTRACTION_FREQUENCY_DESC"));
				vObject.setBusinessDate(rs.getString("BUSINESS_DATE"));
				vObject.setExtractionEngine(rs.getString("EXTRACTION_ENGINE"));
				vObject.setExtractionEngineDesc(rs.getString("EXTRACTION_ENGINE_DESC"));
				vObject.setStartTime(rs.getString("START_TIME"));
				vObject.setEndTime(rs.getString("END_TIME"));
				vObject.setNode(rs.getString("NODE"));
				vObject.setSubmitType(rs.getString("SUBMIT_TYPE"));
				vObject.setSubmitterId(rs.getString("SUBMITTER_ID"));
				vObject.setPostingStatus(rs.getString("POSTING_STATUS"));
				vObject.setPostingStatusDesc(rs.getString("POSTING_STATUS_DESC"));
				vObject.setDateCreation(rs.getString("DATE_CREATION"));
				return vObject;
			}
		};
		return mapper;
	}

	protected RowMapper getDetailMapper() {
		RowMapper mapper = new RowMapper() {
			@Override
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				EtlConsoleDetailVb vObject = new EtlConsoleDetailVb();
				vObject.setBusinessDate(rs.getString("BUSINESS_DATE"));
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
				return vObject;
			}
		};
		return mapper;
	}

	protected RowMapper getDetailRestartMapper() {
		RowMapper mapper = new RowMapper() {
			@Override
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				EtlPostingsVb vObject = new EtlPostingsVb();
				vObject.setCountry(rs.getString("COUNTRY"));
				vObject.setLeBook(rs.getString("LE_BOOK"));
				vObject.setExtractionSequence(rs.getString("EXTRACTION_SEQUENCE"));
				vObject.setExtractionFrequency(rs.getString("EXTRACTION_FREQUENCY"));
				vObject.setBusinessDate(rs.getString("BUSINESS_DATE"));
				vObject.setExtractionFeedId(rs.getString("EXTRACTION_FEED_ID"));
				vObject.setPostingSequence(rs.getString("POSTING_SEQUENCE"));
				vObject.setExtractionEngine(rs.getString("EXTRACTION_ENGINE"));
				vObject.setExrtactionType(rs.getString("EXRTACTION_TYPE"));
				vObject.setEventName(rs.getString("EVENT_NAME"));
				vObject.setDebugMode(rs.getString("DEBUG_MODE"));
				vObject.setDependentFlag(rs.getString("DEPENDENT_FLAG"));
				vObject.setStartTime(rs.getString("START_TIME"));
				vObject.setEndTime(rs.getString("END_TIME"));
				vObject.setPostedStatus(rs.getString("POSTED_STATUS"));
				vObject.setPostedBy(rs.getString("POSTED_BY"));
				vObject.setLogFileName(rs.getString("LOG_FILE_NAME"));
				return vObject;
			}
		};
		return mapper;
	}

	public String calcDuration(String durationSec) {
		int seconds = Integer.parseInt(durationSec) % 60;
		int minutes = Integer.parseInt(durationSec) / 60;
		if (minutes > 1 || minutes == 1)
			durationSec = minutes + " Min(s) " + seconds + " Sec(s) ";
		else
			durationSec = seconds + " Sec(s) ";
		return durationSec;
	}

	public ExceptionCode getQuerySummaryResultsNew(EtlPostingsHeaderVb dObj) {
		ExceptionCode exceptionCode = new ExceptionCode();
		CommonApiModel commonApiModel = new CommonApiModel();
		String query = "";
		try {
			if ("ORACLE".equalsIgnoreCase(databaseType)) {
				query = "SELECT S2.ALPHA_SUB_TAB,NVL(SUM(POSTED_STATUS_CNT),0) STATUS_CNT FROM ( "
					+ " SELECT POSTED_STATUS,COUNT(POSTED_STATUS) POSTED_STATUS_CNT FROM  RA_TRN_POSTING T1  	WHERE  "
					+ " 	T1.COUNTRY = " + dObj.getCountry() + " AND T1.LE_BOOK = " + dObj.getLeBook()
					+ "  AND T1.EXTRACTION_SEQUENCE= " + dObj.getExtractionSequence() + "   "
					+ " 	AND T1.EXTRACTION_FREQUENCY = " + dObj.getExtractionFrequency()
					+ " AND  T1.BUSINESS_DATE = " + dObj.getBusinessDate() + " "
					+ " GROUP BY POSTED_STATUS) S1,ALPHA_SUB_TAB S2 "
					+ " 	WHERE S2.ALPHA_SUB_TAB = S1.POSTED_STATUS(+)  	AND ALPHA_TAB = 7039 "
					+ " 	GROUP BY S2.ALPHA_SUB_TAB  	UNION ALL  "
					+ " 	SELECT 'TOT' ID,COUNT(1) DESCRIPTION  	FROM RA_TRN_POSTING T1 WHERE  "
					+ " 	T1.COUNTRY = " + dObj.getCountry() + " AND T1.LE_BOOK = " + dObj.getLeBook() + " "
					+ " 	AND T1.EXTRACTION_SEQUENCE= " + dObj.getExtractionSequence() + "  "
					+ " 	AND T1.EXTRACTION_FREQUENCY = " + dObj.getExtractionFrequency() + ""
					+ "		AND T1.BUSINESS_DATE = " + dObj.getBusinessDate() + " ";
			} else if ("MSSQL".equalsIgnoreCase(databaseType)) {
				query = "WITH T1 AS ( SELECT															 "
						+ " S2.ALPHA_SUB_TAB,                                                  "
						+ " ISNULL(SUM(POSTED_STATUS_CNT),0) STATUS_CNT                        "
						+ " FROM (SELECT	POSTED_STATUS,COUNT(POSTED_STATUS) POSTED_STATUS_CNT "
						+ " FROM RA_TRN_POSTING T1 WHERE    T1.COUNTRY = " + dObj.getCountry()
						+ "                             " + " AND T1.LE_BOOK = " + dObj.getLeBook()
						+ " AND T1.EXTRACTION_SEQUENCE= " + dObj.getExtractionSequence() + "   "
						+ " AND T1.EXTRACTION_FREQUENCY = " + dObj.getExtractionFrequency() + "  "
						+ " AND T1.BUSINESS_DATE = " + dObj.getBusinessDate()
						+ "  GROUP BY POSTED_STATUS) S1                                         "
						+ " LEFT OUTER JOIN                                                    "
						+ " ALPHA_SUB_TAB S2 ON                                                "
						+ " S2.ALPHA_SUB_TAB = S1.POSTED_STATUS                                "
						+ " WHERE ALPHA_TAB = 7039                                             "
						+ " GROUP BY S2.ALPHA_SUB_TAB                                          "
						+ " UNION ALL                                                          "
						+ " SELECT 'TOT' ID,COUNT(1) DESCRIPTION                               "
						+ " FROM RA_TRN_POSTING T1                                             "
						+ " WHERE T1.COUNTRY = " + dObj.getCountry() + " " + "AND T1.LE_BOOK = " + dObj.getLeBook()
						+ "                            " + " AND T1.EXTRACTION_SEQUENCE= "
						+ dObj.getExtractionSequence() + "   " + " AND T1.EXTRACTION_FREQUENCY =  "
						+ dObj.getExtractionFrequency() + " " + " AND T1.BUSINESS_DATE = " + dObj.getBusinessDate()
						+ "), T2 AS (SELECT ALPHA_SUB_TAB,0 STATUS_CNT FROM ALPHA_SUB_TAB WHERE ALPHA_TAB = 7039),"
						+ "T3 AS (SELECT * FROM T1 UNION ALL SELECT * FROM T2) "
						+ "SELECT T3.ALPHA_SUB_TAB,SUM(T3.STATUS_CNT) STATUS_CNT FROM T3 GROUP BY T3.ALPHA_SUB_TAB";
			}

			exceptionCode = commonApiDao.getCommonResultDataFetch(commonApiModel, query);
		} catch (Exception e) {
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg(e.getMessage());
		}
		return exceptionCode;
	}

	public List<EtlConsoleDetailVb> getQueryDetailResults(EtlPostingsHeaderVb dObj) {
		List<EtlConsoleDetailVb> collTemp = null;
		final int intKeyFieldsCount = 4;
		setServiceDefaults();
		String sqlQuery = null;
		String orderBy = "";

		boolean flag = !"NCBA".equalsIgnoreCase(clientName);
		String programDesc = flag?
				"SELECT PROGRAM_DESCRIPTION FROM PROGRAMS  WHERE PROGRAM = FEED_ID ":"SELECT PROGRAM_DESCRIPTION FROM RA_PROGRAMS P1 WHERE P1.PROGRAM = FEED_ID AND P1.COUNTRY = T1.COUNTRY AND P1.LE_BOOK = T1.LE_BOOK";
		if ("ORACLE".equalsIgnoreCase(databaseType)) {
			sqlQuery = new String(
					"SELECT TO_CHAR(T1.BUSINESS_DATE,'DD-Mon-RRRR') BUSINESS_DATE ,T1.COUNTRY,T1.LE_BOOK,  "
							+ " T1.EXTRACTION_FREQUENCY FREQUENCY,EXTRACTION_DESCRIPTION EXTRACTION_NAME, "
							+ " T1.EXTRACTION_FEED_ID, T1.SYSTEM_PROCESS_ID,T1.EXTRACTION_SEQUENCE, "
							+ " T1.POSTING_SEQUENCE, FEED_CATEGORY, POSTED_STATUS, "
					+ " (SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB = FEED_CATEGORY_AT AND ALPHA_SUB_TAB = FEED_CATEGORY) ETL_CATEGORY, "
					+ " CASE WHEN T2.EXTRACTION_ENGINE IN ('BLD','RCY','MST') THEN ("+programDesc+") "
					+ " WHEN T2.EXTRACTION_ENGINE IN ('CHG','REC','TAX','TREC','TRCV','TRCB','RCB', 'RCV','CONC') THEN (SELECT BUSINESS_LINE_ID||' - '||BUSINESS_LINE_DESCRIPTION FROM RA_MST_BUSINESS_LINE_HEADER WHERE COUNTRY = T1.COUNTRY AND LE_BOOK = T1.LE_BOOK AND BUSINESS_LINE_ID = EXTRACTION_FEED_ID) "
					+ " ELSE (SELECT GENERAL_DESCRIPTION FROM Template_Names WHERE TEMPLATE_NAME = EXTRACTION_FEED_ID) END FEED_DESCRIPTION, "
					+ " TO_CHAR(T1.START_TIME,'HH24:MI:SS') START_TIME,TO_CHAR(T1.END_TIME,'HH24:MI:SS') END_TIME,"
					+ " CASE WHEN START_TIME = TO_TIMESTAMP('1900-01-01 00:00:00','RRRR-MM-DD HH24:MI:SS') THEN '0' "
					+ " WHEN END_TIME = TO_TIMESTAMP('1900-01-01 00:00:00','RRRR-MM-DD HH24:MI:SS') THEN "
							+ " TO_CHAR (ROUND(TO_NUMBER (START_TIME - SYSDATE) * 24 * 60 * 60),2) ELSE "
					+ " TO_CHAR(ROUND(NVL (TO_CHAR (TO_NUMBER (END_TIME - START_TIME) * 24 * 60 * 60),"
					+ " 0),2))END DURATION, "
					+ " (SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB = 7039 AND ALPHA_SUB_TAB = T1.POSTED_STATUS)  ETL_STATUS, "
					+ " CASE WHEN T1.POSTING_SEQUENCE =1 THEN 'New Initiate' ELSE 'Re-Initiate' END EXECUTION_TYPE, "
							+ " T1.LOG_FILE_NAME LOG_FILE FROM RA_TRN_POSTING T1, "
							+ " RA_MST_EXTRACTION_HEADER T2, RA_MST_EXTRACTION_DETAIL T3 WHERE 	 "
							+ " T1.COUNTRY = T2.COUNTRY AND T1.LE_BOOK = T2.LE_BOOK "
					+ " AND T1.EXTRACTION_FREQUENCY = T2.EXTRACTION_FREQUENCY "
							+ " AND T1.EXTRACTION_SEQUENCE = T2.EXTRACTION_SEQUENCE AND T1.COUNTRY = T3.COUNTRY "
							+ " AND T1.LE_BOOK = T3.LE_BOOK AND T1.EXTRACTION_FREQUENCY = T3.EXTRACTION_FREQUENCY "
					+ " AND T1.EXTRACTION_SEQUENCE = T3.EXTRACTION_SEQUENCE "
							+ " AND T3.FEED_ID = T1.EXTRACTION_FEED_ID AND T1.COUNTRY = " + dObj.getCountry()
							+ " AND T1.LE_BOOK = " + dObj.getLeBook() + " AND T1.EXTRACTION_FREQUENCY IN ("
							+ dObj.getExtractionFrequency() + ") AND T1.EXTRACTION_SEQUENCE = "
							+ dObj.getExtractionSequence() + " AND T1.BUSINESS_DATE=" + dObj.getBusinessDate() + " ");
		} else if ("MSSQL".equalsIgnoreCase(databaseType)) {
			sqlQuery = new String(
					"SELECT Format(T1.BUSINESS_DATE,'dd-MMM-yyyy') BUSINESS_DATE ,T1.COUNTRY,  T1.LE_BOOK,  "
							+ " T1.EXTRACTION_FREQUENCY FREQUENCY,  EXTRACTION_DESCRIPTION EXTRACTION_NAME, "
							+ " T1.EXTRACTION_FEED_ID,  T1.SYSTEM_PROCESS_ID,  T1.EXTRACTION_SEQUENCE, "
							+ " T1.POSTING_SEQUENCE,  FEED_CATEGORY,  POSTED_STATUS, "
					+ " (SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB = FEED_CATEGORY_AT AND ALPHA_SUB_TAB = FEED_CATEGORY) ETL_CATEGORY, "
					+ " CASE WHEN T2.EXTRACTION_ENGINE IN ('BLD','RCY','MST') THEN ("+programDesc+") "
					+ " WHEN T2.EXTRACTION_ENGINE IN ('CHG','REC','TAX','TREC','TRCV','TRCB','RCB', 'RCV','CONC') THEN (SELECT BUSINESS_LINE_ID+' - '+BUSINESS_LINE_DESCRIPTION FROM RA_MST_BUSINESS_LINE_HEADER WHERE COUNTRY = T1.COUNTRY AND LE_BOOK = T1.LE_BOOK AND BUSINESS_LINE_ID = EXTRACTION_FEED_ID) "
					+ " ELSE ("+programDesc+") END FEED_DESCRIPTION, "
					+ " CONVERT(varchar,T1.START_TIME, 108) START_TIME,CONVERT(varchar,T1.END_TIME, 108) END_TIME,"
							+ " CASE  WHEN START_TIME = '1900-01-01 00:00:00.000' THEN  	'0' "
					+ " WHEN END_TIME = '1900-01-01 00:00:00.000' THEN  "
							+ " 	CAST(DATEDIFF(SECOND, START_TIME, getdate()) AS VARCHAR)  ELSE "
							+ " 	CAST(DATEDIFF(SECOND, START_TIME, END_TIME) AS VARCHAR)  END DURATION, "
					+ " (SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB = 7039 AND ALPHA_SUB_TAB = T1.POSTED_STATUS)  ETL_STATUS, "
					+ " CASE WHEN T1.POSTING_SEQUENCE =1 THEN 'New Initiate' ELSE 'Re-Initiate' END EXECUTION_TYPE, "
							+ " T1.LOG_FILE_NAME LOG_FILE  FROM 	  RA_TRN_POSTING T1, "
							+ " RA_MST_EXTRACTION_HEADER T2,  RA_MST_EXTRACTION_DETAIL T3  WHERE 	 "
							+ " T1.COUNTRY = T2.COUNTRY  AND T1.LE_BOOK = T2.LE_BOOK "
					+ " AND T1.EXTRACTION_FREQUENCY = T2.EXTRACTION_FREQUENCY "
					+ " AND T1.EXTRACTION_SEQUENCE = T2.EXTRACTION_SEQUENCE "
							+ " AND T3.FEED_ID = T1.EXTRACTION_FEED_ID  AND T1.COUNTRY = T3.COUNTRY "
							+ " AND T1.LE_BOOK = T3.LE_BOOK  AND T1.EXTRACTION_FREQUENCY = T3.EXTRACTION_FREQUENCY "
							+ " AND T1.EXTRACTION_SEQUENCE = T3.EXTRACTION_SEQUENCE AND T1.COUNTRY = "
							+ dObj.getCountry() + " AND T1.LE_BOOK =" + dObj.getLeBook()
							+ " AND T1.EXTRACTION_FREQUENCY IN (" + dObj.getExtractionFrequency()
							+ ")  AND T1.EXTRACTION_SEQUENCE = " + dObj.getExtractionSequence()
							+ " AND T1.BUSINESS_DATE= " + dObj.getBusinessDate() + " ");
		}
		orderBy = "ORDER BY T1.POSTING_SEQUENCE ";
		sqlQuery = sqlQuery + orderBy;

		try {
			logger.info("Executing approved query");
			collTemp = getJdbcTemplate().query(sqlQuery.toString(), getDetailMapper());
			return collTemp;
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("Error: getQueryResults Exception :   ");
			//logger.error(((sqlQuery == null) ? "sqlQuery is Null" : sqlQuery.toString()));
			return null;
		}
	}

	public ExceptionCode killProcess(EtlConsoleDetailVb vObject) {
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			String osType = System.getProperty("os.name");
			if(osType.toUpperCase().contains("WINDOWS"))
				Runtime.getRuntime().exec("taskkill /F /PID " +vObject.getProcessId());
			else
				Runtime.getRuntime().exec("kill -9 "+vObject.getProcessId());
			doUpdatePostingsTerminate(vObject);
			doUpdatePostingsTermHistory(vObject);
			exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
			exceptionCode.setErrorMsg("Process Terminated Successfully[" + vObject.getFeedId() + "]");
			exceptionCode.setOtherInfo(vObject);
		} catch (Exception e) {
			exceptionCode.setErrorMsg(e.getMessage());
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setOtherInfo(vObject);
		}
		return exceptionCode;
	}

	public int doUpdatePostingsTerminate(EtlConsoleDetailVb vObject) {
		setServiceDefaults();
		String query = "";
		String busDate = "";
		if ("ORACLE".equalsIgnoreCase(databaseType)) {
			busDate = "BUSINESS_DATE = TO_DATE('" + vObject.getBusinessDate() + "','DD-Mon-RRRR')";
		} else if ("MSSQL".equalsIgnoreCase(databaseType)) {
			busDate = "BUSINESS_DATE = CONVERT(varchar,'" + vObject.getBusinessDate() + "',103)";
		}
		try {
			query = " Update RA_TRN_POSTING SET POSTED_STATUS = ?,TERMINATED_USER='" + intCurrentUserId + "', "
					+ " END_TIME = " + getDbFunction("SYSDATE") + " WHERE COUNTRY = ? AND LE_BOOK = ? "
					+ " AND EXTRACTION_FREQUENCY = ? AND EXTRACTION_SEQUENCE = ? AND EXTRACTION_FEED_ID = ? "
					+ " AND POSTING_SEQUENCE = ? AND " + busDate + " ";

			Object[] args = { vObject.getEtlStatusId(), vObject.getCountry(), vObject.getLeBook(),
					vObject.getFrequency(),
					vObject.getExtractionSequence(), vObject.getFeedId(), vObject.getPostingSequence() };
			return getJdbcTemplate().update(query, args);
		} catch (Exception e) {
			return 0;
		}
	}

	public int doUpdatePostingsTermHistory(EtlConsoleDetailVb vObject) {
		setServiceDefaults();
		String query = "";
		String busDate = "";
		if ("ORACLE".equalsIgnoreCase(databaseType)) {
			busDate = "BUSINESS_DATE = TO_DATE('" + vObject.getBusinessDate() + "','DD-Mon-RRRR')";
		} else if ("MSSQL".equalsIgnoreCase(databaseType)) {
			busDate = "BUSINESS_DATE = CONVERT(varchar,'" + vObject.getBusinessDate() + "',103)";
		}
		try {
			query = " Update RA_TRN_POSTING_HISTORY SET POSTED_STATUS = ?,TERMINATED_USER='" + intCurrentUserId
					+ "',END_TIME = " + getDbFunction("SYSDATE") + " WHERE COUNTRY = ? AND LE_BOOK = ? "
					+ " AND EXTRACTION_FREQUENCY = ? AND EXTRACTION_SEQUENCE = ? AND EXTRACTION_FEED_ID = ? "
					+ " AND POSTING_SEQUENCE = ?  AND " + busDate + "  ";
			Object[] args = { vObject.getEtlStatusId(), vObject.getCountry(), vObject.getLeBook(),
					vObject.getFrequency(),
					vObject.getExtractionSequence(), vObject.getFeedId(), vObject.getPostingSequence() };
			return getJdbcTemplate().update(query, args);
		} catch (Exception e) {
			return 0;
		}
	}

	public List<EtlPostingsHeaderVb> getEtlConsoleHeaderList(EtlPostingsHeaderVb vObject) {
		List<EtlPostingsHeaderVb> collTemp = null;
		String query = "";
		try {
			if ("ORACLE".equals(databaseType)) {
				query = " SELECT COUNTRY,LE_BOOK,EXTRACTION_SEQUENCE,EXTRACTION_FREQUENCY, "
					+ " (SELECT EXTRACTION_DESCRIPTION FROM RA_MST_EXTRACTION_HEADER WHERE COUNTRY = T1.COUNTRY AND LE_BOOK= T1.LE_BOOK "
					+ " AND EXTRACTION_FREQUENCY=T1.EXTRACTION_FREQUENCY AND EXTRACTION_SEQUENCE= T1.EXTRACTION_SEQUENCE)  EXTRACTION_SEQUENCE_DESC, "
					+ " (SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB = 7015 AND ALPHA_SUB_TAB= EXTRACTION_FREQUENCY) EXTRACTION_FREQUENCY_DESC,"
					+ " TO_CHAR(BUSINESS_DATE,'DD-Mon-RRRR') BUSINESS_DATE,EXTRACTION_ENGINE,"
					+ " (SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB = 7035 AND ALPHA_SUB_TAB= EXTRACTION_ENGINE) EXTRACTION_ENGINE_DESC, "
						+ " TO_CHAR(START_TIME,'HH24:MI:SS') START_TIME,"
						+ " TO_CHAR(END_TIME,'HH24:MI:SS') END_TIME,NODE,CASE WHEN SUBMIT_TYPE='S' THEN 'Schedule' Else 'Manual' END SUBMIT_TYPE,"
					+ " SUBMITTER_ID,POSTING_STATUS,"
					+ " (SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB = 7039 AND ALPHA_SUB_TAB= POSTING_STATUS) POSTING_STATUS_DESC,"
						+ " TO_CHAR(DATE_CREATION, 'dd-Mon-yyyy HH24:MI:SS') DATE_CREATION FROM RA_TRN_POSTING_HEADER T1	 ";
			} else if ("MSSQL".equals(databaseType)) {
				query = " SELECT COUNTRY,LE_BOOK,EXTRACTION_SEQUENCE,EXTRACTION_FREQUENCY, "
						+ " (SELECT EXTRACTION_DESCRIPTION FROM RA_MST_EXTRACTION_HEADER WHERE COUNTRY = T1.COUNTRY AND LE_BOOK= T1.LE_BOOK "
						+ " AND EXTRACTION_FREQUENCY=T1.EXTRACTION_FREQUENCY AND EXTRACTION_SEQUENCE= T1.EXTRACTION_SEQUENCE)  EXTRACTION_SEQUENCE_DESC, "
						+ " (SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB = 7015 AND ALPHA_SUB_TAB= EXTRACTION_FREQUENCY) EXTRACTION_FREQUENCY_DESC,"
						+ " FORMAT(BUSINESS_DATE,'dd-MMM-yyyy') BUSINESS_DATE,EXTRACTION_ENGINE,"
						+ " (SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB = 7035 AND ALPHA_SUB_TAB= EXTRACTION_ENGINE) EXTRACTION_ENGINE_DESC, "
						+ " FORMAT(START_TIME,'HH:mm:ss') START_TIME,"
						+ " FORMAT(END_TIME,'HH:mm:ss') END_TIME,NODE,CASE WHEN SUBMIT_TYPE='S' THEN 'Schedule' Else 'Manual' END SUBMIT_TYPE,"
						+ " SUBMITTER_ID,POSTING_STATUS,"
						+ " (SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB = 7039 AND ALPHA_SUB_TAB= POSTING_STATUS) POSTING_STATUS_DESC,"
						+ " FORMAT(DATE_CREATION, 'dd-MMM-yyyy HH:mm:ss') DATE_CREATION FROM RA_TRN_POSTING_HEADER T1	 ";
			}
			String orderBy = "ORDER BY BUSINESS_DATE DESC,EXTRACTION_SEQUENCE ASC,EXTRACTION_FREQUENCY";
			query = query + orderBy;
			collTemp = getJdbcTemplate().query(query.toString(), getHeaderMapper());
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("Exception while getting Etl console Header");
		}
		return collTemp;
	}

	public int etlHeaderStatusUpdate(EtlConsoleDetailVb vObject) {
		String query = "";
		int retVal = 0;
		try {
			query = "UPDATE RA_TRN_POSTING_HEADER SET POSTING_STATUS = ? WHERE COUNTRY = ? AND LE_BOOK = ?"
					+ " AND EXTRACTION_SEQUENCE = ?  AND EXTRACTION_FREQUENCY = ? ";

			String query1 = "UPDATE RA_TRN_POSTING_HISTORY_HEADER SET POSTING_STATUS = ? WHERE COUNTRY = ? AND LE_BOOK = ?"
					+ " AND EXTRACTION_SEQUENCE = ?  AND EXTRACTION_FREQUENCY = ? ";

			Object[] args = { vObject.getEtlStatusId(), vObject.getCountry(), vObject.getLeBook(),
					vObject.getExtractionSequence(), vObject.getFrequency() };

			retVal = getJdbcTemplate().update(query, args);

			retVal = getJdbcTemplate().update(query1, args);
			return retVal;
		} catch (Exception e) {
			return retVal;
		}
	}

	public int etlDetailStatusUpdate(EtlConsoleDetailVb vObject) {
		String query = "";
		int retVal = 0;
			if (ValidationUtil.isValid(vObject.getPostingSequence())) {
				query = " Update RA_TRN_POSTING SET POSTED_STATUS = ?  WHERE COUNTRY = ? AND LE_BOOK = ? "
					+ " AND EXTRACTION_FREQUENCY = ? AND EXTRACTION_SEQUENCE = ?   AND POSTING_SEQUENCE = '"
						+ vObject.getPostingSequence() + "'  ";

			} else {
				query = " Update RA_TRN_POSTING SET POSTED_STATUS = ?  WHERE COUNTRY = ? AND LE_BOOK = ? "
						+ " AND EXTRACTION_FREQUENCY = ? AND EXTRACTION_SEQUENCE = ?  ";
			}
			Object[] args = { vObject.getEtlStatusId(), vObject.getCountry(), vObject.getLeBook(),
					vObject.getFrequency(), vObject.getExtractionSequence() };
		return getJdbcTemplate().update(query, args);
	}

	public int countExistforDetail(EtlConsoleDetailVb dObj) {
		int cnt = 0;
		String query = "";
		try {
			query = " SELECT COUNT(POSTING_SEQUENCE) FROM  RA_TRN_POSTING_HISTORY WHERE COUNTRY = ? AND LE_BOOK = ? AND EXTRACTION_FREQUENCY = ? AND EXTRACTION_SEQUENCE = ? ";

			Object objParams[] = new Object[4];
			objParams[0] = new String(dObj.getCountry());
			objParams[1] = new String(dObj.getLeBook());
			objParams[2] = new String(dObj.getFrequency());
			objParams[3] = new String(dObj.getExtractionSequence());
			cnt = getJdbcTemplate().queryForObject(query, objParams, Integer.class);
			return cnt;
		} catch (Exception e) {
			e.printStackTrace();
			return cnt;
		}
	}

	public int doUpdatePostingsDetails(EtlConsoleDetailVb vObject) {
		String query = "";
		if ("ORACLE".equalsIgnoreCase(databaseType)) {
			query = " Update RA_TRN_POSTING SET POSTED_STATUS = ?  WHERE COUNTRY = ? AND LE_BOOK = ? "
					+ " AND EXTRACTION_FREQUENCY = ? AND EXTRACTION_SEQUENCE = ? "
					+ " AND BUSINESS_DATE =TO_DATE(?,'RRRR-MM-DD') ";
		} else {
			query = " Update RA_TRN_POSTING SET POSTED_STATUS = ?  WHERE COUNTRY = ? AND LE_BOOK = ? "
					+ " AND EXTRACTION_FREQUENCY = ? AND EXTRACTION_SEQUENCE = ?   AND BUSINESS_DATE = ?";
		}

		Object[] args = { "P", vObject.getCountry(), vObject.getLeBook(), vObject.getFrequency(),
				vObject.getExtractionSequence(), vObject.getBusinessDate() };
		return getJdbcTemplate().update(query, args);
	}

	public List<EtlPostingsVb> getDetailQueryList(EtlConsoleDetailVb vObject) {
		List<EtlPostingsVb> collTemp = null;
		String query = "";
		try {
			if ("ORACLE".equalsIgnoreCase(databaseType)) {
				query = " SELECT COUNTRY,LE_BOOK,EXTRACTION_SEQUENCE,EXTRACTION_FREQUENCY, "
					+ " TO_CHAR(BUSINESS_DATE,'DD-Mon-RRRR') BUSINESS_DATE,EXTRACTION_FEED_ID,"
					+ "POSTING_SEQUENCE, EXTRACTION_ENGINE,EXRTACTION_TYPE,EVENT_NAME,DEBUG_MODE,"
					+ "DEPENDENT_FLAG,START_TIME, END_TIME,POSTED_STATUS,POSTED_BY,LOG_FILE_NAME"
					+ " FROM RA_TRN_POSTING_HISTORY WHERE COUNTRY = ? AND LE_BOOK = ? AND "
					+ "	EXTRACTION_SEQUENCE = ? AND EXTRACTION_FREQUENCY = ? AND BUSINESS_DATE = ?";
			} else if ("MSSQL".equalsIgnoreCase(databaseType)) {
				query = " SELECT COUNTRY,LE_BOOK,EXTRACTION_SEQUENCE,EXTRACTION_FREQUENCY, "
						+ " FORMAT(BUSINESS_DATE,'dd-MMM-yyyy') BUSINESS_DATE,EXTRACTION_FEED_ID,"
						+ "POSTING_SEQUENCE, EXTRACTION_ENGINE,EXRTACTION_TYPE,EVENT_NAME,DEBUG_MODE,"
						+ "DEPENDENT_FLAG,START_TIME, END_TIME,POSTED_STATUS,POSTED_BY,LOG_FILE_NAME"
						+ " FROM RA_TRN_POSTING_HISTORY WHERE COUNTRY = ? AND LE_BOOK = ? AND "
						+ "	EXTRACTION_SEQUENCE = ? AND EXTRACTION_FREQUENCY = ? AND BUSINESS_DATE = ?";
			}

			Object objParams[] = new Object[5];
			objParams[0] = new String(vObject.getCountry());
			objParams[1] = new String(vObject.getLeBook());
			objParams[2] = new String(vObject.getFrequency());
			objParams[3] = new String(vObject.getExtractionSequence());
			objParams[4] = new String(vObject.getBusinessDate());

			collTemp = getJdbcTemplate().query(query.toString(), getDetailRestartMapper());
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("Exception while getting Etl console Header");
		}
		return collTemp;
	}

	@Override
	protected void setServiceDefaults() {
		serviceName = "EtlConsole";
		serviceDesc = "Etl Console";
		tableName = "RA_TRN_POSTING_HEADER";
		childTableName = "RA_TRN_POSTING_HEADER";
		intCurrentUserId = CustomContextHolder.getContext().getVisionId();
	}

	public int getDetailCnt(EtlConsoleDetailVb dObj) {
		String query = "";
		int cnt;
		query = "SELECT COUNT(1) FROM RA_TRN_POSTING WHERE COUNTRY = ? AND LE_BOOK = ? AND "
				+ "EXTRACTION_SEQUENCE = ? AND EXTRACTION_FREQUENCY = ? AND BUSINESS_DATE = ?";
		Object objParams[] = new Object[5];
		objParams[0] = new String(dObj.getCountry());
		objParams[1] = new String(dObj.getLeBook());
		objParams[2] = new String(dObj.getExtractionSequence());
		objParams[3] = new String(dObj.getFrequency());
		objParams[4] = new String(dObj.getBusinessDate());
		cnt = getJdbcTemplate().queryForObject(query, objParams, Integer.class);
		return cnt;
	}

	public int deletePostingHeader(EtlConsoleDetailVb vObject) {
		String query = "";
		try {
			query = "Delete from RA_TRN_POSTING_HEADER where COUNTRY = ? AND LE_BOOK = ? AND EXTRACTION_FREQUENCY= ? AND EXTRACTION_SEQUENCE = ? "
					+ " AND BUSINESS_DATE = ?";
		Object[] args = { vObject.getCountry(), vObject.getLeBook(), vObject.getFrequency(),
					vObject.getExtractionSequence(), vObject.getBusinessDate() };
		return getJdbcTemplate().update(query, args);
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}

	}

	public int deletePosting(EtlConsoleDetailVb vObject) {
		String query = "";
		try {
			if (ValidationUtil.isValid(vObject.getPostingSequence())) {
				query = "Delete from RA_TRN_POSTING where COUNTRY = ? AND LE_BOOK = ? AND EXTRACTION_FREQUENCY= ? AND EXTRACTION_SEQUENCE = ? "
						+ " AND POSTING_SEQUENCE = '" + vObject.getPostingSequence() + "' AND BUSINESS_DATE = ?";
			} else {
				query = "Delete from RA_TRN_POSTING where COUNTRY = ? AND LE_BOOK = ? AND EXTRACTION_FREQUENCY= ? AND EXTRACTION_SEQUENCE = ? AND BUSINESS_DATE = ?";
			}
			Object[] args = { vObject.getCountry(), vObject.getLeBook(), vObject.getFrequency(),
					vObject.getExtractionSequence(), vObject.getBusinessDate() };
			return getJdbcTemplate().update(query, args);
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}
}
