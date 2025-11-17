package com.vision.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.vision.vb.BusinessLineHeaderVb;
import com.vision.vb.FeesConfigHeaderVb;
import com.vision.vb.SmartSearchVb;
import com.vision.vb.TransLineChannelVb;
import com.vision.vb.TransLineGLVb;
import com.vision.vb.TransLineHeaderVb;
import com.vision.vb.TransLineSbuVb;
import com.vision.vb.VisionUsersVb;
import com.vision.wb.BusinessLineConfigWb;
import com.vision.wb.FeesConfigHeadersWb;

@Component
public class ProductLineConfigDao extends AbstractDao<TransLineHeaderVb> {
	@Autowired
	TransLinesSbuDao transLinesSbuDao;
	
	@Autowired
	TransLinesGlDao transLinesGlDao;
	
	@Autowired
	TransLinesChannelDao transLinesChannelDao;
	
	@Autowired
	CommonDao commonDao;
	@Autowired
	BusinessLineConfigDao businessLineConfigDao;
	
	@Autowired
	FeesConfigHeadersDao feesConfigHeadersDao;
	
	@Autowired
	FeesConfigHeadersWb feesConfigHeadersWb;
	@Autowired
	BusinessLineConfigWb businessLineConfigWb;
	
	@Override
	protected RowMapper getMapper(){
		RowMapper mapper = new RowMapper() {
			@Override
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				TransLineHeaderVb vObject = new TransLineHeaderVb();
				vObject.setCountry(rs.getString("COUNTRY"));
				vObject.setLeBook(rs.getString("LE_BOOK"));
				vObject.setTransLineId(rs.getString("TRANS_LINE_ID"));
				vObject.setTransLineDescription(rs.getString("TRANS_LINE_DESCRIPTION"));
				vObject.setTransLineSubType(rs.getString("TRANS_LINE_SUB_TYPE"));
				vObject.setTransLineSubTypeDesc(rs.getString("TRANS_LINE_SUB_TYPE_DESC"));
				vObject.setTransLineGrp(rs.getString("TRANS_LINE_GRP"));
				vObject.setTransLineGrpDesc(rs.getString("TRANS_LINE_GRP_DESC"));
				vObject.setExtractionFrequency(rs.getString("EXTRACTION_FREQUENCY"));
				vObject.setExtractionFrequencyDesc(rs.getString("EXTRACTION_FREQUENCY_DESC"));
				vObject.setTransLineStatus(rs.getInt("TRANS_LINE_STATUS"));
				vObject.setTransLineStatusDesc(rs.getString("TRANS_LINE_STATUS_Desc"));
				vObject.setRecordIndicator(rs.getInt("RECORD_INDICATOR"));
				vObject.setRecordIndicatorDesc(rs.getString("RECORD_INDICATOR_Desc"));
				vObject.setMaker(rs.getInt("MAKER"));
				vObject.setMakerName(rs.getString("MAKER_NAME"));
				vObject.setVerifier(rs.getInt("VERIFIER"));
				vObject.setVerifierName(rs.getString("VERIFIER_NAME"));
				vObject.setDateLastModified(rs.getString("DATE_LAST_MODIFIED"));
				vObject.setDateCreation(rs.getString("DATE_CREATION"));
				vObject.setTransLineType(rs.getString("TRANS_LINE_TYPE"));
				vObject.setTransLineTypeDesc(rs.getString("TRANS_LINE_TYPE_DESC"));
				return vObject;
			}
		};
		return mapper;
	}
	protected RowMapper getDetailMapper(){
		RowMapper mapper = new RowMapper() {
			@Override
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				TransLineHeaderVb vObject = new TransLineHeaderVb();
				vObject.setCountry(rs.getString("COUNTRY"));
				vObject.setLeBook(rs.getString("LE_BOOK"));
				vObject.setTransLineId(rs.getString("TRANS_LINE_ID"));
				
				vObject.setTransLineType(rs.getString("TRANS_LINE_TYPE"));
				vObject.setTransLineDescription(rs.getString("TRANS_LINE_DESCRIPTION"));
				vObject.setTransLineSubType(rs.getString("TRANS_LINE_SUB_TYPE"));
				vObject.setTransLineSubTypeDesc(rs.getString("TRANS_LINE_SUB_TYPE_DESC"));
				
				vObject.setTransLineGrp(rs.getString("TRANS_LINE_GRP"));
    			vObject.setTransLineGrpDesc(rs.getString("TRANS_LINE_GRP_DESC"));
				
				vObject.setExtractionFrequency(rs.getString("EXTRACTION_FREQUENCY"));
				vObject.setExtractionFrequencyDesc(rs.getString("EXTRACTION_FREQUENCY_DESC"));
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
				//vObject.setTransLineTypeDesc(rs.getString("ALPHA_SUBTAB_DESCRIPTION"));
				vObject.setTransLineTypeDesc(rs.getString("TRANS_LINE_TYPE_DESC"));
				vObject.setDepartment(rs.getString("DEPT_ID"));
				if("NA".equalsIgnoreCase(vObject.getDepartment()))
					vObject.setDepartmentDesc("Not Applicable");
				else
					vObject.setDepartmentDesc(rs.getString("DEPT_ID_DESC"));
				return vObject;
			}
		};
		return mapper;
	}
	@Override
	protected void setServiceDefaults() {
		serviceName = "TransLineConfig";
		serviceDesc = "Trans Line Config";
		tableName = "RA_MST_TRANS_LINE_HEADER";
		childTableName = "RA_MST_TRANS_LINE_HEADER";
		intCurrentUserId = CustomContextHolder.getContext().getVisionId();
	}

	@Override
	public List<TransLineHeaderVb> getQueryPopupResults(TransLineHeaderVb dObj) {
		Vector<Object> params = new Vector<Object>();
		StringBuffer strBufApprove = null;
		StringBuffer strBufPending = null;
		String strWhereNotExists = null;
		String orderBy = "";
		strBufApprove = new StringBuffer(" select distinct COUNTRY,LE_BOOK,TRANS_LINE_TYPE_DESC,TRANS_LINE_TYPE,TRANS_LINE_ID, 		"+
						"    TRANS_LINE_DESCRIPTION, TRANS_LINE_SUB_TYPE, TRANS_LINE_SUB_TYPE_DESC,                " +
						"    TRANS_LINE_GRP, TRANS_LINE_GRP_DESC, EXTRACTION_FREQUENCY, EXTRACTION_FREQUENCY_DESC, " +
						"    TRANS_LINE_STATUS, TRANS_LINE_STATUS_DESC, RECORD_INDICATOR, RECORD_INDICATOR_DESC,   " +
						"    MAKER, MAKER_NAME, VERIFIER, VERIFIER_NAME, DATE_LAST_MODIFIED,DATE_LAST_MODIFIED_1,  " +
						"    DATE_CREATION                                                                         " +
						"   from  (  SELECT  Distinct TAppr.COUNTRY, TAppr.LE_BOOK,                                            " +
						"        (SELECT  T0.ALPHA_SUBTAB_DESCRIPTION  FROM ALPHA_SUB_TAB T0                       " +
						"          WHERE t0.Alpha_tab = TAppr.TRANS_LINE_TYPE_AT                                   " +
						"            AND T0.ALPHA_SUB_TAB = TAppr.TRANS_LINE_TYPE                                  " +
						"        ) TRANS_LINE_TYPE_DESC,                                                           " +
						"        TAppr.TRANS_LINE_TYPE,                                                            " +
						"        TAppr.TRANS_LINE_ID,                                                              " +
						"        TAppr.TRANS_LINE_DESCRIPTION,                                                     " +
						"        CASE WHEN TAppr.TRANS_LINE_TYPE = 'P' THEN TAppr.TRANS_LINE_PROD_SUB_TYPE         " +
						" 		ELSE TAppr.TRANS_LINE_SERV_SUB_TYPE END TRANS_LINE_SUB_TYPE,                       " +
						"        CASE WHEN TAppr.TRANS_LINE_TYPE = 'P' THEN (                                      " +
						"          SELECT T1.ALPHA_SUBTAB_DESCRIPTION  FROM ALPHA_SUB_TAB T1                       " +
						"          WHERE t1.Alpha_tab = TAppr.TRANS_LINE_PROD_SUB_TYPE_AT                          " +
						"            AND T1.ALPHA_SUB_TAB = TAppr.TRANS_LINE_PROD_SUB_TYPE                         " +
						"        ) ELSE (                                                                          " +
						"          SELECT  T1.ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB T1                       " +
						"          WHERE t1.Alpha_tab = TAppr.TRANS_LINE_SERV_SUB_TYPE_AT                          " +
						"            AND T1.ALPHA_SUB_TAB = TAppr.TRANS_LINE_SERV_SUB_TYPE                         " +
						"        ) END TRANS_LINE_SUB_TYPE_DESC,                                                   " +
						"        CASE WHEN TAppr.TRANS_LINE_TYPE = 'P' THEN TAppr.TRANS_LINE_PROD_GRP              " +
						"		ELSE TRANS_LINE_SERV_GRP END TRANS_LINE_GRP,                                       " +
						"        CASE WHEN TAppr.TRANS_LINE_TYPE = 'P' THEN 									   " +
						"		(SELECT  T2.ALPHA_SUBTAB_DESCRIPTION  FROM ALPHA_SUB_TAB T2                        " +                                      
						"          WHERE  t2.Alpha_tab = TAppr.TRANS_LINE_PROD_GRP_AT                              " +
						"            AND T2.ALPHA_SUB_TAB = TAppr.TRANS_LINE_PROD_GRP                              " +
						"        ) ELSE (                                                                          " +
						"          SELECT T2.ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB T2 WHERE                                                                           " +
						"            t2.Alpha_tab = TAppr.TRANS_LINE_SERV_GRP_AT                                   " +
						"            AND T2.ALPHA_SUB_TAB = TAppr.TRANS_LINE_SERV_GRP ) END TRANS_LINE_GRP_DESC,                                                        " +
						"        TAppr.EXTRACTION_FREQUENCY,                                                       " +
						"        (SELECT T5.ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB T5                                                              " +
						"          WHERE t5.Alpha_tab = TAppr.EXTRACTION_FREQUENCY_AT                                  " +
						"            AND T5.ALPHA_SUB_TAB = TAppr.EXTRACTION_FREQUENCY) EXTRACTION_FREQUENCY_DESC,                                                      " +
						"        TAppr.TRANS_LINE_STATUS,T3.NUM_SUBTAB_DESCRIPTION TRANS_LINE_STATUS_DESC,                                 " +
						"        TAppr.RECORD_INDICATOR,T4.NUM_SUBTAB_DESCRIPTION RECORD_INDICATOR_DESC,                                  " +
						"        TAppr.MAKER,( SELECT MIN (USER_NAME) FROM  VISION_USERS                                                                  " +
						"          WHERE VISION_ID = "+getDbFunction("NVL", null)+" (TAppr.MAKER, 0) ) MAKER_NAME,                                                                     " +
						"        TAppr.VERIFIER,( SELECT MIN (USER_NAME) FROM VISION_USERS                                                                  " +
						"          WHERE  VISION_ID = "+getDbFunction("NVL", null)+" (TAppr.VERIFIER, 0)) VERIFIER_NAME,                "+
						"	"+dbFunctionFormats("TAPPR.DATE_LAST_MODIFIED","DATETIME_FORMAT", null)+" DATE_LAST_MODIFIED, "+
						"        TAppr.DATE_LAST_MODIFIED DATE_LAST_MODIFIED_1,                                    " +
						"	"+dbFunctionFormats("TAPPR.DATE_CREATION","DATETIME_FORMAT", null)+" DATE_CREATION, "+
						"        (SELECT BUSINESS_LINE_STATUS FROM RA_MST_BUSINESS_LINE_HEADER                                                   " +
						"          WHERE COUNTRY = TAPPR.COUNTRY  AND LE_BOOK = TAPPR.LE_BOOK                                                   " +
						"            AND TRANS_LINE_ID = TAPPR.TRANS_LINE_ID                                       " +
						"            AND BUSINESS_LINE_STATUS_NT = 0) BUSINESS_LINE_STATUS,                                                           " +
						"        BLA.BUSINESS_LINE_ID, BLA.BUSINESS_LINE_DESCRIPTION,BLA.BUSINESS_LINE_TYPE,                                                           " +
						"        ( SELECT T0.ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB T0                                                              " +
						"          WHERE t0.Alpha_tab = BLA.BUSINESS_LINE_TYPE_AT                                      " +
						"            AND T0.ALPHA_SUB_TAB = BLA.BUSINESS_LINE_TYPE) BL_TYPE_DESC,                                                                   " +
						"        BLA.IE_TYPE,( SELECT  T0.ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB T0                                                              " +
						"          WHERE t0.Alpha_tab = BLA.IE_TYPE_AT                                                 " +
						"            AND T0.ALPHA_SUB_TAB = BLA.IE_TYPE ) BL_IE_TYPE_DESC,                                                                " +
						"        (SELECT T0.NUM_SUBTAB_DESCRIPTION FROM  NUM_SUB_TAB T0                                                                " +
						"          WHERE t0.num_tab = 0 AND T0.NUM_SUB_TAB = BLA.BUSINESS_LINE_STATUS ) BL_STATUS_DESC,                                                                 " +
						"        ( SELECT T0.NUM_SUBTAB_DESCRIPTION FROM NUM_SUB_TAB T0                                                                " +
						"          WHERE t0.num_tab = 7 AND T0.NUM_SUB_TAB = BLA.RECORD_INDICATOR) BL_RECORD_INDICATOR_DESC,                                                       " +
						"        FLA.FEE_TYPE,( SELECT T0.ALPHA_SUBTAB_DESCRIPTION FROM  ALPHA_SUB_TAB T0                                                              " +
						"          WHERE t0.Alpha_tab = FLA.FEE_TYPE_AT                                                " +
						"            AND T0.ALPHA_SUB_TAB = FLA.FEE_TYPE ) FEE_TYPE_DESC,                                                                  " +
						"        FLA.FEE_BASIS,(  SELECT T0.ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB T0                                                              " +
						"          WHERE t0.Alpha_tab = FLA.FEE_BASIS_AT                                               " +
						"            AND T0.ALPHA_SUB_TAB = FLA.FEE_BASIS ) FEE_BASIS_DESC,                                                                 " +
						"        FLA.TIER_TYPE,( SELECT T0.ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB T0                                                              " +
						"          WHERE t0.Alpha_tab = FLA.TIER_TYPE_AT                                               " +
						"            AND T0.ALPHA_SUB_TAB = FLA.TIER_TYPE ) TIER_TYPE_DESC,                                                                 " +
						"        (SELECT T0.NUM_SUBTAB_DESCRIPTION FROM NUM_SUB_TAB T0                                                                " +
						"          WHERE  t0.num_tab = 0  AND T0.NUM_SUB_TAB = FLA.FEE_LINE_STATUS) FL_STATUS_DESC,                                                                 " +
						"        (SELECT T0.NUM_SUBTAB_DESCRIPTION FROM NUM_SUB_TAB T0                                                                " +
						"          WHERE t0.num_tab = 7 AND T0.NUM_SUB_TAB = FLA.RECORD_INDICATOR) FL_RECORD_INDICATOR_DESC                                                        " +
						"      FROM RA_MST_TRANS_LINE_HEADER TAppr                                                    " +
						"        LEFT OUTER JOIN ( Select Country,Le_Book,TRANS_LINE_ID, BUSINESS_LINE_ID,                         " +                                    
						"            BUSINESS_LINE_DESCRIPTION,BUSINESS_LINE_TYPE, BUSINESS_LINE_TYPE_AT,          " +                                              
						"            IE_TYPE,IE_TYPE_AT,BUSINESS_LINE_STATUS,BUSINESS_LINE_STATUS_NT,              " +                                        
						"            RECORD_INDICATOR from RA_MST_BUSINESS_LINE_HEADER    Union all                                                                       " +
						"          Select Country,Le_Book,TRANS_LINE_ID, BUSINESS_LINE_ID,                         " +                                    
						"            BUSINESS_LINE_DESCRIPTION,BUSINESS_LINE_TYPE, BUSINESS_LINE_TYPE_AT,          " +                                              
						"            IE_TYPE,IE_TYPE_AT,BUSINESS_LINE_STATUS,BUSINESS_LINE_STATUS_NT,              " +                                       
						"            RECORD_INDICATOR from RA_MST_BUSINESS_LINE_HEADER_PEND )BLA				   " +
						"        ON ( TAppr.Country = BLA.Country                                                     " +
						"          AND TAppr.LE_BOOK = BLA.LE_BOOK                                                 " +
						"          AND TAppr.TRANS_LINE_ID = BLA.TRANS_LINE_ID )                                    " +
						"        LEFT OUTER JOIN ( select  COUNTRY,LE_BOOK, BUSINESS_LINE_ID, FEE_BASIS_AT,        " +                                       
						"            FEE_BASIS,FEE_TYPE_AT,FEE_TYPE,TIER_TYPE_AT, TIER_TYPE,                       " +                                              
						"            FEE_LINE_STATUS,RECORD_INDICATOR                                              " +                  
						"          FROM   RA_MST_FEES_HEADER  UNION ALL                                            " +                             
						"          select  COUNTRY,LE_BOOK, BUSINESS_LINE_ID, FEE_BASIS_AT,                        " +                                           
						"            FEE_BASIS,FEE_TYPE_AT,FEE_TYPE,TIER_TYPE_AT, TIER_TYPE,                       " +                                              
						"            FEE_LINE_STATUS,RECORD_INDICATOR  FROM   RA_MST_FEES_HEADER_PEND) FLA                                             " +  
						"       ON (BLA.Country = FLA.Country                                                       " +
						"          AND BLA.LE_BOOK = FLA.LE_BOOK                                                   " +
						"          AND BLA.BUSINESS_LINE_ID = FLA.BUSINESS_LINE_ID   )                              " +
						"        JOIN NUM_SUB_TAB T3 ON (                                                          " +
						"          t3.NUM_tab = TAppr.TRANS_LINE_STATUS_NT                                         " +
						"          AND t3.NUM_sub_tab = TAppr.TRANS_LINE_STATUS)                                                                                 " +
						"        JOIN NUM_SUB_TAB T4 ON (                                                          " +
						"          t4.NUM_tab = TAppr.RECORD_INDICATOR_NT                                          " +
						"          AND t4.NUM_sub_tab = TAppr.RECORD_INDICATOR)  ) TAPPR                                                                               ");
		
		strWhereNotExists = new String(" Not Exists (Select 'X' From RA_MST_TRANS_LINE_HEADER_PEND TPEND WHERE TAppr.COUNTRY = TPend.COUNTRY"
						+ " AND TAppr.LE_BOOK = TPend.LE_BOOK AND TAppr.TRANS_LINE_ID = TPend.TRANS_LINE_ID)");
		
		strBufPending = new StringBuffer(" select distinct COUNTRY,LE_BOOK,TRANS_LINE_TYPE_DESC,TRANS_LINE_TYPE,TRANS_LINE_ID, 		"+
				"    TRANS_LINE_DESCRIPTION, TRANS_LINE_SUB_TYPE, TRANS_LINE_SUB_TYPE_DESC,                " +
				"    TRANS_LINE_GRP, TRANS_LINE_GRP_DESC, EXTRACTION_FREQUENCY, EXTRACTION_FREQUENCY_DESC, " +
				"    TRANS_LINE_STATUS, TRANS_LINE_STATUS_DESC, RECORD_INDICATOR, RECORD_INDICATOR_DESC,   " +
				"    MAKER, MAKER_NAME, VERIFIER, VERIFIER_NAME, DATE_LAST_MODIFIED,DATE_LAST_MODIFIED_1,  " +
				"    DATE_CREATION                                                                         " +
				"   from  (  SELECT  Distinct TPEND.COUNTRY, TPEND.LE_BOOK,                                            " +
				"        (SELECT  T0.ALPHA_SUBTAB_DESCRIPTION  FROM ALPHA_SUB_TAB T0                       " +
				"          WHERE t0.Alpha_tab = TPEND.TRANS_LINE_TYPE_AT                                   " +
				"            AND T0.ALPHA_SUB_TAB = TPEND.TRANS_LINE_TYPE                                  " +
				"        ) TRANS_LINE_TYPE_DESC,                                                           " +
				"        TPEND.TRANS_LINE_TYPE,                                                            " +
				"        TPEND.TRANS_LINE_ID,                                                              " +
				"        TPEND.TRANS_LINE_DESCRIPTION,                                                     " +
				"        CASE WHEN TPEND.TRANS_LINE_TYPE = 'P' THEN TPEND.TRANS_LINE_PROD_SUB_TYPE         " +
				" 		ELSE TPEND.TRANS_LINE_SERV_SUB_TYPE END TRANS_LINE_SUB_TYPE,                       " +
				"        CASE WHEN TPEND.TRANS_LINE_TYPE = 'P' THEN (                                      " +
				"          SELECT T1.ALPHA_SUBTAB_DESCRIPTION  FROM ALPHA_SUB_TAB T1                       " +
				"          WHERE t1.Alpha_tab = TPEND.TRANS_LINE_PROD_SUB_TYPE_AT                          " +
				"            AND T1.ALPHA_SUB_TAB = TPEND.TRANS_LINE_PROD_SUB_TYPE                         " +
				"        ) ELSE (                                                                          " +
				"          SELECT  T1.ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB T1                       " +
				"          WHERE t1.Alpha_tab = TPEND.TRANS_LINE_SERV_SUB_TYPE_AT                          " +
				"            AND T1.ALPHA_SUB_TAB = TPEND.TRANS_LINE_SERV_SUB_TYPE                         " +
				"        ) END TRANS_LINE_SUB_TYPE_DESC,                                                   " +
				"        CASE WHEN TPEND.TRANS_LINE_TYPE = 'P' THEN TPEND.TRANS_LINE_PROD_GRP              " +
				"		ELSE TRANS_LINE_SERV_GRP END TRANS_LINE_GRP,                                       " +
				"        CASE WHEN TPEND.TRANS_LINE_TYPE = 'P' THEN 									   " +
				"		(SELECT  T2.ALPHA_SUBTAB_DESCRIPTION  FROM ALPHA_SUB_TAB T2                        " +                                      
				"          WHERE  t2.Alpha_tab = TPEND.TRANS_LINE_PROD_GRP_AT                              " +
				"            AND T2.ALPHA_SUB_TAB = TPEND.TRANS_LINE_PROD_GRP                              " +
				"        ) ELSE (                                                                          " +
				"          SELECT T2.ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB T2 WHERE                                                                           " +
				"            t2.Alpha_tab = TPEND.TRANS_LINE_SERV_GRP_AT                                   " +
				"            AND T2.ALPHA_SUB_TAB = TPEND.TRANS_LINE_SERV_GRP ) END TRANS_LINE_GRP_DESC,                                                        " +
				"        TPEND.EXTRACTION_FREQUENCY,                                                       " +
				"        (SELECT T5.ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB T5                                                              " +
				"          WHERE t5.Alpha_tab = TPEND.EXTRACTION_FREQUENCY_AT                                  " +
				"            AND T5.ALPHA_SUB_TAB = TPEND.EXTRACTION_FREQUENCY) EXTRACTION_FREQUENCY_DESC,                                                      " +
				"        TPEND.TRANS_LINE_STATUS,T3.NUM_SUBTAB_DESCRIPTION TRANS_LINE_STATUS_DESC,                                 " +
				"        TPEND.RECORD_INDICATOR,T4.NUM_SUBTAB_DESCRIPTION RECORD_INDICATOR_DESC,                                  " +
				"        TPEND.MAKER,( SELECT MIN (USER_NAME) FROM  VISION_USERS                                                                  " +
				"          WHERE VISION_ID = "+getDbFunction("NVL", null)+" (TPEND.MAKER, 0) ) MAKER_NAME,                                                                     " +
				"        TPEND.VERIFIER,( SELECT MIN (USER_NAME) FROM VISION_USERS                                                                  " +
				"          WHERE  VISION_ID = "+getDbFunction("NVL", null)+" (TPEND.VERIFIER, 0)) VERIFIER_NAME,                                                                  " +
				"        "+getDbFunction("DATEFUNC", null)+" (TPEND.DATE_LAST_MODIFIED, '"+getDbFunction("DD_Mon_RRRR", null)+" "+getDbFunction("TIME", null)+"') DATE_LAST_MODIFIED,                                                             " +
				"        TPEND.DATE_LAST_MODIFIED DATE_LAST_MODIFIED_1,                                    " +
				"        "+getDbFunction("DATEFUNC", null)+" (TPEND.DATE_CREATION, '"+getDbFunction("DD_Mon_RRRR", null)+" "+getDbFunction("TIME", null)+"') DATE_CREATION,                                                                  " +
				"        (SELECT BUSINESS_LINE_STATUS FROM RA_MST_BUSINESS_LINE_HEADER                                                   " +
				"          WHERE COUNTRY = TPEND.COUNTRY  AND LE_BOOK = TPEND.LE_BOOK                                                   " +
				"            AND TRANS_LINE_ID = TPEND.TRANS_LINE_ID                                       " +
				"            AND BUSINESS_LINE_STATUS_NT = 0) BUSINESS_LINE_STATUS,                                                           " +
				"        BLP.BUSINESS_LINE_ID, BLP.BUSINESS_LINE_DESCRIPTION,BLP.BUSINESS_LINE_TYPE,                                                           " +
				"        ( SELECT T0.ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB T0                                                              " +
				"          WHERE t0.Alpha_tab = BLP.BUSINESS_LINE_TYPE_AT                                      " +
				"            AND T0.ALPHA_SUB_TAB = BLP.BUSINESS_LINE_TYPE) BL_TYPE_DESC,                                                                   " +
				"        BLP.IE_TYPE,( SELECT  T0.ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB T0                                                              " +
				"          WHERE t0.Alpha_tab = BLP.IE_TYPE_AT                                                 " +
				"            AND T0.ALPHA_SUB_TAB = BLP.IE_TYPE ) BL_IE_TYPE_DESC,                                                                " +
				"        (SELECT T0.NUM_SUBTAB_DESCRIPTION FROM  NUM_SUB_TAB T0                                                                " +
				"          WHERE t0.num_tab = 0 AND T0.NUM_SUB_TAB = BLP.BUSINESS_LINE_STATUS ) BL_STATUS_DESC,                                                                 " +
				"        ( SELECT T0.NUM_SUBTAB_DESCRIPTION FROM NUM_SUB_TAB T0                                                                " +
				"          WHERE t0.num_tab = 7 AND T0.NUM_SUB_TAB = BLP.RECORD_INDICATOR) BL_RECORD_INDICATOR_DESC,                                                       " +
				"        FLP.FEE_TYPE,( SELECT T0.ALPHA_SUBTAB_DESCRIPTION FROM  ALPHA_SUB_TAB T0                                                              " +
				"          WHERE t0.Alpha_tab = FLP.FEE_TYPE_AT                                                " +
				"            AND T0.ALPHA_SUB_TAB = FLP.FEE_TYPE ) FEE_TYPE_DESC,                                                                  " +
				"        FLP.FEE_BASIS,(  SELECT T0.ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB T0                                                              " +
				"          WHERE t0.Alpha_tab = FLP.FEE_BASIS_AT                                               " +
				"            AND T0.ALPHA_SUB_TAB = FLP.FEE_BASIS ) FEE_BASIS_DESC,                                                                 " +
				"        FLP.TIER_TYPE,( SELECT T0.ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB T0                                                              " +
				"          WHERE t0.Alpha_tab = FLP.TIER_TYPE_AT                                               " +
				"            AND T0.ALPHA_SUB_TAB = FLP.TIER_TYPE ) TIER_TYPE_DESC,                                                                 " +
				"        (SELECT T0.NUM_SUBTAB_DESCRIPTION FROM NUM_SUB_TAB T0                                                                " +
				"          WHERE  t0.num_tab = 0  AND T0.NUM_SUB_TAB = FLP.FEE_LINE_STATUS) FL_STATUS_DESC,                                                                 " +
				"        (SELECT T0.NUM_SUBTAB_DESCRIPTION FROM NUM_SUB_TAB T0                                                                " +
				"          WHERE t0.num_tab = 7 AND T0.NUM_SUB_TAB = FLP.RECORD_INDICATOR) FL_RECORD_INDICATOR_DESC                                                        " +
				"      FROM RA_MST_TRANS_LINE_HEADER_PEND TPEND                                                    " +
				"        LEFT OUTER JOIN ( Select Country,Le_Book,TRANS_LINE_ID, BUSINESS_LINE_ID,                         " +                                    
				"            BUSINESS_LINE_DESCRIPTION,BUSINESS_LINE_TYPE, BUSINESS_LINE_TYPE_AT,          " +                                              
				"            IE_TYPE,IE_TYPE_AT,BUSINESS_LINE_STATUS,BUSINESS_LINE_STATUS_NT,              " +                                        
				"            RECORD_INDICATOR from RA_MST_BUSINESS_LINE_HEADER    Union all                                                                       " +
				"          Select Country,Le_Book,TRANS_LINE_ID, BUSINESS_LINE_ID,                         " +                                    
				"            BUSINESS_LINE_DESCRIPTION,BUSINESS_LINE_TYPE, BUSINESS_LINE_TYPE_AT,          " +                                              
				"            IE_TYPE,IE_TYPE_AT,BUSINESS_LINE_STATUS,BUSINESS_LINE_STATUS_NT,              " +                                       
				"            RECORD_INDICATOR from RA_MST_BUSINESS_LINE_HEADER_PEND )BLP				   " +
				"        ON ( TPEND.Country = BLP.Country                                                     " +
				"          AND TPEND.LE_BOOK = BLP.LE_BOOK                                                 " +
				"          AND TPEND.TRANS_LINE_ID = BLP.TRANS_LINE_ID )                                    " +
				"        LEFT OUTER JOIN ( select  COUNTRY,LE_BOOK, BUSINESS_LINE_ID, FEE_BASIS_AT,        " +                                       
				"            FEE_BASIS,FEE_TYPE_AT,FEE_TYPE,TIER_TYPE_AT, TIER_TYPE,                       " +                                              
				"            FEE_LINE_STATUS,RECORD_INDICATOR                                              " +                  
				"          FROM   RA_MST_FEES_HEADER  UNION ALL                                            " +                             
				"          select  COUNTRY,LE_BOOK, BUSINESS_LINE_ID, FEE_BASIS_AT,                        " +                                           
				"            FEE_BASIS,FEE_TYPE_AT,FEE_TYPE,TIER_TYPE_AT, TIER_TYPE,                       " +                                              
				"            FEE_LINE_STATUS,RECORD_INDICATOR  FROM   RA_MST_FEES_HEADER_PEND) FLP                                             " +  
				"       ON (BLP.Country = FLP.Country                                                       " +
				"          AND BLP.LE_BOOK = FLP.LE_BOOK                                                   " +
				"          AND BLP.BUSINESS_LINE_ID = FLP.BUSINESS_LINE_ID   )                              " +
				"        JOIN NUM_SUB_TAB T3 ON (                                                          " +
				"          t3.NUM_tab = TPEND.TRANS_LINE_STATUS_NT                                         " +
				"          AND t3.NUM_sub_tab = TPEND.TRANS_LINE_STATUS)                                                                                 " +
				"        JOIN NUM_SUB_TAB T4 ON (                                                          " +
				"          t4.NUM_tab = TPEND.RECORD_INDICATOR_NT                                          " +
				"          AND t4.NUM_sub_tab = TPEND.RECORD_INDICATOR)  ) TPEND                           ");   
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
						case "leBook":
							CommonUtils.addToQuerySearch(" ( upper(TAPPR.LE_BOOK) "+ val+ "OR upper(TAPPR.COUNTRY) "+ val+" )", strBufApprove, data.getJoinType());
							CommonUtils.addToQuerySearch(" ( upper(TPend.LE_BOOK) "+ val+ "OR upper(TPend.COUNTRY) "+ val+" )", strBufPending, data.getJoinType());
							break;
							
						case "transLineType":
							CommonUtils.addToQuerySearch(" upper(TAPPR.TRANS_LINE_TYPE) " + val, strBufApprove,
									data.getJoinType());
							CommonUtils.addToQuerySearch(" upper(TPend.TRANS_LINE_TYPE) " + val, strBufPending,
									data.getJoinType());
							break;
	
						case "transLineTypeDesc":
							CommonUtils.addToQuerySearch(" upper(TAPPR.TRANS_LINE_TYPE_DESC) " + val, strBufApprove,
									data.getJoinType());
							CommonUtils.addToQuerySearch(" upper(TPend.TRANS_LINE_TYPE_DESC) " + val, strBufPending,
									data.getJoinType());
							break;
						case "transLineId":
							CommonUtils.addToQuerySearch(" (upper(TAPPR.TRANS_LINE_ID) "+ val+" OR upper(TAPPR.TRANS_LINE_DESCRIPTION) "+ val+")", strBufApprove, data.getJoinType());
							CommonUtils.addToQuerySearch(" (upper(TPend.TRANS_LINE_ID) "+ val+" OR upper(TPend.TRANS_LINE_DESCRIPTION) "+ val+")", strBufPending, data.getJoinType());
							break;
	
						case "transLineDescription":
							CommonUtils.addToQuerySearch(" upper(TAPPR.TRANS_LINE_DESCRIPTION) "+ val, strBufApprove, data.getJoinType());
							CommonUtils.addToQuerySearch(" upper(TPend.TRANS_LINE_DESCRIPTION) "+ val, strBufPending, data.getJoinType());
							break;
	
						case "transLineProdSubTypeDesc":
							CommonUtils.addToQuerySearch(" upper(TAPPR.TRANS_LINE_PROD_SUB_TYPE_DESC) "+ val, strBufApprove, data.getJoinType());
							CommonUtils.addToQuerySearch(" upper(TPend.TRANS_LINE_PROD_SUB_TYPE_DESC) "+ val, strBufPending, data.getJoinType());
							break;
							
						case "transLineGrpDesc":
							if("P".equalsIgnoreCase(dObj.getTransLineType())) {
								CommonUtils.addToQuerySearch(" upper(TAPPR.TRANS_LINE_GRP_DESC) "+ val, strBufApprove, data.getJoinType());
								CommonUtils.addToQuerySearch(" upper(TPend.TRANS_LINE_GRP_DESC) "+ val, strBufPending, data.getJoinType());
								break;
							} else {
								CommonUtils.addToQuerySearch(" upper(TAPPR.TRANS_LINE_GRP_DESC) "+ val, strBufApprove, data.getJoinType());
								CommonUtils.addToQuerySearch(" upper(TPend.TRANS_LINE_GRP_DESC) "+ val, strBufPending, data.getJoinType());
								break;
							}
							
						case "extractionFrequencyDesc":
							CommonUtils.addToQuerySearch(" upper(TAPPR.EXTRACTION_FREQUENCY_DESC) "+ val, strBufApprove, data.getJoinType());
							CommonUtils.addToQuerySearch(" upper(TPend.EXTRACTION_FREQUENCY_DESC) "+ val, strBufPending, data.getJoinType());
							break;
							
						case "transLineStatusDesc":
							CommonUtils.addToQuerySearch(" upper(TAPPR.TRANS_LINE_STATUS_DESC) "+ val, strBufApprove, data.getJoinType());
							CommonUtils.addToQuerySearch(" upper(TPend.TRANS_LINE_STATUS_DESC) "+ val, strBufPending, data.getJoinType());
							break;
							
						case "transLineRecordIndicatorDesc":
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
							
						case "businessLineId":
							CommonUtils.addToQuerySearch(" (upper(TAPPR.BUSINESS_LINE_ID) "+ val+ " OR upper(TAPPR.BUSINESS_LINE_DESCRIPTION)"+ val+")", strBufApprove, data.getJoinType());
							CommonUtils.addToQuerySearch(" (upper(TPend.BUSINESS_LINE_ID) "+ val+ " OR upper(TPend.BUSINESS_LINE_DESCRIPTION)"+ val+")", strBufPending, data.getJoinType());
							break;

						case "businessLineDescription":
							CommonUtils.addToQuerySearch(" upper(TAPPR.BUSINESS_LINE_DESCRIPTION) "+ val, strBufApprove, data.getJoinType());
							CommonUtils.addToQuerySearch(" upper(TPend.BUSINESS_LINE_DESCRIPTION) "+ val, strBufPending, data.getJoinType());
							break;
							
						case "businessLineTypeDesc":
							CommonUtils.addToQuerySearch(" upper(TAPPR.BL_TYPE_DESC) "+ val, strBufApprove, data.getJoinType());
							CommonUtils.addToQuerySearch(" upper(TPend.BL_TYPE_DESC) "+ val, strBufPending, data.getJoinType());
							break;	
							
						case "IncomeExpenseTypeDesc":
							CommonUtils.addToQuerySearch(" upper(TAPPR.BL_IE_TYPE_DESC) "+ val, strBufApprove, data.getJoinType());
							CommonUtils.addToQuerySearch(" upper(TPend.BL_IE_TYPE_DESC) "+ val, strBufPending, data.getJoinType());
							break;	
							
						case "businessLineStatusDesc":
							CommonUtils.addToQuerySearch(" upper(TAPPR.BL_STATUS_DESC) "+ val, strBufApprove, data.getJoinType());
							CommonUtils.addToQuerySearch(" upper(TPend.BL_STATUS_DESC) "+ val, strBufPending, data.getJoinType());
							break;
							
						case "businessLineRecordIndicatorDesc":
							CommonUtils.addToQuerySearch(" upper(TAPPR.BL_RECORD_INDICATOR_DESC) "+ val, strBufApprove, data.getJoinType());
							CommonUtils.addToQuerySearch(" upper(TPend.BL_RECORD_INDICATOR_DESC) "+ val, strBufPending, data.getJoinType());
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
							
						case "feeConfigStatusDesc":
							CommonUtils.addToQuerySearch(" upper(TAPPR.FL_STATUS_DESC) "+ val, strBufApprove, data.getJoinType());
							CommonUtils.addToQuerySearch(" upper(TPend.FL_STATUS_DESC) "+ val, strBufPending, data.getJoinType());
							break;
							
						case "feeConfigRecordIndicatorDesc":
							CommonUtils.addToQuerySearch(" upper(TAPPR.FL_RECORD_INDICATOR_DESC) "+ val, strBufApprove, data.getJoinType());
							CommonUtils.addToQuerySearch(" upper(TPend.FL_RECORD_INDICATOR_DESC) "+ val, strBufPending, data.getJoinType());
							break;	
							
						default:
					}
					count++;
				}
			}
			VisionUsersVb visionUsersVb = CustomContextHolder.getContext();
			visionUsersVb = commonDao.getRestrictionInfo(visionUsersVb);
			if(("Y".equalsIgnoreCase(visionUsersVb.getUpdateRestriction()))){
				if(ValidationUtil.isValid(visionUsersVb.getCountry())){
					CommonUtils.addToQuery(" COUNTRY"+getDbFunction("PIPELINE", "")+"'-'"+getDbFunction("PIPELINE", "")+"LE_BOOK IN ("+visionUsersVb.getCountry()+") ", strBufApprove);
					CommonUtils.addToQuery(" COUNTRY"+getDbFunction("PIPELINE", "")+"'-'"+getDbFunction("PIPELINE", "")+"LE_BOOK IN ("+visionUsersVb.getCountry()+") ", strBufPending);
				}
				/*if(ValidationUtil.isValid(visionUsersVb.getLeBook())){
					CommonUtils.addToQuery(" LE_BOOK IN ('"+visionUsersVb.getLeBook()+"') ", strBufApprove);
					CommonUtils.addToQuery(" LE_BOOK IN ('"+visionUsersVb.getLeBook()+"') ", strBufPending);
				}*/
				if(ValidationUtil.isValid(visionUsersVb.getClebTrasnBusline())){
					String userSao = commonDao.getUserSoc(visionUsersVb.getClebTrasnBusline());
					CommonUtils.addToQuery(" TRANS_LINE_ID IN ("+userSao+") ", strBufApprove);
					CommonUtils.addToQuery(" TRANS_LINE_ID IN ("+userSao+") ", strBufPending);
				}
			}
			orderBy="  Order by DATE_LAST_MODIFIED_1 DESC,TRANS_LINE_ID ";
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
	public List<TransLineHeaderVb> getQueryResults(TransLineHeaderVb dObj, int intStatus){
		List<TransLineHeaderVb> collTemp = null;
		final int intKeyFieldsCount = 3;
		setServiceDefaults();
		String strQueryAppr = null;
		String strQueryPend = null;
		strQueryAppr = new String(
				"SELECT TAppr.COUNTRY,  TAppr.LE_BOOK, TAppr.TRANS_LINE_ID,TAppr.TRANS_LINE_DESCRIPTION,A1.ALPHA_SUBTAB_DESCRIPTION TRANS_LINE_TYPE_DESC,TAppr.TRANS_LINE_TYPE, "
				+ " case when TAppr.TRANS_LINE_TYPE = 'P' THEN TAppr.TRANS_LINE_PROD_SUB_TYPE ELSE TAppr.TRANS_LINE_SERV_SUB_TYPE END TRANS_LINE_SUB_TYPE,"
				+ " case when TAppr.TRANS_LINE_TYPE = 'P' THEN "
				+ " (select T1.ALPHA_SUBTAB_DESCRIPTION from ALPHA_SUB_TAB T1 where t1.Alpha_tab = TAppr.TRANS_LINE_PROD_SUB_TYPE_AT and T1.ALPHA_SUB_TAB=TAppr.TRANS_LINE_PROD_SUB_TYPE) "
				+ " 	ELSE "
				+ " (select T1.ALPHA_SUBTAB_DESCRIPTION from ALPHA_SUB_TAB T1 where t1.Alpha_tab = TAppr.TRANS_LINE_SERV_SUB_TYPE_AT and T1.ALPHA_SUB_TAB=TAppr.TRANS_LINE_SERV_SUB_TYPE) "
				+ " END TRANS_LINE_SUB_TYPE_DESC,"
				+ " case when TAppr.TRANS_LINE_TYPE = 'P' THEN TAppr.TRANS_LINE_PROD_GRP ELSE TAppr.TRANS_LINE_SERV_GRP END TRANS_LINE_GRP,"
				+ "  case when TAppr.TRANS_LINE_TYPE = 'P' THEN"
				+ " (select T2.ALPHA_SUBTAB_DESCRIPTION from ALPHA_SUB_TAB T2 "
				+ " where t2.Alpha_tab = TAppr.TRANS_LINE_PROD_GRP_AT "
				+ " and T2.ALPHA_SUB_TAB=TAppr.TRANS_LINE_PROD_GRP) "
				+ " ELSE "
				+ " (select T2.ALPHA_SUBTAB_DESCRIPTION from ALPHA_SUB_TAB T2 "
				+ " where t2.Alpha_tab = TAppr.TRANS_LINE_SERV_GRP_AT "
				+ " and T2.ALPHA_SUB_TAB=TAppr.TRANS_LINE_SERV_GRP) END "
				+ " TRANS_LINE_GRP_DESC, "
				+ "TAppr.EXTRACTION_FREQUENCY,(SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB=7015 and alpha_sub_tab = TAppr.EXTRACTION_FREQUENCY)EXTRACTION_FREQUENCY_DESC ,"
				+ "EXTRACTION_MONTH_DAY, " 
				+ "TARGET_STG_TABLE_ID,TAppr.DEPT_ID,(SELECT ALPHA_SUBTAB_DESCRIPTION  description  FROM ALPHA_SUB_TAB WHERE ALPHA_TAB  = 7041 AND ALPHA_SUBTAB_STATUS = 0 AND ALPHA_SUB_TAB = DEPT_ID)DEPT_ID_DESC,"
				+ "TAppr.TRANS_LINE_STATUS,TAppr.RECORD_INDICATOR,T1.NUM_SUBTAB_DESCRIPTION RECORD_INDICATOR_DESC, " + 
				"TAppr. MAKER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "+getDbFunction("NVL")+"(TAppr.MAKER,0) ) MAKER_NAME, "+
				"TAppr. VERIFIER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "+getDbFunction("NVL")+"(TAppr.VERIFIER,0) ) VERIFIER_NAME, "+
				" "+getDbFunction("DATEFUNC")+"(TAppr.DATE_LAST_MODIFIED, '"+getDbFunction("DD_Mon_RRRR")+" "+getDbFunction("TIME")+"') DATE_LAST_MODIFIED ,"+getDbFunction("DATEFUNC")+"(TAppr.DATE_CREATION, '"+getDbFunction("DD_Mon_RRRR")+" "+getDbFunction("TIME")+"') DATE_CREATION "+ 
				" FROM RA_MST_TRANS_LINE_HEADER TAppr, NUM_SUB_TAB T1,ALPHA_SUB_TAB A1 WHERE " +  
				"TAppr.COUNTRY =? AND TAPPR.LE_BOOK =? AND TAppr.TRANS_LINE_ID = ? AND  T1.NUM_tab = TAppr.RECORD_INDICATOR_NT"
				+ " and T1.NUM_sub_tab = TAppr.RECORD_INDICATOR AND TAppr.TRANS_LINE_TYPE=A1.ALPHA_SUB_TAB  and A1.alpha_tab=TAppr.TRANS_LINE_TYPE_AT");
		strQueryPend = new String(
				"SELECT TPend.COUNTRY, TPend.LE_BOOK, TPend.TRANS_LINE_ID,TPend.TRANS_LINE_DESCRIPTION,A1.ALPHA_SUBTAB_DESCRIPTION TRANS_LINE_TYPE_DESC,TPend.TRANS_LINE_TYPE,  "
				+ " case when TRANS_LINE_TYPE = 'P' THEN TPend.TRANS_LINE_PROD_SUB_TYPE ELSE TPend.TRANS_LINE_SERV_SUB_TYPE END TRANS_LINE_SUB_TYPE,"
				+ " case when TPend.TRANS_LINE_TYPE = 'P' THEN "
				+ " (select T1.ALPHA_SUBTAB_DESCRIPTION from ALPHA_SUB_TAB T1 where t1.Alpha_tab = TPend.TRANS_LINE_PROD_SUB_TYPE_AT and T1.ALPHA_SUB_TAB=TPend.TRANS_LINE_PROD_SUB_TYPE) "
				+ " 	ELSE "
				+ " (select T1.ALPHA_SUBTAB_DESCRIPTION from ALPHA_SUB_TAB T1 where t1.Alpha_tab = TPend.TRANS_LINE_SERV_SUB_TYPE_AT and T1.ALPHA_SUB_TAB=TPend.TRANS_LINE_SERV_SUB_TYPE) "
				+ " END TRANS_LINE_SUB_TYPE_DESC,"
				+ " case when TRANS_LINE_TYPE = 'P' THEN TPend.TRANS_LINE_PROD_GRP ELSE TPend.TRANS_LINE_SERV_GRP END TRANS_LINE_GRP,"
				+ "  case when TPend.TRANS_LINE_TYPE = 'P' THEN"
				+ " (select T2.ALPHA_SUBTAB_DESCRIPTION from ALPHA_SUB_TAB T2 "
				+ " where t2.Alpha_tab = TPend.TRANS_LINE_PROD_GRP_AT "
				+ " and T2.ALPHA_SUB_TAB=TPend.TRANS_LINE_PROD_GRP) "
				+ " ELSE "
				+ " (select T2.ALPHA_SUBTAB_DESCRIPTION from ALPHA_SUB_TAB T2 "
				+ " where t2.Alpha_tab = TPend.TRANS_LINE_SERV_GRP_AT "
				+ " and T2.ALPHA_SUB_TAB=TPend.TRANS_LINE_SERV_GRP) END "
				+ " TRANS_LINE_GRP_DESC, "
				+ " TPend.EXTRACTION_FREQUENCY,(SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB=7015 and alpha_sub_tab = TPend.EXTRACTION_FREQUENCY) EXTRACTION_FREQUENCY_DESC ,"
				+ " EXTRACTION_MONTH_DAY, "  
				+"TARGET_STG_TABLE_ID,TPend.DEPT_ID,(SELECT ALPHA_SUBTAB_DESCRIPTION  description  FROM ALPHA_SUB_TAB WHERE ALPHA_TAB  = 7041 AND ALPHA_SUBTAB_STATUS = 0 AND ALPHA_SUB_TAB = DEPT_ID)DEPT_ID_DESC,"
				+ "TPend.TRANS_LINE_STATUS,TPend.RECORD_INDICATOR,T1.NUM_SUBTAB_DESCRIPTION RECORD_INDICATOR_DESC, " + 
				"TPend. MAKER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "+getDbFunction("NVL")+"(TPend.MAKER,0) ) MAKER_NAME," +
				"TPend. VERIFIER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "+getDbFunction("NVL")+"(TPend.VERIFIER,0) ) VERIFIER_NAME, "+
				" "+getDbFunction("DATEFUNC")+"(TPend.DATE_LAST_MODIFIED, '"+getDbFunction("DD_Mon_RRRR")+" "+getDbFunction("TIME")+"') DATE_LAST_MODIFIED ,"+getDbFunction("DATEFUNC")+"(TPend.DATE_CREATION, '"+getDbFunction("DD_Mon_RRRR")+" "+getDbFunction("TIME")+"') DATE_CREATION "+ 
				" FROM RA_MST_TRANS_LINE_HEADER_PEND TPend, NUM_SUB_TAB T1,ALPHA_SUB_TAB A1 WHERE "+
				"TPend.COUNTRY =? AND TPend.LE_BOOK =? AND TPend.TRANS_LINE_ID = ? AND  T1.NUM_tab = TPend.RECORD_INDICATOR_NT"
						+ " and T1.NUM_sub_tab = TPend.RECORD_INDICATOR and TPend.TRANS_LINE_TYPE=A1.ALPHA_SUB_TAB  and A1.alpha_tab=TPend.TRANS_LINE_TYPE_AT");
		Object objParams[] = new Object[intKeyFieldsCount];
		objParams[0] = new String(dObj.getCountry());// country
		objParams[1] = new String(dObj.getLeBook());
		objParams[2] = new String(dObj.getTransLineId());
		try
		{if(!dObj.isVerificationRequired() || dObj.isReview()){intStatus =0;}
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
	protected int deleteTransLineHeaderAppr(TransLineHeaderVb vObject){
		String query = "Delete from RA_MST_TRANS_LINE_HEADER where COUNTRY = ? AND LE_BOOK = ? AND TRANS_LINE_ID = ? ";
		Object[] args = {vObject.getCountry(),vObject.getLeBook(),vObject.getTransLineId()};
		return getJdbcTemplate().update(query,args);
		
	}
	protected int deleteTransLineHeaderPend(TransLineHeaderVb vObject){
		String query = "Delete from RA_MST_TRANS_LINE_HEADER_PEND where COUNTRY = ? AND LE_BOOK = ? AND TRANS_LINE_ID = ? ";
		Object[] args = {vObject.getCountry(),vObject.getLeBook(),vObject.getTransLineId()};
		return getJdbcTemplate().update(query,args);
		
	}
	protected int doInsertionApprTransLineHeaders(TransLineHeaderVb vObject){
		String insertionGrpCols = "TRANS_LINE_PROD_GRP,TRANS_LINE_PROD_SUB_TYPE ";
		if("S".equals(vObject.getTransLineType())) {
			insertionGrpCols = "TRANS_LINE_SERV_GRP,TRANS_LINE_SERV_SUB_TYPE ";
		}
		String query =  " Insert Into RA_MST_TRANS_LINE_HEADER(COUNTRY,LE_BOOK,TRANS_LINE_ID,TRANS_LINE_DESCRIPTION,TRANS_LINE_TYPE" + 
				","+insertionGrpCols+"" + 
				",EXTRACTION_FREQUENCY,EXTRACTION_MONTH_DAY" + 
				",TARGET_STG_TABLE_ID,TRANS_LINE_STATUS_NT,TRANS_LINE_STATUS" + 
				",RECORD_INDICATOR_NT,RECORD_INDICATOR,MAKER,VERIFIER,INTERNAL_STATUS,DATE_LAST_MODIFIED,DATE_CREATION,DEPT_ID) "+
				" Values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,"+getDbFunction("SYSDATE")+","+getDbFunction("SYSDATE")+",?)";
		
		Object[] args = {vObject.getCountry(),vObject.getLeBook(),vObject.getTransLineId(),vObject.getTransLineDescription(),
				vObject.getTransLineType(),
				vObject.getTransLineGrp(),vObject.getTransLineSubType(),
				vObject.getExtractionFrequency(),vObject.getExtractionMonthDay(),
				vObject.getTargetStgTableId(),
				vObject.getTransLineStatusNT(),vObject.getTransLineStatus(),vObject.getRecordIndicatorNt(),vObject.getRecordIndicator(),
				vObject.getMaker(),vObject.getVerifier(),vObject.getInternalStatus(),vObject.getDepartment()};
		return getJdbcTemplate().update(query,args);
	}
	protected int doInsertionPendTransLineHeaders(TransLineHeaderVb vObject){
		String insertionGrpCols = "TRANS_LINE_PROD_GRP,TRANS_LINE_PROD_SUB_TYPE ";
		if("S".equals(vObject.getTransLineType())) {
			insertionGrpCols = "TRANS_LINE_SERV_GRP,TRANS_LINE_SERV_SUB_TYPE ";
		}
		String query =  " Insert Into RA_MST_TRANS_LINE_HEADER_PEND(COUNTRY,LE_BOOK,TRANS_LINE_ID,TRANS_LINE_DESCRIPTION,TRANS_LINE_TYPE" + 
				","+insertionGrpCols+"" + 
				",EXTRACTION_FREQUENCY,EXTRACTION_MONTH_DAY" + 
				",TARGET_STG_TABLE_ID,TRANS_LINE_STATUS_NT,TRANS_LINE_STATUS" + 
				",RECORD_INDICATOR_NT,RECORD_INDICATOR,MAKER,VERIFIER,INTERNAL_STATUS,DATE_LAST_MODIFIED,DATE_CREATION,DEPT_ID) "+
				" Values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,"+getDbFunction("SYSDATE")+","+getDbFunction("SYSDATE")+",?)";
		
		Object[] args = {vObject.getCountry(),vObject.getLeBook(),vObject.getTransLineId(),vObject.getTransLineDescription(),
				vObject.getTransLineType(),
				vObject.getTransLineGrp(),vObject.getTransLineSubType(),
				vObject.getExtractionFrequency(),vObject.getExtractionMonthDay(),
				vObject.getTargetStgTableId(),
				vObject.getTransLineStatusNT(),vObject.getTransLineStatus(),vObject.getRecordIndicatorNt(),vObject.getRecordIndicator(),
				vObject.getMaker(),vObject.getVerifier(),vObject.getInternalStatus(),vObject.getDepartment()};
		return getJdbcTemplate().update(query,args);
	}
	protected int doInsertionPendTransLineHeadersDc(TransLineHeaderVb vObject){
		String query = "";
		String insertionGrpCols = "TRANS_LINE_PROD_GRP,TRANS_LINE_PROD_SUB_TYPE ";
		if("S".equals(vObject.getTransLineType())) {
			insertionGrpCols = "TRANS_LINE_SERV_GRP,TRANS_LINE_SERV_SUB_TYPE ";
		}
		String dateCreation = "";
		if ("ORACLE".equalsIgnoreCase(databaseType)) 
			dateCreation = "To_Date(?, 'DD-MM-YYYY HH24:MI:SS')";
		else if ("MSSQL".equalsIgnoreCase(databaseType))	
			dateCreation = "CONVERT(datetime, ?, 103)";
		
		query =  " Insert Into RA_MST_TRANS_LINE_HEADER_PEND(COUNTRY,LE_BOOK,TRANS_LINE_ID,TRANS_LINE_DESCRIPTION,TRANS_LINE_TYPE" + 
				","+insertionGrpCols+"" + 
				",EXTRACTION_FREQUENCY,EXTRACTION_MONTH_DAY" + 
				",TARGET_STG_TABLE_ID,TRANS_LINE_STATUS_NT,TRANS_LINE_STATUS" + 
				",RECORD_INDICATOR_NT,RECORD_INDICATOR,MAKER,VERIFIER,INTERNAL_STATUS,DATE_LAST_MODIFIED,DATE_CREATION"+
				",DEPT_ID ) "+
				" Values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,"+getDbFunction("SYSDATE")+", "+dateCreation+",? )";
		
		Object[] args = {vObject.getCountry(),vObject.getLeBook(),vObject.getTransLineId(),vObject.getTransLineDescription(),
				vObject.getTransLineType(),vObject.getTransLineGrp(),vObject.getTransLineSubType(),
				vObject.getExtractionFrequency(),vObject.getExtractionMonthDay(),
				vObject.getTargetStgTableId(),
				vObject.getTransLineStatusNT(),vObject.getTransLineStatus(),vObject.getRecordIndicatorNt(),vObject.getRecordIndicator(),
				vObject.getMaker(),vObject.getVerifier(),vObject.getInternalStatus(),vObject.getDateCreation(),
				vObject.getDepartment()};
		return getJdbcTemplate().update(query,args);
	}
	protected int doUpdateApprHeader(TransLineHeaderVb vObject){
		String insertionGrpCols = "TRANS_LINE_PROD_GRP = ? ,TRANS_LINE_PROD_SUB_TYPE  = ?";
		if("S".equals(vObject.getTransLineType())) {
			insertionGrpCols = "TRANS_LINE_SERV_GRP  = ?,TRANS_LINE_SERV_SUB_TYPE = ? ";
		}
		
		String query = " Update RA_MST_TRANS_LINE_HEADER set TRANS_LINE_TYPE= ?,TRANS_LINE_DESCRIPTION= ?"
				+ ","+insertionGrpCols+" "
				+ ",EXTRACTION_FREQUENCY= ?,EXTRACTION_MONTH_DAY= ?" + 
				",TARGET_STG_TABLE_ID= ?,TRANS_LINE_STATUS= ?" + 
				",RECORD_INDICATOR= ?,MAKER= ?,VERIFIER= ?,DATE_LAST_MODIFIED= "+getDbFunction("SYSDATE")+""+
				",DEPT_ID = ? "+
				" WHERE COUNTRY= ? AND LE_BOOK= ? AND TRANS_LINE_ID= ? ";
		Object[] args = { vObject.getTransLineType(), vObject.getTransLineDescription(),vObject.getTransLineGrp(),
				vObject.getTransLineSubType(),
				vObject.getExtractionFrequency(),vObject.getExtractionMonthDay(),vObject.getTargetStgTableId(),
				vObject.getTransLineStatus(),vObject.getRecordIndicator(),
				vObject.getMaker(),vObject.getVerifier(),vObject.getDepartment(),
				vObject.getCountry(),vObject.getLeBook(),vObject.getTransLineId()};
		return getJdbcTemplate().update(query,args);
	}
	protected int doUpdatePendHeader(TransLineHeaderVb vObject){
		String insertionGrpCols = "TRANS_LINE_PROD_GRP = ? ,TRANS_LINE_PROD_SUB_TYPE  = ?";
		if("S".equals(vObject.getTransLineType())) {
			insertionGrpCols = "TRANS_LINE_SERV_GRP  = ?,TRANS_LINE_SERV_SUB_TYPE = ? ";
		}
		String query = " Update RA_MST_TRANS_LINE_HEADER_PEND set TRANS_LINE_TYPE=?,TRANS_LINE_DESCRIPTION= ?"
				+ ","+insertionGrpCols+" "
				+ ",EXTRACTION_FREQUENCY= ?,EXTRACTION_MONTH_DAY= ?" + 
				",TARGET_STG_TABLE_ID= ?,TRANS_LINE_STATUS= ?" + 
				",RECORD_INDICATOR= ?,MAKER= ?,VERIFIER= ?,DATE_LAST_MODIFIED= "+getDbFunction("SYSDATE")+""+
				",DEPT_ID = ? "+
				" WHERE COUNTRY= ? AND LE_BOOK= ? AND TRANS_LINE_ID= ? ";
		Object[] args = { vObject.getTransLineType(), vObject.getTransLineDescription(),vObject.getTransLineGrp(),
				vObject.getTransLineSubType(),
				vObject.getExtractionFrequency(),vObject.getExtractionMonthDay(),vObject.getTargetStgTableId(),
				vObject.getTransLineStatus(),vObject.getRecordIndicator(),
				vObject.getMaker(),vObject.getVerifier(),vObject.getDepartment(),
				vObject.getCountry(),vObject.getLeBook(),vObject.getTransLineId()};
		return getJdbcTemplate().update(query,args);
	}
	@Override
	protected List<TransLineHeaderVb> selectApprovedRecord(TransLineHeaderVb vObject){
		return getQueryResults(vObject, Constants.STATUS_ZERO);
	}
	@Override
	public List<TransLineHeaderVb> doSelectPendingRecord(TransLineHeaderVb vObject){
		return getQueryResults(vObject, Constants.STATUS_PENDING);
	}
	@Override
	protected int getStatus(TransLineHeaderVb records){return records.getTransLineStatus();}
	@Override
	protected void setStatus(TransLineHeaderVb vObject,int status){vObject.setTransLineStatus(status);
	}
	
	@Override
	@Transactional(rollbackForClassName={"com.vision.exception.RuntimeCustomException"})
	public ExceptionCode doInsertApprRecordForNonTrans(TransLineHeaderVb vObject) throws RuntimeCustomException {
		List<TransLineHeaderVb> collTemp = null;
		TransLineSbuVb transLineSbuVb = new TransLineSbuVb();
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
		vObject.setTransLineStatus(Constants.STATUS_ZERO);
		vObject.setMaker(intCurrentUserId);
		vObject.setVerifier(intCurrentUserId);
		//vObject.setTransLineServGrp("NA");
		//vObject.setTransLineType("P");
		retVal = doInsertionApprTransLineHeaders(vObject);
		if (retVal != Constants.SUCCESSFUL_OPERATION){
			exceptionCode = getResultObject(retVal);
			throw buildRuntimeCustomException(exceptionCode);
		} else {
			exceptionCode = getResultObject(Constants.SUCCESSFUL_OPERATION);
		}
		if(ValidationUtil.isValid(vObject.getBusinessVertical())) {
			exceptionCode = transLinesSbuDao.deleteAndInsertApprSbu(vObject);
			if(exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION)
				throw buildRuntimeCustomException(exceptionCode);
		}
		
		if (vObject.getTransLineGllst() != null && !vObject.getTransLineGllst().isEmpty()) {
			exceptionCode = transLinesGlDao.deleteAndInsertApprGl(vObject);
			if(exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION)
				throw buildRuntimeCustomException(exceptionCode);
		}
		String defaultChannel = commonDao.findVisionVariableValue("RA_DEFAULT_CHANNEL");
		if(!ValidationUtil.isValid(defaultChannel)) {
			defaultChannel = "CH08";
		}
		vObject.setChannelId(defaultChannel);
		if(ValidationUtil.isValid(vObject.getChannelId())) {
			exceptionCode = transLinesChannelDao.deleteAndInsertApprChannel(vObject);
			if(exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION)
				throw buildRuntimeCustomException(exceptionCode);
		}
		exceptionCode =getResultObject(Constants.SUCCESSFUL_OPERATION);
		writeAuditLog(vObject, null);
		/*if(exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION){
			exceptionCode = getResultObject(Constants.AUDIT_TRAIL_ERROR);
			throw buildRuntimeCustomException(exceptionCode);
		}*/
		return exceptionCode;
	}
	@Override
	@Transactional(rollbackForClassName={"com.vision.exception.RuntimeCustomException"})
	public ExceptionCode doInsertRecordForNonTrans(TransLineHeaderVb vObject) throws RuntimeCustomException {
		List<TransLineHeaderVb> collTemp = null;
		List<TransLineHeaderVb> collTempAppr = null;
		TransLineSbuVb transLineSbuVb = new TransLineSbuVb();
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
			int staticDeletionFlag = getStatus(((ArrayList<TransLineHeaderVb>) collTempAppr).get(0));
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
		vObject.setTransLineStatus(Constants.STATUS_ZERO);
		vObject.setMaker(intCurrentUserId);
		//vObject.setVerifier(intCurrentUserId);
		vObject.setVerifier(0);
		//vObject.setTransLineServGrp("NA");
		// vObject.setTransLineType("P");
		retVal = doInsertionPendTransLineHeaders(vObject);
		if (retVal != Constants.SUCCESSFUL_OPERATION){
			exceptionCode = getResultObject(retVal);
			throw buildRuntimeCustomException(exceptionCode);
		} else {
			exceptionCode = getResultObject(Constants.SUCCESSFUL_OPERATION);
		}
		if(ValidationUtil.isValid(vObject.getBusinessVertical())) {
			exceptionCode = transLinesSbuDao.deleteAndInsertPendSbu(vObject);
			if(exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION)
				throw buildRuntimeCustomException(exceptionCode);
		}
		
		if (vObject.getTransLineGllst() != null && !vObject.getTransLineGllst().isEmpty()) {
			exceptionCode = transLinesGlDao.deleteAndInsertPendGl(vObject);
			if(exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION)
				throw buildRuntimeCustomException(exceptionCode);
		}
		String defaultChannel = commonDao.findVisionVariableValue("RA_DEFAULT_CHANNEL");
		if(!ValidationUtil.isValid(defaultChannel)) {
			defaultChannel = "CH08";
		}
		vObject.setChannelId(defaultChannel);
		
		if(ValidationUtil.isValid(vObject.getChannelId())) {
			exceptionCode = transLinesChannelDao.deleteAndInsertPendChannel(vObject);
			if(exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION)
				throw buildRuntimeCustomException(exceptionCode);
		}
		writeAuditLog(vObject, null);
		exceptionCode =getResultObject(Constants.SUCCESSFUL_OPERATION);
		return exceptionCode;
	}
	@Override
	@Transactional(rollbackForClassName={"com.vision.exception.RuntimeCustomException"})
	public ExceptionCode doUpdateApprRecordForNonTrans(TransLineHeaderVb vObject) throws RuntimeCustomException  {
		List<TransLineHeaderVb> collTemp = null;
		TransLineHeaderVb vObjectlocal = null;
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
		vObjectlocal = ((ArrayList<TransLineHeaderVb>)collTemp).get(0);
		// Even if record is not there in Appr. table reject the record
		if (collTemp.size() == 0) {
			exceptionCode = getResultObject(Constants.ATTEMPT_TO_MODIFY_UNEXISTING_RECORD);
			throw buildRuntimeCustomException(exceptionCode);
		}

		if (vObject.getTransLineStatus() == Constants.PASSIVATE) {
			exceptionCode = getResultObject(Constants.CANNOT_MODIFY_TO_DELETE_STATE);
			throw buildRuntimeCustomException(exceptionCode);
		}
		vObject.setRecordIndicator(Constants.STATUS_ZERO);
		vObject.setVerifier(getIntCurrentUserId());
		vObject.setDateCreation(vObjectlocal.getDateCreation());
		// vObject.setTransLineType("P");
		retVal = doUpdateApprHeader(vObject);
		if (retVal != Constants.SUCCESSFUL_OPERATION){
			exceptionCode = getResultObject(retVal);
			throw buildRuntimeCustomException(exceptionCode);
		} else {
			exceptionCode = getResultObject(Constants.SUCCESSFUL_OPERATION);
		}
		if(ValidationUtil.isValid(vObject.getBusinessVertical())) {
			exceptionCode = transLinesSbuDao.deleteAndInsertApprSbu(vObject);
			if(exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION)
				throw buildRuntimeCustomException(exceptionCode);
		}
		if (vObject.getTransLineGllst() != null && !vObject.getTransLineGllst().isEmpty()) {
			exceptionCode = transLinesGlDao.deleteAndInsertApprGl(vObject);
			if(exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION)
				throw buildRuntimeCustomException(exceptionCode);
		}
		String defaultChannel = commonDao.findVisionVariableValue("RA_DEFAULT_CHANNEL");
		if(!ValidationUtil.isValid(defaultChannel)) {
			defaultChannel = "CH08";
		}
		vObject.setChannelId(defaultChannel);
		if(ValidationUtil.isValid(vObject.getChannelId())) {
			exceptionCode = transLinesChannelDao.deleteAndInsertApprChannel(vObject);
			if(exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION)
				throw buildRuntimeCustomException(exceptionCode);
		}
		String systemDate = getSystemDate();
		vObject.setDateLastModified(systemDate);
		
		writeAuditLog(vObject, vObjectlocal);
		/*if(exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION){
			exceptionCode = getResultObject(Constants.AUDIT_TRAIL_ERROR);
			throw buildRuntimeCustomException(exceptionCode);
		}*/
		exceptionCode =getResultObject(Constants.SUCCESSFUL_OPERATION);
		return exceptionCode;
	}
	@Override
	@Transactional(rollbackForClassName={"com.vision.exception.RuntimeCustomException"})
	public ExceptionCode doUpdateRecordForNonTrans(TransLineHeaderVb vObject) throws RuntimeCustomException  {
		List<TransLineHeaderVb> collTemp = null;
		TransLineHeaderVb vObjectlocal = null;
		ExceptionCode exceptionCode = null;
		strApproveOperation =Constants.MODIFY;
		strErrorDesc  = "";
		strCurrentOperation = Constants.MODIFY;
		setServiceDefaults();
		vObject.setMaker(getIntCurrentUserId());
		// vObject.setTransLineType("P");
		collTemp = doSelectPendingRecord(vObject);
		if (collTemp == null){
			exceptionCode = getResultObject(Constants.ERRONEOUS_OPERATION);
			throw buildRuntimeCustomException(exceptionCode);
		}
		if (vObject.getTransLineStatus() == Constants.PASSIVATE) {
			exceptionCode = getResultObject(Constants.CANNOT_MODIFY_TO_DELETE_STATE);
			throw buildRuntimeCustomException(exceptionCode);
		}
		if (!collTemp.isEmpty()) {
			vObjectlocal = ((ArrayList<TransLineHeaderVb>)collTemp).get(0);
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
			if(ValidationUtil.isValid(vObject.getBusinessVertical())) {
				exceptionCode = transLinesSbuDao.deleteAndInsertPendSbu(vObject);
				if(exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION)
					throw buildRuntimeCustomException(exceptionCode);
			}
			if (vObject.getTransLineGllst() != null && !vObject.getTransLineGllst().isEmpty()) {
				exceptionCode = transLinesGlDao.deleteAndInsertPendGl(vObject);
				if(exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION)
					throw buildRuntimeCustomException(exceptionCode);
			}
			String defaultChannel = commonDao.findVisionVariableValue("RA_DEFAULT_CHANNEL");
			if(!ValidationUtil.isValid(defaultChannel)) {
				defaultChannel = "CH08";
			}
			vObject.setChannelId(defaultChannel);
			if(ValidationUtil.isValid(vObject.getChannelId())) {
				exceptionCode = transLinesChannelDao.deleteAndInsertPendChannel(vObject);
				if(exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION)
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
				vObjectlocal = ((ArrayList<TransLineHeaderVb>) collTemp).get(0);
				vObject.setDateCreation(vObjectlocal.getDateCreation());
			}
		    vObject.setDateCreation(vObjectlocal.getDateCreation());
		 // Record is there in approved, but not in pending.  So add it to pending
		    vObject.setVerifier(0);
		    vObject.setRecordIndicator(Constants.STATUS_UPDATE);
		    retVal = doInsertionPendTransLineHeadersDc(vObject);
			if (retVal != Constants.SUCCESSFUL_OPERATION){
				exceptionCode = getResultObject(retVal);
				throw buildRuntimeCustomException(exceptionCode);
			}
			if(ValidationUtil.isValid(vObject.getBusinessVertical())) {
				exceptionCode = transLinesSbuDao.deleteAndInsertPendSbu(vObject);//del and insert pend
				if(exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION)
					throw buildRuntimeCustomException(exceptionCode);
			}
			if (vObject.getTransLineGllst() != null && !vObject.getTransLineGllst().isEmpty()) {
				exceptionCode = transLinesGlDao.deleteAndInsertPendGl(vObject);
				if(exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION)
					throw buildRuntimeCustomException(exceptionCode);
			}
			String defaultChannel = commonDao.findVisionVariableValue("RA_DEFAULT_CHANNEL");
			if(!ValidationUtil.isValid(defaultChannel)) {
				defaultChannel = "CH08";
			}
			vObject.setChannelId(defaultChannel);
			if(ValidationUtil.isValid(vObject.getChannelId())) {
				exceptionCode = transLinesChannelDao.deleteAndInsertPendChannel(vObject);
				if(exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION)
					throw buildRuntimeCustomException(exceptionCode);
			}
			writeAuditLog(vObject, null);
			return getResultObject(Constants.SUCCESSFUL_OPERATION);
		}
	}
	@Override
	@Transactional(rollbackForClassName={"com.vision.exception.RuntimeCustomException"})
	public ExceptionCode doRejectForTransaction(TransLineHeaderVb vObject)throws RuntimeCustomException {
		strErrorDesc  = "";
		strCurrentOperation = Constants.APPROVE;
		setServiceDefaults();
		return doRejectRecord(vObject);
	}
	@Override
	@Transactional(rollbackForClassName={"com.vision.exception.RuntimeCustomException"})
	public ExceptionCode doRejectRecord(TransLineHeaderVb vObject)throws RuntimeCustomException {
		TransLineHeaderVb vObjectlocal = null;
		List<TransLineHeaderVb> collTemp = null;
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
			vObjectlocal = ((ArrayList<TransLineHeaderVb>)collTemp).get(0);
			retVal = deleteTransLineHeaderPend(vObject);
			
			List<TransLineGLVb> collTempGl = null;
			collTempGl = transLinesGlDao.getTransGLDetails(vObject, 1);
			if (collTempGl != null && !collTempGl.isEmpty()) {
				retVal = transLinesGlDao.deleteTransLineGlPend(vObject);
			}
			List<TransLineSbuVb> collTempSbu = null;
			collTempSbu = transLinesSbuDao.getTransSbuDetails(vObject, 1);
			if (collTempSbu != null && !collTempSbu.isEmpty()) {
				retVal = transLinesSbuDao.deleteTransLineSbuPend(vObject);
			}
			String defaultChannel = commonDao.findVisionVariableValue("RA_DEFAULT_CHANNEL");
			if(!ValidationUtil.isValid(defaultChannel)) {
				defaultChannel = "CH08";
			}
			vObject.setChannelId(defaultChannel);
			List<TransLineChannelVb> collTempChannel = null;
			collTempChannel = transLinesChannelDao.getTransChannelDetails(vObject, 1);
			if (collTempChannel != null && !collTempChannel.isEmpty()) {
				retVal = transLinesChannelDao.deleteTransLineChannelPend(vObject);
			}
			exceptionCode = getResultObject(Constants.SUCCESSFUL_OPERATION);
			
			if (vObject.isBusinessFlag() || vObject.isFeesFlag()) {
				List<BusinessLineHeaderVb> businessLineDatalst = businessLineConfigDao.getExistingRecords(vObject,
						Constants.STATUS_PENDING);
				BusinessLineHeaderVb businessLineHeaderVb = new BusinessLineHeaderVb();
				businessLineHeaderVb.setCountry(vObject.getCountry());
				businessLineHeaderVb.setLeBook(vObject.getLeBook());
				businessLineHeaderVb.setTransLineId(vObject.getTransLineId());
				List<BusinessLineHeaderVb> collTempBl = businessLineConfigDao.getQueryResults(businessLineHeaderVb,
						Constants.STATUS_ZERO);
				if (collTempBl != null && !collTempBl.isEmpty()) {
					businessLineDatalst.addAll(collTempBl);
				}
				if (businessLineDatalst != null && !businessLineDatalst.isEmpty()) {
					for (BusinessLineHeaderVb dObj : businessLineDatalst) {
						dObj.setFeesFlag(vObject.isFeesFlag());
						dObj.setBusinessFlag(vObject.isBusinessFlag());
						exceptionCode = businessLineConfigDao.doRejectForTransaction(dObj);
						if (exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION)
							throw buildRuntimeCustomException(exceptionCode);
					}
				}
			}
			writeAuditLog(vObject, null);
			return exceptionCode;
		}catch (UncategorizedSQLException uSQLEcxception) {
			strErrorDesc = parseErrorMsg(uSQLEcxception);
			exceptionCode = getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION);
			throw buildRuntimeCustomException(exceptionCode);
		}catch(Exception ex){
			logger.error("Error in Reject.",ex);
			//logger.error( ((vObject==null)? "vObject is Null":vObject.toString()));
			strErrorDesc = ex.getMessage();
			exceptionCode = getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION);
			throw buildRuntimeCustomException(exceptionCode);
		}
	}

	@Override
	@Transactional(rollbackForClassName = { "com.vision.exception.RuntimeCustomException" })
	public ExceptionCode doApproveForTransaction(TransLineHeaderVb vObject, boolean staticDelete){
		ExceptionCode exceptionCode = new ExceptionCode();
		strErrorDesc = "";
		strCurrentOperation = Constants.APPROVE;
		setServiceDefaults();
		boolean flag = true;
		List<TransLineHeaderVb> collTemp = null;
		try {
			String systemDate = getSystemDate();
			BusinessLineHeaderVb businessLineHeaderVb = new BusinessLineHeaderVb();
			businessLineHeaderVb.setCountry(vObject.getCountry());
			businessLineHeaderVb.setLeBook(vObject.getLeBook());
			businessLineHeaderVb.setTransLineId(vObject.getTransLineId());
			List<BusinessLineHeaderVb> businessLinelst = businessLineConfigDao.getQueryResults(businessLineHeaderVb,
					Constants.STATUS_PENDING);
			if (businessLinelst != null && !businessLinelst.isEmpty()) {
				for (BusinessLineHeaderVb businessHeaderVb : businessLinelst) {
					if (vObject.isFeesFlag()) {
						FeesConfigHeaderVb feesConfigHeaderVb = new FeesConfigHeaderVb();
						feesConfigHeaderVb.setCountry(businessHeaderVb.getCountry());
						feesConfigHeaderVb.setLeBook(businessHeaderVb.getLeBook());
						feesConfigHeaderVb.setBusinessLineId(businessHeaderVb.getBusinessLineId());
						List<FeesConfigHeaderVb> feesLineDatalst = feesConfigHeadersDao
								.getQueryResults(feesConfigHeaderVb, Constants.STATUS_PENDING);
						if (feesLineDatalst != null && !feesLineDatalst.isEmpty()) {
							List<FeesConfigHeaderVb> myFeeLst = feesLineDatalst.stream()
									.filter(n -> n.getMaker() == intCurrentUserId).collect(Collectors.toList());
							if (myFeeLst != null && !myFeeLst.isEmpty()) {
								exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
								exceptionCode.setErrorMsg("[" + myFeeLst.size()
										+ "] Fee Configs are added from your ID.Maker cannot Approve !!");
								flag = false;
								break;
							}
						}
					}
				}
				if (vObject.isBusinessFlag()) {
					List<BusinessLineHeaderVb> myBusLst = businessLinelst.stream()
							.filter(n -> n.getMaker() == intCurrentUserId).collect(Collectors.toList());
					if (myBusLst != null && !myBusLst.isEmpty()) {
						exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
						exceptionCode.setErrorMsg("[" + myBusLst.size()
								+ "] Business Lines are added from your Id.Maker cannot Approve !!");
						flag = false;

					}
				}

			}
			List<BusinessLineHeaderVb> collTempBl = businessLineConfigDao.getQueryResults(businessLineHeaderVb,
					Constants.STATUS_ZERO);
			if (collTempBl != null && !collTempBl.isEmpty()) {
				businessLinelst.addAll(collTempBl);
			}
			if (!flag) {
				return exceptionCode;
			}
			if (!staticDelete && vObject.getRecordIndicator() == Constants.STATUS_DELETE) {
				collTemp = selectApprovedRecord(vObject);
				retVal = deleteTransLineHeaderAppr(vObject);
				if (retVal != Constants.SUCCESSFUL_OPERATION) {
					exceptionCode = getResultObject(retVal);
					throw buildRuntimeCustomException(exceptionCode);
				}
				if (collTemp != null && !collTemp.isEmpty()) {
					retVal = transLinesSbuDao.deleteTransLineSbuAppr(vObject);
					retVal = transLinesGlDao.deleteTransLineGlAppr(vObject);
					retVal = transLinesChannelDao.deleteTransLineChannelAppr(vObject);
					vObject.setDateLastModified(systemDate);
					writeAuditLog(null, vObject);
				}
				retVal = deleteTransLineHeaderPend(vObject);
				retVal = transLinesSbuDao.deleteTransLineSbuPend(vObject);
				retVal = transLinesGlDao.deleteTransLineGlPend(vObject);
				retVal = transLinesChannelDao.deleteTransLineChannelPend(vObject);

				if (businessLinelst != null && !businessLinelst.isEmpty()) {
					for (BusinessLineHeaderVb businessLineVb : businessLinelst) {
						if (businessLineVb.getRecordIndicator() == Constants.STATUS_ZERO) {
							retVal = businessLineConfigDao.deleteBusinessLineHeaderAppr(businessLineVb);
							retVal = businessLineConfigDao.getBusinessLineConfigGLDao()
									.deleteBusinessLineGlAppr(businessLineVb);
							retVal = businessLineConfigDao.getBlReconRuleDao().doDeleteApprBlReconRule(businessLineVb);
							businessLineVb.setDateLastModified(systemDate);
							businessLineConfigDao.writeAuditLog(null, businessLineVb);
						}

						// Pend
						retVal = businessLineConfigDao.deleteBusinessLineHeaderPend(businessLineVb);
						retVal = businessLineConfigDao.getBusinessLineConfigGLDao()
								.deleteBusinessLineGlPend(businessLineVb);
						retVal = businessLineConfigDao.getBlReconRuleDao().doDeletePendBlReconRule(businessLineVb);

						// fees
						FeesConfigHeaderVb feesConfigHeaderVb = new FeesConfigHeaderVb();
						feesConfigHeaderVb.setCountry(businessLineVb.getCountry());
						feesConfigHeaderVb.setLeBook(businessLineVb.getLeBook());
						feesConfigHeaderVb.setBusinessLineId(businessLineVb.getBusinessLineId());
						List<FeesConfigHeaderVb> feesLineDatalst = feesConfigHeadersDao
								.getQueryResults(feesConfigHeaderVb, Constants.STATUS_PENDING);
						List<FeesConfigHeaderVb> collTempFl = feesConfigHeadersDao.getQueryResults(feesConfigHeaderVb,
								Constants.STATUS_ZERO);
						if (collTempFl != null && !collTempFl.isEmpty()) {
							feesLineDatalst.addAll(collTempFl);
						}
						if (feesLineDatalst != null && !feesLineDatalst.isEmpty()) {
							for (FeesConfigHeaderVb feesConfigVb : feesLineDatalst) {
								if (feesConfigVb.getRecordIndicator() == Constants.STATUS_ZERO) {
									retVal = feesConfigHeadersDao.deleteFeesHeaderAppr(feesConfigVb);
									retVal = feesConfigHeadersDao.getFeesConfigDetailsDao()
											.deleteFeesDetailsApprMain(feesConfigVb);
									retVal = feesConfigHeadersDao.getFeesConfigTierDao()
											.deleteFeesTierApprMain(feesConfigVb);
									feesConfigVb.setDateLastModified(systemDate);
									feesConfigHeadersDao.writeAuditLog(null, feesConfigVb);
								}

								// Pend
								retVal = feesConfigHeadersDao.deleteFeesHeaderPend(feesConfigVb);
								retVal = feesConfigHeadersDao.getFeesConfigDetailsDao()
										.deleteFeesDetailsPendMain(feesConfigVb);
								retVal = feesConfigHeadersDao.getFeesConfigTierDao()
										.deleteFeesTierPendMain(feesConfigVb);
							}
						}
					}
				}
				exceptionCode = getResultObject(Constants.SUCCESSFUL_OPERATION);
			} else {
				exceptionCode = doApproveRecord(vObject, staticDelete);
				if (exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION) {
					return getResultObject(exceptionCode.getErrorCode());
				}
				if ((vObject.isBusinessFlag() || vObject.isFeesFlag()) && businessLinelst != null
						&& !businessLinelst.isEmpty()) {
					for (BusinessLineHeaderVb businessLineVb : businessLinelst) {
						if (vObject.isBusinessFlag() && businessLineVb.getRecordIndicator() != Constants.STATUS_ZERO ) {
							businessLineVb.setBusinessFlag(vObject.isBusinessFlag());
							exceptionCode = businessLineConfigDao.doApproveRecord(businessLineVb, staticDelete);
						}
						if (vObject.isFeesFlag() && exceptionCode.getErrorCode() == Constants.SUCCESSFUL_OPERATION && businessLineVb.getRecordIndicator() != Constants.STATUS_INSERT) {
							FeesConfigHeaderVb feesConfigHeaderVb = new FeesConfigHeaderVb();
							feesConfigHeaderVb.setCountry(businessLineVb.getCountry());
							feesConfigHeaderVb.setLeBook(businessLineVb.getLeBook());
							feesConfigHeaderVb.setBusinessLineId(businessLineVb.getBusinessLineId());
							List<FeesConfigHeaderVb> feesLineDatalst = feesConfigHeadersDao
									.getQueryResults(feesConfigHeaderVb, Constants.STATUS_PENDING);
							if (feesLineDatalst != null && !feesLineDatalst.isEmpty()) {
								for (FeesConfigHeaderVb feeConfigVb : feesLineDatalst) {
									feeConfigVb.setFeesFlag(vObject.isFeesFlag());
									exceptionCode = feesConfigHeadersDao.doApproveRecord(feeConfigVb, staticDelete);
									if (exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION)
										throw buildRuntimeCustomException(exceptionCode);
								}
							}
						}
					}

				}
			}
			// For returning the service desc as Transline config
			if (exceptionCode.getErrorCode() == Constants.SUCCESSFUL_OPERATION) {
				return getResultObject(Constants.SUCCESSFUL_OPERATION);
			}
		} catch (RuntimeCustomException ex) {
			exceptionCode.setErrorMsg(ex.getMessage());
			throw ex;
//			exceptionCode =	getResultObject(Constants.ERRONEOUS_OPERATION);
		} catch (Exception e) {
			exceptionCode.setErrorMsg(e.getMessage());
			throw e;
		}
		// return exceptionCode;
		return exceptionCode;// getResultObject(Constants.ERRONEOUS_OPERATION);

	}
	
	@Override
	//@Transactional(rollbackForClassName={"com.vision.exception.RuntimeCustomException"})
	public ExceptionCode doApproveRecord(TransLineHeaderVb vObject, boolean staticDelete) throws RuntimeCustomException {
		TransLineHeaderVb oldContents = null;
		TransLineGLVb oldContentsGl = null;
		TransLineSbuVb oldContentsSbu = null;
		TransLineChannelVb oldContentsChannel = null;
		
		TransLineHeaderVb vObjectlocal = null;
		TransLineGLVb vObjectGllocal = null;
		TransLineSbuVb vObjectSbulocal = null;
		TransLineChannelVb vObjectChannellocal = null;
		
		List<TransLineHeaderVb> collTemp = null;
		List<TransLineGLVb> collTempGl = null;
		List<TransLineGLVb> collTempGlAppr = null;
		List<TransLineSbuVb> collTempSbu = null;
		List<TransLineSbuVb> collTempSbuAppr = null;
		List<TransLineChannelVb> collTempChannel = null;
		List<TransLineChannelVb> collTempChannelAppr = null;
		List<BusinessLineHeaderVb> collTempBL = null;
		List<FeesConfigHeaderVb> collTempFL = null;
		BusinessLineHeaderVb businessLineHeaderVb=new BusinessLineHeaderVb();
		FeesConfigHeaderVb feesConfigHeaderVb=new FeesConfigHeaderVb();
		ExceptionCode exceptionCode = null;
		strCurrentOperation = Constants.APPROVE;
		setServiceDefaults();
		try {
			// See if such a pending request exists in the pending table
			vObject.setVerifier(getIntCurrentUserId());
			vObject.setRecordIndicator(Constants.STATUS_ZERO);
			collTemp = doSelectPendingRecord(vObject);
			if (collTemp == null){
				exceptionCode = getResultObject(Constants.ERRONEOUS_OPERATION);
				throw buildRuntimeCustomException(exceptionCode);
			}

			if (collTemp.size() == 0){
				exceptionCode = getResultObject(Constants.NO_SUCH_PENDING_RECORD);
				throw buildRuntimeCustomException(exceptionCode);
			}

			vObjectlocal = ((ArrayList<TransLineHeaderVb>)collTemp).get(0);

			if (vObjectlocal.getMaker() == getIntCurrentUserId()){
				exceptionCode = getResultObject(Constants.MAKER_CANNOT_APPROVE);
				throw buildRuntimeCustomException(exceptionCode);
			}
			
			if(vObject.getRecordIndicator()==1) {
				//When trying to approve Transline record ,check any pending exist in BusinessLine and Feesline.
				//If not exist,proceed for approval
				String businessLineId=getTranBusinessLinePendId(vObject.getCountry(), vObject.getLeBook(),vObject.getTransLineId());
				businessLineHeaderVb.setCountry(vObject.getCountry());
				businessLineHeaderVb.setLeBook(vObject.getLeBook());
				businessLineHeaderVb.setTransLineId(vObject.getTransLineId());
				businessLineHeaderVb.setBusinessLineId(businessLineId);
	
				collTempBL = doSelectPendingBusinessLineRecord(businessLineHeaderVb);
	
				feesConfigHeaderVb.setCountry(vObject.getCountry());
				feesConfigHeaderVb.setLeBook(vObject.getLeBook());
				feesConfigHeaderVb.setTransLineId(vObject.getTransLineId());
				feesConfigHeaderVb.setBusinessLineId(businessLineId);
				collTempFL = doSelectPendingFeeLineRecord(feesConfigHeaderVb);
//				if (collTempBL!= null || collTempFL!=null){
//					exceptionCode = getResultObject(Constants.ERRONEOUS_OPERATION);
//					exceptionCode.setErrorMsg("Business Line/Fee Line is in pending.Kindly Approve");
//					throw buildRuntimeCustomException(exceptionCode);
//				}
			}else {
				collTempGl = transLinesGlDao.getTransGLDetails(vObjectlocal,1);
				if (collTempGl != null && !collTempGl.isEmpty()) {
					vObjectGllocal = ((ArrayList<TransLineGLVb>) collTempGl).get(0);
				}
				
				collTempSbu = transLinesSbuDao.getTransSbuDetails(vObjectlocal,1); // SBU
				if (collTempSbu != null && !collTempSbu.isEmpty()) {
					vObjectSbulocal = ((ArrayList<TransLineSbuVb>) collTempSbu).get(0);
				}
				collTempChannel = transLinesChannelDao.getTransChannelDetails(vObjectlocal,1);
				if (collTempChannel != null && !collTempChannel.isEmpty()) {
					vObjectChannellocal = ((ArrayList<TransLineChannelVb>) collTempChannel).get(0);
				}
				// If it's NOT addition, collect the existing record contents from the
				// Approved table and keep it aside, for writing audit information later.
				if (vObjectlocal.getRecordIndicator() != Constants.STATUS_INSERT){
					collTemp = selectApprovedRecord(vObject);
					if (collTemp == null || collTemp.isEmpty()){
						exceptionCode = getResultObject(Constants.ERRONEOUS_OPERATION);
						throw buildRuntimeCustomException(exceptionCode);
					}
					oldContents = ((ArrayList<TransLineHeaderVb>)collTemp).get(0);
					
					collTempGlAppr = transLinesGlDao.getTransGLDetails(vObjectlocal,0);
					if (collTempGlAppr != null && !collTempGlAppr.isEmpty()) {
						oldContentsGl = ((ArrayList<TransLineGLVb>) collTempGlAppr).get(0);
					}
					
					collTempSbuAppr = transLinesSbuDao.getTransSbuDetails(vObjectlocal,0);
					if (collTempSbuAppr != null && !collTempSbuAppr.isEmpty()) {
						oldContentsSbu = ((ArrayList<TransLineSbuVb>) collTempSbuAppr).get(0);
					}
					collTempChannelAppr = transLinesChannelDao.getTransChannelDetails(vObjectlocal,0);
					if (collTempChannelAppr != null && !collTempChannelAppr.isEmpty()) {
						oldContentsChannel = ((ArrayList<TransLineChannelVb>) collTempChannelAppr).get(0);
					}
				}
	
				if (vObjectlocal.getRecordIndicator() == Constants.STATUS_INSERT){  // Add authorization
					// Write the contents of the Pending table record to the Approved table
					vObjectlocal.setRecordIndicator(Constants.STATUS_ZERO);
					vObjectlocal.setVerifier(getIntCurrentUserId());
					retVal = doInsertionApprTransLineHeaders(vObjectlocal);
					if (retVal != Constants.SUCCESSFUL_OPERATION){
						exceptionCode = getResultObject(retVal);
						throw buildRuntimeCustomException(exceptionCode);
					}
					if (collTempSbuAppr != null && !collTempSbuAppr.isEmpty()) {
						transLinesSbuDao.deleteTransLineSbuAppr(vObjectlocal);
					}
					if(collTempSbu != null && !collTempSbu.isEmpty()) {
						collTempSbu.forEach(sbuPend -> {
							retVal = transLinesSbuDao.doInsertionApprTransLineSBU(sbuPend);
						});
					}
					if (collTempGlAppr != null && !collTempGlAppr.isEmpty()) {
						transLinesGlDao.deleteTransLineGlAppr(vObjectlocal);
					}
					if(collTempGl != null && !collTempGl.isEmpty()) {
						collTempGl.forEach(glPend -> {
							retVal = transLinesGlDao.doInsertionApprTransLineGL(glPend);
						});
					}
					String defaultChannel = commonDao.findVisionVariableValue("RA_DEFAULT_CHANNEL");
					if(!ValidationUtil.isValid(defaultChannel)) {
						defaultChannel = "CH08";
					}
					vObject.setChannelId(defaultChannel);
					if (collTempChannelAppr != null && !collTempChannelAppr.isEmpty()) {
						transLinesChannelDao.deleteTransLineChannelAppr(vObjectlocal);
					}
					if(collTempChannel != null && !collTempChannel.isEmpty()) {
						collTempChannel.forEach(channelPend -> {
							retVal = transLinesChannelDao.doInsertionApprTransLineChannel(channelPend);
						});
					}
					String systemDate = getSystemDate();
					vObject.setDateLastModified(systemDate);
					vObject.setDateCreation(systemDate);
					strApproveOperation = Constants.ADD;
				}else if (vObjectlocal.getRecordIndicator() == Constants.STATUS_UPDATE){ // Modify authorization
				
					collTemp = selectApprovedRecord(vObject);
					if (collTemp == null || collTemp.isEmpty()){
						exceptionCode = getResultObject(Constants.ERRONEOUS_OPERATION);
						throw buildRuntimeCustomException(exceptionCode);
					}	
	
					// If record already exists in the approved table, reject the addition
					if (!collTemp.isEmpty()) {
						// retVal = doUpdateAppr(vObjectlocal, MISConstants.ACTIVATE);
						vObjectlocal.setVerifier(getIntCurrentUserId());
						vObjectlocal.setRecordIndicator(Constants.STATUS_ZERO);
						retVal = doUpdateApprHeader(vObjectlocal);
						if (retVal != Constants.SUCCESSFUL_OPERATION){
							exceptionCode = getResultObject(retVal);
							throw buildRuntimeCustomException(exceptionCode);
						}
					}
					if (collTempSbuAppr != null && !collTempSbuAppr.isEmpty()) {
						transLinesSbuDao.deleteTransLineSbuAppr(vObjectlocal);
					}
					if(collTempSbu != null && !collTempSbu.isEmpty()) {
						collTempSbu.forEach(sbuPend -> {
							retVal = transLinesSbuDao.doInsertionApprTransLineSBU(sbuPend);
						});
					}
					if (collTempGlAppr != null && !collTempGlAppr.isEmpty()) {
						transLinesGlDao.deleteTransLineGlAppr(vObjectlocal);
					}
					if(collTempGl != null && !collTempGl.isEmpty()) {
						collTempGl.forEach(glPend -> {
							retVal = transLinesGlDao.doInsertionApprTransLineGL(glPend);
						});
					}
					String defaultChannel = commonDao.findVisionVariableValue("RA_DEFAULT_CHANNEL");
					if(!ValidationUtil.isValid(defaultChannel)) {
						defaultChannel = "CH08";
					}
					vObject.setChannelId(defaultChannel);
					if (collTempChannelAppr != null && !collTempChannelAppr.isEmpty()) {
						transLinesChannelDao.deleteTransLineChannelAppr(vObjectlocal);
					}
					if(collTempChannel != null && !collTempChannel.isEmpty()) {
						collTempChannel.forEach(channelPend -> {
							retVal = transLinesChannelDao.doInsertionApprTransLineChannel(channelPend);
						});
					}
					// Modify the existing contents of the record in Approved table
					
					String systemDate = getSystemDate();
					vObject.setDateLastModified(systemDate);
					// Set the current operation to write to audit log
					strApproveOperation = Constants.MODIFY;
				}else if (vObjectlocal.getRecordIndicator() == Constants.STATUS_DELETE){ // Delete authorization
					if(staticDelete){
						// Update the existing record status in the Approved table to delete 
						setStatus(vObjectlocal, Constants.PASSIVATE);
						vObjectlocal.setRecordIndicator(Constants.STATUS_ZERO);
						vObjectlocal.setVerifier(getIntCurrentUserId());
						retVal = doUpdateApprHeader(vObjectlocal);
						if (retVal != Constants.SUCCESSFUL_OPERATION){
							exceptionCode = getResultObject(retVal);
							throw buildRuntimeCustomException(exceptionCode);
						}
						setStatus(vObject, Constants.PASSIVATE);
						String systemDate = getSystemDate();
						vObject.setDateLastModified(systemDate);

					}else{
						// Delete the existing record from the Approved table 
						retVal = deleteTransLineHeaderAppr(vObjectlocal);
						if (retVal != Constants.SUCCESSFUL_OPERATION){
							exceptionCode = getResultObject(retVal);
							throw buildRuntimeCustomException(exceptionCode);
						}
						retVal = transLinesSbuDao.deleteTransLineSbuAppr(vObjectlocal);
						retVal = transLinesGlDao.deleteTransLineGlAppr(vObjectlocal);
						retVal = transLinesChannelDao.deleteTransLineChannelAppr(vObjectlocal);
						String systemDate = getSystemDate();
						vObject.setDateLastModified(systemDate);
					}
					// Set the current operation to write to audit log
					strApproveOperation = Constants.DELETE;
				}else{
					exceptionCode = getResultObject(Constants.INVALID_STATUS_FLAG_IN_DATABASE);
					throw buildRuntimeCustomException(exceptionCode);
				}	
	
				// Delete the record from the Pending table
				retVal = deleteTransLineHeaderPend(vObjectlocal);
				if (retVal != Constants.SUCCESSFUL_OPERATION){
					exceptionCode = getResultObject(retVal);
					throw buildRuntimeCustomException(exceptionCode);
				}
				retVal = transLinesSbuDao.deleteTransLineSbuPend(vObjectlocal);
				retVal = transLinesGlDao.deleteTransLineGlPend(vObjectlocal);
				retVal = transLinesChannelDao.deleteTransLineChannelPend(vObjectlocal);
				// Set the internal status to Approved
				vObject.setInternalStatus(0);
				vObject.setRecordIndicator(Constants.STATUS_ZERO);
				
			
				if (vObjectlocal.getRecordIndicator() == Constants.STATUS_DELETE && !staticDelete){
					writeAuditLog(null, oldContents);
					vObject.setRecordIndicator(-1);
				}
				else
					writeAuditLog(vObjectlocal, oldContents);
	
				}
			
			/*if(exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION){
				exceptionCode = getResultObject(Constants.AUDIT_TRAIL_ERROR);
				throw buildRuntimeCustomException(exceptionCode);
			}*/
			return getResultObject(Constants.SUCCESSFUL_OPERATION);
		}catch (UncategorizedSQLException uSQLEcxception) {
			strErrorDesc = parseErrorMsg(uSQLEcxception);
			exceptionCode = getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION);
			throw buildRuntimeCustomException(exceptionCode);
		}catch(Exception ex){
			logger.error("Error in Approve.",ex);
//			//logger.error( ((vObject==null)? "vObject is Null":vObject.toString()));
			strErrorDesc = ex.getMessage();
			exceptionCode = getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION);
			throw buildRuntimeCustomException(exceptionCode);
		}
	}
	protected RowMapper getDetailMapperFee(){
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
				vObject.setFeeType(rs.getString("FEE_TYPE"));
				vObject.setTierType(rs.getString("TIER_TYPE"));
				vObject.setFeesLineStatus (rs.getInt("FEE_LINE_STATUS"));
				vObject.setRecordIndicator(rs.getInt("RECORD_INDICATOR"));
				vObject.setRecordIndicatorDesc(rs.getString("RECORD_INDICATOR_Desc"));
				vObject.setMaker(rs.getInt("MAKER"));
				vObject.setMakerName(rs.getString("MAKER_NAME"));
				vObject.setVerifier(rs.getInt("VERIFIER"));
				vObject.setVerifierName(rs.getString("VERIFIER_NAME"));
				vObject.setDateLastModified(rs.getString("DATE_LAST_MODIFIED"));
				vObject.setDateCreation(rs.getString("DATE_CREATION"));
				vObject.setFeeBasisDesc(rs.getString("FEE_BASIS_DESC"));
				return vObject;
			}
		};
		return mapper;
	}
	public List<FeesConfigHeaderVb> doSelectPendingFeeLineRecord(FeesConfigHeaderVb dObj){
		List<FeesConfigHeaderVb> collTemp = null;
		setServiceDefaults();
		String strQueryAppr = null;
		String strQueryPend = null;
		String effectiveDate =" ";
		if(ValidationUtil.isValid(dObj.getEffectiveDate())) {
			effectiveDate = "TPend.EFFECTIVE_DATE = "+dObj.getEffectiveDate()+" AND";
		}
		if ("ORACLE".equalsIgnoreCase(databaseType)) {
			strQueryPend = new String(" SELECT TPend.COUNTRY, TPend.LE_BOOK,TPend.BUSINESS_LINE_ID,(select T1.ALPHA_SUBTAB_DESCRIPTION from ALPHA_SUB_TAB T1 where t1.Alpha_tab = TPend.FEE_BASIS_AT and T1.ALPHA_SUB_TAB=TPend.FEE_BASIS) FEE_BASIS_DESC,"+
					" (SELECT T6.BUSINESS_LINE_DESCRIPTION FROM RA_MST_BUSINESS_LINE_HEADER T6 WHERE T6.COUNTRY = TPend.COUNTRY AND "+
					" T6.LE_BOOK = TPend.LE_BOOK AND T6.BUSINESS_LINE_ID = TPend.BUSINESS_LINE_ID) BUSINESS_LINE_DESCRIPTION,"+
					" TPend.FEE_CONFIG_TYPE,TPend.FEE_BASIS,(select T1.ALPHA_SUBTAB_DESCRIPTION from ALPHA_SUB_TAB T1 where t1.Alpha_tab = TPend.FEE_CONFIG_TYPE_AT and T1.ALPHA_SUB_TAB=TPend.FEE_CONFIG_TYPE) FEE_CONFIG_TYPE_DESC"
					 +" TPend.FEE_CONFIG_CODE,"
						+ "	 Case when TPend.FEE_CONFIG_TYPE= 'D' then 'Not Applicable'                                                                 "
						+ "		  when TPend.FEE_CONFIG_TYPE= 'S' THEN (SELECT ALPHA_SUBTAB_DESCRIPTION                                                    "
						+ "			FROM ALPHA_SUB_TAB WHERE ALPHA_TAB = 3 AND ALPHA_SUB_TAB =TPend.FEE_CONFIG_CODE)                                       "
						+ "		WHEN TPend.FEE_CONFIG_TYPE= 'B' THEN (SELECT OUC_DESCRIPTION                                                                 "
						+ "			FROM RA_Ouc_Codes WHERE COUNTRY =TPend.COUNTRY AND LE_BOOK=TPend.LE_BOOK AND VISION_OUC = TPend.FEE_CONFIG_CODE)             "
						+ "		WHEN TPend.FEE_CONFIG_TYPE= 'C' THEN (SELECT CUST_FIRST_NAME                                                                   "
						+ "			FROM RA_CUSTOMERS WHERE COUNTRY =TPend.COUNTRY AND LE_BOOK=TPend.LE_BOOK AND CUSTOMER_ID = TPend.FEE_CONFIG_CODE)    "
						+ "		WHEN TPend.FEE_CONFIG_TYPE= 'A' THEN (SELECT ACCOUNT_NAME FROM RA_ACCOUNTS                                                                   "
						+ "		 WHERE COUNTRY =TPend.COUNTRY AND LE_BOOK=TPend.LE_BOOK AND ACCOUNT_NO = TPend.FEE_CONFIG_CODE)          "
						+ "		END FEE_CONFIG_CODE_DESC,                                                                                               "+
					/*" TPend.FEE_CONFIG_CODE, "+
							" CASE WHEN TPend.FEE_CONFIG_TYPE = 'A' THEN (SELECT CUSTOMER_ID FROM RA_ACCOUNTS WHERE COUNTRY = TPend.COUNTRY "
					+ " AND LE_BOOK = TPend.LE_BOOK AND ACCOUNT_NO=TPend.FEE_CONFIG_CODE) ELSE '' END CONTRACT_CUSTOMER, "+*/
					" TO_CHAR(TPend.EFFECTIVE_DATE,'DD-Mon-RRRR') EFFECTIVE_DATE,"+
					" TPend.FEE_CCY,TPend.FEE_TYPE,TPend.TIER_TYPE,"+
					" TPend.FEE_LINE_STATUS,TPend.RECORD_INDICATOR,T7.NUM_SUBTAB_DESCRIPTION RECORD_INDICATOR_DESC,                                                                                           "+
					" TPend. MAKER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "+getDbFunction("NVL")+"(TPend.MAKER,0) ) MAKER_NAME,                                  "+
					" TPend.VERIFIER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "+getDbFunction("NVL")+"(TPend.VERIFIER,0) ) VERIFIER_NAME,                          "+
					" "+getDbFunction("DATEFUNC")+"(TPend.DATE_LAST_MODIFIED, 'dd-MM-yyyy "+getDbFunction("TIME")+"') DATE_LAST_MODIFIED ,"+getDbFunction("DATEFUNC")+"(TPend.DATE_CREATION, 'dd-MM-yyyy "+getDbFunction("TIME")+"') DATE_CREATION "+                                                                                               
					" FROM RA_MST_FEES_HEADER_PEND TPend , NUM_SUB_TAB T7                                                                                 "+
					" Where  "+
					" TPend.COUNTRY = ? AND TPend.LE_BOOK = ? AND "+
					" TPend.BUSINESS_LINE_ID = ?  AND  "+effectiveDate+
					" T7.NUM_tab = TPend.RECORD_INDICATOR_NT" + 
					" and T7.NUM_sub_tab = TPend.RECORD_INDICATOR");
		}else if ("MSSQL".equalsIgnoreCase(databaseType)) {
		strQueryPend = new String(" SELECT TPend.COUNTRY, TPend.LE_BOOK,"+
				" TPend.BUSINESS_LINE_ID,(select T1.ALPHA_SUBTAB_DESCRIPTION from ALPHA_SUB_TAB T1 where t1.Alpha_tab = TPend.FEE_BASIS_AT and T1.ALPHA_SUB_TAB=TPend.FEE_BASIS) FEE_BASIS_DESC,"+
				" (SELECT T6.BUSINESS_LINE_DESCRIPTION FROM RA_MST_BUSINESS_LINE_HEADER T6 WHERE T6.COUNTRY = TPend.COUNTRY AND "+
				" T6.LE_BOOK = TPend.LE_BOOK AND T6.BUSINESS_LINE_ID = TPend.BUSINESS_LINE_ID) BUSINESS_LINE_DESCRIPTION,"+
				" TPend.FEE_CONFIG_TYPE,TPend.FEE_BASIS,(select T1.ALPHA_SUBTAB_DESCRIPTION from ALPHA_SUB_TAB T1 where t1.Alpha_tab = TPend.FEE_CONFIG_TYPE_AT and T1.ALPHA_SUB_TAB=TPend.FEE_CONFIG_TYPE) FEE_CONFIG_TYPE_DESC,"
				 +" TPend.FEE_CONFIG_CODE,"
					+ "	 Case when TPend.FEE_CONFIG_TYPE= 'D' then 'Not Applicable'                                                                 "
					+ "		  when TPend.FEE_CONFIG_TYPE= 'S' THEN (SELECT ALPHA_SUBTAB_DESCRIPTION                                                    "
					+ "			FROM ALPHA_SUB_TAB WHERE ALPHA_TAB = 3 AND ALPHA_SUB_TAB =TPend.FEE_CONFIG_CODE)                                       "
					+ "		WHEN TPend.FEE_CONFIG_TYPE= 'B' THEN (SELECT OUC_DESCRIPTION                                                                 "
					+ "			FROM RA_Ouc_Codes WHERE COUNTRY =TPend.COUNTRY AND LE_BOOK=TPend.LE_BOOK AND VISION_OUC = TPend.FEE_CONFIG_CODE)             "
					+ "		WHEN TPend.FEE_CONFIG_TYPE= 'C' THEN (SELECT CUST_FIRST_NAME                                                                   "
					+ "			FROM RA_CUSTOMERS WHERE COUNTRY =TPend.COUNTRY AND LE_BOOK=TPend.LE_BOOK AND CUSTOMER_ID = TPend.FEE_CONFIG_CODE)    "
					+ "		WHEN TPend.FEE_CONFIG_TYPE= 'A' THEN (SELECT ACCOUNT_NAME FROM RA_ACCOUNTS                                                                   "
					+ "		 WHERE COUNTRY =TPend.COUNTRY AND LE_BOOK=TPend.LE_BOOK AND ACCOUNT_NO = TPend.FEE_CONFIG_CODE)          "
					+ "		END FEE_CONFIG_CODE_DESC,                                                                                         "+
				/*" TPend.FEE_CONFIG_CODE, "+
							" CASE WHEN TPend.FEE_CONFIG_TYPE = 'A' THEN (SELECT CUSTOMER_ID FROM RA_ACCOUNTS WHERE COUNTRY = TPend.COUNTRY "
				+ " AND LE_BOOK = TPend.LE_BOOK AND ACCOUNT_NO=TPend.FEE_CONFIG_CODE) ELSE '' END CONTRACT_CUSTOMER, "+*/
				" "+getDbFunction("DATEFUNC")+"(CAST(TPend.EFFECTIVE_DATE AS DATETIME), 'dd-MMM-yyyy') EFFECTIVE_DATE,"+
				"  TPend.FEE_CCY, TPend.FEE_TYPE,TPend.TIER_TYPE, "+
				" TPend.FEE_LINE_STATUS,TPend.RECORD_INDICATOR,T7.NUM_SUBTAB_DESCRIPTION RECORD_INDICATOR_DESC,                                                                                           "+
				" TPend. MAKER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "+getDbFunction("NVL")+"(TPend.MAKER,0) ) MAKER_NAME,                                  "+
				" TPend.VERIFIER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "+getDbFunction("NVL")+"(TPend.VERIFIER,0) ) VERIFIER_NAME,                          "+
				" "+getDbFunction("DATEFUNC")+"(TPend.DATE_LAST_MODIFIED, 'dd-MM-yyyy "+getDbFunction("TIME")+"') DATE_LAST_MODIFIED ,"+getDbFunction("DATEFUNC")+"(TPend.DATE_CREATION, 'dd-MM-yyyy "+getDbFunction("TIME")+"') DATE_CREATION "+                                                                                               
				" FROM RA_MST_FEES_HEADER_PEND TPend , NUM_SUB_TAB T7                                                                                 "+
				" Where   "+
				" TPend.COUNTRY = ? AND TPend.LE_BOOK = ?  AND "+
				" TPend.BUSINESS_LINE_ID = ?  AND "+effectiveDate+
				" T7.NUM_tab = TPend.RECORD_INDICATOR_NT" + 
				" and T7.NUM_sub_tab = TPend.RECORD_INDICATOR");
	}
		
		Object objParams[] = new Object[4];
		objParams[0] = new String(dObj.getCountry());// country
		objParams[1] = new String(dObj.getLeBook());
		objParams[2] = new String(dObj.getBusinessLineId());
//		objParams[3] = new String(dObj.getEffectiveDate());
		
		try
		{
				logger.info("Executing pending query");
				collTemp = getJdbcTemplate().query(strQueryPend.toString(),objParams,getDetailMapperFee());
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

	
	
	public List<BusinessLineHeaderVb> doSelectPendingBusinessLineRecord(BusinessLineHeaderVb dObj) {
			List<BusinessLineHeaderVb> collTemp = null;
			final int intKeyFieldsCount = 4;
			setServiceDefaults();
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
	@Override
	protected String getAuditString(TransLineHeaderVb vObject){
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
			
			if(ValidationUtil.isValid(vObject.getTransLineId()))
				strAudit.append("TRANS_LINE_ID"+auditDelimiterColVal+vObject.getTransLineId().trim());
			else
				strAudit.append("TRANS_LINE_ID"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			
			if(ValidationUtil.isValid(vObject.getTransLineDescription()))
				strAudit.append("TRANS_LINE_DESCRIPTION"+auditDelimiterColVal+vObject.getTransLineDescription().trim());
			else
				strAudit.append("TRANS_LINE_DESCRIPTION"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			
			strAudit.append("TRANS_LINE_PROD_SUB_TYPE"+auditDelimiterColVal+vObject.getTransLineSubType());
			strAudit.append(auditDelimiter);
			
			strAudit.append("TRANS_LINE_SERV_SUB_TYPE"+auditDelimiterColVal+vObject.getTransLineSubType());
			strAudit.append(auditDelimiter);
			
			strAudit.append("TRANS_LINE_PROD_GRP"+auditDelimiterColVal+vObject.getTransLineGrp());
			strAudit.append(auditDelimiter);
			
			strAudit.append("TRANS_LINE_SERV_GRP"+auditDelimiterColVal+vObject.getTransLineGrp());
			strAudit.append(auditDelimiter);
			
			strAudit.append("EXTRACTION_FREQUENCY"+auditDelimiterColVal+vObject.getExtractionFrequency());
			strAudit.append(auditDelimiter);
			
			if(ValidationUtil.isValid(vObject.getExtractionMonthDay()))
				strAudit.append("EXTRACTION_MONTH_DAY"+auditDelimiterColVal+vObject.getExtractionMonthDay().trim());
			else
				strAudit.append("EXTRACTION_MONTH_DAY"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			
			strAudit.append("TARGET_STG_TABLE_ID"+auditDelimiterColVal+vObject.getTargetStgTableId());
			strAudit.append(auditDelimiter);
			
			strAudit.append("TRANS_LINE_STATUS_NT"+auditDelimiterColVal+vObject.getTransLineStatusNT());
			strAudit.append(auditDelimiter);
			
			strAudit.append("TRANS_LINE_STATUS"+auditDelimiterColVal+vObject.getTransLineStatus());
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
	
	public String getTranBusinessLinePendId(String country,String leBook,String tranLineId) {
		Object args[] = {country,leBook,tranLineId};
		String orginalQuery = "SELECT BUSINESS_LINE_ID FROM RA_MST_BUSINESS_LINE_HEADER_PEND WHERE COUNTRY = ? AND LE_BOOK = ? "+
				" AND TRANS_LINE_ID = ?";
		return getJdbcTemplate().queryForObject(orginalQuery,args,String.class);		
	}
	
	/*private RowMapper getRowMapperForReview(){
		RowMapper mapper = new RowMapper() {
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
	}*/
	public String getErrorMsgTransLine(String serviceDesc) {
		ExceptionCode exceptionCode=null;
		int index =exceptionCode.getErrorMsg().trim().indexOf(serviceDesc+" - Approve - Failed -");
		if(index >=0 ){
			strErrorDesc +=" "+exceptionCode.getErrorMsg().replaceFirst(serviceDesc+" - Approve - Failed -", "-");
		}else{
			strErrorDesc +=" "+exceptionCode.getErrorMsg();
		}
		
		return strErrorDesc;
	}
	
	protected String frameErrorMessage(BusinessLineHeaderVb vObject, String strOperation){
		// specify all the key fields and their values first
		String strErrMsg = new String("");
		try{
			strErrMsg =  strErrMsg + "TRANSLINE_ID: " + vObject.getTransLineId();
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
	@Transactional(rollbackForClassName={"com.vision.exception.RuntimeCustomException"})
	public ExceptionCode bulkApprove(List<TransLineHeaderVb> vObjects,boolean staticDelete)throws RuntimeCustomException {
		strErrorDesc  = "";
		strCurrentOperation = Constants.APPROVE;
		ExceptionCode exceptionCode = null;
		setServiceDefaults();
		try {
			boolean foundFlag = false;
			for(TransLineHeaderVb object : vObjects){
				if (object.getRecordIndicator() > 0 && object.isChecked()){
					foundFlag = true;
					strErrorDesc = frameErrorMessage(object, Constants.APPROVE);
					exceptionCode = doApproveForTransaction(object,staticDelete);
					if (exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION){
						int index =exceptionCode.getErrorMsg().trim().indexOf(serviceDesc+" - Approve - Failed -");
						if(index >=0 ){
							strErrorDesc +=" "+exceptionCode.getErrorMsg().replaceFirst(serviceDesc+" - Approve - Failed -", "-");
						}else{
							strErrorDesc +=" "+exceptionCode.getErrorMsg();
						}
						break;
					}
				}
			}
			if (foundFlag == false){
				logger.error("No Records To Approve");
				exceptionCode = getResultObject(Constants.NO_RECORDS_TO_APPROVE);
				throw buildRuntimeCustomException(exceptionCode);
			}
			// When it has come out of the loop, check whether it has exited successfully or with error
			if (exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION){
				logger.error("Error in Bulk Approve. "+exceptionCode.getErrorMsg());
				exceptionCode = getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION);
				throw buildRuntimeCustomException(exceptionCode);
			}
			return exceptionCode;
		}catch (UncategorizedSQLException uSQLEcxception) {
			strErrorDesc = parseErrorMsg(uSQLEcxception);
			exceptionCode = getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION);
			throw buildRuntimeCustomException(exceptionCode);
		}catch(Exception ex){
			//logger.error("Error in Bulk Approve.",ex);
			strErrorDesc = ex.getMessage();
			exceptionCode = getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION);
			throw buildRuntimeCustomException(exceptionCode);
		}
	}
	
	@Override
	protected ExceptionCode doDeleteRecordForNonTrans(TransLineHeaderVb vObject) throws RuntimeCustomException {
		TransLineHeaderVb vObjectlocal = null;
		List<TransLineHeaderVb> collTemp = null;
		ExceptionCode exceptionCode = null;
		strApproveOperation = Constants.DELETE;
		strErrorDesc  = "";
		strCurrentOperation = Constants.DELETE;
		setServiceDefaults();
		collTemp = selectApprovedRecord(vObject);
	
	
		if (collTemp == null){
			logger.error("Collection is null");
			exceptionCode = getResultObject(Constants.ERRONEOUS_OPERATION);
			throw buildRuntimeCustomException(exceptionCode);
		}
		// If record already exists in the approved table, reject the addition
		if (!collTemp.isEmpty()) {
			vObjectlocal = ((ArrayList<TransLineHeaderVb>) collTemp).get(0);
			int intStaticDeletionFlag = getStatus(vObjectlocal);
			if (intStaticDeletionFlag == Constants.PASSIVATE){
				exceptionCode = getResultObject(Constants.CANNOT_DELETE_AN_INACTIVE_RECORD);
				throw buildRuntimeCustomException(exceptionCode);
			}
		}
		else{
			exceptionCode = getResultObject(Constants.ATTEMPT_TO_DELETE_UNEXISTING_RECORD);
			throw buildRuntimeCustomException(exceptionCode);
		}

		// check to see if the record already exists in the pending table
		collTemp = doSelectPendingRecord(vObject);
		if (collTemp == null){
			exceptionCode = getResultObject(Constants.ERRONEOUS_OPERATION);
			throw buildRuntimeCustomException(exceptionCode);
		}

		// If records are there, check for the status and decide what error to return
		// back
		if (!collTemp.isEmpty()) {
			exceptionCode = getResultObject(Constants.TRYING_TO_DELETE_APPROVAL_PENDING_RECORD);
			throw buildRuntimeCustomException(exceptionCode);
		}

		// insert the record into pending table with status 3 - deletion
		if(vObjectlocal==null){
			exceptionCode = getResultObject(Constants.ERRONEOUS_OPERATION);
			throw buildRuntimeCustomException(exceptionCode);
		}
		if (!collTemp.isEmpty()) {
			vObjectlocal = ((ArrayList<TransLineHeaderVb>) collTemp).get(0);
		    vObjectlocal.setDateCreation(vObject.getDateCreation());
		}
		//vObjectlocal.setDateCreation(vObject.getDateCreation());
		vObjectlocal.setMaker(getIntCurrentUserId());
		vObjectlocal.setRecordIndicator(Constants.STATUS_DELETE);
		vObjectlocal.setVerifier(0);
		retVal = doInsertionPendTransLineHeadersDc(vObjectlocal);
		if (retVal != Constants.SUCCESSFUL_OPERATION){
			exceptionCode = getResultObject(retVal);
			throw buildRuntimeCustomException(exceptionCode);
		}
		vObject.setRecordIndicator(Constants.STATUS_DELETE);
		vObject.setVerifier(0);
		if (vObject.isBusinessFlag() || vObject.isFeesFlag()) {
			BusinessLineHeaderVb businessLineHeaderVb = new BusinessLineHeaderVb();
			businessLineHeaderVb.setCountry(vObject.getCountry());
			businessLineHeaderVb.setLeBook(vObject.getLeBook());
			businessLineHeaderVb.setTransLineId(vObject.getTransLineId());
			List<BusinessLineHeaderVb> businessLineLst = businessLineConfigDao
					.getQueryResults(businessLineHeaderVb,Constants.STATUS_ZERO);
			for (BusinessLineHeaderVb businessLineVb : businessLineLst) {
				businessLineVb.setFeesFlag(vObject.isFeesFlag());
				businessLineVb.setBusinessFlag(vObject.isBusinessFlag());
					exceptionCode = businessLineConfigDao.doDeleteRecordForNonTrans(businessLineVb);
				
		
				
			}
			
		}
//		exceptionCode = businessLineConfigDao.doDeleteRecordForNonTrans(businessLineHeaderVb);
//		logger.info(exceptionCode.getErrorMsg());
		return getResultObject(Constants.SUCCESSFUL_OPERATION);
	}
	@Override
	protected ExceptionCode doDeleteApprRecordForNonTrans(TransLineHeaderVb vObject) throws RuntimeCustomException {
		List<TransLineHeaderVb> collTemp = null;
		ExceptionCode exceptionCode = null;
		strApproveOperation = Constants.DELETE;
		strErrorDesc  = "";
		strCurrentOperation = Constants.DELETE;
		setServiceDefaults();
		TransLineHeaderVb vObjectlocal = null;
		String systemDate = getSystemDate();
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
			int intStaticDeletionFlag = getStatus(((ArrayList<TransLineHeaderVb>) collTemp).get(0));
			if (intStaticDeletionFlag == Constants.PASSIVATE) {
				exceptionCode = getResultObject(Constants.CANNOT_DELETE_AN_INACTIVE_RECORD);
				throw buildRuntimeCustomException(exceptionCode);
			}
		}
		else{
			exceptionCode = getResultObject(Constants.ATTEMPT_TO_DELETE_UNEXISTING_RECORD);
			throw buildRuntimeCustomException(exceptionCode);
		}
		vObjectlocal = ((ArrayList<TransLineHeaderVb>)collTemp).get(0);
		vObject.setDateCreation(vObjectlocal.getDateCreation());
		if(vObject.isStaticDelete()){
			vObjectlocal.setMaker(getIntCurrentUserId());
			vObject.setVerifier(getIntCurrentUserId());
			vObject.setRecordIndicator(Constants.STATUS_ZERO);
//			setStatus(vObject, Constants.PASSIVATE);
			setStatus(vObjectlocal, Constants.PASSIVATE);
			vObjectlocal.setVerifier(getIntCurrentUserId());
			vObjectlocal.setRecordIndicator(Constants.STATUS_ZERO);
			retVal = doUpdateApprHeader(vObjectlocal);

			vObject.setDateLastModified(systemDate);
		}else{
			// delete the record from the Approve Table
			retVal = deleteTransLineHeaderAppr(vObject);
//			vObject.setRecordIndicator(-1);
			vObject.setDateLastModified(systemDate);
			List<TransLineSbuVb> collTempSbu = null;
			collTempSbu = transLinesSbuDao.getTransSbuDetails(vObject, 0);
			if (collTempSbu != null && !collTempSbu.isEmpty()) {
				int delCnt = transLinesSbuDao.deleteTransLineSbuAppr(vObject);
			}
			
			List<TransLineGLVb> collTempGL = null;
			collTempGL = transLinesGlDao.getTransGLDetails(vObject, 0);
			if (collTempGL != null && !collTempGL.isEmpty()) {
				int delCnt = transLinesGlDao.deleteTransLineGlAppr(vObject);
			}
			List<TransLineChannelVb> collTempChannel = null;
			collTempChannel = transLinesChannelDao.getTransChannelDetails(vObject, 0);
			if (collTempChannel != null && !collTempChannel.isEmpty()) {
				int delCnt = transLinesChannelDao.deleteTransLineChannelAppr(vObject);
			}

			BusinessLineHeaderVb businessLineHeaderVb = new BusinessLineHeaderVb();
			businessLineHeaderVb.setCountry(vObject.getCountry());
			businessLineHeaderVb.setLeBook(vObject.getLeBook());
			businessLineHeaderVb.setTransLineId(vObject.getTransLineId());
			List<BusinessLineHeaderVb> businessLinelst = businessLineConfigDao.getQueryResults(businessLineHeaderVb,Constants.STATUS_PENDING);
			List<BusinessLineHeaderVb> collTempBl = businessLineConfigDao.getQueryResults(businessLineHeaderVb,Constants.STATUS_ZERO);
			if (collTempBl != null && !collTempBl.isEmpty()) {
				businessLinelst.addAll(collTempBl);
			}

			if (businessLinelst != null && !businessLinelst.isEmpty()) {
				for (BusinessLineHeaderVb businessLineVb : businessLinelst) {
					if (businessLineVb.getRecordIndicator() == Constants.STATUS_ZERO) {
						retVal = businessLineConfigDao.deleteBusinessLineHeaderAppr(businessLineVb);
						retVal = businessLineConfigDao.getBusinessLineConfigGLDao().deleteBusinessLineGlAppr(businessLineVb);
						retVal = businessLineConfigDao.getBlReconRuleDao().doDeleteApprBlReconRule(businessLineVb);
						businessLineVb.setDateLastModified(systemDate);
						businessLineConfigDao.writeAuditLog(null, businessLineVb);
					}

					// Pend
					retVal = businessLineConfigDao.deleteBusinessLineHeaderPend(businessLineVb);
					retVal = businessLineConfigDao.getBusinessLineConfigGLDao().deleteBusinessLineGlPend(businessLineVb);
					retVal = businessLineConfigDao.getBlReconRuleDao().doDeletePendBlReconRule(businessLineVb);

					// fees
					FeesConfigHeaderVb feesConfigHeaderVb = new FeesConfigHeaderVb();
					feesConfigHeaderVb.setCountry(businessLineVb.getCountry());
					feesConfigHeaderVb.setLeBook(businessLineVb.getLeBook());
					feesConfigHeaderVb.setBusinessLineId(businessLineVb.getBusinessLineId());
					List<FeesConfigHeaderVb> feesLineDatalst = feesConfigHeadersDao.getQueryResults(feesConfigHeaderVb,
							Constants.STATUS_PENDING);
					List<FeesConfigHeaderVb> collTempFl = feesConfigHeadersDao.getQueryResults(feesConfigHeaderVb,
							Constants.STATUS_ZERO);
					if (collTempFl != null && !collTempFl.isEmpty()) {
						feesLineDatalst.addAll(collTempFl);
					}
					if (feesLineDatalst != null && !feesLineDatalst.isEmpty()) {
						for (FeesConfigHeaderVb feesConfigVb : feesLineDatalst) {
							if (feesConfigVb.getRecordIndicator() == Constants.STATUS_ZERO) {
								retVal = feesConfigHeadersDao.deleteFeesHeaderAppr(feesConfigVb);
								retVal = feesConfigHeadersDao.getFeesConfigDetailsDao().deleteFeesDetailsApprMain(feesConfigVb);
								retVal = feesConfigHeadersDao.getFeesConfigTierDao().deleteFeesTierApprMain(feesConfigVb);
								feesConfigVb.setDateLastModified(systemDate);
								feesConfigHeadersDao.writeAuditLog(null, feesConfigVb);
							}

							// Pend
							retVal = feesConfigHeadersDao.deleteFeesHeaderPend(feesConfigVb);
							retVal = feesConfigHeadersDao.getFeesConfigDetailsDao().deleteFeesDetailsPendMain(feesConfigVb);
							retVal = feesConfigHeadersDao.getFeesConfigTierDao().deleteFeesTierPendMain(feesConfigVb);
						}
					}
				}
			}

		}
//		if (retVal != Constants.SUCCESSFUL_OPERATION) {
//			exceptionCode = getResultObject(retVal);
//			throw buildRuntimeCustomException(exceptionCode);
//		}else {
		exceptionCode = getResultObject(Constants.SUCCESSFUL_OPERATION);
//		}
		if (vObject.isStaticDelete()) {
			setStatus(vObjectlocal, Constants.STATUS_ZERO);
			setStatus(vObject, Constants.PASSIVATE);
			writeAuditLog(vObject, vObjectlocal);
		} else {
			writeAuditLog(null, vObject);
			vObject.setRecordIndicator(-1);
		}
		BusinessLineHeaderVb businessLineHeaderVb = new BusinessLineHeaderVb();
		businessLineHeaderVb.setCountry(vObject.getCountry());
		businessLineHeaderVb.setLeBook(vObject.getLeBook());
		businessLineHeaderVb.setTransLineId(vObject.getTransLineId());

		if (vObject.isBusinessFlag() || vObject.isFeesFlag()) {
			List<BusinessLineHeaderVb> businessLineLst = businessLineConfigDao.getQueryResults(businessLineHeaderVb,Constants.STATUS_ZERO);
			for (BusinessLineHeaderVb businessLineVb : businessLineLst) {
				businessLineVb.setFeesFlag(vObject.isFeesFlag());
				businessLineVb.setBusinessFlag(vObject.isBusinessFlag());
				businessLineVb.setVerificationRequired(vObject.isVerificationRequired());
				businessLineVb.setStaticDelete(vObject.isStaticDelete());
					exceptionCode = businessLineConfigDao.doDeleteApprRecordForNonTrans(businessLineVb);
			}
		}

		if (exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION) {
			exceptionCode = getResultObject(Constants.AUDIT_TRAIL_ERROR);
			throw buildRuntimeCustomException(exceptionCode);
		}
		return exceptionCode;
	}
}