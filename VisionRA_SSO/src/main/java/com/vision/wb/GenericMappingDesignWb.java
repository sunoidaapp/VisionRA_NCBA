package com.vision.wb;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.vision.dao.AbstractDao;
import com.vision.dao.CommonDao;
import com.vision.dao.GenericMappingDesignDao;
import com.vision.exception.ExceptionCode;
import com.vision.exception.RuntimeCustomException;
import com.vision.util.CommonUtils;
import com.vision.util.Constants;
import com.vision.util.ValidationUtil;
import com.vision.vb.CommonVb;
import com.vision.vb.NumSubTabVb;
import com.vision.vb.ReviewResultVb;
import com.vision.vb.TemplateNameGBMVb;

@Component
public class GenericMappingDesignWb extends AbstractWorkerBean<TemplateNameGBMVb>{
	
	@Autowired
	private GenericMappingDesignDao genericMappingDesignDao;
		public static Logger logger = LoggerFactory.getLogger(GenericMappingDesignWb.class);
		@Autowired
		private CommonDao commonDao;
	
	public ArrayList getPageLoadValues(){
		List collTemp = null;
		ArrayList<Object> arrListLocal = new ArrayList<Object>();
		try{
			collTemp = getNumSubTabDao().findActiveNumSubTabsByNumTab(1);//Status
			arrListLocal.add(collTemp);
			collTemp = getNumSubTabDao().findActiveNumSubTabsByNumTab(7);//Record Indicator
			arrListLocal.add(collTemp);
			collTemp = getAlphaSubTabDao().findActiveAlphaSubTabsByAlphaTab(1079);//Acquisition_Process_Type
			arrListLocal.add(collTemp);
			collTemp = getAlphaSubTabDao().findActiveAlphaSubTabsByAlphaTab(1092);//GenricBLD Execution_Type
			arrListLocal.add(collTemp);
			collTemp = getAlphaSubTabDao().findActiveAlphaSubTabsByAlphaTab(1096);//GenricBLD Fatal_Flag
			arrListLocal.add(collTemp);
			collTemp = getAlphaSubTabDao().findActiveAlphaSubTabsByAlphaTab(1098);//GenricBLD Log_Text
			arrListLocal.add(collTemp);
			collTemp = genericMappingDesignDao.getExecutionSequenceRedirect();
			arrListLocal.add(collTemp);
			String country = commonDao.findVisionVariableValue("DEFAULT_COUNTRY");
			arrListLocal.add(country);
			String leBook = commonDao.findVisionVariableValue("DEFAULT_LE_BOOK");
			arrListLocal.add(leBook);
			return arrListLocal;
		}catch(Exception ex){
			ex.printStackTrace();
			logger.error("Exception in getting the Page load values.", ex);
			return null;
		}
	}
	
	public ArrayList getPageLoadValuesForProgram(TemplateNameGBMVb templateNameGBMVb){
		List collTemp = null;
		ArrayList<Object> arrListLocal = new ArrayList<Object>();
		try{
			collTemp = getNumSubTabDao().findActiveNumSubTabsByNumTab(1);
			arrListLocal.add(collTemp);
			collTemp = getAlphaSubTabDao().findActiveAlphaSubTabsByAlphaTab(1092);
			arrListLocal.add(collTemp);
			collTemp = getAlphaSubTabDao().findActiveAlphaSubTabsByAlphaTab(1096);
			arrListLocal.add(collTemp);
			collTemp = getAlphaSubTabDao().findActiveAlphaSubTabsByAlphaTab(1098);
			arrListLocal.add(collTemp);
			collTemp = genericMappingDesignDao.getExecutionSequence(templateNameGBMVb);
			arrListLocal.add(collTemp);
			collTemp = getNumSubTabDao().findActiveNumSubTabsByNumTab(1);
			arrListLocal.add(collTemp);
			collTemp = getNumSubTabDao().findActiveNumSubTabsByNumTab(7);
			arrListLocal.add(collTemp);
			collTemp = genericMappingDesignDao.getStakeHolderData(templateNameGBMVb.getProgram());
			arrListLocal.add(collTemp);
			return arrListLocal;
		}catch(Exception ex){
			ex.printStackTrace();
			logger.error("Exception in getting the Page load values for Template Design.", ex);
			return null;
		}
	}
	//@Override
//	protected List<ReviewResultVb> transformToReviewResults(List<TemplateNameGBMVb> approvedCollection, List<TemplateNameGBMVb> pendingCollection) {
//		ArrayList collTemp = getPageLoadValues();
//		ResourceBundle rsb = CommonUtils.getResourceManger();
//		if(pendingCollection != null)
//			getScreenDao().fetchMakerVerifierNames(pendingCollection.get(0));
//		if(approvedCollection != null)
//			getScreenDao().fetchMakerVerifierNames(approvedCollection.get(0).);
//		ArrayList<ReviewResultVb> lResult = new ArrayList<ReviewResultVb>();
//		ReviewResultVb lTemplateName = new ReviewResultVb(rsb.getString("templateName"),
//			(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getTemplateName(),
//			(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0)..getTemplateName());
//		lResult.add(lTemplateName);
//		ReviewResultVb lGeneralDescription = new ReviewResultVb(rsb.getString("generalDescription"),
//			(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getGeneralDescription(),
//			(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0)..getGeneralDescription());
//		lResult.add(lGeneralDescription);
//		ReviewResultVb lFeedCategory = new ReviewResultVb(rsb.getString("feedCategory"),
//				(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getFeedCategory(),
//				(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0)..getFeedCategory());
//		lResult.add(lFeedCategory);
//		ReviewResultVb lStagingName = new ReviewResultVb(rsb.getString("stagingName"),
//				(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getFeedStgName(),
//				(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0)..getFeedStgName());
//		lResult.add(lStagingName);
//		ReviewResultVb lViewName = new ReviewResultVb(rsb.getString("viewName"),
//				(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getViewName(),
//				(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0)..getViewName());
//		lResult.add(lViewName);
//		ReviewResultVb lEffectiveDate = new ReviewResultVb(rsb.getString("effectiveDate"),
//				(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getEffectiveDate(),
//				(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0)..getEffectiveDate());
//		lResult.add(lEffectiveDate);
//		ReviewResultVb lFreezeStatus = new ReviewResultVb(rsb.getString("defaultAcquProcessType"),
//				(pendingCollection == null || pendingCollection.isEmpty())?"":getAtDescription((List<AlphaSubTabVb>)collTemp.get(2),pendingCollection.get(0).getDefaultAcquProcessType()),
//				(approvedCollection == null || approvedCollection.isEmpty())?"":getAtDescription((List<AlphaSubTabVb>)collTemp.get(2),approvedCollection.get(0)..getDefaultAcquProcessType()));
//		lResult.add(lFreezeStatus);
//		ReviewResultVb lProgram = new ReviewResultVb(rsb.getString("program"),
//				(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getProgram(),
//				(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0)..getProgram());
//		lResult.add(lProgram);
//		ReviewResultVb lProcessSequence = new ReviewResultVb("Process Sequence",
//				(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getProcessSequence(),
//				(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0)..getProcessSequence());
//		lResult.add(lProcessSequence);
//		ReviewResultVb lFilePattern = new ReviewResultVb("File Pattern",
//				(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getFilePattern(),
//				(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0)..getFilePattern());
//		lResult.add(lFilePattern);
//		ReviewResultVb lExcelFilePattern = new ReviewResultVb("Excel File Pattern",
//				(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getExcelFilePattern(),
//				(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0)..getExcelFilePattern());
//		lResult.add(lExcelFilePattern);
//		ReviewResultVb lExcelTemplateID = new ReviewResultVb("Excel Template ID",
//				(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getExcelTemplateId(),
//				(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0)..getExcelTemplateId());
//		lResult.add(lExcelTemplateID);
//		ReviewResultVb lTemplateStatus = new ReviewResultVb(rsb.getString("status"),
//			(pendingCollection == null || pendingCollection.isEmpty())?"":getNtDescription((List<NumSubTabVb>) collTemp.get(0),pendingCollection.get(0).getTemplateStatus()),
//			(approvedCollection == null || approvedCollection.isEmpty())?"":getNtDescription((List<NumSubTabVb>) collTemp.get(0),approvedCollection.get(0)..getTemplateStatus()));
//		lResult.add(lTemplateStatus);   
//		ReviewResultVb lRecordIndicator = new ReviewResultVb(rsb.getString("recordIndicator"),(pendingCollection == null || pendingCollection.isEmpty())?"":getNtDescription((List<NumSubTabVb>) collTemp.get(1),pendingCollection.get(0).getRecordIndicator()),
//			(approvedCollection == null || approvedCollection.isEmpty())?"":getNtDescription((List<NumSubTabVb>) collTemp.get(1),approvedCollection.get(0)..getRecordIndicator()));
//		lResult.add(lRecordIndicator);
//		ReviewResultVb lMaker = new ReviewResultVb(rsb.getString("maker"),(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getMaker() == 0?"":pendingCollection.get(0).getMakerName(),
//				(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0)..getMaker() == 0?"":approvedCollection.get(0)..getMakerName());
//		lResult.add(lMaker);
//		ReviewResultVb lVerifier = new ReviewResultVb(rsb.getString("verifier"),(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getVerifier() == 0?"":pendingCollection.get(0).getVerifierName(),
//				(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0)..getVerifier() == 0?"":approvedCollection.get(0)..getVerifierName());
//		lResult.add(lVerifier);
//		ReviewResultVb lDateLastModified = new ReviewResultVb(rsb.getString("dateLastModified"),(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getDateLastModified(),
//			(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0)..getDateLastModified());
//		lResult.add(lDateLastModified);
//		ReviewResultVb lDateCreation = new ReviewResultVb(rsb.getString("dateCreation"),(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getDateCreation(),
//			(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0)..getDateCreation());
//		lResult.add(lDateCreation);
//		return lResult;
//	}
	@Override
	protected void setVerifReqDeleteType(TemplateNameGBMVb vObject){
		ArrayList<CommonVb> lCommVbList =(ArrayList<CommonVb>) getCommonDao().findVerificationRequiredAndStaticDelete("GENERIC_BUILD_MAPPINGS");
		vObject.setStaticDelete(lCommVbList.get(0).isStaticDelete());
		vObject.setVerificationRequired(lCommVbList.get(0).isVerificationRequired());
	}
	public <E> ExceptionCode getTemplateNamesPopUp(TemplateNameGBMVb vObject){
		int intStatus = 1;
		setVerifReqDeleteType(vObject);
		List<TemplateNameGBMVb> collTemp = genericMappingDesignDao.getTemplatePopUp(vObject, 0);
		if (collTemp.size() == 0){
			intStatus = 0;
			collTemp = genericMappingDesignDao.getTemplatePopUp(vObject,0);
		}
		if(collTemp.size() == 0){
			ExceptionCode exceptionCode = CommonUtils.getResultObject(getScreenDao().getServiceDesc(), 16, "Query", "");
			exceptionCode.setOtherInfo(vObject);
			return exceptionCode;
		}else{
			doSetDesctiptionsAfterQuery((TemplateNameGBMVb) ((ArrayList<E>)collTemp).get(0));
			ExceptionCode exceptionCode = CommonUtils.getResultObject(getScreenDao().getServiceDesc(), 1, "Query", "");
			getScreenDao().fetchMakerVerifierNames((TemplateNameGBMVb) ((ArrayList<E>)collTemp).get(0));
			/*((ArrayList<E>)collTemp).get(0).setVerificationRequired(vObject.isVerificationRequired());
			((ArrayList<E>)collTemp).get(0).setStaticDelete(vObject.isStaticDelete());*/
			exceptionCode.setOtherInfo(((ArrayList<E>)collTemp).get(0));
			exceptionCode.setResponse(collTemp);
			return exceptionCode;
		}
	}
	
