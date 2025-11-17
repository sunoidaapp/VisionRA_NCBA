
package com.vision.dao;

import java.io.IOException;
import java.net.SocketException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.StringJoiner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.UncategorizedSQLException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.vision.authentication.CustomContextHolder;
import com.vision.exception.ExceptionCode;
import com.vision.util.CommonUtils;
import com.vision.util.Constants;
import com.vision.util.ValidationUtil;
import com.vision.vb.AlphaSubTabVb;
import com.vision.vb.BusinessLineHeaderVb;
import com.vision.vb.CommonVb;
import com.vision.vb.CurrencyDetailsVb;
import com.vision.vb.FeesConfigDetailsVb;
import com.vision.vb.FeesConfigTierVb;
import com.vision.vb.MenuVb;
import com.vision.vb.ProfileData;
import com.vision.vb.TransLineHeaderVb;
import com.vision.vb.VisionUsersVb;

@Component
public class CommonDao {

	@Autowired
	JdbcTemplate jdbcTemplate;

	@Value("${app.productName}")
	private String productName;

	@Value("${app.client}")
	private String clientType;

	@Value("${app.databaseType}")
	private String databaseType;
	
	@Value("${app.clientName}")
	private String clientName;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Autowired
	CommonApiDao commonApiDao;
	
	@Autowired
	LoginUserDao loginUserDao;
	
