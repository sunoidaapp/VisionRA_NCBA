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
import com.vision.vb.ConcessionActivityConfigHeaderVb;
import com.vision.vb.ConcessionActivityFilterVb;

@Component
public class ConcessionActivityFilterDao extends AbstractDao<ConcessionActivityFilterVb> {
	@Autowired
	CommonDao commonDao;
	
	@Value("${app.databaseType}")
	private String databaseType;
	
	protected RowMapper getConcessionFilterMapper(){
		RowMapper mapper = new RowMapper() {
			@Override
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				ConcessionActivityFilterVb vObject = new ConcessionActivityFilterVb();
				vObject.setCountry(rs.getString("COUNTRY"));
				vObject.setLeBook(rs.getString("LE_BOOK"));
				vObject.setActivityId(rs.getString("ACTIVITY_ID"));
				vObject.setFilterSequence(rs.getInt("FILTER_SEQUENCE"));
				vObject.setLhsFilterTable(rs.getString("LHS_FILTER_TABLE"));
				vObject.setLhsFilterColumn(rs.getString("LHS_FILTER_COLUMN"));
				vObject.setRhsFilterTable(rs.getString("RHS_FILTER_TABLE"));
				vObject.setRhsFilterColumn(rs.getString("RHS_FILTER_COLUMN"));
				vObject.setConditionType(rs.getString("CONDITION_TYPE"));
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
		serviceName = "ConcessionActivityFilter";
		serviceDesc = "Concession Activity Filter";
		tableName = "RA_MST_CONCESSION_ACT_FILTER";
		childTableName = "RA_MST_CONCESSION_ACT_FILTER";
		intCurrentUserId = CustomContextHolder.getContext().getVisionId();
	}
	public List<ConcessionActivityFilterVb> getConcessionFilters(ConcessionActivityConfigHeaderVb dObj,int intStatus){
		List<ConcessionActivityFilterVb> collTemp = null;
		String query = "";
		String format = "";
		try
		{	
			if("ORACLE".equalsIgnoreCase(databaseType)) {
				if(!dObj.isVerificationRequired() || dObj.isReview()){intStatus =0;}
				if(intStatus == Constants.STATUS_ZERO) {
					query = " SELECT COUNTRY, LE_BOOK,ACTIVITY_ID,  "+
							"  FILTER_SEQUENCE,LHS_FILTER_TABLE,LHS_FILTER_COLUMN ,RHS_FILTER_TABLE,RHS_FILTER_COLUMN , "+
							" CONDITION_OPERATION, CONDITION_TYPE, CONDITION_VALUE1, CONDITION_VALUE2 "+
							" FROM RA_MST_CONCESSION_ACT_FILTER TAPPR "+
							" WHERE TAPPR.COUNTRY = ? AND TAPPR.LE_BOOK = ? AND  " + 
							" TAPPR.ACTIVITY_ID = ?  ";
				}else {
					query = " SELECT COUNTRY, LE_BOOK,ACTIVITY_ID,  "+
							" TO_CHAR(TPEND.'DD-Mon-RRRR')  FILTER_SEQUENCE,LHS_FILTER_TABLE,LHS_FILTER_COLUMN ,"+
							" RHS_FILTER_TABLE,RHS_FILTER_COLUMN , "+
							" CONDITION_OPERATION, CONDITION_TYPE, CONDITION_VALUE1, CONDITION_VALUE2 "+
							" FROM RA_MST_CONCESSION_ACT_FILTER_PEND TPEND "+
							" WHERE TPEND.COUNTRY = ? AND TPEND.LE_BOOK = ? AND  " + 
							" TPEND.ACTIVITY_ID = ?  ";
				}
				
			} else if("MSSQL".equalsIgnoreCase(databaseType)) {
				if(!dObj.isVerificationRequired() ){intStatus =0;}
				if(intStatus == Constants.STATUS_ZERO) {
					query = " SELECT  COUNTRY,LE_BOOK,ACTIVITY_ID,  "+
							" FILTER_SEQUENCE,LHS_FILTER_TABLE,LHS_FILTER_COLUMN ,RHS_FILTER_TABLE,RHS_FILTER_COLUMN , "+
							" CONDITION_OPERATION, CONDITION_TYPE, CONDITION_VALUE1, CONDITION_VALUE2 "+
							" FROM RA_MST_CONCESSION_ACT_FILTER TAPPR WHERE TAPPR.COUNTRY = ? AND TAPPR.LE_BOOK = ? AND  " 
							+ " TAPPR.ACTIVITY_ID = ? ";
				}else {
					query = " SELECT  COUNTRY,LE_BOOK,ACTIVITY_ID,  "+
							" FILTER_SEQUENCE,LHS_FILTER_TABLE,LHS_FILTER_COLUMN ,RHS_FILTER_TABLE,RHS_FILTER_COLUMN , "+
							" CONDITION_OPERATION, CONDITION_TYPE, CONDITION_VALUE1, CONDITION_VALUE2 "+
							" FROM RA_MST_CONCESSION_ACT_FILTER_PEND TPEND WHERE TPEND.COUNTRY = ? AND TPEND.LE_BOOK = ? AND  " 
							+ " TPEND.ACTIVITY_ID = ?  ";
				}
			}
			
			Object objParams[] = new Object[3];
			objParams[0] = new String(dObj.getCountry());// country
			objParams[1] = new String(dObj.getLeBook());
			objParams[2] = new String(dObj.getActivityId());
			
			collTemp = getJdbcTemplate().query(query,objParams,getConcessionFilterMapper());
			return collTemp;
		}catch(Exception ex){
			ex.printStackTrace();
			logger.error("Exception ");
			return null;
		}
	}

	protected int doInsertionApprConcessionFilter(ConcessionActivityFilterVb vObject) {
		String query = " INSERT INTO RA_MST_CONCESSION_ACT_FILTER(COUNTRY,LE_BOOK,ACTIVITY_ID,"
				+ "  FILTER_SEQUENCE, LHS_FILTER_TABLE,LHS_FILTER_COLUMN ,RHS_FILTER_TABLE,RHS_FILTER_COLUMN , "
				+ " CONDITION_OPERATION, CONDITION_TYPE," + " CONDITION_VALUE1, CONDITION_VALUE2," + " FILTER_STATUS,RECORD_INDICATOR_NT,"
				+ " RECORD_INDICATOR,MAKER,VERIFIER,DATE_LAST_MODIFIED,DATE_CREATION) "
				+ " Values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

		Object[] args = { vObject.getCountry(), vObject.getLeBook(), vObject.getActivityId(),
				vObject.getFilterSequence(), vObject.getLhsFilterTable(), vObject.getLhsFilterColumn(),
				vObject.getRhsFilterTable(),vObject.getRhsFilterColumn(),
				vObject.getConditionOperation(),vObject.getConditionType(), vObject.getConditionValue1(), vObject.getConditionValue2(),
				vObject.getFilterStatus(),vObject.getRecordIndicatorNt(), vObject.getRecordIndicator(), vObject.getMaker(), vObject.getVerifier(),
				vObject.getDateLastModified(), vObject.getDateCreation() };
		return getJdbcTemplate().update(query, args);
	}
	
	protected int doInsertionPendConcessionFilter(ConcessionActivityFilterVb vObject) {
		String query = " INSERT INTO RA_MST_CONCESSION_ACT_FILTER_PEND(COUNTRY,LE_BOOK,ACTIVITY_ID,"
				+ "  FILTER_SEQUENCE, LHS_FILTER_TABLE,LHS_FILTER_COLUMN ,RHS_FILTER_TABLE,RHS_FILTER_COLUMN , "
				+ " CONDITION_OPERATION, CONDITION_TYPE," + " CONDITION_VALUE1, CONDITION_VALUE2," + " FILTER_STATUS,RECORD_INDICATOR_NT,"
				+ " RECORD_INDICATOR,MAKER,VERIFIER,DATE_LAST_MODIFIED,DATE_CREATION) "
				+ " Values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

		Object[] args = { vObject.getCountry(), vObject.getLeBook(), vObject.getActivityId(),
				vObject.getFilterSequence(), vObject.getLhsFilterTable(), vObject.getLhsFilterColumn(),
				vObject.getRhsFilterTable(),vObject.getRhsFilterColumn(),
				vObject.getConditionOperation(),vObject.getConditionType(), vObject.getConditionValue1(), vObject.getConditionValue2(),
				vObject.getFilterStatus(), vObject.getRecordIndicatorNt(),vObject.getRecordIndicator(), vObject.getMaker(), vObject.getVerifier(),
				vObject.getDateLastModified(), vObject.getDateCreation() };
		return getJdbcTemplate().update(query, args);
	}
	
	protected int deleteConcessionFilterAppr(ConcessionActivityConfigHeaderVb vObject){
		try {
			String query = "Delete from RA_MST_CONCESSION_ACT_FILTER WHERE COUNTRY= ? AND LE_BOOK= ? AND ACTIVITY_ID = ?";
			Object[] args = {vObject.getCountry(),vObject.getLeBook(),vObject.getActivityId()};
			getJdbcTemplate().update(query,args);
			return Constants.SUCCESSFUL_OPERATION;
		}catch(Exception e) {
			return Constants.ERRONEOUS_OPERATION;
		}
	}
	protected int deleteConcessionFilterPend(ConcessionActivityConfigHeaderVb vObject){
		String query = "Delete from RA_MST_CONCESSION_ACT_FILTER_PEND WHERE COUNTRY= ? AND LE_BOOK= ? AND ACTIVITY_ID = ?";
		Object[] args = {vObject.getCountry(),vObject.getLeBook(),vObject.getActivityId()};
		return getJdbcTemplate().update(query,args);
		
	}
	public ExceptionCode deleteAndInsertApprFilter(ConcessionActivityConfigHeaderVb vObject) throws RuntimeCustomException {
		ExceptionCode exceptionCode = new ExceptionCode();
		List<ConcessionActivityFilterVb> collTemp = null;
		collTemp = getConcessionFilters(vObject, Constants.STATUS_ZERO);
		if (collTemp != null && collTemp.size() > 0 ){
			int delCnt = deleteConcessionFilterAppr(vObject);
		}
		List<ConcessionActivityFilterVb> detaillst = vObject.getConcessionJoinLst();
		if(detaillst != null && !detaillst.isEmpty()) {
			for(ConcessionActivityFilterVb concessionFilterVb : detaillst){
				concessionFilterVb.setRecordIndicator(vObject.getRecordIndicator());
				concessionFilterVb.setFilterStatus(vObject.getActivityStatus());
				concessionFilterVb.setDateCreation(vObject.getDateCreation());
				concessionFilterVb.setDateLastModified(vObject.getDateLastModified());
				concessionFilterVb.setMaker(vObject.getMaker());
				concessionFilterVb.setVerifier(vObject.getVerifier());
				concessionFilterVb.setCountry(vObject.getCountry());
				concessionFilterVb.setLeBook(vObject.getLeBook());
				concessionFilterVb.setActivityId(vObject.getActivityId());
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
	public ExceptionCode deleteAndInsertPendFilter(ConcessionActivityConfigHeaderVb vObject) throws RuntimeCustomException {
		ExceptionCode exceptionCode =  new ExceptionCode();
		List<ConcessionActivityFilterVb> collTemp = null;
		collTemp = getConcessionFilters(vObject, 1);
		if (collTemp != null && collTemp.size() > 0 ){
			int delCnt = deleteConcessionFilterPend(vObject);
		}
		List<ConcessionActivityFilterVb> detaillst = vObject.getConcessionJoinLst();
		if(detaillst != null && !detaillst.isEmpty()) {
			for(ConcessionActivityFilterVb concessionFilterVb : detaillst){
				concessionFilterVb.setRecordIndicator(vObject.getRecordIndicator());
				concessionFilterVb.setFilterStatus(vObject.getActivityStatus());
				concessionFilterVb.setDateCreation(vObject.getDateCreation());
				concessionFilterVb.setDateLastModified(vObject.getDateLastModified());
				concessionFilterVb.setMaker(vObject.getMaker());
				concessionFilterVb.setVerifier(vObject.getVerifier());
				concessionFilterVb.setCountry(vObject.getCountry());
				concessionFilterVb.setLeBook(vObject.getLeBook());
				concessionFilterVb.setActivityId(vObject.getActivityId());
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
	protected String getAuditString(ConcessionActivityFilterVb vObject){
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
			
			if(ValidationUtil.isValid(vObject.getActivityId()))
				strAudit.append("ACTIVITY_ID"+auditDelimiterColVal+vObject.getActivityId().trim());
			else
				strAudit.append("ACTIVITY_ID"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			
			if(ValidationUtil.isValid(vObject.getFilterSequence()))
				strAudit.append("FILTER_SEQUENCE"+auditDelimiterColVal+vObject.getFilterSequence());
			else
				strAudit.append("FILTER_SEQUENCE"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			
			if(ValidationUtil.isValid(vObject.getLhsFilterTable()))
				strAudit.append("LHS_FILTER_TABLE"+auditDelimiterColVal+vObject.getLhsFilterTable().trim());
			else
				strAudit.append("LHS_FILTER_TABLE"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			
			if(ValidationUtil.isValid(vObject.getLhsFilterColumn()))
				strAudit.append("LHS_FILTER_COLUMN"+auditDelimiterColVal+vObject.getLhsFilterColumn().trim());
			else
				strAudit.append("LHS_FILTER_COLUMN"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			if(ValidationUtil.isValid(vObject.getRhsFilterTable()))
				strAudit.append("RHS_FILTER_TABLE"+auditDelimiterColVal+vObject.getRhsFilterTable().trim());
			else
				strAudit.append("RHS_FILTER_TABLE"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			
			if(ValidationUtil.isValid(vObject.getRhsFilterColumn()))
				strAudit.append("RHS_FILTER_COLUMN"+auditDelimiterColVal+vObject.getRhsFilterColumn().trim());
			else
				strAudit.append("RHS_FILTER_COLUMN"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			
			
			if(ValidationUtil.isValid(vObject.getConditionType()))
				strAudit.append("CONDITION_TYPE"+auditDelimiterColVal+vObject.getConditionType().trim());
			else
				strAudit.append("CONDITION_TYPE"+auditDelimiterColVal+"NULL");
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