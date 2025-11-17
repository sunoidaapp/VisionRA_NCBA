package com.vision.wb;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.vision.dao.AbstractDao;
import com.vision.dao.AlphaSubTabDao;
import com.vision.dao.CommonDao;
import com.vision.dao.NumSubTabDao;
import com.vision.dao.TaxConfigTierDao;
import com.vision.dao.TaxLineConfigDetailsDao;
import com.vision.dao.TaxLineConfigHeaderDao;
import com.vision.exception.ExceptionCode;
import com.vision.util.CommonUtils;
import com.vision.util.Constants;
import com.vision.vb.AlphaSubTabVb;
import com.vision.vb.CommonVb;
import com.vision.vb.ReviewResultVb;
import com.vision.vb.TaxConfigTierVb;
import com.vision.vb.TaxLineConfigDetailsVb;
import com.vision.vb.TaxLineConfigHeaderVb;
import com.vision.vb.TaxLinkVb;

@Component
public class TaxLineConfigHeaderWb extends AbstractDynaWorkerBean<TaxLineConfigHeaderVb> {
	@Autowired
	private TaxLineConfigHeaderDao taxLineConfigHeaderDao;
	@Autowired
	private TaxLineConfigDetailsDao taxLineConfigDetailsDao;
	@Autowired
	private TaxConfigTierDao taxLineConfigTierDao;

	@Autowired
	private NumSubTabDao numSubTabDao;
	@Autowired
	private AlphaSubTabDao alphaSubTabDao;
	@Autowired
	private CommonDao commonDao;

	public static Logger logger = LoggerFactory.getLogger(TaxLineConfigHeaderWb.class);

