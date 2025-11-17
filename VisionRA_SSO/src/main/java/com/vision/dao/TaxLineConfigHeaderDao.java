package com.vision.dao;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
import com.vision.vb.SmartSearchVb;
import com.vision.vb.TaxConfigTierVb;
import com.vision.vb.TaxLineConfigDetailsVb;
import com.vision.vb.TaxLineConfigHeaderVb;
import com.vision.vb.TaxLinkVb;
import com.vision.vb.VisionUsersVb;

@Component
public class TaxLineConfigHeaderDao extends AbstractDao<TaxLineConfigHeaderVb> {
	@Autowired
	CommonDao commonDao;
	@Autowired
	TaxLineConfigDetailsDao taxLineConfigDetailsDao;
	@Autowired
	TaxConfigTierDao taxConfigTierDao;
	@Value("${app.databaseType}")
	private String databaseType;

	@Override
	protected RowMapper getMapper() {
		RowMapper mapper = new RowMapper() {
			@Override
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				TaxLineConfigHeaderVb vObject = new TaxLineConfigHeaderVb();
				vObject.setCountry(rs.getString("COUNTRY"));
				vObject.setLeBook(rs.getString("LE_BOOK"));
				vObject.setTaxLineId(rs.getString("TAX_LINE_ID"));
				vObject.setTaxLineDescription(
						rs.getString("TAX_LINE_ID") + " - " + rs.getString("TAX_LINE_DESCRIPTION"));
				vObject.setEffectiveDate(rs.getString("EFFECTIVE_DATE"));
				vObject.setTaxCcyDesc(rs.getString("TAX_CCY_DESC"));
				vObject.setTaxCcy(rs.getString("TAX_CCY"));
				vObject.setTaxBasisDesc(rs.getString("TAX_BASIS_DESC"));
				vObject.setTaxBasis(rs.getString("TAX_BASIS"));
				vObject.setTaxChargeTypeDesc(rs.getString("TAX_CHARGE_TYPE_DESC"));
				vObject.setTaxChargeType(rs.getString("TAX_CHARGE_TYPE"));
				vObject.setTierTypeDesc(rs.getString("TIER_TYPE_DESC"));
				vObject.setTierType(rs.getString("TIER_TYPE"));
				vObject.setTaxLineStatus(rs.getInt("TAX_LINE_STATUS"));
				vObject.setTaxLineStatusDesc(rs.getString("TAX_LINE_STATUS_Desc"));
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
				TaxLineConfigHeaderVb vObject = new TaxLineConfigHeaderVb();
				vObject.setCountry(rs.getString("COUNTRY"));
				vObject.setLeBook(rs.getString("LE_BOOK"));
				vObject.setTaxLineId(rs.getString("TAX_LINE_ID"));
				vObject.setTaxLineDescription(rs.getString("TAX_LINE_DESCRIPTION"));
				vObject.setEffectiveDate(rs.getString("EFFECTIVE_DATE"));
				vObject.setTaxCcy(rs.getString("TAX_CCY"));
				vObject.setTaxBasis(rs.getString("TAX_BASIS"));
				vObject.setTaxChargeType(rs.getString("TAX_CHARGE_TYPE"));
				vObject.setTierType(rs.getString("TIER_TYPE"));
				vObject.setTaxLineStatus(rs.getInt("TAX_LINE_STATUS"));
				vObject.setTaxLineStatusDesc(rs.getString("TAX_LINE_STATUS_DESC"));
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
	public List<TaxLineConfigHeaderVb> getQueryPopupResults(TaxLineConfigHeaderVb dObj) {
		Vector<Object> params = new Vector<Object>();
		StringBuffer strBufApprove = null;
		StringBuffer strBufPending = null;
		String strWhereNotExists = null;
		String orderBy = "";
		String effectiveDateAppr = "";
		String effectiveDatePend = "";
		if ("ORACLE".equalsIgnoreCase(databaseType)) {
			effectiveDateAppr = "TO_CHAR(TAPPR.EFFECTIVE_DATE,'DD-Mon-RRRR')";
			effectiveDatePend = "TO_CHAR(TPend.EFFECTIVE_DATE,'DD-Mon-RRRR')";
		} else if ("MSSQL".equalsIgnoreCase(databaseType)) {
			effectiveDateAppr = "format(CAST(TAppr.EFFECTIVE_DATE AS DATETIME), 'dd-MMM-yyyy')";
			effectiveDatePend = "format(CAST(TPend.EFFECTIVE_DATE AS DATETIME), 'dd-MMM-yyyy')";
		}
		strBufApprove = new StringBuffer(
				"Select * from ( SELECT TAppr.COUNTRY, TAppr.LE_BOOK, TAppr.TAX_LINE_ID,TAppr.TAX_LINE_DESCRIPTION, "
						+ "	" + effectiveDateAppr + " EFFECTIVE_DATE, "
						+ " (select T5.ALPHA_SUBTAB_DESCRIPTION from ALPHA_SUB_TAB T5 where t5.Alpha_tab = TAppr.TAX_CCY_AT and T5.ALPHA_SUB_TAB=TAppr.TAX_CCY) TAX_CCY_DESC, TAX_CCY, "
						+ " (select T1.ALPHA_SUBTAB_DESCRIPTION from ALPHA_SUB_TAB T1 where t1.Alpha_tab = TAppr.TAX_BASIS_AT and T1.ALPHA_SUB_TAB=TAppr.TAX_BASIS)   TAX_BASIS_DESC, TAX_BASIS,"
						+ " (select T1.ALPHA_SUBTAB_DESCRIPTION from ALPHA_SUB_TAB T1 where t1.Alpha_tab = TAppr.TAX_CHARGE_TYPE_AT and T1.ALPHA_SUB_TAB=TAppr.TAX_CHARGE_TYPE)   TAX_CHARGE_TYPE_DESC, TAX_CHARGE_TYPE,"
						+ " (select T1.ALPHA_SUBTAB_DESCRIPTION from ALPHA_SUB_TAB T1 where t1.Alpha_tab = TAppr.TIER_TYPE_AT and T1.ALPHA_SUB_TAB=TAppr.TIER_TYPE)   TIER_TYPE_DESC, TIER_TYPE,"
						+ "  TAppr.TAX_LINE_STATUS,T2.NUM_SUBTAB_DESCRIPTION TAX_LINE_STATUS_DESC,                                                                                                                           "
						+ "  TAppr.RECORD_INDICATOR,T3.NUM_SUBTAB_DESCRIPTION RECORD_INDICATOR_DESC,                                                                                                                             "
						+ "  TAppr. MAKER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "
						+ getDbFunction("NVL")
						+ "(TAppr.MAKER,0) ) MAKER_NAME,                                                                                         "
						+ "  TAppr.VERIFIER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "
						+ getDbFunction("NVL")
						+ "(TAppr.VERIFIER,0) ) VERIFIER_NAME,                                                                                 "
						+ "  " + getDbFunction("DATEFUNC") + "(TAppr.DATE_LAST_MODIFIED, 'dd-MM-yyyy "
						+ getDbFunction("TIME") + "') DATE_LAST_MODIFIED ,"
						+ getDbFunction("DATEFUNC") + "(TAppr.DATE_CREATION, 'dd-MM-yyyy "
						+ getDbFunction("TIME")
						+ "') DATE_CREATION,TAppr.DATE_LAST_MODIFIED Sort_Order                                                        "
						+ "  FROM RA_MST_TAX_HEADER TAppr ,NUM_SUB_TAB T2,NUM_SUB_TAB T3                                                                                                                                  "
						+ "  Where t2.NUM_tab = TAppr.TAX_LINE_STATUS_NT                                                                                                                                                         "
						+ "  and t2.NUM_sub_tab = TAppr.TAX_LINE_STATUS   "
						+ "  and t3.NUM_tab = TAppr.RECORD_INDICATOR_NT    "
						+ "  and t3.NUM_sub_tab = TAppr.RECORD_INDICATOR " + " ) TAppr");
		strWhereNotExists = new String(
				" Not Exists (Select 'X' From RA_MST_TAX_HEADER_PEND TPEND WHERE TAPPR.COUNTRY = TPend.COUNTRY"
						+ " AND TAPPR.LE_BOOK = TPend.LE_BOOK AND TAPPR.TAX_LINE_ID = TPend.TAX_LINE_ID AND TAPPR.EFFECTIVE_DATE = TPEND.EFFECTIVE_DATE)");

		strBufPending = new StringBuffer(
				"Select * from ( SELECT TPend.COUNTRY, TPend.LE_BOOK, TPend.TAX_LINE_ID,TPend.TAX_LINE_DESCRIPTION, "
						+ " " + effectiveDatePend + " EFFECTIVE_DATE, "
						+ " (select T5.ALPHA_SUBTAB_DESCRIPTION from ALPHA_SUB_TAB T5 where t5.Alpha_tab = TPend.TAX_CCY_AT and T5.ALPHA_SUB_TAB=TPend.TAX_CCY) TAX_CCY_DESC, TAX_CCY, "
						+ " (select T1.ALPHA_SUBTAB_DESCRIPTION from ALPHA_SUB_TAB T1 where t1.Alpha_tab = TPend.TAX_BASIS_AT and T1.ALPHA_SUB_TAB=TPend.TAX_BASIS)   TAX_BASIS_DESC, TAX_BASIS,"
						+ " (select T1.ALPHA_SUBTAB_DESCRIPTION from ALPHA_SUB_TAB T1 where t1.Alpha_tab = TPend.TAX_CHARGE_TYPE_AT and T1.ALPHA_SUB_TAB=TPend.TAX_CHARGE_TYPE)   TAX_CHARGE_TYPE_DESC, TAX_CHARGE_TYPE,"
						+ " (select T1.ALPHA_SUBTAB_DESCRIPTION from ALPHA_SUB_TAB T1 where t1.Alpha_tab = TPend.TIER_TYPE_AT and T1.ALPHA_SUB_TAB=TPend.TIER_TYPE)   TIER_TYPE_DESC, TIER_TYPE,"
						+ "  TPend.TAX_LINE_STATUS,T2.NUM_SUBTAB_DESCRIPTION TAX_LINE_STATUS_DESC,                                                                                                                           "
						+ "  TPend.RECORD_INDICATOR,T3.NUM_SUBTAB_DESCRIPTION RECORD_INDICATOR_DESC,                                                                                                                             "
						+ "  TPend. MAKER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "
						+ getDbFunction("NVL")
						+ "(TPend.MAKER,0) ) MAKER_NAME,                                                                                         "
						+ "  TPend.VERIFIER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "
						+ getDbFunction("NVL")
						+ "(TPend.VERIFIER,0) ) VERIFIER_NAME,                                                                                 "
						+ "  " + getDbFunction("DATEFUNC") + "(TPend.DATE_LAST_MODIFIED, 'dd-MM-yyyy "
						+ getDbFunction("TIME") + "') DATE_LAST_MODIFIED ,"
						+ getDbFunction("DATEFUNC") + "(TPend.DATE_CREATION, 'dd-MM-yyyy "
						+ getDbFunction("TIME")
						+ "') DATE_CREATION,TPend.DATE_LAST_MODIFIED Sort_Order                                                        "
						+ "  FROM RA_MST_TAX_HEADER_PEND TPend ,NUM_SUB_TAB T2,NUM_SUB_TAB T3                                                                                                                                  "
						+ "  Where t2.NUM_tab = TPend.TAX_LINE_STATUS_NT                                                                                                                                                         "
						+ "  and t2.NUM_sub_tab = TPend.TAX_LINE_STATUS   "
						+ "  and t3.NUM_tab = TPend.RECORD_INDICATOR_NT    "
						+ "  and t3.NUM_sub_tab = TPend.RECORD_INDICATOR " + " ) TPend");

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

					case "taxLineDescription":
						CommonUtils.addToQuerySearch(" upper(TAPPR.TAX_LINE_DESCRIPTION) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.TAX_LINE_DESCRIPTION) " + val, strBufPending,
								data.getJoinType());
						break;

					case "taxCcyDesc":
						CommonUtils.addToQuerySearch(" upper(TAPPR.TAX_CCY_DESC) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.TAX_CCY_DESC) " + val, strBufPending,
								data.getJoinType());
						break;

					case "taxBasisDesc":
						CommonUtils.addToQuerySearch(" upper(TAPPR.TAX_BASIS_DESC) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.TAX_BASIS_DESC) " + val, strBufPending,
								data.getJoinType());
						break;

					case "taxChargeTypeDesc":
						CommonUtils.addToQuerySearch(" upper(TAPPR.TAX_CHARGE_TYPE_DESC) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.TAX_CHARGE_TYPE_DESC) " + val, strBufPending,
								data.getJoinType());
						break;

					case "tierTypeDesc":
						CommonUtils.addToQuerySearch(" upper(TAPPR.TIER_TYPE_DESC) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.TIER_TYPE_DESC) " + val, strBufPending,
								data.getJoinType());
						break;

					case "taxLineStatusDesc":
						CommonUtils.addToQuerySearch(" upper(TAPPR.TAX_LINE_STATUS_DESC) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.TAX_LINE_STATUS_DESC) " + val, strBufPending,
								data.getJoinType());
						break;

					case "effectiveDate":
						CommonUtils.addToQuerySearch(" " + getDbFunction("DATEFUNC")
								+ "(TAPPR.EFFECTIVE_DATE 'DD-MMM-YYYY') " + val, strBufApprove, data.getJoinType());
						CommonUtils.addToQuerySearch(" " + getDbFunction("DATEFUNC")
								+ "(TPend.EFFECTIVE_DATE 'DD-MMM-YYYY') " + val, strBufPending, data.getJoinType());
						break;

					case "businessLineStatusDesc":
						CommonUtils.addToQuerySearch(" upper(TAPPR.FEE_LINE_STATUS_DESC) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.FEE_LINE_STATUS_DESC) " + val, strBufPending,
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
			visionUsersVb = commonDao.getRestrictionInfo(visionUsersVb);
			if (("Y".equalsIgnoreCase(visionUsersVb.getUpdateRestriction()))) {
				if(ValidationUtil.isValid(visionUsersVb.getCountry())){
					CommonUtils.addToQuery(" COUNTRY"+getDbFunction("PIPELINE", "")+"'-'"+getDbFunction("PIPELINE", "")+"LE_BOOK IN ("+visionUsersVb.getCountry()+") ", strBufApprove);
					CommonUtils.addToQuery(" COUNTRY"+getDbFunction("PIPELINE", "")+"'-'"+getDbFunction("PIPELINE", "")+"LE_BOOK IN ("+visionUsersVb.getCountry()+") ", strBufPending);
				}
			}
			orderBy = " Order by Sort_Order DESC,TAX_LINE_ID ";
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
	public List<TaxLineConfigHeaderVb> getQueryResults(TaxLineConfigHeaderVb dObj, int intStatus) {
		List<TaxLineConfigHeaderVb> collTemp = null;
		setServiceDefaults();
		String strQueryAppr = null;
		String strQueryPend = null;
		if ("ORACLE".equalsIgnoreCase(databaseType)) {
			strQueryAppr = new String(
					"SELECT TAppr.COUNTRY, TAppr.LE_BOOK, TAppr.TAX_LINE_ID,TAppr.TAX_LINE_DESCRIPTION,"
							+ " TO_CHAR(TAPPR.EFFECTIVE_DATE,'DD-Mon-RRRR') EFFECTIVE_DATE," + " TAppr.TAX_CCY, "
							+ " TAppr.TAX_BASIS,TAppr.TAX_CHARGE_TYPE,TAppr.TIER_TYPE, "
							+ " TAppr.TAX_LINE_STATUS,TAppr.RECORD_INDICATOR,T1.NUM_SUBTAB_DESCRIPTION RECORD_INDICATOR_DESC,"
							+ " TAppr. MAKER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "
							+ getDbFunction("NVL") + "(TAppr.MAKER,0) ) MAKER_NAME, "
							+ " TAppr.VERIFIER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "
							+ getDbFunction("NVL") + "(TAppr.VERIFIER,0) ) VERIFIER_NAME, " + " "
							+ getDbFunction("DATEFUNC") + "(TAppr.DATE_LAST_MODIFIED, 'dd-MM-yyyy "
							+ getDbFunction("TIME") + "') DATE_LAST_MODIFIED ,"
							+ getDbFunction("DATEFUNC") + "(TAppr.DATE_CREATION, 'dd-MM-yyyy "
							+ getDbFunction("TIME")
							+ "') DATE_CREATION,T2.NUM_SUBTAB_DESCRIPTION TAX_LINE_STATUS_DESC "
							+ "  FROM RA_MST_TAX_HEADER TAppr, NUM_SUB_TAB T1 ,NUM_SUB_TAB T2 WHERE "
							+ " TAppr.COUNTRY =? AND TAPPR.LE_BOOK =? AND TAppr.TAX_LINE_ID = ? AND TAPPR.EFFECTIVE_DATE = ? "
							+ " AND T1.NUM_tab = TAppr.RECORD_INDICATOR_NT and T1.NUM_sub_tab = TAppr.RECORD_INDICATOR"
							+ " AND T2.NUM_TAB = TAPPR.TAX_LINE_STATUS_NT "
							+ " AND T2.NUM_SUB_TAB = TAPPR.TAX_LINE_STATUS ");

			strQueryPend = new String(
					"SELECT TPend.COUNTRY, TPend.LE_BOOK, TPend.TAX_LINE_ID,TPend.TAX_LINE_DESCRIPTION,"
							+ " TO_CHAR(TPend.EFFECTIVE_DATE,'DD-Mon-RRRR') EFFECTIVE_DATE," + " TPend.TAX_CCY, "
							+ " TPend.TAX_BASIS,TPend.TAX_CHARGE_TYPE,TPend.TIER_TYPE, "
							+ " TPend.TAX_LINE_STATUS,TPend.RECORD_INDICATOR,T1.NUM_SUBTAB_DESCRIPTION RECORD_INDICATOR_DESC,"
							+ " TPend. MAKER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "
							+ getDbFunction("NVL") + "(TPend.MAKER,0) ) MAKER_NAME, "
							+ " TPend.VERIFIER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "
							+ getDbFunction("NVL") + "(TPend.VERIFIER,0) ) VERIFIER_NAME, " + " "
							+ getDbFunction("DATEFUNC") + "(TPend.DATE_LAST_MODIFIED, 'dd-MM-yyyy "
							+ getDbFunction("TIME") + "') DATE_LAST_MODIFIED ,"
							+ getDbFunction("DATEFUNC") + "(TPend.DATE_CREATION, 'dd-MM-yyyy "
							+ getDbFunction("TIME")
							+ "') DATE_CREATION ,T2.NUM_SUBTAB_DESCRIPTION TAX_LINE_STATUS_DESC "
							+ "  FROM RA_MST_TAX_HEADER_PEND TPend, NUM_SUB_TAB T1,NUM_SUB_TAB T2 WHERE "
							+ " TPend.COUNTRY =? AND TPend.LE_BOOK =? AND TPend.TAX_LINE_ID = ? AND TPend.EFFECTIVE_DATE = ? "
							+ " AND T1.NUM_tab = TPend.RECORD_INDICATOR_NT and T1.NUM_sub_tab = TPend.RECORD_INDICATOR"
							+ " AND T2.NUM_TAB = TPend.TAX_LINE_STATUS_NT "
							+ " AND T2.NUM_SUB_TAB = TPend.TAX_LINE_STATUS ");

		} else if ("MSSQL".equalsIgnoreCase(databaseType)) {
			strQueryAppr = new String(
					"SELECT TAppr.COUNTRY, TAppr.LE_BOOK, TAppr.TAX_LINE_ID,TAppr.TAX_LINE_DESCRIPTION," + " "
							+ getDbFunction("DATEFUNC")
							+ "(CAST(TAppr.EFFECTIVE_DATE AS DATETIME), 'dd-MMM-yyyy') EFFECTIVE_DATE,"
							+ " TAppr.TAX_CCY, " + " TAppr.TAX_BASIS,TAppr.TAX_CHARGE_TYPE,TAppr.TIER_TYPE, "
							+ " TAppr.TAX_LINE_STATUS,TAppr.RECORD_INDICATOR,T1.NUM_SUBTAB_DESCRIPTION RECORD_INDICATOR_DESC,"
							+ " TAppr. MAKER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "
							+ getDbFunction("NVL") + "(TAppr.MAKER,0) ) MAKER_NAME, "
							+ " TAppr.VERIFIER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "
							+ getDbFunction("NVL") + "(TAppr.VERIFIER,0) ) VERIFIER_NAME, " + " "
							+ getDbFunction("DATEFUNC") + "(TAppr.DATE_LAST_MODIFIED, 'dd-MM-yyyy "
							+ getDbFunction("TIME") + "') DATE_LAST_MODIFIED ,"
							+ getDbFunction("DATEFUNC") + "(TAppr.DATE_CREATION, 'dd-MM-yyyy "
							+ getDbFunction("TIME")
							+ "') DATE_CREATION ,T2.NUM_SUBTAB_DESCRIPTION TAX_LINE_STATUS_DESC "
							+ "  FROM RA_MST_TAX_HEADER TAppr, NUM_SUB_TAB T1,NUM_SUB_TAB T2 WHERE "
							+ " TAppr.COUNTRY =? AND TAPPR.LE_BOOK =? AND TAppr.TAX_LINE_ID = ? AND FORMAT(CAST(TAppr.EFFECTIVE_DATE AS DATETIME), 'dd-MMM-yyyy') = ? "
							+ " AND T1.NUM_tab = TAppr.RECORD_INDICATOR_NT and T1.NUM_sub_tab = TAppr.RECORD_INDICATOR"
							+ " AND T2.NUM_TAB = TAppr.TAX_LINE_STATUS_NT "
							+ " AND T2.NUM_SUB_TAB = TAppr.TAX_LINE_STATUS ");

			strQueryPend = new String(
					"SELECT TPend.COUNTRY, TPend.LE_BOOK, TPend.TAX_LINE_ID,TPend.TAX_LINE_DESCRIPTION," + " "
							+ getDbFunction("DATEFUNC")
							+ "(CAST(TPend.EFFECTIVE_DATE AS DATETIME), 'dd-MMM-yyyy') EFFECTIVE_DATE,"
							+ " TPend.TAX_CCY, " + " TPend.TAX_BASIS,TPend.TAX_CHARGE_TYPE,TPend.TIER_TYPE, "
							+ " TPend.TAX_LINE_STATUS,TPend.RECORD_INDICATOR,T1.NUM_SUBTAB_DESCRIPTION RECORD_INDICATOR_DESC,"
							+ " TPend. MAKER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "
							+ getDbFunction("NVL") + "(TPend.MAKER,0) ) MAKER_NAME, "
							+ " TPend.VERIFIER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "
							+ getDbFunction("NVL") + "(TPend.VERIFIER,0) ) VERIFIER_NAME, " + " "
							+ getDbFunction("DATEFUNC") + "(TPend.DATE_LAST_MODIFIED, 'dd-MM-yyyy "
							+ getDbFunction("TIME") + "') DATE_LAST_MODIFIED ,"
							+ getDbFunction("DATEFUNC") + "(TPend.DATE_CREATION, 'dd-MM-yyyy "
							+ getDbFunction("TIME")
							+ "') DATE_CREATION,T2.NUM_SUBTAB_DESCRIPTION TAX_LINE_STATUS_DESC "
							+ "  FROM RA_MST_TAX_HEADER_PEND TPend, NUM_SUB_TAB T1,NUM_SUB_TAB T2 WHERE "
							+ " TPend.COUNTRY =? AND TPend.LE_BOOK =? AND TPend.TAX_LINE_ID = ? AND FORMAT(CAST(TPend.EFFECTIVE_DATE AS DATETIME), 'dd-MMM-yyyy') = ? "
							+ " AND T1.NUM_tab = TPend.RECORD_INDICATOR_NT and T1.NUM_sub_tab = TPend.RECORD_INDICATOR"
							+ " AND T2.NUM_TAB = TPend.TAX_LINE_STATUS_NT "
							+ " AND T2.NUM_SUB_TAB = TPend.TAX_LINE_STATUS ");

		}
		Object objParams[] = new Object[4];
		objParams[0] = new String(dObj.getCountry());
		objParams[1] = new String(dObj.getLeBook());
		objParams[2] = new String(dObj.getTaxLineId());
		objParams[3] = new String(dObj.getEffectiveDate());

		try {
			/*if (!dObj.isVerificationRequired() || dObj.isReview()) {
				intStatus = 0;
			}*/
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
				logger.error(((strQueryAppr == null) ? "strQueryAppr is Null" : strQueryAppr.toString()));
			else
				logger.error(((strQueryPend == null) ? "strQueryPend is Null" : strQueryPend.toString()));

			if (objParams != null)
				for (int i = 0; i < objParams.length; i++)
					//logger.error("objParams[" + i + "]" + objParams[i].toString());
*/			return null;
		}
	}

	@Override
	protected void setServiceDefaults() {
		serviceName = "TaxLineConfigHeader";
		serviceDesc = "Tax Line Config ";
		tableName = "RA_MST_TAX_HEADER";
		childTableName = "RA_MST_TAX_HEADER";
		intCurrentUserId = CustomContextHolder.getContext().getVisionId();
	}

	protected int doInsertionApprTaxLineHeaders(TaxLineConfigHeaderVb vObject) {
		String query = " Insert Into RA_MST_TAX_HEADER(COUNTRY,LE_BOOK,TAX_LINE_ID,TAX_LINE_DESCRIPTION,EFFECTIVE_DATE"
				+ ",TAX_CCY,TAX_BASIS,TAX_CHARGE_TYPE" + ",TIER_TYPE" + ",TAX_LINE_STATUS_NT,TAX_LINE_STATUS"
				+ ",RECORD_INDICATOR_NT,RECORD_INDICATOR,MAKER,VERIFIER,DATE_LAST_MODIFIED,DATE_CREATION) "
				+ " Values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?," + getDbFunction("SYSDATE") + ","
				+ getDbFunction("SYSDATE") + ")";

		Object[] args = { vObject.getCountry(), vObject.getLeBook(), vObject.getTaxLineId(),
				vObject.getTaxLineDescription(), vObject.getEffectiveDate(), vObject.getTaxCcy(), vObject.getTaxBasis(),
				vObject.getTaxChargeType(),ValidationUtil.isValid( vObject.getTierType())? vObject.getTierType() :"NA", vObject.getTaxLineStatusNT(),
				vObject.getTaxLineStatus(), vObject.getRecordIndicatorNt(), vObject.getRecordIndicator(),
				vObject.getMaker(), vObject.getVerifier() };
		return getJdbcTemplate().update(query, args);
	}

	protected int doInsertionPendTaxLineHeaders(TaxLineConfigHeaderVb vObject) {
		String query = " Insert Into RA_MST_TAX_HEADER_PEND(COUNTRY,LE_BOOK,TAX_LINE_ID,TAX_LINE_DESCRIPTION,EFFECTIVE_DATE"
				+ ",TAX_CCY,TAX_BASIS,TAX_CHARGE_TYPE" + ",TIER_TYPE" + ",TAX_LINE_STATUS_NT,TAX_LINE_STATUS"
				+ ",RECORD_INDICATOR_NT,RECORD_INDICATOR,MAKER,VERIFIER,DATE_LAST_MODIFIED,DATE_CREATION) "
				+ " Values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?," + getDbFunction("SYSDATE") + ","
				+ getDbFunction("SYSDATE") + ")";

		Object[] args = { vObject.getCountry(), vObject.getLeBook(), vObject.getTaxLineId(),
				vObject.getTaxLineDescription(), vObject.getEffectiveDate(), vObject.getTaxCcy(), vObject.getTaxBasis(),
				vObject.getTaxChargeType(), ValidationUtil.isValid( vObject.getTierType())? vObject.getTierType() :"NA", vObject.getTaxLineStatusNT(),
				vObject.getTaxLineStatus(), vObject.getRecordIndicatorNt(), vObject.getRecordIndicator(),
				vObject.getMaker(), vObject.getVerifier() };
		return getJdbcTemplate().update(query, args);
	}

	protected int doInsertionPendTaxLineHeadersDc(TaxLineConfigHeaderVb vObject) {
		String query = "";
		if ("ORACLE".equalsIgnoreCase(databaseType)) {
			query = " Insert Into RA_MST_TAX_HEADER_PEND(COUNTRY,LE_BOOK,TAX_LINE_ID,TAX_LINE_DESCRIPTION,EFFECTIVE_DATE"
					+ ",TAX_CCY,TAX_BASIS,TAX_CHARGE_TYPE" + ",TIER_TYPE" + ",TAX_LINE_STATUS_NT,TAX_LINE_STATUS"
					+ ",RECORD_INDICATOR_NT,RECORD_INDICATOR,MAKER,VERIFIER,DATE_LAST_MODIFIED,DATE_CREATION) "
					+ " Values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?," + getDbFunction("SYSDATE")
					+ ",To_Date(?, 'DD-MM-YYYY HH24:MI:SS') )";
		} else {
			query = " Insert Into RA_MST_TAX_HEADER_PEND(COUNTRY,LE_BOOK,TAX_LINE_ID,TAX_LINE_DESCRIPTION,EFFECTIVE_DATE"
					+ ",TAX_CCY,TAX_BASIS,TAX_CHARGE_TYPE" + ",TIER_TYPE" + ",TAX_LINE_STATUS_NT,TAX_LINE_STATUS"
					+ ",RECORD_INDICATOR_NT,RECORD_INDICATOR,MAKER,VERIFIER,DATE_LAST_MODIFIED,DATE_CREATION) "
					+ " Values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?," + getDbFunction("SYSDATE")
					+ ",CONVERT(datetime, ?, 103) )";
		}
		Object[] args = { vObject.getCountry(), vObject.getLeBook(), vObject.getTaxLineId(),
				vObject.getTaxLineDescription(), vObject.getEffectiveDate(), vObject.getTaxCcy(), vObject.getTaxBasis(),
				vObject.getTaxChargeType(),ValidationUtil.isValid( vObject.getTierType())? vObject.getTierType() :"NA", vObject.getTaxLineStatusNT(),
				vObject.getTaxLineStatus(), vObject.getRecordIndicatorNt(), vObject.getRecordIndicator(),
				vObject.getMaker(), vObject.getVerifier(), vObject.getDateCreation() };

		return getJdbcTemplate().update(query, args);
	}

	protected int doUpdateApprHeader(TaxLineConfigHeaderVb vObject) {
		String effectiveDate =" AND EFFECTIVE_DATE = ?";
		String dateCreation = " AND DATE_CREATION = ?";
		if (databaseType.equalsIgnoreCase("ORACLE")) {
			effectiveDate = " AND EFFECTIVE_DATE = TO_DATE(?,'DD-MM-YYYY HH24:MI:SS')";
			dateCreation = " AND DATE_CREATION = TO_DATE(?,'DD-MM-YYYY HH24:MI:SS')";
		}
		String query = " Update RA_MST_TAX_HEADER set TAX_LINE_DESCRIPTION= ?, TAX_CCY= ?"
				+ ",TAX_BASIS= ?,TAX_CHARGE_TYPE= ?" + ",TIER_TYPE= ?, TAX_LINE_STATUS= ?"
				+ ",RECORD_INDICATOR= ?,MAKER= ?,VERIFIER= ?,DATE_LAST_MODIFIED= " + getDbFunction("SYSDATE")
				+ " WHERE COUNTRY= ? AND LE_BOOK= ? AND TAX_LINE_ID= ? "+effectiveDate+"";
		Object[] args = { vObject.getTaxLineDescription(), vObject.getTaxCcy(),
				vObject.getTaxBasis(), vObject.getTaxChargeType(),ValidationUtil.isValid( vObject.getTierType())? vObject.getTierType() :"NA", vObject.getTaxLineStatus(),
				vObject.getRecordIndicator(), vObject.getMaker(), vObject.getVerifier(), vObject.getCountry(),
				vObject.getLeBook(), vObject.getTaxLineId(),vObject.getEffectiveDate() };
		return getJdbcTemplate().update(query, args);
	}

	protected int doUpdatePendHeader(TaxLineConfigHeaderVb vObject) {
		String effectiveDate =" AND EFFECTIVE_DATE = ?";
		String dateCreation = " AND DATE_CREATION = ?";
		if (databaseType.equalsIgnoreCase("ORACLE")) {
			effectiveDate = " AND EFFECTIVE_DATE = TO_DATE(?,'DD-MM-YYYY HH24:MI:SS')";
			dateCreation = " AND DATE_CREATION = TO_DATE(?,'DD-MM-YYYY HH24:MI:SS')";
		}
		String query = " Update RA_MST_TAX_HEADER_PEND set TAX_LINE_DESCRIPTION= ?, TAX_CCY= ?"
				+ ",TAX_BASIS= ?,TAX_CHARGE_TYPE= ?" + ",TIER_TYPE= ?, TAX_LINE_STATUS= ?"
				+ ",RECORD_INDICATOR= ?,MAKER= ?,VERIFIER= ?,DATE_LAST_MODIFIED= " + getDbFunction("SYSDATE")
				+ " WHERE COUNTRY= ? AND LE_BOOK= ? AND TAX_LINE_ID= ? "+effectiveDate+" ";
		Object[] args = { vObject.getTaxLineDescription(), vObject.getTaxCcy(),
				vObject.getTaxBasis(), vObject.getTaxChargeType(), ValidationUtil.isValid( vObject.getTierType())? vObject.getTierType() :"NA", vObject.getTaxLineStatus(),
				vObject.getRecordIndicator(), vObject.getMaker(), vObject.getVerifier(), vObject.getCountry(),
				vObject.getLeBook(), vObject.getTaxLineId(),vObject.getEffectiveDate() };
		return getJdbcTemplate().update(query, args);
	}

	protected int deleteTaxLineHeaderAppr(TaxLineConfigHeaderVb vObject) {
		try {
			String query = "Delete from RA_MST_TAX_HEADER WHERE COUNTRY= ? AND LE_BOOK= ?  AND TAX_LINE_ID = ?"
					+ " AND  EFFECTIVE_DATE= ?";
			Object[] args = { vObject.getCountry(), vObject.getLeBook(), vObject.getTaxLineId(),
					vObject.getEffectiveDate() };
			getJdbcTemplate().update(query, args);
			return Constants.SUCCESSFUL_OPERATION;
		}catch(Exception e) {
			return Constants.ERRONEOUS_OPERATION;
		}
	}

	protected int deleteTaxLineHeaderPend(TaxLineConfigHeaderVb vObject) {
		String query = "Delete from RA_MST_TAX_HEADER_PEND  WHERE COUNTRY= ? AND LE_BOOK= ?  AND TAX_LINE_ID = ?"
				+ " AND EFFECTIVE_DATE= ?";
		Object[] args = { vObject.getCountry(), vObject.getLeBook(), vObject.getTaxLineId(),
				vObject.getEffectiveDate() };
		return getJdbcTemplate().update(query, args);

	}

	@Override
	protected List<TaxLineConfigHeaderVb> selectApprovedRecord(TaxLineConfigHeaderVb vObject) {
		return getQueryResults(vObject, Constants.STATUS_ZERO);
	}

	@Override
	protected List<TaxLineConfigHeaderVb> doSelectPendingRecord(TaxLineConfigHeaderVb vObject) {
		return getQueryResults(vObject, Constants.STATUS_PENDING);
	}

	@Override
	protected int getStatus(TaxLineConfigHeaderVb records) {
		return records.getTaxLineStatus();
	}

	@Override
	protected void setStatus(TaxLineConfigHeaderVb vObject, int status) {
		vObject.setTaxLineStatus(status);
	}

	@Override
	public ExceptionCode doInsertApprRecordForNonTrans(TaxLineConfigHeaderVb vObject) throws RuntimeCustomException {
		List<TaxLineConfigHeaderVb> collTemp = null;
		List<TaxLinkVb> taxLinkVbCol = null;
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
		vObject.setTaxLineStatus(Constants.STATUS_ZERO);
		vObject.setMaker(intCurrentUserId);
		vObject.setVerifier(intCurrentUserId);
        if("F".equalsIgnoreCase(vObject.getTaxChargeType()))
        	vObject.setTierType("NA");
		retVal = doInsertionApprTaxLineHeaders(vObject);
		if (retVal != Constants.SUCCESSFUL_OPERATION) {
			exceptionCode = getResultObject(retVal);
			throw buildRuntimeCustomException(exceptionCode);
		} else {
			exceptionCode = getResultObject(Constants.SUCCESSFUL_OPERATION);
		}
		if (vObject.getTaxLineConfigDetaillst() != null && vObject.getTaxLineConfigDetaillst().size() > 0) {
			exceptionCode = taxLineConfigDetailsDao.deleteAndInsertApprTaxDetail(vObject);
		}

		if (vObject.getTaxLinklst() != null && vObject.getTaxLinklst().size() > 0) {
			exceptionCode = deleteAndInsertTaxLink(vObject);
		}
		if (exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION) {
			throw buildRuntimeCustomException(exceptionCode);
		}
		exceptionCode = getResultObject(Constants.SUCCESSFUL_OPERATION);
		writeAuditLog(vObject, null);
		if (exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION) {
			exceptionCode = getResultObject(Constants.AUDIT_TRAIL_ERROR);
			throw buildRuntimeCustomException(exceptionCode);
		}
		try {
			taxExpandedJar(vObject);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return exceptionCode;
	}

	@Override
	public ExceptionCode doInsertRecordForNonTrans(TaxLineConfigHeaderVb vObject) throws RuntimeCustomException {
		List<TaxLineConfigHeaderVb> collTemp = null;
		List<TaxLinkVb> colltemp = null;
		List<TaxLinkVb> taxLinkVbCol = null;
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
		vObject.setTaxLineStatus(Constants.STATUS_ZERO);
		vObject.setMaker(intCurrentUserId);
		vObject.setVerifier(intCurrentUserId);
		retVal = doInsertionPendTaxLineHeaders(vObject);
		if (retVal != Constants.SUCCESSFUL_OPERATION) {
			exceptionCode = getResultObject(retVal);
			throw buildRuntimeCustomException(exceptionCode);
		} else {
			exceptionCode = getResultObject(Constants.SUCCESSFUL_OPERATION);
		}
		if (vObject.getTaxLineConfigDetaillst() != null && vObject.getTaxLineConfigDetaillst().size() > 0) {
			exceptionCode = taxLineConfigDetailsDao.deleteAndInsertPendTaxDetail(vObject);
		}
		if (vObject.getTaxLinklst() != null && vObject.getTaxLinklst().size() > 0) {
			exceptionCode = deleteAndInsertTaxLinkPend(vObject);
		}
		if (exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION) {
			throw buildRuntimeCustomException(exceptionCode);
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
	public ExceptionCode doUpdateApprRecordForNonTrans(TaxLineConfigHeaderVb vObject) throws RuntimeCustomException {
		List<TaxLineConfigHeaderVb> collTemp = null;
		TaxLineConfigHeaderVb taxLineHeaderVb = null;
		List<TaxLinkVb> taxLinkVbCol = null;
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
		taxLineHeaderVb = ((ArrayList<TaxLineConfigHeaderVb>) collTemp).get(0);
		// Even if record is not there in Appr. table reject the record
		if (collTemp.size() == 0) {
			exceptionCode = getResultObject(Constants.ATTEMPT_TO_MODIFY_UNEXISTING_RECORD);
			throw buildRuntimeCustomException(exceptionCode);
		}
		vObject.setRecordIndicator(Constants.STATUS_ZERO);
		vObject.setVerifier(getIntCurrentUserId());
		vObject.setDateCreation(taxLineHeaderVb.getDateCreation());
		retVal = doUpdateApprHeader(vObject);
		if (retVal != Constants.SUCCESSFUL_OPERATION) {
			exceptionCode = getResultObject(retVal);
			throw buildRuntimeCustomException(exceptionCode);
		} else {
			exceptionCode = getResultObject(Constants.SUCCESSFUL_OPERATION);
		}
		if (vObject.getTaxLineConfigDetaillst() != null && vObject.getTaxLineConfigDetaillst().size() > 0) {
			exceptionCode = taxLineConfigDetailsDao.deleteAndInsertApprTaxDetail(vObject);
		}
		if (vObject.getTaxLinklst() != null && vObject.getTaxLinklst().size() > 0) {
			exceptionCode = deleteAndInsertTaxLink(vObject);
		}
		if (exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION) {
			throw buildRuntimeCustomException(exceptionCode);
		}
		String systemDate = getSystemDate();
		vObject.setDateLastModified(systemDate);

		writeAuditLog(vObject, taxLineHeaderVb);
		if (exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION) {
			exceptionCode = getResultObject(Constants.AUDIT_TRAIL_ERROR);
			throw buildRuntimeCustomException(exceptionCode);
		}
		try {
			taxExpandedJar(vObject);
		} catch (IOException e) {
			e.printStackTrace();
		}
		exceptionCode = getResultObject(Constants.SUCCESSFUL_OPERATION);
		return exceptionCode;
	}

	@Override
	public ExceptionCode doUpdateRecordForNonTrans(TaxLineConfigHeaderVb vObject) throws RuntimeCustomException {
		List<TaxLineConfigHeaderVb> collTemp = null;
		List<TaxLinkVb> taxLinkVbCol = null;
		TaxLineConfigHeaderVb taxLineHeaderVb = null;
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
			taxLineHeaderVb = ((ArrayList<TaxLineConfigHeaderVb>) collTemp).get(0);
			vObject.setDateCreation(taxLineHeaderVb.getDateCreation());
			if (taxLineHeaderVb.getRecordIndicator() == Constants.STATUS_INSERT) {
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
			if (vObject.getTaxLineConfigDetaillst() != null && vObject.getTaxLineConfigDetaillst().size() > 0) {
				taxLineConfigDetailsDao.deleteAndInsertPendTaxDetail(vObject);
			}
			if (vObject.getTaxLinklst() != null && vObject.getTaxLinklst().size() > 0) {
				exceptionCode = deleteAndInsertTaxLinkPend(vObject);
			}
			if (exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION) {
				throw buildRuntimeCustomException(exceptionCode);
			}

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
				taxLineHeaderVb = ((ArrayList<TaxLineConfigHeaderVb>) collTemp).get(0);
				vObject.setDateCreation(taxLineHeaderVb.getDateCreation());
			}
			vObject.setDateCreation(taxLineHeaderVb.getDateCreation());
			// Record is there in approved, but not in pending. So add it to pending
			vObject.setVerifier(0);
			vObject.setRecordIndicator(Constants.STATUS_UPDATE);
			retVal = doInsertionPendTaxLineHeadersDc(vObject);
			if (retVal != Constants.SUCCESSFUL_OPERATION) {
				exceptionCode = getResultObject(retVal);
				throw buildRuntimeCustomException(exceptionCode);
			}
			if (vObject.getTaxLineConfigDetaillst() != null && vObject.getTaxLineConfigDetaillst().size() > 0) {
				taxLineConfigDetailsDao.deleteAndInsertPendTaxDetail(vObject);
			}
			if (vObject.getTaxLinklst() != null && vObject.getTaxLinklst().size() > 0) {
				exceptionCode = deleteAndInsertTaxLinkPend(vObject);
			}
			if (exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION) {
				throw buildRuntimeCustomException(exceptionCode);
			}
			return getResultObject(Constants.SUCCESSFUL_OPERATION);
		}
	}

	@Override
	@Transactional(rollbackForClassName = { "com.vision.exception.RuntimeCustomException" })
	public ExceptionCode doRejectForTransaction(TaxLineConfigHeaderVb vObject) throws RuntimeCustomException {
		strErrorDesc = "";
		strCurrentOperation = Constants.APPROVE;
		setServiceDefaults();
		return doRejectRecord(vObject);
	}

	@Override
	public ExceptionCode doRejectRecord(TaxLineConfigHeaderVb vObject) throws RuntimeCustomException {
		TaxLineConfigHeaderVb taxLineHeaderVb = null;
		List<TaxLineConfigHeaderVb> collTemp = null;
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
			taxLineHeaderVb = ((ArrayList<TaxLineConfigHeaderVb>) collTemp).get(0);
			retVal = deleteTaxLineHeaderPend(vObject);

			List<TaxLineConfigDetailsVb> collTempDet = null;
			collTempDet = taxLineConfigDetailsDao.getTaxLineConfigDetails(vObject, 1);
			if (collTempDet != null && collTempDet.size() > 0) {
				retVal = taxLineConfigDetailsDao.deleteTaxDetailsPend(vObject);
			}
			List<TaxConfigTierVb> collTempTier = null;
			collTempTier = taxConfigTierDao.getTaxConfigTierByGroup(vObject, 1);
			if (collTempTier != null && collTempTier.size() > 0) {
				int delCnt = taxConfigTierDao.deleteTaxTierPend(vObject);
			}
			List<TaxLinkVb> taxLinkVbCol = null;
			TaxLinkVb taxLinkVb = new TaxLinkVb();
			taxLinkVb.setCountry(vObject.getCountry());
			taxLinkVb.setLeBook(vObject.getLeBook());
			taxLinkVb.setTaxLineId(vObject.getTaxLineId());
			taxLinkVbCol = getQueryTaxLink(taxLinkVb, Constants.SUCCESSFUL_OPERATION);
			if (taxLinkVbCol != null && taxLinkVbCol.size() > 0) {
				retVal = doDeleteTaxLinkPend(vObject);
			}
			exceptionCode = getResultObject(Constants.SUCCESSFUL_OPERATION);
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
	public ExceptionCode doBulkReject(List<TaxLineConfigHeaderVb> vObjects) throws RuntimeCustomException {

		strErrorDesc = "";
		strCurrentOperation = Constants.REJECT;
		setServiceDefaults();
		ExceptionCode exceptionCode = null;
		try {
			boolean foundFlag = false;
			for (TaxLineConfigHeaderVb object : vObjects) {

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
	public void taxExpandedJar(TaxLineConfigHeaderVb vObject) throws IOException {
		String execsPath = commonDao.findVisionVariableValue("RA_EXECS_PATH");
		String logPath = commonDao.findVisionVariableValue("RA_SERV_LOGPATH");
		
		if(!ValidationUtil.isValid(execsPath))
			logger.info("RATaxExpanded Jar Path not maintained");
		if(!ValidationUtil.isValid(logPath))
			logger.info("RATaxExpanded  Logs Path not maintained");
		
		String logFileName = "RATAXEXP_"+vObject.getCountry()+"_"+vObject.getLeBook()+"_"+vObject.getTaxLineId()+"_"+getCurrentDateOnly();
		String fulLogFileName = logPath + logFileName + ".log";
		File file = new File(fulLogFileName);
		
		Process proc = Runtime.getRuntime().exec("java -jar " + execsPath + "RATaxExpanded.jar Y " +fulLogFileName+"");
		BufferedReader reader =new BufferedReader(new InputStreamReader(proc.getInputStream()));
		while ((reader.readLine()) != null) {}
		try {
			proc.waitFor();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int outputStatus = proc.exitValue();
	}
	private String getCurrentDateOnly() {
		String currentDate = "";
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Calendar cal = Calendar.getInstance();
		currentDate = dateFormat.format(cal.getTime());
		return currentDate;
	}
	@Override
	@Transactional(rollbackForClassName = { "com.vision.exception.RuntimeCustomException" })
	public ExceptionCode doApproveForTransaction(TaxLineConfigHeaderVb vObject, boolean staticDelete)
			throws RuntimeCustomException {
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			
			strErrorDesc = "";
			strCurrentOperation = Constants.APPROVE;
			setServiceDefaults();
			exceptionCode =  doApproveRecord(vObject, staticDelete);
			taxExpandedJar(vObject);
		}catch(Exception e) {
			exceptionCode.setErrorMsg(e.getMessage());
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
		}
		return exceptionCode;
	}

	@Override
	public ExceptionCode doApproveRecord(TaxLineConfigHeaderVb vObject, boolean staticDelete)
			throws RuntimeCustomException {
		TaxLineConfigHeaderVb oldContents = null;
		TaxLineConfigDetailsVb oldContentsDet = null;
		TaxConfigTierVb oldContentsTier = null;
		TaxLinkVb oldContentsLink = null;

		TaxLineConfigHeaderVb taxLineHeaderVb = null;
		TaxLineConfigDetailsVb taxLineDetVb = null;
		TaxConfigTierVb taxLineTierVb = null;
		TaxLinkVb taxLinkVb = null;

		List<TaxLineConfigHeaderVb> collTemp = null;
		List<TaxLineConfigDetailsVb> collTempDet = null;
		List<TaxLineConfigDetailsVb> collTempDetAppr = null;
		List<TaxConfigTierVb> collTempTier = null;
		List<TaxConfigTierVb> collTempTierAppr = null;
		List<TaxLinkVb> collTempLink = null;
		List<TaxLinkVb> collTempLinkAppr = null;
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
			taxLineHeaderVb = ((ArrayList<TaxLineConfigHeaderVb>) collTemp).get(0);
			if (taxLineHeaderVb.getMaker() == getIntCurrentUserId()) {
				exceptionCode = getResultObject(Constants.MAKER_CANNOT_APPROVE);
				throw buildRuntimeCustomException(exceptionCode);
			}
			if (vObject.getRecordIndicator() == 1) {

				String taxHeader = getTaxHeaderPend(vObject.getCountry(), vObject.getLeBook(), vObject.getTaxLineId());
				taxLineHeaderVb.setCountry(vObject.getCountry());
				taxLineHeaderVb.setLeBook(vObject.getLeBook());
				taxLineHeaderVb.setTaxLineId(vObject.getTaxLineId());

				collTemp = doSelectPendingRecord(taxLineHeaderVb);

				if (collTemp != null || collTemp.size() > 0) {
					exceptionCode = getResultObject(Constants.ERRONEOUS_OPERATION);
					exceptionCode.setErrorMsg("Tax header/Tax header is in pending.Kindly Approve");
					throw buildRuntimeCustomException(exceptionCode);
				}
			} else {

				collTempDet = taxLineConfigDetailsDao.getTaxLineConfigDetails(taxLineHeaderVb,
						Constants.STATUS_PENDING);
				if (collTempDet != null && collTempDet.size() > 0) {
					taxLineHeaderVb.setTaxLineConfigDetaillst(collTempDet);
				}
				if (!"F".equalsIgnoreCase(taxLineHeaderVb.getTaxChargeType())) {
					collTempTier = taxConfigTierDao.getTaxConfigTierByGroup(taxLineHeaderVb, Constants.STATUS_PENDING); // SBU
					if (collTempTier != null && collTempTier.size() > 0) {
						taxLineHeaderVb.setTaxTierlst(collTempTier);
					}
				}
				taxLinkVb = new TaxLinkVb();
				taxLinkVb.setCountry(vObject.getCountry());
				taxLinkVb.setLeBook(vObject.getLeBook());
				taxLinkVb.setTaxLineId(vObject.getTaxLineId());
				collTempLink = getQueryTaxLink(taxLinkVb, Constants.STATUS_PENDING);
				if (collTempLink != null && collTempLink.size() > 0) {
					taxLineHeaderVb.setTaxLinklst(collTempLink);
				}

				if (taxLineHeaderVb.getRecordIndicator() != Constants.STATUS_INSERT) {
					collTemp = selectApprovedRecord(vObject);
					if (collTemp == null || collTemp.isEmpty()) {
						exceptionCode = getResultObject(Constants.ERRONEOUS_OPERATION);
						throw buildRuntimeCustomException(exceptionCode);
					}
					oldContents = ((ArrayList<TaxLineConfigHeaderVb>) collTemp).get(0);

					collTempDetAppr = taxLineConfigDetailsDao.getTaxLineConfigDetails(taxLineHeaderVb, 0);
					if (collTempDetAppr != null && collTempDetAppr.size() > 0) {
						oldContentsDet = ((ArrayList<TaxLineConfigDetailsVb>) collTempDetAppr).get(0);
					}

					collTempTierAppr = taxConfigTierDao.getTaxConfigTierByGroup(taxLineHeaderVb, 0);
					if (collTempTierAppr != null && collTempTierAppr.size() > 0) {
						oldContentsTier = ((ArrayList<TaxConfigTierVb>) collTempTierAppr).get(0);
					}
					taxLinkVb = new TaxLinkVb();
					taxLinkVb.setCountry(vObject.getCountry());
					taxLinkVb.setLeBook(vObject.getLeBook());
					taxLinkVb.setTaxLineId(vObject.getTaxLineId());
					collTempLinkAppr = getQueryTaxLink(taxLinkVb, Constants.STATUS_ZERO);
					if (collTempLinkAppr != null && collTempLinkAppr.size() > 0) {
						oldContentsLink = ((ArrayList<TaxLinkVb>) collTempLinkAppr).get(0);
					}
				}

				if (taxLineHeaderVb.getRecordIndicator() == Constants.STATUS_INSERT) { // Add authorization
					// Write the contents of the Pending table record to the Approved table
					taxLineHeaderVb.setRecordIndicator(Constants.STATUS_ZERO);
					taxLineHeaderVb.setVerifier(getIntCurrentUserId());

					retVal = doInsertionApprTaxLineHeaders(taxLineHeaderVb);
					if (retVal != Constants.SUCCESSFUL_OPERATION) {
						exceptionCode = getResultObject(retVal);
						throw buildRuntimeCustomException(exceptionCode);
					}

					if (taxLineHeaderVb.getTaxLineConfigDetaillst() != null
							&& taxLineHeaderVb.getTaxLineConfigDetaillst().size() > 0) {
						taxLineHeaderVb.getTaxLineConfigDetaillst().forEach(n -> {
							n.setRecordIndicator(vObject.getRecordIndicator());
							taxLineConfigDetailsDao.doInsertionApprTaxDetails(n);
						});
					}
					if (taxLineHeaderVb.getTaxTierlst() != null && taxLineHeaderVb.getTaxTierlst().size() > 0) {
						taxLineHeaderVb.getTaxTierlst().forEach(n -> {
							n.setRecordIndicator(vObject.getRecordIndicator());
							taxConfigTierDao.doInsertionApprTaxTier(n);
						});
					}
					if (taxLineHeaderVb.getTaxLinklst() != null && taxLineHeaderVb.getTaxLinklst().size() > 0) {
						taxLineHeaderVb.getTaxLinklst().forEach(n -> {
							n.setRecordIndicator(vObject.getRecordIndicator());
							doInsertionTaxLinkAppr(n);
						});

					}
					String systemDate = getSystemDate();
					vObject.setDateLastModified(systemDate);
					vObject.setDateCreation(systemDate);
					strApproveOperation = Constants.ADD;
				} else if (taxLineHeaderVb.getRecordIndicator() == Constants.STATUS_UPDATE) { // Modify authorization
					collTemp = selectApprovedRecord(vObject);
					if (collTemp == null || collTemp.isEmpty()) {
						exceptionCode = getResultObject(Constants.ERRONEOUS_OPERATION);
						throw buildRuntimeCustomException(exceptionCode);
					}

					// If record already exists in the approved table, reject the addition
					if (collTemp.size() > 0) {
						// retVal = doUpdateAppr(taxLineHeaderVb, MISConstants.ACTIVATE);
						taxLineHeaderVb.setVerifier(getIntCurrentUserId());
						taxLineHeaderVb.setRecordIndicator(Constants.STATUS_ZERO);
						retVal = doUpdateApprHeader(taxLineHeaderVb);
						if (retVal != Constants.SUCCESSFUL_OPERATION) {
							exceptionCode = getResultObject(retVal);
							throw buildRuntimeCustomException(exceptionCode);
						}
					}
					if (collTempTierAppr != null && collTempTierAppr.size() > 0) {
						taxConfigTierDao.deleteTaxTierAppr(taxLineHeaderVb);
					}
					if (collTempTier != null && !collTempTier.isEmpty()) {
						collTempTier.forEach(sbuPend -> {
							sbuPend.setRecordIndicator(vObject.getRecordIndicator());
							retVal = taxConfigTierDao.doInsertionApprTaxTier(sbuPend);
						});
					}
					if (collTempDetAppr != null && collTempDetAppr.size() > 0) {
						taxLineConfigDetailsDao.deleteTaxDetailsAppr(taxLineHeaderVb);
					}
					if (collTempDet != null && !collTempDet.isEmpty()) {
						collTempDet.forEach(glPend -> {
							glPend.setRecordIndicator(vObject.getRecordIndicator());
							retVal = taxLineConfigDetailsDao.doInsertionApprTaxDetails(glPend);
						});
					}
					if (collTempLinkAppr != null && collTempLinkAppr.size() > 0) {
						doDeleteTaxLinkAppr(taxLineHeaderVb);
					}
					if (collTempLink != null && collTempLink.size() > 0) {
						collTempLink.forEach(n -> {
							n.setRecordIndicator(vObject.getRecordIndicator());
							retVal = doInsertionTaxLinkAppr(n);
						});

					}
					// Modify the existing contents of the record in Approved table

					String systemDate = getSystemDate();
					vObject.setDateLastModified(systemDate);
					// Set the current operation to write to audit log
					strApproveOperation = Constants.MODIFY;
				} else if (taxLineHeaderVb.getRecordIndicator() == Constants.STATUS_DELETE){ // Delete authorization
					if(staticDelete){
						// Update the existing record status in the Approved table to delete 
						setStatus(taxLineHeaderVb, Constants.PASSIVATE);
						taxLineHeaderVb.setRecordIndicator(Constants.STATUS_ZERO);
						taxLineHeaderVb.setVerifier(getIntCurrentUserId());
						retVal = doUpdateApprHeader(taxLineHeaderVb);
						if (retVal != Constants.SUCCESSFUL_OPERATION){
							exceptionCode = getResultObject(retVal);
							throw buildRuntimeCustomException(exceptionCode);
						}
						setStatus(vObject, Constants.PASSIVATE);
						String systemDate = getSystemDate();
						vObject.setDateLastModified(systemDate);

					}
					else{
						// Delete the existing record from the Approved table 
						retVal = deleteTaxLineHeaderAppr(taxLineHeaderVb);
						if (retVal != Constants.SUCCESSFUL_OPERATION) {
							taxLineConfigDetailsDao.deleteTaxDetailsAppr(taxLineHeaderVb);
							taxConfigTierDao.deleteTaxTierAppr(taxLineHeaderVb);
							retVal = doDeleteTaxLinkAppr(taxLineHeaderVb);
						}
						if (retVal != Constants.SUCCESSFUL_OPERATION){
							exceptionCode = getResultObject(retVal);
							throw buildRuntimeCustomException(exceptionCode);
						}
						String systemDate = getSystemDate();
						vObject.setDateLastModified(systemDate);
					}
					// Set the current operation to write to audit log
					strApproveOperation = Constants.DELETE;
				}else {
					exceptionCode = getResultObject(Constants.INVALID_STATUS_FLAG_IN_DATABASE);
					throw buildRuntimeCustomException(exceptionCode);
				}

				// Delete the record from the Pending table
				retVal = deleteTaxLineHeaderPend(taxLineHeaderVb);
				if (retVal == Constants.SUCCESSFUL_OPERATION) {
					taxLineConfigDetailsDao.deleteTaxDetailsPend(taxLineHeaderVb);
					taxConfigTierDao.deleteTaxTierPend(taxLineHeaderVb);
					retVal = doDeleteTaxLinkPend(taxLineHeaderVb);
				}else {
					exceptionCode = getResultObject(retVal);
					throw buildRuntimeCustomException(exceptionCode);
				}
				// Set the internal status to Approved
				vObject.setInternalStatus(0);
				vObject.setRecordIndicator(Constants.STATUS_ZERO);
				if (taxLineHeaderVb.getRecordIndicator() == Constants.STATUS_DELETE && !staticDelete) {
					writeAuditLog(null, oldContents);
					vObject.setRecordIndicator(-1);
				} else
					writeAuditLog(taxLineHeaderVb, oldContents);
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
	@Transactional(rollbackForClassName = { "com.vision.exception.RuntimeCustomException" })
	public ExceptionCode bulkApprove(List<TaxLineConfigHeaderVb> vObjects, boolean staticDelete)
			throws RuntimeCustomException {
		strErrorDesc = "";
		strCurrentOperation = Constants.APPROVE;
		ExceptionCode exceptionCode = null;
		setServiceDefaults();
		try {
			boolean foundFlag = false;
			for (TaxLineConfigHeaderVb object : vObjects) {
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
	protected String getAuditString(TaxLineConfigHeaderVb vObject) {
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

			if (ValidationUtil.isValid(vObject.getTaxLineId()))
				strAudit.append("TAX_LINE_ID" + auditDelimiterColVal + vObject.getTaxLineId().trim());
			else
				strAudit.append("TAX_LINE_ID" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);

			if (ValidationUtil.isValid(vObject.getTaxLineDescription()))
				strAudit.append("TAX_LINE_DESCRIPTION" + auditDelimiterColVal + vObject.getTaxLineDescription().trim());
			else
				strAudit.append("TAX_LINE_DESCRIPTION" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);

			if (ValidationUtil.isValid(vObject.getEffectiveDate()))
				strAudit.append("EFFECTIVE_DATE" + auditDelimiterColVal + vObject.getEffectiveDate().trim());
			else
				strAudit.append("EFFECTIVE_DATE" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);

			if (ValidationUtil.isValid(vObject.getTaxCcy()))
				strAudit.append("TAX_CCY" + auditDelimiterColVal + vObject.getTaxCcy().trim());
			else
				strAudit.append("TAX_CCY" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);

			if (ValidationUtil.isValid(vObject.getTaxBasis()))
				strAudit.append("TAX_BASIS" + auditDelimiterColVal + vObject.getTaxBasis().trim());
			else
				strAudit.append("TAX_BASIS" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);

			if (ValidationUtil.isValid(vObject.getTaxChargeType()))
				strAudit.append("TAX_CHARGE_TYPE" + auditDelimiterColVal + vObject.getTaxChargeType().trim());
			else
				strAudit.append("TAX_CHARGE_TYPE" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);

			if (ValidationUtil.isValid(vObject.getTierType()))
				strAudit.append("TIER_TYPE" + auditDelimiterColVal + vObject.getTierType().trim());
			else
				strAudit.append("TIER_TYPE" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);

			strAudit.append("TAX_LINE_STATUS_NT" + auditDelimiterColVal + vObject.getTaxLineStatusNT());
			strAudit.append(auditDelimiter);

			strAudit.append("TAX_LINE_STATUS" + auditDelimiterColVal + vObject.getTaxLineStatus());
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

	public int effectiveDateCheck(TaxLineConfigHeaderVb dObj, int status) {
		int cnt = 0;
		String query = "";
		try {
			if (status == 0) {
				query = " SELECT COUNT(1) FROM RA_MST_TAX_HEADER TAPPR "
						+ " WHERE TAPPR.COUNTRY = ? AND TAPPR.LE_BOOK = ? AND "
						+ " TAPPR.TAX_LINE_ID = ?  AND TAPPR.EFFECTIVE_DATE = ? ";
			} else {
				query = " SELECT COUNT(1) FROM RA_MST_TAX_HEADER_PEND TAPPR "
						+ " WHERE TAPPR.COUNTRY = ? AND TAPPR.LE_BOOK = ? AND  "
						+ " TAPPR.TAX_LINE_ID = ?  AND TAPPR.EFFECTIVE_DATE = ? ";
			}

			Object objParams[] = new Object[4];
			objParams[0] = new String(dObj.getCountry());
			objParams[1] = new String(dObj.getLeBook());
			objParams[2] = new String(dObj.getTaxLineId());
			objParams[3] = new String(dObj.getEffectiveDate());
			cnt = getJdbcTemplate().queryForObject(query, objParams, Integer.class);
			return cnt;
		} catch (Exception e) {
			return cnt;
		}
	}

	public int effectiveDateBusinessCheck(TaxLineConfigHeaderVb dObj) {
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

	public ExceptionCode callLineMergeProc(String country, String leBook, String businessLineId, String procedureId) {
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
			cs.setString(3, businessLineId);
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

	protected int doInsertionTaxLinkAppr(TaxLinkVb vObject) {
		try {
			String query = "Insert into  RA_MST_bL_TAX_LINK(COUNTRY,LE_BOOK,BUSINESS_LINE_ID,TAX_LINE_ID,"
					+ " ACTUAL_IE_POSTING_AT,ACTUAL_IE_POSTING,"
					+ " ACTUAL_IE_MATCH_RULE_AT,ACTUAL_IE_MATCH_RULE,LINK_STATUS_NT,LINK_STATUS,"
					+ " RECORD_INDICATOR_NT,RECORD_INDICATOR,MAKER,VERIFIER,DATE_LAST_MODIFIED,DATE_CREATION)"
					+ " VALUES(?, ?, ?, ?, ?,?, ?, ?, ?, ?, ?, ?,  ?, ?," + getDbFunction("SYSDATE") + ","
					+ getDbFunction("SYSDATE") + ")";
			Object[] args = { vObject.getCountry(), vObject.getLeBook(), vObject.getBusinessLineId(),
					vObject.getTaxLineId(), vObject.getPostingTypeAt(), vObject.getPostingType(),
					vObject.getMatchRuleAt(), vObject.getMatchRule(), vObject.getLinkStatusNt(),
					vObject.getLinkStatus(), vObject.getRecordIndicatorNt(), vObject.getRecordIndicator(),
					vObject.getMaker(), vObject.getVerifier() };
			getJdbcTemplate().update(query, args);
			return Constants.SUCCESSFUL_OPERATION;
		} catch (Exception e) {
			return Constants.ERRONEOUS_OPERATION;
		}

	}

	protected int doInsertionTaxLinkPend(TaxLinkVb vObject) {
		try {
			String query = "Insert into  RA_MST_bL_TAX_LINK_PEND(COUNTRY,LE_BOOK,BUSINESS_LINE_ID,TAX_LINE_ID,"
					+ " ACTUAL_IE_POSTING_AT,ACTUAL_IE_POSTING,"
					+ " ACTUAL_IE_MATCH_RULE_AT,ACTUAL_IE_MATCH_RULE,LINK_STATUS_NT,LINK_STATUS,"
					+ " RECORD_INDICATOR_NT,RECORD_INDICATOR,MAKER,VERIFIER,DATE_LAST_MODIFIED,DATE_CREATION)"
					+ " VALUES(?, ?, ?, ?, ?,?, ? , ?, ?, ?, ?, ?, ?, ?," + getDbFunction("SYSDATE") + ","
					+ getDbFunction("SYSDATE") + ")";
			Object[] args = { vObject.getCountry(), vObject.getLeBook(), vObject.getBusinessLineId(),
					vObject.getTaxLineId(), vObject.getPostingTypeAt(), vObject.getPostingType(),
					vObject.getMatchRuleAt(), vObject.getMatchRule(), vObject.getLinkStatusNt(),
					vObject.getLinkStatus(), vObject.getRecordIndicatorNt(), vObject.getRecordIndicator(),
					vObject.getMaker(), vObject.getVerifier() };
			getJdbcTemplate().update(query, args);
			return Constants.SUCCESSFUL_OPERATION;
		} catch (Exception e) {
			return Constants.ERRONEOUS_OPERATION;
		}

	}

	protected int doDeleteTaxLinkAppr(TaxLineConfigHeaderVb vObject) {
		String query = "Delete from RA_MST_bL_TAX_LINK where Country = ? and LE_Book = ? and TAX_LINE_ID = ? ";
		Object[] args = { vObject.getCountry(), vObject.getLeBook(), vObject.getTaxLineId() };
		return getJdbcTemplate().update(query, args);

	}

	protected int doDeleteTaxLinkPend(TaxLineConfigHeaderVb vObject) {
		String query = "Delete from RA_MST_bL_TAX_LINK_PEND where Country = ? and LE_Book = ? and TAX_LINE_ID = ? ";
		Object[] args = { vObject.getCountry(), vObject.getLeBook(), vObject.getTaxLineId() };
		return getJdbcTemplate().update(query, args);

	}

	public List<TaxLinkVb> getQueryTaxLink(TaxLinkVb vObject, int intStatus) {
		List<TaxLinkVb> collTemp = null;
		final int intKeyFieldsCount = 3;
		String strQuery = "";
		try {
			if (!vObject.isVerificationRequired() || vObject.isReview()) {
				intStatus = 0;
			}
			if (intStatus == Constants.STATUS_ZERO) {
				strQuery = "SELECT COUNTRY,LE_BOOK,BUSINESS_LINE_ID,"
						+ " (SELECT bUSINESS_LINE_DESCRIPTION   FROM RA_MST_BUSINESS_LINE_HEADER "
						+ "	WHERE COUNTRY = TAPPR.COUNTRY " + "	AND LE_BOOK = TAPPR.LE_BOOK "
						+ "	AND BUSINESS_LINE_ID = TAPPR.BUSINESS_LINE_ID )BUSINESS_LINE_DESC, "
						+ " TAX_LINE_ID,ACTUAL_IE_POSTING," + " ACTUAL_IE_MATCH_RULE,LINK_STATUS,"
						+ " RECORD_INDICATOR,MAKER,VERIFIER,DATE_LAST_MODIFIED,DATE_CREATION"
						+ " FROM RA_MST_bL_TAX_LINK TAPPR WHERE TAPPR.COUNTRY = ? AND TAPPR.LE_BOOK = ? AND "
						+ " TAPPR.TAX_LINE_ID = ? ";

			} else {
				strQuery = "SELECT COUNTRY,LE_BOOK,BUSINESS_LINE_ID,"
						+ " (SELECT bUSINESS_LINE_DESCRIPTION   FROM RA_MST_BUSINESS_LINE_HEADER "
						+ "	WHERE COUNTRY = TPEND.COUNTRY " + "	AND LE_BOOK = TPEND.LE_BOOK "
						+ "	AND BUSINESS_LINE_ID = TPEND.BUSINESS_LINE_ID )BUSINESS_LINE_DESC, "
						+ " TAX_LINE_ID,ACTUAL_IE_POSTING," + " ACTUAL_IE_MATCH_RULE,LINK_STATUS,"
						+ " RECORD_INDICATOR,MAKER,VERIFIER,DATE_LAST_MODIFIED,DATE_CREATION"
						+ " FROM RA_MST_bL_TAX_LINK_PEND  TPEND WHERE TPEND.COUNTRY = ? AND TPEND.LE_BOOK = ? AND "
						+ " TPEND.TAX_LINE_ID = ?";
			}
			Object objParams[] = new Object[intKeyFieldsCount];
			objParams[0] = new String(vObject.getCountry());
			objParams[1] = new String(vObject.getLeBook());
			objParams[2] = new String(vObject.getTaxLineId());
			logger.info("Executing query");
			collTemp = getJdbcTemplate().query(strQuery.toString(), objParams, getTaxLinkMapper());
			return collTemp;
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("Error: getQueryResults Exception :   ");
			return null;
		}
	}

	protected RowMapper getTaxLinkMapper() {
		RowMapper mapper = new RowMapper() {
			@Override
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				TaxLinkVb vObject = new TaxLinkVb();
				vObject.setCountry(rs.getString("COUNTRY"));
				vObject.setLeBook(rs.getString("LE_BOOK"));
				vObject.setBusinessLineId(rs.getString("BUSINESS_LINE_ID"));
				vObject.setBusinessLineDesc(rs.getString("BUSINESS_LINE_DESC"));
				vObject.setTaxLineId(rs.getString("TAX_LINE_ID"));
				vObject.setPostingType(rs.getString("ACTUAL_IE_POSTING"));
				vObject.setMatchRule(rs.getString("ACTUAL_IE_MATCH_RULE"));
				vObject.setLinkStatus(rs.getInt("LINK_STATUS"));
				vObject.setRecordIndicator(rs.getInt("RECORD_INDICATOR"));
				vObject.setMaker(rs.getLong("MAKER"));
				vObject.setVerifier(rs.getLong("VERIFIER"));
				vObject.setDateLastModified(rs.getString("DATE_LAST_MODIFIED"));
				vObject.setDateCreation(rs.getString("DATE_CREATION"));
				return vObject;
			}
		};
		return mapper;
	}

	public int getCntforTaxLink(TaxLineConfigHeaderVb vObject, int status) {
		String tableName = "";
		if (status == Constants.STATUS_ZERO)
			tableName = "RA_MST_bL_TAX_LINK";
		else
			tableName = "RA_MST_bL_TAX_LINK_PEND";
		try {
			String sql = "SELECT COUNT(*) FROM " + tableName
					+ " TAPPR WHERE TAPPR.COUNTRY = ? AND TAPPR.LE_BOOK = ? AND " + " TAPPR.TAX_LINE_ID = ? ";
			Object objParams[] = new Object[3];
			objParams[0] = new String(vObject.getCountry());
			objParams[1] = new String(vObject.getLeBook());
			objParams[2] = new String(vObject.getTaxLineId());
			int retVal = getJdbcTemplate().queryForObject(sql, objParams, Integer.class);
			return retVal;
		} catch (Exception e) {
			return Constants.ERRONEOUS_OPERATION;
		}
	}

	public ExceptionCode deleteAndInsertTaxLink(TaxLineConfigHeaderVb taxLineHeaderVb) {
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			if (getCntforTaxLink(taxLineHeaderVb, Constants.STATUS_ZERO) > 0) {
				doDeleteTaxLinkAppr(taxLineHeaderVb);
			}
			for (TaxLinkVb taxLinkVb : taxLineHeaderVb.getTaxLinklst()) {
				taxLinkVb.setRecordIndicator(taxLineHeaderVb.getRecordIndicator());
				taxLinkVb.setCountry(taxLineHeaderVb.getCountry());
				taxLinkVb.setLeBook(taxLineHeaderVb.getLeBook());
				taxLinkVb.setTaxLineId(taxLineHeaderVb.getTaxLineId());
				int retVal = doInsertionTaxLinkAppr(taxLinkVb);
				if (retVal != Constants.SUCCESSFUL_OPERATION) {
					exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
					exceptionCode.setErrorMsg("Error While Inserting Tax Link");
					return exceptionCode;
				} else {
					exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
				}
			}
		} catch (Exception e) {
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg(e.getMessage());
		}
		return exceptionCode;

	}

	public ExceptionCode deleteAndInsertTaxLinkPend(TaxLineConfigHeaderVb taxLineHeaderVb) {
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			if (getCntforTaxLink(taxLineHeaderVb, 1) > 0) {
				doDeleteTaxLinkPend(taxLineHeaderVb);
			}
			for (TaxLinkVb taxLinkVb : taxLineHeaderVb.getTaxLinklst()) {
				taxLinkVb.setRecordIndicator(taxLineHeaderVb.getRecordIndicator());
				taxLinkVb.setCountry(taxLineHeaderVb.getCountry());
				taxLinkVb.setLeBook(taxLineHeaderVb.getLeBook());
				taxLinkVb.setTaxLineId(taxLineHeaderVb.getTaxLineId());
				int retVal = doInsertionTaxLinkPend(taxLinkVb);
				if (retVal != Constants.SUCCESSFUL_OPERATION) {
					exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
					exceptionCode.setErrorMsg("Error While Inserting Tax Link");
					return exceptionCode;
				} else {
					exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
				}
			}
		} catch (Exception e) {
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg(e.getMessage());
		}
		return exceptionCode;
	}

	@Override
	protected ExceptionCode doDeleteRecordForNonTrans(TaxLineConfigHeaderVb vObject) throws RuntimeCustomException {
		TaxLineConfigHeaderVb vObjectlocal = null;
		List<TaxLineConfigHeaderVb> collTemp = null;
		TaxLineConfigHeaderVb taxLineHeaderVb = null;
		TaxLinkVb taxLinkVb = null;
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
			vObjectlocal = ((ArrayList<TaxLineConfigHeaderVb>) collTemp).get(0);
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
			vObjectlocal = ((ArrayList<TaxLineConfigHeaderVb>) collTemp).get(0);
			vObjectlocal.setDateCreation(vObject.getDateCreation());
		}
		// vObjectlocal.setDateCreation(vObject.getDateCreation());
		vObjectlocal.setMaker(getIntCurrentUserId());
		vObjectlocal.setRecordIndicator(Constants.STATUS_DELETE);
		vObjectlocal.setVerifier(0);
		retVal = doInsertionPendTaxLineHeaders(vObjectlocal);
		if (retVal != Constants.SUCCESSFUL_OPERATION) {
			exceptionCode = getResultObject(retVal);
			throw buildRuntimeCustomException(exceptionCode);
		}
		List<TaxLineConfigDetailsVb> collTempDetPend = null;
		List<TaxConfigTierVb> collTempTierPend = null;
		List<TaxLinkVb> collTempLinkPend = null;

		collTempDetPend = taxLineConfigDetailsDao.getTaxLineConfigDetails(vObject, 0);
		if (collTempDetPend != null && collTempDetPend.size() > 0) {
			vObjectlocal.setTaxLineConfigDetaillst(collTempDetPend);
			collTempTierPend = taxConfigTierDao.getTaxConfigTierByGroup(vObject, 0);
			if (collTempTierPend != null && collTempTierPend.size() > 0)
				vObjectlocal.setTaxTierlst(collTempTierPend);
			exceptionCode = taxLineConfigDetailsDao.deleteAndInsertPendTaxDetail(vObjectlocal);
		}
		if(exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION) {
			exceptionCode = getResultObject(exceptionCode.getErrorCode());
			throw buildRuntimeCustomException(exceptionCode);
		}
		
		taxLinkVb = new TaxLinkVb();
		taxLinkVb.setCountry(vObject.getCountry());
		taxLinkVb.setLeBook(vObject.getLeBook());
		taxLinkVb.setTaxLineId(vObject.getTaxLineId());
		collTempLinkPend = getQueryTaxLink(taxLinkVb, 0);

		if (collTempLinkPend != null && collTempLinkPend.size() > 0) {
			vObjectlocal.setTaxLinklst(collTempLinkPend);
			exceptionCode = deleteAndInsertTaxLinkPend(vObjectlocal);
		}
		if (exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION) {
			exceptionCode = getResultObject(exceptionCode.getErrorCode());
			throw buildRuntimeCustomException(exceptionCode);
		}
		vObject.setRecordIndicator(Constants.STATUS_DELETE);
		vObject.setVerifier(0);
		return getResultObject(Constants.SUCCESSFUL_OPERATION);
	}

	@Override
	protected ExceptionCode doDeleteApprRecordForNonTrans(TaxLineConfigHeaderVb vObject) throws RuntimeCustomException {
		List<TaxLineConfigHeaderVb> collTemp = null;
		ExceptionCode exceptionCode = null;
		strApproveOperation = Constants.DELETE;
		strErrorDesc = "";
		strCurrentOperation = Constants.DELETE;
		setServiceDefaults();
		TaxLineConfigHeaderVb vObjectlocal = null;
		TaxLineConfigHeaderVb taxLineHeaderVb = null;
		TaxLinkVb taxLinkVb = null;
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
			int intStaticDeletionFlag = getStatus(((ArrayList<TaxLineConfigHeaderVb>) collTemp).get(0));
			if (intStaticDeletionFlag == Constants.PASSIVATE) {
				exceptionCode = getResultObject(Constants.CANNOT_DELETE_AN_INACTIVE_RECORD);
				throw buildRuntimeCustomException(exceptionCode);
			}
		} else {
			exceptionCode = getResultObject(Constants.ATTEMPT_TO_DELETE_UNEXISTING_RECORD);
			throw buildRuntimeCustomException(exceptionCode);
		}
		vObjectlocal = ((ArrayList<TaxLineConfigHeaderVb>) collTemp).get(0);
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
			String systemDate = getSystemDate();
			vObject.setDateLastModified(systemDate);
		} else {
			// delete the record from the Approve Table
			retVal = deleteTaxLineHeaderAppr(vObjectlocal);
			if (retVal != Constants.SUCCESSFUL_OPERATION) {
				exceptionCode = getResultObject(retVal);
				throw buildRuntimeCustomException(exceptionCode);
			}
//			vObject.setRecordIndicator(-1);
			String systemDate = getSystemDate();
			vObject.setDateLastModified(systemDate);
			List<TaxLineConfigDetailsVb> collTempDetPend = null;
			List<TaxConfigTierVb> collTempTierPend = null;
			List<TaxLinkVb> collTempLinkPend = null;

			collTempDetPend = taxLineConfigDetailsDao.getTaxLineConfigDetails(vObject, 0);
			if (collTempDetPend != null && collTempDetPend.size() > 0) {
				int delCnt = taxLineConfigDetailsDao.deleteTaxDetailsAppr(vObjectlocal);
				if (delCnt != Constants.SUCCESSFUL_OPERATION) {
					exceptionCode = getResultObject(delCnt);
					throw buildRuntimeCustomException(exceptionCode);
				}
			}
			collTempTierPend = taxConfigTierDao.getTaxConfigTierByGroup(vObject, 0);
			if (collTempTierPend != null && collTempTierPend.size() > 0) {
				int delCnt = taxConfigTierDao.deleteTaxTierAppr(vObjectlocal);
				if (delCnt != Constants.SUCCESSFUL_OPERATION) {
					exceptionCode = getResultObject(delCnt);
					throw buildRuntimeCustomException(exceptionCode);
				}
			}
			taxLinkVb = new TaxLinkVb();
			taxLinkVb.setCountry(vObject.getCountry());
			taxLinkVb.setLeBook(vObject.getLeBook());
			taxLinkVb.setTaxLineId(vObject.getTaxLineId());
			collTempLinkPend = getQueryTaxLink(taxLinkVb, 0);

			if (collTempLinkPend != null && collTempLinkPend.size() > 0) {
				int delCnt = doDeleteTaxLinkAppr(vObjectlocal);
				if (delCnt != Constants.SUCCESSFUL_OPERATION) {
					exceptionCode = getResultObject(delCnt);
					throw buildRuntimeCustomException(exceptionCode);
				}
			}
		}
		if (vObject.isStaticDelete()) {
			setStatus(vObjectlocal, Constants.STATUS_ZERO);
			setStatus(vObject, Constants.PASSIVATE);
			writeAuditLog(vObject, vObjectlocal);
		} else {
			writeAuditLog(null, vObject);
			vObject.setRecordIndicator(-1);
		}
//		if(exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION){
//			exceptionCode = getResultObject(Constants.AUDIT_TRAIL_ERROR);
//			throw buildRuntimeCustomException(exceptionCode);
//		}
		exceptionCode = getResultObject(Constants.SUCCESSFUL_OPERATION);
		return exceptionCode;
	}

	public String getTaxHeaderPend(String country, String leBook, String taxLineId) {
		Object args[] = { country, leBook, taxLineId };
		String orginalQuery = "SELECT *  from RA_MST_TAX_HEADER WHERE COUNTRY= ? AND LE_BOOK= ?  AND TAX_LINE_ID = ?";
		return getJdbcTemplate().queryForObject(orginalQuery, args, String.class);
	}
}