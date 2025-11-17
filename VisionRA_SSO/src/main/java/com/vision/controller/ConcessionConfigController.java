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

import com.vision.dao.ConcessionHeaderDao;
import com.vision.exception.ExceptionCode;
import com.vision.exception.JSONExceptionCode;
import com.vision.exception.RuntimeCustomException;
import com.vision.util.Constants;
import com.vision.vb.ConcessionConfigHeaderVb;
import com.vision.vb.MenuVb;
import com.vision.vb.ReviewResultVb;
import com.vision.wb.ConcessionConfigWb;
@RestController
@RequestMapping("concessionConfig")
//@Api(value = "concessionConfig", description = "Concession Configuration and Details")
public class ConcessionConfigController {
		@Autowired
		ConcessionConfigWb concessionConfigWb;
		
		@Autowired
		ConcessionHeaderDao concessionHeaderDao;
		
		/*-------------------------------------Concession config SCREEN PAGE LOAD------------------------------------------*/
		@RequestMapping(path = "/pageLoadValues", method = RequestMethod.GET)
		//@ApiOperation(value = "Page Load Values",notes = "Load AT/NT Values on screen load",response = ResponseEntity.class)
		public ResponseEntity<JSONExceptionCode> pageOnLoad(){
			JSONExceptionCode jsonExceptionCode  = null;
			try{
				MenuVb menuVb = new MenuVb();
				menuVb.setActionType("Clear");
				ArrayList arrayList = concessionConfigWb.getPageLoadValues();
				jsonExceptionCode = new JSONExceptionCode(Constants.SUCCESSFUL_OPERATION, "Page Load Values", arrayList);
				return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
			}catch(RuntimeCustomException rex){
				jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
				return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
			}	
		}
		/*--------------get Query Results------------------*/
		@RequestMapping(path = "/getAllQueryResults", method = RequestMethod.POST)
		//@ApiOperation(value = "Get All the details", notes = "Fetch all the existing records from the table",response = ResponseEntity.class)
		public ResponseEntity<JSONExceptionCode> getAllQueryResults(@RequestBody ConcessionConfigHeaderVb vObject){
			JSONExceptionCode jsonExceptionCode = null;
			try {
				vObject.setActionType("Query");
				ExceptionCode exceptionCode = concessionConfigWb.getAllQueryPopupResult(vObject);
				jsonExceptionCode = new JSONExceptionCode(Constants.SUCCESSFUL_OPERATION, "Query Results", exceptionCode.getResponse(),exceptionCode.getOtherInfo());
				return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);	
			}catch(RuntimeCustomException rex){
				jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
				return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
			}
		}
		/*--------------get Query Results------------------*/
		@RequestMapping(path = "/getQueryDetails", method = RequestMethod.POST)
		//@ApiOperation(value = "Get All the details", notes = "Fetch all the existing records from the table",response = ResponseEntity.class)
		public ResponseEntity<JSONExceptionCode> getAllQueryDetails(@RequestBody ConcessionConfigHeaderVb vObject){
			JSONExceptionCode jsonExceptionCode = null;
			try {
				vObject.setActionType("Query");
				ExceptionCode exceptionCode = concessionConfigWb.getQueryResults(vObject);
				jsonExceptionCode = new JSONExceptionCode(Constants.SUCCESSFUL_OPERATION, "Query Results", exceptionCode.getResponse(),exceptionCode.getOtherInfo());
				return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);	
			}catch(RuntimeCustomException rex){
				jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
				return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
			}
		}
		/*-------------------------------------ADD Concession config------------------------------------------*/
		@RequestMapping(path = "/addConcessionConfig", method = RequestMethod.POST)
		//@ApiOperation(value = "Add Concession Config", notes = "Add Concession Config", response = ResponseEntity.class)
		public ResponseEntity<JSONExceptionCode> addConcessionConfig(@RequestBody ConcessionConfigHeaderVb vObject) {
			JSONExceptionCode jsonExceptionCode = null;
			ExceptionCode exceptionCode = new ExceptionCode();
			try {
				vObject.setActionType("Add");
				exceptionCode = concessionConfigWb.insertRecord(vObject);
				if(exceptionCode.getErrorCode() ==Constants.SUCCESSFUL_OPERATION){
					jsonExceptionCode = new JSONExceptionCode(Constants.SUCCESSFUL_OPERATION, exceptionCode.getErrorMsg(), exceptionCode.getOtherInfo());
				}else {
					jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, exceptionCode.getErrorMsg(),exceptionCode.getOtherInfo());
				}
				return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
			} catch (RuntimeCustomException rex) {
				jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
				return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
			}
		}
		/*-------------------------------------Modify Concession config------------------------------------------*/
		@RequestMapping(path = "/modifyConcessionConfig", method = RequestMethod.POST)
		//@ApiOperation(value = "Modify Concession Config", notes = "Modify Concession Config", response = ResponseEntity.class)
		public ResponseEntity<JSONExceptionCode> modifyConcessionConfig(@RequestBody ConcessionConfigHeaderVb vObject) {
			JSONExceptionCode jsonExceptionCode = null;
			ExceptionCode exceptionCode = new ExceptionCode();
			try {
				vObject.setActionType("Modify");
				exceptionCode = concessionConfigWb.modifyRecord(vObject);
				if(exceptionCode.getErrorCode() ==Constants.SUCCESSFUL_OPERATION){
					jsonExceptionCode = new JSONExceptionCode(Constants.SUCCESSFUL_OPERATION, exceptionCode.getErrorMsg(), exceptionCode.getOtherInfo());
				}else {
					jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, exceptionCode.getErrorMsg(),exceptionCode.getOtherInfo());
				}
				return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
			} catch (RuntimeCustomException rex) {
				jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
				return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
			}
		}
		/*-------------------------------------Delete Concession config------------------------------------------*/
		@RequestMapping(path = "/deleteConcessionConfig", method = RequestMethod.POST)
		//@ApiOperation(value = "delete Concession Config", notes = "delete Concession Config", response = ResponseEntity.class)
		public ResponseEntity<JSONExceptionCode> deleteConcessionConfig(@RequestBody ConcessionConfigHeaderVb vObject) {
			JSONExceptionCode jsonExceptionCode = null;
			ExceptionCode exceptionCode = new ExceptionCode();
			try {
				vObject.setActionType("Delete");
				exceptionCode = concessionConfigWb.deleteRecord(vObject);
				if(exceptionCode.getErrorCode() ==Constants.SUCCESSFUL_OPERATION){
					jsonExceptionCode = new JSONExceptionCode(Constants.SUCCESSFUL_OPERATION, exceptionCode.getErrorMsg(), exceptionCode.getOtherInfo());
				}else {
					jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, exceptionCode.getErrorMsg(),exceptionCode.getOtherInfo());
				}
				return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
			} catch (RuntimeCustomException rex) {
				jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
				return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
			}
		}
		/*-------------------------------------Reject Business config------------------------------------------*/
		@RequestMapping(path = "/rejectConcessionConfig", method = RequestMethod.POST)
		//@ApiOperation(value = "reject Concession Config", notes = "reject Concession Config", response = ResponseEntity.class)
		public ResponseEntity<JSONExceptionCode> rejectConcessionConfig(@RequestBody  List<ConcessionConfigHeaderVb> vObject) {
			JSONExceptionCode jsonExceptionCode = null;
			ExceptionCode exceptionCode = new ExceptionCode();
			try {
				ConcessionConfigHeaderVb concessionConfigHeaderVb;
				concessionConfigHeaderVb=vObject.get(0);
				exceptionCode = concessionConfigWb.bulkReject(vObject, concessionConfigHeaderVb);
				if(exceptionCode.getErrorCode() ==Constants.SUCCESSFUL_OPERATION){
					jsonExceptionCode = new JSONExceptionCode(Constants.SUCCESSFUL_OPERATION, exceptionCode.getErrorMsg(), exceptionCode.getOtherInfo());
				}else {
					jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, exceptionCode.getErrorMsg(),exceptionCode.getOtherInfo());
				}
				return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
			} catch (RuntimeCustomException rex) {
				jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
				return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
			}
		}
		/*-------------------------------------Approve Business config------------------------------------------*/
		@RequestMapping(path = "/approveConcessionConfig", method = RequestMethod.POST)
		//@ApiOperation(value = "Approve Concession Config", notes = "reject Concession Config", response = ResponseEntity.class)
		public ResponseEntity<JSONExceptionCode> approveConcessionConfig(@RequestBody List<ConcessionConfigHeaderVb>  vObject) {
			JSONExceptionCode jsonExceptionCode = null;
			ExceptionCode exceptionCode = new ExceptionCode();
			try {
				
				ConcessionConfigHeaderVb ConcessionConfigHeaderVb= new ConcessionConfigHeaderVb();
				ConcessionConfigHeaderVb=vObject.get(0);
				exceptionCode = concessionConfigWb.bulkApprove(vObject, ConcessionConfigHeaderVb);
				if(exceptionCode.getErrorCode() ==Constants.SUCCESSFUL_OPERATION){
					jsonExceptionCode = new JSONExceptionCode(Constants.SUCCESSFUL_OPERATION, exceptionCode.getErrorMsg(), exceptionCode.getOtherInfo());
				}else {
					jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, exceptionCode.getErrorMsg(),exceptionCode.getOtherInfo());
				}
				return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
			} catch (RuntimeCustomException rex) {
				jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
				return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
			}
		}
		/*-------------------------------------Review Concession Config ------------------------------------------*/
		@RequestMapping(path = "/reviewConcessionConfig", method = RequestMethod.POST)
		//@ApiOperation(value = "Get Headers", notes = "Fetch all the existing records from the table", response = ResponseEntity.class)
		public ResponseEntity<JSONExceptionCode> reviewTransLineConfig(@RequestBody ConcessionConfigHeaderVb vObject) {
			JSONExceptionCode jsonExceptionCode = null;
			ExceptionCode exceptionCode = new ExceptionCode();
			try {
				vObject.setActionType("Query");
				List<ReviewResultVb> bnfParameterList = concessionConfigWb.reviewRecord(vObject);
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
		
		@RequestMapping(path = "/checkEffectiveDate", method = RequestMethod.POST)
		//@ApiOperation(value = "Check Effective Date", notes = "Effective Date Check", response = ResponseEntity.class)
		public ResponseEntity<JSONExceptionCode> checkEffectiveDate(@RequestBody ConcessionConfigHeaderVb vObject) {
			JSONExceptionCode jsonExceptionCode = null;
			ExceptionCode exceptionCode = new ExceptionCode();
			int effectiveDateCnt = 0;
			try {
				vObject.setActionType("Add");
				effectiveDateCnt = concessionHeaderDao.effectiveDateCheck(vObject, 0);
				if(effectiveDateCnt == 0) {
					effectiveDateCnt = concessionHeaderDao.effectiveDateCheck(vObject, 1);
					if(effectiveDateCnt > 0) {
						exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
						exceptionCode.setErrorMsg("Record already Present for this Effective Date and Pending for Approval");
						jsonExceptionCode = new JSONExceptionCode(Constants.SUCCESSFUL_OPERATION, exceptionCode.getErrorMsg(), exceptionCode.getOtherInfo());
						return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
					}
				}else {
					exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
					exceptionCode.setErrorMsg("Record already Present for this Effective Date");
					jsonExceptionCode = new JSONExceptionCode(Constants.SUCCESSFUL_OPERATION, exceptionCode.getErrorMsg(), exceptionCode.getOtherInfo());
					return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
				}
				exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
				jsonExceptionCode = new JSONExceptionCode(Constants.SUCCESSFUL_OPERATION, exceptionCode.getErrorMsg(), exceptionCode.getOtherInfo());
				return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
			} catch (RuntimeCustomException rex) {
				jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
				return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
			}
		}
		
}