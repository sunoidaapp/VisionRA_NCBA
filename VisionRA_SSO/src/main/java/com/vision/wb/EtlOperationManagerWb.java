package com.vision.wb;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.vision.dao.AbstractDao;
import com.vision.dao.CommonDao;
import com.vision.dao.EtlOperationManagerDao;
import com.vision.exception.ExceptionCode;
import com.vision.util.CommonUtils;
import com.vision.util.Constants;
import com.vision.util.ValidationUtil;
import com.vision.vb.EtlOperationDetailVb;
import com.vision.vb.EtlOperationManagerHeaderVb;

@Component
public class EtlOperationManagerWb extends AbstractDynaWorkerBean<EtlOperationManagerHeaderVb>{
	
	@Autowired
	EtlOperationManagerDao etlOperationManagerDao;
	@Autowired
	CommonDao commonDao;
	public ArrayList getPageLoadValues(){
		ArrayList<Object> arrListLocal = new ArrayList<Object>();
		try{
			String autoRefreshTime = commonDao.findVisionVariableValue("RA_ETL_REFRESH_TIME");
			if(!ValidationUtil.isValid(autoRefreshTime)) {
				autoRefreshTime = "120";
			}
			arrListLocal.add(autoRefreshTime);
			String country = commonDao.findVisionVariableValue("DEFAULT_COUNTRY");
			String leBook = commonDao.findVisionVariableValue("DEFAULT_LE_BOOK");
			String businessDate = commonDao.getVisionBusinessDate(country+ "-" +leBook);
			arrListLocal.add(businessDate);
			String etlRunStatus = commonDao.findVisionVariableValue("RA_ETL_RUNSTATUS");
			arrListLocal.add(etlRunStatus);
			return arrListLocal;
		}catch(Exception ex){
			ex.printStackTrace();
			//logger.error("Exception in getting the Page load values.", ex);
			return null;
		}
	}


	public ExceptionCode getEtlOperationDetail(EtlOperationManagerHeaderVb vObject){
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			List<EtlOperationDetailVb> etlOperationDetailLst = new ArrayList<EtlOperationDetailVb>();
			List<EtlOperationManagerHeaderVb> collTemp = etlOperationManagerDao.getQueryHeaderResults(vObject);
			if(collTemp.size() == 0){
				exceptionCode = CommonUtils.getResultObject(etlOperationManagerDao.getServiceDesc(), 16, "Query", "");
				exceptionCode.setOtherInfo(vObject);
				exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
				exceptionCode.setErrorMsg("No records Found");
				return exceptionCode;
			}else{
				EtlOperationManagerHeaderVb etlOperationManagerHeaderVb = collTemp.get(0);
				etlOperationDetailLst = etlOperationManagerDao.getQueryDetailResults(vObject);
				if(etlOperationDetailLst != null && !etlOperationDetailLst.isEmpty()) {
					etlOperationManagerHeaderVb.setEtlOperationDetailLst(etlOperationDetailLst);
				}
				exceptionCode.setOtherInfo(vObject);
				exceptionCode.setResponse(etlOperationManagerHeaderVb);
				exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
				exceptionCode.setErrorMsg("Successful operation");
				return exceptionCode;
			}
		}catch(Exception e) {
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg(e.getMessage());
			return exceptionCode;
		}
	}
	public ExceptionCode downloadFile(String logFileName) throws IOException{
		ExceptionCode exceptionCode = new ExceptionCode();
		ByteArrayOutputStream out =null;
		OutputStream outputStream = null;
		try {
			String filePath = commonDao.findVisionVariableValue("RA_SERV_LOGPATH");
			String tmpFilePath = System.getProperty("java.io.tmpdir"); 
		    File my_file = new File(filePath+File.separator+logFileName);
		    out = new ByteArrayOutputStream();
	        FileInputStream in = new FileInputStream(my_file);
	        out = new ByteArrayOutputStream();  
		    File lfile = new File(tmpFilePath+File.separator+logFileName);
		    if(lfile.exists()) {
		    	lfile.delete();
		    }
		    outputStream = new FileOutputStream(tmpFilePath+File.separator+logFileName);
		    int length = logFileName.length();  
		    int bufferSize = 1024;  
		    byte[] buffer = new byte[bufferSize];  
		    while ((length = in.read(buffer)) != -1) {  
		        out.write(buffer, 0, length);
		    }
		    out.writeTo(outputStream);
		    outputStream.flush();
		    outputStream.close();
		    out.flush();
		    out.close();
		    in.close();
	        
	        exceptionCode.setResponse(tmpFilePath);
	        exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
	        exceptionCode.setErrorMsg("Log File Downloaded");
		    return exceptionCode;
		}catch(Exception ex){
			//logger.error("Download Errror : "+ex.getMessage());
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
		    return exceptionCode;
		}
	}

	@Override
	protected AbstractDao<EtlOperationManagerHeaderVb> getScreenDao() {
		// TODO Auto-generated method stub
		return etlOperationManagerDao;
	}

	@Override
	protected void setAtNtValues(EtlOperationManagerHeaderVb vObject) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void setVerifReqDeleteType(EtlOperationManagerHeaderVb vObject) {
		// TODO Auto-generated method stub
		
	}
}
