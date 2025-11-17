package com.vision.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.vision.authentication.CustomContextHolder;
import com.vision.exception.ExceptionCode;
import com.vision.util.Constants;
import com.vision.util.ValidationUtil;
import com.vision.vb.TaxReconHeaderVb;

@Component
public class TaxReconColumnDao extends AbstractDao<TaxReconHeaderVb> {

	public List<TaxReconHeaderVb> getQueryReconColumns(TaxReconHeaderVb dObj, int intStatus) {
		List<TaxReconHeaderVb> collTemp = null;
		setServiceDefaults();
		String strQueryAppr = null;
		String strQueryPend = null;
		strQueryAppr = new String("SELECT TAPPR.RULE_ID,                                "
				+ "       TAPPR.TABLE_ID,                               "
				+ "       TAPPR.COL_TYPE,                               "
				+ "       T4.SEQUENCE ,                                 "
				+ "       TAPPR.COL_NAME,                               "
				+ "       T4.COL_NAME ALIAS,                            "
				+ "       TAPPR.AGG_FUNCTION,                           "
				+ "       TAPPR.GROUP_BY,                               "
				+ "       TAPPR.STATUS_NT,                              "
				+ "       TAPPR.STATUS,                                 "
				+ "       TAPPR.RECORD_INDICATOR_NT,                    "
				+ "       TAPPR.RECORD_INDICATOR,                       "
				+ "       TAPPR.MAKER,                                  "
				+ "       TAPPR.VERIFIER,                               "
				+ "       TAPPR.INTERNAL_STATUS,                        "
				+ "       TAPPR.DATE_LAST_MODIFIED,                     "
				+ "       TAPPR.DATE_CREATION, TAPPR.ORDER_BY                          "
				+ "  	  FROM RA_RECON_COLUMNS_REPOSITORY  t4  left join (select * from RA_RECON_COLUMNS_TAX a1 where rule_id = ?) TAPPR "
				//+ "			on (t4.SEQUENCE = TAPPR.COL_ID)              "
				+ " 		on (TAPPR.ALIAS=T4.COL_NAME)				" 	
				+ " ORDER BY t4.SEQUENCE                 ");
		strQueryPend = new String("SELECT TPEND.RULE_ID,                                "
				+ "       TPEND.TABLE_ID,                               "
				+ "       TPEND.COL_TYPE,                               "
				+ "       T4.SEQUENCE ,                                 "
				+ "       TPEND.COL_NAME,                               "
				+ "       T4.COL_NAME ALIAS,                            "
				+ "       TPEND.AGG_FUNCTION,                           "
				+ "       TPEND.GROUP_BY,                               "
				+ "       TPEND.STATUS_NT,                              "
				+ "       TPEND.STATUS,                                 "
				+ "       TPEND.RECORD_INDICATOR_NT,                    "
				+ "       TPEND.RECORD_INDICATOR,                       "
				+ "       TPEND.MAKER,                                  "
				+ "       TPEND.VERIFIER,                               "
				+ "       TPEND.INTERNAL_STATUS,                        "
				+ "       TPEND.DATE_LAST_MODIFIED,                     "
				+ "       TPEND.DATE_CREATION, TPEND.ORDER_BY                          "
				+ "  	  FROM RA_RECON_COLUMNS_REPOSITORY  t4  left join (select * from RA_RECON_COLUMNS_TAX_PEND a1 where rule_id = ?) TPEND "
				//+ "			on (t4.SEQUENCE = TPEND.COL_ID)              "
				+ " 		on (TPEND.ALIAS=T4.COL_NAME)				"
				+ " ORDER BY t4.SEQUENCE                 ");
		Object objParams[] = new Object[1];
		objParams[0] = dObj.getRuleId();

		try {

			if (intStatus == 0) {
				//logger.info("Executing approved query");
				collTemp = getJdbcTemplate().query(strQueryAppr.toString(), objParams, getReconColumnMapper());
			} else {
				//logger.info("Executing pending query");
				collTemp = getJdbcTemplate().query(strQueryPend.toString(), objParams, getReconColumnMapper());
			}
			return collTemp;
		} catch (Exception ex) {
			//ex.printStackTrace();
			//logger.error("Error: getQueryResults Exception :   ");
			/*if (intStatus == 0)
				//logger.error(((strQueryAppr == null) ? "strQueryAppr is Null" : strQueryAppr.toString()));
			else
				//logger.error(((strQueryPend == null) ? "strQueryPend is Null" : strQueryPend.toString()));

			if (objParams != null)
				for (int i = 0; i < objParams.length; i++)
					////logger.error("objParams[" + i + "]" + objParams[i].toString());
*/			return null;
		}
	}

