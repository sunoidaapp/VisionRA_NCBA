package com.vision.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.vision.authentication.CustomContextHolder;
import com.vision.exception.ExceptionCode;
import com.vision.exception.RuntimeCustomException;
import com.vision.util.Constants;
import com.vision.util.ValidationUtil;
import com.vision.vb.TransLineGLVb;
import com.vision.vb.TransLineHeaderVb;

@Component
public class TransLinesGlDao extends AbstractDao<TransLineGLVb> {
	
	protected RowMapper getTransLineGLMapper(){
		RowMapper mapper = new RowMapper() {
			@Override
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				TransLineGLVb vObject = new TransLineGLVb();
				vObject.setCountry(rs.getString("COUNTRY"));
				vObject.setLeBook(rs.getString("LE_BOOK"));
				vObject.setTransLineId(rs.getString("TRANS_LINE_ID"));
				vObject.setBsGl(rs.getString("BS_GL"));
				vObject.setBsGlDesc(rs.getString("BS_GL_DESC"));
				if(!ValidationUtil.isValid(vObject.getBsGlDesc())) {
					vObject.setBsGlDesc("No Charges");
				}
				vObject.setbAcid(rs.getString("BACID"));
				vObject.setbAcidDesc(rs.getString("BACID_DESC"));
				vObject.setProductType(rs.getString("PRODUCT_TYPE"));
				vObject.setProductTypeDesc(rs.getString("PROD_TYPE_DESC"));
				vObject.setProductID(rs.getString("PRODUCT_ID"));
				vObject.setProductIDDesc(rs.getString("PRODUCT_ID_DESC"));
				vObject.setCcyCode(rs.getString("CCY_CODE"));
				return vObject;
			}
		};
		return mapper;
	}
	@Override
	protected void setServiceDefaults() {
		serviceName = "TransLineGlConfig";
		serviceDesc = "Product Line GL Config";
		tableName = "RA_MST_TRANS_LINE_GL";
		childTableName = "RA_MST_TRANS_LINE_GL";
		intCurrentUserId = CustomContextHolder.getContext().getVisionId();
	}
	public List<TransLineGLVb> getTransGLDetails(TransLineHeaderVb vObject,int intStatus){
		List<TransLineGLVb> collTemp = null;
		String query = "";
		try
		{	
			if(!vObject.isVerificationRequired() || vObject.isReview()){intStatus =0;}
			if(intStatus == Constants.STATUS_ZERO) {
				query = "SELECT COUNTRY,LE_BOOK,TRANS_LINE_ID,BS_GL,BACID,PRODUCT_TYPE, " +
					" (SELECT GL_DESCRIPTION FROM RA_GL_CODES WHERE VISION_GL=TAPPR.BS_GL AND COUNTRY=TAPPR.COUNTRY AND LE_BOOK= TAPPR.LE_BOOK) BS_GL_DESC, "+
					" (SELECT ACCOUNT_NAME FROM RA_ACCOUNTS WHERE ACCOUNT_NO=TAPPR.BACID AND COUNTRY=TAPPR.COUNTRY AND LE_BOOK= TAPPR.LE_BOOK) BACID_DESC,	 "+
					" (SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB = 50 AND ALPHA_SUB_TAB = PRODUCT_TYPE) PROD_TYPE_DESC, " + 
					" PRODUCT_ID," + 
					" (SELECT PRODUCT_DESC FROM RA_PAR_PRODUCT WHERE PRODUCT_TYPE = 'P' " + 
					"  AND PRODUCT_TYPE = TAPPR.PRODUCT_TYPE AND PRODUCT_ID =TAPPR.PRODUCT_ID ) PRODUCT_ID_DESC, " + 
					" CCY_CODE FROM RA_MST_TRANS_LINE_GL TAPPR  WHERE COUNTRY = ? AND LE_BOOK = ? AND TRANS_LINE_ID = ? ";
			}else {
				query = "SELECT COUNTRY,LE_BOOK,TRANS_LINE_ID,BS_GL,BACID,PRODUCT_TYPE, " + 
					" (SELECT GL_DESCRIPTION FROM RA_GL_CODES WHERE VISION_GL=TAPPR.BS_GL AND COUNTRY=TAPPR.COUNTRY AND LE_BOOK= TAPPR.LE_BOOK) BS_GL_DESC, "+
					" (SELECT ACCOUNT_NAME FROM RA_ACCOUNTS WHERE ACCOUNT_NO=TAPPR.BACID AND COUNTRY=TAPPR.COUNTRY AND LE_BOOK= TAPPR.LE_BOOK) BACID_DESC,	 "+
					" (SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB = 50 AND ALPHA_SUB_TAB = PRODUCT_TYPE) PROD_TYPE_DESC, " + 
					" PRODUCT_ID," + 
					" (SELECT PRODUCT_DESC FROM RA_PAR_PRODUCT WHERE PRODUCT_TYPE = 'P' " + 
					"  AND PRODUCT_TYPE = TAPPR.PRODUCT_TYPE AND PRODUCT_ID =TAPPR.PRODUCT_ID ) PRODUCT_ID_DESC, " + 
					" CCY_CODE FROM RA_MST_TRANS_LINE_GL_PEND TAPPR  WHERE COUNTRY = ? AND LE_BOOK = ? AND TRANS_LINE_ID = ? ";
			}
			
			Object objParams[] = new Object[3];
			objParams[0] = new String(vObject.getCountry());// country
			objParams[1] = new String(vObject.getLeBook());
			objParams[2] = new String(vObject.getTransLineId());
			collTemp = getJdbcTemplate().query(query,objParams,getTransLineGLMapper());
			return collTemp;
		}catch(Exception ex){
			ex.printStackTrace();
			logger.error("Exception ");
			return null;
		}
	}
	protected int deleteTransLineGlAppr(TransLineHeaderVb vObject){
		String query = "Delete from RA_MST_TRANS_LINE_GL where COUNTRY = ? AND LE_BOOK = ? AND TRANS_LINE_ID = ? ";
		Object[] args = {vObject.getCountry(),vObject.getLeBook(),vObject.getTransLineId()};
		return getJdbcTemplate().update(query,args);
		
	}
	protected int deleteTransLineGlPend(TransLineHeaderVb vObject){
		String query = "Delete from RA_MST_TRANS_LINE_GL_PEND where COUNTRY = ? AND LE_BOOK = ? AND TRANS_LINE_ID = ? ";
		Object[] args = {vObject.getCountry(),vObject.getLeBook(),vObject.getTransLineId()};
		return getJdbcTemplate().update(query,args);
		
	}
	protected int doInsertionApprTransLineGL(TransLineGLVb vObject){
		String query =  " Insert Into RA_MST_TRANS_LINE_GL(COUNTRY,LE_BOOK, TRANS_LINE_ID, "+
				"BS_GL ,BACID,PRODUCT_TYPE,PRODUCT_ID,CCY_CODE) "+
				" Values (?,?,?,?,?,?,?,?)";
		
		Object[] args = {vObject.getCountry(),vObject.getLeBook(),vObject.getTransLineId(),
				vObject.getBsGl(),vObject.getbAcid(),vObject.getProductType(),vObject.getProductID(),
				vObject.getCcyCode()};
		return getJdbcTemplate().update(query,args);
	}
	protected int doInsertionPendTransLineGL(TransLineGLVb vObject){
		String query =   "Insert Into RA_MST_TRANS_LINE_GL_PEND(COUNTRY,LE_BOOK, TRANS_LINE_ID, "+
				"BS_GL ,BACID,PRODUCT_TYPE,PRODUCT_ID,CCY_CODE) "+
				" Values (?,?,?,?,?,?,?,?)";
		
		Object[] args = {vObject.getCountry(),vObject.getLeBook(),vObject.getTransLineId(),
				vObject.getBsGl(),vObject.getbAcid(),vObject.getProductType(),vObject.getProductID(),
				vObject.getCcyCode()};
		return getJdbcTemplate().update(query,args);
	}
	/*protected int checkGlAvail(String visionGl,String transLineType) {
		try {
			String query =   " SELECT COUNT(1) FROM GL_CODES WHERE GL_STATUS = 0 "
					+ "AND GL_ATTRIBUTE_3 = ? and VISION_GL = ? ";
			
			Object objParams[] = new Object[2];
			objParams[0] = new String(transLineType);
			objParams[1] = new String(visionGl);	
			return getJdbcTemplate().queryForObject(query,objParams,int.class);
		}catch(Exception e) {
			return 0;
		}
	}*/
	@Transactional(rollbackForClassName={"com.vision.exception.RuntimeCustomException"})
	public ExceptionCode deleteAndInsertApprGl(TransLineHeaderVb vObject) throws RuntimeCustomException {
		ExceptionCode exceptionCode = new ExceptionCode();
		ArrayList<TransLineGLVb> transLineGllst = (ArrayList<TransLineGLVb>)vObject.getTransLineGllst();
		List<TransLineGLVb> collTemp = null;
		collTemp = getTransGLDetails(vObject, Constants.STATUS_ZERO);
		StringJoiner unAvailGls = new StringJoiner(",");
		if (collTemp != null && collTemp.size() > 0 ){
			int delCnt = deleteTransLineGlAppr(vObject);
		}
		/*for(TransLineGLVb vObjDetailVb : transLineGllst) {
			if(checkGlAvail(vObjDetailVb.getBsGl(),vObject.getTransLineSubType())  < 1) {
				unAvailGls.add(vObjDetailVb.getBsGl());
				continue;
			}
		}
		if(unAvailGls != null && unAvailGls.length() > 0) {
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorSevr("S");
			exceptionCode.setErrorMsg(unAvailGls.toString()+" - These GL's are not related to the selected Trans Line type !!");
			throw buildRuntimeCustomException(exceptionCode);
		}*/
		for(TransLineGLVb vObjDetailVb : transLineGllst) {
			vObjDetailVb.setCountry(vObject.getCountry());
			vObjDetailVb.setLeBook(vObject.getLeBook());
			vObjDetailVb.setTransLineId(vObject.getTransLineId());
			vObjDetailVb.setRecordIndicator(Constants.STATUS_ZERO);
			vObjDetailVb.setTransLineGLStatus(Constants.STATUS_ZERO);
			vObjDetailVb.setMaker(intCurrentUserId);
			vObjDetailVb.setVerifier(intCurrentUserId);
		
			retVal = doInsertionApprTransLineGL(vObjDetailVb);
			if (retVal != Constants.SUCCESSFUL_OPERATION){
				exceptionCode = getResultObject(retVal);
				throw buildRuntimeCustomException(exceptionCode);
			} else {
				exceptionCode = getResultObject(Constants.SUCCESSFUL_OPERATION);
			}
		}
		return exceptionCode;
	}
	public ExceptionCode deleteAndInsertPendGl(TransLineHeaderVb vObject) throws RuntimeCustomException {
		ExceptionCode exceptionCode = new ExceptionCode();
		ArrayList<TransLineGLVb> transLineGllst = (ArrayList<TransLineGLVb>)vObject.getTransLineGllst();
		List<TransLineGLVb> collTemp = null;
		collTemp = getTransGLDetails(vObject, 1);
		StringJoiner unAvailGls = new StringJoiner(",");
		if (collTemp != null && collTemp.size() > 0 ){
			int delCnt = deleteTransLineGlPend(vObject);
		}
		/*for(TransLineGLVb vObjDetailVb : transLineGllst) {
			if(checkGlAvail(vObjDetailVb.getBsGl(),vObject.getTransLineSubType())  < 1) {
				unAvailGls.add(vObjDetailVb.getBsGl());
				continue;
			}
		}
		if(unAvailGls != null && unAvailGls.length() > 0) {
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorSevr("F");
			exceptionCode.setErrorMsg(unAvailGls.toString()+" - These GL's are not related to the selected Trans Line type !!");
			throw buildRuntimeCustomException(exceptionCode);
		}*/
		for(TransLineGLVb vObjDetailVb : transLineGllst) {
			vObjDetailVb.setCountry(vObject.getCountry());
			vObjDetailVb.setLeBook(vObject.getLeBook());
			vObjDetailVb.setTransLineId(vObject.getTransLineId());
			vObjDetailVb.setRecordIndicator(Constants.STATUS_ZERO);
			vObjDetailVb.setTransLineGLStatus(Constants.STATUS_ZERO);
			vObjDetailVb.setMaker(intCurrentUserId);
			vObjDetailVb.setVerifier(intCurrentUserId);
			retVal = doInsertionPendTransLineGL(vObjDetailVb);
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
	protected String getAuditString(TransLineGLVb vObject){
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
			
			if(ValidationUtil.isValid(vObject.getCountry()))
				strAudit.append("BS_GL"+auditDelimiterColVal+vObject.getBsGl().trim());
			else
				strAudit.append("BS_GL"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			
			if(ValidationUtil.isValid(vObject.getCountry()))
				strAudit.append("BACID"+auditDelimiterColVal+vObject.getbAcid().trim());
			else
				strAudit.append("BACID"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			
			if(ValidationUtil.isValid(vObject.getCountry()))
				strAudit.append("PRODUCT_TYPE"+auditDelimiterColVal+vObject.getProductType().trim());
			else
				strAudit.append("PRODUCT_TYPE"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			
			if(ValidationUtil.isValid(vObject.getCountry()))
				strAudit.append("PRODUCT_ID"+auditDelimiterColVal+vObject.getProductID().trim());
			else
				strAudit.append("PRODUCT_ID"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			
			if(ValidationUtil.isValid(vObject.getCountry()))
				strAudit.append("CCY_CODE"+auditDelimiterColVal+vObject.getCcyCode().trim());
			else
				strAudit.append("CCY_CODE"+auditDelimiterColVal+"NULL");
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