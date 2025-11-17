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
import com.vision.vb.ExceptionConfigHeaderVb;
import com.vision.vb.ExceptionFilterVb;

@Component
public class ExceptionFilterDao extends AbstractDao<ExceptionFilterVb> {
	@Autowired
	CommonDao commonDao;
	
	@Value("${app.databaseType}")
	private String databaseType;
	protected RowMapper getManualExceptionFilterMapper(){
		RowMapper mapper = new RowMapper() {
			@Override
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				ExceptionFilterVb vObject = new ExceptionFilterVb();
				vObject.setCountry(rs.getString("COUNTRY"));
				vObject.setLeBook(rs.getString("LE_BOOK"));
				vObject.setExceptionReference(rs.getString("EXCEPTION_REFERENCE"));
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
		serviceName = "ManualExceptionFilter";
		serviceDesc = "Manual Exception Filter";
		tableName = "RA_Exception_Auto_Filters";
		childTableName = "RA_Exception_Auto_Filters";
		intCurrentUserId = CustomContextHolder.getContext().getVisionId();
	}
	public List<ExceptionFilterVb> getManualExceptionFilters(ExceptionConfigHeaderVb dObj,int intStatus){
		List<ExceptionFilterVb> collTemp = null;
		String query = "";
		String format = "";
		try
		{	
			if("ORACLE".equalsIgnoreCase(databaseType)) {
				if(!dObj.isVerificationRequired() || dObj.isReview()){intStatus =0;}
				if(intStatus == Constants.STATUS_ZERO) {
					query = " SELECT COUNTRY, LE_BOOK,  "+
							" EXCEPTION_REFERENCE, FILTER_SEQUENCE,FILTER_TABLE,FILTER_COLUMN , "+
							" CONDITION_OPERATION, CONDITION_VALUE1, CONDITION_VALUE2 "+
							" FROM RA_Exception_Auto_Filters TAPPR "+
							" WHERE TAPPR.COUNTRY = ? AND TAPPR.LE_BOOK = ? AND  " + 
							" TAPPR.EXCEPTION_REFERENCE = ?  ";
				}else {
					query = " SELECT COUNTRY, LE_BOOK,  "+
							" EXCEPTION_REFERENCE, FILTER_SEQUENCE,FILTER_TABLE,FILTER_COLUMN , "+
							" CONDITION_OPERATION, CONDITION_VALUE1, CONDITION_VALUE2 "+
							" FROM RA_Exception_Auto_Filters_PEND TPEND "+
							" WHERE TPEND.COUNTRY = ? AND TPEND.LE_BOOK = ? AND  " + 
							" TPEND.EXCEPTION_REFERENCE = ? ";
				}
				
			} else if("MSSQL".equalsIgnoreCase(databaseType)) {
				if(!dObj.isVerificationRequired() ){intStatus =0;}
				if(intStatus == Constants.STATUS_ZERO) {
					query = " SELECT  COUNTRY,LE_BOOK,EXCEPTION_REFERENCE,  "+
							" FILTER_SEQUENCE,FILTER_TABLE,FILTER_COLUMN , "+
							" CONDITION_OPERATION, CONDITION_VALUE1, CONDITION_VALUE2 "+
							" FROM RA_Exception_Auto_Filters TAPPR WHERE TAPPR.COUNTRY = ? AND TAPPR.LE_BOOK = ? AND  " 
							+ " TAPPR.EXCEPTION_REFERENCE = ? ";
				}else {
					query = " SELECT  COUNTRY,LE_BOOK,EXCEPTION_REFERENCE,  "+
							" FILTER_SEQUENCE,FILTER_TABLE,FILTER_COLUMN , "+
							" CONDITION_OPERATION, CONDITION_VALUE1, CONDITION_VALUE2 "+
							" FROM RA_Exception_Auto_Filters_PEND TPEND WHERE TPEND.COUNTRY = ? AND TPEND.LE_BOOK = ? AND  " 
							+ " TPEND.EXCEPTION_REFERENCE = ?  ";
				}
			}
			
			Object objParams[] = new Object[3];
			objParams[0] = new String(dObj.getCountry());// country
			objParams[1] = new String(dObj.getLeBook());
			objParams[2] = new String(dObj.getExceptionReference());
			
			collTemp = getJdbcTemplate().query(query,objParams,getManualExceptionFilterMapper());
			return collTemp;
		}catch(Exception ex){
			ex.printStackTrace();
			logger.error("Exception ");
			return null;
		}
	}
	protected int doInsertionApprManualExceptionFilter(ExceptionFilterVb vObject){
		String query =  " Insert Into RA_Exception_Auto_Filters(COUNTRY,LE_BOOK,"
				+ "EXCEPTION_REFERENCE," + 
				"  FILTER_SEQUENCE, FILTER_TABLE, FILTER_COLUMN, CONDITION_OPERATION,"+
				" CONDITION_VALUE1, CONDITION_VALUE2,RECORD_INDICATOR) "
				+ " Values (?,?,?,?,?,?,?,?,?,?)";
		
		Object[] args = { vObject.getCountry(), vObject.getLeBook(), vObject.getExceptionReference(),
				 vObject.getFilterSequence(), vObject.getFilterTable(), vObject.getFilterColumn(),
				vObject.getConditionOperation(), vObject.getConditionValue1(), vObject.getConditionValue2(),vObject.getRecordIndicator()};
		return getJdbcTemplate().update(query,args);
	}
	protected int doInsertionPendManualExceptionFilter(ExceptionFilterVb vObject){
		String query =  " Insert Into RA_Exception_Auto_Filters_PEND(COUNTRY,LE_BOOK,EXCEPTION_REFERENCE," + 
				"  FILTER_SEQUENCE, FILTER_TABLE, FILTER_COLUMN, CONDITION_OPERATION,"+
				" CONDITION_VALUE1, CONDITION_VALUE2,RECORD_INDICATOR ) "
				+ " Values (?,?,?,?,?,?,?,?,?,?)";
		
		Object[] args = { vObject.getCountry(), vObject.getLeBook(), vObject.getExceptionReference(),
				 vObject.getFilterSequence(), vObject.getFilterTable(), vObject.getFilterColumn(),
				vObject.getConditionOperation(), vObject.getConditionValue1(), vObject.getConditionValue2(),vObject.getRecordIndicator()};
		return getJdbcTemplate().update(query,args);
	}
	protected int deleteManualExceptionFilterAppr(ExceptionConfigHeaderVb vObject){
		String query = "Delete from RA_Exception_Auto_Filters WHERE COUNTRY= ? AND LE_BOOK= ? AND EXCEPTION_REFERENCE = ?";
		Object[] args = {vObject.getCountry(),vObject.getLeBook(),
				vObject.getExceptionReference()};
		return getJdbcTemplate().update(query,args);
		
	}
	protected int deleteManualExceptionFilterPend(ExceptionConfigHeaderVb vObject){
		String query = "Delete from RA_Exception_Auto_Filters_PEND WHERE COUNTRY= ? AND LE_BOOK= ? AND EXCEPTION_REFERENCE = ?";
		Object[] args = {vObject.getCountry(),vObject.getLeBook(),
				vObject.getExceptionReference()};
		return getJdbcTemplate().update(query,args);
		
	}
	public ExceptionCode deleteAndInsertApprFilter(ExceptionConfigHeaderVb vObject) throws RuntimeCustomException {
		ExceptionCode exceptionCode = new ExceptionCode();
		List<ExceptionFilterVb> collTemp = null;
		collTemp = getManualExceptionFilters(vObject, Constants.STATUS_ZERO);
		if (collTemp != null && collTemp.size() > 0 ){
			int delCnt = deleteManualExceptionFilterAppr(vObject);
		}
		List<ExceptionFilterVb> detaillst = vObject.getManualExceptionFilterlst();
		if(detaillst != null && !detaillst.isEmpty()) {
			for(ExceptionFilterVb ManualExceptionFilterVb : detaillst){
				ManualExceptionFilterVb.setRecordIndicator(vObject.getRecordIndicator());
				ManualExceptionFilterVb.setCountry(vObject.getCountry());
				ManualExceptionFilterVb.setLeBook(vObject.getLeBook());
				ManualExceptionFilterVb.setExceptionReference(vObject.getExceptionReference());
				retVal = doInsertionApprManualExceptionFilter(ManualExceptionFilterVb);
				writeAuditLog(ManualExceptionFilterVb, null);
				if (retVal != Constants.SUCCESSFUL_OPERATION){
					exceptionCode = getResultObject(retVal);
					throw buildRuntimeCustomException(exceptionCode);
				}
			}
		}
		exceptionCode = getResultObject(Constants.SUCCESSFUL_OPERATION);
		return exceptionCode;
	}
	public ExceptionCode deleteAndInsertPendFilter(ExceptionConfigHeaderVb vObject) throws RuntimeCustomException {
		ExceptionCode exceptionCode =  new ExceptionCode();
		List<ExceptionFilterVb> collTemp = null;
		collTemp = getManualExceptionFilters(vObject, 1);
		if (collTemp != null && collTemp.size() > 0 ){
			int delCnt = deleteManualExceptionFilterPend(vObject);
		}
		List<ExceptionFilterVb> detaillst = vObject.getManualExceptionFilterlst();
		if(detaillst != null && !detaillst.isEmpty()) {
			for(ExceptionFilterVb ManualExceptionFilterVb : detaillst){
				ManualExceptionFilterVb.setRecordIndicator(vObject.getRecordIndicator());
				ManualExceptionFilterVb.setCountry(vObject.getCountry());
				ManualExceptionFilterVb.setLeBook(vObject.getLeBook());
				ManualExceptionFilterVb.setExceptionReference(vObject.getExceptionReference());
				retVal = doInsertionPendManualExceptionFilter(ManualExceptionFilterVb);
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
	protected String getAuditString(ExceptionFilterVb vObject){
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
			
			if(ValidationUtil.isValid(vObject.getExceptionReference()))
				strAudit.append("EXCEPTION_REFERENCE"+auditDelimiterColVal+vObject.getExceptionReference().trim());
			else
				strAudit.append("EXCEPTION_REFERENCE"+auditDelimiterColVal+"NULL");
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