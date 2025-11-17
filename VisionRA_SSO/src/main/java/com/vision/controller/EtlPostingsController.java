package com.vision.controller;

import java.util.ArrayList;
import java.util.List;

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
import com.vision.vb.EtlPostingsVb;
import com.vision.wb.ETLPostingWb;

@RestController
@RequestMapping("etlPosting")
//@Api(value = "etlPosting", description = "ETL Posting")
public class EtlPostingsController {

	@Autowired
	ETLPostingWb etlPostingWb;

	/*-------------------------------------ETL POSTING SCREEN PAGE LOAD------------------------------------------*/
	@RequestMapping(path = "/pageLoadValues", method = RequestMethod.GET)
	//@ApiOperation(value = "Page Load Values", notes = "Load AT/NT Values on screen load", response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> pageOnLoad() {
		JSONExceptionCode jsonExceptionCode = null;
		try {
			EtlPostingsVb vObj = new EtlPostingsVb();
			vObj.setActionType("Query");
			ExceptionCode exceptionCode = etlPostingWb.doValidate(vObj);
			if (exceptionCode.getErrorCode() == Constants.ERRONEOUS_OPERATION) {
				jsonExceptionCode = new JSONExceptionCode(exceptionCode.getErrorCode(), exceptionCode.getErrorMsg(), null);
				return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
			}
			ArrayList arrayList = new ArrayList<>();
			arrayList = etlPostingWb.getPageLoadValues();
			if (arrayList != null && !arrayList.isEmpty())
				jsonExceptionCode = new JSONExceptionCode(Constants.SUCCESSFUL_OPERATION, "On Load Values",
						arrayList);
			else
				jsonExceptionCode = new JSONExceptionCode(Constants.NO_RECORDS_FOUND, "On Load Values",
						new ArrayList<>());
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		} catch (RuntimeCustomException rex) {
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		}
	}

	/*--------------get Query Results------------------*/
	@RequestMapping(path = "/getFeedDetails", method = RequestMethod.POST)
	//@ApiOperation(value = "Get All the details", notes = "Fetch all the existing records from the table", response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> getAllQueryResults(@RequestBody EtlPostingsVb vObject) {
		JSONExceptionCode jsonExceptionCode = null;
		try {
			vObject.setActionType("Query");
			if (vObject.getBusinessDate().contains("Sept")) {
				vObject.setBusinessDate(vObject.getBusinessDate().replaceAll("Sept", "Sep"));
			}
			ExceptionCode exceptionCode = etlPostingWb.getAllQueryPopupResult(vObject);
			jsonExceptionCode = new JSONExceptionCode(exceptionCode.getErrorCode(), exceptionCode.getErrorMsg(),exceptionCode.getResponse(), exceptionCode.getOtherInfo());
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		} catch (RuntimeCustomException rex) {
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
		}
	}

	/*-------------------------------------ADD ETL Posting ------------------------------------------*/
	@RequestMapping(path = "/addEtlPosting", method = RequestMethod.POST)
	//@ApiOperation(value = "Add ETL Posting", notes = "Add ETL Posting", response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> addEtlPosting(@RequestBody List<EtlPostingsVb> etlPostinglst) {
		JSONExceptionCode jsonExceptionCode = null;
		ExceptionCode exceptionCode = new ExceptionCode();
		EtlPostingsVb dObj = new EtlPostingsVb();
		try {
			if (etlPostinglst != null && !etlPostinglst.isEmpty()) {
				dObj = etlPostinglst.get(0);
			}
			exceptionCode.setOtherInfo(dObj);
			etlPostinglst.get(0).setActionType("Add");
			exceptionCode = etlPostingWb.insertRecord(exceptionCode, etlPostinglst);
			jsonExceptionCode = new JSONExceptionCode(exceptionCode.getErrorCode(), exceptionCode.getErrorMsg(),exceptionCode.getResponse(), exceptionCode.getOtherInfo());
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		} catch (RuntimeCustomException rex) {
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
		}
	}
}
