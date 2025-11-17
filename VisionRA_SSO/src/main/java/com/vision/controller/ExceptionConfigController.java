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

import com.vision.dao.ExceptionConfigHeaderDao;
import com.vision.exception.ExceptionCode;
import com.vision.exception.JSONExceptionCode;
import com.vision.exception.RuntimeCustomException;
import com.vision.util.Constants;
import com.vision.vb.ExceptionConfigDetailsVb;
import com.vision.vb.ExceptionConfigHeaderVb;
import com.vision.vb.ReviewResultVb;
import com.vision.wb.ExceptionConfigWb;
import com.vision.wb.ExceptionDetailsWb;

@RestController
@RequestMapping("exceptionConfig")
//@Api(value = "ExceptionConfig", description = "ManualException Configuration and Details")
public class ExceptionConfigController {
		@Autowired
		ExceptionConfigWb exceptionConfigWb;
		
		@Autowired
		ExceptionDetailsWb exceptionDetailsWb;
		
		@Autowired
		ExceptionConfigHeaderDao manualExceptionHeaderDao;
		
		/*-------------------------------------ManualException config SCREEN PAGE LOAD------------------------------------------*/
		@GetMapping("/pageLoadValues")
		//@ApiOperation(value = "Page Load Values",notes = "Load AT/NT Values on screen load",response = ResponseEntity.class)
		public ResponseEntity<JSONExceptionCode> pageOnLoad(){
			JSONExceptionCode jsonExceptionCode  = null;
			try{
				ExceptionConfigHeaderVb vObj = new ExceptionConfigHeaderVb();
				vObj.setActionType("Query");
				ExceptionCode exceptionCode = exceptionConfigWb.doValidate(vObj);
				if(exceptionCode.getErrorCode() == Constants.ERRONEOUS_OPERATION) {
					jsonExceptionCode = new JSONExceptionCode(exceptionCode.getErrorCode(), exceptionCode.getErrorMsg(), null);
					return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
				}
				ArrayList arrayList = exceptionConfigWb.getPageLoadValues();
				jsonExceptionCode = new JSONExceptionCode(Constants.SUCCESSFUL_OPERATION, "On Load Values", arrayList);
				return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
			}catch(RuntimeCustomException rex){
				jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
				return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
			}	
		}
		/*--------------get Query Results------------------*/
		@PostMapping("/getAllQueryResults")
		//@ApiOperation(value = "Get All the details", notes = "Fetch all the existing records from the table",response = ResponseEntity.class)
		public ResponseEntity<JSONExceptionCode> getAllQueryResults(@RequestBody ExceptionConfigHeaderVb vObject){
			JSONExceptionCode jsonExceptionCode = null;
			try {
				vObject.setActionType("Query");
				ExceptionCode exceptionCode = exceptionConfigWb.getAllQueryPopupResult(vObject);
				jsonExceptionCode = new JSONExceptionCode(exceptionCode.getErrorCode(), exceptionCode.getErrorMsg(),exceptionCode.getResponse(), exceptionCode.getOtherInfo());
				return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);	
			}catch(RuntimeCustomException rex){
				jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
				return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
			}
		}
		/*--------------get Query Results------------------*/
		@PostMapping("/getQueryDetails")
		//@ApiOperation(value = "Get All the details", notes = "Fetch all the existing records from the table",response = ResponseEntity.class)
		public ResponseEntity<JSONExceptionCode> getAllQueryDetails(@RequestBody ExceptionConfigDetailsVb vObject){
			JSONExceptionCode jsonExceptionCode = null;
			try {
				vObject.setActionType("Query");
				vObject.setMaxRecords(100);
				
				ExceptionConfigHeaderVb exceptionConfigHeaderVb = new ExceptionConfigHeaderVb();
				exceptionConfigHeaderVb.setCountry(vObject.getCountry());
				exceptionConfigHeaderVb.setLeBook(vObject.getLeBook());
				exceptionConfigHeaderVb.setExceptionReference(vObject.getExceptionReference());
				exceptionConfigHeaderVb.setActionType(vObject.getActionType());
				ExceptionCode exceptionCodeHeader = exceptionConfigWb.getQueryResults(exceptionConfigHeaderVb);
				if(exceptionCodeHeader.getResponse() != null) {
					List<ExceptionConfigHeaderVb> collTemp = (List<ExceptionConfigHeaderVb>)exceptionCodeHeader.getResponse();
					if(collTemp != null && collTemp.size() > 0) {
						exceptionConfigHeaderVb = collTemp.get(0);
					}
				}
				vObject.setRecordIndicator(exceptionConfigHeaderVb.getRecordIndicator());
				ExceptionCode exceptionCode = exceptionDetailsWb.getAllQueryPopupResult(vObject);
				if(exceptionCodeHeader.getErrorCode() == Constants.SUCCESSFUL_OPERATION) {
					exceptionCode.setErrorCode(exceptionCodeHeader.getErrorCode());
					exceptionCode.setErrorMsg(exceptionCodeHeader.getErrorMsg());
					List<ExceptionConfigDetailsVb> exceptionDetLst = (ArrayList<ExceptionConfigDetailsVb>)exceptionCode.getResponse();
					if(exceptionDetLst != null && exceptionDetLst.size() > 0)
						exceptionConfigHeaderVb.setDetailCnt(exceptionDetLst.size());
					exceptionCode.setRequest(exceptionConfigHeaderVb);
				}
				jsonExceptionCode = new JSONExceptionCode(exceptionCode.getErrorCode(), exceptionCode.getErrorMsg(),exceptionCode.getResponse(), exceptionCode.getOtherInfo(),exceptionCode.getRequest());	
				return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);	
			}catch(RuntimeCustomException rex){
				jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
				return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
			}
		}
		/*-------------------------------------ADD ManualException config------------------------------------------*/
		@PostMapping("/addManualExceptionConfig")
		//@ApiOperation(value = "Add ManualException Config", notes = "Add ManualException Config", response = ResponseEntity.class)
		public ResponseEntity<JSONExceptionCode> addManualExceptionConfig(@RequestBody ExceptionConfigHeaderVb vObject) {
			JSONExceptionCode jsonExceptionCode = null;
			ExceptionCode exceptionCode = new ExceptionCode();
			try {
				vObject.setActionType("Add");
				vObject.setVerificationRequired(true); //Hard coded based on functionality always insert in Pend table
				exceptionCode = exceptionConfigWb.insertRecord(vObject);
				jsonExceptionCode = new JSONExceptionCode(exceptionCode.getErrorCode(), exceptionCode.getErrorMsg(), exceptionCode.getOtherInfo());
				return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
			} catch (RuntimeCustomException rex) {
				jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
				return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
			}
		}
		/*-------------------------------------ADD Exception Detail------------------------------------------*/
		@PostMapping("/addExceptionDetail")
		//@ApiOperation(value = "AddExceptionDetails", notes = "Add Exception Details", response = ResponseEntity.class)
		public ResponseEntity<JSONExceptionCode> addExceptionDetails(@RequestBody List<ExceptionConfigDetailsVb> vObjects) {
			JSONExceptionCode jsonExceptionCode = null;
			ExceptionCode exceptionCode = new ExceptionCode();
			try {
				vObjects.get(0).setActionType("Add");
				exceptionCode.setOtherInfo(vObjects.get(0));
				exceptionCode = exceptionDetailsWb.insertRecord(exceptionCode, vObjects);
				jsonExceptionCode = new JSONExceptionCode(exceptionCode.getErrorCode(), exceptionCode.getErrorMsg(), exceptionCode.getRequest(),exceptionCode.getOtherInfo());
				return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
			} catch (RuntimeCustomException rex) {
				jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
				return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
			}
		}
		/*-------------------------------------Modify ManualException config------------------------------------------*/
		@PostMapping("/modifyManualExceptionConfig")
		//@ApiOperation(value = "Modify ManualException Config", notes = "Modify ManualException Config", response = ResponseEntity.class)
		public ResponseEntity<JSONExceptionCode> modifyManualExceptionConfig(@RequestBody ExceptionConfigHeaderVb vObject) {
			JSONExceptionCode jsonExceptionCode = null;
			ExceptionCode exceptionCode = new ExceptionCode();
			try {
				vObject.setActionType("Modify");
				exceptionCode = exceptionConfigWb.modifyRecord(vObject,false);
				exceptionCode.setOtherInfo(vObject);
				jsonExceptionCode = new JSONExceptionCode(exceptionCode.getErrorCode(), exceptionCode.getErrorMsg(), exceptionCode.getOtherInfo());
				return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
			} catch (RuntimeCustomException rex) {
				jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
				return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
			}
		}
		
		/*-------------------------------------Delete Detail Exception------------------------------------------*/
		@PostMapping("/deleteExceptionDetails")
		//@ApiOperation(value = "delete ManualException Config", notes = "delete Detail Exception Config", response = ResponseEntity.class)
		public ResponseEntity<JSONExceptionCode> deleteExceptionDetails(@RequestBody ExceptionConfigHeaderVb vObject) {
			JSONExceptionCode jsonExceptionCode = null;
			ExceptionCode exceptionCode = new ExceptionCode();
			try {
				vObject.setActionType("Delete");
				exceptionCode = exceptionConfigWb.deleteRecord(vObject);
				jsonExceptionCode = new JSONExceptionCode(exceptionCode.getErrorCode(), exceptionCode.getErrorMsg(),exceptionCode.getResponse(), exceptionCode.getOtherInfo());
				return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
			} catch (RuntimeCustomException rex) {
				jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
				return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
			}
		}
		/*-------------------------------------Review ManualException Config Not in Use------------------------------------------*/
		@PostMapping("/reviewManualExceptionConfig")
		//@ApiOperation(value = "Get Headers", notes = "Fetch all the existing records from the table", response = ResponseEntity.class)
		public ResponseEntity<JSONExceptionCode> reviewTransLineConfig(@RequestBody ExceptionConfigHeaderVb vObject) {
			JSONExceptionCode jsonExceptionCode = null;
			ExceptionCode exceptionCode = new ExceptionCode();
			try {
				vObject.setActionType("Query");
				exceptionCode = exceptionConfigWb.doValidate(vObject);
				if(exceptionCode.getErrorCode() == Constants.ERRONEOUS_OPERATION){
					jsonExceptionCode = new JSONExceptionCode(exceptionCode.getErrorCode(), exceptionCode.getErrorMsg(),exceptionCode.getResponse(), exceptionCode.getOtherInfo());
				}
				List<ReviewResultVb> bnfParameterList = exceptionConfigWb.reviewRecord(vObject);
				if (bnfParameterList != null && !bnfParameterList.isEmpty()) {
					exceptionCode.setResponse(bnfParameterList);
					exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
					exceptionCode.setErrorMsg("Review Successful");
					jsonExceptionCode = new JSONExceptionCode(Constants.SUCCESSFUL_OPERATION, exceptionCode.getErrorMsg(),exceptionCode.getResponse(), exceptionCode.getOtherInfo());
				} else {
					jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, exceptionCode.getErrorMsg(),exceptionCode.getOtherInfo());
				}
				return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
			} catch (RuntimeCustomException rex) {
				jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
				return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
			}
		}
		/*--------------Authorize Exception------------------*/
		@PostMapping("/authorizeException")
		//@ApiOperation(value = "Authorize the Exception", notes = "Change status to authorize",response = ResponseEntity.class)
		public ResponseEntity<JSONExceptionCode> authorizeException(@RequestBody List<ExceptionConfigHeaderVb> vObjectlst){
			JSONExceptionCode jsonExceptionCode = null;
			ExceptionCode exceptionCode = new ExceptionCode();
			try {
				ExceptionConfigHeaderVb exceptionConfigHeaderVb = (ExceptionConfigHeaderVb)vObjectlst.get(0);
				exceptionConfigHeaderVb.setActionType("Approve");
				exceptionCode = exceptionConfigWb.authorizeException(exceptionConfigHeaderVb,vObjectlst);
				exceptionCode.setOtherInfo(exceptionConfigHeaderVb);
				jsonExceptionCode = new JSONExceptionCode(exceptionCode.getErrorCode(), exceptionCode.getErrorMsg(), exceptionCode.getOtherInfo());
				return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);	
			}catch(RuntimeCustomException rex){
				jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
				return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
			}
		}
		/*--------------Reject Exception------------------*/
		@PostMapping("/rejectException")
		//@ApiOperation(value = "Reject the Exception", notes = "Reject record from exception",response = ResponseEntity.class)
		public ResponseEntity<JSONExceptionCode> rejectException(@RequestBody List<ExceptionConfigHeaderVb> vObjectlst){
			JSONExceptionCode jsonExceptionCode = null;
			ExceptionCode exceptionCode = new ExceptionCode();
			try {
				ExceptionConfigHeaderVb exceptionConfigHeaderVb = (ExceptionConfigHeaderVb)vObjectlst.get(0);
				exceptionConfigHeaderVb.setActionType("Reject");
				exceptionCode = exceptionConfigWb.rejectException(exceptionConfigHeaderVb,vObjectlst);
				exceptionCode.setOtherInfo(exceptionConfigHeaderVb);
				jsonExceptionCode = new JSONExceptionCode(exceptionCode.getErrorCode(), exceptionCode.getErrorMsg(), exceptionCode.getOtherInfo());
				return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);	
			}catch(RuntimeCustomException rex){
				jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
				return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
			}
		}
		
		/*--------------Delete Exception------------------*/
		@PostMapping("/deleteException")
		//@ApiOperation(value = "Delete the Exception", notes = "Delete record from exception",response = ResponseEntity.class)
		public ResponseEntity<JSONExceptionCode> deleteException(@RequestBody ExceptionConfigHeaderVb vObject){
			JSONExceptionCode jsonExceptionCode = null;
			ExceptionCode exceptionCode = new ExceptionCode();
			try {
				vObject.setActionType("Delete");
				exceptionCode = exceptionConfigWb.deleteException(vObject);
				exceptionCode.setOtherInfo(vObject);
				jsonExceptionCode = new JSONExceptionCode(exceptionCode.getErrorCode(), exceptionCode.getErrorMsg(), exceptionCode.getOtherInfo());
				return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);	
			}catch(RuntimeCustomException rex){
				jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
				return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
			}
		}
		/*--------------get Query Leakage Details------------------*/
		@PostMapping("/getLekageDetails")
		//@ApiOperation(value = "Get All the details", notes = "Fetch all the existing records from the table",response = ResponseEntity.class)
		public ResponseEntity<JSONExceptionCode> getLekageDetails(@RequestBody ExceptionConfigDetailsVb vObject){
			JSONExceptionCode jsonExceptionCode = null;
			try {
				vObject.setActionType("Query");
				vObject.setMaxRecords(100);
				ExceptionCode exceptionCode = exceptionDetailsWb.getLeakgeTransactionDetail(vObject);
				jsonExceptionCode = new JSONExceptionCode(exceptionCode.getErrorCode(), exceptionCode.getErrorMsg(),exceptionCode.getResponse(), exceptionCode.getOtherInfo());
				return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);	
			}catch(RuntimeCustomException rex){
				jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
				return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
			}
		}
		/*--------------get Posted Audit Details------------------*/
		@PostMapping("/getExceptionPostedAudit")
		//@ApiOperation(value = "Get All the details", notes = "Fetch all the existing records from the Audit table",response = ResponseEntity.class)
		public ResponseEntity<JSONExceptionCode> getExceptionPostedAudit(@RequestBody ExceptionConfigDetailsVb vObject){
			JSONExceptionCode jsonExceptionCode = null;
			try {
				vObject.setActionType("Query");
				vObject.setMaxRecords(100);
				ExceptionCode exceptionCode = exceptionDetailsWb.getAuditDetails(vObject);
				jsonExceptionCode = new JSONExceptionCode(exceptionCode.getErrorCode(), exceptionCode.getErrorMsg(),exceptionCode.getResponse(), exceptionCode.getOtherInfo(),exceptionCode.getRequest());
				return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);	
			}catch(RuntimeCustomException rex){
				jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
				return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
			}
		}
		/*--------------get Max Reference Sequence------------------*/
		@GetMapping("/getReferenceId")
		//@ApiOperation(value = "Get Reference Id", notes = "Reference Id",response = ResponseEntity.class)
		public ResponseEntity<JSONExceptionCode> getExceptionPostedAudit(){
			JSONExceptionCode jsonExceptionCode = null;
			ExceptionCode exceptionCode= new ExceptionCode();
			try {
				ExceptionConfigDetailsVb exceptionConfigDetailsVb = new ExceptionConfigDetailsVb();
				exceptionCode = exceptionDetailsWb.doValidate(exceptionConfigDetailsVb);
				if(exceptionCode.getErrorCode() == Constants.ERRONEOUS_OPERATION){
					jsonExceptionCode = new JSONExceptionCode(exceptionCode.getErrorCode(), exceptionCode.getErrorMsg(),exceptionCode.getResponse(), exceptionCode.getOtherInfo());
				}
				String reference = manualExceptionHeaderDao.getMaxReference();
				reference = "00"+reference;
				exceptionCode.setResponse(reference);
				jsonExceptionCode = new JSONExceptionCode(Constants.SUCCESSFUL_OPERATION,"Success",exceptionCode.getResponse());
				return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);	
			}catch(RuntimeCustomException rex){
				jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
				return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
			}
		}
		@PostMapping("/submitManualExceptionConfig")
		//@ApiOperation(value = "Submit ManualException Config", notes = "Submit ManualException Config", response = ResponseEntity.class)
		public ResponseEntity<JSONExceptionCode> submitManualExceptionConfig(@RequestBody ExceptionConfigHeaderVb vObject) {
			JSONExceptionCode jsonExceptionCode = null;
			ExceptionCode exceptionCode = new ExceptionCode();
			try {
				vObject.setActionType("Submit");
				exceptionCode = exceptionConfigWb.modifyRecord(vObject,true);
				exceptionCode.setOtherInfo(vObject);
				jsonExceptionCode = new JSONExceptionCode(exceptionCode.getErrorCode(), exceptionCode.getErrorMsg(), exceptionCode.getOtherInfo());
				return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
			} catch (RuntimeCustomException rex) {
				jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
				return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
			}
		}
}