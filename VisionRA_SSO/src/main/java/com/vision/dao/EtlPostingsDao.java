package com.vision.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.UncategorizedSQLException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.vision.authentication.CustomContextHolder;
import com.vision.exception.ExceptionCode;
import com.vision.exception.RuntimeCustomException;
import com.vision.util.Constants;
import com.vision.util.ValidationUtil;
import com.vision.vb.EtlPostingsHeaderVb;
import com.vision.vb.EtlPostingsVb;
@Component
public class EtlPostingsDao extends AbstractDao<EtlPostingsVb> {
	@Value("${app.databaseType}")
	private String databaseType;
	@Value("${app.clientName}")
	private String clientName;
	@Override
	protected RowMapper getMapper(){
		RowMapper mapper = new RowMapper() {
			@Override
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				EtlPostingsVb vObject = new EtlPostingsVb();
				vObject.setPostingDate(rs.getString("POSTING_DATE"));
				vObject.setBusinessDate(rs.getString("BUSINESS_DATE"));
				vObject.setPostingType(rs.getString("POSTING_TYPE"));
				vObject.setPostedBy(rs.getString("POSTED_USER_ID"));
				vObject.setCountry(rs.getString("COUNTRY"));
				vObject.setLeBook(rs.getString("LE_BOOK"));
				vObject.setExtractionFrequency(rs.getString("EXTRACTION_FREQUENCY"));
				vObject.setExtractionFrequencyDesc(rs.getString("EXTRACTION_FREQUENCY_DESC"));
				vObject.setDependentBuildSeq(rs.getString("DEPENDENT_BUILD_SEQ"));
				vObject.setExtractionSequence(rs.getString("EXTRACTION_SEQUENCE"));
				vObject.setExtractionDescription(rs.getString("EXTRACTION_DESCRIPTION"));
				vObject.setFeedCategory(rs.getString("FEED_CATEGORY"));
				vObject.setFeedCategoryDesc(rs.getString("FEED_CATEGORY_DESC"));
				vObject.setExtractionFeedId(rs.getString("EXTRACTION_FEED_ID"));
				vObject.setExtractionFeedIdDesc(rs.getString("EXTRACTION_FEED_ID_DESC"));
				vObject.setDebugMode(rs.getString("DEBUG_MODE"));
				vObject.setDependentFlag(rs.getString("DEPENDENT_FLAG"));
				vObject.setDependentFeedId(rs.getString("DEPENDENT_FEED_ID"));
				vObject.setPostingSequence(rs.getString("POSTING_SEQUENCE"));				
				vObject.setExtractionEngine(rs.getString("EXTRACTION_ENGINE"));
				vObject.setExtractionEngineDesc(rs.getString("EXTRACTION_ENGINE_DESC"));
				vObject.setExrtactionType(rs.getString("EXTRACTION_TYPE"));
				vObject.setExrtactionTypeDesc(rs.getString("EXTRACTION_TYPE_DESC"));
				vObject.setScheduleType(rs.getString("SCHEDULE_TYPE"));
				vObject.setScheduleTypeDesc(rs.getString("SCHEDULE_TYPE_DESC"));
				vObject.setScheduleTime(rs.getString("SCHEDULE_TIME"));
				vObject.setEventName(rs.getString("EVENT_NAME"));
				vObject.setEventNameDesc(rs.getString("EVENT_NAME_DESC"));
				return vObject;
			}
		};
		return mapper;
	}
	@Override
	protected void setServiceDefaults() {
		serviceName = "EtlPosting";
		serviceDesc = "Etl Posting";
		tableName = "RA_TRN_POSTING";
		childTableName = "RA_TRN_POSTING";
		intCurrentUserId = CustomContextHolder.getContext().getVisionId();
	}

	@Override
	public List<EtlPostingsVb> getQueryPopupResults(EtlPostingsVb dObj) {
		Vector<Object> params = new Vector<Object>();
		StringBuffer reInitiate = null;
		StringBuffer newPosting = null;
		String orderBy = "";
		final int intKeyFieldsCount = 3;
		List<EtlPostingsVb> collTemp = null;
		boolean flag = !"NCBA".equalsIgnoreCase(clientName);
		String programDesc = flag?
				"SELECT PROGRAM_DESCRIPTION FROM PROGRAMS  WHERE PROGRAM = FEED_ID ":"SELECT PROGRAM_DESCRIPTION FROM RA_PROGRAMS P1 WHERE P1.PROGRAM = FEED_ID AND P1.COUNTRY = T1.COUNTRY AND P1.LE_BOOK = T1.LE_BOOK";
		if ("ORACLE".equalsIgnoreCase(databaseType)) {
			reInitiate = new StringBuffer("SELECT "+
					" TO_CHAR(SYSDATE,'DD-Mon-RRRR HH24:MI:SS') POSTING_DATE, ?  BUSINESS_DATE ,'R' POSTING_TYPE, ? POSTED_USER_ID,"+
					" T1.COUNTRY,T1.LE_BOOK,T1.EXTRACTION_FREQUENCY,CASE WHEN T1.EXTRACTION_FREQUENCY= 'D' THEN 'Daily' ELSE 'Monthly' END EXTRACTION_FREQUENCY_DESC,"+
					" T1.DEPENDENT_BUILD_SEQ," +
					" T1.EXTRACTION_SEQUENCE,"+
					" T1.EXTRACTION_DESCRIPTION,T2.FEED_CATEGORY,"+
					" (SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB = FEED_CATEGORY_AT AND ALPHA_SUB_TAB = FEED_CATEGORY) FEED_CATEGORY_DESC,"+
					" T2.FEED_ID EXTRACTION_FEED_ID,"+
					" CASE WHEN T1.EXTRACTION_ENGINE IN ('BLD','RCY','MST') THEN ("+programDesc+") "+
					" WHEN T1.EXTRACTION_ENGINE IN ('CHG','REC','TAX','TREC','TRCV','TRCB','RCB', 'RCV','CONC') THEN (SELECT  BUSINESS_LINE_DESCRIPTION FROM RA_MST_BUSINESS_LINE_HEADER WHERE "
					+ " COUNTRY = T1.COUNTRY AND LE_BOOK = T1.LE_BOOK AND BUSINESS_LINE_ID = FEED_ID) "+
					//" WHEN T1.EXTRACTION_ENGINE = 'REC' THEN (SELECT TRANS_LINE_ID+' - '+TRANS_LINE_DESCRIPTION FROM RA_MST_TRANS_LINE_HEADER WHERE TRANS_LINE_ID = FEED_ID) "+
					" ELSE ("+programDesc+") END EXTRACTION_FEED_ID_DESC, "
					+
					" T3.DEBUG_MODE,T3.DEPENDENT_FLAG,T3.DEPENDENT_FEED_ID,	" +
					// " T3.POSTING_SEQUENCE+1 POSTING_SEQUENCE,"
					" T2.FEED_SEQUENCE POSTING_SEQUENCE,T1.EXTRACTION_ENGINE," +
					" (SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB = T1.EXTRACTION_ENGINE_AT AND ALPHA_SUB_TAB = T1.EXTRACTION_ENGINE) EXTRACTION_ENGINE_DESC,"+
					" T1.EXTRACTION_TYPE,CASE WHEN T1.EXTRACTION_TYPE = 'M' THEN 'Manual' ELSE 'Schedule' END EXTRACTION_TYPE_DESC,"+
					" T1.SCHEDULE_TYPE,CASE WHEN T1.SCHEDULE_TYPE = 'T' THEN 'Time' ELSE 'Event' END SCHEDULE_TYPE_DESC,"+
					" TO_CHAR(T1.SCHEDULE_TIME,'HH24:MI:SS') SCHEDULE_TIME,"+
					" T1.EVENT_NAME, (SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB = T1.EVENT_NAME_AT AND ALPHA_SUB_TAB = T1.EVENT_NAME) EVENT_NAME_DESC"+
					" FROM "+
					" RA_MST_EXTRACTION_HEADER T1, "+
					" RA_MST_EXTRACTION_DETAIL T2, "+
					" RA_TRN_POSTING T3,RA_TRN_POSTING_HEADER T4 " +
					" WHERE "+
					" T1.COUNTRY = T2.COUNTRY "+
					" AND T1.LE_BOOK = T2.LE_BOOK "+
					" AND T1.EXTRACTION_FREQUENCY = T2.EXTRACTION_FREQUENCY "+
					" AND T1.EXTRACTION_SEQUENCE = T2.EXTRACTION_SEQUENCE "+
					" AND T2.FEED_ID = T3.EXTRACTION_FEED_ID " +
					" AND T1.COUNTRY = T3.COUNTRY "+
					" AND T1.LE_BOOK = T3.LE_BOOK "+
					" AND T1.EXTRACTION_FREQUENCY = T3.EXTRACTION_FREQUENCY "+
					" AND T1.EXTRACTION_SEQUENCE = T3.EXTRACTION_SEQUENCE "+
					" AND T3.BUSINESS_DATE = ? "+
					" AND T3.POSTED_STATUS IN ('C','E','T') AND T1.COUNTRY IN (?)"
					+ " AND T1.LE_BOOK IN (?) " + " AND T1.EXTRACTION_FREQUENCY IN (?)"
				    + " AND T1.EXTRACTION_SEQUENCE IN (?)"
					+" AND T1.EXTRACTION_STATUS = 0  "
					+ "AND T2.EXTRACTION_STATUS = 0 "
					+ "AND T1.COUNTRY = T4.COUNTRY " + " AND T1.LE_BOOK = T4.LE_BOOK "
					+ " AND T1.EXTRACTION_FREQUENCY = T4.EXTRACTION_FREQUENCY "
					+ " AND T1.EXTRACTION_SEQUENCE = T4.EXTRACTION_SEQUENCE "
					+ " AND T4.BUSINESS_DATE = T3.BUSINESS_DATE " + " AND T4.POSTING_STATUS IN ('C','E','T') ");
			
			
			newPosting = new StringBuffer("SELECT	"+			
					" TO_CHAR(SYSDATE,'DD-Mon-RRRR HH24:MI:SS') POSTING_DATE, ? BUSINESS_DATE,'I' POSTING_TYPE,? POSTED_USER_ID,		"+		
					" T1.COUNTRY,T1.LE_BOOK,T1.EXTRACTION_FREQUENCY,CASE WHEN T1.EXTRACTION_FREQUENCY= 'D' THEN 'Daily' ELSE 'Monthly' END EXTRACTION_FREQUENCY_DESC,"+
					" T1.DEPENDENT_BUILD_SEQ," +
					" T1.EXTRACTION_SEQUENCE,	"+			
					" EXTRACTION_DESCRIPTION,FEED_CATEGORY,"+
					" (SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB = FEED_CATEGORY_AT AND ALPHA_SUB_TAB = FEED_CATEGORY) FEED_CATEGORY_DESC,"+
					" FEED_ID EXTRACTION_FEED_ID,"+
					" CASE WHEN T1.EXTRACTION_ENGINE IN ('BLD','RCY','MST') THEN ("+programDesc+") "+
					" WHEN T1.EXTRACTION_ENGINE IN ('CHG','REC','TAX','TREC','TRCV','TRCB','RCB', 'RCV','CONC') THEN (SELECT  BUSINESS_LINE_DESCRIPTION FROM RA_MST_BUSINESS_LINE_HEADER WHERE "
					+ " COUNTRY = T1.COUNTRY AND LE_BOOK = T1.LE_BOOK AND BUSINESS_LINE_ID = FEED_ID) "+
					" ELSE ("+programDesc+") END EXTRACTION_FEED_ID_DESC, "
					+
					" DEBUG_MODE,DEPENDENT_FLAG,DEPENDENT_FEED_ID,		"+		
					//" '"+1+"' POSTING_SEQUENCE,"+
					" T2.FEED_SEQUENCE POSTING_SEQUENCE,"+
					" EXTRACTION_ENGINE,"
					+ " (SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB = EXTRACTION_ENGINE_AT AND ALPHA_SUB_TAB = EXTRACTION_ENGINE) EXTRACTION_ENGINE_DESC,"
					+
					" EXTRACTION_TYPE,CASE WHEN EXTRACTION_TYPE = 'M' THEN 'Manual' ELSE 'Schedule' END EXTRACTION_TYPE_DESC,"+
					" SCHEDULE_TYPE,CASE WHEN SCHEDULE_TYPE = 'T' THEN 'Time' ELSE 'Event' END SCHEDULE_TYPE_DESC,"+
					" TO_CHAR(SCHEDULE_TIME,'HH24:MI:SS') SCHEDULE_TIME,"+				
					" EVENT_NAME, (SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB = T1.EVENT_NAME_AT AND ALPHA_SUB_TAB = T1.EVENT_NAME) EVENT_NAME_DESC				"+
					" FROM				"+
					" RA_MST_EXTRACTION_HEADER T1,"+				
					" RA_MST_EXTRACTION_DETAIL T2		"+		
					" WHERE				"+
					" T1.COUNTRY = T2.COUNTRY		"+		
					" AND T1.LE_BOOK = T2.LE_BOOK		"+		
					" AND T1.EXTRACTION_FREQUENCY = T2.EXTRACTION_FREQUENCY"+				
					" AND T1.EXTRACTION_SEQUENCE = T2.EXTRACTION_SEQUENCE		"+	
					" AND T1.COUNTRY IN (?) AND T1.LE_BOOK IN (?) "
					+ " AND T1.EXTRACTION_FREQUENCY IN (?) "
					+ " AND T1.EXTRACTION_SEQUENCE IN (?)" +
					" AND T1.EXTRACTION_STATUS = 0 "+
					" AND T2.EXTRACTION_STATUS = 0 "+
					" AND NOT EXISTS (SELECT 'X' FROM RA_TRN_POSTING B 	" + 
					" WHERE B.COUNTRY = T1.COUNTRY  "+
					" AND B.LE_BOOK = T1.LE_BOOK  "+
					" AND B.EXTRACTION_FREQUENCY = T1.EXTRACTION_FREQUENCY "+
					" AND B.EXTRACTION_SEQUENCE = T1.EXTRACTION_SEQUENCE AND B.EXTRACTION_FEED_ID = T2.FEED_ID "+
					" AND B.BUSINESS_DATE = ? "+
					" AND B.COUNTRY IN (?) AND B.LE_BOOK IN (?) "
					+ " AND B.EXTRACTION_FREQUENCY IN (?) "
					+ " AND B.EXTRACTION_SEQUENCE IN (?))");
			
		}else if("MSSQL".equalsIgnoreCase(databaseType)) {
			reInitiate = new StringBuffer("SELECT "+
					" FORMAT(cast(getdate() as datetime), 'dd-MMM-yyyy HH:mm:ss') POSTING_DATE,FORMAT(cast( ? as date), 'dd-MMM-yyyy') BUSINESS_DATE,'R' POSTING_TYPE,? POSTED_USER_ID,"+
					" T1.COUNTRY,T1.LE_BOOK,T1.EXTRACTION_FREQUENCY,CASE WHEN T1.EXTRACTION_FREQUENCY= 'D' THEN 'Daily' ELSE 'Monthly' END EXTRACTION_FREQUENCY_DESC,"+
					" T1.DEPENDENT_BUILD_SEQ," +
					" T1.EXTRACTION_SEQUENCE,"+
					" T1.EXTRACTION_DESCRIPTION,T2.FEED_CATEGORY,"+
					" (SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB = FEED_CATEGORY_AT AND ALPHA_SUB_TAB = FEED_CATEGORY) FEED_CATEGORY_DESC,"+
					" T2.FEED_ID EXTRACTION_FEED_ID,"+
					" CASE WHEN T1.EXTRACTION_ENGINE IN ('BLD','RCY') THEN ("+programDesc+") "+
					" WHEN T1.EXTRACTION_ENGINE IN ('CHG','REC','TAX','TREC','TRCV','TRCB','RCB', 'RCV','CONC') THEN (SELECT BUSINESS_LINE_DESCRIPTION FROM RA_MST_BUSINESS_LINE_HEADER WHERE "
					+ " COUNTRY = T1.COUNTRY AND LE_BOOK = T1.LE_BOOK AND BUSINESS_LINE_ID = FEED_ID) "+
					//" WHEN T1.EXTRACTION_ENGINE = 'REC' THEN (SELECT TRANS_LINE_ID+' - '+TRANS_LINE_DESCRIPTION FROM RA_MST_TRANS_LINE_HEADER WHERE TRANS_LINE_ID = FEED_ID) "+
					" ELSE ("+programDesc+") END EXTRACTION_FEED_ID_DESC, "
					+
					" T3.DEBUG_MODE,T3.DEPENDENT_FLAG,T3.DEPENDENT_FEED_ID," +
					// " T3.POSTING_SEQUENCE+1 POSTING_SEQUENCE,
					"  T2.FEED_SEQUENCE POSTING_SEQUENCE,T1.EXTRACTION_ENGINE," +
					" (SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB = T1.EXTRACTION_ENGINE_AT AND ALPHA_SUB_TAB = T1.EXTRACTION_ENGINE) EXTRACTION_ENGINE_DESC,"+
					" T1.EXTRACTION_TYPE,CASE WHEN T1.EXTRACTION_TYPE = 'M' THEN 'Manual' ELSE 'Schedule' END EXTRACTION_TYPE_DESC,"+
					" T1.SCHEDULE_TYPE,CASE WHEN T1.SCHEDULE_TYPE = 'T' THEN 'Time' ELSE 'Event' END SCHEDULE_TYPE_DESC,"+
					" CONVERT(varchar, T1.SCHEDULE_TIME, 108) SCHEDULE_TIME,"+
					" T1.EVENT_NAME, (SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB = T1.EVENT_NAME_AT AND ALPHA_SUB_TAB = T1.EVENT_NAME) EVENT_NAME_DESC"+
					" FROM "+
					" RA_MST_EXTRACTION_HEADER T1, "+
					" RA_MST_EXTRACTION_DETAIL T2, "+
					" RA_TRN_POSTING T3,RA_TRN_POSTING_HEADER T4 " +
					" WHERE "+
					" T1.COUNTRY = T2.COUNTRY "+
					" AND T1.LE_BOOK = T2.LE_BOOK "+
					" AND T1.EXTRACTION_FREQUENCY = T2.EXTRACTION_FREQUENCY "+
					" AND T1.EXTRACTION_SEQUENCE = T2.EXTRACTION_SEQUENCE "+
					" AND T2.FEED_ID = T3.EXTRACTION_FEED_ID " +
					" AND T1.COUNTRY = T3.COUNTRY "+
					" AND T1.LE_BOOK = T3.LE_BOOK "+
					" AND T1.EXTRACTION_FREQUENCY = T3.EXTRACTION_FREQUENCY "+
					" AND T1.EXTRACTION_SEQUENCE = T3.EXTRACTION_SEQUENCE "+
					" AND T3.BUSINESS_DATE =  ? "+
					" AND T3.POSTED_STATUS IN ('C','E','T') AND T1.COUNTRY IN ( ?)"
					+ " AND T1.LE_BOOK IN (?)  AND T1.EXTRACTION_FREQUENCY IN (?)"
					+ " AND T1.EXTRACTION_SEQUENCE IN (?) " +
					" 	AND T1.EXTRACTION_STATUS = 0  "
					+ "	AND T2.EXTRACTION_STATUS =0 "
					+ " AND T1.COUNTRY = T4.COUNTRY " + " AND T1.LE_BOOK = T4.LE_BOOK "
					+ " AND T1.EXTRACTION_FREQUENCY = T4.EXTRACTION_FREQUENCY "
					+ " AND T1.EXTRACTION_SEQUENCE = T4.EXTRACTION_SEQUENCE "
					+ " AND T4.BUSINESS_DATE = T3.BUSINESS_DATE " + " AND T4.POSTING_STATUS IN ('C','E','T') ");
			
			newPosting = new StringBuffer("SELECT	"+			
					" FORMAT(cast(getdate() as datetime), 'dd-MMM-yyyy HH:mm:ss') POSTING_DATE, FORMAT(cast( ?  as date), 'dd-MMM-yyyy') BUSINESS_DATE,'I' POSTING_TYPE, ? POSTED_USER_ID,		"+		
					" T1.COUNTRY,T1.LE_BOOK,T1.EXTRACTION_FREQUENCY,CASE WHEN T1.EXTRACTION_FREQUENCY= 'D' THEN 'Daily' ELSE 'Monthly' END EXTRACTION_FREQUENCY_DESC,"+
					" T1.DEPENDENT_BUILD_SEQ," +
					" T1.EXTRACTION_SEQUENCE,	"+			
					" EXTRACTION_DESCRIPTION,FEED_CATEGORY,"+
					" (SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB = FEED_CATEGORY_AT AND ALPHA_SUB_TAB = FEED_CATEGORY) FEED_CATEGORY_DESC,"+
					" FEED_ID EXTRACTION_FEED_ID,"+
					" CASE WHEN T1.EXTRACTION_ENGINE IN ('BLD','RCY') THEN ("+programDesc+") "+
					" WHEN T1.EXTRACTION_ENGINE IN ('CHG','REC','TAX','TREC','TRCV','TRCB','RCB', 'RCV','CONC') THEN (SELECT BUSINESS_LINE_DESCRIPTION FROM RA_MST_BUSINESS_LINE_HEADER WHERE "
					+ " COUNTRY = T1.COUNTRY AND LE_BOOK = T1.LE_BOOK AND BUSINESS_LINE_ID = FEED_ID) "+
					//" WHEN T1.EXTRACTION_ENGINE = 'REC' THEN (SELECT TRANS_LINE_ID+' - '+TRANS_LINE_DESCRIPTION FROM RA_MST_TRANS_LINE_HEADER WHERE TRANS_LINE_ID = FEED_ID) "+
					" ELSE ("+programDesc+") END EXTRACTION_FEED_ID_DESC, "
					+
					" DEBUG_MODE,DEPENDENT_FLAG,DEPENDENT_FEED_ID,"+
					// " '"+1+"' POSTING_SEQUENCE,
					" T2.FEED_SEQUENCE POSTING_SEQUENCE,EXTRACTION_ENGINE,"
					+
					" (SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB = EXTRACTION_ENGINE_AT AND ALPHA_SUB_TAB = EXTRACTION_ENGINE) EXTRACTION_ENGINE_DESC,"
					+
					" EXTRACTION_TYPE,CASE WHEN EXTRACTION_TYPE = 'M' THEN 'Manual' ELSE 'Schedule' END EXTRACTION_TYPE_DESC,"+
					" SCHEDULE_TYPE,CASE WHEN SCHEDULE_TYPE = 'T' THEN 'Time' ELSE 'Event' END SCHEDULE_TYPE_DESC,"
					+ " CONVERT(varchar,SCHEDULE_TIME, 108) SCHEDULE_TIME," +
					" EVENT_NAME, (SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB = T1.EVENT_NAME_AT AND ALPHA_SUB_TAB = T1.EVENT_NAME) EVENT_NAME_DESC				"+
					" FROM				"+
					" RA_MST_EXTRACTION_HEADER T1,"+				
					" RA_MST_EXTRACTION_DETAIL T2		" + 
					" WHERE				"+
					" T1.COUNTRY = T2.COUNTRY		"+		
					" AND T1.LE_BOOK = T2.LE_BOOK		"+		
					" AND T1.EXTRACTION_FREQUENCY = T2.EXTRACTION_FREQUENCY"+				
					" AND T1.EXTRACTION_SEQUENCE = T2.EXTRACTION_SEQUENCE		"+	
					" AND T1.COUNTRY IN (?) AND T1.LE_BOOK IN (?) "
					+ " AND T1.EXTRACTION_FREQUENCY IN (?) "
					+ " AND T1.EXTRACTION_SEQUENCE IN (?)" +
					" AND T1.EXTRACTION_STATUS = 0 "+
					" AND T2.EXTRACTION_STATUS = 0 "+
					" AND NOT EXISTS (SELECT 'X' FROM RA_TRN_POSTING B 	"+			
					" WHERE B.COUNTRY = T1.COUNTRY  "+
					" AND B.LE_BOOK = T1.LE_BOOK  "+
					" AND B.EXTRACTION_FREQUENCY = T1.EXTRACTION_FREQUENCY "+
					" AND B.EXTRACTION_SEQUENCE = T1.EXTRACTION_SEQUENCE AND B.EXTRACTION_FEED_ID = T2.FEED_ID "+
					" AND B.BUSINESS_DATE = ?"+
					" AND B.COUNTRY IN (?) AND B.LE_BOOK IN (?) "
					+ " AND B.EXTRACTION_FREQUENCY IN (?) "
					+ " AND B.EXTRACTION_SEQUENCE IN (?))");
		}
		Object[] args = {dObj.getBusinessDate(),dObj.getPostedBy(),dObj.getBusinessDate(),dObj.getCountry(),dObj.getLeBook()
				,dObj.getExtractionFrequency(),dObj.getExtractionSequence()};
		Object[] args1 = {dObj.getBusinessDate(),dObj.getPostedBy(),dObj.getCountry(),dObj.getLeBook(), dObj.getExtractionFrequency(),
				 dObj.getExtractionSequence(),dObj.getBusinessDate(),dObj.getCountry(),dObj.getLeBook(),dObj.getExtractionFrequency(),dObj.getExtractionSequence()};
		try
		{
			
			orderBy = " Order by COUNTRY,LE_BOOK,EXTRACTION_FREQUENCY,EXTRACTION_SEQUENCE,POSTING_SEQUENCE,EXTRACTION_DESCRIPTION,FEED_CATEGORY,EXTRACTION_FEED_ID ";
			if("I".equalsIgnoreCase(dObj.getPostingType())) {
				collTemp = getJdbcTemplate().query(newPosting.toString()+" "+orderBy,args1,getMapper());
			}else {
				collTemp = getJdbcTemplate().query(reInitiate.toString()+" "+orderBy,args,getMapper());
			}
			return collTemp;
		}catch(Exception ex){
			ex.printStackTrace();
			////logger.error(((newPosting==null)? "strBufApprove is Null":newPosting.toString()));
			/*if (params != null)
				for(int i=0 ; i< params.size(); i++)
					//logger.error("objParams[" + i + "]" + params.get(i).toString());
*/			return null;
		}
}
	@Override
	@Transactional(rollbackForClassName={"com.vision.exception.RuntimeCustomException"})
	public ExceptionCode doInsertApprRecord(List<EtlPostingsVb> vObjects) throws RuntimeCustomException {
		ExceptionCode exceptionCode = new ExceptionCode();
		strErrorDesc  = "";
		strCurrentOperation = Constants.ADD;
		strApproveOperation = Constants.ADD;
		if (!"S".equalsIgnoreCase(vObjects.get(0).getExrtactionType())) {
			setServiceDefaults();
		}
		try {
			EtlPostingsVb ObjDetailVb = vObjects.get(0); 
			EtlPostingsHeaderVb etlPostingHeaderVb = new EtlPostingsHeaderVb();
			etlPostingHeaderVb.setCountry(ObjDetailVb.getCountry());
			etlPostingHeaderVb.setLeBook(ObjDetailVb.getLeBook());
			etlPostingHeaderVb.setExtractionSequence(Integer.parseInt(ObjDetailVb.getExtractionSequence()));
			etlPostingHeaderVb.setExtractionFrequency(ObjDetailVb.getExtractionFrequency());
			etlPostingHeaderVb.setExtractionEngine(ObjDetailVb.getExtractionEngine());
			etlPostingHeaderVb.setBusinessDate(ObjDetailVb.getBusinessDate());
			etlPostingHeaderVb.setSubmitterId(ObjDetailVb.getPostedBy());
			etlPostingHeaderVb.setSubmitType(ObjDetailVb.getExrtactionType());
			if ("Y".equalsIgnoreCase(ObjDetailVb.getDependentFlagChkBox())
					|| "S".equalsIgnoreCase(etlPostingHeaderVb.getSubmitType()))
				etlPostingHeaderVb.setDependentSequence(getDependentSequence(etlPostingHeaderVb));
			else
				etlPostingHeaderVb.setDependentSequence(0);
			if ("R".equalsIgnoreCase(ObjDetailVb.getPostingType())) {
				retVal = etlDetailStatusUpdate("P", ObjDetailVb.getCountry(), ObjDetailVb.getLeBook(),
						ObjDetailVb.getExtractionSequence(), ObjDetailVb.getExtractionFrequency(),
						ObjDetailVb.getPostingSequence(), ObjDetailVb.getExrtactionType(),
						etlPostingHeaderVb.getBusinessDate(), "A");
				if (retVal != Constants.SUCCESSFUL_OPERATION) {
					exceptionCode = getResultObject(retVal);
					throw buildRuntimeCustomException(exceptionCode);
				}
				retVal = etlHeaderStatusUpdate("P", ObjDetailVb.getCountry(), ObjDetailVb.getLeBook(),
						ObjDetailVb.getExtractionSequence(), ObjDetailVb.getExtractionFrequency(),
						ObjDetailVb.getExrtactionType(), etlPostingHeaderVb.getBusinessDate());
				if (retVal != Constants.SUCCESSFUL_OPERATION) {
					exceptionCode = getResultObject(retVal);
					throw buildRuntimeCustomException(exceptionCode);
				}
				retVal = etlHistoryHeaderStatusUpdate("P", ObjDetailVb.getCountry(), ObjDetailVb.getLeBook(),
						ObjDetailVb.getExtractionSequence(), ObjDetailVb.getExtractionFrequency(),
						ObjDetailVb.getExrtactionType(), etlPostingHeaderVb.getBusinessDate());
				exceptionCode = getResultObject(Constants.SUCCESSFUL_OPERATION);
				return exceptionCode;
			}
			if (countExistsPostingHeaderinProgress(etlPostingHeaderVb) > 0) {
				exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
				exceptionCode
						.setErrorMsg("The Major Build is in-Progress.Cannot Post a New build under same Major Build..");
				return exceptionCode;
			}
			for(EtlPostingsVb vObject : vObjects){
				retVal = doInsertionPosting(vObject);
				if (retVal != Constants.SUCCESSFUL_OPERATION){
					exceptionCode = getResultObject(retVal);
					throw buildRuntimeCustomException(exceptionCode);
				} else {
					retVal = doInsertionPostingHistory(vObject);
					if (retVal != Constants.SUCCESSFUL_OPERATION){
						exceptionCode = getResultObject(retVal);
						throw buildRuntimeCustomException(exceptionCode);
					} 
				}
			}
			if (countExistsPostingHeader(etlPostingHeaderVb) == 0) {
				retVal = doInsertionPostingHeader(etlPostingHeaderVb);
				if (retVal != Constants.SUCCESSFUL_OPERATION) {
					exceptionCode = getResultObject(retVal);
					throw buildRuntimeCustomException(exceptionCode);
				}
			} else {
				retVal = updateExistsPostingHeader(etlPostingHeaderVb);
				if (retVal != Constants.SUCCESSFUL_OPERATION) {
					exceptionCode = getResultObject(retVal);
					throw buildRuntimeCustomException(exceptionCode);
				}
			}
			retVal = doInsertionPostingHistoryHeader(etlPostingHeaderVb);
			exceptionCode = getResultObject(Constants.SUCCESSFUL_OPERATION);
			return exceptionCode;
		}catch (RuntimeCustomException rcException) {
			throw rcException;
		}catch (UncategorizedSQLException uSQLEcxception) {
			strErrorDesc = parseErrorMsg(uSQLEcxception);
			exceptionCode = getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION);
			throw buildRuntimeCustomException(exceptionCode);
		}catch(Exception ex){
			logger.error("Error in Add.",ex);
			strErrorDesc = ex.getMessage();
			exceptionCode = getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION);
			throw buildRuntimeCustomException(exceptionCode);
		}
	}
	
	protected int doInsertionPostingHistory(EtlPostingsVb vObject){
		if(!ValidationUtil.isValid(vObject.getPostedStatus())) {
			vObject.setPostedStatus("P");
		}
		String postingDate = "";

		if ("ORACLE".equalsIgnoreCase(databaseType)) {
			postingDate = " SYSDATE ";
		} else if ("MSSQL".equalsIgnoreCase(databaseType)) {
			postingDate = "GetDate()";
		}

		String query = " Insert Into RA_TRN_POSTING_HISTORY(POSTING_TYPE_AT,POSTING_TYPE,POSTING_DATE, BUSINESS_DATE,"
				+ "COUNTRY,LE_BOOK,EXTRACTION_FREQUENCY,EXTRACTION_SEQUENCE,EXTRACTION_FEED_ID,POSTING_SEQUENCE,"
				+ "EXTRACTION_ENGINE_AT,EXTRACTION_ENGINE, EXRTACTION_TYPE,"
				+ "START_TIME,END_TIME,POSTED_STATUS,POSTED_BY,POSTED_TIME,DEBUG_MODE,DEPENDENT_FLAG,DEPENDENT_FEED_ID,LOG_FILE_NAME) "
				+ " Values (?,?," + postingDate + ",?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

		Object[] args = {vObject.getPostingTypeAt(),vObject.getPostingType(),vObject.getBusinessDate(),
				vObject.getCountry(),vObject.getLeBook(),vObject.getExtractionFrequency(),vObject.getExtractionSequence(),
				vObject.getExtractionFeedId(),vObject.getPostingSequence(),
				vObject.getExtractionEngineAt(), vObject.getExtractionEngine(), vObject.getExrtactionType(),
				vObject.getStartTime(), vObject.getEndTime(), vObject.getPostedStatus(), vObject.getPostedBy(),
				vObject.getPostedTime(), vObject.getDebugMode(),
				vObject.getDependentFlag(), vObject.getDependentFeedId(), vObject.getLogFileName() };
		return getJdbcTemplate().update(query,args);
	}

	protected int doInsertionPosting(EtlPostingsVb vObject){
		if(!ValidationUtil.isValid(vObject.getPostedStatus())) {
			vObject.setPostedStatus("P");
		}
		String postingDate = "";

		if ("ORACLE".equalsIgnoreCase(databaseType)) {
			postingDate = " SYSDATE ";
		} else if ("MSSQL".equalsIgnoreCase(databaseType)) {
			postingDate = "GetDate()";
		}

		String query =  " Insert Into RA_TRN_POSTING(POSTING_TYPE_AT,POSTING_TYPE,POSTING_DATE, BUSINESS_DATE,"
				+ "COUNTRY,LE_BOOK,EXTRACTION_FREQUENCY,EXTRACTION_SEQUENCE,EXTRACTION_FEED_ID,POSTING_SEQUENCE,"
				+ "EXTRACTION_ENGINE_AT,EXTRACTION_ENGINE, EXRTACTION_TYPE,"
				+ "START_TIME,END_TIME,POSTED_STATUS,POSTED_BY,POSTED_TIME,DEBUG_MODE,DEPENDENT_FLAG,DEPENDENT_FEED_ID,LOG_FILE_NAME) "
				+ " Values (?,?," + postingDate + ",?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		
		Object[] args = {vObject.getPostingTypeAt(),vObject.getPostingType(),vObject.getBusinessDate(),
				vObject.getCountry(),vObject.getLeBook(),vObject.getExtractionFrequency(),vObject.getExtractionSequence(),
				vObject.getExtractionFeedId(),vObject.getPostingSequence(),
				vObject.getExtractionEngineAt(), vObject.getExtractionEngine(), vObject.getExrtactionType(),
				vObject.getStartTime(), vObject.getEndTime(), vObject.getPostedStatus(),
				vObject.getPostedBy(),vObject.getPostedTime(),vObject.getDebugMode(),
				vObject.getDependentFlag(), vObject.getDependentFeedId(), vObject.getLogFileName() };
		return getJdbcTemplate().update(query,args);
	}
	protected int doInsertionPostingHeader(EtlPostingsHeaderVb vObject){
		if(!ValidationUtil.isValid(vObject.getPostedStatus())) {
			vObject.setPostedStatus("P");
		}
		String date = "";
		if("ORACLE".equalsIgnoreCase(databaseType)) {
			date = "SYSDATE";
		}else if("MSSQL".equalsIgnoreCase(databaseType)) {
			date = "GetDate()";
		}
		if (!"S".equalsIgnoreCase(vObject.getSubmitType()))
			setServiceDefaults();
		else
			intCurrentUserId = 9999;
		String query =  " Insert Into RA_TRN_POSTING_HEADER(BUSINESS_DATE,"
				+ "COUNTRY,LE_BOOK,EXTRACTION_FREQUENCY,EXTRACTION_SEQUENCE,DEPENDENT_SEQUENCE,"
				+ "EXTRACTION_ENGINE, SUBMIT_TYPE,SUBMITTER_ID,MAKER,VERIFIER,DATE_LAST_MODIFIED,DATE_CREATION) "
				+
				" Values (?,?,?,?,?,?,?,?,?,?,?,"+date+","+date+")";
		
		Object[] args = {vObject.getBusinessDate(),
				vObject.getCountry(),vObject.getLeBook(),vObject.getExtractionFrequency(),vObject.getExtractionSequence(),
				vObject.getDependentSequence(),vObject.getExtractionEngine(),vObject.getSubmitType(),vObject.getSubmitterId(),
				intCurrentUserId,intCurrentUserId};
		return getJdbcTemplate().update(query,args);
	}

	private int countExistsPostingHeader(EtlPostingsHeaderVb vObject) {
		try {
			String query = "SELECT COUNT(1) FROM RA_TRN_POSTING_HEADER WHERE BUSINESS_DATE= ? AND COUNTRY =? AND LE_BOOK = ?"
					+ " AND EXTRACTION_FREQUENCY = ? AND EXTRACTION_SEQUENCE = ? ";
			Object[] args = { vObject.getBusinessDate(), vObject.getCountry(), vObject.getLeBook(),
					vObject.getExtractionFrequency(), vObject.getExtractionSequence() };

			return getJdbcTemplate().queryForObject(query, args, Integer.class);
		} catch (Exception e) {
			return 0;
		}
	}

	private int countExistsPostingHeaderinProgress(EtlPostingsHeaderVb vObject) {
		try {
			String query = "SELECT COUNT(1) FROM RA_TRN_POSTING_HEADER WHERE BUSINESS_DATE= ? AND COUNTRY =? AND LE_BOOK = ?"
					+ " AND EXTRACTION_FREQUENCY = ? AND EXTRACTION_SEQUENCE = ? AND POSTING_STATUS='I' ";
			Object[] args = { vObject.getBusinessDate(), vObject.getCountry(), vObject.getLeBook(),
					vObject.getExtractionFrequency(), vObject.getExtractionSequence() };

			return getJdbcTemplate().queryForObject(query, args, Integer.class);
		} catch (Exception e) {
			return 0;
		}
	}

	private int updateExistsPostingHeader(EtlPostingsHeaderVb vObject) {
		try {
			String query = " UPDATE RA_TRN_POSTING_HEADER SET POSTING_STATUS = 'P',START_TIME='',END_TIME='' WHERE BUSINESS_DATE= ? AND COUNTRY =? AND LE_BOOK = ?"
					+ " AND EXTRACTION_FREQUENCY = ? AND EXTRACTION_SEQUENCE = ? ";
			Object[] args = { vObject.getBusinessDate(), vObject.getCountry(), vObject.getLeBook(),
					vObject.getExtractionFrequency(), vObject.getExtractionSequence() };

			return getJdbcTemplate().update(query, args);
		} catch (Exception e) {
			return 0;
		}
	}
	protected int doInsertionPostingHistoryHeader(EtlPostingsHeaderVb vObject) {
		if (!ValidationUtil.isValid(vObject.getPostedStatus())) {
			vObject.setPostedStatus("P");
		}
		String date = "";
		if ("ORACLE".equalsIgnoreCase(databaseType)) {
			date = "SYSDATE";
		} else if ("MSSQL".equalsIgnoreCase(databaseType)) {
			date = "GetDate()";
		}
		if (!"S".equalsIgnoreCase(vObject.getSubmitType()))
			setServiceDefaults();
		else
			intCurrentUserId = 9999;
		String query = " Insert Into RA_TRN_POSTING_HISTORY_HEADER(BUSINESS_DATE,"
				+ "COUNTRY,LE_BOOK,EXTRACTION_FREQUENCY,EXTRACTION_SEQUENCE,DEPENDENT_SEQUENCE,"
				+ "EXTRACTION_ENGINE, SUBMIT_TYPE,SUBMITTER_ID,MAKER,VERIFIER,DATE_LAST_MODIFIED,DATE_CREATION) "
				+ " Values (?,?,?,?,?,?,?,?,?,?,?," + date + "," + date + ")";

		Object[] args = { vObject.getBusinessDate(), vObject.getCountry(), vObject.getLeBook(),
				vObject.getExtractionFrequency(), vObject.getExtractionSequence(), vObject.getDependentSequence(),
				vObject.getExtractionEngine(), vObject.getSubmitType(), vObject.getSubmitterId(), intCurrentUserId,
				intCurrentUserId };
		return getJdbcTemplate().update(query, args);
	}
	public int etlHeaderStatusUpdate(String etlStatusId, String country, String leBook, String extSeq, String extFreq,
			String extractionType, String busDate) {

		if (!"S".equalsIgnoreCase(extractionType))
			setServiceDefaults();
		else
			intCurrentUserId = 9999;
		String query = "";
		int retVal = 0;
		try {
			query = "UPDATE RA_TRN_POSTING_HEADER SET POSTING_STATUS = ?,SUBMITTER_ID ="+intCurrentUserId+", "+ 
					" MAKER = " + intCurrentUserId
					+ ",DATE_LAST_MODIFIED = " + getDbFunction("SYSDATE")
					+ " ,START_TIME='' , END_TIME = '' WHERE COUNTRY = ? AND LE_BOOK = ? "//,SUBMIT_TYPE = ?
					+ " AND EXTRACTION_SEQUENCE = ?  AND EXTRACTION_FREQUENCY = ? AND  BUSINESS_DATE = ?";

			Object[] args = { etlStatusId, country, leBook, extSeq, extFreq, busDate };
			retVal = getJdbcTemplate().update(query, args);
			return retVal;
		} catch (Exception e) {
			return retVal;
		}
	}

	public int etlHistoryHeaderStatusUpdate(String etlStatusId, String country, String leBook, String extSeq,
			String extFreq, String extractionType, String busDate) {

		if (!"S".equalsIgnoreCase(extractionType))
			setServiceDefaults();
		else
			intCurrentUserId = 9999;
		String query = "";
		int retVal = 0;
		try {
			String query1 = "UPDATE RA_TRN_POSTING_HISTORY_HEADER SET POSTING_STATUS = ?,SUBMITTER_ID ="
					+ intCurrentUserId + ", " + " MAKER = " + intCurrentUserId + ",DATE_LAST_MODIFIED = "
					+ getDbFunction("SYSDATE")
					+ " ,START_TIME='' , END_TIME = '' WHERE COUNTRY = ? AND LE_BOOK = ? "//,SUBMIT_TYPE = ?
					+ " AND EXTRACTION_SEQUENCE = ?  AND EXTRACTION_FREQUENCY = ? AND  BUSINESS_DATE = ?";
			Object[] args = { etlStatusId, country, leBook, extSeq, extFreq, busDate };
			retVal = getJdbcTemplate().update(query1, args);
			return retVal;
		} catch (Exception e) {
			return retVal;
		}
	}

	public int etlDetailStatusUpdate(String etlStatusId, String country, String leBook, String extSeq, String extFreq,
			String postingSeq, String extractionType, String busDate, String restartProcess) {
		if (!"S".equalsIgnoreCase(extractionType))
			setServiceDefaults();
		else
			intCurrentUserId = 9999;
		String query = "";
		String resartStatus = "";
		if (ValidationUtil.isValid(restartProcess)
				&& ("E".equalsIgnoreCase(restartProcess) || "T".equalsIgnoreCase(restartProcess))) {
			resartStatus = "AND POSTED_STATUS <> 'C' ";
		}
		if (ValidationUtil.isValid(postingSeq)) {
			query = " Update RA_TRN_POSTING SET POSTED_STATUS = ?,POSTED_BY = " + intCurrentUserId
					+ " , POSTING_DATE = " + getDbFunction("SYSDATE")
					+ ",START_TIME='' , END_TIME = ''" //,EXRTACTION_TYPE = ? 
					+ " WHERE COUNTRY = ? AND LE_BOOK = ? "
					+ " AND EXTRACTION_FREQUENCY = ? AND EXTRACTION_SEQUENCE = ?  AND POSTING_SEQUENCE = '" + postingSeq
					+ "' " + " AND BUSINESS_DATE = ? " + resartStatus + "";

		} else {
			query = " Update RA_TRN_POSTING SET POSTED_STATUS = ?,POSTED_BY = " + intCurrentUserId
					+ " , POSTING_DATE = " + getDbFunction("SYSDATE")
					+ ",START_TIME='' , END_TIME = ''   "//,EXRTACTION_TYPE = ?
					+ " WHERE COUNTRY = ? AND LE_BOOK = ? "
					+ " AND EXTRACTION_FREQUENCY = ? AND EXTRACTION_SEQUENCE = ? AND BUSINESS_DATE = ? " 
					+ " " + resartStatus + " ";
		}
		Object[] args = { etlStatusId, country, leBook, extFreq, extSeq, busDate };
		return getJdbcTemplate().update(query, args);
	}

	public int getDependentSequence(EtlPostingsHeaderVb vObject) {
		try {
			String query = "";
			query = " SELECT " + getDbFunction("NVL")
					+ "(DEPENDENT_BUILD_SEQ,0) FROM RA_MST_EXTRACTION_HEADER where COUNTRY = ? AND LE_BOOK =? "
					+ " AND EXTRACTION_FREQUENCY = ? AND EXTRACTION_SEQUENCE = ? ";

			Object[] args = { vObject.getCountry(), vObject.getLeBook(), vObject.getExtractionFrequency(),
					vObject.getExtractionSequence() };
			return getJdbcTemplate().queryForObject(query, args,Integer.class);
		} catch (Exception e) {
			return 0;
		}

	}

	public int getCountErrored(String country, String leBook, String extSeq, String extFreq, String busDate) {
		try {
			String query = " SELECT COUNT(1) FROM RA_TRN_POSTING " + " WHERE COUNTRY = ? AND LE_BOOK = ? "
					+ " AND EXTRACTION_FREQUENCY = ? AND EXTRACTION_SEQUENCE = ? AND BUSINESS_DATE = ? AND POSTED_STATUS <> 'C' ";

			Object[] args = { country, leBook, extFreq, extSeq, busDate };
			return getJdbcTemplate().queryForObject(query, args, Integer.class);
		} catch (Exception e) {
			return 0;
		}
	}
}
