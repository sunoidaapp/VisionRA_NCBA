package com.vision.wb;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.vision.dao.AbstractDao;
import com.vision.dao.CommonApiDao;
import com.vision.dao.CommonDao;
import com.vision.dao.EtlConsoleDao;
import com.vision.dao.EtlPostingsDao;
import com.vision.exception.ExceptionCode;
import com.vision.util.CommonUtils;
import com.vision.util.Constants;
import com.vision.util.ValidationUtil;
import com.vision.vb.EtlConsoleDetailVb;
import com.vision.vb.EtlPostingsHeaderVb;

@Component
public class EtlConsoleWb extends AbstractDynaWorkerBean<EtlPostingsHeaderVb> {

	@Autowired
	EtlConsoleDao etlConsoleDao;
	@Autowired
	CommonDao commonDao;
	@Autowired
	CommonApiDao commonApiDao;
	@Autowired
	EtlPostingsDao etlPostinsDao;
	@Value("${schedule.genBuild}")
	private String genBuildFlag;

	public ArrayList getPageLoadValues() {
		ArrayList<Object> arrListLocal = new ArrayList<Object>();
		try {
			String autoRefreshTime = commonDao.findVisionVariableValue("RA_ETL_REFRESH_TIME");
			if (!ValidationUtil.isValid(autoRefreshTime)) {
				autoRefreshTime = "120";
			}
			arrListLocal.add(autoRefreshTime);
			String country = commonDao.findVisionVariableValue("DEFAULT_COUNTRY");
			String leBook = commonDao.findVisionVariableValue("DEFAULT_LE_BOOK");
			String businessDate = commonDao.getVisionBusinessDate(country + "-" + leBook);
			arrListLocal.add(businessDate);
			String etlRunStatus = "N";
			String query = "SELECT CRON_RUN_STATUS FROM RA_CRON_CONTROL WHERE CRON_TYPE = 'BLD' ";
			ExceptionCode exceptionCode = commonApiDao.getCommonResultDataQuery(query);
			List resultlst = (List) exceptionCode.getResponse();
			if (resultlst != null && resultlst.size() > 0) {
				HashMap<String, String> statusMap = (HashMap<String, String>) resultlst.get(0);
				etlRunStatus = statusMap.get("CRON_RUN_STATUS");
			}
			if ("Y".equalsIgnoreCase(genBuildFlag) && "Y".equalsIgnoreCase(etlRunStatus))
				etlRunStatus = "Y";
			else
				etlRunStatus = "N";
			arrListLocal.add(etlRunStatus);
			return arrListLocal;
		} catch (Exception ex) {
			ex.printStackTrace();
			//logger.error("Exception in getting the Page load values.", ex);
			return null;
		}
	}

