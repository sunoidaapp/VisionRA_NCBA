package com.vision.wb;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.vision.dao.AbstractDao;
import com.vision.dao.AlphaSubTabDao;
import com.vision.dao.CommonDao;
import com.vision.dao.FeesConfigDetailsDao;
import com.vision.dao.FeesConfigHeadersDao;
import com.vision.dao.FeesConfigTierDao;
import com.vision.dao.NumSubTabDao;
import com.vision.exception.ExceptionCode;
import com.vision.exception.RuntimeCustomException;
import com.vision.util.CommonUtils;
import com.vision.util.Constants;
import com.vision.util.DeepCopy;
import com.vision.util.ValidationUtil;
import com.vision.vb.BusinessLineHeaderVb;
import com.vision.vb.CommonVb;
import com.vision.vb.FeesConfigDetailsVb;
import com.vision.vb.FeesConfigHeaderVb;
import com.vision.vb.FeesConfigTierVb;
import com.vision.vb.ReviewResultVb;
import com.vision.vb.TransLineHeaderVb;
@Component
public class FeesConfigHeadersWb extends AbstractDynaWorkerBean<FeesConfigHeaderVb> {
	@Autowired
	private FeesConfigHeadersDao feesConfigHeadersDao;
	@Autowired
	private FeesConfigDetailsDao feesConfigDetailsDao;
	@Autowired
	private FeesConfigTierDao feesConfigTierDao;
	
	@Autowired
	private NumSubTabDao numSubTabDao;
	@Autowired
	private AlphaSubTabDao alphaSubTabDao;
	@Autowired
	private CommonDao commonDao;
	
	public static Logger logger = LoggerFactory.getLogger(FeesConfigHeadersWb.class);
	
	public ArrayList getPageLoadValues(){
		List collTemp = null;
		ArrayList<Object> arrListLocal = new ArrayList<Object>();
		try{
			collTemp = numSubTabDao.findActiveNumSubTabsByNumTab(1);//status
			arrListLocal.add(collTemp);
			collTemp = numSubTabDao.findActiveNumSubTabsByNumTab(7);//record indicator
			arrListLocal.add(collTemp);
			collTemp= commonDao.getVisionBusinessDate();
			arrListLocal.add(collTemp);
			collTemp = alphaSubTabDao.findActiveAlphaSubTabsByAlphaTab(7071);
			arrListLocal.add(collTemp);
			collTemp = alphaSubTabDao.findActiveAlphaSubTabsByAlphaTab(7072); // Fees Detail - Round Off Basis
			arrListLocal.add(collTemp);
			collTemp = alphaSubTabDao.findActiveAlphaSubTabsByAlphaTab(7074); // Fees Detail - Mix Max Ccy Type
			arrListLocal.add(collTemp);
			collTemp= commonDao.getCurrencyDecimals(false);
			arrListLocal.add(collTemp);
			collTemp= commonDao.getCurrencyDecimals(true);
			arrListLocal.add(collTemp);
			String decimalCccy = commonDao.findVisionVariableValue("RA_DECIMAL_CCY");
			arrListLocal.add(decimalCccy);
			collTemp = alphaSubTabDao.findActiveAlphaSubTabsByAlphaTab(7087); // Pricing
			arrListLocal.add(collTemp);
			return arrListLocal;
		}catch(Exception ex){
			ex.printStackTrace();
			//logger.error("Exception in getting the Page load values.", ex);
			return null;
		}
	}

