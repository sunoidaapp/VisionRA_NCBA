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
import com.vision.dao.BusinessLineConfigDao;
import com.vision.dao.CommonDao;
import com.vision.dao.FeesConfigHeadersDao;
import com.vision.dao.NumSubTabDao;
import com.vision.dao.ServiceLineConfigDao;
import com.vision.dao.TransLinesChannelDao;
import com.vision.dao.TransLinesGlDao;
import com.vision.dao.TransLinesSbuDao;
import com.vision.exception.ExceptionCode;
import com.vision.exception.RuntimeCustomException;
import com.vision.util.CommonUtils;
import com.vision.util.Constants;
import com.vision.util.DeepCopy;
import com.vision.vb.BusinessLineHeaderVb;
import com.vision.vb.CommonVb;
import com.vision.vb.FeesConfigHeaderVb;
import com.vision.vb.TransLineChannelVb;
import com.vision.vb.TransLineGLVb;
import com.vision.vb.TransLineHeaderVb;
import com.vision.vb.TransLineSbuVb;
@Component
public class ServiceLineConfigWb extends AbstractDynaWorkerBean<TransLineHeaderVb>{
	@Autowired
	private ServiceLineConfigDao serviceLineConfigDao;
	@Autowired
	private NumSubTabDao numSubTabDao;
	@Autowired
	private AlphaSubTabDao alphaSubTabDao;
	@Autowired
	private CommonDao commonDao;
	@Autowired
	private TransLinesSbuDao transLinesSbuDao;
	@Autowired
	private TransLinesChannelDao transLinesChannelDao;
	@Autowired
	private TransLinesGlDao transLinesGlDao;
	@Autowired
	private FeesConfigHeadersDao  feesConfigHeadersDao;
	@Autowired
	private BusinessLineConfigDao businessLineConfigDao;
	
	public static Logger logger = LoggerFactory.getLogger(ServiceLineConfigWb.class);
	
	public ArrayList getPageLoadValues(){
		List collTemp = null;
		ArrayList<Object> arrListLocal = new ArrayList<Object>();
		try{
			collTemp = numSubTabDao.findActiveNumSubTabsByNumTab(1);
			arrListLocal.add(collTemp);
			collTemp = numSubTabDao.findActiveNumSubTabsByNumTab(7);
			arrListLocal.add(collTemp);
			return arrListLocal;
		}catch(Exception ex){
			ex.printStackTrace();
			//logger.error("Exception in getting the Page load values.", ex);
			return null;
		}
	}

