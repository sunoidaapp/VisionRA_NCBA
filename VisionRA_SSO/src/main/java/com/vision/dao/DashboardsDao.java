/**
 * Author : Deepak.s
 */
package com.vision.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetProvider;

import org.json.JSONObject;
import org.json.XML;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.vision.authentication.CustomContextHolder;
import com.vision.exception.ExceptionCode;
import com.vision.util.ChartUtils;
import com.vision.util.CommonUtils;
import com.vision.util.Constants;
import com.vision.util.ValidationUtil;
import com.vision.vb.AlphaSubTabVb;
import com.vision.vb.DashboardFilterVb;
import com.vision.vb.DashboardTabVb;
import com.vision.vb.DashboardTilesVb;
import com.vision.vb.DashboardVb;
import com.vision.vb.PrdQueryConfig;
import com.vision.vb.VisionUsersVb;

@Component
public class DashboardsDao extends AbstractDao<DashboardVb>{
	@Autowired
	CommonDao commonDao;
	@Value("${app.clientName}")
	private String clientName;
	@Value("${app.productName}")
	private String productName;
	@Value("${app.databaseType}")
	private String databaseType;
	@Autowired
	ChartUtils chartUtils;
	@Autowired
	ReportsDao reportsDao;
	@Autowired
	CommonApiDao commonApiDao;
	//Connection con = null;
	
