package com.vision.wb;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.vision.dao.AbstractDao;
import com.vision.dao.CommonDao;
import com.vision.dao.RaTellerFreeDao;
import com.vision.exception.ExceptionCode;
import com.vision.util.CommonUtils;
import com.vision.util.Constants;
import com.vision.vb.AlphaSubTabVb;
import com.vision.vb.CommonVb;
import com.vision.vb.NumSubTabVb;
import com.vision.vb.RaTellerFreeVb;
import com.vision.vb.ReviewResultVb;

@Component
public class RaTellerFreeWb extends AbstractDynaWorkerBean<RaTellerFreeVb>{

	@Autowired
	private RaTellerFreeDao raTellerFreeDao;
	public static Logger logger = LoggerFactory.getLogger(RaTellerFreeWb.class);
	
	@Autowired
	CommonDao commonDao;
	
	public ArrayList getPageLoadValues() {
		List collTemp = null;
		ArrayList<Object> arrListLocal = new ArrayList<Object>();
		try {
			
			collTemp = commonDao.findActiveAlphaSubTabsByAlphaTab(7060);//Data Source
			arrListLocal.add(collTemp);
			collTemp = getNumSubTabDao().findActiveNumSubTabsByNumTab(1); //Status
			arrListLocal.add(collTemp);
			collTemp = getNumSubTabDao().findActiveNumSubTabsByNumTab(7); //Record Indicator
			arrListLocal.add(collTemp);
			String country = commonDao.findVisionVariableValue("DEFAULT_COUNTRY");
			arrListLocal.add(country);
			String leBook = commonDao.findVisionVariableValue("DEFAULT_LE_BOOK");
			arrListLocal.add(leBook);
			return arrListLocal;	
		} catch (Exception ex) {
		//	ex.printStackTrace();
			//logger.error("Exception in getting the Page load values.", ex);
			return null;
		}
	}
	
	@Override
	protected AbstractDao<RaTellerFreeVb> getScreenDao() {
		return raTellerFreeDao;
	}
	@Override
	protected void setAtNtValues(RaTellerFreeVb vObject) {
		vObject.setRecordIndicatorNt(7);
		vObject.setTellerFreeStatusNt(1);
		vObject.setTellerBucketAt(7060);
	}
	@Override
	protected void setVerifReqDeleteType(RaTellerFreeVb vObject) {
		ArrayList<CommonVb> lCommVbList =(ArrayList<CommonVb>) getCommonDao().findVerificationRequiredAndStaticDelete("RA_TELLER_FREE");
		vObject.setStaticDelete(lCommVbList.get(0).isStaticDelete());
		vObject.setVerificationRequired(lCommVbList.get(0).isVerificationRequired());
		
	}
	
