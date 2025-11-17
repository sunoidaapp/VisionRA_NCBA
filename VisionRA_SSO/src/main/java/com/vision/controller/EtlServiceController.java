package com.vision.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vision.exception.JSONExceptionCode;
import com.vision.exception.RuntimeCustomException;
import com.vision.util.Constants;
import com.vision.wb.EtlSwitch;

@RestController
@RequestMapping("etlService")
//@Api(value = "etlService", description = "ETL Service")
public class EtlServiceController {
	@Autowired
	EtlSwitch etlSwitch;
	
	/*------------------------------------ON LOAD VALUES------------------------------------------*/
	@RequestMapping(path = "/etlSwitchControl", method = RequestMethod.POST)
	//@ApiOperation(value = "ETL Switch Controls",notes = "ETL Switch Controls",response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> etlSwitchControl(@RequestParam String switchName){
		JSONExceptionCode jsonExceptionCode  = null;
		try{
			if("ETL_SERVICE".equalsIgnoreCase(switchName)) {
				etlSwitch.etlService = false;
			}
			//jsonExceptionCode = new JSONExceptionCode(Constants.SUCCESSFUL_OPERATION, "Page Load Values", arrayList);
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		}catch(RuntimeCustomException rex){
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		}	
	}
}