	@Override
	protected AbstractDao<FeesConfigHeaderVb> getScreenDao() {
		return feesConfigHeadersDao;
	}
	@Override
	protected void setAtNtValues(FeesConfigHeaderVb vObject) {
		// TODO Auto-generated method stub
		
	}
	@Override
	protected void setVerifReqDeleteType(FeesConfigHeaderVb vObject) {
		ArrayList<CommonVb> lCommVbList =(ArrayList<CommonVb>) commonDao.findVerificationRequiredAndStaticDelete("RA_MST_TRANS_LINE_HEADER");
		vObject.setStaticDelete(lCommVbList.get(0).isStaticDelete());
		vObject.setVerificationRequired(lCommVbList.get(0).isVerificationRequired());
	}
	@Override
	public ExceptionCode getQueryResults(FeesConfigHeaderVb vObject){
		int intStatus = 1;
		ExceptionCode exceptionCode = new ExceptionCode();
		setVerifReqDeleteType(vObject);
		List<FeesConfigDetailsVb> feesConfigDetaillst = new ArrayList<FeesConfigDetailsVb>();
		exceptionCode = doValidate(vObject);
		if(exceptionCode.getErrorCode() == Constants.ERRONEOUS_OPERATION){
			return exceptionCode;
		}
		List<FeesConfigHeaderVb> collTemp = feesConfigHeadersDao.getQueryResults(vObject,intStatus);
		if (collTemp.size() == 0){
			intStatus = 0;
			collTemp = feesConfigHeadersDao.getQueryResults(vObject,intStatus);
		}
		if(collTemp.size() == 0){
			exceptionCode = CommonUtils.getResultObject(getScreenDao().getServiceDesc(), 16, "Query", "");
			exceptionCode.setOtherInfo(vObject);
			return exceptionCode;
		}else{
			for(FeesConfigHeaderVb feeConfigHeaderVb : collTemp) {
				String feeType = feeConfigHeaderVb.getFeeType();
				if(feeConfigHeaderVb.getRecordIndicator() == 3)
					intStatus = 0;
				feeConfigHeaderVb.setVerificationRequired(vObject.isVerificationRequired());
				feeConfigHeaderVb.setPricing(vObject.getPricing());
				feesConfigDetaillst = feesConfigDetailsDao.getAllQueryforFeeDetails(feeConfigHeaderVb,intStatus);
				int tierFetchStatus = intStatus;
				feesConfigDetaillst.forEach(detailVb -> {
					detailVb.setVerificationRequired(vObject.isVerificationRequired());
					detailVb.setFeeType(feeType);
					List<FeesConfigTierVb> feesConfigTierlst = feesConfigTierDao.getQueryResults(detailVb,detailVb.getRecordIndicator());
					detailVb.setFeesTierlst(feesConfigTierlst);
				});
				if(feesConfigDetaillst != null && !feesConfigDetaillst.isEmpty()) {
					feeConfigHeaderVb.setFeesConfigDetaillst(feesConfigDetaillst);
				}
			}
			exceptionCode.setOtherInfo(vObject);
			exceptionCode.setResponse(collTemp);
			exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
			exceptionCode.setErrorMsg("Successful operation");
			return exceptionCode;
		}
	}