	public ExceptionCode getQueryResults(TemplateNameGBMVb vObject){
		int intStatus = 1;
		setVerifReqDeleteType(vObject);
		List<TemplateNameGBMVb> collTemp = genericMappingDesignDao.getQueryResults(vObject, Constants.SUCCESSFUL_OPERATION);
		if (collTemp.size() == 0){
			intStatus = 0;
			collTemp = genericMappingDesignDao.getQueryResults(vObject,Constants.STATUS_ZERO);
		}
		if(collTemp.size() == 0){
			ExceptionCode exceptionCode = CommonUtils.getResultObject(getScreenDao().getServiceDesc(), 16, "Query", "");
			exceptionCode.setOtherInfo(vObject);
			return exceptionCode;
		}else{
			doSetDesctiptionsAfterQuery((TemplateNameGBMVb) ((ArrayList<TemplateNameGBMVb>)collTemp).get(0));
			ExceptionCode exceptionCode = CommonUtils.getResultObject(getScreenDao().getServiceDesc(), 1, "Query", "");
			getScreenDao().fetchMakerVerifierNames((TemplateNameGBMVb) ((ArrayList<TemplateNameGBMVb>)collTemp).get(0));
			/*((ArrayList<E>)collTemp).get(0).setVerificationRequired(vObject.isVerificationRequired());
			((ArrayList<E>)collTemp).get(0).setStaticDelete(vObject.isStaticDelete());*/
			exceptionCode.setOtherInfo(((ArrayList<TemplateNameGBMVb>)collTemp).get(0));
			exceptionCode.setResponse(collTemp);
			return exceptionCode;
		}
	}
	public ExceptionCode modifyRecordAD(TemplateNameGBMVb vObject){
		ExceptionCode exceptionCode  = null;
		try
		{
			exceptionCode = genericMappingDesignDao.doinsertMainRecord(vObject);
			exceptionCode.setOtherInfo(vObject);
			exceptionCode.setErrorMsg("Record reflected successfully");
			return exceptionCode;
		}catch(RuntimeCustomException rex){
			logger.error("Insert Exception " + rex.getCode().getErrorMsg());
			logger.error( ((vObject==null)? "vObject is Null":vObject.toString()));
			exceptionCode = rex.getCode();
			return exceptionCode;
		}
	}
	public List<ReviewResultVb> compareRecord(TemplateNameGBMVb vObject,String verNo1,String verNo2){
		try{
			List<TemplateNameGBMVb> compList1 = genericMappingDesignDao.getComplist(vObject,verNo1);
			List<TemplateNameGBMVb> compList2 = genericMappingDesignDao.getComplist(vObject,verNo2);
			return transformToCompareResults(compList1,compList2);
//			return exceptionCode;
		}catch(Exception ex){
			return null;
		}
	}
	public List<ReviewResultVb> compareExeSeq(TemplateNameGBMVb vObject){
		int ver1 = vObject.getVersionNo();
		int ver2 = vObject.getCurrentVersion();
		try{
			List<TemplateNameGBMVb> compList1 = genericMappingDesignDao.getCompareExeSeqlist(vObject,ver1);
			List<TemplateNameGBMVb> compList2 = genericMappingDesignDao.getCompareExeSeqlist(vObject,ver2);
			compList1.get(0).setVersionNo(ver1);
			compList2.get(0).setVersionNo(ver2);
			return transformToExeSeqCompareResults(compList2.get(0),compList1.get(0));
//			return exceptionCode;
		}catch(Exception ex){
			ex.printStackTrace();
			return null;
		}
	}
	protected  List<ReviewResultVb> transformToCompareResults(List<TemplateNameGBMVb> compList1, List<TemplateNameGBMVb> compList2) {
		ResourceBundle rsb=CommonUtils.getResourceManger();
		ArrayList<ReviewResultVb> lResult = new ArrayList<ReviewResultVb>();
		ArrayList<ReviewResultVb> lResultTemp = new ArrayList<ReviewResultVb>();
//		HashMap<K, V>
		TreeSet<Integer> sortedExeSeqList = new TreeSet<Integer>();
		int biggerListSize = 0;
		if(compList1!=null && compList1.size()>0 && compList2!=null && compList2.size()>0)
			biggerListSize = (compList1.size() > compList2.size())?compList1.size():compList2.size();
			
			
		for(int index=0;index<biggerListSize;index++){
			try{
			//	System.out.println("compList1.get(index).getExecutionSequence():"+compList1.get(index).getExecutionSequence());
				if(ValidationUtil.isValid(compList1.get(index).getExecutionSequence()))
					
					sortedExeSeqList.add(Integer.parseInt(compList1.get(index).getExecutionSequence()));
			}catch(Exception e){e.printStackTrace();}
			try{
				//System.out.println("compList2.get(index).getExecutionSequence():"+compList2.get(index).getExecutionSequence());
				if(ValidationUtil.isValid(compList2.get(index).getExecutionSequence()))
					
					sortedExeSeqList.add(Integer.parseInt(compList2.get(index).getExecutionSequence()));
			}catch(Exception e){e.printStackTrace();}
		} 
		
		Iterator<Integer> it = sortedExeSeqList.iterator();
		while(it.hasNext()){
			int currentVal = it.next();
			TemplateNameGBMVb list1 = new TemplateNameGBMVb();
			TemplateNameGBMVb list2 = new TemplateNameGBMVb();
			for(TemplateNameGBMVb templateNameGBMVb:compList1){
				if(currentVal==Integer.parseInt(templateNameGBMVb.getExecutionSequence())){
					list1 = templateNameGBMVb;
				}
			}
			for(TemplateNameGBMVb templateNameGBMVb:compList2){
				if(currentVal==Integer.parseInt(templateNameGBMVb.getExecutionSequence())){
					list2 = templateNameGBMVb;
				}
			}
			
			ReviewResultVb lExeSeq = null;
			if(ValidationUtil.isValid(list1.getExecutionSequence()) && ValidationUtil.isValid(list2.getExecutionSequence()))
				lExeSeq = new ReviewResultVb(rsb.getString("executionSequence"),list1.getExecutionSequence(),list2.getExecutionSequence());
			else if(ValidationUtil.isValid(list1.getExecutionSequence()))
				lExeSeq = new ReviewResultVb(rsb.getString("executionSequence"),list1.getExecutionSequence(),"");
			else if(ValidationUtil.isValid(list2.getExecutionSequence()))
				lExeSeq = new ReviewResultVb(rsb.getString("executionSequence"),"",list2.getExecutionSequence());
			lResult.add(lExeSeq);
		}
		
		return lResult;
	}
	
