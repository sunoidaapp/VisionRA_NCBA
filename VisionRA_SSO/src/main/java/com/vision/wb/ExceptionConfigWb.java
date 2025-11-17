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
import com.vision.dao.ExceptionConfigDetailsDao;
import com.vision.dao.ExceptionConfigHeaderDao;
import com.vision.dao.NumSubTabDao;
import com.vision.exception.ExceptionCode;
import com.vision.exception.RuntimeCustomException;
import com.vision.util.CommonUtils;
import com.vision.util.Constants;
import com.vision.util.DeepCopy;
import com.vision.util.ValidationUtil;
import com.vision.vb.AlphaSubTabVb;
import com.vision.vb.CommonVb;
import com.vision.vb.ExceptionConfigCommentsVb;
import com.vision.vb.ExceptionConfigDetailsVb;
import com.vision.vb.ExceptionConfigHeaderVb;
import com.vision.vb.ExceptionManualFiltersVb;

@Component
public class ExceptionConfigWb extends AbstractDynaWorkerBean<ExceptionConfigHeaderVb> {
	@Autowired
	private ExceptionConfigHeaderDao manualExceptionConfigHeadersDao;
	@Autowired
	private ExceptionDetailsWb exceptionDetailsWb;
	@Autowired
	private ExceptionConfigHeaderDao exceptionHeaderDao;
	@Autowired
	private ExceptionConfigDetailsDao exceptionConfigDetailsDao;
	@Autowired
	private NumSubTabDao numSubTabDao;
	@Autowired
	private AlphaSubTabDao alphaSubTabDao;
	@Autowired
	private CommonDao commonDao;

	public static Logger logger = LoggerFactory.getLogger(ExceptionConfigWb.class);

	public ArrayList getPageLoadValues() {
		List collTemp = null;
		ArrayList<Object> arrListLocal = new ArrayList<Object>();
		try {
			collTemp = numSubTabDao.findActiveNumSubTabsByNumTab(1);// status
			arrListLocal.add(collTemp);
			collTemp = numSubTabDao.findActiveNumSubTabsByNumTab(175);// record indicator
			arrListLocal.add(collTemp);
			collTemp = commonDao.getVisionBusinessDate();
			arrListLocal.add(collTemp);
			collTemp = alphaSubTabDao.findActiveAlphaSubTabsByAlphaTab(7077);// Exception Flag
			arrListLocal.add(collTemp);
			collTemp = alphaSubTabDao.findActiveAlphaSubTabsByAlphaTab(207); // Posted Flag
			arrListLocal.add(collTemp);
			collTemp = alphaSubTabDao.findActiveAlphaSubTabsByAlphaTab(7006); // Transaction Type
			arrListLocal.add(collTemp);
			collTemp = alphaSubTabDao.findActiveAlphaSubTabsByAlphaTab(7078); // Exception Type
			arrListLocal.add(collTemp);
			collTemp = alphaSubTabDao.findActiveAlphaSubTabsByAlphaTab(7032); // Fee Basis
			arrListLocal.add(collTemp);
			collTemp = alphaSubTabDao.findActiveAlphaSubTabsByAlphaTab(7401); // condition operation
			arrListLocal.add(collTemp);
			String country = commonDao.findVisionVariableValue("DEFAULT_COUNTRY");
			arrListLocal.add(country);
			String leBook = commonDao.findVisionVariableValue("DEFAULT_LE_BOOK");
			arrListLocal.add(leBook);
			ArrayList<AlphaSubTabVb> countrylst = (ArrayList<AlphaSubTabVb>) manualExceptionConfigHeadersDao.findCountry();
			for(AlphaSubTabVb alphaSubTabVb : countrylst) {
				alphaSubTabVb.setChildren(manualExceptionConfigHeadersDao.findLeBook(alphaSubTabVb.getAlphaSubTab()));
			}
			arrListLocal.add(countrylst);
			ArrayList<ExceptionManualFiltersVb> filterlst = (ArrayList<ExceptionManualFiltersVb>) manualExceptionConfigHeadersDao.findSourceTable();
			for(ExceptionManualFiltersVb filterVb : filterlst) {
				filterVb.setChildren(manualExceptionConfigHeadersDao.findTableColumns(filterVb.getTableName()));
			}
			arrListLocal.add(filterlst);
			collTemp = alphaSubTabDao.findActiveAlphaSubTabsByAlphaTab(7079); // Category
			arrListLocal.add(collTemp);
			collTemp  = commonDao.getAllBusinessDate();
			arrListLocal.add(collTemp);
			String exceptionDateChange = commonDao.findVisionVariableValue("RA_EXC_DATECHANGE");
			arrListLocal.add(exceptionDateChange);
			String leakageFormula = commonDao.findVisionVariableValue("RA_LEAKAGE_FORMULA");
			arrListLocal.add(leakageFormula);
			collTemp = commonDao.getCurrencyDecimals(false);
			arrListLocal.add(collTemp);
			collTemp = commonDao.getCurrencyDecimals(true);
			arrListLocal.add(collTemp);
			collTemp = exceptionHeaderDao.getExceptionTypeDetails();
			arrListLocal.add(collTemp);
			return arrListLocal;
		} catch (Exception ex) {
			logger.error("Exception in getting the Page load values.", ex);
			return null;
		}
	}

