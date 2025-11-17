package com.vision.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.vision.authentication.CustomContextHolder;
import com.vision.exception.ExceptionCode;
import com.vision.exception.RuntimeCustomException;
import com.vision.util.Constants;
import com.vision.util.ValidationUtil;
import com.vision.vb.EtlManagerDetailsVb;
import com.vision.vb.EtlManagerVb;

@Component
public class EtlManagerDetailsDao extends AbstractDao<EtlManagerDetailsVb> {
	
	protected RowMapper getEtlDetailMapper(){
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				EtlManagerDetailsVb vObject = new EtlManagerDetailsVb();
				vObject.setCountry(rs.getString("COUNTRY"));
				vObject.setLeBook(rs.getString("LE_BOOK"));
				vObject.setExtractionFrequency(rs.getString("EXTRACTION_FREQUENCY"));
				vObject.setExtractionSequence(rs.getString("EXTRACTION_SEQUENCE"));
				vObject.setFeedCategoryAt(rs.getInt("FEED_CATEGORY_AT"));
				vObject.setFeedCategory(rs.getString("FEED_CATEGORY"));
				vObject.setFeedId(rs.getString("FEED_ID"));
				vObject.setDebugMode(rs.getString("DEBUG_MODE"));
				vObject.setDependentFlag(rs.getString("DEPENDENT_FLAG"));
				vObject.setDependentFeedId (rs.getString("DEPENDENT_FEED_ID"));
				vObject.setFeedSequence(rs.getInt("FEED_SEQUENCE"));
				vObject.setExtractionStatus(rs.getString("EXTRACTION_STATUS"));
				vObject.setExtractionStatusDesc(rs.getString("EXTRACTION_STATUS_DESC"));
				return vObject;
			}
		};
		return mapper;
	}
	@Override
	protected void setServiceDefaults() {
		serviceName = "ExtractionManagerDetails";
		serviceDesc = "Extraction Manager Details";
		tableName = "RA_MST_EXTRACTION_DETAIL";
		childTableName = "RA_MST_EXTRACTION_DETAIL";
		intCurrentUserId = CustomContextHolder.getContext().getVisionId();
	}
	public List<EtlManagerDetailsVb> getQueryEtlDetails(EtlManagerVb vObject,int intStatus){
		List<EtlManagerDetailsVb> collTemp = null;
		String query = "";
		try
		{	
			if(!vObject.isVerificationRequired() || vObject.isReview()){intStatus =0;}
				query = "SELECT COUNTRY,LE_BOOK,EXTRACTION_FREQUENCY,EXTRACTION_SEQUENCE,FEED_CATEGORY_AT,FEED_CATEGORY, " +
					" FEED_ID,DEBUG_MODE,DEPENDENT_FLAG,DEPENDENT_FEED_ID,FEED_SEQUENCE,EXTRACTION_STATUS_NT,EXTRACTION_STATUS,"+
					"(SELECT NUM_SUBTAB_DESCRIPTION FROM NUM_SUB_TAB WHERE NUM_TAB = EXTRACTION_STATUS_NT AND NUM_SUB_TAB = EXTRACTION_STATUS)EXTRACTION_STATUS_DESC"+
					" FROM RA_MST_EXTRACTION_DETAIL TAPPR "+
				    " WHERE COUNTRY = ? AND LE_BOOK = ? AND EXTRACTION_FREQUENCY = ? AND EXTRACTION_SEQUENCE = ? ORDER BY FEED_SEQUENCE ";
			
			Object objParams[] = new Object[4];
			objParams[0] = new String(vObject.getCountry());// country
			objParams[1] = new String(vObject.getLeBook());
			objParams[2] = new String(vObject.getExtractionFrequency());
			objParams[3] = new Integer(vObject.getExtractionSequence());
			collTemp = getJdbcTemplate().query(query,objParams,getEtlDetailMapper());
			return collTemp;
		}catch(Exception ex){
			ex.printStackTrace();
			logger.error("Exception ");
			return null;
		}
	}
	protected int deleteEtlDetails(EtlManagerVb vObject){
		String query = "Delete from RA_MST_EXTRACTION_DETAIL where COUNTRY = ? AND LE_BOOK = ? AND EXTRACTION_FREQUENCY = ? AND EXTRACTION_SEQUENCE = ? ";
		Object[] args = {vObject.getCountry(),vObject.getLeBook(),vObject.getExtractionFrequency(),vObject.getExtractionSequence()};
		return getJdbcTemplate().update(query,args);
		
	}
	protected int doInsertionApprEtlDetails(EtlManagerDetailsVb vObject){
		String query =  " Insert Into RA_MST_EXTRACTION_DETAIL(COUNTRY,LE_BOOK, EXTRACTION_FREQUENCY, "+
				"EXTRACTION_SEQUENCE ,FEED_CATEGORY_AT,FEED_CATEGORY,FEED_ID,DEBUG_MODE,DEPENDENT_FLAG,DEPENDENT_FEED_ID,FEED_SEQUENCE,EXTRACTION_STATUS) "+
				" Values (?,?,?,?,?,?,?,?,?,?,?,?)";
		
		Object[] args = {vObject.getCountry(),vObject.getLeBook(),vObject.getExtractionFrequency(),
				vObject.getExtractionSequence() ,vObject.getFeedCategoryAt(),vObject.getFeedCategory(),vObject.getFeedId(),
				vObject.getDebugMode(),vObject.getDependentFlag(),vObject.getDependentFeedId(),vObject.getFeedSequence(),
				vObject.getExtractionStatus()};
		return getJdbcTemplate().update(query,args);
	}
	public ExceptionCode deleteAndInsertApprEtlDetail(EtlManagerVb vObject) throws RuntimeCustomException {
		ExceptionCode exceptionCode = new ExceptionCode();
		ArrayList<EtlManagerDetailsVb> etlDetaillst = (ArrayList<EtlManagerDetailsVb>)vObject.getEtlManagerDetaillst();
		List<EtlManagerDetailsVb> collTemp = null;
		collTemp = getQueryEtlDetails(vObject, Constants.STATUS_ZERO);
		StringJoiner unAvailGls = new StringJoiner(",");
		if (collTemp != null && collTemp.size() > 0 ){
			int delCnt = deleteEtlDetails(vObject);
		}
		for(EtlManagerDetailsVb vObjDetailVb : etlDetaillst) {
			vObjDetailVb.setCountry(vObject.getCountry());
			vObjDetailVb.setLeBook(vObject.getLeBook());
			vObjDetailVb.setExtractionFrequency(vObject.getExtractionFrequency());
			vObjDetailVb.setExtractionSequence(vObject.getExtractionSequence());
			retVal = doInsertionApprEtlDetails(vObjDetailVb);
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
	protected String getAuditString(EtlManagerDetailsVb vObject){
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
			
			if(ValidationUtil.isValid(vObject.getExtractionFrequency()))
				strAudit.append("EXTRACTION_FREQUENCY"+auditDelimiterColVal+vObject.getExtractionFrequency().trim());
			else
				strAudit.append("EXTRACTION_FREQUENCY"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			
			if(ValidationUtil.isValid(vObject.getCountry()))
				strAudit.append("EXTRACTION_SEQUENC"+auditDelimiterColVal+vObject.getExtractionSequence().trim());
			else
				strAudit.append("EXTRACTION_SEQUENC"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);

			if(ValidationUtil.isValid(vObject.getCountry()))
				strAudit.append("FEED_CATEGORY"+auditDelimiterColVal+vObject.getFeedCategory().trim());
			else
				strAudit.append("FEED_CATEGORY"+auditDelimiterColVal+"NULL");
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