	public ArrayList getPageLoadValues() {
		List collTemp = null;
		ArrayList<Object> arrListLocal = new ArrayList<Object>();
		try {
			collTemp = numSubTabDao.findActiveNumSubTabsByNumTab(1);// status
			arrListLocal.add(collTemp);
			collTemp = numSubTabDao.findActiveNumSubTabsByNumTab(7);// record indicator
			arrListLocal.add(collTemp);
			collTemp = alphaSubTabDao.findActiveAlphaSubTabsByAlphaTab(7030); // Tax Currency
			arrListLocal.add(collTemp);
			collTemp = alphaSubTabDao.findActiveAlphaSubTabsByAlphaTab(7032);// Tax Basis
			arrListLocal.add(collTemp);
			collTemp = alphaSubTabDao.findActiveAlphaSubTabsByAlphaTab(7033);// Tax Charge Type
			arrListLocal.add(collTemp);
			collTemp = alphaSubTabDao.findActiveAlphaSubTabsByAlphaTab(7034);// Tier Type
			arrListLocal.add(collTemp);
			collTemp = commonDao.getVisionBusinessDate();
			arrListLocal.add(collTemp);
			collTemp = alphaSubTabDao.findActiveAlphaSubTabsByAlphaTab(7007);// posting Type
			arrListLocal.add(collTemp);
			collTemp = alphaSubTabDao.findActiveAlphaSubTabsByAlphaTab(7007);// posting Type
			arrListLocal.add(collTemp);
			collTemp = alphaSubTabDao.findActiveAlphaSubTabsByAlphaTab(7008);// MAtch Rule
			arrListLocal.add(collTemp);
			collTemp = alphaSubTabDao.findActiveAlphaSubTabsByAlphaTab(7071);// Lookup Amount Type
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
	protected AbstractDao<TaxLineConfigHeaderVb> getScreenDao() {
		return taxLineConfigHeaderDao;
	}

	@Override
	protected void setAtNtValues(TaxLineConfigHeaderVb vObject) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void setVerifReqDeleteType(TaxLineConfigHeaderVb vObject) {
		ArrayList<CommonVb> lCommVbList = (ArrayList<CommonVb>) commonDao
				.findVerificationRequiredAndStaticDelete("RA_MST_TAX_HEADER");
		vObject.setStaticDelete(lCommVbList.get(0).isStaticDelete());
		vObject.setVerificationRequired(lCommVbList.get(0).isVerificationRequired());
	}

	@SuppressWarnings("unused")
	@Override
	public ExceptionCode getQueryResults(TaxLineConfigHeaderVb vObject) {
		int intStatus = 1;
		ExceptionCode exceptionCode = new ExceptionCode();
		setVerifReqDeleteType(vObject);
		Boolean pendFlag = true;
		exceptionCode = doValidate(vObject);
		if(exceptionCode.getErrorCode() == Constants.ERRONEOUS_OPERATION){
			return exceptionCode;
		}	
		List<TaxLineConfigDetailsVb> taxLineConfigDetaillst = new ArrayList<TaxLineConfigDetailsVb>();
		List<TaxLinkVb> taxLinkDetaillst = new ArrayList<TaxLinkVb>();
		List<TaxLineConfigHeaderVb> collTemp = taxLineConfigHeaderDao.getQueryResults(vObject, intStatus);

		if (collTemp.size() == 0) {
			intStatus = 0;
			collTemp = taxLineConfigHeaderDao.getQueryResults(vObject, intStatus);
			pendFlag = false;
		}
		if (collTemp.size() == 0) {
			exceptionCode = CommonUtils.getResultObject(taxLineConfigHeaderDao.getServiceDesc(), 16, "Query", "");
			exceptionCode.setOtherInfo(vObject);
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg("No records found");
			return exceptionCode;
		} else {
			String taxType = collTemp.get(0).getTaxChargeType();
			if (!pendFlag) {
				taxLineConfigDetaillst = taxLineConfigDetailsDao.getTaxLineConfigDetails(vObject,
						Constants.STATUS_ZERO);
				taxLineConfigDetaillst.forEach(detailVb -> {
					detailVb.setVerificationRequired(vObject.isVerificationRequired());
					detailVb.setTaxType(taxType);
					List<TaxConfigTierVb> taxLineConfigTierlst = taxLineConfigTierDao.getTaxConfigTier(detailVb,
							Constants.STATUS_ZERO);
					detailVb.setTaxTierlst(taxLineConfigTierlst);
				});
			} else {
				taxLineConfigDetaillst = taxLineConfigDetailsDao.getTaxLineConfigDetails(vObject,
						Constants.STATUS_DELETE);
				for (TaxLineConfigDetailsVb detailVb : taxLineConfigDetaillst) {
					detailVb.setVerificationRequired(vObject.isVerificationRequired());
					detailVb.setTaxType(taxType);
					List<TaxConfigTierVb> taxLineConfigTierlst = taxLineConfigTierDao.getTaxConfigTier(detailVb,
							Constants.STATUS_DELETE);
					detailVb.setTaxTierlst(taxLineConfigTierlst);
				}
			}
			if (taxLineConfigDetaillst != null && !taxLineConfigDetaillst.isEmpty()) {
				collTemp.get(0).setTaxLineConfigDetaillst(taxLineConfigDetaillst);
			}
			TaxLinkVb taxLinkVb = new TaxLinkVb();
			taxLinkVb.setCountry(collTemp.get(0).getCountry());
			taxLinkVb.setLeBook(collTemp.get(0).getLeBook());
			taxLinkVb.setTaxLineId(collTemp.get(0).getTaxLineId());

			if (!pendFlag) {
				taxLinkDetaillst = taxLineConfigHeaderDao.getQueryTaxLink(taxLinkVb, Constants.STATUS_ZERO);

			} else {
				taxLinkDetaillst = taxLineConfigHeaderDao.getQueryTaxLink(taxLinkVb, Constants.STATUS_DELETE);
			}

			if (taxLinkDetaillst != null && !taxLinkDetaillst.isEmpty()) {
				collTemp.get(0).setTaxLinklst(taxLinkDetaillst);
			}
			exceptionCode.setOtherInfo(vObject);
			exceptionCode.setResponse(collTemp);
			exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
			exceptionCode.setErrorMsg("Successful operation");
			return exceptionCode;
		}
	}

	public ExceptionCode reviewTaxHeader(List<TaxLineConfigHeaderVb> approvedCollection,
			List<TaxLineConfigHeaderVb> pendingCollection) {
		ExceptionCode exceptioncode = new ExceptionCode();
		ArrayList<ReviewResultVb> lResult = new ArrayList<>();
		try {
			ArrayList collTemp = getPageLoadValues();
			if (pendingCollection != null)
				getScreenDao().fetchMakerVerifierNames(pendingCollection.get(0));
			if (approvedCollection != null)
				getScreenDao().fetchMakerVerifierNames(approvedCollection.get(0));

			ReviewResultVb lCountry = new ReviewResultVb("Country",
					(pendingCollection == null || pendingCollection.isEmpty()) ? ""
							: pendingCollection.get(0).getCountry(),
					(approvedCollection == null || approvedCollection.isEmpty()) ? ""
							: approvedCollection.get(0).getCountry(),
					(!pendingCollection.get(0).getCountry().equals(approvedCollection.get(0).getCountry())));
			lResult.add(lCountry);
			ReviewResultVb lLeBook = new ReviewResultVb("LeBook",
					(pendingCollection == null || pendingCollection.isEmpty()) ? ""
							: pendingCollection.get(0).getLeBook(),
					(approvedCollection == null || approvedCollection.isEmpty()) ? ""
							: approvedCollection.get(0).getLeBook(),
					(!pendingCollection.get(0).getLeBook().equals(approvedCollection.get(0).getLeBook())));
			lResult.add(lLeBook);
			ReviewResultVb taxLineId = new ReviewResultVb("TaxLine Id",
					(pendingCollection == null || pendingCollection.isEmpty()) ? ""
							: pendingCollection.get(0).getTaxLineId(),
					(approvedCollection == null || approvedCollection.isEmpty()) ? ""
							: approvedCollection.get(0).getTaxLineId(),
					(!pendingCollection.get(0).getTaxLineId().equals(approvedCollection.get(0).getTaxLineId())));
			lResult.add(taxLineId);
			ReviewResultVb taxLineIdDesc = new ReviewResultVb("TaxLine Description",
					(pendingCollection == null || pendingCollection.isEmpty()) ? ""
							: pendingCollection.get(0).getTaxLineDescription(),
					(approvedCollection == null || approvedCollection.isEmpty()) ? ""
							: approvedCollection.get(0).getTaxLineDescription(),
					(!pendingCollection.get(0).getTaxLineDescription()
							.equals(approvedCollection.get(0).getTaxLineDescription())));
			lResult.add(taxLineIdDesc);

			ReviewResultVb effDate = new ReviewResultVb("Effective Date ",
					(pendingCollection == null || pendingCollection.isEmpty()) ? ""
							: pendingCollection.get(0).getEffectiveDate(),
					(approvedCollection == null || approvedCollection.isEmpty()) ? ""
							: approvedCollection.get(0).getEffectiveDate(),
					(!pendingCollection.get(0).getEffectiveDate()
							.equals(approvedCollection.get(0).getEffectiveDate())));
			lResult.add(effDate);

			/*
			 * ReviewResultVb tierType = new ReviewResultVb("Tier Type", (pendingCollection
			 * == null || pendingCollection.isEmpty()) ? "" :
			 * getAtDescription((List<AlphaSubTabVb>) collTemp.get(5),
			 * pendingCollection.get(0).getTierType()), (approvedCollection == null ||
			 * approvedCollection.isEmpty()) ? "" : getAtDescription((List<AlphaSubTabVb>)
			 * collTemp.get(5), approvedCollection.get(0).getTierType()),
			 * (!pendingCollection.get(0).getTierType().equals(approvedCollection.get(0).
			 * getTierType()))); lResult.add(tierType);
			 */

			ReviewResultVb taxBasis = new ReviewResultVb("Tax Basis",
					(pendingCollection == null || pendingCollection.isEmpty()) ? ""
							: getAtDescription((List<AlphaSubTabVb>) collTemp.get(3),
									pendingCollection.get(0).getTaxBasis()),
					(approvedCollection == null || approvedCollection.isEmpty()) ? ""
							: getAtDescription((List<AlphaSubTabVb>) collTemp.get(3),
									approvedCollection.get(0).getTaxBasis()),
					(!pendingCollection.get(0).getTaxBasis().equals(approvedCollection.get(0).getTaxBasis())));
			lResult.add(taxBasis);
			if ("F".equalsIgnoreCase(approvedCollection.get(0).getTaxChargeType())
					&& "F".equalsIgnoreCase(pendingCollection.get(0).getTaxChargeType())) {
				ReviewResultVb chargeType = new ReviewResultVb("Charge Type",
						(pendingCollection == null || pendingCollection.isEmpty()) ? ""
								: getAtDescription((List<AlphaSubTabVb>) collTemp.get(4),
										pendingCollection.get(0).getTaxChargeType()),
						(approvedCollection == null || approvedCollection.isEmpty()) ? ""
								: getAtDescription((List<AlphaSubTabVb>) collTemp.get(4),
										approvedCollection.get(0).getTaxChargeType()),
						(!pendingCollection.get(0).getTaxChargeType()
								.equals(approvedCollection.get(0).getTaxChargeType())));
				lResult.add(chargeType);

			} else {
				ReviewResultVb chargeType = new ReviewResultVb("Charge Type",
						(pendingCollection == null || pendingCollection.isEmpty()) ? ""
								: getAtDescription((List<AlphaSubTabVb>) collTemp.get(4),
										pendingCollection.get(0).getTaxChargeType()),
						(approvedCollection == null || approvedCollection.isEmpty()) ? ""
								: getAtDescription((List<AlphaSubTabVb>) collTemp.get(4),
										approvedCollection.get(0).getTaxChargeType()),
						(!pendingCollection.get(0).getTaxChargeType()
								.equals(approvedCollection.get(0).getTaxChargeType())));
				lResult.add(chargeType);

				ReviewResultVb tierType = new ReviewResultVb("Tier Type",
						(pendingCollection == null || pendingCollection.isEmpty()) ? ""
								: getAtDescription((List<AlphaSubTabVb>) collTemp.get(5),
										pendingCollection.get(0).getTierType()),
						(approvedCollection == null || approvedCollection.isEmpty()) ? ""
								: getAtDescription((List<AlphaSubTabVb>) collTemp.get(5),
										approvedCollection.get(0).getTierType()),
						(!pendingCollection.get(0).getTierType().equals(approvedCollection.get(0).getTierType())));
				lResult.add(tierType);
			}
			ReviewResultVb taxCcy = new ReviewResultVb("Tax Currency",
					(pendingCollection == null || pendingCollection.isEmpty()) ? ""
							: getAtDescription((List<AlphaSubTabVb>) collTemp.get(2),
									pendingCollection.get(0).getTaxCcy()),
					(approvedCollection == null || approvedCollection.isEmpty()) ? ""
							: getAtDescription((List<AlphaSubTabVb>) collTemp.get(2),
									approvedCollection.get(0).getTaxCcy()),
					(!pendingCollection.get(0).getTaxCcy().equals(approvedCollection.get(0).getTaxCcy())));
			lResult.add(taxCcy);

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
							: pendingCollection.get(0).getTaxLineStatusDesc(),
					(approvedCollection == null || approvedCollection.isEmpty()) ? ""
							: approvedCollection.get(0).getTaxLineStatusDesc(),
							(!pendingCollection.get(0).getTaxLineStatusDesc().equals(approvedCollection.get(0)
							.getTaxLineStatusDesc())));
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
							: pendingCollection.get(0).getVerifier() == 0 ? ""
									: pendingCollection.get(0).getVerifierName(),
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
			if (lResult != null && !lResult.isEmpty()) {
				exceptioncode.setResponse(lResult);
				exceptioncode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
			}
		} catch (Exception e) {
			exceptioncode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptioncode.setErrorMsg(e.getMessage());
		}
		return exceptioncode;
	}

	public ExceptionCode reviewRecordTaxLineHeader(TaxLineConfigHeaderVb vObject) {
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			List<TaxLineConfigHeaderVb> approvedCollection = getScreenDao().getQueryResults(vObject, 0);
			List<TaxLineConfigHeaderVb> pendingCollection = getScreenDao().getQueryResults(vObject, 1);
			exceptionCode = reviewTaxHeader(approvedCollection, pendingCollection);
		} catch (Exception ex) {
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg(ex.getMessage());
		}
		return exceptionCode;
	}
	protected ExceptionCode doValidate(List<TaxLineConfigHeaderVb> vObjects) {
		ExceptionCode exceptionCode = new ExceptionCode();
		TaxLineConfigHeaderVb vObject = vObjects.get(0);
		String operation = vObject.getActionType();
		String srtRestrion = getCommonDao().getRestrictionsByUsers("TaxLineConfig", operation);
		if(!"Y".equalsIgnoreCase(srtRestrion)) {
			exceptionCode = new ExceptionCode();
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg(operation +" "+Constants.userRestrictionMsg);
			return exceptionCode;
		}
		exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
		return exceptionCode;
	
	}
	protected ExceptionCode doValidate(TaxLineConfigHeaderVb vObject){
		ExceptionCode exceptionCode = new ExceptionCode();
		String operation = vObject.getActionType();
		String srtRestrion = getCommonDao().getRestrictionsByUsers("TaxLineConfig", operation);
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