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
import com.vision.vb.TaxReconHeaderVb;
import com.vision.wb.TaxReconWb;

@RestController
@RequestMapping("taxReconConfig")
//@Api(value = "TaxReconConfig", description = "TaxReconConfig Configuration and Details")

public class TaxReconConfigController {

	@Autowired
	TaxReconWb taxReconWb;
	
	@RequestMapping(path = "/getAllQueryResults", method = RequestMethod.POST)
	//@ApiOperation(value = "Get All the details", notes ="Fetch all the existing records from the table",response =ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> getAllQueryResults(@RequestBody TaxReconHeaderVb vObject){
		JSONExceptionCode jsonExceptionCode = null;
		try {
			vObject.setActionType("Query");
			ExceptionCode exceptionCode = taxReconWb.getQueryResults(vObject);
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
			TaxReconHeaderVb vObj = new TaxReconHeaderVb();
			vObj.setActionType("Query");
			ExceptionCode exceptionCode = taxReconWb.doValidate(vObj);
			if(exceptionCode.getErrorCode() == Constants.ERRONEOUS_OPERATION) {
				jsonExceptionCode = new JSONExceptionCode(exceptionCode.getErrorCode(), exceptionCode.getErrorMsg(), null);
				return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
			}
			ArrayList arrayList = taxReconWb.getPageLoadValues();
			jsonExceptionCode = new JSONExceptionCode(Constants.SUCCESSFUL_OPERATION, "On Load Values", arrayList);
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		} catch (RuntimeCustomException rex) {
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
		}
	}

	/*--------------get Table Names ------------------*/
	@RequestMapping(path = "/getTaxTableNames", method = RequestMethod.POST)
	//@ApiOperation(value = "Get All the Table Names", notes ="Fetch all the existing table name ", response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> getAllTableName(@RequestBody TaxReconHeaderVb vObject) {
		JSONExceptionCode jsonExceptionCode = null;
		try {
			vObject.setActionType("Query");
			ExceptionCode exceptionCode = taxReconWb.getReconTableName(vObject);
			jsonExceptionCode = new JSONExceptionCode(exceptionCode.getErrorCode(), exceptionCode.getErrorMsg(),exceptionCode.getResponse(), exceptionCode.getOtherInfo());
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		} catch (RuntimeCustomException rex) {
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
		}
	}
	/*--------------get column Names ------------------*/
	@RequestMapping(path = "/getTaxColumnNames", method = RequestMethod.POST)
	//@ApiOperation(value = "Get All the Table Names", notes ="Fetch all the existing table name ", response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> getAllColumnName(@RequestBody List<TaxReconHeaderVb> vObjects) {
		JSONExceptionCode jsonExceptionCode = null;
		try {
			vObjects.get(0).setActionType("Query");
			ExceptionCode exceptionCode = taxReconWb.getReconColName(vObjects);
			jsonExceptionCode = new JSONExceptionCode(exceptionCode.getErrorCode(), exceptionCode.getErrorMsg(),exceptionCode.getResponse(), exceptionCode.getOtherInfo());
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		} catch (RuntimeCustomException rex) {
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
		}
	}
	/*-------------------------------------Adding Table Records-------------------------------*/
	@RequestMapping(path = "/addTaxReconTable", method = RequestMethod.POST)
	//@ApiOperation(value = "Add Recon Table", notes = "Add Recon Table", response= ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> addReconTable(@RequestBody List<TaxReconHeaderVb> vObjects) {
		JSONExceptionCode jsonExceptionCode = null;
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			vObjects.get(0).setActionType("Add");
			exceptionCode.setOtherInfo(vObjects.get(0));
			exceptionCode = taxReconWb.insertRecord(exceptionCode,vObjects);
			jsonExceptionCode = new JSONExceptionCode(exceptionCode.getErrorCode(), exceptionCode.getErrorMsg(), exceptionCode.getOtherInfo());
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		} catch (RuntimeCustomException rex) {
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
		}
	}
	/*-------------------------------------Modify Table Records-------------------------------*/
	@RequestMapping(path = "/modifyTaxReconTable", method = RequestMethod.POST)
	//@ApiOperation(value = "Modify Recon Table", notes = "Modify Recon Table",response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> modifyReconTable(@RequestBody List<TaxReconHeaderVb> vObjects) {
		JSONExceptionCode jsonExceptionCode = null;
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			vObjects.get(0).setActionType("Modify");
			exceptionCode.setOtherInfo(vObjects.get(0));
			exceptionCode = taxReconWb.modifyRecord(exceptionCode,vObjects);
			jsonExceptionCode = new JSONExceptionCode(exceptionCode.getErrorCode(), exceptionCode.getErrorMsg(), exceptionCode.getOtherInfo());
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		} catch (RuntimeCustomException rex) {
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
		}
	}
	/*-------------------------------------Delete Table Records-------------------------------*/
	@RequestMapping(path = "/deleteTaxReconTable", method = RequestMethod.POST)
	//@ApiOperation(value = "Delete Recon Table", notes = "Delete Recon Table",response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> deleteReconTable(@RequestBody TaxReconHeaderVb vObject) {
		JSONExceptionCode jsonExceptionCode = null;
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			vObject.setActionType("Delete");
			exceptionCode = taxReconWb.deleteRecord(vObject);
			jsonExceptionCode = new JSONExceptionCode(exceptionCode.getErrorCode(), exceptionCode.getErrorMsg(), exceptionCode.getOtherInfo());
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		} catch (RuntimeCustomException rex) {
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
		}
	}
	/*-------------------------------------Approve Table Records-------------------------------*/
	@RequestMapping(path = "/approveTaxReconTable", method = RequestMethod.POST)
	//@ApiOperation(value = "Approve Recon Table", notes = "Approve Recon Table",response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> approveReconTable(@RequestBody List<TaxReconHeaderVb> vObjects) {
		JSONExceptionCode jsonExceptionCode = null;
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			TaxReconHeaderVb TaxReconHeaderVb = new TaxReconHeaderVb();
			vObjects.get(0).setActionType("Approve");
			TaxReconHeaderVb = vObjects.get(0);
			exceptionCode = taxReconWb.bulkApprove(vObjects, TaxReconHeaderVb);
			jsonExceptionCode = new JSONExceptionCode(exceptionCode.getErrorCode(), exceptionCode.getErrorMsg(), exceptionCode.getOtherInfo());
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		} catch (RuntimeCustomException rex) {
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
		}
	}
	
	/*-------------------------------------Reject Table Records-------------------------------*/
	@RequestMapping(path = "/rejectTaxReconTable", method = RequestMethod.POST)
	//@ApiOperation(value = "Reject Recon Table", notes = "Reject Recon Table",response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> rejectReconTable(@RequestBody List<TaxReconHeaderVb> vObjects) {
		JSONExceptionCode jsonExceptionCode = null;
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			TaxReconHeaderVb TaxReconHeaderVb = new TaxReconHeaderVb();
			vObjects.get(0).setActionType("Reject");
			TaxReconHeaderVb = vObjects.get(0);
			exceptionCode = taxReconWb.bulkReject(vObjects, TaxReconHeaderVb);
			jsonExceptionCode = new JSONExceptionCode(exceptionCode.getErrorCode(), exceptionCode.getErrorMsg(), exceptionCode.getOtherInfo());
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		} catch (RuntimeCustomException rex) {
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
		}
	}
	/*--------------get Table Names ------------------*/
	@RequestMapping(path = "/getTaxReconQuery", method = RequestMethod.POST)
	//@ApiOperation(value = "Show Query", notes ="Fetch all the existing table name ", response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> showQuery(@RequestBody TaxReconHeaderVb vObject) {
		JSONExceptionCode jsonExceptionCode = null;
		try {
			vObject.setActionType("Query");
			ExceptionCode exceptionCode = taxReconWb.showQuery(vObject);
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
	public ResponseEntity<JSONExceptionCode> validateQuery(@RequestBody TaxReconHeaderVb vObject) {
		JSONExceptionCode jsonExceptionCode = null;
		try {
			vObject.setActionType("Query");
			ExceptionCode exceptionCode = taxReconWb.validateQuery(vObject);
			jsonExceptionCode = new JSONExceptionCode(exceptionCode.getErrorCode(), exceptionCode.getErrorMsg(),exceptionCode.getResponse(), exceptionCode.getOtherInfo());
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		} catch (RuntimeCustomException rex) {
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
		}
	}
	/*--------------get Table Names ------------------*/
	@GetMapping(path = "/getTaxDestinationRuleIdsforCopy")
	//@ApiOperation(value = "Show Query", notes ="Fetch all the existing table name ", response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> getToRuleIdforCopy() {
		JSONExceptionCode jsonExceptionCode = null;
		try {
			TaxReconHeaderVb TaxReconHeaderVb = new TaxReconHeaderVb();
			TaxReconHeaderVb.setActionType("Query");
			ExceptionCode exceptionCode = taxReconWb.getToTableId(TaxReconHeaderVb);
			jsonExceptionCode = new JSONExceptionCode(exceptionCode.getErrorCode(), exceptionCode.getErrorMsg(),exceptionCode.getResponse(), exceptionCode.getOtherInfo());
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		} catch (RuntimeCustomException rex) {
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
		}
	}
}
