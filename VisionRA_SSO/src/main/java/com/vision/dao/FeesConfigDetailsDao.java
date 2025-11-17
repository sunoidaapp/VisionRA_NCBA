package com.vision.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.vision.authentication.CustomContextHolder;
import com.vision.exception.ExceptionCode;
import com.vision.exception.RuntimeCustomException;
import com.vision.util.Constants;
import com.vision.util.ValidationUtil;
import com.vision.vb.CurrencyDetailsVb;
import com.vision.vb.FeesConfigDetailsVb;
import com.vision.vb.FeesConfigHeaderVb;
import com.vision.vb.ReviewResultVb;

@Component
public class FeesConfigDetailsDao extends AbstractDao<FeesConfigDetailsVb> {
	@Autowired
	CommonDao commonDao;
	@Autowired
	FeesConfigTierDao feesConfigTierDao;
	
	@Value("${app.databaseType}")
	private String databaseType;
	
	String channelTypeDescAppr = "(SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB= 7042 AND ALPHA_SUB_TAB = TAPPR.CHANNEL_TYPE) CHANNEL_TYPE_DESC, ";
	String channelTypeDescPend = "(SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB= 7042 AND ALPHA_SUB_TAB = TPEND.CHANNEL_TYPE) CHANNEL_TYPE_DESC, ";
	
	String channelCodeDescAppr = "(SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB= 7042 AND ALPHA_SUB_TAB = TAPPR.CHANNEL_CODE) CHANNEL_CODE_DESC, ";
	String channelCodeDescPend = "(SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB= 7042 AND ALPHA_SUB_TAB = TPEND.CHANNEL_CODE) CHANNEL_CODE_DESC, ";
	
	String businessVerticalDescAppr = "(SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB= 7040 AND ALPHA_SUB_TAB = TAPPR.BUSINESS_VERTICAL) BUSINESS_VERTICAL_DESC, ";
	String businessVerticalDescPend = "(SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB= 7040 AND ALPHA_SUB_TAB = TPEND.BUSINESS_VERTICAL) BUSINESS_VERTICAL_DESC, ";
	
	String productTypeDescAppr = "(SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB= 7019 AND ALPHA_SUB_TAB = TAPPR.PRODUCT_TYPE) PRODUCT_TYPE_DESC, ";
	String productTypeDescPend = "(SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB= 7019 AND ALPHA_SUB_TAB = TPEND.PRODUCT_TYPE) PRODUCT_TYPE_DESC, ";
	
	String productIdDescAppr = "(SELECT PRODUCT_DESC FROM RA_PAR_PRODUCT WHERE PRODUCT_ID = TAPPR.PRODUCT_ID AND COUNTRY = TAPPR.COUNTRY AND LE_BOOK = TAPPR.LE_BOOK) PRODUCT_ID_DESC, ";
	String productIdDescPend = "(SELECT PRODUCT_DESC FROM RA_PAR_PRODUCT WHERE PRODUCT_ID = TPEND.PRODUCT_ID AND COUNTRY = TPEND.COUNTRY AND LE_BOOK = TPEND.LE_BOOK) PRODUCT_ID_DESC, ";
	
	String tranCcyDescAppr = "(SELECT CCY_DESCRIPTION FROM CURRENCIES WHERE CURRENCY = TAPPR.TRAN_CCY) TRAN_CCY_DESC, ";
	String tranCcyDescPend = "(SELECT CCY_DESCRIPTION FROM CURRENCIES WHERE CURRENCY = TPEND.TRAN_CCY) TRAN_CCY_DESC, ";
	
	String postingCcyDescAppr = "(SELECT CCY_DESCRIPTION FROM CURRENCIES WHERE CURRENCY = TAPPR.POSTING_CCY) POSTING_CCY_DESC, ";
	String postingCcyDescPend = "(SELECT CCY_DESCRIPTION FROM CURRENCIES WHERE CURRENCY = TPEND.POSTING_CCY) POSTING_CCY_DESC, ";
	
	String ccyConversionTypeDescAppr = "(SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB= 1106 AND ALPHA_SUB_TAB = TAPPR.CCY_CONVERSION_TYPE) CCY_CONVERSION_TYPE_DESC, ";
	String ccyConversionTypeDescPend = "(SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB= 1106 AND ALPHA_SUB_TAB = TPEND.CCY_CONVERSION_TYPE) CCY_CONVERSION_TYPE_DESC, ";
	
	String feeBasisDescAppr = "(SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB= 7032 AND ALPHA_SUB_TAB = TAPPR.FEE_BASIS) FEE_BASIS_DESC, ";
	String feeBasisDescPend = "(SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB= 7032 AND ALPHA_SUB_TAB = TPEND.FEE_BASIS) FEE_BASIS_DESC, ";
	
	String lookupDescAppr = "(SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB= 7071 AND ALPHA_SUB_TAB = TAPPR.LOOKUP_AMOUNT_TYPE) LOOKUP_AMOUNT_TYPE_DESC, ";
	String lookupDescPend = "(SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB= 7071 AND ALPHA_SUB_TAB = TPEND.LOOKUP_AMOUNT_TYPE) LOOKUP_AMOUNT_TYPE_DESC, ";
	
	String percentDescAppr = "(SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB= 7071 AND ALPHA_SUB_TAB = TAPPR.PERCENT_AMOUNT_TYPE) PERCENT_AMOUNT_TYPE_DESC, ";
	String  percentDescPend = "(SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB= 7071 AND ALPHA_SUB_TAB = TPEND.PERCENT_AMOUNT_TYPE) PERCENT_AMOUNT_TYPE_DESC, ";
	
	String roundOffBasisDescAppr = "(SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB= 7072 AND ALPHA_SUB_TAB = TAPPR.ROUND_OFF_BASIS) ROUND_OFF_BASIS_DESC, ";
	String roundOffBasisDescPend = "(SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB= 7072 AND ALPHA_SUB_TAB = TPEND.ROUND_OFF_BASIS) ROUND_OFF_BASIS_DESC, ";
	
	String minMaxCcyTypeAppr = "(SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB= 7032 AND ALPHA_SUB_TAB = TAPPR.MIN_MAX_CCY_TYPE) MIN_MAX_CCY_TYPE_DESC, ";
	String minMaxCcyTypePend = "(SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB= 7032 AND ALPHA_SUB_TAB = TPEND.MIN_MAX_CCY_TYPE) MIN_MAX_CCY_TYPE_DESC, ";
	
	String customerNameDesc = "(SELECT CUST_FIRST_NAME FROM RA_CUSTOMERS WHERE COUNTRY = TPEND.COUNTRY AND LE_BOOK = TPEND.LE_BOOK AND CUSTOMER_ID = TPEND.CUSTOMER_ID) CUSTOMER_ID_DESC, ";
	String contractNameDesc = "(SELECT ACCOUNT_NAME FROM RA_ACCOUNTS WHERE COUNTRY = TPEND.COUNTRY AND LE_BOOK = TPEND.LE_BOOK AND ACCOUNT_NO = TPEND.CONTRACT_ID) CONTRACT_ID_DESC, ";
	
	String customerNameDescAppr = "(SELECT CUST_FIRST_NAME FROM RA_CUSTOMERS WHERE COUNTRY = TAPPR.COUNTRY AND LE_BOOK = TAPPR.LE_BOOK AND CUSTOMER_ID = TAPPR.CUSTOMER_ID) CUSTOMER_ID_DESC, ";
	String contractNameDescAppr = "(SELECT ACCOUNT_NAME FROM RA_ACCOUNTS WHERE COUNTRY = TAPPR.COUNTRY AND LE_BOOK = TAPPR.LE_BOOK AND ACCOUNT_NO = TAPPR.CONTRACT_ID) CONTRACT_ID_DESC, ";
	
	String pricingAppr = "(SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB= 7087 AND ALPHA_SUB_TAB = TAPPR.PRICING) PRICING_DESC, ";
	String pricingPend = "(SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB= 7087 AND ALPHA_SUB_TAB = TPEND.PRICING) PRICING_DESC, ";
	