	@Override
	protected AbstractDao<ExceptionConfigHeaderVb> getScreenDao() {
		return manualExceptionConfigHeadersDao;
	}

	@Override
	protected void setAtNtValues(ExceptionConfigHeaderVb vObject) {
		vObject.setExceptionFlagAT(7077);
		vObject.setPostedFlagAT(207);
		vObject.setTransLineTypeAt(7006);
		vObject.setAutoExceptionTypeAT(7078);
		vObject.setFeeBasisAt(7032);
		vObject.setRecordIndicatorNt(175);
		vObject.setExceptionStatus(1);
		if(!ValidationUtil.isValid(vObject.getPostedFlag()))
			vObject.setPostedFlag("N");
	}

	@Override
	protected void setVerifReqDeleteType(ExceptionConfigHeaderVb vObject) {
		ArrayList<CommonVb> lCommVbList = (ArrayList<CommonVb>) commonDao
				.findVerificationRequiredAndStaticDelete("RA_Exception_Header");
		vObject.setStaticDelete(lCommVbList.get(0).isStaticDelete());
		vObject.setVerificationRequired(false);
	}

	public ExceptionCode modifyRecord(ExceptionConfigHeaderVb vObject,Boolean submitFlag){
		ExceptionCode exceptionCode  = null;
		DeepCopy<ExceptionConfigHeaderVb> deepCopy = new DeepCopy<ExceptionConfigHeaderVb>();
		ExceptionConfigHeaderVb clonedObject = null;
		try{
			setAtNtValues(vObject);
			setVerifReqDeleteType(vObject);
			clonedObject = deepCopy.copy(vObject);
			doFormateData(vObject);
			exceptionCode = doValidate(vObject);
			if(exceptionCode.getErrorCode() == Constants.ERRONEOUS_OPERATION){
				return exceptionCode;
			}
			if(!vObject.isVerificationRequired()){
				exceptionCode = getScreenDao().doUpdateApprRecord(vObject);
				if(exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION)
					return exceptionCode;
			}else{
				exceptionCode = getScreenDao().doUpdateRecord(vObject);
				if(exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION)
					return exceptionCode;
			}
			if(vObject.getManualExceptionConfigDetaillst() != null && vObject.getManualExceptionConfigDetaillst().size() > 0)
				exceptionCode = exceptionConfigDetailsDao.doUpdateDetailsRecord(vObject.getManualExceptionConfigDetaillst()); 
			getScreenDao().fetchMakerVerifierNames(vObject);
			exceptionCode.setOtherInfo(vObject);
			if(submitFlag) {
				int retVal = 0;
				retVal = exceptionHeaderDao.doSubmitRecord(vObject,8);
				if(retVal == Constants.ERRONEOUS_OPERATION) {
					exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
					exceptionCode.setErrorMsg("Error while submitting the reference");
				}else {
					exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
					exceptionCode.setErrorMsg("Variance Flagging - Submit - Successful");
				}
			}
			return exceptionCode;
		}catch(RuntimeCustomException rex){
			exceptionCode = rex.getCode();
			exceptionCode.setOtherInfo(clonedObject);
			return exceptionCode;
		}
	}
	public ExceptionCode doValidate(ExceptionConfigHeaderVb vObject){
		ExceptionCode exceptionCode = new ExceptionCode();
		String operation = vObject.getActionType();
		String srtRestriction = getCommonDao().getRestrictionsByUsers("ManualException", operation);
		if(!"Y".equalsIgnoreCase(srtRestriction)) {
			exceptionCode = new ExceptionCode();
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg(operation +" "+Constants.userRestrictionMsg);
			return exceptionCode;
		}
		exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
		vObject.setVerificationRequired(false);
		return exceptionCode;
	}
	
