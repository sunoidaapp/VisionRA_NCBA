package com.vision.wb;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.vision.dao.AbstractDao;
import com.vision.dao.AlphaNumTabDao;
import com.vision.dao.AlphaSubTabDao;
import com.vision.dao.NumSubTabDao;
import com.vision.exception.ExceptionCode;
import com.vision.exception.RuntimeCustomException;
import com.vision.util.CommonUtils;
import com.vision.util.Constants;
import com.vision.util.DeepCopy;
import com.vision.vb.AlphaSubTabVb;
import com.vision.vb.CommonVb;
import com.vision.vb.NumSubTabVb;
import com.vision.vb.ReviewResultVb;
import com.vision.vb.TabVb;

@Component
public class AlphaNumTabWb extends AbstractDynaWorkerBean<TabVb> {
	@Autowired
	private AlphaNumTabDao alphaNumTabDao;
	
	@Autowired
	private AlphaSubTabDao alphaSubTabDao;
	
	
	@Autowired
	private NumSubTabDao numSubTabDao;
	
	
	@Override
	protected AbstractDao<TabVb> getScreenDao() {
		return alphaNumTabDao;
	}
	// *******************************************************************************************************************************
	public ArrayList getPageLoadValues(){
		List collTemp = null;
		ArrayList<Object> arrListLocal = new ArrayList<Object>();
		try{
			collTemp = getNumSubTabDao().findActiveNumSubTabsByNumTab(1);
			arrListLocal.add(collTemp);
			collTemp = getNumSubTabDao().findActiveNumSubTabsByNumTab(7);
			arrListLocal.add(collTemp);
			return arrListLocal;
		}catch(Exception ex){
			//ex.printStackTrace();
			//logger.error("Exception in getting the Page load values.", ex);
			return null;
		}
	}
	
	public ExceptionCode getAllQueryPopupResultNumTab(TabVb vObject){
		ExceptionCode exceptionCode = new ExceptionCode();
			setVerifReqDeleteType(vObject);
			exceptionCode = doValidate(vObject);
			if(exceptionCode.getErrorCode() == Constants.ERRONEOUS_OPERATION){
				return exceptionCode;
			}
			List<TabVb> collTemp = getScreenDao().getQueryPopupResults(vObject);
			if (collTemp.size() == 0 && vObject.isVerificationRequired()){
				collTemp = getScreenDao().getQueryPopupResults(vObject);
			}
			if(collTemp != null && !collTemp.isEmpty())
				exceptionCode = CommonUtils.getResultObject(getScreenDao().getServiceName(), 1, Constants.QUERY, "");
			else
				exceptionCode = CommonUtils.getResultObject(getScreenDao().getServiceDesc(), Constants.NO_RECORDS_FOUND, "Query", "");
			exceptionCode.setResponse(collTemp);
			exceptionCode.setOtherInfo(vObject);
			
			return exceptionCode;
	}
	public ExceptionCode getAllQueryPopupResultAlpha(TabVb vObject){
		ExceptionCode exceptionCode = new ExceptionCode();
			setVerifReqDeleteType(vObject);
			vObject.setRequestType("alphaTab");
			setVerifReqDeleteType(vObject);
			exceptionCode = doValidate(vObject);
			if(exceptionCode.getErrorCode() == Constants.ERRONEOUS_OPERATION){
				return exceptionCode;
			}
			List<TabVb> collTemp = getScreenDao().getQueryPopupResults(vObject);
			if (collTemp.size() == 0 && vObject.isVerificationRequired()){
				collTemp = getScreenDao().getQueryPopupResults(vObject);
			}
			if(collTemp != null && !collTemp.isEmpty())
				exceptionCode = CommonUtils.getResultObject(getScreenDao().getServiceName(), 1, Constants.QUERY, "");
			else
				exceptionCode = CommonUtils.getResultObject(getScreenDao().getServiceDesc(), Constants.NO_RECORDS_FOUND, "Query", "");
			exceptionCode.setResponse(collTemp);
			exceptionCode.setOtherInfo(vObject);
			
			return exceptionCode;
	}
	// *******************************************************************************************************************************
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	@Override
	protected void setAtNtValues(TabVb tabVb) {
		tabVb.setRecordIndicatorNt(7);
		tabVb.setTabStatusNt(1);
		if(tabVb.getAlphaSubTabs() != null && !tabVb.getAlphaSubTabs().isEmpty()){
			for(AlphaSubTabVb alphaSubTabVb: tabVb.getAlphaSubTabs()){
				alphaSubTabVb.setRecordIndicatorNt(7);
				alphaSubTabVb.setAlphaSubTabStatusNt(1);
			}
		}
		if(tabVb.getNumSubTabs() != null && !tabVb.getNumSubTabs().isEmpty()){
			for(NumSubTabVb numSubTabVb: tabVb.getNumSubTabs()){
				numSubTabVb.setRecordIndicatorNt(7);
				numSubTabVb.setNumSubTabStatusNt(1);
			}
		}
	}

