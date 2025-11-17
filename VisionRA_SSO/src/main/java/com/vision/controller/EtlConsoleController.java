package com.vision.controller;

import java.io.IOException;
import java.util.ArrayList;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vision.dao.EtlConsoleDao;
import com.vision.exception.ExceptionCode;
import com.vision.exception.JSONExceptionCode;
import com.vision.exception.RuntimeCustomException;
import com.vision.util.Constants;
import com.vision.vb.EtlConsoleDetailVb;
import com.vision.vb.EtlPostingsHeaderVb;
import com.vision.wb.EtlConsoleWb;
import com.vision.wb.VisionUploadWb;

@RestController
@RequestMapping("etlConsole")
//@Api(value = "etlConsole", description = "Etl Console")
public class EtlConsoleController {

	@Autowired
	EtlConsoleWb etlConsoleWb;
	@Autowired
	EtlConsoleDao etlConsoleDao;
	@Autowired
	VisionUploadWb visionUploadWb;

	/*-------------------------------------PAGE LOAD VALUES------------------------------------------*/
	@RequestMapping(path = "/pageLoadValues", method = RequestMethod.GET)
	//@ApiOperation(value = "Page Load Values", notes = "Load AT/NT Values on screen load", response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> pageOnLoad() {
		JSONExceptionCode jsonExceptionCode = null;
		try {
			EtlConsoleDetailVb vObj = new EtlConsoleDetailVb();
			vObj.setActionType("Query");
			ExceptionCode exceptionCode = etlConsoleWb.doValidate(vObj);
			if(exceptionCode.getErrorCode() == Constants.ERRONEOUS_OPERATION) {
				jsonExceptionCode = new JSONExceptionCode(exceptionCode.getErrorCode(), exceptionCode.getErrorMsg(), null);
				return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
			}
			ArrayList arrayList = etlConsoleWb.getPageLoadValues();
			jsonExceptionCode = new JSONExceptionCode(Constants.SUCCESSFUL_OPERATION, "On Load Values", arrayList);
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		} catch (RuntimeCustomException rex) {
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
		}
	}

	/*--------------get Query Results------------------*/
	@RequestMapping(path = "/getEtlConsoleDetail", method = RequestMethod.POST)
	//@ApiOperation(value = "Get All the details", notes = "Fetch all the existing records from the table", response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> getAllQueryDetails(@RequestBody EtlPostingsHeaderVb vObject) {
		JSONExceptionCode jsonExceptionCode = null;
		try {
			vObject.setActionType("Query");
			ExceptionCode exceptionCode = etlConsoleWb.getEtlConsoleDetail(vObject);
			jsonExceptionCode = new JSONExceptionCode(exceptionCode.getErrorCode(), exceptionCode.getErrorMsg(),exceptionCode.getResponse(), exceptionCode.getOtherInfo());
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		} catch (RuntimeCustomException rex) {
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
		}
	}

	/*--------------get Header Results------------------*/
	@RequestMapping(path = "/getEtlConsoleHeader", method = RequestMethod.POST)
	//@ApiOperation(value = "Get All the Header details", notes = "Fetch all the Header records from the table", response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> getEtlConsoleHeader(@RequestBody EtlPostingsHeaderVb vObject) {
		JSONExceptionCode jsonExceptionCode = null;
		try {
			vObject.setActionType("Query");
			ExceptionCode exceptionCode = etlConsoleWb.getEtlConsoleHeader(vObject);
			
			jsonExceptionCode = new JSONExceptionCode(exceptionCode.getErrorCode(), exceptionCode.getErrorMsg(),exceptionCode.getResponse(), exceptionCode.getOtherInfo());
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		} catch (RuntimeCustomException rex) {
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
		}
	}

	/*-------------------------------------Log File Download------------------------------------------*/
	@RequestMapping(path = "/downloadEtlLog", method = RequestMethod.POST)
	//@ApiOperation(value = "downloadEtlLog", notes = "Download EtlLog", response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> downloadCallReport(@RequestParam("logFileName") String logFileName,
			HttpServletRequest request, HttpServletResponse response) throws IOException {
		JSONExceptionCode jsonExceptionCode = null;
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			EtlConsoleDetailVb vObject = new EtlConsoleDetailVb();
			vObject.setActionType("Download");
			exceptionCode = etlConsoleWb.downloadFile(logFileName,vObject);
			if (exceptionCode.getErrorCode() == Constants.SUCCESSFUL_OPERATION) {
				if (!logFileName.contains(",")) {
					String fileExt[] = logFileName.split("\\.");
					request.setAttribute("fileExtension", fileExt[1]);
					request.setAttribute("fileName", fileExt[0]);
				} else {
					request.setAttribute("fileExtension", "zip");
					request.setAttribute("fileName", "logs");
				}
				request.setAttribute("filePath", exceptionCode.getResponse());
				visionUploadWb.setExportXlsServlet(request, response);
				if (response.getStatus() == 404) {
					jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, "File not found", null);
					return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
				} else {
					jsonExceptionCode = new JSONExceptionCode(Constants.SUCCESSFUL_OPERATION, "Success", response);
					return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
				}
			} else {
				jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, exceptionCode.getErrorMsg(),exceptionCode.getOtherInfo());
				return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
			}
		} catch (RuntimeCustomException rex) {
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
		}
	}

	// --------------Terminate the Process------------------
	@RequestMapping(path = "/terminateProcess", method = RequestMethod.POST)
	//@ApiOperation(value = "terminating the process", notes = "Terminating the process from the console", response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> terminateProcess(@RequestBody EtlConsoleDetailVb vObject) {
		JSONExceptionCode jsonExceptionCode = null;
		try {
			vObject.setActionType("Modify");
			ExceptionCode exceptionCode = etlConsoleWb.doValidate(vObject);
			if(exceptionCode.getErrorCode() == Constants.ERRONEOUS_OPERATION) {
				jsonExceptionCode = new JSONExceptionCode(exceptionCode.getErrorCode(), exceptionCode.getErrorMsg(), null);
				return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
			}
			exceptionCode = etlConsoleDao.killProcess(vObject);
			jsonExceptionCode = new JSONExceptionCode(exceptionCode.getErrorCode(), exceptionCode.getErrorMsg(),
					exceptionCode.getResponse(), exceptionCode.getOtherInfo());
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		} catch (RuntimeCustomException rex) {
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
		}
	}

	// --------------Restart the Process------------------
	@RequestMapping(path = "/restartProcess", method = RequestMethod.POST)
	//@ApiOperation(value = "reinitiating the process", notes = "Reinitiating the process from the console", response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> restartProcess(@RequestBody EtlConsoleDetailVb vObject) {
		JSONExceptionCode jsonExceptionCode = null;
		try {
			vObject.setActionType("Modify");
			ExceptionCode exceptionCode = etlConsoleWb.etlRestart(vObject);
			jsonExceptionCode = new JSONExceptionCode(exceptionCode.getErrorCode(), exceptionCode.getErrorMsg(),
					exceptionCode.getResponse(), exceptionCode.getOtherInfo());
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		} catch (RuntimeCustomException rex) {
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
		}
	}

	/*--------------Delete ETL Console------------------*/
	@RequestMapping(path = "/deleteEtlConsole", method = RequestMethod.POST)
	//@ApiOperation(value = "Delete Etl Console", notes = "delete the records from the table", response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> deleteEtlConsole(@RequestBody EtlConsoleDetailVb vObject) {
		JSONExceptionCode jsonExceptionCode = null;
		try {
			vObject.setActionType("Delete");
			ExceptionCode exceptionCode = etlConsoleWb.deleteConsoleRecord(vObject);
			jsonExceptionCode = new JSONExceptionCode(exceptionCode.getErrorCode(), exceptionCode.getErrorMsg(),exceptionCode.getResponse(), exceptionCode.getOtherInfo());
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		} catch (RuntimeCustomException rex) {
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
		}
	}

	/*-------------------------------------Log File Download------------------------------------------*/
	@RequestMapping(path = "/listEtlLogs", method = RequestMethod.GET)
	//@ApiOperation(value = "listEtlLogs", notes = "List Etl log files", response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> downloadConsoleFolderLog() throws IOException {
		JSONExceptionCode jsonExceptionCode = null;
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			EtlConsoleDetailVb etlConsoleDetailVb = new EtlConsoleDetailVb();
			etlConsoleDetailVb.setActionType("Query");
			exceptionCode = etlConsoleWb.listGroupedLogs(etlConsoleDetailVb);
			jsonExceptionCode = new JSONExceptionCode(exceptionCode.getErrorCode(), exceptionCode.getErrorMsg(),exceptionCode.getResponse(), exceptionCode.getOtherInfo());
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		} catch (RuntimeCustomException rex) {
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
		}
	}

}
