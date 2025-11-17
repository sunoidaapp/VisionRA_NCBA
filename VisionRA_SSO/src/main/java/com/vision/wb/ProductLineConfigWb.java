package com.vision.wb;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

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
import com.vision.dao.ProductLineConfigDao;
import com.vision.dao.ServiceLineConfigDao;
import com.vision.dao.TransLinesChannelDao;
import com.vision.dao.TransLinesGlDao;
import com.vision.dao.TransLinesSbuDao;
import com.vision.exception.ExceptionCode;
import com.vision.exception.RuntimeCustomException;
import com.vision.util.CommonUtils;
import com.vision.util.Constants;
import com.vision.util.DeepCopy;
import com.vision.util.ValidationUtil;
import com.vision.vb.AlphaSubTabVb;
import com.vision.vb.BusinessLineHeaderVb;
import com.vision.vb.CommonVb;
import com.vision.vb.FeesConfigHeaderVb;
import com.vision.vb.NumSubTabVb;
import com.vision.vb.ReviewResultVb;
import com.vision.vb.SmartSearchVb;
import com.vision.vb.TransLineChannelVb;
import com.vision.vb.TransLineGLVb;
import com.vision.vb.TransLineHeaderVb;
import com.vision.vb.TransLineSbuVb;
@Component
public class ProductLineConfigWb extends AbstractWorkerBean<TransLineHeaderVb>{
	@Autowired
	private ProductLineConfigDao productLineConfigDao;
	@Autowired
	private NumSubTabDao numSubTabDao;
	@Autowired
	private AlphaSubTabDao alphaSubTabDao;
	@Autowired
	private CommonDao commonDao;
	@Autowired
	TransLinesSbuDao transLinesSbuDao;
	@Autowired
	TransLinesGlDao transLinesGlDao;
	@Autowired
	private FeesConfigHeadersDao  feesConfigHeadersDao;
	@Autowired
	private BusinessLineConfigDao businessLineConfigDao;
	@Autowired
	private BusinessLineConfigWb businessLineConfigWb;
	@Autowired
	private FeesConfigHeadersWb feesConfigHeadersWb;
	@Autowired
	private ServiceLineConfigDao serviceLineConfigDao;
	@Autowired
	private TransLinesChannelDao transLinesChannelDao;
	
	public static Logger logger = LoggerFactory.getLogger(ProductLineConfigWb.class);
	
	public ArrayList getPageLoadValues(){
		List collTemp = null;
		ArrayList<Object> arrListLocal = new ArrayList<Object>();
		try{
			collTemp = numSubTabDao.findActiveNumSubTabsByNumTab(1);
			arrListLocal.add(collTemp);
			collTemp = numSubTabDao.findActiveNumSubTabsByNumTab(7);
			arrListLocal.add(collTemp);
			collTemp = alphaSubTabDao.findActiveAlphaSubTabsByAlphaTab(7006);
			arrListLocal.add(collTemp);
			String country = commonDao.findVisionVariableValue("DEFAULT_COUNTRY");
			arrListLocal.add(country);
			String leBook = commonDao.findVisionVariableValue("DEFAULT_LE_BOOK");
			arrListLocal.add(leBook);
			String product = commonDao.findVisionVariableValue("RA_STG_PRODUCT_TABLE");
			arrListLocal.add(product);
			String service = commonDao.findVisionVariableValue("RA_STG_SERVICE_TABLE");
			arrListLocal.add(service);
			String blLink = commonDao.findVisionVariableValue("RA_CONCESSION_BL_LINK");
			arrListLocal.add(blLink);
			return arrListLocal;
		}catch(Exception ex){
			ex.printStackTrace();
			//logger.error("Exception in getting the Page load values.", ex);
			return null;
		}
	}

