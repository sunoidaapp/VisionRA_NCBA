package com.vision.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.vision.authentication.CustomContextHolder;
import com.vision.util.Constants;
import com.vision.util.ValidationUtil;
import com.vision.vb.TaxReconActivityFilterVb;
import com.vision.vb.TaxReconHeaderVb;


@Component
public class TaxReconTabRelationDao extends AbstractDao<TaxReconHeaderVb> {



	@Override
	protected void setServiceDefaults() {
		serviceName = "ReconTabRelation";
		serviceDesc = "Recon Tab Relation ";
		tableName = "RA_Recon_Act_Filter";
		childTableName = "RA_Recon_Act_Filter";
		intCurrentUserId = CustomContextHolder.getContext().getVisionId();
	}

	protected int doInsertionApprReconTabRelation(TaxReconActivityFilterVb vObject) {
		String query = " INSERT INTO RA_Recon_Tab_Relations_Tax (RULE_ID_AT,RULE_ID,FROM_TABLE_ID,TO_TABLE_ID,JOIN_TYPE_NT,JOIN_TYPE         "
				+ " ,JOIN_STRING_1,JOIN_STRING_2,JOIN_STRING_3,JOIN_STRING_4,JOIN_STRING_5,FILTER_CONDITION_1,FILTER_CONDITION_2    "
				+ " ,FILTER_CONDITION_3,FILTER_CONDITION_4,FILTER_CONDITION_5,STATUS_NT,STATUS,RECORD_INDICATOR_NT,RECORD_INDICATOR "
				+ " ,MAKER,VERIFIER,INTERNAL_STATUS,DATE_LAST_MODIFIED,DATE_CREATION)                                               "
				+ "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, "
				+ getDbFunction("SYSDATE") + "," + getDbFunction("SYSDATE") + ")";

		Object[] args = { vObject.getRuleIdAt(), vObject.getRuleId(), vObject.getFromTableId(), vObject.getToTableId(),
				vObject.getJoinTypeNt(), vObject.getJoinType(), vObject.getJoinString1(), vObject.getJoinString2(),
				vObject.getJoinString3(), vObject.getJoinString4(), vObject.getJoinString5(),
				vObject.getFilterConditon1(), vObject.getFilterConditon2(), vObject.getFilterConditon3(),
				vObject.getFilterConditon4(), vObject.getFilterConditon5(), vObject.getTabStatusNt(),
				vObject.getTabStatus(), vObject.getRecordIndicatorNt(), vObject.getRecordIndicator(),
				vObject.getMaker(), vObject.getVerifier(), vObject.getInternalStatus() };
		return getJdbcTemplate().update(query, args);
	}

