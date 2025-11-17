package com.vision.dao;
/**
 * @author Prabu.CJ
 *
 */
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.vision.authentication.CustomContextHolder;
import com.vision.util.CommonUtils;
import com.vision.util.Constants;
import com.vision.util.ValidationUtil;
import com.vision.vb.SmartSearchVb;
import com.vision.vb.VisionVariablesVb;

@Component
public class VisionVariablesDao extends AbstractDao<VisionVariablesVb> {
	
	@Value("${app.databaseType}")
	private String databaseType;
	String VariableStatusNtApprDesc = ValidationUtil.numAlphaTabDescritpionQuery("NT", 1, "TAppr.VARIABLE_STATUS", "VARIABLE_STATUS_DESC");
	String VariableStatusNtPendDesc = ValidationUtil.numAlphaTabDescritpionQuery("NT", 1, "TPend.VARIABLE_STATUS", "VARIABLE_STATUS_DESC");

	String RecordIndicatorNtApprDesc = ValidationUtil.numAlphaTabDescritpionQuery("NT", 7, "TAppr.RECORD_INDICATOR", "RECORD_INDICATOR_DESC");
	String RecordIndicatorNtPendDesc = ValidationUtil.numAlphaTabDescritpionQuery("NT", 7, "TPend.RECORD_INDICATOR", "RECORD_INDICATOR_DESC");
	
