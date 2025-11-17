
package com.vision.controller;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.vision.dao.RaTellerFreeDao;
import com.vision.exception.ExceptionCode;
import com.vision.exception.JSONExceptionCode;
import com.vision.exception.RuntimeCustomException;
import com.vision.util.Constants;
import com.vision.vb.RaTellerFreeVb;
import com.vision.vb.ReviewResultVb;
import com.vision.wb.RaTellerFreeWb;

@RestController
@RequestMapping("RaTellerFree")
//@Api(value = "RaTellerFree", description = "RA Teller Free")
public class RaTellerFreeController {

	@Autowired
	RaTellerFreeWb raTellerFreeWb;

	@Autowired
	RaTellerFreeDao raTellerFreeDao;

	public static Logger logger = LoggerFactory.getLogger(RaTellerFreeController.class);

	/*-------------------------------------Teller Free Page Load Values------------------------------------------*/
	@RequestMapping(path = "/pageLoadValues", method = RequestMethod.GET)
	//@ApiOperation(value = "Page Load Values", notes = "Load AT/NT Values on screen load", response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> pageOnLoad() {
		JSONExceptionCode jsonExceptionCode = null;
		try {
			RaTellerFreeVb vObj = new RaTellerFreeVb();
			vObj.setActionType("Query");
			ExceptionCode exceptionCode = raTellerFreeWb.doValidate(vObj);
			if(exceptionCode.getErrorCode() == Constants.ERRONEOUS_OPERATION) {
				jsonExceptionCode = new JSONExceptionCode(exceptionCode.getErrorCode(), exceptionCode.getErrorMsg(), null);
				return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
			}
			ArrayList arrayList = raTellerFreeWb.getPageLoadValues();
			jsonExceptionCode = new JSONExceptionCode(Constants.SUCCESSFUL_OPERATION, "On Load Values", arrayList);
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		} catch (RuntimeCustomException rex) {
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
		}
	}

	/*------------------------------------Teller Free Get All Query ----------------------------------------*/
	@RequestMapping(path = "/getAllQueryResults", method = RequestMethod.POST)
	//@ApiOperation(value = "Get All the details", notes = "Fetch all the existing records from the table", response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> getAllQueryResults(@RequestBody RaTellerFreeVb vObject) {
		JSONExceptionCode jsonExceptionCode = null;
		try {
			vObject.setActionType("Query");
			ExceptionCode exceptionCode = raTellerFreeWb.getAllQueryPopupResult(vObject);
			jsonExceptionCode = new JSONExceptionCode(exceptionCode.getErrorCode(), exceptionCode.getErrorMsg(),
					exceptionCode.getResponse(), exceptionCode.getOtherInfo());
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		} catch (RuntimeCustomException rex) {
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
		}
	}

	/*------------------------------------get Query Results----------------------------------------*/
	@RequestMapping(path = "/getQueryDetails", method = RequestMethod.POST)
	//@ApiOperation(value = "Get All the details", notes = "Fetch all the existing records from the table", response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> getAllQueryDetails(@RequestBody RaTellerFreeVb vObject) {
		JSONExceptionCode jsonExceptionCode = null;
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			vObject.setActionType("Query");
			exceptionCode.setOtherInfo(vObject);
			exceptionCode = raTellerFreeWb.getQueryResults(vObject);
			jsonExceptionCode = new JSONExceptionCode(exceptionCode.getErrorCode(), exceptionCode.getErrorMsg(),exceptionCode.getResponse(), exceptionCode.getOtherInfo());
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		} catch (RuntimeCustomException rex) {
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
		}
	}

	/*-------------------------------------ADD RA Teller Free------------------------------------------*/
	@RequestMapping(path = "/saveRaTellerFree", method = RequestMethod.POST)
	//@ApiOperation(value = "Add Bnf Parameters", notes = "Add Bnf Parameters", response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> addRaTellerFree(@RequestBody List<RaTellerFreeVb> raTellerFreelst) {
		JSONExceptionCode jsonExceptionCode = null;

		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			RaTellerFreeVb raTellerFreeVb = new RaTellerFreeVb();
			raTellerFreelst.get(0).setActionType("Add");
			raTellerFreeVb = raTellerFreelst.get(0);
			exceptionCode.setOtherInfo(raTellerFreeVb);
			exceptionCode = raTellerFreeWb.insertRecord(exceptionCode, raTellerFreelst);
			jsonExceptionCode = new JSONExceptionCode(exceptionCode.getErrorCode(), exceptionCode.getErrorMsg(),
					exceptionCode.getOtherInfo(), exceptionCode.getResponse());
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		} catch (RuntimeCustomException rex) {
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
		}
	}

	/*-------------------------------------Modify RA Teller Free------------------------------------------*/
	@RequestMapping(path = "/modifyRaTellerFree", method = RequestMethod.POST)
	//@ApiOperation(value = "Modify Bnf Parameters", notes = "Modify Bnf Parameters", response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> modifyRaTellerFree(@RequestBody List<RaTellerFreeVb> raTellerFreelst) {
		JSONExceptionCode jsonExceptionCode = null;
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			RaTellerFreeVb raTellerFreeVb = new RaTellerFreeVb();
			raTellerFreelst.get(0).setActionType("Modify");
			raTellerFreeVb = raTellerFreelst.get(0);
			exceptionCode.setOtherInfo(raTellerFreeVb);
			exceptionCode = raTellerFreeWb.modifyRecord(exceptionCode, raTellerFreelst);
			jsonExceptionCode = new JSONExceptionCode(exceptionCode.getErrorCode(), exceptionCode.getErrorMsg(),
					exceptionCode.getOtherInfo(), exceptionCode.getResponse());
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		} catch (RuntimeCustomException rex) {
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
		}
	}

	/*-------------------------------------Delete RA Teller Free------------------------------------------*/
	@RequestMapping(path = "/deleteRaTellerFree", method = RequestMethod.POST)
	//@ApiOperation(value = "delete Bnf Parameters", notes = "delete Bnf Parameters", response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> deleteHrLedger(@RequestBody List<RaTellerFreeVb> raTellerFreelst) {
		JSONExceptionCode jsonExceptionCode = null;
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			RaTellerFreeVb raTellerFreeVb = raTellerFreelst.get(0);
			raTellerFreelst.get(0).setActionType("Delete");
			raTellerFreeVb = raTellerFreelst.get(0);
			exceptionCode.setOtherInfo(raTellerFreeVb);
			exceptionCode = raTellerFreeWb.deleteRecord(exceptionCode, raTellerFreelst);
			jsonExceptionCode = new JSONExceptionCode(exceptionCode.getErrorCode(), exceptionCode.getErrorMsg(),
					exceptionCode.getOtherInfo(), exceptionCode.getResponse());
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		} catch (RuntimeCustomException rex) {
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
		}
	}

	/*-------------------------------------Approve RA Teller Free------------------------------------------*/
	@RequestMapping(path = "/bulkApproveRaTellerFree", method = RequestMethod.POST)
	//@ApiOperation(value = "Approve RA Teller Free", notes = "Approve RA Teller Free", response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> bulkApprove(@RequestBody List<RaTellerFreeVb> raTellerFreeList) {
		JSONExceptionCode jsonExceptionCode = null;
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			RaTellerFreeVb vObject = new RaTellerFreeVb();
			raTellerFreeList.get(0).setActionType("Approve");
			vObject = raTellerFreeList.get(0);
			exceptionCode.setOtherInfo(vObject);
			exceptionCode = raTellerFreeWb.bulkApprove(raTellerFreeList, vObject);
			jsonExceptionCode = new JSONExceptionCode(exceptionCode.getErrorCode(), exceptionCode.getErrorMsg(),
					exceptionCode.getOtherInfo());
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		} catch (RuntimeCustomException rex) {
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
		}
	}

	/*-------------------------------------Reject RA Teller Free------------------------------------------*/
	@RequestMapping(path = "/bulkRejectRaTellerFree", method = RequestMethod.POST)
	//@ApiOperation(value = "Reject RA Teller Free", notes = "Reject RA Teller Free", response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> bulkReject(@RequestBody List<RaTellerFreeVb> raTellerFreeList) {
		JSONExceptionCode jsonExceptionCode = null;
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			RaTellerFreeVb vObject = new RaTellerFreeVb();
			raTellerFreeList.get(0).setActionType("Reject");
			vObject = raTellerFreeList.get(0);
			exceptionCode.setOtherInfo(vObject);
			exceptionCode = raTellerFreeWb.bulkReject(raTellerFreeList, vObject);
			jsonExceptionCode = new JSONExceptionCode(exceptionCode.getErrorCode(), exceptionCode.getErrorMsg(),
					exceptionCode.getOtherInfo());
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		} catch (RuntimeCustomException rex) {
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
		}
	}

	/*-------------------------------------Review ------------------------------------------*/
	@RequestMapping(path = "/reviewRaTellerFree", method = RequestMethod.POST)
	//@ApiOperation(value = "Get Headers", notes = "Fetch all the existing records from the table", response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> review(@RequestBody RaTellerFreeVb vObject) {
		JSONExceptionCode jsonExceptionCode = null;
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			vObject.setActionType("Query");
			exceptionCode = raTellerFreeWb.doValidate(vObject);
			if(exceptionCode.getErrorCode() == Constants.ERRONEOUS_OPERATION){
				jsonExceptionCode = new JSONExceptionCode(exceptionCode.getErrorCode(), exceptionCode.getErrorMsg(),exceptionCode.getResponse(), exceptionCode.getOtherInfo());
			}
			List<ReviewResultVb> tellerFreeList = raTellerFreeWb.reviewRecord(vObject);
			if (tellerFreeList != null && !tellerFreeList.isEmpty()) {
				exceptionCode.setResponse(tellerFreeList);
				exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
				exceptionCode.setErrorMsg("Review Successful");
				jsonExceptionCode = new JSONExceptionCode(Constants.SUCCESSFUL_OPERATION, exceptionCode.getErrorMsg(),
						exceptionCode.getResponse(), exceptionCode.getOtherInfo());
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

}
