package com.vision.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.UncategorizedSQLException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;

import com.vision.authentication.CustomContextHolder;
import com.vision.exception.ExceptionCode;
import com.vision.exception.RuntimeCustomException;
import com.vision.util.CommonUtils;
import com.vision.util.Constants;
import com.vision.util.ValidationUtil;
import com.vision.vb.AlphaSubTabVb;
import com.vision.vb.AuditTrailDataVb;
import com.vision.vb.UserRestrictionVb;

public abstract class AbstractCommonDao{

	@Autowired
	JdbcTemplate jdbcTemplate;
	
	public static String databaseType;
	
	@Value("${app.databaseType}")
	public void setDatabaseType(String privateName) {
		AbstractCommonDao.databaseType = privateName;
	}
	
	public static String productName;
	
	@Value("${app.productName}")
	public  void setProductName(String productName) {
		AbstractCommonDao.productName = productName;
	}
	String makerApprDesc =  "(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "+getDbFunction("NVL")+"(TAPPR.MAKER,0) ) MAKER_NAME";
	String makerPendDesc =  "(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "+getDbFunction("NVL")+"(TPend.MAKER,0) ) MAKER_NAME";
	
	String verifierApprDesc =  "(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "+getDbFunction("NVL")+"(TAPPR.VERIFIER,0) ) VERIFIER_NAME";
	String verifierPendDesc =  "(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "+getDbFunction("NVL")+"(TPend.VERIFIER,0) ) VERIFIER_NAME";
	
	/*@Autowired
	CommonDao commonDao;*/


	private int batchLimit = 5000;
	
	public Connection getConnection() {
		try {
			return getJdbcTemplate().getDataSource().getConnection();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	Logger logger = LoggerFactory.getLogger(this.getClass());

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	public final DataSource getDataSource() {
		return (this.jdbcTemplate != null ? this.jdbcTemplate.getDataSource() : null);
	}
	protected String strErrorDesc = "";
	protected String strCurrentOperation = "";
	protected String strApproveOperation = "";// Current operation for Audit purpose
	protected long intCurrentUserId = 0;// Current user Id of incoming request
	protected int retVal = 0;
	protected String serviceName = "";
	protected String serviceDesc = "";// Display Name of the service.
	protected String tableName = "";
	protected String childTableName = "";
	protected String userGroup = "";
	protected String userProfile = "";
	
	/*@Autowired
	protected AuditTrailDataDao auditTrailDataDao;

	public AuditTrailDataDao getAuditTrailDataDao() {
		return auditTrailDataDao;
	}

	public void setAuditTrailDataDao(AuditTrailDataDao auditTrailDataDao) {
		this.auditTrailDataDao = auditTrailDataDao;
	}*/

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getChildTableName() {
		return childTableName;
	}

	public void setChildTableName(String childTableName) {
		this.childTableName = childTableName;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getServiceDesc() {
		return serviceDesc;
	}

	public void setServiceDesc(String serviceDesc) {
		this.serviceDesc = serviceDesc;
	}

	public String getStrErrorDesc() {
		return strErrorDesc;
	}

	public void setStrErrorDesc(String strErrorDesc) {
		this.strErrorDesc = strErrorDesc;
	}

	public String getStrCurrentOperation() {
		return strCurrentOperation;
	}

	public void setStrCurrentOperation(String strCurrentOperation) {
		this.strCurrentOperation = strCurrentOperation;
	}

	public String getStrApproveOperation() {
		return strApproveOperation;
	}

	public void setStrApproveOperation(String strApproveOperation) {
		this.strApproveOperation = strApproveOperation;
	}

	public long getIntCurrentUserId() {
		return intCurrentUserId;
	}

	public void setIntCurrentUserId(long intCurrentUserId) {
		this.intCurrentUserId = intCurrentUserId;
	}

	public int getRetVal() {
		return retVal;
	}

	public void setRetVal(int retVal) {
		this.retVal = retVal;
	}

	public String getSystemDate() {
		String sql = "";
		if("MSSQL".equalsIgnoreCase(databaseType)) {
		sql = "SELECT FORMAT(GETDATE(), 'dd-MMM-yyyy HH:mm:ss') AS SYSDATE1";
		}else if("ORACLE".equalsIgnoreCase(databaseType)) {
		sql = "SELECT To_Char(SysDate, 'DD-MM-YYYY HH24:MI:SS') AS SYSDATE1 FROM DUAL";
		}
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				return (rs.getString("SYSDATE1"));
			}
		};
		return (String) getJdbcTemplate().queryForObject(sql, null, mapper);
	}

	protected String getReferenceNo() {
		String sql = "SELECT TO_CHAR(SYSTIMESTAMP,'yyyymmddhh24missff') as timestamp FROM DUAL";
		try {
			RowMapper mapper = new RowMapper() {
				public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
					return (rs.getString("timestamp"));
				}
			};
			return (String) getJdbcTemplate().queryForObject(sql, null, mapper);
		} catch (Exception e) {
			return CommonUtils.getReferenceNo();
		}
	}

