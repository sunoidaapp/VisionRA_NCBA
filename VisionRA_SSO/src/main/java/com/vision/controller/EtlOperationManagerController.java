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

import com.vision.dao.EtlOperationManagerDao;
import com.vision.exception.ExceptionCode;
import com.vision.exception.JSONExceptionCode;
import com.vision.exception.RuntimeCustomException;
import com.vision.util.Constants;
import com.vision.vb.EtlOperationDetailVb;
import com.vision.vb.EtlOperationManagerHeaderVb;
import com.vision.vb.MenuVb;
import com.vision.wb.EtlOperationManagerWb;
import com.vision.wb.VisionUploadWb;

@RestController
@RequestMapping("etlOperationManager")
//@Api(value = "etlOperationManager", description = "Etl Operation Manager")
public class EtlOperationManagerController {
	
	@Autowired
	EtlOperationManagerWb etlOperationManagerWb;
	@Autowired
	EtlOperationManagerDao etlOperationManagerDao;
	@Autowired
	VisionUploadWb visionUploadWb;
	
	/*-------------------------------------BRANCH RM TARGET SCREEN PAGE LOAD------------------------------------------*/
	@RequestMapping(path = "/pageLoadValues", method = RequestMethod.GET)
	//@ApiOperation(value = "Page Load Values",notes = "Load AT/NT Values on screen load",response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> pageOnLoad(){
		JSONExceptionCode jsonExceptionCode  = null;
		try{
			MenuVb menuVb = new MenuVb();
			menuVb.setActionType("Clear");
			ArrayList arrayList = etlOperationManagerWb.getPageLoadValues();
			jsonExceptionCode = new JSONExceptionCode(Constants.SUCCESSFUL_OPERATION, "Page Load Values", arrayList);
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		}catch(RuntimeCustomException rex){
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		}	
	}

	/*--------------get Query Results------------------*/
	@RequestMapping(path = "/getEtlOperationDetail", method = RequestMethod.POST)
	//@ApiOperation(value = "Get All the details", notes = "Fetch all the existing records from the table",response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> getAllQueryDetails(@RequestBody EtlOperationManagerHeaderVb vObject){
		JSONExceptionCode jsonExceptionCode = null;
		try {
			vObject.setActionType("Query");
			ExceptionCode exceptionCode = etlOperationManagerWb.getEtlOperationDetail(vObject);
			jsonExceptionCode = new JSONExceptionCode(Constants.SUCCESSFUL_OPERATION, "Query Results", exceptionCode.getResponse(),exceptionCode.getOtherInfo());
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);	
		}catch(RuntimeCustomException rex){
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
			exceptionCode = etlOperationManagerWb.downloadFile(logFileName);
			if(exceptionCode.getErrorCode() == Constants.SUCCESSFUL_OPERATION) {
				String fileExt[] = logFileName.split("\\.");
				request.setAttribute("fileExtension", fileExt[1]);
				request.setAttribute("fileName", fileExt[0]);
				request.setAttribute("filePath", exceptionCode.getResponse());
				visionUploadWb.setExportXlsServlet(request, response);
				if(response.getStatus() == 404) {
					jsonExceptionCode =	new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, "File not found", null);
					return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
				}else{
					jsonExceptionCode =	new JSONExceptionCode(Constants.SUCCESSFUL_OPERATION, "Success", response);
					return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
				}
			}else{
				return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
			}
		} catch (RuntimeCustomException rex) {
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
		}
	}

	// --------------Terminate the Process------------------
	@RequestMapping(path = "/terminateProcess", method = RequestMethod.POST)
	//@ApiOperation(value = "terminating the process", notes = "Terminating the process from the console",response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> terminateProcess(@RequestBody EtlOperationDetailVb vObject) {
		JSONExceptionCode jsonExceptionCode = null;
		try {
			ExceptionCode exceptionCode = etlOperationManagerDao.killProcess(vObject);
			jsonExceptionCode = new JSONExceptionCode(exceptionCode.getErrorCode(),exceptionCode.getErrorMsg(), exceptionCode.getResponse(),exceptionCode.getOtherInfo());
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);	
		}catch(RuntimeCustomException rex){
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
		}
	}
}
