package com.vision.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

import org.springframework.jdbc.UncategorizedSQLException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.vision.authentication.CustomContextHolder;
import com.vision.exception.ExceptionCode;
import com.vision.exception.RuntimeCustomException;
import com.vision.util.CommonUtils;
import com.vision.util.Constants;
import com.vision.util.ValidationUtil;
import com.vision.vb.SmartSearchVb;
import com.vision.vb.VisionUploadVb;




@Component
public class VisionUploadDao extends AbstractDao<VisionUploadVb> {
	
	@Override
	protected RowMapper getMapper(){
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				VisionUploadVb visionUploadVb = new VisionUploadVb();
				visionUploadVb.setTableName(rs.getString("TABLE_NAME").trim());
				visionUploadVb.setFileName(rs.getString("FILE_NAME").trim());
				visionUploadVb.setUploadSequence(rs.getString("UPLOAD_SEQUENCE"));
				visionUploadVb.setUploadDate(rs.getString("UPLOAD_DATE").trim());
				visionUploadVb.setUploadStatusNt(rs.getInt("UPLOAD_STATUS_NT"));
				visionUploadVb.setUploadStatus(rs.getInt("UPLOAD_STATUS"));
				visionUploadVb.setStatusDesc(rs.getString("UPLOAD_STATUS_DESC"));
				visionUploadVb.setSheetName(rs.getString("SHEET_NAME"));
				visionUploadVb.setMaker(rs.getLong("MAKER"));
				visionUploadVb.setMakerName(rs.getString("MAKER_NAME"));
				visionUploadVb.setDateLastModified(rs.getString("DATE_LAST_MODIFIED").trim());
				visionUploadVb.setDateCreation(rs.getString("DATE_CREATION").trim());
				if("Y".equalsIgnoreCase(rs.getString("VERIFICATION_REQD"))) {
					visionUploadVb.setVerification("Yes");
				} else {
					visionUploadVb.setVerification("No");
				}
            	visionUploadVb.setLogFile(rs.getString("TABLE_NAME").trim()+"_"+rs.getString("UPLOAD_SEQUENCE")+"_"+rs.getLong("MAKER")+"_"+rs.getString("LOG_DATE").trim()+".err");
				return visionUploadVb;
			}
		};
		return mapper;
	}
	public List<VisionUploadVb> getQueryResults(VisionUploadVb dObj,int intStatus)
	{
		setServiceDefaults();
		List<VisionUploadVb> collTemp= null;
		Vector<Object> params = new Vector<Object>();
		String uploadDate = "";
		String logDate = "";
		String visionId="";
		if ("MSSQL".equalsIgnoreCase(databaseType)) {
			uploadDate = "format((CAST(UPLOAD_DATE AS DATETIME)),'dd-MMM-yyyy HH:mm:ss')";
			logDate = "format(CAST(TAppr.UPLOAD_DATE AS DATETIME), 'yyyy-MM-dd') LOG_DATE";
			visionId ="cast(vision_id as varchar) +' - '+USER_NAME";
		}else if("ORACLE".equalsIgnoreCase(databaseType)) {
			uploadDate = "TO_CHAR(TAPPR.UPLOAD_DATE,'DD-Mon-RRRR')";
			 logDate = "TO_CHAR(TAPPR.UPLOAD_DATE,'yyyy-MM-dd') LOG_DATE";
			 visionId="vision_id||'-'||USER_NAME";
		}
		String user= "";
		
		if(!"ALL".equalsIgnoreCase(dObj.getUserId())) 
			user = " WHERE TAppr.MAKER = ? " ;
		
		StringBuffer strBufApprove = new StringBuffer("Select * from (Select TAppr.TABLE_NAME,"
				+ "TAppr.FILE_NAME, TAppr.UPLOAD_SEQUENCE, " + uploadDate + " UPLOAD_DATE,"
				+ "TAppr.UPLOAD_STATUS_NT, TAppr.UPLOAD_STATUS, TAppr.MAKER,"
				+ " (SELECT NUM_SUBTAB_DESCRIPTION FROM NUM_SUB_TAB WHERE NUM_TAB = 10 AND NUM_SUB_TAB = TAPPR.UPLOAD_STATUS) UPLOAD_STATUS_DESC, "
				+ " (SELECT "+visionId+" FROM VISION_USERS WHERE VISION_ID = TAPPR.MAKER) MAKER_NAME,TAppr.SHEET_NAME,(SELECT  VERIFICATION_REQD DESCRIPTION FROM VISION_TABLES where TABLE_NAME =TAppr.TABLE_NAME) VERIFICATION_REQD, "
				+ "" + getDbFunction("DATEFUNC") + "(TAppr.DATE_LAST_MODIFIED, 'dd-MM-yyyy "
				+ getDbFunction("TIME") + "') DATE_LAST_MODIFIED, " + "" + getDbFunction("DATEFUNC")
				+ "(TAppr.DATE_CREATION, 'dd-MM-yyyy " + getDbFunction("TIME") + "') DATE_CREATION,"+logDate
				+ " From VISION_UPLOAD TAppr "+user+" ) TAppr");
		
			Object[] args= {dObj.getUserId()};
		
	
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
					
					case "uploadSequence":
						CommonUtils.addToQuerySearch(" upper(TAPPR.UPLOAD_SEQUENCE) "+ val, strBufApprove, data.getJoinType());
						break;
						
					case "verification":
						CommonUtils.addToQuerySearch(" upper(TAPPR.VERIFICATION_REQD) "+ val, strBufApprove, data.getJoinType());
						break;
						
					case "tableName":
						CommonUtils.addToQuerySearch(" upper(TAPPR.TABLE_NAME) "+ val, strBufApprove, data.getJoinType());
						break;

					case "fileName":
						CommonUtils.addToQuerySearch(" upper(TAPPR.FILE_NAME) "+ val, strBufApprove, data.getJoinType());
						break;

					case "makerName":
						CommonUtils.addToQuerySearch(" upper(TAPPR.MAKER_NAME) "+ val, strBufApprove, data.getJoinType());
						break;
						
					case "statusDesc":
						CommonUtils.addToQuerySearch(" upper(TAPPR.UPLOAD_STATUS_DESC) "+ val, strBufApprove, data.getJoinType());
						break;
						
					case "sheetName":
						CommonUtils.addToQuerySearch(" upper(TAPPR.SHEET_NAME) "+ val, strBufApprove, data.getJoinType());
						break;
						
					case "uploadDate":
						CommonUtils.addToQuerySearch(" upper(TAPPR.UPLOAD_DATE) "+ val, strBufApprove, data.getJoinType());
						break;
						
					default:
					}
					count++;
				}
			}
			String orderBy=" Order By UPLOAD_SEQUENCE DESC ";
			strBufApprove.append(orderBy);
			
			if(!"ALL".equalsIgnoreCase(dObj.getUserId()))
			    collTemp = getJdbcTemplate().query(strBufApprove.toString(),args, getMapper());
			else
				collTemp = getJdbcTemplate().query(strBufApprove.toString(), getMapper());
			return collTemp;
		}catch(Exception ex){
			
			ex.printStackTrace();
			//logger.error(((strBufApprove==null)? "strBufApprove is Null":strBufApprove.toString()));
			/*if (params != null)
				for(int i=0 ; i< params.size(); i++)*/
					//logger.error("objParams[" + i + "]" + params.get(i).toString());
			return null;
		}
	}
	/**
	 * 
	 * @param dObj
	 * @return count
	 */
	public int getCountOfUploadTables(VisionUploadVb dObj)
	{
		Object objParams[]=new Object[2];;
		StringBuffer strBufApprove = new StringBuffer("select count(1) from VISION_TABLES MAXTABLES, PROFILE_PRIVILEGES PP, VISION_USERS MU " +
				"where MAXTABLES.TABLE_NAME =? AND PP.MENU_GROUP =MAXTABLES.MENU_GROUP"+ 
				" AND MAXTABLES.UPLOAD_ALLOWED  ='Y' AND PP.P_EXCEL_UPLOAD ='Y' AND MU.USER_GROUP=PP.USER_GROUP " +
				" AND MU.USER_PROFILE=PP.USER_PROFILE AND MU.VISION_ID = ? ");
		try{
			objParams[0] = dObj.getTableName() == null? "" : dObj.getTableName().toUpperCase();
			objParams[1] = dObj.getMaker();
			return getJdbcTemplate().queryForObject(strBufApprove.toString(), objParams, Integer.class);
		}catch(Exception ex){
			ex.printStackTrace();
			return 1;
		}
	}
	/**
	 * 
	 * @return
	 */
	private int getMaxSequence(){
		StringBuffer strBufApprove = new StringBuffer("Select MAX(TAppr.UPLOAD_SEQUENCE) From VISION_UPLOAD TAppr ");
		try{
			return getJdbcTemplate().queryForObject(strBufApprove.toString(), Integer.class);
		}catch(Exception ex){
			ex.printStackTrace();
			return 1;
		}
	}
	@Transactional(rollbackForClassName={"com.vision.exception.RuntimeCustomException"})
	public ExceptionCode doInsertRecord(List<VisionUploadVb> vObjects) throws RuntimeCustomException {
		ExceptionCode exceptionCode = new ExceptionCode();
		strErrorDesc  = "";
		strCurrentOperation = "Add";
		setServiceDefaults();
		try {
			int maxSequence = getMaxSequence();
			if(maxSequence <= 0) maxSequence =1;
			else maxSequence++;
			for(VisionUploadVb vObject : vObjects){
				vObject.setVerificationRequired(false);
				    	vObject.setUploadStatus(1);
				        vObject.setMaker(intCurrentUserId);
				        String strDBDate = getSystemDate();
				        vObject.setDateCreation(strDBDate);
				        vObject.setDateLastModified(strDBDate);
						vObject.setUploadSequence(maxSequence+"");
						vObject.setUploadDate(strDBDate);
						maxSequence++;
						retVal = doInsertionAppr(vObject); 
						if (retVal != Constants.SUCCESSFUL_OPERATION)
						{
							throw new RuntimeCustomException(getResultObject(retVal));
						}else {
							exceptionCode.setErrorCode(retVal);
						}
					}
				
		
			return exceptionCode;
		}catch (RuntimeCustomException rcException) {
			throw rcException;
		}catch (UncategorizedSQLException uSQLEcxception) {
			strErrorDesc = parseErrorMsg(uSQLEcxception);
			exceptionCode = getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION);
			throw buildRuntimeCustomException(exceptionCode);
		}catch(Exception ex){
			logger.error("Error in Add.",ex);
			strErrorDesc = ex.getMessage();
			exceptionCode = getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION);
			throw buildRuntimeCustomException(exceptionCode);
		}
	}
	@Override
	protected int doUpdateAppr(VisionUploadVb vObject){
		try {
			String query = "Update VISION_UPLOAD SET UPLOAD_DATE = "+getDbFunction("SYSDATE")+", " +
					"UPLOAD_STATUS_NT = ?, UPLOAD_STATUS = ?, DATE_LAST_MODIFIED = "+getDbFunction("SYSDATE")+",	FILE_NAME = ?,SHEET_NAME = ?  " +
					"Where TABLE_NAME = ? AND UPLOAD_SEQUENCE = ? AND MAKER = ?";
				Object[] args = {vObject.getUploadStatusNt(),vObject.getUploadStatus(),
					vObject.getFileName(),vObject.getSheetName(),vObject.getTableName(),vObject.getUploadSequence(), vObject.getMaker()};
				return getJdbcTemplate().update(query,args);
		
		}catch(Exception ex) {
			ex.printStackTrace();
			return 0;
		}
	}
	@Override
	protected int doInsertionAppr(VisionUploadVb vObject){
		String query = "Insert Into VISION_UPLOAD ( TABLE_NAME, FILE_NAME, UPLOAD_SEQUENCE, UPLOAD_DATE, " +
			"UPLOAD_STATUS_NT, UPLOAD_STATUS,SHEET_NAME, MAKER, DATE_LAST_MODIFIED, DATE_CREATION) "+
			"Values (?, ?, ?, ?, ?, ?, ?, ?, "+getDbFunction("SYSDATE")+","+getDbFunction("SYSDATE")+")";
		Object[] args = {vObject.getTableName(),vObject.getFileName().toUpperCase(),vObject.getUploadSequence(),
			vObject.getUploadDate(), vObject.getUploadStatusNt(),vObject.getUploadStatus(),vObject.getSheetName(),vObject.getMaker()};  
		return getJdbcTemplate().update(query,args);
	}
	@Override
	protected void setServiceDefaults(){
		serviceName = "Vision Upload";
		serviceDesc = "Vision Upload";
		tableName = "VISION_UPLOAD";
		childTableName = "VISION_UPLOAD";
		intCurrentUserId = CustomContextHolder.getContext().getVisionId();
	}
	@Override
	protected List<VisionUploadVb> selectApprovedRecord(VisionUploadVb vObject){
		return getQueryResults(vObject, Constants.STATUS_ZERO);
	}
	
	@Override
	protected ExceptionCode doUpdateApprRecordForNonTrans(VisionUploadVb vObject) throws RuntimeCustomException  {
		List<VisionUploadVb> collTemp = null;
		VisionUploadVb vObjectlocal = null;
		ExceptionCode exceptionCode = new ExceptionCode();
		strApproveOperation =Constants.MODIFY;
		strErrorDesc  = "";
		strCurrentOperation = Constants.MODIFY;
		setServiceDefaults();
		vObject.setMaker(getIntCurrentUserId());
		 collTemp = getQueryDetails(vObject, Constants.STATUS_ZERO);
		 
		if("RUNNING".equalsIgnoreCase(getBuildStatus(vObject))){
			exceptionCode = getResultObject(Constants.BUILD_IS_RUNNING);
			throw buildRuntimeCustomException(exceptionCode);
		}
		  if (vObject.getUploadStatus() != 2 && collTemp != null && !collTemp.isEmpty()){
		    	VisionUploadVb vObjectPersis = collTemp.get(0);
		    	vObjectPersis.setMaker(intCurrentUserId);
		    	vObjectPersis.setSheetName(vObject.getSheetName());
		    	if(vObjectPersis.getUploadStatus() == 2){
		    		strErrorDesc="File Upload in Progress for Table: "+vObjectPersis.getTableName();
		    		throw new RuntimeCustomException(getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION));
		    	}
		    	vObjectPersis.setUploadStatus(1);
		    	vObjectPersis.setFileName(vObject.getFileName());
		    	
				retVal = doUpdateAppr(vObjectPersis);
				if (retVal != Constants.SUCCESSFUL_OPERATION)
				{
					throw new RuntimeCustomException(getResultObject(retVal));
				}else {
					exceptionCode.setErrorCode(retVal);
					exceptionCode.setErrorMsg("ReInitated successfully ");
				}
				vObject.setMaker(intCurrentUserId);
				vObject.setUploadStatus(1);
		    }
		/*exceptionCode = writeAuditLog(vObject, vObjectlocal);
		if(exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION){
			exceptionCode = getResultObject(Constants.AUDIT_TRAIL_ERROR);
			throw buildRuntimeCustomException(exceptionCode);
		}*/
		return exceptionCode;
	}
	public List<VisionUploadVb> getQueryDetails(VisionUploadVb dObj,int intStatus)
	{
		setServiceDefaults();
		List<VisionUploadVb> collTemp= null;
		String uploadDate = "";
		String logDate = "";
		String visionId="";
		if ("MSSQL".equalsIgnoreCase(databaseType)) {
			uploadDate = "format(CAST(TAppr.UPLOAD_DATE AS DATETIME), 'dd-MMM-yyyy')";
			logDate = "format(CAST(TAppr.UPLOAD_DATE AS DATETIME), 'yyyy-MM-dd') LOG_DATE";
			visionId ="cast(vision_id as varchar) +' - '+USER_NAME";
		}else if("ORACLE".equalsIgnoreCase(databaseType)) {
			uploadDate = "TO_CHAR(TAPPR.UPLOAD_DATE,'DD-Mon-RRRR')";
			 logDate = "TO_CHAR(TAPPR.UPLOAD_DATE,'yyyy-MM-dd') LOG_DATE";
			 visionId="vision_id||'-'||USER_NAME";
		}
		StringBuffer strBufApprove = new StringBuffer("Select TAppr.TABLE_NAME,"
				+ "TAppr.FILE_NAME, TAppr.UPLOAD_SEQUENCE, " + uploadDate + " UPLOAD_DATE,"
				+ "TAppr.UPLOAD_STATUS_NT, TAppr.UPLOAD_STATUS, TAppr.MAKER,"
				+ " (SELECT NUM_SUBTAB_DESCRIPTION FROM NUM_SUB_TAB WHERE NUM_TAB = 10 AND NUM_SUB_TAB = TAPPR.UPLOAD_STATUS) UPLOAD_STATUS_DESC, "
				+ " (SELECT "+visionId+" FROM VISION_USERS WHERE VISION_ID = TAPPR.MAKER) MAKER_NAME,TAppr.SHEET_NAME,(SELECT  VERIFICATION_REQD DESCRIPTION FROM VISION_TABLES where TABLE_NAME =TAppr.TABLE_NAME) VERIFICATION_REQD, "
				+ "" + getDbFunction("DATEFUNC") + "(TAppr.DATE_LAST_MODIFIED, 'dd-MM-yyyy "
				+ getDbFunction("TIME") + "') DATE_LAST_MODIFIED, " + "" + getDbFunction("DATEFUNC")
				+ "(TAppr.DATE_CREATION, 'dd-MM-yyyy " + getDbFunction("TIME") + "') DATE_CREATION,"+logDate
				+ " From VISION_UPLOAD TAppr where UPLOAD_SEQUENCE = ? ");
		Object objParams[] = new Object[1];
		objParams[0] = new String(dObj.getUploadSequence());
		try {
			collTemp = getJdbcTemplate().query(strBufApprove.toString(), objParams, getMapper());
			return collTemp;
		}catch(Exception e) {
			e.printStackTrace();
			return null;
		}
}
	public int doDeleteAppr(VisionUploadVb vObject){
		String query = "DELETE FROM VISION_UPLOAD WHERE UPLOAD_SEQUENCE = ?";
		Object[] args = {vObject.getUploadSequence()};  
		return getJdbcTemplate().update(query,args);
	}
	
}