	protected List<ReviewResultVb> transformToExeSeqCompareResults(TemplateNameGBMVb approvedCollection, TemplateNameGBMVb pendingCollection) {
		ResourceBundle rsb = CommonUtils.getResourceManger();
		ArrayList<ReviewResultVb> lResult = new ArrayList<ReviewResultVb>();
		
		ReviewResultVb lVerNo = new ReviewResultVb(rsb.getString("alertVersionNo"),
				(pendingCollection!=null && ValidationUtil.isValid(pendingCollection.getVersionNo()))?String.valueOf(pendingCollection.getVersionNo()):"",
						(approvedCollection!=null && ValidationUtil.isValid(approvedCollection.getVersionNo())  && approvedCollection.getVersionNo()!=9999)?String.valueOf(approvedCollection.getVersionNo()):"Current Version");
		lResult.add(lVerNo);
		ReviewResultVb lExeType = new ReviewResultVb(rsb.getString("executionType"),
				(pendingCollection!=null && ValidationUtil.isValid(pendingCollection.getExecutionType()))?pendingCollection.getExecutionType():"",
						(approvedCollection!=null && ValidationUtil.isValid(approvedCollection.getExecutionType()))?approvedCollection.getExecutionType():"");
			lResult.add(lExeType);
		ReviewResultVb lCommitFlag = new ReviewResultVb(rsb.getString("commitFlag"),
				(pendingCollection!=null && ValidationUtil.isValid(pendingCollection.getCommitFlag()))?pendingCollection.getCommitFlag():"",
						(approvedCollection!=null && ValidationUtil.isValid(approvedCollection.getCommitFlag()))?approvedCollection.getCommitFlag():"");
		lResult.add(lCommitFlag);
		ReviewResultVb lMainQuery = new ReviewResultVb(rsb.getString("mainQuery"),
				(pendingCollection!=null && ValidationUtil.isValid(pendingCollection.getMainQuery()))?pendingCollection.getMainQuery():"",
				(approvedCollection!=null && ValidationUtil.isValid(approvedCollection.getMainQuery()))?approvedCollection.getMainQuery():"");
		lResult.add(lMainQuery);
		ReviewResultVb lDML1 = new ReviewResultVb("DML1",
				(pendingCollection!=null && ValidationUtil.isValid(pendingCollection.getDml1()))?pendingCollection.getDml1():"",
				(approvedCollection!=null && ValidationUtil.isValid(approvedCollection.getDml1()))?approvedCollection.getDml1():"");
		if(!pendingCollection.getDml1().equals(approvedCollection.getDml1()))
			lDML1.setDataDiffer(true);
		lResult.add(lDML1);
		ReviewResultVb lDML2 = new ReviewResultVb("DML2",
				(pendingCollection!=null && ValidationUtil.isValid(pendingCollection.getDml2()))?pendingCollection.getDml2():"",
				(approvedCollection!=null && ValidationUtil.isValid(approvedCollection.getDml2()))?approvedCollection.getDml2():"");
			if(!pendingCollection.getDml2().equals(approvedCollection.getDml2()))
				lDML2.setDataDiffer(true);
		lResult.add(lDML2);
		ReviewResultVb lDML3 = new ReviewResultVb("DML3",
				(pendingCollection!=null && ValidationUtil.isValid(pendingCollection.getDml3()))?pendingCollection.getDml3():"",
				(approvedCollection!=null && ValidationUtil.isValid(approvedCollection.getDml3()))?approvedCollection.getDml3():"");
			if(!pendingCollection.getDml3().equals(approvedCollection.getDml3()))
				lDML3.setDataDiffer(true);
		lResult.add(lDML3);
		ReviewResultVb lDML4 = new ReviewResultVb("DML4",
				(pendingCollection!=null && ValidationUtil.isValid(pendingCollection.getDml4()))?pendingCollection.getDml4():"",
				(approvedCollection!=null && ValidationUtil.isValid(approvedCollection.getDml4()))?approvedCollection.getDml4():"");
			if(!pendingCollection.getDml4().equals(approvedCollection.getDml4()))
				lDML4.setDataDiffer(true);
		lResult.add(lDML4);
		ReviewResultVb lDML5 = new ReviewResultVb("DML5",
				(pendingCollection!=null && ValidationUtil.isValid(pendingCollection.getDml5()))?pendingCollection.getDml5():"",
				(approvedCollection!=null && ValidationUtil.isValid(approvedCollection.getDml5()))?approvedCollection.getDml5():"");
			if(!pendingCollection.getDml5().equals(approvedCollection.getDml5()))
				lDML5.setDataDiffer(true);
		lResult.add(lDML5);
		ReviewResultVb lDML6 = new ReviewResultVb("DML6",
				(pendingCollection!=null && ValidationUtil.isValid(pendingCollection.getDml6()))?pendingCollection.getDml6():"",
				(approvedCollection!=null && ValidationUtil.isValid(approvedCollection.getDml6()))?approvedCollection.getDml6():"");
			if(!pendingCollection.getDml6().equals(approvedCollection.getDml6()))
				lDML6.setDataDiffer(true);
		lResult.add(lDML6);
		ReviewResultVb lDML7 = new ReviewResultVb("DML7",
				(pendingCollection!=null && ValidationUtil.isValid(pendingCollection.getDml7()))?pendingCollection.getDml7():"",
				(approvedCollection!=null && ValidationUtil.isValid(approvedCollection.getDml7()))?approvedCollection.getDml7():"");
			if(!pendingCollection.getDml7().equals(approvedCollection.getDml7()))
				lDML7.setDataDiffer(true);
		lResult.add(lDML7);
		ReviewResultVb lDML8 = new ReviewResultVb("DML8",
				(pendingCollection!=null && ValidationUtil.isValid(pendingCollection.getDml8()))?pendingCollection.getDml8():"",
				(approvedCollection!=null && ValidationUtil.isValid(approvedCollection.getDml8()))?approvedCollection.getDml8():"");
			if(!pendingCollection.getDml8().equals(approvedCollection.getDml8()))
				lDML8.setDataDiffer(true);
		lResult.add(lDML8);
		ReviewResultVb lDML9 = new ReviewResultVb("DML9",
				(pendingCollection!=null && ValidationUtil.isValid(pendingCollection.getDml9()))?pendingCollection.getDml9():"",
				(approvedCollection!=null && ValidationUtil.isValid(approvedCollection.getDml9()))?approvedCollection.getDml9():"");
			if(!pendingCollection.getDml9().equals(approvedCollection.getDml9()))
				lDML9.setDataDiffer(true);
		lResult.add(lDML9);
		ReviewResultVb lDML10 = new ReviewResultVb("DML10",
				(pendingCollection!=null && ValidationUtil.isValid(pendingCollection.getDml10()))?pendingCollection.getDml10():"",
				(approvedCollection!=null && ValidationUtil.isValid(approvedCollection.getDml10()))?approvedCollection.getDml10():"");
			if(!pendingCollection.getDml10().equals(approvedCollection.getDml10()))
				lDML10.setDataDiffer(true);
		lResult.add(lDML10);
		
		ReviewResultVb lFatalFlag1 = new ReviewResultVb("FATAL FLAG 1",
				(pendingCollection!=null && ValidationUtil.isValid(pendingCollection.getFatalFlag1()))?pendingCollection.getFatalFlag1():"",
				(approvedCollection!=null && ValidationUtil.isValid(approvedCollection.getFatalFlag1()))?approvedCollection.getFatalFlag1():"");
			if(!pendingCollection.getFatalFlag1().equals(approvedCollection.getFatalFlag1()))
				lFatalFlag1.setDataDiffer(true);
		lResult.add(lFatalFlag1);
		ReviewResultVb lFatalFlag2 = new ReviewResultVb("FATAL FLAG 2",
				(pendingCollection!=null && ValidationUtil.isValid(pendingCollection.getFatalFlag2()))?pendingCollection.getFatalFlag2():"",
				(approvedCollection!=null && ValidationUtil.isValid(approvedCollection.getFatalFlag2()))?approvedCollection.getFatalFlag2():"");
			if(!pendingCollection.getFatalFlag2().equals(approvedCollection.getFatalFlag2()))
				lFatalFlag2.setDataDiffer(true);
		lResult.add(lFatalFlag2);
		ReviewResultVb lFatalFlag3 = new ReviewResultVb("FATAL FLAG 3",
				(pendingCollection!=null && ValidationUtil.isValid(pendingCollection.getFatalFlag3()))?pendingCollection.getFatalFlag3():"",
				(approvedCollection!=null && ValidationUtil.isValid(approvedCollection.getFatalFlag3()))?approvedCollection.getFatalFlag3():"");
			if(!pendingCollection.getFatalFlag3().equals(approvedCollection.getFatalFlag3()))
				lFatalFlag3.setDataDiffer(true);
		lResult.add(lFatalFlag3);
		ReviewResultVb lFatalFlag4 = new ReviewResultVb("FATAL FLAG 4",
				(pendingCollection!=null && ValidationUtil.isValid(pendingCollection.getFatalFlag4()))?pendingCollection.getFatalFlag4():"",
				(approvedCollection!=null && ValidationUtil.isValid(approvedCollection.getFatalFlag4()))?approvedCollection.getFatalFlag4():"");
			if(!pendingCollection.getFatalFlag4().equals(approvedCollection.getFatalFlag4()))
				lFatalFlag4.setDataDiffer(true);
		lResult.add(lFatalFlag4);
		ReviewResultVb lFatalFlag5 = new ReviewResultVb("FATAL FLAG 5",
				(pendingCollection!=null && ValidationUtil.isValid(pendingCollection.getFatalFlag5()))?pendingCollection.getFatalFlag5():"",
				(approvedCollection!=null && ValidationUtil.isValid(approvedCollection.getFatalFlag5()))?approvedCollection.getFatalFlag5():"");
			if(!pendingCollection.getFatalFlag5().equals(approvedCollection.getFatalFlag5()))
				lFatalFlag5.setDataDiffer(true);
		lResult.add(lFatalFlag5);
		ReviewResultVb lFatalFlag6 = new ReviewResultVb("FATAL FLAG 6",
				(pendingCollection!=null && ValidationUtil.isValid(pendingCollection.getFatalFlag6()))?pendingCollection.getFatalFlag6():"",
				(approvedCollection!=null && ValidationUtil.isValid(approvedCollection.getFatalFlag6()))?approvedCollection.getFatalFlag6():"");
			if(!pendingCollection.getFatalFlag6().equals(approvedCollection.getFatalFlag6()))
				lFatalFlag6.setDataDiffer(true);
		lResult.add(lFatalFlag6);
		ReviewResultVb lFatalFlag7 = new ReviewResultVb("FATAL FLAG 7",
				(pendingCollection!=null && ValidationUtil.isValid(pendingCollection.getFatalFlag7()))?pendingCollection.getFatalFlag7():"",
				(approvedCollection!=null && ValidationUtil.isValid(approvedCollection.getFatalFlag7()))?approvedCollection.getFatalFlag7():"");
			if(!pendingCollection.getFatalFlag7().equals(approvedCollection.getFatalFlag7()))
				lFatalFlag7.setDataDiffer(true);
		lResult.add(lFatalFlag7);
		ReviewResultVb lFatalFlag8 = new ReviewResultVb("FATAL FLAG 8",
				(pendingCollection!=null && ValidationUtil.isValid(pendingCollection.getFatalFlag8()))?pendingCollection.getFatalFlag8():"",
				(approvedCollection!=null && ValidationUtil.isValid(approvedCollection.getFatalFlag8()))?approvedCollection.getFatalFlag8():"");
			if(!pendingCollection.getFatalFlag8().equals(approvedCollection.getFatalFlag8()))
				lFatalFlag8.setDataDiffer(true);
		lResult.add(lFatalFlag8);
		ReviewResultVb lFatalFlag9 = new ReviewResultVb("FATAL FLAG 9",
				(pendingCollection!=null && ValidationUtil.isValid(pendingCollection.getFatalFlag9()))?pendingCollection.getFatalFlag9():"",
				(approvedCollection!=null && ValidationUtil.isValid(approvedCollection.getFatalFlag9()))?approvedCollection.getFatalFlag9():"");
			if(!pendingCollection.getFatalFlag9().equals(approvedCollection.getFatalFlag9()))
				lFatalFlag9.setDataDiffer(true);
		lResult.add(lFatalFlag9);
		ReviewResultVb lFatalFlag10 = new ReviewResultVb("FATAL FLAG 10",
				(pendingCollection!=null && ValidationUtil.isValid(pendingCollection.getFatalFlag10()))?pendingCollection.getFatalFlag10():"",
				(approvedCollection!=null && ValidationUtil.isValid(approvedCollection.getFatalFlag10()))?approvedCollection.getFatalFlag10():"");
			if(!pendingCollection.getFatalFlag10().equals(approvedCollection.getFatalFlag10()))
				lFatalFlag10.setDataDiffer(true);
		lResult.add(lFatalFlag10);
		
		ReviewResultVb lLogTextFlag1 = new ReviewResultVb("LOG TEXT FLAG 1",
				(pendingCollection!=null && ValidationUtil.isValid(pendingCollection.getLogTextFlag1()))?pendingCollection.getLogTextFlag1():"",
				(approvedCollection!=null && ValidationUtil.isValid(approvedCollection.getLogTextFlag1()))?approvedCollection.getLogTextFlag1():"");
			if(!pendingCollection.getLogTextFlag1().equals(approvedCollection.getLogTextFlag1()))
				lLogTextFlag1.setDataDiffer(true);
		lResult.add(lLogTextFlag1);
		ReviewResultVb lLogTextFlag2 = new ReviewResultVb("LOG TEXT FLAG 2",
				(pendingCollection!=null && ValidationUtil.isValid(pendingCollection.getLogTextFlag2()))?pendingCollection.getLogTextFlag2():"",
				(approvedCollection!=null && ValidationUtil.isValid(approvedCollection.getLogTextFlag2()))?approvedCollection.getLogTextFlag2():"");
			if(!pendingCollection.getLogTextFlag2().equals(approvedCollection.getLogTextFlag2()))
				lLogTextFlag2.setDataDiffer(true);
		lResult.add(lLogTextFlag2);
		ReviewResultVb lLogTextFlag3 = new ReviewResultVb("LOG TEXT FLAG 3",
				(pendingCollection!=null && ValidationUtil.isValid(pendingCollection.getLogTextFlag3()))?pendingCollection.getLogTextFlag3():"",
				(approvedCollection!=null && ValidationUtil.isValid(approvedCollection.getLogTextFlag3()))?approvedCollection.getLogTextFlag3():"");
			if(!pendingCollection.getLogTextFlag3().equals(approvedCollection.getLogTextFlag3()))
				lLogTextFlag3.setDataDiffer(true);
		lResult.add(lLogTextFlag3);
		ReviewResultVb lLogTextFlag4 = new ReviewResultVb("LOG TEXT FLAG 4",
				(pendingCollection!=null && ValidationUtil.isValid(pendingCollection.getLogTextFlag4()))?pendingCollection.getLogTextFlag4():"",
				(approvedCollection!=null && ValidationUtil.isValid(approvedCollection.getLogTextFlag4()))?approvedCollection.getLogTextFlag4():"");
			if(!pendingCollection.getLogTextFlag4().equals(approvedCollection.getLogTextFlag4()))
				lLogTextFlag4.setDataDiffer(true);
		lResult.add(lLogTextFlag4);
		ReviewResultVb lLogTextFlag5 = new ReviewResultVb("LOG TEXT FLAG 5",
				(pendingCollection!=null && ValidationUtil.isValid(pendingCollection.getLogTextFlag5()))?pendingCollection.getLogTextFlag5():"",
				(approvedCollection!=null && ValidationUtil.isValid(approvedCollection.getLogTextFlag5()))?approvedCollection.getLogTextFlag5():"");
			if(!pendingCollection.getLogTextFlag5().equals(approvedCollection.getLogTextFlag5()))
				lLogTextFlag5.setDataDiffer(true);
		lResult.add(lLogTextFlag5);
		ReviewResultVb lLogTextFlag6 = new ReviewResultVb("LOG TEXT FLAG 6",
				(pendingCollection!=null && ValidationUtil.isValid(pendingCollection.getLogTextFlag6()))?pendingCollection.getLogTextFlag6():"",
				(approvedCollection!=null && ValidationUtil.isValid(approvedCollection.getLogTextFlag6()))?approvedCollection.getLogTextFlag6():"");
			if(!pendingCollection.getLogTextFlag6().equals(approvedCollection.getLogTextFlag6()))
				lLogTextFlag6.setDataDiffer(true);
		lResult.add(lLogTextFlag6);
		ReviewResultVb lLogTextFlag7 = new ReviewResultVb("LOG TEXT FLAG 7",
				(pendingCollection!=null && ValidationUtil.isValid(pendingCollection.getLogTextFlag7()))?pendingCollection.getLogTextFlag7():"",
				(approvedCollection!=null && ValidationUtil.isValid(approvedCollection.getLogTextFlag7()))?approvedCollection.getLogTextFlag7():"");
			if(!pendingCollection.getLogTextFlag7().equals(approvedCollection.getLogTextFlag7()))
				lLogTextFlag7.setDataDiffer(true);
		lResult.add(lLogTextFlag7);
		ReviewResultVb lLogTextFlag8 = new ReviewResultVb("LOG TEXT FLAG 8",
				(pendingCollection!=null && ValidationUtil.isValid(pendingCollection.getLogTextFlag8()))?pendingCollection.getLogTextFlag8():"",
				(approvedCollection!=null && ValidationUtil.isValid(approvedCollection.getLogTextFlag8()))?approvedCollection.getLogTextFlag8():"");
			if(!pendingCollection.getLogTextFlag8().equals(approvedCollection.getLogTextFlag8()))
				lLogTextFlag8.setDataDiffer(true);
		lResult.add(lLogTextFlag8);
		ReviewResultVb lLogTextFlag9 = new ReviewResultVb("LOG TEXT FLAG 9",
				(pendingCollection!=null && ValidationUtil.isValid(pendingCollection.getLogTextFlag9()))?pendingCollection.getLogTextFlag9():"",
				(approvedCollection!=null && ValidationUtil.isValid(approvedCollection.getLogTextFlag9()))?approvedCollection.getLogTextFlag9():"");
			if(!pendingCollection.getLogTextFlag9().equals(approvedCollection.getLogTextFlag9()))
				lLogTextFlag9.setDataDiffer(true);
		lResult.add(lLogTextFlag9);
		ReviewResultVb lLogTextFlag10 = new ReviewResultVb("LOG TEXT FLAG 10",
				(pendingCollection!=null && ValidationUtil.isValid(pendingCollection.getLogTextFlag10()))?pendingCollection.getLogTextFlag10():"",
				(approvedCollection!=null && ValidationUtil.isValid(approvedCollection.getLogTextFlag10()))?approvedCollection.getLogTextFlag10():"");
			if(!pendingCollection.getLogTextFlag10().equals(approvedCollection.getLogTextFlag10()))
				lLogTextFlag10.setDataDiffer(true);
		lResult.add(lLogTextFlag10);
		
		ReviewResultVb lDMLStatusFlag1 = new ReviewResultVb("DML STATUS 1",
				(pendingCollection!=null && ValidationUtil.isValid(pendingCollection.getDmlStatus1()))?pendingCollection.getDmlStatus1():"",
				(approvedCollection!=null && ValidationUtil.isValid(approvedCollection.getDmlStatus1()))?approvedCollection.getDmlStatus1():"");
			if(!pendingCollection.getDmlStatus1().equals(approvedCollection.getDmlStatus1()))
				lDMLStatusFlag1.setDataDiffer(true);
		lResult.add(lDMLStatusFlag1);
		ReviewResultVb lDMLStatusFlag2 = new ReviewResultVb("DML STATUS 2",
				(pendingCollection!=null && ValidationUtil.isValid(pendingCollection.getDmlStatus2()))?pendingCollection.getDmlStatus2():"",
				(approvedCollection!=null && ValidationUtil.isValid(approvedCollection.getDmlStatus2()))?approvedCollection.getDmlStatus2():"");
			if(!pendingCollection.getDmlStatus2().equals(approvedCollection.getDmlStatus2()))
				lDMLStatusFlag2.setDataDiffer(true);
		lResult.add(lDMLStatusFlag2);
		ReviewResultVb lDMLStatusFlag3 = new ReviewResultVb("DML STATUS 3",
				(pendingCollection!=null && ValidationUtil.isValid(pendingCollection.getDmlStatus3()))?pendingCollection.getDmlStatus3():"",
				(approvedCollection!=null && ValidationUtil.isValid(approvedCollection.getDmlStatus3()))?approvedCollection.getDmlStatus3():"");
			if(!pendingCollection.getDmlStatus3().equals(approvedCollection.getDmlStatus3()))
				lDMLStatusFlag3.setDataDiffer(true);
		lResult.add(lDMLStatusFlag3);
		ReviewResultVb lDMLStatusFlag4 = new ReviewResultVb("DML STATUS 4",
				(pendingCollection!=null && ValidationUtil.isValid(pendingCollection.getDmlStatus4()))?pendingCollection.getDmlStatus4():"",
				(approvedCollection!=null && ValidationUtil.isValid(approvedCollection.getDmlStatus4()))?approvedCollection.getDmlStatus4():"");
			if(!pendingCollection.getDmlStatus4().equals(approvedCollection.getDmlStatus4()))
				lDMLStatusFlag4.setDataDiffer(true);
		lResult.add(lDMLStatusFlag4);
		ReviewResultVb lDMLStatusFlag5 = new ReviewResultVb("DML STATUS 5",
				(pendingCollection!=null && ValidationUtil.isValid(pendingCollection.getDmlStatus5()))?pendingCollection.getDmlStatus5():"",
				(approvedCollection!=null && ValidationUtil.isValid(approvedCollection.getDmlStatus5()))?approvedCollection.getDmlStatus5():"");
			if(!pendingCollection.getDmlStatus5().equals(approvedCollection.getDmlStatus5()))
				lDMLStatusFlag5.setDataDiffer(true);
		lResult.add(lDMLStatusFlag5);
		ReviewResultVb lDMLStatusFlag6 = new ReviewResultVb("DML STATUS 6",
				(pendingCollection!=null && ValidationUtil.isValid(pendingCollection.getDmlStatus6()))?pendingCollection.getDmlStatus6():"",
				(approvedCollection!=null && ValidationUtil.isValid(approvedCollection.getDmlStatus6()))?approvedCollection.getDmlStatus6():"");
			if(!pendingCollection.getDmlStatus6().equals(approvedCollection.getDmlStatus6()))
				lDMLStatusFlag6.setDataDiffer(true);
		lResult.add(lDMLStatusFlag6);
		ReviewResultVb lDMLStatusFlag7 = new ReviewResultVb("DML STATUS 7",
				(pendingCollection!=null && ValidationUtil.isValid(pendingCollection.getDmlStatus7()))?pendingCollection.getDmlStatus7():"",
				(approvedCollection!=null && ValidationUtil.isValid(approvedCollection.getDmlStatus7()))?approvedCollection.getDmlStatus7():"");
			if(!pendingCollection.getDmlStatus7().equals(approvedCollection.getDmlStatus7()))
				lDMLStatusFlag7.setDataDiffer(true);
		lResult.add(lDMLStatusFlag7);
		ReviewResultVb lDMLStatusFlag8 = new ReviewResultVb("DML STATUS 8",
				(pendingCollection!=null && ValidationUtil.isValid(pendingCollection.getDmlStatus8()))?pendingCollection.getDmlStatus8():"",
				(approvedCollection!=null && ValidationUtil.isValid(approvedCollection.getDmlStatus8()))?approvedCollection.getDmlStatus8():"");
			if(!pendingCollection.getDmlStatus8().equals(approvedCollection.getDmlStatus8()))
				lDMLStatusFlag8.setDataDiffer(true);
		lResult.add(lDMLStatusFlag8);
		ReviewResultVb lDMLStatusFlag9 = new ReviewResultVb("DML STATUS 9",
				(pendingCollection!=null && ValidationUtil.isValid(pendingCollection.getDmlStatus9()))?pendingCollection.getDmlStatus9():"",
				(approvedCollection!=null && ValidationUtil.isValid(approvedCollection.getDmlStatus9()))?approvedCollection.getDmlStatus9():"");
			if(!pendingCollection.getDmlStatus9().equals(approvedCollection.getDmlStatus9()))
				lDMLStatusFlag9.setDataDiffer(true);
		lResult.add(lDMLStatusFlag9);
		ReviewResultVb lDMLStatusFlag10 = new ReviewResultVb("DML STATUS 10",
				(pendingCollection!=null && ValidationUtil.isValid(pendingCollection.getDmlStatus10()))?pendingCollection.getDmlStatus10():"",
				(approvedCollection!=null && ValidationUtil.isValid(approvedCollection.getDmlStatus10()))?approvedCollection.getDmlStatus10():"");
			if(!pendingCollection.getDmlStatus10().equals(approvedCollection.getDmlStatus10()))
				lDMLStatusFlag10.setDataDiffer(true);
		lResult.add(lDMLStatusFlag10);
		
		return lResult;
	}
	
