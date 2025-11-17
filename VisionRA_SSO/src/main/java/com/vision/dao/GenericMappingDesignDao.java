package com.vision.dao;

import java.io.Reader;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.UncategorizedSQLException;
import org.springframework.jdbc.core.PreparedStatementCreator;
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
import com.vision.vb.TemplateNameGBMVb;
import com.vision.vb.VisionUsersVb;

@Component
public class GenericMappingDesignDao extends AbstractDao<TemplateNameGBMVb> {
// private static final ExceptionCode ExceptionCode() = null;
	@Autowired
	CommonDao commonDao;

	String mappingStatusNtApprDesc = ValidationUtil.numAlphaTabDescritpionQuery("NT", 1, "TAppr.MAPPING_STATUS",
			"MAPPING_STATUS_DESC");
	String mappingStatusNtPendDesc = ValidationUtil.numAlphaTabDescritpionQuery("NT", 1, "TPend.MAPPING_STATUS",
			"MAPPING_STATUS_DESC");

	String RecordIndicatorNtApprDesc = ValidationUtil.numAlphaTabDescritpionQuery("NT", 7, "TAppr.RECORD_INDICATOR",
			"RECORD_INDICATOR_DESC");
	String RecordIndicatorNtPendDesc = ValidationUtil.numAlphaTabDescritpionQuery("NT", 7, "TPend.RECORD_INDICATOR",
			"RECORD_INDICATOR_DESC");
	

	String programDescAppr = "(SELECT PROGRAM_DESCRIPTION FROM PROGRAMS WHERE PROGRAM = TAPPR.PROGRAM AND COUNTRY = TAPPR.COUNTRY AND LE_BOOK = TAPPR.LE_BOOK) PROGRAM_DESC, ";
	String programDescPend = "(SELECT PROGRAM_DESCRIPTION FROM PROGRAMS WHERE PROGRAM = TPEND.PROGRAM AND COUNTRY = TPEND.COUNTRY AND LE_BOOK = TPEND.LE_BOOK) PROGRAM_DESC, ";
	
	
	String exceutionTypeNtApprDesc = ValidationUtil.numAlphaTabDescritpionQuery("AT", 1092, "TAppr.EXECUTION_TYPE",
			"EXECUTION_TYPE_DESC");
	String exceutionTypeNtPendDesc = ValidationUtil.numAlphaTabDescritpionQuery("AT", 1092, "TPend.EXECUTION_TYPE",
			"EXECUTION_TYPE_DESC");
	@Override
	protected RowMapper getMapper(){
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				TemplateNameGBMVb templateNameVb = new TemplateNameGBMVb();
				if(rs.getString("COUNTRY") != null){
					templateNameVb.setCountry(rs.getString("COUNTRY"));
				}else{
					templateNameVb.setCountry("");
				}
				if(rs.getString("LE_BOOK") != null){
							templateNameVb.setLeBook(rs.getString("LE_BOOK"));
				}else{
					templateNameVb.setLeBook("");
				}
				if(rs.getString("PROGRAM") != null){
					templateNameVb.setProgram(rs.getString("PROGRAM"));
					templateNameVb.setProgramDesc(rs.getString("PROGRAM_DESC"));
				}else{
					templateNameVb.setProgram("");
					templateNameVb.setProgramDesc(rs.getString(""));
				}
				if(rs.getString("EXECUTION_SEQUENCE") != null){
					templateNameVb.setExecutionSequence(rs.getString("EXECUTION_SEQUENCE"));
				}else{
					templateNameVb.setExecutionSequence("");
				}
				if(rs.getString("EXECUTION_TYPE") != null){
					templateNameVb.setExecutionType(rs.getString("EXECUTION_TYPE"));
					templateNameVb.setExecutionTypeDesc(rs.getString("EXECUTION_TYPE_DESC"));
				}else{
					templateNameVb.setExecutionType("");
					templateNameVb.setExecutionTypeDesc("");
				}
				if(rs.getString("MAPPING_STATUS_DESC") != null){
					templateNameVb.setStatusDesc(rs.getString("MAPPING_STATUS_DESC"));
				}else{
					templateNameVb.setStatusDesc("");
				}
				if(rs.getString("RECORD_INDICATOR_DESC") != null){
					templateNameVb.setRecordIndicatorDesc(rs.getString("RECORD_INDICATOR_DESC"));
				}else{
					templateNameVb.setRecordIndicatorDesc("");
				}
				templateNameVb.setRecordIndicator(rs.getInt("RECORD_INDICATOR"));
				templateNameVb.setMappingStatus(rs.getInt("MAPPING_STATUS"));
				templateNameVb.setMaker(rs.getLong("MAKER"));
				templateNameVb.setVerifier(rs.getLong("VERIFIER"));
				templateNameVb.setInternalStatus(rs.getInt("INTERNAL_STATUS"));
				templateNameVb.setDateLastModified(rs.getString("DATE_LAST_MODIFIED"));
				templateNameVb.setDateCreation(rs.getString("DATE_CREATION"));
				templateNameVb.setMakerName(rs.getString("MAKER_NAME"));
				templateNameVb.setVerifierName(rs.getString("VERIFIER_NAME"));
				return templateNameVb;
			}
		};
		return mapper;
	}
