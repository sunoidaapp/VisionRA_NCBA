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
import com.vision.vb.FeesPriorityVb;
import com.vision.wb.FeesPriorityWb;

@RestController
@RequestMapping("feesPriority")
//@Api(value = "FeesPriority", description = "Ra Mst Fees Default Priority")
public class FeesPriorityController{
	@Autowired
	FeesPriorityWb feesPriorityWb;
	/*-------------------------------------Ra Mst Fees Default Priority SCREEN PAGE LOAD------------------------------------------*/
	@RequestMapping(path = "/pageLoadValues", method = RequestMethod.GET)
	//@ApiOperation(value = "Page Load Values", notes = "Load AT/NT Values on screen load", response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> pageOnLoad() {
		JSONExceptionCode jsonExceptionCode = null;
		try {
			FeesPriorityVb vObj = new FeesPriorityVb();
			vObj.setActionType("Query");
			ExceptionCode exceptionCode = feesPriorityWb.doValidate(vObj);
			if(exceptionCode.getErrorCode() == Constants.ERRONEOUS_OPERATION) {
				jsonExceptionCode = new JSONExceptionCode(exceptionCode.getErrorCode(), exceptionCode.getErrorMsg(), null);
				return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
			}
			ArrayList arrayList = feesPriorityWb.getPageLoadValues();
			jsonExceptionCode = new JSONExceptionCode(Constants.SUCCESSFUL_OPERATION, "On Load Values", arrayList);
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		} catch (RuntimeCustomException rex) {
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		}
	}

	/*------------------------------------Ra Mst Fees Default Priority - FETCH HEADER RECORDS-------------------------------*/
	@RequestMapping(path = "/getAllDefaultPriority", method = RequestMethod.POST)
	//@ApiOperation(value = "Get All Priority List",notes = "Fetch all the existing records from the table",response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> getAllDefaultPriority(@RequestBody FeesPriorityVb vObject) {
		JSONExceptionCode jsonExceptionCode  = null;
		try{
			vObject.setActionType("Query");
			ExceptionCode exceptionCode = feesPriorityWb.getAllQueryPopupResult(vObject);
			jsonExceptionCode = new JSONExceptionCode(exceptionCode.getErrorCode(), exceptionCode.getErrorMsg(),exceptionCode.getResponse(), exceptionCode.getOtherInfo());
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		}catch(RuntimeCustomException rex){
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		}	
	}
	/*------------------------------------Ra Mst Fees Detail Priority - FETCH HEADER RECORDS-------------------------------*/
	@RequestMapping(path = "/getAllDetailPriority", method = RequestMethod.POST)
	//@ApiOperation(value = "Get All Priority List",notes = "Fetch all the existing records from the table",response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> getAllDetailPriority(@RequestBody FeesPriorityVb vObject) {
		JSONExceptionCode jsonExceptionCode  = null;
		try{
			vObject.setActionType("Query");
			ExceptionCode exceptionCode = feesPriorityWb.getAllDetailQueryPopupResult(vObject);
			jsonExceptionCode = new JSONExceptionCode(exceptionCode.getErrorCode(), exceptionCode.getErrorMsg(),exceptionCode.getResponse(), exceptionCode.getOtherInfo());
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		}catch(RuntimeCustomException rex){
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		}	
	}
	@RequestMapping(path = "/getQueryDetails", method = RequestMethod.POST)
	//@ApiOperation(value = "Get Query for Priority Details",notes = "Get Query for Priority Details",response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> queryDetails(@RequestBody FeesPriorityVb vObject) {
		JSONExceptionCode jsonExceptionCode  = null;
		try{
			vObject.setActionType("Query");
			ExceptionCode  exceptionCode = feesPriorityWb.getQueryResults(vObject);
			jsonExceptionCode = new JSONExceptionCode(exceptionCode.getErrorCode(), exceptionCode.getErrorMsg(),exceptionCode.getResponse(), exceptionCode.getOtherInfo());
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		}catch(RuntimeCustomException rex){
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		}	
	}
	/*-------------------------------------ADD Ra Mst Fees Default Priority------------------------------------------*/
	@RequestMapping(path = "/addFeesPriority", method = RequestMethod.POST)
	//@ApiOperation(value = "Add Ra Mst Fees Default Priority", notes = "Add Ra Mst Fees Default Priority", response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> add(@RequestBody List<FeesPriorityVb> vObjects) {
		JSONExceptionCode jsonExceptionCode = null;
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			vObjects.get(0).setActionType("Add");
			exceptionCode.setOtherInfo(vObjects.get(0));
			exceptionCode = feesPriorityWb.insertRecord(exceptionCode,vObjects);
			jsonExceptionCode = new JSONExceptionCode(exceptionCode.getErrorCode(), exceptionCode.getErrorMsg(),
					exceptionCode.getOtherInfo());
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		} catch (RuntimeCustomException rex) {
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		}
	}
	/*-------------------------------------MODIFY Ra Mst Fees Default Priority------------------------------------------*/
	@RequestMapping(path = "/modifyFeesPriority", method = RequestMethod.POST)
	//@ApiOperation(value = "Modify Ra Mst Fees Default Priority", notes = "Modify Ra Mst Fees Default Priority Values", response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> modify(@RequestBody List<FeesPriorityVb> vObjects) {
		JSONExceptionCode jsonExceptionCode = null;
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			vObjects.get(0).setActionType("Modify");
			exceptionCode.setOtherInfo(vObjects.get(0));
			exceptionCode = feesPriorityWb.modifyRecord(exceptionCode,vObjects);
			jsonExceptionCode = new JSONExceptionCode(exceptionCode.getErrorCode(), exceptionCode.getErrorMsg(),
					exceptionCode.getOtherInfo());
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		} catch (RuntimeCustomException rex) {
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		}
	}

	/*-------------------------------------DELETE Ra Mst Fees Default Priority------------------------------------------*/
	@RequestMapping(path = "/deleteFeesPriority", method = RequestMethod.POST)
	//@ApiOperation(value = "Delete Ra Mst Fees Default Priority", notes = "Delete existing Ra Mst Fees Default Priority", response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> delete(@RequestBody List<FeesPriorityVb> vObjects) {
		JSONExceptionCode jsonExceptionCode = null;
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			vObjects.get(0).setActionType("Delete");
			exceptionCode.setOtherInfo(vObjects.get(0));
			exceptionCode = feesPriorityWb.deleteRecord(exceptionCode,vObjects);
			jsonExceptionCode = new JSONExceptionCode(exceptionCode.getErrorCode(), exceptionCode.getErrorMsg(),
					exceptionCode.getOtherInfo());
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		} catch (RuntimeCustomException rex) {
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		}
	}
	/*-------------------------------------Reject Ra Mst Fees Default Priority------------------------------------------*/
	@RequestMapping(path = "/rejectFeesPriority", method = RequestMethod.POST)
	//@ApiOperation(value = "Reject Ra Mst Fees Default Priority", notes = "Reject existing Ra Mst Fees Default Priority", response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> reject(@RequestBody List<FeesPriorityVb> vObjects) {
		JSONExceptionCode jsonExceptionCode = null;
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			vObjects.get(0).setActionType("Reject");
			exceptionCode = feesPriorityWb.bulkReject(vObjects,vObjects.get(0));
			jsonExceptionCode = new JSONExceptionCode(exceptionCode.getErrorCode(), exceptionCode.getErrorMsg(),
					exceptionCode.getOtherInfo());
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		} catch (RuntimeCustomException rex) {
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		}
	}
	
	@RequestMapping(path = "/approveFeesPriority", method = RequestMethod.POST)
	//@ApiOperation(value = "Approve Ra Mst Fees Default Priority", notes = "Approve existing Ra Mst Fees Default Priority", response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> approve(@RequestBody List<FeesPriorityVb> vObjects) {
		JSONExceptionCode jsonExceptionCode = null;
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			vObjects.get(0).setActionType("Approve");
			exceptionCode = feesPriorityWb.bulkApprove(vObjects, vObjects.get(0));
			jsonExceptionCode = new JSONExceptionCode(exceptionCode.getErrorCode(), exceptionCode.getErrorMsg(),
					exceptionCode.getOtherInfo());
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		} catch (RuntimeCustomException rex) {
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		}
	}
	
	@RequestMapping(path = "/reviewFeesPriority", method = RequestMethod.POST)
	//@ApiOperation(value = "Get Headers", notes = "Fetch all the existing records from the table", response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> review(@RequestBody FeesPriorityVb vObject) {
		JSONExceptionCode jsonExceptionCode = null;
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			vObject.setActionType("Query");
			exceptionCode = feesPriorityWb.doValidate(vObject);
			if(exceptionCode.getErrorCode() == Constants.ERRONEOUS_OPERATION){
				jsonExceptionCode = new JSONExceptionCode(exceptionCode.getErrorCode(), exceptionCode.getErrorMsg(),exceptionCode.getResponse(), exceptionCode.getOtherInfo());
			}
			exceptionCode = feesPriorityWb.reviewRecordNew(vObject);
			String errorMesss = exceptionCode.getErrorMsg().replaceAll("Query ", "Review");
			jsonExceptionCode = new JSONExceptionCode(exceptionCode.getErrorCode(), errorMesss,
					exceptionCode.getResponse(), exceptionCode.getOtherInfo());
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		} catch (RuntimeCustomException rex) {
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		}
	}	
	@RequestMapping(path = "/getMaxEffectiveDatePriorityList", method = RequestMethod.POST)
	//@ApiOperation(value = "get MaxEffectiveDate PriorityList", notes = "Fetch all the existing records from the table", response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> getMaxEffectiveDatePriorityList(@RequestBody FeesPriorityVb vObject) {
		JSONExceptionCode jsonExceptionCode = null;
		try {
			vObject.setActionType("Query");
			ExceptionCode exceptionCode = feesPriorityWb.getMaxEffectiveDatePriorityList(vObject);
			//String errorMesss = exceptionCode.getErrorMsg().replaceAll("Query ", "Review");
			jsonExceptionCode = new JSONExceptionCode(exceptionCode.getErrorCode(), exceptionCode.getErrorMsg(),
					exceptionCode.getResponse(), exceptionCode.getOtherInfo());
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		} catch (RuntimeCustomException rex) {
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		}
	}	

}
