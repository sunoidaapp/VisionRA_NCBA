package com.vision.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.vision.vb.TaxReconActivityFilterVb;
import com.vision.vb.TaxReconHeaderVb;


@Component
public class TaxReconConfigHeaderDao extends AbstractDao<TaxReconHeaderVb> {

	@Value("${app.databaseType}")
	private String databaseType;
	@Autowired
	CommonDao commonDao;
	@Autowired
	TaxReconColumnDao taxReconColumnDao;
	@Autowired
	TaxReconActivityFilterDao taxReconActivityFilterDao;
	@Autowired
	TaxReconTabRelationDao taxReconTabRelationDao;
	
	@Override
	protected void setServiceDefaults() {
		serviceName = "ReconConfigHeader";
		serviceDesc = "Recon Config Header";
		tableName = "RA_RECON_TAB_TAX";
		childTableName = "RA_RECON_TAB_TAX";
		intCurrentUserId = CustomContextHolder.getContext().getVisionId();
	}
	protected void setStatus(TaxReconHeaderVb vObject, int status) {
		vObject.setReconStatus(status);
	}
	@Override
	protected int getStatus(TaxReconHeaderVb records) {
		return records.getReconStatus();
	}
	public List getReconTableName(TaxReconHeaderVb vObject) {
		List collTemp = new ArrayList();
		String query = "";

		
			query = "SELECT T1.TABLE_NAME,T2.TABLE_ID,T2.ALIAS_NAME                                          "
					+ "FROM VISION_TABLES T1 LEFT OUTER JOIN (select * from RA_RECON_TAB_TAX where RULE_ID = ?) T2 "
					+ "ON (T1.TABLE_NAME= T2.TABLE_NAME)                                                       "
					+ "WHERE T1.TABLE_NAME LIKE 'RA%'                                                          ";
		
		Object[] lParams = new Object[1];
		lParams[0] = vObject.getRuleId();
		try {
			collTemp = getJdbcTemplate().query(query, lParams, getTableMapper());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return collTemp;

	}

	protected RowMapper getTableMapper() {
	RowMapper mapper = new RowMapper() {

		@Override
		public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
			TaxReconHeaderVb ReconVb = new TaxReconHeaderVb();
			ReconVb.setTableName(rs.getString("TABLE_NAME"));
			ReconVb.setTableId(rs.getInt("TABLE_ID"));
			ReconVb.setAliasName(rs.getString("ALIAS_NAME"));
			return ReconVb;
		}

	};
	return mapper;
	}
	public List getReconColName(TaxReconHeaderVb vObject) {
		List collTemp = new ArrayList();
		String query = "";
		if ("ORACLE".equalsIgnoreCase(databaseType)) {
			query = " SELECT COLUMN_NAME , COLUMN_NAME COL_ALIAS_NAME, COLUMN_ID,DATA_TYPE FROM USER_TAB_COLUMNS           "+         
                    " WHERE UPPER(TABLE_NAME) = ? AND                                                                      "+
                    " (COLUMN_NAME NOT IN ('RECORD_INDICATOR','MAKER','VERIFIER','DATE_CREATION','DATE_LAST_MODIFIED') AND "+
                    " SUBSTR(COLUMN_NAME,-3) NOT IN ('_AT','_NT'))                                                         "+
                    " ORDER BY COLUMN_ID                                                                                   ";
		} else if ("MSSQL".equalsIgnoreCase(databaseType)) {
			query = " SELECT COLUMN_NAME ,COLUMN_NAME COL_ALIAS_NAME, ORDINAL_POSITION,DATA_TYPE  FROM INFORMATION_SCHEMA.columns"+                       
					" WHERE TABLE_NAME = ? AND                                                                                   "+             
					" (COLUMN_NAME NOT IN ('RECORD_INDICATOR','MAKER','VERIFIER','DATE_CREATION','DATE_LAST_MODIFIED') AND       "+
					" RIGHT(COLUMN_NAME,3) NOT IN ('_NT','_AT'))                                                                 "+
					" ORDER BY ORDINAL_POSITION                                                                                  ";
		}
		Object[] lParams = new Object[1];
		lParams[0] = vObject.getTableName();
		try {
			collTemp = getJdbcTemplate().query(query, lParams, getColMapper());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return collTemp;

	}
	
	protected RowMapper getColMapper() {
	RowMapper mapper = new RowMapper() {

		@Override
		public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
			TaxReconHeaderVb ReconVb = new TaxReconHeaderVb();
			ReconVb.setColName(rs.getString("COLUMN_NAME"));
			ReconVb.setColAliasName(rs.getString("COL_ALIAS_NAME"));
			ReconVb.setDataType(rs.getString("DATA_TYPE"));
			if(ReconVb.getDataType().equalsIgnoreCase("VARCHAR2")||ReconVb.getDataType().equalsIgnoreCase("VARCHAR"))
				ReconVb.setDataType("T");
			else if(ReconVb.getDataType().equalsIgnoreCase("NUMERIC")
					||ReconVb.getDataType().equalsIgnoreCase("NUMBER")
					||ReconVb.getDataType().equalsIgnoreCase("INT"))
				ReconVb.setDataType("N");
			else if(ReconVb.getDataType().equalsIgnoreCase("DATE")
					||ReconVb.getDataType().equalsIgnoreCase("DATETIME")
					||ReconVb.getDataType().equalsIgnoreCase("DATETIME2"))
				ReconVb.setDataType("D");
		
			return ReconVb;
		}

	};
	return mapper;
	}


	protected int doInsertionApprReconHeaders(TaxReconHeaderVb vObject) {
		String query = " INSERT INTO RA_RECON_TAB_TAX(RULE_ID_AT,RULE_ID,TABLE_ID,TABLE_NAME,ALIAS_NAME,STATUS_NT,STATUS,  "
				+ " RECORD_INDICATOR_NT,RECORD_INDICATOR,MAKER,VERIFIER,INTERNAL_STATUS,DATE_LAST_MODIFIED,DATE_CREATION) "
				+ " VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,  " + getDbFunction("SYSDATE") + ","
				+ getDbFunction("SYSDATE") + ")";
		Object[] args = { vObject.getRuleIdAt(), vObject.getRuleId(), vObject.getTableId(), vObject.getTableName(),
				vObject.getAliasName(), vObject.getReconStatusNt(), vObject.getReconStatus(),
				vObject.getRecordIndicatorNt(), vObject.getRecordIndicator(), vObject.getMaker(), vObject.getVerifier(),
				vObject.getInternalStatus() };
		return getJdbcTemplate().update(query, args);
	}

	protected int doInsertionPendReconHeaders(TaxReconHeaderVb vObject) {
		String query = " INSERT INTO RA_RECON_TAB_TAX_PEND(RULE_ID_AT,RULE_ID,TABLE_ID,TABLE_NAME,ALIAS_NAME,STATUS_NT,STATUS,          "
				+ " RECORD_INDICATOR_NT,RECORD_INDICATOR,MAKER,VERIFIER,INTERNAL_STATUS,DATE_LAST_MODIFIED,DATE_CREATION) "
				+ " VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,  " + getDbFunction("SYSDATE") + ","
				+ getDbFunction("SYSDATE") + ")";
		Object[] args = { vObject.getRuleIdAt(), vObject.getRuleId(), vObject.getTableId(), vObject.getTableName(),
				vObject.getAliasName(), vObject.getReconStatusNt(), vObject.getReconStatus(),
				vObject.getRecordIndicatorNt(), vObject.getRecordIndicator(), vObject.getMaker(), vObject.getVerifier(),
				vObject.getInternalStatus() };
		return getJdbcTemplate().update(query, args);
	}

	protected int doInsertionReconHeadersHis(TaxReconHeaderVb vObject) {
		String query = " INSERT INTO RA_RECON_TAB_TAX_HIS(REF_NO,RULE_ID_AT,RULE_ID,TABLE_ID                    "
				+ " ,TABLE_NAME,ALIAS_NAME,STATUS_NT,STATUS,RECORD_INDICATOR_NT                        "
				+ " ,RECORD_INDICATOR,MAKER,VERIFIER,INTERNAL_STATUS,DATE_LAST_MODIFIED,DATE_CREATION) "
				+ " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " + getDbFunction("SYSDATE") + ","
				+ getDbFunction("SYSDATE") + ")";
		Object[] args = { vObject.getRefNo(), vObject.getRuleIdAt(), vObject.getRuleId(), vObject.getTableId(),
				vObject.getTableName(), vObject.getAliasName(), vObject.getReconStatusNt(), vObject.getReconStatus(),
				vObject.getRecordIndicatorNt(), vObject.getRecordIndicator(), vObject.getMaker(), vObject.getVerifier(),
				vObject.getInternalStatus()};
		return getJdbcTemplate().update(query, args);
	}
	
	protected int doInsertionPendReconHeadersDc(TaxReconHeaderVb vObject) {
		String query = " INSERT INTO RA_RECON_TAB_TAX_PEND(RULE_ID_AT,RULE_ID,TABLE_ID,TABLE_NAME,ALIAS_NAME,STATUS_NT,STATUS,          "
				+ " RECORD_INDICATOR_NT,RECORD_INDICATOR,MAKER,VERIFIER,INTERNAL_STATUS,DATE_LAST_MODIFIED,DATE_CREATION) "
				+ " VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,  " + getDbFunction("SYSDATE") + ",?)";
		Object[] args = { vObject.getRuleIdAt(), vObject.getRuleId(), vObject.getTableId(), vObject.getTableName(),
				vObject.getAliasName(), vObject.getReconStatusNt(), vObject.getReconStatus(),
				vObject.getRecordIndicatorNt(), vObject.getRecordIndicator(), vObject.getMaker(), vObject.getVerifier(),
				vObject.getInternalStatus(), vObject.getDateCreation() };
		return getJdbcTemplate().update(query, args);
	}