	protected int doInsertionPendReconTabRelation(TaxReconActivityFilterVb vObject) {
		String query = " INSERT INTO RA_Recon_Tab_Relations_Tax_PEND (RULE_ID_AT,RULE_ID,FROM_TABLE_ID,TO_TABLE_ID,JOIN_TYPE_NT,JOIN_TYPE         "
				+ " ,JOIN_STRING_1,JOIN_STRING_2,JOIN_STRING_3,JOIN_STRING_4,JOIN_STRING_5,FILTER_CONDITION_1,FILTER_CONDITION_2    "
				+ " ,FILTER_CONDITION_3,FILTER_CONDITION_4,FILTER_CONDITION_5,STATUS_NT,STATUS,RECORD_INDICATOR_NT,RECORD_INDICATOR "
				+ " ,MAKER,VERIFIER,INTERNAL_STATUS,DATE_LAST_MODIFIED,DATE_CREATION)                                               "
				+ "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, "
				+ getDbFunction("SYSDATE") + "," + getDbFunction("SYSDATE") + ")";

		Object[] args = { vObject.getRuleIdAt(), vObject.getRuleId(), vObject.getFromTableId(), vObject.getToTableId(),
				vObject.getJoinTypeNt(), vObject.getJoinType(), vObject.getJoinString1(), vObject.getJoinString2(),
				vObject.getJoinString3(), vObject.getJoinString4(), vObject.getJoinString5(),
				vObject.getFilterConditon1(), vObject.getFilterConditon2(), vObject.getFilterConditon3(),
				vObject.getFilterConditon4(), vObject.getFilterConditon5(), vObject.getTabStatusNt(),
				vObject.getTabStatus(), vObject.getRecordIndicatorNt(), vObject.getRecordIndicator(),
				vObject.getMaker(), vObject.getVerifier(),vObject.getInternalStatus() };
		return getJdbcTemplate().update(query, args);
	}
	protected int doInsertionReconTabRelationHis(TaxReconActivityFilterVb vObject) {
		String query =" INSERT INTO RA_RECON_TAB_RELATIONS_HIS(REF_NO,RULE_ID_AT,RULE_ID,FROM_TABLE_ID        "
				+" ,TO_TABLE_ID,JOIN_TYPE_NT,JOIN_TYPE,JOIN_STRING_1,JOIN_STRING_2,JOIN_STRING_3         "
				+" ,JOIN_STRING_4,JOIN_STRING_5,FILTER_CONDITION_1,FILTER_CONDITION_2,FILTER_CONDITION_3 "
				+" ,FILTER_CONDITION_4,FILTER_CONDITION_5,STATUS_NT,STATUS,RECORD_INDICATOR_NT           "
				+" ,RECORD_INDICATOR,MAKER,VERIFIER,INTERNAL_STATUS,DATE_LAST_MODIFIED,DATE_CREATION)                      "
				+" VALUES( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " + getDbFunction("SYSDATE") + ","
				+ getDbFunction("SYSDATE") + ")";

		Object[] args = {vObject.getRefNo(), vObject.getRuleIdAt(), vObject.getRuleId(), vObject.getFromTableId(), vObject.getToTableId(),
				vObject.getJoinTypeNt(), vObject.getJoinType(), vObject.getJoinString1(), vObject.getJoinString2(),
				vObject.getJoinString3(), vObject.getJoinString4(), vObject.getJoinString5(),
				vObject.getFilterConditon1(), vObject.getFilterConditon2(), vObject.getFilterConditon3(),
				vObject.getFilterConditon4(), vObject.getFilterConditon5(), vObject.getTabStatusNt(),
				vObject.getTabStatus(), vObject.getRecordIndicatorNt(), vObject.getRecordIndicator(),
				vObject.getMaker(), vObject.getVerifier(), vObject.getInternalStatus()};
		return getJdbcTemplate().update(query, args);
	}
	
	protected int doInsertionPendReconTabRelationDc(TaxReconActivityFilterVb vObject) {
		String query = " INSERT INTO RA_Recon_Tab_Relations_Tax_PEND(RULE_ID_AT,RULE_ID,TABLE_ID,TABLE_NAME,ALIAS_NAME,STATUS_NT,STATUS,          "
				+ " RECORD_INDICATOR_NT,RECORD_INDICATOR,MAKER,VERIFIER,INTERNAL_STATUS,DATE_LAST_MODIFIED,DATE_CREATION) "
				+ " VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,  " + getDbFunction("SYSDATE") + ","
				+ "CONVERT(datetime, ?, 103))";
		
		Object[] args = { vObject.getRuleIdAt(), vObject.getRuleId(), vObject.getFromTableId(), vObject.getToTableId(),
				vObject.getJoinTypeNt(), vObject.getJoinType(), vObject.getJoinString1(), vObject.getJoinString2(),
				vObject.getJoinString3(), vObject.getJoinString4(), vObject.getJoinString5(),
				vObject.getFilterConditon1(), vObject.getFilterConditon2(), vObject.getFilterConditon3(),
				vObject.getFilterConditon4(), vObject.getFilterConditon5(), vObject.getTabStatusNt(),
				vObject.getTabStatus(), vObject.getRecordIndicatorNt(), vObject.getRecordIndicator(),
				vObject.getMaker(), vObject.getVerifier(),vObject.getInternalStatus(),  vObject.getDateCreation() };
		return getJdbcTemplate().update(query, args);
	}

	protected int deleteApprReconTabRelation(TaxReconHeaderVb vObject) {
		String query = "DELETE FROM RA_Recon_Tab_Relations_Tax  WHERE RULE_ID = ?";

		Object[] args = { vObject.getRuleId() };
		return getJdbcTemplate().update(query, args);
	}