	@Override
	protected List<ReviewResultVb> transformToReviewResults(List<RaTellerFreeVb> approvedCollection, List<RaTellerFreeVb> pendingCollection) {	
		ResourceBundle rsb = CommonUtils.getResourceManger();
		ArrayList collTemp = getPageLoadValues();
		if(pendingCollection != null)
			getScreenDao().fetchMakerVerifierNames(pendingCollection.get(0));
		if(approvedCollection != null)
			getScreenDao().fetchMakerVerifierNames(approvedCollection.get(0));
		ArrayList<ReviewResultVb> lResult = new ArrayList<ReviewResultVb>();
		
		ReviewResultVb lCountry = new ReviewResultVb(rsb.getString("country"),(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getCountry(),
	            (approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getCountry(),
	            		(!pendingCollection.get(0).getCountry().equals(approvedCollection.get(0).getCountry())));
		lResult.add(lCountry);
		
		ReviewResultVb lLeBook = new ReviewResultVb(rsb.getString("leBook"),(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getLeBook(),
	            (approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getLeBook(),
	            		(!pendingCollection.get(0).getLeBook().equals(approvedCollection.get(0).getLeBook())));
		lResult.add(lLeBook);
		
		ReviewResultVb lCustomerId = new ReviewResultVb(rsb.getString("customerId"),(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getCustomerId(),
	            (approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getCustomerId(),
	            		(!pendingCollection.get(0).getCustomerId().equals(approvedCollection.get(0).getCustomerId())));
		lResult.add(lCustomerId);
		
		ReviewResultVb lCustomerName = new ReviewResultVb(rsb.getString("customerName"),(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getCustomerName(),
	            (approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getCustomerName(),
	            		(!pendingCollection.get(0).getCustomerName().equals(approvedCollection.get(0).getCustomerName())));
		lResult.add(lCustomerName);
		
		ReviewResultVb lTellerBucket = new ReviewResultVb(rsb.getString("tellerBucket"),
				(pendingCollection == null || pendingCollection.isEmpty())?"":getAtDescription((List<AlphaSubTabVb>) collTemp.get(0),pendingCollection.get(0).getTellerBucket() ),
		        (approvedCollection == null || approvedCollection.isEmpty())?"":getAtDescription((List<AlphaSubTabVb>) collTemp.get(0),approvedCollection.get(0).getTellerBucket()),
		        		(!pendingCollection.get(0).getTellerBucket().equals(approvedCollection.get(0).getTellerBucket())));
		lResult.add(lTellerBucket);
		
		ReviewResultVb lEffectiveDate = new ReviewResultVb(rsb.getString("effectiveDate"),(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getEffectiveDateStart(),
	            (approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getEffectiveDateStart(),
	            		(!pendingCollection.get(0).getEffectiveDateStart().equals(approvedCollection.get(0).getEffectiveDateStart())));
		lResult.add(lEffectiveDate);
		
		ReviewResultVb lExpiryDate = new ReviewResultVb(rsb.getString("expiryDate"),(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getEffectiveDateEnd(),
	            (approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getEffectiveDateEnd(),
	            		(!pendingCollection.get(0).getEffectiveDateEnd().equals(approvedCollection.get(0).getEffectiveDateEnd())));
		lResult.add(lExpiryDate);
		
		ReviewResultVb lStatus = new ReviewResultVb(rsb.getString("status"),(pendingCollection == null || pendingCollection.isEmpty())?"":getNtDescription((List<NumSubTabVb>) collTemp.get(1),pendingCollection.get(0).getTellerFreeStatus()),
	            (approvedCollection == null || approvedCollection.isEmpty())?"":getNtDescription((List<NumSubTabVb>) collTemp.get(1),approvedCollection.get(0).getTellerFreeStatus()),
	            		(pendingCollection.get(0).getTellerFreeStatus() != approvedCollection.get(0).getTellerFreeStatus()));
		lResult.add(lStatus); 
		
		ReviewResultVb lRecordIndicator = new ReviewResultVb(rsb.getString("recordIndicator"),(pendingCollection == null || pendingCollection.isEmpty())?"":getNtDescription((List<NumSubTabVb>) collTemp.get(2),pendingCollection.get(0).getRecordIndicator()),
		            (approvedCollection == null || approvedCollection.isEmpty())?"":getNtDescription((List<NumSubTabVb>) collTemp.get(2),approvedCollection.get(0).getRecordIndicator()),
		            		(pendingCollection.get(0).getRecordIndicator() != approvedCollection.get(0).getRecordIndicator()));
		lResult.add(lRecordIndicator);
		
		ReviewResultVb lMaker = new ReviewResultVb(rsb.getString("maker"),(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getMaker() == 0?"":pendingCollection.get(0).getMakerName(),
				(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getMaker() == 0?"":approvedCollection.get(0).getMakerName(),
						(pendingCollection.get(0).getMaker() != approvedCollection.get(0).getMaker()));
		lResult.add(lMaker);
		
		ReviewResultVb lVerifier = new ReviewResultVb(rsb.getString("verifier"),(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getVerifier() == 0?"":pendingCollection.get(0).getVerifierName(),
				(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getVerifier() == 0?"":approvedCollection.get(0).getVerifierName(),
						(pendingCollection.get(0).getVerifier() != approvedCollection.get(0).getVerifier()));
		lResult.add(lVerifier);
		
		ReviewResultVb lDateLastModified = new ReviewResultVb(rsb.getString("dateLastModified"),(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getDateLastModified(),
		            (approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getDateLastModified(),
		            		(!pendingCollection.get(0).getDateLastModified().equals(approvedCollection.get(0).getDateLastModified())));
		lResult.add(lDateLastModified);
		
		ReviewResultVb lDateCreation = new ReviewResultVb(rsb.getString("dateCreation"),(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getDateCreation(),
		            (approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getDateCreation(),
		            		(!pendingCollection.get(0).getDateCreation().equals(approvedCollection.get(0).getDateCreation())));
		lResult.add(lDateCreation);
		
		return lResult;
	}
	public ExceptionCode getQueryResults(RaTellerFreeVb vObject){
		int intStatus = Constants.STATUS_PENDING;
		ExceptionCode exceptionCode = new ExceptionCode();
		setVerifReqDeleteType(vObject);
		exceptionCode = doValidate(vObject);
		if(exceptionCode.getErrorCode() == Constants.ERRONEOUS_OPERATION){
			return exceptionCode;
		}
		List<RaTellerFreeVb> collTemp = getScreenDao().getQueryResults(vObject,intStatus);
		if (collTemp.size() == 0){
			intStatus = Constants.STATUS_ZERO;
			collTemp = getScreenDao().getQueryResults(vObject,intStatus);
		}
		if(collTemp.size() == 0){
			 exceptionCode = CommonUtils.getResultObject(getScreenDao().getServiceDesc(), 16, "Query", "");
			exceptionCode.setOtherInfo(vObject);
			return exceptionCode;
		}else{
			doSetDesctiptionsAfterQuery(((ArrayList<RaTellerFreeVb>)collTemp).get(0));
			 exceptionCode = CommonUtils.getResultObject(getScreenDao().getServiceDesc(), 1, "Query", "");
			getScreenDao().fetchMakerVerifierNames(((ArrayList<RaTellerFreeVb>)collTemp).get(0));
			((ArrayList<RaTellerFreeVb>)collTemp).get(0).setVerificationRequired(vObject.isVerificationRequired());
			((ArrayList<RaTellerFreeVb>)collTemp).get(0).setStaticDelete(vObject.isStaticDelete());
			exceptionCode.setResponse(((ArrayList<RaTellerFreeVb>)collTemp).get(0));
			return exceptionCode;
		}
	} 
	

	public ExceptionCode doValidate(RaTellerFreeVb vObject){
		ExceptionCode exceptionCode = new ExceptionCode();
		String operation = vObject.getActionType();
		String srtRestriction = getCommonDao().getRestrictionsByUsers("TellerCounter", operation);
		if(!"Y".equalsIgnoreCase(srtRestriction)) {
			exceptionCode = new ExceptionCode();
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg(operation +" "+Constants.userRestrictionMsg);
			return exceptionCode;
		}
		exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
		return exceptionCode;
	}
	
	
	protected ExceptionCode doValidate(List<RaTellerFreeVb> vObjects) {
		ExceptionCode exceptionCode = new ExceptionCode();
		RaTellerFreeVb vObject = vObjects.get(0);
		String operation = vObject.getActionType();
		if (vObject.getActionType().equalsIgnoreCase("Add") || vObject.getActionType().equalsIgnoreCase("Modify")) {
			return checkAddModifyRestriction(vObjects, "TellerCounter");
		} else {
			String srtRestriction = getCommonDao().getRestrictionsByUsers("TellerCounter", operation);
			if (!"Y".equalsIgnoreCase(srtRestriction)) {
				exceptionCode = new ExceptionCode();
				exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
				exceptionCode.setErrorMsg(operation + " " + Constants.userRestrictionMsg);
				return exceptionCode;
			}
		}
		exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
		return exceptionCode;
	}
}
