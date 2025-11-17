/**
 * 
 */
package com.vision.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.vision.authentication.CustomContextHolder;
import com.vision.util.CommonUtils;
import com.vision.util.Constants;
import com.vision.util.ValidationUtil;
import com.vision.vb.NumSubTabVb;
import com.vision.vb.SmartSearchVb;
import com.vision.vb.TabVb;

@Component
public class NumSubTabDao extends AbstractDao<NumSubTabVb> {
	
	@SuppressWarnings("unchecked")
	public List<NumSubTabVb> findActiveNumSubTabsByNumTab(int pNumTab) throws DataAccessException {
		String sql = "SELECT * FROM NUM_SUB_TAB WHERE NUM_SUBTAB_STATUS = 0 AND NUM_TAB = ? ORDER BY NUM_SUB_TAB";
		Object[] lParams = new Object[1];
		lParams[0] = pNumTab;
		return getJdbcTemplate().query(sql, lParams, getMapper());
	}
	
	public List findActiveNumSubTabsByNumTabCols(int pNumTab, String cols) throws DataAccessException {
		if(!ValidationUtil.isValid(cols))
			cols="*";

		String sql = "SELECT "+cols+" FROM NUM_SUB_TAB WHERE NUM_SUBTAB_STATUS = 0 AND NUM_TAB = ? ORDER BY NUM_SUB_TAB";	
		Object[] lParams = new Object[1];
		lParams[0] = pNumTab;
		return  jdbcTemplate.queryForList(sql,lParams);
	}
	
