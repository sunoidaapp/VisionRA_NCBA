package com.vision.wb;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.vision.dao.AbstractDao;
import com.vision.dao.AlphaSubTabDao;
import com.vision.dao.BlReconRuleDao;
import com.vision.dao.BusinessLineConfigDao;
import com.vision.dao.BusinessLineConfigGLDao;
import com.vision.dao.CommonDao;
import com.vision.dao.FeesConfigHeadersDao;
import com.vision.dao.NumSubTabDao;
import com.vision.dao.TaxBlReconRuleDao;
import com.vision.exception.ExceptionCode;
import com.vision.exception.RuntimeCustomException;
import com.vision.util.CommonUtils;
import com.vision.util.Constants;
import com.vision.util.DeepCopy;
import com.vision.util.ValidationUtil;
import com.vision.vb.BlReconRuleVb;
import com.vision.vb.BusinessLineGLVb;
import com.vision.vb.BusinessLineHeaderVb;
import com.vision.vb.CommonVb;
import com.vision.vb.NumSubTabVb;
import com.vision.vb.ReviewResultVb;
import com.vision.vb.TransLineHeaderVb;

@Component
public class BusinessLineConfigWb extends AbstractDynaWorkerBean<BusinessLineHeaderVb> {

	private final ValidationUtil validationUtil;
	@Autowired
	private BusinessLineConfigDao businessLineConfigDao;
	@Autowired
	private BusinessLineConfigGLDao businessLineConfigGLDao;
	@Autowired
	private NumSubTabDao numSubTabDao;
	@Autowired
	private AlphaSubTabDao alphaSubTabDao;
	@Autowired
	private CommonDao commonDao;
	@Autowired
	private FeesConfigHeadersDao feesConfigHeadersDao;
	@Autowired
	private BlReconRuleDao blReconRuleDao;
	@Autowired
	private TaxBlReconRuleDao taxBlReconRuleDao;

	public static Logger logger = LoggerFactory.getLogger(BusinessLineConfigWb.class);

	BusinessLineConfigWb(ValidationUtil validationUtil) {
		this.validationUtil = validationUtil;
	}

	public ArrayList getPageLoadValues() {
		List collTemp = null;
		ArrayList<Object> arrListLocal = new ArrayList<Object>();
		try {
			collTemp = numSubTabDao.findActiveNumSubTabsByNumTab(1);
			arrListLocal.add(collTemp);
			collTemp = numSubTabDao.findActiveNumSubTabsByNumTab(7);
			arrListLocal.add(collTemp);
			collTemp = alphaSubTabDao.findActiveAlphaSubTabsByAlphaTab(7075);
			arrListLocal.add(collTemp);
			collTemp = alphaSubTabDao.findActiveAlphaSubTabsByAlphaTab(7076);
			arrListLocal.add(collTemp);
			collTemp = alphaSubTabDao.findActiveAlphaSubTabsByAlphaTab(7008);
			arrListLocal.add(collTemp);
			arrListLocal.add(("Y".equalsIgnoreCase(commonDao.getRestrictionsByScreen("TaxLineConfig"))
					&& "Y".equalsIgnoreCase(ValidationUtil.isValid(commonDao.findVisionVariableValue("TAX_RECON_RULE"))
							? commonDao.findVisionVariableValue("TAX_RECON_RULE")
							: "N")) ? "Y" : "N");

			collTemp = alphaSubTabDao.findActiveAlphaSubTabsByAlphaTab(7080);
			arrListLocal.add(collTemp);
			return arrListLocal;
		} catch (Exception ex) {
			ex.printStackTrace();
			// logger.error("Exception in getting the Page load values.", ex);
			return null;
		}
	}

