package com.vision.util;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

import org.apache.commons.lang3.StringUtils;

import com.vision.vb.VisionUsersVb;

public class StringUlt {
	public static void main(String[] args) {
//		StringUlt stringUlt= new StringUlt();
//		String value = "SELECT Country ALPHA_SUB_TAB,Country ALPHA_SUBTAB_DESCRIPTION FROM LE_book WHERE LEB_Status = 0 AND COUNTRY != 'ZZ' and #VU_C(COUNTRY)#";
//		boolean valu1 = StringUtils.containsIgnoreCase(value, "COUNTRY");
//		boolean valu2 = StringUtils.containsIgnoreCase(value, "LE_BOOK");
//		String query1 = stringUlt.applyUserRestriction(value);
//		System.out.println(query1);
//		System.out.println(valu1+" : "+valu2);

		String rawInput = "'KE-01','TZ-02'";
		rawInput = rawInput.replace("'", ""); // Remove all single quotes

		String[] countryParts = rawInput.split(",");

		List<String> countryList = new ArrayList<>();
		List<String> leBookList = new ArrayList<>();

		for (String item : countryParts) {
			String[] parts = item.split("-");
			if (parts.length == 2) {
				countryList.add("'" + parts[0].trim() + "'");
				leBookList.add("'" + parts[1].trim() + "'");
			}
		}

		// Build final strings for query
		String countryQueryPart = countryList.size() == 1 ? countryList.get(0) : String.join(",", countryList);
		String leBookQueryPart = leBookList.size() == 1 ? leBookList.get(0) : String.join(",", leBookList);

		// Example usage:
		String sql = "SELECT * FROM some_table WHERE country IN (" + countryQueryPart + ") AND le_book IN ("
				+ leBookQueryPart + ")";
		System.out.println(sql);

	}

	public String applyUserRestriction(String sqlQuery) {
//		VisionUsersVb visionUserVb = CustomContextHolder.getContext();
		VisionUsersVb visionUserVb = new VisionUsersVb();
		visionUserVb.setUpdateRestriction("Y");
		visionUserVb.setCountry("KE,CD");
		visionUserVb.setLeBook("01");
//		visionUserVb = getRestrictionInfo(visionUserVb);
		// VU_CLEB,VU_CLEB_AO,VU_CLEB_LV,VU_SBU,VU_PRODUCT,VU_OUC
		if (sqlQuery.contains("#VU_LEB"))
			sqlQuery = replacehashPrompt(sqlQuery, "#VU_LEB(", visionUserVb.getLeBook(),
					visionUserVb.getUpdateRestriction());
		if (sqlQuery.contains("#VU_C"))
			sqlQuery = replacehashPrompt(sqlQuery, "#VU_C(", visionUserVb.getCountry(),
					visionUserVb.getUpdateRestriction());
		if (sqlQuery.contains("#VU_CLEB_AO"))
			sqlQuery = replacehashPrompt(sqlQuery, "#VU_CLEB_AO(", visionUserVb.getAccountOfficer(),
					visionUserVb.getUpdateRestriction());
		if (sqlQuery.contains("#VU_CLEB_LV"))
			sqlQuery = replacehashPrompt(sqlQuery, "#VU_CLEB_LV(", visionUserVb.getLegalVehicle(),
					visionUserVb.getUpdateRestriction());
		if (sqlQuery.contains("#VU_LV_CLEB"))
			sqlQuery = replacehashPrompt(sqlQuery, "#VU_LV_CLEB(", visionUserVb.getLegalVehicleCleb(),
					visionUserVb.getUpdateRestriction());
		if (sqlQuery.contains("#VU_SBU"))
			sqlQuery = replacehashPrompt(sqlQuery, "#VU_SBU(", visionUserVb.getSbuCode(),
					visionUserVb.getUpdateRestriction());
		if (sqlQuery.contains("#VU_PRODUCT"))
			sqlQuery = replacehashPrompt(sqlQuery, "#VU_PRODUCT(", visionUserVb.getProductAttribute(),
					visionUserVb.getUpdateRestriction());
		if (sqlQuery.contains("#VU_OUC"))
			sqlQuery = replacehashPrompt(sqlQuery, "#VU_OUC(", visionUserVb.getOucAttribute(),
					visionUserVb.getUpdateRestriction());
		if (sqlQuery.contains("#VU_CLEB_SOC_TL"))
			sqlQuery = replacehashPrompt(sqlQuery, "#VU_CLEB_SOC_TL(", visionUserVb.getClebTransline(),
					visionUserVb.getUpdateRestriction());
		if (sqlQuery.contains("#VU_CLEB_SOC_BL"))
			sqlQuery = replacehashPrompt(sqlQuery, "#VU_CLEB_SOC_BL(", visionUserVb.getClebBusinessline(),
					visionUserVb.getUpdateRestriction());
		if (sqlQuery.contains("#VU_CLEB_SOC")) {
			sqlQuery = replacehashPrompt(sqlQuery, "#VU_CLEB_SOC(", visionUserVb.getClebTrasnBusline(),
					visionUserVb.getUpdateRestriction());
		}
		if (sqlQuery.contains("#VU_CLEB_AOAS"))
			sqlQuery = replacehashPrompt(sqlQuery, "#VU_CLEB_AOAS(", visionUserVb.getAccountOfficer(),
					visionUserVb.getUpdateRestriction());
		if (sqlQuery.contains("#VU_OTHERS"))
			sqlQuery = replacehashPrompt(sqlQuery, "#VU_OTHERS(", visionUserVb.getOtherAttr(),
					visionUserVb.getUpdateRestriction());
		return sqlQuery;
	}

