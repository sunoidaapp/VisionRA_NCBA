package com.vision.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.vision.authentication.CustomContextHolder;
import com.vision.exception.ExceptionCode;
import com.vision.util.Constants;
import com.vision.util.ValidationUtil;
import com.vision.vb.ReconActivityFilterVb;
import com.vision.vb.ReconHeaderVb;


@Component
public class ReconActivityFilterDao extends AbstractDao<ReconActivityFilterVb> {
	
	@Autowired
	ReconTabRelationDao reconTabRelationDao;
	@Override
	protected void setServiceDefaults() {
		serviceName = "ReconActivityFilter";
		serviceDesc = "Recon Activity Filter ";
		tableName = "RA_Recon_Act_Filter";
		childTableName = "RA_Recon_Act_Filter";
		intCurrentUserId = CustomContextHolder.getContext().getVisionId();
	}

	protected int doInsertionApprReconActFilter(ReconActivityFilterVb vObject) {
		try {
			String query = "INSERT INTO RA_Recon_Act_Filter(RULE_ID,FILTER_SEQUENCE,LHS_FILTER_TABLE,LHS_FILTER_COLUMN,CONDITION_OPERATION, "
					+ "CONDITION_TYPE,RHS_FILTER_TABLE,RHS_FILTER_COLUMN,CONDITION_VALUE1,CONDITION_VALUE2,FILTER_STATUS_NT,FILTER_STATUS,"
					+ "RECORD_INDICATOR_NT,RECORD_INDICATOR,MAKER,VERIFIER,DATE_LAST_MODIFIED,DATE_CREATION,LHS_FILTER_COL_TYPE,RHS_FILTER_COL_TYPE)                              "
					+ "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " + getDbFunction("SYSDATE") + ","
					+ getDbFunction("SYSDATE") + ",?,?)";
			Object[] args = { vObject.getRuleId(), vObject.getFilterSequence(), vObject.getLhsFilterTable(),
					vObject.getLhsFilterColumn(), vObject.getConditionOperation(), vObject.getConditionType(),
					vObject.getRhsFilterTable(), vObject.getRhsFilterColumn(), vObject.getConditionValue1(),
					vObject.getConditionValue2(), vObject.getFilterStatusNt(), vObject.getFilterStatus(),
					vObject.getRecordIndicatorNt(), vObject.getRecordIndicator(), vObject.getMaker(),
					vObject.getVerifier(),vObject.getLhsFilterColType(),vObject.getRhsFilterColType() };
			getJdbcTemplate().update(query, args);
			return Constants.SUCCESSFUL_OPERATION;
		}catch(Exception e) {
			return Constants.ERRONEOUS_OPERATION;
		}
		
	}
	protected int doInsertionReconActFilterHis(ReconActivityFilterVb vObject) {
		try {
			String query =" INSERT INTO RA_Recon_Act_Filter_HIS(REF_NO,RULE_ID,FILTER_SEQUENCE "
					+" ,LHS_FILTER_TABLE,LHS_FILTER_COLUMN,CONDITION_OPERATION,CONDITION_TYPE "
					+" ,RHS_FILTER_TABLE,RHS_FILTER_COLUMN,CONDITION_VALUE1,CONDITION_VALUE2  "
					+" ,FILTER_STATUS_NT,FILTER_STATUS,RECORD_INDICATOR_NT,RECORD_INDICATOR   "
					+" ,MAKER,VERIFIER,DATE_LAST_MODIFIED,DATE_CREATION,LHS_FILTER_COL_TYPE,RHS_FILTER_COL_TYPE)                      "
					+" VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,  " + getDbFunction("SYSDATE") + ","
				+ getDbFunction("SYSDATE") + ",?,?)";
			Object[] args = {vObject.getRefNo(), vObject.getRuleId(), vObject.getFilterSequence(), vObject.getLhsFilterTable(),
					vObject.getLhsFilterColumn(), vObject.getConditionOperation(), vObject.getConditionType(),
					vObject.getRhsFilterTable(), vObject.getRhsFilterColumn(), vObject.getConditionValue1(),
					vObject.getConditionValue2(), vObject.getFilterStatusNt(), vObject.getFilterStatus(),
					vObject.getRecordIndicatorNt(), vObject.getRecordIndicator(), vObject.getMaker(),
					vObject.getVerifier(),vObject.getLhsFilterColType(),vObject.getRhsFilterColType()};
			getJdbcTemplate().update(query, args);
			return Constants.SUCCESSFUL_OPERATION;
		}catch(Exception e) {
			return Constants.ERRONEOUS_OPERATION;
		}
		
	}

