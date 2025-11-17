package com.vision.dao;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Vector;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.vision.authentication.CustomContextHolder;
import com.vision.util.CommonUtils;
import com.vision.util.Constants;
import com.vision.util.ValidationUtil;
import com.vision.vb.FeesPriorityVb;
import com.vision.vb.SmartSearchVb;

@Component
public class FeesPriorityDao extends AbstractDao<FeesPriorityVb> {
	
	String statusNtApprDesc = ValidationUtil.numAlphaTabDescritpionQuery("NT", 1, "TAppr.STATUS", "STATUS_DESC");
	String statusNtPendDesc = ValidationUtil.numAlphaTabDescritpionQuery("NT", 1, "TPend.STATUS", "STATUS_DESC");

	String RecordIndicatorNtApprDesc = ValidationUtil.numAlphaTabDescritpionQuery("NT", 7, "TAppr.RECORD_INDICATOR", "RECORD_INDICATOR_DESC");
	String RecordIndicatorNtPendDesc = ValidationUtil.numAlphaTabDescritpionQuery("NT", 7, "TPend.RECORD_INDICATOR", "RECORD_INDICATOR_DESC");
	
	String ChannelTypeAtApprDesc = ValidationUtil.numAlphaTabDescritpionQuery("AT", 8, "TAppr.CHANNEL_TYPE", "CHANNEL_TYPE_DESC");
	String ChannelTypeAtPendDesc = ValidationUtil.numAlphaTabDescritpionQuery("AT", 8, "TPend.CHANNEL_TYPE", "CHANNEL_TYPE_DESC");
	
/*******Mapper Start**********/
	String CountryApprDesc = "(SELECT COUNTRY_DESCRIPTION FROM COUNTRIES WHERE COUNTRY = TAppr.COUNTRY) COUNTRY_DESC";
	String CountryPendDesc = "(SELECT COUNTRY_DESCRIPTION FROM COUNTRIES WHERE COUNTRY = TPend.COUNTRY) COUNTRY_DESC";
	String LeBookApprDesc = "(SELECT  LEB_DESCRIPTION FROM LE_BOOK WHERE  LE_BOOK = TAppr.LE_BOOK AND COUNTRY = TAppr.COUNTRY ) LE_BOOK_DESC";
	String LeBookTPendDesc = "(SELECT  LEB_DESCRIPTION FROM LE_BOOK WHERE  LE_BOOK = TPend.LE_BOOK AND COUNTRY = TPend.COUNTRY ) LE_BOOK_DESC";
	
	protected RowMapper getMapper(FeesPriorityVb feePriorityVb){
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				FeesPriorityVb vObject = new FeesPriorityVb();
				vObject.setCountry(rs.getString("COUNTRY"));
				vObject.setLeBook(rs.getString("LE_BOOK"));
				vObject.setDimensionName(rs.getString("DIMENSION_NAME"));
				vObject.setPriorityOrder(rs.getInt("PRIORITY_ORDER"));
				vObject.setColumnAlias(rs.getString("COLUMN_ALIAS"));
				vObject.setChannelType(rs.getString("CHANNEL_TYPE"));
				vObject.setChannelTypeAt(rs.getInt("CHANNEL_TYPE_AT"));
				vObject.setWeightage(rs.getInt("WEIGHTAGE"));
				vObject.setStatusNt(rs.getInt("STATUS_NT"));
				vObject.setDbStatus(rs.getInt("STATUS"));
				vObject.setStatusDesc(rs.getString("STATUS_DESC"));
				vObject.setRecordIndicatorNt(rs.getInt("RECORD_INDICATOR_NT"));
				vObject.setRecordIndicator(rs.getInt("RECORD_INDICATOR"));
				vObject.setRecordIndicatorDesc(rs.getString("RECORD_INDICATOR_DESC"));
				vObject.setMaker(rs.getLong("MAKER"));
				vObject.setVerifier(rs.getLong("VERIFIER"));
				vObject.setMakerName(rs.getString("MAKER_NAME"));
				vObject.setVerifierName(rs.getString("VERIFIER_NAME"));
				vObject.setDateCreation(rs.getString("DATE_CREATION"));
				vObject.setDateLastModified(rs.getString("DATE_LAST_MODIFIED"));
				vObject.setPriorityType(feePriorityVb.getPriorityType());
				return vObject;
			}
		};
		return mapper;
	}
	protected RowMapper getDetailMapper(FeesPriorityVb priorityVb){
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				FeesPriorityVb vObject = new FeesPriorityVb();
				vObject.setCountry(rs.getString("COUNTRY"));
				vObject.setLeBook(rs.getString("LE_BOOK"));
				vObject.setBusinessLineId(rs.getString("BUSINESS_LINE_ID"));
				vObject.setBusinessLineDesc(rs.getString("BUSINESS_LINE_DESCRIPTION"));
				vObject.setEffectiveDate(rs.getString("EFFECTIVE_DATE"));
				vObject.setDimensionName(rs.getString("DIMENSION_NAME"));
				vObject.setPriorityOrder(rs.getInt("PRIORITY_ORDER"));
				vObject.setColumnAlias(rs.getString("COLUMN_ALIAS"));
				vObject.setWeightage(rs.getInt("WEIGHTAGE"));
				vObject.setStatusNt(rs.getInt("STATUS_NT"));
				vObject.setDbStatus(rs.getInt("STATUS"));
				vObject.setStatusDesc(rs.getString("STATUS_DESC"));
				vObject.setRecordIndicatorNt(rs.getInt("RECORD_INDICATOR_NT"));
				vObject.setRecordIndicator(rs.getInt("RECORD_INDICATOR"));
				vObject.setRecordIndicatorDesc(rs.getString("RECORD_INDICATOR_DESC"));
				vObject.setMaker(rs.getLong("MAKER"));
				vObject.setVerifier(rs.getLong("VERIFIER"));
				vObject.setMakerName(rs.getString("MAKER_NAME"));
				vObject.setVerifierName(rs.getString("VERIFIER_NAME"));
				vObject.setDateCreation(rs.getString("DATE_CREATION"));
				vObject.setDateLastModified(rs.getString("DATE_LAST_MODIFIED"));
				vObject.setPriorityType(priorityVb.getPriorityType());
				return vObject;
			}
		};
		return mapper;
	}
	@Override
	public List<FeesPriorityVb> getQueryPopupResults(FeesPriorityVb feePriorityVb){
		if("DEFAULT".equalsIgnoreCase(feePriorityVb.getPriorityType())){
			setServiceDefaults();
			return getQueryPopupResultsDefault(feePriorityVb);
		}else{
			setServiceDefaults();
			return getQueryPopupResultsDetail(feePriorityVb);
		}
	}
