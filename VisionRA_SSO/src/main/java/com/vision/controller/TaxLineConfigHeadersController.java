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

import com.vision.dao.TaxLineConfigHeaderDao;
import com.vision.exception.ExceptionCode;
import com.vision.exception.JSONExceptionCode;
import com.vision.exception.RuntimeCustomException;
import com.vision.util.Constants;
import com.vision.vb.MenuVb;
import com.vision.vb.TaxLineConfigHeaderVb;
import com.vision.wb.TaxLineConfigHeaderWb;

@RestController
@RequestMapping("taxLineConfig")
//@Api(value = "taxLineConfig", description = "TaxLine Config Header and Details")
public class TaxLineConfigHeadersController {

	@Autowired
	TaxLineConfigHeaderWb taxLineConfigHeadersWb;
	
	@Autowired
	TaxLineConfigHeaderDao taxLineConfigHeadersDao;
	
	/*-------------------------------------BRANCH RM TARGET SCREEN PAGE LOAD------------------------------------------*/
	@RequestMapping(path = "/pageLoadValues", method = RequestMethod.GET)
	//@ApiOperation(value = "Page Load Values",notes = "Load AT/NT Values on screen load",response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> pageOnLoad(){
		JSONExceptionCode jsonExceptionCode  = null;
		try{
			MenuVb menuVb = new MenuVb();
			menuVb.setActionType("Clear");
			ArrayList arrayList = taxLineConfigHeadersWb.getPageLoadValues();
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
	public ResponseEntity<JSONExceptionCode> getAllQueryResults(@RequestBody TaxLineConfigHeaderVb vObject){
		JSONExceptionCode jsonExceptionCode = null;
		try {
			vObject.setActionType("Query");
			ExceptionCode exceptionCode = taxLineConfigHeadersWb.getAllQueryPopupResult(vObject);
			if(exceptionCode.getErrorCode() ==Constants.SUCCESSFUL_OPERATION){
				jsonExceptionCode = new JSONExceptionCode(Constants.SUCCESSFUL_OPERATION, exceptionCode.getErrorMsg(),exceptionCode.getResponse(), exceptionCode.getOtherInfo());
			}else {
				jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, exceptionCode.getErrorMsg(),exceptionCode.getResponse(), exceptionCode.getOtherInfo());
			}			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);	
		}catch(RuntimeCustomException rex){
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
		}
	}
	
	/*--------------get Query Results------------------*/
	@RequestMapping(path = "/getQueryDetails", method = RequestMethod.POST)
	//@ApiOperation(value = "Get All the details", notes = "Fetch all the existing records from the table",response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> getAllQueryDetails(@RequestBody TaxLineConfigHeaderVb vObject){
		JSONExceptionCode jsonExceptionCode = null;
		try {
			vObject.setActionType("Query");
			ExceptionCode exceptionCode = taxLineConfigHeadersWb.getQueryResults(vObject);
			if(exceptionCode.getErrorCode() ==Constants.SUCCESSFUL_OPERATION){
				jsonExceptionCode = new JSONExceptionCode(Constants.SUCCESSFUL_OPERATION, exceptionCode.getErrorMsg(),exceptionCode.getResponse(), exceptionCode.getOtherInfo());
			}else {
				jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, exceptionCode.getErrorMsg(),exceptionCode.getResponse(), exceptionCode.getOtherInfo());
			}
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);	
		}catch(RuntimeCustomException rex){
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
		}
	}

	/*-------------------------------------ADD TaxLine config------------------------------------------*/
	@RequestMapping(path = "/addTaxLineConfig", method = RequestMethod.POST)
	//@ApiOperation(value = "Add Tax Line Config", notes = "Add Tax Line Config", response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> addTaxLineConfig(@RequestBody TaxLineConfigHeaderVb vObject) {
		JSONExceptionCode jsonExceptionCode = null;
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			vObject.setActionType("Add");
			exceptionCode = taxLineConfigHeadersWb.insertRecord(vObject);
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

	/*-------------------------------------Modify TaxLine config------------------------------------------*/
	@RequestMapping(path = "/modifyTaxLineConfig", method = RequestMethod.POST)
	//@ApiOperation(value = "Modify Tax Line Config", notes = "Modify Tax Line Config", response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> modifyTaxLineConfig(@RequestBody TaxLineConfigHeaderVb vObject) {
		JSONExceptionCode jsonExceptionCode = null;
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			vObject.setActionType("Modify");
			exceptionCode = taxLineConfigHeadersWb.modifyRecord(vObject);
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

	/*-------------------------------------Delete TaxLine config------------------------------------------*/
	@RequestMapping(path = "/deleteTaxLineConfig", method = RequestMethod.POST)
	//@ApiOperation(value = "delete Tax Line Config", notes = "delete Tax Line Config", response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> deleteTaxLineConfig(@RequestBody TaxLineConfigHeaderVb vObject) {
		JSONExceptionCode jsonExceptionCode = null;
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			vObject.setActionType("Delete");
			exceptionCode = taxLineConfigHeadersWb.deleteRecord(vObject);
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

	/*-------------------------------------Reject TaxLine config------------------------------------------*/
	@RequestMapping(path = "/rejectTaxLineConfig", method = RequestMethod.POST)
	//@ApiOperation(value = "reject Tax Line Config", notes = "reject Tax Line Config", response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> rejectTaxLineConfig(@RequestBody List<TaxLineConfigHeaderVb> taxlinelst) {
		JSONExceptionCode jsonExceptionCode = null;
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
		
			TaxLineConfigHeaderVb taxLineConfigHeaderVb = new TaxLineConfigHeaderVb();
			taxLineConfigHeaderVb = taxlinelst.get(0);
			taxlinelst.get(0).setActionType("Reject");
			exceptionCode = taxLineConfigHeadersWb.bulkReject(taxlinelst,taxLineConfigHeaderVb);
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

	/*-------------------------------------Approve TaxLine config------------------------------------------*/
	@RequestMapping(path = "/approveTaxLineConfig", method = RequestMethod.POST)
	//@ApiOperation(value = "Approve Tax Line Config", notes = "reject Tax Line Config", response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> approveTaxLineConfig(@RequestBody List<TaxLineConfigHeaderVb> taxlinelst) {
		JSONExceptionCode jsonExceptionCode = null;
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			TaxLineConfigHeaderVb taxLineConfigHeaderVb = new TaxLineConfigHeaderVb();
			taxLineConfigHeaderVb = taxlinelst.get(0);
			taxlinelst.get(0).setActionType("Approve");
			exceptionCode = taxLineConfigHeadersWb.bulkApprove(taxlinelst,taxLineConfigHeaderVb);
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
	/*-------------------------------------EFFECTIVE DATE CHECK-----------------------------------------*/
	@RequestMapping(path = "/checkEffectiveDate", method = RequestMethod.POST)
	//@ApiOperation(value = "Check Effective Date", notes = "Effective Date Check", response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> checkEffectiveDate(@RequestBody TaxLineConfigHeaderVb vObject) {
		JSONExceptionCode jsonExceptionCode = null;
		ExceptionCode exceptionCode = new ExceptionCode();
		int effectiveDateCnt = 0;
		int businesCount = 0;
		try {
			vObject.setActionType("Add");
			//businesCount = taxLineConfigHeadersDao.effectiveDateBusinessCheck(vObject);
			//if(businesCount == 1) {
			effectiveDateCnt = taxLineConfigHeadersDao.effectiveDateCheck(vObject, 0);
			if(effectiveDateCnt == 0) {
				effectiveDateCnt = taxLineConfigHeadersDao.effectiveDateCheck(vObject, 1);
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
			/*}else {
				exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
				exceptionCode.setErrorMsg("Effective Date should be greater or Equal to Business Date");
				jsonExceptionCode = new JSONExceptionCode(Constants.SUCCESSFUL_OPERATION, exceptionCode.getErrorMsg(), exceptionCode.getOtherInfo());
				return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
			}*/
			exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
			jsonExceptionCode = new JSONExceptionCode(Constants.SUCCESSFUL_OPERATION, exceptionCode.getErrorMsg(), exceptionCode.getOtherInfo());
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		} catch (RuntimeCustomException rex) {
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
		}
	}
	/*-------------------------------------Review TaxLineConfig ------------------------------------------*/
	@RequestMapping(path = "/reviewTaxLineConfig", method = RequestMethod.POST)
	//@ApiOperation(value = "Get Headers", notes = "Fetch all the existing records from the table", response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> reviewTransLineConfig(@RequestBody TaxLineConfigHeaderVb vObject) {
		JSONExceptionCode jsonExceptionCode = null;
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			vObject.setActionType("Query");
			exceptionCode = taxLineConfigHeadersWb.reviewRecordTaxLineHeader(vObject);
			if (exceptionCode.getErrorCode() == Constants.SUCCESSFUL_OPERATION && exceptionCode.getResponse() != null) {
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
}