	@Override
	protected void setVerifReqDeleteType(TabVb tabVb) {
		if("numTab".equalsIgnoreCase(tabVb.getRequestType())){
			ArrayList<CommonVb> lCommVbList =(ArrayList<CommonVb>) getCommonDao().findVerificationRequiredAndStaticDelete("NUM_TAB");
			tabVb.setStaticDelete(lCommVbList.get(0).isStaticDelete());
			tabVb.setVerificationRequired(lCommVbList.get(0).isVerificationRequired());
		}else{
			ArrayList<CommonVb> lCommVbList =(ArrayList<CommonVb>) getCommonDao().findVerificationRequiredAndStaticDelete("ALPHA_TAB");
			tabVb.setStaticDelete(lCommVbList.get(0).isStaticDelete());
			tabVb.setVerificationRequired(lCommVbList.get(0).isVerificationRequired());
		}
	}
	public ArrayList getPageLoadValues(TabVb tabVb){
		List collTemp = null;
		ArrayList<Object> arrListLocal = new ArrayList<Object>();
		try{
			collTemp = getNumSubTabDao().findActiveNumSubTabsByNumTab(1);
			arrListLocal.add(collTemp);
			collTemp = getNumSubTabDao().findActiveNumSubTabsByNumTab(7);
			arrListLocal.add(collTemp);
			if("numTab".equalsIgnoreCase(tabVb.getRequestType())){
				collTemp = getCommonDao().findVerificationRequiredAndStaticDelete("NUM_TAB");
				arrListLocal.add(collTemp);
			}else{
				collTemp = getCommonDao().findVerificationRequiredAndStaticDelete("ALPHA_TAB");
				arrListLocal.add(collTemp);
			}
			return arrListLocal;
		}catch(Exception ex){
			//ex.printStackTrace();
			//logger.error("Exception in getting the Page load values.", ex);
			return null;
		}
	}
	@Override
	public ExceptionCode getQueryResults(TabVb vObject){
		ExceptionCode exceptionCode = new ExceptionCode();
		int intStatus = 1;
		exceptionCode = doValidate(vObject);
		if(exceptionCode.getErrorCode() == Constants.ERRONEOUS_OPERATION) {
			return exceptionCode;
		}
		setVerifReqDeleteType(vObject);
		List<TabVb> collTemp = getScreenDao().getQueryResults(vObject,intStatus);
		if (collTemp.size() == 0 && vObject.isVerificationRequired()){
			intStatus = 0;
			collTemp = getScreenDao().getQueryResults(vObject,intStatus);
		}
		if(collTemp.size() == 0){
			exceptionCode = CommonUtils.getResultObject(getScreenDao().getServiceName(), 41, Constants.QUERY, "");
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

	public ExceptionCode getQueryResultsforSubTabs(TabVb vObject) {
		setVerifReqDeleteType(vObject);
		ExceptionCode exceptionCode = new ExceptionCode();
		exceptionCode = doValidate(vObject);
		if(exceptionCode.getErrorCode() == Constants.ERRONEOUS_OPERATION) {
			return exceptionCode;
		}
		exceptionCode = new ExceptionCode();
		if ("numTab".equalsIgnoreCase(vObject.getRequestType())) {
			NumSubTabVb numSubTabVb = new NumSubTabVb();
			numSubTabVb.setNumTab(vObject.getTab());
			numSubTabVb.setMaxRecords(vObject.getMaxRecords());
			numSubTabVb.setCurrentPage(vObject.getCurrentPage());
			numSubTabVb.setTotalRows(vObject.getTotRecordsCount());
			numSubTabVb.setSmartSearchOpt(vObject.getSmartSearchOpt());
			List<NumSubTabVb> lResult = getNumSubTabDao().getQueryPopupResults(numSubTabVb);
			getNumSubTabDao().setPendingRecordCount(vObject);
			if (lResult != null && lResult.size() > 0) {
				exceptionCode.setOtherInfo(numSubTabVb);
				exceptionCode.setResponse(lResult);
				exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
			}else {
				exceptionCode = CommonUtils.getResultObject(getScreenDao().getServiceDesc(), Constants.NO_RECORDS_FOUND, "Query", "");
			}
		} else {
			AlphaSubTabVb alphaSubTabVb = new AlphaSubTabVb();
			alphaSubTabVb.setAlphaTab(vObject.getTab());
			alphaSubTabVb.setStaticDelete(vObject.isStaticDelete());
			alphaSubTabVb.setVerificationRequired(vObject.isVerificationRequired());
			alphaSubTabVb.setMaxRecords(vObject.getMaxRecords());
			alphaSubTabVb.setCurrentPage(vObject.getCurrentPage());
			alphaSubTabVb.setTotalRows(vObject.getTotRecordsCount());
			alphaSubTabVb.setSmartSearchOpt(vObject.getSmartSearchOpt());
			List<AlphaSubTabVb> lResult = getAlphaSubTabDao().getQueryPopupResults(alphaSubTabVb);
			getAlphaSubTabDao().setPendingRecordCount(vObject);
			if (lResult != null && lResult.size() > 0) {
				exceptionCode.setOtherInfo(alphaSubTabVb);
				exceptionCode.setResponse(lResult);
				exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
			}else {
				exceptionCode = CommonUtils.getResultObject(getScreenDao().getServiceDesc(), Constants.NO_RECORDS_FOUND, "Query", "");
			}
		}
		getScreenDao().fetchMakerVerifierNames(vObject);
		vObject.setVerificationRequired(vObject.isVerificationRequired());
		vObject.setStaticDelete(vObject.isStaticDelete());
		return exceptionCode;
	}
	/**
	 * Inserts a new record to the approved or pending tables depending on Verification Required flag of the 
	 * object. 
	 * @param vObject
	 * @return 
	 */
	@Override
	public ExceptionCode insertRecord(TabVb vObject){
		ExceptionCode exceptionCode  = null;
		DeepCopy<TabVb> deepCopy = new DeepCopy<TabVb>();
		TabVb clonedObject = null;
		try
		{
			setAtNtValues(vObject);
			setVerifReqDeleteType(vObject);
			clonedObject = deepCopy.copy(vObject);
			exceptionCode = doValidate(vObject);
			if(exceptionCode.getErrorCode() == Constants.ERRONEOUS_OPERATION){
				return exceptionCode;
			}
			if(!vObject.isVerificationRequired()){
				exceptionCode = getScreenDao().doInsertApprRecord(vObject);
			}else{
				exceptionCode = getScreenDao().doInsertRecord(vObject);
			}
			exceptionCode.setOtherInfo(vObject);
			if("numTab".equalsIgnoreCase(vObject.getRequestType())){
				exceptionCode.setResponse(vObject.getNumSubTabs());
			}else{
				exceptionCode.setResponse(vObject.getAlphaSubTabs());
			}
			return exceptionCode;
		}catch(RuntimeCustomException rex){
			//logger.error("Insert Exception " + rex.getCode().getErrorMsg());
			//logger.error( ((vObject==null)? "vObject is Null":vObject.toString()));
			exceptionCode = rex.getCode();
			String errorMsg = getScreenDao().parseErrorMsg(rex);
			exceptionCode.setErrorMsg(errorMsg);
			exceptionCode.setOtherInfo(clonedObject);
			if("numTab".equalsIgnoreCase(vObject.getRequestType())){
				exceptionCode.setResponse(clonedObject.getNumSubTabs());
			}else{
				exceptionCode.setResponse(clonedObject.getAlphaSubTabs());
			}
			return exceptionCode;
		}
	}
	/**
	 * updates the existing record in the approved or pending tables depending on Verification Required flag of the 
	 * object. 
	 * @param vObject
	 * @return 
	 */
	public ExceptionCode modifyRecord(TabVb vObject){
		ExceptionCode exceptionCode  = null;
		DeepCopy<TabVb> deepCopy = new DeepCopy<TabVb>();
		TabVb clonedObject = null;
		try{
			setAtNtValues(vObject);
			setVerifReqDeleteType(vObject);
			clonedObject = deepCopy.copy(vObject);
			exceptionCode = doValidate(vObject);
			if(exceptionCode.getErrorCode() == Constants.ERRONEOUS_OPERATION){
				return exceptionCode;
			}
			if(!vObject.isVerificationRequired()){
				exceptionCode = getScreenDao().doUpdateApprRecord(vObject);
			}else{
				exceptionCode = getScreenDao().doUpdateRecord(vObject);
			}
			exceptionCode.setOtherInfo(vObject);
			if("numTab".equalsIgnoreCase(vObject.getRequestType())){
				exceptionCode.setResponse(vObject.getNumSubTabs());
			}else{
				exceptionCode.setResponse(vObject.getAlphaSubTabs());
			}
			return exceptionCode;
		}catch(RuntimeCustomException rex){
			//logger.error("Modify Exception " + rex.getCode().getErrorMsg());
			//logger.error( ((vObject==null)? "vObject is Null":vObject.toString()));
			exceptionCode = rex.getCode();
			String errorMsg = getScreenDao().parseErrorMsg(rex);
			exceptionCode.setErrorMsg(errorMsg);
			exceptionCode.setOtherInfo(clonedObject);
			if("numTab".equalsIgnoreCase(vObject.getRequestType())){
				exceptionCode.setResponse(clonedObject.getNumSubTabs());
			}else{
				exceptionCode.setResponse(clonedObject.getAlphaSubTabs());
			}
			return exceptionCode;
		}
	}
	/**
	 * Delete the existing record in the approved or pending tables depending on Verification Required flag of the 
	 * object. 
	 * @param vObject
	 * @return 
	 */
	@Override
	public ExceptionCode deleteRecord(TabVb vObject){
		ExceptionCode exceptionCode  = null;
		DeepCopy<TabVb> deepCopy = new DeepCopy<TabVb>();
		TabVb clonedObject = null;
		try{
			setAtNtValues(vObject);
			setVerifReqDeleteType(vObject);
			clonedObject = deepCopy.copy(vObject);
			exceptionCode = doValidate(vObject);
			if(exceptionCode.getErrorCode() == Constants.ERRONEOUS_OPERATION){
				return exceptionCode;
			}
			if(!vObject.isVerificationRequired()){
				exceptionCode = getScreenDao().doDeleteApprRecord(vObject);
			}else{
				exceptionCode = getScreenDao().doDeleteRecord(vObject);
			}
			exceptionCode.setOtherInfo(vObject);
			if("numTab".equalsIgnoreCase(vObject.getRequestType())){
				exceptionCode.setResponse(vObject.getNumSubTabs());
			}else{
				exceptionCode.setResponse(vObject.getAlphaSubTabs());
			}
			return exceptionCode;
		}catch(RuntimeCustomException rex){
			//logger.error("Delete Exception " + rex.getCode().getErrorMsg());
			//logger.error( ((vObject==null)? "vObject is Null":vObject.toString()));
			exceptionCode = rex.getCode();
			String errorMsg = getScreenDao().parseErrorMsg(rex);
			exceptionCode.setErrorMsg(errorMsg);
			exceptionCode.setOtherInfo(clonedObject);
			if("numTab".equalsIgnoreCase(vObject.getRequestType())){
				exceptionCode.setResponse(clonedObject.getNumSubTabs());
			}else{
				exceptionCode.setResponse(clonedObject.getAlphaSubTabs());
			}
			return exceptionCode;
		}
	}
	/**
	 * Approve to be called from other Business classes 
	 * object. 
	 * @param vObject
	 * @return 
	 */
	public ExceptionCode bulkApprove(List<TabVb> vObjects,TabVb queryPopObj){
		ExceptionCode exceptionCode  = null;
		DeepCopy<TabVb> deepCopy = new DeepCopy<TabVb>();
		List<TabVb> clonedObjects = null;
		try{
			setVerifReqDeleteType(queryPopObj);
			doFormateData(vObjects);
			clonedObjects = deepCopy.copyCollection(vObjects);
			exceptionCode = doValidate(queryPopObj);
			if(exceptionCode.getErrorCode() == Constants.ERRONEOUS_OPERATION){
				return exceptionCode;
			}
			exceptionCode =  getScreenDao().bulkApprove(vObjects,queryPopObj.isStaticDelete());
			ArrayList<TabVb> tmpResult = (ArrayList<TabVb>)getScreenDao().getQueryResults(queryPopObj,1);
			exceptionCode.setResponse(tmpResult);
			exceptionCode.setOtherInfo(queryPopObj);
			return exceptionCode;
		}catch(RuntimeCustomException rex){
			//logger.error("Bulk Approve Exception " + rex.getCode().getErrorMsg());
			exceptionCode = rex.getCode();
			String errorMsg = getScreenDao().parseErrorMsg(rex);
			exceptionCode.setErrorMsg(errorMsg);
			exceptionCode.setOtherInfo(queryPopObj);
			exceptionCode.setResponse(clonedObjects);
			return exceptionCode;
		}
	}
	public ExceptionCode bulkReject(List<TabVb> vObjects,TabVb queryPopObj){
		ExceptionCode exceptionCode  = null;
		DeepCopy<TabVb> deepCopy = new DeepCopy<TabVb>();
		List<TabVb> clonedObjects = null;
		try{
			setVerifReqDeleteType(queryPopObj);
			clonedObjects = deepCopy.copyCollection(vObjects);
			exceptionCode = doValidate(queryPopObj);
			if(exceptionCode.getErrorCode() == Constants.ERRONEOUS_OPERATION){
				return exceptionCode;
			}
			exceptionCode =  getScreenDao().doBulkReject(vObjects);
			ArrayList<TabVb> tmpResult = (ArrayList<TabVb>) getScreenDao().getQueryResults(queryPopObj,1);
			exceptionCode.setResponse(tmpResult);
			exceptionCode.setOtherInfo(queryPopObj);
			return exceptionCode;
		}catch(RuntimeCustomException rex){
			//logger.error("Bulk Reject Exception " + rex.getCode().getErrorMsg());
			exceptionCode = rex.getCode();
			String errorMsg = getScreenDao().parseErrorMsg(rex);
			exceptionCode.setErrorMsg(errorMsg);
			exceptionCode.setOtherInfo(queryPopObj);
			exceptionCode.setResponse(clonedObjects);
			return exceptionCode;
		}
	}
	public ExceptionCode approve(TabVb vObject){
		ExceptionCode exceptionCode  = null;
		DeepCopy<TabVb> deepCopy = new DeepCopy<TabVb>();
		TabVb clonedObject = null;
		try{
			setVerifReqDeleteType(vObject);
			clonedObject = deepCopy.copy(vObject);
			exceptionCode = doValidate(vObject);
			if(exceptionCode.getErrorCode() == Constants.ERRONEOUS_OPERATION){
				return exceptionCode;
			}
			exceptionCode = getScreenDao().doApproveForTransaction(vObject,vObject.isStaticDelete());
			exceptionCode.setOtherInfo(vObject);
			if("numTab".equalsIgnoreCase(vObject.getRequestType())){
				exceptionCode.setResponse(vObject.getNumSubTabs());
			}else{
				exceptionCode.setResponse(vObject.getAlphaSubTabs());
			}
			return exceptionCode;
		}catch(RuntimeCustomException rex){
			//logger.error("Approve Exception " + rex.getCode().getErrorMsg());
			exceptionCode = rex.getCode();
			String errorMsg = getScreenDao().parseErrorMsg(rex);
			exceptionCode.setErrorMsg(errorMsg);
			exceptionCode.setOtherInfo(clonedObject);
			if("numTab".equalsIgnoreCase(vObject.getRequestType())){
				exceptionCode.setResponse(clonedObject.getNumSubTabs());
			}else{
				exceptionCode.setResponse(clonedObject.getAlphaSubTabs());
			}
			return exceptionCode;
		}
	}
	/**
	 * Approve to be called from other Business classes 
	 * object. 
	 * @param vObject
	 * @return 
	 */
	public ExceptionCode reject(TabVb vObject){
		ExceptionCode exceptionCode  = null;
		DeepCopy<TabVb> deepCopy = new DeepCopy<TabVb>();
		TabVb clonedObject = null;
		try{
			setVerifReqDeleteType(vObject);
			clonedObject = deepCopy.copy(vObject);
			exceptionCode = doValidate(vObject);
			if(exceptionCode.getErrorCode() == Constants.ERRONEOUS_OPERATION){
				return exceptionCode;
			}
			exceptionCode = getScreenDao().doRejectForTransaction(vObject);
			exceptionCode.setOtherInfo(vObject);
			if("numTab".equalsIgnoreCase(vObject.getRequestType())){
				exceptionCode.setResponse(vObject.getNumSubTabs());
			}else{
				exceptionCode.setResponse(vObject.getAlphaSubTabs());
			}
			return exceptionCode;
		}catch(RuntimeCustomException rex){
			//logger.error("Reject Exception " + rex.getCode().getErrorMsg());
			exceptionCode = rex.getCode();
			String errorMsg = getScreenDao().parseErrorMsg(rex);
			exceptionCode.setErrorMsg(errorMsg);
			exceptionCode.setOtherInfo(clonedObject);
			if("numTab".equalsIgnoreCase(vObject.getRequestType())){
				exceptionCode.setResponse(clonedObject.getNumSubTabs());
			}else{
				exceptionCode.setResponse(clonedObject.getAlphaSubTabs());
			}
			return exceptionCode;
		}
	}
	@Override
	public List<ReviewResultVb> reviewRecord(TabVb vObject){
		try{
			setVerifReqDeleteType(vObject);
			List<TabVb> approvedCollection = getScreenDao().getQueryResultsForReview(vObject,0);
			List<TabVb> pendingCollection = getScreenDao().getQueryResultsForReview(vObject,1);
			if("numTab".equalsIgnoreCase(vObject.getRequestType())){
				return transformToReviewResultsNumTab(approvedCollection,pendingCollection);
			}else{
				return transformToReviewResultsAlphaTab(approvedCollection,pendingCollection);
			}
		}catch(Exception ex){
			return null;
		}
	}
	private List<ReviewResultVb> transformToReviewResultsNumTab(List<TabVb> approvedCollection, List<TabVb> pendingCollection) {
		ResourceBundle rsb = CommonUtils.getResourceManger();
		ArrayList collTemp = getPageLoadValues(new TabVb());
		if(pendingCollection != null)
			getScreenDao().fetchMakerVerifierNames(pendingCollection.get(0));
		if(approvedCollection != null)
			getScreenDao().fetchMakerVerifierNames(approvedCollection.get(0));
		ArrayList<ReviewResultVb> lResult = new ArrayList<ReviewResultVb>();
		ReviewResultVb lTab = new ReviewResultVb(rsb.getString("numTab"),(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getTab() == -1?"":String.valueOf(pendingCollection.get(0).getTab()),
				(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getTab() == -1?"":String.valueOf(approvedCollection.get(0).getTab()), !(approvedCollection.get(0).getTab() == pendingCollection.get(0).getTab()));
		lResult.add(lTab);
		ReviewResultVb lDescription = new ReviewResultVb(rsb.getString("numTabDescription"),(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getTabDescription(),
				(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getTabDescription(), !(approvedCollection.get(0).getTabDescription().equals(pendingCollection.get(0).getTabDescription())));
		lResult.add(lDescription);
		ReviewResultVb lStatus = new ReviewResultVb(rsb.getString("numTabStatus"),(pendingCollection == null || pendingCollection.isEmpty())?"":getNtDescription((List<NumSubTabVb>) collTemp.get(0),pendingCollection.get(0).getTabStatus()),
				(approvedCollection == null || approvedCollection.isEmpty())?"":getNtDescription((List<NumSubTabVb>) collTemp.get(0),approvedCollection.get(0).getTabStatus()), !(approvedCollection.get(0).getTabStatus() == pendingCollection.get(0).getTabStatus()));
		lResult.add(lStatus);
		ReviewResultVb lRecordIndicator = new ReviewResultVb(rsb.getString("recordIndicator"),(pendingCollection == null || pendingCollection.isEmpty())?"":getNtDescription((List<NumSubTabVb>) collTemp.get(1), pendingCollection.get(0).getRecordIndicator()),
				(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getRecordIndicator() == -1?"":getNtDescription((List<NumSubTabVb>) collTemp.get(1), approvedCollection.get(0).getRecordIndicator()), !(approvedCollection.get(0).getRecordIndicator() == pendingCollection.get(0).getRecordIndicator()));
		lResult.add(lRecordIndicator);
		ReviewResultVb lMaker = new ReviewResultVb(rsb.getString("maker"),(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getMaker() == 0?"":pendingCollection.get(0).getMakerName(),
				(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getMaker() == 0?"":approvedCollection.get(0).getMakerName(), !(approvedCollection.get(0).getMaker() == pendingCollection.get(0).getMaker()));
		lResult.add(lMaker);
		ReviewResultVb lVerifier = new ReviewResultVb(rsb.getString("verifier"),(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getVerifier() == 0?"":pendingCollection.get(0).getVerifierName(),
				(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getVerifier() == 0?"":approvedCollection.get(0).getVerifierName(), !(approvedCollection.get(0).getVerifier() == pendingCollection.get(0).getVerifier()));
		lResult.add(lVerifier);
		ReviewResultVb lDateLastModified = new ReviewResultVb(rsb.getString("dateLastModified"),(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getDateLastModified(),
				(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getDateLastModified(), !(approvedCollection.get(0).getDateLastModified().equals(pendingCollection.get(0).getDateLastModified())));
		lResult.add(lDateLastModified);
		ReviewResultVb lDateCreation = new ReviewResultVb(rsb.getString("dateCreation"),(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getDateCreation(),
				(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getDateCreation(), !(approvedCollection.get(0).getDateCreation().equals(pendingCollection.get(0).getDateCreation())));
		lResult.add(lDateCreation);
		return lResult;
	}
	private List<ReviewResultVb> transformToReviewResultsAlphaTab(List<TabVb> approvedCollection, List<TabVb> pendingCollection) {
		ResourceBundle rsb = CommonUtils.getResourceManger();
		ArrayList collTemp = getPageLoadValues(new TabVb());
		if(pendingCollection != null)
			getScreenDao().fetchMakerVerifierNames(pendingCollection.get(0));
		if(approvedCollection != null)
			getScreenDao().fetchMakerVerifierNames(approvedCollection.get(0));
		ArrayList<ReviewResultVb> lResult = new ArrayList<ReviewResultVb>();
		ReviewResultVb lTab = new ReviewResultVb(rsb.getString("alphaTab"),(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getTab() == -1?"":String.valueOf(pendingCollection.get(0).getTab()),
				(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getTab() == -1?"":String.valueOf(approvedCollection.get(0).getTab()), !(approvedCollection.get(0).getTab() == pendingCollection.get(0).getTab()));
		lResult.add(lTab);
		ReviewResultVb lDescription = new ReviewResultVb(rsb.getString("alphaTabDescription"),(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getTabDescription(),
				(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getTabDescription(), !(approvedCollection.get(0).getTabDescription().equals(pendingCollection.get(0).getTabDescription())));
		lResult.add(lDescription);
		ReviewResultVb lStatus = new ReviewResultVb(rsb.getString("alphaTabStatus"),(pendingCollection == null || pendingCollection.isEmpty())?"":getNtDescription((List<NumSubTabVb>) collTemp.get(0),pendingCollection.get(0).getTabStatus()),
				(approvedCollection == null || approvedCollection.isEmpty())?"":getNtDescription((List<NumSubTabVb>) collTemp.get(0),approvedCollection.get(0).getTabStatus()), !(approvedCollection.get(0).getTabStatus() == pendingCollection.get(0).getTabStatus()));
		lResult.add(lStatus);
		ReviewResultVb lRecordIndicator = new ReviewResultVb(rsb.getString("recordIndicator"),(pendingCollection == null || pendingCollection.isEmpty())?"":getNtDescription((List<NumSubTabVb>) collTemp.get(1), pendingCollection.get(0).getRecordIndicator()),
				(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getRecordIndicator() == -1?"":getNtDescription((List<NumSubTabVb>) collTemp.get(1), approvedCollection.get(0).getRecordIndicator()), !(approvedCollection.get(0).getRecordIndicator() == pendingCollection.get(0).getRecordIndicator()));
		lResult.add(lRecordIndicator);
		ReviewResultVb lMaker = new ReviewResultVb(rsb.getString("maker"),(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getMaker() == 0?"":pendingCollection.get(0).getMakerName(),
				(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getMaker() == 0?"":approvedCollection.get(0).getMakerName(), !(approvedCollection.get(0).getMaker() == pendingCollection.get(0).getMaker()));
		lResult.add(lMaker);
		ReviewResultVb lVerifier = new ReviewResultVb(rsb.getString("verifier"),(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getVerifier() == 0?"":pendingCollection.get(0).getVerifierName(),
				(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getVerifier() == 0?"":approvedCollection.get(0).getVerifierName(), !(approvedCollection.get(0).getVerifier() == pendingCollection.get(0).getVerifier()));
		lResult.add(lVerifier);
		ReviewResultVb lDateLastModified = new ReviewResultVb(rsb.getString("dateLastModified"),(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getDateLastModified(),
				(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getDateLastModified(), !(approvedCollection.get(0).getDateLastModified().equals(pendingCollection.get(0).getDateLastModified())));
		lResult.add(lDateLastModified);
		ReviewResultVb lDateCreation = new ReviewResultVb(rsb.getString("dateCreation"),(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getDateCreation(),
				(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getDateCreation(), !(approvedCollection.get(0).getDateCreation().equals(pendingCollection.get(0).getDateCreation())));
		lResult.add(lDateCreation);
		return lResult;
	}
	public AlphaNumTabDao getAlphaNumTabDao() {
		return alphaNumTabDao;
	}

	public void setAlphaNumTabDao(AlphaNumTabDao alphaNumTabDao) {
		this.alphaNumTabDao = alphaNumTabDao;
	}

	public NumSubTabDao getNumSubTabDao() {
		return numSubTabDao;
	}

	public void setNumSubTabDao(NumSubTabDao numSubTabDao) {
		this.numSubTabDao = numSubTabDao;
	}

	public AlphaSubTabDao getAlphaSubTabDao() {
		return alphaSubTabDao;
	}

	public void setAlphaSubTabDao(AlphaSubTabDao alphaSubTabDao) {
		this.alphaSubTabDao = alphaSubTabDao;
	}
	public ExceptionCode doValidate(TabVb vObject){
		ExceptionCode exceptionCode = new ExceptionCode();
		String operation = vObject.getActionType();
		String srtRestriction = getCommonDao().getRestrictionsByUsers("AlphaNumTab", operation);
		if(!"Y".equalsIgnoreCase(srtRestriction)) {
			exceptionCode = new ExceptionCode();
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg(operation +" "+Constants.userRestrictionMsg);
			return exceptionCode;
		}
		exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
		return exceptionCode;
	}

}