package com.vision.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.vision.dao.CommonApiDao;
import com.vision.exception.ExceptionCode;
import com.vision.exception.JSONExceptionCode;
import com.vision.exception.RuntimeCustomException;
import com.vision.util.Constants;
import com.vision.vb.CommonApiModel;
import com.vision.vb.VisionUsersVb;

@RestController
@RequestMapping("commonCompApi")
//@Api(value = "commonCompApi", description = "commonCompApi")
public class CommonApiController {

	@Autowired
	CommonApiDao commonApiDao;
	
	@RequestMapping(path = "/commonDataApi", method = RequestMethod.POST)
	//@ApiOperation(value = "commonDataApi",notes = "commonDataApi",response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> commonDataApi(@RequestBody CommonApiModel vObject){
		JSONExceptionCode jsonExceptionCode  = null;
		try{
			ExceptionCode exceptionCode = commonApiDao.getCommonResultDataFetch(vObject, null);
			jsonExceptionCode = new JSONExceptionCode(exceptionCode.getErrorCode(), exceptionCode.getErrorMsg(), exceptionCode.getResponse(),exceptionCode.getOtherInfo());
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		}catch(RuntimeCustomException rex){
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
		}
	}
	@RequestMapping(path = "/saveAppTheme", method = RequestMethod.POST)
	//@ApiOperation(value = "saveAppTheme",notes = "saveAppTheme",response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> saveAppTheme(@RequestBody VisionUsersVb vObject){
		JSONExceptionCode jsonExceptionCode  = null;
		try{
			ExceptionCode exceptionCode = commonApiDao.insertAppTheme(vObject) ;
			jsonExceptionCode = new JSONExceptionCode(exceptionCode.getErrorCode(), exceptionCode.getErrorMsg(), exceptionCode.getResponse());
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		}catch(RuntimeCustomException rex){
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
		}
	}
}
