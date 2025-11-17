package com.vision.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vision.exception.ExceptionCode;
import com.vision.exception.JSONExceptionCode;
import com.vision.exception.RuntimeCustomException;
import com.vision.util.Constants;
import com.vision.vb.VisionVariablesVb;
import com.vision.wb.VisionVariablesWb;

@RestController
@RequestMapping("VisionVariables")
//@Api(value = "VisionVariables", description = "VisionVariables")
public class VisionVariablesController{
	@Autowired
	VisionVariablesWb visionVariablesWb;
	/*-------------------------------------VisionVariables SCREEN PAGE LOAD------------------------------------------*/
	@GetMapping("/pageLoadValues")
	//@ApiOperation(value = "Page Load Values", notes = "Load AT/NT Values on screen load", response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> pageOnLoad() {
		JSONExceptionCode jsonExceptionCode = null;
		try {
			VisionVariablesVb vObj = new VisionVariablesVb();
			vObj.setActionType("Query");
			ExceptionCode exceptionCode = visionVariablesWb.doValidate(vObj);
			if(exceptionCode.getErrorCode() == Constants.ERRONEOUS_OPERATION) {
				jsonExceptionCode = new JSONExceptionCode(exceptionCode.getErrorCode(), exceptionCode.getErrorMsg(), null);
				return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
			}

			ArrayList arrayList = visionVariablesWb.getPageLoadValues();
			jsonExceptionCode = new JSONExceptionCode(Constants.SUCCESSFUL_OPERATION, "On Load Values", arrayList);
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		} catch (RuntimeCustomException rex) {
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
		}
	}

	/*------------------------------------VisionVariables - FETCH HEADER RECORDS-------------------------------*/
	@PostMapping("/getAllQueryResults")
	//@ApiOperation(value = "Get All profile Data",notes = "Fetch all the existing records from the table",response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> getAllQueryResults(@RequestBody VisionVariablesVb vObject) {
		JSONExceptionCode jsonExceptionCode  = null;
		try{
			vObject.setActionType("Query");
			ExceptionCode exceptionCode = visionVariablesWb.getAllQueryPopupResult(vObject);
				jsonExceptionCode = new JSONExceptionCode(exceptionCode.getErrorCode(), exceptionCode.getErrorMsg(),exceptionCode.getResponse(), exceptionCode.getOtherInfo());
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		}catch(RuntimeCustomException rex){
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
		}	
	}
	@PostMapping("/getQueryDetails")
	//@ApiOperation(value = "Get All ADF Schules",notes = "ADF Schules Details",response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> queryDetails(@RequestBody VisionVariablesVb vObject) {
		JSONExceptionCode jsonExceptionCode  = null;
		try{
			vObject.setActionType("Query");
			ExceptionCode  exceptionCode= visionVariablesWb.getQueryResults(vObject);
			jsonExceptionCode = new JSONExceptionCode(exceptionCode.getErrorCode(), exceptionCode.getErrorMsg(), exceptionCode.getResponse(),exceptionCode.getOtherInfo());
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		}catch(RuntimeCustomException rex){
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
		}	
	}
	/*-------------------------------------MODIFY VisionVariables------------------------------------------*/
	@PostMapping("/saveVisionVariables")
	//@ApiOperation(value = "Modify VisionVariables", notes = "Modify VisionVariables Values", response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> modify(@RequestBody List<VisionVariablesVb>  vObjects) {
		JSONExceptionCode jsonExceptionCode = null;
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			VisionVariablesVb vObject = new VisionVariablesVb();
			vObjects.get(0).setActionType("Add");
			vObject = vObjects.get(0);
			exceptionCode.setOtherInfo(vObject);
			exceptionCode = visionVariablesWb.modifyRecord(exceptionCode, vObjects);
			jsonExceptionCode = new JSONExceptionCode(exceptionCode.getErrorCode(), exceptionCode.getErrorMsg(),
					exceptionCode.getOtherInfo());
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		} catch (RuntimeCustomException rex) {
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
		}
	}
	/*-------------------------------------DELETE VisionVariables------------------------------------------*/
	@PostMapping("/deleteVisionVariables")
	//@ApiOperation(value = "Delete VisionVariables", notes = "Delete existing VisionVariables", response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> delete(@RequestBody List<VisionVariablesVb>  vObjects) {
		JSONExceptionCode jsonExceptionCode = null;
		ExceptionCode exceptionCode = new ExceptionCode();
		ExceptionCode pRequestCode = new ExceptionCode();
		try {
			VisionVariablesVb vObject = new VisionVariablesVb();
			vObjects.get(0).setActionType("Delete");
			vObject = vObjects.get(0);
			pRequestCode.setOtherInfo(vObject);
			exceptionCode = visionVariablesWb.deleteRecord(pRequestCode, vObjects);
			jsonExceptionCode = new JSONExceptionCode(exceptionCode.getErrorCode(), exceptionCode.getErrorMsg(),
					exceptionCode.getOtherInfo());
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		} catch (RuntimeCustomException rex) {
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
		}
	}
	
	@PostMapping("/bulkApproveVisionVariables")
	//@ApiOperation(value = "Approve VisionVariables", notes = "Approve existing VisionVariables", response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> bulkApprove(@RequestBody List<VisionVariablesVb> vObjects) {
		JSONExceptionCode jsonExceptionCode = null;
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			VisionVariablesVb vObject = new VisionVariablesVb();
			vObjects.get(0).setActionType("Approve");
			vObject = vObjects.get(0);
			exceptionCode.setOtherInfo(vObject);
			exceptionCode = visionVariablesWb.bulkApprove(vObjects, vObject);
			String errorMessage= exceptionCode.getErrorMsg().replaceAll("- Approve -", "- Bulk Approve -");
			jsonExceptionCode = new JSONExceptionCode(exceptionCode.getErrorCode(), errorMessage,
					exceptionCode.getOtherInfo());
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		} catch (RuntimeCustomException rex) {
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
		}
	}
	@PostMapping("/bulkRejectVisionVariables")
	//@ApiOperation(value = "Approve VisionVariables", notes = "Approve existing VisionVariables", response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> bulkReject(@RequestBody List<VisionVariablesVb> vObjects) {
		JSONExceptionCode jsonExceptionCode = null;
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			VisionVariablesVb vObject = new VisionVariablesVb();
			vObjects.get(0).setActionType("Reject");
			vObject = vObjects.get(0);
			exceptionCode.setOtherInfo(vObject);
			exceptionCode = visionVariablesWb.bulkReject(vObjects, vObject);
			String errorMessage= exceptionCode.getErrorMsg().replaceAll("- Reject -", "- Bulk Reject -");
			jsonExceptionCode = new JSONExceptionCode(exceptionCode.getErrorCode(), errorMessage,
					exceptionCode.getOtherInfo());
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		} catch (RuntimeCustomException rex) {
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
		}
	}	
	
	@PostMapping("/reviewVisionVariables")
	//@ApiOperation(value = "Get Headers", notes = "Fetch all the existing records from the table", response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> review(@RequestBody VisionVariablesVb vObject) {
		JSONExceptionCode jsonExceptionCode = null;
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			vObject.setActionType("Query");
			exceptionCode = visionVariablesWb.doValidate(vObject);
			if(exceptionCode.getErrorCode() == Constants.ERRONEOUS_OPERATION){
				jsonExceptionCode = new JSONExceptionCode(exceptionCode.getErrorCode(), exceptionCode.getErrorMsg(),exceptionCode.getResponse(), exceptionCode.getOtherInfo());
			}
			exceptionCode = visionVariablesWb.reviewRecordNew(vObject);
			jsonExceptionCode = new JSONExceptionCode(exceptionCode.getErrorCode(), exceptionCode.getErrorMsg(), exceptionCode.getResponse());			
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		} catch (RuntimeCustomException rex) {
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
		}
	}	

}
