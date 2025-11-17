package com.vision.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.vision.authentication.CustomContextHolder;
import com.vision.exception.ExceptionCode;
import com.vision.exception.RuntimeCustomException;
import com.vision.util.Constants;
import com.vision.util.ValidationUtil;
import com.vision.vb.TransLineHeaderVb;
import com.vision.vb.TransLineSbuVb;
import com.vision.vb.VisionUsersVb;

@Component
public class TransLinesSbuDao extends AbstractDao<TransLineSbuVb> {
	protected RowMapper getTransLineSbuMapper(){
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				TransLineSbuVb vObject = new TransLineSbuVb();
				vObject.setCountry(rs.getString("COUNTRY"));
				vObject.setLeBook(rs.getString("LE_BOOK"));
				vObject.setTransLineId(rs.getString("TRANS_LINE_ID"));
				vObject.setBusinessVertical(rs.getString("BUSINESS_VERTICAL"));
				if(ValidationUtil.isValid(vObject.getBusinessVertical()) && "ALL".equalsIgnoreCase(vObject.getBusinessVertical()))
					vObject.setBusinessVerticalDesc(rs.getString("BUSINESS_VERTICAL"));
				else
					vObject.setBusinessVerticalDesc(rs.getString("BUSINESS_VERTICAL_DESC"));
				return vObject;
			}
		};
		return mapper;
	}
	@Override
	protected void setServiceDefaults() {
		serviceName = "TransLineSbuConfig";
		serviceDesc = "Trans Line SBU Config";
		tableName = "RA_MST_TRANS_LINE_SBU";
		childTableName = "RA_MST_TRANS_LINE_SBU";
		intCurrentUserId = CustomContextHolder.getContext().getVisionId();
	}
	public List<TransLineSbuVb> getTransSbuDetails(TransLineHeaderVb vObject,int intStatus){
		List<TransLineSbuVb> collTemp = null;
		StringBuffer query = new StringBuffer("");
		try
		{	
			if(!vObject.isVerificationRequired() || vObject.isReview()){intStatus =0;}
			if(intStatus == Constants.STATUS_ZERO) {
				query = new StringBuffer("SELECT COUNTRY,LE_BOOK,TRANS_LINE_ID,BUSINESS_VERTICAL,"
						+ " (select ALPHA_SUBTAB_DESCRIPTION FROM   ALPHA_SUB_TAB WHERE  ALPHA_TAB = 7040  "
						+ " AND ALPHA_SUB_TAB = BUSINESS_VERTICAL AND ALPHA_SUBTAB_STATUS = 0) BUSINESS_VERTICAL_DESC"
						+ " FROM RA_MST_TRANS_LINE_SBU WHERE COUNTRY = ? AND LE_BOOK = ? AND TRANS_LINE_ID = ? ");
			}else {
				query = new StringBuffer("SELECT COUNTRY,LE_BOOK,TRANS_LINE_ID,BUSINESS_VERTICAL,"
						+ " (select ALPHA_SUBTAB_DESCRIPTION FROM   ALPHA_SUB_TAB WHERE  ALPHA_TAB = 7040  "
						+ " AND ALPHA_SUB_TAB = BUSINESS_VERTICAL AND ALPHA_SUBTAB_STATUS = 0) BUSINESS_VERTICAL_DESC"
						+ " FROM RA_MST_TRANS_LINE_SBU_PEND WHERE COUNTRY = ? AND LE_BOOK = ? AND TRANS_LINE_ID = ? ");
			}
			VisionUsersVb visionUsersVb = CustomContextHolder.getContext();
			if(("Y".equalsIgnoreCase(visionUsersVb.getUpdateRestriction()))){
				if(ValidationUtil.isValid(visionUsersVb.getSbuCode())){
					query.append(" AND ");
					query.append(" BUSINESS_VERTICAL IN( '"+visionUsersVb.getSbuCode() + "') ");
				}
			}
			Object objParams[] = new Object[3];
			objParams[0] = new String(vObject.getCountry());// country
			objParams[1] = new String(vObject.getLeBook());
			objParams[2] = new String(vObject.getTransLineId());
			collTemp = getJdbcTemplate().query(query.toString(),objParams,getTransLineSbuMapper());
			return collTemp;
		}catch(Exception ex){
			ex.printStackTrace();
			logger.error("Exception ");
			return null;
		}
	}
	protected int deleteTransLineSbuAppr(TransLineHeaderVb vObject){
		String query = "Delete from RA_MST_TRANS_LINE_SBU where COUNTRY = ? AND LE_BOOK = ? AND TRANS_LINE_ID = ? ";
		Object[] args = {vObject.getCountry(),vObject.getLeBook(),vObject.getTransLineId()};
		return getJdbcTemplate().update(query,args);
		
	}
	protected int deleteTransLineSbuPend(TransLineHeaderVb vObject){
		String query = "Delete from RA_MST_TRANS_LINE_SBU_PEND where COUNTRY = ? AND LE_BOOK = ? AND TRANS_LINE_ID = ? ";
		Object[] args = {vObject.getCountry(),vObject.getLeBook(),vObject.getTransLineId()};
		return getJdbcTemplate().update(query,args);
	}
	protected int doInsertionApprTransLineSBU(TransLineSbuVb vObject){
		String query =  " Insert Into RA_MST_TRANS_LINE_SBU(COUNTRY,LE_BOOK,TRANS_LINE_ID,BUSINESS_VERTICAL) "+
				" Values (?,?,?,?)";
		
		Object[] args = {vObject.getCountry(),vObject.getLeBook(),vObject.getTransLineId(),vObject.getBusinessVertical()};
		return getJdbcTemplate().update(query,args);
	}
	protected int doInsertionPendTransLineSBU(TransLineSbuVb vObject){
		String query =  " Insert Into RA_MST_TRANS_LINE_SBU_PEND(COUNTRY,LE_BOOK,TRANS_LINE_ID,BUSINESS_VERTICAL) "+
				" Values (?,?,?,?)";
		
		Object[] args = {vObject.getCountry(),vObject.getLeBook(),vObject.getTransLineId(),vObject.getBusinessVertical()};
		return getJdbcTemplate().update(query,args);
	}
	@Transactional(rollbackForClassName={"com.vision.exception.RuntimeCustomException"})
	public ExceptionCode deleteAndInsertApprSbu(TransLineHeaderVb vObject) throws RuntimeCustomException {
		ExceptionCode exceptionCode = null;
		TransLineSbuVb transLineSbuVb = new TransLineSbuVb();
		List<TransLineSbuVb> collTemp = null;
		collTemp = getTransSbuDetails(vObject, Constants.STATUS_ZERO);
		if (collTemp != null && collTemp.size() > 0 ){
			int delCnt = deleteTransLineSbuAppr(vObject);
		}
		String[] businessVertical = vObject.getBusinessVertical().split(",");
		for(String s : businessVertical) {
			transLineSbuVb = new TransLineSbuVb();
			transLineSbuVb.setBusinessVertical(s);
			transLineSbuVb.setCountry(vObject.getCountry());
			transLineSbuVb.setLeBook(vObject.getLeBook());
			transLineSbuVb.setTransLineId(vObject.getTransLineId());
			transLineSbuVb.setRecordIndicator(Constants.STATUS_ZERO);
			transLineSbuVb.setDbStatus(Constants.STATUS_ZERO);
			transLineSbuVb.setMaker(intCurrentUserId);
			transLineSbuVb.setVerifier(intCurrentUserId);
			retVal = doInsertionApprTransLineSBU(transLineSbuVb);
			if (retVal != Constants.SUCCESSFUL_OPERATION){
				exceptionCode = getResultObject(retVal);
				throw buildRuntimeCustomException(exceptionCode);
			} else {
				exceptionCode = getResultObject(Constants.SUCCESSFUL_OPERATION);
			}
		}
		return exceptionCode;
	}
	public ExceptionCode deleteAndInsertPendSbu(TransLineHeaderVb vObject) throws RuntimeCustomException {
		ExceptionCode exceptionCode = null;
		TransLineSbuVb transLineSbuVb = new TransLineSbuVb(); 
		List<TransLineSbuVb> collTemp = null;
		collTemp = getTransSbuDetails(vObject, 1);
		if (collTemp != null && collTemp.size() > 0 ){
			int delCnt = deleteTransLineSbuPend(vObject);
		}
		String[] businessVertical = vObject.getBusinessVertical().split(",");
		for(String s : businessVertical) {
			transLineSbuVb = new TransLineSbuVb();
			transLineSbuVb.setBusinessVertical(s);
			transLineSbuVb.setCountry(vObject.getCountry());
			transLineSbuVb.setLeBook(vObject.getLeBook());
			transLineSbuVb.setTransLineId(vObject.getTransLineId());
			transLineSbuVb.setRecordIndicator(Constants.STATUS_ZERO);
			transLineSbuVb.setDbStatus(Constants.STATUS_ZERO);
			transLineSbuVb.setMaker(intCurrentUserId);
			transLineSbuVb.setVerifier(intCurrentUserId);
			retVal = doInsertionPendTransLineSBU(transLineSbuVb);
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
	protected String getAuditString(TransLineSbuVb vObject){
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
			
			if(ValidationUtil.isValid(vObject.getTransLineId()))
				strAudit.append("TRANS_LINE_ID"+auditDelimiterColVal+vObject.getTransLineId().trim());
			else
				strAudit.append("TRANS_LINE_ID"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			
			strAudit.append("BUSINESS_VERTICAL_AT"+auditDelimiterColVal+vObject.getBusinessVerticalAT());
			strAudit.append(auditDelimiter);
				
			strAudit.append("BUSINESS_VERTICAL"+auditDelimiterColVal+vObject.getBusinessVertical());
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