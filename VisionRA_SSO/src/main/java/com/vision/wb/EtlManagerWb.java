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
import com.vision.dao.EtlManagerDao;
import com.vision.dao.EtlManagerDetailsDao;
import com.vision.dao.EtlScheduleDao;
import com.vision.dao.NumSubTabDao;
import com.vision.exception.ExceptionCode;
import com.vision.util.CommonUtils;
import com.vision.util.Constants;
import com.vision.vb.CommonVb;
import com.vision.vb.EtlManagerDetailsVb;
import com.vision.vb.EtlManagerVb;
import com.vision.vb.EtlScheduleVb;
@Component
public class EtlManagerWb extends AbstractDynaWorkerBean<EtlManagerVb>{
	@Autowired
	private EtlManagerDao etlManagerDao;
	@Autowired
	private EtlManagerDetailsDao etlManagerDetailsDao;
	@Autowired
	private NumSubTabDao numSubTabDao;
	@Autowired
	private AlphaSubTabDao alphaSubTabDao;
	@Autowired
	private CommonDao commonDao;
	@Autowired
	private EtlScheduleDao etlScheduleDao;
	
	public static Logger logger = LoggerFactory.getLogger(EtlManagerWb.class);
	
	public ArrayList getPageLoadValues(){
		List collTemp = null;
		ArrayList<Object> arrListLocal = new ArrayList<Object>();
		try{
			collTemp = numSubTabDao.findActiveNumSubTabsByNumTab(1);//status
			arrListLocal.add(collTemp);
			collTemp = numSubTabDao.findActiveNumSubTabsByNumTab(7);//record indicator
			arrListLocal.add(collTemp);
			return arrListLocal;
		}catch(Exception ex){
			ex.printStackTrace();
			//logger.error("Exception in getting the Page load values.", ex);
			return null;
		}
	}

	@Override
	protected AbstractDao<EtlManagerVb> getScreenDao() {
		return etlManagerDao;
	}
	@Override
	protected void setAtNtValues(EtlManagerVb vObject) {
		// TODO Auto-generated method stub
		
	}
	@Override
	protected void setVerifReqDeleteType(EtlManagerVb vObject) {
		ArrayList<CommonVb> lCommVbList =(ArrayList<CommonVb>) commonDao.findVerificationRequiredAndStaticDelete("RA_MST_EXTRACTION_HEADER");
		vObject.setStaticDelete(lCommVbList.get(0).isStaticDelete());
		vObject.setVerificationRequired(false);
	}
	@Override
	public ExceptionCode getQueryResults(EtlManagerVb vObject){
		int intStatus = 0;
		EtlManagerVb etlManagerVb = new EtlManagerVb();
		ExceptionCode exceptionCode = new ExceptionCode();
		setVerifReqDeleteType(vObject);
		Boolean pendFlag = true;
		exceptionCode = doValidate(vObject);
		if(exceptionCode.getErrorCode() == Constants.ERRONEOUS_OPERATION){
			return exceptionCode;
		}
		List<EtlManagerVb> collTemp = etlManagerDao.getQueryResults(vObject,intStatus);
		if(collTemp.size() == 0){
			exceptionCode = CommonUtils.getResultObject(etlManagerDao.getServiceDesc(), 16, "Query", "");
			exceptionCode.setOtherInfo(vObject);
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg("No records found");
			return exceptionCode;
		}else{
			collTemp.forEach(headerVb -> {
				List<EtlManagerDetailsVb> detailslst = etlManagerDetailsDao.getQueryEtlDetails(headerVb, 0);
				 headerVb.setEtlManagerDetaillst(detailslst);
			 });
			exceptionCode.setOtherInfo(vObject);
			exceptionCode.setResponse(collTemp);
			exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
			exceptionCode.setErrorMsg("Successful operation");
			return exceptionCode;
		}
	}
	public ExceptionCode addEtlSchedule(EtlScheduleVb vObject) {
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			EtlManagerVb dObj = new EtlManagerVb();
			dObj.setActionType(vObject.getActionType());
			exceptionCode = doValidate(dObj);
			if(exceptionCode.getErrorCode() == Constants.ERRONEOUS_OPERATION){
				return exceptionCode;
			}
			exceptionCode = etlScheduleDao.doInsertApprRecordForNonTrans(vObject);
		}catch(Exception e) {
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg(e.getMessage());
		}
		return exceptionCode;
	}
	public ExceptionCode deleteEtlSchedule(EtlScheduleVb vObject) {
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			EtlManagerVb dObj = new EtlManagerVb();
			dObj.setActionType(vObject.getActionType());
			exceptionCode = doValidate(dObj);
			if(exceptionCode.getErrorCode() == Constants.ERRONEOUS_OPERATION){
				return exceptionCode;
			}
			exceptionCode = etlScheduleDao.doDeleteApprRecordForNonTrans(vObject);
		}catch(Exception e) {
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg(e.getMessage());
		}
		return exceptionCode;
	}
	public ExceptionCode getQueryEtlSchedule(EtlScheduleVb vObject){
		int intStatus = 0;
		ExceptionCode exceptionCode = new ExceptionCode();
		EtlManagerVb dObj = new EtlManagerVb();
		dObj.setActionType(vObject.getActionType());
		exceptionCode = doValidate(dObj);
		if(exceptionCode.getErrorCode() == Constants.ERRONEOUS_OPERATION){
			return exceptionCode;
		}
		try {
			List<EtlScheduleVb> collTemp = etlScheduleDao.getQueryResults(vObject);
			exceptionCode.setOtherInfo(vObject);
			exceptionCode.setResponse(collTemp);
			exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
			exceptionCode.setErrorMsg("Successful operation");
		} catch (Exception e) {
			exceptionCode.setOtherInfo(vObject);
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg(e.getMessage());
		}
		return exceptionCode;
	}
	public ExceptionCode doValidate(EtlManagerVb vObject){
		ExceptionCode exceptionCode = new ExceptionCode();
		String operation = vObject.getActionType();
		String srtRestriction = getCommonDao().getRestrictionsByUsers("etlManager", operation);
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