	String VvCategoryAtApprDesc = ValidationUtil.numAlphaTabDescritpionQuery("AT", 8, "TAppr.VV_CATEGORY", "VV_CATEGORY_DESC");
	String VvCategoryAtPendDesc = ValidationUtil.numAlphaTabDescritpionQuery("AT", 8, "TPend.VV_CATEGORY", "VV_CATEGORY_DESC");
	
	
	@Override
	protected RowMapper getMapper(){
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				VisionVariablesVb visionVariablesVb = new VisionVariablesVb();
				visionVariablesVb.setVariable(rs.getString("VARIABLE"));
				visionVariablesVb.setValue(rs.getString("VALUE"));
				visionVariablesVb.setVariableStatusNt(rs.getInt("VARIABLE_STATUS_NT"));
				visionVariablesVb.setVariableStatus(rs.getInt("VARIABLE_STATUS"));
				visionVariablesVb.setDbStatus(rs.getInt("VARIABLE_STATUS"));
				visionVariablesVb.setStatusDesc(rs.getString("VARIABLE_STATUS_DESC"));
				visionVariablesVb.setRecordIndicatorNt(rs.getInt("RECORD_INDICATOR_NT"));
				visionVariablesVb.setRecordIndicator(rs.getInt("RECORD_INDICATOR"));
				visionVariablesVb.setRecordIndicatorDesc(rs.getString("RECORD_INDICATOR_DESC"));
				visionVariablesVb.setMaker(rs.getLong("MAKER"));
				visionVariablesVb.setVerifier(rs.getLong("VERIFIER"));
				visionVariablesVb.setInternalStatus(rs.getInt("INTERNAL_STATUS"));
				visionVariablesVb.setDateCreation(rs.getString("DATE_CREATION"));
				visionVariablesVb.setDateLastModified(rs.getString("DATE_LAST_MODIFIED"));
				visionVariablesVb.setReadOnly(rs.getString("READ_ONLY"));
				visionVariablesVb.setVvCategoryAt(rs.getInt("VV_CATEGORY_AT"));
				if(rs.getString("VV_CATEGORY")!= null){ 
					visionVariablesVb.setVvCategory(rs.getString("VV_CATEGORY"));
				}else{
					visionVariablesVb.setVvCategory("");
				}
				if(rs.getString("VV_CATEGORY_DESC")!= null){ 
					visionVariablesVb.setCategoryDescription(rs.getString("VV_CATEGORY_DESC"));
				}else{
					visionVariablesVb.setCategoryDescription("");
				}
				
				if(rs.getString("MAKER_NAME")!= null){ 
					visionVariablesVb.setMakerName(rs.getString("MAKER_NAME"));
				}
				
				if(rs.getString("VERIFIER_NAME")!= null){ 
					visionVariablesVb.setVerifierName(rs.getString("VERIFIER_NAME"));
				}
				
				return visionVariablesVb;
			}
		};
		return mapper;
	}
	
	public List<VisionVariablesVb> getQueryPopupResults(VisionVariablesVb dObj){
		Vector<Object> params = new Vector<Object>();
		StringBuffer strBufApprove = new StringBuffer("select * from ( Select TAppr.VARIABLE," +
			"TAppr.VALUE, TAppr.VARIABLE_STATUS_NT, TAppr.VARIABLE_STATUS," +VariableStatusNtApprDesc+
			",TAppr.RECORD_INDICATOR_NT, TAppr.RECORD_INDICATOR, "+RecordIndicatorNtApprDesc
			+ ", TAppr.MAKER, "+makerApprDesc
			+ ", TAppr.VERIFIER,"+verifierApprDesc
			+ ", TAppr.INTERNAL_STATUS, " +
			""+dbFunctionFormats("TAPPR.DATE_LAST_MODIFIED","DATETIME_FORMAT", null)+" DATE_LAST_MODIFIED, "+
			""+dbFunctionFormats("TAPPR.DATE_CREATION","DATETIME_FORMAT", null)+" DATE_CREATION, "
			+ "TAppr.READ_ONLY, TAppr.VV_CATEGORY_AT, TAppr.VV_CATEGORY, " +VvCategoryAtApprDesc+
			" From VISION_VARIABLES TAppr ) TAppr ");
		String strWhereNotExists = new String( " Not Exists (Select 'X' From VISION_VARIABLES_PEND TPend Where TPend.VARIABLE = TAppr.VARIABLE)");
		StringBuffer strBufPending = new StringBuffer("Select * from (  Select TPend.VARIABLE, TPend.VALUE, TPend.VARIABLE_STATUS_NT, TPend.VARIABLE_STATUS, " +VariableStatusNtPendDesc+
			", TPend.RECORD_INDICATOR_NT, TPend.RECORD_INDICATOR, "+RecordIndicatorNtPendDesc
			+ ", TPend.MAKER,"+makerPendDesc
			+ ", TPend.VERIFIER,"+verifierPendDesc
			+ ", TPend.INTERNAL_STATUS, " +
			""+dbFunctionFormats("TPend.DATE_LAST_MODIFIED","DATETIME_FORMAT", null)+" DATE_LAST_MODIFIED, "+
			""+dbFunctionFormats("TPend.DATE_CREATION","DATETIME_FORMAT", null)+" DATE_CREATION, "
			+ " READ_ONLY, TPend.VV_CATEGORY_AT, TPend.VV_CATEGORY, " +VvCategoryAtPendDesc+
			" From VISION_VARIABLES_PEND TPend ) TPend ");
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
					case "variable":
						CommonUtils.addToQuerySearch(" upper(TAppr.VARIABLE) "+ val, strBufApprove, data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.VARIABLE) "+ val, strBufPending, data.getJoinType());
						break;

					case "value":
						CommonUtils.addToQuerySearch(" upper(TAppr.VALUE) "+ val, strBufApprove, data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.VALUE) "+ val, strBufPending, data.getJoinType());
						break;

					case "readOnly":
						CommonUtils.addToQuerySearch(" upper(TAppr.READ_ONLY) "+ val, strBufApprove, data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.READ_ONLY) "+ val, strBufPending, data.getJoinType());
						break;

					case "variableStatus":
						CommonUtils.addToQuerySearch(" upper(TAppr.VARIABLE_STATUS_DESC) "+ val, strBufApprove, data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.VARIABLE_STATUS_DESC) "+ val, strBufPending, data.getJoinType());
						break;

					case "recordIndicatorDesc":
						CommonUtils.addToQuerySearch(" upper(TAppr.RECORD_INDICATOR_DESC) "+ val, strBufApprove, data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.RECORD_INDICATOR_DESC) "+ val, strBufPending, data.getJoinType());
						break;

					case "vvCategory":
						CommonUtils.addToQuerySearch(" upper(TAppr.VV_CATEGORY_DESC) "+ val, strBufApprove, data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.VV_CATEGORY_DESC) "+ val, strBufPending, data.getJoinType());
						break;

					case "shortDescription":
						CommonUtils.addToQuerySearch(" upper(TAppr.SHORT_DESCRIPTION) "+ val, strBufApprove, data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.SHORT_DESCRIPTION) "+ val, strBufPending, data.getJoinType());
						break;

						default:
					}
					count++;
				}
			}
			String orderBy=" Order By VARIABLE ";
			return getQueryPopupResults(dObj,strBufPending, strBufApprove, strWhereNotExists, orderBy, params);
			
		}catch(Exception ex){
			
			ex.printStackTrace();
			logger.error(((strBufApprove==null)? "strBufApprove is Null":strBufApprove.toString()));
			logger.error("UNION");
			logger.error(((strBufPending==null)? "strBufPending is Null":strBufPending.toString()));

			if (params != null)
				for(int i=0 ; i< params.size(); i++)
					logger.error("objParams[" + i + "]" + params.get(i).toString());
			return null;

		}
	}
	public List<VisionVariablesVb> getQueryResults(VisionVariablesVb dObj, int intStatus){
		setServiceDefaults();
		List<VisionVariablesVb> collTemp = null;
		final int intKeyFieldsCount = 1;

		String strQueryAppr = new String("Select TAppr.VARIABLE," +
			"TAppr.VALUE, TAppr.VARIABLE_STATUS_NT, TAppr.VARIABLE_STATUS," +VariableStatusNtApprDesc+
			", TAppr.RECORD_INDICATOR_NT, TAppr.RECORD_INDICATOR," +RecordIndicatorNtApprDesc+
			", TAppr.MAKER, "+makerApprDesc
			+ ", TAppr.VERIFIER, "+verifierApprDesc
			+ ", TAppr.INTERNAL_STATUS," +
			""+dbFunctionFormats("TAPPR.DATE_LAST_MODIFIED","DATETIME_FORMAT", null)+" DATE_LAST_MODIFIED, "+
			""+dbFunctionFormats("TAPPR.DATE_CREATION","DATETIME_FORMAT", null)+" DATE_CREATION, "
			+ "READ_ONLY"
			+ ", VV_CATEGORY_AT, VV_CATEGORY, " +VvCategoryAtApprDesc+
			" From VISION_VARIABLES TAppr " + 
			"Where TAppr.VARIABLE = ?");
		String strQueryPend = new String("Select TPend.VARIABLE," +
			"TPend.VALUE, TPend.VARIABLE_STATUS_NT, TPend.VARIABLE_STATUS," +VariableStatusNtPendDesc+
			", TPend.RECORD_INDICATOR_NT, TPend.RECORD_INDICATOR," +RecordIndicatorNtPendDesc+
			", TPend.MAKER, "+makerPendDesc
			+ ", TPend.VERIFIER, "+verifierPendDesc
			+ ", TPend.INTERNAL_STATUS," +
			""+dbFunctionFormats("TPend.DATE_LAST_MODIFIED","DATETIME_FORMAT", null)+" DATE_LAST_MODIFIED, "+
			""+dbFunctionFormats("TPend.DATE_CREATION","DATETIME_FORMAT", null)+" DATE_CREATION, "
			+ "READ_ONLY" +
			", VV_CATEGORY_AT, VV_CATEGORY, " +VvCategoryAtPendDesc+
			" From VISION_VARIABLES_PEND TPend " + 
		"Where TPend.VARIABLE = ?");

		Object objParams[] = new Object[intKeyFieldsCount];
		objParams[0] = new String(dObj.getVariable());//[VARIABLE]

		try
		{if(!dObj.isVerificationRequired()){intStatus =0;}
			if(intStatus == 0)
			{
				logger.info("Executing approved query");
				collTemp = getJdbcTemplate().query(strQueryAppr.toString(),objParams,getMapper());
			}else{
				logger.info("Executing pending query");
				collTemp = getJdbcTemplate().query(strQueryPend.toString(),objParams,getMapper());
			}
			return collTemp;
		}catch(Exception ex){
			ex.printStackTrace();
			logger.error("Error: getQueryResults Exception :   ");
			if(intStatus == 0)
				logger.error(((strQueryAppr == null) ? "strQueryAppr is Null" : strQueryAppr.toString()));
			else
				logger.error(((strQueryPend == null) ? "strQueryPend is Null" : strQueryPend.toString()));

			if (objParams != null)
				for(int i=0 ; i< objParams.length; i++)
					logger.error("objParams[" + i + "]" + objParams[i].toString());
			return null;
		}
	}
	@Override
	protected List<VisionVariablesVb> selectApprovedRecord(VisionVariablesVb vObject){
		return getQueryResults(vObject, Constants.STATUS_ZERO);
	}
	@Override
	protected List<VisionVariablesVb> doSelectPendingRecord(VisionVariablesVb vObject){
		return getQueryResults(vObject, Constants.STATUS_PENDING);
	}
	@Override
	protected int getStatus(VisionVariablesVb records){return records.getVariableStatus();}
	@Override
	protected void setStatus(VisionVariablesVb vObject,int status){vObject.setVariableStatus(status);}
	@Override
	protected int doInsertionAppr(VisionVariablesVb vObject){
		String query = "Insert Into VISION_VARIABLES ( VARIABLE, VALUE, VARIABLE_STATUS_NT, VARIABLE_STATUS,"+
			"RECORD_INDICATOR_NT, RECORD_INDICATOR, MAKER, VERIFIER, INTERNAL_STATUS, DATE_LAST_MODIFIED, DATE_CREATION, VV_CATEGORY)"+
			"Values (?, ?, ?, ?, ?, ?, ?, ?, ?, "+getDbFunction("SYSDATE")+", "+getDbFunction("SYSDATE")+", ?)";
		Object[] args = {vObject.getVariable(),vObject.getValue(),vObject.getVariableStatusNt(),vObject.getVariableStatus(),vObject.getRecordIndicatorNt(),vObject.getRecordIndicator()
			,vObject.getMaker(),
			vObject.getVerifier(), vObject.getInternalStatus(), vObject.getVvCategory()};  
		return getJdbcTemplate().update(query,args);
	}
	@Override
	protected int doInsertionPend(VisionVariablesVb vObject){
		String query = "Insert Into VISION_VARIABLES_PEND ( VARIABLE, VALUE, VARIABLE_STATUS_NT, "+
			"VARIABLE_STATUS, RECORD_INDICATOR_NT, RECORD_INDICATOR, MAKER, VERIFIER, INTERNAL_STATUS, DATE_LAST_MODIFIED, DATE_CREATION, VV_CATEGORY) "+
			"Values (?, ?, ?, ?, ?, ?, ?, ?, ?, "+getDbFunction("SYSDATE")+", "+getDbFunction("SYSDATE")+", ?)";
		Object[] args = {vObject.getVariable(),vObject.getValue(),vObject.getVariableStatusNt(),vObject.getVariableStatus(),
			vObject.getRecordIndicatorNt(),vObject.getRecordIndicator(),vObject.getMaker(),
			vObject.getVerifier(), vObject.getInternalStatus(), vObject.getVvCategory()};  
		return getJdbcTemplate().update(query,args);	
	}
	@Override
	protected int doInsertionPendWithDc(VisionVariablesVb vObject){
		String query = "Insert Into VISION_VARIABLES_PEND ( VARIABLE, VALUE, VARIABLE_STATUS_NT,"+
			"VARIABLE_STATUS, RECORD_INDICATOR_NT, RECORD_INDICATOR, MAKER, VERIFIER, INTERNAL_STATUS, DATE_LAST_MODIFIED, DATE_CREATION, VV_CATEGORY) "+
			"Values (?, ?, ?, ?, ?, ?, ?, ?, ?, "+getDbFunction("SYSDATE")+", "+getDbFunction("DATE_CREATION")+", ?)";
		Object[] args = {vObject.getVariable(),vObject.getValue(),vObject.getVariableStatusNt(),vObject.getVariableStatus(), 
			vObject.getRecordIndicatorNt(),vObject.getRecordIndicator(),vObject.getMaker(),
			vObject.getVerifier(), vObject.getInternalStatus(),vObject.getDateCreation(), vObject.getVvCategory()};  
		return getJdbcTemplate().update(query,args);
	}
	@Override
	protected int doUpdateAppr(VisionVariablesVb vObject){
		String query = "Update VISION_VARIABLES Set VALUE = ?, VARIABLE_STATUS_NT = ?, VARIABLE_STATUS = ?, "+
			"RECORD_INDICATOR_NT = ?, RECORD_INDICATOR = ?, MAKER = ?, VERIFIER = ?, INTERNAL_STATUS = ?, "+
			"DATE_LAST_MODIFIED = "+getDbFunction("SYSDATE")+", VV_CATEGORY = ? Where VARIABLE = ?";
		Object[] args = {vObject.getValue(),vObject.getVariableStatusNt(),vObject.getVariableStatus(), vObject.getRecordIndicatorNt(),vObject.getRecordIndicator(),vObject.getMaker(),
			vObject.getVerifier(), vObject.getInternalStatus(), vObject.getVvCategory(), vObject.getVariable()};
		return getJdbcTemplate().update(query,args);
	}
	@Override
	protected int doUpdatePend(VisionVariablesVb vObject){
		String query = "Update VISION_VARIABLES_PEND Set VALUE = ?, VARIABLE_STATUS_NT = ?, VARIABLE_STATUS = ?,"+
			"RECORD_INDICATOR_NT = ?, RECORD_INDICATOR = ?, MAKER = ?, VERIFIER = ?, INTERNAL_STATUS = ?, DATE_LAST_MODIFIED = "+getDbFunction("SYSDATE")+", VV_CATEGORY = ?  Where VARIABLE = ?";
		Object[] args = {vObject.getValue(),vObject.getVariableStatusNt(),vObject.getVariableStatus(), vObject.getRecordIndicatorNt(),vObject.getRecordIndicator(),vObject.getMaker(),
			vObject.getVerifier(), vObject.getInternalStatus(), vObject.getVvCategory(), vObject.getVariable()};
		return getJdbcTemplate().update(query,args);
	}
	@Override
	protected int doDeleteAppr(VisionVariablesVb vObject){
		String query = "Delete From VISION_VARIABLES	Where VARIABLE = ?";
		Object[] args = {vObject.getVariable()};
		return getJdbcTemplate().update(query,args);
	}
	@Override
	protected int deletePendingRecord(VisionVariablesVb vObject){
		String query = "Delete From VISION_VARIABLES_PEND Where VARIABLE = ?";
		Object[] args = {vObject.getVariable()};
		return getJdbcTemplate().update(query,args);
	}
	@Override
	protected String frameErrorMessage(VisionVariablesVb vObject, String strOperation){
		// specify all the key fields and their values first
		String strErrMsg = new String("");
		try{
			strErrMsg =  strErrMsg + "VARIABLE: " + vObject.getVariable();
			// Now concatenate the error message that has been sent
			if ("Approve".equalsIgnoreCase(strOperation))
				strErrMsg = strErrMsg + " failed during approve Operation. Bulk Approval aborted !!";
			else
				strErrMsg = strErrMsg + " failed during reject Operation. Bulk Rejection aborted !!";
		}catch(Exception ex){
			strErrorDesc = ex.getMessage();
			strErrMsg = strErrMsg + strErrorDesc;
			logger.error(strErrMsg, ex);
		}
		// Return back the error message string
		return strErrMsg;
	}
	@Override
	protected String getAuditString(VisionVariablesVb vObject){
		final String auditDelimiter = vObject.getAuditDelimiter();
		final String auditDelimiterColVal = vObject.getAuditDelimiterColVal();
		StringBuffer strAudit = new StringBuffer("");
		if(ValidationUtil.isValid(vObject.getVariable()))
			strAudit.append("VARIABLE"+auditDelimiterColVal+vObject.getVariable().trim());
		else
			strAudit.append("VARIABLE"+auditDelimiterColVal+"NULL");
		strAudit.append(auditDelimiter);

		if(ValidationUtil.isValid(vObject.getValue()))
			strAudit.append("VALUE"+auditDelimiterColVal+vObject.getValue().trim());
		else
			strAudit.append("VALUE"+auditDelimiterColVal+"NULL");
		strAudit.append(auditDelimiter);
		
		if(ValidationUtil.isValid(vObject.getVvCategory()))
			strAudit.append("VV_CATEGORY"+auditDelimiterColVal+vObject.getVvCategory().trim());
		else
			strAudit.append("VV_CATEGORY"+auditDelimiterColVal+"NULL");
		strAudit.append(auditDelimiter);
		
		strAudit.append("VARIABLE_STATUS_NT"+auditDelimiterColVal+vObject.getVariableStatusNt());
		strAudit.append(auditDelimiter);
		
		strAudit.append("VARIABLE_STATUS"+auditDelimiterColVal+vObject.getVariableStatus());
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
		serviceName = "Vision Variables";
		serviceDesc = CommonUtils.getResourceManger().getString("visionVariables");
		//serviceDesc = "VisionVariables";
		tableName = "VISION_VARIABLES";
		childTableName = "VISION_VARIABLES";
		intCurrentUserId = CustomContextHolder.getContext().getVisionId();
		
	}
}