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
public class TaxLineConfigDetailsDao extends AbstractDao<TaxLineConfigDetailsVb> {
	@Autowired
	CommonDao commonDao;
	@Autowired
	TaxConfigTierDao taxConfigTierDao;
	
	@Value("${app.databaseType}")
	private String databaseType;
	
	String lookupAmtDescAppr = "(SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB= 7071 AND ALPHA_SUB_TAB = TAPPR.LOOKUP_AMOUNT_TYPE) LOOKUP_AMOUNT_TYPE_DESC, ";
	String lookupAmtDescPend = "(SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB= 7071 AND ALPHA_SUB_TAB = TPEND.LOOKUP_AMOUNT_TYPE) LOOKUP_AMOUNT_TYPE_DESC, ";
	
	protected RowMapper getTaxLineConfigDetailMapper(){
		RowMapper mapper = new RowMapper() {
			@Override
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				TaxLineConfigDetailsVb vObject = new TaxLineConfigDetailsVb();
				vObject.setCountry(rs.getString("COUNTRY"));
				vObject.setLeBook(rs.getString("LE_BOOK"));
				vObject.setTaxLineId(rs.getString("TAX_LINE_ID"));
				vObject.setEffectiveDate(rs.getString("EFFECTIVE_DATE"));
				vObject.setProductType(rs.getString("PRODUCT_TYPE"));
				vObject.setProductId(rs.getString("PRODUCT_ID"));
				vObject.setTranCcy(rs.getString("TRAN_CCY"));
				vObject.setTaxAmt(rs.getString("TAX_AMT"));
				vObject.setTaxPercentage(rs.getString("TAX_PERCENTAGE"));
				vObject.setPlGL(rs.getString("PL_GL"));
				vObject.setOfficeAccount(rs.getString("OFFICE_ACCOUNT"));
				vObject.setLookupAmountType(rs.getString("LOOKUP_AMOUNT_TYPE"));
				vObject.setLookupAmountTypeDesc(rs.getString("LOOKUP_AMOUNT_TYPE_DESC"));
				return vObject;
			}
		};
		return mapper;
	}
	@Override
	protected void setServiceDefaults() {
		serviceName = "TaxLineConfigDetails";
		serviceDesc = "Tax Line Config Details";
		tableName = "RA_MST_TAX_DETAIL";
		childTableName = "RA_MST_TAX_DETAIL";
		intCurrentUserId = CustomContextHolder.getContext().getVisionId();
	}
	public List<TaxLineConfigDetailsVb> getTaxLineConfigDetails(TaxLineConfigHeaderVb dObj,int intStatus){
		List<TaxLineConfigDetailsVb> collTemp = null;
		String query = "";
		try
		{	
			if("ORACLE".equalsIgnoreCase(databaseType)) {
				if(!dObj.isVerificationRequired() || dObj.isReview()){intStatus =0;}
				if(intStatus == Constants.STATUS_ZERO) {
					query = " SELECT COUNTRY, LE_BOOK,TAX_LINE_ID,  "+
							" TO_CHAR(TAPPR.EFFECTIVE_DATE,'DD-Mon-RRRR') EFFECTIVE_DATE, PRODUCT_TYPE,PRODUCT_ID,TRAN_CCY , "+
							" TRIM(TO_CHAR(TAX_AMT,'999,999,999,999,990.99990')) TAX_AMT, "+
							" TRIM(TO_CHAR(TAX_PERCENTAGE,'990.90')) TAX_PERCENTAGE, "+
							" PL_GL, OFFICE_ACCOUNT,"+lookupAmtDescAppr+"LOOKUP_AMOUNT_TYPE "+
							" FROM RA_MST_TAX_DETAIL TAPPR"+
							" WHERE TAPPR.COUNTRY = ? AND TAPPR.LE_BOOK = ? AND  " + 
							" TAPPR.TAX_LINE_ID = ?  "+
							" AND TAPPR.EFFECTIVE_DATE = ? ";
				}else {
					query = " SELECT COUNTRY, LE_BOOK,TAX_LINE_ID,  "+
							" TO_CHAR(TPEND.EFFECTIVE_DATE,'DD-Mon-RRRR') EFFECTIVE_DATE, PRODUCT_TYPE,PRODUCT_ID,TRAN_CCY , "+
							" TRIM(TO_CHAR(TAX_AMT,'999,999,999,999,990.99990')) TAX_AMT, "+
							" TRIM(TO_CHAR(TAX_PERCENTAGE,'990.90')) TAX_PERCENTAGE, "+
							" PL_GL, OFFICE_ACCOUNT,"+lookupAmtDescPend+"LOOKUP_AMOUNT_TYPE "+
							" FROM RA_MST_TAX_DETAIL_PEND TPEND"+
							" WHERE TPEND.COUNTRY = ? AND TPEND.LE_BOOK = ? AND  " + 
							" TPEND.TAX_LINE_ID = ?  "+
							" AND TPEND.EFFECTIVE_DATE = ? ";
				}
				
			} else if("MSSQL".equalsIgnoreCase(databaseType)) {
				//if(!dObj.isVerificationRequired() || dObj.isReview()){intStatus =0;}
				if(intStatus == Constants.STATUS_ZERO) {
					query = " SELECT  COUNTRY,LE_BOOK,TAX_LINE_ID,  "+
							" EFFECTIVE_DATE,PRODUCT_TYPE,PRODUCT_ID,TRAN_CCY,          "+
							" RTRIM(LTRIM(" + getDbFunction("DATEFUNC")
							+ "(TAX_AMT,'N5'))) TAX_AMT,TAX_PERCENTAGE,  "
							+" PL_GL, OFFICE_ACCOUNT,"+lookupAmtDescAppr+"LOOKUP_AMOUNT_TYPE"
							+ " FROM RA_MST_TAX_DETAIL TAPPR WHERE TAPPR.COUNTRY = ? AND TAPPR.LE_BOOK = ? AND  " 
							+ " TAPPR.TAX_LINE_ID = ?  "
							+ "  AND TAPPR.EFFECTIVE_DATE = ? ";
				}else {
					query = " SELECT  COUNTRY,LE_BOOK,TAX_LINE_ID,  "+
							" EFFECTIVE_DATE,PRODUCT_TYPE,PRODUCT_ID,TRAN_CCY,          "+
							" RTRIM(LTRIM(" + getDbFunction("DATEFUNC")
							+ "(TAX_AMT,'N5'))) TAX_AMT,TAX_PERCENTAGE,   "
							+" PL_GL, OFFICE_ACCOUNT,"+lookupAmtDescPend+"LOOKUP_AMOUNT_TYPE "
							+" FROM RA_MST_TAX_DETAIL_PEND TPEND  WHERE TPEND.COUNTRY = ? AND TPEND.LE_BOOK = ? AND  "+  
							" TPEND.TAX_LINE_ID = ? AND  "+
							"  TPEND.EFFECTIVE_DATE = ?";
				}
			}
			
			
			Object objParams[] = new Object[4];
			objParams[0] = new String(dObj.getCountry());// country
			objParams[1] = new String(dObj.getLeBook());
			objParams[2] = new String(dObj.getTaxLineId());
			objParams[3] = new String(dObj.getEffectiveDate());
			
			collTemp = getJdbcTemplate().query(query,objParams,getTaxLineConfigDetailMapper());
			return collTemp;
		}catch(Exception ex){
			ex.printStackTrace();
			logger.error("Exception ");
			return null;
		}
	}
	protected int doInsertionApprTaxDetails(TaxLineConfigDetailsVb vObject){
		String query =  " Insert Into RA_MST_TAX_DETAIL(COUNTRY,LE_BOOK,TAX_LINE_ID," + 
				" EFFECTIVE_DATE, PRODUCT_TYPE, PRODUCT_ID, TRAN_CCY, TAX_AMT,"+
				"TAX_PERCENTAGE, PL_GL, OFFICE_ACCOUNT,LOOKUP_AMOUNT_TYPE)"
				+ " Values (?,?,?,?,?,?,?,?,?,?,?,?)";
		
		Object[] args = {vObject.getCountry(),vObject.getLeBook(),vObject.getTaxLineId(),
				vObject.getEffectiveDate(),vObject.getProductType(),vObject.getProductId(),vObject.getTranCcy(),
				ValidationUtil.isValid(vObject.getTaxAmt())? vObject.getTaxAmt().replaceAll(",",""):0,
				ValidationUtil.isValid(vObject.getTaxPercentage())?vObject.getTaxPercentage().replaceAll(",", ""):0 ,
				vObject.getPlGL(), vObject.getOfficeAccount(),vObject.getLookupAmountType()};
		
		return getJdbcTemplate().update(query,args);
	}
	protected int doInsertionPendTaxDetails(TaxLineConfigDetailsVb vObject){
		String query =  " Insert Into RA_MST_TAX_DETAIL_PEND(COUNTRY,LE_BOOK,TAX_LINE_ID," + 
				" EFFECTIVE_DATE, PRODUCT_TYPE, PRODUCT_ID, TRAN_CCY, TAX_AMT,"+
				"TAX_PERCENTAGE, PL_GL, OFFICE_ACCOUNT,LOOKUP_AMOUNT_TYPE)"
				+ " Values (?,?,?,?,?,?,?,?,?,?,?,?)";
		
		Object[] args = {vObject.getCountry(),vObject.getLeBook(),vObject.getTaxLineId(),
				vObject.getEffectiveDate(),vObject.getProductType(),vObject.getProductId(),vObject.getTranCcy(),
				ValidationUtil.isValid(vObject.getTaxAmt())? vObject.getTaxAmt().replaceAll(",",""):0,
				ValidationUtil.isValid(vObject.getTaxPercentage())?vObject.getTaxPercentage().replaceAll(",", ""):0 ,
				vObject.getPlGL(), vObject.getOfficeAccount(),vObject.getLookupAmountType()};
		
		return getJdbcTemplate().update(query,args);
	}
	protected int deleteTaxDetailsAppr(TaxLineConfigHeaderVb vObject){
		String effectiveDate =" AND EFFECTIVE_DATE = ?";
		if (databaseType.equalsIgnoreCase("ORACLE")) {
			effectiveDate = " AND EFFECTIVE_DATE = TO_DATE(?,'DD-MM-YYYY HH24:MI:SS')";
		}
		String query = "Delete from RA_MST_TAX_DETAIL WHERE COUNTRY= ? AND LE_BOOK= ? AND TAX_LINE_ID = ?"+
				"  "+effectiveDate+" ";
		Object[] args = {vObject.getCountry(),vObject.getLeBook(),vObject.getTaxLineId(),
				vObject.getEffectiveDate()};
		return getJdbcTemplate().update(query,args);
		
	}
	protected int deleteTaxDetailsPend(TaxLineConfigHeaderVb vObject){
		String effectiveDate =" AND EFFECTIVE_DATE = ?";
		if (databaseType.equalsIgnoreCase("ORACLE")) {
			effectiveDate = " AND EFFECTIVE_DATE = TO_DATE(?,'DD-MM-YYYY HH24:MI:SS')";
		}
		String query = "Delete from RA_MST_TAX_DETAIL_PEND WHERE COUNTRY= ? AND LE_BOOK= ? AND TAX_LINE_ID = ?"+
				"  "+effectiveDate+" ";
		Object[] args = {vObject.getCountry(),vObject.getLeBook(),vObject.getTaxLineId(),
				vObject.getEffectiveDate()};
		return getJdbcTemplate().update(query,args);
		
	}
	public ExceptionCode deleteAndInsertApprTaxDetail(TaxLineConfigHeaderVb vObject) throws RuntimeCustomException {
		ExceptionCode exceptionCode = new ExceptionCode();
		List<TaxLineConfigDetailsVb> collTemp = null;
		collTemp = getTaxLineConfigDetails(vObject, Constants.STATUS_ZERO);
		if (collTemp != null && collTemp.size() > 0 ){
			int delCnt = deleteTaxDetailsAppr(vObject);
		}
		List<TaxConfigTierVb> collTempTier = null;
		collTempTier =  taxConfigTierDao.getTaxConfigTierByGroup(vObject, Constants.STATUS_ZERO);
		if (collTempTier != null && collTempTier.size() > 0 ){
			int delCnt = taxConfigTierDao.deleteTaxTierAppr(vObject);
		}
		
		List<TaxLineConfigDetailsVb> detaillst = vObject.getTaxLineConfigDetaillst();
		if(detaillst != null && !detaillst.isEmpty()) {
			for(TaxLineConfigDetailsVb feeDetailVb : detaillst){
				feeDetailVb.setRecordIndicator(vObject.getRecordIndicator());
				feeDetailVb.setCountry(vObject.getCountry());
				feeDetailVb.setLeBook(vObject.getLeBook());
				feeDetailVb.setTaxLineId(vObject.getTaxLineId());
				feeDetailVb.setEffectiveDate(vObject.getEffectiveDate());
				feeDetailVb.setTaxType(vObject.getTaxChargeType());
				if(ValidationUtil.isValid(feeDetailVb.getTaxAmt()))
						feeDetailVb.setTaxAmt(feeDetailVb.getTaxAmt().replaceAll(",", "")); 
				else
					feeDetailVb.setTaxAmt("0");
				if(ValidationUtil.isValid(feeDetailVb.getTaxPercentage()))
					feeDetailVb.setTaxPercentage(feeDetailVb.getTaxPercentage().replaceAll(",", ""));
				else
					feeDetailVb.setTaxPercentage("0");
				feeDetailVb.setPlGL(feeDetailVb.getPlGL());
				feeDetailVb.setOfficeAccount(feeDetailVb.getOfficeAccount());
				retVal = doInsertionApprTaxDetails(feeDetailVb);
				if (retVal != Constants.SUCCESSFUL_OPERATION){
					exceptionCode = getResultObject(retVal);
					throw buildRuntimeCustomException(exceptionCode);
				}
				if(!"F".equalsIgnoreCase(feeDetailVb.getTaxType())) {
					exceptionCode=taxConfigTierDao.doInsertApprTaxTier(feeDetailVb);	
				}
			}
		}
		exceptionCode = getResultObject(Constants.SUCCESSFUL_OPERATION);
		return exceptionCode;
	}
	public ExceptionCode deleteAndInsertPendTaxDetail(TaxLineConfigHeaderVb vObject) throws RuntimeCustomException {
		ExceptionCode exceptionCode =  new ExceptionCode();
		List<TaxLineConfigDetailsVb> collTemp = null;
		collTemp = getTaxLineConfigDetails(vObject, 1);
		if (collTemp != null && collTemp.size() > 0 ){
			int delCnt = deleteTaxDetailsPend(vObject);
		}
		List<TaxConfigTierVb> collTempTier = null;
		collTempTier =  taxConfigTierDao.getTaxConfigTierByGroup(vObject, 1);
		if (collTempTier != null && collTempTier.size() > 0 ){
			int delCnt = taxConfigTierDao.deleteTaxTierPend(vObject);
		}
		List<TaxLineConfigDetailsVb> detaillst = vObject.getTaxLineConfigDetaillst();
		if(detaillst != null && !detaillst.isEmpty()) {
			for(TaxLineConfigDetailsVb feeDetailVb : detaillst){
				feeDetailVb.setCountry(vObject.getCountry());
				feeDetailVb.setLeBook(vObject.getLeBook());
				feeDetailVb.setTaxLineId(vObject.getTaxLineId());
				feeDetailVb.setEffectiveDate(vObject.getEffectiveDate());
				feeDetailVb.setTaxType(vObject.getTaxChargeType());
				if(ValidationUtil.isValid(feeDetailVb.getTaxAmt()))
					feeDetailVb.setTaxAmt(feeDetailVb.getTaxAmt().replaceAll(",", ""));
				else
					feeDetailVb.setTaxAmt("0");
				if(ValidationUtil.isValid(feeDetailVb.getTaxPercentage()))
					feeDetailVb.setTaxPercentage(feeDetailVb.getTaxPercentage().replaceAll(",", ""));
				else
					feeDetailVb.setTaxPercentage("0");
				feeDetailVb.setPlGL(feeDetailVb.getPlGL());
				feeDetailVb.setOfficeAccount(feeDetailVb.getOfficeAccount());
				retVal = doInsertionPendTaxDetails(feeDetailVb);
				if (retVal != Constants.SUCCESSFUL_OPERATION){
					exceptionCode = getResultObject(retVal);
					throw buildRuntimeCustomException(exceptionCode);
				}
				if(!"F".equalsIgnoreCase(vObject.getTaxChargeType()) && feeDetailVb.getTaxTierlst() != null && feeDetailVb.getTaxTierlst().size() > 0) {
					exceptionCode=taxConfigTierDao.doInsertPendTaxTier(feeDetailVb);
				}
			}	
		}
		exceptionCode = getResultObject(Constants.SUCCESSFUL_OPERATION);
		return exceptionCode;
	}
	@Override
	protected String getAuditString(TaxLineConfigDetailsVb vObject){
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
			
			if(ValidationUtil.isValid(vObject.getTaxAmt()))
				strAudit.append("TAX_AMT"+auditDelimiterColVal+vObject.getTaxAmt().trim());
			else
				strAudit.append("TAX_AMT"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			
			if(ValidationUtil.isValid(vObject.getTaxPercentage()))
				strAudit.append("TAX_PERCENTAGE"+auditDelimiterColVal+vObject.getTaxPercentage().trim());
			else
				strAudit.append("TAX_PERCENTAGE"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			
			if (ValidationUtil.isValid(vObject.getPlGL()))
				strAudit.append("PL_GL" + auditDelimiterColVal + vObject.getPlGL().trim());
			else
				strAudit.append("PL_GL" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);
			
			if (ValidationUtil.isValid(vObject.getOfficeAccount()))
				strAudit.append("OFFICE_ACCOUNT" + auditDelimiterColVal + vObject.getOfficeAccount().trim());
			else
				strAudit.append("OFFICE_ACCOUNT" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);
			
			if (ValidationUtil.isValid(vObject.getLookupAmountType()))
				strAudit.append("LOOKUP_AMOUNT_TYPE" + auditDelimiterColVal + vObject.getLookupAmountType().trim());
			else
				strAudit.append("LOOKUP_AMOUNT_TYPE" + auditDelimiterColVal + "NULL");
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