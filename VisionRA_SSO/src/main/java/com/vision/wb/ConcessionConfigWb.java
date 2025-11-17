package com.vision.wb;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.vision.dao.AbstractDao;
import com.vision.dao.AlphaSubTabDao;
import com.vision.dao.CommonDao;
import com.vision.dao.ConcessionConfigDetailsDao;
import com.vision.dao.ConcessionConfigTierDao;
import com.vision.dao.ConcessionFilterDao;
import com.vision.dao.ConcessionHeaderDao;
import com.vision.dao.NumSubTabDao;
import com.vision.exception.ExceptionCode;
import com.vision.exception.RuntimeCustomException;
import com.vision.util.CommonUtils;
import com.vision.util.Constants;
import com.vision.util.DeepCopy;
import com.vision.util.ValidationUtil;
import com.vision.vb.CommonVb;
import com.vision.vb.ConcessionConfigDetailsVb;
import com.vision.vb.ConcessionConfigHeaderVb;
import com.vision.vb.ConcessionConfigTierVb;
import com.vision.vb.ConcessionFilterVb;
import com.vision.vb.ReviewResultVb;

@Component
public class ConcessionConfigWb extends AbstractDynaWorkerBean<ConcessionConfigHeaderVb> {
	@Autowired
	private ConcessionHeaderDao concessionConfigHeadersDao;
	@Autowired
	private ConcessionConfigDetailsDao concessionConfigDetailsDao;
	@Autowired
	private ConcessionFilterDao concessionFilterDao;
	@Autowired
	private ConcessionConfigTierDao concessionConfigTierDao;

	@Autowired
	private NumSubTabDao numSubTabDao;
	@Autowired
	private AlphaSubTabDao alphaSubTabDao;
	@Autowired
	private CommonDao commonDao;

	public static Logger logger = LoggerFactory.getLogger(ConcessionConfigWb.class);

	public ArrayList getPageLoadValues() {
		List collTemp = null;
		ArrayList<Object> arrListLocal = new ArrayList<Object>();
		try {
			collTemp = numSubTabDao.findActiveNumSubTabsByNumTab(1);// status
			arrListLocal.add(collTemp);
			collTemp = numSubTabDao.findActiveNumSubTabsByNumTab(7);// record indicator
			arrListLocal.add(collTemp);
			collTemp = commonDao.getVisionBusinessDate();
			arrListLocal.add(collTemp);
			collTemp = alphaSubTabDao.findActiveAlphaSubTabsByAlphaTab(7071);
			arrListLocal.add(collTemp);
			collTemp = alphaSubTabDao.findActiveAlphaSubTabsByAlphaTab(7072); // Fees Detail - Round Off Basis
			arrListLocal.add(collTemp);
			collTemp = alphaSubTabDao.findActiveAlphaSubTabsByAlphaTab(7074); // Fees Detail - Mix Max Ccy Type
			arrListLocal.add(collTemp);
			collTemp = alphaSubTabDao.findActiveAlphaSubTabsByAlphaTab(7401); // condition operation
			arrListLocal.add(collTemp);
			collTemp = concessionConfigHeadersDao.findSourceTableNames();
			arrListLocal.add(collTemp);
			String country = commonDao.findVisionVariableValue("DEFAULT_COUNTRY");
			arrListLocal.add(country);
			String leBook = commonDao.findVisionVariableValue("DEFAULT_LE_BOOK");
			arrListLocal.add(leBook);

			return arrListLocal;
		} catch (Exception ex) {
			ex.printStackTrace();
			//logger.error("Exception in getting the Page load values.", ex);
			return null;
		}
	}

	@Override
	protected AbstractDao<ConcessionConfigHeaderVb> getScreenDao() {
		return concessionConfigHeadersDao;
	}

	@Override
	protected void setAtNtValues(ConcessionConfigHeaderVb vObject) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void setVerifReqDeleteType(ConcessionConfigHeaderVb vObject) {
		ArrayList<CommonVb> lCommVbList = (ArrayList<CommonVb>) commonDao
				.findVerificationRequiredAndStaticDelete("RA_MST_CONCESSION_HEADER");
		vObject.setStaticDelete(lCommVbList.get(0).isStaticDelete());
		vObject.setVerificationRequired(lCommVbList.get(0).isVerificationRequired());
	}

