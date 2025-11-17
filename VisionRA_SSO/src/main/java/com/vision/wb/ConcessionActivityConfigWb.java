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
import com.vision.dao.ConcessionActivityFilterDao;
import com.vision.dao.ConcessionActivityHeaderDao;
import com.vision.dao.NumSubTabDao;
import com.vision.exception.ExceptionCode;
import com.vision.exception.RuntimeCustomException;
import com.vision.util.CommonUtils;
import com.vision.util.Constants;
import com.vision.util.DeepCopy;
import com.vision.vb.AlphaSubTabVb;
import com.vision.vb.CommonVb;
import com.vision.vb.ConcessionActivityConfigHeaderVb;
import com.vision.vb.ConcessionActivityFilterVb;
import com.vision.vb.ReviewResultVb;
@Component
public class ConcessionActivityConfigWb extends AbstractDynaWorkerBean<ConcessionActivityConfigHeaderVb> {
	@Autowired
	private ConcessionActivityHeaderDao concessionActivityHeadersDao;
	@Autowired
	private ConcessionActivityFilterDao concessionFilterDao;
	@Autowired
	private NumSubTabDao numSubTabDao;
	@Autowired
	private AlphaSubTabDao alphaSubTabDao;
	@Autowired
	private CommonDao commonDao;
	
