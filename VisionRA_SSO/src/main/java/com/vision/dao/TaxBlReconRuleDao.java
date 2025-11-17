package com.vision.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.vision.authentication.CustomContextHolder;
import com.vision.exception.ExceptionCode;
import com.vision.exception.RuntimeCustomException;
import com.vision.util.Constants;
import com.vision.util.ValidationUtil;
import com.vision.vb.BlReconRuleVb;
import com.vision.vb.BusinessLineHeaderVb;

@Component
public class TaxBlReconRuleDao extends AbstractDao<BlReconRuleVb> {
	@Override
	protected void setServiceDefaults() {
		serviceName = "BusinessLineTaxReconRule";
		serviceDesc = "Business Line Tax Recon Rule";
		tableName = "RA_BL_RECON_RULE_TAX";
		childTableName = "RA_BL_RECON_RULE_TAX";
		intCurrentUserId = CustomContextHolder.getContext().getVisionId();
	}

	public List<BlReconRuleVb> getTaxBlReconRuleDetails(BusinessLineHeaderVb vObject, int intStatus) {
		List<BlReconRuleVb> collTemp = null;
		String query = "";
		try {
			if (!vObject.isVerificationRequired() || vObject.isReview()) {
				intStatus = 0;
			}
			if (intStatus == Constants.STATUS_ZERO) {
				query = " SELECT TAPPR.COUNTRY,                                             "
						+ "        TAPPR.LE_BOOK,                                             "
						+ "        TAPPR.BUSINESS_LINE_ID,                                    "
						+ "        TAPPR.RECON_TYPE_AT,                                       "
						+ "        TAPPR.RECON_TYPE,                                          "
						+ "        TAPPR.RECON_METHOD_AT,                                     "
						+ "        TAPPR.RECON_METHOD,                                        "
						+ "        TAPPR.RULE_ID_AT,                                          "
						+ "        TAPPR.RULE_ID,                                             "
						+ "        TAPPR.RECON_GRACE_DAYS,                                    "
						+ "        TAPPR.RECON_MATCH_TYPE_AT,                                       "
						+ "        TAPPR.RECON_MATCH_TYPE,                                          "
						+ "        TAPPR.RULE_PRIORITY,                                       "
						+ "        TAPPR.STATUS_NT,                                           "
						+ "        TAPPR.STATUS,                                              "
						+ "        TAPPR.RECORD_INDICATOR_NT,                                 "
						+ "        TAPPR.RECORD_INDICATOR,                                    "
						+ "        TAPPR.MAKER,                                               "
						+ "        TAPPR.VERIFIER,                                            "
						+ "        TAPPR.INTERNAL_STATUS,                                     "
						+ "        TAPPR.DATE_LAST_MODIFIED,                                  "
						+ "        TAPPR.DATE_CREATION                                        "
						+ "   FROM RA_BL_RECON_RULE_TAX TAPPR                                  "
						+ "  WHERE TAPPR.COUNTRY = ? AND TAPPR.LE_BOOK = ? AND TAPPR.BUSINESS_LINE_ID = ? order by TAPPR.RULE_PRIORITY";
			} else {
				query = " SELECT TPEND.COUNTRY,                                             "
						+ "        TPEND.LE_BOOK,                                             "
						+ "        TPEND.BUSINESS_LINE_ID,                                    "
						+ "        TPEND.RECON_TYPE_AT,                                       "
						+ "        TPEND.RECON_TYPE,                                          "
						+ "        TPEND.RECON_METHOD_AT,                                     "
						+ "        TPEND.RECON_METHOD,                                        "
						+ "        TPEND.RULE_ID_AT,                                          "
						+ "        TPEND.RULE_ID,                                             "
						+ "        TPEND.RECON_GRACE_DAYS,                                    "
						+ "        TPEND.RECON_MATCH_TYPE_AT,                                       "
						+ "        TPEND.RECON_MATCH_TYPE,                                          "
						+ "        TPEND.RULE_PRIORITY,                                       "
						+ "        TPEND.STATUS_NT,                                           "
						+ "        TPEND.STATUS,                                              "
						+ "        TPEND.RECORD_INDICATOR_NT,                                 "
						+ "        TPEND.RECORD_INDICATOR,                                    "
						+ "        TPEND.MAKER,                                               "
						+ "        TPEND.VERIFIER,                                            "
						+ "        TPEND.INTERNAL_STATUS,                                     "
						+ "        TPEND.DATE_LAST_MODIFIED,                                  "
						+ "        TPEND.DATE_CREATION                                        "
						+ "   FROM RA_BL_RECON_RULE_TAX_PEND TPEND                                  "
						+ "  WHERE TPEND.COUNTRY = ? AND TPEND.LE_BOOK = ? AND TPEND.BUSINESS_LINE_ID = ? order by TPEND.RULE_PRIORITY";
			}

			Object objParams[] = new Object[3];
			objParams[0] = new String(vObject.getCountry());// country
			objParams[1] = new String(vObject.getLeBook());// Le_book
			objParams[2] = new String(vObject.getBusinessLineId());//BusinessLineId
			collTemp = getJdbcTemplate().query(query, objParams, getBusinessLineReconRuleMapper());
			return collTemp;
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("Exception ");
			return null;
		}
	}