	public ExceptionCode getEtlConsoleHeader(EtlPostingsHeaderVb vObject) {
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			EtlConsoleDetailVb etlConsoleDetailVb  = new EtlConsoleDetailVb();
			etlConsoleDetailVb.setActionType(vObject.getActionType());
			exceptionCode = doValidate(etlConsoleDetailVb);
			if(exceptionCode.getErrorCode() == Constants.ERRONEOUS_OPERATION){
				return exceptionCode;
			}
			List<EtlPostingsHeaderVb> etlPostingHeaderLst = etlConsoleDao.getEtlConsoleHeaderList(vObject);
			exceptionCode.setOtherInfo(vObject);
			exceptionCode.setResponse(etlPostingHeaderLst);
			exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
			exceptionCode.setErrorMsg("Successful operation");
			return exceptionCode;
		} catch (Exception e) {
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg(e.getMessage());
			return exceptionCode;
		}
	}

	public ExceptionCode getEtlConsoleDetail(EtlPostingsHeaderVb vObject) {
		ExceptionCode exceptionCode = new ExceptionCode();
		List summaryLst = new ArrayList();
		try {
			EtlConsoleDetailVb etlConsoleDetailVb  = new EtlConsoleDetailVb();
			etlConsoleDetailVb.setActionType(vObject.getActionType());
			exceptionCode = doValidate(etlConsoleDetailVb);
			if(exceptionCode.getErrorCode() == Constants.ERRONEOUS_OPERATION){
				return exceptionCode;
			}
			ExceptionCode exceptionCodeSummary = etlConsoleDao.getQuerySummaryResultsNew(vObject);
			summaryLst = (ArrayList) exceptionCodeSummary.getResponse();
			List<EtlConsoleDetailVb> collTemp = etlConsoleDao.getQueryDetailResults(vObject);
			if (collTemp.size() == 0) {
				exceptionCode = CommonUtils.getResultObject(etlConsoleDao.getServiceDesc(), 16, "Query", "");
				exceptionCode.setOtherInfo(vObject);
				exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
				exceptionCode.setErrorMsg("No records Found");
				return exceptionCode;
			}
			exceptionCode.setResponse(collTemp);
			exceptionCode.setOtherInfo(summaryLst);
			exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
			exceptionCode.setErrorMsg("Successful operation");
			return exceptionCode;
		} catch (Exception e) {
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg(e.getMessage());
			return exceptionCode;
		}
	}

	public ExceptionCode downloadFile(String logFileName,EtlConsoleDetailVb vObject) throws IOException {
		ExceptionCode exceptionCode = new ExceptionCode();
		ByteArrayOutputStream out = null;
		OutputStream outputStream = null;
		try {
			exceptionCode = doValidate(vObject);
			if(exceptionCode.getErrorCode() == Constants.ERRONEOUS_OPERATION){
				return exceptionCode;
			}
			String filePath = commonDao.findVisionVariableValue("RA_SERV_LOGPATH");
			String tmpFilePath = System.getProperty("java.io.tmpdir");
			String[] logFile = logFileName.split(",");
			File lfile = new File(tmpFilePath + File.separator + "logs.zip");
			if (lfile.exists()) {
				lfile.delete();
			}
			if (logFile.length > 1) {
				FileOutputStream fos = new FileOutputStream(tmpFilePath + File.separator + "logs.zip");
				ZipOutputStream zipOut = new ZipOutputStream(new BufferedOutputStream(fos));
				for (int i = 0; i < logFile.length; i++) {
					FileInputStream fis = new FileInputStream(filePath + File.separator + logFile[i]);
					ZipEntry ze = new ZipEntry(logFile[i]);
					zipOut.putNextEntry(ze);
					byte[] tmp = new byte[4 * 1024];
					int size = 0;
					while ((size = fis.read(tmp)) != -1) {
						zipOut.write(tmp, 0, size);
					}
					fis.close();
				}
				zipOut.close();
				fos.close();
			} else {
				File my_file = new File(filePath + File.separator + logFileName);
				out = new ByteArrayOutputStream();
				FileInputStream in = new FileInputStream(my_file);
				out = new ByteArrayOutputStream();
				lfile = new File(tmpFilePath + File.separator + logFileName);
				if (lfile.exists()) {
					lfile.delete();
				}
				outputStream = new FileOutputStream(tmpFilePath + File.separator + logFileName);
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

			}

			exceptionCode.setResponse(tmpFilePath);
			exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
			exceptionCode.setErrorMsg("Log File Downloaded");
			return exceptionCode;
		} catch (Exception ex) {
			//logger.error("Download Errror : " + ex.getMessage());
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			return exceptionCode;
		}
	}

	public ExceptionCode listGroupedLogs(EtlConsoleDetailVb vObj) throws IOException {
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			exceptionCode = doValidate(vObj);
			if(exceptionCode.getErrorCode() == Constants.ERRONEOUS_OPERATION){
				return exceptionCode;
			}
			String folderPath = commonDao.findVisionVariableValue("RA_SERV_LOGPATH");
			File directory = new File(folderPath);
			Map<String, List<File>> mapFiles = new TreeMap<>();

			if (directory.exists()) {
				File[] listFiles = directory.listFiles();
				for (File listFile : listFiles) {
					List fileLst = new ArrayList<>();
					String fileName = listFile.getName();
					if (!fileName.contains(".log"))
						continue;
					fileName = fileName.substring(0, fileName.lastIndexOf(".log"));
					String date = fileName.substring(fileName.length() - 10);
					for (File File : listFiles) {
						if (File.getName().contains(date)) {
							fileLst.add(File.getName());

						}
					}
					mapFiles.put(date, fileLst);
				}
			}
			exceptionCode.setResponse(mapFiles);
		} catch (Exception e) {

		}
		return exceptionCode;
	}
	@Override
	protected AbstractDao<EtlPostingsHeaderVb> getScreenDao() {
		return etlConsoleDao;
	}

	@Override
	protected void setAtNtValues(EtlPostingsHeaderVb vObject) {
	}

	@Override
	protected void setVerifReqDeleteType(EtlPostingsHeaderVb vObject) {
	}
	public ExceptionCode etlRestart(EtlConsoleDetailVb vObject) {
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			exceptionCode = doValidate(vObject);
			if(exceptionCode.getErrorCode() == Constants.ERRONEOUS_OPERATION){
				return exceptionCode;
			}
			if ("E".equalsIgnoreCase(vObject.getRestartProcess())) {
				int errorFeedAvailCnt = etlPostinsDao.getCountErrored(vObject.getCountry(), vObject.getLeBook(),
						vObject.getExtractionSequence(), vObject.getFrequency(), vObject.getBusinessDate());
				if (errorFeedAvailCnt == 0) {
					exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
					exceptionCode.setErrorMsg("No Error/Terminated Feeds to Restart");
					return exceptionCode;
				}
			}
			etlPostinsDao.etlDetailStatusUpdate(vObject.getEtlStatusId(), vObject.getCountry(), vObject.getLeBook(),
					vObject.getExtractionSequence(), vObject.getFrequency(), vObject.getPostingSequence(), "M",
					vObject.getBusinessDate(), vObject.getRestartProcess());
			etlPostinsDao.etlHeaderStatusUpdate(vObject.getEtlStatusId(), vObject.getCountry(), vObject.getLeBook(),
					vObject.getExtractionSequence(), vObject.getFrequency(), "M", vObject.getBusinessDate());

			exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
			exceptionCode.setErrorMsg("Process Reinititated Successfully");
		} catch (Exception e) {
		}
		return exceptionCode;
	}

	public ExceptionCode deleteConsoleRecord(EtlConsoleDetailVb vObject) {
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			exceptionCode = doValidate(vObject);
			if(exceptionCode.getErrorCode() == Constants.ERRONEOUS_OPERATION){
				return exceptionCode;
			}
			if (ValidationUtil.isValid(vObject.getPostingSequence())) {
				int cnt = etlConsoleDao.getDetailCnt(vObject);
				if (cnt == 1) {
					etlConsoleDao.deletePosting(vObject);
					etlConsoleDao.deletePostingHeader(vObject);
				} else {
					etlConsoleDao.deletePosting(vObject);
				}
			} else {
				etlConsoleDao.deletePostingHeader(vObject);
				etlConsoleDao.deletePosting(vObject);
			}
			exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
			exceptionCode.setErrorMsg("Process Deleted Successfully");
		} catch (Exception e) {
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg(e.getMessage());
		}
		return exceptionCode;
	}
	public ExceptionCode doValidate(EtlConsoleDetailVb vObject){
		ExceptionCode exceptionCode = new ExceptionCode();
		String operation = vObject.getActionType();
		String srtRestriction = getCommonDao().getRestrictionsByUsers("ETLConsoleNew", operation);
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