	protected RowMapper getReconColumnMapper() {
		RowMapper mapper = new RowMapper() {
			@Override
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				TaxReconHeaderVb reconVb = new TaxReconHeaderVb();
				reconVb.setColType(rs.getString("COL_TYPE"));
				if(!ValidationUtil.isValid(reconVb.getColType())) {
					reconVb.setColType("C");
				}
				reconVb.setColName(rs.getString("COL_NAME"));
				reconVb.setRuleId(rs.getString("RULE_ID"));
				reconVb.setTableId(rs.getInt("TABLE_ID"));
				reconVb.setColId(rs.getInt("SEQUENCE"));
				reconVb.setColAliasName(rs.getString("ALIAS").trim());
				reconVb.setAggFunc(rs.getString("AGG_FUNCTION"));
				reconVb.setGroupBy(rs.getString("GROUP_BY"));
				reconVb.setRecordIndicator(rs.getInt("RECORD_INDICATOR"));
				reconVb.setMaker(rs.getInt("MAKER"));
				reconVb.setVerifier(rs.getInt("VERIFIER"));
				reconVb.setDateLastModified(rs.getString("DATE_LAST_MODIFIED"));
				reconVb.setDateCreation(rs.getString("DATE_CREATION"));
				reconVb.setOrderBy(rs.getString("ORDER_BY"));
				return reconVb;
			}
		};
		return mapper;
	}
	protected RowMapper getReconColRepositoryMapper() {
		RowMapper mapper = new RowMapper() {
			@Override
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				TaxReconHeaderVb reconVb = new TaxReconHeaderVb();
				reconVb.setColName(rs.getString("COL_NAME").trim());
				reconVb.setColId(rs.getInt("SEQUENCE"));
				reconVb.setRecordIndicator(rs.getInt("RECORD_INDICATOR"));
				reconVb.setMaker(rs.getInt("MAKER"));
				reconVb.setVerifier(rs.getInt("VERIFIER"));
				reconVb.setDateLastModified(rs.getString("DATE_LAST_MODIFIED"));
				reconVb.setDateCreation(rs.getString("DATE_CREATION"));
				return reconVb;
			}
		};
		return mapper;
	}

	@Override
	protected void setServiceDefaults() {
		serviceName = "ReconConfigHeader";
		serviceDesc = "Recon Config Header";
		tableName = "RA_RECON_COLUMNS";
		childTableName = "RA_RECON_COLUMNS";
		intCurrentUserId = CustomContextHolder.getContext().getVisionId();
	}

	protected int doInsertionApprReconColumn(TaxReconHeaderVb vObject) {
		String query = " INSERT INTO RA_RECON_COLUMNS_TAX (RULE_ID_AT,RULE_ID,TABLE_ID,COL_ID,COL_TYPE,COL_NAME,ALIAS,                       "
				+ " AGG_FUNCTION,GROUP_BY,STATUS_NT,STATUS,RECORD_INDICATOR_NT,                        "
				+ " RECORD_INDICATOR,MAKER,VERIFIER,INTERNAL_STATUS,DATE_LAST_MODIFIED,DATE_CREATION,ORDER_BY)  "
				+ " VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,   " + getDbFunction("SYSDATE")
				+ "," + getDbFunction("SYSDATE") + ",?)";
		Object[] args = { vObject.getRuleIdAt(), vObject.getRuleId(), vObject.getTableId(), vObject.getColId(),
				vObject.getColType(), vObject.getColName(), vObject.getColAliasName(), vObject.getAggFunc(),
				vObject.getGroupBy(), vObject.getReconStatusNt(), vObject.getReconStatus(),
				vObject.getRecordIndicatorNt(), vObject.getRecordIndicator(), vObject.getMaker(), vObject.getVerifier(),
				vObject.getInternalStatus(),vObject.getOrderBy() };
		return getJdbcTemplate().update(query, args);
	}

	protected int doInsertionPendReconColumn(TaxReconHeaderVb vObject) {
		String query = " INSERT INTO RA_RECON_COLUMNS_TAX_PEND (RULE_ID_AT,RULE_ID,TABLE_ID,COL_ID,COL_TYPE,COL_NAME,ALIAS,                       "
				+ " AGG_FUNCTION,GROUP_BY,STATUS_NT,STATUS,RECORD_INDICATOR_NT,                        "
				+ " RECORD_INDICATOR,MAKER,VERIFIER,INTERNAL_STATUS,DATE_LAST_MODIFIED,DATE_CREATION,ORDER_BY)  "
				+ " VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,   " + getDbFunction("SYSDATE")
				+ "," + getDbFunction("SYSDATE") + ",?)";
		Object[] args = { vObject.getRuleIdAt(), vObject.getRuleId(), vObject.getTableId(), vObject.getColId(),
				vObject.getColType(), vObject.getColName(), vObject.getColAliasName(), vObject.getAggFunc(),
				vObject.getGroupBy(), vObject.getReconStatusNt(), vObject.getReconStatus(),
				vObject.getRecordIndicatorNt(), vObject.getRecordIndicator(), vObject.getMaker(), vObject.getVerifier(),
				vObject.getInternalStatus(),ValidationUtil.isValid(vObject.getOrderBy())? vObject.getOrderBy() :null};
		return getJdbcTemplate().update(query, args);
	}

	protected int doInsertionReconColumnHis(TaxReconHeaderVb vObject) {
		String query = " INSERT INTO RA_RECON_COLUMNS_TAX_HIS (REF_NO,RULE_ID_AT,RULE_ID,TABLE_ID,COL_ID,COL_TYPE        "
				+ " ,COL_NAME,ALIAS,AGG_FUNCTION,GROUP_BY,STATUS_NT,STATUS,RECORD_INDICATOR_NT,RECORD_INDICATOR "
				+ " ,MAKER,VERIFIER,INTERNAL_STATUS,DATE_LAST_MODIFIED,DATE_CREATION,ORDER_BY )                          "
				+ " VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,  " + getDbFunction("SYSDATE") + ","
				+ getDbFunction("SYSDATE") + ",?)";
		Object[] args = { vObject.getRefNo(), vObject.getRuleIdAt(), vObject.getRuleId(), vObject.getTableId(),
				vObject.getColId(), vObject.getColType(), vObject.getColName(), vObject.getColAliasName(),
				vObject.getAggFunc(), vObject.getGroupBy(), vObject.getReconStatusNt(), vObject.getReconStatus(),
				vObject.getRecordIndicatorNt(), vObject.getRecordIndicator(), vObject.getMaker(), vObject.getVerifier(),
				vObject.getInternalStatus(),vObject.getOrderBy() };
		return getJdbcTemplate().update(query, args);
	}



	protected int deleteApprReconColumn(TaxReconHeaderVb vObject) {
		String query = "DELETE FROM RA_RECON_COLUMNS_TAX WHERE RULE_ID = ?    ";
		Object[] args = { vObject.getRuleId() };
		return getJdbcTemplate().update(query, args);
	}

	protected int deletePendReconColumn(TaxReconHeaderVb vObject) {
		String query = "DELETE FROM RA_RECON_COLUMNS_TAX_PEND WHERE RULE_ID = ?   ";
		Object[] args = { vObject.getRuleId() };
		return getJdbcTemplate().update(query, args);
	}

	public ExceptionCode deleteAndInsertApprReconColumn(TaxReconHeaderVb vObject) {
		ExceptionCode exceptionCode = new ExceptionCode();
		List<TaxReconHeaderVb> collTemp = getQueryReconColumns(vObject, Constants.STATUS_ZERO);
		if (collTemp != null && collTemp.size() > 0) {
			if (collTemp != null && collTemp.size() > 0) {
				collTemp.forEach(SelectionCriteria ->{
					SelectionCriteria.setRefNo(vObject.getRefNo());
					doInsertionReconColumnHis(SelectionCriteria);
				});
			}
			int delCnt = deleteApprReconColumn(vObject);
		}
		List<TaxReconHeaderVb> selectionCriteriaLst = vObject.getSelectionCriteriaLst();
		for (TaxReconHeaderVb TaxReconHeaderVb : selectionCriteriaLst) {
			TaxReconHeaderVb.setMaker(vObject.getMaker());
			TaxReconHeaderVb.setRecordIndicator(vObject.getRecordIndicator());
			retVal = doInsertionApprReconColumn(TaxReconHeaderVb);
			writeAuditLog(TaxReconHeaderVb, null);
			if (retVal != Constants.SUCCESSFUL_OPERATION){
				exceptionCode = getResultObject(retVal);
				throw buildRuntimeCustomException(exceptionCode);
			}
		}
		exceptionCode = getResultObject(Constants.SUCCESSFUL_OPERATION);
		return exceptionCode;
	}

	public ExceptionCode deleteAndInsertPendReconColumn(TaxReconHeaderVb vObject) {
		ExceptionCode exceptionCode = new ExceptionCode();
		List<TaxReconHeaderVb> collTemp = getQueryReconColumns(vObject, Constants.SUCCESSFUL_OPERATION);
		if (collTemp != null && collTemp.size() > 0) {
			int delCnt = deletePendReconColumn(vObject);
		}
		List<TaxReconHeaderVb> selectionCriteriaLst = vObject.getSelectionCriteriaLst();
		for (TaxReconHeaderVb TaxReconHeaderVb : selectionCriteriaLst) {
		
			TaxReconHeaderVb.setRuleId(vObject.getRuleId());
			TaxReconHeaderVb.setMaker(vObject.getMaker());
			TaxReconHeaderVb.setRecordIndicator(vObject.getRecordIndicator());
			retVal = doInsertionPendReconColumn(TaxReconHeaderVb);
			writeAuditLog(TaxReconHeaderVb, null);
			if (retVal != Constants.SUCCESSFUL_OPERATION){
				exceptionCode = getResultObject(retVal);
				throw buildRuntimeCustomException(exceptionCode);
			}
		}
		exceptionCode = getResultObject(Constants.SUCCESSFUL_OPERATION);
		return exceptionCode;
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

			if (ValidationUtil.isValid(vObject.getColId()))
				strAudit.append("COL_ID" + auditDelimiterColVal + vObject.getColId());
			else
				strAudit.append("COL_ID" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);
			
			if (ValidationUtil.isValid(vObject.getColType()))
				strAudit.append("COL_TYPE" + auditDelimiterColVal + vObject.getColType());
			else
				strAudit.append("COL_TYPE" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);

			if (ValidationUtil.isValid(vObject.getColAliasName()))
				strAudit.append("COL_ALIAS_NAME" + auditDelimiterColVal + vObject.getColAliasName().trim());
			else
				strAudit.append("COL_ALIAS_NAME" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);
			
			if (ValidationUtil.isValid(vObject.getAggFunc()))
				strAudit.append("AGG_FUNCTION" + auditDelimiterColVal + vObject.getAggFunc().trim());
			else
				strAudit.append("AGG_FUNCTION" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);
			
			if (ValidationUtil.isValid(vObject.getGroupBy()))
				strAudit.append("GROUP_BY" + auditDelimiterColVal + vObject.getGroupBy().trim());
			else
				strAudit.append("GROUP_BY" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);
			

			strAudit.append("RECON_STATUS_NT" + auditDelimiterColVal + "NULL");
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
	public List<TaxReconHeaderVb> getQueryReconColumnsRepository() {
		List<TaxReconHeaderVb> collTemp = null;
		setServiceDefaults();
		String strQueryAppr = null;
		strQueryAppr = "	   SELECT TAPPR.SEQUENCE,                         "
				+ "       TAPPR.COL_NAME,                             "
				+ "       TAPPR.STATUS_NT,                            "
				+ "       TAPPR.STATUS,                               "
				+ "       TAPPR.RECORD_INDICATOR_NT,                  "
				+ "       TAPPR.RECORD_INDICATOR,                     "
				+ "       TAPPR.MAKER,                                "
				+ "       TAPPR.VERIFIER,                             "
				+ "       TAPPR.INTERNAL_STATUS,                      "
				+ "       TAPPR.DATE_LAST_MODIFIED,                   "
				+ "       TAPPR.DATE_CREATION                         "
				+ "  FROM RA_RECON_COLUMNS_REPOSITORY TAPPR                      "
				+ " WHERE TAPPR.STATUS = 0                                 "
				+ " ORDER BY SEQUENCE";
		try {
			collTemp = getJdbcTemplate().query(strQueryAppr, getReconColRepositoryMapper());
			return collTemp;
		} catch (Exception ex) {
			//ex.printStackTrace();
			//logger.error("Error: getQueryResults Exception :   ");
			/*if (intStatus == 0)
				//logger.error(((strQueryAppr == null) ? "strQueryAppr is Null" : strQueryAppr.toString()));
			else
				//logger.error(((strQueryPend == null) ? "strQueryPend is Null" : strQueryPend.toString()));

			if (objParams != null)
				for (int i = 0; i < objParams.length; i++)
					////logger.error("objParams[" + i + "]" + objParams[i].toString());
*/			return null;
		}
	}
}
