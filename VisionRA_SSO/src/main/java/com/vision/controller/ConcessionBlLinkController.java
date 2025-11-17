package com.vision.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.vision.dao.ConcessionBlLinkDao;
import com.vision.exception.ExceptionCode;
import com.vision.exception.JSONExceptionCode;
import com.vision.exception.RuntimeCustomException;
import com.vision.util.Constants;
import com.vision.vb.ConcessionBlLinkVb;
import com.vision.vb.MenuVb;
import com.vision.wb.ConcessionBlLinkWb;

/*@RestController
@RequestMapping("concessionBlLink")
//@Api(value = "concessionBlLink", description = "Concession BL Link")*/
public class ConcessionBlLinkController {
	@Autowired
	ConcessionBlLinkWb concessionBlLinkWb;

	@Autowired
	ConcessionBlLinkDao concessionBlLinkDao;

	/*-------------------------------------Concession BL Link SCREEN PAGE LOAD------------------------------------------*/
	@RequestMapping(path = "/pageLoadValues", method = RequestMethod.POST)
	//@ApiOperation(value = "Page Load Values", notes = "Load AT/NT Values on screen load", response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> pageOnLoad(@RequestBody ConcessionBlLinkVb vObject) {
		JSONExceptionCode jsonExceptionCode = null;
		try {
			MenuVb menuVb = new MenuVb();
			menuVb.setActionType("Clear");
			ArrayList arrayList = concessionBlLinkWb.getPageLoadValues(vObject);
			jsonExceptionCode = new JSONExceptionCode(Constants.SUCCESSFUL_OPERATION, "Page Load Values", arrayList);
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		} catch (RuntimeCustomException rex) {
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		}
	}

	/*--------------get Query Results------------------*/
	@RequestMapping(path = "/getQueryDetails", method = RequestMethod.POST)
	//@ApiOperation(value = "Get All the details", notes = "Fetch all the existing records from the table", response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> getQueryResults(@RequestBody ConcessionBlLinkVb vObject) {
		JSONExceptionCode jsonExceptionCode = null;
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			vObject.setActionType("Query");
			exceptionCode = concessionBlLinkWb.getQueryResults(vObject);
			jsonExceptionCode = new JSONExceptionCode(Constants.SUCCESSFUL_OPERATION, exceptionCode.getErrorMsg(),
					exceptionCode.getResponse(), exceptionCode.getOtherInfo());
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		} catch (RuntimeCustomException rex) {
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
		}
	}

	/*-------------------------------------ADD Concession Bl Link------------------------------------------*/
	@RequestMapping(path = "/addconcessionBlLink", method = RequestMethod.POST)
	//@ApiOperation(value = "Add ConcessionBlLink", notes = "Add Concession Bl Link", response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> addconcessionActivityConfig(
			@RequestBody List<ConcessionBlLinkVb> concessionLinkVbList) {
		JSONExceptionCode jsonExceptionCode = null;
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			exceptionCode.setOtherInfo(concessionLinkVbList.get(0));
			exceptionCode = concessionBlLinkWb.insertRecord(exceptionCode, concessionLinkVbList);
			if (exceptionCode.getErrorCode() == Constants.SUCCESSFUL_OPERATION) {
				jsonExceptionCode = new JSONExceptionCode(Constants.SUCCESSFUL_OPERATION, exceptionCode.getErrorMsg(),
						exceptionCode.getOtherInfo());
			} else {
				jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, exceptionCode.getErrorMsg(),
						exceptionCode.getOtherInfo());
			}
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		} catch (RuntimeCustomException rex) {
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
		}
	}

	/*-------------------------------------Modify Concession Bl Link------------------------------------------*/
	@RequestMapping(path = "/modifyconcessionBlLink", method = RequestMethod.POST)
	//@ApiOperation(value = "Modify ConcessionBlLink", notes = "Modify Concession Bl Link", response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> modifyconcessionActivityConfig(
			@RequestBody List<ConcessionBlLinkVb> vObjects) {
		JSONExceptionCode jsonExceptionCode = null;
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			exceptionCode.setOtherInfo(vObjects.get(0));
			exceptionCode = concessionBlLinkWb.modifyRecord(exceptionCode, vObjects);
			if (exceptionCode.getErrorCode() == Constants.SUCCESSFUL_OPERATION) {
				jsonExceptionCode = new JSONExceptionCode(Constants.SUCCESSFUL_OPERATION, exceptionCode.getErrorMsg(),
						exceptionCode.getOtherInfo());
			} else {
				jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, exceptionCode.getErrorMsg(),
						exceptionCode.getOtherInfo());
			}
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		} catch (RuntimeCustomException rex) {
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
		}
	}

	/*-------------------------------------Delete Concession Bl Link------------------------------------------*/
	@RequestMapping(path = "/deleteconcessionBlLink", method = RequestMethod.POST)
	//@ApiOperation(value = "delete ConcessionBlLink", notes = "delete Concession Bl Link", response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> deleteconcessionActivityConfig(@RequestBody ConcessionBlLinkVb vObject) {
		JSONExceptionCode jsonExceptionCode = null;
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			vObject.setActionType("Delete");
			exceptionCode = concessionBlLinkWb.deleteRecord(vObject);
			if (exceptionCode.getErrorCode() == Constants.SUCCESSFUL_OPERATION) {
				jsonExceptionCode = new JSONExceptionCode(Constants.SUCCESSFUL_OPERATION, exceptionCode.getErrorMsg(),exceptionCode.getOtherInfo());
			} else {
				jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, exceptionCode.getErrorMsg(),exceptionCode.getOtherInfo());
			}
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		} catch (RuntimeCustomException rex) {
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
		}
	}

}