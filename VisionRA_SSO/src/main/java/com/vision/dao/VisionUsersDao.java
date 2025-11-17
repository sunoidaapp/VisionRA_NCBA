/**
 * 
 */
package com.vision.dao;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.UncategorizedSQLException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.vision.authentication.CustomContextHolder;
import com.vision.exception.ExceptionCode;
import com.vision.exception.RuntimeCustomException;
import com.vision.util.CommonUtils;
import com.vision.util.Constants;
import com.vision.util.ValidationUtil;
import com.vision.vb.AlphaSubTabVb;
import com.vision.vb.SmartSearchVb;
import com.vision.vb.UserRestrictionVb;
import com.vision.vb.VisionUsersVb;

/**
 * @author kiran-kumar.karra
 *
 */
@Component
public class VisionUsersDao extends AbstractDao<VisionUsersVb> implements ApplicationContextAware {
	public ApplicationContext applicationContext;
	@Value("${app.databaseType}")
	private String databaseType;
	@Value("${app.clientName}")
	private String clientName;
	@Value("${app.productName}")
	private String productName;
	@Value("${spring.profiles.active}")
	private String loginProfile;
	@Value("${vision.default.auth.adServers}")
	private String adServerProfile;

	@Autowired 
	CommonDao commonDao;
	@Override
	protected RowMapper getMapper(){
		RowMapper mapper = new RowMapper() {
			@Override
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				VisionUsersVb visionUsersVb = new VisionUsersVb();
				visionUsersVb.setVisionId(rs.getInt("VISION_ID"));
				if(ValidationUtil.isValid(rs.getString("USER_NAME")))
					visionUsersVb.setUserName(rs.getString("USER_NAME"));
				if(ValidationUtil.isValid(rs.getString("USER_LOGIN_ID")))				
					visionUsersVb.setUserLoginId(rs.getString("USER_LOGIN_ID"));
				if(ValidationUtil.isValid(rs.getString("STF_LE_BOOK")))
					visionUsersVb.setStfleBook(rs.getString("STF_LE_BOOK").trim());
				if(ValidationUtil.isValid(rs.getString("STF_COUNTRY")))
					visionUsersVb.setStfcountry(rs.getString("STF_COUNTRY").trim());
				if(ValidationUtil.isValid(rs.getString("STAFF_ID")))
					visionUsersVb.setStaffId(rs.getString("STAFF_ID"));
				/*if(ValidationUtil.isValid(rs.getString("STAFF_NAME")))
					visionUsersVb.setStaffName(rs.getString("STAFF_NAME"));*/
				
				if(ValidationUtil.isValid(rs.getString("USER_EMAIL_ID")))
					visionUsersVb.setUserEmailId(rs.getString("USER_EMAIL_ID"));
				if(ValidationUtil.isValid(rs.getString("USER_STATUS_DATE")))
					visionUsersVb.setUserStatusDate(rs.getString("USER_STATUS_DATE"));
				visionUsersVb.setUserStatusNt(rs.getInt("USER_STATUS_NT"));
				visionUsersVb.setUserStatus(rs.getInt("USER_STATUS"));
				visionUsersVb.setUserStatusDesc(rs.getString("USER_STATUS_DESC"));
				visionUsersVb.setUserProfileAt(rs.getInt("USER_PROFILE_AT"));
				if(ValidationUtil.isValid(rs.getString("USER_PROFILE")))
					visionUsersVb.setUserProfile(rs.getString("USER_PROFILE").trim());
				visionUsersVb.setUserGroupAt(rs.getInt("USER_GROUP_AT"));
				if(ValidationUtil.isValid(rs.getString("USER_GROUP")))
					visionUsersVb.setUserGroup(rs.getString("USER_GROUP").trim());
				if(ValidationUtil.isValid(rs.getString("LAST_ACTIVITY_DATE")))
					visionUsersVb.setLastActivityDate(rs.getString("LAST_ACTIVITY_DATE").trim());
				if(ValidationUtil.isValid(rs.getString("UPDATE_RESTRICTION")))
					visionUsersVb.setUpdateRestriction(rs.getString("UPDATE_RESTRICTION").trim());
				if(ValidationUtil.isValid(rs.getString("LE_BOOK")))
					visionUsersVb.setLeBook(rs.getString("LE_BOOK").trim());
				if(ValidationUtil.isValid(rs.getString("COUNTRY")))
					visionUsersVb.setCountry(rs.getString("COUNTRY").trim());
				if(ValidationUtil.isValid(rs.getString("LEGAL_VEHICLE")))
					visionUsersVb.setLegalVehicle(rs.getString("LEGAL_VEHICLE").trim());
				if(ValidationUtil.isValid(rs.getString("LegalVehicleDesc")))
					visionUsersVb.setLegalVehicleDesc(rs.getString("LegalVehicleDesc").trim());
				if(ValidationUtil.isValid(rs.getString("PRODUCT_ATTRIBUTE")))
					visionUsersVb.setProductAttribute(rs.getString("PRODUCT_ATTRIBUTE").trim());
				if(ValidationUtil.isValid(rs.getString("SBU_CODE")))
					visionUsersVb.setSbuCode(rs.getString("SBU_CODE").trim());
				visionUsersVb.setSbuCodeAt(rs.getInt("SBU_CODE_AT"));
				if(ValidationUtil.isValid(rs.getString("OUC_ATTRIBUTE")))
					visionUsersVb.setOucAttribute(rs.getString("OUC_ATTRIBUTE").trim());
				if(ValidationUtil.isValid(rs.getString("PRODUCT_SUPER_GROUP")))
					visionUsersVb.setProductSuperGroup(rs.getString("PRODUCT_SUPER_GROUP").trim());
				if(ValidationUtil.isValid(rs.getString("BUSINESS_GROUP")))
					visionUsersVb.setBusinessGroup(rs.getString("BUSINESS_GROUP").trim());
				if(ValidationUtil.isValid(rs.getString("ACCOUNT_OFFICER")))
					visionUsersVb.setAccountOfficer(rs.getString("ACCOUNT_OFFICER").trim());
				if(ValidationUtil.isValid(rs.getString("REGION_PROVINCE")))
					visionUsersVb.setRegionProvince(rs.getString("REGION_PROVINCE").trim());
				if(ValidationUtil.isValid(rs.getString("GCID_ACCESS"))){
					visionUsersVb.setGcidAccess(rs.getString("GCID_ACCESS").trim());
				}
				visionUsersVb.setRecordIndicatorNt(rs.getInt("RECORD_INDICATOR_NT"));
				visionUsersVb.setRecordIndicator(rs.getInt("RECORD_INDICATOR"));
				visionUsersVb.setRecordIndicatorDesc(rs.getString("RECORD_INDICATOR_DESC"));
				visionUsersVb.setMaker(rs.getLong("MAKER"));
				visionUsersVb.setMakerName(rs.getString("MAKER_NAME"));
				visionUsersVb.setVerifier(rs.getLong("VERIFIER"));
				visionUsersVb.setVerifierName(rs.getString("VERIFIER_NAME"));
				visionUsersVb.setInternalStatus(rs.getInt("INTERNAL_STATUS"));
				if(ValidationUtil.isValid(rs.getString("DATE_CREATION")))
					visionUsersVb.setDateCreation(rs.getString("DATE_CREATION"));
				if(ValidationUtil.isValid(rs.getString("DATE_LAST_MODIFIED")))
					visionUsersVb.setDateLastModified(rs.getString("DATE_LAST_MODIFIED"));
				if(ValidationUtil.isValid(rs.getString("LAST_UNSUCCESSFUL_LOGIN_DATE")))
					visionUsersVb.setLastUnsuccessfulLoginDate(rs.getString("LAST_UNSUCCESSFUL_LOGIN_DATE"));
				if(ValidationUtil.isValid(rs.getString("UNSUCCESSFUL_LOGIN_ATTEMPTS")))
					visionUsersVb.setLastUnsuccessfulLoginAttempts(rs.getString("UNSUCCESSFUL_LOGIN_ATTEMPTS"));
				if(ValidationUtil.isValid(rs.getString("ENABLE_WIDGETS")))
					visionUsersVb.setEnableWidgets(rs.getString("ENABLE_WIDGETS"));
				return visionUsersVb;
			}
		};
		return mapper;
	}

	private RowMapper getMapper1() {
		RowMapper mapper = new RowMapper() {
			@Override
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				VisionUsersVb visionUsersVb = new VisionUsersVb();
				visionUsersVb.setVisionId(rs.getInt("VISION_ID"));
				if (ValidationUtil.isValid(rs.getString("USER_NAME")))
					visionUsersVb.setUserName(rs.getString("USER_NAME"));
				if (ValidationUtil.isValid(rs.getString("USER_LOGIN_ID")))
					visionUsersVb.setUserLoginId(rs.getString("USER_LOGIN_ID"));
				if (ValidationUtil.isValid(rs.getString("USER_EMAIL_ID")))
					visionUsersVb.setUserEmailId(rs.getString("USER_EMAIL_ID"));
				if (ValidationUtil.isValid(rs.getString("USER_STATUS_DATE")))
					visionUsersVb.setUserStatusDate(rs.getString("USER_STATUS_DATE"));
				visionUsersVb.setUserStatusNt(rs.getInt("USER_STATUS_NT"));
				visionUsersVb.setUserStatus(rs.getInt("USER_STATUS"));
				visionUsersVb.setUserProfileAt(rs.getInt("USER_PROFILE_AT"));
				if (ValidationUtil.isValid(rs.getString("USER_PROFILE")))
					visionUsersVb.setUserProfile(rs.getString("USER_PROFILE").trim());
				visionUsersVb.setUserGroupAt(rs.getInt("USER_GROUP_AT"));
				if (ValidationUtil.isValid(rs.getString("USER_GROUP")))
					visionUsersVb.setUserGroup(rs.getString("USER_GROUP").trim());
				if (ValidationUtil.isValid(rs.getString("LAST_ACTIVITY_DATE")))
					visionUsersVb.setLastActivityDate(rs.getString("LAST_ACTIVITY_DATE").trim());
				if (ValidationUtil.isValid(rs.getString("UPDATE_RESTRICTION")))
					visionUsersVb.setUpdateRestriction(rs.getString("UPDATE_RESTRICTION").trim());
				if (ValidationUtil.isValid(rs.getString("LE_BOOK")))
					visionUsersVb.setLeBook(rs.getString("LE_BOOK").trim());
				if (ValidationUtil.isValid(rs.getString("COUNTRY")))
					visionUsersVb.setCountry(rs.getString("COUNTRY").trim());
				if (ValidationUtil.isValid(rs.getString("LEGAL_VEHICLE")))
					visionUsersVb.setLegalVehicle(rs.getString("LEGAL_VEHICLE").trim());
				if (ValidationUtil.isValid(rs.getString("LegalVehicleDesc")))
					visionUsersVb.setLegalVehicleDesc(rs.getString("LegalVehicleDesc").trim());
				if (ValidationUtil.isValid(rs.getString("PRODUCT_ATTRIBUTE")))
					visionUsersVb.setProductAttribute(rs.getString("PRODUCT_ATTRIBUTE").trim());
				if (ValidationUtil.isValid(rs.getString("SBU_CODE")))
					visionUsersVb.setSbuCode(rs.getString("SBU_CODE").trim());
				visionUsersVb.setSbuCodeAt(rs.getInt("SBU_CODE_AT"));
				if (ValidationUtil.isValid(rs.getString("OUC_ATTRIBUTE")))
					visionUsersVb.setOucAttribute(rs.getString("OUC_ATTRIBUTE").trim());
				if (ValidationUtil.isValid(rs.getString("PRODUCT_SUPER_GROUP")))
					visionUsersVb.setProductSuperGroup(rs.getString("PRODUCT_SUPER_GROUP").trim());
				if (ValidationUtil.isValid(rs.getString("BUSINESS_GROUP")))
					visionUsersVb.setBusinessGroup(rs.getString("BUSINESS_GROUP").trim());
				if (ValidationUtil.isValid(rs.getString("ACCOUNT_OFFICER")))
					visionUsersVb.setAccountOfficer(rs.getString("ACCOUNT_OFFICER").trim());
				if (ValidationUtil.isValid(rs.getString("REGION_PROVINCE")))
					visionUsersVb.setRegionProvince(rs.getString("REGION_PROVINCE").trim());
				if (ValidationUtil.isValid(rs.getString("GCID_ACCESS"))) {
					visionUsersVb.setGcidAccess(rs.getString("GCID_ACCESS").trim());
				}
				visionUsersVb.setRecordIndicatorNt(rs.getInt("RECORD_INDICATOR_NT"));
				visionUsersVb.setRecordIndicator(rs.getInt("RECORD_INDICATOR"));
				visionUsersVb.setMaker(rs.getLong("MAKER"));
				visionUsersVb.setVerifier(rs.getLong("VERIFIER"));
				visionUsersVb.setInternalStatus(rs.getInt("INTERNAL_STATUS"));
				if (ValidationUtil.isValid(rs.getString("DATE_CREATION")))
					visionUsersVb.setDateCreation(rs.getString("DATE_CREATION"));
				if (ValidationUtil.isValid(rs.getString("DATE_LAST_MODIFIED")))
					visionUsersVb.setDateLastModified(rs.getString("DATE_LAST_MODIFIED"));
				if (ValidationUtil.isValid(rs.getString("LAST_UNSUCCESSFUL_LOGIN_DATE")))
					visionUsersVb.setLastUnsuccessfulLoginDate(rs.getString("LAST_UNSUCCESSFUL_LOGIN_DATE"));
				if (ValidationUtil.isValid(rs.getString("UNSUCCESSFUL_LOGIN_ATTEMPTS")))
					visionUsersVb.setLastUnsuccessfulLoginAttempts(rs.getString("UNSUCCESSFUL_LOGIN_ATTEMPTS"));
				if (ValidationUtil.isValid(rs.getString("FILE_NAME"))) {
					visionUsersVb.setFileNmae(rs.getString("FILE_NAME"));
					if (rs.getBlob("USER_PHOTO") != null) {
						createImage(visionUsersVb.getFileNmae(), rs);
					}
				}
				if (ValidationUtil.isValid(rs.getString("ENABLE_WIDGETS")))
					visionUsersVb.setEnableWidgets(rs.getString("ENABLE_WIDGETS"));
				if (ValidationUtil.isValid(rs.getString("APP_THEME"))) {
					visionUsersVb.setAppTheme(rs.getString("APP_THEME"));
				}else {
					visionUsersVb.setAppTheme("BLUE");
				}
				if (ValidationUtil.isValid(rs.getString("Report_Slide_Theme"))) {
					visionUsersVb.setReportSliderTheme(rs.getString("Report_Slide_Theme"));
				}else {
					visionUsersVb.setReportSliderTheme("BLUE");
				}
				if (ValidationUtil.isValid(rs.getString("Language"))) {
					visionUsersVb.setLanguage(rs.getString("Language"));
				}else {
					visionUsersVb.setLanguage("EN");
				}
				return visionUsersVb;
			}

		};
		return mapper;
	}
	private RowMapper getQueryResultsMapper(){
		RowMapper mapper = new RowMapper() {
			@Override
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				VisionUsersVb visionUsersVb = new VisionUsersVb();
				visionUsersVb.setVisionId(rs.getInt("VISION_ID"));
				if(ValidationUtil.isValid(rs.getString("USER_NAME")))
					visionUsersVb.setUserName(rs.getString("USER_NAME"));
				if(ValidationUtil.isValid(rs.getString("USER_LOGIN_ID")))				
					visionUsersVb.setUserLoginId(rs.getString("USER_LOGIN_ID"));
				if(ValidationUtil.isValid(rs.getString("STF_LE_BOOK")))
					visionUsersVb.setStfleBook(rs.getString("STF_LE_BOOK").trim());
				if(ValidationUtil.isValid(rs.getString("STF_COUNTRY")))
					visionUsersVb.setStfcountry(rs.getString("STF_COUNTRY").trim());
				if(ValidationUtil.isValid(rs.getString("STAFF_ID")))
					visionUsersVb.setStaffId(rs.getString("STAFF_ID"));
				if(ValidationUtil.isValid(rs.getString("STAFF_NAME")))
					visionUsersVb.setStaffName(rs.getString("STAFF_NAME"));
				if(ValidationUtil.isValid(rs.getString("USER_EMAIL_ID")))
					visionUsersVb.setUserEmailId(rs.getString("USER_EMAIL_ID"));
				if(ValidationUtil.isValid(rs.getString("USER_STATUS_DATE")))
					visionUsersVb.setUserStatusDate(rs.getString("USER_STATUS_DATE"));
				visionUsersVb.setUserStatusNt(rs.getInt("USER_STATUS_NT"));
				visionUsersVb.setUserStatus(rs.getInt("USER_STATUS"));
				visionUsersVb.setUserProfileAt(rs.getInt("USER_PROFILE_AT"));
				if(ValidationUtil.isValid(rs.getString("USER_PROFILE")))
					visionUsersVb.setUserProfile(rs.getString("USER_PROFILE").trim());
				visionUsersVb.setUserGroupAt(rs.getInt("USER_GROUP_AT"));
				if(ValidationUtil.isValid(rs.getString("USER_GROUP")))
					visionUsersVb.setUserGroup(rs.getString("USER_GROUP").trim());
				if(ValidationUtil.isValid(rs.getString("LAST_ACTIVITY_DATE")))
					visionUsersVb.setLastActivityDate(rs.getString("LAST_ACTIVITY_DATE").trim());
				if(ValidationUtil.isValid(rs.getString("UPDATE_RESTRICTION")))
					visionUsersVb.setUpdateRestriction(rs.getString("UPDATE_RESTRICTION").trim());
				if(ValidationUtil.isValid(rs.getString("LE_BOOK")))
					visionUsersVb.setLeBook(rs.getString("LE_BOOK").trim());
				if(ValidationUtil.isValid(rs.getString("COUNTRY")))
					visionUsersVb.setCountry(rs.getString("COUNTRY").trim());
				if(ValidationUtil.isValid(rs.getString("LEGAL_VEHICLE")))
					visionUsersVb.setLegalVehicle(rs.getString("LEGAL_VEHICLE").trim());
				if(ValidationUtil.isValid(rs.getString("LegalVehicleDesc")))
					visionUsersVb.setLegalVehicleDesc(rs.getString("LegalVehicleDesc").trim());
				if(ValidationUtil.isValid(rs.getString("PRODUCT_ATTRIBUTE")))
					visionUsersVb.setProductAttribute(rs.getString("PRODUCT_ATTRIBUTE").trim());
				if(ValidationUtil.isValid(rs.getString("SBU_CODE")))
					visionUsersVb.setSbuCode(rs.getString("SBU_CODE").trim());
				visionUsersVb.setSbuCodeAt(rs.getInt("SBU_CODE_AT"));
				if(ValidationUtil.isValid(rs.getString("OUC_ATTRIBUTE")))
					visionUsersVb.setOucAttribute(rs.getString("OUC_ATTRIBUTE").trim());
				if(ValidationUtil.isValid(rs.getString("PRODUCT_SUPER_GROUP")))
					visionUsersVb.setProductSuperGroup(rs.getString("PRODUCT_SUPER_GROUP").trim());
				if(ValidationUtil.isValid(rs.getString("BUSINESS_GROUP")))
					visionUsersVb.setBusinessGroup(rs.getString("BUSINESS_GROUP").trim());
				if(ValidationUtil.isValid(rs.getString("ACCOUNT_OFFICER")))
					visionUsersVb.setAccountOfficer(rs.getString("ACCOUNT_OFFICER").trim());
				if(ValidationUtil.isValid(rs.getString("REGION_PROVINCE")))
					visionUsersVb.setRegionProvince(rs.getString("REGION_PROVINCE").trim());
				if(ValidationUtil.isValid(rs.getString("GCID_ACCESS"))){
					visionUsersVb.setGcidAccess(rs.getString("GCID_ACCESS").trim());
				}
				visionUsersVb.setRecordIndicatorNt(rs.getInt("RECORD_INDICATOR_NT"));
				visionUsersVb.setRecordIndicator(rs.getInt("RECORD_INDICATOR"));
				visionUsersVb.setRecordIndicatorDesc(rs.getString("RECORD_INDICATOR_DESC"));
				visionUsersVb.setMaker(rs.getLong("MAKER"));
				visionUsersVb.setMakerName(rs.getString("MAKER_NAME"));
				visionUsersVb.setVerifier(rs.getLong("VERIFIER"));
				visionUsersVb.setVerifierName(rs.getString("VERIFIER_NAME"));
				visionUsersVb.setInternalStatus(rs.getInt("INTERNAL_STATUS"));
				if(ValidationUtil.isValid(rs.getString("DATE_CREATION")))
					visionUsersVb.setDateCreation(rs.getString("DATE_CREATION"));
				if(ValidationUtil.isValid(rs.getString("DATE_LAST_MODIFIED")))
					visionUsersVb.setDateLastModified(rs.getString("DATE_LAST_MODIFIED"));
				if(ValidationUtil.isValid(rs.getString("LAST_UNSUCCESSFUL_LOGIN_DATE")))
					visionUsersVb.setLastUnsuccessfulLoginDate(rs.getString("LAST_UNSUCCESSFUL_LOGIN_DATE"));
				if(ValidationUtil.isValid(rs.getString("UNSUCCESSFUL_LOGIN_ATTEMPTS")))
					visionUsersVb.setLastUnsuccessfulLoginAttempts(rs.getString("UNSUCCESSFUL_LOGIN_ATTEMPTS"));
				if(ValidationUtil.isValid(rs.getString("FILE_NAME"))){
					visionUsersVb.setFileNmae(rs.getString("FILE_NAME"));
					if(rs.getBlob("USER_PHOTO") !=null){
						createImage(visionUsersVb.getFileNmae(), rs);
					}
				}
				if(ValidationUtil.isValid(rs.getString("ENABLE_WIDGETS")))
					visionUsersVb.setEnableWidgets(rs.getString("ENABLE_WIDGETS"));
				if(ValidationUtil.isValid(rs.getString("APPLICATION_ACCESS")))
					visionUsersVb.setApplicationAccess(rs.getString("APPLICATION_ACCESS"));
				if(ValidationUtil.isValid(rs.getString("RA_SOC")))
					visionUsersVb.setRaSoc(rs.getString("RA_SOC"));
				if(ValidationUtil.isValid(rs.getString("OTHER_ATTR")))
					visionUsersVb.setOtherAttr(rs.getString("OTHER_ATTR"));
				if(ValidationUtil.isValid(rs.getString("PWD_RESET_FLAG")))
					visionUsersVb.setPasswordResetFlag(rs.getString("PWD_RESET_FLAG"));
				
				return visionUsersVb;
			}
			
		};
		return mapper;
	}
	private RowMapper getPasswordApprMapper() {
		RowMapper mapper = new RowMapper() {
			@Override
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				VisionUsersVb visionUsersVb = new VisionUsersVb();
				if(ValidationUtil.isValid(rs.getString("PWD_RESET_FLAG")))
					visionUsersVb.setPasswordResetFlag(rs.getString("PWD_RESET_FLAG"));
				if(ValidationUtil.isValid(rs.getString("PASSWORD")))
					visionUsersVb.setPassword(rs.getString("PASSWORD"));
				return visionUsersVb;
			}

		};
		return mapper;
	}
	private RowMapper<AlphaSubTabVb> getMapperRestriction() {
		RowMapper<AlphaSubTabVb> mapper = new RowMapper<AlphaSubTabVb>() {
			@Override
			public AlphaSubTabVb mapRow(ResultSet rs, int rowNum) throws SQLException {
				AlphaSubTabVb alphaSubTabVb = new AlphaSubTabVb();
				alphaSubTabVb.setAlphaSubTab(rs.getString("RESTRICTION"));
				return alphaSubTabVb;
			}
		};
		return mapper;
	}

	private void createImage(String fileName, ResultSet rs) {
		try{
			String filePath = commonDao.findVisionVariableValue("RA_IMAGE_PATH");
		    Blob ph = rs.getBlob("USER_PHOTO");
		    InputStream in = ph.getBinaryStream();
		    ByteArrayOutputStream out = new ByteArrayOutputStream();  
		    OutputStream outputStream = new FileOutputStream(filePath+"\\"+fileName);  
		    int length = (int) ph.length();  
		    int bufferSize = 1024;  
		    byte[] buffer = new byte[bufferSize];  
		    while ((length = in.read(buffer)) != -1) {  
		        out.write(buffer, 0, length);     
		    }
		    out.writeTo(outputStream);
		    outputStream.flush();
		    out.flush();
		    in.close();
		}catch(Exception ex){
			logger.error("Create User Profile Image Errror : "+ex.getMessage());
		}
	}

	@Override
	public List<VisionUsersVb> getQueryPopupResults(VisionUsersVb dObj) {
		Vector<Object> params = new Vector<Object>();
		StringBuffer strBufApprove = null;
		String strWhereNotExists = null;
		StringBuffer strBufPending = null;

		strBufApprove = new StringBuffer("Select * from ( Select TAppr.VISION_ID,"
					+ "TAppr.USER_NAME, TAppr.USER_LOGIN_ID, TAppr.USER_EMAIL_ID,TAppr.STF_COUNTRY,TAppr.STF_LE_BOOK,TAppr.STAFF_ID,"
				 	+ getDbFunction("DATEFUNC")+"(TAppr.LAST_ACTIVITY_DATE, '"+getDbFunction("DD_Mon_RRRR")+" "+getDbFunction("TIME")+"') LAST_ACTIVITY_DATE, TAppr.USER_GROUP_AT,"
					+ "TAppr.USER_GROUP, TAppr.USER_PROFILE_AT, TAppr.USER_PROFILE,"
					+ "TAppr.UPDATE_RESTRICTION, TAppr.LEGAL_VEHICLE, (Select LV_DESCRIPTION From LEGAL_VEHICLES Where LEGAL_VEHICLE = TAppr.LEGAL_VEHICLE) AS LegalVehicleDesc,"
					+ "TAppr.COUNTRY, TAppr.LE_BOOK,"
					+ "TAppr.REGION_PROVINCE, TAppr.BUSINESS_GROUP, TAppr.PRODUCT_SUPER_GROUP,"
					+ "TAppr.OUC_ATTRIBUTE, TAppr.SBU_CODE, TAppr.SBU_CODE_AT, TAppr.PRODUCT_ATTRIBUTE,"
					+ "TAppr.ACCOUNT_OFFICER, TAppr.GCID_ACCESS, TAppr.USER_STATUS_NT, TAppr.USER_STATUS,"
					+ "T2.NUM_SUBTAB_DESCRIPTION USER_STATUS_DESC,"
					+ getDbFunction("DATEFUNC")+"(TAppr.USER_STATUS_DATE, '"+getDbFunction("DD_Mon_RRRR")+" "+getDbFunction("TIME")+"') USER_STATUS_DATE, TAppr.MAKER,"
					+ "(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = " + getDbFunction("NVL")
					+ "(TAppr.MAKER,0) ) MAKER_NAME, "
					+ "TAppr.VERIFIER, (SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "
					+ getDbFunction("NVL") + "(TAppr.VERIFIER,0) ) VERIFIER_NAME,"
					+ "TAppr.INTERNAL_STATUS, TAppr.RECORD_INDICATOR_NT,"
					+ "TAppr.RECORD_INDICATOR,T1.NUM_SUBTAB_DESCRIPTION RECORD_INDICATOR_DESC,"
					+ getDbFunction("DATEFUNC")+"(TAppr.DATE_LAST_MODIFIED, '"+getDbFunction("DD_Mon_RRRR")+" "+getDbFunction("TIME")+"') DATE_LAST_MODIFIED,TAppr.DATE_LAST_MODIFIED DATE_LAST_MODIFIED_1,"
					+ getDbFunction("DATEFUNC")+"(TAppr.DATE_CREATION, '"+getDbFunction("DD_Mon_RRRR")+" "+getDbFunction("TIME")+"') DATE_CREATION,"
					+ getDbFunction("DATEFUNC")+"(TAppr.LAST_UNSUCCESSFUL_LOGIN_DATE, '"+getDbFunction("DD_Mon_RRRR")+" "+getDbFunction("TIME")+"') LAST_UNSUCCESSFUL_LOGIN_DATE, TAppr.UNSUCCESSFUL_LOGIN_ATTEMPTS,  TAppr.ENABLE_WIDGETS "
					+ " From VISION_USERS TAppr,NUM_SUB_TAB T1,NUM_SUB_TAB T2"
					+ " where  T1.NUM_tab = TAppr.RECORD_INDICATOR_NT   "
					+ " and T1.NUM_sub_tab = TAppr.RECORD_INDICATOR  " + "and T2.NUM_tab = TAppr.USER_STATUS_NT "
					+ " and T2.NUM_sub_tab = TAppr.USER_STATUS  " + ")TAPPR");
		strWhereNotExists = new String(
				" Not Exists (Select 'X' From VISION_USERS_PEND TPend Where TPend.VISION_ID = TAppr.VISION_ID )");
		strBufPending = new StringBuffer("Select * from ( Select TPend.VISION_ID,"
				+ "TPend.USER_NAME, TPend.USER_LOGIN_ID, TPend.USER_EMAIL_ID,TPend.STF_COUNTRY,TPend.STF_LE_BOOK,TPend.STAFF_ID,"
				+ getDbFunction("DATEFUNC")+"(TPend.LAST_ACTIVITY_DATE, '"+getDbFunction("DD_Mon_RRRR")+" "+getDbFunction("TIME")+"') LAST_ACTIVITY_DATE, TPend.USER_GROUP_AT,"
				+ "TPend.USER_GROUP, TPend.USER_PROFILE_AT, TPend.USER_PROFILE,"
				+ "TPend.UPDATE_RESTRICTION, TPend.LEGAL_VEHICLE,(Select LV_DESCRIPTION From LEGAL_VEHICLES Where LEGAL_VEHICLE = TPend.LEGAL_VEHICLE) AS LegalVehicleDesc,"
				+ "TPend.COUNTRY, TPend.LE_BOOK,"
				+ "TPend.REGION_PROVINCE, TPend.BUSINESS_GROUP, TPend.PRODUCT_SUPER_GROUP,"
				+ "TPend.OUC_ATTRIBUTE, TPend.SBU_CODE, TPend.SBU_CODE_AT, TPend.PRODUCT_ATTRIBUTE,"
				+ "TPend.ACCOUNT_OFFICER, TPend.GCID_ACCESS, TPend.USER_STATUS_NT, TPend.USER_STATUS,"
				+ "T2.NUM_SUBTAB_DESCRIPTION USER_STATUS_DESC,"
				+ getDbFunction("DATEFUNC")+"(TPend.USER_STATUS_DATE, '"+getDbFunction("DD_Mon_RRRR")+" "+getDbFunction("TIME")+"') USER_STATUS_DATE, TPend.MAKER,"
				+ "(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = " + getDbFunction("NVL")
				+ "(TPend.MAKER,0) ) MAKER_NAME, "
				+ "TPend.VERIFIER, (SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "
				+ getDbFunction("NVL") + "(TPend.VERIFIER,0) ) VERIFIER_NAME,"
				+ "TPend.INTERNAL_STATUS, TPend.RECORD_INDICATOR_NT,"
				+ "TPend.RECORD_INDICATOR, T1.NUM_SUBTAB_DESCRIPTION RECORD_INDICATOR_DESC,"
				+ getDbFunction("DATEFUNC")+"(TPend.DATE_LAST_MODIFIED, '"+getDbFunction("DD_Mon_RRRR")+" "+getDbFunction("TIME")+"') DATE_LAST_MODIFIED,TPend.DATE_LAST_MODIFIED DATE_LAST_MODIFIED_1,"
				+ getDbFunction("DATEFUNC")+"(TPend.DATE_CREATION, '"+getDbFunction("DD_Mon_RRRR")+" "+getDbFunction("TIME")+"') DATE_CREATION, "
				+ getDbFunction("DATEFUNC")+"(TPend.LAST_UNSUCCESSFUL_LOGIN_DATE, '"+getDbFunction("DD_Mon_RRRR")+" "+getDbFunction("TIME")+"') LAST_UNSUCCESSFUL_LOGIN_DATE, TPend.UNSUCCESSFUL_LOGIN_ATTEMPTS,  TPend.ENABLE_WIDGETS "
				+ " From VISION_USERS_PEND TPend, NUM_SUB_TAB T1,NUM_SUB_TAB T2 "
				+ " where  t1.NUM_tab = TPend.RECORD_INDICATOR_NT   "
				+ " and t1.NUM_sub_tab = TPend.RECORD_INDICATOR  " + "and t2.NUM_tab = TPend.USER_STATUS_NT "
				+ " and t2.NUM_sub_tab = TPend.USER_STATUS  " + ")TPend");
		try {
			if (dObj.getSmartSearchOpt() != null && dObj.getSmartSearchOpt().size() > 0) {
				int count = 1;
				for (SmartSearchVb data: dObj.getSmartSearchOpt()){
					if(count == dObj.getSmartSearchOpt().size()) {
						data.setJoinType("");
					} else {
						if(!ValidationUtil.isValid(data.getJoinType()) && !("AND".equalsIgnoreCase(data.getJoinType()) || "OR".equalsIgnoreCase(data.getJoinType()))) {
							data.setJoinType("AND");
						}
					}
					String val = CommonUtils.criteriaBasedVal(data.getCriteria(), data.getValue());
					switch (data.getObject()) {
					case "visionId":
						CommonUtils.addToQuerySearch(" upper(TAPPR.VISION_ID) "+ val, strBufApprove, data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPEND.VISION_ID) "+ val, strBufPending, data.getJoinType());
						break;

					case "userLoginId":
						CommonUtils.addToQuerySearch(" upper(TAPPR.USER_LOGIN_ID) "+ val, strBufApprove, data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPEND.USER_LOGIN_ID) "+ val, strBufPending, data.getJoinType());
						break;
						
					case "userName":
						CommonUtils.addToQuerySearch(" upper(TAPPR.USER_NAME) "+ val, strBufApprove, data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPEND.USER_NAME) "+ val, strBufPending, data.getJoinType());
						break;

					case "userGroup":
						CommonUtils.addToQuerySearch(" upper(TAPPR.USER_GROUP) "+ val, strBufApprove, data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPEND.USER_GROUP) "+ val, strBufPending, data.getJoinType());
						break;
						
					case "userProfile":
						CommonUtils.addToQuerySearch(" upper(TAPPR.USER_PROFILE) "+ val, strBufApprove, data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPEND.USER_PROFILE) "+ val, strBufPending, data.getJoinType());
						break;
						
					case "userStatusDesc":
						CommonUtils.addToQuerySearch(" upper(TAPPR.USER_STATUS_DESC) "+ val, strBufApprove, data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPEND.USER_STATUS_DESC) "+ val, strBufPending, data.getJoinType());
						break;
					
					case "recordIndicatorDesc":
						CommonUtils.addToQuerySearch(" upper(TAPPR.RECORD_INDICATOR_DESC) "+ val, strBufApprove, data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPEND.RECORD_INDICATOR_DESC) "+ val, strBufPending, data.getJoinType());
						break;
					default:
					}
					count++;
				}
			}
				
		} catch (Exception ex) {
			ex.printStackTrace();
			/*
			 * logger.error(((strBufApprove == null) ? "strBufApprove is Null" :
			 * strBufApprove.toString())); logger.error("UNION");
			 * logger.error(((strBufPending == null) ? "strBufPending is Null" :
			 * strBufPending.toString()));
			 * 
			 * if (params != null) for (int i = 0; i < params.size(); i++)
			 * logger.error("objParams[" + i + "]" + params.get(i).toString());
			 */
			return null;
		}
		String orderBy = " Order By DATE_LAST_MODIFIED_1 desc ";
		return getQueryPopupResults(dObj, strBufPending, strBufApprove, strWhereNotExists, orderBy, params);

	}

	public boolean updateActivityDateByUserLoginId(VisionUsersVb visionUsersVb) {
		Object[] params = new Object[1];
		params[0] = visionUsersVb.getVisionId();
		int count = 0;
		String sql = "";
		sql = "Update VISION_USERS SET LAST_ACTIVITY_DATE = "+commonDao.getDbFunction("SYSDATE")+","
				+ "LAST_UNSUCCESSFUL_LOGIN_DATE = null, UNSUCCESSFUL_LOGIN_ATTEMPTS = null  WHERE VISION_ID = ?";
		count = getJdbcTemplate().update(sql, params);
		return count == 1;

	}

	public void updateUnsuccessfulLoginAttempts(String userId) {
		Object[] params = new Object[1];
		params[0] = userId.toUpperCase();
		String sql = "";
		sql = "Update VISION_USERS SET LAST_UNSUCCESSFUL_LOGIN_DATE = "+commonDao.getDbFunction("SYSDATE")+","
				+ "UNSUCCESSFUL_LOGIN_ATTEMPTS = "+commonDao.getDbFunction("NVL")+"(UNSUCCESSFUL_LOGIN_ATTEMPTS,(UNSUCCESSFUL_LOGIN_ATTEMPTS+1),1) WHERE UPPER(USER_LOGIN_ID) = ?";
		getJdbcTemplate().update(sql, params);

	}

	/* Convert to SQL Query */
	public List<VisionUsersVb> getActiveUserByUserLoginId(VisionUsersVb dObj) {
		StringBuffer strQueryAppr = null;
		Object objParams[] = new Object[4];
		if ("ORACLE".equalsIgnoreCase(databaseType)) {
			strQueryAppr = new StringBuffer("Select TAppr.VISION_ID,"
					+ " TAppr.USER_NAME, TAppr.USER_LOGIN_ID, TAppr.USER_EMAIL_ID,"
					+ " To_Char(TAppr.LAST_ACTIVITY_DATE, ''"+getDbFunction("DD_Mon_RRRR")+" "+getDbFunction("TIME")+"') LAST_ACTIVITY_DATE, TAppr.USER_GROUP_AT,"
					+ " TAppr.USER_GROUP, TAppr.USER_PROFILE_AT, TAppr.USER_PROFILE,"
					+ " TAppr.UPDATE_RESTRICTION, TAppr.LEGAL_VEHICLE, (Select LV_DESCRIPTION From LEGAL_VEHICLES Where LEGAL_VEHICLE = TAppr.LEGAL_VEHICLE) AS LegalVehicleDesc,"
					+ " TAppr.COUNTRY, TAppr.LE_BOOK,"
					+ " TAppr.REGION_PROVINCE, TAppr.BUSINESS_GROUP, TAppr.PRODUCT_SUPER_GROUP,"
					+ " TAppr.OUC_ATTRIBUTE, TAppr.SBU_CODE, TAppr.SBU_CODE_AT, TAppr.PRODUCT_ATTRIBUTE,"
					+ " TAppr.ACCOUNT_OFFICER, TAppr.GCID_ACCESS, TAppr.USER_STATUS_NT, TAppr.USER_STATUS,"
					+ " To_Char(TAppr.USER_STATUS_DATE, '"+getDbFunction("DD_Mon_RRRR")+" "+getDbFunction("TIME")+"') USER_STATUS_DATE, TAppr.MAKER,"
					+ " TAppr.VERIFIER, TAppr.INTERNAL_STATUS, TAppr.RECORD_INDICATOR_NT,"
					+ " TAppr.RECORD_INDICATOR, To_Char(TAppr.DATE_LAST_MODIFIED, '"+getDbFunction("DD_Mon_RRRR")+" "+getDbFunction("TIME")+"') DATE_LAST_MODIFIED,"
					+ " To_Char(TAppr.DATE_CREATION, '"+getDbFunction("DD_Mon_RRRR")+" "+getDbFunction("TIME")+"') DATE_CREATION,"
					+ " To_Char(TAppr.LAST_UNSUCCESSFUL_LOGIN_DATE, '"+getDbFunction("DD_Mon_RRRR")+" "+getDbFunction("TIME")+"') LAST_UNSUCCESSFUL_LOGIN_DATE, TAppr.UNSUCCESSFUL_LOGIN_ATTEMPTS, TAppr.FILE_NAME, TAppr.USER_PHOTO, "
					+ " TAppr.ENABLE_WIDGETS, (SELECT App_Theme FROM PRD_APP_THEME S1 WHERE S1.VISION_ID=TAppr.VISION_ID AND APPLICATION_ID= ?) APP_THEME,  "
					+ " (SELECT Report_Slide_Theme FROM PRD_APP_THEME S1 WHERE S1.VISION_ID=TAppr.VISION_ID AND APPLICATION_ID = ?) Report_Slide_Theme, "
					+ " (SELECT Language FROM PRD_APP_THEME S1 WHERE S1.VISION_ID=TAppr.VISION_ID AND APPLICATION_ID = ?) Language "
					+ " From VISION_USERS TAppr WHERE USER_STATUS = 0 AND RECORD_INDICATOR = 0 AND UPPER(USER_LOGIN_ID) =UPPER(?) AND (UNSUCCESSFUL_LOGIN_ATTEMPTS is NULL OR UNSUCCESSFUL_LOGIN_ATTEMPTS <= 3)");
		} else if ("MSSQL".equalsIgnoreCase(databaseType)) {
			strQueryAppr = new StringBuffer("SELECT TAppr.VISION_ID,TAppr.USER_NAME,"
					+ " TAppr.USER_LOGIN_ID,  TAppr.USER_EMAIL_ID,"
					+ " FORMAT(LAST_ACTIVITY_DATE,'"+getDbFunction("DD_Mon_RRRR")+" "+getDbFunction("TIME")+"') LAST_ACTIVITY_DATE,"
					+ " TAppr.USER_GROUP_AT,  TAppr.USER_GROUP,  TAppr.USER_PROFILE_AT,"
					+ " TAppr.USER_PROFILE,  TAppr.UPDATE_RESTRICTION,  TAppr.LEGAL_VEHICLE,"
					+ " (SELECT LV_DESCRIPTION FROM LEGAL_VEHICLES WHERE LEGAL_VEHICLE = TAppr.LEGAL_VEHICLE) AS LegalVehicleDesc,"
					+ " TAppr.COUNTRY,  TAppr.LE_BOOK,  TAppr.REGION_PROVINCE,"
					+ " TAppr.BUSINESS_GROUP,  TAppr.PRODUCT_SUPER_GROUP,"
					+ " TAppr.OUC_ATTRIBUTE,  TAppr.SBU_CODE,  TAppr.SBU_CODE_AT,"
					+ " TAppr.PRODUCT_ATTRIBUTE,  TAppr.ACCOUNT_OFFICER,  TAppr.GCID_ACCESS,"
					+ " TAppr.USER_STATUS_NT,  TAppr.USER_STATUS,"
					+ " FORMAT(TAppr.USER_STATUS_DATE,'"+getDbFunction("DD_Mon_RRRR")+" "+getDbFunction("TIME")+"') USER_STATUS_DATE,"
					+ " TAppr.MAKER,  TAppr.VERIFIER,  TAppr.INTERNAL_STATUS,"
					+ " TAppr.RECORD_INDICATOR_NT,  TAppr.RECORD_INDICATOR,"
					+ " FORMAT(TAppr.DATE_LAST_MODIFIED,'dd-MM-yyyy HH:mm:ss') DATE_LAST_MODIFIED,"
					+ " FORMAT(TAppr.DATE_CREATION,'"+getDbFunction("DD_Mon_RRRR")+" "+getDbFunction("TIME")+"') DATE_CREATION,"
					+ " FORMAT(TAppr.LAST_UNSUCCESSFUL_LOGIN_DATE,'dd-MM-yyyy HH:mm:ss') LAST_UNSUCCESSFUL_LOGIN_DATE,"
					+ " TAppr.UNSUCCESSFUL_LOGIN_ATTEMPTS,  TAppr.FILE_NAME,"
					+ " TAppr.USER_PHOTO,  TAppr.ENABLE_WIDGETS,(SELECT App_Theme FROM PRD_APP_THEME S1 WHERE S1.VISION_ID=TAppr.VISION_ID AND APPLICATION_ID= ?) APP_THEME,  "
					+ " (SELECT Report_Slide_Theme FROM PRD_APP_THEME S1 WHERE S1.VISION_ID=TAppr.VISION_ID AND APPLICATION_ID = ?) Report_Slide_Theme, "
					+ " (SELECT Language FROM PRD_APP_THEME S1 WHERE S1.VISION_ID=TAppr.VISION_ID AND APPLICATION_ID = ?) Language "
					+ " FROM VISION_USERS TAppr"
					+ " WHERE USER_STATUS = 0  AND RECORD_INDICATOR = 0"
					+ " AND UPPER (USER_LOGIN_ID) = UPPER (?)"
					+ " AND ( UNSUCCESSFUL_LOGIN_ATTEMPTS IS NULL OR UNSUCCESSFUL_LOGIN_ATTEMPTS <= 3)");
		}
		try {

			objParams[0] = dObj.getUserLoginId();
			objParams[1] = productName;
			objParams[2] = productName;
			objParams[3] = productName;
			return getJdbcTemplate().query(strQueryAppr.toString(), objParams, getMapper1());

		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("Error: getQueryResults Exception :   ");
			/*if (objParams != null)
				for (int i = 0; i < objParams.length; i++)
					//logger.error("objParams[" + i + "]" + objParams[i].toString());
*/			return null;

		}
	}

	@Override
	public List<VisionUsersVb> getQueryResults(VisionUsersVb dObj, int intStatus) {
		setServiceDefaults();
		List<VisionUsersVb> collTemp = null;
		final int intKeyFieldsCount = 1;
		String strQueryAppr = "";
		String strQueryPend = "";

			strQueryAppr = new String("Select TAppr.VISION_ID,"
					+ "TAppr.USER_NAME, TAppr.USER_LOGIN_ID, TAppr.USER_EMAIL_ID,TAppr.STF_COUNTRY,TAppr.STF_LE_BOOK, TAppr.STAFF_ID,(Select STAFF_NAME FROM STAFF_ID WHERE STAFF_ID=TAppr.STAFF_ID AND COUNTRY=TAppr.STF_COUNTRY AND LE_BOOK=TAppr.STF_LE_BOOK)AS STAFF_NAME, "
					+ getDbFunction("DATEFUNC")+"(TAppr.LAST_ACTIVITY_DATE, '"+getDbFunction("DD_Mon_RRRR")+" "+getDbFunction("TIME")+"') LAST_ACTIVITY_DATE, TAppr.USER_GROUP_AT,"
					+ "TAppr.USER_GROUP, TAppr.USER_PROFILE_AT, TAppr.USER_PROFILE,"
					+ "TAppr.UPDATE_RESTRICTION, TAppr.LEGAL_VEHICLE, (Select LV_DESCRIPTION From LEGAL_VEHICLES Where LEGAL_VEHICLE = TAppr.LEGAL_VEHICLE) AS LegalVehicleDesc,"
					+ "TAppr.COUNTRY, TAppr.LE_BOOK,"
					+ "TAppr.REGION_PROVINCE, TAppr.BUSINESS_GROUP, TAppr.PRODUCT_SUPER_GROUP,"
					+ "TAppr.OUC_ATTRIBUTE, TAppr.SBU_CODE_AT,TAppr.SBU_CODE, TAppr.PRODUCT_ATTRIBUTE,"
					+ "TAppr.ACCOUNT_OFFICER, TAppr.GCID_ACCESS, TAppr.USER_STATUS_NT, TAppr.USER_STATUS,"
					+ getDbFunction("DATEFUNC")+"(TAppr.USER_STATUS_DATE, '"+getDbFunction("DD_Mon_RRRR")+" "+getDbFunction("TIME")+"') USER_STATUS_DATE, TAppr.MAKER,"
					+ "(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = " + getDbFunction("NVL")
					+ "(TAppr.MAKER,0) ) MAKER_NAME, "
					+ "TAppr.VERIFIER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "
					+ getDbFunction("NVL") + "(TAppr.VERIFIER,0) ) VERIFIER_NAME, "
					+ "TAppr.INTERNAL_STATUS, TAppr.RECORD_INDICATOR_NT,"
					+ "TAppr.RECORD_INDICATOR,T1.NUM_SUBTAB_DESCRIPTION RECORD_INDICATOR_DESC, "+ getDbFunction("DATEFUNC")+"(TAppr.DATE_LAST_MODIFIED, '"+getDbFunction("DD_Mon_RRRR")+" "+getDbFunction("TIME")+"') DATE_LAST_MODIFIED,"
					+ getDbFunction("DATEFUNC")+"(TAppr.DATE_CREATION, '"+getDbFunction("DD_Mon_RRRR")+" "+getDbFunction("TIME")+"') DATE_CREATION,"
					+ getDbFunction("DATEFUNC")+"(TAppr.LAST_UNSUCCESSFUL_LOGIN_DATE, '"+getDbFunction("DD_Mon_RRRR")+" "+getDbFunction("TIME")+"') LAST_UNSUCCESSFUL_LOGIN_DATE, TAppr.UNSUCCESSFUL_LOGIN_ATTEMPTS, TAppr.FILE_NAME, TAppr.USER_PHOTO, TAppr.ENABLE_WIDGETS, "
					+ "TAppr.APPLICATION_ACCESS,TAppr.RA_SOC,TAppr.OTHER_ATTR,TAppr.PWD_RESET_FLAG  From VISION_USERS TAppr,NUM_SUB_TAB T1 "
					+ "Where TAppr.VISION_ID = ? and T1.NUM_tab = TAppr.RECORD_INDICATOR_NT"
					+ " and T1.NUM_sub_tab = TAppr.RECORD_INDICATOR");
			strQueryPend = new String("Select TPend.VISION_ID,"
					+ "TPend.USER_NAME, TPend.USER_LOGIN_ID, TPend.USER_EMAIL_ID,TPend.STF_COUNTRY,TPend.STF_LE_BOOK, TPend.STAFF_ID,(Select STAFF_NAME FROM STAFF_ID WHERE STAFF_ID=TPend.STAFF_ID AND COUNTRY=TPend.STF_COUNTRY AND LE_BOOK=TPend.STF_LE_BOOK)AS STAFF_NAME, "
					+ getDbFunction("DATEFUNC")+"(TPend.LAST_ACTIVITY_DATE, '"+getDbFunction("DD_Mon_RRRR")+" "+getDbFunction("TIME")+"') LAST_ACTIVITY_DATE, TPend.USER_GROUP_AT,"
					+ "TPend.USER_GROUP, TPend.USER_PROFILE_AT, TPend.USER_PROFILE,"
					+ "TPend.UPDATE_RESTRICTION, TPend.LEGAL_VEHICLE, (Select LV_DESCRIPTION From LEGAL_VEHICLES Where LEGAL_VEHICLE = TPend.LEGAL_VEHICLE) AS LegalVehicleDesc,"
					+ "TPend.COUNTRY, TPend.LE_BOOK,"
					+ "TPend.REGION_PROVINCE, TPend.BUSINESS_GROUP, TPend.PRODUCT_SUPER_GROUP,"
					+ "TPend.OUC_ATTRIBUTE, TPend.SBU_CODE_AT,TPend.SBU_CODE, TPend.PRODUCT_ATTRIBUTE,"
					+ "TPend.ACCOUNT_OFFICER, TPend.GCID_ACCESS, TPend.USER_STATUS_NT, TPend.USER_STATUS,"
					+ getDbFunction("DATEFUNC")+"(TPend.USER_STATUS_DATE, '"+getDbFunction("DD_Mon_RRRR")+" "+getDbFunction("TIME")+"') USER_STATUS_DATE, TPend.MAKER,"
					+ " (SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = " + getDbFunction("NVL")
					+ "(TPend.MAKER,0) ) MAKER_NAME, "
					+ "TPend.VERIFIER, (SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "
					+ getDbFunction("NVL") + "(TPend.VERIFIER,0) ) VERIFIER_NAME,"
					+ "TPend.INTERNAL_STATUS, TPend.RECORD_INDICATOR_NT,"
					+ "TPend.RECORD_INDICATOR,T1.NUM_SUBTAB_DESCRIPTION RECORD_INDICATOR_DESC,"+ getDbFunction("DATEFUNC")+"(TPend.DATE_LAST_MODIFIED, '"+getDbFunction("DD_Mon_RRRR")+" "+getDbFunction("TIME")+"') DATE_LAST_MODIFIED,"
					+ getDbFunction("DATEFUNC")+"(TPend.DATE_CREATION, '"+getDbFunction("DD_Mon_RRRR")+" "+getDbFunction("TIME")+"') DATE_CREATION,"
					+ getDbFunction("DATEFUNC")+"(TPend.LAST_UNSUCCESSFUL_LOGIN_DATE, '"+getDbFunction("DD_Mon_RRRR")+" "+getDbFunction("TIME")+"') LAST_UNSUCCESSFUL_LOGIN_DATE, TPend.UNSUCCESSFUL_LOGIN_ATTEMPTS, TPend.FILE_NAME, TPend.USER_PHOTO, TPend.ENABLE_WIDGETS,"
					+ "TPend.APPLICATION_ACCESS,TPend.RA_SOC,TPend.OTHER_ATTR,TPend.PWD_RESET_FLAG From VISION_USERS_PEND TPend,NUM_SUB_TAB T1 "
					+ " Where TPend.VISION_ID = ? and T1.NUM_tab = TPend.RECORD_INDICATOR_NT"
					+ " and T1.NUM_sub_tab = TPend.RECORD_INDICATOR");
		
		Object objParams[] = new Object[intKeyFieldsCount];
		objParams[0] = dObj.getVisionId();// [VISION_ID]

		try 
			{if(!dObj.isVerificationRequired() || dObj.isReview()){intStatus =0;}
			if (intStatus == 0) {
				logger.info("Executing approved query");
				collTemp = getJdbcTemplate().query(strQueryAppr.toString(), objParams, getQueryResultsMapper());
			} else {
				logger.info("Executing pending query");
				collTemp = getJdbcTemplate().query(strQueryPend.toString(), objParams, getQueryResultsMapper());
			}
			return collTemp;
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("Error: getQueryResults Exception :   ");
			/*if (intStatus == 0)
				//logger.error(((strQueryAppr == null) ? "strQueryAppr is Null" : strQueryAppr.toString()));
			else
				//logger.error(((strQueryPend == null) ? "strQueryPend is Null" : strQueryPend.toString()));

			if (objParams != null)
				for (int i = 0; i < objParams.length; i++)*/
					//logger.error("objParams[" + i + "]" + objParams[i].toString());
			return null;
		}
	}

	@Override
	protected List<VisionUsersVb> selectApprovedRecord(VisionUsersVb vObject) {
		return getQueryResults(vObject, Constants.STATUS_ZERO);
	}

	@Override
	protected List<VisionUsersVb> doSelectPendingRecord(VisionUsersVb vObject) {
		return getQueryResults(vObject, Constants.STATUS_PENDING);
	}

	@Override
	protected int getStatus(VisionUsersVb records) {
		return records.getUserStatus();
	}

	@Override
	protected void setStatus(VisionUsersVb vObject, int status) {
		vObject.setUserStatus(status);
	}

	@Override
	protected int doInsertionAppr(VisionUsersVb vObject) {
		String query = "";
		vObject = passwordFlagSet(vObject);
		if ("ORACLE".equalsIgnoreCase(databaseType)) {
			query = "Insert Into VISION_USERS ( VISION_ID, USER_NAME, USER_LOGIN_ID, USER_EMAIL_ID, STF_COUNTRY, STF_LE_BOOK, STAFF_ID, LAST_ACTIVITY_DATE,"
					+ "USER_GROUP, USER_PROFILE, UPDATE_RESTRICTION, LEGAL_VEHICLE, COUNTRY,"
					+ "LE_BOOK, REGION_PROVINCE, BUSINESS_GROUP, PRODUCT_SUPER_GROUP, OUC_ATTRIBUTE, SBU_CODE,"
					+ "PRODUCT_ATTRIBUTE, ACCOUNT_OFFICER, GCID_ACCESS, USER_STATUS_NT, USER_STATUS, USER_STATUS_DATE,"
					+ "MAKER, VERIFIER, INTERNAL_STATUS, RECORD_INDICATOR_NT, RECORD_INDICATOR, DATE_LAST_MODIFIED, DATE_CREATION,  "
					+ "FILE_NAME, ENABLE_WIDGETS,APPLICATION_ACCESS,RA_SOC,OTHER_ATTR,PASSWORD,PWD_RESET_FLAG)"
					+ "Values (?, ?, ?, ?, ?,?,?, To_Date(?, 'DD-MM-YYYY HH24:MI:SS'), ?, ?, ?, ?, ?, ?, ?, ?, ? ,?, ?, ?, ?, ?, ?, ?,To_Date(?, 'DD-MM-YYYY HH24:MI:SS'),?, ?, ?, ?, ?,SysDate,SysDate, ?, ?,?,?,?,?,?)";

		} else if ("MSSQL".equalsIgnoreCase(databaseType)) {
			query = "Insert Into VISION_USERS ( VISION_ID, USER_NAME, USER_LOGIN_ID, USER_EMAIL_ID, STF_COUNTRY, STF_LE_BOOK, STAFF_ID, LAST_ACTIVITY_DATE,"
					+ "USER_GROUP, USER_PROFILE, UPDATE_RESTRICTION, LEGAL_VEHICLE, COUNTRY,"
					+ "LE_BOOK, REGION_PROVINCE, BUSINESS_GROUP, PRODUCT_SUPER_GROUP, OUC_ATTRIBUTE, SBU_CODE,"
					+ "PRODUCT_ATTRIBUTE, ACCOUNT_OFFICER, GCID_ACCESS, USER_STATUS_NT, USER_STATUS, USER_STATUS_DATE,"
					+ "MAKER, VERIFIER, INTERNAL_STATUS, RECORD_INDICATOR_NT, RECORD_INDICATOR, DATE_LAST_MODIFIED, DATE_CREATION,  FILE_NAME, ENABLE_WIDGETS,APPLICATION_ACCESS,RA_SOC,OTHER_ATTR,PASSWORD,PWD_RESET_FLAG)"
					+ "Values (?, ?, ?, ?, ?,?,?, CONVERT(datetime, ?, 103), ?, ?, ?, ?, ?, ?, ?, ?, ? ,?, ?, ?, ?, ?, ?, ?, CONVERT(datetime, ?, 103),?, ?, ?, ?, ?,GetDate(),GetDate(), ?, ?,?,?,?,?,?)";

		}

		Object[] args = { vObject.getVisionId(), vObject.getUserName(), vObject.getUserLoginId(),
				vObject.getUserEmailId(), vObject.getStfcountry(), vObject.getStfleBook(), vObject.getStaffId(),
				vObject.getLastActivityDate(), vObject.getUserGroup(), vObject.getUserProfile(),
				vObject.getUpdateRestriction(), vObject.getLegalVehicle(), vObject.getCountry(), vObject.getLeBook(),
				vObject.getRegionProvince(), vObject.getBusinessGroup(), vObject.getProductSuperGroup(),
				vObject.getOucAttribute(), vObject.getSbuCode(), vObject.getProductAttribute(),
				vObject.getAccountOfficer(), vObject.getGcidAccess(), vObject.getUserStatusNt(),
				vObject.getUserStatus(), vObject.getUserStatusDate(), vObject.getMaker(), vObject.getVerifier(),
				vObject.getInternalStatus(), vObject.getRecordIndicatorNt(), vObject.getRecordIndicator(),
				vObject.getFileNmae(), vObject.getEnableWidgets(), vObject.getApplicationAccess(),vObject.getRaSoc(), 
				vObject.getOtherAttr(),vObject.getPassword(),vObject.getPasswordResetFlag()};
		return getJdbcTemplate().update(query, args);
	}

	@Override
	protected int doInsertionPend(VisionUsersVb vObject) {
		String query = "";
		vObject = passwordFlagSet(vObject);
		if ("ORACLE".equalsIgnoreCase(databaseType)) {
			query = "Insert Into VISION_USERS_PEND ( VISION_ID, USER_NAME, USER_LOGIN_ID, USER_EMAIL_ID, STF_COUNTRY, STF_LE_BOOK, STAFF_ID, LAST_ACTIVITY_DATE,"
					+ "USER_GROUP, USER_PROFILE, UPDATE_RESTRICTION, LEGAL_VEHICLE, COUNTRY,"
					+ "LE_BOOK, REGION_PROVINCE, BUSINESS_GROUP, PRODUCT_SUPER_GROUP, OUC_ATTRIBUTE, SBU_CODE,"
					+ "PRODUCT_ATTRIBUTE, ACCOUNT_OFFICER, GCID_ACCESS, USER_STATUS_NT, USER_STATUS, USER_STATUS_DATE,"
					+ "MAKER, VERIFIER, INTERNAL_STATUS, RECORD_INDICATOR_NT, RECORD_INDICATOR, DATE_LAST_MODIFIED, DATE_CREATION,  FILE_NAME, ENABLE_WIDGETS,APPLICATION_ACCESS,RA_SOC,OTHER_ATTR,PASSWORD,PWD_RESET_FLAG)"
					+ "Values (?, ?, ?, ?, ?,?,?, To_Date(?, 'DD-MM-YYYY HH24:MI:SS'), ?, ?, ?, ?, ?, ?, ?, ?, ? ,?, ?, ?, ?, ?, ?, ?,To_Date(?, 'DD-MM-YYYY HH24:MI:SS'),?, ?, ?, ?, ?,SysDate,SysDate, ?, ?,?,?,?,?,?)";

		} else if ("MSSQL".equalsIgnoreCase(databaseType)) {
			query = "Insert Into VISION_USERS_PEND ( VISION_ID, USER_NAME, USER_LOGIN_ID, USER_EMAIL_ID, STF_COUNTRY, STF_LE_BOOK, STAFF_ID, LAST_ACTIVITY_DATE,"
					+ "USER_GROUP, USER_PROFILE, UPDATE_RESTRICTION, LEGAL_VEHICLE, COUNTRY,"
					+ "LE_BOOK, REGION_PROVINCE, BUSINESS_GROUP, PRODUCT_SUPER_GROUP, OUC_ATTRIBUTE, SBU_CODE,"
					+ "PRODUCT_ATTRIBUTE, ACCOUNT_OFFICER, GCID_ACCESS, USER_STATUS_NT, USER_STATUS, USER_STATUS_DATE,"
					+ "MAKER, VERIFIER, INTERNAL_STATUS, RECORD_INDICATOR_NT, RECORD_INDICATOR, DATE_LAST_MODIFIED, DATE_CREATION,  FILE_NAME, ENABLE_WIDGETS,APPLICATION_ACCESS,RA_SOC,OTHER_ATTR,PASSWORD,PWD_RESET_FLAG) "
					+ "Values (?, ?, ?, ?, ?,?,?, CONVERT(datetime, ?, 103), ?, ?, ?, ?, ?, ?, ?, ?, ? ,?, ?, ?, ?, ?, ?, ?, CONVERT(datetime, ?, 103),?, ?, ?, ?, ?,GetDate(),GetDate(), ?, ?,?,?,?,?,?)";

		}

		Object[] args = { vObject.getVisionId(), vObject.getUserName(), vObject.getUserLoginId(),
				vObject.getUserEmailId(), vObject.getStfcountry(), vObject.getStfleBook(), vObject.getStaffId(),
				vObject.getLastActivityDate(), vObject.getUserGroup(), vObject.getUserProfile(),
				vObject.getUpdateRestriction(), vObject.getLegalVehicle(), vObject.getCountry(), vObject.getLeBook(),
				vObject.getRegionProvince(), vObject.getBusinessGroup(), vObject.getProductSuperGroup(),
				vObject.getOucAttribute(), vObject.getSbuCode(), vObject.getProductAttribute(),
				vObject.getAccountOfficer(), vObject.getGcidAccess(), vObject.getUserStatusNt(),
				vObject.getUserStatus(), vObject.getUserStatusDate(), vObject.getMaker(), vObject.getVerifier(),
				vObject.getInternalStatus(), vObject.getRecordIndicatorNt(), vObject.getRecordIndicator(),
				vObject.getFileNmae(), vObject.getEnableWidgets(), vObject.getApplicationAccess(),vObject.getRaSoc(),
				vObject.getOtherAttr(),vObject.getPassword(),vObject.getPasswordResetFlag()};
		return getJdbcTemplate().update(query, args);
	}

	@Override
	protected int doInsertionPendWithDc(VisionUsersVb vObject) {
		String query = "";
		vObject = passwordFlagSet(vObject);
		if ("ORACLE".equalsIgnoreCase(databaseType)) {
			query = "Insert Into VISION_USERS_PEND ( VISION_ID, USER_NAME, USER_LOGIN_ID, USER_EMAIL_ID, STF_COUNTRY, STF_LE_BOOK, STAFF_ID, LAST_ACTIVITY_DATE,"
					+ "USER_GROUP, USER_PROFILE, UPDATE_RESTRICTION, LEGAL_VEHICLE, COUNTRY,"
					+ "LE_BOOK, REGION_PROVINCE, BUSINESS_GROUP, PRODUCT_SUPER_GROUP, OUC_ATTRIBUTE, SBU_CODE,"
					+ "PRODUCT_ATTRIBUTE, ACCOUNT_OFFICER, GCID_ACCESS, USER_STATUS_NT, USER_STATUS, USER_STATUS_DATE,"
					+ "MAKER, VERIFIER, INTERNAL_STATUS, RECORD_INDICATOR_NT, RECORD_INDICATOR, DATE_LAST_MODIFIED, DATE_CREATION,  FILE_NAME, ENABLE_WIDGETS,APPLICATION_ACCESS,RA_SOC,OTHER_ATTR,PASSWORD,PWD_RESET_FLAG)"
					+ "Values (?, ?, ?, ?, ?,?,?, To_Date(?, 'DD-MM-YYYY HH24:MI:SS'), ?, ?, ?, ?, ?, ?, ?, ?, ? ,?, ?, ?, ?, ?, ?, ?,To_Date(?, 'DD-MM-YYYY HH24:MI:SS'),?, ?, ?, ?, ?,SysDate,To_Date(?, 'DD-MM-YYYY HH24:MI:SS'), ?, ?,?,?,?,?, ?)";
		} else if ("MSSQL".equalsIgnoreCase(databaseType)) {
			query = "Insert Into VISION_USERS_PEND ( VISION_ID, USER_NAME, USER_LOGIN_ID, USER_EMAIL_ID, STF_COUNTRY, STF_LE_BOOK, STAFF_ID, LAST_ACTIVITY_DATE,"
					+ "USER_GROUP, USER_PROFILE, UPDATE_RESTRICTION, LEGAL_VEHICLE, COUNTRY,"
					+ "LE_BOOK, REGION_PROVINCE, BUSINESS_GROUP, PRODUCT_SUPER_GROUP, OUC_ATTRIBUTE, SBU_CODE,"
					+ "PRODUCT_ATTRIBUTE, ACCOUNT_OFFICER, GCID_ACCESS, USER_STATUS_NT, USER_STATUS, USER_STATUS_DATE,"
					+ "MAKER, VERIFIER, INTERNAL_STATUS, RECORD_INDICATOR_NT, RECORD_INDICATOR, DATE_LAST_MODIFIED, DATE_CREATION,  FILE_NAME, ENABLE_WIDGETS,APPLICATION_ACCESS,RA_SOC,OTHER_ATTR,PASSWORD,PWD_RESET_FLAG)"
					+ "Values (?, ?, ?, ?, ?,?,?, CONVERT(datetime, ?, 103), ?, ?, ?, ?, ?, ?, ?, ?, ? ,?, ?, ?, ?, ?, ?, ?, CONVERT(datetime, ?, 103),?, ?, ?, ?, ?,GetDate(),CONVERT(datetime, ?, 103), ?, ?,?,?,?,?,?)";

		}
		Object[] args = { vObject.getVisionId(), vObject.getUserName(), vObject.getUserLoginId(),
				vObject.getUserEmailId(), vObject.getStfcountry(), vObject.getStfleBook(), vObject.getStaffId(),
				vObject.getLastActivityDate(), vObject.getUserGroup(), vObject.getUserProfile(),
				vObject.getUpdateRestriction(), vObject.getLegalVehicle(), vObject.getCountry(), vObject.getLeBook(),
				vObject.getRegionProvince(), vObject.getBusinessGroup(), vObject.getProductSuperGroup(),
				vObject.getOucAttribute(), vObject.getSbuCode(), vObject.getProductAttribute(),
				vObject.getAccountOfficer(), vObject.getGcidAccess(), vObject.getUserStatusNt(),
				vObject.getUserStatus(), vObject.getUserStatusDate(), vObject.getMaker(), vObject.getVerifier(),
				vObject.getInternalStatus(), vObject.getRecordIndicatorNt(), vObject.getRecordIndicator(),
				vObject.getDateCreation(), vObject.getFileNmae(), vObject.getEnableWidgets(),
				vObject.getApplicationAccess(),vObject.getRaSoc(),vObject.getOtherAttr(),vObject.getPassword(),vObject.getPasswordResetFlag() };
		return getJdbcTemplate().update(query, args);
	}
	private List<VisionUsersVb> getUserPasswordAppr(VisionUsersVb vObject) {
		List<VisionUsersVb> collTemp = null;
		try {
			String query = "Select Password,PWD_RESET_FLAG from Vision_Users where Vision_ID = ?";
			Object args[] = {vObject.getVisionId()};
			collTemp = getJdbcTemplate().query(query, args, getPasswordApprMapper());
			return collTemp;
		}catch(Exception e) {
			return null;
		}
	}
	private List<VisionUsersVb> getUserPasswordPend(VisionUsersVb vObject) {
		List<VisionUsersVb> collTemp = null;
		try {
			String query = "Select Password,PWD_RESET_FLAG from Vision_Users_Pend where Vision_ID = ?";
			Object args[] = {vObject.getVisionId()};
			collTemp = getJdbcTemplate().query(query, args, getPasswordApprMapper());
			return collTemp;
		}catch(Exception e) {
			return null;
		}
	}
	@Override
	protected int doUpdateAppr(VisionUsersVb vObject) {
		String query = "";
		String passwordresetCols = "";
		if (ValidationUtil.isValid(vObject.getPassword())) {
			passwordresetCols = ",PASSWORD = ? ";
			vObject.setPasswordResetFlag("Y");
		}
		vObject = passwordFlagSet(vObject);
		if ("ORACLE".equalsIgnoreCase(databaseType)) {
			query = "Update VISION_USERS Set USER_LOGIN_ID = ?, USER_NAME = ?, USER_EMAIL_ID = ?, STF_COUNTRY=?, STF_LE_BOOK=?, STAFF_ID = ?,"
					+ "LAST_ACTIVITY_DATE = To_Date(?, 'DD-MM-YYYY HH24:MI:SS'),"
					+ "LEGAL_VEHICLE = ?, COUNTRY = ?, LE_BOOK = ?, REGION_PROVINCE = ?, BUSINESS_GROUP = ?, PRODUCT_SUPER_GROUP = ?, "
					+ "USER_GROUP = ?, USER_PROFILE = ?, UPDATE_RESTRICTION = ?,"
					+ "OUC_ATTRIBUTE = ?, GCID_ACCESS = ?, SBU_CODE = ?, PRODUCT_ATTRIBUTE = ?, ACCOUNT_OFFICER = ?,"
					+ "USER_STATUS_NT = ?, USER_STATUS = ?, USER_STATUS_DATE = To_Date(?, 'DD-MM-YYYY HH24:MI:SS'),"
					+ "MAKER = ?, VERIFIER = ?, INTERNAL_STATUS = ?, RECORD_INDICATOR_NT = ?, RECORD_INDICATOR = ?,DATE_LAST_MODIFIED = SysDate, FILE_NAME = ?, ENABLE_WIDGETS = ?, "
					+ "APPLICATION_ACCESS = ?,RA_SOC = ? , OTHER_ATTR = ?, PWD_RESET_FLAG = ? " + passwordresetCols
					+ " Where VISION_ID = ?";
		} else if ("MSSQL".equalsIgnoreCase(databaseType)) {
			query = "Update VISION_USERS Set USER_LOGIN_ID = ?, USER_NAME = ?, USER_EMAIL_ID = ?, STF_COUNTRY=?, STF_LE_BOOK=?, STAFF_ID = ?,"
					+ "LAST_ACTIVITY_DATE = CONVERT(datetime, ?, 103),"
					+ "LEGAL_VEHICLE = ?, COUNTRY = ?, LE_BOOK = ?, REGION_PROVINCE = ?, BUSINESS_GROUP = ?, PRODUCT_SUPER_GROUP = ?, "
					+ "USER_GROUP = ?, USER_PROFILE = ?, UPDATE_RESTRICTION = ?,"
					+ "OUC_ATTRIBUTE = ?, GCID_ACCESS = ?, SBU_CODE = ?, PRODUCT_ATTRIBUTE = ?, ACCOUNT_OFFICER = ?,"
					+ "USER_STATUS_NT = ?, USER_STATUS = ?, USER_STATUS_DATE = CONVERT(datetime, ?, 103),"
					+ "MAKER = ?, VERIFIER = ?, INTERNAL_STATUS = ?, RECORD_INDICATOR_NT = ?, RECORD_INDICATOR = ?,DATE_LAST_MODIFIED = GetDate(), FILE_NAME = ?, ENABLE_WIDGETS = ?, "
					+ "APPLICATION_ACCESS = ?,RA_SOC = ?, OTHER_ATTR = ?, PWD_RESET_FLAG = ? " + passwordresetCols
					+ " Where VISION_ID = ?";

		}

		Object[] args = null;

		if (!ValidationUtil.isValid(vObject.getPassword())) {
			args = new Object[] { vObject.getUserLoginId(), vObject.getUserName(), vObject.getUserEmailId(),
					vObject.getStfcountry(), vObject.getStfleBook(), vObject.getStaffId(),
					vObject.getLastActivityDate(), vObject.getLegalVehicle(), vObject.getCountry(), vObject.getLeBook(),
					vObject.getRegionProvince(), vObject.getBusinessGroup(), vObject.getProductSuperGroup(),
					vObject.getUserGroup(), vObject.getUserProfile(), vObject.getUpdateRestriction(),
					vObject.getOucAttribute(), vObject.getGcidAccess(), vObject.getSbuCode(),
					vObject.getProductAttribute(), vObject.getAccountOfficer(), vObject.getUserStatusNt(),
					vObject.getUserStatus(), vObject.getUserStatusDate(), vObject.getMaker(), vObject.getVerifier(),
					vObject.getInternalStatus(), vObject.getRecordIndicatorNt(), vObject.getRecordIndicator(),
					vObject.getFileNmae(), vObject.getEnableWidgets(), vObject.getApplicationAccess(),
					vObject.getRaSoc(), vObject.getOtherAttr(), vObject.getPasswordResetFlag(), vObject.getVisionId() };
		} else {
			args = new Object[] { vObject.getUserLoginId(), vObject.getUserName(), vObject.getUserEmailId(),
					vObject.getStfcountry(), vObject.getStfleBook(), vObject.getStaffId(),
					vObject.getLastActivityDate(), vObject.getLegalVehicle(), vObject.getCountry(), vObject.getLeBook(),
					vObject.getRegionProvince(), vObject.getBusinessGroup(), vObject.getProductSuperGroup(),
					vObject.getUserGroup(), vObject.getUserProfile(), vObject.getUpdateRestriction(),
					vObject.getOucAttribute(), vObject.getGcidAccess(), vObject.getSbuCode(),
					vObject.getProductAttribute(), vObject.getAccountOfficer(), vObject.getUserStatusNt(),
					vObject.getUserStatus(), vObject.getUserStatusDate(), vObject.getMaker(), vObject.getVerifier(),
					vObject.getInternalStatus(), vObject.getRecordIndicatorNt(), vObject.getRecordIndicator(),
					vObject.getFileNmae(), vObject.getEnableWidgets(), vObject.getApplicationAccess(),
					vObject.getRaSoc(), vObject.getOtherAttr(), vObject.getPasswordResetFlag(), vObject.getPassword(), vObject.getVisionId() };
		}

		return getJdbcTemplate().update(query, args);
	}
	//This method used only when Approve
	protected int doUpdateMainTable(VisionUsersVb vObject) {
		String query = "";
		String passwordresetCols = "";
		if (ValidationUtil.isValid(vObject.getPassword())) {
			passwordresetCols = ",PASSWORD = ? ";
		}
		vObject = passwordFlagSet(vObject);
		if ("ORACLE".equalsIgnoreCase(databaseType)) {
			query = "Update VISION_USERS Set USER_LOGIN_ID = ?, USER_NAME = ?, USER_EMAIL_ID = ?, STF_COUNTRY=?, STF_LE_BOOK=?, STAFF_ID = ?,"
					+ "LAST_ACTIVITY_DATE = To_Date(?, 'DD-MM-YYYY HH24:MI:SS'),"
					+ "LEGAL_VEHICLE = ?, COUNTRY = ?, LE_BOOK = ?, REGION_PROVINCE = ?, BUSINESS_GROUP = ?, PRODUCT_SUPER_GROUP = ?, "
					+ "USER_GROUP = ?, USER_PROFILE = ?, UPDATE_RESTRICTION = ?,"
					+ "OUC_ATTRIBUTE = ?, GCID_ACCESS = ?, SBU_CODE = ?, PRODUCT_ATTRIBUTE = ?, ACCOUNT_OFFICER = ?,"
					+ "USER_STATUS_NT = ?, USER_STATUS = ?, USER_STATUS_DATE = To_Date(?, 'DD-MM-YYYY HH24:MI:SS'),"
					+ "MAKER = ?, VERIFIER = ?, INTERNAL_STATUS = ?, RECORD_INDICATOR_NT = ?, RECORD_INDICATOR = ?,DATE_LAST_MODIFIED = SysDate, FILE_NAME = ?, ENABLE_WIDGETS = ?, "
					+ "APPLICATION_ACCESS = ?,RA_SOC = ? , OTHER_ATTR = ?, PWD_RESET_FLAG = ? "+passwordresetCols+" "
					+ " Where VISION_ID = ?";
		} else if ("MSSQL".equalsIgnoreCase(databaseType)) {
			query = "Update VISION_USERS Set USER_LOGIN_ID = ?, USER_NAME = ?, USER_EMAIL_ID = ?, STF_COUNTRY=?, STF_LE_BOOK=?, STAFF_ID = ?,"
					+ "LAST_ACTIVITY_DATE = CONVERT(datetime, ?, 103),"
					+ "LEGAL_VEHICLE = ?, COUNTRY = ?, LE_BOOK = ?, REGION_PROVINCE = ?, BUSINESS_GROUP = ?, PRODUCT_SUPER_GROUP = ?, "
					+ "USER_GROUP = ?, USER_PROFILE = ?, UPDATE_RESTRICTION = ?,"
					+ "OUC_ATTRIBUTE = ?, GCID_ACCESS = ?, SBU_CODE = ?, PRODUCT_ATTRIBUTE = ?, ACCOUNT_OFFICER = ?,"
					+ "USER_STATUS_NT = ?, USER_STATUS = ?, USER_STATUS_DATE = CONVERT(datetime, ?, 103),"
					+ "MAKER = ?, VERIFIER = ?, INTERNAL_STATUS = ?, RECORD_INDICATOR_NT = ?, RECORD_INDICATOR = ?,DATE_LAST_MODIFIED = GetDate(), FILE_NAME = ?, ENABLE_WIDGETS = ?, "
					+ "APPLICATION_ACCESS = ?,RA_SOC = ?, OTHER_ATTR = ?, PWD_RESET_FLAG = ? "+passwordresetCols+" "
					+ " Where VISION_ID = ?";

		}

		Object[] args = null;

		if (!ValidationUtil.isValid(vObject.getPassword())) {
			args = new Object[] { vObject.getUserLoginId(), vObject.getUserName(), vObject.getUserEmailId(),
					vObject.getStfcountry(), vObject.getStfleBook(), vObject.getStaffId(),
					vObject.getLastActivityDate(), vObject.getLegalVehicle(), vObject.getCountry(), vObject.getLeBook(),
					vObject.getRegionProvince(), vObject.getBusinessGroup(), vObject.getProductSuperGroup(),
					vObject.getUserGroup(), vObject.getUserProfile(), vObject.getUpdateRestriction(),
					vObject.getOucAttribute(), vObject.getGcidAccess(), vObject.getSbuCode(),
					vObject.getProductAttribute(), vObject.getAccountOfficer(), vObject.getUserStatusNt(),
					vObject.getUserStatus(), vObject.getUserStatusDate(), vObject.getMaker(), vObject.getVerifier(),
					vObject.getInternalStatus(), vObject.getRecordIndicatorNt(), vObject.getRecordIndicator(),
					vObject.getFileNmae(), vObject.getEnableWidgets(), vObject.getApplicationAccess(),
					vObject.getRaSoc(), vObject.getOtherAttr(), vObject.getPasswordResetFlag(), vObject.getVisionId() };
		} else {
			args = new Object[] { vObject.getUserLoginId(), vObject.getUserName(), vObject.getUserEmailId(),
					vObject.getStfcountry(), vObject.getStfleBook(), vObject.getStaffId(),
					vObject.getLastActivityDate(), vObject.getLegalVehicle(), vObject.getCountry(), vObject.getLeBook(),
					vObject.getRegionProvince(), vObject.getBusinessGroup(), vObject.getProductSuperGroup(),
					vObject.getUserGroup(), vObject.getUserProfile(), vObject.getUpdateRestriction(),
					vObject.getOucAttribute(), vObject.getGcidAccess(), vObject.getSbuCode(),
					vObject.getProductAttribute(), vObject.getAccountOfficer(), vObject.getUserStatusNt(),
					vObject.getUserStatus(), vObject.getUserStatusDate(), vObject.getMaker(), vObject.getVerifier(),
					vObject.getInternalStatus(), vObject.getRecordIndicatorNt(), vObject.getRecordIndicator(),
					vObject.getFileNmae(), vObject.getEnableWidgets(), vObject.getApplicationAccess(),
					vObject.getRaSoc(), vObject.getOtherAttr(), vObject.getPasswordResetFlag(), vObject.getPassword(), vObject.getVisionId() };
		}

		return getJdbcTemplate().update(query, args);
	}
	
	@Override
	protected int doUpdatePend(VisionUsersVb vObject) {
		String query = "";
		String passwordresetCols = "";
		if (ValidationUtil.isValid(vObject.getPassword())) {
			passwordresetCols = ",PASSWORD = ? ";
		//	vObject.setPasswordResetFlag("Y");
		}
		vObject = passwordFlagSet(vObject);
		if ("ORACLE".equalsIgnoreCase(databaseType)) {
			query = "Update VISION_USERS_PEND Set USER_LOGIN_ID = ?, USER_NAME = ?, USER_EMAIL_ID = ?, STF_COUNTRY=?, STF_LE_BOOK=?, STAFF_ID = ?,"
					+ "LAST_ACTIVITY_DATE = To_Date(?, 'DD-MM-YYYY HH24:MI:SS'),"
					+ "LEGAL_VEHICLE = ?, COUNTRY = ?, LE_BOOK = ?, REGION_PROVINCE = ?, BUSINESS_GROUP = ?, PRODUCT_SUPER_GROUP = ?, "
					+ "USER_GROUP = ?, USER_PROFILE = ?, UPDATE_RESTRICTION = ?,"
					+ "OUC_ATTRIBUTE = ?, GCID_ACCESS = ?, SBU_CODE = ?, PRODUCT_ATTRIBUTE = ?, ACCOUNT_OFFICER = ?,"
					+ "USER_STATUS_NT = ?, USER_STATUS = ?, USER_STATUS_DATE = To_Date(?, 'DD-MM-YYYY HH24:MI:SS'),"
					+ "MAKER = ?, VERIFIER = ?, INTERNAL_STATUS = ?, RECORD_INDICATOR_NT = ?, RECORD_INDICATOR = ?,DATE_LAST_MODIFIED = SysDate, FILE_NAME = ?, ENABLE_WIDGETS = ?, "
					+ "APPLICATION_ACCESS = ?,RA_SOC = ? , OTHER_ATTR = ?, PWD_RESET_FLAG = ? " + passwordresetCols
					+ "  Where VISION_ID = ?";
		} else if ("MSSQL".equalsIgnoreCase(databaseType)) {
			query = "Update VISION_USERS_PEND Set USER_LOGIN_ID = ?, USER_NAME = ?, USER_EMAIL_ID = ?, STF_COUNTRY=?, STF_LE_BOOK=?, STAFF_ID = ?,"
					+ "LAST_ACTIVITY_DATE = CONVERT(datetime, ?, 103),"
					+ "LEGAL_VEHICLE = ?, COUNTRY = ?, LE_BOOK = ?, REGION_PROVINCE = ?, BUSINESS_GROUP = ?, PRODUCT_SUPER_GROUP = ?, "
					+ "USER_GROUP = ?, USER_PROFILE = ?, UPDATE_RESTRICTION = ?,"
					+ "OUC_ATTRIBUTE = ?, GCID_ACCESS = ?, SBU_CODE = ?, PRODUCT_ATTRIBUTE = ?, ACCOUNT_OFFICER = ?,"
					+ "USER_STATUS_NT = ?, USER_STATUS = ?, USER_STATUS_DATE = CONVERT(datetime, ?, 103),"
					+ "MAKER = ?, VERIFIER = ?, INTERNAL_STATUS = ?, RECORD_INDICATOR_NT = ?, RECORD_INDICATOR = ?,DATE_LAST_MODIFIED = GetDate(), FILE_NAME = ?, ENABLE_WIDGETS = ?, "
					+ "APPLICATION_ACCESS = ?,RA_SOC = ?, OTHER_ATTR = ?, PWD_RESET_FLAG = ? " + passwordresetCols
					+ "  Where VISION_ID = ?";

		}

		Object[] args = null;

		if (!ValidationUtil.isValid(vObject.getPassword())) {
			args = new Object[] { vObject.getUserLoginId(), vObject.getUserName(), vObject.getUserEmailId(),
					vObject.getStfcountry(), vObject.getStfleBook(), vObject.getStaffId(),
					vObject.getLastActivityDate(), vObject.getLegalVehicle(), vObject.getCountry(), vObject.getLeBook(),
					vObject.getRegionProvince(), vObject.getBusinessGroup(), vObject.getProductSuperGroup(),
					vObject.getUserGroup(), vObject.getUserProfile(), vObject.getUpdateRestriction(),
					vObject.getOucAttribute(), vObject.getGcidAccess(), vObject.getSbuCode(),
					vObject.getProductAttribute(), vObject.getAccountOfficer(), vObject.getUserStatusNt(),
					vObject.getUserStatus(), vObject.getUserStatusDate(), vObject.getMaker(), vObject.getVerifier(),
					vObject.getInternalStatus(), vObject.getRecordIndicatorNt(), vObject.getRecordIndicator(),
					vObject.getFileNmae(), vObject.getEnableWidgets(), vObject.getApplicationAccess(),
					vObject.getRaSoc(), vObject.getOtherAttr(), vObject.getPasswordResetFlag(), vObject.getVisionId() };
		} else {
			args = new Object[] { vObject.getUserLoginId(), vObject.getUserName(), vObject.getUserEmailId(),
					vObject.getStfcountry(), vObject.getStfleBook(), vObject.getStaffId(),
					vObject.getLastActivityDate(), vObject.getLegalVehicle(), vObject.getCountry(), vObject.getLeBook(),
					vObject.getRegionProvince(), vObject.getBusinessGroup(), vObject.getProductSuperGroup(),
					vObject.getUserGroup(), vObject.getUserProfile(), vObject.getUpdateRestriction(),
					vObject.getOucAttribute(), vObject.getGcidAccess(), vObject.getSbuCode(),
					vObject.getProductAttribute(), vObject.getAccountOfficer(), vObject.getUserStatusNt(),
					vObject.getUserStatus(), vObject.getUserStatusDate(), vObject.getMaker(), vObject.getVerifier(),
					vObject.getInternalStatus(), vObject.getRecordIndicatorNt(), vObject.getRecordIndicator(),
					vObject.getFileNmae(), vObject.getEnableWidgets(), vObject.getApplicationAccess(),
					vObject.getRaSoc(), vObject.getOtherAttr(), vObject.getPasswordResetFlag(), vObject.getPassword(), vObject.getVisionId() };
		}

		return getJdbcTemplate().update(query, args);
	}

	@Override
	protected int doDeleteAppr(VisionUsersVb vObject) {
		String query = "Delete From VISION_USERS Where VISION_ID = ?";
		Object[] args = { vObject.getVisionId() };
		return getJdbcTemplate().update(query, args);
	}

	@Override
	protected int deletePendingRecord(VisionUsersVb vObject) {
		String query = "Delete From VISION_USERS_PEND Where VISION_ID = ?";
		Object[] args = { vObject.getVisionId() };
		return getJdbcTemplate().update(query, args);
	}

	@Override
	protected String frameErrorMessage(VisionUsersVb vObject, String strOperation) {
		// specify all the key fields and their values first
		String strErrMsg = new String("");
		try {
			strErrMsg = strErrMsg + "VISION_ID: " + vObject.getVisionId();
			// Now concatenate the error message that has been sent
			if ("Approve".equalsIgnoreCase(strOperation))
				strErrMsg = strErrMsg + " failed during approve Operation. Bulk Approval aborted !!";
			else
				strErrMsg = strErrMsg + " failed during reject Operation. Bulk Rejection aborted !!";
		} catch (Exception ex) {
			strErrorDesc = ex.getMessage();
			strErrMsg = strErrMsg + strErrorDesc;
			logger.error(strErrMsg, ex);
		}
		// Return back the error message string
		return strErrMsg;
	}

	@Override
	protected String getAuditString(VisionUsersVb vObject) {
		final String auditDelimiter = vObject.getAuditDelimiter();
		final String auditDelimiterColVal = vObject.getAuditDelimiterColVal();
		StringBuffer strAudit = new StringBuffer("");

		strAudit.append("VISION_ID" + auditDelimiterColVal + vObject.getVisionId());
		strAudit.append(auditDelimiter);

		if (ValidationUtil.isValid(vObject.getUserName()))
			strAudit.append("USER_NAME" + auditDelimiterColVal + vObject.getUserName().trim());
		else
			strAudit.append("USER_NAME" + auditDelimiterColVal + "NULL");
		strAudit.append(auditDelimiter);

		if (ValidationUtil.isValid(vObject.getUserLoginId()))
			strAudit.append("USER_LOGIN_ID" + auditDelimiterColVal + vObject.getUserLoginId().toLowerCase().trim());
		else
			strAudit.append("USER_LOGIN_ID" + auditDelimiterColVal + "NULL");
		strAudit.append(auditDelimiter);

		if (ValidationUtil.isValid(vObject.getUserEmailId()))
			strAudit.append("USER_EMAIL_ID" + auditDelimiterColVal + vObject.getUserEmailId().trim());
		else
			strAudit.append("USER_EMAIL_ID" + auditDelimiterColVal + "NULL");
		strAudit.append(auditDelimiter);

		if (ValidationUtil.isValid(vObject.getLastActivityDate()))
			strAudit.append("LAST_ACTIVITY_DATE" + auditDelimiterColVal + vObject.getLastActivityDate().trim());
		else
			strAudit.append("LAST_ACTIVITY_DATE" + auditDelimiterColVal + "NULL");
		strAudit.append(auditDelimiter);

		strAudit.append("USER_GROUP_AT" + auditDelimiterColVal + vObject.getUserGroupAt());
		strAudit.append(auditDelimiter);

		if (ValidationUtil.isValid(vObject.getUserGroup()))
			strAudit.append("USER_GROUP" + auditDelimiterColVal + vObject.getUserGroup().trim());
		else
			strAudit.append("USER_GROUP" + auditDelimiterColVal + "NULL");
		strAudit.append(auditDelimiter);

		strAudit.append("USER_PROFILE_AT" + auditDelimiterColVal + vObject.getUserProfileAt());
		strAudit.append(auditDelimiter);

		if (ValidationUtil.isValid(vObject.getUserProfile()))
			strAudit.append("USER_PROFILE" + auditDelimiterColVal + vObject.getUserProfile().trim());
		else
			strAudit.append("USER_PROFILE" + auditDelimiterColVal + "NULL");
		strAudit.append(auditDelimiter);

		if (ValidationUtil.isValid(vObject.getUpdateRestriction())
				&& (vObject.getUpdateRestriction().equalsIgnoreCase("Y")
						|| vObject.getUpdateRestriction().equalsIgnoreCase("N")))
			strAudit.append("UPDATE_RESTRICTION" + auditDelimiterColVal + vObject.getUpdateRestriction().trim());
		else
			strAudit.append("UPDATE_RESTRICTION" + auditDelimiterColVal + "NULL");
		strAudit.append(auditDelimiter);

		if (ValidationUtil.isValid(vObject.getCountry()))
			strAudit.append("COUNTRY" + auditDelimiterColVal + vObject.getCountry().trim());
		else
			strAudit.append("COUNTRY" + auditDelimiterColVal + "NULL");
		strAudit.append(auditDelimiter);

		if (ValidationUtil.isValid(vObject.getLeBook()))
			strAudit.append("LE_BOOK" + auditDelimiterColVal + vObject.getLeBook().trim());
		else
			strAudit.append("LE_BOOK" + auditDelimiterColVal + "NULL");
		strAudit.append(auditDelimiter);

		if (ValidationUtil.isValid(vObject.getRegionProvince()))
			strAudit.append("REGION_PROVINCE" + auditDelimiterColVal + vObject.getRegionProvince().trim());
		else
			strAudit.append("REGION_PROVINCE" + auditDelimiterColVal + "NULL");
		strAudit.append(auditDelimiter);

		if (ValidationUtil.isValid(vObject.getBusinessGroup()))
			strAudit.append("BUINESSS_GROUP" + auditDelimiterColVal + vObject.getBusinessGroup().trim());
		else
			strAudit.append("BUINESSS_GROUP" + auditDelimiterColVal + "NULL");
		strAudit.append(auditDelimiter);

		if (ValidationUtil.isValid(vObject.getProductSuperGroup()))
			strAudit.append("PRODUCT_SUPER_GROUP" + auditDelimiterColVal + vObject.getProductSuperGroup().trim());
		else
			strAudit.append("PRODUCT_SUPER_GROUP" + auditDelimiterColVal + "NULL");
		strAudit.append(auditDelimiter);

		if (ValidationUtil.isValid(vObject.getOucAttribute()))
			strAudit.append("OUC_ATTRIBUTE" + auditDelimiterColVal + vObject.getOucAttribute().trim());
		else
			strAudit.append("OUC_ATTRIBUTE" + auditDelimiterColVal + "NULL");
		strAudit.append(auditDelimiter);

		strAudit.append("SBU_CODE_AT" + auditDelimiterColVal + vObject.getSbuCodeAt());
		strAudit.append(auditDelimiter);

		if (ValidationUtil.isValid(vObject.getSbuCode()))
			strAudit.append("SBU_CODE" + auditDelimiterColVal + vObject.getSbuCode().trim());
		else
			strAudit.append("SBU_CODE" + auditDelimiterColVal + "NULL");
		strAudit.append(auditDelimiter);

		if (ValidationUtil.isValid(vObject.getProductAttribute()))
			strAudit.append("PRODUCT_ATTRIBUTE" + auditDelimiterColVal + vObject.getProductAttribute().trim());
		else
			strAudit.append("PRODUCT_ATTRIBUTE" + auditDelimiterColVal + "NULL");
		strAudit.append(auditDelimiter);

		if (ValidationUtil.isValid(vObject.getAccountOfficer()))
			strAudit.append("ACCOUNT_OFFICER" + auditDelimiterColVal + vObject.getAccountOfficer().trim());
		else
			strAudit.append("ACCOUNT_OFFICER" + auditDelimiterColVal + "NULL");
		strAudit.append(auditDelimiter);

		if (ValidationUtil.isValid(vObject.getGcidAccess()))
			strAudit.append("GCID_ACCESS" + auditDelimiterColVal + vObject.getGcidAccess().trim());
		else
			strAudit.append("GCID_ACCESS" + auditDelimiterColVal + "NULL");
		strAudit.append(auditDelimiter);

		if (ValidationUtil.isValid(vObject.getLegalVehicle()))
			strAudit.append("LEGAL_VEHICLE" + auditDelimiterColVal + vObject.getLegalVehicle().trim());
		else
			strAudit.append("LEGAL_VEHICLE" + auditDelimiterColVal + "NULL");
		strAudit.append(auditDelimiter);

		strAudit.append("USER_STATUS_NT" + auditDelimiterColVal + vObject.getUserStatusNt());
		strAudit.append(auditDelimiter);
		strAudit.append("USER_STATUS" + auditDelimiterColVal + vObject.getUserStatus());
		strAudit.append(auditDelimiter);
		if (ValidationUtil.isValid(vObject.getUserStatusDate()))
			strAudit.append("USER_STATUS_DATE" + auditDelimiterColVal + vObject.getUserStatusDate().trim());
		else
			strAudit.append("USER_STATUS_DATE" + auditDelimiterColVal + "NULL");
		strAudit.append(auditDelimiter);
		strAudit.append("RECORD_INDICATOR_NT" + auditDelimiterColVal + vObject.getRecordIndicatorNt());
		strAudit.append(auditDelimiter);
		if (vObject.getRecordIndicator() == -1)
			vObject.setRecordIndicator(0);
		strAudit.append("RECORD_INDICATOR" + auditDelimiterColVal + vObject.getRecordIndicator());
		strAudit.append(auditDelimiter);

		strAudit.append("MAKER" + auditDelimiterColVal + vObject.getMaker());
		strAudit.append(auditDelimiter);
		strAudit.append("VERIFIER" + auditDelimiterColVal + vObject.getVerifier());
		strAudit.append(auditDelimiter);
		if (vObject.getInternalStatus() != 0)
			strAudit.append("INTERNAL_STATUS" + auditDelimiterColVal + vObject.getInternalStatus());
		else
			strAudit.append("INTERNAL_STATUS" + auditDelimiterColVal + "NULL");
		strAudit.append(auditDelimiter);

		if (ValidationUtil.isValid(vObject.getDateLastModified()))
			strAudit.append("DATE_LAST_MODIFIED" + auditDelimiterColVal + vObject.getDateLastModified().trim());
		else
			strAudit.append("DATE_LAST_MODIFIED" + auditDelimiterColVal + "NULL");
		strAudit.append(auditDelimiter);

		if (ValidationUtil.isValid(vObject.getDateCreation()))
			strAudit.append("DATE_CREATION" + auditDelimiterColVal + vObject.getDateCreation().trim());
		else
			strAudit.append("DATE_CREATION" + auditDelimiterColVal + "NULL");
		strAudit.append(auditDelimiter);
		
		if (ValidationUtil.isValid(vObject.getOtherAttr()))
			strAudit.append("OTHER_ATTR" + auditDelimiterColVal + vObject.getOtherAttr().trim());
		else
			strAudit.append("OTHER_ATTR" + auditDelimiterColVal + "NULL");
		strAudit.append(auditDelimiter);
		
		if (ValidationUtil.isValid(vObject.getRaSoc()))
			strAudit.append("RA_SOC" + auditDelimiterColVal + vObject.getRaSoc().trim());
		else
			strAudit.append("RA_SOC" + auditDelimiterColVal + "NULL");
		strAudit.append(auditDelimiter);
		
		strAudit.append("APPLICATION_ACCESS_AT" + auditDelimiterColVal + vObject.getApplicationAccessAt());
		strAudit.append(auditDelimiter);

		if (ValidationUtil.isValid(vObject.getApplicationAccess()))
			strAudit.append("APPLICATION_ACCESS" + auditDelimiterColVal + vObject.getApplicationAccess().trim());
		else
			strAudit.append("APPLICATION_ACCESS" + auditDelimiterColVal + "NULL");
		strAudit.append(auditDelimiter);
		
		return strAudit.toString();
	}

	@Override
	protected void setServiceDefaults() {
		serviceName = "VisionUsers";
		serviceDesc = "Vision Users";
		tableName = "VISION_USERS";
		childTableName = "VISION_USERS";
		intCurrentUserId = CustomContextHolder.getContext().getVisionId();
	}

	@Override
	public void setApplicationContext(ApplicationContext arg0) throws BeansException {
		applicationContext = arg0;

	}

	private int doUpdateImage(VisionUsersVb vObject) {
		String query = "Update VISION_USERS Set USER_PHOTO = (select USER_PHOTO from VISION_USERS_PEND where VISION_ID ="
				+ vObject.getVisionId() + ") " + " Where VISION_ID = ?";
		Object[] args = { vObject.getVisionId() };
		return getJdbcTemplate().update(query, args);
	}

	// Used in Authentication for loading Widgets
	public String getVisionBusinessDay(String country, String leBook) {
		Object args[] = { country, leBook };
		String sql = "";
		if("CAL".equalsIgnoreCase(clientName)) {
			sql = "select FORMAT(REPORT_BUSINESS_DATE,'dd-MM-yyyy') from Vision_Business_Day  WHERE COUNTRY = ? and LE_BOOK=?";
		} else {
			if ("ORACLE".equalsIgnoreCase(databaseType)) {
				sql = "select TO_CHAR(BUSINESS_DATE,'MM/DD/RRRR') BUSINESS_DATE  from Vision_Business_Day  WHERE COUNTRY = ? and LE_BOOK=?";

			} else if ("MSSQL".equalsIgnoreCase(databaseType)) {
				sql = "select FORMAT(BUSINESS_DATE,'dd-MM-yyyy') from Vision_Business_Day  WHERE COUNTRY = ? and LE_BOOK=?";

			}
		}
		return getJdbcTemplate().queryForObject(sql, args, String.class);
	}

	@Override
	protected ExceptionCode doInsertApprRecordForNonTrans(VisionUsersVb vObject) throws RuntimeCustomException {
		List<VisionUsersVb> collTemp = null;
		ExceptionCode exceptionCode = null;
		strCurrentOperation = Constants.ADD;
		strApproveOperation = Constants.ADD;
		setServiceDefaults();
		vObject.setMaker(getIntCurrentUserId());
		collTemp = selectApprovedRecord(vObject);
		if (collTemp == null) {
			logger.error("Collection is null for Select Approved Record");
			exceptionCode = getResultObject(Constants.ERRONEOUS_OPERATION);
			throw buildRuntimeCustomException(exceptionCode);
		}
		// If record already exists in the approved table, reject the addition
		if (collTemp.size() > 0) {
			int intStaticDeletionFlag = getStatus(((ArrayList<VisionUsersVb>) collTemp).get(0));
			if (intStaticDeletionFlag == Constants.PASSIVATE) {
				logger.error("Collection size is greater than zero - Duplicate record found, but inactive");
				exceptionCode = getResultObject(Constants.RECORD_ALREADY_PRESENT_BUT_INACTIVE);
				throw buildRuntimeCustomException(exceptionCode);
			} else {
				logger.error("Collection size is greater than zero - Duplicate record found");
				exceptionCode = getResultObject(Constants.DUPLICATE_KEY_INSERTION);
				throw buildRuntimeCustomException(exceptionCode);
			}
		}
		// Try inserting the record
		vObject.setRecordIndicator(Constants.STATUS_ZERO);
		vObject.setVerifier(getIntCurrentUserId());
		/*
		 * int businessGrpCount = getQueryReviewbusinessGrp(vObject); String[]
		 * businessGroup = vObject.getBusinessGroup().split(","); for(String s :
		 * businessGroup) { retVal = doInsertionAppr(vObject); }
		 */
		if(ValidationUtil.isValid(vObject.getRaSoc()) && vObject.getRaSoc().startsWith(","))
			vObject.setRaSoc(vObject.getRaSoc().substring(1));
		/*if(ValidationUtil.isValid(vObject.getPassword()))
			vObject.setPassword(ValidationUtil.jasyptEncryption(vObject.getPassword()));*/
		
		retVal = doInsertionAppr(vObject);
		if (retVal != Constants.SUCCESSFUL_OPERATION) {
			exceptionCode = getResultObject(retVal);
			throw buildRuntimeCustomException(exceptionCode);
		}
		if (vObject.getFile() != null && vObject.isProilePictureChange()) {
			PreparedStatement pstmt;
			try {
				InputStream fis = vObject.getFile().getInputStream();
				String query = ("update VISION_USERS set USER_PHOTO =  ?  where VISION_ID=?");
				pstmt = getConnection().prepareStatement(query);
				// Method used to insert a stream of bytes
				pstmt.setBinaryStream(1, fis);
				pstmt.setInt(2, vObject.getVisionId());
				pstmt.executeUpdate();

			} catch (Exception ex) {
				exceptionCode.setErrorMsg(ex.getMessage());
				exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
				//strErrorDesc = ex.getMessage();
				strErrorDesc = parseErrorMsg(ex);
				exceptionCode = getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION);
				throw buildRuntimeCustomException(exceptionCode);
			}
		}
		String systemDate = getSystemDate();
		vObject.setDateLastModified(systemDate);
		vObject.setDateCreation(systemDate);
		exceptionCode = writeAuditLog(vObject, null);
		if (exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION) {
			exceptionCode = getResultObject(Constants.AUDIT_TRAIL_ERROR);
			throw buildRuntimeCustomException(exceptionCode);
		}
		exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
		return exceptionCode;
	}

	@Override
	protected ExceptionCode doUpdateApprRecordForNonTrans(VisionUsersVb vObject) throws RuntimeCustomException {
		List<VisionUsersVb> collTemp = null;
		VisionUsersVb vObjectlocal = null;
		ExceptionCode exceptionCode = null;
		strApproveOperation =Constants.MODIFY;
		strErrorDesc  = "";
		strCurrentOperation = Constants.MODIFY;
		setServiceDefaults();
		vObject.setMaker(getIntCurrentUserId());
		/*if("RUNNING".equalsIgnoreCase(getBuildStatus(vObject))){
			exceptionCode = getResultObject(Constants.BUILD_IS_RUNNING);
			throw buildRuntimeCustomException(exceptionCode);
		}*/
		collTemp = selectApprovedRecord(vObject);
		if (collTemp == null){
			exceptionCode = getResultObject(Constants.ERRONEOUS_OPERATION);
			throw buildRuntimeCustomException(exceptionCode);
		}
		vObjectlocal = ((ArrayList<VisionUsersVb>)collTemp).get(0);
		vObject.setDateCreation(vObjectlocal.getDateCreation());
		// Even if record is not there in Appr. table reject the record
		if (collTemp.size() == 0){
			exceptionCode = getResultObject(Constants.ATTEMPT_TO_MODIFY_UNEXISTING_RECORD);
			throw buildRuntimeCustomException(exceptionCode);
		}
		vObject.setRecordIndicator(Constants.STATUS_ZERO);
		vObject.setVerifier(getIntCurrentUserId());
		if(ValidationUtil.isValid(vObject.getRaSoc()) && vObject.getRaSoc().startsWith(","))
			vObject.setRaSoc(vObject.getRaSoc().substring(1));
		vObject.setPasswordResetFlag("Y");
		retVal = doUpdateAppr(vObject);
		if (retVal != Constants.SUCCESSFUL_OPERATION){
			exceptionCode = getResultObject(retVal);
			throw buildRuntimeCustomException(exceptionCode);
		}
		if(vObject.getFile() != null && vObject.isProilePictureChange()){
			PreparedStatement pstmt;
			try
            {
					InputStream fis = vObject.getFile().getInputStream();
                    String query = ("update VISION_USERS set USER_PHOTO =  ?  where VISION_ID=?");
                    pstmt = getConnection().prepareStatement(query);
                    // Method used to insert a stream of bytes
                    pstmt.setBinaryStream(1, fis);
                    pstmt.setInt(2, vObject.getVisionId());
                    pstmt.executeUpdate();

			}catch(Exception ex){
				//strErrorDesc = ex.getMessage();
				strErrorDesc = parseErrorMsg(ex);
				exceptionCode = getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION);
				throw buildRuntimeCustomException(exceptionCode);				
			}
		}	
		String systemDate = getSystemDate();
		vObject.setDateLastModified(systemDate);
		exceptionCode = writeAuditLog(vObject, vObjectlocal);
		if(exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION){
			exceptionCode = getResultObject(Constants.AUDIT_TRAIL_ERROR);
			throw buildRuntimeCustomException(exceptionCode);
		}
		return exceptionCode;
	}

	@Override
	protected ExceptionCode doInsertRecordForNonTrans(VisionUsersVb vObject) throws RuntimeCustomException {
		List<VisionUsersVb> collTemp = null;
		ExceptionCode exceptionCode = null;
		strApproveOperation =Constants.ADD;
		strErrorDesc  = "";
		strCurrentOperation = Constants.ADD;
		setServiceDefaults();
		vObject.setMaker(getIntCurrentUserId());
		collTemp = selectApprovedRecord(vObject);
		if (collTemp == null){
			logger.error("Collection is null for Select Approved Record");
			exceptionCode = getResultObject(Constants.ERRONEOUS_OPERATION);
			throw buildRuntimeCustomException(exceptionCode);
		}
		// If record already exists in the approved table, reject the addition
		if (collTemp.size() > 0 ){
			int staticDeletionFlag = getStatus(((ArrayList<VisionUsersVb>)collTemp).get(0));
			if (staticDeletionFlag == Constants.PASSIVATE){
				logger.info("Collection size is greater than zero - Duplicate record found, but inactive");
				exceptionCode = getResultObject(Constants.RECORD_ALREADY_PRESENT_BUT_INACTIVE);
				throw buildRuntimeCustomException(exceptionCode);
			}
			else
			{
				logger.info("Collection size is greater than zero - Duplicate record found");
				exceptionCode = getResultObject(Constants.DUPLICATE_KEY_INSERTION);
				throw buildRuntimeCustomException(exceptionCode);
			}
		}

		// Try to see if the record already exists in the pending table, but not in approved table
		collTemp = null;
		collTemp = doSelectPendingRecord(vObject);

		// The collTemp variable could not be null.  If so, there is no problem fetching data
		// return back error code to calling routine
		if (collTemp == null){
			exceptionCode = getResultObject(Constants.ERRONEOUS_OPERATION);
			throw buildRuntimeCustomException(exceptionCode);
		}

		// if record already exists in pending table, modify the record
		if (collTemp.size() > 0){
			VisionUsersVb vObjectLocal = ((ArrayList<VisionUsersVb>)collTemp).get(0);
			if (vObjectLocal.getRecordIndicator() == Constants.STATUS_INSERT){
				exceptionCode = getResultObject(Constants.PENDING_FOR_ADD_ALREADY);
				throw buildRuntimeCustomException(exceptionCode);
			}
			exceptionCode = getResultObject(Constants.ERRONEOUS_OPERATION);
			throw buildRuntimeCustomException(exceptionCode);
		}else{
			// Try inserting the record
			vObject.setVerifier(0);
			vObject.setRecordIndicator(Constants.STATUS_INSERT);
			if(ValidationUtil.isValid(vObject.getRaSoc()) && vObject.getRaSoc().startsWith(","))
				vObject.setRaSoc(vObject.getRaSoc().substring(1));
			
			
			/*if(ValidationUtil.isValid(vObject.getPassword()))
				vObject.setPassword(ValidationUtil.jasyptEncryption(vObject.getPassword()));*/
			vObject.setPasswordResetFlag("Y");
			retVal = doInsertionPend(vObject);
			if (retVal != Constants.SUCCESSFUL_OPERATION){
				exceptionCode = getResultObject(retVal);
				throw buildRuntimeCustomException(exceptionCode);
			}
			if(vObject.getFile() != null && vObject.isProilePictureChange()){
				PreparedStatement pstmt;
				try
                {
						InputStream fis = vObject.getFile().getInputStream();
                        String query = ("update VISION_USERS_PEND set USER_PHOTO =  ?  where VISION_ID=?");
                        pstmt = getConnection().prepareStatement(query);
                        // Method used to insert a stream of bytes
                        pstmt.setBinaryStream(1, fis);
                        pstmt.setInt(2, vObject.getVisionId());
                        pstmt.executeUpdate();
 
				}catch(Exception ex){
					//strErrorDesc = ex.getMessage();
					strErrorDesc = parseErrorMsg(ex);
					exceptionCode = getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION);
					throw buildRuntimeCustomException(exceptionCode);				
				}
			}
			exceptionCode = writeAuditLog(vObject, null);
			if(exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION){
				exceptionCode = getResultObject(Constants.AUDIT_TRAIL_ERROR);
				throw buildRuntimeCustomException(exceptionCode);
			}
			exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
			return exceptionCode;
		}
	}

	@Override
	protected ExceptionCode doUpdateRecordForNonTrans(VisionUsersVb vObject) throws RuntimeCustomException {
		List<VisionUsersVb> collTemp = null;
		VisionUsersVb vObjectlocal = null;
		ExceptionCode exceptionCode =null;
		strApproveOperation =Constants.MODIFY;
		strErrorDesc  = "";
		strCurrentOperation = Constants.MODIFY;
		setServiceDefaults();
		vObject.setMaker(getIntCurrentUserId());
		// Search if record already exists in pending.  If it already exists, check for status
		collTemp = doSelectPendingRecord(vObject);
		if (collTemp == null){
			exceptionCode = getResultObject(Constants.ERRONEOUS_OPERATION);
			throw buildRuntimeCustomException(exceptionCode);
		}
		if (collTemp.size() > 0){
			vObjectlocal = ((ArrayList<VisionUsersVb>)collTemp).get(0);

			// Check if the record is pending for deletion. If so return the error
			if (vObjectlocal.getRecordIndicator() == Constants.STATUS_DELETE){
				exceptionCode = getResultObject(Constants.RECORD_PENDING_FOR_DELETION);
				throw buildRuntimeCustomException(exceptionCode);
			}
			if(!ValidationUtil.isValid(vObject.getPassword())) {
		    	List<VisionUsersVb>  userPwdlst = getUserPasswordAppr(vObject);
		    	if(userPwdlst != null && userPwdlst.size() > 0) {
		    		vObject.setPassword(userPwdlst.get(0).getPassword());
			    	vObject.setPasswordResetFlag("Y");
		    	}
		    }else {
		    	vObject.setPasswordResetFlag("Y");
		    }
			if (vObjectlocal.getRecordIndicator() == Constants.STATUS_INSERT){
				vObject.setVerifier(0);
				vObject.setRecordIndicator(Constants.STATUS_INSERT);
				vObject.setDateCreation(vObjectlocal.getDateCreation());
				vObject.setPasswordResetFlag("Y");
				retVal = doUpdatePend(vObject);
			}else{
				vObject.setVerifier(0);
				vObject.setRecordIndicator(Constants.STATUS_UPDATE);
				vObject.setDateCreation(vObjectlocal.getDateCreation());
				if(ValidationUtil.isValid(vObject.getRaSoc()) && vObject.getRaSoc().startsWith(","))
					vObject.setRaSoc(vObject.getRaSoc().substring(1));
				retVal = doUpdatePend(vObject);
			}
			if (retVal != Constants.SUCCESSFUL_OPERATION){
				exceptionCode = getResultObject(retVal);
				throw buildRuntimeCustomException(exceptionCode);
			}
			if(vObject.getFile() != null && vObject.isProilePictureChange()){
				PreparedStatement pstmt;
				try
                {
						InputStream fis = vObject.getFile().getInputStream();
                        String query = ("update VISION_USERS_PEND set USER_PHOTO =  ?  where VISION_ID=?");
                        pstmt = getConnection().prepareStatement(query);
                        // Method used to insert a stream of bytes
                        pstmt.setBinaryStream(1, fis);
                        pstmt.setInt(2, vObject.getVisionId());
                        pstmt.executeUpdate();
 
				}catch(Exception ex){
					//strErrorDesc = ex.getMessage();
					strErrorDesc = parseErrorMsg(ex);
					exceptionCode = getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION);
					throw buildRuntimeCustomException(exceptionCode);				
				}
			}			
			return getResultObject(Constants.SUCCESSFUL_OPERATION);
		}else{
			collTemp = null;
			collTemp = selectApprovedRecord(vObject);

			if (collTemp == null){
				exceptionCode = getResultObject(Constants.ERRONEOUS_OPERATION);
				throw buildRuntimeCustomException(exceptionCode);
			}

			// Even if record is not there in Appr. table reject the record
			if (collTemp.size() == 0){
				exceptionCode = getResultObject(Constants.ATTEMPT_TO_MODIFY_UNEXISTING_RECORD);
				throw buildRuntimeCustomException(exceptionCode);
			}
			//This is required for Audit Trail.
			if (collTemp.size() > 0){
				vObjectlocal = ((ArrayList<VisionUsersVb>)collTemp).get(0);
				vObject.setDateCreation(vObjectlocal.getDateCreation());
			}
		    vObject.setDateCreation(vObjectlocal.getDateCreation());
		 // Record is there in approved, but not in pending.  So add it to pending
		    vObject.setVerifier(0);
		    vObject.setRecordIndicator(Constants.STATUS_UPDATE);
		    if(!ValidationUtil.isValid(vObject.getPassword())) {
		    	List<VisionUsersVb>  userPwdlst = getUserPasswordAppr(vObject);
		    	if(userPwdlst != null && userPwdlst.size() > 0) {
		    		vObject.setPassword(userPwdlst.get(0).getPassword());
			    	vObject.setPasswordResetFlag("Y");
		    	}
		    }else {
		    	vObject.setPasswordResetFlag("Y");
		    }
		    retVal = doInsertionPendWithDc(vObject);
			if (retVal != Constants.SUCCESSFUL_OPERATION){
				exceptionCode = getResultObject(retVal);
				throw buildRuntimeCustomException(exceptionCode);
			}
			if (vObject.getFile() != null && vObject.isProilePictureChange()) {
				PreparedStatement pstmt;
				try {
					InputStream fis = vObject.getFile().getInputStream();
					String query = ("update VISION_USERS_PEND set USER_PHOTO =  ?  where VISION_ID=?");
					pstmt = getConnection().prepareStatement(query);
					// Method used to insert a stream of bytes
					pstmt.setBinaryStream(1, fis);
					pstmt.setInt(2, vObject.getVisionId());
					pstmt.executeUpdate();

				} catch (Exception ex) {
					//strErrorDesc = ex.getMessage();
					strErrorDesc = parseErrorMsg(ex);
					exceptionCode = getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION);
					throw buildRuntimeCustomException(exceptionCode);
				}
			}	
			exceptionCode = getResultObject(Constants.SUCCESSFUL_OPERATION);
			return exceptionCode;
		}
	}

	@Override
	public ExceptionCode doApproveRecord(VisionUsersVb vObject, boolean staticDelete) throws RuntimeCustomException {
		VisionUsersVb oldContents = null;
		VisionUsersVb vObjectlocal = null;
		List<VisionUsersVb> collTemp = null;
		ExceptionCode exceptionCode = null;
		strCurrentOperation = Constants.APPROVE;
		setServiceDefaults();
		try {
			// See if such a pending request exists in the pending table
			vObject.setVerifier(getIntCurrentUserId());
			vObject.setRecordIndicator(Constants.STATUS_ZERO);
			collTemp = doSelectPendingRecord(vObject);
			if (collTemp == null){
				exceptionCode = getResultObject(Constants.ERRONEOUS_OPERATION);
				throw buildRuntimeCustomException(exceptionCode);
			}

			if (collTemp.size() == 0){
				exceptionCode = getResultObject(Constants.NO_SUCH_PENDING_RECORD);
				throw buildRuntimeCustomException(exceptionCode);
			}

			vObjectlocal = ((ArrayList<VisionUsersVb>)collTemp).get(0);
			List<VisionUsersVb>  userPwdlst = getUserPasswordPend(vObject);
	    	if(userPwdlst != null && userPwdlst.size() > 0) {
	    		vObjectlocal.setPassword(userPwdlst.get(0).getPassword());
	    		vObjectlocal.setPasswordResetFlag("Y");
	    	}
			if (vObjectlocal.getMaker() == getIntCurrentUserId()){
				exceptionCode = getResultObject(Constants.MAKER_CANNOT_APPROVE);
				throw buildRuntimeCustomException(exceptionCode);
			}
			
			// If it's NOT addition, collect the existing record contents from the
			// Approved table and keep it aside, for writing audit information later.
			if (vObjectlocal.getRecordIndicator() != Constants.STATUS_INSERT){
				collTemp = selectApprovedRecord(vObject);
				if (collTemp == null || collTemp.isEmpty()){
					exceptionCode = getResultObject(Constants.ERRONEOUS_OPERATION);
					throw buildRuntimeCustomException(exceptionCode);
				}
				oldContents = ((ArrayList<VisionUsersVb>)collTemp).get(0);
			}

			if (vObjectlocal.getRecordIndicator() == Constants.STATUS_INSERT){  // Add authorization
				// Write the contents of the Pending table record to the Approved table
				vObjectlocal.setRecordIndicator(Constants.STATUS_ZERO);
				vObjectlocal.setVerifier(getIntCurrentUserId());
				
				/*if(ValidationUtil.isValid(vObject.getPassword()))
					vObjectlocal.setPassword(ValidationUtil.jasyptEncryption(vObject.getPassword()));*/
				
				retVal = doInsertionAppr(vObjectlocal);
				if (retVal != Constants.SUCCESSFUL_OPERATION){
					exceptionCode = getResultObject(retVal);
					throw buildRuntimeCustomException(exceptionCode);
				}
				doUpdateImage(vObject);
				String systemDate = getSystemDate();
				vObject.setDateLastModified(systemDate);
				vObject.setDateCreation(systemDate);
				strApproveOperation =Constants.ADD;
			}else if (vObjectlocal.getRecordIndicator() == Constants.STATUS_UPDATE){ // Modify authorization
			
				collTemp = selectApprovedRecord(vObject);
				if (collTemp == null || collTemp.isEmpty()){
					exceptionCode = getResultObject(Constants.ERRONEOUS_OPERATION);
					throw buildRuntimeCustomException(exceptionCode);
				}	

				// If record already exists in the approved table, reject the addition
				if (collTemp.size() > 0 ){
					//retVal = doUpdateAppr(vObjectlocal, MISConstants.ACTIVATE);
					vObjectlocal.setVerifier(getIntCurrentUserId());
					vObjectlocal.setRecordIndicator(Constants.STATUS_ZERO);
					retVal = doUpdateMainTable(vObjectlocal);
				}
				// Modify the existing contents of the record in Approved table
				if (retVal != Constants.SUCCESSFUL_OPERATION){
					exceptionCode = getResultObject(retVal);
					throw buildRuntimeCustomException(exceptionCode);
				}
				doUpdateImage(vObject);
				String systemDate = getSystemDate();
				vObject.setDateLastModified(systemDate);
				// Set the current operation to write to audit log
				strApproveOperation = Constants.MODIFY;
			}
			else if (vObjectlocal.getRecordIndicator() == Constants.STATUS_DELETE){ // Delete authorization
				if(staticDelete){
					// Update the existing record status in the Approved table to delete 
					setStatus(vObjectlocal, Constants.PASSIVATE);
					vObjectlocal.setRecordIndicator(Constants.STATUS_ZERO);
					vObjectlocal.setVerifier(getIntCurrentUserId());
					retVal = doUpdateMainTable(vObjectlocal);
					if (retVal != Constants.SUCCESSFUL_OPERATION){
						exceptionCode = getResultObject(retVal);
						throw buildRuntimeCustomException(exceptionCode);
					}
					setStatus(vObject, Constants.PASSIVATE);
					String systemDate = getSystemDate();
					vObject.setDateLastModified(systemDate);

				}else{
					// Delete the existing record from the Approved table 
					retVal = doDeleteAppr(vObjectlocal);
					if (retVal != Constants.SUCCESSFUL_OPERATION){
						exceptionCode = getResultObject(retVal);
						throw buildRuntimeCustomException(exceptionCode);
					}
					String systemDate = getSystemDate();
					vObject.setDateLastModified(systemDate);
				}
				// Set the current operation to write to audit log
				strApproveOperation = Constants.DELETE;
			}
			else{
				exceptionCode = getResultObject(Constants.INVALID_STATUS_FLAG_IN_DATABASE);
				throw buildRuntimeCustomException(exceptionCode);
			}	     

			// Delete the record from the Pending table
			retVal = deletePendingRecord(vObjectlocal);

			if (retVal != Constants.SUCCESSFUL_OPERATION){
				exceptionCode = getResultObject(retVal);
				throw buildRuntimeCustomException(exceptionCode);
			}

			// Set the internal status to Approved
			vObject.setInternalStatus(0);
			vObject.setRecordIndicator(Constants.STATUS_ZERO);
			if (vObjectlocal.getRecordIndicator() == Constants.STATUS_DELETE && !staticDelete){
				exceptionCode = writeAuditLog(null, oldContents);
				vObject.setRecordIndicator(-1);
			}
			else
				exceptionCode = writeAuditLog(vObjectlocal, oldContents);

			if(exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION){
				exceptionCode = getResultObject(Constants.AUDIT_TRAIL_ERROR);
				throw buildRuntimeCustomException(exceptionCode);
			}
			exceptionCode = getResultObject(Constants.SUCCESSFUL_OPERATION);
			return exceptionCode;
		}catch (UncategorizedSQLException uSQLEcxception) {
			strErrorDesc = parseErrorMsg(uSQLEcxception);
			exceptionCode = getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION);
			throw buildRuntimeCustomException(exceptionCode);
		}catch(Exception ex){
			logger.error("Error in Approve.",ex);
			//logger.error( ((vObject==null)? "vObject is Null":vObject.toString()));
			//strErrorDesc = ex.getMessage();
			strErrorDesc = parseErrorMsg(ex);
			exceptionCode = getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION);
			throw buildRuntimeCustomException(exceptionCode);
		}
	}

	public String getDefaultLegalVehicle(String countryLeBook) {
		return getJdbcTemplate().queryForObject(
				"SELECT LEGAL_VEHICLE FROM LE_BOOK WHERE COUNTRY||'-'||LE_BOOK = '" + countryLeBook + "'",
				String.class);
	}

	public String getUnsuccessfulLoginAttempts(String userId) {
		String sql = "select UNSUCCESSFUL_LOGIN_ATTEMPTS from vision_users WHERE UPPER(USER_LOGIN_ID)='"
				+ userId.toUpperCase() + "'";
		RowMapper mapper = new RowMapper() {
			@Override
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				return (rs.getString("UNSUCCESSFUL_LOGIN_ATTEMPTS"));
			}
		};
		return (String) getJdbcTemplate().queryForObject(sql, null, mapper);
	}

	public String getNonActiveUsers(String userId) throws DataAccessException {
		if (!ValidationUtil.isValid(userId)) {
			return null;
		}
		String sql = "select USER_STATUS FROM vision_users where UPPER(USER_LOGIN_ID) = UPPER(?)";
		Object[] lParams = new Object[1];
		lParams[0] = userId.toUpperCase();
		ResultSetExtractor<String> rse = new ResultSetExtractor<String>() {
			@Override
			public String extractData(ResultSet rs) throws SQLException, DataAccessException {
				String returnStatus = "";
				if (rs.next()) {
					returnStatus = rs.getString("USER_STATUS");
				}
				return ValidationUtil.isValid(returnStatus) ? returnStatus : null;
			}
		};
		return getJdbcTemplate().query(sql, lParams, rse);
	}

	public List<VisionUsersVb> getPromptDataByUserLoginId(VisionUsersVb dObj) {

		Object objParams[] = new Object[1];
		StringBuffer strQueryAppr = new StringBuffer(
				"SELECT NVL(T1.LEGAL_VEHICLE,T2.LEGAL_VEHICLE) LEGAL_VEHICLE, T1.COUNTRY,T1.LE_BOOK, "
						+ " NVL(T1.VISION_OUC,'zzzz') VISION_OUC, "
						+ " (CASE when T1.VISION_OUC is not null Then (select X2.VISION_OUC ||' - '||ouc_description from OUC_CODES x2 where X2.VISION_OUC=t1.vision_ouc) else T2.LEB_DESCRIPTION end) LEB_DESCRIPTION FROM ("
						+ " select NVL(COUNTRY,CASE WHEN OUC_ATTRIBUTE IS NOT NULL THEN SUBSTR(OUC_ATTRIBUTE, 1,2) "
						+ " WHEN LEGAL_VEHICLE IS NOT NULL THEN (select  COUNTRY from LE_BOOK X1"
						+ " WHERE   X1.LEB_STATUS=0 AND X1.RECORD_INDICATOR=0"
						+ " AND X1.LEGAL_VEHICLE= T2.LEGAL_VEHICLE) " + " ELSE 'ZZ' END) COUNTRY,"
						+ " NVL(LE_BOOK,CASE WHEN OUC_ATTRIBUTE IS NOT NULL THEN SUBSTR(OUC_ATTRIBUTE, 3,2) "
						+ " WHEN LEGAL_VEHICLE IS NOT NULL THEN (select LE_BOOK from LE_BOOK X1 "
						+ " WHERE   X1.LEB_STATUS=0 AND X1.RECORD_INDICATOR=0 "
						+ " AND X1.LEGAL_VEHICLE= T2.LEGAL_VEHICLE) " + " ELSE '99' END) LE_BOOK , "
						+ " LEGAL_VEHICLE LEGAL_VEHICLE, " + " OUC_ATTRIBUTE VISION_OUC "
						+ " from VISION_USERS T2 WHERE UPPER(USER_LOGIN_ID)=?) t1 , LE_BOOk T2 "
						+ " WHERE T1.COUNTRY=T2.COUNTRY AND T1.LE_BOOK=T2.LE_BOOK");
		try {
			objParams[0] = dObj.getUserLoginId();
			return getJdbcTemplate().query(strQueryAppr.toString(), objParams, getPromptDataMapper());
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("Error: getQueryResults Exception :   ");
			/*if (objParams != null)
				for (int i = 0; i < objParams.length; i++)
					//logger.error("objParams[" + i + "]" + objParams[i].toString());
*/			return null;
		}
	}

	private RowMapper getPromptDataMapper() {
		RowMapper mapper = new RowMapper() {
			@Override
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				VisionUsersVb visionUsersVb = new VisionUsersVb();
				if (ValidationUtil.isValid(rs.getString("LE_BOOK")))
					visionUsersVb.setLeBook(rs.getString("LE_BOOK").trim());
				if (ValidationUtil.isValid(rs.getString("COUNTRY")))
					visionUsersVb.setCountry(rs.getString("COUNTRY").trim());
				if (ValidationUtil.isValid(rs.getString("LEGAL_VEHICLE")))
					visionUsersVb.setLegalVehicle(rs.getString("LEGAL_VEHICLE").trim());
				if (ValidationUtil.isValid(rs.getString("VISION_OUC")))
					visionUsersVb.setOucAttribute(rs.getString("VISION_OUC").trim());
				if (ValidationUtil.isValid(rs.getString("LEB_DESCRIPTION")))
					visionUsersVb.setLeBookDesc(rs.getString("LEB_DESCRIPTION").trim());
				return visionUsersVb;
			}
		};
		return mapper;
	}

	public VisionUsersVb getUserByLoginId(VisionUsersVb vObj) {
		String sql = "";
		if ("ORACLE".equalsIgnoreCase(databaseType)) {
			sql  = new String("Select TAppr.VISION_ID," + "TAppr.USER_NAME, TAppr.USER_LOGIN_ID, TAppr.USER_EMAIL_ID,"
					+ "To_Char(TAppr.LAST_ACTIVITY_DATE, 'DD-MM-YYYY HH24:MI:SS') LAST_ACTIVITY_DATE, TAppr.USER_GROUP_AT,"
					+ "TAppr.USER_GROUP, TAppr.USER_PROFILE_AT, TAppr.USER_PROFILE,"
					+ "TAppr.UPDATE_RESTRICTION, TAppr.LEGAL_VEHICLE, (Select LV_DESCRIPTION From LEGAL_VEHICLES Where LEGAL_VEHICLE = TAppr.LEGAL_VEHICLE) AS LegalVehicleDesc,"
					+ "TAppr.COUNTRY, TAppr.LE_BOOK,"
					+ "TAppr.REGION_PROVINCE, TAppr.BUSINESS_GROUP, TAppr.PRODUCT_SUPER_GROUP,"
					+ "TAppr.OUC_ATTRIBUTE, TAppr.SBU_CODE_AT,TAppr.SBU_CODE, TAppr.PRODUCT_ATTRIBUTE,"
					+ "TAppr.ACCOUNT_OFFICER, TAppr.GCID_ACCESS, TAppr.USER_STATUS_NT, TAppr.USER_STATUS,"
					+ "To_Char(TAppr.USER_STATUS_DATE, 'DD-MM-YYYY HH24:MI:SS') USER_STATUS_DATE, TAppr.MAKER,"
					+ "TAppr.VERIFIER, TAppr.INTERNAL_STATUS, TAppr.RECORD_INDICATOR_NT,"
					+ "TAppr.RECORD_INDICATOR, To_Char(TAppr.DATE_LAST_MODIFIED, 'DD-MM-YYYY HH24:MI:SS') DATE_LAST_MODIFIED,"
					+ "To_Char(TAppr.DATE_CREATION, 'DD-MM-YYYY HH24:MI:SS') DATE_CREATION,"
					+ "To_Char(TAppr.LAST_UNSUCCESSFUL_LOGIN_DATE, 'DD-MM-YYYY HH24:MI:SS') LAST_UNSUCCESSFUL_LOGIN_DATE, TAppr.UNSUCCESSFUL_LOGIN_ATTEMPTS, TAppr.FILE_NAME, TAppr.USER_PHOTO, TAppr.ENABLE_WIDGETS "
					+ " From VISION_USERS TAppr Where TAppr.USER_LOGIN_ID = ? ");
		} else if ("MSSQL".equalsIgnoreCase(databaseType)) {
			sql = new String("Select TAppr.VISION_ID," + "TAppr.USER_NAME, TAppr.USER_LOGIN_ID, TAppr.USER_EMAIL_ID,"
					+ "Format(TAppr.LAST_ACTIVITY_DATE, 'dd-MM-yyyy HH:mm:ss') LAST_ACTIVITY_DATE, TAppr.USER_GROUP_AT,"
					+ "TAppr.USER_GROUP, TAppr.USER_PROFILE_AT, TAppr.USER_PROFILE,"
					+ "TAppr.UPDATE_RESTRICTION, TAppr.LEGAL_VEHICLE, (Select LV_DESCRIPTION From LEGAL_VEHICLES Where LEGAL_VEHICLE = TAppr.LEGAL_VEHICLE) AS LegalVehicleDesc,"
					+ "TAppr.COUNTRY, TAppr.LE_BOOK,"
					+ "TAppr.REGION_PROVINCE, TAppr.BUSINESS_GROUP, TAppr.PRODUCT_SUPER_GROUP,"
					+ "TAppr.OUC_ATTRIBUTE, TAppr.SBU_CODE_AT,TAppr.SBU_CODE, TAppr.PRODUCT_ATTRIBUTE,"
					+ "TAppr.ACCOUNT_OFFICER, TAppr.GCID_ACCESS, TAppr.USER_STATUS_NT, TAppr.USER_STATUS,"
					+ "Format(TAppr.USER_STATUS_DATE, 'dd-MM-yyyy HH:mm:ss') USER_STATUS_DATE, TAppr.MAKER,"
					+ "TAppr.VERIFIER, TAppr.INTERNAL_STATUS, TAppr.RECORD_INDICATOR_NT,"
					+ "TAppr.RECORD_INDICATOR, Format(TAppr.DATE_LAST_MODIFIED, 'dd-MM-yyyy HH:mm:ss') DATE_LAST_MODIFIED,"
					+ "Format(TAppr.DATE_CREATION, 'dd-MM-yyyy HH:mm:ss') DATE_CREATION,"
					+ "Format(TAppr.LAST_UNSUCCESSFUL_LOGIN_DATE, 'dd-MM-yyyy HH:mm:ss') LAST_UNSUCCESSFUL_LOGIN_DATE, TAppr.UNSUCCESSFUL_LOGIN_ATTEMPTS, TAppr.FILE_NAME, TAppr.USER_PHOTO, TAppr.ENABLE_WIDGETS "
					+ " From VISION_USERS TAppr Where TAppr.USER_LOGIN_ID = ? ");
		}
		 
		
		Object arr[] = { vObj.getUserLoginId() };
		ResultSetExtractor<VisionUsersVb> rse = new ResultSetExtractor<VisionUsersVb>() {
			@Override
			public VisionUsersVb extractData(ResultSet rs) throws SQLException, DataAccessException {
				if (rs.next()) {
					VisionUsersVb visionUsersVb = new VisionUsersVb();
					visionUsersVb.setVisionId(rs.getInt("VISION_ID"));
					if (ValidationUtil.isValid(rs.getString("USER_NAME")))
						visionUsersVb.setUserName(rs.getString("USER_NAME"));
					if (ValidationUtil.isValid(rs.getString("USER_LOGIN_ID")))
						visionUsersVb.setUserLoginId(rs.getString("USER_LOGIN_ID"));
					if (ValidationUtil.isValid(rs.getString("USER_EMAIL_ID")))
						visionUsersVb.setUserEmailId(rs.getString("USER_EMAIL_ID"));
					if (ValidationUtil.isValid(rs.getString("USER_STATUS_DATE")))
						visionUsersVb.setUserStatusDate(rs.getString("USER_STATUS_DATE"));
					visionUsersVb.setUserStatusNt(rs.getInt("USER_STATUS_NT"));
					visionUsersVb.setUserStatus(rs.getInt("USER_STATUS"));
					visionUsersVb.setUserProfileAt(rs.getInt("USER_PROFILE_AT"));
					if (ValidationUtil.isValid(rs.getString("USER_PROFILE")))
						visionUsersVb.setUserProfile(rs.getString("USER_PROFILE").trim());
					visionUsersVb.setUserGroupAt(rs.getInt("USER_GROUP_AT"));
					if (ValidationUtil.isValid(rs.getString("USER_GROUP")))
						visionUsersVb.setUserGroup(rs.getString("USER_GROUP").trim());
					if (ValidationUtil.isValid(rs.getString("LAST_ACTIVITY_DATE")))
						visionUsersVb.setLastActivityDate(rs.getString("LAST_ACTIVITY_DATE").trim());
					if (ValidationUtil.isValid(rs.getString("UPDATE_RESTRICTION")))
						visionUsersVb.setUpdateRestriction(rs.getString("UPDATE_RESTRICTION").trim());
					if (ValidationUtil.isValid(rs.getString("LE_BOOK")))
						visionUsersVb.setLeBook(rs.getString("LE_BOOK").trim());
					if (ValidationUtil.isValid(rs.getString("COUNTRY")))
						visionUsersVb.setCountry(rs.getString("COUNTRY").trim());
					if (ValidationUtil.isValid(rs.getString("LEGAL_VEHICLE")))
						visionUsersVb.setLegalVehicle(rs.getString("LEGAL_VEHICLE").trim());
					if (ValidationUtil.isValid(rs.getString("LegalVehicleDesc")))
						visionUsersVb.setLegalVehicleDesc(rs.getString("LegalVehicleDesc").trim());
					if (ValidationUtil.isValid(rs.getString("PRODUCT_ATTRIBUTE")))
						visionUsersVb.setProductAttribute(rs.getString("PRODUCT_ATTRIBUTE").trim());
					if (ValidationUtil.isValid(rs.getString("SBU_CODE")))
						visionUsersVb.setSbuCode(rs.getString("SBU_CODE").trim());
					visionUsersVb.setSbuCodeAt(rs.getInt("SBU_CODE_AT"));
					if (ValidationUtil.isValid(rs.getString("OUC_ATTRIBUTE")))
						visionUsersVb.setOucAttribute(rs.getString("OUC_ATTRIBUTE").trim());
					if (ValidationUtil.isValid(rs.getString("PRODUCT_SUPER_GROUP")))
						visionUsersVb.setProductSuperGroup(rs.getString("PRODUCT_SUPER_GROUP").trim());
					if (ValidationUtil.isValid(rs.getString("BUSINESS_GROUP")))
						visionUsersVb.setBusinessGroup(rs.getString("BUSINESS_GROUP").trim());
					if (ValidationUtil.isValid(rs.getString("ACCOUNT_OFFICER")))
						visionUsersVb.setAccountOfficer(rs.getString("ACCOUNT_OFFICER").trim());
					if (ValidationUtil.isValid(rs.getString("REGION_PROVINCE")))
						visionUsersVb.setRegionProvince(rs.getString("REGION_PROVINCE").trim());
					if (ValidationUtil.isValid(rs.getString("GCID_ACCESS"))) {
						visionUsersVb.setGcidAccess(rs.getString("GCID_ACCESS").trim());
					}
					visionUsersVb.setRecordIndicatorNt(rs.getInt("RECORD_INDICATOR_NT"));
					visionUsersVb.setRecordIndicator(rs.getInt("RECORD_INDICATOR"));
					visionUsersVb.setMaker(rs.getLong("MAKER"));
					visionUsersVb.setVerifier(rs.getLong("VERIFIER"));
					visionUsersVb.setInternalStatus(rs.getInt("INTERNAL_STATUS"));
					if (ValidationUtil.isValid(rs.getString("DATE_CREATION")))
						visionUsersVb.setDateCreation(rs.getString("DATE_CREATION"));
					if (ValidationUtil.isValid(rs.getString("DATE_LAST_MODIFIED")))
						visionUsersVb.setDateLastModified(rs.getString("DATE_LAST_MODIFIED"));
					if (ValidationUtil.isValid(rs.getString("LAST_UNSUCCESSFUL_LOGIN_DATE")))
						visionUsersVb.setLastUnsuccessfulLoginDate(rs.getString("LAST_UNSUCCESSFUL_LOGIN_DATE"));
					if (ValidationUtil.isValid(rs.getString("UNSUCCESSFUL_LOGIN_ATTEMPTS")))
						visionUsersVb.setLastUnsuccessfulLoginAttempts(rs.getString("UNSUCCESSFUL_LOGIN_ATTEMPTS"));
					if (ValidationUtil.isValid(rs.getString("ENABLE_WIDGETS")))
						visionUsersVb.setEnableWidgets(rs.getString("ENABLE_WIDGETS"));
					return visionUsersVb;
				} else {
					return null;
				}
			}
		};

		return getJdbcTemplate().query(sql, arr, rse);
	}

	public VisionUsersVb callProcToPopulateForgotPasswordEmail(VisionUsersVb vObj, String resultForgotBy) {
		strCurrentOperation = "Query";
		strErrorDesc = "";
		try (Connection con = getConnection();
				CallableStatement cs = con.prepareCall("{call PR_FORGOT_PWD_CHK_V1(?, ? , ?, ?)}");) {
			cs.setString(1, vObj.getUserEmailId());// usermailID
			cs.setString(2, resultForgotBy);// Based on Username/Password
			cs.registerOutParameter(3, java.sql.Types.VARCHAR); // Status
			cs.registerOutParameter(4, java.sql.Types.VARCHAR); // Error Message
			/*
			 * p_Status = 0, if procedure executes successfully. P_ErrorMsg will contain
			 * nothing in this case p_Status = 1, Procedure has fetched NO records for the
			 * given query criteria. P_ErrorMsg will contain nothing. p_Status = 2, You Must
			 * Change Your Password Before Logging In For The First Time. p_Status = 3, Your
			 * Password Has Been Expired.
			 */
			ResultSet rs = cs.executeQuery();
			vObj.setStatus(cs.getString(3));
			vObj.setErrorMessage(cs.getString(4));
			rs.close();
		} catch (Exception ex) {
			ex.printStackTrace();
			strErrorDesc = ex.getMessage().trim();
			throw buildRuntimeCustomException(getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION));
		}
		return vObj;
	}

	public List<VisionUsersVb> findUserIdByDesc(String val) throws DataAccessException {
		String sql = "SELECT VISION_ID FROM Vision_Users WHERE UPPER(USER_NAME) " + val + " ORDER BY USER_NAME";
		RowMapper mapper = new RowMapper() {
			@Override
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				VisionUsersVb visionUsersVb = new VisionUsersVb();
				visionUsersVb.setVisionId(rs.getInt("VISION_ID"));
				return visionUsersVb;
			}
		};
		List<VisionUsersVb> visionUsersVb = getJdbcTemplate().query(sql, mapper);
		return visionUsersVb;
	}

	// Concept of UBA changes
	public Map<String, List> doUpdateRestrictionToUserObject(VisionUsersVb vObject, Map<String, List> restrictionTree) {
		Object args[] = { vObject.getVisionId() };
		for (Map.Entry<String, List> entry : restrictionTree.entrySet()) {
			List categoryData = entry.getValue();
			String sql = (String) categoryData.get(1);
			String restrictedValue = userObjectUpdate(vObject, entry.getKey(),
					getJdbcTemplate().query(sql, args, getMapperRestriction()));
			categoryData.add(restrictedValue);
			restrictionTree.put(entry.getKey(), categoryData);
		}
		return restrictionTree;
	}

	private String userObjectUpdate(VisionUsersVb vObject, String category, List<AlphaSubTabVb> valueLst) {
		StringBuffer restrictValue = new StringBuffer();
		if (valueLst != null && valueLst.size() > 0) {
			restrictValue = formInConditionWithResultListForRestriction(valueLst);
			switch (category) {
			case "COUNTRY":
				restrictValue = new StringBuffer();
				Set countrySet = new HashSet();
				for (AlphaSubTabVb vObj : valueLst) {
					countrySet.add((vObj.getAlphaSubTab().split("-"))[0]);
				}
				int idx = 0;
				for (Object country : countrySet) {
					restrictValue.append("'" + country + "'");
					if (idx != countrySet.size() - 1)
						restrictValue.append(",");
					idx++;
				}
				break;
			case "COUNTRY-LE_BOOK":
				vObject.setCountry((restrictValue != null) ? String.valueOf(restrictValue) : "");
				break;
			case "COUNTRY-LE_BOOK-ACCOUNT_OFFICER":
				vObject.setAccountOfficer((restrictValue != null) ? String.valueOf(restrictValue) : "");
				break;
			case "COUNTRY-LE_BOOK-STAFF":
				vObject.setStaffId((restrictValue != null) ? String.valueOf(restrictValue) : "");
				break;
			case "LEGAL_VEHICLE":
				vObject.setLegalVehicle((restrictValue != null) ? String.valueOf(restrictValue) : "");
				break;
			case "LEGAL_VEHICLE-COUNTRY-LE_BOOK":
				vObject.setLegalVehicleCleb((restrictValue != null) ? String.valueOf(restrictValue) : "");
				break;
			case "OUC":
				vObject.setOucAttribute((restrictValue != null) ? String.valueOf(restrictValue) : "");
				break;
			case "PRODUCT":
				vObject.setProductAttribute((restrictValue != null) ? String.valueOf(restrictValue) : "");
				break;
			case "SBU":
				vObject.setSbuCode((restrictValue != null) ? String.valueOf(restrictValue) : "");
				break;
			default:
				break;
			}
		} else {
			restrictValue = null;
		}
		return (restrictValue != null) ? String.valueOf(restrictValue) : null;
	}

	private StringBuffer formInConditionWithResultListForRestriction(List<AlphaSubTabVb> valueLst) {
		StringBuffer restrictValue = new StringBuffer();
		if (valueLst != null && valueLst.size() > 0) {
			int idx = 1;
			for (AlphaSubTabVb vObj : valueLst) {
				restrictValue.append("'" + vObj.getAlphaSubTab() + "'");
				if (idx < valueLst.size())
					restrictValue.append(",");
				idx++;
			}
		} else {
			restrictValue = null;
		}
		return restrictValue;
	}

	/* Update SQL Query */
	public List<VisionUsersVb> getActiveUserByUserLoginIdNew(VisionUsersVb dObj) {
		StringBuffer strQueryAppr = null;
		Object objParams[] = new Object[1];
		if ("oracle".equalsIgnoreCase(databaseType)) {
			strQueryAppr = new StringBuffer("Select TAppr.VISION_ID,"
					+ " SUBSTR(LINK_CLEB_STAFFID,0,2) STF_COUNTRY,SUBSTR(LINK_CLEB_STAFFID,INSTR(LINK_CLEB_STAFFID,'-',1,1)+1,2) STF_LE_BOOK,SUBSTR(LINK_CLEB_STAFFID,INSTR(LINK_CLEB_STAFFID,'-',1,2)+1) STAFF_ID,"
					+ " TAppr.USER_NAME, TAppr.USER_LOGIN_ID, TAppr.USER_EMAIL_ID, "
					+ " To_Char(TAppr.LAST_ACTIVITY_DATE, 'DD-MM-YYYY HH24:MI:SS') LAST_ACTIVITY_DATE, "
					+ " TAppr.UPDATE_RESTRICTION,TAppr.AUTO_UPDATE_RESTRICTION,TAppr.LEGAL_VEHICLE, (Select LV_DESCRIPTION From LEGAL_VEHICLES Where LEGAL_VEHICLE = TAppr.LEGAL_VEHICLE) AS LegalVehicleDesc,"
					+ " TAppr.GCID_ACCESS, TAppr.USER_STATUS_NT, TAppr.USER_STATUS,"
					+ " To_Char(TAppr.USER_STATUS_DATE, 'DD-MM-YYYY HH24:MI:SS') USER_STATUS_DATE, TAppr.MAKER,"
					+ " TAppr.VERIFIER, TAppr.INTERNAL_STATUS, TAppr.RECORD_INDICATOR_NT,"
					+ " TAppr.RECORD_INDICATOR, To_Char(TAppr.DATE_LAST_MODIFIED, 'DD-MM-YYYY HH24:MI:SS') DATE_LAST_MODIFIED,"
					+ " To_Char(TAppr.DATE_CREATION, 'DD-MM-YYYY HH24:MI:SS') DATE_CREATION,"
					+ " To_Char(TAppr.LAST_UNSUCCESSFUL_LOGIN_DATE, 'DD-MM-YYYY HH24:MI:SS') LAST_UNSUCCESSFUL_LOGIN_DATE, TAppr.UNSUCCESSFUL_LOGIN_ATTEMPTS, TAppr.FILE_NAME, TAppr.USER_PHOTO, "
					+ " TAppr.ENABLE_WIDGETS, TAppr.PREF_LANGUAGE,(SELECT App_Theme FROM PRD_APP_THEME S1 WHERE S1.VISION_ID=TAppr.VISION_ID) APP_THEME,  "
					+ " (SELECT Report_Slide_Theme FROM PRD_APP_THEME S1 WHERE S1.VISION_ID=TAppr.VISION_ID) Report_Slide_Theme, "
					+ " (SELECT Language FROM PRD_APP_THEME S1 WHERE S1.VISION_ID=TAppr.VISION_ID) Language "
					+ " From VISION_USERS TAppr WHERE USER_STATUS = 0 AND RECORD_INDICATOR = 0 AND UPPER(USER_LOGIN_ID) =UPPER(?) AND (UNSUCCESSFUL_LOGIN_ATTEMPTS is NULL OR UNSUCCESSFUL_LOGIN_ATTEMPTS <= 3)");
		} else if ("MSSQL".equalsIgnoreCase(databaseType)) {
			strQueryAppr = new StringBuffer(
					"SELECT TAppr.VISION_ID,Substring (LINK_CLEB_STAFFID, 0, 2) STF_COUNTRY,Substring (LINK_CLEB_STAFFID,CharIndex ('-',LINK_CLEB_STAFFID,1)+ 1,2)STF_LE_BOOK,Substring (LINK_CLEB_STAFFID,"
					+ " CharIndex('-',LINK_CLEB_STAFFID, CharIndex ('-',LINK_CLEB_STAFFID,1)+1)+1, Len(LINK_CLEB_STAFFID)) STAFF_ID, "
					+ " TAppr.USER_NAME, TAppr.USER_LOGIN_ID,"
					+ " TAppr.USER_EMAIL_ID, Format (TAppr.LAST_ACTIVITY_DATE, 'dd-MM-yyyy HH:mm:ss') LAST_ACTIVITY_DATE, TAppr.UPDATE_RESTRICTION,TAppr.AUTO_UPDATE_RESTRICTION,TAppr.LEGAL_VEHICLE,"
					+ " (SELECT LV_DESCRIPTION FROM LEGAL_VEHICLES WHERE LEGAL_VEHICLE = TAppr.LEGAL_VEHICLE) AS LegalVehicleDesc,TAppr.GCID_ACCEss,TAppr.USER_STATUS_NT,TAppr.USER_STATUS,"
					+ " Format (TAppr.USER_STATUS_DATE, 'dd-MM-yyyy HH:mm:ss') USER_STATUS_DATE,TAppr.MAKER,TAppr.VERIFIER,TAppr.INTERNAL_STATUS,TAppr.RECORD_INDICATOR_NT,TAppr.RECORD_INDICATOR,"
					+ " Format (TAppr.DATE_LAST_MODIFIED, 'dd-MM-yyyy HH:mm:ss') DATE_LAST_MODIFIED,Format (TAppr.DATE_CREATION, 'dd-MM-yyyy HH:mm:ss') DATE_CREATION,"
					+ " Format(TAppr.LAST_UNSUCCEssFUL_LOGIN_DATE, 'dd-MM-yyyy HH:mm:ss')LAST_UNSUCCEssFUL_LOGIN_DATE,TAppr.UNSUCCEssFUL_LOGIN_ATTEMPTS,TAppr.FILE_NAME,TAppr.USER_PHOTO,"
					+ " TAppr.ENABLE_WIDGETS,TAppr.PREF_LANGUAGE,"
					+ " (SELECT App_Theme FROM PRD_APP_THEME S1 WHERE S1.VISION_ID=TAppr.VISION_ID) APP_THEME,  "
					+ " (SELECT Report_Slide_Theme FROM PRD_APP_THEME S1 WHERE S1.VISION_ID=TAppr.VISION_ID) Report_Slide_Theme, "
					+ " (SELECT Language FROM PRD_APP_THEME S1 WHERE S1.VISION_ID=TAppr.VISION_ID) Language"
					+ " FROM VISION_USERS TAppr WHERE USER_STATUS = 0"
					+ " AND RECORD_INDICATOR = 0 AND UPPER (USER_LOGIN_ID) = UPPER (?) AND (UNSUCCEssFUL_LOGIN_ATTEMPTS IS NULL OR UNSUCCEssFUL_LOGIN_ATTEMPTS <= 3)");
		}
		try {

			objParams[0] = dObj.getUserLoginId();
			return getJdbcTemplate().query(strQueryAppr.toString(), objParams, getMapper2());

		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("Error: getQueryResults Exception :   ");
			/*if (objParams != null)
				for (int i = 0; i < objParams.length; i++)*/
					//logger.error("objParams[" + i + "]" + objParams[i].toString());
			return null;

		}
	}

	private RowMapper getMapper2() {
		RowMapper mapper = new RowMapper() {
			@Override
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				VisionUsersVb visionUsersVb = new VisionUsersVb();
				visionUsersVb.setVisionId(rs.getInt("VISION_ID"));
				if (ValidationUtil.isValid(rs.getString("USER_NAME")))
					visionUsersVb.setUserName(rs.getString("USER_NAME"));
				if (ValidationUtil.isValid(rs.getString("USER_LOGIN_ID")))
					visionUsersVb.setUserLoginId(rs.getString("USER_LOGIN_ID"));
				if (ValidationUtil.isValid(rs.getString("STF_LE_BOOK")))
					visionUsersVb.setStfleBook(rs.getString("STF_LE_BOOK").trim());
				if (ValidationUtil.isValid(rs.getString("STF_COUNTRY")))
					visionUsersVb.setStfcountry(rs.getString("STF_COUNTRY").trim());
				if (ValidationUtil.isValid(rs.getString("STAFF_ID")))
					visionUsersVb.setStaffId(rs.getString("STAFF_ID"));
				if (ValidationUtil.isValid(rs.getString("USER_EMAIL_ID")))
					visionUsersVb.setUserEmailId(rs.getString("USER_EMAIL_ID"));
				if (ValidationUtil.isValid(rs.getString("USER_STATUS_DATE")))
					visionUsersVb.setUserStatusDate(rs.getString("USER_STATUS_DATE"));
				visionUsersVb.setUserStatusNt(rs.getInt("USER_STATUS_NT"));
				visionUsersVb.setUserStatus(rs.getInt("USER_STATUS"));
				if (ValidationUtil.isValid(rs.getString("LAST_ACTIVITY_DATE")))
					visionUsersVb.setLastActivityDate(rs.getString("LAST_ACTIVITY_DATE").trim());
				if (ValidationUtil.isValid(rs.getString("UPDATE_RESTRICTION")))
					visionUsersVb.setUpdateRestriction(rs.getString("UPDATE_RESTRICTION").trim());
				if (ValidationUtil.isValid(rs.getString("AUTO_UPDATE_RESTRICTION")))
					visionUsersVb.setAutoUpdateRestriction(rs.getString("AUTO_UPDATE_RESTRICTION").trim());
				if (ValidationUtil.isValid(rs.getString("GCID_ACCESS"))) {
					visionUsersVb.setGcidAccess(rs.getString("GCID_ACCESS").trim());
				}
				visionUsersVb.setRecordIndicatorNt(rs.getInt("RECORD_INDICATOR_NT"));
				visionUsersVb.setRecordIndicator(rs.getInt("RECORD_INDICATOR"));
				visionUsersVb.setMaker(rs.getLong("MAKER"));
				visionUsersVb.setVerifier(rs.getLong("VERIFIER"));
				visionUsersVb.setInternalStatus(rs.getInt("INTERNAL_STATUS"));
				if (ValidationUtil.isValid(rs.getString("DATE_CREATION")))
					visionUsersVb.setDateCreation(rs.getString("DATE_CREATION"));
				if (ValidationUtil.isValid(rs.getString("DATE_LAST_MODIFIED")))
					visionUsersVb.setDateLastModified(rs.getString("DATE_LAST_MODIFIED"));
				if (ValidationUtil.isValid(rs.getString("LAST_UNSUCCESSFUL_LOGIN_DATE")))
					visionUsersVb.setLastUnsuccessfulLoginDate(rs.getString("LAST_UNSUCCESSFUL_LOGIN_DATE"));
				if (ValidationUtil.isValid(rs.getString("UNSUCCESSFUL_LOGIN_ATTEMPTS")))
					visionUsersVb.setLastUnsuccessfulLoginAttempts(rs.getString("UNSUCCESSFUL_LOGIN_ATTEMPTS"));
				if (ValidationUtil.isValid(rs.getString("FILE_NAME"))) {
					visionUsersVb.setFileNmae(rs.getString("FILE_NAME"));
					if (rs.getBlob("USER_PHOTO") != null) {
						createImage(visionUsersVb.getFileNmae(), rs);
					}
				}
				/*
				 * if(ValidationUtil.isValid(rs.getString("PREF_LANGUAGE")))
				 * visionUsersVb.setPrefLanguage(rs.getString("PREF_LANGUAGE"));
				 * if(ValidationUtil.isValid(rs.getString("PREF_LANGUAGE")))
				 * visionUsersVb.setMainPrefLanguage(rs.getString("PREF_LANGUAGE"));
				 */
				return visionUsersVb;
			}

		};
		return mapper;
	}

	public String getUserGrpProfile(int visionId) {
		try {

			String query = " Select LISTAGG(STR, ''',''') WITHIN GROUP (ORDER BY NULL) from " + " (WITH DATA AS "
					+ " 		( Select User_GRP_PROFILE str from vision_Users where vision_ID = '" + visionId + "') "
					+ " 		SELECT trim(regexp_substr(str, '[^,]+', 1, LEVEL)) str " + " 		FROM DATA "
					+ " 		CONNECT BY instr(str, ',', 1, LEVEL - 1) > 0) ";
			return getJdbcTemplate().queryForObject(query, null, String.class);
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	public String getMultiOucUserAccess(String regionProvince, String visionOuc, String businessGroup) {
		String restrictedSbu = "";
		try {
			String query = " SELECT distinct LISTAGG(VISION_OUC, ''',''') WITHIN GROUP (ORDER BY VISION_OUC) vision_ouc "
					+ " FROM (Select distinct VISION_OUC from OUC_CODES where Region_Province = '" + regionProvince
					+ "' " + " Union all Select distinct VISION_OUC from OUC_CODES " + " where Vision_ouc = '"
					+ visionOuc + "' Union all " + " Select distinct VISION_OUC from OUC_CODES "
					+ " where Business_Group = '" + businessGroup + "' ) ";
			return getJdbcTemplate().queryForObject(query, null, String.class);
		} catch (Exception e) {
			e.printStackTrace();
			return restrictedSbu;
		}
	}

	public int insertUserLoginAudit(VisionUsersVb vObject) {
		String query = "";
		String node = System.getenv("RA_NODE_NAME");
		if (!ValidationUtil.isValid(node)) {
			node = "A1";
		}
		try {
			if ("ORACLE".equalsIgnoreCase(databaseType)) {
				query = "Insert Into RA_USERS_LOGIN_AUDIT (USER_LOGIN_ID,VISION_ID,IP_ADDRESS,HOST_NAME,ACCESS_DATE,LOGIN_STATUS_AT,LOGIN_STATUS,COMMENTS,MAC_ADDRESS, NODE_NAME) "
						+ " Values (?, ?, ?, ?, Sysdate, 1206, ?, ?, ?, ?)";
			} else if ("MSSQL".equalsIgnoreCase(databaseType)) {
				query = "Insert Into RA_USERS_LOGIN_AUDIT (USER_LOGIN_ID,VISION_ID,IP_ADDRESS,HOST_NAME,ACCESS_DATE,LOGIN_STATUS_AT,LOGIN_STATUS,COMMENTS,MAC_ADDRESS, NODE_NAME) "
						+ " Values (?, ?, ?, ?, GETDATE(), 1206, ?, ?, ?, ?)";
			}
			Object[] args = { vObject.getUserLoginId(), vObject.getVisionId(), vObject.getIpAddress(),
					vObject.getRemoteHostName(), vObject.getLoginStatus(), vObject.getComments(),
					vObject.getMacAddress(), node };
			return getJdbcTemplate().update(query, args);
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}
	public int doUnlockUserAppr(VisionUsersVb vObject){
		String query = "Update VISION_USERS Set UNSUCCESSFUL_LOGIN_ATTEMPTS = 0 Where VISION_ID = ?";
		Object[] args = {vObject.getVisionId()};
		return getJdbcTemplate().update(query,args);
	}
	@Override
	protected ExceptionCode doDeleteRecordForNonTrans(VisionUsersVb vObject) throws RuntimeCustomException {
		VisionUsersVb vObjectlocal = null;
		List<VisionUsersVb> collTemp = null;
		ExceptionCode exceptionCode = null;
		strApproveOperation = Constants.DELETE;
		strErrorDesc  = "";
		strCurrentOperation = Constants.DELETE;
		setServiceDefaults();
		collTemp = selectApprovedRecord(vObject);
	
	
		if (collTemp == null){
			logger.error("Collection is null");
			exceptionCode = getResultObject(Constants.ERRONEOUS_OPERATION);
			throw buildRuntimeCustomException(exceptionCode);
		}
		// If record already exists in the approved table, reject the addition
		if (collTemp.size() > 0 ){
			vObjectlocal = ((ArrayList<VisionUsersVb>)collTemp).get(0);
			int intStaticDeletionFlag = getStatus(vObjectlocal);
			if (intStaticDeletionFlag == Constants.PASSIVATE){
				exceptionCode = getResultObject(Constants.CANNOT_DELETE_AN_INACTIVE_RECORD);
				throw buildRuntimeCustomException(exceptionCode);
			}
		}
		else{
			exceptionCode = getResultObject(Constants.ATTEMPT_TO_DELETE_UNEXISTING_RECORD);
			throw buildRuntimeCustomException(exceptionCode);
		}

		// check to see if the record already exists in the pending table
		collTemp = doSelectPendingRecord(vObject);
		if (collTemp == null){
			exceptionCode = getResultObject(Constants.ERRONEOUS_OPERATION);
			throw buildRuntimeCustomException(exceptionCode);
		}

		// If records are there, check for the status and decide what error to return back
		if (collTemp.size() > 0){
			exceptionCode = getResultObject(Constants.TRYING_TO_DELETE_APPROVAL_PENDING_RECORD);
			throw buildRuntimeCustomException(exceptionCode);
		}

		// insert the record into pending table with status 3 - deletion
		if(vObjectlocal==null){
			exceptionCode = getResultObject(Constants.ERRONEOUS_OPERATION);
			throw buildRuntimeCustomException(exceptionCode);
		}
		if (collTemp.size() > 0){
		    vObjectlocal=((ArrayList<VisionUsersVb>)collTemp).get(0);
		    vObjectlocal.setDateCreation(vObject.getDateCreation());
		}
		//vObjectlocal.setDateCreation(vObject.getDateCreation());
		vObjectlocal.setMaker(getIntCurrentUserId());
		vObjectlocal.setRecordIndicator(Constants.STATUS_DELETE);
		vObjectlocal.setVerifier(getIntCurrentUserId());
		retVal = doInsertionPendWithDc(vObjectlocal);
		if (retVal != Constants.SUCCESSFUL_OPERATION){
			exceptionCode = getResultObject(retVal);
			throw buildRuntimeCustomException(exceptionCode);
		}
		vObject.setRecordIndicator(Constants.STATUS_DELETE);
		vObject.setVerifier(getIntCurrentUserId());
		return getResultObject(Constants.SUCCESSFUL_OPERATION);
	}

	public List<VisionUsersVb> getUserInfoForLogin(VisionUsersVb dObj) {

		Object objParams[] = new Object[1];
		StringBuffer strQueryAppr = new StringBuffer(
				"SELECT VISION_ID,USER_NAME,USER_LOGIN_ID,USER_EMAIL_ID,LAST_ACTIVITY_DATE,USER_GROUP,USER_PROFILE, "
						+ " USER_GRP_PROFILE,UPDATE_RESTRICTION,COUNTRY,LE_BOOK,REGION_PROVINCE,BUSINESS_GROUP,PRODUCT_SUPER_GROUP,OUC_ATTRIBUTE, "
						+ " SBU_CODE,PRODUCT_ATTRIBUTE,ACCOUNT_OFFICER,GCID_ACCESS,USER_STATUS,LAST_UNSUCCESSFUL_LOGIN_DATE,UNSUCCESSFUL_LOGIN_ATTEMPTS, "
						+ " LEGAL_VEHICLE,LINK_CLEB_STAFFID,USER_PHOTO,FILE_NAME,ENABLE_WIDGETS, "
						+ " APPLICATION_ACCESS, (SELECT App_Theme FROM PRD_APP_THEME S1 WHERE S1.VISION_ID=TAppr.VISION_ID and Application_id = '"
						+ productName + "') APP_THEME,  "
						+ " (SELECT Report_Slide_Theme FROM PRD_APP_THEME S1 WHERE S1.VISION_ID=TAppr.VISION_ID and Application_id = '"
						+ productName + "') Report_Slide_Theme, "
						+ " (SELECT Language FROM PRD_APP_THEME S1 WHERE S1.VISION_ID=TAppr.VISION_ID and Application_id = '"
						+ productName + "') Language " + " From VISION_USERS_VW TAppr WHERE USER_STATUS = 0 "
						+ " AND UPPER(USER_LOGIN_ID) =UPPER(?) AND (UNSUCCESSFUL_LOGIN_ATTEMPTS is NULL OR UNSUCCESSFUL_LOGIN_ATTEMPTS <= 3) ");
		try {

			objParams[0] = dObj.getUserLoginId();
			return getJdbcTemplate().query(strQueryAppr.toString(), objParams, getUserMapperView());

		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("Error: getQueryResults Exception :   ");
			/*if (objParams != null)
				for (int i = 0; i < objParams.length; i++)*/
					//logger.error("objParams[" + i + "]" + objParams[i].toString());
			return null;

		}
	}

	private RowMapper getUserMapperView() {
		RowMapper mapper = new RowMapper() {
			@Override
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				VisionUsersVb visionUsersVb = new VisionUsersVb();
				visionUsersVb.setVisionId(rs.getInt("VISION_ID"));
				if (ValidationUtil.isValid(rs.getString("USER_NAME")))
					visionUsersVb.setUserName(rs.getString("USER_NAME"));
				if (ValidationUtil.isValid(rs.getString("USER_LOGIN_ID")))
					visionUsersVb.setUserLoginId(rs.getString("USER_LOGIN_ID"));
				if (ValidationUtil.isValid(rs.getString("USER_EMAIL_ID")))
					visionUsersVb.setUserEmailId(rs.getString("USER_EMAIL_ID"));
				visionUsersVb.setUserStatus(rs.getInt("USER_STATUS"));
				visionUsersVb.setUserGrpProfile(rs.getString("USER_GRP_PROFILE"));
				if (ValidationUtil.isValid(rs.getString("UPDATE_RESTRICTION")))
					visionUsersVb.setUpdateRestriction(rs.getString("UPDATE_RESTRICTION").trim());
				if (ValidationUtil.isValid(rs.getString("LE_BOOK")))
					visionUsersVb.setLeBook(rs.getString("LE_BOOK").trim());
				if (ValidationUtil.isValid(rs.getString("COUNTRY")))
					visionUsersVb.setCountry(rs.getString("COUNTRY").trim());
				if (ValidationUtil.isValid(rs.getString("LEGAL_VEHICLE")))
					visionUsersVb.setLegalVehicle(rs.getString("LEGAL_VEHICLE").trim());
				if (ValidationUtil.isValid(rs.getString("PRODUCT_ATTRIBUTE")))
					visionUsersVb.setProductAttribute(rs.getString("PRODUCT_ATTRIBUTE").trim());
				if (ValidationUtil.isValid(rs.getString("SBU_CODE")))
					visionUsersVb.setSbuCode(rs.getString("SBU_CODE").trim());
				if (ValidationUtil.isValid(rs.getString("OUC_ATTRIBUTE")))
					visionUsersVb.setOucAttribute(rs.getString("OUC_ATTRIBUTE").trim());
				if (ValidationUtil.isValid(rs.getString("PRODUCT_SUPER_GROUP")))
					visionUsersVb.setProductSuperGroup(rs.getString("PRODUCT_SUPER_GROUP").trim());
				if (ValidationUtil.isValid(rs.getString("BUSINESS_GROUP")))
					visionUsersVb.setBusinessGroup(rs.getString("BUSINESS_GROUP").trim());
				if (ValidationUtil.isValid(rs.getString("ACCOUNT_OFFICER")))
					visionUsersVb.setAccountOfficer(rs.getString("ACCOUNT_OFFICER").trim());
				if (ValidationUtil.isValid(rs.getString("REGION_PROVINCE")))
					visionUsersVb.setRegionProvince(rs.getString("REGION_PROVINCE").trim());
				if (ValidationUtil.isValid(rs.getString("GCID_ACCESS"))) {
					visionUsersVb.setGcidAccess(rs.getString("GCID_ACCESS").trim());
				}
				if (ValidationUtil.isValid(rs.getString("LAST_UNSUCCESSFUL_LOGIN_DATE")))
					visionUsersVb.setLastUnsuccessfulLoginDate(rs.getString("LAST_UNSUCCESSFUL_LOGIN_DATE"));
				if (ValidationUtil.isValid(rs.getString("UNSUCCESSFUL_LOGIN_ATTEMPTS")))
					visionUsersVb.setLastUnsuccessfulLoginAttempts(rs.getString("UNSUCCESSFUL_LOGIN_ATTEMPTS"));
				if (ValidationUtil.isValid(rs.getString("FILE_NAME"))) {
					visionUsersVb.setFileNmae(rs.getString("FILE_NAME"));
					if (rs.getBlob("USER_PHOTO") != null) {
						createImage(visionUsersVb.getFileNmae(), rs);
					}
				}
				if (ValidationUtil.isValid(rs.getString("ENABLE_WIDGETS")))
					visionUsersVb.setEnableWidgets(rs.getString("ENABLE_WIDGETS"));
				if (ValidationUtil.isValid(rs.getString("APPLICATION_ACCESS")))
					visionUsersVb.setApplicationAccess(rs.getString("APPLICATION_ACCESS"));
				if (ValidationUtil.isValid(rs.getString("APP_THEME"))) {
					visionUsersVb.setAppTheme(rs.getString("APP_THEME"));
				} else {
					visionUsersVb.setAppTheme("BLUE");
				}
				if (ValidationUtil.isValid(rs.getString("Report_Slide_Theme"))) {
					visionUsersVb.setReportSliderTheme(rs.getString("Report_Slide_Theme"));
				} else {
					visionUsersVb.setReportSliderTheme("DARK");
				}
				if (ValidationUtil.isValid(rs.getString("Language"))) {
					visionUsersVb.setLanguage(rs.getString("Language"));
				} else {
					visionUsersVb.setLanguage("EN");
				}
				return visionUsersVb;
			}

		};
		return mapper;
	}
	public int getMaxVisionId() {
		String sql = "";
		try {
//			sql = "SELECT  ISNULL(MAX(VISION_ID)+1,1000) VISION_ID FROM (SELECT  VISION_ID  FROM VISION_USERS "+
//				" UNION "+
//				" SELECT  VISION_ID  FROM VISION_USERS_PEND) T1";
			sql = " SELECT RIGHT('000000' + CAST(ISNULL(MAX(VISION_ID) + 1, 1000) AS VARCHAR), 6) AS VISION_ID "
					+ " FROM (SELECT VISION_ID FROM VISION_USERS UNION SELECT VISION_ID FROM VISION_USERS_PEND) AS T1 ";
			if("ORACLE".equalsIgnoreCase(databaseType)) {
				sql = " SELECT  LPAD(NVL(MAX(VISION_ID) + 1, 1000), 6, '0') AS VISION_ID FROM ( "
						+ " SELECT VISION_ID FROM VISION_USERS UNION SELECT VISION_ID FROM VISION_USERS_PEND) T1 ";	
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return getJdbcTemplate().queryForObject(sql, Integer.class);
	}
	public int userLoginIdExists(String userLoginId) {
		int retVal = 0;
		try {
			String sql = "SELECT SUM(CNT) CNT FROM( "+
					"select count(1) CNT from vision_users where upper(user_login_id) = upper('"+userLoginId+"') "+
					" UNION "+
					" select count(1) CNT from vision_users_pend where upper(user_login_id) = upper('"+userLoginId+"')) T1";
			retVal = getJdbcTemplate().queryForObject(sql, Integer.class);
		}catch(Exception e) {
			e.printStackTrace();
		}
		return retVal;
	}
	public List<UserRestrictionVb> doUpdateRestrictionToUserObject(VisionUsersVb vObject,
			List<UserRestrictionVb> restrictionList) {
		Object args[] = { vObject.getVisionId() };

		Iterator<UserRestrictionVb> restrictionItr = restrictionList.iterator();
		while (restrictionItr.hasNext()) {
			UserRestrictionVb restrictionVb = restrictionItr.next();
			String restrictedValue = userObjectUpdate(vObject, restrictionVb.getMacrovarName(),
					getJdbcTemplate().query(restrictionVb.getRestrictionSql(), args, getMapperRestriction()));
			restrictionVb.setRestrictedValue(restrictedValue);
		}

		return restrictionList;
	}
	private VisionUsersVb passwordFlagSet(VisionUsersVb dObj) {
		if(loginProfile.equals("SAML2") || loginProfile.equals("OAUTH") || loginProfile.equals("OPENID")|| !adServerProfile.equalsIgnoreCase("NATIVE")) {
			dObj.setPasswordResetFlag("N");
		}
		return dObj;
	}
}