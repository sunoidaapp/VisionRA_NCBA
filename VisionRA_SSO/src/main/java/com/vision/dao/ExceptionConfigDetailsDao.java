package com.vision.dao;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import java.util.Vector;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.UncategorizedSQLException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.vision.authentication.CustomContextHolder;
import com.vision.exception.ExceptionCode;
import com.vision.exception.RuntimeCustomException;
import com.vision.util.CommonUtils;
import com.vision.util.Constants;
import com.vision.util.ValidationUtil;
import com.vision.vb.CurrencyDetailsVb;
import com.vision.vb.ExceptionConfigDetailsVb;
import com.vision.vb.ExceptionConfigHeaderVb;
import com.vision.vb.ExceptionManualFiltersVb;
import com.vision.vb.SmartSearchVb;
import com.vision.vb.VisionUsersVb;

@Component
public class ExceptionConfigDetailsDao extends AbstractDao<ExceptionConfigDetailsVb> {
	@Autowired
	CommonDao commonDao;
	
	private String txnTable = "RA_SERVICE_IE_TRANSACTIONS";
	private String txnccy = "TRN_CCY";
	private String fcyAmount = "TRN_AMOUNT";
	private String lcyAmount = "TRN_AMOUNT_LCY";
	
	private void setSourceTableColumn(String transLineType) {
		if("P".equals(transLineType)) {
			txnTable = "RA_PRODUCT_IE_TRANSACTIONS";
			txnccy = "CONTRACT_CCY TRN_CCY";
			fcyAmount = "CONTRACT_BAL TRN_AMOUNT";
			lcyAmount = "CONTRACT_BAL_LCY TRN_AMOUNT_LCY";
		}else {
			txnTable = "RA_SERVICE_IE_TRANSACTIONS";
			txnccy = "TRN_CCY";
			fcyAmount = "TRN_AMOUNT";
			lcyAmount = "TRN_AMOUNT_LCY";

		}
	}
	protected RowMapper getDetailMapper(){
		RowMapper mapper = new RowMapper() {
			@Override
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				ExceptionConfigDetailsVb vObject = new ExceptionConfigDetailsVb();
				vObject.setCountry(rs.getString("COUNTRY"));
				vObject.setLeBook(rs.getString("LE_BOOK"));
				vObject.setExceptionReference(rs.getString("EXCEPTION_REFERENCE"));
				vObject.setBusinessDate(rs.getString("Business_Date"));
				vObject.setTransLineId(rs.getString("Trans_Line_Id"));
				vObject.setTransLineDescription(rs.getString("Trans_Line_Desc"));
				vObject.setBusinessLineDescription(rs.getString("Bus_Line_Desc"));
				vObject.setTransSequence(rs.getInt("Trans_Sequence"));
				vObject.setBusinessLineId(rs.getString("Business_Line_ID"));
				vObject.setExceptionDate(rs.getString("Exception_Date"));
				vObject.setExceptionType(rs.getString("Exception_Type"));
				vObject.setExceptionTypeAT(rs.getInt("Exception_Type_AT"));
				vObject.setExceptionTypeDesc(rs.getString("Exception_Type_DESC"));
				vObject.setExceptionTypeRemarks(rs.getString("Exception_Remarks"));
				vObject.setExceptionAmountFcy(rs.getString("Exception_IE_Amount_FCY"));
				vObject.setExceptionAmountLcy(rs.getString("Exception_IE_Amount_LCY"));
				vObject.setExceptionCount(rs.getString("Exception_IE_Count"));
				vObject.setLekageAmountFcy(rs.getString("Leakage_Amt_FCY"));
				vObject.setLekageAmountLcy(rs.getString("Leakage_Amt_LCY"));
				vObject.setLeakageCount(rs.getString("Leakage_Count"));
				vObject.setMrbusinessDate(rs.getString("MR_Business_Date"));
				vObject.setMrtransLineId(rs.getString("MR_Trans_Line_Id"));
				vObject.setMrTransSequence(rs.getInt("MR_Trans_Sequence"));
				vObject.setMrbusinessLineId(rs.getString("MR_Business_Line_ID"));
				vObject.setMrRemarks(rs.getString("MR_Remarks"));
				vObject.setRecordIndicatorNt(rs.getInt("RECORD_INDICATOR_NT"));
				vObject.setRecordIndicator(rs.getInt("RECORD_INDICATOR"));
				vObject.setRecordIndicatorDesc(rs.getString("RECORD_INDICATOR_Desc"));
				vObject.setMaker(rs.getInt("MAKER"));
				vObject.setMakerName(rs.getString("MAKER_NAME"));
				vObject.setVerifier(rs.getInt("VERIFIER"));
				vObject.setVerifierName(rs.getString("VERIFIER_NAME"));
				vObject.setDateLastModified(rs.getString("DATE_LAST_MODIFIED"));
				vObject.setDateCreation(rs.getString("DATE_CREATION"));
				vObject.setNavigate(rs.getString("AUDIT_COUNT"));
				vObject.setExceptionIEAmountFcy(rs.getString("Prior_Exception_Amt_FCY"));
				vObject.setExceptionIEAmountLcy(rs.getString("Prior_Exception_Amt_LCY"));
				vObject.setExceptionIECount(rs.getString("Prior_Exception_Count"));
				return vObject;
			}
		};
		return mapper;
	}
	protected RowMapper getExDetailMapper(List<CurrencyDetailsVb> currencylst){
		RowMapper mapper = new RowMapper() {
			@Override
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				ExceptionConfigDetailsVb vObject = new ExceptionConfigDetailsVb();
				vObject.setCountry(rs.getString("COUNTRY"));
				vObject.setLeBook(rs.getString("LE_BOOK"));
				vObject.setExceptionReference(rs.getString("EXCEPTION_REFERENCE"));
				vObject.setBusinessDate(rs.getString("Business_Date"));
				vObject.setTransLineId(rs.getString("Trans_Line_Id"));
				vObject.setTransLineDescription(rs.getString("Trans_Line_Desc"));
				vObject.setBusinessLineDescription(rs.getString("Bus_Line_Desc"));
				vObject.setTransSequence(rs.getInt("Trans_Sequence"));
				vObject.setBusinessLineId(rs.getString("Business_Line_ID"));
				vObject.setExceptionDate(rs.getString("Exception_Date"));
				vObject.setExceptionType(rs.getString("Exception_Type"));
				vObject.setExceptionTypeAT(rs.getInt("Exception_Type_AT"));
				vObject.setExceptionTypeDesc(rs.getString("Exception_Type_DESC"));
				vObject.setExceptionTypeRemarks(rs.getString("Exception_Remarks"));
				vObject.setExceptionAmountFcy(rs.getString("Exception_Amount_FCY"));
				vObject.setExceptionAmountLcy(rs.getString("Exception_Amount_LCY"));
				vObject.setExceptionCount(rs.getString("Exception_Count"));
				vObject.setLekageAmountFcy(rs.getString("Leakage_Amt_FCY"));
				vObject.setLekageAmountLcy(rs.getString("Leakage_Amt_LCY"));
				vObject.setLeakageCount(rs.getString("Leakage_Count"));
				vObject.setMrbusinessDate(rs.getString("MR_Business_Date"));
				vObject.setMrtransLineId(rs.getString("MR_Trans_Line_Id"));
				vObject.setMrTransSequence(rs.getInt("MR_Trans_Sequence"));
				vObject.setMrbusinessLineId(rs.getString("MR_Business_Line_ID"));
				vObject.setMrRemarks(rs.getString("MR_Remarks"));
				vObject.setRecordIndicatorNt(rs.getInt("RECORD_INDICATOR_NT"));
				vObject.setRecordIndicator(rs.getInt("RECORD_INDICATOR"));
				vObject.setRecordIndicatorDesc(rs.getString("RECORD_INDICATOR_Desc"));
				vObject.setMaker(rs.getInt("MAKER"));
				vObject.setMakerName(rs.getString("MAKER_NAME"));
				vObject.setVerifier(rs.getInt("VERIFIER"));
				vObject.setVerifierName(rs.getString("VERIFIER_NAME"));
				vObject.setDateLastModified(rs.getString("DATE_LAST_MODIFIED"));
				vObject.setDateCreation(rs.getString("DATE_CREATION"));
				vObject.setNavigate(rs.getString("AUDIT_COUNT"));
				vObject.setActualAmountFcy(rs.getString("ACTUAL_IE_AMT_FCY"));
				vObject.setActualAmountLcy(rs.getString("ACTUAL_IE_AMT_LCY"));
				vObject.setActualCount(rs.getString("ACTUAL_IE_COUNT"));
				vObject.setExpectedAmountFcy(rs.getString("EXPECTED_IE_AMT_FCY"));
				vObject.setExpectedAmountLcy(rs.getString("EXPECTED_IE_AMT_LCY"));
				vObject.setExpectedCount(rs.getString("EXPECTED_IE_COUNT"));
				/*vObject.setExceptionIEAmountFcy(rs.getString("EXCEPTION_IE_AMOUNT_FCY"));
				vObject.setExceptionIEAmountLcy(rs.getString("EXCEPTION_IE_AMOUNT_LCY"));*/
				vObject.setExceptionIEAmountFcy(rs.getString("Prior_Exception_Amt_FCY"));
				vObject.setExceptionIEAmountLcy(rs.getString("Prior_Exception_Amt_LCY"));
				vObject.setExceptionIECount(rs.getString("Prior_Exception_Count"));
				vObject.setOrginalLeakageFcy(rs.getString("ORIGINAL_LEAKAGE_FCY"));
				vObject.setOrginalLeakageLcy(rs.getString("ORIGINAL_LEAKAGE_LCY"));
				vObject.setOriginalLeakageCount(rs.getString("ORIGINAL_LEAKAGE_COUNT"));
				vObject.setPostingCcy(rs.getString("POSTING_CCY"));
				vObject.setFxRate(dynamicDecimalFormat(rs.getDouble("FX_RATE"),5));
				vObject.setTranCcy(rs.getString("TRN_CCY"));
				vObject.setExceptionFlag(rs.getString("EXCEPTION_FLAG"));
				ExceptionCode exceptionCode = setDecimalPrecision(currencylst,vObject);
				if(exceptionCode.getErrorCode() == Constants.SUCCESSFUL_OPERATION)
					vObject= (ExceptionConfigDetailsVb)exceptionCode.getResponse();
				return vObject;
			}
		};
		return mapper;
	}

	protected RowMapper getLeakageDataMapper(String category,List<CurrencyDetailsVb> currencylst){
		RowMapper mapper = new RowMapper() {
			@Override
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				ExceptionConfigDetailsVb vObject = new ExceptionConfigDetailsVb();
				vObject.setCountry(rs.getString("COUNTRY"));
				vObject.setLeBook(rs.getString("LE_BOOK"));
				vObject.setBusinessDate(rs.getString("Business_Date"));
				vObject.setTransLineId(rs.getString("Trans_Line_Id"));
				vObject.setTransLineDescription(rs.getString("Trans_Line_Desc"));
				vObject.setTransSequence(rs.getInt("Trans_Sequence"));
				vObject.setBusinessLineId(rs.getString("Business_Line_ID"));
				vObject.setBusinessLineDescription(rs.getString("Bus_Line_Desc"));
				vObject.setLekageAmountFcy(rs.getString("Leakage_FCY"));
				vObject.setLekageAmountLcy(rs.getString("Leakage_LCY"));
				vObject.setLeakageCount(rs.getString("LEAKAGE_COUNT"));
				vObject.setExceptionType(rs.getString("EXCEPTION_TYPE"));
				if("DEFLAG".equalsIgnoreCase(category)) {
					vObject.setExceptionAmountFcy(rs.getString("EXCEPTION_AMT_FCY"));
					vObject.setExceptionAmountLcy(rs.getString("EXCEPTION_AMT_LCY"));	
					vObject.setExceptionCount(rs.getString("EXCEPTION_COUNT"));
				}else {
					if(ValidationUtil.isValid(rs.getString("EXCEPTION_INT_STATUS")) && "1".equals(rs.getString("EXCEPTION_INT_STATUS"))) {
						vObject.setExceptionAmountFcy(rs.getString("Leakage_FCY"));
						vObject.setExceptionAmountLcy(rs.getString("Leakage_LCY"));
						vObject.setLeakageCount(rs.getString("LEAKAGE_COUNT"));
					}
				}
				vObject.setExceptionFlag(rs.getString("EXCEPTION_FLAG"));
				vObject.setNavigate(rs.getString("AUDIT_COUNT"));
				vObject.setActualAmountFcy(rs.getString("ACTUAL_IE_AMT_FCY"));
				vObject.setActualAmountLcy(rs.getString("ACTUAL_IE_AMT_LCY"));
				vObject.setActualCount(rs.getString("ACTUAL_IE_COUNT"));
				vObject.setExpectedAmountFcy(rs.getString("EXPECTED_IE_AMT_FCY"));
				vObject.setExpectedAmountLcy(rs.getString("EXPECTED_IE_AMT_LCY"));
				vObject.setExpectedCount(rs.getString("EXPECTED_COUNT"));
				vObject.setExceptionIEAmountFcy(rs.getString("EXCEPTION_IE_AMOUNT_FCY"));
				vObject.setExceptionIEAmountLcy(rs.getString("EXCEPTION_IE_AMOUNT_LCY"));
				vObject.setExceptionIECount(rs.getString("EXCEPTION_IE_COUNT"));
				vObject.setOrginalLeakageFcy(rs.getString("ORIGINAL_LEAKAGE_FCY"));
				vObject.setOrginalLeakageLcy(rs.getString("ORIGINAL_LEAKAGE_LCY"));
				vObject.setOriginalLeakageCount(rs.getString("ORIGINAL_LEAKAGE_COUNT"));
				vObject.setPostingCcy(rs.getString("POSTING_CCY"));
				vObject.setFxRate(dynamicDecimalFormat(rs.getDouble("FX_RATE"),5));
				vObject.setTranCcy(rs.getString("TRN_CCY"));
				ExceptionCode exceptionCode = setDecimalPrecision(currencylst,vObject);
				if(exceptionCode.getErrorCode() == Constants.SUCCESSFUL_OPERATION)
					vObject= (ExceptionConfigDetailsVb)exceptionCode.getResponse();
				return vObject;
			}
		};
		return mapper;
	}

	@Override
	public List<ExceptionConfigDetailsVb> getQueryPopupResults(ExceptionConfigDetailsVb dObj) {
		List<CurrencyDetailsVb> currencyPrecisionlst = commonDao.getCurrencyDecimals(false);
		Vector<Object> params = new Vector<Object>();
		StringBuffer strBufApprove = null;
		String orderBy = "";
		String tableName = "RA_Exception_Details";
		if(dObj.getRecordIndicator() != 0) {
			tableName = "RA_Exception_Details_Pend";
		}
		setSourceTableColumn(dObj.getTransLineType());
		
		String query = String.format("Select * from (SELECT TAppr.COUNTRY, TAppr.LE_BOOK,TAppr.Exception_Reference, "
				+ " TAPPR.Trans_Line_Id, "
				+ " TAPPR.Trans_Sequence,TAPPR.Business_Line_ID, "
				+ " (Select TRANS_LINE_DESCRIPTION from RA_MST_TRANS_LINE_HEADER where country = TAPPR.Country "
				+ " and LE_Book = TAPPR.LE_BOOK and TRANS_LINE_ID = TAPPR.TRANS_LINE_ID) Trans_Line_Desc, "
				+ " (Select Business_Line_Description from RA_MST_Business_Line_Header where Country = TAPPR.Country "
				+ " and LE_Book = TAPPR.LE_Book and Business_Line_ID = TAPPR.Business_Line_ID) Bus_Line_Desc, "
				+ " TAPPR.EXCEPTION_TYPE,TAPPR.EXCEPTION_TYPE_AT,(select T1.ALPHA_SUBTAB_DESCRIPTION from ALPHA_SUB_TAB T1 "
				+ "      where t1.Alpha_tab = TAppr.EXCEPTION_TYPE_AT and T1.ALPHA_SUB_TAB=TAppr.EXCEPTION_TYPE) EXCEPTION_TYPE_DESC, "
				+ "	"+dbFunctionFormats("TAPPR.EXCEPTION_DATE","DATE_FORMAT", null)+" EXCEPTION_DATE, "
				+ "	"+dbFunctionFormats("TAPPR.Business_Date","DATE_FORMAT", null)+" Business_Date, "
				+" RTRIM(LTRIM "
				+ "(TAPPR.Leakage_Amt_FCY)) Leakage_Amt_FCY,"
				+" RTRIM(LTRIM "
				+ "(TAPPR.Leakage_Amt_LCY)) Leakage_Amt_LCY,"
				+" RTRIM(LTRIM "
				+ "(TAPPR.Leakage_Count)) Leakage_Count,"
				+ " TAPPR.Exception_Remarks,  "
				+ "TAPPR.Exception_IE_Amount_FCY Exception_Amount_FCY, "
				+ "TAPPR.Exception_IE_Amount_LCY Exception_Amount_LCY,TAppr.Exception_IE_Count Exception_Count, "
				+ "	"+dbFunctionFormats("TAPPR.MR_Business_Date","DATE_FORMAT", null)+" MR_Business_Date, "
				+ " TAPPR.MR_Trans_Line_Id, TAPPR.MR_Trans_Sequence,"
				+ " TAPPR.MR_Remarks,TAPPR.MR_Business_Line_ID,TAppr.RECORD_INDICATOR_NT, "
				+ " TAppr.RECORD_INDICATOR,T4.NUM_SUBTAB_DESCRIPTION RECORD_INDICATOR_DESC,"
				+ " TAppr. MAKER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "
				+ getDbFunction("NVL") + "(TAppr.MAKER,0) ) MAKER_NAME, "
				+ " TAppr.VERIFIER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "
				+ getDbFunction("NVL") + "(TAppr.VERIFIER,0) ) VERIFIER_NAME,  " + " "
				+dbFunctionFormats("TAPPR.DATE_LAST_MODIFIED","DATETIME_FORMAT", null)+" DATE_LAST_MODIFIED, "
				+ "TAppr.DATE_LAST_MODIFIED DATE_LAST_MODIFIED_1,"
				+dbFunctionFormats("TAPPR.DATE_CREATION","DATETIME_FORMAT", null)+" DATE_CREATION, "
				+ " (select count(1) from RA_Exception_Posted_Audit where "
				+ " TAppr.country = country and " 
				+ "  TAppr.LE_Book = LE_Book  "
				+ "  and TAppr.Trans_Line_Id = Trans_Line_Id "
				+ "  and TAppr.Trans_Sequence = Trans_Sequence   "
				+ "  and TAppr.Business_Line_ID = Business_Line_ID "
				+ "  and TAppr.Business_Date =Business_Date) audit_count,"
				+ getDbFunction("NVL", null)+"(T2.EXPECTED_IE_AMT_FCY,0) EXPECTED_IE_AMT_FCY, "
				+ getDbFunction("NVL", null)+"(T2.EXPECTED_IE_AMT_LCY,0) EXPECTED_IE_AMT_LCY, "
				+ getDbFunction("NVL", null)+"(T2.Chargeable_Count,0) EXPECTED_IE_COUNT, "
				+ getDbFunction("NVL", null)+"(T2.ACTUAL_IE_AMT_FCY,0) ACTUAL_IE_AMT_FCY, "
				+ getDbFunction("NVL", null)+"(T2.ACTUAL_IE_AMT_LCY,0) ACTUAL_IE_AMT_LCY, "
				+ getDbFunction("NVL", null)+"(T2.Actual_Income_Count,0) ACTUAL_IE_COUNT, "
				+ getDbFunction("NVL", null)+"(T2.EXCEPTION_IE_AMOUNT_FCY,0) EXCEPTION_IE_AMOUNT_FCY, "
				+ getDbFunction("NVL", null)+"(T2.EXCEPTION_IE_AMOUNT_LCY,0) EXCEPTION_IE_AMOUNT_LCY, "
				+ getDbFunction("NVL", null)+"(T2.EXCEPTION_IE_COUNT,0) EXCEPTION_IE_COUNT, "
				+ " RTRIM(LTRIM(("+getDbFunction("NVL", null)+"(T2.EXPECTED_IE_AMT_FCY,0) - ("+getDbFunction("NVL", null)+"(T2.ACTUAL_IE_AMT_FCY,0))))) Original_Leakage_FCY,"
				+ " RTRIM(LTRIM(("+getDbFunction("NVL", null)+"(T2.EXPECTED_IE_AMT_LCY,0) - ("+getDbFunction("NVL", null)+"(T2.ACTUAL_IE_AMT_LCY,0))))) Original_Leakage_LCY,"
				+ " RTRIM(LTRIM(("+getDbFunction("NVL", null)+"(T2.Chargeable_Count,0) - ("+getDbFunction("NVL", null)+"(T2.ACtual_Income_Count,0))))) Original_Leakage_Count,"
				+ " T2.POSTING_CCY ,T2.FX_RATE,%s,T2.EXCEPTION_FLAG,"
				+ getDbFunction("NVL", null)+"(TAppr.Prior_Exception_Amt_LCY,0) Prior_Exception_Amt_LCY, "
				+ getDbFunction("NVL", null)+"(TAppr.Prior_Exception_Amt_FCY,0) Prior_Exception_Amt_FCY,"
				+ getDbFunction("NVL", null)+"(TAppr.Prior_Exception_Count,0) Prior_Exception_Count "
				+ " FROM "+tableName+" TAppr ,%s T2,NUM_SUB_TAB T4     " + " Where  "
				+ "  t4.NUM_tab = TAppr.RECORD_INDICATOR_NT  "
				+ " and t4.NUM_sub_tab = TAppr.RECORD_INDICATOR "
				+ "  and TAppr.country = t2.country and " 
				+ "  TAppr.LE_Book = T2.LE_Book  "
				+ "  and TAppr.Trans_Line_Id = T2.Trans_Line_Id "
				+ "  and TAppr.Trans_Sequence = T2.Trans_Sequence   "
				+ "  and TAppr.Business_Line_ID = T2.Business_Line_ID "
				+ "  and TAppr.Business_Date = T2.Business_Date "
				+ " and TAppr.Country = ? and TAppr.LE_Book = ? And TAPPR.Exception_Reference = ?) TAppr", txnccy,txnTable);
		
		strBufApprove = new StringBuffer(query);
		
		params.add(dObj.getCountry());
		params.add(dObj.getLeBook());
		params.add(dObj.getExceptionReference());

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
						case "businessDate":
							if("ORACLE".equalsIgnoreCase(databaseType))
								CommonUtils.addToQuerySearch("TAPPR.Business_Date = TO_DATE('"+data.getValue()+"', 'DD-Mon-RRRR') ", strBufApprove, data.getJoinType());
							else
								CommonUtils.addToQuerySearch(" upper(TAPPR.Business_Date) " + val, strBufApprove, data.getJoinType());
							break;
	
						case "transLineId":
							CommonUtils.addToQuerySearch(" upper(TAPPR.TRANS_LINE_ID) " + val, strBufApprove, data.getJoinType());
							break;
	
						case "transSequence":
							CommonUtils.addToQuerySearch(" upper(TAPPR.TRANS_SEQUENCE) " + val, strBufApprove,data.getJoinType());
							break;
						
						case "transLineDescription":
							CommonUtils.addToQuerySearch(" upper(TAPPR.TRANS_LINE_DESC) " + val, strBufApprove,data.getJoinType());
							break;
							
						case "businessLineId":
							CommonUtils.addToQuerySearch(" upper(TAPPR.BUSINESS_LINE_ID) " + val, strBufApprove,data.getJoinType());
							break;
							
						case "businessLineDescription":
							CommonUtils.addToQuerySearch(" upper(TAPPR.BUS_LINE_DESC) " + val, strBufApprove,data.getJoinType());
							break;	
							
						case "lekageAmountFcy":
							CommonUtils.addToQuerySearch(" upper(TAPPR.LEAKAGE_AMT_FCY) " + val, strBufApprove,data.getJoinType());
							break;
							
						case "lekageAmountLcy":
							CommonUtils.addToQuerySearch(" upper(TAPPR.LEAKAGE_AMT_LCY) " + val, strBufApprove,data.getJoinType());
							break;	
							
						case "leakageCount":
							CommonUtils.addToQuerySearch(" upper(TAPPR.Leakage_Count) " + val, strBufApprove,data.getJoinType());
							break;		
						
						case "exceptionAmountFcy":
							CommonUtils.addToQuerySearch(" upper(TAPPR.EXCEPTION_AMOUNT_FCY) " + val, strBufApprove,data.getJoinType());
							break;
							
						case "exceptionAmountLcy":
							CommonUtils.addToQuerySearch(" upper(TAPPR.EXCEPTION_AMOUNT_LCY) " + val, strBufApprove,data.getJoinType());
							break;
							
						case "exceptionCount":
							CommonUtils.addToQuerySearch(" upper(TAPPR.EXCEPTION_COUNT) " + val, strBufApprove,data.getJoinType());
							break;	
							
						case "exceptionDate":
							if("ORACLE".equalsIgnoreCase(databaseType))
								CommonUtils.addToQuerySearch("TAPPR.EXCEPTION_DATE = TO_DATE('"+data.getValue()+"', 'DD-Mon-RRRR') ", strBufApprove, data.getJoinType());
							else
								CommonUtils.addToQuerySearch(" upper(TAPPR.EXCEPTION_DATE) " + val, strBufApprove,data.getJoinType());
							break;
							
						case "exceptionType":
							CommonUtils.addToQuerySearch(" upper(TAPPR.EXCEPTION_TYPE_DESC) " + val, strBufApprove,data.getJoinType());
							break;
							
						case "exceptionTypeRemarks":
							CommonUtils.addToQuerySearch(" upper(TAPPR.EXCEPTION_REMARKS) " + val, strBufApprove,data.getJoinType());
							break;
							
						case "postingCcy":
							CommonUtils.addToQuerySearch(" upper(TAPPR.POSTING_CCY) " + val, strBufApprove,data.getJoinType());
							break;	
							
						case "tranCcy":
							CommonUtils.addToQuerySearch(" upper(TAPPR.Trn_Ccy) " + val, strBufApprove,data.getJoinType());
							break;	
							
						case "fxRate":
							CommonUtils.addToQuerySearch(" upper(TAPPR.FX_RATE) " + val, strBufApprove,data.getJoinType());
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
				}
				if (ValidationUtil.isValid(visionUsersVb.getLeBook())) {
					CommonUtils.addToQuery(" LE_BOOK IN ('" + visionUsersVb.getLeBook() + "') ", strBufApprove);
				}
			}
			orderBy = " Order by DATE_LAST_MODIFIED_1 DESC,Exception_Reference ";
			return getQueryPopupResults(dObj, null, strBufApprove, null, orderBy, params,getExDetailMapper(currencyPrecisionlst));
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}
	public List<ExceptionConfigDetailsVb> getQueryResultsPend(ExceptionConfigHeaderVb dObj){
		List<ExceptionConfigDetailsVb> collTemp = null;
		String query = "";
		try	{	
			query="SELECT TAppr.COUNTRY, TAppr.LE_BOOK,TAppr.Exception_Reference, "
//					+ " TAPPR.Business_Date,"
					+ " TAPPR.Trans_Line_Id, "
					+ " TAPPR.Trans_Sequence,TAPPR.Business_Line_ID, "
					+ " (Select TRANS_LINE_DESCRIPTION from RA_MST_TRANS_LINE_HEADER where country = TAPPR.Country "
					+ " and LE_Book = TAPPR.LE_BOOK and TRANS_LINE_ID = TAPPR.TRANS_LINE_ID) Trans_Line_Desc, "
					+ " (Select Business_Line_Description from RA_MST_Business_Line_Header where Country = TAPPR.Country "
					+ " and LE_Book = TAPPR.LE_Book and Business_Line_ID = TAPPR.Business_Line_ID) Bus_Line_Desc, "
					+ " TAPPR.EXCEPTION_TYPE,TAPPR.EXCEPTION_TYPE_AT,(select T1.ALPHA_SUBTAB_DESCRIPTION from ALPHA_SUB_TAB T1 "
					+ "      where t1.Alpha_tab = TAppr.EXCEPTION_TYPE_AT and T1.ALPHA_SUB_TAB=TAppr.EXCEPTION_TYPE) EXCEPTION_TYPE_DESC, "
					+ "	"+dbFunctionFormats("TAPPR.EXCEPTION_DATE","DATE_FORMAT", null)+" EXCEPTION_DATE, "
					+ "	"+dbFunctionFormats("TAPPR.Business_Date","DATE_FORMAT", null)+" Business_Date, "
					+ " TAPPR.Leakage_Amt_FCY, TAPPR.Leakage_Amt_LCY,TAPPR.LEAKAGE_COUNT,"
					+ " TAPPR.Exception_Remarks, TAPPR.Exception_IE_Amount_FCY, TAPPR.Exception_IE_Amount_LCY,TAPPR.Exception_IE_Count, "
					+ "	"+dbFunctionFormats("TAPPR.MR_Business_Date","DATE_FORMAT", null)+" MR_Business_Date, "
					+ " TAPPR.MR_Trans_Line_Id, TAPPR.MR_Trans_Sequence, TAPPR.MR_Business_Line_ID, "
					+ " TAPPR.MR_Remarks, TAPPR.MR_Trans_Sequence, TAPPR.MR_Business_Line_ID,TAppr.RECORD_INDICATOR_NT, "
					+ " TAppr.RECORD_INDICATOR,T4.NUM_SUBTAB_DESCRIPTION RECORD_INDICATOR_DESC,"
					+ " TAppr. MAKER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "
					+ getDbFunction("NVL") + "(TAppr.MAKER,0) ) MAKER_NAME, "
					+ " TAppr.VERIFIER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "
					+ getDbFunction("NVL") + "(TAppr.VERIFIER,0) ) VERIFIER_NAME,  " + " "
					+ "	"+dbFunctionFormats("TAPPR.DATE_LAST_MODIFIED","DATETIME_FORMAT", null)+" DATE_LAST_MODIFIED, "
					+ " TAppr.DATE_LAST_MODIFIED DATE_LAST_MODIFIED_1,"
					+ "	"+dbFunctionFormats("TAPPR.DATE_CREATION","DATETIME_FORMAT", null)+" DATE_CREATION, "
					+ " '' AUDIT_COUNT, "
					+ getDbFunction("NVL", null)+"(TAppr.Prior_Exception_Amt_LCY,0) Prior_Exception_Amt_LCY, "
					+ getDbFunction("NVL", null)+"(TAppr.Prior_Exception_Amt_FCY,0) Prior_Exception_Amt_FCY, "
					+ getDbFunction("NVL", null)+"(TAppr.Prior_Exception_Count,0) Prior_Exception_Count "
					+ " FROM RA_Exception_Details_Pend TAppr , NUM_SUB_TAB T4     " + " Where  "
					+ "  t4.NUM_tab = TAppr.RECORD_INDICATOR_NT  "
					+ " and t4.NUM_sub_tab = TAppr.RECORD_INDICATOR "
					+ " and TAppr.Country = ? and TAppr.LE_Book = ? And TAPPR.Exception_Reference = ?";
			
			Object objParams[] = new Object[3];
			objParams[0] = new String(dObj.getCountry());
			objParams[1] = new String(dObj.getLeBook());
			objParams[2] = new String(dObj.getExceptionReference());
			
			collTemp = getJdbcTemplate().query(query,objParams,getDetailMapper());
			return collTemp;
		}catch(Exception ex){
			ex.printStackTrace();
			logger.error("Exception ");
			return null;
		}
	}
	public List<ExceptionConfigDetailsVb> getQueryResultsForReview(ExceptionConfigDetailsVb dObj,int intStatus){
		List<ExceptionConfigDetailsVb> collTemp = null;
		String query = "";
		String tableName = "RA_Exception_Details";
		if(dObj.getRecordIndicator() != 0) {
			tableName = "RA_Exception_Details_Pend";
		}
		String businessDate ="TAPPR.Business_Date = ?";
		if (databaseType.equalsIgnoreCase("ORACLE")) {
			businessDate = "TAPPR.Business_Date = TO_DATE(?,'dd-Mon-RRRR HH24:MI:SS')";
		}
		try	{	
			query="SELECT TAppr.COUNTRY, TAppr.LE_BOOK,TAppr.Exception_Reference, "
					+ " TAPPR.Business_Date,TAPPR.Trans_Line_Id, "
					+ " TAPPR.Trans_Sequence,TAPPR.Business_Line_ID, "
					+ " TAPPR.Business_Line_ID, "
					+ " (Select TRANS_LINE_DESCRIPTION from RA_MST_TRANS_LINE_HEADER where country = TAPPR.Country "
					+ " and LE_Book = TAPPR.LE_BOOK and TRANS_LINE_ID = TAPPR.TRANS_LINE_ID) Trans_Line_Desc, "
					+ " (Select Business_Line_Description from RA_MST_Business_Line_Header where Country = TAPPR.Country "
					+ " and LE_Book = TAPPR.LE_Book and Business_Line_ID = TAPPR.Business_Line_ID) Bus_Line_Desc, "
					+ " TAPPR.EXCEPTION_TYPE,TAPPR.EXCEPTION_TYPE_AT,(select T1.ALPHA_SUBTAB_DESCRIPTION from ALPHA_SUB_TAB T1 "
					+ "      where t1.Alpha_tab = TAppr.EXCEPTION_TYPE_AT and T1.ALPHA_SUB_TAB=TAppr.EXCEPTION_TYPE) EXCEPTION_TYPE_DESC, "
					+ "	"+dbFunctionFormats("TAPPR.EXCEPTION_DATE","DATE_FORMAT", null)+" EXCEPTION_DATE, "
					+ "	"+dbFunctionFormats("TAPPR.Business_Date","DATE_FORMAT", null)+" Business_Date, "
					+ " TAPPR.Leakage_Amt_FCY, TAPPR.Leakage_Amt_LCY,TAppr.Leakage_Count, "
					+ " TAPPR.Exception_Remarks, TAPPR.Exception_IE_Amount_FCY, TAPPR.Exception_IE_Amount_LCY,TAppr.Exception_IE_Count,"
					+ "	"+dbFunctionFormats("TAPPR.MR_Business_Date","DATE_FORMAT", null)+" MR_Business_Date, "
					+ " TAPPR.MR_Trans_Line_Id, TAPPR.MR_Trans_Sequence, TAPPR.MR_Business_Line_ID, "
					+ " TAPPR.MR_Remarks, TAPPR.MR_Trans_Sequence, TAPPR.MR_Business_Line_ID,TAppr.RECORD_INDICATOR_NT, "
					+ " TAppr.RECORD_INDICATOR,T4.NUM_SUBTAB_DESCRIPTION RECORD_INDICATOR_DESC,"
					+ " TAppr. MAKER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "
					+ getDbFunction("NVL") + "(TAppr.MAKER,0) ) MAKER_NAME, "
					+ " TAppr.VERIFIER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "
					+ getDbFunction("NVL") + "(TAppr.VERIFIER,0) ) VERIFIER_NAME,  " + " "
					+ getDbFunction("DATEFUNC") + "(TAppr.DATE_LAST_MODIFIED, '"+getDbFunction("DD_Mon_RRRR")+" "
					+ getDbFunction("TIME") + "') DATE_LAST_MODIFIED ,TAppr.DATE_LAST_MODIFIED DATE_LAST_MODIFIED_1,"
					+ getDbFunction("DATEFUNC") + "(TAppr.DATE_CREATION, '"+getDbFunction("DD_Mon_RRRR")+" "
					+ getDbFunction("TIME") + "') DATE_CREATION,'' AUDIT_COUNT, "
					+ getDbFunction("NVL", null)+"(TAppr.Prior_Exception_Amt_LCY,0) Prior_Exception_Amt_LCY, "
					+ getDbFunction("NVL", null)+"(TAppr.Prior_Exception_Amt_FCY,0) Prior_Exception_Amt_FCY, "
					+ getDbFunction("NVL", null)+"(TAppr.Prior_Exception_Count,0) Prior_Exception_Count "
					+ " FROM "+tableName+" TAppr , NUM_SUB_TAB T4     " + " Where  "
					+ "  t4.NUM_tab = TAppr.RECORD_INDICATOR_NT  "
					+ " and t4.NUM_sub_tab = TAppr.RECORD_INDICATOR "
					+ " and TAppr.Country = ? and TAppr.LE_Book = ? and "+businessDate+" And TAPPR.Trans_Line_Id = ? "
					+ "	And TAPPR.Trans_Sequence = ? And TAPPR.Business_Line_ID = ? "
					+ "	And TAPPR.Exception_Reference = ?";
			
			Object objParams[] = new Object[7];
			objParams[0] = new String(dObj.getCountry());
			objParams[1] = new String(dObj.getLeBook());
			objParams[2] = new String(dObj.getBusinessDate());
			objParams[3] = new String(dObj.getTransLineId());
			objParams[4] = new Integer(dObj.getTransSequence());
			objParams[5] = new String(dObj.getBusinessLineId());
			objParams[6] = new String(dObj.getExceptionReference());
			
			collTemp = getJdbcTemplate().query(query,objParams,getDetailMapper());
			return collTemp;
		}catch(Exception ex){
			ex.printStackTrace();
			logger.error("Exception in getQueryResultsForReview");
			return null;
		}
	}

	public List<ExceptionConfigDetailsVb> getLeakageTransactionsDetails(ExceptionConfigDetailsVb dObj, String filterStr,
			String fromTableJoiner,String specialFilter){
		setSourceTableColumn(dObj.getTransLineType());
		if("P".equals(dObj.getTransLineType())) {
//			fromTableJoiner = fromTableJoiner.replace("RA_SERVICE_IE_TRANSACTIONS", "RA_PRODUCT_IE_TRANSACTIONS");
			fromTableJoiner = Pattern.compile("RA_SERVICE_IE_TRANSACTIONS", Pattern.CASE_INSENSITIVE)
                    .matcher(fromTableJoiner)
                    .replaceAll("RA_PRODUCT_IE_TRANSACTIONS");
			
			fromTableJoiner = Pattern.compile("RA_SERVICE_TRANSACTIONS", Pattern.CASE_INSENSITIVE)
                    .matcher(fromTableJoiner)
                    .replaceAll("RA_PRODUCT_TRANSACTIONS");
		}
		List<CurrencyDetailsVb> currencyPrecisionlst = commonDao.getCurrencyDecimals(false);
		Vector<Object> params = new Vector<Object>();
		try {
			String exceptionAmtFcy = "0";
			String exceptionAmtLcy = "0";
			String exceptionCount = "0";
			String exceptionAmtCols =  "";
			VisionUsersVb visionUsersVb = CustomContextHolder.getContext();
			visionUsersVb = commonDao.getRestrictionInfo(visionUsersVb);
			if("DEFLAG".equalsIgnoreCase(dObj.getCategory())) {
				exceptionAmtCols = ",RTRIM(LTRIM("+getDbFunction("NVL", null)+"(EXCEPTION_IE_AMOUNT_FCY,0))) EXCEPTION_AMT_FCY,"
						+ " RTRIM(LTRIM("+getDbFunction("NVL", null)+"(EXCEPTION_IE_AMOUNT_LCY,0))) EXCEPTION_AMT_LCY,"
						+ "RTRIM(LTRIM("+getDbFunction("NVL", null)+"(EXCEPTION_IE_COUNT,0))) EXCEPTION_COUNT ";	
			}
			//if ("REFLAG".equalsIgnoreCase(dObj.getCategory()) || "FLAG".equalsIgnoreCase(dObj.getCategory())) {
				exceptionAmtFcy = ""+getDbFunction("NVL", null)+"(EXCEPTION_IE_AMOUNT_FCY,0)";
				exceptionAmtLcy = ""+getDbFunction("NVL", null)+"(EXCEPTION_IE_AMOUNT_LCY,0)";
				exceptionCount = ""+getDbFunction("NVL", null)+"(EXCEPTION_IE_COUNT,0)";
			//}
			String query = String.format("SELECT T1.Country,T1.LE_Book,"+dbFunctionFormats("T1.Business_Date", "DATE_FORMAT", null)+" Business_Date,"
					+ " T1.Trans_Line_ID,T1.Trans_Sequence,Business_Line_ID,"
					+ " (Select TRANS_LINE_DESCRIPTION from RA_MST_TRANS_LINE_HEADER where country = t1.Country "
					+ " and LE_Book = t1.LE_BOOK and TRANS_LINE_ID = T1.TRANS_LINE_ID) Trans_Line_Desc, "
					+ " (Select Business_Line_Description from RA_MST_Business_Line_Header where Country = t1.Country "
					+ " and LE_Book = T1.LE_Book and Business_Line_ID = t1.Business_Line_ID) Bus_Line_Desc, "
					+ ""+getDbFunction("NVL", null)+"(EXPECTED_IE_AMT_FCY,0) EXPECTED_IE_AMT_FCY, "
					+ ""+getDbFunction("NVL", null)+"(EXPECTED_IE_AMT_LCY,0) EXPECTED_IE_AMT_LCY, "
					+ ""+getDbFunction("NVL", null)+"(Chargeable_Count,0) Expected_Count, "
					+ ""+getDbFunction("NVL", null)+"(ACTUAL_IE_AMT_FCY,0) ACTUAL_IE_AMT_FCY, "
					+ ""+getDbFunction("NVL", null)+"(ACTUAL_IE_AMT_LCY,0) ACTUAL_IE_AMT_LCY, "
					+ ""+getDbFunction("NVL", null)+"(Actual_Income_Count,0) Actual_IE_Count, "
					+ exceptionAmtFcy+" EXCEPTION_IE_AMOUNT_FCY, "
					+ exceptionAmtLcy+" EXCEPTION_IE_AMOUNT_LCY, "
					+ exceptionCount+" EXCEPTION_IE_COUNT, "
					+ " RTRIM(LTRIM((("+getDbFunction("NVL", null)+"(EXPECTED_IE_AMT_FCY,0) - ("+getDbFunction("NVL", null)+"(ACTUAL_IE_AMT_FCY,0)+"
								+ " "+exceptionAmtFcy+"))))) Leakage_FCY,"
					+ " RTRIM(LTRIM((("+getDbFunction("NVL", null)+"(EXPECTED_IE_AMT_LCY,0) - ("+getDbFunction("NVL", null)+"(ACTUAL_IE_AMT_LCY,0)+"
								+ ""+exceptionAmtLcy+"))))) Leakage_LCY,"
					+ " RTRIM(LTRIM((("+getDbFunction("NVL", null)+"(Chargeable_Count,0) - ("+getDbFunction("NVL", null)+"(Actual_Income_Count,0)+"
								+ " "+exceptionCount+"))))) Leakage_Count,"
					+ " RTRIM(LTRIM(("+getDbFunction("NVL", null)+"(EXPECTED_IE_AMT_FCY,0) - ("+getDbFunction("NVL", null)+"(ACTUAL_IE_AMT_FCY,0))))) Original_Leakage_FCY,"
					+ " RTRIM(LTRIM(("+getDbFunction("NVL", null)+"(EXPECTED_IE_AMT_LCY,0) - ("+getDbFunction("NVL", null)+"(ACTUAL_IE_AMT_LCY,0))))) Original_Leakage_LCY,"
					+ " RTRIM(LTRIM(("+getDbFunction("NVL", null)+"(Chargeable_Count,0) - ("+getDbFunction("NVL", null)+"(Actual_Income_Count,0))))) Original_Leakage_Count,"
					+ " T1.EXCEPTION_TYPE,(SELECT INTERNAL_STATUS FROM ALPHA_SUB_TAB WHERE ALPHA_TAB = 7078 AND ALPHA_SUB_TAB = "+getDbFunction("NVL", null)+"(T1.EXCEPTION_TYPE,'NA') ) EXCEPTION_INT_STATUS," 
					+ " "+getDbFunction("NVL", null)+"(T1.Exception_Flag,'N') Exception_Flag, T1.POSTING_CCY,T1.FX_RATE,%s, "
					+ " (select count(1) from RA_Exception_Posted_Audit where T1.country = country and " 
					+ "   T1.LE_Book = LE_Book "
					+ "   and T1.Trans_Line_Id = Trans_Line_Id and T1.Trans_Sequence = Trans_Sequence "
					+ "   and T1.Business_Line_ID = Business_Line_ID and T1.Business_Date =Business_Date) AUDIT_COUNT "
					+ ""+exceptionAmtCols+""
					+ " FROM "+fromTableJoiner+ filterStr+"",txnccy);
			
			if(ValidationUtil.isValid(dObj.getBasicFilterStr())) {
				if("P".equals(dObj.getTransLineType())) {
					String basicFilterCond =Pattern.compile("TRN_CCY", Pattern.CASE_INSENSITIVE)
		                    .matcher(dObj.getBasicFilterStr())
		                    .replaceAll("CONTRACT_CCY");
					basicFilterCond = Pattern.compile("TRANS_LINE_SERV_GRP ", Pattern.CASE_INSENSITIVE)
						    .matcher(basicFilterCond)
						    .replaceAll("TRANS_LINE_PROD_GRP ");
					dObj.setBasicFilterStr(basicFilterCond);
				}
				query = query+ " And "+dObj.getBasicFilterStr();
			}
			if("FLAG".equalsIgnoreCase(dObj.getCategory())) {
				query = query +" and "+getDbFunction("NVL", null)+"(T1.Exception_Flag,'N') IN ('Y','N') ";
				
			}else if("DEFLAG".equalsIgnoreCase(dObj.getCategory()) || "REFLAG".equalsIgnoreCase(dObj.getCategory())) {
				query = query +" and "+getDbFunction("NVL", null)+"(T1.Exception_Flag,'N') = 'Y' ";
			}
			if(!"DEFLAG".equalsIgnoreCase(dObj.getCategory())) {
				query = query + specialFilter;
			}
			query = query +" and not exists ( SELECT * FROM ( "+
					" SELECT S2.* FROM RA_Exception_Header_Pend S1,RA_Exception_Details_Pend S2 "+
					" WHERE S1.COUNTRY = S2.COUNTRY AND S1.LE_BOOK = S2.LE_BOOK AND S1.Exception_Reference = S2.Exception_Reference "+
					" AND S1.Record_Indicator !=0) A1 "+ 
					" where A1.Country = t1.Country and A1.LE_Book = T1.LE_BOOK  and A1.Business_Date = T1.BUSINESS_DATE "+  
					" and A1.Business_Line_ID = T1.BUSINESS_LINE_ID and A1.TRANS_LINE_ID = t1.TRANS_LINE_ID  and A1.Trans_Sequence = T1.TRANS_SEQUENCE)"; 
			
			
			dObj.setVerificationRequired(false);
			String orderBy =" Order by Business_Date,Trans_Line_ID,Trans_Sequence ";
			StringBuffer strBufApprove = new StringBuffer(query);
			strBufApprove.append(" AND T1.COUNTRY"+getDbFunction("PIPELINE", "")+"'-'"+getDbFunction("PIPELINE", "")+"T1.LE_BOOK = '"+dObj.getCountry()+"-"+dObj.getLeBook()+"' ");
			if(("Y".equalsIgnoreCase(visionUsersVb.getUpdateRestriction()))){
				if(ValidationUtil.isValid(visionUsersVb.getCountry())){
					strBufApprove.append(" AND T1.COUNTRY"+getDbFunction("PIPELINE", "")+"'-'"+getDbFunction("PIPELINE", "")+"T1.LE_BOOK IN ("+visionUsersVb.getCountry()+") ");
				}
				if(ValidationUtil.isValid(visionUsersVb.getClebTrasnBusline())){
					String userSao = commonDao.getUserSoc(visionUsersVb.getClebTrasnBusline());
					strBufApprove.append(" AND (T1.TRANS_LINE_ID IN ("+userSao+") OR Business_Line_ID IN ("+userSao+") )");
				}
				/*if(ValidationUtil.isValid(visionUsersVb.getOtherAttr())){
					String rcCode = getUserRestrictedRcCode(visionUsersVb.getCountry(),visionUsersVb.getOtherAttr());
					strBufApprove.append(" AND t1.CUSTOMER_ID IN ("+rcCode+") ");
				}*/
			}
			
			return getQueryPopupResults(dObj, null, strBufApprove, null, orderBy, params,getLeakageDataMapper(dObj.getCategory(),currencyPrecisionlst));
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("Error: Leakage Transactions Details Exception :   ");
			return null;
		}
	}
	protected int doInsertionAppr(ExceptionConfigDetailsVb vObject){
		vObject.setRecordIndicatorNt(175);
		vObject.setRecordIndicator(Constants.STATUS_INSERT);
		if("DEFLAG".equalsIgnoreCase(vObject.getCategory())) {
			vObject.setExceptionType("DEFLAG");
			vObject.setExceptionAmountFcy("0");
			vObject.setExceptionAmountLcy("0");
			vObject.setExceptionCount("0");
		}
		String businessDate =" ?";
		if (databaseType.equalsIgnoreCase("ORACLE")) {
			businessDate = " TO_DATE(?,'DD-Mon-RRRR')";
		}
			
		String query =  " Insert Into RA_Exception_Details_Pend(COUNTRY,LE_BOOK,"
				+ " Exception_Reference, Business_Date, Trans_Line_Id, Trans_Sequence,"
				+ " Business_Line_ID, Leakage_Amt_FCY, Leakage_Amt_LCY,Leakage_Count,Exception_Date,  Exception_Type_AT,Exception_Type,"
				+ " Exception_Remarks, Exception_IE_Amount_FCY, Exception_IE_Amount_LCY,EXCEPTION_IE_COUNT,MR_Business_Date,"
				+ " MR_Trans_Line_Id, MR_Trans_Sequence, MR_Business_Line_ID, MR_Remarks,"
				+ " Prior_Exception_Amt_FCY,Prior_Exception_Amt_LCY,Prior_Exception_Count, "
				+ " RECORD_INDICATOR_NT, RECORD_INDICATOR ,MAKER,VERIFIER,DATE_LAST_MODIFIED,DATE_CREATION,Internal_Status) "
				+ " Values (?,?,?, "+businessDate+",?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?," + getDbFunction("SYSDATE")+ ","+ getDbFunction("SYSDATE") + ",?)";

		Object[] args = { vObject.getCountry(), vObject.getLeBook(), vObject.getExceptionReference(),
				vObject.getBusinessDate(), vObject.getTransLineId(),vObject.getTransSequence(), 
				vObject.getBusinessLineId(),
				ValidationUtil.isValid(vObject.getLekageAmountFcy())?vObject.getLekageAmountFcy().replaceAll(",", ""):"0",
				ValidationUtil.isValid(vObject.getLekageAmountLcy())?vObject.getLekageAmountLcy().replaceAll(",", ""):"0",
				ValidationUtil.isValid(vObject.getLeakageCount())?vObject.getLeakageCount().replaceAll(",", ""):"0",
				vObject.getExceptionDate(),vObject.getExceptionTypeAT(),
				vObject.getExceptionType(), vObject.getExceptionTypeRemarks(),
				ValidationUtil.isValid(vObject.getExceptionAmountFcy())?vObject.getExceptionAmountFcy().replaceAll(",", ""):"0",
				ValidationUtil.isValid(vObject.getExceptionAmountLcy())?vObject.getExceptionAmountLcy().replaceAll(",", ""):"0",
				ValidationUtil.isValid(vObject.getExceptionCount())?vObject.getExceptionCount().replaceAll(",", ""):"0",
				vObject.getMrbusinessDate(),
				vObject.getMrtransLineId(),vObject.getMrTransSequence(),
				vObject.getMrbusinessLineId(),vObject.getMrRemarks(),
				ValidationUtil.isValid(vObject.getExceptionIEAmountFcy())?vObject.getExceptionIEAmountFcy().replaceAll(",", ""):"0",
				ValidationUtil.isValid(vObject.getExceptionIEAmountLcy())?vObject.getExceptionIEAmountLcy().replaceAll(",", ""):"0",
				ValidationUtil.isValid(vObject.getExceptionIECount())?vObject.getExceptionIECount().replaceAll(",", ""):"0",
				vObject.getRecordIndicatorNt(),vObject.getRecordIndicator(),
				vObject.getMaker(), vObject.getVerifier(),vObject.getInternalStatus()};
		return getJdbcTemplate().update(query,args);
	}
	protected int approveDetailRecord(ExceptionConfigDetailsVb vObject){
		vObject.setRecordIndicatorNt(175);
		if("DEFLAG".equalsIgnoreCase(vObject.getCategory())) {
			vObject.setExceptionType("DEFLAG");
			vObject.setExceptionAmountFcy("0");
			vObject.setExceptionAmountLcy("0");
			vObject.setExceptionCount("0");
		}
		String businessDate =" ?";
		if (databaseType.equalsIgnoreCase("ORACLE")) {
			businessDate = " TO_DATE(?,'DD-Mon-RRRR')";
		}
		String mrBusinessDate=ValidationUtil.isValid(vObject.getMrbusinessDate()) ? businessDate:"?";
		String query =  " Insert Into RA_Exception_Details(COUNTRY,LE_BOOK,"
				+ " Exception_Reference, Business_Date, Trans_Line_Id, Trans_Sequence,"
				+ " Business_Line_ID, Leakage_Amt_FCY, Leakage_Amt_LCY,Leakage_Count,Exception_Date,  Exception_Type_AT,Exception_Type,"
				+ " Exception_Remarks, Exception_IE_Amount_FCY, Exception_IE_Amount_LCY,EXCEPTION_IE_COUNT, MR_Business_Date,"
				+ " MR_Trans_Line_Id, MR_Trans_Sequence, MR_Business_Line_ID, MR_Remarks,"
				+ " Prior_Exception_Amt_FCY,Prior_Exception_Amt_LCY,Prior_Exception_Count,"
				+ " RECORD_INDICATOR_NT, RECORD_INDICATOR ,MAKER,VERIFIER,DATE_LAST_MODIFIED,DATE_CREATION,Internal_Status) "
				+ " Values (?,?,?,"+businessDate+",?,?,?,?,?,?,"+businessDate+",?,?,?,?,?,?,"+
				mrBusinessDate
				+",?,?,?,?,?,?,?,?,?,?,?," + getDbFunction("SYSDATE")+ ","+getDbFunction("DATE_CREATION")+",0)";

		Object[] args = { vObject.getCountry(), vObject.getLeBook(), vObject.getExceptionReference(),
				vObject.getBusinessDate(), vObject.getTransLineId(),vObject.getTransSequence(), 
				vObject.getBusinessLineId(),
				ValidationUtil.isValid(vObject.getLekageAmountFcy())?vObject.getLekageAmountFcy().replaceAll(",", ""):"0",
				ValidationUtil.isValid(vObject.getLekageAmountLcy())?vObject.getLekageAmountLcy().replaceAll(",", ""):"0",
				ValidationUtil.isValid(vObject.getLeakageCount())?vObject.getLeakageCount().replaceAll(",", ""):"0",
				vObject.getExceptionDate(),vObject.getExceptionTypeAT(),
				vObject.getExceptionType(), vObject.getExceptionTypeRemarks(),
				ValidationUtil.isValid(vObject.getExceptionAmountFcy())?vObject.getExceptionAmountFcy().replaceAll(",", ""):"0",
				ValidationUtil.isValid(vObject.getExceptionAmountLcy())?vObject.getExceptionAmountLcy().replaceAll(",", ""):"0",
				ValidationUtil.isValid(vObject.getExceptionCount())?vObject.getExceptionCount().replaceAll(",", ""):"0",
				vObject.getMrbusinessDate(),
				vObject.getMrtransLineId(),vObject.getMrTransSequence(),
				vObject.getMrbusinessLineId(),vObject.getMrRemarks(),
				ValidationUtil.isValid(vObject.getExceptionIEAmountFcy())?vObject.getExceptionIEAmountFcy().replaceAll(",", ""):"0",
				ValidationUtil.isValid(vObject.getExceptionIEAmountLcy())?vObject.getExceptionIEAmountLcy().replaceAll(",", ""):"0",
				ValidationUtil.isValid(vObject.getExceptionIECount())?vObject.getExceptionIECount().replaceAll(",", ""):"0",	
				vObject.getRecordIndicatorNt(),vObject.getRecordIndicator(),
				vObject.getMaker(), vObject.getVerifier(),vObject.getDateCreation()};
		return getJdbcTemplate().update(query,args);
	}
	protected int doUpdateAppr(ExceptionConfigDetailsVb vObject) {
		String dateLastModified = "";
		String businessDate ="";
		if ("ORACLE".equalsIgnoreCase(databaseType)) {
			dateLastModified = "To_Date(?, 'DD-MM-YYYY HH24:MI:SS')";
			businessDate = " TO_DATE(?,'dd-Mon-RRRR HH24:MI:SS')";
		} else if ("MSSQL".equalsIgnoreCase(databaseType)) {
			dateLastModified = "CONVERT(datetime, ?, 103)";
			businessDate =" ?";
		}
		
			
		String query = " Update RA_Exception_Details_Pend set "
				+ " Exception_Date = ?,Exception_Type = ?,Exception_Remarks = ?, "
				+ " Exception_IE_Amount_FCY=? ,Exception_IE_Amount_LCY= ?,Exception_IE_Count = ?,MR_Business_Date= ?,MR_Trans_Line_Id=?, "
				+"  MR_Trans_Sequence =? , 	MR_Business_Line_ID= ?, MR_Remarks =?,"
				+ " MAKER= ? ," + " VERIFIER= ? ,"
				+ "DATE_LAST_MODIFIED= "
				+ getDbFunction("SYSDATE")
				+ " ,DATE_CREATION = "+dateLastModified+" " 
				+ " WHERE COUNTRY= ? AND LE_BOOK= ? AND Exception_Reference = ? AND Business_Date = "+businessDate
				+ " AND Trans_Line_Id =? AND Trans_Sequence =? AND Business_Line_ID =?";
		
		Object[] args = { vObject.getExceptionDate(),
				vObject.getExceptionType(), vObject.getExceptionTypeRemarks(),
				ValidationUtil.isValid(vObject.getExceptionAmountFcy())?vObject.getExceptionAmountFcy().replaceAll(",", ""):"0",
				ValidationUtil.isValid(vObject.getExceptionAmountLcy())?vObject.getExceptionAmountLcy().replaceAll(",", ""):"0",
				ValidationUtil.isValid(vObject.getExceptionCount())?vObject.getExceptionCount().replaceAll(",", ""):"0",
				vObject.getMrbusinessDate(),
				vObject.getMrtransLineId(),vObject.getMrTransSequence(),
				vObject.getMrbusinessLineId(),vObject.getMrRemarks(),
				vObject.getMaker(), vObject.getVerifier(),vObject.getDateCreation(),vObject.getCountry(), vObject.getLeBook(), vObject.getExceptionReference(),
				vObject.getBusinessDate(), vObject.getTransLineId(),vObject.getTransSequence(), 
				vObject.getBusinessLineId() };
		return getJdbcTemplate().update(query, args);
	}
	@Override
	protected List<ExceptionConfigDetailsVb> selectApprovedRecord(ExceptionConfigDetailsVb vObject) {
		return getQueryResultsForReview(vObject, Constants.STATUS_ZERO);
	}
	@Override
	public List<ExceptionConfigDetailsVb> doSelectPendingRecord(ExceptionConfigDetailsVb vObject) {
		return getQueryResultsForReview(vObject, Constants.STATUS_PENDING);
	}
	public int doDeleteDetailRecordPend(ExceptionConfigDetailsVb dObj){
		String businessDate  = " ? ";
		if ("ORACLE".equalsIgnoreCase(databaseType)) {
		businessDate  = " TO_DATE(?,'dd-Mon-RRRR HH24:MI:SS')";
		}
		String query = "Delete from RA_Exception_Details_Pend WHERE Country = ? and "
				+ " LE_Book = ? and Business_Date = "+businessDate
				+ " And Trans_Line_Id = ? "
				+ "	And Trans_Sequence = ? And Business_Line_ID = ? "
				+ "	And Exception_Reference = ?";
		
		Object objParams[] = new Object[7];
		objParams[0] = new String(dObj.getCountry());
		objParams[1] = new String(dObj.getLeBook());
		objParams[2] = new String(dObj.getBusinessDate());
		objParams[3] = new String(dObj.getTransLineId());
		objParams[4] = new Integer(dObj.getTransSequence());
		objParams[5] = new String(dObj.getBusinessLineId());
		objParams[6] = new String(dObj.getExceptionReference());
		return getJdbcTemplate().update(query,objParams);
	}
	public int doDeletePendingRecord(ExceptionConfigDetailsVb dObj){
		String query = "Delete from RA_Exception_Details_Pend WHERE Country = ? and LE_Book = ? And Exception_Reference = ?";
		
		Object objParams[] = new Object[3];
		objParams[0] = new String(dObj.getCountry());
		objParams[1] = new String(dObj.getLeBook());
		objParams[2] = new String(dObj.getExceptionReference());
		return getJdbcTemplate().update(query,objParams);
	}
	public List<ExceptionManualFiltersVb> getSourceTableMappings(){
		List<ExceptionManualFiltersVb> collTemp = null;
		String query = "SELECT Source_Table,Table_Alias,Generic_Condition_Product,"
				+ " Generic_Condition_Service,FILTER_COLUMN,SPECIAL_FILTER "+
				" FROM RA_EXCEPTION_SOURCE_TABLE ORDER BY TABLE_ALIAS ";
		try {
			collTemp = getJdbcTemplate().query(query, getSourceTableMapper());
			return collTemp;
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("Error: getSourceTableMappings Exception :   ");
			return null;
		}
	}
	protected RowMapper getSourceTableMapper(){
		RowMapper mapper = new RowMapper() {
			@Override
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				ExceptionManualFiltersVb vObject = new ExceptionManualFiltersVb();
				vObject.setSourceTable(rs.getString("Source_Table"));
				vObject.setAliasName(rs.getString("Table_Alias"));
				vObject.setGenericCondProduct(rs.getString("Generic_Condition_Product"));
				vObject.setGenericCondService(rs.getString("Generic_Condition_Service"));
				vObject.setFilterColumn(rs.getString("FILTER_COLUMN"));
				vObject.setSpecialFilter(rs.getString("SPECIAL_FILTER"));
				return vObject;
			}
		};
		return mapper;
	}
	
	protected int updateExceptionOnIE(ExceptionConfigDetailsVb vObject) {
		String exceptionFlag = "Y";
		String businessDate ="Business_Date = ?";
		if (databaseType.equalsIgnoreCase("ORACLE")) {
			businessDate = "Business_Date = TO_DATE(?,'dd-Mon-RRRR HH24:MI:SS')";
		}
		setSourceTableColumn(vObject.getTransLineType());
		String query = " Update %s set "
				+ " Exception_IE_Amount_FCY= "+getDbFunction("NVL", null)+"(Exception_IE_Amount_FCY,0) + ? ,"
				+ " Exception_IE_Amount_LCY="+getDbFunction("NVL", null)+"(Exception_IE_Amount_LCY ,0)+ ?,"
				+ "Exception_IE_Count= "+getDbFunction("NVL", null) + "(Exception_IE_Count ,0)+ ?,"
				+ "Exception_Flag= ? ,"
				+ " Exception_Type_AT = 7078,Exception_Type = ? ,Exception_Date = ? , Exception_Remarks = ? "
				+ " WHERE COUNTRY= ? AND LE_BOOK= ? AND  "+businessDate
				+ " AND Trans_Line_Id =? AND Trans_Sequence = ? AND Business_Line_ID = ? ";
		if("DEFLAG".equalsIgnoreCase(vObject.getCategory())) {
			exceptionFlag = "N";
			vObject.setExceptionType("");
			query = " Update %s set "
					+ " Exception_IE_Amount_FCY=?,Exception_IE_Amount_LCY=?,Exception_IE_Count = ?,Exception_Flag= ?, "
					+ " Exception_Type_AT = 7078,Exception_Type = ? ,Exception_Date = ? , Exception_Remarks = ? "
					+ " WHERE COUNTRY= ? AND LE_BOOK= ? AND  "+businessDate
					+ " AND Trans_Line_Id =? AND Trans_Sequence =? AND Business_Line_ID =?";
		}
		Object[] args = { 
				ValidationUtil.isValid(vObject.getExceptionAmountFcy())?vObject.getExceptionAmountFcy().replaceAll(",", ""):"0",
				ValidationUtil.isValid(vObject.getExceptionAmountLcy())?vObject.getExceptionAmountLcy().replaceAll(",", ""):"0",
				ValidationUtil.isValid(vObject.getExceptionCount())?vObject.getExceptionCount().replaceAll(",", ""):"0",
				exceptionFlag,vObject.getExceptionType(),vObject.getExceptionDate(),vObject.getExceptionTypeRemarks(),
				vObject.getCountry(),vObject.getLeBook(), 
				vObject.getBusinessDate(), vObject.getTransLineId(),vObject.getTransSequence(), 
				vObject.getBusinessLineId() };
		query = String.format(query,txnTable);
		return getJdbcTemplate().update(query, args);
	}

	@Transactional(rollbackForClassName={"com.vision.exception.RuntimeCustomException"})
	public ExceptionCode doUpdateDetailsRecord(List<ExceptionConfigDetailsVb> vObjects) throws RuntimeCustomException {
		ExceptionCode exceptionCode =null;
		strApproveOperation =Constants.MODIFY;
		strErrorDesc  = "";
		setServiceDefaults();
		try {
				for(ExceptionConfigDetailsVb vObject : vObjects){
					if(vObject.isChecked()){
						if(vObject.isNewRecord()){
							strCurrentOperation = Constants.ADD;
							exceptionCode = doInsertRecordForNonTrans(vObject);
							if(exceptionCode == null || exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION ){
								throw buildRuntimeCustomException(exceptionCode);
							}
						}else{
							strCurrentOperation = Constants.MODIFY;
							exceptionCode = doUpdateRecordForNonTrans(vObject);
							if(exceptionCode == null || exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION ){
								throw buildRuntimeCustomException(exceptionCode);
							}
						}
					}
				}
				return getResultObject(Constants.SUCCESSFUL_OPERATION);
		}catch (RuntimeCustomException rcException) {
			throw rcException;
		}catch (UncategorizedSQLException uSQLEcxception) {
			strErrorDesc = parseErrorMsg(uSQLEcxception);
			exceptionCode = getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION);
			throw buildRuntimeCustomException(exceptionCode);
		}catch(Exception ex){
			//logger.error("Error in Modify.",ex);
			ex.printStackTrace();
			strErrorDesc = ex.getMessage();
			exceptionCode = getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION);
			throw buildRuntimeCustomException(exceptionCode);
		}
	}
	protected ExceptionCode doUpdateRecordForNonTrans(ExceptionConfigDetailsVb vObject) throws RuntimeCustomException {
		List<ExceptionConfigDetailsVb> collTemp = null;
		ExceptionConfigDetailsVb vObjectlocal = null;
		ExceptionCode exceptionCode =null;
		strApproveOperation =Constants.MODIFY;
		strErrorDesc  = "";
		strCurrentOperation = Constants.MODIFY;
		setServiceDefaults();
		vObject.setMaker(getIntCurrentUserId());
		// Search if record already exists in pending.  If it already exists, check for status
		collTemp = doSelectPendingRecord(vObject);
		if (collTemp == null){
			exceptionCode = getResultObject(Constants.ERRONEOUS_OPERATION);
			throw buildRuntimeCustomException(exceptionCode);
		}
		if (collTemp.size() == 0){
			exceptionCode = getResultObject(Constants.ATTEMPT_TO_MODIFY_UNEXISTING_RECORD);
			throw buildRuntimeCustomException(exceptionCode);
		}
		vObjectlocal = ((ArrayList<ExceptionConfigDetailsVb>)collTemp).get(0);
		vObject.setDateCreation(vObjectlocal.getDateCreation());
		vObject.setVerifier(0);
		vObject.setRecordIndicator(Constants.STATUS_INSERT);
		retVal = doUpdateAppr(vObject);
		if (retVal != Constants.SUCCESSFUL_OPERATION){
			exceptionCode = getResultObject(retVal);
			throw buildRuntimeCustomException(exceptionCode);
		}
		return getResultObject(Constants.SUCCESSFUL_OPERATION);
	}

	protected int doInsertPostedAudit(ExceptionConfigDetailsVb vObject, ExceptionConfigHeaderVb dObj){
		setServiceDefaults();
		String businessDate ="?";
		if (databaseType.equalsIgnoreCase("ORACLE")) {
			businessDate = "TO_DATE(?,'dd-Mon-RRRR HH24:MI:SS')";
		}
		String query =  " Insert Into RA_Exception_Posted_Audit(COUNTRY,LE_BOOK,"
				+ " Exception_Reference, POSTED_DATE, Business_Date, Trans_Line_Id, Trans_Sequence,"
				+ " Business_Line_ID, Exception_Date, EXCEPTION_TYPE_AT,  Exception_Type,"
				+ " Exception_Remarks, Exception_IE_Amount_FCY, Exception_IE_Amount_LCY,Exception_IE_Count,MR_Business_Date,"
				+ " MR_Trans_Line_Id, MR_Trans_Sequence, MR_Business_Line_ID, MR_Remarks,"
				+ " RECORD_INDICATOR_NT, RECORD_INDICATOR ,MAKER,VERIFIER,DATE_LAST_MODIFIED,DATE_CREATION) "
				+ " Values (?,?,?," + getDbFunction("SYSDATE")+ ","+businessDate+",?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?," + getDbFunction("SYSDATE")+ ","+ getDbFunction("SYSDATE") + ")";

		Object[] args = { vObject.getCountry(), vObject.getLeBook(), vObject.getExceptionReference(),
				vObject.getBusinessDate(), vObject.getTransLineId(),vObject.getTransSequence(), 
				vObject.getBusinessLineId(),
				vObject.getExceptionDate(),7078,
				vObject.getExceptionType(), vObject.getExceptionTypeRemarks(),
				ValidationUtil.isValid(vObject.getExceptionAmountFcy())?vObject.getExceptionAmountFcy().replaceAll(",", ""):"0",
				ValidationUtil.isValid(vObject.getExceptionAmountLcy())?vObject.getExceptionAmountLcy().replaceAll(",", ""):"0",
				ValidationUtil.isValid(vObject.getExceptionCount())?vObject.getExceptionCount().replaceAll(",", ""):"0",
				vObject.getMrbusinessDate(),
				vObject.getMrtransLineId(),vObject.getMrTransSequence(),
				vObject.getMrbusinessLineId(),vObject.getMrRemarks(),dObj.getRecordIndicatorNt(),dObj.getRecordIndicator(),
				vObject.getMaker(), intCurrentUserId };
		return getJdbcTemplate().update(query,args);
	}
	protected int doInsertDetailRejectAudit(ExceptionConfigDetailsVb vObject){
		String businessDate ="?";
		if (databaseType.equalsIgnoreCase("ORACLE")) {
			businessDate = "TO_DATE(?,'dd-Mon-RRRR HH24:MI:SS')";
		}
		if("DEFLAG".equalsIgnoreCase(vObject.getCategory()))
			vObject.setExceptionType("DEFLAG");
		vObject.setRecordIndicatorNt(175);
		vObject.setRecordIndicator(Constants.STATUS_INSERT);
		String query =  " Insert Into RA_Exception_Details_RejAud(COUNTRY,LE_BOOK,"
				+ " Exception_Reference, Business_Date, Trans_Line_Id, Trans_Sequence,"
				+ " Business_Line_ID, Leakage_Amt_FCY, Leakage_Amt_LCY,Leakage_Count,Exception_Date,  Exception_Type_AT,Exception_Type,"
				+ " Exception_Remarks, Exception_IE_Amount_FCY, Exception_IE_Amount_LCY,EXCEPTION_IE_COUNT,MR_Business_Date,"
				+ " MR_Trans_Line_Id, MR_Trans_Sequence, MR_Business_Line_ID, MR_Remarks,"
				+ " RECORD_INDICATOR_NT, RECORD_INDICATOR ,MAKER,VERIFIER,DATE_LAST_MODIFIED,DATE_CREATION,DATE_OF_REJECTION,REJECTED_BY) "
				+ " Values (?,?,?,"+businessDate+",?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,"+ getDbFunction("SYSDATE") + ",?)";

		Object[] args = { vObject.getCountry(), vObject.getLeBook(), vObject.getExceptionReference(),
				vObject.getBusinessDate(), vObject.getTransLineId(),vObject.getTransSequence(), 
				vObject.getBusinessLineId(),
				ValidationUtil.isValid(vObject.getLekageAmountFcy())?vObject.getLekageAmountFcy().replaceAll(",", ""):"0",
				ValidationUtil.isValid(vObject.getLekageAmountLcy())?vObject.getLekageAmountLcy().replaceAll(",", ""):"0",
				ValidationUtil.isValid(vObject.getLeakageCount())?vObject.getLeakageCount().replaceAll(",", ""):"0",
				vObject.getExceptionDate(),vObject.getExceptionTypeAT(),
				vObject.getExceptionType(), vObject.getExceptionTypeRemarks(),
				ValidationUtil.isValid(vObject.getExceptionAmountFcy())?vObject.getExceptionAmountFcy().replaceAll(",", ""):"0",
				ValidationUtil.isValid(vObject.getExceptionAmountLcy())?vObject.getExceptionAmountLcy().replaceAll(",", ""):"0",
				ValidationUtil.isValid(vObject.getExceptionCount())?vObject.getExceptionCount().replaceAll(",", ""):"0",
				vObject.getMrbusinessDate(),
				vObject.getMrtransLineId(),vObject.getMrTransSequence(),
				vObject.getMrbusinessLineId(),vObject.getMrRemarks(),vObject.getRecordIndicatorNt(),vObject.getRecordIndicator(),
				vObject.getMaker(), vObject.getVerifier(),vObject.getDateCreation(),vObject.getDateLastModified(),intCurrentUserId };
		return getJdbcTemplate().update(query,args);
	}
	@Override
	protected void setServiceDefaults() {
		serviceName = "VarianceFlagging";
		serviceDesc = "Variance Flagging";
		tableName = "RA_Exception_Details";
		childTableName = "RA_Exception_Details";
		intCurrentUserId = CustomContextHolder.getContext().getVisionId();
	}
	@Override
	protected String getAuditString(ExceptionConfigDetailsVb vObject){
		final String auditDelimiter = vObject.getAuditDelimiter();
		final String auditDelimiterColVal = vObject.getAuditDelimiterColVal();
		StringBuffer strAudit = new StringBuffer("");
		try{
			if(ValidationUtil.isValid(vObject.getCountry()))
				strAudit.append("COUNTRY"+auditDelimiterColVal+vObject.getCountry().trim());
			else
				strAudit.append("COUNTRY"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);

			if(ValidationUtil.isValid(vObject.getLeBook()))
				strAudit.append("LE_BOOK"+auditDelimiterColVal+vObject.getLeBook().trim());
			else
				strAudit.append("LE_BOOK"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			
			if(ValidationUtil.isValid(vObject.getExceptionReference()))
				strAudit.append("Exception_Reference"+auditDelimiterColVal+vObject.getExceptionReference().trim());
			else
				strAudit.append("Exception_Reference"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			
			if(ValidationUtil.isValid(vObject.getBusinessDate()))
				strAudit.append("Business_date"+auditDelimiterColVal+vObject.getBusinessDate().trim());
			else
				strAudit.append("Business_date"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);

			if(ValidationUtil.isValid(vObject.getTransLineId()))
				strAudit.append("TRANS_LINE_ID"+auditDelimiterColVal+vObject.getTransLineId().trim());
			else
				strAudit.append("TRANS_LINE_ID"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			
			if(ValidationUtil.isValid(vObject.getTransSequence()))
				strAudit.append("TRANS_SEQUENCE"+auditDelimiterColVal+vObject.getTransSequence());
			else
				strAudit.append("TRANS_SEQUENCE"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			
			if(ValidationUtil.isValid(vObject.getBusinessLineId()))
				strAudit.append("BUSINESS_LINE_ID"+auditDelimiterColVal+vObject.getBusinessLineId().trim());
			else
				strAudit.append("BUSINESS_LINE_ID"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			
			if(ValidationUtil.isValid(vObject.getExceptionDate()))
				strAudit.append("EXCEPTION_DATE"+auditDelimiterColVal+vObject.getExceptionDate().trim());
			else
				strAudit.append("EXCEPTION_DATE"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			
			if(ValidationUtil.isValid(vObject.getExceptionTypeAT()))
				strAudit.append("EXCEPTION_TYPE_AT"+auditDelimiterColVal+vObject.getExceptionTypeAT());
			else
				strAudit.append("EXCEPTION_TYPE_AT"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			
			if(ValidationUtil.isValid(vObject.getExceptionType()))
				strAudit.append("EXCEPTION_TYPE"+auditDelimiterColVal+vObject.getExceptionType().trim());
			else
				strAudit.append("EXCEPTION_TYPE"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			
			if(ValidationUtil.isValid(vObject.getExceptionTypeRemarks()))
				strAudit.append("EXCEPTION_REMARKS"+auditDelimiterColVal+vObject.getExceptionTypeRemarks().trim());
			else
				strAudit.append("EXCEPTION_REMARKS"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			
			if(ValidationUtil.isValid(vObject.getMrbusinessDate()))
				strAudit.append("MR_Business_date"+auditDelimiterColVal+vObject.getMrbusinessDate().trim());
			else
				strAudit.append("MR_Business_date"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);

			if(ValidationUtil.isValid(vObject.getMrtransLineId()))
				strAudit.append("MR_TRANS_LINE_ID"+auditDelimiterColVal+vObject.getMrtransLineId().trim());
			else
				strAudit.append("MR_TRANS_LINE_ID"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			
			if(ValidationUtil.isValid(vObject.getMrTransSequence()))
				strAudit.append("MR_TRANS_SEQUENCE"+auditDelimiterColVal+vObject.getMrTransSequence());
			else
				strAudit.append("MR_TRANS_SEQUENCE"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			
			if(ValidationUtil.isValid(vObject.getMrbusinessLineId()))
				strAudit.append("MR_BUSINESS_LINE_ID"+auditDelimiterColVal+vObject.getMrbusinessLineId().trim());
			else
				strAudit.append("MR_BUSINESS_LINE_ID"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
		
			if(ValidationUtil.isValid(vObject.getMrRemarks()))
				strAudit.append("MR_REMARKS"+auditDelimiterColVal+vObject.getMrRemarks().trim());
			else
				strAudit.append("MR_REMARKS"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			
			if(ValidationUtil.isValid(vObject.getLekageAmountFcy()))
				strAudit.append("Leakage_Amt_FCY"+auditDelimiterColVal+vObject.getLekageAmountFcy());
			else
				strAudit.append("Leakage_Amt_FCY"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			
			if(ValidationUtil.isValid(vObject.getLekageAmountLcy()))
				strAudit.append("Leakage_Amt_LCY"+auditDelimiterColVal+vObject.getLekageAmountLcy());
			else
				strAudit.append("Leakage_Amt_LCY"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			
		}catch(Exception ex){
			strErrorDesc = ex.getMessage();
			strAudit = strAudit.append(strErrorDesc);
			ex.printStackTrace();
		}
		return strAudit.toString();
	}
	@Transactional(rollbackForClassName={"com.vision.exception.RuntimeCustomException"})
	public ExceptionCode doDeleteApprRecord(List<ExceptionConfigDetailsVb> vObjects) throws RuntimeCustomException {
		ExceptionCode exceptionCode =null;
		strApproveOperation = Constants.DELETE;
		strErrorDesc  = "";
		setServiceDefaults();
		strCurrentOperation = Constants.DELETE;
		int retVal = 0;
		try {
			for(ExceptionConfigDetailsVb vObject : vObjects){
				List<ExceptionConfigDetailsVb> collTemp = doSelectPendingRecord(vObject);
				if (collTemp == null || collTemp.size() == 0){
					exceptionCode = getResultObject(Constants.ATTEMPT_TO_DELETE_UNEXISTING_RECORD);
					throw buildRuntimeCustomException(exceptionCode);
				}
				retVal = doDeleteDetailRecordPend(vObject);
				auditForDeletedDetailRecords(vObject);
				if(retVal != Constants.SUCCESSFUL_OPERATION ){
					exceptionCode = getResultObject(retVal);
					throw buildRuntimeCustomException(exceptionCode);
				}
			}
			return getResultObject(Constants.SUCCESSFUL_OPERATION);
		}catch (RuntimeCustomException rcException) {
			throw rcException;
		}catch (UncategorizedSQLException uSQLEcxception) {
			strErrorDesc = parseErrorMsg(uSQLEcxception);
			exceptionCode = getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION);
			throw buildRuntimeCustomException(exceptionCode);
		}catch(Exception ex){
			//logger.error("Error in Delete.",ex);
			strErrorDesc = ex.getMessage();
			exceptionCode = getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION);
			throw buildRuntimeCustomException(exceptionCode);
		}
	}
	public int getCntforRejectDetailsAudit(ExceptionConfigDetailsVb dObj) {
		String businessDate ="?";
		if (databaseType.equalsIgnoreCase("ORACLE")) {
			businessDate = "TO_DATE(?,'dd-Mon-RRRR HH24:MI:SS')";
		}
		try {
			String sql = "SELECT COUNT(*) CNT FROM RA_EXCEPTION_DETAILS_REJAUD"
					+ " WHERE COUNTRY = ? AND LE_BOOK = ?  AND EXCEPTION_REFERENCE = ? AND BUSINESS_DATE =  "+businessDate
					+ " AND TRANS_LINE_ID = ?  AND TRANS_SEQ = ?  AND BUSINESS_LINE_ID = ? ";
			Object objParams[] = new Object[7];
			objParams[0] = new String(dObj.getCountry());
			objParams[1] = new String(dObj.getLeBook());
			objParams[2] = new String(dObj.getExceptionReference());
			objParams[3] = new String(dObj.getBusinessDate());
			objParams[4] = new String(dObj.getTransLineId());
			objParams[5] = dObj.getTransSequence();
			objParams[6] = new String(dObj.getBusinessLineId());
			int count = getJdbcTemplate().queryForObject(sql, objParams,Integer.class);
			return count ;
		} catch(Exception e) {
			return 0;
		}
	}
	public int deleteRejAuditDetailRecord(ExceptionConfigDetailsVb vObject) {
		String businessDate ="?";
		if (databaseType.equalsIgnoreCase("ORACLE")) {
			businessDate = "TO_DATE(?,'dd-Mon-RRRR HH24:MI:SS')";
		}
		String query = "Delete from RA_EXCEPTION_DETAILS_REJAUD  "
				+ " WHERE COUNTRY = ? AND LE_BOOK = ?  AND EXCEPTION_REFERENCE = ? AND BUSINESS_DATE =  "+businessDate
				+ " AND TRANS_LINE_ID = ?  AND TRANS_SEQ = ?  AND BUSINESS_LINE_ID = ? ";
		Object[] args = { vObject.getCountry(), vObject.getLeBook(), vObject.getExceptionReference() ,vObject.getBusinessDate(),
				vObject.getTransLineId(),vObject.getTransSequence(),vObject.getBusinessLineId()};
		getJdbcTemplate().update(query, args);
		return Constants.SUCCESSFUL_OPERATION;
	}

	public void auditForDeletedDetailRecords(ExceptionConfigDetailsVb exceptionDetVb) {
		try {
			if (getCntforRejectDetailsAudit(exceptionDetVb) > 0)
				deleteRejAuditDetailRecord(exceptionDetVb);
			doInsertDetailRejectAudit(exceptionDetVb);
		} catch (Exception e) {

		}
	}
	public int doDeleteAllRejectAuditDet(ExceptionConfigDetailsVb dObj){
		String query = "Delete from RA_Exception_details_RejAud WHERE Country = ? and LE_Book = ? And Exception_Reference = ?";
		
		Object objParams[] = new Object[3];
		objParams[0] = new String(dObj.getCountry());
		objParams[1] = new String(dObj.getLeBook());
		objParams[2] = new String(dObj.getExceptionReference());
		return getJdbcTemplate().update(query,objParams);
	}
	public List getAuditDetails(ExceptionConfigDetailsVb vObject) {
		List<ExceptionConfigDetailsVb> collTemp = new ArrayList<>();
		List<CurrencyDetailsVb> currencyPrecisionlst = commonDao.getCurrencyDecimals(false);
		String exceptionAmtFcy = "";
		String exceptionAmtLcy = "";
		setSourceTableColumn(vObject.getTransLineType());
		//if ("REFLAG".equalsIgnoreCase(vObject.getCategory()) || "FLAG".equalsIgnoreCase(vObject.getCategory())) {
			exceptionAmtFcy = "ISNULL(T4.EXCEPTION_IE_AMOUNT_FCY,0)";
			exceptionAmtLcy = "ISNULL(T4.EXCEPTION_IE_AMOUNT_FCY,0)";
		//}
			String businessDate ="?";
			if (databaseType.equalsIgnoreCase("ORACLE")) {
				businessDate = "TO_DATE(?,'dd-Mon-RRRR HH24:MI:SS')";
			}
		try {
			String sql =  String.format( " SELECT T1.COUNTRY,T1.LE_BOOK,T1.EXCEPTION_REFERENCE,T1.EXCEPTION_REFERENCE "+ getDbFunction("PIPELINE")+"' - '"+getDbFunction("PIPELINE")+"T3.REFERENCE_DESCRIPTION REFERENCE_DESCRIPTION,"
							  +dbFunctionFormats("T1.POSTED_DATE","DATETIME_FORMAT", null)+" POSTED_DATE,T1.POSTED_DATE POSTED_DATE_1, "
					  + " T1.TRANS_LINE_ID, "+
					  " T1.TRANS_SEQUENCE,T1.BUSINESS_LINE_ID,"
					  + " (Select T1.TRANS_LINE_ID "+getDbFunction("PIPELINE")+"' - '"+getDbFunction("PIPELINE")+"TRANS_LINE_DESCRIPTION from RA_MST_TRANS_LINE_HEADER where country = t1.Country "
					  + " and LE_Book = t1.LE_BOOK and TRANS_LINE_ID = T1.TRANS_LINE_ID) Trans_Line_Desc, "
					  + " (Select T1.BUSINESS_LINE_ID "+getDbFunction("PIPELINE")+"' - '"+getDbFunction("PIPELINE")+"Business_Line_Description from RA_MST_Business_Line_Header where Country = t1.Country "
					  + " and LE_Book = t1.LE_Book and Business_Line_ID = t1.Business_Line_ID) Bus_Line_Desc, "
					  + " T1.EXCEPTION_TYPE,"
					  + "	"+dbFunctionFormats("T1.EXCEPTION_DATE","DATE_FORMAT", null)+" EXCEPTION_DATE, "
					  + "	"+dbFunctionFormats("T1.Business_Date","DATE_FORMAT", null)+" Business_Date, " 
					  +" (select ALPHA_SUBTAB_DESCRIPTION from ALPHA_SUB_TAB "
					  + "      where Alpha_tab = T1.EXCEPTION_TYPE_AT and ALPHA_SUB_TAB=T1.EXCEPTION_TYPE) EXCEPTION_TYPE_DESC, "
					  + " T1.EXCEPTION_REMARKS, "
					  + " "+getDbFunction("NVL", null)+"(T1.EXCEPTION_IE_AMOUNT_FCY,0) EXCEPTION_AMOUNT_FCY,"
					  + " "+getDbFunction("NVL", null)+"(T1.EXCEPTION_IE_AMOUNT_LCY,0) EXCEPTION_AMOUNT_LCY,"
					  + " "+getDbFunction("NVL", null)+"(T1.EXCEPTION_IE_COUNT,0) EXCEPTION_COUNT,"
					  + " "+getDbFunction("NVL", null)+"(T4.LEAKAGE_AMT_LCY,0) Leakage_LCY,"
					  + " "+getDbFunction("NVL", null)+"(T4.LEAKAGE_AMT_FCY,0) Leakage_FCY,"
					  + " "+getDbFunction("NVL", null)+"(T4.Leakage_Count,0) Leakage_Count,"
					  + " T1.MAKER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "+ getDbFunction("NVL") + "(T1.MAKER,0) ) MAKER_NAME,  "
					  + " T1.VERIFIER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "+ getDbFunction("NVL") + "(T1.VERIFIER,0) ) VERIFIER_NAME,  "
					  + " t3.exception_category, "
					  + " (select ALPHA_SUBTAB_DESCRIPTION from ALPHA_SUB_TAB " 
					  +	"  where Alpha_tab = 7079 and ALPHA_SUB_TAB=t3.exception_category) exception_category_DESC, "
					  + " "+getDbFunction("NVL", null)+"(T2.EXPECTED_IE_AMT_FCY,0) EXPECTED_IE_AMT_FCY, "
					  + " "+getDbFunction("NVL", null)+"(T2.EXPECTED_IE_AMT_LCY,0) EXPECTED_IE_AMT_LCY, "
					  + " "+getDbFunction("NVL", null)+"(T2.CHARGEABLE_COUNT,0) EXPECTED_IE_COUNT, "
					  + " "+getDbFunction("NVL", null)+"(T2.ACTUAL_IE_AMT_FCY,0) ACTUAL_IE_AMT_FCY, "
					  + " "+getDbFunction("NVL", null)+"(T2.ACTUAL_IE_AMT_LCY,0) ACTUAL_IE_AMT_LCY, "
					  + " "+getDbFunction("NVL", null)+"(T2.actual_income_count,0) ACTUAL_IE_COUNT, "
					  + " RTRIM(LTRIM(("+getDbFunction("NVL", null)+"(T2.EXPECTED_IE_AMT_FCY,0) - ("+getDbFunction("NVL", null)+"(T2.ACTUAL_IE_AMT_FCY,0))))) Original_Leakage_FCY,"
					  + " RTRIM(LTRIM(("+getDbFunction("NVL", null)+"(T2.EXPECTED_IE_AMT_LCY,0) - ("+getDbFunction("NVL", null)+"(T2.ACTUAL_IE_AMT_LCY,0))))) Original_Leakage_LCY,"
					  + " RTRIM(LTRIM(("+getDbFunction("NVL", null)+"(Chargeable_Count,0) - ("+getDbFunction("NVL", null)+"(Actual_Income_Count,0))))) Original_Leakage_Count,"
					  + getDbFunction("NVL", null)+"(T2.EXPECTED_IE_AMT_FCY, 0)- "+getDbFunction("NVL", null)+"(T4.LEAKAGE_AMT_FCY, 0)EXCEPTION_IE_AMOUNT_FCY, "
					  + getDbFunction("NVL", null)+"(T2.EXPECTED_IE_AMT_LCY, 0)- "+getDbFunction("NVL", null)+"(T4.LEAKAGE_AMT_LCY, 0)EXCEPTION_IE_AMOUNT_LCY,"
					  + getDbFunction("NVL", null)+"(T2.CHARGEABLE_COUNT, 0)- "+getDbFunction("NVL", null)+"(T4.LEAKAGE_COUNT, 0)EXCEPTION_IE_COUNT,"
					  + " T2.POSTING_CCY,T2.TRN_CCY,T2.FX_RATE, "
					  + getDbFunction("NVL", null)+"(T4.Prior_Exception_Amt_LCY, 0) Prior_Exception_Amt_LCY, "
					  + getDbFunction("NVL", null)+"(T4.Prior_Exception_Amt_FCY, 0) Prior_Exception_Amt_FCY,"
					  + getDbFunction("NVL", null)+"(T4.Prior_Exception_Count, 0) Prior_Exception_Count "
					   +" FROM RA_EXCEPTION_POSTED_AUDIT T1,%s T2, RA_EXCEPTION_HEADER T3,RA_EXCEPTION_DETAILS T4"
					   + " WHERE T1.Country = T2.COUNTRY AND "
					   + " T1.LE_BOOK = T2.LE_Book AND "
					   + " T1.BUSINESS_DATE = T2.BUSINESS_DATE AND  "
					   + " T1.TRANS_SEQUENCE = T2.TRANS_SEQUENCE AND  "
					   + " T1.BUSINESS_LINE_ID = T2.BUSINESS_LINE_ID AND " 
					   + " T1.TRANS_LINE_ID = T2.TRANS_LINE_ID AND "
					   + " T1.COUNTRY = T3.COUNTRY "
					   + " AND T1.LE_BOOK = T3.LE_BOOK "
					   + " AND T1.EXCEPTION_REFERENCE = T3.EXCEPTION_REFERENCE"
					   + "  and T1.Country = T4.COUNTRY "
					   + " AND T1.LE_BOOK = T4.LE_Book "
					   + " AND T1.EXCEPTION_REFERENCE = T4.EXCEPTION_REFERENCE"
					   + " AND T1.BUSINESS_DATE = T4.BUSINESS_DATE " 
					   + " AND T1.TRANS_SEQUENCE = T4.TRANS_SEQUENCE " 
					   + " AND T1.BUSINESS_LINE_ID = T4.BUSINESS_LINE_ID " 
					   + " AND T1.TRANS_LINE_ID = T4.TRANS_LINE_ID "+
					  "  and T1.COUNTRY = ? AND T1.LE_BOOK = ? AND T1.BUSINESS_DATE = "+businessDate+
					  " AND T1.TRANS_SEQUENCE = ? AND T1.BUSINESS_LINE_ID = ? "+
					  " AND T1.TRANS_LINE_ID = ? "
					  + " ORDER BY POSTED_DATE_1 DESC ",txnTable);
			Object[] args = {vObject.getCountry(),vObject.getLeBook(),vObject.getBusinessDate(),vObject.getTransSequence(),
					vObject.getBusinessLineId(),vObject.getTransLineId()};
			collTemp = getJdbcTemplate().query(sql, args,getAuditDetailMapper(currencyPrecisionlst));
			return collTemp;
		}catch(Exception e) {
			e.printStackTrace();
			return new ArrayList<>();
		}
	}
	protected RowMapper getAuditDetailMapper(List<CurrencyDetailsVb> currencylst){
		RowMapper mapper = new RowMapper() {
			@Override
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				ExceptionConfigDetailsVb vObject = new ExceptionConfigDetailsVb();
				vObject.setCountry(rs.getString("COUNTRY"));
				vObject.setLeBook(rs.getString("LE_BOOK"));
				vObject.setExceptionReference(rs.getString("EXCEPTION_REFERENCE"));
				vObject.setExceptionReferenceDesc(rs.getString("REFERENCE_DESCRIPTION"));
				vObject.setPostedDate(rs.getString("POSTED_DATE"));
				vObject.setBusinessDate(rs.getString("Business_Date"));
				vObject.setTransLineId(rs.getString("Trans_Line_Id"));
				vObject.setExceptionCategory(rs.getString("EXCEPTION_CATEGORY"));
				vObject.setExceptionCategoryDesc(rs.getString("EXCEPTION_CATEGORY_DESC"));
				vObject.setTransLineDescription(rs.getString("Trans_Line_Desc"));
				vObject.setBusinessLineDescription(rs.getString("Bus_Line_Desc"));
				vObject.setTransSequence(rs.getInt("Trans_Sequence"));
				vObject.setBusinessLineId(rs.getString("Business_Line_ID"));
				vObject.setExceptionDate(rs.getString("Exception_Date"));
				vObject.setExceptionType(rs.getString("Exception_Type"));
				vObject.setLekageAmountFcy(rs.getString("Leakage_FCY"));
				vObject.setLekageAmountLcy(rs.getString("Leakage_LCY"));
				vObject.setLeakageCount(rs.getString("Leakage_Count"));
				vObject.setActualAmountFcy(rs.getString("ACTUAL_IE_AMT_FCY"));
				vObject.setActualAmountLcy(rs.getString("ACTUAL_IE_AMT_LCY"));
				vObject.setActualCount(rs.getString("ACTUAL_IE_COUNT"));
				vObject.setExpectedAmountFcy(rs.getString("EXPECTED_IE_AMT_FCY"));
				vObject.setExpectedAmountLcy(rs.getString("EXPECTED_IE_AMT_LCY"));
				vObject.setExpectedCount(rs.getString("EXPECTED_IE_COUNT"));
				vObject.setExceptionTypeDesc(rs.getString("Exception_Type_DESC"));
				vObject.setExceptionTypeRemarks(rs.getString("Exception_Remarks"));
				vObject.setExceptionAmountFcy(rs.getString("Exception_Amount_FCY"));
				vObject.setExceptionAmountLcy(rs.getString("Exception_Amount_LCY"));
				vObject.setExceptionCount(rs.getString("Exception_Count"));
				vObject.setMaker(rs.getInt("MAKER"));
				vObject.setMakerName(rs.getString("MAKER_NAME"));
				vObject.setVerifier(rs.getInt("VERIFIER"));
				vObject.setVerifierName(rs.getString("VERIFIER_NAME"));
				vObject.setExceptionIEAmountFcy(rs.getString("Prior_Exception_Amt_FCY"));
				vObject.setExceptionIEAmountLcy(rs.getString("Prior_Exception_Amt_LCY"));
				vObject.setExceptionIECount(rs.getString("Prior_Exception_Count"));
				vObject.setOrginalLeakageFcy(rs.getString("ORIGINAL_LEAKAGE_FCY"));
				vObject.setOrginalLeakageLcy(rs.getString("ORIGINAL_LEAKAGE_LCY"));
				vObject.setOriginalLeakageCount(rs.getString("ORIGINAL_LEAKAGE_Count"));
				vObject.setPostingCcy(rs.getString("POSTING_CCY"));
				vObject.setTranCcy(rs.getString("TRN_CCY"));
				vObject.setFxRate(dynamicDecimalFormat(rs.getDouble("FX_RATE"),5));
				ExceptionCode exceptionCode = setDecimalPrecision(currencylst,vObject);
				if(exceptionCode.getErrorCode() == Constants.SUCCESSFUL_OPERATION)
					vObject= (ExceptionConfigDetailsVb)exceptionCode.getResponse();
				return vObject;
			}
		};
		return mapper;
	}
	public String getUserRestrictedRcCode(String countryLeBook,String userRcCode) {
		try {
			String sql = " SELECT CUSTOMER_ID FROM RA_CUSTOMERS CUST WHERE "
					+ " CUST.COUNTRY+'-'+CUST.LE_BOOK = "+countryLeBook+" "
					+ " AND CUST.EXCISE_EXEMPT IN ("+userRcCode+")";
			StringJoiner joiner = new StringJoiner("','");
			String rcCode = "";
			RowMapper mapper = new RowMapper() {
				@Override
				public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
					joiner.add(rs.getString("CUSTOMER_ID"));
					return joiner.toString();
				}
			};
			getJdbcTemplate().query(sql,mapper);
			rcCode = "'"+joiner.toString()+"'";
			return rcCode;	
		}catch(Exception e) {
			return null;
		}
	}
	public ExceptionCode setDecimalPrecision(List<CurrencyDetailsVb> currencylst,ExceptionConfigDetailsVb excepDetVb) {
		ExceptionCode exceptionCode = new ExceptionCode();
		int unitPrecision = 2;
		CurrencyDetailsVb ccyDetVb = new CurrencyDetailsVb();
		try {
			CurrencyDetailsVb ccyVb = currencylst.stream()
			        .filter(n -> 
			        	(n.getCleb().equalsIgnoreCase(excepDetVb.getCountry()+"-"+excepDetVb.getLeBook())) 
			            && (n.getCcyConversionType().equalsIgnoreCase(excepDetVb.getPostingCcy())) )
			        .findFirst()
			        .orElse(ccyDetVb);
			unitPrecision = ccyVb.getDecimals();
			StringBuilder pattern = null;
			 if(unitPrecision == 0) {
				 pattern = new StringBuilder("#,##");
			} else {
				pattern = new StringBuilder("#,##0.");
				for (int i = 0; i < unitPrecision; i++) {
					pattern.append("0");
				}
			}
	        DecimalFormat decimalFormat = new DecimalFormat(pattern.toString());
			excepDetVb.setLekageAmountLcy(ValidationUtil.isValid(excepDetVb.getLekageAmountLcy()) ? decimalFormat.format(Double.parseDouble(excepDetVb.getLekageAmountLcy().replaceAll(",", ""))) : "");
	        excepDetVb.setLekageAmountFcy(ValidationUtil.isValid(excepDetVb.getLekageAmountFcy()) ? decimalFormat.format(Double.parseDouble(excepDetVb.getLekageAmountFcy().replaceAll(",", ""))) : "");
	        excepDetVb.setActualAmountLcy(ValidationUtil.isValid(excepDetVb.getActualAmountLcy()) ? decimalFormat.format(Double.parseDouble(excepDetVb.getActualAmountLcy().replaceAll(",", ""))) : "");
	        excepDetVb.setActualAmountFcy(ValidationUtil.isValid(excepDetVb.getActualAmountFcy()) ? decimalFormat.format(Double.parseDouble(excepDetVb.getActualAmountFcy().replaceAll(",", ""))) : "");
	        excepDetVb.setExpectedAmountLcy(ValidationUtil.isValid(excepDetVb.getExpectedAmountLcy()) ? decimalFormat.format(Double.parseDouble(excepDetVb.getExpectedAmountLcy().replaceAll(",", ""))) : "");
	        excepDetVb.setExpectedAmountFcy(ValidationUtil.isValid(excepDetVb.getExpectedAmountFcy()) ? decimalFormat.format(Double.parseDouble(excepDetVb.getExpectedAmountFcy().replaceAll(",", ""))) : "");
	        excepDetVb.setExceptionIEAmountLcy(ValidationUtil.isValid(excepDetVb.getExceptionIEAmountLcy()) ? decimalFormat.format(Double.parseDouble(excepDetVb.getExceptionIEAmountLcy().replaceAll(",", ""))) : "");
	        excepDetVb.setExceptionIEAmountFcy(ValidationUtil.isValid(excepDetVb.getExceptionIEAmountFcy()) ? decimalFormat.format(Double.parseDouble(excepDetVb.getExceptionIEAmountFcy().replaceAll(",", ""))) : "");
	        excepDetVb.setOrginalLeakageLcy(ValidationUtil.isValid(excepDetVb.getOrginalLeakageLcy()) ? decimalFormat.format(Double.parseDouble(excepDetVb.getOrginalLeakageLcy().replaceAll(",", ""))) : "");
	        excepDetVb.setOrginalLeakageFcy(ValidationUtil.isValid(excepDetVb.getOrginalLeakageFcy()) ? decimalFormat.format(Double.parseDouble(excepDetVb.getOrginalLeakageFcy().replaceAll(",", ""))) : "");
	        excepDetVb.setExceptionAmountLcy(ValidationUtil.isValid(excepDetVb.getExceptionAmountLcy()) ? decimalFormat.format(Double.parseDouble(excepDetVb.getExceptionAmountLcy().replaceAll(",", ""))) : "");
	        excepDetVb.setExceptionAmountFcy(ValidationUtil.isValid(excepDetVb.getExceptionAmountFcy()) ? decimalFormat.format(Double.parseDouble(excepDetVb.getExceptionAmountFcy().replaceAll(",", ""))) : "");
	        exceptionCode.setResponse(excepDetVb);
	        exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
		} catch(Exception e) {
			e.printStackTrace();
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			//Logger.ERROR("Error while converting precision..");
		}
		return exceptionCode;
	}
	private String checkRecordAlreadyFlagged(ExceptionConfigDetailsVb dObj) {
		String businessDate ="?";
		if (databaseType.equalsIgnoreCase("ORACLE")) {
			businessDate = "TO_DATE(?,'dd-Mon-RRRR HH24:MI:SS')";
		}
		try {
			String sql = "Select (Select user_Name from vision_Users where Vision_ID=t1.Maker) User_Name from "
					+ " RA_Exception_Details_Pend t1 where Country = ? and LE_Book = ? "
					+ " and Business_Date = "+businessDate
					+ " and Trans_Line_ID = ? and Trans_Sequence = ? and Business_Line_ID = ?";
			Object args[] = { dObj.getCountry(), dObj.getLeBook(), dObj.getBusinessDate(), dObj.getTransLineId(),
					dObj.getTransSequence(), dObj.getBusinessLineId()};
			return getJdbcTemplate().queryForObject(sql,args,String.class);
		} catch (Exception e) {
			return null;
		}
	}
	public int doDeleteCurrentBatch(ExceptionConfigDetailsVb dObj){
		String query = "Delete from RA_Exception_Details_Pend WHERE Exception_Reference = ? and Internal_Status = 1 ";
		
		Object objParams[] = new Object[1];
		objParams[0] = new String(dObj.getExceptionReference());
		return getJdbcTemplate().update(query,objParams);
	}
	public int doUpdateCurrentBatch(ExceptionConfigDetailsVb dObj){
		String query = "Update RA_Exception_Details set Internal_Status = 0 WHERE Exception_Reference = ? ";
		
		Object objParams[] = new Object[1];
		objParams[0] = new String(dObj.getExceptionReference());
		return getJdbcTemplate().update(query,objParams);
	}
	@Transactional(rollbackForClassName={"com.vision.exception.RuntimeCustomException"})
	public ExceptionCode doInsertApprRecord(List<ExceptionConfigDetailsVb> vObjects) throws RuntimeCustomException {
		ExceptionCode exceptionCode = new ExceptionCode();
		strErrorDesc  = "";
		strCurrentOperation = Constants.ADD;
		strApproveOperation =Constants.ADD;		
		setServiceDefaults();
		try {
			ArrayList<ExceptionConfigDetailsVb> alreadyExistslst = new ArrayList<>();
				for(ExceptionConfigDetailsVb vObject : vObjects){
					if(vObject.isChecked()){
						String previousMaker = checkRecordAlreadyFlagged(vObject);
						if(ValidationUtil.isValid(previousMaker)) {
							vObject.setScreenName("This record has already flagged by the user ["+previousMaker+"]");
							vObject.setActionType(childTableName);
							alreadyExistslst.add(vObject);
						}else {
							vObject.setInternalStatus(1);
							exceptionCode = doInsertApprRecordForNonTrans(vObject);
							if(exceptionCode == null || exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION ){
								if(ValidationUtil.isValid(exceptionCode.getErrorMsg()) && exceptionCode.getErrorMsg().contains("Unique")) {
									vObject.setScreenName("This record has already flagged by another user");
								}else {
									vObject.setScreenName(exceptionCode.getErrorMsg());
								}
								alreadyExistslst.add(vObject);
							}
						}
					}
				}
				if(alreadyExistslst != null && alreadyExistslst.size() > 0) {
					doDeleteCurrentBatch(vObjects.get(0));
					exceptionCode = getResultObject(Constants.VALIDATION_ERRORS_FOUND);
					exceptionCode.setRequest(alreadyExistslst);
					return exceptionCode;
				}else {
					doUpdateCurrentBatch(vObjects.get(0));
				}
				return getResultObject(Constants.SUCCESSFUL_OPERATION);
		}catch (RuntimeCustomException rcException) {
			throw rcException;
		}catch (UncategorizedSQLException uSQLEcxception) {
			strErrorDesc = parseErrorMsg(uSQLEcxception);
			exceptionCode = getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION);
			throw buildRuntimeCustomException(exceptionCode);
		}catch(Exception ex){
			//logger.error("Error in Add.",ex);
			strErrorDesc = ex.getMessage();
			exceptionCode = getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION);
			throw buildRuntimeCustomException(exceptionCode);
		}
	}
	public static String dynamicDecimalFormat(double number, int noOfDecimals) {
		BigDecimal decimalFormat = BigDecimal.valueOf(number);
		BigDecimal roundedNumber = decimalFormat.setScale(noOfDecimals, RoundingMode.HALF_UP);
		return roundedNumber.toString();
	}
}