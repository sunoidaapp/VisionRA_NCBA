package com.vision.controller;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.vision.exception.ExceptionCode;
import com.vision.exception.JSONExceptionCode;
import com.vision.exception.RuntimeCustomException;
import com.vision.util.Constants;
import com.vision.vb.EtlManagerVb;
import com.vision.vb.EtlScheduleVb;
import com.vision.wb.EtlManagerWb;

@RestController
@RequestMapping("etlManager")
//@Api(value = "etlManager", description = "ETL Manager")
public class EtlManagerController {

	@Autowired
	EtlManagerWb etlManagerWb;

	/*------------------------------------ON LOAD VALUES------------------------------------------*/
	@RequestMapping(path = "/pageLoadValues", method = RequestMethod.GET)
	//@ApiOperation(value = "Page Load Values", notes = "Load AT/NT Values on screen load", response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> pageOnLoad() {
		JSONExceptionCode jsonExceptionCode = null;
		try {
			EtlManagerVb vObj = new EtlManagerVb();
			vObj.setActionType("Query");
			ExceptionCode exceptionCode = etlManagerWb.doValidate(vObj);
			if(exceptionCode.getErrorCode() == Constants.ERRONEOUS_OPERATION) {
				jsonExceptionCode = new JSONExceptionCode(exceptionCode.getErrorCode(), exceptionCode.getErrorMsg(), null);
				return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
			}
			ArrayList arrayList = etlManagerWb.getPageLoadValues();
			jsonExceptionCode = new JSONExceptionCode(Constants.SUCCESSFUL_OPERATION, "On Load Values", arrayList);
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		} catch (RuntimeCustomException rex) {
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		}
	}

	/*--------------get Query Results------------------*/
	@RequestMapping(path = "/getAllQueryResults", method = RequestMethod.POST)
	//@ApiOperation(value = "Get All the details", notes = "Fetch all the existing records from the table", response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> getAllQueryResults(@RequestBody EtlManagerVb vObject) {
		JSONExceptionCode jsonExceptionCode = null;
		try {
			vObject.setActionType("Query");
			ExceptionCode exceptionCode = etlManagerWb.getAllQueryPopupResult(vObject);
			jsonExceptionCode = new JSONExceptionCode(exceptionCode.getErrorCode(), exceptionCode.getErrorMsg(),exceptionCode.getResponse(), exceptionCode.getOtherInfo());
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		} catch (RuntimeCustomException rex) {
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
		}
	}

	/*--------------get Query Results------------------*/
	@RequestMapping(path = "/getQueryDetails", method = RequestMethod.POST)
	//@ApiOperation(value = "Get All the details", notes = "Fetch all the existing records from the table", response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> getAllQueryDetails(@RequestBody EtlManagerVb vObject) {
		JSONExceptionCode jsonExceptionCode = null;
		try {
			vObject.setActionType("Query");
			ExceptionCode exceptionCode = etlManagerWb.getQueryResults(vObject);
			jsonExceptionCode = new JSONExceptionCode(exceptionCode.getErrorCode(), exceptionCode.getErrorMsg(),exceptionCode.getResponse(), exceptionCode.getOtherInfo());
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		} catch (RuntimeCustomException rex) {
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
		}
	}

	/*-------------------------------------Add Etl Manager------------------------------------------*/
	@RequestMapping(path = "/addEtlManager", method = RequestMethod.POST)
	//@ApiOperation(value = "Add Etl Manager", notes = "Add Etl Manager", response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> addEtlManager(@RequestBody EtlManagerVb vObject) {
		JSONExceptionCode jsonExceptionCode = null;
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			vObject.setActionType("Add");
			exceptionCode = etlManagerWb.insertRecord(vObject);
			jsonExceptionCode = new JSONExceptionCode(exceptionCode.getErrorCode(), exceptionCode.getErrorMsg(),exceptionCode.getResponse(), exceptionCode.getOtherInfo());
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		} catch (RuntimeCustomException rex) {
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
		}
	}

	/*-------------------------------------Modify Etl Manager------------------------------------------*/
	@RequestMapping(path = "/modifyEtlManager", method = RequestMethod.POST)
	//@ApiOperation(value = "Modify Etl Manager", notes = "Modify Etl Manager", response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> modifyEtlManager(@RequestBody EtlManagerVb vObject) {
		JSONExceptionCode jsonExceptionCode = null;
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			vObject.setActionType("Modify");
			exceptionCode = etlManagerWb.modifyRecord(vObject);
			jsonExceptionCode = new JSONExceptionCode(exceptionCode.getErrorCode(), exceptionCode.getErrorMsg(),exceptionCode.getResponse(), exceptionCode.getOtherInfo());
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		} catch (RuntimeCustomException rex) {
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
		}
	}

	/*-------------------------------------Delete Etl Manager------------------------------------------*/
	@RequestMapping(path = "/deleteEtlManager", method = RequestMethod.POST)
	//@ApiOperation(value = "delete Etl Manager", notes = "delete Etl Manager", response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> deleteEtlManager(@RequestBody EtlManagerVb vObject) {
		JSONExceptionCode jsonExceptionCode = null;
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			vObject.setActionType("Delete");
			exceptionCode = etlManagerWb.deleteRecord(vObject);
			jsonExceptionCode = new JSONExceptionCode(exceptionCode.getErrorCode(), exceptionCode.getErrorMsg(),exceptionCode.getResponse(), exceptionCode.getOtherInfo());
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		} catch (RuntimeCustomException rex) {
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
		}
	}

	/*-------------------------------------ETL Schedule------------------------------------------*/
	@RequestMapping(path = "/addEtlSchedule", method = RequestMethod.POST)
	//@ApiOperation(value = "Add ETL Schedule", notes = "Add ETL Schedule", response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> addEtlSchedule(@RequestBody EtlScheduleVb vObject) {
		JSONExceptionCode jsonExceptionCode = null;
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			vObject.setActionType("Add");
			exceptionCode = etlManagerWb.addEtlSchedule(vObject);
			jsonExceptionCode = new JSONExceptionCode(exceptionCode.getErrorCode(), exceptionCode.getErrorMsg(),exceptionCode.getResponse(), exceptionCode.getOtherInfo());
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		} catch (RuntimeCustomException rex) {
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
		}
	}

	/*-------------------------------------ETL Schedule------------------------------------------*/
	@RequestMapping(path = "/deleteEtlSchedule", method = RequestMethod.POST)
	//@ApiOperation(value = "Delete ETL Schedule", notes = "Delete ETL Schedule", response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> deleteEtlSchedule(@RequestBody EtlScheduleVb vObject) {
		JSONExceptionCode jsonExceptionCode = null;
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			vObject.setActionType("Delete");
			exceptionCode = etlManagerWb.deleteEtlSchedule(vObject);
			jsonExceptionCode = new JSONExceptionCode(exceptionCode.getErrorCode(), exceptionCode.getErrorMsg(),exceptionCode.getResponse(), exceptionCode.getOtherInfo());
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		} catch (RuntimeCustomException rex) {
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
		}
	}

	/*-------------------------------------ETL Schedule------------------------------------------*/
	@RequestMapping(path = "/queryEtlSchedule", method = RequestMethod.POST)
	//@ApiOperation(value = "Query ETL Schedule", notes = "Delete ETL Schedule", response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> queryEtlSchedule(@RequestBody EtlScheduleVb vObject) {
		JSONExceptionCode jsonExceptionCode = null;
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			vObject.setActionType("Query");
			exceptionCode = etlManagerWb.getQueryEtlSchedule(vObject);
			jsonExceptionCode = new JSONExceptionCode(exceptionCode.getErrorCode(), exceptionCode.getErrorMsg(),exceptionCode.getResponse(), exceptionCode.getOtherInfo());
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		} catch (RuntimeCustomException rex) {
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
		}
	}
}
