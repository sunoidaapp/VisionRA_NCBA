package com.vision.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.vision.download.ExportXlsServlet;
import com.vision.exception.ExceptionCode;
import com.vision.exception.JSONExceptionCode;
import com.vision.exception.RuntimeCustomException;
import com.vision.util.Constants;
import com.vision.util.ValidationUtil;
import com.vision.vb.FileInfoVb;
import com.vision.vb.VisionUploadVb;
import com.vision.wb.VisionUploadWb;

@RestController
@RequestMapping(value = "visionUpload")
//@Api(value="visionUpload",description="Operations pertaining to Handle Files to/from Server")
public class VisionUploadController {
	
	@Autowired
	private VisionUploadWb visionUploadWb;

	@Autowired
	ExportXlsServlet exportXlsServlet;
	
	/*-------------------------------------Upload Files Rest Service-------------------------------*/
	@RequestMapping(path = "/uploadFilesToFtp", method = RequestMethod.POST)
	//@ApiOperation(value = "Uploading Multipart files to Server",notes = "Upload files to Server",response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> uploadFilesToFtp(@RequestParam("file") MultipartFile[] files){
		JSONExceptionCode jsonExceptionCode = null;
		ExceptionCode exceptionCode = null;
		try{
			VisionUploadVb vObject = new VisionUploadVb();
			vObject.setActionType("upload");
			exceptionCode = visionUploadWb.doValidate(vObject);
			if (exceptionCode.getErrorCode() == Constants.ERRONEOUS_OPERATION) {
				jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, exceptionCode.getErrorMsg(),exceptionCode.getOtherInfo());
				return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
			}
		   if (files.length<=0) {
			   return new ResponseEntity<JSONExceptionCode>(new JSONExceptionCode(Constants.FILE_UPLOAD_REMOTE_ERROR, "Upload file error",null), HttpStatus.EXPECTATION_FAILED);
		   }else {		  
			   for(MultipartFile uploadedFile : files) {
				   String extension = FilenameUtils.getExtension(uploadedFile.getOriginalFilename());
				   FileInfoVb fileInfoVb = new FileInfoVb();
				    fileInfoVb.setName(uploadedFile.getOriginalFilename());
				    fileInfoVb.setData(uploadedFile.getBytes());
				    fileInfoVb.setExtension(extension);
				    if(ValidationUtil.isValid(fileInfoVb.getName())){
				    	exceptionCode = visionUploadWb.doUpload(fileInfoVb.getName(), fileInfoVb.getData());
				    	if(exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION){
							return new ResponseEntity<JSONExceptionCode>(new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, "Fail",null), HttpStatus.EXPECTATION_FAILED);
				    	}
				    }
				  }
			  	}
		   return new ResponseEntity<JSONExceptionCode>(new JSONExceptionCode(Constants.SUCCESSFUL_OPERATION, "Vision Upload Successful",""), HttpStatus.OK);
		}catch (Exception e) {
			RuntimeCustomException ex = (RuntimeCustomException)e;
			e.printStackTrace();
			return new ResponseEntity<JSONExceptionCode>(new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, ex.getMessage(),null), HttpStatus.EXPECTATION_FAILED);
		}
	}
	
	/*-------------------------------------Page Load Values-------------------------------*/
	@RequestMapping(path = "/pageLoadValues", method = RequestMethod.GET)
	//@ApiOperation(value = "Page Load Values",notes = "Load AT/NT Values on screen load",response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> pageOnLoad(){
		JSONExceptionCode jsonExceptionCode  = null;
		try{
			VisionUploadVb vObj = new VisionUploadVb();
			vObj.setActionType("Query");
			ExceptionCode exceptionCode = visionUploadWb.doValidate(vObj);
			if(exceptionCode.getErrorCode() == Constants.ERRONEOUS_OPERATION) {
				jsonExceptionCode = new JSONExceptionCode(exceptionCode.getErrorCode(), exceptionCode.getErrorMsg(), null);
				return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
			}
			ArrayList arrayList = visionUploadWb.getPageLoadValues();
			jsonExceptionCode = new JSONExceptionCode(Constants.SUCCESSFUL_OPERATION, "On Load Values", arrayList);
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		}catch(RuntimeCustomException rex){
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		}	
	}
	
	/*-------------------------------------Get Query Results -------------------------------*/
	@RequestMapping(path = "/getAllQueryResults", method = RequestMethod.POST)
	//@ApiOperation(value = "Get All the details", notes = "Fetch all the existing records from the table",response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> getAllQueryResults(@RequestBody VisionUploadVb vObject){
		JSONExceptionCode jsonExceptionCode = null;
		try {
			vObject.setActionType("Query");
			ExceptionCode exceptionCode = visionUploadWb.getQueryResults(vObject);
			jsonExceptionCode = new JSONExceptionCode(exceptionCode.getErrorCode(), exceptionCode.getErrorMsg(),exceptionCode.getResponse(), exceptionCode.getOtherInfo());
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);	
		}catch(RuntimeCustomException rex){
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
		}
	}
	
	/*-------------------------------------Adding records-------------------------------*/
	@RequestMapping(path = "/addVisionUpload", method = RequestMethod.POST)
	//@ApiOperation(value = "Add Vision Upload", notes = "Add Vision Upload", response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> addVisionUpload(@RequestBody List<VisionUploadVb> vObject) {
		JSONExceptionCode jsonExceptionCode = null;
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			exceptionCode.setOtherInfo(vObject.get(0));
			vObject.get(0).setActionType("Add");
			exceptionCode = visionUploadWb.insertRecord(exceptionCode,vObject);
			jsonExceptionCode = new JSONExceptionCode(exceptionCode.getErrorCode(), exceptionCode.getErrorMsg(),exceptionCode.getResponse(), exceptionCode.getOtherInfo());
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		} catch (RuntimeCustomException rex) {
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
		}
	}
	
	/*-------------------------------------Reintiate the progress-------------------------------*/
	@RequestMapping(path = "/modifyVisionUpload", method = RequestMethod.POST)
	//@ApiOperation(value = "Modify Vision Upload", notes = "Modify Vision Upload", response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> modifyVisionUpload(@RequestBody VisionUploadVb vObject) {
		JSONExceptionCode jsonExceptionCode = null;
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			vObject.setActionType("Modify");
			exceptionCode = visionUploadWb.modifyRecord(vObject);
			jsonExceptionCode = new JSONExceptionCode(exceptionCode.getErrorCode(), exceptionCode.getErrorMsg(),exceptionCode.getResponse(), exceptionCode.getOtherInfo());
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		} catch (RuntimeCustomException rex) {
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
		}
	}
	/*-------------------------------------Download Files Rest Service-------------------------------*/
	@RequestMapping(path = "/downloadFilesFromFtp", method = RequestMethod.POST)
	//@ApiOperation(value = "Downloading Multipart files to Server",notes = "Download files from server",response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> downloadFilesFromFtp(@RequestParam("fileName") String fileName, @RequestParam("fileExtension") String fileExtension, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		JSONExceptionCode jsonExceptionCode = null;
		try{
			VisionUploadVb vObject = new VisionUploadVb();
			vObject.setActionType("Download");
			ExceptionCode exceptionCode = visionUploadWb.fileDownload(1,fileName,"","", fileExtension,vObject);
			if (exceptionCode.getErrorCode() == Constants.SUCCESSFUL_OPERATION) {
				if (!fileName.contains(" @- ")) {
					String fileExt[] = fileName.split("\\.");
					request.setAttribute("fileExtension", fileExt[1]);
					request.setAttribute("fileName", fileExt[0]);
				} else {
					request.setAttribute("fileExtension", "zip");
					request.setAttribute("fileName", "logs");
				}
				request.setAttribute("filePath", exceptionCode.getRequest());
				exportXlsServlet.doPost(request,response); 
				if(response.getStatus()==404) {
					return new ResponseEntity<JSONExceptionCode>(new JSONExceptionCode(Constants.ERRONEOUS_OPERATION,"File not found",null), HttpStatus.EXPECTATION_FAILED);
				}
				return new ResponseEntity<JSONExceptionCode>(new JSONExceptionCode(Constants.SUCCESSFUL_OPERATION, "Success",exceptionCode.getResponse(),exceptionCode.getOtherInfo()), HttpStatus.OK);
			}else {
				jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, exceptionCode.getErrorMsg(),exceptionCode.getOtherInfo());
				return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
			}
		}catch(RuntimeCustomException rex){
			return new ResponseEntity<JSONExceptionCode>(new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(),null), HttpStatus.EXPECTATION_FAILED);
		}
	}
	
	/*-------------------------------------List Rest Service-------------------------------*/
	@RequestMapping(path = "/listFilesFromSftp", method = RequestMethod.GET)
	//@ApiOperation(value = "Listing Multipart files from Server",notes = "List files from Server",response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> listRestFilesFromFtpServer(@RequestParam("groupBy") String groupBy,@RequestParam("dirType") int dirType){
		ExceptionCode exceptionCode = null;
		try {
			VisionUploadVb visionUploadVb = new VisionUploadVb();
			visionUploadVb.setActionType("Query");
			if("T".equalsIgnoreCase(groupBy)){
				groupBy = "Table";
			}else if("B".equalsIgnoreCase(groupBy)){
				groupBy = "Build";
			}else {
				groupBy = "Date";
			}
			exceptionCode = visionUploadWb.listFilesFromFtpServer(dirType, groupBy,visionUploadVb);
			return new ResponseEntity<JSONExceptionCode>(new JSONExceptionCode(exceptionCode.getErrorCode(), exceptionCode.getErrorMsg(),exceptionCode.getResponse(), exceptionCode.getOtherInfo()), HttpStatus.OK);
		}catch(RuntimeCustomException rex){
			return new ResponseEntity<JSONExceptionCode>(new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(),null), HttpStatus.EXPECTATION_FAILED);
		}
	}

	/*-------------------------------------Load Excel -------------------------------*/
	@RequestMapping(path = "/loadExcel", method = RequestMethod.POST)
	//@ApiOperation(value = "Loading the Excel ", notes = "Loading the Excel", response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> loadExcel(@RequestParam("file") MultipartFile file) {
		JSONExceptionCode jsonExceptionCode = null;
		ExceptionCode exceptionCode = new ExceptionCode();	
		boolean isExlel;
		try {
			VisionUploadVb visionUploadVb = new VisionUploadVb();
			visionUploadVb.setActionType("Query");
			byte [] byteArr=file.getBytes();
			InputStream inputStream = new ByteArrayInputStream(byteArr);	
			isExlel = visionUploadWb.isExcel(inputStream);
			if(isExlel != true){
				return new ResponseEntity<JSONExceptionCode>(
						new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, "Please upload a valid Excel file", null),
						HttpStatus.OK);
			}
			exceptionCode = visionUploadWb.doloadingExcel(file,visionUploadVb);
			jsonExceptionCode = new JSONExceptionCode(exceptionCode.getErrorCode(), exceptionCode.getErrorMsg(),exceptionCode.getResponse(), exceptionCode.getOtherInfo());
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);		}
		
		
		catch (Exception e) {
			return new ResponseEntity<JSONExceptionCode>(new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, e.getMessage(),null), HttpStatus.EXPECTATION_FAILED);
		}
	}
	
	/*-------------------------------------Delete Concession config------------------------------------------*/
	@RequestMapping(path = "/deleteVisionUpload", method = RequestMethod.POST)
	//@ApiOperation(value = "delete Vision Upload", notes = "delete Vision Upload", response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> deleteConcessionConfig(@RequestBody VisionUploadVb vObject) {
		JSONExceptionCode jsonExceptionCode = null;
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			vObject.setActionType("Delete");
			exceptionCode = visionUploadWb.deleteRecord(vObject);
			jsonExceptionCode = new JSONExceptionCode(exceptionCode.getErrorCode(), exceptionCode.getErrorMsg(),exceptionCode.getResponse(), exceptionCode.getOtherInfo());
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		} catch (RuntimeCustomException rex) {
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
		}
	}
}