	protected int deletePendReconTabRelation(TaxReconHeaderVb vObject) {
		String query = "DELETE FROM RA_Recon_Tab_Relations_Tax_PEND  WHERE RULE_ID = ?";

		Object[] args = { vObject.getRuleId() };
		return getJdbcTemplate().update(query, args);
	}

	public List<TaxReconActivityFilterVb> getQueryReconTabRelation(TaxReconHeaderVb dObj, int intStatus,Boolean checkFlag) {
		List<TaxReconActivityFilterVb> collTemp = null;
		setServiceDefaults();
		String strQueryAppr = null;
		String strQueryPend = null;
		String fromTableQuery = "";
		/*if(checkFlag) {
			if(intStatus == Constants.STATUS_ZERO)
				fromTableQuery = " AND TAPPR.FROM_TABLE_ID = '1' AND TAPPR.TO_TABLE_ID = '1' ";
			else
				fromTableQuery = " AND TPEND.FROM_TABLE_ID = '1' AND TPEND.TO_TABLE_ID = '1' ";
		}else {
			if(intStatus == Constants.STATUS_ZERO)
				fromTableQuery ="AND TAPPR.JOIN_STRING_1 != ''  ";
			else
				fromTableQuery = "AND TPEND.JOIN_STRING_1 != ''  ";
			
			
		}*/
		strQueryAppr = new String("  SELECT TAPPR.RULE_ID_AT,            " + "         TAPPR.RULE_ID,               "
				+ "         TAPPR.FROM_TABLE_ID,         " + "         TAPPR.TO_TABLE_ID,           "
				+ "         TAPPR.JOIN_TYPE_NT,          " + "         TAPPR.JOIN_TYPE,             "
				+ "         TAPPR.JOIN_STRING_1,         " + "         TAPPR.JOIN_STRING_2,         "
				+ "         TAPPR.JOIN_STRING_3,         " + "         TAPPR.JOIN_STRING_4,         "
				+ "         TAPPR.JOIN_STRING_5,         " + "         TAPPR.FILTER_CONDITION_1,    "
				+ "         TAPPR.FILTER_CONDITION_2,    " + "         TAPPR.FILTER_CONDITION_3,    "
				+ "         TAPPR.FILTER_CONDITION_4,    " + "         TAPPR.FILTER_CONDITION_5,    "
				+ "         TAPPR.STATUS_NT,             " + "         TAPPR.STATUS,                "
				+ "         TAPPR.RECORD_INDICATOR_NT,   " + "         TAPPR.RECORD_INDICATOR,      "
				+ "         TAPPR.MAKER,                 " + "         TAPPR.VERIFIER,              "
				+ "         TAPPR.INTERNAL_STATUS,       " + "         TAPPR.DATE_LAST_MODIFIED,    "
				+ "         TAPPR.DATE_CREATION          "
				+ "    FROM RA_Recon_Tab_Relations_Tax TAPPR,NUM_SUB_TAB T2, NUM_SUB_TAB T3  "
				+ "   WHERE TAPPR.RULE_ID = ?   "+fromTableQuery+"      AND T2.NUM_tab = TAPPR.RECORD_INDICATOR_NT       "
				+ "  AND T2.NUM_sub_tab = TAPPR.RECORD_INDICATOR      "
				+ "  AND T3.NUM_TAB = TAPPR.STATUS_NT                 "
				+ "  AND T3.NUM_SUB_TAB = TAPPR.STATUS                ");
		strQueryPend = new String("  SELECT TPEND.RULE_ID_AT,            " + "         TPEND.RULE_ID,               "
				+ "         TPEND.FROM_TABLE_ID,         " + "         TPEND.TO_TABLE_ID,           "
				+ "         TPEND.JOIN_TYPE_NT,          " + "         TPEND.JOIN_TYPE,             "
				+ "         TPEND.JOIN_STRING_1,         " + "         TPEND.JOIN_STRING_2,         "
				+ "         TPEND.JOIN_STRING_3,         " + "         TPEND.JOIN_STRING_4,         "
				+ "         TPEND.JOIN_STRING_5,         " + "         TPEND.FILTER_CONDITION_1,    "
				+ "         TPEND.FILTER_CONDITION_2,    " + "         TPEND.FILTER_CONDITION_3,    "
				+ "         TPEND.FILTER_CONDITION_4,    " + "         TPEND.FILTER_CONDITION_5,    "
				+ "         TPEND.STATUS_NT,             " + "         TPEND.STATUS,                "
				+ "         TPEND.RECORD_INDICATOR_NT,   " + "         TPEND.RECORD_INDICATOR,      "
				+ "         TPEND.MAKER,                 " + "         TPEND.VERIFIER,              "
				+ "         TPEND.INTERNAL_STATUS,       " + "         TPEND.DATE_LAST_MODIFIED,    "
				+ "         TPEND.DATE_CREATION          "
				+ "    FROM RA_Recon_Tab_Relations_Tax_PEND TPEND,NUM_SUB_TAB T2, NUM_SUB_TAB T3  "
				+ "   WHERE TPEND.RULE_ID = ?    "+fromTableQuery+"  AND T2.NUM_tab = TPEND.RECORD_INDICATOR_NT       "
				+ "  AND T2.NUM_sub_tab = TPEND.RECORD_INDICATOR      "
				+ "  AND T3.NUM_TAB = TPEND.STATUS_NT                 "
				+ "  AND T3.NUM_SUB_TAB = TPEND.STATUS                ");
		Object objParams[] = new Object[1];
		objParams[0] = dObj.getRuleId();

		try {
			if (intStatus == 0) {
				//logger.info("Executing approved query");
				collTemp = getJdbcTemplate().query(strQueryAppr.toString(), objParams, getReconTabRelationMapper());
			} else {
				//logger.info("Executing pending query");
				collTemp = getJdbcTemplate().query(strQueryPend.toString(), objParams, getReconTabRelationMapper());
			}
			return collTemp;
		} catch (Exception ex) {
			//ex.printStackTrace();
			//logger.error("Error: getQueryResults Exception :   ");
			/*if (intStatus == 0)
				////logger.error(((strQueryAppr == null) ? "strQueryAppr is Null" : strQueryAppr.toString()));
			else
				////logger.error(((strQueryPend == null) ? "strQueryPend is Null" : strQueryPend.toString()));

			if (objParams != null)
				for (int i = 0; i < objParams.length; i++)*/
					////logger.error("objParams[" + i + "]" + objParams[i].toString());
			return null;
		}
	}
	