	@Override
	protected AbstractDao<TransLineHeaderVb> getScreenDao() {
		return productLineConfigDao;
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
		exceptionCode = doValidate(vObject);
		if(exceptionCode.getErrorCode() == Constants.ERRONEOUS_OPERATION){
			return exceptionCode;
		}	
		Boolean pendFlag = true;
		List<TransLineGLVb> transGlList = new ArrayList<TransLineGLVb>();
		List<TransLineSbuVb> transSbuList = new ArrayList<TransLineSbuVb>();
		List<TransLineHeaderVb> collTemp = productLineConfigDao.getQueryResults(vObject,intStatus);
		if (collTemp.size() == 0){
			intStatus = 0;
			collTemp = productLineConfigDao.getQueryResults(vObject,intStatus);
			pendFlag = false;
		}
		if(collTemp.size() == 0){
			exceptionCode = CommonUtils.getResultObject(productLineConfigDao.getServiceDesc(), 16, "Query", "");
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
			}else {
				transGlList = transLinesGlDao.getTransGLDetails(vObject,Constants.STATUS_DELETE);
				transSbuList = transLinesSbuDao.getTransSbuDetails(vObject,Constants.STATUS_DELETE);
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
	//@Transactional(rollbackForClassName={"com.vision.exception.RuntimeCustomException"})
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
			logger.error("Modify Exception " + rex.getCode().getErrorMsg());
//			//logger.error( ((vObject==null)? "vObject is Null":vObject.toString()));
			exceptionCode = rex.getCode();
			exceptionCode.setOtherInfo(clonedObject);
			return exceptionCode;
		}
	}
	public ExceptionCode approve(List<TransLineHeaderVb> translinelst){
		ExceptionCode exceptionCode  = null;
		DeepCopy<TransLineHeaderVb> deepCopy = new DeepCopy<TransLineHeaderVb>();
		TransLineHeaderVb clonedObject = null;
		try{
			exceptionCode = doValidate(translinelst.get(0));
			if(exceptionCode.getErrorCode() == Constants.ERRONEOUS_OPERATION){
				return exceptionCode;
			}
			for(TransLineHeaderVb vObject : translinelst) {
				setVerifReqDeleteType(vObject);
				clonedObject = deepCopy.copy(vObject);
				exceptionCode = doValidate(vObject);
				if(exceptionCode != null && exceptionCode.getErrorMsg() != ""){
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
			}
			exceptionCode.setOtherInfo(translinelst);
			return exceptionCode;
		}catch(RuntimeCustomException rex){
			//logger.error("Approve Exception " + rex.getCode().getErrorMsg());
			exceptionCode = rex.getCode();
			exceptionCode.setOtherInfo(clonedObject);
			return exceptionCode;
		}
	}
	public ExceptionCode reject(List<TransLineHeaderVb> translinelst){
		ExceptionCode exceptionCode  = null;
		DeepCopy<TransLineHeaderVb> deepCopy = new DeepCopy<TransLineHeaderVb>();
		TransLineHeaderVb clonedObject = null;
		try{
			exceptionCode = doValidate(translinelst.get(0));
			if(exceptionCode.getErrorCode() == Constants.ERRONEOUS_OPERATION){
				return exceptionCode;
			}
			for(TransLineHeaderVb vObject : translinelst) {
				setVerifReqDeleteType(vObject);
				clonedObject = deepCopy.copy(vObject);
				exceptionCode = getScreenDao().doRejectForTransaction(vObject);
				exceptionCode.setOtherInfo(vObject);
				getScreenDao().fetchMakerVerifierNames(vObject);
			}
			exceptionCode.setOtherInfo(translinelst);
			return exceptionCode;
		}catch(RuntimeCustomException rex){
			//logger.error("Reject Exception " + rex.getCode().getErrorMsg());
			exceptionCode = rex.getCode();
			exceptionCode.setOtherInfo(clonedObject);
			return exceptionCode;
		}
	}
	@Override
	protected List<ReviewResultVb> transformToReviewResults(List<TransLineHeaderVb> approvedCollection, List<TransLineHeaderVb> pendingCollection) {
		ArrayList collTemp = getPageLoadValues();
		if(pendingCollection != null)
			getScreenDao().fetchMakerVerifierNames(pendingCollection.get(0));
		if(approvedCollection != null)
			getScreenDao().fetchMakerVerifierNames(approvedCollection.get(0));
			
		ArrayList<ReviewResultVb> lResult = new ArrayList<ReviewResultVb>();
		ReviewResultVb lCountry = new ReviewResultVb("Country",
        		(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getCountry(),
                (approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getCountry(),
                		(!pendingCollection.get(0).getCountry().equals(approvedCollection.get(0).getCountry())));
        lResult.add(lCountry);
        ReviewResultVb lLeBook = new ReviewResultVb("Le Book",
        		(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getLeBook(),
                (approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getLeBook(),
                		(!pendingCollection.get(0).getLeBook().equals(approvedCollection.get(0).getLeBook())));
        lResult.add(lLeBook);
		ReviewResultVb transLineId = new ReviewResultVb("Trans Line Id",(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getTransLineId(),
				(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getTransLineId(),
						(!pendingCollection.get(0).getTransLineId().equals(approvedCollection.get(0).getTransLineId())));
		lResult.add(transLineId);
		ReviewResultVb transLineDesc = new ReviewResultVb("Trans Line Decription",(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getTransLineDescription(),
				(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getTransLineDescription(),
						(!pendingCollection.get(0).getTransLineDescription().equals(approvedCollection.get(0).getTransLineDescription())));
		lResult.add(transLineDesc);
		ReviewResultVb transLineType = new ReviewResultVb("Trans Line Type",(pendingCollection == null || pendingCollection.isEmpty())?"":getAtDescription((List<AlphaSubTabVb>) collTemp.get(2),pendingCollection.get(0).getTransLineType()),
				(approvedCollection == null || approvedCollection.isEmpty())?"":getAtDescription((List<AlphaSubTabVb>) collTemp.get(2),approvedCollection.get(0).getTransLineType()),
							(!pendingCollection.get(0).getTransLineType().equals(approvedCollection.get(0).getTransLineType())));
			lResult.add(transLineType);
			
		ReviewResultVb prodLineType = new ReviewResultVb("Trans Line Sub Type",(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getTransLineSubTypeDesc(),
				(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getTransLineSubTypeDesc(),
						(!pendingCollection.get(0).getTransLineSubTypeDesc().equals(approvedCollection.get(0).getTransLineSubTypeDesc())));
		lResult.add(prodLineType);
		
		ReviewResultVb transLineGrp = new ReviewResultVb("Trans Line Group",(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getTransLineGrpDesc(),
				(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getTransLineGrpDesc(),
						(!pendingCollection.get(0).getTransLineGrpDesc().equals(approvedCollection.get(0).getTransLineGrpDesc())));
		lResult.add(transLineGrp);
		
		ReviewResultVb busVert = new ReviewResultVb("Business Vertical",(pendingCollection == null || pendingCollection.isEmpty())?"":ValidationUtil.isValid(pendingCollection.get(0).getBusinessVerticalDesc()) ? pendingCollection.get(0).getBusinessVerticalDesc() : pendingCollection.get(0).getBusinessVertical(),
				(approvedCollection == null || approvedCollection.isEmpty())?"":ValidationUtil.isValid(approvedCollection.get(0).getBusinessVerticalDesc()) ? approvedCollection.get(0).getBusinessVerticalDesc() : approvedCollection.get(0).getBusinessVertical(),
						(!pendingCollection.get(0).getBusinessVerticalDesc().equals(approvedCollection.get(0).getBusinessVerticalDesc())));
		lResult.add(busVert);
		
		ReviewResultVb extFreq = new ReviewResultVb("Extraction Frequency",(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getExtractionFrequencyDesc(),
				(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getExtractionFrequencyDesc(),
						(!pendingCollection.get(0).getExtractionFrequencyDesc().equals(approvedCollection.get(0).getExtractionFrequencyDesc())));
		lResult.add(extFreq);
		
		ReviewResultVb extDay = new ReviewResultVb("Extraction Day",(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getExtractionMonthDay(),
				(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getExtractionMonthDay(),
						(!((ValidationUtil.isValid(pendingCollection.get(0).getExtractionMonthDay()))? pendingCollection.get(0).getExtractionMonthDay() : "").equals((ValidationUtil.isValid(approvedCollection.get(0).getExtractionMonthDay()))? approvedCollection.get(0).getExtractionMonthDay() : "")));
		lResult.add(extDay);
		
		ReviewResultVb trgtStgTable = new ReviewResultVb("Target Staging Table",(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getTargetStgTableId(),
				(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getTargetStgTableId(),
						(!pendingCollection.get(0).getTargetStgTableId().equals(approvedCollection.get(0).getTargetStgTableId())));
		lResult.add(trgtStgTable);
		
		if(pendingCollection != null && pendingCollection.size() > 0) {
			if(ValidationUtil.isValid(pendingCollection.get(0).getTransLineType()) && "S".equalsIgnoreCase(pendingCollection.get(0).getTransLineType())) {
				ReviewResultVb channel = new ReviewResultVb("Channels",(pendingCollection == null || pendingCollection.isEmpty())?"":ValidationUtil.isValid(pendingCollection.get(0).getChannelIdDesc()) ? pendingCollection.get(0).getChannelIdDesc() : pendingCollection.get(0).getChannelId(),
						(approvedCollection == null || approvedCollection.isEmpty())?"":ValidationUtil.isValid(approvedCollection.get(0).getChannelIdDesc()) ? approvedCollection.get(0).getChannelIdDesc() : approvedCollection.get(0).getChannelId(),
								(!pendingCollection.get(0).getChannelId().equals(approvedCollection.get(0).getChannelId())));
				lResult.add(channel);
				
				ReviewResultVb department = new ReviewResultVb("Department",(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getDepartmentDesc(),
						(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getDepartmentDesc(),
								(!pendingCollection.get(0).getDepartmentDesc().equals(approvedCollection.get(0).getDepartmentDesc())));
				lResult.add(department);
			}
		}
			
		
		
		ReviewResultVb lPrrofileStatus = new ReviewResultVb("Status",
				(pendingCollection == null || pendingCollection.isEmpty())?"":getNtDescription((List<NumSubTabVb>) collTemp.get(0),pendingCollection.get(0).getTransLineStatus()),
		        (approvedCollection == null || approvedCollection.isEmpty())?"":getNtDescription((List<NumSubTabVb>) collTemp.get(0),approvedCollection.get(0).getTransLineStatus()),
		        (pendingCollection.get(0).getTransLineStatus() != approvedCollection.get(0).getTransLineStatus()));
		lResult.add(lPrrofileStatus);  
		
		ReviewResultVb lRecordIndicator = new ReviewResultVb("Record Indicator",(pendingCollection == null || pendingCollection.isEmpty())?"":getNtDescription((List<NumSubTabVb>) collTemp.get(1),pendingCollection.get(0).getRecordIndicator()),
		            (approvedCollection == null || approvedCollection.isEmpty())?"":getNtDescription((List<NumSubTabVb>) collTemp.get(1),approvedCollection.get(0).getRecordIndicator()),
		            (pendingCollection.get(0).getRecordIndicator() != approvedCollection.get(0).getRecordIndicator()));
		lResult.add(lRecordIndicator);
		
		
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
	@Override
	public ExceptionCode doValidate(TransLineHeaderVb vObject){
		ExceptionCode exceptionCode = new ExceptionCode();
		String operation = vObject.getActionType();
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
	public ExceptionCode getAllQueryPopupResult(TransLineHeaderVb queryPopupObj){
		ExceptionCode exceptionCode = new ExceptionCode();
		try{
			setVerifReqDeleteType(queryPopupObj);
			doFormateDataForQuery(queryPopupObj);
			
			exceptionCode = doValidate(queryPopupObj);
			if(exceptionCode.getErrorCode() == Constants.ERRONEOUS_OPERATION){
				return exceptionCode;
			}	
			List<SmartSearchVb> transLineSmartSearchOpt = new ArrayList<>();
			List<SmartSearchVb> businessLineSmartSearchOpt = new ArrayList<>();
			List<SmartSearchVb> feeLineSmartSearchOpt = new ArrayList<>();
			queryPopupObj.setTotalRows(0);
			if(queryPopupObj.getSmartSearchOpt() != null && queryPopupObj.getSmartSearchOpt().size() > 0) {
				transLineSmartSearchOpt = queryPopupObj.getSmartSearchOpt().stream().filter(vb -> ValidationUtil.isValid(vb.getScreenName()) && "TRANSLINE".equalsIgnoreCase(vb.getScreenName())).collect(Collectors.toList());
				businessLineSmartSearchOpt = queryPopupObj.getSmartSearchOpt().stream().filter(vb -> ValidationUtil.isValid(vb.getScreenName()) && "BUSINESSLINE".equalsIgnoreCase(vb.getScreenName())).collect(Collectors.toList());
				feeLineSmartSearchOpt = queryPopupObj.getSmartSearchOpt().stream().filter(vb -> ValidationUtil.isValid(vb.getScreenName()) && "FEELINE".equalsIgnoreCase(vb.getScreenName())).collect(Collectors.toList());
			}
			//queryPopupObj.setSmartSearchOpt(transLineSmartSearchOpt);
			List<TransLineHeaderVb> arrListResult = getScreenDao().getQueryPopupResults(queryPopupObj);
			
			if(arrListResult!= null && arrListResult.size() > 0) {
				CopyOnWriteArrayList<TransLineHeaderVb>  transLineListCopy = new CopyOnWriteArrayList<TransLineHeaderVb>(arrListResult);
				for (TransLineHeaderVb transLineHeaderVb : transLineListCopy) {
					BusinessLineHeaderVb vObject = new BusinessLineHeaderVb();
					vObject.setSmartSearchOpt(businessLineSmartSearchOpt);
					vObject.setCountry(transLineHeaderVb.getCountry());
					vObject.setLeBook(transLineHeaderVb.getLeBook());
					vObject.setTransLineId(transLineHeaderVb.getTransLineId());
					vObject.setActionType(queryPopupObj.getActionType());
					ExceptionCode exceptionCodeBus = (ExceptionCode) businessLineConfigWb.getAllQueryPopupResult(vObject);
					List<BusinessLineHeaderVb> businessLinelst =  new ArrayList<>();
					if(exceptionCodeBus.getErrorCode() == Constants.SUCCESSFUL_OPERATION) {
						businessLinelst = (List<BusinessLineHeaderVb>) exceptionCodeBus.getResponse();
					
						if (businessLinelst != null && businessLinelst.size() > 0) {
							CopyOnWriteArrayList<BusinessLineHeaderVb>  businessLinelstCopy = new CopyOnWriteArrayList<BusinessLineHeaderVb>(businessLinelst);
							for (BusinessLineHeaderVb businessLineVb : businessLinelstCopy) {
								FeesConfigHeaderVb feesConfigHeaderVb = new FeesConfigHeaderVb();
								feesConfigHeaderVb.setSmartSearchOpt(feeLineSmartSearchOpt);
								feesConfigHeaderVb.setCountry(businessLineVb.getCountry());
								feesConfigHeaderVb.setLeBook(businessLineVb.getLeBook());
								feesConfigHeaderVb.setTransLineId(businessLineVb.getTransLineId());
								feesConfigHeaderVb.setBusinessLineId(businessLineVb.getBusinessLineId());
								feesConfigHeaderVb.setActionType(queryPopupObj.getActionType());
								ExceptionCode exceptionCodeFee = (ExceptionCode) feesConfigHeadersWb.getAllQueryPopupResult(feesConfigHeaderVb);
								List<FeesConfigHeaderVb> feesConfiglst = (List<FeesConfigHeaderVb>) exceptionCodeFee.getResponse();
								
								if(feeLineSmartSearchOpt != null && feeLineSmartSearchOpt.size() > 0 && (feesConfiglst == null || feesConfiglst.isEmpty() )) {
									businessLinelstCopy.remove(businessLineVb);
								}else if(feesConfiglst != null && feesConfiglst.size() > 0) {
									List<FeesConfigHeaderVb> topFiveFeeConfigList = feesConfiglst.stream().limit(5).collect(Collectors.toList());
									businessLineVb.setFeeConfiglst(topFiveFeeConfigList);
								}
							}
							if(((businessLineSmartSearchOpt != null && businessLineSmartSearchOpt.size() > 0)
									|| (feeLineSmartSearchOpt != null && feeLineSmartSearchOpt.size() > 0 ))
									&& (businessLinelstCopy == null || businessLinelstCopy.isEmpty() )) {
								transLineListCopy.remove(transLineHeaderVb);
							}else if(businessLinelstCopy != null && businessLinelstCopy.size() > 0) {
								transLineHeaderVb.setBusinessLinelst(businessLinelstCopy);
							}
						} else {
							if(((businessLineSmartSearchOpt != null && businessLineSmartSearchOpt.size() > 0)
									|| (feeLineSmartSearchOpt != null && feeLineSmartSearchOpt.size() > 0 ))
									&& (businessLinelst == null || businessLinelst.isEmpty() )) {
								transLineListCopy.remove(transLineHeaderVb);
							}
						}
					}
					if(((businessLineSmartSearchOpt != null && businessLineSmartSearchOpt.size() > 0)
							|| (feeLineSmartSearchOpt != null && feeLineSmartSearchOpt.size() > 0 ))
							&& (businessLinelst == null || businessLinelst.isEmpty() )) {
						transLineListCopy.remove(transLineHeaderVb);
					}
				}
				exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
				exceptionCode.setResponse(transLineListCopy);
				/*if(transLineListCopy != null && transLineListCopy.size() > 0 )
					queryPopupObj.setTotalRows(transLineListCopy.size());*/
				if(transLineListCopy != null && (businessLineSmartSearchOpt != null || feeLineSmartSearchOpt != null) 
						&& (businessLineSmartSearchOpt.size() > 0 || feeLineSmartSearchOpt.size() > 0))
					queryPopupObj.setTotalRows(transLineListCopy.size());
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
	public ExceptionCode reviewRecordNew(TransLineHeaderVb vObject){
		ExceptionCode exceptionCode = null;
		List<ReviewResultVb> list = null;
		List<TransLineHeaderVb> approvedCollection = new ArrayList();
		List<TransLineHeaderVb> pendingCollection = new ArrayList();
		try{
			exceptionCode = doValidate(vObject);
			if(exceptionCode.getErrorCode() == Constants.ERRONEOUS_OPERATION){
				return exceptionCode;
			}
			if(ValidationUtil.isValid(vObject.getTransLineType()) && "P".equalsIgnoreCase(vObject.getTransLineType())) {
				approvedCollection = getScreenDao().getQueryResults(vObject,Constants.STATUS_ZERO);
				pendingCollection = getScreenDao().getQueryResults(vObject,Constants.STATUS_PENDING);
			} else {
				approvedCollection = serviceLineConfigDao.getQueryResults(vObject,Constants.STATUS_ZERO);
				if(approvedCollection != null && approvedCollection.size() > 0) {
					List<TransLineChannelVb> transChannelList = new ArrayList<TransLineChannelVb>();
					transChannelList = transLinesChannelDao.getTransChannelDetails(vObject, Constants.STATUS_ZERO);
					if(transChannelList != null && transChannelList.size() > 0) {
						StringJoiner channelArrJoiner = new StringJoiner(",");
						StringJoiner channelDescArrJoiner = new StringJoiner(",");
						transChannelList.forEach(transChannel -> {
							channelArrJoiner.add(transChannel.getChannelId());
							channelDescArrJoiner.add(transChannel.getChannelIdDesc());
						});
						approvedCollection.get(0).setChannelId(channelArrJoiner.toString());
						approvedCollection.get(0).setChannelIdDesc(channelDescArrJoiner.toString());
					}
				}
				pendingCollection = serviceLineConfigDao.getQueryResults(vObject,Constants.STATUS_PENDING);
				if(pendingCollection != null && pendingCollection.size() > 0) {
					List<TransLineChannelVb> transChannelList = new ArrayList<TransLineChannelVb>();
					transChannelList = transLinesChannelDao.getTransChannelDetails(vObject, Constants.STATUS_PENDING);
					if(transChannelList != null && transChannelList.size() > 0) {
						StringJoiner channelArrJoiner = new StringJoiner(",");
						StringJoiner channelDescArrJoiner = new StringJoiner(",");
						transChannelList.forEach(transChannel -> {
							channelArrJoiner.add(transChannel.getChannelId());
							channelDescArrJoiner.add(transChannel.getChannelIdDesc());
						});
						pendingCollection.get(0).setChannelId(channelArrJoiner.toString());
						pendingCollection.get(0).setChannelIdDesc(channelDescArrJoiner.toString());
					}
				}
			}
			List<TransLineSbuVb> transSbuList = new ArrayList<TransLineSbuVb>();
			transSbuList = transLinesSbuDao.getTransSbuDetails(vObject,Constants.STATUS_ZERO);
			if(transSbuList != null && transSbuList.size() > 0) {
				StringJoiner sbuArrJoiner = new StringJoiner(",");
				StringJoiner sbuDescArrJoiner = new StringJoiner(",");
				transSbuList.forEach(transSbu -> {
					sbuArrJoiner.add(transSbu.getBusinessVertical());
					sbuDescArrJoiner.add(transSbu.getBusinessVerticalDesc());
				});
				approvedCollection.get(0).setBusinessVertical(sbuArrJoiner.toString());
				approvedCollection.get(0).setBusinessVerticalDesc(sbuDescArrJoiner.toString());
			}
			transSbuList = new ArrayList<TransLineSbuVb>();
			transSbuList = transLinesSbuDao.getTransSbuDetails(vObject,Constants.STATUS_PENDING);
			if(transSbuList != null && transSbuList.size() > 0) {
				StringJoiner sbuArrJoiner = new StringJoiner(",");
				StringJoiner sbuDescArrJoiner = new StringJoiner(",");
				transSbuList.forEach(transSbu -> {
					sbuArrJoiner.add(transSbu.getBusinessVertical());
					sbuDescArrJoiner.add(transSbu.getBusinessVerticalDesc());
				});
				pendingCollection.get(0).setBusinessVertical(sbuArrJoiner.toString());
				pendingCollection.get(0).setBusinessVerticalDesc(sbuDescArrJoiner.toString());
			}
			list =  transformToReviewResults(approvedCollection,pendingCollection);
			exceptionCode = CommonUtils.getResultObject(getScreenDao().getServiceDesc(), 1, "Query", "");
			exceptionCode.setOtherInfo(vObject);
			exceptionCode.setResponse(list);
			return exceptionCode;
		}catch(Exception ex){
			exceptionCode = CommonUtils.getResultObject(getScreenDao().getServiceDesc(), 16, "Query", "");
			exceptionCode.setResponse(list);
			return exceptionCode;
		}
	}
}
