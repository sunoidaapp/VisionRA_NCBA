package com.vision.wb;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.vision.dao.CommonDao;
import com.vision.exception.ExceptionCode;
import com.vision.exception.RuntimeCustomException;
import com.vision.util.CommonUtils;
import com.vision.util.Constants;
import com.vision.util.DeepCopy;
import com.vision.vb.CommonVb;
import com.vision.vb.ReviewResultVb;

@Component
public abstract class AbstractDynaWorkerBean<E extends CommonVb> extends AbstractWorkerBean<E> {

	protected void setAtNtValues(List<E> vObjects){
		for(E vObject: vObjects){
			setAtNtValues(vObject);
		}
	}
	protected void doFormateData(List<E> vObject){}
	protected ExceptionCode doValidate(List<E> vObject) {
		ExceptionCode exceptionCode = null;
		return exceptionCode;
	}
	@Autowired
	private CommonDao commonDao;
	protected void doSetDesctiptionsAfterQuery(List<E> vObject) {}
	/**
	 * Inserts a new record to the approved or pending tables depending on Verification Required flag of the 
	 * object. 
	 * @param vObject
	 * @return 
	 */
	public ExceptionCode insertRecord(ExceptionCode pRequestCode, List<E> vObjects){
		ExceptionCode exceptionCode  = null;
		DeepCopy<E> deepCopy = new DeepCopy<E>();
		List<E> clonedObject = null;
		E vObject = null;
		try {
			setAtNtValues(vObjects);
			vObject = (E) pRequestCode.getOtherInfo();
			setVerifReqDeleteType(vObject);
			clonedObject = deepCopy.copyCollection(vObjects);
			doFormateData(vObjects);
			exceptionCode = doValidate(vObjects);
			if(exceptionCode.getErrorCode() == Constants.ERRONEOUS_OPERATION){
				return exceptionCode;
			}			
			if(!vObject.isVerificationRequired()){
				exceptionCode = getScreenDao().doInsertApprRecord(vObjects);
			}else{
				exceptionCode = getScreenDao().doInsertRecord(vObjects);
			}
			exceptionCode.setOtherInfo(vObject);
			exceptionCode.setResponse(vObjects);
			return exceptionCode;
		}catch(RuntimeCustomException rex){
			//logger.error("Insert Exception " + rex.getCode().getErrorMsg());
			exceptionCode = rex.getCode();
			String errMsg = getScreenDao().parseErrorMsg(rex);
			exceptionCode.setErrorMsg(errMsg);
			exceptionCode.setResponse(clonedObject);
			exceptionCode.setOtherInfo(vObject);
			return exceptionCode;
		}
	}
	
	/**
	 * updates the existing record in the approved or pending tables depending on Verification Required flag of the 
	 * object. 
	 * @param vObject
	 * @return 
	 */
	public ExceptionCode modifyRecord(ExceptionCode pRequestCode, List<E> vObjects){
		ExceptionCode exceptionCode  = null;
		DeepCopy<E> deepCopy = new DeepCopy<E>();
		List<E> clonedObject = null;
		E vObject = null;
		try{
			setAtNtValues(vObjects);
			vObject = (E) pRequestCode.getOtherInfo();
			setVerifReqDeleteType(vObject);
			clonedObject = deepCopy.copyCollection(vObjects);
			doFormateData(vObjects);
			exceptionCode = doValidate(vObjects);
			if(exceptionCode.getErrorCode() == Constants.ERRONEOUS_OPERATION){
				return exceptionCode;
			}
			if(!vObject.isVerificationRequired()){
				exceptionCode = getScreenDao().doUpdateApprRecord(vObjects);
			}else{
				exceptionCode = getScreenDao().doUpdateRecord(vObjects);
			}
			exceptionCode.setOtherInfo(vObject);
			exceptionCode.setResponse(vObjects);
			return exceptionCode;
		}catch(RuntimeCustomException rex){
			//logger.error("Modify Exception " + rex.getCode().getErrorMsg());
			//logger.error( ((vObject==null)? "vObject is Null":vObject.toString()));
			exceptionCode = rex.getCode();
			String errMsg = getScreenDao().parseErrorMsg(rex);
			exceptionCode.setErrorMsg(errMsg);
			exceptionCode.setResponse(clonedObject);
			exceptionCode.setOtherInfo(vObject);
			return exceptionCode;
		}
	}
	
