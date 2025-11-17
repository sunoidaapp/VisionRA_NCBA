package com.vision.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.vision.exception.ExceptionCode;
import com.vision.exception.JSONExceptionCode;
import com.vision.exception.RuntimeCustomException;
import com.vision.util.Constants;
import com.vision.vb.ReconHeaderVb;
import com.vision.wb.ReconWb;

@RestController
@RequestMapping("reconConfig")
//@Api(value = "reconConfig", description = "reconConfig Configuration and Details")

public class ReconConfigController {

	@Autowired
	ReconWb reconWb;
	
	@RequestMapping(path = "/getAllQueryResults", method = RequestMethod.POST)
	//@ApiOperation(value = "Get All the details", notes ="Fetch all the existing records from the table",response =ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> getAllQueryResults(@RequestBody ReconHeaderVb vObject){
		JSONExceptionCode jsonExceptionCode = null;
		try {
			vObject.setActionType("Query");
			ExceptionCode exceptionCode = reconWb.getQueryResults(vObject);
			jsonExceptionCode = new JSONExceptionCode(exceptionCode.getErrorCode(), exceptionCode.getErrorMsg(),exceptionCode.getResponse(), exceptionCode.getOtherInfo());
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);	
		}catch(RuntimeCustomException rex){
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
		}
	}

	/*-------------------------------------Recon config SCREEN PAGE LOAD------------------------------------------*/
	
	@RequestMapping(path = "/pageLoadValues", method = RequestMethod.GET)
	//@ApiOperation(value = "Page Load Values", notes ="Load AT/NT Values on screen load", response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> pageOnLoad() {
		JSONExceptionCode jsonExceptionCode = null;
		try {
			ReconHeaderVb vObj = new ReconHeaderVb();
			vObj.setActionType("Query");
			ExceptionCode exceptionCode = reconWb.doValidate(vObj);
			if(exceptionCode.getErrorCode() == Constants.ERRONEOUS_OPERATION) {
				jsonExceptionCode = new JSONExceptionCode(exceptionCode.getErrorCode(), exceptionCode.getErrorMsg(), null);
				return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
			}
			ArrayList arrayList = reconWb.getPageLoadValues();
			jsonExceptionCode = new JSONExceptionCode(Constants.SUCCESSFUL_OPERATION, "On Load Values", arrayList);
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		} catch (RuntimeCustomException rex) {
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
		}
	}

	/*--------------get Table Names ------------------*/
	@RequestMapping(path = "/getTableNames", method = RequestMethod.POST)
	//@ApiOperation(value = "Get All the Table Names", notes ="Fetch all the existing table name ", response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> getAllTableName(@RequestBody ReconHeaderVb vObject) {
		JSONExceptionCode jsonExceptionCode = null;
		try {
			vObject.setActionType("Query");
			ExceptionCode exceptionCode = reconWb.getReconTableName(vObject);
			jsonExceptionCode = new JSONExceptionCode(exceptionCode.getErrorCode(), exceptionCode.getErrorMsg(),exceptionCode.getResponse(), exceptionCode.getOtherInfo());
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		} catch (RuntimeCustomException rex) {
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
		}
	}
	/*--------------get column Names ------------------*/
	@RequestMapping(path = "/getColumnNames", method = RequestMethod.POST)
	//@ApiOperation(value = "Get All the Table Names", notes ="Fetch all the existing table name ", response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> getAllColumnName(@RequestBody List<ReconHeaderVb> vObjects) {
		JSONExceptionCode jsonExceptionCode = null;
		try {
			vObjects.get(0).setActionType("Query");
			ExceptionCode exceptionCode = reconWb.getReconColName(vObjects);
			jsonExceptionCode = new JSONExceptionCode(exceptionCode.getErrorCode(), exceptionCode.getErrorMsg(),exceptionCode.getResponse(), exceptionCode.getOtherInfo());
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		} catch (RuntimeCustomException rex) {
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
		}
	}
	/*-------------------------------------Adding Table Records-------------------------------*/
	@RequestMapping(path = "/addReconTable", method = RequestMethod.POST)
	//@ApiOperation(value = "Add Recon Table", notes = "Add Recon Table", response= ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> addReconTable(@RequestBody List<ReconHeaderVb> vObjects) {
		JSONExceptionCode jsonExceptionCode = null;
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			vObjects.get(0).setActionType("Add");
			exceptionCode.setOtherInfo(vObjects.get(0));
			exceptionCode = reconWb.insertRecord(exceptionCode,vObjects);
			jsonExceptionCode = new JSONExceptionCode(exceptionCode.getErrorCode(), exceptionCode.getErrorMsg(), exceptionCode.getOtherInfo());
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		} catch (RuntimeCustomException rex) {
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
		}
	}
	/*-------------------------------------Modify Table Records-------------------------------*/
	@RequestMapping(path = "/modifyReconTable", method = RequestMethod.POST)
	//@ApiOperation(value = "Modify Recon Table", notes = "Modify Recon Table",response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> modifyReconTable(@RequestBody List<ReconHeaderVb> vObjects) {
		JSONExceptionCode jsonExceptionCode = null;
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			vObjects.get(0).setActionType("Modify");
			exceptionCode.setOtherInfo(vObjects.get(0));
			exceptionCode = reconWb.modifyRecord(exceptionCode,vObjects);
			jsonExceptionCode = new JSONExceptionCode(exceptionCode.getErrorCode(), exceptionCode.getErrorMsg(), exceptionCode.getOtherInfo());
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		} catch (RuntimeCustomException rex) {
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
		}
	}
	/*-------------------------------------Delete Table Records-------------------------------*/
	@RequestMapping(path = "/deleteReconTable", method = RequestMethod.POST)
	//@ApiOperation(value = "Delete Recon Table", notes = "Delete Recon Table",response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> deleteReconTable(@RequestBody ReconHeaderVb vObject) {
		JSONExceptionCode jsonExceptionCode = null;
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			vObject.setActionType("Delete");
			exceptionCode = reconWb.deleteRecord(vObject);
			jsonExceptionCode = new JSONExceptionCode(exceptionCode.getErrorCode(), exceptionCode.getErrorMsg(), exceptionCode.getOtherInfo());
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		} catch (RuntimeCustomException rex) {
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
		}
	}
	/*-------------------------------------Approve Table Records-------------------------------*/
	@RequestMapping(path = "/approveReconTable", method = RequestMethod.POST)
	//@ApiOperation(value = "Approve Recon Table", notes = "Approve Recon Table",response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> approveReconTable(@RequestBody List<ReconHeaderVb> vObjects) {
		JSONExceptionCode jsonExceptionCode = null;
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			ReconHeaderVb reconHeaderVb = new ReconHeaderVb();
			vObjects.get(0).setActionType("Approve");
			reconHeaderVb = vObjects.get(0);
			exceptionCode = reconWb.bulkApprove(vObjects, reconHeaderVb);
			jsonExceptionCode = new JSONExceptionCode(exceptionCode.getErrorCode(), exceptionCode.getErrorMsg(), exceptionCode.getOtherInfo());
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		} catch (RuntimeCustomException rex) {
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
		}
	}
	
	/*-------------------------------------Reject Table Records-------------------------------*/
	@RequestMapping(path = "/rejectReconTable", method = RequestMethod.POST)
	//@ApiOperation(value = "Reject Recon Table", notes = "Reject Recon Table",response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> rejectReconTable(@RequestBody List<ReconHeaderVb> vObjects) {
		JSONExceptionCode jsonExceptionCode = null;
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			ReconHeaderVb reconHeaderVb = new ReconHeaderVb();
			vObjects.get(0).setActionType("Reject");
			reconHeaderVb = vObjects.get(0);
			exceptionCode = reconWb.bulkReject(vObjects, reconHeaderVb);
			jsonExceptionCode = new JSONExceptionCode(exceptionCode.getErrorCode(), exceptionCode.getErrorMsg(), exceptionCode.getOtherInfo());
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		} catch (RuntimeCustomException rex) {
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
		}
	}
	/*--------------get Table Names ------------------*/
	@RequestMapping(path = "/getReconQuery", method = RequestMethod.POST)
	//@ApiOperation(value = "Show Query", notes ="Fetch all the existing table name ", response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> showQuery(@RequestBody ReconHeaderVb vObject) {
		JSONExceptionCode jsonExceptionCode = null;
		try {
			vObject.setActionType("Query");
			ExceptionCode exceptionCode = reconWb.showQuery(vObject);
			jsonExceptionCode = new JSONExceptionCode(exceptionCode.getErrorCode(), exceptionCode.getErrorMsg(),exceptionCode.getResponse(), exceptionCode.getOtherInfo());
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		} catch (RuntimeCustomException rex) {
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
		}
	}
	/*--------------Validate Query ------------------*/
	@RequestMapping(path = "/validateQuery", method = RequestMethod.POST)
	//@ApiOperation(value = "Validate Query", notes ="Fetch all the existing table name ", response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> validateQuery(@RequestBody ReconHeaderVb vObject) {
		JSONExceptionCode jsonExceptionCode = null;
		try {
			vObject.setActionType("Query");
			ExceptionCode exceptionCode = reconWb.validateQuery(vObject);
			jsonExceptionCode = new JSONExceptionCode(exceptionCode.getErrorCode(), exceptionCode.getErrorMsg(),exceptionCode.getResponse(), exceptionCode.getOtherInfo());
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		} catch (RuntimeCustomException rex) {
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
		}
	}
	/*--------------get Table Names ------------------*/
	@GetMapping(path = "/getDestinationRuleIdsforCopy")
	//@ApiOperation(value = "Show Query", notes ="Fetch all the existing table name ", response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> getToRuleIdforCopy() {
		JSONExceptionCode jsonExceptionCode = null;
		try {
			ReconHeaderVb reconHeaderVb = new ReconHeaderVb();
			reconHeaderVb.setActionType("Query");
			ExceptionCode exceptionCode = reconWb.getToTableId(reconHeaderVb);
			jsonExceptionCode = new JSONExceptionCode(exceptionCode.getErrorCode(), exceptionCode.getErrorMsg(),exceptionCode.getResponse(), exceptionCode.getOtherInfo());
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		} catch (RuntimeCustomException rex) {
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
		}
	}
}