	//used to reterive and store in history table
	public List<TaxReconActivityFilterVb> getQueryReconTabRelationRuleid(TaxReconHeaderVb dObj, int intStatus) {
		List<TaxReconActivityFilterVb> collTemp = null;
		setServiceDefaults();
		String strQueryAppr = null;
		String strQueryPend = null;
		String fromTableQuery = "";
		
		strQueryAppr = new String("  SELECT TAPPR.RULE_ID_AT,            " + "         TAPPR.RULE_ID,               "
				+ "         TAPPR.FROM_TABLE_ID,         " + "         TAPPR.TO_TABLE_ID,           "
				+ "         TAPPR.JOIN_TYPE_NT,          " + "         TAPPR.JOIN_TYPE,             "
				+ "         TAPPR.JOIN_STRING_1,         " + "         TAPPR.JOIN_STRING_2,         "
				+ "         TAPPR.JOIN_STRING_3,         " + "         TAPPR.JOIN_STRING_4,         "
				+ "         TAPPR.JOIN_STRING_5,         " + "         TAPPR.FILTER_CONDITION_1,    "
				+ "         TAPPR.FILTER_CONDITION_2,    " + "         TAPPR.FILTER_CONDITION_3,    "
				+ "         TAPPR.FILTER_CONDITION_4,    " + "         TAPPR.FILTER_CONDITION_5,    "
				+ "         TAPPR.STATUS_NT,             " + "         TAPPR.STATUS,                "
				+ "         TAPPR.RECORD_INDICATOR_NT,   " + "         TAPPR.RECORD_INDICATOR,      "
				+ "         TAPPR.MAKER,                 " + "         TAPPR.VERIFIER,              "
				+ "         TAPPR.INTERNAL_STATUS,       " + "         TAPPR.DATE_LAST_MODIFIED,    "
				+ "         TAPPR.DATE_CREATION          "
				+ "    FROM RA_Recon_Tab_Relations_Tax TAPPR,NUM_SUB_TAB T2, NUM_SUB_TAB T3  "
				+ "   WHERE TAPPR.RULE_ID = ?   AND T2.NUM_tab = TAPPR.RECORD_INDICATOR_NT       "
				+ "  AND T2.NUM_sub_tab = TAPPR.RECORD_INDICATOR      "
				+ "  AND T3.NUM_TAB = TAPPR.STATUS_NT                 "
				+ "  AND T3.NUM_SUB_TAB = TAPPR.STATUS                ");
		strQueryPend = new String("  SELECT TPEND.RULE_ID_AT,            " + "         TPEND.RULE_ID,               "
				+ "         TPEND.FROM_TABLE_ID,         " + "         TPEND.TO_TABLE_ID,           "
				+ "         TPEND.JOIN_TYPE_NT,          " + "         TPEND.JOIN_TYPE,             "
				+ "         TPEND.JOIN_STRING_1,         " + "         TPEND.JOIN_STRING_2,         "
				+ "         TPEND.JOIN_STRING_3,         " + "         TPEND.JOIN_STRING_4,         "
				+ "         TPEND.JOIN_STRING_5,         " + "         TPEND.FILTER_CONDITION_1,    "
				+ "         TPEND.FILTER_CONDITION_2,    " + "         TPEND.FILTER_CONDITION_3,    "
				+ "         TPEND.FILTER_CONDITION_4,    " + "         TPEND.FILTER_CONDITION_5,    "
				+ "         TPEND.STATUS_NT,             " + "         TPEND.STATUS,                "
				+ "         TPEND.RECORD_INDICATOR_NT,   " + "         TPEND.RECORD_INDICATOR,      "
				+ "         TPEND.MAKER,                 " + "         TPEND.VERIFIER,              "
				+ "         TPEND.INTERNAL_STATUS,       " + "         TPEND.DATE_LAST_MODIFIED,    "
				+ "         TPEND.DATE_CREATION          "
				+ "    FROM RA_Recon_Tab_Relations_Tax_PEND TPEND,NUM_SUB_TAB T2, NUM_SUB_TAB T3  "
				+ "   WHERE TPEND.RULE_ID = ?  AND T2.NUM_tab = TPEND.RECORD_INDICATOR_NT       "
				+ "  AND T2.NUM_sub_tab = TPEND.RECORD_INDICATOR      "
				+ "  AND T3.NUM_TAB = TPEND.STATUS_NT                 "
				+ "  AND T3.NUM_SUB_TAB = TPEND.STATUS                ");
		Object objParams[] = new Object[1];
		objParams[0] = dObj.getRuleId();

		try {
			if (intStatus == 0) {
				//logger.info("Executing approved query");
				collTemp = getJdbcTemplate().query(strQueryAppr.toString(), objParams, getReconTabRelationMapper());
			} else {
				//logger.info("Executing pending query");
				collTemp = getJdbcTemplate().query(strQueryPend.toString(), objParams, getReconTabRelationMapper());
			}
			return collTemp;
		} catch (Exception ex) {
			//ex.printStackTrace();
			//logger.error("Error: getQueryResults Exception :   ");
			/*if (intStatus == 0)
				////logger.error(((strQueryAppr == null) ? "strQueryAppr is Null" : strQueryAppr.toString()));
			else
				////logger.error(((strQueryPend == null) ? "strQueryPend is Null" : strQueryPend.toString()));

			if (objParams != null)
				for (int i = 0; i < objParams.length; i++)*/
					////logger.error("objParams[" + i + "]" + objParams[i].toString());
			return null;
		}
	}

