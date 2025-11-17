package com.vision.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.vision.authentication.CustomContextHolder;
import com.vision.exception.ExceptionCode;
import com.vision.exception.RuntimeCustomException;
import com.vision.util.CommonUtils;
import com.vision.util.Constants;
import com.vision.util.ValidationUtil;
import com.vision.vb.AlphaSubTabVb;
import com.vision.vb.ExceptionConfigCommentsVb;
import com.vision.vb.ExceptionConfigDetailsVb;
import com.vision.vb.ExceptionConfigHeaderVb;
import com.vision.vb.ExceptionManualFiltersVb;
import com.vision.vb.ExceptionTypeVb;
import com.vision.vb.SmartSearchVb;
import com.vision.vb.VisionUsersVb;

@Component
public class ExceptionConfigHeaderDao extends AbstractDao<ExceptionConfigHeaderVb> {
	@Autowired
	CommonDao commonDao;
	@Autowired
	ExceptionConfigDetailsDao manualExceptionConfigDetailsDao;
	@Autowired
	ExceptionFilterDao manualExceptionFilterDao;
	@Autowired
	CommonApiDao commonApiDao;

	@Value("${app.databaseType}")
	private String databaseType;

	@Override
	protected RowMapper getMapper() {
		RowMapper mapper = new RowMapper() {
			@Override
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				ExceptionConfigHeaderVb vObject = new ExceptionConfigHeaderVb();
				vObject.setCountry(rs.getString("COUNTRY"));
				vObject.setLeBook(rs.getString("LE_BOOK"));
				vObject.setExceptionReference(rs.getString("EXCEPTION_REFERENCE"));
				vObject.setExceptionReferenceDesc(rs.getString("Reference_Description"));
				vObject.setDetailCnt(rs.getInt("Detail_cnt"));
				vObject.setExceptionFlag(rs.getString("Exception_Flag"));
				vObject.setExceptionFlagAT(rs.getInt("Exception_Flag_AT"));
				vObject.setExceptionFlagDesc(rs.getString("Exception_Flag_DESC"));
				vObject.setPostedFlag(rs.getString("Posted_Flag"));
				vObject.setPostedFlagAT(rs.getInt("Posted_Flag_AT"));
				vObject.setPostedFlagDesc(rs.getString("Posted_Flag_DESC"));
				vObject.setCategory(rs.getString("Exception_Category"));
				vObject.setCategoryAt(rs.getInt("Exception_Category_at"));
				vObject.setCategoryDesc(rs.getString("Exception_Category_DESC"));
				vObject.setStartDate(rs.getString("Start_Date"));
				vObject.setEndDate(rs.getString("End_Date"));
				vObject.setTransLineType(rs.getString("Transaction_Type"));
				vObject.setTransLineTypeAt(rs.getInt("Transaction_Type_AT"));
				vObject.setTransLineTypeDesc(rs.getString("Transaction_Type_DESC"));
				vObject.setLatestPostedDate(rs.getString("LATEST_POSTED_DATE"));
				vObject.setAutoExceptionDate(rs.getString("AUTO_EXCEPTION_DATE"));
				vObject.setAutoExceptionType(rs.getString("Auto_Exception_Type"));
				vObject.setAutoExceptionTypeAT(rs.getInt("Auto_Exception_Type_AT"));
				vObject.setAutoExceptionTypeDesc(rs.getString("Auto_Exception_Type_DESC"));
				vObject.setAutoExceptionRemarks(rs.getString("AUTO_EXCEPTION_REMARKS"));
				vObject.setFeeBasis(rs.getString("Fee_Basis"));
				vObject.setFeeBasisAt(rs.getInt("Fee_Basis_AT"));
				vObject.setFeeBasisDesc(rs.getString("Fee_Basis_DESC"));
				vObject.setAutoExceptionFeeAmt(rs.getString("Auto_Exception_Fee_Amount"));
				vObject.setAutoExceptionFeePercentage(rs.getString("Auto_Exception_Fee_Percent"));
				vObject.setExceptionStatus(rs.getInt("Exception_Status"));
				vObject.setExceptionStatusDesc(rs.getString("Exception_Status_Desc"));
				vObject.setRecordIndicatorNt(rs.getInt("RECORD_INDICATOR_NT"));
				vObject.setRecordIndicator(rs.getInt("RECORD_INDICATOR"));
				vObject.setRecordIndicatorDesc(rs.getString("RECORD_INDICATOR_Desc"));
				vObject.setMaker(rs.getInt("MAKER"));
				vObject.setMakerName(rs.getString("MAKER_NAME"));
				vObject.setVerifier(rs.getInt("VERIFIER"));
				vObject.setVerifierName(rs.getString("VERIFIER_NAME"));
				vObject.setDateLastModified(rs.getString("DATE_LAST_MODIFIED"));
				vObject.setDateCreation(rs.getString("DATE_CREATION"));
				vObject.setRaSocRestriction(rs.getString("RA_SOC"));
				return vObject;
			}
		};
		return mapper;
	}

	protected RowMapper getDetailMapper() {
		RowMapper mapper = new RowMapper() {
			@Override
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				ExceptionConfigHeaderVb vObject = new ExceptionConfigHeaderVb();
				vObject.setCountry(rs.getString("COUNTRY"));
				vObject.setLeBook(rs.getString("LE_BOOK"));
				vObject.setExceptionReference(rs.getString("EXCEPTION_REFERENCE"));
				vObject.setExceptionReferenceDesc(rs.getString("Reference_Description"));
				vObject.setExceptionFlag(rs.getString("Exception_Flag"));
				vObject.setPostedFlag(rs.getString("Posted_Flag"));
				vObject.setCategory(rs.getString("exception_category"));
				vObject.setStartDate(rs.getString("Start_Date"));
				vObject.setEndDate(rs.getString("End_Date"));
				vObject.setTransLineType(rs.getString("Transaction_Type"));
				vObject.setLatestPostedDate(rs.getString("LATest_POSTED_DATE"));
				vObject.setAutoExceptionDate(rs.getString("AUTO_EXCEPTION_DATE"));
				vObject.setAutoExceptionType(rs.getString("Auto_Exception_Type"));
				vObject.setFeeBasis(rs.getString("Fee_Basis"));
				vObject.setAutoExceptionFeeAmt(rs.getString("Auto_Exception_Fee_Amount"));
				vObject.setAutoExceptionFeePercentage(rs.getString("Auto_Exception_Fee_Percent"));
				vObject.setAutoExceptionRemarks(rs.getString("AUTO_EXCEPTION_REMARKS"));
				vObject.setExceptionStatus(rs.getInt("Exception_Status"));
				vObject.setRecordIndicatorNt(rs.getInt("RECORD_INDICATOR_NT"));
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
	public List<ExceptionConfigHeaderVb> getQueryPopupResults(ExceptionConfigHeaderVb dObj) {
		Vector<Object> params = new Vector<Object>();
		StringBuffer strBufApprove = null;
		String orderBy = "";
		dObj.setVerificationRequired(true);
		strBufApprove = new StringBuffer(
				"Select * from ( SELECT TAppr.COUNTRY, TAppr.LE_BOOK,TAppr.Exception_Reference,TAppr.Reference_Description, "
						+ "TAPPR.EXCEPTION_FLAG,TAPPR.EXCEPTION_FLAG_AT,(select T1.ALPHA_SUBTAB_DESCRIPTION from ALPHA_SUB_TAB T1 "
						+ "      where t1.Alpha_tab = 7077 and T1.ALPHA_SUB_TAB=TAppr.EXCEPTION_FLAG) EXCEPTION_FLAG_DESC, "
						+ "TAPPR.POSTED_FLAG,TAPPR.POSTED_FLAG_AT,(select T1.ALPHA_SUBTAB_DESCRIPTION from ALPHA_SUB_TAB T1 "
						+ "      where t1.Alpha_tab = 207 and T1.ALPHA_SUB_TAB=TAppr.POSTED_FLAG) POSTED_FLAG_DESC, "
						+ "TAPPR.exception_category,TAPPR.exception_category_AT,(select T1.ALPHA_SUBTAB_DESCRIPTION from ALPHA_SUB_TAB T1 "
						+ "      where t1.Alpha_tab = 7079 and T1.ALPHA_SUB_TAB=TAppr.exception_category) exception_category_DESC, "
						+ "	"+dbFunctionFormats("TAPPR.START_DATE","DATE_FORMAT", null)+" START_DATE, "
						+ "	"+dbFunctionFormats("TAPPR.END_DATE","DATE_FORMAT", null)+" END_DATE, "
						+ "TAPPR.TRANSACTION_TYPE,TAPPR.TRANSACTION_TYPE_AT,(select T1.ALPHA_SUBTAB_DESCRIPTION from ALPHA_SUB_TAB T1 "
						+ "      where t1.Alpha_tab = TAppr.TRANSACTION_TYPE_AT and T1.ALPHA_SUB_TAB=TAppr.TRANSACTION_TYPE) TRANSACTION_TYPE_DESC, "
						+ "	"+dbFunctionFormats("TAPPR.LATEST_POSTED_DATE","DATETIME_FORMAT", null)+" LATEST_POSTED_DATE, "
						+ "	"+dbFunctionFormats("TAPPR.AUTO_EXCEPTION_DATE","DATE_FORMAT", null)+" AUTO_EXCEPTION_DATE, "
						+ "TAPPR.AUTO_EXCEPTION_TYPE,TAPPR.AUTO_EXCEPTION_TYPE_AT,(select T1.ALPHA_SUBTAB_DESCRIPTION from ALPHA_SUB_TAB T1 "
						+ "      where t1.Alpha_tab = 7078 and T1.ALPHA_SUB_TAB=TAppr.AUTO_EXCEPTION_TYPE) AUTO_EXCEPTION_TYPE_DESC, "
						+ " TAPPR.AUTO_EXCEPTION_REMARKS,"
						+ "TAPPR.FEE_BASIS,TAPPR.FEE_BASIS_AT,(select T1.ALPHA_SUBTAB_DESCRIPTION from ALPHA_SUB_TAB T1 "
						+ "      where t1.Alpha_tab = TAppr.FEE_BASIS_AT and T1.ALPHA_SUB_TAB=TAppr.FEE_BASIS) FEE_BASIS_DESC, "
						+ "TAPPR.AUTO_EXCEPTION_FEE_AMOUNT,TAPPR.AUTO_EXCEPTION_FEE_PERCENT,TAPPR.RA_SOC, "
						+ " TAppr.EXCEPTION_STATUS,T3.NUM_SUBTAB_DESCRIPTION EXCEPTION_STATUS_DESC,TAppr.RECORD_INDICATOR_NT, "
						+ " TAppr.RECORD_INDICATOR,T4.NUM_SUBTAB_DESCRIPTION RECORD_INDICATOR_DESC,"
						+ " TAppr. MAKER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "
						+ getDbFunction("NVL") + "(TAppr.MAKER,0) ) MAKER_NAME, "
						+ " TAppr.VERIFIER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "
						+ getDbFunction("NVL") + "(TAppr.VERIFIER,0) ) VERIFIER_NAME,  " + " "
						+ "	"+dbFunctionFormats("TAPPR.DATE_LAST_MODIFIED","DATETIME_FORMAT", null)+" DATE_LAST_MODIFIED, "
						+ " TAppr.DATE_LAST_MODIFIED DATE_LAST_MODIFIED_1,"
						+ "	"+dbFunctionFormats("TAPPR.DATE_CREATION","DATETIME_FORMAT", null)+" DATE_CREATION, "
						+ " (SELECT COUNT(1) FROM RA_EXCEPTION_DETAILS S1 WHERE S1.COUNTRY = TAppr.COUNTRY AND S1.LE_BOOK= TAppr.LE_BOOK "
						+ " AND S1.Exception_Reference = TAppr.Exception_Reference "
						+ " ) Detail_Cnt "
						+ " FROM RA_Exception_Header TAppr ,NUM_SUB_TAB T3,NUM_SUB_TAB T4     " + " Where  "
						+ "	 t3.NUM_tab = TAppr.EXCEPTION_STATUS_NT  "
						+ " and t3.NUM_sub_tab = TAppr.EXCEPTION_STATUS  "
						+ " and t4.NUM_tab = 175  "
						+ " and t4.NUM_sub_tab = TAppr.RECORD_INDICATOR ) TAppr");
		
		StringBuffer strBufPend = new StringBuffer(
				"Select * from ( SELECT TAppr.COUNTRY, TAppr.LE_BOOK,TAppr.Exception_Reference,TAppr.Reference_Description, "
						+ "TAPPR.EXCEPTION_FLAG,TAPPR.EXCEPTION_FLAG_AT,(select T1.ALPHA_SUBTAB_DESCRIPTION from ALPHA_SUB_TAB T1 "
						+ "      where t1.Alpha_tab = 7077 and T1.ALPHA_SUB_TAB=TAppr.EXCEPTION_FLAG) EXCEPTION_FLAG_DESC, "
						+ "TAPPR.POSTED_FLAG,TAPPR.POSTED_FLAG_AT,(select T1.ALPHA_SUBTAB_DESCRIPTION from ALPHA_SUB_TAB T1 "
						+ "      where t1.Alpha_tab = 207 and T1.ALPHA_SUB_TAB=TAppr.POSTED_FLAG) POSTED_FLAG_DESC, "
						+ "TAPPR.exception_category,TAPPR.exception_category_AT,(select T1.ALPHA_SUBTAB_DESCRIPTION from ALPHA_SUB_TAB T1 "
						+ "      where t1.Alpha_tab = 7079 and T1.ALPHA_SUB_TAB=TAppr.exception_category) exception_category_DESC, "
						+ "	"+dbFunctionFormats("TAPPR.START_DATE","DATE_FORMAT", null)+" START_DATE, "
						+ "	"+dbFunctionFormats("TAPPR.END_DATE","DATE_FORMAT", null)+" END_DATE, "
						+ "TAPPR.TRANSACTION_TYPE,TAPPR.TRANSACTION_TYPE_AT,(select T1.ALPHA_SUBTAB_DESCRIPTION from ALPHA_SUB_TAB T1 "
						+ "      where t1.Alpha_tab = TAppr.TRANSACTION_TYPE_AT and T1.ALPHA_SUB_TAB=TAppr.TRANSACTION_TYPE) TRANSACTION_TYPE_DESC, "
						+ "	"+dbFunctionFormats("TAPPR.LATEST_POSTED_DATE","DATETIME_FORMAT", null)+" LATEST_POSTED_DATE, "
						+ "	"+dbFunctionFormats("TAPPR.AUTO_EXCEPTION_DATE","DATE_FORMAT", null)+" AUTO_EXCEPTION_DATE, "
						+ "TAPPR.AUTO_EXCEPTION_TYPE,TAPPR.AUTO_EXCEPTION_TYPE_AT,(select T1.ALPHA_SUBTAB_DESCRIPTION from ALPHA_SUB_TAB T1 "
						+ "      where t1.Alpha_tab = 7078 and T1.ALPHA_SUB_TAB=TAppr.AUTO_EXCEPTION_TYPE) AUTO_EXCEPTION_TYPE_DESC, "
						+ " TAPPR.AUTO_EXCEPTION_REMARKS,"
						+ "TAPPR.FEE_BASIS,TAPPR.FEE_BASIS_AT,(select T1.ALPHA_SUBTAB_DESCRIPTION from ALPHA_SUB_TAB T1 "
						+ "      where t1.Alpha_tab = TAppr.FEE_BASIS_AT and T1.ALPHA_SUB_TAB=TAppr.FEE_BASIS) FEE_BASIS_DESC, "
						+ "TAPPR.AUTO_EXCEPTION_FEE_AMOUNT,TAPPR.AUTO_EXCEPTION_FEE_PERCENT,TAPPR.RA_SOC, "
						+ " TAppr.EXCEPTION_STATUS,T3.NUM_SUBTAB_DESCRIPTION EXCEPTION_STATUS_DESC,TAppr.RECORD_INDICATOR_NT, "
						+ " TAppr.RECORD_INDICATOR,T4.NUM_SUBTAB_DESCRIPTION RECORD_INDICATOR_DESC,"
						+ " TAppr. MAKER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "
						+ getDbFunction("NVL") + "(TAppr.MAKER,0) ) MAKER_NAME, "
						+ " TAppr.VERIFIER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "
						+ getDbFunction("NVL") + "(TAppr.VERIFIER,0) ) VERIFIER_NAME,  " + " "
						+ "	"+dbFunctionFormats("TAPPR.DATE_LAST_MODIFIED","DATETIME_FORMAT", null)+" DATE_LAST_MODIFIED, "
						+ " TAppr.DATE_LAST_MODIFIED DATE_LAST_MODIFIED_1,"
						+ "	"+dbFunctionFormats("TAPPR.DATE_CREATION","DATETIME_FORMAT", null)+" DATE_CREATION, "
						+ " (SELECT COUNT(1) FROM RA_EXCEPTION_DETAILS_PEND S1 WHERE S1.COUNTRY = TAppr.COUNTRY AND S1.LE_BOOK= TAppr.LE_BOOK "
						+ " AND S1.Exception_Reference = TAppr.Exception_Reference "
						+ " ) Detail_Cnt "
						+ " FROM RA_Exception_Header_Pend TAppr ,NUM_SUB_TAB T3,NUM_SUB_TAB T4     " + " Where  "
						+ "	 t3.NUM_tab = TAppr.EXCEPTION_STATUS_NT  "
						+ " and t3.NUM_sub_tab = TAppr.EXCEPTION_STATUS  "
						+ " and t4.NUM_tab = 175  "
						+ " and t4.NUM_sub_tab = TAppr.RECORD_INDICATOR ) TPend");

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
						CommonUtils.addToQuerySearch(" upper(TPend.COUNTRY) " + val, strBufPend, data.getJoinType());
						break;

					case "leBook":
						CommonUtils.addToQuerySearch(" upper(TAPPR.LE_BOOK) " + val, strBufApprove, data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.LE_BOOK) " + val, strBufPend, data.getJoinType());
						break;

					case "exceptionReference":
						CommonUtils.addToQuerySearch(" upper(TAPPR.Exception_Reference) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.Exception_Reference) " + val, strBufPend,
								data.getJoinType());
						break;

					case "exceptionReferenceDesc":
						CommonUtils.addToQuerySearch(" upper(TAPPR.Reference_Description) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.Reference_Description) " + val, strBufPend,
								data.getJoinType());
						break;
						
					case "categoryDesc":
						CommonUtils.addToQuerySearch(" upper(TAPPR.exception_category_DESC) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.exception_category_DESC) " + val, strBufPend,
								data.getJoinType());
						break;
						
					case "transLineTypeDesc":
						CommonUtils.addToQuerySearch(" upper(TAPPR.TRANSACTION_TYPE_DESC) " + val, strBufApprove,data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.TRANSACTION_TYPE_DESC) " + val, strBufPend,data.getJoinType());
						break;
						
					case "postedFlagDesc":
						CommonUtils.addToQuerySearch(" upper(TAPPR.POSTED_FLAG_DESC) " + val, strBufApprove,data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.POSTED_FLAG_DESC) " + val, strBufPend,data.getJoinType());
						break;	

					case "latestPostedDate":
						CommonUtils.addToQuerySearch(" upper(TAPPR.LATEST_POSTED_DATE) "+val, strBufApprove, data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.LATEST_POSTED_DATE) "+val, strBufPend, data.getJoinType());
						break;
						
					case "exceptionStatusDesc":
						CommonUtils.addToQuerySearch(" upper(TAPPR.EXCEPTION_STATUS_DESC) " + val, strBufApprove,data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.EXCEPTION_STATUS_DESC) " + val, strBufPend,data.getJoinType());
						break;

					case "recordIndicatorDesc":
						CommonUtils.addToQuerySearch(" upper(TAPPR.RECORD_INDICATOR_DESC) " + val, strBufApprove,data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.RECORD_INDICATOR_DESC) " + val, strBufPend,data.getJoinType());
						break;

					default:
					}
					count++;
				}
			}
			VisionUsersVb visionUsersVb = CustomContextHolder.getContext();
			visionUsersVb = commonDao.getRestrictionInfo(visionUsersVb);
			if (("Y".equalsIgnoreCase(visionUsersVb.getUpdateRestriction()))) {
				if (ValidationUtil.isValid(visionUsersVb.getCountry())) {
					CommonUtils.addToQuery("TAPPR.COUNTRY"+getDbFunction("PIPELINE", null)+"'-'"+getDbFunction("PIPELINE", null)+"TAPPR.LE_BOOK IN (" + visionUsersVb.getCountry() + ") ", strBufApprove);
				}
				if(ValidationUtil.isValid(visionUsersVb.getClebTrasnBusline()) && ValidationUtil.isValid(getUserSoc())) {
					strBufApprove.append("and  "+getDbFunction("NVL", null)+"(TAPPR.ra_soc,'') != '' and not exists ( "
									+ " select CLEB_BL from VU_CLEB_EXP_TLBL t2 where t2.country = TAPPR.country " 
									+ " and t2.le_book = TAPPR.le_book and t2.Exception_Reference = TAPPR.exception_reference "
									+getDbFunction("EXCEPT", null)+" "
									+ "select CLEB_BL from VU_CLEB_SOC_TLBL t2 where vision_id = '"+intCurrentUserId+"')"
									+ " AND EXISTS ( "
									+ " SELECT  'X' FROM  VU_CLEB_EXP_TLBL T2 "
									+ " WHERE "
									+ " T2.COUNTRY = TAPPR.COUNTRY " 
									+ " AND T2.LE_BOOK = TAPPR.LE_BOOK " 
									+ " AND T2.EXCEPTION_REFERENCE = TAPPR.EXCEPTION_REFERENCE )");
				}
			}
			orderBy = " ORDER BY DATE_LAST_MODIFIED_1 DESC,EXCEPTION_REFERENCE ";
			return getQueryPopupResults(dObj, strBufPend, strBufApprove, null, orderBy, params);
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	@Override
	public List<ExceptionConfigHeaderVb> getQueryResults(ExceptionConfigHeaderVb dObj, int intStatus) {
		List<ExceptionConfigHeaderVb> collTemp = null;
		setServiceDefaults();
		String strQueryAppr = null;
		String tableName = "RA_Exception_Header_Pend";
		if(intStatus == 0)
			tableName = "RA_Exception_Header";
		
		strQueryAppr = new String(" SELECT TAppr.COUNTRY, TAppr.LE_BOOK,TAppr.Exception_Reference,TAppr.Reference_Description, "
				+ "TAPPR.EXCEPTION_FLAG,TAPPR.EXCEPTION_FLAG_AT,(select T1.ALPHA_SUBTAB_DESCRIPTION from ALPHA_SUB_TAB T1 "
				+ "      where t1.Alpha_tab = TAppr.EXCEPTION_FLAG_AT and T1.ALPHA_SUB_TAB=TAppr.EXCEPTION_FLAG) EXCEPTION_FLAG_DESC, "
				+ "TAPPR.POSTED_FLAG,TAPPR.POSTED_FLAG_AT,(select T1.ALPHA_SUBTAB_DESCRIPTION from ALPHA_SUB_TAB T1 "
				+ "      where t1.Alpha_tab = TAppr.POSTED_FLAG_AT and T1.ALPHA_SUB_TAB=TAppr.POSTED_FLAG) POSTED_FLAG_DESC, "
				+ "TAPPR.exception_category,TAPPR.exception_category_AT,(select T1.ALPHA_SUBTAB_DESCRIPTION from ALPHA_SUB_TAB T1 "
				+ "      where t1.Alpha_tab = TAppr.exception_category_AT and T1.ALPHA_SUB_TAB=TAppr.exception_category) exception_category_DESC, "
				+ "	"+dbFunctionFormats("TAPPR.START_DATE","DATE_FORMAT", null)+" START_DATE, "
				+ "	"+dbFunctionFormats("TAPPR.END_DATE","DATE_FORMAT", null)+" END_DATE, "
				+ "TAPPR.TRANSACTION_TYPE,TAPPR.TRANSACTION_TYPE_AT,(select T1.ALPHA_SUBTAB_DESCRIPTION from ALPHA_SUB_TAB T1 "
				+ "      where t1.Alpha_tab = TAppr.TRANSACTION_TYPE_AT and T1.ALPHA_SUB_TAB=TAppr.TRANSACTION_TYPE) TRANSACTION_TYPE_DESC, "
				+ "	"+dbFunctionFormats("TAPPR.LATEST_POSTED_DATE","DATE_FORMAT", null)+" LATEST_POSTED_DATE, "
				+ "	"+dbFunctionFormats("TAPPR.AUTO_EXCEPTION_DATE","DATE_FORMAT", null)+" AUTO_EXCEPTION_DATE, "
				+ "TAPPR.AUTO_EXCEPTION_TYPE,TAPPR.AUTO_EXCEPTION_TYPE_AT,(select T1.ALPHA_SUBTAB_DESCRIPTION from ALPHA_SUB_TAB T1 "
				+ "      where t1.Alpha_tab = TAppr.AUTO_EXCEPTION_TYPE_AT and T1.ALPHA_SUB_TAB=TAppr.AUTO_EXCEPTION_TYPE) AUTO_EXCEPTION_TYPE_DESC, "
				+ " TAPPR.AUTO_EXCEPTION_REMARKS,"
				+ "TAPPR.FEE_BASIS,TAPPR.FEE_BASIS_AT,(select T1.ALPHA_SUBTAB_DESCRIPTION from ALPHA_SUB_TAB T1 "
				+ "      where t1.Alpha_tab = TAppr.FEE_BASIS_AT and T1.ALPHA_SUB_TAB=TAppr.FEE_BASIS) FEE_BASIS_DESC, "
				+ "TAPPR.AUTO_EXCEPTION_FEE_AMOUNT,TAPPR.AUTO_EXCEPTION_FEE_PERCENT, "
				+ " TAppr.EXCEPTION_STATUS,T3.NUM_SUBTAB_DESCRIPTION EXCEPTION_STATUS_DESC,TAppr.RECORD_INDICATOR_NT, "
				+ " TAppr.RECORD_INDICATOR,T4.NUM_SUBTAB_DESCRIPTION RECORD_INDICATOR_DESC,"
				+ " TAppr. MAKER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "
				+ getDbFunction("NVL") + "(TAppr.MAKER,0) ) MAKER_NAME, "
				+ " TAppr.VERIFIER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "
				+ getDbFunction("NVL") + "(TAppr.VERIFIER,0) ) VERIFIER_NAME,  " + " "
				+ "	"+dbFunctionFormats("TAPPR.DATE_LAST_MODIFIED","DATETIME_FORMAT", null)+" DATE_LAST_MODIFIED, "
				+ "TAppr.DATE_LAST_MODIFIED DATE_LAST_MODIFIED_1,"
				+ "	"+dbFunctionFormats("TAPPR.DATE_CREATION","DATETIME_FORMAT", null)+" DATE_CREATION "
				+ " FROM "+tableName+" TAppr ,NUM_SUB_TAB T3,NUM_SUB_TAB T4     " + " Where  "
				+ "	 t3.NUM_tab = TAppr.EXCEPTION_STATUS_NT  "
				+ " and t3.NUM_sub_tab = TAppr.EXCEPTION_STATUS  "
				+ " and t4.NUM_tab = TAppr.RECORD_INDICATOR_NT  "
				+ " and t4.NUM_sub_tab = TAppr.RECORD_INDICATOR And TAPPR.COUNTRY = ? "
				+ " and TAPPR.LE_BOOK = ? "
				+ " and TAPPR.Exception_Reference=? ");

		Object objParams[] = new Object[3];
		objParams[0] = new String(dObj.getCountry());
		objParams[1] = new String(dObj.getLeBook());
		objParams[2] = new String(dObj.getExceptionReference());
		try {
			collTemp = getJdbcTemplate().query(strQueryAppr.toString(), objParams, getDetailMapper());
			return collTemp;
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("Error: getQueryResults Exception :   ");
			return null;
		}
	}

	@Override
	public List<ExceptionConfigHeaderVb> getQueryResultsForReview(ExceptionConfigHeaderVb dObj, int intStatus) {
		List<ExceptionConfigHeaderVb> collTemp = null;
		setServiceDefaults();
		String strQueryAppr = null;
		String strQueryPend = null;
		strQueryAppr = new String(" SELECT TAppr.COUNTRY, TAppr.LE_BOOK,TAppr.Exception_Reference, TAppr.Reference_Description, "
							+ "TAPPR.EXCEPTION_FLAG,TAPPR.EXCEPTION_FLAG_AT,(select T1.ALPHA_SUBTAB_DESCRIPTION from ALPHA_SUB_TAB T1 "
							+ "      where t1.Alpha_tab = 7077 and T1.ALPHA_SUB_TAB=TAppr.EXCEPTION_FLAG) EXCEPTION_FLAG_DESC, "
							+ "TAPPR.POSTED_FLAG,TAPPR.POSTED_FLAG_AT,(select T1.ALPHA_SUBTAB_DESCRIPTION from ALPHA_SUB_TAB T1 "
							+ "      where t1.Alpha_tab = 207 and T1.ALPHA_SUB_TAB=TAppr.POSTED_FLAG) POSTED_FLAG_DESC, "
							+ "TAPPR.exception_category,TAPPR.exception_category_AT,(select T1.ALPHA_SUBTAB_DESCRIPTION from ALPHA_SUB_TAB T1 "
							+ "      where t1.Alpha_tab = 7079 and T1.ALPHA_SUB_TAB=TAppr.exception_category) exception_category_DESC, "
							+ "	"+dbFunctionFormats("TAPPR.START_DATE","DATE_FORMAT", null)+" START_DATE, "
							+ "	"+dbFunctionFormats("TAPPR.END_DATE","DATE_FORMAT", null)+" END_DATE, "
							+ "TAPPR.TRANSACTION_TYPE,TAPPR.TRANSACTION_TYPE_AT,(select T1.ALPHA_SUBTAB_DESCRIPTION from ALPHA_SUB_TAB T1 "
							+ "      where t1.Alpha_tab = TAppr.TRANSACTION_TYPE_AT and T1.ALPHA_SUB_TAB=TAppr.TRANSACTION_TYPE) TRANSACTION_TYPE_DESC, "
							+ "	"+dbFunctionFormats("TAPPR.LATEST_POSTED_DATE","DATE_FORMAT", null)+" LATEST_POSTED_DATE, "
							+ "	"+dbFunctionFormats("TAPPR.AUTO_EXCEPTION_DATE","DATE_FORMAT", null)+" AUTO_EXCEPTION_DATE, "
							+ "TAPPR.AUTO_EXCEPTION_TYPE,TAPPR.AUTO_EXCEPTION_TYPE_AT,(select T1.ALPHA_SUBTAB_DESCRIPTION from ALPHA_SUB_TAB T1 "
							+ "      where t1.Alpha_tab = 7078 and T1.ALPHA_SUB_TAB=TAppr.AUTO_EXCEPTION_TYPE) AUTO_EXCEPTION_TYPE_DESC, "
							+ " TAPPR.AUTO_EXCEPTION_REMARKS,"
							+ "TAPPR.FEE_BASIS,TAPPR.FEE_BASIS_AT,(select T1.ALPHA_SUBTAB_DESCRIPTION from ALPHA_SUB_TAB T1 "
							+ "      where t1.Alpha_tab = TAppr.FEE_BASIS_AT and T1.ALPHA_SUB_TAB=TAppr.FEE_BASIS) FEE_BASIS_DESC, "
							+ "TAPPR.AUTO_EXCEPTION_FEE_AMOUNT,TAPPR.AUTO_EXCEPTION_FEE_PERCENT, "
							+ " TAppr.EXCEPTION_STATUS,T3.NUM_SUBTAB_DESCRIPTION EXCEPTION_STATUS_DESC, TAppr.RECORD_INDICATOR_NT,"
							+ " TAppr.RECORD_INDICATOR,T4.NUM_SUBTAB_DESCRIPTION RECORD_INDICATOR_DESC,"
							+ " TAppr. MAKER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "
							+ getDbFunction("NVL") + "(TAppr.MAKER,0) ) MAKER_NAME, "
							+ " TAppr.VERIFIER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "
							+ getDbFunction("NVL") + "(TAppr.VERIFIER,0) ) VERIFIER_NAME,  " + " "
							+ getDbFunction("DATEFUNC") + "(TAppr.DATE_LAST_MODIFIED, 'dd-MMM-yyyy "
							+ getDbFunction("TIME") + "') DATE_LAST_MODIFIED ,TAppr.DATE_LAST_MODIFIED DATE_LAST_MODIFIED_1,"
							+ getDbFunction("DATEFUNC") + "(TAppr.DATE_CREATION, 'dd-MMM-yyyy "
							+ getDbFunction("TIME") + "') DATE_CREATION , 0 Detail_Cnt"
						+ " FROM RA_Exception_Header TAppr ,NUM_SUB_TAB T3,NUM_SUB_TAB T4     " + " Where  "
						+ "	 t3.NUM_tab = TAppr.EXCEPTION_STATUS_NT  "
						+ " and t3.NUM_sub_tab = TAppr.EXCEPTION_STATUS  "
						+ " and t4.NUM_tab = TAppr.RECORD_INDICATOR_NT  "
						+ " and t4.NUM_sub_tab = TAppr.RECORD_INDICATOR and TAppr.COUNTRY = ? "
				+ " and TAppr.LE_BOOK = ? "
				+ " and TAppr.Exception_Reference =?");
		

		strQueryPend = new String(" SELECT TPend.COUNTRY, TPend.LE_BOOK,TPend.Exception_Reference, TPend.Reference_Description, "
							+ "TPend.EXCEPTION_FLAG,TPend.EXCEPTION_FLAG_AT,(select T1.ALPHA_SUBTAB_DESCRIPTION from ALPHA_SUB_TAB T1 "
							+ "      where t1.Alpha_tab = 7077 and T1.ALPHA_SUB_TAB=TPend.EXCEPTION_FLAG) EXCEPTION_FLAG_DESC, "
							+ "TPend.POSTED_FLAG,TPend.POSTED_FLAG_AT,(select T1.ALPHA_SUBTAB_DESCRIPTION from ALPHA_SUB_TAB T1 "
							+ "      where t1.Alpha_tab = 207 and T1.ALPHA_SUB_TAB=TPend.POSTED_FLAG) POSTED_FLAG_DESC, "
							+ "TPend.exception_category,TPend.exception_category_AT,(select T1.ALPHA_SUBTAB_DESCRIPTION from ALPHA_SUB_TAB T1 "
							+ "      where t1.Alpha_tab = 7079 and T1.ALPHA_SUB_TAB=TPend.exception_category) exception_category_DESC, "
							+ "	"+dbFunctionFormats("TPend.START_DATE","DATE_FORMAT", null)+" START_DATE, "
							+ "	"+dbFunctionFormats("TPend.END_DATE","DATE_FORMAT", null)+" END_DATE, "
							+ "TPend.TRANSACTION_TYPE,TPend.TRANSACTION_TYPE_AT,(select T1.ALPHA_SUBTAB_DESCRIPTION from ALPHA_SUB_TAB T1 "
							+ "      where t1.Alpha_tab = TPend.TRANSACTION_TYPE_AT and T1.ALPHA_SUB_TAB=TPend.TRANSACTION_TYPE) TRANSACTION_TYPE_DESC, "
							+ "	"+dbFunctionFormats("TPend.LATEST_POSTED_DATE","DATE_FORMAT", null)+" LATEST_POSTED_DATE, "
							+ "	"+dbFunctionFormats("TPend.AUTO_EXCEPTION_DATE","DATE_FORMAT", null)+" AUTO_EXCEPTION_DATE, "
							+ "TPend.AUTO_EXCEPTION_TYPE,TPend.AUTO_EXCEPTION_TYPE_AT,(select T1.ALPHA_SUBTAB_DESCRIPTION from ALPHA_SUB_TAB T1 "
							+ "      where t1.Alpha_tab = 7078 and T1.ALPHA_SUB_TAB=TPend.AUTO_EXCEPTION_TYPE) AUTO_EXCEPTION_TYPE_DESC, "
							+ " TPend.AUTO_EXCEPTION_REMARKS,"
							+ "TPend.FEE_BASIS,TPend.FEE_BASIS_AT,(select T1.ALPHA_SUBTAB_DESCRIPTION from ALPHA_SUB_TAB T1 "
							+ "      where t1.Alpha_tab = TPend.FEE_BASIS_AT and T1.ALPHA_SUB_TAB=TPend.FEE_BASIS) FEE_BASIS_DESC, "
							+ "TPend.AUTO_EXCEPTION_FEE_AMOUNT,TPend.AUTO_EXCEPTION_FEE_PERCENT, "
							+ " TPend.EXCEPTION_STATUS,T3.NUM_SUBTAB_DESCRIPTION EXCEPTION_STATUS_DESC, TPend.RECORD_INDICATOR_NT,"
							+ " TPend.RECORD_INDICATOR,T4.NUM_SUBTAB_DESCRIPTION RECORD_INDICATOR_DESC,"
							+ " TPend. MAKER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "
							+ getDbFunction("NVL") + "(TPend.MAKER,0) ) MAKER_NAME, "
							+ " TPend.VERIFIER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "
							+ getDbFunction("NVL") + "(TPend.VERIFIER,0) ) VERIFIER_NAME,  " + " "
							+ getDbFunction("DATEFUNC") + "(TPend.DATE_LAST_MODIFIED, 'dd-MMM-yyyy "
							+ getDbFunction("TIME") + "') DATE_LAST_MODIFIED ,TPend.DATE_LAST_MODIFIED DATE_LAST_MODIFIED_1,"
							+ getDbFunction("DATEFUNC") + "(TPend.DATE_CREATION, 'dd-MMM-yyyy "
							+ getDbFunction("TIME") + "') DATE_CREATION , 0 Detail_Cnt"
						+ " FROM RA_Exception_Header TPend ,NUM_SUB_TAB T3,NUM_SUB_TAB T4     " + " Where  "
						+ "	 t3.NUM_tab = TPend.EXCEPTION_STATUS_NT  "
						+ " and t3.NUM_sub_tab = TPend.EXCEPTION_STATUS  "
						+ " and t4.NUM_tab = TPend.RECORD_INDICATOR_NT  "
						+ " and t4.NUM_sub_tab = TPend.RECORD_INDICATOR and TPend.COUNTRY = ? "
				+ " and TPend.LE_BOOK = ? "
				+ " and TPend.Exception_Reference =?");
		
		Object objParams[] = new Object[3];
		objParams[0] = new String(dObj.getCountry());// country
		objParams[1] = new String(dObj.getLeBook());
		objParams[2] = new String(dObj.getExceptionReference());
		try {
			if (intStatus == 0) {
				logger.info("Executing approved query");
				collTemp = getJdbcTemplate().query(strQueryAppr.toString(), objParams, getMapper());
			} else {
				logger.info("Executing pending query");
				collTemp = getJdbcTemplate().query(strQueryPend.toString(), objParams, getMapper());
			}
			return collTemp;
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("Error: getQueryResults Exception :   ");
			return null;
		}
	}

	protected int doInsertionAppr(ExceptionConfigHeaderVb vObject) {
		String query = " Insert Into RA_Exception_Header(COUNTRY,LE_BOOK,Exception_Reference,Reference_Description,Exception_category, "
				+ " Exception_Flag_AT,Exception_Flag,Posted_Flag,Start_Date, END_DATE, Transaction_Type,"
				+ "Latest_Posted_Date, Auto_Exception_Date,"
				+ "Auto_Exception_type, 	Auto_Exception_remarks, fee_basis,"
				+" Auto_Exception_Fee_Amount,Auto_Exception_Fee_Percent,RA_SOC,"
				+ "	EXCEPTION_Status_NT, EXCEPTION_Status,"
				+ "RECORD_INDICATOR_NT,RECORD_INDICATOR,MAKER,VERIFIER,DATE_LAST_MODIFIED,DATE_CREATION) "
				+ " Values (?,?,?,?,?,?,?,?,?,?,?,"+getDbFunction("SYSDATE")+",?,?,?,?,?,?,?,?,?,?,?,?,?," + getDbFunction("SYSDATE") + "," + getDbFunction("DATE_CREATION") + ")";

		Object[] args = { vObject.getCountry(), vObject.getLeBook(), 
				vObject.getExceptionReference(),vObject.getExceptionReferenceDesc(), vObject.getCategory(),
				vObject.getExceptionFlagAT(),vObject.getExceptionFlag(), vObject.getPostedFlag(),
				vObject.getStartDate(),
				vObject.getEndDate(), vObject.getTransLineType(),
				vObject.getAutoExceptionDate(),
				vObject.getAutoExceptionType(), vObject.getAutoExceptionRemarks(), vObject.getFeeBasis(),
				ValidationUtil.isValid(vObject.getAutoExceptionFeeAmt())?vObject.getAutoExceptionFeeAmt().replaceAll(",", ""):"0", 
				ValidationUtil.isValid(vObject.getAutoExceptionFeePercentage())?vObject.getAutoExceptionFeePercentage().replaceAll(",", ""):"0",
				vObject.getRaSocRestriction(),
				vObject.getExceptionStatusNt(),
				vObject.getExceptionStatus(), vObject.getRecordIndicatorNt(), 
				vObject.getRecordIndicator(),
				vObject.getMaker(), vObject.getVerifier(),vObject.getDateCreation()};
		return getJdbcTemplate().update(query, args);
	}

	protected int doInsertionPend(ExceptionConfigHeaderVb vObject) {
		String query = " Insert Into RA_Exception_Header_pend(COUNTRY,LE_BOOK,Exception_Reference,Reference_Description, Exception_category,"
				+ " Exception_Flag,Posted_Flag,Start_Date, END_DATE, Transaction_Type,"
				+ "Latest_Posted_Date, Auto_Exception_Date,"
				+ "Auto_Exception_type, 	Auto_Exception_remarks, fee_basis,"
				+" Auto_Exception_Fee_Amount,Auto_Exception_Fee_Percent,"
				+ "	EXCEPTION_Status_NT, EXCEPTION_Status,"
				+ "RECORD_INDICATOR_NT,RECORD_INDICATOR,MAKER,VERIFIER,DATE_LAST_MODIFIED,DATE_CREATION) "
				+ " Values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?," + getDbFunction("SYSDATE") + ","
				+ getDbFunction("SYSDATE") + ")";

		Object[] args = { vObject.getCountry(), vObject.getLeBook(), 
				vObject.getExceptionReference(),vObject.getExceptionReferenceDesc(),vObject.getCategory(),
				vObject.getExceptionFlag(), vObject.getPostedFlag(),
				vObject.getStartDate(),
				vObject.getEndDate(), vObject.getTransLineType(),
				vObject.getLatestPostedDate(), vObject.getAutoExceptionDate(),
				vObject.getAutoExceptionType(), vObject.getAutoExceptionRemarks(), vObject.getFeeBasis(),
				vObject.getAutoExceptionFeeAmt().replaceAll(",", ""), 
				vObject.getAutoExceptionFeePercentage().replaceAll(",", ""),
				vObject.getExceptionStatusNt(),
				vObject.getExceptionStatus(), vObject.getRecordIndicatorNt(), 
				vObject.getRecordIndicator(),
				vObject.getMaker(), vObject.getVerifier() };
		return getJdbcTemplate().update(query, args);
	}
	@Override
	protected int deletePendingRecord(ExceptionConfigHeaderVb vObject) {
		String query = "Delete from RA_Exception_Header_Pend  WHERE COUNTRY= ? AND LE_BOOK= ? AND EXCEPTION_REFERENCE = ?";
		Object[] args = { vObject.getCountry(), vObject.getLeBook(), vObject.getExceptionReference() };
		return getJdbcTemplate().update(query, args);
	}

	@Override
	protected List<ExceptionConfigHeaderVb> selectApprovedRecord(ExceptionConfigHeaderVb vObject) {
		return getQueryResults(vObject, Constants.STATUS_ZERO);
	}

	@Override
	public List<ExceptionConfigHeaderVb> doSelectPendingRecord(ExceptionConfigHeaderVb vObject) {
		return getQueryResults(vObject, Constants.STATUS_PENDING);
	}

	@Override
	protected int getStatus(ExceptionConfigHeaderVb records) {
		return records.getExceptionStatus();
	}

	@Override
	protected void setStatus(ExceptionConfigHeaderVb vObject, int status) {
		vObject.setExceptionStatus(status);
	}

	public int doUpdateAllAppr(ExceptionConfigHeaderVb dObj){
		String query = "Update RA_Exception_Header_Pend set record_indicator=?, verifier =? ,date_last_modified = "+getDbFunction("SYSDATE", null)+"  WHERE Country = ? and "
				+ " LE_Book = ? And Exception_Reference = ?";
		Object objParams[] = new Object[5];
		objParams[0] = new Integer(dObj.getRecordIndicator());
		objParams[1] = dObj.getVerifier();
		objParams[2] = new String(dObj.getCountry());
		objParams[3] = new String(dObj.getLeBook());
		objParams[4] = new String(dObj.getExceptionReference());
		return getJdbcTemplate().update(query,objParams);
	}
	public int approveRecord(ExceptionConfigHeaderVb dObj){
		String query = "Update RA_Exception_Header set record_indicator=?, verifier =?,Posted_Flag ='Y',"
				+ " Latest_posted_Date = "+getDbFunction("SYSDATE")+" WHERE Country = ? and "
				+ " LE_Book = ? And Exception_Reference = ?";
		Object objParams[] = new Object[5];
		objParams[0] = new Integer(dObj.getRecordIndicator());
		objParams[1] = new Integer((int) dObj.getVerifier());
		objParams[2] = new String(dObj.getCountry());
		objParams[3] = new String(dObj.getLeBook());
		objParams[4] = new String(dObj.getExceptionReference());
		return getJdbcTemplate().update(query,objParams);
	}
	public List<AlphaSubTabVb> findCountry() throws DataAccessException {
		String sql = "Select Country ID,Country DESCRIPTION from LE_Book";
		return  getJdbcTemplate().query(sql, getAlphaTabMapper());
	}
	public List<AlphaSubTabVb> findLeBook(String country) throws DataAccessException {
		String sql = "Select LE_Book ID, LEB_DESCRIPTION DESCRIPTION from LE_Book where Country  = ? ";
		Object[] lParams = new Object[1];
		lParams[0] = country;
		return  getJdbcTemplate().query(sql,lParams, getAlphaTabMapper());
	}
	protected RowMapper getAlphaTabMapper(){
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
	public List<ExceptionManualFiltersVb> findSourceTable() throws DataAccessException {
		String sql = "SELECT TABLE_NAME,SOURCE_TABLE FROM RA_SOURCE_TABLE_MAPPINGS WHERE TABLE_STATUS = 0";
		return  getJdbcTemplate().query(sql, findSourceTableMapper());
	}
	public List<ExceptionManualFiltersVb> findTableColumns(String tableName) throws DataAccessException {
		String sql = "SELECT Col_Name,Alias_Name,Format_Type,Mag_Enable_Flag,Mag_Type,Mag_Selection_type, "+
			" Mag_Query_ID FROM RA_Exception_Filter_Cols where Table_Name =  ? "+
			" and Exc_Status =0";
		Object[] lParams = new Object[1];
		lParams[0] = tableName;
		return  getJdbcTemplate().query(sql,lParams, findTableColumnsMapper());
	}
	protected RowMapper findSourceTableMapper(){
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				ExceptionManualFiltersVb dObj = new ExceptionManualFiltersVb();
				dObj.setSourceTable(rs.getString("SOURCE_TABLE"));
				dObj.setTableName(rs.getString("TABLE_NAME"));
				return dObj;
			}
		};
		return mapper;
	}
	protected RowMapper findTableColumnsMapper(){
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				ExceptionManualFiltersVb dObj = new ExceptionManualFiltersVb();
				dObj.setColName(rs.getString("Col_Name"));
				dObj.setAliasName(rs.getString("Alias_Name"));
				dObj.setFormatType(rs.getString("Format_Type"));
				dObj.setMagEnableFlag(rs.getString("Mag_Enable_Flag"));
				dObj.setMagType(rs.getString("Mag_Type"));
				dObj.setMagSelectionType(rs.getString("Mag_Selection_type"));
				dObj.setMagQueryId(rs.getString("Mag_Query_ID"));
				return dObj;
			}
		};
		return mapper;
	}
	@Override
	public ExceptionCode doInsertApprRecordForNonTrans(ExceptionConfigHeaderVb vObject) throws RuntimeCustomException {
		List<ExceptionConfigHeaderVb> collTemp = null;
		VisionUsersVb visionUsersVb = CustomContextHolder.getContext();
		visionUsersVb = commonDao.getRestrictionInfo(visionUsersVb);
		ExceptionCode exceptionCode = null;
		strCurrentOperation = Constants.ADD;
		strApproveOperation = Constants.ADD;
		setServiceDefaults();
		
		vObject.setRecordIndicator(Constants.STATUS_ZERO);
		vObject.setExceptionStatus(Constants.STATUS_ZERO);
		vObject.setMaker(intCurrentUserId);
		vObject.setVerifier(0);
		if("Y".equalsIgnoreCase(visionUsersVb.getUpdateRestriction()))
			vObject.setRaSocRestriction(getUserSoc());
		collTemp = doSelectPendingRecord(vObject);
		if (collTemp != null && collTemp.size() > 0) {
			vObject.setDateCreation(collTemp.get(0).getDateCreation());
			 exceptionCode = getResultObject(Constants.DUPLICATE_KEY_INSERTION); 
			 throw buildRuntimeCustomException(exceptionCode);
		}else {
			vObject.setRecordIndicator(Constants.STATUS_INSERT);
			retVal = doInsertionPend(vObject);
		}
		if (retVal != Constants.SUCCESSFUL_OPERATION) {
			exceptionCode = getResultObject(retVal);
			throw buildRuntimeCustomException(exceptionCode);
		} else {
			exceptionCode = getResultObject(Constants.SUCCESSFUL_OPERATION);
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
	public ExceptionCode doUpdateApprRecordForNonTrans(ExceptionConfigHeaderVb vObject) throws RuntimeCustomException {
		List<ExceptionConfigHeaderVb> collTemp = null;
		ExceptionCode exceptionCode = new ExceptionCode();
		ExceptionConfigHeaderVb vObjectlocal = new ExceptionConfigHeaderVb();
		strApproveOperation = Constants.MODIFY;
		strErrorDesc = "";
		strCurrentOperation = Constants.MODIFY;
		setServiceDefaults();

		collTemp = doSelectPendingRecord(vObject);
		if (collTemp.size() == 0) {
			collTemp = selectApprovedRecord(vObject);
			if (collTemp.size() == 0) {
				vObject.setMaker(getIntCurrentUserId());
				vObject.setVerifier(0);
				vObject.setRecordIndicator(Constants.STATUS_INSERT);
				exceptionCode = doInsertApprRecordForNonTrans(vObject);
				return exceptionCode;
			}else {
				
			}
		}
		vObjectlocal = ((ArrayList<ExceptionConfigHeaderVb>) collTemp).get(0);
		exceptionCode = checkRecordAuthorized(vObject.getCountry(), vObject.getLeBook(), vObject.getExceptionReference(),vObjectlocal.getMaker());
		if(exceptionCode.getErrorCode() == Constants.ERRONEOUS_OPERATION) {
			return exceptionCode;
		}
		if(vObjectlocal.getMaker() != intCurrentUserId) {
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg("Only Maker can Modify the Record");
			return exceptionCode;
		}
		vObject.setMaker(getIntCurrentUserId());
		vObject.setVerifier(0);
		if(vObjectlocal.getRecordIndicator() == 6)// verifying whether the record Rejected
			vObject.setRecordIndicator(1);//Update the record indicator to modify Pending
		else
			vObject.setRecordIndicator(Constants.STATUS_INSERT);
		vObject.setDateCreation(vObjectlocal.getDateCreation());
		retVal = doUpdateAppr(vObject);
		if (retVal != Constants.SUCCESSFUL_OPERATION) {
			exceptionCode = getResultObject(retVal);
			throw buildRuntimeCustomException(exceptionCode);
		} else {
			exceptionCode = getResultObject(Constants.SUCCESSFUL_OPERATION);
		}
		if(vObjectlocal.getRecordIndicator() == 6) {
			vObject.setRecordIndicator(Constants.STATUS_UPDATE);
			doUpdateAllAppr(vObject);
		}
		String systemDate = getSystemDate();
		vObject.setDateLastModified(systemDate);
		return exceptionCode;
	}
	
	@Transactional(rollbackForClassName = { "com.vision.exception.RuntimeCustomException" })
	public ExceptionCode doApproveRecord(ExceptionConfigHeaderVb vObject) throws RuntimeCustomException {
		List<ExceptionConfigHeaderVb> collTemp = null;
		ExceptionCode exceptionCode = new ExceptionCode();
		strCurrentOperation = Constants.APPROVE;
		strApproveOperation = Constants.APPROVE;
		setServiceDefaults();
		ExceptionConfigHeaderVb vObjectlocal = new ExceptionConfigHeaderVb();
		collTemp = doSelectPendingRecord(vObject);
		if (collTemp != null && collTemp.size() > 0) {
			if (collTemp.get(0).getMaker() == getIntCurrentUserId()){
				exceptionCode = getResultObject(Constants.MAKER_CANNOT_APPROVE);
				throw buildRuntimeCustomException(exceptionCode);
			}
			vObject.setDateCreation(collTemp.get(0).getDateCreation());
			vObjectlocal=collTemp.get(0);
			vObjectlocal.setRecordIndicator(Constants.STATUS_ZERO);
			vObjectlocal.setVerifier(intCurrentUserId);
			vObjectlocal.setPostedFlag("Y");
			List<ExceptionConfigDetailsVb> exceptionDetaillst = manualExceptionConfigDetailsDao.getQueryResultsPend(vObject);
			if(exceptionDetaillst == null || exceptionDetaillst.size() == 0) {
				exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
				exceptionCode.setErrorMsg("No Details found to Authorize");
				return exceptionCode;
			}
			for(ExceptionConfigDetailsVb exceptionConfigDetailsVb : exceptionDetaillst) {
				exceptionConfigDetailsVb.setVerifier(intCurrentUserId);
				exceptionConfigDetailsVb.setRecordIndicator(Constants.STATUS_ZERO);
				manualExceptionConfigDetailsDao.approveDetailRecord(exceptionConfigDetailsVb);
				
				exceptionConfigDetailsVb.setCategory(vObject.getCategory());
				exceptionConfigDetailsVb.setTransLineType(vObject.getTransLineType());
				retVal = manualExceptionConfigDetailsDao.updateExceptionOnIE(exceptionConfigDetailsVb);
				if (retVal == Constants.ERRONEOUS_OPERATION) {
					exceptionCode = getResultObject(retVal);
					throw buildRuntimeCustomException(exceptionCode);
				}
				retVal = manualExceptionConfigDetailsDao.doInsertPostedAudit(exceptionConfigDetailsVb, vObject);
				retVal = manualExceptionConfigDetailsDao.doDeleteDetailRecordPend(exceptionConfigDetailsVb);
			}
			retVal = doInsertionAppr(vObjectlocal);
		}
		if (retVal != Constants.SUCCESSFUL_OPERATION) {
			exceptionCode = getResultObject(retVal);
			throw buildRuntimeCustomException(exceptionCode);
		} else {
			exceptionCode = getResultObject(Constants.SUCCESSFUL_OPERATION);
		}
		int retVal = saveComments(vObject);
		if (retVal != Constants.SUCCESSFUL_OPERATION) {
			exceptionCode = getResultObject(retVal);
			throw buildRuntimeCustomException(exceptionCode);
		}
		exceptionCode = getResultObject(Constants.SUCCESSFUL_OPERATION);
		retVal = deletePendingRecord(vObjectlocal);
		
		
		writeAuditLog(vObject, null);
		if (exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION) {
			exceptionCode = getResultObject(Constants.AUDIT_TRAIL_ERROR);
			throw buildRuntimeCustomException(exceptionCode);
		}
		return exceptionCode;
	}
	
	public ExceptionCode doDeleteRecord(ExceptionConfigHeaderVb vObject) throws RuntimeCustomException {
		List<ExceptionConfigHeaderVb> collTemp = null;
		ExceptionCode exceptionCode = null;
		strCurrentOperation = Constants.DELETE;
		strApproveOperation = Constants.DELETE;
		setServiceDefaults();
		
		collTemp = doSelectPendingRecord(vObject);
		if (collTemp != null && collTemp.size() > 0) {
			vObject.setDateCreation(collTemp.get(0).getDateCreation());
			retVal = deletePendingRecord(vObject);
			auditForDeletedHeaderRecords(collTemp);
		}
		if (retVal != Constants.SUCCESSFUL_OPERATION) {
			exceptionCode = getResultObject(retVal);
			throw buildRuntimeCustomException(exceptionCode);
		} else {
			exceptionCode = getResultObject(Constants.SUCCESSFUL_OPERATION);
		}
		ExceptionConfigDetailsVb detailsVb = new ExceptionConfigDetailsVb();
		detailsVb.setCountry(vObject.getCountry());
		detailsVb.setLeBook(vObject.getLeBook());
		detailsVb.setExceptionReference(vObject.getExceptionReference());
		List<ExceptionConfigDetailsVb> exceptionDetList = manualExceptionConfigDetailsDao.doSelectPendingRecord(detailsVb);
		retVal = manualExceptionConfigDetailsDao.doDeletePendingRecord(detailsVb);
		//delete and Insert Audit for Reject
		manualExceptionConfigDetailsDao.doDeleteAllRejectAuditDet(detailsVb);
		if(exceptionDetList != null && exceptionDetList.size() > 0) {
			for(ExceptionConfigDetailsVb exceptionDetVb : exceptionDetList) {
				manualExceptionConfigDetailsDao.doInsertDetailRejectAudit(exceptionDetVb);
			}
		}
		/*
		 * if (retVal == Constants.SUCCESSFUL_OPERATION) { exceptionCode =
		 * getResultObject(retVal); throw buildRuntimeCustomException(exceptionCode); }
		 * else { exceptionCode = getResultObject(Constants.SUCCESSFUL_OPERATION); }
		 */
		exceptionCode = getResultObject(Constants.SUCCESSFUL_OPERATION);
		writeAuditLog(vObject, null);
		if (exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION) {
			exceptionCode = getResultObject(Constants.AUDIT_TRAIL_ERROR);
			throw buildRuntimeCustomException(exceptionCode);
		}
		return exceptionCode;
	}
	protected int doInsertHeaderRejectAudit(ExceptionConfigHeaderVb vObject) {
		String query = " Insert Into RA_Exception_Header_RejAud(COUNTRY,LE_BOOK,Exception_Reference,Reference_Description,Exception_category, "
				+ " Exception_Flag_AT,Exception_Flag,Posted_Flag,Start_Date, END_DATE, Transaction_Type,"
				+ "Latest_Posted_Date, Auto_Exception_Date,"
				+ "Auto_Exception_type, 	Auto_Exception_remarks, fee_basis,"
				+" Auto_Exception_Fee_Amount,Auto_Exception_Fee_Percent,"
				+ "	EXCEPTION_Status_NT, EXCEPTION_Status,"
				+ "RECORD_INDICATOR_NT,RECORD_INDICATOR,MAKER,VERIFIER,DATE_LAST_MODIFIED,DATE_CREATION,Date_Of_Rejection,REJECTED_BY) "
				+ " Values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?," + getDbFunction("SYSDATE") + ",?)";

		Object[] args = { vObject.getCountry(), vObject.getLeBook(), 
				vObject.getExceptionReference(),vObject.getExceptionReferenceDesc(), vObject.getCategory(),
				vObject.getExceptionFlagAT(),vObject.getExceptionFlag(), vObject.getPostedFlag(),
				vObject.getStartDate(),
				vObject.getEndDate(), vObject.getTransLineType(),
				vObject.getLatestPostedDate(), vObject.getAutoExceptionDate(),
				vObject.getAutoExceptionType(), vObject.getAutoExceptionRemarks(), vObject.getFeeBasis(),
				ValidationUtil.isValid(vObject.getAutoExceptionFeeAmt())?vObject.getAutoExceptionFeeAmt().replaceAll(",", ""):"0", 
				ValidationUtil.isValid(vObject.getAutoExceptionFeePercentage())?vObject.getAutoExceptionFeePercentage().replaceAll(",", ""):"0",
				vObject.getExceptionStatusNt(),
				vObject.getExceptionStatus(), vObject.getRecordIndicatorNt(), 
				vObject.getRecordIndicator(),
				vObject.getMaker(), vObject.getVerifier(),vObject.getDateCreation(),vObject.getDateLastModified(),intCurrentUserId};
		return getJdbcTemplate().update(query, args);
	}
	public int deleteRejAuditHeaderRecord(ExceptionConfigHeaderVb vObject) {
		String query = "Delete from RA_EXCEPTION_HEADER_REJAUD  WHERE COUNTRY= ? AND LE_BOOK= ? AND EXCEPTION_REFERENCE = ?";
		Object[] args = { vObject.getCountry(), vObject.getLeBook(), vObject.getExceptionReference() };
		getJdbcTemplate().update(query, args);
		return Constants.SUCCESSFUL_OPERATION;
	}
	
	@Override
	protected void setServiceDefaults() {
		serviceName = "VarianceFlagging";
		serviceDesc = "Variance Flagging";
		tableName = "RA_Exception_Header";
		childTableName = "RA_Exception_Header";
		intCurrentUserId = CustomContextHolder.getContext().getVisionId();
	}

	@Override
	protected String getAuditString(ExceptionConfigHeaderVb vObject) {
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

			if (ValidationUtil.isValid(vObject.getExceptionReference()))
				strAudit.append("EXCEPTION_REFERENCE" + auditDelimiterColVal + vObject.getExceptionReference().trim());
			else
				strAudit.append("EXCEPTION_REFERENCE" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);
			
			if (ValidationUtil.isValid(vObject.getExceptionReferenceDesc()))
				strAudit.append("Reference_Description" + auditDelimiterColVal + vObject.getExceptionReferenceDesc().trim());
			else
				strAudit.append("Reference_Description" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);

			if (ValidationUtil.isValid(vObject.getExceptionFlagAT()))
				strAudit.append("EXCEPTION_FLAG_AT" + auditDelimiterColVal + vObject.getExceptionFlagAT());
			else
				strAudit.append("EXCEPTION_FLAG_AT" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);
			
			if (ValidationUtil.isValid(vObject.getExceptionFlag()))
				strAudit.append("EXCEPTION_FLAG" + auditDelimiterColVal + vObject.getExceptionFlag().trim());
			else
				strAudit.append("EXCEPTION_FLAG" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);
			
			
			strAudit.append("EXCEPTION_STATUS_NT" + auditDelimiterColVal + vObject.getExceptionStatusNt());
			strAudit.append(auditDelimiter);

			strAudit.append("EXCEPTION_STATUS" + auditDelimiterColVal + vObject.getExceptionStatus());
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
	public void auditForDeletedHeaderRecords(List<ExceptionConfigHeaderVb> exceptionConfigHeaderLst) {
		try {
			for(ExceptionConfigHeaderVb exceptionHeaderVb : exceptionConfigHeaderLst) {
				if (getCntforRejectHeaderAudit(exceptionHeaderVb) > 0)
					deleteRejAuditHeaderRecord(exceptionHeaderVb);
				doInsertHeaderRejectAudit(exceptionHeaderVb);
			}
		} catch(Exception e) {
			
		}
	}
	
	public int getCntforRejectHeaderAudit(ExceptionConfigHeaderVb dObj) {
		try {
			String sql = "SELECT COUNT(*) CNT FROM RA_EXCEPTION_HEADER_REJAUD"
					+ " WHERE COUNTRY = ? AND LE_BOOK = ?  AND EXCEPTION_REFERENCE = ?  ";
			Object objParams[] = new Object[3];
			objParams[0] = new String(dObj.getCountry());
			objParams[1] = new String(dObj.getLeBook());
			objParams[2] = new String(dObj.getExceptionReference());
			int count = getJdbcTemplate().queryForObject(sql, objParams,Integer.class);
			return count ;
		} catch(Exception e) {
			return 0;
		}
	}
	public String getMaxReference() {
		StringBuffer strBufApprove=new StringBuffer();
		if("ORACLE".equalsIgnoreCase(databaseType)) {
			strBufApprove=new StringBuffer("SELECT RA_EXC_REFSEQUENCE.NEXTVAL FROM dual");
		}else if("MSSQL".equalsIgnoreCase(databaseType)) {
			strBufApprove=new StringBuffer("SELECT NEXT VALUE FOR RA_EXC_REFSEQUENCE");
		}
		String max = getJdbcTemplate().queryForObject(strBufApprove.toString(), String.class);
		return max;
	}
	
	public int getApprCount(ExceptionConfigDetailsVb dObj,int recordIndicator) {
		try {
			String recId = "";
			String tableName = "RA_EXCEPTION_HEADER_Pend";
			if(recordIndicator == Constants.STATUS_ZERO) {
				tableName = "RA_EXCEPTION_HEADER";
			}
			if(recordIndicator != 4)
				recId = " and RECORD_INDICATOR = "+recordIndicator+" ";	 
			
			String sql = "SELECT COUNT(1) CNT FROM "+tableName+" "
					+ " WHERE COUNTRY = ? AND LE_BOOK = ?  AND EXCEPTION_REFERENCE = ? "+recId+" ";
			
			Object objParams[] = new Object[3];
			objParams[0] = new String(dObj.getCountry());
			objParams[1] = new String(dObj.getLeBook());
			objParams[2] = new String(dObj.getExceptionReference());
			int count = getJdbcTemplate().queryForObject(sql, objParams,Integer.class);
			return count ;
		} catch(Exception e) {
			return 0;
		}
	}
	public String getUserSoc() {
		try {
			setServiceDefaults();
			String query= "Select RA_SOC from Vision_Users where Update_Restriction = 'Y' and Vision_Id = "+intCurrentUserId+" ";
			return getJdbcTemplate().queryForObject(query, String.class);	
		}catch(Exception e) {
			return null;
		}
	}
	public ExceptionCode checkRecordAuthorized (String country,String leBook,String exceptionReference,long maker) {
		ExceptionConfigDetailsVb dObj = new ExceptionConfigDetailsVb();
		ExceptionCode exceptionCode= new ExceptionCode();
		setServiceDefaults();
		try {
			dObj.setCountry(country);
			dObj.setLeBook(leBook);
			dObj.setExceptionReference(exceptionReference);
			exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
			
			if(getApprCount(dObj,Constants.STATUS_ZERO) > 0) {
				exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
				exceptionCode.setErrorMsg("This reference has already been Authorised");
				return exceptionCode;
			}
			if (getApprCount(dObj, 6) > 0 && maker != intCurrentUserId) {
				exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
				exceptionCode.setErrorMsg("This reference has already been Rejected");
				return exceptionCode;
			}
			if (getApprCount(dObj, 8) > 0 && maker == intCurrentUserId) { // Check for record already submitted
				exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
				exceptionCode.setErrorMsg("This reference has already been Submitted");
				return exceptionCode;
			}
			if(getApprCount(dObj,Constants.STATUS_PENDING) == 0) {
				exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
				exceptionCode.setErrorMsg("This reference has already been Deleted");
				return exceptionCode;
			}
		}catch(Exception e) {
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg("Error while checking Authorized count for this reference");	
		}
		return exceptionCode;
	}
	public ExceptionCode rejectException (ExceptionConfigHeaderVb exceptionConfigHeaderVb) {
		ExceptionCode exceptionCode= new ExceptionCode();
		List<ExceptionConfigHeaderVb>collTemp = null;
		strApproveOperation = Constants.REJECT;
		strErrorDesc = "";
		strCurrentOperation = Constants.REJECT;
		try {
			collTemp = doSelectPendingRecord(exceptionConfigHeaderVb);
			if (collTemp.size() > 0 && collTemp != null) {
				if (collTemp.get(0).getMaker() == intCurrentUserId) {
					exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
					exceptionCode.setErrorMsg("Maker cannot reject pending records");
					throw buildRuntimeCustomException(exceptionCode);
				}
				if (ValidationUtil.isValid(exceptionConfigHeaderVb)) {
					exceptionConfigHeaderVb.setVerifier(intCurrentUserId);
					retVal = doUpdateAllAppr(exceptionConfigHeaderVb);
					if (retVal != Constants.SUCCESSFUL_OPERATION) {
						exceptionCode = getResultObject(retVal);
						throw buildRuntimeCustomException(exceptionCode);
					}
					retVal = saveComments(exceptionConfigHeaderVb);
					if (retVal != Constants.SUCCESSFUL_OPERATION) {
						exceptionCode = getResultObject(retVal);
						throw buildRuntimeCustomException(exceptionCode);
					}
					exceptionCode = getResultObject(Constants.SUCCESSFUL_OPERATION);
				}

			}
		}catch(Exception e) {
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg(e.getMessage());	
		}
		return exceptionCode;
	}

	public int saveComments(ExceptionConfigHeaderVb vObject) {
		setServiceDefaults();
		String sql = "INSERT INTO RA_EXCEPTION_COMMENTS (COUNTRY,LE_BOOK,Exception_Reference, "
				+ "	COMMENTS,Status_NT, Status,"
				+ "RECORD_INDICATOR_NT,RECORD_INDICATOR,MAKER,VERIFIER,DATE_LAST_MODIFIED,DATE_CREATION) "
				+ " Values (?,?,?,?,?,?,?,?,?,?," + getDbFunction("SYSDATE") + "," + getDbFunction("SYSDATE") + ")";
		Object[] args = { vObject.getCountry(), vObject.getLeBook(), vObject.getExceptionReference(),
				vObject.getComments(), vObject.getExceptionStatusNt(), vObject.getExceptionStatus(), vObject.getRecordIndicatorNt(),
				vObject.getRecordIndicator(), intCurrentUserId, intCurrentUserId};
		return getJdbcTemplate().update(sql, args);
	}
	public ArrayList<ExceptionConfigCommentsVb> getCommmentsLst(ExceptionConfigHeaderVb vObject){
		ArrayList<ExceptionConfigCommentsVb> collTemp = new ArrayList<>();
		try {
			String sql = "SELECT T1.COUNTRY,T1.LE_BOOK,T1.EXCEPTION_REFERENCE,"
					+ " T1.COMMENTS,Status_NT, T1.Status, T1.RECORD_INDICATOR_NT,T2.NUM_SUBTAB_DESCRIPTION RECORD_INDICATOR_DESC,"
					+ " T1.RECORD_INDICATOR,T1.MAKER,T1.VERIFIER,"
					+ " (SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "
					+ getDbFunction("NVL") + "(T1.MAKER,0) ) MAKER_NAME, "
					+ "	"+dbFunctionFormats("T1.DATE_LAST_MODIFIED","DATETIME_FORMAT", null)+" DATE_LAST_MODIFIED, "
							+ " T1.DATE_LAST_MODIFIED DATE_LAST_MODIFIED_1,"
					+ "	"+dbFunctionFormats("T1.DATE_CREATION","DATETIME_FORMAT", null)+" DATE_CREATION "
					+ " FROM RA_EXCEPTION_COMMENTS T1, NUM_SUB_TAB T2"
					+ " WHERE T1.COUNTRY = ? "
					+ " AND T1.LE_BOOK = ? "
					+ " AND T1.EXCEPTION_REFERENCE = ?"
					+ " and t2.NUM_tab = T1.RECORD_INDICATOR_NT  "
					+ " and t2.NUM_sub_tab = T1.RECORD_INDICATOR"
					+ " ORDER BY DATE_LAST_MODIFIED_1 DESC ";
			Object[] args = { vObject.getCountry(), vObject.getLeBook(), vObject.getExceptionReference()};
			collTemp = (ArrayList<ExceptionConfigCommentsVb>) getJdbcTemplate().query(sql, args,getCommentsMapper());
		} catch(Exception e) {
			e.printStackTrace();
		}
		return collTemp;
	}
	protected RowMapper getCommentsMapper() {
		RowMapper mapper = new RowMapper() {
			@Override
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				ExceptionConfigCommentsVb vObject = new ExceptionConfigCommentsVb();
				vObject.setCountry(rs.getString("COUNTRY"));
				vObject.setLeBook(rs.getString("LE_BOOK"));
				vObject.setExceptionReference(rs.getString("EXCEPTION_REFERENCE"));
				vObject.setComments(rs.getString("COMMENTS"));
				vObject.setStatusNt(rs.getInt("Status_NT"));
				vObject.setStatus(rs.getInt("Status"));
				vObject.setRecordIndicator(rs.getInt("RECORD_INDICATOR"));
				vObject.setRecordIndicatorDesc(rs.getString("RECORD_INDICATOR_DESC"));
				vObject.setMaker(rs.getInt("MAKER"));
				vObject.setMakerName(rs.getString("MAKER_NAME"));
				vObject.setVerifier(rs.getInt("VERIFIER"));
				vObject.setDateLastModified(rs.getString("DATE_LAST_MODIFIED"));
				vObject.setDateCreation(rs.getString("DATE_CREATION"));
				return vObject;
			}
		};
		return mapper;
	}
	public List getExceptionTypeDetails() {
		List<ExceptionTypeVb> excetionTypeLst = new ArrayList<>();
		try {
			String sql = "SELECT T1.EXCEPTION_TYPE,T2.ALPHA_SUBTAB_DESCRIPTION , "
					+ " T1.LEAKAGE_TYPE,T1.AMOUNT_FILL,T1.AMOUNT_FIELD,T1.COUNT_FILL, "
					+ " T1.REFLECT_FIELD,T1.EXCEPTION_REMARK_FILL,T2.INTERNAL_STATUS "
					+ " FROM RA_Exception_Type_Config T1, ALPHA_SUB_TAB T2 " 
					+ " WHERE  T1.EXCEPTION_TYPE_AT = T2.ALPHA_TAB "
					+ " AND T1.EXCEPTION_TYPE = T2.ALPHA_SUB_TAB";
			excetionTypeLst = getJdbcTemplate().query(sql, getExceptionTypeMapper());
		} catch (Exception e) {
			new ArrayList<>();
		}
		return excetionTypeLst;
	}
	protected RowMapper getExceptionTypeMapper() {
		RowMapper mapper = new RowMapper() {
			@Override
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				ExceptionTypeVb vObject = new ExceptionTypeVb();
				vObject.setExceptionType(rs.getString("EXCEPTION_TYPE"));
				vObject.setExceptionTypeDesc(rs.getString("ALPHA_SUBTAB_DESCRIPTION"));
				vObject.setLeakageType(rs.getString("LEAKAGE_TYPE"));
				vObject.setAmountFill(rs.getString("AMOUNT_FILL"));
				vObject.setAmountField(rs.getString("AMOUNT_FIELD"));
				vObject.setCountFill(rs.getString("COUNT_FILL"));
				vObject.setReflectField(rs.getString("REFLECT_FIELD"));
				vObject.setExceptionMarkRefill(rs.getString("EXCEPTION_REMARK_FILL"));
				vObject.setInternalStatus(rs.getInt("INTERNAL_STATUS"));
				return vObject;
			}
		};
		return mapper;
	}
	public int doSubmitRecord(ExceptionConfigHeaderVb dObj,int status){
		String query = "Update RA_Exception_Header_Pend set record_indicator=? WHERE Country = ? and "
				+ " LE_Book = ? And Exception_Reference = ?";
		Object objParams[] = new Object[4];
		objParams[0] = status;
		objParams[1] = new String(dObj.getCountry());
		objParams[2] = new String(dObj.getLeBook());
		objParams[3] = new String(dObj.getExceptionReference());
		return getJdbcTemplate().update(query,objParams);
	}
}
