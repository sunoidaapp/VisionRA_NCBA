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
import com.vision.vb.ConcessionConfigDetailsVb;
import com.vision.vb.ConcessionConfigHeaderVb;
import com.vision.vb.ConcessionConfigTierVb;

@Component
public class ConcessionConfigTierDao extends AbstractDao<ConcessionConfigTierVb> {
	@Autowired
	CommonDao commonDao;
	
	@Value("${app.databaseType}")
	private String databaseType;
	protected RowMapper getConcessionConfigTierMapper(){
		RowMapper mapper = new RowMapper() {
			@Override
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				ConcessionConfigTierVb vObject = new ConcessionConfigTierVb();
				vObject.setCountry(rs.getString("COUNTRY"));
				vObject.setLeBook(rs.getString("LE_BOOK"));
				vObject.setConcessionId(rs.getString("CONCESSION_ID"));
				vObject.setEffectiveDate(rs.getString("EFFECTIVE_DATE"));
				vObject.setTranCcy(rs.getString("TRAN_CCY"));
				vObject.setFeeSequence(rs.getInt("FEE_SEQUENCE"));
				vObject.setAmtFrom(rs.getString("AMT_FROM"));
				vObject.setAmtTo(rs.getString("AMT_TO"));
				vObject.setCountFrom(rs.getString("CNT_FROM"));
				vObject.setCountTo(rs.getString("CNT_TO"));
				vObject.setFeePercentage(String.valueOf(rs.getLong("FEE_PERCENTAGE")));
				vObject.setFeeAmt(rs.getString("FEE_AMT"));
				vObject.setFeeBasis(rs.getString("FEE_BASIS"));
				return vObject;
			}
		};
		return mapper;
	}
	@Override
	protected void setServiceDefaults() {
		serviceName = "ConcessionLineConfigTier";
		serviceDesc = "Concession Config Tier";
		tableName = "RA_MST_CONCESSION_TIER";
		childTableName = "RA_MST_CONCESSION_TIER";
		intCurrentUserId = CustomContextHolder.getContext().getVisionId();
	}
	public List<ConcessionConfigTierVb> getConcessionConfigTierByGroup(ConcessionConfigHeaderVb dObj,int intStatus){
		List<ConcessionConfigTierVb> collTemp = null;
		String query = "";
		try
		{	
			if ("ORACLE".equalsIgnoreCase(databaseType)) {
				if(!dObj.isVerificationRequired() || dObj.isReview()){intStatus =0;}
				if(intStatus == Constants.STATUS_ZERO) {
					query = " SELECT  COUNTRY,LE_BOOK,CONCESSION_ID,  "+
							" TO_CHAR(TAPPR.EFFECTIVE_DATE,'DD-Mon-RRRR') EFFECTIVE_DATE,"+
						    " TRAN_CCY,FEE_SEQUENCE,"
						    + "TRIM(TO_CHAR(AMT_FROM,'999,999,999,999,990.99990')) AMT_FROM,"+
							" TRIM(TO_CHAR(AMT_TO,'999,999,999,999,990.99990')) AMT_TO,"+
							" CNT_FROM,CNT_TO, "+
							" TRIM(TO_CHAR(FEE_AMT,'999,999,999,999,990.99990')) FEE_AMT,FEE_PERCENTAGE, FEE_BASIS "
							+" FROM RA_MST_CONCESSION_TIER TAPPR WHERE TAPPR.COUNTRY = ? AND TAPPR.LE_BOOK = ? AND " + 
							" TAPPR.CONCESSION_ID = ? AND  "+
							"  TAPPR.EFFECTIVE_DATE = ? ";
				}else {
					query = " SELECT  COUNTRY,LE_BOOK,CONCESSION_ID,  "+
							" TO_CHAR(TPEND.EFFECTIVE_DATE,'DD-Mon-RRRR') EFFECTIVE_DATE,"+
						    " TRAN_CCY,FEE_SEQUENCE,"
						    + "TRIM(TO_CHAR(AMT_FROM,'999,999,999,999,990.99990')) AMT_FROM,"+
							" TRIM(TO_CHAR(AMT_TO,'999,999,999,999,990.99990')) AMT_TO,"+
							" CNT_FROM,CNT_TO, "+
							" TRIM(TO_CHAR(FEE_AMT,'999,999,999,999,990.99990')) FEE_AMT,FEE_PERCENTAGE ,FEE_BASIS"
							+" FROM RA_MST_CONCESSION_TIER_PEND TPEND WHERE TPEND.COUNTRY = ? AND TPEND.LE_BOOK = ? AND " + 
							" TPEND.CONCESSION_ID = ? AND  "+
							"  TPEND.EFFECTIVE_DATE = ? ";
				}
			}else if ("MSSQL".equalsIgnoreCase(databaseType)){
				if(!dObj.isVerificationRequired() || dObj.isReview()){intStatus =0;}
				if(intStatus == Constants.STATUS_ZERO) {
					query = " SELECT  COUNTRY,LE_BOOK,CONCESSION_ID,  "+
							" FORMAT(CAST(EFFECTIVE_DATE AS DATETIME), 'dd-MMM-yyyy') EFFECTIVE_DATE,"+
						    " TRAN_CCY,FEE_SEQUENCE,RTRIM(LTRIM("+getDbFunction("DATEFUNC")+"(AMT_FROM,'N5'))) AMT_FROM,"+
							" RTRIM(LTRIM("+getDbFunction("DATEFUNC")+"(AMT_TO,'N5'))) AMT_TO,"+
							" CNT_FROM,CNT_TO, "+
							" RTRIM(LTRIM(" + getDbFunction("DATEFUNC")
							+ "(FEE_AMT,'N5')))  FEE_AMT,FEE_PERCENTAGE ,FEE_BASIS"
							+" FROM RA_MST_CONCESSION_TIER TAPPR WHERE TAPPR.COUNTRY = ? AND TAPPR.LE_BOOK = ? AND " + 
							" TAPPR.CONCESSION_ID = ? AND  "+
							"  TAPPR.EFFECTIVE_DATE = ? ";
				}else {
					query = " SELECT  COUNTRY,LE_BOOK,CONCESSION_ID,  "+
							" FORMAT(CAST(EFFECTIVE_DATE AS DATETIME), 'dd-MMM-yyyy') EFFECTIVE_DATE,"+
						    " TRAN_CCY,FEE_SEQUENCE,RTRIM(LTRIM("+getDbFunction("DATEFUNC")+"(AMT_FROM,'N5'))) AMT_FROM,"+
							" RTRIM(LTRIM("+getDbFunction("DATEFUNC")+"(AMT_TO,'N5'))) AMT_TO,"+
							" CNT_FROM,CNT_TO, "+
							" RTRIM(LTRIM(" + getDbFunction("DATEFUNC")
							+ "(FEE_AMT,'N5')))  FEE_AMT,FEE_PERCENTAGE ,FEE_BASIS"
							+" FROM RA_MST_CONCESSION_TIER_PEND TPEND WHERE TPEND.COUNTRY = ? AND TPEND.LE_BOOK = ? AND " + 
							" TPEND.CONCESSION_ID = ? AND  "+
							"  TPEND.EFFECTIVE_DATE = ? ";
				}
			}
			
			
			Object objParams[] = new Object[4];
			objParams[0] = new String(dObj.getCountry());// country
			objParams[1] = new String(dObj.getLeBook());
			objParams[2] = new String(dObj.getConcessionId());
			objParams[3] = new String(dObj.getEffectiveDate());
			
			collTemp = getJdbcTemplate().query(query,objParams,getConcessionConfigTierMapper());
			return collTemp;
		}catch(Exception ex){
			ex.printStackTrace();
			logger.error("Exception ");
			return null;
		}
	}
	public List<ConcessionConfigTierVb> getConcessionConfigTier(ConcessionConfigDetailsVb dObj,int intStatus){
		List<ConcessionConfigTierVb> collTemp = null;
		String query = "";
		String effectiveDate = "";
		try
		{	
			if ("ORACLE".equalsIgnoreCase(databaseType)) {
				if(!dObj.isVerificationRequired() || dObj.isReview()){intStatus =0;}
				if(intStatus == Constants.STATUS_ZERO) {
					query = " SELECT  COUNTRY,LE_BOOK,CONCESSION_ID,  "+
							" TO_CHAR(TAPPR.EFFECTIVE_DATE,'DD-Mon-RRRR')  EFFECTIVE_DATE,  "+
						    " TRAN_CCY,FEE_SEQUENCE, "+
							" TRIM(TO_CHAR(AMT_FROM,'999,999,999,999,990.99990')) AMT_FROM, "+
							" TRIM(TO_CHAR(AMT_TO,'999,999,999,999,990.99990')) AMT_TO, "+
							" CNT_FROM,CNT_TO, "+ 
							" TRIM(TO_CHAR(FEE_AMT,'999,999,999,999,990.99990')) FEE_AMT,FEE_PERCENTAGE ,FEE_BASIS "
							+" FROM RA_MST_CONCESSION_TIER TAPPR WHERE TAPPR.COUNTRY = ? AND TAPPR.LE_BOOK = ?  AND "+   
							" TAPPR.CONCESSION_ID = ? AND   "+
							" TAPPR.EFFECTIVE_DATE = ? "+ 
							"  AND TAPPR.TRAN_CCY = ? ";

				}else {
					query = " SELECT  COUNTRY,LE_BOOK,CONCESSION_ID,"+
							" TO_CHAR(TPEND.EFFECTIVE_DATE,'DD-Mon-RRRR')  EFFECTIVE_DATE,  "+
						    " TRAN_CCY,FEE_SEQUENCE, "+
							" TRIM(TO_CHAR(AMT_FROM,'999,999,999,999,990.99990')) AMT_FROM, "+
							" TRIM(TO_CHAR(AMT_TO,'999,999,999,999,990.99990')) AMT_TO, "+
							" CASE WHEN 'R' = '"+dObj.getFeeBasis()+"' THEN 1 ELSE CNT_FROM END CNT_FROM,CNT_TO, "+ 
							" TRIM(TO_CHAR(FEE_AMT,'999,999,999,999,990.99990')) FEE_AMT,FEE_PERCENTAGE  ,FEE_BASIS"
							+" FROM RA_MST_CONCESSION_TIER_PEND TPEND WHERE TPEND.COUNTRY = ? AND TPEND.LE_BOOK = ?  AND "+   
							" TPEND.CONCESSION_ID = ? AND   "+
							" TPEND.EFFECTIVE_DATE = ?  "+ 
							" AND TPEND.TRAN_CCY = ? ";
				}
			}else if ("MSSQL".equalsIgnoreCase(databaseType)){
				if(!dObj.isVerificationRequired() || dObj.isReview()){intStatus =0;}
				if(intStatus == Constants.STATUS_ZERO) {
					query = " SELECT DISTINCT COUNTRY,LE_BOOK,CONCESSION_ID,  "+
							" FORMAT(CAST(EFFECTIVE_DATE AS DATETIME), 'dd-MMM-yyyy') EFFECTIVE_DATE, "+
						    " TRAN_CCY,FEE_SEQUENCE,"+
						    " RTRIM(LTRIM("+getDbFunction("DATEFUNC")+"(AMT_FROM,'N5'))) AMT_FROM,"+
							" RTRIM(LTRIM("+getDbFunction("DATEFUNC")+"(AMT_TO,'N5'))) AMT_TO,"+
							" CASE WHEN 'R' = '"+dObj.getFeeBasis()+"' THEN '1' ELSE CNT_FROM END CNT_FROM,CNT_TO, "+
							" RTRIM(LTRIM(" + getDbFunction("DATEFUNC")
							+ "(FEE_AMT,'N5'))) FEE_AMT,FEE_PERCENTAGE,FEE_BASIS "
							+" FROM RA_MST_CONCESSION_TIER TAPPR WHERE TAPPR.COUNTRY = ? AND TAPPR.LE_BOOK = ?  AND " + 
							" TAPPR.CONCESSION_ID = ? AND  "+
							" TAPPR.EFFECTIVE_DATE = ? "+
							" AND TAPPR.TRAN_CCY = ? ";
				}else {
					query = " SELECT DISTINCT COUNTRY,LE_BOOK,CONCESSION_ID,  "+
							" FORMAT(CAST(EFFECTIVE_DATE AS DATETIME), 'dd-MMM-yyyy') EFFECTIVE_DATE, "+
						    " TRAN_CCY,FEE_SEQUENCE,"+
						    " RTRIM(LTRIM("+getDbFunction("DATEFUNC")+"(AMT_FROM,'N5'))) AMT_FROM,"+
							" RTRIM(LTRIM("+getDbFunction("DATEFUNC")+"(AMT_TO,'N5'))) AMT_TO,"+
							" CASE WHEN 'R' = '"+dObj.getFeeBasis()+"' THEN '1' ELSE CNT_FROM END CNT_FROM,CNT_TO, "+
							" RTRIM(LTRIM(" + getDbFunction("DATEFUNC")
							+ "(FEE_AMT,'N5'))) FEE_AMT,FEE_PERCENTAGE,FEE_BASIS"
							+ " FROM RA_MST_CONCESSION_TIER_PEND TPEND WHERE TPEND.COUNTRY = ? AND TPEND.LE_BOOK = ?  AND " + 
							" TPEND.CONCESSION_ID = ? AND  "+
							" TPEND.EFFECTIVE_DATE = ? "+
							" AND TPEND.TRAN_CCY = ? ";
				}
			}
			
			
			Object objParams[] = new Object[5];
			objParams[0] = new String(dObj.getCountry());// country
			objParams[1] = new String(dObj.getLeBook());
			objParams[2] = new String(dObj.getConcessionId());
			objParams[3] = new String(dObj.getEffectiveDate());
		 	objParams[4] = new String(dObj.getTranCcy());
			collTemp = getJdbcTemplate().query(query,objParams,getConcessionConfigTierMapper());
			return collTemp;
		}catch(Exception ex){
			ex.printStackTrace();
			logger.error("Exception ");
			return null;
		}
	}
	protected int doInsertionApprConcessionTier(ConcessionConfigTierVb vObject){
		
		String query =  " Insert Into RA_MST_CONCESSION_TIER(COUNTRY,LE_BOOK,CONCESSION_ID," + 
				" EFFECTIVE_DATE,   TRAN_CCY, FEE_SEQUENCE, AMT_FROM,"+
				"AMT_TO,CNT_FROM, CNT_TO, FEE_AMT,FEE_PERCENTAGE, FEE_BASIS,RECORD_INDICATOR) "
				+ " Values (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		
		Object[] args = {vObject.getCountry(),vObject.getLeBook(),vObject.getConcessionId(),
				vObject.getEffectiveDate(),
				vObject.getTranCcy(),vObject.getFeeSequence(),
				vObject.getAmtFrom().replaceAll(",",""),vObject.getAmtTo().replaceAll(",",""),vObject.getCountFrom().replaceAll(",",""),
				vObject.getCountTo().replaceAll(",",""),
				ValidationUtil.isValid(vObject.getFeeAmt())? vObject.getFeeAmt().replaceAll(",",""):0,
				ValidationUtil.isValid(vObject.getFeePercentage())?vObject.getFeePercentage().replaceAll(",", ""):0 ,
				vObject.getFeeBasis(),vObject.getRecordIndicator()};
		return getJdbcTemplate().update(query,args);
	}
	protected int doInsertionPendConcessionTier(ConcessionConfigTierVb vObject){
		String query =  " Insert Into RA_MST_CONCESSION_TIER_PEND(COUNTRY,LE_BOOK,CONCESSION_ID," + 
				" EFFECTIVE_DATE,   TRAN_CCY, FEE_SEQUENCE, AMT_FROM,"+
				"AMT_TO,CNT_FROM, CNT_TO, FEE_AMT,FEE_PERCENTAGE, FEE_BASIS,RECORD_INDICATOR) "
				+ " Values (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		
		Object[] args = {vObject.getCountry(),vObject.getLeBook(),vObject.getConcessionId(),
				vObject.getEffectiveDate(),
				vObject.getTranCcy(),vObject.getFeeSequence(),
				vObject.getAmtFrom().replaceAll(",",""),vObject.getAmtTo().replaceAll(",",""),vObject.getCountFrom().replaceAll(",",""),
				vObject.getCountTo().replaceAll(",",""),
				ValidationUtil.isValid(vObject.getFeeAmt())? vObject.getFeeAmt().replaceAll(",",""):0,
				ValidationUtil.isValid(vObject.getFeePercentage())?vObject.getFeePercentage().replaceAll(",", ""):0 ,
				vObject.getFeeBasis(),vObject.getRecordIndicator()};
		return getJdbcTemplate().update(query,args);
	}
	protected int deleteConcessionTierAppr(ConcessionConfigHeaderVb vObject){
		String query = "Delete from RA_MST_CONCESSION_TIER WHERE COUNTRY= ? AND LE_BOOK= ? AND CONCESSION_ID = ?"+
				"   AND EFFECTIVE_DATE = ?";
		Object[] args = {vObject.getCountry(),vObject.getLeBook(),vObject.getConcessionId(),
				vObject.getEffectiveDate()};
		return getJdbcTemplate().update(query,args);
		
	}
	protected int deleteConcessionTierPend(ConcessionConfigHeaderVb vObject){
		String query = "Delete from RA_MST_CONCESSION_TIER_PEND WHERE COUNTRY= ? AND LE_BOOK= ? AND CONCESSION_ID = ?"+
				" AND EFFECTIVE_DATE= ?";
		Object[] args = {vObject.getCountry(),vObject.getLeBook(),vObject.getConcessionId(),
				vObject.getEffectiveDate()};
		return getJdbcTemplate().update(query,args);
		
	}
	public ExceptionCode doInsertApprConcessionTier(ConcessionConfigDetailsVb vObject) throws RuntimeCustomException {
		ExceptionCode exceptionCode = new ExceptionCode();
		List<ConcessionConfigTierVb> tierlst = vObject.getConcessionTierlst();
		int ctr = 0;
		for(ConcessionConfigTierVb concessionTierVb : tierlst){
			concessionTierVb.setCountry(vObject.getCountry());
			concessionTierVb.setLeBook(vObject.getLeBook());
			concessionTierVb.setConcessionId(vObject.getConcessionId());
			concessionTierVb.setEffectiveDate(vObject.getEffectiveDate());
			concessionTierVb.setRecordIndicator(vObject.getRecordIndicator());
			concessionTierVb.setTranCcy(vObject.getTranCcy());
			concessionTierVb.setFeeSequence(concessionTierVb.getFeeSequence());
			concessionTierVb.setAmtFrom(concessionTierVb.getAmtFrom().replaceAll(",", ""));
			concessionTierVb.setAmtTo(concessionTierVb.getAmtTo().replaceAll(",", ""));
			concessionTierVb.setCountFrom(concessionTierVb.getCountFrom().replaceAll(",", ""));
			concessionTierVb.setCountTo(concessionTierVb.getCountTo().replaceAll(",", ""));
			concessionTierVb.setFeeAmt(concessionTierVb.getFeeAmt().replaceAll(",", ""));
			concessionTierVb.setFeePercentage(concessionTierVb.getFeePercentage().replaceAll(",", ""));
			concessionTierVb.setFeeBasis(concessionTierVb.getFeeBasis());
			retVal = doInsertionApprConcessionTier(concessionTierVb);
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
	public ExceptionCode doInsertPendConcessionTier(ConcessionConfigDetailsVb vObject) throws RuntimeCustomException {
		ExceptionCode exceptionCode = new ExceptionCode();
		List<ConcessionConfigTierVb> tierlst = vObject.getConcessionTierlst();
		int ctr = 0;
		for(ConcessionConfigTierVb concessionTierVb : tierlst){
			concessionTierVb.setCountry(vObject.getCountry());
			concessionTierVb.setLeBook(vObject.getLeBook());
			concessionTierVb.setConcessionId(vObject.getConcessionId());
			concessionTierVb.setEffectiveDate(vObject.getEffectiveDate());
			concessionTierVb.setRecordIndicator(vObject.getRecordIndicator());
			concessionTierVb.setTranCcy(vObject.getTranCcy());
			concessionTierVb.setFeeSequence(concessionTierVb.getFeeSequence());
			concessionTierVb.setAmtFrom(concessionTierVb.getAmtFrom().replaceAll(",", ""));
			concessionTierVb.setAmtTo(concessionTierVb.getAmtTo().replaceAll(",", ""));
			concessionTierVb.setCountFrom(concessionTierVb.getCountFrom().replaceAll(",", ""));
			concessionTierVb.setCountTo(concessionTierVb.getCountTo().replaceAll(",", ""));
			concessionTierVb.setFeeAmt(concessionTierVb.getFeeAmt().replaceAll(",", ""));
			concessionTierVb.setFeePercentage(concessionTierVb.getFeePercentage().replaceAll(",", ""));
			concessionTierVb.setFeeBasis(concessionTierVb.getFeeBasis());
			retVal = doInsertionPendConcessionTier(concessionTierVb);
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
	protected String getAuditString(ConcessionConfigTierVb vObject){
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
			
			if(ValidationUtil.isValid(vObject.getConcessionId()))
				strAudit.append("CONCESSION_ID"+auditDelimiterColVal+vObject.getConcessionId().trim());
			else
				strAudit.append("CONCESSION_ID"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			
			if(ValidationUtil.isValid(vObject.getEffectiveDate()))
				strAudit.append("EFFECTIVE_DATE"+auditDelimiterColVal+vObject.getEffectiveDate().trim());
			else
				strAudit.append("EFFECTIVE_DATE"+auditDelimiterColVal+"NULL");
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
				strAudit.append("FEE_AMT"+auditDelimiterColVal+vObject.getFeeAmt().trim());
			else
				strAudit.append("FEE_AMT"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			
			if(ValidationUtil.isValid(vObject.getFeePercentage()))
				strAudit.append("FEE_PERCENTAGE"+auditDelimiterColVal+vObject.getFeePercentage().trim());
			else
				strAudit.append("FEE_PERCENTAGE"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			
			if(ValidationUtil.isValid(vObject.getFeeBasis()))
				strAudit.append("FEE_BASIS"+auditDelimiterColVal+vObject.getFeeBasis().trim());
			else
				strAudit.append("FEE_BASIS"+auditDelimiterColVal+"NULL");
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