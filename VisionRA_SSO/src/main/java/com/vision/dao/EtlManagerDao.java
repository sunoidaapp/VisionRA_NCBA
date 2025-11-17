package com.vision.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.vision.authentication.CustomContextHolder;
import com.vision.exception.ExceptionCode;
import com.vision.exception.RuntimeCustomException;
import com.vision.util.CommonUtils;
import com.vision.util.Constants;
import com.vision.util.ValidationUtil;
import com.vision.vb.EtlManagerVb;
import com.vision.vb.SmartSearchVb;
import com.vision.vb.TransLineSbuVb;
import com.vision.vb.VisionUsersVb;

@Component
public class EtlManagerDao extends AbstractDao<EtlManagerVb> {
	@Autowired
	EtlManagerDetailsDao etlManagerDetailsDao;
	
	@Autowired
	CommonDao commonDao;
	
	@Value("${app.databaseType}")
	private String databaseType;
	
	protected RowMapper getDetailMapper(){
		RowMapper mapper = new RowMapper() {
			@Override
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				EtlManagerVb vObject = new EtlManagerVb();
				vObject.setCountry(rs.getString("COUNTRY"));
				vObject.setLeBook(rs.getString("LE_BOOK"));
				vObject.setExtractionFrequency(rs.getString("EXTRACTION_FREQUENCY"));
				vObject.setExtractionSequence(rs.getString("EXTRACTION_SEQUENCE"));
				vObject.setExtractionDateType(rs.getString("EXTRACTION_DATE_TYPE"));
				vObject.setExtractionDay(rs.getString("EXTRACTION_DAY"));
				vObject.setExtractionEngine(rs.getString("EXTRACTION_ENGINE"));
				vObject.setHolidayFlag(rs.getString("HOLIDAY_FLAG"));
				vObject.setExtractionDescription(rs.getString("EXTRACTION_DESCRIPTION") );
				vObject.setExrtactionType(rs.getString("EXTRACTION_TYPE"));
				vObject.setScheduleType(rs.getString("SCHEDULE_TYPE"));
				vObject.setSchPeriodFrom(rs.getString("SCH_PERIOD_FROM_DT"));
				vObject.setSchPeriodTo(rs.getString("SCH_PERIOD_TO_DT"));
				vObject.setScheduleTime(rs.getString("SCHEDULE_TIME"));
				vObject.setEventName(rs.getString("EVENT_NAME"));
				vObject.setExtractionStartDate(ValidationUtil.isValid(rs.getString("EXTRACTION_START_DATE"))?rs.getString("EXTRACTION_START_DATE"):"");
				vObject.setExtractionStatus(rs.getInt("EXTRACTION_STATUS"));
				vObject.setRecordIndicator(rs.getInt("RECORD_INDICATOR"));
				vObject.setDependentBuildSeq(rs.getString("DEPENDENT_BUILD_SEQ"));
				return vObject;
			}
		};
		return mapper;
	}
	@Override
	protected RowMapper getMapper(){
		RowMapper mapper = new RowMapper() {
			@Override
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				EtlManagerVb vObject = new EtlManagerVb();
				vObject.setCountry(rs.getString("COUNTRY"));
				vObject.setLeBook(rs.getString("LE_BOOK"));
				vObject.setExtractionFrequency(rs.getString("EXTRACTION_FREQUENCY"));
				vObject.setExtractionSequence(rs.getString("EXTRACTION_SEQUENCE"));
				vObject.setExtractionDateType(rs.getString("EXTRACTION_DATE_TYPE"));
				vObject.setExtractionDay(rs.getString("EXTRACTION_DAY"));
				vObject.setExtractionEngine(rs.getString("EXTRACTION_ENGINE"));
				vObject.setExtractionEngineDesc(rs.getString("EXTRACTION_ENGINE_DESC"));
				vObject.setExtractionDescription(rs.getString("EXTRACTION_DESCRIPTION") );
				vObject.setExrtactionType(rs.getString("EXTRACTION_TYPE"));
				vObject.setExtractionStartDate(ValidationUtil.isValid(rs.getString("EXTRACTION_START_DATE"))?rs.getString("EXTRACTION_START_DATE"):"");
				vObject.setExtractionStatus(rs.getInt("EXTRACTION_STATUS"));
				vObject.setExtractionStatusDesc(rs.getString("EXTRACTION_STATUS_DESC"));				
				vObject.setRecordIndicator(rs.getInt("RECORD_INDICATOR"));
				vObject.setRecordIndicatorDesc(rs.getString("RECORD_INDICATOR_Desc"));
				vObject.setLastExecutedDate(ValidationUtil.isValid(rs.getString("LAST_SCHEDULE_ON"))?rs.getString("LAST_SCHEDULE_ON"):"");
				vObject.setLastExecutedStatusDesc(rs.getString("LAST_RUN_STATUS_DESC"));
				vObject.setScheduleStatus(rs.getString("SCHEDULE_STATUS"));
				return vObject;
			}
		};
		return mapper;
	}
	@Override
	protected void setServiceDefaults() {
		serviceName = "EtlManager";
		serviceDesc = "Etl Manager";
		tableName = "RA_MST_EXTRACTION_HEADER";
		childTableName = "RA_MST_EXTRACTION_HEADER";
		intCurrentUserId = CustomContextHolder.getContext().getVisionId();
	}

	@Override
	public List<EtlManagerVb> getQueryPopupResults(EtlManagerVb dObj) {
		Vector<Object> params = new Vector<Object>();
		StringBuffer strBufApprove = null;
		String orderBy = "";
		if ("ORACLE".equalsIgnoreCase(databaseType)) {
			strBufApprove = new StringBuffer(" SELECT * " + " FROM (SELECT APPR.COUNTRY,              "
				+ "       APPR.LE_BOOK,APPR.EXTRACTION_FREQUENCY,        "
				+ "       APPR.EXTRACTION_SEQUENCE, APPR.EXTRACTION_DATE_TYPE,        "
				+ "       CASE   WHEN APPR.EXTRACTION_DAY = '0' " + "          THEN  'Start Day of Month'        "
				+ "          ELSE  (SELECT ALPHA_SUBTAB_DESCRIPTION "
				+ "        FROM ALPHA_SUB_TAB WHERE     ALPHA_TAB = 7025     "
				+ "          AND ALPHA_SUB_TAB = APPR.EXTRACTION_DAY)"
				+ "       END                                                   "
				+ "       EXTRACTION_DAY,                                    "
				+ "       APPR.EXTRACTION_ENGINE,                               "
				+ "       (SELECT ALPHA_SUBTAB_DESCRIPTION                      "
				+ "          FROM ALPHA_SUB_TAB                                 "
				+ "         WHERE ALPHA_TAB = 7035 AND ALPHA_SUB_TAB = APPR.EXTRACTION_ENGINE)"
				+ "          EXTRACTION_ENGINE_DESC,                                          "
				+ "       APPR.EXTRACTION_DESCRIPTION,                                        "
				+ "       APPR.EXTRACTION_TYPE,                                               "
					+ "       TO_CHAR (APPR.EXTRACTION_START_DATE, 'DD-Mon-RRRR')                 "
				+ "          EXTRACTION_START_DATE,                                           "
				+ "       APPR.EXTRACTION_STATUS,                                             "
				+ "       (SELECT NUM_SUBTAB_DESCRIPTION                                      "
				+ "          FROM NUM_SUB_TAB                                                 "
				+ "         WHERE NUM_TAB = 1 AND NUM_SUB_TAB = APPR.EXTRACTION_STATUS)       "
				+ "          EXTRACTION_STATUS_DESC,                                          "
				+ "       APPR.RECORD_INDICATOR,                                              "
				+ "       APPR.MAKER,                                                         "
				+ "       APPR.VERIFIER,                                                      "
				+ "       (SELECT NUM_SUBTAB_DESCRIPTION                                      "
				+ "          FROM NUM_SUB_TAB                                                 "
				+ "         WHERE NUM_TAB = 7 AND NUM_SUB_TAB = APPR.RECORD_INDICATOR)        "
				+ "          RECORD_INDICATOR_DESC,                                           "
				+ "       APPR.DATE_CREATION,                                                 "
				+ "       APPR.DATE_LAST_MODIFIED,                                            "
				+ "       (SELECT NUM_SUBTAB_DESCRIPTION FROM NUM_SUB_TAB WHERE NUM_TAB =1 AND NUM_SUB_TAB = T2.SCHEDULE_STATUS) SCHEDULE_STATUS,"
				+ "       TO_CHAR(T2.LAST_SCHEDULE_DATE,'DD-Mon-RRRR HH24:MI:SS') LAST_SCHEDULE_ON,"
					+ " (SELECT ALPHA_SUBTAB_DESCRIPTION       " + "      FROM ALPHA_SUB_TAB                  "
					+ "        WHERE ALPHA_TAB = '7039' AND ALPHA_SUB_TAB = T2.LAST_RUN_STATUS)  LAST_RUN_STATUS_DESC "
					+ "   FROM RA_MST_EXTRACTION_HEADER APPR, " + "  RA_ETL_SCHEDULE T2      "
				+ "   WHERE APPR.COUNTRY = T2.COUNTRY(+) " + "   AND APPR.LE_BOOK = T2.LE_BOOK(+) "
				+ "   AND APPR.EXTRACTION_FREQUENCY = T2.EXTRACTION_FREQUENCY(+) "
				+ "   AND APPR.EXTRACTION_SEQUENCE = T2.EXTRACTION_SEQUENCE(+) " + "  ) TAPPR ");
		} else if ("MSSQL".equalsIgnoreCase(databaseType)) {
			strBufApprove = new StringBuffer(" SELECT * " + " FROM (SELECT APPR.COUNTRY,              "
					+ "       APPR.LE_BOOK,APPR.EXTRACTION_FREQUENCY,        "
					+ "       APPR.EXTRACTION_SEQUENCE, APPR.EXTRACTION_DATE_TYPE,        "
					+ "       CASE   WHEN APPR.EXTRACTION_DAY = '0' " + "          THEN  'Start Day of Month'        "
					+ "          ELSE  (SELECT ALPHA_SUBTAB_DESCRIPTION "
					+ "        FROM ALPHA_SUB_TAB WHERE     ALPHA_TAB = 7025     "
					+ "          AND ALPHA_SUB_TAB = APPR.EXTRACTION_DAY)"
					+ "       END                                                   "
					+ "       EXTRACTION_DAY,                                    "
					+ "       APPR.EXTRACTION_ENGINE,                               "
					+ "       (SELECT ALPHA_SUBTAB_DESCRIPTION                      "
					+ "          FROM ALPHA_SUB_TAB                                 "
					+ "         WHERE ALPHA_TAB = 7035 AND ALPHA_SUB_TAB = APPR.EXTRACTION_ENGINE)"
					+ "          EXTRACTION_ENGINE_DESC,                                          "
					+ "       APPR.EXTRACTION_DESCRIPTION,                                        "
					+ "       APPR.EXTRACTION_TYPE,                                               "
					+ " FORMAT(APPR.EXTRACTION_START_DATE,'dd-MMM-yyyy')						"
					+ "          EXTRACTION_START_DATE,                                           "
					+ "       APPR.EXTRACTION_STATUS,                                             "
					+ "       (SELECT NUM_SUBTAB_DESCRIPTION                                      "
					+ "          FROM NUM_SUB_TAB                                                 "
					+ "         WHERE NUM_TAB = 1 AND NUM_SUB_TAB = APPR.EXTRACTION_STATUS)       "
					+ "          EXTRACTION_STATUS_DESC,                                          "
					+ "       APPR.RECORD_INDICATOR,                                              "
					+ "       APPR.MAKER,                                                         "
					+ "       APPR.VERIFIER,                                                      "
					+ "       (SELECT NUM_SUBTAB_DESCRIPTION                                      "
					+ "          FROM NUM_SUB_TAB                                                 "
					+ "         WHERE NUM_TAB = 7 AND NUM_SUB_TAB = APPR.RECORD_INDICATOR)        "
					+ "          RECORD_INDICATOR_DESC,                                           "
					+ "       APPR.DATE_CREATION,                                                 "
					+ "       APPR.DATE_LAST_MODIFIED,                                            "
					+ "       (SELECT NUM_SUBTAB_DESCRIPTION FROM NUM_SUB_TAB WHERE NUM_TAB =1 AND NUM_SUB_TAB = T2.SCHEDULE_STATUS) SCHEDULE_STATUS,"
					+ " Format(T2.LAST_SCHEDULE_DATE, 'dd-MMM-yyyy HH:mm:ss') LAST_SCHEDULE_ON,"
					+ " (SELECT ALPHA_SUBTAB_DESCRIPTION       " + "      FROM ALPHA_SUB_TAB                  "
					+ "        WHERE ALPHA_TAB = '7039' AND ALPHA_SUB_TAB = T2.LAST_RUN_STATUS)  LAST_RUN_STATUS_DESC "
					+ "        FROM RA_MST_EXTRACTION_HEADER APPR LEFT OUTER JOIN "
					+ "  RA_ETL_SCHEDULE T2     ON   APPR.COUNTRY = T2.COUNTRY "
					+ "   AND APPR.LE_BOOK = T2.LE_BOOK "
					+ "   AND APPR.EXTRACTION_FREQUENCY = T2.EXTRACTION_FREQUENCY "
					+ "   AND APPR.EXTRACTION_SEQUENCE = T2.EXTRACTION_SEQUENCE ) TAPPR ");
		}
		
		try
		{
			
			if (dObj.getSmartSearchOpt() != null && dObj.getSmartSearchOpt().size() > 0) {
				int count = 1;
				for (SmartSearchVb data: dObj.getSmartSearchOpt()){
					if(count == dObj.getSmartSearchOpt().size()) {
						data.setJoinType("");
					} else {
						if(!ValidationUtil.isValid(data.getJoinType()) && !("AND".equalsIgnoreCase(data.getJoinType()) || "OR".equalsIgnoreCase(data.getJoinType()))) {
							data.setJoinType("AND");
						}
					}
					String val = CommonUtils.criteriaBasedVal(data.getCriteria(), data.getValue());
					switch (data.getObject()) {
					case "country":
						CommonUtils.addToQuerySearch(" upper(TAPPR.COUNTRY) "+ val, strBufApprove, data.getJoinType());
						break;

					case "leBook":
						CommonUtils.addToQuerySearch(" upper(TAPPR.LE_BOOK) "+ val, strBufApprove, data.getJoinType());
						break;

					case "extractionFrequency":
						CommonUtils.addToQuerySearch(" upper(TAPPR.EXTRACTION_FREQUENCY) "+ val, strBufApprove, data.getJoinType());
						break;
						
					case "extractionSequence":
						CommonUtils.addToQuerySearch(" upper(TAPPR.EXTRACTION_SEQUENCE) "+ val, strBufApprove, data.getJoinType());
						break;
						
					case "extractionDateType":
						CommonUtils.addToQuerySearch(" upper(TAPPR.EXTRACTION_DATE_TYPE) "+ val, strBufApprove, data.getJoinType());
						break;
						
					case "extractionDay":
						CommonUtils.addToQuerySearch(" upper(TAPPR.EXTRACTION_DAY) "+ val, strBufApprove, data.getJoinType());
						break;
					
					case "extractionDescription":
						CommonUtils.addToQuerySearch(" upper(TAPPR.EXTRACTION_DESCRIPTION) "+ val, strBufApprove, data.getJoinType());
						break;
						
					case "extractionEngineDesc":
						CommonUtils.addToQuerySearch(" upper(TAPPR.EXRTACTION_ENGINE_DESC) "+ val, strBufApprove, data.getJoinType());
						break;
						
					case "exrtactionType":
						CommonUtils.addToQuerySearch(" upper(TAPPR.EXTRACTION_TYPE) "+ val, strBufApprove, data.getJoinType());
						break;
						
					case "extractionStatusDesc":
						CommonUtils.addToQuerySearch(" upper(TAPPR.EXTRACTION_STATUS_DESC) "+ val, strBufApprove, data.getJoinType());
						break;
						
					case "recordIndicatorDesc":
						CommonUtils.addToQuerySearch(" upper(TAPPR.RECORD_INDICATOR_DESC) "+ val, strBufApprove, data.getJoinType());
						break;
						
					case "dateCreation":
						CommonUtils.addToQuerySearch(" "+getDbFunction("DATEFUNC")+"(TAPPR.DATE_CREATION,'DD-MM-YYYY "+getDbFunction("TIME")+"') " + val, strBufApprove, data.getJoinType());
						break;
									
					case "dateLastModified":
						CommonUtils.addToQuerySearch(" "+getDbFunction("DATEFUNC")+"(TAPPR.DATE_LAST_MODIFIED,'DD-MM-YYYY "+getDbFunction("TIME")+"') "  + val, strBufApprove, data.getJoinType());
						break;

					case "makerName":
						CommonUtils.addToQuerySearch(" (TAPPR.MAKER_NAME) IN ("+ val+") ", strBufApprove, data.getJoinType());
						break;

					default:
					}
					count++;
				}
			}
			VisionUsersVb visionUsersVb = CustomContextHolder.getContext();
			visionUsersVb = commonDao.getRestrictionInfo(visionUsersVb);
			if (("Y".equalsIgnoreCase(visionUsersVb.getUpdateRestriction()))) {
				if(ValidationUtil.isValid(visionUsersVb.getCountry())){
					CommonUtils.addToQuery(" COUNTRY"+getDbFunction("PIPELINE", "")+"'-'"+getDbFunction("PIPELINE", "")+"LE_BOOK IN ("+visionUsersVb.getCountry()+") ", strBufApprove);
				}
			}
			orderBy=" Order by COUNTRY,LE_BOOK,EXTRACTION_FREQUENCY,EXTRACTION_SEQUENCE ";
			return getQueryPopupResults(dObj,new StringBuffer(), strBufApprove, null, orderBy, params);
		}catch(Exception ex){
			ex.printStackTrace();
			//logger.error(((strBufApprove==null)? "strBufApprove is Null":strBufApprove.toString()));

			/*if (params != null)
				for(int i=0 ; i< params.size(); i++)
					//logger.error("objParams[" + i + "]" + params.get(i).toString());
*/			return null;
			}
}
	@Override
	public List<EtlManagerVb> getQueryResults(EtlManagerVb dObj, int intStatus){
		List<EtlManagerVb> collTemp = null;
		final int intKeyFieldsCount = 4;
		setServiceDefaults();
		String strQueryAppr = null;
		String strQueryPend = null;
		strQueryAppr = new String("SELECT APPR.COUNTRY,APPR.LE_BOOK,APPR.EXTRACTION_FREQUENCY,APPR.EXTRACTION_SEQUENCE,APPR.EXTRACTION_DATE_TYPE, "+ 
				" APPR.EXTRACTION_DAY,APPR.EXTRACTION_ENGINE, "+
				" APPR.HOLIDAY_FLAG, "+
				" APPR.EXTRACTION_DESCRIPTION,APPR.EXTRACTION_TYPE,APPR.SCHEDULE_TYPE,"+getDbFunction("DATEFUNC")+"(APPR.SCH_PERIOD_FROM_DT,'"+getDbFunction("DD_Mon_RRRR")+"') SCH_PERIOD_FROM_DT,"+
			    " "+getDbFunction("DATEFUNC")+"(APPR.SCH_PERIOD_TO_DT,'"+getDbFunction("DD_Mon_RRRR")+"') SCH_PERIOD_TO_DT, "+
				" "+getDbFunction("CONVERT")+"("+getDbFunction("TYPE")+" APPR.SCHEDULE_TIME, "+getDbFunction("TIMEFORMAT")+") SCHEDULE_TIME,APPR.EVENT_NAME,"+
						" " + getDbFunction("DATEFUNC") + "(APPR.EXTRACTION_START_DATE,'"
						+ getDbFunction("DD_Mon_RRRR")
						+ "') EXTRACTION_START_DATE,APPR.EXTRACTION_STATUS,APPR.RECORD_INDICATOR, "
						+ " APPR.DEPENDENT_BUILD_SEQ FROM RA_MST_EXTRACTION_HEADER APPR WHERE APPR.COUNTRY = ? AND " +
				" APPR.LE_BOOK = ?  AND APPR.EXTRACTION_FREQUENCY= ? AND APPR.EXTRACTION_SEQUENCE = ?");
		strQueryPend = new String("SELECT APPR.COUNTRY,APPR.LE_BOOK,APPR.EXTRACTION_FREQUENCY,APPR.EXTRACTION_SEQUENCE,APPR.EXTRACTION_DATE_TYPE, "+ 
				" APPR.EXTRACTION_DAY,APPR.EXTRACTION_ENGINE, "+
				" APPR.HOLIDAY_FLAG, "+
				" APPR.EXTRACTION_DESCRIPTION,APPR.EXTRACTION_TYPE,APPR.SCHEDULE_TYPE,APPR.SCH_PERIOD_FROM_DT,APPR.SCH_PERIOD_TO_DT, "+
						" APPR.SCHEDULE_TIME,APPR.EVENT_NAME,APPR.EXTRACTION_START_DATE,APPR.EXTRACTION_STATUS,APPR.RECORD_INDICATOR, "
						+ " APPR.DEPENDENT_BUILD_SEQ FROM RA_MST_EXTRACTION_HEADER APPR WHERE APPR.COUNTRY = ? AND " +
				" APPR.LE_BOOK = ?  AND APPR.EXTRACTION_FREQUENCY= ? AND APPR.EXTRACTION_SEQUENCE = ?");
		
		Object objParams[] = new Object[intKeyFieldsCount];
		objParams[0] = new String(dObj.getCountry());// country
		objParams[1] = new String(dObj.getLeBook());
		objParams[2] = new String(dObj.getExtractionFrequency());
		objParams[3] = new String(dObj.getExtractionSequence());
		try
		{if(!dObj.isVerificationRequired() || dObj.isReview()){intStatus =0;}
			if(intStatus == 0)
			{
				logger.info("Executing approved query");
				collTemp = getJdbcTemplate().query(strQueryAppr.toString(),objParams,getDetailMapper());
			}else{
				logger.info("Executing pending query");
				collTemp = getJdbcTemplate().query(strQueryPend.toString(),objParams,getDetailMapper());
			}
			return collTemp;
		}catch(Exception ex){
			ex.printStackTrace();
			logger.error("Error: getQueryResults Exception :   ");
			/*if(intStatus == 0)
				//logger.error(((strQueryAppr == null) ? "strQueryAppr is Null" : strQueryAppr.toString()));
			else
				//logger.error(((strQueryPend == null) ? "strQueryPend is Null" : strQueryPend.toString()));

			if (objParams != null)
				for(int i=0 ; i< objParams.length; i++)
					//logger.error("objParams[" + i + "]" + objParams[i].toString());
*/			return null;
		}
	}
	protected int deleteEtlHeader(EtlManagerVb vObject){
		String query = "Delete from RA_MST_EXTRACTION_HEADER where COUNTRY = ? AND LE_BOOK = ? AND EXTRACTION_FREQUENCY= ? AND EXTRACTION_SEQUENCE = ? ";
		Object[] args = {vObject.getCountry(),vObject.getLeBook(),vObject.getExtractionFrequency(),vObject.getExtractionSequence()};
		return getJdbcTemplate().update(query,args);
	}
	protected int doInsertApprEtlHeader(EtlManagerVb vObject){
		if("M".equalsIgnoreCase(vObject.getExrtactionType())) {
			vObject.setSchPeriodFrom(null);
			vObject.setSchPeriodTo(null);
			vObject.setScheduleTime(null);
		}
		if (vObject.getDependentBuildSeq() == "")
			vObject.setDependentBuildSeq(null);
		String query =  " Insert Into RA_MST_EXTRACTION_HEADER(COUNTRY,"+
				" LE_BOOK,EXTRACTION_FREQUENCY,EXTRACTION_SEQUENCE,EXTRACTION_DATE_TYPE,EXTRACTION_DAY,"+
				" HOLIDAY_FLAG,EXTRACTION_DESCRIPTION,EXTRACTION_ENGINE,EXTRACTION_TYPE,"+
				" SCHEDULE_TYPE,SCH_PERIOD_FROM_DT,SCH_PERIOD_TO_DT,SCHEDULE_TIME,"+
				" EVENT_NAME,EXTRACTION_START_DATE,EXTRACTION_STATUS_NT,EXTRACTION_STATUS,"+
				" RECORD_INDICATOR_NT,RECORD_INDICATOR,MAKER,VERIFIER,DATE_CREATION,DATE_LAST_MODIFIED,DEPENDENT_BUILD_SEQ) "
				+ " Values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?," + getDbFunction("SYSDATE") + ","
				+ getDbFunction("SYSDATE") + ",?)";
		
		Object[] args = {vObject.getCountry(),vObject.getLeBook(),vObject.getExtractionFrequency(),vObject.getExtractionSequence(),
				vObject.getExtractionDateType(),vObject.getExtractionDay(),vObject.getHolidayFlag(),vObject.getExtractionDescription(),
				vObject.getExtractionEngine(),vObject.getExrtactionType(),vObject.getScheduleType(),
				vObject.getSchPeriodFrom(),vObject.getSchPeriodTo(),vObject.getScheduleTime(),
				vObject.getEventName(),vObject.getExtractionStartDate(),
				vObject.getExtractionStatusNt(),vObject.getExtractionStatus(),vObject.getRecordIndicatorNt(),vObject.getRecordIndicator(),
				vObject.getMaker(), vObject.getVerifier(), vObject.getDependentBuildSeq() };
		return getJdbcTemplate().update(query,args);
	}
	protected int doUpdateApprHeader(EtlManagerVb vObject){
		String scheduleTime = "";
		String scheduleTimeQuery = "";
		if("ORACLE".equalsIgnoreCase(databaseType)) {
			scheduleTimeQuery = "To_Date(?,'DD/MM/YYYY HH24:MI:SS')";
			if(ValidationUtil.isValid(vObject.getSchPeriodFrom()))
				scheduleTime = vObject.getSchPeriodFrom()+" "+ vObject.getScheduleTime();
			else 
				scheduleTime=  null;
			if(ValidationUtil.isValid(scheduleTime))
				scheduleTimeQuery = "SCHEDULE_TIME = To_Date('"+scheduleTime+"','DD-MM-YYYY HH24:MI:SS'), ";
			else{
				scheduleTimeQuery = "";
			}
		}else if("MSSQL".equalsIgnoreCase(databaseType)) {
			scheduleTime = vObject.getScheduleTime();
			if(ValidationUtil.isValid(scheduleTime))
				scheduleTimeQuery = "SCHEDULE_TIME = '"+scheduleTime+"', ";
			else{
				scheduleTimeQuery = "";
			}
		}
		//scheduleTime = vObject.getSchPeriodFrom()+" "+ vObject.getScheduleTime();
		String query = " Update RA_MST_EXTRACTION_HEADER set EXTRACTION_DATE_TYPE= ?,EXTRACTION_DAY= ?,"+
				" HOLIDAY_FLAG= ?,EXTRACTION_DESCRIPTION= ?,EXTRACTION_ENGINE= ?,EXTRACTION_TYPE= ?,"+
				" SCHEDULE_TYPE= ?,SCH_PERIOD_FROM_DT= ?,SCH_PERIOD_TO_DT= ?, "+scheduleTimeQuery+" "+
				" EVENT_NAME= ?,EXTRACTION_START_DATE="+getDbFunction("DATE_CREATION")+",EXTRACTION_STATUS_NT= ?,EXTRACTION_STATUS=? " + 
				",RECORD_INDICATOR= ?,MAKER= ?,VERIFIER= ?,DATE_LAST_MODIFIED= " + getDbFunction("SYSDATE")
				+ ", " + " DEPENDENT_BUILD_SEQ = ? " +
				" WHERE COUNTRY = ? AND LE_BOOK = ? AND EXTRACTION_FREQUENCY = ? AND EXTRACTION_SEQUENCE = ? ";
		
		Object[] args = {vObject.getExtractionDateType(),vObject.getExtractionDay(),vObject.getHolidayFlag(),vObject.getExtractionDescription(),
				vObject.getExtractionEngine(),vObject.getExrtactionType(),vObject.getScheduleType(),
				vObject.getSchPeriodFrom(),vObject.getSchPeriodTo(),
				vObject.getEventName(),vObject.getExtractionStartDate(),
				vObject.getExtractionStatusNt(),vObject.getExtractionStatus(),vObject.getRecordIndicator(),
				vObject.getMaker(), vObject.getVerifier(), vObject.getDependentBuildSeq(), vObject.getCountry(),
				vObject.getLeBook(), vObject.getExtractionFrequency(), vObject.getExtractionSequence() };
		return getJdbcTemplate().update(query,args);
	}
	@Override
	protected List<EtlManagerVb> selectApprovedRecord(EtlManagerVb vObject){
		return getQueryResults(vObject, Constants.STATUS_ZERO);
	}
	@Override
	protected List<EtlManagerVb> doSelectPendingRecord(EtlManagerVb vObject){
		return getQueryResults(vObject, Constants.STATUS_PENDING);
	}
	@Override
	protected int getStatus(EtlManagerVb records){return records.getExtractionStatus();}
	@Override
	protected void setStatus(EtlManagerVb vObject,int status){vObject.setExtractionStatus(status);
	}
	@Override
	public ExceptionCode doInsertApprRecordForNonTrans(EtlManagerVb vObject) throws RuntimeCustomException {
		List<EtlManagerVb> collTemp = null;
		TransLineSbuVb transLineSbuVb = new TransLineSbuVb();
		ExceptionCode exceptionCode = null;
		strCurrentOperation = Constants.ADD;
		strApproveOperation =Constants.ADD;		
		setServiceDefaults();
		vObject.setExtractionSequence(String.valueOf(getenrateSequence(vObject)));
		collTemp = selectApprovedRecord(vObject);
		if (collTemp != null && collTemp.size() > 0){
			exceptionCode = getResultObject(Constants.DUPLICATE_KEY_INSERTION);
			throw buildRuntimeCustomException(exceptionCode);
		}
		vObject.setRecordIndicator(Constants.STATUS_ZERO);
		vObject.setExtractionStatus(Constants.STATUS_ZERO);
		vObject.setMaker(intCurrentUserId);
		vObject.setVerifier(intCurrentUserId);
		retVal = doInsertApprEtlHeader(vObject);
		if (retVal != Constants.SUCCESSFUL_OPERATION){
			exceptionCode = getResultObject(retVal);
			throw buildRuntimeCustomException(exceptionCode);
		} else {
			exceptionCode = getResultObject(Constants.SUCCESSFUL_OPERATION);
		}
		if(ValidationUtil.isValid(vObject.getEtlManagerDetaillst())) {
			exceptionCode = etlManagerDetailsDao.deleteAndInsertApprEtlDetail(vObject);
		}
		exceptionCode =getResultObject(Constants.SUCCESSFUL_OPERATION);
		exceptionCode = writeAuditLog(vObject, null);
		/*if(exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION){
			exceptionCode = getResultObject(Constants.AUDIT_TRAIL_ERROR);
			throw buildRuntimeCustomException(exceptionCode);
		}*/
		return exceptionCode;
	}
	@Override
	public ExceptionCode doUpdateApprRecordForNonTrans(EtlManagerVb vObject) throws RuntimeCustomException  {
		List<EtlManagerVb> collTemp = null;
		EtlManagerVb vObjectlocal = null;
		ExceptionCode exceptionCode = null;
		strApproveOperation =Constants.MODIFY;
		strErrorDesc  = "";
		strCurrentOperation = Constants.MODIFY;
		setServiceDefaults();
		vObject.setMaker(getIntCurrentUserId());
		collTemp = selectApprovedRecord(vObject);
		if (collTemp == null){
			exceptionCode = getResultObject(Constants.ERRONEOUS_OPERATION);
			throw buildRuntimeCustomException(exceptionCode);
		}
		vObjectlocal = ((ArrayList<EtlManagerVb>)collTemp).get(0);
		// Even if record is not there in Appr. table reject the record
		if (collTemp.size() == 0){
			exceptionCode = getResultObject(Constants.ATTEMPT_TO_MODIFY_UNEXISTING_RECORD);
			throw buildRuntimeCustomException(exceptionCode);
		}
		vObject.setRecordIndicator(Constants.STATUS_ZERO);
		vObject.setVerifier(getIntCurrentUserId());
		vObject.setDateCreation(vObjectlocal.getDateCreation());
		if("M".equalsIgnoreCase(vObject.getExrtactionType())) {
			vObject.setSchPeriodFrom(null);
			vObject.setSchPeriodTo(null);
			vObject.setScheduleTime(null);
		}		
		retVal = doUpdateApprHeader(vObject);
		if (retVal != Constants.SUCCESSFUL_OPERATION){
			exceptionCode = getResultObject(retVal);
			throw buildRuntimeCustomException(exceptionCode);
		} else {
			exceptionCode = getResultObject(Constants.SUCCESSFUL_OPERATION);
		}
		if(vObject.getEtlManagerDetaillst() != null && !vObject.getEtlManagerDetaillst().isEmpty()) {
			etlManagerDetailsDao.deleteAndInsertApprEtlDetail(vObject);
		}
		String systemDate = getSystemDate();
		vObject.setDateLastModified(systemDate);
		
		exceptionCode = writeAuditLog(vObject, vObjectlocal);
		/*if(exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION){
			exceptionCode = getResultObject(Constants.AUDIT_TRAIL_ERROR);
			throw buildRuntimeCustomException(exceptionCode);
		}*/
		exceptionCode =getResultObject(Constants.SUCCESSFUL_OPERATION);
		return exceptionCode;
	}
	
	@Override
	protected String getAuditString(EtlManagerVb vObject){
		final String auditDelimiter = vObject.getAuditDelimiter();
		final String auditDelimiterColVal = vObject.getAuditDelimiterColVal();
		StringBuffer strAudit = new StringBuffer("");
		try
		{
			if(ValidationUtil.isValid(vObject.getCountry()))
				strAudit.append("COUNTRY"+auditDelimiterColVal+vObject.getCountry().trim());
			else
				strAudit.append("COUNTRY"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);

			if(ValidationUtil.isValid(vObject.getLeBook()))
				strAudit.append("LE_BOOK"+auditDelimiterColVal+vObject.getLeBook().trim());
			else
				strAudit.append("LE_BOOK"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			
			if(ValidationUtil.isValid(vObject.getExtractionFrequency()))
				strAudit.append("EXTRACTION_FREQUENCY"+auditDelimiterColVal+vObject.getExtractionFrequency().trim());
			else
				strAudit.append("EXTRACTION_FREQUENCY"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			
			if(ValidationUtil.isValid(vObject.getExtractionSequence()))
				strAudit.append("EXTRACTION_SEQUENCE"+auditDelimiterColVal+vObject.getExtractionSequence().trim());
			else
				strAudit.append("EXTRACTION_SEQUENCE"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			
			
			strAudit.append("EXTRACTION_STATUS_NT"+auditDelimiterColVal+vObject.getExtractionStatusNt());
			strAudit.append(auditDelimiter);
			
			strAudit.append("EXTRACTION_STATUS"+auditDelimiterColVal+vObject.getExtractionStatus());
			strAudit.append(auditDelimiter);
						
			strAudit.append("RECORD_INDICATOR_NT"+auditDelimiterColVal+vObject.getRecordIndicatorNt());
			strAudit.append(auditDelimiter);
			if(vObject.getRecordIndicator() == -1)
				vObject.setRecordIndicator(0);
			strAudit.append("RECORD_INDICATOR"+auditDelimiterColVal+vObject.getRecordIndicator());
			strAudit.append(auditDelimiter);
			strAudit.append("MAKER"+auditDelimiterColVal+vObject.getMaker());
			strAudit.append(auditDelimiter);
			strAudit.append("VERIFIER"+auditDelimiterColVal+vObject.getVerifier());
			strAudit.append(auditDelimiter);
			strAudit.append("INTERNAL_STATUS"+auditDelimiterColVal+vObject.getInternalStatus());
			strAudit.append(auditDelimiter);
			
			if(vObject.getDateLastModified() != null && !vObject.getDateLastModified().equalsIgnoreCase(""))
				strAudit.append("DATE_LAST_MODIFIED"+auditDelimiterColVal+vObject.getDateLastModified().trim());
			else
				strAudit.append("DATE_LAST_MODIFIED"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			
			if(vObject.getDateCreation() != null && !vObject.getDateCreation().equalsIgnoreCase(""))
				strAudit.append("DATE_CREATION"+auditDelimiterColVal+vObject.getDateCreation().trim());
			else
				strAudit.append("DATE_CREATION"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			

		}
		catch(Exception ex)
		{
			strErrorDesc = ex.getMessage();
			strAudit = strAudit.append(strErrorDesc);
			ex.printStackTrace();
		}
		return strAudit.toString();
	}
	
	@Override
	public ExceptionCode doDeleteApprRecordForNonTrans(EtlManagerVb vObject) throws RuntimeCustomException {
	List<EtlManagerVb> collTemp = null;
	ExceptionCode exceptionCode = null;
	EtlManagerVb vObjectlocal = null;
	strCurrentOperation = Constants.DELETE;
	strApproveOperation =Constants.DELETE;		
	setServiceDefaults();
	vObject.setMaker(getIntCurrentUserId());
	collTemp = selectApprovedRecord(vObject);
	if (collTemp == null){
		exceptionCode = getResultObject(Constants.ERRONEOUS_OPERATION);
		throw buildRuntimeCustomException(exceptionCode);
	}
	// If record already exists in the approved table, reject the addition
	if (collTemp.size() > 0 ){
		int intStaticDeletionFlag = getStatus(((ArrayList<EtlManagerVb>)collTemp).get(0));
		if (intStaticDeletionFlag == Constants.PASSIVATE){
			exceptionCode = getResultObject(Constants.CANNOT_DELETE_AN_INACTIVE_RECORD);
			throw buildRuntimeCustomException(exceptionCode);
		}
	}
	else{
		exceptionCode = getResultObject(Constants.ATTEMPT_TO_DELETE_UNEXISTING_RECORD);
		throw buildRuntimeCustomException(exceptionCode);
	}
	vObjectlocal = ((ArrayList<EtlManagerVb>)collTemp).get(0);
	vObject.setDateCreation(vObjectlocal.getDateCreation());
	if(vObject.isStaticDelete()){
		vObjectlocal.setMaker(getIntCurrentUserId());
		vObject.setVerifier(getIntCurrentUserId());
		vObject.setRecordIndicator(Constants.STATUS_ZERO);
//		setStatus(vObject, Constants.PASSIVATE);
		setStatus(vObjectlocal, Constants.PASSIVATE);
		vObjectlocal.setVerifier(getIntCurrentUserId());
		vObjectlocal.setRecordIndicator(Constants.STATUS_ZERO);
		retVal = doUpdateApprHeader(vObjectlocal);
		String systemDate = getSystemDate();
		vObject.setDateLastModified(systemDate);
		if(ValidationUtil.isValid(vObject.getEtlManagerDetaillst())) {
			retVal = etlManagerDetailsDao.deleteEtlDetails(vObjectlocal);
		}
	}else{
		retVal = deleteEtlHeader(vObject);
		if(ValidationUtil.isValid(vObject.getEtlManagerDetaillst())) {
			retVal = etlManagerDetailsDao.deleteEtlDetails(vObjectlocal);
			String systemDate = getSystemDate();
			vObject.setDateLastModified(systemDate);
		}
		if (retVal != Constants.SUCCESSFUL_OPERATION){
			exceptionCode = getResultObject(retVal);
			throw buildRuntimeCustomException(exceptionCode);
		}
		exceptionCode = writeAuditLog(vObject, vObjectlocal);
		if(exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION){
			exceptionCode = getResultObject(Constants.AUDIT_TRAIL_ERROR);
			throw buildRuntimeCustomException(exceptionCode);
		}
	}
	exceptionCode = getResultObject(Constants.SUCCESSFUL_OPERATION);
	return exceptionCode;
}
/*public ExceptionCode doDeleteRecordForNonTrans(EtlManagerVb vObject) throws RuntimeCustomException {
	List<EtlManagerVb> collTemp = null;
	ExceptionCode exceptionCode = null;
	EtlManagerVb vObjectlocal = null;
	strCurrentOperation = Constants.DELETE;
	strApproveOperation =Constants.DELETE;		
	setServiceDefaults();
	vObject.setMaker(getIntCurrentUserId());
	collTemp = doSelectPendingRecord(vObject);
	if (collTemp == null){
		exceptionCode = getResultObject(Constants.ERRONEOUS_OPERATION);
		throw buildRuntimeCustomException(exceptionCode);
	}
	// If record already exists in the approved table, reject the addition
	if (collTemp.size() > 0 ){
		int intStaticDeletionFlag = getStatus(((ArrayList<EtlManagerVb>)collTemp).get(0));
		if (intStaticDeletionFlag == Constants.PASSIVATE){
			exceptionCode = getResultObject(Constants.CANNOT_DELETE_AN_INACTIVE_RECORD);
			throw buildRuntimeCustomException(exceptionCode);
		}
	}
	else{
		exceptionCode = getResultObject(Constants.ATTEMPT_TO_DELETE_UNEXISTING_RECORD);
		throw buildRuntimeCustomException(exceptionCode);
	}
	int transLineSbuRecCnt = getTransLineSbuPendRecCnt(vObject); // Get count of existing Sbu Record
	int transLineGlCnt = getPendTransLineGlRecCnt(vObject); // Get count of existing GL Record
	
	vObjectlocal = ((ArrayList<EtlManagerVb>)collTemp).get(0);
	vObject.setDateCreation(vObjectlocal.getDateCreation());
	if(vObject.isStaticDelete()){
		vObjectlocal.setMaker(getIntCurrentUserId());
		vObject.setVerifier(getIntCurrentUserId());
		vObject.setRecordIndicator(Constants.STATUS_ZERO);
//		setStatus(vObject, Constants.PASSIVATE);
		setStatus(vObjectlocal, Constants.PASSIVATE);
		vObjectlocal.setVerifier(getIntCurrentUserId());
		vObjectlocal.setRecordIndicator(Constants.STATUS_ZERO);
		retVal = doUpdatePendHeader(vObjectlocal);
		String systemDate = getSystemDate();
		vObject.setDateLastModified(systemDate);
		if (transLineSbuRecCnt > 0){
			//doUpdatePendTransSbu(vObjectlocal);
		}
		if (transLineGlCnt > 0){
			//doUpdatePendTransGl(vObjectlocal);
		}
	}else{
		retVal = deleteEtlHeaderPend(vObject);
		if (transLineSbuRecCnt > 0){
			retVal = deleteTransLineSbuPend(vObject);
		}
		if (transLineGlCnt > 0){
			retVal = deleteTransLineGlPend(vObject);
		}
	}
	exceptionCode = getResultObject(Constants.SUCCESSFUL_OPERATION);
	return exceptionCode;
}*/
		/*public List<EtlManagerVb>  getQueryResultsNavigate(EtlManagerVb dObj){
			List<EtlManagerVb> collTemp = null;
			String strApp = "";
			String strPend = "";
			String whereCondition = "";
			String whereNotExists = "";
			String orderBy = "";
			
			strApp = new String("SELECT TAppr.COUNTRY, TAppr.LE_BOOK, TAppr.EXTRACTION_FREQUENCY,TAppr.TRANS_LINE_DESCRIPTION, " + 
					"TAppr.TRANS_LINE_PROD_SUB_TYPE,T1.ALPHA_SUBTAB_DESCRIPTION TRANS_LINE_PROD_SUB_TYPE_DESC,  " + 
					"TAppr.TRANS_LINE_PROD_GRP, T2.ALPHA_SUBTAB_DESCRIPTION TRANS_LINE_PROD_GRP_DESC, " + 
					"TAppr.EXTRACTION_FREQUENCY,T5.ALPHA_SUBTAB_DESCRIPTION EXTRACTION_FREQUENCY_DESC, " + 
					"TAppr.TRANS_LINE_STATUS,T3.NUM_SUBTAB_DESCRIPTION TRANS_LINE_STATUS_DESC,                                                 " + 
					"TAppr.RECORD_INDICATOR,T4.NUM_SUBTAB_DESCRIPTION RECORD_INDICATOR_DESC,                                                   " + 
					"TAppr. MAKER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = ISNULL(TAppr.MAKER,0) ) MAKER_NAME,               " + 
					"TAppr.VERIFIER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = ISNULL(TAppr.VERIFIER,0) ) VERIFIER_NAME,       " + 
					"TAppr.DATE_LAST_MODIFIED ,                                                                                                 " + 
					"TAppr.DATE_CREATION FROM RA_MST_EXTRACTION_HEADER TAppr ,Alpha_sub_tab t1,ALPHA_SUB_TAB T2,NUM_SUB_TAB T3,NUM_SUB_TAB T4,  " + 
					"ALPHA_SUB_TAB T5 Where t1.Alpha_tab = TAppr.TRANS_LINE_PROD_SUB_TYPE_AT                                                     " + 
					"and t1.Alpha_sub_tab = TAppr.TRANS_LINE_PROD_SUB_TYPE                                                                     " + 
					"and t2.Alpha_tab = TAppr.TRANS_LINE_PROD_GRP_AT                                                                           " + 
					"and t2.Alpha_sub_tab = TAppr.TRANS_LINE_PROD_GRP                                                                          " + 
					"and t3.NUM_tab = TAppr.EXTRACTION_STATUS_NT                                                                               " + 
					"and t3.NUM_sub_tab = TAppr.TRANS_LINE_STATUS                                                                              " + 
					"and t4.NUM_tab = TAppr.RECORD_INDICATOR_NT                                                                                " + 
					"and t4.NUM_sub_tab = TAppr.RECORD_INDICATOR AND T5.ALPHA_TAB = TAppr.EXTRACTION_FREQUENCY_AT " + 
					"AND t5.Alpha_sub_tab = TAppr.EXTRACTION_FREQUENCY) TAppr");
			whereNotExists = new String("Not Exists (Select 'X' From RA_MST_EXTRACTION_HEADER_PEND TPEND WHERE TAppr.COUNTRY = TPend.COUNTRY " + 
					" AND TAppr.LE_BOOK = TPend.LE_BOOK AND TAppr.EXTRACTION_FREQUENCY = TPend.EXTRACTION_FREQUENCY)");
			
			strPend = new String("SELECT TPend.COUNTRY, TPend.LE_BOOK, TPend.EXTRACTION_FREQUENCY,TPend.TRANS_LINE_DESCRIPTION,           " + 
					"TPend.TRANS_LINE_PROD_SUB_TYPE,T1.ALPHA_SUBTAB_DESCRIPTION TRANS_LINE_PROD_SUB_TYPE_DESC,                                 " + 
					"TPend.TRANS_LINE_PROD_GRP, T2.ALPHA_SUBTAB_DESCRIPTION TRANS_LINE_PROD_GRP_DESC,TPend.EXTRACTION_FREQUENCY,T5.ALPHA_SUBTAB_DESCRIPTION EXTRACTION_FREQUENCY_DESC," + 
					"TPend.TRANS_LINE_STATUS,T3.NUM_SUBTAB_DESCRIPTION TRANS_LINE_STATUS_DESC,                                                 " + 
					"TPend.RECORD_INDICATOR,T4.NUM_SUBTAB_DESCRIPTION RECORD_INDICATOR_DESC,                                                   " + 
					"TPend. MAKER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = ISNULL(TPend.MAKER,0) ) MAKER_NAME,               " + 
					"TPend.VERIFIER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = ISNULL(TPend.VERIFIER,0) ) VERIFIER_NAME,       " + 
					"TPend.DATE_LAST_MODIFIED,                                                                                                 " + 
					"TPend.DATE_CREATION FROM RA_MST_EXTRACTION_HEADER_PEND TPend ,Alpha_sub_tab t1,ALPHA_SUB_TAB T2,NUM_SUB_TAB T3,NUM_SUB_TAB T4,  " + 
					"ALPHA_SUB_TAB T5 Where t1.Alpha_tab = TPend.TRANS_LINE_PROD_SUB_TYPE_AT                                                                    " + 
					"and t1.Alpha_sub_tab = TPend.TRANS_LINE_PROD_SUB_TYPE                                                                     " + 
					"and t2.Alpha_tab = TPend.TRANS_LINE_PROD_GRP_AT                                                                           " + 
					"and t2.Alpha_sub_tab = TPend.TRANS_LINE_PROD_GRP                                                                          " + 
					"and t3.NUM_tab = TPend.EXTRACTION_STATUS_NT                                                                               " + 
					"and t3.NUM_sub_tab = TPend.TRANS_LINE_STATUS                                                                              " + 
					"and t4.NUM_tab = TPend.RECORD_INDICATOR_NT                                                                                " + 
					"and t4.NUM_sub_tab = TPend.RECORD_INDICATOR AND T5.ALPHA_TAB = TPend.EXTRACTION_FREQUENCY_AT AND t5.Alpha_sub_tab = TPend.EXTRACTION_FREQUENCY ");
			//whereCondition = "WHERE COUNTRY = ? AND LE_BOOK = ? AND EXTRACTION_FREQUENCY = ?"; 
			orderBy = "ORDER BY COUNTRY , LE_BOOK, EXTRACTION_FREQUENCY";
			return getquer;
		}*/
	public int getenrateSequence(EtlManagerVb dObj){
		int cnt = 1;
		String query= "";
		try {
			query = " SELECT  MAX(EXTRACTION_SEQUENCE)+1  FROM RA_MST_EXTRACTION_HEADER ";
			
			//WHERE COUNTRY = ? AND LE_BOOK = ? AND EXTRACTION_FREQUENCY = ? 
			
			Object objParams[] = new Object[3];
			/*objParams[0] = new String(dObj.getCountry());
			objParams[1] = new String(dObj.getLeBook());
			objParams[2] = new String(dObj.getExtractionFrequency());*/
			cnt = getJdbcTemplate().queryForObject(query, Integer.class);
			return cnt;
		}catch(Exception e) {
			return cnt;
		}
	}
	}