	/*
	 * public ExceptionCode insertRecord(FeesConfigHeaderVb vObject){ ExceptionCode
	 * exceptionCode = null; DeepCopy<FeesConfigHeaderVb> deepCopy = new
	 * DeepCopy<FeesConfigHeaderVb>(); FeesConfigHeaderVb clonedObject = null; try {
	 * setAtNtValues(vObject); setVerifReqDeleteType(vObject); clonedObject =
	 * deepCopy.copy(vObject); doFormateData(vObject); exceptionCode =
	 * doValidate(vObject); if(exceptionCode!=null &&
	 * exceptionCode.getErrorMsg()!=""){ return exceptionCode; }
	 * if(!vObject.isVerificationRequired()){ exceptionCode =
	 * getScreenDao().doInsertApprRecord(vObject);
	 * feesConfigHeadersDao.callLineMergeProc(vObject.getCountry(),vObject.getLeBook
	 * (),vObject.getBusinessLineId(),"PR_RA_MST_FEE_LINE"); }else{ exceptionCode =
	 * getScreenDao().doInsertRecord(vObject); }
	 * getScreenDao().fetchMakerVerifierNames(vObject);
	 * exceptionCode.setOtherInfo(vObject); return exceptionCode;
	 * }catch(RuntimeCustomException rex){ //logger.error("Insert Exception " +
	 * rex.getCode().getErrorMsg()); //logger.error( ((vObject==null)?
	 * "vObject is Null":vObject.toString())); exceptionCode = rex.getCode();
	 * exceptionCode.setOtherInfo(clonedObject); return exceptionCode; } }
	 */
	/*@Override
	public ExceptionCode modifyRecord(FeesConfigHeaderVb vObject){
		ExceptionCode exceptionCode  = null;
		DeepCopy<FeesConfigHeaderVb> deepCopy = new DeepCopy<FeesConfigHeaderVb>();
		FeesConfigHeaderVb clonedObject = null;
		try{
			setAtNtValues(vObject);
			setVerifReqDeleteType(vObject);
			clonedObject = deepCopy.copy(vObject);
			doFormateData(vObject);
			exceptionCode = doValidate(vObject);
			if(exceptionCode!=null && exceptionCode.getErrorMsg()!=""){
				return exceptionCode;
			}
			if(!vObject.isVerificationRequired()){
				exceptionCode = getScreenDao().doUpdateApprRecord(vObject);
				feesConfigHeadersDao.callLineMergeProc(vObject.getCountry(),vObject.getLeBook(),vObject.getBusinessLineId(),"PR_RA_MST_FEE_LINE");
			}else{
				exceptionCode = getScreenDao().doUpdateRecord(vObject);
			}
			getScreenDao().fetchMakerVerifierNames(vObject);
			exceptionCode.setOtherInfo(vObject);
			return exceptionCode;
		}catch(RuntimeCustomException rex){
			//logger.error("Modify Exception " + rex.getCode().getErrorMsg());
			//logger.error( ((vObject==null)? "vObject is Null":vObject.toString()));
			exceptionCode = rex.getCode();
			exceptionCode.setOtherInfo(clonedObject);
			return exceptionCode;
		}
	}
	@Override*/
	public ExceptionCode insertRecord(FeesConfigHeaderVb vObject) {
		ExceptionCode exceptionCode = null;
		DeepCopy<FeesConfigHeaderVb> deepCopy = new DeepCopy<FeesConfigHeaderVb>();
		FeesConfigHeaderVb clonedObject = null;
		try {
			setAtNtValues(vObject);
			setVerifReqDeleteType(vObject);
			clonedObject = deepCopy.copy(vObject);
			doFormateData(vObject);
			exceptionCode = doValidate(vObject);
			if (exceptionCode != null && exceptionCode.getErrorMsg() != "") {
				return exceptionCode;
			}
			boolean tranFlag = true;
			List<BusinessLineHeaderVb> collTempBL = null;
			List<TransLineHeaderVb> collTemp = null;
			TransLineHeaderVb transLineHeaderVb = new TransLineHeaderVb();
			BusinessLineHeaderVb businessLineHeaderVb = new BusinessLineHeaderVb();
			transLineHeaderVb.setCountry(vObject.getCountry());
			transLineHeaderVb.setLeBook(vObject.getLeBook());
			transLineHeaderVb.setTransLineId(vObject.getTransLineId());
			collTemp = feesConfigHeadersDao.getTransLineRecords(transLineHeaderVb);
			if (collTemp != null && collTemp.size() > 0) {
				TransLineHeaderVb transLineVb = collTemp.get(0);
				if (transLineVb.getTransLineStatus() == Constants.PASSIVATE) {
					exceptionCode = new ExceptionCode();
					exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
					exceptionCode.setErrorMsg("Trans Line [" + transLineHeaderVb.getTransLineId()+ "] is  Deleted - Kindly Active the Record");
					tranFlag = false;
				}
			}
			businessLineHeaderVb.setCountry(vObject.getCountry());
			businessLineHeaderVb.setLeBook(vObject.getLeBook());
			businessLineHeaderVb.setTransLineId(vObject.getTransLineId());
			businessLineHeaderVb.setBusinessLineId(vObject.getBusinessLineId());

			collTempBL = feesConfigHeadersDao.doSelectBusinessLineRecord(businessLineHeaderVb);
			if (collTempBL != null && collTempBL.size() > 0) {
				for (BusinessLineHeaderVb businessLineVb : collTempBL) {
					if (businessLineVb.getBusinessLineStatus() == Constants.PASSIVATE) {
						exceptionCode = new ExceptionCode();
						exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
						exceptionCode.setErrorMsg("Business Line [" + businessLineHeaderVb.getBusinessLineId()+ "] is  Deleted - Kindly Active the Record");
						tranFlag = false;
						break; // Breaks both loops
					}
				}
			}

			if (tranFlag) {
				if (!vObject.isVerificationRequired()) {
					exceptionCode = getScreenDao().doInsertApprRecord(vObject);
				} else {
					exceptionCode = getScreenDao().doInsertRecord(vObject);
				}
			}
			getScreenDao().fetchMakerVerifierNames(vObject);
			exceptionCode.setOtherInfo(vObject);
			return exceptionCode;
		} catch (RuntimeCustomException rex) {
			// logger.error("Insert Exception " + rex.getCode().getErrorMsg());
			// logger.error( ((vObject==null)? "vObject is Null":vObject.toString()));
			exceptionCode = rex.getCode();
			exceptionCode.setOtherInfo(clonedObject);
			return exceptionCode;
		}
	}