	@Override
	public ExceptionCode getQueryResults(ConcessionConfigHeaderVb vObject) {
		int intStatus = 1;
		ExceptionCode exceptionCode = new ExceptionCode();
		setVerifReqDeleteType(vObject);
		List<ConcessionConfigDetailsVb> concessionConfigDetaillst = new ArrayList<ConcessionConfigDetailsVb>();
		List<ConcessionFilterVb> concessionConfigFilterlst = new ArrayList<ConcessionFilterVb>();
		List<ConcessionConfigHeaderVb> concessionActivitylst = new ArrayList<ConcessionConfigHeaderVb>();

		List<ConcessionConfigHeaderVb> collTemp = concessionConfigHeadersDao.getQueryResults(vObject, intStatus);
		if (collTemp.size() == 0) {
			intStatus = 0;
			collTemp = concessionConfigHeadersDao.getQueryResults(vObject, intStatus);
		}
		if (collTemp.size() == 0) {
			exceptionCode = CommonUtils.getResultObject(getScreenDao().getServiceDesc(), 16, "Query", "");
			exceptionCode.setOtherInfo(vObject);
			return exceptionCode;
		} else {
			for (ConcessionConfigHeaderVb ConcessionConfigHeaderVb : collTemp) {
				String feeType = ConcessionConfigHeaderVb.getFeeType();
				concessionConfigDetaillst = concessionConfigDetailsDao
						.getConcessionConfigDetails(ConcessionConfigHeaderVb, intStatus);
				int tierFetchStatus = intStatus;
				concessionConfigDetaillst.forEach(detailVb -> {
					detailVb.setVerificationRequired(vObject.isVerificationRequired());
					detailVb.setFeeType(feeType);
					List<ConcessionConfigTierVb> concessionConfigTierlst = concessionConfigTierDao
							.getConcessionConfigTier(detailVb, tierFetchStatus);
					detailVb.setConcessionTierlst(concessionConfigTierlst);
				});
				if (concessionConfigDetaillst != null && !concessionConfigDetaillst.isEmpty()) {
					ConcessionConfigHeaderVb.setConcessionConfigDetaillst(concessionConfigDetaillst);
				}
				concessionConfigFilterlst = concessionFilterDao.getConcessionFilters(ConcessionConfigHeaderVb,
						intStatus);
				if (concessionConfigFilterlst != null && !concessionConfigFilterlst.isEmpty()) {
					ConcessionConfigHeaderVb.setConcessionFilterlst(concessionConfigFilterlst);
				}
				if ("Y".equalsIgnoreCase(ConcessionConfigHeaderVb.getActivityLinkFlag())) {
					concessionActivitylst = concessionConfigHeadersDao.getQueryActivityLink(ConcessionConfigHeaderVb,
							intStatus);
					if (concessionActivitylst != null && !concessionActivitylst.isEmpty()) {

						if (concessionActivitylst != null && concessionActivitylst.size() > 0) {
							StringJoiner ActArrJoiner = new StringJoiner(",");
							concessionActivitylst.forEach(ActLink -> {
								ActArrJoiner.add(ActLink.getActivityId());
							});
							collTemp.get(0).setActivityId(ActArrJoiner.toString());
						}
						ConcessionConfigHeaderVb.setConcessionActivitylst(concessionActivitylst);
					}

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
	 * public ExceptionCode insertRecord(ConcessionConfigHeaderVb vObject){
	 * ExceptionCode exceptionCode = null; DeepCopy<ConcessionConfigHeaderVb>
	 * deepCopy = new DeepCopy<ConcessionConfigHeaderVb>(); ConcessionConfigHeaderVb
	 * clonedObject = null; try { setAtNtValues(vObject);
	 * setVerifReqDeleteType(vObject); clonedObject = deepCopy.copy(vObject);
	 * doFormateData(vObject); exceptionCode = doValidate(vObject);
	 * if(exceptionCode!=null && exceptionCode.getErrorMsg()!=""){ return
	 * exceptionCode; } if(!vObject.isVerificationRequired()){ exceptionCode =
	 * getScreenDao().doInsertApprRecord(vObject);
	 * concessionConfigHeadersDao.callLineMergeProc(vObject.getCountry(),vObject.
	 * getLeBook (),vObject.getBusinessLineId(),"PR_RA_MST_FEE_LINE"); }else{
	 * exceptionCode = getScreenDao().doInsertRecord(vObject); }
	 * getScreenDao().fetchMakerVerifierNames(vObject);
	 * exceptionCode.setOtherInfo(vObject); return exceptionCode;
	 * }catch(RuntimeCustomException rex){ //logger.error("Insert Exception " +
	 * rex.getCode().getErrorMsg()); //logger.error( ((vObject==null)?
	 * "vObject is Null":vObject.toString())); exceptionCode = rex.getCode();
	 * exceptionCode.setOtherInfo(clonedObject); return exceptionCode; } }
	 */
	/*
	 * @Override public ExceptionCode modifyRecord(ConcessionConfigHeaderVb
	 * vObject){ ExceptionCode exceptionCode = null;
	 * DeepCopy<ConcessionConfigHeaderVb> deepCopy = new
	 * DeepCopy<ConcessionConfigHeaderVb>(); ConcessionConfigHeaderVb clonedObject =
	 * null; try{ setAtNtValues(vObject); setVerifReqDeleteType(vObject);
	 * clonedObject = deepCopy.copy(vObject); doFormateData(vObject); exceptionCode
	 * = doValidate(vObject); if(exceptionCode!=null &&
	 * exceptionCode.getErrorMsg()!=""){ return exceptionCode; }
	 * if(!vObject.isVerificationRequired()){ exceptionCode =
	 * getScreenDao().doUpdateApprRecord(vObject);
	 * concessionConfigHeadersDao.callLineMergeProc(vObject.getCountry(),vObject.
	 * getLeBook(),vObject.getBusinessLineId(),"PR_RA_MST_FEE_LINE"); }else{
	 * exceptionCode = getScreenDao().doUpdateRecord(vObject); }
	 * getScreenDao().fetchMakerVerifierNames(vObject);
	 * exceptionCode.setOtherInfo(vObject); return exceptionCode;
	 * }catch(RuntimeCustomException rex){ //logger.error("Modify Exception " +
	 * rex.getCode().getErrorMsg()); //logger.error( ((vObject==null)?
	 * "vObject is Null":vObject.toString())); exceptionCode = rex.getCode();
	 * exceptionCode.setOtherInfo(clonedObject); return exceptionCode; } }
	 * 
	 * @Override
	 */
	@Override
	public ExceptionCode approve(ConcessionConfigHeaderVb vObject) {
		ExceptionCode exceptionCode = null;
		DeepCopy<ConcessionConfigHeaderVb> deepCopy = new DeepCopy<ConcessionConfigHeaderVb>();
		ConcessionConfigHeaderVb clonedObject = null;
		try {
			setVerifReqDeleteType(vObject);
			clonedObject = deepCopy.copy(vObject);
			exceptionCode = doValidate(vObject);
			if(exceptionCode.getErrorCode() == Constants.ERRONEOUS_OPERATION){
				return exceptionCode;
			}
			exceptionCode = getScreenDao().doApproveForTransaction(vObject, vObject.isStaticDelete());
			concessionConfigHeadersDao.callLineMergeProc(vObject.getCountry(), vObject.getLeBook(),
					vObject.getConcessionId(), "PR_RA_MST_FEE_LINE");
			getScreenDao().fetchMakerVerifierNames(vObject);
			exceptionCode.setOtherInfo(vObject);
			return exceptionCode;
		} catch (RuntimeCustomException rex) {
			//logger.error("Approve Exception " + rex.getCode().getErrorMsg());
			exceptionCode = rex.getCode();
			exceptionCode.setOtherInfo(clonedObject);
			return exceptionCode;
		}
	}

	@Override
	protected List<ReviewResultVb> transformToReviewResults(List<ConcessionConfigHeaderVb> approvedCollection,
			List<ConcessionConfigHeaderVb> pendingCollection) {
		ArrayList collTemp = getPageLoadValues();
		if (pendingCollection != null)
			getScreenDao().fetchMakerVerifierNames(pendingCollection.get(0));
		if (approvedCollection != null)
			getScreenDao().fetchMakerVerifierNames(approvedCollection.get(0));

		ArrayList<ReviewResultVb> lResult = new ArrayList<ReviewResultVb>();

		ReviewResultVb lCountry = new ReviewResultVb("Country",
				(pendingCollection == null || pendingCollection.isEmpty()) ? "" : pendingCollection.get(0).getCountry(),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: approvedCollection.get(0).getCountry(),
				(!pendingCollection.get(0).getCountry().equals(approvedCollection.get(0).getCountry())));
		lResult.add(lCountry);

		ReviewResultVb lLeBook = new ReviewResultVb("LeBook",
				(pendingCollection == null || pendingCollection.isEmpty()) ? "" : pendingCollection.get(0).getLeBook(),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: approvedCollection.get(0).getLeBook(),
				(!pendingCollection.get(0).getLeBook().equals(approvedCollection.get(0).getLeBook())));
		lResult.add(lLeBook);

		ReviewResultVb concessionId = new ReviewResultVb("Concession Id",
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: pendingCollection.get(0).getConcessionId(),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: approvedCollection.get(0).getConcessionId(),
				(!pendingCollection.get(0).getConcessionId().equals(approvedCollection.get(0).getConcessionId())));
		lResult.add(concessionId);

		ReviewResultVb concessionDesc = new ReviewResultVb("Concession Description",
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: pendingCollection.get(0).getConcessionDesc(),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: approvedCollection.get(0).getConcessionDesc(),
				(!pendingCollection.get(0).getConcessionDesc().equals(approvedCollection.get(0).getConcessionDesc())));
		lResult.add(concessionDesc);

		ReviewResultVb effectiveDate = new ReviewResultVb("Effective Date",
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: pendingCollection.get(0).getEffectiveDate(),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: approvedCollection.get(0).getEffectiveDate(),
				(!pendingCollection.get(0).getEffectiveDate().equals(approvedCollection.get(0).getEffectiveDate())));
		lResult.add(effectiveDate);

		ReviewResultVb endDate = new ReviewResultVb("End Date",
				(pendingCollection == null || pendingCollection.isEmpty()) ? "" : pendingCollection.get(0).getEndDate(),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: approvedCollection.get(0).getEndDate(),
				(!pendingCollection.get(0).getEndDate().equals(approvedCollection.get(0).getEndDate())));
		lResult.add(endDate);

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
						: pendingCollection.get(0).getConcessionStatusDesc(),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: approvedCollection.get(0).getConcessionStatusDesc(),
				(!pendingCollection.get(0).getConcessionStatusDesc()
						.equals(approvedCollection.get(0).getConcessionStatusDesc())));
		lResult.add(status);

		ReviewResultVb lMaker = new ReviewResultVb("Maker",
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: pendingCollection.get(0).getMaker() == 0 ? "" : pendingCollection.get(0).getMakerName(),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: approvedCollection.get(0).getMaker() == 0 ? "" : approvedCollection.get(0).getMakerName(),
				(pendingCollection.get(0).getMaker() != approvedCollection.get(0).getMaker()));
		lResult.add(lMaker);
		ReviewResultVb lVerifier = new ReviewResultVb("Verifier",
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: pendingCollection.get(0).getVerifier() == 0 ? "" : pendingCollection.get(0).getVerifierName(),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: approvedCollection.get(0).getVerifier() == 0 ? ""
								: approvedCollection.get(0).getVerifierName(),
				(pendingCollection.get(0).getVerifier() != approvedCollection.get(0).getVerifier()));
		lResult.add(lVerifier);
		ReviewResultVb lDateLastModified = new ReviewResultVb("Date Last Modified",
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: pendingCollection.get(0).getDateLastModified(),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: approvedCollection.get(0).getDateLastModified(),
				(!pendingCollection.get(0).getDateLastModified()
						.equals(approvedCollection.get(0).getDateLastModified())));
		lResult.add(lDateLastModified);
		ReviewResultVb lDateCreation = new ReviewResultVb("Date Creation",
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: pendingCollection.get(0).getDateCreation(),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: approvedCollection.get(0).getDateCreation(),
				(!pendingCollection.get(0).getDateCreation().equals(approvedCollection.get(0).getDateCreation())));
		lResult.add(lDateCreation);
		return lResult;
	}
	
	protected ExceptionCode doValidate(List<ConcessionConfigHeaderVb> vObjects) {
		ExceptionCode exceptionCode = new ExceptionCode();
		ConcessionConfigHeaderVb vObject = vObjects.get(0);
		String operation = vObject.getActionType();
		String srtRestrion = getCommonDao().getRestrictionsByUsers("concessionConfig", operation);
		if(!"Y".equalsIgnoreCase(srtRestrion)) {
			exceptionCode = new ExceptionCode();
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg(operation +" "+Constants.userRestrictionMsg);
			return exceptionCode;
		}
		exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
		return exceptionCode;
	
	}
	
	protected ExceptionCode doValidate(ConcessionConfigHeaderVb vObject){
		ExceptionCode exceptionCode = new ExceptionCode();
		String operation = vObject.getActionType();
		//System.out.println(operation);
		String srtRestrion = getCommonDao().getRestrictionsByUsers("concessionConfig", operation);
		if(!"Y".equalsIgnoreCase(srtRestrion)) {
			exceptionCode = new ExceptionCode();
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg(operation +" "+Constants.userRestrictionMsg);
			return exceptionCode;
		}
		exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
		return exceptionCode;
	}
}