	@Override
	protected AbstractDao<TransLineHeaderVb> getScreenDao() {
		return serviceLineConfigDao;
	}
	@Override
	protected void setAtNtValues(TransLineHeaderVb vObject) {
		// TODO Auto-generated method stub
		
	}
	@Override
	protected void setVerifReqDeleteType(TransLineHeaderVb vObject) {
		ArrayList<CommonVb> lCommVbList =(ArrayList<CommonVb>) commonDao.findVerificationRequiredAndStaticDelete("RA_MST_TRANS_LINE_HEADER");
		vObject.setStaticDelete(lCommVbList.get(0).isStaticDelete());
		vObject.setVerificationRequired(lCommVbList.get(0).isVerificationRequired());
	}
	@Override
	public ExceptionCode getQueryResults(TransLineHeaderVb vObject){
		int intStatus = 1;
		TransLineHeaderVb transLineHeaderVb = new TransLineHeaderVb();
		ExceptionCode exceptionCode = new ExceptionCode();
		setVerifReqDeleteType(vObject);
		exceptionCode= doValidate(vObject);
		if(exceptionCode.getErrorCode() == Constants.ERRONEOUS_OPERATION){
			return exceptionCode;
		}
		Boolean pendFlag = true;
		List<TransLineGLVb> transGlList = new ArrayList<TransLineGLVb>();
		List<TransLineSbuVb> transSbuList = new ArrayList<TransLineSbuVb>();
		List<TransLineChannelVb> transChannelList = new ArrayList<TransLineChannelVb>();
		List<TransLineHeaderVb> collTemp = serviceLineConfigDao.getQueryResults(vObject,intStatus);
		if (collTemp.size() == 0){
			intStatus = 0;
			collTemp = serviceLineConfigDao.getQueryResults(vObject,intStatus);
			pendFlag = false;
		}
		if(collTemp.size() == 0){
			exceptionCode = CommonUtils.getResultObject(serviceLineConfigDao.getServiceDesc(), 16, "Query", "");
			exceptionCode.setOtherInfo(vObject);
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg("No records found");
			return exceptionCode;
		}else{
			if(collTemp.get(0).getRecordIndicator() == 3) {
				pendFlag = false;
			}
			if(!pendFlag) {
				transGlList = transLinesGlDao.getTransGLDetails(vObject,Constants.STATUS_ZERO);
				transSbuList = transLinesSbuDao.getTransSbuDetails(vObject,Constants.STATUS_ZERO);
				transChannelList = transLinesChannelDao.getTransChannelDetails(vObject, Constants.STATUS_ZERO);
			}else {
				transGlList = transLinesGlDao.getTransGLDetails(vObject,Constants.STATUS_DELETE);
				transSbuList = transLinesSbuDao.getTransSbuDetails(vObject,Constants.STATUS_DELETE);
				transChannelList = transLinesChannelDao.getTransChannelDetails(vObject, Constants.STATUS_DELETE);
			}
			if(transGlList != null && !transGlList.isEmpty()) {
				collTemp.get(0).setTransLineGllst(transGlList);
			}
			if(transSbuList != null && transSbuList.size() > 0) {
				StringJoiner sbuArrJoiner = new StringJoiner(",");
				transSbuList.forEach(transSbu -> {
					sbuArrJoiner.add(transSbu.getBusinessVertical());
				});
				collTemp.get(0).setBusinessVertical(sbuArrJoiner.toString());
			}
			if(transChannelList != null && transChannelList.size() > 0) {
				StringJoiner channelArrJoiner = new StringJoiner(",");
				transChannelList.forEach(transChannel -> {
					channelArrJoiner.add(transChannel.getChannelId());
				});
				collTemp.get(0).setChannelId(channelArrJoiner.toString());
			}
			BusinessLineHeaderVb businessLineHeaderVb = new BusinessLineHeaderVb();
			
			businessLineHeaderVb.setCountry(vObject.getCountry());
			businessLineHeaderVb.setLeBook(vObject.getLeBook());
			businessLineHeaderVb.setTransLineId(vObject.getTransLineId());
			List<BusinessLineHeaderVb> businesslst = businessLineConfigDao.getQueryPopupResults(businessLineHeaderVb);
			if(businesslst != null && businesslst.size() > 0) {
				vObject.setBusinessLineCount(businesslst.size());
				for(BusinessLineHeaderVb businessVb : businesslst) {
					FeesConfigHeaderVb feeLineVb = new FeesConfigHeaderVb();
					feeLineVb.setCountry(businessVb.getCountry());
					feeLineVb.setLeBook(businessVb.getLeBook());
					feeLineVb.setTransLineId(businessVb.getTransLineId());
					feeLineVb.setBusinessLineId(businessVb.getBusinessLineId());
					List<FeesConfigHeaderVb> feeConfigLst = feesConfigHeadersDao.getQueryPopupResults(feeLineVb);
					if(feeConfigLst != null && feeConfigLst.size() > 0) {
						vObject.setFeeLineCount(feeConfigLst.size());
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
	@Override
	public ExceptionCode insertRecord(TransLineHeaderVb vObject){
		ExceptionCode exceptionCode  = null;
		DeepCopy<TransLineHeaderVb> deepCopy = new DeepCopy<TransLineHeaderVb>();
		TransLineHeaderVb clonedObject = null;
		try
		{
			setAtNtValues(vObject);
			setVerifReqDeleteType(vObject);
			clonedObject = deepCopy.copy(vObject);
			doFormateData(vObject);
			exceptionCode = doValidate(vObject);
			if(exceptionCode.getErrorCode() == Constants.ERRONEOUS_OPERATION){
				return exceptionCode;
			}			
			if(!vObject.isVerificationRequired()){
				exceptionCode = getScreenDao().doInsertApprRecord(vObject);
				feesConfigHeadersDao.callLineMergeProc(vObject.getCountry(),vObject.getLeBook(),vObject.getTransLineId() ,"PR_RA_MST_TRANS_LINE");
			}else{
				exceptionCode = getScreenDao().doInsertRecord(vObject);
			}
			getScreenDao().fetchMakerVerifierNames(vObject);
			exceptionCode.setOtherInfo(vObject);
			return exceptionCode;
		}catch(RuntimeCustomException rex){
			//logger.error("Insert Exception " + rex.getCode().getErrorMsg());
//			//logger.error( ((vObject==null)? "vObject is Null":vObject.toString()));
			exceptionCode = rex.getCode();
			exceptionCode.setOtherInfo(clonedObject);
			return exceptionCode;
		}
	}
	@Override
	public ExceptionCode modifyRecord(TransLineHeaderVb vObject){
		ExceptionCode exceptionCode  = null;
		DeepCopy<TransLineHeaderVb> deepCopy = new DeepCopy<TransLineHeaderVb>();
		TransLineHeaderVb clonedObject = null;
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
				feesConfigHeadersDao.callLineMergeProc(vObject.getCountry(),vObject.getLeBook(),vObject.getTransLineId() ,"PR_RA_MST_TRANS_LINE");
				ArrayList<String> businessLinelst = businessLineConfigDao.getTranBusinessLine(vObject.getCountry(),vObject.getLeBook(),vObject.getTransLineId());
				if(businessLinelst != null && !businessLinelst.isEmpty()) {
					businessLinelst.forEach(businessLine -> {
						feesConfigHeadersDao.callLineMergeProc(vObject.getCountry(),vObject.getLeBook(),businessLine,"PR_RA_MST_BUSINESS_LINE");
						//feesConfigHeadersDao.callLineMergeProc(vObject.getCountry(),vObject.getLeBook(),businessLine,"PR_RA_MST_FEE_LINE");
					});
				}
			}else{
				exceptionCode = getScreenDao().doUpdateRecord(vObject);
			}
			getScreenDao().fetchMakerVerifierNames(vObject);
			exceptionCode.setOtherInfo(vObject);
			return exceptionCode;
		}catch(RuntimeCustomException rex){
			//logger.error("Modify Exception " + rex.getCode().getErrorMsg());
//			//logger.error( ((vObject==null)? "vObject is Null":vObject.toString()));
			exceptionCode = rex.getCode();
			exceptionCode.setOtherInfo(clonedObject);
			return exceptionCode;
		}
	}
	@Override
	public ExceptionCode approve(TransLineHeaderVb vObject){
		ExceptionCode exceptionCode  = null;
		DeepCopy<TransLineHeaderVb> deepCopy = new DeepCopy<TransLineHeaderVb>();
		TransLineHeaderVb clonedObject = null;
		try{
			exceptionCode = doValidate(vObject);
			if(exceptionCode != null && exceptionCode.getErrorMsg() != ""){
				return exceptionCode;
			}
			setVerifReqDeleteType(vObject);
			clonedObject = deepCopy.copy(vObject);
			exceptionCode = doValidate(vObject);
			if(exceptionCode.getErrorCode() == Constants.ERRONEOUS_OPERATION){
				return exceptionCode;
			}
			exceptionCode = getScreenDao().doApproveForTransaction(vObject,vObject.isStaticDelete());
			feesConfigHeadersDao.callLineMergeProc(vObject.getCountry(),vObject.getLeBook(),vObject.getTransLineId() ,"PR_RA_MST_TRANS_LINE");
			ArrayList<String> businessLinelst = businessLineConfigDao.getTranBusinessLine(vObject.getCountry(),vObject.getLeBook(),vObject.getTransLineId());
			if(businessLinelst != null && !businessLinelst.isEmpty()) {
				businessLinelst.forEach(businessLine -> {
					feesConfigHeadersDao.callLineMergeProc(vObject.getCountry(),vObject.getLeBook(),businessLine,"PR_RA_MST_BUSINESS_LINE");
					//feesConfigHeadersDao.callLineMergeProc(vObject.getCountry(),vObject.getLeBook(),businessLine,"PR_RA_MST_FEE_LINE");
				});
			}
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
	public ExceptionCode doValidate(TransLineHeaderVb vObject){
		ExceptionCode exceptionCode = new ExceptionCode();
		String operation = vObject.getActionType();
		String srtRestrion = getCommonDao().getRestrictionsByUsers("TransLineConfig", operation);
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

