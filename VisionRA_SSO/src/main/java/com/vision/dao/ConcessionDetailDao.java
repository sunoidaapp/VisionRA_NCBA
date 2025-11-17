package com.vision.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.vision.authentication.CustomContextHolder;
import com.vision.util.Constants;
import com.vision.util.ValidationUtil;
import com.vision.vb.ConcessionHeaderDetailVb;

@Component
public class ConcessionDetailDao extends AbstractDao<ConcessionHeaderDetailVb> {
	@Autowired
	CommonDao commonDao;
	@Autowired
	//FeesConfigTierDao feesConfigTierDao;
	
	@Value("${app.databaseType}")
	private String databaseType;
	
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
	
	protected RowMapper getFeesConfigDetailMapper(){
		RowMapper mapper = new RowMapper() {
			@Override
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				ConcessionHeaderDetailVb vObject = new ConcessionHeaderDetailVb();
				vObject.setCountry(rs.getString("COUNTRY"));
				vObject.setLeBook(rs.getString("LE_BOOK"));
				vObject.setConcessionId(rs.getString("CONCESSION_ID"));
				vObject.setEffectiveDate(rs.getString("EFFECTIVE_DATE"));
				vObject.setTranCcy(rs.getString("TRAN_CCY"));
				vObject.setFeeAmt(rs.getString("FEE_AMT"));
				vObject.setFeePercentage(rs.getString("FEE_PERCENTAGE"));
				vObject.setPostingCcy(rs.getString("POSTING_CCY"));
				vObject.setCcyConversionType(rs.getString("CCY_CONVERSION_TYPE"));
				if(rs.getInt("INT_BASIS") != 0)
					vObject.setInterestBasis(rs.getInt("INT_BASIS"));
				else
					vObject.setInterestBasis(365);
				vObject.setFeeBasis(rs.getString("FEE_BASIS"));
				vObject.setPercentAmountType(rs.getString("PERCENT_AMOUNT_TYPE"));
				vObject.setPercentAmountTypeAt(rs.getString("PERCENT_AMOUNT_TYPE_AT"));
				vObject.setLookupAmountTypeAt(rs.getString("LOOKUP_AMOUNT_TYPE_AT"));
				vObject.setLookupAmountType(rs.getString("LOOKUP_AMOUNT_TYPE"));
				vObject.setRoundOffBasis(rs.getString("ROUND_OFF_BASIS"));
				vObject.setRoundOffBasisDecimal(rs.getInt("ROUND_OFF_DECIMAL"));
				vObject.setMinMaxCcyType(rs.getString("MIN_MAX_CCY_TYPE"));
				return vObject;
			}
		};
		return mapper;
	}
	@Override
	protected void setServiceDefaults() {
		serviceName = "FeesConfigDetails";
		serviceDesc = "Fees Config Details";
		tableName = "RA_MST_CONCESSION_DETAILS";
		childTableName = "RA_MST_CONCESSION_DETAILS";
		intCurrentUserId = CustomContextHolder.getContext().getVisionId();
	}
	public List<ConcessionHeaderDetailVb> getConcessionDetail(ConcessionHeaderDetailVb dObj,int intStatus){
		List<ConcessionHeaderDetailVb> collTemp = null;
		String query = "";
		String format = "";
		try
		{	
			if("ORACLE".equalsIgnoreCase(databaseType)) {
				if(!dObj.isVerificationRequired() || dObj.isReview()){intStatus =0;}
				if(intStatus == Constants.STATUS_ZERO) {
					query = " SELECT COUNTRY, LE_BOOK,CONCESSION_ID,  "+
							" TO_CHAR(TAPPR.EFFECTIVE_DATE,'DD-Mon-RRRR') EFFECTIVE_DATE, "+
							" TRIM(TO_CHAR(FEE_AMT,'999,999,999,999,990.99990')) FEE_AMT, "+
							" TRIM(TO_CHAR(FEE_PERCENTAGE,'990.90')) FEE_PERCENTAGE,TRAN_CCY,POSTING_CCY, CCY_CONVERSION_TYPE,INT_BASIS, FEE_BASIS,  "+
							" PERCENT_AMOUNT_TYPE , PERCENT_AMOUNT_TYPE_AT, LOOKUP_AMOUNT_TYPE, LOOKUP_AMOUNT_TYPE_AT,"+
							" ROUND_OFF_BASIS,ROUND_OFF_DECIMAL,MIN_MAX_CCY_TYPE "+
							" FROM RA_MST_CONCESSION_DETAILS TAPPR "+
							" WHERE TAPPR.COUNTRY = ? AND TAPPR.LE_BOOK = ? AND  " + 
							" TAPPR.CONCESSION_ID = ?  "+
							" AND TAPPR.EFFECTIVE_DATE = ? ";
				}else {
					query = " SELECT COUNTRY, LE_BOOK,CONCESSION_ID,  "+
							" TO_CHAR(TPEND.EFFECTIVE_DATE,'DD-Mon-RRRR') EFFECTIVE_DATE, "+
							" TRIM(TO_CHAR(FEE_AMT,'999,999,999,999,990.99990')) FEE_AMT, "+
							" TRIM(TO_CHAR(FEE_PERCENTAGE,'990.90')) FEE_PERCENTAGE,TRAN_CCY,POSTING_CCY, CCY_CONVERSION_TYPE,INT_BASIS, FEE_BASIS,  "+
							" PERCENT_AMOUNT_TYPE , PERCENT_AMOUNT_TYPE_AT, LOOKUP_AMOUNT_TYPE, LOOKUP_AMOUNT_TYPE_AT,"+
							" ROUND_OFF_BASIS,ROUND_OFF_DECIMAL,MIN_MAX_CCY_TYPE "+
							" FROM RA_MST_CONCESSION_DETAILS_PEND TPEND "+
							" WHERE TPEND.COUNTRY = ? AND TPEND.LE_BOOK = ? AND  " + 
							" TPEND.CONCESSION_ID = ?  "+
							" AND TPEND.EFFECTIVE_DATE = ? ";
				}
				
			} else if("MSSQL".equalsIgnoreCase(databaseType)) {
				if(!dObj.isVerificationRequired() || dObj.isReview()){intStatus =0;}
				if(intStatus == Constants.STATUS_ZERO) {
					query = " SELECT COUNTRY, LE_BOOK,CONCESSION_ID,  "+
							"  format(CAST(TAPPR.EFFECTIVE_DATE AS DATETIME), 'dd-MMM-yyyy')  EFFECTIVE_DATE, "+
							" RTRIM(LTRIM(" + getDbFunction("DATEFUNC")
							+ "(FEE_AMT,'N5'))) FEE_AMT,FEE_PERCENTAGE,TRAN_CCY,POSTING_CCY, CCY_CONVERSION_TYPE,INT_BASIS, FEE_BASIS,  "+
							" PERCENT_AMOUNT_TYPE , PERCENT_AMOUNT_TYPE_AT, LOOKUP_AMOUNT_TYPE, LOOKUP_AMOUNT_TYPE_AT,"+
							" ROUND_OFF_BASIS,ROUND_OFF_DECIMAL,MIN_MAX_CCY_TYPE "+
							" FROM RA_MST_CONCESSION_DETAILS TAPPR "+
							" WHERE TAPPR.COUNTRY = ? AND TAPPR.LE_BOOK = ? AND  " + 
							" TAPPR.CONCESSION_ID = ?  "+
							" AND TAPPR.EFFECTIVE_DATE = ? ";
				}else {
					query = " SELECT COUNTRY, LE_BOOK,CONCESSION_ID,  "+
							" format(CAST(TPEND.EFFECTIVE_DATE AS DATETIME), 'dd-MMM-yyyy')  EFFECTIVE_DATE, "+
							" RTRIM(LTRIM(" + getDbFunction("DATEFUNC")
							+ "(FEE_AMT,'N5'))) FEE_AMT,FEE_PERCENTAGE,TRAN_CCY,POSTING_CCY, CCY_CONVERSION_TYPE,INT_BASIS, FEE_BASIS,  "+
							" PERCENT_AMOUNT_TYPE , PERCENT_AMOUNT_TYPE_AT, LOOKUP_AMOUNT_TYPE, LOOKUP_AMOUNT_TYPE_AT,"+
							" ROUND_OFF_BASIS,ROUND_OFF_DECIMAL,MIN_MAX_CCY_TYPE "+
							" FROM RA_MST_CONCESSION_DETAILS_PEND TPEND "+
							" WHERE TPEND.COUNTRY = ? AND TPEND.LE_BOOK = ? AND  " + 
							" TPEND.CONCESSION_ID = ?  "+
							" AND TPEND.EFFECTIVE_DATE = ? ";
				}
			}
			
			
			Object objParams[] = new Object[4];
			objParams[0] = new String(dObj.getCountry());// country
			objParams[1] = new String(dObj.getLeBook());
			objParams[2] = new String(dObj.getConcessionId());
			objParams[3] = new String(dObj.getEffectiveDate());
			
			collTemp = getJdbcTemplate().query(query,objParams,getFeesConfigDetailMapper());
			return collTemp;
		}catch(Exception ex){
			ex.printStackTrace();
			logger.error("Exception ");
			return null;
		}
	}
	protected int doInsertionApprConcessionDetail(ConcessionHeaderDetailVb vObject){
		String query =  " Insert Into RA_MST_CONCESSION_DETAILS(COUNTRY,LE_BOOK,CONCESSION_ID," + 
				" EFFECTIVE_DATE, TRAN_CCY, FEE_AMT,FEE_PERCENTAGE,POSTING_CCY, CCY_CONVERSION_TYPE,"
				+ " INT_BASIS, FEE_BASIS, "
				+ " PERCENT_AMOUNT_TYPE , PERCENT_AMOUNT_TYPE_AT, LOOKUP_AMOUNT_TYPE, LOOKUP_AMOUNT_TYPE_AT,"
				+ " ROUND_OFF_BASIS_AT,ROUND_OFF_BASIS,ROUND_OFF_DECIMAL,MIN_MAX_CCY_TYPE,DATE_CREATION,DATE_LAST_MODIFIED ) "
				+ " Values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,"+getDbFunction("SYSDATE")+","+getDbFunction("SYSDATE")+")";
		
		Object[] args = { vObject.getCountry(), vObject.getLeBook(), vObject.getConcessionId(),
				vObject.getEffectiveDate(), vObject.getTranCcy(),
				vObject.getFeeAmt().replaceAll(",", ""), vObject.getFeePercentage().replaceAll(",", ""),
				vObject.getPostingCcy(), vObject.getCcyConversionType(),
				vObject.getInterestBasis(), vObject.getFeeBasis(), vObject.getPercentAmountType(),
				vObject.getPercentAmountTypeAt(), vObject.getLookupAmountType(), vObject.getLookupAmountTypeAt(),
				vObject.getRoundOffBasisAt(),vObject.getRoundOffBasis(),vObject.getRoundOffBasisDecimal(),vObject.getMinMaxCcyType()};
		return getJdbcTemplate().update(query,args);
	}
	protected int doInsertionPendConcessionDetails(ConcessionHeaderDetailVb vObject){
		String query =  " Insert Into RA_MST_CONCESSION_DETAILS_PEND(COUNTRY,LE_BOOK,CONCESSION_ID," + 
				" EFFECTIVE_DATE, TRAN_CCY, FEE_AMT,FEE_PERCENTAGE,POSTING_CCY, CCY_CONVERSION_TYPE,"
				+ " INT_BASIS, FEE_BASIS, "
				+ " PERCENT_AMOUNT_TYPE , PERCENT_AMOUNT_TYPE_AT, LOOKUP_AMOUNT_TYPE, LOOKUP_AMOUNT_TYPE_AT,"
				+ " ROUND_OFF_BASIS_AT,ROUND_OFF_BASIS,ROUND_OFF_DECIMAL,MIN_MAX_CCY_TYPE,DATE_CREATION,DATE_LAST_MODIFIED ) "
				+ " Values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,"+getDbFunction("SYSDATE")+","+getDbFunction("SYSDATE")+")";
		
		Object[] args = { vObject.getCountry(), vObject.getLeBook(), vObject.getConcessionId(),
				vObject.getEffectiveDate(), vObject.getTranCcy(),
				vObject.getFeeAmt().replaceAll(",", ""), vObject.getFeePercentage().replaceAll(",", ""),
				vObject.getPostingCcy(), vObject.getCcyConversionType(),
				vObject.getInterestBasis(), vObject.getFeeBasis(), vObject.getPercentAmountType(),
				vObject.getPercentAmountTypeAt(), vObject.getLookupAmountType(), vObject.getLookupAmountTypeAt(),
				vObject.getRoundOffBasisAt(),vObject.getRoundOffBasis(),vObject.getRoundOffBasisDecimal(),vObject.getMinMaxCcyType()};
		return getJdbcTemplate().update(query,args);
	}
	protected int doInsertionPendConcessionDetailDc(ConcessionHeaderDetailVb vObject){
		String query =  " Insert Into RA_MST_CONCESSION_DETAILS_PEND(COUNTRY,LE_BOOK,CONCESSION_ID," + 
				" EFFECTIVE_DATE, TRAN_CCY, FEE_AMT,FEE_PERCENTAGE,POSTING_CCY, CCY_CONVERSION_TYPE,"
				+ " INT_BASIS, FEE_BASIS, "
				+ " PERCENT_AMOUNT_TYPE , PERCENT_AMOUNT_TYPE_AT, LOOKUP_AMOUNT_TYPE, LOOKUP_AMOUNT_TYPE_AT,"
				+ " ROUND_OFF_BASIS_AT,ROUND_OFF_BASIS,ROUND_OFF_DECIMAL,MIN_MAX_CCY_TYPE,DATE_CREATION,DATE_LAST_MODIFIED ) "
				+ " Values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,"+getDbFunction("SYSDATE")+","+getDbFunction("SYSDATE")+")";
		
		Object[] args = { vObject.getCountry(), vObject.getLeBook(), vObject.getConcessionId(),
				vObject.getEffectiveDate(), vObject.getTranCcy(),
				vObject.getFeeAmt().replaceAll(",", ""), vObject.getFeePercentage().replaceAll(",", ""),
				vObject.getPostingCcy(), vObject.getCcyConversionType(),
				vObject.getInterestBasis(), vObject.getFeeBasis(), vObject.getPercentAmountType(),
				vObject.getPercentAmountTypeAt(), vObject.getLookupAmountType(), vObject.getLookupAmountTypeAt(),
				vObject.getRoundOffBasisAt(),vObject.getRoundOffBasis(),vObject.getRoundOffBasisDecimal(),vObject.getMinMaxCcyType()};
		return getJdbcTemplate().update(query,args);
	}
	protected int doUpdateApprHeader(ConcessionHeaderDetailVb vObject){
		String query = " Update RA_MST_CONCESSION_DETAIL set "+
				" TRAN_CCY= ?, FEE_AMT= ?,FEE_PERCENTAGE= ?,CCY_CONVERSION_TYPE= ?,"+
				" INT_BASIS= ?, FEE_BASIS= ?, "+
				" PERCENT_AMOUNT_TYPE = ?, PERCENT_AMOUNT_TYPE_AT= ?, LOOKUP_AMOUNT_TYPE= ?, LOOKUP_AMOUNT_TYPE_AT= ?,"+
				" ROUND_OFF_BASIS_AT= ?,ROUND_OFF_BASIS= ?,ROUND_OFF_DECIMAL= ?,MIN_MAX_CCY_TYPE "+
				" WHERE COUNTRY= ? AND LE_BOOK= ? AND CONCESSION_ID = ? "+
				" AND EFFECTIVE_DATE= ?";
		Object[] args = {vObject.getTranCcy(),
				vObject.getFeeAmt().replaceAll(",", ""), vObject.getFeePercentage().replaceAll(",", ""),
				vObject.getPostingCcy(), vObject.getCcyConversionType(),
				vObject.getInterestBasis(), vObject.getFeeBasis(), vObject.getPercentAmountType(),
				vObject.getPercentAmountTypeAt(), vObject.getLookupAmountType(), vObject.getLookupAmountTypeAt(),
				vObject.getRoundOffBasisAt(),vObject.getRoundOffBasis(),vObject.getRoundOffBasisDecimal(),vObject.getMinMaxCcyType(),
				vObject.getCountry(),vObject.getLeBook(),
				vObject.getConcessionId(),vObject.getEffectiveDate()};
		return getJdbcTemplate().update(query,args);
	}
	protected int doUpdatePendHeader(ConcessionHeaderDetailVb vObject){
		String query = " Update RA_MST_CONCESSION_DETAIL_PEND set "+
				" TRAN_CCY= ?, FEE_AMT= ?,FEE_PERCENTAGE= ?,CCY_CONVERSION_TYPE= ?,"+
				" INT_BASIS= ?, FEE_BASIS= ?, "+
				" PERCENT_AMOUNT_TYPE = ?, PERCENT_AMOUNT_TYPE_AT= ?, LOOKUP_AMOUNT_TYPE= ?, LOOKUP_AMOUNT_TYPE_AT= ?,"+
				" ROUND_OFF_BASIS_AT= ?,ROUND_OFF_BASIS= ?,ROUND_OFF_DECIMAL= ?,MIN_MAX_CCY_TYPE "+
				" WHERE COUNTRY= ? AND LE_BOOK= ? AND CONCESSION_ID = ? "+
				" AND EFFECTIVE_DATE= ?";
		Object[] args = {vObject.getTranCcy(),
				vObject.getFeeAmt().replaceAll(",", ""), vObject.getFeePercentage().replaceAll(",", ""),
				vObject.getPostingCcy(), vObject.getCcyConversionType(),
				vObject.getInterestBasis(), vObject.getFeeBasis(), vObject.getPercentAmountType(),
				vObject.getPercentAmountTypeAt(), vObject.getLookupAmountType(), vObject.getLookupAmountTypeAt(),
				vObject.getRoundOffBasisAt(),vObject.getRoundOffBasis(),vObject.getRoundOffBasisDecimal(),vObject.getMinMaxCcyType(),
				vObject.getCountry(),vObject.getLeBook(),
				vObject.getConcessionId(),vObject.getEffectiveDate()};
			return getJdbcTemplate().update(query,args);
	}
	protected int deleteConcessionDetailAppr(ConcessionHeaderDetailVb vObject){
		String query = "Delete from RA_MST_CONCESSION_DETAILS WHERE COUNTRY= ? AND LE_BOOK= ? AND CONCESSION_ID = ?"+
				"  AND EFFECTIVE_DATE= ? ";
		Object[] args = {vObject.getCountry(),vObject.getLeBook(),vObject.getConcessionId(),
				vObject.getEffectiveDate()};
		return getJdbcTemplate().update(query,args);
		
	}
	protected int deleteConcessionDetailPend(ConcessionHeaderDetailVb vObject){
		String query = "Delete from RA_MST_CONCESSION_DETAILS_PEND WHERE COUNTRY= ? AND LE_BOOK= ? AND CONCESSION_ID = ?"+
				"  AND EFFECTIVE_DATE= ? ";
		Object[] args = {vObject.getCountry(),vObject.getLeBook(),vObject.getConcessionId(),
				vObject.getEffectiveDate()};
		return getJdbcTemplate().update(query,args);
		
	}
	@Override
	protected String getAuditString(ConcessionHeaderDetailVb vObject){
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