	protected int doUpdateApprReconHeaders(TaxReconHeaderVb vObject) {
		String query = "UPDATE RA_RECON_TAB_TAX SET RULE_ID_AT              = ? ,TABLE_NAME             = ?"
				+ " ,ALIAS_NAME             = ? ,STATUS_NT              = ? ,STATUS                 = ?"
				+ " ,RECORD_INDICATOR_NT    = ? ,RECORD_INDICATOR       = ? ,MAKER                  = ?"
				+ " ,VERIFIER               = ? ,INTERNAL_STATUS        = ? ,DATE_LAST_MODIFIED     = "
				+ getDbFunction("SYSDATE") + " ,DATE_CREATION  = ?" + " WHERE RULE_ID  = ? AND TABLE_ID  = ?";
		Object[] args = { vObject.getRuleIdAt(), vObject.getTableName(), vObject.getAliasName(),
				vObject.getReconStatusNt(), vObject.getReconStatus(), vObject.getRecordIndicatorNt(),
				vObject.getRecordIndicator(), vObject.getMaker(), vObject.getVerifier(), vObject.getInternalStatus(),
				vObject.getDateCreation(), vObject.getRuleId(), vObject.getTableId(), };
		return getJdbcTemplate().update(query, args);
	}

	protected int doUpdatePendReconHeaders(TaxReconHeaderVb vObject) {
		String query = "UPDATE RA_RECON_TAB_TAX_PEND SET RULE_ID_AT         = ? ,TABLE_NAME             = ?"
				+ " ,ALIAS_NAME             = ? ,STATUS_NT              = ? ,STATUS                 = ?"
				+ " ,RECORD_INDICATOR_NT    = ? ,RECORD_INDICATOR       = ? ,MAKER                  = ?"
				+ " ,VERIFIER               = ? ,INTERNAL_STATUS        = ? ,DATE_LAST_MODIFIED     = "
				+ getDbFunction("SYSDATE") + " ,DATE_CREATION   = ?"
				+ " WHERE RULE_ID  = ? AND TABLE_ID  = ?";
		Object[] args = { vObject.getRuleIdAt(),  vObject.getTableName(),
				vObject.getAliasName(), vObject.getReconStatusNt(), vObject.getReconStatus(),
				vObject.getRecordIndicatorNt(), vObject.getRecordIndicator(), vObject.getMaker(), vObject.getVerifier(),
				vObject.getInternalStatus(),vObject.getDateCreation(),vObject.getRuleId(), vObject.getTableId(), };
		return getJdbcTemplate().update(query, args);
	}

	protected int deleteApprReconHeaders(TaxReconHeaderVb vObject) {
		String query = "DELETE RA_RECON_TAB_TAX WHERE RULE_ID = ? ";
		Object[] args = { vObject.getRuleId() };
		return getJdbcTemplate().update(query, args);
	}

	protected int deletePendReconHeaders(TaxReconHeaderVb vObject) {
		String query = "DELETE RA_RECON_TAB_TAX_PEND WHERE RULE_ID = ? ";
		Object[] args = { vObject.getRuleId() };
		return getJdbcTemplate().update(query, args);
	}

	public ArrayList<TaxReconHeaderVb> getAllQueryReconTables(TaxReconHeaderVb dObj, int intStatus) {
		ArrayList<TaxReconHeaderVb> collTemp = null;
		setServiceDefaults();
		String strQueryAppr = null;
		String strQueryPend = null;

		strQueryAppr = new String(" 	   SELECT TAPPR.RULE_ID_AT,								"
				+ "       TAPPR.RULE_ID,                                     "
				+ "       TAPPR.TABLE_ID,                                    "
				+ "       TAPPR.TABLE_NAME,                                  "
				+ "       TAPPR.ALIAS_NAME,                                  "
				+ "       TAPPR.STATUS_NT,                                   "
				+ "       TAPPR.STATUS,                                      "
				+ "       T3.NUM_SUBTAB_DESCRIPTION RECON_STATUS_DESC,       "
				+ "       TAPPR.RECORD_INDICATOR_NT,                         "
				+ "       TAPPR.RECORD_INDICATOR,                            "
				+ "       T2.NUM_SUBTAB_DESCRIPTION RECORD_INDICATOR_DESC,   "
				+ "       TAPPR.MAKER,                                       "
				+ "       TAPPR.VERIFIER,                                    "
				+ "       TAPPR.INTERNAL_STATUS,                             "
				+dbFunctionFormats("TAPPR.DATE_LAST_MODIFIED","DATETIME_FORMAT", null)+" DATE_LAST_MODIFIED, "+
				""+dbFunctionFormats("TAPPR.DATE_CREATION","DATETIME_FORMAT", null)+" DATE_CREATION, "
				+ "(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = " + getDbFunction("NVL")
				+ "(TAppr.MAKER,0) ) MAKER_NAME, "
				+ "(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "
				+ getDbFunction("NVL") + "(TAppr.VERIFIER,0) ) VERIFIER_NAME "
				+ "  FROM RA_RECON_TAB_TAX TAPPR, NUM_SUB_TAB T2, NUM_SUB_TAB T3 "
				+ " WHERE     TAPPR.RULE_ID = ?                                 "
				+ "       AND T2.NUM_tab = TAPPR.RECORD_INDICATOR_NT         "
				+ "       AND T2.NUM_sub_tab = TAPPR.RECORD_INDICATOR        "
				+ "       AND T3.NUM_TAB = TAPPR.STATUS_NT                   "
				+ "       AND T3.NUM_SUB_TAB = TAPPR.STATUS                  ");

		strQueryPend = new String(" SELECT TPEND.RULE_ID_AT,                                        "
				+ "       TPEND.RULE_ID,                                           "
				+ "       TPEND.TABLE_ID,                                          "
				+ "       TPEND.TABLE_NAME,                                        "
				+ "       TPEND.ALIAS_NAME,                                        "
				+ "       TPEND.STATUS_NT,                                         "
				+ "       TPEND.STATUS,                                            "
				+ "       T3.NUM_SUBTAB_DESCRIPTION RECON_STATUS_DESC,             "
				+ "       TPEND.RECORD_INDICATOR_NT,                               "
				+ "       TPEND.RECORD_INDICATOR,                                  "
				+ "       T2.NUM_SUBTAB_DESCRIPTION RECORD_INDICATOR_DESC,         "
				+ "       TPEND.MAKER,                                             "
				+ "       TPEND.VERIFIER,                                          "
				+ "       TPEND.INTERNAL_STATUS,                                   "
				+dbFunctionFormats("TPEND.DATE_LAST_MODIFIED","DATETIME_FORMAT", null)+" DATE_LAST_MODIFIED, "+
				""+dbFunctionFormats("TPEND.DATE_CREATION","DATETIME_FORMAT", null)+" DATE_CREATION, "
				+ "(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = " + getDbFunction("NVL")
				+ "(TPEND.MAKER,0) ) MAKER_NAME, "
				+ "(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "
				+ getDbFunction("NVL") + "(TPEND.VERIFIER,0) ) VERIFIER_NAME "
				+ "  FROM RA_RECON_TAB_TAX_PEND TPEND, NUM_SUB_TAB T2, NUM_SUB_TAB T3  "
				+ " WHERE     TPEND.RULE_ID = ?                                        "
				+ "       AND T2.NUM_tab = TPEND.RECORD_INDICATOR_NT               "
				+ "       AND T2.NUM_sub_tab = TPEND.RECORD_INDICATOR              "
				+ "       AND T3.NUM_TAB = TPEND.STATUS_NT                         "
				+ "       AND T3.NUM_SUB_TAB = TPEND.STATUS                        ");
		Object objParams[] = new Object[1];
		objParams[0] = dObj.getRuleId();
		
		try {
			if (intStatus == 0) {
				logger.info("Executing approved query");
				collTemp = (ArrayList<TaxReconHeaderVb>) getJdbcTemplate().query(strQueryAppr.toString(), objParams,
						getReconTabMapper());
			} else {
				logger.info("Executing pending query");
				collTemp = (ArrayList<TaxReconHeaderVb>) getJdbcTemplate().query(strQueryPend.toString(), objParams,
						getReconTabMapper());
			}
			return collTemp;
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("Error: getQueryResults Exception :   ");
			/*if (intStatus == 0)
				logger.error(((strQueryAppr == null) ? "strQueryAppr is Null" : strQueryAppr.toString()));
			else
				logger.error(((strQueryPend == null) ? "strQueryPend is Null" : strQueryPend.toString()));

			if (objParams != null)
				for (int i = 0; i < objParams.length; i++)
					//logger.error("objParams[" + i + "]" + objParams[i].toString());
*/			return null;
		}
	}