	public DashboardVb getDashboardDetail(DashboardVb dObj){
		setServiceDefaults();
		DashboardVb dashboardVb = new DashboardVb();
		String query = "";
		try
		{	
			if("ORACLE".equalsIgnoreCase(databaseType)) {
				query = " SELECT t1.Dashboard_Id,Dashboard_Name,Total_Tabs,Data_Source_Ref,Filter,Filter_Ref_Code," + 
						"  NVL((Select Min(t2.DS_Theme) from PRD_USER_Dashboard_Theme t2 where t2.dashboard_ID=t1.dashboard_ID and t2.vision_ID= '"+CustomContextHolder.getContext().getVisionId()+"'),t1.DS_THEME) DS_THEME," + 
						"  t1.DS_THEME_AT " + 
						"  from PRD_DASHBOARDS t1 where Dashboard_ID = '"+dObj.getDashboardId()+"' ";
			} else if("MSSQL".equalsIgnoreCase(databaseType)) {
				query = " Select t1.Dashboard_Id,Dashboard_Name,Total_Tabs,Data_Source_Ref,Filter,Filter_Ref_Code," + 
					"  isnull((Select Min(t2.DS_Theme) from PRD_USER_Dashboard_Theme t2 where t2.dashboard_ID=t1.dashboard_ID and t2.vision_ID= '"+CustomContextHolder.getContext().getVisionId()+"'),t1.DS_THEME) DS_THEME," + 
					"  t1.DS_THEME_AT " + 
					"  from PRD_DASHBOARDS t1 where Dashboard_ID = '"+dObj.getDashboardId()+"' ";
			}
			List<DashboardVb> list = getJdbcTemplate().query(query, getDashboardDetMapper());
			return (list!=null && list.size()>0)?list.get(0):dashboardVb;
		}catch(Exception ex){
			ex.printStackTrace();
			logger.error("Exception while getting Dashboard Detail...!!");
			return dashboardVb;
		}
	}
	private RowMapper getDashboardDetMapper(){
		RowMapper mapper = new RowMapper(){
			@Override
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				DashboardVb vObject = new DashboardVb();
				vObject.setDashboardId(rs.getString("Dashboard_Id"));
				vObject.setDashboardName(rs.getString("Dashboard_Name"));
				vObject.setTotalTabs(rs.getString("Total_Tabs"));
				vObject.setDataSourceRef(rs.getString("Data_Source_Ref"));
				vObject.setFilterFlag(rs.getString("Filter"));
				vObject.setFilterRefCode(rs.getString("Filter_Ref_Code"));
				vObject.setDashboard_Theme(rs.getString("DS_THEME"));
				vObject.setDashboar_ThemeAT(rs.getInt("DS_THEME_AT"));
				return vObject;
			}
		};
		return mapper;
	}
	public List<DashboardTabVb> getDashboardTabDetails(DashboardVb dObj){
		setServiceDefaults();
		List<DashboardTabVb> collTemp = null;
		try
		{			
			String query = " Select Tab_ID,Tab_Name,Template_ID from PRD_DASHBOARD_TABS where Status = 0 and Dashboard_id= '"+dObj.getDashboardId()+"' order by Tab_Sequence ";
			collTemp = getJdbcTemplate().query(query,getDashboardTabMapper());
			return collTemp;
		}catch(Exception ex){
			ex.printStackTrace();
			logger.error("Exception while getting Dashboard Detail...!!");
			return null;
		}
	}
	private RowMapper getDashboardTabMapper(){
		RowMapper mapper = new RowMapper(){
			@Override
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				DashboardTabVb vObject = new DashboardTabVb();
				vObject.setTabId(rs.getString("Tab_ID"));
				vObject.setTabName(rs.getString("Tab_Name"));
				vObject.setTemplateId(rs.getString("Template_ID"));
				return vObject;
			}
		};
		return mapper;
	}
	public List<DashboardTilesVb> getDashboardTileDetails(DashboardTabVb dObj){
		setServiceDefaults();
		List<DashboardTilesVb> collTemp = null;
		try
		{			
			String query = "Select Dashboard_ID,Tab_ID,Tile_ID,Tile_Caption,Sequence,Query_ID,TILE_TYPE,DRILL_DOWN_FLAG,(SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE "+
					" ALPHA_TAB = 7005 AND ALPHA_SUB_TAB = T1.CHART_TYPE) CHART_TYPE,"
					+ "TILE_PROPERTY_XML,PLACE_HOLDER_COUNT,DOUBLE_WIDTH_FLAG,SUB_TILES,Transition_Effect,Theme from PRD_DASHBOARD_TILES T1 where Dashboard_ID = '"+dObj.getDashboardId()+"' and Tab_ID = '"+dObj.getTabId()+"' Order by Sequence " ;
			collTemp = getJdbcTemplate().query(query,getDashboardTileMapper());
			return collTemp;
		}catch(Exception ex){
			ex.printStackTrace();
			logger.error("Exception while getDashboardTileDetails...!!");
			return null;
		}
	}
	private RowMapper getDashboardTileMapper(){
		RowMapper mapper = new RowMapper(){
			@Override
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				DashboardTilesVb vObject = new DashboardTilesVb();
				vObject.setDashboardId(rs.getString("Dashboard_ID"));
				vObject.setTabId(rs.getString("Tab_ID"));
				vObject.setTileId(rs.getString("Tile_ID"));
				vObject.setTileCaption(rs.getString("Tile_Caption"));
				vObject.setTileSequence(rs.getInt("Sequence"));
				vObject.setQueryId(rs.getString("Query_ID"));
				vObject.setTileType(rs.getString("TILE_TYPE"));
				vObject.setPropertyAttr(rs.getString("TILE_PROPERTY_XML"));
				vObject.setPlaceHolderCnt(rs.getInt("PLACE_HOLDER_COUNT"));
				vObject.setChartType(rs.getString("CHART_TYPE"));
				vObject.setDoubleWidthFlag(rs.getString("DOUBLE_WIDTH_FLAG"));
				if("Y".equalsIgnoreCase(rs.getString("DRILL_DOWN_FLAG"))) {
					vObject.setDrillDownFlag(true);
				}else {
					vObject.setDrillDownFlag(false);
				}
				vObject.setSubTiles(rs.getString("SUB_TILES"));
				vObject.setTransitionEffect(rs.getString("Transition_Effect"));
				vObject.setTheme(rs.getString("Theme"));
				return vObject;
			}
		};
		return mapper;
	}
	public List<DashboardTilesVb> getTileDrillDownDetails(String dashboardId,String tabId,int parentSequence,String subSequence,Boolean subTileFlag){
		setServiceDefaults();
		List<DashboardTilesVb> collTemp = null;
		try
		{			
			String query = "Select Dashboard_ID,Tab_ID,Tile_ID,Tile_Caption,Sequence,Query_ID,TILE_TYPE,DRILL_DOWN_FLAG," + 
					" (SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE "+
					" ALPHA_TAB = 7005 AND ALPHA_SUB_TAB = T1.CHART_TYPE) CHART_TYPE,TILE_PROPERTY_XML,PLACE_HOLDER_COUNT,DOUBLE_WIDTH_FLAG,'N' SUB_TILES,'' Transition_Effect,Theme from PRD_DASHBOARD_TILES_DD T1" + 
					" where Dashboard_ID = '"+dashboardId+"' and Tab_ID = '"+tabId+"' and Parent_Sequence = '"+parentSequence+"' " ;
			
			if(subTileFlag) {
				query = query + " and Sub_Sequence= '"+subSequence+"' ";
			}
			collTemp = getJdbcTemplate().query(query,getDashboardTileMapper());
			return collTemp;
		}catch(Exception ex){
			ex.printStackTrace();
			logger.error("Exception while getDashboardTileDetails...!!");
			return null;
		}
	}
	public ExceptionCode getTilesReportData(DashboardTilesVb vObject) throws SQLException{
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"); 
		setServiceDefaults();
		Statement stmt = null;
		ResultSet rs = null;
		String resultData = "";
		DecimalFormat dfDec = new DecimalFormat("0.00");
		DecimalFormat dfNoDec = new DecimalFormat("0");
		List<PrdQueryConfig> sqlQueryList = new ArrayList<PrdQueryConfig>();
		PrdQueryConfig prdQueryConfig = new PrdQueryConfig();
		Connection conExt =null;
		ExceptionCode exceptionCode = new ExceptionCode();
		Statement stmt1 = null;
		try
		{	
			sqlQueryList = reportsDao.getSqlQuery(vObject.getQueryId());
			if (sqlQueryList != null && sqlQueryList.size() > 0) {
				prdQueryConfig = sqlQueryList.get(0);
			} else {
				exceptionCode.setOtherInfo(vObject);
				exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
				exceptionCode.setErrorMsg("Query not maintained for the Data Ref Id[" + vObject.getQueryId()+ "]");
				return exceptionCode;
			}
			
			ExceptionCode exConnection = commonDao.getReqdConnection(conExt,prdQueryConfig.getDbConnectionName());
			if(exConnection.getErrorCode() != Constants.ERRONEOUS_OPERATION && exConnection.getResponse() != null) {
				conExt = (Connection)exConnection.getResponse();
			}else {
				exceptionCode.setErrorCode(exConnection.getErrorCode());
				exceptionCode.setErrorMsg(exConnection.getErrorMsg());
				return exceptionCode;
			}
			String orginalQuery = prdQueryConfig.getQueryProc();
			orginalQuery = replacePromptVariables(orginalQuery, vObject);
			
			stmt1 = conExt.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, 
				    ResultSet.CONCUR_READ_ONLY);
			
			ResultSet rsData = stmt1.executeQuery(orginalQuery);
			ResultSetMetaData metaData = rsData.getMetaData();
			int colCount = metaData.getColumnCount();
			HashMap<String,String> columns = new HashMap<String,String>();
			Boolean dataAvail = false;
			while(rsData.next()){
				for(int cn = 1;cn <= colCount;cn++) {
					String columnName = metaData.getColumnName(cn);
					columns.put(columnName.toUpperCase(), rsData.getString(columnName));
				}
				dataAvail = true;
				break;
			}
			rsData.close();
			if(!dataAvail) {
				exceptionCode.setErrorCode(Constants.NO_RECORDS_FOUND);
				exceptionCode.setErrorMsg("No Records Found");
				return exceptionCode;
			}
			
			String fieldProp = vObject.getPropertyAttr();
			fieldProp = ValidationUtil.isValid(fieldProp)?fieldProp.replaceAll("\n", "").replaceAll("\r", ""):"";
			for(int ctr = 1;ctr <= vObject.getPlaceHolderCnt();ctr++) {
				String placeHolder = CommonUtils.getValueForXmlTag(fieldProp, "PLACEHOLDER"+ctr);
				String sourceCol = CommonUtils.getValueForXmlTag(placeHolder, "SOURCECOL");
				String dataType = CommonUtils.getValueForXmlTag(placeHolder, "DATA_TYPE");
				String numberFormat = CommonUtils.getValueForXmlTag(placeHolder, "NUMBERFORMAT");
				String scaling = CommonUtils.getValueForXmlTag(placeHolder, "SCALING");
				if(!ValidationUtil.isValid(placeHolder) || !ValidationUtil.isValid(sourceCol)) {
					continue;
				}
				if(ValidationUtil.isValid(sourceCol)) {
					String fieldValue = columns.get(sourceCol);
					/*Double val = 0.00;*/
					String prefix="";
					if(ValidationUtil.isValid(scaling) && "Y".equalsIgnoreCase(scaling) && ValidationUtil.isValid(fieldValue)) {
						Double dbValue = Math.abs(Double.parseDouble(fieldValue));
						if(dbValue > 1000000000) {
							dbValue = Double.parseDouble(fieldValue)/1000000000;
							prefix = "B";
						}else if(dbValue > 1000000) {
							dbValue = Double.parseDouble(fieldValue)/1000000;
							prefix = "M";
						}else if(dbValue > 10000) {
							dbValue = Double.parseDouble(fieldValue)/1000;
							prefix = "K";
						}
						String afterDecimalVal = String.valueOf(dbValue);
						if(!afterDecimalVal.contains("E")) {
							afterDecimalVal = afterDecimalVal.substring(afterDecimalVal.indexOf ( "." )+1);
							if(Double.parseDouble(afterDecimalVal) > 0)
								fieldValue = dfDec.format(dbValue)+prefix;
							else
								fieldValue = dfNoDec.format(dbValue)+prefix;
						}else {
							fieldValue = "0.00";
						}
					}
					if(ValidationUtil.isValid(fieldValue) && ValidationUtil.isValid(numberFormat) && "Y".equalsIgnoreCase(numberFormat) && !ValidationUtil.isValid(prefix)) {
						DecimalFormat decimalFormat = new DecimalFormat("#,###.##");
						double tmpVal = Double.parseDouble(fieldValue);
						fieldValue = decimalFormat.format(tmpVal);
					}
					/*if(ValidationUtil.isValid(fieldValue))*/
					fieldProp = fieldProp.replaceAll(sourceCol, fieldValue);
				}
			}
			int PRETTY_PRINT_INDENT_FACTOR = 4;
			JSONObject xmlJSONObj = XML.toJSONObject(fieldProp);
			resultData = xmlJSONObj.toString(PRETTY_PRINT_INDENT_FACTOR).replaceAll("[\\n\\t ]", "");
			exceptionCode.setResponse(resultData);
			exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
		}catch(Exception ex){
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg(ex.getMessage());
		}finally {
			if(stmt1 != null)
				stmt1.close();
			if(conExt != null)
				conExt.close();
		}
		return exceptionCode;
	}
	public ExceptionCode getGridData(DashboardTilesVb vObject) throws SQLException{
		setServiceDefaults();
		Statement stmt = null;
		ResultSet rs = null;
		ArrayList resultlst = new ArrayList();
		List<PrdQueryConfig> sqlQueryList = new ArrayList<PrdQueryConfig>();
		PrdQueryConfig prdQueryConfig = new PrdQueryConfig();
		ExceptionCode exceptionCode = new ExceptionCode();
		Connection conExt  = null;
		try
		{
			//String orginalQuery = getReportQuery(vObject.getQueryId());
			sqlQueryList = reportsDao.getSqlQuery(vObject.getQueryId());
			if (sqlQueryList != null && sqlQueryList.size() > 0) {
				prdQueryConfig = sqlQueryList.get(0);
			}else {
				exceptionCode.setOtherInfo(vObject);
				exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
				exceptionCode.setErrorMsg("Query not maintained for the Data Ref Id[" + vObject.getQueryId() + "]");
				return exceptionCode;
			}
			ExceptionCode exConnection = commonDao.getReqdConnection(conExt,prdQueryConfig.getDbConnectionName());
			if(exConnection.getErrorCode() != Constants.ERRONEOUS_OPERATION && exConnection.getResponse() != null) {
				conExt = (Connection)exConnection.getResponse();
			}else {
				exceptionCode.setErrorCode(exConnection.getErrorCode());
				exceptionCode.setErrorMsg(exConnection.getErrorMsg());
				return exceptionCode;
			}
			String orginalQuery = prdQueryConfig.getQueryProc();
			orginalQuery = replacePromptVariables(orginalQuery, vObject);
			
			ArrayList datalst = new ArrayList();
			stmt = conExt.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			ResultSet rsData = stmt.executeQuery(orginalQuery);
			ResultSetMetaData metaData = rsData.getMetaData();
			int colCount = metaData.getColumnCount();
			HashMap<String,String> columns = new HashMap<String,String>();
			while(rsData.next()){
				columns = new HashMap<String, String>();
				for (int cn = 1; cn <= colCount; cn++) {
					String columnName = metaData.getColumnName(cn);
					columns.put(columnName.toUpperCase(), rsData.getString(columnName));
				}
				datalst.add(columns);
			}
			rsData.close();
			if(datalst == null && datalst.size() == 0) {
				exceptionCode.setErrorCode(Constants.NO_RECORDS_FOUND);
				exceptionCode.setErrorMsg("No Records Found");
				return exceptionCode;
			}
			//HashMap<String,String> columns = (HashMap<String,String>)getJdbcTemplate().query(orginalQuery, mapper);
			String fieldProp = vObject.getPropertyAttr();
			fieldProp = ValidationUtil.isValid(fieldProp)?fieldProp.replaceAll("\n", "").replaceAll("\r", ""):"";
			String columnValueDetails = CommonUtils.getValueForXmlTag(fieldProp, "COLUMNVALUE");
			String colValArray[] = {};
			if(ValidationUtil.isValid(columnValueDetails))
				colValArray = columnValueDetails.split(",");
			
			ArrayList finalDatalst = new ArrayList();
			ArrayList columnlst = new ArrayList();
			ArrayList columnFormatlst = new ArrayList();
			for(int ctr = 0;ctr < datalst.size();ctr++) {
				LinkedHashMap <String,String> finalDataMap = new LinkedHashMap <String,String>();
				HashMap<String, String> dataMap = (HashMap<String, String>)datalst.get(ctr);
				for(int ct = 0;ct < colValArray.length;ct++) {
					String colArr[] = colValArray[ct].split("!@#");
					String colName = colArr[0]; 
					String colSrc = colArr[1];
					String colType = colArr[2];
					finalDataMap.put(colName, dataMap.get(colSrc));
					if(ctr == 0) {
						columnlst.add(colName);
						columnFormatlst.add(colType);
					}
				}
				finalDatalst.add(finalDataMap);
			}
			resultlst.add(columnlst);
			resultlst.add(finalDatalst);
			resultlst.add(columnFormatlst);
			exceptionCode.setResponse(resultlst);
			exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
			exceptionCode.setErrorMsg("successful operation");
			return exceptionCode;
		}catch(Exception ex){
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg(ex.getMessage());
		}finally {
			if(stmt != null)
				stmt.close();
			if(conExt != null)
				conExt.close();
		}
		return exceptionCode;
	}
	public ExceptionCode getChartReportData(DashboardTilesVb vObject) throws SQLException{
		ExceptionCode exceptionCode =new ExceptionCode();
		setServiceDefaults();
		Statement stmt = null;
		List collTemp = new ArrayList();
		HashMap chartDataMap = new HashMap();
		ResultSet rs = null;
		Connection conExt = null;
		List<PrdQueryConfig> sqlQueryList = new ArrayList<PrdQueryConfig>();
		PrdQueryConfig prdQueryConfig = new PrdQueryConfig();
		try
		{	
			sqlQueryList = reportsDao.getSqlQuery(vObject.getQueryId());
			if (sqlQueryList != null && sqlQueryList.size() > 0) {
				prdQueryConfig = sqlQueryList.get(0);
			} else {
				exceptionCode.setOtherInfo(vObject);
				exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
				exceptionCode.setErrorMsg("Query not maintained for the Data Ref Id[" + vObject.getQueryId() + "]");
				return exceptionCode;
			}
			String orginalQuery = prdQueryConfig.getQueryProc();
			orginalQuery = replacePromptVariables(orginalQuery, vObject);
			
			ExceptionCode exConnection = commonDao.getReqdConnection(conExt,prdQueryConfig.getDbConnectionName());
			if(exConnection.getErrorCode() != Constants.ERRONEOUS_OPERATION && exConnection.getResponse() != null) {
				conExt = (Connection)exConnection.getResponse();
			}else {
				exceptionCode.setErrorCode(exConnection.getErrorCode());
				exceptionCode.setErrorMsg(exConnection.getErrorMsg());
				return exceptionCode;
			}
			
			stmt = conExt.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
			if(ValidationUtil.isValid(orginalQuery)) {
				rs = stmt.executeQuery(orginalQuery);
			}else {
				return null;
			}
			CachedRowSet rsChild = RowSetProvider.newFactory().createCachedRowSet();
	    	rsChild.populate(rs);
	    	//String returnXml = chartUtils.getChartXML(vObject.getChartType(), vObject.getPropertyAttr(),rs ,rsChild);
			exceptionCode = chartUtils.getChartXML(vObject.getChartType(), vObject.getPropertyAttr(), rs, rsChild,
					null);
			if(exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION) {
				exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
				exceptionCode.setErrorMsg(exceptionCode.getErrorMsg());
				return exceptionCode;
			}
			exceptionCode.setResponse(exceptionCode.getResponse());
			exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
			return exceptionCode;
			}catch(Exception ex){
			ex.printStackTrace();
			logger.error("Exception while getting Dashboard Data...!!");
			return null;
		}finally{
			if(stmt != null)
				stmt.close();
			if(conExt != null)
				conExt.close();
			if(rs != null)
				rs.close();
		}
	}
	/*public String getChartXmlFormObjProperties(String chartType){
		String sql = "select HTML_TAG_PROPERTY from PRD_CHART_PROPERTIES where "
				+ "VRD_OBJECT_ID='Col3D' AND "
				+ "UPPER(OBJ_TAG_ID)=UPPER('"+chartType+"')";
		return getJdbcTemplate().queryForObject(sql, String.class);
	}*/
	public String getReportQuery(String reportId){
		setServiceDefaults();
		String strQueryAppr = new String("Select QUERY from PRD_QUERY_CONFIG where Report_ID = '"+reportId+"' ");
		try
		{
			String i = getJdbcTemplate().queryForObject(strQueryAppr,null, String.class);
			return i;
		}catch(Exception ex){
			return "";
		}
	}
	
	
	
	
	public LinkedHashMap<String,String> getDashboardFilterValue(String sourceQuery){
		ResultSetExtractor mapper = new ResultSetExtractor() {
			@Override
			public Object extractData(ResultSet rs)  throws SQLException, DataAccessException {
				ResultSetMetaData metaData = rs.getMetaData();
				LinkedHashMap<String,String> filterMap = new LinkedHashMap<String,String>();
				while(rs.next()){
					filterMap.put(rs.getString(1),rs.getString(2));
				}
				return filterMap;
			}
		};
		return (LinkedHashMap<String,String>)getJdbcTemplate().query(sourceQuery, mapper);
	}
	public String getDashboardDefaultValue(String sourceQuery){
		ResultSetExtractor mapper = new ResultSetExtractor() {
			String defaultValue = "";
			@Override
			public Object extractData(ResultSet rs)  throws SQLException, DataAccessException {
				ResultSetMetaData metaData = rs.getMetaData();
				while(rs.next()){
					defaultValue = rs.getString(1);
				}
				return defaultValue;
			}
		};
		return (String)getJdbcTemplate().query(sourceQuery, mapper);
	}
	public List<DashboardFilterVb> getDashboardFilterDetail(String filterRefCode){
		setServiceDefaults();
		List<DashboardFilterVb> collTemp = null;
		try
		{			
			String query = " Select Filter_Ref_code,filter_Xml,FILTER_COUNT from PRD_DASHBOARDS_FILTER where Status = 0 and Filter_Ref_Code = '"+filterRefCode+"' ";
			collTemp = getJdbcTemplate().query(query,getDashboardFilterMapper());
			return collTemp;
		}catch(Exception ex){
			ex.printStackTrace();
			logger.error("Exception while getting Dashboard Detail...!!");
			return null;
		}
	}
	private RowMapper getDashboardFilterMapper(){
		RowMapper mapper = new RowMapper(){
			@Override
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				DashboardFilterVb vObject = new DashboardFilterVb();
				vObject.setFilterRefCode(rs.getString("Filter_Ref_code"));
				vObject.setFilterRefXml(rs.getString("filter_Xml"));
				vObject.setFilterCnt(rs.getInt("FILTER_COUNT"));
				return vObject;
			}
		};
		return mapper;
	}
	
	public String replacePromptVariables(String reportQuery,DashboardTilesVb promptsVb) {
		reportQuery = reportQuery.replaceAll("#PROMPT_VALUE_1#",ValidationUtil.isValid(promptsVb.getPromptValue1()) ? promptsVb.getPromptValue1() : "''");
		reportQuery = reportQuery.replaceAll("#PROMPT_VALUE_2#",ValidationUtil.isValid(promptsVb.getPromptValue2()) ? promptsVb.getPromptValue2() : "''");
		reportQuery = reportQuery.replaceAll("#PROMPT_VALUE_3#",ValidationUtil.isValid(promptsVb.getPromptValue3()) ? promptsVb.getPromptValue3() : "''");
		reportQuery = reportQuery.replaceAll("#PROMPT_VALUE_4#",ValidationUtil.isValid(promptsVb.getPromptValue4()) ? promptsVb.getPromptValue4() : "''");
		reportQuery = reportQuery.replaceAll("#PROMPT_VALUE_5#",ValidationUtil.isValid(promptsVb.getPromptValue5()) ? promptsVb.getPromptValue5() : "''");
		reportQuery = reportQuery.replaceAll("#PROMPT_VALUE_6#",ValidationUtil.isValid(promptsVb.getPromptValue6()) ? promptsVb.getPromptValue6() : "''");
		reportQuery = reportQuery.replaceAll("#PROMPT_VALUE_7#",ValidationUtil.isValid(promptsVb.getPromptValue7()) ? promptsVb.getPromptValue7() : "''");
		reportQuery = reportQuery.replaceAll("#PROMPT_VALUE_8#",ValidationUtil.isValid(promptsVb.getPromptValue8()) ? promptsVb.getPromptValue8() : "''");
		reportQuery = reportQuery.replaceAll("#PROMPT_VALUE_9#",ValidationUtil.isValid(promptsVb.getPromptValue9()) ? promptsVb.getPromptValue9() : "''");
		reportQuery = reportQuery.replaceAll("#PROMPT_VALUE_10#",ValidationUtil.isValid(promptsVb.getPromptValue10()) ? promptsVb.getPromptValue10() : "''");
		reportQuery = reportQuery.replaceAll("#VISION_ID#", ""+CustomContextHolder.getContext().getVisionId());
		/*if(promptsVb.getReportId().contains("MPR") && ValidationUtil.isValid(promptsVb.getPromptValue2())) {
			reportQuery = reportQuery.replaceAll("#PYM#", getDateFormat(promptsVb.getPromptValue2(),"PYM"));
			reportQuery = reportQuery.replaceAll("#NYM#", getDateFormat(promptsVb.getPromptValue2(),"NYM"));
			reportQuery = reportQuery.replaceAll("#PM#", getDateFormat(promptsVb.getPromptValue2(),"PM"));
			reportQuery = reportQuery.replaceAll("#NM#", getDateFormat(promptsVb.getPromptValue2(),"NM"));
			reportQuery = reportQuery.replaceAll("#CM#", getDateFormat(promptsVb.getPromptValue2(),"CM"));
			reportQuery = reportQuery.replaceAll("#CY#", getDateFormat(promptsVb.getPromptValue2(),"CY"));
			reportQuery = reportQuery.replaceAll("#PY#", getDateFormat(promptsVb.getPromptValue2(),"PY"));
		}*/
		return reportQuery;
	}
	
	public List<DashboardTilesVb> getDashboardSubTileDetails(DashboardTilesVb dObj){
		setServiceDefaults();
		List<DashboardTilesVb> collTemp = null;
		try
		{			
			String query = "Select Dashboard_ID,Tab_ID,PARENT_SEQUENCE,SUB_SEQUENCE,SUB_TILE_ID,Tile_Caption,Query_ID,TILE_TYPE,DRILL_DOWN_FLAG,CHART_TYPE,TILE_PROPERTY_XML,PLACE_HOLDER_COUNT,DOUBLE_WIDTH_FLAG,Theme_AT,Theme from PRD_DASHBOARD_SUB_TILES where Dashboard_ID = '"+dObj.getDashboardId()+"' and Tab_ID = '"+dObj.getTabId()+"' and PARENT_SEQUENCE = '"+dObj.getTileSequence() +"' Order by SUB_SEQUENCE " ;
			collTemp = getJdbcTemplate().query(query,getDashboardSubTileMapper());
			return collTemp;
		}catch(Exception ex){
			ex.printStackTrace();
			logger.error("Exception while getDashboardSubTileDetails...!!");
			return null;
		}
	}
	private RowMapper getDashboardSubTileMapper(){
		RowMapper mapper = new RowMapper(){
			@Override
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				DashboardTilesVb vObject = new DashboardTilesVb();
				vObject.setDashboardId(rs.getString("Dashboard_ID"));
				vObject.setTabId(rs.getString("Tab_ID"));
				vObject.setTileSequence(rs.getInt("PARENT_SEQUENCE"));
				vObject.setSubSequence(rs.getString("SUB_SEQUENCE"));
				vObject.setSubTileId(rs.getString("SUB_TILE_ID"));
				vObject.setTileCaption(rs.getString("Tile_Caption"));
				vObject.setQueryId(rs.getString("Query_ID"));
				vObject.setTileType(rs.getString("TILE_TYPE"));
				vObject.setPropertyAttr(rs.getString("TILE_PROPERTY_XML"));
				vObject.setPlaceHolderCnt(rs.getInt("PLACE_HOLDER_COUNT"));
				vObject.setChartType(rs.getString("CHART_TYPE"));
				vObject.setDoubleWidthFlag(rs.getString("DOUBLE_WIDTH_FLAG"));
				if("Y".equalsIgnoreCase(rs.getString("DRILL_DOWN_FLAG"))) {
					vObject.setDrillDownFlag(true);
				}else {
					vObject.setDrillDownFlag(false);
				}
				vObject.setThemeAT(rs.getInt("Theme_AT"));
				vObject.setTheme(rs.getString("Theme"));
				return vObject;
			}
		};
		return mapper;
	}
	//V1.02 Version New Features
	public List<AlphaSubTabVb> getDashboadList(String dashboardGroup,String applicationId) throws DataAccessException {
		VisionUsersVb visionUsersVb = CustomContextHolder.getContext();
		String sql = " Select t2.DASHBOARD_ID,t2.DASHBOARD_NAME from PRD_DASHBOARD_ACCESS t1,PRD_DASHBOARDS t2 "+ 
				" where t1.Dashboard_ID  = t2.DASHBOARD_ID and T1.USER_GROUP||'-'||T1.USER_PROFILE = ? "+
				" AND T2.APPLICATION_ID  = ? AND  T2.Dashboard_Group = ? AND T1.PRODUCT_NAME = T2.APPLICATION_ID ";
		Object[] lParams = new Object[3];
		lParams[0] =  visionUsersVb.getUserGrpProfile();
		lParams[1] = applicationId;
		lParams[2] = dashboardGroup;
		return  getJdbcTemplate().query(sql, lParams, getDashboardMapper());
	}
	protected RowMapper getDashboardMapper(){
		RowMapper mapper = new RowMapper() {
			@Override
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				AlphaSubTabVb alphaSubTabVb = new AlphaSubTabVb();
				alphaSubTabVb.setAlphaSubTab(rs.getString("Dashboard_ID"));
				alphaSubTabVb.setDescription(rs.getString("Dashboard_Name"));
				return alphaSubTabVb;
			}
		};
		return mapper;
	}
	/*public LinkedHashMap<String,String> getDashboardFilterTree(String sourceQuery){
		sourceQuery= "Select Bank_Group,Divison,Parent_SBU,Vision_SBU from Sbu_Temp order by Bank_Group,Divison,Parent_SBU,Vision_SBU";
		try {
			ResultSetExtractor mapper = new ResultSetExtractor() {
				public Object extractData(ResultSet rs)  throws SQLException, DataAccessException {
					ResultSetMetaData metaData = rs.getMetaData();
					LinkedHashMap<String,String> filterMap = new LinkedHashMap<String,String>();

					DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
		            DocumentBuilder documentBuilder;
					try {
						documentBuilder = documentFactory.newDocumentBuilder();
						Document document = documentBuilder.newDocument();
						Element root = document.createElement("TREE");
			            document.appendChild(root);
						while(rs.next()){
							Element employee = document.createElement(rs.getString(1));
							if(root.getElementsByTagName(rs.getString(1)) == null)
								root.appendChild(employee);
						}
						TransformerFactory transformerFactory = TransformerFactory.newInstance();
			            Transformer transformer = transformerFactory.newTransformer();
			            DOMSource domSource = new DOMSource(document);
			            StringWriter writer = new StringWriter();
			            StreamResult result = new StreamResult(writer);
			            transformer.transform(domSource, result);
			            //System.out.println("XML IN String format is: \n" + writer.toString());
			            
					} catch (ParserConfigurationException e) {
					} catch (TransformerConfigurationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (TransformerException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					return filterMap;
				}
			};
			return (LinkedHashMap<String,String>)getJdbcTemplate().query(sourceQuery, mapper);
		}catch (Exception e) {
			return new LinkedHashMap<>();
        }
		
	}*/
	public List<AlphaSubTabVb> findDashboardApplicationCategory() throws DataAccessException {
		VisionUsersVb visionUsersVb = CustomContextHolder.getContext();
		String sql = " SELECT DISTINCT APPLICATION_ID ALPHA_SUB_TAB,(SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE "+
				" ALPHA_TAB = 8000 AND ALPHA_SUB_TAB = APPLICATION_ID) ALPHA_SUBTAB_DESCRIPTION "+
				" FROM PRD_DASHBOARDS where APPLICATION_ID IN ("+visionUsersVb.getApplicationAccess()+") ORDER BY ALPHA_SUBTAB_DESCRIPTION ";
		return  getJdbcTemplate().query(sql, getCategoryMapper());
	}
	public List<AlphaSubTabVb> findDashboardCategory(String productId) throws DataAccessException {
		String sql = "SELECT DISTINCT DASHBOARD_GROUP ALPHA_SUB_TAB,(SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE "+
					" ALPHA_TAB = 6016 AND ALPHA_SUB_TAB = DASHBOARD_GROUP) ALPHA_SUBTAB_DESCRIPTION "+
					" FROM PRD_DASHBOARDS WHERE APPLICATION_ID = ? ORDER BY ALPHA_SUB_TAB " ; 
		Object[] lParams = new Object[1];
		lParams[0] = productId;
		return  getJdbcTemplate().query(sql, lParams, getCategoryMapper());
	}
	private RowMapper getCategoryMapper(){
		RowMapper mapper = new RowMapper() {
			@Override
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				AlphaSubTabVb vObject = new AlphaSubTabVb();
				vObject.setAlphaSubTab(rs.getString("ALPHA_SUB_TAB"));
				vObject.setDescription(rs.getString("ALPHA_SUBTAB_DESCRIPTION"));
				return vObject;
			}
		};
		return mapper;
	}
}