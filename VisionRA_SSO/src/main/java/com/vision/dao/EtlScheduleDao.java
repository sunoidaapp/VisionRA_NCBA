package com.vision.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.vision.authentication.CustomContextHolder;
import com.vision.exception.ExceptionCode;
import com.vision.exception.RuntimeCustomException;
import com.vision.util.Constants;
import com.vision.util.ValidationUtil;
import com.vision.vb.EtlManagerVb;
import com.vision.vb.EtlScheduleVb;
import com.vision.vb.TransLineSbuVb;

@Component
public class EtlScheduleDao extends AbstractDao<EtlScheduleVb> {
	@Autowired
	EtlManagerDetailsDao etlManagerDetailsDao;
	
	@Autowired
	CommonDao commonDao;
	
	@Value("${app.databaseType}")
	private String databaseType;
	
	@Override
	protected RowMapper getMapper(){
		RowMapper mapper = new RowMapper() {
			@Override
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				EtlScheduleVb vObject = new EtlScheduleVb();
				vObject.setCountry(rs.getString("COUNTRY"));
				vObject.setLeBook(rs.getString("LE_BOOK"));
				vObject.setExtractionFrequency(rs.getString("EXTRACTION_FREQUENCY"));
				vObject.setExtractionSequence(rs.getString("EXTRACTION_SEQUENCE"));
				vObject.setExtractionDescription(rs.getString("EXTRACTION_DESCRIPTION"));
				vObject.setExtractionEngine(rs.getString("EXTRACTION_ENGINE"));
				vObject.setScheduleType(rs.getString("SCHEDULE_TYPE"));
				vObject.setScheduleFrequency(rs.getString("SCHEDULE_FREQUENCY"));
				vObject.setScheduleStartDate(rs.getString("SCHEDULE_START_DATE"));
				vObject.setScheduleEndDate(rs.getString("SCHEDULE_END_DATE"));
				vObject.setScheduleTime(rs.getString("SCHEDULE_TIME"));
				vObject.setLastScheduleDate(rs.getString("LAST_SCHEDULE_DATE"));
				vObject.setNextScheduleDate(rs.getString("NEXT_SCHEDULE_DATE"));
				vObject.setLastRunStatus(rs.getString("LAST_RUN_STATUS"));
				vObject.setLastRunStatusDesc(rs.getString("LAST_RUN_STATUS_DESC"));
				vObject.setEventNameAt(rs.getInt("EVENT_ID_AT"));
				vObject.setEventName(rs.getString("EVENT_ID"));
				vObject.setScheduleStatusNt(rs.getInt("SCHEDULE_STATUS_NT"));
				vObject.setScheduleStatus(rs.getInt("SCHEDULE_STATUS"));
				vObject.setNode(rs.getString("NODE"));
				return vObject;
			}
		};
		return mapper;
	}
	@Override
	protected void setServiceDefaults() {
		serviceName = "ETL Schedule";
		serviceDesc = "ETL Schedule";
		tableName = "RA_ETL_SCHEDULE";
		childTableName = "RA_ETL_SCHEDULE";
		intCurrentUserId = CustomContextHolder.getContext().getVisionId();
	}

	
	public List<EtlScheduleVb> getQueryResults(EtlScheduleVb dObj){
		List<EtlScheduleVb> collTemp = null;
		final int intKeyFieldsCount = 4;
		setServiceDefaults();
		String strQueryAppr = null;
		String strQueryPend = null;
		String scheduleTime = "";
		if("ORACLE".equalsIgnoreCase(databaseType)) {
			scheduleTime = "TO_CHAR(APPR.SCHEDULE_TIME,'HH24:MI:SS') SCHEDULE_TIME";
		} else if ("MSSQL".equalsIgnoreCase(databaseType)) {
			scheduleTime = "convert(varchar,APPR.SCHEDULE_TIME,108) SCHEDULE_TIME";
		}
		strQueryAppr = new String("SELECT COUNTRY,LE_BOOK,EXTRACTION_FREQUENCY,EXTRACTION_SEQUENCE,EXTRACTION_DESCRIPTION, "
						+ " EXTRACTION_ENGINE_AT,EXTRACTION_ENGINE,SCHEDULE_TYPE,EVENT_ID_AT,EVENT_ID,SCHEDULE_FREQUENCY, "
						+ " " + getDbFunction("DATEFUNC") + "(SCHEDULE_START_DATE,'"
						+ getDbFunction("DD_Mon_RRRR") + "') SCHEDULE_START_DATE, "
						+ " " + getDbFunction("DATEFUNC") + "(SCHEDULE_END_DATE,'"
						+ getDbFunction("DD_Mon_RRRR")
						+ "') SCHEDULE_END_DATE, " + scheduleTime + ","
						//+ "TO_CHAR(APPR.SCHEDULE_TIME,'HH24:MI:SS') SCHEDULE_TIME, "
						+ getDbFunction("DATEFUNC") + "(NEXT_SCHEDULE_DATE,'"
						+ getDbFunction("DD_Mon_RRRR") + "') NEXT_SCHEDULE_DATE," + " "
						+ getDbFunction("DATEFUNC") + "(LAST_SCHEDULE_DATE,'"
						+ getDbFunction("DD_Mon_RRRR") + "') LAST_SCHEDULE_DATE,"
						+ " LAST_RUN_STATUS,"
						+ "(SELECT ALPHA_SUBTAB_DESCRIPTION       " + "      FROM ALPHA_SUB_TAB                  "
						+ "        WHERE ALPHA_TAB = '7039' AND ALPHA_SUB_TAB = APPR.LAST_RUN_STATUS)  LAST_RUN_STATUS_DESC, "
						+ " NODE,SCHEDULE_STATUS_NT,SCHEDULE_STATUS,RECORD_INDICATOR_NT,RECORD_INDICATOR, "
						+ " MAKER,VERIFIER,DATE_LAST_MODIFIED,DATE_CREATION FROM RA_ETL_SCHEDULE APPR WHERE APPR.COUNTRY = ? AND "
						+ " APPR.LE_BOOK = ?  AND APPR.EXTRACTION_FREQUENCY= ? AND APPR.EXTRACTION_SEQUENCE = ?");
		
		Object objParams[] = new Object[intKeyFieldsCount];
		objParams[0] = new String(dObj.getCountry());// country
		objParams[1] = new String(dObj.getLeBook());
		objParams[2] = new String(dObj.getExtractionFrequency());
		objParams[3] = new String(dObj.getExtractionSequence());
		try {
			logger.info("Executing approved query");
			collTemp = getJdbcTemplate().query(strQueryAppr.toString(),objParams,getMapper());
			return collTemp;
		}catch(Exception ex){
			ex.printStackTrace();
			logger.error("Error: getQueryResults Exception :   ");
				//logger.error(((strQueryAppr == null) ? "strQueryAppr is Null" : strQueryAppr.toString()));
			/*if (objParams != null)
				for(int i=0 ; i< objParams.length; i++)
					//logger.error("objParams[" + i + "]" + objParams[i].toString());
*/			return null;
		}
	}
	public int addEtlSchedule(EtlScheduleVb vObject) {
		String scheduleTime = "";
		String scheduleTimeQuery = "";
		String scheduleVal = "";
		String nextScheduleDate = "";
		
		if(!ValidationUtil.isValid( vObject.getScheduleTime())) {
			vObject.setScheduleTime("00:00:00");
		}

		if("ORACLE".equalsIgnoreCase(databaseType)) {
			nextScheduleDate = "To_Date('" + vObject.getNextScheduleDate() + " " + vObject.getScheduleTime() 
					+ "','DD/MM/YYYY HH24:MI:SS')";
			scheduleTimeQuery = "To_Date(?,'DD/MM/YYYY HH24:MI:SS')";
			if(ValidationUtil.isValid(vObject.getScheduleStartDate()))
				scheduleTime = vObject.getScheduleStartDate()+" "+ vObject.getScheduleTime();
			else 
				scheduleTime=  "";
			if (ValidationUtil.isValid(scheduleTime)) {
				scheduleTimeQuery = "To_Date(?,'DD-MM-YYYY HH24:MI:SS')";
				scheduleVal = scheduleTime;
			} else {
				scheduleTimeQuery = "?";
				scheduleVal = "";
			}
		}else if("MSSQL".equalsIgnoreCase(databaseType)) {
			if ("E".equalsIgnoreCase(vObject.getScheduleType())) {
				vObject.setScheduleStartDate(null);
				vObject.setScheduleEndDate(null);
				vObject.setScheduleTime("00:00:00");
				vObject.setLastScheduleDate(null);
			}
			// nextScheduleDate = " '" + vObject.getNextScheduleDate() + " " +
			// vObject.getScheduleTime() + "' ";
			
			nextScheduleDate = "CONVERT(datetime, '" + vObject.getNextScheduleDate() + " " + vObject.getScheduleTime()
					+ "', 103)";
			scheduleTime = vObject.getScheduleTime();
			scheduleTimeQuery = "?";
			scheduleVal = scheduleTime;
			if (!ValidationUtil.isValid(vObject.getLastScheduleDate())) {
				vObject.setLastScheduleDate(null);
			}
			if(!ValidationUtil.isValid(vObject.getEventName())) {
				vObject.setEventName("NA");
			}
			/*
			 * if(ValidationUtil.isValid(scheduleTime)) scheduleTimeQuery = "?"; else{
			 * scheduleTimeQuery = ""; }
			 */
		}
		String query =  " Insert Into RA_ETL_SCHEDULE(COUNTRY,"+
				" LE_BOOK,EXTRACTION_FREQUENCY,EXTRACTION_SEQUENCE,EXTRACTION_DESCRIPTION,EXTRACTION_ENGINE,"+
				" SCHEDULE_TYPE,EVENT_ID,SCHEDULE_FREQUENCY,SCHEDULE_START_DATE,SCHEDULE_END_DATE,SCHEDULE_TIME,NEXT_SCHEDULE_DATE,"+
				" LAST_SCHEDULE_DATE,LAST_RUN_STATUS,NODE,SCHEDULE_STATUS_NT,SCHEDULE_STATUS,"+
				" RECORD_INDICATOR_NT,RECORD_INDICATOR,MAKER,VERIFIER,DATE_CREATION,DATE_LAST_MODIFIED) "+
				" Values (?,?,?,?,?,?,?,?,?,?,?," + scheduleTimeQuery + "," + nextScheduleDate + ",?,?,?,?,?,?,?,?,?,"
				+ getDbFunction("SYSDATE") + "," + getDbFunction("SYSDATE") + ")";
		
		Object[] args = {vObject.getCountry(),vObject.getLeBook(),vObject.getExtractionFrequency(),vObject.getExtractionSequence(),
				vObject.getExtractionDescription(),vObject.getExtractionEngine(),vObject.getScheduleType(),vObject.getEventName(),
				vObject.getScheduleFrequency(), vObject.getScheduleStartDate(), vObject.getScheduleEndDate(),
				scheduleVal,
				vObject.getLastScheduleDate(),
				vObject.getLastRunStatus(), vObject.getNode(),
				vObject.getScheduleStatusNt(),vObject.getScheduleStatus(),vObject.getRecordIndicatorNt(),vObject.getRecordIndicator(),
				vObject.getMaker(),vObject.getVerifier()};
		
		return getJdbcTemplate().update(query,args);
	}
	public int deleteEtlSchedule(EtlScheduleVb vObject) {
		String query =  " DELETE FROM RA_ETL_SCHEDULE WHERE COUNTRY = ? AND LE_BOOK = ? AND EXTRACTION_FREQUENCY = ? AND EXTRACTION_SEQUENCE = ? ";
		Object[] args = {vObject.getCountry(),vObject.getLeBook(),vObject.getExtractionFrequency(),vObject.getExtractionSequence()};
		return getJdbcTemplate().update(query,args);
	}
	public int countExistSchedule(EtlScheduleVb dObj){
		int cnt = 0;
		String query= "";
		try {
			query = " SELECT COUNT(1) FROM  RA_ETL_SCHEDULE WHERE COUNTRY = ? AND LE_BOOK = ? AND EXTRACTION_FREQUENCY = ? AND EXTRACTION_SEQUENCE = ? ";
			
			Object objParams[] = new Object[4];
			objParams[0] = new String(dObj.getCountry());
			objParams[1] = new String(dObj.getLeBook());
			objParams[2] = new String(dObj.getExtractionFrequency());
			objParams[3] = new String(dObj.getExtractionSequence());
			cnt = getJdbcTemplate().queryForObject(query, objParams, Integer.class);
			return cnt;
		}catch(Exception e) {
			e.printStackTrace();
			return cnt;
		}
	}
	@Override
	public ExceptionCode doInsertApprRecordForNonTrans(EtlScheduleVb vObject) throws RuntimeCustomException {
		List<EtlManagerVb> collTemp = null;
		TransLineSbuVb transLineSbuVb = new TransLineSbuVb();
		ExceptionCode exceptionCode = null;
		strCurrentOperation = Constants.ADD;
		strApproveOperation =Constants.ADD;		
		setServiceDefaults();
		int existsCnt = countExistSchedule(vObject);
		if (existsCnt > 0) {
			retVal = deleteEtlSchedule(vObject);
		}
		vObject.setRecordIndicator(Constants.STATUS_ZERO);
		vObject.setMaker(intCurrentUserId);
		vObject.setVerifier(intCurrentUserId);
		retVal = addEtlSchedule(vObject);
		if (retVal != Constants.SUCCESSFUL_OPERATION){
			exceptionCode = getResultObject(retVal);
			throw buildRuntimeCustomException(exceptionCode);
		} else {
			exceptionCode = getResultObject(Constants.SUCCESSFUL_OPERATION);
		}
		// updateSubmitTypeEtlSchedule(vObject);
		exceptionCode =getResultObject(Constants.SUCCESSFUL_OPERATION);
		// exceptionCode = writeAuditLog(vObject, null);
		/*if(exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION){
			exceptionCode = getResultObject(Constants.AUDIT_TRAIL_ERROR);
			throw buildRuntimeCustomException(exceptionCode);
		}*/
		return exceptionCode;
	}
	@Override
	public ExceptionCode doDeleteApprRecordForNonTrans(EtlScheduleVb vObject) throws RuntimeCustomException {
		List<EtlScheduleVb> collTemp = null;
		ExceptionCode exceptionCode = null;
		EtlScheduleVb vObjectlocal = null;
		strCurrentOperation = Constants.DELETE;
		strApproveOperation =Constants.DELETE;		
		setServiceDefaults();
		vObject.setMaker(getIntCurrentUserId());
		collTemp = getQueryResults(vObject);
		if (collTemp == null){
			exceptionCode = getResultObject(Constants.ERRONEOUS_OPERATION);
			throw buildRuntimeCustomException(exceptionCode);
		}
		if (collTemp.size() > 0 ){
			retVal = deleteEtlSchedule(vObject);
			if (retVal != Constants.SUCCESSFUL_OPERATION){
				exceptionCode = getResultObject(retVal);
				throw buildRuntimeCustomException(exceptionCode);
			}
		}else{
			exceptionCode = getResultObject(Constants.ATTEMPT_TO_DELETE_UNEXISTING_RECORD);
			throw buildRuntimeCustomException(exceptionCode);
		}
		vObjectlocal = ((ArrayList<EtlScheduleVb>)collTemp).get(0);
		vObject.setDateCreation(vObjectlocal.getDateCreation());
		// exceptionCode = writeAuditLog(vObject, vObjectlocal);
		exceptionCode = getResultObject(Constants.SUCCESSFUL_OPERATION);
		return exceptionCode;
	}

}