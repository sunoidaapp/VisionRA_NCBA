package com.vision.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.UncategorizedSQLException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.vision.authentication.CustomContextHolder;
import com.vision.exception.ExceptionCode;
import com.vision.exception.RuntimeCustomException;
import com.vision.util.CommonUtils;
import com.vision.util.Constants;
import com.vision.util.ValidationUtil;
import com.vision.vb.BusinessLineHeaderVb;
import com.vision.vb.FeesConfigDetailsVb;
import com.vision.vb.FeesConfigHeaderVb;
import com.vision.vb.FeesConfigTierVb;
import com.vision.vb.SmartSearchVb;
import com.vision.vb.TransLineHeaderVb;

@Component
public class FeesConfigHeadersDao extends AbstractDao<FeesConfigHeaderVb> {
	@Autowired
	CommonDao commonDao;
	@Autowired
	FeesConfigDetailsDao feesConfigDetailsDao;
	
	@Autowired
	FeesConfigTierDao feesConfigTierDao;
	@Value("${app.databaseType}")
	private String databaseType;
	
	String feeBasisDescAppr = "(SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB= 7032 AND ALPHA_SUB_TAB = TAPPR.FEE_BASIS) FEE_BASIS_DESC, ";
	String feeBasisDescPend = "(SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB= 7032 AND ALPHA_SUB_TAB = TPEND.FEE_BASIS) FEE_BASIS_DESC, ";
	
