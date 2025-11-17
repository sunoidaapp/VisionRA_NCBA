package com.vision.dao;

import java.io.StringReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.vision.authentication.CustomContextHolder;
import com.vision.util.ValidationUtil;
import com.vision.vb.AlphaSubTabVb;
import com.vision.vb.BearerTokenVb;
import com.vision.vb.RefreshTokenVb;
import com.vision.vb.UserRestrictionVb;
import com.vision.vb.VisionUsersVb;

@Component
public class LoginUserDao extends AbstractCommonDao {
	
	public List<VisionUsersVb> getUserByUserLoginIdOrUserEmailId(String userIdentityAttValue) {

		StringBuffer strQueryAppr = new StringBuffer("Select TAppr.VISION_ID,"
				+ "TAppr.USER_NAME, TAppr.USER_LOGIN_ID, TAppr.USER_EMAIL_ID, "
				+ getDbFunction("DATEFUNC")+ " (TAppr.LAST_ACTIVITY_DATE, '"
				+ getDbFunction("DD_Mon_RRRR")+ " " + getDbFunction("TIME")
				+ "') LAST_ACTIVITY_DATE, TAppr.USER_GROUP_AT,"
				+ "TAppr.USER_GROUP, TAppr.USER_PROFILE_AT, TAppr.USER_PROFILE, TAppr.UPDATE_RESTRICTION, "
				+ "TAppr.GCID_ACCESS, TAppr.USER_STATUS_NT, TAppr.USER_STATUS,"
				+ getDbFunction("DATEFUNC")+ " (TAppr.USER_STATUS_DATE, '"
				+ getDbFunction("DD_Mon_RRRR")+ " " + getDbFunction("TIME")
				+ "') USER_STATUS_DATE, TAppr.MAKER, "
				+ "TAppr.VERIFIER, TAppr.INTERNAL_STATUS, TAppr.RECORD_INDICATOR_NT," + "TAppr.RECORD_INDICATOR, "
				+ getDbFunction("DATEFUNC")+ " (TAppr.DATE_LAST_MODIFIED, '"
				+ getDbFunction("DD_Mon_RRRR")+ " " + getDbFunction("TIME")
				+ "') DATE_LAST_MODIFIED, " + getDbFunction("DATEFUNC")+ " (TAppr.DATE_CREATION, '"
				+ getDbFunction("DD_Mon_RRRR")+ " " + getDbFunction("TIME")
				+ "') DATE_CREATION," + " " + getDbFunction("DATEFUNC")
				+ " (TAppr.LAST_UNSUCCESSFUL_LOGIN_DATE, '" + getDbFunction("DD_Mon_RRRR")+ " "
				+ getDbFunction("TIME")
				+ "') LAST_UNSUCCESSFUL_LOGIN_DATE, TAppr.UNSUCCESSFUL_LOGIN_ATTEMPTS, TAppr.FILE_NAME, TAppr.USER_PHOTO, "
				+ "TAppr.ENABLE_WIDGETS,APPLICATION_ACCESS, "
				+ " (SELECT App_Theme FROM PRD_APP_THEME S1 WHERE S1.VISION_ID=TAppr.VISION_ID and Application_id = '"+productName+"') APP_THEME,  "
				+" (SELECT Report_Slide_Theme FROM PRD_APP_THEME S1 WHERE S1.VISION_ID=TAppr.VISION_ID and Application_id = '"+productName+"') Report_Slide_Theme, "
				+" (SELECT Language FROM PRD_APP_THEME S1 WHERE S1.VISION_ID=TAppr.VISION_ID and Application_id = '"+productName+"') Language, "
				+ " PWD_RESET_FLAG, "+getDbFunction("DATEDIFF", "LAST_PWD_RESET_DATE")+" AS DATE_DIFFERENCE From VISION_USERS_VW TAppr WHERE (UPPER(USER_LOGIN_ID) = UPPER(?) OR UPPER(USER_EMAIL_ID) = UPPER(?)) ");
		Object objParams[] = { userIdentityAttValue, userIdentityAttValue };
		try {
			return getJdbcTemplate().query(strQueryAppr.toString(), objParams, visionUserMapper());
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("Error: getQueryResults Exception :   ");
			/*if (objParams != null)
				for (int i = 0; i < objParams.length; i++)
					//logger.error("objParams[" + i + "]" + objParams[i].toString());
*/			return null;

		}
	}
	
