package com.vision.wb;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.vision.dao.AbstractDao;
import com.vision.dao.CommonDao;
import com.vision.dao.EtlPostingsDao;
import com.vision.exception.ExceptionCode;
import com.vision.util.Constants;
import com.vision.util.ValidationUtil;
import com.vision.vb.CommonVb;
import com.vision.vb.EtlPostingsHeaderVb;
import com.vision.vb.EtlPostingsVb;
@Component
public class ETLPostingWb extends AbstractDynaWorkerBean<EtlPostingsVb>{

	@Autowired
	EtlPostingsDao etlPostingsDao;
	
	@Autowired
	CommonDao commonDao;
	
	public ArrayList getPageLoadValues(){
		ArrayList<Object> arrListLocal = new ArrayList<Object>();
		try{
			String country = commonDao.findVisionVariableValue("DEFAULT_COUNTRY");
			String leBook = commonDao.findVisionVariableValue("DEFAULT_LE_BOOK");
			String businessDate = commonDao.getVisionBusinessDate(country+ "-" +leBook);
			arrListLocal.add(ValidationUtil.isValid(businessDate) ? businessDate : "");
			return arrListLocal;
		}catch(Exception ex){
			ex.printStackTrace();
			//logger.error("Exception in getting the Page load values.", ex);
			return new ArrayList<>();
		}
	}

	public ExceptionCode getDependentFlag(EtlPostingsVb vObject) {
		ExceptionCode exceptionCode = new ExceptionCode();
		int value;
		EtlPostingsHeaderVb etlPostingsHeaderVb = new EtlPostingsHeaderVb();
		try {
			etlPostingsHeaderVb.setCountry(vObject.getCountry());
			etlPostingsHeaderVb.setLeBook(vObject.getLeBook());
			etlPostingsHeaderVb.setExtractionFrequency(vObject.getExtractionFrequency());
			etlPostingsHeaderVb.setExtractionSequence(Integer.parseInt(vObject.getExtractionSequence()));
			value = etlPostingsDao.getDependentSequence(etlPostingsHeaderVb);
			if (value != 0) {
				vObject.setDependentFlag("Y");
			} else {
				vObject.setDependentFlag("N");
			}
			exceptionCode.setOtherInfo(vObject);
		} catch (Exception e) {

		}
		return exceptionCode;
	}
	@Override
	protected void setVerifReqDeleteType(EtlPostingsVb vObject) {
		ArrayList<CommonVb> lCommVbList =(ArrayList<CommonVb>) commonDao.findVerificationRequiredAndStaticDelete("RA_TRN_POSTING_HISTORY");
		vObject.setStaticDelete(lCommVbList.get(0).isStaticDelete());
		vObject.setVerificationRequired(false);
		//vObject.setVerificationRequired(lCommVbList.get(0).isVerificationRequired());
	}
	@Override
	protected AbstractDao<EtlPostingsVb> getScreenDao() {
		// TODO Auto-generated method stub
		return etlPostingsDao;
	}

	@Override
	protected void setAtNtValues(EtlPostingsVb vObject) {
		// TODO Auto-generated method stub
		
	}
	protected ExceptionCode doValidate(List<EtlPostingsVb> vObjects) {
		ExceptionCode exceptionCode = new ExceptionCode();
		EtlPostingsVb vObject = vObjects.get(0);
		String operation = vObject.getActionType();
		String srtRestriction = getCommonDao().getRestrictionsByUsers("ETLPosting", operation);
		if(!"Y".equalsIgnoreCase(srtRestriction)) {
			exceptionCode = new ExceptionCode();
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg(operation +" "+Constants.userRestrictionMsg);
			return exceptionCode;
		}
		exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
		return exceptionCode;
	}

	public ExceptionCode doValidate(EtlPostingsVb vObject){
		ExceptionCode exceptionCode = new ExceptionCode();
		String operation = vObject.getActionType();
		String srtRestriction = getCommonDao().getRestrictionsByUsers("ETLPosting", operation);
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