	/**
	 * Deletes the existing record in the approved or pending tables depending on Verification Required flag of the 
	 * object. 
	 * @param vObject
	 * @return 
	 */
	public ExceptionCode deleteRecord(ExceptionCode pRequestCode, List<E> vObjects){
		ExceptionCode exceptionCode  = null;
		DeepCopy<E> deepCopy = new DeepCopy<E>();
		List<E> clonedObject = null;
		E vObject = null;
		try{
			setAtNtValues(vObjects);
			vObject = (E) pRequestCode.getOtherInfo();
			setVerifReqDeleteType(vObject);
			clonedObject = deepCopy.copyCollection(vObjects);
			doFormateData(vObjects);
			exceptionCode = doValidate(vObjects);
			if(exceptionCode.getErrorCode() == Constants.ERRONEOUS_OPERATION){
				return exceptionCode;
			}
			if(!vObject.isVerificationRequired()){
				exceptionCode = getScreenDao().doDeleteApprRecord(vObjects, vObject);
			}else{
				exceptionCode = getScreenDao().doDeleteRecord(vObjects, vObject);
			}
			exceptionCode.setOtherInfo(vObject);
			exceptionCode.setResponse(vObjects);
			return exceptionCode;
		}catch(RuntimeCustomException rex){
			//logger.error("Delete Exception " + rex.getCode().getErrorMsg());
			//logger.error( ((vObject==null)? "vObject is Null":vObject.toString()));
			exceptionCode = rex.getCode();
			String errMsg = getScreenDao().parseErrorMsg(rex);
			exceptionCode.setErrorMsg(errMsg);
			exceptionCode.setOtherInfo(vObject);
			exceptionCode.setResponse(clonedObject);
			return exceptionCode;
		}
	}
	@Override
	public List<ReviewResultVb> reviewRecord(E vObject){
		try{
			List<E> approvedCollection = getScreenDao().getQueryResultsForReview(vObject,0);
			List<E> pendingCollection = getScreenDao().getQueryResultsForReview(vObject,1);
			return transformToReviewResults(approvedCollection,pendingCollection);
		}catch(Exception ex){
			return null;
		}
	}
	public ExceptionCode reviewRecordNew(E vObject){
		ExceptionCode exceptionCode = null;
		List<ReviewResultVb> list = null;
		try{
			List<E> approvedCollection = getScreenDao().getQueryResults(vObject,Constants.STATUS_ZERO);
			List<E> pendingCollection = getScreenDao().getQueryResults(vObject,Constants.STATUS_PENDING);
			list =  transformToReviewResults(approvedCollection,pendingCollection);
			exceptionCode = CommonUtils.getResultObject(getScreenDao().getServiceDesc(), 1, "Query", "");
			exceptionCode.setOtherInfo(vObject);
			exceptionCode.setResponse(list);
			return exceptionCode;
		}catch(Exception ex){
			exceptionCode = CommonUtils.getResultObject(getScreenDao().getServiceDesc(), 16, "Query", "");
			String errMsg = getScreenDao().parseErrorMsg(ex);
			exceptionCode.setErrorMsg(errMsg);
			exceptionCode.setResponse(list);
			return exceptionCode;
		}
	}
	
	public ExceptionCode getQueryResults(E vObject){
		int intStatus = 1;
		ExceptionCode exceptionCode = new ExceptionCode();
		setVerifReqDeleteType(vObject);
		exceptionCode = doValidate(vObject);
		if(exceptionCode.getErrorCode() == Constants.ERRONEOUS_OPERATION) {
			return exceptionCode;
		}
		List<E> collTemp = getScreenDao().getQueryResults(vObject,intStatus);
		if(collTemp.size() == 0){
			exceptionCode = CommonUtils.getResultObject(getScreenDao().getServiceDesc(), 16, "Query", "");
			exceptionCode.setOtherInfo(vObject);
			return exceptionCode;
		}else{
			doSetDesctiptionsAfterQuery(collTemp);
			doSetDesctiptionsAfterQuery(vObject);
			exceptionCode = CommonUtils.getResultObject(getScreenDao().getServiceDesc(), 1, "Query", "");
			exceptionCode.setOtherInfo(vObject);
			exceptionCode.setResponse(collTemp);
			return exceptionCode;
		}
	}
	
