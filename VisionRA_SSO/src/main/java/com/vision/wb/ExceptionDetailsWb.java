package com.vision.wb;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.vision.authentication.CustomContextHolder;
import com.vision.dao.AbstractDao;
import com.vision.dao.CommonDao;
import com.vision.dao.ExceptionConfigDetailsDao;
import com.vision.dao.ExceptionConfigHeaderDao;
import com.vision.exception.ExceptionCode;
import com.vision.exception.RuntimeCustomException;
import com.vision.util.CommonUtils;
import com.vision.util.Constants;
import com.vision.util.DeepCopy;
import com.vision.util.ValidationUtil;
import com.vision.vb.CommonVb;
import com.vision.vb.ExceptionConfigDetailsVb;
import com.vision.vb.ExceptionConfigHeaderVb;
import com.vision.vb.ExceptionManualFiltersVb;

@Component
public class ExceptionDetailsWb extends AbstractDynaWorkerBean<ExceptionConfigDetailsVb> {
	@Autowired
	private ExceptionConfigDetailsDao exceptionDetailsDao;
	@Autowired
	private CommonDao commonDao;
	@Autowired
	private ExceptionConfigHeaderDao exceptionHeaderDao;

	@Value("${app.databaseType}")
	private String databaseType;
	
	public static Logger logger = LoggerFactory.getLogger(ExceptionDetailsWb.class);

	@Override
	protected AbstractDao<ExceptionConfigDetailsVb> getScreenDao() {
		return exceptionDetailsDao;
	}
	@Override
	protected void setAtNtValues(List<ExceptionConfigDetailsVb> vObjects){
		for(ExceptionConfigDetailsVb vObject: vObjects){
			setAtNtValues(vObject);
		}
	}
	protected void setAtNtValues(ExceptionConfigDetailsVb vObject) {}
	
