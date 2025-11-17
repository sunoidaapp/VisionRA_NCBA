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
import org.springframework.dao.DataAccessException;
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
import com.vision.vb.AlphaSubTabVb;
import com.vision.vb.BusinessLineHeaderVb;
import com.vision.vb.ConcessionConfigDetailsVb;
import com.vision.vb.ConcessionConfigHeaderVb;
import com.vision.vb.ConcessionConfigTierVb;
import com.vision.vb.ConcessionFilterVb;
import com.vision.vb.SmartSearchVb;
import com.vision.vb.TransLineHeaderVb;
import com.vision.vb.VisionUsersVb;

@Component
public class ConcessionHeaderDao extends AbstractDao<ConcessionConfigHeaderVb> {
	@Autowired
	CommonDao commonDao;
	@Autowired
	ConcessionConfigDetailsDao concessionConfigDetailsDao;
	@Autowired
	ConcessionFilterDao concessionFilterDao;
	@Autowired
	ConcessionConfigTierDao concessionConfigTierDao;

	@Value("${app.databaseType}")
	private String databaseType;

	@Override
	protected RowMapper getMapper() {
		RowMapper mapper = new RowMapper() {
			@Override
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				ConcessionConfigHeaderVb vObject = new ConcessionConfigHeaderVb();
				vObject.setCountry(rs.getString("COUNTRY"));
				vObject.setLeBook(rs.getString("LE_BOOK"));
				vObject.setConcessionId(rs.getString("CONCESSION_ID"));
				vObject.setConcessionDesc(rs.getString("CONCESSION_DESCRIPTION"));
				vObject.setEffectiveDate(rs.getString("EFFECTIVE_DATE"));
				vObject.setEndDate(rs.getString("End_Date"));
				vObject.setConcessionAmountCalc(rs.getString("Concession_Amount_Calc"));
				vObject.setDiscountType(rs.getString("DISCOUNT_TYPE"));
				vObject.setDiscountTypeDesc(rs.getString("DISCOUNT_TYPE_DESC"));
				vObject.setFeeType(rs.getString("Fee_Type"));
				vObject.setTierType(rs.getString("Tier_Type"));
				vObject.setActivityLinkFlag(rs.getString("Activity_Link_Flag"));
				vObject.setConcessionStatus(rs.getInt("Concession_Status"));
				vObject.setConcessionStatusDesc(rs.getString("Concession_Status_Desc"));
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

	protected RowMapper getDetailMapper() {
		RowMapper mapper = new RowMapper() {
			@Override
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				ConcessionConfigHeaderVb vObject = new ConcessionConfigHeaderVb();
				vObject.setCountry(rs.getString("COUNTRY"));
				vObject.setLeBook(rs.getString("LE_BOOK"));
				vObject.setConcessionId(rs.getString("CONCESSION_ID"));
				vObject.setConcessionDesc(rs.getString("CONCESSION_DESCRIPTION"));
				vObject.setEffectiveDate(rs.getString("EFFECTIVE_DATE"));
				vObject.setEndDate(rs.getString("End_Date"));
				vObject.setConcessionAmountCalc(rs.getString("Concession_Amount_Calc"));
				vObject.setDiscountType(rs.getString("DISCOUNT_TYPE"));
				vObject.setDiscountTypeDesc(rs.getString("DISCOUNT_TYPE_DESC"));
				vObject.setFeeType(rs.getString("Fee_Type"));
				vObject.setTierType(rs.getString("Tier_Type"));
				vObject.setActivityLinkFlag(rs.getString("Activity_Link_Flag"));
				vObject.setConcessionStatus(rs.getInt("Concession_Status"));
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

	protected RowMapper getReviewMapper() {
		RowMapper mapper = new RowMapper() {
			@Override
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				ConcessionConfigHeaderVb vObject = new ConcessionConfigHeaderVb();
				vObject.setCountry(rs.getString("COUNTRY"));
				vObject.setLeBook(rs.getString("LE_BOOK"));
				vObject.setConcessionId(rs.getString("CONCESSION_ID"));
				vObject.setConcessionDesc(rs.getString("CONCESSION_DESCRIPTION"));
				vObject.setEffectiveDate(rs.getString("EFFECTIVE_DATE"));
				vObject.setEndDate(rs.getString("End_Date"));
				vObject.setConcessionAmountCalc(rs.getString("Concession_Amount_Calc"));
				vObject.setDiscountType(rs.getString("DISCOUNT_TYPE"));
				vObject.setDiscountTypeDesc(rs.getString("DISCOUNT_TYPE_DESC"));
				vObject.setFeeType(rs.getString("Fee_Type"));
				vObject.setFeeTypeDesc(rs.getString("FEE_TYPE_DESC"));
				vObject.setTierType(rs.getString("Tier_Type"));
				vObject.setTierTypeDesc(rs.getString("TIER_TYPE_DESC"));
				vObject.setActivityLinkFlag(rs.getString("Activity_Link_Flag"));
				vObject.setConcessionStatus(rs.getInt("Concession_Status"));
				vObject.setConcessionStatusDesc(rs.getString("Concession_Status_Desc"));
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
	public List<ConcessionConfigHeaderVb> getQueryPopupResults(ConcessionConfigHeaderVb dObj) {
		Vector<Object> params = new Vector<Object>();
		StringBuffer strBufApprove = null;
		StringBuffer strBufPending = null;
		String strWhereNotExists = null;
		String orderBy = "";
		String effectiveDateAppr = "";
		String effectiveDatePend = "";
		if ("ORACLE".equalsIgnoreCase(databaseType)) {
			effectiveDateAppr = "TO_CHAR(TAPPR.EFFECTIVE_DATE,'DD-Mon-RRRR') EFFECTIVE_DATE,TO_CHAR(TAPPR.END_DATE,'DD-Mon-RRRR') END_DATE ";
			effectiveDatePend = "TO_CHAR(TPend.EFFECTIVE_DATE,'DD-Mon-RRRR') EFFECTIVE_DATE,TO_CHAR(TPend.END_DATE,'DD-Mon-RRRR') END_DATE";
		} else if ("MSSQL".equalsIgnoreCase(databaseType)) {
			effectiveDateAppr = "format(CAST(TAppr.EFFECTIVE_DATE AS DATETIME), 'dd-MMM-yyyy') EFFECTIVE_DATE,format(CAST(TAppr.END_DATE AS DATETIME), 'dd-MMM-yyyy') END_DATE";
			effectiveDatePend = "format(CAST(TPend.EFFECTIVE_DATE AS DATETIME), 'dd-MMM-yyyy') EFFECTIVE_DATE,format(CAST(TPend.END_DATE AS DATETIME), 'dd-MMM-yyyy') END_DATE";
		}

		strBufApprove = new StringBuffer(
				"Select * from ( SELECT TAppr.COUNTRY, TAppr.LE_BOOK,TAppr.CONCESSION_ID, CONCESSION_DESCRIPTION, "
						+ "	" + effectiveDateAppr
						+ ",TAPPR.DISCOUNT_TYPE,(select T1.ALPHA_SUBTAB_DESCRIPTION from ALPHA_SUB_TAB T1 "
						+ "      where t1.Alpha_tab = TAppr.DISCOUNT_TYPE_AT and T1.ALPHA_SUB_TAB=TAppr.DISCOUNT_TYPE) DISCOUNT_TYPE_DESC, "
						+ " TAPPR.FEE_TYPE,TAPPR.TIER_TYPE,TAPPR.ACTIVITY_LINK_FLAG,TAPPR.CONCESSION_AMOUNT_CALC, "
						+ " TAppr.Concession_Status,T3.NUM_SUBTAB_DESCRIPTION Concession_Status_DESC, "
						+ " TAppr.RECORD_INDICATOR,T4.NUM_SUBTAB_DESCRIPTION RECORD_INDICATOR_DESC,"
						+ " TAppr. MAKER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "
						+ getDbFunction("NVL") + "(TAppr.MAKER,0) ) MAKER_NAME, "
						+ " TAppr.VERIFIER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "
						+ getDbFunction("NVL") + "(TAppr.VERIFIER,0) ) VERIFIER_NAME,  " + " "
						+ getDbFunction("DATEFUNC") + "(TAppr.DATE_LAST_MODIFIED, 'dd-MMM-yyyy "
						+ getDbFunction("TIME") + "') DATE_LAST_MODIFIED ,TAppr.DATE_LAST_MODIFIED DATE_LAST_MODIFIED_1,"
						+ getDbFunction("DATEFUNC") + "(TAppr.DATE_CREATION, 'dd-MMM-yyyy "
						+ getDbFunction("TIME") + "') DATE_CREATION "
						+ " FROM RA_MST_CONCESSION_HEADER TAppr ,NUM_SUB_TAB T3,NUM_SUB_TAB T4     " + " Where  "
						+ "	 t3.NUM_tab = TAppr.Concession_Status_NT  "
						+ " and t3.NUM_sub_tab = TAppr.Concession_Status  "
						+ " and t4.NUM_tab = TAppr.RECORD_INDICATOR_NT  "
						+ " and t4.NUM_sub_tab = TAppr.RECORD_INDICATOR ) TAppr");

		strWhereNotExists = new String(
				" Not Exists (Select 'X' From RA_MST_CONCESSION_HEADER_PEND TPEND WHERE TAppr.COUNTRY = TPend.COUNTRY"
						+ " AND TAppr.LE_BOOK = TPend.LE_BOOK  AND TAPPR.CONCESSION_ID = TPEND.CONCESSION_ID"
						+ " AND TAPPR.EFFECTIVE_DATE = TPEND.EFFECTIVE_DATE)");

		strBufPending = new StringBuffer(
				"Select * from ( SELECT TPend.COUNTRY, TPend.LE_BOOK,TPend.CONCESSION_ID, CONCESSION_DESCRIPTION, "
						+ "	" + effectiveDatePend
						+ ",TPend.DISCOUNT_TYPE,(select T1.ALPHA_SUBTAB_DESCRIPTION from ALPHA_SUB_TAB T1 "
						+ "      where t1.Alpha_tab = TPend.DISCOUNT_TYPE_AT and T1.ALPHA_SUB_TAB=TPend.DISCOUNT_TYPE) DISCOUNT_TYPE_DESC ,"
						+ " TPend.FEE_TYPE,TPend.TIER_TYPE,TPend.ACTIVITY_LINK_FLAG, TPend.CONCESSION_AMOUNT_CALC,"
						+ " TPend.Concession_Status,T3.NUM_SUBTAB_DESCRIPTION Concession_Status_DESC, "
						+ " TPend.RECORD_INDICATOR,T4.NUM_SUBTAB_DESCRIPTION RECORD_INDICATOR_DESC,"
						+ " TPend. MAKER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "
						+ getDbFunction("NVL") + "(TPend.MAKER,0) ) MAKER_NAME, "
						+ " TPend.VERIFIER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "
						+ getDbFunction("NVL") + "(TPend.VERIFIER,0) ) VERIFIER_NAME,  " + " "
						+ getDbFunction("DATEFUNC") + "(TPend.DATE_LAST_MODIFIED, 'dd-MMM-yyyy "
						+ getDbFunction("TIME") + "') DATE_LAST_MODIFIED ,TPend.DATE_LAST_MODIFIED DATE_LAST_MODIFIED_1,"
						+ getDbFunction("DATEFUNC") + "(TPend.DATE_CREATION, 'dd-MMM-yyyy "
						+ getDbFunction("TIME") + "') DATE_CREATION "
						+ " FROM RA_MST_CONCESSION_HEADER_PEND TPend ,NUM_SUB_TAB T3,NUM_SUB_TAB T4     " + " Where  "
						+ "	 t3.NUM_tab = TPend.Concession_Status_NT  "
						+ " and t3.NUM_sub_tab = TPend.Concession_Status  "
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

					case "concessionId":
						CommonUtils.addToQuerySearch(" upper(TAPPR.CONCESSION_ID) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.CONCESSION_ID) " + val, strBufPending,
								data.getJoinType());
						break;

					case "concessionDesc":
						CommonUtils.addToQuerySearch(" upper(TAPPR.CONCESSION_DESCRIPTION) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.CONCESSION_DESCRIPTION) " + val, strBufPending,
								data.getJoinType());
						break;

					case "effectiveDate":
						CommonUtils.addToQuerySearch(" " + getDbFunction("DATEFUNC")
								+ "(TAPPR.EFFECTIVE_DATE 'DD-MMM-YYYY') " + val, strBufApprove, data.getJoinType());
						CommonUtils.addToQuerySearch(" " + getDbFunction("DATEFUNC")
								+ "(TPend.EFFECTIVE_DATE 'DD-MMM-YYYY') " + val, strBufPending, data.getJoinType());
						break;

					case "endDate":
						CommonUtils.addToQuerySearch(
								" " + getDbFunction("DATEFUNC") + "(TAPPR.END_DATE 'DD-MMM-YYYY') " + val,
								strBufApprove, data.getJoinType());
						CommonUtils.addToQuerySearch(
								" " + getDbFunction("DATEFUNC") + "(TPend.END_DATE 'DD-MMM-YYYY') " + val,
								strBufPending, data.getJoinType());
						break;

					case "concessionStatusDesc":
						CommonUtils.addToQuerySearch(" upper(TAPPR.Concession_Status_DESC) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.Concession_Status_DESC) " + val, strBufPending,
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
			orderBy = " Order by DATE_LAST_MODIFIED_1 DESC,CONCESSION_ID ";
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
	public List<ConcessionConfigHeaderVb> getQueryResults(ConcessionConfigHeaderVb dObj, int intStatus) {
		List<ConcessionConfigHeaderVb> collTemp = null;
		setServiceDefaults();
		String strQueryAppr = null;
		String strQueryPend = null;
		String effectiveDateAppr = "";
		String effectiveDatePend = "";
		if ("ORACLE".equalsIgnoreCase(databaseType)) {
			effectiveDateAppr = "TO_CHAR(TAPPR.EFFECTIVE_DATE,'DD-Mon-RRRR') EFFECTIVE_DATE,TO_CHAR(TAPPR.END_DATE,'DD-Mon-RRRR') END_DATE ";
			effectiveDatePend = "TO_CHAR(TPend.EFFECTIVE_DATE,'DD-Mon-RRRR') EFFECTIVE_DATE,TO_CHAR(TPend.END_DATE,'DD-Mon-RRRR') END_DATE";
		} else if ("MSSQL".equalsIgnoreCase(databaseType)) {
			effectiveDateAppr = "format(CAST(TAppr.EFFECTIVE_DATE AS DATETIME), 'dd-MMM-yyyy') EFFECTIVE_DATE,format(CAST(TAppr.END_DATE AS DATETIME), 'dd-MMM-yyyy') END_DATE";
			effectiveDatePend = "format(CAST(TPend.EFFECTIVE_DATE AS DATETIME), 'dd-MMM-yyyy') EFFECTIVE_DATE,format(CAST(TPend.END_DATE AS DATETIME), 'dd-MMM-yyyy') END_DATE";
		}
		strQueryAppr = new String(" SELECT TAppr.COUNTRY, TAppr.LE_BOOK,TAppr.CONCESSION_ID, CONCESSION_DESCRIPTION, "
				+ "	" + effectiveDateAppr
				+ ",TAPPR.DISCOUNT_TYPE,(select T1.ALPHA_SUBTAB_DESCRIPTION from ALPHA_SUB_TAB T1 "
				+ "      where t1.Alpha_tab = TAppr.DISCOUNT_TYPE_AT and T1.ALPHA_SUB_TAB=TAppr.DISCOUNT_TYPE) DISCOUNT_TYPE_DESC, "
				+ " TAPPR.FEE_TYPE,TAPPR.TIER_TYPE,TAPPR.ACTIVITY_LINK_FLAG, TAPPR.CONCESSION_AMOUNT_CALC,"
				+ " TAppr.Concession_Status,T3.NUM_SUBTAB_DESCRIPTION TAPPR, "
				+ " TAppr.RECORD_INDICATOR,T4.NUM_SUBTAB_DESCRIPTION RECORD_INDICATOR_DESC,"
				+ " TAppr. MAKER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "
				+ getDbFunction("NVL") + "(TAppr.MAKER,0) ) MAKER_NAME, "
				+ " TAppr.VERIFIER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "
				+ getDbFunction("NVL") + "(TAppr.VERIFIER,0) ) VERIFIER_NAME,  " + " "
				+ getDbFunction("DATEFUNC") + "(TAppr.DATE_LAST_MODIFIED, 'dd-MM-yyyy "
				+ getDbFunction("TIME") + "') DATE_LAST_MODIFIED ," + getDbFunction("DATEFUNC")
				+ "(TAppr.DATE_CREATION, 'dd-MM-yyyy " + getDbFunction("TIME") + "') DATE_CREATION "
				+ " FROM RA_MST_CONCESSION_HEADER TAppr ,NUM_SUB_TAB T3,NUM_SUB_TAB T4     " + " Where  "
				+ "	 t3.NUM_tab = TAppr.Concession_Status_NT  " + " and t3.NUM_sub_tab = TAppr.Concession_Status  "
				+ " and t4.NUM_tab = TAppr.RECORD_INDICATOR_NT  "
				+ " and t4.NUM_sub_tab = TAppr.RECORD_INDICATOR and TAppr.COUNTRY = ? "
				+ " and TAppr.LE_BOOK = ? and TAppr.CONCESSION_ID = ? " + " and TAppr.Effective_Date = ? ");
		strQueryPend = new String(" SELECT TPend.COUNTRY, TPend.LE_BOOK,TPend.CONCESSION_ID, CONCESSION_DESCRIPTION, "
				+ "	" + effectiveDatePend
				+ ",TPend.DISCOUNT_TYPE,(select T1.ALPHA_SUBTAB_DESCRIPTION from ALPHA_SUB_TAB T1 "
				+ "      where t1.Alpha_tab = TPend.DISCOUNT_TYPE_AT and T1.ALPHA_SUB_TAB=TPend.DISCOUNT_TYPE) DISCOUNT_TYPE_DESC, "
				+ " TPend.FEE_TYPE,TPend.TIER_TYPE,TPend.ACTIVITY_LINK_FLAG,TPend.CONCESSION_AMOUNT_CALC, "
				+ " TPend.Concession_Status,T3.NUM_SUBTAB_DESCRIPTION Concession_Status_DESC, "
				+ " TPend.RECORD_INDICATOR,T4.NUM_SUBTAB_DESCRIPTION RECORD_INDICATOR_DESC,"
				+ " TPend. MAKER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "
				+ getDbFunction("NVL") + "(TPend.MAKER,0) ) MAKER_NAME, "
				+ " TPend.VERIFIER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "
				+ getDbFunction("NVL") + "(TPend.VERIFIER,0) ) VERIFIER_NAME,  " + " "
				+ getDbFunction("DATEFUNC") + "(TPend.DATE_LAST_MODIFIED, 'dd-MM-yyyy "
				+ getDbFunction("TIME") + "') DATE_LAST_MODIFIED ," + getDbFunction("DATEFUNC")
				+ "(TPend.DATE_CREATION, 'dd-MM-yyyy " + getDbFunction("TIME") + "') DATE_CREATION "
				+ " FROM RA_MST_CONCESSION_HEADER_PEND TPend ,NUM_SUB_TAB T3,NUM_SUB_TAB T4     " + " Where  "
				+ "	 t3.NUM_tab = TPend.Concession_Status_NT  " + " and t3.NUM_sub_tab = TPend.Concession_Status  "
				+ " and t4.NUM_tab = TPend.RECORD_INDICATOR_NT  "
				+ " and t4.NUM_sub_tab = TPend.RECORD_INDICATOR And TPend.COUNTRY = ? "
				+ " and TPend.LE_BOOK = ? and TPend.CONCESSION_ID = ? " + " and TPend.Effective_Date = ? ");

		Object objParams[] = new Object[4];
		objParams[0] = new String(dObj.getCountry());
		objParams[1] = new String(dObj.getLeBook());
		objParams[2] = new String(dObj.getConcessionId());
		objParams[3] = new String(dObj.getEffectiveDate());
		try {
			if (intStatus == 0) {
				logger.info("Executing approved query");
				collTemp = getJdbcTemplate().query(strQueryAppr.toString(), objParams, getDetailMapper());
			} else {
				logger.info("Executing pending query");
				collTemp = getJdbcTemplate().query(strQueryPend.toString(), objParams, getDetailMapper());
			}
			return collTemp;
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("Error: getQueryResults Exception :   ");
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
	public List<ConcessionConfigHeaderVb> getQueryResultsForReview(ConcessionConfigHeaderVb dObj, int intStatus) {
		List<ConcessionConfigHeaderVb> collTemp = null;
		setServiceDefaults();
		String strQueryAppr = null;
		String strQueryPend = null;
		String effectiveDateAppr = "";
		String effectiveDatePend = "";
		if ("ORACLE".equalsIgnoreCase(databaseType)) {
			effectiveDateAppr = "TO_CHAR(TAPPR.EFFECTIVE_DATE,'DD-Mon-RRRR') EFFECTIVE_DATE,TO_CHAR(TAPPR.END_DATE,'DD-Mon-RRRR') END_DATE ";
			effectiveDatePend = "TO_CHAR(TPend.EFFECTIVE_DATE,'DD-Mon-RRRR') EFFECTIVE_DATE,TO_CHAR(TPend.END_DATE,'DD-Mon-RRRR') END_DATE";
		} else if ("MSSQL".equalsIgnoreCase(databaseType)) {
			effectiveDateAppr = "format(CAST(TAppr.EFFECTIVE_DATE AS DATETIME), 'dd-MMM-yyyy') EFFECTIVE_DATE,format(CAST(TAppr.END_DATE AS DATETIME), 'dd-MMM-yyyy') END_DATE";
			effectiveDatePend = "format(CAST(TPend.EFFECTIVE_DATE AS DATETIME), 'dd-MMM-yyyy') EFFECTIVE_DATE,format(CAST(TPend.END_DATE AS DATETIME), 'dd-MMM-yyyy') END_DATE";
		}
		strQueryAppr = new String(" SELECT TAppr.COUNTRY, TAppr.LE_BOOK,TAppr.CONCESSION_ID, CONCESSION_DESCRIPTION, "
				+ "	" + effectiveDateAppr
				+ ",TAPPR.DISCOUNT_TYPE,(select T1.ALPHA_SUBTAB_DESCRIPTION from ALPHA_SUB_TAB T1 "
				+ "      where t1.Alpha_tab = TAPPR.DISCOUNT_TYPE_AT and T1.ALPHA_SUB_TAB=TAPPR.DISCOUNT_TYPE) DISCOUNT_TYPE_DESC, "
				+ " TAPPR.FEE_TYPE,(SELECT T1.ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB T1 where t1.Alpha_tab =  7033 and T1.ALPHA_SUB_TAB=TAPPR.FEE_TYPE )FEE_TYPE_DESC, "
				+ "TAPPR.TIER_TYPE,(SELECT T1.ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB T1 where t1.Alpha_tab =  7034 and T1.ALPHA_SUB_TAB=TAPPR.TIER_TYPE )TIER_TYPE_DESC,"
				+ "TAPPR.ACTIVITY_LINK_FLAG,TAPPR.CONCESSION_AMOUNT_CALC, "
				+ " TAppr.Concession_Status,T3.NUM_SUBTAB_DESCRIPTION Concession_Status_DESC, "
				+ " TAppr.RECORD_INDICATOR,T4.NUM_SUBTAB_DESCRIPTION RECORD_INDICATOR_DESC,"
				+ " TAppr. MAKER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "
				+ getDbFunction("NVL") + "(TAppr.MAKER,0) ) MAKER_NAME, "
				+ " TAppr.VERIFIER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "
				+ getDbFunction("NVL") + "(TAppr.VERIFIER,0) ) VERIFIER_NAME,  " + " "
				+ getDbFunction("DATEFUNC") + "(TAppr.DATE_LAST_MODIFIED, 'dd-MM-yyyy "
				+ getDbFunction("TIME") + "') DATE_LAST_MODIFIED ," + getDbFunction("DATEFUNC")
				+ "(TAppr.DATE_CREATION, 'dd-MM-yyyy " + getDbFunction("TIME") + "') DATE_CREATION "
				+ " FROM RA_MST_CONCESSION_HEADER TAppr ,NUM_SUB_TAB T3,NUM_SUB_TAB T4     " + " Where  "
				+ "	 t3.NUM_tab = TAppr.Concession_Status_NT  " + " and t3.NUM_sub_tab = TAppr.Concession_Status  "
				+ " and t4.NUM_tab = TAppr.RECORD_INDICATOR_NT  "
				+ " and t4.NUM_sub_tab = TAppr.RECORD_INDICATOR and TAppr.COUNTRY = ? "
				+ " and TAppr.LE_BOOK = ? and TAppr.CONCESSION_ID = ? " + " and TAppr.Effective_Date = ? ");
		strQueryPend = new String(" SELECT TPend.COUNTRY, TPend.LE_BOOK,TPend.CONCESSION_ID, CONCESSION_DESCRIPTION, "
				+ "	" + effectiveDatePend
				+ ",TPend.DISCOUNT_TYPE,(select T1.ALPHA_SUBTAB_DESCRIPTION from ALPHA_SUB_TAB T1 "
				+ "      where t1.Alpha_tab = TPend.DISCOUNT_TYPE_AT and T1.ALPHA_SUB_TAB=TPend.DISCOUNT_TYPE) DISCOUNT_TYPE_DESC, "
				+ " TPend.FEE_TYPE,(SELECT T1.ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB T1 where t1.Alpha_tab =  7033 and T1.ALPHA_SUB_TAB=TPend.FEE_TYPE )FEE_TYPE_DESC,"
				+ "TPend.TIER_TYPE,(SELECT T1.ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB T1 where t1.Alpha_tab =  7034 and T1.ALPHA_SUB_TAB=TPend.TIER_TYPE )TIER_TYPE_DESC,"
				+ "TPend.ACTIVITY_LINK_FLAG,TPend.CONCESSION_AMOUNT_CALC, "
				+ " TPend.Concession_Status,T3.NUM_SUBTAB_DESCRIPTION Concession_Status_DESC, "
				+ " TPend.RECORD_INDICATOR,T4.NUM_SUBTAB_DESCRIPTION RECORD_INDICATOR_DESC,"
				+ " TPend. MAKER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "
				+ getDbFunction("NVL") + "(TPend.MAKER,0) ) MAKER_NAME, "
				+ " TPend.VERIFIER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "
				+ getDbFunction("NVL") + "(TPend.VERIFIER,0) ) VERIFIER_NAME,  " + " "
				+ getDbFunction("DATEFUNC") + "(TPend.DATE_LAST_MODIFIED, 'dd-MM-yyyy "
				+ getDbFunction("TIME") + "') DATE_LAST_MODIFIED ," + getDbFunction("DATEFUNC")
				+ "(TPend.DATE_CREATION, 'dd-MM-yyyy " + getDbFunction("TIME") + "') DATE_CREATION "
				+ " FROM RA_MST_CONCESSION_HEADER_PEND TPend ,NUM_SUB_TAB T3,NUM_SUB_TAB T4     " + " Where  "
				+ "	 t3.NUM_tab = TPend.Concession_Status_NT  " + " and t3.NUM_sub_tab = TPend.Concession_Status  "
				+ " and t4.NUM_tab = TPend.RECORD_INDICATOR_NT  "
				+ " and t4.NUM_sub_tab = TPend.RECORD_INDICATOR And TPend.COUNTRY = ? "
				+ " and TPend.LE_BOOK = ? and TPend.CONCESSION_ID = ? " + " and TPend.Effective_Date = ? ");

		Object objParams[] = new Object[4];
		objParams[0] = new String(dObj.getCountry());// country
		objParams[1] = new String(dObj.getLeBook());
		objParams[2] = new String(dObj.getConcessionId());
		objParams[3] = new String(dObj.getEffectiveDate());
		try {
			if (intStatus == 0) {
				logger.info("Executing approved query");
				collTemp = getJdbcTemplate().query(strQueryAppr.toString(), objParams, getReviewMapper());
			} else {
				logger.info("Executing pending query");
				collTemp = getJdbcTemplate().query(strQueryPend.toString(), objParams, getReviewMapper());
			}
			return collTemp;
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("Error: getQueryResults Exception :   ");
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
		serviceName = "ConcessionConfigHeader";
		serviceDesc = "Concession Config Header";
		tableName = "RA_MST_CONCESSION_HEADER";
		childTableName = "RA_MST_CONCESSION_HEADER";
		intCurrentUserId = CustomContextHolder.getContext().getVisionId();
	}

	protected int doInsertionApprConcessionHeaders(ConcessionConfigHeaderVb vObject) {
		String query = " Insert Into RA_MST_CONCESSION_HEADER(COUNTRY,LE_BOOK,CONCESSION_ID,"
				+ " EFFECTIVE_DATE,END_DATE,CONCESSION_DESCRIPTION,CONCESSION_AMOUNT_CALC,DISCOUNT_TYPE,FEE_TYPE,TIER_TYPE,"
				+ "ACTIVITY_LINK_FLAG,	Concession_Status_NT, Concession_Status,"
				+ "RECORD_INDICATOR_NT,RECORD_INDICATOR,MAKER,VERIFIER,DATE_LAST_MODIFIED,DATE_CREATION) "
				+ " Values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?," + getDbFunction("SYSDATE") + ","
				+ getDbFunction("SYSDATE") + ")";

		Object[] args = { vObject.getCountry(), vObject.getLeBook(), vObject.getConcessionId(),
				vObject.getEffectiveDate(), vObject.getEndDate(), vObject.getConcessionDesc(),
				vObject.getConcessionAmountCalc(), vObject.getDiscountType(), vObject.getFeeType(),
				vObject.getTierType(), vObject.getActivityLinkFlag(), vObject.getConcessionStatusNt(),
				vObject.getConcessionStatus(), vObject.getRecordIndicatorNt(), vObject.getRecordIndicator(),
				vObject.getMaker(), vObject.getVerifier() };
		return getJdbcTemplate().update(query, args);
	}

	protected int doInsertionPendConcessionHeaders(ConcessionConfigHeaderVb vObject) {
		String query = " Insert Into RA_MST_CONCESSION_HEADER_PEND(COUNTRY,LE_BOOK,CONCESSION_ID,"
				+ " EFFECTIVE_DATE,END_DATE,CONCESSION_DESCRIPTION,CONCESSION_AMOUNT_CALC,DISCOUNT_TYPE,FEE_TYPE,TIER_TYPE,"
				+ "ACTIVITY_LINK_FLAG,	Concession_Status_NT, Concession_Status,"
				+ "RECORD_INDICATOR_NT,RECORD_INDICATOR,MAKER,VERIFIER,DATE_LAST_MODIFIED,DATE_CREATION) "
				+ " Values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?," + getDbFunction("SYSDATE") + ","
				+ getDbFunction("SYSDATE") + ")";

		Object[] args = { vObject.getCountry(), vObject.getLeBook(), vObject.getConcessionId(),
				vObject.getEffectiveDate(), vObject.getEndDate(), vObject.getConcessionDesc(),
				vObject.getConcessionAmountCalc(), vObject.getDiscountType(), vObject.getFeeType(),
				vObject.getTierType(), vObject.getActivityLinkFlag(), vObject.getConcessionStatusNt(),
				vObject.getConcessionStatus(), vObject.getRecordIndicatorNt(), vObject.getRecordIndicator(),
				vObject.getMaker(), vObject.getVerifier() };
		return getJdbcTemplate().update(query, args);
	}

	protected int doInsertionPendConcessionHeadersDc(ConcessionConfigHeaderVb vObject) {
		String query = "";
		if ("ORACLE".equalsIgnoreCase(databaseType)) {
			query = " Insert Into RA_MST_CONCESSION_HEADER_PEND(COUNTRY,LE_BOOK,CONCESSION_ID,"
					+ " EFFECTIVE_DATE,END_DATE,CONCESSION_DESCRIPTION,CONCESSION_AMOUNT_CALC,DISCOUNT_TYPE,FEE_TYPE,TIER_TYPE,"
					+ "ACTIVITY_LINK_FLAG,	Concession_Status_NT, Concession_Status,"
					+ "RECORD_INDICATOR_NT,RECORD_INDICATOR,MAKER,VERIFIER,DATE_LAST_MODIFIED,DATE_CREATION) "
					+ " Values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?," + getDbFunction("SYSDATE") + ","
					+ getDbFunction("SYSDATE") + ")";

		} else if ("MSSQL".equalsIgnoreCase(databaseType)) {

			query = " Insert Into RA_MST_CONCESSION_HEADER_PEND(COUNTRY,LE_BOOK,CONCESSION_ID,"
					+ " EFFECTIVE_DATE,END_DATE,CONCESSION_DESCRIPTION,CONCESSION_AMOUNT_CALC,DISCOUNT_TYPE,FEE_TYPE,TIER_TYPE,"
					+ "ACTIVITY_LINK_FLAG,	Concession_Status_NT, Concession_Status,"
					+ "RECORD_INDICATOR_NT,RECORD_INDICATOR,MAKER,VERIFIER,DATE_LAST_MODIFIED,DATE_CREATION) "
					+ " Values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?," + getDbFunction("SYSDATE")
					+ ", CONVERT(datetime, ?, 103))";
		}

		Object[] args = { vObject.getCountry(), vObject.getLeBook(), vObject.getConcessionId(),
				vObject.getEffectiveDate(), vObject.getEndDate(), vObject.getConcessionDesc(),
				vObject.getConcessionAmountCalc(), vObject.getDiscountType(), vObject.getFeeType(),
				vObject.getTierType(), vObject.getActivityLinkFlag(), vObject.getConcessionStatusNt(),
				vObject.getConcessionStatus(), vObject.getRecordIndicatorNt(), vObject.getRecordIndicator(),
				vObject.getMaker(), vObject.getVerifier(), vObject.getDateCreation() };

		return getJdbcTemplate().update(query, args);
	}

	protected int doUpdateApprHeader(ConcessionConfigHeaderVb vObject) {
		String query = " Update RA_MST_CONCESSION_HEADER set "
				+ " END_DATE=?,CONCESSION_DESCRIPTION=?,CONCESSION_AMOUNT_CALC=?, "
				+ " DISCOUNT_TYPE=? ,FEE_TYPE= ?,TIER_TYPE= ?,ACTIVITY_LINK_FLAG=?, "
				+ " Concession_Status= ? ,RECORD_INDICATOR= ? ,MAKER= ? ," + " VERIFIER= ? ,DATE_LAST_MODIFIED= "
				+ getDbFunction("SYSDATE")
				+ " ,DATE_CREATION = CONVERT(datetime, ?, 103)" 
				+ " WHERE COUNTRY= ? AND LE_BOOK= ? AND CONCESSION_ID = ?"
				+ " AND EFFECTIVE_DATE= ?";
		Object[] args = { vObject.getEndDate(), vObject.getConcessionDesc(), vObject.getConcessionAmountCalc(),
				vObject.getDiscountType(), vObject.getFeeType(), vObject.getTierType(), vObject.getActivityLinkFlag(),
				vObject.getConcessionStatus(), vObject.getRecordIndicator(), vObject.getMaker(), vObject.getVerifier(),
				vObject.getDateCreation(), vObject.getCountry(), vObject.getLeBook(), vObject.getConcessionId(), vObject.getEffectiveDate() };
		return getJdbcTemplate().update(query, args);
	}

	protected int doUpdatePendHeader(ConcessionConfigHeaderVb vObject) {
		String query = " Update RA_MST_CONCESSION_HEADER_PEND set "
				+ " END_DATE=?,CONCESSION_DESCRIPTION=?,CONCESSION_AMOUNT_CALC=?, "
				+ " DISCOUNT_TYPE=? ,FEE_TYPE= ?,TIER_TYPE= ?,ACTIVITY_LINK_FLAG=?, "
				+ " Concession_Status= ? ,RECORD_INDICATOR= ? ,MAKER= ? ," + " VERIFIER= ? ,DATE_LAST_MODIFIED= "
				+ getDbFunction("SYSDATE") + ",DATE_CREATION = CONVERT(datetime, ?, 103)" + " WHERE COUNTRY= ? AND LE_BOOK= ? AND CONCESSION_ID = ?"
				+ " AND EFFECTIVE_DATE= ?";
		Object[] args = { vObject.getEndDate(), vObject.getConcessionDesc(), vObject.getConcessionAmountCalc(),
				vObject.getDiscountType(), vObject.getFeeType(), vObject.getTierType(), vObject.getActivityLinkFlag(),
				vObject.getConcessionStatus(), vObject.getRecordIndicator(), vObject.getMaker(), vObject.getVerifier(),
				vObject.getDateCreation(),vObject.getCountry(), vObject.getLeBook(), vObject.getConcessionId(), vObject.getEffectiveDate() };
		return getJdbcTemplate().update(query, args);
	}

	protected int deleteConcessionHeaderAppr(ConcessionConfigHeaderVb vObject) {
		String query = "Delete from RA_MST_CONCESSION_HEADER  WHERE COUNTRY= ? AND LE_BOOK= ?  AND CONCESSION_ID = ?"
				+ " AND  EFFECTIVE_DATE= ?";
		Object[] args = { vObject.getCountry(), vObject.getLeBook(), vObject.getConcessionId(),
				vObject.getEffectiveDate() };
		return getJdbcTemplate().update(query, args);

	}

	protected int deleteConcessionHeaderPend(ConcessionConfigHeaderVb vObject) {
		String query = "Delete from RA_MST_CONCESSION_HEADER_PEND  WHERE COUNTRY= ? AND LE_BOOK= ?  AND CONCESSION_ID = ?"
				+ " AND EFFECTIVE_DATE= ?";
		Object[] args = { vObject.getCountry(), vObject.getLeBook(), vObject.getConcessionId(),
				vObject.getEffectiveDate() };
		return getJdbcTemplate().update(query, args);

	}

	@Override
	protected List<ConcessionConfigHeaderVb> selectApprovedRecord(ConcessionConfigHeaderVb vObject) {
		return getQueryResults(vObject, Constants.STATUS_ZERO);
	}

	@Override
	public List<ConcessionConfigHeaderVb> doSelectPendingRecord(ConcessionConfigHeaderVb vObject) {
		return getQueryResults(vObject, Constants.STATUS_PENDING);
	}

	@Override
	protected int getStatus(ConcessionConfigHeaderVb records) {
		return records.getConcessionStatus();
	}

	@Override
	protected void setStatus(ConcessionConfigHeaderVb vObject, int status) {
		vObject.setConcessionStatus(status);
	}

	@Override
	public ExceptionCode doInsertApprRecordForNonTrans(ConcessionConfigHeaderVb vObject) throws RuntimeCustomException {
		List<ConcessionConfigHeaderVb> collTemp = null;
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
		vObject.setConcessionStatus(Constants.STATUS_ZERO);
		vObject.setMaker(intCurrentUserId);
		vObject.setVerifier(intCurrentUserId);

		retVal = doInsertionApprConcessionHeaders(vObject);
		if (retVal != Constants.SUCCESSFUL_OPERATION) {
			exceptionCode = getResultObject(retVal);
			throw buildRuntimeCustomException(exceptionCode);
		} else {
			exceptionCode = getResultObject(Constants.SUCCESSFUL_OPERATION);
		}
		if (vObject.getConcessionConfigDetaillst() != null && vObject.getConcessionConfigDetaillst().size() > 0) {
			exceptionCode = concessionConfigDetailsDao.deleteAndInsertApprConcessionDetail(vObject);
		}
		if (exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION) {
			exceptionCode = getResultObject(retVal);
			throw buildRuntimeCustomException(exceptionCode);
		} else {
			exceptionCode = getResultObject(Constants.SUCCESSFUL_OPERATION);
		}
		if (vObject.getConcessionFilterlst() != null && vObject.getConcessionFilterlst().size() > 0) {
			exceptionCode = concessionFilterDao.deleteAndInsertApprFilter(vObject);
		}
		if("Y".equalsIgnoreCase(vObject.getActivityLinkFlag())) {
		if(vObject.getConcessionActivitylst() != null && vObject.getConcessionActivitylst().size() > 0) {
			exceptionCode= deleteAndInsertApprActivityLink(vObject);
		}}
		exceptionCode = getResultObject(Constants.SUCCESSFUL_OPERATION);
		writeAuditLog(vObject, null);
		if (exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION) {
			exceptionCode = getResultObject(Constants.AUDIT_TRAIL_ERROR);
			throw buildRuntimeCustomException(exceptionCode);
		}
		return exceptionCode;
	}

	@Override
	public ExceptionCode doInsertRecordForNonTrans(ConcessionConfigHeaderVb vObject) throws RuntimeCustomException {
		List<ConcessionConfigHeaderVb> collTemp = null;
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
		vObject.setConcessionStatus(Constants.STATUS_ZERO);
		vObject.setMaker(intCurrentUserId);
		vObject.setVerifier(intCurrentUserId);
		retVal = doInsertionPendConcessionHeaders(vObject);
		if (retVal != Constants.SUCCESSFUL_OPERATION) {
			exceptionCode = getResultObject(retVal);
			throw buildRuntimeCustomException(exceptionCode);
		} else {
			exceptionCode = getResultObject(Constants.SUCCESSFUL_OPERATION);
		}
		if (vObject.getConcessionConfigDetaillst() != null && vObject.getConcessionConfigDetaillst().size() > 0) {
			exceptionCode = concessionConfigDetailsDao.deleteAndInsertPendConcessionDetail(vObject);
		}
		if (exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION) {
			exceptionCode = getResultObject(retVal);
			throw buildRuntimeCustomException(exceptionCode);
		} else {
			exceptionCode = getResultObject(Constants.SUCCESSFUL_OPERATION);
		}
		if (vObject.getConcessionFilterlst() != null && vObject.getConcessionFilterlst().size() > 0) {
			exceptionCode = concessionFilterDao.deleteAndInsertPendFilter(vObject);
		}
		if("Y".equalsIgnoreCase(vObject.getActivityLinkFlag())) {
		if(vObject.getConcessionActivitylst() != null && vObject.getConcessionActivitylst().size() > 0) {
			exceptionCode = deleteAndInsertPendActivityLink(vObject);
		}
		}
		exceptionCode = getResultObject(Constants.SUCCESSFUL_OPERATION);
		writeAuditLog(vObject, null);
		exceptionCode = getResultObject(Constants.SUCCESSFUL_OPERATION);
		return exceptionCode;
	}

	@Override
	public ExceptionCode doUpdateApprRecordForNonTrans(ConcessionConfigHeaderVb vObject) throws RuntimeCustomException {
		List<ConcessionConfigHeaderVb> collTemp = null;
		ConcessionConfigHeaderVb vObjectlocal = null;
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
		vObjectlocal = ((ArrayList<ConcessionConfigHeaderVb>) collTemp).get(0);
		// Even if record is not there in Appr. table reject the record
		if (collTemp.size() == 0) {
			exceptionCode = getResultObject(Constants.ATTEMPT_TO_MODIFY_UNEXISTING_RECORD);
			throw buildRuntimeCustomException(exceptionCode);
		}
		vObject.setRecordIndicator(Constants.STATUS_ZERO);
		vObject.setVerifier(getIntCurrentUserId());
		vObject.setDateCreation(vObjectlocal.getDateCreation());
		retVal = doUpdateApprHeader(vObject);
		if (retVal != Constants.SUCCESSFUL_OPERATION) {
			exceptionCode = getResultObject(retVal);
			throw buildRuntimeCustomException(exceptionCode);
		} else {
			exceptionCode = getResultObject(Constants.SUCCESSFUL_OPERATION);
		}
		if (vObject.getConcessionConfigDetaillst() != null && vObject.getConcessionConfigDetaillst().size() > 0) {
			exceptionCode = concessionConfigDetailsDao.deleteAndInsertApprConcessionDetail(vObject);
		}
		
		if (vObject.getConcessionFilterlst() != null && vObject.getConcessionFilterlst().size() > 0) {
			exceptionCode = concessionFilterDao.deleteAndInsertApprFilter(vObject);
		}
		if("Y".equalsIgnoreCase(vObject.getActivityLinkFlag())) {
		if(vObject.getConcessionActivitylst() != null && vObject.getConcessionActivitylst().size() > 0) {
			exceptionCode = deleteAndInsertApprActivityLink(vObject);
		}
		}
		if (exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION) {
			exceptionCode = getResultObject(retVal);
			throw buildRuntimeCustomException(exceptionCode);
		} else {
			exceptionCode = getResultObject(Constants.SUCCESSFUL_OPERATION);
		}
		String systemDate = getSystemDate();
		vObject.setDateLastModified(systemDate);

		writeAuditLog(vObject, vObjectlocal);
		if (exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION) {
			exceptionCode = getResultObject(Constants.AUDIT_TRAIL_ERROR);
			throw buildRuntimeCustomException(exceptionCode);
		}
		exceptionCode = getResultObject(Constants.SUCCESSFUL_OPERATION);
		return exceptionCode;
	}

	@Override
	public ExceptionCode doUpdateRecordForNonTrans(ConcessionConfigHeaderVb vObject) throws RuntimeCustomException {
		List<ConcessionConfigHeaderVb> collTemp = null;
		ConcessionConfigHeaderVb vObjectlocal = null;
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
			vObjectlocal = ((ArrayList<ConcessionConfigHeaderVb>) collTemp).get(0);
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
			if (vObject.getConcessionConfigDetaillst() != null && vObject.getConcessionConfigDetaillst().size() > 0) {
				concessionConfigDetailsDao.deleteAndInsertPendConcessionDetail(vObject);
			}
			if (vObject.getConcessionFilterlst() != null && vObject.getConcessionFilterlst().size() > 0) {
				concessionFilterDao.deleteAndInsertPendFilter(vObject);
			}
			if("Y".equalsIgnoreCase(vObject.getActivityLinkFlag())) {
			if(vObject.getConcessionActivitylst() != null && vObject.getConcessionActivitylst().size() > 0) {
				deleteAndInsertPendActivityLink(vObject);
			}
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
				vObjectlocal = ((ArrayList<ConcessionConfigHeaderVb>) collTemp).get(0);
				vObject.setDateCreation(vObjectlocal.getDateCreation());
			}
			vObject.setDateCreation(vObjectlocal.getDateCreation());
			// Record is there in approved, but not in pending. So add it to pending
			vObject.setVerifier(0);
			vObject.setRecordIndicator(Constants.STATUS_UPDATE);
			retVal = doInsertionPendConcessionHeadersDc(vObject);
			if (retVal != Constants.SUCCESSFUL_OPERATION) {
				exceptionCode = getResultObject(retVal);
				throw buildRuntimeCustomException(exceptionCode);
			}
			if (vObject.getConcessionConfigDetaillst() != null && vObject.getConcessionConfigDetaillst().size() > 0) {
				concessionConfigDetailsDao.deleteAndInsertPendConcessionDetail(vObject);
			}
			if (vObject.getConcessionFilterlst() != null && vObject.getConcessionFilterlst().size() > 0) {
				concessionFilterDao.deleteAndInsertPendFilter(vObject);
			}
			if("Y".equalsIgnoreCase(vObject.getActivityLinkFlag())) {
			if(vObject.getConcessionActivitylst() != null && vObject.getConcessionActivitylst().size() > 0) {
				deleteAndInsertPendActivityLink(vObject);
			}
			}
			writeAuditLog(vObject, null);
			return getResultObject(Constants.SUCCESSFUL_OPERATION);
		}
	}

	@Override
	@Transactional(rollbackForClassName = { "com.vision.exception.RuntimeCustomException" })
	public ExceptionCode doRejectForTransaction(ConcessionConfigHeaderVb vObject) throws RuntimeCustomException {
		strErrorDesc = "";
		strCurrentOperation = Constants.APPROVE;
		setServiceDefaults();
		return doRejectRecord(vObject);
	}

	@Override
	public ExceptionCode doRejectRecord(ConcessionConfigHeaderVb vObject) throws RuntimeCustomException {
		ConcessionConfigHeaderVb vObjectlocal = null;
		List<ConcessionConfigHeaderVb> collTemp = null;
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
			vObjectlocal = ((ArrayList<ConcessionConfigHeaderVb>) collTemp).get(0);
			retVal = deleteConcessionHeaderPend(vObject);

			List<ConcessionConfigDetailsVb> collTempDet = null;
			collTempDet = concessionConfigDetailsDao.getConcessionConfigDetails(vObject, 1);
			if (collTempDet != null && collTempDet.size() > 0) {
				retVal = concessionConfigDetailsDao.deleteConcessionDetailsPend(vObject);
			}
			List<ConcessionConfigTierVb> collTempTier = null;
			collTempTier = concessionConfigTierDao.getConcessionConfigTierByGroup(vObject, 1);
			if (collTempTier != null && collTempTier.size() > 0) {
				int delCnt = concessionConfigTierDao.deleteConcessionTierPend(vObject);
			}

			List<ConcessionFilterVb> collTempFilter = null;
			collTempFilter = concessionFilterDao.getConcessionFilters(vObject, 1);
			if (collTempFilter != null && collTempFilter.size() > 0) {
				retVal = concessionFilterDao.deleteConcessionFilterPend(vObject);
			}
			if("Y".equalsIgnoreCase(vObject.getActivityLinkFlag())) {
			List<ConcessionConfigHeaderVb> collTempActLink = null;
			collTempActLink = getQueryActivityLink(vObject, 1);
			if(collTempActLink != null && collTempActLink.size() > 0) {
				deleteConcessionActivityLinkPend(vObject);
			}
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
	public ExceptionCode doApproveForTransaction(ConcessionConfigHeaderVb vObject, boolean staticDelete)
			throws RuntimeCustomException {
		strErrorDesc = "";
		strCurrentOperation = Constants.APPROVE;
		setServiceDefaults();
		return doApproveRecord(vObject, staticDelete);
	}

	@Override
	public ExceptionCode doApproveRecord(ConcessionConfigHeaderVb vObject, boolean staticDelete)
			throws RuntimeCustomException {
		ConcessionConfigHeaderVb oldContents = null;
		ConcessionConfigDetailsVb oldContentsGl = null;
		ConcessionConfigTierVb oldContentsSbu = null;
		ConcessionFilterVb oldContentsFilter = null;
		ConcessionConfigHeaderVb oldContentsAct = null;

		ConcessionConfigHeaderVb vObjectlocal = null;
		ConcessionConfigDetailsVb vObjectGllocal = null;
		ConcessionConfigTierVb vObjectSbulocal = null;
		ConcessionFilterVb vObjectFilterlocal = null;
		ConcessionConfigHeaderVb vObjectAct = null;

		List<ConcessionConfigHeaderVb> collTemp = null;
		List<ConcessionConfigDetailsVb> collTempDet = null;
		List<ConcessionConfigDetailsVb> collTempDetAppr = null;

		List<ConcessionFilterVb> collTempFilter = null;
		List<ConcessionFilterVb> collTempFilterAppr = null;

		List<ConcessionConfigTierVb> collTempTier = null;
		List<ConcessionConfigTierVb> collTempTierAppr = null;
		
	    List<ConcessionConfigHeaderVb> collTempActLink = null;
	    List<ConcessionConfigHeaderVb> collTempActLinkAppr = null;
		  
		ExceptionCode exceptionCode = null;
		strCurrentOperation = Constants.APPROVE;
		setServiceDefaults();
		try {
			// See if such a pending request exists in the pending table
			vObject.setVerifier(getIntCurrentUserId());
			vObject.setRecordIndicator(Constants.STATUS_ZERO);

			// When trying to approve business line record, Check Trans line is approved or
			// not
			/*
			 * TransLineHeaderVb transLineHeaderVb=new TransLineHeaderVb();
			 * BusinessLineHeaderVb businessLineHeaderVb=new BusinessLineHeaderVb();
			 * transLineHeaderVb.setCountry(vObject.getCountry());
			 * transLineHeaderVb.setLeBook(vObject.getLeBook());
			 * transLineHeaderVb.setTransLineId(vObject.getTransLineId()); collTempTL
			 * =doselectPendingproductRecord(transLineHeaderVb);
			 * 
			 * businessLineHeaderVb.setCountry(vObject.getCountry());
			 * businessLineHeaderVb.setLeBook(vObject.getLeBook());
			 * businessLineHeaderVb.setConcessionId(vObject.getConcessionId()); collTempBL =
			 * doSelectPendingBusinessLineRecord(businessLineHeaderVb); if(collTempTL!=null
			 * && collTempTL.size()>0 ){ exceptionCode =
			 * getResultObject(Constants.ERRONEOUS_OPERATION);
			 * exceptionCode.setErrorMsg("Trans Line is in Add Pending - Kindly Approve");
			 * exceptionCode.setResponse(vObject); throw
			 * buildRuntimeCustomException(exceptionCode); }else if(collTempBL!=null &&
			 * collTempBL.size()>0) { exceptionCode =
			 * getResultObject(Constants.ERRONEOUS_OPERATION);
			 * exceptionCode.setErrorMsg("Business Line is in Add Pending - Kindly Approve"
			 * ); exceptionCode.setResponse(vObject); throw
			 * buildRuntimeCustomException(exceptionCode); }else {
			 */
			collTemp = doSelectPendingRecord(vObject);
			if (collTemp == null) {
				exceptionCode = getResultObject(Constants.ERRONEOUS_OPERATION);
				throw buildRuntimeCustomException(exceptionCode);
			}

			if (collTemp.size() == 0) {
				exceptionCode = getResultObject(Constants.NO_SUCH_PENDING_RECORD);
				throw buildRuntimeCustomException(exceptionCode);
			}

			vObjectlocal = ((ArrayList<ConcessionConfigHeaderVb>) collTemp).get(0);

			if (vObjectlocal.getMaker() == getIntCurrentUserId()) {
				exceptionCode = getResultObject(Constants.MAKER_CANNOT_APPROVE);
				throw buildRuntimeCustomException(exceptionCode);
			}
			
			if (vObject.getRecordIndicator() == 1) {

				String concessionHeader = getconcessionHeaderPend(vObject.getCountry(), vObject.getLeBook(), vObject.getConcessionId());
				vObjectlocal.setCountry(vObject.getCountry());
				vObjectlocal.setLeBook(vObject.getLeBook());
				vObjectlocal.setConcessionId(vObject.getConcessionId());

				collTemp = doSelectPendingRecord(vObjectlocal);

				if (collTemp != null || collTemp.size() > 0) {
					exceptionCode = getResultObject(Constants.ERRONEOUS_OPERATION);
					exceptionCode.setErrorMsg("Concession header/Concession header is in pending.Kindly Approve");
					throw buildRuntimeCustomException(exceptionCode);
				}
			} else {

			collTempDet = concessionConfigDetailsDao.getConcessionConfigDetails(vObjectlocal, 1);
			if (collTempDet != null && collTempDet.size() > 0) {
				vObjectGllocal = ((ArrayList<ConcessionConfigDetailsVb>) collTempDet).get(0);
			}

			collTempTier = concessionConfigTierDao.getConcessionConfigTierByGroup(vObjectlocal, 1); // SBU
			if (collTempTier != null && collTempTier.size() > 0) {
				vObjectSbulocal = ((ArrayList<ConcessionConfigTierVb>) collTempTier).get(0);
			}

			collTempFilter = concessionFilterDao.getConcessionFilters(vObjectlocal, 1);
			if (collTempFilter != null && collTempFilter.size() > 0) {
				vObjectFilterlocal = ((ArrayList<ConcessionFilterVb>) collTempFilter).get(0);
			}
			
			collTempActLink = getQueryActivityLink(vObjectlocal, 1);
			if(collTempActLink != null && collTempActLink.size() > 0) {
				vObjectAct = ((ArrayList<ConcessionConfigHeaderVb>) collTempActLink).get(0);
			}
			
			
			// If it's NOT addition, collect the existing record contents from the
			// Approved table and keep it aside, for writing audit information later.
			if (vObjectlocal.getRecordIndicator() != Constants.STATUS_INSERT) {
				collTemp = selectApprovedRecord(vObject);
				if (collTemp == null || collTemp.isEmpty()) {
					exceptionCode = getResultObject(Constants.ERRONEOUS_OPERATION);
					throw buildRuntimeCustomException(exceptionCode);
				}
				oldContents = ((ArrayList<ConcessionConfigHeaderVb>) collTemp).get(0);

				collTempDetAppr = concessionConfigDetailsDao.getConcessionConfigDetails(vObjectlocal, 0);
				if (collTempDetAppr != null && collTempDetAppr.size() > 0) {
					oldContentsGl = ((ArrayList<ConcessionConfigDetailsVb>) collTempDetAppr).get(0);
				}

				collTempTierAppr = concessionConfigTierDao.getConcessionConfigTierByGroup(vObjectlocal, 0);
				if (collTempTierAppr != null && collTempTierAppr.size() > 0) {
					oldContentsSbu = ((ArrayList<ConcessionConfigTierVb>) collTempTierAppr).get(0);
				}

				collTempFilterAppr = concessionFilterDao.getConcessionFilters(vObjectlocal, 0);
				if (collTempFilterAppr != null && collTempFilterAppr.size() > 0) {
					oldContentsFilter = ((ArrayList<ConcessionFilterVb>) collTempFilterAppr).get(0);
				}
			
				collTempActLinkAppr = getQueryActivityLink(vObjectlocal, 0);
				if(collTempActLinkAppr != null && collTempActLinkAppr.size() > 0) {
					oldContentsAct = ((ArrayList<ConcessionConfigHeaderVb>) collTempActLinkAppr).get(0);
				
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
				if (collTempDetAppr != null && collTempDetAppr.size() > 0) {
					concessionConfigDetailsDao.deleteConcessionDetailsAppr(vObjectlocal);
				}
				if (collTempDet != null && !collTempDet.isEmpty()) {
					collTempDet.forEach(glPend -> {
						glPend.setRecordIndicator(vObject.getRecordIndicator());
						retVal = concessionConfigDetailsDao.doInsertionApprConcessionDetails(glPend);
					});
				}

				if (collTempTierAppr != null && collTempTierAppr.size() > 0) {
					concessionConfigTierDao.deleteConcessionTierAppr(vObjectlocal);
				}
				if (collTempTier != null && !collTempTier.isEmpty()) {
					collTempTier.forEach(conTier -> {
						conTier.setRecordIndicator(vObject.getRecordIndicator());
						retVal = concessionConfigTierDao.doInsertionApprConcessionTier(conTier);
					});
				}
		
				if (collTempFilterAppr != null && collTempFilterAppr.size() > 0) {
					concessionFilterDao.deleteConcessionFilterAppr(vObjectlocal);
				}
				if (collTempFilter != null && !collTempFilter.isEmpty()) {
					collTempFilter.forEach(glPend -> {
						glPend.setRecordIndicator(vObject.getRecordIndicator());
						retVal = concessionFilterDao.doInsertionApprConcessionFilter(glPend);
					});
				}
				
				if("Y".equalsIgnoreCase(vObjectlocal.getActivityLinkFlag())) {
					if(collTempActLinkAppr != null && collTempActLinkAppr.size() > 0) {
						deleteConcessionActivityLinkAppr(vObjectlocal);
					}
					if(collTempActLink != null && collTempActLink.size() > 0) {
						collTempActLink.forEach(Actlink -> {
							Actlink.setRecordIndicator(vObject.getRecordIndicator());
							retVal = doInsertionApprConcessionActivityLink(Actlink);
						});
					}
				}
				String systemDate = getSystemDate();
				vObject.setDateLastModified(systemDate);
				vObject.setDateCreation(systemDate);
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
					retVal = doUpdateApprHeader(vObjectlocal);
					if (retVal != Constants.SUCCESSFUL_OPERATION) {
						exceptionCode = getResultObject(retVal);
						throw buildRuntimeCustomException(exceptionCode);
					}
				}
				if (collTempTierAppr != null && collTempTierAppr.size() > 0) {
					concessionConfigTierDao.deleteConcessionTierAppr(vObjectlocal);
				}
				if (collTempTier != null && !collTempTier.isEmpty()) {
					collTempTier.forEach(sbuPend -> {
						sbuPend.setRecordIndicator(vObject.getRecordIndicator());
						retVal = concessionConfigTierDao.doInsertionApprConcessionTier(sbuPend);
					});
				}
				if (collTempDetAppr != null && collTempDetAppr.size() > 0) {
					concessionConfigDetailsDao.deleteConcessionDetailsAppr(vObjectlocal);
				}
				if (collTempDet != null && !collTempDet.isEmpty()) {
					collTempDet.forEach(glPend -> {
						glPend.setRecordIndicator(vObject.getRecordIndicator());
						retVal = concessionConfigDetailsDao.doInsertionApprConcessionDetails(glPend);
					});
				}

				if (collTempFilterAppr != null && collTempFilterAppr.size() > 0) {
					concessionFilterDao.deleteConcessionFilterAppr(vObjectlocal);
				}
				if (collTempFilter != null && !collTempFilter.isEmpty()) {
					collTempFilter.forEach(glPend -> {
						glPend.setRecordIndicator(vObject.getRecordIndicator());
						retVal = concessionFilterDao.doInsertionApprConcessionFilter(glPend);
					});
				}
				
				if("Y".equalsIgnoreCase(vObjectlocal.getActivityLinkFlag())) {
					if(collTempActLinkAppr != null && collTempActLinkAppr.size() > 0) {
						deleteConcessionActivityLinkAppr(vObjectlocal);					}
				}
				if(collTempActLink != null && collTempActLink.size() > 0) {
					collTempActLink.forEach(ActLink ->{
						ActLink.setRecordIndicator(vObject.getRecordIndicator());
						retVal = doInsertionApprConcessionActivityLink(ActLink);
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

				}
			} else {
				exceptionCode = getResultObject(Constants.INVALID_STATUS_FLAG_IN_DATABASE);
				throw buildRuntimeCustomException(exceptionCode);
			}

			// Delete the record from the Pending table
			retVal = deleteConcessionHeaderPend(vObjectlocal);
			if (retVal != Constants.SUCCESSFUL_OPERATION) {
				exceptionCode = getResultObject(retVal);
				throw buildRuntimeCustomException(exceptionCode);
			}
			retVal = concessionConfigTierDao.deleteConcessionTierPend(vObjectlocal);
			retVal = concessionConfigDetailsDao.deleteConcessionDetailsPend(vObjectlocal);
			retVal = concessionFilterDao.deleteConcessionFilterPend(vObjectlocal);
            retVal = deleteConcessionActivityLinkPend(vObjectlocal);
			// Set the internal status to Approved
			vObject.setInternalStatus(0);
			vObject.setRecordIndicator(Constants.STATUS_ZERO);
			if (vObjectlocal.getRecordIndicator() == Constants.STATUS_DELETE && !staticDelete) {
				writeAuditLog(null, oldContents);
				vObject.setRecordIndicator(-1);
			} else
				writeAuditLog(vObjectlocal, oldContents);
			}
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
	protected String getAuditString(ConcessionConfigHeaderVb vObject) {
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

			if (ValidationUtil.isValid(vObject.getEffectiveDate()))
				strAudit.append("EFFECTIVE_DATE" + auditDelimiterColVal + vObject.getEffectiveDate().trim());
			else
				strAudit.append("EFFECTIVE_DATE" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);

			if (ValidationUtil.isValid(vObject.getFeeType()))
				strAudit.append("FEE_TYPE" + auditDelimiterColVal + vObject.getFeeType().trim());
			else
				strAudit.append("FEE_TYPE" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);

			if (ValidationUtil.isValid(vObject.getTierType()))
				strAudit.append("TIER_TYPE" + auditDelimiterColVal + vObject.getTierType().trim());
			else
				strAudit.append("TIER_TYPE" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);

			strAudit.append("CONCESSION_STATUS_NT" + auditDelimiterColVal + vObject.getConcessionStatusNt());
			strAudit.append(auditDelimiter);

			strAudit.append("CONCESSION_STATUS" + auditDelimiterColVal + vObject.getConcessionStatus());
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

	public int effectiveDateCheck(ConcessionConfigHeaderVb dObj, int status) {
		int cnt = 0;
		String query = "";
		try {
			if (status == 0) {
				query = " SELECT COUNT(1) FROM RA_MST_CONCESSION_HEADER TAPPR "
						+ " WHERE TAPPR.COUNTRY = ? AND TAPPR.LE_BOOK = ? AND "
						+ " TAPPR.CONCESSION_ID = ?  AND TAPPR.EFFECTIVE_DATE = ? ";
			} else {
				query = " SELECT COUNT(1) FROM RA_MST_CONCESSION_HEADER_PEND TAPPR "
						+ " WHERE TAPPR.COUNTRY = ? AND TAPPR.LE_BOOK = ? AND  "
						+ " TAPPR.CONCESSION_ID = ?  AND TAPPR.EFFECTIVE_DATE = ? ";
			}

			Object objParams[] = new Object[4];
			objParams[0] = new String(dObj.getCountry());// country
			objParams[1] = new String(dObj.getLeBook());
			objParams[2] = new String(dObj.getConcessionId());
			objParams[3] = new String(dObj.getEffectiveDate());
			cnt = getJdbcTemplate().queryForObject(query, objParams, Integer.class);
			return cnt;
		} catch (Exception e) {
			return cnt;
		}
	}

	public int effectiveDateBusinessCheck(ConcessionConfigHeaderVb dObj) {
		int cnt = 0;
		String query = "";
		try {
			// query = " SELECT CASE WHEN ? >= BUSINESS_DATE THEN 1 ELSE 0 END
			// EFFECTIVE_DATE FROM VISION_BUSINESS_DAY WHERE COUNTRY = ? AND LE_BOOK = ? ";
			query = " SELECT CASE WHEN ? >= REPORT_BUSINESS_DATE THEN 1 ELSE 0 END EFFECTIVE_DATE FROM VISION_BUSINESS_DAY WHERE COUNTRY = ? AND LE_BOOK = ? ";

			Object objParams[] = new Object[3];
			objParams[0] = new String(dObj.getEffectiveDate());// country
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
				logger.error("Error on Line Merge[" + exceptionCode.getErrorMsg() + "]");
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
			collTemp = getJdbcTemplate().query(strQueryPend.toString(), objParams, getDetailMapper1());
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

	protected RowMapper getDetailMapper1() {
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

	protected RowMapper getDetailMapperTrans() {
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
			collTemp = getJdbcTemplate().query(strQueryPend.toString(), objParams, getDetailMapperTrans());
			return collTemp;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}

	}

	@Override
	protected ExceptionCode doDeleteRecordForNonTrans(ConcessionConfigHeaderVb vObject) throws RuntimeCustomException {
		ConcessionConfigHeaderVb vObjectlocal = null;
		List<ConcessionConfigHeaderVb> collTemp = null;
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
			vObjectlocal = ((ArrayList<ConcessionConfigHeaderVb>) collTemp).get(0);
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
			vObjectlocal = ((ArrayList<ConcessionConfigHeaderVb>) collTemp).get(0);
			vObjectlocal.setDateCreation(vObject.getDateCreation());
		}
		// vObjectlocal.setDateCreation(vObject.getDateCreation());
		vObjectlocal.setMaker(getIntCurrentUserId());
		vObjectlocal.setRecordIndicator(Constants.STATUS_DELETE);
		vObjectlocal.setVerifier(0);
//		retVal = doInsertionPendWithDc(vObjectlocal);
		retVal = doInsertionPendConcessionHeadersDc(vObjectlocal);
		// Fee Detail
		vObject.setRecordIndicator(Constants.STATUS_DELETE);
		vObject.setVerifier(0);
		

		List<ConcessionConfigDetailsVb> collTempFeeDet = null;
		collTempFeeDet = concessionConfigDetailsDao.getConcessionConfigDetails(vObjectlocal, 0);
		if (collTempFeeDet != null && collTempFeeDet.size() > 0) {
			collTempFeeDet.forEach(conDet ->{
				conDet.setRecordIndicator(vObject.getRecordIndicator());
				retVal =concessionConfigDetailsDao.doInsertionPendConcessionDetails(conDet);
			});
		}
		
		List<ConcessionConfigTierVb> collTempTierDet = null;
		collTempTierDet = concessionConfigTierDao.getConcessionConfigTierByGroup(vObjectlocal, 0);
		if (collTempTierDet != null && collTempTierDet.size() > 0) {
			 concessionConfigTierDao.deleteConcessionTierPend(vObjectlocal);
				
			 collTempTierDet.forEach(conTier -> {
						conTier.setRecordIndicator(vObject.getRecordIndicator());
						retVal = concessionConfigTierDao.doInsertionPendConcessionTier(conTier);
					});
		}

		List<ConcessionFilterVb> collTempFilter = null;
		collTempFilter = concessionFilterDao.getConcessionFilters(vObject, 0);
		if (collTempFilter != null && collTempFilter.size() > 0) {
			vObjectlocal.setConcessionFilterlst(collTempFilter);
			exceptionCode = concessionFilterDao.deleteAndInsertPendFilter(vObjectlocal);
		}
		if("Y".equalsIgnoreCase(vObjectlocal.getActivityLinkFlag())) {
			List<ConcessionConfigHeaderVb> collTempActLink= null;
			collTempActLink =getQueryActivityLink(vObject, 0);
			if(collTempActLink != null && collTempActLink.size() > 0) {
				collTempActLink.forEach(conAct -> {
					retVal = doInsertionConcessionActivityLinkPend(conAct);
				});
			}
			}
		
		if (exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION) {
			exceptionCode = getResultObject(retVal);
			throw buildRuntimeCustomException(exceptionCode);
		}
	
		return getResultObject(Constants.SUCCESSFUL_OPERATION);
	}

	@Override
	protected ExceptionCode doDeleteApprRecordForNonTrans(ConcessionConfigHeaderVb vObject)
			throws RuntimeCustomException {
		List<ConcessionConfigHeaderVb> collTemp = null;
		ExceptionCode exceptionCode = null;
		strApproveOperation = Constants.DELETE;
		strErrorDesc = "";
		strCurrentOperation = Constants.DELETE;
		setServiceDefaults();
		ConcessionConfigHeaderVb vObjectlocal = null;
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
			int intStaticDeletionFlag = getStatus(((ArrayList<ConcessionConfigHeaderVb>) collTemp).get(0));
			if (intStaticDeletionFlag == Constants.PASSIVATE) {
				exceptionCode = getResultObject(Constants.CANNOT_DELETE_AN_INACTIVE_RECORD);
				throw buildRuntimeCustomException(exceptionCode);
			}
		} else {
			exceptionCode = getResultObject(Constants.ATTEMPT_TO_DELETE_UNEXISTING_RECORD);
			throw buildRuntimeCustomException(exceptionCode);
		}
		vObjectlocal = ((ArrayList<ConcessionConfigHeaderVb>) collTemp).get(0);
		vObject.setDateCreation(vObjectlocal.getDateCreation());
		if (vObject.isStaticDelete()) {
			vObjectlocal.setMaker(getIntCurrentUserId());
			vObject.setVerifier(getIntCurrentUserId());
			vObject.setRecordIndicator(Constants.STATUS_ZERO);
//			setStatus(vObject, Constants.PASSIVATE);
			setStatus(vObjectlocal, Constants.PASSIVATE);
			vObjectlocal.setVerifier(getIntCurrentUserId());
			vObjectlocal.setRecordIndicator(Constants.STATUS_ZERO);
			retVal = doUpdateApprHeader(vObjectlocal);
			List<ConcessionConfigDetailsVb> collTempFeeDet = null;
			collTempFeeDet = concessionConfigDetailsDao.getConcessionConfigDetails(vObject, 1);
			if (collTempFeeDet != null && collTempFeeDet.size() > 0) {
				exceptionCode = concessionConfigDetailsDao.deleteAndInsertApprConcessionDetail(vObjectlocal);
			}
			List<ConcessionFilterVb> collTempFilterDet = null;
			collTempFilterDet = concessionFilterDao.getConcessionFilters(vObject, 1);
			if (collTempFilterDet != null && collTempFilterDet.size() > 0) {
				exceptionCode= concessionFilterDao.deleteAndInsertApprFilter(vObjectlocal);
			}
			List<ConcessionConfigTierVb> collTempTierDet = null;
			collTempTierDet = concessionConfigTierDao.getConcessionConfigTierByGroup(vObject, 1);
			if (collTempTierDet != null && collTempTierDet.size() > 0) {
				 concessionConfigTierDao.deleteConcessionTierAppr(vObjectlocal);
					
				 collTempTierDet.forEach(conTier -> {
							conTier.setRecordIndicator(vObject.getRecordIndicator());
							retVal = concessionConfigTierDao.doInsertionApprConcessionTier(conTier);
						});
			}
			if("Y".equalsIgnoreCase(vObjectlocal.getActivityLinkFlag())) {
			List<ConcessionConfigHeaderVb> collTempAct= null;
			collTempAct =getQueryActivityLink(vObject, 0);
			if(collTempAct != null && collTempAct.size() > 0) {
				exceptionCode = deleteAndInsertApprActivityLink(vObjectlocal);
			}
			
			String systemDate = getSystemDate();
			vObject.setDateLastModified(systemDate);
			}
			} else {
			// delete the record from the Approve Table
			retVal = deleteConcessionHeaderAppr(vObject);
//			vObject.setRecordIndicator(-1);
			String systemDate = getSystemDate();
			vObject.setDateLastModified(systemDate);
			List<ConcessionConfigDetailsVb> collTempFee = null;
			collTempFee = concessionConfigDetailsDao.getConcessionConfigDetails(vObject, 0);
			if (collTempFee != null && collTempFee.size() > 0) {
				int delCnt = concessionConfigDetailsDao.deleteConcessionDetailsAppr(vObject);
			}
			List<ConcessionFilterVb> collTempFilter = null;
			collTempFilter = concessionFilterDao.getConcessionFilters(vObject, 0);
			if (collTempFilter != null && collTempFilter.size() > 0) {
				int delCnt = concessionFilterDao.deleteConcessionFilterAppr(vObject);
			}
			List<ConcessionConfigTierVb> collTempTier = null;
			collTempTier = concessionConfigTierDao.getConcessionConfigTierByGroup(vObject, 0);
			if (collTempTier != null && collTempTier.size() > 0) {
				int delCnt = concessionConfigTierDao.deleteConcessionTierAppr(vObject);
			}
			if("Y".equalsIgnoreCase(vObject.getActivityLinkFlag())) {
			List<ConcessionConfigHeaderVb> collTempActLink= null;
			collTempActLink =getQueryActivityLink(vObject, 0);
			if(collTempActLink != null && collTempActLink.size() > 0) {
				int delCnt = deleteConcessionActivityLinkAppr(vObject);
			}
			}
			

		}
		if (retVal != Constants.SUCCESSFUL_OPERATION) {
			exceptionCode = getResultObject(retVal);
			throw buildRuntimeCustomException(exceptionCode);
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

	public List<AlphaSubTabVb> findSourceTableNames() throws DataAccessException {
		String sql = "SELECT TABLE_NAME,SOURCE_TABLE FROM RA_SOURCE_TABLE_MAPPINGS WHERE TABLE_STATUS = 0 ORDER BY TABLE_NAME";
		return getJdbcTemplate().query(sql, getSourceTableMappper());
	}

	protected RowMapper getSourceTableMappper() {
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				AlphaSubTabVb alphaSubTabVb = new AlphaSubTabVb();
				alphaSubTabVb.setDescription(rs.getString("SOURCE_TABLE"));
				alphaSubTabVb.setAlphaSubTab(rs.getString("TABLE_NAME"));
				return alphaSubTabVb;
			}
		};
		return mapper;
	}

	@Override
	@Transactional(rollbackForClassName = { "com.vision.exception.RuntimeCustomException" })
	public ExceptionCode bulkApprove(List<ConcessionConfigHeaderVb> vObjects, boolean staticDelete)
			throws RuntimeCustomException {
		strErrorDesc = "";
		strCurrentOperation = Constants.APPROVE;
		ExceptionCode exceptionCode = null;
		setServiceDefaults();
		try {
			boolean foundFlag = false;
			for (ConcessionConfigHeaderVb object : vObjects) {
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
	public ExceptionCode doBulkReject(List<ConcessionConfigHeaderVb> vObjects) throws RuntimeCustomException {

		strErrorDesc = "";
		strCurrentOperation = Constants.REJECT;
		setServiceDefaults();
		ExceptionCode exceptionCode = null;
		try {
			boolean foundFlag = false;
			for (ConcessionConfigHeaderVb object : vObjects) {

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
	
	
	protected int doInsertionApprConcessionActivityLink(ConcessionConfigHeaderVb vObject) {
		String query = " Insert Into RA_MST_CONCESSION_ACT_LINK(COUNTRY,LE_BOOK,CONCESSION_ID,"
				+ "ACTIVITY_ID,"
				+ "	Status_NT, Status,"
				+ "RECORD_INDICATOR_NT,RECORD_INDICATOR,MAKER,VERIFIER,DATE_LAST_MODIFIED,DATE_CREATION) "
				+ " Values (?,?,?,?,?,?,?,?,?,?," + getDbFunction("SYSDATE") + ","
				+ getDbFunction("SYSDATE") + ")";

		Object[] args = { vObject.getCountry(), vObject.getLeBook(), vObject.getConcessionId(),vObject.getActivityId(),
				 vObject.getConcessionStatusNt(),
				vObject.getConcessionStatus(), vObject.getRecordIndicatorNt(), vObject.getRecordIndicator(),
				vObject.getMaker(), vObject.getVerifier() };
		return getJdbcTemplate().update(query, args);
	}

	protected int doInsertionConcessionActivityLinkPend(ConcessionConfigHeaderVb vObject) {
		String query = " Insert Into RA_MST_CONCESSION_ACT_LINK_PEND(COUNTRY,LE_BOOK,CONCESSION_ID,"
				+ "ACTIVITY_ID,"
				+ "	Status_NT, Status,"
				+ "RECORD_INDICATOR_NT,RECORD_INDICATOR,MAKER,VERIFIER,DATE_LAST_MODIFIED,DATE_CREATION) "
				+ " Values (?,?,?,?,?,?,?,?,?,?," + getDbFunction("SYSDATE") + ","
				+ getDbFunction("SYSDATE") + ")";
		
		Object[] args = { vObject.getCountry(), vObject.getLeBook(), vObject.getConcessionId(),vObject.getActivityId(),
				 vObject.getConcessionStatusNt(),
				vObject.getConcessionStatus(), vObject.getRecordIndicatorNt(), vObject.getRecordIndicator(),
				vObject.getMaker(), vObject.getVerifier() };
		return getJdbcTemplate().update(query, args);
	}
	protected int doUpdateApprConcessionActivityLink(ConcessionConfigHeaderVb vObject) {
		String query = " Update RA_MST_CONCESSION_ACT_LINK set "
				+ " ACTIVITY_ID=?,"
				+ " Status= ? ,RECORD_INDICATOR= ? ,MAKER= ? ," + " VERIFIER= ? ,DATE_LAST_MODIFIED= "
				+ getDbFunction("SYSDATE") + " " + " WHERE COUNTRY= ? AND LE_BOOK= ? AND CONCESSION_ID = ?";
		Object[] args = { vObject.getActivityId(),vObject.getConcessionStatus(), vObject.getRecordIndicator(), vObject.getMaker(), vObject.getVerifier(),
				vObject.getCountry(), vObject.getLeBook(), vObject.getConcessionId()};
		return getJdbcTemplate().update(query, args);
	}

	protected int doUpdateConcessionActivityLinkPend(ConcessionConfigHeaderVb vObject) {
		String query = " Update RA_MST_CONCESSION_ACT_LINK_PEND set "
				+ " ACTIVITY_ID=?,"
				+ " Status= ? ,RECORD_INDICATOR= ? ,MAKER= ? ," + " VERIFIER= ? ,DATE_LAST_MODIFIED= "
				+ getDbFunction("SYSDATE") + " " + " WHERE COUNTRY= ? AND LE_BOOK= ? AND CONCESSION_ID = ?";
		Object[] args = { vObject.getActivityId(),vObject.getConcessionStatus(), vObject.getRecordIndicator(), vObject.getMaker(), vObject.getVerifier(),
				vObject.getCountry(), vObject.getLeBook(), vObject.getConcessionId()};
		return getJdbcTemplate().update(query, args);
	}
	protected int deleteConcessionActivityLinkAppr(ConcessionConfigHeaderVb vObject) {
		String query = "Delete from RA_MST_CONCESSION_ACT_LINK  WHERE COUNTRY= ? AND LE_BOOK= ?  AND CONCESSION_ID = ?";
				
		Object[] args = { vObject.getCountry(), vObject.getLeBook(), vObject.getConcessionId()};
				
		return getJdbcTemplate().update(query, args);

	}

	protected int deleteConcessionActivityLinkPend(ConcessionConfigHeaderVb vObject) {
		String query = "Delete from RA_MST_CONCESSION_ACT_LINK_PEND  WHERE COUNTRY= ? AND LE_BOOK= ?  AND CONCESSION_ID = ?";
		
		Object[] args = { vObject.getCountry(), vObject.getLeBook(), vObject.getConcessionId()};
		return getJdbcTemplate().update(query, args);

	}
	public List<ConcessionConfigHeaderVb> getQueryActivityLink(ConcessionConfigHeaderVb dObj, int intStatus) {
		List<ConcessionConfigHeaderVb> collTemp = null;
		setServiceDefaults();
		String strQueryAppr = null;
		String strQueryPend = null;
		
		strQueryAppr = new String(" SELECT TAppr.COUNTRY, TAppr.LE_BOOK,TAppr.CONCESSION_ID,ACTIVITY_ID,"
				+ " TAppr.Status, "
				+ " TAppr.RECORD_INDICATOR,"
				+ " TAppr. MAKER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "
				+ getDbFunction("NVL") + "(TAppr.MAKER,0) ) MAKER_NAME, "
				+ " TAppr.VERIFIER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "
				+ getDbFunction("NVL") + "(TAppr.VERIFIER,0) ) VERIFIER_NAME,  " + " "
				+ getDbFunction("DATEFUNC") + "(TAppr.DATE_LAST_MODIFIED, 'dd-MM-yyyy "
				+ getDbFunction("TIME") + "') DATE_LAST_MODIFIED ," + getDbFunction("DATEFUNC")
				+ "(TAppr.DATE_CREATION, 'dd-MM-yyyy " + getDbFunction("TIME") + "') DATE_CREATION "
				+ " FROM RA_MST_CONCESSION_ACT_LINK TAppr     " + " Where  "
				+" TAppr.COUNTRY = ? "
				+ " and TAppr.LE_BOOK = ? and TAppr.CONCESSION_ID = ? " );
		strQueryPend = new String(" SELECT TPend.COUNTRY, TPend.LE_BOOK,TPend.CONCESSION_ID,TPend.ACTIVITY_ID, "
				+ " TPend.Status, "
				+ " TPend.RECORD_INDICATOR,"
				+ " TPend. MAKER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "
				+ getDbFunction("NVL") + "(TPend.MAKER,0) ) MAKER_NAME, "
				+ " TPend.VERIFIER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "
				+ getDbFunction("NVL") + "(TPend.VERIFIER,0) ) VERIFIER_NAME,  " + " "
				+ getDbFunction("DATEFUNC") + "(TPend.DATE_LAST_MODIFIED, 'dd-MM-yyyy "
				+ getDbFunction("TIME") + "') DATE_LAST_MODIFIED ," + getDbFunction("DATEFUNC")
				+ "(TPend.DATE_CREATION, 'dd-MM-yyyy " + getDbFunction("TIME") + "') DATE_CREATION "
				+ " FROM RA_MST_CONCESSION_ACT_LINK_PEND TPend     " + " Where  "
				+ "  TPend.COUNTRY = ? "
				+ " and TPend.LE_BOOK = ? and TPend.CONCESSION_ID = ? ");

		Object objParams[] = new Object[3];
		objParams[0] = new String(dObj.getCountry());
		objParams[1] = new String(dObj.getLeBook());
		objParams[2] = new String(dObj.getConcessionId());
		
		try {
			if (intStatus == 0) {
				logger.info("Executing approved query");
				collTemp = getJdbcTemplate().query(strQueryAppr.toString(), objParams, getActivityLinkMapper());
			} else {
				logger.info("Executing pending query");
				collTemp = getJdbcTemplate().query(strQueryPend.toString(), objParams, getActivityLinkMapper());
			}
			return collTemp;
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("Error: getQueryResults Exception :   ");
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
	protected RowMapper getActivityLinkMapper() {
		RowMapper mapper = new RowMapper() {
			@Override
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				ConcessionConfigHeaderVb vObject = new ConcessionConfigHeaderVb();
				vObject.setCountry(rs.getString("COUNTRY"));
				vObject.setLeBook(rs.getString("LE_BOOK"));
				vObject.setConcessionId(rs.getString("CONCESSION_ID"));
				vObject.setActivityId(rs.getString("ACTIVITY_ID"));
				vObject.setConcessionStatus(rs.getInt("Status"));
				vObject.setRecordIndicator(rs.getInt("RECORD_INDICATOR"));
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

	public ExceptionCode deleteAndInsertApprActivityLink(ConcessionConfigHeaderVb vObject) throws RuntimeCustomException {
		ExceptionCode exceptionCode = new ExceptionCode();
		List<ConcessionConfigHeaderVb> collTemp = null;
		collTemp = getQueryActivityLink(vObject, Constants.STATUS_ZERO);
		if (collTemp != null && collTemp.size() > 0 ){
			int delCnt = deleteConcessionActivityLinkAppr(vObject);
		}
	ConcessionConfigHeaderVb concessionConfigHeaderVb = new ConcessionConfigHeaderVb(); 
		String [] activityId=vObject.getActivityId().split(",");
		
			for( String actId:activityId ){
				concessionConfigHeaderVb.setActivityId(actId);
				concessionConfigHeaderVb.setRecordIndicator(vObject.getRecordIndicator());
				concessionConfigHeaderVb.setCountry(vObject.getCountry());
				concessionConfigHeaderVb.setLeBook(vObject.getLeBook());
				concessionConfigHeaderVb.setConcessionId(vObject.getConcessionId());
				concessionConfigHeaderVb.setEffectiveDate(vObject.getEffectiveDate());
				retVal = doInsertionApprConcessionActivityLink(concessionConfigHeaderVb);
				writeAuditLog(concessionConfigHeaderVb, null);
				if (retVal != Constants.SUCCESSFUL_OPERATION){
					exceptionCode = getResultObject(retVal);
					throw buildRuntimeCustomException(exceptionCode);
				}
			}
		
		exceptionCode = getResultObject(Constants.SUCCESSFUL_OPERATION);
		return exceptionCode;
	}
	public ExceptionCode deleteAndInsertPendActivityLink(ConcessionConfigHeaderVb vObject) throws RuntimeCustomException {
		ExceptionCode exceptionCode =  new ExceptionCode();
		List<ConcessionConfigHeaderVb> collTemp = null;
		collTemp = getQueryActivityLink(vObject, 1);
		if (collTemp != null && collTemp.size() > 0 ){
			int delCnt = deleteConcessionActivityLinkPend(vObject);
		}
		ConcessionConfigHeaderVb concessionConfigHeaderVb = new ConcessionConfigHeaderVb(); 
		String [] activityId=vObject.getActivityId().split(",");
		
			for( String actId:activityId ){
				concessionConfigHeaderVb.setActivityId(actId);
				concessionConfigHeaderVb.setRecordIndicator(vObject.getRecordIndicator());
				concessionConfigHeaderVb.setCountry(vObject.getCountry());
				concessionConfigHeaderVb.setLeBook(vObject.getLeBook());
				concessionConfigHeaderVb.setConcessionId(vObject.getConcessionId());
				concessionConfigHeaderVb.setEffectiveDate(vObject.getEffectiveDate());
				retVal = doInsertionConcessionActivityLinkPend(concessionConfigHeaderVb);
				writeAuditLog(concessionConfigHeaderVb, null);
				if (retVal != Constants.SUCCESSFUL_OPERATION){
					exceptionCode = getResultObject(retVal);
					throw buildRuntimeCustomException(exceptionCode);
				}
			}	
		
		exceptionCode = getResultObject(Constants.SUCCESSFUL_OPERATION);
		return exceptionCode;
	}
	
	public String getconcessionHeaderPend(String country, String leBook, String concessionId) {
		Object args[] = { country, leBook, concessionId };
		String orginalQuery = "SELECT *  from RA_MST_CONCESSION_HEADER WHERE COUNTRY= ? AND LE_BOOK= ?  AND CONCESSION_ID = ?";
		return getJdbcTemplate().queryForObject(orginalQuery, args, String.class);
	}
}
