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
import com.vision.vb.ConcessionConfigHeaderVb;
import com.vision.vb.ConcessionFilterVb;
import com.vision.vb.SmartSearchVb;

@Component
public class ConcessionFilterDao extends AbstractDao<ConcessionFilterVb> {
	@Autowired
	CommonDao commonDao;
	@Autowired
	ConcessionConfigTierDao ConcessionConfigTierDao;
	
	@Value("${app.databaseType}")
	private String databaseType;
	
	private int FilterSequence = 0;
	private String FilterTable = "";
	private String FilterColumn = "";
	private String ConditionOperation = "";
	private String ConditionValue1 = "";
	private String ConditionValue2 = "";
	private int FilterStatusNt = 1;
	private int FilterStatus = 0;
	
	List<SmartSearchVb> smartSearchOpt = null;


	protected RowMapper getConcessionFilterMapper(){
		RowMapper mapper = new RowMapper() {
			@Override
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				ConcessionFilterVb vObject = new ConcessionFilterVb();
				vObject.setCountry(rs.getString("COUNTRY"));
				vObject.setLeBook(rs.getString("LE_BOOK"));
				vObject.setConcessionId(rs.getString("CONCESSION_ID"));
				vObject.setEffectiveDate(rs.getString("EFFECTIVE_DATE"));
				vObject.setFilterSequence(rs.getInt("FILTER_SEQUENCE"));
				vObject.setFilterTable(rs.getString("FILTER_TABLE"));
				vObject.setFilterColumn(rs.getString("FILTER_COLUMN"));
				vObject.setConditionOperation(rs.getString("CONDITION_OPERATION"));
				vObject.setConditionValue1(rs.getString("CONDITION_VALUE1"));
				vObject.setConditionValue2(rs.getString("CONDITION_VALUE2"));
				return vObject;
			}
		};
		return mapper;
	}
	@Override
	protected void setServiceDefaults() {
		serviceName = "ConcessionFilter";
		serviceDesc = "Concession Filter";
		tableName = "RA_MST_CONCESSION_FILTER";
		childTableName = "RA_MST_CONCESSION_FILTER";
		intCurrentUserId = CustomContextHolder.getContext().getVisionId();
	}
	public List<ConcessionFilterVb> getConcessionFilters(ConcessionConfigHeaderVb dObj,int intStatus){
		List<ConcessionFilterVb> collTemp = null;
		String query = "";
		String format = "";
		try
		{	
			if("ORACLE".equalsIgnoreCase(databaseType)) {
				if(!dObj.isVerificationRequired() || dObj.isReview()){intStatus =0;}
				if(intStatus == Constants.STATUS_ZERO) {
					query = " SELECT COUNTRY, LE_BOOK,CONCESSION_ID,  "+
							" TO_CHAR(TAPPR.EFFECTIVE_DATE,'DD-Mon-RRRR') EFFECTIVE_DATE, FILTER_SEQUENCE,FILTER_TABLE,FILTER_COLUMN , "+
							" CONDITION_OPERATION, CONDITION_VALUE1, CONDITION_VALUE2 "+
							" FROM RA_MST_CONCESSION_FILTER TAPPR "+
							" WHERE TAPPR.COUNTRY = ? AND TAPPR.LE_BOOK = ? AND  " + 
							" TAPPR.CONCESSION_ID = ?  "+
							" AND TAPPR.EFFECTIVE_DATE = ? ";
				}else {
					query = " SELECT COUNTRY, LE_BOOK,CONCESSION_ID,  "+
							" TO_CHAR(TPEND.EFFECTIVE_DATE,'DD-Mon-RRRR') EFFECTIVE_DATE, FILTER_SEQUENCE,FILTER_TABLE,FILTER_COLUMN , "+
							" CONDITION_OPERATION, CONDITION_VALUE1, CONDITION_VALUE2 "+
							" FROM RA_MST_CONCESSION_FILTER_PEND TPEND "+
							" WHERE TPEND.COUNTRY = ? AND TPEND.LE_BOOK = ? AND  " + 
							" TPEND.CONCESSION_ID = ?  "+
							" AND TPEND.EFFECTIVE_DATE = ? ";
				}
				
			} else if("MSSQL".equalsIgnoreCase(databaseType)) {
				if(!dObj.isVerificationRequired() ){intStatus =0;}
				if(intStatus == Constants.STATUS_ZERO) {
					query = " SELECT  COUNTRY,LE_BOOK,CONCESSION_ID,  "+
							" EFFECTIVE_DATE,FILTER_SEQUENCE,FILTER_TABLE,FILTER_COLUMN , "+
							" CONDITION_OPERATION, CONDITION_VALUE1, CONDITION_VALUE2 "+
							" FROM RA_MST_CONCESSION_FILTER TAPPR WHERE TAPPR.COUNTRY = ? AND TAPPR.LE_BOOK = ? AND  " 
							+ " TAPPR.CONCESSION_ID = ?  "
							+ "  AND TAPPR.EFFECTIVE_DATE = ? ";
				}else {
					query = " SELECT  COUNTRY,LE_BOOK,CONCESSION_ID,  "+
							" EFFECTIVE_DATE,FILTER_SEQUENCE,FILTER_TABLE,FILTER_COLUMN , "+
							" CONDITION_OPERATION, CONDITION_VALUE1, CONDITION_VALUE2 "+
							" FROM RA_MST_CONCESSION_FILTER_PEND TPEND WHERE TPEND.COUNTRY = ? AND TPEND.LE_BOOK = ? AND  " 
							+ " TPEND.CONCESSION_ID = ?  "
							+ "  AND TPEND.EFFECTIVE_DATE = ? ";
				}
			}
			
			Object objParams[] = new Object[4];
			objParams[0] = new String(dObj.getCountry());// country
			objParams[1] = new String(dObj.getLeBook());
			objParams[2] = new String(dObj.getConcessionId());
			objParams[3] = new String(dObj.getEffectiveDate());
			
			collTemp = getJdbcTemplate().query(query,objParams,getConcessionFilterMapper());
			return collTemp;
		}catch(Exception ex){
			ex.printStackTrace();
			logger.error("Exception ");
			return null;
		}
	}
	protected int doInsertionApprConcessionFilter(ConcessionFilterVb vObject){
		String query =  " Insert Into RA_MST_CONCESSION_FILTER(COUNTRY,LE_BOOK,CONCESSION_ID," + 
				" EFFECTIVE_DATE, FILTER_SEQUENCE, FILTER_TABLE, FILTER_COLUMN, CONDITION_OPERATION,"+
				" CONDITION_VALUE1, CONDITION_VALUE2,RECORD_INDICATOR) "
				+ " Values (?,?,?,?,?,?,?,?,?,?,?)";
		
		Object[] args = { vObject.getCountry(), vObject.getLeBook(), vObject.getConcessionId(),
				vObject.getEffectiveDate(), vObject.getFilterSequence(), vObject.getFilterTable(), vObject.getFilterColumn(),
				vObject.getConditionOperation(), vObject.getConditionValue1(), vObject.getConditionValue2(),vObject.getRecordIndicator()};
		return getJdbcTemplate().update(query,args);
	}
	protected int doInsertionPendConcessionFilter(ConcessionFilterVb vObject){
		String query =  " Insert Into RA_MST_CONCESSION_FILTER_PEND(COUNTRY,LE_BOOK,CONCESSION_ID," + 
				" EFFECTIVE_DATE, FILTER_SEQUENCE, FILTER_TABLE, FILTER_COLUMN, CONDITION_OPERATION,"+
				" CONDITION_VALUE1, CONDITION_VALUE2,RECORD_INDICATOR ) "
				+ " Values (?,?,?,?,?,?,?,?,?,?,?)";
		
		Object[] args = { vObject.getCountry(), vObject.getLeBook(), vObject.getConcessionId(),
				vObject.getEffectiveDate(), vObject.getFilterSequence(), vObject.getFilterTable(), vObject.getFilterColumn(),
				vObject.getConditionOperation(), vObject.getConditionValue1(), vObject.getConditionValue2(),vObject.getRecordIndicator()};
		return getJdbcTemplate().update(query,args);
	}
	protected int deleteConcessionFilterAppr(ConcessionConfigHeaderVb vObject){
		String query = "Delete from RA_MST_CONCESSION_FILTER WHERE COUNTRY= ? AND LE_BOOK= ? AND CONCESSION_ID = ?"+
				"  AND EFFECTIVE_DATE= ? ";
		Object[] args = {vObject.getCountry(),vObject.getLeBook(),vObject.getConcessionId(),
				vObject.getEffectiveDate()};
		return getJdbcTemplate().update(query,args);
		
	}
	protected int deleteConcessionFilterPend(ConcessionConfigHeaderVb vObject){
		String query = "Delete from RA_MST_CONCESSION_FILTER_PEND WHERE COUNTRY= ? AND LE_BOOK= ? AND CONCESSION_ID = ?"+
				"  AND EFFECTIVE_DATE= ? ";
		Object[] args = {vObject.getCountry(),vObject.getLeBook(),vObject.getConcessionId(),
				vObject.getEffectiveDate()};
		return getJdbcTemplate().update(query,args);
		
	}
	public ExceptionCode deleteAndInsertApprFilter(ConcessionConfigHeaderVb vObject) throws RuntimeCustomException {
		ExceptionCode exceptionCode = new ExceptionCode();
		List<ConcessionFilterVb> collTemp = null;
		collTemp = getConcessionFilters(vObject, Constants.STATUS_ZERO);
		if (collTemp != null && collTemp.size() > 0 ){
			int delCnt = deleteConcessionFilterAppr(vObject);
		}
		List<ConcessionFilterVb> detaillst = vObject.getConcessionFilterlst();
		if(detaillst != null && !detaillst.isEmpty()) {
			for(ConcessionFilterVb concessionFilterVb : detaillst){
				concessionFilterVb.setRecordIndicator(vObject.getRecordIndicator());
				concessionFilterVb.setCountry(vObject.getCountry());
				concessionFilterVb.setLeBook(vObject.getLeBook());
				concessionFilterVb.setConcessionId(vObject.getConcessionId());
				concessionFilterVb.setEffectiveDate(vObject.getEffectiveDate());
				retVal = doInsertionApprConcessionFilter(concessionFilterVb);
				writeAuditLog(concessionFilterVb, null);
				if (retVal != Constants.SUCCESSFUL_OPERATION){
					exceptionCode = getResultObject(retVal);
					throw buildRuntimeCustomException(exceptionCode);
				}
			}
		}
		exceptionCode = getResultObject(Constants.SUCCESSFUL_OPERATION);
		return exceptionCode;
	}
	public ExceptionCode deleteAndInsertPendFilter(ConcessionConfigHeaderVb vObject) throws RuntimeCustomException {
		ExceptionCode exceptionCode =  new ExceptionCode();
		List<ConcessionFilterVb> collTemp = null;
		collTemp = getConcessionFilters(vObject, 1);
		if (collTemp != null && collTemp.size() > 0 ){
			int delCnt = deleteConcessionFilterPend(vObject);
		}
		List<ConcessionFilterVb> detaillst = vObject.getConcessionFilterlst();
		if(detaillst != null && !detaillst.isEmpty()) {
			for(ConcessionFilterVb concessionFilterVb : detaillst){
				concessionFilterVb.setRecordIndicator(vObject.getRecordIndicator());
				concessionFilterVb.setCountry(vObject.getCountry());
				concessionFilterVb.setLeBook(vObject.getLeBook());
				concessionFilterVb.setConcessionId(vObject.getConcessionId());
				concessionFilterVb.setEffectiveDate(vObject.getEffectiveDate());
				retVal = doInsertionPendConcessionFilter(concessionFilterVb);
				if (retVal != Constants.SUCCESSFUL_OPERATION){
					exceptionCode = getResultObject(retVal);
					throw buildRuntimeCustomException(exceptionCode);
				}
			}	
		}
		exceptionCode = getResultObject(Constants.SUCCESSFUL_OPERATION);
		return exceptionCode;
	}
	

	@Override
	protected String getAuditString(ConcessionFilterVb vObject){
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
			
			if(ValidationUtil.isValid(vObject.getFilterSequence()))
				strAudit.append("FILTER_SEQUENCE"+auditDelimiterColVal+vObject.getFilterSequence());
			else
				strAudit.append("FILTER_SEQUENCE"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			
			if(ValidationUtil.isValid(vObject.getFilterTable()))
				strAudit.append("FILTER_TABLE"+auditDelimiterColVal+vObject.getFilterTable().trim());
			else
				strAudit.append("FILTER_TABLE"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			
			if(ValidationUtil.isValid(vObject.getFilterColumn()))
				strAudit.append("FILTER_COLUMN"+auditDelimiterColVal+vObject.getFilterColumn().trim());
			else
				strAudit.append("FILTER_COLUMN"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			
			if(ValidationUtil.isValid(vObject.getConditionOperation()))
				strAudit.append("CONDITION_OPERATION"+auditDelimiterColVal+vObject.getConditionOperation().trim());
			else
				strAudit.append("CONDITION_OPERATION"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			
			if(ValidationUtil.isValid(vObject.getConditionValue1()))
				strAudit.append("CONDITION_VALUE1"+auditDelimiterColVal+vObject.getConditionValue1().trim());
			else
				strAudit.append("CONDITION_VALUE1"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			
			if(ValidationUtil.isValid(vObject.getConditionValue2()))
				strAudit.append("CONDITION_VALUE2"+auditDelimiterColVal+vObject.getConditionValue2().trim());
			else
				strAudit.append("CONDITION_VALUE2"+auditDelimiterColVal+"NULL");
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