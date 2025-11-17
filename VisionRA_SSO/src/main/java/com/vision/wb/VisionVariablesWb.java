/**
 * 
 */
package com.vision.wb;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.vision.dao.AbstractDao;
import com.vision.dao.VisionVariablesDao;
import com.vision.exception.ExceptionCode;
import com.vision.util.CommonUtils;
import com.vision.util.Constants;
import com.vision.vb.AlphaSubTabVb;
import com.vision.vb.CommonVb;
import com.vision.vb.NumSubTabVb;
import com.vision.vb.ReviewResultVb;
import com.vision.vb.VisionVariablesVb;

@Component
public class VisionVariablesWb extends AbstractDynaWorkerBean<VisionVariablesVb>{
	
	@Autowired
	private VisionVariablesDao visionVariablesDao;
	public static Logger logger = LoggerFactory.getLogger(VisionVariablesWb.class);
	
	public ArrayList getPageLoadValues(){
		List collTemp = null;
		ArrayList<Object> arrListLocal = new ArrayList<Object>();
		try{
			collTemp = getNumSubTabDao().findActiveNumSubTabsByNumTab(1);
			arrListLocal.add(collTemp);
			collTemp = getNumSubTabDao().findActiveNumSubTabsByNumTab(7);
			arrListLocal.add(collTemp);
			collTemp = getAlphaSubTabDao().findActiveAlphaSubTabsByAlphaTab(8);
			arrListLocal.add(collTemp);
			collTemp = getCommonDao().findVerificationRequiredAndStaticDelete("VISION_VARIABLES");
			arrListLocal.add(collTemp);
			return arrListLocal;
		}catch(Exception ex){
	//		ex.printStackTrace();
		//	logger.error("Exception in getting the Page load values.", ex);
			return null;
		}
	}
	@Override
	protected List<ReviewResultVb> transformToReviewResults(List<VisionVariablesVb> approvedCollection, List<VisionVariablesVb> pendingCollection) {
		ResourceBundle rsb=CommonUtils.getResourceManger();
		ArrayList collTemp = getPageLoadValues();
		if(pendingCollection != null)
			getScreenDao().fetchMakerVerifierNames(pendingCollection.get(0));
		if(approvedCollection != null)
			getScreenDao().fetchMakerVerifierNames(approvedCollection.get(0));
		ArrayList<ReviewResultVb> lResult = new ArrayList<ReviewResultVb>();
		ReviewResultVb lVariable = new ReviewResultVb(rsb.getString("variable"),(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getVariable(),
				(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getVariable(), !(approvedCollection.get(0).getVariable().equals(pendingCollection.get(0).getVariable())));
		lResult.add(lVariable);
		ReviewResultVb lValue = new ReviewResultVb(rsb.getString("variableValue"),(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getValue(),
				(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getValue(), !(approvedCollection.get(0).getValue().equals(pendingCollection.get(0).getValue())));
		lResult.add(lValue);
		ReviewResultVb lCategory = new ReviewResultVb(rsb.getString("category"),(pendingCollection == null || pendingCollection.isEmpty())?"":getAtDescription((List<AlphaSubTabVb>) collTemp.get(2), pendingCollection.get(0).getVvCategory()),
				(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getVvCategory() == "-1"?"":getAtDescription((List<AlphaSubTabVb>) collTemp.get(2), approvedCollection.get(0).getVvCategory()), !(approvedCollection.get(0).getVvCategory().equals(pendingCollection.get(0).getVvCategory())));
		lResult.add(lCategory);
		ReviewResultVb lVariableStatus = new ReviewResultVb(rsb.getString("variableStatus"),(pendingCollection == null || pendingCollection.isEmpty())?"":getNtDescription((List<NumSubTabVb>) collTemp.get(0), pendingCollection.get(0).getVariableStatus()),
				(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getVariableStatus() == -1?"":getNtDescription((List<NumSubTabVb>) collTemp.get(0), approvedCollection.get(0).getVariableStatus()), !(approvedCollection.get(0).getVariableStatus() == pendingCollection.get(0).getVariableStatus()));
		lResult.add(lVariableStatus);
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
	@Override
	protected void setVerifReqDeleteType(VisionVariablesVb vObject){
		ArrayList<CommonVb> lCommVbList =(ArrayList<CommonVb>) getCommonDao().findVerificationRequiredAndStaticDelete("VISION_VARIABLES");
		vObject.setStaticDelete(lCommVbList.get(0).isStaticDelete());
		vObject.setVerificationRequired(lCommVbList.get(0).isVerificationRequired());
	}
	public VisionVariablesDao getVisionVariablesDao() {
		return visionVariablesDao;
	}
	public void setVisionVariablesDao(VisionVariablesDao visionVariablesDao) {
		this.visionVariablesDao = visionVariablesDao;
	}
	@Override
	protected void setAtNtValues(VisionVariablesVb vObject){
		vObject.setRecordIndicatorNt(7);
		vObject.setVariableStatusNt(1);
		vObject.setVvCategoryAt(8);
	}
	@Override
	protected AbstractDao<VisionVariablesVb> getScreenDao() {
		return visionVariablesDao;
	}
	public ExceptionCode doValidate(VisionVariablesVb vObject){
		ExceptionCode exceptionCode = new ExceptionCode();
		String operation = vObject.getActionType();
		String srtRestriction = getCommonDao().getRestrictionsByUsers("visionVariables", operation);
		if(!"Y".equalsIgnoreCase(srtRestriction)) {
			exceptionCode = new ExceptionCode();
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg(operation +" "+Constants.userRestrictionMsg);
			return exceptionCode;
		}
		exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
		return exceptionCode;
	}
	protected ExceptionCode doValidate(List<VisionVariablesVb> vObjects) {
		ExceptionCode exceptionCode = new ExceptionCode();
		VisionVariablesVb vObject = vObjects.get(0);
		String operation = vObject.getActionType();
		if (vObject.getActionType().equalsIgnoreCase("Add") || vObject.getActionType().equalsIgnoreCase("Modify")) {
			return checkAddModifyRestriction(vObjects, "visionVariables");
		} else {
			String srtRestriction = getCommonDao().getRestrictionsByUsers("visionVariables", operation);
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