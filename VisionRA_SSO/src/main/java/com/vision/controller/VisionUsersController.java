package com.vision.controller;

import java.io.IOException;
import java.util.ArrayList;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.vision.dao.VisionUsersDao;
import com.vision.exception.ExceptionCode;
import com.vision.exception.JSONExceptionCode;
import com.vision.exception.RuntimeCustomException;
import com.vision.util.Constants;
import com.vision.vb.VisionUsersVb;
import com.vision.wb.VisionUploadWb;
import com.vision.wb.VisionUsersWb;

@RestController
@RequestMapping("raUserSetup")
//@Api(value = "raUserSetup", description = "RA User Setup screen configuration")
public class VisionUsersController {

	@Autowired
	VisionUsersWb visionUsersWb;
	@Autowired
	VisionUploadWb visionUploadWb;
	@Autowired
	VisionUsersDao visionUsersDao;

	/*-------------------------------------BRANCH RM TARGET SCREEN PAGE LOAD------------------------------------------*/
	@RequestMapping(path = "/pageLoadValues", method = RequestMethod.GET)
	//@ApiOperation(value = "Page Load Values", notes = "Load AT/NT Values on screen load", response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> pageOnLoad() {
		JSONExceptionCode jsonExceptionCode = null;
		try {
			VisionUsersVb vObj = new VisionUsersVb();
			vObj.setActionType("Query");
			ExceptionCode exceptionCode = visionUsersWb.doValidate(vObj);
			if(exceptionCode.getErrorCode() == Constants.ERRONEOUS_OPERATION) {
				jsonExceptionCode = new JSONExceptionCode(exceptionCode.getErrorCode(), exceptionCode.getErrorMsg(), null);
				return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
			}
			ArrayList arrayList = visionUsersWb.getPageLoadValues();
			jsonExceptionCode = new JSONExceptionCode(Constants.SUCCESSFUL_OPERATION, "On Load Values", arrayList);
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		} catch (RuntimeCustomException rex) {
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		}
	}

	/*--------------get Query Results------------------*/
	@RequestMapping(path = "/getAllQueryResults", method = RequestMethod.POST)
	//@ApiOperation(value = "Get All the details", notes = "Fetch all the existing records from the table", response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> getAllQueryResults(@RequestBody VisionUsersVb vObject) {
		JSONExceptionCode jsonExceptionCode = null;
		try {
			vObject.setActionType("Query");
			ExceptionCode exceptionCode = visionUsersWb.getAllQueryPopupResult(vObject);
			jsonExceptionCode = new JSONExceptionCode(exceptionCode.getErrorCode(), exceptionCode.getErrorMsg(),exceptionCode.getResponse(), exceptionCode.getOtherInfo());
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		} catch (RuntimeCustomException rex) {
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
		}
	}

	/*--------------get Query Results------------------*/
	@RequestMapping(path = "/getQueryDetails", method = RequestMethod.POST)
	//@ApiOperation(value = "Get All the details", notes = "Fetch all the existing records from the table", response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> getAllQueryDetails(@RequestBody VisionUsersVb vObject) {
		JSONExceptionCode jsonExceptionCode = null;
		try {
			vObject.setActionType("Query");
			ExceptionCode exceptionCode = visionUsersWb.getQueryResults(vObject);
			jsonExceptionCode = new JSONExceptionCode(exceptionCode.getErrorCode(), exceptionCode.getErrorMsg(),exceptionCode.getResponse(), exceptionCode.getOtherInfo());
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		} catch (RuntimeCustomException rex) {
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
		}
	}

	/*-------------------------------------ADD RA User Setup------------------------------------------*/
	@RequestMapping(path = "/addRaUserSetup", method = RequestMethod.POST)
	//@ApiOperation(value = "Add RA User Setup", notes = "Add RA User Setup", response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> addRaUserSetup(@RequestBody VisionUsersVb vObject) {
		JSONExceptionCode jsonExceptionCode = null;
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			vObject.setActionType("Add");
			exceptionCode = visionUsersWb.checkUserLoginIdExists(vObject);
			if (exceptionCode.getErrorCode() == Constants.SUCCESSFUL_OPERATION) {
				exceptionCode = visionUsersWb.insertRecord(vObject);
			}
			jsonExceptionCode = new JSONExceptionCode(exceptionCode.getErrorCode(), exceptionCode.getErrorMsg(),exceptionCode.getResponse(), exceptionCode.getOtherInfo());
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		} catch (RuntimeCustomException rex) {
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
		}
	}

	/*-------------------------------------Modify RA User Setup------------------------------------------*/
	@RequestMapping(path = "/modifyRaUserSetup", method = RequestMethod.POST)
	//@ApiOperation(value = "Modify RA User Setup", notes = "Modify RA User Setup", response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> modifyRaUserSetup(@RequestBody VisionUsersVb vObject) {
		JSONExceptionCode jsonExceptionCode = null;
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			vObject.setActionType("Modify");
			exceptionCode = visionUsersWb.modifyRecord(vObject);
			jsonExceptionCode = new JSONExceptionCode(exceptionCode.getErrorCode(), exceptionCode.getErrorMsg(),exceptionCode.getResponse(), exceptionCode.getOtherInfo());
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		} catch (RuntimeCustomException rex) {
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
		}
	}

	/*-------------------------------------Delete RA User Setup------------------------------------------*/
	@RequestMapping(path = "/deleteRaUserSetup", method = RequestMethod.POST)
	//@ApiOperation(value = "delete RA User Setup", notes = "delete RA User Setup", response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> deleteRaUserSetup(@RequestBody VisionUsersVb vObject) {
		JSONExceptionCode jsonExceptionCode = null;
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			vObject.setActionType("Delete");
			exceptionCode = visionUsersWb.deleteRecord(vObject);
			jsonExceptionCode = new JSONExceptionCode(exceptionCode.getErrorCode(), exceptionCode.getErrorMsg(),exceptionCode.getResponse(), exceptionCode.getOtherInfo());
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		} catch (RuntimeCustomException rex) {
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
		}
	}

	/*-------------------------------------Reject RA User Setup------------------------------------------*/
	@RequestMapping(path = "/rejectRaUserSetup", method = RequestMethod.POST)
	//@ApiOperation(value = "reject RA User Setup", notes = "reject RA User Setup", response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> rejectRaUserSetup(@RequestBody VisionUsersVb vObject) {
		JSONExceptionCode jsonExceptionCode = null;
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			vObject.setActionType("Reject");
			exceptionCode = visionUsersWb.reject(vObject);
			jsonExceptionCode = new JSONExceptionCode(exceptionCode.getErrorCode(), exceptionCode.getErrorMsg(),exceptionCode.getResponse(), exceptionCode.getOtherInfo());
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		} catch (RuntimeCustomException rex) {
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
		}
	}

	/*-------------------------------------Approve RA User Setup------------------------------------------*/
	@RequestMapping(path = "/approveRaUserSetup", method = RequestMethod.POST)
	//@ApiOperation(value = "Approve RA User Setup", notes = "approve RA User Setup", response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> approveRaUserSetup(@RequestBody VisionUsersVb vObject) {
		JSONExceptionCode jsonExceptionCode = null;
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			vObject.setActionType("Approve");
			exceptionCode = visionUsersWb.approve(vObject);
			jsonExceptionCode = new JSONExceptionCode(exceptionCode.getErrorCode(), exceptionCode.getErrorMsg(),exceptionCode.getResponse(), exceptionCode.getOtherInfo());
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		} catch (RuntimeCustomException rex) {
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
		}
	}

	/*-------------------------------------Unlock Vision User------------------------------------------*/
	@RequestMapping(path = "/unLockUser", method = RequestMethod.POST)
	//@ApiOperation(value = "Unlock user ", notes = "unlock user", response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> unLockUser(@RequestBody VisionUsersVb vObject) {
		JSONExceptionCode jsonExceptionCode = null;
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			vObject.setActionType("Modify");
			exceptionCode = visionUsersWb.unlockUser(vObject);
			jsonExceptionCode = new JSONExceptionCode(exceptionCode.getErrorCode(), exceptionCode.getErrorMsg(),exceptionCode.getResponse(), exceptionCode.getOtherInfo());
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		} catch (RuntimeCustomException rex) {
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
		}
	}

	/*-------------------------------------Upload Files------------------------------------------*/
	@RequestMapping(path = "/uploadUserPhoto", method = RequestMethod.POST)
	//@ApiOperation(value = "upload UserPhoto", notes = "Upload User Photo", response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> uploadFile(@RequestParam("visionId") String visionId,
			@RequestParam("uploadFiles") MultipartFile file) throws IOException {
		JSONExceptionCode jsonExceptionCode = null;
		ExceptionCode exceptionCode = new ExceptionCode();
		VisionUsersVb vObject = new VisionUsersVb();
		try {
			vObject.setActionType("Upload");
			exceptionCode = visionUsersWb.uploadFile(file, visionId,vObject);
			jsonExceptionCode = new JSONExceptionCode(exceptionCode.getErrorCode(), exceptionCode.getErrorMsg(),
					exceptionCode.getOtherInfo());
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		} catch (RuntimeCustomException rex) {
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
		}
	}

	/*-------------------------------------Download Files------------------------------------------*/
	@RequestMapping(path = "/downloadUserPhoto", method = RequestMethod.POST)
	//@ApiOperation(value = "download User Photo", notes = "Download File", response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> downloadFile(@RequestParam("downloadFileName") String downloadFileName,
			HttpServletRequest request, HttpServletResponse response) throws IOException {
		JSONExceptionCode jsonExceptionCode = null;
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			VisionUsersVb vObject = new VisionUsersVb();
			vObject.setActionType("Download");
			exceptionCode = visionUsersWb.downloadFile(downloadFileName,vObject);
			if (exceptionCode.getErrorCode() == Constants.SUCCESSFUL_OPERATION) {
				String fileExt[] = downloadFileName.split("\\.");
				request.setAttribute("fileExtension", fileExt[1]);
				request.setAttribute("fileName", fileExt[0]);
				request.setAttribute("filePath", exceptionCode.getResponse());
				visionUploadWb.setExportXlsServlet(request, response);
				if (response.getStatus() == 404) {
					jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, "File not found", null);
					return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
				} else {
					jsonExceptionCode = new JSONExceptionCode(Constants.SUCCESSFUL_OPERATION, "Success", response);
					return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
				}
			} else {
				return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
			}
		} catch (RuntimeCustomException rex) {
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
		}
	}
	/*-------------------------------------User Password Change------------------------------------------*/
	/*@RequestMapping(path = "/passwordChange", method = RequestMethod.POST)
	//@ApiOperation(value = "Password Change", notes = "password Change", response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> passwordChange(@RequestBody VisionUsersVb vObject) {
		JSONExceptionCode jsonExceptionCode = null;
		ExceptionCode exceptionCode = new ExceptionCode();
		try {

			exceptionCode = visionUsersWb.passwordChange(vObject);
			if (exceptionCode.getErrorCode() == Constants.SUCCESSFUL_OPERATION) {
				jsonExceptionCode = new JSONExceptionCode(Constants.SUCCESSFUL_OPERATION, exceptionCode.getErrorMsg(),
						exceptionCode.getOtherInfo());
			} else {
				jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, exceptionCode.getErrorMsg(),
						exceptionCode.getOtherInfo());
			}
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		} catch (RuntimeCustomException rex) {
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
		}
	}*/
	/*-------------------------------------Unlock Vision User------------------------------------------*/
	@RequestMapping(path = "/generateVisionId", method = RequestMethod.GET)
	//@ApiOperation(value = "Generate Vision ID ", notes = "Generate Vision ID", response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> generateVisionId() {
		JSONExceptionCode jsonExceptionCode = null;
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			VisionUsersVb vObject = new VisionUsersVb();
			vObject.setActionType("Query");
			exceptionCode = visionUsersWb.generateVisionId(vObject);
			jsonExceptionCode = new JSONExceptionCode(exceptionCode.getErrorCode(), exceptionCode.getErrorMsg(),exceptionCode.getResponse(), exceptionCode.getOtherInfo());
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		} catch (RuntimeCustomException rex) {
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
		}
	}
	@RequestMapping(path = "/reviewUserSetup", method = RequestMethod.POST)
	//@ApiOperation(value = "Review ", notes = "Fetch all the existing records from the table", response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> review(@RequestBody VisionUsersVb vObject) {
		JSONExceptionCode jsonExceptionCode = null;
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			vObject.setActionType("Query");
			exceptionCode = visionUsersWb.doValidate(vObject);
			if(exceptionCode.getErrorCode() == Constants.ERRONEOUS_OPERATION){
				jsonExceptionCode = new JSONExceptionCode(exceptionCode.getErrorCode(), exceptionCode.getErrorMsg(),exceptionCode.getResponse(), exceptionCode.getOtherInfo());
			}
			exceptionCode = visionUsersWb.reviewRecordNew(vObject);
			String errorMesss = exceptionCode.getErrorMsg().replaceAll("Query ", "Review");
			jsonExceptionCode = new JSONExceptionCode(exceptionCode.getErrorCode(), errorMesss,
					exceptionCode.getResponse(), exceptionCode.getOtherInfo());
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		} catch (RuntimeCustomException rex) {
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		}
	}
}
