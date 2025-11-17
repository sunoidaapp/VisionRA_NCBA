package com.vision.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.UncategorizedSQLException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.vision.authentication.CustomContextHolder;
import com.vision.exception.ExceptionCode;
import com.vision.exception.RuntimeCustomException;
import com.vision.util.CommonUtils;
import com.vision.util.Constants;
import com.vision.util.ValidationUtil;
import com.vision.vb.BusinessLineHeaderVb;
import com.vision.vb.ConcessionActivityConfigHeaderVb;
import com.vision.vb.ConcessionActivityFilterVb;
import com.vision.vb.SmartSearchVb;
import com.vision.vb.TransLineHeaderVb;
import com.vision.vb.VisionUsersVb;

@Component
public class ConcessionActivityHeaderDao extends AbstractDao<ConcessionActivityConfigHeaderVb> {
	@Autowired
	CommonDao commonDao;
	@Autowired
	ConcessionActivityFilterDao concessionFilterDao;

	@Value("${app.databaseType}")
	private String databaseType;

	@Override
	protected RowMapper getMapper() {
		RowMapper mapper = new RowMapper() {
			@Override
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				ConcessionActivityConfigHeaderVb vObject = new ConcessionActivityConfigHeaderVb();
				vObject.setCountry(rs.getString("COUNTRY"));
				vObject.setLeBook(rs.getString("LE_BOOK"));
				vObject.setActivityId(rs.getString("ACTIVITY_ID"));
				vObject.setActivityDesc(rs.getString("ACTIVITY_DESCRIPTION"));
				vObject.setTransLineId(rs.getString("TRANS_LINE_ID"));
				vObject.setActSourceTable(rs.getString("ACTIVITY_SOURCE_TABLE"));
				vObject.setAggFun(rs.getString("AGG_FUNCTION"));
				vObject.setAggCol(rs.getString("AGG_COLUMN"));
				vObject.setTimeSeriesType(rs.getString("TIMESERIES_TYPE"));
				vObject.setTimeSeriesTypeDesc(rs.getString("TIMESERIES_TYPE_DESC"));
				vObject.setActivityType(rs.getString("ACTIVITY_TYPE"));
				vObject.setActivityTypeDesc(rs.getString("ACTIVITY_TYPE_DESC"));
				vObject.setRangeFrom(rs.getString("RANGE_FROM"));
				vObject.setRangeTo(rs.getString("RANGE_TO"));
				vObject.setSortColumns(rs.getString("SORT_COLUMNS"));
				vObject.setActivityStatus(rs.getInt("Activity_Status"));
				vObject.setActivityStatusDesc(rs.getString("Activity_Status_Desc"));
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
	public List<ConcessionActivityConfigHeaderVb> getQueryPopupResults(ConcessionActivityConfigHeaderVb dObj) {
		Vector<Object> params = new Vector<Object>();
		StringBuffer strBufApprove = null;
		StringBuffer strBufPending = null;
		String strWhereNotExists = null;
		String orderBy = "";

		strBufApprove = new StringBuffer(
				"Select * from ( SELECT TAppr.COUNTRY, TAppr.LE_BOOK,TAppr.ACTIVITY_ID, ACTIVITY_DESCRIPTION, "
						+ "	TAppr.TRANS_LINE_ID, TAppr.ACTIVITY_SOURCE_TABLE,TAppr.AGG_FUNCTION,TAppr.AGG_COLUMN,"
						+ "	TAppr.TIMESERIES_TYPE,TAppr.ACTIVITY_TYPE,TAppr.RANGE_FROM,TAppr.RANGE_TO,TAppr.SORT_COLUMNS, "
						+ " (select T1.ALPHA_SUBTAB_DESCRIPTION from ALPHA_SUB_TAB T1 "
						+ "      where t1.Alpha_tab = TAppr.TIMESERIES_TYPE_AT and T1.ALPHA_SUB_TAB=TAppr.TIMESERIES_TYPE) TIMESERIES_TYPE_DESC, "
						+ " (select T1.ALPHA_SUBTAB_DESCRIPTION from ALPHA_SUB_TAB T1 "
						+ "      where t1.Alpha_tab = TAppr.ACTIVITY_TYPE_AT and T1.ALPHA_SUB_TAB=TAppr.ACTIVITY_TYPE) ACTIVITY_TYPE_DESC, "
						+ " TAppr.Activity_Status,T3.NUM_SUBTAB_DESCRIPTION Activity_Status_DESC, "
						+ " TAppr.RECORD_INDICATOR,T4.NUM_SUBTAB_DESCRIPTION RECORD_INDICATOR_DESC,"
						+ " TAppr. MAKER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "
						+ getDbFunction("NVL") + "(TAppr.MAKER,0) ) MAKER_NAME, "
						+ " TAppr.VERIFIER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "
						+ getDbFunction("NVL") + "(TAppr.VERIFIER,0) ) VERIFIER_NAME,  " + " "
						+ getDbFunction("DATEFUNC") + "(TAppr.DATE_LAST_MODIFIED, 'dd-MMM-yyyy "
						+ getDbFunction("TIME") + "') DATE_LAST_MODIFIED ,"
						+ getDbFunction("DATEFUNC") + "(TAppr.DATE_CREATION, 'dd-MMM-yyyy "
						+ getDbFunction("TIME") + "') DATE_CREATION "
						+ " FROM RA_Mst_Concession_Activity TAppr ,NUM_SUB_TAB T3,NUM_SUB_TAB T4     " + " Where  "
						+ "	 t3.NUM_tab = TAppr.Activity_Status_NT  " + " and t3.NUM_sub_tab = TAppr.Activity_Status  "
						+ " and t4.NUM_tab = TAppr.RECORD_INDICATOR_NT  "
						+ " and t4.NUM_sub_tab = TAppr.RECORD_INDICATOR ) TAppr");

		strWhereNotExists = new String(
				" Not Exists (Select 'X' From RA_Mst_Concession_Activity_PEND TPEND WHERE TAppr.COUNTRY = TPend.COUNTRY"
						+ " AND TAppr.LE_BOOK = TPend.LE_BOOK  AND TAPPR.ACTIVITY_ID = TPEND.ACTIVITY_ID)");

		strBufPending = new StringBuffer(
				"Select * from ( SELECT TPend.COUNTRY, TPend.LE_BOOK,TPend.ACTIVITY_ID, ACTIVITY_DESCRIPTION, "
						+ "	TPend.TRANS_LINE_ID, TPend.ACTIVITY_SOURCE_TABLE,TPend.AGG_FUNCTION,TPend.AGG_COLUMN,"
						+ "TPend.TIMESERIES_TYPE,TPend.ACTIVITY_TYPE,TPend.RANGE_FROM,TPend.RANGE_TO,TPend.SORT_COLUMNS, "
						+ " (select T1.ALPHA_SUBTAB_DESCRIPTION from ALPHA_SUB_TAB T1 "
						+ "      where t1.Alpha_tab = TPend.TIMESERIES_TYPE_AT and T1.ALPHA_SUB_TAB=TPend.TIMESERIES_TYPE) TIMESERIES_TYPE_DESC ,"
						+ " (select T1.ALPHA_SUBTAB_DESCRIPTION from ALPHA_SUB_TAB T1 "
						+ "      where t1.Alpha_tab = TPend.ACTIVITY_TYPE_AT and T1.ALPHA_SUB_TAB=TPend.ACTIVITY_TYPE) ACTIVITY_TYPE_DESC, "
						+ " TPend.Activity_Status,T3.NUM_SUBTAB_DESCRIPTION Activity_Status_DESC, "
						+ " TPend.RECORD_INDICATOR,T4.NUM_SUBTAB_DESCRIPTION RECORD_INDICATOR_DESC,"
						+ " TPend. MAKER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "
						+ getDbFunction("NVL") + "(TPend.MAKER,0) ) MAKER_NAME, "
						+ " TPend.VERIFIER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "
						+ getDbFunction("NVL") + "(TPend.VERIFIER,0) ) VERIFIER_NAME,  " + " "
						+ getDbFunction("DATEFUNC") + "(TPend.DATE_LAST_MODIFIED, 'dd-MMM-yyyy "
						+ getDbFunction("TIME") + "') DATE_LAST_MODIFIED ,"
						+ getDbFunction("DATEFUNC") + "(TPend.DATE_CREATION, 'dd-MMM-yyyy "
						+ getDbFunction("TIME") + "') DATE_CREATION "
						+ " FROM RA_Mst_Concession_Activity_PEND TPend ,NUM_SUB_TAB T3,NUM_SUB_TAB T4     " + " Where  "
						+ "	 t3.NUM_tab = TPend.Activity_Status_NT  " + " and t3.NUM_sub_tab = TPend.Activity_Status  "
						+ " and t4.NUM_tab = TPend.RECORD_INDICATOR_NT  "
						+ " and t4.NUM_sub_tab = TPend.RECORD_INDICATOR) TPend");
		try {
			if (dObj.getSmartSearchOpt() != null && dObj.getSmartSearchOpt().size() > 0) {
				int count = 1;
				for (SmartSearchVb data : dObj.getSmartSearchOpt()) {
					if (count == dObj.getSmartSearchOpt().size()) {
						data.setJoinType("");
					} else {
						if (!ValidationUtil.isValid(data.getJoinType()) && !("AND".equalsIgnoreCase(data.getJoinType())
								|| "OR".equalsIgnoreCase(data.getJoinType()))) {
							data.setJoinType("AND");
						}
					}
					String val = CommonUtils.criteriaBasedVal(data.getCriteria(), data.getValue());
					switch (data.getObject()) {
					case "country":
						CommonUtils.addToQuerySearch(" upper(TAPPR.COUNTRY) " + val, strBufApprove, data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.COUNTRY) " + val, strBufPending, data.getJoinType());
						break;

					case "leBook":
						CommonUtils.addToQuerySearch(" upper(TAPPR.LE_BOOK) " + val, strBufApprove, data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.LE_BOOK) " + val, strBufPending, data.getJoinType());
						break;

					case "activityId":
						CommonUtils.addToQuerySearch(" upper(TAPPR.ACTIVITY_ID) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.ACTIVITY_ID) " + val, strBufPending,
								data.getJoinType());
						break;

					case "activityDesc":
						CommonUtils.addToQuerySearch(" upper(TAPPR.ACTIVITY_DESCRIPTION) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.ACTIVITY_DESCRIPTION) " + val, strBufPending,
								data.getJoinType());
						break;

					case "activityStatusDesc":
						CommonUtils.addToQuerySearch(" upper(TAPPR.Activity_Status_DESC) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.Activity_Status_DESC) " + val, strBufPending,
								data.getJoinType());
						break;

					case "recordIndicatorDesc":
						CommonUtils.addToQuerySearch(" upper(TAPPR.RECORD_INDICATOR_DESC) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.RECORD_INDICATOR_DESC) " + val, strBufPending,
								data.getJoinType());
						break;

					case "dateCreation":
						CommonUtils.addToQuerySearch(
								" " + getDbFunction("DATEFUNC") + "(TAPPR.DATE_CREATION,'DD-MM-YYYY "
										+ getDbFunction("TIME") + "') " + val,
								strBufApprove, data.getJoinType());
						CommonUtils.addToQuerySearch(
								" " + getDbFunction("DATEFUNC") + "(TPend.DATE_CREATION,'DD-MM-YYYY "
										+ getDbFunction("TIME") + "')  " + val,
								strBufPending, data.getJoinType());
						break;

					case "dateLastModified":
						CommonUtils.addToQuerySearch(
								" " + getDbFunction("DATEFUNC") + "(TAPPR.DATE_LAST_MODIFIED,'DD-MM-YYYY "
										+ getDbFunction("TIME") + "') " + val,
								strBufApprove, data.getJoinType());
						CommonUtils.addToQuerySearch(
								" " + getDbFunction("DATEFUNC") + "(TPend.DATE_LAST_MODIFIED,'DD-MM-YYYY "
										+ getDbFunction("TIME") + "') " + val,
								strBufPending, data.getJoinType());
						break;

					case "makerName":
						CommonUtils.addToQuerySearch(" (TAPPR.MAKER_NAME) IN (" + val + ") ", strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" (TPend.MAKER_NAME) IN (" + val + ") ", strBufPending,
								data.getJoinType());
						break;

					default:
					}
					count++;
				}
			}
			VisionUsersVb visionUsersVb = CustomContextHolder.getContext();
			if (("Y".equalsIgnoreCase(visionUsersVb.getUpdateRestriction()))) {
				if (ValidationUtil.isValid(visionUsersVb.getCountry())) {
					CommonUtils.addToQuery(" COUNTRY IN ('" + visionUsersVb.getCountry() + "') ", strBufApprove);
					CommonUtils.addToQuery(" COUNTRY IN ('" + visionUsersVb.getCountry() + "') ", strBufPending);
				}
				if (ValidationUtil.isValid(visionUsersVb.getLeBook())) {
					CommonUtils.addToQuery(" LE_BOOK IN ('" + visionUsersVb.getLeBook() + "') ", strBufApprove);
					CommonUtils.addToQuery(" LE_BOOK IN ('" + visionUsersVb.getLeBook() + "') ", strBufPending);
				}
			}
			orderBy = " Order by DATE_LAST_MODIFIED DESC ";
			return getQueryPopupResults(dObj, strBufPending, strBufApprove, strWhereNotExists, orderBy, params);
		} catch (Exception ex) {
			ex.printStackTrace();
			//logger.error(((strBufApprove == null) ? "strBufApprove is Null" : strBufApprove.toString()));
			//logger.error("UNION");
			//logger.error(((strBufPending == null) ? "strBufPending is Null" : strBufPending.toString()));

			/*if (params != null)
				for (int i = 0; i < params.size(); i++)
					//logger.error("objParams[" + i + "]" + params.get(i).toString());
*/			return null;
		}
	}

	@Override
	public List<ConcessionActivityConfigHeaderVb> getQueryResults(ConcessionActivityConfigHeaderVb dObj,
			int intStatus) {
		List<ConcessionActivityConfigHeaderVb> collTemp = null;
		setServiceDefaults();
		String strQueryAppr = null;
		String strQueryPend = null;

		strQueryAppr = new String(" SELECT TAppr.COUNTRY, TAppr.LE_BOOK,TAppr.ACTIVITY_ID, ACTIVITY_DESCRIPTION, "
				+ "	TAppr.TRANS_LINE_ID, TAppr.ACTIVITY_SOURCE_TABLE,TAppr.AGG_FUNCTION,TAppr.AGG_COLUMN,"
				+ " TAppr.TIMESERIES_TYPE,TAppr.ACTIVITY_TYPE,"
				+ "RTRIM(LTRIM("+getDbFunction("DATEFUNC")+"(TAppr.RANGE_FROM,'N5'))) RANGE_FROM,"
				+ "RTRIM(LTRIM("+getDbFunction("DATEFUNC")+"(TAppr.RANGE_TO,'N5'))) RANGE_TO,"
				+ "TAppr.SORT_COLUMNS, "
				+ " (select T1.ALPHA_SUBTAB_DESCRIPTION from ALPHA_SUB_TAB T1 "
				+ "      where t1.Alpha_tab = TAppr.TIMESERIES_TYPE_AT and T1.ALPHA_SUB_TAB=TAppr.TIMESERIES_TYPE) TIMESERIES_TYPE_DESC, "
				+ " (select T1.ALPHA_SUBTAB_DESCRIPTION from ALPHA_SUB_TAB T1 "
				+ "      where t1.Alpha_tab = TAppr.ACTIVITY_TYPE_AT and T1.ALPHA_SUB_TAB=TAppr.ACTIVITY_TYPE) ACTIVITY_TYPE_DESC, "
				+ " TAppr.Activity_Status,T3.NUM_SUBTAB_DESCRIPTION Activity_Status_DESC, "
				+ " TAppr.RECORD_INDICATOR,T4.NUM_SUBTAB_DESCRIPTION RECORD_INDICATOR_DESC,"
				+ " TAppr. MAKER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "
				+ getDbFunction("NVL") + "(TAppr.MAKER,0) ) MAKER_NAME, "
				+ " TAppr.VERIFIER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "
				+ getDbFunction("NVL") + "(TAppr.VERIFIER,0) ) VERIFIER_NAME,  " + " "
				+ getDbFunction("DATEFUNC") + "(TAppr.DATE_LAST_MODIFIED, 'dd-MMM-yyyy "
				+ getDbFunction("TIME") + "') DATE_LAST_MODIFIED ," + getDbFunction("DATEFUNC")
				+ "(TAppr.DATE_CREATION, 'dd-MMM-yyyy " + getDbFunction("TIME") + "') DATE_CREATION "
				+ " FROM RA_Mst_Concession_Activity TAppr ,NUM_SUB_TAB T3,NUM_SUB_TAB T4     " + " Where  "
				+ "	 t3.NUM_tab = TAppr.Activity_Status_NT  " + " and t3.NUM_sub_tab = TAppr.Activity_Status  "
				+ " and t4.NUM_tab = TAppr.RECORD_INDICATOR_NT  "
				+ " and t4.NUM_sub_tab = TAppr.RECORD_INDICATOR and TAppr.COUNTRY = ? "
				+ " and TAppr.LE_BOOK = ? and TAppr.ACTIVITY_ID = ? ");
		strQueryPend = new String(" SELECT TPend.COUNTRY, TPend.LE_BOOK,TPend.ACTIVITY_ID, ACTIVITY_DESCRIPTION, "
				+ "	TPend.TRANS_LINE_ID, TPend.ACTIVITY_SOURCE_TABLE,TPend.AGG_FUNCTION,TPend.AGG_COLUMN,"
				+ " TPend.TIMESERIES_TYPE,TPend.ACTIVITY_TYPE,"
				+ "RTRIM(LTRIM("+getDbFunction("DATEFUNC")+"(TPend.RANGE_FROM,'N5'))) RANGE_FROM,"
				+ "RTRIM(LTRIM("+getDbFunction("DATEFUNC")+"(TPend.RANGE_TO,'N5'))) RANGE_TO,"
				+ "TPend.SORT_COLUMNS, "
				+ " (select T1.ALPHA_SUBTAB_DESCRIPTION from ALPHA_SUB_TAB T1 "
				+ "      where t1.Alpha_tab = TPend.TIMESERIES_TYPE_AT and T1.ALPHA_SUB_TAB=TPend.TIMESERIES_TYPE) TIMESERIES_TYPE_DESC, "
				+ " (select T1.ALPHA_SUBTAB_DESCRIPTION from ALPHA_SUB_TAB T1 "
				+ "      where t1.Alpha_tab = TPend.ACTIVITY_TYPE_AT and T1.ALPHA_SUB_TAB=TPend.ACTIVITY_TYPE) ACTIVITY_TYPE_DESC, "
				+ " TPend.Activity_Status,T3.NUM_SUBTAB_DESCRIPTION Activity_Status_DESC, "
				+ " TPend.RECORD_INDICATOR,T4.NUM_SUBTAB_DESCRIPTION RECORD_INDICATOR_DESC,"
				+ " TPend. MAKER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "
				+ getDbFunction("NVL") + "(TPend.MAKER,0) ) MAKER_NAME, "
				+ " TPend.VERIFIER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "
				+ getDbFunction("NVL") + "(TPend.VERIFIER,0) ) VERIFIER_NAME,  " + " "
				+ getDbFunction("DATEFUNC") + "(TPend.DATE_LAST_MODIFIED, 'dd-MMM-yyyy "
				+ getDbFunction("TIME") + "') DATE_LAST_MODIFIED ," + getDbFunction("DATEFUNC")
				+ "(TPend.DATE_CREATION, 'dd-MMM-yyyy " + getDbFunction("TIME") + "') DATE_CREATION "
				+ " FROM RA_Mst_Concession_Activity_PEND TPend ,NUM_SUB_TAB T3,NUM_SUB_TAB T4     " + " Where  "
				+ "	 t3.NUM_tab = TPend.Activity_Status_NT  " + " and t3.NUM_sub_tab = TPend.Activity_Status  "
				+ " and t4.NUM_tab = TPend.RECORD_INDICATOR_NT  "
				+ " and t4.NUM_sub_tab = TPend.RECORD_INDICATOR And TPend.COUNTRY = ? "
				+ " and TPend.LE_BOOK = ? and TPend.ACTIVITY_ID = ? ");

		Object objParams[] = new Object[3];
		objParams[0] = new String(dObj.getCountry());
		objParams[1] = new String(dObj.getLeBook());
		objParams[2] = new String(dObj.getActivityId());
		try {
			if (intStatus == 0) {
				//logger.info("Executing approved query");
				collTemp = getJdbcTemplate().query(strQueryAppr.toString(), objParams, getMapper());
			} else {
				//logger.info("Executing pending query");
				collTemp = getJdbcTemplate().query(strQueryPend.toString(), objParams, getMapper());
			}
			return collTemp;
		} catch (Exception ex) {
			ex.printStackTrace();
			//logger.error("Error: getQueryResults Exception :   ");
			/*if (intStatus == 0)
				//logger.error(((strQueryAppr == null) ? "strQueryAppr is Null" : strQueryAppr.toString()));
			else
				//logger.error(((strQueryPend == null) ? "strQueryPend is Null" : strQueryPend.toString()));

			if (objParams != null)
				for (int i = 0; i < objParams.length; i++)
					//logger.error("objParams[" + i + "]" + objParams[i].toString());
*/			return null;
		}
	}

	@Override
	public List<ConcessionActivityConfigHeaderVb> getQueryResultsForReview(ConcessionActivityConfigHeaderVb dObj,
			int intStatus) {
		List<ConcessionActivityConfigHeaderVb> collTemp = null;
		setServiceDefaults();
		String strQueryAppr = null;
		String strQueryPend = null;

		strQueryAppr = new String(" SELECT TAppr.COUNTRY, TAppr.LE_BOOK,TAppr.ACTIVITY_ID, ACTIVITY_DESCRIPTION, "
				+ "	TAppr.TRANS_LINE_ID, TAppr.ACTIVITY_SOURCE_TABLE,TAppr.AGG_FUNCTION,TAppr.AGG_COLUMN,"
				+ " TAppr.TIMESERIES_TYPE,TAppr.ACTIVITY_TYPE,TAppr.RANGE_FROM,TAppr.RANGE_TO,TAppr.SORT_COLUMNS, "
				+ " (select T1.ALPHA_SUBTAB_DESCRIPTION from ALPHA_SUB_TAB T1 "
				+ "      where t1.Alpha_tab = TAPPR.TIMESERIES_TYPE_AT and T1.ALPHA_SUB_TAB=TAPPR.TIMESERIES_TYPE) TIMESERIES_TYPE_DESC, "
				+ " (select T1.ALPHA_SUBTAB_DESCRIPTION from ALPHA_SUB_TAB T1 "
				+ "      where t1.Alpha_tab = TAppr.ACTIVITY_TYPE_AT and T1.ALPHA_SUB_TAB=TAppr.ACTIVITY_TYPE) ACTIVITY_TYPE_DESC, "
				+ " TAppr.Activity_Status,T3.NUM_SUBTAB_DESCRIPTION Activity_Status_DESC, "
				+ " TAppr.RECORD_INDICATOR,T4.NUM_SUBTAB_DESCRIPTION RECORD_INDICATOR_DESC,"
				+ " TAppr. MAKER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "
				+ getDbFunction("NVL") + "(TAppr.MAKER,0) ) MAKER_NAME, "
				+ " TAppr.VERIFIER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "
				+ getDbFunction("NVL") + "(TAppr.VERIFIER,0) ) VERIFIER_NAME,  " + " "
				+ getDbFunction("DATEFUNC") + "(TAppr.DATE_LAST_MODIFIED, 'dd-MM-yyyy "
				+ getDbFunction("TIME") + "') DATE_LAST_MODIFIED ," + getDbFunction("DATEFUNC")
				+ "(TAppr.DATE_CREATION, 'dd-MM-yyyy " + getDbFunction("TIME") + "') DATE_CREATION"
				+ " FROM RA_Mst_Concession_Activity TAppr ,NUM_SUB_TAB T3,NUM_SUB_TAB T4     " + " Where  "
				+ "	 t3.NUM_tab = TAppr.Activity_Status_NT  " + " and t3.NUM_sub_tab = TAppr.Activity_Status  "
				+ " and t4.NUM_tab = TAppr.RECORD_INDICATOR_NT  "
				+ " and t4.NUM_sub_tab = TAppr.RECORD_INDICATOR and TAppr.COUNTRY = ? "
				+ " and TAppr.LE_BOOK = ? and TAppr.ACTIVITY_ID = ? ");
		strQueryPend = new String(" SELECT TPend.COUNTRY, TPend.LE_BOOK,TPend.ACTIVITY_ID, ACTIVITY_DESCRIPTION, "
				+ "	TPend.TRANS_LINE_ID, TPend.ACTIVITY_SOURCE_TABLE,TPend.AGG_FUNCTION,TPend.AGG_COLUMN,"
				+ " TPend.TIMESERIES_TYPE,TPend.ACTIVITY_TYPE,TPend.RANGE_FROM,TPend.RANGE_TO,TPend.SORT_COLUMNS, "
				+ " (select T1.ALPHA_SUBTAB_DESCRIPTION from ALPHA_SUB_TAB T1 "
				+ "      where t1.Alpha_tab = TPend.TIMESERIES_TYPE_AT and T1.ALPHA_SUB_TAB=TPend.TIMESERIES_TYPE) TIMESERIES_TYPE_DESC, "
				+ " (select T1.ALPHA_SUBTAB_DESCRIPTION from ALPHA_SUB_TAB T1 "
				+ "      where t1.Alpha_tab = TPend.ACTIVITY_TYPE_AT and T1.ALPHA_SUB_TAB=TPend.ACTIVITY_TYPE) ACTIVITY_TYPE_DESC, "
				+ " TPend.Activity_Status,T3.NUM_SUBTAB_DESCRIPTION Activity_Status_DESC, "
				+ " TPend.RECORD_INDICATOR,T4.NUM_SUBTAB_DESCRIPTION RECORD_INDICATOR_DESC,"
				+ " TPend. MAKER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "
				+ getDbFunction("NVL") + "(TPend.MAKER,0) ) MAKER_NAME, "
				+ " TPend.VERIFIER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "
				+ getDbFunction("NVL") + "(TPend.VERIFIER,0) ) VERIFIER_NAME,  " + " "
				+ getDbFunction("DATEFUNC") + "(TPend.DATE_LAST_MODIFIED, 'dd-MM-yyyy "
				+ getDbFunction("TIME") + "') DATE_LAST_MODIFIED ," + getDbFunction("DATEFUNC")
				+ "(TPend.DATE_CREATION, 'dd-MM-yyyy " + getDbFunction("TIME") + "') DATE_CREATION "
				+ " FROM RA_Mst_Concession_Activity_PEND TPend ,NUM_SUB_TAB T3,NUM_SUB_TAB T4     " + " Where  "
				+ "	 t3.NUM_tab = TPend.Activity_Status_NT  " + " and t3.NUM_sub_tab = TPend.Activity_Status  "
				+ " and t4.NUM_tab = TPend.RECORD_INDICATOR_NT  "
				+ " and t4.NUM_sub_tab = TPend.RECORD_INDICATOR And TPend.COUNTRY = ? "
				+ " and TPend.LE_BOOK = ? and TPend.ACTIVITY_ID = ? ");

		Object objParams[] = new Object[3];
		objParams[0] = new String(dObj.getCountry());// country
		objParams[1] = new String(dObj.getLeBook());
		objParams[2] = new String(dObj.getActivityId());
		try {
			if (intStatus == 0) {
				//logger.info("Executing approved query");
				collTemp = getJdbcTemplate().query(strQueryAppr.toString(), objParams, getMapper());
			} else {
				//logger.info("Executing pending query");
				collTemp = getJdbcTemplate().query(strQueryPend.toString(), objParams, getMapper());
			}
			return collTemp;
		} catch (Exception ex) {
			ex.printStackTrace();
			//logger.error("Error: getQueryResults Exception :   ");
			/*if (intStatus == 0)
				//logger.error(((strQueryAppr == null) ? "strQueryAppr is Null" : strQueryAppr.toString()));
			else
				//logger.error(((strQueryPend == null) ? "strQueryPend is Null" : strQueryPend.toString()));

			if (objParams != null)
				for (int i = 0; i < objParams.length; i++)
					//logger.error("objParams[" + i + "]" + objParams[i].toString());
*/			return null;
		}
	}

	@Override
	protected void setServiceDefaults() {
		serviceName = "ConcessionActivityHeader";
		serviceDesc = "Concession Activity config";
		tableName = "RA_Mst_Concession_Activity";
		childTableName = "RA_Mst_Concession_Activity";
		intCurrentUserId = CustomContextHolder.getContext().getVisionId();
	}

	protected int doInsertionApprConcessionHeaders(ConcessionActivityConfigHeaderVb vObject) {
		String query = " Insert Into RA_Mst_Concession_Activity(COUNTRY,LE_BOOK,ACTIVITY_ID,"
				+ " ACTIVITY_DESCRIPTION,TRANS_LINE_ID, ACTIVITY_SOURCE_tABLE, AGG_FUNCTION, AGG_COLUMN, "
				+ " TIMESERIES_TYPE,ACTIVITY_TYPE,RANGE_FROM, RANGE_TO, SORT_COLUMNS,"
				+ " Activity_Status_NT, Activity_Status,"
				+ "RECORD_INDICATOR_NT,RECORD_INDICATOR,MAKER,VERIFIER,DATE_LAST_MODIFIED,DATE_CREATION) "
				+ " Values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?," + getDbFunction("SYSDATE") + ","
				+ getDbFunction("SYSDATE") + ")";

		Object[] args = { vObject.getCountry(), vObject.getLeBook(), vObject.getActivityId(), vObject.getActivityDesc(),
				vObject.getTransLineId(), vObject.getActSourceTable(), vObject.getAggFun(), vObject.getAggCol(),
				vObject.getTimeSeriesType(), vObject.getActivityType(), 
				ValidationUtil.isValid(vObject.getRangeFrom()) ? vObject.getRangeFrom().replaceAll(",", "") : "",
				ValidationUtil.isValid(vObject.getRangeTo()) ? vObject.getRangeTo().replaceAll(",", "") : "",
				vObject.getSortColumns(), vObject.getActivityStatusNt(), vObject.getActivityStatus(),
				vObject.getRecordIndicatorNt(), vObject.getRecordIndicator(), vObject.getMaker(),
				vObject.getVerifier() };
		return getJdbcTemplate().update(query, args);
	}

	protected int doInsertionPendConcessionHeaders(ConcessionActivityConfigHeaderVb vObject) {
		String query = " Insert Into RA_Mst_Concession_Activity_PEND(COUNTRY,LE_BOOK,ACTIVITY_ID,"
				+ " ACTIVITY_DESCRIPTION,TRANS_LINE_ID, ACTIVITY_SOURCE_tABLE, AGG_FUNCTION, AGG_COLUMN, "
				+ " TIMESERIES_TYPE,ACTIVITY_TYPE,RANGE_FROM, RANGE_TO, SORT_COLUMNS,"
				+ " Activity_Status_NT, Activity_Status,"
				+ "RECORD_INDICATOR_NT,RECORD_INDICATOR,MAKER,VERIFIER,DATE_LAST_MODIFIED,DATE_CREATION) "
				+ " Values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?," + getDbFunction("SYSDATE") + ","
				+ getDbFunction("SYSDATE") + ")";

		Object[] args = { vObject.getCountry(), vObject.getLeBook(), vObject.getActivityId(), vObject.getActivityDesc(),
				vObject.getTransLineId(), vObject.getActSourceTable(), vObject.getAggFun(), vObject.getAggCol(),
				vObject.getTimeSeriesType(), vObject.getActivityType(),
				ValidationUtil.isValid(vObject.getRangeFrom()) ? vObject.getRangeFrom().replaceAll(",", "") : "",
				ValidationUtil.isValid(vObject.getRangeTo()) ? vObject.getRangeTo().replaceAll(",", "") : "",
				vObject.getSortColumns(), vObject.getActivityStatusNt(), vObject.getActivityStatus(),
				vObject.getRecordIndicatorNt(), vObject.getRecordIndicator(), vObject.getMaker(),
				vObject.getVerifier() };
		return getJdbcTemplate().update(query, args);
	}

	protected int doInsertionPendConcessionHeadersDc(ConcessionActivityConfigHeaderVb vObject) {
		String query = "";
		if ("ORACLE".equalsIgnoreCase(databaseType)) {
			query = " Insert Into RA_Mst_Concession_Activity_PEND(COUNTRY,LE_BOOK,ACTIVITY_ID,"
					+ " ACTIVITY_DESCRIPTION,TRANS_LINE_ID, ACTIVITY_SOURCE_tABLE, AGG_FUNCTION, AGG_COLUMN, "
					+ " TIMESERIES_TYPE,ACTIVITY_TYPE,RANGE_FROM, RANGE_TO, SORT_COLUMNS,"
					+ " Activity_Status_NT, Activity_Status,"
					+ "RECORD_INDICATOR_NT,RECORD_INDICATOR,MAKER,VERIFIER,DATE_LAST_MODIFIED,DATE_CREATION) "
					+ " Values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?," + getDbFunction("SYSDATE") + ","
					+ getDbFunction("SYSDATE") + ")";

		} else if ("MSSQL".equalsIgnoreCase(databaseType)) {

			query = " Insert Into RA_Mst_Concession_Activity_PEND(COUNTRY,LE_BOOK,ACTIVITY_ID,"
					+ " ACTIVITY_DESCRIPTION,TRANS_LINE_ID, ACTIVITY_SOURCE_tABLE, AGG_FUNCTION, AGG_COLUMN, "
					+ " TIMESERIES_TYPE,ACTIVITY_TYPE,RANGE_FROM, RANGE_TO, SORT_COLUMNS,"
					+ " Activity_Status_NT, Activity_Status,"
					+ "RECORD_INDICATOR_NT,RECORD_INDICATOR,MAKER,VERIFIER,DATE_LAST_MODIFIED,DATE_CREATION) "
					+ " Values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?," + getDbFunction("SYSDATE") + ",?)";
					

		}

		Object[] args = { vObject.getCountry(), vObject.getLeBook(), vObject.getActivityId(), vObject.getActivityDesc(),
				vObject.getTransLineId(), vObject.getActSourceTable(), vObject.getAggFun(), vObject.getAggCol(),
				vObject.getTimeSeriesType(), vObject.getActivityType(), 
				ValidationUtil.isValid(vObject.getRangeFrom()) ? vObject.getRangeFrom().replaceAll(",", "") : "",
				ValidationUtil.isValid(vObject.getRangeTo()) ? vObject.getRangeTo().replaceAll(",", "") : "",
				vObject.getSortColumns(), vObject.getActivityStatusNt(), vObject.getActivityStatus(),
				vObject.getRecordIndicatorNt(), vObject.getRecordIndicator(), vObject.getMaker(),
				vObject.getVerifier(),vObject.getDateCreation() };
		return getJdbcTemplate().update(query, args);
	}

	protected int doUpdateApprHeader(ConcessionActivityConfigHeaderVb vObject) {
		String query = " UPDATE RA_MST_CONCESSION_ACTIVITY SET "
				+ " ACTIVITY_DESCRIPTION=?,TRANS_LINE_ID=?, ACTIVITY_SOURCE_TABLE=?, AGG_FUNCTION=?, AGG_COLUMN=?, "
				+ " TIMESERIES_TYPE=? ,ACTIVITY_TYPE= ?,RANGE_FROM= ?,RANGE_TO=?,  SORT_COLUMNS =?,"
				+ " ACTIVITY_STATUS= ? ,RECORD_INDICATOR= ? ,MAKER= ? ," + " VERIFIER= ? ,DATE_LAST_MODIFIED= "
				+ getDbFunction("SYSDATE") + ",DATE_CREATION = ?"   + " WHERE COUNTRY= ? AND LE_BOOK= ? AND ACTIVITY_ID = ?";

		Object[] args = { vObject.getActivityDesc(), vObject.getTransLineId(), vObject.getActSourceTable(),
				vObject.getAggFun(), vObject.getAggCol(), vObject.getTimeSeriesType(), vObject.getActivityType(),
				ValidationUtil.isValid(vObject.getRangeFrom()) ? vObject.getRangeFrom().replaceAll(",", "") : "",
				ValidationUtil.isValid(vObject.getRangeTo()) ? vObject.getRangeTo().replaceAll(",", "") : "",
				vObject.getSortColumns(), vObject.getActivityStatus(),
				vObject.getRecordIndicator(), vObject.getMaker(), vObject.getVerifier(),vObject.getDateCreation(), vObject.getCountry(),
				vObject.getLeBook(), vObject.getActivityId() };
		return getJdbcTemplate().update(query, args);
	}

	protected int doUpdatePendHeader(ConcessionActivityConfigHeaderVb vObject) {
		String query = " UPDATE RA_MST_CONCESSION_ACTIVITY_PEND SET "
				+ " ACTIVITY_DESCRIPTION=?,TRANS_LINE_ID=?, ACTIVITY_SOURCE_TABLE=?, AGG_FUNCTION=?, AGG_COLUMN=?, "
				+ " TIMESERIES_TYPE=? ,ACTIVITY_TYPE= ?,RANGE_FROM= ?,RANGE_TO=?,  SORT_COLUMNS =?,"
				+ " ACTIVITY_STATUS= ? ,RECORD_INDICATOR= ? ,MAKER= ? ," + " VERIFIER= ? ,DATE_LAST_MODIFIED= "
				+ getDbFunction("SYSDATE") + ",DATE_CREATION = ? " + " WHERE COUNTRY= ? AND LE_BOOK= ? AND ACTIVITY_ID = ?";

		Object[] args = { vObject.getActivityDesc(), vObject.getTransLineId(), vObject.getActSourceTable(),
				vObject.getAggFun(), vObject.getAggCol(), vObject.getTimeSeriesType(), vObject.getActivityType(),
				ValidationUtil.isValid(vObject.getRangeFrom()) ? vObject.getRangeFrom().replaceAll(",", "") : "",
				ValidationUtil.isValid(vObject.getRangeTo()) ? vObject.getRangeTo().replaceAll(",", "") : "",
				vObject.getSortColumns(), vObject.getActivityStatus(),
				vObject.getRecordIndicator(), vObject.getMaker(), vObject.getVerifier(),vObject.getDateCreation(), vObject.getCountry(),
				vObject.getLeBook(), vObject.getActivityId() };
		return getJdbcTemplate().update(query, args);
	}

	protected int deleteConcessionHeaderAppr(ConcessionActivityConfigHeaderVb vObject) {
		String query = "Delete from RA_Mst_Concession_Activity  WHERE COUNTRY= ? AND LE_BOOK= ?  AND ACTIVITY_ID = ?";
		Object[] args = { vObject.getCountry(), vObject.getLeBook(), vObject.getActivityId() };
		return getJdbcTemplate().update(query, args);

	}

	protected int deleteConcessionHeaderPend(ConcessionActivityConfigHeaderVb vObject) {
		String query = "Delete from RA_Mst_Concession_Activity_PEND  WHERE COUNTRY= ? AND LE_BOOK= ?  AND ACTIVITY_ID =  ?";
		Object[] args = { vObject.getCountry(), vObject.getLeBook(), vObject.getActivityId() };
		return getJdbcTemplate().update(query, args);

	}

	@Override
	protected List<ConcessionActivityConfigHeaderVb> selectApprovedRecord(ConcessionActivityConfigHeaderVb vObject) {
		return getQueryResults(vObject, Constants.STATUS_ZERO);
	}

	@Override
	public List<ConcessionActivityConfigHeaderVb> doSelectPendingRecord(ConcessionActivityConfigHeaderVb vObject) {
		return getQueryResults(vObject, Constants.STATUS_PENDING);
	}

	@Override
	protected int getStatus(ConcessionActivityConfigHeaderVb records) {
		return records.getActivityStatus();
	}

	@Override
	protected void setStatus(ConcessionActivityConfigHeaderVb vObject, int status) {
		vObject.setActivityStatus(status);
	}

	@Override
	public ExceptionCode doInsertApprRecordForNonTrans(ConcessionActivityConfigHeaderVb vObject)
			throws RuntimeCustomException {
		List<ConcessionActivityConfigHeaderVb> collTemp = null;
		ExceptionCode exceptionCode = null;
		strCurrentOperation = Constants.ADD;
		strApproveOperation = Constants.ADD;
		setServiceDefaults();
		collTemp = selectApprovedRecord(vObject);
		if (collTemp != null && collTemp.size() > 0) {
			exceptionCode = getResultObject(Constants.DUPLICATE_KEY_INSERTION);
			throw buildRuntimeCustomException(exceptionCode);
		}
		vObject.setRecordIndicator(Constants.STATUS_ZERO);
		vObject.setActivityStatus(Constants.STATUS_ZERO);
		vObject.setMaker(intCurrentUserId);
		vObject.setVerifier(intCurrentUserId);
		String systemDate = getSystemDate();
//		vObject.setDateCreation(systemDate);
		vObject.setDateLastModified(systemDate);
		retVal = doInsertionApprConcessionHeaders(vObject);
		if (retVal != Constants.SUCCESSFUL_OPERATION) {
			exceptionCode = getResultObject(retVal);
			throw buildRuntimeCustomException(exceptionCode);
		} else {
			exceptionCode = getResultObject(Constants.SUCCESSFUL_OPERATION);
		}
		if (vObject.getConcessionJoinLst() != null && vObject.getConcessionJoinLst().size() > 0) {
			exceptionCode = concessionFilterDao.deleteAndInsertApprFilter(vObject);
		}
		exceptionCode = getResultObject(Constants.SUCCESSFUL_OPERATION);
		writeAuditLog(vObject, null);
		if (exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION) {
			exceptionCode = getResultObject(Constants.AUDIT_TRAIL_ERROR);
			throw buildRuntimeCustomException(exceptionCode);
		}
		return exceptionCode;
	}

	@Override
	public ExceptionCode doInsertRecordForNonTrans(ConcessionActivityConfigHeaderVb vObject)
			throws RuntimeCustomException {
		List<ConcessionActivityConfigHeaderVb> collTemp = null;
		ExceptionCode exceptionCode = null;
		strCurrentOperation = Constants.ADD;
		strApproveOperation = Constants.ADD;
		setServiceDefaults();
		collTemp = doSelectPendingRecord(vObject);
		if (collTemp != null && collTemp.size() > 0) {
			logger.error("!!");
			exceptionCode = getResultObject(Constants.DUPLICATE_KEY_INSERTION);
			throw buildRuntimeCustomException(exceptionCode);
		}
		vObject.setRecordIndicator(Constants.STATUS_INSERT);
		vObject.setActivityStatus(Constants.STATUS_ZERO);
		vObject.setMaker(intCurrentUserId);
		vObject.setVerifier(intCurrentUserId);
		String systemDate = getSystemDate();
		vObject.setDateCreation(systemDate);
		vObject.setDateLastModified(systemDate);
		retVal = doInsertionPendConcessionHeaders(vObject);
		if (retVal != Constants.SUCCESSFUL_OPERATION) {
			exceptionCode = getResultObject(retVal);
			throw buildRuntimeCustomException(exceptionCode);
		} else {
			exceptionCode = getResultObject(Constants.SUCCESSFUL_OPERATION);
		}

		if (vObject.getConcessionJoinLst() != null && vObject.getConcessionJoinLst().size() > 0) {
			exceptionCode = concessionFilterDao.deleteAndInsertPendFilter(vObject);
		}
		writeAuditLog(vObject, null);
		exceptionCode = getResultObject(Constants.SUCCESSFUL_OPERATION);
		return exceptionCode;
	}

	@Override
	public ExceptionCode doUpdateApprRecordForNonTrans(ConcessionActivityConfigHeaderVb vObject)
			throws RuntimeCustomException {
		List<ConcessionActivityConfigHeaderVb> collTemp = null;
		ConcessionActivityConfigHeaderVb vObjectlocal = null;
		ExceptionCode exceptionCode = null;
		strApproveOperation = Constants.MODIFY;
		strErrorDesc = "";
		strCurrentOperation = Constants.MODIFY;
		setServiceDefaults();
		vObject.setMaker(getIntCurrentUserId());
		collTemp = selectApprovedRecord(vObject);
		if (collTemp == null) {
			exceptionCode = getResultObject(Constants.ERRONEOUS_OPERATION);
			throw buildRuntimeCustomException(exceptionCode);
		}
		vObjectlocal = ((ArrayList<ConcessionActivityConfigHeaderVb>) collTemp).get(0);
		// Even if record is not there in Appr. table reject the record
		if (collTemp.size() == 0) {
			exceptionCode = getResultObject(Constants.ATTEMPT_TO_MODIFY_UNEXISTING_RECORD);
			throw buildRuntimeCustomException(exceptionCode);
		}
		vObject.setRecordIndicator(Constants.STATUS_ZERO);
		vObject.setVerifier(getIntCurrentUserId());
		
		vObject.setDateLastModified(getSystemDate());
		retVal = doUpdateApprHeader(vObject);
		if (retVal != Constants.SUCCESSFUL_OPERATION) {
			exceptionCode = getResultObject(retVal);
			throw buildRuntimeCustomException(exceptionCode);
		} else {
			exceptionCode = getResultObject(Constants.SUCCESSFUL_OPERATION);
		}
		if (vObject.getConcessionJoinLst() != null && vObject.getConcessionJoinLst().size() > 0) {
			exceptionCode = concessionFilterDao.deleteAndInsertApprFilter(vObject);
		}
		writeAuditLog(vObject, vObjectlocal);
		if (exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION) {
			exceptionCode = getResultObject(Constants.AUDIT_TRAIL_ERROR);
			throw buildRuntimeCustomException(exceptionCode);
		}
		exceptionCode = getResultObject(Constants.SUCCESSFUL_OPERATION);
		return exceptionCode;
	}

	@Override
	public ExceptionCode doUpdateRecordForNonTrans(ConcessionActivityConfigHeaderVb vObject)
			throws RuntimeCustomException {
		List<ConcessionActivityConfigHeaderVb> collTemp = null;
		ConcessionActivityConfigHeaderVb vObjectlocal = null;
		ExceptionCode exceptionCode = null;
		strApproveOperation = Constants.MODIFY;
		strErrorDesc = "";
		strCurrentOperation = Constants.MODIFY;
		setServiceDefaults();
		vObject.setMaker(getIntCurrentUserId());
		collTemp = doSelectPendingRecord(vObject);
		if (collTemp == null) {
			exceptionCode = getResultObject(Constants.ERRONEOUS_OPERATION);
			throw buildRuntimeCustomException(exceptionCode);
		}
		if (collTemp.size() > 0) {
			vObjectlocal = ((ArrayList<ConcessionActivityConfigHeaderVb>) collTemp).get(0);
			vObject.setDateCreation(vObjectlocal.getDateCreation());
			if (vObjectlocal.getRecordIndicator() == Constants.STATUS_INSERT) {
				vObject.setVerifier(0);
				vObject.setRecordIndicator(Constants.STATUS_INSERT);
				retVal = doUpdatePendHeader(vObject);
			} else {
				vObject.setVerifier(0);
				vObject.setRecordIndicator(Constants.STATUS_UPDATE);
				retVal = doUpdatePendHeader(vObject);
			}
			if (retVal != Constants.SUCCESSFUL_OPERATION) {
				exceptionCode = getResultObject(retVal);
				throw buildRuntimeCustomException(exceptionCode);
			}
			if (vObject.getConcessionJoinLst() != null && vObject.getConcessionJoinLst().size() > 0) {
				concessionFilterDao.deleteAndInsertPendFilter(vObject);
			}
			writeAuditLog(vObject, null);
			return getResultObject(Constants.SUCCESSFUL_OPERATION);
		} else {
			collTemp = null;
			collTemp = selectApprovedRecord(vObject);

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
				vObjectlocal = ((ArrayList<ConcessionActivityConfigHeaderVb>) collTemp).get(0);
				vObject.setDateCreation(vObjectlocal.getDateCreation());
			}
			vObject.setDateCreation(vObjectlocal.getDateCreation());
			// Record is there in approved, but not in pending. So add it to pending
			vObject.setVerifier(0);
			vObject.setRecordIndicator(Constants.STATUS_UPDATE);
			vObject.setDateLastModified(getSystemDate());
			retVal = doInsertionPendConcessionHeadersDc(vObject);
			if (retVal != Constants.SUCCESSFUL_OPERATION) {
				exceptionCode = getResultObject(retVal);
				throw buildRuntimeCustomException(exceptionCode);
			}
			if (vObject.getConcessionJoinLst() != null && vObject.getConcessionJoinLst().size() > 0) {
				concessionFilterDao.deleteAndInsertPendFilter(vObject);
			}
			writeAuditLog(vObject, null);
			return getResultObject(Constants.SUCCESSFUL_OPERATION);
		}
	}

	@Override
	@Transactional(rollbackForClassName = { "com.vision.exception.RuntimeCustomException" })
	public ExceptionCode doRejectForTransaction(ConcessionActivityConfigHeaderVb vObject)
			throws RuntimeCustomException {
		strErrorDesc = "";
		strCurrentOperation = Constants.APPROVE;
		setServiceDefaults();
		return doRejectRecord(vObject);
	}

	@Override
	public ExceptionCode doRejectRecord(ConcessionActivityConfigHeaderVb vObject) throws RuntimeCustomException {
		ConcessionActivityConfigHeaderVb vObjectlocal = null;
		List<ConcessionActivityConfigHeaderVb> collTemp = null;
		ExceptionCode exceptionCode = null;
		setServiceDefaults();
		strErrorDesc = "";
		strCurrentOperation = Constants.REJECT;
		vObject.setMaker(getIntCurrentUserId());
		try {
			if (vObject.getRecordIndicator() == 1 || vObject.getRecordIndicator() == 3)
				vObject.setRecordIndicator(0);
			else
				vObject.setRecordIndicator(-1);
			// See if such a pending request exists in the pending table
			collTemp = doSelectPendingRecord(vObject);
			if (collTemp == null) {
				exceptionCode = getResultObject(Constants.ERRONEOUS_OPERATION);
				throw buildRuntimeCustomException(exceptionCode);
			}
			if (collTemp.size() == 0) {
				exceptionCode = getResultObject(Constants.NO_SUCH_PENDING_RECORD);
				throw buildRuntimeCustomException(exceptionCode);
			}
			vObjectlocal = ((ArrayList<ConcessionActivityConfigHeaderVb>) collTemp).get(0);
			retVal = deleteConcessionHeaderPend(vObject);

			List<ConcessionActivityFilterVb> collTempFilter = null;
			collTempFilter = concessionFilterDao.getConcessionFilters(vObject, 1);
			if (collTempFilter != null && collTempFilter.size() > 0) {
				retVal = concessionFilterDao.deleteConcessionFilterPend(vObject);
			}
			exceptionCode = getResultObject(Constants.SUCCESSFUL_OPERATION);
			writeAuditLog(vObject, null);
			return exceptionCode;
		} catch (UncategorizedSQLException uSQLEcxception) {
			strErrorDesc = parseErrorMsg(uSQLEcxception);
			exceptionCode = getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION);
			throw buildRuntimeCustomException(exceptionCode);
		} catch (Exception ex) {
			logger.error("Error in Reject.", ex);
			//logger.error(((vObject == null) ? "vObject is Null" : vObject.toString()));
			strErrorDesc = ex.getMessage();
			exceptionCode = getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION);
			throw buildRuntimeCustomException(exceptionCode);
		}
	}

	@Override
	@Transactional(rollbackForClassName = { "com.vision.exception.RuntimeCustomException" })
	public ExceptionCode doApproveForTransaction(ConcessionActivityConfigHeaderVb vObject, boolean staticDelete)
			throws RuntimeCustomException {
		strErrorDesc = "";
		strCurrentOperation = Constants.APPROVE;
		setServiceDefaults();
		return doApproveRecord(vObject, staticDelete);
	}

	@Override
	public ExceptionCode doApproveRecord(ConcessionActivityConfigHeaderVb vObject, boolean staticDelete)
			throws RuntimeCustomException {
		ConcessionActivityConfigHeaderVb oldContents = null;
		ConcessionActivityFilterVb oldContentsFilter = null;

		ConcessionActivityConfigHeaderVb vObjectlocal = null;
		ConcessionActivityFilterVb vObjectFilterlocal = null;

		List<ConcessionActivityConfigHeaderVb> collTemp = null;

		List<ConcessionActivityFilterVb> collTempFilter = null;
		List<ConcessionActivityFilterVb> collTempFilterAppr = null;

		ExceptionCode exceptionCode = null;
		strCurrentOperation = Constants.APPROVE;
		setServiceDefaults();
		try {
			// See if such a pending request exists in the pending table
			vObject.setVerifier(getIntCurrentUserId());
			vObject.setRecordIndicator(Constants.STATUS_ZERO);
			collTemp = doSelectPendingRecord(vObject);
			if (collTemp == null) {
				exceptionCode = getResultObject(Constants.ERRONEOUS_OPERATION);
				throw buildRuntimeCustomException(exceptionCode);
			}

			if (collTemp.size() == 0) {
				exceptionCode = getResultObject(Constants.NO_SUCH_PENDING_RECORD);
				throw buildRuntimeCustomException(exceptionCode);
			}

			vObjectlocal = ((ArrayList<ConcessionActivityConfigHeaderVb>) collTemp).get(0);

			if (vObjectlocal.getMaker() == getIntCurrentUserId()) {
				exceptionCode = getResultObject(Constants.MAKER_CANNOT_APPROVE);
				throw buildRuntimeCustomException(exceptionCode);
			}

			collTempFilter = concessionFilterDao.getConcessionFilters(vObjectlocal, 1);
			if (collTempFilter != null && collTempFilter.size() > 0) {
				vObjectFilterlocal = ((ArrayList<ConcessionActivityFilterVb>) collTempFilter).get(0);
			}
			// If it's NOT addition, collect the existing record contents from the
			// Approved table and keep it aside, for writing audit information later.
			if (vObjectlocal.getRecordIndicator() != Constants.STATUS_INSERT) {
				collTemp = selectApprovedRecord(vObject);
				if (collTemp == null || collTemp.isEmpty()) {
					exceptionCode = getResultObject(Constants.ERRONEOUS_OPERATION);
					throw buildRuntimeCustomException(exceptionCode);
				}
				oldContents = ((ArrayList<ConcessionActivityConfigHeaderVb>) collTemp).get(0);

				collTempFilterAppr = concessionFilterDao.getConcessionFilters(vObjectlocal, 0);
				if (collTempFilterAppr != null && collTempFilterAppr.size() > 0) {
					oldContentsFilter = ((ArrayList<ConcessionActivityFilterVb>) collTempFilterAppr).get(0);
				}
			}

			if (vObjectlocal.getRecordIndicator() == Constants.STATUS_INSERT) { // Add authorization
				// Write the contents of the Pending table record to the Approved table
				vObjectlocal.setRecordIndicator(Constants.STATUS_ZERO);
				vObject.setRecordIndicator(Constants.STATUS_ZERO);
				vObjectlocal.setVerifier(getIntCurrentUserId());
				retVal = doInsertionApprConcessionHeaders(vObjectlocal);
				if (retVal != Constants.SUCCESSFUL_OPERATION) {
					exceptionCode = getResultObject(retVal);
					throw buildRuntimeCustomException(exceptionCode);
				}

				if (collTempFilterAppr != null && collTempFilterAppr.size() > 0) {
					concessionFilterDao.deleteConcessionFilterAppr(vObjectlocal);
				}
				if (collTempFilter != null && !collTempFilter.isEmpty()) {
					collTempFilter.forEach(filterVb -> {
						String systemDate = getSystemDate();
						filterVb.setDateLastModified(systemDate);
						filterVb.setRecordIndicator(vObject.getRecordIndicator());
						retVal = concessionFilterDao.doInsertionApprConcessionFilter(filterVb);
					});
				}
				String systemDate = getSystemDate();
				vObject.setDateLastModified(systemDate);
			
				strApproveOperation = Constants.ADD;
			} else if (vObjectlocal.getRecordIndicator() == Constants.STATUS_UPDATE) { // Modify authorization
				collTemp = selectApprovedRecord(vObject);
				if (collTemp == null || collTemp.isEmpty()) {
					exceptionCode = getResultObject(Constants.ERRONEOUS_OPERATION);
					throw buildRuntimeCustomException(exceptionCode);
				}

				// If record already exists in the approved table, reject the addition
				if (collTemp.size() > 0) {
					// retVal = doUpdateAppr(vObjectlocal, MISConstants.ACTIVATE);
					vObjectlocal.setVerifier(getIntCurrentUserId());
					vObjectlocal.setRecordIndicator(Constants.STATUS_ZERO);
					vObject.setRecordIndicator(Constants.STATUS_ZERO);
					vObject.setMaker(vObjectlocal.getMaker());
					retVal = doUpdateApprHeader(vObjectlocal);
					if (retVal != Constants.SUCCESSFUL_OPERATION) {
						exceptionCode = getResultObject(retVal);
						throw buildRuntimeCustomException(exceptionCode);
					}
				}

				if (collTempFilterAppr != null && collTempFilterAppr.size() > 0) {
					concessionFilterDao.deleteConcessionFilterAppr(vObjectlocal);
				}
				if (collTempFilter != null && !collTempFilter.isEmpty()) {
					collTempFilter.forEach(flPend -> {
						String systemDate = getSystemDate();
						flPend.setDateLastModified(systemDate);
						flPend.setVerifier(getIntCurrentUserId());
						flPend.setMaker(vObject.getMaker());
						flPend.setRecordIndicator(vObject.getRecordIndicator());
						retVal = concessionFilterDao.doInsertionApprConcessionFilter(flPend);
					});
				}
				// Modify the existing contents of the record in Approved table

				String systemDate = getSystemDate();
				vObject.setDateLastModified(systemDate);
				// Set the current operation to write to audit log
				strApproveOperation = Constants.MODIFY;
			} else if (vObjectlocal.getRecordIndicator() == Constants.STATUS_DELETE) { // Delete authorization
				if (staticDelete) {
					// Update the existing record status in the Approved table to delete
					setStatus(vObjectlocal, Constants.PASSIVATE);
					vObjectlocal.setRecordIndicator(Constants.STATUS_ZERO);
					vObjectlocal.setVerifier(getIntCurrentUserId());
					retVal = doUpdateApprHeader(vObjectlocal);
					if (retVal != Constants.SUCCESSFUL_OPERATION) {
						exceptionCode = getResultObject(retVal);
						throw buildRuntimeCustomException(exceptionCode);
					}
					setStatus(vObject, Constants.PASSIVATE);
					String systemDate = getSystemDate();
					vObject.setDateLastModified(systemDate);
					
				

				} else {
					// Delete the existing record from the Approved table
					retVal = deleteConcessionHeaderAppr(vObjectlocal);
					if (retVal != Constants.SUCCESSFUL_OPERATION) {
						retVal = concessionFilterDao.deleteConcessionFilterAppr(vObjectlocal);
					}
					if (retVal != Constants.SUCCESSFUL_OPERATION) {
						exceptionCode = getResultObject(retVal);
						throw buildRuntimeCustomException(exceptionCode);
					}
					String systemDate = getSystemDate();
					vObject.setDateLastModified(systemDate);
				}
			}
		else {
				exceptionCode = getResultObject(Constants.INVALID_STATUS_FLAG_IN_DATABASE);
				throw buildRuntimeCustomException(exceptionCode);
			}

			// Delete the record from the Pending table
			retVal = deleteConcessionHeaderPend(vObjectlocal);
			if (retVal != Constants.SUCCESSFUL_OPERATION) {
				exceptionCode = getResultObject(retVal);
				throw buildRuntimeCustomException(exceptionCode);
			}
			retVal = concessionFilterDao.deleteConcessionFilterPend(vObjectlocal);

			// Set the internal status to Approved
			vObject.setInternalStatus(0);
			vObject.setRecordIndicator(Constants.STATUS_ZERO);
			if (vObjectlocal.getRecordIndicator() == Constants.STATUS_DELETE && !staticDelete) {
				writeAuditLog(null, oldContents);
				vObject.setRecordIndicator(-1);
			} else
				writeAuditLog(vObjectlocal, oldContents);

			/*
			 * if(exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION){
			 * exceptionCode = getResultObject(Constants.AUDIT_TRAIL_ERROR); throw
			 * buildRuntimeCustomException(exceptionCode); }
			 */
			return getResultObject(Constants.SUCCESSFUL_OPERATION);
		} catch (UncategorizedSQLException uSQLEcxception) {
			strErrorDesc = parseErrorMsg(uSQLEcxception);
			exceptionCode = getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION);
			throw buildRuntimeCustomException(exceptionCode);
		} catch (Exception ex) {
			logger.error("Error in Approve.", ex);
			//logger.error(((vObject == null) ? "vObject is Null" : vObject.toString()));
			strErrorDesc = ex.getMessage();
			exceptionCode = getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION);
			throw buildRuntimeCustomException(exceptionCode);
		}
	}

	@Override
	protected String getAuditString(ConcessionActivityConfigHeaderVb vObject) {
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

			if (ValidationUtil.isValid(vObject.getActivityId()))
				strAudit.append("ACTIVITY_ID" + auditDelimiterColVal + vObject.getActivityId().trim());
			else
				strAudit.append("ACTIVITY_ID" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);

			if (ValidationUtil.isValid(vObject.getTransLineId()))
				strAudit.append("TRANS_LINE_ID" + auditDelimiterColVal + vObject.getTransLineId().trim());
			else
				strAudit.append("TRANS_LINE_ID" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);

			if (ValidationUtil.isValid(vObject.getActSourceTable()))
				strAudit.append("ACTIVITY_SOURCE_TABLE" + auditDelimiterColVal + vObject.getActSourceTable().trim());
			else
				strAudit.append("ACTIVITY_SOURCE_TABLE" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);

			if (ValidationUtil.isValid(vObject.getAggFun()))
				strAudit.append("AGG_FUNCTION" + auditDelimiterColVal + vObject.getAggFun().trim());
			else
				strAudit.append("AGG_FUNCTION" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);

			if (ValidationUtil.isValid(vObject.getAggCol()))
				strAudit.append("AGG_COLUMN" + auditDelimiterColVal + vObject.getAggCol().trim());
			else
				strAudit.append("AGG_COLUMN" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);

			if (ValidationUtil.isValid(vObject.getTimeSeriesType()))
				strAudit.append("TIMESERIES_TYPE" + auditDelimiterColVal + vObject.getTimeSeriesType().trim());
			else
				strAudit.append("TIMESERIES_TYPE" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);

			if (ValidationUtil.isValid(vObject.getActionType()))
				strAudit.append("ACTIVITY_TYPE" + auditDelimiterColVal + vObject.getActionType().trim());
			else
				strAudit.append("ACTIVITY_TYPE" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);

			if (ValidationUtil.isValid(vObject.getRangeFrom()))
				strAudit.append("RANGE_FROM" + auditDelimiterColVal + vObject.getRangeFrom());
			else
				strAudit.append("RANGE_FROM" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);

			if (ValidationUtil.isValid(vObject.getRangeTo()))
				strAudit.append("RANGE_TO" + auditDelimiterColVal + vObject.getRangeTo());
			else
				strAudit.append("RANGE_TO" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);

			if (ValidationUtil.isValid(vObject.getSortColumns()))
				strAudit.append("SORT_COLUMNS" + auditDelimiterColVal + vObject.getSortColumns().trim());
			else
				strAudit.append("SORT_COLUMNS" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);

			strAudit.append("ACTIVITY_STATUS_NT" + auditDelimiterColVal + vObject.getActivityStatusNt());
			strAudit.append(auditDelimiter);

			strAudit.append("ACTIVITY_STATUS" + auditDelimiterColVal + vObject.getActivityStatus());
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

	public int effectiveDateCheck(ConcessionActivityConfigHeaderVb dObj, int status) {
		int cnt = 0;
		String query = "";
		try {
			if (status == 0) {
				query = " SELECT COUNT(1) FROM RA_Mst_Concession_Activity TAPPR "
						+ " WHERE TAPPR.COUNTRY = ? AND TAPPR.LE_BOOK = ? AND " + " TAPPR.ACTIVITY_ID = ?  ";
			} else {
				query = " SELECT COUNT(1) FROM RA_Mst_Concession_Activity_PEND TAPPR "
						+ " WHERE TAPPR.COUNTRY = ? AND TAPPR.LE_BOOK = ? AND  " + " TAPPR.ACTIVITY_ID = ?   ";
			}

			Object objParams[] = new Object[3];
			objParams[0] = new String(dObj.getCountry());// country
			objParams[1] = new String(dObj.getLeBook());
			objParams[2] = new String(dObj.getActivityId());
			cnt = getJdbcTemplate().queryForObject(query, objParams, Integer.class);
			return cnt;
		} catch (Exception e) {
			return cnt;
		}
	}

	public int effectiveDateBusinessCheck(ConcessionActivityConfigHeaderVb dObj) {
		int cnt = 0;
		String query = "";
		try {
			// query = " SELECT CASE WHEN ? >= BUSINESS_DATE THEN 1 ELSE 0 END
			// EFFECTIVE_DATE FROM VISION_BUSINESS_DAY WHERE COUNTRY = ? AND LE_BOOK = ? ";
			query = " SELECT CASE WHEN ? >= REPORT_BUSINESS_DATE THEN 1 ELSE 0 END EFFECTIVE_DATE FROM VISION_BUSINESS_DAY WHERE COUNTRY = ? AND LE_BOOK = ? ";

			Object objParams[] = new Object[2];
			objParams[1] = new String(dObj.getCountry());
			objParams[2] = new String(dObj.getLeBook());
			cnt = getJdbcTemplate().queryForObject(query, objParams, Integer.class);
			return cnt;
		} catch (Exception e) {
			return cnt;
		}
	}

	public ExceptionCode callLineMergeProc(String country, String leBook, String concessionId, String procedureId) {
		ExceptionCode exceptionCode = new ExceptionCode();
		setServiceDefaults();
		strCurrentOperation = "Query";
		strErrorDesc = "";
		Connection con = null;
		CallableStatement cs = null;
		try {
			con = getConnection();
			cs = con.prepareCall("{call " + procedureId + "(?,?,?,?,?)}");
			cs.setString(1, country);
			cs.setString(2, leBook);
			cs.setString(3, concessionId);
			cs.registerOutParameter(4, java.sql.Types.VARCHAR); // Status
			cs.registerOutParameter(5, java.sql.Types.VARCHAR); // Error Message
			cs.execute();
			exceptionCode.setErrorCode(Integer.parseInt(cs.getString(4)));
			exceptionCode.setErrorMsg(cs.getString(5));
			if (exceptionCode.getErrorCode() != 0) {
				//logger.error("Error on Line Merge[" + exceptionCode.getErrorMsg() + "]");
			}
			cs.close();
		} catch (Exception ex) {
			ex.printStackTrace();
			strErrorDesc = ex.getMessage().trim();
		} finally {
			JdbcUtils.closeStatement(cs);
			DataSourceUtils.releaseConnection(con, getDataSource());
		}
		return exceptionCode;
	}

	public List<BusinessLineHeaderVb> doSelectPendingBusinessLineRecord(BusinessLineHeaderVb dObj) {
		List<BusinessLineHeaderVb> collTemp = null;
		final int intKeyFieldsCount = 4;
		setServiceDefaults();
		String strQueryPend = null;
		strQueryPend = new String(
				"SELECT DISTINCT TPEND.COUNTRY, TPEND.LE_BOOK, TPEND.BUSINESS_LINE_ID,TPEND.BUSINESS_LINE_DESCRIPTION,TPEND.TRANS_LINE_TYPE,   "
						+ " TPEND.TRANS_LINE_ID,"
						+ " CASE WHEN TPEND.TRANS_LINE_TYPE='P' THEN T2.TRANS_LINE_PROD_GRP ELSE T2.TRANS_LINE_SERV_GRP END TRAN_LINE_GRP, "
						+ " CASE WHEN TPEND.TRANS_LINE_TYPE='P' THEN "
						+ " (SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB =T2.TRANS_LINE_PROD_GRP_AT AND ALPHA_SUB_TAB= T2.TRANS_LINE_PROD_GRP) "
						+ "  ELSE "
						+ "  (SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB =T2.TRANS_LINE_SERV_GRP_AT AND ALPHA_SUB_TAB= T2.TRANS_LINE_SERV_GRP) "
						+ "  END TRAN_LINE_GRP_DESC, "
						+ " TPEND.BUSINESS_LINE_TYPE,(SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB =TPEND.BUSINESS_LINE_TYPE_AT AND ALPHA_SUB_TAB= TPEND.BUSINESS_LINE_TYPE) BUSINESS_LINE_TYPE_DESC, "
						+ "TPEND.IE_TYPE, TPEND.ACTUAL_IE_POSTING, TPEND.ACTUAL_IE_MATCH_RULE ,  "
						+ " TPEND.BUSINESS_LINE_STATUS,T3.NUM_SUBTAB_DESCRIPTION BUSINESS_LINE_STATUS_DESC,TPEND.RECORD_INDICATOR,T1.NUM_SUBTAB_DESCRIPTION RECORD_INDICATOR_DESC,   "
						+ " TPEND. MAKER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "
						+ getDbFunction("NVL") + "(TPEND.MAKER,0) ) MAKER_NAME,   "
						+ " TPEND.VERIFIER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "
						+ getDbFunction("NVL") + "(TPEND.VERIFIER,0) ) VERIFIER_NAME,   " + " "
						+ getDbFunction("DATEFUNC") + "(TPEND.DATE_LAST_MODIFIED, 'dd-MM-yyyy "
						+ getDbFunction("TIME") + "') DATE_LAST_MODIFIED ,"
						+ getDbFunction("DATEFUNC") + "(TPEND.DATE_CREATION, 'dd-MM-yyyy "
						+ getDbFunction("TIME") + "') DATE_CREATION "
						+ " FROM RA_MST_BUSINESS_LINE_HEADER_PEND TPEND, NUM_SUB_TAB T1,NUM_SUB_TAB T3,RA_MST_TRANS_LINE_HEADER T2 ,RA_MST_TRANS_LINE_HEADER_PEND T4 "
						+ " WHERE TPEND.COUNTRY =? AND TPEND.LE_BOOK =? AND TPEND.BUSINESS_LINE_ID = ? AND TPEND.TRANS_LINE_ID = ? AND  "
						+ " T1.NUM_tab = TPEND.RECORD_INDICATOR_NT" + " and T1.NUM_sub_tab = TPEND.RECORD_INDICATOR"
						+ " AND (TPEND.TRANS_LINE_ID = T2.TRANS_LINE_ID OR TPEND.TRANS_LINE_ID = T4.TRANS_LINE_ID)  and T3.NUM_tab = TPEND.BUSINESS_LINE_STATUS_NT and T3.NUM_sub_tab = TPEND.BUSINESS_LINE_STATUS");
		Object objParams[] = new Object[intKeyFieldsCount];
		objParams[0] = new String(dObj.getCountry());// country
		objParams[1] = new String(dObj.getLeBook());
		objParams[2] = new String(dObj.getBusinessLineId());
		objParams[3] = new String(dObj.getTransLineId());
		try {
			logger.info("Executing pending query");
			collTemp = getJdbcTemplate().query(strQueryPend.toString(), objParams, getMapper1());
			return collTemp;
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("Error: getQueryResults Exception :   ");
			//logger.error(((strQueryPend == null) ? "strQueryPend is Null" : strQueryPend.toString()));

			/*if (objParams != null)
				for (int i = 0; i < objParams.length; i++)
					//logger.error("objParams[" + i + "]" + objParams[i].toString());
*/			return null;
		}
	}

	protected RowMapper getMapper1() {
		RowMapper mapper = new RowMapper() {
			@Override
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				BusinessLineHeaderVb vObject = new BusinessLineHeaderVb();
				vObject.setCountry(rs.getString("COUNTRY"));
				vObject.setLeBook(rs.getString("LE_BOOK"));
				vObject.setBusinessLineId(rs.getString("BUSINESS_LINE_ID"));
				vObject.setBusinessLineDescription(rs.getString("BUSINESS_LINE_DESCRIPTION"));
				vObject.setTransLineType(rs.getString("TRANS_LINE_TYPE"));
				vObject.setTransLineId(rs.getString("TRANS_LINE_ID"));
				vObject.setTranLineGrp(rs.getString("TRAN_LINE_GRP"));
				vObject.setTranLineGrpDesc(rs.getString("TRAN_LINE_GRP_DESC"));
				vObject.setBusinessLineType(rs.getString("BUSINESS_LINE_TYPE"));
				vObject.setIncomeExpenseType(rs.getString("IE_TYPE"));
				vObject.setActualIePosting(rs.getString("ACTUAL_IE_POSTING"));
				vObject.setActualIeMatchRule(rs.getString("ACTUAL_IE_MATCH_RULE"));
				vObject.setBusinessLineStatus(rs.getInt("BUSINESS_LINE_STATUS"));
				vObject.setRecordIndicator(rs.getInt("RECORD_INDICATOR"));
				vObject.setRecordIndicatorDesc(rs.getString("RECORD_INDICATOR_Desc"));
				vObject.setMaker(rs.getInt("MAKER"));
				vObject.setMakerName(rs.getString("MAKER_NAME"));
				vObject.setVerifier(rs.getInt("VERIFIER"));
				vObject.setVerifierName(rs.getString("VERIFIER_NAME"));
				vObject.setDateLastModified(rs.getString("DATE_LAST_MODIFIED"));
				vObject.setDateCreation(rs.getString("DATE_CREATION"));
				vObject.setBusinessLineTypeDesc(rs.getString("BUSINESS_LINE_TYPE_DESC"));
				vObject.setBusinessLineStatusDesc(rs.getString("BUSINESS_LINE_STATUS_DESC"));
				return vObject;
			}
		};
		return mapper;
	}

	protected RowMapper getMapperTrans() {
		RowMapper mapper = new RowMapper() {
			@Override
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				TransLineHeaderVb vObject = new TransLineHeaderVb();
				vObject.setCountry(rs.getString("COUNTRY"));
				vObject.setLeBook(rs.getString("LE_BOOK"));
				vObject.setTransLineId(rs.getString("TRANS_LINE_ID"));
				vObject.setTransLineType(rs.getString("TRANS_LINE_TYPE"));
				vObject.setTransLineDescription(rs.getString("TRANS_LINE_DESCRIPTION"));
				vObject.setTransLineSubType(rs.getString("TRANS_LINE_PROD_SUB_TYPE"));
				vObject.setTransLineGrp(rs.getString("TRANS_LINE_PROD_GRP"));
				vObject.setExtractionFrequency(rs.getString("EXTRACTION_FREQUENCY"));
				vObject.setExtractionMonthDay(rs.getString("EXTRACTION_MONTH_DAY"));
				vObject.setTargetStgTableId(rs.getString("TARGET_STG_TABLE_ID"));
				vObject.setTransLineStatus(rs.getInt("TRANS_LINE_STATUS"));
				vObject.setRecordIndicator(rs.getInt("RECORD_INDICATOR"));
				vObject.setRecordIndicatorDesc(rs.getString("RECORD_INDICATOR_Desc"));
				vObject.setMaker(rs.getInt("MAKER"));
				vObject.setMakerName(rs.getString("MAKER_NAME"));
				vObject.setVerifier(rs.getInt("VERIFIER"));
				vObject.setVerifierName(rs.getString("VERIFIER_NAME"));
				vObject.setDateLastModified(rs.getString("DATE_LAST_MODIFIED"));
				vObject.setDateCreation(rs.getString("DATE_CREATION"));
				vObject.setTransLineTypeDesc(rs.getString("ALPHA_SUBTAB_DESCRIPTION"));
				return vObject;
			}
		};
		return mapper;
	}

	public List<TransLineHeaderVb> doselectPendingproductRecord(TransLineHeaderVb dObj) {
		List<TransLineHeaderVb> collTemp = null;
		final int intKeyFieldsCount = 3;
		String strQueryPend = null;
		strQueryPend = new String(
				"SELECT TPend.COUNTRY, TPend.LE_BOOK, TPend.TRANS_LINE_ID,TPend.TRANS_LINE_DESCRIPTION,A1.ALPHA_SUBTAB_DESCRIPTION,TPend.TRANS_LINE_TYPE,  "
						+ "TPend.TRANS_LINE_PROD_SUB_TYPE,TPend.TRANS_LINE_PROD_GRP,TPend.EXTRACTION_FREQUENCY,EXTRACTION_MONTH_DAY, "
						+ "TARGET_STG_TABLE_ID,TPend.TRANS_LINE_STATUS,TPend.RECORD_INDICATOR,T1.NUM_SUBTAB_DESCRIPTION RECORD_INDICATOR_DESC, "
						+ "TPend. MAKER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "
						+ getDbFunction("NVL") + "(TPend.MAKER,0) ) MAKER_NAME,"
						+ "TPend. VERIFIER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "
						+ getDbFunction("NVL") + "(TPend.VERIFIER,0) ) VERIFIER_NAME, " + " "
						+ getDbFunction("DATEFUNC") + "(TPend.DATE_LAST_MODIFIED, 'dd-MM-yyyy "
						+ getDbFunction("TIME") + "') DATE_LAST_MODIFIED ,"
						+ getDbFunction("DATEFUNC") + "(TPend.DATE_CREATION, 'dd-MM-yyyy "
						+ getDbFunction("TIME") + "') DATE_CREATION "
						+ " FROM RA_MST_TRANS_LINE_HEADER_PEND TPend, NUM_SUB_TAB T1,ALPHA_SUB_TAB A1 WHERE "
						+ "TPend.COUNTRY =? AND TPend.LE_BOOK =? AND TPend.TRANS_LINE_ID = ? AND  T1.NUM_tab = TPend.RECORD_INDICATOR_NT"
						+ " and T1.NUM_sub_tab = TPend.RECORD_INDICATOR and TPend.TRANS_LINE_TYPE=A1.ALPHA_SUB_TAB  and A1.alpha_tab=TPend.TRANS_LINE_TYPE_AT");
		Object objParams[] = new Object[intKeyFieldsCount];
		objParams[0] = new String(dObj.getCountry());// country
		objParams[1] = new String(dObj.getLeBook());
		objParams[2] = new String(dObj.getTransLineId());
		try {
			collTemp = getJdbcTemplate().query(strQueryPend.toString(), objParams, getMapperTrans());
			return collTemp;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}

	}

	@Override
	protected ExceptionCode doDeleteRecordForNonTrans(ConcessionActivityConfigHeaderVb vObject)
			throws RuntimeCustomException {
		ConcessionActivityConfigHeaderVb vObjectlocal = null;
		List<ConcessionActivityConfigHeaderVb> collTemp = null;
		ExceptionCode exceptionCode = null;
		strApproveOperation = Constants.DELETE;
		strErrorDesc = "";
		strCurrentOperation = Constants.DELETE;
		setServiceDefaults();
		collTemp = selectApprovedRecord(vObject);

		if (collTemp == null) {
			logger.error("Collection is null");
			exceptionCode = getResultObject(Constants.ERRONEOUS_OPERATION);
			throw buildRuntimeCustomException(exceptionCode);
		}
		// If record already exists in the approved table, reject the addition
		if (collTemp.size() > 0) {
			vObjectlocal = ((ArrayList<ConcessionActivityConfigHeaderVb>) collTemp).get(0);
			int intStaticDeletionFlag = getStatus(vObjectlocal);
			if (intStaticDeletionFlag == Constants.PASSIVATE) {
				exceptionCode = getResultObject(Constants.CANNOT_DELETE_AN_INACTIVE_RECORD);
				throw buildRuntimeCustomException(exceptionCode);
			}
		} else {
			exceptionCode = getResultObject(Constants.ATTEMPT_TO_DELETE_UNEXISTING_RECORD);
			throw buildRuntimeCustomException(exceptionCode);
		}

		// check to see if the record already exists in the pending table
		collTemp = doSelectPendingRecord(vObject);
		if (collTemp == null) {
			exceptionCode = getResultObject(Constants.ERRONEOUS_OPERATION);
			throw buildRuntimeCustomException(exceptionCode);
		}

		// If records are there, check for the status and decide what error to return
		// back
		if (collTemp.size() > 0) {
			exceptionCode = getResultObject(Constants.TRYING_TO_DELETE_APPROVAL_PENDING_RECORD);
			throw buildRuntimeCustomException(exceptionCode);
		}

		// insert the record into pending table with status 3 - deletion
		if (vObjectlocal == null) {
			exceptionCode = getResultObject(Constants.ERRONEOUS_OPERATION);
			throw buildRuntimeCustomException(exceptionCode);
		}
		if (collTemp.size() > 0) {
			vObjectlocal = ((ArrayList<ConcessionActivityConfigHeaderVb>) collTemp).get(0);
			vObjectlocal.setDateCreation(vObject.getDateCreation());
		}
		// vObjectlocal.setDateCreation(vObject.getDateCreation());
		vObjectlocal.setMaker(getIntCurrentUserId());
		vObjectlocal.setRecordIndicator(Constants.STATUS_DELETE);
		vObject.setRecordIndicator(Constants.STATUS_DELETE);
		vObjectlocal.setVerifier(0);
//		retVal = doInsertionPendWithDc(vObjectlocal);
		retVal = doInsertionPendConcessionHeadersDc(vObjectlocal);
		// Fee Detail

		List<ConcessionActivityFilterVb> collTempFilter = null;
		collTempFilter = concessionFilterDao.getConcessionFilters(vObject, 0);
		if (collTempFilter != null && collTempFilter.size() > 0) {
			collTempFilter.forEach(confltr -> {
				confltr.setRecordIndicator(vObject.getRecordIndicator());
				retVal=	concessionFilterDao.doInsertionPendConcessionFilter(confltr);
			});
		}
		if (retVal != Constants.SUCCESSFUL_OPERATION) {
			exceptionCode = getResultObject(retVal);
			throw buildRuntimeCustomException(exceptionCode);
		}
		vObject.setRecordIndicator(Constants.STATUS_DELETE);
		vObject.setVerifier(0);
		return getResultObject(Constants.SUCCESSFUL_OPERATION);
	}

	@Override
	protected ExceptionCode doDeleteApprRecordForNonTrans(ConcessionActivityConfigHeaderVb vObject)
			throws RuntimeCustomException {
		List<ConcessionActivityConfigHeaderVb> collTemp = null;
		ExceptionCode exceptionCode = null;
		strApproveOperation = Constants.DELETE;
		strErrorDesc = "";
		strCurrentOperation = Constants.DELETE;
		setServiceDefaults();
		ConcessionActivityConfigHeaderVb vObjectlocal = null;
		vObject.setMaker(getIntCurrentUserId());
		if ("RUNNING".equalsIgnoreCase(getBuildStatus(vObject))) {
			exceptionCode = getResultObject(Constants.BUILD_IS_RUNNING);
			throw buildRuntimeCustomException(exceptionCode);
		}
		collTemp = selectApprovedRecord(vObject);
		if (collTemp == null) {
			exceptionCode = getResultObject(Constants.ERRONEOUS_OPERATION);
			throw buildRuntimeCustomException(exceptionCode);
		}
		// If record already exists in the approved table, reject the addition
		if (collTemp.size() > 0) {
			int intStaticDeletionFlag = getStatus(((ArrayList<ConcessionActivityConfigHeaderVb>) collTemp).get(0));
			if (intStaticDeletionFlag == Constants.PASSIVATE) {
				exceptionCode = getResultObject(Constants.CANNOT_DELETE_AN_INACTIVE_RECORD);
				throw buildRuntimeCustomException(exceptionCode);
			}
		} else {
			exceptionCode = getResultObject(Constants.ATTEMPT_TO_DELETE_UNEXISTING_RECORD);
			throw buildRuntimeCustomException(exceptionCode);
		}
		vObjectlocal = ((ArrayList<ConcessionActivityConfigHeaderVb>) collTemp).get(0);
		vObject.setDateCreation(vObjectlocal.getDateCreation());
		if (vObject.isStaticDelete()) {
			vObjectlocal.setMaker(getIntCurrentUserId());
			vObject.setVerifier(getIntCurrentUserId());
			vObject.setRecordIndicator(Constants.STATUS_ZERO);
			String systemDate = getSystemDate();
			vObject.setDateLastModified(systemDate);
//			setStatus(vObject, Constants.PASSIVATE);
			setStatus(vObjectlocal, Constants.PASSIVATE);
			vObjectlocal.setVerifier(getIntCurrentUserId());
			vObjectlocal.setRecordIndicator(Constants.STATUS_ZERO);
			retVal = doUpdateApprHeader(vObjectlocal);
			List<ConcessionActivityFilterVb> collTempFilter = null;
			collTempFilter = concessionFilterDao.getConcessionFilters(vObject, 1);
			if (collTempFilter != null && collTempFilter.size() > 0) {
				concessionFilterDao.deleteAndInsertApprFilter(vObjectlocal);
			}
		} else {
			// delete the record from the Approve Table
			retVal = deleteConcessionHeaderAppr(vObject);
			if (retVal != Constants.SUCCESSFUL_OPERATION) {
				exceptionCode = getResultObject(retVal);
				throw buildRuntimeCustomException(exceptionCode);
			}
//			vObject.setRecordIndicator(-1);
			String systemDate = getSystemDate();
			vObject.setDateLastModified(systemDate);

			List<ConcessionActivityFilterVb> collTempFilter = null;
			collTempFilter = concessionFilterDao.getConcessionFilters(vObject, 0);
			if (collTempFilter != null && collTempFilter.size() > 0) {
				int delCnt = concessionFilterDao.deleteConcessionFilterAppr(vObject);
				if (delCnt != Constants.SUCCESSFUL_OPERATION) {
					exceptionCode = getResultObject(retVal);
					throw buildRuntimeCustomException(exceptionCode);
				}
			}
		}
		if (vObject.isStaticDelete()) {
			setStatus(vObjectlocal, Constants.STATUS_ZERO);
			setStatus(vObject, Constants.PASSIVATE);
			exceptionCode = writeAuditLog(vObject, vObjectlocal);
		} else {
			exceptionCode = writeAuditLog(null, vObject);
			vObject.setRecordIndicator(-1);
		}
		if (exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION) {
			exceptionCode = getResultObject(Constants.AUDIT_TRAIL_ERROR);
			throw buildRuntimeCustomException(exceptionCode);
		}
		return exceptionCode;
	}

	@Override
	@Transactional(rollbackForClassName = { "com.vision.exception.RuntimeCustomException" })
	public ExceptionCode bulkApprove(List<ConcessionActivityConfigHeaderVb> vObjects, boolean staticDelete)
			throws RuntimeCustomException {
		strErrorDesc = "";
		strCurrentOperation = Constants.APPROVE;
		ExceptionCode exceptionCode = null;
		setServiceDefaults();
		try {
			boolean foundFlag = false;
			for (ConcessionActivityConfigHeaderVb object : vObjects) {
				exceptionCode = doApproveRecord(object, staticDelete);
				if (exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION) {
					exceptionCode.setResponse(vObjects);
					exceptionCode.setOtherInfo(vObjects);
				}
				foundFlag = true;
			}

			if (foundFlag == false) {
				logger.error("No Records To Approve");
				exceptionCode = getResultObject(Constants.NO_RECORDS_TO_APPROVE);
				throw buildRuntimeCustomException(exceptionCode);
			}

			if (exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION) {
				logger.error("Error in Bulk Approve. " + exceptionCode.getErrorMsg());
				exceptionCode = getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION);
				throw buildRuntimeCustomException(exceptionCode);
			}
			return exceptionCode;
		} catch (Exception ex) {
			logger.error("Error in Bulk Approve.", ex);
			strErrorDesc = ex.getMessage();
			exceptionCode = getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION);
			throw buildRuntimeCustomException(exceptionCode);
		}
	}

	@Override
	@Transactional(rollbackForClassName = { "com.vision.exception.RuntimeCustomException" })
	public ExceptionCode doBulkReject(List<ConcessionActivityConfigHeaderVb> vObjects) throws RuntimeCustomException {

		strErrorDesc = "";
		strCurrentOperation = Constants.REJECT;
		setServiceDefaults();
		ExceptionCode exceptionCode = null;
		try {
			boolean foundFlag = false;
			for (ConcessionActivityConfigHeaderVb object : vObjects) {

				foundFlag = true;

				exceptionCode = doRejectRecord(object);

			}
			if (foundFlag == false) {
				logger.error("No Records To Reject");
				exceptionCode = getResultObject(Constants.NO_RECORDS_TO_REJECT);
				throw buildRuntimeCustomException(exceptionCode);
			}

			if (exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION) {
				logger.error("Error in Bulk Reject. " + exceptionCode.getErrorMsg());
				exceptionCode = getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION);
				throw buildRuntimeCustomException(exceptionCode);
			}

			return exceptionCode;
		} catch (Exception ex) {
			logger.error("Error in Bulk Reject.", ex);
			strErrorDesc = ex.getMessage();
			exceptionCode = getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION);
			throw buildRuntimeCustomException(exceptionCode);
		}

	}

}