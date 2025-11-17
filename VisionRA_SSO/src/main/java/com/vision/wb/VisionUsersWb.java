package com.vision.wb;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.vision.dao.AbstractDao;
import com.vision.dao.CommonDao;
import com.vision.dao.NumSubTabDao;
import com.vision.dao.VisionUsersDao;
import com.vision.exception.ExceptionCode;
import com.vision.util.CommonUtils;
import com.vision.util.Constants;
import com.vision.util.RSAEncryptDecryptUtil;
import com.vision.util.ValidationUtil;
import com.vision.vb.CommonVb;
import com.vision.vb.NumSubTabVb;
import com.vision.vb.ReviewResultVb;
import com.vision.vb.VisionUsersVb;
@Component
public class VisionUsersWb extends AbstractDynaWorkerBean<VisionUsersVb>{

	@Autowired
	private VisionUsersDao visionUsersDao;
	@Autowired
	private NumSubTabDao numSubTabDao;
	@Autowired
	private CommonDao commonDao;
	
	@Value("${vision.inbound.private.key}")
	private String visionInboundAuthPrivateKey;
	
	@Value("${vision.default.auth.adServers}")
	private String visionAdServers;
	
	public static Logger logger = LoggerFactory.getLogger(VisionUsersWb.class);
	
	public ArrayList getPageLoadValues(){
		List collTemp = null;
		ArrayList<Object> arrListLocal = new ArrayList<Object>();
		try{
			collTemp = numSubTabDao.findActiveNumSubTabsByNumTab(1);
			arrListLocal.add(collTemp);
			collTemp = numSubTabDao.findActiveNumSubTabsByNumTab(7);
			arrListLocal.add(collTemp);
			String value = commonDao.findVisionVariableValue("EMAIL_DOMAIN");
			if(!ValidationUtil.isValid(value)){
				value = "@sunoida.com";
			}else{
				if(value.indexOf('@') < 0){
					value = "@"+value;
				}
			}
			arrListLocal.add(value);
			collTemp = commonDao.findVerificationRequiredAndStaticDelete("VISION_USERS");
			arrListLocal.add(collTemp);
			String visionAuthAdServers = visionAdServers;
			arrListLocal.add(visionAuthAdServers);
			return arrListLocal;
		}catch(Exception ex){
			ex.printStackTrace();
			//logger.error("Exception in getting the Page load values.", ex);
			return null;
		}
	}
	@Override
	public ExceptionCode getQueryResults(VisionUsersVb vObject){
		int intStatus = 1;
		VisionUsersVb visionUsersVb = new VisionUsersVb();
		ExceptionCode exceptionCode = new ExceptionCode();
		setVerifReqDeleteType(vObject);
		exceptionCode = doValidate(vObject);
		if(exceptionCode.getErrorCode() == Constants.ERRONEOUS_OPERATION){
			return exceptionCode;
		}
		Boolean pendFlag = true;
		List<VisionUsersVb> collTemp = visionUsersDao.getQueryResults(vObject,intStatus);
		if (collTemp.size() == 0){
			intStatus = 0;
			collTemp = visionUsersDao.getQueryResults(vObject,intStatus);
			pendFlag = false;
		}
		if(collTemp.size() == 0){
			exceptionCode = CommonUtils.getResultObject(visionUsersDao.getServiceDesc(), 16, "Query", "");
			exceptionCode.setOtherInfo(vObject);
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg("No records found");
			return exceptionCode;
		}else{
			visionUsersVb = ((ArrayList<VisionUsersVb>)collTemp).get(0);
			exceptionCode.setOtherInfo(vObject);
			exceptionCode.setResponse(visionUsersVb);
			exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
			exceptionCode.setErrorMsg("Successful operation");
			return exceptionCode;
		}
	}
	@Override
	protected void setVerifReqDeleteType(VisionUsersVb vObject) {
		ArrayList<CommonVb> lCommVbList =(ArrayList<CommonVb>) commonDao.findVerificationRequiredAndStaticDelete("VISION_USERS");
		vObject.setStaticDelete(lCommVbList.get(0).isStaticDelete());
		vObject.setVerificationRequired(lCommVbList.get(0).isVerificationRequired());
	}

	@Override
	protected AbstractDao<VisionUsersVb> getScreenDao() {
		return visionUsersDao;
	}