	@Override
	public ExceptionCode getQueryResults(BusinessLineHeaderVb vObject) {
		int intStatus = 1;
		BusinessLineHeaderVb businessLineHeaderVb = new BusinessLineHeaderVb();
		ExceptionCode exceptionCode = new ExceptionCode();
		setVerifReqDeleteType(vObject);
		exceptionCode = doValidate(vObject);
		if (exceptionCode.getErrorCode() == Constants.ERRONEOUS_OPERATION) {
			return exceptionCode;
		}
		Boolean pendFlag = true;
		List<BusinessLineGLVb> businessGlList = new ArrayList<BusinessLineGLVb>();
		List<BlReconRuleVb> blReconRulelst = new ArrayList<BlReconRuleVb>();
		List<BlReconRuleVb> taxBlReconRulelst = new ArrayList<BlReconRuleVb>();
		String taxRecon = ValidationUtil.isValid(commonDao.findVisionVariableValue("TAX_RECON_RULE"))
				? commonDao.findVisionVariableValue("TAX_RECON_RULE")
				: "N";
		List<BusinessLineHeaderVb> collTemp = businessLineConfigDao.getQueryResults(vObject, intStatus);
		if (collTemp.size() == 0) {
			intStatus = 0;
			collTemp = businessLineConfigDao.getQueryResults(vObject, intStatus);
			pendFlag = false;
		}
		if (collTemp.size() == 0) {
			exceptionCode = CommonUtils.getResultObject(businessLineConfigDao.getServiceDesc(), 16, "Query", "");
			exceptionCode.setOtherInfo(vObject);
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg("No records found");
			return exceptionCode;
		} else {
			for (BusinessLineHeaderVb dObj : collTemp) {
				if (dObj.getRecordIndicator() == 3) {
					pendFlag = false;
				}
				if (!pendFlag) {
					businessGlList = businessLineConfigGLDao.getBusinessGLDetails(vObject, Constants.STATUS_ZERO);
					blReconRulelst = blReconRuleDao.getBlReconRuleDetails(vObject, Constants.STATUS_ZERO);
					if (ValidationUtil.isValid(taxRecon) && "Y".equalsIgnoreCase(taxRecon))
						taxBlReconRulelst = taxBlReconRuleDao.getTaxBlReconRuleDetails(vObject, Constants.STATUS_ZERO);
				} else {
					businessGlList = businessLineConfigGLDao.getBusinessGLDetails(vObject, Constants.STATUS_DELETE);
					blReconRulelst = blReconRuleDao.getBlReconRuleDetails(vObject, Constants.STATUS_DELETE);
					if (ValidationUtil.isValid(taxRecon) && "Y".equalsIgnoreCase(taxRecon))
						taxBlReconRulelst = taxBlReconRuleDao.getTaxBlReconRuleDetails(vObject,
								Constants.STATUS_DELETE);
				}
				if (businessGlList != null && !businessGlList.isEmpty()) {
					dObj.setBusinessLineGllst(businessGlList);
				}
				if (blReconRulelst != null && !blReconRulelst.isEmpty()) {
					dObj.setBlReconRulelst(blReconRulelst);
				}
				if (taxBlReconRulelst != null && !taxBlReconRulelst.isEmpty()) {
					dObj.setTaxBlReconRulelst(taxBlReconRulelst);
				}
			}
			exceptionCode.setOtherInfo(vObject);
			exceptionCode.setResponse(collTemp);
			exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
			exceptionCode.setErrorMsg("Successful operation");
			return exceptionCode;
		}
	}

	@Override
	protected AbstractDao<BusinessLineHeaderVb> getScreenDao() {
		return businessLineConfigDao;
	}

	@Override
	protected void setAtNtValues(BusinessLineHeaderVb vObject) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void setVerifReqDeleteType(BusinessLineHeaderVb vObject) {
		ArrayList<CommonVb> lCommVbList = (ArrayList<CommonVb>) commonDao
				.findVerificationRequiredAndStaticDelete("RA_MST_TRANS_LINE_HEADER");
		vObject.setStaticDelete(lCommVbList.get(0).isStaticDelete());
		vObject.setVerificationRequired(lCommVbList.get(0).isVerificationRequired());
	}