	protected int doInsertionPendReconActFilter(ReconActivityFilterVb vObject) {
		String query = "INSERT INTO RA_Recon_Act_Filter_PEND(RULE_ID,FILTER_SEQUENCE,LHS_FILTER_TABLE,LHS_FILTER_COLUMN,CONDITION_OPERATION, "
				+ "CONDITION_TYPE,RHS_FILTER_TABLE,RHS_FILTER_COLUMN,CONDITION_VALUE1,CONDITION_VALUE2,FILTER_STATUS_NT,FILTER_STATUS,"
				+ "RECORD_INDICATOR_NT,RECORD_INDICATOR,MAKER,VERIFIER,DATE_LAST_MODIFIED,DATE_CREATION,LHS_FILTER_COL_TYPE,RHS_FILTER_COL_TYPE)                              "
				+ "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " + getDbFunction("SYSDATE") + ","
				+ getDbFunction("SYSDATE") + ",?,?)";
		
		Object[] args = { vObject.getRuleId(), vObject.getFilterSequence(), vObject.getLhsFilterTable(),
				vObject.getLhsFilterColumn(), vObject.getConditionOperation(), vObject.getConditionType(),
				vObject.getRhsFilterTable(), vObject.getRhsFilterColumn(), vObject.getConditionValue1(),
				vObject.getConditionValue2(), vObject.getFilterStatusNt(), vObject.getFilterStatus(),
				vObject.getRecordIndicatorNt(), vObject.getRecordIndicator(), vObject.getMaker(),
				vObject.getVerifier(),vObject.getLhsFilterColType(),vObject.getRhsFilterColType() };
		return getJdbcTemplate().update(query, args);
	}

	protected int deleteApprReconActFilter(ReconHeaderVb vObject) {
		String query = "DELETE FROM RA_Recon_Act_Filter  WHERE RULE_ID = ?  ";

		Object[] args = { vObject.getRuleId() };
		return getJdbcTemplate().update(query, args);
	}