	protected RowMapper getFeesConfigDetailRateEffectiveDateMapper(){
		RowMapper mapper = new RowMapper() {
			@Override
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				FeesConfigDetailsVb vObject = new FeesConfigDetailsVb();
				vObject.setRateEffectiveDate(rs.getString("RATE_EFFECTIVE_DATE"));
				return vObject;
				}
			};
			return mapper;
		}
	
	protected RowMapper getFeesConfigDetailMapper(List<CurrencyDetailsVb> currencylst){
		RowMapper mapper = new RowMapper() {
			@Override
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				FeesConfigDetailsVb vObject = new FeesConfigDetailsVb();
				vObject.setNewRecord(false);
				vObject.setCountry(rs.getString("COUNTRY"));
				vObject.setLeBook(rs.getString("LE_BOOK"));
				vObject.setBusinessLineId(rs.getString("BUSINESS_LINE_ID"));
				vObject.setEffectiveDate(rs.getString("EFFECTIVE_DATE"));
				vObject.setRateEffectiveDate(rs.getString("RATE_EFFECTIVE_DATE"));
				vObject.setRateEffectiveEndDate(rs.getString("RATE_EFFECTIVE_END_DATE"));
				vObject.setProductType(rs.getString("PRODUCT_TYPE"));
				vObject.setProductId(rs.getString("PRODUCT_ID"));
				vObject.setTranCcy(rs.getString("TRAN_CCY"));
				vObject.setFeeAmt(rs.getString("FEE_AMT"));
				vObject.setFeePercentage(rs.getString("FEE_PERCENTAGE"));
				vObject.setMinFee(rs.getString("MIN_FEE"));
				vObject.setMaxFee(rs.getString("MAX_FEE"));
				vObject.setPostingCcy(rs.getString("POSTING_CCY"));
				vObject.setChannelCode(rs.getString("CHANNEL_CODE"));
				vObject.setBusinessVertical(rs.getString("BUSINESS_VERTICAL"));
				vObject.setContractId(rs.getString("CONTRACT_ID"));
				if(vObject.getContractId().equalsIgnoreCase("ALL"))
					vObject.setContractName("All Accounts");
				else
					vObject.setContractName(rs.getString("CONTRACT_ID_DESC"));
				vObject.setChannelTypeDesc(rs.getString("CHANNEL_TYPE_DESC"));
				vObject.setCustomerId(rs.getString("CUSTOMER_ID"));
				if(vObject.getCustomerId().equalsIgnoreCase("ALL"))
					vObject.setCustomerName("All Customers");
				else
					vObject.setCustomerName(rs.getString("CUSTOMER_ID_DESC"));
				
				vObject.setCcyConversionType(rs.getString("CCY_CONVERSION_TYPE"));
				//if(rs.getInt("INT_BASIS") != 0)
					vObject.setInterestBasis(rs.getInt("INT_BASIS"));
				/*else
					vObject.setInterestBasis(365);*/
				vObject.setFeeBasis(rs.getString("FEE_BASIS"));
				vObject.setFeesBasisDesc(rs.getString("FEE_BASIS_DESC"));
				vObject.setFeesDetailsAttribute1(rs.getString("Fees_Details_Attribute_1"));	
				vObject.setFeesDetailsAttribute2(rs.getString("Fees_Details_Attribute_2"));	
				vObject.setFeesDetailsAttribute3(rs.getString("Fees_Details_Attribute_3"));	
				vObject.setFeesDetailsAttribute4(rs.getString("Fees_Details_Attribute_4"));	
				vObject.setFeesDetailsAttribute5(rs.getString("Fees_Details_Attribute_5"));	
				vObject.setFeesDetailsAttribute6(rs.getString("Fees_Details_Attribute_6"));
				vObject.setFeesDetailsAttribute7(rs.getString("Fees_Details_Attribute_7"));	
				vObject.setFeesDetailsAttribute8(rs.getString("Fees_Details_Attribute_8"));
				vObject.setFeesDetailsAttribute9(rs.getString("Fees_Details_Attribute_9"));
				vObject.setFeesDetailsAttribute10(rs.getString("Fees_Details_Attribute_10"));
				vObject.setFeesDetailsAttribute11(rs.getString("Fees_Details_Attribute_11"));
				vObject.setFeesDetailsAttribute12(rs.getString("Fees_Details_Attribute_12"));
				vObject.setFeesDetailsAttribute13(rs.getString("Fees_Details_Attribute_13"));
				vObject.setFeesDetailsAttribute14(rs.getString("Fees_Details_Attribute_14"));
				vObject.setFeesDetailsAttribute15(rs.getString("Fees_Details_Attribute_15"));
				vObject.setFeesDetailsAttribute16(rs.getString("Fees_Details_Attribute_16"));
				vObject.setFeesDetailsAttribute17(rs.getString("Fees_Details_Attribute_17"));
				vObject.setFeesDetailsAttribute18(rs.getString("Fees_Details_Attribute_18"));
				vObject.setFeesDetailsAttribute19(rs.getString("Fees_Details_Attribute_19"));
				vObject.setFeesDetailsAttribute20(rs.getString("Fees_Details_Attribute_20"));
				vObject.setPercentAmountType(rs.getString("PERCENT_AMOUNT_TYPE"));
				vObject.setPercentAmountTypeAt(rs.getString("PERCENT_AMOUNT_TYPE_AT"));
				vObject.setLookupAmountTypeAt(rs.getString("LOOKUP_AMOUNT_TYPE_AT"));
				vObject.setLookupAmountType(rs.getString("LOOKUP_AMOUNT_TYPE"));
				
				vObject.setChannelCodeDesc(rs.getString("CHANNEL_CODE_DESC"));
				if(!ValidationUtil.isValid(vObject.getChannelCodeDesc()))
					vObject.setChannelCodeDesc(vObject.getChannelCode());
				
				vObject.setBusinessVerticalDesc(rs.getString("BUSINESS_VERTICAL_DESC"));
				if(!ValidationUtil.isValid(vObject.getBusinessVerticalDesc()))
					vObject.setBusinessVerticalDesc(vObject.getBusinessVerticalDesc());
				
				vObject.setProductTypeDesc(rs.getString("PRODUCT_TYPE_DESC"));
				if(!ValidationUtil.isValid(vObject.getProductTypeDesc()))
					vObject.setProductTypeDesc(vObject.getProductType());
				
				vObject.setProductIdDesc(rs.getString("PRODUCT_ID_DESC"));
				if(!ValidationUtil.isValid(vObject.getProductIdDesc()))
					vObject.setProductIdDesc(vObject.getProductId());
				
				vObject.setTransCCYDesc(rs.getString("TRAN_CCY_DESC"));
				if(!ValidationUtil.isValid(vObject.getTransCCYDesc()))
					vObject.setTransCCYDesc(vObject.getTranCcy());
				
				vObject.setPostingCCYDesc(rs.getString("POSTING_CCY_DESC"));
				if(!ValidationUtil.isValid(vObject.getPostingCCYDesc()))
					vObject.setPostingCCYDesc(vObject.getPostingCcy());
				
				vObject.setCcyConversionTypeDesc(rs.getString("CCY_CONVERSION_TYPE_DESC"));
				vObject.setFeesBasisDesc(rs.getString("FEE_BASIS_DESC"));
				vObject.setLookupAmountTypeDesc(rs.getString("LOOKUP_AMOUNT_TYPE_DESC"));
				vObject.setPercentAmountTypeDesc(rs.getString("PERCENT_AMOUNT_TYPE_DESC"));
				vObject.setChannelType(rs.getString("CHANNEL_TYPE"));
				
				vObject.setChannelTypeDesc(rs.getString("CHANNEL_TYPE_DESC"));
				if(!ValidationUtil.isValid(vObject.getChannelTypeDesc()))
					vObject.setChannelTypeDesc(vObject.getChannelType());
				
				vObject.setRoundOffBasis(rs.getString("ROUND_OFF_BASIS"));
				vObject.setRoundOffBasisDesc(rs.getString("ROUND_OFF_BASIS_DESC"));
				vObject.setRoundOffBasisDecimal(rs.getInt("ROUND_OFF_DECIMAL"));
				vObject.setMinMaxCcyType(rs.getString("MIN_MAX_CCY_TYPE"));
				vObject.setMinMaxCcyTypeDesc(rs.getString("MIN_MAX_CCY_TYPE_DESC"));
				if(!ValidationUtil.isValid(vObject.getMinMaxCcyTypeDesc()))
					vObject.setMinMaxCcyTypeDesc(vObject.getMinMaxCcyType());
				vObject.setFeeDetailStatus(rs.getInt("Fee_Detail_Status"));
				
				vObject.setFeeDetailStatusDesc(rs.getString("Fee_Detail_Status_desc"));
				if(!ValidationUtil.isValid(vObject.getFeeDetailStatusDesc()))
					vObject.setFeeDetailStatusDesc(vObject.getFeeDetailStatusDesc());
				
				vObject.setRecordIndicator(rs.getInt("RECORD_INDICATOR"));
				
				vObject.setRecordIndicatorDesc(rs.getString("RECORD_INDICATOR_Desc"));
				if(!ValidationUtil.isValid(vObject.getRecordIndicatorDesc()))
					vObject.setRecordIndicatorDesc(vObject.getRecordIndicatorDesc());
				
				
				vObject.setMaker(rs.getInt("MAKER"));
				vObject.setMakerName(rs.getString("MAKER_NAME"));
				if(!ValidationUtil.isValid(vObject.getMakerName()))
					vObject.setMakerName("");
				vObject.setVerifier(rs.getInt("VERIFIER"));
				
				vObject.setVerifierName(rs.getString("VERIFIER_NAME"));
				if(!ValidationUtil.isValid(vObject.getVerifierName()))
					vObject.setVerifierName("");
				vObject.setDateLastModified(rs.getString("DATE_LAST_MODIFIED"));
				vObject.setDateCreation(rs.getString("DATE_CREATION"));
				vObject.setPricing(rs.getString("PRICING"));
				vObject.setPricingDesc(rs.getString("PRICING_DESC"));
				if(!ValidationUtil.isValid(vObject.getPricingDesc()))
					vObject.setRecordIndicatorDesc(vObject.getPricingDesc());
				
				ExceptionCode exceptionCode = commonDao.setDecimalPrecision(currencylst,vObject,null,"FEE_DET");
				if(exceptionCode.getErrorCode() == Constants.SUCCESSFUL_OPERATION)
					vObject= (FeesConfigDetailsVb)exceptionCode.getResponse();
				return vObject;
			}
		};
		return mapper;
	}
	
	@Override
	protected void setServiceDefaults() {
		serviceName = "FeesConfigDetails";
		serviceDesc = "Fees Config Details";
		tableName = "RA_MST_FEES_DETAIL";
		childTableName = "RA_MST_FEES_DETAIL";
		intCurrentUserId = CustomContextHolder.getContext().getVisionId();
	}
	public List<FeesConfigDetailsVb> getQueryDetails(FeesConfigHeaderVb dObj,int intStatus){
		List<CurrencyDetailsVb> currencyPrecisionlst = commonDao.getCurrencyDecimals(false);
		List<FeesConfigDetailsVb> collTemp = null;
		String query = "";
		String maxEffectiveRateQuery = "";
		try
		{	
			String fetchTableName ="";
			//if(!dObj.isVerificationRequired() || dObj.isReview()){intStatus =0;}
			if(intStatus == Constants.STATUS_ZERO) {
				fetchTableName ="RA_MST_FEES_DETAIL";
			}else {
				fetchTableName ="RA_MST_FEES_DETAIL_PEND";
			}
			maxEffectiveRateQuery = "SELECT SPEND.COUNTRY,SPEND.LE_BOOK,SPEND.BUSINESS_LINE_ID, \n" + 
				"	SPEND.EFFECTIVE_DATE," +
				"	MAX(SPEND.RATE_EFFECTIVE_DATE) AS RATE_EFFECTIVE_DATE, " + 
				"	SPEND.RATE_EFFECTIVE_END_DATE , " + 
				"	SPEND.PRODUCT_TYPE,SPEND.PRODUCT_ID,SPend.Tran_Ccy, " + 
				"	SPEND.CHANNEL_CODE,SPEND.BUSINESS_VERTICAL,SPEND.CONTRACT_ID,SPEND.CUSTOMER_ID," + 
				"	SPEND.Fees_Details_Attribute_1,SPEND.Fees_Details_Attribute_2," + 
				"	SPEND.Fees_Details_Attribute_3,SPEND.Fees_Details_Attribute_4,SPEND.Fees_Details_Attribute_5,"
				+ "SPEND.Fees_Details_Attribute_6," + 
				"	SPEND.Fees_Details_Attribute_7,SPEND.Fees_Details_Attribute_8," + 
				"	SPEND.Fees_Details_Attribute_9,SPEND.Fees_Details_Attribute_10,SPEND.Fees_Details_Attribute_11,"+
				"	SPEND.Fees_Details_Attribute_12,SPEND.Fees_Details_Attribute_13,SPEND.Fees_Details_Attribute_14,"+
				"	SPEND.Fees_Details_Attribute_15,SPEND.Fees_Details_Attribute_16,SPEND.Fees_Details_Attribute_17,"+
				"	SPEND.Fees_Details_Attribute_18,SPEND.Fees_Details_Attribute_19,SPEND.Fees_Details_Attribute_20,"+
				"	SPEND.CHANNEL_TYPE ,SPEND.PRICING " + 
				"   FROM "+fetchTableName+" SPEND WHERE"+
				"     SPEND.Country = ? and SPEND.LE_Book = ? and  SPEND.Business_Line_ID =? \n" +
				"   And "+dbFunctionFormats("SPEND.EFFECTIVE_DATE","DATETIME_FORMAT",null)+" = ? "+
				"   GROUP BY SPEND.COUNTRY,SPEND.LE_BOOK,SPEND.BUSINESS_LINE_ID," + 
				"	SPEND.EFFECTIVE_DATE," + 
				"	SPEND.PRODUCT_TYPE,SPEND.PRODUCT_ID,SPend.Tran_Ccy," + 
				"	SPEND.CHANNEL_CODE,SPEND.BUSINESS_VERTICAL,SPEND.CONTRACT_ID,SPEND.CUSTOMER_ID," + 
				"	SPEND.Fees_Details_Attribute_1,SPEND.Fees_Details_Attribute_2," + 
				"	SPEND.Fees_Details_Attribute_3,SPEND.Fees_Details_Attribute_4,SPEND.Fees_Details_Attribute_5,SPEND.Fees_Details_Attribute_6," + 
				"	SPEND.Fees_Details_Attribute_7,SPEND.Fees_Details_Attribute_8,SPEND.Fees_Details_Attribute_9,SPEND.Fees_Details_Attribute_10,"+
				"	SPEND.Fees_Details_Attribute_11,SPEND.Fees_Details_Attribute_12,SPEND.Fees_Details_Attribute_13,SPEND.Fees_Details_Attribute_14,"+
				"	SPEND.Fees_Details_Attribute_15,SPEND.Fees_Details_Attribute_16,SPEND.Fees_Details_Attribute_17,SPEND.Fees_Details_Attribute_18,"+
				"	SPEND.Fees_Details_Attribute_19,SPEND.Fees_Details_Attribute_20,"+
				"   SPEND.CHANNEL_TYPE,SPEND.PRICING ,SPEND.RATE_EFFECTIVE_DATE,SPEND.RATE_EFFECTIVE_END_DATE "; 
			 
				query = " SELECT DISTINCT TPEND.COUNTRY,TPEND.LE_BOOK,TPEND.BUSINESS_LINE_ID,  "+
						" "+dbFunctionFormats("TPend.EFFECTIVE_DATE","DATETIME_FORMAT",null)+" EFFECTIVE_DATE,"+
						" "+dbFunctionFormats("TPend.RATE_EFFECTIVE_DATE","DATETIME_FORMAT",null)+" RATE_EFFECTIVE_DATE,"+
						" "+dbFunctionFormats("TPend.RATE_EFFECTIVE_END_DATE","DATETIME_FORMAT",null)+" RATE_EFFECTIVE_END_DATE,"
						+ "TPEND.PRODUCT_TYPE,TPEND.PRODUCT_ID,TPEND.TRAN_CCY, TPEND.POSTING_CCY,         "
						+ "TPend.FEE_AMT,"
						+ ""+dbFunctionFormats("TPend.FEE_PERCENTAGE","NUM_FORMAT","5")+" FEE_PERCENTAGE,"
						+ "TPend.MIN_FEE,"
						+ "TPend.MAX_FEE,"
						+ " TPEND.CHANNEL_CODE, TPEND.BUSINESS_VERTICAL, TPEND.CONTRACT_ID, TPEND.CUSTOMER_ID, TPEND.POSTING_CCY, TPEND.CCY_CONVERSION_TYPE, "
						+ ""+channelCodeDescPend+productTypeDescPend+productIdDescPend+tranCcyDescPend+postingCcyDescPend+businessVerticalDescPend+customerNameDesc+contractNameDesc+"" 
						+ ""+ccyConversionTypeDescPend+feeBasisDescPend+lookupDescPend+percentDescPend+channelTypeDescPend+roundOffBasisDescPend+minMaxCcyTypePend+""
						+ " TPEND.INT_BASIS, TPEND.FEE_BASIS , TPEND.Fees_Details_Attribute_1, TPEND.Fees_Details_Attribute_2, TPEND.Fees_Details_Attribute_3,"
						+ "TPEND.Fees_Details_Attribute_4,TPEND.Fees_Details_Attribute_5,TPEND.Fees_Details_Attribute_6," 
						+ " TPEND.Fees_Details_Attribute_7,TPEND.Fees_Details_Attribute_8,"
						+ "TPEND.Fees_Details_Attribute_9,TPEND.Fees_Details_Attribute_10,TPEND.Fees_Details_Attribute_11,"
						+ "TPEND.Fees_Details_Attribute_12,TPEND.Fees_Details_Attribute_13,TPEND.Fees_Details_Attribute_14,"
						+ "TPEND.Fees_Details_Attribute_15,TPEND.Fees_Details_Attribute_16,TPEND.Fees_Details_Attribute_17,"
						+ "TPEND.Fees_Details_Attribute_18,TPEND.Fees_Details_Attribute_19,TPEND.Fees_Details_Attribute_20,"
						+ " TPEND.PERCENT_AMOUNT_TYPE ,"
						+ " TPEND.PERCENT_AMOUNT_TYPE_AT, TPEND.LOOKUP_AMOUNT_TYPE, TPEND.LOOKUP_AMOUNT_TYPE_AT,TPEND.CHANNEL_TYPE,"
						+ "TPEND.ROUND_OFF_BASIS,TPEND.ROUND_OFF_DECIMAL,TPEND.MIN_MAX_CCY_TYPE,"+
						" TPend.Fee_Detail_Status,"
						+"  (SELECT NUM_SUBTAB_DESCRIPTION FROM NUM_SUB_TAB WHERE NUM_TAB= 1 "
						+ "AND NUM_SUB_TAB = TPend.Fee_Detail_Status) Fee_Detail_Status_DESC," 
						+"  (SELECT NUM_SUBTAB_DESCRIPTION FROM NUM_SUB_TAB WHERE NUM_TAB= 7 "
						+ "AND NUM_SUB_TAB = TPend.RECORD_INDICATOR) RECORD_INDICATOR_DESC,"
						+ "TPend.RECORD_INDICATOR,                                                                                          "+
						" TPend. MAKER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "+getDbFunction("NVL")+"(TPend.MAKER,0) ) MAKER_NAME,                                  "+
						" TPend.VERIFIER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "+getDbFunction("NVL")+"(TPend.VERIFIER,0) ) VERIFIER_NAME,                          "+
						" "+getDbFunction("DATEFUNC")+"(TPend.DATE_LAST_MODIFIED, 'dd-MM-yyyy "+getDbFunction("TIME")+"') DATE_LAST_MODIFIED ,"
						+ ""+getDbFunction("DATEFUNC")+"(TPend.DATE_CREATION, 'dd-MM-yyyy "+getDbFunction("TIME")+"') DATE_CREATION ,"+pricingPend+"TPend.PRICING "+                                                                                             
						" FROM "+fetchTableName+" TPEND  Inner Join ("+maxEffectiveRateQuery+") S1 ON "+
						 " TPEND.Business_Line_ID = S1.Business_Line_ID " + 
						" AND TPEND.COUNTRY = S1.COUNTRY AND TPEND.LE_BOOK = S1.LE_BOOK " + 
						" AND TPEND.EFFECTIVE_DATE = S1.EFFECTIVE_DATE "+
						" AND TPEND.RATE_EFFECTIVE_DATE = S1.RATE_EFFECTIVE_DATE "+
						" AND S1.PRODUCT_TYPE =TPEND.PRODUCT_TYPE                          "+
						" AND S1.PRODUCT_ID =TPEND.PRODUCT_ID                              "+
						" AND S1.CHANNEL_CODE =TPEND.CHANNEL_CODE                          "+
						" AND S1.BUSINESS_VERTICAL =TPEND.BUSINESS_VERTICAL                "+
						" AND S1.CONTRACT_ID =TPEND.CONTRACT_ID                            "+
						" AND S1.CUSTOMER_ID =TPEND.CUSTOMER_ID                            "+
						" AND S1.TRAN_CCY =TPEND.TRAN_CCY                            "+
						" AND S1.Fees_Details_Attribute_1 =TPEND.Fees_Details_Attribute_1  "+
						" AND S1.Fees_Details_Attribute_2 =TPEND.Fees_Details_Attribute_2  "+
						" AND S1.Fees_Details_Attribute_3 =TPEND.Fees_Details_Attribute_3  "+
						" AND S1.Fees_Details_Attribute_4 =TPEND.Fees_Details_Attribute_4  "+
						" AND S1.Fees_Details_Attribute_5 =TPEND.Fees_Details_Attribute_5  "+
						" AND S1.Fees_Details_Attribute_6 =TPEND.Fees_Details_Attribute_6  "+
						" AND S1.Fees_Details_Attribute_7 =TPEND.Fees_Details_Attribute_7  "+
						" AND S1.Fees_Details_Attribute_8 =TPEND.Fees_Details_Attribute_8  "+
						" AND S1.Fees_Details_Attribute_9 =TPEND.Fees_Details_Attribute_9  "+
						" AND S1.Fees_Details_Attribute_10 =TPEND.Fees_Details_Attribute_10  "+
						" AND S1.Fees_Details_Attribute_11 =TPEND.Fees_Details_Attribute_11  "+
						" AND S1.Fees_Details_Attribute_12 =TPEND.Fees_Details_Attribute_12  "+
						" AND S1.Fees_Details_Attribute_13 =TPEND.Fees_Details_Attribute_13  "+
						" AND S1.Fees_Details_Attribute_14 =TPEND.Fees_Details_Attribute_14  "+
						" AND S1.Fees_Details_Attribute_15 =TPEND.Fees_Details_Attribute_15  "+
						" AND S1.Fees_Details_Attribute_16 =TPEND.Fees_Details_Attribute_16  "+
						" AND S1.Fees_Details_Attribute_17 =TPEND.Fees_Details_Attribute_17  "+
						" AND S1.Fees_Details_Attribute_18 =TPEND.Fees_Details_Attribute_18  "+
						" AND S1.Fees_Details_Attribute_19 =TPEND.Fees_Details_Attribute_19  "+
						" AND S1.Fees_Details_Attribute_20 =TPEND.Fees_Details_Attribute_20  "+
						" AND S1.CHANNEL_TYPE=TPEND.CHANNEL_TYPE                          "
						+ "AND S1.PRICING=TPEND.PRICING ";
		
		
			Object objParams[] = new Object[4];
			objParams[0] = new String(dObj.getCountry());// country
			objParams[1] = new String(dObj.getLeBook());
			objParams[2] = new String(dObj.getBusinessLineId());
			objParams[3] = new String(dObj.getEffectiveDate());
			collTemp = getJdbcTemplate().query(query,objParams,getFeesConfigDetailMapper(currencyPrecisionlst));
			return collTemp;
		}catch(Exception ex){
			ex.printStackTrace();
			logger.error("Exception ");
			return null;
		}
	}
	protected int doInsertionApprFeesDetails(FeesConfigDetailsVb vObject){
		try {
			String effectiveDate ="?";
			String rateEffectiveDate ="?";
			String rateEffectiveEndDate ="?";
			String dateCreation ="";
			if (databaseType.equalsIgnoreCase("ORACLE")) {
				effectiveDate = "TO_DATE(?,'DD-MM-RRRR HH24:MI:SS')";
				rateEffectiveDate="TO_DATE(?,'DD-MM-RRRR HH24:MI:SS')";
				rateEffectiveEndDate="TO_DATE(?,'DD-MM-RRRR HH24:MI:SS')";
				 dateCreation = "To_Date(?, 'DD-MM-RRRR HH24:MI:SS')";
			}else {
				 dateCreation = "CONVERT(datetime, ?, 103)";
			}
				
		String query =  " Insert Into RA_MST_FEES_DETAIL(COUNTRY,LE_BOOK,BUSINESS_LINE_ID," + 
				" EFFECTIVE_DATE, RATE_EFFECTIVE_DATE, RATE_EFFECTIVE_END_DATE,PRODUCT_TYPE, PRODUCT_ID, TRAN_CCY, FEE_AMT,"+
				"FEE_PERCENTAGE, MIN_FEE, MAX_FEE,"
				+ "CHANNEL_CODE, BUSINESS_VERTICAL, CONTRACT_ID, CUSTOMER_ID,  POSTING_CCY, CCY_CONVERSION_TYPE,"
				+ " INT_BASIS, FEE_BASIS,  Fees_Details_Attribute_1, Fees_Details_Attribute_2, Fees_Details_Attribute_3,"
				+ "Fees_Details_Attribute_4,Fees_Details_Attribute_5,Fees_Details_Attribute_6, "
				+ " Fees_Details_Attribute_7,Fees_Details_Attribute_8,Fees_Details_Attribute_9,Fees_Details_Attribute_10,"
				+ " Fees_Details_Attribute_11,Fees_Details_Attribute_12,Fees_Details_Attribute_13,Fees_Details_Attribute_14,"
				+ " Fees_Details_Attribute_15,Fees_Details_Attribute_16,Fees_Details_Attribute_17,Fees_Details_Attribute_18,"
				+ " Fees_Details_Attribute_19,Fees_Details_Attribute_20,PERCENT_AMOUNT_TYPE ,"
				+ " PERCENT_AMOUNT_TYPE_AT, LOOKUP_AMOUNT_TYPE, LOOKUP_AMOUNT_TYPE_AT,CHANNEL_TYPE,"
				+ "ROUND_OFF_BASIS_AT,ROUND_OFF_BASIS,ROUND_OFF_DECIMAL,MIN_MAX_CCY_TYPE,"
				+ " FEE_DETAIL_STATUS_NT, FEE_DETAIL_STATUS," + 
				" RECORD_INDICATOR_NT,RECORD_INDICATOR,MAKER,VERIFIER,DATE_LAST_MODIFIED,DATE_CREATION,PRICING ) "
					+ " Values (?,?,?,"+effectiveDate+","+rateEffectiveDate+","+rateEffectiveEndDate+",?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?"
				+ ","+getDbFunction("SYSDATE")+","+dateCreation+" ,?)";
		
		Object[] args = { vObject.getCountry(), vObject.getLeBook(), vObject.getBusinessLineId(),
				vObject.getEffectiveDate(), vObject.getRateEffectiveDate(),vObject.getRateEffectiveEndDate(), vObject.getProductType(), vObject.getProductId(), vObject.getTranCcy(),
				vObject.getFeeAmt().replaceAll(",", ""), vObject.getFeePercentage().replaceAll(",", ""),
				vObject.getMinFee().replaceAll(",", ""), vObject.getMaxFee().replaceAll(",", ""),
				vObject.getChannelCode(), vObject.getBusinessVertical(), vObject.getContractId(),
				vObject.getCustomerId(), vObject.getPostingCcy(), vObject.getCcyConversionType(),
				vObject.getInterestBasis(), vObject.getFeeBasis(), vObject.getFeesDetailsAttribute1(),
				vObject.getFeesDetailsAttribute2(), vObject.getFeesDetailsAttribute3(),vObject.getFeesDetailsAttribute4(),
				vObject.getFeesDetailsAttribute5(),vObject.getFeesDetailsAttribute6(),vObject.getFeesDetailsAttribute7(),vObject.getFeesDetailsAttribute8(),
				vObject.getFeesDetailsAttribute9(),vObject.getFeesDetailsAttribute10(),vObject.getFeesDetailsAttribute11(),vObject.getFeesDetailsAttribute12(),
				vObject.getFeesDetailsAttribute13(),vObject.getFeesDetailsAttribute14(),vObject.getFeesDetailsAttribute15(),vObject.getFeesDetailsAttribute16(),
				vObject.getFeesDetailsAttribute17(),vObject.getFeesDetailsAttribute18(),vObject.getFeesDetailsAttribute19(),vObject.getFeesDetailsAttribute20(),
				vObject.getPercentAmountType(),
				vObject.getPercentAmountTypeAt(), vObject.getLookupAmountType(), vObject.getLookupAmountTypeAt(),
				vObject.getChannelType(),vObject.getRoundOffBasisAt(),vObject.getRoundOffBasis(),
				vObject.getRoundOffBasisDecimal(),vObject.getMinMaxCcyType(),vObject.getFeeDetailStatusNt(),vObject.getFeeDetailStatus(),
				vObject.getRecordIndicatorNt(),vObject.getRecordIndicator(),vObject.getMaker(),vObject.getVerifier(),vObject.getDateCreation(),vObject.getPricing()};
		return getJdbcTemplate().update(query,args);
		}catch(Exception e) {
			e.printStackTrace();
			return 0;
		}
	}
	protected int doInsertionPendFeesDetails(FeesConfigDetailsVb vObject){
		String effectiveDate ="?";
		String rateEffectiveDate ="?";
		String rateEffectiveEndDate ="?";
		if (databaseType.equalsIgnoreCase("ORACLE")) {
			effectiveDate = "TO_DATE(?,'DD-MM-RRRR HH24:MI:SS')";
			rateEffectiveDate="TO_DATE(?,'DD-MM-RRRR HH24:MI:SS')";
			rateEffectiveEndDate="TO_DATE(?,'DD-MM-RRRR HH24:MI:SS')";
		}
		try {
			String query =  " Insert Into RA_MST_FEES_DETAIL_PEND(COUNTRY,LE_BOOK,BUSINESS_LINE_ID," + 
					" EFFECTIVE_DATE, RATE_EFFECTIVE_DATE,RATE_EFFECTIVE_END_DATE, PRODUCT_TYPE, PRODUCT_ID, TRAN_CCY, FEE_AMT,"+
					"FEE_PERCENTAGE, MIN_FEE, MAX_FEE,"
					+ "CHANNEL_CODE, BUSINESS_VERTICAL, CONTRACT_ID, CUSTOMER_ID,  POSTING_CCY, CCY_CONVERSION_TYPE,"
					+ " INT_BASIS, FEE_BASIS,  Fees_Details_Attribute_1, Fees_Details_Attribute_2, Fees_Details_Attribute_3,"
					+ "Fees_Details_Attribute_4,Fees_Details_Attribute_5,Fees_Details_Attribute_6, "
					+ "Fees_Details_Attribute_7,Fees_Details_Attribute_8,Fees_Details_Attribute_9, "
					+ "Fees_Details_Attribute_10,Fees_Details_Attribute_11,Fees_Details_Attribute_12, "
					+ "Fees_Details_Attribute_13,Fees_Details_Attribute_14,Fees_Details_Attribute_15, "
					+ "Fees_Details_Attribute_16,Fees_Details_Attribute_17,Fees_Details_Attribute_18, "
					+ " Fees_Details_Attribute_19,Fees_Details_Attribute_20,PERCENT_AMOUNT_TYPE ,"
					+ " PERCENT_AMOUNT_TYPE_AT, LOOKUP_AMOUNT_TYPE, LOOKUP_AMOUNT_TYPE_AT,CHANNEL_TYPE,"
					+ "ROUND_OFF_BASIS_AT,ROUND_OFF_BASIS,ROUND_OFF_DECIMAL,MIN_MAX_CCY_TYPE,"
					+ " FEE_DETAIL_STATUS_NT, FEE_DETAIL_STATUS," + 
					" RECORD_INDICATOR_NT,RECORD_INDICATOR,MAKER,VERIFIER,DATE_LAST_MODIFIED,DATE_CREATION,PRICING ) "
					+ " Values (?,?,?,"+effectiveDate+","+rateEffectiveDate+","+rateEffectiveEndDate+",?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,"
					+ ""+getDbFunction("SYSDATE")+","+getDbFunction("SYSDATE")+",?) ";
			
			Object[] args = { vObject.getCountry(), vObject.getLeBook(), vObject.getBusinessLineId(),
					vObject.getEffectiveDate(), vObject.getRateEffectiveDate(),vObject.getRateEffectiveEndDate(), vObject.getProductType(), vObject.getProductId(), vObject.getTranCcy(),
					vObject.getFeeAmt().replaceAll(",", ""), vObject.getFeePercentage().replaceAll(",", ""),
					vObject.getMinFee().replaceAll(",", ""), vObject.getMaxFee().replaceAll(",", ""),
					vObject.getChannelCode(), vObject.getBusinessVertical(), vObject.getContractId(),
					vObject.getCustomerId(), vObject.getPostingCcy(), vObject.getCcyConversionType(),
					vObject.getInterestBasis(), vObject.getFeeBasis(), vObject.getFeesDetailsAttribute1(),
					vObject.getFeesDetailsAttribute2(), vObject.getFeesDetailsAttribute3(),vObject.getFeesDetailsAttribute4(),
					vObject.getFeesDetailsAttribute5(),vObject.getFeesDetailsAttribute6(),vObject.getFeesDetailsAttribute7(),vObject.getFeesDetailsAttribute8(),
					vObject.getFeesDetailsAttribute9(),vObject.getFeesDetailsAttribute10(),vObject.getFeesDetailsAttribute11(),vObject.getFeesDetailsAttribute12(),
					vObject.getFeesDetailsAttribute13(),vObject.getFeesDetailsAttribute14(),vObject.getFeesDetailsAttribute15(),vObject.getFeesDetailsAttribute16(),
					vObject.getFeesDetailsAttribute17(),vObject.getFeesDetailsAttribute18(),vObject.getFeesDetailsAttribute19(),vObject.getFeesDetailsAttribute20(),
					vObject.getPercentAmountType(),
					vObject.getPercentAmountTypeAt(), vObject.getLookupAmountType(), vObject.getLookupAmountTypeAt(),
					vObject.getChannelType(),vObject.getRoundOffBasisAt(),vObject.getRoundOffBasis(),
					vObject.getRoundOffBasisDecimal(),vObject.getMinMaxCcyType(),vObject.getFeeDetailStatusNt(),vObject.getFeeDetailStatus(),
					vObject.getRecordIndicatorNt(),vObject.getRecordIndicator(),vObject.getMaker(),vObject.getVerifier(),vObject.getPricing()};
			return getJdbcTemplate().update(query,args);
		} catch(Exception ex) {
				ex.printStackTrace();
				return Constants.ERRONEOUS_OPERATION;
				
			}
	}
	
	protected int doInsertionFeesDetailsHis(FeesConfigDetailsVb vObject){
		String effectiveDate ="?";
		String rateEffectiveDate ="?";
		String rateEffectiveEndDate = "?";
		if (databaseType.equalsIgnoreCase("ORACLE")) {
			effectiveDate = "TO_DATE(?,'DD-MM-RRRR HH24:MI:SS')";
			rateEffectiveDate="TO_DATE(?,'DD-MM-RRRR HH24:MI:SS')";
			rateEffectiveEndDate="TO_DATE(?,'DD-MM-RRRR HH24:MI:SS')";
		}
		String query =  " Insert Into RA_MST_FEES_DETAIL_HIS(REF_NO,HISTORY_CREATION_DATE,COUNTRY,LE_BOOK,BUSINESS_LINE_ID," + 
				" EFFECTIVE_DATE, RATE_EFFECTIVE_DATE, RATE_EFFECTIVE_END_DATE,PRODUCT_TYPE, PRODUCT_ID, TRAN_CCY, FEE_AMT,"+
				"FEE_PERCENTAGE, MIN_FEE, MAX_FEE,"
				+ "CHANNEL_CODE, BUSINESS_VERTICAL, CONTRACT_ID, CUSTOMER_ID,  POSTING_CCY, CCY_CONVERSION_TYPE,"
				+ " INT_BASIS, FEE_BASIS,  Fees_Details_Attribute_1, Fees_Details_Attribute_2, Fees_Details_Attribute_3,"
				+ "Fees_Details_Attribute_4,Fees_Details_Attribute_5,Fees_Details_Attribute_6, "
				+ " Fees_Details_Attribute_7,Fees_Details_Attribute_8,Fees_Details_Attribute_9,"
				+ " Fees_Details_Attribute_10,Fees_Details_Attribute_11,Fees_Details_Attribute_12,"
				+ " Fees_Details_Attribute_13,Fees_Details_Attribute_14,Fees_Details_Attribute_15,"
				+ " Fees_Details_Attribute_16,Fees_Details_Attribute_17,Fees_Details_Attribute_18,"
				+ " Fees_Details_Attribute_19,Fees_Details_Attribute_20,PERCENT_AMOUNT_TYPE ,"
				+ " PERCENT_AMOUNT_TYPE_AT, LOOKUP_AMOUNT_TYPE, LOOKUP_AMOUNT_TYPE_AT,CHANNEL_TYPE,"
				+ "ROUND_OFF_BASIS_AT,ROUND_OFF_BASIS,ROUND_OFF_DECIMAL,MIN_MAX_CCY_TYPE,"
				+ " FEE_DETAIL_STATUS_NT, FEE_DETAIL_STATUS," + 
				" RECORD_INDICATOR_NT,RECORD_INDICATOR,MAKER,VERIFIER,DATE_LAST_MODIFIED,DATE_CREATION,PRICING) "
				+ " Values (?,"+getDbFunction("SYSDATE")+",?,?,?,"+effectiveDate+","+rateEffectiveDate+","+rateEffectiveEndDate+",?,?,?,?,?,?,?,?,?,?,?,"
				+ "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ";
		
		Object[] args = {vObject.getRefNo(),vObject.getCountry(), vObject.getLeBook(), vObject.getBusinessLineId(),
				vObject.getEffectiveDate(), vObject.getRateEffectiveDate(),vObject.getRateEffectiveEndDate(), vObject.getProductType(), vObject.getProductId(), vObject.getTranCcy(),
				vObject.getFeeAmt().replaceAll(",", ""), vObject.getFeePercentage().replaceAll(",", ""),
				vObject.getMinFee().replaceAll(",", ""), vObject.getMaxFee().replaceAll(",", ""),
				vObject.getChannelCode(), vObject.getBusinessVertical(), vObject.getContractId(),
				vObject.getCustomerId(), vObject.getPostingCcy(), vObject.getCcyConversionType(),
				vObject.getInterestBasis(), vObject.getFeeBasis(), vObject.getFeesDetailsAttribute1(),
				vObject.getFeesDetailsAttribute2(), vObject.getFeesDetailsAttribute3(),vObject.getFeesDetailsAttribute4(),
				vObject.getFeesDetailsAttribute5(),vObject.getFeesDetailsAttribute6(),vObject.getFeesDetailsAttribute7(),vObject.getFeesDetailsAttribute8(),
				vObject.getFeesDetailsAttribute9(),vObject.getFeesDetailsAttribute10(),vObject.getFeesDetailsAttribute11(),vObject.getFeesDetailsAttribute12(),
				vObject.getFeesDetailsAttribute13(),vObject.getFeesDetailsAttribute14(),vObject.getFeesDetailsAttribute15(),vObject.getFeesDetailsAttribute16(),
				vObject.getFeesDetailsAttribute17(),vObject.getFeesDetailsAttribute18(),vObject.getFeesDetailsAttribute19(),vObject.getFeesDetailsAttribute20(),
				vObject.getPercentAmountType(),
				vObject.getPercentAmountTypeAt(), vObject.getLookupAmountType(), vObject.getLookupAmountTypeAt(),
				vObject.getChannelType(),vObject.getRoundOffBasisAt(),vObject.getRoundOffBasis(),
				vObject.getRoundOffBasisDecimal(),vObject.getMinMaxCcyType(),vObject.getFeeDetailStatusNt(),vObject.getFeeDetailStatus(),
				vObject.getRecordIndicatorNt(),vObject.getRecordIndicator(),vObject.getMaker(),vObject.getVerifier(),
				vObject.getDateLastModified(),vObject.getDateCreation(),vObject.getPricing()};
		return getJdbcTemplate().update(query,args);
	}
	protected int deleteFeesDetailsAppr(FeesConfigDetailsVb vObject){
		String query = "Delete from RA_MST_FEES_DETAIL WHERE COUNTRY= ? AND LE_BOOK= ? AND BUSINESS_LINE_ID = ? "
				+ " AND "+dbFunctionFormats("EFFECTIVE_DATE","DATETIME_FORMAT",null)+"= ? "
				+ " AND "+dbFunctionFormats("RATE_EFFECTIVE_DATE","DATETIME_FORMAT",null)+" =?"
				+ " AND CHANNEL_CODE= ? AND  BUSINESS_VERTICAL= ? AND  CONTRACT_ID= ? AND  CUSTOMER_ID= ? AND "
				+ " PRODUCT_TYPE= ? AND   PRODUCT_ID= ? AND   TRAN_CCY= ? AND CHANNEL_TYPE=? AND "
				+ " Fees_Details_Attribute_1= ? AND  Fees_Details_Attribute_2= ? AND  Fees_Details_Attribute_3= ? AND "
				+ " Fees_Details_Attribute_4= ? AND  Fees_Details_Attribute_5= ? AND "
				+ " Fees_Details_Attribute_6= ? AND Fees_Details_Attribute_7= ? AND Fees_Details_Attribute_8= ? AND "
				+ " Fees_Details_Attribute_9= ? AND Fees_Details_Attribute_10= ? AND Fees_Details_Attribute_11= ? AND "
				+ " Fees_Details_Attribute_12= ? AND Fees_Details_Attribute_13= ? AND Fees_Details_Attribute_14= ? AND "
				+ " Fees_Details_Attribute_15= ? AND Fees_Details_Attribute_16= ? AND Fees_Details_Attribute_17= ? AND "
				+ " Fees_Details_Attribute_18= ? AND Fees_Details_Attribute_19= ? AND Fees_Details_Attribute_20= ? ";
		Object[] args = {vObject.getCountry(),vObject.getLeBook(),vObject.getBusinessLineId(),
				vObject.getEffectiveDate(),vObject.getRateEffectiveDate(),vObject.getChannelCode(), 
				vObject.getBusinessVertical(), vObject.getContractId(),
				vObject.getCustomerId(),vObject.getProductType(), vObject.getProductId(), vObject.getTranCcy(),
				vObject.getChannelType(),vObject.getFeesDetailsAttribute1(),
				vObject.getFeesDetailsAttribute2(), vObject.getFeesDetailsAttribute3(),vObject.getFeesDetailsAttribute4(),
				vObject.getFeesDetailsAttribute5(),vObject.getFeesDetailsAttribute6(),vObject.getFeesDetailsAttribute7(),
				vObject.getFeesDetailsAttribute8(),vObject.getFeesDetailsAttribute9(),vObject.getFeesDetailsAttribute10(),
				vObject.getFeesDetailsAttribute11(),vObject.getFeesDetailsAttribute12(),vObject.getFeesDetailsAttribute13(),
				vObject.getFeesDetailsAttribute14(),vObject.getFeesDetailsAttribute15(),vObject.getFeesDetailsAttribute16(),
				vObject.getFeesDetailsAttribute17(),vObject.getFeesDetailsAttribute18(),vObject.getFeesDetailsAttribute19(),
				vObject.getFeesDetailsAttribute20()};
		return getJdbcTemplate().update(query,args);
		
	}
	protected int deleteFeesDetailsPend(FeesConfigDetailsVb vObject){
		String query = "Delete from RA_MST_FEES_DETAIL_PEND WHERE COUNTRY= ? AND LE_BOOK= ? AND BUSINESS_LINE_ID = ?"
				+ " AND "+dbFunctionFormats("EFFECTIVE_DATE","DATETIME_FORMAT",null)+"= ? "
				+ " AND "+dbFunctionFormats("RATE_EFFECTIVE_DATE","DATETIME_FORMAT",null)+" =?"
				+ " AND CHANNEL_CODE= ? AND  BUSINESS_VERTICAL= ? AND  CONTRACT_ID= ? AND  CUSTOMER_ID= ? AND "
				+ " PRODUCT_TYPE= ? AND   PRODUCT_ID= ? AND   TRAN_CCY= ? AND CHANNEL_TYPE=? AND "
				+ " Fees_Details_Attribute_1= ? AND  Fees_Details_Attribute_2= ? AND  Fees_Details_Attribute_3= ? AND "
				+ " Fees_Details_Attribute_4= ? AND  Fees_Details_Attribute_5= ? AND "
				+ " Fees_Details_Attribute_6= ? AND Fees_Details_Attribute_7= ? AND Fees_Details_Attribute_8= ? AND "
				+ " Fees_Details_Attribute_9= ? AND Fees_Details_Attribute_10= ? AND Fees_Details_Attribute_11= ? AND "
				+ " Fees_Details_Attribute_12= ? AND Fees_Details_Attribute_13= ? AND Fees_Details_Attribute_14= ? AND "
				+ " Fees_Details_Attribute_15= ? AND Fees_Details_Attribute_16= ? AND Fees_Details_Attribute_17= ? AND "
				+ " Fees_Details_Attribute_18= ? AND Fees_Details_Attribute_19= ? AND "
				+ " Fees_Details_Attribute_20= ? ";
		Object[] args = {vObject.getCountry(),vObject.getLeBook(),vObject.getBusinessLineId(),
				vObject.getEffectiveDate(),vObject.getRateEffectiveDate(),vObject.getChannelCode(), vObject.getBusinessVertical(), vObject.getContractId(),
				vObject.getCustomerId(),vObject.getProductType(), vObject.getProductId(), vObject.getTranCcy(),
				vObject.getChannelType(),vObject.getFeesDetailsAttribute1(),
				vObject.getFeesDetailsAttribute2(), vObject.getFeesDetailsAttribute3(),vObject.getFeesDetailsAttribute4(),
				vObject.getFeesDetailsAttribute5(),vObject.getFeesDetailsAttribute6(),vObject.getFeesDetailsAttribute7(),
				vObject.getFeesDetailsAttribute8(),vObject.getFeesDetailsAttribute9(),vObject.getFeesDetailsAttribute10(),
				vObject.getFeesDetailsAttribute11(),vObject.getFeesDetailsAttribute12(),vObject.getFeesDetailsAttribute13(),
				vObject.getFeesDetailsAttribute14(),vObject.getFeesDetailsAttribute15(),vObject.getFeesDetailsAttribute16(),
				vObject.getFeesDetailsAttribute17(),vObject.getFeesDetailsAttribute18(),vObject.getFeesDetailsAttribute19(),
				vObject.getFeesDetailsAttribute20()};
		return getJdbcTemplate().update(query,args);
		
	}
	protected int deleteFeesDetailsPendMain(FeesConfigHeaderVb vObject){
		String query = "Delete from RA_MST_FEES_DETAIL_PEND WHERE COUNTRY= ? AND LE_BOOK= ? AND BUSINESS_LINE_ID = ?"+
					" AND  "+dbFunctionFormats("EFFECTIVE_DATE","DATETIME_FORMAT",null)+" = ?";
		Object[] args = {vObject.getCountry(),vObject.getLeBook(),vObject.getBusinessLineId(),
				vObject.getEffectiveDate()  };
		return getJdbcTemplate().update(query,args);
	}
	protected int deleteFeesDetailsApprMain(FeesConfigHeaderVb vObject){
		String query = "Delete from RA_MST_FEES_DETAIL WHERE COUNTRY= ? AND LE_BOOK= ? AND BUSINESS_LINE_ID = ?"+
				"   AND "+dbFunctionFormats("EFFECTIVE_DATE","DATETIME_FORMAT",null)+" = ?";
		Object[] args = {vObject.getCountry(),vObject.getLeBook(),vObject.getBusinessLineId(),
				vObject.getEffectiveDate()  };
		return getJdbcTemplate().update(query,args);
	}
	public ExceptionCode deleteAndInsertApprFeeDetail(FeesConfigHeaderVb vObject) throws RuntimeCustomException {
		setServiceDefaults();
		ExceptionCode exceptionCode = new ExceptionCode();
		List<FeesConfigDetailsVb> collTemp = null;
		
		//FEE CONFIG DETAIL
		List<FeesConfigDetailsVb> detaillst = vObject.getFeesConfigDetaillst();
		if(detaillst != null && !detaillst.isEmpty()) {
			for(FeesConfigDetailsVb feeDetailVb : detaillst){
				feeDetailVb.setCountry(vObject.getCountry());
				feeDetailVb.setLeBook(vObject.getLeBook());
				feeDetailVb.setBusinessLineId(vObject.getBusinessLineId());
				feeDetailVb.setEffectiveDate(vObject.getEffectiveDate());
				feeDetailVb.setFeeType(vObject.getFeeType());
				feeDetailVb.setFeeAmt(feeDetailVb.getFeeAmt().replaceAll(",", "")); 
				feeDetailVb.setMinFee(feeDetailVb.getMinFee().replaceAll(",", ""));
				feeDetailVb.setMaxFee(feeDetailVb.getMaxFee().replaceAll(",", ""));
                feeDetailVb.setFeePercentage(feeDetailVb.getFeePercentage().replaceAll(",", ""));
				if(Double.parseDouble(feeDetailVb.getMinFee()) >  Double.parseDouble(feeDetailVb.getMaxFee())) {
					exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
					exceptionCode.setErrorMsg("Max Fee amount should be greather than the Min Fee Amt for the Product Id["+feeDetailVb.getProductId()+"]");
					throw buildRuntimeCustomException(exceptionCode);
				}
				feeDetailVb = validateFeeAttribute(feeDetailVb);
				int delCnt = deleteFeesDetailsAppr(feeDetailVb);
				if(!feeDetailVb.isDeleteRecord()) {
					feeDetailVb.setFeeDetailStatus(Constants.STATUS_ZERO);
					feeDetailVb.setMaker(vObject.getMaker());
					feeDetailVb.setVerifier(vObject.getVerifier());
					feeDetailVb.setRecordIndicator(vObject.getRecordIndicator());
					
					retVal = doInsertionApprFeesDetails(feeDetailVb);
				}
				writeAuditLog(feeDetailVb, null);
				if (retVal != Constants.SUCCESSFUL_OPERATION){
					exceptionCode = getResultObject(retVal);
					throw buildRuntimeCustomException(exceptionCode);
				}
				//TIER INSERTION
				if(!"F".equalsIgnoreCase(feeDetailVb.getFeeType())) {
					int cnt = feesConfigTierDao.deleteFeesTierAppr(feeDetailVb);
					if(!feeDetailVb.isDeleteRecord()) {
						exceptionCode=feesConfigTierDao.doInsertApprFeeTier(feeDetailVb);	
						if (exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION){
							exceptionCode = getResultObject(retVal);
							throw buildRuntimeCustomException(exceptionCode);
						}
					}
				}
			}
		}
		exceptionCode = getResultObject(Constants.SUCCESSFUL_OPERATION);
		return exceptionCode;
	}
	public ExceptionCode deleteAndInsertPendFeeDetail(FeesConfigHeaderVb vObject) throws RuntimeCustomException {
		setServiceDefaults();
		ExceptionCode exceptionCode =  new ExceptionCode();
		List<FeesConfigDetailsVb> collTemp = null;
		collTemp = getQueryDetails(vObject, 1);
		
		//FEE CONFIG DETAIL
		List<FeesConfigDetailsVb> detaillst = vObject.getFeesConfigDetaillst();
		if(detaillst != null && !detaillst.isEmpty()) {
			for(FeesConfigDetailsVb feeDetailVb : detaillst){
				feeDetailVb.setCountry(vObject.getCountry());
				feeDetailVb.setLeBook(vObject.getLeBook());
				feeDetailVb.setBusinessLineId(vObject.getBusinessLineId());
				feeDetailVb.setEffectiveDate(vObject.getEffectiveDate());
				feeDetailVb.setFeeType(vObject.getFeeType());
				feeDetailVb.setFeeAmt(feeDetailVb.getFeeAmt().replaceAll(",", "")); 
				feeDetailVb.setMinFee(feeDetailVb.getMinFee().replaceAll(",", ""));
				feeDetailVb.setMaxFee(feeDetailVb.getMaxFee().replaceAll(",", ""));
				feeDetailVb.setFeePercentage(feeDetailVb.getFeePercentage().replaceAll(",", ""));
				if(Double.parseDouble(feeDetailVb.getMinFee()) >  Double.parseDouble(feeDetailVb.getMaxFee())) {
					exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
					exceptionCode.setErrorMsg("Max Fee amount should be greather than the Min Fee Amt for the Product Id["+feeDetailVb.getProductId()+"]");
					throw buildRuntimeCustomException(exceptionCode);
				}
				List<FeesConfigDetailsVb> colTemp = getQueryResultsForReview(feeDetailVb, 0);
				if(collTemp !=null && colTemp.size()>0) {
					feeDetailVb.setRecordIndicator(Constants.STATUS_UPDATE);
				}else {
					feeDetailVb.setRecordIndicator(Constants.STATUS_INSERT);
				}
				feeDetailVb = validateFeeAttribute(feeDetailVb);
				retVal = deleteFeesDetailsPend(feeDetailVb);
				/*if(feeDetailVb.isNewRecord()) {
					feeDetailVb.setRecordIndicator(Constants.STATUS_INSERT);
				}else {
					feeDetailVb.setRecordIndicator(vObject.getRecordIndicator());
				}*/
				if(!feeDetailVb.isDeleteRecord()) {
					feeDetailVb.setFeeDetailStatus(Constants.STATUS_ZERO);
					feeDetailVb.setMaker(intCurrentUserId);
					retVal = doInsertionPendFeesDetails(feeDetailVb);
					if (retVal != Constants.SUCCESSFUL_OPERATION){
						exceptionCode = getResultObject(retVal);
						throw buildRuntimeCustomException(exceptionCode);
					}
					if (retVal != Constants.SUCCESSFUL_OPERATION){
						exceptionCode = getResultObject(retVal);
						throw buildRuntimeCustomException(exceptionCode);
					}
				}else {
					if(feeDetailVb.getRecordIndicator() != Constants.STATUS_INSERT) {
						feeDetailVb.setRecordIndicator(Constants.STATUS_DELETE);
						feeDetailVb.setFeeDetailStatus(Constants.STATUS_ZERO);
						feeDetailVb.setMaker(intCurrentUserId);
						retVal = doInsertionPendFeesDetails(feeDetailVb);
						if (retVal != Constants.SUCCESSFUL_OPERATION){
							exceptionCode = getResultObject(retVal);
							throw buildRuntimeCustomException(exceptionCode);
						}
					}
				}
				//TIER INSERTION
				if(!"F".equalsIgnoreCase(feeDetailVb.getFeeType())) {
					retVal = feesConfigTierDao.deleteFeesTierPend(feeDetailVb);	
					exceptionCode=feesConfigTierDao.doInsertPendFeeTier(feeDetailVb);	
					if (exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION){
						exceptionCode = getResultObject(exceptionCode.getErrorCode());
						throw buildRuntimeCustomException(exceptionCode);
					}
				}
			}	
		}
		exceptionCode = getResultObject(Constants.SUCCESSFUL_OPERATION);
		return exceptionCode;
	}
	@Override
	protected String getAuditString(FeesConfigDetailsVb vObject){
		final String auditDelimiter = vObject.getAuditDelimiter();
		final String auditDelimiterColVal = vObject.getAuditDelimiterColVal();
		StringBuffer strAudit = new StringBuffer("");
		try
		{
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
			
			if(ValidationUtil.isValid(vObject.getBusinessLineId()))
				strAudit.append("BUSINESS_LINE_ID"+auditDelimiterColVal+vObject.getBusinessLineId().trim());
			else
				strAudit.append("BUSINESS_LINE_ID"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			
			/*strAudit.append("FEE_CONFIG_TYPE"+auditDelimiterColVal+vObject.getFeeConfigType());
			strAudit.append(auditDelimiter);
			
			if(ValidationUtil.isValid(vObject.getFeeConfigCode()))
				strAudit.append("FEE_CONFIG_CODE"+auditDelimiterColVal+vObject.getFeeConfigCode().trim());
			else
				strAudit.append("FEE_CONFIG_CODE"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);*/
			
			if(ValidationUtil.isValid(vObject.getEffectiveDate()))
				strAudit.append("EFFECTIVE_DATE"+auditDelimiterColVal+vObject.getEffectiveDate().trim());
			else
				strAudit.append("EFFECTIVE_DATE"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			
			if(ValidationUtil.isValid(vObject.getProductType()))
				strAudit.append("PRODUCT_TYPE"+auditDelimiterColVal+vObject.getProductType().trim());
			else
				strAudit.append("PRODUCT_TYPE"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			
			strAudit.append("PRODUCT_ID"+auditDelimiterColVal+vObject.getProductId());
			strAudit.append(auditDelimiter);
			
			if(ValidationUtil.isValid(vObject.getTranCcy()))
				strAudit.append("TRAN_CCY"+auditDelimiterColVal+vObject.getTranCcy().trim());
			else
				strAudit.append("TRAN_CCY"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			
			if(ValidationUtil.isValid(vObject.getFeeAmt()))
				strAudit.append("FEE_AMT"+auditDelimiterColVal+vObject.getFeeAmt().trim());
			else
				strAudit.append("FEE_AMT"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			
			if(ValidationUtil.isValid(vObject.getFeePercentage()))
				strAudit.append("FEE_PERCENTAGE"+auditDelimiterColVal+vObject.getFeePercentage().trim());
			else
				strAudit.append("FEE_PERCENTAGE"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			
			if(ValidationUtil.isValid(vObject.getMinFee()))
				strAudit.append("MIN_FEE"+auditDelimiterColVal+vObject.getMinFee().trim());
			else
				strAudit.append("MIN_FEE"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			
			if(ValidationUtil.isValid(vObject.getMaxFee()))
				strAudit.append("MAX_FEE"+auditDelimiterColVal+vObject.getMaxFee().trim());
			else
				strAudit.append("MAX_FEE"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);

			if (ValidationUtil.isValid(vObject.getChannelCode()))
				strAudit.append("CHANNEL_CODE" + auditDelimiterColVal + vObject.getChannelCode().trim());
			else
				strAudit.append("CHANNEL_CODE" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);

			if (ValidationUtil.isValid(vObject.getBusinessVertical()))
				strAudit.append("BUSINESS_VERTICAL" + auditDelimiterColVal + vObject.getBusinessVertical().trim());
			else
				strAudit.append("BUSINESS_VERTICAL" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);

			if (ValidationUtil.isValid(vObject.getCustomerId()))
				strAudit.append("CUSTOMER_ID" + auditDelimiterColVal + vObject.getCustomerId().trim());
			else
				strAudit.append("CUSTOMER_ID" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);

			if (ValidationUtil.isValid(vObject.getCustomerId()))
				strAudit.append("CUSTOMER_ID" + auditDelimiterColVal + vObject.getCustomerId().trim());
			else
				strAudit.append("CUSTOMER_ID" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);

			if (ValidationUtil.isValid(vObject.getContractId()))
				strAudit.append("CONTRACT_ID" + auditDelimiterColVal + vObject.getContractId().trim());
			else
				strAudit.append("CONTRACT_ID" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);

			if (ValidationUtil.isValid(vObject.getPostingCcy()))
				strAudit.append("POSTING_CCY" + auditDelimiterColVal + vObject.getPostingCcy().trim());
			else
				strAudit.append("POSTING_CCY" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);

			if (ValidationUtil.isValid(vObject.getCcyConversionType()))
				strAudit.append("CCY_CONVERSION_TYPE" + auditDelimiterColVal + vObject.getCcyConversionType().trim());
			else
				strAudit.append("CCY_CONVERSION_TYPE" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);
			
			if(ValidationUtil.isValid(vObject.getInterestBasis()))
				strAudit.append("INT_BASIS"+auditDelimiterColVal+vObject.getInterestBasis());
			else
				strAudit.append("INT_BASIS"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			
			if (ValidationUtil.isValid(vObject.getFeeBasis()))
				strAudit.append("FEE_BASIS" + auditDelimiterColVal + vObject.getFeeBasis().trim());
			else
				strAudit.append("FEE_BASIS" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);
			
			if(ValidationUtil.isValid(vObject.getRateEffectiveDate()))
				strAudit.append("RATE_EFFECTIVE_DATE"+auditDelimiterColVal+vObject.getRateEffectiveDate().trim());
			else
				strAudit.append("RATE_EFFECTIVE_DATE"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			
		}
		catch(Exception ex)
		{
			strErrorDesc = ex.getMessage();
			strAudit = strAudit.append(strErrorDesc);
			ex.printStackTrace();
		}
		return strAudit.toString();
	}

	public int getMaxSequence() {
		StringBuffer strBufApprove = new StringBuffer("Select MAX(TAppr.REF_NO) From RA_MST_FEES_DETAIL_HIS TAppr ");
		try {
			int max = getJdbcTemplate().queryForObject(strBufApprove.toString(), Integer.class);
			if (max <= 0)
				max = 1;
			else
				max++;
			return max;
		} catch (Exception ex) {
			// ex.printStackTrace();
			return 1;
		}
	}
	
	public List<FeesConfigDetailsVb> getFeesConfigDetailsHistory(FeesConfigDetailsVb dObj){
		List<FeesConfigDetailsVb> collTemp = null;
		List<CurrencyDetailsVb> currencyPrecisionlst = commonDao.getCurrencyDecimals(false);
		Vector<Object> params = new Vector<Object>();
		StringBuffer strBufApprove = null;
		StringBuffer strBufPending = null;
		String strWhereNotExists = null;
		String orderBy = "";
		String effectiveDateAppr="";
		String effectiveDatePend="";
		if(databaseType.equalsIgnoreCase("ORACLE")) {
			effectiveDateAppr =" AND TO_DATE(TAppr.EFFECTIVE_DATE,'DD-MM-RRRR HH24:MI:SS') = TO_DATE(?,'DD-MM-RRRR HH24:MI:SS') ";
			effectiveDatePend = " AND TO_DATE(TPend.EFFECTIVE_DATE,'DD-MM-RRRR HH24:MI:SS') = TO_DATE(?,'DD-MM-RRRR HH24:MI:SS') ";
				     
		} else{
			effectiveDateAppr =" AND TAppr.EFFECTIVE_DATE = ? ";
			effectiveDatePend =" AND TPend.EFFECTIVE_DATE = ? ";
		}
		try{
			strBufApprove = new StringBuffer(
					" SELECT  COUNTRY,LE_BOOK,BUSINESS_LINE_ID,   "+
							"  "+dbFunctionFormats("TAppr.EFFECTIVE_DATE","DATETIME_FORMAT",null)+" EFFECTIVE_DATE,"+
							"  "+dbFunctionFormats("TAppr.RATE_EFFECTIVE_DATE","DATETIME_FORMAT",null)+" RATE_EFFECTIVE_DATE,PRODUCT_TYPE,PRODUCT_ID,TRAN_CCY, "+
							"  "+dbFunctionFormats("TAppr.RATE_EFFECTIVE_END_DATE","DATETIME_FORMAT",null)+" RATE_EFFECTIVE_END_DATE ,"+
							"  TAppr.FEE_AMT,TAppr.RATE_EFFECTIVE_DATE RATE_EFFECTIVE_DATE_1, "+
							"  "+dbFunctionFormats("TAppr.FEE_PERCENTAGE","NUM_FORMAT","5")+" FEE_PERCENTAGE, "+
							"  TAppr.MIN_FEE, "+
							"  TAppr.MAX_FEE,"+
							" CHANNEL_CODE, BUSINESS_VERTICAL, CONTRACT_ID,"+
							"  CUSTOMER_ID, POSTING_CCY, CCY_CONVERSION_TYPE, "+
							"  (SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB= 7042 AND ALPHA_SUB_TAB = TAppr.CHANNEL_CODE) CHANNEL_CODE_DESC,"+ 
							"  (SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB= 7019 AND ALPHA_SUB_TAB = TAppr.PRODUCT_TYPE) PRODUCT_TYPE_DESC,"+ 
							"  (SELECT PRODUCT_DESC FROM RA_PAR_PRODUCT WHERE PRODUCT_ID = TAppr.PRODUCT_ID) PRODUCT_ID_DESC,"+ 
							"  (SELECT CCY_DESCRIPTION FROM CURRENCIES WHERE CURRENCY = TAppr.TRAN_CCY) TRAN_CCY_DESC, "+
							"  (SELECT CCY_DESCRIPTION FROM CURRENCIES WHERE CURRENCY = TAppr.POSTING_CCY) POSTING_CCY_DESC,"+
							"  (SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB= 7040 "+
							" AND ALPHA_SUB_TAB = TAppr.BUSINESS_VERTICAL) BUSINESS_VERTICAL_DESC, "+ 
							"  (SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB= 1106  "+
							" AND ALPHA_SUB_TAB = TAppr.CCY_CONVERSION_TYPE) CCY_CONVERSION_TYPE_DESC, "+
							"  (SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB= 7032 "+
							" AND ALPHA_SUB_TAB = TAppr.FEE_BASIS) FEE_BASIS_DESC,  "+
							"  (SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB= 7071 "+ 
							" AND ALPHA_SUB_TAB = TAppr.LOOKUP_AMOUNT_TYPE) LOOKUP_AMOUNT_TYPE_DESC, "+
							"  (SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB= 7071  "+
							" AND ALPHA_SUB_TAB = TAppr.PERCENT_AMOUNT_TYPE) PERCENT_AMOUNT_TYPE_DESC, "+
							"  (SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB= 7042 "+
							" AND ALPHA_SUB_TAB = TAppr.CHANNEL_TYPE) CHANNEL_TYPE_DESC, "+
							"  (SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB= 7072 "+
							" AND ALPHA_SUB_TAB = TAppr.ROUND_OFF_BASIS) ROUND_OFF_BASIS_DESC, "+
							"  (SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB= 7074 "+ 
							" AND ALPHA_SUB_TAB = TAppr.MIN_MAX_CCY_TYPE) MIN_MAX_CCY_TYPE_DESC,"+customerNameDescAppr+contractNameDescAppr+" "+
							"  INT_BASIS, FEE_BASIS , Fees_Details_Attribute_1, Fees_Details_Attribute_2, "+
							" Fees_Details_Attribute_3,Fees_Details_Attribute_4,Fees_Details_Attribute_5, "+
							"  Fees_Details_Attribute_6, Fees_Details_Attribute_7,Fees_Details_Attribute_8,"+
							"  Fees_Details_Attribute_9, Fees_Details_Attribute_10,Fees_Details_Attribute_11,"+
							"  Fees_Details_Attribute_12, Fees_Details_Attribute_13,Fees_Details_Attribute_14,"+
							"  Fees_Details_Attribute_15, Fees_Details_Attribute_16,Fees_Details_Attribute_17,"+
							"  Fees_Details_Attribute_18, Fees_Details_Attribute_19,Fees_Details_Attribute_20,"
							+ "PERCENT_AMOUNT_TYPE , PERCENT_AMOUNT_TYPE_AT,"+ 
							"  LOOKUP_AMOUNT_TYPE, LOOKUP_AMOUNT_TYPE_AT,CHANNEL_TYPE,ROUND_OFF_BASIS,ROUND_OFF_DECIMAL,MIN_MAX_CCY_TYPE,"+
							" TAppr.Fee_Detail_Status,"
							+"  (SELECT NUM_SUBTAB_DESCRIPTION FROM NUM_SUB_TAB WHERE NUM_TAB= 1 "
							+ "AND NUM_SUB_TAB = TAppr.Fee_Detail_Status) Fee_Detail_Status_DESC," 
							+"  (SELECT NUM_SUBTAB_DESCRIPTION FROM NUM_SUB_TAB WHERE NUM_TAB= 7 "
							+ "AND NUM_SUB_TAB = TAppr.RECORD_INDICATOR) RECORD_INDICATOR_DESC,"
							+ "TAppr.RECORD_INDICATOR,                                                                                          "+
							" TAppr. MAKER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "+getDbFunction("NVL")+"(TAppr.MAKER,0) ) MAKER_NAME,                                  "+
							" TAppr.VERIFIER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "+getDbFunction("NVL")+"(TAppr.VERIFIER,0) ) VERIFIER_NAME,                          "+
							" "+getDbFunction("DATEFUNC")+"(TAppr.DATE_LAST_MODIFIED, 'dd-MM-yyyy "+getDbFunction("TIME")+"') DATE_LAST_MODIFIED ,"
							+ ""+getDbFunction("DATEFUNC")+"(TAppr.DATE_CREATION, 'dd-MM-yyyy "+getDbFunction("TIME")+"') DATE_CREATION ,"+pricingAppr+ "TAppr.PRICING"+                                                                                            
							"  FROM RA_MST_FEES_DETAIL TAppr "+
							"  WHERE TAppr.COUNTRY = ?  "+
							"  AND TAppr.LE_BOOK = ? "+ 
							"  AND TAppr.BUSINESS_LINE_ID = ? "+
							effectiveDateAppr+ 
							" AND TAppr.PRODUCT_TYPE= ?         "+
							" AND TAppr.PRODUCT_ID= ?           "+
							" AND TAppr.CHANNEL_CODE= ?         "+
							" AND TAppr.BUSINESS_VERTICAL= ?    "+
							" AND TAppr.CONTRACT_ID= ?          "+
							" AND TAppr.CUSTOMER_ID= ?          "+
							" AND TAppr.CHANNEL_TYPE = ?        "+
							" AND TAppr.Fees_Details_Attribute_1= ? "+
							" AND TAppr.Fees_Details_Attribute_2= ? "+
							" AND TAppr.Fees_Details_Attribute_3= ? "+
							" AND TAppr.Fees_Details_Attribute_4= ? "+
							" AND TAppr.Fees_Details_Attribute_5= ? "+
							" AND TAppr.Fees_Details_Attribute_6= ? "+
							" AND TAppr.Fees_Details_Attribute_7= ? "+
							" AND TAppr.Fees_Details_Attribute_8= ? "+
							" AND TAppr.Fees_Details_Attribute_9= ? "+
							" AND TAppr.Fees_Details_Attribute_10= ? "+
							" AND TAppr.Fees_Details_Attribute_11= ? "+
							" AND TAppr.Fees_Details_Attribute_12= ? "+
							" AND TAppr.Fees_Details_Attribute_13= ? "+
							" AND TAppr.Fees_Details_Attribute_14= ? "+
							" AND TAppr.Fees_Details_Attribute_15= ? "+
							" AND TAppr.Fees_Details_Attribute_16= ? "+
							" AND TAppr.Fees_Details_Attribute_17= ? "+
							" AND TAppr.Fees_Details_Attribute_18= ? "+
							" AND TAppr.Fees_Details_Attribute_19= ? "+
							" AND TAppr.Fees_Details_Attribute_20= ? "+
							" AND TAppr.TRAN_CCY = ?");
		
		strWhereNotExists = new String(" Not Exists (Select 'X' From RA_MST_FEES_DETAIL_PEND TPEND WHERE "+
						" TAppr.COUNTRY = Tpend.COUNTRY        								 "+
						" AND TAppr.LE_BOOK = Tpend.LE_BOOK                                  "+
						" AND TAPPR.BUSINESS_LINE_ID = Tpend.BUSINESS_LINE_ID                "+
						" AND TAPPR.EFFECTIVE_DATE = Tpend.EFFECTIVE_DATE                    "+
						" AND TAppr.PRODUCT_TYPE = Tpend.PRODUCT_TYPE                        "+
					    " AND TAppr.PRODUCT_ID = Tpend.PRODUCT_ID                            "+
                        " AND TAppr.CHANNEL_CODE= Tpend.CHANNEL_CODE                         "+
                        " AND TAppr.BUSINESS_VERTICAL= Tpend.BUSINESS_VERTICAL               "+
                        " AND TAppr.CONTRACT_ID= Tpend.CONTRACT_ID                           "+
                        " AND TAppr.CUSTOMER_ID= Tpend.CUSTOMER_ID                           "+
                        " AND TAppr.CHANNEL_TYPE = Tpend.CHANNEL_TYPE                        "+
                        " AND TAppr.Fees_Details_Attribute_1= TPend.Fees_Details_Attribute_1 "+
                        " AND TAppr.Fees_Details_Attribute_2= TPend.Fees_Details_Attribute_2 "+
                        " AND TAppr.Fees_Details_Attribute_3= TPend.Fees_Details_Attribute_3 "+
                        " AND TAppr.Fees_Details_Attribute_4= TPend.Fees_Details_Attribute_4 "+
                        " AND TAppr.Fees_Details_Attribute_5= TPend.Fees_Details_Attribute_5 "+
                        " AND TAppr.Fees_Details_Attribute_6= TPend.Fees_Details_Attribute_6 "+
                        " AND TAppr.Fees_Details_Attribute_7= TPend.Fees_Details_Attribute_7 "+
                        " AND TAppr.Fees_Details_Attribute_8= TPend.Fees_Details_Attribute_8 "+
                        " AND TAppr.Fees_Details_Attribute_9= TPend.Fees_Details_Attribute_9 "+
                        " AND TAppr.Fees_Details_Attribute_10= TPend.Fees_Details_Attribute_10 "+
                        " AND TAppr.Fees_Details_Attribute_11= TPend.Fees_Details_Attribute_11 "+
                        " AND TAppr.Fees_Details_Attribute_12= TPend.Fees_Details_Attribute_12 "+
                        " AND TAppr.Fees_Details_Attribute_13= TPend.Fees_Details_Attribute_13 "+
                        " AND TAppr.Fees_Details_Attribute_14= TPend.Fees_Details_Attribute_14 "+
                        " AND TAppr.Fees_Details_Attribute_15= TPend.Fees_Details_Attribute_15 "+
                        " AND TAppr.Fees_Details_Attribute_16= TPend.Fees_Details_Attribute_16 "+
                        " AND TAppr.Fees_Details_Attribute_17= TPend.Fees_Details_Attribute_17 "+
                        " AND TAppr.Fees_Details_Attribute_18= TPend.Fees_Details_Attribute_18 "+
                        " AND TAppr.Fees_Details_Attribute_19= TPend.Fees_Details_Attribute_19 "+
                        " AND TAppr.Fees_Details_Attribute_20= TPend.Fees_Details_Attribute_20 "+        
                        " AND TAppr.RATE_EFFECTIVE_DATE= TPend.RATE_EFFECTIVE_DATE"+
                        " AND TAppr.TRAN_CCY= TPend.TRAN_CCY ) ");
		
		strBufPending = new StringBuffer(
				" SELECT  COUNTRY,LE_BOOK,BUSINESS_LINE_ID,   "+
						"  "+dbFunctionFormats("TPend.EFFECTIVE_DATE","DATETIME_FORMAT",null)+" EFFECTIVE_DATE,"+
						"  "+dbFunctionFormats("TPend.RATE_EFFECTIVE_DATE","DATETIME_FORMAT",null)+" RATE_EFFECTIVE_DATE,PRODUCT_TYPE,PRODUCT_ID,TRAN_CCY, "+ 
						"  "+dbFunctionFormats("TPend.RATE_EFFECTIVE_END_DATE","DATETIME_FORMAT",null)+" RATE_EFFECTIVE_END_DATE ,"+
						"  TPend.FEE_AMT, TPend.RATE_EFFECTIVE_DATE RATE_EFFECTIVE_DATE_1,"+
						"  "+dbFunctionFormats("TPend.FEE_PERCENTAGE","NUM_FORMAT","5")+" FEE_PERCENTAGE, "+
						"  TPend.MIN_FEE, "+
						"  TPend.MAX_FEE,  "+
						" CHANNEL_CODE, BUSINESS_VERTICAL, CONTRACT_ID,"+
						"  CUSTOMER_ID, POSTING_CCY, CCY_CONVERSION_TYPE, "+
						"  (SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB= 7042 AND ALPHA_SUB_TAB = TPend.CHANNEL_CODE) CHANNEL_CODE_DESC,"+ 
						"  (SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB= 7019 AND ALPHA_SUB_TAB = TPend.PRODUCT_TYPE) PRODUCT_TYPE_DESC,"+ 
						"  (SELECT PRODUCT_DESC FROM RA_PAR_PRODUCT WHERE PRODUCT_ID = TPend.PRODUCT_ID) PRODUCT_ID_DESC,"+ 
						"  (SELECT CCY_DESCRIPTION FROM CURRENCIES WHERE CURRENCY = TPend.TRAN_CCY) TRAN_CCY_DESC, "+
						"  (SELECT CCY_DESCRIPTION FROM CURRENCIES WHERE CURRENCY = TPend.POSTING_CCY) POSTING_CCY_DESC,"+
						"  (SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB= 7040 "+
						" AND ALPHA_SUB_TAB = TPend.BUSINESS_VERTICAL) BUSINESS_VERTICAL_DESC, "+ 
						"  (SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB= 1106  "+
						" AND ALPHA_SUB_TAB = TPend.CCY_CONVERSION_TYPE) CCY_CONVERSION_TYPE_DESC, "+
						"  (SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB= 7032 "+
						" AND ALPHA_SUB_TAB = TPend.FEE_BASIS) FEE_BASIS_DESC,  "+
						"  (SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB= 7071 "+ 
						" AND ALPHA_SUB_TAB = TPend.LOOKUP_AMOUNT_TYPE) LOOKUP_AMOUNT_TYPE_DESC, "+
						"  (SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB= 7071  "+
						" AND ALPHA_SUB_TAB = TPend.PERCENT_AMOUNT_TYPE) PERCENT_AMOUNT_TYPE_DESC, "+
						"  (SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB= 7042 "+
						" AND ALPHA_SUB_TAB = TPend.CHANNEL_TYPE) CHANNEL_TYPE_DESC, "+
						"  (SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB= 7072 "+
						" AND ALPHA_SUB_TAB = TPend.ROUND_OFF_BASIS) ROUND_OFF_BASIS_DESC, "+
						"  (SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB= 7074 "+ 
						" AND ALPHA_SUB_TAB = TPend.MIN_MAX_CCY_TYPE) MIN_MAX_CCY_TYPE_DESC,"+customerNameDesc+contractNameDesc+" "+
						"  INT_BASIS, FEE_BASIS , Fees_Details_Attribute_1, Fees_Details_Attribute_2, "+
						" Fees_Details_Attribute_3,Fees_Details_Attribute_4,Fees_Details_Attribute_5, "+
						"  Fees_Details_Attribute_6, Fees_Details_Attribute_7,Fees_Details_Attribute_8,"+
						"  Fees_Details_Attribute_9, Fees_Details_Attribute_10,Fees_Details_Attribute_11,"+
						"  Fees_Details_Attribute_12, Fees_Details_Attribute_13,Fees_Details_Attribute_14,"+
						"  Fees_Details_Attribute_15, Fees_Details_Attribute_16,Fees_Details_Attribute_17,"+
						"  Fees_Details_Attribute_18, Fees_Details_Attribute_19,Fees_Details_Attribute_20,"
						+" PERCENT_AMOUNT_TYPE , PERCENT_AMOUNT_TYPE_AT,"+ 
						"  LOOKUP_AMOUNT_TYPE, LOOKUP_AMOUNT_TYPE_AT,CHANNEL_TYPE,ROUND_OFF_BASIS,ROUND_OFF_DECIMAL,MIN_MAX_CCY_TYPE,"+
						" TPend.Fee_Detail_Status,"
						+"  (SELECT NUM_SUBTAB_DESCRIPTION FROM NUM_SUB_TAB WHERE NUM_TAB= 1 "
						+ "AND NUM_SUB_TAB = TPend.Fee_Detail_Status) Fee_Detail_Status_DESC," 
						+"  (SELECT NUM_SUBTAB_DESCRIPTION FROM NUM_SUB_TAB WHERE NUM_TAB= 7 "
						+ "AND NUM_SUB_TAB = TPend.RECORD_INDICATOR) RECORD_INDICATOR_DESC,"
						+ "TPend.RECORD_INDICATOR,                                                                                          "+
						" TPend. MAKER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "+getDbFunction("NVL")+"(TPend.MAKER,0) ) MAKER_NAME,                                  "+
						" TPend.VERIFIER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "+getDbFunction("NVL")+"(TPend.VERIFIER,0) ) VERIFIER_NAME,                          "+
						" "+getDbFunction("DATEFUNC")+"(TPend.DATE_LAST_MODIFIED, 'dd-MM-yyyy "+getDbFunction("TIME")+"') DATE_LAST_MODIFIED ,"
						+ ""+getDbFunction("DATEFUNC")+"(TPend.DATE_CREATION, 'dd-MM-yyyy "+getDbFunction("TIME")+"') DATE_CREATION ,"+pricingPend+" TPend.PRICING"+                                                                                             
						"  FROM RA_MST_FEES_DETAIL_Pend TPend "+
						"  WHERE TPend.COUNTRY = ?  "+
						"  AND TPend.LE_BOOK = ? "+ 
						"  AND TPend.BUSINESS_LINE_ID = ? "+
						effectiveDatePend+ 
						" AND TPend.PRODUCT_TYPE= ?         "+
						" AND TPend.PRODUCT_ID= ?           "+
						" AND TPend.CHANNEL_CODE= ?         "+
						" AND TPend.BUSINESS_VERTICAL= ?    "+
						" AND TPend.CONTRACT_ID= ?          "+
						" AND TPend.CUSTOMER_ID= ?          "+
						" AND TPend.CHANNEL_TYPE = ?        "+
						" AND TPend.Fees_Details_Attribute_1= ? "+
						" AND TPend.Fees_Details_Attribute_2= ? "+
						" AND TPend.Fees_Details_Attribute_3= ? "+
						" AND TPend.Fees_Details_Attribute_4= ? "+
						" AND TPend.Fees_Details_Attribute_5= ? "+
						" AND TPend.Fees_Details_Attribute_6= ? "+
						" AND TPend.Fees_Details_Attribute_7= ? "+
						" AND TPend.Fees_Details_Attribute_8= ? "+
						" AND TPend.Fees_Details_Attribute_9= ? "+
						" AND TPend.Fees_Details_Attribute_10= ? "+
						" AND TPend.Fees_Details_Attribute_11= ? "+
						" AND TPend.Fees_Details_Attribute_12= ? "+
						" AND TPend.Fees_Details_Attribute_13= ? "+
						" AND TPend.Fees_Details_Attribute_14= ? "+
						" AND TPend.Fees_Details_Attribute_15= ? "+
						" AND TPend.Fees_Details_Attribute_16= ? "+
						" AND TPend.Fees_Details_Attribute_17= ? "+
						" AND TPend.Fees_Details_Attribute_18= ? "+
						" AND TPend.Fees_Details_Attribute_19= ? "+
						" AND TPend.Fees_Details_Attribute_20= ? "+
						" AND TPend.TRAN_CCY = ? ");
			
			params.addElement(dObj.getCountry());// country
			params.addElement(dObj.getLeBook());
			params.addElement(dObj.getBusinessLineId());
			params.addElement(dObj.getEffectiveDate());
			params.addElement(dObj.getProductType());
			params.addElement(dObj.getProductId());
			params.addElement(dObj.getChannelCode());
			params.addElement(dObj.getBusinessVertical());
			params.addElement(dObj.getContractId());
			params.addElement(dObj.getCustomerId());
			params.addElement(dObj.getChannelType());
			params.addElement(dObj.getFeesDetailsAttribute1());
			params.addElement(dObj.getFeesDetailsAttribute2());
			params.addElement(dObj.getFeesDetailsAttribute3());
			params.addElement(dObj.getFeesDetailsAttribute4());
			params.addElement(dObj.getFeesDetailsAttribute5());
			params.addElement(dObj.getFeesDetailsAttribute6());
			params.addElement(dObj.getFeesDetailsAttribute7());
			params.addElement(dObj.getFeesDetailsAttribute8());
			params.addElement(dObj.getFeesDetailsAttribute9());
			params.addElement(dObj.getFeesDetailsAttribute10());
			params.addElement(dObj.getFeesDetailsAttribute11());
			params.addElement(dObj.getFeesDetailsAttribute12());
			params.addElement(dObj.getFeesDetailsAttribute13());
			params.addElement(dObj.getFeesDetailsAttribute14());
			params.addElement(dObj.getFeesDetailsAttribute15());
			params.addElement(dObj.getFeesDetailsAttribute16());
			params.addElement(dObj.getFeesDetailsAttribute17());
			params.addElement(dObj.getFeesDetailsAttribute18());
			params.addElement(dObj.getFeesDetailsAttribute19());
			params.addElement(dObj.getFeesDetailsAttribute20());
			params.addElement(dObj.getTranCcy());
		
			orderBy=" Order by RATE_EFFECTIVE_DATE_1 DESC ";
			return getQueryPopupResults(dObj,strBufPending, strBufApprove, strWhereNotExists, orderBy, params,getFeesConfigDetailMapper(currencyPrecisionlst));
		}catch(Exception ex){
			ex.printStackTrace();
			logger.error("Exception ");
			return null;
		}
	}
	protected int deleteFeesDetailsHistory(FeesConfigDetailsVb vObject){
		String query = "Delete from RA_MST_FEES_DETAIL_HIS WHERE COUNTRY= ? AND LE_BOOK= ? AND BUSINESS_LINE_ID = ?"+
				"  AND "+dbFunctionFormats("EFFECTIVE_DATE","DATETIME_FORMAT",null)+" = ? ";
		Object[] args = {vObject.getCountry(),vObject.getLeBook(),vObject.getBusinessLineId(),
				vObject.getEffectiveDate()};
		return getJdbcTemplate().update(query,args);
		
	}
	public List<FeesConfigDetailsVb> getQueryDetailsMain(FeesConfigHeaderVb dObj){
		List<FeesConfigDetailsVb> collTemp = null;
		String query = "";
		try{	
			List<CurrencyDetailsVb> currencyPrecisionlst = commonDao.getCurrencyDecimals(false);
					query =" SELECT  COUNTRY,LE_BOOK,BUSINESS_LINE_ID,   "+
							"  "+dbFunctionFormats("TAppr.EFFECTIVE_DATE","DATETIME_FORMAT",null)+" EFFECTIVE_DATE,"+
							"  "+dbFunctionFormats("TAppr.RATE_EFFECTIVE_DATE","DATETIME_FORMAT",null)+" RATE_EFFECTIVE_DATE,PRODUCT_TYPE,PRODUCT_ID,TRAN_CCY, "+  
							"  "+dbFunctionFormats("TAppr.RATE_EFFECTIVE_END_DATE","DATETIME_FORMAT",null)+" RATE_EFFECTIVE_END_DATE ,"+
							"  TAppr.FEE_AMT,   "+
							"  "+dbFunctionFormats("TAppr.FEE_PERCENTAGE","NUM_FORMAT","5")+" FEE_PERCENTAGE, "+
							"  TAppr.MIN_FEE,   "+
							"  TAppr.MAX_FEE,   "+
							" CHANNEL_CODE, BUSINESS_VERTICAL, CONTRACT_ID,"+
							"  CUSTOMER_ID, POSTING_CCY, CCY_CONVERSION_TYPE, "+
							"  (SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB= 7027 AND ALPHA_SUB_TAB = TAppr.CHANNEL_CODE) CHANNEL_CODE_DESC,"+ 
							"  (SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB= 7019 AND ALPHA_SUB_TAB = TAppr.PRODUCT_TYPE) PRODUCT_TYPE_DESC,"+ 
							"  (SELECT PRODUCT_DESC FROM RA_PAR_PRODUCT WHERE PRODUCT_ID = TAppr.PRODUCT_ID) PRODUCT_ID_DESC,"+ 
							"  (SELECT CCY_DESCRIPTION FROM CURRENCIES WHERE CURRENCY = TAppr.TRAN_CCY) TRAN_CCY_DESC, "+
							"  (SELECT CCY_DESCRIPTION FROM CURRENCIES WHERE CURRENCY = TAppr.POSTING_CCY) POSTING_CCY_DESC,"+
							"  (SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB= 7040 "+
							" AND ALPHA_SUB_TAB = TAppr.BUSINESS_VERTICAL) BUSINESS_VERTICAL_DESC, "+ 
							"  (SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB= 1106  "+
							" AND ALPHA_SUB_TAB = TAppr.CCY_CONVERSION_TYPE) CCY_CONVERSION_TYPE_DESC, "+
							"  (SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB= 7032 "+
							" AND ALPHA_SUB_TAB = TAppr.FEE_BASIS) FEE_BASIS_DESC,  "+
							"  (SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB= 7071 "+ 
							" AND ALPHA_SUB_TAB = TAppr.LOOKUP_AMOUNT_TYPE) LOOKUP_AMOUNT_TYPE_DESC, "+
							"  (SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB= 7071  "+
							" AND ALPHA_SUB_TAB = TAppr.PERCENT_AMOUNT_TYPE) PERCENT_AMOUNT_TYPE_DESC, "+
							"  (SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB= 7027 "+
							" AND ALPHA_SUB_TAB = TAppr.CHANNEL_TYPE) CHANNEL_TYPE_DESC, "+
							"  (SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB= 7072 "+
							" AND ALPHA_SUB_TAB = TAppr.ROUND_OFF_BASIS) ROUND_OFF_BASIS_DESC, "+
							"  (SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB= 7032 "+ 
							" AND ALPHA_SUB_TAB = TAppr.MIN_MAX_CCY_TYPE) MIN_MAX_CCY_TYPE_DESC, "+
							"  INT_BASIS, FEE_BASIS , Fees_Details_Attribute_1, Fees_Details_Attribute_2, "+
							" Fees_Details_Attribute_3,Fees_Details_Attribute_4,Fees_Details_Attribute_5, "+
							"  Fees_Details_Attribute_6, Fees_Details_Attribute_7,Fees_Details_Attribute_8,"+
							"  Fees_Details_Attribute_9, Fees_Details_Attribute_10,Fees_Details_Attribute_11,"+
							"  Fees_Details_Attribute_12, Fees_Details_Attribute_13,Fees_Details_Attribute_14,"+
							"  Fees_Details_Attribute_15, Fees_Details_Attribute_16,Fees_Details_Attribute_17,"+
							"  Fees_Details_Attribute_18, Fees_Details_Attribute_19,Fees_Details_Attribute_20,"
							+ " PERCENT_AMOUNT_TYPE , PERCENT_AMOUNT_TYPE_AT,"+ 
							"  LOOKUP_AMOUNT_TYPE, LOOKUP_AMOUNT_TYPE_AT,CHANNEL_TYPE,ROUND_OFF_BASIS,ROUND_OFF_DECIMAL,MIN_MAX_CCY_TYPE,"+
							"  TAppr.Fee_Detail_Status,TAppr.RECORD_INDICATOR, "+
							"  (SELECT NUM_SUBTAB_DESCRIPTION FROM NUM_SUB_TAB WHERE NUM_TAB= 1 "
							+ "AND NUM_SUB_TAB = TAppr.Fee_Detail_Status) Fee_Detail_Status_DESC," 
							+"  (SELECT NUM_SUBTAB_DESCRIPTION FROM NUM_SUB_TAB WHERE NUM_TAB= 7 "
							+ "AND NUM_SUB_TAB = TAppr.RECORD_INDICATOR) RECORD_INDICATOR_DESC,"
							+" TAppr. MAKER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "+getDbFunction("NVL")+"(TAppr.MAKER,0) ) MAKER_NAME,                                  "+
							" TAppr.VERIFIER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "+getDbFunction("NVL")+"(TAppr.VERIFIER,0) ) VERIFIER_NAME,                          "+
							dbFunctionFormats("TAppr.DATE_LAST_MODIFIED","DATETIME_FORMAT",null)+" DATE_LAST_MODIFIED , "+
							"  "+dbFunctionFormats("TAppr.DATE_CREATION","DATETIME_FORMAT",null)+" DATE_CREATION  ,"+pricingAppr+" TAppr.PRICING "+
							"  FROM RA_MST_FEES_DETAIL TAppr  where Country = ? and LE_Book = ? and  Business_Line_ID =? " +
				"   And "+dbFunctionFormats("EFFECTIVE_DATE","DATETIME_FORMAT",null)+" = ? ";

			Object objParams[] = new Object[4];
			objParams[0] = new String(dObj.getCountry());// country
			objParams[1] = new String(dObj.getLeBook());
			objParams[2] = new String(dObj.getBusinessLineId());
			objParams[3] = new String(dObj.getEffectiveDate());
			collTemp = getJdbcTemplate().query(query,objParams,getFeesConfigDetailMapper(currencyPrecisionlst));
			return collTemp;
		}catch(Exception ex){
			logger.error("Error in get Query Main "+ex.getMessage());
			return null;
		}
	}
	public List<FeesConfigDetailsVb> getQueryDetailsPend(FeesConfigHeaderVb dObj){
		List<FeesConfigDetailsVb> collTemp = null;
		String query = "";
		try{
			List<CurrencyDetailsVb> currencyPrecisionlst = commonDao.getCurrencyDecimals(false);
					query =" SELECT  COUNTRY,LE_BOOK,BUSINESS_LINE_ID,   "+
							"  "+dbFunctionFormats("TAppr.EFFECTIVE_DATE","DATETIME_FORMAT",null)+" EFFECTIVE_DATE,"+
							"  "+dbFunctionFormats("TAppr.RATE_EFFECTIVE_DATE","DATETIME_FORMAT",null)+" RATE_EFFECTIVE_DATE,PRODUCT_TYPE,PRODUCT_ID,TRAN_CCY, "+  
							"  "+dbFunctionFormats("TAppr.RATE_EFFECTIVE_END_DATE","DATETIME_FORMAT",null)+" RATE_EFFECTIVE_END_DATE,"+
							"  TAppr.FEE_AMT, "+
							"  "+dbFunctionFormats("TAppr.FEE_PERCENTAGE","NUM_FORMAT","5")+" FEE_PERCENTAGE, "+
							"  TAppr.MIN_FEE, "+
							"  TAppr.MAX_FEE, "+
							" CHANNEL_CODE, BUSINESS_VERTICAL, CONTRACT_ID,"+customerNameDescAppr+contractNameDescAppr+" "+
							"  CUSTOMER_ID, POSTING_CCY, CCY_CONVERSION_TYPE, "+
							"  (SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB= 7027 AND ALPHA_SUB_TAB = TAppr.CHANNEL_CODE) CHANNEL_CODE_DESC,"+ 
							"  (SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB= 7019 AND ALPHA_SUB_TAB = TAppr.PRODUCT_TYPE) PRODUCT_TYPE_DESC,"+ 
							"  (SELECT PRODUCT_DESC FROM RA_PAR_PRODUCT WHERE PRODUCT_ID = TAppr.PRODUCT_ID) PRODUCT_ID_DESC,"+ 
							"  (SELECT CCY_DESCRIPTION FROM CURRENCIES WHERE CURRENCY = TAppr.TRAN_CCY) TRAN_CCY_DESC, "+
							"  (SELECT CCY_DESCRIPTION FROM CURRENCIES WHERE CURRENCY = TAppr.POSTING_CCY) POSTING_CCY_DESC,"+
							"  (SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB= 7040 "+
							" AND ALPHA_SUB_TAB = TAppr.BUSINESS_VERTICAL) BUSINESS_VERTICAL_DESC, "+ 
							"  (SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB= 1106  "+
							" AND ALPHA_SUB_TAB = TAppr.CCY_CONVERSION_TYPE) CCY_CONVERSION_TYPE_DESC, "+
							"  (SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB= 7032 "+
							" AND ALPHA_SUB_TAB = TAppr.FEE_BASIS) FEE_BASIS_DESC,  "+
							"  (SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB= 7071 "+ 
							" AND ALPHA_SUB_TAB = TAppr.LOOKUP_AMOUNT_TYPE) LOOKUP_AMOUNT_TYPE_DESC, "+
							"  (SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB= 7071  "+
							" AND ALPHA_SUB_TAB = TAppr.PERCENT_AMOUNT_TYPE) PERCENT_AMOUNT_TYPE_DESC, "+
							"  (SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB= 7027 "+
							" AND ALPHA_SUB_TAB = TAppr.CHANNEL_TYPE) CHANNEL_TYPE_DESC, "+
							"  (SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB= 7072 "+
							" AND ALPHA_SUB_TAB = TAppr.ROUND_OFF_BASIS) ROUND_OFF_BASIS_DESC, "+
							"  (SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB= 7032 "+ 
							" AND ALPHA_SUB_TAB = TAppr.MIN_MAX_CCY_TYPE) MIN_MAX_CCY_TYPE_DESC, "+
							"  INT_BASIS, FEE_BASIS , Fees_Details_Attribute_1, Fees_Details_Attribute_2, "+
							" Fees_Details_Attribute_3,Fees_Details_Attribute_4,Fees_Details_Attribute_5, "+
							"  Fees_Details_Attribute_6, Fees_Details_Attribute_7,Fees_Details_Attribute_8,"+
							"  Fees_Details_Attribute_9, Fees_Details_Attribute_10,Fees_Details_Attribute_11,"+
							"  Fees_Details_Attribute_12, Fees_Details_Attribute_13,Fees_Details_Attribute_14,"+
							"  Fees_Details_Attribute_15, Fees_Details_Attribute_16,Fees_Details_Attribute_17,"+
							"  Fees_Details_Attribute_18, Fees_Details_Attribute_19,Fees_Details_Attribute_20,"
							+" PERCENT_AMOUNT_TYPE , PERCENT_AMOUNT_TYPE_AT,"+ 
							"  LOOKUP_AMOUNT_TYPE, LOOKUP_AMOUNT_TYPE_AT,CHANNEL_TYPE,ROUND_OFF_BASIS,ROUND_OFF_DECIMAL,MIN_MAX_CCY_TYPE,"+
							"  TAppr.Fee_Detail_Status,TAppr.RECORD_INDICATOR, "
							+"  (SELECT NUM_SUBTAB_DESCRIPTION FROM NUM_SUB_TAB WHERE NUM_TAB= 1 "
							+ "AND NUM_SUB_TAB = TAppr.Fee_Detail_Status) Fee_Detail_Status_DESC," 
							+"  (SELECT NUM_SUBTAB_DESCRIPTION FROM NUM_SUB_TAB WHERE NUM_TAB= 7 "
							+ "AND NUM_SUB_TAB = TAppr.RECORD_INDICATOR) RECORD_INDICATOR_DESC,"
							+" (SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "+getDbFunction("NVL")+"(TAppr.MAKER,0) ) MAKER_NAME,          "
							+" (SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "+getDbFunction("NVL")+"(TAppr.VERIFIER,0) ) VERIFIER_NAME,   "
							+"  TAppr.MAKER,   "+
							"  TAppr.VERIFIER, "+dbFunctionFormats("TAppr.DATE_LAST_MODIFIED","DATETIME_FORMAT",null)+" DATE_LAST_MODIFIED , "+
							"  "+dbFunctionFormats("TAppr.DATE_CREATION","DATETIME_FORMAT",null)+" DATE_CREATION  ,"+pricingAppr+"TAppr.PRICING "+
							"  FROM RA_MST_FEES_DETAIL_PEND TAppr  where Country = ? and LE_Book = ? and  Business_Line_ID =? " +
				"   And "+dbFunctionFormats("EFFECTIVE_DATE","DATETIME_FORMAT",null)+" = ? ";

			Object objParams[] = new Object[4];
			objParams[0] = new String(dObj.getCountry());// country
			objParams[1] = new String(dObj.getLeBook());
			objParams[2] = new String(dObj.getBusinessLineId());
			objParams[3] = new String(dObj.getEffectiveDate());
			collTemp = getJdbcTemplate().query(query,objParams,getFeesConfigDetailMapper(currencyPrecisionlst));
			return collTemp;
		}catch(Exception ex){
			logger.error("Error in get Query Main "+ex.getMessage());
			return null;
		}
	}
	protected int doUpdateDetailStatus(FeesConfigHeaderVb vObject){
		String effectiveDate ="EFFECTIVE_DATE = ?";
		if (databaseType.equalsIgnoreCase("ORACLE")) {
			effectiveDate = "EFFECTIVE_DATE = TO_DATE(?,'DD-MM-RRRR HH24:MI:SS')";
		}
		String query = " Update RA_MST_FEES_DETAIL set "+
				" FEE_DETAIL_STATUS = ? "+
				" WHERE COUNTRY= ? AND LE_BOOK= ? AND BUSINESS_LINE_ID = ?"+
				" AND " +effectiveDate;
		Object[] args = {vObject.getFeesLineStatus(),vObject.getCountry(),vObject.getLeBook(),
				vObject.getBusinessLineId(),
				vObject.getEffectiveDate()};
		return getJdbcTemplate().update(query,args);
	}
	public List<ReviewResultVb> transformToReviewResults(List<FeesConfigDetailsVb> approvedCollection, List<FeesConfigDetailsVb> pendingCollection,FeesConfigDetailsVb vObject) {
		/*if(pendingCollection != null)
			getScreenDao().fetchMakerVerifierNames(pendingCollection.get(0));
		if(approvedCollection != null)
			getScreenDao().fetchMakerVerifierNames(approvedCollection.get(0));*/
		ArrayList<ReviewResultVb> lResult = new ArrayList<ReviewResultVb>();
		
		LinkedHashMap<String,String> resultData = new LinkedHashMap<String,String>();
		String feeAttrArr[] = new String[20];
		String dataDicArr[] = {"FEES_DETAILS_ATTRIBUTE_1","FEES_DETAILS_ATTRIBUTE_2","FEES_DETAILS_ATTRIBUTE_3","FEES_DETAILS_ATTRIBUTE_4","FEES_DETAILS_ATTRIBUTE_5","FEES_DETAILS_ATTRIBUTE_6","FEES_DETAILS_ATTRIBUTE_7","FEES_DETAILS_ATTRIBUTE_8,","FEES_DETAILS_ATTRIBUTE_9","FEES_DETAILS_ATTRIBUTE_10","FEES_DETAILS_ATTRIBUTE_11",
				"FEES_DETAILS_ATTRIBUTE_12","FEES_DETAILS_ATTRIBUTE_13","FEES_DETAILS_ATTRIBUTE_14","FEES_DETAILS_ATTRIBUTE_15","FEES_DETAILS_ATTRIBUTE_16","FEES_DETAILS_ATTRIBUTE_17","FEES_DETAILS_ATTRIBUTE_18","FEES_DETAILS_ATTRIBUTE_19","FEES_DETAILS_ATTRIBUTE_20"};
		resultData = getFeesConfigPriority(vObject);
		if(resultData != null && resultData.size() > 0) {
			for (int i = 1; i <= dataDicArr.length; i++) {
				for (Map.Entry<String, String> entry : resultData.entrySet()) {
					String val = entry.getKey().toUpperCase();
					if(val.equalsIgnoreCase(dataDicArr[i-1])) {
						feeAttrArr[i - 1] = ValidationUtil.isValid(entry.getValue()) ? entry.getValue(): "Fees Details Attribute " + i;
						break;
					}else {
						feeAttrArr[i - 1] = "Fees Details Attribute " + i;
					}
				}
			}
		} else {
			for (int i = 1; i <= 20; i++) {
				feeAttrArr[i-1] = "Fees Details Attribute "+i;
			}
		}
		
		ReviewResultVb effectiveDate = new ReviewResultVb("Effective Date",(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getEffectiveDate(),
				(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getEffectiveDate(),
						(!pendingCollection.get(0).getEffectiveDate().equals(approvedCollection.get(0).getEffectiveDate())));
		lResult.add(effectiveDate);
		
		ReviewResultVb rateEffectiveDate = new ReviewResultVb("Rate Effective Date",(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getRateEffectiveDate(),
				(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getRateEffectiveDate(),
						(!pendingCollection.get(0).getRateEffectiveDate().equals(approvedCollection.get(0).getRateEffectiveDate())));
		lResult.add(rateEffectiveDate);
		
		ReviewResultVb channelType = new ReviewResultVb("Channel Type",(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getChannelTypeDesc(),
				(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getChannelTypeDesc(),
						(!pendingCollection.get(0).getChannelTypeDesc().equals(approvedCollection.get(0).getChannelTypeDesc())));
		lResult.add(channelType);
		
		ReviewResultVb channelCode = new ReviewResultVb("Channel Code",(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getChannelCodeDesc(),
				(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getChannelCodeDesc(),
						(!pendingCollection.get(0).getChannelCodeDesc().equals(approvedCollection.get(0).getChannelCodeDesc())));
		lResult.add(channelCode);
		
		ReviewResultVb businessVertical = new ReviewResultVb("Business Vertical",(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getBusinessVertical(),
				(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getBusinessVertical(),
						(!pendingCollection.get(0).getBusinessVertical().equals(approvedCollection.get(0).getBusinessVertical())));
		lResult.add(businessVertical);
		
		ReviewResultVb customerId = new ReviewResultVb("Customer Id",(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getCustomerId(),
				(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getCustomerId(),
						(!pendingCollection.get(0).getCustomerId().equals(approvedCollection.get(0).getCustomerId())));
		lResult.add(customerId);
		
		ReviewResultVb customerName = new ReviewResultVb("Customer Name",(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getCustomerName(),
				(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getCustomerName(),
						(!((ValidationUtil.isValid(pendingCollection.get(0).getCustomerName()))? pendingCollection.get(0).getCustomerName() : "").equals((ValidationUtil.isValid(approvedCollection.get(0).getCustomerName()))? approvedCollection.get(0).getCustomerName() : "")));
		lResult.add(customerName);
		
		ReviewResultVb contractId = new ReviewResultVb("Contract Id",(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getContractId(),
				(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getContractId(),
						(!pendingCollection.get(0).getContractId().equals(approvedCollection.get(0).getContractId())));
		lResult.add(contractId);
		
		ReviewResultVb contractName = new ReviewResultVb("Contract Name",(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getContractName(),
				(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getContractName(),
						(!pendingCollection.get(0).getContractName().equals(approvedCollection.get(0).getContractName())));
		lResult.add(contractName);
		
		ReviewResultVb productType = new ReviewResultVb("Product Type",(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getProductType(),
				(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getProductType(),
						(!pendingCollection.get(0).getProductType().equals(approvedCollection.get(0).getProductType())));
		lResult.add(productType);
		
		ReviewResultVb productDesc = new ReviewResultVb("Product Description",(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getProductTypeDesc(),
				(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getProductTypeDesc(),
						(!pendingCollection.get(0).getProductTypeDesc().equals(approvedCollection.get(0).getProductTypeDesc())));
		lResult.add(productDesc);
		
		ReviewResultVb feeDetAttr1 = new ReviewResultVb(feeAttrArr[0],(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getFeesDetailsAttribute1(),
				(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getFeesDetailsAttribute1(),
						(!pendingCollection.get(0).getFeesDetailsAttribute1().equals(approvedCollection.get(0).getFeesDetailsAttribute1())));
		lResult.add(feeDetAttr1);
		
		ReviewResultVb feeDetAttr2 = new ReviewResultVb(feeAttrArr[1],(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getFeesDetailsAttribute2(),
				(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getFeesDetailsAttribute2(),
						(!pendingCollection.get(0).getFeesDetailsAttribute2().equals(approvedCollection.get(0).getFeesDetailsAttribute2())));
		lResult.add(feeDetAttr2);
		
		ReviewResultVb feeDetAttr3 = new ReviewResultVb(feeAttrArr[2],(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getFeesDetailsAttribute3(),
				(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getFeesDetailsAttribute3(),
						(!pendingCollection.get(0).getFeesDetailsAttribute3().equals(approvedCollection.get(0).getFeesDetailsAttribute3())));
		lResult.add(feeDetAttr3);
		
		ReviewResultVb feeDetAttr4 = new ReviewResultVb(feeAttrArr[3],(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getFeesDetailsAttribute4(),
				(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getFeesDetailsAttribute4(),
						(!pendingCollection.get(0).getFeesDetailsAttribute4().equals(approvedCollection.get(0).getFeesDetailsAttribute4())));
		lResult.add(feeDetAttr4);
		
		ReviewResultVb feeDetAttr5 = new ReviewResultVb(feeAttrArr[4],(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getFeesDetailsAttribute5(),
				(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getFeesDetailsAttribute5(),
						(!pendingCollection.get(0).getFeesDetailsAttribute5().equals(approvedCollection.get(0).getFeesDetailsAttribute5())));
		lResult.add(feeDetAttr5);
		
		ReviewResultVb feeDetAttr6 = new ReviewResultVb(feeAttrArr[5],(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getFeesDetailsAttribute6(),
				(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getFeesDetailsAttribute6(),
						(!pendingCollection.get(0).getFeesDetailsAttribute6().equals(approvedCollection.get(0).getFeesDetailsAttribute6())));
		lResult.add(feeDetAttr6);
		
		ReviewResultVb feeDetAttr7 = new ReviewResultVb(feeAttrArr[6],(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getFeesDetailsAttribute7(),
				(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getFeesDetailsAttribute7(),
						(!pendingCollection.get(0).getFeesDetailsAttribute7().equals(approvedCollection.get(0).getFeesDetailsAttribute7())));
		lResult.add(feeDetAttr7);
		
		ReviewResultVb feeDetAttr8 = new ReviewResultVb(feeAttrArr[7],(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getFeesDetailsAttribute8(),
				(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getFeesDetailsAttribute8(),
						(!pendingCollection.get(0).getFeesDetailsAttribute8().equals(approvedCollection.get(0).getFeesDetailsAttribute8())));
		lResult.add(feeDetAttr8);
		
		ReviewResultVb feeDetAttr9 = new ReviewResultVb(feeAttrArr[8],(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getFeesDetailsAttribute9(),
				(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getFeesDetailsAttribute9(),
						(!pendingCollection.get(0).getFeesDetailsAttribute9().equals(approvedCollection.get(0).getFeesDetailsAttribute9())));
		lResult.add(feeDetAttr9);
		
		ReviewResultVb feeDetAttr10 = new ReviewResultVb(feeAttrArr[9],(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getFeesDetailsAttribute10(),
				(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getFeesDetailsAttribute10(),
						(!pendingCollection.get(0).getFeesDetailsAttribute10().equals(approvedCollection.get(0).getFeesDetailsAttribute10())));
		lResult.add(feeDetAttr10);
		
		ReviewResultVb feeDetAttr11 = new ReviewResultVb(feeAttrArr[10],(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getFeesDetailsAttribute11(),
				(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getFeesDetailsAttribute11(),
						(!pendingCollection.get(0).getFeesDetailsAttribute11().equals(approvedCollection.get(0).getFeesDetailsAttribute11())));
		lResult.add(feeDetAttr11);
		
		ReviewResultVb feeDetAttr12 = new ReviewResultVb(feeAttrArr[11],(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getFeesDetailsAttribute12(),
				(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getFeesDetailsAttribute12(),
						(!pendingCollection.get(0).getFeesDetailsAttribute12().equals(approvedCollection.get(0).getFeesDetailsAttribute12())));
		lResult.add(feeDetAttr12);
		
		ReviewResultVb feeDetAttr13 = new ReviewResultVb(feeAttrArr[12],(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getFeesDetailsAttribute13(),
				(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getFeesDetailsAttribute13(),
						(!pendingCollection.get(0).getFeesDetailsAttribute13().equals(approvedCollection.get(0).getFeesDetailsAttribute13())));
		lResult.add(feeDetAttr13);
		
		ReviewResultVb feeDetAttr14 = new ReviewResultVb(feeAttrArr[13],(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getFeesDetailsAttribute14(),
				(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getFeesDetailsAttribute14(),
						(!pendingCollection.get(0).getFeesDetailsAttribute14().equals(approvedCollection.get(0).getFeesDetailsAttribute14())));
		lResult.add(feeDetAttr14);
		
		ReviewResultVb feeDetAttr15 = new ReviewResultVb(feeAttrArr[14],(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getFeesDetailsAttribute15(),
				(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getFeesDetailsAttribute15(),
						(!pendingCollection.get(0).getFeesDetailsAttribute15().equals(approvedCollection.get(0).getFeesDetailsAttribute15())));
		lResult.add(feeDetAttr15);
		
		ReviewResultVb feeDetAttr16 = new ReviewResultVb(feeAttrArr[15],(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getFeesDetailsAttribute16(),
				(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getFeesDetailsAttribute16(),
						(!pendingCollection.get(0).getFeesDetailsAttribute16().equals(approvedCollection.get(0).getFeesDetailsAttribute16())));
		lResult.add(feeDetAttr16);
		
		ReviewResultVb feeDetAttr17 = new ReviewResultVb(feeAttrArr[16],(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getFeesDetailsAttribute17(),
				(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getFeesDetailsAttribute17(),
						(!pendingCollection.get(0).getFeesDetailsAttribute17().equals(approvedCollection.get(0).getFeesDetailsAttribute17())));
		lResult.add(feeDetAttr17);
		
		ReviewResultVb feeDetAttr18 = new ReviewResultVb(feeAttrArr[17],(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getFeesDetailsAttribute18(),
				(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getFeesDetailsAttribute18(),
						(!pendingCollection.get(0).getFeesDetailsAttribute18().equals(approvedCollection.get(0).getFeesDetailsAttribute18())));
		lResult.add(feeDetAttr18);
		
		ReviewResultVb feeDetAttr19 = new ReviewResultVb(feeAttrArr[18],(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getFeesDetailsAttribute19(),
				(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getFeesDetailsAttribute19(),
						(!pendingCollection.get(0).getFeesDetailsAttribute19().equals(approvedCollection.get(0).getFeesDetailsAttribute19())));
		lResult.add(feeDetAttr19);
		
		ReviewResultVb feeDetAttr20 = new ReviewResultVb(feeAttrArr[19],(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getFeesDetailsAttribute20(),
				(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getFeesDetailsAttribute20(),
						(!pendingCollection.get(0).getFeesDetailsAttribute20().equals(approvedCollection.get(0).getFeesDetailsAttribute20())));
		lResult.add(feeDetAttr20);
		
		ReviewResultVb transCur = new ReviewResultVb("Transaction Currency",(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getTransCCYDesc(),
				(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getTransCCYDesc(),
						(!pendingCollection.get(0).getTransCCYDesc().equals(approvedCollection.get(0).getTransCCYDesc())));
		lResult.add(transCur);
		
		ReviewResultVb lPricing = new ReviewResultVb("Pricing",(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getPricingDesc(),
				(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getPricingDesc(),
						(!pendingCollection.get(0).getPricingDesc().equals(approvedCollection.get(0).getPricingDesc())));
		lResult.add(lPricing);
		
		ReviewResultVb postCur = new ReviewResultVb("Posting Currency",(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getPostingCCYDesc(),
				(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getPostingCCYDesc(),
						(!pendingCollection.get(0).getPostingCCYDesc().equals(approvedCollection.get(0).getPostingCCYDesc())));
		lResult.add(postCur);
		
		ReviewResultVb ccyConvType = new ReviewResultVb("Currency Conversion Type",(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getCcyConversionTypeDesc(),
				(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getCcyConversionTypeDesc(),
						(!pendingCollection.get(0).getCcyConversionTypeDesc().equals(approvedCollection.get(0).getCcyConversionTypeDesc())));
		lResult.add(ccyConvType);
		
		ReviewResultVb lookAmtType = new ReviewResultVb("Look Amount Type",(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getLookupAmountTypeDesc(),
				(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getLookupAmountTypeDesc(),
						(!pendingCollection.get(0).getLookupAmountTypeDesc().equals(approvedCollection.get(0).getLookupAmountTypeDesc())));
		lResult.add(lookAmtType);
		
		ReviewResultVb percentAmtType = new ReviewResultVb("Percent Amount Type",(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getPercentAmountTypeDesc(),
				(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getPercentAmountTypeDesc(),
						(!pendingCollection.get(0).getPercentAmountTypeDesc().equals(approvedCollection.get(0).getPercentAmountTypeDesc())));
		lResult.add(percentAmtType);
	
		ReviewResultVb intBasis = new ReviewResultVb("Interest Basis",(pendingCollection == null || pendingCollection.isEmpty())?"":String.valueOf(pendingCollection.get(0).getInterestBasis()),
				(approvedCollection == null || approvedCollection.isEmpty())?"":String.valueOf(approvedCollection.get(0).getInterestBasis()),
						(!String.valueOf(pendingCollection.get(0).getInterestBasis()).equals(String.valueOf(approvedCollection.get(0).getInterestBasis()))));
		lResult.add(intBasis);
		
		ReviewResultVb roundOffBasis = new ReviewResultVb("Round Off Basis",(pendingCollection == null || pendingCollection.isEmpty())?"":String.valueOf(pendingCollection.get(0).getRoundOffBasisDesc()),
				(approvedCollection == null || approvedCollection.isEmpty())?"":String.valueOf(approvedCollection.get(0).getRoundOffBasisDesc()),
						(!String.valueOf(pendingCollection.get(0).getRoundOffBasisDesc()).equals(String.valueOf(approvedCollection.get(0).getRoundOffBasisDesc()))));
		lResult.add(roundOffBasis);
		
		ReviewResultVb roundOffDecimal = new ReviewResultVb("Round Off Basis Decimal",(pendingCollection == null || pendingCollection.isEmpty())?"":String.valueOf(pendingCollection.get(0).getRoundOffBasisDecimal()),
				(approvedCollection == null || approvedCollection.isEmpty())?"":String.valueOf(approvedCollection.get(0).getRoundOffBasisDecimal()),
						(!String.valueOf(pendingCollection.get(0).getRoundOffBasisDecimal()).equals(String.valueOf(approvedCollection.get(0).getRoundOffBasisDecimal()))));
		lResult.add(roundOffDecimal);

		ReviewResultVb minMaxCCyType = new ReviewResultVb("Min Max CCY Type",(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getMinMaxCcyTypeDesc(),
				(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getMinMaxCcyTypeDesc(),
						(!pendingCollection.get(0).getMinMaxCcyTypeDesc().equals(approvedCollection.get(0).getMinMaxCcyTypeDesc())));
		lResult.add(minMaxCCyType);
		
		ReviewResultVb feeBasis = new ReviewResultVb("Fee Basis",(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getFeesBasisDesc(),
				(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getFeesBasisDesc(),
						(!pendingCollection.get(0).getFeesBasisDesc().equals(approvedCollection.get(0).getFeesBasisDesc())));
		lResult.add(feeBasis);
		

		ReviewResultVb feeAmount = new ReviewResultVb("Fee Amount",(pendingCollection == null || pendingCollection.isEmpty())?"":String.valueOf(pendingCollection.get(0).getFeeAmt()),
				(approvedCollection == null || approvedCollection.isEmpty())?"":String.valueOf(approvedCollection.get(0).getFeeAmt()),
						(!String.valueOf(pendingCollection.get(0).getFeeAmt()).equals(String.valueOf(approvedCollection.get(0).getFeeAmt()))));
		lResult.add(feeAmount);
		
		ReviewResultVb feePercent = new ReviewResultVb("Fee Percentage",(pendingCollection == null || pendingCollection.isEmpty())?"":String.valueOf(pendingCollection.get(0).getFeePercentage()),
				(approvedCollection == null || approvedCollection.isEmpty())?"":String.valueOf(approvedCollection.get(0).getFeePercentage()),
						(!String.valueOf(pendingCollection.get(0).getFeePercentage()).equals(String.valueOf(approvedCollection.get(0).getFeePercentage()))));
		lResult.add(feePercent);
		
		ReviewResultVb minFeeAmount = new ReviewResultVb("Min Fee Amount",(pendingCollection == null || pendingCollection.isEmpty())?"":String.valueOf(pendingCollection.get(0).getMinFee()),
				(approvedCollection == null || approvedCollection.isEmpty())?"":String.valueOf(approvedCollection.get(0).getMinFee()),
						(!String.valueOf(pendingCollection.get(0).getMinFee()).equals(String.valueOf(approvedCollection.get(0).getMinFee()))));
		lResult.add(minFeeAmount);
		
		ReviewResultVb maxFeeAmount = new ReviewResultVb("Max Fee Amount",(pendingCollection == null || pendingCollection.isEmpty())?"":String.valueOf(pendingCollection.get(0).getMaxFee()),
				(approvedCollection == null || approvedCollection.isEmpty())?"":String.valueOf(approvedCollection.get(0).getMaxFee()),
						(!String.valueOf(pendingCollection.get(0).getMaxFee()).equals(String.valueOf(approvedCollection.get(0).getMaxFee()))));
		lResult.add(maxFeeAmount);
		
		ReviewResultVb lRecordIndicator = new ReviewResultVb("Record Indicator",
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: pendingCollection.get(0).getRecordIndicatorDesc() == null ? ""
								: pendingCollection.get(0).getRecordIndicatorDesc(),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: approvedCollection.get(0).getRecordIndicatorDesc() == null ? ""
								: approvedCollection.get(0).getRecordIndicatorDesc(),
				(pendingCollection.get(0).getRecordIndicatorDesc() != approvedCollection.get(0)
						.getRecordIndicatorDesc()));
		lResult.add(lRecordIndicator);

		ReviewResultVb status = new ReviewResultVb("Record Status",
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: pendingCollection.get(0).getFeeDetailStatusDesc(),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: approvedCollection.get(0).getFeeDetailStatusDesc(),
				(!pendingCollection.get(0).getFeeDetailStatusDesc()
						.equals(approvedCollection.get(0).getFeeDetailStatusDesc())));
		lResult.add(status);

		ReviewResultVb lMaker = new ReviewResultVb("Maker",(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getMaker() == 0?"":pendingCollection.get(0).getMakerName(),
				(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getMaker() == 0?"":approvedCollection.get(0).getMakerName(),
						(pendingCollection.get(0).getMaker() != approvedCollection.get(0).getMaker()));
		lResult.add(lMaker);
		ReviewResultVb lVerifier = new ReviewResultVb("Verifier",(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getVerifier() == 0?"":pendingCollection.get(0).getVerifierName(),
				(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getVerifier() == 0?"":approvedCollection.get(0).getVerifierName(),
						(pendingCollection.get(0).getVerifier() != approvedCollection.get(0).getVerifier()));
		lResult.add(lVerifier);
		ReviewResultVb lDateLastModified = new ReviewResultVb("Date Last Modified",(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getDateLastModified(),
				(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getDateLastModified(),
						(!pendingCollection.get(0).getDateLastModified().equals(approvedCollection.get(0).getDateLastModified())));
		lResult.add(lDateLastModified);
		ReviewResultVb lDateCreation = new ReviewResultVb("Date Creation",(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getDateCreation(),
				(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getDateCreation(),
						(!pendingCollection.get(0).getDateCreation().equals(approvedCollection.get(0).getDateCreation())));
		lResult.add(lDateCreation);
		
		return lResult;
	}

	public List<FeesConfigDetailsVb> getQueryResultsForReview(FeesConfigDetailsVb dObj, int intStatus){
		List<FeesConfigDetailsVb> collTemp = null;

		String fetchTableName ="";
		if(intStatus == Constants.STATUS_ZERO) {
			fetchTableName ="RA_MST_FEES_DETAIL";
		}else {
			fetchTableName ="RA_MST_FEES_DETAIL_PEND";
		}
		String query = "";
		try{	
			List<CurrencyDetailsVb> currencyPrecisionlst = commonDao.getCurrencyDecimals(false);
			query =" SELECT  COUNTRY,LE_BOOK,BUSINESS_LINE_ID,   "+
					"  "+dbFunctionFormats("TAppr.EFFECTIVE_DATE","DATETIME_FORMAT",null)+" EFFECTIVE_DATE,"+
					"  "+dbFunctionFormats("TAppr.RATE_EFFECTIVE_DATE","DATETIME_FORMAT",null)+" RATE_EFFECTIVE_DATE,PRODUCT_TYPE,PRODUCT_ID,TRAN_CCY, "+
					"  "+dbFunctionFormats("TAppr.RATE_EFFECTIVE_END_DATE","DATETIME_FORMAT",null)+" RATE_EFFECTIVE_END_DATE ,"+
					"  TAppr.FEE_AMT, "+
					"  "+dbFunctionFormats("TAppr.FEE_PERCENTAGE","NUM_FORMAT","5")+" FEE_PERCENTAGE, "+
					"  TAppr.MIN_FEE, "+
					"  TAppr.MAX_FEE, "+
					" CHANNEL_CODE, BUSINESS_VERTICAL, CONTRACT_ID,"+
					"  CUSTOMER_ID, POSTING_CCY, CCY_CONVERSION_TYPE, "+
					"  (SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB= 7027 AND ALPHA_SUB_TAB = TAppr.CHANNEL_CODE) CHANNEL_CODE_DESC,"+ 
					"  (SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB= 7019 AND ALPHA_SUB_TAB = TAppr.PRODUCT_TYPE) PRODUCT_TYPE_DESC,"+ 
					"  (SELECT PRODUCT_DESC FROM RA_PAR_PRODUCT WHERE PRODUCT_ID = TAppr.PRODUCT_ID) PRODUCT_ID_DESC,"+ 
					"  (SELECT CCY_DESCRIPTION FROM CURRENCIES WHERE CURRENCY = TAppr.TRAN_CCY) TRAN_CCY_DESC, "+
					"  (SELECT CCY_DESCRIPTION FROM CURRENCIES WHERE CURRENCY = TAppr.POSTING_CCY) POSTING_CCY_DESC,"+
					"  (SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB= 7040 "+
					" AND ALPHA_SUB_TAB = TAppr.BUSINESS_VERTICAL) BUSINESS_VERTICAL_DESC, "+ 
					"  (SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB= 1106  "+
					" AND ALPHA_SUB_TAB = TAppr.CCY_CONVERSION_TYPE) CCY_CONVERSION_TYPE_DESC, "+
					"  (SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB= 7032 "+
					" AND ALPHA_SUB_TAB = TAppr.FEE_BASIS) FEE_BASIS_DESC,  "+
					"  (SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB= 7071 "+ 
					" AND ALPHA_SUB_TAB = TAppr.LOOKUP_AMOUNT_TYPE) LOOKUP_AMOUNT_TYPE_DESC, "+
					"  (SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB= 7071  "+
					" AND ALPHA_SUB_TAB = TAppr.PERCENT_AMOUNT_TYPE) PERCENT_AMOUNT_TYPE_DESC, "+
					"  (SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB= 7042 "+
					" AND ALPHA_SUB_TAB = TAppr.CHANNEL_TYPE) CHANNEL_TYPE_DESC, "+
					"  (SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB= 7072 "+
					" AND ALPHA_SUB_TAB = TAppr.ROUND_OFF_BASIS) ROUND_OFF_BASIS_DESC, "+
					"  (SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB= 7032 "+ 
					" AND ALPHA_SUB_TAB = TAppr.MIN_MAX_CCY_TYPE) MIN_MAX_CCY_TYPE_DESC,"+customerNameDescAppr+contractNameDescAppr+" "+
					"  INT_BASIS, FEE_BASIS , Fees_Details_Attribute_1, Fees_Details_Attribute_2, "+
					" Fees_Details_Attribute_3,Fees_Details_Attribute_4,Fees_Details_Attribute_5, "+
					"  Fees_Details_Attribute_6, Fees_Details_Attribute_7,Fees_Details_Attribute_8,"+
					"  Fees_Details_Attribute_9, Fees_Details_Attribute_10,Fees_Details_Attribute_11,"+
					"  Fees_Details_Attribute_12, Fees_Details_Attribute_13,Fees_Details_Attribute_14,"+
					"  Fees_Details_Attribute_15, Fees_Details_Attribute_16,Fees_Details_Attribute_17,"+
					"  Fees_Details_Attribute_18, Fees_Details_Attribute_19,Fees_Details_Attribute_20,"
					+ " PERCENT_AMOUNT_TYPE , PERCENT_AMOUNT_TYPE_AT,"+ 
					"  LOOKUP_AMOUNT_TYPE, LOOKUP_AMOUNT_TYPE_AT,CHANNEL_TYPE,ROUND_OFF_BASIS,ROUND_OFF_DECIMAL,MIN_MAX_CCY_TYPE,"+
					" TAppr.Fee_Detail_Status,"
					+"  (SELECT NUM_SUBTAB_DESCRIPTION FROM NUM_SUB_TAB WHERE NUM_TAB= 1 "
					+ "AND NUM_SUB_TAB = TAppr.Fee_Detail_Status) Fee_Detail_Status_DESC," 
					+"  (SELECT NUM_SUBTAB_DESCRIPTION FROM NUM_SUB_TAB WHERE NUM_TAB= 7 "
					+ "AND NUM_SUB_TAB = TAppr.RECORD_INDICATOR) RECORD_INDICATOR_DESC,"
					+ "TAppr.RECORD_INDICATOR,                                                                                          "+
					" TAppr. MAKER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "+getDbFunction("NVL")+"(TAppr.MAKER,0) ) MAKER_NAME,                                  "+
					" TAppr.VERIFIER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "+getDbFunction("NVL")+"(TAppr.VERIFIER,0) ) VERIFIER_NAME,                          "+
					" "+getDbFunction("DATEFUNC")+"(TAppr.DATE_LAST_MODIFIED, '"+getDbFunction("DD_Mon_RRRR")+" "+getDbFunction("TIME")+"') DATE_LAST_MODIFIED ,"
					+ ""+getDbFunction("DATEFUNC")+"(TAppr.DATE_CREATION, '"+getDbFunction("DD_Mon_RRRR")+" "+getDbFunction("TIME")+"') DATE_CREATION ,"+pricingAppr+" TAppr.PRICING "+    
					" FROM "+fetchTableName+" TAppr  where "+
					" TAppr.COUNTRY = ?  "+
					" AND TAppr.LE_BOOK = ? "+ 
					" AND TAppr.BUSINESS_LINE_ID = ? "+
					" AND "+dbFunctionFormats("TAppr.EFFECTIVE_DATE","DATETIME_FORMAT",null)+" = ? "+ 
					" And "+dbFunctionFormats("RATE_EFFECTIVE_DATE","DATETIME_FORMAT",null)+" = ? "+
					" AND TAppr.PRODUCT_TYPE= ?         "+
					" AND TAppr.PRODUCT_ID= ?           "+
					" AND TAppr.CHANNEL_CODE= ?         "+
					" AND TAppr.BUSINESS_VERTICAL= ?    "+
					" AND TAppr.CONTRACT_ID= ?          "+
					" AND TAppr.CUSTOMER_ID= ?          "+
					" AND TAppr.CHANNEL_TYPE = ?        "+
					" AND TAppr.Fees_Details_Attribute_1= ? "+
					" AND TAppr.Fees_Details_Attribute_2= ? "+
					" AND TAppr.Fees_Details_Attribute_3= ? "+
					" AND TAppr.Fees_Details_Attribute_4= ? "+
					" AND TAppr.Fees_Details_Attribute_5= ? "+
					" AND TAppr.Fees_Details_Attribute_6= ? "+
					" AND TAppr.Fees_Details_Attribute_7= ? "+
					" AND TAppr.Fees_Details_Attribute_8= ? "+
					" AND TAppr.Fees_Details_Attribute_9= ? "+
					" AND TAppr.Fees_Details_Attribute_10= ? "+
					" AND TAppr.Fees_Details_Attribute_11= ? "+
					" AND TAppr.Fees_Details_Attribute_12= ? "+
					" AND TAppr.Fees_Details_Attribute_13= ? "+
					" AND TAppr.Fees_Details_Attribute_14= ? "+
					" AND TAppr.Fees_Details_Attribute_15= ? "+
					" AND TAppr.Fees_Details_Attribute_16= ? "+
					" AND TAppr.Fees_Details_Attribute_17= ? "+
					" AND TAppr.Fees_Details_Attribute_18= ? "+
					" AND TAppr.Fees_Details_Attribute_19= ? "+
					" AND TAppr.Fees_Details_Attribute_20= ? "+
					" And TAppr.Tran_CCY = ?  ";

		Object objParams[] = new Object[33];
		objParams[0] = new String(dObj.getCountry());
		objParams[1] = new String(dObj.getLeBook());
		objParams[2] = new String(dObj.getBusinessLineId());
		objParams[3] = new String(dObj.getEffectiveDate());
		objParams[4] = new String(dObj.getRateEffectiveDate());
		objParams[5] = new String(dObj.getProductType());
		objParams[6] = new String(dObj.getProductId());
		objParams[7] = new String(dObj.getChannelCode());
		objParams[8] = new String(dObj.getBusinessVertical());
		objParams[9] = new String(dObj.getContractId());
		objParams[10] = new String(dObj.getCustomerId());
		objParams[11] = new String(dObj.getChannelType());
		objParams[12] = new String(dObj.getFeesDetailsAttribute1());
		objParams[13] = new String(dObj.getFeesDetailsAttribute2());
		objParams[14] = new String(dObj.getFeesDetailsAttribute3());
		objParams[15] = new String(dObj.getFeesDetailsAttribute4());
		objParams[16] = new String(dObj.getFeesDetailsAttribute5());
		objParams[17] = new String(dObj.getFeesDetailsAttribute6());
		objParams[18] = new String(dObj.getFeesDetailsAttribute7());
		objParams[19] = new String(dObj.getFeesDetailsAttribute8());
		objParams[20] = new String(dObj.getFeesDetailsAttribute9());
		objParams[21] = new String(dObj.getFeesDetailsAttribute10());
		objParams[22] = new String(dObj.getFeesDetailsAttribute11());
		objParams[23] = new String(dObj.getFeesDetailsAttribute12());
		objParams[24] = new String(dObj.getFeesDetailsAttribute13());
		objParams[25] = new String(dObj.getFeesDetailsAttribute14());
		objParams[26] = new String(dObj.getFeesDetailsAttribute15());
		objParams[27] = new String(dObj.getFeesDetailsAttribute16());
		objParams[28] = new String(dObj.getFeesDetailsAttribute17());
		objParams[29] = new String(dObj.getFeesDetailsAttribute18());
		objParams[30] = new String(dObj.getFeesDetailsAttribute19());
		objParams[31] = new String(dObj.getFeesDetailsAttribute20());
		objParams[32] = new String(dObj.getTranCcy());
		collTemp = getJdbcTemplate().query(query,objParams,getFeesConfigDetailMapper(currencyPrecisionlst));
		return collTemp;
		}catch(Exception ex){
			logger.error("Error in get Query Main "+ex.getMessage());
			return null;
		}
	}
	public List<FeesConfigDetailsVb> getAllQueryforFeeDetails(FeesConfigHeaderVb dObj,int intStatus){
		List<FeesConfigDetailsVb> collTemp = null;
		String query = "";
		String pendQuery = "";
		String finalQuery = "";
		String maxEffectiveRateQuery = "";
		String strWhereNotExists = "";
		String pricing = "";
		String effectiveDate ="";
		Object objParams[]=null;
		try
		{	
			List<CurrencyDetailsVb> currencyPrecisionlst = commonDao.getCurrencyDecimals(false);
			String fetchTableName ="";
			/*if(!dObj.isVerificationRequired() || dObj.isReview()){intStatus =0;}
			if(intStatus == Constants.STATUS_ZERO) {
				fetchTableName ="RA_MST_FEES_DETAIL";
			}else {
				fetchTableName ="RA_MST_FEES_DETAIL_PEND";
			}*/
			if ("ORACLE".equalsIgnoreCase(databaseType)) {
				effectiveDate = dbFunctionFormats("SAPPR.EFFECTIVE_DATE","DATETIME_FORMAT",null)+" = ? ";
			}else if ("MSSQL".equalsIgnoreCase(databaseType)) {
				effectiveDate =" SAPPR.EFFECTIVE_DATE = ? ";
			}
			if(ValidationUtil.isValid(dObj.getPricing())) {
				pricing = " AND TAPPR.PRICING = ? ";
				objParams = new Object[10];
				objParams[0] = new String(dObj.getCountry());// country
				objParams[1] = new String(dObj.getLeBook());
				objParams[2] = new String(dObj.getBusinessLineId());
				objParams[3] = new String(dObj.getEffectiveDate());
				objParams[4] = new String(dObj.getPricing());
				objParams[5] = new String(dObj.getCountry());// country
				objParams[6] = new String(dObj.getLeBook());
				objParams[7] = new String(dObj.getBusinessLineId());
				objParams[8] = new String(dObj.getEffectiveDate());
				objParams[9] = new String(dObj.getPricing());
			}else {
				objParams = new Object[8];
				objParams[0] = new String(dObj.getCountry());// country
				objParams[1] = new String(dObj.getLeBook());
				objParams[2] = new String(dObj.getBusinessLineId());
				objParams[3] = new String(dObj.getEffectiveDate());
				objParams[4] = new String(dObj.getCountry());// country
				objParams[5] = new String(dObj.getLeBook());
				objParams[6] = new String(dObj.getBusinessLineId());
				objParams[7] = new String(dObj.getEffectiveDate());
			}
			fetchTableName ="RA_MST_FEES_DETAIL";
			maxEffectiveRateQuery = "SELECT SAPPR.COUNTRY,SAPPR.LE_BOOK,SAPPR.BUSINESS_LINE_ID, \n" + 
				"	SAPPR.EFFECTIVE_DATE," +
				"	MAX(SAPPR.RATE_EFFECTIVE_DATE) AS RATE_EFFECTIVE_DATE, " + 
				"   SAPPR.RATE_EFFECTIVE_END_DATE ,"+
				"	SAPPR.PRODUCT_TYPE,SAPPR.PRODUCT_ID, " + 
				"	SAPPR.CHANNEL_CODE,SAPPR.BUSINESS_VERTICAL,SAPPR.CONTRACT_ID,SAPPR.CUSTOMER_ID," + 
				" SAPPR.FEES_DETAILS_ATTRIBUTE_KEY," + 
				"	SAPPR.CHANNEL_TYPE,SAPPR.TRAN_CCY " +
				"   FROM "+fetchTableName+" SAPPR WHERE"+
				" 	SAPPR.Country = ? and SAPPR.LE_Book = ? and  SAPPR.Business_Line_ID =? \n" +
				"   And "+effectiveDate+" "+
				"   GROUP BY SAPPR.COUNTRY,SAPPR.LE_BOOK,SAPPR.BUSINESS_LINE_ID," + 
				"	SAPPR.EFFECTIVE_DATE,SAPPR.RATE_EFFECTIVE_END_DATE ," + 
				"	SAPPR.PRODUCT_TYPE,SAPPR.PRODUCT_ID," + 
				"	SAPPR.CHANNEL_CODE,SAPPR.BUSINESS_VERTICAL,SAPPR.CONTRACT_ID,SAPPR.CUSTOMER_ID," + 
				"SAPPR.FEES_DETAILS_ATTRIBUTE_KEY,SAPPR.CHANNEL_TYPE,SAPPR.TRAN_CCY "; 
			 
				query = " SELECT DISTINCT TAPPR.COUNTRY,TAPPR.LE_BOOK,TAPPR.BUSINESS_LINE_ID,  "+
						" "+dbFunctionFormats("TAPPR.EFFECTIVE_DATE","DATETIME_FORMAT",null)+" EFFECTIVE_DATE,"+
						" "+dbFunctionFormats("TAPPR.RATE_EFFECTIVE_DATE","DATETIME_FORMAT",null)+" RATE_EFFECTIVE_DATE,"+
						" "+dbFunctionFormats("TAPPR.RATE_EFFECTIVE_END_DATE","DATETIME_FORMAT",null)+" RATE_EFFECTIVE_END_DATE,"
						+ "TAPPR.PRODUCT_TYPE,TAPPR.PRODUCT_ID,TAPPR.TRAN_CCY, "
						+ " TAPPR.FEE_AMT, "  
						+ ""+dbFunctionFormats("TAPPR.FEE_PERCENTAGE","NUM_FORMAT","5")+" FEE_PERCENTAGE,"
						+"  TAPPR.MIN_FEE, "
						+"  TAPPR.MAX_FEE, "
						+ " TAPPR.CHANNEL_CODE, TAPPR.BUSINESS_VERTICAL, TAPPR.CONTRACT_ID, TAPPR.CUSTOMER_ID, TAPPR.POSTING_CCY, TAPPR.CCY_CONVERSION_TYPE, "
						+ ""+channelCodeDescAppr+productTypeDescAppr+productIdDescAppr+tranCcyDescAppr+postingCcyDescAppr+businessVerticalDescAppr+customerNameDescAppr+contractNameDescAppr+"" 
						+ ""+ccyConversionTypeDescAppr+feeBasisDescAppr+lookupDescAppr+percentDescAppr+channelTypeDescAppr+roundOffBasisDescAppr+minMaxCcyTypeAppr+""
						+ " TAPPR.INT_BASIS, TAPPR.FEE_BASIS , TAPPR.Fees_Details_Attribute_1, TAPPR.Fees_Details_Attribute_2, TAPPR.Fees_Details_Attribute_3,"
						+ "TAPPR.Fees_Details_Attribute_4,TAPPR.Fees_Details_Attribute_5,TAPPR.Fees_Details_Attribute_6," 
						+ " TAPPR.Fees_Details_Attribute_7,TAPPR.Fees_Details_Attribute_8,TAPPR.Fees_Details_Attribute_9,TAPPR.Fees_Details_Attribute_10,"
						+ " TAPPR.Fees_Details_Attribute_11,TAPPR.Fees_Details_Attribute_12,TAPPR.Fees_Details_Attribute_13,TAPPR.Fees_Details_Attribute_14,"
						+ " TAPPR.Fees_Details_Attribute_15,TAPPR.Fees_Details_Attribute_16,TAPPR.Fees_Details_Attribute_17,TAPPR.Fees_Details_Attribute_18,"
						+ " TAPPR.Fees_Details_Attribute_19,TAPPR.Fees_Details_Attribute_20,TAPPR.PERCENT_AMOUNT_TYPE ,"
						+ " TAPPR.PERCENT_AMOUNT_TYPE_AT, TAPPR.LOOKUP_AMOUNT_TYPE, TAPPR.LOOKUP_AMOUNT_TYPE_AT,TAPPR.CHANNEL_TYPE,"
						+ "TAPPR.ROUND_OFF_BASIS,TAPPR.ROUND_OFF_DECIMAL,TAPPR.MIN_MAX_CCY_TYPE,"+
						" TAPPR.Fee_Detail_Status,"
						+"  (SELECT NUM_SUBTAB_DESCRIPTION FROM NUM_SUB_TAB WHERE NUM_TAB= 1 "
						+ "AND NUM_SUB_TAB = TAPPR.Fee_Detail_Status) Fee_Detail_Status_DESC," 
						+"  (SELECT NUM_SUBTAB_DESCRIPTION FROM NUM_SUB_TAB WHERE NUM_TAB= 7 "
						+ "AND NUM_SUB_TAB = TAPPR.RECORD_INDICATOR) RECORD_INDICATOR_DESC,"
						+ "TAPPR.RECORD_INDICATOR,                                                                                          "+
						" TAPPR. MAKER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "+getDbFunction("NVL")+"(TAPPR.MAKER,0) ) MAKER_NAME,                                  "+
						" TAPPR.VERIFIER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "+getDbFunction("NVL")+"(TAPPR.VERIFIER,0) ) VERIFIER_NAME,                          "+
						" "+getDbFunction("DATEFUNC")+"(TAPPR.DATE_LAST_MODIFIED, '"+getDbFunction("DD_Mon_RRRR")+" "+getDbFunction("TIME")+"') DATE_LAST_MODIFIED ,"
						+ ""+getDbFunction("DATEFUNC")+"(TAPPR.DATE_CREATION, '"+getDbFunction("DD_Mon_RRRR")+" "+getDbFunction("TIME")+"') DATE_CREATION,"+pricingAppr+"TAPPR.PRICING "+                                                                                        
						" FROM "+fetchTableName+" TAPPR  Inner Join ("+maxEffectiveRateQuery+") S1 ON "+
						 " TAPPR.Business_Line_ID = S1.Business_Line_ID " + 
						" AND TAPPR.COUNTRY = S1.COUNTRY AND TAPPR.LE_BOOK = S1.LE_BOOK " + 
						" AND TAPPR.EFFECTIVE_DATE = S1.EFFECTIVE_DATE "+
						" AND TAPPR.RATE_EFFECTIVE_DATE = S1.RATE_EFFECTIVE_DATE "+
						" AND TAPPR.RATE_EFFECTIVE_END_DATE = S1.RATE_EFFECTIVE_END_DATE "+
						" AND S1.PRODUCT_TYPE =TAPPR.PRODUCT_TYPE                          "+
						" AND S1.PRODUCT_ID =TAPPR.PRODUCT_ID                              "+
						" AND S1.CHANNEL_CODE =TAPPR.CHANNEL_CODE                          "+
						" AND S1.BUSINESS_VERTICAL =TAPPR.BUSINESS_VERTICAL                "+
						" AND S1.CONTRACT_ID =TAPPR.CONTRACT_ID                            "+
						" AND S1.CUSTOMER_ID =TAPPR.CUSTOMER_ID                            "+
						" AND S1.CHANNEL_TYPE=TAPPR.CHANNEL_TYPE                           "+
						" AND S1.TRAN_CCY= TAPPR.TRAN_CCY "+
						" AND S1.FEES_DETAILS_ATTRIBUTE_KEY= TAPPR.FEES_DETAILS_ATTRIBUTE_KEY"+
						" "+pricing+" ";
				
				
			strWhereNotExists = new String(" where Not Exists (Select 'X' From RA_MST_FEES_DETAIL_PEND TPEND WHERE TAppr.COUNTRY = TPend.COUNTRY"
					+ " AND TAppr.LE_BOOK = TPend.LE_BOOK  AND TAPPR.BUSINESS_LINE_ID = TPEND.BUSINESS_LINE_ID"
					+ " AND TAPPR.EFFECTIVE_DATE = TPEND.EFFECTIVE_DATE"
 					+ " AND TAPPR.CHANNEL_CODE = TPEND.CHANNEL_CODE "
					+ " AND TAPPR.CHANNEL_TYPE = TPEND.CHANNEL_TYPE "
					+ " AND TAPPR.BUSINESS_VERTICAL = TPEND.BUSINESS_VERTICAL "
					+ " AND TAPPR.CONTRACT_ID = TPEND.CONTRACT_ID "
					+ " AND TAPPR.CUSTOMER_ID = TPEND.CUSTOMER_ID "
					+ " AND TAPPR.PRODUCT_TYPE = TPEND.PRODUCT_TYPE "
					+ " AND TAPPR.PRODUCT_ID = TPEND.PRODUCT_ID "
					//+ " AND TAPPR.TRAN_CCY = TPEND.TRAN_CCY "
					+ " AND TAPPR.TRAN_CCY = TPEND.TRAN_CCY "
					+ " AND TAPPR.FEES_DETAILS_ATTRIBUTE_KEY =TPEND.FEES_DETAILS_ATTRIBUTE_KEY "
					+ " AND TAPPR.RATE_EFFECTIVE_DATE = TPEND.RATE_EFFECTIVE_DATE)");
			String orderBy = " ORDER BY PRICING DESC";
			finalQuery = query;
			pendQuery = query.replaceAll("RA_MST_FEES_DETAIL", "RA_MST_FEES_DETAIL_PEND");
			pendQuery = pendQuery.replaceAll("TAPPR", "TPEND");
			pendQuery = pendQuery.replaceAll("SAPPR", "SPEND");
			finalQuery = finalQuery+strWhereNotExists+" UNION "+pendQuery+orderBy;
			
			/*if (dObj.getSmartSearchOpt() != null && dObj.getSmartSearchOpt().size() > 0) {
				int count = 1;
				for (SmartSearchVb data: dObj.getSmartSearchOpt()){
					if(count == dObj.getSmartSearchOpt().size()) {
						data.setJoinType("");
					} else {
						if(!ValidationUtil.isValid(data.getJoinType()) && !("AND".equalsIgnoreCase(data.getJoinType()) || "OR".equalsIgnoreCase(data.getJoinType()))) {
							data.setJoinType("AND");
						}
					}
					String val = CommonUtils.criteriaBasedVal(data.getCriteria(), data.getValue());
					switch (data.getObject()) {
					case "channelCodeDesc":
						CommonUtils.addToQuerySearch(" (upper(t1.CHANNEL_CODE_DESC) "+ val+ "OR upper(t1.CHANNEL_CODE) "+ val+")" , query, data.getJoinType());
						break;
						
					case "channelTypeDesc":
						CommonUtils.addToQuerySearch(" (upper(t1.CHANNEL_TYPE_DESC) "+ val+ "OR upper(t1.CHANNEL_TYPE) "+ val+")", query, data.getJoinType());
						break;

					case "customerId":
						CommonUtils.addToQuerySearch(" (upper(t1.CUSTOMER_ID) "+ val+ "OR upper(t1.CUSTOMER_ID_DESC) "+ val+")", query, data.getJoinType());
						break;
						
					case "businessVerticalDesc":
						CommonUtils.addToQuerySearch(" upper(t1.BUSINESS_VERTICAL_DESC) "+ val, query, data.getJoinType());
						break;
						
					case "contractId":
						CommonUtils.addToQuerySearch(" (upper(t1.CONTRACT_ID) "+ val+ "OR upper(t1.CONTRACT_ID_DESC) "+ val+")", query, data.getJoinType());
						break;
						
					case "productIdDesc":
						CommonUtils.addToQuerySearch(" (upper(t1.PRODUCT_ID) "+ val+ "OR upper(t1.PRODUCT_ID_DESC) "+ val+")", query, data.getJoinType());
						break;
						
					case "productTypeDesc":
						CommonUtils.addToQuerySearch(" (upper(t1.PRODUCT_TYPE) "+ val+ "OR upper(t1.PRODUCT_TYPE_DESC) "+ val+")", query, data.getJoinType());
						break;
						
					case "feesDetailsAttribute1":
						CommonUtils.addToQuerySearch(" upper(t1.FEES_DETAILS_ATTRIBUTE_1) "+ val, query, data.getJoinType());
						break;
						
					case "feesDetailsAttribute2":
						CommonUtils.addToQuerySearch(" upper(t1.FEES_DETAILS_ATTRIBUTE_2) "+ val, query, data.getJoinType());
						break;
						
					case "feesDetailsAttribute3":
						CommonUtils.addToQuerySearch(" upper(t1.FEES_DETAILS_ATTRIBUTE_3) "+ val, query, data.getJoinType());
						break;
						
					case "feesDetailsAttribute4":
						CommonUtils.addToQuerySearch(" upper(t1.FEES_DETAILS_ATTRIBUTE_4) "+ val, query, data.getJoinType());
						break;
						
					case "feesDetailsAttribute5":
						CommonUtils.addToQuerySearch(" upper(t1.FEES_DETAILS_ATTRIBUTE_5) "+ val, query, data.getJoinType());
						break;
						
					case "feesDetailsAttribute6":
						CommonUtils.addToQuerySearch(" upper(t1.FEES_DETAILS_ATTRIBUTE_6) "+ val, query, data.getJoinType());
						break;
						
					case "feesDetailsAttribute7":
						CommonUtils.addToQuerySearch(" upper(t1.FEES_DETAILS_ATTRIBUTE_7) "+ val, query, data.getJoinType());
						break;
						
					case "feesDetailsAttribute8":
						CommonUtils.addToQuerySearch(" upper(t1.FEES_DETAILS_ATTRIBUTE_8) "+ val, query, data.getJoinType());
						break;
						
					case "tranCcy":
						CommonUtils.addToQuerySearch("(upper(t1.TRAN_CCY) "+ val+ "OR  upper(t1.TRAN_CCY_DESC) "+ val+")", query, data.getJoinType());
						break;
						
					case "postingCcy":
						CommonUtils.addToQuerySearch(" (upper(t1.POSTING_CCY) "+ val+ "OR  upper(t1.POSTING_CCY_DESC) "+ val+")", query, data.getJoinType());
						break;
						
					case "ccyConversionTypeDesc":
						CommonUtils.addToQuerySearch(" (upper(t1.CCY_CONVERSION_TYPE) "+ val+ "OR upper(t1.CCY_CONVERSION_TYPE_DESC) "+ val+")", query, data.getJoinType());
						break;
						
					case "lookupAmountTypeDesc":
						CommonUtils.addToQuerySearch(" (upper(t1.LOOKUP_AMOUNT_TYPE) "+ val+ "OR upper(t1.LOOKUP_AMOUNT_TYPE_DESC) "+ val+")", query, data.getJoinType());
						break;
						
					case "percentAmountTypeDesc":
						CommonUtils.addToQuerySearch(" (upper(t1.PERCENT_AMOUNT_TYPE) "+ val+ "OR upper(t1.PERCENT_AMOUNT_TYPE_DESC) "+ val+")", query, data.getJoinType());
						break;
						
					case "roundOffBasisDesc":
						CommonUtils.addToQuerySearch(" (upper(t1.ROUND_OFF_BASIS) "+ val+ "OR upper(t1.ROUND_OFF_BASIS_DESC) "+ val+")", query, data.getJoinType());
						break;
						
					case "interestBasis":
						CommonUtils.addToQuerySearch(" upper(t1.INT_BASIS) "+ val, query, data.getJoinType());
						break;
						
					case "roundOffBasisDecimal":
						CommonUtils.addToQuerySearch(" upper(t1.ROUND_OFF_DECIMAL) "+ val, query, data.getJoinType());
						break;
						
					case "minMaxCcyTypeDesc":
						CommonUtils.addToQuerySearch(" (upper(t1.MIN_MAX_CCY_TYPE) "+ val+ "OR upper(t1.MIN_MAX_CCY_TYPE_DESC) "+ val+")", query, data.getJoinType());
						break;
						
					case "feesBasisDesc":
						CommonUtils.addToQuerySearch("( upper(t1.FEE_BASIS) "+ val+ "OR upper(t1.FEE_BASIS_DESC) "+ val+")", query, data.getJoinType());
						break;
						
					case "feeConfigStatusDesc":
						CommonUtils.addToQuerySearch(" upper(FEE_DETAIL_STATUS_DESC) "+ val, query, data.getJoinType());
						break;
						
					case "feeConfigRecordIndicatorDesc":
						CommonUtils.addToQuerySearch(" upper(RECORD_INDICATOR_DESC) "+ val, query, data.getJoinType());
						break;
						
					case "dateCrean":
						CommonUtils.addToQuerySearch(" "+getDbFunction("DATEFUNC")+"(DATE_CREATION,'DD-MM-YYYY "+getDbFunction("TIME")+"') " + val, query, data.getJoinType());
						break;
									
					case "dateLastModified":
						CommonUtils.addToQuerySearch(" "+getDbFunction("DATEFUNC")+"(DATE_LAST_MODIFIED,'DD-MM-YYYY "+getDbFunction("TIME")+"') "  + val, query, data.getJoinType());
						break;

					case "makerName":
						CommonUtils.addToQuerySearch(" (MAKER_NAME) IN ("+ val+") ", query, data.getJoinType());
						break;

					default:
					}
					count++;
				}
			}*/
		//	String orderBy = " ORDER BY DATE_LAST_MODIFIED_1 DESC ";
			collTemp = getJdbcTemplate().query(finalQuery,objParams,getFeesConfigDetailMapper(currencyPrecisionlst));
			return collTemp;
			//return getQueryPopupResults(dObj,null, query, "", orderBy, params,getFeesConfigDetailMapper());
		}catch(Exception ex){
			ex.printStackTrace();
			logger.error("Exception ");
			return null;
		}
	}

	public LinkedHashMap<String,String> getFeesConfigPriority(FeesConfigDetailsVb dObj) {
		String effectiveDate = "";
		if (databaseType.equalsIgnoreCase("ORACLE")) {
			effectiveDate = "TO_DATE(?,'DD-MM-RRRR HH24:MI:SS')";
		}else {
			effectiveDate = "?";
		} 
		String query =   "    SELECT * FROM ( SELECT  		  																		"				
				+"  "+getDbFunction("NVL", null)+"(TT1.DIMENSION_NAME,TT2.DIMENSION_NAME) DIMENSION_NAME,       	"                          
				+"  "+getDbFunction("NVL", null)+"(TT1.COLUMN_ALIAS,TT2.COLUMN_ALIAS) COLUMN_ALIAS,              	"
				+"  "+getDbFunction("NVL", null)+"(TT1.COUNTRY,TT2.COUNTRY) COUNTRY ,             	"  
				+"  "+getDbFunction("NVL", null)+"(TT1.LE_BOOK,TT2.LE_BOOK) LE_BOOK              	"  
				+"  FROM                                                                							"                          
				+"  (SELECT T1.COUNTRY, T1.LE_BOOK, T1.DIMENSION_NAME DIMENSION_NAME, T1.COLUMN_ALIAS COLUMN_ALIAS  "
				+"  FROM RA_MST_FEES_DETAIL_PRIORITY T1   															"
				+"  WHERE T1.COUNTRY = ?                															"                    
				+"  AND T1.LE_BOOK = ?                   															"                                                          
				+"  AND T1.BUSINESS_LINE_ID = ?																		"
				+"  and t1.EFFECTIVE_DATE = (select max(H1.EFFECTIVE_DATE) from RA_MST_FEES_DETAIL_PRIORITY H1   	"
				+"  WHERE H1.COUNTRY = t1.country                                    								"
				+"  AND H1.LE_BOOK = t1.le_book                                      								"                              
				+"  AND H1.BUSINESS_LINE_ID = t1.BUSINESS_LINE_ID					 								"
				+"  and H1.DIMENSION_NAME = t1.DIMENSION_NAME						 								"
				+"  and H1.EFFECTIVE_DATE <= "+effectiveDate+" ) ) TT1                               								"                                
				+"  FULL OUTER JOIN  RA_MST_FEES_DEFAULT_PRIORITY TT2 ON (			 								"
				+"  TT1.COUNTRY=TT2.COUNTRY AND										 								"
				+"  TT1.LE_BOOK=TT2.LE_BOOK AND										 								"
				+"  TT1.DIMENSION_NAME=TT2.DIMENSION_NAME)    						 								"
				+"  WHERE  "+getDbFunction("NVL", null)+"(TT1.DIMENSION_NAME,TT2.DIMENSION_NAME) LIKE 'FEES_DETAILS_ATTRIBUTE_%'  "
				+ " ) S1 WHERE COUNTRY = ?  AND LE_BOOK = ?  "
				+"  ORDER BY DIMENSION_NAME				";                                                                      
		Object objParams[]=new Object[6];
		objParams[0] = dObj.getCountry();
		objParams[1] = dObj.getLeBook();
		objParams[2] = dObj.getBusinessLineId();
		objParams[3] = dObj.getEffectiveDate();
		objParams[4] = dObj.getCountry();
		objParams[5] = dObj.getLeBook();
		
		LinkedHashMap<String,String> resultData = new LinkedHashMap<String,String>();
		ResultSetExtractor mapper = new ResultSetExtractor() {
			public Object extractData(ResultSet rs)  throws SQLException, DataAccessException {
				while(rs.next()){
					resultData.put(rs.getString(1).trim(), rs.getString(2));
				}
				return resultData;
			}
		};
		return (LinkedHashMap<String,String>)getJdbcTemplate().query(query,objParams, mapper);
	}
	private FeesConfigDetailsVb validateFeeAttribute(FeesConfigDetailsVb dObj) {
		if(!ValidationUtil.isValid(dObj.getFeesDetailsAttribute1())){dObj.setFeesDetailsAttribute1("ALL");}
		if(!ValidationUtil.isValid(dObj.getFeesDetailsAttribute2())){dObj.setFeesDetailsAttribute2("ALL");}
		if(!ValidationUtil.isValid(dObj.getFeesDetailsAttribute3())){dObj.setFeesDetailsAttribute3("ALL");}
		if(!ValidationUtil.isValid(dObj.getFeesDetailsAttribute4())){dObj.setFeesDetailsAttribute4("ALL");}
		if(!ValidationUtil.isValid(dObj.getFeesDetailsAttribute5())){dObj.setFeesDetailsAttribute5("ALL");}
		if(!ValidationUtil.isValid(dObj.getFeesDetailsAttribute6())){dObj.setFeesDetailsAttribute6("ALL");}
		if(!ValidationUtil.isValid(dObj.getFeesDetailsAttribute7())){dObj.setFeesDetailsAttribute7("ALL");}
		if(!ValidationUtil.isValid(dObj.getFeesDetailsAttribute8())){dObj.setFeesDetailsAttribute8("ALL");}
		if(!ValidationUtil.isValid(dObj.getFeesDetailsAttribute9())){dObj.setFeesDetailsAttribute9("ALL");}
		if(!ValidationUtil.isValid(dObj.getFeesDetailsAttribute10())){dObj.setFeesDetailsAttribute10("ALL");}
		if(!ValidationUtil.isValid(dObj.getFeesDetailsAttribute11())){dObj.setFeesDetailsAttribute11("ALL");}
		if(!ValidationUtil.isValid(dObj.getFeesDetailsAttribute12())){dObj.setFeesDetailsAttribute12("ALL");}
		if(!ValidationUtil.isValid(dObj.getFeesDetailsAttribute13())){dObj.setFeesDetailsAttribute13("ALL");}
		if(!ValidationUtil.isValid(dObj.getFeesDetailsAttribute14())){dObj.setFeesDetailsAttribute14("ALL");}
		if(!ValidationUtil.isValid(dObj.getFeesDetailsAttribute15())){dObj.setFeesDetailsAttribute15("ALL");}
		if(!ValidationUtil.isValid(dObj.getFeesDetailsAttribute16())){dObj.setFeesDetailsAttribute16("ALL");}
		if(!ValidationUtil.isValid(dObj.getFeesDetailsAttribute17())){dObj.setFeesDetailsAttribute17("ALL");}
		if(!ValidationUtil.isValid(dObj.getFeesDetailsAttribute18())){dObj.setFeesDetailsAttribute18("ALL");}
		if(!ValidationUtil.isValid(dObj.getFeesDetailsAttribute19())){dObj.setFeesDetailsAttribute19("ALL");}
		if(!ValidationUtil.isValid(dObj.getFeesDetailsAttribute20())){dObj.setFeesDetailsAttribute20("ALL");}
		return dObj;
	}
}
