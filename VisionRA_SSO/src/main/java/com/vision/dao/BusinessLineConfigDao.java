package com.vision.dao;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.UncategorizedSQLException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.vision.authentication.CustomContextHolder;
import com.vision.exception.ExceptionCode;
import com.vision.exception.RuntimeCustomException;
import com.vision.util.CommonUtils;
import com.vision.util.Constants;
import com.vision.util.ValidationUtil;
import com.vision.vb.BlReconRuleVb;
import com.vision.vb.BusinessLineGLVb;
import com.vision.vb.BusinessLineHeaderVb;
import com.vision.vb.FeesConfigHeaderVb;
import com.vision.vb.SmartSearchVb;
import com.vision.vb.TransLineHeaderVb;

@Component
public class BusinessLineConfigDao extends AbstractDao<BusinessLineHeaderVb> {
	@Autowired
	BusinessLineConfigGLDao businessLineConfigGLDao;

	@Autowired
	BlReconRuleDao blReconRuleDao;

	@Autowired
	CommonDao commonDao;
	
	@Autowired
	TaxBlReconRuleDao taxBlReconRuleDao;

	@Autowired
	FeesConfigHeadersDao feesConfigHeadersDao;

	String ieTypeDescAppr = "(SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB= 7013 AND ALPHA_SUB_TAB = TAPPR.IE_TYPE) IE_TYPE_DESC, ";
	String ieTypeDescPend = "(SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB= 7013 AND ALPHA_SUB_TAB = TPEND.IE_TYPE) IE_TYPE_DESC, ";

	String postingTypeDescAppr = "(SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB= 7007 AND ALPHA_SUB_TAB = TAPPR.ACTUAL_IE_POSTING) ACTUAL_IE_POSTING_DESC, ";
	String postingTypeDescPend = "(SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB= 7007 AND ALPHA_SUB_TAB = TPEND.ACTUAL_IE_POSTING) ACTUAL_IE_POSTING_DESC, ";

	String macthRuleDescAppr = "(SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB= 7008 AND ALPHA_SUB_TAB = TAPPR.ACTUAL_IE_MATCH_RULE) ACTUAL_IE_MATCH_RULE_DESC, ";
	String macthRuleDescPend = "(SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB= 7008 AND ALPHA_SUB_TAB = TPEND.ACTUAL_IE_MATCH_RULE) ACTUAL_IE_MATCH_RULE_DESC, ";