	public List<VisionUsersVb> getUserByVisionId(String visionId) {

		StringBuffer strQueryAppr = new StringBuffer("Select TAppr.VISION_ID,"
				+ "TAppr.USER_NAME, TAppr.USER_LOGIN_ID, TAppr.USER_EMAIL_ID, "
				+ getDbFunction("DATEFUNC")+ " (TAppr.LAST_ACTIVITY_DATE, '"
				+ getDbFunction("DD_Mon_RRRR")+ " " + getDbFunction("TIME")
				+ "') LAST_ACTIVITY_DATE, TAppr.USER_GROUP_AT,"
				+ "TAppr.USER_GROUP, TAppr.USER_PROFILE_AT, TAppr.USER_PROFILE, TAppr.UPDATE_RESTRICTION, "
				+ "TAppr.GCID_ACCESS, TAppr.USER_STATUS_NT, TAppr.USER_STATUS,"
				+ getDbFunction("DATEFUNC")+ " (TAppr.USER_STATUS_DATE, '"
				+ getDbFunction("DD_Mon_RRRR")+ " " + getDbFunction("TIME")
				+ "') USER_STATUS_DATE, TAppr.MAKER, "
				+ "TAppr.VERIFIER, TAppr.INTERNAL_STATUS, TAppr.RECORD_INDICATOR_NT," + "TAppr.RECORD_INDICATOR, "
				+ getDbFunction("DATEFUNC")+ " (TAppr.DATE_LAST_MODIFIED, '"
				+ getDbFunction("DD_Mon_RRRR")+ " " + getDbFunction("TIME")
				+ "') DATE_LAST_MODIFIED, " + getDbFunction("DATEFUNC")+ " (TAppr.DATE_CREATION, '"
				+ getDbFunction("DD_Mon_RRRR")+ " " + getDbFunction("TIME")
				+ "') DATE_CREATION," + " " + getDbFunction("DATEFUNC")
				+ " (TAppr.LAST_UNSUCCESSFUL_LOGIN_DATE, '" + getDbFunction("DD_Mon_RRRR")+ " "
				+ getDbFunction("TIME")
				+ "') LAST_UNSUCCESSFUL_LOGIN_DATE, TAppr.UNSUCCESSFUL_LOGIN_ATTEMPTS, TAppr.FILE_NAME, TAppr.USER_PHOTO, "
				+ "TAppr.ENABLE_WIDGETS,TAppr.APPLICATION_ACCESS, "
				+ " (SELECT App_Theme FROM PRD_APP_THEME S1 WHERE S1.VISION_ID=TAppr.VISION_ID and Application_id = '"+productName+"') APP_THEME,  "
				+" (SELECT Report_Slide_Theme FROM PRD_APP_THEME S1 WHERE S1.VISION_ID=TAppr.VISION_ID and Application_id = '"+productName+"') Report_Slide_Theme, "
				+" (SELECT Language FROM PRD_APP_THEME S1 WHERE S1.VISION_ID=TAppr.VISION_ID and Application_id = '"+productName+"') Language, "
				+ " PWD_RESET_FLAG, "+getDbFunction("DATEDIFF", "LAST_PWD_RESET_DATE")+" AS DATE_DIFFERENCE From VISION_USERS_VW TAppr WHERE VISION_ID = ? ");
		Object objParams[] = { visionId };
		try {
			return getJdbcTemplate().query(strQueryAppr.toString(), objParams, visionUserMapper());
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("Error: getQueryResults Exception :   ");
			/*if (objParams != null)
				for (int i = 0; i < objParams.length; i++)
					//logger.error("objParams[" + i + "]" + objParams[i].toString());
*/			return null;

		}
	}
	