	@Override
	protected void setVerifReqDeleteType(ExceptionConfigDetailsVb vObject) {
		ArrayList<CommonVb> lCommVbList = (ArrayList<CommonVb>) commonDao.findVerificationRequiredAndStaticDelete("RA_EXCEPTION_HEADER");
		vObject.setStaticDelete(lCommVbList.get(0).isStaticDelete());
		vObject.setVerificationRequired(false);
	}
	public ExceptionCode getLeakgeTransactionDetail(ExceptionConfigDetailsVb vObject) {
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			exceptionCode = doValidate(vObject);
			if(exceptionCode.getErrorCode() == Constants.ERRONEOUS_OPERATION) {
				return exceptionCode;
			}
			exceptionCode = exceptionHeaderDao.checkRecordAuthorized(vObject.getCountry(), vObject.getLeBook(), vObject.getExceptionReference(),vObject.getMaker());
			if(exceptionCode.getErrorCode() == Constants.ERRONEOUS_OPERATION) {
				return exceptionCode;
			}
			String filterStr = " Where 1=1 AND ";
			StringJoiner fromTableJoiner = new StringJoiner(",");
			StringJoiner specialFilter = new StringJoiner(" ");
			List<ExceptionManualFiltersVb> sourceTablelst = exceptionDetailsDao.getSourceTableMappings();
			if(sourceTablelst == null || sourceTablelst.size() == 0) {
				exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
				exceptionCode.setErrorMsg("Source Table mapping is not maintained [RA_EXCEPTION_SOURCE_TABLE]");
				return exceptionCode;
			}
			for(ExceptionManualFiltersVb sourceTableVb : sourceTablelst) {
				if(ValidationUtil.isValid(sourceTableVb.getSpecialFilter()))
					specialFilter.add(sourceTableVb.getSpecialFilter());
				if(ValidationUtil.isValid(sourceTableVb.getFilterColumn())  
						&& ValidationUtil.isValid(vObject.getBasicFilterStr())
						&& !vObject.getBasicFilterStr().contains(sourceTableVb.getFilterColumn()))
					continue;
				if(ValidationUtil.isValid(sourceTableVb.getGenericCondService()))
					filterStr = filterStr + " "+sourceTableVb.getGenericCondService();
				fromTableJoiner.add(sourceTableVb.getSourceTable()+" "+sourceTableVb.getAliasName());
			}
			StringJoiner filterTable = new StringJoiner(",");
			StringJoiner filterCondition = new StringJoiner(" And ");
			if(vObject.getExceptionUpdateFilterlst() != null) {
				for(ExceptionManualFiltersVb filterVb : vObject.getExceptionUpdateFilterlst()) {
					if(vObject.getExceptionUpdateFilterlst() != null && vObject.getExceptionUpdateFilterlst().contains(filterVb.getFilterTable())) {
					}else {
						filterTable.add(filterVb.getFilterTable());
					}
					String conditionValue = "";
					if(!filterVb.getConditionOperation().equals("BETWEEN")) {
						conditionValue = filterVb.getFilterColumn()+" "+filterVb.getConditionOperation()+" "+"("+filterVb.getConditionValue1()+")";
					}else {
						conditionValue = filterVb.getFilterColumn()+" "+filterVb.getConditionOperation()+" "+filterVb.getConditionValue1()+" And "+filterVb.getConditionValue2();
					}
					filterCondition.add(conditionValue);
				}
			}
			List<ExceptionConfigDetailsVb> exceptionDetlst =  exceptionDetailsDao.getLeakageTransactionsDetails(vObject, filterStr,fromTableJoiner.toString(),specialFilter.toString());
			if(exceptionDetlst != null && exceptionDetlst.size() > 0) {
				exceptionCode = CommonUtils.getResultObject(getScreenDao().getServiceDesc(), 1, "Query", "");
				exceptionCode.setResponse(exceptionDetlst);
			}else{
				exceptionCode.setErrorCode(Constants.NO_RECORDS_FOUND);
				exceptionCode.setErrorMsg("No Records Found");
				//exceptionCode = CommonUtils.getResultObject(getScreenDao().getServiceDesc(), Constants.NO_RECORDS_FOUND, "Query", "");
			}
			exceptionCode.setOtherInfo(vObject);
			return exceptionCode;
			
		}catch(Exception e) {
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg(e.getMessage());
		}
		return exceptionCode;
	}
	public ExceptionCode deleteRecord(ExceptionCode pRequestCode, List<ExceptionConfigDetailsVb> vObjects){
		ExceptionCode exceptionCode  = null;
		DeepCopy<ExceptionConfigDetailsVb> deepCopy = new DeepCopy<ExceptionConfigDetailsVb>();
		List<ExceptionConfigDetailsVb> clonedObject = null;
		ExceptionConfigDetailsVb vObject = null;
		try{
			setAtNtValues(vObjects);
			vObject = (ExceptionConfigDetailsVb) pRequestCode.getOtherInfo();
			setVerifReqDeleteType(vObject);
			clonedObject = deepCopy.copyCollection(vObjects);
			doFormateData(vObjects);
			exceptionCode = doValidate(vObjects.get(0));
			if(exceptionCode.getErrorCode() == Constants.ERRONEOUS_OPERATION){
				return exceptionCode;
			}
			if(vObjects != null && vObjects.size() > 0) {
				exceptionCode = getScreenDao().doDeleteApprRecord(vObjects, vObject);
			}else {
				int retVal = exceptionDetailsDao.doDeletePendingRecord(vObject);
				exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
			}
			exceptionCode.setOtherInfo(vObject);
			exceptionCode.setResponse(vObjects);
			return exceptionCode;
		}catch(RuntimeCustomException rex){
			exceptionCode = rex.getCode();
			exceptionCode.setOtherInfo(vObject);
			exceptionCode.setResponse(clonedObject);
			return exceptionCode;
		}
	}
	public String dbFunctionFormats(String columnName,String formatReq,String dec) {
		String returnStr = "";
		if("MSSQL".equalsIgnoreCase(databaseType)) {
			switch(formatReq) {

				case "NUM_FORMAT":
					returnStr = "RTRIM(LTRIM(FORMAT("+columnName+",'N"+dec+"')))";
					break;
					
				case "DATETIME_FORMAT":
					returnStr = "Format("+columnName+", 'dd-MMM-yyyy HH:mm:ss')";
					break;
				
				case "DATE_FORMAT":
					returnStr = "Format("+columnName+", 'dd-MMM-yyyy')";
					break;	
				
			}
		}else if("ORACLE".equalsIgnoreCase(databaseType)) {
			switch(formatReq) {
				case "NUM_FORMAT":
					returnStr = "TRIM(TO_CHAR("+columnName+",'999,999,999,999,990.99990'))";
					break;
					
				case "DATETIME_FORMAT":
					returnStr = "TO_CHAR("+columnName+",'DD-Mon-RRRR HH24:MI:SS')";
					break;
					
				case "DATE_FORMAT":
					returnStr = "TO_CHAR("+columnName+",'DD-Mon-RRRR')";
					break;	
			}
		}
		return returnStr;
	}
	public ExceptionCode getAuditDetails(ExceptionConfigDetailsVb vObject) {
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			exceptionCode = doValidate(vObject);
			if(exceptionCode.getErrorCode() == Constants.ERRONEOUS_OPERATION) {
				return exceptionCode;
			}
			List auditList = exceptionDetailsDao.getAuditDetails(vObject);
			if(auditList != null && auditList.size() > 0) {
				exceptionCode.setResponse(auditList);
				exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
				exceptionCode.setErrorMsg("success");
			}
		}catch(Exception e) {
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg(e.getMessage());
		}
		return exceptionCode;
	}
	public ExceptionCode insertRecord(ExceptionCode pRequestCode, List<ExceptionConfigDetailsVb> vObjects){
		ExceptionCode exceptionCode  = new ExceptionCode();
		DeepCopy<ExceptionConfigDetailsVb> deepCopy = new DeepCopy<ExceptionConfigDetailsVb>();
		List<ExceptionConfigDetailsVb> clonedObject = null;
		ExceptionConfigDetailsVb vObject = null;
		try
		{
			setAtNtValues(vObjects);
			vObject = (ExceptionConfigDetailsVb) pRequestCode.getOtherInfo();
			exceptionCode = exceptionHeaderDao.checkRecordAuthorized(vObject.getCountry(), vObject.getLeBook(), vObject.getExceptionReference(),CustomContextHolder.getContext().getVisionId());
			if(exceptionCode.getErrorCode() == Constants.ERRONEOUS_OPERATION) {
				return exceptionCode;
			}
			setVerifReqDeleteType(vObject);
			clonedObject = deepCopy.copyCollection(vObjects);
			doFormateData(vObjects);
			exceptionCode = doValidate(vObject);
			if(exceptionCode.getErrorCode() == Constants.ERRONEOUS_OPERATION){
				return exceptionCode;
			}
			
			if(exceptionHeaderDao.getApprCount(vObject,6) > 0) {
				ExceptionConfigHeaderVb excepHeaderVb = new ExceptionConfigHeaderVb();
				excepHeaderVb.setCountry(vObject.getCountry());
				excepHeaderVb.setLeBook(vObject.getLeBook());
				excepHeaderVb.setExceptionReference(vObject.getExceptionReference());
				excepHeaderVb.setRecordIndicator(1);
				exceptionHeaderDao.doUpdateAllAppr(excepHeaderVb);
			}
			exceptionCode = exceptionDetailsDao.doInsertApprRecord(vObjects);
			exceptionCode.setOtherInfo(vObject);
			exceptionCode.setResponse(vObjects);
			return exceptionCode;
		}catch(RuntimeCustomException rex){
			exceptionCode = rex.getCode();
			exceptionCode.setResponse(clonedObject);
			exceptionCode.setOtherInfo(vObject);
			return exceptionCode;
		}
	}
	public ExceptionCode doValidate(ExceptionConfigDetailsVb vObject){
		ExceptionCode exceptionCode = new ExceptionCode();
		String operation = vObject.getActionType();
		String srtRestrion = getCommonDao().getRestrictionsByUsers("ManualException", operation);
		if(!"Y".equalsIgnoreCase(srtRestrion)) {
			exceptionCode = new ExceptionCode();
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg(operation +" "+Constants.userRestrictionMsg);
			return exceptionCode;
		}
		exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
		vObject.setVerificationRequired(false);
		return exceptionCode;
	}
}