	protected RowMapper getReconTabRelationMapper() {
		RowMapper mapper = new RowMapper() {
			@Override
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				TaxReconActivityFilterVb reconTabRelationVb = new TaxReconActivityFilterVb();
				reconTabRelationVb.setRuleId(rs.getString("RULE_ID"));
				reconTabRelationVb.setFromTableId(rs.getInt("FROM_TABLE_ID"));
				reconTabRelationVb.setToTableId(rs.getInt("TO_TABLE_ID"));
				reconTabRelationVb.setJoinType(rs.getInt("JOIN_TYPE"));
				reconTabRelationVb.setJoinString1(rs.getString("JOIN_STRING_1"));
				reconTabRelationVb.setJoinString2(rs.getString("JOIN_STRING_2"));
				reconTabRelationVb.setJoinString3(rs.getString("JOIN_STRING_3"));
				reconTabRelationVb.setJoinString4(rs.getString("JOIN_STRING_4"));
				reconTabRelationVb.setJoinString5(rs.getString("JOIN_STRING_5"));
				reconTabRelationVb.setFilterConditon1(rs.getString("FILTER_CONDITION_1"));
				reconTabRelationVb.setFilterConditon2(rs.getString("FILTER_CONDITION_2"));
				reconTabRelationVb.setFilterConditon3(rs.getString("FILTER_CONDITION_3"));
				reconTabRelationVb.setFilterConditon4(rs.getString("FILTER_CONDITION_4"));
				reconTabRelationVb.setFilterConditon5(rs.getString("FILTER_CONDITION_5"));
				reconTabRelationVb.setTabStatus(rs.getInt("STATUS"));
				reconTabRelationVb.setRecordIndicator(rs.getInt("RECORD_INDICATOR"));
				reconTabRelationVb.setMaker(rs.getInt("MAKER"));
				reconTabRelationVb.setVerifier(rs.getInt("VERIFIER"));
				reconTabRelationVb.setDateLastModified(rs.getString("DATE_LAST_MODIFIED"));
				reconTabRelationVb.setDateCreation(rs.getString("DATE_CREATION"));
				return reconTabRelationVb;
			}
		};
		return mapper;
	}

	protected String getAuditString(TaxReconActivityFilterVb vObject) {
		final String auditDelimiter = vObject.getAuditDelimiter();
		final String auditDelimiterColVal = vObject.getAuditDelimiterColVal();
		StringBuffer strAudit = new StringBuffer("");
		try {
			
			strAudit.append("RULE_ID_AT" + auditDelimiterColVal + vObject.getRuleIdAt());
			strAudit.append(auditDelimiter);
			
			if (ValidationUtil.isValid(vObject.getRuleId()))
				strAudit.append("RULE_ID" + auditDelimiterColVal + vObject.getRuleId().trim());
			else
				strAudit.append("RULE_ID" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);

			if (ValidationUtil.isValid(vObject.getFromTableId()))
				strAudit.append("FROM_TABLE_ID" + auditDelimiterColVal + vObject.getFromTableId());
			else
				strAudit.append("FROM_TABLE_ID" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);

			if (ValidationUtil.isValid(vObject.getToTableId()))
				strAudit.append("TO_TABLE_ID" + auditDelimiterColVal + vObject.getToTableId());
			else
				strAudit.append("TO_TABLE_ID" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);
			
			strAudit.append("JOIN_TYPE_NT" + auditDelimiterColVal + vObject.getJoinTypeNt());
			strAudit.append(auditDelimiter);
			
			if (ValidationUtil.isValid(vObject.getJoinType()))
				strAudit.append("JOIN_TYPE" + auditDelimiterColVal + vObject.getJoinType());
			else
				strAudit.append("JOIN_TYPE" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);
			
			if (ValidationUtil.isValid(vObject.getJoinString1()))
				strAudit.append("JOIN_STRING_1" + auditDelimiterColVal + vObject.getJoinString1().trim());
			else
				strAudit.append("JOIN_STRING_1" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);
			
			if (ValidationUtil.isValid(vObject.getJoinString2()))
				strAudit.append("JOIN_STRING_2" + auditDelimiterColVal + vObject.getJoinString2().trim());
			else
				strAudit.append("JOIN_STRING_2" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);
			
			if (ValidationUtil.isValid(vObject.getJoinString3()))
				strAudit.append("JOIN_STRING_3" + auditDelimiterColVal + vObject.getJoinString3().trim());
			else
				strAudit.append("JOIN_STRING_3" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);
			
			if (ValidationUtil.isValid(vObject.getJoinString4()))
				strAudit.append("JOIN_STRING_4" + auditDelimiterColVal + vObject.getJoinString4().trim());
			else
				strAudit.append("JOIN_STRING_4" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);
			
			if (ValidationUtil.isValid(vObject.getJoinString5()))
				strAudit.append("JOIN_STRING_5" + auditDelimiterColVal + vObject.getJoinString5().trim());
			else
				strAudit.append("JOIN_STRING_5" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);
			
			if (ValidationUtil.isValid(vObject.getFilterConditon1()))
				strAudit.append("FILTER_CONDITION_1" + auditDelimiterColVal + vObject.getFilterConditon1().trim());
			else
				strAudit.append("FILTER_CONDITION_1" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);
			
			if (ValidationUtil.isValid(vObject.getFilterConditon2()))
				strAudit.append("FILTER_CONDITION_2" + auditDelimiterColVal + vObject.getFilterConditon2().trim());
			else
				strAudit.append("FILTER_CONDITION_2" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);
			
			if (ValidationUtil.isValid(vObject.getFilterConditon3()))
				strAudit.append("FILTER_CONDITION_3" + auditDelimiterColVal + vObject.getFilterConditon3().trim());
			else
				strAudit.append("FILTER_CONDITION_3" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);

			if (ValidationUtil.isValid(vObject.getFilterConditon4()))
				strAudit.append("FILTER_CONDITION_4" + auditDelimiterColVal + vObject.getFilterConditon4().trim());
			else
				strAudit.append("FILTER_CONDITION_4" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);
			
			if (ValidationUtil.isValid(vObject.getFilterConditon5()))
				strAudit.append("FILTER_CONDITION_5" + auditDelimiterColVal + vObject.getFilterConditon5().trim());
			else
				strAudit.append("FILTER_CONDITION_5" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);

			strAudit.append("TABLE_STATUS_NT" + auditDelimiterColVal + vObject.getTabStatusNt());
			strAudit.append(auditDelimiter);

			if (ValidationUtil.isValid(vObject.getTabStatus()))
				strAudit.append("TABLE_STATUS" + auditDelimiterColVal + vObject.getTabStatus());
			else
				strAudit.append("TABLE_STATUS" + auditDelimiterColVal + "NULL");
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
	public List getLhsAndRhsTableId(String ruleId,int status) {
		try {
			String sql = "";
			String tableName = "";
			if(status == Constants.STATUS_ZERO)
				tableName = "RA_Recon_Act_Filter_tax";
			else
				tableName = "RA_Recon_Act_Filter_tax_PEND";
			
			sql = " Select Rule_ID,LHS_FILTER_TABLE,RHS_FILTER_TABLE,Min(Filter_Sequence) FILTER_SEQUENCE,CONDITION_TYPE "+
					" from "+tableName+" t1 "+ 
					" where Rule_ID = ? and upper(Condition_Type) = ?  "+
					" Group by LHS_FILTER_TABLE,RHS_FILTER_TABLE,Rule_ID,CONDITION_TYPE "+
					" Order by FILTER_SEQUENCE " ;
			
			Object[] args = {ruleId,"JOIN"};
			RowMapper mapper = new RowMapper() {
				@Override
				public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
					TaxReconActivityFilterVb reconTabRelationVb = new TaxReconActivityFilterVb();
					reconTabRelationVb.setFromTableId(rs.getInt("LHS_FILTER_TABLE"));
					reconTabRelationVb.setToTableId(rs.getInt("RHS_FILTER_TABLE"));
					reconTabRelationVb.setRuleId(rs.getString("RULE_ID"));
					reconTabRelationVb.setConditionType(rs.getString("CONDITION_TYPE"));
					reconTabRelationVb.setJoinType(1);
					return reconTabRelationVb;
				}
			};
			return getJdbcTemplate().query(sql, args,mapper);
		} catch(Exception e) {
			e.printStackTrace();
			return new ArrayList<>();
		}
	}
	
}