	@Override
	protected RowMapper getMapper() {
		RowMapper mapper = new RowMapper() {
			@Override
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				BusinessLineHeaderVb vObject = new BusinessLineHeaderVb();
				vObject.setCountry(rs.getString("COUNTRY"));
				vObject.setLeBook(rs.getString("LE_BOOK"));
				vObject.setBusinessLineId(rs.getString("BUSINESS_LINE_ID"));
				vObject.setBusinessLineDescription(rs.getString("BUSINESS_LINE_DESCRIPTION"));
				vObject.setTransLineType(rs.getString("TRANS_LINE_TYPE"));
				vObject.setTransLineTypeDesc(rs.getString("TRANS_LINE_TYPE_DESC"));
				vObject.setTransLineId(rs.getString("TRANS_LINE_ID"));
				vObject.setTransLineIdDesc(rs.getString("TRANS_LINE_DESCRIPTION"));
				vObject.setBusinessLineStatus(rs.getInt("BUSINESS_LINE_STATUS"));
				vObject.setBusinessLineStatusDesc(rs.getString("BUSINESS_LINE_STATUS_DESC"));
				vObject.setRecordIndicator(rs.getInt("RECORD_INDICATOR"));
				vObject.setRecordIndicatorDesc(rs.getString("RECORD_INDICATOR_Desc"));
				vObject.setMaker(rs.getInt("MAKER"));
				vObject.setMakerName(rs.getString("MAKER_NAME"));
				vObject.setVerifier(rs.getInt("VERIFIER"));
				vObject.setVerifierName(rs.getString("VERIFIER_NAME"));
				vObject.setDateLastModified(rs.getString("DATE_LAST_MODIFIED"));
				vObject.setDateCreation(rs.getString("DATE_CREATION"));
				vObject.setBusinessLineTypeDesc(rs.getString("BUSINESS_LINE_TYPE_DESC"));
				vObject.setFeeLineCount(rs.getString("FEE_LINE_COUNT"));
				if (ValidationUtil.isValid(rs.getString("FEECALC_TIMESTAMP_FLAG")))
					vObject.setFeeCalcTimeStampFlag(rs.getString("FEECALC_TIMESTAMP_FLAG"));
				else
					vObject.setFeeCalcTimeStampFlag("N");
				vObject.setIncomeExpenseType(rs.getString("IE_TYPE"));
				vObject.setIncomeExpenseTypeDesc(rs.getString("IE_TYPE_DESC"));
				return vObject;
			}
		};
		return mapper;
	}

	protected RowMapper getDetailMapper() {
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
//				vObject.setTranLineGrp(rs.getString("TRAN_LINE_GRP"));
//				vObject.setTranLineGrpDesc(rs.getString("TRAN_LINE_GRP_DESC"));
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
				vObject.setFeeLineCount(rs.getString("Fee_line_Count"));
				if (ValidationUtil.isValid(rs.getString("FEECALC_TIMESTAMP_FLAG")))
					vObject.setFeeCalcTimeStampFlag(rs.getString("FEECALC_TIMESTAMP_FLAG"));
				else
					vObject.setFeeCalcTimeStampFlag("N");
				vObject.setBusinessLineTypeDesc(rs.getString("BUSINESS_LINE_TYPE_DESC"));
				vObject.setIncomeExpenseTypeDesc(rs.getString("IE_TYPE_DESC"));
				vObject.setActualIePostingDesc(rs.getString("ACTUAL_IE_POSTING_DESC"));
				vObject.setActualIeMatchRuleDesc(rs.getString("ACTUAL_IE_MATCH_RULE_DESC"));
				return vObject;
			}
		};
		return mapper;
	}

	@Override
	public List<BusinessLineHeaderVb> getQueryResults(BusinessLineHeaderVb dObj, int intStatus) {
		List<BusinessLineHeaderVb> collTemp = null;
		final int intKeyFieldsCount = 3;
		setServiceDefaults();
		String strQueryAppr = null;
		String strQueryPend = null;
		String tranBusinessLineIdAppr = "";
		String tranBusinessLineIdPend = "";
		if (ValidationUtil.isValid(dObj.getBusinessLineId())) {
			tranBusinessLineIdAppr = " AND TAPPR.BUSINESS_LINE_ID = ? ";
			tranBusinessLineIdPend = " AND TPEND.BUSINESS_LINE_ID = ? ";
		} else if (ValidationUtil.isValid(dObj.getTransLineId())) {
			tranBusinessLineIdAppr = " AND TAPPR.TRANS_LINE_ID = ? ";
			tranBusinessLineIdPend = " AND TPEND.TRANS_LINE_ID = ? ";
		}
		strQueryAppr = new String(
				"SELECT TAppr.COUNTRY, TAppr.LE_BOOK, TAppr.BUSINESS_LINE_ID,TAppr.BUSINESS_LINE_DESCRIPTION,TAppr.TRANS_LINE_TYPE,   "
						+ " TAppr.TRANS_LINE_ID," +
						/*
						 * " CASE WHEN TAppr.TRANS_LINE_TYPE='P' THEN T2.TRANS_LINE_PROD_GRP ELSE T2.TRANS_LINE_SERV_GRP END TRAN_LINE_GRP, "
						 * + " CASE WHEN TAppr.TRANS_LINE_TYPE='P' THEN "+
						 * " (SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB =T2.TRANS_LINE_PROD_GRP_AT AND ALPHA_SUB_TAB= T2.TRANS_LINE_PROD_GRP) "
						 * + "  ELSE "+
						 * "  (SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB =T2.TRANS_LINE_SERV_GRP_AT AND ALPHA_SUB_TAB= T2.TRANS_LINE_SERV_GRP) "
						 * + " END TRAN_LINE_GRP_DESC,
						 */ "TAppr.FEECALC_TIMESTAMP_FLAG,"
						+ " TAppr.BUSINESS_LINE_TYPE,(SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB =TAppr.BUSINESS_LINE_TYPE_AT AND ALPHA_SUB_TAB= TAppr.BUSINESS_LINE_TYPE) BUSINESS_LINE_TYPE_DESC, "
						+ " TAppr.IE_TYPE," + ieTypeDescAppr + "TAppr.ACTUAL_IE_POSTING," + postingTypeDescAppr
						+ " TAppr.ACTUAL_IE_MATCH_RULE , " + macthRuleDescAppr + " "
						+ " TAppr.BUSINESS_LINE_STATUS,T3.NUM_SUBTAB_DESCRIPTION BUSINESS_LINE_STATUS_DESC,TAppr.RECORD_INDICATOR,T1.NUM_SUBTAB_DESCRIPTION RECORD_INDICATOR_DESC,   "
						+ " TAppr. MAKER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "
						+ getDbFunction("NVL") + "(TAppr.MAKER,0) ) MAKER_NAME,   "
						+ " TAppr.VERIFIER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "
						+ getDbFunction("NVL") + "(TAppr.VERIFIER,0) ) VERIFIER_NAME,   " + " "
						+ getDbFunction("DATEFUNC") + "(TAppr.DATE_LAST_MODIFIED, 'dd-MM-yyyy " + getDbFunction("TIME")
						+ "') DATE_LAST_MODIFIED ," + getDbFunction("DATEFUNC") + "(TAppr.DATE_CREATION, 'dd-MM-yyyy "
						+ getDbFunction("TIME") + "') DATE_CREATION, "
						+" (SELECT COUNT(*) FROM ( "
						+" SELECT * FROM RA_MST_FEES_HEADER WHERE COUNTRY = TAppr.country AND LE_BOOK = TAppr.le_book AND BUSINESS_LINE_ID = TAppr.BUSINESS_LINE_ID UNION ALL "
						+" SELECT * FROM RA_MST_FEES_HEADER_PEND WHERE COUNTRY = TAppr.country AND LE_BOOK = TAppr.le_book AND BUSINESS_LINE_ID = TAppr.BUSINESS_LINE_ID)A) Fee_line_Count "
						+ " FROM RA_MST_BUSINESS_LINE_HEADER TAppr, NUM_SUB_TAB T1,NUM_SUB_TAB T3"
//				+ ",RA_MST_TRANS_LINE_HEADER T2 "
						+ " WHERE   " + " TAppr.COUNTRY =? AND TAPPR.LE_BOOK =? " + tranBusinessLineIdAppr + " AND "
						+ " T1.NUM_tab = TAppr.RECORD_INDICATOR_NT " + " and T1.NUM_sub_tab = TAppr.RECORD_INDICATOR " +
//				" AND TAPPR.TRANS_LINE_ID = T2.TRANS_LINE_ID"
						" AND T3.NUM_tab = TAppr.BUSINESS_LINE_STATUS_NT and T3.NUM_sub_tab = TAppr.BUSINESS_LINE_STATUS");

		strQueryPend = new String(
				"SELECT DISTINCT TPEND.COUNTRY, TPEND.LE_BOOK, TPEND.BUSINESS_LINE_ID,TPEND.BUSINESS_LINE_DESCRIPTION,TPEND.TRANS_LINE_TYPE,   "
						+ " TPEND.TRANS_LINE_ID," +
						/*
						 * " CASE WHEN TPEND.TRANS_LINE_TYPE='P' THEN T2.TRANS_LINE_PROD_GRP ELSE T2.TRANS_LINE_SERV_GRP END TRAN_LINE_GRP, "
						 * + " CASE WHEN TPEND.TRANS_LINE_TYPE='P' THEN "+
						 * " (SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB =T2.TRANS_LINE_PROD_GRP_AT AND ALPHA_SUB_TAB= T2.TRANS_LINE_PROD_GRP) "
						 * + "  ELSE "+
						 * "  (SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB =T2.TRANS_LINE_SERV_GRP_AT AND ALPHA_SUB_TAB= T2.TRANS_LINE_SERV_GRP) "
						 * + "  END TRAN_LINE_GRP_DESC"
						 */
						"TPEND.FEECALC_TIMESTAMP_FLAG, "
						+ " TPEND.BUSINESS_LINE_TYPE,(SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB =TPEND.BUSINESS_LINE_TYPE_AT AND ALPHA_SUB_TAB= TPEND.BUSINESS_LINE_TYPE) BUSINESS_LINE_TYPE_DESC, "
						+ " TPEND.IE_TYPE," + ieTypeDescPend + "TPEND.ACTUAL_IE_POSTING," + postingTypeDescPend
						+ " TPEND.ACTUAL_IE_MATCH_RULE , " + macthRuleDescPend + " "
						+ " TPEND.BUSINESS_LINE_STATUS,T3.NUM_SUBTAB_DESCRIPTION BUSINESS_LINE_STATUS_DESC,TPEND.RECORD_INDICATOR,T1.NUM_SUBTAB_DESCRIPTION RECORD_INDICATOR_DESC,   "
						+ " TPEND. MAKER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "
						+ getDbFunction("NVL") + "(TPEND.MAKER,0) ) MAKER_NAME,   "
						+ " TPEND.VERIFIER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "
						+ getDbFunction("NVL") + "(TPEND.VERIFIER,0) ) VERIFIER_NAME,   " + " "
						+ getDbFunction("DATEFUNC") + "(TPEND.DATE_LAST_MODIFIED, 'dd-MM-yyyy " + getDbFunction("TIME")
						+ "') DATE_LAST_MODIFIED ," + getDbFunction("DATEFUNC") + "(TPEND.DATE_CREATION, 'dd-MM-yyyy "
						+ getDbFunction("TIME") + "') DATE_CREATION, "
						+" (SELECT COUNT(*) FROM ( "
						+" SELECT * FROM RA_MST_FEES_HEADER WHERE COUNTRY = TPEND.country AND LE_BOOK = TPEND.le_book AND BUSINESS_LINE_ID = TPEND.BUSINESS_LINE_ID UNION ALL "
						+" SELECT * FROM RA_MST_FEES_HEADER_PEND WHERE COUNTRY = TPEND.country AND LE_BOOK = TPEND.le_book AND BUSINESS_LINE_ID = TPEND.BUSINESS_LINE_ID)A) Fee_line_Count "
						+ " FROM RA_MST_BUSINESS_LINE_HEADER_PEND TPEND, NUM_SUB_TAB T1,NUM_SUB_TAB T3"
//				+ "RA_MST_TRANS_LINE_HEADER_pend T2 "+
						+ " WHERE TPEND.COUNTRY =? AND TPEND.LE_BOOK =? " + tranBusinessLineIdPend + " AND  "
						+ " T1.NUM_tab = TPEND.RECORD_INDICATOR_NT " + " and T1.NUM_sub_tab = TPEND.RECORD_INDICATOR " +
//				" AND TPEND.TRANS_LINE_ID = T2.TRANS_LINE_ID  "
						" and T3.NUM_tab = TPEND.BUSINESS_LINE_STATUS_NT and T3.NUM_sub_tab = TPEND.BUSINESS_LINE_STATUS");

		Object objParams[] = new Object[intKeyFieldsCount];
		objParams[0] = new String(dObj.getCountry());// country
		objParams[1] = new String(dObj.getLeBook());
		if (ValidationUtil.isValid(dObj.getBusinessLineId()))
			objParams[2] = new String(dObj.getBusinessLineId());
		else
			objParams[2] = new String(dObj.getTransLineId());
		try {
			if (!dObj.isVerificationRequired() || dObj.isReview()) {
				intStatus = 0;
			}
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
			/*
			 * if(intStatus == 0) //logger.error(((strQueryAppr == null) ?
			 * "strQueryAppr is Null" : strQueryAppr.toString())); else
			 * //logger.error(((strQueryPend == null) ? "strQueryPend is Null" :
			 * strQueryPend.toString()));
			 * 
			 * if (objParams != null) for(int i=0 ; i< objParams.length; i++)
			 * //logger.error("objParams[" + i + "]" + objParams[i].toString());
			 */
			return null;
		}
	}

	@Override
	public List<BusinessLineHeaderVb> getQueryResultsForReview(BusinessLineHeaderVb dObj, int intStatus) {
		List<BusinessLineHeaderVb> collTemp = null;
		final int intKeyFieldsCount = 3;
		setServiceDefaults();
		String strQueryAppr = null;
		String strQueryPend = null;
		strQueryAppr = new String(
				"SELECT TAppr.COUNTRY, TAppr.LE_BOOK, TAppr.BUSINESS_LINE_ID,TAppr.BUSINESS_LINE_DESCRIPTION,TAppr.TRANS_LINE_TYPE,   "
						+ " TAppr.TRANS_LINE_ID,"
						+ " CASE WHEN TAppr.TRANS_LINE_TYPE='P' THEN T2.TRANS_LINE_PROD_GRP ELSE T2.TRANS_LINE_SERV_GRP END TRAN_LINE_GRP, "
						+ " CASE WHEN TAppr.TRANS_LINE_TYPE='P' THEN "
						+ " (SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB =T2.TRANS_LINE_PROD_GRP_AT AND ALPHA_SUB_TAB= T2.TRANS_LINE_PROD_GRP) "
						+ "  ELSE "
						+ "  (SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB =T2.TRANS_LINE_SERV_GRP_AT AND ALPHA_SUB_TAB= T2.TRANS_LINE_SERV_GRP) "
						+ "  END TRAN_LINE_GRP_DESC, TAppr.FEECALC_TIMESTAMP_FLAG,"
						+ " TAppr.BUSINESS_LINE_TYPE,(SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB =TAppr.BUSINESS_LINE_TYPE_AT AND ALPHA_SUB_TAB= TAppr.BUSINESS_LINE_TYPE) BUSINESS_LINE_TYPE_DESC, "
						+ "TAppr.IE_TYPE," + ieTypeDescAppr + "TAppr.ACTUAL_IE_POSTING," + postingTypeDescAppr
						+ " TAppr.ACTUAL_IE_MATCH_RULE , " + macthRuleDescAppr + " "
						+ " TAppr.BUSINESS_LINE_STATUS,T3.NUM_SUBTAB_DESCRIPTION BUSINESS_LINE_STATUS_DESC,TAppr.RECORD_INDICATOR,T1.NUM_SUBTAB_DESCRIPTION RECORD_INDICATOR_DESC,   "
						+ " TAppr. MAKER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "
						+ getDbFunction("NVL") + "(TAppr.MAKER,0) ) MAKER_NAME,   "
						+ " TAppr.VERIFIER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "
						+ getDbFunction("NVL") + "(TAppr.VERIFIER,0) ) VERIFIER_NAME,   " + " "
						+ getDbFunction("DATEFUNC") + "(TAppr.DATE_LAST_MODIFIED, '" + getDbFunction("DD_Mon_RRRR")
						+ " " + getDbFunction("TIME") + "') DATE_LAST_MODIFIED ," + getDbFunction("DATEFUNC")
						+ "(TAppr.DATE_CREATION, '" + getDbFunction("DD_Mon_RRRR") + " " + getDbFunction("TIME")
						+ "') DATE_CREATION, "
						+ "  (select count(1) from RA_MST_FEES_HEADER where country = TAppr.country and le_book = TAppr.le_book and BUSINESS_LINE_ID = TAppr.BUSINESS_LINE_ID) Fee_line_Count "
						+ " FROM RA_MST_BUSINESS_LINE_HEADER TAppr, NUM_SUB_TAB T1,NUM_SUB_TAB T3,RA_MST_TRANS_LINE_HEADER T2 WHERE   "
						+ " TAppr.COUNTRY =? AND TAPPR.LE_BOOK =? AND TAppr.BUSINESS_LINE_ID = ? AND "
						+ " T1.NUM_tab = TAppr.RECORD_INDICATOR_NT" + " and T1.NUM_sub_tab = TAppr.RECORD_INDICATOR"
						+ " AND TAPPR.TRANS_LINE_ID = T2.TRANS_LINE_ID AND T3.NUM_tab = TAppr.BUSINESS_LINE_STATUS_NT and T3.NUM_sub_tab = TAppr.BUSINESS_LINE_STATUS");

		strQueryPend = new String(
				"SELECT DISTINCT TPEND.COUNTRY, TPEND.LE_BOOK, TPEND.BUSINESS_LINE_ID,TPEND.BUSINESS_LINE_DESCRIPTION,TPEND.TRANS_LINE_TYPE,   "
						+ " TPEND.TRANS_LINE_ID,"
						+ " CASE WHEN TPEND.TRANS_LINE_TYPE='P' THEN T2.TRANS_LINE_PROD_GRP ELSE T2.TRANS_LINE_SERV_GRP END TRAN_LINE_GRP, "
						+ " CASE WHEN TPEND.TRANS_LINE_TYPE='P' THEN "
						+ " (SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB =T2.TRANS_LINE_PROD_GRP_AT AND ALPHA_SUB_TAB= T2.TRANS_LINE_PROD_GRP) "
						+ "  ELSE "
						+ "  (SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB =T2.TRANS_LINE_SERV_GRP_AT AND ALPHA_SUB_TAB= T2.TRANS_LINE_SERV_GRP) "
						+ "  END TRAN_LINE_GRP_DESC,TPEND.FEECALC_TIMESTAMP_FLAG, "
						+ " TPEND.BUSINESS_LINE_TYPE,(SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB =TPEND.BUSINESS_LINE_TYPE_AT AND ALPHA_SUB_TAB= TPEND.BUSINESS_LINE_TYPE) BUSINESS_LINE_TYPE_DESC, "
						+ "TPEND.IE_TYPE," + ieTypeDescPend + " TPEND.ACTUAL_IE_POSTING, " + postingTypeDescPend
						+ "TPEND.ACTUAL_IE_MATCH_RULE ," + macthRuleDescPend + "  "
						+ " TPEND.BUSINESS_LINE_STATUS,T3.NUM_SUBTAB_DESCRIPTION BUSINESS_LINE_STATUS_DESC,TPEND.RECORD_INDICATOR,T1.NUM_SUBTAB_DESCRIPTION RECORD_INDICATOR_DESC,   "
						+ " TPEND. MAKER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "
						+ getDbFunction("NVL") + "(TPEND.MAKER,0) ) MAKER_NAME,   "
						+ " TPEND.VERIFIER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "
						+ getDbFunction("NVL") + "(TPEND.VERIFIER,0) ) VERIFIER_NAME,   " + " "
						+ getDbFunction("DATEFUNC") + "(TPEND.DATE_LAST_MODIFIED, '" + getDbFunction("DD_Mon_RRRR")
						+ " " + getDbFunction("TIME") + "') DATE_LAST_MODIFIED ," + getDbFunction("DATEFUNC")
						+ "(TPEND.DATE_CREATION, '" + getDbFunction("DD_Mon_RRRR") + " " + getDbFunction("TIME")
						+ "') DATE_CREATION, "
						+ "  (select count(1) from RA_MST_FEES_HEADER where country = TPEND.country and le_book = TPEND.le_book and BUSINESS_LINE_ID = TPEND.BUSINESS_LINE_ID) Fee_line_Count "
						+ " FROM RA_MST_BUSINESS_LINE_HEADER_PEND TPEND, NUM_SUB_TAB T1,NUM_SUB_TAB T3,RA_MST_TRANS_LINE_HEADER T2,RA_MST_TRANS_LINE_HEADER_PEND T4 "
						+ " WHERE TPEND.COUNTRY =? AND TPEND.LE_BOOK =? AND TPEND.BUSINESS_LINE_ID = ? AND  "
						+ " T1.NUM_tab = TPEND.RECORD_INDICATOR_NT" + " and T1.NUM_sub_tab = TPEND.RECORD_INDICATOR"
						+ " AND (TPEND.TRANS_LINE_ID = T2.TRANS_LINE_ID OR TPEND.TRANS_LINE_ID = T4.TRANS_LINE_ID) and T3.NUM_tab = TPEND.BUSINESS_LINE_STATUS_NT and T3.NUM_sub_tab = TPEND.BUSINESS_LINE_STATUS");

		Object objParams[] = new Object[intKeyFieldsCount];
		objParams[0] = new String(dObj.getCountry());// country
		objParams[1] = new String(dObj.getLeBook());
		objParams[2] = new String(dObj.getBusinessLineId());
		try {
			if (!dObj.isVerificationRequired() || dObj.isReview()) {
				intStatus = 0;
			}
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
			/*
			 * if(intStatus == 0) //logger.error(((strQueryAppr == null) ?
			 * "strQueryAppr is Null" : strQueryAppr.toString())); else
			 * //logger.error(((strQueryPend == null) ? "strQueryPend is Null" :
			 * strQueryPend.toString()));
			 * 
			 * if (objParams != null) for(int i=0 ; i< objParams.length; i++)
			 * //logger.error("objParams[" + i + "]" + objParams[i].toString());
			 */
			return null;
		}
	}

	@Override
	public List<BusinessLineHeaderVb> getQueryPopupResults(BusinessLineHeaderVb dObj) {
		Vector<Object> params = new Vector<Object>();
		StringBuffer strBufApprove = null;
		StringBuffer strBufPending = null;
		String strWhereNotExists = null;
		// final int intKeyFieldsCount = 4;
		String orderBy = "";
		strBufApprove = new StringBuffer(
				" Select * from ( SELECT TAppr.COUNTRY, TAppr.LE_BOOK, TAppr.BUSINESS_LINE_ID,TAppr.BUSINESS_LINE_DESCRIPTION, "
						+ "  TAppr.TRANS_LINE_TYPE, t2.alpha_subtab_description TRANS_LINE_TYPE_DESC,TAppr.TRANS_LINE_ID, "
						+ " (SELECT T5.TRANS_LINE_DESCRIPTION  FROM RA_MST_TRANS_LINE_HEADER T5 WHERE TAppr.TRANS_LINE_ID = T5.TRANS_LINE_ID"
						+ " and TAppr.country = t5.country and TAppr.le_book = t5.le_book) TRANS_LINE_DESCRIPTION,TAppr.FEECALC_TIMESTAMP_FLAG, "
						+ "  TAppr.BUSINESS_LINE_STATUS,T3.NUM_SUBTAB_DESCRIPTION BUSINESS_LINE_STATUS_DESC,  "
						+ "(SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB =TAppr.BUSINESS_LINE_TYPE_AT AND ALPHA_SUB_TAB= TAppr.BUSINESS_LINE_TYPE) BUSINESS_LINE_TYPE_DESC, "
						+ "  TAppr.RECORD_INDICATOR,T4.NUM_SUBTAB_DESCRIPTION RECORD_INDICATOR_DESC,  "
						+ "  TAppr. MAKER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "
						+ getDbFunction("NVL") + "(TAppr.MAKER,0) ) MAKER_NAME,  "
						+ "  TAppr.VERIFIER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "
						+ getDbFunction("NVL") + "(TAppr.VERIFIER,0) ) VERIFIER_NAME, " + "  "
						+ getDbFunction("DATEFUNC") + "(TAppr.DATE_LAST_MODIFIED, '" + getDbFunction("DD_Mon_RRRR")
						+ " " + getDbFunction("TIME") + "') DATE_LAST_MODIFIED ," + getDbFunction("DATEFUNC")
						+ "(TAppr.DATE_CREATION, '" + getDbFunction("DD_Mon_RRRR") + " " + getDbFunction("TIME")
						+ "') DATE_CREATION ,    "
						+" (SELECT COUNT(*) FROM ( "
						+" SELECT * FROM RA_MST_FEES_HEADER WHERE COUNTRY = TAppr.country AND LE_BOOK = TAppr.le_book AND BUSINESS_LINE_ID = TAppr.BUSINESS_LINE_ID UNION ALL "
						+" SELECT * FROM RA_MST_FEES_HEADER_PEND WHERE COUNTRY = TAppr.country AND LE_BOOK = TAppr.le_book AND BUSINESS_LINE_ID = TAppr.BUSINESS_LINE_ID)A) Fee_line_Count, "
						+ " " + ieTypeDescAppr + " TAppr.IE_TYPE "
						+ " FROM RA_MST_BUSINESS_LINE_HEADER TAppr ,ALPHA_SUB_TAB T2,NUM_SUB_TAB T3,NUM_SUB_TAB T4   "
						+ "   Where                 " + "  t2.Alpha_tab = TAppr.TRANS_LINE_TYPE_AT           "
						+ "  and t2.Alpha_sub_tab = TAppr.TRANS_LINE_TYPE      "
						+ "  and t3.NUM_tab = TAppr.BUSINESS_LINE_STATUS_NT    "
						+ "  and t3.NUM_sub_tab = TAppr.BUSINESS_LINE_STATUS   "
						+ "  and t4.NUM_tab = TAppr.RECORD_INDICATOR_NT        "
						+ "  and t4.NUM_sub_tab = TAppr.RECORD_INDICATOR and TAppr.COUNTRY= ?"
						+ " and TAppr.LE_BOOK= ?   and TAppr.TRANS_LINE_ID= ? ) TAppr");

		strWhereNotExists = new String(
				" Not Exists (Select 'X' From RA_MST_BUSINESS_LINE_HEADER_PEND TPEND WHERE TAppr.COUNTRY = TPend.COUNTRY"
						+ " AND TAppr.LE_BOOK = TPend.LE_BOOK AND TAppr.BUSINESS_LINE_ID = TPend.BUSINESS_LINE_ID)");

		strBufPending = new StringBuffer(
				" Select * from ( SELECT TPend.COUNTRY, TPend.LE_BOOK, TPend.BUSINESS_LINE_ID,TPend.BUSINESS_LINE_DESCRIPTION, "
						+ "  TPend.TRANS_LINE_TYPE, t2.alpha_subtab_description TRANS_LINE_TYPE_DESC,TPend.TRANS_LINE_ID, "
						+ " (SELECT T5.TRANS_LINE_DESCRIPTION  FROM RA_MST_TRANS_LINE_HEADER T5 WHERE TPend.TRANS_LINE_ID = T5.TRANS_LINE_ID"
						+ " and TPend.country = t5.country and TPend.le_book = t5.le_book) TRANS_LINE_DESCRIPTION,TPend.FEECALC_TIMESTAMP_FLAG, "
						+ "  TPend.BUSINESS_LINE_STATUS,T3.NUM_SUBTAB_DESCRIPTION BUSINESS_LINE_STATUS_DESC,  "
						+ "(SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB =TPend.BUSINESS_LINE_TYPE_AT AND ALPHA_SUB_TAB=TPend.BUSINESS_LINE_TYPE) BUSINESS_LINE_TYPE_DESC, "
						+ "  TPend.RECORD_INDICATOR,T4.NUM_SUBTAB_DESCRIPTION RECORD_INDICATOR_DESC,  "
						+ "  TPend. MAKER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "
						+ getDbFunction("NVL") + "(TPend.MAKER,0) ) MAKER_NAME,  "
						+ "  TPend.VERIFIER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "
						+ getDbFunction("NVL") + "(TPend.VERIFIER,0) ) VERIFIER_NAME, " + "  "
						+ getDbFunction("DATEFUNC") + "(TPend.DATE_LAST_MODIFIED, '" + getDbFunction("DD_Mon_RRRR")
						+ " " + getDbFunction("TIME") + "') DATE_LAST_MODIFIED ," + getDbFunction("DATEFUNC")
						+ "(TPend.DATE_CREATION, '" + getDbFunction("DD_Mon_RRRR") + " " + getDbFunction("TIME")
						+ "') DATE_CREATION  ,   "
						+" (SELECT COUNT(*) FROM ( "
						+" SELECT * FROM RA_MST_FEES_HEADER WHERE COUNTRY = TPEND.country AND LE_BOOK = TPEND.le_book AND BUSINESS_LINE_ID = TPEND.BUSINESS_LINE_ID UNION ALL "
						+" SELECT * FROM RA_MST_FEES_HEADER_PEND WHERE COUNTRY = TPEND.country AND LE_BOOK = TPEND.le_book AND BUSINESS_LINE_ID = TPEND.BUSINESS_LINE_ID)A) Fee_line_Count, "
						+ "  " + ieTypeDescPend
						+ " TPend.IE_TYPE  FROM RA_MST_BUSINESS_LINE_HEADER_PEND TPend ,ALPHA_SUB_TAB T2,NUM_SUB_TAB T3,NUM_SUB_TAB T4   "
						+ "   Where                 " + "  t2.Alpha_tab = TPend.TRANS_LINE_TYPE_AT           "
						+ "  and t2.Alpha_sub_tab = TPend.TRANS_LINE_TYPE      "
						+ "  and t3.NUM_tab = TPend.BUSINESS_LINE_STATUS_NT    "
						+ "  and t3.NUM_sub_tab = TPend.BUSINESS_LINE_STATUS   "
						+ "  and t4.NUM_tab = TPend.RECORD_INDICATOR_NT        "
						+ "  and t4.NUM_sub_tab = TPend.RECORD_INDICATOR and TPend.COUNTRY= ? "
						+ " and TPend.LE_BOOK= ?  and TPend.TRANS_LINE_ID= ?) TPend  ");

		params.addElement(dObj.getCountry());
		params.addElement(dObj.getLeBook());
		params.addElement(dObj.getTransLineId());
		try {

			if (dObj.getSmartSearchOpt() != null && !dObj.getSmartSearchOpt().isEmpty()) {
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
					case "businessLineId":
						CommonUtils
								.addToQuerySearch(
										" (upper(TAPPR.BUSINESS_LINE_ID) " + val
												+ " OR upper(TAPPR.BUSINESS_LINE_DESCRIPTION)" + val + ")",
										strBufApprove, data.getJoinType());
						CommonUtils
								.addToQuerySearch(
										" (upper(TPend.BUSINESS_LINE_ID) " + val
												+ " OR upper(TPend.BUSINESS_LINE_DESCRIPTION)" + val + ")",
										strBufPending, data.getJoinType());
						break;

					case "businessLineDescription":
						CommonUtils.addToQuerySearch(" upper(TAPPR.BUSINESS_LINE_DESCRIPTION) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.BUSINESS_LINE_DESCRIPTION) " + val, strBufPending,
								data.getJoinType());
						break;

					case "businessLineTypeDesc":
						CommonUtils.addToQuerySearch(" upper(TAPPR.BUSINESS_LINE_TYPE_DESC) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.BUSINESS_LINE_TYPE_DESC) " + val, strBufPending,
								data.getJoinType());
						break;

					case "IncomeExpenseTypeDesc":
						CommonUtils.addToQuerySearch(" upper(TAPPR.IE_TYPE_DESC) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.IE_TYPE_DESC) " + val, strBufPending,
								data.getJoinType());
						break;

					case "transLineTypeDesc":
						CommonUtils.addToQuerySearch(" upper(TAPPR.TRANS_LINE_TYPE_DESC) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.TRANS_LINE_TYPE_DESC) " + val, strBufPending,
								data.getJoinType());
						break;

					case "transLineIdDesc":
						CommonUtils.addToQuerySearch(" upper(TAPPR.TRANS_LINE_DESCRIPTION) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.TRANS_LINE_DESCRIPTION) " + val, strBufPending,
								data.getJoinType());
						break;

					case "businessLineStatusDesc":
						CommonUtils.addToQuerySearch(" upper(TAPPR.BUSINESS_LINE_STATUS_DESC) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.BUSINESS_LINE_STATUS_DESC) " + val, strBufPending,
								data.getJoinType());
						break;

					case "businessLineRecordIndicatorDesc":
						CommonUtils.addToQuerySearch(" upper(TAPPR.RECORD_INDICATOR_DESC) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.RECORD_INDICATOR_DESC) " + val, strBufPending,
								data.getJoinType());
						break;

					case "dateCreation":
						CommonUtils
								.addToQuerySearch(
										" " + getDbFunction("DATEFUNC") + "(TAPPR.DATE_CREATION,'DD-MM-YYYY "
												+ getDbFunction("TIME") + "') " + val,
										strBufApprove, data.getJoinType());
						CommonUtils
								.addToQuerySearch(
										" " + getDbFunction("DATEFUNC") + "(TPend.DATE_CREATION,'DD-MM-YYYY "
												+ getDbFunction("TIME") + "') " + val,
										strBufPending, data.getJoinType());
						break;

					case "dateLastModified":
						CommonUtils
								.addToQuerySearch(
										" " + getDbFunction("DATEFUNC") + "(TAPPR.DATE_LAST_MODIFIED,'DD-MM-YYYY "
												+ getDbFunction("TIME") + "') " + val,
										strBufApprove, data.getJoinType());
						CommonUtils
								.addToQuerySearch(
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
			/*
			 * VisionUsersVb visionUsersVb = CustomContextHolder.getContext();
			 * if(("Y".equalsIgnoreCase(visionUsersVb.getUpdateRestriction()))){
			 * if(ValidationUtil.isValid(visionUsersVb.getCountry())){
			 * CommonUtils.addToQuery(" COUNTRY IN ("+visionUsersVb.getCountry()+") ",
			 * strBufApprove);
			 * CommonUtils.addToQuery(" COUNTRY IN ("+visionUsersVb.getCountry()+") ",
			 * strBufPending); } if(ValidationUtil.isValid(visionUsersVb.getLeBook())){
			 * CommonUtils.addToQuery(" LE_BOOK IN ('"+visionUsersVb.getLeBook()+"') ",
			 * strBufApprove);
			 * CommonUtils.addToQuery(" LE_BOOK IN ('"+visionUsersVb.getLeBook()+"') ",
			 * strBufPending); } }
			 */
			orderBy = " Order by BUSINESS_LINE_ID ";
			return getQueryPopupResults(dObj, strBufPending, strBufApprove, strWhereNotExists, orderBy, params);
		} catch (Exception ex) {
			ex.printStackTrace();
			// logger.error(((strBufApprove==null)? "strBufApprove is
			// Null":strBufApprove.toString()));
			// logger.error("UNION");
			// logger.error(((strBufPending==null)? "strBufPending is
			// Null":strBufPending.toString()));

			/*
			 * if (params != null) for(int i=0 ; i< params.size(); i++)
			 * //logger.error("objParams[" + i + "]" + params.get(i).toString());
			 */
			return null;
		}
	}

	public List<BusinessLineHeaderVb> getExistingRecords(TransLineHeaderVb dObj, int intStatus) {
		List<BusinessLineHeaderVb> collTemp = null;
		final int intKeyFieldsCount = 3;
		setServiceDefaults();
		String strQueryAppr = null;
		String strQueryPend = null;
		strQueryAppr = new String(
				"SELECT TAppr.COUNTRY, TAppr.LE_BOOK, TAppr.BUSINESS_LINE_ID,TAppr.BUSINESS_LINE_DESCRIPTION,TAppr.TRANS_LINE_TYPE,   "
						+ " TAppr.TRANS_LINE_ID,"
						+ " CASE WHEN TAppr.TRANS_LINE_TYPE='P' THEN T2.TRANS_LINE_PROD_GRP ELSE T2.TRANS_LINE_SERV_GRP END TRAN_LINE_GRP, "
						+ " CASE WHEN TAppr.TRANS_LINE_TYPE='P' THEN "
						+ " (SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB =T2.TRANS_LINE_PROD_GRP_AT AND ALPHA_SUB_TAB= T2.TRANS_LINE_PROD_GRP) "
						+ "  ELSE "
						+ "  (SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB =T2.TRANS_LINE_SERV_GRP_AT AND ALPHA_SUB_TAB= T2.TRANS_LINE_SERV_GRP) "
						+ "  END TRAN_LINE_GRP_DESC, TAppr.FEECALC_TIMESTAMP_FLAG,"
						+ " TAppr.BUSINESS_LINE_TYPE,(SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB =TAppr.BUSINESS_LINE_TYPE_AT AND ALPHA_SUB_TAB= TAppr.BUSINESS_LINE_TYPE) BUSINESS_LINE_TYPE_DESC, "
						+ "TAppr.IE_TYPE," + ieTypeDescAppr + " TAppr.ACTUAL_IE_POSTING," + postingTypeDescAppr
						+ " TAppr.ACTUAL_IE_MATCH_RULE ,   " + macthRuleDescAppr
						+ " TAppr.BUSINESS_LINE_STATUS,T3.NUM_SUBTAB_DESCRIPTION BUSINESS_LINE_STATUS_DESC,TAppr.RECORD_INDICATOR,T1.NUM_SUBTAB_DESCRIPTION RECORD_INDICATOR_DESC,   "
						+ " TAppr. MAKER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "
						+ getDbFunction("NVL") + "(TAppr.MAKER,0) ) MAKER_NAME,   "
						+ " TAppr.VERIFIER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "
						+ getDbFunction("NVL") + "(TAppr.VERIFIER,0) ) VERIFIER_NAME,   " + " "
						+ getDbFunction("DATEFUNC") + "(TAppr.DATE_LAST_MODIFIED, '" + getDbFunction("DD_Mon_RRRR")
						+ " " + getDbFunction("TIME") + "') DATE_LAST_MODIFIED ," + getDbFunction("DATEFUNC")
						+ "(TAppr.DATE_CREATION, '" + getDbFunction("DD_Mon_RRRR") + " " + getDbFunction("TIME")
						+ "') DATE_CREATION, "
						+ "  (select count(1) from RA_MST_FEES_HEADER where country = TAppr.country and le_book = TAppr.le_book and BUSINESS_LINE_ID = TAppr.BUSINESS_LINE_ID) Fee_line_Count "
						+
//				" FROM RA_MST_BUSINESS_LINE_HEADER TAppr, NUM_SUB_TAB T1,NUM_SUB_TAB T3,RA_MST_TRANS_LINE_HEADER T2 WHERE   " + 
//				" TAppr.COUNTRY =? AND TAPPR.LE_BOOK =? AND TAppr.TRANS_LINE_ID=? AND "+
//				" T1.NUM_tab = TAppr.RECORD_INDICATOR_NT" + 
//				" and T1.NUM_sub_tab = TAppr.RECORD_INDICATOR"+
//				" AND TAPPR.TRANS_LINE_ID = T2.TRANS_LINE_ID AND T3.NUM_tab = TAppr.BUSINESS_LINE_STATUS_NT and T3.NUM_sub_tab = TAppr.BUSINESS_LINE_STATUS");
						" FROM RA_MST_BUSINESS_LINE_HEADER TAPPR "
						+ " JOIN NUM_SUB_TAB T1 ON T1.NUM_TAB = TAPPR.RECORD_INDICATOR_NT  "
						+ " AND T1.NUM_SUB_TAB = TAPPR.RECORD_INDICATOR "
						+ " LEFT JOIN NUM_SUB_TAB T3 ON T3.NUM_TAB = TAPPR.BUSINESS_LINE_STATUS_NT  "
						+ " AND T3.NUM_SUB_TAB = TAPPR.BUSINESS_LINE_STATUS "
						+ " LEFT JOIN RA_MST_TRANS_LINE_HEADER T2 ON TAPPR.TRANS_LINE_ID = T2.TRANS_LINE_ID "
						+ " LEFT JOIN RA_MST_TRANS_LINE_HEADER_PEND T4 ON TAPPR.TRANS_LINE_ID = T4.TRANS_LINE_ID "
						+ " WHERE TAPPR.COUNTRY = ? " + " AND TAPPR.LE_BOOK = ? " + " AND TAPPR.TRANS_LINE_ID = ? ");
		strQueryPend = new String(
				"SELECT DISTINCT TPEND.COUNTRY, TPEND.LE_BOOK, TPEND.BUSINESS_LINE_ID,TPEND.BUSINESS_LINE_DESCRIPTION,TPEND.TRANS_LINE_TYPE,   "
						+ " TPEND.TRANS_LINE_ID,"
						+ " CASE WHEN TPEND.TRANS_LINE_TYPE='P' THEN T2.TRANS_LINE_PROD_GRP ELSE T2.TRANS_LINE_SERV_GRP END TRAN_LINE_GRP, "
						+ " CASE WHEN TPEND.TRANS_LINE_TYPE='P' THEN "
						+ " (SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB =T2.TRANS_LINE_PROD_GRP_AT AND ALPHA_SUB_TAB= T2.TRANS_LINE_PROD_GRP) "
						+ "  ELSE "
						+ "  (SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB =T2.TRANS_LINE_SERV_GRP_AT AND ALPHA_SUB_TAB= T2.TRANS_LINE_SERV_GRP) "
						+ "  END TRAN_LINE_GRP_DESC,TPEND.FEECALC_TIMESTAMP_FLAG, "
						+ " TPEND.BUSINESS_LINE_TYPE,(SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB =TPEND.BUSINESS_LINE_TYPE_AT AND ALPHA_SUB_TAB= TPEND.BUSINESS_LINE_TYPE) BUSINESS_LINE_TYPE_DESC, "
						+ "TPEND.IE_TYPE," + ieTypeDescPend + " TPEND.ACTUAL_IE_POSTING, " + postingTypeDescPend
						+ "TPEND.ACTUAL_IE_MATCH_RULE ,  " + macthRuleDescPend
						+ " TPEND.BUSINESS_LINE_STATUS,T3.NUM_SUBTAB_DESCRIPTION BUSINESS_LINE_STATUS_DESC,TPEND.RECORD_INDICATOR,T1.NUM_SUBTAB_DESCRIPTION RECORD_INDICATOR_DESC,   "
						+ " TPEND. MAKER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "
						+ getDbFunction("NVL") + "(TPEND.MAKER,0) ) MAKER_NAME,   "
						+ " TPEND.VERIFIER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "
						+ getDbFunction("NVL") + "(TPEND.VERIFIER,0) ) VERIFIER_NAME,   " + " "
						+ getDbFunction("DATEFUNC") + "(TPEND.DATE_LAST_MODIFIED, '" + getDbFunction("DD_Mon_RRRR")
						+ " " + getDbFunction("TIME") + "') DATE_LAST_MODIFIED ," + getDbFunction("DATEFUNC")
						+ "(TPEND.DATE_CREATION, '" + getDbFunction("DD_Mon_RRRR") + " " + getDbFunction("TIME")
						+ "') DATE_CREATION, "
						+ "  (select count(1) from RA_MST_FEES_HEADER where country = TPEND.country and le_book = TPEND.le_book and BUSINESS_LINE_ID = TPEND.BUSINESS_LINE_ID) Fee_line_Count "
						+
//				" FROM RA_MST_BUSINESS_LINE_HEADER_PEND TPEND, NUM_SUB_TAB T1,NUM_SUB_TAB T3,RA_MST_TRANS_LINE_HEADER T2,RA_MST_TRANS_LINE_HEADER_PEND T4 "+
//				" WHERE TPEND.COUNTRY =? AND TPEND.LE_BOOK =? AND TPEND.TRANS_LINE_ID = ? AND  "+
//				" T1.NUM_tab = TPEND.RECORD_INDICATOR_NT" + 
//				" and T1.NUM_sub_tab = TPEND.RECORD_INDICATOR"+
//				" AND (TPEND.TRANS_LINE_ID = T2.TRANS_LINE_ID OR TPEND.TRANS_LINE_ID = T4.TRANS_LINE_ID) and T3.NUM_tab = TPEND.BUSINESS_LINE_STATUS_NT and T3.NUM_sub_tab = TPEND.BUSINESS_LINE_STATUS");
						" FROM RA_MST_BUSINESS_LINE_HEADER_PEND TPEND "
						+ " JOIN NUM_SUB_TAB T1 ON T1.NUM_TAB = TPEND.RECORD_INDICATOR_NT  "
						+ " AND T1.NUM_SUB_TAB = TPEND.RECORD_INDICATOR "
						+ " LEFT JOIN NUM_SUB_TAB T3 ON T3.NUM_TAB = TPEND.BUSINESS_LINE_STATUS_NT  "
						+ " AND T3.NUM_SUB_TAB = TPEND.BUSINESS_LINE_STATUS "
						+ " LEFT JOIN RA_MST_TRANS_LINE_HEADER T2 ON TPEND.TRANS_LINE_ID = T2.TRANS_LINE_ID "
						+ " LEFT JOIN RA_MST_TRANS_LINE_HEADER_PEND T4 ON TPEND.TRANS_LINE_ID = T4.TRANS_LINE_ID "
						+ " WHERE TPEND.COUNTRY = ? " + " AND TPEND.LE_BOOK = ? " + " AND TPEND.TRANS_LINE_ID = ? ");
		Object objParams[] = new Object[intKeyFieldsCount];
		objParams[0] = new String(dObj.getCountry());// country
		objParams[1] = new String(dObj.getLeBook());
		objParams[2] = new String(dObj.getTransLineId());
		try {
			if (!dObj.isVerificationRequired() || dObj.isReview()) {
				intStatus = 0;
			}
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
			logger.error("Error: getQueryPopupResult Exception :   ");
			/*
			 * if(intStatus == 0) //logger.error(((strQueryAppr == null) ?
			 * "strQueryAppr is Null" : strQueryAppr.toString())); else
			 * //logger.error(((strQueryPend == null) ? "strQueryPend is Null" :
			 * strQueryPend.toString()));
			 * 
			 * if (objParams != null) for(int i=0 ; i< objParams.length; i++)
			 * //logger.error("objParams[" + i + "]" + objParams[i].toString());
			 */
			return null;
		}
	}

	protected int deleteBusinessLineHeaderAppr(BusinessLineHeaderVb vObject) {
		String query = "Delete from RA_MST_business_LINE_HEADER where COUNTRY = ? AND LE_BOOK = ? AND business_LINE_ID = ? ";
		Object[] args = { vObject.getCountry(), vObject.getLeBook(), vObject.getBusinessLineId() };
		return getJdbcTemplate().update(query, args);

	}

	protected int deleteBusinessLineHeaderPend(BusinessLineHeaderVb vObject) {
		String query = "Delete from RA_MST_business_LINE_HEADER_PEND where COUNTRY = ? AND LE_BOOK = ? AND business_LINE_ID = ? ";
		Object[] args = { vObject.getCountry(), vObject.getLeBook(), vObject.getBusinessLineId() };
		return getJdbcTemplate().update(query, args);

	}

	protected int doInsertionApprBusinessLineHeaders(BusinessLineHeaderVb vObject) {
		String query = " Insert Into RA_MST_business_LINE_HEADER(COUNTRY,LE_BOOK,business_LINE_ID,"
				+ "business_LINE_DESCRIPTION,TRANS_LINE_TYPE"
				+ ",TRANS_LINE_ID,BUSINESS_LINE_TYPE,IE_TYPE,ACTUAL_IE_POSTING,ACTUAL_IE_MATCH_RULE,FEECALC_TIMESTAMP_FLAG"
				+ ",business_LINE_STATUS_NT,business_LINE_STATUS"
				+ ",RECORD_INDICATOR_NT,RECORD_INDICATOR,MAKER,VERIFIER,DATE_LAST_MODIFIED,DATE_CREATION) "
				+ " Values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?," + getDbFunction("SYSDATE") + ","
				+ getDbFunction("SYSDATE") + ")";

		Object[] args = { vObject.getCountry(), vObject.getLeBook(), vObject.getBusinessLineId(),
				vObject.getBusinessLineDescription(), vObject.getTransLineType(), vObject.getTransLineId(),
				vObject.getBusinessLineType(), vObject.getIncomeExpenseType(), vObject.getActualIePosting(),
				vObject.getActualIeMatchRule(), vObject.getFeeCalcTimeStampFlag(), vObject.getBusinessLineStatusNT(),
				vObject.getBusinessLineStatus(), vObject.getRecordIndicatorNt(), vObject.getRecordIndicator(),
				vObject.getMaker(), vObject.getVerifier() };
		return getJdbcTemplate().update(query, args);
	}

	protected int doInsertionPendBusinessLineHeaders(BusinessLineHeaderVb vObject) {
		String query = " Insert Into RA_MST_business_LINE_HEADER_PEND(COUNTRY,LE_BOOK,business_LINE_ID,business_LINE_DESCRIPTION,TRANS_LINE_TYPE"
				+ ",TRANS_LINE_ID,BUSINESS_LINE_TYPE,IE_TYPE,ACTUAL_IE_POSTING,ACTUAL_IE_MATCH_RULE,FEECALC_TIMESTAMP_FLAG"
				+ ",business_LINE_STATUS_NT,business_LINE_STATUS"
				+ ",RECORD_INDICATOR_NT,RECORD_INDICATOR,MAKER,VERIFIER,DATE_LAST_MODIFIED,DATE_CREATION) "
				+ " Values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?," + getDbFunction("SYSDATE") + ","
				+ getDbFunction("SYSDATE") + ")";

		Object[] args = { vObject.getCountry(), vObject.getLeBook(), vObject.getBusinessLineId(),
				vObject.getBusinessLineDescription(), vObject.getTransLineType(), vObject.getTransLineId(),
				vObject.getBusinessLineType(), vObject.getIncomeExpenseType(), vObject.getActualIePosting(),
				vObject.getActualIeMatchRule(), vObject.getFeeCalcTimeStampFlag(), vObject.getBusinessLineStatusNT(),
				vObject.getBusinessLineStatus(), vObject.getRecordIndicatorNt(), vObject.getRecordIndicator(),
				vObject.getMaker(), vObject.getVerifier() };
		return getJdbcTemplate().update(query, args);

	}

	protected int doInsertionPendBusinessLineHeadersDc(BusinessLineHeaderVb vObject) {
		String query = "";
		if ("ORACLE".equalsIgnoreCase(databaseType)) {
			query = " Insert Into RA_MST_business_LINE_HEADER_PEND(COUNTRY,LE_BOOK,business_LINE_ID,business_LINE_DESCRIPTION,TRANS_LINE_TYPE"
					+ ",TRANS_LINE_ID,BUSINESS_LINE_TYPE,IE_TYPE,ACTUAL_IE_POSTING,ACTUAL_IE_MATCH_RULE,FEECALC_TIMESTAMP_FLAG"
					+ ",business_LINE_STATUS_NT,business_LINE_STATUS"
					+ ",RECORD_INDICATOR_NT,RECORD_INDICATOR,MAKER,VERIFIER,DATE_LAST_MODIFIED,DATE_CREATION) "
					+ " Values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?," + getDbFunction("SYSDATE")
					+ ",To_Date(?, 'DD-MM-YYYY HH24:MI:SS') )";
		} else {
			query = " Insert Into RA_MST_business_LINE_HEADER_PEND(COUNTRY,LE_BOOK,business_LINE_ID,business_LINE_DESCRIPTION,TRANS_LINE_TYPE"
					+ ",TRANS_LINE_ID,BUSINESS_LINE_TYPE,IE_TYPE,ACTUAL_IE_POSTING,ACTUAL_IE_MATCH_RULE,FEECALC_TIMESTAMP_FLAG"
					+ ",business_LINE_STATUS_NT,business_LINE_STATUS"
					+ ",RECORD_INDICATOR_NT,RECORD_INDICATOR,MAKER,VERIFIER,DATE_LAST_MODIFIED,DATE_CREATION) "
					+ " Values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?," + getDbFunction("SYSDATE")
					+ ",CONVERT(datetime, ?, 103) )";
		}
		Object[] args = { vObject.getCountry(), vObject.getLeBook(), vObject.getBusinessLineId(),
				vObject.getBusinessLineDescription(), vObject.getTransLineType(), vObject.getTransLineId(),
				vObject.getBusinessLineType(), vObject.getIncomeExpenseType(), vObject.getActualIePosting(),
				vObject.getActualIeMatchRule(), vObject.getFeeCalcTimeStampFlag(), vObject.getBusinessLineStatusNT(),
				vObject.getBusinessLineStatus(), vObject.getRecordIndicatorNt(), vObject.getRecordIndicator(),
				vObject.getMaker(), vObject.getVerifier(), vObject.getDateCreation() };
		return getJdbcTemplate().update(query, args);
	}

	protected int doUpdateApprHeader(BusinessLineHeaderVb vObject) {
		String query = " Update RA_MST_business_LINE_HEADER set business_LINE_DESCRIPTION= ?"
				+ ",TRANS_LINE_TYPE= ?, TRANS_LINE_ID = ?,BUSINESS_LINE_TYPE= ?"
				+ ",IE_TYPE= ?, ACTUAL_IE_POSTING = ? , ACTUAL_IE_MATCH_RULE = ?,FEECALC_TIMESTAMP_FLAG =? ,business_LINE_STATUS= ?"
				+ ",RECORD_INDICATOR= ?,MAKER= ?,VERIFIER= ?,DATE_LAST_MODIFIED= " + getDbFunction("SYSDATE") + " "
				+ " WHERE COUNTRY= ? AND LE_BOOK= ? AND business_LINE_ID= ? ";
		Object[] args = { vObject.getBusinessLineDescription(), vObject.getTransLineType(), vObject.getTransLineId(),
				vObject.getBusinessLineType(), vObject.getIncomeExpenseType(), vObject.getActualIePosting(),
				vObject.getActualIeMatchRule(), vObject.getFeeCalcTimeStampFlag(), vObject.getBusinessLineStatus(),
				vObject.getRecordIndicator(), vObject.getMaker(), vObject.getVerifier(), vObject.getCountry(),
				vObject.getLeBook(), vObject.getBusinessLineId() };
		return getJdbcTemplate().update(query, args);
	}

	protected int doUpdatePendHeader(BusinessLineHeaderVb vObject) {
		String query = " Update RA_MST_business_LINE_HEADER_PEND set business_LINE_DESCRIPTION= ?"
				+ ",TRANS_LINE_TYPE= ?,TRANS_LINE_ID = ?,BUSINESS_LINE_TYPE= ?"
				+ ",IE_TYPE= ?, ACTUAL_IE_POSTING = ? , ACTUAL_IE_MATCH_RULE = ?, FEECALC_TIMESTAMP_FLAG =? ,business_LINE_STATUS= ?"
				+ ",RECORD_INDICATOR= ?,MAKER= ?,VERIFIER= ?,DATE_LAST_MODIFIED= " + getDbFunction("SYSDATE") + " "
				+ " WHERE COUNTRY= ? AND LE_BOOK= ? AND business_LINE_ID= ? ";
		Object[] args = { vObject.getBusinessLineDescription(), vObject.getTransLineType(), vObject.getTransLineId(),
				vObject.getBusinessLineType(), vObject.getIncomeExpenseType(), vObject.getActualIePosting(),
				vObject.getActualIeMatchRule(), vObject.getFeeCalcTimeStampFlag(), vObject.getBusinessLineStatus(),
				vObject.getRecordIndicator(), vObject.getMaker(), vObject.getVerifier(), vObject.getCountry(),
				vObject.getLeBook(), vObject.getBusinessLineId() };
		return getJdbcTemplate().update(query, args);
	}

	@Override
	protected List<BusinessLineHeaderVb> selectApprovedRecord(BusinessLineHeaderVb vObject) {
		return getQueryResults(vObject, Constants.STATUS_ZERO);
	}

	@Override
	protected List<BusinessLineHeaderVb> doSelectPendingRecord(BusinessLineHeaderVb vObject) {
		return getQueryResults(vObject, Constants.STATUS_PENDING);
	}

	@Override
	protected int getStatus(BusinessLineHeaderVb records) {
		return records.getBusinessLineStatus();
	}

	@Override
	protected void setStatus(BusinessLineHeaderVb vObject, int status) {
		vObject.setBusinessLineStatus(status);
	}

	@Override
	public ExceptionCode doInsertApprRecordForNonTrans(BusinessLineHeaderVb vObject) throws RuntimeCustomException {
		List<BusinessLineHeaderVb> collTemp = null;
		List<BusinessLineHeaderVb> collTempPend = null;
		ExceptionCode exceptionCode = new ExceptionCode();
		strCurrentOperation = Constants.ADD;
		strApproveOperation = Constants.ADD;
		setServiceDefaults();
		collTemp = selectApprovedRecord(vObject);
		if (collTemp != null && !collTemp.isEmpty()) {
			exceptionCode = getResultObject(Constants.DUPLICATE_KEY_INSERTION);
			throw buildRuntimeCustomException(exceptionCode);
		}
		collTempPend = doSelectPendingRecord(vObject);
		if (collTempPend != null && !collTempPend.isEmpty()) {
			exceptionCode.setErrorMsg("Record Already Exists");
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			throw buildRuntimeCustomException(exceptionCode);
		}
		vObject.setRecordIndicator(Constants.STATUS_ZERO);
		vObject.setBusinessLineStatus(Constants.STATUS_ZERO);
		vObject.setMaker(intCurrentUserId);
		vObject.setVerifier(intCurrentUserId);
		retVal = doInsertionApprBusinessLineHeaders(vObject);
		if (retVal != Constants.SUCCESSFUL_OPERATION) {
			exceptionCode = getResultObject(retVal);
			throw buildRuntimeCustomException(exceptionCode);
		} else {
			exceptionCode = getResultObject(Constants.SUCCESSFUL_OPERATION);
		}

		if (vObject.getBusinessLineGllst() != null && !vObject.getBusinessLineGllst().isEmpty()) {
			exceptionCode = businessLineConfigGLDao.deleteAndInsertApprGl(vObject);
		}
		if (vObject.getBlReconRulelst() != null && !vObject.getBlReconRulelst().isEmpty()) {
			exceptionCode = blReconRuleDao.deleteAndInsertApprBlReconRule(vObject);
		}
		if(vObject.getTaxBlReconRulelst() != null && vObject.getTaxBlReconRulelst().size() > 0) {
			exceptionCode = taxBlReconRuleDao.deleteAndInsertApprTaxBlReconRule(vObject);
		}
		exceptionCode = getResultObject(Constants.SUCCESSFUL_OPERATION);
		if (exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION) {
			exceptionCode = getResultObject(Constants.AUDIT_TRAIL_ERROR);
			throw buildRuntimeCustomException(exceptionCode);
		}
		writeAuditLog(vObject, null);
		return exceptionCode;
	}

	@Override
	public ExceptionCode doInsertRecordForNonTrans(BusinessLineHeaderVb vObject) throws RuntimeCustomException {
		List<BusinessLineHeaderVb> collTemp = null;
		List<BusinessLineHeaderVb> collTempAppr = null;
		ExceptionCode exceptionCode = new ExceptionCode();
		strCurrentOperation = Constants.ADD;
		strApproveOperation = Constants.ADD;
		setServiceDefaults();
		collTempAppr = selectApprovedRecord(vObject);
		if (collTempAppr != null && !collTempAppr.isEmpty()) {
			exceptionCode = getResultObject(Constants.DUPLICATE_KEY_INSERTION);
			throw buildRuntimeCustomException(exceptionCode);
		}
		collTemp = doSelectPendingRecord(vObject);
		if (collTemp != null && !collTemp.isEmpty()) {
			exceptionCode = getResultObject(Constants.PENDING_FOR_ADD_ALREADY);
			throw buildRuntimeCustomException(exceptionCode);
		}
		vObject.setRecordIndicator(Constants.STATUS_INSERT);
		vObject.setBusinessLineStatus(Constants.STATUS_ZERO);
		vObject.setMaker(intCurrentUserId);
		vObject.setVerifier(0);
		// vObject.setVerifier(intCurrentUserId);
		retVal = doInsertionPendBusinessLineHeaders(vObject);
		if (retVal != Constants.SUCCESSFUL_OPERATION) {
			exceptionCode = getResultObject(retVal);
			throw buildRuntimeCustomException(exceptionCode);
		} else {
			exceptionCode = getResultObject(Constants.SUCCESSFUL_OPERATION);
		}
		if (vObject.getBusinessLineGllst() != null && !vObject.getBusinessLineGllst().isEmpty()) {
			exceptionCode = businessLineConfigGLDao.deleteAndInsertPendGl(vObject);
		}
		if (vObject.getBlReconRulelst() != null && !vObject.getBlReconRulelst().isEmpty()) {
			exceptionCode = blReconRuleDao.deleteAndInsertPendBlReconRule(vObject);
		}
		if(vObject.getTaxBlReconRulelst() != null && vObject.getTaxBlReconRulelst().size() > 0) {
			exceptionCode = taxBlReconRuleDao.deleteAndInsertPendTaxBlReconRule(vObject);
		}
		exceptionCode = getResultObject(Constants.SUCCESSFUL_OPERATION);
		writeAuditLog(vObject, null);
		return exceptionCode;
	}

	@Override
	public ExceptionCode doUpdateApprRecordForNonTrans(BusinessLineHeaderVb vObject) throws RuntimeCustomException {
		List<BusinessLineHeaderVb> collTemp = null;
		BusinessLineHeaderVb vObjectlocal = null;
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
		vObjectlocal = ((ArrayList<BusinessLineHeaderVb>) collTemp).get(0);
		// Even if record is not there in Appr. table reject the record
		if (collTemp.size() == 0) {
			exceptionCode = getResultObject(Constants.ATTEMPT_TO_MODIFY_UNEXISTING_RECORD);
			throw buildRuntimeCustomException(exceptionCode);
		}
		if (vObject.getBusinessLineStatus() == Constants.PASSIVATE) {
			exceptionCode = getResultObject(Constants.CANNOT_MODIFY_TO_DELETE_STATE);
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
		if (vObject.getBusinessLineGllst() != null && !vObject.getBusinessLineGllst().isEmpty()) {
			exceptionCode = businessLineConfigGLDao.deleteAndInsertApprGl(vObject);
		}
		if (vObject.getBlReconRulelst() != null && !vObject.getBlReconRulelst().isEmpty()) {
			exceptionCode = blReconRuleDao.deleteAndInsertApprBlReconRule(vObject);
		}
		if(vObject.getTaxBlReconRulelst() != null && vObject.getTaxBlReconRulelst().size() > 0) {
			exceptionCode = taxBlReconRuleDao.deleteAndInsertApprTaxBlReconRule(vObject);
		}
		String systemDate = getSystemDate();
		vObject.setDateLastModified(systemDate);

		writeAuditLog(vObject, vObjectlocal);
		/*
		 * if(exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION){
		 * exceptionCode = getResultObject(Constants.AUDIT_TRAIL_ERROR); throw
		 * buildRuntimeCustomException(exceptionCode); }
		 */
		exceptionCode = getResultObject(Constants.SUCCESSFUL_OPERATION);
		return exceptionCode;
	}

	@Override
	public ExceptionCode doUpdateRecordForNonTrans(BusinessLineHeaderVb vObject) throws RuntimeCustomException {
		List<BusinessLineHeaderVb> collTemp = null;
		BusinessLineHeaderVb vObjectlocal = null;
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
		if (vObject.getBusinessLineStatus() == Constants.PASSIVATE) {
			exceptionCode = getResultObject(Constants.CANNOT_MODIFY_TO_DELETE_STATE);
			throw buildRuntimeCustomException(exceptionCode);
		}
		if (!collTemp.isEmpty()) {
			vObjectlocal = ((ArrayList<BusinessLineHeaderVb>) collTemp).get(0);
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
			if (vObject.getBusinessLineGllst() != null && !vObject.getBusinessLineGllst().isEmpty()) {
				businessLineConfigGLDao.deleteAndInsertPendGl(vObject);
			}
			if (vObject.getBlReconRulelst() != null && !vObject.getBlReconRulelst().isEmpty()) {
				exceptionCode = blReconRuleDao.deleteAndInsertPendBlReconRule(vObject);
			}
			if(vObject.getTaxBlReconRulelst() != null && vObject.getTaxBlReconRulelst().size() > 0) {
				exceptionCode = taxBlReconRuleDao.deleteAndInsertPendTaxBlReconRule(vObject);
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
			if (!collTemp.isEmpty()) {
				vObjectlocal = ((ArrayList<BusinessLineHeaderVb>) collTemp).get(0);
				vObject.setDateCreation(vObjectlocal.getDateCreation());
			}
			vObject.setDateCreation(vObjectlocal.getDateCreation());
			// Record is there in approved, but not in pending. So add it to pending
			vObject.setVerifier(0);
			vObject.setRecordIndicator(Constants.STATUS_UPDATE);
			retVal = doInsertionPendBusinessLineHeadersDc(vObject);
			if (retVal != Constants.SUCCESSFUL_OPERATION) {
				exceptionCode = getResultObject(retVal);
				throw buildRuntimeCustomException(exceptionCode);
			}
			if (vObject.getBusinessLineGllst() != null && !vObject.getBusinessLineGllst().isEmpty()) {
				businessLineConfigGLDao.deleteAndInsertPendGl(vObject);
			}
			if (vObject.getBlReconRulelst() != null && !vObject.getBlReconRulelst().isEmpty()) {
				exceptionCode = blReconRuleDao.deleteAndInsertPendBlReconRule(vObject);
			}
			if(vObject.getTaxBlReconRulelst() != null && vObject.getTaxBlReconRulelst().size() > 0) {
				exceptionCode = taxBlReconRuleDao.deleteAndInsertPendTaxBlReconRule(vObject);
			}
			writeAuditLog(vObject, null);
			return getResultObject(Constants.SUCCESSFUL_OPERATION);
		}
	}

	@Override
	@Transactional(rollbackForClassName = { "com.vision.exception.RuntimeCustomException" })
	public ExceptionCode doRejectForTransaction(BusinessLineHeaderVb vObject) throws RuntimeCustomException {
		strErrorDesc = "";
		strCurrentOperation = Constants.APPROVE;
		setServiceDefaults();
		return doRejectRecord(vObject);
	}

	@Override
	public ExceptionCode doRejectRecord(BusinessLineHeaderVb vObject) throws RuntimeCustomException {
		BusinessLineHeaderVb vObjectlocal = null;
		List<BusinessLineHeaderVb> collTemp = null;
		List<TransLineHeaderVb> collTempTL = null;
		ExceptionCode exceptionCode = null;
		setServiceDefaults();
		strErrorDesc = "";
		strCurrentOperation = Constants.REJECT;
		vObject.setMaker(getIntCurrentUserId());
		try {
//			if(vObject.getRecordIndicator() == 1 || vObject.getRecordIndicator() == 3 )
//			    vObject.setRecordIndicator(0);
//			    else
//				   vObject.setRecordIndicator(-1);
			if (!vObject.isBusinessFlag()) {
				TransLineHeaderVb transLineHeaderVb = new TransLineHeaderVb();
				transLineHeaderVb.setCountry(vObject.getCountry());
				transLineHeaderVb.setLeBook(vObject.getLeBook());
				transLineHeaderVb.setTransLineId(vObject.getTransLineId());
				collTempTL = doselectproductRecord(transLineHeaderVb);
				if (collTempTL != null && !collTempTL.isEmpty()) {
					if (collTempTL.get(0).getTransLineStatus() == Constants.PASSIVATE) {
						exceptionCode = getResultObject(Constants.ERRONEOUS_OPERATION);
						exceptionCode.setErrorMsg("Trans Line [" + collTempTL.get(0).getTransLineId()
								+ "] is  Deleted - Kindly Active the Record");
						exceptionCode.setResponse(vObject);
						throw buildRuntimeCustomException(exceptionCode);
					}
				}
			}
			// See if such a pending request exists in the pending table
			if (vObject.isBusinessFlag() && vObject.getRecordIndicator() != Constants.STATUS_ZERO) {
				collTemp = doSelectPendingRecord(vObject);
				if (collTemp == null) {
					exceptionCode = getResultObject(Constants.ERRONEOUS_OPERATION);
					throw buildRuntimeCustomException(exceptionCode);
				}
				if (collTemp.size() == 0) {
					exceptionCode = getResultObject(Constants.NO_SUCH_PENDING_RECORD);
					throw buildRuntimeCustomException(exceptionCode);
				}
				vObjectlocal = ((ArrayList<BusinessLineHeaderVb>) collTemp).get(0);
				retVal = deleteBusinessLineHeaderPend(vObject);

				List<BusinessLineGLVb> collTempGl = null;
				collTempGl = businessLineConfigGLDao.getBusinessGLDetails(vObject, 1);
				if (collTempGl != null && !collTempGl.isEmpty()) {
					retVal = businessLineConfigGLDao.deleteBusinessLineGlPend(vObject);
				}
				List<BlReconRuleVb> collTempBl = null;
				collTempBl = blReconRuleDao.getBlReconRuleDetails(vObject, Constants.SUCCESSFUL_OPERATION);
				if (collTempBl != null && !collTempBl.isEmpty()) {
					retVal = blReconRuleDao.doDeletePendBlReconRule(vObject);
				}
				List<BlReconRuleVb> collTempTaxBl = null;
				collTempTaxBl = taxBlReconRuleDao.getTaxBlReconRuleDetails(vObject, Constants.SUCCESSFUL_OPERATION);
				if(collTempTaxBl != null && collTempTaxBl.size() > 0) {
					retVal = taxBlReconRuleDao.doDeletePendTaxBlReconRule(vObject);
				}
			}
			if (vObject.isFeesFlag()) {
				// Reject Fee Line Config Records from Pending table
				FeesConfigHeaderVb feesConfigHeaderVb = new FeesConfigHeaderVb();
				feesConfigHeaderVb.setCountry(vObject.getCountry());
				feesConfigHeaderVb.setLeBook(vObject.getLeBook());
				feesConfigHeaderVb.setBusinessLineId(vObject.getBusinessLineId());
				List<FeesConfigHeaderVb> feesLineDatalst = feesConfigHeadersDao.getQueryResults(feesConfigHeaderVb,
						Constants.STATUS_PENDING);
				if (feesLineDatalst != null && !feesLineDatalst.isEmpty()) {
					for (FeesConfigHeaderVb feeConfigVb : feesLineDatalst) {
						exceptionCode = feesConfigHeadersDao.doRejectForTransaction(feeConfigVb);
					}
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
//			//logger.error( ((vObject==null)? "vObject is Null":vObject.toString()));
			strErrorDesc = ex.getMessage();
			exceptionCode = getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION);
			throw buildRuntimeCustomException(exceptionCode);
		}
	}

	@Override
	@Transactional(rollbackForClassName = { "com.vision.exception.RuntimeCustomException" })
	public ExceptionCode doApproveForTransaction(BusinessLineHeaderVb vObject, boolean staticDelete)
			throws RuntimeCustomException {
		strErrorDesc = "";
		strCurrentOperation = Constants.APPROVE;
		setServiceDefaults();
		ExceptionCode exceptionCode = new ExceptionCode();
		boolean feeFlag = true;
		List<BusinessLineHeaderVb> collTemp = null;
		try {
			String systemDate = getSystemDate();
			FeesConfigHeaderVb feesConfigHeaderVb = new FeesConfigHeaderVb();
			feesConfigHeaderVb.setCountry(vObject.getCountry());
			feesConfigHeaderVb.setLeBook(vObject.getLeBook());
			feesConfigHeaderVb.setBusinessLineId(vObject.getBusinessLineId());
			List<FeesConfigHeaderVb> feesLineDatalst = feesConfigHeadersDao.getQueryResults(feesConfigHeaderVb,
					Constants.STATUS_PENDING);
			if (vObject.isFeesFlag()) {
				if (feesLineDatalst != null && !feesLineDatalst.isEmpty()) {
					List<FeesConfigHeaderVb> myFeeLst = feesLineDatalst.stream()
							.filter(n -> n.getMaker() == intCurrentUserId).collect(Collectors.toList());
					if (!myFeeLst.isEmpty() && !myFeeLst.isEmpty()) {
						exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
						exceptionCode.setErrorMsg("[ " + myFeeLst.size()
								+ " ] Fee Configs are added from your ID.Maker cannot Approve !!");
						feeFlag = false;
					}
				}
			}
			if (!feeFlag) {
				return exceptionCode;
			}

			if ((!staticDelete && (vObject.getRecordIndicator() == Constants.STATUS_DELETE))) {
				collTemp = selectApprovedRecord(vObject);
				if (collTemp != null && !collTemp.isEmpty()) {
					retVal = deleteBusinessLineHeaderAppr(vObject);
					retVal = businessLineConfigGLDao.deleteBusinessLineGlAppr(vObject);
					retVal = blReconRuleDao.doDeleteApprBlReconRule(vObject);
					vObject.setDateLastModified(systemDate);
					writeAuditLog(null, vObject);
				}
				// pend
				retVal = deleteBusinessLineHeaderPend(vObject);
				retVal = businessLineConfigGLDao.deleteBusinessLineGlPend(vObject);
				retVal = blReconRuleDao.doDeletePendBlReconRule(vObject);

				List<FeesConfigHeaderVb> collTempFl = feesConfigHeadersDao.getQueryResults(feesConfigHeaderVb,
						Constants.STATUS_ZERO);
				if (collTempFl != null && !collTempFl.isEmpty()) {
					feesLineDatalst.addAll(collTempFl);
				}
				if (feesLineDatalst != null && !feesLineDatalst.isEmpty()) {
					for (FeesConfigHeaderVb feesConfigVb : feesLineDatalst) {
						if (feesConfigVb.getRecordIndicator() == Constants.STATUS_ZERO) {
							retVal = feesConfigHeadersDao.deleteFeesHeaderAppr(feesConfigVb);
							retVal = feesConfigHeadersDao.getFeesConfigDetailsDao()
									.deleteFeesDetailsApprMain(feesConfigVb);
							retVal = feesConfigHeadersDao.getFeesConfigTierDao().deleteFeesTierApprMain(feesConfigVb);
							feesConfigHeadersDao.writeAuditLog(null, feesConfigVb);
						}

						// Pend
						retVal = feesConfigHeadersDao.deleteFeesHeaderPend(feesConfigVb);
						retVal = feesConfigHeadersDao.getFeesConfigDetailsDao().deleteFeesDetailsPendMain(feesConfigVb);
						retVal = feesConfigHeadersDao.getFeesConfigTierDao().deleteFeesTierPendMain(feesConfigVb);

					}
				}
				exceptionCode = getResultObject(Constants.SUCCESSFUL_OPERATION);
			} else {
				exceptionCode = doApproveRecord(vObject, staticDelete);
				if (vObject.isFeesFlag() && exceptionCode.getErrorCode() == Constants.SUCCESSFUL_OPERATION) {
					if (feesLineDatalst != null && !feesLineDatalst.isEmpty()) {
						for (FeesConfigHeaderVb feeConfigVb : feesLineDatalst) {
							exceptionCode = feesConfigHeadersDao.doApproveRecord(feeConfigVb, staticDelete);
							if (exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION)
								throw buildRuntimeCustomException(exceptionCode);
						}
					}
				}
			}
			// For returning the service desc as BusinessLine config
			if (exceptionCode.getErrorCode() == Constants.SUCCESSFUL_OPERATION) {
				exceptionCode = getResultObject(Constants.SUCCESSFUL_OPERATION);
			}

		} catch (RuntimeCustomException ex) {
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg(ex.getMessage());
			throw ex;
		} catch (Exception e) {
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg(e.getMessage());
			throw e;
		}
		return exceptionCode;
//		return doApproveRecord(vObject,staticDelete);
	}

	@Override
	// @Transactional(rollbackForClassName={"com.vision.exception.RuntimeCustomException"})
	public ExceptionCode doApproveRecord(BusinessLineHeaderVb vObject, boolean staticDelete)
			throws RuntimeCustomException {
		BusinessLineHeaderVb oldContents = null;
		BusinessLineGLVb oldContentsGl = null;
		BlReconRuleVb oldContentsBl = null;

		BusinessLineHeaderVb vObjectlocal = null;
		BusinessLineGLVb vObjectGllocal = null;
		BlReconRuleVb vObjectBllocal = null;

		List<BusinessLineHeaderVb> collTemp = null;
		List<BusinessLineGLVb> collTempGl = null;
		List<BusinessLineGLVb> collTempGlAppr = null;

		List<BlReconRuleVb> collTempBl = null;
		List<BlReconRuleVb> collTempBlAppr = null;
		
		List<BlReconRuleVb> collTempTaxBl = null;
		List<BlReconRuleVb> collTempTaxBlAppr = null;

		ExceptionCode exceptionCode = null;
		List<TransLineHeaderVb> collTempTL = null;
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

			vObjectlocal = ((ArrayList<BusinessLineHeaderVb>) collTemp).get(0);

			if (vObjectlocal.getMaker() == getIntCurrentUserId()) {
				exceptionCode = getResultObject(Constants.MAKER_CANNOT_APPROVE);
				String errorMsg = exceptionCode.getErrorMsg() + " for the Business Line Id["
						+ vObject.getBusinessLineId() + " - " + vObject.getBusinessLineDescription() + "]";
				exceptionCode.setErrorMsg(errorMsg);
				exceptionCode.setErrorCode(Constants.MAKER_CANNOT_APPROVE);
				throw buildRuntimeCustomException(exceptionCode);
			}

			// When trying to approve business line record, Check Trans line is approved or
			// not
			if (!vObject.isBusinessFlag()) {
				TransLineHeaderVb transLineHeaderVb = new TransLineHeaderVb();
				transLineHeaderVb.setCountry(vObject.getCountry());
				transLineHeaderVb.setLeBook(vObject.getLeBook());
				transLineHeaderVb.setTransLineId(vObject.getTransLineId());
				collTempTL = doselectproductRecord(transLineHeaderVb);
				if (collTempTL != null && !collTempTL.isEmpty()) {
					if (collTempTL.get(0).getTransLineStatus() == Constants.PASSIVATE) {
						exceptionCode = getResultObject(Constants.ERRONEOUS_OPERATION);
						exceptionCode.setErrorMsg("Trans Line [" + collTempTL.get(0).getTransLineId()
								+ "] is  Deleted - Kindly Active the Record");
						exceptionCode.setResponse(vObject);
						throw buildRuntimeCustomException(exceptionCode);
					}
				}
			}
//			else {
			collTempGl = businessLineConfigGLDao.getBusinessGLDetails(vObjectlocal, 1);
			if (collTempGl != null && !collTempGl.isEmpty()) {
				vObjectGllocal = ((ArrayList<BusinessLineGLVb>) collTempGl).get(0);
			}
			collTempBl = blReconRuleDao.getBlReconRuleDetails(vObjectlocal, Constants.SUCCESSFUL_OPERATION);
			if (collTempBl != null && !collTempBl.isEmpty()) {
				vObjectBllocal = collTempBl.get(0);
			}
			collTempTaxBl = taxBlReconRuleDao.getTaxBlReconRuleDetails(vObjectlocal, Constants.SUCCESSFUL_OPERATION);
			if(collTempTaxBl != null && collTempTaxBl.size() > 0) {
				vObjectBllocal = collTempTaxBl.get(0);
			}
			// If it's NOT addition, collect the existing record contents from the
			// Approved table and keep it aside, for writing audit
			// in"+getDbFunction("DATEFUNC")+"ion later.
			if (vObjectlocal.getRecordIndicator() != Constants.STATUS_INSERT) {
				collTemp = selectApprovedRecord(vObject);
				if (collTemp == null || collTemp.isEmpty()) {
					exceptionCode = getResultObject(Constants.ERRONEOUS_OPERATION);
					throw buildRuntimeCustomException(exceptionCode);
				}
				oldContents = ((ArrayList<BusinessLineHeaderVb>) collTemp).get(0);

				collTempGlAppr = businessLineConfigGLDao.getBusinessGLDetails(vObjectlocal, 0);
				if (collTempGlAppr != null && !collTempGlAppr.isEmpty()) {
					oldContentsGl = ((ArrayList<BusinessLineGLVb>) collTempGlAppr).get(0);
				}

				collTempBlAppr = blReconRuleDao.getBlReconRuleDetails(vObjectlocal, Constants.STATUS_ZERO);
				if (collTempBlAppr != null && !collTempBlAppr.isEmpty()) {
					oldContentsBl = collTempBlAppr.get(0);
				}
				collTempTaxBlAppr = taxBlReconRuleDao.getTaxBlReconRuleDetails(vObjectlocal, Constants.STATUS_ZERO);
				if(collTempTaxBlAppr != null && collTempTaxBlAppr.size() > 0) {
					oldContentsBl = collTempBlAppr.get(0);
				}

			}

			if (vObjectlocal.getRecordIndicator() == Constants.STATUS_INSERT) { // Add authorization
				// Write the contents of the Pending table record to the Approved table
				vObjectlocal.setRecordIndicator(Constants.STATUS_ZERO);
				vObjectlocal.setVerifier(getIntCurrentUserId());
				retVal = doInsertionApprBusinessLineHeaders(vObjectlocal);
				if (retVal != Constants.SUCCESSFUL_OPERATION) {
					exceptionCode = getResultObject(retVal);
					throw buildRuntimeCustomException(exceptionCode);
				}
				if (collTempGlAppr != null && !collTempGlAppr.isEmpty()) {
					businessLineConfigGLDao.deleteBusinessLineGlAppr(vObjectlocal);
				}
				if (collTempGl != null && !collTempGl.isEmpty()) {
					collTempGl.forEach(glPend -> {
						retVal = businessLineConfigGLDao.doInsertionApprBusinessLineGL(glPend);
					});
				}

				if (collTempBlAppr != null && !collTempBlAppr.isEmpty()) {
					blReconRuleDao.doDeleteApprBlReconRule(vObjectlocal);
				}
				if (collTempBl != null && !collTempBl.isEmpty()) {
					collTempBl.forEach(blAppr -> {
						retVal = blReconRuleDao.doInsertApprBlReconRule(blAppr);
					});
				}
				if(collTempTaxBlAppr != null && collTempTaxBlAppr.size() > 0) {
					taxBlReconRuleDao.doDeleteApprTaxBlReconRule(vObjectlocal);
				}
				if(collTempTaxBl != null && collTempTaxBl.size() > 0) {
					collTempTaxBl.forEach(blAppr ->{
						retVal = taxBlReconRuleDao.doInsertApprTaxBlReconRule(blAppr);
					});
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
				if (!collTemp.isEmpty()) {
					// retVal = doUpdateAppr(vObjectlocal, MISConstants.ACTIVATE);
					vObjectlocal.setVerifier(getIntCurrentUserId());
					vObjectlocal.setRecordIndicator(Constants.STATUS_ZERO);
					retVal = doUpdateApprHeader(vObjectlocal);
					if (retVal != Constants.SUCCESSFUL_OPERATION) {
						exceptionCode = getResultObject(retVal);
						throw buildRuntimeCustomException(exceptionCode);
					}
				}
				if (collTempGlAppr != null && !collTempGlAppr.isEmpty()) {
					businessLineConfigGLDao.deleteBusinessLineGlAppr(vObjectlocal);
				}
				if (collTempGl != null && !collTempGl.isEmpty()) {
					collTempGl.forEach(glPend -> {
						retVal = businessLineConfigGLDao.doInsertionApprBusinessLineGL(glPend);
					});
				}
				if (collTempBlAppr != null && !collTempBlAppr.isEmpty()) {
					collTempBlAppr.forEach(blReconRule -> {
						blReconRule.setRefNo(blReconRuleDao.getMaxSequence());
						blReconRuleDao.doInsertBlReconRuleHis(blReconRule);
					});
					blReconRuleDao.doDeleteApprBlReconRule(vObjectlocal);
				}
				if (collTempBl != null && !collTempBl.isEmpty()) {
					collTempBl.forEach(blAppr -> {
						retVal = blReconRuleDao.doInsertApprBlReconRule(blAppr);
					});
				}
				if(collTempTaxBlAppr != null && collTempTaxBlAppr.size() > 0) {
					collTempTaxBlAppr.forEach(blReconRule ->{
						blReconRule.setRefNo(taxBlReconRuleDao.getMaxSequence());
						taxBlReconRuleDao.doInsertTaxBlReconRuleHis(blReconRule);
					});
					taxBlReconRuleDao.doDeleteApprTaxBlReconRule(vObjectlocal);
				}
				if(collTempTaxBl != null && collTempTaxBl.size() > 0) {
					collTempTaxBl.forEach(blAppr ->{
						retVal = taxBlReconRuleDao.doInsertApprTaxBlReconRule(blAppr);
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
					retVal = deleteBusinessLineHeaderAppr(vObjectlocal);
					if (retVal != Constants.SUCCESSFUL_OPERATION) {
						exceptionCode = getResultObject(retVal);
						throw buildRuntimeCustomException(exceptionCode);
					}
					retVal = businessLineConfigGLDao.deleteBusinessLineGlAppr(vObjectlocal);
					retVal = blReconRuleDao.doDeleteApprBlReconRule(vObjectlocal);
					retVal = taxBlReconRuleDao.doDeleteApprTaxBlReconRule(vObjectlocal);
					String systemDate = getSystemDate();
					vObject.setDateLastModified(systemDate);
				}
				// Set the current operation to write to audit log
				strApproveOperation = Constants.DELETE;
			} else {
				exceptionCode = getResultObject(Constants.INVALID_STATUS_FLAG_IN_DATABASE);
				throw buildRuntimeCustomException(exceptionCode);
			}
			// Delete the record from the Pending table
			retVal = deleteBusinessLineHeaderPend(vObjectlocal);
			if (retVal != Constants.SUCCESSFUL_OPERATION) {
				exceptionCode = getResultObject(retVal);
				throw buildRuntimeCustomException(exceptionCode);
			}
			retVal = businessLineConfigGLDao.deleteBusinessLineGlPend(vObjectlocal);
			retVal = blReconRuleDao.doDeletePendBlReconRule(vObjectlocal);
			retVal = taxBlReconRuleDao.doDeletePendTaxBlReconRule(vObjectlocal);
			// Set the internal status to Approved
			vObject.setInternalStatus(0);
			vObject.setRecordIndicator(Constants.STATUS_ZERO);
			if (vObjectlocal.getRecordIndicator() == Constants.STATUS_DELETE && !staticDelete) {
				writeAuditLog(null, oldContents);
				vObject.setRecordIndicator(-1);
			} else
				writeAuditLog(vObjectlocal, oldContents);

//			}
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
//			//logger.error( ((vObject==null)? "vObject is Null":vObject.toString()));
			strErrorDesc = ex.getMessage();
			exceptionCode = getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION);
			throw buildRuntimeCustomException(exceptionCode);
		}
	}

	@Override
	protected void setServiceDefaults() {
		serviceName = "BusinessLineConfig";
		serviceDesc = "Business Line Config";
		tableName = "RA_MST_BUSINESS_LINE_HEADER";
		childTableName = "RA_MST_BUSINESS_LINE_HEADER";
		intCurrentUserId = CustomContextHolder.getContext().getVisionId();
	}

	@Override
	protected String getAuditString(BusinessLineHeaderVb vObject) {
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

			if (ValidationUtil.isValid(vObject.getTransLineId()))
				strAudit.append("BUSINESS_LINE_ID " + auditDelimiterColVal + vObject.getBusinessLineId().trim());
			else
				strAudit.append("BUSINESS_LINE_ID" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);

			if (ValidationUtil.isValid(vObject.getTransLineId()))
				strAudit.append("BUSINESS_LINE_DESCRIPTION " + auditDelimiterColVal
						+ vObject.getBusinessLineDescription().trim());
			else
				strAudit.append("BUSINESS_LINE_DESCRIPTION" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);

			if (ValidationUtil.isValid(vObject.getTransLineId()))
				strAudit.append("TRANS_LINE_ID" + auditDelimiterColVal + vObject.getTransLineId().trim());
			else
				strAudit.append("TRANS_LINE_ID" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);

			strAudit.append("BUSINESS_LINE_TYPE" + auditDelimiterColVal + vObject.getBusinessLineType());
			strAudit.append(auditDelimiter);

			strAudit.append("IE_TYPE" + auditDelimiterColVal + vObject.getIncomeExpenseType());
			strAudit.append(auditDelimiter);

			strAudit.append("BUSINESS_LINE_STATUS_NT" + auditDelimiterColVal + vObject.getBusinessLineStatusNT());
			strAudit.append(auditDelimiter);

			strAudit.append("BUSINESS_LINE_STATUS" + auditDelimiterColVal + vObject.getBusinessLineStatus());
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
//			ex.printStackTrace();
		}
		return strAudit.toString();
	}

	/*
	 * public ExceptionCode callBusinessLineMergeProc(BusinessLineHeaderVb vObject){
	 * ExceptionCode exceptionCode = new ExceptionCode(); setServiceDefaults();
	 * strCurrentOperation = "Query"; strErrorDesc = ""; Connection con = null;
	 * CallableStatement cs = null; try{ con = getConnection(); cs =
	 * con.prepareCall("{call PR_RA_MST_BUSINESS_LINE(?,?,?,?,?)}"); cs.setString(1,
	 * vObject.getCountry()); cs.setString(2, vObject.getLeBook()); cs.setString(3,
	 * vObject.getBusinessLineId()); cs.registerOutParameter(4,
	 * java.sql.Types.VARCHAR); //Status cs.registerOutParameter(5,
	 * java.sql.Types.VARCHAR); //Error Message cs.execute();
	 * exceptionCode.setErrorCode(Integer.parseInt(cs.getString(4)));
	 * exceptionCode.setErrorMsg(cs.getString(5)); if(exceptionCode.getErrorCode()
	 * != 0) {
	 * //logger.error("Error on Business Line Merge["+exceptionCode.getErrorMsg()+
	 * "]"); } cs.close(); }catch(Exception ex){ ex.printStackTrace(); strErrorDesc
	 * = ex.getMessage().trim(); }finally{ JdbcUtils.closeStatement(cs);
	 * DataSourceUtils.releaseConnection(con, getDataSource()); } return
	 * exceptionCode; }
	 */
	public ArrayList<String> getTranBusinessLine(String country, String leBook, String tranLineId) {
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			String orginalQuery = "SELECT BUSINESS_LINE_ID FROM RA_MST_BUSINESS_LINE_HEADER WHERE COUNTRY = ? AND LE_BOOK = ? "
					+ " AND TRANS_LINE_ID = ? ";

			Object[] params = { country, leBook, tranLineId };

			ResultSetExtractor mapper = new ResultSetExtractor() {
				@Override
				public Object extractData(ResultSet rs) throws SQLException, DataAccessException {
					ArrayList<String> logMsglst = new ArrayList<String>();
					ResultSetMetaData metaData = rs.getMetaData();
					while (rs.next()) {
						logMsglst.add(rs.getString("BUSINESS_LINE_ID"));
					}
					return logMsglst;
				}
			};
			return (ArrayList<String>) getJdbcTemplate().query(orginalQuery, params, mapper);
		} catch (Exception e) {
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg(e.getMessage());
			return null;
		}
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
						+ getDbFunction("DATEFUNC") + "(TPend.DATE_LAST_MODIFIED, 'dd-MM-yyyy " + getDbFunction("TIME")
						+ "') DATE_LAST_MODIFIED ," + getDbFunction("DATEFUNC") + "(TPend.DATE_CREATION, 'dd-MM-yyyy "
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
//			ex.printStackTrace();
			return null;
		}

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

	@Override
	protected ExceptionCode doDeleteRecordForNonTrans(BusinessLineHeaderVb vObject) throws RuntimeCustomException {
		BusinessLineHeaderVb vObjectlocal = null;
		List<BusinessLineHeaderVb> collTemp = null;
		List<BusinessLineHeaderVb> collTempAppr = null;
		ExceptionCode exceptionCode = null;
		strApproveOperation = Constants.DELETE;
		strErrorDesc = "";
		strCurrentOperation = Constants.DELETE;
		setServiceDefaults();
		if (vObject.isBusinessFlag() && vObject.getBusinessLineStatus() != Constants.PASSIVATE) {
			collTempAppr = selectApprovedRecord(vObject);

			if (collTempAppr == null) {
				logger.error("Collection is null");
				exceptionCode = getResultObject(Constants.ERRONEOUS_OPERATION);
				throw buildRuntimeCustomException(exceptionCode);
			}
			// If record already exists in the approved table, reject the addition
			if (ValidationUtil.isValid(vObject.getBusinessLineId()) && collTempAppr.size() == 0) {
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
			if (vObject.isBusinessFlag()) {
				if (!collTemp.isEmpty()) {
					exceptionCode = getResultObject(Constants.TRYING_TO_DELETE_APPROVAL_PENDING_RECORD);
					throw buildRuntimeCustomException(exceptionCode);
				}
			}
			if (!collTempAppr.isEmpty() && vObject.isBusinessFlag()) {
//			for (BusinessLineHeaderVb dObj : collTempAppr) {

				int intStaticDeletionFlag = getStatus(collTempAppr.get(0));
				if (intStaticDeletionFlag == Constants.PASSIVATE) {
					exceptionCode = getResultObject(Constants.CANNOT_DELETE_AN_INACTIVE_RECORD);
					throw buildRuntimeCustomException(exceptionCode);
				}
				BusinessLineHeaderVb businessLineVb = collTempAppr.get(0);
				businessLineVb.setMaker(getIntCurrentUserId());
				businessLineVb.setRecordIndicator(Constants.STATUS_DELETE);
				businessLineVb.setVerifier(0);

				retVal = doInsertionPendBusinessLineHeadersDc(businessLineVb);
				if (retVal != Constants.SUCCESSFUL_OPERATION) {
					exceptionCode = getResultObject(retVal);
					throw buildRuntimeCustomException(exceptionCode);
				}
			}
		}
		
		if ( vObject.isFeesFlag()) {
			FeesConfigHeaderVb feesConfigHeaderVb = new FeesConfigHeaderVb();
			feesConfigHeaderVb.setCountry(vObject.getCountry());
			feesConfigHeaderVb.setLeBook(vObject.getLeBook());
			feesConfigHeaderVb.setBusinessLineId(vObject.getBusinessLineId());
			feesConfigHeaderVb.setVerificationRequired(vObject.isVerificationRequired());
//			exceptionCode = feesConfigHeadersDao.doDeleteRecordForNonTrans(feesConfigHeaderVb);

			List<FeesConfigHeaderVb> feeConfigLst = feesConfigHeadersDao.getQueryResults(feesConfigHeaderVb,
					Constants.STATUS_ZERO);
			if (feeConfigLst != null && !feeConfigLst.isEmpty()) {
				for (FeesConfigHeaderVb feeVb : feeConfigLst) {
					if (feeVb.getFeesLineStatus() != Constants.PASSIVATE) {
						exceptionCode = feesConfigHeadersDao.doDeleteRecordForNonTrans(feeVb);
					}
				}
			}
		}
		// logger.info(exceptionCode.getErrorMsg());
//			}

		// vObjectlocal.setDateCreation(vObject.getDateCreation());
		vObject.setRecordIndicator(Constants.STATUS_DELETE);
		vObject.setVerifier(0);
		// return exceptionCode;
		return getResultObject(Constants.SUCCESSFUL_OPERATION);
	}

	@Override
	protected ExceptionCode doDeleteApprRecordForNonTrans(BusinessLineHeaderVb vObject) throws RuntimeCustomException {
		List<BusinessLineHeaderVb> collTemp = null;
		ExceptionCode exceptionCode = new ExceptionCode();
		strApproveOperation = Constants.DELETE;
		strErrorDesc = "";
		strCurrentOperation = Constants.DELETE;
		setServiceDefaults();
		BusinessLineHeaderVb vObjectlocal = null;
		vObject.setMaker(getIntCurrentUserId());
		if ("RUNNING".equalsIgnoreCase(getBuildStatus(vObject))) {
			exceptionCode = getResultObject(Constants.BUILD_IS_RUNNING);
			throw buildRuntimeCustomException(exceptionCode);
		}
		if (vObject.isBusinessFlag() && vObject.getBusinessLineStatus() != Constants.PASSIVATE) {
			collTemp = selectApprovedRecord(vObject);
			if (collTemp == null) {
				exceptionCode = getResultObject(Constants.ERRONEOUS_OPERATION);
				throw buildRuntimeCustomException(exceptionCode);
			}
			// If record already exists in the approved table, reject the addition
			if (!collTemp.isEmpty()) {
				vObjectlocal = collTemp.get(0);
				int intStaticDeletionFlag = getStatus(collTemp.get(0));
				if (intStaticDeletionFlag == Constants.PASSIVATE) {
					exceptionCode = getResultObject(Constants.CANNOT_DELETE_AN_INACTIVE_RECORD);
					throw buildRuntimeCustomException(exceptionCode);
				}
				/*
				 * retVal = doInsertionPendBusinessLineHeadersDc(dObj); if (retVal !=
				 * Constants.SUCCESSFUL_OPERATION){ exceptionCode = getResultObject(retVal);
				 * throw buildRuntimeCustomException(exceptionCode); }
				 * vObject.setDateCreation(vObjectlocal.getDateCreation());
				 */
				if (vObject.isStaticDelete()) {
					if (vObject.isBusinessFlag()) {
						BusinessLineHeaderVb businessLineVb = collTemp.get(0);
						businessLineVb.setMaker(getIntCurrentUserId());
						businessLineVb.setVerifier(getIntCurrentUserId());
						businessLineVb.setRecordIndicator(Constants.STATUS_ZERO);
						setStatus(businessLineVb, Constants.PASSIVATE);
						businessLineVb.setVerifier(getIntCurrentUserId());
						retVal = doUpdateApprHeader(businessLineVb);
						if (retVal == Constants.SUCCESSFUL_OPERATION) {
							exceptionCode = getResultObject(retVal);
						}
						String systemDate = getSystemDate();
						businessLineVb.setDateLastModified(systemDate);

						if (vObject.isStaticDelete()) {
							setStatus(vObjectlocal, Constants.STATUS_ZERO);
							setStatus(vObject, Constants.PASSIVATE);
							writeAuditLog(vObject, vObjectlocal);
						} else {
							writeAuditLog(null, vObject);
							vObject.setRecordIndicator(-1);
						}
					}
				} else {
					// delete the record from the Approve Table if the Del Type is H
					retVal = deleteBusinessLineHeaderAppr(vObject);
					String systemDate = getSystemDate();
					vObject.setDateLastModified(systemDate);
					List<BusinessLineGLVb> collTempGL = null;
					collTempGL = businessLineConfigGLDao.getBusinessGLDetails(vObject, 0);
					if (collTempGL != null && !collTempGL.isEmpty()) {
						int delCnt = businessLineConfigGLDao.deleteBusinessLineGlAppr(vObject);
						if (delCnt == Constants.ERRONEOUS_OPERATION) {
							exceptionCode = getResultObject(Constants.ERRONEOUS_OPERATION);
							throw buildRuntimeCustomException(exceptionCode);
						}
					}
					List<BlReconRuleVb> collTempBl = null;
					collTempBl = blReconRuleDao.getBlReconRuleDetails(vObject, Constants.STATUS_ZERO);
					if (collTempBl != null && !collTempBl.isEmpty()) {
						int delcnt = blReconRuleDao.doDeleteApprBlReconRule(vObject);
						if (delcnt == Constants.ERRONEOUS_OPERATION) {
							exceptionCode = getResultObject(Constants.ERRONEOUS_OPERATION);
							throw buildRuntimeCustomException(exceptionCode);
						}
					}
					List<BlReconRuleVb> collTempTaxBl = null;
					collTempTaxBl = taxBlReconRuleDao.getTaxBlReconRuleDetails(vObject, Constants.STATUS_ZERO);
					if(collTempTaxBl != null && collTempTaxBl.size() > 0) {
						 int delcnt = taxBlReconRuleDao.doDeleteApprTaxBlReconRule(vObject);
					}

					FeesConfigHeaderVb feesConfigHeaderVb = new FeesConfigHeaderVb();
					feesConfigHeaderVb.setCountry(vObject.getCountry());
					feesConfigHeaderVb.setLeBook(vObject.getLeBook());
					feesConfigHeaderVb.setBusinessLineId(vObject.getBusinessLineId());
					List<FeesConfigHeaderVb> feesLineDatalst = feesConfigHeadersDao.getQueryResults(feesConfigHeaderVb,
							Constants.STATUS_PENDING);
					List<FeesConfigHeaderVb> collTempFl = feesConfigHeadersDao.getQueryResults(feesConfigHeaderVb,
							Constants.STATUS_ZERO);
					if (collTempFl != null && !collTempFl.isEmpty()) {
						feesLineDatalst.addAll(collTempFl);
					}
					if (feesLineDatalst != null && !feesLineDatalst.isEmpty()) {
						for (FeesConfigHeaderVb feesConfigVb : feesLineDatalst) {
							if (feesConfigVb.getRecordIndicator() == Constants.STATUS_ZERO) {
								retVal = feesConfigHeadersDao.deleteFeesHeaderAppr(feesConfigVb);
								if (retVal != Constants.SUCCESSFUL_OPERATION) {
									exceptionCode = getResultObject(Constants.ERRONEOUS_OPERATION);
									throw buildRuntimeCustomException(exceptionCode);
								}
								retVal = feesConfigHeadersDao.getFeesConfigDetailsDao()
										.deleteFeesDetailsApprMain(feesConfigVb);
								if (retVal == Constants.ERRONEOUS_OPERATION) {
									exceptionCode = getResultObject(Constants.ERRONEOUS_OPERATION);
									throw buildRuntimeCustomException(exceptionCode);
								}
								retVal = feesConfigHeadersDao.getFeesConfigTierDao()
										.deleteFeesTierApprMain(feesConfigVb);
								if (retVal == Constants.ERRONEOUS_OPERATION) {
									exceptionCode = getResultObject(Constants.ERRONEOUS_OPERATION);
									throw buildRuntimeCustomException(exceptionCode);
								}
								feesConfigHeadersDao.writeAuditLog(null, feesConfigVb);
							}

							// Delete Pending Records
							feesConfigHeadersDao.deleteFeesHeaderPend(feesConfigVb);
							feesConfigHeadersDao.getFeesConfigDetailsDao().deleteFeesDetailsPendMain(feesConfigVb);
							feesConfigHeadersDao.getFeesConfigTierDao().deleteFeesTierPendMain(feesConfigVb);
						}
					}
				}

			}
		} else {
			exceptionCode = getResultObject(Constants.SUCCESSFUL_OPERATION);
		}
		if (vObject.isFeesFlag()) {
			FeesConfigHeaderVb feesConfigHeaderVb = new FeesConfigHeaderVb();
			feesConfigHeaderVb.setCountry(vObject.getCountry());
			feesConfigHeaderVb.setLeBook(vObject.getLeBook());
			feesConfigHeaderVb.setBusinessLineId(vObject.getBusinessLineId());
			feesConfigHeaderVb.setVerificationRequired(vObject.isVerificationRequired());
			feesConfigHeaderVb.setStaticDelete(vObject.isStaticDelete());
			List<FeesConfigHeaderVb> feeConfigLst = feesConfigHeadersDao.getQueryResults(feesConfigHeaderVb,
					Constants.STATUS_ZERO);
			if (feeConfigLst != null && !feeConfigLst.isEmpty()) {
				for (FeesConfigHeaderVb feeVb : feeConfigLst) {
					if (feeVb.getFeesLineStatus() != Constants.PASSIVATE) {
						feeVb.setStaticDelete(vObject.isStaticDelete());
						exceptionCode = feesConfigHeadersDao.doDeleteApprRecordForNonTrans(feeVb);
					}
				}
			}

		}
		if (exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION) {
			exceptionCode = getResultObject(exceptionCode.getErrorCode());
			throw buildRuntimeCustomException(exceptionCode);
		}
		return exceptionCode;
	}

	public List<TransLineHeaderVb> doselectproductRecord(TransLineHeaderVb dObj) {
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
						+ getDbFunction("DATEFUNC") + "(TPend.DATE_LAST_MODIFIED, 'dd-MM-yyyy " + getDbFunction("TIME")
						+ "') DATE_LAST_MODIFIED ," + getDbFunction("DATEFUNC") + "(TPend.DATE_CREATION, 'dd-MM-yyyy "
						+ getDbFunction("TIME") + "') DATE_CREATION "
						+ " FROM RA_MST_TRANS_LINE_HEADER TPend, NUM_SUB_TAB T1,ALPHA_SUB_TAB A1 WHERE "
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
//			ex.printStackTrace();
			return null;
		}

	}

	public BusinessLineConfigGLDao getBusinessLineConfigGLDao() {
		return businessLineConfigGLDao;
	}

	public void setBusinessLineConfigGLDao(BusinessLineConfigGLDao businessLineConfigGLDao) {
		this.businessLineConfigGLDao = businessLineConfigGLDao;
	}

	public BlReconRuleDao getBlReconRuleDao() {
		return blReconRuleDao;
	}

	public void setBlReconRuleDao(BlReconRuleDao blReconRuleDao) {
		this.blReconRuleDao = blReconRuleDao;
	}
}