	public List<ReviewResultVb> compareDML(TemplateNameGBMVb vObject,int DML){
		int ver1 = vObject.getVersionNo();
		int ver2 = vObject.getCurrentVersion();
		try{
			List<TemplateNameGBMVb> compList1 = genericMappingDesignDao.getCompareDMLlist(vObject,ver1,DML);
			List<TemplateNameGBMVb> compList2 = genericMappingDesignDao.getCompareDMLlist(vObject,ver2,DML);
			return transformToDMLCompareResults(compList2.get(0),compList1.get(0));
//			return exceptionCode;
		}catch(Exception ex){
			return null;
		}
	}
	protected List<ReviewResultVb> transformToDMLCompareResults(TemplateNameGBMVb approvedCollection, TemplateNameGBMVb pendingCollection) {
		ResourceBundle rsb = CommonUtils.getResourceManger();
		ArrayList<ReviewResultVb> lResult = new ArrayList<ReviewResultVb>();
		
		ReviewResultVb lDMLQuery = new ReviewResultVb("DML Query",
				(pendingCollection!=null && ValidationUtil.isValid(pendingCollection.getDml()))?String.valueOf(pendingCollection.getDml()):"",
						(approvedCollection!=null && ValidationUtil.isValid(approvedCollection.getVersionNo()))?String.valueOf(approvedCollection.getDml()):"");
		lResult.add(lDMLQuery);
		ReviewResultVb lFatalFlag = new ReviewResultVb("Fatal Flag",
				(pendingCollection!=null && ValidationUtil.isValid(pendingCollection.getFatalFlag()))?pendingCollection.getFatalFlag():"",
						(approvedCollection!=null && ValidationUtil.isValid(approvedCollection.getFatalFlag()))?approvedCollection.getFatalFlag():"");
			lResult.add(lFatalFlag);
		ReviewResultVb lLogTextFlag = new ReviewResultVb("Log Text Flag",
				(pendingCollection!=null && ValidationUtil.isValid(pendingCollection.getLogTextFlag()))?pendingCollection.getLogTextFlag():"",
						(approvedCollection!=null && ValidationUtil.isValid(approvedCollection.getLogTextFlag()))?approvedCollection.getLogTextFlag():"");
		lResult.add(lLogTextFlag);
		ReviewResultVb lDMLStatus = new ReviewResultVb("DML Status",
				(pendingCollection!=null && ValidationUtil.isValid(pendingCollection.getDmlStatus()))?pendingCollection.getDmlStatus():"",
				(approvedCollection!=null && ValidationUtil.isValid(approvedCollection.getDmlStatus()))?approvedCollection.getDmlStatus():"");
		lResult.add(lDMLStatus);
		
		return lResult;
	}
	