	@SuppressWarnings("unchecked")
	public List<NumSubTabVb> findNumSubTabsByNumSubTabs(int pNumTab, int numSubTab) throws DataAccessException {
		String sql = "SELECT * FROM NUM_SUB_TAB WHERE NUM_SUBTAB_STATUS = 0 AND NUM_TAB = ? and NUM_SUB_TAB ! = "+numSubTab+" ORDER BY NUM_SUB_TAB";
		Object[] lParams = new Object[1];
		lParams[0] = pNumTab;
		return getJdbcTemplate().query(sql, lParams, getMapper());
	}	
	@SuppressWarnings("unchecked")
	public List<NumSubTabVb> findNumSubTabsByNumTab(int pNumTab) throws DataAccessException {
		String sql = "SELECT * FROM NUM_SUB_TAB WHERE NUM_TAB = ? ORDER BY NUM_SUB_TAB";
		Object[] lParams = new Object[1];
		lParams[0] = pNumTab;
		return getJdbcTemplate().query(sql, lParams, getMapper());
	}
	@SuppressWarnings("unchecked")
	public List<NumSubTabVb> findNumSubTabsByNumTabAndStatus(int pNumTab, int[] status) throws DataAccessException {
		String query = "SELECT * FROM NUM_SUB_TAB WHERE NUM_SUBTAB_STATUS IN (";
		String params = Arrays.toString(status);
		params = params.substring(1,params.length()-1);
		query = query + params + ") AND NUM_TAB = ? ORDER BY NUM_SUB_TAB";
		Object[] lParams = new Object[1];
		lParams[0] = pNumTab;
		return getJdbcTemplate().query(query, lParams, getMapper());
	}
	@SuppressWarnings("unchecked")
	public List<NumSubTabVb> findNumSubTabsByNumTabAndNumSubTab(int pNumTab, int[] numSubTab) throws DataAccessException {
		String query = "SELECT * FROM NUM_SUB_TAB WHERE NUM_SUBTAB_STATUS = 0 AND NUM_SUB_TAB IN (";
		String params = Arrays.toString(numSubTab);
		params = params.substring(1,params.length()-1);
		query = query + params + ") AND NUM_TAB = ? ORDER BY NUM_SUB_TAB";
		Object[] lParams = new Object[1];
		lParams[0] = pNumTab;
		return getJdbcTemplate().query(query, lParams, getMapper());
	}	
	@Override
	protected RowMapper getMapper(){
		
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				NumSubTabVb numSubTabVb = new NumSubTabVb();
				numSubTabVb.setNumTab(rs.getInt("NUM_TAB"));
				numSubTabVb.setNumSubTab(rs.getInt("NUM_SUB_TAB"));
				numSubTabVb.setDescription(rs.getString("NUM_SUBTAB_DESCRIPTION"));
				numSubTabVb.setNumSubTabStatusNt(rs.getInt("NUM_SUBTAB_STATUS_NT"));
				numSubTabVb.setNumSubTabStatus(rs.getInt("NUM_SUBTAB_STATUS"));
				numSubTabVb.setDbStatus(rs.getInt("NUM_SUBTAB_STATUS"));
				numSubTabVb.setRecordIndicator(rs.getInt("RECORD_INDICATOR"));
				numSubTabVb.setMaker(rs.getLong("MAKER"));
				numSubTabVb.setVerifier(rs.getLong("VERIFIER"));
				numSubTabVb.setInternalStatus(rs.getInt("INTERNAL_STATUS"));
				numSubTabVb.setDateCreation(rs.getString("DATE_CREATION"));
				numSubTabVb.setDateLastModified(rs.getString("DATE_LAST_MODIFIED"));
				return numSubTabVb;
			}
		};
		return mapper;
	}
	protected RowMapper getQueryPopupMapper(){
		
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				NumSubTabVb numSubTabVb = new NumSubTabVb();
				numSubTabVb.setNumTab(rs.getInt("NUM_TAB"));
				numSubTabVb.setNumSubTab(rs.getInt("NUM_SUB_TAB"));
				numSubTabVb.setDescription(rs.getString("NUM_SUBTAB_DESCRIPTION"));
				numSubTabVb.setNumSubTabStatusNt(rs.getInt("NUM_SUBTAB_STATUS_NT"));
				numSubTabVb.setNumSubTabStatus(rs.getInt("NUM_SUBTAB_STATUS"));
				numSubTabVb.setRecordIndicator(rs.getInt("RECORD_INDICATOR"));
				numSubTabVb.setMaker(rs.getLong("MAKER"));
				numSubTabVb.setVerifier(rs.getLong("VERIFIER"));
				numSubTabVb.setStatusDesc(rs.getString("STATUS_DESC"));
				numSubTabVb.setRecordIndicatorDesc(rs.getString("RECORD_INDICATOR_DESC"));
				numSubTabVb.setMakerName(rs.getString("MAKER_NAME"));
				numSubTabVb.setVerifierName(rs.getString("VERIFIER_NAME"));
				numSubTabVb.setInternalStatus(rs.getInt("INTERNAL_STATUS"));
				numSubTabVb.setDateCreation(rs.getString("DATE_CREATION"));
				numSubTabVb.setDateLastModified(rs.getString("DATE_LAST_MODIFIED"));
				return numSubTabVb;
			}
		};
		return mapper;
	}
	public List<NumSubTabVb> getQueryPopupResults(NumSubTabVb dObj){
		Vector<Object> params = new Vector<Object>();
		setServiceDefaults();
		StringBuffer strBufApprove = new StringBuffer(" SELECT * FROM ( Select TAppr.NUM_TAB, TAppr.NUM_SUB_TAB, TAppr.NUM_SUBTAB_DESCRIPTION, TAppr.NUM_SUBTAB_STATUS_NT, " +
				"TAppr.NUM_SUBTAB_STATUS, " +
				" (SELECT  NUM_SUBTAB_DESCRIPTION FROM NUM_SUB_TAB  WHERE NUM_TAB = TAppr.NUM_SUBTAB_STATUS_NT AND NUM_SUB_TAB = TAppr.NUM_SUBTAB_STATUS) STATUS_DESC,"+  
			    "  RECORD_INDICATOR,"+  
			    " (SELECT NUM_SUBTAB_DESCRIPTION  FROM  NUM_SUB_TAB  WHERE  NUM_TAB = TAppr.RECORD_INDICATOR_NT  AND NUM_SUB_TAB = TAppr.RECORD_INDICATOR) RECORD_INDICATOR_DESC,"+  
			    "  MAKER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "+getDbFunction("NVL")+"(TAppr.MAKER,0) ) MAKER_NAME, "+               
				"  VERIFIER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "+getDbFunction("NVL")+"(TAppr.VERIFIER,0) ) VERIFIER_NAME, "+
				"	"+dbFunctionFormats("TAppr.DATE_LAST_MODIFIED","DATETIME_FORMAT", null)+" DATE_LAST_MODIFIED, "+
				"	"+dbFunctionFormats("TAppr.DATE_CREATION","DATETIME_FORMAT", null)+" DATE_CREATION, "+
				" INTERNAL_STATUS From NUM_SUB_TAB TAppr WHERE TAPPR.NUM_TAB = ? ) TAppr ");
		String strWhereNotExists = new String( " Not Exists " +
				"(Select 'X' From NUM_SUB_TAB_PEND TPend Where TPend.NUM_TAB = TAppr.NUM_TAB AND TAppr.NUM_SUB_TAB=TPend.NUM_SUB_TAB) ");
		StringBuffer strBufPending = new StringBuffer(" SELECT * FROM ( Select TPEND.NUM_TAB, TPEND.NUM_SUB_TAB, TPEND.NUM_SUBTAB_DESCRIPTION, TPEND.NUM_SUBTAB_STATUS_NT, " +
				"TPEND.NUM_SUBTAB_STATUS, " +
				" (SELECT  NUM_SUBTAB_DESCRIPTION FROM NUM_SUB_TAB  WHERE NUM_TAB = TPEND.NUM_SUBTAB_STATUS_NT AND NUM_SUB_TAB = TPEND.NUM_SUBTAB_STATUS) STATUS_DESC,"+  
			    "  RECORD_INDICATOR,"+  
			    " (SELECT NUM_SUBTAB_DESCRIPTION  FROM  NUM_SUB_TAB  WHERE  NUM_TAB = TPEND.RECORD_INDICATOR_NT  AND NUM_SUB_TAB = TPEND.RECORD_INDICATOR) RECORD_INDICATOR_DESC,"+  
			    "  MAKER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "+getDbFunction("NVL")+"(TPEND.MAKER,0) ) MAKER_NAME, "+               
				"  VERIFIER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "+getDbFunction("NVL")+"(TPEND.VERIFIER,0) ) VERIFIER_NAME, "+
				"	"+dbFunctionFormats("TPEND.DATE_LAST_MODIFIED","DATETIME_FORMAT", null)+" DATE_LAST_MODIFIED, "+
				"	"+dbFunctionFormats("TPEND.DATE_CREATION","DATETIME_FORMAT", null)+" DATE_CREATION, "+
				" INTERNAL_STATUS From NUM_SUB_TAB_PEND TPEND WHERE TPEND.NUM_TAB = ? ) TPEND ");
		params.addElement(new Integer(dObj.getNumTab()));
		if (dObj.getRecordIndicator() != -1)
		{
			if (dObj.getRecordIndicator() > 3)
			{
				params.addElement(new Integer(0));
				CommonUtils.addToQuery("TAppr.RECORD_INDICATOR > ?", strBufApprove);
				CommonUtils.addToQuery("TPend.RECORD_INDICATOR > ?", strBufPending);
			}
			else
			{
				params.addElement(new Integer(dObj.getRecordIndicator()));
				CommonUtils.addToQuery("TAppr.RECORD_INDICATOR = ?", strBufApprove);
				CommonUtils.addToQuery("TPend.RECORD_INDICATOR = ?", strBufPending);
			}
		}
		try
		{
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
					case "numSubTab":
						CommonUtils.addToQuerySearch(" upper(TAppr.NUM_SUB_TAB) "+ val, strBufApprove, data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPEND.NUM_SUB_TAB) "+ val, strBufPending, data.getJoinType());
						break;
						
					case "description":
						CommonUtils.addToQuerySearch(" upper(TAppr.NUM_SUBTAB_DESCRIPTION) "+ val, strBufApprove, data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPEND.NUM_SUBTAB_DESCRIPTION) "+ val, strBufPending, data.getJoinType());
						break;	
						
					case "numSubTabStatus":
						CommonUtils.addToQuerySearch(" upper(TAppr.STATUS_DESC) "+ val, strBufApprove, data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPEND.STATUS_DESC) "+ val, strBufPending, data.getJoinType());
						break;

					case "recordIndicatorDesc":
						CommonUtils.addToQuerySearch(" upper(TAppr.RECORD_INDICATOR_DESC) "+ val, strBufApprove, data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPEND.RECORD_INDICATOR_DESC) "+ val, strBufPending, data.getJoinType());
						break;

						default:
					}
					count++;
				}
			}
			String orderBy = " Order By NUM_TAB, NUM_SUB_TAB ";
			return getQueryPopupResults(dObj,strBufPending, strBufApprove, strWhereNotExists, orderBy, params, getQueryPopupMapper());
			
		}catch(Exception ex){
			
			//ex.printStackTrace();
			//logger.error(((strBufApprove==null)? "strBufApprove is Null":strBufApprove.toString()));
			//logger.error("UNION");
			//logger.error(((strBufPending==null)? "strBufPending is Null":strBufPending.toString()));

			/*if (params != null)
				for(int i=0 ; i< params.size(); i++)*/
					//logger.error("objParams[" + i + "]" + params.get(i).toString());
			return null;

		}
	}
	@Override
	public List<NumSubTabVb> getQueryResults(NumSubTabVb dObj, int intStatus){
		return getQueryResultsForReview(dObj, intStatus);
	}
	public List<NumSubTabVb> getQueryResultsForReview(NumSubTabVb dObj, int intStatus){

		List<NumSubTabVb> collTemp = null;
		final int intKeyFieldsCount = 2;
		setServiceDefaults();
		String strQueryAppr = new String("Select TAppr.NUM_TAB, TAppr.NUM_SUB_TAB, TAppr.NUM_SUBTAB_DESCRIPTION, " +
				"TAppr.NUM_SUBTAB_STATUS_NT, TAppr.NUM_SUBTAB_STATUS,  " +
				" (SELECT  NUM_SUBTAB_DESCRIPTION FROM NUM_SUB_TAB  WHERE NUM_TAB = TAppr.NUM_SUBTAB_STATUS_NT AND NUM_SUB_TAB = TAppr.NUM_SUBTAB_STATUS) STATUS_DESC,"+  
				"  RECORD_INDICATOR,"+  
				" (SELECT NUM_SUBTAB_DESCRIPTION  FROM  NUM_SUB_TAB  WHERE  NUM_TAB = TAppr.RECORD_INDICATOR_NT  AND NUM_SUB_TAB = TAppr.RECORD_INDICATOR) RECORD_INDICATOR_DESC,"+  
				"  MAKER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "+getDbFunction("NVL")+"(TAppr.MAKER,0) ) MAKER_NAME, "+               
				"  VERIFIER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "+getDbFunction("NVL")+"(TAppr.VERIFIER,0) ) VERIFIER_NAME, "+
				""+dbFunctionFormats("TAPPR.DATE_LAST_MODIFIED","DATETIME_FORMAT", null)+" DATE_LAST_MODIFIED, "+
				""+dbFunctionFormats("TAPPR.DATE_CREATION","DATETIME_FORMAT", null)+" DATE_CREATION, "+
				" INTERNAL_STATUS From NUM_SUB_TAB TAppr " +
				"Where TAppr.NUM_TAB= ? AND TAppr.NUM_SUB_TAB =?");
		String strQueryPend = new String("Select TPend.NUM_TAB, TPend.NUM_SUB_TAB, TPend.NUM_SUBTAB_DESCRIPTION, " +
				"TPend.NUM_SUBTAB_STATUS_NT, TPend.NUM_SUBTAB_STATUS,  " +
				" (SELECT  NUM_SUBTAB_DESCRIPTION FROM NUM_SUB_TAB  WHERE NUM_TAB = TPend.NUM_SUBTAB_STATUS_NT AND NUM_SUB_TAB = TPend.NUM_SUBTAB_STATUS) STATUS_DESC,"+  
				"  RECORD_INDICATOR,"+  
				" (SELECT NUM_SUBTAB_DESCRIPTION  FROM  NUM_SUB_TAB  WHERE  NUM_TAB = TPend.RECORD_INDICATOR_NT  AND NUM_SUB_TAB = TPend.RECORD_INDICATOR) RECORD_INDICATOR_DESC,"+  
				"  MAKER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "+getDbFunction("NVL")+"(TPend.MAKER,0) ) MAKER_NAME, "+               
				"  VERIFIER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "+getDbFunction("NVL")+"(TPend.VERIFIER,0) ) VERIFIER_NAME, "+
				""+dbFunctionFormats("TPend.DATE_LAST_MODIFIED","DATETIME_FORMAT", null)+" DATE_LAST_MODIFIED, "+
				""+dbFunctionFormats("TPend.DATE_CREATION","DATETIME_FORMAT", null)+" DATE_CREATION, "+
				" INTERNAL_STATUS From NUM_SUB_TAB_PEND TPend " +
				"Where TPend.NUM_TAB= ? AND TPend.NUM_SUB_TAB =?");

		Object objParams[] = new Object[intKeyFieldsCount];
		objParams[0] = dObj.getNumTab();
		objParams[1] = dObj.getNumSubTab();

		try
		{	if(!dObj.isVerificationRequired()){intStatus =0;}
			if(intStatus == 0){
				//logger.info("Executing approved query");
				collTemp = getJdbcTemplate().query(strQueryAppr.toString(),objParams,getQueryPopupMapper());
			}else{
				//logger.info("Executing pending query");
				collTemp = getJdbcTemplate().query(strQueryPend.toString(),objParams,getQueryPopupMapper());
			}
			return collTemp;
		}catch(Exception ex){
			/*ex.printStackTrace();
			logger.error("Error: getQueryResultsForReview Exception :   ");
			if(intStatus == 0)
				//logger.error(((strQueryAppr == null) ? "strQueryAppr is Null" : strQueryAppr.toString()));
			else
				//logger.error(((strQueryPend == null) ? "strQueryPend is Null" : strQueryPend.toString()));

			if (objParams != null)
				for(int i=0 ; i< objParams.length; i++)*/
					//logger.error("objParams[" + i + "]" + objParams[i].toString());
			return null;
		}
	}
	public List<NumSubTabVb> getQueryResultsByParent(NumSubTabVb dObj, int intStatus){

		List<NumSubTabVb> collTemp = null;
		final int intKeyFieldsCount = 1;
		setServiceDefaults();
		String strQueryAppr = new String("Select TAppr.NUM_TAB, TAppr.NUM_SUB_TAB, TAppr.NUM_SUBTAB_DESCRIPTION, " +
				" TAppr.NUM_SUBTAB_STATUS_NT, TAppr.NUM_SUBTAB_STATUS, TAppr.RECORD_INDICATOR_NT, TAppr.RECORD_INDICATOR, " +
				" TAppr.MAKER, TAppr.VERIFIER,  " +
				""+dbFunctionFormats("TAppr.DATE_LAST_MODIFIED","DATETIME_FORMAT", null)+" DATE_LAST_MODIFIED, "+
				""+dbFunctionFormats("TAppr.DATE_CREATION","DATETIME_FORMAT", null)+" DATE_CREATION, "+
				" INTERNAL_STATUS From NUM_SUB_TAB TAppr " +
				" Where TAppr.NUM_TAB= ?");
		String strQueryPend = new String("Select TPend.NUM_TAB, TPend.NUM_SUB_TAB, TPend.NUM_SUBTAB_DESCRIPTION, " +
				" TPend.NUM_SUBTAB_STATUS_NT, TPend.NUM_SUBTAB_STATUS, TPend.RECORD_INDICATOR_NT, TPend.RECORD_INDICATOR, " +
				" TPend.MAKER, TPend.VERIFIER," +
				""+dbFunctionFormats("TPend.DATE_LAST_MODIFIED","DATETIME_FORMAT", null)+" DATE_LAST_MODIFIED, "+
				""+dbFunctionFormats("TPend.DATE_CREATION","DATETIME_FORMAT", null)+" DATE_CREATION, "+
				" INTERNAL_STATUS From NUM_SUB_TAB_PEND TPend " +
				"Where TPend.NUM_TAB= ?");

		Object objParams[] = new Object[intKeyFieldsCount];
		objParams[0] = dObj.getNumTab();

		try
		{	if(!dObj.isVerificationRequired()){intStatus =0;}
			if(intStatus == 0){
				logger.info("Executing approved query");
				collTemp = getJdbcTemplate().query(strQueryAppr.toString(),objParams,getMapper());
			}else{
				logger.info("Executing pending query");
				collTemp = getJdbcTemplate().query(strQueryPend.toString(),objParams,getMapper());
			}
			return collTemp;
		}catch(Exception ex){
			/*ex.printStackTrace();
			logger.error("Error: getQueryResultsForReview Exception :   ");
			if(intStatus == 0)
				//logger.error(((strQueryAppr == null) ? "strQueryAppr is Null" : strQueryAppr.toString()));
			else
				//logger.error(((strQueryPend == null) ? "strQueryPend is Null" : strQueryPend.toString()));

			if (objParams != null)
				for(int i=0 ; i< objParams.length; i++)*/
					//logger.error("objParams[" + i + "]" + objParams[i].toString());
			return null;
		}
	}	
	@Override
	protected List<NumSubTabVb> selectApprovedRecord(NumSubTabVb vObject){
		return getQueryResultsForReview(vObject, Constants.STATUS_ZERO);
	}

	@Override
	protected List<NumSubTabVb> doSelectPendingRecord(NumSubTabVb vObject){
		return getQueryResultsForReview(vObject, Constants.STATUS_PENDING);
	}
	public void setPendingRecordCount(TabVb objectTemp) {
		final int intKeyFieldsCount = 1;
		int intStatus = 1;
		StringBuffer strQueryPend = new StringBuffer("Select COUNT('X')" +
			" From NUM_SUB_TAB_PEND TPend Where TPend.NUM_TAB = '"+objectTemp.getTab()+"' ");
		Object objParams[] = new Object[intKeyFieldsCount];
		objParams[0] = objectTemp.getTab();//[NUM_TAB]
		try
		{
			if(!objectTemp.isVerificationRequired()){intStatus =0;}
			if(intStatus != 0){
				logger.info("Executing pending query");
				int count = getJdbcTemplate().queryForObject(strQueryPend.toString(),Integer.class);
				//int count = getJdbcTemplate().queryForInt(strQueryPend.toString(),objParams);
				objectTemp.setPendingRecordsCount(count);
			}
		}
		catch(Exception ex){
			//ex.printStackTrace();
			//logger.error("Error: getQueryResultsForReview Exception :   ");
			//logger.error(((strQueryPend == null) ? "strQueryPend is null" : strQueryPend.toString()));

		}
	}
	public void setTotRecordCount(TabVb objectTemp) {
		final int intKeyFieldsCount = 1;
		int intStatus = 1;
		StringBuffer strQueryAppr = new StringBuffer("Select COUNT('X')" +
			" From NUM_SUB_TAB TAppr Where TAppr.NUM_TAB = '"+objectTemp.getTab()+"' ");
		Object objParams[] = new Object[intKeyFieldsCount];
		objParams[0] = objectTemp.getTab();//[NUM_TAB]
		try
		{
			if(!objectTemp.isVerificationRequired()){intStatus =0;}
			if(intStatus != 0){
				//logger.info("Executing approved query");
				int count = getJdbcTemplate().queryForObject(strQueryAppr.toString(),Integer.class);
				//int count = getJdbcTemplate().queryForInt(strQueryAppr.toString(),objParams);
				objectTemp.setTotRecordsCount(count);
			}
		}
		catch(Exception ex){
			//ex.printStackTrace();
			//logger.error("Error: getQueryResultsForReview Exception :   ");
			//logger.error(((strQueryAppr == null) ? "strQueryAppr is null" : strQueryAppr.toString()));
		}
	}
	@Override
	protected void setServiceDefaults(){
		serviceName = "NumSubTab";
		serviceDesc = "Num Sub Tab";
		tableName = "NUM_TAB";
		childTableName = "NUM_SUB_TAB";
		intCurrentUserId = CustomContextHolder.getContext().getVisionId();
	}

	@Override
	protected int getStatus(NumSubTabVb records){return records.getNumSubTabStatus();}

	@Override
	protected void setStatus(NumSubTabVb vObject,int status){vObject.setNumSubTabStatus(status);}

	@Override
	protected int doInsertionAppr(NumSubTabVb vObject){
		String query = "Insert Into NUM_SUB_TAB( " + 
			"NUM_TAB, NUM_SUB_TAB, NUM_SUBTAB_DESCRIPTION, NUM_SUBTAB_STATUS_NT, NUM_SUBTAB_STATUS, " + 
			" RECORD_INDICATOR_NT, RECORD_INDICATOR, MAKER, VERIFIER, INTERNAL_STATUS, DATE_LAST_MODIFIED, " + 
			" DATE_CREATION) " + 
			" Values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " + getDbFunction("SYSDATE")+ ", " + getDbFunction("SYSDATE")+ ")";

		Object args[] = {vObject.getNumTab(), vObject.getNumSubTab(), vObject.getDescription(),
			vObject.getNumSubTabStatusNt(), vObject.getNumSubTabStatus(), vObject.getRecordIndicatorNt(),
			vObject.getRecordIndicator(), vObject.getMaker(), vObject.getVerifier(), vObject.getInternalStatus()};

		return getJdbcTemplate().update(query,args);
	}

	@Override
	protected int doInsertionPend(NumSubTabVb vObject){
		String query = "Insert Into NUM_SUB_TAB_PEND( " + 
			"NUM_TAB, NUM_SUB_TAB, NUM_SUBTAB_DESCRIPTION, NUM_SUBTAB_STATUS_NT, NUM_SUBTAB_STATUS, " + 
			" RECORD_INDICATOR_NT, RECORD_INDICATOR, MAKER, VERIFIER, INTERNAL_STATUS, DATE_LAST_MODIFIED, " + 
			" DATE_CREATION) " + 
			" Values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " + getDbFunction("SYSDATE")+ ", " + getDbFunction("SYSDATE")+ ")";

		Object args[] = {vObject.getNumTab(), vObject.getNumSubTab(), vObject.getDescription(),
			vObject.getNumSubTabStatusNt(), vObject.getNumSubTabStatus(), vObject.getRecordIndicatorNt(),
			vObject.getRecordIndicator(), vObject.getMaker(), vObject.getVerifier(), vObject.getInternalStatus()};

		return getJdbcTemplate().update(query,args);
	}

	@Override
	protected int doInsertionPendWithDc(NumSubTabVb vObject){
		String query = "Insert Into NUM_SUB_TAB_PEND( " + 
			"NUM_TAB, NUM_SUB_TAB, NUM_SUBTAB_DESCRIPTION, NUM_SUBTAB_STATUS_NT, NUM_SUBTAB_STATUS, " + 
			" RECORD_INDICATOR_NT, RECORD_INDICATOR, MAKER, VERIFIER, INTERNAL_STATUS, DATE_LAST_MODIFIED, " + 
			" DATE_CREATION) " + 
			"Values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " + getDbFunction("SYSDATE")+ ", "+getDbFunction("DATE_CREATION")+")";

		Object args[] = {vObject.getNumTab(), vObject.getNumSubTab(), vObject.getDescription(), 
			vObject.getNumSubTabStatusNt(), vObject.getNumSubTabStatus(), vObject.getRecordIndicatorNt(), 
			vObject.getRecordIndicator(), vObject.getMaker(), vObject.getVerifier(), vObject.getInternalStatus(), 
			vObject.getDateCreation()};

		return getJdbcTemplate().update(query,args);
	}

	@Override
	protected int doUpdateAppr(NumSubTabVb vObject){
		String query = "Update NUM_SUB_TAB Set " + 
			"NUM_SUBTAB_DESCRIPTION = ?, NUM_SUBTAB_STATUS_NT = ?, NUM_SUBTAB_STATUS = ?, RECORD_INDICATOR_NT = ?, " + 
			" RECORD_INDICATOR = ?, MAKER = ?, VERIFIER = ?, INTERNAL_STATUS = ?, DATE_LAST_MODIFIED = " + getDbFunction("SYSDATE")+ " " + 
			"Where NUM_TAB = ? " +
			" And NUM_SUB_TAB = ? ";

		Object args[] = {vObject.getDescription(), vObject.getNumSubTabStatusNt(),
			vObject.getNumSubTabStatus(), vObject.getRecordIndicatorNt(), vObject.getRecordIndicator(),
			vObject.getMaker(), vObject.getVerifier(), vObject.getInternalStatus(), 
			vObject.getNumTab(), vObject.getNumSubTab()};

		return getJdbcTemplate().update(query,args);
	}

	@Override
	protected int doUpdatePend(NumSubTabVb vObject){
		String query = "Update NUM_SUB_TAB_PEND Set " + 
			"NUM_SUBTAB_DESCRIPTION = ?, NUM_SUBTAB_STATUS_NT = ?, NUM_SUBTAB_STATUS = ?, RECORD_INDICATOR_NT = ?, "+
			"RECORD_INDICATOR = ?, MAKER = ?, VERIFIER = ?, INTERNAL_STATUS = ?, DATE_LAST_MODIFIED = " + getDbFunction("SYSDATE")+ " " + 
			"Where NUM_TAB = ? " +
			" And NUM_SUB_TAB = ? ";

		Object args[] = {vObject.getDescription(), vObject.getNumSubTabStatusNt(), 
			vObject.getNumSubTabStatus(), vObject.getRecordIndicatorNt(), vObject.getRecordIndicator(), 
			vObject.getMaker(), vObject.getVerifier(), vObject.getInternalStatus(),
			vObject.getNumTab(), vObject.getNumSubTab()};

		return getJdbcTemplate().update(query,args);
	}

	@Override
	protected int doDeleteAppr(NumSubTabVb vObject){
		String query = "Delete From NUM_SUB_TAB Where " + 
			"NUM_TAB = ? AND NUM_SUB_TAB = ? " ;
		Object args[] = {vObject.getNumTab(), vObject.getNumSubTab()};
		return getJdbcTemplate().update(query,args);
	}

	@Override
	protected int deletePendingRecord(NumSubTabVb vObject){
		String query = "Delete From NUM_SUB_TAB_PEND Where " + 
		"NUM_TAB = ? AND NUM_SUB_TAB = ? " ;
		Object args[] = {vObject.getNumTab(), vObject.getNumSubTab()};
		return getJdbcTemplate().update(query,args);
	}

	@Override
	protected String frameErrorMessage(NumSubTabVb vObject, String strOperation)
	{
		// specify all the key fields and their values first
		String strErrMsg = new String("");
		try {
			strErrMsg =  strErrMsg + " NUM_TAB:" + vObject.getNumTab();
			strErrMsg =  strErrMsg + " NUM_SUB_TAB:" + vObject.getNumSubTab();
			// Now concatenate the error message that has been sent
			if ("Approve".equalsIgnoreCase(strOperation))
				strErrMsg = strErrMsg + " failed during approve Operation. Bulk Approval aborted !!";
			else
				strErrMsg = strErrMsg + " failed during reject Operation. Bulk Rejection aborted !!";
		}catch(Exception ex){
			strErrorDesc = ex.getMessage();
			strErrMsg = strErrMsg + strErrorDesc;
			//logger.error(strErrMsg, ex);
		}
		// Return back the error message string
		return strErrMsg;
	}


	@Override
	protected String getAuditString(NumSubTabVb vObject)
	{
		StringBuffer strAudit = new StringBuffer("");
		try
		{
			strAudit.append(vObject.getNumTab());
			strAudit.append("!|#");
			strAudit.append(vObject.getNumSubTab());
			strAudit.append("!|#");
			if(vObject.getDescription() != null)
				strAudit.append(vObject.getDescription().trim());
			else
				strAudit.append("NULL");
			strAudit.append("!|#");

			strAudit.append(vObject.getNumSubTabStatusNt());
			strAudit.append("!|#");
			strAudit.append(vObject.getNumSubTabStatus());
			strAudit.append("!|#");
			strAudit.append(vObject.getRecordIndicatorNt());
			strAudit.append("!|#");
			strAudit.append(vObject.getRecordIndicator());
			strAudit.append("!|#");
			strAudit.append(vObject.getMaker());
			strAudit.append("!|#");
			strAudit.append(vObject.getVerifier());
			strAudit.append("!|#");
			strAudit.append(vObject.getInternalStatus());
			strAudit.append("!|#");
			if(vObject.getDateLastModified() != null)
				strAudit.append(vObject.getDateLastModified().trim());
			else
				strAudit.append("NULL");
			strAudit.append("!|#");

			if(vObject.getDateCreation() != null)
				strAudit.append(vObject.getDateCreation().trim());
			else
				strAudit.append("NULL");
			strAudit.append("!|#");

		}
		catch(Exception ex)
		{
			strErrorDesc = ex.getMessage();
			strAudit = strAudit.append(strErrorDesc);
			//ex.printStackTrace();
		}
		return strAudit.toString();
	}

	@SuppressWarnings("unchecked")
	public List<NumSubTabVb> findNumSubTabByDesc(String val) throws DataAccessException {
		String sql = "SELECT * FROM NUM_SUB_TAB WHERE NUM_SUBTAB_STATUS = 0 AND NUM_TAB = 2000  and upper(NUM_SUBTAB_DESCRIPTION) "+val+" ORDER BY NUM_SUB_TAB";	
		return  getJdbcTemplate().query(sql, getMapper());
	}
	
}