	protected RowMapper getBusinessLineReconRuleMapper() {
		RowMapper mapper = new RowMapper() {
			@Override
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				BlReconRuleVb blReconRuleVb = new BlReconRuleVb();
				blReconRuleVb.setCountry(rs.getString("COUNTRY"));
				blReconRuleVb.setLeBook(rs.getString("LE_BOOK"));
				blReconRuleVb.setBusinessLineId(rs.getString("BUSINESS_LINE_ID"));
				blReconRuleVb.setReconType(rs.getString("RECON_TYPE"));
				blReconRuleVb.setReconMethod(rs.getString("RECON_METHOD"));
				blReconRuleVb.setRuleId(rs.getString("RULE_ID"));
				blReconRuleVb.setReconGraceDays(rs.getInt("RECON_GRACE_DAYS"));
				blReconRuleVb.setReconMatchType(rs.getString("RECON_MATCH_TYPE"));
				blReconRuleVb.setRulePriority(rs.getInt("RULE_PRIORITY"));
				blReconRuleVb.setReconStatus(rs.getInt("STATUS"));
				blReconRuleVb.setRecordIndicator(rs.getInt("RECORD_INDICATOR"));
				return blReconRuleVb;
			}
		};
		return mapper;
	}

	protected int doInsertApprTaxBlReconRule(BlReconRuleVb vObject) {
		String query = " INSERT INTO RA_BL_RECON_RULE_TAX (COUNTRY,LE_BOOK,BUSINESS_LINE_ID, "
				+ "  RECON_TYPE_AT,RECON_TYPE,RECON_MATCH_TYPE_AT,RECON_MATCH_TYPE,                                      "
				+ "  RECON_METHOD_AT,RECON_METHOD,                                 "
				+ "  RULE_ID_AT,RULE_ID,                                           "
				+ "  RECON_GRACE_DAYS,RULE_PRIORITY,                               "
				+ "  STATUS_NT,STATUS,                                             "
				+ "  RECORD_INDICATOR_NT,RECORD_INDICATOR,                         "
				+ "  MAKER,VERIFIER,                                               "
				+ "  INTERNAL_STATUS,DATE_LAST_MODIFIED,DATE_CREATION)             "
				+ "  VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, "  + getDbFunction("SYSDATE")
				+ "," + getDbFunction("SYSDATE") + ")   ";
		Object[] args = { vObject.getCountry(), vObject.getLeBook(), vObject.getBusinessLineId(),
				vObject.getReconTypeAT(), vObject.getReconType(), vObject.getReconMatchTypeAT(),
				vObject.getReconMatchType(),vObject.getReconMethodAT(), vObject.getReconMethod(),
				vObject.getRuleIdAT(), vObject.getRuleId(), vObject.getReconGraceDays(), vObject.getRulePriority(),
				vObject.getReconStatusNt(), vObject.getReconStatus(), vObject.getRecordIndicatorNt(),
				vObject.getRecordIndicator(), vObject.getMaker(), vObject.getVerifier(), vObject.getInternalStatus() };
		return getJdbcTemplate().update(query, args);

	}

	protected int doInsertPendTaxBlReconRule(BlReconRuleVb vObject) {
		String query = " INSERT INTO RA_BL_RECON_RULE_TAX_PEND (COUNTRY,LE_BOOK,BUSINESS_LINE_ID, "
				+ "  RECON_TYPE_AT,RECON_TYPE,RECON_MATCH_TYPE_AT,RECON_MATCH_TYPE,                                      "
				+ "  RECON_METHOD_AT,RECON_METHOD,                                 "
				+ "  RULE_ID_AT,RULE_ID,                                           "
				+ "  RECON_GRACE_DAYS,RULE_PRIORITY,                               "
				+ "  STATUS_NT,STATUS,                                             "
				+ "  RECORD_INDICATOR_NT,RECORD_INDICATOR,                         "
				+ "  MAKER,VERIFIER,                                               "
				+ "  INTERNAL_STATUS,DATE_LAST_MODIFIED,DATE_CREATION)             "
				+ "  VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, "  + getDbFunction("SYSDATE")
				+ "," + getDbFunction("SYSDATE") + ")   ";
		Object[] args = { vObject.getCountry(), vObject.getLeBook(), vObject.getBusinessLineId(),
				vObject.getReconTypeAT(), vObject.getReconType(), vObject.getReconMatchTypeAT(),
				vObject.getReconMatchType(),vObject.getReconMethodAT(), vObject.getReconMethod(),
				vObject.getRuleIdAT(), vObject.getRuleId(), vObject.getReconGraceDays(), vObject.getRulePriority(),
				vObject.getReconStatusNt(), vObject.getReconStatus(), vObject.getRecordIndicatorNt(),
				vObject.getRecordIndicator(), vObject.getMaker(), vObject.getVerifier(), vObject.getInternalStatus() };
		return getJdbcTemplate().update(query, args);

	}

	protected int doInsertTaxBlReconRuleHis(BlReconRuleVb vObject) {
		String query = " INSERT INTO RA_BL_RECON_RULE_TAX_HIS (REF_NO,COUNTRY,LE_BOOK,BUSINESS_LINE_ID, "
				+ "  RECON_TYPE_AT,RECON_TYPE,RECON_MATCH_TYPE_AT,RECON_MATCH_TYPE,                                      "
				+ "  RECON_METHOD_AT,RECON_METHOD,                                 "
				+ "  RULE_ID_AT,RULE_ID,                                           "
				+ "  RECON_GRACE_DAYS,RULE_PRIORITY,                               "
				+ "  STATUS_NT,STATUS,                                             "
				+ "  RECORD_INDICATOR_NT,RECORD_INDICATOR,                         "
				+ "  MAKER,VERIFIER,                                               "
				+ "  INTERNAL_STATUS,DATE_LAST_MODIFIED,DATE_CREATION)             "
				+ "  VALUES (?,?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, "  + getDbFunction("SYSDATE")
				+ "," + getDbFunction("SYSDATE") + ")   ";
		Object[] args = { vObject.getRefNo(),vObject.getCountry(), vObject.getLeBook(), vObject.getBusinessLineId(),
				vObject.getReconTypeAT(), vObject.getReconType(), vObject.getReconMatchTypeAT(),
				vObject.getReconMatchType(),vObject.getReconMethodAT(), vObject.getReconMethod(),
				vObject.getRuleIdAT(), vObject.getRuleId(), vObject.getReconGraceDays(), vObject.getRulePriority(),
				vObject.getReconStatusNt(), vObject.getReconStatus(), vObject.getRecordIndicatorNt(),
				vObject.getRecordIndicator(), vObject.getMaker(), vObject.getVerifier(), vObject.getInternalStatus() };
		return getJdbcTemplate().update(query, args);

	}

	protected int doDeleteApprTaxBlReconRule(BusinessLineHeaderVb vObject) {
		String query = " DELETE FROM RA_BL_RECON_RULE_TAX WHERE COUNTRY = ? AND LE_BOOK = ?  "
				+ " AND BUSINESS_LINE_ID = ?     ";
		Object[] args = { vObject.getCountry(), vObject.getLeBook(), vObject.getBusinessLineId() };
		return getJdbcTemplate().update(query, args);
	}

	protected int doDeletePendTaxBlReconRule(BusinessLineHeaderVb vObject) {
		String query = " DELETE FROM RA_BL_RECON_RULE_TAX_PEND WHERE COUNTRY = ? AND LE_BOOK = ?  "
				+ " AND BUSINESS_LINE_ID = ?     ";
		Object[] args = { vObject.getCountry(), vObject.getLeBook(), vObject.getBusinessLineId() };
		return getJdbcTemplate().update(query, args);
	}

	public ExceptionCode deleteAndInsertApprTaxBlReconRule(BusinessLineHeaderVb vObject) throws RuntimeCustomException {
		ExceptionCode exceptionCode = null;
		ArrayList<BlReconRuleVb> blReconRulelst = (ArrayList<BlReconRuleVb>) vObject.getTaxBlReconRulelst();
		List<BlReconRuleVb> collTemp = null;
		collTemp = getTaxBlReconRuleDetails(vObject, Constants.STATUS_ZERO);
		if (collTemp != null && collTemp.size() > 0) {
			collTemp.forEach(blReconRule ->{
				blReconRule.setRefNo(getMaxSequence());
				doInsertTaxBlReconRuleHis(blReconRule);
			});
			int delCnt = doDeleteApprTaxBlReconRule(vObject);
		}
		for (BlReconRuleVb blReconRuleVb : blReconRulelst) {
			blReconRuleVb.setCountry(vObject.getCountry());
			blReconRuleVb.setLeBook(vObject.getLeBook());
			blReconRuleVb.setBusinessLineId(vObject.getBusinessLineId());
			blReconRuleVb.setRecordIndicator(vObject.getRecordIndicator());
			blReconRuleVb.setMaker(intCurrentUserId);
			blReconRuleVb.setVerifier(intCurrentUserId);
			// blReconRuleVb.setReconStatus(vObject.getBusinessLineStatus());
			retVal = doInsertApprTaxBlReconRule(blReconRuleVb);
			if (retVal != Constants.SUCCESSFUL_OPERATION) {
				exceptionCode = getResultObject(retVal);
				throw buildRuntimeCustomException(exceptionCode);
			} else {
				exceptionCode = getResultObject(Constants.SUCCESSFUL_OPERATION);
			}
			writeAuditLog(blReconRuleVb, null);
		}
		return exceptionCode;
	}

	public ExceptionCode deleteAndInsertPendTaxBlReconRule(BusinessLineHeaderVb vObject) throws RuntimeCustomException {
		ExceptionCode exceptionCode = null;
		ArrayList<BlReconRuleVb> blReconRulelst = (ArrayList<BlReconRuleVb>) vObject.getTaxBlReconRulelst();
		List<BlReconRuleVb> collTemp = null;
		collTemp = getTaxBlReconRuleDetails(vObject, Constants.SUCCESSFUL_OPERATION);
		if (collTemp != null && collTemp.size() > 0) {
			int delCnt = doDeletePendTaxBlReconRule(vObject);
		}
		for (BlReconRuleVb blReconRuleVb : blReconRulelst) {
			blReconRuleVb.setCountry(vObject.getCountry());
			blReconRuleVb.setLeBook(vObject.getLeBook());
			blReconRuleVb.setBusinessLineId(vObject.getBusinessLineId());
			blReconRuleVb.setRecordIndicator(vObject.getRecordIndicator());
			blReconRuleVb.setMaker(intCurrentUserId);
			blReconRuleVb.setVerifier(intCurrentUserId);
			// blReconRuleVb.setReconStatus(vObject.getBusinessLineStatus());
			retVal = doInsertPendTaxBlReconRule(blReconRuleVb);
			if (retVal != Constants.SUCCESSFUL_OPERATION) {
				exceptionCode = getResultObject(retVal);
				throw buildRuntimeCustomException(exceptionCode);
			} else {
				exceptionCode = getResultObject(Constants.SUCCESSFUL_OPERATION);
			}
			writeAuditLog(blReconRuleVb, null);
		}
		return exceptionCode;
	}

	@Override
	protected String getAuditString(BlReconRuleVb vObject) {
		final String auditDelimiter = vObject.getAuditDelimiter();
		final String auditDelimiterColVal = vObject.getAuditDelimiterColVal();
		StringBuffer strAudit = new StringBuffer("");
		try {
			if (ValidationUtil.isValid(vObject.getCountry()))
				strAudit.append("COUNTRY" + auditDelimiterColVal + vObject.getCountry().trim());
			else
				strAudit.append("COUNTRY" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);

			if (ValidationUtil.isValid(vObject.getLeBook()))
				strAudit.append("LE_BOOK" + auditDelimiterColVal + vObject.getLeBook().trim());
			else
				strAudit.append("LE_BOOK" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);

			if (ValidationUtil.isValid(vObject.getBusinessLineId()))
				strAudit.append("BUSINESS_LINE_ID" + auditDelimiterColVal + vObject.getBusinessLineId().trim());
			else
				strAudit.append("BUSINESS_LINE_ID" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);

			strAudit.append("RULE_TYPE_AT" + auditDelimiterColVal + vObject.getReconTypeAT());
			strAudit.append(auditDelimiter);

			if (ValidationUtil.isValid(vObject.getReconType()))
				strAudit.append("RECON_TYPE" + auditDelimiterColVal + vObject.getReconType().trim());
			else
				strAudit.append("RECON_TYPE" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);

			strAudit.append("RULE_METHOD_AT" + auditDelimiterColVal + vObject.getReconMethodAT());
			strAudit.append(auditDelimiter);

			if (ValidationUtil.isValid(vObject.getReconMethod()))
				strAudit.append("RECON_METHOD" + auditDelimiterColVal + vObject.getReconMethod().trim());
			else
				strAudit.append("RECON_METHOD" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);

			strAudit.append("RULE_ID_AT" + auditDelimiterColVal + vObject.getRuleIdAT());
			strAudit.append(auditDelimiter);

			if (ValidationUtil.isValid(vObject.getRuleId()))
				strAudit.append("RULE_ID" + auditDelimiterColVal + vObject.getRuleId().trim());
			else
				strAudit.append("RULE_ID" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);

			if (ValidationUtil.isValid(vObject.getReconGraceDays()))
				strAudit.append("RECON_GRACE_DAYS" + auditDelimiterColVal + vObject.getReconGraceDays());
			else
				strAudit.append("RECON_GRACE_DAYS" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);

			if (ValidationUtil.isValid(vObject.getRulePriority()))
				strAudit.append("RULE_PRIORITY" + auditDelimiterColVal + vObject.getRulePriority());
			else
				strAudit.append("RULE_PRIORITY" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);

			strAudit.append("RECON_STATUS_NT" + auditDelimiterColVal + vObject.getReconStatusNt());
			strAudit.append(auditDelimiter);

			strAudit.append("RECON_STATUS" + auditDelimiterColVal + vObject.getReconStatus());
			strAudit.append(auditDelimiter);

			strAudit.append("RECORD_INDICATOR_NT" + auditDelimiterColVal + vObject.getRecordIndicatorNt());
			strAudit.append(auditDelimiter);

			if (vObject.getRecordIndicator() == -1)
				vObject.setRecordIndicator(0);
			strAudit.append("RECORD_INDICATOR" + auditDelimiterColVal + vObject.getRecordIndicator());
			strAudit.append(auditDelimiter);

			strAudit.append("MAKER" + auditDelimiterColVal + vObject.getMaker());
			strAudit.append(auditDelimiter);

			strAudit.append("VERIFIER" + auditDelimiterColVal + vObject.getVerifier());
			strAudit.append(auditDelimiter);

			if (vObject.getDateLastModified() != null && !vObject.getDateLastModified().equalsIgnoreCase(""))
				strAudit.append("DATE_LAST_MODIFIED" + auditDelimiterColVal + vObject.getDateLastModified().trim());
			else
				strAudit.append("DATE_LAST_MODIFIED" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);

			if (vObject.getDateCreation() != null && !vObject.getDateCreation().equalsIgnoreCase(""))
				strAudit.append("DATE_CREATION" + auditDelimiterColVal + vObject.getDateCreation().trim());
			else
				strAudit.append("DATE_CREATION" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);
			
			strAudit.append("RULE_METHOD_AT" + auditDelimiterColVal + vObject.getReconMethodAT());
			strAudit.append(auditDelimiter);

			if (ValidationUtil.isValid(vObject.getReconMethod()))
				strAudit.append("RECON_MATCH_TYPE_AT" + auditDelimiterColVal + vObject.getReconMatchType().trim());
			else
				strAudit.append("RECON_MATCH_TYPE" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);

		} catch (Exception ex) {
			strErrorDesc = ex.getMessage();
			strAudit = strAudit.append(strErrorDesc);
			ex.printStackTrace();
		}
		return strAudit.toString();
	}
	public int getMaxSequence(){
		StringBuffer strBufApprove = new StringBuffer("Select "+getDbFunction("NVL", null)+"(MAX(TAppr.REF_NO),0) REF_NO From RA_BL_RECON_RULE_TAX_HIS TAppr ");
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

}