	protected int deletePendReconActFilter(ReconHeaderVb vObject) {
		String query = "DELETE FROM RA_Recon_Act_Filter_PEND WHERE RULE_ID = ?  ";

		Object[] args = { vObject.getRuleId() };
		return getJdbcTemplate().update(query, args);
	}
	public List<ReconActivityFilterVb> getQueryReconActFilter(ReconHeaderVb dObj, int intStatus) {
		List<ReconActivityFilterVb> collTemp = null;
		setServiceDefaults();
		String strQueryAppr = null;
		String strQueryPend = null;
		strQueryAppr = new String(" SELECT TAPPR.RULE_ID,                    "
				+ "        TAPPR.FILTER_SEQUENCE,            " + "        TAPPR.LHS_FILTER_TABLE,           "
				+ "        TAPPR.LHS_FILTER_COLUMN,          " + "        TAPPR.CONDITION_OPERATION,        "
				+ "        TAPPR.CONDITION_TYPE,             " + "        TAPPR.RHS_FILTER_TABLE,           "
				+ "        TAPPR.RHS_FILTER_COLUMN,          " + "        TAPPR.CONDITION_VALUE1,           "
				+ "        TAPPR.CONDITION_VALUE2,           " + "        TAPPR.FILTER_STATUS_NT,           "
				+ "        TAPPR.FILTER_STATUS,              " + "        TAPPR.RECORD_INDICATOR_NT,        "
				+ "        TAPPR.RECORD_INDICATOR,           " + "        TAPPR.MAKER,                      "
				+ "        TAPPR.VERIFIER,                   " + "        TAPPR.DATE_LAST_MODIFIED,         "
				+ "        TAPPR.DATE_CREATION,              " + " TAPPR.LHS_FILTER_COL_TYPE,"
				+ "		   TAPPR.RHS_FILTER_COL_TYPE 		"
				+ "  FROM RA_Recon_Act_Filter TAPPR,NUM_SUB_TAB T2, NUM_SUB_TAB T3 "
				+ "  WHERE TAPPR.RULE_ID = ?                 " + "  AND T2.NUM_tab = TAPPR.RECORD_INDICATOR_NT       "
				+ "  AND T2.NUM_sub_tab = TAPPR.RECORD_INDICATOR      "
				+ "  AND T3.NUM_TAB = TAPPR.FILTER_STATUS_NT                 "
				+ "  AND T3.NUM_SUB_TAB = TAPPR.FILTER_STATUS                ");
		strQueryPend = new String(" SELECT TPEND.RULE_ID,                    "
				+ "        TPEND.FILTER_SEQUENCE,            " + "        TPEND.LHS_FILTER_TABLE,           "
				+ "        TPEND.LHS_FILTER_COLUMN,          " + "        TPEND.CONDITION_OPERATION,        "
				+ "        TPEND.CONDITION_TYPE,             " + "        TPEND.RHS_FILTER_TABLE,           "
				+ "        TPEND.RHS_FILTER_COLUMN,          " + "        TPEND.CONDITION_VALUE1,           "
				+ "        TPEND.CONDITION_VALUE2,           " + "        TPEND.FILTER_STATUS_NT,           "
				+ "        TPEND.FILTER_STATUS,              " + "        TPEND.RECORD_INDICATOR_NT,        "
				+ "        TPEND.RECORD_INDICATOR,           " + "        TPEND.MAKER,                      "
				+ "        TPEND.VERIFIER,                   " + "        TPEND.DATE_LAST_MODIFIED,         "
				+ "        TPEND.DATE_CREATION,              " + " TPEND.LHS_FILTER_COL_TYPE,"
				+ "		   TPEND.RHS_FILTER_COL_TYPE 		"
				+ "  FROM RA_Recon_Act_Filter_PEND TPEND, NUM_SUB_TAB T2, NUM_SUB_TAB T3 "
				+ "  WHERE TPEND.RULE_ID = ?                 " + "  AND T2.NUM_tab = TPEND.RECORD_INDICATOR_NT       "
				+ "  AND T2.NUM_sub_tab = TPEND.RECORD_INDICATOR      "
				+ "  AND T3.NUM_TAB = TPEND.FILTER_STATUS_NT                 "
				+ "  AND T3.NUM_SUB_TAB = TPEND.FILTER_STATUS                ");
		Object objParams[] = new Object[1];
		objParams[0] = dObj.getRuleId();

		try {
			if (intStatus == 0) {
				//logger.info("Executing approved query");
				collTemp = getJdbcTemplate().query(strQueryAppr.toString(), objParams, getReconActFilterMapper());
			} else {
				//logger.info("Executing pending query");
				collTemp = getJdbcTemplate().query(strQueryPend.toString(), objParams, getReconActFilterMapper());
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
	public List<ReconActivityFilterVb> getQueryReconActFilterRec(ReconActivityFilterVb dObj, int intStatus) {
		List<ReconActivityFilterVb> collTemp = null;
		setServiceDefaults();
		String strQueryAppr = null;
		String strQueryPend = null;
		strQueryAppr = new String(" SELECT TAPPR.RULE_ID,                    "
				+ "        TAPPR.FILTER_SEQUENCE,            " + "        TAPPR.LHS_FILTER_TABLE,           "
				+ "        TAPPR.LHS_FILTER_COLUMN,          " + "        TAPPR.CONDITION_OPERATION,        "
				+ "        TAPPR.CONDITION_TYPE,             " + "        TAPPR.RHS_FILTER_TABLE,           "
				+ "        TAPPR.RHS_FILTER_COLUMN,          " + "        TAPPR.CONDITION_VALUE1,           "
				+ "        TAPPR.CONDITION_VALUE2,           " + "        TAPPR.FILTER_STATUS_NT,           "
				+ "        TAPPR.FILTER_STATUS,              " + "        TAPPR.RECORD_INDICATOR_NT,        "
				+ "        TAPPR.RECORD_INDICATOR,           " + "        TAPPR.MAKER,                      "
				+ "        TAPPR.VERIFIER,                   " + "        TAPPR.DATE_LAST_MODIFIED,         "
				+ "        TAPPR.DATE_CREATION,              " + " TAPPR.LHS_FILTER_COL_TYPE,"
				+ "		   TAPPR.RHS_FILTER_COL_TYPE 		"
				+ "  FROM RA_Recon_Act_Filter TAPPR,NUM_SUB_TAB T2, NUM_SUB_TAB T3 "
				+ "  WHERE TAPPR.RULE_ID = ? AND  TAPPR.LHS_FILTER_TABLE = ? AND TAPPR.RHS_FILTER_TABLE = ?"
				+ "  AND T2.NUM_tab = TAPPR.RECORD_INDICATOR_NT       "
				+ "  AND T2.NUM_sub_tab = TAPPR.RECORD_INDICATOR      "
				+ "  AND T3.NUM_TAB = TAPPR.FILTER_STATUS_NT                 "
				+ "  AND T3.NUM_SUB_TAB = TAPPR.FILTER_STATUS                ");
		strQueryPend = new String(" SELECT TPEND.RULE_ID,                    "
				+ "        TPEND.FILTER_SEQUENCE,            " + "        TPEND.LHS_FILTER_TABLE,           "
				+ "        TPEND.LHS_FILTER_COLUMN,          " + "        TPEND.CONDITION_OPERATION,        "
				+ "        TPEND.CONDITION_TYPE,             " + "        TPEND.RHS_FILTER_TABLE,           "
				+ "        TPEND.RHS_FILTER_COLUMN,          " + "        TPEND.CONDITION_VALUE1,           "
				+ "        TPEND.CONDITION_VALUE2,           " + "        TPEND.FILTER_STATUS_NT,           "
				+ "        TPEND.FILTER_STATUS,              " + "        TPEND.RECORD_INDICATOR_NT,        "
				+ "        TPEND.RECORD_INDICATOR,           " + "        TPEND.MAKER,                      "
				+ "        TPEND.VERIFIER,                   " + "        TPEND.DATE_LAST_MODIFIED,         "
				+ "        TPEND.DATE_CREATION,              " + " TPEND.LHS_FILTER_COL_TYPE,"
				+ "		   TPEND.RHS_FILTER_COL_TYPE 		"
				+ "  FROM RA_Recon_Act_Filter_PEND TPEND, NUM_SUB_TAB T2, NUM_SUB_TAB T3 "
				+ "  WHERE TPEND.RULE_ID = ? AND  TPEND.LHS_FILTER_TABLE = ? AND   TPEND.RHS_FILTER_TABLE =?              "
				+  "  AND T2.NUM_tab = TPEND.RECORD_INDICATOR_NT       "
				+ "  AND T2.NUM_sub_tab = TPEND.RECORD_INDICATOR      "
				+ "  AND T3.NUM_TAB = TPEND.FILTER_STATUS_NT                 "
				+ "  AND T3.NUM_SUB_TAB = TPEND.FILTER_STATUS                ");
		Object objParams[] = new Object[3];
		objParams[0] = dObj.getRuleId();
		objParams[1] = dObj.getFromTableId();
		objParams[2] = dObj.getToTableId();

		try {
			if (intStatus == 0) {
				//logger.info("Executing approved query");
				collTemp = getJdbcTemplate().query(strQueryAppr.toString(), objParams, getReconActFilterMapper());
			} else {
				//logger.info("Executing pending query");
				collTemp = getJdbcTemplate().query(strQueryPend.toString(), objParams, getReconActFilterMapper());
			}
			return collTemp;
		} catch (Exception ex) {
			//ex.printStackTrace();
			////logger.error("Error: getQueryResults Exception :   ");
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
	public List<ReconActivityFilterVb> getQueryReconRelationFilter(ReconHeaderVb dObj, int intStatus,String conditionType) {
		List<ReconActivityFilterVb> collTemp = null;
		setServiceDefaults();
		String strQueryAppr = null;
		String strQueryPend = null;
		strQueryAppr = new String(" SELECT TAPPR.RULE_ID,                    "
				+ "        TAPPR.FILTER_SEQUENCE,            " + "        TAPPR.LHS_FILTER_TABLE,           "
				+ "        TAPPR.LHS_FILTER_COLUMN,          " + "        TAPPR.CONDITION_OPERATION,        "
				+ "        TAPPR.CONDITION_TYPE,             " + "        TAPPR.RHS_FILTER_TABLE,           "
				+ "        TAPPR.RHS_FILTER_COLUMN,          " + "        TAPPR.CONDITION_VALUE1,           "
				+ "        TAPPR.CONDITION_VALUE2,           " + "        TAPPR.FILTER_STATUS_NT,           "
				+ "        TAPPR.FILTER_STATUS,              " + "        TAPPR.RECORD_INDICATOR_NT,        "
				+ "        TAPPR.RECORD_INDICATOR,           " + "        TAPPR.MAKER,                      "
				+ "        TAPPR.VERIFIER,                   " + "        TAPPR.DATE_LAST_MODIFIED,         "
				+ "        TAPPR.DATE_CREATION,              " + " TAPPR.LHS_FILTER_COL_TYPE,"
				+ "		   TAPPR.RHS_FILTER_COL_TYPE 		"
				+ "  FROM RA_Recon_Act_Filter TAPPR,NUM_SUB_TAB T2, NUM_SUB_TAB T3 "
				+ "  WHERE TAPPR.RULE_ID = ? AND   UPPER(TAPPR.CONDITION_TYPE) = ?	"
				+ "  AND T2.NUM_tab = TAPPR.RECORD_INDICATOR_NT       "
				+ "  AND T2.NUM_sub_tab = TAPPR.RECORD_INDICATOR      "
				+ "  AND T3.NUM_TAB = TAPPR.FILTER_STATUS_NT                 "
				+ "  AND T3.NUM_SUB_TAB = TAPPR.FILTER_STATUS                ");
		strQueryPend = new String(" SELECT TPEND.RULE_ID,                    "
				+ "        TPEND.FILTER_SEQUENCE,            " + "        TPEND.LHS_FILTER_TABLE,           "
				+ "        TPEND.LHS_FILTER_COLUMN,          " + "        TPEND.CONDITION_OPERATION,        "
				+ "        TPEND.CONDITION_TYPE,             " + "        TPEND.RHS_FILTER_TABLE,           "
				+ "        TPEND.RHS_FILTER_COLUMN,          " + "        TPEND.CONDITION_VALUE1,           "
				+ "        TPEND.CONDITION_VALUE2,           " + "        TPEND.FILTER_STATUS_NT,           "
				+ "        TPEND.FILTER_STATUS,              " + "        TPEND.RECORD_INDICATOR_NT,        "
				+ "        TPEND.RECORD_INDICATOR,           " + "        TPEND.MAKER,                      "
				+ "        TPEND.VERIFIER,                   " + "        TPEND.DATE_LAST_MODIFIED,         "
				+ "        TPEND.DATE_CREATION,              " + " TPEND.LHS_FILTER_COL_TYPE,"
				+ "		   TPEND.RHS_FILTER_COL_TYPE 		"
				+ "  FROM RA_Recon_Act_Filter_PEND TPEND, NUM_SUB_TAB T2, NUM_SUB_TAB T3 "
				+ "  WHERE TPEND.RULE_ID = ?  AND UPPER(TPEND.CONDITION_TYPE) = ? "
				+  "  AND T2.NUM_tab = TPEND.RECORD_INDICATOR_NT       "
				+ "  AND T2.NUM_sub_tab = TPEND.RECORD_INDICATOR      "
				+ "  AND T3.NUM_TAB = TPEND.FILTER_STATUS_NT                 "
				+ "  AND T3.NUM_SUB_TAB = TPEND.FILTER_STATUS                ");
		Object objParams[] = new Object[2]; 
		objParams[0] = dObj.getRuleId();
		objParams[1] = conditionType;

		try {
			if (intStatus == 0) {
				//logger.info("Executing approved query");
				collTemp = getJdbcTemplate().query(strQueryAppr.toString(), objParams, getReconActFilterMapper());
			} else {
				//logger.info("Executing pending query");
				collTemp = getJdbcTemplate().query(strQueryPend.toString(), objParams, getReconActFilterMapper());
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

	protected RowMapper getReconActFilterMapper() {
		RowMapper mapper = new RowMapper() {
			@Override
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				ReconActivityFilterVb reconActivityFilterVb = new ReconActivityFilterVb();
				reconActivityFilterVb.setRuleId(rs.getString("RULE_ID"));
				reconActivityFilterVb.setFilterSequence(rs.getInt("FILTER_SEQUENCE"));
				reconActivityFilterVb.setLhsFilterTable(rs.getString("LHS_FILTER_TABLE"));
				reconActivityFilterVb.setLhsFilterColumn(rs.getString("LHS_FILTER_COLUMN"));
				reconActivityFilterVb.setConditionOperation(rs.getString("CONDITION_OPERATION"));
				reconActivityFilterVb.setConditionType(rs.getString("CONDITION_TYPE"));
				reconActivityFilterVb.setRhsFilterColumn(rs.getString("RHS_FILTER_COLUMN"));
				reconActivityFilterVb.setRhsFilterTable(rs.getString("RHS_FILTER_TABLE"));
				reconActivityFilterVb.setConditionValue1(rs.getString("CONDITION_VALUE1"));
				reconActivityFilterVb.setConditionValue2(rs.getString("CONDITION_VALUE2"));
				reconActivityFilterVb.setFilterStatus(rs.getInt("FILTER_STATUS"));
				reconActivityFilterVb.setRecordIndicator(rs.getInt("RECORD_INDICATOR"));
				reconActivityFilterVb.setMaker(rs.getInt("MAKER"));
				reconActivityFilterVb.setVerifier(rs.getInt("VERIFIER"));
				reconActivityFilterVb.setDateLastModified(rs.getString("DATE_LAST_MODIFIED"));
				reconActivityFilterVb.setDateCreation(rs.getString("DATE_CREATION"));
				if("FILTER".equalsIgnoreCase(reconActivityFilterVb.getConditionType())) {
					reconActivityFilterVb.setFilterTableId(rs.getString("LHS_FILTER_TABLE"));
				}
				reconActivityFilterVb.setLhsFilterColType(rs.getString("LHS_FILTER_COL_TYPE"));
				reconActivityFilterVb.setRhsFilterColType(rs.getString("RHS_FILTER_COL_TYPE"));
				return reconActivityFilterVb;
			}
		};
		return mapper;

	}

	public ExceptionCode deleteAndInsertApprRelationFilter(ReconHeaderVb vObject) {
		ExceptionCode exceptionCode = new ExceptionCode();
		List<ReconActivityFilterVb> collTemp = getQueryReconActFilter(vObject, Constants.STATUS_ZERO);
		List<ReconActivityFilterVb> collTempTab=reconTabRelationDao.getQueryReconTabRelationRuleid(vObject, Constants.STATUS_ZERO);
		int delCnt = 0;
		if (collTemp != null && collTemp.size() > 0) {
			collTemp.forEach(filter ->{
				filter.setRefNo(vObject.getRefNo());
				doInsertionReconActFilterHis(filter);
			});
			delCnt = deleteApprReconActFilter(vObject);
		}
		
		if(collTempTab != null && collTempTab.size() > 0) {
			collTempTab.forEach(relationTab ->{
				relationTab.setRefNo(vObject.getRefNo());
				reconTabRelationDao.doInsertionReconTabRelationHis(relationTab);
			});
			delCnt = reconTabRelationDao.deleteApprReconTabRelation(vObject);
		}
			
		ArrayList<ReconActivityFilterVb> tableRellst = new ArrayList<ReconActivityFilterVb>();
		Boolean filterCondition = false;
		if(vObject.getRelationJoinsLst() != null && vObject.getRelationJoinsLst().size() > 0 ) {
			//Relation Joins
			for (ReconActivityFilterVb reconRelationVb : vObject.getRelationJoinsLst()) {
				reconRelationVb.setMaker(vObject.getMaker());
				reconRelationVb.setRecordIndicator(vObject.getRecordIndicator());
				reconRelationVb.setRuleId(vObject.getRuleId());
				if (reconRelationVb.getJoinsLst() != null && reconRelationVb.getJoinsLst().size() > 0) {
					List<ReconActivityFilterVb> joinsLst = reconRelationVb.getJoinsLst();
					for (ReconActivityFilterVb joinVb : joinsLst) {
						joinVb.setLhsFilterTable(joinVb.getLhsFilterTable());
						joinVb.setRhsFilterTable(joinVb.getRhsFilterTable());
						joinVb.setRecordIndicator(vObject.getRecordIndicator());
						joinVb.setLhsFilterTable(reconRelationVb.getLhsFilterTable());
						joinVb.setRhsFilterTable(reconRelationVb.getRhsFilterTable());
						joinVb.setMaker(vObject.getMaker());
						joinVb.setVerifier(vObject.getVerifier());
						retVal = doInsertionApprReconActFilter(joinVb);
						if (retVal != Constants.SUCCESSFUL_OPERATION) {
							exceptionCode = getResultObject(retVal);
							throw buildRuntimeCustomException(exceptionCode);
						}
					}
				}
				ReconActivityFilterVb filterVb = new ReconActivityFilterVb();
				filterVb.setLhsFilterTable(reconRelationVb.getLhsFilterTable());
				filterVb.setRhsFilterTable(reconRelationVb.getRhsFilterTable());
				filterVb.setJoinString1(reconRelationVb.getJoinString1());
				filterVb.setJoinType(reconRelationVb.getJoinType());
				if(!filterCondition) {
					filterCondition = true;
					filterVb.setFilterConditon1(vObject.getFilterConditon1());
				}
				exceptionCode = getTableRelationsList(tableRellst,filterVb);
				if(exceptionCode.getErrorCode() != Constants.ERRONEOUS_OPERATION) {
					tableRellst = (ArrayList<ReconActivityFilterVb>)exceptionCode.getRequest();
				}
			}
		}
		if(tableRellst != null && tableRellst.size() > 0) {
			for(ReconActivityFilterVb tabRelVb : tableRellst) {
				tabRelVb.setMaker(vObject.getMaker());
				tabRelVb.setRecordIndicator(vObject.getRecordIndicator());
				tabRelVb.setRuleId(vObject.getRuleId());
				tabRelVb.setFromTableId(Integer.parseInt(tabRelVb.getLhsFilterTable()));
				tabRelVb.setToTableId(Integer.parseInt(tabRelVb.getRhsFilterTable()));
				retVal = reconTabRelationDao.doInsertionApprReconTabRelation(tabRelVb);
			}
		}
		//Relation Filters
		if (vObject.getFiltersLst() != null && vObject.getFiltersLst().size() > 0) {
			for (ReconActivityFilterVb reconFilterVb : vObject.getFiltersLst()) {
				reconFilterVb.setLhsFilterTable(reconFilterVb.getFilterTableId());
				reconFilterVb.setRhsFilterTable("");
				reconFilterVb.setRuleId(vObject.getRuleId());
				reconFilterVb.setRecordIndicator(vObject.getRecordIndicator());
				reconFilterVb.setMaker(vObject.getMaker());
				reconFilterVb.setVerifier(vObject.getVerifier());
				reconFilterVb.setVerifier(vObject.getVerifier());
				retVal = doInsertionApprReconActFilter(reconFilterVb);
				if (retVal != Constants.SUCCESSFUL_OPERATION) {
					exceptionCode = getResultObject(retVal);
					throw buildRuntimeCustomException(exceptionCode);
				}
				writeAuditLog(reconFilterVb, null);
			}
		}
		/*if(ValidationUtil.isValid(vObject.getJoinString2())|| ValidationUtil.isValid(vObject.getFilterConditon1())) {
			ReconActivityFilterVb reconTabFilter = new ReconActivityFilterVb();
			reconTabFilter.setRuleId(vObject.getRuleId());
			reconTabFilter.setFilterConditon1(vObject.getFilterConditon1());
			reconTabFilter.setFromTableId(Constants.SUCCESSFUL_OPERATION);
			reconTabFilter.setToTableId(Constants.SUCCESSFUL_OPERATION);
			reconTabFilter.setJoinString2(vObject.getJoinString2());
			reconTabFilter.setRecordIndicator(vObject.getRecordIndicator());
			reconTabFilter.setMaker(vObject.getMaker());
			reconTabFilter.setVerifier(vObject.getVerifier());
			retVal = reconTabRelationDao.doInsertionApprReconTabRelation(reconTabFilter);
		}*/
		exceptionCode = getResultObject(Constants.SUCCESSFUL_OPERATION);
		return exceptionCode;
	}

	public ExceptionCode deleteAndInsertPendRelationFilter(ReconHeaderVb vObject) {
		ExceptionCode exceptionCode = new ExceptionCode();
		List<ReconActivityFilterVb> collTemp = getQueryReconActFilter(vObject, Constants.SUCCESSFUL_OPERATION);
		if (collTemp != null && collTemp.size() > 0) {
			int delCnt = deletePendReconActFilter(vObject);
			delCnt = reconTabRelationDao.deletePendReconTabRelation(vObject);
		}
		ArrayList<ReconActivityFilterVb> tableRellst = new ArrayList<ReconActivityFilterVb>();
		Boolean filterCondition = false;
		if(vObject.getRelationJoinsLst() != null && !vObject.getRelationJoinsLst().isEmpty()) {
			for (ReconActivityFilterVb reconRelationVb : vObject.getRelationJoinsLst()) {
				if (reconRelationVb.getJoinsLst() != null && reconRelationVb.getJoinsLst().size() > 0) {
					List<ReconActivityFilterVb> joinsLst = reconRelationVb.getJoinsLst();
					for (ReconActivityFilterVb joinVb : joinsLst) {
						joinVb.setLhsFilterTable(joinVb.getLhsFilterTable());
						joinVb.setRhsFilterTable(joinVb.getRhsFilterTable());
						joinVb.setRecordIndicator(vObject.getRecordIndicator());
						joinVb.setLhsFilterTable(reconRelationVb.getLhsFilterTable());
						joinVb.setRhsFilterTable(reconRelationVb.getRhsFilterTable());
						joinVb.setMaker(vObject.getMaker());
						joinVb.setVerifier(vObject.getVerifier());
						retVal = doInsertionPendReconActFilter(joinVb);
					}
				}
				
				ReconActivityFilterVb filterVb = new ReconActivityFilterVb();
				filterVb.setLhsFilterTable(reconRelationVb.getLhsFilterTable());
				filterVb.setRhsFilterTable(reconRelationVb.getRhsFilterTable());
				filterVb.setJoinString1(reconRelationVb.getJoinString1());
				filterVb.setJoinType(reconRelationVb.getJoinType());
				if(!filterCondition) {
					filterCondition = true;
					filterVb.setFilterConditon1(vObject.getFilterConditon1());
				}
				exceptionCode = getTableRelationsList(tableRellst,filterVb);
				if(exceptionCode.getErrorCode() != Constants.ERRONEOUS_OPERATION) {
					tableRellst = (ArrayList<ReconActivityFilterVb>)exceptionCode.getRequest();
				}
			}
		}
		for(ReconActivityFilterVb tabRelVb : tableRellst) {
			tabRelVb.setMaker(vObject.getMaker());
			tabRelVb.setRecordIndicator(vObject.getRecordIndicator());
			tabRelVb.setRuleId(vObject.getRuleId());
			tabRelVb.setFromTableId(Integer.parseInt(tabRelVb.getLhsFilterTable()));
			tabRelVb.setToTableId(Integer.parseInt(tabRelVb.getRhsFilterTable()));
			retVal = reconTabRelationDao.doInsertionPendReconTabRelation(tabRelVb);
		}
		
		//Relation Filters
		if (vObject.getFiltersLst() != null && vObject.getFiltersLst().size() > 0) {
			for (ReconActivityFilterVb reconFilterVb : vObject.getFiltersLst()) {
				reconFilterVb.setLhsFilterTable(reconFilterVb.getFilterTableId());
				reconFilterVb.setRhsFilterTable("");
				reconFilterVb.setRuleId(vObject.getRuleId());
				reconFilterVb.setRecordIndicator(vObject.getRecordIndicator());
				reconFilterVb.setMaker(vObject.getMaker());
				reconFilterVb.setVerifier(vObject.getVerifier());
				reconFilterVb.setVerifier(vObject.getVerifier());
				retVal = doInsertionPendReconActFilter(reconFilterVb);
				if (retVal != Constants.SUCCESSFUL_OPERATION) {
					exceptionCode = getResultObject(retVal);
					throw buildRuntimeCustomException(exceptionCode);
				}
				writeAuditLog(reconFilterVb, null);
			}
		}
		/*if(ValidationUtil.isValid(vObject.getJoinString2())|| ValidationUtil.isValid(vObject.getFilterConditon1())) {
			ReconActivityFilterVb reconTabFilter = new ReconActivityFilterVb();
			reconTabFilter.setRuleId(vObject.getRuleId());
			reconTabFilter.setFilterConditon1(vObject.getFilterConditon1());
			reconTabFilter.setFromTableId(Constants.SUCCESSFUL_OPERATION);
			reconTabFilter.setToTableId(Constants.SUCCESSFUL_OPERATION);
			reconTabFilter.setJoinString2(vObject.getJoinString2());
			reconTabFilter.setRecordIndicator(vObject.getRecordIndicator());
			reconTabFilter.setMaker(vObject.getMaker());
			reconTabFilter.setVerifier(vObject.getVerifier());
			retVal = reconTabRelationDao.doInsertionPendReconTabRelation(reconTabFilter);
		}*/
		
		exceptionCode = getResultObject(Constants.SUCCESSFUL_OPERATION);
		return exceptionCode;
	}
	
	@Override
	protected String getAuditString(ReconActivityFilterVb vObject) {
		final String auditDelimiter = vObject.getAuditDelimiter();
		final String auditDelimiterColVal = vObject.getAuditDelimiterColVal();
		StringBuffer strAudit = new StringBuffer("");
		try {
			if (ValidationUtil.isValid(vObject.getRuleId()))
				strAudit.append("RULE_ID" + auditDelimiterColVal + vObject.getRuleId().trim());
			else
				strAudit.append("RULE_ID" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);

			if (ValidationUtil.isValid(vObject.getFilterSequence()))
				strAudit.append("FILTER_SEQUENCE" + auditDelimiterColVal + vObject.getFilterSequence());
			else
				strAudit.append("FILTER_SEQUENCE" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);

			if (ValidationUtil.isValid(vObject.getLhsFilterTable()))
				strAudit.append("LHS_FILTER_TABLE" + auditDelimiterColVal + vObject.getLhsFilterTable().trim());
			else
				strAudit.append("LHS_FILTER_TABLE" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);

			if (ValidationUtil.isValid(vObject.getLhsFilterColumn()))
				strAudit.append("LHS_FILTER_COLUMN" + auditDelimiterColVal + vObject.getLhsFilterColumn().trim());
			else
				strAudit.append("LHS_FILTER_COLUMN" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);

			if (ValidationUtil.isValid(vObject.getConditionOperation()))
				strAudit.append("CONDITION_OPERATION" + auditDelimiterColVal + vObject.getConditionOperation().trim());
			else
				strAudit.append("CONDITION_OPERATION" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);

			if (ValidationUtil.isValid(vObject.getConditionType()))
				strAudit.append("CONDITION_TYPE" + auditDelimiterColVal + vObject.getConditionType().trim());
			else
				strAudit.append("CONDITION_TYPE" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);

			if (ValidationUtil.isValid(vObject.getRhsFilterTable()))
				strAudit.append("RHS_FILTER_TABLE" + auditDelimiterColVal + vObject.getRhsFilterTable().trim());
			else
				strAudit.append("RHS_FILTER_TABLE" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);

			if (ValidationUtil.isValid(vObject.getRhsFilterColumn()))
				strAudit.append("RHS_FILTER_COLUMN" + auditDelimiterColVal + vObject.getRhsFilterColumn().trim());
			else
				strAudit.append("RHS_FILTER_COLUMN" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);

			if (ValidationUtil.isValid(vObject.getConditionValue1()))
				strAudit.append("CONDITION_VALUE1" + auditDelimiterColVal + vObject.getConditionValue1().trim());
			else
				strAudit.append("CONDITION_VALUE1" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);

			if (ValidationUtil.isValid(vObject.getConditionValue1()))
				strAudit.append("CONDITION_VALUE2" + auditDelimiterColVal + vObject.getConditionValue2().trim());
			else
				strAudit.append("CONDITION_VALUE2" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);

			strAudit.append("FILTER_STATUS_NT" + auditDelimiterColVal + vObject.getFilterStatusNt());
			strAudit.append(auditDelimiter);

			if (ValidationUtil.isValid(vObject.getFilterStatus()))
				strAudit.append("FILTER_STATUS" + auditDelimiterColVal + vObject.getFilterStatus());
			else
				strAudit.append("FILTER_STATUS" + auditDelimiterColVal + "NULL");
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
	public ExceptionCode getTableRelationsList(ArrayList<ReconActivityFilterVb> tableRellst,ReconActivityFilterVb tableRelations) {
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			int fromTableIndex = 0;
			int toTableIndex = 0;
			if(tableRellst != null && !tableRellst.isEmpty()) {
				//From Table - Check 
				for(int idx = 0;idx < tableRellst.size();idx++) {
					if(tableRellst.get(idx).getLhsFilterTable().equals(tableRelations.getLhsFilterTable())) {
						fromTableIndex = idx+1;
					}else if(tableRellst.get(idx).getRhsFilterTable().equals(tableRelations.getLhsFilterTable())) {
						fromTableIndex = idx+1;
					}
				}
				//To Table - Check 
				for(int idx = 0;idx < tableRellst.size();idx++) {
					if(tableRellst.get(idx).getLhsFilterTable().equals(tableRelations.getRhsFilterTable())) {
						toTableIndex = idx+1;
					}else if(tableRellst.get(idx).getRhsFilterTable().equals(tableRelations.getRhsFilterTable())) {
						toTableIndex = idx+1;
					}
				}
			}
			if(fromTableIndex == 0 || toTableIndex == 0) {
				tableRellst.add(tableRelations);	
			}else {
				if(fromTableIndex < toTableIndex) {
					String relationJoinString = tableRellst.get(toTableIndex-1).getJoinString1();
					relationJoinString = relationJoinString+" AND "+tableRelations.getJoinString1();
					tableRellst.get(toTableIndex-1).setJoinString1(relationJoinString);
				}else {
					String relationJoinString = tableRellst.get(fromTableIndex-1).getJoinString1();
					relationJoinString = relationJoinString+" AND "+tableRelations.getJoinString1();
					tableRellst.get(fromTableIndex-1).setJoinString1(relationJoinString);	
				}
			}
			exceptionCode.setRequest(tableRellst);
			exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
		} catch(Exception e) {
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg(e.getMessage());
		}
		return exceptionCode;
	}
	class TableRelations{
		String fromTableId = "";
		String toTableId = "";
		int fromTable;
		int toTable;
		String joinString = "";
		String joinType = "INNER JOIN";
		
		public String getFromTableId() {
			return fromTableId;
		}
		public void setFromTableId(String fromTableId) {
			this.fromTableId = fromTableId;
		}
		public String getToTableId() {
			return toTableId;
		}
		public void setToTableId(String toTableId) {
			this.toTableId = toTableId;
		}
		public String getJoinString() {
			return joinString;
		}
		public void setJoinString(String joinString) {
			this.joinString = joinString;
		}
		public String getJoinType() {
			return joinType;
		}
		public void setJoinType(String joinType) {
			this.joinType = joinType;
		}
		public int getFromTable() {
			return fromTable;
		}
		public void setFromTable(int fromTable) {
			this.fromTable = fromTable;
		}
		public int getToTable() {
			return toTable;
		}
		public void setToTable(int toTable) {
			this.toTable = toTable;
		}
	}
}