	protected RowMapper getReconTabMapper() {
		RowMapper mapper = new RowMapper() {
			@Override
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				TaxReconHeaderVb reconVb = new TaxReconHeaderVb();
				reconVb.setRuleId(rs.getString("RULE_ID"));
				reconVb.setTableId(rs.getInt("TABLE_ID"));
				reconVb.setTableName(rs.getString("TABLE_NAME"));
				reconVb.setAliasName(rs.getString("ALIAS_NAME"));
				reconVb.setRecordIndicator(rs.getInt("RECORD_INDICATOR"));
				reconVb.setRecordIndicatorDesc(rs.getString("RECORD_INDICATOR_DESC"));
				reconVb.setMaker(rs.getInt("MAKER"));
				reconVb.setVerifier(rs.getInt("VERIFIER"));
				reconVb.setMakerName(rs.getString("MAKER_NAME"));
				reconVb.setVerifierName(rs.getString("VERIFIER_NAME"));
				reconVb.setReconStatus(rs.getInt("STATUS"));
				reconVb.setReconStatusDesc(rs.getString("RECON_STATUS_DESC"));
				reconVb.setDateLastModified(rs.getString("DATE_LAST_MODIFIED"));
				reconVb.setDateCreation(rs.getString("DATE_CREATION"));
				return reconVb;
			}
		};
		return mapper;

	}

	protected ExceptionCode doInsertRecordForNonTrans(TaxReconHeaderVb vObject) throws RuntimeCustomException {
		List<TaxReconHeaderVb> collTemp = null;
		ExceptionCode exceptionCode = null;
		strApproveOperation = Constants.ADD;
		strErrorDesc = "";
		strCurrentOperation = Constants.ADD;
		setServiceDefaults();
		vObject.setMaker(getIntCurrentUserId());
		collTemp = getAllQueryReconTables(vObject, Constants.STATUS_ZERO);
		if (collTemp == null) {
			logger.error("Collection is null for Select Approved Record");
			exceptionCode = getResultObject(Constants.ERRONEOUS_OPERATION);
			throw buildRuntimeCustomException(exceptionCode);
		}
		// If record already exists in the approved table, reject the addition
		if (collTemp.size() > 0) {
			int staticDeletionFlag = getStatus(((ArrayList<TaxReconHeaderVb>) collTemp).get(0));
			if (staticDeletionFlag == Constants.PASSIVATE) {
				logger.info("Collection size is greater than zero - Duplicate record found, but inactive");
				exceptionCode = getResultObject(Constants.RECORD_ALREADY_PRESENT_BUT_INACTIVE);
				throw buildRuntimeCustomException(exceptionCode);
			} else {
				logger.info("Collection size is greater than zero - Duplicate record found");
				exceptionCode = getResultObject(Constants.DUPLICATE_KEY_INSERTION);
				throw buildRuntimeCustomException(exceptionCode);
			}
		}

		// Try to see if the record already exists in the pending table, but not in
		// approved table
		collTemp = null;
		collTemp = getAllQueryReconTables(vObject, Constants.STATUS_UPDATE);
		// The collTemp variable could not be null. If so, there is no problem fetching
		// data
		// return back error code to calling routine
		if (collTemp == null) {
			exceptionCode = getResultObject(Constants.ERRONEOUS_OPERATION);
			throw buildRuntimeCustomException(exceptionCode);
		}

		// if record already exists in pending table, modify the record
		if (collTemp.size() > 0) {
			TaxReconHeaderVb vObjectLocal = ((ArrayList<TaxReconHeaderVb>) collTemp).get(0);
			if (vObjectLocal.getRecordIndicator() == Constants.STATUS_INSERT) {
				exceptionCode = getResultObject(Constants.PENDING_FOR_ADD_ALREADY);
				throw buildRuntimeCustomException(exceptionCode);
			}
			exceptionCode = getResultObject(Constants.ERRONEOUS_OPERATION);
			throw buildRuntimeCustomException(exceptionCode);
		} else {
			// Try inserting the record
			vObject.setReconStatus(Constants.STATUS_ZERO);
			vObject.setVerifier(0);
			vObject.setRecordIndicator(Constants.STATUS_INSERT);
			String systemDate = getSystemDate();
			vObject.setDateLastModified(systemDate);
			vObject.setDateCreation(systemDate);
			if(vObject.getTableNamesList() != null && vObject.getTableNamesList().size() > 0) {
				for(TaxReconHeaderVb TaxReconHeaderVb : vObject.getTableNamesList()) {
					TaxReconHeaderVb.setRecordIndicator(vObject.getRecordIndicator());
					TaxReconHeaderVb.setReconStatus(vObject.getReconStatus());
					TaxReconHeaderVb.setMaker(vObject.getMaker());
					TaxReconHeaderVb.setVerifier(vObject.getVerifier());
					retVal = doInsertionPendReconHeaders(TaxReconHeaderVb);
				}
			}
			if (retVal != Constants.SUCCESSFUL_OPERATION) {
				exceptionCode = getResultObject(retVal);
				throw buildRuntimeCustomException(exceptionCode);
			} else {
				exceptionCode = getResultObject(Constants.SUCCESSFUL_OPERATION);
			}
			if(vObject.getSelectionCriteriaLst() != null && vObject.getSelectionCriteriaLst().size() > 0) {
				exceptionCode = taxReconColumnDao.deleteAndInsertPendReconColumn(vObject);
			}
			if (exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION) {
				exceptionCode = getResultObject(retVal);
				throw buildRuntimeCustomException(exceptionCode);
			} else {
				exceptionCode = getResultObject(Constants.SUCCESSFUL_OPERATION);
			}
			//need to check
				exceptionCode = taxReconActivityFilterDao.deleteAndInsertPendRelationFilter(vObject);
			
			if (exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION) {
				exceptionCode = getResultObject(retVal);
				throw buildRuntimeCustomException(exceptionCode);
			} else {
				exceptionCode = getResultObject(Constants.SUCCESSFUL_OPERATION);
			}
			
			if (exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION) {
				exceptionCode = getResultObject(retVal);
				throw buildRuntimeCustomException(exceptionCode);
			}
			exceptionCode = getResultObject(retVal);
			writeAuditLog(vObject, null);
			return exceptionCode;
		}
	}



	protected ExceptionCode doInsertApprRecordForNonTrans(TaxReconHeaderVb vObject) throws RuntimeCustomException {
		List<TaxReconHeaderVb> collTemp = null;
		ExceptionCode exceptionCode = null;
		strCurrentOperation = Constants.ADD;
		strApproveOperation = Constants.ADD;
		setServiceDefaults();
		if ("RUNNING".equalsIgnoreCase(getBuildStatus(vObject))) {
			exceptionCode = getResultObject(Constants.BUILD_IS_RUNNING);
			throw buildRuntimeCustomException(exceptionCode);
		}
		vObject.setMaker(getIntCurrentUserId());
		collTemp = getAllQueryReconTables(vObject, Constants.STATUS_ZERO);
		if (collTemp == null) {
			logger.error("Collection is null for Select Approved Record");
			exceptionCode = getResultObject(Constants.ERRONEOUS_OPERATION);
			throw buildRuntimeCustomException(exceptionCode);
		}
		// If record already exists in the approved table, reject the addition
		if (collTemp.size() > 0) {
			int intStaticDeletionFlag = getStatus(((ArrayList<TaxReconHeaderVb>) collTemp).get(0));
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
		vObject.setReconStatus(Constants.STATUS_ZERO);
		vObject.setRecordIndicator(Constants.STATUS_ZERO);
		vObject.setVerifier(getIntCurrentUserId());
		if(vObject.getTableNamesList() != null && vObject.getTableNamesList().size() > 0) {
			for(TaxReconHeaderVb TaxReconHeaderVb : vObject.getTableNamesList()) {
				TaxReconHeaderVb.setRecordIndicator(vObject.getRecordIndicator());
				TaxReconHeaderVb.setReconStatus(vObject.getReconStatus());
				TaxReconHeaderVb.setMaker(vObject.getMaker());
				TaxReconHeaderVb.setVerifier(vObject.getVerifier());
				retVal = doInsertionApprReconHeaders(TaxReconHeaderVb);
			}
		}
		if (retVal != Constants.SUCCESSFUL_OPERATION) {
			exceptionCode = getResultObject(retVal);
			throw buildRuntimeCustomException(exceptionCode);
		} else {
			exceptionCode = getResultObject(Constants.SUCCESSFUL_OPERATION);
		}
		if(vObject.getSelectionCriteriaLst() != null && vObject.getSelectionCriteriaLst().size() > 0) {
			exceptionCode = taxReconColumnDao.deleteAndInsertApprReconColumn(vObject);
		}
		if (exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION) {
			exceptionCode = getResultObject(retVal);
			throw buildRuntimeCustomException(exceptionCode);
		} else {
			exceptionCode = getResultObject(Constants.SUCCESSFUL_OPERATION);
		}
	
			exceptionCode = taxReconActivityFilterDao.deleteAndInsertApprRelationFilter(vObject);
		
		if (exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION) {
			exceptionCode = getResultObject(retVal);
			throw buildRuntimeCustomException(exceptionCode);
		} else {
			exceptionCode = getResultObject(Constants.SUCCESSFUL_OPERATION);
		}
		
		String systemDate = getSystemDate();
		vObject.setDateLastModified(systemDate);
		vObject.setDateCreation(systemDate);
		exceptionCode = writeAuditLog(vObject, null);
		if (exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION) {
			exceptionCode = getResultObject(Constants.AUDIT_TRAIL_ERROR);
			throw buildRuntimeCustomException(exceptionCode);
		}
		return exceptionCode;
	}


	protected ExceptionCode doUpdateRecordForNonTrans(TaxReconHeaderVb vObject) throws RuntimeCustomException {
		setServiceDefaults();
		List<TaxReconHeaderVb> collTemp = null;
		TaxReconHeaderVb vObjectlocal = null;
		ExceptionCode exceptionCode = null;
		strApproveOperation = Constants.MODIFY;
		strErrorDesc = "";
		strCurrentOperation = Constants.MODIFY;
		setServiceDefaults();
		vObject.setMaker(intCurrentUserId);
		// Search if record already exists in pending. If it already exists, check for
		// status
		collTemp = getAllQueryReconTables(vObject, Constants.SUCCESSFUL_OPERATION);
		if (collTemp == null) {
			exceptionCode = getResultObject(Constants.ERRONEOUS_OPERATION);
			throw buildRuntimeCustomException(exceptionCode);
		}
		if (collTemp.size() > 0) {
			vObjectlocal = ((ArrayList<TaxReconHeaderVb>) collTemp).get(0);

			// Check if the record is pending for deletion. If so return the error
			if (vObjectlocal.getRecordIndicator() == Constants.STATUS_DELETE) {
				exceptionCode = getResultObject(Constants.RECORD_PENDING_FOR_DELETION);
				throw buildRuntimeCustomException(exceptionCode);
			}
			vObject.setDateCreation(vObjectlocal.getDateCreation());
			if (vObject.getTableNamesList() != null && vObject.getTableNamesList().size() > 0) {
				if (vObjectlocal.getRecordIndicator() == Constants.STATUS_INSERT) {
					vObject.setVerifier(0);
					vObject.setRecordIndicator(Constants.STATUS_INSERT);
					collTemp.forEach(table ->{
						table.setRefNo(getMaxSequence());
						doInsertionReconHeadersHis(table);
					});
					
					exceptionCode = deleteAndInsertPendReconHeader(vObject);
				} else {
					vObject.setVerifier(0);
					vObject.setRecordIndicator(Constants.STATUS_UPDATE);
					
					exceptionCode = deleteAndInsertPendReconHeader(vObject);
				}
			}
			if (exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION) {
				exceptionCode = getResultObject(retVal);
				throw buildRuntimeCustomException(exceptionCode);
			}
			if (vObject.getSelectionCriteriaLst() != null && vObject.getSelectionCriteriaLst().size() > 0) {
				exceptionCode = taxReconColumnDao.deleteAndInsertPendReconColumn(vObject);
			}
			if (exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION) {
				exceptionCode = getResultObject(retVal);
				throw buildRuntimeCustomException(exceptionCode);
			} else {
				exceptionCode = getResultObject(Constants.SUCCESSFUL_OPERATION);
			}
			
				exceptionCode = taxReconActivityFilterDao.deleteAndInsertPendRelationFilter(vObject);
			
			if (exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION) {
				exceptionCode = getResultObject(retVal);
				throw buildRuntimeCustomException(exceptionCode);
			} else {
				exceptionCode = getResultObject(Constants.SUCCESSFUL_OPERATION);
			}
			writeAuditLog(vObject, null);
			return getResultObject(Constants.SUCCESSFUL_OPERATION);
		} else {
			collTemp = null;
			collTemp = getAllQueryReconTables(vObject, Constants.STATUS_ZERO);

			if (collTemp == null) {
				exceptionCode = getResultObject(Constants.ERRONEOUS_OPERATION);
				throw buildRuntimeCustomException(exceptionCode);
			}
			// Even if record is not there in Appr. table reject the record
			if (collTemp.size() == 0) {
				exceptionCode = getResultObject(Constants.ATTEMPT_TO_MODIFY_UNEXISTING_RECORD);
				throw buildRuntimeCustomException(exceptionCode);
			}
			// This is required for Audit Trail.
			if (collTemp.size() > 0) {
				vObjectlocal = ((ArrayList<TaxReconHeaderVb>) collTemp).get(0);
				vObject.setDateCreation(vObjectlocal.getDateCreation());
			}
			vObject.setDateCreation(vObjectlocal.getDateCreation());
			// Record is there in approved, but not in pending. So add it to pending
			vObject.setVerifier(0);
			vObject.setRecordIndicator(Constants.STATUS_UPDATE);
			if (vObject.getTableNamesList() != null && vObject.getTableNamesList().size() > 0) {
				for (TaxReconHeaderVb TaxReconHeaderVb : vObject.getTableNamesList()) {
					TaxReconHeaderVb.setRecordIndicator(vObject.getRecordIndicator());
					TaxReconHeaderVb.setReconStatus(vObject.getReconStatus());
					TaxReconHeaderVb.setMaker(vObject.getMaker());
					TaxReconHeaderVb.setVerifier(vObject.getVerifier());
					TaxReconHeaderVb.setDateCreation(vObject.getDateCreation());
					retVal = doInsertionPendReconHeadersDc(TaxReconHeaderVb);
				}
			}
			if (retVal != Constants.SUCCESSFUL_OPERATION) {
				exceptionCode = getResultObject(retVal);
				throw buildRuntimeCustomException(exceptionCode);
			}
			if(vObject.getSelectionCriteriaLst() != null && vObject.getSelectionCriteriaLst().size() > 0) {
				exceptionCode = taxReconColumnDao.deleteAndInsertPendReconColumn(vObject);
			}
			if (exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION) {
				exceptionCode = getResultObject(retVal);
				throw buildRuntimeCustomException(exceptionCode);
			} else {
				exceptionCode = getResultObject(Constants.SUCCESSFUL_OPERATION);
			}
			
				exceptionCode = taxReconActivityFilterDao.deleteAndInsertPendRelationFilter(vObject);
			
			if (exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION) {
				exceptionCode = getResultObject(retVal);
				throw buildRuntimeCustomException(exceptionCode);
			} else {
				exceptionCode = getResultObject(Constants.SUCCESSFUL_OPERATION);
			}
			
	
		
			writeAuditLog(vObject, null);
			return getResultObject(Constants.SUCCESSFUL_OPERATION);
		}
	}

	

	protected ExceptionCode doUpdateApprRecordForNonTrans(TaxReconHeaderVb vObject) throws RuntimeCustomException {
		List<TaxReconHeaderVb> collTemp = null;
		TaxReconHeaderVb vObjectlocal = null;
		ExceptionCode exceptionCode = null;
		strApproveOperation = Constants.MODIFY;
		strErrorDesc = "";
		strCurrentOperation = Constants.MODIFY;
		setServiceDefaults();
		vObject.setMaker(getIntCurrentUserId());
		if ("RUNNING".equalsIgnoreCase(getBuildStatus(vObject))) {
			exceptionCode = getResultObject(Constants.BUILD_IS_RUNNING);
			throw buildRuntimeCustomException(exceptionCode);
		}
		collTemp = getAllQueryReconTables(vObject, Constants.STATUS_ZERO);
		if (collTemp == null) {
			exceptionCode = getResultObject(Constants.ERRONEOUS_OPERATION);
			throw buildRuntimeCustomException(exceptionCode);
		}
		vObjectlocal = ((ArrayList<TaxReconHeaderVb>) collTemp).get(0);
		// Even if record is not there in Appr. table reject the record
		if (collTemp.size() == 0) {
			exceptionCode = getResultObject(Constants.ATTEMPT_TO_MODIFY_UNEXISTING_RECORD);
			throw buildRuntimeCustomException(exceptionCode);
		}
		vObject.setRecordIndicator(Constants.STATUS_ZERO);
		vObject.setVerifier(getIntCurrentUserId());
		vObject.setDateCreation(vObjectlocal.getDateCreation());
		int maxRefNumber = getMaxSequence();
		vObject.setRefNo(maxRefNumber);
		collTemp.forEach(table ->{
			table.setRefNo(maxRefNumber);
			doInsertionReconHeadersHis(table);
		});
		if(vObject.getTableNamesList() != null && vObject.getTableNamesList().size() > 0) {
			for(TaxReconHeaderVb TaxReconHeaderVb : vObject.getTableNamesList()) {
				TaxReconHeaderVb.setRecordIndicator(vObject.getRecordIndicator());
				TaxReconHeaderVb.setReconStatus(vObject.getReconStatus());
				TaxReconHeaderVb.setMaker(vObject.getMaker());
				TaxReconHeaderVb.setVerifier(vObject.getVerifier());
				retVal = doUpdateApprReconHeaders(TaxReconHeaderVb);
			}
		}
		if (retVal != Constants.SUCCESSFUL_OPERATION) {
			exceptionCode = getResultObject(retVal);
			throw buildRuntimeCustomException(exceptionCode);
		} else {
			exceptionCode = getResultObject(Constants.SUCCESSFUL_OPERATION);
		}
		if(vObject.getSelectionCriteriaLst() != null && vObject.getSelectionCriteriaLst().size() > 0) {
			exceptionCode = taxReconColumnDao.deleteAndInsertApprReconColumn(vObject);
		}
		if (exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION) {
			exceptionCode = getResultObject(retVal);
			throw buildRuntimeCustomException(exceptionCode);
		} else {
			exceptionCode = getResultObject(Constants.SUCCESSFUL_OPERATION);
		}
	
		exceptionCode = taxReconActivityFilterDao.deleteAndInsertApprRelationFilter(vObject);
		
		if (exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION) {
			exceptionCode = getResultObject(retVal);
			throw buildRuntimeCustomException(exceptionCode);
		} else {
			exceptionCode = getResultObject(Constants.SUCCESSFUL_OPERATION);
		}
		
		String systemDate = getSystemDate();
		vObject.setDateLastModified(systemDate);
		exceptionCode = writeAuditLog(vObject, vObjectlocal);
		if (exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION) {
			exceptionCode = getResultObject(Constants.AUDIT_TRAIL_ERROR);
			throw buildRuntimeCustomException(exceptionCode);
		}
		return exceptionCode;
	}
	protected ExceptionCode doDeleteRecordForNonTrans(TaxReconHeaderVb vObject) throws RuntimeCustomException {
		TaxReconHeaderVb vObjectlocal = null;
		ArrayList<TaxReconHeaderVb> collTemp = null;
		ExceptionCode exceptionCode = null;
		strApproveOperation = Constants.DELETE;
		strErrorDesc  = "";
		strCurrentOperation = Constants.DELETE;
		setServiceDefaults();
		collTemp = getAllQueryReconTables(vObject, Constants.STATUS_ZERO);

		if (collTemp == null) {
			logger.error("Collection is null");
			exceptionCode = getResultObject(Constants.ERRONEOUS_OPERATION);
			throw buildRuntimeCustomException(exceptionCode);
		}
		// If record already exists in the approved table, reject the addition
		if (collTemp.size() > 0) {
			vObjectlocal = ((ArrayList<TaxReconHeaderVb>) collTemp).get(0);
			vObjectlocal.setTableNamesList(collTemp);
			int intStaticDeletionFlag = getStatus(vObjectlocal);
			if (intStaticDeletionFlag == Constants.PASSIVATE){
				exceptionCode = getResultObject(Constants.CANNOT_DELETE_AN_INACTIVE_RECORD);
				throw buildRuntimeCustomException(exceptionCode);
			}
		}
		else{
			exceptionCode = getResultObject(Constants.ATTEMPT_TO_DELETE_UNEXISTING_RECORD);
			throw buildRuntimeCustomException(exceptionCode);
		}

		// check to see if the record already exists in the pending table
		collTemp = getAllQueryReconTables(vObject, Constants.SUCCESSFUL_OPERATION);
		if (collTemp == null) {
			exceptionCode = getResultObject(Constants.ERRONEOUS_OPERATION);
			throw buildRuntimeCustomException(exceptionCode);
		}

		// If records are there, check for the status and decide what error to return back
		if (collTemp.size() > 0){
			exceptionCode = getResultObject(Constants.TRYING_TO_DELETE_APPROVAL_PENDING_RECORD);
			throw buildRuntimeCustomException(exceptionCode);
		}

		// insert the record into pending table with status 3 - deletion
		if(vObjectlocal==null){
			exceptionCode = getResultObject(Constants.ERRONEOUS_OPERATION);
			throw buildRuntimeCustomException(exceptionCode);
		}
		if (collTemp.size() > 0){
		    vObjectlocal=((ArrayList<TaxReconHeaderVb>)collTemp).get(0);
		    vObjectlocal.setDateCreation(vObject.getDateCreation());
		}
		//vObjectlocal.setDateCreation(vObject.getDateCreation());
		vObjectlocal.setMaker(getIntCurrentUserId());
		vObjectlocal.setRecordIndicator(Constants.STATUS_DELETE);
		vObjectlocal.setVerifier(0);
		if(vObjectlocal.getTableNamesList() != null && vObjectlocal.getTableNamesList().size() > 0) {
			for(TaxReconHeaderVb TaxReconHeaderVb : vObjectlocal.getTableNamesList()) {
				TaxReconHeaderVb.setRecordIndicator(vObjectlocal.getRecordIndicator());
				TaxReconHeaderVb.setReconStatus(vObjectlocal.getReconStatus());
				TaxReconHeaderVb.setMaker(vObjectlocal.getMaker());
				TaxReconHeaderVb.setVerifier(vObjectlocal.getVerifier());
				retVal = doInsertionPendReconHeadersDc(TaxReconHeaderVb);
			}
		}
		if (retVal != Constants.SUCCESSFUL_OPERATION) {
			exceptionCode = getResultObject(retVal);
			throw buildRuntimeCustomException(exceptionCode);
		}
		vObject.setRecordIndicator(Constants.STATUS_DELETE);
		vObject.setVerifier(0);
		// fetching all the sub records from concern tables
		List<TaxReconHeaderVb> selectionCriteriaLst = taxReconColumnDao.getQueryReconColumns(vObjectlocal,Constants.STATUS_ZERO);
		List<TaxReconActivityFilterVb> relationFilterLst = taxReconActivityFilterDao.getQueryReconActFilter(vObjectlocal,Constants.STATUS_ZERO);
		List<TaxReconActivityFilterVb> relationshipLst = taxReconTabRelationDao.getQueryReconTabRelation(vObjectlocal,Constants.STATUS_ZERO,false);
		List<TaxReconActivityFilterVb> relationshipFilterLst = taxReconTabRelationDao.getQueryReconTabRelation(vObjectlocal,Constants.STATUS_ZERO,true);
		if (selectionCriteriaLst != null && selectionCriteriaLst.size() > 0) {
			selectionCriteriaLst.forEach(sel -> {
				sel.setRecordIndicator(vObject.getRecordIndicator());
				retVal = taxReconColumnDao.doInsertionPendReconColumn(sel);
			});
		}
		if(relationFilterLst != null && relationFilterLst.size() > 0) {
			relationFilterLst.forEach(fil -> {
				fil.setRecordIndicator(vObject.getRecordIndicator());
				retVal = taxReconActivityFilterDao.doInsertionPendReconActFilter(fil);
			});
		}
		if(relationshipLst != null && relationshipLst.size() > 0) {
			relationshipLst.forEach(rel -> {
				rel.setRecordIndicator(vObject.getRecordIndicator());
				retVal = taxReconTabRelationDao.doInsertionPendReconTabRelation(rel);
			});
		}
		/*if(relationshipFilterLst != null && relationshipFilterLst.size() > 0) {
			relationshipFilterLst.forEach(rel -> {
				rel.setRecordIndicator(vObject.getRecordIndicator());
				retVal = taxReconTabRelationDao.doInsertionPendReconTabRelation(rel);
			});
		}*/
		if (retVal != Constants.SUCCESSFUL_OPERATION) {
			exceptionCode = getResultObject(retVal);
			throw buildRuntimeCustomException(exceptionCode);
		}
		
		return getResultObject(Constants.SUCCESSFUL_OPERATION);
	}
	protected ExceptionCode doDeleteApprRecordForNonTrans(TaxReconHeaderVb vObject) throws RuntimeCustomException {
		List<TaxReconHeaderVb> collTemp = null;
		ExceptionCode exceptionCode = null;
		strApproveOperation = Constants.DELETE;
		strErrorDesc  = "";
		strCurrentOperation = Constants.DELETE;
		setServiceDefaults();
		TaxReconHeaderVb vObjectlocal = null;
		vObject.setMaker(getIntCurrentUserId());
		if("RUNNING".equalsIgnoreCase(getBuildStatus(vObject))){
			exceptionCode = getResultObject(Constants.BUILD_IS_RUNNING);
			throw buildRuntimeCustomException(exceptionCode);
		}
		collTemp = getAllQueryReconTables(vObject, Constants.STATUS_ZERO);
		if (collTemp == null) {
			exceptionCode = getResultObject(Constants.ERRONEOUS_OPERATION);
			throw buildRuntimeCustomException(exceptionCode);
		}
		// If record already exists in the approved table, reject the addition
		if (collTemp.size() > 0 ){
			int intStaticDeletionFlag = getStatus(((ArrayList<TaxReconHeaderVb>)collTemp).get(0));
			if (intStaticDeletionFlag == Constants.PASSIVATE){
				exceptionCode = getResultObject(Constants.CANNOT_DELETE_AN_INACTIVE_RECORD);
				throw buildRuntimeCustomException(exceptionCode);
			}
		}
		else{
			exceptionCode = getResultObject(Constants.ATTEMPT_TO_DELETE_UNEXISTING_RECORD);
			throw buildRuntimeCustomException(exceptionCode);
		}
		vObjectlocal = ((ArrayList<TaxReconHeaderVb>)collTemp).get(0);
		vObject.setDateCreation(vObjectlocal.getDateCreation());
		if(vObject.isStaticDelete()){
			vObjectlocal.setMaker(getIntCurrentUserId());
			vObject.setVerifier(getIntCurrentUserId());
			vObject.setRecordIndicator(Constants.STATUS_ZERO);
//			setStatus(vObject, Constants.PASSIVATE);
			setStatus(vObjectlocal, Constants.PASSIVATE);
			vObjectlocal.setVerifier(getIntCurrentUserId());
			vObjectlocal.setRecordIndicator(Constants.STATUS_ZERO);
			if (vObjectlocal.getTableNamesList() != null && vObjectlocal.getTableNamesList().size() > 0) {
				for (TaxReconHeaderVb TaxReconHeaderVb : vObjectlocal.getTableNamesList()) {
					TaxReconHeaderVb.setRecordIndicator(vObjectlocal.getRecordIndicator());
					TaxReconHeaderVb.setReconStatus(vObjectlocal.getReconStatus());
					TaxReconHeaderVb.setMaker(vObjectlocal.getMaker());
					TaxReconHeaderVb.setVerifier(vObjectlocal.getVerifier());
					retVal = doUpdateApprReconHeaders(TaxReconHeaderVb);
				}
			}
			String systemDate = getSystemDate();
			vObject.setDateLastModified(systemDate);

			List<TaxReconHeaderVb> selectionCriteriaLst = taxReconColumnDao.getQueryReconColumns(vObjectlocal,Constants.SUCCESSFUL_OPERATION);
			List<TaxReconActivityFilterVb> relationFilterLst = taxReconActivityFilterDao.getQueryReconActFilter(vObjectlocal,Constants.SUCCESSFUL_OPERATION);
			List<TaxReconActivityFilterVb> relationshipLst = taxReconTabRelationDao.getQueryReconTabRelation(vObjectlocal,Constants.SUCCESSFUL_OPERATION,false);

			if (selectionCriteriaLst != null && selectionCriteriaLst.size() > 0) {
				exceptionCode = taxReconColumnDao.deleteAndInsertApprReconColumn(vObject);
			}
			if(relationFilterLst != null && relationFilterLst.size() > 0 ) {
				exceptionCode = taxReconActivityFilterDao.deleteAndInsertApprRelationFilter(vObject);
			}
			
		}else{
			//delete the record from the Approve Table
			retVal = deleteApprReconHeaders(vObject);
//			vObject.setRecordIndicator(-1);
			String systemDate = getSystemDate();
			vObject.setDateLastModified(systemDate);
			List<TaxReconHeaderVb> selectionCriteriaLst = taxReconColumnDao.getQueryReconColumns(vObjectlocal,Constants.STATUS_ZERO);
			List<TaxReconActivityFilterVb> relationActFilterLst = taxReconActivityFilterDao.getQueryReconActFilter(vObjectlocal,Constants.STATUS_ZERO);
			List<TaxReconActivityFilterVb> relationshipJoinsLst = taxReconTabRelationDao.getQueryReconTabRelation(vObjectlocal,Constants.STATUS_ZERO,false);
			if (selectionCriteriaLst != null && selectionCriteriaLst.size() > 0) {
				int retVal = taxReconColumnDao.deleteApprReconColumn(vObject);
			}
			if(relationActFilterLst != null && relationActFilterLst.size() > 0 ) {
				int retVal = taxReconActivityFilterDao.deleteApprReconActFilter(vObject);
			}
			if(relationshipJoinsLst != null && relationshipJoinsLst.size() > 0 ) {
				int retVal = taxReconTabRelationDao.deleteApprReconTabRelation(vObject);
			}
			//Move to History
			moveDatatoHistory(collTemp,selectionCriteriaLst,relationActFilterLst,relationshipJoinsLst);
		}
		
		if(vObject.isStaticDelete()){
			setStatus(vObjectlocal, Constants.STATUS_ZERO);
			setStatus(vObject, Constants.PASSIVATE);
			exceptionCode = writeAuditLog(vObject,vObjectlocal);
		}else{
			exceptionCode = writeAuditLog(null,vObject);
			vObject.setRecordIndicator(-1);
		}
		if(exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION){
			exceptionCode = getResultObject(Constants.AUDIT_TRAIL_ERROR);
			throw buildRuntimeCustomException(exceptionCode);
		}
		return exceptionCode;
	}
	@Transactional(rollbackForClassName={"com.vision.exception.RuntimeCustomException"})
	public ExceptionCode bulkApprove(List<TaxReconHeaderVb> vObjects,boolean staticDelete)throws RuntimeCustomException {
		strErrorDesc  = "";
		strCurrentOperation = Constants.APPROVE;
		ExceptionCode exceptionCode = null;
		setServiceDefaults();
		try {
			boolean foundFlag = false;
			for(TaxReconHeaderVb object : vObjects){
				if (object.getRecordIndicator() > 0 && object.isChecked()){
					foundFlag = true;
					strErrorDesc = frameErrorMessage(object, Constants.APPROVE);
					exceptionCode = doApproveRecord(object,staticDelete);
					if (exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION){
						int index =exceptionCode.getErrorMsg().trim().indexOf(serviceDesc+" - Approve - Failed -");
						if(index >=0 ){
							strErrorDesc +=" "+exceptionCode.getErrorMsg().replaceFirst(serviceDesc+" - Approve - Failed -", "-");
						}else{
							strErrorDesc +=" "+exceptionCode.getErrorMsg();
						}
						break;
					}
				}
			}
			if (foundFlag == false){
				logger.error("No Records To Approve");
				exceptionCode = getResultObject(Constants.NO_RECORDS_TO_APPROVE);
				throw buildRuntimeCustomException(exceptionCode);
			}
			// When it has come out of the loop, check whether it has exited successfully or with error
			if (exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION){
				logger.error("Error in Bulk Approve. "+exceptionCode.getErrorMsg());
				exceptionCode = getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION);
				throw buildRuntimeCustomException(exceptionCode);
			}
			return exceptionCode;
		}catch (UncategorizedSQLException uSQLEcxception) {
			strErrorDesc = parseErrorMsg(uSQLEcxception);
			exceptionCode = getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION);
			throw buildRuntimeCustomException(exceptionCode);
		}catch(Exception ex){
			//logger.error("Error in Bulk Approve.",ex);
			strErrorDesc = ex.getMessage();
			exceptionCode = getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION);
			throw buildRuntimeCustomException(exceptionCode);
		}
	}

	public ExceptionCode doApproveRecord(TaxReconHeaderVb vObject, boolean staticDelete) throws RuntimeCustomException {

		TaxReconHeaderVb oldContents = null;
		TaxReconHeaderVb oldContentscol = null;
		TaxReconActivityFilterVb oldContentfil = null;
		TaxReconActivityFilterVb oldContentsRelation = null;
		TaxReconActivityFilterVb oldContentsRelationFilter = null;

		TaxReconHeaderVb vObjectlocal = null;
		TaxReconHeaderVb vObjectlocalCol = null;
		TaxReconActivityFilterVb vObjectlocalFil = null;
		TaxReconActivityFilterVb vObjectlocalRelation = null;
		TaxReconActivityFilterVb vObjectlocalRelationFilter = null;

		ArrayList<TaxReconHeaderVb> collTemp = null;

		List<TaxReconHeaderVb> collTempCol = null;
		List<TaxReconHeaderVb> collTempColAppr = null;
		
		List<TaxReconActivityFilterVb> collTempFil = null;
		List<TaxReconActivityFilterVb> collTempActFilAppr = null;

		List<TaxReconActivityFilterVb> collTempRelation = null;
		//List<TaxReconActivityFilterVb> collTempRelationFilter = null;
		List<TaxReconActivityFilterVb> collTempRelJoinsAppr = null;
		//List<TaxReconActivityFilterVb> collTempRelationFilterAppr = null;
		
		ExceptionCode exceptionCode = null;
		strCurrentOperation = Constants.APPROVE;
		setServiceDefaults();
		int maxRefNo = getMaxSequence(); 
		try {
			
			vObject.setVerifier(getIntCurrentUserId());
			vObject.setRecordIndicator(Constants.STATUS_ZERO);
			collTemp = getAllQueryReconTables(vObject, Constants.SUCCESSFUL_OPERATION);
			if (collTemp == null) {
				exceptionCode = getResultObject(Constants.ERRONEOUS_OPERATION);
				throw buildRuntimeCustomException(exceptionCode);
			}

			if (collTemp.size() == 0){
				exceptionCode = getResultObject(Constants.NO_SUCH_PENDING_RECORD);
				throw buildRuntimeCustomException(exceptionCode);
			}

			vObjectlocal = ((ArrayList<TaxReconHeaderVb>) collTemp).get(0);
			vObjectlocal.setTableNamesList(collTemp);
			if (vObjectlocal.getMaker() == getIntCurrentUserId()) {
				exceptionCode = getResultObject(Constants.MAKER_CANNOT_APPROVE);
				throw buildRuntimeCustomException(exceptionCode);
			}
			if(vObject.getRecordIndicator() == 1) {
				collTemp = getAllQueryReconTables(vObjectlocal, Constants.STATUS_PENDING);
				if (collTemp != null && collTemp.size() > 0) {
					exceptionCode = getResultObject(Constants.ERRONEOUS_OPERATION);
					exceptionCode.setErrorMsg("Recon Header is in Pending,Kindly Approve");
					throw buildRuntimeCustomException(exceptionCode);
				}
			}else {
				
				collTempCol = taxReconColumnDao.getQueryReconColumns(vObjectlocal, Constants.SUCCESSFUL_OPERATION);
				if(collTempCol != null && collTempCol.size() > 0) {
					vObjectlocalCol = collTempCol.get(0);
				}
				
				collTempFil = taxReconActivityFilterDao.getQueryReconActFilter(vObjectlocal,  Constants.SUCCESSFUL_OPERATION);
				if(collTempFil != null && collTempFil.size() > 0) {
					vObjectlocalFil = collTempFil.get(0);
				}
				collTempRelation = taxReconTabRelationDao.getQueryReconTabRelation(vObjectlocal,Constants.SUCCESSFUL_OPERATION,false);
				if (collTempRelation != null && collTempRelation.size() > 0) {
					vObjectlocalRelation = collTempRelation.get(0);
				}
				/*collTempRelationFilter = taxReconTabRelationDao.getQueryReconTabRelation(vObjectlocal,Constants.SUCCESSFUL_OPERATION,true);
				if (collTempRelationFilter != null && collTempRelationFilter.size() > 0) {
					vObjectlocalRelationFilter = collTempRelationFilter.get(0);
				}*/
				// If it's NOT addition, collect the existing record contents from the
				// Approved table and keep it aside, for writing audit information later.
				if (vObjectlocal.getRecordIndicator() != Constants.STATUS_INSERT){
					collTemp = getAllQueryReconTables(vObject, Constants.STATUS_ZERO);
					if (collTemp == null || collTemp.isEmpty()){
						exceptionCode = getResultObject(Constants.ERRONEOUS_OPERATION);
						throw buildRuntimeCustomException(exceptionCode);
					}
					oldContents = ((ArrayList<TaxReconHeaderVb>)collTemp).get(0);
					
					collTempColAppr = taxReconColumnDao.getQueryReconColumns(vObjectlocal, Constants.STATUS_ZERO);
					if(collTempColAppr != null && collTempColAppr.size() > 0) {
						oldContentscol = collTempColAppr.get(0);
					}
					
					collTempActFilAppr = taxReconActivityFilterDao.getQueryReconActFilter(vObjectlocal, Constants.STATUS_ZERO);
					if(collTempActFilAppr != null && collTempActFilAppr.size() > 0) {
						oldContentfil = collTempActFilAppr.get(0);
					}
					collTempRelJoinsAppr = taxReconTabRelationDao.getQueryReconTabRelation(vObjectlocal,Constants.STATUS_ZERO,false);
					if (collTempRelJoinsAppr != null && collTempRelJoinsAppr.size() > 0) {
						oldContentsRelation = collTempRelJoinsAppr.get(0);
					}
					/*collTempRelationFilterAppr = taxReconTabRelationDao.getQueryReconTabRelation(vObjectlocal,Constants.STATUS_ZERO,true);
					if (collTempRelationFilterAppr != null && collTempRelationFilterAppr.size() > 0) {
						oldContentsRelationFilter = collTempRelationFilterAppr.get(0);
					}*/
				}
				if (vObjectlocal.getRecordIndicator() == Constants.STATUS_INSERT) { // Add authorization
					// Write the contents of the Pending table record to the Approved table
					vObjectlocal.setRecordIndicator(Constants.STATUS_ZERO);
					vObjectlocal.setVerifier(getIntCurrentUserId());
					if(vObjectlocal.getTableNamesList() != null && vObjectlocal.getTableNamesList().size() > 0) {
						for(TaxReconHeaderVb TaxReconHeaderVb : vObjectlocal.getTableNamesList()) {
							TaxReconHeaderVb.setRecordIndicator(vObjectlocal.getRecordIndicator());
							TaxReconHeaderVb.setReconStatus(vObjectlocal.getReconStatus());
							TaxReconHeaderVb.setMaker(vObjectlocal.getMaker());
							TaxReconHeaderVb.setVerifier(vObjectlocal.getVerifier());
							retVal = doInsertionApprReconHeaders(TaxReconHeaderVb);
						}
					}
					if (retVal != Constants.SUCCESSFUL_OPERATION) {
						exceptionCode = getResultObject(retVal);
						throw buildRuntimeCustomException(exceptionCode);
					}
					if(collTempColAppr != null && collTempColAppr.size() > 0) {
						taxReconColumnDao.deleteApprReconColumn(vObjectlocal);
					}
					if(collTempCol != null && collTempCol.size() > 0 ) {
						collTempCol.forEach(SelectionCriteria ->{
							SelectionCriteria.setRuleId(vObject.getRuleId());
							SelectionCriteria.setRecordIndicator(vObject.getRecordIndicator());
							SelectionCriteria.setMaker(vObject.getMaker());
							retVal = taxReconColumnDao.doInsertionApprReconColumn(SelectionCriteria);
						});
					}
					if(collTempActFilAppr != null && collTempActFilAppr.size() > 0) {
						taxReconActivityFilterDao.deleteApprReconActFilter(vObjectlocal);
					}
					if(collTempFil != null && collTempFil.size() > 0) {
						collTempFil.forEach(filter ->{
							filter.setRuleId(vObject.getRuleId());
							filter.setRecordIndicator(vObject.getRecordIndicator());
							filter.setVerifier(vObject.getVerifier());
							retVal = taxReconActivityFilterDao.doInsertionApprReconActFilter(filter);
						});
					}
					if(collTempRelJoinsAppr != null && collTempRelJoinsAppr.size() > 0) {
						taxReconTabRelationDao.deleteApprReconTabRelation(vObjectlocal);
					}
					if(collTempRelation != null && collTempRelation.size() > 0) {
						collTempRelation.forEach(relationTab ->{
							relationTab.setRuleId(vObject.getRuleId());
							relationTab.setRecordIndicator(vObject.getRecordIndicator());
							relationTab.setVerifier(vObject.getVerifier());
							retVal = taxReconTabRelationDao.doInsertionApprReconTabRelation(relationTab);
						});
					}
					/*if(collTempRelationFilterAppr != null && collTempRelationFilterAppr.size() > 0) {
						taxReconTabRelationDao.deleteApprReconTabRelation(vObjectlocal);
					}
					if(collTempRelationFilter != null && collTempRelationFilter.size() > 0 ) {
						collTempRelationFilter.forEach(relationFilter ->{
							relationFilter.setRuleId(vObject.getRuleId());
							relationFilter.setRecordIndicator(vObject.getRecordIndicator());
							relationFilter.setVerifier(vObject.getVerifier());
							retVal = taxReconTabRelationDao.doInsertionApprReconTabRelation(relationFilter);
						});
					}*/
					String systemDate = getSystemDate();
					vObject.setDateLastModified(systemDate);
					vObject.setDateCreation(systemDate);
					strApproveOperation = Constants.ADD;
				}else if (vObjectlocal.getRecordIndicator() == Constants.STATUS_UPDATE){ // Modify authorization
	               collTemp = getAllQueryReconTables(vObject, Constants.STATUS_ZERO);
					if (collTemp == null || collTemp.isEmpty()){
						exceptionCode = getResultObject(Constants.ERRONEOUS_OPERATION);
						throw buildRuntimeCustomException(exceptionCode);
					}	

					// If record already exists in the approved table, reject the addition
					if (collTemp.size() > 0 ){
						//retVal = doUpdateAppr(vObjectlocal, MISConstants.ACTIVATE);
						vObjectlocal.setVerifier(getIntCurrentUserId());
						vObjectlocal.setRecordIndicator(Constants.STATUS_ZERO);
						//Move to History
						moveDatatoHistory(collTemp,collTempColAppr,collTempActFilAppr,collTempRelJoinsAppr);
						
						if(vObjectlocal.getTableNamesList() != null && vObjectlocal.getTableNamesList().size() > 0) {
							exceptionCode = deleteAndInsertApprReconTable(vObjectlocal);
						}
					}
					// Modify the existing contents of the record in Approved table
					if (exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION) {
						exceptionCode = getResultObject(retVal);
						throw buildRuntimeCustomException(exceptionCode);
					}
					//delete Old Contents
					taxReconColumnDao.deleteApprReconColumn(vObjectlocal);
					taxReconActivityFilterDao.deleteApprReconActFilter(vObjectlocal);
					taxReconTabRelationDao.deleteApprReconTabRelation(vObjectlocal);
					//Insert into Recon Column
					if(collTempCol != null && collTempCol.size() > 0 ) {
						collTempCol.forEach(col ->{
							col.setRecordIndicator(vObject.getRecordIndicator());
							retVal = taxReconColumnDao.doInsertionApprReconColumn(col);
						});
					}
					//Insert into Recon Act Filter
					if(collTempFil != null && collTempFil.size() > 0) {
						collTempFil.forEach(fil ->{
							fil.setRecordIndicator(vObject.getRecordIndicator());
							retVal = taxReconActivityFilterDao.doInsertionApprReconActFilter(fil);
						});
					}
					//Insert into Recon Tab Relations Joins
					if(collTempRelation != null && collTempRelation.size() > 0) {
						collTempRelation.forEach(rel ->{
							rel.setRecordIndicator(vObject.getRecordIndicator());
							retVal = taxReconTabRelationDao.doInsertionApprReconTabRelation(rel);
						});
					}
					String systemDate = getSystemDate();
					vObject.setDateLastModified(systemDate);
					//Move to History
					moveDatatoHistory(collTemp,collTempColAppr,collTempActFilAppr,collTempRelJoinsAppr);
					// Set the current operation to write to audit log
					strApproveOperation = Constants.MODIFY;
				}
				else if (vObjectlocal.getRecordIndicator() == Constants.STATUS_DELETE){ // Delete authorization
					if(staticDelete){
						// Update the existing record status in the Approved table to delete 
						setStatus(vObjectlocal, Constants.PASSIVATE);
						vObjectlocal.setRecordIndicator(Constants.STATUS_ZERO);
						vObjectlocal.setVerifier(getIntCurrentUserId());
						if(vObjectlocal.getTableNamesList() != null && vObjectlocal.getTableNamesList().size() > 0) {
							for(TaxReconHeaderVb TaxReconHeaderVb : vObjectlocal.getTableNamesList()) {
								TaxReconHeaderVb.setRecordIndicator(vObjectlocal.getRecordIndicator());
								TaxReconHeaderVb.setReconStatus(vObjectlocal.getReconStatus());
								TaxReconHeaderVb.setMaker(vObjectlocal.getMaker());
								TaxReconHeaderVb.setVerifier(vObjectlocal.getVerifier());
								exceptionCode = deleteAndInsertApprReconTable(TaxReconHeaderVb);
							}
						}
						if (exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION) {
							exceptionCode = getResultObject(retVal);
							throw buildRuntimeCustomException(exceptionCode);
						}
						setStatus(vObject, Constants.PASSIVATE);
						String systemDate = getSystemDate();
						vObject.setDateLastModified(systemDate);

					}else{
						// Delete the existing record from the Approved table 
						retVal = deleteApprReconHeaders(vObjectlocal);
						retVal = taxReconColumnDao.deleteApprReconColumn(vObjectlocal);
						retVal = taxReconActivityFilterDao.deleteApprReconActFilter(vObjectlocal);
						retVal = taxReconTabRelationDao.deleteApprReconTabRelation(vObjectlocal);
						String systemDate = getSystemDate();
						vObject.setDateLastModified(systemDate);
						//Move to History
						moveDatatoHistory(collTemp,collTempColAppr,collTempActFilAppr,collTempRelJoinsAppr);
					}
					// Set the current operation to write to audit log
					strApproveOperation = Constants.DELETE;
				}
				else{
					exceptionCode = getResultObject(Constants.INVALID_STATUS_FLAG_IN_DATABASE);
					throw buildRuntimeCustomException(exceptionCode);
				}	

				// Delete the record from the Pending table
				retVal = deletePendReconHeaders(vObjectlocal);
				retVal = taxReconColumnDao.deletePendReconColumn(vObjectlocal);
				retVal = taxReconActivityFilterDao.deletePendReconActFilter(vObjectlocal);
				retVal = taxReconTabRelationDao.deletePendReconTabRelation(vObjectlocal);

				/*if (retVal == Constants.STATUS_ZERO) {
					exceptionCode = getResultObject(retVal);
					throw buildRuntimeCustomException(exceptionCode);
				}*/

				// Set the internal status to Approved
				vObject.setInternalStatus(0);
				vObject.setRecordIndicator(Constants.STATUS_ZERO);
				if (vObjectlocal.getRecordIndicator() == Constants.STATUS_DELETE && !staticDelete){
					exceptionCode = writeAuditLog(null, oldContents);
					vObject.setRecordIndicator(-1);
				} else
					exceptionCode = writeAuditLog(vObjectlocal, oldContents);

				if(exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION){
					exceptionCode = getResultObject(Constants.AUDIT_TRAIL_ERROR);
					throw buildRuntimeCustomException(exceptionCode);
				}
				
			}
			return getResultObject(Constants.SUCCESSFUL_OPERATION);
		}catch (UncategorizedSQLException uSQLEcxception) {
			strErrorDesc = parseErrorMsg(uSQLEcxception);
			exceptionCode = getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION);
			throw buildRuntimeCustomException(exceptionCode);
		}catch(Exception ex){
			//logger.error("Error in Approve.",ex);
			//logger.error( ((vObject==null)? "vObject is Null":vObject.toString()));
			strErrorDesc = ex.getMessage();
			exceptionCode = getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION);
			throw buildRuntimeCustomException(exceptionCode);
		}
	}
	@Override
	@Transactional(rollbackForClassName = { "com.vision.exception.RuntimeCustomException" })
	public ExceptionCode doBulkReject(List<TaxReconHeaderVb> vObjects) throws RuntimeCustomException {

		strErrorDesc = "";
		strCurrentOperation = Constants.REJECT;
		setServiceDefaults();
		ExceptionCode exceptionCode = null;
		try {
			boolean foundFlag = false;
			for (TaxReconHeaderVb object : vObjects) {
				foundFlag = true;
				exceptionCode = doRejectRecord(object);
			}
			if (foundFlag == false) {
				//logger.error("No Records To Reject");
				exceptionCode = getResultObject(Constants.NO_RECORDS_TO_REJECT);
				throw buildRuntimeCustomException(exceptionCode);
			}

			if (exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION) {
				//logger.error("Error in Bulk Reject. " + exceptionCode.getErrorMsg());
				exceptionCode = getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION);
				throw buildRuntimeCustomException(exceptionCode);
			}

			return exceptionCode;
		} catch (Exception ex) {
			//logger.error("Error in Bulk Reject.", ex);
			strErrorDesc = ex.getMessage();
			exceptionCode = getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION);
			throw buildRuntimeCustomException(exceptionCode);
		}

	}
	public ExceptionCode doRejectRecord(TaxReconHeaderVb vObject)throws RuntimeCustomException {
		TaxReconHeaderVb vObjectlocal = null;
		List<TaxReconHeaderVb> collTemp = null;
		ExceptionCode exceptionCode = null;
		setServiceDefaults();
		strErrorDesc  = "";
		strCurrentOperation = Constants.REJECT;
		vObject.setMaker(getIntCurrentUserId());
		try {
			if(vObject.getRecordIndicator() == 1 || vObject.getRecordIndicator() == 3 )
			    vObject.setRecordIndicator(0);
			    else
				   vObject.setRecordIndicator(-1);
			// See if such a pending request exists in the pending table
			collTemp = getAllQueryReconTables(vObject, Constants.STATUS_PENDING);
			if (collTemp == null){
				exceptionCode = getResultObject(Constants.ERRONEOUS_OPERATION);
				throw buildRuntimeCustomException(exceptionCode);
			}
			if (collTemp.size() == 0){
				exceptionCode = getResultObject(Constants.NO_SUCH_PENDING_RECORD);
				throw buildRuntimeCustomException(exceptionCode);
			}
			vObjectlocal = ((ArrayList<TaxReconHeaderVb>)collTemp).get(0);
			// Delete the record from the Pending table
			retVal = deletePendReconHeaders(vObject);
			List<TaxReconHeaderVb> selectionCriteriaLst = taxReconColumnDao.getQueryReconColumns(vObject,Constants.SUCCESSFUL_OPERATION);
			List<TaxReconActivityFilterVb> relationFilterLst = taxReconActivityFilterDao.getQueryReconActFilter(vObject,Constants.SUCCESSFUL_OPERATION);
			List<TaxReconActivityFilterVb> relationshipLst = taxReconTabRelationDao.getQueryReconTabRelation(vObject,Constants.SUCCESSFUL_OPERATION,false);
			List<TaxReconActivityFilterVb> relationshipFilterLst = taxReconTabRelationDao.getQueryReconTabRelation(vObject,Constants.SUCCESSFUL_OPERATION,true);
			

			if (selectionCriteriaLst != null && selectionCriteriaLst.size() > 0) {
				retVal = taxReconColumnDao.deletePendReconColumn(vObject);
			}
			if(relationFilterLst != null && relationFilterLst.size() > 0) {
				retVal =taxReconActivityFilterDao.deletePendReconActFilter(vObject);
			}
			if(relationshipLst != null && relationshipLst.size() > 0
					||relationshipFilterLst != null && relationshipFilterLst.size() > 0) {
				retVal = taxReconTabRelationDao.deletePendReconTabRelation(vObject);
			}
			writeAuditLog(vObject, null);
			return getResultObject(Constants.SUCCESSFUL_OPERATION);
		}catch (UncategorizedSQLException uSQLEcxception) {
			strErrorDesc = parseErrorMsg(uSQLEcxception);
			exceptionCode = getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION);
			throw buildRuntimeCustomException(exceptionCode);
		}catch(Exception ex){
			//logger.error("Error in Reject.",ex);
			//logger.error( ((vObject==null)? "vObject is Null":vObject.toString()));
			strErrorDesc = ex.getMessage();
			exceptionCode = getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION);
			throw buildRuntimeCustomException(exceptionCode);
		}
	}
	
	@Override
	protected String getAuditString(TaxReconHeaderVb vObject) {
		final String auditDelimiter = vObject.getAuditDelimiter();
		final String auditDelimiterColVal = vObject.getAuditDelimiterColVal();
		StringBuffer strAudit = new StringBuffer("");
		try {

			strAudit.append("RULE_ID_AT" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);

			if (ValidationUtil.isValid(vObject.getRuleId()))
				strAudit.append("RULE_ID" + auditDelimiterColVal + vObject.getRuleId().trim());
			else
				strAudit.append("RULE_ID" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);

			if (ValidationUtil.isValid(vObject.getTableId()))
				strAudit.append("TABLE_ID" + auditDelimiterColVal + vObject.getTableId());
			else
				strAudit.append("TABLE_ID" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);

			if (ValidationUtil.isValid(vObject.getTableName()))
				strAudit.append("TABLE_NAME" + auditDelimiterColVal + vObject.getTableName().trim());
			else
				strAudit.append("TABLE_NAME" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);

			if (ValidationUtil.isValid(vObject.getAliasName()))
				strAudit.append("ALIAS_NAME" + auditDelimiterColVal + vObject.getAliasName().trim());
			else
				strAudit.append("ALIAS_NAME" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);

			strAudit.append("RECON_STATUS_NT" + auditDelimiterColVal + vObject.getReconStatusNt());
			strAudit.append(auditDelimiter);

			if (ValidationUtil.isValid(vObject.getReconStatus()))
				strAudit.append("RECON_STATUS" + auditDelimiterColVal + vObject.getReconStatus());
			else
				strAudit.append("RECON_STATUS" + auditDelimiterColVal + "NULL");
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
			//ex.printStackTrace();
		}
		return strAudit.toString();
	}
	public ExceptionCode deleteAndInsertApprReconTable(TaxReconHeaderVb vObject) {
		ExceptionCode exceptionCode = new ExceptionCode();
		List<TaxReconHeaderVb> collTemp = getAllQueryReconTables(vObject, Constants.STATUS_ZERO);
		if (collTemp != null && collTemp.size() > 0) {
			int delCnt = deleteApprReconHeaders(vObject);
		}
		List<TaxReconHeaderVb> tableLst= vObject.getTableNamesList();
		for (TaxReconHeaderVb TaxReconHeaderVb : tableLst) {
			//TaxReconHeaderVb.setRuleId(vObject.getRuleId());
			TaxReconHeaderVb.setMaker(vObject.getMaker());
			TaxReconHeaderVb.setRecordIndicator(vObject.getRecordIndicator());
			retVal = doInsertionApprReconHeaders(TaxReconHeaderVb);
			writeAuditLog(TaxReconHeaderVb, null);
			if (retVal != Constants.SUCCESSFUL_OPERATION) {
				exceptionCode = getResultObject(retVal);
				throw buildRuntimeCustomException(exceptionCode);
			}
		}
		exceptionCode = getResultObject(Constants.SUCCESSFUL_OPERATION);
		return exceptionCode;
	}
	public ExceptionCode deleteAndInsertPendReconHeader(TaxReconHeaderVb vObject) {
		ExceptionCode exceptionCode = new ExceptionCode();
		List<TaxReconHeaderVb> collTemp = getAllQueryReconTables(vObject, Constants.SUCCESSFUL_OPERATION);
		if (collTemp != null && collTemp.size() > 0) {
			int delCnt = deletePendReconHeaders(vObject);
		}
		List<TaxReconHeaderVb> tableLst = vObject.getTableNamesList();
		for (TaxReconHeaderVb TaxReconHeaderVb : tableLst) {
			TaxReconHeaderVb.setRuleId(vObject.getRuleId());
			TaxReconHeaderVb.setMaker(vObject.getMaker());
			TaxReconHeaderVb.setRecordIndicator(vObject.getRecordIndicator());
			retVal = doInsertionPendReconHeaders(TaxReconHeaderVb);
			writeAuditLog(TaxReconHeaderVb, null);
			if (retVal != Constants.SUCCESSFUL_OPERATION) {
				exceptionCode = getResultObject(retVal);
				throw buildRuntimeCustomException(exceptionCode);
			}
		}
		exceptionCode = getResultObject(Constants.SUCCESSFUL_OPERATION);
		return exceptionCode;
	}
	public int getMaxSequence(){
		StringBuffer strBufApprove = new StringBuffer("Select MAX(TAppr.REF_NO) From RA_RECON_TAB_tax_HIS TAppr ");
		try{
			int max = getJdbcTemplate().queryForObject(strBufApprove.toString(), Integer.class);
			if(max <= 0)
				max = 1;
			else
				max++;
			return max;
		}catch(Exception ex){
//			ex.printStackTrace();
			return 1;
		}
	}
	public List<AlphaSubTabVb> getRuleIdLstforCopy(){
		try {
			String sql = "SELECT ALPHA_SUB_TAB,ALPHA_SUB_TAB"+getDbFunction("PIPELINE", null)+"' - '"+getDbFunction("PIPELINE", null)+"ALPHA_SUBTAB_DESCRIPTION ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_SUBTAB_STATUS = 0 AND ALPHA_TAB = 7008 "+
						" AND ALPHA_SUB_TAB NOT IN (SELECT RULE_ID FROM RA_RECON_TAB_TAX UNION SELECT RULE_ID FROM RA_RECON_TAB_TAX_PEND) ";
			return getJdbcTemplate().query(sql, commonDao.getGenMapper());
		} catch(Exception e) {
			return null;
		}
	}
	public void moveDatatoHistory(List<TaxReconHeaderVb> tableNamesList,List<TaxReconHeaderVb> collTempColAppr,List<TaxReconActivityFilterVb> collTempActFilAppr,
			List<TaxReconActivityFilterVb> collTempRelJoinsAppr) {
		try {
			int maxRefNo = getMaxSequence();
			if (tableNamesList != null && tableNamesList.size() > 0) {
				tableNamesList.forEach(table->{
					table.setRefNo(maxRefNo);
					doInsertionReconHeadersHis(table);
				});
			}
			if (collTempColAppr != null && collTempColAppr.size() > 0) {
				collTempColAppr.forEach(SelectionCriteria ->{
					SelectionCriteria.setRefNo(maxRefNo);
					taxReconColumnDao.doInsertionReconColumnHis(SelectionCriteria);
				});
			}
			
			if(collTempActFilAppr != null && collTempActFilAppr.size() > 0) {
				collTempActFilAppr.forEach(filter ->{
					filter.setRefNo(maxRefNo);
					taxReconActivityFilterDao.doInsertionReconActFilterHis(filter);
				});
			}
			
			if (collTempRelJoinsAppr != null && collTempRelJoinsAppr.size() > 0) {
				collTempRelJoinsAppr.forEach(relationTab ->{
					relationTab.setRefNo(maxRefNo);
					taxReconTabRelationDao.doInsertionReconTabRelationHis(relationTab);
				});
			}
			
		} catch(Exception e) {
			
		}
	}
	public ExceptionCode checkValidQuery(String sql) {
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			sql = replaceHashVariables(sql);
			Connection con = getConnection();
			Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
		} catch(Exception e) {
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg(e.getMessage());
		}
		return exceptionCode;
	}
	public String replaceHashVariables(String query) {
		try {
			query = query.replaceAll("#COUNTRY#", "''");
			query = query.replaceAll("#LE_BOOK#", "''");
			query = query.replaceAll("#BUSINESS_DATE#", "''");
			query = query.replaceAll("#BUSINESS_LINE_ID#", "''");
			query = query.replaceAll("#GRACE_DAYS#", "1");
			query = query.replaceAll("#GRACE_DATE#", "''");
			query = query.replaceAll("#RECOVERY_DATE#", "''");
			//query = query.replaceAll("#TRANS_LINE_ID#", transVb.getTransLineId());
		}catch(Exception e) {
			//e.printStackTrace();
		}
		return query;
	}
}