/*******Mapper End**********/
	public List<FeesPriorityVb> getQueryPopupResultsDefault(FeesPriorityVb dObj){

		Vector<Object> params = new Vector<Object>();
		StringBuffer strBufApprove = new StringBuffer("SELECT * FROM ( Select TAppr.COUNTRY"
			+ ",TAppr.LE_BOOK"
			+ ",TAppr.DIMENSION_NAME"
			+ ",TAppr.PRIORITY_ORDER,TAppr.COLUMN_ALIAS"
			+ ",TAppr.CHANNEL_TYPE"
			+ ",TAppr.CHANNEL_TYPE_AT,"+ChannelTypeAtApprDesc+""
			+ " ,TAppr.WEIGHTAGE"
			+ ",TAppr.STATUS_NT, TAppr.STATUS," +statusNtApprDesc
			+ ",TAppr.RECORD_INDICATOR_NT, TAppr.RECORD_INDICATOR, "+RecordIndicatorNtApprDesc
			+ ", TAppr.MAKER, "+makerApprDesc
			+ ", TAppr.VERIFIER,"+verifierApprDesc
			+ ","+dbFunctionFormats("TAPPR.DATE_LAST_MODIFIED","DATETIME_FORMAT", null)+" DATE_LAST_MODIFIED, "+
			""+dbFunctionFormats("TAPPR.DATE_CREATION","DATETIME_FORMAT", null)+" DATE_CREATION "
			+ " from RA_MST_FEES_DEFAULT_PRIORITY TAppr WHERE TAppr.COUNTRY = ? AND TAppr.LE_BOOK = ? ) TAppr ");
		String strWhereNotExists = new String( " Not Exists (Select 'X' From RA_MST_FEES_DEFAULT_PRIORITY_PEND TPend WHERE "
				+ " TPend.COUNTRY = TAppr.COUNTRY and TPend.LE_BOOK = TAppr.LE_BOOK and TPend.DIMENSION_NAME = TAppr.DIMENSION_NAME )");
		StringBuffer strBufPending = new StringBuffer("SELECT * FROM ( Select TPend.COUNTRY"
			+ ",TPend.LE_BOOK"
			+ ",TPend.DIMENSION_NAME"
			+ ",TPend.PRIORITY_ORDER,TPend.COLUMN_ALIAS"
			+ ",TPend.CHANNEL_TYPE"
			+ ",TPend.CHANNEL_TYPE_AT,"+ChannelTypeAtPendDesc+""
			+ ", TPend.WEIGHTAGE"
			+ ",TPend.STATUS_NT, TPend.STATUS," +statusNtPendDesc
			+ ",TPend.RECORD_INDICATOR_NT, TPend.RECORD_INDICATOR, "+RecordIndicatorNtPendDesc
			+ ", TPend.MAKER, "+makerPendDesc
			+ ", TPend.VERIFIER,"+verifierPendDesc
			+ ","+dbFunctionFormats("TPend.DATE_LAST_MODIFIED","DATETIME_FORMAT", null)+" DATE_LAST_MODIFIED, "+
			""+dbFunctionFormats("TPend.DATE_CREATION","DATETIME_FORMAT", null)+" DATE_CREATION "
			+ " from RA_MST_FEES_DEFAULT_PRIORITY_PEND TPend WHERE TPend.COUNTRY = ? AND TPend.LE_BOOK = ? ) TPend");
		params.addElement(dObj.getCountry());
		params.addElement(dObj.getLeBook());
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
						case "country":
							CommonUtils.addToQuerySearch(" upper(TAppr.COUNTRY) "+ val, strBufApprove, data.getJoinType());
							CommonUtils.addToQuerySearch(" upper(TPend.COUNTRY) "+ val, strBufPending, data.getJoinType());
							break;
	
						case "leBook":
							CommonUtils.addToQuerySearch(" upper(TAppr.LE_BOOK) "+ val, strBufApprove, data.getJoinType());
							CommonUtils.addToQuerySearch(" upper(TPend.LE_BOOK) "+ val, strBufPending, data.getJoinType());
							break;
	
						case "dimensionName":
							CommonUtils.addToQuerySearch(" upper(TAppr.DIMENSION_NAME) "+ val, strBufApprove, data.getJoinType());
							CommonUtils.addToQuerySearch(" upper(TPend.DIMENSION_NAME) "+ val, strBufPending, data.getJoinType());
							break;
	
						case "priorityOrder":
							CommonUtils.addToQuerySearch(" upper(TAppr.PRIORITY_ORDER) "+ val, strBufApprove, data.getJoinType());
							CommonUtils.addToQuerySearch(" upper(TPend.PRIORITY_ORDER) "+ val, strBufPending, data.getJoinType());
							break;
							
						case "columnAlias":
							CommonUtils.addToQuerySearch(" upper(TAppr.COLUMN_ALIAS) "+ val, strBufApprove, data.getJoinType());
							CommonUtils.addToQuerySearch(" upper(TPend.COLUMN_ALIAS) "+ val, strBufPending, data.getJoinType());
							break;
	
						case "channelType":
							CommonUtils.addToQuerySearch(" upper(TAppr.CHANNEL_TYPE) "+ val, strBufApprove, data.getJoinType());
							CommonUtils.addToQuerySearch(" upper(TPend.CHANNEL_TYPE) "+ val, strBufPending, data.getJoinType());
							break;
	
						case "weightage":
							CommonUtils.addToQuerySearch(" upper(TAppr.WEIGHTAGE) "+ val, strBufApprove, data.getJoinType());
							CommonUtils.addToQuerySearch(" upper(TPend.WEIGHTAGE) "+ val, strBufPending, data.getJoinType());
							break;
							
						case "dbStatus":
							CommonUtils.addToQuerySearch(" upper(TAppr.STATUS_DESC) "+ val, strBufApprove, data.getJoinType());
							CommonUtils.addToQuerySearch(" upper(TPend.STATUS_DESC) "+ val, strBufPending, data.getJoinType());
							break;

						case "recordIndicatorDesc":
							CommonUtils.addToQuerySearch(" upper(TAppr.RECORD_INDICATOR_DESC) "+ val, strBufApprove, data.getJoinType());
							CommonUtils.addToQuerySearch(" upper(TPend.RECORD_INDICATOR_DESC) "+ val, strBufPending, data.getJoinType());
							break;		
	
							default:
						}
						count++;
					}
				}
			String orderBy=" Order By  PRIORITY_ORDER ";
			return getQueryPopupResults(dObj,strBufPending, strBufApprove, strWhereNotExists, orderBy, params,getMapper(dObj));

			}catch(Exception ex){
				/*ex.printStackTrace();
				logger.error(((strBufApprove==null)? "strBufApprove is Null":strBufApprove.toString()));
				logger.error("UNION");
				logger.error(((strBufPending==null)? "strBufPending is Null":strBufPending.toString()));

				if (params != null)
					for(int i=0 ; i< params.size(); i++)
						logger.error("objParams[" + i + "]" + params.get(i).toString());*/
			return null;

			}
	}
	public List<FeesPriorityVb> getQueryPopupResultsDetail(FeesPriorityVb dObj){
		List<FeesPriorityVb> collTemp = new ArrayList<>();
		Vector<Object> params = new Vector<Object>();
		String effectiveDateCondAppr = "";
		String effectiveDateCondPend = "";
		if(ValidationUtil.isValid(dObj.getNavigate()) && "Y".equalsIgnoreCase(dObj.getNavigate())) {
			effectiveDateCondAppr = " AND TAPPR.EFFECTIVE_DATE = ? ";
			effectiveDateCondPend = " AND TPend.EFFECTIVE_DATE = ? ";
			//effecDateWhereNotExist = " AND TAPPR.EFFECTIVE_DATE = TPEND.EFFECTIVE_DATE ";
			params.addElement(dObj.getCountry());
			params.addElement(dObj.getLeBook());
			params.addElement(dObj.getBusinessLineId());
			params.addElement(dObj.getEffectiveDate());
		}
		StringBuffer strBufApprove = new StringBuffer("SELECT * FROM ( Select TAppr.COUNTRY"
			+ ",TAppr.LE_BOOK"
			+ ",TAppr.DIMENSION_NAME"
			+ ",TAppr.BUSINESS_LINE_ID"
			+ " ,(select BUSINESS_LINE_DESCRIPTION from RA_MST_BUSINESS_LINE_HEADER T1 "
			+ " where BUSINESS_LINE_STATUS = 0 AND T1.COUNTRY = TAppr.COUNTRY AND T1.LE_BOOK = TAppr.LE_BOOK AND T1.BUSINESS_LINE_ID = TAppr.BUSINESS_LINE_ID ) BUSINESS_LINE_DESCRIPTION "
			+ ","+dbFunctionFormats("TAPPR.EFFECTIVE_DATE","DATETIME_FORMAT", null)+" EFFECTIVE_DATE "
			+ ",TAppr.PRIORITY_ORDER,TAppr.COLUMN_ALIAS"
			+ ", TAppr.WEIGHTAGE"
			+ ",TAppr.STATUS_NT, TAppr.STATUS," +statusNtApprDesc
			+ ",TAppr.RECORD_INDICATOR_NT, TAppr.RECORD_INDICATOR, "+RecordIndicatorNtApprDesc
			+ ", TAppr.MAKER, "+makerApprDesc
			+ ", TAppr.VERIFIER,"+verifierApprDesc
			+ ","+dbFunctionFormats("TAPPR.DATE_LAST_MODIFIED","DATETIME_FORMAT", null)+" DATE_LAST_MODIFIED, "+
			""+dbFunctionFormats("TAPPR.DATE_CREATION","DATETIME_FORMAT", null)+" DATE_CREATION "
			+ " from RA_MST_FEES_DETAIL_PRIORITY TAppr WHERE TAppr.COUNTRY = ? AND TAppr.LE_BOOK = ? "
			+ " and TAppr.BUSINESS_LINE_ID = ? "+effectiveDateCondAppr+") TAppr ");
		String strWhereNotExists = new String( " Not Exists (Select 'X' From RA_MST_FEES_DETAIL_PRIORITY_PEND TPend WHERE  "
				+ " TPend.COUNTRY = TAppr.COUNTRY and TPend.LE_BOOK = TAppr.LE_BOOK "
				+ " and TPend.BUSINESS_LINE_ID = TAppr.BUSINESS_LINE_ID AND TAPPR.EFFECTIVE_DATE = TPEND.EFFECTIVE_DATE"
				+ " AND TAPPR.DIMENSION_NAME = TPend.DIMENSION_NAME)");
		StringBuffer strBufPending = new StringBuffer("SELECT * FROM ( Select TPend.COUNTRY"
			+ ",TPend.LE_BOOK"
			+ ",TPend.DIMENSION_NAME"
			+ ",TPend.BUSINESS_LINE_ID"
			+ " ,(select BUSINESS_LINE_DESCRIPTION from RA_MST_BUSINESS_LINE_HEADER T1 "
			+ " where BUSINESS_LINE_STATUS = 0 AND T1.COUNTRY = TPend.COUNTRY AND T1.LE_BOOK = TPend.LE_BOOK AND T1.BUSINESS_LINE_ID = TPend.BUSINESS_LINE_ID ) BUSINESS_LINE_DESCRIPTION "
			+ ","+dbFunctionFormats("TPend.EFFECTIVE_DATE","DATETIME_FORMAT", null)+" EFFECTIVE_DATE "
			+ ",TPend.PRIORITY_ORDER,TPend.COLUMN_ALIAS"
			+ " ,TPend.WEIGHTAGE"
			+ ",TPend.STATUS_NT, TPend.STATUS," +statusNtPendDesc
			+ ",TPend.RECORD_INDICATOR_NT, TPend.RECORD_INDICATOR, "+RecordIndicatorNtPendDesc
			+ ", TPend.MAKER, "+makerPendDesc
			+ ", TPend.VERIFIER,"+verifierPendDesc
			+ ","+dbFunctionFormats("TPend.DATE_LAST_MODIFIED","DATETIME_FORMAT", null)+" DATE_LAST_MODIFIED, "+
			""+dbFunctionFormats("TPend.DATE_CREATION","DATETIME_FORMAT", null)+" DATE_CREATION "
			+ " from RA_MST_FEES_DETAIL_PRIORITY_PEND TPend WHERE TPend.COUNTRY = ? AND TPend.LE_BOOK = ? "
			+ " and TPend.BUSINESS_LINE_ID = ? "+effectiveDateCondPend+") TPend");
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
					
						case "effectiveDate":
							CommonUtils.addToQuerySearch(getEffectiveDateFilterQuery("TAppr.EFFECTIVE_DATE",data.getValue()), strBufApprove,data.getJoinType());
							CommonUtils.addToQuerySearch(getEffectiveDateFilterQuery("TPend.EFFECTIVE_DATE",data.getValue()), strBufPending,data.getJoinType());
							break;
					
						case "dimensionName":
							CommonUtils.addToQuerySearch(" upper(TAppr.DIMENSION_NAME) "+ val, strBufApprove, data.getJoinType());
							CommonUtils.addToQuerySearch(" upper(TPend.DIMENSION_NAME) "+ val, strBufPending, data.getJoinType());
							break;
	
						case "priorityOrder":
							CommonUtils.addToQuerySearch(" upper(TAppr.PRIORITY_ORDER) "+ val, strBufApprove, data.getJoinType());
							CommonUtils.addToQuerySearch(" upper(TPend.PRIORITY_ORDER) "+ val, strBufPending, data.getJoinType());
							break;
							
						case "columnAlias":
							CommonUtils.addToQuerySearch(" upper(TAppr.COLUMN_ALIAS) "+ val, strBufApprove, data.getJoinType());
							CommonUtils.addToQuerySearch(" upper(TPend.COLUMN_ALIAS) "+ val, strBufPending, data.getJoinType());
							break;
	
						case "channelType":
							CommonUtils.addToQuerySearch(" upper(TAppr.CHANNEL_TYPE) "+ val, strBufApprove, data.getJoinType());
							CommonUtils.addToQuerySearch(" upper(TPend.CHANNEL_TYPE) "+ val, strBufPending, data.getJoinType());
							break;
	
						case "weightage":
							CommonUtils.addToQuerySearch(" upper(TAppr.WEIGHTAGE) "+ val, strBufApprove, data.getJoinType());
							CommonUtils.addToQuerySearch(" upper(TPend.WEIGHTAGE) "+ val, strBufPending, data.getJoinType());
							break;
							
						case "dbStatus":
							CommonUtils.addToQuerySearch(" upper(TAppr.STATUS_DESC) "+ val, strBufApprove, data.getJoinType());
							CommonUtils.addToQuerySearch(" upper(TPend.STATUS_DESC) "+ val, strBufPending, data.getJoinType());
							break;
	
						case "recordIndicator":
							CommonUtils.addToQuerySearch(" upper(TAppr.RECORD_INDICATOR_DESC) "+ val, strBufApprove, data.getJoinType());
							CommonUtils.addToQuerySearch(" upper(TPend.RECORD_INDICATOR_DESC) "+ val, strBufPending, data.getJoinType());
							break;	
	
							default:
					}
					count++;
				}
			}
			String orderBy=" Order By  PRIORITY_ORDER ";
			String whereCond = " WHERE ";
			if(dObj.getSmartSearchOpt() != null && dObj.getSmartSearchOpt().size() > 0)
				whereCond = " AND ";
			
			if(ValidationUtil.isValid(dObj.getNavigate()) && "Y".equalsIgnoreCase(dObj.getNavigate())) {
				collTemp = getQueryPopupResults(dObj,strBufPending, strBufApprove, strWhereNotExists, orderBy, params,getDetailMapper(dObj));
			} else {//get All the Records to Group under Effective Date
				String finalQuery = strBufApprove+whereCond+strWhereNotExists+" UNION "+strBufPending+" "+orderBy;
				Object[] args = new Object[]{dObj.getCountry(),dObj.getLeBook(),dObj.getBusinessLineId(),
						dObj.getCountry(),dObj.getLeBook(),dObj.getBusinessLineId()};
				if(!dObj.isVerificationRequired()) {
					finalQuery =strBufApprove+" "+orderBy;
					args = new Object[]{dObj.getCountry(),dObj.getLeBook(),dObj.getBusinessLineId()};
				}
				collTemp = getJdbcTemplate().query(finalQuery,args,getDetailMapper(dObj));
			}
			}catch(Exception ex){
				/*ex.printStackTrace();
				logger.error(((strBufApprove==null)? "strBufApprove is Null":strBufApprove.toString()));
				logger.error("UNION");
				logger.error(((strBufPending==null)? "strBufPending is Null":strBufPending.toString()));

				if (params != null)
					for(int i=0 ; i< params.size(); i++)
						logger.error("objParams[" + i + "]" + params.get(i).toString());*/
			return null;

			}
		return collTemp;
	}


	public List<FeesPriorityVb> getQueryResultsDefault(FeesPriorityVb dObj, int intStatus){

		setServiceDefaults();

		List<FeesPriorityVb> collTemp = null;

		final int intKeyFieldsCount = 3;
		String strQueryAppr = new String("Select TAppr.COUNTRY"
				+ ",TAppr.LE_BOOK"
				+ ",TAppr.DIMENSION_NAME"
				+ ",TAppr.PRIORITY_ORDER,TAppr.COLUMN_ALIAS"
				+ ",TAppr.CHANNEL_TYPE"
				+ ",TAppr.CHANNEL_TYPE_AT,"+ChannelTypeAtApprDesc+""
				+ " ,TAppr.WEIGHTAGE"
				+ ",TAppr.STATUS_NT, TAppr.STATUS," +statusNtApprDesc
				+ ",TAppr.RECORD_INDICATOR_NT, TAppr.RECORD_INDICATOR, "+RecordIndicatorNtApprDesc
				+ ", TAppr.MAKER, "+makerApprDesc
				+ ", TAppr.VERIFIER,"+verifierApprDesc
				+ ","+dbFunctionFormats("TAPPR.DATE_LAST_MODIFIED","DATETIME_FORMAT", null)+" DATE_LAST_MODIFIED, "+
				""+dbFunctionFormats("TAPPR.DATE_CREATION","DATETIME_FORMAT", null)+" DATE_CREATION "
				+ " from RA_MST_FEES_DEFAULT_PRIORITY TAppr WHERE TAppr.COUNTRY = ? AND TAppr.LE_BOOK = ? AND TAppr.DIMENSION_NAME = ?  ");
		String strQueryPend = new String("Select TPend.COUNTRY"
				+ ",TPend.LE_BOOK"
				+ ",TPend.DIMENSION_NAME"
				+ ",TPend.PRIORITY_ORDER,TPend.COLUMN_ALIAS"
				+ ",TPend.CHANNEL_TYPE"
				+ ",TPend.CHANNEL_TYPE_AT,"+ChannelTypeAtPendDesc+""
				+ ", TPend.WEIGHTAGE"
				+ ",TPend.STATUS_NT, TPend.STATUS," +statusNtPendDesc
				+ ",TPend.RECORD_INDICATOR_NT, TPend.RECORD_INDICATOR, "+RecordIndicatorNtPendDesc
				+ ", TPend.MAKER, "+makerPendDesc
				+ ", TPend.VERIFIER,"+verifierPendDesc
				+ ","+dbFunctionFormats("TPend.DATE_LAST_MODIFIED","DATETIME_FORMAT", null)+" DATE_LAST_MODIFIED, "+
				""+dbFunctionFormats("TPend.DATE_CREATION","DATETIME_FORMAT", null)+" DATE_CREATION "
			+ " From RA_MST_FEES_DEFAULT_PRIORITY_PEND TPend WHERE  TPend.COUNTRY = ? AND TPend.LE_BOOK = ? AND TPend.DIMENSION_NAME = ? ");

		Object objParams[] = new Object[intKeyFieldsCount];
		objParams[0] = dObj.getCountry();
		objParams[1] = dObj.getLeBook();
		objParams[2] = dObj.getDimensionName();

		try{
			if(!dObj.isVerificationRequired()){intStatus =0;}
			if(intStatus == 0){
				//logger.info("Executing approved query");
				collTemp = getJdbcTemplate().query(strQueryAppr.toString(),objParams,getMapper(dObj));
			}else{
				//logger.info("Executing pending query");
				collTemp = getJdbcTemplate().query(strQueryPend.toString(),objParams,getMapper(dObj));
			}
			return collTemp;
		}catch(Exception ex){
				//ex.printStackTrace();
				//logger.error("Error: getQueryResults Exception :   ");
				/*if(intStatus == 0)
					logger.error(((strQueryAppr == null) ? "strQueryAppr is Null" : strQueryAppr.toString()));
				else
					logger.error(((strQueryPend == null) ? "strQueryPend is Null" : strQueryPend.toString()));

				if (objParams != null)
				for(int i=0 ; i< objParams.length; i++)
					logger.error("objParams[" + i + "]" + objParams[i].toString());*/
				return null;
				}
	}
	public List<FeesPriorityVb> getQueryResultsDetail(FeesPriorityVb dObj, int intStatus){
		String effectiveDateAppr = "";
		String effectiveDatePend = "";
		setServiceDefaults();
		List<FeesPriorityVb> collTemp = null;
		if ("ORACLE".equalsIgnoreCase(databaseType)) {
			effectiveDateAppr = "TO_CHAR(TAPPR.EFFECTIVE_DATE,'DD-Mon-RRRR HH24:MI:SS')";
			effectiveDatePend = "TO_CHAR(TPend.EFFECTIVE_DATE,'DD-Mon-RRRR HH24:MI:SS')";
		}else if ("MSSQL".equalsIgnoreCase(databaseType)) {
			effectiveDateAppr = "format(CAST(TAppr.EFFECTIVE_DATE AS DATETIME), 'dd-MMM-yyyy HH:mm:ss')";
			effectiveDatePend = "format(CAST(TPend.EFFECTIVE_DATE AS DATETIME), 'dd-MMM-yyyy HH:mm:ss')";
		}
		/*String dimensionNameAppr = "";
		String dimensionNamePend = "";
		Object objParams[] = null;
		if(ValidationUtil.isValid(dObj.getDimensionName())) {
			dimensionNameAppr = " AND TAppr.DIMENSION_NAME = ? ";
			dimensionNamePend = " AND TPend.DIMENSION_NAME = ? ";
			objParams = new Object[] {dObj.getCountry(),dObj.getLeBook(),dObj.getBusinessLineId(),dObj.getEffectiveDate(),dObj.getDimensionName()};
		} else {
			objParams = new Object[] {dObj.getCountry(),dObj.getLeBook(),dObj.getBusinessLineId(),dObj.getEffectiveDate(),dObj.getDimensionName()};
		}*/
			
		String strQueryAppr = new String("SELECT * FROM ( Select TAppr.COUNTRY"
				+ ",TAppr.LE_BOOK"
				+ ",TAppr.DIMENSION_NAME"
				+ ",TAppr.BUSINESS_LINE_ID"
				+ " ,(select BUSINESS_LINE_DESCRIPTION from RA_MST_BUSINESS_LINE_HEADER T1 "
				+ " where BUSINESS_LINE_STATUS = 0 AND T1.COUNTRY = TAppr.COUNTRY AND T1.LE_BOOK = TAppr.LE_BOOK AND T1.BUSINESS_LINE_ID = TAppr.BUSINESS_LINE_ID ) BUSINESS_LINE_DESCRIPTION "
				+ ","+effectiveDateAppr+" EFFECTIVE_DATE"
				+ ",TAppr.PRIORITY_ORDER,TAppr.COLUMN_ALIAS"
				+ ", TAppr.WEIGHTAGE"
				+ ",TAppr.STATUS_NT, TAppr.STATUS," +statusNtApprDesc
				+ ",TAppr.RECORD_INDICATOR_NT, TAppr.RECORD_INDICATOR, "+RecordIndicatorNtApprDesc
				+ ", TAppr.MAKER, "+makerApprDesc
				+ ", TAppr.VERIFIER,"+verifierApprDesc
				+ ","+dbFunctionFormats("TAPPR.DATE_LAST_MODIFIED","DATETIME_FORMAT", null)+" DATE_LAST_MODIFIED, "+
				""+dbFunctionFormats("TAPPR.DATE_CREATION","DATETIME_FORMAT", null)+" DATE_CREATION "
				+ " from RA_MST_FEES_DETAIL_PRIORITY TAppr WHERE TAppr.COUNTRY = ? AND TAppr.LE_BOOK = ? "
				+ " and TAppr.BUSINESS_LINE_ID = ? AND TAppr.EFFECTIVE_DATE = ? AND TAppr.DIMENSION_NAME = ? ) TAppr ");
		String strQueryPend = new String(" SELECT * FROM ( Select TPend.COUNTRY"
				+ ",TPend.LE_BOOK"
				+ ",TPend.DIMENSION_NAME"
				+ ",TPend.BUSINESS_LINE_ID"
				+ " ,(select BUSINESS_LINE_DESCRIPTION from RA_MST_BUSINESS_LINE_HEADER T1 "
				+ " where BUSINESS_LINE_STATUS = 0 AND T1.COUNTRY = TPend.COUNTRY AND T1.LE_BOOK = TPend.LE_BOOK AND T1.BUSINESS_LINE_ID = TPend.BUSINESS_LINE_ID ) BUSINESS_LINE_DESCRIPTION "
				+ ","+effectiveDatePend+" EFFECTIVE_DATE"
				+ ",TPend.PRIORITY_ORDER,TPend.COLUMN_ALIAS"
				+ ", TPend.WEIGHTAGE"
				+ ",TPend.STATUS_NT, TPend.STATUS," +statusNtPendDesc
				+ ",TPend.RECORD_INDICATOR_NT, TPend.RECORD_INDICATOR, "+RecordIndicatorNtPendDesc
				+ ", TPend.MAKER, "+makerPendDesc
				+ ", TPend.VERIFIER,"+verifierPendDesc
				+ ","+dbFunctionFormats("TPend.DATE_LAST_MODIFIED","DATETIME_FORMAT", null)+" DATE_LAST_MODIFIED, "+
				""+dbFunctionFormats("TPend.DATE_CREATION","DATETIME_FORMAT", null)+" DATE_CREATION "
				+ " from RA_MST_FEES_DETAIL_PRIORITY_PEND TPend WHERE TPend.COUNTRY = ? AND TPend.LE_BOOK = ? "
				+ " and TPend.BUSINESS_LINE_ID = ? AND TPend.EFFECTIVE_DATE = ? AND TPend.DIMENSION_NAME = ? ) TPend ");

		Object[] objParams = {dObj.getCountry(),dObj.getLeBook(),dObj.getBusinessLineId(),dObj.getEffectiveDate(),dObj.getDimensionName()};

		try{
			if(!dObj.isVerificationRequired()){intStatus =0;}
			if(intStatus == 0){
				//logger.info("Executing approved query");
				collTemp = getJdbcTemplate().query(strQueryAppr.toString(),objParams,getDetailMapper(dObj));
			}else{
				//logger.info("Executing pending query");
				collTemp = getJdbcTemplate().query(strQueryPend.toString(),objParams,getDetailMapper(dObj));
			}
			return collTemp;
		}catch(Exception ex){
				/*ex.printStackTrace();
				logger.error("Error: getQueryResults Exception :   ");
				if(intStatus == 0)
					logger.error(((strQueryAppr == null) ? "strQueryAppr is Null" : strQueryAppr.toString()));
				else
					logger.error(((strQueryPend == null) ? "strQueryPend is Null" : strQueryPend.toString()));

				if (objParams != null)
				for(int i=0 ; i< objParams.length; i++)
					logger.error("objParams[" + i + "]" + objParams[i].toString());*/
			return null;
		}
	}
	public List<FeesPriorityVb> getQueryResults(FeesPriorityVb dObj, int intStatus){
		if("DEFAULT".equalsIgnoreCase(dObj.getPriorityType())){
			setServiceDefaults();
			return getQueryResultsDefault(dObj, intStatus);
		}else {
			setServiceDefaults();
			return getQueryResultsDetail(dObj, intStatus);
		}
	}
	
	@Override
	protected List<FeesPriorityVb> selectApprovedRecord(FeesPriorityVb vObject){
		return getQueryResults(vObject, Constants.STATUS_ZERO);
	}


	@Override
	protected List<FeesPriorityVb> doSelectPendingRecord(FeesPriorityVb vObject){
		return getQueryResults(vObject, Constants.STATUS_PENDING);
	}


	@Override
	protected int getStatus(FeesPriorityVb records){return records.getDbStatus();}


	@Override
	protected void setStatus(FeesPriorityVb vObject,int status){vObject.setDbStatus(status);}


	@Override
	protected int doInsertionAppr(FeesPriorityVb vObject){
		String query =	"";
		Object[] args = null;
		if("DEFAULT".equalsIgnoreCase(vObject.getPriorityType())) {
			query = "Insert Into RA_MST_FEES_DEFAULT_PRIORITY (COUNTRY, LE_BOOK, DIMENSION_NAME,"
					+ " PRIORITY_ORDER, CHANNEL_TYPE, WEIGHTAGE,COLUMN_ALIAS,STATUS,RECORD_INDICATOR,"
					+ "MAKER,VERIFIER,DATE_LAST_MODIFIED,DATE_CREATION )"
					+ " Values (?, ?, ?, ?, ?, ?, ?,?,?,?,?,"+getDbFunction("SYSDATE", null)+","+getDbFunction("SYSDATE", null)+")";
			args = new Object[]{vObject.getCountry(), vObject.getLeBook(), vObject.getDimensionName(),
					vObject.getPriorityOrder(), vObject.getChannelType(), vObject.getWeightage(),vObject.getColumnAlias(),
					vObject.getDbStatus(),vObject.getRecordIndicator(),vObject.getMaker(),vObject.getVerifier()};
		} else if("DETAIL".equalsIgnoreCase(vObject.getPriorityType())) {
			query = "Insert Into RA_MST_FEES_DETAIL_PRIORITY (COUNTRY, LE_BOOK, BUSINESS_LINE_ID, EFFECTIVE_DATE,DIMENSION_NAME,"
					+ " PRIORITY_ORDER, WEIGHTAGE,COLUMN_ALIAS,STATUS,RECORD_INDICATOR,"
					+ " MAKER,VERIFIER,DATE_LAST_MODIFIED,DATE_CREATION )"
					+ " Values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, "
					+ ""+getDbFunction("SYSDATE", null)+","+getDbFunction("SYSDATE", null)+")";
			args = new Object[]{vObject.getCountry(), vObject.getLeBook(), vObject.getBusinessLineId(),vObject.getEffectiveDate(),
					vObject.getDimensionName(),vObject.getPriorityOrder(), vObject.getWeightage(),vObject.getColumnAlias(),
					vObject.getDbStatus(),vObject.getRecordIndicator(),vObject.getMaker(),vObject.getVerifier()};
		}
		 return getJdbcTemplate().update(query,args);
	}


	@Override
	protected int doInsertionPend(FeesPriorityVb vObject){
		String query =	"";
		Object[] args = null;
		if("DEFAULT".equalsIgnoreCase(vObject.getPriorityType())) {
			query = "Insert Into RA_MST_FEES_DEFAULT_PRIORITY_PEND (COUNTRY, LE_BOOK, DIMENSION_NAME,"
					+ " PRIORITY_ORDER, CHANNEL_TYPE, WEIGHTAGE,COLUMN_ALIAS,STATUS,RECORD_INDICATOR,"
					+ "MAKER,VERIFIER,DATE_LAST_MODIFIED,DATE_CREATION )"
					+ " Values (?, ?, ?, ?, ?, ?, ?,?,?,?,?,"+getDbFunction("SYSDATE", null)+","+getDbFunction("SYSDATE", null)+")";
			args = new Object[]{vObject.getCountry(), vObject.getLeBook(), vObject.getDimensionName(),
					vObject.getPriorityOrder(), vObject.getChannelType(), vObject.getWeightage(),vObject.getColumnAlias(),
					vObject.getDbStatus(),vObject.getRecordIndicator(),vObject.getMaker(),vObject.getVerifier()};
		} else if("DETAIL".equalsIgnoreCase(vObject.getPriorityType())) {
			query = "Insert Into RA_MST_FEES_DETAIL_PRIORITY_PEND (COUNTRY, LE_BOOK, BUSINESS_LINE_ID, EFFECTIVE_DATE,DIMENSION_NAME,"
					+ " PRIORITY_ORDER, WEIGHTAGE,COLUMN_ALIAS,STATUS,RECORD_INDICATOR,"
					+ " MAKER,VERIFIER,DATE_LAST_MODIFIED,DATE_CREATION )"
					+ " Values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, "
					+ ""+getDbFunction("SYSDATE", null)+","+getDbFunction("SYSDATE", null)+")";
			args = new Object[]{vObject.getCountry(), vObject.getLeBook(), vObject.getBusinessLineId(),vObject.getEffectiveDate(),
					vObject.getDimensionName(),vObject.getPriorityOrder(), vObject.getWeightage(),vObject.getColumnAlias(),
					vObject.getDbStatus(),vObject.getRecordIndicator(),vObject.getMaker(),vObject.getVerifier()};
		}
		return getJdbcTemplate().update(query,args);
	}


	@Override
	protected int doInsertionPendWithDc(FeesPriorityVb vObject){
		String query =	"";
		Object[] args = null;
		if("DEFAULT".equalsIgnoreCase(vObject.getPriorityType())) {
			query = "Insert Into RA_MST_FEES_DEFAULT_PRIORITY_PEND (COUNTRY, LE_BOOK, DIMENSION_NAME,"
					+ " PRIORITY_ORDER, CHANNEL_TYPE, WEIGHTAGE,COLUMN_ALIAS,STATUS,RECORD_INDICATOR,"
					+ "MAKER,VERIFIER,DATE_LAST_MODIFIED,DATE_CREATION )"
					+ " Values (?, ?, ?, ?, ?, ?, ?,?,?,?,?,"+getDbFunction("SYSDATE", null)+","+getDbFunction("DATE_CREATION")+")";
			args = new Object[]{vObject.getCountry(), vObject.getLeBook(), vObject.getDimensionName(),
					vObject.getPriorityOrder(), vObject.getChannelType(), vObject.getWeightage(),vObject.getColumnAlias(),
					vObject.getDbStatus(),vObject.getRecordIndicator(),vObject.getMaker(),vObject.getVerifier(),
					vObject.getDateCreation()};
		} else if("DETAIL".equalsIgnoreCase(vObject.getPriorityType())) {
			query = "Insert Into RA_MST_FEES_DETAIL_PRIORITY_PEND (COUNTRY, LE_BOOK, BUSINESS_LINE_ID, EFFECTIVE_DATE,DIMENSION_NAME,"
					+ " PRIORITY_ORDER, WEIGHTAGE,COLUMN_ALIAS,STATUS,RECORD_INDICATOR,"
					+ " MAKER,VERIFIER,DATE_LAST_MODIFIED,DATE_CREATION )"
					+ " Values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, "
					+ ""+getDbFunction("SYSDATE", null)+","+getDbFunction("DATE_CREATION")+")";
			args = new Object[]{vObject.getCountry(), vObject.getLeBook(), vObject.getBusinessLineId(),vObject.getEffectiveDate(),
					vObject.getDimensionName(),vObject.getPriorityOrder(), vObject.getWeightage(),vObject.getColumnAlias(),
					vObject.getDbStatus(),vObject.getRecordIndicator(),vObject.getMaker(),vObject.getVerifier(),
					vObject.getDateCreation()};
		}
		return getJdbcTemplate().update(query,args);
	}


	@Override
	protected int doUpdateAppr(FeesPriorityVb vObject){
		String query =	"";
		Object[] args = null;
		if ("DEFAULT".equalsIgnoreCase(vObject.getPriorityType())) {
			query = "Update RA_MST_FEES_DEFAULT_PRIORITY Set  "
					+ " PRIORITY_ORDER = ?, CHANNEL_TYPE = ?, WEIGHTAGE = ?,COLUMN_ALIAS = ?,"
					+ "STATUS = ?,RECORD_INDICATOR = ?,MAKER = ?,VERIFIER = ?,DATE_LAST_MODIFIED = "
					+ getDbFunction("SYSDATE") + " WHERE COUNTRY = ? AND LE_BOOK = ? AND DIMENSION_NAME = ? ";
			args = new Object[]{ vObject.getPriorityOrder(), vObject.getChannelType(), vObject.getWeightage(),
					vObject.getColumnAlias(), vObject.getDbStatus(), vObject.getRecordIndicator(), vObject.getMaker(),
					vObject.getVerifier(), vObject.getCountry(), vObject.getLeBook(), vObject.getDimensionName() };
		} else if("DETAIL".equalsIgnoreCase(vObject.getPriorityType())) {
			query = "Update RA_MST_FEES_DETAIL_PRIORITY Set  "
					+ " PRIORITY_ORDER = ?, WEIGHTAGE = ?, COLUMN_ALIAS = ?,"
					+ " STATUS = ?,RECORD_INDICATOR = ?,MAKER = ?,VERIFIER = ?,DATE_LAST_MODIFIED = "
					+ getDbFunction("SYSDATE") + " WHERE COUNTRY = ? AND LE_BOOK = ? AND DIMENSION_NAME = ? AND "
					+ " BUSINESS_LINE_ID = ? AND EFFECTIVE_DATE = ? ";
			args = new Object[]{ vObject.getPriorityOrder(), vObject.getWeightage(),
					vObject.getColumnAlias(), vObject.getDbStatus(), vObject.getRecordIndicator(), vObject.getMaker(),
					vObject.getVerifier(), vObject.getCountry(), vObject.getLeBook(), vObject.getDimensionName(),
					vObject.getBusinessLineId(),vObject.getEffectiveDate()};
		}
		return getJdbcTemplate().update(query,args);
	}


	@Override
	protected int doUpdatePend(FeesPriorityVb vObject){
		String query =	"";
		Object[] args = null;
		if ("DEFAULT".equalsIgnoreCase(vObject.getPriorityType())) {
			query = "Update RA_MST_FEES_DEFAULT_PRIORITY_PEND Set  "
					+ " PRIORITY_ORDER = ?, CHANNEL_TYPE = ?, WEIGHTAGE = ?,COLUMN_ALIAS = ?,"
					+ "STATUS = ?,RECORD_INDICATOR = ?,MAKER = ?,VERIFIER = ?,DATE_LAST_MODIFIED = "
					+ getDbFunction("SYSDATE") + " WHERE COUNTRY = ? AND LE_BOOK = ? AND DIMENSION_NAME = ? ";
			args = new Object[]{ vObject.getPriorityOrder(), vObject.getChannelType(), vObject.getWeightage(),
					vObject.getColumnAlias(), vObject.getDbStatus(), vObject.getRecordIndicator(), vObject.getMaker(),
					vObject.getVerifier(), vObject.getCountry(), vObject.getLeBook(), vObject.getDimensionName() };
		} else if("DETAIL".equalsIgnoreCase(vObject.getPriorityType())) {
			query = "Update RA_MST_FEES_DETAIL_PRIORITY_PEND Set  "
					+ " PRIORITY_ORDER = ?, WEIGHTAGE = ?, COLUMN_ALIAS = ?,"
					+ " STATUS = ?,RECORD_INDICATOR = ?,MAKER = ?,VERIFIER = ?,DATE_LAST_MODIFIED = "
					+ getDbFunction("SYSDATE") + " WHERE COUNTRY = ? AND LE_BOOK = ? AND DIMENSION_NAME = ? AND "
					+ " BUSINESS_LINE_ID = ? AND EFFECTIVE_DATE = ? ";
			args = new Object[]{ vObject.getPriorityOrder(), vObject.getWeightage(),
					vObject.getColumnAlias(), vObject.getDbStatus(), vObject.getRecordIndicator(), vObject.getMaker(),
					vObject.getVerifier(), vObject.getCountry(), vObject.getLeBook(), vObject.getDimensionName(),
					vObject.getBusinessLineId(),vObject.getEffectiveDate()};
		}
		return getJdbcTemplate().update(query,args);
	}


	@Override
	protected int doDeleteAppr(FeesPriorityVb vObject){
		String query =	"";
		Object[] args = null;
		if ("DEFAULT".equalsIgnoreCase(vObject.getPriorityType())) {
			query = "Delete From RA_MST_FEES_DEFAULT_PRIORITY  "
					+ " WHERE COUNTRY = ? AND LE_BOOK = ? AND DIMENSION_NAME = ? ";
			args = new Object[]{vObject.getCountry(), vObject.getLeBook(), vObject.getDimensionName()};
		} else if ("DETAIL".equalsIgnoreCase(vObject.getPriorityType())) {
			query = "Delete From RA_MST_FEES_DETAIL_PRIORITY  "
					+ " WHERE COUNTRY = ? AND LE_BOOK = ? AND DIMENSION_NAME = ? "
					+ " AND BUSINESS_LINE_ID = ? AND EFFECTIVE_DATE = ?";
			args = new Object[]{vObject.getCountry(), vObject.getLeBook(), vObject.getDimensionName(),
					vObject.getBusinessLineId(),vObject.getEffectiveDate()};
		}
		return getJdbcTemplate().update(query,args);
	}


	@Override
	protected int deletePendingRecord(FeesPriorityVb vObject){
		String query =	"";
		Object[] args = null;
		if ("DEFAULT".equalsIgnoreCase(vObject.getPriorityType())) {
			query = "Delete From RA_MST_FEES_DEFAULT_PRIORITY_PEND  "
					+ " WHERE COUNTRY = ? AND LE_BOOK = ? AND DIMENSION_NAME = ? ";
			args = new Object[]{vObject.getCountry(), vObject.getLeBook(), vObject.getDimensionName()};
		} else if ("DETAIL".equalsIgnoreCase(vObject.getPriorityType())) {
			query = "Delete From RA_MST_FEES_DETAIL_PRIORITY_PEND  "
					+ " WHERE COUNTRY = ? AND LE_BOOK = ? AND DIMENSION_NAME = ? "
					+ " AND BUSINESS_LINE_ID = ? AND EFFECTIVE_DATE = ?";
			args = new Object[]{vObject.getCountry(), vObject.getLeBook(), vObject.getDimensionName(),
					vObject.getBusinessLineId(),vObject.getEffectiveDate()};
		}
		return getJdbcTemplate().update(query,args);
	}


	@Override
	protected String getAuditString(FeesPriorityVb vObject){
		final String auditDelimiter = vObject.getAuditDelimiter();
		final String auditDelimiterColVal = vObject.getAuditDelimiterColVal();
		StringBuffer strAudit = new StringBuffer("");
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
			
			if(ValidationUtil.isValid(vObject.getLeBook()))
				strAudit.append("BUSINESS_LINE_ID"+auditDelimiterColVal+vObject.getBusinessLineId().trim());
			else
				strAudit.append("BUSINESS_LINE_ID"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			
			if(ValidationUtil.isValid(vObject.getEffectiveDate()))
				strAudit.append("EFFECTIVE_DATE"+auditDelimiterColVal+vObject.getEffectiveDate());
			else
				strAudit.append("EFFECTIVE_DATE"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);

			if(ValidationUtil.isValid(vObject.getDimensionName()))
				strAudit.append("DIMENSION_NAME"+auditDelimiterColVal+vObject.getDimensionName().trim());
			else
				strAudit.append("DIMENSION_NAME"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);

			strAudit.append("PRIORITY_ORDER"+auditDelimiterColVal+vObject.getPriorityOrder());
			strAudit.append(auditDelimiter);

			strAudit.append("WEIGHTAGE"+auditDelimiterColVal+vObject.getWeightage());
			strAudit.append(auditDelimiter);
			
			strAudit.append("STATUS_NT"+auditDelimiterColVal+vObject.getStatusNt());
			strAudit.append(auditDelimiter);
			
			strAudit.append("STATUS"+auditDelimiterColVal+vObject.getDbStatus());
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

		return strAudit.toString();
		}

	@Override
	protected void setServiceDefaults(){
		serviceName = "Fees Priority";
		serviceDesc = "Fees Priority";
		tableName = "RA_MST_FEES_DEFAULT_PRIORITY";
		childTableName = "RA_MST_FEES_DEFAULT_PRIORITY";
		intCurrentUserId = CustomContextHolder.getContext().getVisionId();
		
	}
	public List<FeesPriorityVb> getEffectiveDateList(FeesPriorityVb dObj){
		Vector<Object> params = new Vector<Object>();
		StringBuffer strBufApprove = new StringBuffer("select * from ( SELECT DISTINCT "+dbFunctionFormats("TAPPR.EFFECTIVE_DATE","DATETIME_FORMAT", null)+" EFFECTIVE_DATE_FORMAT,EFFECTIVE_DATE "
						+ " FROM RA_MST_FEES_DETAIL_PRIORITY TAppr              "
						+ " WHERE TAppr.COUNTRY = ?                             "
						+ "   AND TAppr.LE_BOOK = ?                             "
						+ "   AND TAppr.BUSINESS_LINE_ID = ?                   )TAPPR ");
		StringBuffer strBufPend = new StringBuffer("select * from ( SELECT DISTINCT "+dbFunctionFormats("TPEND.EFFECTIVE_DATE","DATETIME_FORMAT", null)+" EFFECTIVE_DATE_FORMAT,EFFECTIVE_DATE "
				+ " FROM RA_MST_FEES_DETAIL_PRIORITY_PEND TPend              "
				+ " WHERE TPend.COUNTRY = ?                             "
				+ "   AND TPend.LE_BOOK = ?                             "
				+ "   AND TPend.BUSINESS_LINE_ID = ?                   )TPend ");
		
		params.addElement(dObj.getCountry());
		params.addElement(dObj.getLeBook());
		params.addElement(dObj.getBusinessLineId());
		try
			{
			if (dObj.getSmartSearchOpt() != null && dObj.getSmartSearchOpt().size() > 0) {
				for (SmartSearchVb data: dObj.getSmartSearchOpt()){
					switch (data.getObject()) {
						case "effectiveDate":
							CommonUtils.addToQuerySearch(getEffectiveDateFilterQuery("TAppr.EFFECTIVE_DATE",data.getValue()), strBufApprove,data.getJoinType());
							CommonUtils.addToQuerySearch(getEffectiveDateFilterQuery("TPend.EFFECTIVE_DATE",data.getValue()), strBufApprove,data.getJoinType());
							break;
					}
					break;
				}
			}
			String orderBy=" Order By  EFFECTIVE_DATE ";
			return getQueryPopupResults(dObj,strBufPend, strBufApprove, "", orderBy, params,getEffectiveDateMapper());

			}catch(Exception ex){
				/*ex.printStackTrace();
				logger.error(((strBufApprove==null)? "strBufApprove is Null":strBufApprove.toString()));

				if (params != null)
					for(int i=0 ; i< params.size(); i++)
						logger.error("objParams[" + i + "]" + params.get(i).toString());*/
				return null;

			}
	}
	protected RowMapper getEffectiveDateMapper(){
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				FeesPriorityVb vObject = new FeesPriorityVb();
				vObject.setEffectiveDate(rs.getString("EFFECTIVE_DATE_FORMAT"));
				return vObject;
			}
		};
		return mapper;
	}

	public List getPriorityDimensions() {
		List collTemp = new ArrayList<String>();
		try {
			String sql = "SELECT DIMENSION_NAME,COLUMN_ALIAS_ENABLE FROM RA_MST_FEES_PRIORITY_DIMENSIONS WHERE STATUS = 0 "
					+ " ORDER BY DIMENSION_NAME ";
			collTemp = getJdbcTemplate().query(sql, new ResultSetExtractor<List<LinkedHashMap<String, String>>>() {
				@Override
				public List<LinkedHashMap<String, String>> extractData(ResultSet rs) throws SQLException, DataAccessException {
					List<LinkedHashMap<String, String>> result = new ArrayList<>();
					ResultSetMetaData metaData = rs.getMetaData();
					while (rs.next()) {
						LinkedHashMap<String, String> resultMap = new LinkedHashMap<>();
						int colCount = metaData.getColumnCount();
						for (int cn = 1; cn <= colCount; cn++) {
							String columnName = metaData.getColumnName(cn);
							resultMap.put(columnName.toUpperCase(), rs.getString(columnName));
						}
						result.add(resultMap);
					}
					return result;
				}
			});
		} catch (Exception e) {
		}
		return collTemp;
	}
	public String getEffectiveDateFilterQuery(String columnName, String value) {
		String filterQuery = "";
		try {
			filterQuery = " ( Format(CAST("+columnName+" AS datetime2), 'dd-MMM-yyyy HH:mm:ss') = Format(CAST('"+value+"' AS datetime2), 'dd-MMM-yyyy HH:mm:ss')  OR "+
							" Format(CAST("+columnName+" AS datetime2), 'dd-MMM-yyyy HH:mm:ss') = Format(CAST(CONVERT(DATETIME,'"+value+"', 105) AS datetime2), 'dd-MMM-yyyy HH:mm:ss') OR "+
							" Format(CAST("+columnName+" AS datetime2), 'dd-MMM-yyyy HH:mm:ss') = Format(CAST(CONVERT(DATETIME,'"+value+"', 120) AS datetime2), 'dd-MMM-yyyy HH:mm:ss') OR "+
							" Format(CAST("+columnName+" AS datetime2), 'dd-MMM-yyyy') = Format(CAST(CONVERT(DATETIME,'"+value+"', 120) AS datetime2), 'dd-MMM-yyyy') OR "+
							" Format(CAST("+columnName+" AS datetime2), 'dd-MMM-yyyy') = Format(CAST(CONVERT(DATETIME,'"+value+"', 105) AS datetime2), 'dd-MMM-yyyy') ) ";
		} catch (Exception e) {
		}
		return filterQuery;
	}
	@SuppressWarnings("deprecation")
	public List getMaxEffectiveDatePriorityList(FeesPriorityVb dObj) {
		List<FeesPriorityVb> collTemp = new ArrayList<FeesPriorityVb>();
		try {
			String sql = " SELECT 																			"
					+ "   *                                                                             "
					+ " FROM                                                                            "
					+ "   (                                                                             "
					+ "     SELECT                                                                      "
					+ "       ISNULL(TT1.DIMENSION_NAME, TT2.DIMENSION_NAME) DIMENSION_NAME,            "
					+ "       ISNULL(TT1.COLUMN_ALIAS, TT2.COLUMN_ALIAS) COLUMN_ALIAS,                  "
					+ " 	  ISNULL(TT1.COUNTRY, TT2.COUNTRY) COUNTRY,                                   "
					+ "       ISNULL(TT1.LE_BOOK, TT2.LE_BOOK) LE_BOOK ,                              "
					+ " 	  ISNULL(TT1.PRIORITY_ORDER, TT2.PRIORITY_ORDER) PRIORITY_ORDER,          "
					+ " 	  ISNULL(TT1.WEIGHTAGE, TT2.WEIGHTAGE) WEIGHTAGE,                         "
					+ " 	  ISNULL(TT1.STATUS, TT2.STATUS) STATUS                                  "
					+ "     FROM                                                                      "
					+ "       (                                                                       "
					+ "         SELECT                                                                "
					+ "           T1.COUNTRY,                                                         "
					+ "           T1.LE_BOOK,                                                         "
					+ "           T1.DIMENSION_NAME DIMENSION_NAME,                                   "
					+ "           T1.COLUMN_ALIAS COLUMN_ALIAS,                                       "
					+ " 		  T1.PRIORITY_ORDER PRIORITY_ORDER,                                   "
					+ " 		  T1.WEIGHTAGE,                                                       "
					+ " 		  T1.STATUS                                                          "
					+ "         FROM                                                                  "
					+ "           RA_MST_FEES_DETAIL_PRIORITY T1                                      "
					+ "         WHERE                                                                 "
					+ "           T1.COUNTRY = ?                                                      "
					+ "           AND T1.LE_BOOK = ?                                                  "
					+ "           AND T1.BUSINESS_LINE_ID = ?                                         "
					+ "           and t1.EFFECTIVE_DATE = (                                           "
					+ "             select                                                            "
					+ "               max(H1.EFFECTIVE_DATE)                                          "
					+ "             from                                                              "
					+ "               RA_MST_FEES_DETAIL_PRIORITY H1                                  "
					+ "             WHERE                                                             "
					+ "               H1.COUNTRY = t1.country                                         "
					+ "               AND H1.LE_BOOK = t1.le_book                                     "
					+ "               AND H1.BUSINESS_LINE_ID = t1.BUSINESS_LINE_ID                   "
					//+ "               and H1.DIMENSION_NAME = t1.DIMENSION_NAME                       "
					+ "               and H1.COUNTRY = ?                                         		"
					+ "               AND H1.LE_BOOK = ?                                     		"
					+ "               AND H1.BUSINESS_LINE_ID = ?                                     "
					+ "           )                                                                   "
					+ "       ) TT1 FULL                                                              "
					+ "       OUTER JOIN RA_MST_FEES_DEFAULT_PRIORITY TT2 ON (                        "
					+ "         TT1.COUNTRY = TT2.COUNTRY                                             "
					+ "         AND TT1.LE_BOOK = TT2.LE_BOOK                                         "
					+ "         AND TT1.DIMENSION_NAME = TT2.DIMENSION_NAME                           "
					+ "       )                                                                       "
					+ "   ) S1                                                                        "
					+ " WHERE                                                                         "
					+ "   S1.COUNTRY = ?                                                              "
					+ "   AND  S1.LE_BOOK = ?    "
					+ "	  and S1.STATUS = 0			                                                   "
					+ " ORDER BY                                                                      "
					+ "   PRIORITY_ORDER                                                              ";
			Object args[] = {dObj.getCountry(),dObj.getLeBook(),dObj.getBusinessLineId(),
					dObj.getCountry(),dObj.getLeBook(),dObj.getBusinessLineId(),dObj.getCountry(),dObj.getLeBook()};
			RowMapper mapper = new RowMapper() {
				public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
					FeesPriorityVb vObject = new FeesPriorityVb();
					vObject.setDimensionName(rs.getString("DIMENSION_NAME"));
					vObject.setColumnAlias(rs.getString("COLUMN_ALIAS") );
					vObject.setCountry(rs.getString("COUNTRY") );
					vObject.setLeBook(rs.getString("LE_BOOK") );
					vObject.setPriorityOrder(rs.getInt("PRIORITY_ORDER") );
					vObject.setWeightage(rs.getInt("WEIGHTAGE") );
					return vObject;
				}
			};
			collTemp = getJdbcTemplate().query(sql,args, mapper);
		} catch (Exception e) {
		}
		return collTemp;
	}
	public String getDefaultBusLineId(String country,String leBook) {
		String sql = "";
		try {
			if("ORACLE".equalsIgnoreCase(databaseType)) {
				sql = " select * from (Select BUSINESS_LINE_ID "
						+ " from RA_MST_BUSINESS_LINE_HEADER where BUSINESS_LINE_STATUS = 0 "
						+ " and COUNTRY = ? AND LE_BOOK = ? "
						+ " ORDER BY BUSINESS_LINE_ID ) WHERE ROWNUM = 1";
			} else if("MSSQL".equalsIgnoreCase(databaseType)) {
				sql = " Select TOP 1.BUSINESS_LINE_ID "
						+ " from RA_MST_BUSINESS_LINE_HEADER where BUSINESS_LINE_STATUS = 0 "
						+ " and COUNTRY = ? AND LE_BOOK = ? "
						+ " ORDER BY BUSINESS_LINE_ID ";
			}
			Object[] args = new Object[]{country,leBook};
			return getJdbcTemplate().queryForObject(sql, args,String.class);
		} catch(Exception e) {
			return "";
		}
		
	}
}