	protected ExceptionCode getResultObject(int intErrorId) {
		if (intErrorId == Constants.WE_HAVE_ERROR_DESCRIPTION)
			return CommonUtils.getResultObject(serviceDesc, intErrorId, strCurrentOperation, strErrorDesc);
		else
			return CommonUtils.getResultObject(serviceDesc, intErrorId, strCurrentOperation, "");
	}

	protected String parseErrorMsg(UncategorizedSQLException ecxception) {
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
	public String parseErrorMsg(Exception ecxception) {
		String pattern = "\\[(.*?)\\b(Insert|Select|Update|Delete)\\b(.*?)\\]";
		Pattern regex = Pattern.compile(pattern);
		Matcher matcher = regex.matcher(ecxception.getMessage());
		// Remove matched SQL statements from the error message
		StringBuffer strErrorDesc = new StringBuffer();
		while (matcher.find()) {
			matcher.appendReplacement(strErrorDesc, "");
		}
		matcher.appendTail(strErrorDesc);
		String errorDesc = strErrorDesc.toString();
		String sqlErrorCodes[] = { "ORA-00928:", "ORA-00942:", "ORA-00998:", "ORA-01400:", "ORA-01722:", "ORA-04098:",
				"ORA-01810:", "ORA-01840:", "ORA-01843:", "ORA-20001:", "ORA-20002:", "ORA-20003:", "ORA-20004:",
				"ORA-20005:", "ORA-20006:", "ORA-20007:", "ORA-20008:", "ORA-20009:", "ORA-200010:", "ORA-20011:",
				"ORA-20012:", "ORA-20013:", "ORA-20014:", "ORA-20015:", "ORA-20016:", "ORA-20017:", "ORA-20018:",
				"ORA-20019:", "ORA-20020:", "ORA-20021:", "ORA-20022:", "ORA-20023:", "ORA-20024:", "ORA-20025:",
				"ORA-20102:", "ORA-20105:", "ORA-01422:", "ORA-06502:", "ORA-20082:", "ORA-20030:", "ORA-20010:",
				"ORA-20034:", "ORA-20043:", "ORA-20111:", "ORA-06512:", "ORA-04088:", "ORA-06552:", "ORA-00001:" };
		for (String sqlErrorCode : sqlErrorCodes) {
			if (ValidationUtil.isValid(errorDesc) && errorDesc.lastIndexOf(sqlErrorCode) >= 0) {
				errorDesc = errorDesc.substring(
						errorDesc.lastIndexOf(sqlErrorCode) + sqlErrorCode.length() + 1, errorDesc.length());
				if (errorDesc.indexOf("ORA-06512:") >= 0) {
					errorDesc = errorDesc.substring(0, errorDesc.indexOf("ORA-06512:"));
				}
			}
		}
		return errorDesc;
	}
	protected RuntimeCustomException buildRuntimeCustomException(ExceptionCode rObject) {
		RuntimeCustomException lException = new RuntimeCustomException(rObject);
		return lException;
	}

	/**
	 * This method used to get the status of the Build Module
	 *
	 * @param String Service Name
	 * @returns String The running status of Build Module
	 */
	protected String getBuildModuleStatus(String country, String leBook) {
		int lockCount = 0;

		StringBuffer strBufApprove = new StringBuffer("Select count(LOCK_STATUS) STATUS_COUNT " + "FROM VISION_LOCKING "
				+ "WHERE LOCK_STATUS = 'Y' " + " AND SERVICE_NAME = ?" + " AND COUNTRY = ?" + " AND LE_BOOK = ?");

		StringBuffer defaultQuery = new StringBuffer(
				" Select count(LOCK_STATUS) STATUS_COUNT FROM VISION_LOCKING WHERE LOCK_STATUS = 'Y'  "
						+ "AND SERVICE_NAME = ? AND COUNTRY = "+getDbFunction("NVL")+"((SELECT VALUE FROM VISION_VARIABLES WHERE VARIABLE ='DEFAULT_COUNTRY'),'NG') "
						+ "AND LE_BOOK = "+getDbFunction("NVL")+"((SELECT VALUE FROM VISION_VARIABLES WHERE VARIABLE ='DEFAULT_LEBOOK'),'01')");
		try {

			if (ValidationUtil.isValid(country) && ValidationUtil.isValid(leBook)) {
				Object objParams[] = new Object[3];
				objParams[0] = getServiceName();
				objParams[1] = country;
				objParams[2] = leBook;
				lockCount = getJdbcTemplate().queryForObject(strBufApprove.toString(), objParams, Integer.class);
			} else {
				Object objParams[] = new Object[1];
				objParams[0] = getServiceDesc();
				lockCount = getJdbcTemplate().queryForObject(defaultQuery.toString(), objParams, Integer.class);
			}
			if (lockCount > 0)
				return "RUNNING";

			return "NOT-RUNNING";
		} catch (Exception ex) {
			ex.printStackTrace();
			return "NOT-RUNNING";
		}
	}

	public String removeDescLeBook(String promptString) {
		if (ValidationUtil.isValid(promptString)) {
			return promptString.substring(0,
					promptString.indexOf("-") > 0 ? promptString.indexOf("-") - 1 : promptString.length());
		} else
			return promptString;
	}

	public List<AlphaSubTabVb> greaterLesserSymbol(String ipString, boolean chgFlag) {
		List<AlphaSubTabVb> collTemp = null;
		try {
			if (chgFlag = true) {
				ipString = ipString.replaceAll("lessRep", "<").replaceAll("grtRep", ">");
			} else {
				ipString = ipString.replaceAll("<", "lessRep").replaceAll(">", "grtRep");
			}
			collTemp = getJdbcTemplate().query(ipString, getMapperforDropDown());
		} catch (Exception e) {
			e.printStackTrace();
			return collTemp;
		}

		return collTemp;
	}

	public RowMapper getMapperforDropDown() {
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				AlphaSubTabVb alphaSubTabVb = new AlphaSubTabVb();
				alphaSubTabVb.setAlphaSubTab(rs.getString(1));
				alphaSubTabVb.setDescription(rs.getString(2));
				return alphaSubTabVb;
			}
		};
		return mapper;
	}

	public String getServerCredentials(String enironmentVariable, String node, String columnName) {
		String sql = "SELECT " + columnName + " FROM VISION_NODE_CREDENTIALS WHERE SERVER_ENVIRONMENT= ?"
				+ " AND NODE_NAME= ? AND ROWNUM<2";
		Object[] args = {enironmentVariable,node};
		return getJdbcTemplate().queryForObject(sql,args, String.class);
	}

	public String getUserGroup() {
		return userGroup;
	}

	public void setUserGroup(String userGroup) {
		this.userGroup = userGroup;
	}

	public String getUserProfile() {
		return userProfile;
	}

	public void setUserProfile(String userProfile) {
		this.userProfile = userProfile;
	}
	public Map<String, List> getRestrictionTreeForLogin() throws DataAccessException {
		String sql = "select MACROVAR_NAME,TAG_NAME, DISPLAY_NAME, MACROVAR_DESC from MACROVAR_TAGGING where MACROVAR_TYPE = 'DATA_RESTRICTION' order by MACROVAR_NAME, TAG_NO";
		return getJdbcTemplate().query(sql, new ResultSetExtractor<Map<String, List>>(){
			@Override
			public Map<String, List> extractData(ResultSet rs) throws SQLException,
					DataAccessException {
				Map<String, List> returnMap = new HashMap<String, List>();
				while(rs.next()){
					if(returnMap.get(rs.getString("MACROVAR_NAME"))==null){
						List<String> tagList = new ArrayList<String>();
						tagList.add(rs.getString("TAG_NAME"));
						returnMap.put(rs.getString("MACROVAR_NAME"), tagList);
					} else {
						List<String> tagList = returnMap.get(rs.getString("MACROVAR_NAME"));
						tagList.add(rs.getString("TAG_NAME"));
						returnMap.put(rs.getString("MACROVAR_NAME"), tagList);
					}
				}
				return returnMap;
			}
		});
	}
	public Connection returnConnection() {
		return getConnection();
	}
	private void dropTemporaryCreatedTable(String temporaryTableName) {
		try {
			String sql = "DROP TABLE "+temporaryTableName+" PURGE";
			getJdbcTemplate().execute(sql);
		}catch(Exception e) {}
	}
	
	private Map<String, String> returnDateConvertionSyntaxMap(String dataBaseType) {
		
		String sql = "SELECT * FROM MACROVAR_TAGGING WHERE MACROVAR_TYPE = 'DATE_CONVERT' AND UPPER(MACROVAR_NAME) = UPPER(?)";
		Object[] args = {dataBaseType};
		
		return getJdbcTemplate().query(sql,args, new ResultSetExtractor<Map<String, String>>(){
			@Override
			public Map<String, String> extractData(ResultSet rs) throws SQLException, DataAccessException {
				Map<String, String> map = new HashMap<String, String>();
				while(rs.next()) {
					map.put(rs.getString("TAG_NAME"), rs.getString("DISPLAY_NAME"));
				}
				return map;
			}
		});
		
	}
	
	private Map<String, String> returnDateFormatSyntaxMap(String dataBaseType) {
		
		String sql = "SELECT * FROM MACROVAR_TAGGING WHERE MACROVAR_TYPE = 'DATE_FORMAT' AND UPPER(MACROVAR_NAME) = UPPER(?)";
		Object[] args = {dataBaseType};
		return getJdbcTemplate().query(sql,args, new ResultSetExtractor<Map<String, String>>(){
			@Override
			public Map<String, String> extractData(ResultSet rs) throws SQLException, DataAccessException {
				Map<String, String> map = new HashMap<String, String>();
				while(rs.next()) {
					map.put(rs.getString("TAG_NAME"), rs.getString("DISPLAY_NAME"));
				}
				return map;
			}
		});
		
	}
	
	public int getBatchLimit() {
		return batchLimit;
	}
	
	public void setBatchLimit(int batchLimit) {
		this.batchLimit = batchLimit;
	}
	
	public String createRandomTableName() {
		String sessionId = ValidationUtil.generateRandomNumberForVcReport();
		return "VC_CV_"+intCurrentUserId+sessionId;
	}
	
	public String getVisionDynamicHashVariable(String variableName){
		try{
			String sql = "select VARIABLE_SCRIPT from vision_dynamic_hash_var where VARIABLE_NAME = ?";
			String variable="VU_RESTRICTION_"+variableName;
			Object[] args= {variable};
			return getJdbcTemplate().queryForObject(sql,args, String.class);
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	/*public Map<String, List> getRestrictionTree() throws DataAccessException {
		String sql = "select MACROVAR_NAME,TAG_NAME, DISPLAY_NAME, MACROVAR_DESC from MACROVAR_TAGGING where MACROVAR_TYPE = 'DATA_RESTRICTION' order by MACROVAR_NAME, TAG_NO";
		return getJdbcTemplate().query(sql, new ResultSetExtractor<Map<String, List>>(){
			@Override
			public Map<String, List> extractData(ResultSet rs) throws SQLException,
					DataAccessException {
				Map<String, List> returnMap = new HashMap<String, List>();
				while(rs.next()){
					if(returnMap.get(rs.getString("MACROVAR_NAME"))==null){
						List<String> tagList = new ArrayList<String>();
						tagList.add(rs.getString("TAG_NAME"));
						returnMap.put(rs.getString("MACROVAR_NAME"), tagList);
					} else {
						List<String> tagList = returnMap.get(rs.getString("MACROVAR_NAME"));
						tagList.add(rs.getString("TAG_NAME"));
						returnMap.put(rs.getString("MACROVAR_NAME"), tagList);
					}
				}
				return returnMap;
			}
		});
	}*/
	public int insertAuditTrail(AuditTrailDataVb auditTrailDataVb){
		int result=0;
		String remoteIpAddress = "";
		if(ValidationUtil.isValid(CustomContextHolder.getContext())){
			remoteIpAddress = CustomContextHolder.getContext().getRemoteAddress();	
		}
		
		try{
			String query = "Insert Into AUDIT_TRAIL_DATA(REFERENCE_NO, SUB_REFERENCE_NO, TABLE_NAME, CHILD_TABLE_NAME,"+
				"TABLE_SEQUENCE, AUDIT_MODE, DATE_CREATION,MAKER, IP_ADDRESS, AUDIT_DATA_OLD, AUDIT_DATA_NEW) Values (?, ?, ?, ?, ?, ?, " + getDbFunction("SYSDATE") +" , ?, ?, ?, ?)";
			Object[] args = {auditTrailDataVb.getReferenceNo(), auditTrailDataVb.getSubReferenceNo(),auditTrailDataVb.getTableName(),
				auditTrailDataVb.getChildTableName(),getMaxSequence(auditTrailDataVb.getChildTableName()),auditTrailDataVb.getAuditMode(),
				auditTrailDataVb.getMaker(), remoteIpAddress,auditTrailDataVb.getAuditDataOld(),auditTrailDataVb.getAuditDataNew()};
			return getJdbcTemplate().update(query,args);
			
		}catch(Exception e){
			//System.out.println(e.getMessage());
			strErrorDesc = e.getMessage();
			//logger.error("Update Error : "+e.getMessage());
			return result;
		}finally{
			/*if (ps != null) {
				try {
					ps.close();
					ps=null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}*/
		}
	}
	public int getMaxSequence(String strTableName){
		int tableSequence  =1;
		if (strTableName == null || strTableName.length() == 0)
			return 1;
		StringBuffer strBufApprove = new StringBuffer("Select (case when max(TABLE_SEQUENCE) is null then 0 else max(TABLE_SEQUENCE) end) as TABLE_SEQUENCE FROM Vision_Table_Columns ");
		strBufApprove.append(" Where TABLE_NAME = ? ");
		Object objParams[] = {strTableName};
		int tableSequnceTemp = getJdbcTemplate().queryForObject(strBufApprove.toString(), objParams, Integer.class);
		if(tableSequnceTemp == 0){
			return tableSequence;
		}
		return tableSequnceTemp;
	}
	public static String getDbFunction(String reqFunction, String... args) {
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
			case "DD_Mon_RRRR":
				functionName = "dd-MMM-yyyy";
				break;
			case "DD_MM_YYYY":
				functionName = "dd-MM-yyyy";
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
			
			case "LENGTH":
				functionName = "len";
				break;
			case "SUBSTR":
				functionName = "SUBSTRING";
				break;
			
			case "DUAL":
				functionName = null;
				break;
			case "SYSTIMESTAMP":
				functionName = "SYSDATETIME()";
				break;
			case "SYSTIMESTAMP_FORMAT":
				functionName = "yyyyMMddHHmmssffffff";
				break;
		
			case "FN_UNIX_TIME_TO_DATE":
				functionName = "[dbo].[FN_UNIX_TIME_TO_DATE]";
				break;
			
			case "DATEDIFF":
				String columnName = (args!=null && args.length>0)?args[0]:"";
				functionName = String.format("DATEDIFF(DAY, ISNULL(%s, GETDATE()), GETDATE())", columnName);
				break;
				
			case "DATE_CREATION":
				functionName = "CONVERT(datetime, ?, 103)";
				break;	
			case "EXCEPT":
				functionName = "EXCEPT";
				break;	
			}
		}else if("ORACLE".equalsIgnoreCase(databaseType)) {
			switch(reqFunction) {case "DATEFUNC":
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
			case "DD_Mon_RRRR":
				functionName = "DD-Mon-RRRR";
				break;
			case "DD_MM_YYYY":
				functionName = "DD-MM-YYYY";
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
			case "LENGTH":
				functionName = "LENGTH";
				break;
			case "SUBSTR":
				functionName = "SUBSTR";
				break;
			
			
			case "DUAL":
				functionName = "FROM DUAL";
				break;
			case "SYSTIMESTAMP":
				functionName = "SYSTIMESTAMP";
				break;
			case "SYSTIMESTAMP_FORMAT":
				functionName = "yyyymmddhh24missff";
				break;
			
		
			case "FN_UNIX_TIME_TO_DATE":
				functionName = "FN_UNIX_TIME_TO_DATE";
				break;
				
			case "DATEDIFF":
				String columnName = (args!=null && args.length>0)?args[0]:"";
				functionName = String.format("NVL(%s, SYSDATE) - SYSDATE",columnName);
//				functionName = String.format("EXTRACT(DAY FROM (NVL(%s, SYSDATE) - SYSDATE))",columnName);
				break;
				
			case "DATE_CREATION":
				functionName = "To_Date(?, 'DD-MM-YYYY HH24:MI:SS')";
				break;	
			case "EXCEPT":
				functionName = "MINUS";
				break;
			
			}
		}
		
		return functionName;
	}
	
	public List<UserRestrictionVb> getRestrictionTree() throws DataAccessException {
		String sql = "select MACROVAR_NAME,TAG_NAME, DISPLAY_NAME, MACROVAR_DESC from MACROVAR_TAGGING where MACROVAR_TYPE = 'DATA_RESTRICTION' order by MACROVAR_NAME, TAG_NO";
		return getJdbcTemplate().query(sql, new ResultSetExtractor<List<UserRestrictionVb>>() {
			@Override
			public List<UserRestrictionVb> extractData(ResultSet rs) throws SQLException, DataAccessException {
				List<UserRestrictionVb> returnList = new ArrayList<UserRestrictionVb>();
				while (rs.next()) {
					String macroVar = rs.getString("MACROVAR_NAME");
					List<UserRestrictionVb> filteredList = returnList.stream()
							.filter(vb -> macroVar.equalsIgnoreCase(vb.getMacrovarName())).collect(Collectors.toList());
					if (filteredList != null && filteredList.size() > 0) {
						List<UserRestrictionVb> childrenList = filteredList.get(0).getChildren();
						childrenList.add(new UserRestrictionVb(macroVar, rs.getString("TAG_NAME"),
								rs.getString("DISPLAY_NAME"), rs.getString("MACROVAR_DESC")));
					} else {
						List<UserRestrictionVb> childrenList = new ArrayList<UserRestrictionVb>();
						childrenList.add(new UserRestrictionVb(macroVar, rs.getString("TAG_NAME"),
								rs.getString("DISPLAY_NAME"), rs.getString("MACROVAR_DESC")));
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
	public String dbFunctionFormats(String columnName,String formatReq,String dec) {
		String returnStr = "";
		if("MSSQL".equalsIgnoreCase(databaseType)) {
			switch(formatReq) {

				case "NUM_FORMAT":
					//returnStr = "RTRIM(LTRIM("+columnName+"))";
					returnStr = "RTRIM(LTRIM(FORMAT("+columnName+",'N"+dec+"')))";
					break;
					
				case "DATETIME_FORMAT":
					returnStr = "Format("+columnName+", 'dd-MMM-yyyy HH:mm:ss')";
					break;
				
				case "DATE_FORMAT":
					returnStr = "Format("+columnName+", 'dd-MMM-yyyy')";
					break;
			}
		}else if("ORACLE".equalsIgnoreCase(databaseType)) {
			switch(formatReq) {
				case "NUM_FORMAT":
					//returnStr = "TRIM(TO_CHAR("+columnName+",'999,999,999,999,990.99990'))";
					returnStr = "TRIM(TO_CHAR("+columnName+",'999,999,999,999,990.99990'))";
					break;
					
				case "DATETIME_FORMAT":
					returnStr = "TO_CHAR("+columnName+",'DD-Mon-RRRR HH24:MI:SS')";
					break;
					
				case "DATE_FORMAT":
					returnStr = "TO_CHAR("+columnName+",'DD-Mon-RRRR')";
					break;	
					
			}
		}
		return returnStr;
	}
}