	private RowMapper visionUserMapper(){
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				VisionUsersVb visionUsersVb = new VisionUsersVb();
				visionUsersVb.setVisionId(rs.getInt("VISION_ID"));
				if(ValidationUtil.isValid(rs.getString("USER_NAME")))
					visionUsersVb.setUserName(rs.getString("USER_NAME"));
				if(ValidationUtil.isValid(rs.getString("USER_LOGIN_ID")))				
					visionUsersVb.setUserLoginId(rs.getString("USER_LOGIN_ID"));
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
				if(ValidationUtil.isValid(rs.getString("GCID_ACCESS"))){
					visionUsersVb.setGcidAccess(rs.getString("GCID_ACCESS").trim());
				}
				visionUsersVb.setRecordIndicator(rs.getInt("RECORD_INDICATOR"));
				if(ValidationUtil.isValid(rs.getString("LAST_UNSUCCESSFUL_LOGIN_DATE")))
					visionUsersVb.setLastUnsuccessfulLoginDate(rs.getString("LAST_UNSUCCESSFUL_LOGIN_DATE"));
				if(ValidationUtil.isValid(rs.getString("UNSUCCESSFUL_LOGIN_ATTEMPTS")))
					visionUsersVb.setLastUnsuccessfulLoginAttempts(rs.getString("UNSUCCESSFUL_LOGIN_ATTEMPTS"));
				if (ValidationUtil.isValid(rs.getString("APPLICATION_ACCESS")))
					visionUsersVb.setApplicationAccess(rs.getString("APPLICATION_ACCESS"));
				if (ValidationUtil.isValid(rs.getString("APP_THEME"))) {
					visionUsersVb.setAppTheme(rs.getString("APP_THEME"));
				}else {
					visionUsersVb.setAppTheme("BLUE");
				}
				if (ValidationUtil.isValid(rs.getString("Report_Slide_Theme"))) {
					visionUsersVb.setReportSliderTheme(rs.getString("Report_Slide_Theme"));
				}else {
					visionUsersVb.setReportSliderTheme("DARK");
				}
				if (ValidationUtil.isValid(rs.getString("Language"))) {
					visionUsersVb.setLanguage(rs.getString("Language"));
				}else {
					visionUsersVb.setLanguage("EN");
				}
				if(ValidationUtil.isValid(rs.getString("ENABLE_WIDGETS")))
					visionUsersVb.setEnableWidgets(rs.getString("ENABLE_WIDGETS"));
				else
					visionUsersVb.setEnableWidgets("N");
				
				if(ValidationUtil.isValid(rs.getString("PWD_RESET_FLAG")))
					visionUsersVb.setPasswordResetFlag(rs.getString("PWD_RESET_FLAG"));
				else
					visionUsersVb.setPasswordResetFlag("Y");
				
				if(ValidationUtil.isValid(rs.getInt("DATE_DIFFERENCE")))
					visionUsersVb.setLastPwdResetCount(rs.getInt("DATE_DIFFERENCE"));
				
				return visionUsersVb;
			}
			
		};
		return mapper;
	}
	
	public boolean updateActivityDateByUserLoginId(VisionUsersVb visionUsersVb) {
		Object[] params = new Object[1];
		params[0] = visionUsersVb.getVisionId();
		int count = getJdbcTemplate().update(
				"Update VISION_USERS_VW SET LAST_ACTIVITY_DATE = " + getDbFunction("SYSDATE") + " , "
						+ "LAST_UNSUCCESSFUL_LOGIN_DATE = null, UNSUCCESSFUL_LOGIN_ATTEMPTS = 0  WHERE VISION_ID = ?",
				params);
		return count == 1;
	}

	public List<UserRestrictionVb> getRestrictionTree() throws DataAccessException {
		String sql = "select MACROVAR_NAME,TAG_NAME, DISPLAY_NAME, MACROVAR_DESC from MACROVAR_TAGGING where MACROVAR_TYPE = 'DATA_RESTRICTION' order by MACROVAR_NAME, TAG_NO";
		return getJdbcTemplate().query(sql, new ResultSetExtractor<List<UserRestrictionVb>>(){
			@Override
			public List<UserRestrictionVb> extractData(ResultSet rs) throws SQLException,
					DataAccessException {
				List<UserRestrictionVb> returnList = new ArrayList<UserRestrictionVb>();
				while(rs.next()){
					String macroVar = rs.getString("MACROVAR_NAME");
					List<UserRestrictionVb> filteredList = returnList.stream().filter(vb -> macroVar.equalsIgnoreCase(vb.getMacrovarName())).collect(Collectors.toList());
					if(filteredList!=null && filteredList.size()>0) {
						List<UserRestrictionVb> childrenList = filteredList.get(0).getChildren();
						childrenList.add(new UserRestrictionVb(macroVar, rs.getString("TAG_NAME"), rs.getString("DISPLAY_NAME"), rs.getString("MACROVAR_DESC")));
					} else {
						List<UserRestrictionVb> childrenList = new ArrayList<UserRestrictionVb>();
						childrenList.add(new UserRestrictionVb(macroVar, rs.getString("TAG_NAME"), rs.getString("DISPLAY_NAME"), rs.getString("MACROVAR_DESC")));
						UserRestrictionVb userRestrictionVb = new UserRestrictionVb();
						userRestrictionVb.setMacrovarName(macroVar);
						userRestrictionVb.setMacrovarDesc(rs.getString("MACROVAR_DESC"));
						userRestrictionVb.setChildren(childrenList);
						returnList.add(userRestrictionVb);
					}
				}
				return returnList;
			}
		});
	}
	
	public String getVisionDynamicHashVariable(String variableName) {
		String script = "";
		String query = "select VARIABLE_SCRIPT from vision_dynamic_hash_var where VARIABLE_NAME = ? ";
		Object args[] = {"VU_RESTRICTION_"+variableName};
		try {
			script = getJdbcTemplate().queryForObject(query,args,String.class);	
		}catch(Exception e) {
			logger.error("Error on getting Variable script for ["+variableName+"]");
			logger.error(e.getMessage());
		}
		return script;
	}
	
	public List<UserRestrictionVb> doUpdateRestrictionToUserObject(VisionUsersVb vObject, List<UserRestrictionVb> restrictionList){
		try {
			Object args[] = {vObject.getVisionId()};
	    	Iterator<UserRestrictionVb> restrictionItr = restrictionList.iterator();
	    	StringJoiner restrictionXml = new StringJoiner("");
			while(restrictionItr.hasNext()) {
				UserRestrictionVb restrictionVb = restrictionItr.next();
				String restrictedValue = userObjectUpdate(vObject, restrictionVb.getMacrovarName(), getJdbcTemplate().query(restrictionVb.getRestrictionSql(), args, getMapperRestriction()));
				restrictionVb.setRestrictedValue(restrictedValue);
				
				restrictionXml.add("<"+restrictionVb.getMacrovarName()+">"+restrictedValue+"</"+restrictionVb.getMacrovarName()+">");
			}
			insertUserRestriction(restrictionXml.toString(),vObject.getVisionId());
	    	return restrictionList;
		}catch(Exception e) {
			logger.error(e.getMessage());
			return null;
		}
    }
	
	private RowMapper<AlphaSubTabVb> getMapperRestriction(){
		RowMapper<AlphaSubTabVb> mapper = new RowMapper<AlphaSubTabVb>() {
			public AlphaSubTabVb mapRow(ResultSet rs, int rowNum) throws SQLException {
				AlphaSubTabVb alphaSubTabVb = new AlphaSubTabVb();
				alphaSubTabVb.setAlphaSubTab(rs.getString("RESTRICTION"));
				return alphaSubTabVb;
			}
		};
		return mapper;
	}
	
	private String userObjectUpdate(VisionUsersVb vObject, String category, List<AlphaSubTabVb> valueLst){
    	StringBuffer restrictValue = new StringBuffer();
		if (valueLst != null && valueLst.size() > 0) {
			restrictValue = formInConditionWithResultListForRestriction(valueLst);
			if(category.equals("COUNTRY")){
				restrictValue = new StringBuffer();
				Set countrySet = new HashSet();
				for(AlphaSubTabVb vObj: valueLst){
					countrySet.add((vObj.getAlphaSubTab().split("-"))[0]);
				}
				int idx = 0;
				for(Object country:countrySet){
					restrictValue.append("'"+country+"'");
					if(idx != countrySet.size()-1)
						restrictValue.append(",");
					idx++;
				}
			}
		} else {
			restrictValue = null;
		}
    	return (restrictValue!=null)?String.valueOf(restrictValue):null;
    }
	
	private StringBuffer formInConditionWithResultListForRestriction(List<AlphaSubTabVb> valueLst){
    	StringBuffer restrictValue = new StringBuffer();
		if (valueLst != null && valueLst.size() > 0) {
			int idx = 1;
			for(AlphaSubTabVb vObj: valueLst){
				restrictValue.append("'"+vObj.getAlphaSubTab()+"'");
				if(idx < valueLst.size())
					restrictValue.append(",");
				idx++;
			}
		}else{
			restrictValue = null;
		}
		return restrictValue;
    }
	private int insertUserRestriction(String restrictionXml,int intCurrentUserId) {
		try {
			//setServiceDefaults();
			checkRestrictionExists(intCurrentUserId);
			String sql = "Insert into VISION_USER_RESTRICTION (Vision_ID,USER_RESTRICTION_XML) Values(?,?) ";
			Object args[] = {intCurrentUserId,restrictionXml};
			return getJdbcTemplate().update(sql,args);	
		}catch(Exception e) {
			logger.error(e.getMessage());
		}
		return 0;
	}
	private void checkRestrictionExists(int intCurrentUserId) {
		setServiceDefaults();
		try {
			String sql = "Select count(1) from VISION_USER_RESTRICTION where Vision_ID = ? ";
			Object args[] = {intCurrentUserId};
			int cnt = getJdbcTemplate().queryForObject(sql,args,Integer.class);
			if(cnt > 0) {
				sql = "Delete from VISION_USER_RESTRICTION where Vision_ID = ? ";
				cnt = getJdbcTemplate().update(sql,args);
			}
		}catch(Exception e) {
			logger.error(e.getMessage());
		}
	}
	public String getLoginUserXml() {
		setServiceDefaults();
		String xml = "";
		try {
			String sql =" Select USER_RESTRICTION_XML from VISION_USER_RESTRICTION where Vision_ID = ? ";
			Object args[] = {intCurrentUserId};
			xml = getJdbcTemplate().queryForObject(sql,args,String.class);	
		}catch(Exception e) {
			logger.error("Exception while getting User restriction Xml");
			logger.error(e.getMessage());
		}
		return xml;
	}
	protected void setServiceDefaults(){
		serviceName = "VisionUsers";
		serviceDesc = "Vision Users";
		tableName = "VISION_USERS";
		childTableName = "VISION_USERS";
		intCurrentUserId = CustomContextHolder.getContext().getVisionId();
	}
	public int insertUserLoginAudit(VisionUsersVb vObject) {
		String query = "";
		String node = System.getenv("RA_NODE_NAME");
		if (!ValidationUtil.isValid(node)) {
			node = "A1";
		}
		try {
			query = "Insert Into PRD_USERS_LOGIN_AUDIT (APPLICATION_ACCESS_AT,APPLICATION_ACCESS,USER_LOGIN_ID,VISION_ID,IP_ADDRESS,HOST_NAME,ACCESS_DATE,LOGIN_STATUS_AT,LOGIN_STATUS,COMMENTS,MAC_ADDRESS, NODE_NAME) "
					+ " Values (?,?,?, ?, ?, ?, "+getDbFunction("SYSDATE")+", 1206, ?, ?, ?, ?)";
			Object[] args = {8000,productName,vObject.getUserLoginId(), vObject.getVisionId(), vObject.getIpAddress(),
					vObject.getRemoteHostName(), vObject.getLoginStatus(), vObject.getComments(),
					vObject.getMacAddress(), node };
			return getJdbcTemplate().update(query, args);
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}
	public int insertPrdRefreshToken(VisionUsersVb vObject, String refreshToken, String strTokenCreatedDate, String strTokenExpirationDate, String a_PublicToken, String j_PrivateToken) {
		String tokenExpireQuery = "";
		String tokenCreatedQuery = "";
		
		if("ORACLE".equalsIgnoreCase(databaseType)) {
			tokenExpireQuery = "TO_DATE ('" + strTokenExpirationDate + "', 'DD-MM-YYYY HH24:MI:SS')";
			tokenCreatedQuery =  "TO_DATE ('" + strTokenCreatedDate + "', 'DD-MM-YYYY HH24:MI:SS')";
		} else {
			tokenExpireQuery = 	"CONVERT (datetime,'" + strTokenExpirationDate + "', 103)";
			tokenCreatedQuery = "CONVERT (datetime,'" + strTokenCreatedDate + "', 103)";
		}
		
		String query = " insert into PRD_REFRESH_TOKEN (R_SESSION_ID, VISION_ID, IP_ADDRESS, HOSTNAME, MAC_ADDRESS, TOKEN_STATUS_NT, "
				+ " TOKEN_STATUS, TOKEN_CREATED_DATE, VALID_TILL, COMMENTS, A_PUBLIC_TOKEN, J_PRIVATE_TOKEN, REFRESH_TOKEN)  values (?, ?, ?, ?, ?, 8001, 0, "
				+ tokenCreatedQuery + ", "+tokenExpireQuery +", ?, ?, ?, ?)";
		Object[] args = { vObject.getrSessionId(), vObject.getVisionId(), vObject.getIpAddress(),
				vObject.getRemoteHostName(), vObject.getMacAddress(), vObject.getComments(), a_PublicToken, j_PrivateToken};
		int result = 0;
		try {
			return getJdbcTemplate().update(new PreparedStatementCreator() {
				@Override
				public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
					int argumentLength = args.length;
					PreparedStatement ps = connection.prepareStatement(query);
					for (int i = 1; i <= argumentLength; i++) {
						ps.setObject(i, args[i - 1]);
					}
					String clobData = ValidationUtil.isValid(refreshToken) ? refreshToken : "";
					ps.setCharacterStream(++argumentLength, new StringReader(clobData), clobData.length());
					return ps;
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
			strErrorDesc = e.getMessage();
			logger.error("inset Error in PRD_REFRESH_TOKEN : " + e.getMessage());
		}
		return result;
	}
	
	public int writePrdBearerToken(VisionUsersVb vObject, String bearerToken, String strTokenCreatedDate, String expiration) {
		String tokenExpireQuery ="";
		String tokenCreatedQuery = "";
		
		if ("ORACLE".equalsIgnoreCase(databaseType)) {
			tokenExpireQuery = "TO_DATE ('" + expiration + "', 'DD-MM-YYYY HH24:MI:SS')";
			tokenCreatedQuery = "TO_DATE ('" + strTokenCreatedDate + "', 'DD-MM-YYYY HH24:MI:SS')";
		} else {
			tokenExpireQuery = "CONVERT (datetime,'" + expiration + "', 103)";
			tokenCreatedQuery = "CONVERT (datetime,'" + strTokenCreatedDate + "', 103)";
		}
			
		String query = " insert into PRD_BEARER_TOKEN ( R_SESSION_ID, B_SESSION_ID, VISION_ID, IP_ADDRESS, HOSTNAME, MAC_ADDRESS, "
				+ " TOKEN_STATUS_NT, TOKEN_STATUS, TOKEN_CREATED_DATE, VALID_TILL, APPLICATION_ACCESS_AT, APPLICATION_ACCESS, "
				+ " COMMENTS, UTILIZATION_COUNT, BEARER_TOKEN) values ( ?, ?, ?, ?, ?, ?, 8001, 0, "
				+ tokenCreatedQuery + ", "+tokenExpireQuery+", 8000, ?, ?, 0, ?) ";
		Object[] args = { vObject.getrSessionId(), vObject.getbSessionId(), vObject.getVisionId(),
				vObject.getIpAddress(), vObject.getRemoteHostName(), vObject.getMacAddress(), productName,
				vObject.getComments() };
		int result = 0;
		try {
			return getJdbcTemplate().update(new PreparedStatementCreator() {
				@Override
				public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
					int argumentLength = args.length;
					PreparedStatement ps = connection.prepareStatement(query);
					for (int i = 1; i <= argumentLength; i++) {
						ps.setObject(i, args[i - 1]);
					}
					String clobData = ValidationUtil.isValid(bearerToken) ? bearerToken : "";
					ps.setCharacterStream(++argumentLength, new StringReader(clobData), clobData.length());
					return ps;
				}
			});

		} catch (Exception e) {
			e.printStackTrace();
			strErrorDesc = e.getMessage();
			logger.error("inset Error in PRD_BEARER_TOKEN : " + e.getMessage());
		}
		return result;
	}
	
	public int getBearerTokenCount(VisionUsersVb vObject) {
		String sql = "SELECT  count(1) FROM PRD_BEARER_TOKEN  WHERE VISION_ID = ? AND R_SESSION_ID = ? "
				+ "AND B_SESSION_ID = ?  AND APPLICATION_ACCESS = ? ";
		
		Object[] args = {vObject.getVisionId(), vObject.getrSessionId(), vObject.getbSessionId(), productName};
		
		return getJdbcTemplate().queryForObject(sql, args, Integer.class);
	}
	
	public BearerTokenVb getBearerTokenWithPrdID_rSessID_visionID(String rSessionId, String visionID) {
		
		String sql = "SELECT R_SESSION_ID, B_SESSION_ID, VISION_ID, BEARER_TOKEN, TOKEN_STATUS, IP_ADDRESS, HOSTNAME, MAC_ADDRESS, VALID_TILL, UTILIZATION_COUNT"
				+ " FROM PRD_BEARER_TOKEN  WHERE R_SESSION_ID = ? AND VISION_ID = ?  AND APPLICATION_ACCESS = ? ";
		
		Object[] args = {rSessionId, visionID, productName};
		
		return getJdbcTemplate().query(sql, args, new ResultSetExtractor<BearerTokenVb>() {

			@Override
			public BearerTokenVb extractData(ResultSet rs) throws SQLException, DataAccessException {
				BearerTokenVb vb = null;
				if(rs.next()) {
					vb = new BearerTokenVb();
					vb.setrSessionId(rs.getString("R_SESSION_ID"));
					vb.setbSessionId(rs.getString("B_SESSION_ID"));
					vb.setVisionId(rs.getString("VISION_ID"));
					vb.setBearerToken(rs.getString("BEARER_TOKEN"));
					vb.setTokenStatus(rs.getInt("TOKEN_STATUS"));
					vb.setIpAddress(rs.getString("IP_ADDRESS"));
					vb.setHostname(rs.getString("HOSTNAME"));
					vb.setMacAddress(rs.getString("MAC_ADDRESS"));
					vb.setValidTill(rs.getString("VALID_TILL"));
					vb.setUtilizationCount(rs.getInt("UTILIZATION_COUNT"));
				}
				return vb;
			}
		});

	}
	
	public int updatePrdBearerToken(VisionUsersVb vObject, String bearerToken, String strTokenExpirationDate) {
		String tokenExpireQuery ="";
		if("ORACLE".equalsIgnoreCase(databaseType))
			tokenExpireQuery = "TO_DATE ('" + strTokenExpirationDate + "', 'DD-MM-YYYY HH24:MI:SS')";
		else
			tokenExpireQuery = 	"CONVERT (datetime,'" + strTokenExpirationDate + "', 103)";
		String query = "UPDATE PRD_BEARER_TOKEN SET BEARER_TOKEN = ?, VALID_TILL = "
				+ tokenExpireQuery+ ", UTILIZATION_COUNT = 0 "
				+ " WHERE VISION_ID = ? AND R_SESSION_ID = ? AND B_SESSION_ID = ? ";
		Object[] args = { vObject.getVisionId(), vObject.getrSessionId(),
				vObject.getbSessionId() };
		int result = 0;
		try {
			return getJdbcTemplate().update(new PreparedStatementCreator() {
				@Override
				public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
					PreparedStatement ps = connection.prepareStatement(query);
					int psIndex = 0;
					String clobData = ValidationUtil.isValid(bearerToken) ? bearerToken : "";
					ps.setCharacterStream(++psIndex, new StringReader(clobData), clobData.length());
					for (int i = 1; i <= args.length; i++) {
						ps.setObject(++psIndex, args[i - 1]);
					}
					return ps;
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
			strErrorDesc = e.getMessage();
			logger.error("Update Error in PRD_BEARER_TOKEN : " + e.getMessage());
		}
		return result;
	}
	
	public int selectAndInsertPrdBearerTokenIntoAudit(VisionUsersVb vObject) {
		String sql = " SELECT R_SESSION_ID, B_SESSION_ID, VISION_ID, BEARER_TOKEN, IP_ADDRESS, HOSTNAME, MAC_ADDRESS, TOKEN_STATUS_NT, "
				+ " TOKEN_STATUS, TOKEN_CREATED_DATE, VALID_TILL, DATE_LAST_UTILIZED, APPLICATION_ACCESS_AT, APPLICATION_ACCESS, COMMENTS, UTILIZATION_COUNT "
				+ " FROM PRD_BEARER_TOKEN  WHERE VISION_ID = ? AND R_SESSION_ID = ? AND B_SESSION_ID = ?  AND APPLICATION_ACCESS = ? ";
		String instemplateSelPrep = " INSERT INTO PRD_BEARER_TOKEN_AUDIT ( R_SESSION_ID, B_SESSION_ID, VISION_ID, BEARER_TOKEN,"
				+ " IP_ADDRESS, HOSTNAME, MAC_ADDRESS, TOKEN_STATUS_NT, TOKEN_STATUS, TOKEN_CREATED_DATE, "
				+ " VALID_TILL, DATE_LAST_UTILIZED, APPLICATION_ACCESS_AT, APPLICATION_ACCESS, COMMENTS, UTILIZATION_COUNT )  " + sql;
		
		Object[] args = {vObject.getVisionId(),  vObject.getrSessionId(), vObject.getbSessionId(), productName};
		
		return getJdbcTemplate().update(instemplateSelPrep, args);
	}
	
	public int selectAndInsertPrdRefreshTokenIntoAudit(VisionUsersVb vObject) {
		String sql = " SELECT R_SESSION_ID, VISION_ID, REFRESH_TOKEN, IP_ADDRESS, HOSTNAME, MAC_ADDRESS, TOKEN_STATUS_NT,  "
				+ " TOKEN_STATUS, TOKEN_CREATED_DATE, VALID_TILL, DATE_LAST_UTILIZED, COMMENTS, A_PUBLIC_TOKEN, J_PRIVATE_TOKEN, UTILIZATION_COUNT "
				+ " FROM PRD_REFRESH_TOKEN  WHERE VISION_ID = ? AND R_SESSION_ID = ? ";
		String instemplateSelPrep = " INSERT INTO PRD_REFRESH_TOKEN_AUDIT ( R_SESSION_ID, VISION_ID, REFRESH_TOKEN, IP_ADDRESS,"
				+ " HOSTNAME, MAC_ADDRESS, TOKEN_STATUS_NT, "
				+ " TOKEN_STATUS, TOKEN_CREATED_DATE, VALID_TILL, DATE_LAST_UTILIZED, COMMENTS, A_PUBLIC_TOKEN, J_PRIVATE_TOKEN, UTILIZATION_COUNT)  " + sql;
		
		Object[] args = {vObject.getVisionId(), vObject.getrSessionId()};
		
		return getJdbcTemplate().update(instemplateSelPrep, args);
	}
	
	public RefreshTokenVb getRefreshTokenInfoWith_rSessionID(String rSessionId) {
		String sql = "SELECT R_SESSION_ID, VISION_ID, REFRESH_TOKEN, J_PRIVATE_TOKEN, A_PUBLIC_TOKEN, TOKEN_STATUS,"
				+ " IP_ADDRESS, HOSTNAME, MAC_ADDRESS, VALID_TILL, UTILIZATION_COUNT"
				+ " FROM PRD_REFRESH_TOKEN  WHERE R_SESSION_ID = ? ";
		
		Object[] args = {rSessionId};
		
		return getJdbcTemplate().query(sql, args, new ResultSetExtractor<RefreshTokenVb>() {
			@Override
			public RefreshTokenVb extractData(ResultSet rs) throws SQLException, DataAccessException {
				RefreshTokenVb vb = null;
				if (rs.next()) {
					vb = new RefreshTokenVb();
					vb.setrSessionId(rs.getString("R_SESSION_ID"));
					vb.setVisionId(rs.getString("VISION_ID"));
					vb.setRefreshToken(rs.getString("REFRESH_TOKEN"));
					vb.setjPrivateKey(rs.getString("J_PRIVATE_TOKEN"));
					vb.setTokenStatus(rs.getInt("TOKEN_STATUS"));
					vb.setIpAddress(rs.getString("IP_ADDRESS"));
					vb.setHostname(rs.getString("HOSTNAME"));
					vb.setMacAddress(rs.getString("MAC_ADDRESS"));
					vb.setValidTill(rs.getString("VALID_TILL"));
					vb.setaPublicKey(rs.getString("A_PUBLIC_TOKEN"));
					vb.setUtilizationCount(rs.getInt("UTILIZATION_COUNT"));
				}
				return vb;
			}
		});

	}
	
	
	public int updatePrdRefreshToken(VisionUsersVb vObject, String bearerToken, String strTokenExpirationDate) {
		String tokenExpireQuery ="";
		if("ORACLE".equalsIgnoreCase(databaseType))
			tokenExpireQuery = "TO_DATE ('" + strTokenExpirationDate + "', 'DD-MM-YYYY HH24:MI:SS')";
		else
			tokenExpireQuery = 	"CONVERT (datetime,'" + strTokenExpirationDate + "', 103)";
		String query = "UPDATE PRD_REFRESH_TOKEN SET REFRESH_TOKEN = ?, VALID_TILL = "+tokenExpireQuery+", UTILIZATION_COUNT = 0 "
				+ " WHERE VISION_ID = ? AND R_SESSION_ID = ? ";
		Object[] args = { vObject.getVisionId(), vObject.getrSessionId() };
		int result = 0;
		try {
			return getJdbcTemplate().update(new PreparedStatementCreator() {
				@Override
				public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
					PreparedStatement ps = connection.prepareStatement(query);
					int psIndex = 0;
					String clobData = ValidationUtil.isValid(bearerToken) ? bearerToken : "";
					ps.setCharacterStream(++psIndex, new StringReader(clobData), clobData.length());
					for (int i = 1; i <= args.length; i++) {
						ps.setObject(++psIndex, args[i - 1]);
					}
					return ps;
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
			strErrorDesc = e.getMessage();
			logger.error("Update Error in PRD_REFRESH_TOKEN : " + e.getMessage());
		}
		return result;
	}
	
	public int writePRD_Suspecious_Token_Audit(String rSessionId, String bSessionId, String refreshToken, String bearerToken, String tokenType,
			String comments, VisionUsersVb vb) {
		String sql = "Insert into PRD_Suspecious_Token_Audit ( R_SESSION_ID, B_SESSION_ID, REFRESH_TOKEN, BEARER_TOKEN, IP_ADDRESS, "
				+ " HOSTNAME, MAC_ADDRESS, TOKEN_TYPE, COMMENTS, DATE_LAST_MODIFIED, DATE_CREATION ) "
				+ " values (?, ?, ?, ?, ?, ?, ?, ?, ?, " + getDbFunction("SYSDATE") + ", "
				+ getDbFunction("SYSDATE") + " )";
		Object[] args = { rSessionId, bSessionId, refreshToken, bearerToken, vb.getIpAddress(), vb.getRemoteHostName(),
				vb.getMacAddress(), tokenType, comments };
		return getJdbcTemplate().update(sql, args);
	}
	
	public int invalidateRefreshToken(String rSessionId, int tokenStatus, String visionId) {
		String sql = "update PRD_REFRESH_TOKEN set token_status = ? where Vision_id = ? and R_SESSION_ID = ? ";
		Object[] args = {tokenStatus, visionId, rSessionId};
		return getJdbcTemplate().update(sql, args);
	}
	
	public int insertBearerTokenCount(String bSessionId, String tokenCount) {
		String sql = "Insert into PRD_BEARER_TOKEN_COUNT (B_SESSION_ID, TOKEN_COUNT) values (?, ?)";
		Object[] args = {bSessionId, tokenCount};
		return getJdbcTemplate().update(sql, args);
	}
	
	public int getBearerTokenCount(String bSessionId, String tokenCount) {
		String sql = "SELECT  count(1) FROM PRD_BEARER_TOKEN_COUNT  WHERE  B_SESSION_ID = ? AND TOKEN_COUNT = ? ";
		Object[] args = {bSessionId, tokenCount};
		return getJdbcTemplate().queryForObject(sql, args, Integer.class);
	}
	
	public int invalidateBearerToken(String bSessionId, int tokenStatus, String visionId) {
		String sql = "update PRD_BEARER_TOKEN set token_status = ? where Vision_id = ? and B_SESSION_ID = ? ";
		Object[] args = {tokenStatus, visionId, bSessionId};
		return getJdbcTemplate().update(sql, args);
	}
	
	public int deleteRefreshTokenCount(String rSessionId) {
		String sql = "DELETE FROM PRD_REFRESH_TOKEN_COUNT WHERE R_SESSION_ID = ? ";
		Object[] args = {rSessionId};
		return getJdbcTemplate().update(sql, args);
	}
	
	public int insertRefreshTokenCount(String rSessionId, String tokenCount) {
		String sql = "Insert into PRD_REFRESH_TOKEN_COUNT (R_SESSION_ID, TOKEN_COUNT) values (?, ?)";
		Object[] args = {rSessionId, tokenCount};
		return getJdbcTemplate().update(sql, args);
	}
	public int getRefreshTokenCount(String rSessionId, String tokenCount) {
		String sql = "SELECT  count(1) FROM PRD_REFRESH_TOKEN_COUNT  WHERE  R_SESSION_ID = ? AND TOKEN_COUNT = ? ";
		Object[] args = {rSessionId, tokenCount};
		return getJdbcTemplate().queryForObject(sql, args, Integer.class);
	}
	public int increaseUtilizationCount_RefreshToken(String rSessionID) {
		String sql = "update PRD_REFRESH_TOKEN set UTILIZATION_COUNT = (UTILIZATION_COUNT+1) WHERE R_SESSION_ID = ?";
		Object[] args = {rSessionID};
		return getJdbcTemplate().update(sql, args);
	}
	public int increaseUtilizationCount_BearerToken(String rSessionID, String bSessionID) {
		String sql = "update PRD_BEARER_TOKEN set UTILIZATION_COUNT = (UTILIZATION_COUNT+1) WHERE R_SESSION_ID = ? AND B_SESSION_ID = ?  AND APPLICATION_ACCESS = ?";
		Object[] args = {rSessionID, bSessionID, productName};
		return getJdbcTemplate().update(sql, args);
	}
	
	public int updateAngularRSAPublicToken(String rSessionId, String angularRSAPublicToken) {
		String query = "UPDATE PRD_REFRESH_TOKEN SET A_PUBLIC_TOKEN = ? WHERE R_SESSION_ID = ? ";
		Object[] args = {angularRSAPublicToken, rSessionId};
		return getJdbcTemplate().update(query, args);
	}
	
	public int invalidateBearerTokenByR_SessionID(int tokenStatus, String visionId, String rSessionID) {
		String sql = "update PRD_BEARER_TOKEN set token_status = ? where Vision_id = ? and R_SESSION_ID = ? ";
		Object[] args = {tokenStatus, visionId, rSessionID};
		return getJdbcTemplate().update(sql, args);
	}
	
	public int invalidateRefreshTokenByVisionID(int tokenStatus, String visionId) {
		String sql = "update PRD_REFRESH_TOKEN set token_status = ? where Vision_id = ?";
		Object[] args = {tokenStatus, visionId};
		return getJdbcTemplate().update(sql, args);
	}
	
	public int invalidateBearerTokenByVisionID(int tokenStatus, String visionId) {
		String sql = "update PRD_BEARER_TOKEN set token_status = ? where Vision_id = ?";
		Object[] args = {tokenStatus, visionId};
		return getJdbcTemplate().update(sql, args);
	}
	public String getExistingVisionPwd(String visionId) {
		try {
			String sql = " SELECT PASSWORD FROM vision_users WHERE VISION_ID = ? ";
			Object[] args = {visionId};
			return getJdbcTemplate().queryForObject(sql, args,String.class);
		}catch(Exception e) {
			return "";
		}
	}

	public int updateUserPassword(String newPwd, String visionId) {
		String sql = "Update Vision_users set password = ? , PWD_RESET_FLAG = 'N', LAST_PWD_RESET_DATE = "
				+ getDbFunction("SYSDATE") + "  where VISION_ID = ?";
		Object[] args = {newPwd, visionId};
		return getJdbcTemplate().update(sql, args);
	}
	
	public int updateUnsuccessfulLoginAttempts(String userId) {
		String sql = "Update VISION_USERS SET LAST_UNSUCCESSFUL_LOGIN_DATE = "+getDbFunction("SYSDATE")+","
					+ "UNSUCCESSFUL_LOGIN_ATTEMPTS = "+getDbFunction("NVL")+"(UNSUCCESSFUL_LOGIN_ATTEMPTS,0)+ 1 WHERE (UPPER(USER_LOGIN_ID) = UPPER(?) OR UPPER(USER_EMAIL_ID) = UPPER(?)) ";
		Object[] params = {userId.toUpperCase(), userId.toUpperCase()};
		return getJdbcTemplate().update(sql, params);

	}
	 
}