	public ExceptionCode authorizeException(ExceptionConfigHeaderVb vObject,List<ExceptionConfigHeaderVb> vObjectlst) {
		setAtNtValues(vObject);
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			exceptionCode = doValidate(vObject);
			if(exceptionCode.getErrorCode() == Constants.ERRONEOUS_OPERATION){
				return exceptionCode;
			}
			exceptionCode = exceptionHeaderDao.checkRecordAuthorized(vObject.getCountry(), vObject.getLeBook(), vObject.getExceptionReference(),vObject.getMaker());
			if(exceptionCode.getErrorCode() == Constants.ERRONEOUS_OPERATION) {
				return exceptionCode;
			}
			if(vObjectlst != null && vObjectlst.size() > 0) {
				for(ExceptionConfigHeaderVb dObj : vObjectlst) {
					setAtNtValues(dObj);
					exceptionCode = manualExceptionConfigHeadersDao.doApproveRecord(dObj);
				}
			}
		}catch(Exception e) {
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg(e.getMessage());
		}
		return exceptionCode;
	}
	
	
	public ExceptionCode rejectException(ExceptionConfigHeaderVb vObject, List<ExceptionConfigHeaderVb> vObjectlst) {
		setAtNtValues(vObject);
		ExceptionCode exceptionCode = new ExceptionCode();
		int retVal = 0;
		try {
			exceptionCode = doValidate(vObject);
			if (exceptionCode.getErrorCode() == Constants.ERRONEOUS_OPERATION) {
				return exceptionCode;
			}
			exceptionCode = exceptionHeaderDao.checkRecordAuthorized(vObject.getCountry(), vObject.getLeBook(), vObject.getExceptionReference(),vObject.getMaker());
			if(exceptionCode.getErrorCode() == Constants.ERRONEOUS_OPERATION) {
				return exceptionCode;
			}
			if (vObjectlst != null && vObjectlst.size() > 0) {
				for (ExceptionConfigHeaderVb dObj : vObjectlst) {
					setAtNtValues(dObj);
					exceptionCode = manualExceptionConfigHeadersDao.rejectException(dObj);
				}
			}
		} catch (Exception e) {
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg(e.getMessage());
		}
		return exceptionCode;
	}
	public ExceptionCode deleteException(ExceptionConfigHeaderVb vObject) {
		ExceptionCode exceptionCode = new ExceptionCode();
		exceptionCode.setOtherInfo(vObject);
		List<ExceptionConfigDetailsVb> vObjectlst = new ArrayList();
		try {
			exceptionCode = doValidate(vObject);
			if(exceptionCode.getErrorCode() == Constants.ERRONEOUS_OPERATION){
				return exceptionCode;
			}
			exceptionCode = exceptionHeaderDao.checkRecordAuthorized(vObject.getCountry(), vObject.getLeBook(), vObject.getExceptionReference(),vObject.getMaker());
			if(exceptionCode.getErrorCode() == Constants.ERRONEOUS_OPERATION) {
				return exceptionCode;
			}
			Boolean detailPresent = false;
			if (vObject.getManualExceptionConfigDetaillst() != null && vObject.getManualExceptionConfigDetaillst().size() > 0) {
				exceptionCode = exceptionConfigDetailsDao.doDeleteApprRecord(vObject.getManualExceptionConfigDetaillst());
				detailPresent = true;
			}
			if (!detailPresent)
				exceptionCode = manualExceptionConfigHeadersDao.doDeleteRecord(vObject);
		}catch(Exception e) {
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg(e.getMessage());
		}
		return exceptionCode;
	}
	public ExceptionCode getAllQueryPopupResult(ExceptionConfigHeaderVb queryPopupObj){
		ExceptionCode exceptionCode = new ExceptionCode();
		try{
			setVerifReqDeleteType(queryPopupObj);
			doFormateDataForQuery(queryPopupObj);
			exceptionCode = doValidate(queryPopupObj);
			if(exceptionCode.getErrorCode() == Constants.ERRONEOUS_OPERATION){
				return exceptionCode;
			}
			List<ExceptionConfigHeaderVb> arrListResult = getScreenDao().getQueryPopupResults(queryPopupObj);
			List<ExceptionConfigHeaderVb> exceptionHeadersLst = getScreenDao().getQueryPopupResults(queryPopupObj);
			if(arrListResult!= null && arrListResult.size() > 0) {
				for (ExceptionConfigHeaderVb exceptionConfigHeaderVb : arrListResult) {
					ArrayList<ExceptionConfigCommentsVb> commentsLst = exceptionHeaderDao.getCommmentsLst(exceptionConfigHeaderVb);
					exceptionConfigHeaderVb.setExceptionCommentsLst(commentsLst);
					exceptionHeadersLst.add(exceptionConfigHeaderVb);
				}
				exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
				exceptionCode.setResponse(arrListResult);
			}else {
				exceptionCode.setResponse(new ArrayList());
				exceptionCode = CommonUtils.getResultObject(getScreenDao().getServiceDesc(), Constants.NO_RECORDS_FOUND, "Query", "");
			}
			exceptionCode.setOtherInfo(queryPopupObj);
			return exceptionCode;
		}catch(Exception ex){
			ex.printStackTrace();
			//logger.error("Exception in getting the getAllQueryPopupResult results.", ex);
			return null;
		}
	}
	public ExceptionCode getQueryResults(ExceptionConfigHeaderVb vObject){
		int intStatus = 1;
		setVerifReqDeleteType(vObject);
		ExceptionCode exceptionCode = doValidate(vObject);
		if(exceptionCode.getErrorCode() == Constants.ERRONEOUS_OPERATION){
			return exceptionCode;
		}
		
		List<ExceptionConfigHeaderVb> collTemp = getScreenDao().getQueryResults(vObject,intStatus);
		if (collTemp.size() == 0){
			intStatus = 0;
			collTemp = getScreenDao().getQueryResults(vObject,intStatus);
		}
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
}