package com.vision.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
import com.vision.vb.AlphaSubTabVb;
import com.vision.vb.ConcessionBlLinkVb;

@Component
public class ConcessionBlLinkDao extends AbstractDao<ConcessionBlLinkVb> {

	@Value("${app.databaseType}")
	private String databaseType;

	protected RowMapper getDetailMapper() {
		RowMapper mapper = new RowMapper() {
			@Override
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				ConcessionBlLinkVb vObject = new ConcessionBlLinkVb();
				vObject.setCountry(rs.getString("COUNTRY"));
				vObject.setLeBook(rs.getString("LE_BOOK"));
				vObject.setBusinessLineId(rs.getString("BUSINESS_LINE_ID"));
				vObject.setConcessionId(rs.getString("CONCESSION_ID"));
				vObject.setConcessionDesc(rs.getString("Conc_description"));
				vObject.setConcessionPriority(rs.getString("CONCESSION_PRIORITY"));
				vObject.setConcessionSubPriority(rs.getString("CONCESSION_SUB_PRIORITY"));
				vObject.setConcessionAgg(rs.getString("CONCESSION_AGG"));
				vObject.setStatus(rs.getInt("Status"));
				vObject.setRecordIndicator(rs.getInt("RECORD_INDICATOR"));
				vObject.setRecordIndicatorDesc(rs.getString("RECORD_INDICATOR_Desc"));
				vObject.setMaker(rs.getInt("MAKER"));
				vObject.setMakerName(rs.getString("MAKER_NAME"));
				vObject.setVerifier(rs.getInt("VERIFIER"));
				vObject.setVerifierName(rs.getString("VERIFIER_NAME"));
				vObject.setDateLastModified(rs.getString("DATE_LAST_MODIFIED"));
				vObject.setDateCreation(rs.getString("DATE_CREATION"));
				return vObject;
			}
		};
		return mapper;
	}

	@Override
	public List<ConcessionBlLinkVb> getQueryResults(ConcessionBlLinkVb vObject, int intStatus) {
		List<ConcessionBlLinkVb> collTemp = null;
		setServiceDefaults();
		String strQueryAppr = null;
		strQueryAppr = new String(" SELECT TAppr.COUNTRY, TAppr.LE_BOOK,TAppr.BUSINESS_LINE_ID,TAppr.CONCESSION_ID,"
				+ " (select CONCESSION_DESCRIPTION from  RA_MST_CONCESSION_HEADER t1 where TAppr.COUNTRY =t1.country"
				+ " and TAppr.le_book = t1.Le_book and TAppr.CONCESSION_ID = t1.CONCESSION_ID) Conc_description,"
				+ " TAPPR.CONCESSION_PRIORITY, TAPPR.CONCESSION_SUB_PRIORITY,TAPPR.CONCESSION_AGG, "
				+ " TAppr.Status,T3.NUM_SUBTAB_DESCRIPTION Status_DESC, "
				+ " TAppr.RECORD_INDICATOR,T4.NUM_SUBTAB_DESCRIPTION RECORD_INDICATOR_DESC,"
				+ " TAppr. MAKER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "
				+ getDbFunction("NVL") + "(TAppr.MAKER,0) ) MAKER_NAME, "
				+ " TAppr.VERIFIER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "
				+ getDbFunction("NVL") + "(TAppr.VERIFIER,0) ) VERIFIER_NAME,  " + " "
				+ getDbFunction("DATEFUNC") + "(TAppr.DATE_LAST_MODIFIED, 'dd-MM-yyyy "
				+ getDbFunction("TIME") + "') DATE_LAST_MODIFIED ," + getDbFunction("DATEFUNC")
				+ "(TAppr.DATE_CREATION, 'dd-MM-yyyy " + getDbFunction("TIME") + "') DATE_CREATION "
				+ " FROM RA_MST_BL_CONCESSIONS TAppr ,NUM_SUB_TAB T3,NUM_SUB_TAB T4     " + " Where  "
				+ "	 t3.NUM_tab = TAppr.Status_NT  " + " and t3.NUM_sub_tab = TAppr.Status  "
				+ " and t4.NUM_tab = TAppr.RECORD_INDICATOR_NT  "
				+ " and t4.NUM_sub_tab = TAppr.RECORD_INDICATOR and TAppr.COUNTRY = ? "
				+ " and TAppr.LE_BOOK = ?  and TAppr.BUSINESS_LINE_ID = ? AND TAppr.CONCESSION_ID= ? AND TAPPR.CONCESSION_PRIORITY = ? ");

		Object objParams[] = new Object[5];
		objParams[0] = vObject.getCountry();
		objParams[1] = vObject.getLeBook();
		objParams[2] = vObject.getBusinessLineId();
		objParams[3] = vObject.getConcessionId();
		objParams[4] = vObject.getConcessionPriority();
		try {
			logger.info("Executing approved query");
			collTemp = getJdbcTemplate().query(strQueryAppr.toString(), objParams, getDetailMapper());
			return collTemp;
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("Error: getQueryResults Exception :   ");
			//logger.error(((strQueryAppr == null) ? "strQueryAppr is Null" : strQueryAppr.toString()));
			/*if (objParams != null)
				for (int i = 0; i < objParams.length; i++)
					//logger.error("objParams[" + i + "]" + objParams[i].toString());
*/			return null;
		}
	}

	public List<ConcessionBlLinkVb> getQueryBlResults(ConcessionBlLinkVb vObject, int intStatus) {
		List<ConcessionBlLinkVb> collTemp = null;
		setServiceDefaults();
		String strQueryAppr = null;
		strQueryAppr = new String(" SELECT TAppr.COUNTRY, TAppr.LE_BOOK,TAppr.BUSINESS_LINE_ID,TAppr.CONCESSION_ID,"
				+ " (select CONCESSION_DESCRIPTION from  RA_MST_CONCESSION_HEADER t1 where TAppr.COUNTRY =t1.country"
				+ " and TAppr.le_book = t1.Le_book and TAppr.CONCESSION_ID = t1.CONCESSION_ID) Conc_description,"
				+ " TAPPR.CONCESSION_PRIORITY, TAPPR.CONCESSION_SUB_PRIORITY,TAPPR.CONCESSION_AGG, "
				+ " TAppr.Status,T3.NUM_SUBTAB_DESCRIPTION Status_DESC, "
				+ " TAppr.RECORD_INDICATOR,T4.NUM_SUBTAB_DESCRIPTION RECORD_INDICATOR_DESC,"
				+ " TAppr. MAKER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "
				+ getDbFunction("NVL") + "(TAppr.MAKER,0) ) MAKER_NAME, "
				+ " TAppr.VERIFIER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "
				+ getDbFunction("NVL") + "(TAppr.VERIFIER,0) ) VERIFIER_NAME,  " + " "
				+ getDbFunction("DATEFUNC") + "(TAppr.DATE_LAST_MODIFIED, 'dd-MM-yyyy "
				+ getDbFunction("TIME") + "') DATE_LAST_MODIFIED ," + getDbFunction("DATEFUNC")
				+ "(TAppr.DATE_CREATION, 'dd-MM-yyyy " + getDbFunction("TIME") + "') DATE_CREATION "
				+ " FROM RA_MST_BL_CONCESSIONS TAppr ,NUM_SUB_TAB T3,NUM_SUB_TAB T4     " + " Where  "
				+ "	 t3.NUM_tab = TAppr.Status_NT  " + " and t3.NUM_sub_tab = TAppr.Status  "
				+ " and t4.NUM_tab = TAppr.RECORD_INDICATOR_NT  "
				+ " and t4.NUM_sub_tab = TAppr.RECORD_INDICATOR and TAppr.COUNTRY = ? "
				+ " and TAppr.LE_BOOK = ?  and TAppr.BUSINESS_LINE_ID = ? ");

		Object objParams[] = new Object[3];
		objParams[0] = vObject.getCountry();
		objParams[1] = vObject.getLeBook();
		objParams[2] = vObject.getBusinessLineId();

		try {
			logger.info("Executing approved query");
			collTemp = getJdbcTemplate().query(strQueryAppr.toString(), objParams, getDetailMapper());
			return collTemp;
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("Error: getQueryResults Exception :   ");
			//logger.error(((strQueryAppr == null) ? "strQueryAppr is Null" : strQueryAppr.toString()));
		/*	if (objParams != null)
				for (int i = 0; i < objParams.length; i++)
					//logger.error("objParams[" + i + "]" + objParams[i].toString());
*/			return null;
		}
	}

	@Override
	protected void setServiceDefaults() {
		serviceName = "ConcessionBlLink";
		serviceDesc = "Concession Link";
		tableName = "RA_MST_BL_CONCESSIONS";
		childTableName = "RA_MST_BL_CONCESSIONS";
		intCurrentUserId = CustomContextHolder.getContext().getVisionId();
	}

	protected int doInsertionApprConcessionBl(ConcessionBlLinkVb vObject) {
		String query = " Insert Into RA_MST_BL_CONCESSIONS(COUNTRY,LE_BOOK,BUSINESS_LINE_ID,"
				+ " CONCESSION_ID,CONCESSION_PRIORITY,CONCESSION_SUB_PRIORITY,CONCESSION_AGG,Status_NT,Status,"
				+ "RECORD_INDICATOR_NT,RECORD_INDICATOR,MAKER,VERIFIER,INTERNAL_STATUS,DATE_LAST_MODIFIED,DATE_CREATION) "
				+ " Values (?, ?, ?, ? ,? ,? ,? ,? ,?, ?, ? ,? ,? ,? ," + getDbFunction("SYSDATE") + ","
				+ getDbFunction("SYSDATE") + ")";

		Object[] args = { vObject.getCountry(), vObject.getLeBook(), vObject.getBusinessLineId(),
				vObject.getConcessionId(), vObject.getConcessionPriority(), vObject.getConcessionSubPriority(),
				vObject.getConcessionAgg(), vObject.getStatusNt(), vObject.getStatus(), vObject.getRecordIndicatorNt(),
				vObject.getRecordIndicator(), vObject.getMaker(), vObject.getVerifier(), vObject.getInternalStatus() };
		return getJdbcTemplate().update(query, args);
	}

	protected int deleteConcessionBlAppr(ConcessionBlLinkVb vObject) {
		try {
			String query = "Delete from RA_MST_BL_CONCESSIONS  WHERE COUNTRY= ? AND LE_BOOK= ?  AND BUSINESS_LINE_ID = ?";
			Object[] args = { vObject.getCountry(), vObject.getLeBook(), vObject.getBusinessLineId() };
			getJdbcTemplate().update(query, args);
			return Constants.SUCCESSFUL_OPERATION;
		}catch(Exception e) {
			return Constants.ERRONEOUS_OPERATION;
		}
		
	}
	@Override
	protected List<ConcessionBlLinkVb> selectApprovedRecord(ConcessionBlLinkVb vObject) {
		return getQueryResults(vObject, Constants.STATUS_ZERO);
	}

	@Transactional(rollbackForClassName = { "com.vision.exception.RuntimeCustomException" })
	public ExceptionCode doInsertApprRecord(List<ConcessionBlLinkVb> vObjects) throws RuntimeCustomException {
		ExceptionCode exceptionCode = new ExceptionCode();
		strErrorDesc = "";
		strCurrentOperation = Constants.ADD;
		strApproveOperation = Constants.ADD;
		setServiceDefaults();
		try {
			for (ConcessionBlLinkVb vObject : vObjects) {
				for (ConcessionBlLinkVb subPriorityVb : vObject.getConcessionPriorityList()) {
					exceptionCode = doInsertApprRecordForNonTrans(subPriorityVb);
					if (exceptionCode == null || exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION) {
						throw buildRuntimeCustomException(exceptionCode);
					}else {
						exceptionCode.setErrorMsg("Concession Link Add Successful");
					}
				}
			}
			return exceptionCode;
		} catch (RuntimeCustomException rcException) {
			throw rcException;
		} catch (UncategorizedSQLException uSQLEcxception) {
			strErrorDesc = parseErrorMsg(uSQLEcxception);
			exceptionCode = getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION);
			throw buildRuntimeCustomException(exceptionCode);
		} catch (Exception ex) {
			logger.error("Error in Add.", ex);
			strErrorDesc = ex.getMessage();
			exceptionCode = getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION);
			throw buildRuntimeCustomException(exceptionCode);
		}
	}

	@Override
	protected ExceptionCode doInsertApprRecordForNonTrans(ConcessionBlLinkVb vObject) throws RuntimeCustomException {
		List<ConcessionBlLinkVb> collTemp = null;
		ExceptionCode exceptionCode = new ExceptionCode();
		strCurrentOperation = Constants.ADD;
		strApproveOperation = Constants.ADD;
		setServiceDefaults();
		if ("RUNNING".equalsIgnoreCase(getBuildStatus(vObject))) {
			exceptionCode = getResultObject(Constants.BUILD_IS_RUNNING);
			throw buildRuntimeCustomException(exceptionCode);
		}
		vObject.setMaker(getIntCurrentUserId());
		collTemp = selectApprovedRecord(vObject);
		// If record already exists in the approved table, reject the addition
		if (collTemp.size() > 0) {
			int intStaticDeletionFlag = getStatus(((ArrayList<ConcessionBlLinkVb>) collTemp).get(0));
			if (intStaticDeletionFlag == Constants.PASSIVATE) {
				logger.error("Collection size is greater than zero - Duplicate record found, but inactive");
				exceptionCode = getResultObject(Constants.RECORD_ALREADY_PRESENT_BUT_INACTIVE);
				throw buildRuntimeCustomException(exceptionCode);
			} else {
				logger.error("Collection size is greater than zero - Duplicate record found");
				exceptionCode = getResultObject(Constants.DUPLICATE_KEY_INSERTION);
				throw buildRuntimeCustomException(exceptionCode);
			}
		}
		// Try inserting the record
		vObject.setRecordIndicator(Constants.STATUS_ZERO);
		vObject.setVerifier(getIntCurrentUserId());
		retVal = doInsertionApprConcessionBl(vObject);
		if (retVal != Constants.SUCCESSFUL_OPERATION) {
			exceptionCode = getResultObject(retVal);
			throw buildRuntimeCustomException(exceptionCode);
		} else {
			exceptionCode.setErrorCode(retVal);
			exceptionCode.setErrorMsg("Concession Link Add Successful");
		}
		String systemDate = getSystemDate();
		vObject.setDateLastModified(systemDate);
		vObject.setDateCreation(systemDate);
		/*
		 * exceptionCode = writeAuditLog(vObject, null); if(exceptionCode.getErrorCode()
		 * != Constants.SUCCESSFUL_OPERATION){ exceptionCode =
		 * getResultObject(Constants.AUDIT_TRAIL_ERROR); throw
		 * buildRuntimeCustomException(exceptionCode); }
		 */
		return exceptionCode;
	}




	protected ExceptionCode doDeleteApprRecordForNonTrans(ConcessionBlLinkVb vObject) throws RuntimeCustomException {
		List<ConcessionBlLinkVb> collTemp = null;
		ExceptionCode exceptionCode = new ExceptionCode();
		strApproveOperation = Constants.DELETE;
		strErrorDesc = "";
		strCurrentOperation = Constants.DELETE;
		setServiceDefaults();
		ConcessionBlLinkVb vObjectlocal = null;
		vObject.setMaker(getIntCurrentUserId());

		collTemp = getQueryBlResults(vObject,Constants.STATUS_ZERO);
		if (collTemp == null) {
			exceptionCode = getResultObject(Constants.ERRONEOUS_OPERATION);
			throw buildRuntimeCustomException(exceptionCode);
		}
		// If record already exists in the approved table, reject the addition
		if (collTemp.size() > 0) {
			int intStaticDeletionFlag = getStatus(((ArrayList<ConcessionBlLinkVb>) collTemp).get(0));
			if (intStaticDeletionFlag == Constants.PASSIVATE) {
				exceptionCode = getResultObject(Constants.CANNOT_DELETE_AN_INACTIVE_RECORD);
				throw buildRuntimeCustomException(exceptionCode);
			}
		} else {
			exceptionCode = getResultObject(Constants.ATTEMPT_TO_DELETE_UNEXISTING_RECORD);
			throw buildRuntimeCustomException(exceptionCode);
		}

		// delete the record from the Approve Table
		retVal = deleteConcessionBlAppr(vObject);
		if (retVal == Constants.SUCCESSFUL_OPERATION) {
			exceptionCode.setErrorCode(retVal);
			exceptionCode.setErrorMsg("Concession Link Delete Successful");
		}else {
			exceptionCode = getResultObject(retVal);
			throw buildRuntimeCustomException(exceptionCode);
		}
		writeAuditLog(null, vObject);
		return exceptionCode;
	}

	@Override
	protected String getAuditString(ConcessionBlLinkVb vObject) {
		final String auditDelimiter = vObject.getAuditDelimiter();
		final String auditDelimiterColVal = vObject.getAuditDelimiterColVal();
		StringBuffer strAudit = new StringBuffer("");
		try {
			if (ValidationUtil.isValid(vObject.getCountry()))
				strAudit.append("COUNTRY" + auditDelimiterColVal + vObject.getCountry().trim());
			else
				strAudit.append("COUNTRY" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);

			if (ValidationUtil.isValid(vObject.getLeBook()))
				strAudit.append("LE_BOOK" + auditDelimiterColVal + vObject.getLeBook().trim());
			else
				strAudit.append("LE_BOOK" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);

			if (ValidationUtil.isValid(vObject.getConcessionId()))
				strAudit.append("CONCESSION_ID" + auditDelimiterColVal + vObject.getConcessionId().trim());
			else
				strAudit.append("CONCESSION_ID" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);

			if (ValidationUtil.isValid(vObject.getBusinessLineId()))
				strAudit.append("BUSINESS_LINE_ID" + auditDelimiterColVal + vObject.getBusinessLineId().trim());
			else
				strAudit.append("BUSINESS_LINE_ID" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);

			if (ValidationUtil.isValid(vObject.getConcessionPriority()))
				strAudit.append("CONCESSION_PRIORITY" + auditDelimiterColVal + vObject.getConcessionPriority().trim());
			else
				strAudit.append("CONCESSION_PRIORITY" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);

			if (ValidationUtil.isValid(vObject.getConcessionSubPriority()))
				strAudit.append(
						"CONCESSION_SUB_PRIORITY" + auditDelimiterColVal + vObject.getConcessionSubPriority().trim());
			else
				strAudit.append("CONCESSION_SUB_PRIORITY" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);

			if (ValidationUtil.isValid(vObject.getConcessionAgg()))
				strAudit.append("CONCESSION_AGG" + auditDelimiterColVal + vObject.getConcessionAgg().trim());
			else
				strAudit.append("CONCESSION_AGG" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);
			
			strAudit.append("STATUS_NT" + auditDelimiterColVal + vObject.getStatus());
			strAudit.append(auditDelimiter);

			strAudit.append("STATUS" + auditDelimiterColVal + vObject.getStatus());
			strAudit.append(auditDelimiter);

			strAudit.append("RECORD_INDICATOR_NT" + auditDelimiterColVal + vObject.getRecordIndicatorNt());
			strAudit.append(auditDelimiter);
			
			if (vObject.getRecordIndicator() == -1)
				vObject.setRecordIndicator(0);
			strAudit.append("RECORD_INDICATOR" + auditDelimiterColVal + vObject.getRecordIndicator());
			strAudit.append(auditDelimiter);
			
			strAudit.append("MAKER" + auditDelimiterColVal + vObject.getMaker());
			strAudit.append(auditDelimiter);
			
			strAudit.append("VERIFIER" + auditDelimiterColVal + vObject.getVerifier());
			strAudit.append(auditDelimiter);

			if (vObject.getDateLastModified() != null && !vObject.getDateLastModified().equalsIgnoreCase(""))
				strAudit.append("DATE_LAST_MODIFIED" + auditDelimiterColVal + vObject.getDateLastModified().trim());
			else
				strAudit.append("DATE_LAST_MODIFIED" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);

			if (vObject.getDateCreation() != null && !vObject.getDateCreation().equalsIgnoreCase(""))
				strAudit.append("DATE_CREATION" + auditDelimiterColVal + vObject.getDateCreation().trim());
			else
				strAudit.append("DATE_CREATION" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);

		} catch (Exception ex) {
			strErrorDesc = ex.getMessage();
			strAudit = strAudit.append(strErrorDesc);
			ex.printStackTrace();
		}
		return strAudit.toString();
	}

	public List<AlphaSubTabVb> getConcessionIdList(ConcessionBlLinkVb vObject) {
		String sql = "";
		if ("MSSQL".equalsIgnoreCase(databaseType)) {
			sql = "SELECT CONCAT(CONCESSION_ID,' - ' ,CONCESSION_DESCRIPTION) ID,format(CAST(EFFECTIVE_DATE AS DATETIME), 'dd-MMM-yyyy') DESCRIPTION FROM RA_MST_CONCESSION_HEADER WHERE Concession_Status = 0 "
					+ " AND COUNTRY = ? AND LE_BOOK = ? " + " ORDER BY CONCESSION_ID ";
		} else if ("ORACLE".equalsIgnoreCase(databaseType)) {
			sql = "SELECT CONCESSION_ID||' - '||CONCESSION_DESCRIPTION) ID,TO_CHAR(EFFECTIVE_DATE,'DD-Mon-RRRR') DESCRIPTION FROM RA_MST_CONCESSION_HEADER WHERE Concession_Status = 0 "
					+ " AND COUNTRY = ? AND LE_BOOK = ? " + " ORDER BY CONCESSION_ID ";
		}
		Object[] args = { vObject.getCountry(), vObject.getLeBook() };
		return getJdbcTemplate().query(sql, args, getfindConcessionMappper());
	}

	protected RowMapper getfindConcessionMappper() {
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				AlphaSubTabVb alphaSubTabVb = new AlphaSubTabVb();
				alphaSubTabVb.setDescription(rs.getString("ID"));
				alphaSubTabVb.setAlphaSubTab(rs.getString("DESCRIPTION"));
				return alphaSubTabVb;
			}
		};
		return mapper;
	}

	public List getActivityIdList(ConcessionBlLinkVb vObject, String concessionId) {
		List<AlphaSubTabVb> activityList = new ArrayList<>();
		try {
			String sql = " SELECT ACTIVITY_ID ID,									"
					+ "        (SELECT ACTIVITY_DESCRIPTION                "
					+ "           FROM RA_Mst_Concession_Activity          "
					+ "          WHERE     COUNTRY = T1.COUNTRY            "
					+ "                AND LE_BOOK = T1.LE_BOOK            "
					+ "                AND ACTIVITY_ID = T1.ACTIVITY_ID)   "
					+ "           DESCRIPTION                              "
					+ "   FROM RA_Mst_Concession_Act_Link T1               "
					+ "  WHERE COUNTRY = ?                                 "
					+ "  AND LE_BOOK = ?                                   " + "  AND CONCESSION_ID = ?";
			Object[] args = { vObject.getCountry(), vObject.getLeBook(), concessionId };
			activityList = getJdbcTemplate().query(sql, args, getActivityMappper());
			return activityList;
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<>();
		}
	}

	protected RowMapper getActivityMappper() {
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				AlphaSubTabVb alphaSubTabVb = new AlphaSubTabVb();
				alphaSubTabVb.setAlphaSubTab(rs.getString("ID"));
				alphaSubTabVb.setDescription(rs.getString("DESCRIPTION"));
				return alphaSubTabVb;
			}
		};
		return mapper;
	}
	
	
	@Override
	@Transactional(rollbackForClassName = { "com.vision.exception.RuntimeCustomException" })
	public ExceptionCode doUpdateApprRecord(List<ConcessionBlLinkVb> vObjects) throws RuntimeCustomException {
		ExceptionCode exceptionCode = new ExceptionCode();
		strApproveOperation = Constants.MODIFY;
		strErrorDesc = "";
		setServiceDefaults();
		List<ConcessionBlLinkVb> collTemp = new ArrayList<>();
		try {
			ConcessionBlLinkVb concessionBlLinkVb = new ConcessionBlLinkVb();
			if (vObjects != null && vObjects.size() > 0) {
				concessionBlLinkVb = vObjects.get(0);
				collTemp = getQueryBlResults(concessionBlLinkVb, Constants.STATUS_ZERO);
				deleteConcessionBlAppr(concessionBlLinkVb);
			}
			for (ConcessionBlLinkVb vObject : vObjects) {
				for (ConcessionBlLinkVb subPriorityVb : vObject.getConcessionPriorityList()) {
					exceptionCode = doInsertApprRecordForNonTrans(subPriorityVb);
					if ( exceptionCode.getErrorCode() == Constants.SUCCESSFUL_OPERATION) {
						exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
						exceptionCode.setErrorMsg("Concession Link Modify Successful");
					}else {
						exceptionCode = getResultObject(exceptionCode.getErrorCode());
						throw buildRuntimeCustomException(exceptionCode);
					}
				}
			}
			return exceptionCode;
		} catch (RuntimeCustomException rcException) {
			throw rcException;
		} catch (UncategorizedSQLException uSQLEcxception) {
			strErrorDesc = parseErrorMsg(uSQLEcxception);
			exceptionCode = getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION);
			throw buildRuntimeCustomException(exceptionCode);
		} catch (Exception ex) {
			logger.error("Error in Modify.", ex);
			strErrorDesc = ex.getMessage();
			exceptionCode = getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION);
			throw buildRuntimeCustomException(exceptionCode);
		}
	}

}