	@Override
	protected RowMapper getMapper(){
		RowMapper mapper = new RowMapper() {
			@Override
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				FeesConfigHeaderVb vObject = new FeesConfigHeaderVb();
				vObject.setCountry(rs.getString("COUNTRY"));
				vObject.setLeBook(rs.getString("LE_BOOK"));
				vObject.setBusinessLineId(rs.getString("BUSINESS_LINE_ID"));
				vObject.setBusinessLineIdDesc(rs.getString("BUSINESS_LINE_DESCRIPTION"));
				vObject.setEffectiveDate(rs.getString("EFFECTIVE_DATE"));
				vObject.setFeeBasis(rs.getString("FEE_BASIS"));
				vObject.setFeeBasisDesc(rs.getString("FEE_BASIS_DESC"));
				vObject.setFeeTypeDesc(rs.getString("FEE_TYPE_DESC"));
				vObject.setTierType(rs.getString("Tier_Type"));
				vObject.setTierTypeDesc(rs.getString("TIER_TYPE_DESC"));
				vObject.setFeeType(rs.getString("Fee_Type"));
				vObject.setFeesLineStatus (rs.getInt("FEE_LINE_STATUS"));
				vObject.setFeesLineStatusDesc(rs.getString("FEE_LINE_STATUS_Desc"));
				vObject.setRecordIndicator(rs.getInt("RECORD_INDICATOR"));
				vObject.setRecordIndicatorDesc(rs.getString("RECORD_INDICATOR_Desc"));
				vObject.setMaker(rs.getInt("MAKER"));
				vObject.setMakerName(rs.getString("MAKER_NAME"));
				vObject.setVerifier(rs.getInt("VERIFIER"));
				vObject.setVerifierName(rs.getString("VERIFIER_NAME"));
				vObject.setDateLastModified(rs.getString("DATE_LAST_MODIFIED"));
				vObject.setDateCreation(rs.getString("DATE_CREATION"));
				if("NA".equalsIgnoreCase(vObject.getTierType()))
					vObject.setTierTypeDesc("Not Applicable");
				return vObject;
			}
		};
		return mapper;
	}
	protected RowMapper getDetailMapper(){
		RowMapper mapper = new RowMapper() {
			@Override
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				FeesConfigHeaderVb vObject = new FeesConfigHeaderVb();
				vObject.setCountry(rs.getString("COUNTRY"));
				vObject.setLeBook(rs.getString("LE_BOOK"));
				vObject.setBusinessLineId(rs.getString("BUSINESS_LINE_ID"));
				vObject.setBusinessLineIdDesc(rs.getString("BUSINESS_LINE_DESCRIPTION"));
				vObject.setEffectiveDate(rs.getString("EFFECTIVE_DATE"));
				vObject.setFeeBasis(rs.getString("FEE_BASIS"));
				vObject.setFeeBasisDesc(rs.getString("FEE_BASIS_DESC"));
				vObject.setFeeType(rs.getString("FEE_TYPE"));
				vObject.setTierType(rs.getString("TIER_TYPE"));
				vObject.setFeeTypeDesc(rs.getString("FEE_TYPE_DESC"));
				vObject.setTierTypeDesc(rs.getString("TIER_TYPE_DESC"));
				vObject.setFeesLineStatus(rs.getInt("FEE_LINE_STATUS"));
				vObject.setFeesLineStatusDesc(rs.getString("FEE_LINE_STATUS_DESC"));
				vObject.setRecordIndicator(rs.getInt("RECORD_INDICATOR"));
				vObject.setRecordIndicatorDesc(rs.getString("RECORD_INDICATOR_Desc"));
				vObject.setMaker(rs.getInt("MAKER"));
				vObject.setMakerName(rs.getString("MAKER_NAME"));
				vObject.setVerifier(rs.getInt("VERIFIER"));
				vObject.setVerifierName(rs.getString("VERIFIER_NAME"));
				vObject.setDateLastModified(rs.getString("DATE_LAST_MODIFIED"));
				vObject.setDateCreation(rs.getString("DATE_CREATION"));
				return vObject;
			}
		};
		return mapper;
	}
	@Override
	public List<FeesConfigHeaderVb> getQueryPopupResults(FeesConfigHeaderVb dObj) {
		Vector<Object> params = new Vector<Object>();
		StringBuffer strBufApprove = null;
		StringBuffer strBufPending = null;
		String strWhereNotExists = null;
		String orderBy = "";
		String effectiveDateAppr = "";
		String effectiveDatePend = "";
		if ("ORACLE".equalsIgnoreCase(databaseType)) {
			effectiveDateAppr = "TO_CHAR(TAPPR.EFFECTIVE_DATE,'DD-Mon-RRRR HH24:MI:SS')";
			effectiveDatePend = "TO_CHAR(TPend.EFFECTIVE_DATE,'DD-Mon-RRRR HH24:MI:SS')";
		}else if ("MSSQL".equalsIgnoreCase(databaseType)) {
			effectiveDateAppr = "format(CAST(TAppr.EFFECTIVE_DATE AS DATETIME), 'dd-MMM-yyyy HH:mm:ss')";
			effectiveDatePend = "format(CAST(TPend.EFFECTIVE_DATE AS DATETIME), 'dd-MMM-yyyy HH:mm:ss')";
		}
			strBufApprove = new StringBuffer(
					"Select * from ( SELECT TAppr.COUNTRY, TAppr.LE_BOOK,       "
						+ "  TAppr.BUSINESS_LINE_ID,(select T1.ALPHA_SUBTAB_DESCRIPTION from ALPHA_SUB_TAB T1 where t1.Alpha_tab = TAPPR.FEE_BASIS_AT and T1.ALPHA_SUB_TAB=TAPPR.FEE_BASIS) FEE_BASIS_DESC,"
						+ "(SELECT T6.BUSINESS_LINE_DESCRIPTION FROM RA_MST_BUSINESS_LINE_HEADER T6 WHERE T6.COUNTRY = TAPPR.COUNTRY AND "
						+ "T6.LE_BOOK = TAPPR.LE_BOOK AND T6.BUSINESS_LINE_ID = TAppr.BUSINESS_LINE_ID) BUSINESS_LINE_DESCRIPTION, "
					    + " TAPPR.FEE_BASIS,  " 
						+ " TAPPR.FEE_TYPE,(SELECT T1.ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB T1 where t1.Alpha_tab =  7033 and T1.ALPHA_SUB_TAB=TAPPR.FEE_TYPE )FEE_TYPE_DESC, "
						+ "TAPPR.TIER_TYPE,CASE WHEN TIER_TYPE = 'NA' then 'Not applicable' "
						+ " ELSE "
						+ " (SELECT T1.ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB T1 where t1.Alpha_tab =  7034 and T1.ALPHA_SUB_TAB=TAPPR.TIER_TYPE )"
						+ " END TIER_TYPE_DESC,"
					    + "	" + effectiveDateAppr
						+ " EFFECTIVE_DATE, TAppr.FEE_LINE_STATUS,T3.NUM_SUBTAB_DESCRIPTION FEE_LINE_STATUS_DESC, "
						+ " TAppr.RECORD_INDICATOR,T4.NUM_SUBTAB_DESCRIPTION RECORD_INDICATOR_DESC,"
						+ " TAppr. MAKER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "
						+ getDbFunction("NVL") + "(TAppr.MAKER,0) ) MAKER_NAME, "
						+ " TAppr.VERIFIER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "
						+ getDbFunction("NVL") + "(TAppr.VERIFIER,0) ) VERIFIER_NAME,  "
						+ " " + getDbFunction("DATEFUNC") + "(TAppr.DATE_LAST_MODIFIED, '"+getDbFunction("DD_Mon_RRRR")+" "
						+ getDbFunction("TIME") + "') DATE_LAST_MODIFIED ,"
						+ getDbFunction("DATEFUNC") + "(TAppr.DATE_CREATION, '"+getDbFunction("DD_Mon_RRRR")+" "
						+ getDbFunction("TIME") + "') DATE_CREATION "
						+ " FROM RA_MST_FEES_HEADER TAppr ,NUM_SUB_TAB T3,NUM_SUB_TAB T4     " + " Where  "
						+ "	 t3.NUM_tab = TAppr.FEE_LINE_STATUS_NT  " + " and t3.NUM_sub_tab = TAppr.FEE_LINE_STATUS  "
						+ " and t4.NUM_tab = TAppr.RECORD_INDICATOR_NT  "
						+ " and t4.NUM_sub_tab = TAppr.RECORD_INDICATOR and TAppr.COUNTRY= ?" 
						+ " and TAppr.LE_BOOK= ? and TAppr.BUSINESS_LINE_ID= ?) TAppr");
		
		if(databaseType.equalsIgnoreCase("ORACLE")) {
			strWhereNotExists = new String(" Not Exists (Select 'X' From RA_MST_FEES_HEADER_PEND TPEND WHERE TAppr.COUNTRY = TPend.COUNTRY"
					+ " AND TAppr.LE_BOOK = TPend.LE_BOOK  AND TAPPR.BUSINESS_LINE_ID = TPEND.BUSINESS_LINE_ID"
					+ " AND TO_DATE(TAPPR.EFFECTIVE_DATE,'DD-Mon-RRRR HH24:MI:SS') = TPEND.EFFECTIVE_DATE)");
		}else {
			strWhereNotExists = new String(" Not Exists (Select 'X' From RA_MST_FEES_HEADER_PEND TPEND WHERE TAppr.COUNTRY = TPend.COUNTRY"
					+ " AND TAppr.LE_BOOK = TPend.LE_BOOK  AND TAPPR.BUSINESS_LINE_ID = TPEND.BUSINESS_LINE_ID"
					+ " AND TAPPR.EFFECTIVE_DATE = TPEND.EFFECTIVE_DATE)");	
		}
		strBufPending = new StringBuffer(
				"Select * from ( SELECT TPend.COUNTRY, TPend.LE_BOOK,       "
						+ "  TPend.BUSINESS_LINE_ID,(select T1.ALPHA_SUBTAB_DESCRIPTION from ALPHA_SUB_TAB T1 where t1.Alpha_tab = TPend.FEE_BASIS_AT and T1.ALPHA_SUB_TAB=TPend.FEE_BASIS) FEE_BASIS_DESC,"
						+ "(SELECT T6.BUSINESS_LINE_DESCRIPTION FROM RA_MST_BUSINESS_LINE_HEADER_PEND T6 WHERE T6.COUNTRY = TPend.COUNTRY AND "
						+ "T6.LE_BOOK = TPend.LE_BOOK AND T6.BUSINESS_LINE_ID = TPend.BUSINESS_LINE_ID) BUSINESS_LINE_DESCRIPTION, "
					    + " TPend.FEE_BASIS,  " 
						+ " TPend.FEE_TYPE,(SELECT T1.ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB T1 where t1.Alpha_tab =  7033 and T1.ALPHA_SUB_TAB=TPend.FEE_TYPE )FEE_TYPE_DESC, "
						+ " TPend.TIER_TYPE, CASE WHEN TIER_TYPE = 'NA' then 'Not applicable' "
						+ " ELSE "
						+ " (SELECT T1.ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB T1 where t1.Alpha_tab =  7034 and T1.ALPHA_SUB_TAB=TPend.TIER_TYPE )"
						+ " END TIER_TYPE_DESC,"
					    + "	" + effectiveDatePend
						+ " EFFECTIVE_DATE, TPend.FEE_LINE_STATUS,T3.NUM_SUBTAB_DESCRIPTION FEE_LINE_STATUS_DESC, "
						+ " TPend.RECORD_INDICATOR,T4.NUM_SUBTAB_DESCRIPTION RECORD_INDICATOR_DESC,"
						+ " TPend. MAKER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "
						+ getDbFunction("NVL") + "(TPend.MAKER,0) ) MAKER_NAME, "
						+ " TPend.VERIFIER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "
						+ getDbFunction("NVL") + "(TPend.VERIFIER,0) ) VERIFIER_NAME,  "
						+ " " + getDbFunction("DATEFUNC") + "(TPend.DATE_LAST_MODIFIED, '"+getDbFunction("DD_Mon_RRRR")+" "
						+ getDbFunction("TIME") + "') DATE_LAST_MODIFIED ,"
						+ getDbFunction("DATEFUNC") + "(TPend.DATE_CREATION, '"+getDbFunction("DD_Mon_RRRR")+" "
						+ getDbFunction("TIME") + "') DATE_CREATION "
						+ " FROM RA_MST_FEES_HEADER_pend TPend ,NUM_SUB_TAB T3,NUM_SUB_TAB T4     " + " Where  "
						+ "	 t3.NUM_tab = TPend.FEE_LINE_STATUS_NT  " + " and t3.NUM_sub_tab = TPend.FEE_LINE_STATUS  "
						+ " and t4.NUM_tab = TPend.RECORD_INDICATOR_NT  "
						+ " and t4.NUM_sub_tab = TPend.RECORD_INDICATOR and TPend.COUNTRY= ?" 
						+ " and TPend.LE_BOOK= ? and TPend.BUSINESS_LINE_ID= ?) TPend");
		
		       params.addElement(dObj.getCountry());
		       params.addElement(dObj.getLeBook());
		       params.addElement(dObj.getBusinessLineId());
		       
		try
		{
			if (dObj.getSmartSearchOpt() != null && !dObj.getSmartSearchOpt().isEmpty()) {
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
					case "country":
						CommonUtils.addToQuerySearch(" upper(TAPPR.COUNTRY) "+ val, strBufApprove, data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.COUNTRY) "+ val, strBufPending, data.getJoinType());
						break;
						
					case "leBook":
						CommonUtils.addToQuerySearch(" upper(TAPPR.LE_BOOK) "+ val, strBufApprove, data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.LE_BOOK) "+ val, strBufPending, data.getJoinType());
						break;
						

					case "businessLineIdDesc":
						CommonUtils.addToQuerySearch(" upper(TAPPR.BUSINESS_LINE_DESCRIPTION) "+ val, strBufApprove, data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.BUSINESS_LINE_DESCRIPTION) "+ val, strBufPending, data.getJoinType());
						break;
						
					case "feeBasisDesc":
						CommonUtils.addToQuerySearch(" upper(TAPPR.FEE_BASIS_DESC) "+ val, strBufApprove, data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.FEE_BASIS_DESC) "+ val, strBufPending, data.getJoinType());
						break;
						
					case "tierTypeDesc":
						CommonUtils.addToQuerySearch(" upper(TAPPR.TIER_TYPE_DESC) "+ val, strBufApprove, data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.TIER_TYPE_DESC) "+ val, strBufPending, data.getJoinType());
						break;
						
					case "feeTypeDesc":
						CommonUtils.addToQuerySearch(" upper(TAPPR.FEE_TYPE_DESC) "+ val, strBufApprove, data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.FEE_TYPE_DESC) "+ val, strBufPending, data.getJoinType());
						break;	
						
/*						
					case "feeConfigTypeDesc":
						CommonUtils.addToQuerySearch(" upper(TAPPR.FEE_CONF_TYPE_DESC) "+ val, strBufApprove, data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.FEE_CONF_TYPE_DESC) "+ val, strBufPending, data.getJoinType());
						break;
						
					case "feeConfigCodeDesc":
						CommonUtils.addToQuerySearch(" upper(TAPPR.FEE_CONFIG_CODE_DESC) "+ val, strBufApprove, data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.FEE_CONFIG_CODE_DESC) "+ val, strBufPending, data.getJoinType());
						break;*/
						
					case "effectiveDate":
						CommonUtils.addToQuerySearch(" "+getDbFunction("DATEFUNC")+"(TAPPR.EFFECTIVE_DATE '"+getDbFunction("DD_Mon_RRRR")+" "
									+ getDbFunction("TIME") + "') "+ val, strBufApprove, data.getJoinType());
						CommonUtils.addToQuerySearch(" "+getDbFunction("DATEFUNC")+"(TPend.EFFECTIVE_DATE '"+getDbFunction("DD_Mon_RRRR")+" "
									+ getDbFunction("TIME") + "') "+ val, strBufPending, data.getJoinType());
						break;
						
						
					case "feeConfigStatusDesc":
						CommonUtils.addToQuerySearch(" upper(TAPPR.FEE_LINE_STATUS_DESC) "+ val, strBufApprove, data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.FEE_LINE_STATUS_DESC) "+ val, strBufPending, data.getJoinType());
						break;
						
					case "feeConfigRecordIndicatorDesc":
						CommonUtils.addToQuerySearch(" upper(TAPPR.RECORD_INDICATOR_DESC) "+ val, strBufApprove, data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.RECORD_INDICATOR_DESC) "+ val, strBufPending, data.getJoinType());
						break;
						
					case "dateCreation":
						CommonUtils.addToQuerySearch(" "+getDbFunction("DATEFUNC")+"(TAPPR.DATE_CREATION,'DD-MM-YYYY "+getDbFunction("TIME")+"') " + val, strBufApprove, data.getJoinType());
						CommonUtils.addToQuerySearch(" "+getDbFunction("DATEFUNC")+"(TPend.DATE_CREATION,'DD-MM-YYYY "+getDbFunction("TIME")+"')  " + val, strBufPending, data.getJoinType());
						break;
									
					case "dateLastModified":
						CommonUtils.addToQuerySearch(" "+getDbFunction("DATEFUNC")+"(TAPPR.DATE_LAST_MODIFIED,'DD-MM-YYYY "+getDbFunction("TIME")+"') "  + val, strBufApprove, data.getJoinType());
						CommonUtils.addToQuerySearch(" "+getDbFunction("DATEFUNC")+"(TPend.DATE_LAST_MODIFIED,'DD-MM-YYYY "+getDbFunction("TIME")+"') "  + val, strBufPending, data.getJoinType());
						break;

					case "makerName":
						CommonUtils.addToQuerySearch(" (TAPPR.MAKER_NAME) IN ("+ val+") ", strBufApprove, data.getJoinType());
						CommonUtils.addToQuerySearch(" (TPend.MAKER_NAME) IN ("+ val+") ", strBufPending, data.getJoinType());
						break;

					default:
					}
					count++;
				}
			}
		/*	VisionUsersVb visionUsersVb = CustomContextHolder.getContext();
			if(("Y".equalsIgnoreCase(visionUsersVb.getUpdateRestriction()))){
				if(ValidationUtil.isValid(visionUsersVb.getCountry())){
					CommonUtils.addToQuery(" COUNTRY IN ('"+visionUsersVb.getCountry()+"') ", strBufApprove);
					CommonUtils.addToQuery(" COUNTRY IN ('"+visionUsersVb.getCountry()+"') ", strBufPending);
				}
				if(ValidationUtil.isValid(visionUsersVb.getLeBook())){
					CommonUtils.addToQuery(" LE_BOOK IN ('"+visionUsersVb.getLeBook()+"') ", strBufApprove);
					CommonUtils.addToQuery(" LE_BOOK IN ('"+visionUsersVb.getLeBook()+"') ", strBufPending);
				}
			}*/
			orderBy=" Order by EFFECTIVE_DATE DESC,BUSINESS_LINE_DESCRIPTION ASC ";
			//orderBy=" Order by DATE_LAST_MODIFIED_1 DESC ";
			return getQueryPopupResults(dObj,strBufPending, strBufApprove, strWhereNotExists, orderBy, params);
		}catch(Exception ex){
			ex.printStackTrace();
			//logger.error(((strBufApprove==null)? "strBufApprove is Null":strBufApprove.toString()));
			//logger.error("UNION");
			//logger.error(((strBufPending==null)? "strBufPending is Null":strBufPending.toString()));

			/*if (params != null)
				for(int i=0 ; i< params.size(); i++)
					//logger.error("objParams[" + i + "]" + params.get(i).toString());*/
			return null;
			}
	}
	@Override
	public List<FeesConfigHeaderVb> getQueryResults(FeesConfigHeaderVb dObj, int intStatus){
		List<FeesConfigHeaderVb> collTemp = null;
		setServiceDefaults();
		String strQueryAppr = null;
		String strQueryPend = null;
		String effectiveDateAppr ="";
		String effectiveDatePend ="";
		if(ValidationUtil.isValid(dObj.getEffectiveDate())) {
			if ("ORACLE".equalsIgnoreCase(databaseType)) {
				effectiveDateAppr = "TAPPR.EFFECTIVE_DATE = TO_DATE('"+dObj.getEffectiveDate()+"','DD-Mon-RRRR HH24:MI:SS') AND";
				effectiveDatePend = "TPend.EFFECTIVE_DATE = TO_DATE('"+dObj.getEffectiveDate()+"','DD-Mon-RRRR HH24:MI:SS') AND";
			}else if ("MSSQL".equalsIgnoreCase(databaseType)) {
				effectiveDateAppr = "TAPPR.EFFECTIVE_DATE = '"+dObj.getEffectiveDate()+"' AND";
				effectiveDatePend = "TPend.EFFECTIVE_DATE = '"+dObj.getEffectiveDate()+"' AND";
			}
		}
		if ("ORACLE".equalsIgnoreCase(databaseType)) {
			strQueryAppr = new String(" SELECT TAppr.COUNTRY, TAppr.LE_BOOK,TAppr.BUSINESS_LINE_ID,"
					+ "(select T1.ALPHA_SUBTAB_DESCRIPTION from ALPHA_SUB_TAB T1 where t1.Alpha_tab = TAPPR.FEE_BASIS_AT and T1.ALPHA_SUB_TAB=TAPPR.FEE_BASIS) FEE_BASIS_DESC,"+
					" (SELECT T6.BUSINESS_LINE_DESCRIPTION FROM RA_MST_BUSINESS_LINE_HEADER T6 WHERE T6.COUNTRY = TAppr.COUNTRY AND "+
					" T6.LE_BOOK = TAppr.LE_BOOK AND T6.BUSINESS_LINE_ID = TAppr.BUSINESS_LINE_ID) BUSINESS_LINE_DESCRIPTION,"+
					" TAPPR.FEE_BASIS,"
					+ " TAPPR.FEE_TYPE,(SELECT T1.ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB T1 where t1.Alpha_tab =  7033 and T1.ALPHA_SUB_TAB=TAPPR.FEE_TYPE )FEE_TYPE_DESC, "
					+ "TAPPR.TIER_TYPE,(SELECT T1.ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB T1 where t1.Alpha_tab =  7034 and T1.ALPHA_SUB_TAB=TAPPR.TIER_TYPE )TIER_TYPE_DESC,"
					+" TO_CHAR(TAPPR.EFFECTIVE_DATE,'DD-Mon-RRRR HH24:MI:SS') EFFECTIVE_DATE,"+
					" TAppr.FEE_LINE_STATUS,T3.NUM_SUBTAB_DESCRIPTION FEE_LINE_STATUS_DESC, TAppr.RECORD_INDICATOR,T7.NUM_SUBTAB_DESCRIPTION RECORD_INDICATOR_DESC,                                                                                           "+
					" TAppr. MAKER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "+getDbFunction("NVL")+"(TAppr.MAKER,0) ) MAKER_NAME,                                  "+
					" TAppr.VERIFIER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "+getDbFunction("NVL")+"(TAppr.VERIFIER,0) ) VERIFIER_NAME,                          "+
					" "+getDbFunction("DATEFUNC")+"(TAppr.DATE_LAST_MODIFIED, 'dd-MM-yyyy "+getDbFunction("TIME")+"') DATE_LAST_MODIFIED ,"+getDbFunction("DATEFUNC")+"(TAppr.DATE_CREATION, 'dd-MM-yyyy "+getDbFunction("TIME")+"') DATE_CREATION "+                                                                                               
					" FROM RA_MST_FEES_HEADER TAppr ,NUM_SUB_TAB T7 , NUM_SUB_TAB T3                                                                                "+
					" Where   "+
					"TAPPR.COUNTRY = ? AND TAPPR.LE_BOOK = ? AND "+
					" TAPPR.BUSINESS_LINE_ID = ? AND   "+effectiveDateAppr+
					" T7.NUM_tab = TAppr.RECORD_INDICATOR_NT AND " + 
					" T3.NUM_tab = TAppr.FEE_LINE_STATUS_NT AND " + 
					" T3.NUM_sub_tab = TAppr.FEE_LINE_STATUS" + 
					" and T7.NUM_sub_tab = TAppr.RECORD_INDICATOR");
			strQueryPend = new String(" SELECT TPend.COUNTRY, TPend.LE_BOOK,TPend.BUSINESS_LINE_ID,"
					+ "(select T1.ALPHA_SUBTAB_DESCRIPTION from ALPHA_SUB_TAB T1 where t1.Alpha_tab = TPend.FEE_BASIS_AT and T1.ALPHA_SUB_TAB=TPend.FEE_BASIS) FEE_BASIS_DESC,"+
					" (SELECT T6.BUSINESS_LINE_DESCRIPTION FROM RA_MST_BUSINESS_LINE_HEADER T6 WHERE T6.COUNTRY = TPend.COUNTRY AND "+
					" T6.LE_BOOK = TPend.LE_BOOK AND T6.BUSINESS_LINE_ID = TPend.BUSINESS_LINE_ID) BUSINESS_LINE_DESCRIPTION,"+
					" TPend.FEE_BASIS,"
					+ " TPend.FEE_TYPE,(SELECT T1.ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB T1 where t1.Alpha_tab =  7033 and T1.ALPHA_SUB_TAB=TPend.FEE_TYPE )FEE_TYPE_DESC, "
					+ "TPend.TIER_TYPE,(SELECT T1.ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB T1 where t1.Alpha_tab =  7034 and T1.ALPHA_SUB_TAB=TPend.TIER_TYPE )TIER_TYPE_DESC,"
					+" TO_CHAR(TPend.EFFECTIVE_DATE,'DD-Mon-RRRR HH24:MI:SS') EFFECTIVE_DATE,"+
					" TPend.FEE_LINE_STATUS,T3.NUM_SUBTAB_DESCRIPTION FEE_LINE_STATUS_DESC,TPend.RECORD_INDICATOR,T7.NUM_SUBTAB_DESCRIPTION RECORD_INDICATOR_DESC,                                                                                           "+
					" TPend. MAKER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "+getDbFunction("NVL")+"(TPend.MAKER,0) ) MAKER_NAME,                                  "+
					" TPend.VERIFIER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "+getDbFunction("NVL")+"(TPend.VERIFIER,0) ) VERIFIER_NAME,                          "+
					" "+getDbFunction("DATEFUNC")+"(TPend.DATE_LAST_MODIFIED, 'dd-MM-yyyy "+getDbFunction("TIME")+"') DATE_LAST_MODIFIED ,"+getDbFunction("DATEFUNC")+"(TPend.DATE_CREATION, 'dd-MM-yyyy "+getDbFunction("TIME")+"') DATE_CREATION "+                                                                                               
					" FROM RA_MST_FEES_HEADER_PEND TPend , NUM_SUB_TAB T7  ,NUM_SUB_TAB T3                                                                               "+
					" Where  "+
					" TPend.COUNTRY = ? AND TPend.LE_BOOK = ? AND "+
					" TPend.BUSINESS_LINE_ID = ?  AND "+effectiveDatePend+
					" T7.NUM_tab = TPend.RECORD_INDICATOR_NT AND " + 
					" T3.NUM_tab = TPend.FEE_LINE_STATUS_NT AND " + 
					" T3.NUM_sub_tab = TPend.FEE_LINE_STATUS" + 
					" and T7.NUM_sub_tab = TPend.RECORD_INDICATOR");
		}else if ("MSSQL".equalsIgnoreCase(databaseType)) {
		strQueryAppr = new String(" SELECT TAppr.COUNTRY, TAppr.LE_BOOK,TAppr.BUSINESS_LINE_ID,(select T1.ALPHA_SUBTAB_DESCRIPTION from ALPHA_SUB_TAB T1 where t1.Alpha_tab = TAPPR.FEE_BASIS_AT and T1.ALPHA_SUB_TAB=TAPPR.FEE_BASIS) FEE_BASIS_DESC,"+
				" (SELECT T6.BUSINESS_LINE_DESCRIPTION FROM RA_MST_BUSINESS_LINE_HEADER T6 WHERE T6.COUNTRY = TAPPR.COUNTRY AND "+
				" T6.LE_BOOK = TAPPR.LE_BOOK AND T6.BUSINESS_LINE_ID = TAPPR.BUSINESS_LINE_ID) BUSINESS_LINE_DESCRIPTION,"+
				" TAPPR.FEE_BASIS,"
				+ " TAppr.FEE_TYPE,(SELECT T1.ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB T1 where t1.Alpha_tab =  7033 and T1.ALPHA_SUB_TAB=TAppr.FEE_TYPE )FEE_TYPE_DESC, "
				+ "TAppr.TIER_TYPE,(SELECT T1.ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB T1 where t1.Alpha_tab =  7034 and T1.ALPHA_SUB_TAB=TAppr.TIER_TYPE )TIER_TYPE_DESC,"
				+" "+getDbFunction("DATEFUNC")+"(CAST(TAppr.EFFECTIVE_DATE AS DATETIME), 'dd-MMM-yyyy HH:mm:ss') EFFECTIVE_DATE,"+
				" TAppr.FEE_LINE_STATUS,T3.NUM_SUBTAB_DESCRIPTION FEE_LINE_STATUS_DESC,TAppr.RECORD_INDICATOR, T7.NUM_SUBTAB_DESCRIPTION RECORD_INDICATOR_DESC,                                                                                         "+
				" TAppr. MAKER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "+getDbFunction("NVL")+"(TAppr.MAKER,0) ) MAKER_NAME,                                  "+
				" TAppr.VERIFIER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "+getDbFunction("NVL")+"(TAppr.VERIFIER,0) ) VERIFIER_NAME,                          "+
				" "+getDbFunction("DATEFUNC")+"(TAppr.DATE_LAST_MODIFIED, '"+getDbFunction("DD_Mon_RRRR")+" "+getDbFunction("TIME")+"') DATE_LAST_MODIFIED ,"+getDbFunction("DATEFUNC")+"(TAppr.DATE_CREATION, '"+getDbFunction("DD_Mon_RRRR")+" "+getDbFunction("TIME")+"') DATE_CREATION "+                                                                                               
				" FROM RA_MST_FEES_HEADER TAppr ,NUM_SUB_TAB T7 ,  NUM_SUB_TAB T3                                                                               "+
				" Where  "+
				" TAPPR.COUNTRY = ? AND TAPPR.LE_BOOK = ? AND "+
				" TAPPR.BUSINESS_LINE_ID = ? AND "+effectiveDateAppr +
				" T7.NUM_tab = TAppr.RECORD_INDICATOR_NT AND " + 
				" T3.NUM_tab = TAppr.FEE_LINE_STATUS_NT AND " + 
				" T3.NUM_sub_tab = TAppr.FEE_LINE_STATUS" + 
				" and T7.NUM_sub_tab = TAppr.RECORD_INDICATOR");
		strQueryPend = new String(" SELECT TPend.COUNTRY, TPend.LE_BOOK,"+
				" TPend.BUSINESS_LINE_ID,(select T1.ALPHA_SUBTAB_DESCRIPTION from ALPHA_SUB_TAB T1"
				+ " where t1.Alpha_tab = TPend.FEE_BASIS_AT and T1.ALPHA_SUB_TAB=TPend.FEE_BASIS) FEE_BASIS_DESC,"+
				" (SELECT T6.BUSINESS_LINE_DESCRIPTION FROM RA_MST_BUSINESS_LINE_HEADER_PEND T6 WHERE T6.COUNTRY = TPend.COUNTRY AND "+
				" T6.LE_BOOK = TPend.LE_BOOK AND T6.BUSINESS_LINE_ID = TPend.BUSINESS_LINE_ID) BUSINESS_LINE_DESCRIPTION,"+
				" TPend.FEE_BASIS,"
				+ " TPend.FEE_TYPE,(SELECT T1.ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB T1 where t1.Alpha_tab =  7033 and T1.ALPHA_SUB_TAB=TPend.FEE_TYPE )FEE_TYPE_DESC, "
				+ "TPend.TIER_TYPE,(SELECT T1.ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB T1 where t1.Alpha_tab =  7034 and T1.ALPHA_SUB_TAB=TPend.TIER_TYPE )TIER_TYPE_DESC,"
				+" "+getDbFunction("DATEFUNC")+"(CAST(TPend.EFFECTIVE_DATE AS DATETIME), 'dd-MMM-yyyy HH:mm:ss') EFFECTIVE_DATE,"+
				" TPend.FEE_LINE_STATUS,T3.NUM_SUBTAB_DESCRIPTION FEE_LINE_STATUS_DESC,TPend.RECORD_INDICATOR,T7.NUM_SUBTAB_DESCRIPTION RECORD_INDICATOR_DESC,                                                                                           "+
				" TPend. MAKER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "+getDbFunction("NVL")+"(TPend.MAKER,0) ) MAKER_NAME,                                  "+
				" TPend.VERIFIER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "+getDbFunction("NVL")+"(TPend.VERIFIER,0) ) VERIFIER_NAME,                          "+
				" "+getDbFunction("DATEFUNC")+"(TPend.DATE_LAST_MODIFIED, '"+getDbFunction("DD_Mon_RRRR")+" "+getDbFunction("TIME")+"') DATE_LAST_MODIFIED ,"+getDbFunction("DATEFUNC")+"(TPend.DATE_CREATION, '"+getDbFunction("DD_Mon_RRRR")+" "+getDbFunction("TIME")+"') DATE_CREATION "+                                                                                               
				" FROM RA_MST_FEES_HEADER_PEND TPend , NUM_SUB_TAB T7  , NUM_SUB_TAB T3                                                                               "+
				" Where   "+
				" TPend.COUNTRY = ? AND TPend.LE_BOOK = ?  AND "+
				" TPend.BUSINESS_LINE_ID = ? AND "+effectiveDatePend+
				" T7.NUM_tab = TPend.RECORD_INDICATOR_NT AND " + 
				" T3.NUM_tab = TPend.FEE_LINE_STATUS_NT AND " + 
				" T3.NUM_sub_tab = TPend.FEE_LINE_STATUS" + 
				" and T7.NUM_sub_tab = TPend.RECORD_INDICATOR");
	}
		
		Object objParams[] = new Object[3];
		objParams[0] = new String(dObj.getCountry());// country
		objParams[1] = new String(dObj.getLeBook());
		objParams[2] = new String(dObj.getBusinessLineId());
//		objParams[3] = new String(dObj.getEffectiveDate());
		try
		{
			/*if(!dObj.isVerificationRequired() || dObj.isReview()){intStatus =0;}*/
			if(intStatus == 0)
			{
				logger.info("Executing approved query");
				collTemp = getJdbcTemplate().query(strQueryAppr.toString(),objParams,getDetailMapper());
			}else{
				logger.info("Executing pending query");
				collTemp = getJdbcTemplate().query(strQueryPend.toString(),objParams,getDetailMapper());
			}
			return collTemp;
		}catch(Exception ex){
			ex.printStackTrace();
			logger.error("Error: getQueryResults Exception :   ");
			/*if(intStatus == 0)
				//logger.error(((strQueryAppr == null) ? "strQueryAppr is Null" : strQueryAppr.toString()));
			else
				//logger.error(((strQueryPend == null) ? "strQueryPend is Null" : strQueryPend.toString()));

			if (objParams != null)
				for(int i=0 ; i< objParams.length; i++)
					//logger.error("objParams[" + i + "]" + objParams[i].toString());*/
			return null;
		}
	}
	@Override
	public List<FeesConfigHeaderVb> getQueryResultsForReview(FeesConfigHeaderVb dObj, int intStatus){
		List<FeesConfigHeaderVb> collTemp = null;
		setServiceDefaults();
		String strQueryAppr = null;
		String strQueryPend = null;
		if ("ORACLE".equalsIgnoreCase(databaseType)) {
			strQueryAppr = new String(" SELECT TAppr.COUNTRY, TAppr.LE_BOOK,TAppr.BUSINESS_LINE_ID,"
					+ "(select T1.ALPHA_SUBTAB_DESCRIPTION from ALPHA_SUB_TAB T1 where t1.Alpha_tab = TAPPR.FEE_BASIS_AT and T1.ALPHA_SUB_TAB=TAPPR.FEE_BASIS) FEE_BASIS_DESC,"+
					" (SELECT T6.BUSINESS_LINE_DESCRIPTION FROM RA_MST_BUSINESS_LINE_HEADER T6 WHERE T6.COUNTRY = TAppr.COUNTRY AND "+
					" T6.LE_BOOK = TAppr.LE_BOOK AND T6.BUSINESS_LINE_ID = TAppr.BUSINESS_LINE_ID) BUSINESS_LINE_DESCRIPTION,"+
					" TAPPR.FEE_BASIS,"
					+ " TAPPR.FEE_TYPE,(SELECT T1.ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB T1 where t1.Alpha_tab =  7033 and T1.ALPHA_SUB_TAB=TAPPR.FEE_TYPE )FEE_TYPE_DESC, "
					+ "TAPPR.TIER_TYPE,(SELECT T1.ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB T1 where t1.Alpha_tab =  7034 and T1.ALPHA_SUB_TAB=TAPPR.TIER_TYPE )TIER_TYPE_DESC,"
					+" TO_CHAR(TAPPR.EFFECTIVE_DATE,'DD-Mon-RRRR HH24:MI:SS') EFFECTIVE_DATE,"+
					" TAppr.FEE_LINE_STATUS,T3.NUM_SUBTAB_DESCRIPTION FEE_LINE_STATUS_DESC, TAppr.RECORD_INDICATOR,T7.NUM_SUBTAB_DESCRIPTION RECORD_INDICATOR_DESC,                                                                                           "+
					" TAppr. MAKER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "+getDbFunction("NVL")+"(TAppr.MAKER,0) ) MAKER_NAME,                                  "+
					" TAppr.VERIFIER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "+getDbFunction("NVL")+"(TAppr.VERIFIER,0) ) VERIFIER_NAME,                          "+
					" "+getDbFunction("DATEFUNC")+"(TAppr.DATE_LAST_MODIFIED, '"+getDbFunction("DD_Mon_RRRR")+" "+getDbFunction("TIME")+"') DATE_LAST_MODIFIED ,"+getDbFunction("DATEFUNC")+"(TAppr.DATE_CREATION, '"+getDbFunction("DD_Mon_RRRR")+" "+getDbFunction("TIME")+"') DATE_CREATION "+                                                                                               
					" FROM RA_MST_FEES_HEADER TAppr ,NUM_SUB_TAB T7 , NUM_SUB_TAB T3                                                                                "+
					" Where   "+
					"TAPPR.COUNTRY = ? AND TAPPR.LE_BOOK = ? AND "+
					" TAPPR.BUSINESS_LINE_ID = ? AND To_Char(TAPPR.EFFECTIVE_DATE,'DD-Mon-RRRR HH24:MI:SS') = To_Char(?,'DD-Mon-RRRR HH24:MI:SS') AND  "+
					" T7.NUM_tab = TAppr.RECORD_INDICATOR_NT AND " + 
					" T3.NUM_tab = TAppr.FEE_LINE_STATUS_NT AND " + 
					" T3.NUM_sub_tab = TAppr.FEE_LINE_STATUS" + 
					" and T7.NUM_sub_tab = TAppr.RECORD_INDICATOR");
			strQueryPend = new String(" SELECT TPend.COUNTRY, TPend.LE_BOOK,TPend.BUSINESS_LINE_ID,"
					+ "(select T1.ALPHA_SUBTAB_DESCRIPTION from ALPHA_SUB_TAB T1 where t1.Alpha_tab = TPend.FEE_BASIS_AT and T1.ALPHA_SUB_TAB=TPend.FEE_BASIS) FEE_BASIS_DESC,"+
					" (SELECT T6.BUSINESS_LINE_DESCRIPTION FROM RA_MST_BUSINESS_LINE_HEADER T6 WHERE T6.COUNTRY = TPend.COUNTRY AND "+
					" T6.LE_BOOK = TPend.LE_BOOK AND T6.BUSINESS_LINE_ID = TPend.BUSINESS_LINE_ID) BUSINESS_LINE_DESCRIPTION,"+
					" TAPPR.FEE_BASIS,"
					+ " TPend.FEE_TYPE,(SELECT T1.ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB T1 where t1.Alpha_tab =  7033 and T1.ALPHA_SUB_TAB=TPend.FEE_TYPE )FEE_TYPE_DESC, "
					+ "TPend.TIER_TYPE,(SELECT T1.ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB T1 where t1.Alpha_tab =  7034 and T1.ALPHA_SUB_TAB=TPend.TIER_TYPE )TIER_TYPE_DESC,"
					+" TO_CHAR(TPend.EFFECTIVE_DATE,'DD-Mon-RRRR HH24:MI:SS') EFFECTIVE_DATE,"+
					" TPend.FEE_LINE_STATUS,T3.NUM_SUBTAB_DESCRIPTION FEE_LINE_STATUS_DESC,TPend.RECORD_INDICATOR,T7.NUM_SUBTAB_DESCRIPTION RECORD_INDICATOR_DESC,                                                                                           "+
					" TPend. MAKER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "+getDbFunction("NVL")+"(TPend.MAKER,0) ) MAKER_NAME,                                  "+
					" TPend.VERIFIER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "+getDbFunction("NVL")+"(TPend.VERIFIER,0) ) VERIFIER_NAME,                          "+
					" "+getDbFunction("DATEFUNC")+"(TPend.DATE_LAST_MODIFIED, '"+getDbFunction("DD_Mon_RRRR")+" "+getDbFunction("TIME")+"') DATE_LAST_MODIFIED ,"+getDbFunction("DATEFUNC")+"(TPend.DATE_CREATION, '"+getDbFunction("DD_Mon_RRRR")+" "+getDbFunction("TIME")+"') DATE_CREATION "+                                                                                               
					" FROM RA_MST_FEES_HEADER_PEND TPend , NUM_SUB_TAB T7  ,NUM_SUB_TAB T3                                                                               "+
					" Where  "+
					" TPend.COUNTRY = ? AND TPend.LE_BOOK = ? AND "+
					" TPend.BUSINESS_LINE_ID = ?  AND To_Char(TPend.EFFECTIVE_DATE,'DD-Mon-RRRR HH24:MI:SS')  = To_Char(?,'DD-Mon-RRRR HH24:MI:SS') AND  "+
					" T7.NUM_tab = TPend.RECORD_INDICATOR_NT AND " + 
					" T3.NUM_tab = TPend.FEE_LINE_STATUS_NT AND " + 
					" T3.NUM_sub_tab = TPend.FEE_LINE_STATUS" + 
					" and T7.NUM_sub_tab = TPend.RECORD_INDICATOR");
		}else if ("MSSQL".equalsIgnoreCase(databaseType)) {
		strQueryAppr = new String(" SELECT TAppr.COUNTRY, TAppr.LE_BOOK,TAppr.BUSINESS_LINE_ID,(select T1.ALPHA_SUBTAB_DESCRIPTION from ALPHA_SUB_TAB T1 where t1.Alpha_tab = TAPPR.FEE_BASIS_AT and T1.ALPHA_SUB_TAB=TAPPR.FEE_BASIS) FEE_BASIS_DESC,"+
				" (SELECT T6.BUSINESS_LINE_DESCRIPTION FROM RA_MST_BUSINESS_LINE_HEADER T6 WHERE T6.COUNTRY = TAPPR.COUNTRY AND "+
				" T6.LE_BOOK = TAPPR.LE_BOOK AND T6.BUSINESS_LINE_ID = TAPPR.BUSINESS_LINE_ID) BUSINESS_LINE_DESCRIPTION,"+
				" TAPPR.FEE_BASIS,"
				+ " TAppr.FEE_TYPE,(SELECT T1.ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB T1 where t1.Alpha_tab =  7033 and T1.ALPHA_SUB_TAB=TAppr.FEE_TYPE )FEE_TYPE_DESC, "
				+ "TAppr.TIER_TYPE,(SELECT T1.ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB T1 where t1.Alpha_tab =  7034 and T1.ALPHA_SUB_TAB=TAppr.TIER_TYPE )TIER_TYPE_DESC,"
				+" "+getDbFunction("DATEFUNC")+"(CAST(TAppr.EFFECTIVE_DATE AS DATETIME), 'dd-MMM-yyyy HH:mm:ss') EFFECTIVE_DATE,"+
				" TAppr.FEE_LINE_STATUS,T3.NUM_SUBTAB_DESCRIPTION FEE_LINE_STATUS_DESC,TAppr.RECORD_INDICATOR, T7.NUM_SUBTAB_DESCRIPTION RECORD_INDICATOR_DESC,                                                                                         "+
				" TAppr. MAKER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "+getDbFunction("NVL")+"(TAppr.MAKER,0) ) MAKER_NAME,                                  "+
				" TAppr.VERIFIER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "+getDbFunction("NVL")+"(TAppr.VERIFIER,0) ) VERIFIER_NAME,                          "+
				" "+getDbFunction("DATEFUNC")+"(TAppr.DATE_LAST_MODIFIED, '"+getDbFunction("DD_Mon_RRRR")+" "+getDbFunction("TIME")+"') DATE_LAST_MODIFIED ,"+getDbFunction("DATEFUNC")+"(TAppr.DATE_CREATION, '"+getDbFunction("DD_Mon_RRRR")+" "+getDbFunction("TIME")+"') DATE_CREATION "+                                                                                               
				" FROM RA_MST_FEES_HEADER TAppr ,NUM_SUB_TAB T7 ,  NUM_SUB_TAB T3                                                                               "+
				" Where  "+
				" TAPPR.COUNTRY = ? AND TAPPR.LE_BOOK = ? AND "+
				" TAPPR.BUSINESS_LINE_ID = ? AND TAPPR.EFFECTIVE_DATE = ? AND "+
				" T7.NUM_tab = TAppr.RECORD_INDICATOR_NT AND " + 
				" T3.NUM_tab = TAppr.FEE_LINE_STATUS_NT AND " + 
				" T3.NUM_sub_tab = TAppr.FEE_LINE_STATUS" + 
				" and T7.NUM_sub_tab = TAppr.RECORD_INDICATOR");
		strQueryPend = new String(" SELECT TPend.COUNTRY, TPend.LE_BOOK,"+
				" TPend.BUSINESS_LINE_ID,(select T1.ALPHA_SUBTAB_DESCRIPTION from ALPHA_SUB_TAB T1"
				+ " where t1.Alpha_tab = TPend.FEE_BASIS_AT and T1.ALPHA_SUB_TAB=TPend.FEE_BASIS) FEE_BASIS_DESC,"+
				" (SELECT T6.BUSINESS_LINE_DESCRIPTION FROM RA_MST_BUSINESS_LINE_HEADER T6 WHERE T6.COUNTRY = TPend.COUNTRY AND "+
				" T6.LE_BOOK = TPend.LE_BOOK AND T6.BUSINESS_LINE_ID = TPend.BUSINESS_LINE_ID) BUSINESS_LINE_DESCRIPTION,"+
				" TPend.FEE_BASIS,"
				+ " TPend.FEE_TYPE,(SELECT T1.ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB T1 where t1.Alpha_tab =  7033 and T1.ALPHA_SUB_TAB=TPend.FEE_TYPE )FEE_TYPE_DESC, "
				+ "TPend.TIER_TYPE,(SELECT T1.ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB T1 where t1.Alpha_tab =  7034 and T1.ALPHA_SUB_TAB=TPend.TIER_TYPE )TIER_TYPE_DESC,"
				+" "+getDbFunction("DATEFUNC")+"(CAST(TPend.EFFECTIVE_DATE AS DATETIME), 'dd-MMM-yyyy HH:mm:ss') EFFECTIVE_DATE,"+
				" TPend.FEE_LINE_STATUS,T3.NUM_SUBTAB_DESCRIPTION FEE_LINE_STATUS_DESC,TPend.RECORD_INDICATOR,T7.NUM_SUBTAB_DESCRIPTION RECORD_INDICATOR_DESC,                                                                                           "+
				" TPend. MAKER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "+getDbFunction("NVL")+"(TPend.MAKER,0) ) MAKER_NAME,                                  "+
				" TPend.VERIFIER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "+getDbFunction("NVL")+"(TPend.VERIFIER,0) ) VERIFIER_NAME,                          "+
				" "+getDbFunction("DATEFUNC")+"(TPend.DATE_LAST_MODIFIED, '"+getDbFunction("DD_Mon_RRRR")+" "+getDbFunction("TIME")+"') DATE_LAST_MODIFIED ,"+getDbFunction("DATEFUNC")+"(TPend.DATE_CREATION, '"+getDbFunction("DD_Mon_RRRR")+" "+getDbFunction("TIME")+"') DATE_CREATION "+                                                                                               
				" FROM RA_MST_FEES_HEADER_PEND TPend , NUM_SUB_TAB T7  , NUM_SUB_TAB T3                                                                               "+
				" Where   "+
				" TPend.COUNTRY = ? AND TPend.LE_BOOK = ?  AND "+
				" TPend.BUSINESS_LINE_ID = ? AND TPend.EFFECTIVE_DATE = ? AND"+
				" T7.NUM_tab = TPend.RECORD_INDICATOR_NT AND " + 
				" T3.NUM_tab = TPend.FEE_LINE_STATUS_NT AND " + 
				" T3.NUM_sub_tab = TPend.FEE_LINE_STATUS" + 
				" and T7.NUM_sub_tab = TPend.RECORD_INDICATOR");
	}
		
		Object objParams[] = new Object[4];
		objParams[0] = new String(dObj.getCountry());// country
		objParams[1] = new String(dObj.getLeBook());
		objParams[2] = new String(dObj.getBusinessLineId());
		objParams[3] = new String(dObj.getEffectiveDate());
		try
		{
			if(intStatus == 0)
			{
				logger.info("Executing approved query");
				collTemp = getJdbcTemplate().query(strQueryAppr.toString(),objParams,getDetailMapper());
			}else{
				logger.info("Executing pending query");
				collTemp = getJdbcTemplate().query(strQueryPend.toString(),objParams,getDetailMapper());
			}
			return collTemp;
		}catch(Exception ex){
			ex.printStackTrace();
			logger.error("Error: getQueryResults Exception :   ");
			/*if(intStatus == 0)
				//logger.error(((strQueryAppr == null) ? "strQueryAppr is Null" : strQueryAppr.toString()));
			else
				//logger.error(((strQueryPend == null) ? "strQueryPend is Null" : strQueryPend.toString()));

			if (objParams != null)
				for(int i=0 ; i< objParams.length; i++)
					//logger.error("objParams[" + i + "]" + objParams[i].toString());*/
			return null;
		}
	}
	@Override
	protected void setServiceDefaults() {
		serviceName = "FeesConfigHeader";
		serviceDesc = "Fees Config Header";
		tableName = "RA_MST_FEES_HEADER";
		childTableName = "RA_MST_FEES_HEADER";
		intCurrentUserId = CustomContextHolder.getContext().getVisionId();
	}
	protected int doInsertionApprFeesHeaders(FeesConfigHeaderVb vObject){
		String effectiveDate ="?";
		if (databaseType.equalsIgnoreCase("ORACLE")) {
			effectiveDate = "TO_DATE(?,'DD-MM-YYYY HH24:MI:SS')";
		}
		String query =  " Insert Into RA_MST_FEES_HEADER(COUNTRY,LE_BOOK,BUSINESS_LINE_ID," + 
				" EFFECTIVE_DATE, FEE_TYPE,TIER_TYPE, FEE_LINE_STATUS_NT, FEE_LINE_STATUS," + 
				"RECORD_INDICATOR_NT,RECORD_INDICATOR,MAKER,VERIFIER,FEE_BASIS,DATE_LAST_MODIFIED,DATE_CREATION) "+
				" Values (?,?,?,"+effectiveDate+",?,?,?,?,?,?,?,?,?,"+getDbFunction("SYSDATE")+","+getDbFunction("SYSDATE")+")";
		
		Object[] args = {vObject.getCountry(),vObject.getLeBook(),vObject.getBusinessLineId(),
				vObject.getEffectiveDate(),
				vObject.getFeeType(),vObject.getTierType(),
				vObject.getFeesLineStatusNt(),vObject.getFeesLineStatus(),
				vObject.getRecordIndicatorNt(),vObject.getRecordIndicator(),
				vObject.getMaker(),vObject.getVerifier(),vObject.getFeeBasis()};
		return getJdbcTemplate().update(query,args);
	}
	protected int doInsertionPendFeesHeaders(FeesConfigHeaderVb vObject){
		String effectiveDate ="?";
		if (databaseType.equalsIgnoreCase("ORACLE")) {
			effectiveDate = "TO_DATE(?,'DD-MM-YYYY HH24:MI:SS')";
		}
		String query =  " Insert Into RA_MST_FEES_HEADER_PEND(COUNTRY,LE_BOOK,BUSINESS_LINE_ID," + 
				" EFFECTIVE_DATE, FEE_TYPE,TIER_TYPE, FEE_LINE_STATUS_NT, FEE_LINE_STATUS," + 
				"RECORD_INDICATOR_NT,RECORD_INDICATOR,MAKER,VERIFIER,FEE_BASIS,DATE_LAST_MODIFIED,DATE_CREATION) "+
				" Values (?,?,?,"+effectiveDate+",?,?,?,?,?,?,?,?,?," + getDbFunction("SYSDATE") + "," + getDbFunction("SYSDATE")
				+ ")";

		Object[] args = { vObject.getCountry(), vObject.getLeBook(), vObject.getBusinessLineId(),
				vObject.getEffectiveDate(), vObject.getFeeType(), vObject.getTierType(), vObject.getFeesLineStatusNt(),
				vObject.getFeesLineStatus(), vObject.getRecordIndicatorNt(), vObject.getRecordIndicator(),
				vObject.getMaker(), vObject.getVerifier(), vObject.getFeeBasis() };
		return getJdbcTemplate().update(query, args);
	}
	protected int doInsertionFeesHeadersHis(FeesConfigHeaderVb vObject){
		String dateCreation = "";
		String effectiveDate ="?";
		if ("ORACLE".equalsIgnoreCase(databaseType)) {
			dateCreation = "To_Date(?, 'DD-MM-YYYY HH24:MI:SS')";
			effectiveDate = "TO_DATE(?,'DD-MM-YYYY HH24:MI:SS')";
		}else if ("MSSQL".equalsIgnoreCase(databaseType)) {
			dateCreation = "CONVERT(datetime, ?, 103)";
		}
		String query =  " Insert Into RA_MST_FEES_HEADER_HIS(COUNTRY,LE_BOOK,BUSINESS_LINE_ID," + 
				" EFFECTIVE_DATE,REF_NO, FEE_TYPE,TIER_TYPE, FEE_LINE_STATUS_NT, FEE_LINE_STATUS," + 
				"RECORD_INDICATOR_NT,RECORD_INDICATOR,MAKER,VERIFIER,FEE_BASIS,DATE_LAST_MODIFIED,DATE_CREATION) "+
				" Values (?,?,?,"+effectiveDate+",?,?,?,?,?,?,?,?,?,?,"+commonDao.getDbFunction("SYSDATE")+","+dateCreation+")";
		
		Object[] args = {vObject.getCountry(),vObject.getLeBook(),vObject.getBusinessLineId(),
				vObject.getEffectiveDate(),vObject.getRefNo(),
				vObject.getFeeType(),vObject.getTierType(),
				vObject.getFeesLineStatusNt(),vObject.getFeesLineStatus(),
				vObject.getRecordIndicatorNt(),vObject.getRecordIndicator(),
				vObject.getMaker(),vObject.getVerifier(),vObject.getFeeBasis(),vObject.getDateCreation()};
		return getJdbcTemplate().update(query,args);
	}
	protected int doInsertionPendFeesHeadersDc(FeesConfigHeaderVb vObject){
		String query = "";
		if ("ORACLE".equalsIgnoreCase(databaseType)) {
			query =  " Insert Into RA_MST_FEES_HEADER_PEND(COUNTRY,LE_BOOK,BUSINESS_LINE_ID," + 
					" EFFECTIVE_DATE,FEE_TYPE,TIER_TYPE, FEE_LINE_STATUS_NT, FEE_LINE_STATUS," + 
					"RECORD_INDICATOR_NT,RECORD_INDICATOR,MAKER,VERIFIER,DATE_LAST_MODIFIED,DATE_CREATION,FEE_BASIS) "+
					" Values (?,?,?,TO_DATE(?,'DD-MM-YYYY HH24:MI:SS'),?,?,?,?,?,?,?,?,"+getDbFunction("SYSDATE")+", To_Date(?, 'DD-MM-YYYY HH24:MI:SS'),?)";
		}else if ("MSSQL".equalsIgnoreCase(databaseType)) {
			query =  " Insert Into RA_MST_FEES_HEADER_PEND(COUNTRY,LE_BOOK,BUSINESS_LINE_ID," + 
					"EFFECTIVE_DATE, FEE_TYPE,TIER_TYPE, FEE_LINE_STATUS_NT, FEE_LINE_STATUS," + 
					"RECORD_INDICATOR_NT,RECORD_INDICATOR,MAKER,VERIFIER,DATE_LAST_MODIFIED,DATE_CREATION,FEE_BASIS) "+
					" Values (?,?,?,?,?,?,?,?,?,?,?,?,"+getDbFunction("SYSDATE")+", CONVERT(datetime, ?, 103),?)";
			
		}
		
		Object[] args = {vObject.getCountry(),vObject.getLeBook(),vObject.getBusinessLineId(),
				vObject.getEffectiveDate(),
				vObject.getFeeType(),vObject.getTierType(),
				vObject.getFeesLineStatusNt(),vObject.getFeesLineStatus(),
				vObject.getRecordIndicatorNt(),vObject.getRecordIndicator(),
				vObject.getMaker(),vObject.getVerifier(),vObject.getDateCreation(),vObject.getFeeBasis()};
		return getJdbcTemplate().update(query,args);
	}
	protected int doUpdateApprHeader(FeesConfigHeaderVb vObject){
		String effectiveDate="";
		if ("ORACLE".equalsIgnoreCase(databaseType)) {
			effectiveDate =" EFFECTIVE_DATE= TO_DATE( ?,'DD-MM-YYYY HH24:MI:SS')";
		}else{ 
			effectiveDate = "EFFECTIVE_DATE= ?";
		}
		String query = " Update RA_MST_FEES_HEADER set "+
				" FEE_BASIS=? ,FEE_TYPE= ?,TIER_TYPE= ?, "+
				" FEE_LINE_STATUS= ? ,RECORD_INDICATOR= ? ,MAKER= ? ,"+
				" VERIFIER= ? ,DATE_LAST_MODIFIED= "+getDbFunction("SYSDATE")+" "+
				" WHERE COUNTRY= ? AND LE_BOOK= ? AND BUSINESS_LINE_ID = ?"+
				" AND "+effectiveDate;
		Object[] args = {vObject.getFeeBasis(),vObject.getFeeType(),
				vObject.getTierType(),vObject.getFeesLineStatus(),vObject.getRecordIndicator(),
				vObject.getMaker(),vObject.getVerifier(),vObject.getCountry(),vObject.getLeBook(),
				vObject.getBusinessLineId(),
				vObject.getEffectiveDate()};
		return getJdbcTemplate().update(query,args);
	}
	protected int doUpdatePendHeader(FeesConfigHeaderVb vObject){
		String effectiveDate="";
		if ("ORACLE".equalsIgnoreCase(databaseType)) {
			effectiveDate =" EFFECTIVE_DATE= TO_DATE( ?,'DD-MM-YYYY HH24:MI:SS')";
		}else{ 
			effectiveDate = "EFFECTIVE_DATE= ?";
		}
			String query = " Update RA_MST_FEES_HEADER_PEND set "+
					"  FEE_BASIS=? , FEE_TYPE= ?,TIER_TYPE= ?, "+
					" FEE_LINE_STATUS= ? ,RECORD_INDICATOR= ? ,MAKER= ? ,"+
					" VERIFIER= ? ,DATE_LAST_MODIFIED= "+getDbFunction("SYSDATE")+" "+
					" WHERE COUNTRY= ? AND LE_BOOK= ? AND BUSINESS_LINE_ID = ?"+
					" AND "+effectiveDate;
			Object[] args = {vObject.getFeeBasis(),vObject.getFeeType(),
					vObject.getTierType(),vObject.getFeesLineStatus(),vObject.getRecordIndicator(),
					vObject.getMaker(),vObject.getVerifier(),vObject.getCountry(),vObject.getLeBook(),
					vObject.getBusinessLineId(),
					vObject.getEffectiveDate()};
			return getJdbcTemplate().update(query,args);
		}
	protected int deleteFeesHeaderAppr(FeesConfigHeaderVb vObject){
		String effectiveDate = "";
		if ("ORACLE".equalsIgnoreCase(databaseType)) {
			effectiveDate =" EFFECTIVE_DATE= TO_DATE( ?,'DD-MM-YYYY HH24:MI:SS')";
		}else{ 
			effectiveDate = "EFFECTIVE_DATE= ?";
		}
		String query = "Delete from RA_MST_FEES_HEADER  WHERE COUNTRY= ? AND LE_BOOK= ?  AND BUSINESS_LINE_ID = ?"+
				" AND  "+effectiveDate+" ";
		Object[] args = {vObject.getCountry(),vObject.getLeBook(),vObject.getBusinessLineId(),
				vObject.getEffectiveDate()};
		return getJdbcTemplate().update(query,args);
		
	}
	protected int deleteFeesHeaderPend(FeesConfigHeaderVb vObject){
		String effectiveDate = "";
		if ("ORACLE".equalsIgnoreCase(databaseType)) {
			effectiveDate =" EFFECTIVE_DATE= TO_DATE( ?,'DD-MM-YYYY HH24:MI:SS')";
		}else{ 
			effectiveDate = "EFFECTIVE_DATE= ?";
		}
		String query = "Delete from RA_MST_FEES_HEADER_PEND  WHERE COUNTRY= ? AND LE_BOOK= ?  AND BUSINESS_LINE_ID = ?"+
				" AND "+effectiveDate+" ";
		Object[] args = {vObject.getCountry(),vObject.getLeBook(),vObject.getBusinessLineId(),
				vObject.getEffectiveDate()};
		return getJdbcTemplate().update(query,args);
		
	}
	@Override
	protected List<FeesConfigHeaderVb> selectApprovedRecord(FeesConfigHeaderVb vObject){
		return getQueryResults(vObject, Constants.STATUS_ZERO);
	}
	@Override
	public List<FeesConfigHeaderVb> doSelectPendingRecord(FeesConfigHeaderVb vObject){
		return getQueryResults(vObject, Constants.STATUS_PENDING);
	}
	@Override
	protected int getStatus(FeesConfigHeaderVb records){return records.getFeesLineStatus();}
	@Override
	protected void setStatus(FeesConfigHeaderVb vObject,int status){vObject.setFeesLineStatus(status);
	}
	@Override
	public ExceptionCode doInsertApprRecordForNonTrans(FeesConfigHeaderVb vObject) throws RuntimeCustomException {
		List<FeesConfigHeaderVb> collTemp = null;
		ExceptionCode exceptionCode = null;
		strCurrentOperation = Constants.ADD;
		strApproveOperation =Constants.ADD;		
		setServiceDefaults();
		collTemp = selectApprovedRecord(vObject);
		if (collTemp != null && !collTemp.isEmpty()) {
			exceptionCode = getResultObject(Constants.DUPLICATE_KEY_INSERTION);
			throw buildRuntimeCustomException(exceptionCode);
		}
		vObject.setRecordIndicator(Constants.STATUS_ZERO);
		vObject.setFeesLineStatus(Constants.STATUS_ZERO);
		vObject.setMaker(intCurrentUserId);
		vObject.setVerifier(intCurrentUserId);
		
		retVal = doInsertionApprFeesHeaders(vObject);
		if (retVal != Constants.SUCCESSFUL_OPERATION){
			exceptionCode = getResultObject(retVal);
			throw buildRuntimeCustomException(exceptionCode);
		} else {
			exceptionCode = getResultObject(Constants.SUCCESSFUL_OPERATION);
		}
		if (vObject.getFeesConfigDetaillst() != null && !vObject.getFeesConfigDetaillst().isEmpty()) {
			exceptionCode = feesConfigDetailsDao.deleteAndInsertApprFeeDetail(vObject);
		}
		exceptionCode =getResultObject(Constants.SUCCESSFUL_OPERATION);
		writeAuditLog(vObject, null);
		if(exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION){
			exceptionCode = getResultObject(Constants.AUDIT_TRAIL_ERROR);
			throw buildRuntimeCustomException(exceptionCode);
		}
		return exceptionCode;
	}
	@Override
	public ExceptionCode doInsertRecordForNonTrans(FeesConfigHeaderVb vObject) throws RuntimeCustomException {
		List<FeesConfigHeaderVb> collTemp = null;
		List<FeesConfigHeaderVb> collTempAppr = null;
		
		ExceptionCode exceptionCode = new ExceptionCode();
		strCurrentOperation = Constants.ADD;
		strApproveOperation =Constants.ADD;		
		setServiceDefaults();
		collTemp = doSelectPendingRecord(vObject);
		if (collTemp != null && !collTemp.isEmpty()) {
			logger.error("!!");
			exceptionCode = getResultObject(Constants.DUPLICATE_KEY_INSERTION);
			throw buildRuntimeCustomException(exceptionCode);
		}
		collTempAppr = selectApprovedRecord(vObject);
		if (collTempAppr != null && !collTempAppr.isEmpty()) {
			int staticDeletionFlag = getStatus(((ArrayList<FeesConfigHeaderVb>) collTempAppr).get(0));
			if (staticDeletionFlag == Constants.PASSIVATE){
				//logger.info("Collection size is greater than zero - Duplicate record found, but inactive");
				exceptionCode = getResultObject(Constants.RECORD_ALREADY_PRESENT_BUT_INACTIVE);
				throw buildRuntimeCustomException(exceptionCode);
			}
			else
			{
				//logger.info("Collection size is greater than zero - Duplicate record found");
				exceptionCode = getResultObject(Constants.DUPLICATE_KEY_INSERTION);
				throw buildRuntimeCustomException(exceptionCode);
			}
		}
		vObject.setRecordIndicator(Constants.STATUS_INSERT);
		vObject.setFeesLineStatus(Constants.STATUS_ZERO);
		vObject.setMaker(intCurrentUserId);
		//vObject.setVerifier(intCurrentUserId);
		vObject.setVerifier(0);
		retVal = doInsertionPendFeesHeaders(vObject);
		if (retVal != Constants.SUCCESSFUL_OPERATION){
			exceptionCode = getResultObject(retVal);
			throw buildRuntimeCustomException(exceptionCode);
		} else {
			exceptionCode = getResultObject(Constants.SUCCESSFUL_OPERATION);
		}
		if(vObject.getFeesConfigDetaillst() != null && vObject.getFeesConfigDetaillst().size() > 0) {
			exceptionCode = feesConfigDetailsDao.deleteAndInsertPendFeeDetail(vObject);
		}
		if (exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION) {
			exceptionCode = getResultObject(retVal);
			throw buildRuntimeCustomException(exceptionCode);
		} else {
			writeAuditLog(vObject, null);
			exceptionCode = getResultObject(Constants.SUCCESSFUL_OPERATION);
		}
		return exceptionCode;
	}
	@Override
	public ExceptionCode doUpdateApprRecordForNonTrans(FeesConfigHeaderVb vObject) throws RuntimeCustomException  {
		List<FeesConfigHeaderVb> collTemp = null;
		FeesConfigHeaderVb vObjectlocal = null;
		ExceptionCode exceptionCode = null;
		strApproveOperation =Constants.MODIFY;
		strErrorDesc  = "";
		strCurrentOperation = Constants.MODIFY;
		setServiceDefaults();
		vObject.setMaker(getIntCurrentUserId());
		collTemp = selectApprovedRecord(vObject);
		if (collTemp == null){
			exceptionCode = getResultObject(Constants.ERRONEOUS_OPERATION);
			throw buildRuntimeCustomException(exceptionCode);
		}
		vObjectlocal = ((ArrayList<FeesConfigHeaderVb>)collTemp).get(0);
		// Even if record is not there in Appr. table reject the record
		if (collTemp.size() == 0){
			exceptionCode = getResultObject(Constants.ATTEMPT_TO_MODIFY_UNEXISTING_RECORD);
			throw buildRuntimeCustomException(exceptionCode);
		}
		if (vObject.getFeesLineStatus() == Constants.PASSIVATE) {
			exceptionCode = getResultObject(Constants.CANNOT_MODIFY_TO_DELETE_STATE);
			throw buildRuntimeCustomException(exceptionCode);
		}
		insertHistory(vObjectlocal);
		vObject.setRecordIndicator(Constants.STATUS_ZERO);
		vObject.setVerifier(intCurrentUserId);
		vObject.setDateCreation(vObjectlocal.getDateCreation());
		retVal = doUpdateApprHeader(vObject);
		if (retVal != Constants.SUCCESSFUL_OPERATION){
			exceptionCode = getResultObject(retVal);
			throw buildRuntimeCustomException(exceptionCode);
		} else {
			exceptionCode = getResultObject(Constants.SUCCESSFUL_OPERATION);
		}
		if (vObject.getFeesConfigDetaillst() != null && !vObject.getFeesConfigDetaillst().isEmpty()) {
			exceptionCode = feesConfigDetailsDao.deleteAndInsertApprFeeDetail(vObject);
		}
		String systemDate = getSystemDate();
		vObject.setDateLastModified(systemDate);
		
		writeAuditLog(vObject, vObjectlocal);
		if(exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION){
			exceptionCode = getResultObject(Constants.AUDIT_TRAIL_ERROR);
			throw buildRuntimeCustomException(exceptionCode);
		}
		exceptionCode =getResultObject(Constants.SUCCESSFUL_OPERATION);
		return exceptionCode;
	}
	@Override
	public ExceptionCode doUpdateRecordForNonTrans(FeesConfigHeaderVb vObject) throws RuntimeCustomException  {
		List<FeesConfigHeaderVb> collTemp = null;
		FeesConfigHeaderVb vObjectlocal = null;
		ExceptionCode exceptionCode = null;
		strApproveOperation =Constants.MODIFY;
		strErrorDesc  = "";
		strCurrentOperation = Constants.MODIFY;
		setServiceDefaults();
		vObject.setMaker(getIntCurrentUserId());
		collTemp = doSelectPendingRecord(vObject);
		if (collTemp == null){
			exceptionCode = getResultObject(Constants.ERRONEOUS_OPERATION);
			throw buildRuntimeCustomException(exceptionCode);
		}
		if (vObject.getFeesLineStatus() == Constants.PASSIVATE) {
			exceptionCode = getResultObject(Constants.CANNOT_MODIFY_TO_DELETE_STATE);
			throw buildRuntimeCustomException(exceptionCode);
		}
		if (!collTemp.isEmpty()) {
			collTemp.stream().forEach(header -> {
				header.setRefNo(getMaxSequence());
				doInsertionFeesHeadersHis(header);
			});

			vObjectlocal = ((ArrayList<FeesConfigHeaderVb>)collTemp).get(0);
			vObject.setDateCreation(vObjectlocal.getDateCreation());
			if (vObjectlocal.getRecordIndicator() == Constants.STATUS_INSERT){
				vObject.setVerifier(0);
				vObject.setRecordIndicator(Constants.STATUS_INSERT);
				retVal = doUpdatePendHeader(vObject);
			}else{
				vObject.setVerifier(0);
				vObject.setRecordIndicator(Constants.STATUS_UPDATE);
				retVal = doUpdatePendHeader(vObject);
			}
			if (retVal != Constants.SUCCESSFUL_OPERATION){
				exceptionCode = getResultObject(retVal);
				throw buildRuntimeCustomException(exceptionCode);
			}
			if (vObject.getFeesConfigDetaillst() != null && !vObject.getFeesConfigDetaillst().isEmpty()) {
				exceptionCode = feesConfigDetailsDao.deleteAndInsertPendFeeDetail(vObject);
			}
			if (exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION) {
				throw buildRuntimeCustomException(exceptionCode);
			}
			writeAuditLog(vObject, null);
			return getResultObject(Constants.SUCCESSFUL_OPERATION);
		}else {
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
			if (!collTemp.isEmpty()) {
				vObjectlocal = ((ArrayList<FeesConfigHeaderVb>) collTemp).get(0);
				vObject.setDateCreation(vObjectlocal.getDateCreation());
			}
		    vObject.setDateCreation(vObjectlocal.getDateCreation());
		 // Record is there in approved, but not in pending.  So add it to pending
		    vObject.setVerifier(0);
		    vObject.setRecordIndicator(Constants.STATUS_UPDATE);
		    retVal = doInsertionPendFeesHeadersDc(vObject);
			if (retVal != Constants.SUCCESSFUL_OPERATION){
				exceptionCode = getResultObject(retVal);
				throw buildRuntimeCustomException(exceptionCode);
			}
			if (vObject.getFeesConfigDetaillst() != null && !vObject.getFeesConfigDetaillst().isEmpty()) {
				exceptionCode = feesConfigDetailsDao.deleteAndInsertPendFeeDetail(vObject);
			}
			if (exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION) {
				throw buildRuntimeCustomException(exceptionCode);
			}
			writeAuditLog(vObject, null);
			return getResultObject(Constants.SUCCESSFUL_OPERATION);
		}
	}
	@Override
	@Transactional(rollbackForClassName={"com.vision.exception.RuntimeCustomException"})
	public ExceptionCode doRejectForTransaction(FeesConfigHeaderVb vObject)throws RuntimeCustomException {
		strErrorDesc  = "";
		strCurrentOperation = Constants.APPROVE;
		setServiceDefaults();
		return doRejectRecord(vObject);
	}
	@Override
	public ExceptionCode doRejectRecord(FeesConfigHeaderVb vObject)throws RuntimeCustomException {
		FeesConfigHeaderVb vObjectlocal = null;
		List<FeesConfigHeaderVb> collTemp = null;
		ExceptionCode exceptionCode = null;
		setServiceDefaults();
		strErrorDesc  = "";
		strCurrentOperation = Constants.REJECT;
		vObject.setMaker(getIntCurrentUserId());
		try {
			if(vObject.getRecordIndicator() == 1 || vObject.getRecordIndicator() == 3 )
			    vObject.setRecordIndicator(0);
			    else
				   vObject.setRecordIndicator(-1);
//			See if the transline or businessline is in deleted status
			if (!vObject.isFeesFlag()) {
				List<TransLineHeaderVb> collTempTL = null;
				List<BusinessLineHeaderVb> collTempBL = null;
				TransLineHeaderVb transLineHeaderVb = new TransLineHeaderVb();
				BusinessLineHeaderVb businessLineHeaderVb = new BusinessLineHeaderVb();
				transLineHeaderVb.setCountry(vObject.getCountry());
				transLineHeaderVb.setLeBook(vObject.getLeBook());
				transLineHeaderVb.setTransLineId(vObject.getTransLineId());
				collTempTL = getTransLineRecords(transLineHeaderVb);

				businessLineHeaderVb.setCountry(vObject.getCountry());
				businessLineHeaderVb.setLeBook(vObject.getLeBook());
				businessLineHeaderVb.setTransLineId(vObject.getTransLineId());
				businessLineHeaderVb.setBusinessLineId(vObject.getBusinessLineId());
				collTempBL = doSelectBusinessLineRecord(businessLineHeaderVb);
				if (collTempTL != null && collTempTL.size() > 0) {
					if (collTempTL.get(0).getTransLineStatus() == Constants.PASSIVATE) {
						exceptionCode = getResultObject(Constants.ERRONEOUS_OPERATION);
						exceptionCode.setErrorMsg("Trans Line [" + collTempTL.get(0).getTransLineId()
								+ "] is  Deleted - Kindly Active the Record");
						exceptionCode.setResponse(vObject);
						throw buildRuntimeCustomException(exceptionCode);
					}
				}
				if (collTempBL != null && collTempBL.size() > 0) {
					if (collTempBL.get(0).getBusinessLineStatus() == Constants.PASSIVATE) {
						exceptionCode = getResultObject(Constants.ERRONEOUS_OPERATION);
						exceptionCode.setErrorMsg("Business Line [" + collTempBL.get(0).getBusinessLineId()
								+ "] is  Deleted - Kindly Active the Record");
						exceptionCode.setResponse(vObject);
						throw buildRuntimeCustomException(exceptionCode);
					}
				}
			}
			// See if such a pending request exists in the pending table
			collTemp = doSelectPendingRecord(vObject);
			if (collTemp == null){
				exceptionCode = getResultObject(Constants.ERRONEOUS_OPERATION);
				throw buildRuntimeCustomException(exceptionCode);
			}
			if (collTemp.size() == 0){
				exceptionCode = getResultObject(Constants.NO_SUCH_PENDING_RECORD);
				throw buildRuntimeCustomException(exceptionCode);
			}
			vObjectlocal = ((ArrayList<FeesConfigHeaderVb>)collTemp).get(0);
			retVal = deleteFeesHeaderPend(vObjectlocal);
			if(retVal != Constants.SUCCESSFUL_OPERATION) {
				exceptionCode = getResultObject(Constants.ERRONEOUS_OPERATION);
				throw buildRuntimeCustomException(exceptionCode);
			}
			String feeType = vObjectlocal.getFeeType();
			List<FeesConfigDetailsVb> collTempDet = null;
			//collTempDet = feesConfigDetailsDao.getQueryDetails(vObject, 1);
			collTempDet = feesConfigDetailsDao.getQueryDetailsPend(vObjectlocal);
			if (collTempDet == null){
				exceptionCode = getResultObject(Constants.ERRONEOUS_OPERATION);
				throw buildRuntimeCustomException(exceptionCode);
			}
			if (collTempDet != null && !collTempDet.isEmpty()) {
				collTempDet.forEach(detailVb -> {
					feesConfigDetailsDao.deleteFeesDetailsPend(detailVb);
					if(!feeType.equalsIgnoreCase("F"))
						feesConfigTierDao.deleteFeesTierPend(detailVb);
				});
			}
			/*List<FeesConfigDetailsVb> collTempPendDet = feesConfigDetailsDao.getQueryDetailsPend(vObjectlocal);
			if(collTempPendDet == null || collTempPendDet.size() == 0) {
				
			}*/
			exceptionCode = getResultObject(Constants.SUCCESSFUL_OPERATION);
			writeAuditLog(vObject, null);
			return exceptionCode;
		}catch (UncategorizedSQLException uSQLEcxception) {
			strErrorDesc = parseErrorMsg(uSQLEcxception);
			exceptionCode = getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION);
			throw buildRuntimeCustomException(exceptionCode);
		}catch(Exception ex){
			logger.error("Error in Reject.",ex);
			strErrorDesc = ex.getMessage();
			exceptionCode = getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION);
			throw buildRuntimeCustomException(exceptionCode);
		}
	}
	@Override
	@Transactional(rollbackForClassName={"com.vision.exception.RuntimeCustomException"})
	public ExceptionCode doApproveForTransaction(FeesConfigHeaderVb vObject, boolean staticDelete) throws RuntimeCustomException {
		strErrorDesc  = "";
		strCurrentOperation = Constants.APPROVE;
		setServiceDefaults();
		return doApproveRecord(vObject,staticDelete);
	}
	@Override
	public ExceptionCode doApproveRecord(FeesConfigHeaderVb vObject, boolean staticDelete)
			throws RuntimeCustomException {
		FeesConfigHeaderVb oldContents = null;
		FeesConfigHeaderVb vObjectlocal = null;
		List<FeesConfigHeaderVb> collTemp = null;
		List<FeesConfigDetailsVb> collTempDet = null;
		List<FeesConfigDetailsVb> collTempDetAppr = null;
		List<FeesConfigTierVb> collTempTierAppr = null;
		List<TransLineHeaderVb> collTempTL = null;
		List<BusinessLineHeaderVb> collTempBL = null;
		ExceptionCode exceptionCode = new ExceptionCode();
		strCurrentOperation = Constants.APPROVE;
		setServiceDefaults();
		try {
			// See if such a pending request exists in the pending table
			vObject.setVerifier(getIntCurrentUserId());
			vObject.setRecordIndicator(Constants.STATUS_ZERO);

//			//When trying to approve business line record, Check Trans line is approved or not
			if (!vObject.isFeesFlag()) {
				TransLineHeaderVb transLineHeaderVb = new TransLineHeaderVb();
				BusinessLineHeaderVb businessLineHeaderVb = new BusinessLineHeaderVb();
				transLineHeaderVb.setCountry(vObject.getCountry());
				transLineHeaderVb.setLeBook(vObject.getLeBook());
				transLineHeaderVb.setTransLineId(vObject.getTransLineId());
				collTempTL = getTransLineRecords(transLineHeaderVb);

				businessLineHeaderVb.setCountry(vObject.getCountry());
				businessLineHeaderVb.setLeBook(vObject.getLeBook());
				businessLineHeaderVb.setTransLineId(vObject.getTransLineId());
				businessLineHeaderVb.setBusinessLineId(vObject.getBusinessLineId());
				collTempBL = doSelectBusinessLineRecord(businessLineHeaderVb);
				if (collTempTL != null && !collTempTL.isEmpty()) {
					if (collTempTL.get(0).getTransLineStatus() == Constants.PASSIVATE) {
						exceptionCode = getResultObject(Constants.ERRONEOUS_OPERATION);
						exceptionCode.setErrorMsg("Trans Line [" + collTempTL.get(0).getTransLineId()
								+ "] is  Deleted - Kindly Active the Record");
						exceptionCode.setResponse(vObject);
						throw buildRuntimeCustomException(exceptionCode);
					}
				}
				if (collTempBL != null && !collTempBL.isEmpty()) {
					if (collTempBL.get(0).getBusinessLineStatus() == Constants.PASSIVATE) {
						exceptionCode = getResultObject(Constants.ERRONEOUS_OPERATION);
						exceptionCode.setErrorMsg("Business Line [" + collTempBL.get(0).getBusinessLineId()
								+ "] is  Deleted - Kindly Active the Record");
						exceptionCode.setResponse(vObject);
						throw buildRuntimeCustomException(exceptionCode);
					}
				}
			}
			collTemp = doSelectPendingRecord(vObject);
			if (collTemp == null) {
				exceptionCode = getResultObject(Constants.ERRONEOUS_OPERATION);
				throw buildRuntimeCustomException(exceptionCode);
			}

			if (collTemp.size() == 0) {
				exceptionCode = getResultObject(Constants.NO_SUCH_PENDING_RECORD);
				throw buildRuntimeCustomException(exceptionCode);
			}

			vObjectlocal = ((ArrayList<FeesConfigHeaderVb>) collTemp).get(0);
			String feeType = vObjectlocal.getFeeType();
			String creationDate = vObjectlocal.getDateCreation();
			if (vObjectlocal.getMaker() == getIntCurrentUserId()) {
				exceptionCode = getResultObject(Constants.MAKER_CANNOT_APPROVE);
				throw buildRuntimeCustomException(exceptionCode);
			}

			collTempDet = feesConfigDetailsDao.getQueryDetails(vObjectlocal, 1);
			if (collTempDet != null && !collTempDet.isEmpty()) {
				collTempDet.forEach(feeDetailPend -> {
					feeDetailPend.setFeesTierlst(feesConfigTierDao.getQueryResults(feeDetailPend, 1));
				});
			}
			// If it's NOT addition, collect the existing record contents from the
			// Approved table and keep it aside, for writing audit information later.
			if (vObjectlocal.getRecordIndicator() != Constants.STATUS_INSERT) {
				insertHistory(vObject);
				collTemp = selectApprovedRecord(vObject);
				if (collTemp == null || collTemp.isEmpty()) {
					exceptionCode = getResultObject(Constants.ERRONEOUS_OPERATION);
					throw buildRuntimeCustomException(exceptionCode);
				}
				oldContents = ((ArrayList<FeesConfigHeaderVb>) collTemp).get(0);

				collTempDetAppr = feesConfigDetailsDao.getQueryDetails(vObjectlocal, 0);
				if (collTempDetAppr != null && !collTempDetAppr.isEmpty()) {
					collTempDetAppr.forEach(feeDetailAppr -> {
						feeDetailAppr.setFeesTierlst(feesConfigTierDao.getQueryResults(feeDetailAppr, 0));
					});
				}
			}
			if (vObjectlocal.getRecordIndicator() == Constants.STATUS_INSERT) { // Add authorization
				// Write the contents of the Pending table record to the Approved table
				vObjectlocal.setRecordIndicator(Constants.STATUS_ZERO);
				vObjectlocal.setVerifier(intCurrentUserId);
				retVal = doInsertionApprFeesHeaders(vObjectlocal);
				if (retVal != Constants.SUCCESSFUL_OPERATION) {
					exceptionCode = getResultObject(retVal);
					throw buildRuntimeCustomException(exceptionCode);
				}
				String systemDate = getSystemDate();
				vObject.setDateLastModified(systemDate);
				vObject.setDateCreation(systemDate);
				if (collTempDet != null && !collTempDet.isEmpty()) {
					for (FeesConfigDetailsVb feeDetailPend : collTempDet) {
						feeDetailPend.setDateCreation(creationDate);
						retVal = feesConfigDetailsDao.deleteFeesDetailsAppr(feeDetailPend);
						feeDetailPend.setRecordIndicator(Constants.STATUS_ZERO);// Approve
						feeDetailPend.setVerifier(intCurrentUserId);
						retVal = feesConfigDetailsDao.doInsertionApprFeesDetails(feeDetailPend);
						if (retVal != Constants.SUCCESSFUL_OPERATION) {
							exceptionCode = getResultObject(retVal);
							throw buildRuntimeCustomException(exceptionCode);
						}
						if (!feeType.equalsIgnoreCase("F")) {
							feesConfigTierDao.deleteFeesTierAppr(feeDetailPend);
							feesConfigTierDao.doInsertApprFeeTier(feeDetailPend);
						}
					}
				}
				strApproveOperation = Constants.ADD;
			} else if (vObjectlocal.getRecordIndicator() == Constants.STATUS_UPDATE) { // Modify authorization
				collTemp = selectApprovedRecord(vObject);
				String systemDate = getSystemDate();
				vObject.setDateLastModified(systemDate);
				if (collTemp == null || collTemp.isEmpty()) {
					exceptionCode = getResultObject(Constants.ERRONEOUS_OPERATION);
					throw buildRuntimeCustomException(exceptionCode);
				}
				// If record already exists in the approved table, reject the addition
				if (!collTemp.isEmpty()) {
					vObjectlocal.setVerifier(intCurrentUserId);
					vObjectlocal.setRecordIndicator(Constants.STATUS_ZERO);
					retVal = doUpdateApprHeader(vObjectlocal);
					if (retVal != Constants.SUCCESSFUL_OPERATION) {
						exceptionCode = getResultObject(retVal);
						throw buildRuntimeCustomException(exceptionCode);
					}
				}
				if (collTempDet != null && !collTempDet.isEmpty()) {
					for (FeesConfigDetailsVb feeDetailPend : collTempDet) {
						retVal = feesConfigDetailsDao.deleteFeesDetailsAppr(feeDetailPend);
						// if(!feeType.equalsIgnoreCase("F"))
						feesConfigTierDao.deleteFeesTierAppr(feeDetailPend);
						if (feeDetailPend.getRecordIndicator() != Constants.STATUS_DELETE) {
							feeDetailPend.setVerifier(intCurrentUserId);
							feeDetailPend.setRecordIndicator(Constants.STATUS_ZERO);// Approve
							feeDetailPend.setDateCreation(creationDate);
							retVal = feesConfigDetailsDao.doInsertionApprFeesDetails(feeDetailPend);
							if (retVal != Constants.SUCCESSFUL_OPERATION) {
								exceptionCode = getResultObject(retVal);
								throw buildRuntimeCustomException(exceptionCode);
							}
							if (!feeType.equalsIgnoreCase("F")) {
								feesConfigTierDao.doInsertApprFeeTier(feeDetailPend);
							}
						}
					}
				}

				// Set the current operation to write to audit log
				strApproveOperation = Constants.MODIFY;
			} else if (vObjectlocal.getRecordIndicator() == Constants.STATUS_DELETE) {
				if (staticDelete) {
					// Update the existing record status in the Approved table to delete
					setStatus(vObjectlocal, Constants.PASSIVATE);
					vObjectlocal.setRecordIndicator(Constants.STATUS_ZERO);
					vObjectlocal.setVerifier(intCurrentUserId);
					retVal = doUpdateApprHeader(vObjectlocal);
					if (retVal != Constants.SUCCESSFUL_OPERATION) {
						exceptionCode = getResultObject(retVal);
						throw buildRuntimeCustomException(exceptionCode);
					}
					feesConfigDetailsDao.doUpdateDetailStatus(vObjectlocal);
					feesConfigTierDao.doUpdateTierStatus(vObjectlocal);
					setStatus(vObject, Constants.PASSIVATE);
					String systemDate = getSystemDate();
					vObject.setDateLastModified(systemDate);
				} else {
					// Delete the existing record from the Approved table
					retVal = deleteFeesHeaderAppr(vObjectlocal);
					if (retVal != Constants.SUCCESSFUL_OPERATION) {
						exceptionCode = getResultObject(retVal);
						throw buildRuntimeCustomException(exceptionCode);
					}
					retVal = feesConfigDetailsDao.deleteFeesDetailsApprMain(vObjectlocal);
					retVal = feesConfigTierDao.deleteFeesTierApprMain(vObjectlocal);
					String systemDate = getSystemDate();
					vObject.setDateLastModified(systemDate);
				}
			} else {
				exceptionCode = getResultObject(Constants.INVALID_STATUS_FLAG_IN_DATABASE);
				throw buildRuntimeCustomException(exceptionCode);
			}
			// Delete the record from the Pending table
			retVal = deleteFeesHeaderPend(vObjectlocal);
			if (retVal != Constants.SUCCESSFUL_OPERATION) {
				exceptionCode = getResultObject(retVal);
				throw buildRuntimeCustomException(exceptionCode);
			}
			retVal = feesConfigTierDao.deleteFeesTierPendMain(vObjectlocal);
			retVal = feesConfigDetailsDao.deleteFeesDetailsPendMain(vObjectlocal);
			vObject.setInternalStatus(0);
			vObject.setRecordIndicator(Constants.STATUS_ZERO);
			if (vObjectlocal.getRecordIndicator() == Constants.STATUS_DELETE && !staticDelete) {
				writeAuditLog(null, oldContents);
				vObject.setRecordIndicator(-1);
			} else
				writeAuditLog(vObjectlocal, oldContents);

			return getResultObject(Constants.SUCCESSFUL_OPERATION);
		} catch (UncategorizedSQLException uSQLEcxception) {
			strErrorDesc = parseErrorMsg(uSQLEcxception);
			exceptionCode = getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION);
			throw buildRuntimeCustomException(exceptionCode);
		} catch (Exception ex) {
			logger.error("Error in Approve.", ex);
//			//logger.error( ((vObject==null)? "vObject is Null":vObject.toString()));
			strErrorDesc = ex.getMessage();
			exceptionCode = getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION);
			throw buildRuntimeCustomException(exceptionCode);
		}
	}
	@Override
	protected String getAuditString(FeesConfigHeaderVb vObject){
		final String auditDelimiter = vObject.getAuditDelimiter();
		final String auditDelimiterColVal = vObject.getAuditDelimiterColVal();
		StringBuffer strAudit = new StringBuffer("");
		try
		{
			if(ValidationUtil.isValid(vObject.getCountry()))
				strAudit.append("COUNTRY"+auditDelimiterColVal+vObject.getCountry().trim());
			else
				strAudit.append("COUNTRY"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);

			if(ValidationUtil.isValid(vObject.getLeBook()))
				strAudit.append("LE_BOOK"+auditDelimiterColVal+vObject.getLeBook().trim());
			else
				strAudit.append("LE_BOOK"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			
			if(ValidationUtil.isValid(vObject.getBusinessLineId()))
				strAudit.append("BUSINESS_LINE_ID"+auditDelimiterColVal+vObject.getBusinessLineId().trim());
			else
				strAudit.append("BUSINESS_LINE_ID"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			
			if(ValidationUtil.isValid(vObject.getEffectiveDate()))
				strAudit.append("EFFECTIVE_DATE"+auditDelimiterColVal+vObject.getEffectiveDate().trim());
			else
				strAudit.append("EFFECTIVE_DATE"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			
			if(ValidationUtil.isValid(vObject.getFeeBasis()))
				strAudit.append("FEE_BASIS"+auditDelimiterColVal+vObject.getFeeBasis().trim());
			else
				strAudit.append("FEE_BASIS"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			
			if(ValidationUtil.isValid(vObject.getFeeType()))
				strAudit.append("FEE_TYPE"+auditDelimiterColVal+vObject.getFeeType().trim());
			else
				strAudit.append("FEE_TYPE"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			
			if(ValidationUtil.isValid(vObject.getTierType()))
				strAudit.append("TIER_TYPE"+auditDelimiterColVal+vObject.getTierType().trim());
			else
				strAudit.append("TIER_TYPE"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			
			strAudit.append("FEE_LINE_STATUS_NT"+auditDelimiterColVal+vObject.getFeesLineStatusNt());
			strAudit.append(auditDelimiter);
			
			strAudit.append("FEE_LINE_STATUS"+auditDelimiterColVal+vObject.getFeesLineStatus());
			strAudit.append(auditDelimiter);
						
			strAudit.append("RECORD_INDICATOR_NT"+auditDelimiterColVal+vObject.getRecordIndicatorNt());
			strAudit.append(auditDelimiter);
			if(vObject.getRecordIndicator() == -1)
				vObject.setRecordIndicator(0);
			strAudit.append("RECORD_INDICATOR"+auditDelimiterColVal+vObject.getRecordIndicator());
			strAudit.append(auditDelimiter);
			strAudit.append("MAKER"+auditDelimiterColVal+vObject.getMaker());
			strAudit.append(auditDelimiter);
			strAudit.append("VERIFIER"+auditDelimiterColVal+vObject.getVerifier());
			strAudit.append(auditDelimiter);
			
			if(vObject.getDateLastModified() != null && !vObject.getDateLastModified().equalsIgnoreCase(""))
				strAudit.append("DATE_LAST_MODIFIED"+auditDelimiterColVal+vObject.getDateLastModified().trim());
			else
				strAudit.append("DATE_LAST_MODIFIED"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			
			if(vObject.getDateCreation() != null && !vObject.getDateCreation().equalsIgnoreCase(""))
				strAudit.append("DATE_CREATION"+auditDelimiterColVal+vObject.getDateCreation().trim());
			else
				strAudit.append("DATE_CREATION"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			

		}
		catch(Exception ex)
		{
			strErrorDesc = ex.getMessage();
			strAudit = strAudit.append(strErrorDesc);
			ex.printStackTrace();
		}
		return strAudit.toString();
	}
	public int effectiveDateCheck(FeesConfigHeaderVb dObj,int status){
		int cnt = 0;
		String query= "";
		try {
			if(status == 0) {
				query = " SELECT COUNT(1) FROM RA_MST_FEES_HEADER TAPPR "+
						" WHERE TAPPR.COUNTRY = ? AND TAPPR.LE_BOOK = ? AND "+
						" TAPPR.BUSINESS_LINE_ID = ?  AND TAPPR.EFFECTIVE_DATE = ? ";
			}else {
				query = " SELECT COUNT(1) FROM RA_MST_FEES_HEADER_PEND TAPPR "+
						" WHERE TAPPR.COUNTRY = ? AND TAPPR.LE_BOOK = ? AND  "+
						" TAPPR.BUSINESS_LINE_ID = ?  AND TAPPR.EFFECTIVE_DATE = ? ";
			}
			
			
			Object objParams[] = new Object[4];
			objParams[0] = new String(dObj.getCountry());// country
			objParams[1] = new String(dObj.getLeBook());
			objParams[2] = new String(dObj.getBusinessLineId());
			//objParams[4] = new String(dObj.getFeeConfigType());
			//objParams[5] = new String(dObj.getFeeConfigCode());
			objParams[3] = new String(dObj.getEffectiveDate());
			cnt = getJdbcTemplate().queryForObject(query, objParams,Integer.class);
			return cnt;
		}catch(Exception e) {
			return cnt;
		}
	}
	public int effectiveDateBusinessCheck(FeesConfigHeaderVb dObj){
		int cnt = 0;
		String query= "";
		try {
				//query = " SELECT CASE WHEN ? >= BUSINESS_DATE THEN 1 ELSE 0 END EFFECTIVE_DATE FROM VISION_BUSINESS_DAY WHERE COUNTRY = ? AND LE_BOOK = ? ";
			query = " SELECT CASE WHEN ? >= REPORT_BUSINESS_DATE THEN 1 ELSE 0 END EFFECTIVE_DATE FROM VISION_BUSINESS_DAY WHERE COUNTRY = ? AND LE_BOOK = ? ";
			
			Object objParams[] = new Object[3];
			objParams[0] = new String(dObj.getEffectiveDate());// country
			objParams[1] = new String(dObj.getCountry());
			objParams[2] = new String(dObj.getLeBook());
			cnt = getJdbcTemplate().queryForObject(query, objParams,Integer.class);
			return cnt;
		}catch(Exception e) {
			return cnt;
		}
	}
	public ExceptionCode callLineMergeProc(String country,String leBook,String businessLineId,String procedureId){
		ExceptionCode exceptionCode = new ExceptionCode();
		setServiceDefaults();
		strCurrentOperation = "Query";
		strErrorDesc = "";
		Connection con = null;
		CallableStatement cs =  null;
		try{
			con = getConnection();
			cs = con.prepareCall("{call "+procedureId+"(?,?,?,?,?)}");
			cs.setString(1, country);
	        cs.setString(2, leBook);
	        cs.setString(3, businessLineId);
        	cs.registerOutParameter(4, java.sql.Types.VARCHAR); //Status
	        cs.registerOutParameter(5, java.sql.Types.VARCHAR); //Error Message
	        cs.execute();
	        exceptionCode.setErrorCode(Integer.parseInt(cs.getString(4)));
	        exceptionCode.setErrorMsg(cs.getString(5));
	        if(exceptionCode.getErrorCode() != 0) {
	        	logger.error("Error on Line Merge["+exceptionCode.getErrorMsg()+"]");
	        }
            cs.close();
		}catch(Exception ex){
//			ex.printStackTrace();
			strErrorDesc = ex.getMessage().trim();
		}finally{
			JdbcUtils.closeStatement(cs);
			DataSourceUtils.releaseConnection(con, getDataSource());
		}
		return exceptionCode;
	}
	
	
	public List<BusinessLineHeaderVb> doSelectPendingBusinessLineRecord(BusinessLineHeaderVb dObj) {
		List<BusinessLineHeaderVb> collTemp = null;
		final int intKeyFieldsCount = 4;
		setServiceDefaults();
		String strQueryPend = null;
		strQueryPend = new String("SELECT DISTINCT TPEND.COUNTRY, TPEND.LE_BOOK, TPEND.BUSINESS_LINE_ID,TPEND.BUSINESS_LINE_DESCRIPTION,TPEND.TRANS_LINE_TYPE,   " + 
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
				" FROM RA_MST_BUSINESS_LINE_HEADER_PEND TPEND, NUM_SUB_TAB T1,NUM_SUB_TAB T3,RA_MST_TRANS_LINE_HEADER T2 ,RA_MST_TRANS_LINE_HEADER_PEND T4 "+
				" WHERE TPEND.COUNTRY =? AND TPEND.LE_BOOK =? AND TPEND.BUSINESS_LINE_ID = ? AND TPEND.TRANS_LINE_ID = ? AND  "+
				" T1.NUM_tab = TPEND.RECORD_INDICATOR_NT" + 
				" and T1.NUM_sub_tab = TPEND.RECORD_INDICATOR"+
				" AND (TPEND.TRANS_LINE_ID = T2.TRANS_LINE_ID OR TPEND.TRANS_LINE_ID = T4.TRANS_LINE_ID)  and T3.NUM_tab = TPEND.BUSINESS_LINE_STATUS_NT and T3.NUM_sub_tab = TPEND.BUSINESS_LINE_STATUS");
		Object objParams[] = new Object[intKeyFieldsCount];
		objParams[0] = new String(dObj.getCountry());// country
		objParams[1] = new String(dObj.getLeBook());
		objParams[2] = new String(dObj.getBusinessLineId());
		objParams[3] = new String(dObj.getTransLineId());
		try {
			logger.info("Executing pending query");
			collTemp = getJdbcTemplate().query(strQueryPend.toString(),objParams,getDetailMapper1());
			return collTemp;
		}catch(Exception ex){
			ex.printStackTrace();
			logger.error("Error: getQueryResults Exception :   ");
			/*//logger.error(((strQueryPend == null) ? "strQueryPend is Null" : strQueryPend.toString()));

			if (objParams != null)
				for(int i=0 ; i< objParams.length; i++)
					//logger.error("objParams[" + i + "]" + objParams[i].toString());*/
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
	public List<TransLineHeaderVb> doselectPendingproductRecord(TransLineHeaderVb dObj) {
		List<TransLineHeaderVb> collTemp = null;
		final int intKeyFieldsCount = 3;
		String strQueryPend = null;
		strQueryPend = new String(
				"SELECT TPend.COUNTRY, TPend.LE_BOOK, TPend.TRANS_LINE_ID,TPend.TRANS_LINE_DESCRIPTION,A1.ALPHA_SUBTAB_DESCRIPTION,TPend.TRANS_LINE_TYPE,  "
						+ "TPend.TRANS_LINE_PROD_SUB_TYPE,TPend.TRANS_LINE_PROD_GRP,TPend.EXTRACTION_FREQUENCY,EXTRACTION_MONTH_DAY, "
						+ "TARGET_STG_TABLE_ID,TPend.TRANS_LINE_STATUS,TPend.RECORD_INDICATOR,T1.NUM_SUBTAB_DESCRIPTION RECORD_INDICATOR_DESC, "
						+ "TPend. MAKER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "
						+ getDbFunction("NVL") + "(TPend.MAKER,0) ) MAKER_NAME,"
						+ "TPend. VERIFIER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "
						+ getDbFunction("NVL") + "(TPend.VERIFIER,0) ) VERIFIER_NAME, " + " "
						+ getDbFunction("DATEFUNC") + "(TPend.DATE_LAST_MODIFIED, 'dd-MM-yyyy " + getDbFunction("TIME")
						+ "') DATE_LAST_MODIFIED ," + getDbFunction("DATEFUNC") + "(TPend.DATE_CREATION, 'dd-MM-yyyy "
						+ getDbFunction("TIME") + "') DATE_CREATION "
						+ " FROM RA_MST_TRANS_LINE_HEADER_PEND TPend, NUM_SUB_TAB T1,ALPHA_SUB_TAB A1 WHERE "
						+ "TPend.COUNTRY =? AND TPend.LE_BOOK =? AND TPend.TRANS_LINE_ID = ? AND  T1.NUM_tab = TPend.RECORD_INDICATOR_NT"
						+ " and T1.NUM_sub_tab = TPend.RECORD_INDICATOR and TPend.TRANS_LINE_TYPE=A1.ALPHA_SUB_TAB  and A1.alpha_tab=TPend.TRANS_LINE_TYPE_AT");
		Object objParams[] = new Object[intKeyFieldsCount];
		objParams[0] = new String(dObj.getCountry());// country
		objParams[1] = new String(dObj.getLeBook());
		objParams[2] = new String(dObj.getTransLineId());
		try {
			collTemp = getJdbcTemplate().query(strQueryPend.toString(), objParams, getDetailMapperTrans());
			return collTemp;
		} catch (Exception ex) {
//		ex.printStackTrace();
			return null;
		}

	}

	public List<TransLineHeaderVb> getTransLineRecords(TransLineHeaderVb dObj) {
		List<TransLineHeaderVb> collTemp = null;
		final int intKeyFieldsCount = 3;
		String strQueryPend = null;
		strQueryPend = new String(
				"SELECT TAppr.COUNTRY, TAppr.LE_BOOK, TAppr.TRANS_LINE_ID,TAppr.TRANS_LINE_DESCRIPTION,A1.ALPHA_SUBTAB_DESCRIPTION,TAppr.TRANS_LINE_TYPE,  "
						+ "TAppr.TRANS_LINE_PROD_SUB_TYPE,TAppr.TRANS_LINE_PROD_GRP,TAppr.EXTRACTION_FREQUENCY,EXTRACTION_MONTH_DAY, "
						+ "TARGET_STG_TABLE_ID,TAppr.TRANS_LINE_STATUS,TAppr.RECORD_INDICATOR,T1.NUM_SUBTAB_DESCRIPTION RECORD_INDICATOR_DESC, "
						+ "TAppr. MAKER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "
						+ getDbFunction("NVL") + "(TAppr.MAKER,0) ) MAKER_NAME,"
						+ "TAppr. VERIFIER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "
						+ getDbFunction("NVL") + "(TAppr.VERIFIER,0) ) VERIFIER_NAME, " + " "
						+ getDbFunction("DATEFUNC") + "(TAppr.DATE_LAST_MODIFIED, 'dd-MM-yyyy " + getDbFunction("TIME")
						+ "') DATE_LAST_MODIFIED ," + getDbFunction("DATEFUNC") + "(TAppr.DATE_CREATION, 'dd-MM-yyyy "
						+ getDbFunction("TIME") + "') DATE_CREATION "
						+ " FROM RA_MST_TRANS_LINE_HEADER TAppr, NUM_SUB_TAB T1,ALPHA_SUB_TAB A1 WHERE "
						+ "TAppr.COUNTRY =? AND TAppr.LE_BOOK =? AND TAppr.TRANS_LINE_ID = ? AND  T1.NUM_tab = TAppr.RECORD_INDICATOR_NT"
						+ " and T1.NUM_sub_tab = TAppr.RECORD_INDICATOR and TAppr.TRANS_LINE_TYPE=A1.ALPHA_SUB_TAB  and A1.alpha_tab=TAppr.TRANS_LINE_TYPE_AT");
		Object objParams[] = new Object[intKeyFieldsCount];
		objParams[0] = new String(dObj.getCountry());// country
		objParams[1] = new String(dObj.getLeBook());
		objParams[2] = new String(dObj.getTransLineId());
		try {
			collTemp = getJdbcTemplate().query(strQueryPend.toString(), objParams, getDetailMapperTrans());
			return collTemp;
		} catch (Exception ex) {
//		ex.printStackTrace();
			return null;
		}

	}

	@Override
	protected ExceptionCode doDeleteRecordForNonTrans(FeesConfigHeaderVb vObject) throws RuntimeCustomException {
		FeesConfigHeaderVb vObjectlocal = null;
		List<FeesConfigHeaderVb> collTemp = null;
		List<FeesConfigHeaderVb> collTempAppr = null;
		ExceptionCode exceptionCode = null;
		strApproveOperation = Constants.DELETE;
		strErrorDesc = "";
		strCurrentOperation = Constants.DELETE;
		setServiceDefaults();
		collTempAppr = selectApprovedRecord(vObject);

		if (collTempAppr == null) {
			logger.error("Collection is null");
			exceptionCode = getResultObject(Constants.ERRONEOUS_OPERATION);
			throw buildRuntimeCustomException(exceptionCode);
		}
		// If record already exists in the approved table, reject the addition
		if (ValidationUtil.isValid(vObject.getEffectiveDate()) && collTempAppr.size() == 0) {
			exceptionCode = getResultObject(Constants.ATTEMPT_TO_DELETE_UNEXISTING_RECORD);
			throw buildRuntimeCustomException(exceptionCode);
		}
		// check to see if the record already exists in the pending table
		collTemp = doSelectPendingRecord(vObject);
		if (collTemp == null) {
			exceptionCode = getResultObject(Constants.ERRONEOUS_OPERATION);
			throw buildRuntimeCustomException(exceptionCode);
		}

		// If records are there, check for the status and decide what error to return
		// back
		if (!collTemp.isEmpty()) {
			exceptionCode = getResultObject(Constants.TRYING_TO_DELETE_APPROVAL_PENDING_RECORD);
			throw buildRuntimeCustomException(exceptionCode);
		}
		if (!collTempAppr.isEmpty()) {
//			for(FeesConfigHeaderVb dObj : collTempAppr) {
			int intStaticDeletionFlag = getStatus(collTempAppr.get(0));
			if (intStaticDeletionFlag == Constants.PASSIVATE) {
				exceptionCode = getResultObject(Constants.CANNOT_DELETE_AN_INACTIVE_RECORD);
				throw buildRuntimeCustomException(exceptionCode);
			}

			FeesConfigHeaderVb feesConfigHeaderVb = (collTempAppr.get(0));
			feesConfigHeaderVb.setMaker(getIntCurrentUserId());
			feesConfigHeaderVb.setVerifier(getIntCurrentUserId());
			feesConfigHeaderVb.setRecordIndicator(Constants.STATUS_DELETE);
			feesConfigHeaderVb.setVerifier(0);
			retVal = doInsertionPendFeesHeadersDc(feesConfigHeaderVb);
			if (retVal != Constants.SUCCESSFUL_OPERATION) {
				exceptionCode = getResultObject(retVal);
				throw buildRuntimeCustomException(exceptionCode);
			}
//			}
		}
		// vObjectlocal.setDateCreation(vObject.getDateCreation());
		vObject.setRecordIndicator(Constants.STATUS_DELETE);
		vObject.setVerifier(0);
		return getResultObject(Constants.SUCCESSFUL_OPERATION);
	}
	@Override
	protected ExceptionCode doDeleteApprRecordForNonTrans(FeesConfigHeaderVb vObject) throws RuntimeCustomException {
		List<FeesConfigHeaderVb> collTemp = null;
		ExceptionCode exceptionCode = null;
		strApproveOperation = Constants.DELETE;
		strErrorDesc  = "";
		strCurrentOperation = Constants.DELETE;
		setServiceDefaults();
		FeesConfigHeaderVb vObjectlocal = new FeesConfigHeaderVb();
		vObject.setMaker(getIntCurrentUserId());
		if("RUNNING".equalsIgnoreCase(getBuildStatus(vObject))){
			exceptionCode = getResultObject(Constants.BUILD_IS_RUNNING);
			throw buildRuntimeCustomException(exceptionCode);
		}
		collTemp = selectApprovedRecord(vObject);
		if (collTemp == null){
			exceptionCode = getResultObject(Constants.ERRONEOUS_OPERATION);
			throw buildRuntimeCustomException(exceptionCode);
		}
		// If record already exists in the approved table, reject the addition
		if (!collTemp.isEmpty()) {
			vObjectlocal = collTemp.get(0);
//			for(FeesConfigHeaderVb dObj : collTemp) {
			insertHistory(collTemp.get(0));
			int intStaticDeletionFlag = getStatus(collTemp.get(0));
			if (intStaticDeletionFlag == Constants.PASSIVATE) {
				exceptionCode = getResultObject(Constants.CANNOT_DELETE_AN_INACTIVE_RECORD);
				throw buildRuntimeCustomException(exceptionCode);
			}
			if (vObject.isStaticDelete()) {
				FeesConfigHeaderVb feesConfigHeaderVb = (collTemp.get(0));
				feesConfigHeaderVb.setMaker(getIntCurrentUserId());
				feesConfigHeaderVb.setVerifier(getIntCurrentUserId());
				feesConfigHeaderVb.setRecordIndicator(Constants.STATUS_ZERO);
				setStatus(feesConfigHeaderVb, Constants.PASSIVATE);
				retVal = doUpdateApprHeader(feesConfigHeaderVb);
				if (retVal != Constants.SUCCESSFUL_OPERATION) {
					exceptionCode = getResultObject(retVal);
					throw buildRuntimeCustomException(exceptionCode);
				}
				retVal = feesConfigDetailsDao.doUpdateDetailStatus(feesConfigHeaderVb);
				if (retVal == Constants.ERRONEOUS_OPERATION) {
					exceptionCode = getResultObject(retVal);
					throw buildRuntimeCustomException(exceptionCode);
				}
				if (!"F".equalsIgnoreCase(feesConfigHeaderVb.getFeeType())) {
					retVal = feesConfigTierDao.doUpdateTierStatus(feesConfigHeaderVb);
					if (retVal == Constants.ERRONEOUS_OPERATION) {
						exceptionCode = getResultObject(retVal);
						throw buildRuntimeCustomException(exceptionCode);
					}
				}
				String systemDate = getSystemDate();
				vObject.setDateLastModified(systemDate);
			} else {
				retVal = deleteFeesHeaderAppr(vObject);
				if (retVal != Constants.SUCCESSFUL_OPERATION) {
					exceptionCode = getResultObject(retVal);
					throw buildRuntimeCustomException(exceptionCode);
				}
				String systemDate = getSystemDate();
				vObject.setDateLastModified(systemDate);
				retVal = feesConfigDetailsDao.deleteFeesDetailsApprMain(vObject);
				if (retVal == Constants.ERRONEOUS_OPERATION) {
					exceptionCode = getResultObject(retVal);
					throw buildRuntimeCustomException(exceptionCode);
				}
				if (!"F".equalsIgnoreCase(vObject.getTierType())) {
					retVal = feesConfigTierDao.deleteFeesTierApprMain(vObject);
					if (retVal == Constants.ERRONEOUS_OPERATION) {
						exceptionCode = getResultObject(retVal);
						throw buildRuntimeCustomException(exceptionCode);
					}
				}
			}
//			}
		} else {
			exceptionCode = getResultObject(Constants.ATTEMPT_TO_DELETE_UNEXISTING_RECORD);
			throw buildRuntimeCustomException(exceptionCode);
		}
		vObject.setDateCreation(vObjectlocal.getDateCreation());
		if(vObject.isStaticDelete()){
			setStatus(vObjectlocal, Constants.STATUS_ZERO);
			setStatus(vObject, Constants.PASSIVATE);
			exceptionCode = writeAuditLog(vObject,vObjectlocal);
		}else{
			exceptionCode = writeAuditLog(null,vObject);
			vObject.setRecordIndicator(-1);
		}
		if(exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION){
			exceptionCode = getResultObject(Constants.AUDIT_TRAIL_ERROR);
			throw buildRuntimeCustomException(exceptionCode);
		}
		return exceptionCode;
	}
	private int getMaxSequence(){
		StringBuffer strBufApprove = new StringBuffer("Select MAX(TAppr.REF_NO) From RA_MST_FEES_HEADER_HIS TAppr ");
		try{
			int max = getJdbcTemplate().queryForObject(strBufApprove.toString(), Integer.class);
			if(max <= 0)
				max = 1;
			else
				max++;
			return max;
		}catch(Exception ex){
//			ex.printStackTrace();
			return 1;
		}
	}
	public void insertHistory(FeesConfigHeaderVb dObj) {
		try {
			// HISTORY CREATION
			dObj.setRefNo(getMaxSequence());
			doInsertionFeesHeadersHis(dObj);
			List<FeesConfigDetailsVb> collTempFeeDet = null;
			int refNo = feesConfigDetailsDao.getMaxSequence();
			collTempFeeDet = feesConfigDetailsDao.getQueryDetailsMain(dObj);
			if (collTempFeeDet != null && !collTempFeeDet.isEmpty()) {
				for (FeesConfigDetailsVb feesConfigDetailsVb : collTempFeeDet) {
					feesConfigDetailsVb.setRefNo(refNo);
					feesConfigDetailsDao.doInsertionFeesDetailsHis(feesConfigDetailsVb);
					List<FeesConfigTierVb> collTempTier = feesConfigTierDao.getQueryResultsMain(feesConfigDetailsVb, 0);
					if (collTempTier != null && !collTempTier.isEmpty()) {
						for (FeesConfigTierVb feesConfigTierVb : collTempTier) {
							feesConfigTierVb.setRefNo(refNo);
							feesConfigTierDao.doInsertionFeesTierHis(feesConfigTierVb);
						}
					}
				}
			}
			
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}

	public List<BusinessLineHeaderVb> doSelectBusinessLineRecord(BusinessLineHeaderVb dObj) {
		List<BusinessLineHeaderVb> collTemp = null;
		final int intKeyFieldsCount = 4;
		setServiceDefaults();
		String strQueryPend = null;
		strQueryPend = new String(
				"SELECT DISTINCT TAppr.COUNTRY, TAppr.LE_BOOK, TAppr.BUSINESS_LINE_ID,TAppr.BUSINESS_LINE_DESCRIPTION,TAppr.TRANS_LINE_TYPE,   "
						+ " TAppr.TRANS_LINE_ID,"
						+ " CASE WHEN TAppr.TRANS_LINE_TYPE='P' THEN T2.TRANS_LINE_PROD_GRP ELSE T2.TRANS_LINE_SERV_GRP END TRAN_LINE_GRP, "
						+ " CASE WHEN TAppr.TRANS_LINE_TYPE='P' THEN "
						+ " (SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB =T2.TRANS_LINE_PROD_GRP_AT AND ALPHA_SUB_TAB= T2.TRANS_LINE_PROD_GRP) "
						+ "  ELSE "
						+ "  (SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB =T2.TRANS_LINE_SERV_GRP_AT AND ALPHA_SUB_TAB= T2.TRANS_LINE_SERV_GRP) "
						+ "  END TRAN_LINE_GRP_DESC, "
						+ " TAppr.BUSINESS_LINE_TYPE,(SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB =TAppr.BUSINESS_LINE_TYPE_AT AND ALPHA_SUB_TAB= TAppr.BUSINESS_LINE_TYPE) BUSINESS_LINE_TYPE_DESC, "
						+ "TAppr.IE_TYPE, TAppr.ACTUAL_IE_POSTING, TAppr.ACTUAL_IE_MATCH_RULE ,  "
						+ " TAppr.BUSINESS_LINE_STATUS,T3.NUM_SUBTAB_DESCRIPTION BUSINESS_LINE_STATUS_DESC,TAppr.RECORD_INDICATOR,T1.NUM_SUBTAB_DESCRIPTION RECORD_INDICATOR_DESC,   "
						+ " TAppr. MAKER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "
						+ getDbFunction("NVL") + "(TAppr.MAKER,0) ) MAKER_NAME,   "
						+ " TAppr.VERIFIER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "
						+ getDbFunction("NVL") + "(TAppr.VERIFIER,0) ) VERIFIER_NAME,   " + " "
						+ getDbFunction("DATEFUNC") + "(TAppr.DATE_LAST_MODIFIED, 'dd-MM-yyyy " + getDbFunction("TIME")
						+ "') DATE_LAST_MODIFIED ," + getDbFunction("DATEFUNC") + "(TAppr.DATE_CREATION, 'dd-MM-yyyy "
						+ getDbFunction("TIME") + "') DATE_CREATION "
						+ " FROM RA_MST_BUSINESS_LINE_HEADER TAppr, NUM_SUB_TAB T1,NUM_SUB_TAB T3,RA_MST_TRANS_LINE_HEADER T2 ,RA_MST_TRANS_LINE_HEADER_PEND T4 "
						+ " WHERE TAppr.COUNTRY =? AND TAppr.LE_BOOK =? AND TAppr.BUSINESS_LINE_ID = ? AND TAppr.TRANS_LINE_ID = ? AND  "
						+ " T1.NUM_tab = TAppr.RECORD_INDICATOR_NT" + " and T1.NUM_sub_tab = TAppr.RECORD_INDICATOR"
						+ " AND (TAppr.TRANS_LINE_ID = T2.TRANS_LINE_ID OR TAppr.TRANS_LINE_ID = T4.TRANS_LINE_ID)  and T3.NUM_tab = TAppr.BUSINESS_LINE_STATUS_NT and T3.NUM_sub_tab = TAppr.BUSINESS_LINE_STATUS");
		Object objParams[] = new Object[intKeyFieldsCount];
		objParams[0] = new String(dObj.getCountry());// country
		objParams[1] = new String(dObj.getLeBook());
		objParams[2] = new String(dObj.getBusinessLineId());
		objParams[3] = new String(dObj.getTransLineId());
		try {
			logger.info("Executing pending query");
			collTemp = getJdbcTemplate().query(strQueryPend.toString(), objParams, getDetailMapper1());
			return collTemp;
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("Error: getQueryResults Exception :   ");
			/*
			 * //logger.error(((strQueryPend == null) ? "strQueryPend is Null" :
			 * strQueryPend.toString()));
			 * 
			 * if (objParams != null) for(int i=0 ; i< objParams.length; i++)
			 * //logger.error("objParams[" + i + "]" + objParams[i].toString());
			 */
			return null;
		}
	}

	public FeesConfigDetailsDao getFeesConfigDetailsDao() {
		return feesConfigDetailsDao;
	}

	public void setFeesConfigDetailsDao(FeesConfigDetailsDao feesConfigDetailsDao) {
		this.feesConfigDetailsDao = feesConfigDetailsDao;
	}

	public FeesConfigTierDao getFeesConfigTierDao() {
		return feesConfigTierDao;
	}

	public void setFeesConfigTierDao(FeesConfigTierDao feesConfigTierDao) {
		this.feesConfigTierDao = feesConfigTierDao;
	}
}