	/**
	 * Bulk Approve to be called from other Business classes 
	 * object. 
	 * @param vObject
	 * @return 
	 */
	public ExceptionCode bulkApprove(List<E> vObjects,E queryPopObj){
		ExceptionCode exceptionCode  = null;
		DeepCopy<E> deepCopy = new DeepCopy<E>();
		List<E> clonedObjects = null;
		try{
			setVerifReqDeleteType(queryPopObj);
			doFormateData(vObjects);
			clonedObjects = deepCopy.copyCollection(vObjects);
			exceptionCode = doValidate(vObjects);
			if(exceptionCode.getErrorCode() == Constants.ERRONEOUS_OPERATION){
				return exceptionCode;
			}
			exceptionCode =  getScreenDao().bulkApprove(vObjects,queryPopObj.isStaticDelete());
			ArrayList<E> tmpResult = (ArrayList<E>)getScreenDao().getQueryResults(queryPopObj,1);
			exceptionCode.setResponse(tmpResult);
			exceptionCode.setOtherInfo(queryPopObj);
			return exceptionCode;
		}catch(RuntimeCustomException rex){
			//logger.error("Bulk Approve Exception " + rex.getCode().getErrorMsg());
			exceptionCode = rex.getCode();
			exceptionCode.setOtherInfo(queryPopObj);
			String errMsg = getScreenDao().parseErrorMsg(rex);
			exceptionCode.setErrorMsg(errMsg);
			exceptionCode.setResponse(clonedObjects);
			return exceptionCode;
		}
	}
	/**
	 * Bulk Approve to be called from other Business classes 
	 * object. 
	 * @param vObject
	 * @return 
	 */
	public ExceptionCode bulkReject(List<E> vObjects,E queryPopObj){
		ExceptionCode exceptionCode  = null;
		DeepCopy<E> deepCopy = new DeepCopy<E>();
		List<E> clonedObjects = null;
		try{
			setVerifReqDeleteType(queryPopObj);
			clonedObjects = deepCopy.copyCollection(vObjects);
			exceptionCode = doValidate(vObjects);
			if(exceptionCode.getErrorCode() == Constants.ERRONEOUS_OPERATION){
				return exceptionCode;
			}
			exceptionCode =  getScreenDao().doBulkReject(vObjects);
			ArrayList<E> tmpResult = (ArrayList<E>) getScreenDao().getQueryResults(queryPopObj,1);
			exceptionCode.setResponse(tmpResult);
			exceptionCode.setOtherInfo(queryPopObj);
			return exceptionCode;
		}catch(RuntimeCustomException rex){
			//logger.error("Bulk Reject Exception " + rex.getCode().getErrorMsg());
			exceptionCode = rex.getCode();
			String errMsg = getScreenDao().parseErrorMsg(rex);
			exceptionCode.setErrorMsg(errMsg);
			exceptionCode.setOtherInfo(queryPopObj);
			exceptionCode.setResponse(clonedObjects);
			return exceptionCode;
		}
	}
	public ExceptionCode checkAddModifyRestriction(List<E> vObjects,String screenName) {
		ExceptionCode exceptionCode = new ExceptionCode();
		String addRestriction = commonDao.getRestrictionsByUsers(screenName, "Add");
		String modifyRestriction = commonDao.getRestrictionsByUsers(screenName, "Modify");
		List newReclst =  vObjects.stream().filter(n -> n.isNewRecord()).collect(Collectors.toList());
		List oldReclst =  vObjects.stream().filter(n -> !n.isNewRecord()).collect(Collectors.toList());
		if(!"Y".equalsIgnoreCase(addRestriction) && newReclst!= null && !newReclst.isEmpty()) {
			exceptionCode = new ExceptionCode();
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg("Add "+Constants.userRestrictionMsg);
			return exceptionCode;
		}
		if(!"Y".equalsIgnoreCase(modifyRestriction) && oldReclst!= null && !oldReclst.isEmpty()) {
			exceptionCode = new ExceptionCode();
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg("Modify "+Constants.userRestrictionMsg);
			return exceptionCode;
		}
		exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
		return exceptionCode;
	}
}
