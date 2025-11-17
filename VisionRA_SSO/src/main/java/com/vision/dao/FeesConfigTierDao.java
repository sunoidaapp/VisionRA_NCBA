package com.vision.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import com.vision.vb.FeesConfigTierVb;

@Component
public class FeesConfigTierDao extends AbstractDao<FeesConfigTierVb> {
	@Autowired
	CommonDao commonDao;
	
	@Value("${app.databaseType}")
	private String databaseType;
	protected RowMapper getFeesConfigTierMapper(FeesConfigDetailsVb detailVb ,List<CurrencyDetailsVb> currencylst){
		RowMapper mapper = new RowMapper() {
			@Override
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				FeesConfigTierVb vObject = new FeesConfigTierVb();
				vObject.setCountry(rs.getString("COUNTRY"));
				vObject.setLeBook(rs.getString("LE_BOOK"));
				vObject.setBusinessLineId(rs.getString("BUSINESS_LINE_ID"));
				vObject.setEffectiveDate(rs.getString("EFFECTIVE_DATE"));
				vObject.setRateEffectiveDate(rs.getString("RATE_EFFECTIVE_DATE"));
				vObject.setRateEffectiveEndDate(rs.getString("RATE_EFFECTIVE_END_DATE"));
				vObject.setFeeSequence(rs.getInt("FEE_SEQUENCE"));
				vObject.setTranCcy(rs.getString("TRAN_CCY"));
				vObject.setAmtFrom(rs.getString("AMT_FROM"));
				vObject.setAmtTo(rs.getString("AMT_TO"));
				vObject.setCountFrom(rs.getString("CNT_FROM"));
				vObject.setCountTo(rs.getString("CNT_TO"));
				vObject.setFeeBasis(rs.getString("FEE_BASIS"));
				vObject.setFeeAmt(rs.getString("FEE_AMT"));
				vObject.setFeePercentage(rs.getString("FEE_PERCENTAGE"));
				
				ExceptionCode exceptionCode = commonDao.setDecimalPrecision(currencylst,detailVb,vObject,"FEE_TIER");
				if(exceptionCode.getErrorCode() == Constants.SUCCESSFUL_OPERATION)
					vObject= (FeesConfigTierVb)exceptionCode.getResponse();
				
				return vObject;
			}
		};
		return mapper;
	}
	protected RowMapper getMainMapper(FeesConfigDetailsVb detailVb ,List<CurrencyDetailsVb> currencylst){
		RowMapper mapper = new RowMapper() {
			@Override
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				FeesConfigTierVb vObject = new FeesConfigTierVb();
				vObject.setCountry(rs.getString("COUNTRY"));
				vObject.setLeBook(rs.getString("LE_BOOK"));
				vObject.setBusinessLineId(rs.getString("BUSINESS_LINE_ID"));
				vObject.setEffectiveDate(rs.getString("EFFECTIVE_DATE"));
				vObject.setRateEffectiveDate(rs.getString("RATE_EFFECTIVE_DATE"));
				vObject.setRateEffectiveEndDate(rs.getString("RATE_EFFECTIVE_END_DATE"));
				vObject.setProductType(rs.getString("PRODUCT_TYPE"));
				vObject.setProductId(rs.getString("PRODUCT_ID"));
				vObject.setTranCcy(rs.getString("TRAN_CCY"));
				vObject.setChannelCode(rs.getString("CHANNEL_CODE"));
				vObject.setBusinessVertical(rs.getString("BUSINESS_VERTICAL"));
				vObject.setContractId(rs.getString("CONTRACT_ID"));
				vObject.setCustomerId(rs.getString("CUSTOMER_ID"));
				vObject.setFeeBasis(rs.getString("FEE_BASIS"));
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
				vObject.setFeeSequence(rs.getInt("FEE_SEQUENCE"));
				vObject.setAmtFrom(rs.getString("AMT_FROM"));
				vObject.setAmtTo(rs.getString("AMT_TO"));
				vObject.setCountFrom(rs.getString("CNT_FROM"));
				vObject.setCountTo(rs.getString("CNT_TO"));
				vObject.setFeeAmt(rs.getString("FEE_AMT"));
				vObject.setFeePercentage(rs.getString("FEE_PERCENTAGE"));
				vObject.setFeeTierStatus(rs.getInt("Fee_Tier_Status"));
				vObject.setRecordIndicator(rs.getInt("RECORD_INDICATOR"));
				vObject.setMaker(rs.getInt("MAKER"));
				vObject.setVerifier(rs.getInt("VERIFIER"));
				vObject.setDateLastModified(rs.getString("DATE_LAST_MODIFIED"));
				vObject.setDateCreation(rs.getString("DATE_CREATION"));
				vObject.setPricing(rs.getString("PRICING"));
				ExceptionCode exceptionCode = commonDao.setDecimalPrecision(currencylst,detailVb,vObject,"FEE_TIER");
				if(exceptionCode.getErrorCode() == Constants.SUCCESSFUL_OPERATION)
					vObject= (FeesConfigTierVb)exceptionCode.getResponse();
				
				return vObject;
			}
		};
		return mapper;
	}
	@Override
	protected void setServiceDefaults() {
		serviceName = "FeesConfigTier";
		serviceDesc = "Fees Config Tier";
		tableName = "RA_MST_FEES_TIER";
		childTableName = "RA_MST_FEES_TIER";
		intCurrentUserId = CustomContextHolder.getContext().getVisionId();
	}
	public List<FeesConfigTierVb> getQueryResultsMain(FeesConfigDetailsVb dObj,int status){
		List<FeesConfigTierVb> collTemp = null;
		String query = "";
		String tableName = "RA_MST_FEES_TIER";
		if(status == 1) {
			tableName = "RA_MST_FEES_TIER_PEND";
		}
		List<CurrencyDetailsVb> currencyPrecisionlst = commonDao.getCurrencyDecimals(false);
		String effectiveDateAppr="";
		if(databaseType.equalsIgnoreCase("ORACLE")) {
			effectiveDateAppr =" AND TO_DATE(EFFECTIVE_DATE,'DD-MM-RRRR HH24:MI:SS') = TO_DATE(?,'DD-MM-RRRR HH24:MI:SS') "
					     + " AND TO_DATE(RATE_EFFECTIVE_DATE,'DD-MM-RRRR HH24:MI:SS') = TO_DATE(?,'DD-MM-RRRR HH24:MI:SS')";
		} else{
			effectiveDateAppr =" AND EFFECTIVE_DATE = ? AND RATE_EFFECTIVE_DATE = ? ";
		}
		try
		{	
		
		query = " Select * from  "+tableName+" where Country = ? and LE_Book = ? and  Business_Line_ID =? " +
				effectiveDateAppr+ 
						" AND PRODUCT_TYPE= ?         "+
						" AND PRODUCT_ID= ?           "+
						" AND TRAN_CCY = ?            "+
						" AND CHANNEL_CODE= ?         "+
						" AND BUSINESS_VERTICAL= ?    "+
						" AND CONTRACT_ID= ?          "+
						" AND CUSTOMER_ID= ?          "+
						" AND CHANNEL_TYPE = ?        "+
						" AND Fees_Details_Attribute_1= ? "+
						" AND Fees_Details_Attribute_2= ? "+
						" AND Fees_Details_Attribute_3= ? "+
						" AND Fees_Details_Attribute_4= ? "+
						" AND Fees_Details_Attribute_5= ? "+
						" AND Fees_Details_Attribute_6= ? "+
						" AND Fees_Details_Attribute_7= ? "+
						" AND Fees_Details_Attribute_8= ? "+
						" AND Fees_Details_Attribute_9= ? "+
						" AND Fees_Details_Attribute_10= ? "+
						" AND Fees_Details_Attribute_11= ? "+
						" AND Fees_Details_Attribute_12= ? "+
						" AND Fees_Details_Attribute_13= ? "+
						" AND Fees_Details_Attribute_14= ? "+
						" AND Fees_Details_Attribute_15= ? "+
						" AND Fees_Details_Attribute_16= ? "+
						" AND Fees_Details_Attribute_17= ? "+
						" AND Fees_Details_Attribute_18= ? "+
						" AND Fees_Details_Attribute_19= ? "+
						" AND Fees_Details_Attribute_20= ? ";;

//			Object objParams[] = new Object[4];
//			objParams[0] = new String(dObj.getCountry());// country
//			objParams[1] = new String(dObj.getLeBook());
//			objParams[2] = new String(dObj.getBusinessLineId());
//			objParams[3] = new String(dObj.getEffectiveDate());
						Object objParams[] = new Object[33];
						objParams[0] = new String(dObj.getCountry());
						objParams[1] = new String(dObj.getLeBook());
						objParams[2] = new String(dObj.getBusinessLineId());
						objParams[3] = new String(dObj.getEffectiveDate());
						objParams[4] = new String(dObj.getRateEffectiveDate());
						objParams[5] = new String(dObj.getProductType());
					 	objParams[6] = new String(dObj.getProductId());
					 	objParams[7] = new String(dObj.getTranCcy());
						objParams[8] = new String(dObj.getChannelCode());
						objParams[9] = new String(dObj.getBusinessVertical());
						objParams[10] = new String(dObj.getContractId());
						objParams[11] = new String(dObj.getCustomerId());
						objParams[12] = new String(dObj.getChannelType());
						objParams[13] = new String(dObj.getFeesDetailsAttribute1());
						objParams[14] = new String(dObj.getFeesDetailsAttribute2());
						objParams[15] = new String(dObj.getFeesDetailsAttribute3());
						objParams[16] = new String(dObj.getFeesDetailsAttribute4());
						objParams[17] = new String(dObj.getFeesDetailsAttribute5());
						objParams[18] = new String(dObj.getFeesDetailsAttribute6());
						objParams[19] = new String(dObj.getFeesDetailsAttribute7());
						objParams[20] = new String(dObj.getFeesDetailsAttribute8());
						objParams[21] = new String(dObj.getFeesDetailsAttribute9());
						objParams[22] = new String(dObj.getFeesDetailsAttribute10());
						objParams[23] = new String(dObj.getFeesDetailsAttribute11());
						objParams[24] = new String(dObj.getFeesDetailsAttribute12());
						objParams[25] = new String(dObj.getFeesDetailsAttribute13());
						objParams[26] = new String(dObj.getFeesDetailsAttribute14());
						objParams[27] = new String(dObj.getFeesDetailsAttribute15());
						objParams[28] = new String(dObj.getFeesDetailsAttribute16());
						objParams[29] = new String(dObj.getFeesDetailsAttribute17());
						objParams[30] = new String(dObj.getFeesDetailsAttribute18());
						objParams[31] = new String(dObj.getFeesDetailsAttribute19());
						objParams[32] = new String(dObj.getFeesDetailsAttribute20());
			collTemp = getJdbcTemplate().query(query,objParams,getMainMapper(dObj,currencyPrecisionlst));
			return collTemp;
		}catch(Exception ex){
			logger.error("Error in get Query Main "+ex.getMessage());
			return null;
		}
	}
	public List<FeesConfigTierVb> getQueryResults(FeesConfigDetailsVb dObj,int intStatus){
		List<CurrencyDetailsVb> currencyPrecisionlst = commonDao.getCurrencyDecimals(false);
		List<FeesConfigTierVb> collTemp = null;
		String query = "";
		String effectiveDateAppr="";
		String effectiveDatePend="";
		if(databaseType.equalsIgnoreCase("ORACLE")) {
			effectiveDateAppr =" AND TO_DATE(TAppr.EFFECTIVE_DATE,'DD-MM-RRRR HH24:MI:SS') = TO_DATE(?,'DD-MM-RRRR HH24:MI:SS') "
					     + " AND TO_DATE(TAppr.RATE_EFFECTIVE_DATE,'DD-MM-RRRR HH24:MI:SS') = TO_DATE(?,'DD-MM-RRRR HH24:MI:SS')";
			effectiveDatePend = " AND TO_DATE(TPend.EFFECTIVE_DATE,'DD-MM-RRRR HH24:MI:SS') = TO_DATE(?,'DD-MM-RRRR HH24:MI:SS') "
				     + " AND TO_DATE(TPend.RATE_EFFECTIVE_DATE,'DD-MM-RRRR HH24:MI:SS') = TO_DATE(?,'DD-MM-RRRR HH24:MI:SS')";
		} else{
			effectiveDateAppr =" AND TAppr.EFFECTIVE_DATE = ? AND TAppr.RATE_EFFECTIVE_DATE = ? ";
			effectiveDatePend =" AND TPend.EFFECTIVE_DATE = ? AND TPend.RATE_EFFECTIVE_DATE = ? ";
		}
		try
		{	
			//if(!dObj.isVerificationRequired() || dObj.isReview()){intStatus =0;}
			if(intStatus == Constants.STATUS_ZERO) {
				query = " SELECT  TAPPR.COUNTRY,TAPPR.LE_BOOK,TAPPR.BUSINESS_LINE_ID,  "+
						" "+dbFunctionFormats("TAPPR.EFFECTIVE_DATE","DATETIME_FORMAT",null)+" EFFECTIVE_DATE, "+
						" "+dbFunctionFormats("TAPPR.RATE_EFFECTIVE_DATE","DATETIME_FORMAT",null)+" RATE_EFFECTIVE_DATE, "+
						" "+dbFunctionFormats("TAPPR.RATE_EFFECTIVE_END_DATE","DATETIME_FORMAT",null)+" RATE_EFFECTIVE_END_DATE, "+
						" TAPPR.FEE_SEQUENCE,TAPPR.FEE_BASIS, "+
						" TAPPR.AMT_FROM, "+
						" TAPPR.AMT_TO, "+
						" TAPPR.FEE_AMT, "+
						" TAPPR.CNT_FROM,TAPPR.CNT_TO,"+
						"  "+dbFunctionFormats("TAppr.FEE_PERCENTAGE","NUM_FORMAT","5")+" FEE_PERCENTAGE, "+
						" TAPPR.TRAN_CCY  "+
						" FROM RA_MST_FEES_TIER TAPPR"
						+ " WHERE TAppr.COUNTRY = ?  "+
						" AND TAppr.LE_BOOK = ? "+ 
						" AND TAppr.BUSINESS_LINE_ID = ? "+
						effectiveDateAppr+ 
						" AND TAppr.PRODUCT_TYPE= ?         "+
						" AND TAppr.PRODUCT_ID= ?           "+
						" AND TAppr.TRAN_CCY = ?            "+
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
						" AND TAppr.Fees_Details_Attribute_20= ? ";
			}else {
				query = " SELECT  TPend.COUNTRY,TPend.LE_BOOK,TPend.BUSINESS_LINE_ID,  "+
						" "+dbFunctionFormats("TPend.EFFECTIVE_DATE","DATETIME_FORMAT",null)+" EFFECTIVE_DATE, "+
						" "+dbFunctionFormats("TPend.RATE_EFFECTIVE_DATE","DATETIME_FORMAT",null)+" RATE_EFFECTIVE_DATE, "+
						" "+dbFunctionFormats("TPend.RATE_EFFECTIVE_END_DATE","DATETIME_FORMAT",null)+" RATE_EFFECTIVE_END_DATE, "+
						" TPend.FEE_SEQUENCE, TPend.FEE_BASIS,"+
						" TPend.AMT_FROM, "+
						" TPend.AMT_TO, "+
						" TPend.FEE_AMT, "+
						" TPend.CNT_FROM,TPend.CNT_TO,TPend.TRAN_CCY,  "+
						"  "+dbFunctionFormats("TPend.FEE_PERCENTAGE","NUM_FORMAT","5")+" FEE_PERCENTAGE "+
						" FROM RA_MST_FEES_TIER_pend TPend"
						+ " WHERE TPend.COUNTRY = ?  "+
						" AND TPend.LE_BOOK = ? "+ 
						" AND TPend.BUSINESS_LINE_ID = ? "+
						effectiveDatePend+ 
						" AND TPend.PRODUCT_TYPE= ?         "+
						" AND TPend.PRODUCT_ID= ?           "+
						" AND TPend.TRAN_CCY = ?            "+
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
						" AND TPend.Fees_Details_Attribute_20= ? ";
				}
		
			
			Object objParams[] = new Object[33];
			objParams[0] = new String(dObj.getCountry());
			objParams[1] = new String(dObj.getLeBook());
			objParams[2] = new String(dObj.getBusinessLineId());
			objParams[3] = new String(dObj.getEffectiveDate());
			objParams[4] = new String(dObj.getRateEffectiveDate());
			objParams[5] = new String(dObj.getProductType());
		 	objParams[6] = new String(dObj.getProductId());
		 	objParams[7] = new String(dObj.getTranCcy());
			objParams[8] = new String(dObj.getChannelCode());
			objParams[9] = new String(dObj.getBusinessVertical());
			objParams[10] = new String(dObj.getContractId());
			objParams[11] = new String(dObj.getCustomerId());
			objParams[12] = new String(dObj.getChannelType());
			objParams[13] = new String(dObj.getFeesDetailsAttribute1());
			objParams[14] = new String(dObj.getFeesDetailsAttribute2());
			objParams[15] = new String(dObj.getFeesDetailsAttribute3());
			objParams[16] = new String(dObj.getFeesDetailsAttribute4());
			objParams[17] = new String(dObj.getFeesDetailsAttribute5());
			objParams[18] = new String(dObj.getFeesDetailsAttribute6());
			objParams[19] = new String(dObj.getFeesDetailsAttribute7());
			objParams[20] = new String(dObj.getFeesDetailsAttribute8());
			objParams[21] = new String(dObj.getFeesDetailsAttribute9());
			objParams[22] = new String(dObj.getFeesDetailsAttribute10());
			objParams[23] = new String(dObj.getFeesDetailsAttribute11());
			objParams[24] = new String(dObj.getFeesDetailsAttribute12());
			objParams[25] = new String(dObj.getFeesDetailsAttribute13());
			objParams[26] = new String(dObj.getFeesDetailsAttribute14());
			objParams[27] = new String(dObj.getFeesDetailsAttribute15());
			objParams[28] = new String(dObj.getFeesDetailsAttribute16());
			objParams[29] = new String(dObj.getFeesDetailsAttribute17());
			objParams[30] = new String(dObj.getFeesDetailsAttribute18());
			objParams[31] = new String(dObj.getFeesDetailsAttribute19());
			objParams[32] = new String(dObj.getFeesDetailsAttribute20());
			collTemp = getJdbcTemplate().query(query,objParams,getFeesConfigTierMapper(dObj,currencyPrecisionlst));
			return collTemp;
		}catch(Exception ex){
			ex.printStackTrace();
			logger.error("Exception ");
			return null;
		}
	}
	protected int doInsertionApprFeesTier(FeesConfigTierVb vObject,FeesConfigDetailsVb feesConfigDetailsVb){
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
		String query =  " Insert Into RA_MST_FEES_TIER (COUNTRY,LE_BOOK,BUSINESS_LINE_ID," + 
				" EFFECTIVE_DATE,RATE_EFFECTIVE_DATE,RATE_EFFECTIVE_END_DATE,  "
				+ " CHANNEL_TYPE,CHANNEL_CODE, BUSINESS_VERTICAL, "
				+ " CONTRACT_ID,CUSTOMER_ID, PRODUCT_TYPE,"
				+ " PRODUCT_ID, TRAN_CCY, FEE_BASIS,"
				+ " Fees_Details_Attribute_1, Fees_Details_Attribute_2, Fees_Details_Attribute_3,"
				+ " Fees_Details_Attribute_4,Fees_Details_Attribute_5,Fees_Details_Attribute_6, "
				+ " Fees_Details_Attribute_7,Fees_Details_Attribute_8,Fees_Details_Attribute_9,"
				+ " Fees_Details_Attribute_10,Fees_Details_Attribute_11,Fees_Details_Attribute_12,"
				+ " Fees_Details_Attribute_13,Fees_Details_Attribute_14,Fees_Details_Attribute_15,"
				+ " Fees_Details_Attribute_16,Fees_Details_Attribute_17,Fees_Details_Attribute_18,"
				+ " Fees_Details_Attribute_19,Fees_Details_Attribute_20,"
				+ " FEE_SEQUENCE, AMT_FROM,AMT_TO,"
				+ " CNT_FROM, CNT_TO, FEE_AMT,FEE_PERCENTAGE, "
				+ " FEE_Tier_STATUS_NT, FEE_Tier_STATUS,"  
				+ " RECORD_INDICATOR_NT,RECORD_INDICATOR,MAKER,VERIFIER,DATE_LAST_MODIFIED,DATE_CREATION,PRICING )"
				+ " Values (?,?,?,"+effectiveDate+","+rateEffectiveDate+","+rateEffectiveEndDate+",?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,"
				+ ""+getDbFunction("SYSDATE")+","+dateCreation+",?)";
		
		Object[] args = {feesConfigDetailsVb.getCountry(),feesConfigDetailsVb.getLeBook(),feesConfigDetailsVb.getBusinessLineId(),
				feesConfigDetailsVb.getEffectiveDate(),feesConfigDetailsVb.getRateEffectiveDate(),feesConfigDetailsVb.getRateEffectiveEndDate(),
				feesConfigDetailsVb.getChannelType(), feesConfigDetailsVb.getChannelCode(),feesConfigDetailsVb.getBusinessVertical(),
				feesConfigDetailsVb.getContractId(),feesConfigDetailsVb.getCustomerId(), feesConfigDetailsVb.getProductType(),
				feesConfigDetailsVb.getProductId(),feesConfigDetailsVb.getTranCcy(),vObject.getFeeBasis(),
				feesConfigDetailsVb.getFeesDetailsAttribute1(),
				feesConfigDetailsVb.getFeesDetailsAttribute2(), feesConfigDetailsVb.getFeesDetailsAttribute3(),feesConfigDetailsVb.getFeesDetailsAttribute4(),
				feesConfigDetailsVb.getFeesDetailsAttribute5(),feesConfigDetailsVb.getFeesDetailsAttribute6(),feesConfigDetailsVb.getFeesDetailsAttribute7(),
				feesConfigDetailsVb.getFeesDetailsAttribute8(),feesConfigDetailsVb.getFeesDetailsAttribute9(),feesConfigDetailsVb.getFeesDetailsAttribute10(),
				feesConfigDetailsVb.getFeesDetailsAttribute11(),feesConfigDetailsVb.getFeesDetailsAttribute12(),feesConfigDetailsVb.getFeesDetailsAttribute13(),
				feesConfigDetailsVb.getFeesDetailsAttribute14(),feesConfigDetailsVb.getFeesDetailsAttribute15(),feesConfigDetailsVb.getFeesDetailsAttribute16(),
				feesConfigDetailsVb.getFeesDetailsAttribute17(),feesConfigDetailsVb.getFeesDetailsAttribute18(),feesConfigDetailsVb.getFeesDetailsAttribute19(),
				feesConfigDetailsVb.getFeesDetailsAttribute20(),
				vObject.getFeeSequence(),vObject.getAmtFrom().replaceAll(",",""),vObject.getAmtTo().replaceAll(",",""),
				vObject.getCountFrom().replaceAll(",",""),vObject.getCountTo().replaceAll(",",""),
				vObject.getFeeAmt().replaceAll(",",""),vObject.getFeePercentage().replaceAll(",", ""),
				feesConfigDetailsVb.getFeeDetailStatusNt(),feesConfigDetailsVb.getFeeDetailStatus(),
				feesConfigDetailsVb.getRecordIndicatorNt(),feesConfigDetailsVb.getRecordIndicator(),
				feesConfigDetailsVb.getMaker(),feesConfigDetailsVb.getVerifier(),feesConfigDetailsVb.getDateCreation(),
				feesConfigDetailsVb.getPricing()};
		return getJdbcTemplate().update(query,args);
	}
	protected int doInsertionPendFeesTier(FeesConfigTierVb vObject,FeesConfigDetailsVb feesConfigDetailsVb){
		int retVal=0;
		try {
			String effectiveDate ="?";
			String rateEffectiveDate ="?";
			String rateEffectiveEndDate ="?";
			if (databaseType.equalsIgnoreCase("ORACLE")) {
				effectiveDate = "TO_DATE(?,'DD-MM-RRRR HH24:MI:SS')";
				rateEffectiveDate="TO_DATE(?,'DD-MM-RRRR HH24:MI:SS')";
				rateEffectiveEndDate="TO_DATE(?,'DD-MM-RRRR HH24:MI:SS')";
			}
		String query =  " Insert Into RA_MST_FEES_TIER_PEND (COUNTRY,LE_BOOK,BUSINESS_LINE_ID," + 
				" EFFECTIVE_DATE,RATE_EFFECTIVE_DATE,RATE_EFFECTIVE_END_DATE ,"
				+ " CHANNEL_TYPE,CHANNEL_CODE, BUSINESS_VERTICAL, "
				+ " CONTRACT_ID,CUSTOMER_ID, PRODUCT_TYPE,"
				+ " PRODUCT_ID, TRAN_CCY, FEE_BASIS,"
				+ " Fees_Details_Attribute_1, Fees_Details_Attribute_2, Fees_Details_Attribute_3,"
				+ " Fees_Details_Attribute_4,Fees_Details_Attribute_5,Fees_Details_Attribute_6, "
				+ " Fees_Details_Attribute_7,Fees_Details_Attribute_8,"
				+ " Fees_Details_Attribute_9,Fees_Details_Attribute_10,Fees_Details_Attribute_11, "
				+ " Fees_Details_Attribute_12,Fees_Details_Attribute_13,Fees_Details_Attribute_14, "
				+ " Fees_Details_Attribute_15,Fees_Details_Attribute_16,Fees_Details_Attribute_17, "
				+ " Fees_Details_Attribute_18,Fees_Details_Attribute_19,Fees_Details_Attribute_20, "
				+ " FEE_SEQUENCE, AMT_FROM,AMT_TO,"
				+ " CNT_FROM, CNT_TO, FEE_AMT,FEE_PERCENTAGE, "
				+ " FEE_Tier_STATUS_NT, FEE_Tier_STATUS,"  
				+ " RECORD_INDICATOR_NT,RECORD_INDICATOR,MAKER,VERIFIER,DATE_LAST_MODIFIED,DATE_CREATION,PRICING )"
				+ " Values (?,?,?,"+effectiveDate+","+rateEffectiveDate+","+rateEffectiveEndDate+",?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,"
				+ ""+getDbFunction("SYSDATE")+","+getDbFunction("SYSDATE")+",?)";
		
		Object[] args = {feesConfigDetailsVb.getCountry(),feesConfigDetailsVb.getLeBook(),feesConfigDetailsVb.getBusinessLineId(),
				feesConfigDetailsVb.getEffectiveDate(),feesConfigDetailsVb.getRateEffectiveDate(),feesConfigDetailsVb.getRateEffectiveEndDate(),
				feesConfigDetailsVb.getChannelType(), feesConfigDetailsVb.getChannelCode(),feesConfigDetailsVb.getBusinessVertical(),
				feesConfigDetailsVb.getContractId(),feesConfigDetailsVb.getCustomerId(), feesConfigDetailsVb.getProductType(),
				feesConfigDetailsVb.getProductId(),feesConfigDetailsVb.getTranCcy(),vObject.getFeeBasis(),
				feesConfigDetailsVb.getFeesDetailsAttribute1(),
				feesConfigDetailsVb.getFeesDetailsAttribute2(), feesConfigDetailsVb.getFeesDetailsAttribute3(),feesConfigDetailsVb.getFeesDetailsAttribute4(),
				feesConfigDetailsVb.getFeesDetailsAttribute5(),feesConfigDetailsVb.getFeesDetailsAttribute6(),feesConfigDetailsVb.getFeesDetailsAttribute7(),
				feesConfigDetailsVb.getFeesDetailsAttribute8(),feesConfigDetailsVb.getFeesDetailsAttribute9(),feesConfigDetailsVb.getFeesDetailsAttribute10(),
				feesConfigDetailsVb.getFeesDetailsAttribute11(),feesConfigDetailsVb.getFeesDetailsAttribute12(),feesConfigDetailsVb.getFeesDetailsAttribute13(),
				feesConfigDetailsVb.getFeesDetailsAttribute14(),feesConfigDetailsVb.getFeesDetailsAttribute15(),feesConfigDetailsVb.getFeesDetailsAttribute16(),
				feesConfigDetailsVb.getFeesDetailsAttribute17(),feesConfigDetailsVb.getFeesDetailsAttribute18(),feesConfigDetailsVb.getFeesDetailsAttribute19(),
				feesConfigDetailsVb.getFeesDetailsAttribute20(),
				vObject.getFeeSequence(),vObject.getAmtFrom().replaceAll(",",""),vObject.getAmtTo().replaceAll(",",""),
				vObject.getCountFrom().replaceAll(",",""),vObject.getCountTo().replaceAll(",",""),
				vObject.getFeeAmt().replaceAll(",",""),vObject.getFeePercentage().replaceAll(",", ""),
				feesConfigDetailsVb.getFeeDetailStatusNt(),feesConfigDetailsVb.getFeeDetailStatus(),
				feesConfigDetailsVb.getRecordIndicatorNt(),feesConfigDetailsVb.getRecordIndicator(),
				feesConfigDetailsVb.getMaker(),feesConfigDetailsVb.getVerifier(),feesConfigDetailsVb.getPricing()};
		retVal = getJdbcTemplate().update(query,args);
		}catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}
		return retVal;
	}
	protected int doInsertionFeesTierHis(FeesConfigTierVb vObject){
		String effectiveDate ="?";
		String rateEffectiveDate ="?";
		String rateEffectiveEndDate = "?";
		if (databaseType.equalsIgnoreCase("ORACLE")) {
			effectiveDate = "TO_DATE(?,'DD-MM-RRRR HH24:MI:SS')";
			rateEffectiveDate="TO_DATE(?,'DD-MM-RRRR HH24:MI:SS')";
			rateEffectiveEndDate="TO_DATE(?,'DD-MM-RRRR HH24:MI:SS')";
		}
		String query =  " Insert Into RA_MST_FEES_TIER_HIS (REF_NO,HISTORY_CREATION_DATE,COUNTRY,LE_BOOK,BUSINESS_LINE_ID," + 
				" EFFECTIVE_DATE,RATE_EFFECTIVE_DATE,RATE_EFFECTIVE_END_DATE,  "
				+ " CHANNEL_TYPE,CHANNEL_CODE, BUSINESS_VERTICAL, "
				+ " CONTRACT_ID,CUSTOMER_ID, PRODUCT_TYPE,"
				+ " PRODUCT_ID, TRAN_CCY, FEE_BASIS,"
				+ " Fees_Details_Attribute_1, Fees_Details_Attribute_2, Fees_Details_Attribute_3,"
				+ " Fees_Details_Attribute_4,Fees_Details_Attribute_5,Fees_Details_Attribute_6, "
				+ " Fees_Details_Attribute_7,Fees_Details_Attribute_8,Fees_Details_Attribute_9,Fees_Details_Attribute_10,"
				+ " Fees_Details_Attribute_11,Fees_Details_Attribute_12,Fees_Details_Attribute_13,Fees_Details_Attribute_14,"
				+ " Fees_Details_Attribute_15,Fees_Details_Attribute_16,Fees_Details_Attribute_17,Fees_Details_Attribute_18,"
				+ " Fees_Details_Attribute_19,Fees_Details_Attribute_20,"
				+ " FEE_SEQUENCE, AMT_FROM,AMT_TO,"
				+ " CNT_FROM, CNT_TO, FEE_AMT,FEE_PERCENTAGE, "
				+ " FEE_Tier_STATUS_NT, FEE_Tier_STATUS,"  
				+ " RECORD_INDICATOR_NT,RECORD_INDICATOR,MAKER,VERIFIER,DATE_LAST_MODIFIED,DATE_CREATION,PRICING )"
				+ " Values (?,"+getDbFunction("SYSDATE")+",?,?,?,"+effectiveDate+","+rateEffectiveDate+","+rateEffectiveEndDate+",?,?,?,?,?,?,?,?,?,?,?,?,?,?,"
						+ "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		
		Object[] args = {vObject.getRefNo(),vObject.getCountry(),vObject.getLeBook(),vObject.getBusinessLineId(),
				vObject.getEffectiveDate(),vObject.getRateEffectiveDate(),vObject.getRateEffectiveEndDate(),
				vObject.getChannelType(), vObject.getChannelCode(),vObject.getBusinessVertical(),
				vObject.getContractId(),vObject.getCustomerId(), vObject.getProductType(),
				vObject.getProductId(),vObject.getTranCcy(),vObject.getFeeBasis(),
				vObject.getFeesDetailsAttribute1(),
				vObject.getFeesDetailsAttribute2(), vObject.getFeesDetailsAttribute3(),vObject.getFeesDetailsAttribute4(),
				vObject.getFeesDetailsAttribute5(),vObject.getFeesDetailsAttribute6(),vObject.getFeesDetailsAttribute7(),
				vObject.getFeesDetailsAttribute8(),vObject.getFeesDetailsAttribute9(),vObject.getFeesDetailsAttribute10(),vObject.getFeesDetailsAttribute11(),
				vObject.getFeesDetailsAttribute12(),vObject.getFeesDetailsAttribute13(),vObject.getFeesDetailsAttribute14(),vObject.getFeesDetailsAttribute15(),
				vObject.getFeesDetailsAttribute16(),vObject.getFeesDetailsAttribute17(),vObject.getFeesDetailsAttribute18(),vObject.getFeesDetailsAttribute19(),
				vObject.getFeesDetailsAttribute20(),vObject.getFeeSequence(),vObject.getAmtFrom().replaceAll(",",""),vObject.getAmtTo().replaceAll(",",""),
				vObject.getCountFrom().replaceAll(",",""),vObject.getCountTo().replaceAll(",",""),
				vObject.getFeeAmt().replaceAll(",",""),vObject.getFeePercentage().replaceAll(",", ""),
				vObject.getFeeTierStatusNt(),vObject.getFeeTierStatus(),
				vObject.getRecordIndicatorNt(),vObject.getRecordIndicator(),
				vObject.getMaker(),vObject.getVerifier(),
				vObject.getDateLastModified(),vObject.getDateCreation(),vObject.getPricing()};
		return getJdbcTemplate().update(query,args);
	}
	protected int deleteFeesTierAppr(FeesConfigDetailsVb vObject){
		String effectiveDate =" EFFECTIVE_DATE = ?";
		String rateEffectiveDate ="RATE_EFFECTIVE_DATE = ?";
		if (databaseType.equalsIgnoreCase("ORACLE")) {
			effectiveDate = " EFFECTIVE_DATE= TO_DATE(?,'DD-MM-RRRR HH24:MI:SS') ";
			rateEffectiveDate=" RATE_EFFECTIVE_DATE = TO_DATE(?,'DD-MM-RRRR HH24:MI:SS') ";
		}
		String query = "Delete from RA_MST_FEES_TIER WHERE COUNTRY= ? AND LE_BOOK= ? AND BUSINESS_LINE_ID = ?"+
				"  AND "+effectiveDate+" AND "+rateEffectiveDate
				+ " AND CHANNEL_CODE= ? AND  BUSINESS_VERTICAL= ? AND  CONTRACT_ID= ? AND  CUSTOMER_ID= ? AND "
				+" PRODUCT_TYPE= ? AND   PRODUCT_ID= ? AND   TRAN_CCY= ? AND CHANNEL_TYPE=? AND "
				+ "  Fees_Details_Attribute_1= ? AND  Fees_Details_Attribute_2= ? AND  Fees_Details_Attribute_3= ? AND "
				+"    Fees_Details_Attribute_4= ? AND  Fees_Details_Attribute_5= ? AND "
				+" Fees_Details_Attribute_6= ? AND Fees_Details_Attribute_7= ? AND Fees_Details_Attribute_8= ? AND "
				+" Fees_Details_Attribute_9= ? AND Fees_Details_Attribute_10= ? AND Fees_Details_Attribute_11= ? AND "
				+" Fees_Details_Attribute_12= ? AND Fees_Details_Attribute_13= ? AND Fees_Details_Attribute_14= ? AND "
				+" Fees_Details_Attribute_15= ? AND Fees_Details_Attribute_16= ? AND Fees_Details_Attribute_17= ? AND "
				+" Fees_Details_Attribute_18= ? AND Fees_Details_Attribute_19= ? AND Fees_Details_Attribute_20= ? ";
		Object[] args = {vObject.getCountry(),vObject.getLeBook(),vObject.getBusinessLineId(),
				vObject.getEffectiveDate(),vObject.getRateEffectiveDate(),vObject.getChannelCode(), 
				vObject.getBusinessVertical(), vObject.getContractId(),
				vObject.getCustomerId(),vObject.getProductType(), vObject.getProductId(), vObject.getTranCcy(),
				vObject.getChannelType(),vObject.getFeesDetailsAttribute1(),
				vObject.getFeesDetailsAttribute2(), vObject.getFeesDetailsAttribute3(),vObject.getFeesDetailsAttribute4(),
				vObject.getFeesDetailsAttribute5(),vObject.getFeesDetailsAttribute6(),vObject.getFeesDetailsAttribute7(),
				vObject.getFeesDetailsAttribute8(),vObject.getFeesDetailsAttribute9(),vObject.getFeesDetailsAttribute10(),vObject.getFeesDetailsAttribute11(),
				vObject.getFeesDetailsAttribute12(),vObject.getFeesDetailsAttribute13(),vObject.getFeesDetailsAttribute14(),
				vObject.getFeesDetailsAttribute15(),vObject.getFeesDetailsAttribute16(),vObject.getFeesDetailsAttribute17(),
				vObject.getFeesDetailsAttribute18(),vObject.getFeesDetailsAttribute19(),vObject.getFeesDetailsAttribute20()};
		return getJdbcTemplate().update(query,args);
	}
	protected int deleteFeesTierPend(FeesConfigDetailsVb vObject){
		String effectiveDate ="EFFECTIVE_DATE = ?";
		String rateEffectiveDate ="RATE_EFFECTIVE_DATE = ?";
		if (databaseType.equalsIgnoreCase("ORACLE")) {
			effectiveDate = " EFFECTIVE_DATE= TO_DATE(?,'DD-MM-RRRR HH24:MI:SS') ";
			rateEffectiveDate=" RATE_EFFECTIVE_DATE = TO_DATE(?,'DD-MM-RRRR HH24:MI:SS') ";
		}
		String query = "Delete from RA_MST_FEES_TIER_PEND WHERE COUNTRY= ? AND LE_BOOK= ? AND BUSINESS_LINE_ID = ?"+
				"  AND "+effectiveDate+" AND "+rateEffectiveDate
				+ " AND CHANNEL_CODE= ? AND  BUSINESS_VERTICAL= ? AND  CONTRACT_ID= ? AND  CUSTOMER_ID= ? AND "
				+" PRODUCT_TYPE= ? AND   PRODUCT_ID= ? AND   TRAN_CCY= ? AND CHANNEL_TYPE=? AND "
				+ "  Fees_Details_Attribute_1= ? AND  Fees_Details_Attribute_2= ? AND  Fees_Details_Attribute_3= ? AND "
				+"    Fees_Details_Attribute_4= ? AND  Fees_Details_Attribute_5= ? AND "
				+" Fees_Details_Attribute_6= ? AND Fees_Details_Attribute_7= ? AND Fees_Details_Attribute_8= ? AND "
				+" Fees_Details_Attribute_9= ? AND Fees_Details_Attribute_10= ? AND Fees_Details_Attribute_11= ? AND "
				+" Fees_Details_Attribute_12= ? AND Fees_Details_Attribute_13= ? AND Fees_Details_Attribute_14= ? AND "
				+" Fees_Details_Attribute_15= ? AND Fees_Details_Attribute_16= ? AND Fees_Details_Attribute_17= ? AND "
				+" Fees_Details_Attribute_18= ? AND Fees_Details_Attribute_19= ? AND "
				+" Fees_Details_Attribute_20= ? ";
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
	protected int deleteFeesTierPendMain(FeesConfigHeaderVb vObject){
		
		String effectiveDate =" EFFECTIVE_DATE = ?";
		if (databaseType.equalsIgnoreCase("ORACLE")) {
			effectiveDate = " EFFECTIVE_DATE= TO_DATE(?,'DD-MM-RRRR HH24:MI:SS') ";
		}
		String query = "Delete from RA_MST_FEES_TIER_PEND WHERE COUNTRY= ? AND LE_BOOK= ? AND BUSINESS_LINE_ID = ?"+
				"   AND "+effectiveDate;
		Object[] args = {vObject.getCountry(),vObject.getLeBook(),vObject.getBusinessLineId(),
				vObject.getEffectiveDate()  };
		return getJdbcTemplate().update(query,args);
	}
	protected int deleteFeesTierApprMain(FeesConfigHeaderVb vObject){
		String effectiveDate =" EFFECTIVE_DATE = ?";
		if (databaseType.equalsIgnoreCase("ORACLE")) {
			effectiveDate = " EFFECTIVE_DATE= TO_DATE(?,'DD-MM-RRRR HH24:MI:SS') ";
		}
		String query = "Delete from RA_MST_FEES_TIER WHERE COUNTRY= ? AND LE_BOOK= ? AND BUSINESS_LINE_ID = ?"+
				"   AND "+effectiveDate;
		Object[] args = {vObject.getCountry(),vObject.getLeBook(),vObject.getBusinessLineId(),
				vObject.getEffectiveDate()  };
		return getJdbcTemplate().update(query,args);
	}
	
	public ExceptionCode doInsertApprFeeTier(FeesConfigDetailsVb vObject) throws RuntimeCustomException {
		ExceptionCode exceptionCode = new ExceptionCode();
		setServiceDefaults();
		List<FeesConfigTierVb> tierlst = vObject.getFeesTierlst();
		for(FeesConfigTierVb feeTierVb : tierlst){
			feeTierVb.setAmtFrom(feeTierVb.getAmtFrom().replaceAll(",", ""));
			feeTierVb.setAmtTo(feeTierVb.getAmtTo().replaceAll(",", ""));
			feeTierVb.setCountFrom(feeTierVb.getCountFrom().replaceAll(",", ""));
			feeTierVb.setCountTo(feeTierVb.getCountTo().replaceAll(",", ""));
			feeTierVb.setFeeAmt(feeTierVb.getFeeAmt().replaceAll(",", ""));
			feeTierVb.setFeePercentage(feeTierVb.getFeePercentage().replaceAll(",", ""));
			feeTierVb.setActionType(vObject.getActionType());
			retVal = doInsertionApprFeesTier(feeTierVb,vObject);
			writeAuditLog(feeTierVb, null);
			if (retVal != Constants.SUCCESSFUL_OPERATION){
				exceptionCode = getResultObject(retVal);
				throw buildRuntimeCustomException(exceptionCode);
			} else {
				exceptionCode = getResultObject(Constants.SUCCESSFUL_OPERATION);
			}
		}
		return exceptionCode;
	}
	public ExceptionCode doInsertPendFeeTier(FeesConfigDetailsVb vObject) throws RuntimeCustomException {
		setServiceDefaults();
		ExceptionCode exceptionCode = new ExceptionCode();
		List<FeesConfigTierVb> tierlst = vObject.getFeesTierlst();
		for(FeesConfigTierVb feeTierVb : tierlst){
			feeTierVb.setAmtFrom(feeTierVb.getAmtFrom().replaceAll(",", ""));
			feeTierVb.setAmtTo(feeTierVb.getAmtTo().replaceAll(",", ""));
			feeTierVb.setCountFrom(feeTierVb.getCountFrom().replaceAll(",", ""));
			feeTierVb.setCountTo(feeTierVb.getCountTo().replaceAll(",", ""));
			feeTierVb.setFeeAmt(feeTierVb.getFeeAmt().replaceAll(",", ""));
			feeTierVb.setFeePercentage(feeTierVb.getFeePercentage().replaceAll(",", ""));
			feeTierVb.setActionType(vObject.getActionType());
			retVal = doInsertionPendFeesTier(feeTierVb,vObject);
			if (retVal != Constants.SUCCESSFUL_OPERATION){
				exceptionCode = getResultObject(retVal);
				throw buildRuntimeCustomException(exceptionCode);
			} else {
				exceptionCode = getResultObject(Constants.SUCCESSFUL_OPERATION);
			}
		}
		return exceptionCode;
	}
	@Override
	protected String getAuditString(FeesConfigTierVb vObject){
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
			
			
			/*if(ValidationUtil.isValid(vObject.getFeeConfigCode()))
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
			
			if(ValidationUtil.isValid(vObject.getFeeSequence()))
				strAudit.append("FEE_SEQUENCE"+auditDelimiterColVal+vObject.getFeeSequence());
			else
				strAudit.append("FEE_SEQUENCE"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			
			if(ValidationUtil.isValid(vObject.getAmtFrom()))
				strAudit.append("AMT_FROM"+auditDelimiterColVal+vObject.getAmtFrom());
			else
				strAudit.append("AMT_FROM"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			
			if(ValidationUtil.isValid(vObject.getAmtTo()))
				strAudit.append("AMT_TO"+auditDelimiterColVal+vObject.getAmtTo());
			else
				strAudit.append("AMT_TO"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			
			if(ValidationUtil.isValid(vObject.getCountFrom()))
				strAudit.append("CNT_FROM"+auditDelimiterColVal+vObject.getCountFrom());
			else
				strAudit.append("CNT_FROM"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			
			if(ValidationUtil.isValid(vObject.getCountTo()))
				strAudit.append("CNT_TO"+auditDelimiterColVal+vObject.getCountTo());
			else
				strAudit.append("CNT_TO"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			
			if(ValidationUtil.isValid(vObject.getFeeAmt()))
				strAudit.append("FEE_AMOUNT"+auditDelimiterColVal+vObject.getFeeAmt().trim());
			else
				strAudit.append("FEE_AMOUNT"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			
			if(ValidationUtil.isValid(vObject.getFeePercentage()))
				strAudit.append("FEE_PERCENTAGE"+auditDelimiterColVal+vObject.getFeePercentage().trim());
			else
				strAudit.append("FEE_PERCENTAGE"+auditDelimiterColVal+"NULL");
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
			
			if (ValidationUtil.isValid(vObject.getFeeBasis()))
				strAudit.append("FEE_BASIS" + auditDelimiterColVal + vObject.getFeeBasis().trim());
			else
				strAudit.append("FEE_BASIS" + auditDelimiterColVal + "NULL");
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
		StringBuffer strBufApprove = new StringBuffer("Select MAX(TAppr.REF_NO) From RA_MST_FEES_TIER_HIS TAppr ");
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
	protected int doUpdateTierStatus(FeesConfigHeaderVb vObject){
		String effectiveDate ="EFFECTIVE_DATE = ?";
		if (databaseType.equalsIgnoreCase("ORACLE")) {
			effectiveDate = "EFFECTIVE_DATE = TO_DATE(?,'DD-MM-RRRR HH24:MI:SS')";
		}
		String query = " Update RA_MST_FEES_TIER set "+
				" FEE_TIER_STATUS = ? "+
				" WHERE COUNTRY= ? AND LE_BOOK= ? AND BUSINESS_LINE_ID = ?"+
				" AND "+effectiveDate;
		Object[] args = {vObject.getFeesLineStatus(),vObject.getCountry(),vObject.getLeBook(),
				vObject.getBusinessLineId(),
				vObject.getEffectiveDate()};
		return getJdbcTemplate().update(query,args);
	}
}