	public List<CommonVb> findVerificationRequiredAndStaticDelete(String pTableName) throws DataAccessException {

		String sql = "select DELETE_TYPE,VERIFICATION_REQD FROM VISION_TABLES where UPPER(TABLE_NAME) = UPPER(?)";
		Object[] lParams = new Object[1];
		lParams[0] = pTableName;
		RowMapper mapper = new RowMapper() {
			@Override
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				CommonVb commonVb = new CommonVb();
				commonVb.setStaticDelete(
						rs.getString("DELETE_TYPE") == null || rs.getString("DELETE_TYPE").equalsIgnoreCase("S") ? true
								: false);
				commonVb.setVerificationRequired(rs.getString("VERIFICATION_REQD") == null
						|| rs.getString("VERIFICATION_REQD").equalsIgnoreCase("Y") ? true : false);
				return commonVb;
			}
		};
		List<CommonVb> commonVbs = getJdbcTemplate().query(sql, lParams, mapper);
		if (commonVbs == null || commonVbs.isEmpty()) {
			commonVbs = new ArrayList<CommonVb>();
			CommonVb commonVb = new CommonVb();
			commonVb.setStaticDelete(true);
			commonVb.setVerificationRequired(true);
			commonVbs.add(commonVb);
		}
		return commonVbs;
	}
	public List<ProfileData> getTopLevelMenu() throws DataAccessException {
		String sql = " Select MENU_GROUP_SEQ,MENU_GROUP_NAME,MENU_GROUP_ICON from PRD_MENU_GROUP where MENU_GROUP_Status = 0 "
				+ "and Application_Access = ? ORDER BY MENU_GROUP_SEQ ";
		Object[] lParams = new Object[1];
		lParams[0] = productName;
		RowMapper mapper = new RowMapper() {
			@Override
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				ProfileData profileData = new ProfileData();
				profileData.setMenuItem(rs.getString("MENU_GROUP_NAME"));
				profileData.setMenuGroup(rs.getInt("MENU_GROUP_SEQ"));
				profileData.setMenuIcon(rs.getString("MENU_GROUP_ICON"));
				return profileData;
			}
		};
		List<ProfileData> profileData = getJdbcTemplate().query(sql,lParams, mapper);
		return profileData;
	}

	public ArrayList<MenuVb> getSubMenuItemsForMenuGroup(int menuGroup) throws DataAccessException {

		String sql = "SELECT * FROM PRD_VISION_MENU WHERE MENU_GROUP = ? AND MENU_STATUS = 0 AND UPPER(MENU_NAME) != 'SEPERATOR' "
				+ " AND APPLICATION_ACCESS = ? AND MENU_SEQUENCE = PARENT_SEQUENCE ORDER BY PARENT_SEQUENCE, MENU_SEQUENCE";
		Object[] lParams = new Object[2];
		lParams[0] = menuGroup;
		lParams[1] = productName;
		RowMapper mapper = new RowMapper() {
			@Override
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				MenuVb menuVb = new MenuVb();
				menuVb.setMenuProgram(rs.getString("MENU_PROGRAM"));
				menuVb.setMenuName(rs.getString("MENU_NAME"));
				menuVb.setMenuSequence(rs.getInt("MENU_SEQUENCE"));
				menuVb.setParentSequence(rs.getInt("PARENT_SEQUENCE"));
				menuVb.setSeparator(rs.getString("SEPARATOR"));
				menuVb.setMenuGroup(rs.getInt("MENU_GROUP"));
				menuVb.setMenuStatus(rs.getInt("MENU_STATUS"));
				return menuVb;
			}
		};
		ArrayList<MenuVb> menuList = (ArrayList<MenuVb>) getJdbcTemplate().query(sql, lParams, mapper);
		return menuList;
	}

	public ArrayList<MenuVb> getSubMenuItemsForSubMenuGroup(int menuGroup, int parentSequence, int visionId)
			throws DataAccessException {
		String sql = "";
		if ("ORACLE".equalsIgnoreCase(databaseType)) {
			sql = " SELECT *                                                          "
					+ "     FROM PRD_VISION_MENU t1, PRD_PROFILE_PRIVILEGES t2, VISION_USERS_VW t3   "
					+ "    WHERE     MENU_GROUP = ? "
					+ "AND PROFILE_STATUS = 0                                              "
					+ "          AND MENU_STATUS = 0                                              "
					+ "          AND UPPER (MENU_NAME) != 'SEPERATOR'                             "
					+ "          AND t1.Application_Access = '" + productName + "'                  "
					+ " 		 AND t1.Application_Access = t2.Application_Access "
					+ "          AND Menu_Sequence <> Parent_Sequence                             "
					+ "          AND Parent_Sequence = " + parentSequence + "                         "
					+ "          AND T1.MENU_PROGRAM = t2.screen_Name                                "
					+ "          AND t3.vision_ID = '" + visionId + "'                                "
					+ "          AND t2.User_Group||'-'||T2.USER_PROFILE = t3.USER_GRP_PROFILE    "
					+ " ORDER BY PARENT_SEQUENCE, MENU_SEQUENCE                                   ";
		} else {
			sql = " SELECT *                                                          "
					+ "     FROM PRD_VISION_MENU t1, PRD_PROFILE_PRIVILEGES t2, VISION_USERS_VW t3   "
					+ "    WHERE     MENU_GROUP = ?  "
					+ "AND PROFILE_STATUS = 0                                             "
					+ "          AND MENU_STATUS = 0                                              "
					+ "          AND UPPER (MENU_NAME) != 'SEPERATOR'                             "
					+ "          AND t1.Application_Access = '" + productName + "'                  "
					+ " 		 AND t1.Application_Access = t2.Application_Access "
					+ "          AND Menu_Sequence <> Parent_Sequence                             "
					+ "          AND Parent_Sequence = " + parentSequence + "                         "
					+ "          AND T1.MENU_PROGRAM = t2.screen_Name                                "
					+ "          AND t3.vision_ID = '" + visionId + "'                                "
					+ "          AND t2.User_Group+'-'+T2.USER_PROFILE = t3.USER_GRP_PROFILE    "
					+ " ORDER BY PARENT_SEQUENCE, MENU_SEQUENCE                                   ";
		}


		Object[] lParams = new Object[1];
		lParams[0] = menuGroup;
		RowMapper mapper = new RowMapper() {
			@Override
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				MenuVb menuVb = new MenuVb();
				menuVb.setMenuProgram(rs.getString("MENU_PROGRAM"));
				menuVb.setMenuName(rs.getString("MENU_NAME"));
				menuVb.setMenuSequence(rs.getInt("MENU_SEQUENCE"));
				menuVb.setParentSequence(rs.getInt("PARENT_SEQUENCE"));
				menuVb.setSeparator(rs.getString("SEPARATOR"));
				menuVb.setMenuGroup(rs.getInt("MENU_GROUP"));
				menuVb.setMenuStatus(rs.getInt("MENU_STATUS"));
				menuVb.setProfileAdd(rs.getString("P_ADD"));
				menuVb.setProfileModify(rs.getString("P_MODIFY"));
				menuVb.setProfileDelete(rs.getString("P_DELETE"));
				menuVb.setProfileView(rs.getString("P_INQUIRY"));
				menuVb.setProfileVerification(rs.getString("P_VERIFICATION"));
				menuVb.setProfileUpload(rs.getString("P_EXCEL_UPLOAD"));
				menuVb.setProfileDownload(rs.getString("P_DOWNLOAD"));
				return menuVb;
			}
		};
		ArrayList<MenuVb> menuList = (ArrayList<MenuVb>) getJdbcTemplate().query(sql, lParams, mapper);
		return menuList;
	}

	public String findVisionVariableValue(String pVariableName) throws DataAccessException {
		if (!ValidationUtil.isValid(pVariableName)) {
			return null;
		}
		String sql = "select VALUE FROM VISION_VARIABLES where UPPER(VARIABLE) = UPPER(?)";
		Object[] lParams = new Object[1];
		lParams[0] = pVariableName;
		RowMapper mapper = new RowMapper() {
			@Override
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				CommonVb commonVb = new CommonVb();
				commonVb.setMakerName(rs.getString("VALUE"));
				return commonVb;
			}
		};
		List<CommonVb> commonVbs = getJdbcTemplate().query(sql, lParams, mapper);
		if (commonVbs != null && !commonVbs.isEmpty()) {
			return commonVbs.get(0).getMakerName();
		}
		return null;
	}

	public int getMaxOfId() {
		String sql = "select max(vision_id) from (Select max(vision_id) vision_id from vision_users UNION ALL select Max(vision_id) from vision_users_pend)";
		int i = getJdbcTemplate().queryForObject(sql, Integer.class);
		return i;
	}
	public String getVisionBusinessDate(String countryLeBook) {
		Object args[] = { countryLeBook,productName };
		String sql = "";
		if("CAL".equalsIgnoreCase(clientName)) {
			if ("ORACLE".equalsIgnoreCase(databaseType)) {
				sql = "select TO_CHAR(BUSINESS_DATE,'DD-Mon-RRRR') BUSINESS_DATE  from Vision_Business_Day  WHERE COUNTRY ||'-'|| LE_BOOK=? and application_id = ? ";
			} else if ("MSSQL".equalsIgnoreCase(databaseType)) {
				sql = "select FORMAT(REPORT_BUSINESS_DATE,'dd-MMM-yyyy') BUSINESS_DATE  from Vision_Business_Day  WHERE COUNTRY +'-'+ LE_BOOK=? and application_id = ? ";
			}
		} else {
			if ("ORACLE".equalsIgnoreCase(databaseType)) {
				sql = "select TO_CHAR(BUSINESS_DATE,'DD-Mon-RRRR') BUSINESS_DATE  from Vision_Business_Day  WHERE COUNTRY ||'-'|| LE_BOOK=? and application_id = ? ";
			} else if ("MSSQL".equalsIgnoreCase(databaseType)) {
				sql = "select FORMAT(BUSINESS_DATE,'dd-MMM-yyyy') BUSINESS_DATE  from Vision_Business_Day  WHERE COUNTRY +'-'+ LE_BOOK=? and application_id = ? ";
			}
		}
		return getJdbcTemplate().queryForObject(sql, args, String.class);
	}

	public int getUploadCount() {
		String sql = "Select count(1) from Vision_Upload where Upload_Status = 1 AND FILE_NAME LIKE '%XLSX'";
		int i = getJdbcTemplate().queryForObject(sql, Integer.class);
		return i;
	}

	public int doPasswordResetInsertion(VisionUsersVb vObject) {
		Date oldDate = new Date();
		SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
		String resetValidity = df.format(oldDate);
		if (ValidationUtil.isValid(vObject.getPwdResetTime())) {
			Date newDate = DateUtils.addHours(oldDate, Integer.parseInt(vObject.getPwdResetTime()));
			resetValidity = df.format(newDate);
		}
		String query = "";
		if("ORACLE".equalsIgnoreCase(databaseType)) {
		query = "Insert Into FORGOT_PASSWORD ( VISION_ID, RESET_DATE, RESET_VALIDITY, RS_STATUS_NT, RS_STATUS)"
				+ "Values (?, SysDate, To_Date(?, 'DD-MM-YYYY HH24:MI:SS'), ?, ?)";
		}else if("MSSQL".equalsIgnoreCase(databaseType)) {
			query = "Insert Into FORGOT_PASSWORD ( VISION_ID, RESET_DATE, RESET_VALIDITY, RS_STATUS_NT, RS_STATUS)"
					+ "Values (?, GetDate(), CONVERT(datetime, ?, 103), ?, ?)";
		}
		Object[] args = { vObject.getVisionId(), resetValidity, vObject.getUserStatusNt(), vObject.getUserStatus() };
		return getJdbcTemplate().update(query, args);
	}
	public List<AlphaSubTabVb> getAvailableNodesLst() throws DataAccessException {
		String sql = "SELECT NODE_NAME,NODE_DESCRIPTION FROM VISION_NODE_CREDENTIALS WHERE NODE_STATUS = 0 ORDER BY NODE_DESCRIPTION";
		RowMapper mapper = new RowMapper() {
			@Override
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				AlphaSubTabVb vObj = new AlphaSubTabVb();
				vObj.setAlphaSubTab(rs.getString("NODE_NAME"));
				vObj.setDescription(rs.getString("NODE_DESCRIPTION"));
				return vObj;
			}
		};
		List<AlphaSubTabVb> nodeAvailablelst = getJdbcTemplate().query(sql, mapper);
		return nodeAvailablelst;
	}

	// Deepak maintained seperately.Already avialble on AbstractCommonDao
	public String parseErrorMsgCommon(UncategorizedSQLException ecxception) {
		String strErrorDesc = ecxception.getSQLException() != null ? ecxception.getSQLException().getMessage()
				: ecxception.getMessage();
		String sqlErrorCodes[] = { "ORA-00928:", "ORA-00942:", "ORA-00998:", "ORA-01400:", "ORA-01722:", "ORA-04098:",
				"ORA-01810:", "ORA-01840:", "ORA-01843:", "ORA-20001:", "ORA-20002:", "ORA-20003:", "ORA-20004:",
				"ORA-20005:", "ORA-20006:", "ORA-20007:", "ORA-20008:", "ORA-20009:", "ORA-200010:", "ORA-20011:",
				"ORA-20012:", "ORA-20013:", "ORA-20014:", "ORA-20015:", "ORA-20016:", "ORA-20017:", "ORA-20018:",
				"ORA-20019:", "ORA-20020:", "ORA-20021:", "ORA-20022:", "ORA-20023:", "ORA-20024:", "ORA-20025:",
				"ORA-20102:", "ORA-20105:", "ORA-01422:", "ORA-06502:", "ORA-20082:", "ORA-20030:", "ORA-20010:",
				"ORA-20034:", "ORA-20043:", "ORA-20111:", "ORA-06512:", "ORA-04088:", "ORA-06552:", "ORA-00001:" };
		for (String sqlErrorCode : sqlErrorCodes) {
			if (ValidationUtil.isValid(strErrorDesc) && strErrorDesc.lastIndexOf(sqlErrorCode) >= 0) {
				strErrorDesc = strErrorDesc.substring(
						strErrorDesc.lastIndexOf(sqlErrorCode) + sqlErrorCode.length() + 1, strErrorDesc.length());
				if (strErrorDesc.indexOf("ORA-06512:") >= 0) {
					strErrorDesc = strErrorDesc.substring(0, strErrorDesc.indexOf("ORA-06512:"));
				}
			}
		}
		return strErrorDesc;
	}
	public String getUserHomeDashboard(String userGrpProfile) {
		String homeDashboard = "NA";
		String query = "";
		try {
			query = " Select NVL(Home_dashboard,'NA') from PRD_PROFILE_DASHBOARDS where "
					+ "User_group "+getDbFunction("PIPELINE")+"'-'"+getDbFunction("PIPELINE")+"User_Profile = ? and Application_access = ? ";

			Object[] lParams = new Object[2];
			lParams[0] = userGrpProfile;
			lParams[1] = productName;
			homeDashboard = getJdbcTemplate().queryForObject(query, lParams, String.class);
			if (!ValidationUtil.isValid(homeDashboard)) {
				homeDashboard = "NA";
			}
		} catch (Exception e) {
		}
		return homeDashboard;
	}

	public static String getMacAddress(String ip) throws IOException {
		String address = null;
		String str = "";
		String macAddress = "";
		try {

			String cmd = "arp -a " + ip;
			Scanner s = new Scanner(Runtime.getRuntime().exec(cmd).getInputStream());
			Pattern pattern = Pattern
					.compile("(([0-9A-Fa-f]{2}[-:]){5}[0-9A-Fa-f]{2})|(([0-9A-Fa-f]{4}\\.){2}[0-9A-Fa-f]{4})");
			try {
				while (s.hasNext()) {
					str = s.next();
					Matcher matcher = pattern.matcher(str);
					if (matcher.matches()) {
						break;
					} else {
						str = null;
					}
				}
			} finally {
				s.close();
			}
			if (!ValidationUtil.isValid(str)) {
				return ip;
			}
			return (str != null) ? str.toUpperCase() : null;
		} catch (SocketException ex) {
			ex.printStackTrace();
			return ip;
		}
	}
	public String getDbFunction(String reqFunction) {
		String functionName = "";
		if("MSSQL".equalsIgnoreCase(databaseType)) {
			switch(reqFunction) {
			case "DATEFUNC":
				functionName = "FORMAT";
				break;
			case "SYSDATE":
				functionName = "GetDate()";
				break;
			case "NVL":
				functionName = "ISNULL";
				break;
			case "TIME":
				functionName = "HH:mm:ss";
				break;
			case "DATEFORMAT":
				functionName = "dd-MMM-yyyy";
				break;
			case "CONVERT":
				functionName = "CONVERT";
				break;
			case "TYPE":
				functionName = "varchar,";
				break;
			case "TIMEFORMAT":
				functionName = "108";
				break;
			case "PIPELINE":
				functionName = "+";	
				break;
			}
		}else if("ORACLE".equalsIgnoreCase(databaseType)) {
			switch(reqFunction) {
			case "DATEFUNC":
				functionName = "TO_CHAR";
				break;
			case "SYSDATE":
				functionName = "SYSDATE";
				break;
			case "NVL":
				functionName = "NVL";
				break;
			case "TIME":
				functionName = "HH24:MI:SS";
				break;
			case "DATEFORMAT":
				functionName = "DD-Mon-RRRR";
				break;
			case "CONVERT":
				functionName = "TO_CHAR";
				break;
			case "TYPE":
				functionName = "";
				break;
			case "TIMEFORMAT":
				functionName = "'HH:MM:SS'";
				break;
			case "PIPELINE":
				functionName = "||";
				break;
			}
		}
		
		return functionName;
	}
	public int doScreenClickAudit(String screenName) {
		VisionUsersVb visionUsersVb = CustomContextHolder.getContext();
		String node = System.getenv("RA_NODE_NAME");
		if (!ValidationUtil.isValid(node)) {
			node = "A1";
		}
		String query = "";
		if("ORACLE".equalsIgnoreCase(databaseType)) {
			query = "Insert Into RA_USERS_SCREEN_AUDIT (VISION_ID, IP_ADDRESS, HOST_NAME, ACCESS_DATE, "
					+ " SCREEN_NAME,MAC_ADDRESS, NODE_NAME) " + "Values (?, ?, ?, Sysdate, ?, ?, ?)";
		}else if("MSSQL".equalsIgnoreCase(databaseType)) {
		query = "Insert Into RA_USERS_SCREEN_AUDIT (VISION_ID, IP_ADDRESS, HOST_NAME, ACCESS_DATE, "
				+ " SCREEN_NAME,MAC_ADDRESS, NODE_NAME) " + "Values (?, ?, ?, getdate(), ?, ?, ?)";
		}
		Object[] args = { visionUsersVb.getVisionId(), visionUsersVb.getIpAddress(), visionUsersVb.getRemoteHostName(),
				screenName, visionUsersVb.getMacAddress(), node };
		return getJdbcTemplate().update(query, args);
	}
	public List<AlphaSubTabVb> getVisionBusinessDate() throws DataAccessException {
		String sql = "";
		if ("CAL".equalsIgnoreCase(clientName) && "MSSQL".equalsIgnoreCase(databaseType)) {
			sql = "select COUNTRY+'-'+LE_BOOK ALPHA_SUB_TAB,FORMAT(REPORT_BUSINESS_DATE,'dd-MMM-yyyy') ALPHA_SUBTAB_DESCRIPTION  from Vision_Business_Day  ";
		} else {
			if ("ORACLE".equalsIgnoreCase(databaseType)) {
				sql = "select COUNTRY||'-'||LE_BOOK ALPHA_SUB_TAB,TO_CHAR(BUSINESS_DATE,'DD-Mon-RRRR') ALPHA_SUBTAB_DESCRIPTION  from Vision_Business_Day ";
			} else if ("MSSQL".equalsIgnoreCase(databaseType)) {
				sql = "select COUNTRY+'-'+LE_BOOK ALPHA_SUB_TAB,FORMAT(BUSINESS_DATE,'dd-MMM-yyyy') ALPHA_SUBTAB_DESCRIPTION  from Vision_Business_Day  ";
			}
		}
		return  getJdbcTemplate().query(sql, getGenMapper());
	}
	protected RowMapper getGenMapper(){
		RowMapper mapper = new RowMapper() {
			@Override
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				AlphaSubTabVb alphaSubTabVb = new AlphaSubTabVb();
				alphaSubTabVb.setAlphaSubTab(rs.getString("ALPHA_SUB_TAB"));
				alphaSubTabVb.setDescription(rs.getString("ALPHA_SUBTAB_DESCRIPTION"));
				return alphaSubTabVb;
			}
		};
		return mapper;
	}
	/*
	 * public HashMap<String,String> getAllBusinessDate(){ try { String query = "";
	 * if("CAL".equalsIgnoreCase(clientName)) { if
	 * ("MSSQL".equalsIgnoreCase(databaseType)) { query =
	 * "SELECT COUNTRY+'-'+LE_BOOK COUNTRY,FORMAT(REPORT_BUSINESS_DATE,'dd-MMM-yyyy') BUSINESS_DATE FROM Vision_Business_Day"
	 * ; } else if ("ORACLE".equalsIgnoreCase(databaseType)) { query =
	 * "SELECT COUNTRY||'-'||LE_BOOK COUNTRY,TO_CHAR(BUSINESS_DATE,'DD-Mon-RRRR') BUSINESS_DATE FROM Vision_Business_Day"
	 * ; }
	 * 
	 * } else { if ("MSSQL".equalsIgnoreCase(databaseType)) { query =
	 * "SELECT COUNTRY+'-'+LE_BOOK COUNTRY,FORMAT(BUSINESS_DATE,'dd-MMM-yyyy') BUSINESS_DATE FROM Vision_Business_Day"
	 * ; } else if ("ORACLE".equalsIgnoreCase(databaseType)) { query =
	 * "SELECT COUNTRY||'-'||LE_BOOK COUNTRY,TO_CHAR(BUSINESS_DATE,'DD-Mon-RRRR') BUSINESS_DATE FROM Vision_Business_Day"
	 * ; } } ResultSetExtractor mapper = new ResultSetExtractor() {
	 * 
	 * @Override public Object extractData(ResultSet rs) throws SQLException,
	 * DataAccessException { ResultSetMetaData metaData = rs.getMetaData(); int
	 * colCount = metaData.getColumnCount(); HashMap<String,String> businessDateMap
	 * = new HashMap<String,String>(); while(rs.next()){ for(int cn = 1;cn <=
	 * colCount;cn++) { businessDateMap.put(rs.getString("COUNTRY"),
	 * rs.getString("BUSINESS_DATE")); } } return businessDateMap; } }; return
	 * (HashMap<String,String> )getJdbcTemplate().query(query, mapper);
	 * }catch(Exception e) { return null; } }
	 */

	public ExceptionCode getReqdConnection(Connection conExt, String connectionName) {
		ExceptionCode exceptionCodeCon = new ExceptionCode();
		try {
			if (!ValidationUtil.isValid(connectionName) || "DEFAULT".equalsIgnoreCase(connectionName)) {
				conExt = commonApiDao.getConnection();
				exceptionCodeCon.setResponse(conExt);
			} else {
				String dbScript = getScriptValue(connectionName);
				if (!ValidationUtil.isValid(dbScript)) {
					exceptionCodeCon.setErrorCode(Constants.ERRONEOUS_OPERATION);
					exceptionCodeCon.setErrorMsg("DB Connection Name is Invalid");
					return exceptionCodeCon;
				}
				exceptionCodeCon = CommonUtils.getConnection(dbScript);
			}
			exceptionCodeCon.setErrorCode(Constants.SUCCESSFUL_OPERATION);
		} catch (Exception e) {
			exceptionCodeCon.setErrorMsg(e.getMessage());
			exceptionCodeCon.setErrorCode(Constants.ERRONEOUS_OPERATION);
		}
		return exceptionCodeCon;
	}

	public String getScriptValue(String pVariableName) throws DataAccessException, Exception {
		String returnValue = "";
		try {
			Object params[] = { pVariableName };
			returnValue = getJdbcTemplate().queryForObject(
					"select VARIABLE_SCRIPT from VISION_DYNAMIC_HASH_VAR WHERE VARIABLE_TYPE = 2 AND SCRIPT_TYPE='MACROVAR' AND UPPER(VARIABLE_NAME)=UPPER(?) ",
					params, String.class);
			return returnValue;
		} catch (Exception e) {
			return null;
		}
	}

	public List getAllBusinessDate(){
		try
		{	
			String query = "";
			if ("MSSQL".equalsIgnoreCase(databaseType)) {
				query = " SELECT COUNTRY+'-'+LE_BOOK as COUNTRY, "+  
					    " FORMAT(BUSINESS_DATE,'dd-MMM-yyyy') VBD, "+ 
						" FORMAT(CONVERT(DATE,CONCAT(BUSINESS_YEAR_MONTH,'01')),'MMM-yyyy') VBM , "+
						" FORMAT(BUSINESS_WEEK_DATE,'dd-MMM-yyyy') VBW, "+  
						" FORMAT(CONVERT(DATE,CONCAT(BUSINESS_QTR_YEAR_MONTH,'01')),'MMM-yyyy') VBQ, "+
						" FORMAT(REPORT_BUSINESS_DATE,'dd-MMM-yyyy') VRD "+
						" FROM VISION_BUSINESS_DAY where Application_ID = '"+productName+"' ";
			}else if ("ORACLE".equalsIgnoreCase(databaseType)) {
				query = " SELECT COUNTRY||'-'||LE_BOOK COUNTRY, "+
					    " TO_CHAR(BUSINESS_DATE,'DD-Mon-RRRR') VBD, "+
						" TO_CHAR(TO_DATE(BUSINESS_YEAR_MONTH,'RRRRMM'),'Mon-RRRR') VBM, "+
						" TO_CHAR(BUSINESS_WEEK_DATE,'DD-Mon-RRRR') VBW, "+
						" TO_CHAR(TO_DATE(BUSINESS_QTR_YEAR_MONTH,'RRRRMM'),'Mon-RRRR') VBQ, "+
						" TO_CHAR(REPORT_BUSINESS_DATE,'DD-Mon-RRRR') VRD "+
						" FROM VISION_BUSINESS_DAY where Application_ID = '"+productName+"' ";
			}
			ExceptionCode exceptionCode = commonApiDao.getCommonResultDataQuery(query);
			List resultlst = (List)exceptionCode.getResponse();
			return resultlst;
		}catch(Exception e) {
			return null;
		}
	}

	public String getCurrentDateInfo(String option, String ctryLeBook) {
		String val = "";
		String sql = "";
		try {
			switch (option) {
			case "CYM":
				if ("ORACLE".equalsIgnoreCase(databaseType)) {
					sql = "SELECT TO_CHAR(To_Date(BUSINESS_YEAR_MONTH,'RRRRMM'),'Mon-RRRR') FROM VISION_BUSINESS_DAY "
							+ " WHERE COUNTRY||'-'||LE_BOOK = ? ";
				} else if ("MSSQL".equalsIgnoreCase(databaseType)) {
					sql = "SELECT BUSINESS_YEAR_MONTH   FROM VISION_BUSINESS_DAY  WHERE COUNTRY+'-'+LE_BOOK = ? ";
				}
				break;
			case "CY":
				if ("ORACLE".equalsIgnoreCase(databaseType)) {
					sql = "SELECT TO_CHAR(To_Date(BUSINESS_YEAR_MONTH,'RRRRMM'),'Mon-RRRR') FROM VISION_BUSINESS_DAY "
							+ " WHERE COUNTRY||'-'||LE_BOOK = ? ";
				} else if ("MSSQL".equalsIgnoreCase(databaseType)) {
					sql = "SELECT BUSINESS_YEAR_MONTH   FROM VISION_BUSINESS_DAY  WHERE COUNTRY+'-'+LE_BOOK = ? ";
				}
				break;
			default:
				if ("ORACLE".equalsIgnoreCase(databaseType)) {
					sql = "SELECT TO_CHAR(To_Date(BUSINESS_DATE),'DD-Mon-RRRR') FROM VISION_BUSINESS_DAY "
							+ " WHERE COUNTRY||'-'||LE_BOOK = ? ";
				} else if ("MSSQL".equalsIgnoreCase(databaseType)) {
					sql = "SELECT FORMAT(BUSINESS_DATE,'dd-MMM-yyyy')  BUSINESS_DATE FROM VISION_BUSINESS_DAY WHERE COUNTRY+'-'+LE_BOOK = ?";
				}
				break;
			}
			String args[] = { ctryLeBook };

			val = getJdbcTemplate().queryForObject(sql, args, String.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return val;
	}

	public List getDateFormatforCaption() {
		try {
			String query = "";
			VisionUsersVb visionUsersVb = CustomContextHolder.getContext();
			if ("MSSQL".equalsIgnoreCase(databaseType)) {
				query = " SELECT COUNTRY+'-'+LE_BOOK as COUNTRY, " + " FORMAT(BUSINESS_DATE,'dd-MMM-yyyy') VBD, "
						+ " FORMAT(CONVERT(DATE,CONCAT(BUSINESS_YEAR_MONTH,'01')),'MMM-yyyy') VBM , "
						+ " FORMAT(BUSINESS_WEEK_DATE,'dd-MMM-yyyy') VBW, "
						+ " FORMAT(CONVERT(DATE,CONCAT(BUSINESS_QTR_YEAR_MONTH,'01')),'MMM-yyyy') VBQ "
						+ " BUSINESS_QTR_YEAR_MONTH VBQ, " + " FORMAT(BUSINESS_DATE,'yyyy') CY, "
						+ " (select value from vision_variables where variable = 'CURRENT_MONTH') CM "
						+ " FROM VISION_BUSINESS_DAY ";
			} else if ("ORACLE".equalsIgnoreCase(databaseType)) {
				query = " SELECT COUNTRY||'-'||LE_BOOK COUNTRY, " + "  TO_CHAR(BUSINESS_DATE,'DD-Mon-RRRR') VBD, "
						+ "  TO_CHAR(TO_DATE(BUSINESS_YEAR_MONTH,'RRRRMM'),'Mon-RRRR') VBM, "
						+ "  TO_CHAR(BUSINESS_WEEK_DATE,'DD-Mon-RRRR') VBW, "
						+ "  TO_CHAR(TO_DATE(BUSINESS_QTR_YEAR_MONTH,'RRRRMM'),'Mon-RRRR') VBQ, "
						+ "  TO_CHAR(BUSINESS_DATE,'RRRR') CY, "
						+ "  (select value from vision_variables where variable = 'CURRENT_MONTH') CM, "
						+ "  (SELECT TO_CHAR(CURRENT_TIMESTAMP, 'HH24:MI:SS') FROM dual) SYSTIME, "
						+ "  (SELECT SYSDATE FROM DUAL) SYSTEMDATE, "
						+ "  TO_CHAR(PREV_BUSINESS_DATE,'DD-Mon-RRRR') PVBD, " + // #VBD-1#
						"   TO_CHAR(ADD_MONTHS(BUSINESS_DATE,-1),'DD-Mon-RRRR') PMVBD , "
						+ "   TO_CHAR(ADD_MONTHS(BUSINESS_DATE,-12),'DD-Mon-RRRR') PYVBD, "
						+ "   TO_CHAR(ADD_MONTHS(BUSINESS_DATE,-1)-1,'DD-Mon-RRRR') PMPVBD " + // #PMVBD-1#
						"  FROM VISION_BUSINESS_DAY " + "  WHERE COUNTRY = '" + visionUsersVb.getCountry()
						+ "' AND LE_BOOK = '" + visionUsersVb.getLeBook() + "'";
			}
			ExceptionCode exceptionCode = commonApiDao.getCommonResultDataQuery(query);
			List resultlst = (List) exceptionCode.getResponse();
			return resultlst;
		} catch (Exception e) {
			return null;
		}
	}

	
	public List<BusinessLineHeaderVb> doSelectPendingBusinessLineRecord(BusinessLineHeaderVb dObj) {
		List<BusinessLineHeaderVb> collTemp = null;
		final int intKeyFieldsCount = 4;
		String strQueryPend = null;
		strQueryPend = new String("SELECT TPEND.COUNTRY, TPEND.LE_BOOK, TPEND.BUSINESS_LINE_ID,TPEND.BUSINESS_LINE_DESCRIPTION,TPEND.TRANS_LINE_TYPE,   " + 
				" TPEND.TRANS_LINE_ID,"+
				" CASE WHEN TPEND.TRANS_LINE_TYPE='P' THEN T2.TRANS_LINE_PROD_GRP ELSE T2.TRANS_LINE_SERV_GRP END TRAN_LINE_GRP, "+
				" CASE WHEN TPEND.TRANS_LINE_TYPE='P' THEN "+
				" (SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB =T2.TRANS_LINE_PROD_GRP_AT AND ALPHA_SUB_TAB= T2.TRANS_LINE_PROD_GRP) "+
				"  ELSE "+
				"  (SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB =T2.TRANS_LINE_SERV_GRP_AT AND ALPHA_SUB_TAB= T2.TRANS_LINE_SERV_GRP) "+
				"  END TRAN_LINE_GRP_DESC, "+
				" TPEND.BUSINESS_LINE_TYPE,(SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB =TPEND.BUSINESS_LINE_TYPE_AT AND ALPHA_SUB_TAB= TPEND.BUSINESS_LINE_TYPE) BUSINESS_LINE_TYPE_DESC, "
				+ "TPEND.IE_TYPE, TPEND.ACTUAL_IE_POSTING, TPEND.ACTUAL_IE_MATCH_RULE ,  " + 
				" TPEND.BUSINESS_LINE_STATUS,T3.NUM_SUBTAB_DESCRIPTION BUSINESS_LINE_STATUS_DESC,TPEND.RECORD_INDICATOR,T1.NUM_SUBTAB_DESCRIPTION RECORD_INDICATOR_DESC,   " + 
				" TPEND. MAKER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "+getDbFunction("NVL")+"(TPEND.MAKER,0) ) MAKER_NAME,   " + 
				" TPEND.VERIFIER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "+getDbFunction("NVL")+"(TPEND.VERIFIER,0) ) VERIFIER_NAME,   " + 
				" "+getDbFunction("DATEFUNC")+"(TPEND.DATE_LAST_MODIFIED, 'dd-MM-yyyy "+getDbFunction("TIME")+"') DATE_LAST_MODIFIED ,"+getDbFunction("DATEFUNC")+"(TPEND.DATE_CREATION, 'dd-MM-yyyy "+getDbFunction("TIME")+"') DATE_CREATION "+ 
				" FROM RA_MST_BUSINESS_LINE_HEADER_PEND TPEND, NUM_SUB_TAB T1,NUM_SUB_TAB T3,RA_MST_TRANS_LINE_HEADER_PEND T2 "+
				" WHERE TPEND.COUNTRY =? AND TPEND.LE_BOOK =? AND TPEND.BUSINESS_LINE_ID = ? AND TPEND.TRANS_LINE_ID = ? AND  "+
				" T1.NUM_tab = TPEND.RECORD_INDICATOR_NT" + 
				" and T1.NUM_sub_tab = TPEND.RECORD_INDICATOR"+
				" AND TPEND.TRANS_LINE_ID = T2.TRANS_LINE_ID and T3.NUM_tab = TPEND.BUSINESS_LINE_STATUS_NT and T3.NUM_sub_tab = TPEND.BUSINESS_LINE_STATUS");
		Object objParams[] = new Object[intKeyFieldsCount];
		objParams[0] = new String(dObj.getCountry());// country
		objParams[1] = new String(dObj.getLeBook());
		objParams[2] = new String(dObj.getBusinessLineId());
		objParams[3] = new String(dObj.getTransLineId());
		try {
			collTemp = getJdbcTemplate().query(strQueryPend.toString(),objParams,getDetailMapper1());
			return collTemp;
		}catch(Exception ex){
			ex.printStackTrace();
			return null;
		}
	}

protected RowMapper getDetailMapper1(){
RowMapper mapper = new RowMapper() {
	@Override
	public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
		BusinessLineHeaderVb vObject = new BusinessLineHeaderVb();
		vObject.setCountry(rs.getString("COUNTRY"));
		vObject.setLeBook(rs.getString("LE_BOOK"));
		vObject.setBusinessLineId(rs.getString("BUSINESS_LINE_ID"));
		vObject.setBusinessLineDescription(rs.getString("BUSINESS_LINE_DESCRIPTION"));
		vObject.setTransLineType(rs.getString("TRANS_LINE_TYPE"));
		vObject.setTransLineId(rs.getString("TRANS_LINE_ID"));
		vObject.setTranLineGrp(rs.getString("TRAN_LINE_GRP"));
		vObject.setTranLineGrpDesc(rs.getString("TRAN_LINE_GRP_DESC"));
		vObject.setBusinessLineType(rs.getString("BUSINESS_LINE_TYPE"));
		vObject.setIncomeExpenseType(rs.getString("IE_TYPE"));
		vObject.setActualIePosting(rs.getString("ACTUAL_IE_POSTING"));
		vObject.setActualIeMatchRule(rs.getString("ACTUAL_IE_MATCH_RULE"));
		vObject.setBusinessLineStatus(rs.getInt("BUSINESS_LINE_STATUS"));
		vObject.setRecordIndicator(rs.getInt("RECORD_INDICATOR"));
		vObject.setRecordIndicatorDesc(rs.getString("RECORD_INDICATOR_Desc"));
		vObject.setMaker(rs.getInt("MAKER"));
		vObject.setMakerName(rs.getString("MAKER_NAME"));
		vObject.setVerifier(rs.getInt("VERIFIER"));
		vObject.setVerifierName(rs.getString("VERIFIER_NAME"));
		vObject.setDateLastModified(rs.getString("DATE_LAST_MODIFIED"));
		vObject.setDateCreation(rs.getString("DATE_CREATION"));
		vObject.setBusinessLineTypeDesc(rs.getString("BUSINESS_LINE_TYPE_DESC"));
		vObject.setBusinessLineStatusDesc(rs.getString("BUSINESS_LINE_STATUS_DESC"));
		return vObject;
	}
};
return mapper;
}

	protected RowMapper getDetailMapperTrans(){
		RowMapper mapper = new RowMapper() {
			@Override
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				TransLineHeaderVb vObject = new TransLineHeaderVb();
				vObject.setCountry(rs.getString("COUNTRY"));
				vObject.setLeBook(rs.getString("LE_BOOK"));
				vObject.setTransLineId(rs.getString("TRANS_LINE_ID"));
				vObject.setTransLineType(rs.getString("TRANS_LINE_TYPE"));
				vObject.setTransLineDescription(rs.getString("TRANS_LINE_DESCRIPTION"));
				vObject.setTransLineSubType(rs.getString("TRANS_LINE_PROD_SUB_TYPE"));
				vObject.setTransLineGrp(rs.getString("TRANS_LINE_PROD_GRP"));
				vObject.setExtractionFrequency(rs.getString("EXTRACTION_FREQUENCY"));
				vObject.setExtractionMonthDay(rs.getString("EXTRACTION_MONTH_DAY") );
				vObject.setTargetStgTableId(rs.getString("TARGET_STG_TABLE_ID"));
				vObject.setTransLineStatus(rs.getInt("TRANS_LINE_STATUS"));
				vObject.setRecordIndicator(rs.getInt("RECORD_INDICATOR"));
				vObject.setRecordIndicatorDesc(rs.getString("RECORD_INDICATOR_Desc"));
				vObject.setMaker(rs.getInt("MAKER"));
				vObject.setMakerName(rs.getString("MAKER_NAME"));
				vObject.setVerifier(rs.getInt("VERIFIER"));
				vObject.setVerifierName(rs.getString("VERIFIER_NAME"));
				vObject.setDateLastModified(rs.getString("DATE_LAST_MODIFIED"));
				vObject.setDateCreation(rs.getString("DATE_CREATION"));
				vObject.setTransLineTypeDesc(rs.getString("ALPHA_SUBTAB_DESCRIPTION"));
				return vObject;
			}
		};
		return mapper;
	}
	public List<TransLineHeaderVb> doselectPendingproductRecord(TransLineHeaderVb dObj){
		List<TransLineHeaderVb> collTemp = null;
		final int intKeyFieldsCount = 3;
		String strQueryPend = null;
		strQueryPend = new String(
				"SELECT TPend.COUNTRY, TPend.LE_BOOK, TPend.TRANS_LINE_ID,TPend.TRANS_LINE_DESCRIPTION,A1.ALPHA_SUBTAB_DESCRIPTION,TPend.TRANS_LINE_TYPE,  "
						+
				"TPend.TRANS_LINE_PROD_SUB_TYPE,TPend.TRANS_LINE_PROD_GRP,TPend.EXTRACTION_FREQUENCY,EXTRACTION_MONTH_DAY, " + 
				"TARGET_STG_TABLE_ID,TPend.TRANS_LINE_STATUS,TPend.RECORD_INDICATOR,T1.NUM_SUBTAB_DESCRIPTION RECORD_INDICATOR_DESC, " + 
				"TPend. MAKER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "+getDbFunction("NVL")+"(TPend.MAKER,0) ) MAKER_NAME," +
				"TPend. VERIFIER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "+getDbFunction("NVL")+"(TPend.VERIFIER,0) ) VERIFIER_NAME, "+
				" "+getDbFunction("DATEFUNC")+"(TPend.DATE_LAST_MODIFIED, 'dd-MM-yyyy "+getDbFunction("TIME")+"') DATE_LAST_MODIFIED ,"+getDbFunction("DATEFUNC")+"(TPend.DATE_CREATION, 'dd-MM-yyyy "+getDbFunction("TIME")+"') DATE_CREATION "+ 
						" FROM RA_MST_TRANS_LINE_HEADER_PEND TPend, NUM_SUB_TAB T1,ALPHA_SUB_TAB A1 WHERE "
						+ 
				"TPend.COUNTRY =? AND TPend.LE_BOOK =? AND TPend.TRANS_LINE_ID = ? AND  T1.NUM_tab = TPend.RECORD_INDICATOR_NT"
						+ " and T1.NUM_sub_tab = TPend.RECORD_INDICATOR and TPend.TRANS_LINE_TYPE=A1.ALPHA_SUB_TAB  and A1.alpha_tab=TPend.TRANS_LINE_TYPE_AT");
		Object objParams[] = new Object[intKeyFieldsCount];
		objParams[0] = new String(dObj.getCountry());// country
		objParams[1] = new String(dObj.getLeBook());
		objParams[2] = new String(dObj.getTransLineId());
		try
		{				
				collTemp = getJdbcTemplate().query(strQueryPend.toString(),objParams,getDetailMapperTrans());
			return collTemp;
		}catch(Exception ex){
			ex.printStackTrace();
			return null;
		}
		
	}
	public String getRestrictionsByUsers(String screenName, String operation) throws DataAccessException {
		if (!ValidationUtil.isValid(screenName)) {
			return null;
		}
		VisionUsersVb usersVb = CustomContextHolder.getContext();
		String column = "P_ADD";
		if("MODIFY".equalsIgnoreCase(operation.toUpperCase()))
			column = "P_MODIFY";
		if("DELETE".equalsIgnoreCase(operation.toUpperCase()))
			column = "P_DELETE";		
		if("APPROVE".equalsIgnoreCase(operation.toUpperCase()) || "REJECT".equalsIgnoreCase(operation.toUpperCase()))
			column = "P_VERIFICATION";	
		if("DOWNLOAD".equalsIgnoreCase(operation.toUpperCase()))
			column = "P_DOWNLOAD";	
		if("UPLOAD".equalsIgnoreCase(operation.toUpperCase()))
			column = "P_EXCEL_UPLOAD";
		if("QUERY".equalsIgnoreCase(operation.toUpperCase()))
			column = "P_INQUIRY";
		
		String sql = "SELECT "+column+" USER_RESTRINCTION FROM PRD_PROFILE_PRIVILEGES "
				+ "WHERE APPLICATION_ACCESS = ? "
				+ " AND upper(SCREEN_NAME) = upper(?) "
				+ " AND USER_GROUP = ? "
				+ " AND USER_PROFILE = ? "
				+ " AND PROFILE_STATUS = 0";
		Object[] lParams = new Object[4];
		lParams[0] = productName;
		lParams[1] = screenName;
		lParams[2] = usersVb.getUserGroup();
		lParams[3] = usersVb.getUserProfile();
		RowMapper mapper = new RowMapper() {
			@Override
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				CommonVb commonVb = new CommonVb();
				commonVb.setMakerName(rs.getString("USER_RESTRINCTION"));
				return commonVb;
			}
		};
		List<CommonVb> commonVbs = getJdbcTemplate().query(sql, lParams, mapper);
		if (commonVbs != null && !commonVbs.isEmpty()) {
			return commonVbs.get(0).getMakerName();
		}
		return null;
	}
	public VisionUsersVb getRestrictionInfo(VisionUsersVb vObject) {
		try {
			String restrictionXml = loginUserDao.getLoginUserXml();
			vObject.setCountry(CommonUtils.getValueForXmlTag(restrictionXml,"COUNTRY-LE_BOOK"));
			vObject.setAccountOfficer(CommonUtils.getValueForXmlTag(restrictionXml,"COUNTRY-LE_BOOK-ACCOUNT_OFFICER"));
			vObject.setLegalVehicle(CommonUtils.getValueForXmlTag(restrictionXml,"LEGAL_VEHICLE"));
			vObject.setLegalVehicleCleb(CommonUtils.getValueForXmlTag(restrictionXml,"LEGAL_VEHICLE-COUNTRY-LE_BOOK"));
			vObject.setOucAttribute(CommonUtils.getValueForXmlTag(restrictionXml,"OUC"));
			vObject.setProductAttribute(CommonUtils.getValueForXmlTag(restrictionXml,"PRODUCT"));
			vObject.setSbuCode(CommonUtils.getValueForXmlTag(restrictionXml,"SBU"));
			vObject.setClebTransline(CommonUtils.getValueForXmlTag(restrictionXml,"COUNTRY-LE_BOOK-TRANSLINE"));
			vObject.setClebTrasnBusline(CommonUtils.getValueForXmlTag(restrictionXml,"COUNTRY-LE_BOOK-TRANBUSLINE"));
			vObject.setClebBusinessline(CommonUtils.getValueForXmlTag(restrictionXml,"COUNTRY-LE_BOOK-BUSINESSLINE"));
			vObject.setOtherAttr(CommonUtils.getValueForXmlTag(restrictionXml,"OTHERS"));
		}catch(Exception e) {
			e.printStackTrace();
		}
		return vObject;
	}
	public String  getUserSoc(String raSoc) {
		/*String[] socArr =  visionUserVb.getRaSoc().split(",");
		StringJoiner socJoiner = new StringJoiner("','");
		String userSoc = "";
		for(String str : socArr) {
			socJoiner.add(str);
		}
		userSoc = "'"+socJoiner.toString()+"'";*/
		try {
			String sql = "SELECT DISTINCT TRANS_LINE_ID FROM RA_MST_BUSINESS_LINE_HEADER WHERE COUNTRY"+AbstractCommonDao.getDbFunction("PIPELINE", "")+"'-'"+AbstractCommonDao.getDbFunction("PIPELINE", "")+"LE_BOOK"+AbstractCommonDao.getDbFunction("PIPELINE", "")+"'-'"+AbstractCommonDao.getDbFunction("PIPELINE", "")+"BUSINESS_LINE_ID IN ("+raSoc+") "+
						" UNION ALL "+
						" SELECT DISTINCT TRANS_LINE_ID FROM RA_MST_TRANS_LINE_HEADER WHERE COUNTRY"+AbstractCommonDao.getDbFunction("PIPELINE", "")+"'-'"+AbstractCommonDao.getDbFunction("PIPELINE", "")+"LE_BOOK"+AbstractCommonDao.getDbFunction("PIPELINE", "")+"'-'"+AbstractCommonDao.getDbFunction("PIPELINE", "")+"TRANS_LINE_ID IN  ("+raSoc+") "+
						" UNION ALL "+
						" SELECT DISTINCT TRANS_LINE_ID FROM RA_MST_BUSINESS_LINE_HEADER_PEND WHERE COUNTRY"+AbstractCommonDao.getDbFunction("PIPELINE", "")+"'-'"+AbstractCommonDao.getDbFunction("PIPELINE", "")+"LE_BOOK"+AbstractCommonDao.getDbFunction("PIPELINE", "")+"'-'"+AbstractCommonDao.getDbFunction("PIPELINE", "")+"BUSINESS_LINE_ID IN ("+raSoc+") "+
						" UNION ALL "+
						" SELECT DISTINCT TRANS_LINE_ID FROM RA_MST_TRANS_LINE_HEADER_PEND WHERE COUNTRY"+AbstractCommonDao.getDbFunction("PIPELINE", "")+"'-'"+AbstractCommonDao.getDbFunction("PIPELINE", "")+"LE_BOOK"+AbstractCommonDao.getDbFunction("PIPELINE", "")+"'-'"+AbstractCommonDao.getDbFunction("PIPELINE", "")+"TRANS_LINE_ID IN  ("+raSoc+") ";
			//Object args[] = {visionUserVb.getCountry(),visionUserVb.getLeBook(),visionUserVb.getCountry(),visionUserVb.getLeBook()};
			StringJoiner joiner = new StringJoiner("','");
			String transLineIds = "";
			RowMapper mapper = new RowMapper() {
				@Override
				public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
					joiner.add(rs.getString("TRANS_LINE_ID"));
					return joiner.toString();
				}
			};
			getJdbcTemplate().query(sql,mapper);
			transLineIds = "'"+joiner.toString()+"'";
			return transLineIds;
		}catch(Exception e) {
			return null;
		}
	}
	public String applyUserRestriction(String sqlQuery) {
		VisionUsersVb visionUserVb = CustomContextHolder.getContext();
		visionUserVb = getRestrictionInfo(visionUserVb);
		//VU_CLEB,VU_CLEB_AO,VU_CLEB_LV,VU_SBU,VU_PRODUCT,VU_OUC
		if(sqlQuery.contains("#VU_CLEB")) 
			sqlQuery = replacehashPrompt(sqlQuery, "#VU_CLEB(", visionUserVb.getCountry(),visionUserVb.getUpdateRestriction());
		if(sqlQuery.contains("#VU_CLEB_AO")) 
			sqlQuery = replacehashPrompt(sqlQuery, "#VU_CLEB_AO(", visionUserVb.getAccountOfficer(),visionUserVb.getUpdateRestriction());
		if(sqlQuery.contains("#VU_CLEB_LV")) 
			sqlQuery = replacehashPrompt(sqlQuery, "#VU_CLEB_LV(", visionUserVb.getLegalVehicle(),visionUserVb.getUpdateRestriction());
		if(sqlQuery.contains("#VU_LV_CLEB"))
			sqlQuery = replacehashPrompt(sqlQuery, "#VU_LV_CLEB(", visionUserVb.getLegalVehicleCleb(),visionUserVb.getUpdateRestriction());
		if(sqlQuery.contains("#VU_SBU")) 
			sqlQuery = replacehashPrompt(sqlQuery, "#VU_SBU(", visionUserVb.getSbuCode(),visionUserVb.getUpdateRestriction());
		if(sqlQuery.contains("#VU_PRODUCT")) 
			sqlQuery = replacehashPrompt(sqlQuery, "#VU_PRODUCT(", visionUserVb.getProductAttribute(),visionUserVb.getUpdateRestriction());
		if(sqlQuery.contains("#VU_OUC")) 
			sqlQuery = replacehashPrompt(sqlQuery, "#VU_OUC(", visionUserVb.getOucAttribute(),visionUserVb.getUpdateRestriction());	
		if (sqlQuery.contains("#VU_CLEB_SOC_TL"))
			sqlQuery = replacehashPrompt(sqlQuery, "#VU_CLEB_SOC_TL(", visionUserVb.getClebTransline(),
					visionUserVb.getUpdateRestriction());
		if (sqlQuery.contains("#VU_CLEB_SOC_BL"))
			sqlQuery = replacehashPrompt(sqlQuery, "#VU_CLEB_SOC_BL(", visionUserVb.getClebBusinessline(),
					visionUserVb.getUpdateRestriction());
		if (sqlQuery.contains("#VU_CLEB_SOC")) {
			sqlQuery = replacehashPrompt(sqlQuery, "#VU_CLEB_SOC(", visionUserVb.getClebTrasnBusline(),
					visionUserVb.getUpdateRestriction());
		}
		if (sqlQuery.contains("#VU_CLEB_AOAS"))
			sqlQuery = replacehashPrompt(sqlQuery, "#VU_CLEB_AOAS(", visionUserVb.getAccountOfficer(),
					visionUserVb.getUpdateRestriction());
		if (sqlQuery.contains("#VU_OTHERS"))
			sqlQuery = replacehashPrompt(sqlQuery, "#VU_OTHERS(", visionUserVb.getOtherAttr(),
					visionUserVb.getUpdateRestriction());
		return sqlQuery;
	}
	public String replacehashPrompt(String query,String restrictStr,String restrictVal,String updateRestriction) {
		try {
			String replaceStr = "";
			String orgSbuStr = StringUtils.substringBetween(query, restrictStr, ")#");
			if (ValidationUtil.isValid(orgSbuStr)) {
				if (orgSbuStr.contains("OR") && "Y".equalsIgnoreCase(updateRestriction) && ValidationUtil.isValid(restrictVal)) {
					if (orgSbuStr.contains("OR") && "Y".equalsIgnoreCase(updateRestriction)) {
						StringJoiner conditionjoiner = new StringJoiner(" OR ");
						String[] arrsplit = orgSbuStr.split("OR");
						for (String str : arrsplit) {
							String st = str + " IN (" + restrictVal + ")";
							conditionjoiner.add(st);
						}
						replaceStr = " AND (" + conditionjoiner + ")";
					}
				}else if ("Y".equalsIgnoreCase(updateRestriction) && ValidationUtil.isValid(restrictVal)) {
					replaceStr = " AND " + orgSbuStr + " IN (" + restrictVal + ")";
				}
				restrictStr = restrictStr.replace("(", "\\(");
				orgSbuStr = orgSbuStr.replace("|", "\\|");
				orgSbuStr = orgSbuStr.replace("+", "\\+");
				query = query.replaceAll(restrictStr + orgSbuStr + "\\)#", replaceStr);
			}
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		return query;
	}
	/*public String getPrecisionBasedCcy(List<CurrencyDetailsVb> currencylst,List<AlphaSubTabVb> bookCcylst,
			String amount,String txnCcy,String postCcy,String cleb) {
		 int unitPrecision = 2;
		 try {
			 List<CurrencyDetailsVb> matchCurrencyList = new ArrayList<>();
			 List<AlphaSubTabVb> matchBookccyList = new ArrayList<>();
			 if("BCY".equalsIgnoreCase(postCcy)) {
				 matchBookccyList = bookCcylst.stream()
						 .filter(m -> cleb.equalsIgnoreCase(m.getAlphaSubTab())) //CountryLEBook Stored in AlphaSubtab
						 .collect(Collectors.toList());
				 if(matchBookccyList != null && matchBookccyList.size() > 0) {
					 unitPrecision = (int) matchBookccyList.get(0).getAlphaTab();//Unit Precision Stored in AlphaTab
				 }
			 } else if ("FCY".equalsIgnoreCase(postCcy) && "ALL".equalsIgnoreCase(txnCcy)) {
				 unitPrecision = currencylst.stream().mapToInt(CurrencyDetailsVb :: getDecimals).max().orElse(2);
			 } else if ("FCY".equalsIgnoreCase(postCcy) && !"ALL".equalsIgnoreCase(txnCcy)) {
				 matchCurrencyList = currencylst.stream().filter(m -> txnCcy.equalsIgnoreCase(m.getCcyConversionType())).collect(Collectors.toList());
				 if(matchCurrencyList != null && matchCurrencyList.size() > 0) {
					 unitPrecision = matchCurrencyList.get(0).getDecimals();
				 }		 
			 }else {
				 matchCurrencyList = currencylst.stream().filter(m -> postCcy.equalsIgnoreCase(m.getCcyConversionType())).collect(Collectors.toList());
				 if(matchCurrencyList != null && matchCurrencyList.size() > 0) {
					 unitPrecision = matchCurrencyList.get(0).getDecimals();
				 }	
			 }
			 StringBuilder zeroPattern = new StringBuilder("#,##");
			 StringBuilder pattern = new StringBuilder("#,##0.");
			 StringBuilder pattern = null;
			 if(unitPrecision == 0) {
				 pattern = new StringBuilder("#,##");
			} else {
				pattern = new StringBuilder("#,##0.");
				for (int i = 0; i < unitPrecision; i++) {
					pattern.append("0");
				}
			}
			double val = Double.parseDouble(amount.replaceAll(",", ""));
	        DecimalFormat decimalFormat = new DecimalFormat(pattern.toString());
	        return decimalFormat.format(val);
			 
		 }catch(Exception e) {
			 return amount;
		 }
	 }*/
	
	public List<CurrencyDetailsVb> getCurrencyDecimals(Boolean isLocalCcy) {
		try {
			String whereCond = "";
			if(isLocalCcy)
				whereCond = "WHERE BOOK_CCY = CURRENCY";
			String sql = "SELECT COUNTRY,LE_BOOK,BOOK_CCY,CURRENCY,UNITS_PRECISION FROM VW_LE_BOOK_CURRENCIES "+whereCond+" ";
			return getJdbcTemplate().query(sql, getCurrencyDetailsMapper());
		} catch (Exception e) {
			return new ArrayList<>();
		}
	}

	protected RowMapper getCurrencyDetailsMapper() {
		RowMapper mapper = new RowMapper() {
			@Override
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				CurrencyDetailsVb vobj = new CurrencyDetailsVb();
				vobj.setCleb(rs.getString("COUNTRY") + "-" + rs.getString("LE_BOOK"));
				vobj.setBookCcy(rs.getString("BOOK_CCY"));
				vobj.setCcyConversionType(rs.getString("CURRENCY"));
				vobj.setDecimals(rs.getInt("UNITS_PRECISION"));
				return vobj;
			}
		};
		return mapper;
	}
	
	public ExceptionCode setDecimalPrecision(List<CurrencyDetailsVb> currencylst,FeesConfigDetailsVb detailVb,FeesConfigTierVb tierVb,String type) {
		ExceptionCode exceptionCode = new ExceptionCode();
		int unitPrecision = 2;
		CurrencyDetailsVb ccyDetVb = new CurrencyDetailsVb();
		try {
			if("BCY".equalsIgnoreCase(detailVb.getPostingCcy()) || "LCY".equalsIgnoreCase(detailVb.getPostingCcy())) {
				CurrencyDetailsVb ccyVb = currencylst.stream()
				        .filter(n -> 
				        	(n.getCleb().equalsIgnoreCase(detailVb.getCountry()+"-"+detailVb.getLeBook())) 
				            && (n.getBookCcy().equalsIgnoreCase(n.getCcyConversionType())) )
				        .findFirst()
				        .orElse(ccyDetVb);
				unitPrecision = ccyVb.getDecimals();
			} else if ("FCY".equalsIgnoreCase(detailVb.getPostingCcy()) && "ALL".equalsIgnoreCase(detailVb.getTranCcy())) {
				unitPrecision = currencylst.stream()
				        .filter(n -> 
				        	(n.getCleb().equalsIgnoreCase(detailVb.getCountry()+"-"+detailVb.getLeBook())) 
				            && (!n.getBookCcy().equalsIgnoreCase(n.getCcyConversionType()))
				            )
				        .mapToInt(CurrencyDetailsVb :: getDecimals).max().orElse(2);
			} else if ("FCY".equalsIgnoreCase(detailVb.getPostingCcy()) && !"ALL".equalsIgnoreCase(detailVb.getTranCcy())) {
				CurrencyDetailsVb ccyVb = currencylst.stream()
				        .filter(n -> 
				        	(n.getCleb().equalsIgnoreCase(detailVb.getCountry()+"-"+detailVb.getLeBook())) 
				            && (n.getCcyConversionType().equalsIgnoreCase(detailVb.getTranCcy())))
				        .findFirst()
				        .orElse(ccyDetVb);
				unitPrecision = ccyVb.getDecimals();
			}else if(!"FCY".equalsIgnoreCase(detailVb.getPostingCcy()) && !"BCY".equalsIgnoreCase(detailVb.getPostingCcy())) {
				CurrencyDetailsVb ccyVb = currencylst.stream()
				        .filter(n -> 
				        	(n.getCleb().equalsIgnoreCase(detailVb.getCountry()+"-"+detailVb.getLeBook())) 
				            && (n.getCcyConversionType().equalsIgnoreCase(detailVb.getPostingCcy())) )
				        .findFirst()
				        .orElse(ccyDetVb);
				unitPrecision = ccyVb.getDecimals();
			}
			StringBuilder pattern = null;
			 if(unitPrecision == 0) {
				 pattern = new StringBuilder("#,##");
			} else {
				pattern = new StringBuilder("#,##0.");
				for (int i = 0; i < unitPrecision; i++) {
					pattern.append("0");
				}
			}
	        DecimalFormat decimalFormat = new DecimalFormat(pattern.toString());
	        if("FEE_DET".equalsIgnoreCase(type)) {
	        	detailVb.setFeeAmt(decimalFormat.format(Double.parseDouble(detailVb.getFeeAmt())));
		        detailVb.setMinFee(decimalFormat.format(Double.parseDouble(detailVb.getMinFee())));
		        detailVb.setMaxFee(decimalFormat.format(Double.parseDouble(detailVb.getMaxFee())));
		        detailVb.setFeePercentage(decimalFormat.format(Double.parseDouble(detailVb.getFeePercentage())));
		        exceptionCode.setResponse(detailVb);
	        }else if("FEE_TIER".equalsIgnoreCase(type)) {
	        	tierVb.setFeeAmt(decimalFormat.format(Double.parseDouble(tierVb.getFeeAmt())));
	        	tierVb.setAmtFrom(decimalFormat.format(Double.parseDouble(tierVb.getAmtFrom())));
	        	tierVb.setAmtTo(decimalFormat.format(Double.parseDouble(tierVb.getAmtTo())));
	        	tierVb.setFeePercentage(decimalFormat.format(Double.parseDouble(tierVb.getFeePercentage())));
	        	exceptionCode.setResponse(tierVb);
	        }
	        exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
		} catch(Exception e) {
			e.printStackTrace();
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			//Logger.ERROR("Error while converting precision..");
		}
		return exceptionCode;
	}
	@SuppressWarnings("unchecked")
	public List<AlphaSubTabVb> findActiveAlphaSubTabsByAlphaTab(int pAlphaTab) throws DataAccessException {
		String sql = "SELECT ALPHA_SUB_TAB,ALPHA_SUB_TAB"+AbstractCommonDao.getDbFunction("PIPELINE", null)+"' - '"+AbstractCommonDao.getDbFunction("PIPELINE", null)+"ALPHA_SUBTAB_DESCRIPTION ALPHA_SUBTAB_DESCRIPTION"
				+ " FROM ALPHA_SUB_TAB WHERE ALPHA_SUBTAB_STATUS = 0 AND ALPHA_TAB = ?  AND ALPHA_SUB_TAB !='Z' ORDER BY ALPHA_SUB_TAB";
		Object[] lParams = new Object[1];
		lParams[0] = pAlphaTab;
		return  getJdbcTemplate().query(sql, lParams, getGenMapper());
	}
	
	public String getRestrictionsByScreen(String screenName) throws DataAccessException {
		if (!ValidationUtil.isValid(screenName)) {
			return null;
		}
		String flag = "N";
		VisionUsersVb usersVb = CustomContextHolder.getContext();

		String sql = "SELECT count(*) cnt FROM PRD_PROFILE_PRIVILEGES " + "WHERE APPLICATION_ACCESS = ? "
				+ " AND upper(SCREEN_NAME) = upper(?) " + " AND USER_GROUP = ? " + " AND USER_PROFILE = ? "
				+ " AND PROFILE_STATUS = 0";
		Object[] lParams = new Object[4];
		lParams[0] = productName;
		lParams[1] = screenName;
		lParams[2] = usersVb.getUserGroup();
		lParams[3] = usersVb.getUserProfile();

		int cnt = getJdbcTemplate().queryForObject(sql, Integer.class, lParams);
		if (cnt > 0) {
			flag = "Y";
		}

		return flag;
	}
	
}