	@Override
	protected AbstractDao<TemplateNameGBMVb> getScreenDao() {
		return genericMappingDesignDao;
	}

	@Override
	protected void setAtNtValues(TemplateNameGBMVb vObject) {
		vObject.setRecordIndicatorNt(7);
		vObject.setTemplateStatusNt(1);
	}


	protected String strErrorDesc = "";
	protected String strCurrentOperation = ""; 
	protected String serviceDesc = "";// Display Name of the service. 
	
	public String getStrErrorDesc() {
		return strErrorDesc;
	}

	public void setStrErrorDesc(String strErrorDesc) {
		this.strErrorDesc = strErrorDesc;
	}

	public String getStrCurrentOperation() {
		return strCurrentOperation;
	}

	public void setStrCurrentOperation(String strCurrentOperation) {
		this.strCurrentOperation = strCurrentOperation;
	}

	public String getServiceDesc() {
		return serviceDesc;
	}

	public void setServiceDesc(String serviceDesc) {
		this.serviceDesc = serviceDesc;
	}
	public ExceptionCode reviewRecordNew(TemplateNameGBMVb vObject) {
		ExceptionCode exceptionCode = new ExceptionCode();
		List<ReviewResultVb> list = null;
		try {
			List<TemplateNameGBMVb> approvedCollection = genericMappingDesignDao.getQueryResults(vObject, 0);
			List<TemplateNameGBMVb> pendingCollection = genericMappingDesignDao.getQueryResults(vObject, 1);
			list = transformToReviewResults(approvedCollection, pendingCollection);
			exceptionCode.setResponse(list);
			exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
		} catch (Exception ex) {
			exceptionCode.setErrorMsg(ex.getMessage());
		}
		return exceptionCode;
	}
	@Override
	protected List<ReviewResultVb> transformToReviewResults(List<TemplateNameGBMVb> approvedCollection, List <TemplateNameGBMVb> pendingCollection) {
		ResourceBundle rsb = CommonUtils.getResourceManger();
		ArrayList collTemp = getPageLoadValues();
		ArrayList<ReviewResultVb> lResult = new ArrayList<ReviewResultVb>();
		
		ReviewResultVb country = new ReviewResultVb(rsb.getString("country"),
				(pendingCollection!=null && ValidationUtil.isValid(pendingCollection.get(0).getCountry()))?pendingCollection.get(0).getCountry():"",
						(approvedCollection!=null && ValidationUtil.isValid(approvedCollection.get(0).getCountry()))?approvedCollection.get(0).getCountry():""
							,!pendingCollection.get(0).getCountry().equals(approvedCollection.get(0).getCountry()));
			lResult.add(country);
		ReviewResultVb lExeType = new ReviewResultVb(rsb.getString("executionType"),
				(pendingCollection.get(0)!=null && ValidationUtil.isValid(pendingCollection.get(0).getExecutionType()))?pendingCollection.get(0).getExecutionType():"",
						(approvedCollection!=null && ValidationUtil.isValid(approvedCollection.get(0).getExecutionType()))?approvedCollection.get(0).getExecutionType():""
							,!pendingCollection.get(0).getExecutionType().equals(approvedCollection.get(0).getExecutionType()));
			lResult.add(lExeType);
		ReviewResultVb lCommitFlag = new ReviewResultVb(rsb.getString("commitFlag"),
				(pendingCollection.get(0)!=null && ValidationUtil.isValid(pendingCollection.get(0).getCommitFlag()))?pendingCollection.get(0).getCommitFlag():"",
						(approvedCollection!=null && ValidationUtil.isValid(approvedCollection.get(0).getCommitFlag()))?approvedCollection.get(0).getCommitFlag():""
							,!pendingCollection.get(0).getCommitFlag().equals(approvedCollection.get(0).getCommitFlag()));
		lResult.add(lCommitFlag);
		ReviewResultVb lMainQuery = new ReviewResultVb(rsb.getString("mainQuery"),
				(pendingCollection.get(0)!=null && ValidationUtil.isValid(pendingCollection.get(0).getMainQuery()))?pendingCollection.get(0).getMainQuery():"",
				(approvedCollection!=null && ValidationUtil.isValid(approvedCollection.get(0).getMainQuery()))?approvedCollection.get(0).getMainQuery():""
					,!pendingCollection.get(0).getMainQuery().equals(approvedCollection.get(0).getMainQuery()));
		lResult.add(lMainQuery);
		ReviewResultVb lDML1 = new ReviewResultVb("DML1",
				(pendingCollection.get(0)!=null && ValidationUtil.isValid(pendingCollection.get(0).getDml1()))?pendingCollection.get(0).getDml1():"",
				(approvedCollection!=null && ValidationUtil.isValid(approvedCollection.get(0).getDml1()))?approvedCollection.get(0).getDml1():""
					,!pendingCollection.get(0).getDml1().equals(approvedCollection.get(0).getDml1()));
		
		lResult.add(lDML1);
		ReviewResultVb lDML2 = new ReviewResultVb("DML2",
				(pendingCollection.get(0)!=null && ValidationUtil.isValid(pendingCollection.get(0).getDml2()))?pendingCollection.get(0).getDml2():"",
				(approvedCollection!=null && ValidationUtil.isValid(approvedCollection.get(0).getDml2()))?approvedCollection.get(0).getDml2():""
					,!pendingCollection.get(0).getDml2().equals(approvedCollection.get(0).getDml2()));
		lResult.add(lDML2);
		ReviewResultVb lDML3 = new ReviewResultVb("DML3",
				(pendingCollection.get(0)!=null && ValidationUtil.isValid(pendingCollection.get(0).getDml3()))?pendingCollection.get(0).getDml3():"",
				(approvedCollection!=null && ValidationUtil.isValid(approvedCollection.get(0).getDml3()))?approvedCollection.get(0).getDml3():""
					,!pendingCollection.get(0).getDml3().equals(approvedCollection.get(0).getDml3()));
		lResult.add(lDML3);
		ReviewResultVb lDML4 = new ReviewResultVb("DML4",
				(pendingCollection.get(0)!=null && ValidationUtil.isValid(pendingCollection.get(0).getDml4()))?pendingCollection.get(0).getDml4():"",
				(approvedCollection!=null && ValidationUtil.isValid(approvedCollection.get(0).getDml4()))?approvedCollection.get(0).getDml4():""
					,!pendingCollection.get(0).getDml4().equals(approvedCollection.get(0).getDml4()));
			
		lResult.add(lDML4);
		ReviewResultVb lDML5 = new ReviewResultVb("DML5",
				(pendingCollection.get(0)!=null && ValidationUtil.isValid(pendingCollection.get(0).getDml5()))?pendingCollection.get(0).getDml5():"",
				(approvedCollection!=null && ValidationUtil.isValid(approvedCollection.get(0).getDml5()))?approvedCollection.get(0).getDml5():""
					,!pendingCollection.get(0).getDml5().equals(approvedCollection.get(0).getDml5()));

		lResult.add(lDML5);
		ReviewResultVb lDML6 = new ReviewResultVb("DML6",
				(pendingCollection.get(0)!=null && ValidationUtil.isValid(pendingCollection.get(0).getDml6()))?pendingCollection.get(0).getDml6():"",
				(approvedCollection!=null && ValidationUtil.isValid(approvedCollection.get(0).getDml6()))?approvedCollection.get(0).getDml6():""
					,!pendingCollection.get(0).getDml6().equals(approvedCollection.get(0).getDml6()));
		lResult.add(lDML6);
		ReviewResultVb lDML7 = new ReviewResultVb("DML7",
				(pendingCollection.get(0)!=null && ValidationUtil.isValid(pendingCollection.get(0).getDml7()))?pendingCollection.get(0).getDml7():"",
				(approvedCollection!=null && ValidationUtil.isValid(approvedCollection.get(0).getDml7()))?approvedCollection.get(0).getDml7():""
					,!pendingCollection.get(0).getDml7().equals(approvedCollection.get(0).getDml7()));
		lResult.add(lDML7);
		ReviewResultVb lDML8 = new ReviewResultVb("DML8",
				(pendingCollection.get(0)!=null && ValidationUtil.isValid(pendingCollection.get(0).getDml8()))?pendingCollection.get(0).getDml8():"",
				(approvedCollection!=null && ValidationUtil.isValid(approvedCollection.get(0).getDml8()))?approvedCollection.get(0).getDml8():""
					,!pendingCollection.get(0).getDml8().equals(approvedCollection.get(0).getDml8()));
		lResult.add(lDML8);
		ReviewResultVb lDML9 = new ReviewResultVb("DML9",
				(pendingCollection.get(0)!=null && ValidationUtil.isValid(pendingCollection.get(0).getDml9()))?pendingCollection.get(0).getDml9():"",
				(approvedCollection!=null && ValidationUtil.isValid(approvedCollection.get(0).getDml9()))?approvedCollection.get(0).getDml9():""
					,!pendingCollection.get(0).getDml9().equals(approvedCollection.get(0).getDml9()));
		lResult.add(lDML9);
		ReviewResultVb lDML10 = new ReviewResultVb("DML10",
				(pendingCollection.get(0)!=null && ValidationUtil.isValid(pendingCollection.get(0).getDml10()))?pendingCollection.get(0).getDml10():"",
				(approvedCollection!=null && ValidationUtil.isValid(approvedCollection.get(0).getDml10()))?approvedCollection.get(0).getDml10():""
					,!pendingCollection.get(0).getDml10().equals(approvedCollection.get(0).getDml10()));
		lResult.add(lDML10);
		
		ReviewResultVb lFatalFlag1 = new ReviewResultVb("FATAL FLAG 1",
				(pendingCollection.get(0)!=null && ValidationUtil.isValid(pendingCollection.get(0).getFatalFlag1()))?pendingCollection.get(0).getFatalFlag1():"",
				(approvedCollection!=null && ValidationUtil.isValid(approvedCollection.get(0).getFatalFlag1()))?approvedCollection.get(0).getFatalFlag1():""
					,!pendingCollection.get(0).getFatalFlag1().equals(approvedCollection.get(0).getFatalFlag1()));
		lResult.add(lFatalFlag1);
		ReviewResultVb lFatalFlag2 = new ReviewResultVb("FATAL FLAG 2",
				(pendingCollection.get(0)!=null && ValidationUtil.isValid(pendingCollection.get(0).getFatalFlag2()))?pendingCollection.get(0).getFatalFlag2():"",
				(approvedCollection!=null && ValidationUtil.isValid(approvedCollection.get(0).getFatalFlag2()))?approvedCollection.get(0).getFatalFlag2():""
					,!pendingCollection.get(0).getFatalFlag2().equals(approvedCollection.get(0).getFatalFlag2()));
		lResult.add(lFatalFlag2);
		ReviewResultVb lFatalFlag3 = new ReviewResultVb("FATAL FLAG 3",
				(pendingCollection.get(0)!=null && ValidationUtil.isValid(pendingCollection.get(0).getFatalFlag3()))?pendingCollection.get(0).getFatalFlag3():"",
				(approvedCollection!=null && ValidationUtil.isValid(approvedCollection.get(0).getFatalFlag3()))?approvedCollection.get(0).getFatalFlag3():""
					,!pendingCollection.get(0).getFatalFlag3().equals(approvedCollection.get(0).getFatalFlag3()));
		lResult.add(lFatalFlag3);
		ReviewResultVb lFatalFlag4 = new ReviewResultVb("FATAL FLAG 4",
				(pendingCollection.get(0)!=null && ValidationUtil.isValid(pendingCollection.get(0).getFatalFlag4()))?pendingCollection.get(0).getFatalFlag4():"",
				(approvedCollection!=null && ValidationUtil.isValid(approvedCollection.get(0).getFatalFlag4()))?approvedCollection.get(0).getFatalFlag4():""
					,!pendingCollection.get(0).getFatalFlag4().equals(approvedCollection.get(0).getFatalFlag4()));
		lResult.add(lFatalFlag4);
		ReviewResultVb lFatalFlag5 = new ReviewResultVb("FATAL FLAG 5",
				(pendingCollection.get(0)!=null && ValidationUtil.isValid(pendingCollection.get(0).getFatalFlag5()))?pendingCollection.get(0).getFatalFlag5():"",
				(approvedCollection!=null && ValidationUtil.isValid(approvedCollection.get(0).getFatalFlag5()))?approvedCollection.get(0).getFatalFlag5():""
					,!pendingCollection.get(0).getFatalFlag5().equals(approvedCollection.get(0).getFatalFlag5()));
		lResult.add(lFatalFlag5);
		ReviewResultVb lFatalFlag6 = new ReviewResultVb("FATAL FLAG 6",
				(pendingCollection.get(0)!=null && ValidationUtil.isValid(pendingCollection.get(0).getFatalFlag6()))?pendingCollection.get(0).getFatalFlag6():"",
				(approvedCollection!=null && ValidationUtil.isValid(approvedCollection.get(0).getFatalFlag6()))?approvedCollection.get(0).getFatalFlag6():""
					,!pendingCollection.get(0).getFatalFlag6().equals(approvedCollection.get(0).getFatalFlag6()));
		lResult.add(lFatalFlag6);
		ReviewResultVb lFatalFlag7 = new ReviewResultVb("FATAL FLAG 7",
				(pendingCollection.get(0)!=null && ValidationUtil.isValid(pendingCollection.get(0).getFatalFlag7()))?pendingCollection.get(0).getFatalFlag7():"",
				(approvedCollection!=null && ValidationUtil.isValid(approvedCollection.get(0).getFatalFlag7()))?approvedCollection.get(0).getFatalFlag7():""
					,!pendingCollection.get(0).getFatalFlag7().equals(approvedCollection.get(0).getFatalFlag7()));
		lResult.add(lFatalFlag7);
		ReviewResultVb lFatalFlag8 = new ReviewResultVb("FATAL FLAG 8",
				(pendingCollection.get(0)!=null && ValidationUtil.isValid(pendingCollection.get(0).getFatalFlag8()))?pendingCollection.get(0).getFatalFlag8():"",
				(approvedCollection!=null && ValidationUtil.isValid(approvedCollection.get(0).getFatalFlag8()))?approvedCollection.get(0).getFatalFlag8():""
					,!pendingCollection.get(0).getFatalFlag8().equals(approvedCollection.get(0).getFatalFlag8()));
		lResult.add(lFatalFlag8);
		ReviewResultVb lFatalFlag9 = new ReviewResultVb("FATAL FLAG 9",
				(pendingCollection.get(0)!=null && ValidationUtil.isValid(pendingCollection.get(0).getFatalFlag9()))?pendingCollection.get(0).getFatalFlag9():"",
				(approvedCollection!=null && ValidationUtil.isValid(approvedCollection.get(0).getFatalFlag9()))?approvedCollection.get(0).getFatalFlag9():""
					,!pendingCollection.get(0).getFatalFlag9().equals(approvedCollection.get(0).getFatalFlag9()));
		lResult.add(lFatalFlag9);
		ReviewResultVb lFatalFlag10 = new ReviewResultVb("FATAL FLAG 10",
				(pendingCollection.get(0)!=null && ValidationUtil.isValid(pendingCollection.get(0).getFatalFlag10()))?pendingCollection.get(0).getFatalFlag10():"",
				(approvedCollection!=null && ValidationUtil.isValid(approvedCollection.get(0).getFatalFlag10()))?approvedCollection.get(0).getFatalFlag10():""
					,!pendingCollection.get(0).getFatalFlag10().equals(approvedCollection.get(0).getFatalFlag10()));
		lResult.add(lFatalFlag10);
		
		ReviewResultVb lLogTextFlag1 = new ReviewResultVb("LOG TEXT FLAG 1",
				(pendingCollection.get(0)!=null && ValidationUtil.isValid(pendingCollection.get(0).getLogTextFlag1()))?pendingCollection.get(0).getLogTextFlag1():"",
				(approvedCollection!=null && ValidationUtil.isValid(approvedCollection.get(0).getLogTextFlag1()))?approvedCollection.get(0).getLogTextFlag1():""
					,!pendingCollection.get(0).getLogTextFlag1().equals(approvedCollection.get(0).getLogTextFlag1()));
		lResult.add(lLogTextFlag1);
		ReviewResultVb lLogTextFlag2 = new ReviewResultVb("LOG TEXT FLAG 2",
				(pendingCollection.get(0)!=null && ValidationUtil.isValid(pendingCollection.get(0).getLogTextFlag2()))?pendingCollection.get(0).getLogTextFlag2():"",
				(approvedCollection!=null && ValidationUtil.isValid(approvedCollection.get(0).getLogTextFlag2()))?approvedCollection.get(0).getLogTextFlag2():""
					,!pendingCollection.get(0).getLogTextFlag2().equals(approvedCollection.get(0).getLogTextFlag2()));
		lResult.add(lLogTextFlag2);
		ReviewResultVb lLogTextFlag3 = new ReviewResultVb("LOG TEXT FLAG 3",
				(pendingCollection.get(0)!=null && ValidationUtil.isValid(pendingCollection.get(0).getLogTextFlag3()))?pendingCollection.get(0).getLogTextFlag3():"",
				(approvedCollection!=null && ValidationUtil.isValid(approvedCollection.get(0).getLogTextFlag3()))?approvedCollection.get(0).getLogTextFlag3():""
					,!pendingCollection.get(0).getLogTextFlag3().equals(approvedCollection.get(0).getLogTextFlag3()));
		lResult.add(lLogTextFlag3);
		ReviewResultVb lLogTextFlag4 = new ReviewResultVb("LOG TEXT FLAG 4",
				(pendingCollection.get(0)!=null && ValidationUtil.isValid(pendingCollection.get(0).getLogTextFlag4()))?pendingCollection.get(0).getLogTextFlag4():"",
				(approvedCollection!=null && ValidationUtil.isValid(approvedCollection.get(0).getLogTextFlag4()))?approvedCollection.get(0).getLogTextFlag4():""
					,!pendingCollection.get(0).getLogTextFlag4().equals(approvedCollection.get(0).getLogTextFlag4()));
		lResult.add(lLogTextFlag4);
		ReviewResultVb lLogTextFlag5 = new ReviewResultVb("LOG TEXT FLAG 5",
				(pendingCollection.get(0)!=null && ValidationUtil.isValid(pendingCollection.get(0).getLogTextFlag5()))?pendingCollection.get(0).getLogTextFlag5():"",
				(approvedCollection!=null && ValidationUtil.isValid(approvedCollection.get(0).getLogTextFlag5()))?approvedCollection.get(0).getLogTextFlag5():""
					,!pendingCollection.get(0).getLogTextFlag5().equals(approvedCollection.get(0).getLogTextFlag5()));
		lResult.add(lLogTextFlag5);
		ReviewResultVb lLogTextFlag6 = new ReviewResultVb("LOG TEXT FLAG 6",
				(pendingCollection.get(0)!=null && ValidationUtil.isValid(pendingCollection.get(0).getLogTextFlag6()))?pendingCollection.get(0).getLogTextFlag6():"",
				(approvedCollection!=null && ValidationUtil.isValid(approvedCollection.get(0).getLogTextFlag6()))?approvedCollection.get(0).getLogTextFlag6():""
					,!pendingCollection.get(0).getLogTextFlag6().equals(approvedCollection.get(0).getLogTextFlag6()));
		lResult.add(lLogTextFlag6);
		ReviewResultVb lLogTextFlag7 = new ReviewResultVb("LOG TEXT FLAG 7",
				(pendingCollection.get(0)!=null && ValidationUtil.isValid(pendingCollection.get(0).getLogTextFlag7()))?pendingCollection.get(0).getLogTextFlag7():"",
				(approvedCollection!=null && ValidationUtil.isValid(approvedCollection.get(0).getLogTextFlag7()))?approvedCollection.get(0).getLogTextFlag7():""
					,!pendingCollection.get(0).getLogTextFlag7().equals(approvedCollection.get(0).getLogTextFlag7()));
		lResult.add(lLogTextFlag7);
		ReviewResultVb lLogTextFlag8 = new ReviewResultVb("LOG TEXT FLAG 8",
				(pendingCollection.get(0)!=null && ValidationUtil.isValid(pendingCollection.get(0).getLogTextFlag8()))?pendingCollection.get(0).getLogTextFlag8():"",
				(approvedCollection!=null && ValidationUtil.isValid(approvedCollection.get(0).getLogTextFlag8()))?approvedCollection.get(0).getLogTextFlag8():""
					,!pendingCollection.get(0).getLogTextFlag8().equals(approvedCollection.get(0).getLogTextFlag8()));
		lResult.add(lLogTextFlag8);
		ReviewResultVb lLogTextFlag9 = new ReviewResultVb("LOG TEXT FLAG 9",
				(pendingCollection.get(0)!=null && ValidationUtil.isValid(pendingCollection.get(0).getLogTextFlag9()))?pendingCollection.get(0).getLogTextFlag9():"",
				(approvedCollection!=null && ValidationUtil.isValid(approvedCollection.get(0).getLogTextFlag9()))?approvedCollection.get(0).getLogTextFlag9():""
					,!pendingCollection.get(0).getLogTextFlag9().equals(approvedCollection.get(0).getLogTextFlag9()));
		lResult.add(lLogTextFlag9);
		ReviewResultVb lLogTextFlag10 = new ReviewResultVb("LOG TEXT FLAG 10",
				(pendingCollection.get(0)!=null && ValidationUtil.isValid(pendingCollection.get(0).getLogTextFlag10()))?pendingCollection.get(0).getLogTextFlag10():"",
				(approvedCollection!=null && ValidationUtil.isValid(approvedCollection.get(0).getLogTextFlag10()))?approvedCollection.get(0).getLogTextFlag10():""
					,!pendingCollection.get(0).getLogTextFlag10().equals(approvedCollection.get(0).getLogTextFlag10()));
		lResult.add(lLogTextFlag10);
		
		ReviewResultVb lDMLStatusFlag1 = new ReviewResultVb("DML STATUS 1",
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: getNtDescription((List<NumSubTabVb>) collTemp.get(0),
								Integer.parseInt(pendingCollection.get(0).getDmlStatus1())),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: getNtDescription((List<NumSubTabVb>) collTemp.get(0),
								Integer.parseInt(approvedCollection.get(0).getDmlStatus1())),!pendingCollection.get(0).getDmlStatus1().equals(approvedCollection.get(0).getDmlStatus1()));
		lResult.add(lDMLStatusFlag1);
		ReviewResultVb lDMLStatusFlag2 = new ReviewResultVb("DML STATUS 2",
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: getNtDescription((List<NumSubTabVb>) collTemp.get(0),
								Integer.parseInt(pendingCollection.get(0).getDmlStatus2())),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: getNtDescription((List<NumSubTabVb>) collTemp.get(0),
								Integer.parseInt(approvedCollection.get(0).getDmlStatus2())),!pendingCollection.get(0).getDmlStatus2().equals(approvedCollection.get(0).getDmlStatus2()));
		lResult.add(lDMLStatusFlag2);
		ReviewResultVb lDMLStatusFlag3 = new ReviewResultVb("DML STATUS 3",
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: getNtDescription((List<NumSubTabVb>) collTemp.get(0),
								Integer.parseInt(pendingCollection.get(0).getDmlStatus3())),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: getNtDescription((List<NumSubTabVb>) collTemp.get(0),
								Integer.parseInt(approvedCollection.get(0).getDmlStatus3())),!pendingCollection.get(0).getDmlStatus3().equals(approvedCollection.get(0).getDmlStatus3()));
		lResult.add(lDMLStatusFlag3);
		ReviewResultVb lDMLStatusFlag4 = new ReviewResultVb("DML STATUS 4",
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: getNtDescription((List<NumSubTabVb>) collTemp.get(0),
								Integer.parseInt(pendingCollection.get(0).getDmlStatus4())),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: getNtDescription((List<NumSubTabVb>) collTemp.get(0),
								Integer.parseInt(approvedCollection.get(0).getDmlStatus4())),!pendingCollection.get(0).getDmlStatus4().equals(approvedCollection.get(0).getDmlStatus4()));
		lResult.add(lDMLStatusFlag4);
		ReviewResultVb lDMLStatusFlag5 = new ReviewResultVb("DML STATUS 5",
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: getNtDescription((List<NumSubTabVb>) collTemp.get(0),
								Integer.parseInt(pendingCollection.get(0).getDmlStatus5())),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: getNtDescription((List<NumSubTabVb>) collTemp.get(0),
								Integer.parseInt(approvedCollection.get(0).getDmlStatus5())),!pendingCollection.get(0).getDmlStatus5().equals(approvedCollection.get(0).getDmlStatus5()));
		lResult.add(lDMLStatusFlag5);
		ReviewResultVb lDMLStatusFlag6 = new ReviewResultVb("DML STATUS 6",
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: getNtDescription((List<NumSubTabVb>) collTemp.get(0),
								Integer.parseInt(pendingCollection.get(0).getDmlStatus6())),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: getNtDescription((List<NumSubTabVb>) collTemp.get(0),
								Integer.parseInt(approvedCollection.get(0).getDmlStatus6()))	,!pendingCollection.get(0).getDmlStatus6().equals(approvedCollection.get(0).getDmlStatus6()));
		lResult.add(lDMLStatusFlag6);
		ReviewResultVb lDMLStatusFlag7 = new ReviewResultVb("DML STATUS 7",
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: getNtDescription((List<NumSubTabVb>) collTemp.get(0),
								Integer.parseInt(pendingCollection.get(0).getDmlStatus7())),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: getNtDescription((List<NumSubTabVb>) collTemp.get(0),
								Integer.parseInt(approvedCollection.get(0).getDmlStatus7())),!pendingCollection.get(0).getDmlStatus7().equals(approvedCollection.get(0).getDmlStatus7()));
		lResult.add(lDMLStatusFlag7);
		ReviewResultVb lDMLStatusFlag8 = new ReviewResultVb("DML STATUS 8",
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: getNtDescription((List<NumSubTabVb>) collTemp.get(0),
								Integer.parseInt(pendingCollection.get(0).getDmlStatus8())),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: getNtDescription((List<NumSubTabVb>) collTemp.get(0),
								Integer.parseInt(approvedCollection.get(0).getDmlStatus8())),!pendingCollection.get(0).getDmlStatus8().equals(approvedCollection.get(0).getDmlStatus8()));
		lResult.add(lDMLStatusFlag8);
		ReviewResultVb lDMLStatusFlag9 = new ReviewResultVb("DML STATUS 9",
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: getNtDescription((List<NumSubTabVb>) collTemp.get(0),
								Integer.parseInt(pendingCollection.get(0).getDmlStatus9())),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: getNtDescription((List<NumSubTabVb>) collTemp.get(0),
								Integer.parseInt(approvedCollection.get(0).getDmlStatus9())),!pendingCollection.get(0).getDmlStatus9().equals(approvedCollection.get(0).getDmlStatus9()));
		lResult.add(lDMLStatusFlag9);
		ReviewResultVb lDMLStatusFlag10 = new ReviewResultVb("DML STATUS 10",
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: getNtDescription((List<NumSubTabVb>) collTemp.get(0),
								Integer.parseInt(pendingCollection.get(0).getDmlStatus10())),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: getNtDescription((List<NumSubTabVb>) collTemp.get(0),
								Integer.parseInt(approvedCollection.get(0).getDmlStatus10())),!pendingCollection.get(0).getDmlStatus10().equals(approvedCollection.get(0).getDmlStatus10()));
		lResult.add(lDMLStatusFlag10);
		
		
		ReviewResultVb lTemplateStatus = new ReviewResultVb(rsb.getString("status"),
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: getNtDescription((List<NumSubTabVb>) collTemp.get(0),
								pendingCollection.get(0).getMappingStatus()),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: getNtDescription((List<NumSubTabVb>) collTemp.get(0),
								approvedCollection.get(0).getMappingStatus()),
						(pendingCollection.get(0).getMappingStatus()!=(approvedCollection.get(0).getMappingStatus())));
		lResult.add(lTemplateStatus);
		
		ReviewResultVb lRecordIndicator = new ReviewResultVb(rsb.getString("recordIndicator"),
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: getNtDescription((List<NumSubTabVb>) collTemp.get(1),
								pendingCollection.get(0).getRecordIndicator()),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: getNtDescription((List<NumSubTabVb>) collTemp.get(1),
								approvedCollection.get(0).getRecordIndicator()),
						(pendingCollection.get(0).getRecordIndicator()!=(approvedCollection.get(0).getRecordIndicator())));
		lResult.add(lRecordIndicator);
		ReviewResultVb lMaker = new ReviewResultVb(rsb.getString("maker"),
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: pendingCollection.get(0).getMaker() == 0 ? "" : pendingCollection.get(0).getMakerName(),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: approvedCollection.get(0).getMaker() == 0 ? "" : approvedCollection.get(0).getMakerName(),
								(pendingCollection.get(0).getMaker()!=(approvedCollection.get(0).getMaker())));
		lResult.add(lMaker);
		ReviewResultVb lVerifier = new ReviewResultVb(rsb.getString("verifier"),
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: pendingCollection.get(0).getVerifier() == 0 ? "" : pendingCollection.get(0).getVerifierName(),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: approvedCollection.get(0).getVerifier() == 0 ? ""
								: approvedCollection.get(0).getVerifierName(),
								(pendingCollection.get(0).getVerifier()!=(approvedCollection.get(0).getVerifier())));
		lResult.add(lVerifier);
		ReviewResultVb lDateLastModified = new ReviewResultVb(rsb.getString("dateLastModified"),
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: pendingCollection.get(0).getDateLastModified(),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: approvedCollection.get(0).getDateLastModified(),
						(!pendingCollection.get(0).getDateLastModified()
								.equals(approvedCollection.get(0).getDateLastModified())));
		lResult.add(lDateLastModified);
		ReviewResultVb lDateCreation = new ReviewResultVb(rsb.getString("dateCreation"),
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: pendingCollection.get(0).getDateCreation(),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: approvedCollection.get(0).getDateCreation(),
						(!pendingCollection.get(0).getDateCreation()
								.equals(approvedCollection.get(0).getDateCreation())));
		lResult.add(lDateCreation);
		return lResult;
	}
	protected ExceptionCode doValidate(List<TemplateNameGBMVb> vObjects) {
		ExceptionCode exceptionCode = new ExceptionCode();
		TemplateNameGBMVb vObject = vObjects.get(0);
		String operation = vObject.getActionType();
		String srtRestrion = getCommonDao().getRestrictionsByUsers("GenericMappingDesign", operation);
		if(!"Y".equalsIgnoreCase(srtRestrion)) {
			exceptionCode = new ExceptionCode();
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg(operation +" "+Constants.userRestrictionMsg);
			return exceptionCode;
		}
		exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
		return exceptionCode;
	
	}
	protected ExceptionCode doValidate(TemplateNameGBMVb vObject){
		ExceptionCode exceptionCode = new ExceptionCode();
		String operation = vObject.getActionType();
		String srtRestrion = getCommonDao().getRestrictionsByUsers("GenericMappingDesign", operation);
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
