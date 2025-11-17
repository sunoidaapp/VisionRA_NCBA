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
import com.vision.vb.TransLineChannelVb;
import com.vision.vb.TransLineHeaderVb;

@Component
public class TransLinesChannelDao extends AbstractDao<TransLineChannelVb> {
	
	protected RowMapper getTransLineChannelMapper(){
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				TransLineChannelVb vObject = new TransLineChannelVb();
				vObject.setCountry(rs.getString("COUNTRY"));
				vObject.setLeBook(rs.getString("LE_BOOK"));
				vObject.setTransLineId(rs.getString("TRANS_LINE_ID"));
				vObject.setChannelId(rs.getString("CHANNEL_ID"));
				vObject.setChannelIdDesc(rs.getString("CHANNEL_ID_DESC"));
				return vObject;
			}
		};
		return mapper;
	}
	public List<TransLineChannelVb> getTransChannelDetails(TransLineHeaderVb vObject,int intStatus){
		List<TransLineChannelVb> collTemp = null;
		String query = "";
		try
		{	
			if(!vObject.isVerificationRequired() || vObject.isReview()){intStatus =0;}
			if(intStatus == Constants.STATUS_ZERO) {
				query = "SELECT COUNTRY,LE_BOOK,TRANS_LINE_ID, CHANNEL_ID,"+
						" (select ALPHA_SUBTAB_DESCRIPTION FROM   ALPHA_SUB_TAB WHERE  ALPHA_TAB = 7042 AND ALPHA_SUB_TAB = CHANNEL_ID AND ALPHA_SUBTAB_STATUS = 0)CHANNEL_ID_DESC"+
						" FROM RA_MST_TRANS_LINE_CHANNEL WHERE COUNTRY = ? AND LE_BOOK = ? AND TRANS_LINE_ID = ? ";
			}else {
				query = "SELECT COUNTRY,LE_BOOK,TRANS_LINE_ID,CHANNEL_ID,"+
						" (select ALPHA_SUBTAB_DESCRIPTION FROM   ALPHA_SUB_TAB WHERE  ALPHA_TAB = 7042 AND ALPHA_SUB_TAB = CHANNEL_ID AND ALPHA_SUBTAB_STATUS = 0)CHANNEL_ID_DESC"+
						" FROM RA_MST_TRANS_LINE_CHANNEL_PEND WHERE COUNTRY = ? AND LE_BOOK = ? AND TRANS_LINE_ID = ? ";
			}
			Object objParams[] = new Object[3];
			objParams[0] = new String(vObject.getCountry());// country
			objParams[1] = new String(vObject.getLeBook());
			objParams[2] = new String(vObject.getTransLineId());
			collTemp = getJdbcTemplate().query(query,objParams,getTransLineChannelMapper());
			return collTemp;
		}catch(Exception ex){
			ex.printStackTrace();
			logger.error("Exception ");
			return null;
		}
	}
	protected int deleteTransLineChannelAppr(TransLineHeaderVb vObject){
		String query = "Delete from RA_MST_TRANS_LINE_CHANNEL where COUNTRY = ? AND LE_BOOK = ? AND TRANS_LINE_ID = ? ";
		Object[] args = {vObject.getCountry(),vObject.getLeBook(),vObject.getTransLineId()};
		return getJdbcTemplate().update(query,args);
		
	}
	protected int deleteTransLineChannelPend(TransLineHeaderVb vObject){
		String query = "Delete from RA_MST_TRANS_LINE_CHANNEL_PEND where COUNTRY = ? AND LE_BOOK = ? AND TRANS_LINE_ID = ? ";
		Object[] args = {vObject.getCountry(),vObject.getLeBook(),vObject.getTransLineId()};
		return getJdbcTemplate().update(query,args);
		
	}
	protected int doInsertionApprTransLineChannel(TransLineChannelVb vObject){
		String query =  " Insert Into RA_MST_TRANS_LINE_CHANNEL(COUNTRY,LE_BOOK,TRANS_LINE_ID,CHANNEL_ID) "+
				" Values (?,?,?,?)";
		
		Object[] args = {vObject.getCountry(),vObject.getLeBook(),vObject.getTransLineId(),vObject.getChannelId()};
		return getJdbcTemplate().update(query,args);
	}
	protected int doInsertionPendTransLineChannel(TransLineChannelVb vObject){
		String query =  " Insert Into RA_MST_TRANS_LINE_CHANNEL_PEND(COUNTRY,LE_BOOK,TRANS_LINE_ID,CHANNEL_ID) "+
				" Values (?,?,?,?)";
		
		Object[] args = {vObject.getCountry(),vObject.getLeBook(),vObject.getTransLineId(),vObject.getChannelId()};
		return getJdbcTemplate().update(query,args);
	}
	@Transactional(rollbackForClassName={"com.vision.exception.RuntimeCustomException"})
	public ExceptionCode deleteAndInsertApprChannel(TransLineHeaderVb vObject) throws RuntimeCustomException {
		ExceptionCode exceptionCode = null;
		TransLineChannelVb transLineChannelVb = new TransLineChannelVb();
		List<TransLineChannelVb> collTemp = null;
		collTemp = getTransChannelDetails(vObject, Constants.STATUS_ZERO);
		if (collTemp != null && collTemp.size() > 0 ){
			int delCnt = deleteTransLineChannelAppr(vObject);
		}
		String[] channelId = vObject.getChannelId().split(",");
		for(String s : channelId) {
			transLineChannelVb = new TransLineChannelVb();
			transLineChannelVb.setChannelId(s);
			transLineChannelVb.setCountry(vObject.getCountry());
			transLineChannelVb.setLeBook(vObject.getLeBook());
			transLineChannelVb.setTransLineId(vObject.getTransLineId());
			transLineChannelVb.setRecordIndicator(Constants.STATUS_ZERO);
			transLineChannelVb.setDbStatus(Constants.STATUS_ZERO);
			transLineChannelVb.setMaker(intCurrentUserId);
			transLineChannelVb.setVerifier(intCurrentUserId);
			retVal = doInsertionApprTransLineChannel(transLineChannelVb);
			if (retVal != Constants.SUCCESSFUL_OPERATION){
				exceptionCode = getResultObject(retVal);
				throw buildRuntimeCustomException(exceptionCode);
			} else {
				exceptionCode = getResultObject(Constants.SUCCESSFUL_OPERATION);
			}
		}
		return exceptionCode;
	}
	public ExceptionCode deleteAndInsertPendChannel(TransLineHeaderVb vObject) throws RuntimeCustomException {
		ExceptionCode exceptionCode = null;
		TransLineChannelVb transLineChannelVb = new TransLineChannelVb(); 
		List<TransLineChannelVb> collTemp = null;
		collTemp = getTransChannelDetails(vObject, 1);
		if (collTemp != null && collTemp.size() > 0 ){
			int delCnt = deleteTransLineChannelPend(vObject);
		}
		String[] channelId = vObject.getChannelId().split(",");
		for(String s : channelId) {
			transLineChannelVb = new TransLineChannelVb();
			transLineChannelVb.setChannelId(s);
			transLineChannelVb.setCountry(vObject.getCountry());
			transLineChannelVb.setLeBook(vObject.getLeBook());
			transLineChannelVb.setTransLineId(vObject.getTransLineId());
			transLineChannelVb.setRecordIndicator(Constants.STATUS_ZERO);
			transLineChannelVb.setDbStatus(Constants.STATUS_ZERO);
			transLineChannelVb.setMaker(intCurrentUserId);
			transLineChannelVb.setVerifier(intCurrentUserId);
			retVal = doInsertionPendTransLineChannel(transLineChannelVb);
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
	protected void setServiceDefaults() {
		serviceName = "ServiceLineChannelConfig";
		serviceDesc = "Service Line Channel Config";
		tableName = "RA_MST_TRANS_LINE_CHANNEL";
		childTableName = "RA_MST_TRANS_LINE_CHANNEL";
		intCurrentUserId = CustomContextHolder.getContext().getVisionId();
	}
	@Override
	protected String getAuditString(TransLineChannelVb vObject){
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
			
			strAudit.append("CHANNEL_ID_AT"+auditDelimiterColVal+vObject.getChannelIdAT());
			strAudit.append(auditDelimiter);
				
			strAudit.append("CHANNEL_ID"+auditDelimiterColVal+vObject.getChannelId());
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
