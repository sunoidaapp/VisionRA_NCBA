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
import com.vision.vb.TaxConfigTierVb;
import com.vision.vb.TaxLineConfigDetailsVb;
import com.vision.vb.TaxLineConfigHeaderVb;

@Component
public class TaxConfigTierDao extends AbstractDao<TaxConfigTierVb> {
	@Autowired
	CommonDao commonDao;
	
	@Value("${app.databaseType}")
	private String databaseType;
	protected RowMapper getTaxConfigTierMapper(){
		RowMapper mapper = new RowMapper() {
			@Override
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				TaxConfigTierVb vObject = new TaxConfigTierVb();
				vObject.setCountry(rs.getString("COUNTRY"));
				vObject.setLeBook(rs.getString("LE_BOOK"));
				vObject.setTaxLineId(rs.getString("TAX_LINE_ID"));
				vObject.setEffectiveDate(rs.getString("EFFECTIVE_DATE"));
				vObject.setProductType(rs.getString("PRODUCT_TYPE"));
				vObject.setProductId(rs.getString("PRODUCT_ID"));
				vObject.setTranCcy(rs.getString("TRAN_CCY"));
				vObject.setTierSequence(rs.getInt("TIER_SEQUENCE"));
				vObject.setAmtFrom(rs.getString("AMT_FROM"));
				vObject.setAmtTo(rs.getString("AMT_TO"));
				vObject.setCountFrom(rs.getString("CNT_FROM"));
				vObject.setCountTo(rs.getString("CNT_TO"));
				vObject.setTaxAmt(rs.getString("TAX_AMT"));
				vObject.setTaxPercentage(String.valueOf(rs.getLong("TAX_PERCENTAGE")));
				return vObject;
			}
		};
		return mapper;
	}
	@Override
	protected void setServiceDefaults() {
		serviceName = "TaxLineConfigTier";
		serviceDesc = "Fees Config Tier";
		tableName = "RA_MST_TAX_TIER";
		childTableName = "RA_MST_TAX_TIER";
		intCurrentUserId = CustomContextHolder.getContext().getVisionId();
	}
	public List<TaxConfigTierVb> getTaxConfigTierByGroup(TaxLineConfigHeaderVb dObj,int intStatus){
		List<TaxConfigTierVb> collTemp = null;
		String query = "";
		try
		{	
			if ("ORACLE".equalsIgnoreCase(databaseType)) {
				if(!dObj.isVerificationRequired() || dObj.isReview()){intStatus =0;}
				if(intStatus == Constants.STATUS_ZERO) {
					query = " SELECT  COUNTRY,LE_BOOK,TAX_LINE_ID,  "+
							" TO_CHAR(TAPPR.EFFECTIVE_DATE,'DD-Mon-RRRR') EFFECTIVE_DATE,PRODUCT_TYPE,"+
						    " PRODUCT_ID,TRAN_CCY,TIER_SEQUENCE,"
						    + "TRIM(TO_CHAR(AMT_FROM,'999,999,999,999,990.99990')) AMT_FROM,"+
							" TRIM(TO_CHAR(AMT_TO,'999,999,999,999,990.99990')) AMT_TO,"+
							" CNT_FROM,CNT_TO, "+
							" TRIM(TO_CHAR(TAX_AMT,'999,999,999,999,990.99990')) TAX_AMT,TAX_PERCENTAGE "
							+" FROM RA_MST_TAX_TIER TAPPR WHERE TAPPR.COUNTRY = ? AND TAPPR.LE_BOOK = ? AND " + 
							" TAPPR.TAX_LINE_ID = ? AND  "+
							"  TAPPR.EFFECTIVE_DATE = ? ";
				}else {
					query = " SELECT  COUNTRY,LE_BOOK,TAX_LINE_ID,  "+
							" TO_CHAR(TPEND.EFFECTIVE_DATE,'DD-Mon-RRRR') EFFECTIVE_DATE,PRODUCT_TYPE,"+
						    " PRODUCT_ID,TRAN_CCY,TIER_SEQUENCE,"
						    + "TRIM(TO_CHAR(AMT_FROM,'999,999,999,999,990.99990')) AMT_FROM,"+
							" TRIM(TO_CHAR(AMT_TO,'999,999,999,999,990.99990')) AMT_TO,"+
							" CNT_FROM,CNT_TO, "+
							" TRIM(TO_CHAR(TAX_AMT,'999,999,999,999,990.99990')) TAX_AMT,TAX_PERCENTAGE "
							+" FROM RA_MST_TAX_TIER_PEND TPEND WHERE TPEND.COUNTRY = ? AND TPEND.LE_BOOK = ? AND " + 
							" TPEND.TAX_LINE_ID = ? AND  "+
							"  TPEND.EFFECTIVE_DATE = ? ";
				}
			}else if ("MSSQL".equalsIgnoreCase(databaseType)){
				if(!dObj.isVerificationRequired() || dObj.isReview()){intStatus =0;}
				if(intStatus == Constants.STATUS_ZERO) {
					query = " SELECT  COUNTRY,LE_BOOK,TAX_LINE_ID,  "+
							" FORMAT(CAST(EFFECTIVE_DATE AS DATETIME), 'dd-MMM-yyyy') EFFECTIVE_DATE,PRODUCT_TYPE,"+
						    " PRODUCT_ID,TRAN_CCY,TIER_SEQUENCE,RTRIM(LTRIM("+getDbFunction("DATEFUNC")+"(AMT_FROM,'N5'))) AMT_FROM,"+
							" RTRIM(LTRIM("+getDbFunction("DATEFUNC")+"(AMT_TO,'N5'))) AMT_TO,"+
							" CNT_FROM,CNT_TO, "+
							" RTRIM(LTRIM(" + getDbFunction("DATEFUNC")
							+ "(TAX_AMT,'N5')))  TAX_AMT,TAX_PERCENTAGE "
							+" FROM RA_MST_TAX_TIER TAPPR WHERE TAPPR.COUNTRY = ? AND TAPPR.LE_BOOK = ? AND " + 
							" TAPPR.TAX_LINE_ID = ? AND  "+
							"  TAPPR.EFFECTIVE_DATE = ? ";
				}else {
					query = " SELECT  COUNTRY,LE_BOOK,TAX_LINE_ID,  "+
							" FORMAT(CAST(EFFECTIVE_DATE AS DATETIME), 'dd-MMM-yyyy') EFFECTIVE_DATE,PRODUCT_TYPE,"+
						    " PRODUCT_ID,TRAN_CCY,TIER_SEQUENCE,RTRIM(LTRIM("+getDbFunction("DATEFUNC")+"(AMT_FROM,'N5'))) AMT_FROM,"+
							" RTRIM(LTRIM("+getDbFunction("DATEFUNC")+"(AMT_TO,'N5'))) AMT_TO,"+
							" CNT_FROM,CNT_TO, "+
							" RTRIM(LTRIM(" + getDbFunction("DATEFUNC")
							+ "(TAX_AMT,'N5')))  TAX_AMT,TAX_PERCENTAGE "
							+" FROM RA_MST_TAX_TIER_PEND TPEND WHERE TPEND.COUNTRY = ? AND TPEND.LE_BOOK = ? AND " + 
							" TPEND.TAX_LINE_ID = ? AND  "+
							"  TPEND.EFFECTIVE_DATE = ? ";
				}
			}
			
			
			Object objParams[] = new Object[4];
			objParams[0] = new String(dObj.getCountry());// country
			objParams[1] = new String(dObj.getLeBook());
			objParams[2] = new String(dObj.getTaxLineId());
			objParams[3] = new String(dObj.getEffectiveDate());
			
			collTemp = getJdbcTemplate().query(query,objParams,getTaxConfigTierMapper());
			return collTemp;
		}catch(Exception ex){
			ex.printStackTrace();
			logger.error("Exception ");
			return null;
		}
	}
	public List<TaxConfigTierVb> getTaxConfigTier(TaxLineConfigDetailsVb dObj,int intStatus){
		List<TaxConfigTierVb> collTemp = null;
		String query = "";
		String effectiveDate = "";
		try
		{	
			if ("ORACLE".equalsIgnoreCase(databaseType)) {
				if(!dObj.isVerificationRequired() || dObj.isReview()){intStatus =0;}
				if(intStatus == Constants.STATUS_ZERO) {
					query = " SELECT  COUNTRY,LE_BOOK,TAX_LINE_ID,  "+
							" TO_CHAR(TAPPR.EFFECTIVE_DATE,'DD-Mon-RRRR')  EFFECTIVE_DATE, PRODUCT_TYPE, "+
						    " PRODUCT_ID,TRAN_CCY,TIER_SEQUENCE, "+
							" TRIM(TO_CHAR(AMT_FROM,'999,999,999,999,990.99990')) AMT_FROM, "+
							" TRIM(TO_CHAR(AMT_TO,'999,999,999,999,990.99990')) AMT_TO, "+
							" CNT_FROM,CNT_TO, "+ 
							" TRIM(TO_CHAR(TAX_AMT,'999,999,999,999,990.99990')) TAX_AMT,TAX_PERCENTAGE  "
							+" FROM RA_MST_TAX_TIER TAPPR WHERE TAPPR.COUNTRY = ? AND TAPPR.LE_BOOK = ?  AND "+   
							" TAPPR.TAX_LINE_ID = ? AND   "+
							"  TAPPR.EFFECTIVE_DATE = ? AND TAPPR.PRODUCT_TYPE = ? "+ 
							" AND TAPPR.PRODUCT_ID = ? AND TAPPR.TRAN_CCY = ? ";

				}else {
					query = " SELECT  COUNTRY,LE_BOOK,TAX_LINE_ID,"+
							" TO_CHAR(TPEND.EFFECTIVE_DATE,'DD-Mon-RRRR')  EFFECTIVE_DATE, PRODUCT_TYPE, "+
						    " PRODUCT_ID,TRAN_CCY,TIER_SEQUENCE, "+
							" TRIM(TO_CHAR(AMT_FROM,'999,999,999,999,990.99990')) AMT_FROM, "+
							" TRIM(TO_CHAR(AMT_TO,'999,999,999,999,990.99990')) AMT_TO, "+
							" CASE WHEN 'R' = '"+dObj.getTaxType()+"' THEN 1 ELSE CNT_FROM END CNT_FROM,CNT_TO, "+ 
							" TRIM(TO_CHAR(TAX_AMT,'999,999,999,999,990.99990')) TAX_AMT,TAX_PERCENTAGE  "
							+" FROM RA_MST_TAX_TIER_PEND TPEND WHERE TPEND.COUNTRY = ? AND TPEND.LE_BOOK = ?  AND "+   
							" TPEND.TAX_LINE_ID = ? AND   "+
							"  TPEND.EFFECTIVE_DATE = ? AND TPEND.PRODUCT_TYPE = ? "+ 
							" AND TPEND.PRODUCT_ID = ? AND TPEND.TRAN_CCY = ? ";
				}
			}else if ("MSSQL".equalsIgnoreCase(databaseType)){
				if(!dObj.isVerificationRequired() || dObj.isReview()){intStatus =0;}
				if(intStatus == Constants.STATUS_ZERO) {
					query = " SELECT  COUNTRY,LE_BOOK,TAX_LINE_ID,  "+
							" FORMAT(CAST(EFFECTIVE_DATE AS DATETIME), 'dd-MMM-yyyy') EFFECTIVE_DATE, PRODUCT_TYPE,"+
						    " PRODUCT_ID,TRAN_CCY,TIER_SEQUENCE,"+
						    " RTRIM(LTRIM("+getDbFunction("DATEFUNC")+"(AMT_FROM,'N5'))) AMT_FROM,"+
							" RTRIM(LTRIM("+getDbFunction("DATEFUNC")+"(AMT_TO,'N5'))) AMT_TO,"+
							" CASE WHEN 'R' = '"+dObj.getTaxType()+"' THEN '1' ELSE CNT_FROM END CNT_FROM,CNT_TO, "+
							" RTRIM(LTRIM(" + getDbFunction("DATEFUNC")
							+ "(TAX_AMT,'N5'))) TAX_AMT,TAX_PERCENTAGE "
							+" FROM RA_MST_TAX_TIER TAPPR WHERE TAPPR.COUNTRY = ? AND TAPPR.LE_BOOK = ?  AND " + 
							" TAPPR.TAX_LINE_ID = ? AND  "+
							" TAPPR.EFFECTIVE_DATE = ? AND TAPPR.PRODUCT_TYPE = ? "+
							" AND TAPPR.PRODUCT_ID = ? AND TAPPR.TRAN_CCY = ? ";
				}else {
					query = " SELECT  COUNTRY,LE_BOOK,TAX_LINE_ID,  "+
							" FORMAT(CAST(EFFECTIVE_DATE AS DATETIME), 'dd-MMM-yyyy') EFFECTIVE_DATE, PRODUCT_TYPE,"+
						    " PRODUCT_ID,TRAN_CCY,TIER_SEQUENCE,"+
						    " RTRIM(LTRIM("+getDbFunction("DATEFUNC")+"(AMT_FROM,'N5'))) AMT_FROM,"+
							" RTRIM(LTRIM("+getDbFunction("DATEFUNC")+"(AMT_TO,'N5'))) AMT_TO,"+
							" CASE WHEN 'R' = '"+dObj.getTaxType()+"' THEN '1' ELSE CNT_FROM END CNT_FROM,CNT_TO, "+
							" RTRIM(LTRIM(" + getDbFunction("DATEFUNC")
							+ "(TAX_AMT,'N5'))) TAX_AMT,TAX_PERCENTAGE "
							+ " FROM RA_MST_TAX_TIER_PEND TPEND WHERE TPEND.COUNTRY = ? AND TPEND.LE_BOOK = ?  AND " + 
							" TPEND.TAX_LINE_ID = ? AND  "+
							" TPEND.EFFECTIVE_DATE = ? AND TPEND.PRODUCT_TYPE = ? "+
							" AND TPEND.PRODUCT_ID = ? AND TPEND.TRAN_CCY = ? ";
				}
			}
			
			
			Object objParams[] = new Object[7];
			objParams[0] = new String(dObj.getCountry());// country
			objParams[1] = new String(dObj.getLeBook());
			objParams[2] = new String(dObj.getTaxLineId());
			objParams[3] = new String(dObj.getEffectiveDate());
			objParams[4] = new String(dObj.getProductType());
		 	objParams[5] = new String(dObj.getProductId());
		 	objParams[6] = new String(dObj.getTranCcy());
			collTemp = getJdbcTemplate().query(query,objParams,getTaxConfigTierMapper());
			return collTemp;
		}catch(Exception ex){
			ex.printStackTrace();
			logger.error("Exception ");
			return null;
		}
	}
	protected int doInsertionApprTaxTier(TaxConfigTierVb vObject){
		
		String query =  " Insert Into RA_MST_TAX_TIER(COUNTRY,LE_BOOK,TAX_LINE_ID," + 
				" EFFECTIVE_DATE,  PRODUCT_TYPE,PRODUCT_ID, TRAN_CCY, TIER_SEQUENCE, AMT_FROM,"+
				"AMT_TO,CNT_FROM, CNT_TO, TAX_AMT,TAX_PERCENTAGE) "
				+ " Values (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		
		Object[] args = {vObject.getCountry(),vObject.getLeBook(),vObject.getTaxLineId(),
				vObject.getEffectiveDate(),vObject.getProductType(),vObject.getProductId(),
				vObject.getTranCcy(),vObject.getTierSequence(),
				vObject.getAmtFrom().replaceAll(",",""),vObject.getAmtTo().replaceAll(",",""),vObject.getCountFrom().replaceAll(",",""),
				vObject.getCountTo().replaceAll(",",""),ValidationUtil.isValid(vObject.getTaxAmt())? vObject.getTaxAmt().replaceAll(",",""):0,
			ValidationUtil.isValid(vObject.getTaxPercentage())?vObject.getTaxPercentage().replaceAll(",", ""):0 };
		return getJdbcTemplate().update(query,args);
	}
	protected int doInsertionPendTaxTier(TaxConfigTierVb vObject){
		String query =  " Insert Into RA_MST_TAX_TIER_PEND(COUNTRY,LE_BOOK,TAX_LINE_ID," + 
				" EFFECTIVE_DATE,  PRODUCT_TYPE,PRODUCT_ID, TRAN_CCY, TIER_SEQUENCE, AMT_FROM,"+
				"AMT_TO,CNT_FROM, CNT_TO, TAX_AMT,TAX_PERCENTAGE) "
				+ " Values (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		
		Object[] args = {vObject.getCountry(),vObject.getLeBook(),vObject.getTaxLineId(),
				vObject.getEffectiveDate(),vObject.getProductType(),vObject.getProductId(),
				vObject.getTranCcy(),vObject.getTierSequence(),
				vObject.getAmtFrom().replaceAll(",",""),vObject.getAmtTo().replaceAll(",",""),vObject.getCountFrom().replaceAll(",",""),
				vObject.getCountTo().replaceAll(",",""), ValidationUtil.isValid(vObject.getTaxAmt())? vObject.getTaxAmt().replaceAll(",",""):0,
						ValidationUtil.isValid(vObject.getTaxPercentage())?vObject.getTaxPercentage().replaceAll(",", ""):0  };
		return getJdbcTemplate().update(query,args);
	}
	protected int deleteTaxTierAppr(TaxLineConfigHeaderVb vObject){
		String query = "Delete from RA_MST_TAX_TIER WHERE COUNTRY= ? AND LE_BOOK= ? AND TAX_LINE_ID = ?"+
				"   AND EFFECTIVE_DATE = ?";
		Object[] args = {vObject.getCountry(),vObject.getLeBook(),vObject.getTaxLineId(),
				vObject.getEffectiveDate()};
		return getJdbcTemplate().update(query,args);
		
	}
	protected int deleteTaxTierPend(TaxLineConfigHeaderVb vObject){
		String query = "Delete from RA_MST_TAX_TIER_PEND WHERE COUNTRY= ? AND LE_BOOK= ? AND TAX_LINE_ID = ?"+
				" AND EFFECTIVE_DATE= ?";
		Object[] args = {vObject.getCountry(),vObject.getLeBook(),vObject.getTaxLineId(),
				vObject.getEffectiveDate()};
		return getJdbcTemplate().update(query,args);
		
	}
	public ExceptionCode doInsertApprTaxTier(TaxLineConfigDetailsVb vObject) throws RuntimeCustomException {
		ExceptionCode exceptionCode = new ExceptionCode();
		List<TaxConfigTierVb> tierlst = vObject.getTaxTierlst();
		int ctr = 0;
		for(TaxConfigTierVb taxTierVb : tierlst){
			taxTierVb.setCountry(vObject.getCountry());
			taxTierVb.setLeBook(vObject.getLeBook());
			taxTierVb.setTaxLineId(vObject.getTaxLineId());
			taxTierVb.setEffectiveDate(vObject.getEffectiveDate());
			taxTierVb.setProductType(vObject.getProductType());
			taxTierVb.setProductId(vObject.getProductId());
			taxTierVb.setTranCcy(vObject.getTranCcy());
			taxTierVb.setTaxType(vObject.getTaxType());
			taxTierVb.setAmtFrom(taxTierVb.getAmtFrom().replaceAll(",", ""));
			taxTierVb.setAmtTo(taxTierVb.getAmtTo().replaceAll(",", ""));
			taxTierVb.setCountFrom(taxTierVb.getCountFrom().replaceAll(",", ""));
			taxTierVb.setCountTo(taxTierVb.getCountTo().replaceAll(",", ""));
			taxTierVb.setTaxAmt(taxTierVb.getTaxAmt().replaceAll(",", ""));
			taxTierVb.setTaxPercentage(taxTierVb.getTaxPercentage().replaceAll(",", ""));
			retVal = doInsertionApprTaxTier(taxTierVb);
			if (retVal != Constants.SUCCESSFUL_OPERATION){
				exceptionCode = getResultObject(retVal);
				throw buildRuntimeCustomException(exceptionCode);
			} else {
				exceptionCode = getResultObject(Constants.SUCCESSFUL_OPERATION);
			}
			ctr  = ctr+1;
		}
		return exceptionCode;
	}
	public ExceptionCode doInsertPendTaxTier(TaxLineConfigDetailsVb vObject) throws RuntimeCustomException {
		ExceptionCode exceptionCode = new ExceptionCode();
		List<TaxConfigTierVb> tierlst = vObject.getTaxTierlst();
		int ctr = 0;
		for(TaxConfigTierVb taxTierVb : tierlst){
			taxTierVb.setCountry(vObject.getCountry());
			taxTierVb.setLeBook(vObject.getLeBook());
			taxTierVb.setTaxLineId(vObject.getTaxLineId());
			taxTierVb.setEffectiveDate(vObject.getEffectiveDate());
			taxTierVb.setProductType(vObject.getProductType());
			taxTierVb.setProductId(vObject.getProductId());
			taxTierVb.setTranCcy(vObject.getTranCcy());
			taxTierVb.setTaxType(vObject.getTaxType());
			taxTierVb.setAmtFrom(taxTierVb.getAmtFrom().replaceAll(",", ""));
			taxTierVb.setAmtTo(taxTierVb.getAmtTo().replaceAll(",", ""));
			taxTierVb.setCountFrom(taxTierVb.getCountFrom().replaceAll(",", ""));
			taxTierVb.setCountTo(taxTierVb.getCountTo().replaceAll(",", ""));
			taxTierVb.setTaxAmt(taxTierVb.getTaxAmt().replaceAll(",", ""));
			taxTierVb.setTaxPercentage(taxTierVb.getTaxPercentage().replaceAll(",", ""));
			retVal = doInsertionPendTaxTier(taxTierVb);
			if (retVal != Constants.SUCCESSFUL_OPERATION){
				exceptionCode = getResultObject(retVal);
				throw buildRuntimeCustomException(exceptionCode);
			} else {
				exceptionCode = getResultObject(Constants.SUCCESSFUL_OPERATION);
			}
			ctr  = ctr+1;
		}
		return exceptionCode;
	}
	@Override
	protected String getAuditString(TaxConfigTierVb vObject){
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
			
			if(ValidationUtil.isValid(vObject.getTaxLineId()))
				strAudit.append("TAX_LINE_ID"+auditDelimiterColVal+vObject.getTaxLineId().trim());
			else
				strAudit.append("TAX_LINE_ID"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			
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
			
			if(ValidationUtil.isValid(vObject.getTierSequence()))
				strAudit.append("TIER_SEQUENCE"+auditDelimiterColVal+vObject.getTierSequence());
			else
				strAudit.append("TIER_SEQUENCE"+auditDelimiterColVal+"NULL");
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
			
			if(ValidationUtil.isValid(vObject.getTaxAmt()))
				strAudit.append("TAX_AMOUNT"+auditDelimiterColVal+vObject.getTaxAmt().trim());
			else
				strAudit.append("TAX_AMOUNT"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			
			if(ValidationUtil.isValid(vObject.getTaxPercentage()))
				strAudit.append("TAX_PERCENTAGE"+auditDelimiterColVal+vObject.getTaxPercentage().trim());
			else
				strAudit.append("TAX_PERCENTAGE"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			
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
}