	/**
	 * updates the existing record in the approved or pending tables depending on
	 * Verification Required flag of the object.
	 * 
	 * @param vObject
	 * @return
	 */
	public ExceptionCode modifyRecord(FeesConfigHeaderVb vObject) {
		ExceptionCode exceptionCode = null;
		DeepCopy<FeesConfigHeaderVb> deepCopy = new DeepCopy<FeesConfigHeaderVb>();
		FeesConfigHeaderVb clonedObject = null;
		try {
			setAtNtValues(vObject);
			setVerifReqDeleteType(vObject);
			clonedObject = deepCopy.copy(vObject);
			doFormateData(vObject);
			exceptionCode = doValidate(vObject);
			if (exceptionCode != null && exceptionCode.getErrorMsg() != "") {
				return exceptionCode;
			}
			boolean tranFlag = true;
			List<BusinessLineHeaderVb> collTempBL = null;
			List<TransLineHeaderVb> collTemp = null;
			TransLineHeaderVb transLineHeaderVb = new TransLineHeaderVb();
			BusinessLineHeaderVb businessLineHeaderVb = new BusinessLineHeaderVb();
			transLineHeaderVb.setCountry(vObject.getCountry());
			transLineHeaderVb.setLeBook(vObject.getLeBook());
			transLineHeaderVb.setTransLineId(vObject.getTransLineId());
			collTemp = feesConfigHeadersDao.getTransLineRecords(transLineHeaderVb);
			if (collTemp != null && collTemp.size() > 0) {
				TransLineHeaderVb transLineVb = collTemp.get(0);
				if (transLineVb.getTransLineStatus() == Constants.PASSIVATE) {
					exceptionCode = new ExceptionCode();
					exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
					exceptionCode.setErrorMsg("Trans Line [" + transLineHeaderVb.getTransLineId()+ "] is  Deleted - Kindly Active the Record");
					tranFlag = false;
				}
			}
			businessLineHeaderVb.setCountry(vObject.getCountry());
			businessLineHeaderVb.setLeBook(vObject.getLeBook());
			businessLineHeaderVb.setTransLineId(vObject.getTransLineId());
			businessLineHeaderVb.setBusinessLineId(vObject.getBusinessLineId());

			collTempBL = feesConfigHeadersDao.doSelectBusinessLineRecord(businessLineHeaderVb);
			if (collTempBL != null && collTempBL.size() > 0) {
				for (BusinessLineHeaderVb businessLineVb : collTempBL) {
					if (businessLineVb.getBusinessLineStatus() == Constants.PASSIVATE) {
						exceptionCode = new ExceptionCode();
						exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
						exceptionCode.setErrorMsg("Business Line [" + businessLineHeaderVb.getBusinessLineId()+ "] is  Deleted - Kindly Active the Record");
						tranFlag = false;
						break; // Breaks both loops
					}
				}
			}
			if (tranFlag) {
				if (!vObject.isVerificationRequired()) {
					exceptionCode = getScreenDao().doUpdateApprRecord(vObject);
				} else {
					exceptionCode = getScreenDao().doUpdateRecord(vObject);
				}
			}
			getScreenDao().fetchMakerVerifierNames(vObject);
			exceptionCode.setOtherInfo(vObject);
			return exceptionCode;
		} catch (RuntimeCustomException rex) {
			// logger.error("Modify Exception " + rex.getCode().getErrorMsg());
			// logger.error( ((vObject==null)? "vObject is Null":vObject.toString()));
			exceptionCode = rex.getCode();
			exceptionCode.setOtherInfo(clonedObject);
			return exceptionCode;
		}
	}