	@Override
	protected void setAtNtValues(VisionUsersVb vObject) {
		// TODO Auto-generated method stub
		
	}
	public ExceptionCode unlockUser(VisionUsersVb vObject){
		ExceptionCode exceptionCode = new ExceptionCode();
		try{
			exceptionCode = doValidate(vObject);
			if(exceptionCode.getErrorCode() == Constants.ERRONEOUS_OPERATION){
				return exceptionCode;
			}
			int retVal = visionUsersDao.doUnlockUserAppr(vObject);
			if(retVal == Constants.SUCCESSFUL_OPERATION){
				exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
				exceptionCode.setErrorMsg("User Unlocked Successfully");
				exceptionCode.setErrorSevr("S");
			}else{
				exceptionCode.setOtherInfo(vObject);
				exceptionCode.setErrorMsg("User is not unlocked");
				exceptionCode.setErrorSevr("F");
			}
			exceptionCode.setOtherInfo(vObject);
			return exceptionCode;
		}catch(Exception e){
			e.printStackTrace();
			exceptionCode.setErrorMsg(e.getMessage());
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorSevr("F");
			return exceptionCode;
		}
	}
	public ExceptionCode uploadFile(MultipartFile file,String visionId,VisionUsersVb vObject) throws IOException{
		ExceptionCode exceptionCode = new ExceptionCode();
    	InputStream in = null;
		ByteArrayOutputStream out =null;
		OutputStream outputStream = null;
		try{
			exceptionCode = doValidate(vObject);
			if(exceptionCode.getErrorCode() == Constants.ERRONEOUS_OPERATION){
				return exceptionCode;
			}
			String fileName = visionId+".png";
			String filePath = commonDao.findVisionVariableValue("RA_IMAGE_PATH");
			//String filePath = "E:\\RA_Image\\";
			if(!ValidationUtil.isValid(filePath)) {
				//logger.error("Image Folder path not configured!!!");
				filePath = System.getProperty("java.io.tmpdir"); 
			}
			in = file.getInputStream();
		    out = new ByteArrayOutputStream();  
		    long currentTime = System.currentTimeMillis();
		    File lfile = new File(filePath+File.separator+fileName);
		    if(lfile.exists()) {
		    	lfile.delete();
		    }
		    outputStream = new FileOutputStream(filePath+File.separator+fileName); 
		    int length = file.getOriginalFilename().length();  
		    int bufferSize = 1024;  
		    byte[] buffer = new byte[bufferSize];  
		    while ((length = in.read(buffer)) != -1) {  
		        out.write(buffer, 0, length);
		    }
		    out.writeTo(outputStream);
		    outputStream.flush();
		    outputStream.close();
		    out.flush();
		    out.close();
		    in.close();
		    exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
		    return exceptionCode;
		}catch(Exception ex){
			//logger.error("witeTofile Errror : "+ex.getMessage());
			 outputStream.flush();
		    out.flush();
		    out.close();
		    in.close();			
		    exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
		    return exceptionCode;
		}
	}
	public ExceptionCode downloadFile(String downloadFileName,VisionUsersVb vObject) throws IOException{
		ExceptionCode exceptionCode = new ExceptionCode();
		ByteArrayOutputStream out =null;
		OutputStream outputStream = null;
		try {
			exceptionCode = doValidate(vObject);
			if(exceptionCode.getErrorCode() == Constants.ERRONEOUS_OPERATION){
				return exceptionCode;
			}
			String filePath = commonDao.findVisionVariableValue("RA_IMAGE_PATH");
			String tmpFilePath = System.getProperty("java.io.tmpdir"); 
		    File my_file = new File(filePath+File.separator+downloadFileName);
		    out = new ByteArrayOutputStream();
	        FileInputStream in = new FileInputStream(my_file);
	        out = new ByteArrayOutputStream();  
		    File lfile = new File(tmpFilePath+File.separator+downloadFileName);
		    if(lfile.exists()) {
		    	lfile.delete();
		    }
		    outputStream = new FileOutputStream(tmpFilePath+File.separator+downloadFileName);
		    int length = downloadFileName.length();  
		    int bufferSize = 1024;  
		    byte[] buffer = new byte[bufferSize];  
		    while ((length = in.read(buffer)) != -1) {  
		        out.write(buffer, 0, length);
		    }
		    out.writeTo(outputStream);
		    outputStream.flush();
		    outputStream.close();
		    out.flush();
		    out.close();
		    in.close();
	        
	        exceptionCode.setResponse(tmpFilePath);
	        exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
	        exceptionCode.setErrorMsg("File Downloaded");
		    return exceptionCode;
		}catch(Exception ex){
			//logger.error("Download Errror : "+ex.getMessage());
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
		    return exceptionCode;
		}
	}
	/*public ExceptionCode passwordChange(VisionUsersVb vObject){
		ExceptionCode exceptionCode = new ExceptionCode();
		try{
			int retVal = visionUsersDao.doUserPasswordChange(vObject);
			if(retVal == Constants.SUCCESSFUL_OPERATION){
				exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
				exceptionCode.setErrorMsg("Password Changes Successfully");
				exceptionCode.setErrorSevr("S");
			}else{
				exceptionCode.setOtherInfo(vObject);
				exceptionCode.setErrorMsg("Failed to change User Password");
				exceptionCode.setErrorSevr("F");
			}
			exceptionCode.setOtherInfo(vObject);
			return exceptionCode;
		}catch(Exception e){
			e.printStackTrace();
			exceptionCode.setErrorMsg(e.getMessage());
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorSevr("F");
			return exceptionCode;
		}
	}*/
	public ExceptionCode generateVisionId(VisionUsersVb vObject) {
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			exceptionCode = doValidate(vObject);
			if(exceptionCode.getErrorCode() == Constants.ERRONEOUS_OPERATION){
				return exceptionCode;
			}
			int visionId = visionUsersDao.getMaxVisionId();
			exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
			exceptionCode.setResponse(visionId);
			exceptionCode.setErrorMsg("success");
		}catch(Exception e) {
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg(e.getMessage());
			e.printStackTrace();
		}
		return exceptionCode;
	}
	public ExceptionCode checkUserLoginIdExists(VisionUsersVb vObject) {
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			String userLoginID = vObject.getUserLoginId();
			exceptionCode = doValidate(vObject);
			if(exceptionCode.getErrorCode() == Constants.ERRONEOUS_OPERATION){
				return exceptionCode;
			}
			if(visionUsersDao.userLoginIdExists(userLoginID) > 0) {
				exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
				exceptionCode.setErrorMsg("UserLogin Id Already Exists");
			}	
			else {
				exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
				exceptionCode.setErrorMsg("Success");
			}
		}catch(Exception e) {
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg(e.getMessage());
			e.printStackTrace();
		}
		return exceptionCode;
	}
	public ExceptionCode doValidate(VisionUsersVb vObject){
		ExceptionCode exceptionCode = new ExceptionCode();
		String operation = vObject.getActionType();
		String srtRestriction = getCommonDao().getRestrictionsByUsers("user-setup", operation);
		if(!"Y".equalsIgnoreCase(srtRestriction)) {
			exceptionCode = new ExceptionCode();
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg(operation +" "+Constants.userRestrictionMsg);
			return exceptionCode;
		}
		exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
		if(ValidationUtil.isValid(vObject.getPassword())) {
			try {
				vObject.setPassword(RSAEncryptDecryptUtil.decryptUsingPrivateKeyString(vObject.getPassword(), visionInboundAuthPrivateKey));
				vObject.setPassword(ValidationUtil.jasyptEncryption(vObject.getPassword()));
			} catch (Exception e) {
				e.printStackTrace();
			}
			vObject.setPasswordResetFlag("Y");
		}
		if(ValidationUtil.isValid(vObject.getVisionId())) {
			int visionId = vObject.getVisionId();
			if(String.valueOf(visionId).length() >6) {
				exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
				exceptionCode.setErrorMsg("Maximum of only 6 characters allowed for Vision ID");
				return exceptionCode;
			}
		}
		return exceptionCode;
	}
	protected List<ReviewResultVb> transformToReviewResults(List<VisionUsersVb> approvedCollection, List<VisionUsersVb> pendingCollection) {	
		ResourceBundle rsb = CommonUtils.getResourceManger();
		ArrayList collTemp = getPageLoadValues();
		if(pendingCollection != null)
			getScreenDao().fetchMakerVerifierNames(pendingCollection.get(0));
		if(approvedCollection != null)
			getScreenDao().fetchMakerVerifierNames(approvedCollection.get(0));
		ArrayList<ReviewResultVb> lResult = new ArrayList<ReviewResultVb>();
		
		
		ReviewResultVb visionId = new ReviewResultVb("Vision Id",
        		(pendingCollection == null || pendingCollection.isEmpty())?"":String.valueOf(pendingCollection.get(0).getVisionId()),
                (approvedCollection == null || approvedCollection.isEmpty())?"":String.valueOf(approvedCollection.get(0).getVisionId()),
                		(pendingCollection.get(0).getVisionId() != approvedCollection.get(0).getVisionId()));
        lResult.add(visionId);
		
        ReviewResultVb userLoginId = new ReviewResultVb("User Login Id",
        		(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getUserLoginId(),
                (approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getUserLoginId(),
                		(!pendingCollection.get(0).getUserLoginId().equals(approvedCollection.get(0).getUserLoginId())));
        lResult.add(userLoginId);
        
        ReviewResultVb userName = new ReviewResultVb("User Name",
        		(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getUserName(),
                (approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getUserName(),
                		(!pendingCollection.get(0).getUserName().equals(approvedCollection.get(0).getUserName())));
        lResult.add(userName);
        
		ReviewResultVb lPAdd = new ReviewResultVb("Reset Flag",
				(pendingCollection == null || pendingCollection.isEmpty())?"":"Y".equalsIgnoreCase(pendingCollection.get(0).getPasswordResetFlag())?"Yes":"No",
						("Y".equalsIgnoreCase(approvedCollection.get(0).getPasswordResetFlag())?"Yes":"No"),
						(!pendingCollection.get(0).getPasswordResetFlag().equals(approvedCollection.get(0).getPasswordResetFlag())));
		lResult.add(lPAdd);
		
		 ReviewResultVb userMailId = new ReviewResultVb("User Mail Id",
	        		(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getUserEmailId(),
	                (approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getUserEmailId(),
	                		(!pendingCollection.get(0).getUserEmailId().equals(approvedCollection.get(0).getUserEmailId())));
	     lResult.add(userMailId);
	     
	     ReviewResultVb userGroup = new ReviewResultVb("User Group",
	        		(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getUserGroup(),
	                (approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getUserGroup(),
	                		(!pendingCollection.get(0).getUserGroup().equals(approvedCollection.get(0).getUserGroup())));
        lResult.add(userGroup);
        
        ReviewResultVb userProfile = new ReviewResultVb("User Profile",
        		(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getUserProfile(),
                (approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getUserProfile(),
                		(!pendingCollection.get(0).getUserProfile().equals(approvedCollection.get(0).getUserProfile())));
        lResult.add(userProfile);
        
        
        ReviewResultVb lCountry = new ReviewResultVb("Staff Country",
        		(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getStfcountry(),
                (approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getStfcountry(),
                		(!pendingCollection.get(0).getStfcountry().equals(approvedCollection.get(0).getStfcountry())));
        lResult.add(lCountry);
        
        ReviewResultVb lLeBook = new ReviewResultVb("Staff LE Book",
        		(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getStfleBook(),
                (approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getStfleBook(),
                		(!pendingCollection.get(0).getStfleBook().equals(approvedCollection.get(0).getStfleBook())));
        lResult.add(lLeBook);
        
        ReviewResultVb staffId = new ReviewResultVb("Staff Id",
        		(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getStaffId(),
                (approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getStaffId(),
                		(!pendingCollection.get(0).getStaffId().equals(approvedCollection.get(0).getStaffId())));
        lResult.add(staffId);
        
        
        ReviewResultVb lastActivityDate = new ReviewResultVb("Last Activity Date",(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getLastActivityDate(),
				(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getLastActivityDate(),
						(!pendingCollection.get(0).getLastActivityDate().equals(approvedCollection.get(0).getLastActivityDate())));
		lResult.add(lastActivityDate);
		
		/*ReviewResultVb userStatusDate = new ReviewResultVb("User Satus Date",(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getUserStatusDate(),
				(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getUserStatusDate(),
						(!pendingCollection.get(0).getUserStatusDate().equals(approvedCollection.get(0).getUserStatusDate())));
		lResult.add(userStatusDate);*/
		
		ReviewResultVb applicationAccess = new ReviewResultVb("Application Access",(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getApplicationAccess(),
				(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getApplicationAccess(),
						(!pendingCollection.get(0).getApplicationAccess().equals(approvedCollection.get(0).getApplicationAccess())));
		lResult.add(applicationAccess);
		
		ReviewResultVb enableWidgets = new ReviewResultVb("Enable Widgets",
				(pendingCollection == null || pendingCollection.isEmpty())?"":"Y".equalsIgnoreCase(pendingCollection.get(0).getEnableWidgets())?"Yes":"No",
						("Y".equalsIgnoreCase(approvedCollection.get(0).getEnableWidgets())?"Yes":"No"),
						(!pendingCollection.get(0).getEnableWidgets().equals(approvedCollection.get(0).getEnableWidgets())));
		lResult.add(enableWidgets);
        
		ReviewResultVb updateRestriction = new ReviewResultVb("Access Control",
				(pendingCollection == null || pendingCollection.isEmpty())?"":"Y".equalsIgnoreCase(pendingCollection.get(0).getUpdateRestriction())?"Yes":"No",
						("Y".equalsIgnoreCase(approvedCollection.get(0).getUpdateRestriction())?"Yes":"No"),
						(!pendingCollection.get(0).getUpdateRestriction().equals(approvedCollection.get(0).getUpdateRestriction())));
		lResult.add(updateRestriction);
		
		String updateRestr = "";
		if(pendingCollection != null && !pendingCollection.isEmpty()) {
			updateRestr = pendingCollection.get(0).getUpdateRestriction();
		}
		if(ValidationUtil.isValid(pendingCollection.get(0).getUpdateRestriction()) && "Y".equalsIgnoreCase(pendingCollection.get(0).getUpdateRestriction())) {
			ReviewResultVb legalVechicle = new ReviewResultVb("Legal Vehicle",(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getLegalVehicle(),
					(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getLegalVehicle(),
							(!pendingCollection.get(0).getLegalVehicle().equals(approvedCollection.get(0).getLegalVehicle())));
			lResult.add(legalVechicle);
			
			ReviewResultVb country = new ReviewResultVb("Country",
	        		(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getCountry(),
	                (approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getCountry(),
	                		(!pendingCollection.get(0).getCountry().equals(approvedCollection.get(0).getCountry())));
	        lResult.add(country);
	        
	        ReviewResultVb leBook = new ReviewResultVb("LE Book",
	        		(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getLeBook(),
	                (approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getLeBook(),
	                		(!pendingCollection.get(0).getLeBook().equals(approvedCollection.get(0).getLeBook())));
	        lResult.add(leBook);
	        
	        ReviewResultVb regionProvince = new ReviewResultVb("Region Province",
	        		(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getRegionProvince(),
	                (approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getRegionProvince(),
	                		(!pendingCollection.get(0).getRegionProvince().equals(approvedCollection.get(0).getRegionProvince())));
	        lResult.add(regionProvince);
	        
	        ReviewResultVb businessGrp = new ReviewResultVb("Business Group",
	        		(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getBusinessGroup(),
	                (approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getBusinessGroup(),
	                		(!pendingCollection.get(0).getBusinessGroup().equals(approvedCollection.get(0).getBusinessGroup())));
	        lResult.add(businessGrp);
	        
	        ReviewResultVb sbuCode = new ReviewResultVb("Sbu Code",
	        		(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getSbuCode(),
	                (approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getSbuCode(),
	                		(!pendingCollection.get(0).getSbuCode().equals(approvedCollection.get(0).getSbuCode())));
	        lResult.add(sbuCode);
	        
	        ReviewResultVb prdSuperGrp = new ReviewResultVb("Product Super Group",
	        		(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getProductSuperGroup(),
	                (approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getProductSuperGroup(),
	                		(!pendingCollection.get(0).getProductSuperGroup().equals(approvedCollection.get(0).getProductSuperGroup())));
	        lResult.add(prdSuperGrp);
	        
	        ReviewResultVb prdAttr = new ReviewResultVb("Product Attribute",
	        		(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getProductAttribute(),
	                (approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getProductAttribute(),
	                		(!pendingCollection.get(0).getProductAttribute().equals(approvedCollection.get(0).getProductAttribute())));
	        lResult.add(prdAttr);
	        
	        ReviewResultVb oucAttr = new ReviewResultVb("OUC Attribute",
	        		(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getOucAttribute(),
	                (approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getOucAttribute(),
	                		(!pendingCollection.get(0).getOucAttribute().equals(approvedCollection.get(0).getOucAttribute())));
	        lResult.add(oucAttr);
	        
	        ReviewResultVb aoOfficer = new ReviewResultVb("Account Officer",
	        		(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getAccountOfficer(),
	                (approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getAccountOfficer(),
	                		(!pendingCollection.get(0).getAccountOfficer().equals(approvedCollection.get(0).getAccountOfficer())));
	        lResult.add(aoOfficer);
	        
	        ReviewResultVb gcidAccess = new ReviewResultVb("GCID Access",
					(pendingCollection == null || pendingCollection.isEmpty())?"":"Y".equalsIgnoreCase(pendingCollection.get(0).getGcidAccess())?"Yes":"No",
							("Y".equalsIgnoreCase(approvedCollection.get(0).getGcidAccess())?"Yes":"No"),
							(!pendingCollection.get(0).getGcidAccess().equals(approvedCollection.get(0).getGcidAccess())));
			lResult.add(gcidAccess);
			
			ReviewResultVb raSoc = new ReviewResultVb("Trans / Business Line Id",
	        		(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getRaSoc(),
	                (approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getRaSoc(),
	                		(!pendingCollection.get(0).getRaSoc().equals(approvedCollection.get(0).getRaSoc())));
	        lResult.add(raSoc);
	        
	        ReviewResultVb otherAttr = new ReviewResultVb("Other Attributes",
	        		(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getOtherAttr(),
	                (approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getOtherAttr(),
	                		(!pendingCollection.get(0).getOtherAttr().equals(approvedCollection.get(0).getOtherAttr())));
	        lResult.add(otherAttr);
		}
		
		ReviewResultVb userStatus = new ReviewResultVb("Status",
				(pendingCollection == null || pendingCollection.isEmpty())?"":getNtDescription((List<NumSubTabVb>) collTemp.get(0),pendingCollection.get(0).getUserStatus()),
		        (approvedCollection == null || approvedCollection.isEmpty())?"":getNtDescription((List<NumSubTabVb>) collTemp.get(0),approvedCollection.get(0).getUserStatus()),
		        (pendingCollection.get(0).getUserStatus() != approvedCollection.get(0).getUserStatus()));
		lResult.add(userStatus);  
		
		ReviewResultVb lRecordIndicator = new ReviewResultVb(rsb.getString("recordIndicator"),(pendingCollection == null || pendingCollection.isEmpty())?"":getNtDescription((List<NumSubTabVb>) collTemp.get(1),pendingCollection.get(0).getRecordIndicator()),
		            (approvedCollection == null || approvedCollection.isEmpty())?"":getNtDescription((List<NumSubTabVb>) collTemp.get(1),approvedCollection.get(0).getRecordIndicator()),
		            (pendingCollection.get(0).getRecordIndicator() != approvedCollection.get(0).getRecordIndicator()));
		lResult.add(lRecordIndicator);
		
		ReviewResultVb lMaker = new ReviewResultVb(rsb.getString("maker"),(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getMaker() == 0?"":pendingCollection.get(0).getMakerName(),
				(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getMaker() == 0?"":approvedCollection.get(0).getMakerName(),
				(pendingCollection.get(0).getMaker() != approvedCollection.get(0).getMaker()));
		lResult.add(lMaker);
		
		ReviewResultVb lVerifier = new ReviewResultVb(rsb.getString("verifier"),(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getVerifier() == 0?"":pendingCollection.get(0).getVerifierName(),
				(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getVerifier() == 0?"":approvedCollection.get(0).getVerifierName(),
				(pendingCollection.get(0).getVerifier() != approvedCollection.get(0).getVerifier()));
		lResult.add(lVerifier);
		
		ReviewResultVb lDateLastModified = new ReviewResultVb("Date Last Modified",(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getDateLastModified(),
				(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getDateLastModified(),
						(!pendingCollection.get(0).getDateLastModified().equals(approvedCollection.get(0).getDateLastModified())));
		lResult.add(lDateLastModified);
		ReviewResultVb lDateCreation = new ReviewResultVb("Date Creation",(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getDateCreation(),
				(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getDateCreation(),
						(!pendingCollection.get(0).getDateCreation().equals(approvedCollection.get(0).getDateCreation())));
		lResult.add(lDateCreation);
		return lResult;
	}
}
