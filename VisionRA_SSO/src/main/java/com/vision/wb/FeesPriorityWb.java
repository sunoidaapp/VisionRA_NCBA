package com.vision.wb;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.vision.dao.AbstractDao;
import com.vision.dao.CommonDao;
import com.vision.dao.FeesPriorityDao;
import com.vision.exception.ExceptionCode;
import com.vision.exception.RuntimeCustomException;
import com.vision.util.CommonUtils;
import com.vision.util.Constants;
import com.vision.util.DeepCopy;
import com.vision.vb.CommonVb;
import com.vision.vb.FeesPriorityVb;
import com.vision.vb.ReviewResultVb;
@Controller
public class FeesPriorityWb extends AbstractDynaWorkerBean<FeesPriorityVb>{

	@Autowired 
	private FeesPriorityDao feesPriorityDao;
	@Autowired
	CommonDao commonDao;
	
	public static Logger logger = LoggerFactory.getLogger(FeesPriorityWb.class);
	
	public ArrayList getPageLoadValues(){
		List collTemp = null;
		ArrayList<Object> arrListLocal = new ArrayList<Object>();
		try {
			collTemp = getNumSubTabDao().findActiveNumSubTabsByNumTab(1);
			arrListLocal.add(collTemp);
			collTemp = getNumSubTabDao().findActiveNumSubTabsByNumTab(7);
			arrListLocal.add(collTemp);
			List<String> dimensionsLst = feesPriorityDao.getPriorityDimensions();
			arrListLocal.add(dimensionsLst);
			String defCountry = commonDao.findVisionVariableValue("DEFAULT_COUNTRY");
			arrListLocal.add(defCountry);
			String defLeBook = commonDao.findVisionVariableValue("DEFAULT_LE_BOOK");
			arrListLocal.add(defLeBook);
			String defBusLineId = feesPriorityDao.getDefaultBusLineId(defCountry,defLeBook);
			arrListLocal.add(defBusLineId);
			collTemp = getCommonDao().findVerificationRequiredAndStaticDelete("RA_MST_FEES_DEFAULT_PRIORITY");
			arrListLocal.add(collTemp);
			return arrListLocal;
		} catch(Exception ex){
			//ex.printStackTrace();
			//logger.error("Exception in getting the Page load values.", ex);
			return null;
		}
	}
	public ExceptionCode getQueryResults(FeesPriorityVb queryPopupObj){
		ExceptionCode exceptionCode = new ExceptionCode();
		try{
			setVerifReqDeleteType(queryPopupObj);
			doFormateDataForQuery(queryPopupObj);
			exceptionCode = doValidate(queryPopupObj);
			if(exceptionCode.getErrorCode() == Constants.ERRONEOUS_OPERATION){
				return exceptionCode;
			}
			queryPopupObj.setNavigate("Y");
			queryPopupObj.setMaxRecords(50);
			List<FeesPriorityVb> arrListResult = getScreenDao().getQueryPopupResults(queryPopupObj);
			if(arrListResult!= null && arrListResult.size() > 0) {
				exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
				exceptionCode.setResponse(arrListResult);
			}else {
				exceptionCode.setResponse(new ArrayList());
				exceptionCode.setErrorCode(Constants.NO_RECORDS_FOUND);
			}
			exceptionCode.setOtherInfo(queryPopupObj);
			return exceptionCode;
		}catch(Exception ex){
			//ex.printStackTrace();
			//logger.error("Exception in getting the getAllQueryPopupResult results.", ex);
			return null;
		}
	}
	@Override
	protected List<ReviewResultVb> transformToReviewResults(List<FeesPriorityVb> approvedCollection, List<FeesPriorityVb> pendingCollection) {
		ArrayList collTemp = getPageLoadValues();
		ResourceBundle rsb = CommonUtils.getResourceManger();
		if(pendingCollection != null)
			getScreenDao().fetchMakerVerifierNames(pendingCollection.get(0));
		if(approvedCollection != null)
			getScreenDao().fetchMakerVerifierNames(approvedCollection.get(0));
		ArrayList<ReviewResultVb> lResult = new ArrayList<ReviewResultVb>();

				ReviewResultVb lCountry = new ReviewResultVb(rsb.getString("country"),
					(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getCountry(),
					(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getCountry(),(!pendingCollection.get(0).getCountry().equalsIgnoreCase(approvedCollection.get(0).getCountry())));
				lResult.add(lCountry);

				ReviewResultVb lLeBook = new ReviewResultVb(rsb.getString("leBook"),
					(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getLeBook(),
					(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getLeBook(),(!pendingCollection.get(0).getLeBook().equalsIgnoreCase(approvedCollection.get(0).getLeBook())));
				lResult.add(lLeBook);
				
				if("DETAIL".equalsIgnoreCase(approvedCollection.get(0).getPriorityType())) {
					ReviewResultVb busLineId = new ReviewResultVb("Business Line Id",(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getBusinessLineId()+" - "+pendingCollection.get(0).getBusinessLineDesc(),
							(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getBusinessLineId()+" - "+approvedCollection.get(0).getBusinessLineDesc(),
									(!pendingCollection.get(0).getBusinessLineId().equalsIgnoreCase(approvedCollection.get(0).getBusinessLineId())));
					lResult.add(busLineId);
					
					ReviewResultVb effectiveDate = new ReviewResultVb("Effective Date",(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getEffectiveDate(),
							(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getEffectiveDate(),
									(!pendingCollection.get(0).getEffectiveDate().equalsIgnoreCase(approvedCollection.get(0).getEffectiveDate())));
					lResult.add(effectiveDate);
				} 
				
				ReviewResultVb lDimensionName = new ReviewResultVb(rsb.getString("dimensionName"),
					(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getDimensionName(),
					(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getDimensionName(),(!pendingCollection.get(0).getDimensionName().equalsIgnoreCase(approvedCollection.get(0).getDimensionName())));
				lResult.add(lDimensionName);
				
				ReviewResultVb columnAlias = new ReviewResultVb(rsb.getString("columnAlias"),
						(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getColumnAlias(),
						(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getColumnAlias(),(!pendingCollection.get(0).getColumnAlias().equalsIgnoreCase(approvedCollection.get(0).getColumnAlias())));
				lResult.add(columnAlias);

				ReviewResultVb lPriorityOrder = new ReviewResultVb(rsb.getString("priorityOrder"),
						(pendingCollection == null || pendingCollection.isEmpty())?"":String.valueOf(pendingCollection.get(0).getPriorityOrder()),
						        (approvedCollection == null || approvedCollection.isEmpty())?"":String.valueOf(approvedCollection.get(0).getPriorityOrder()),
						        (pendingCollection.get(0).getPriorityOrder() != approvedCollection.get(0).getPriorityOrder()));
				lResult.add(lPriorityOrder);
				
				ReviewResultVb lWeightage = new ReviewResultVb(rsb.getString("weightage"),
						(pendingCollection == null || pendingCollection.isEmpty())?"":String.valueOf(pendingCollection.get(0).getWeightage()),
						        (approvedCollection == null || approvedCollection.isEmpty())?"":String.valueOf(approvedCollection.get(0).getWeightage()),
						        (pendingCollection.get(0).getWeightage() != approvedCollection.get(0).getWeightage()));
				lResult.add(lWeightage);
				
				ReviewResultVb lStatus = new ReviewResultVb(rsb.getString("status"),
						(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getStatusDesc(),
						(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getStatusDesc(),(pendingCollection.get(0).getDbStatus() != approvedCollection.get(0).getDbStatus()));
				lResult.add(lStatus);

				ReviewResultVb lRecordIndicator = new ReviewResultVb(rsb.getString("recordIndicator"),
						(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getRecordIndicatorDesc(),
						(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getRecordIndicatorDesc(),(pendingCollection.get(0).getRecordIndicator() != approvedCollection.get(0).getRecordIndicator()));
				lResult.add(lRecordIndicator);
					
				ReviewResultVb lMaker = new ReviewResultVb(rsb.getString("maker"),(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getMaker() == 0?"":pendingCollection.get(0).getMakerName(),
						(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getMaker() == 0?"":approvedCollection.get(0).getMakerName(),(pendingCollection.get(0).getMaker() != approvedCollection.get(0).getMaker()));
				lResult.add(lMaker);
				ReviewResultVb lVerifier = new ReviewResultVb(rsb.getString("verifier"),(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getVerifier() == 0?"":pendingCollection.get(0).getVerifierName(),
						(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getVerifier() == 0?"":approvedCollection.get(0).getVerifierName(),(pendingCollection.get(0).getVerifier() != approvedCollection.get(0).getVerifier()));
				lResult.add(lVerifier);
				ReviewResultVb lDateLastModified = new ReviewResultVb(rsb.getString("dateLastModified"),(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getDateLastModified(),
					(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getDateLastModified(),(!pendingCollection.get(0).getDateLastModified().equals(approvedCollection.get(0).getDateLastModified())));
				lResult.add(lDateLastModified);
				ReviewResultVb lDateCreation = new ReviewResultVb(rsb.getString("dateCreation"),(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getDateCreation(),
					(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getDateCreation(),(!pendingCollection.get(0).getDateCreation().equals(approvedCollection.get(0).getDateCreation())));
				lResult.add(lDateCreation);
				return lResult; 
				

	}

	public FeesPriorityDao getRaMstFeesDefaultPriorityDao() {
		return feesPriorityDao;
	}

	public void setRaMstFeesDefaultPriorityDao(FeesPriorityDao feesPriorityDao) {
		this.feesPriorityDao = feesPriorityDao;
	}

	@Override
	protected AbstractDao<FeesPriorityVb> getScreenDao() {
		return feesPriorityDao;
	}

	@Override
	protected void setAtNtValues(FeesPriorityVb vObject) {
		// set AT NT Values here
	}

	@Override
	protected void setVerifReqDeleteType(FeesPriorityVb vObject) {
		ArrayList<CommonVb> lCommVbList = (ArrayList<CommonVb>) getCommonDao().findVerificationRequiredAndStaticDelete("RA_MST_FEES_DEFAULT_PRIORITY");
		vObject.setStaticDelete(lCommVbList.get(0).isStaticDelete());
		vObject.setVerificationRequired(lCommVbList.get(0).isVerificationRequired());
	}
	public ExceptionCode getAllDetailQueryPopupResult(FeesPriorityVb queryPopupObj){
		ExceptionCode exceptionCode = new ExceptionCode();
		try{
			setVerifReqDeleteType(queryPopupObj);
			doFormateDataForQuery(queryPopupObj);
			exceptionCode = doValidate(queryPopupObj);
			if(exceptionCode.getErrorCode() == Constants.ERRONEOUS_OPERATION){
				return exceptionCode;
			}
			int maxRecords = 0 ;
			
			 // Fetch the list of effective dates
	        List<FeesPriorityVb> effectiveDateLst = feesPriorityDao.getEffectiveDateList(queryPopupObj);
	      Map<String, List<FeesPriorityVb>> groupingMap = new HashMap();
			if (effectiveDateLst != null && !effectiveDateLst.isEmpty()) {
				Set<String> effectiveDateSet = effectiveDateLst.stream().map(FeesPriorityVb::getEffectiveDate)
						.collect(Collectors.toSet());
				maxRecords = effectiveDateSet.size();
				List<FeesPriorityVb> feePriorityDetailLst = feesPriorityDao.getQueryPopupResultsDetail(queryPopupObj);
				if(feePriorityDetailLst != null && feePriorityDetailLst.size() > 0) {
					SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");

					groupingMap = feePriorityDetailLst.stream()
					    .filter(vb -> effectiveDateSet.contains(vb.getEffectiveDate()) && vb.getEffectiveDate() != null)
					    .collect(Collectors.groupingBy(vb -> {
					        try {
					            Date date = dateFormatter.parse(vb.getEffectiveDate()); // Parse the string to Date
					            return date; // Use Date for sorting
					        } catch (ParseException e) {
					            throw new RuntimeException("Failed to parse date", e);
					        }
					    }))
					    .entrySet().stream()
					    .sorted(Map.Entry.<Date, List<FeesPriorityVb>>comparingByKey(Comparator.reverseOrder())) // Sort by Date in descending order
					    .collect(Collectors.toMap(
					        entry -> dateFormatter.format(entry.getKey()), // Format Date to String
					        Map.Entry::getValue,
					        (oldValue, newValue) -> oldValue, // Merge function in case of key collision
					        LinkedHashMap::new // Maintain insertion order
					    ));
			       
				}
			} else {
				exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			}
			
			if(groupingMap != null && groupingMap.size() > 0) {
				exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
				exceptionCode.setResponse(groupingMap);
			}else {
				exceptionCode.setResponse(new ArrayList());
				exceptionCode = CommonUtils.getResultObject(getScreenDao().getServiceDesc(), Constants.NO_RECORDS_FOUND, "Query", "");
			}
			queryPopupObj.setMaxRecords(maxRecords);
			exceptionCode.setOtherInfo(queryPopupObj);
			return exceptionCode;
		}catch(Exception ex){
			//ex.printStackTrace();
			//logger.error("Exception in getting the getAllQueryPopupResult results.", ex);
			return null;
		}
	}
	public ExceptionCode insertRecord(ExceptionCode pRequestCode, List<FeesPriorityVb> vObjects){
		ExceptionCode exceptionCode  = null;
		DeepCopy<FeesPriorityVb> deepCopy = new DeepCopy<FeesPriorityVb>();
		List<FeesPriorityVb> clonedObject = null;
		FeesPriorityVb vObject = null;
		try
		{
			setAtNtValues(vObjects);
			vObject = (FeesPriorityVb) pRequestCode.getOtherInfo();
			setVerifReqDeleteType(vObject);
			clonedObject = deepCopy.copyCollection(vObjects);
			exceptionCode = doValidate(vObject);
			if(exceptionCode.getErrorCode() == Constants.ERRONEOUS_OPERATION){
				return exceptionCode;
			}
			doFormateData(vObjects);
			if(!vObject.isVerificationRequired()){
				exceptionCode = feesPriorityDao.doInsertApprRecord(vObjects);
			}else{
				exceptionCode = feesPriorityDao.doInsertRecord(vObjects);
			}
			
			exceptionCode.setOtherInfo(vObject);
			exceptionCode.setResponse(vObjects);
			return exceptionCode;
		}catch(RuntimeCustomException rex){
			//logger.error("Insert Exception " + rex.getCode().getErrorMsg());
			exceptionCode = rex.getCode();
			String errorMsg = feesPriorityDao.parseErrorMsg(rex);
			exceptionCode.setErrorMsg(errorMsg);
			exceptionCode.setResponse(clonedObject);
			exceptionCode.setOtherInfo(vObject);
			return exceptionCode;
		}
	}
	public ExceptionCode doValidate(FeesPriorityVb vObject){
		ExceptionCode exceptionCode = new ExceptionCode();
		String operation = vObject.getActionType();
		String srtRestriction = getCommonDao().getRestrictionsByUsers("feesPriority", operation);
		if(!"Y".equalsIgnoreCase(srtRestriction)) {
			exceptionCode = new ExceptionCode();
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg(operation +" "+Constants.userRestrictionMsg);
			return exceptionCode;
		}
		exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
		return exceptionCode;
	}
	protected ExceptionCode doValidate(List<FeesPriorityVb> vObjects) {
		ExceptionCode exceptionCode= new ExceptionCode();
		FeesPriorityVb vObject = vObjects.get(0);
		String operation = vObject.getActionType();
		String srtRestriction = getCommonDao().getRestrictionsByUsers("feesPriority", operation);
		if(!"Y".equalsIgnoreCase(srtRestriction)) {
			exceptionCode = new ExceptionCode();
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg(operation +" "+Constants.userRestrictionMsg);
			return exceptionCode;
		}
		exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
		return exceptionCode;
	
	}
	public ExceptionCode getMaxEffectiveDatePriorityList(FeesPriorityVb vObject) {
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			exceptionCode.setOtherInfo(vObject);
			exceptionCode = doValidate(vObject);
			if(exceptionCode.getErrorCode() == Constants.ERRONEOUS_OPERATION){
				return exceptionCode;
			}
			List<FeesPriorityVb> feePriorityLst = feesPriorityDao.getMaxEffectiveDatePriorityList(vObject);
			if(feePriorityLst != null && feePriorityLst.size() > 0) {
				vObject.setTotalRows(feePriorityLst.size());
				exceptionCode.setOtherInfo(vObject);
				exceptionCode.setResponse(feePriorityLst);
				exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
			}
			
		} catch(Exception e) {
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			String errorMsg = feesPriorityDao.parseErrorMsg(e);
			exceptionCode.setErrorMsg(errorMsg);
		}
		return exceptionCode;
	}
}