	@Override
	public ExceptionCode approve(FeesConfigHeaderVb vObject){
		ExceptionCode exceptionCode  = null;
		DeepCopy<FeesConfigHeaderVb> deepCopy = new DeepCopy<FeesConfigHeaderVb>();
		FeesConfigHeaderVb clonedObject = null;
		try{
			setVerifReqDeleteType(vObject);
			clonedObject = deepCopy.copy(vObject);
			exceptionCode = doValidate(vObject);
			if(exceptionCode.getErrorCode() == Constants.ERRONEOUS_OPERATION){
				return exceptionCode;
			}
			exceptionCode = getScreenDao().doApproveForTransaction(vObject,vObject.isStaticDelete());
			//Removed as Per Raj requested on 19Apr23
			//feesConfigHeadersDao.callLineMergeProc(vObject.getCountry(),vObject.getLeBook(),vObject.getBusinessLineId(),"PR_RA_MST_FEE_LINE");
			getScreenDao().fetchMakerVerifierNames(vObject);
			exceptionCode.setOtherInfo(vObject);
			return exceptionCode;
		}catch(RuntimeCustomException rex){
			//logger.error("Approve Exception " + rex.getCode().getErrorMsg());
			exceptionCode = rex.getCode();
			exceptionCode.setOtherInfo(clonedObject);
			return exceptionCode;
		}
	}
	@Override
	protected List<ReviewResultVb> transformToReviewResults(List<FeesConfigHeaderVb> approvedCollection, List<FeesConfigHeaderVb> pendingCollection) {
		ArrayList collTemp = getPageLoadValues();
		if(pendingCollection != null)
			getScreenDao().fetchMakerVerifierNames(pendingCollection.get(0));
		if(approvedCollection != null)
			getScreenDao().fetchMakerVerifierNames(approvedCollection.get(0));
			
		ArrayList<ReviewResultVb> lResult = new ArrayList<ReviewResultVb>();
		
		ReviewResultVb effectiveDate = new ReviewResultVb("Effective Date",(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getEffectiveDate(),
				(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getEffectiveDate(),
						(!pendingCollection.get(0).getEffectiveDate().equals(approvedCollection.get(0).getEffectiveDate())));
		lResult.add(effectiveDate);
	
		ReviewResultVb incExpType = new ReviewResultVb("Fee Basis",(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getFeeBasisDesc(),
				(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getFeeBasisDesc(),
						(!pendingCollection.get(0).getFeeBasisDesc().equals(approvedCollection.get(0).getFeeBasisDesc())));
		lResult.add(incExpType);
		if ("F".equalsIgnoreCase(approvedCollection.get(0).getFeeType())
				&& "F".equalsIgnoreCase(pendingCollection.get(0).getFeeType())) {
			ReviewResultVb feeTypeDesc = new ReviewResultVb("Fee Type",
					(pendingCollection == null || pendingCollection.isEmpty()) ? ""
							: pendingCollection.get(0).getFeeTypeDesc(),
					(approvedCollection == null || approvedCollection.isEmpty()) ? ""
							: approvedCollection.get(0).getFeeTypeDesc(),
					(!pendingCollection.get(0).getFeeTypeDesc().equals(approvedCollection.get(0).getFeeTypeDesc())));
			lResult.add(feeTypeDesc);

		} else {
			ReviewResultVb feeTypeDesc = new ReviewResultVb("Fee Type",
					(pendingCollection == null || pendingCollection.isEmpty()) ? ""
							: pendingCollection.get(0).getFeeTypeDesc(),
					(approvedCollection == null || approvedCollection.isEmpty()) ? ""
							: approvedCollection.get(0).getFeeTypeDesc(),
					(!pendingCollection.get(0).getFeeTypeDesc().equals(approvedCollection.get(0).getFeeTypeDesc())));
			lResult.add(feeTypeDesc);
			
			String tierTypePend= "";
			String tierTypeAppr= "";
			if(pendingCollection != null || !pendingCollection.isEmpty())
				tierTypePend = ValidationUtil.isValid(pendingCollection.get(0).getTierTypeDesc()) ? pendingCollection.get(0).getTierTypeDesc() : "";
				
			if(approvedCollection != null || !approvedCollection.isEmpty())
				tierTypeAppr = ValidationUtil.isValid(approvedCollection.get(0).getTierTypeDesc()) ? approvedCollection.get(0).getTierTypeDesc() : "";
				
			ReviewResultVb tierTypeDesc = new ReviewResultVb("Tier/Range Type",
					tierTypePend,tierTypeAppr,					
					(!tierTypePend.equals(tierTypeAppr)));
			lResult.add(tierTypeDesc);
		}

		ReviewResultVb lRecordIndicator = new ReviewResultVb("Record Indicator",
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: pendingCollection.get(0).getRecordIndicatorDesc() == null ? ""
								: pendingCollection.get(0).getRecordIndicatorDesc(),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: approvedCollection.get(0).getRecordIndicatorDesc() == null ? ""
								: approvedCollection.get(0).getRecordIndicatorDesc(),
				(pendingCollection.get(0).getRecordIndicatorDesc() != approvedCollection.get(0)
						.getRecordIndicatorDesc()));
		lResult.add(lRecordIndicator);

		ReviewResultVb status = new ReviewResultVb("Record Status",
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: pendingCollection.get(0).getFeesLineStatusDesc(),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: approvedCollection.get(0).getFeesLineStatusDesc(),
				(!pendingCollection.get(0).getFeesLineStatusDesc()
						.equals(approvedCollection.get(0).getFeesLineStatusDesc())));
		lResult.add(status);

		ReviewResultVb lMaker = new ReviewResultVb("Maker",(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getMaker() == 0?"":pendingCollection.get(0).getMakerName(),
				(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getMaker() == 0?"":approvedCollection.get(0).getMakerName(),
						(pendingCollection.get(0).getMaker() != approvedCollection.get(0).getMaker()));
		lResult.add(lMaker);
		ReviewResultVb lVerifier = new ReviewResultVb("Verifier",(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getVerifier() == 0?"":pendingCollection.get(0).getVerifierName(),
				(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getVerifier() == 0?"":approvedCollection.get(0).getVerifierName(),
						(pendingCollection.get(0).getVerifier() != approvedCollection.get(0).getVerifier()));
		lResult.add(lVerifier);
		ReviewResultVb lDateLastModified = new ReviewResultVb("Date Last Modified",(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getDateLastModified(),
				(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getDateLastModified(),
						(!pendingCollection.get(0).getDateLastModified().equals(approvedCollection.get(0).getDateLastModified())));
		lResult.add(lDateLastModified);
		ReviewResultVb lDateCreation = new ReviewResultVb("Date Creation",(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getDateCreation(),
				(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getDateCreation(),
						(!pendingCollection.get(0).getDateCreation().equals(approvedCollection.get(0).getDateCreation())));
		lResult.add(lDateCreation);
		return lResult;
	}
	public ExceptionCode doValidate(FeesConfigHeaderVb vObject){
		ExceptionCode exceptionCode = new ExceptionCode();
		String operation = vObject.getActionType();
		if(!ValidationUtil.isValid(vObject.getTierType())) {
			vObject.setTierType("NA");
		}
		String srtRestriction = getCommonDao().getRestrictionsByUsers("TransLineConfig", operation);
		if(!"Y".equalsIgnoreCase(srtRestriction)) {
			exceptionCode = new ExceptionCode();
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg(operation +" "+Constants.userRestrictionMsg);
			return exceptionCode;
		}
		exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
		return exceptionCode;
	}
	public ExceptionCode getQueryResultsHistory(FeesConfigHeaderVb vObject){
		int intStatus = 1;
		ExceptionCode exceptionCode = new ExceptionCode();
		exceptionCode = doValidate(vObject);
		if(exceptionCode.getErrorCode() == Constants.ERRONEOUS_OPERATION){
			return exceptionCode;
		}
		if(vObject.getFeesConfigDetaillst() == null || vObject.getFeesConfigDetaillst().size() == 0) {
			exceptionCode.setOtherInfo(vObject);
			exceptionCode.setErrorCode(Constants.NO_RECORDS_FOUND);
			exceptionCode.setErrorMsg("No History data found");
			return exceptionCode;
		}
		FeesConfigDetailsVb detailVb = vObject.getFeesConfigDetaillst().get(0);
		setVerifReqDeleteType(vObject);
		detailVb.setVerificationRequired(vObject.isVerificationRequired());
		List<FeesConfigHeaderVb> collTemp = new ArrayList<FeesConfigHeaderVb>();
		List<FeesConfigDetailsVb> feesConfigDetaillst = new ArrayList<FeesConfigDetailsVb>();
		
		feesConfigDetaillst = feesConfigDetailsDao.getFeesConfigDetailsHistory(detailVb);
		
		feesConfigDetaillst.forEach(dObj -> {
			detailVb.setVerificationRequired(vObject.isVerificationRequired());
			List<FeesConfigTierVb> feesConfigTierlst = feesConfigTierDao.getQueryResults(dObj,dObj.getRecordIndicator());
			dObj.setFeesTierlst(feesConfigTierlst);
		});
		if(feesConfigDetaillst != null && !feesConfigDetaillst.isEmpty()) {
			vObject.setFeesConfigDetaillst(feesConfigDetaillst);
		}
		collTemp.add(vObject);
		exceptionCode.setOtherInfo(vObject);
		exceptionCode.setResponse(collTemp);
		exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
		exceptionCode.setErrorMsg("Successful operation");
		return exceptionCode;
}

	public List<ReviewResultVb> reviewDetailRecord(FeesConfigDetailsVb vObject){
		try{
			List<FeesConfigDetailsVb> approvedCollection = feesConfigDetailsDao.getQueryResultsForReview(vObject,0);
			List<FeesConfigDetailsVb> pendingCollection = feesConfigDetailsDao.getQueryResultsForReview(vObject,1);
			
			/*List<FeesConfigTierVb> approvedTierCollection = feesConfigTierDao.getQueryResultsForReview(vObject,0);
			List<FeesConfigTierVb> pendingTierCollection = feesConfigTierDao.getQueryResultsForReview(vObject,1);
			*/
			return feesConfigDetailsDao.transformToReviewResults(approvedCollection,pendingCollection,vObject);
		}catch(Exception ex){
			return null;
		}
	}
	public ExceptionCode getAllQueryforFeeDetails(FeesConfigDetailsVb vObject){
		ExceptionCode exceptionCode = new ExceptionCode();
		int intStatus = 0;
		try{
			FeesConfigHeaderVb feesConfigHeaderVb = new FeesConfigHeaderVb();
			feesConfigHeaderVb.setActionType(vObject.getActionType());
			exceptionCode = doValidate(feesConfigHeaderVb);
			if(exceptionCode.getErrorCode() == Constants.ERRONEOUS_OPERATION){
				return exceptionCode;
			}
			if(vObject.getRecordIndicator() == Constants.STATUS_ZERO)
				intStatus = 0;
			else
				intStatus = 1;
			vObject.setVerificationRequired(false);
			List<FeesConfigDetailsVb> feesConfigDetaillst = null;
					//feesConfigDetailsDao.getAllQueryforFeeDetails(vObject,intStatus);
			if(feesConfigDetaillst!= null && feesConfigDetaillst.size() > 0) {
				int tierStatus = intStatus;
				feesConfigDetaillst.forEach(detailVb -> {
					detailVb.setVerificationRequired(vObject.isVerificationRequired());
					//detailVb.setFeeType(feeType);
					List<FeesConfigTierVb> feesConfigTierlst = feesConfigTierDao.getQueryResults(detailVb,tierStatus);
					detailVb.setFeesTierlst(feesConfigTierlst);
				});
				exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
				exceptionCode.setResponse(feesConfigDetaillst);
			}else {
				exceptionCode.setResponse(new ArrayList());
				exceptionCode = CommonUtils.getResultObject(getScreenDao().getServiceDesc(), Constants.NO_RECORDS_FOUND, "Query", "");
			}
			exceptionCode.setOtherInfo(vObject);
			return exceptionCode;
		}catch(Exception ex){
			ex.printStackTrace();
			//logger.error("Exception in getting the getAllQueryPopupResult results.", ex);
			return null;
		}
	}
	public ExceptionCode getFeeConfigDataDictionary(FeesConfigDetailsVb dObj) {
		ExceptionCode exceptionCode = new ExceptionCode();
		LinkedHashMap<String,String> feesConfigPriorityMap = new LinkedHashMap<String,String>();
		try {
			FeesConfigHeaderVb feesConfigHeaderVb = new FeesConfigHeaderVb();
			feesConfigHeaderVb.setActionType(dObj.getActionType());
			exceptionCode = doValidate(feesConfigHeaderVb);
			if(exceptionCode.getErrorCode() == Constants.ERRONEOUS_OPERATION){
				return exceptionCode;
			}
			feesConfigPriorityMap = (LinkedHashMap<String, String>) feesConfigDetailsDao.getFeesConfigPriority(dObj);
			exceptionCode.setResponse(feesConfigPriorityMap);
			exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
			return exceptionCode;
		} catch (Exception ex) {
			exceptionCode.setResponse(feesConfigPriorityMap);
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg(ex.getMessage());
			return exceptionCode;
		}
	}
}
