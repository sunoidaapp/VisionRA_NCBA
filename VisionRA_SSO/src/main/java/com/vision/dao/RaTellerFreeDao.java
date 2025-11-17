package com.vision.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.UncategorizedSQLException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.vision.authentication.CustomContextHolder;
import com.vision.exception.ExceptionCode;
import com.vision.exception.RuntimeCustomException;
import com.vision.util.CommonUtils;
import com.vision.util.Constants;
import com.vision.util.ValidationUtil;
import com.vision.vb.RaTellerFreeVb;
import com.vision.vb.SmartSearchVb;
import com.vision.vb.VisionUsersVb;

@Component
public class RaTellerFreeDao extends AbstractDao<RaTellerFreeVb> {
	@Value("${app.databaseType}")
	private String databaseType;
	@Autowired
	CommonDao commonDao;
	
	String customerDescAppr = "(SELECT CUST_FIRST_NAME FROM RA_CUSTOMERS WHERE COUNTRY = TAPPR.COUNTRY AND LE_BOOK = TAPPR.LE_BOOK AND CUSTOMER_ID = TAPPR.CUSTOMER_ID) CUSTOMER_NAME, ";
	String customerDescPend = "(SELECT CUST_FIRST_NAME FROM RA_CUSTOMERS WHERE COUNTRY = TPEND.COUNTRY AND LE_BOOK = TPEND.LE_BOOK AND CUSTOMER_ID = TPEND.CUSTOMER_ID) CUSTOMER_NAME, ";
	
	String tellerBucketDescAppr = "(Select ALPHA_SUB_TAB"+getDbFunction("PIPELINE", null)+"' - '"+getDbFunction("PIPELINE", null)+"Alpha_subtab_description from Alpha_sub_tab where aLpha_Tab = 7060 and Alpha_Sub_tab = TAppr.Teller_Bucket ) Teller_Bucket_Desc, ";
	String tellerBucketDescPend = "(Select ALPHA_SUB_TAB"+getDbFunction("PIPELINE", null)+"' - '"+getDbFunction("PIPELINE", null)+"Alpha_subtab_description from Alpha_sub_tab where aLpha_Tab = 7060 and Alpha_Sub_tab = TPend.Teller_Bucket ) Teller_Bucket_Desc, ";
	
	String statusDescAppr = "(Select Num_subtab_description from Num_sub_tab where Num_Tab = 1 and Num_Sub_tab = TAppr.Teller_Free_Status ) Teller_Free_Status_Desc, ";
	String statusDescPend = "(Select Num_subtab_description from Num_sub_tab where Num_Tab = 1 and Num_Sub_tab = TPend.Teller_Free_Status ) Teller_Free_Status_Desc, ";
	
	String recordIndicatorDescAppr = "(Select Num_subtab_description from Num_sub_tab where Num_Tab = 7 and Num_Sub_tab = TAppr.Record_Indicator ) Record_Indicator_Desc ";
	String recordIndicatorDescPend = "(Select Num_subtab_description from Num_sub_tab where Num_Tab = 7 and Num_Sub_tab = TPend.Record_Indicator ) Record_Indicator_Desc ";
	