/*	protected RowMapper getMapper(){
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				TemplateNameGBMVb templateNameVb = new TemplateNameGBMVb();
				templateNameVb.setTemplateName(rs.getString("TEMPLATE_NAME"));
				if(rs.getString("GENERAL_DESCRIPTION") != null){
					templateNameVb.setGeneralDescription(rs.getString("GENERAL_DESCRIPTION"));
				}else{
					templateNameVb.setGeneralDescription("");
				}
				if(rs.getString("FEED_CATEGORY") != null){
					templateNameVb.setFeedCategory(rs.getString("FEED_CATEGORY"));
				}else{
					templateNameVb.setFeedCategory("");
				}
				if(rs.getString("PROGRAM") != null){
					templateNameVb.setProgram(rs.getString("PROGRAM"));
				}else{
					templateNameVb.setProgram("");
				}
				if(rs.getString("PROCESS_SEQUENCE") != null){
					templateNameVb.setProcessSequence(rs.getString("PROCESS_SEQUENCE"));
				}else{
					templateNameVb.setProcessSequence("");	
				}
				if(rs.getString("FILE_PATTERN") != null){
					templateNameVb.setFilePattern(rs.getString("FILE_PATTERN"));
				}else{
					templateNameVb.setFilePattern("");	
				}
				if(rs.getString("EXCEL_FILE_PATTERN") != null){
					templateNameVb.setExcelFilePattern(rs.getString("EXCEL_FILE_PATTERN"));
				}else{
					templateNameVb.setExcelFilePattern("");	
				}
				if(rs.getString("EXCEL_TEMPLATE_ID") != null){
					templateNameVb.setExcelTemplateId(rs.getString("EXCEL_TEMPLATE_ID"));
				}else{
					templateNameVb.setExcelTemplateId("");	
				}
				templateNameVb.setDefaultAcquProcessTypeAT(rs.getInt("DEFAULT_ACQ_PROCESS_TYPE_AT"));
				if(rs.getString("DEFAULT_ACQ_PROCESS_TYPE") != null){
					templateNameVb.setDefaultAcquProcessType(rs.getString("DEFAULT_ACQ_PROCESS_TYPE"));
				}else{
					templateNameVb.setDefaultAcquProcessType("");
				}
				templateNameVb.setTemplateStatusNt(rs.getInt("TEMPLATE_STATUS_NT"));
				templateNameVb.setTemplateStatus(rs.getInt("TEMPLATE_STATUS"));
				templateNameVb.setRecordIndicatorNt(rs.getInt("RECORD_INDICATOR_NT"));
				templateNameVb.setRecordIndicator(rs.getInt("RECORD_INDICATOR"));
				templateNameVb.setMaker(rs.getLong("MAKER"));
				templateNameVb.setVerifier(rs.getLong("VERIFIER"));
				templateNameVb.setInternalStatus(rs.getInt("INTERNAL_STATUS"));
				templateNameVb.setDateLastModified(rs.getString("DATE_LAST_MODIFIED"));
				templateNameVb.setDateCreation(rs.getString("DATE_CREATION"));
				if(rs.getString("FEED_STG_NAME") != null){
					templateNameVb.setFeedStgName(rs.getString("FEED_STG_NAME"));
				}else{
					templateNameVb.setFeedStgName("");
				}
				if(rs.getString("VIEW_NAME") != null){
					templateNameVb.setViewName(rs.getString("VIEW_NAME"));
				}else{
					templateNameVb.setViewName("");
				}
				if(rs.getString("PROGRAM_DESC") != null){
					templateNameVb.setProgramDesc(rs.getString("PROGRAM_DESC"));
				}else{
					templateNameVb.setProgramDesc("");
				}
				if(rs.getString("EFFECTIVE_DATE")!= null){
					templateNameVb.setEffectiveDate(rs.getString("EFFECTIVE_DATE"));
				}else{
					templateNameVb.setEffectiveDate("");
				}
				return templateNameVb;
			}
		};
		return mapper;
	}*/
	
	private RowMapper getQueryMapper(){
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				TemplateNameGBMVb templateNameVb = new TemplateNameGBMVb();
				templateNameVb.setTemplateName(rs.getString("TEMPLATE_NAME"));
				if(rs.getString("GENERAL_DESCRIPTION") != null){
					templateNameVb.setGeneralDescription(rs.getString("GENERAL_DESCRIPTION"));
				}else{
					templateNameVb.setGeneralDescription("");
				}			
				if(rs.getString("FEED_CATEGORY") != null){
					templateNameVb.setFeedCategory(rs.getString("FEED_CATEGORY"));
				}else{
					templateNameVb.setFeedCategory("");
				}
				if(rs.getString("PROGRAM") != null){
					templateNameVb.setProgram(rs.getString("PROGRAM"));
				}else{
					templateNameVb.setProgram("");
				}				
				templateNameVb.setDefaultAcquProcessTypeAT(rs.getInt("DEFAULT_ACQ_PROCESS_TYPE_AT"));
				templateNameVb.setDefaultAcquProcessType(rs.getString("DEFAULT_ACQ_PROCESS_TYPE"));
				templateNameVb.setTemplateStatusNt(rs.getInt("TEMPLATE_STATUS_NT"));
				templateNameVb.setTemplateStatus(rs.getInt("TEMPLATE_STATUS"));
				templateNameVb.setDbStatus(rs.getInt("TEMPLATE_STATUS"));
				templateNameVb.setRecordIndicatorNt(rs.getInt("RECORD_INDICATOR_NT"));
				templateNameVb.setRecordIndicator(rs.getInt("RECORD_INDICATOR"));
				templateNameVb.setMaker(rs.getLong("MAKER"));
				templateNameVb.setVerifier(rs.getLong("VERIFIER"));
				templateNameVb.setInternalStatus(rs.getInt("INTERNAL_STATUS"));
				templateNameVb.setDateLastModified(rs.getString("DATE_LAST_MODIFIED"));
				templateNameVb.setDateCreation(rs.getString("DATE_CREATION"));
				if(rs.getString("FEED_STG_NAME") != null){
					templateNameVb.setFeedStgName(rs.getString("FEED_STG_NAME"));
				}else{
					templateNameVb.setFeedStgName("");
				}
				if(rs.getString("VIEW_NAME") != null){
					templateNameVb.setViewName(rs.getString("VIEW_NAME"));
				}else{
					templateNameVb.setViewName("");
				}
				if(rs.getString("PROGRAM_DESC") != null){
					templateNameVb.setProgramDesc(rs.getString("PROGRAM_DESC"));
				}else{
					templateNameVb.setProgramDesc("");
				}
				if(rs.getString("PROCESS_SEQUENCE") != null){
					templateNameVb.setProcessSequence(rs.getString("PROCESS_SEQUENCE"));
				}else{
					templateNameVb.setProcessSequence("");	
				}
				if(rs.getString("FILE_PATTERN") != null){
					templateNameVb.setFilePattern(rs.getString("FILE_PATTERN"));
				}else{
					templateNameVb.setFilePattern("");	
				}
				if(rs.getString("EXCEL_FILE_PATTERN") != null){
					templateNameVb.setExcelFilePattern(rs.getString("EXCEL_FILE_PATTERN"));
				}else{
					templateNameVb.setExcelFilePattern("");	
				}
				if(rs.getString("EXCEL_TEMPLATE_ID") != null){
					templateNameVb.setExcelTemplateId(rs.getString("EXCEL_TEMPLATE_ID"));
				}else{
					templateNameVb.setExcelTemplateId("");	
				}
				templateNameVb.setEffectiveDate(rs.getString("EFFECTIVE_DATE"));
				return templateNameVb;
			}
		};
		return mapper;
	}
	private RowMapper getQueryMapperTemplateScreenPopUp(){
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				TemplateNameGBMVb templateNameVb = new TemplateNameGBMVb();
				templateNameVb.setTemplateName(rs.getString("TEMPLATE_NAME"));
				if(rs.getString("COLUMN_NAME") != null){
					templateNameVb.setColumnName(rs.getString("COLUMN_NAME"));
				}else{
					templateNameVb.setColumnName("");
				}		
				if(rs.getString("COLUMN_ID") != null){
					templateNameVb.setColumnId(rs.getString("COLUMN_ID"));
				}else{
					templateNameVb.setColumnId("");
				}	
				if(rs.getString("DATA_TYPE") != null){
					templateNameVb.setDataType(rs.getString("DATA_TYPE"));
				}else{
					templateNameVb.setDataType("");
				}	
				if(rs.getString("CHAR_USED") != null){
					templateNameVb.setCharUsed(rs.getString("CHAR_USED"));
				}else{
					templateNameVb.setCharUsed("");
				}	
				if(rs.getString("DATA_LENGTH") != null){
					templateNameVb.setDataLength(rs.getString("DATA_LENGTH"));
				}else{
					templateNameVb.setDataLength("");
				}	
				if(rs.getString("DATA_SCALING") != null){
					templateNameVb.setDataScaling(rs.getString("DATA_SCALING"));
				}else{
					templateNameVb.setDataScaling("");
				}	
				if(rs.getString("DATA_INDEX") != null){
					templateNameVb.setDataIndex(rs.getString("DATA_INDEX"));
				}else{
					templateNameVb.setDataIndex("");
				}	
				if(rs.getString("GUIDELINES") != null){
					templateNameVb.setGuidelines(rs.getString("GUIDELINES"));
				}else{
					templateNameVb.setGuidelines("");
				}	
				templateNameVb.setMandatoryFlag(rs.getInt("MANDATORY_FLAG"));
				if(rs.getString("DEFAULT_VALUES") != null){
					templateNameVb.setDefaultValues(rs.getString("DEFAULT_VALUES"));
				}else{
					templateNameVb.setDefaultValues("");
				}	
				if(rs.getString("COLUMN_DESCRIPTION") != null){
					templateNameVb.setColumnDescription(rs.getString("COLUMN_DESCRIPTION"));
				}else{
					templateNameVb.setColumnDescription("");
				}
				if(rs.getString("DATA_SAMPLE") != null){
					templateNameVb.setDataSample(rs.getString("DATA_SAMPLE"));
				}else{
					templateNameVb.setDataSample("");
				}
				if(rs.getString("EFFECTIVE_DATE") != null){
					templateNameVb.setEffectiveDate(rs.getString("EFFECTIVE_DATE"));
				}else{
					templateNameVb.setEffectiveDate("");
				}
				
				return templateNameVb;
			}
		};
		return mapper;
	}
	protected RowMapper getMapperTemplateNameProgram(){
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				TemplateNameGBMVb templateNameVb = new TemplateNameGBMVb();
				templateNameVb.setCountry(rs.getString("COUNTRY"));
				templateNameVb.setLeBook(rs.getString("LE_BOOK"));
				templateNameVb.setProgram(rs.getString("PROGRAM"));
				if(rs.getString("PROGRAM_DESC") != null){
					templateNameVb.setProgramDesc(rs.getString("PROGRAM_DESC"));
				}else{
					templateNameVb.setProgramDesc("");
				}
				templateNameVb.setExecutionSequence(rs.getString("EXECUTION_SEQUENCE"));
				templateNameVb.setExecutionTypeAt(rs.getInt("EXECUTION_TYPE_AT"));
				templateNameVb.setExecutionType(rs.getString("EXECUTION_TYPE"));
				
				
				
				if(ValidationUtil.isValid(rs.getString("MAIN_QUERY"))) {
					templateNameVb.setMainQuery(rs.getString("MAIN_QUERY"));
					templateNameVb.setOldMainQuery(rs.getString("MAIN_QUERY"));	
				}else {
					templateNameVb.setMainQuery("");
					templateNameVb.setOldMainQuery("");	
				}
			
//				Clob clob = rs.getClob("MAIN_QUERY");
//				templateNameVb.setMainQuery(clob);
				
				
				if(ValidationUtil.isValid(rs.getString("DML_1"))) {
					templateNameVb.setDml1(rs.getString("DML_1"));
					templateNameVb.setOldDml1(rs.getString("DML_1"));	
				}else {
					templateNameVb.setDml1("");
					templateNameVb.setOldDml1("");
				}
				templateNameVb.setFatalFlag1(rs.getString("FATAL_FLAG_1"));
				templateNameVb.setLogTextFlag1(rs.getString("LOG_TEXT_FLAG_1"));
				templateNameVb.setDmlStatus1(rs.getString("DML_STATUS_1"));
				if(ValidationUtil.isValid(rs.getString("DML_2"))) {
					templateNameVb.setDml2(rs.getString("DML_2"));
					templateNameVb.setOldDml2(rs.getString("DML_2"));	
				}else {
					templateNameVb.setDml2("");
					templateNameVb.setOldDml2("");
				}
				
				templateNameVb.setFatalFlag2(rs.getString("FATAL_FLAG_2"));
				templateNameVb.setLogTextFlag2(rs.getString("LOG_TEXT_FLAG_2"));
				templateNameVb.setDmlStatus2(rs.getString("DML_STATUS_2"));
				
				if(ValidationUtil.isValid(rs.getString("DML_3"))) {
					templateNameVb.setDml3(rs.getString("DML_3"));
					templateNameVb.setOldDml3(rs.getString("DML_3"));	
				}else {
					templateNameVb.setDml3("");
					templateNameVb.setOldDml3("");
				}
				
				templateNameVb.setFatalFlag3(rs.getString("FATAL_FLAG_3"));
				templateNameVb.setLogTextFlag3(rs.getString("LOG_TEXT_FLAG_3"));
				templateNameVb.setDmlStatus3(rs.getString("DML_STATUS_3"));
				
				if(ValidationUtil.isValid(rs.getString("DML_4"))) {
					templateNameVb.setDml4(rs.getString("DML_4"));
					templateNameVb.setOldDml4(rs.getString("DML_4"));	
				}else {
					templateNameVb.setDml4("");
					templateNameVb.setOldDml4("");
				}
				templateNameVb.setFatalFlag4(rs.getString("FATAL_FLAG_4"));
				templateNameVb.setLogTextFlag4(rs.getString("LOG_TEXT_FLAG_4"));
				templateNameVb.setDmlStatus4(rs.getString("DML_STATUS_4"));
				
				if(ValidationUtil.isValid(rs.getString("DML_5"))) {
					templateNameVb.setDml5(rs.getString("DML_5"));
					templateNameVb.setOldDml5(rs.getString("DML_5"));	
				}else {
					templateNameVb.setDml5("");
					templateNameVb.setOldDml5("");
				}
				templateNameVb.setFatalFlag5(rs.getString("FATAL_FLAG_5"));
				templateNameVb.setLogTextFlag5(rs.getString("LOG_TEXT_FLAG_5"));
				templateNameVb.setDmlStatus5(rs.getString("DML_STATUS_5"));
				
				if(ValidationUtil.isValid(rs.getString("DML_6"))) {
					templateNameVb.setDml6(rs.getString("DML_6"));
					templateNameVb.setOldDml6(rs.getString("DML_6"));	
				}else {
					templateNameVb.setDml6("");
					templateNameVb.setOldDml6("");
				}
				templateNameVb.setFatalFlag6(rs.getString("FATAL_FLAG_6"));
				templateNameVb.setLogTextFlag6(rs.getString("LOG_TEXT_FLAG_6"));
				templateNameVb.setDmlStatus6(rs.getString("DML_STATUS_6"));
				if(ValidationUtil.isValid(rs.getString("DML_7"))) {
					templateNameVb.setDml7(rs.getString("DML_7"));
					templateNameVb.setOldDml7(rs.getString("DML_7"));	
				}else {
					templateNameVb.setDml7("");
					templateNameVb.setOldDml7("");
				}
				templateNameVb.setFatalFlag7(rs.getString("FATAL_FLAG_7"));
				templateNameVb.setLogTextFlag7(rs.getString("LOG_TEXT_FLAG_7"));
				templateNameVb.setDmlStatus7(rs.getString("DML_STATUS_7"));
				if(ValidationUtil.isValid(rs.getString("DML_8"))) {
					templateNameVb.setDml8(rs.getString("DML_8"));
					templateNameVb.setOldDml8(rs.getString("DML_8"));	
				}else {
					templateNameVb.setDml8("");
					templateNameVb.setOldDml8("");
				}
				
				templateNameVb.setFatalFlag8(rs.getString("FATAL_FLAG_8"));
				templateNameVb.setLogTextFlag8(rs.getString("LOG_TEXT_FLAG_8"));
				templateNameVb.setDmlStatus8(rs.getString("DML_STATUS_8"));
				
				if(ValidationUtil.isValid(rs.getString("DML_9"))) {
					templateNameVb.setDml9(rs.getString("DML_9"));
					templateNameVb.setOldDml9(rs.getString("DML_9"));	
				}else {
					templateNameVb.setDml9("");
					templateNameVb.setOldDml9("");
				}
				templateNameVb.setFatalFlag9(rs.getString("FATAL_FLAG_9"));
				templateNameVb.setLogTextFlag9(rs.getString("LOG_TEXT_FLAG_9"));
				templateNameVb.setDmlStatus9(rs.getString("DML_STATUS_9"));
				if(ValidationUtil.isValid(rs.getString("DML_10"))) {
					templateNameVb.setDml10(rs.getString("DML_10"));
					templateNameVb.setOldDml10(rs.getString("DML_10"));	
				}else {
					templateNameVb.setDml10("");
					templateNameVb.setOldDml10("");
				}
				
				templateNameVb.setFatalFlag10(rs.getString("FATAL_FLAG_10"));
				templateNameVb.setLogTextFlag10(rs.getString("LOG_TEXT_FLAG_10"));
				templateNameVb.setDmlStatus10(rs.getString("DML_STATUS_10"));
				templateNameVb.setFatalFlagAt(rs.getInt("FATAL_FLAG_AT"));
				templateNameVb.setLogTextFlagAt(rs.getInt("LOG_TEXT_FLAG_AT"));
				templateNameVb.setDmlStatusNt(rs.getInt("DML_STATUS_NT"));
				templateNameVb.setMappingStatusNt(rs.getInt("MAPPING_STATUS_NT"));
				templateNameVb.setMappingStatus(rs.getInt("MAPPING_STATUS"));
				templateNameVb.setDbStatus(rs.getInt("MAPPING_STATUS"));
				templateNameVb.setCommitFlag(rs.getString("COMMIT_FLAG"));
				templateNameVb.setRecordIndicatorNt(rs.getInt("RECORD_INDICATOR_NT"));
				templateNameVb.setRecordIndicator(rs.getInt("RECORD_INDICATOR"));
				templateNameVb.setMaker(rs.getLong("MAKER"));
				templateNameVb.setVerifier(rs.getLong("VERIFIER"));
				templateNameVb.setInternalStatus(rs.getInt("INTERNAL_STATUS"));
				templateNameVb.setDateCreation(rs.getString("DATE_CREATION"));
				templateNameVb.setDateLastModified(rs.getString("DATE_LAST_MODIFIED"));
				if(rs.getString("MAPPING_STATUS_DESC") != null){
					templateNameVb.setStatusDesc(rs.getString("MAPPING_STATUS_DESC"));
				}else{
					templateNameVb.setStatusDesc("");
				}
				if(rs.getString("RECORD_INDICATOR_DESC") != null){
					templateNameVb.setRecordIndicatorDesc(rs.getString("RECORD_INDICATOR_DESC"));
				}else{
					templateNameVb.setRecordIndicatorDesc("");
				}
				templateNameVb.setMakerName(rs.getString("MAKER_NAME"));
				templateNameVb.setVerifierName(rs.getString("VERIFIER_NAME"));
				return templateNameVb;
			}
		};
		return mapper;
	}
	/*public List<TemplateNameGBMVb> getQueryPopupResults(TemplateNameGBMVb dObj){
		
		Vector<Object> params = new Vector<Object>();
		StringBuffer strBufApprove = new StringBuffer("Select TAppr.TEMPLATE_NAME, TAppr.GENERAL_DESCRIPTION, TAppr.FEED_CATEGORY," +
				"TAppr.PROGRAM,(SELECT PROGRAM_DESCRIPTION FROM PROGRAMS WHERE PROGRAM =TAppr.PROGRAM) AS PROGRAM_DESC, TAppr.DEFAULT_ACQ_PROCESS_TYPE_AT, " +
				"TAppr.DEFAULT_ACQ_PROCESS_TYPE,TAppr.VIEW_NAME,TAppr.FEED_STG_NAME, " +
				"TAppr.TEMPLATE_STATUS_NT," +
				"TAppr.TEMPLATE_STATUS,TAppr.PROCESS_SEQUENCE,TAppr.FILE_PATTERN ,TAppr.EXCEL_FILE_PATTERN , TAppr.EXCEL_TEMPLATE_ID," +
	         		   "Format(TAppr.EFFECTIVE_DATE, 'dd-MM-yyyy') EFFECTIVE_DATE, "+
				"TAppr.RECORD_INDICATOR_NT,TAppr.RECORD_INDICATOR, TAppr.MAKER, TAppr.VERIFIER," +
				"TAppr.INTERNAL_STATUS, Format(TAppr.DATE_LAST_MODIFIED, 'dd-MM-yyyy HH:mm:ss') DATE_LAST_MODIFIED," +
				"Format(TAppr.DATE_CREATION, 'dd-MM-yyyy HH:mm:ss') DATE_CREATION" +
				" From TEMPLATE_NAMES TAppr ");
		
		String strWhereNotExists = new String( "  Not Exists (Select 'X' From TEMPLATE_NAMES_PEND TPend Where TPend.TEMPLATE_NAME = TAppr.TEMPLATE_NAME)");
		
		StringBuffer strBufPending = new StringBuffer("Select TPend.TEMPLATE_NAME, TPend.GENERAL_DESCRIPTION, TPend.FEED_CATEGORY," +
				"TPend.PROGRAM,(SELECT PROGRAM_DESCRIPTION FROM PROGRAMS WHERE PROGRAM =TPend.PROGRAM) AS PROGRAM_DESC, TPend.DEFAULT_ACQ_PROCESS_TYPE_AT, " +
				"TPend.DEFAULT_ACQ_PROCESS_TYPE,TPend.VIEW_NAME,TPend.FEED_STG_NAME, " +
				"TPend.TEMPLATE_STATUS_NT," +
				"TPend.TEMPLATE_STATUS,TPend.PROCESS_SEQUENCE, TPend.FILE_PATTERN ,TPend.EXCEL_FILE_PATTERN ,TPend.EXCEL_TEMPLATE_ID," +
	            "Format(TPend.EFFECTIVE_DATE, 'dd-MM-yyyy') EFFECTIVE_DATE, "+
				"TPend.RECORD_INDICATOR_NT,TPend.RECORD_INDICATOR, TPend.MAKER, TPend.VERIFIER," +
				"TPend.INTERNAL_STATUS, Format(TPend.DATE_LAST_MODIFIED, 'dd-MM-yyyy HH:mm:ss') DATE_LAST_MODIFIED," +
				"Format(TPend.DATE_CREATION, 'dd-MM-yyyy HH:mm:ss') DATE_CREATION" +
				" From TEMPLATE_NAMES_PEND TPend ");
		try
		{
			//check if the column [TEMPLATE_STATUS] should be included in the query
			if (dObj.getTemplateStatus() != -1)
			{
				params.addElement(new Integer(dObj.getTemplateStatus()));
				CommonUtils.addToQuery("TAppr.TEMPLATE_STATUS = ?", strBufApprove);
				CommonUtils.addToQuery("TPend.TEMPLATE_STATUS = ?", strBufPending);
			}
			//check if the column [TEMPLATE_NAME] should be included in the query
			if (ValidationUtil.isValid(dObj.getTemplateName()))
			{
				params.addElement("%" + dObj.getTemplateName()+ "%");
				CommonUtils.addToQuery("TAppr.TEMPLATE_NAME LIKE ?", strBufApprove);
				CommonUtils.addToQuery("TPend.TEMPLATE_NAME LIKE ?", strBufPending);
			}
			//check if the column [GENERAL_DESCRIPTION] should be included in the query
			if (ValidationUtil.isValid(dObj.getGeneralDescription()))
			{
				params.addElement(dObj.getGeneralDescription());
				CommonUtils.addToQuery("TAppr.GENERAL_DESCRIPTION = ?", strBufApprove);
				CommonUtils.addToQuery("TPend.GENERAL_DESCRIPTION = ?", strBufPending);
			}
			//check if the column [FEED_CATEGORY] should be included in the query
			if (ValidationUtil.isValid(dObj.getFeedCategory()))
			{
				params.addElement("%" + dObj.getFeedCategory()+ "%");
				CommonUtils.addToQuery("TAppr.FEED_CATEGORY LIKE ?", strBufApprove);
				CommonUtils.addToQuery("TPend.FEED_CATEGORY LIKE ?", strBufPending);
			}
			//check if the column [ACQUISITION_PROCESS_TYPE] should be included in the query
			if (ValidationUtil.isValid(dObj.getDefaultAcquProcessType()) && !"-1".equalsIgnoreCase(dObj.getDefaultAcquProcessType()))
			{
				params.addElement(dObj.getDefaultAcquProcessType().toUpperCase());
				CommonUtils.addToQuery("TAppr.DEFAULT_ACQ_PROCESS_TYPE = ?", strBufApprove);
				CommonUtils.addToQuery("TPend.DEFAULT_ACQ_PROCESS_TYPE = ?", strBufPending);
			}
			if (ValidationUtil.isValid(dObj.getProgram()))
			{
				params.addElement("%" + dObj.getProgram()+ "%");
				CommonUtils.addToQuery("TAppr.PROGRAM LIKE ?", strBufApprove);
				CommonUtils.addToQuery("TPend.PROGRAM LIKE ?", strBufPending);
			}
			if (ValidationUtil.isValid(dObj.getFeedStgName()))
			{
				params.addElement("%" + dObj.getFeedStgName()+ "%");
				CommonUtils.addToQuery("TAppr.FEED_STG_NAME LIKE ?", strBufApprove);
				CommonUtils.addToQuery("TPend.FEED_STG_NAME LIKE ?", strBufPending);
			}
			if (ValidationUtil.isValid(dObj.getViewName()))
			{
				params.addElement("%" + dObj.getViewName()+ "%");
				CommonUtils.addToQuery("TAppr.VIEW_NAME LIKE ?", strBufApprove);
				CommonUtils.addToQuery("TPend.VIEW_NAME LIKE ?", strBufPending);
			}
			if (ValidationUtil.isValid(dObj.getProcessSequence())){
				params.addElement("%" +dObj.getProcessSequence() + "%");
				CommonUtils.addToQuery("UPPER(TAppr.PROCESS_SEQUENCE) LIKE ?", strBufApprove);
				CommonUtils.addToQuery("UPPER(TPend.PROCESS_SEQUENCE) LIKE ?", strBufPending);
			}
			if (ValidationUtil.isValid(dObj.getFilePattern())){
				params.addElement("%" +dObj.getFilePattern() + "%");
				CommonUtils.addToQuery("UPPER(TAppr.FILE_PATTERN) LIKE ?", strBufApprove);
				CommonUtils.addToQuery("UPPER(TPend.FILE_PATTERN) LIKE ?", strBufPending);
			}
			if (ValidationUtil.isValid(dObj.getExcelFilePattern())){
				params.addElement("%" +dObj.getExcelFilePattern() + "%");
				CommonUtils.addToQuery("UPPER(TAppr.EXCEL_FILE_PATTERN) LIKE ?", strBufApprove);
				CommonUtils.addToQuery("UPPER(TPend.EXCEL_FILE_PATTERN) LIKE ?", strBufPending);
			}
			if (ValidationUtil.isValid(dObj.getExcelTemplateId()))
			{
				params.addElement( dObj.getExcelTemplateId().toUpperCase() );
				CommonUtils.addToQuery("TAppr.EXCEL_TEMPLATE_ID = ?", strBufApprove);
				CommonUtils.addToQuery("TPend.EXCEL_TEMPLATE_ID = ?", strBufPending);
			}
			if (ValidationUtil.isValid(dObj.getEffectiveDate()))
			{
				params.addElement(dObj.getEffectiveDate());
				CommonUtils.addToQuery("TAppr.EFFECTIVE_DATE = ?", strBufApprove);
				CommonUtils.addToQuery("TPend.EFFECTIVE_DATE = ?", strBufPending);
			}
			//check if the column [RECORD_INDICATOR] should be included in the query
			if (dObj.getRecordIndicator() != -1){
				if (dObj.getRecordIndicator() > 3){
					params.addElement(new Integer(0));
					CommonUtils.addToQuery("TAppr.RECORD_INDICATOR > ?", strBufApprove);
					CommonUtils.addToQuery("TPend.RECORD_INDICATOR > ?", strBufPending);
				}else{
					params.addElement(new Integer(dObj.getRecordIndicator()));
					CommonUtils.addToQuery("TAppr.RECORD_INDICATOR = ?", strBufApprove);
					CommonUtils.addToQuery("TPend.RECORD_INDICATOR = ?", strBufPending);
				}
			}
			String orderBy=" Order By TEMPLATE_NAME";
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
	}*/
	public List<TemplateNameGBMVb> getQueryPopupResults(TemplateNameGBMVb dObj){
		
		Vector<Object> params = new Vector<Object>();

		StringBuffer strBufApprove = new StringBuffer("SELECT * FROM (SELECT  TAPPR.COUNTRY, TAPPR.LE_BOOK, TAPPR.PROGRAM,"
				+programDescAppr
				+ " TAPPR.EXECUTION_SEQUENCE,TAppr.MAPPING_STATUS,TAppr.RECORD_INDICATOR "
				+ " , "+RecordIndicatorNtApprDesc+" , "+mappingStatusNtApprDesc +" , "+makerApprDesc+" , "+verifierApprDesc+" , "+exceutionTypeNtApprDesc
				+ ", TAppr.MAKER, TAppr.VERIFIER, TAppr.INTERNAL_STATUS, "
				+ " TAppr.EXECUTION_TYPE,"
				+ dbFunctionFormats("TAPPR.DATE_LAST_MODIFIED", "DATETIME_FORMAT", null)
				+ " DATE_LAST_MODIFIED,TAPPR.DATE_LAST_MODIFIED DATE_LAST_MODIFIED_1," + " "
				+ dbFunctionFormats("TAPPR.DATE_CREATION", "DATETIME_FORMAT", null) + " DATE_CREATION "
				+ "FROM RA_GENERICBUILD_MAPPINGS TAPPR) TAPPR ");
		
		String strWhereNotExists = new String( "  Not Exists (Select 'X' From RA_GENERICBUILD_MAPPINGS_PEND TPEND  Where TPEND.PROGRAM = TAPPR.PROGRAM"
				+ " AND TPEND.EXECUTION_SEQUENCE = TAPPR.EXECUTION_SEQUENCE AND TPEND.COUNTRY = TAPPR.COUNTRY AND TPEND.LE_BOOK = TAPPR.LE_BOOK)");
						
		StringBuffer strBufPending = new StringBuffer("SELECT * FROM (SELECT  TPEND.COUNTRY, TPEND.LE_BOOK, TPEND.PROGRAM,"
				+programDescPend
				+ " TPEND.EXECUTION_SEQUENCE,TPEND.MAPPING_STATUS,TPEND.RECORD_INDICATOR"
				+ " , "+RecordIndicatorNtPendDesc+" , "+mappingStatusNtPendDesc+" , "+makerPendDesc+" , "+verifierPendDesc+" , "+exceutionTypeNtPendDesc
				+ ",TPend.MAKER, TPend.VERIFIER, TPend.INTERNAL_STATUS, "
				+ "TPEND.EXECUTION_TYPE,"
				+ dbFunctionFormats("TPEND.DATE_LAST_MODIFIED", "DATETIME_FORMAT", null)
				+ " DATE_LAST_MODIFIED,TPEND.DATE_LAST_MODIFIED DATE_LAST_MODIFIED_1," + " "
				+ dbFunctionFormats("TPEND.DATE_CREATION", "DATETIME_FORMAT", null) + " DATE_CREATION "
				+ " FROM RA_GENERICBUILD_MAPPINGS_PEND TPEND)TPEND  ");
					
		try
		{
			if (dObj.getSmartSearchOpt() != null && dObj.getSmartSearchOpt().size() > 0) {
				int count = 1;
				for (SmartSearchVb data : dObj.getSmartSearchOpt()) {
					if (count == dObj.getSmartSearchOpt().size()) {
						data.setJoinType("");
					} else {
						if (!ValidationUtil.isValid(data.getJoinType()) && !("AND".equalsIgnoreCase(data.getJoinType())
								|| "OR".equalsIgnoreCase(data.getJoinType()))) {
							data.setJoinType("AND");
						}
					}
					String val = CommonUtils.criteriaBasedVal(data.getCriteria(), data.getValue());
					switch (data.getObject()) {
					case "country":
						CommonUtils.addToQuerySearch(" upper(TAppr.COUNTRY) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.COUNTRY) " + val, strBufPending,
								data.getJoinType());
						break;

					case "programDesc":
						CommonUtils.addToQuerySearch(" upper(TAppr.PROGRAM_desc) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.PROGRAM_desc) " + val, strBufPending,
								data.getJoinType());
						break;
						
					case "program":
						CommonUtils.addToQuerySearch(" upper(TAppr.PROGRAM) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.PROGRAM) " + val, strBufPending,
								data.getJoinType());
						break;
						
					case "leBook":
						CommonUtils.addToQuerySearch(" upper(TAppr.LE_BOOK) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.LE_BOOK) " + val, strBufPending,
								data.getJoinType());
						break;
					case "statusDesc":
						CommonUtils.addToQuerySearch(" upper(TAppr.MAPPING_STATUS_DESC) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.MAPPING_STATUS_DESC) " + val, strBufPending,
								data.getJoinType());
						break;

					case "recordIndicatorDesc":
						CommonUtils.addToQuerySearch(" upper(TAppr.RECORD_INDICATOR_DESC) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.RECORD_INDICATOR_DESC) " + val, strBufPending,
								data.getJoinType());
						break;
						
					case "executionTypeDesc":
						CommonUtils.addToQuerySearch(" upper(TAppr.EXECUTION_TYPE_DESC) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.EXECUTION_TYPE_DESC) " + val, strBufPending,
								data.getJoinType());
						break;
						
						
					default:
					}
					count++;
				}
			}
			VisionUsersVb visionUsersVb = CustomContextHolder.getContext();
			visionUsersVb = commonDao.getRestrictionInfo(visionUsersVb);
			if (("Y".equalsIgnoreCase(visionUsersVb.getUpdateRestriction()))) {
				if(ValidationUtil.isValid(visionUsersVb.getCountry())){
					CommonUtils.addToQuery(" COUNTRY"+getDbFunction("PIPELINE", "")+"'-'"+getDbFunction("PIPELINE", "")+"LE_BOOK IN ("+visionUsersVb.getCountry()+") ", strBufApprove);
					CommonUtils.addToQuery(" COUNTRY"+getDbFunction("PIPELINE", "")+"'-'"+getDbFunction("PIPELINE", "")+"LE_BOOK IN ("+visionUsersVb.getCountry()+") ", strBufPending);
				}
			}
			String orderBy=" Order By DATE_LAST_MODIFIED_1 DESC";
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
	public List<TemplateNameGBMVb> getQueryResults(TemplateNameGBMVb dObj, int intStatus){
		String calLeBook = removeDescLeBook(dObj.getLeBook());
		dObj.setVerificationRequired(false);
		List<TemplateNameGBMVb> collTemp = null;
		final int intKeyFieldsCount = 4;
		setServiceDefaults();
		
		String strQueryAppr = new String("SELECT TAppr.COUNTRY, TAppr.LE_BOOK, "
				+ "TAppr.PROGRAM, "
				+programDescAppr
				+ " TAppr.EXECUTION_SEQUENCE, TAppr.EXECUTION_TYPE_AT, TAppr.EXECUTION_TYPE, TAppr.MAIN_QUERY, " +
			"TAppr.DML_1, TAppr.FATAL_FLAG_1, TAppr.LOG_TEXT_FLAG_1, TAppr.DML_STATUS_1, TAppr.DML_2, TAppr.FATAL_FLAG_2, TAppr.LOG_TEXT_FLAG_2, TAppr.DML_STATUS_2, " +
			"TAppr.DML_3, TAppr.FATAL_FLAG_3, TAppr.LOG_TEXT_FLAG_3, TAppr.DML_STATUS_3, TAppr.DML_4, TAppr.FATAL_FLAG_4, TAppr.LOG_TEXT_FLAG_4, TAppr.DML_STATUS_4, " +
			"TAppr.DML_5, TAppr.FATAL_FLAG_5, TAppr.LOG_TEXT_FLAG_5, TAppr.DML_STATUS_5, TAppr.DML_6, TAppr.FATAL_FLAG_6, TAppr.LOG_TEXT_FLAG_6, TAppr.DML_STATUS_6, " +
			"TAppr.DML_7, TAppr.FATAL_FLAG_7, TAppr.LOG_TEXT_FLAG_7, TAppr.DML_STATUS_7, TAppr.DML_8, TAppr.FATAL_FLAG_8, TAppr.LOG_TEXT_FLAG_8, TAppr.DML_STATUS_8, " +
			"TAppr.DML_9, TAppr.FATAL_FLAG_9, TAppr.LOG_TEXT_FLAG_9, TAppr.DML_STATUS_9, TAppr.DML_10, TAppr.FATAL_FLAG_10, TAppr.LOG_TEXT_FLAG_10, TAppr.DML_STATUS_10, " +
			"TAppr.FATAL_FLAG_AT, TAppr.LOG_TEXT_FLAG_AT, TAppr.DML_STATUS_NT, TAppr.MAPPING_STATUS_NT, TAppr.MAPPING_STATUS,TAppr.COMMIT_FLAG, TAppr.RECORD_INDICATOR_NT, TAppr.RECORD_INDICATOR, " +RecordIndicatorNtApprDesc+","+mappingStatusNtApprDesc +" , "+makerApprDesc+" , "+verifierApprDesc+
			",TAppr.MAKER, TAppr.VERIFIER, TAppr.INTERNAL_STATUS, TAppr.DATE_LAST_MODIFIED, TAppr.DATE_CREATION "
			+ " FROM RA_GENERICBUILD_MAPPINGS TAppr"
			+" WHERE "
			+ " TAppr.COUNTRY = ? AND TAppr.LE_BOOK = ? AND TAppr.PROGRAM = ? AND TAppr.EXECUTION_SEQUENCE = ?");
		String strQueryPend = new String("SELECT TPend.COUNTRY, TPend.LE_BOOK, TPend.PROGRAM,"
				+programDescPend
				+ " TPend.EXECUTION_SEQUENCE, TPend.EXECUTION_TYPE_AT, TPend.EXECUTION_TYPE, TPend.MAIN_QUERY, " +
			"TPend.DML_1, TPend.FATAL_FLAG_1, TPend.LOG_TEXT_FLAG_1, TPend.DML_STATUS_1, TPend.DML_2, TPend.FATAL_FLAG_2, TPend.LOG_TEXT_FLAG_2, TPend.DML_STATUS_2, " +
			"TPend.DML_3, TPend.FATAL_FLAG_3, TPend.LOG_TEXT_FLAG_3, TPend.DML_STATUS_3, TPend.DML_4, TPend.FATAL_FLAG_4, TPend.LOG_TEXT_FLAG_4, TPend.DML_STATUS_4, " +
			"TPend.DML_5, TPend.FATAL_FLAG_5, TPend.LOG_TEXT_FLAG_5, TPend.DML_STATUS_5, TPend.DML_6, TPend.FATAL_FLAG_6, TPend.LOG_TEXT_FLAG_6, TPend.DML_STATUS_6, " +
			"TPend.DML_7, TPend.FATAL_FLAG_7, TPend.LOG_TEXT_FLAG_7, TPend.DML_STATUS_7, TPend.DML_8, TPend.FATAL_FLAG_8, TPend.LOG_TEXT_FLAG_8, TPend.DML_STATUS_8, " +
			"TPend.DML_9, TPend.FATAL_FLAG_9, TPend.LOG_TEXT_FLAG_9, TPend.DML_STATUS_9, TPend.DML_10, TPend.FATAL_FLAG_10, TPend.LOG_TEXT_FLAG_10, TPend.DML_STATUS_10, " +
			"TPend.FATAL_FLAG_AT, TPend.LOG_TEXT_FLAG_AT, TPend.DML_STATUS_NT, TPend.MAPPING_STATUS_NT, TPend.MAPPING_STATUS, TPend.COMMIT_FLAG, TPend.RECORD_INDICATOR_NT, TPend.RECORD_INDICATOR, " +RecordIndicatorNtPendDesc+" , "+mappingStatusNtPendDesc+" , "+makerPendDesc+" , "+verifierPendDesc+
			",TPend.MAKER, TPend.VERIFIER, TPend.INTERNAL_STATUS, TPend.DATE_LAST_MODIFIED, TPend.DATE_CREATION"
			+ " FROM RA_GENERICBUILD_MAPPINGS_PEND TPend"
			+" WHERE  "
			+" TPend.COUNTRY = ? AND TPend.LE_BOOK = ? AND TPend.PROGRAM = ? AND TPend.EXECUTION_SEQUENCE = ?");
		
			Object objParams[] = new Object[intKeyFieldsCount];
			objParams[0] = new String(dObj.getCountry());
			objParams[1] = new String(calLeBook);
			objParams[2] = new String(dObj.getProgram().toUpperCase());
			if(ValidationUtil.isValid(dObj.getExecutionSequence()))
				objParams[3] = new String(dObj.getExecutionSequence());
			else
				objParams[3] = null;

		try
		{
			if(intStatus == 0)
			{
				logger.info("Executing approved query");
				collTemp = getJdbcTemplate().query(strQueryAppr.toString(),objParams,getMapperTemplateNameProgram());
				for(TemplateNameGBMVb dObjLocal:collTemp){
					dObjLocal.setComment(dObj.getComment());
					dObjLocal.setClickStatus(dObj.getClickStatus());
				}
			}else{
				logger.info("Executing pending query");
				collTemp = getJdbcTemplate().query(strQueryPend.toString(),objParams,getMapperTemplateNameProgram());
				for(TemplateNameGBMVb dObjLocal:collTemp){
					dObjLocal.setComment(dObj.getComment());
					dObjLocal.setClickStatus(dObj.getClickStatus());
				}
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
	protected List<TemplateNameGBMVb> selectApprovedRecord(TemplateNameGBMVb vObject){
		return getQueryResults(vObject, Constants.STATUS_ZERO);
	}
	@Override
	protected List<TemplateNameGBMVb> doSelectPendingRecord(TemplateNameGBMVb vObject){
		return getQueryResults(vObject, Constants.STATUS_PENDING);
	}
	@Override
	protected int getStatus(TemplateNameGBMVb records){return records.getMappingStatus();}
	
	@Override
	protected void setStatus(TemplateNameGBMVb vObject,int status){vObject.setMappingStatus(status);}
	
	@Override
	protected int doInsertionAppr(TemplateNameGBMVb vObject){
		int verNo = getCommVersionNo(vObject);
		insertIntoComment(vObject, verNo);
		String calLeBook = removeDescLeBook(vObject.getLeBook());
		if(!ValidationUtil.isValid(vObject.getFatalFlag1())||!ValidationUtil.isValid(vObject.getLogTextFlag1())||!ValidationUtil.isValid(vObject.getDmlStatus1())){
			vObject.setFatalFlag1("Y");
			vObject.setLogTextFlag1("DEFAULT");
			vObject.setDmlStatus1("0");
		}
		if(!ValidationUtil.isValid(vObject.getFatalFlag2())||!ValidationUtil.isValid(vObject.getLogTextFlag2())||!ValidationUtil.isValid(vObject.getDmlStatus2())){
			vObject.setFatalFlag2("Y");
			vObject.setLogTextFlag2("DEFAULT");
			vObject.setDmlStatus2("0");
		}
		if(!ValidationUtil.isValid(vObject.getFatalFlag3())||!ValidationUtil.isValid(vObject.getLogTextFlag3())||!ValidationUtil.isValid(vObject.getDmlStatus3())){
			vObject.setFatalFlag3("Y");
			vObject.setLogTextFlag3("DEFAULT");
			vObject.setDmlStatus3("0");
		}
		if(!ValidationUtil.isValid(vObject.getFatalFlag4())||!ValidationUtil.isValid(vObject.getLogTextFlag4())||!ValidationUtil.isValid(vObject.getDmlStatus4())){
			vObject.setFatalFlag4("Y");
			vObject.setLogTextFlag4("DEFAULT");
			vObject.setDmlStatus4("0");
		}
		if(!ValidationUtil.isValid(vObject.getFatalFlag5())||!ValidationUtil.isValid(vObject.getLogTextFlag5())||!ValidationUtil.isValid(vObject.getDmlStatus5())){
			vObject.setFatalFlag5("Y");
			vObject.setLogTextFlag5("DEFAULT");
			vObject.setDmlStatus5("0");
		}
		if(!ValidationUtil.isValid(vObject.getFatalFlag6())||!ValidationUtil.isValid(vObject.getLogTextFlag6())||!ValidationUtil.isValid(vObject.getDmlStatus6())){
			vObject.setFatalFlag6("Y");
			vObject.setLogTextFlag6("DEFAULT");
			vObject.setDmlStatus6("0");
		}
		if(!ValidationUtil.isValid(vObject.getFatalFlag7())||!ValidationUtil.isValid(vObject.getLogTextFlag7())||!ValidationUtil.isValid(vObject.getDmlStatus7())){
			vObject.setFatalFlag7("Y");
			vObject.setLogTextFlag7("DEFAULT");
			vObject.setDmlStatus7("0");
		}
		if(!ValidationUtil.isValid(vObject.getFatalFlag8())||!ValidationUtil.isValid(vObject.getLogTextFlag8())||!ValidationUtil.isValid(vObject.getDmlStatus8())){
			vObject.setFatalFlag8("Y");
			vObject.setLogTextFlag8("DEFAULT");
			vObject.setDmlStatus8("0");
		}
		if(!ValidationUtil.isValid(vObject.getFatalFlag9())||!ValidationUtil.isValid(vObject.getLogTextFlag9())||!ValidationUtil.isValid(vObject.getDmlStatus9())){
			vObject.setFatalFlag9("Y");
			vObject.setLogTextFlag9("DEFAULT");
			vObject.setDmlStatus9("0");
		}
		if(!ValidationUtil.isValid(vObject.getFatalFlag10())||!ValidationUtil.isValid(vObject.getLogTextFlag10())||!ValidationUtil.isValid(vObject.getDmlStatus10())){
			vObject.setFatalFlag10("Y");
			vObject.setLogTextFlag10("DEFAULT");
			vObject.setDmlStatus10("0");
		}
		PreparedStatement ps= null;
		int result=0;
		try {
			String query = " Insert into RA_GENERICBUILD_MAPPINGS (COUNTRY, LE_BOOK, PROGRAM, EXECUTION_SEQUENCE, EXECUTION_TYPE_AT, EXECUTION_TYPE "+
				" , FATAL_FLAG_1, LOG_TEXT_FLAG_1, DML_STATUS_1, FATAL_FLAG_2, LOG_TEXT_FLAG_2, DML_STATUS_2 "+
				" , FATAL_FLAG_3, LOG_TEXT_FLAG_3, DML_STATUS_3, FATAL_FLAG_4, LOG_TEXT_FLAG_4, DML_STATUS_4 "+
				" , FATAL_FLAG_5, LOG_TEXT_FLAG_5, DML_STATUS_5, FATAL_FLAG_6, LOG_TEXT_FLAG_6, DML_STATUS_6 "+
				" , FATAL_FLAG_7, LOG_TEXT_FLAG_7, DML_STATUS_7, FATAL_FLAG_8, LOG_TEXT_FLAG_8, DML_STATUS_8 "+
				" , FATAL_FLAG_9, LOG_TEXT_FLAG_9, DML_STATUS_9, FATAL_FLAG_10, LOG_TEXT_FLAG_10, DML_STATUS_10 "+
				" , FATAL_FLAG_AT, LOG_TEXT_FLAG_AT, DML_STATUS_NT, MAPPING_STATUS_NT, MAPPING_STATUS, COMMIT_FLAG "+
				" , RECORD_INDICATOR_NT, RECORD_INDICATOR, MAKER, VERIFIER, INTERNAL_STATUS, DATE_LAST_MODIFIED "+
				" , DATE_CREATION, MAIN_QUERY, DML_1, DML_2, DML_3, DML_4, DML_5, DML_6, DML_7, DML_8, DML_9, DML_10  ) "+
				" Values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? "+
				" , ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?, "+getDbFunction("SYSDATE")+", "+getDbFunction("SYSDATE")+", ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		
		
			Object[] args = { vObject.getCountry(), calLeBook, vObject.getProgram(), vObject.getExecutionSequence(), vObject.getExecutionTypeAt(), 
				vObject.getExecutionType(), vObject.getFatalFlag1(), vObject.getLogTextFlag1(), vObject.getDmlStatus1(), 
				vObject.getFatalFlag2(), vObject.getLogTextFlag2(), vObject.getDmlStatus2(), 
				vObject.getFatalFlag3(), vObject.getLogTextFlag3(), vObject.getDmlStatus3(), 
				vObject.getFatalFlag4(), vObject.getLogTextFlag4(), vObject.getDmlStatus4(), 
				vObject.getFatalFlag5(), vObject.getLogTextFlag5(), vObject.getDmlStatus5(), 
				vObject.getFatalFlag6(), vObject.getLogTextFlag6(), vObject.getDmlStatus6(), 
				vObject.getFatalFlag7(), vObject.getLogTextFlag7(), vObject.getDmlStatus7(), 
				vObject.getFatalFlag8(), vObject.getLogTextFlag8(), vObject.getDmlStatus8(), 
				vObject.getFatalFlag9(), vObject.getLogTextFlag9(), vObject.getDmlStatus9(), 
				vObject.getFatalFlag10(), vObject.getLogTextFlag10(), vObject.getDmlStatus10(), 
				vObject.getFatalFlagAt(), vObject.getLogTextFlagAt(), vObject.getDmlStatusNt(), vObject.getMappingStatusNt(),vObject.getMappingStatus(),
				vObject.getCommitFlag(),vObject.getRecordIndicatorNt(), vObject.getRecordIndicator(), vObject.getMaker(), 
				vObject.getVerifier(), vObject.getInternalStatus()};
		
		ps= getConnection().prepareStatement(query);
		for(int i=1;i<=args.length;i++){
			ps.setObject(i,args[i-1]);	
		}
		
		String mainQuery = vObject.getMainQuery();
		try{
			if(mainQuery.equalsIgnoreCase(null))
				mainQuery="";
		}catch(Exception e){
			mainQuery="";
		}
		Reader reader = new StringReader(mainQuery);
		ps.setCharacterStream(args.length+1, reader, mainQuery.length());
		
		String dml1 = vObject.getDml1();
		try{
			if(dml1.equalsIgnoreCase(null))
				dml1="";
		}catch(Exception e){
			dml1="";
		}
		reader = new StringReader(dml1);
		ps.setCharacterStream(args.length+2, reader, dml1.length());
			String dml2 = vObject.getDml2();
			try{
				if(dml2.equalsIgnoreCase(null))
					dml2="";
			}catch(Exception e){
				dml2="";
			}
			reader = new StringReader(dml2);
		ps.setCharacterStream(args.length+3, reader, dml2.length());
			String dml3 = vObject.getDml3();
			try{
				if(dml3.equalsIgnoreCase(null))
					dml3="";
			}catch(Exception e){
				dml3="";
			}
			reader = new StringReader(dml3);
		ps.setCharacterStream(args.length+4, reader, dml3.length());
		
			String dml4 = vObject.getDml4();
			try{
				if(dml4.equalsIgnoreCase(null))
					dml4="";
			}catch(Exception e){
				dml4="";
			}
			reader = new StringReader(dml4);
		ps.setCharacterStream(args.length+5, reader, dml4.length());
			String dml5 = vObject.getDml5();
			try{
				if(dml5.equalsIgnoreCase(null))
					dml5="";
			}catch(Exception e){
				dml5="";
			}
			reader = new StringReader(dml5);
		ps.setCharacterStream(args.length+6, reader, dml5.length());
			String dml6 = vObject.getDml6();
			try{
				if(dml6.equalsIgnoreCase(null))
					dml6="";
			}catch(Exception e){
				dml6="";
			}
			reader = new StringReader(dml6);
		ps.setCharacterStream(args.length+7, reader, dml6.length());
			String dml7 = vObject.getDml7();
			try{
				if(dml7.equalsIgnoreCase(null))
					dml7="";
			}catch(Exception e){
				dml7="";
			}
			reader = new StringReader(dml7);
		ps.setCharacterStream(args.length+8, reader, dml7.length());
			String dml8 = vObject.getDml8();
			try{
				if(dml8.equalsIgnoreCase(null))
					dml8="";
			}catch(Exception e){
				dml8="";
			}
			reader = new StringReader(dml8);
		ps.setCharacterStream(args.length+9, reader, dml8.length());
			String dml9 = vObject.getDml9();
			try{
				if(dml9.equalsIgnoreCase(null))
					dml9="";
			}catch(Exception e){
				dml9="";
			}
			reader = new StringReader(dml9);
		ps.setCharacterStream(args.length+10, reader, dml9.length());
			String dml10 = vObject.getDml10();
			try{
				if(dml10.equalsIgnoreCase(null))
					dml10="";
			}catch(Exception e){
				dml10="";
			}
			reader = new StringReader(dml10);
		ps.setCharacterStream(args.length+11, reader, dml10.length());
		
		result = ps.executeUpdate();
		}catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
			strErrorDesc = e.getMessage();
			logger.error("Update Error : "+e.getMessage());
		}finally{
			if (ps != null) {
				try {
					ps.close();
					ps=null;
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return result;
	}
	@Override
	protected int doInsertionPend(TemplateNameGBMVb vObject){
		int verNo = getCommVersionNo(vObject);
		insertIntoComment(vObject, verNo);
		String calLeBook = removeDescLeBook(vObject.getLeBook());
		if(!ValidationUtil.isValid(vObject.getFatalFlag1())||!ValidationUtil.isValid(vObject.getLogTextFlag1())||!ValidationUtil.isValid(vObject.getDmlStatus1())){
			vObject.setFatalFlag1("Y");
			vObject.setLogTextFlag1("DEFAULT");
			vObject.setDmlStatus1("0");
		}
		if(!ValidationUtil.isValid(vObject.getFatalFlag2())||!ValidationUtil.isValid(vObject.getLogTextFlag2())||!ValidationUtil.isValid(vObject.getDmlStatus2())){
			vObject.setFatalFlag2("Y");
			vObject.setLogTextFlag2("DEFAULT");
			vObject.setDmlStatus2("0");
		}
		if(!ValidationUtil.isValid(vObject.getFatalFlag3())||!ValidationUtil.isValid(vObject.getLogTextFlag3())||!ValidationUtil.isValid(vObject.getDmlStatus3())){
			vObject.setFatalFlag3("Y");
			vObject.setLogTextFlag3("DEFAULT");
			vObject.setDmlStatus3("0");
		}
		if(!ValidationUtil.isValid(vObject.getFatalFlag4())||!ValidationUtil.isValid(vObject.getLogTextFlag4())||!ValidationUtil.isValid(vObject.getDmlStatus4())){
			vObject.setFatalFlag4("Y");
			vObject.setLogTextFlag4("DEFAULT");
			vObject.setDmlStatus4("0");
		}
		if(!ValidationUtil.isValid(vObject.getFatalFlag5())||!ValidationUtil.isValid(vObject.getLogTextFlag5())||!ValidationUtil.isValid(vObject.getDmlStatus5())){
			vObject.setFatalFlag5("Y");
			vObject.setLogTextFlag5("DEFAULT");
			vObject.setDmlStatus5("0");
		}
		if(!ValidationUtil.isValid(vObject.getFatalFlag6())||!ValidationUtil.isValid(vObject.getLogTextFlag6())||!ValidationUtil.isValid(vObject.getDmlStatus6())){
			vObject.setFatalFlag6("Y");
			vObject.setLogTextFlag6("DEFAULT");
			vObject.setDmlStatus6("0");
		}
		if(!ValidationUtil.isValid(vObject.getFatalFlag7())||!ValidationUtil.isValid(vObject.getLogTextFlag7())||!ValidationUtil.isValid(vObject.getDmlStatus7())){
			vObject.setFatalFlag7("Y");
			vObject.setLogTextFlag7("DEFAULT");
			vObject.setDmlStatus7("0");
		}
		if(!ValidationUtil.isValid(vObject.getFatalFlag8())||!ValidationUtil.isValid(vObject.getLogTextFlag8())||!ValidationUtil.isValid(vObject.getDmlStatus8())){
			vObject.setFatalFlag8("Y");
			vObject.setLogTextFlag8("DEFAULT");
			vObject.setDmlStatus8("0");
		}
		if(!ValidationUtil.isValid(vObject.getFatalFlag9())||!ValidationUtil.isValid(vObject.getLogTextFlag9())||!ValidationUtil.isValid(vObject.getDmlStatus9())){
			vObject.setFatalFlag9("Y");
			vObject.setLogTextFlag9("DEFAULT");
			vObject.setDmlStatus9("0");
		}
		if(!ValidationUtil.isValid(vObject.getFatalFlag10())||!ValidationUtil.isValid(vObject.getLogTextFlag10())||!ValidationUtil.isValid(vObject.getDmlStatus10())){
			vObject.setFatalFlag10("Y");
			vObject.setLogTextFlag10("DEFAULT");
			vObject.setDmlStatus10("0");
		}
		PreparedStatement ps= null;
		int result=0;
		try {
			String query = " Insert into RA_GENERICBUILD_MAPPINGS_PEND (COUNTRY, LE_BOOK, PROGRAM, EXECUTION_SEQUENCE, EXECUTION_TYPE_AT, EXECUTION_TYPE "+
				" , FATAL_FLAG_1, LOG_TEXT_FLAG_1, DML_STATUS_1, FATAL_FLAG_2, LOG_TEXT_FLAG_2, DML_STATUS_2 "+
				" , FATAL_FLAG_3, LOG_TEXT_FLAG_3, DML_STATUS_3, FATAL_FLAG_4, LOG_TEXT_FLAG_4, DML_STATUS_4 "+
				" , FATAL_FLAG_5, LOG_TEXT_FLAG_5, DML_STATUS_5, FATAL_FLAG_6, LOG_TEXT_FLAG_6, DML_STATUS_6 "+
				" , FATAL_FLAG_7, LOG_TEXT_FLAG_7, DML_STATUS_7, FATAL_FLAG_8, LOG_TEXT_FLAG_8, DML_STATUS_8 "+
				" , FATAL_FLAG_9, LOG_TEXT_FLAG_9, DML_STATUS_9, FATAL_FLAG_10, LOG_TEXT_FLAG_10, DML_STATUS_10 "+
				" , FATAL_FLAG_AT, LOG_TEXT_FLAG_AT, DML_STATUS_NT, MAPPING_STATUS_NT, MAPPING_STATUS, COMMIT_FLAG "+
				" , RECORD_INDICATOR_NT, RECORD_INDICATOR, MAKER, VERIFIER, INTERNAL_STATUS, DATE_LAST_MODIFIED "+
				" , DATE_CREATION, MAIN_QUERY, DML_1, DML_2, DML_3, DML_4, DML_5, DML_6, DML_7, DML_8, DML_9, DML_10  ) "+
				" Values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? "+
				" , ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?, "+getDbFunction("SYSDATE")+","+getDbFunction("SYSDATE")+", ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		
		
			Object[] args = { vObject.getCountry(), calLeBook, vObject.getProgram(), vObject.getExecutionSequence(), vObject.getExecutionTypeAt(), 
				vObject.getExecutionType(), vObject.getFatalFlag1(), vObject.getLogTextFlag1(), vObject.getDmlStatus1(), 
				vObject.getFatalFlag2(), vObject.getLogTextFlag2(), vObject.getDmlStatus2(), 
				vObject.getFatalFlag3(), vObject.getLogTextFlag3(), vObject.getDmlStatus3(), 
				vObject.getFatalFlag4(), vObject.getLogTextFlag4(), vObject.getDmlStatus4(), 
				vObject.getFatalFlag5(), vObject.getLogTextFlag5(), vObject.getDmlStatus5(), 
				vObject.getFatalFlag6(), vObject.getLogTextFlag6(), vObject.getDmlStatus6(), 
				vObject.getFatalFlag7(), vObject.getLogTextFlag7(), vObject.getDmlStatus7(), 
				vObject.getFatalFlag8(), vObject.getLogTextFlag8(), vObject.getDmlStatus8(), 
				vObject.getFatalFlag9(), vObject.getLogTextFlag9(), vObject.getDmlStatus9(), 
				vObject.getFatalFlag10(), vObject.getLogTextFlag10(), vObject.getDmlStatus10(), 
				vObject.getFatalFlagAt(), vObject.getLogTextFlagAt(), vObject.getDmlStatusNt(), vObject.getMappingStatusNt(),vObject.getMappingStatus(),
				vObject.getCommitFlag(),vObject.getRecordIndicatorNt(), vObject.getRecordIndicator(), vObject.getMaker(), 
				vObject.getVerifier(), vObject.getInternalStatus()};
		
		ps= getConnection().prepareStatement(query);
		for(int i=1;i<=args.length;i++){
			ps.setObject(i,args[i-1]);	
		}
		
		String mainQuery = vObject.getMainQuery();
		try{
			if(mainQuery.equalsIgnoreCase(null))
				mainQuery="";
		}catch(Exception e){
			mainQuery="";
		}
		Reader reader = new StringReader(mainQuery);
		ps.setCharacterStream(args.length+1, reader, mainQuery.length());
		
		String dml1 = vObject.getDml1();
		try{
			if(dml1.equalsIgnoreCase(null))
				dml1="";
		}catch(Exception e){
			dml1="";
		}
		reader = new StringReader(dml1);
		ps.setCharacterStream(args.length+2, reader, dml1.length());
			String dml2 = vObject.getDml2();
			try{
				if(dml2.equalsIgnoreCase(null))
					dml2="";
			}catch(Exception e){
				dml2="";
			}
			reader = new StringReader(dml2);
		ps.setCharacterStream(args.length+3, reader, dml2.length());
			String dml3 = vObject.getDml3();
			try{
				if(dml3.equalsIgnoreCase(null))
					dml3="";
			}catch(Exception e){
				dml3="";
			}
			reader = new StringReader(dml3);
		ps.setCharacterStream(args.length+4, reader, dml3.length());
		
			String dml4 = vObject.getDml4();
			try{
				if(dml4.equalsIgnoreCase(null))
					dml4="";
			}catch(Exception e){
				dml4="";
			}
			reader = new StringReader(dml4);
		ps.setCharacterStream(args.length+5, reader, dml4.length());
			String dml5 = vObject.getDml5();
			try{
				if(dml5.equalsIgnoreCase(null))
					dml5="";
			}catch(Exception e){
				dml5="";
			}
			reader = new StringReader(dml5);
		ps.setCharacterStream(args.length+6, reader, dml5.length());
			String dml6 = vObject.getDml6();
			try{
				if(dml6.equalsIgnoreCase(null))
					dml6="";
			}catch(Exception e){
				dml6="";
			}
			reader = new StringReader(dml6);
		ps.setCharacterStream(args.length+7, reader, dml6.length());
			String dml7 = vObject.getDml7();
			try{
				if(dml7.equalsIgnoreCase(null))
					dml7="";
			}catch(Exception e){
				dml7="";
			}
			reader = new StringReader(dml7);
		ps.setCharacterStream(args.length+8, reader, dml7.length());
			String dml8 = vObject.getDml8();
			try{
				if(dml8.equalsIgnoreCase(null))
					dml8="";
			}catch(Exception e){
				dml8="";
			}
			reader = new StringReader(dml8);
		ps.setCharacterStream(args.length+9, reader, dml8.length());
			String dml9 = vObject.getDml9();
			try{
				if(dml9.equalsIgnoreCase(null))
					dml9="";
			}catch(Exception e){
				dml9="";
			}
			reader = new StringReader(dml9);
		ps.setCharacterStream(args.length+10, reader, dml9.length());
			String dml10 = vObject.getDml10();
			try{
				if(dml10.equalsIgnoreCase(null))
					dml10="";
			}catch(Exception e){
				dml10="";
			}
			reader = new StringReader(dml10);
		ps.setCharacterStream(args.length+11, reader, dml10.length());
		
		result = ps.executeUpdate();
		}catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
			strErrorDesc = e.getMessage();
			logger.error("Update Error : "+e.getMessage());
		}finally{
			if (ps != null) {
				try {
					ps.close();
					ps=null;
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return result;
	}
	/*protected int doInsertionPend(TemplateNameGBMVb vObject){
		String query = "Insert Into TEMPLATE_NAMES_PEND(TEMPLATE_NAME, GENERAL_DESCRIPTION, FEED_CATEGORY, PROGRAM, DEFAULT_ACQ_PROCESS_TYPE_AT, "+
			"DEFAULT_ACQ_PROCESS_TYPE,TEMPLATE_STATUS_NT,TEMPLATE_STATUS,PROCESS_SEQUENCE,FILE_PATTERN,EXCEL_FILE_PATTERN,EXCEL_TEMPLATE_ID,EFFECTIVE_DATE,"+
			"RECORD_INDICATOR_NT,RECORD_INDICATOR, MAKER, VERIFIER, INTERNAL_STATUS, DATE_LAST_MODIFIED, DATE_CREATION, FEED_STG_NAME, VIEW_NAME) "+
				"Values (?, ?, ?, ?, ?,?,?,?, ?, ?, ?, ?,CONVERT(datetime, ?, 103),?, ?, ?,?,?,  GetDate(),GetDate(), ?, ?)";
			
		Object[] args = {vObject.getTemplateName(), vObject.getGeneralDescription(), vObject.getFeedCategory(), vObject.getProgram(),
			vObject.getDefaultAcquProcessTypeAT(),vObject.getDefaultAcquProcessType(),vObject.getTemplateStatusNt(),vObject.getTemplateStatus(),
			vObject.getProcessSequence(),vObject.getFilePattern(),vObject.getExcelFilePattern(),vObject.getExcelTemplateId(),vObject.getEffectiveDate(),
			vObject.getRecordIndicatorNt(),vObject.getRecordIndicator(), vObject.getMaker(), vObject.getVerifier(), vObject.getInternalStatus(),vObject.getFeedStgName(),vObject.getViewName()};
			return getJdbcTemplate().update(query,args);
	}*/
	@Override
	protected int doInsertionPendWithDc(TemplateNameGBMVb vObject){
		int verNo = getCommVersionNo(vObject);
		insertIntoComment(vObject, verNo);
		String calLeBook = removeDescLeBook(vObject.getLeBook());
		if(!ValidationUtil.isValid(vObject.getFatalFlag1())||!ValidationUtil.isValid(vObject.getLogTextFlag1())||!ValidationUtil.isValid(vObject.getDmlStatus1())){
			vObject.setFatalFlag1("Y");
			vObject.setLogTextFlag1("DEFAULT");
			vObject.setDmlStatus1("0");
		}
		if(!ValidationUtil.isValid(vObject.getFatalFlag2())||!ValidationUtil.isValid(vObject.getLogTextFlag2())||!ValidationUtil.isValid(vObject.getDmlStatus2())){
			vObject.setFatalFlag2("Y");
			vObject.setLogTextFlag2("DEFAULT");
			vObject.setDmlStatus2("0");
		}
		if(!ValidationUtil.isValid(vObject.getFatalFlag3())||!ValidationUtil.isValid(vObject.getLogTextFlag3())||!ValidationUtil.isValid(vObject.getDmlStatus3())){
			vObject.setFatalFlag3("Y");
			vObject.setLogTextFlag3("DEFAULT");
			vObject.setDmlStatus3("0");
		}
		if(!ValidationUtil.isValid(vObject.getFatalFlag4())||!ValidationUtil.isValid(vObject.getLogTextFlag4())||!ValidationUtil.isValid(vObject.getDmlStatus4())){
			vObject.setFatalFlag4("Y");
			vObject.setLogTextFlag4("DEFAULT");
			vObject.setDmlStatus4("0");
		}
		if(!ValidationUtil.isValid(vObject.getFatalFlag5())||!ValidationUtil.isValid(vObject.getLogTextFlag5())||!ValidationUtil.isValid(vObject.getDmlStatus5())){
			vObject.setFatalFlag5("Y");
			vObject.setLogTextFlag5("DEFAULT");
			vObject.setDmlStatus5("0");
		}
		if(!ValidationUtil.isValid(vObject.getFatalFlag6())||!ValidationUtil.isValid(vObject.getLogTextFlag6())||!ValidationUtil.isValid(vObject.getDmlStatus6())){
			vObject.setFatalFlag6("Y");
			vObject.setLogTextFlag6("DEFAULT");
			vObject.setDmlStatus6("0");
		}
		if(!ValidationUtil.isValid(vObject.getFatalFlag7())||!ValidationUtil.isValid(vObject.getLogTextFlag7())||!ValidationUtil.isValid(vObject.getDmlStatus7())){
			vObject.setFatalFlag7("Y");
			vObject.setLogTextFlag7("DEFAULT");
			vObject.setDmlStatus7("0");
		}
		if(!ValidationUtil.isValid(vObject.getFatalFlag8())||!ValidationUtil.isValid(vObject.getLogTextFlag8())||!ValidationUtil.isValid(vObject.getDmlStatus8())){
			vObject.setFatalFlag8("Y");
			vObject.setLogTextFlag8("DEFAULT");
			vObject.setDmlStatus8("0");
		}
		if(!ValidationUtil.isValid(vObject.getFatalFlag9())||!ValidationUtil.isValid(vObject.getLogTextFlag9())||!ValidationUtil.isValid(vObject.getDmlStatus9())){
			vObject.setFatalFlag9("Y");
			vObject.setLogTextFlag9("DEFAULT");
			vObject.setDmlStatus9("0");
		}
		if(!ValidationUtil.isValid(vObject.getFatalFlag10())||!ValidationUtil.isValid(vObject.getLogTextFlag10())||!ValidationUtil.isValid(vObject.getDmlStatus10())){
			vObject.setFatalFlag10("Y");
			vObject.setLogTextFlag10("DEFAULT");
			vObject.setDmlStatus10("0");
		}
		PreparedStatement ps= null;
		int result=0;
		try {
			String query = " Insert into RA_GENERICBUILD_MAPPINGS_PEND (COUNTRY, LE_BOOK, PROGRAM, EXECUTION_SEQUENCE, EXECUTION_TYPE_AT, EXECUTION_TYPE "+
				" , FATAL_FLAG_1, LOG_TEXT_FLAG_1, DML_STATUS_1, FATAL_FLAG_2, LOG_TEXT_FLAG_2, DML_STATUS_2 "+
				" , FATAL_FLAG_3, LOG_TEXT_FLAG_3, DML_STATUS_3, FATAL_FLAG_4, LOG_TEXT_FLAG_4, DML_STATUS_4 "+
				" , FATAL_FLAG_5, LOG_TEXT_FLAG_5, DML_STATUS_5, FATAL_FLAG_6, LOG_TEXT_FLAG_6, DML_STATUS_6 "+
				" , FATAL_FLAG_7, LOG_TEXT_FLAG_7, DML_STATUS_7, FATAL_FLAG_8, LOG_TEXT_FLAG_8, DML_STATUS_8 "+
				" , FATAL_FLAG_9, LOG_TEXT_FLAG_9, DML_STATUS_9, FATAL_FLAG_10, LOG_TEXT_FLAG_10, DML_STATUS_10 "+
				" , FATAL_FLAG_AT, LOG_TEXT_FLAG_AT, DML_STATUS_NT, MAPPING_STATUS_NT, MAPPING_STATUS, COMMIT_FLAG "+
				" , RECORD_INDICATOR_NT, RECORD_INDICATOR, MAKER, VERIFIER, INTERNAL_STATUS, DATE_LAST_MODIFIED "+
				" , DATE_CREATION, MAIN_QUERY, DML_1, DML_2, DML_3, DML_4, DML_5, DML_6, DML_7, DML_8, DML_9, DML_10  ) "+
				" Values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? "+
				" , ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?, "+getDbFunction("SYSDATE")+", "+getDbFunction("SYSDATE")+", ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		
		
			Object[] args = { vObject.getCountry(), calLeBook, vObject.getProgram(), vObject.getExecutionSequence(), vObject.getExecutionTypeAt(), 
				vObject.getExecutionType(), vObject.getFatalFlag1(), vObject.getLogTextFlag1(), vObject.getDmlStatus1(), 
				vObject.getFatalFlag2(), vObject.getLogTextFlag2(), vObject.getDmlStatus2(), 
				vObject.getFatalFlag3(), vObject.getLogTextFlag3(), vObject.getDmlStatus3(), 
				vObject.getFatalFlag4(), vObject.getLogTextFlag4(), vObject.getDmlStatus4(), 
				vObject.getFatalFlag5(), vObject.getLogTextFlag5(), vObject.getDmlStatus5(), 
				vObject.getFatalFlag6(), vObject.getLogTextFlag6(), vObject.getDmlStatus6(), 
				vObject.getFatalFlag7(), vObject.getLogTextFlag7(), vObject.getDmlStatus7(), 
				vObject.getFatalFlag8(), vObject.getLogTextFlag8(), vObject.getDmlStatus8(), 
				vObject.getFatalFlag9(), vObject.getLogTextFlag9(), vObject.getDmlStatus9(), 
				vObject.getFatalFlag10(), vObject.getLogTextFlag10(), vObject.getDmlStatus10(), 
				vObject.getFatalFlagAt(), vObject.getLogTextFlagAt(), vObject.getDmlStatusNt(), vObject.getMappingStatusNt(),vObject.getMappingStatus(),
				vObject.getCommitFlag(),vObject.getRecordIndicatorNt(), vObject.getRecordIndicator(), vObject.getMaker(), 
				vObject.getVerifier(), vObject.getInternalStatus()};
		
		ps= getConnection().prepareStatement(query);
		for(int i=1;i<=args.length;i++){
			ps.setObject(i,args[i-1]);	
		}
		
		String mainQuery = vObject.getMainQuery();
		try{
			if(mainQuery.equalsIgnoreCase(null))
				mainQuery="";
		}catch(Exception e){
			mainQuery="";
		}
		Reader reader = new StringReader(mainQuery);
		ps.setCharacterStream(args.length+1, reader, mainQuery.length());
		
		String dml1 = vObject.getDml1();
		try{
			if(dml1.equalsIgnoreCase(null))
				dml1="";
		}catch(Exception e){
			dml1="";
		}
		reader = new StringReader(dml1);
		ps.setCharacterStream(args.length+2, reader, dml1.length());
			String dml2 = vObject.getDml2();
			try{
				if(dml2.equalsIgnoreCase(null))
					dml2="";
			}catch(Exception e){
				dml2="";
			}
			reader = new StringReader(dml2);
		ps.setCharacterStream(args.length+3, reader, dml2.length());
			String dml3 = vObject.getDml3();
			try{
				if(dml3.equalsIgnoreCase(null))
					dml3="";
			}catch(Exception e){
				dml3="";
			}
			reader = new StringReader(dml3);
		ps.setCharacterStream(args.length+4, reader, dml3.length());
		
			String dml4 = vObject.getDml4();
			try{
				if(dml4.equalsIgnoreCase(null))
					dml4="";
			}catch(Exception e){
				dml4="";
			}
			reader = new StringReader(dml4);
		ps.setCharacterStream(args.length+5, reader, dml4.length());
			String dml5 = vObject.getDml5();
			try{
				if(dml5.equalsIgnoreCase(null))
					dml5="";
			}catch(Exception e){
				dml5="";
			}
			reader = new StringReader(dml5);
		ps.setCharacterStream(args.length+6, reader, dml5.length());
			String dml6 = vObject.getDml6();
			try{
				if(dml6.equalsIgnoreCase(null))
					dml6="";
			}catch(Exception e){
				dml6="";
			}
			reader = new StringReader(dml6);
		ps.setCharacterStream(args.length+7, reader, dml6.length());
			String dml7 = vObject.getDml7();
			try{
				if(dml7.equalsIgnoreCase(null))
					dml7="";
			}catch(Exception e){
				dml7="";
			}
			reader = new StringReader(dml7);
		ps.setCharacterStream(args.length+8, reader, dml7.length());
			String dml8 = vObject.getDml8();
			try{
				if(dml8.equalsIgnoreCase(null))
					dml8="";
			}catch(Exception e){
				dml8="";
			}
			reader = new StringReader(dml8);
		ps.setCharacterStream(args.length+9, reader, dml8.length());
			String dml9 = vObject.getDml9();
			try{
				if(dml9.equalsIgnoreCase(null))
					dml9="";
			}catch(Exception e){
				dml9="";
			}
			reader = new StringReader(dml9);
		ps.setCharacterStream(args.length+10, reader, dml9.length());
			String dml10 = vObject.getDml10();
			try{
				if(dml10.equalsIgnoreCase(null))
					dml10="";
			}catch(Exception e){
				dml10="";
			}
			reader = new StringReader(dml10);
		ps.setCharacterStream(args.length+11, reader, dml10.length());
		
		result = ps.executeUpdate();
		}catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
			strErrorDesc = e.getMessage();
			logger.error("Update Error : "+e.getMessage());
		}finally{
			if (ps != null) {
				try {
					ps.close();
					ps=null;
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return result;
	}
	/*protected int doInsertionPendWithDc(TemplateNameGBMVb vObject){
		String query = "Insert Into TEMPLATE_NAMES_PEND ( TEMPLATE_NAME, GENERAL_DESCRIPTION, FEED_CATEGORY, PROGRAM, DEFAULT_ACQ_PROCESS_TYPE_AT, "+
			"DEFAULT_ACQ_PROCESS_TYPE,TEMPLATE_STATUS_NT,TEMPLATE_STATUS,PROCESS_SEQUENCE,FILE_PATTERN,EXCEL_FILE_PATTERN,EXCEL_TEMPLATE_ID,EFFECTIVE_DATE,"+
			"RECORD_INDICATOR_NT,RECORD_INDICATOR, MAKER, VERIFIER, INTERNAL_STATUS, DATE_LAST_MODIFIED, DATE_CREATION, FEED_STG_NAME, VIEW_NAME) "+
			"Values (?, ?, ?, ?, ?, ?, ?,?, ?, ?, ?, ?,CONVERT(datetime, ?, 103),?, ?, ?,?,?,  GetDate(),To_Date(?, 'DD-MM-RRRR HH24:MI:SS'), ?, ?)";
		
		Object[] args = { vObject.getTemplateName(), vObject.getGeneralDescription(), vObject.getFeedCategory(), vObject.getProgram(),
			vObject.getDefaultAcquProcessTypeAT(),vObject.getDefaultAcquProcessType(),vObject.getTemplateStatusNt(),vObject.getTemplateStatus(),
			vObject.getProcessSequence(),vObject.getFilePattern(),vObject.getExcelFilePattern(),vObject.getExcelTemplateId(),vObject.getEffectiveDate(),
			vObject.getRecordIndicatorNt(),vObject.getRecordIndicator(), vObject.getMaker(), vObject.getVerifier(), vObject.getInternalStatus(),vObject.getDateCreation(),vObject.getFeedStgName(),vObject.getViewName()};
			return getJdbcTemplate().update(query,args);
	}*/
	@Override
	protected int doUpdateAppr(TemplateNameGBMVb vObject){
		System.out.println("doUpdateAppr");
		updateComment(vObject);
		System.out.println("Update Comments");
		PreparedStatement ps= null;
		String calLeBook = removeDescLeBook(vObject.getLeBook());
		
		String query = " UPDATE RA_GENERICBUILD_MAPPINGS set EXECUTION_TYPE_AT = ?, "+
				" EXECUTION_TYPE = ?, FATAL_FLAG_1 = ?, LOG_TEXT_FLAG_1 = ?, DML_STATUS_1 = ?, "+
				" FATAL_FLAG_2 = ?, LOG_TEXT_FLAG_2 = ?, DML_STATUS_2 = ?, FATAL_FLAG_3 = ?, LOG_TEXT_FLAG_3 = ?, DML_STATUS_3 = ?, "+
				" FATAL_FLAG_4 = ?, LOG_TEXT_FLAG_4 = ?, DML_STATUS_4 = ?, FATAL_FLAG_5 = ?, LOG_TEXT_FLAG_5 = ?, "+
				" DML_STATUS_5 = ?, FATAL_FLAG_6 = ?, LOG_TEXT_FLAG_6 = ?, DML_STATUS_6 = ?, FATAL_FLAG_7 = ?, "+
				" LOG_TEXT_FLAG_7 = ?, DML_STATUS_7 = ?, FATAL_FLAG_8 = ?, LOG_TEXT_FLAG_8 = ?, DML_STATUS_8 = ?, "+
				" FATAL_FLAG_9 = ?, LOG_TEXT_FLAG_9 = ?, DML_STATUS_9 = ?, FATAL_FLAG_10 = ?, LOG_TEXT_FLAG_10 = ?, "+
				" DML_STATUS_10 = ?, FATAL_FLAG_AT = ?, LOG_TEXT_FLAG_AT = ?, DML_STATUS_NT = ?, MAPPING_STATUS_NT = ?, MAPPING_STATUS = ?, COMMIT_FLAG = ?, "+
				" RECORD_INDICATOR_NT = ?, RECORD_INDICATOR = ?, MAKER = ?, VERIFIER = ?, INTERNAL_STATUS = ?, DATE_LAST_MODIFIED = "+getDbFunction("SYSDATE")+
				" WHERE COUNTRY = ? AND LE_BOOK = ? AND PROGRAM = ? AND EXECUTION_SEQUENCE = ?";
			Object[] args = { vObject.getExecutionTypeAt(), vObject.getExecutionType(), vObject.getFatalFlag1(), 
				vObject.getLogTextFlag1(), vObject.getDmlStatus1(), vObject.getFatalFlag2(), vObject.getLogTextFlag2(), vObject.getDmlStatus2(), 
				vObject.getFatalFlag3(), vObject.getLogTextFlag3(), vObject.getDmlStatus3(), 
				vObject.getFatalFlag4(), vObject.getLogTextFlag4(), vObject.getDmlStatus4(), vObject.getFatalFlag5(), 
				vObject.getLogTextFlag5(), vObject.getDmlStatus5(), vObject.getFatalFlag6(), vObject.getLogTextFlag6(), 
				vObject.getDmlStatus6(), vObject.getFatalFlag7(), vObject.getLogTextFlag7(), vObject.getDmlStatus7(), 
				vObject.getFatalFlag8(), vObject.getLogTextFlag8(), vObject.getDmlStatus8(), 
				vObject.getFatalFlag9(), vObject.getLogTextFlag9(), vObject.getDmlStatus9(), vObject.getFatalFlag10(), 
				vObject.getLogTextFlag10(), vObject.getDmlStatus10(), vObject.getFatalFlagAt(), vObject.getLogTextFlagAt(), 
				vObject.getDmlStatusNt(), vObject.getMappingStatusNt(), vObject.getMappingStatus(), vObject.getCommitFlag(), vObject.getRecordIndicatorNt(), 
				vObject.getRecordIndicator(), vObject.getMaker(), vObject.getVerifier(), vObject.getInternalStatus(), 
				vObject.getCountry(), calLeBook, vObject.getProgram(), vObject.getExecutionSequence() };
			
			int result = getJdbcTemplate().update(query,args);
			try {
				 if(result == Constants.SUCCESSFUL_OPERATION){
					 String query1 = " UPDATE RA_GENERICBUILD_MAPPINGS "+
								" SET MAIN_QUERY = ? ,DML_1 = ?, DML_2 = ?, DML_3 = ?, DML_4 = ?, "+
								" DML_5 = ?, DML_6 = ?, DML_7 = ?, DML_8 = ?, "+
								" DML_9 = ?, DML_10 = ? WHERE COUNTRY = ? AND LE_BOOK = ? AND PROGRAM = ? AND EXECUTION_SEQUENCE = ? ";
					 result=	 getJdbcTemplate().update(new PreparedStatementCreator() {
							@Override
							public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
								PreparedStatement ps = connection.prepareStatement(query1);
								int psIndex = 0;
								String mainQuery = ValidationUtil.isValid(vObject.getMainQuery())?vObject.getMainQuery():"";
								ps.setCharacterStream(++psIndex, new StringReader(mainQuery), mainQuery.length());
								
								mainQuery = ValidationUtil.isValid(vObject.getDml1())?vObject.getDml1():"";
								ps.setCharacterStream(++psIndex, new StringReader(mainQuery), mainQuery.length());
								
								mainQuery = ValidationUtil.isValid(vObject.getDml2())?vObject.getDml2():"";
								ps.setCharacterStream(++psIndex, new StringReader(mainQuery), mainQuery.length());
								
								mainQuery = ValidationUtil.isValid(vObject.getDml3())?vObject.getDml3():"";
								ps.setCharacterStream(++psIndex, new StringReader(mainQuery), mainQuery.length());
								
								mainQuery = ValidationUtil.isValid(vObject.getDml4())?vObject.getDml4():"";
								ps.setCharacterStream(++psIndex, new StringReader(mainQuery), mainQuery.length());
								
								mainQuery = ValidationUtil.isValid(vObject.getDml5())?vObject.getDml5():"";
								ps.setCharacterStream(++psIndex, new StringReader(mainQuery), mainQuery.length());
								
								mainQuery = ValidationUtil.isValid(vObject.getDml6())?vObject.getDml6():"";
								ps.setCharacterStream(++psIndex, new StringReader(mainQuery), mainQuery.length());
								
								mainQuery = ValidationUtil.isValid(vObject.getDml7())?vObject.getDml7():"";
								ps.setCharacterStream(++psIndex, new StringReader(mainQuery), mainQuery.length());
								
								mainQuery = ValidationUtil.isValid(vObject.getDml8())?vObject.getDml8():"";
								ps.setCharacterStream(++psIndex, new StringReader(mainQuery), mainQuery.length());
								
								mainQuery = ValidationUtil.isValid(vObject.getDml9())?vObject.getDml9():"";
								ps.setCharacterStream(++psIndex, new StringReader(mainQuery), mainQuery.length());

								mainQuery = ValidationUtil.isValid(vObject.getDml10())?vObject.getDml10():"";
								ps.setCharacterStream(++psIndex, new StringReader(mainQuery), mainQuery.length());
								
								ps.setObject(12, vObject.getCountry());
								ps.setObject(13, calLeBook);
								ps.setObject(14, vObject.getProgram());
								ps.setObject(15, vObject.getExecutionSequence());
								return ps;
							}
						});
				 }
			}catch (Exception e) {
				System.out.println(e.getMessage());
				strErrorDesc = e.getMessage();
				logger.error("Update Error : "+e.getMessage());
			 }finally{
					if (ps != null) {
						try {
							ps.close();
							ps=null;
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
		return result;
	}
	@Override
	protected int doUpdatePend(TemplateNameGBMVb vObject){
		try{
			System.out.println("Update Comments");
		updateComment(vObject);
		}catch(Exception e){
			e.printStackTrace();
		}
		int result = 0;
		String calLeBook = removeDescLeBook(vObject.getLeBook());
			try {
				System.out.println("Modify Start::::::");
				String query = " UPDATE RA_GENERICBUILD_MAPPINGS_PEND set EXECUTION_TYPE_AT = ?, "+
						" EXECUTION_TYPE = ?, FATAL_FLAG_1 = ?, LOG_TEXT_FLAG_1 = ?, DML_STATUS_1 = ?, "+
						" FATAL_FLAG_2 = ?, LOG_TEXT_FLAG_2 = ?, DML_STATUS_2 = ?, FATAL_FLAG_3 = ?, LOG_TEXT_FLAG_3 = ?, DML_STATUS_3 = ?, "+
						" FATAL_FLAG_4 = ?, LOG_TEXT_FLAG_4 = ?, DML_STATUS_4 = ?, FATAL_FLAG_5 = ?, LOG_TEXT_FLAG_5 = ?, "+
						" DML_STATUS_5 = ?, FATAL_FLAG_6 = ?, LOG_TEXT_FLAG_6 = ?, DML_STATUS_6 = ?, FATAL_FLAG_7 = ?, "+
						" LOG_TEXT_FLAG_7 = ?, DML_STATUS_7 = ?, FATAL_FLAG_8 = ?, LOG_TEXT_FLAG_8 = ?, DML_STATUS_8 = ?, "+
						" FATAL_FLAG_9 = ?, LOG_TEXT_FLAG_9 = ?, DML_STATUS_9 = ?, FATAL_FLAG_10 = ?, LOG_TEXT_FLAG_10 = ?, "+
						" DML_STATUS_10 = ?, FATAL_FLAG_AT = ?, LOG_TEXT_FLAG_AT = ?, DML_STATUS_NT = ?, MAPPING_STATUS_NT = ?, MAPPING_STATUS = ?, COMMIT_FLAG = ?, "+
						" RECORD_INDICATOR_NT = ?, RECORD_INDICATOR = ?, MAKER = ?, VERIFIER = ?, INTERNAL_STATUS = ?, DATE_LAST_MODIFIED ="+getDbFunction("SYSDATE")+
						" WHERE COUNTRY = ? AND LE_BOOK = ? AND PROGRAM = ? AND EXECUTION_SEQUENCE = ?";
					Object[] args = { vObject.getExecutionTypeAt(), vObject.getExecutionType(), vObject.getFatalFlag1(), 
						vObject.getLogTextFlag1(), vObject.getDmlStatus1(), vObject.getFatalFlag2(), vObject.getLogTextFlag2(), vObject.getDmlStatus2(), 
						vObject.getFatalFlag3(), vObject.getLogTextFlag3(), vObject.getDmlStatus3(), 
						vObject.getFatalFlag4(), vObject.getLogTextFlag4(), vObject.getDmlStatus4(), vObject.getFatalFlag5(), 
						vObject.getLogTextFlag5(), vObject.getDmlStatus5(), vObject.getFatalFlag6(), vObject.getLogTextFlag6(), 
						vObject.getDmlStatus6(), vObject.getFatalFlag7(), vObject.getLogTextFlag7(), vObject.getDmlStatus7(), 
						vObject.getFatalFlag8(), vObject.getLogTextFlag8(), vObject.getDmlStatus8(), 
						vObject.getFatalFlag9(), vObject.getLogTextFlag9(), vObject.getDmlStatus9(), vObject.getFatalFlag10(), 
						vObject.getLogTextFlag10(), vObject.getDmlStatus10(), vObject.getFatalFlagAt(), vObject.getLogTextFlagAt(), 
						vObject.getDmlStatusNt(), vObject.getMappingStatusNt(), vObject.getMappingStatus(), vObject.getCommitFlag(), vObject.getRecordIndicatorNt(), 
						vObject.getRecordIndicator(), vObject.getMaker(), vObject.getVerifier(), vObject.getInternalStatus(), 
						vObject.getCountry(), calLeBook, vObject.getProgram(), vObject.getExecutionSequence() };
					
					result = getJdbcTemplate().update(query,args);
				if(result == Constants.SUCCESSFUL_OPERATION){
					String query1 = " UPDATE RA_GENERICBUILD_MAPPINGS_PEND "+
								" SET MAIN_QUERY = ? ,DML_1 = ?, DML_2 = ?, DML_3 = ?, DML_4 = ?, "+
								" DML_5 = ?, DML_6 = ?, DML_7 = ?, DML_8 = ?, "+
								" DML_9 = ?, DML_10 = ? WHERE COUNTRY = ? AND LE_BOOK = ? AND PROGRAM = ? AND EXECUTION_SEQUENCE = ? ";
					 result=	 getJdbcTemplate().update(new PreparedStatementCreator() {
							@Override
							public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
								PreparedStatement ps = connection.prepareStatement(query1);
								int psIndex = 0;
								String mainQuery = ValidationUtil.isValid(vObject.getMainQuery())?vObject.getMainQuery():"";
								ps.setCharacterStream(++psIndex, new StringReader(mainQuery), mainQuery.length());
								
								mainQuery = ValidationUtil.isValid(vObject.getDml1())?vObject.getDml1():"";
								ps.setCharacterStream(++psIndex, new StringReader(mainQuery), mainQuery.length());
								
								mainQuery = ValidationUtil.isValid(vObject.getDml2())?vObject.getDml2():"";
								ps.setCharacterStream(++psIndex, new StringReader(mainQuery), mainQuery.length());
								
								mainQuery = ValidationUtil.isValid(vObject.getDml3())?vObject.getDml3():"";
								ps.setCharacterStream(++psIndex, new StringReader(mainQuery), mainQuery.length());
								
								mainQuery = ValidationUtil.isValid(vObject.getDml4())?vObject.getDml4():"";
								ps.setCharacterStream(++psIndex, new StringReader(mainQuery), mainQuery.length());
								
								mainQuery = ValidationUtil.isValid(vObject.getDml5())?vObject.getDml5():"";
								ps.setCharacterStream(++psIndex, new StringReader(mainQuery), mainQuery.length());
								
								mainQuery = ValidationUtil.isValid(vObject.getDml6())?vObject.getDml6():"";
								ps.setCharacterStream(++psIndex, new StringReader(mainQuery), mainQuery.length());
								
								mainQuery = ValidationUtil.isValid(vObject.getDml7())?vObject.getDml7():"";
								ps.setCharacterStream(++psIndex, new StringReader(mainQuery), mainQuery.length());
								
								mainQuery = ValidationUtil.isValid(vObject.getDml8())?vObject.getDml8():"";
								ps.setCharacterStream(++psIndex, new StringReader(mainQuery), mainQuery.length());
								
								mainQuery = ValidationUtil.isValid(vObject.getDml9())?vObject.getDml9():"";
								ps.setCharacterStream(++psIndex, new StringReader(mainQuery), mainQuery.length());

								mainQuery = ValidationUtil.isValid(vObject.getDml10())?vObject.getDml10():"";
								ps.setCharacterStream(++psIndex, new StringReader(mainQuery), mainQuery.length());
								
								ps.setObject(12, vObject.getCountry());
								ps.setObject(13, calLeBook);
								ps.setObject(14, vObject.getProgram());
								ps.setObject(15, vObject.getExecutionSequence());
								return ps;
							}
						});
					
				 }
			}catch (Exception e) {
				System.out.println(e.getMessage());
				strErrorDesc = e.getMessage();
				logger.error("Update Error : "+e.getMessage());
			 }
			System.out.println("Modify END::::::"+result);
			return result;
	}
/*	protected int doUpdatePend(TemplateNameGBMVb vObject){
		if(!ValidationUtil.isValid(vObject.getDataScaling())){
			vObject.setDataScaling(null);
		}
		if(!ValidationUtil.isValid(vObject.getDataLength())){
			vObject.setDataLength(null);
		}
		String query = "Update TEMPLATE_NAMES_PEND Set  GENERAL_DESCRIPTION = ?, FEED_CATEGORY = ?, "+
				"PROGRAM = ?,"+
				"DEFAULT_ACQ_PROCESS_TYPE_AT = ?, DEFAULT_ACQ_PROCESS_TYPE = ?,   TEMPLATE_STATUS_NT=?,TEMPLATE_STATUS=?, "+
				"PROCESS_SEQUENCE = ?,FILE_PATTERN = ?,EXCEL_FILE_PATTERN = ?,EXCEL_TEMPLATE_ID = ? , EFFECTIVE_DATE = CONVERT(datetime, ?, 103), "+
			"INTERNAL_STATUS = ?, RECORD_INDICATOR_NT = ?, RECORD_INDICATOR = ?, MAKER = ?, VERIFIER = ?, DATE_LAST_MODIFIED = GetDate(), FEED_STG_NAME = ?, VIEW_NAME = ? Where TEMPLATE_NAME = ?";
		Object[] args = {vObject.getGeneralDescription(), vObject.getFeedCategory(), vObject.getProgram(),
				 vObject.getDefaultAcquProcessTypeAT(),vObject.getDefaultAcquProcessType(), vObject.getTemplateStatusNt(),vObject.getTemplateStatus(),
				 vObject.getProcessSequence(),vObject.getFilePattern(),vObject.getExcelFilePattern(),vObject.getExcelTemplateId(),vObject.getEffectiveDate(),
				 vObject.getInternalStatus(), vObject.getRecordIndicatorNt(),vObject.getRecordIndicator(),vObject.getMaker(),
			vObject.getVerifier(), vObject.getFeedStgName(), vObject.getViewName(), vObject.getTemplateName()};
		return getJdbcTemplate().update(query,args);
	}*/
	
	@Override
	protected int doDeleteAppr(TemplateNameGBMVb vObject){
		String calLeBook = removeDescLeBook(vObject.getLeBook());
		String query = "Delete From RA_GENERICBUILD_MAPPINGS Where COUNTRY = ? AND LE_BOOK = ? AND PROGRAM = ? AND EXECUTION_SEQUENCE = ?";
		Object[] args = {vObject.getCountry(), calLeBook, vObject.getProgram(), vObject.getExecutionSequence()};
		return getJdbcTemplate().update(query,args);
	}
	@Override
	protected int deletePendingRecord(TemplateNameGBMVb vObject){
		String calLeBook = removeDescLeBook(vObject.getLeBook());
		String query = "Delete From RA_GENERICBUILD_MAPPINGS_PEND Where COUNTRY = ? AND LE_BOOK = ? AND PROGRAM = ? AND EXECUTION_SEQUENCE = ?";
		Object[] args = {vObject.getCountry(), calLeBook, vObject.getProgram(), vObject.getExecutionSequence()};
		return getJdbcTemplate().update(query,args);
	}
	@Override
	protected String frameErrorMessage(TemplateNameGBMVb vObject, String strOperation){
		// specify all the key fields and their values first
		String strErrMsg = new String("");
		try{
			strErrMsg =  strErrMsg + "TEMPLATE_NAME:" + vObject.getTemplateName();
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
	protected String getAuditString(TemplateNameGBMVb vObject){
		final String auditDelimiter = vObject.getAuditDelimiter();
		final String auditDelimiterColVal = vObject.getAuditDelimiterColVal();
		String calLeBook = removeDescLeBook(vObject.getLeBook());
		StringBuffer strAudit = new StringBuffer("");
		try{
			if(ValidationUtil.isValid(vObject.getCountry()))
				strAudit.append("COUNTRY"+auditDelimiterColVal+vObject.getCountry().trim());
			else
				strAudit.append("COUNTRY"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			
			if(ValidationUtil.isValid(vObject.getLeBook()))
				strAudit.append("LE_BOOK"+auditDelimiterColVal+calLeBook.trim());
			else
				strAudit.append("LE_BOOK"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			
			if(ValidationUtil.isValid(vObject.getProgram()))
				strAudit.append("PROGRAM"+auditDelimiterColVal+vObject.getProgram().trim());
			else
				strAudit.append("PROGRAM"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			
			if(ValidationUtil.isValid(vObject.getExecutionSequence()))
				strAudit.append("EXECUTION_SEQUENCE"+auditDelimiterColVal+vObject.getExecutionSequence().trim());
			else
				strAudit.append("EXECUTION_SEQUENCE"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			
			strAudit.append("EXECUTION_TYPE_AT"+auditDelimiterColVal+vObject.getExecutionTypeAt());
			strAudit.append(auditDelimiter);
			
			strAudit.append("EXECUTION_TYPE"+auditDelimiterColVal+vObject.getExecutionTypeAt());
			strAudit.append(auditDelimiter);
			
			if(ValidationUtil.isValid(vObject.getMainQuery()))
				strAudit.append("MAIN_QUERY"+auditDelimiterColVal+vObject.getMainQuery().trim());
			else
				strAudit.append("MAIN_QUERY"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			
			if(ValidationUtil.isValid(vObject.getDml1()))
				strAudit.append("DML_1"+auditDelimiterColVal+vObject.getDml1().trim());
			else
				strAudit.append("DML_1"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			
			if(ValidationUtil.isValid(vObject.getFatalFlag1()))
				strAudit.append("FATAL_FLAG_1"+auditDelimiterColVal+vObject.getFatalFlag1().trim());
			else
				strAudit.append("FATAL_FLAG_1"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			
			if(ValidationUtil.isValid(vObject.getLogTextFlag1()))
				strAudit.append("LOG_TEXT_FLAG_1"+auditDelimiterColVal+vObject.getLogTextFlag1().trim());
			else
				strAudit.append("LOG_TEXT_FLAG_1"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			
			if(ValidationUtil.isValid(vObject.getDmlStatus1()))
				strAudit.append("DML_STATUS_1"+auditDelimiterColVal+vObject.getDmlStatus1().trim());
			else
				strAudit.append("DML_STATUS_1"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			
			if(ValidationUtil.isValid(vObject.getDml2()))
				strAudit.append("DML_2"+auditDelimiterColVal+vObject.getDml2().trim());
			else
				strAudit.append("DML_2"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			
			if(ValidationUtil.isValid(vObject.getFatalFlag2()))
				strAudit.append("FATAL_FLAG_2"+auditDelimiterColVal+vObject.getFatalFlag2().trim());
			else
				strAudit.append("FATAL_FLAG_2"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			
			if(ValidationUtil.isValid(vObject.getLogTextFlag2()))
				strAudit.append("LOG_TEXT_FLAG_2"+auditDelimiterColVal+vObject.getLogTextFlag2().trim());
			else
				strAudit.append("LOG_TEXT_FLAG_2"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			
			if(ValidationUtil.isValid(vObject.getDmlStatus2()))
				strAudit.append("DML_STATUS_2"+auditDelimiterColVal+vObject.getDmlStatus2().trim());
			else
				strAudit.append("DML_STATUS_2"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			
			if(ValidationUtil.isValid(vObject.getDml3()))
				strAudit.append("DML_3"+auditDelimiterColVal+vObject.getDml3().trim());
			else
				strAudit.append("DML_3"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			
			if(ValidationUtil.isValid(vObject.getFatalFlag3()))
				strAudit.append("FATAL_FLAG_3"+auditDelimiterColVal+vObject.getFatalFlag3().trim());
			else
				strAudit.append("FATAL_FLAG_3"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			
			if(ValidationUtil.isValid(vObject.getLogTextFlag3()))
				strAudit.append("LOG_TEXT_FLAG_3"+auditDelimiterColVal+vObject.getLogTextFlag3().trim());
			else
				strAudit.append("LOG_TEXT_FLAG_3"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			
			if(ValidationUtil.isValid(vObject.getDmlStatus3()))
				strAudit.append("DML_STATUS_3"+auditDelimiterColVal+vObject.getDmlStatus3().trim());
			else
				strAudit.append("DML_STATUS_3"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter); 
			
			if(ValidationUtil.isValid(vObject.getDml4()))
				strAudit.append("DML_4"+auditDelimiterColVal+vObject.getDml4().trim());
			else
				strAudit.append("DML_4"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			
			if(ValidationUtil.isValid(vObject.getFatalFlag4()))
				strAudit.append("FATAL_FLAG_4"+auditDelimiterColVal+vObject.getFatalFlag4().trim());
			else
				strAudit.append("FATAL_FLAG_4"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			
			if(ValidationUtil.isValid(vObject.getLogTextFlag4()))
				strAudit.append("LOG_TEXT_FLAG_4"+auditDelimiterColVal+vObject.getLogTextFlag4().trim());
			else
				strAudit.append("LOG_TEXT_FLAG_4"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			
			if(ValidationUtil.isValid(vObject.getDmlStatus4()))
				strAudit.append("DML_STATUS_4"+auditDelimiterColVal+vObject.getDmlStatus4().trim());
			else
				strAudit.append("DML_STATUS_4"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			
			if(ValidationUtil.isValid(vObject.getDml5()))
				strAudit.append("DML_5"+auditDelimiterColVal+vObject.getDml5().trim());
			else
				strAudit.append("DML_5"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			
			if(ValidationUtil.isValid(vObject.getFatalFlag5()))
				strAudit.append("FATAL_FLAG_5"+auditDelimiterColVal+vObject.getFatalFlag5().trim());
			else
				strAudit.append("FATAL_FLAG_5"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			
			if(ValidationUtil.isValid(vObject.getLogTextFlag5()))
				strAudit.append("LOG_TEXT_FLAG_5"+auditDelimiterColVal+vObject.getLogTextFlag5().trim());
			else
				strAudit.append("LOG_TEXT_FLAG_5"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			
			if(ValidationUtil.isValid(vObject.getDmlStatus5()))
				strAudit.append("DML_STATUS_5"+auditDelimiterColVal+vObject.getDmlStatus5().trim());
			else
				strAudit.append("DML_STATUS_5"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			
			if(ValidationUtil.isValid(vObject.getDml6()))
				strAudit.append("DML_6"+auditDelimiterColVal+vObject.getDml6().trim());
			else
				strAudit.append("DML_6"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			
			if(ValidationUtil.isValid(vObject.getFatalFlag6()))
				strAudit.append("FATAL_FLAG_6"+auditDelimiterColVal+vObject.getFatalFlag6().trim());
			else
				strAudit.append("FATAL_FLAG_6"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			
			if(ValidationUtil.isValid(vObject.getLogTextFlag6()))
				strAudit.append("LOG_TEXT_FLAG_6"+auditDelimiterColVal+vObject.getLogTextFlag6().trim());
			else
				strAudit.append("LOG_TEXT_FLAG_6"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			
			if(ValidationUtil.isValid(vObject.getDmlStatus6()))
				strAudit.append("DML_STATUS_6"+auditDelimiterColVal+vObject.getDmlStatus6().trim());
			else
				strAudit.append("DML_STATUS_6"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			
			if(ValidationUtil.isValid(vObject.getDml7()))
				strAudit.append("DML_7"+auditDelimiterColVal+vObject.getDml7().trim());
			else
				strAudit.append("DML_7"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			
			if(ValidationUtil.isValid(vObject.getFatalFlag7()))
				strAudit.append("FATAL_FLAG_7"+auditDelimiterColVal+vObject.getFatalFlag7().trim());
			else
				strAudit.append("FATAL_FLAG_7"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			
			if(ValidationUtil.isValid(vObject.getLogTextFlag7()))
				strAudit.append("LOG_TEXT_FLAG_7"+auditDelimiterColVal+vObject.getLogTextFlag7().trim());
			else
				strAudit.append("LOG_TEXT_FLAG_7"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			
			if(ValidationUtil.isValid(vObject.getDmlStatus7()))
				strAudit.append("DML_STATUS_7"+auditDelimiterColVal+vObject.getDmlStatus7().trim());
			else
				strAudit.append("DML_STATUS_7"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			
			if(ValidationUtil.isValid(vObject.getDml8()))
				strAudit.append("DML_8"+auditDelimiterColVal+vObject.getDml8().trim());
			else
				strAudit.append("DML_8"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			
			if(ValidationUtil.isValid(vObject.getFatalFlag8()))
				strAudit.append("FATAL_FLAG_8"+auditDelimiterColVal+vObject.getFatalFlag8().trim());
			else
				strAudit.append("FATAL_FLAG_8"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			
			if(ValidationUtil.isValid(vObject.getLogTextFlag8()))
				strAudit.append("LOG_TEXT_FLAG_8"+auditDelimiterColVal+vObject.getLogTextFlag8().trim());
			else
				strAudit.append("LOG_TEXT_FLAG_8"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			
			if(ValidationUtil.isValid(vObject.getDmlStatus8()))
				strAudit.append("DML_STATUS_8"+auditDelimiterColVal+vObject.getDmlStatus8().trim());
			else
				strAudit.append("DML_STATUS_8"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			
			if(ValidationUtil.isValid(vObject.getDml9()))
				strAudit.append("DML_9"+auditDelimiterColVal+vObject.getDml9().trim());
			else
				strAudit.append("DML_9"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			
			if(ValidationUtil.isValid(vObject.getFatalFlag9()))
				strAudit.append("FATAL_FLAG_9"+auditDelimiterColVal+vObject.getFatalFlag9().trim());
			else
				strAudit.append("FATAL_FLAG_9"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			
			if(ValidationUtil.isValid(vObject.getLogTextFlag9()))
				strAudit.append("LOG_TEXT_FLAG_9"+auditDelimiterColVal+vObject.getLogTextFlag9().trim());
			else
				strAudit.append("LOG_TEXT_FLAG_9"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			
			if(ValidationUtil.isValid(vObject.getDmlStatus9()))
				strAudit.append("DML_STATUS_9"+auditDelimiterColVal+vObject.getDmlStatus9().trim());
			else
				strAudit.append("DML_STATUS_9"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			
			if(ValidationUtil.isValid(vObject.getDml10()))
				strAudit.append("DML_10"+auditDelimiterColVal+vObject.getDml10().trim());
			else
				strAudit.append("DML_10"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			
			if(ValidationUtil.isValid(vObject.getFatalFlag10()))
				strAudit.append("FATAL_FLAG_10"+auditDelimiterColVal+vObject.getFatalFlag10().trim());
			else
				strAudit.append("FATAL_FLAG_10"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			
			if(ValidationUtil.isValid(vObject.getLogTextFlag10()))
				strAudit.append("LOG_TEXT_FLAG_10"+auditDelimiterColVal+vObject.getLogTextFlag10().trim());
			else
				strAudit.append("LOG_TEXT_FLAG_10"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			
			if(ValidationUtil.isValid(vObject.getDmlStatus10()))
				strAudit.append("DML_STATUS_10"+auditDelimiterColVal+vObject.getDmlStatus10().trim());
			else
				strAudit.append("DML_STATUS_10"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			
			strAudit.append("FATAL_FLAG_AT"+auditDelimiterColVal+vObject.getFatalFlagAt());
			strAudit.append(auditDelimiter);
			
			strAudit.append("LOG_TEXT_FLAG_AT"+auditDelimiterColVal+vObject.getLogTextFlagAt());
			strAudit.append(auditDelimiter);
			
			strAudit.append("DML_STATUS_NT"+auditDelimiterColVal+vObject.getDmlStatusNt());
			strAudit.append(auditDelimiter);
			
			strAudit.append("MAPPING_STATUS_NT"+auditDelimiterColVal+vObject.getMappingStatusNt());
			strAudit.append(auditDelimiter);
			
			strAudit.append("MAPPING_STATUS"+auditDelimiterColVal+vObject.getMappingStatus());
			strAudit.append(auditDelimiter);
			
			strAudit.append("COMMIT_FLAG"+auditDelimiterColVal+vObject.getCommitFlag());
			strAudit.append(auditDelimiter);
			
			strAudit.append("RECORD_INDICATOR_NT"+auditDelimiterColVal+vObject.getRecordIndicatorNt());
			strAudit.append(auditDelimiter);
			
			if(vObject.getRecordIndicator() == -1){vObject.setRecordIndicator(0);}
			strAudit.append("RECORD_INDICATOR"+auditDelimiterColVal+vObject.getRecordIndicator());
			strAudit.append(auditDelimiter);
			
			strAudit.append("MAKER"+auditDelimiterColVal+vObject.getMaker());
			strAudit.append(auditDelimiter);
			
			strAudit.append("VERIFIER"+auditDelimiterColVal+vObject.getVerifier());
			strAudit.append(auditDelimiter);
			
			strAudit.append("INTERNAL_STATUS"+auditDelimiterColVal+vObject.getInternalStatus());
			strAudit.append(auditDelimiter);
			
			if(vObject.getDateLastModified() != null)
				strAudit.append("DATE_LAST_MODIFIED"+auditDelimiterColVal+vObject.getDateLastModified().trim());
			else
				strAudit.append("DATE_LAST_MODIFIED"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);

			if(vObject.getDateCreation() != null)
				strAudit.append("DATE_CREATION"+auditDelimiterColVal+vObject.getDateCreation().trim());
			else
				strAudit.append("DATE_CREATION"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
		
		}catch(Exception ex){
			strErrorDesc = ex.getMessage();
			strAudit = strAudit.append(strErrorDesc);
			ex.printStackTrace();
		}
		return strAudit.toString();
	}
	@Override
	protected void setServiceDefaults(){
		serviceName = "GenricMappingDesign";
		serviceDesc = "Genric Mapping Design";
		tableName = "RA_GENERICBUILD_MAPPINGS";
		childTableName = "RA_GENERICBUILD_MAPPINGS";
		intCurrentUserId =  CustomContextHolder.getContext().getVisionId();
	}
	@Override
	protected String getBuildStatus(TemplateNameGBMVb templateNameVb){
		return getBuildModuleStatus(templateNameVb.getTemplateName(), templateNameVb.getTemplateName());
	}
	public List<TemplateNameGBMVb> getTemplatePopUp(TemplateNameGBMVb dObj, int intStatus){

		List<TemplateNameGBMVb> collTemp = null;
		final int intKeyFieldsCount = 1;
		setServiceDefaults();
		
		String strQueryAppr = new String("SELECT TAPPR.TEMPLATE_NAME ,TAPPR.COLUMN_NAME ,TAPPR.COLUMN_ID ,TAPPR.DATA_TYPE ,TAPPR.CHAR_USED ,TAPPR.DATA_LENGTH ,TAPPR.DATA_SCALING ," +
				"TAPPR.DATA_INDEX ,TAPPR.GUIDELINES ,TAPPR.MANDATORY_FLAG ,TAPPR.DEFAULT_VALUES ,TAPPR.COLUMN_DESCRIPTION ," +
				"TAPPR.DATA_SAMPLE ," +
				"To_Char(TAppr.EFFECTIVE_DATE, 'DD-MM-YYYY') EFFECTIVE_DATE " +
				" FROM TEMPLATE_DESIGN TAPPR Where TAppr.TEMPLATE_NAME = ? ORDER BY TAPPR.COLUMN_ID");
	
		String strQueryPend = "SELECT TAPPR.TEMPLATE_NAME ,TAPPR.COLUMN_NAME ,TAPPR.COLUMN_ID ,TAPPR.DATA_TYPE ,TAPPR.CHAR_USED ,TAPPR.DATA_LENGTH ,TAPPR.DATA_SCALING ," +
				"TAPPR.DATA_INDEX ,TAPPR.GUIDELINES ,TAPPR.MANDATORY_FLAG ,TAPPR.DEFAULT_VALUES ,TAPPR.COLUMN_DESCRIPTION ," +
				"TAPPR.DATA_SAMPLE ," +
				"To_Char(TAppr.EFFECTIVE_DATE, 'DD-MM-YYYY') EFFECTIVE_DATE " +
				" FROM TEMPLATE_DESIGN TAPPR Where TAppr.TEMPLATE_NAME = ? ORDER BY TAPPR.COLUMN_ID";
	
			Object objParams[] = new Object[intKeyFieldsCount];
			objParams[0] = new String(dObj.getTemplateName());	//[TEMPLATE_NAME]

		try
		{if(!dObj.isVerificationRequired()){intStatus =0;}
			if(intStatus == 0)
			{
				logger.info("Executing approved query");
				collTemp = getJdbcTemplate().query(strQueryAppr.toString(),objParams,getQueryMapperTemplateScreenPopUp());
			}else{
				logger.info("Executing pending query");
				collTemp = getJdbcTemplate().query(strQueryPend.toString(),objParams,getQueryMapperTemplateScreenPopUp());
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
	public String getMinExecutionSequence(TemplateNameGBMVb vObject) throws DataAccessException {
		String calLeBook = removeDescLeBook(vObject.getLeBook());
		if(!ValidationUtil.isValid(vObject.getProgram())){
			return null;
		}
		String sql = "select MIN(EXECUTION_SEQUENCE) EXECUTION_SEQUENCE FROM RA_GENERICBUILD_MAPPINGS where UPPER(COUNTRY) = ? AND UPPER(LE_BOOK) = ? AND UPPER(PROGRAM) = UPPER(?)";
		Object[] lParams = new Object[3];
		lParams[0] = new String(vObject.getCountry());
		lParams[1] = new String(calLeBook);
		lParams[2] = new String(vObject.getProgram());
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				TemplateNameGBMVb templateNameVb = new TemplateNameGBMVb();
				templateNameVb.setExecutionSequence(rs.getString("EXECUTION_SEQUENCE"));
				return templateNameVb;
			}
		};
		List<TemplateNameGBMVb> templateNameVb = getJdbcTemplate().query(sql, lParams, mapper);
		if(templateNameVb != null && !templateNameVb.isEmpty()){
			return templateNameVb.get(0).getExecutionSequence();
		}
		return null;
	}
	public List<TemplateNameGBMVb> getTemplateProgram(TemplateNameGBMVb dObj, int intStatus){

		String calLeBook = removeDescLeBook(dObj.getLeBook());
		if(!ValidationUtil.isValid(dObj.getExecutionSequence())){
			dObj.setExecutionSequence(getMinExecutionSequence(dObj));
		}
		List<TemplateNameGBMVb> collTemp = null;
		final int intKeyFieldsCount = 4;
		setServiceDefaults();
		
		String strQueryAppr = new String("SELECT distinct TAppr.COUNTRY, TAppr.LE_BOOK, TAppr.PROGRAM, TAppr.EXECUTION_SEQUENCE, TAppr.EXECUTION_TYPE_AT, TAppr.EXECUTION_TYPE, TAppr.MAIN_QUERY, " +
			"TAppr.DML_1, TAppr.FATAL_FLAG_1, TAppr.LOG_TEXT_FLAG_1, TAppr.DML_STATUS_1, TAppr.DML_2, TAppr.FATAL_FLAG_2, TAppr.LOG_TEXT_FLAG_2, TAppr.DML_STATUS_2, " +
			"TAppr.DML_3, TAppr.FATAL_FLAG_3, TAppr.LOG_TEXT_FLAG_3, TAppr.DML_STATUS_3, TAppr.DML_4, TAppr.FATAL_FLAG_4, TAppr.LOG_TEXT_FLAG_4, TAppr.DML_STATUS_4, " +
			"TAppr.DML_5, TAppr.FATAL_FLAG_5, TAppr.LOG_TEXT_FLAG_5, TAppr.DML_STATUS_5, TAppr.DML_6, TAppr.FATAL_FLAG_6, TAppr.LOG_TEXT_FLAG_6, TAppr.DML_STATUS_6, " +
			"TAppr.DML_7, TAppr.FATAL_FLAG_7, TAppr.LOG_TEXT_FLAG_7, TAppr.DML_STATUS_7, TAppr.DML_8, TAppr.FATAL_FLAG_8, TAppr.LOG_TEXT_FLAG_8, TAppr.DML_STATUS_8, " +
			"TAppr.DML_9, TAppr.FATAL_FLAG_9, TAppr.LOG_TEXT_FLAG_9, TAppr.DML_STATUS_9, TAppr.DML_10, TAppr.FATAL_FLAG_10, TAppr.LOG_TEXT_FLAG_10, TAppr.DML_STATUS_10, " +
			"TAppr.FATAL_FLAG_AT, TAppr.LOG_TEXT_FLAG_AT, TAppr.DML_STATUS_NT, TAppr.MAPPING_STATUS_NT, TAppr.MAPPING_STATUS,TAppr.COMMIT_FLAG, TAppr.RECORD_INDICATOR_NT, TAppr.RECORD_INDICATOR, " +
			"TAppr.MAKER, TAppr.VERIFIER, TAppr.INTERNAL_STATUS, TAppr.DATE_LAST_MODIFIED, TAppr.DATE_CREATION FROM RA_GENERICBUILD_MAPPINGS TAppr " +
			"WHERE TAppr.COUNTRY = ? AND TAppr.LE_BOOK = ? AND TAppr.PROGRAM = ? AND TAppr.EXECUTION_SEQUENCE = ?");
		String strQueryPend = new String("SELECT distinct TPend.COUNTRY, TPend.LE_BOOK, TPend.PROGRAM, TPend.EXECUTION_SEQUENCE, TPend.EXECUTION_TYPE_AT, TPend.EXECUTION_TYPE, TPend.MAIN_QUERY, " +
			"TPend.DML_1, TPend.FATAL_FLAG_1, TPend.LOG_TEXT_FLAG_1, TPend.DML_STATUS_1, TPend.DML_2, TPend.FATAL_FLAG_2, TPend.LOG_TEXT_FLAG_2, TPend.DML_STATUS_2, " +
			"TPend.DML_3, TPend.FATAL_FLAG_3, TPend.LOG_TEXT_FLAG_3, TPend.DML_STATUS_3, TPend.DML_4, TPend.FATAL_FLAG_4, TPend.LOG_TEXT_FLAG_4, TPend.DML_STATUS_4, " +
			"TPend.DML_5, TPend.FATAL_FLAG_5, TPend.LOG_TEXT_FLAG_5, TPend.DML_STATUS_5, TPend.DML_6, TPend.FATAL_FLAG_6, TPend.LOG_TEXT_FLAG_6, TPend.DML_STATUS_6, " +
			"TPend.DML_7, TPend.FATAL_FLAG_7, TPend.LOG_TEXT_FLAG_7, TPend.DML_STATUS_7, TPend.DML_8, TPend.FATAL_FLAG_8, TPend.LOG_TEXT_FLAG_8, TPend.DML_STATUS_8, " +
			"TPend.DML_9, TPend.FATAL_FLAG_9, TPend.LOG_TEXT_FLAG_9, TPend.DML_STATUS_9, TPend.DML_10, TPend.FATAL_FLAG_10, TPend.LOG_TEXT_FLAG_10, TPend.DML_STATUS_10, " +
			"TPend.FATAL_FLAG_AT, TPend.LOG_TEXT_FLAG_AT, TPend.DML_STATUS_NT, TPend.MAPPING_STATUS_NT, TPend.MAPPING_STATUS,TPend.COMMIT_FLAG, TPend.RECORD_INDICATOR_NT, TPend.RECORD_INDICATOR, " +
			"TPend.MAKER, TPend.VERIFIER, TPend.INTERNAL_STATUS, TPend.DATE_LAST_MODIFIED, TPend.DATE_CREATION FROM RA_GENERICBUILD_MAPPINGS TPend " +
			"WHERE TPend.COUNTRY = ? AND TPend.LE_BOOK = ? AND TPend.PROGRAM = ? AND TPend.EXECUTION_SEQUENCE = ?");
	
		Object objParams[] = new Object[intKeyFieldsCount];
		objParams[0] =  new String(dObj.getCountry());
		objParams[1] = calLeBook;
		objParams[2] = new String(dObj.getProgram().toUpperCase());
		if(ValidationUtil.isValid(dObj.getExecutionSequence()))
			objParams[3] = new String(dObj.getExecutionSequence());
		else
			objParams[3] = null;

		try{
			if(!dObj.isVerificationRequired())
			{intStatus =0;}
			if(intStatus == 0)
			{
				logger.info("Executing approved query");
				collTemp = getJdbcTemplate().query(strQueryAppr.toString(),objParams,getMapperTemplateNameProgram());
			}else{
				logger.info("Executing pending query");
				collTemp = getJdbcTemplate().query(strQueryPend.toString(),objParams,getMapperTemplateNameProgram());
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
	public List<TemplateNameGBMVb> getExecutionSequence(TemplateNameGBMVb vObject) throws DataAccessException {
		String calLeBook = removeDescLeBook(vObject.getLeBook());
		String sql = "select EXECUTION_SEQUENCE FROM RA_GENERICBUILD_MAPPINGS where UPPER(COUNTRY) = '"+vObject.getCountry()+"' AND UPPER(LE_BOOK) = '"+calLeBook+"' AND UPPER(PROGRAM) = UPPER('"+vObject.getProgram()+"') order by EXECUTION_SEQUENCE";
		return getJdbcTemplate().query(sql,  getExecutionSequence());
	}
	public RowMapper getExecutionSequence(){
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				TemplateNameGBMVb templateNameVb = new TemplateNameGBMVb();
				templateNameVb.setExecutionSequence(rs.getString("EXECUTION_SEQUENCE"));
				return templateNameVb;
			}
		};
		return mapper;
	}
	public int doInsertionTemplateNamesAppr(TemplateNameGBMVb vObject){
		String query = "Insert Into TEMPLATE_DESIGN(TEMPLATE_NAME, COLUMN_NAME, COLUMN_ID, DATA_TYPE, CHAR_USED, DATA_LENGTH, DATA_SCALING, DATA_INDEX, GUIDELINES, MANDATORY_FLAG_NT, "
			+ "MANDATORY_FLAG , DEFAULT_VALUES, COLUMN_DESCRIPTION, DATA_SAMPLE, EFFECTIVE_DATE, TEMPLATE_DESIGN_STATUS_NT, TEMPLATE_DESIGN_STATUS,"+
			"RECORD_INDICATOR_NT,RECORD_INDICATOR, MAKER, VERIFIER, INTERNAL_STATUS, DATE_LAST_MODIFIED, DATE_CREATION, FEED_STG_NAME, VIEW_NAME) "+
			"Values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, CONVERT(datetime, ?, 103), ?, ?, ?, ?, ?, ?, ?, GetDate(), GetDate())";
		Object[] args = { vObject.getTemplateName(), vObject.getColumnName(), vObject.getColumnId(), vObject.getDataType(), vObject.getCharUsed(), vObject.getDataLength(), vObject.getDataScaling(),
				vObject.getDataIndex(), vObject.getGuidelines(), vObject.getMandatoryFlagNt(), vObject.getMandatoryFlag(), vObject.getDefaultValues(), vObject.getColumnDescription(), 
				vObject.getDataSample(), vObject.getEffectiveDate(), 1, 0, 
			vObject.getRecordIndicatorNt(),vObject.getRecordIndicator(), vObject.getMaker(), vObject.getVerifier(), vObject.getInternalStatus(),vObject.getFeedStgName(),vObject.getViewName()};
			return getJdbcTemplate().update(query,args);
	}
	public int doUpdateTemplateNamesAppr(TemplateNameGBMVb vObject){
		String query = "Update TEMPLATE_DESIGN Set COLUMN_ID = ? , DATA_TYPE = ? , CHAR_USED = ? , DATA_LENGTH = ? , DATA_SCALING = ? , DATA_INDEX = ? , GUIDELINES = ? , MANDATORY_FLAG_NT = ? , "
			+ "MANDATORY_FLAG  = ? , DEFAULT_VALUES = ? , COLUMN_DESCRIPTION = ? , DATA_SAMPLE = ? , EFFECTIVE_DATE = ? , TEMPLATE_DESIGN_STATUS_NT = ? , TEMPLATE_DESIGN_STATUS = ? ,"+
			"RECORD_INDICATOR_NT = ? ,RECORD_INDICATOR = ? , MAKER = ? , VERIFIER = ? , INTERNAL_STATUS = ? , DATE_LAST_MODIFIED = ? , DATE_CREATION = ? , FEED_STG_NAME = ? , VIEW_NAME  = ? "
			+ "WHERE TEMPLATE_NAME = ? AND COLUMN_NAME = ? ";
		Object[] args = { vObject.getTemplateName(), vObject.getColumnName(), vObject.getColumnId(), vObject.getDataType(), vObject.getCharUsed(), vObject.getDataLength(), vObject.getDataScaling(),
				vObject.getDataIndex(), vObject.getGuidelines(), vObject.getMandatoryFlagNt(), vObject.getMandatoryFlag(), vObject.getDefaultValues(), vObject.getColumnDescription(), 
				vObject.getDataSample(), vObject.getEffectiveDate(), 1, 0, 
			vObject.getRecordIndicatorNt(),vObject.getRecordIndicator(), vObject.getMaker(), vObject.getVerifier(), vObject.getInternalStatus(),vObject.getFeedStgName(),vObject.getViewName()};
			return getJdbcTemplate().update(query,args);
	}
	protected int doDeleteTemplateNamesAppr(TemplateNameGBMVb vObject){
		String query = "Delete From TEMPLATE_DESIGN Where TEMPLATE_NAME = ? AND COLUMN_NAME = ? ";
		Object[] args = {vObject.getTemplateName(), vObject.getColumnName()};
		return getJdbcTemplate().update(query,args);
	}
	public List<TemplateNameGBMVb> getStakeHolderData(String Program) throws DataAccessException {
		String sql = "Select Distinct LE_BOOK+'-'+(Select Leb_description from LE_Book where LE_Book.LE_Book = T1.LE_Book and LE_Book.Country = T1.Country) as LE_BOOK FROM RA_GENERICBUILD_MAPPINGS T1 where UPPER(PROGRAM) = UPPER('"+Program+"') ORDER BY 1";
		//String sql = "Select Distinct LE_BOOK||' - '||(Select Leb_description from LE_Book where LE_Book.LE_Book = T1.LE_Book and LE_Book.Country = T1.Country) as LE_BOOK FROM RA_GENERICBUILD_MAPPINGS T1 where UPPER(PROGRAM) = UPPER('"+Program+"') ORDER BY 1";
		//String sql = "Select Distinct LE_BOOK+'-'+(Select Leb_description from LE_Book where LE_Book.LE_Book = T1.LE_Book and LE_Book.Country = T1.Country) as LE_BOOK FROM RA_GENERICBUILD_MAPPINGS T1 where UPPER(PROGRAM) = UPPER('GB_ACCOUNTSEXTRAS') ORDER BY 1";
		return getJdbcTemplate().query(sql,  getStakeHolderDataMpper());
	}
	public RowMapper getStakeHolderDataMpper(){
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				TemplateNameGBMVb templateNameVb = new TemplateNameGBMVb();
				String calLeBook = removeDescLeBook(rs.getString("LE_BOOK"));
				templateNameVb.setLeBook(calLeBook);
				templateNameVb.setLebDescription(rs.getString("LE_BOOK"));
				return templateNameVb;
			}
		};
		return mapper;
	}
	public String getCountry(String LeBook) throws DataAccessException {
		String sql = "Select Country from LE_BOOK where LE_BOOK = ?";
		Object[] lParams = new Object[1];
		lParams[0] = LeBook;
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				TemplateNameGBMVb templateNameVb = new TemplateNameGBMVb();
				templateNameVb.setCountry(rs.getString("COUNTRY"));
				return templateNameVb;
			}
		};
		List<TemplateNameGBMVb> templateNameVb = getJdbcTemplate().query(sql, lParams, mapper);
		return templateNameVb.get(0).getCountry();
	}
	public  int  getCurrVersionNo(TemplateNameGBMVb vObject){
		try{
			String calLeBook = removeDescLeBook(vObject.getLeBook());
			String query = "SELECT MAX(VERSION_NO) FROM RA_GENERICBUILD_MAPPINGS_AD where COUNTRY = '"+vObject.getCountry()+"' and LE_BOOK = '"+calLeBook+"' and PROGRAM = '"+vObject.getProgram()+"'";
			return getJdbcTemplate().queryForObject(query,Integer.class);
		}catch(Exception ex){
			ex.printStackTrace();
			return 1000;
		}
	}
	public  int  getCommVersionNo(TemplateNameGBMVb vObject){
		try{
			String calLeBook = removeDescLeBook(vObject.getLeBook());
			String query = "SELECT MAX(VERSION_NO) FROM RA_GENERICBUILD_COMMENTS where COUNTRY = '"+vObject.getCountry()+"' and LE_BOOK = '"+calLeBook+"' and PROGRAM = '"+vObject.getProgram()+"'";
			System.out.println("Max Version Query : "+query);
			return getJdbcTemplate().queryForObject(query,Integer.class);
		}catch(Exception ex){
//			ex.printStackTrace();
			return 0;
		}
	}
	public ExceptionCode insertRecordIntoAD(TemplateNameGBMVb vObject,String temp){
		setServiceDefaults();
		ExceptionCode exceptionCode1 = new ExceptionCode();
		int verNo = getCurrVersionNo(vObject);
		String calLeBook = removeDescLeBook(vObject.getLeBook());
		if("abc".equalsIgnoreCase(temp)){
			int verNo1 = getCommVersionNo(vObject);
//			verNo = vObject.getVersionNo();
			retVal = insertCommentReflect(vObject, verNo1);
		}else{
			retVal = insertIntoComment(vObject, verNo);
		}
		if (retVal != Constants.SUCCESSFUL_OPERATION){
			exceptionCode1 = getResultObject(retVal);
//			exceptionCode1.setErrorMsg("Error inserting data in COMMENTS table");
			throw buildRuntimeCustomException(exceptionCode1);
		} 
		ExceptionCode exceptionCode = new ExceptionCode();
		String query = "insert into RA_GENERICBUILD_MAPPINGS_AD (VERSION_NO,COUNTRY,LE_BOOK,PROGRAM,EXECUTION_SEQUENCE,EXECUTION_TYPE_AT,EXECUTION_TYPE,MAIN_QUERY,DML_1,FATAL_FLAG_1,LOG_TEXT_FLAG_1,"
								+ "DML_STATUS_1,DML_2,FATAL_FLAG_2,LOG_TEXT_FLAG_2,DML_STATUS_2,DML_3,FATAL_FLAG_3,LOG_TEXT_FLAG_3,DML_STATUS_3,DML_4,FATAL_FLAG_4,"
								+ "LOG_TEXT_FLAG_4,DML_STATUS_4,DML_5,FATAL_FLAG_5,LOG_TEXT_FLAG_5,DML_STATUS_5,DML_6,FATAL_FLAG_6,LOG_TEXT_FLAG_6,DML_STATUS_6,DML_7,FATAL_FLAG_7,"
								+ "LOG_TEXT_FLAG_7,DML_STATUS_7,DML_8,FATAL_FLAG_8,LOG_TEXT_FLAG_8,DML_STATUS_8,DML_9,FATAL_FLAG_9,LOG_TEXT_FLAG_9,DML_STATUS_9,DML_10,"
								+ "FATAL_FLAG_10,LOG_TEXT_FLAG_10,DML_STATUS_10,FATAL_FLAG_AT,LOG_TEXT_FLAG_AT,DML_STATUS_NT,MAPPING_STATUS_NT,MAPPING_STATUS,RECORD_INDICATOR_NT,"
								+ "RECORD_INDICATOR,MAKER,VERIFIER,INTERNAL_STATUS,DATE_LAST_MODIFIED,DATE_CREATION,COMMIT_FLAG)"
						+ "select "+(verNo+1)+" VERSION_NO,T1.COUNTRY,T1.LE_BOOK,T1.PROGRAM,T1.EXECUTION_SEQUENCE,T1.EXECUTION_TYPE_AT,T1.EXECUTION_TYPE,T1.MAIN_QUERY,T1.DML_1,"
								+ "T1.FATAL_FLAG_1,T1.LOG_TEXT_FLAG_1,T1.DML_STATUS_1,T1.DML_2,T1.FATAL_FLAG_2,T1.LOG_TEXT_FLAG_2,T1.DML_STATUS_2,T1.DML_3,T1.FATAL_FLAG_3,"
								+ "T1.LOG_TEXT_FLAG_3,T1.DML_STATUS_3,T1.DML_4,T1.FATAL_FLAG_4,T1.LOG_TEXT_FLAG_4,T1.DML_STATUS_4,T1.DML_5,T1.FATAL_FLAG_5,T1.LOG_TEXT_FLAG_5,"
								+ "T1.DML_STATUS_5,T1.DML_6,T1.FATAL_FLAG_6,T1.LOG_TEXT_FLAG_6,T1.DML_STATUS_6,T1.DML_7,T1.FATAL_FLAG_7,T1.LOG_TEXT_FLAG_7,T1.DML_STATUS_7,"
								+ "T1.DML_8,T1.FATAL_FLAG_8,T1.LOG_TEXT_FLAG_8,T1.DML_STATUS_8,T1.DML_9,T1.FATAL_FLAG_9,T1.LOG_TEXT_FLAG_9,T1.DML_STATUS_9,T1.DML_10,"
								+ "T1.FATAL_FLAG_10,T1.LOG_TEXT_FLAG_10,T1.DML_STATUS_10,T1.FATAL_FLAG_AT,T1.LOG_TEXT_FLAG_AT,T1.DML_STATUS_NT,"
								+ "T1.MAPPING_STATUS_NT,T1.MAPPING_STATUS,T1.RECORD_INDICATOR_NT,T1.RECORD_INDICATOR,T1.MAKER,T1.VERIFIER,T1.INTERNAL_STATUS,"
								+ "T1.DATE_LAST_MODIFIED,T1.DATE_CREATION,T1.COMMIT_FLAG "
						+ "From RA_GENERICBUILD_MAPPINGS T1 WHERE T1.COUNTRY = ? AND T1.LE_BOOK = ? AND T1.PROGRAM = ? ";
		
		Object[] args = { vObject.getCountry(), calLeBook, vObject.getProgram() };
		int retVal= getJdbcTemplate().update(query,args);
		if (retVal == 0){
			exceptionCode.setErrorCode(0);
			exceptionCode.setErrorMsg("Error");
		}else{
			exceptionCode.setErrorCode(1);
			exceptionCode.setErrorMsg("Record added successfully with new version no.");
		}
		exceptionCode.setOtherInfo(vObject);
		return exceptionCode;
	}
	protected int insertIntoComment(TemplateNameGBMVb vObject, int curAdVer){
		String comment = ValidationUtil.isValid(vObject.getComment())?vObject.getComment():"Default Record";
		++curAdVer;
		String query = "";
		String oldCommentStr = "";
		String calLeBook = removeDescLeBook(vObject.getLeBook());
		try{
			query = "Select COMMENTS FROM RA_GENERICBUILD_COMMENTS WHERE COUNTRY = '"+vObject.getCountry()+"' AND LE_BOOK = '"+calLeBook+"' AND PROGRAM = '"+vObject.getProgram()+"' AND VERSION_NO = '"+curAdVer+"' ";
			oldCommentStr = getJdbcTemplate().queryForObject(query, String.class);
		}catch(Exception e){
			//e.printStackTrace();
			int maxCommentVer = getCommVersionNo(vObject);
			if(ValidationUtil.isValid(maxCommentVer) && maxCommentVer > 0){
				query = "Select COMMENTS FROM RA_GENERICBUILD_COMMENTS WHERE COUNTRY = '"+vObject.getCountry()+"' AND LE_BOOK = '"+calLeBook+"' AND PROGRAM = '"+vObject.getProgram()+"' AND VERSION_NO = '"+maxCommentVer+"' ";
				oldCommentStr = getJdbcTemplate().queryForObject(query, String.class);
			}else{
				oldCommentStr = "{@#SEQ:!#1$@!EXE_SEQ:!#0000$@!STATUS:!#"+vObject.getClickStatus()+"$@!COMMENT:!#"+comment+"$@!MAKER:!#"+intCurrentUserId+"$@!DATE_CREATION:!#"+getDbFunction("SYSDATE")+"#@}";
			}
			if(!"Add".equalsIgnoreCase(vObject.getClickStatus())){
				query = "Insert Into RA_GENERICBUILD_COMMENTS(COUNTRY, LE_BOOK, PROGRAM, VERSION_NO, COMMENTS ) "+
						"Values (?, ?, ?, ?, ?)";
				Object[] args = {vObject.getCountry(), calLeBook, vObject.getProgram(), curAdVer, oldCommentStr};
				getJdbcTemplate().update(query,args);
			}
		}
		query = "Insert Into RA_GENERICBUILD_COMMENTS(COUNTRY, LE_BOOK, PROGRAM, VERSION_NO, COMMENTS ) "+
				"Values (?, ?, ?, ?, ?)";
		Object[] args = {vObject.getCountry(), calLeBook, vObject.getProgram(), curAdVer+1, oldCommentStr};
		
		return getJdbcTemplate().update(query,args);
	}
	protected int insertCommentReflect(TemplateNameGBMVb vObject, int curAdVer){
		String query = "";
		String calLeBook = removeDescLeBook(vObject.getLeBook());
		
		query = "Insert into RA_GENERICBUILD_COMMENTS(COUNTRY,LE_BOOK,PROGRAM,VERSION_NO,COMMENTS) Select COUNTRY,LE_BOOK,PROGRAM,"+(curAdVer+1)+" VERSION_NO,COMMENTS from RA_GENERICBUILD_COMMENTS where "+
                " COUNTRY = ? AND LE_BOOK = ? AND PROGRAM = ? AND VERSION_NO = ?";
		Object[] args = {vObject.getCountry(), calLeBook, vObject.getProgram(), vObject.getVersionNo()==1?1:vObject.getVersionNo()-1};
		
		return getJdbcTemplate().update(query,args);
	}
	public List<TemplateNameGBMVb> getReviewPopupResults(TemplateNameGBMVb dObj){
		List<TemplateNameGBMVb> collTemp = null;
		final int intKeyFieldsCount = 3;
		setServiceDefaults();
		String strQueryAppr = new String("SELECT VERSION_NO, EXECUTION_SEQUENCE, DATE_LAST_MODIFIED, DATE_CREATION,MAKER,VERIFIER FROM RA_GENERICBUILD_MAPPINGS_AD WHERE COUNTRY = ? AND LE_BOOK = ? AND PROGRAM = ? ORDER BY VERSION_NO, EXECUTION_SEQUENCE");
			Object objParams[] = new Object[intKeyFieldsCount];
			objParams[0] = new String(dObj.getCountry());
			objParams[1] = new String(dObj.getLeBook());	
			objParams[2] = new String(dObj.getProgram());	
		try{
			logger.info("Executing Review popUp query");
			collTemp = getJdbcTemplate().query(strQueryAppr.toString(),objParams,getMapperForReviewProgram());
		return collTemp;
		}catch(Exception ex){
			ex.printStackTrace();
			logger.error("Error: getQueryResults Exception :   ");
				logger.error(((strQueryAppr == null) ? "strQueryAppr is Null" : strQueryAppr.toString()));

			if (objParams != null)
				for(int i=0 ; i< objParams.length; i++)
					logger.error("objParams[" + i + "]" + objParams[i].toString());
			return null;
		}
	}
	protected RowMapper getMapperForReviewProgram(){
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				TemplateNameGBMVb templateNameGBMVb = new TemplateNameGBMVb();
				
				templateNameGBMVb.setVersionNo(rs.getInt("VERSION_NO"));
				templateNameGBMVb.setExecutionSequence(rs.getString("EXECUTION_SEQUENCE"));
				templateNameGBMVb.setDateLastModified(rs.getString("DATE_LAST_MODIFIED"));
				templateNameGBMVb.setDateCreation(rs.getString("DATE_CREATION"));
				templateNameGBMVb.setMaker(rs.getLong("MAKER"));
				templateNameGBMVb.setVerifier(rs.getLong("VERIFIER"));
				return templateNameGBMVb;
			}
		};
		return mapper;
	}
	@Transactional(rollbackForClassName={"com.vision.exception.RuntimeCustomException"})
	public ExceptionCode doinsertMainRecord(TemplateNameGBMVb vObject) throws RuntimeCustomException {
		ExceptionCode exceptionCode = null;
		strErrorDesc  = "";
		setServiceDefaults();
		try {
			return doInsertRecordForMain(vObject);
		}catch (RuntimeCustomException rcException){
			throw rcException;
		}catch (UncategorizedSQLException uSQLEcxception) {
			strErrorDesc = parseErrorMsg(uSQLEcxception);
			exceptionCode = getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION);
			throw buildRuntimeCustomException(exceptionCode);
		}catch(Exception ex){
			logger.error("Error in Modify.",ex);
			logger.error( ((vObject==null)? "vObject is Null":vObject.toString()));
			strErrorDesc = ex.getMessage();
			exceptionCode = getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION);
			throw buildRuntimeCustomException(exceptionCode);
		}
	}
	
	protected ExceptionCode doInsertRecordForMain(TemplateNameGBMVb vObject) throws RuntimeCustomException {
		String calLeBook = removeDescLeBook(vObject.getLeBook());
		ExceptionCode exceptionCode = null;
		try{
			insertRecordIntoAD(vObject,"abc");
		}catch(RuntimeCustomException rcException){
			throw rcException;
		}
		vObject.setMaker(getIntCurrentUserId());
		vObject.setVerifier(getIntCurrentUserId());
		retVal = doDeleteMain(vObject);
		if (retVal == 0){
			exceptionCode = getResultObject(retVal);
			throw buildRuntimeCustomException(exceptionCode);
		}
		String systemDate = getSystemDate();
		vObject.setDateLastModified(systemDate);
		vObject.setDateCreation(systemDate);
		retVal = doInsertionIntoMain(vObject);
		if (retVal == 0){
			exceptionCode = getResultObject(retVal);
			throw buildRuntimeCustomException(exceptionCode);
		}
		return getResultObject(Constants.SUCCESSFUL_OPERATION);
	}
	protected int doDeleteMain(TemplateNameGBMVb vObject){
		String calLeBook = removeDescLeBook(vObject.getLeBook());
		String query = "Delete From RA_GENERICBUILD_MAPPINGS Where COUNTRY = ? AND LE_BOOK = ? AND PROGRAM = ?";
		Object[] args = {vObject.getCountry(), calLeBook, vObject.getProgram()};
		return getJdbcTemplate().update(query,args);
	}
	public int checkProgramExistance(String program){
		String sql = "Select Count(1) from RA_GENERICBUILD_MAPPINGS where program = '"+program+"'";
		Object[] args = {};
		return getJdbcTemplate().queryForObject(sql,Integer.class);
	}
	protected int updateComment(TemplateNameGBMVb vObject){
		String calLeBook = removeDescLeBook(vObject.getLeBook());
		setServiceDefaults();
		int seq = 1;
		String oldCommentStr = "";
		String query = "";
		int versionNu = getCommVersionNo(vObject);
		vObject.setCommentVersion(versionNu);
		try{
			query = "Select COMMENTS FROM RA_GENERICBUILD_COMMENTS WHERE COUNTRY = '"+vObject.getCountry()+"'  AND LE_BOOK = '"+calLeBook+"'  AND PROGRAM = '"+vObject.getProgram()+"' AND VERSION_NO = '"+vObject.getCommentVersion()+"' ";
			System.out.println(query);
			oldCommentStr = getJdbcTemplate().queryForObject(query, String.class);
		}catch(Exception e){
			e.printStackTrace();
		}
		if(ValidationUtil.isValid(oldCommentStr)){
			Pattern pattern = Pattern.compile("\\{@#(.*?)\\#@}");
			Matcher matcher = pattern.matcher(oldCommentStr);
			while(matcher.find()){
				String seqStr = matcher.group(1);
				Matcher matcherforSeq = Pattern.compile("SEQ:!#(.*?)\\$@!").matcher(seqStr);
				seq = Integer.parseInt(matcherforSeq.find()? matcherforSeq.group(1) :"1")+1;
			}
		}
		StringBuffer commentStr = new StringBuffer("{@#SEQ:!#");
		commentStr.append(String.valueOf(seq)+"$@!");
//		commentStr.append("EXE_SEQ:!#"+vObject.getExecutionSequence()+"$@!");
		commentStr.append("STATUS:!#"+vObject.getClickStatus()+"$@!");
		commentStr.append("COMMENT:!#"+vObject.getComment()+"$@!");
//		commentStr.append("MAIN_QUERY:!#"+vObject.getOldMainQuery()+"$@!");
		
		if((ValidationUtil.isValid(vObject.getOldExecutionSequence())) && (!String.valueOf(vObject.getExecutionSequence()).equals(String.valueOf(vObject.getOldExecutionSequence()))))
			commentStr.append("EXE_SEQ:!#"+vObject.getOldExecutionSequence()+"$@!");
		
		if((ValidationUtil.isValid(vObject.getOldExecutionType())) && (!String.valueOf(vObject.getExecutionType()).equals(String.valueOf(vObject.getOldExecutionType()))))
			commentStr.append("EXE_TYPE:!#"+vObject.getOldExecutionType()+"$@!");
		
		if((ValidationUtil.isValid(vObject.getOldCommitFlag())) && (!String.valueOf(vObject.getCommitFlag()).equals(String.valueOf(vObject.getOldCommitFlag()))))
			commentStr.append("COMMIT_FLAG:!#"+vObject.getOldCommitFlag()+"$@!");
		
		if((ValidationUtil.isValid(vObject.getOldMainQuery())) && (!String.valueOf(vObject.getMainQuery()).equals(String.valueOf(vObject.getOldMainQuery()))))
			commentStr.append("MAIN_QUERY:!#"+vObject.getOldMainQuery()+"$@!");
		
//		if((ValidationUtil.isValid(vObject.getOldMainQuery())) && (String.valueOf(vObject.getMainQuery()).equals(String.valueOf(vObject.getOldMainQuery())))
//			commentStr.append("MAIN_QUERY:!#"+vObject.getOldMainQuery()+"$@!");
		
		if((ValidationUtil.isValid(vObject.getOldDml1())) && (!String.valueOf(vObject.getDml1()).equals(String.valueOf(vObject.getOldDml1()))))
			commentStr.append("DML1:!#"+vObject.getOldDml1()+"$@!");
		
		if((ValidationUtil.isValid(vObject.getOldDml2())) && (!String.valueOf(vObject.getDml2()).equals(String.valueOf(vObject.getOldDml2()))))
			commentStr.append("DML2:!#"+vObject.getOldDml2()+"$@!");
		
		if((ValidationUtil.isValid(vObject.getOldDml3())) && (!String.valueOf(vObject.getDml3()).equals(String.valueOf(vObject.getOldDml3()))))
			commentStr.append("DML3:!#"+vObject.getOldDml3()+"$@!");
		
		if((ValidationUtil.isValid(vObject.getOldDml4())) && (!String.valueOf(vObject.getDml4()).equals(String.valueOf(vObject.getOldDml4()))))
			commentStr.append("DML4:!#"+vObject.getOldDml4()+"$@!");
		
		if((ValidationUtil.isValid(vObject.getOldDml5())) && (!String.valueOf(vObject.getDml5()).equals(String.valueOf(vObject.getOldDml5()))))
			commentStr.append("DML5:!#"+vObject.getOldDml5()+"$@!");
		
		if((ValidationUtil.isValid(vObject.getOldDml6())) && (!String.valueOf(vObject.getDml6()).equals(String.valueOf(vObject.getOldDml6()))))
			commentStr.append("DML6:!#"+vObject.getOldDml6()+"$@!");
		
		if((ValidationUtil.isValid(vObject.getOldDml7())) && (!String.valueOf(vObject.getDml7()).equals(String.valueOf(vObject.getOldDml7()))))
			commentStr.append("DML7:!#"+vObject.getOldDml7()+"$@!");
		
		if((ValidationUtil.isValid(vObject.getOldDml8())) && (!String.valueOf(vObject.getDml8()).equals(String.valueOf(vObject.getOldDml8()))))
			commentStr.append("DML8:!#"+vObject.getOldDml8()+"$@!");
		
		if((ValidationUtil.isValid(vObject.getOldDml9())) && (!String.valueOf(vObject.getDml9()).equals(String.valueOf(vObject.getOldDml9()))))
			commentStr.append("DML9:!#"+vObject.getOldDml9()+"$@!");
		
		if((ValidationUtil.isValid(vObject.getOldDml10())) && (!String.valueOf(vObject.getDml10()).equals(String.valueOf(vObject.getOldDml10()))))
			commentStr.append("DML10:!#"+vObject.getOldDml10()+"$@!");
		
		if((ValidationUtil.isValid(vObject.getOldFatalFlag1())) && (!String.valueOf(vObject.getFatalFlag1()).equals(String.valueOf(vObject.getOldFatalFlag1()))))
			commentStr.append("FATAL_FLAG_1:!#"+vObject.getOldFatalFlag1()+"$@!");
		
		if((ValidationUtil.isValid(vObject.getOldFatalFlag2())) && (!String.valueOf(vObject.getFatalFlag2()).equals(String.valueOf(vObject.getOldFatalFlag2()))))
			commentStr.append("FATAL_FLAG_2:!#"+vObject.getOldFatalFlag2()+"$@!");
		
		if((ValidationUtil.isValid(vObject.getOldFatalFlag3())) && (!String.valueOf(vObject.getFatalFlag3()).equals(String.valueOf(vObject.getOldFatalFlag3()))))
			commentStr.append("FATAL_FLAG_3:!#"+vObject.getOldFatalFlag3()+"$@!");
		
		if((ValidationUtil.isValid(vObject.getOldFatalFlag4())) && (!String.valueOf(vObject.getFatalFlag4()).equals(String.valueOf(vObject.getOldFatalFlag4()))))
			commentStr.append("FATAL_FLAG_4:!#"+vObject.getOldFatalFlag4()+"$@!");
		
		if((ValidationUtil.isValid(vObject.getOldFatalFlag5())) && (!String.valueOf(vObject.getFatalFlag5()).equals(String.valueOf(vObject.getOldFatalFlag5()))))
			commentStr.append("FATAL_FLAG_5:!#"+vObject.getOldFatalFlag5()+"$@!");
		
		if((ValidationUtil.isValid(vObject.getOldFatalFlag6())) && (!String.valueOf(vObject.getFatalFlag6()).equals(String.valueOf(vObject.getOldFatalFlag6()))))
			commentStr.append("FATAL_FLAG_6:!#"+vObject.getOldFatalFlag6()+"$@!");
		
		if((ValidationUtil.isValid(vObject.getOldFatalFlag7())) && (!String.valueOf(vObject.getFatalFlag7()).equals(String.valueOf(vObject.getOldFatalFlag7()))))
			commentStr.append("FATAL_FLAG_7:!#"+vObject.getOldFatalFlag7()+"$@!");
		
		if((ValidationUtil.isValid(vObject.getOldFatalFlag8())) && (!String.valueOf(vObject.getFatalFlag8()).equals(String.valueOf(vObject.getOldFatalFlag8()))))
			commentStr.append("FATAL_FLAG_8:!#"+vObject.getOldFatalFlag8()+"$@!");
		
		if((ValidationUtil.isValid(vObject.getOldFatalFlag9())) && (!String.valueOf(vObject.getFatalFlag9()).equals(String.valueOf(vObject.getOldFatalFlag9()))))
			commentStr.append("FATAL_FLAG_9:!#"+vObject.getOldFatalFlag9()+"$@!");
		
		if((ValidationUtil.isValid(vObject.getOldFatalFlag10())) && (!String.valueOf(vObject.getFatalFlag10()).equals(String.valueOf(vObject.getOldFatalFlag10()))))
			commentStr.append("FATAL_FLAG_10:!#"+vObject.getOldFatalFlag10()+"$@!");
		
		if((ValidationUtil.isValid(vObject.getOldLogTextFlag1())) && (!String.valueOf(vObject.getLogTextFlag1()).equals(String.valueOf(vObject.getOldLogTextFlag1()))))
			commentStr.append("LOG_TEXT_FLAG_1:!#"+vObject.getOldLogTextFlag1()+"$@!");
		
		if((ValidationUtil.isValid(vObject.getOldLogTextFlag2())) && (!String.valueOf(vObject.getLogTextFlag2()).equals(String.valueOf(vObject.getOldLogTextFlag2()))))
			commentStr.append("LOG_TEXT_FLAG_2:!#"+vObject.getOldLogTextFlag2()+"$@!");
		
		if((ValidationUtil.isValid(vObject.getOldLogTextFlag3())) && (!String.valueOf(vObject.getLogTextFlag3()).equals(String.valueOf(vObject.getOldLogTextFlag3()))))
			commentStr.append("LOG_TEXT_FLAG_3:!#"+vObject.getOldLogTextFlag3()+"$@!");
		
		if((ValidationUtil.isValid(vObject.getOldLogTextFlag4())) && (!String.valueOf(vObject.getLogTextFlag4()).equals(String.valueOf(vObject.getOldLogTextFlag4()))))
			commentStr.append("LOG_TEXT_FLAG_4:!#"+vObject.getOldLogTextFlag4()+"$@!");
		
		if((ValidationUtil.isValid(vObject.getOldLogTextFlag5())) && (!String.valueOf(vObject.getLogTextFlag5()).equals(String.valueOf(vObject.getOldLogTextFlag5()))))
			commentStr.append("LOG_TEXT_FLAG_5:!#"+vObject.getOldLogTextFlag5()+"$@!");
		
		if((ValidationUtil.isValid(vObject.getOldLogTextFlag6())) && (!String.valueOf(vObject.getLogTextFlag6()).equals(String.valueOf(vObject.getOldLogTextFlag6()))))
			commentStr.append("LOG_TEXT_FLAG_6:!#"+vObject.getOldLogTextFlag6()+"$@!");
		
		if((ValidationUtil.isValid(vObject.getOldLogTextFlag7())) && (!String.valueOf(vObject.getLogTextFlag7()).equals(String.valueOf(vObject.getOldLogTextFlag7()))))
			commentStr.append("LOG_TEXT_FLAG_7:!#"+vObject.getOldLogTextFlag7()+"$@!");
		
		if((ValidationUtil.isValid(vObject.getOldLogTextFlag8())) && (!String.valueOf(vObject.getLogTextFlag8()).equals(String.valueOf(vObject.getOldLogTextFlag8()))))
			commentStr.append("LOG_TEXT_FLAG_8:!#"+vObject.getOldLogTextFlag8()+"$@!");
		
		if((ValidationUtil.isValid(vObject.getOldLogTextFlag9())) && (!String.valueOf(vObject.getLogTextFlag9()).equals(String.valueOf(vObject.getOldLogTextFlag9()))))
			commentStr.append("LOG_TEXT_FLAG_9:!#"+vObject.getOldLogTextFlag9()+"$@!");
		
		if((ValidationUtil.isValid(vObject.getOldLogTextFlag10())) && (!String.valueOf(vObject.getLogTextFlag10()).equals(String.valueOf(vObject.getOldLogTextFlag10()))))
			commentStr.append("LOG_TEXT_FLAG_10:!#"+vObject.getOldLogTextFlag10()+"$@!");
		
		if((ValidationUtil.isValid(vObject.getOldDmlStatus1())) && (!String.valueOf(vObject.getDmlStatus1()).equals(String.valueOf(vObject.getOldDmlStatus1()))))
			commentStr.append("DML_STATUS_1:!#"+vObject.getOldDmlStatus1()+"$@!");
		
		if((ValidationUtil.isValid(vObject.getOldDmlStatus2())) && (!String.valueOf(vObject.getDmlStatus2()).equals(String.valueOf(vObject.getOldDmlStatus2()))))
			commentStr.append("DML_STATUS_2:!#"+vObject.getOldDmlStatus2()+"$@!");
		
		if((ValidationUtil.isValid(vObject.getOldDmlStatus3())) && (!String.valueOf(vObject.getDmlStatus3()).equals(String.valueOf(vObject.getOldDmlStatus3()))))
			commentStr.append("DML_STATUS_3:!#"+vObject.getOldDmlStatus3()+"$@!");
		
		if((ValidationUtil.isValid(vObject.getOldDmlStatus4())) && (!String.valueOf(vObject.getDmlStatus4()).equals(String.valueOf(vObject.getOldDmlStatus4()))))
			commentStr.append("DML_STATUS_4:!#"+vObject.getOldDmlStatus4()+"$@!");
		
		if((ValidationUtil.isValid(vObject.getOldDmlStatus5())) && (!String.valueOf(vObject.getDmlStatus5()).equals(String.valueOf(vObject.getOldDmlStatus5()))))
			commentStr.append("DML_STATUS_5:!#"+vObject.getOldDmlStatus5()+"$@!");
		
		if((ValidationUtil.isValid(vObject.getOldDmlStatus6())) && (!String.valueOf(vObject.getDmlStatus6()).equals(String.valueOf(vObject.getOldDmlStatus6()))))
			commentStr.append("DML_STATUS_6:!#"+vObject.getOldDmlStatus6()+"$@!");
		
		if((ValidationUtil.isValid(vObject.getOldDmlStatus7())) && (!String.valueOf(vObject.getDmlStatus7()).equals(String.valueOf(vObject.getOldDmlStatus7()))))
			commentStr.append("DML_STATUS_7:!#"+vObject.getOldDmlStatus7()+"$@!");
		
		if((ValidationUtil.isValid(vObject.getOldDmlStatus8())) && (!String.valueOf(vObject.getDmlStatus8()).equals(String.valueOf(vObject.getOldDmlStatus8()))))
			commentStr.append("DML_STATUS_8:!#"+vObject.getOldDmlStatus8()+"$@!");
		
		if((ValidationUtil.isValid(vObject.getOldDmlStatus9())) && (!String.valueOf(vObject.getDmlStatus9()).equals(String.valueOf(vObject.getOldDmlStatus9()))))
			commentStr.append("DML_STATUS_9:!#"+vObject.getOldDmlStatus9()+"$@!");
		
		if((ValidationUtil.isValid(vObject.getOldDmlStatus10())) && (!String.valueOf(vObject.getDmlStatus10()).equals(String.valueOf(vObject.getOldDmlStatus10()))))
			commentStr.append("DML_STATUS_10:!#"+vObject.getOldDmlStatus10()+"$@!");
		
		commentStr.append("MAKER:!#"+intCurrentUserId+"$@!");
		commentStr.append("DATE_CREATION:!#"+getDbFunction("SYSDATE")+"#@}");
		
		int count = 0;
		query = "UPDATE RA_GENERICBUILD_COMMENTS SET COMMENTS = ? WHERE COUNTRY = ?  AND LE_BOOK = ? AND PROGRAM = ? AND VERSION_NO = ? ";
		
		Object[] args = {(ValidationUtil.isValid(oldCommentStr)?oldCommentStr:"")+String.valueOf(commentStr), vObject.getCountry(), calLeBook, vObject.getProgram(), vObject.getCommentVersion()};
		count = getJdbcTemplate().update(query,args);
		if(count==0){
			query = "Insert Into RA_GENERICBUILD_COMMENTS(COUNTRY, LE_BOOK, PROGRAM, VERSION_NO, COMMENTS ) "+
					"Values (?, ?, ?, ?, ?)";
				
			Object[] args1 = {vObject.getCountry(), calLeBook, vObject.getProgram(), vObject.getCommentVersion()+1, (ValidationUtil.isValid(oldCommentStr)?oldCommentStr:"")+String.valueOf(commentStr)};
			return getJdbcTemplate().update(query,args1);
		}
		return count;
	}
	public String getCurrentDate(){
			return getJdbcTemplate().queryForObject("Select FORMAT(getdate(),'dd-MMM-yyyy')", String.class);
	}
	protected int doInsertionIntoMain(TemplateNameGBMVb vObject){
		String calLeBook = removeDescLeBook(vObject.getLeBook());
		try {
			String query = "Insert Into RA_GENERICBUILD_MAPPINGS (COUNTRY, LE_BOOK, PROGRAM, EXECUTION_SEQUENCE, EXECUTION_TYPE_AT, EXECUTION_TYPE, MAIN_QUERY, "+
								  "DML_1, FATAL_FLAG_1, LOG_TEXT_FLAG_1, DML_STATUS_1, DML_2, FATAL_FLAG_2, LOG_TEXT_FLAG_2, DML_STATUS_2, DML_3,"+
								  "FATAL_FLAG_3, LOG_TEXT_FLAG_3, DML_STATUS_3, DML_4, FATAL_FLAG_4, LOG_TEXT_FLAG_4, DML_STATUS_4, DML_5, FATAL_FLAG_5,"+
								  "LOG_TEXT_FLAG_5, DML_STATUS_5, DML_6, FATAL_FLAG_6, LOG_TEXT_FLAG_6, DML_STATUS_6, DML_7, FATAL_FLAG_7, LOG_TEXT_FLAG_7,"+
								  "DML_STATUS_7, DML_8, FATAL_FLAG_8, LOG_TEXT_FLAG_8, DML_STATUS_8, DML_9, FATAL_FLAG_9, LOG_TEXT_FLAG_9, DML_STATUS_9, DML_10,"+
								  "FATAL_FLAG_10, LOG_TEXT_FLAG_10, DML_STATUS_10, FATAL_FLAG_AT, LOG_TEXT_FLAG_AT, DML_STATUS_NT, MAPPING_STATUS_NT, MAPPING_STATUS,"+
								  "RECORD_INDICATOR_NT, RECORD_INDICATOR, MAKER, VERIFIER, INTERNAL_STATUS, DATE_LAST_MODIFIED, DATE_CREATION, COMMIT_FLAG)"+
						   "Select T1.COUNTRY,T1.LE_BOOK,T1.PROGRAM,T1.EXECUTION_SEQUENCE,T1.EXECUTION_TYPE_AT,T1.EXECUTION_TYPE,T1.MAIN_QUERY,"+
								   "T1.DML_1,T1.FATAL_FLAG_1,T1.LOG_TEXT_FLAG_1,T1.DML_STATUS_1,T1.DML_2,T1.FATAL_FLAG_2,T1.LOG_TEXT_FLAG_2,T1.DML_STATUS_2,T1.DML_3,"+
								   "T1.FATAL_FLAG_3,T1.LOG_TEXT_FLAG_3,T1.DML_STATUS_3,T1.DML_4,T1.FATAL_FLAG_4,T1.LOG_TEXT_FLAG_4,T1.DML_STATUS_4,T1.DML_5,T1.FATAL_FLAG_5,"+
								   "T1.LOG_TEXT_FLAG_5,T1.DML_STATUS_5,T1.DML_6,T1.FATAL_FLAG_6,T1.LOG_TEXT_FLAG_6,T1.DML_STATUS_6,T1.DML_7,T1.FATAL_FLAG_7,T1.LOG_TEXT_FLAG_7,"+
								   "T1.DML_STATUS_7,T1.DML_8,T1.FATAL_FLAG_8,T1.LOG_TEXT_FLAG_8,T1.DML_STATUS_8,T1.DML_9,T1.FATAL_FLAG_9,T1.LOG_TEXT_FLAG_9,T1.DML_STATUS_9,T1.DML_10,"+
								   "T1.FATAL_FLAG_10,T1.LOG_TEXT_FLAG_10,T1.DML_STATUS_10,T1.FATAL_FLAG_AT,T1.LOG_TEXT_FLAG_AT,T1.DML_STATUS_NT,T1.MAPPING_STATUS_NT,T1.MAPPING_STATUS,"+
								   "T1.RECORD_INDICATOR_NT,T1.RECORD_INDICATOR,T1.MAKER,T1.VERIFIER,T1.INTERNAL_STATUS,T1.DATE_LAST_MODIFIED,T1.DATE_CREATION,T1.COMMIT_FLAG "+
						   "FROM RA_GENERICBUILD_MAPPINGS_AD T1 WHERE T1.COUNTRY = ? AND T1.LE_BOOK = ? AND T1.PROGRAM = ? AND T1.VERSION_NO = ? ";
			Object[] args = { vObject.getCountry(), calLeBook, vObject.getProgram(), vObject.getVersionNo()};
		
			int retVal= getJdbcTemplate().update(query,args);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return retVal;
	}
	public List<TemplateNameGBMVb> getComplist(TemplateNameGBMVb dObj,String ver){
		String calLeBook = removeDescLeBook(dObj.getLeBook());
		String strQueryAppr = null;
		List<TemplateNameGBMVb> collTemp = null;
		if(ValidationUtil.isValid(ver)){
		final int intKeyFieldsCount = 4;
		strQueryAppr = "SELECT DISTINCT cast(EXECUTION_SEQUENCE as Integer) as EXECUTION_SEQUENCE  FROM RA_GENERICBUILD_MAPPINGS_AD WHERE COUNTRY = ?  AND LE_BOOK = ?  AND PROGRAM = ? AND VERSION_NO = ? ORDER BY cast(EXECUTION_SEQUENCE as Integer) ";
		
		Object objParams[] = new Object[intKeyFieldsCount];
		objParams[0] = new String(dObj.getCountry());
		objParams[1] = new String(calLeBook);
		objParams[2] = new String(dObj.getProgram());
		objParams[3] = new Integer(ver);
		try{
			collTemp = getJdbcTemplate().query(strQueryAppr,objParams,getCompareMapper());
//			return collTemp;
		}
		catch(Exception ex){
			ex.printStackTrace();
			return null;
		}
	 }else{
		 final int intKeyFieldsCount = 3;
			strQueryAppr = "SELECT DISTINCT cast(EXECUTION_SEQUENCE as Integer) as EXECUTION_SEQUENCE FROM RA_GENERICBUILD_MAPPINGS WHERE COUNTRY = ?  AND LE_BOOK = ?  AND PROGRAM = ? ORDER BY EXECUTION_SEQUENCE ";
			
			Object objParams[] = new Object[intKeyFieldsCount];
			objParams[0] = new String(dObj.getCountry());
			objParams[1] = new String(calLeBook);
			objParams[2] = new String(dObj.getProgram());
			try{
				collTemp = getJdbcTemplate().query(strQueryAppr,objParams,getCompareMapper());
//				return collTemp;
			}
			catch(Exception ex){
				ex.printStackTrace();
				return null;
			}
	   }
		return collTemp;
	}
	
	public List<TemplateNameGBMVb> getCompareExeSeqlist(TemplateNameGBMVb dObj,int ver){
		String calLeBook = removeDescLeBook(dObj.getLeBook());
		String strQueryAppr = null;
		List<TemplateNameGBMVb> collTemp = null;
		if(ver!=9999){
		final int intKeyFieldsCount = 5;
		strQueryAppr = "Select EXECUTION_TYPE,COMMIT_FLAG,MAIN_QUERY,DML_1,DML_2,DML_3,DML_4,DML_5,DML_6,DML_7,DML_8,DML_9,DML_10,"
				+ "FATAL_FLAG_1,LOG_TEXT_FLAG_1,DML_STATUS_1,FATAL_FLAG_2,LOG_TEXT_FLAG_2,DML_STATUS_2,FATAL_FLAG_3,LOG_TEXT_FLAG_3,"
				+ "DML_STATUS_3,FATAL_FLAG_4,LOG_TEXT_FLAG_4,DML_STATUS_4,FATAL_FLAG_5,LOG_TEXT_FLAG_5,DML_STATUS_5,FATAL_FLAG_6,LOG_TEXT_FLAG_6,"
				+ "DML_STATUS_6,FATAL_FLAG_7,LOG_TEXT_FLAG_7,DML_STATUS_7,FATAL_FLAG_8,LOG_TEXT_FLAG_8,DML_STATUS_8,FATAL_FLAG_9,LOG_TEXT_FLAG_9,"
				+ "DML_STATUS_9,FATAL_FLAG_10,LOG_TEXT_FLAG_10,DML_STATUS_10"
				+ " from RA_GENERICBUILD_MAPPINGS_AD where COUNTRY = ? AND LE_BOOK = ? and PROGRAM = ? and EXECUTION_SEQUENCE = ? and VERSION_NO = ?";
		
		Object objParams[] = new Object[intKeyFieldsCount];
		objParams[0] = new String(dObj.getCountry());
		objParams[1] = new String(calLeBook);
		//objParams[1] = new String(dObj.getLeBook());
		objParams[2] = new String(dObj.getProgram());
		objParams[3] = new Integer(dObj.getExecutionSequence());
		objParams[4] = new Integer(ver);
		try{
			collTemp = getJdbcTemplate().query(strQueryAppr,objParams,getCompareExeSeqMapper());
//			return collTemp;
		}
		catch(Exception ex){
			ex.printStackTrace();
			return null;
		}
	 }else{
		 final int intKeyFieldsCount = 4;
			strQueryAppr = "Select EXECUTION_TYPE,COMMIT_FLAG,MAIN_QUERY,DML_1,DML_2,DML_3,DML_4,DML_5,DML_6,DML_7,DML_8,DML_9,DML_10,"
					+ "FATAL_FLAG_1,LOG_TEXT_FLAG_1,DML_STATUS_1,FATAL_FLAG_2,LOG_TEXT_FLAG_2,DML_STATUS_2,FATAL_FLAG_3,LOG_TEXT_FLAG_3,"
					+ "DML_STATUS_3,FATAL_FLAG_4,LOG_TEXT_FLAG_4,DML_STATUS_4,FATAL_FLAG_5,LOG_TEXT_FLAG_5,DML_STATUS_5,FATAL_FLAG_6,LOG_TEXT_FLAG_6,"
					+ "DML_STATUS_6,FATAL_FLAG_7,LOG_TEXT_FLAG_7,DML_STATUS_7,FATAL_FLAG_8,LOG_TEXT_FLAG_8,DML_STATUS_8,FATAL_FLAG_9,LOG_TEXT_FLAG_9,"
					+ "DML_STATUS_9,FATAL_FLAG_10,LOG_TEXT_FLAG_10,DML_STATUS_10"
					+ " from RA_GENERICBUILD_MAPPINGS where COUNTRY = ? AND LE_BOOK = ? and PROGRAM = ? and EXECUTION_SEQUENCE = ?";
			
			Object objParams[] = new Object[intKeyFieldsCount];
			objParams[0] = new String(dObj.getCountry());
			objParams[1] = new String(calLeBook);
		//	objParams[1] = new String(dObj.getLeBook());
			objParams[2] = new String(dObj.getProgram());
			objParams[3] = new String(dObj.getExecutionSequence());
			try{
				collTemp = getJdbcTemplate().query(strQueryAppr,objParams,getCompareExeSeqMapper());
//				return collTemp;
			}
			catch(Exception ex){
				ex.printStackTrace();
				return null;
			}
	   }
		return collTemp;
	}
	
	public List<TemplateNameGBMVb> getCompareDMLlist(TemplateNameGBMVb dObj,int ver,int DML){
		String calLeBook = removeDescLeBook(dObj.getLeBook());
		String strQueryAppr = null;
		List<TemplateNameGBMVb> collTemp = null;
		if(ver!=9999){
		final int intKeyFieldsCount = 5;
		strQueryAppr = "SELECT dml_"+DML+" as DML_QUERY,fatal_flag_"+DML+" as FATAL_FLAG,log_text_flag_"+DML+" as LOG_TEXT_FLAG, dml_status_"+DML+" AS DML_STATUS FROM RA_GENERICBUILD_MAPPINGS_ad "
				     + "WHERE country = ? AND le_book = ? AND program = ? AND execution_sequence = ? AND version_no = ?";
		
		Object objParams[] = new Object[intKeyFieldsCount];
		objParams[0] = new String(dObj.getCountry());
		objParams[1] = new String(calLeBook);
		objParams[2] = new String(dObj.getProgram());
		objParams[3] = new Integer(dObj.getExecutionSequence());
		objParams[4] = new Integer(ver);
		try{
			collTemp = getJdbcTemplate().query(strQueryAppr,objParams,getCompareDMLMapper());
//			return collTemp;
		}
		catch(Exception ex){
			ex.printStackTrace();
			return null;
		}
	 }else{
		 final int intKeyFieldsCount = 4;
			strQueryAppr = "SELECT dml_"+DML+" as DML_QUERY,fatal_flag_"+DML+" as FATAL_FLAG,log_text_flag_"+DML+" as LOG_TEXT_FLAG, dml_status_"+DML+" AS DML_STATUS FROM RA_GENERICBUILD_MAPPINGS "
				     + "WHERE country = ? AND le_book = ? AND program = ? AND execution_sequence = ? ";
			
			Object objParams[] = new Object[intKeyFieldsCount];
			objParams[0] = new String(dObj.getCountry());
			//objParams[1] = new String(calLeBook);
			objParams[1] = new String(dObj.getLeBook());
			objParams[2] = new String(dObj.getProgram());
			objParams[3] = new String(dObj.getExecutionSequence());
			try{
				collTemp = getJdbcTemplate().query(strQueryAppr,objParams,getCompareDMLMapper());
//				return collTemp;
			}
			catch(Exception ex){
				ex.printStackTrace();
				return null;
			}
	   }
		return collTemp;
	}
	
	protected RowMapper getCompareMapper(){
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				TemplateNameGBMVb templateNameGBMVb = new TemplateNameGBMVb();
				templateNameGBMVb.setExecutionSequence(rs.getString("EXECUTION_SEQUENCE"));
				return templateNameGBMVb;
			}
		};
		return mapper;
	}
	protected RowMapper getCompareExeSeqMapper(){
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				TemplateNameGBMVb templateNameGBMVb = new TemplateNameGBMVb();
				templateNameGBMVb.setExecutionType(rs.getString("EXECUTION_TYPE"));
				templateNameGBMVb.setCommitFlag(rs.getString("COMMIT_FLAG"));
				templateNameGBMVb.setMainQuery(rs.getString("MAIN_QUERY"));
				templateNameGBMVb.setDml1(ValidationUtil.isValid(rs.getString("DML_1"))?rs.getString("DML_1"):"");
				templateNameGBMVb.setDml2(ValidationUtil.isValid(rs.getString("DML_2"))?rs.getString("DML_2"):"");
				templateNameGBMVb.setDml3(ValidationUtil.isValid(rs.getString("DML_3"))?rs.getString("DML_3"):"");
				templateNameGBMVb.setDml4(ValidationUtil.isValid(rs.getString("DML_4"))?rs.getString("DML_4"):"");
				templateNameGBMVb.setDml5(ValidationUtil.isValid(rs.getString("DML_5"))?rs.getString("DML_5"):"");
				templateNameGBMVb.setDml6(ValidationUtil.isValid(rs.getString("DML_6"))?rs.getString("DML_6"):"");
				templateNameGBMVb.setDml7(ValidationUtil.isValid(rs.getString("DML_7"))?rs.getString("DML_7"):"");
				templateNameGBMVb.setDml8(ValidationUtil.isValid(rs.getString("DML_8"))?rs.getString("DML_8"):"");
				templateNameGBMVb.setDml9(ValidationUtil.isValid(rs.getString("DML_9"))?rs.getString("DML_9"):"");
				templateNameGBMVb.setDml10(ValidationUtil.isValid(rs.getString("DML_10"))?rs.getString("DML_10"):"");
				templateNameGBMVb.setFatalFlag1(rs.getString("FATAL_FLAG_1"));
				templateNameGBMVb.setFatalFlag2(rs.getString("FATAL_FLAG_2"));
				templateNameGBMVb.setFatalFlag3(rs.getString("FATAL_FLAG_3"));
				templateNameGBMVb.setFatalFlag4(rs.getString("FATAL_FLAG_4"));
				templateNameGBMVb.setFatalFlag5(rs.getString("FATAL_FLAG_5"));
				templateNameGBMVb.setFatalFlag6(rs.getString("FATAL_FLAG_6"));
				templateNameGBMVb.setFatalFlag7(rs.getString("FATAL_FLAG_7"));
				templateNameGBMVb.setFatalFlag8(rs.getString("FATAL_FLAG_8"));
				templateNameGBMVb.setFatalFlag9(rs.getString("FATAL_FLAG_9"));
				templateNameGBMVb.setFatalFlag10(rs.getString("FATAL_FLAG_10"));
				templateNameGBMVb.setLogTextFlag1(rs.getString("LOG_TEXT_FLAG_1"));
				templateNameGBMVb.setLogTextFlag2(rs.getString("LOG_TEXT_FLAG_2"));
				templateNameGBMVb.setLogTextFlag3(rs.getString("LOG_TEXT_FLAG_3"));
				templateNameGBMVb.setLogTextFlag4(rs.getString("LOG_TEXT_FLAG_4"));
				templateNameGBMVb.setLogTextFlag5(rs.getString("LOG_TEXT_FLAG_5"));
				templateNameGBMVb.setLogTextFlag6(rs.getString("LOG_TEXT_FLAG_6"));
				templateNameGBMVb.setLogTextFlag7(rs.getString("LOG_TEXT_FLAG_7"));
				templateNameGBMVb.setLogTextFlag8(rs.getString("LOG_TEXT_FLAG_8"));
				templateNameGBMVb.setLogTextFlag9(rs.getString("LOG_TEXT_FLAG_9"));
				templateNameGBMVb.setLogTextFlag10(rs.getString("LOG_TEXT_FLAG_10"));
				templateNameGBMVb.setDmlStatus1(rs.getString("DML_STATUS_1"));
				templateNameGBMVb.setDmlStatus2(rs.getString("DML_STATUS_2"));
				templateNameGBMVb.setDmlStatus3(rs.getString("DML_STATUS_3"));
				templateNameGBMVb.setDmlStatus4(rs.getString("DML_STATUS_4"));
				templateNameGBMVb.setDmlStatus5(rs.getString("DML_STATUS_5"));
				templateNameGBMVb.setDmlStatus6(rs.getString("DML_STATUS_6"));
				templateNameGBMVb.setDmlStatus7(rs.getString("DML_STATUS_7"));
				templateNameGBMVb.setDmlStatus8(rs.getString("DML_STATUS_8"));
				templateNameGBMVb.setDmlStatus9(rs.getString("DML_STATUS_9"));
				templateNameGBMVb.setDmlStatus10(rs.getString("DML_STATUS_10"));
				
				return templateNameGBMVb;
			}
		};
		return mapper;
	}
	protected RowMapper getCompareDMLMapper(){
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				TemplateNameGBMVb templateNameGBMVb = new TemplateNameGBMVb();
				templateNameGBMVb.setDml(ValidationUtil.isValid(rs.getString("DML_QUERY"))?rs.getString("DML_QUERY"):"");
				templateNameGBMVb.setFatalFlag(rs.getString("FATAL_FLAG"));
				templateNameGBMVb.setLogTextFlag(rs.getString("LOG_TEXT_FLAG"));
				templateNameGBMVb.setDmlStatus(rs.getString("DML_STATUS"));
				
				return templateNameGBMVb;
			}
		};
		return mapper;
	}
	public List<TemplateNameGBMVb> getCommentsReviewPopupResults(TemplateNameGBMVb dObj){
		String query = "";
		String calLeBook = removeDescLeBook(dObj.getLeBook());
		String collTemp = null; 
		int maxCommentVer = getCommVersionNo(dObj);
		try{
			query = "Select COMMENTS FROM RA_GENERICBUILD_COMMENTS WHERE COUNTRY = '"+dObj.getCountry()+"' AND LE_BOOK = '"+calLeBook+"' AND PROGRAM = '"+dObj.getProgram()+"' AND VERSION_NO = '"+maxCommentVer+"' ";
			collTemp = getJdbcTemplate().queryForObject(query, String.class);
		}catch(Exception ex){
			ex.printStackTrace();
			return null;
		}
		try {
			ArrayList<String> commentSeqAL = new ArrayList<String>();
			List<TemplateNameGBMVb> indList = new ArrayList<TemplateNameGBMVb>();

			Pattern pattern = Pattern.compile("\\{@#(.*?)\\#@}");
			Matcher matcher = pattern.matcher(collTemp);
			while (matcher.find()) {
			    commentSeqAL.add(matcher.group(1));
			}
			for(String commentSeqStr : commentSeqAL){
				TemplateNameGBMVb commentSeqVb = new TemplateNameGBMVb();
				
				Matcher regexMatcher = Pattern.compile("SEQ:!#(.*?)\\$@!").matcher(commentSeqStr);
				commentSeqVb.setCommentSeq(regexMatcher.find()? regexMatcher.group(1) :"");
				
				regexMatcher = Pattern.compile("EXE_SEQ:!#(.*?)\\$@!").matcher(commentSeqStr);
				commentSeqVb.setExecutionSequence(regexMatcher.find()? regexMatcher.group(1) :"");
				
				regexMatcher = Pattern.compile("EXE_TYPE:!#(.*?)\\$@!").matcher(commentSeqStr);
				commentSeqVb.setExecutionType(regexMatcher.find()? regexMatcher.group(1) :"");
				
				regexMatcher = Pattern.compile("COMMIT_FLAG:!#(.*?)\\$@!").matcher(commentSeqStr);
				commentSeqVb.setCommitFlag(regexMatcher.find()? regexMatcher.group(1) :"");
				
				regexMatcher = Pattern.compile("COMMENT:!#(.*?)\\$@!").matcher(commentSeqStr);
				commentSeqVb.setComment(regexMatcher.find()? regexMatcher.group(1) :"");
				
				regexMatcher = Pattern.compile("MAIN_QUERY:!#(.*?)\\$@!").matcher(commentSeqStr);
				commentSeqVb.setMainQuery(regexMatcher.find()? regexMatcher.group(1) :"");
				
				regexMatcher = Pattern.compile("DML1:!#(.*?)\\$@!").matcher(commentSeqStr);
				commentSeqVb.setDml1(regexMatcher.find()? regexMatcher.group(1) :"");
				
				regexMatcher = Pattern.compile("DML2:!#(.*?)\\$@!").matcher(commentSeqStr);
				commentSeqVb.setDml2(regexMatcher.find()? regexMatcher.group(1) :"");
				
				regexMatcher = Pattern.compile("DML3:!#(.*?)\\$@!").matcher(commentSeqStr);
				commentSeqVb.setDml3(regexMatcher.find()? regexMatcher.group(1) :"");
				
				regexMatcher = Pattern.compile("DML4:!#(.*?)\\$@!").matcher(commentSeqStr);
				commentSeqVb.setDml4(regexMatcher.find()? regexMatcher.group(1) :"");
				
				regexMatcher = Pattern.compile("DML5:!#(.*?)\\$@!").matcher(commentSeqStr);
				commentSeqVb.setDml5(regexMatcher.find()? regexMatcher.group(1) :"");
				
				regexMatcher = Pattern.compile("DML6:!#(.*?)\\$@!").matcher(commentSeqStr);
				commentSeqVb.setDml6(regexMatcher.find()? regexMatcher.group(1) :"");
				
				regexMatcher = Pattern.compile("DML7:!#(.*?)\\$@!").matcher(commentSeqStr);
				commentSeqVb.setDml7(regexMatcher.find()? regexMatcher.group(1) :"");
				
				regexMatcher = Pattern.compile("DML8:!#(.*?)\\$@!").matcher(commentSeqStr);
				commentSeqVb.setDml8(regexMatcher.find()? regexMatcher.group(1) :"");
				
				regexMatcher = Pattern.compile("DML9:!#(.*?)\\$@!").matcher(commentSeqStr);
				commentSeqVb.setDml9(regexMatcher.find()? regexMatcher.group(1) :"");
				
				regexMatcher = Pattern.compile("DML10:!#(.*?)\\$@!").matcher(commentSeqStr);
				commentSeqVb.setDml10(regexMatcher.find()? regexMatcher.group(1) :"");
				
				regexMatcher = Pattern.compile("FATAL_FLAG_1:!#(.*?)\\$@!").matcher(commentSeqStr);
				commentSeqVb.setFatalFlag1(regexMatcher.find()? regexMatcher.group(1) :"");
				
				regexMatcher = Pattern.compile("FATAL_FLAG_2:!#(.*?)\\$@!").matcher(commentSeqStr);
				commentSeqVb.setFatalFlag2(regexMatcher.find()? regexMatcher.group(1) :"");
				
				regexMatcher = Pattern.compile("FATAL_FLAG_3:!#(.*?)\\$@!").matcher(commentSeqStr);
				commentSeqVb.setFatalFlag3(regexMatcher.find()? regexMatcher.group(1) :"");
				
				regexMatcher = Pattern.compile("FATAL_FLAG_4:!#(.*?)\\$@!").matcher(commentSeqStr);
				commentSeqVb.setFatalFlag4(regexMatcher.find()? regexMatcher.group(1) :"");
				
				regexMatcher = Pattern.compile("FATAL_FLAG_5:!#(.*?)\\$@!").matcher(commentSeqStr);
				commentSeqVb.setFatalFlag5(regexMatcher.find()? regexMatcher.group(1) :"");
				
				regexMatcher = Pattern.compile("FATAL_FLAG_6:!#(.*?)\\$@!").matcher(commentSeqStr);
				commentSeqVb.setFatalFlag6(regexMatcher.find()? regexMatcher.group(1) :"");
				
				regexMatcher = Pattern.compile("FATAL_FLAG_7:!#(.*?)\\$@!").matcher(commentSeqStr);
				commentSeqVb.setFatalFlag7(regexMatcher.find()? regexMatcher.group(1) :"");
				
				regexMatcher = Pattern.compile("FATAL_FLAG_8:!#(.*?)\\$@!").matcher(commentSeqStr);
				commentSeqVb.setFatalFlag8(regexMatcher.find()? regexMatcher.group(1) :"");
				
				regexMatcher = Pattern.compile("FATAL_FLAG_9:!#(.*?)\\$@!").matcher(commentSeqStr);
				commentSeqVb.setFatalFlag9(regexMatcher.find()? regexMatcher.group(1) :"");
				
				regexMatcher = Pattern.compile("FATAL_FLAG_10:!#(.*?)\\$@!").matcher(commentSeqStr);
				commentSeqVb.setFatalFlag10(regexMatcher.find()? regexMatcher.group(1) :"");
				
				regexMatcher = Pattern.compile("LOG_TEXT_FLAG_1:!#(.*?)\\$@!").matcher(commentSeqStr);
				commentSeqVb.setLogTextFlag1(regexMatcher.find()? regexMatcher.group(1) :"");
				
				regexMatcher = Pattern.compile("LOG_TEXT_FLAG_2:!#(.*?)\\$@!").matcher(commentSeqStr);
				commentSeqVb.setLogTextFlag2(regexMatcher.find()? regexMatcher.group(1) :"");
				
				regexMatcher = Pattern.compile("LOG_TEXT_FLAG_3:!#(.*?)\\$@!").matcher(commentSeqStr);
				commentSeqVb.setLogTextFlag3(regexMatcher.find()? regexMatcher.group(1) :"");
				
				regexMatcher = Pattern.compile("LOG_TEXT_FLAG_4:!#(.*?)\\$@!").matcher(commentSeqStr);
				commentSeqVb.setLogTextFlag4(regexMatcher.find()? regexMatcher.group(1) :"");
				
				regexMatcher = Pattern.compile("LOG_TEXT_FLAG_5:!#(.*?)\\$@!").matcher(commentSeqStr);
				commentSeqVb.setLogTextFlag5(regexMatcher.find()? regexMatcher.group(1) :"");
				
				regexMatcher = Pattern.compile("LOG_TEXT_FLAG_6:!#(.*?)\\$@!").matcher(commentSeqStr);
				commentSeqVb.setLogTextFlag6(regexMatcher.find()? regexMatcher.group(1) :"");
				
				regexMatcher = Pattern.compile("LOG_TEXT_FLAG_7:!#(.*?)\\$@!").matcher(commentSeqStr);
				commentSeqVb.setLogTextFlag7(regexMatcher.find()? regexMatcher.group(1) :"");
				
				regexMatcher = Pattern.compile("LOG_TEXT_FLAG_8:!#(.*?)\\$@!").matcher(commentSeqStr);
				commentSeqVb.setLogTextFlag8(regexMatcher.find()? regexMatcher.group(1) :"");
				
				regexMatcher = Pattern.compile("LOG_TEXT_FLAG_9:!#(.*?)\\$@!").matcher(commentSeqStr);
				commentSeqVb.setLogTextFlag9(regexMatcher.find()? regexMatcher.group(1) :"");
				
				regexMatcher = Pattern.compile("LOG_TEXT_FLAG_10:!#(.*?)\\$@!").matcher(commentSeqStr);
				commentSeqVb.setLogTextFlag10(regexMatcher.find()? regexMatcher.group(1) :"");
				
				regexMatcher = Pattern.compile("DML_STATUS_1:!#(.*?)\\$@!").matcher(commentSeqStr);
				commentSeqVb.setDmlStatus1(regexMatcher.find()? regexMatcher.group(1) :"");
				
				regexMatcher = Pattern.compile("DML_STATUS_2:!#(.*?)\\$@!").matcher(commentSeqStr);
				commentSeqVb.setDmlStatus2(regexMatcher.find()? regexMatcher.group(1) :"");
				
				regexMatcher = Pattern.compile("DML_STATUS_3:!#(.*?)\\$@!").matcher(commentSeqStr);
				commentSeqVb.setDmlStatus3(regexMatcher.find()? regexMatcher.group(1) :"");
				
				regexMatcher = Pattern.compile("DML_STATUS_4:!#(.*?)\\$@!").matcher(commentSeqStr);
				commentSeqVb.setDmlStatus4(regexMatcher.find()? regexMatcher.group(1) :"");
				
				regexMatcher = Pattern.compile("DML_STATUS_5:!#(.*?)\\$@!").matcher(commentSeqStr);
				commentSeqVb.setDmlStatus5(regexMatcher.find()? regexMatcher.group(1) :"");
				
				regexMatcher = Pattern.compile("DML_STATUS_6:!#(.*?)\\$@!").matcher(commentSeqStr);
				commentSeqVb.setDmlStatus6(regexMatcher.find()? regexMatcher.group(1) :"");
				
				regexMatcher = Pattern.compile("DML_STATUS_7:!#(.*?)\\$@!").matcher(commentSeqStr);
				commentSeqVb.setDmlStatus7(regexMatcher.find()? regexMatcher.group(1) :"");
				
				regexMatcher = Pattern.compile("DML_STATUS_8:!#(.*?)\\$@!").matcher(commentSeqStr);
				commentSeqVb.setDmlStatus8(regexMatcher.find()? regexMatcher.group(1) :"");
				
				regexMatcher = Pattern.compile("DML_STATUS_9:!#(.*?)\\$@!").matcher(commentSeqStr);
				commentSeqVb.setDmlStatus9(regexMatcher.find()? regexMatcher.group(1) :"");
				
				regexMatcher = Pattern.compile("DML_STATUS_10:!#(.*?)\\$@!").matcher(commentSeqStr);
				commentSeqVb.setDmlStatus10(regexMatcher.find()? regexMatcher.group(1) :"");
				
				regexMatcher = Pattern.compile("MAKER:!#(.*?)\\$@!").matcher(commentSeqStr);
				commentSeqVb.setMaker(Integer.parseInt(regexMatcher.find()? regexMatcher.group(1) :""));
				
				regexMatcher = Pattern.compile("DATE_CREATION:!#(.*?)$").matcher(commentSeqStr);
				commentSeqVb.setDateCreation(regexMatcher.find()? regexMatcher.group(1) :"");
				
				indList.add(commentSeqVb);
			}
			return indList;
		} catch (Exception e) {
			return null;
		}
	}
	public List<TemplateNameGBMVb> getExecutionSequenceRedirect () throws DataAccessException {
				//String sql = "select distinct EXECUTION_SEQUENCE FROM RA_GENERICBUILD_MAPPINGS where country ='KE' and LE_BOOK=01";
				String sql = "select distinct EXECUTION_SEQUENCE FROM RA_GENERICBUILD_MAPPINGS";
		return getJdbcTemplate().query(sql,  getExecutionSequence());
	}
}