	@Override
	public ExceptionCode insertRecord(BusinessLineHeaderVb vObject) {
		ExceptionCode exceptionCode = null;
		DeepCopy<BusinessLineHeaderVb> deepCopy = new DeepCopy<BusinessLineHeaderVb>();
		BusinessLineHeaderVb clonedObject = null;

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
			TransLineHeaderVb transLineHeaderVb = new TransLineHeaderVb();
			transLineHeaderVb.setCountry(vObject.getCountry());
			transLineHeaderVb.setLeBook(vObject.getLeBook());
			transLineHeaderVb.setTransLineId(vObject.getTransLineId());
			List<TransLineHeaderVb> collTemp = businessLineConfigDao.doselectproductRecord(transLineHeaderVb);
			if (collTemp != null && collTemp.size() > 0) {
				TransLineHeaderVb transLineVb = collTemp.get(0);
				if (transLineVb.getTransLineStatus() == Constants.PASSIVATE) {
					exceptionCode = new ExceptionCode();
					exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
					exceptionCode.setErrorMsg("Trans Line [" + transLineHeaderVb.getTransLineId()
							+ "] is  Deleted - Kindly Active the Record");
					tranFlag = false;

				}
			}
			if (tranFlag) {
				if (!vObject.isVerificationRequired()) {
					exceptionCode = getScreenDao().doInsertApprRecord(vObject);
					feesConfigHeadersDao.callLineMergeProc(vObject.getCountry(), vObject.getLeBook(),
							vObject.getBusinessLineId(), "PR_RA_MST_BUSINESS_LINE");
				} else {
					exceptionCode = getScreenDao().doInsertRecord(vObject);
				}
			}
			getScreenDao().fetchMakerVerifierNames(vObject);
			exceptionCode.setOtherInfo(vObject);
			return exceptionCode;
		} catch (RuntimeCustomException rex) {
			logger.error("Insert Exception " + rex.getCode().getErrorMsg());
//			//logger.error( ((vObject==null)? "vObject is Null":vObject.toString()));
			exceptionCode = rex.getCode();
			exceptionCode.setOtherInfo(clonedObject);
			return exceptionCode;
		}
	}

	@Override
	public ExceptionCode modifyRecord(BusinessLineHeaderVb vObject) {
		ExceptionCode exceptionCode = null;
		DeepCopy<BusinessLineHeaderVb> deepCopy = new DeepCopy<BusinessLineHeaderVb>();
		BusinessLineHeaderVb clonedObject = null;
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
			TransLineHeaderVb transLineHeaderVb = new TransLineHeaderVb();
			transLineHeaderVb.setCountry(vObject.getCountry());
			transLineHeaderVb.setLeBook(vObject.getLeBook());
			transLineHeaderVb.setTransLineId(vObject.getTransLineId());
			List<TransLineHeaderVb> collTemp = businessLineConfigDao.doselectproductRecord(transLineHeaderVb);
			if (collTemp != null && collTemp.size() > 0) {
				TransLineHeaderVb transLineVb = collTemp.get(0);
				if (transLineVb.getTransLineStatus() == Constants.PASSIVATE) {
					exceptionCode = new ExceptionCode();
					exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
					exceptionCode.setErrorMsg("Trans Line [" + transLineHeaderVb.getTransLineId()
							+ "] is  Deleted - Kindly Active the Record");
					tranFlag = false;

				}
			}
			if (tranFlag) {
				if (!vObject.isVerificationRequired()) {
					exceptionCode = getScreenDao().doUpdateApprRecord(vObject);
					feesConfigHeadersDao.callLineMergeProc(vObject.getCountry(), vObject.getLeBook(),
							vObject.getBusinessLineId(), "PR_RA_MST_BUSINESS_LINE");
					feesConfigHeadersDao.callLineMergeProc(vObject.getCountry(), vObject.getLeBook(),
							vObject.getBusinessLineId(), "PR_RA_MST_FEE_LINE");
				} else {
					exceptionCode = getScreenDao().doUpdateRecord(vObject);
				}
			}
			getScreenDao().fetchMakerVerifierNames(vObject);
			exceptionCode.setOtherInfo(vObject);
			return exceptionCode;
		} catch (RuntimeCustomException rex) {
			logger.error("Modify Exception " + rex.getCode().getErrorMsg());
//			//logger.error( ((vObject==null)? "vObject is Null":vObject.toString()));
			exceptionCode = rex.getCode();
			exceptionCode.setOtherInfo(clonedObject);
			return exceptionCode;
		}
	}

	@Override
	public ExceptionCode approve(BusinessLineHeaderVb vObject) {
		ExceptionCode exceptionCode = null;
		DeepCopy<BusinessLineHeaderVb> deepCopy = new DeepCopy<BusinessLineHeaderVb>();
		BusinessLineHeaderVb clonedObject = null;
		try {
//			exceptionCode = doValidate(vObject);
			setVerifReqDeleteType(vObject);
			clonedObject = deepCopy.copy(vObject);
			exceptionCode = doValidate(vObject);
			if (exceptionCode.getErrorCode() == Constants.ERRONEOUS_OPERATION) {
				return exceptionCode;
			}
			exceptionCode = getScreenDao().doApproveForTransaction(vObject, vObject.isStaticDelete());
			feesConfigHeadersDao.callLineMergeProc(vObject.getCountry(), vObject.getLeBook(),
					vObject.getBusinessLineId(), "PR_RA_MST_BUSINESS_LINE");
			feesConfigHeadersDao.callLineMergeProc(vObject.getCountry(), vObject.getLeBook(),
					vObject.getBusinessLineId(), "PR_RA_MST_FEE_LINE");
			getScreenDao().fetchMakerVerifierNames(vObject);
			exceptionCode.setOtherInfo(vObject);
			return exceptionCode;
		} catch (RuntimeCustomException rex) {
			logger.error("Approve Exception " + rex.getCode().getErrorMsg());
			exceptionCode = rex.getCode();
			exceptionCode.setOtherInfo(clonedObject);
			return exceptionCode;
		}
	}

	@Override
	public ExceptionCode getAllQueryPopupResult(BusinessLineHeaderVb queryPopupObj) {
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			setVerifReqDeleteType(queryPopupObj);
			doFormateDataForQuery(queryPopupObj);
			exceptionCode = doValidate(queryPopupObj);
			if (exceptionCode.getErrorCode() == Constants.ERRONEOUS_OPERATION) {
				return exceptionCode;
			}
			List<BusinessLineHeaderVb> arrListResult = businessLineConfigDao.getQueryPopupResults(queryPopupObj);
			if (arrListResult != null && arrListResult.size() > 0) {
				exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
				exceptionCode.setResponse(arrListResult);
			} else {
				exceptionCode = CommonUtils.getResultObject(getScreenDao().getServiceDesc(), Constants.NO_RECORDS_FOUND,
						"Query", "");
			}
			exceptionCode.setOtherInfo(queryPopupObj);
		} catch (Exception ex) {
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg(ex.getMessage());
			ex.printStackTrace();
			// logger.error("Exception in getting the getAllQueryPopupResult results.", ex);
		}
		return exceptionCode;
	}

	@Override
	protected List<ReviewResultVb> transformToReviewResults(List<BusinessLineHeaderVb> approvedCollection,
			List<BusinessLineHeaderVb> pendingCollection) {
		ArrayList collTemp = getPageLoadValues();
		if (pendingCollection != null)
			getScreenDao().fetchMakerVerifierNames(pendingCollection.get(0));
		if (approvedCollection != null)
			getScreenDao().fetchMakerVerifierNames(approvedCollection.get(0));

		ArrayList<ReviewResultVb> lResult = new ArrayList<ReviewResultVb>();

		ReviewResultVb busLineId = new ReviewResultVb("Business Line Id",
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: pendingCollection.get(0).getBusinessLineId(),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: approvedCollection.get(0).getBusinessLineId(),
				(!pendingCollection.get(0).getBusinessLineId().equals(approvedCollection.get(0).getBusinessLineId())));
		lResult.add(busLineId);
		ReviewResultVb busLineDesc = new ReviewResultVb("Business Line Decription",
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: pendingCollection.get(0).getBusinessLineDescription(),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: approvedCollection.get(0).getBusinessLineDescription(),
				(!pendingCollection.get(0).getBusinessLineDescription()
						.equals(approvedCollection.get(0).getBusinessLineDescription())));
		lResult.add(busLineDesc);

		ReviewResultVb busLineType = new ReviewResultVb("Business Line Type",
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: pendingCollection.get(0).getBusinessLineTypeDesc(),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: approvedCollection.get(0).getBusinessLineTypeDesc(),
				(!pendingCollection.get(0).getBusinessLineTypeDesc()
						.equals(approvedCollection.get(0).getBusinessLineTypeDesc())));
		lResult.add(busLineType);

		ReviewResultVb incExpType = new ReviewResultVb("Income / Expenses Type",
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: pendingCollection.get(0).getIncomeExpenseTypeDesc(),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: approvedCollection.get(0).getIncomeExpenseTypeDesc(),
				(!pendingCollection.get(0).getIncomeExpenseTypeDesc()
						.equals(approvedCollection.get(0).getIncomeExpenseTypeDesc())));
		lResult.add(incExpType);

		/*
		 * ReviewResultVb postingType = new
		 * ReviewResultVb("Posting Type",(pendingCollection == null ||
		 * pendingCollection.isEmpty())?"":pendingCollection.get(0).
		 * getActualIePostingDesc(), (approvedCollection == null ||
		 * approvedCollection.isEmpty())?"":approvedCollection.get(0).
		 * getActualIePostingDesc(),
		 * (!pendingCollection.get(0).getActualIePostingDesc().equals(approvedCollection
		 * .get(0).getActualIePostingDesc()))); lResult.add(postingType);
		 */

		/*
		 * ReviewResultVb matchRule = new ReviewResultVb("Match Rule",(pendingCollection
		 * == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).
		 * getActualIeMatchRuleDesc(), (approvedCollection == null ||
		 * approvedCollection.isEmpty())?"":approvedCollection.get(0).
		 * getActualIeMatchRuleDesc(),
		 * (!pendingCollection.get(0).getActualIeMatchRuleDesc().equals(
		 * approvedCollection.get(0).getActualIeMatchRuleDesc())));
		 * lResult.add(matchRule);
		 */

		ReviewResultVb timeStampFlag = new ReviewResultVb("FeeCalc TimeStamp Flag",
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: pendingCollection.get(0).getFeeCalcTimeStampFlag(),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: approvedCollection.get(0).getFeeCalcTimeStampFlag(),
				(!pendingCollection.get(0).getFeeCalcTimeStampFlag()
						.equals(approvedCollection.get(0).getFeeCalcTimeStampFlag())));
		lResult.add(timeStampFlag);

		ReviewResultVb lPrrofileStatus = new ReviewResultVb("Status",
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: getNtDescription((List<NumSubTabVb>) collTemp.get(0),
								pendingCollection.get(0).getBusinessLineStatus()),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: getNtDescription((List<NumSubTabVb>) collTemp.get(0),
								approvedCollection.get(0).getBusinessLineStatus()),
				(pendingCollection.get(0).getBusinessLineStatus() != approvedCollection.get(0)
						.getBusinessLineStatus()));
		lResult.add(lPrrofileStatus);

		ReviewResultVb lRecordIndicator = new ReviewResultVb("Record Indicator",
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: getNtDescription((List<NumSubTabVb>) collTemp.get(1),
								pendingCollection.get(0).getRecordIndicator()),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: getNtDescription((List<NumSubTabVb>) collTemp.get(1),
								approvedCollection.get(0).getRecordIndicator()),
				(pendingCollection.get(0).getRecordIndicator() != approvedCollection.get(0).getRecordIndicator()));
		lResult.add(lRecordIndicator);

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

	public ExceptionCode doValidate(BusinessLineHeaderVb vObject) {
		ExceptionCode exceptionCode = new ExceptionCode();
		String operation = vObject.getActionType();
		String srtRestriction = getCommonDao().getRestrictionsByUsers("TransLineConfig", operation);
		if (!"Y".equalsIgnoreCase(srtRestriction)) {
			exceptionCode = new ExceptionCode();
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg(operation + " " + Constants.userRestrictionMsg);
			return exceptionCode;
		}
		exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
		return exceptionCode;
	}
}