	@Override
	protected RowMapper getMapper(){
		RowMapper mapper = new RowMapper() {
			@Override
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				RaTellerFreeVb raTellerFreeVb = new RaTellerFreeVb();
				raTellerFreeVb.setCountry(rs.getString("COUNTRY"));
				raTellerFreeVb.setLeBook(rs.getString("LE_BOOK"));
				raTellerFreeVb.setEffectiveDateStart(rs.getString("EFFECTIVE_DATE"));
				raTellerFreeVb.setEffectiveDateEnd(rs.getString("EXPIRY_DATE"));
				raTellerFreeVb.setCustomerId(rs.getString("CUSTOMER_ID"));
				raTellerFreeVb.setCustomerName(rs.getString("CUSTOMER_NAME"));
				raTellerFreeVb.setTellerBucketAt(rs.getInt("TELLER_BUCKET_AT"));
				raTellerFreeVb.setTellerBucket(rs.getString("TELLER_BUCKET"));
				raTellerFreeVb.setTellerBucketDesc(rs.getString("TELLER_BUCKET_DESC"));
				raTellerFreeVb.setTellerFreeStatusNt(rs.getInt("TELLER_FREE_STATUS_NT"));
				raTellerFreeVb.setTellerFreeStatus(rs.getInt("TELLER_FREE_STATUS"));
				raTellerFreeVb.setTellerFreeStatusDesc(rs.getString("TELLER_FREE_STATUS_DESC"));
				raTellerFreeVb.setDbStatus(rs.getInt("TELLER_FREE_STATUS"));
				raTellerFreeVb.setRecordIndicatorNt(rs.getInt("RECORD_INDICATOR_NT"));
				raTellerFreeVb.setRecordIndicator(rs.getInt("RECORD_INDICATOR"));
				raTellerFreeVb.setRecordIndicatorDesc(rs.getString("RECORD_INDICATOR_Desc"));
				raTellerFreeVb.setMaker(rs.getInt("MAKER"));
				raTellerFreeVb.setMakerName(rs.getString("MAKER_NAME"));
				raTellerFreeVb.setVerifier(rs.getInt("VERIFIER"));
				raTellerFreeVb.setVerifierName(rs.getString("VERIFIER_NAME"));
				raTellerFreeVb.setDateLastModified(rs.getString("DATE_LAST_MODIFIED"));
				raTellerFreeVb.setDateCreation(rs.getString("DATE_CREATION"));
				return raTellerFreeVb;
			}
		};
		return mapper;
	}
	
	@Override
	public List<RaTellerFreeVb> getQueryPopupResults(RaTellerFreeVb dObj){
		StringBuffer strBufApprove = null;
		StringBuffer strBufPending = null;
		String strWhereNotExists = "";
		Vector<Object> params = new Vector<Object>();
		strBufApprove = new StringBuffer("Select * from (Select TAppr.COUNTRY,"
				+ " TAppr.LE_BOOK, TAppr.CUSTOMER_ID,TAppr.TELLER_BUCKET_AT,TAPPR.TELLER_BUCKET,"
				+ ""+commonDao.getDbFunction("DATEFUNC")+"(TAppr.EFFECTIVE_DATE, '"+commonDao.getDbFunction("DATEFORMAT")+"') EFFECTIVE_DATE,"
				+ ""+commonDao.getDbFunction("DATEFUNC")+"(TAppr.EXPIRY_DATE, '"+commonDao.getDbFunction("DATEFORMAT")+"') EXPIRY_DATE,"
				+ " TAppr.TELLER_FREE_STATUS_NT, TAppr.TELLER_FREE_STATUS,TAppr.RECORD_INDICATOR_NT,TAppr.RECORD_INDICATOR,"
				+ " TAppr.MAKER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "+commonDao.getDbFunction("NVL")+"(TAppr.MAKER,0) ) MAKER_NAME,"
				+ " TAppr.VERIFIER, (SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "+commonDao.getDbFunction("NVL")+"(TAppr.VERIFIER,0) ) VERIFIER_NAME,"
				+ " "+commonDao.getDbFunction("DATEFUNC")+"(TAppr.DATE_LAST_MODIFIED, '"+commonDao.getDbFunction("DATEFORMAT")+" "+commonDao.getDbFunction("TIME")+"') DATE_LAST_MODIFIED,TAppr.DATE_LAST_MODIFIED DATE_LAST_MODIFIED_1,"
				+ " "+commonDao.getDbFunction("DATEFUNC")+"(TAppr.DATE_CREATION, '"+commonDao.getDbFunction("DATEFORMAT")+" "+commonDao.getDbFunction("TIME")+"') DATE_CREATION, "
				+ customerDescAppr+tellerBucketDescAppr+statusDescAppr+recordIndicatorDescAppr
				+ " From RA_TELLER_FREE TAppr )TAppr ");
		 strWhereNotExists = new String( " Not Exists (Select 'X' From RA_TELLER_FREE_PEND TPend Where TPend.COUNTRY = TAppr.COUNTRY"
		 		+ " AND TPEND.LE_BOOK = TAPPR.LE_BOOK AND TPEND.CUSTOMER_ID = TAPPR.CUSTOMER_ID"
		 		+ " AND TPend.EFFECTIVE_DATE = TAppr.EFFECTIVE_DATE)");
		 strBufPending = new StringBuffer("Select * from (Select TPend.COUNTRY,"
					+ " TPend.LE_BOOK, TPend.CUSTOMER_ID,TPend.TELLER_BUCKET_AT,TPend.TELLER_BUCKET,"
					+ ""+commonDao.getDbFunction("DATEFUNC")+"(TPend.EFFECTIVE_DATE, '"+commonDao.getDbFunction("DATEFORMAT")+"') EFFECTIVE_DATE,"
					+ ""+commonDao.getDbFunction("DATEFUNC")+"(TPend.EXPIRY_DATE, '"+commonDao.getDbFunction("DATEFORMAT")+"') EXPIRY_DATE,"
					+ " TPend.TELLER_FREE_STATUS_NT, TPend.TELLER_FREE_STATUS,TPend.RECORD_INDICATOR_NT,TPend.RECORD_INDICATOR,"
					+ " TPend.MAKER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "+commonDao.getDbFunction("NVL")+"(TPend.MAKER,0) ) MAKER_NAME,"
					+ " TPend.VERIFIER, (SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "+commonDao.getDbFunction("NVL")+"(TPend.VERIFIER,0) ) VERIFIER_NAME,"
					+ " "+commonDao.getDbFunction("DATEFUNC")+"(TPend.DATE_LAST_MODIFIED, '"+commonDao.getDbFunction("DATEFORMAT")+" "+commonDao.getDbFunction("TIME")+"') DATE_LAST_MODIFIED,TPend.DATE_LAST_MODIFIED DATE_LAST_MODIFIED_1,"
					+ " "+commonDao.getDbFunction("DATEFUNC")+"(TPend.DATE_CREATION, '"+commonDao.getDbFunction("DATEFORMAT")+" "+commonDao.getDbFunction("TIME")+"') DATE_CREATION, "
					+ customerDescPend+tellerBucketDescPend+statusDescPend+recordIndicatorDescPend
					+ " From RA_TELLER_FREE_PEND TPend )TPend ");
		
		try
		{
			if (dObj.getSmartSearchOpt() != null && dObj.getSmartSearchOpt().size() > 0) {
				int count = 1;
				for (SmartSearchVb data: dObj.getSmartSearchOpt()){
					if(count == dObj.getSmartSearchOpt().size()) {
						data.setJoinType("");
					} else {
						if(!ValidationUtil.isValid(data.getJoinType()) && !("AND".equalsIgnoreCase(data.getJoinType()) || "OR".equalsIgnoreCase(data.getJoinType()))) {
							data.setJoinType("AND");
						}
					}
					String val = CommonUtils.criteriaBasedVal(data.getCriteria(), data.getValue());
					switch (data.getObject()) {
					
						case "country":
							CommonUtils.addToQuerySearch(" (upper(TAPPR.COUNTRY) "+ val+" )", strBufApprove, data.getJoinType());
							CommonUtils.addToQuerySearch(" (upper(TPend.COUNTRY) "+ val+" )", strBufPending, data.getJoinType());
							break;
						
						case "leBook":
							CommonUtils.addToQuerySearch(" ( upper(TAPPR.LE_BOOK) "+ val+" )", strBufApprove, data.getJoinType());
							CommonUtils.addToQuerySearch(" ( upper(TPend.LE_BOOK) "+ val+" )", strBufPending, data.getJoinType());
							break;
							
						case "customerId":
							CommonUtils.addToQuerySearch(" upper(TAPPR.CUSTOMER_ID) " + val, strBufApprove,
									data.getJoinType());
							CommonUtils.addToQuerySearch(" upper(TPend.CUSTOMER_ID) " + val, strBufPending,
									data.getJoinType());
							break;
	
						case "customerName":
							CommonUtils.addToQuerySearch(" upper(TAPPR.CUSTOMER_NAME) " + val, strBufApprove,
									data.getJoinType());
							CommonUtils.addToQuerySearch(" upper(TPend.CUSTOMER_NAME) " + val, strBufPending,
									data.getJoinType());
							break;
						case "tellerBucket":
							CommonUtils.addToQuerySearch(" (upper(TAPPR.TELLER_BUCKET) "+ val+" OR upper(TAPPR.TELLER_BUCKET_DESC) "+ val+")", strBufApprove, data.getJoinType());
							CommonUtils.addToQuerySearch(" (upper(TPend.TELLER_BUCKET) "+ val+" OR upper(TPend.TELLER_BUCKET_DESC) "+ val+")", strBufPending, data.getJoinType());
							break;
	
						case "tellerBucketDesc":
							CommonUtils.addToQuerySearch(" (upper(TAPPR.TELLER_BUCKET) "+ val+" OR upper(TAPPR.TELLER_BUCKET_DESC) "+ val+")", strBufApprove, data.getJoinType());
							CommonUtils.addToQuerySearch(" (upper(TPend.TELLER_BUCKET) "+ val+" OR upper(TPend.TELLER_BUCKET_DESC) "+ val+")", strBufPending, data.getJoinType());
							break;
	
						case "effectiveDateStart":
							CommonUtils.addToQuerySearch(" upper(TAPPR.EFFECTIVE_DATE) " + val, strBufApprove, data.getJoinType());
							CommonUtils.addToQuerySearch(" upper(TPend.EFFECTIVE_DATE)  " + val, strBufPending, data.getJoinType());
							break;
										
						case "effectiveDateEnd":
							CommonUtils.addToQuerySearch(" upper(TAPPR.EXPIRY_DATE) "  + val, strBufApprove, data.getJoinType());
							CommonUtils.addToQuerySearch(" upper(TPend.EXPIRY_DATE) "  + val, strBufPending, data.getJoinType());
							break;	
							
						case "dateCreation":
							CommonUtils.addToQuerySearch(" "+getDbFunction("DATEFUNC")+"(TAPPR.DATE_CREATION,'DD-MM-YYYY "+getDbFunction("TIME")+"') " + val, strBufApprove, data.getJoinType());
							CommonUtils.addToQuerySearch(" "+getDbFunction("DATEFUNC")+"(TPend.DATE_CREATION,'DD-MM-YYYY "+getDbFunction("TIME")+"')  " + val, strBufPending, data.getJoinType());
							break;
										
						case "dateLastModified":
							CommonUtils.addToQuerySearch(" "+getDbFunction("DATEFUNC")+"(TAPPR.DATE_LAST_MODIFIED,'DD-MM-YYYY "+getDbFunction("TIME")+"') "  + val, strBufApprove, data.getJoinType());
							CommonUtils.addToQuerySearch(" "+getDbFunction("DATEFUNC")+"(TPend.DATE_LAST_MODIFIED,'DD-MM-YYYY "+getDbFunction("TIME")+"') "  + val, strBufPending, data.getJoinType());
							break;
	
						case "makerName":
							CommonUtils.addToQuerySearch(" (TAPPR.MAKER_NAME) IN ("+ val+") ", strBufApprove, data.getJoinType());
							CommonUtils.addToQuerySearch(" (TPend.MAKER_NAME) IN ("+ val+") ", strBufPending, data.getJoinType());
							break;
							
						case "tellerFreeStatusDesc":
							CommonUtils.addToQuerySearch(" upper(TAPPR.Teller_Free_Status_Desc) "+ val, strBufApprove, data.getJoinType());
							CommonUtils.addToQuerySearch(" upper(TPEND.Teller_Free_Status_Desc) "+ val, strBufPending, data.getJoinType());
							break;
						
						case "recordIndicatorDesc":
							CommonUtils.addToQuerySearch(" upper(TAPPR.RECORD_INDICATOR_DESC) "+ val, strBufApprove, data.getJoinType());
							CommonUtils.addToQuerySearch(" upper(TPEND.RECORD_INDICATOR_DESC) "+ val, strBufPending, data.getJoinType());
							break;
	
						default:
					}
					count++;
				}
			}
			VisionUsersVb visionUsersVb = CustomContextHolder.getContext();
			//visionUsersVb = commonDao.getRestrictionInfo(visionUsersVb);
			if(("Y".equalsIgnoreCase(visionUsersVb.getUpdateRestriction()))){
				if(ValidationUtil.isValid(visionUsersVb.getCountry())){
					CommonUtils.addToQuery(" COUNTRY"+getDbFunction("PIPELINE", "")+"'-'"+getDbFunction("PIPELINE", "")+"LE_BOOK IN ("+visionUsersVb.getCountry()+") ", strBufApprove);
					CommonUtils.addToQuery(" COUNTRY"+getDbFunction("PIPELINE", "")+"'-'"+getDbFunction("PIPELINE", "")+"LE_BOOK IN ("+visionUsersVb.getCountry()+") ", strBufPending);
				}
			}
			String orderBy="  Order by DATE_LAST_MODIFIED_1 DESC ";
			
			return getQueryPopupResults(dObj,strBufPending, strBufApprove, strWhereNotExists, orderBy, params);
			
		}catch(Exception ex){
			/*ex.printStackTrace();
			logger.error(((strBufApprove==null)? "strBufApprove is Null":strBufApprove.toString()));
			logger.error("UNION");
			logger.error(((strBufPending==null)? "strBufPending is Null":strBufPending.toString()));

			if (params != null)
				for(int i=0 ; i< params.size(); i++)
					logger.error("objParams[" + i + "]" + params.get(i).toString());*/
			return null;

		}
	}
	@Override
	public List<RaTellerFreeVb> getQueryResults(RaTellerFreeVb dObj, int intStatus){
		setServiceDefaults();
		List<RaTellerFreeVb> collTemp = null;
		final int intKeyFieldsCount = 4;
		String strQueryAppr = "";
		String strQueryPend = "";
		strQueryAppr = new String("Select TAppr.COUNTRY,"
				+ " TAppr.LE_BOOK, TAppr.CUSTOMER_ID,TAppr.TELLER_BUCKET_AT,TAPPR.TELLER_BUCKET,"
				+ ""+commonDao.getDbFunction("DATEFUNC")+"(TAppr.EFFECTIVE_DATE, '"+commonDao.getDbFunction("DATEFORMAT")+"') EFFECTIVE_DATE,"
				+ ""+commonDao.getDbFunction("DATEFUNC")+"(TAppr.EXPIRY_DATE, '"+commonDao.getDbFunction("DATEFORMAT")+"') EXPIRY_DATE,"
				+ " TAppr.TELLER_FREE_STATUS_NT, TAppr.TELLER_FREE_STATUS,TAppr.RECORD_INDICATOR_NT,TAppr.RECORD_INDICATOR,"
				+ " TAppr.MAKER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "+commonDao.getDbFunction("NVL")+"(TAppr.MAKER,0) ) MAKER_NAME,"
				+ " TAppr.VERIFIER, (SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "+commonDao.getDbFunction("NVL")+"(TAppr.VERIFIER,0) ) VERIFIER_NAME,"
				+ " "+commonDao.getDbFunction("DATEFUNC")+"(TAppr.DATE_LAST_MODIFIED, '"+commonDao.getDbFunction("DATEFORMAT")+" "+commonDao.getDbFunction("TIME")+"') DATE_LAST_MODIFIED,"
				+ " "+commonDao.getDbFunction("DATEFUNC")+"(TAppr.DATE_CREATION, '"+commonDao.getDbFunction("DATEFORMAT")+" "+commonDao.getDbFunction("TIME")+"') DATE_CREATION, "
				+ customerDescAppr+tellerBucketDescAppr+statusDescAppr+recordIndicatorDescAppr
				+ " From RA_TELLER_FREE TAppr " 
				+ " Where TAppr.COUNTRY = ? AND TAppr.LE_Book = ? and TAppr.Customer_ID = ? and TAppr.EFFECTIVE_DATE = ? ");
		
		strQueryPend = new String("Select TPend.COUNTRY,"
				+ " TPend.LE_BOOK, TPend.CUSTOMER_ID,TPend.TELLER_BUCKET_AT,TPend.TELLER_BUCKET,"
				+ ""+commonDao.getDbFunction("DATEFUNC")+"(TPend.EFFECTIVE_DATE, '"+commonDao.getDbFunction("DATEFORMAT")+"') EFFECTIVE_DATE,"
				+ ""+commonDao.getDbFunction("DATEFUNC")+"(TPend.EXPIRY_DATE, '"+commonDao.getDbFunction("DATEFORMAT")+"') EXPIRY_DATE,"
				+ " TPend.TELLER_FREE_STATUS_NT, TPend.TELLER_FREE_STATUS,TPend.RECORD_INDICATOR_NT,TPend.RECORD_INDICATOR,"
				+ " TPend.MAKER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "+commonDao.getDbFunction("NVL")+"(TPend.MAKER,0) ) MAKER_NAME,"
				+ " TPend.VERIFIER, (SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "+commonDao.getDbFunction("NVL")+"(TPend.VERIFIER,0) ) VERIFIER_NAME,"
				+ " "+commonDao.getDbFunction("DATEFUNC")+"(TPend.DATE_LAST_MODIFIED, '"+commonDao.getDbFunction("DATEFORMAT")+" "+commonDao.getDbFunction("TIME")+"') DATE_LAST_MODIFIED,"
				+ " "+commonDao.getDbFunction("DATEFUNC")+"(TPend.DATE_CREATION, '"+commonDao.getDbFunction("DATEFORMAT")+" "+commonDao.getDbFunction("TIME")+"') DATE_CREATION, "
				+ customerDescPend+tellerBucketDescPend+statusDescPend+recordIndicatorDescPend
				+ " From RA_TELLER_FREE_PEND TPend " 
				+ " Where TPend.COUNTRY = ? AND TPend.LE_Book = ? and TPend.Customer_ID = ? and TPend.EFFECTIVE_DATE = ? ");
		
		Object objParams[] = new Object[intKeyFieldsCount];
		objParams[0] = new String(dObj.getCountry());
		objParams[1] = new String(dObj.getLeBook());
		objParams[2] = new String(dObj.getCustomerId());
		objParams[3] = new String(dObj.getEffectiveDateStart());

		try
		{if(!dObj.isVerificationRequired()){intStatus =0;}
			if(intStatus == 0)
			{
				//logger.info("Executing approved query");
				collTemp = getJdbcTemplate().query(strQueryAppr.toString(),objParams,getMapper());
			}else{
				//logger.info("Executing pending query");
				collTemp = getJdbcTemplate().query(strQueryPend.toString(),objParams,getMapper());
			}
			return collTemp;
		}catch(Exception ex){
			/*ex.printStackTrace();
			logger.error("Error: getQueryResults Exception :   ");
			if(intStatus == 0)
				logger.error(((strQueryAppr == null) ? "strQueryAppr is Null" : strQueryAppr.toString()));
			else
				logger.error(((strQueryPend == null) ? "strQueryPend is Null" : strQueryPend.toString()));

			if (objParams != null)
				for(int i=0 ; i< objParams.length; i++)
					logger.error("objParams[" + i + "]" + objParams[i].toString());*/
			return null;
		}
	}
	@Override
	protected List<RaTellerFreeVb> selectApprovedRecord(RaTellerFreeVb vObject){
		return getQueryResults(vObject, Constants.STATUS_ZERO);
	}
	@Override
	protected List<RaTellerFreeVb> doSelectPendingRecord(RaTellerFreeVb vObject){
		return getQueryResults(vObject, Constants.STATUS_PENDING);
	}
	@Override
	protected int getStatus(RaTellerFreeVb records){return records.getTellerFreeStatus();}
	@Override
	protected void setStatus(RaTellerFreeVb vObject,int status){vObject.setTellerFreeStatus(status);}
	
	@Override
	@Transactional(rollbackForClassName={"com.vision.exception.RuntimeCustomException"})
	public ExceptionCode doInsertApprRecord(List<RaTellerFreeVb> vObjects) throws RuntimeCustomException {
		ExceptionCode exceptionCode = null;
		strErrorDesc = "";
		strCurrentOperation = Constants.ADD;
		strApproveOperation = Constants.ADD;
		setServiceDefaults();
		try {
			for (RaTellerFreeVb vObject : vObjects) {
				exceptionCode = doValidateBucketLoop(vObject);
				if (exceptionCode.getErrorCode() == Constants.ERRONEOUS_OPERATION)
					throw buildRuntimeCustomException(exceptionCode);
				if (vObject.isNewRecord()) {
					exceptionCode = doInsertApprRecordForNonTrans(vObject);
					if (exceptionCode == null || exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION) {
						throw buildRuntimeCustomException(exceptionCode);
					}
				} else {
					exceptionCode = doUpdateApprRecordForNonTrans(vObject);
					if (exceptionCode == null || exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION) {
						throw buildRuntimeCustomException(exceptionCode);
					}
				}
			}
			return getResultObject(Constants.SUCCESSFUL_OPERATION);
		} catch (RuntimeCustomException rcException) {
			strErrorDesc = parseErrorMsg(rcException);
			exceptionCode = getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION);
			throw new RuntimeCustomException(exceptionCode);
		} catch (UncategorizedSQLException uSQLEcxception) {
			strErrorDesc = parseErrorMsg(uSQLEcxception);
			exceptionCode = getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION);
			throw buildRuntimeCustomException(exceptionCode);
		} catch (Exception ex) {
			//logger.error("Error in Add.", ex);
			strErrorDesc = parseErrorMsg(ex);
			exceptionCode = getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION);
			throw buildRuntimeCustomException(exceptionCode);
		}
	}
	@Transactional(rollbackForClassName={"com.vision.exception.RuntimeCustomException"})
	public ExceptionCode doInsertRecord(List<RaTellerFreeVb> vObjects) throws RuntimeCustomException {
		ExceptionCode exceptionCode = null;
		strErrorDesc = "";
		strCurrentOperation = Constants.ADD;
		strApproveOperation = Constants.ADD;
		setServiceDefaults();
		try {
			for (RaTellerFreeVb vObject : vObjects) {
				exceptionCode = doValidateBucketLoop(vObject);
				if (exceptionCode.getErrorCode() == Constants.ERRONEOUS_OPERATION)
					throw buildRuntimeCustomException(exceptionCode);
				if (vObject.isNewRecord()) {
					exceptionCode = doInsertRecordForNonTrans(vObject);
					if (exceptionCode == null || exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION) {
						throw buildRuntimeCustomException(exceptionCode);
					}
				} else {
					exceptionCode = doUpdateRecordForNonTrans(vObject);
					if (exceptionCode == null || exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION) {
						throw buildRuntimeCustomException(exceptionCode);
					}
				}
			}
			return getResultObject(Constants.SUCCESSFUL_OPERATION);
		} catch (RuntimeCustomException rcException) {
			strErrorDesc = parseErrorMsg(rcException);
			exceptionCode = getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION);
			throw new RuntimeCustomException(exceptionCode);
		} catch (UncategorizedSQLException uSQLEcxception) {
			strErrorDesc = parseErrorMsg(uSQLEcxception);
			exceptionCode = getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION);
			throw buildRuntimeCustomException(exceptionCode);
		} catch (Exception ex) {
			//logger.error("Error in modify.", ex);
			//strErrorDesc = ex.getMessage();
			strErrorDesc = parseErrorMsg(ex);
			exceptionCode = getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION);
			throw buildRuntimeCustomException(exceptionCode);
		}
	}
	
	protected int doInsertionAppr(RaTellerFreeVb vObject){
		String query = "";
		try { 
			query = "Insert Into RA_TELLER_FREE ( COUNTRY, LE_BOOK,CUSTOMER_ID,EFFECTIVE_DATE,EXPIRY_DATE,TELLER_BUCKET_AT,TELLER_BUCKET, TELLER_FREE_STATUS_NT, TELLER_FREE_STATUS,"+
				"RECORD_INDICATOR_NT, RECORD_INDICATOR, MAKER, VERIFIER, INTERNAL_STATUS, DATE_LAST_MODIFIED, DATE_CREATION)"+
				"Values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, "+commonDao.getDbFunction("SYSDATE")+","+commonDao.getDbFunction("SYSDATE")+")";
			
			Object[] args = {vObject.getCountry(),vObject.getLeBook(),vObject.getCustomerId(),
					vObject.getEffectiveDateStart(),vObject.getEffectiveDateEnd(),
					vObject.getTellerBucketAt(),vObject.getTellerBucket(),
					vObject.getTellerFreeStatusNt(),vObject.getTellerFreeStatus(),
					vObject.getRecordIndicatorNt(),vObject.getRecordIndicator(),vObject.getMaker(),
					vObject.getVerifier(), vObject.getInternalStatus()};  
			return getJdbcTemplate().update(query,args);
		} catch(Exception ex) {
			strErrorDesc = parseErrorMsg(ex);
			return Constants.WE_HAVE_ERROR_DESCRIPTION;
		}	
	}
	@Override
	protected int doInsertionPend(RaTellerFreeVb vObject){
		try { 
			String query = "Insert Into RA_TELLER_FREE_PEND ( COUNTRY, LE_BOOK,CUSTOMER_ID,EFFECTIVE_DATE,EXPIRY_DATE,TELLER_BUCKET_AT,TELLER_BUCKET, TELLER_FREE_STATUS_NT, TELLER_FREE_STATUS,"+
			"RECORD_INDICATOR_NT, RECORD_INDICATOR, MAKER, VERIFIER, INTERNAL_STATUS, DATE_LAST_MODIFIED, DATE_CREATION)"+
			"Values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? ,"+commonDao.getDbFunction("SYSDATE")+","+commonDao.getDbFunction("SYSDATE")+")";
		
		Object[] args = {vObject.getCountry(),vObject.getLeBook(),vObject.getCustomerId(),
				vObject.getEffectiveDateStart(),vObject.getEffectiveDateEnd(),
				vObject.getTellerBucketAt(),vObject.getTellerBucket(),
				vObject.getTellerFreeStatusNt(),vObject.getTellerFreeStatus(),
				vObject.getRecordIndicatorNt(),vObject.getRecordIndicator(),vObject.getMaker(),
				vObject.getVerifier(), vObject.getInternalStatus()};  
		return getJdbcTemplate().update(query,args);}
		catch(Exception ex) {
			strErrorDesc = parseErrorMsg(ex);
			return Constants.WE_HAVE_ERROR_DESCRIPTION;
		}
	}
	@Override
	protected int doInsertionPendWithDc(RaTellerFreeVb vObject){
		String dateCreation = "";
		try {
		 if("ORACLE".equalsIgnoreCase(databaseType))
			 dateCreation = "To_Date(?, 'DD-MM-YYYY HH24:MI:SS')";
		 else
			 dateCreation = "CONVERT(datetime, ?, 103)";
		 String query = "Insert Into RA_TELLER_FREE_PEND ( COUNTRY, LE_BOOK,CUSTOMER_ID,EFFECTIVE_DATE,EXPIRY_DATE,TELLER_BUCKET_AT,TELLER_BUCKET, TELLER_FREE_STATUS_NT, TELLER_FREE_STATUS,"+
			"RECORD_INDICATOR_NT, RECORD_INDICATOR, MAKER, VERIFIER, INTERNAL_STATUS, DATE_LAST_MODIFIED, DATE_CREATION)"+
			"Values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, "+commonDao.getDbFunction("SYSDATE")+", "+dateCreation+")";
		
		Object[] args = {vObject.getCountry(),vObject.getLeBook(),vObject.getCustomerId(),
				vObject.getEffectiveDateStart(),vObject.getEffectiveDateEnd(),
				vObject.getTellerBucketAt(),vObject.getTellerBucket(),
				vObject.getTellerFreeStatusNt(),vObject.getTellerFreeStatus(),
				vObject.getRecordIndicatorNt(),vObject.getRecordIndicator(),vObject.getMaker(),
				vObject.getVerifier(), vObject.getInternalStatus(),vObject.getDateCreation()}; 
		return getJdbcTemplate().update(query,args);}
		catch(Exception ex){
			strErrorDesc = parseErrorMsg(ex);
			return Constants.WE_HAVE_ERROR_DESCRIPTION;
		}
		
	}
	@Override
	protected int doUpdateAppr(RaTellerFreeVb vObject){
		String query = "Update RA_TELLER_FREE  Set EXPIRY_DATE = ?,TELLER_BUCKET = ?, TELLER_FREE_STATUS = ?,RECORD_INDICATOR = ?, MAKER = ?, VERIFIER = ?, "+
			" DATE_LAST_MODIFIED = "+commonDao.getDbFunction("SYSDATE")+" Where COUNTRY = ? AND LE_BOOK = ? AND CUSTOMER_ID = ? AND EFFECTIVE_DATE = ? ";
		
		Object[] args = {vObject.getEffectiveDateEnd(),vObject.getTellerBucket(),vObject.getTellerFreeStatus(), vObject.getRecordIndicator(),vObject.getMaker(),
			vObject.getVerifier(),vObject.getCountry(),vObject.getLeBook(),vObject.getCustomerId(),vObject.getEffectiveDateStart()};
		return getJdbcTemplate().update(query,args);
	}
	@Override
	protected int doUpdatePend(RaTellerFreeVb vObject){
		String query = "Update RA_TELLER_FREE_PEND Set EXPIRY_DATE = ?,TELLER_BUCKET = ?, TELLER_FREE_STATUS = ?,RECORD_INDICATOR = ?, MAKER = ?, VERIFIER = ?, "+
				" DATE_LAST_MODIFIED = "+commonDao.getDbFunction("SYSDATE")+" Where COUNTRY = ? AND LE_BOOK = ? AND CUSTOMER_ID = ? AND EFFECTIVE_DATE = ? ";
			
			Object[] args = {vObject.getEffectiveDateEnd(),vObject.getTellerBucket(),vObject.getTellerFreeStatus(), vObject.getRecordIndicator(),vObject.getMaker(),
				vObject.getVerifier(),vObject.getCountry(),vObject.getLeBook(),vObject.getCustomerId(),vObject.getEffectiveDateStart()};
			return getJdbcTemplate().update(query,args);
	}
	@Override
	protected int doDeleteAppr(RaTellerFreeVb vObject){
		String query = "Delete From RA_TELLER_FREE	Where COUNTRY = ? AND LE_BOOK = ? AND CUSTOMER_ID = ? AND EFFECTIVE_DATE = ? ";
		Object[] args = {vObject.getCountry(),vObject.getLeBook(),vObject.getCustomerId(),vObject.getEffectiveDateStart()};
		return getJdbcTemplate().update(query,args);
	}
	@Override
	protected int deletePendingRecord(RaTellerFreeVb vObject){
		String query = "Delete From RA_TELLER_FREE_PEND Where COUNTRY = ? AND LE_BOOK = ? AND CUSTOMER_ID = ? AND EFFECTIVE_DATE = ? ";
		Object[] args = {vObject.getCountry(),vObject.getLeBook(),vObject.getCustomerId(),vObject.getEffectiveDateStart()};
		return getJdbcTemplate().update(query,args);
	}
	@Override
	protected String frameErrorMessage(RaTellerFreeVb vObject, String strOperation){
		String strErrMsg = new String("");
		try{
			strErrMsg =  strErrMsg + "Country: " + vObject.getCountry()+"  LE Book:"+vObject.getLeBook()+" Customer Id:"+vObject.getCustomerId()+" ";
			if ("Approve".equalsIgnoreCase(strOperation))
				strErrMsg = strErrMsg + " failed during approve Operation. Bulk Approval aborted !!";
			else
				strErrMsg = strErrMsg + " failed during reject Operation. Bulk Rejection aborted !!";
		}catch(Exception ex){
			strErrorDesc = ex.getMessage();
			strErrMsg = strErrMsg + strErrorDesc;
			logger.error(strErrMsg, ex);
		}
		return strErrMsg;
	}
	@Override
	protected String getAuditString(RaTellerFreeVb vObject){
		StringBuffer strAudit = new StringBuffer("");
		strAudit.append(vObject.getCountry().trim());
		strAudit.append("!|#");
		strAudit.append(vObject.getLeBook().trim());
		strAudit.append("!|#");
		strAudit.append(vObject.getEffectiveDateStart().trim());
		strAudit.append("!|#");
		strAudit.append(vObject.getEffectiveDateEnd().trim());
		strAudit.append("!|#");
		strAudit.append(vObject.getCustomerId().trim());
		strAudit.append("!|#");
		strAudit.append(vObject.getTellerBucketAt());
		strAudit.append("!|#");
		strAudit.append(vObject.getTellerBucket().trim());
		strAudit.append("!|#");
		strAudit.append(vObject.getTellerFreeStatusNt());
		strAudit.append("!|#");
		if(vObject.getTellerFreeStatus()== -1)
			vObject.setTellerFreeStatus(0);
		strAudit.append(vObject.getTellerFreeStatus());
		strAudit.append("!|#");
		strAudit.append(vObject.getRecordIndicatorNt());
		strAudit.append("!|#");
		if(vObject.getRecordIndicator()== -1)
			vObject.setRecordIndicator(0);
		strAudit.append(vObject.getRecordIndicator());
		strAudit.append("!|#");
		strAudit.append(vObject.getMaker());
		strAudit.append("!|#");
		strAudit.append(vObject.getVerifier());
		strAudit.append("!|#");
		strAudit.append(vObject.getInternalStatus());
		strAudit.append("!|#");
		if(vObject.getDateLastModified() != null  && !vObject.getDateLastModified().equalsIgnoreCase(""))
			strAudit.append(vObject.getDateLastModified().trim());
		else
			strAudit.append("NULL");
		strAudit.append("!|#");

		if(vObject.getDateCreation() != null && !vObject.getDateCreation().equalsIgnoreCase(""))
			strAudit.append(vObject.getDateCreation().trim());
		else
			strAudit.append("NULL");
		strAudit.append("!|#");
		return strAudit.toString();
	}
	@Override
	protected void setServiceDefaults(){
		serviceName = "RA Teller Free";
		serviceDesc = "RA Teller Free";
		tableName = "RA_TELLER_FREE";
		childTableName = "RA_TELLER_FREE";
		intCurrentUserId = CustomContextHolder.getContext().getVisionId();
	}
	
	@Override
	public List<RaTellerFreeVb> getQueryResultsForReview(RaTellerFreeVb dObj, int intStatus){
		setServiceDefaults();
		List<RaTellerFreeVb> collTemp = null;
		final int intKeyFieldsCount = 4;
		String strQueryAppr = "";
		String strQueryPend = "";
		strQueryAppr = new String("Select TAppr.COUNTRY,"
				+ " TAppr.LE_BOOK, TAppr.CUSTOMER_ID,TAppr.TELLER_BUCKET_AT,TAPPR.TELLER_BUCKET,"
				+ ""+commonDao.getDbFunction("DATEFUNC")+"(TAppr.EFFECTIVE_DATE, '"+commonDao.getDbFunction("DATEFORMAT")+"') EFFECTIVE_DATE,"
				+ ""+commonDao.getDbFunction("DATEFUNC")+"(TAppr.EXPIRY_DATE, '"+commonDao.getDbFunction("DATEFORMAT")+"') EXPIRY_DATE,"
				+ " TAppr.TELLER_FREE_STATUS_NT, TAppr.TELLER_FREE_STATUS,TAppr.RECORD_INDICATOR_NT,TAppr.RECORD_INDICATOR,"
				+ " TAppr.MAKER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "+commonDao.getDbFunction("NVL")+"(TAppr.MAKER,0) ) MAKER_NAME,"
				+ " TAppr.VERIFIER, (SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "+commonDao.getDbFunction("NVL")+"(TAppr.VERIFIER,0) ) VERIFIER_NAME,"
				+ " "+commonDao.getDbFunction("DATEFUNC")+"(TAppr.DATE_LAST_MODIFIED, '"+commonDao.getDbFunction("DATEFORMAT")+" "+commonDao.getDbFunction("TIME")+"') DATE_LAST_MODIFIED,"
				+ " "+commonDao.getDbFunction("DATEFUNC")+"(TAppr.DATE_CREATION, '"+commonDao.getDbFunction("DATEFORMAT")+" "+commonDao.getDbFunction("TIME")+"') DATE_CREATION, "
				+ customerDescAppr+tellerBucketDescAppr+statusDescAppr+recordIndicatorDescAppr
				+ " From RA_TELLER_FREE TAppr " 
				+ " Where TAppr.COUNTRY = ? AND TAppr.LE_Book = ? and TAppr.Customer_ID = ? and TAppr.EFFECTIVE_DATE = ? ");
		
		strQueryPend = new String("Select TPend.COUNTRY,"
				+ " TPend.LE_BOOK, TPend.CUSTOMER_ID,TPend.TELLER_BUCKET_AT,TPend.TELLER_BUCKET,"
				+ ""+commonDao.getDbFunction("DATEFUNC")+"(TPend.EFFECTIVE_DATE, '"+commonDao.getDbFunction("DATEFORMAT")+"') EFFECTIVE_DATE,"
				+ ""+commonDao.getDbFunction("DATEFUNC")+"(TPend.EXPIRY_DATE, '"+commonDao.getDbFunction("DATEFORMAT")+"') EXPIRY_DATE,"
				+ " TPend.TELLER_FREE_STATUS_NT, TPend.TELLER_FREE_STATUS,TPend.RECORD_INDICATOR_NT,TPend.RECORD_INDICATOR,"
				+ " TPend.MAKER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "+commonDao.getDbFunction("NVL")+"(TPend.MAKER,0) ) MAKER_NAME,"
				+ " TPend.VERIFIER, (SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "+commonDao.getDbFunction("NVL")+"(TPend.VERIFIER,0) ) VERIFIER_NAME,"
				+ " "+commonDao.getDbFunction("DATEFUNC")+"(TPend.DATE_LAST_MODIFIED, '"+commonDao.getDbFunction("DATEFORMAT")+" "+commonDao.getDbFunction("TIME")+"') DATE_LAST_MODIFIED,"
				+ " "+commonDao.getDbFunction("DATEFUNC")+"(TPend.DATE_CREATION, '"+commonDao.getDbFunction("DATEFORMAT")+" "+commonDao.getDbFunction("TIME")+"') DATE_CREATION, "
				+ customerDescPend+tellerBucketDescPend+statusDescPend+recordIndicatorDescPend
				+ " From RA_TELLER_FREE_PEND TPend " 
				+ " Where TPend.COUNTRY = ? AND TPend.LE_Book = ? and TPend.Customer_ID = ? and TPend.EFFECTIVE_DATE = ? ");
		
		Object objParams[] = new Object[intKeyFieldsCount];
		objParams[0] = new String(dObj.getCountry());
		objParams[1] = new String(dObj.getLeBook());
		objParams[2] = new String(dObj.getCustomerId());
		objParams[3] = new String(dObj.getEffectiveDateStart());

		try
		{if(!dObj.isVerificationRequired()){intStatus =0;}
			if(intStatus == 0)
			{
				//logger.info("Executing approved query");
				collTemp = getJdbcTemplate().query(strQueryAppr.toString(),objParams,getMapper());
			}else{
				//logger.info("Executing pending query");
				collTemp = getJdbcTemplate().query(strQueryPend.toString(),objParams,getMapper());
			}
			return collTemp;
		}catch(Exception ex){
			//ex.printStackTrace();
			//logger.error("Error: getQueryResults Exception :   ");
			/*if(intStatus == 0)
				logger.error(((strQueryAppr == null) ? "strQueryAppr is Null" : strQueryAppr.toString()));
			else
				logger.error(((strQueryPend == null) ? "strQueryPend is Null" : strQueryPend.toString()));

			if (objParams != null)
				for(int i=0 ; i< objParams.length; i++)
					logger.error("objParams[" + i + "]" + objParams[i].toString());*/
			return null;
		}
	}
	public List<RaTellerFreeVb> validateTellerFree(RaTellerFreeVb dObj, int intStatus,String dateType,String checkType){
		if (!dObj.isVerificationRequired()) {
			intStatus = 0;
		}
		setServiceDefaults();
		List<RaTellerFreeVb> collTemp = null;
		final int intKeyFieldsCount = 3;
		String strQueryAppr = "";
		String extraCondition1 = "";
		String extraCondition2 = "";
		String tableName = "RA_TELLER_FREE";
		if (intStatus != 0)
			tableName = "RA_TELLER_FREE_PEND";
		if(!dObj.isNewRecord()) {
			extraCondition1 = " and TAppr.EFFECTIVE_DATE != '"+dObj.getEffectiveDateStart()+"' ";
		}
		if(checkType.equalsIgnoreCase("OVERLAP")) {
			extraCondition2 = " AND EFFECTIVE_DATE <= '"+dObj.getEffectiveDateEnd()+"' AND EXPIRY_DATE >= '"+dObj.getEffectiveDateStart()+"' ";
		}else {
			String dateStr = dObj.getEffectiveDateStart();
			if(!dateType.equalsIgnoreCase("EFF_DATE"))
				dateStr = dObj.getEffectiveDateEnd();
			
			extraCondition2 = " and '"+dateStr+"' Between TAppr.EFFECTIVE_DATE and TAppr.Expiry_Date ";
		}
		strQueryAppr = new String("Select TAppr.COUNTRY,"
				+ " TAppr.LE_BOOK, TAppr.CUSTOMER_ID,TAppr.TELLER_BUCKET_AT,TAPPR.TELLER_BUCKET,"
				+ ""+commonDao.getDbFunction("DATEFUNC")+"(TAppr.EFFECTIVE_DATE, '"+commonDao.getDbFunction("DATEFORMAT")+"') EFFECTIVE_DATE,"
				+ ""+commonDao.getDbFunction("DATEFUNC")+"(TAppr.EXPIRY_DATE, '"+commonDao.getDbFunction("DATEFORMAT")+"') EXPIRY_DATE,"
				+ " TAppr.TELLER_FREE_STATUS_NT, TAppr.TELLER_FREE_STATUS,TAppr.RECORD_INDICATOR_NT,TAppr.RECORD_INDICATOR,"
				+ " TAppr.MAKER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "+commonDao.getDbFunction("NVL")+"(TAppr.MAKER,0) ) MAKER_NAME,"
				+ " TAppr.VERIFIER, (SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "+commonDao.getDbFunction("NVL")+"(TAppr.VERIFIER,0) ) VERIFIER_NAME,"
				+ " "+commonDao.getDbFunction("DATEFUNC")+"(TAppr.DATE_LAST_MODIFIED, '"+commonDao.getDbFunction("DATEFORMAT")+" "+commonDao.getDbFunction("TIME")+"') DATE_LAST_MODIFIED,"
				+ " "+commonDao.getDbFunction("DATEFUNC")+"(TAppr.DATE_CREATION, '"+commonDao.getDbFunction("DATEFORMAT")+" "+commonDao.getDbFunction("TIME")+"') DATE_CREATION, "
				+ customerDescAppr+tellerBucketDescAppr+statusDescAppr+recordIndicatorDescAppr
				+ " From "+tableName+" TAppr " 
				+ " Where TAppr.COUNTRY = ? AND TAppr.LE_Book = ? and TAppr.Customer_ID = ? "+extraCondition1+ extraCondition2+" ");
		
		Object objParams[] = new Object[intKeyFieldsCount];
		objParams[0] = new String(dObj.getCountry());
		objParams[1] = new String(dObj.getLeBook());
		objParams[2] = new String(dObj.getCustomerId());
		
		try {
			//logger.info("Executing approved query");
			collTemp = getJdbcTemplate().query(strQueryAppr.toString(), objParams, getMapper());
			return collTemp;
		} catch (Exception ex) {
			return null;
		}
	}

	private ExceptionCode doValidateBucketLoop(RaTellerFreeVb vObject) {
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			List<RaTellerFreeVb> validatelst = new ArrayList<RaTellerFreeVb>();
			if(vObject.isNewRecord()) {
				validatelst = validateTellerFree(vObject, Constants.STATUS_ZERO, "EFF_DATE","NA");
				if (validatelst != null && validatelst.size() > 0) {
					exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
					exceptionCode.setErrorMsg("Teller Free Counter[Effective Date] already exist for Cust Id["
							+ vObject.getCustomerId() + "]Eff Date[" + vObject.getEffectiveDateStart() + "]Expiry Date["
							+ vObject.getEffectiveDateEnd() + "]");
					return exceptionCode;
				}
				validatelst = validateTellerFree(vObject, Constants.STATUS_PENDING, "EFF_DATE","NA");
				if (validatelst != null && validatelst.size() > 0) {
					exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
					exceptionCode.setErrorMsg("Teller Free Counter[Effective Date] already exist for Cust Id["
							+ vObject.getCustomerId() + "]Eff Date[" + vObject.getEffectiveDateStart() + "]Expiry Date["
							+ vObject.getEffectiveDateEnd() + "] and Pending for Approval");
					return exceptionCode;
				}
			}
			validatelst = validateTellerFree(vObject, Constants.STATUS_ZERO, "EXP_DATE","NA");
			if (validatelst != null && validatelst.size() > 0) {
				exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
				exceptionCode.setErrorMsg("Teller Free Counter[Expiry Date] already exist for Cust Id["
						+ vObject.getCustomerId() + "]Eff Date[" + vObject.getEffectiveDateStart() + "]Expiry Date["
						+ vObject.getEffectiveDateEnd() + "]");
				return exceptionCode;
			}
			
			validatelst = validateTellerFree(vObject, Constants.STATUS_PENDING, "EXP_DATE","NA");
			if (validatelst != null && validatelst.size() > 0) {
				exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
				exceptionCode.setErrorMsg("Teller Free Counter[Expiry Date] already exist for Cust Id["
						+ vObject.getCustomerId() + "]Eff Date[" + vObject.getEffectiveDateStart() + "]Expiry Date["
						+ vObject.getEffectiveDateEnd() + "] and Pending for Approval");
				return exceptionCode;
			}
			validatelst = validateTellerFree(vObject, Constants.STATUS_ZERO, "EXP_DATE","OVERLAP");
			if (validatelst != null && validatelst.size() > 0) {
				exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
				exceptionCode.setErrorMsg("Teller Free Counter Overlapping[Eff/Expiry Date] already exist for Cust Id["
						+ vObject.getCustomerId() + "]Eff Date[" + vObject.getEffectiveDateStart() + "]Expiry Date["
						+ vObject.getEffectiveDateEnd() + "]");
				return exceptionCode;
			}
			
			validatelst = validateTellerFree(vObject, Constants.STATUS_PENDING, "EXP_DATE","OVERLAP");
			if (validatelst != null && validatelst.size() > 0) {
				exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
				exceptionCode.setErrorMsg("Teller Free Counter Overlapping[Eff/Expiry Date] already exist for Cust Id["
						+ vObject.getCustomerId() + "]Eff Date[" + vObject.getEffectiveDateStart() + "]Expiry Date["
						+ vObject.getEffectiveDateEnd() + "] and Pending for Approval");
				return exceptionCode;
			}
			exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
		} catch (Exception e) {
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			String errorMsg = parseErrorMsg(e);
			exceptionCode.setErrorMsg(errorMsg);
		}
		return exceptionCode;
	}
	
}