	public String replacehashPrompt(String query, String restrictStr, String restrictVal, String updateRestriction) {
		try {
			String replaceStr = "";
			if (restrictVal.contains(",")) {
				restrictVal = restrictVal.replaceAll(",", "','");
			}
			String orgSbuStr = StringUtils.substringBetween(query, restrictStr, ")#");
			if (ValidationUtil.isValid(orgSbuStr)) {
				if (orgSbuStr.contains("OR") && "Y".equalsIgnoreCase(updateRestriction)
						&& ValidationUtil.isValid(restrictVal)) {
					if (orgSbuStr.contains("OR") && "Y".equalsIgnoreCase(updateRestriction)) {
						StringJoiner conditionjoiner = new StringJoiner(" OR ");
						String[] arrsplit = orgSbuStr.split("OR");
						for (String str : arrsplit) {
							String st = str + " IN ('" + restrictVal + "')";
							conditionjoiner.add(st);
						}
						replaceStr = " AND (" + conditionjoiner + ")";
					}
				} else if ("Y".equalsIgnoreCase(updateRestriction) && ValidationUtil.isValid(restrictVal)) {
					replaceStr = " AND " + orgSbuStr + " IN ('" + restrictVal + "')";
				}
				restrictStr = restrictStr.replace("(", "\\(");
				orgSbuStr = orgSbuStr.replace("|", "\\|");
				orgSbuStr = orgSbuStr.replace("+", "\\+");
				query = query.replaceAll(restrictStr + orgSbuStr + "\\)#", replaceStr);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return query;
	}

	private VisionUsersVb getRestrictionInfo(VisionUsersVb vObject) {
		try {
			String restrictionXml = "";
//					getLoginUserXml();
			vObject.setCountry(CommonUtils.getValueForXmlTag(restrictionXml, "COUNTRY-LE_BOOK"));
			vObject.setAccountOfficer(CommonUtils.getValueForXmlTag(restrictionXml, "COUNTRY-LE_BOOK-ACCOUNT_OFFICER"));
			vObject.setLegalVehicle(CommonUtils.getValueForXmlTag(restrictionXml, "LEGAL_VEHICLE"));
			vObject.setLegalVehicleCleb(CommonUtils.getValueForXmlTag(restrictionXml, "LEGAL_VEHICLE-COUNTRY-LE_BOOK"));
			vObject.setOucAttribute(CommonUtils.getValueForXmlTag(restrictionXml, "OUC"));
			vObject.setProductAttribute(CommonUtils.getValueForXmlTag(restrictionXml, "PRODUCT"));
			vObject.setSbuCode(CommonUtils.getValueForXmlTag(restrictionXml, "SBU"));
			vObject.setClebTransline(CommonUtils.getValueForXmlTag(restrictionXml, "COUNTRY-LE_BOOK-TRANSLINE"));
			vObject.setClebTrasnBusline(CommonUtils.getValueForXmlTag(restrictionXml, "COUNTRY-LE_BOOK-TRANBUSLINE"));
			vObject.setClebBusinessline(CommonUtils.getValueForXmlTag(restrictionXml, "COUNTRY-LE_BOOK-BUSINESSLINE"));
			vObject.setOtherAttr(CommonUtils.getValueForXmlTag(restrictionXml, "OTHERS"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return vObject;
	}

}