	public static Logger logger = LoggerFactory.getLogger(ConcessionActivityConfigWb.class);
	
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
			collTemp = alphaSubTabDao.findActiveAlphaSubTabsByAlphaTab(2013); // agg func
			arrListLocal.add(collTemp);
			collTemp = alphaSubTabDao.findActiveAlphaSubTabsByAlphaTab(7075); // time series 
			arrListLocal.add(collTemp);
			collTemp = alphaSubTabDao.findActiveAlphaSubTabsByAlphaTab(7076); // activity type
			arrListLocal.add(collTemp);
			collTemp = alphaSubTabDao.findActiveAlphaSubTabsByAlphaTab(7401); // condition operation
			arrListLocal.add(collTemp);
			String country = commonDao.findVisionVariableValue("DEFAULT_COUNTRY");
			arrListLocal.add(country);
			String leBook = commonDao.findVisionVariableValue("DEFAULT_LE_BOOK");
			arrListLocal.add(leBook);
			return arrListLocal;
		}catch(Exception ex){
			ex.printStackTrace();
			//logger.error("Exception in getting the Page load values.", ex);
			return null;
		}
	}

	@Override
	protected AbstractDao<ConcessionActivityConfigHeaderVb> getScreenDao() {
		return concessionActivityHeadersDao;
	}
	@Override
	protected void setAtNtValues(ConcessionActivityConfigHeaderVb vObject) {
		// TODO Auto-generated method stub
		
	}
	@Override
	protected void setVerifReqDeleteType(ConcessionActivityConfigHeaderVb vObject) {
		ArrayList<CommonVb> lCommVbList =(ArrayList<CommonVb>) commonDao.findVerificationRequiredAndStaticDelete("RA_MST_CONCESSION_ACTIVITY");
		vObject.setStaticDelete(lCommVbList.get(0).isStaticDelete());
		vObject.setVerificationRequired(lCommVbList.get(0).isVerificationRequired());
	}
	@Override
	public ExceptionCode getQueryResults(ConcessionActivityConfigHeaderVb vObject){
		int intStatus = 1;
		ExceptionCode exceptionCode = new ExceptionCode();
		setVerifReqDeleteType(vObject);
		List<ConcessionActivityFilterVb> ConcessionActivityConfigFilterlst = new ArrayList<ConcessionActivityFilterVb>();

		List<ConcessionActivityConfigHeaderVb> collTemp = concessionActivityHeadersDao.getQueryResults(vObject,intStatus);
		if (collTemp.size() == 0){
			intStatus = 0;
			collTemp = concessionActivityHeadersDao.getQueryResults(vObject,intStatus);
		}
		if(collTemp.size() == 0){
			exceptionCode = CommonUtils.getResultObject(getScreenDao().getServiceDesc(), 16, "Query", "");
			exceptionCode.setOtherInfo(vObject);
			return exceptionCode;
		}else{
			for(ConcessionActivityConfigHeaderVb concessionActivityConfigHeaderVb : collTemp) {
				ConcessionActivityConfigFilterlst = concessionFilterDao.getConcessionFilters(concessionActivityConfigHeaderVb,intStatus);
				List<ConcessionActivityFilterVb> joinTableLst = new ArrayList<>();
				List<ConcessionActivityFilterVb> filterTableLst = new ArrayList<>();
				if(ConcessionActivityConfigFilterlst != null && !ConcessionActivityConfigFilterlst.isEmpty()) {
					for(ConcessionActivityFilterVb concessionFilterVb : ConcessionActivityConfigFilterlst) {
						if("JOIN".equalsIgnoreCase(concessionFilterVb.getConditionType()))
							joinTableLst.add(concessionFilterVb);
						else if("FILTER".equalsIgnoreCase(concessionFilterVb.getConditionType()))
							filterTableLst.add(concessionFilterVb);	
						
					}
					
					
					concessionActivityConfigHeaderVb.setConcessionJoinLst(joinTableLst);
					concessionActivityConfigHeaderVb.setConcessionFilterLst(filterTableLst);
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
	 * public ExceptionCode insertRecord(ConcessionActivityConfigHeaderVb vObject){ ExceptionCode
	 * exceptionCode = null; DeepCopy<ConcessionActivityConfigHeaderVb> deepCopy = new
	 * DeepCopy<ConcessionActivityConfigHeaderVb>(); ConcessionActivityConfigHeaderVb clonedObject = null; try {
	 * setAtNtValues(vObject); setVerifReqDeleteType(vObject); clonedObject =
	 * deepCopy.copy(vObject); doFormateData(vObject); exceptionCode =
	 * doValidate(vObject); if(exceptionCode!=null &&
	 * exceptionCode.getErrorMsg()!=""){ return exceptionCode; }
	 * if(!vObject.isVerificationRequired()){ exceptionCode =
	 * getScreenDao().doInsertApprRecord(vObject);
	 * concessionActivityHeadersDao.callLineMergeProc(vObject.getCountry(),vObject.getLeBook
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
	public ExceptionCode modifyRecord(ConcessionActivityConfigHeaderVb vObject){
		ExceptionCode exceptionCode  = null;
		DeepCopy<ConcessionActivityConfigHeaderVb> deepCopy = new DeepCopy<ConcessionActivityConfigHeaderVb>();
		ConcessionActivityConfigHeaderVb clonedObject = null;
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
				concessionActivityHeadersDao.callLineMergeProc(vObject.getCountry(),vObject.getLeBook(),vObject.getBusinessLineId(),"PR_RA_MST_FEE_LINE");
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
	@Override
	public ExceptionCode approve(ConcessionActivityConfigHeaderVb vObject){
		ExceptionCode exceptionCode  = null;
		DeepCopy<ConcessionActivityConfigHeaderVb> deepCopy = new DeepCopy<ConcessionActivityConfigHeaderVb>();
		ConcessionActivityConfigHeaderVb clonedObject = null;
		try{
			setVerifReqDeleteType(vObject);
			clonedObject = deepCopy.copy(vObject);
			exceptionCode = doValidate(vObject);
			if(exceptionCode.getErrorCode() == Constants.ERRONEOUS_OPERATION){
				return exceptionCode;
			}
			exceptionCode = getScreenDao().doApproveForTransaction(vObject,vObject.isStaticDelete());
			concessionActivityHeadersDao.callLineMergeProc(vObject.getCountry(),vObject.getLeBook(),vObject.getActivityId(),"PR_RA_MST_FEE_LINE");
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
	protected List<ReviewResultVb> transformToReviewResults(List<ConcessionActivityConfigHeaderVb> approvedCollection, List<ConcessionActivityConfigHeaderVb> pendingCollection) {
		ArrayList collTemp = getPageLoadValues();
		if(pendingCollection != null)
			getScreenDao().fetchMakerVerifierNames(pendingCollection.get(0));
		if(approvedCollection != null)
			getScreenDao().fetchMakerVerifierNames(approvedCollection.get(0));
			
		ArrayList<ReviewResultVb> lResult = new ArrayList<ReviewResultVb>();
		 
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
		
		ReviewResultVb concessionId = new ReviewResultVb("Activity Id",
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: pendingCollection.get(0).getActivityId(),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: approvedCollection.get(0).getActivityId(),
				(!pendingCollection.get(0).getActivityId().equals(approvedCollection.get(0).getActivityId())));
		lResult.add(concessionId);
		
		ReviewResultVb concessionDesc = new ReviewResultVb("Activity Description",
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: pendingCollection.get(0).getActivityDesc(),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: approvedCollection.get(0).getActivityDesc(),
				(!pendingCollection.get(0).getActivityDesc()
						.equals(approvedCollection.get(0).getActivityDesc())));
		lResult.add(concessionDesc);
		
		ReviewResultVb transLineId = new ReviewResultVb("TransLine Id",(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getTransLineId(),
				(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getTransLineId(),
						(!pendingCollection.get(0).getTransLineId().equals(approvedCollection.get(0).getTransLineId())));
		lResult.add(transLineId);
		
		ReviewResultVb concessionActivityDesc = new ReviewResultVb("Activity Type",		
		(pendingCollection == null || pendingCollection.isEmpty()) ? ""
				: pendingCollection.get(0).getActivityTypeDesc(),
		(approvedCollection == null || approvedCollection.isEmpty()) ? ""
				: approvedCollection.get(0).getActivityTypeDesc(),
		(!pendingCollection.get(0).getActivityTypeDesc().equals(approvedCollection.get(0).getActivityTypeDesc())));
		lResult.add(concessionActivityDesc);
		
		ReviewResultVb aggFun = new ReviewResultVb("Aggregation Function",
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: getAtDescription((List<AlphaSubTabVb>) collTemp.get(3),
								pendingCollection.get(0).getAggFun()),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: getAtDescription((List<AlphaSubTabVb>) collTemp.get(3),
								approvedCollection.get(0).getAggFun()),
				(!pendingCollection.get(0).getAggFun().equals(approvedCollection.get(0).getAggFun())));
		lResult.add(aggFun);
		
		ReviewResultVb timeSer  = new ReviewResultVb("Time Series Type",
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: getAtDescription((List<AlphaSubTabVb>) collTemp.get(4),
								pendingCollection.get(0).getTimeSeriesType()),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: getAtDescription((List<AlphaSubTabVb>) collTemp.get(4),
								approvedCollection.get(0).getTimeSeriesType()),
				(!pendingCollection.get(0).getTimeSeriesType().equals(approvedCollection.get(0).getTimeSeriesType())));
		lResult.add(timeSer);
		
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
						: pendingCollection.get(0).getActivityStatusDesc(),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: approvedCollection.get(0).getActivityStatusDesc(),
						(!pendingCollection.get(0).getActivityStatusDesc() .equals( approvedCollection.get(0)
						.getActivityStatusDesc())));
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
		ReviewResultVb lDateCreation = new ReviewResultVb("Date Creation",(pendingCollection == null || pendingCollection.isEmpty()) ? ""
				: pendingCollection.get(0).getDateCreation(),
		(approvedCollection == null || approvedCollection.isEmpty()) ? ""
				: approvedCollection.get(0).getDateCreation(),
		(!pendingCollection.get(0).getDateCreation().equals((approvedCollection.get(0).getDateCreation()))));
		lResult.add(lDateCreation);
		return lResult;
	}
}
