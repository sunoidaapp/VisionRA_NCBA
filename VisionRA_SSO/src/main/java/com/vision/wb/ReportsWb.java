package com.vision.wb;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.TreeMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.context.ServletContextAware;

import com.vision.authentication.CustomContextHolder;
import com.vision.dao.AbstractDao;
import com.vision.dao.CommonApiDao;
import com.vision.dao.CommonDao;
import com.vision.dao.ReportsDao;
import com.vision.exception.ExceptionCode;
import com.vision.exception.RuntimeCustomException;
import com.vision.util.CommonUtils;
import com.vision.util.Constants;
import com.vision.util.CreateCsv;
import com.vision.util.DeepCopy;
import com.vision.util.ExcelExportUtil;
import com.vision.util.PDFExportUtil;
import com.vision.util.ValidationUtil;
import com.vision.vb.ColumnHeadersVb;
import com.vision.vb.PrdQueryConfig;
import com.vision.vb.PromptTreeVb;
import com.vision.vb.RCReportFieldsVb;
import com.vision.vb.ReportFilterVb;
import com.vision.vb.ReportStgVb;
import com.vision.vb.ReportUserDefVb;
import com.vision.vb.ReportsVb;
import com.vision.vb.SmartSearchVb;
import com.vision.vb.VisionUsersVb;

import jakarta.servlet.ServletContext;

@Component
public class ReportsWb extends AbstractWorkerBean<ReportsVb> implements ServletContextAware {
	public ApplicationContext applicationContext;
	private ServletContext servletContext;
	@Autowired
	private ReportsDao reportsDao;
	@Autowired
	private CommonDao commonDao;
	@Autowired
	private PDFExportUtil pdfExportUtil;
	@Autowired
	CommonApiDao commonApiDao;
	@Value("${app.databaseType}")
	private String databaseType;

	@Value("${app.productName}")
	private String productName;

	@Override
	public void setServletContext(ServletContext arg0) {
		servletContext = arg0;
	}
	public static Logger logger = LoggerFactory.getLogger(ReportsWb.class);

	@Override
	protected void setAtNtValues(ReportsVb vObject) {

	}

	@Override
	protected void setVerifReqDeleteType(ReportsVb vObject) {
		vObject.setStaticDelete(false);
		vObject.setVerificationRequired(false);
	}

	@Override
	protected AbstractDao<ReportsVb> getScreenDao() {
		return reportsDao;
	}

	public ExceptionCode getReportList() {
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			
			ArrayList<ReportsVb> applicationlst = (ArrayList<ReportsVb>) reportsDao.findApplicationCategory();
			for(ReportsVb reportVb : applicationlst) {
				ArrayList<ReportsVb> reportCatlst = (ArrayList<ReportsVb>) reportsDao.findReportCategory(reportVb.getApplicationId());
				ArrayList<ReportsVb> reportslstbyCategory = new ArrayList<ReportsVb>();
				ArrayList<ReportsVb> reportslst = new ArrayList<ReportsVb>();
				for (ReportsVb categoryVb : reportCatlst) {
					reportslstbyCategory = (ArrayList<ReportsVb>) reportsDao.getReportList(categoryVb.getReportCategory(),reportVb.getApplicationId());
					categoryVb.setReportslst(reportslstbyCategory);
					reportslst.add(categoryVb);
				}
				reportVb.setReportslst(reportslst);
			}
			exceptionCode.setResponse(applicationlst);
		} catch (Exception e) {
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg(e.getMessage());
		}
		return exceptionCode;
	}

	public ExceptionCode reportFilterProcess(String filterRefCode) {
		List<ReportFilterVb> filterlst = new ArrayList<ReportFilterVb>();
		ExceptionCode exceptionCode = new ExceptionCode();
		Connection conExt = null;
		try {
			List<ReportFilterVb> filterDetaillst = reportsDao.getReportFilterDetail(filterRefCode);
			if (filterDetaillst != null && filterDetaillst.size() > 0) {
				ReportFilterVb filterObj = filterDetaillst.get(0);
				String filterObjProp = ValidationUtil.isValid(filterObj.getFilterRefXml())
						? filterObj.getFilterRefXml().replaceAll("\n", "").replaceAll("\r", "")
						: "";
				String promptXml = CommonUtils.getValueForXmlTag(filterObjProp, "Prompts");
				int filterCnt = Integer.parseInt(CommonUtils.getValueForXmlTag(promptXml, "PromptCount"));
				for (int i = 1; i <= filterCnt; i++) {
					ReportFilterVb vObject = new ReportFilterVb();
					String refXml = CommonUtils.getValueForXmlTag(filterObjProp, "Prompt" + i);

					vObject.setFilterSeq(Integer.parseInt(CommonUtils.getValueForXmlTag(refXml, "Sequence")));
					vObject.setFilterLabel(CommonUtils.getValueForXmlTag(refXml, "Label"));
					vObject.setFilterType(CommonUtils.getValueForXmlTag(refXml, "Type"));
					vObject.setFilterSourceId(CommonUtils.getValueForXmlTag(refXml, "SourceId"));
					vObject.setDependencyFlag(CommonUtils.getValueForXmlTag(refXml, "DependencyFlag"));
					vObject.setDependentPrompt(CommonUtils.getValueForXmlTag(refXml, "DependentPrompt"));
					vObject.setMultiWidth(CommonUtils.getValueForXmlTag(refXml, "MultiWidth"));
					vObject.setMultiSelect(CommonUtils.getValueForXmlTag(refXml, "MultiSelect"));
					vObject.setSpecificTab(CommonUtils.getValueForXmlTag(refXml, "SpecificTab")); // Only for Dashboards
					vObject.setDefaultValueId( CommonUtils.getValueForXmlTag(refXml, "DefaultValue"));
					vObject.setFilterRow(CommonUtils.getValueForXmlTag(refXml, "FilterRow"));
					vObject.setFilterDateFormat(CommonUtils.getValueForXmlTag(refXml, "DateFormat"));
					vObject.setFilterDateRestrict(CommonUtils.getValueForXmlTag(refXml, "DateRestrict"));
					//Added for Exception Filter - To Add Filter Str T1.Country
					vObject.setFilterDbString(CommonUtils.getValueForXmlTag(refXml, "ExceptionFilter"));
					vObject.setFilterValueType(CommonUtils.getValueForXmlTag(refXml, "FilterValueType"));
					vObject.setIsMandatory(CommonUtils.getValueForXmlTag(refXml, "isMandatory"));
					if(!ValidationUtil.isValid(vObject.getIsMandatory())) {
						vObject.setIsMandatory("N");
					}
					if (ValidationUtil.isValid(vObject.getDefaultValueId())) {
						//defaultValueSrc = replaceFilterHashVariables(vObject.getDefaultValueId(), vObject);
						//vObject.setFilterDefaultValue(reportsDao.getReportFilterValue(defaultValueSrc));
						LinkedHashMap<String,String> filterDefaultValueMap = new LinkedHashMap<String,String>();
						exceptionCode = getReportFilterSourceValue(vObject);
						LinkedHashMap<String,String> filterMap = (LinkedHashMap<String,String>) exceptionCode.getResponse();
						if (filterMap != null) {
							for (Map.Entry<String, String> entry : filterMap.entrySet()) {
								String key = entry.getKey();
								if (key.contains("@")) {
									key = key.replace("@", "");
									filterDefaultValueMap.put(key, entry.getValue());
								}

							}
							vObject.setFilterDefaultValue(filterDefaultValueMap);
						}
					}
					if (!ValidationUtil.isValid(vObject.getDependencyFlag())) {
						vObject.setDependencyFlag("N");
					}
					/*
					 * if ("N".equalsIgnoreCase(vObject.getDependencyFlag()) &&
					 * ValidationUtil.isValid(vObject.getFilterSourceId())) {
					 * vObject.setFilterSourceVal(getFilterSourceValue(vObject)); }
					 */
					filterlst.add(vObject);
				}
			}
			exceptionCode.setResponse(filterlst);
			exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
			return exceptionCode;
		} catch (Exception e) {
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg(e.getMessage());
			return exceptionCode;
		}
	}

	public ExceptionCode getFilterSourceValue(ReportFilterVb vObject) {
		//LinkedHashMap<String, String> filterSourceVal = new LinkedHashMap<String, String>();
		Connection conExt =null;
		ExceptionCode exceptionCode =new ExceptionCode();
		try {
			List<PrdQueryConfig> sourceQuerylst = reportsDao.getSqlQuery(vObject.getFilterSourceId());
			if (sourceQuerylst != null && sourceQuerylst.size() > 0) {
				PrdQueryConfig sourceQueryDet = sourceQuerylst.get(0);
				if ("QUERY".equalsIgnoreCase(sourceQueryDet.getDataRefType())) {
					sourceQueryDet.setQueryProc(replaceFilterHashVariables(sourceQueryDet.getQueryProc(), vObject));
					exceptionCode = reportsDao.getReportFilterValue(sourceQueryDet.getQueryProc());
				}else if ("PROCEDURE".equalsIgnoreCase(sourceQueryDet.getDataRefType())) {
					exceptionCode = reportsDao.getComboPromptData(vObject,sourceQueryDet);
					
				}
			}
		} catch (Exception e) {
			//logger.error(e.getMessage());
		}
		return exceptionCode;
	}

	public String replaceFilterHashVariables(String query, ReportFilterVb vObject) {
		query = query.replaceAll("#VISION_ID#", "" + CustomContextHolder.getContext().getVisionId());
		query = query.replaceAll("#PROMPT_VALUE_1#",
				ValidationUtil.isValid(vObject.getFilter1Val()) ? vObject.getFilter1Val() : "''");
		query = query.replaceAll("#PROMPT_VALUE_2#",
				ValidationUtil.isValid(vObject.getFilter2Val()) ? vObject.getFilter2Val() : "''");
		query = query.replaceAll("#PROMPT_VALUE_3#",
				ValidationUtil.isValid(vObject.getFilter3Val()) ? vObject.getFilter3Val() : "''");
		query = query.replaceAll("#PROMPT_VALUE_4#",
				ValidationUtil.isValid(vObject.getFilter4Val()) ? vObject.getFilter4Val() : "''");
		query = query.replaceAll("#PROMPT_VALUE_5#",
				ValidationUtil.isValid(vObject.getFilter5Val()) ? vObject.getFilter5Val() : "''");
		query = query.replaceAll("#PROMPT_VALUE_6#",
				ValidationUtil.isValid(vObject.getFilter6Val()) ? vObject.getFilter6Val() : "''");
		query = query.replaceAll("#PROMPT_VALUE_7#",
				ValidationUtil.isValid(vObject.getFilter7Val()) ? vObject.getFilter7Val() : "''");
		query = query.replaceAll("#PROMPT_VALUE_8#",
				ValidationUtil.isValid(vObject.getFilter8Val()) ? vObject.getFilter8Val() : "''");
		query = query.replaceAll("#PROMPT_VALUE_9#",
				ValidationUtil.isValid(vObject.getFilter9Val()) ? vObject.getFilter9Val() : "''");
		query = query.replaceAll("#PROMPT_VALUE_10#",
				ValidationUtil.isValid(vObject.getFilter10Val()) ? vObject.getFilter10Val() : "''");
		
		query = query.replaceAll("#EXC_COUNTRY#",
				ValidationUtil.isValid(vObject.getExceptionCountry()) ? vObject.getExceptionCountry() : "''");
		query = query.replaceAll("#EXC_LE_BOOK#",
				ValidationUtil.isValid(vObject.getExceptionLeBook()) ? vObject.getExceptionLeBook() : "''");
		
		query = query.replaceAll("#NS_PROMPT_VALUE_1#",
				ValidationUtil.isValid(vObject.getFilter1Val()) ? vObject.getFilter1Val().replaceAll("'", "") : "''");
		query = query.replaceAll("#NS_PROMPT_VALUE_2#",
				ValidationUtil.isValid(vObject.getFilter2Val()) ? vObject.getFilter2Val().replaceAll("'", "") : "''");
		query = query.replaceAll("#NS_PROMPT_VALUE_3#",
				ValidationUtil.isValid(vObject.getFilter3Val()) ? vObject.getFilter3Val().replaceAll("'", "") : "''");
		query = query.replaceAll("#NS_PROMPT_VALUE_4#",
				ValidationUtil.isValid(vObject.getFilter4Val()) ? vObject.getFilter4Val().replaceAll("'", "") : "''");
		query = query.replaceAll("#NS_PROMPT_VALUE_5#",
				ValidationUtil.isValid(vObject.getFilter5Val()) ? vObject.getFilter5Val().replaceAll("'", "") : "''");
		query = query.replaceAll("#NS_PROMPT_VALUE_6#",
				ValidationUtil.isValid(vObject.getFilter6Val()) ? vObject.getFilter6Val().replaceAll("'", "") : "''");
		query = query.replaceAll("#NS_PROMPT_VALUE_7#",
				ValidationUtil.isValid(vObject.getFilter7Val()) ? vObject.getFilter7Val().replaceAll("'", "") : "''");
		query = query.replaceAll("#NS_PROMPT_VALUE_8#",
				ValidationUtil.isValid(vObject.getFilter8Val()) ? vObject.getFilter8Val().replaceAll("'", "") : "''");
		query = query.replaceAll("#NS_PROMPT_VALUE_9#",
				ValidationUtil.isValid(vObject.getFilter9Val()) ? vObject.getFilter9Val().replaceAll("'", "") : "''");
		query = query.replaceAll("#NS_PROMPT_VALUE_10#",
				ValidationUtil.isValid(vObject.getFilter10Val()) ? vObject.getFilter10Val().replaceAll("'", "") : "''");
		
		query = commonDao.applyUserRestriction(query);
		return query;
	}

	
	public ExceptionCode getReportDetails(ReportsVb vObject) throws SQLException {
		ExceptionCode exceptionCode = new ExceptionCode();
		ExceptionCode exceptionCodeProc = new ExceptionCode();
		PrdQueryConfig prdQueryConfig = new PrdQueryConfig();
		List<PrdQueryConfig> sqlQueryList = new ArrayList<PrdQueryConfig>();
		DeepCopy<ReportsVb> clonedObj = new DeepCopy<ReportsVb>();
		PromptTreeVb promptTreeVb = new PromptTreeVb();
		String reportType  = vObject.getReportType();
		ReportsVb subReportsVb = new ReportsVb();
		try {
			List<ReportsVb> reportDatalst = new ArrayList<ReportsVb>();
			if(!ValidationUtil.isValid(vObject.getObjectType())){
				vObject.setObjectType("G");
			}
			subReportsVb = clonedObj.copy(vObject);
			if (ValidationUtil.isValid(vObject.getSubReportId())) {
				if (ValidationUtil.isValid(subReportsVb.getDdFlag())
						&& "Y".equalsIgnoreCase(subReportsVb.getDdFlag())) {
					reportDatalst = reportsDao.getSubReportDetail(vObject);
					if (reportDatalst != null && reportDatalst.size() > 0) {
						subReportsVb = reportDatalst.get(0);
						subReportsVb.setNextLevel(reportsDao.getIntReportNextLevel(subReportsVb));
					}
				}else {
					subReportsVb.setNextLevel("0");
				}
				
			}  else {
				reportDatalst = reportsDao.getSubReportDetail(vObject);
				if (reportDatalst != null && reportDatalst.size() > 0) {
					subReportsVb = reportDatalst.get(0);
					if (ValidationUtil.isValid(subReportsVb.getDdFlag())
							&& "Y".equalsIgnoreCase(subReportsVb.getDdFlag())) {
						subReportsVb.setNextLevel(reportsDao.getNextLevel(subReportsVb));
					}
				} else {
					exceptionCode.setOtherInfo(vObject);
					exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
					exceptionCode.setErrorMsg(
							"Report Levels Not Maintained for the ReportId[" + vObject.getReportId() + "] !!");
					return exceptionCode;
				}
			}
			sqlQueryList = reportsDao.getSqlQuery(subReportsVb.getDataRefId());
			if (sqlQueryList != null && sqlQueryList.size() > 0) {
				prdQueryConfig = sqlQueryList.get(0);
			} else {
				exceptionCode.setOtherInfo(vObject);
				exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
				exceptionCode.setErrorMsg("Query not maintained for the Data Ref Id[" + subReportsVb.getDataRefId() + "]");
				return exceptionCode;
			}
			subReportsVb.setDbConnection(prdQueryConfig.getDbConnectionName());
			vObject.setDbConnection(prdQueryConfig.getDbConnectionName());
			ArrayList<ColumnHeadersVb> colHeaders = new ArrayList<ColumnHeadersVb>();
			List<ColumnHeadersVb> columnHeadersXmllst = reportsDao.getReportColumns(subReportsVb);
			String columnHeaderXml = "";
			if((columnHeadersXmllst== null || columnHeadersXmllst.isEmpty())) {
				if("QUERY".equalsIgnoreCase(prdQueryConfig.getDataRefType())) {
					exceptionCode.setOtherInfo(vObject);
					exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
					exceptionCode.setErrorMsg("Column Headers Not Maintained for the ReportId[" + subReportsVb.getReportId()+ "] and Sub Report Id[" + subReportsVb.getSubReportId() + "] !!");
					return exceptionCode;
				}
			}else {
				columnHeaderXml = columnHeadersXmllst.get(0).getColumnXml();
				
			}
			subReportsVb.setReportTitle(CommonUtils.getValueForXmlTag(columnHeaderXml, "OBJECT_CAPTION"));
			subReportsVb.setGrandTotalCaption(CommonUtils.getValueForXmlTag(columnHeaderXml, "GRANDTOTAL_CAPTION"));
			
			if ("G".equalsIgnoreCase(subReportsVb.getObjectType())) {
				colHeaders = reportsDao.getColumnHeaders(columnHeaderXml,vObject);
				subReportsVb.setColumnHeaderslst(colHeaders);
			} else if ("C".equalsIgnoreCase(subReportsVb.getObjectType())
					|| "T".equalsIgnoreCase(subReportsVb.getObjectType())) {
				subReportsVb.setColHeaderXml(columnHeaderXml);
			}
			String finalExeQuery = "";
			String maxRecords = commonDao.findVisionVariableValue("PRD_REPORT_MAXFETCH");
			subReportsVb.setMaxRecords(Integer.parseInt(ValidationUtil.isValid(maxRecords) ? maxRecords : "5000"));
			// prdQueryConfig.setQueryProc(prdQueryConfig.getQueryProc().toUpperCase());
			String queryTmp = prdQueryConfig.getQueryProc();
			queryTmp = queryTmp.toUpperCase();
			vObject.setDateCreation(
					String.valueOf(System.currentTimeMillis()) + vObject.getCurrentLevel().substring(0, 1));
			if ("QUERY".equalsIgnoreCase(prdQueryConfig.getDataRefType())) {
				if ("G".equalsIgnoreCase(subReportsVb.getObjectType())
						|| "T".equalsIgnoreCase(subReportsVb.getObjectType())) {
					if (queryTmp.contains("ORDER BY")) {
						String orderBy = queryTmp.substring(queryTmp.lastIndexOf("ORDER BY"), queryTmp.length());
						prdQueryConfig.setQueryProc(
								prdQueryConfig.getQueryProc().substring(0, queryTmp.lastIndexOf("ORDER BY")));
						prdQueryConfig.setSortField(orderBy);
					}
					if (!ValidationUtil.isValid(prdQueryConfig.getSortField())) {
						exceptionCode.setOtherInfo(vObject);
						exceptionCode.setErrorMsg(
								"ORDER BY is mandatory in Query for [" + subReportsVb.getDataRefId() + "] !!");
						return exceptionCode;
					}
				}
				finalExeQuery = reportsDao.replacePromptVariables(prdQueryConfig.getQueryProc(), vObject, false);
			} else if ("PROCEDURE".equalsIgnoreCase(prdQueryConfig.getDataRefType())) {
				vObject.setSubReportId(subReportsVb.getSubReportId());
				if(vObject.getPromptTree() == null) {
					finalExeQuery = reportsDao.replacePromptVariables(prdQueryConfig.getQueryProc(), vObject, true);
					exceptionCodeProc = reportsDao.callProcforReportData(vObject, finalExeQuery);
				}else {
					exceptionCodeProc.setResponse(vObject.getPromptTree());
					exceptionCodeProc.setErrorCode(Constants.STATUS_ZERO);
				}
				if (exceptionCodeProc.getErrorCode() == Constants.STATUS_ZERO) {
					promptTreeVb = (PromptTreeVb)exceptionCodeProc.getResponse();
					if (ValidationUtil.isValid(promptTreeVb.getTableName())) {
						if("REPORTS_STG".equalsIgnoreCase(promptTreeVb.getTableName().toUpperCase())) {
							finalExeQuery = "SELECT * FROM " + promptTreeVb.getTableName() + " WHERE SESSION_ID='"+promptTreeVb.getSessionId()+"' AND REPORT_ID='"+promptTreeVb.getReportId()+"' ";
						}else {
							finalExeQuery = "SELECT * FROM " + promptTreeVb.getTableName() + " ";
						}
						prdQueryConfig.setSortField("ORDER BY SORT_FIELD");
						if(ValidationUtil.isValid(promptTreeVb.getColumnHeaderTable())) {
							colHeaders = (ArrayList<ColumnHeadersVb>)reportsDao.getColumnHeaderFromTable(promptTreeVb);
							if(colHeaders != null && !colHeaders.isEmpty()) {
								subReportsVb.setColumnHeaderslst(colHeaders);
							}
						}
					if(!"CB".equalsIgnoreCase(reportType))	{
						if(subReportsVb.getColumnHeaderslst() == null || subReportsVb.getColumnHeaderslst().isEmpty()) {
							exceptionCode.setOtherInfo(vObject);
							exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
							exceptionCode.setErrorMsg("Column Headers Not Maintained for the ReportId[" + subReportsVb.getReportId()+ "] and Sub Report Id[" + subReportsVb.getSubReportId() + "] !!");
							return exceptionCode;
						}
					}
						subReportsVb.setPromptTree(promptTreeVb);
					} else {
						exceptionCode.setOtherInfo(vObject);
						exceptionCode.setErrorMsg("Output Table not return from Procedure but the Procedure return Success Status");
						return exceptionCode;
					}
				}else {
					exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
					exceptionCode.setErrorMsg(exceptionCodeProc.getErrorMsg());
					exceptionCode.setOtherInfo(vObject);
					return exceptionCode;
				}
			}
			subReportsVb.setSortField(prdQueryConfig.getSortField());
			String masterReportType = reportsDao.getReportType(vObject.getReportId());
			if ("W".equalsIgnoreCase(masterReportType) && "C".equalsIgnoreCase(subReportsVb.getObjectType()))
				subReportsVb.setChartList(commonApiDao.getChartList(vObject.getChartType()));

			// save user def settings
			List savedUserSetLst = new ArrayList<>();
			ArrayList<SmartSearchVb> smartSearchlst = new ArrayList<SmartSearchVb>();
			ReportUserDefVb reportUserDefVb = new ReportUserDefVb();
			if (!ValidationUtil.isValid(vObject.getRunType()))
				vObject.setRunType("R");

			if (!"W".equalsIgnoreCase(masterReportType) && "G".equalsIgnoreCase(vObject.getObjectType())
					&& "R".equalsIgnoreCase(vObject.getRunType())) {
				savedUserSetLst = reportsDao.getSavedUserDefSetting(subReportsVb, true);

			}
			if (savedUserSetLst != null && !savedUserSetLst.isEmpty())
				reportUserDefVb = (ReportUserDefVb) savedUserSetLst.get(0);

			if (!ValidationUtil.isValid(vObject.getColumnsToHide()))
				subReportsVb.setColumnsToHide(reportUserDefVb.getColumnsToHide());
			else
				subReportsVb.setColumnsToHide(vObject.getColumnsToHide());
			if (!ValidationUtil.isValid(vObject.getApplyGrouping()))
				subReportsVb.setApplyGrouping(reportUserDefVb.getApplyGrouping());
			else
				subReportsVb.setApplyGrouping(vObject.getApplyGrouping());
			if (!ValidationUtil.isValid(vObject.getShowDimensions()))
				subReportsVb.setShowDimensions(reportUserDefVb.getShowDimensions());
			else
				subReportsVb.setShowDimensions(vObject.getShowDimensions());
			if (!ValidationUtil.isValid(vObject.getShowMeasures()))
				subReportsVb.setShowMeasures(reportUserDefVb.getShowMeasures());
			else
				subReportsVb.setShowMeasures(vObject.getShowMeasures());
			if (ValidationUtil.isValid(reportUserDefVb.getSearchColumn())) {
				String[] searchArr = reportUserDefVb.getSearchColumn().split(",");
				for (String strArr : searchArr) {
					SmartSearchVb smartSearchVb = new SmartSearchVb();
					String[] searcCondArr = strArr.split("!@#");
					smartSearchVb.setObject(searcCondArr[0]);
					smartSearchVb.setCriteria(searcCondArr[1]);
					smartSearchVb.setValue(searcCondArr[2]);
					smartSearchlst.add(smartSearchVb);
				}
			}
			if ("Y".equalsIgnoreCase(subReportsVb.getApplyGrouping())) {
				// vObject.setScreenSortColumn("ORDER BY " + subReportsVb.getShowDimensions());
				subReportsVb.setSortField("ORDER BY " + subReportsVb.getShowDimensions());
				if (!ValidationUtil.isValid(vObject.getScreenSortColumn())
						&& ValidationUtil.isValid(reportUserDefVb.getSortColumn()))
					subReportsVb.setSortField(reportUserDefVb.getSortColumn());
				if (ValidationUtil.isValid(vObject.getScreenSortColumn()))
					subReportsVb.setSortField(vObject.getScreenSortColumn());
			} else {
				if (!ValidationUtil.isValid(vObject.getScreenSortColumn())
						&& !ValidationUtil.isValid(reportUserDefVb.getSortColumn()))
					subReportsVb.setSortField(prdQueryConfig.getSortField());
				else if (!ValidationUtil.isValid(vObject.getScreenSortColumn())
						&& ValidationUtil.isValid(reportUserDefVb.getSortColumn()))
					subReportsVb.setSortField(reportUserDefVb.getSortColumn());
				else
					subReportsVb.setSortField(vObject.getScreenSortColumn());
			}

			if (smartSearchlst != null && !smartSearchlst.isEmpty()
					&& !ValidationUtil.isValid(vObject.getSmartSearchOpt())) {
				vObject.setSmartSearchOpt(smartSearchlst);
			}
			if (vObject.getSmartSearchOpt() != null && vObject.getSmartSearchOpt().size() > 0) {
				finalExeQuery = "SELECT * FROM ( " + finalExeQuery + " ) TEMP WHERE ";
				for (int len = 0; len < vObject.getSmartSearchOpt().size(); len++) {
					SmartSearchVb data = vObject.getSmartSearchOpt().get(len);
					String searchVal = CommonUtils.criteriaBasedVal(data.getCriteria(),data.getValue());
					if (len > 0) 
						finalExeQuery = finalExeQuery + " AND ";
					if("MSSQL".equalsIgnoreCase(databaseType)) {
						if("GREATER".equalsIgnoreCase(data.getCriteria()) || "GREATEREQUALS".equalsIgnoreCase(data.getCriteria())
								|| "LESSER".equalsIgnoreCase(data.getCriteria()) || "LESSEREQUALS".equalsIgnoreCase(data.getCriteria()))
							finalExeQuery = finalExeQuery + "(" + data.getObject() + ") "+searchVal;
						else
							finalExeQuery = finalExeQuery + " UPPER(" + data.getObject() + ") "+searchVal;
					}else {
						finalExeQuery = finalExeQuery + " UPPER(" + data.getObject() + ") "+searchVal;
					}
				}
			}

			subReportsVb.setReportUserDeflst(savedUserSetLst);
			subReportsVb.setFinalExeQuery(finalExeQuery);
			// subReportsVb.setSortField(prdQueryConfig.getSortField());
			subReportsVb.setPromptLabel(vObject.getPromptLabel());
			subReportsVb.setDbConnection(prdQueryConfig.getDbConnectionName());
			subReportsVb.setDrillDownLabel(vObject.getDrillDownLabel());
			subReportsVb.setCurrentPage(vObject.getCurrentPage());
			subReportsVb.setWidgetTheme(vObject.getWidgetTheme());

			exceptionCode.setResponse(subReportsVb);
			exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
			exceptionCode.setErrorMsg("Success");
			return exceptionCode;
		} catch (Exception ex) {
			//logger.error("Exception while getting Report Data[" + vObject.getReportId() + "]SubSeq["+ vObject.getNextLevel() + "]...!!");
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg(ex.getMessage());
			exceptionCode.setOtherInfo(subReportsVb);
			return exceptionCode;
		}
	}

	
	
	public ExceptionCode getIntReportsDetail(ReportsVb vObject) {
		ExceptionCode exceptionCode = new ExceptionCode();
		List<ReportsVb> reportsLst = new ArrayList<>();
		try {
			reportsLst = reportsDao.getReportsDetail(vObject);
			if(reportsLst!= null && !reportsLst.isEmpty()) {
				reportsLst.forEach(reportDet  -> {
					reportDet.setNextLevel(reportsDao.getNextLevel(reportDet));
					List<ColumnHeadersVb> colHeadersLst =  new ArrayList<ColumnHeadersVb>();
					colHeadersLst = reportsDao.getReportColumns(reportDet);
					if(colHeadersLst != null && colHeadersLst.size()>0) {
						reportDet.setReportTitle(CommonUtils.getValueForXmlTag(colHeadersLst.get(0).getColumnXml(), "OBJECT_CAPTION"));
						reportDet.setGrandTotalCaption(CommonUtils.getValueForXmlTag(colHeadersLst.get(0).getColumnXml(), "GRANDTOTAL_CAPTION"));
						reportDet.setColumnHeaderslst(colHeadersLst);
					}
				});
			}else {
				exceptionCode.setErrorMsg("Report Details not found!!");
				exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			}
			exceptionCode.setOtherInfo(vObject);
			exceptionCode.setResponse(reportsLst);
		} catch (Exception e) {
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg(e.getMessage());
		}
		return exceptionCode;
	}
	public ExceptionCode exportToXls(ReportsVb vObject, int currentUserId, String workBookCnt) {
		ExceptionCode exceptionCode = new ExceptionCode();
		ReportsVb reportsVb = new ReportsVb();
		int rowNum = 0;
		try {
			String reportTitle = "";
			String applicationTheme = vObject.getApplicationTheme();
			reportTitle = vObject.getReportTitle();
			String screenGroupColumn = vObject.getScreenGroupColumn();
			String screenSortColumn = vObject.getScreenSortColumn();
			String[] hiddenColumns = null;
			if (ValidationUtil.isValid(vObject.getColumnsToHide())) {
				hiddenColumns = vObject.getColumnsToHide().split("!@#");
			}
			exceptionCode = getReportDetails(vObject);
			if (exceptionCode.getErrorCode() == Constants.SUCCESSFUL_OPERATION) {
				if (ValidationUtil.isValid(exceptionCode.getResponse())) {
					vObject = (ReportsVb) exceptionCode.getResponse();
				}
			} else {
				return exceptionCode;
			}
			List<ColumnHeadersVb> colHeaderslst = vObject.getColumnHeaderslst();
			List<ColumnHeadersVb> updatedLst = vObject.getColumnHeaderslst();
			if (hiddenColumns != null) {
				for (int ctr = 0; ctr < hiddenColumns.length; ctr++) {
					updatedLst = formColumnHeader(updatedLst, hiddenColumns[ctr]);
				}
			}
			if (updatedLst != null && updatedLst.size() > 0) {
				int finalMaxRow = updatedLst.stream().mapToInt(ColumnHeadersVb::getLabelRowNum).max().orElse(0);
				for (ColumnHeadersVb colObj : updatedLst) {
					if (colObj.getRowspan() > finalMaxRow) {
						colObj.setRowspan(colObj.getRowspan() - finalMaxRow);
					}
				}
				colHeaderslst = updatedLst;
			}

			List<String> colTypes = new ArrayList<String>();
			Map<Integer, Integer> columnWidths = new HashMap<Integer, Integer>(colHeaderslst.size());
			List<ReportStgVb> reportsStgs = new ArrayList<ReportStgVb>();
			for (int loopCnt = 0; loopCnt < colHeaderslst.size(); loopCnt++) {
				columnWidths.put(Integer.valueOf(loopCnt), Integer.valueOf(-1));
				ColumnHeadersVb colHVb = colHeaderslst.get(loopCnt);
				if (colHVb.getColspan() <= 1 && colHVb.getNumericColumnNo() != 99) {
					colTypes.add(colHVb.getColType());
				}
			}
			int headerCnt = 0;
			
			String assetFolderUrl = servletContext.getRealPath("/WEB-INF/classes/images");
			// String assetFolderUrl = commonDao.findVisionVariableValue("MDM_IMAGE_PATH");
			// assetFolderUrl = "E:\\RA_Image\\";
			vObject.setReportTitle(reportTitle);
			vObject.setMaker(currentUserId);
			if (ValidationUtil.isValid(screenGroupColumn))
				vObject.setScreenGroupColumn(screenGroupColumn);
			if (ValidationUtil.isValid(screenSortColumn))
				vObject.setScreenSortColumn(screenSortColumn);
			getScreenDao().fetchMakerVerifierNames(vObject);
			if (ValidationUtil.isValid(applicationTheme))
				vObject.setApplicationTheme(applicationTheme);
			// ExcelExportUtil.createPrompts(vObject, new ArrayList(), sheet, workBook,
			// assetFolderUrl, styls, headerCnt);
			boolean createHeadersAndFooters = true;
			// Excel Report Header
			/// rowNum = ExcelExportUtil.writeHeadersRA(vObject, colHeaderslst, rowNum,
			// sheet, styls, colTypes,columnWidths);
			//logger.info("Excel Export Data Extraction Begin[" + vObject.getReportId() + ":" + vObject.getSubReportId()+ "]");
			exceptionCode = reportsDao.getResultData(vObject);
			//logger.info("Excel Export Data Extraction End[" + vObject.getReportId() + ":" + vObject.getSubReportId() + "]");
			List<HashMap<String, String>> dataLst = null;
			List<HashMap<String, String>> totalLst = null;
			ReportsVb resultVb = new ReportsVb();
			if (exceptionCode.getErrorCode() == Constants.SUCCESSFUL_OPERATION) {
				if (ValidationUtil.isValid(exceptionCode.getResponse())) {
					resultVb = (ReportsVb) exceptionCode.getResponse();
					dataLst = resultVb.getGridDataSet();
					totalLst = resultVb.getTotal();
				}
			}
			//logger.info("Excel Export Data Write Begin[" + vObject.getReportId() + ":" + vObject.getSubReportId() + "]");
			SXSSFWorkbook workBook = new SXSSFWorkbook(500);
			SXSSFSheet sheet = (SXSSFSheet) workBook.createSheet(vObject.getReportTitle());
			// SXSSFSheet sheet = (SXSSFSheet) workBook.getSheet(vObject.getReportTitle());
			Map<Integer, XSSFCellStyle> styls = ExcelExportUtil.createStyles(workBook, applicationTheme);
			ExcelExportUtil.createPromptsPage(vObject, sheet, workBook, assetFolderUrl, styls, headerCnt);
			sheet = (SXSSFSheet) workBook.createSheet("Report");
			int ctr = 1;
			int sheetCnt = 3;
			do {
				if ((rowNum + vObject.getMaxRecords()) > SpreadsheetVersion.EXCEL2007.getMaxRows()) {
					rowNum = 0;
					sheet = (SXSSFSheet) workBook.createSheet("" + sheetCnt);
					sheetCnt++;
					createHeadersAndFooters = true;
				}
				if (createHeadersAndFooters) {
					rowNum = ExcelExportUtil.writeHeadersRA(vObject, colHeaderslst, rowNum, sheet, styls, colTypes,
							columnWidths);
					sheet.createFreezePane(0, rowNum);
				}
				createHeadersAndFooters = false;
				// writing data into excel
				ctr++;
				rowNum = ExcelExportUtil.writeReportDataRA(workBook, vObject, colHeaderslst, dataLst, sheet, rowNum,
						styls, colTypes, columnWidths, false, assetFolderUrl);
				vObject.setCurrentPage(ctr);
				dataLst = new ArrayList();
				exceptionCode = reportsDao.getResultData(vObject);
				if (ValidationUtil.isValid(exceptionCode.getResponse())) {
					resultVb = (ReportsVb) exceptionCode.getResponse();
					dataLst = resultVb.getGridDataSet();
				}
			} while (dataLst != null && !dataLst.isEmpty());

			if (totalLst != null)
				rowNum = ExcelExportUtil.writeReportDataRA(workBook, vObject, colHeaderslst, totalLst, sheet, rowNum,
						styls, colTypes, columnWidths, true, assetFolderUrl);

			headerCnt = colTypes.size();
			int noOfSheets = workBook.getNumberOfSheets();
			for (int a = 1; a < noOfSheets; a++) {
				sheet = (SXSSFSheet) workBook.getSheetAt(a);
				int loopCount = 0;
				for (loopCount = 0; loopCount < headerCnt; loopCount++) {
					sheet.setColumnWidth(loopCount, columnWidths.get(loopCount));
				}
			}
			String filePath = System.getProperty("java.io.tmpdir");
			if (!ValidationUtil.isValid(filePath)) {
				filePath = System.getenv("TMP");
			}
			if (ValidationUtil.isValid(filePath)) {
				filePath = filePath + File.separator;
			}
			File lFile = new File(filePath + ValidationUtil.encode(reportTitle) + "_" + currentUserId + ".xlsx");
			if (lFile.exists()) {
				lFile.delete();
			}
			lFile.createNewFile();
			FileOutputStream fileOS = new FileOutputStream(lFile);
			workBook.write(fileOS);
			fileOS.flush();
			fileOS.close();
			//logger.info("Excel Export Data Write End[" + vObject.getReportId() + ":" + vObject.getSubReportId() + "]");
			exceptionCode.setResponse(filePath);
			exceptionCode.setOtherInfo(vObject.getReportTitle() + "_" + currentUserId);
			exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
		} catch (Exception e) {
			e.printStackTrace();
			//logger.error("Report Export Excel Exception at: " + vObject.getReportId() + " : " + vObject.getReportTitle());
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
		}
		return exceptionCode;
	}
	public ExceptionCode exportToPdf(int currentUserId,ReportsVb reportsVb){
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			String applicationTheme = reportsVb.getApplicationTheme();
			String reportOrientation = reportsVb.getReportOrientation();
			String reportTitle = reportsVb.getReportTitle();
			String assetFolderUrl = servletContext.getRealPath("/WEB-INF/classes/images");
			//String assetFolderUrl = commonDao.findVisionVariableValue("MDM_IMAGE_PATH");
			String screenGroupColumn = reportsVb.getScreenGroupColumn();
			String screenSortColumn = reportsVb.getScreenSortColumn();
			String[] hiddenColumns = null;
			if(ValidationUtil.isValid(reportsVb.getColumnsToHide())) {
				hiddenColumns = reportsVb.getColumnsToHide().split("!@#");
			}
			reportsVb.setMaker(CustomContextHolder.getContext().getVisionId());
			exceptionCode = getReportDetails(reportsVb);
			if(exceptionCode.getErrorCode() == Constants.SUCCESSFUL_OPERATION) {
				if (ValidationUtil.isValid(exceptionCode.getResponse())) {
					reportsVb = (ReportsVb) exceptionCode.getResponse();
					//exceptionCode =  reportsDao.getResultData(subReportsVb);
				}
			}else {
				return exceptionCode;
			}
			List<ColumnHeadersVb> colHeaderslst = reportsVb.getColumnHeaderslst();
			ArrayList<ColumnHeadersVb> updatedLst = new ArrayList<ColumnHeadersVb>();
			if(hiddenColumns != null) {
				for(int ctr=0;ctr < hiddenColumns.length;ctr++) {
					updatedLst = formColumnHeader(colHeaderslst, hiddenColumns[ctr]);
					colHeaderslst = updatedLst;
				}
			}
			
			if(updatedLst != null && updatedLst.size()>0) {
				int finalMaxRow = updatedLst.stream().mapToInt(ColumnHeadersVb::getLabelRowNum).max().orElse(0);
				for(ColumnHeadersVb colObj : updatedLst) {
					if(colObj.getRowspan() > finalMaxRow) {
						colObj.setRowspan(colObj.getRowspan() - finalMaxRow);
					}
				}
				colHeaderslst = updatedLst;
				
			}
				
			
			List<String> colTypes = new ArrayList<String>();
			Map<Integer,Integer> columnWidths = new HashMap<Integer,Integer>(colHeaderslst.size());
			for(int loopCnt= 0; loopCnt < colHeaderslst.size(); loopCnt++){
				columnWidths.put(Integer.valueOf(loopCnt), Integer.valueOf(-1));
				ColumnHeadersVb colHVb = colHeaderslst.get(loopCnt);
				if(colHVb.getColspan() <= 1 && colHVb.getNumericColumnNo() != 99) {
					colTypes.add(colHVb.getColType());
				}
			}
			//logger.info("Pdf Export Data Extraction Begin["+reportsVb.getReportId()+":"+reportsVb.getSubReportId()+"]");
			exceptionCode = reportsDao.getResultData(reportsVb);
			//logger.info("Pdf Export Data Extraction End["+reportsVb.getReportId()+":"+reportsVb.getSubReportId()+"]");
			List<HashMap<String, String>> dataLst = null;
			List<HashMap<String, String>> totalLst = null;
			ReportsVb resultVb = new ReportsVb();
			if (exceptionCode.getErrorCode() == Constants.SUCCESSFUL_OPERATION) {
				if (ValidationUtil.isValid(exceptionCode.getResponse())) {
					resultVb = (ReportsVb) exceptionCode.getResponse();
					dataLst = resultVb.getGridDataSet();
					totalLst = resultVb.getTotal();
				}
			}
			//logger.info("Pdf Export Data Write Begin["+reportsVb.getReportId()+":"+reportsVb.getSubReportId()+"]");
			/*if(!reportOrientation.equalsIgnoreCase(reportsVb.getReportOrientation())) {
				reportsVb.setPdfGrwthPercent(0);
			}*/
			reportsVb.setReportTitle(reportTitle);
			reportsVb.setMaker(currentUserId);
			reportsVb.setReportOrientation(reportOrientation);
			reportsVb.setApplicationTheme(applicationTheme);
			getScreenDao().fetchMakerVerifierNames(reportsVb);
			//Grouping on PDF
			String[] capGrpCols = null; 
			ArrayList<String> groupingCols = new ArrayList<String>();
			ArrayList<ColumnHeadersVb> columnHeadersFinallst = new ArrayList<ColumnHeadersVb>();
			colHeaderslst.forEach(colHeadersVb -> {
				if(colHeadersVb.getColspan() <= 1 && colHeadersVb.getNumericColumnNo() != 99) {
					columnHeadersFinallst.add(colHeadersVb);
				}
			});
			if(ValidationUtil.isValid(screenGroupColumn)) {
				reportsVb.setPdfGroupColumn(screenGroupColumn);
			}
			if(ValidationUtil.isValid(reportsVb.getPdfGroupColumn()))
				capGrpCols = reportsVb.getPdfGroupColumn().split("!@#");
			
			if(reportsVb.getTotalRows() <= reportsVb.getMaxRecords() && capGrpCols != null && capGrpCols.length > 0) {
				for(String grpStr : capGrpCols) {
					for(ColumnHeadersVb colHeader : columnHeadersFinallst) {
						if(grpStr.equalsIgnoreCase(colHeader.getCaption().toUpperCase())) {
							groupingCols.add(colHeader.getDbColumnName());
							break;
						}
					}
				}
			}
			final String[] grpColNames =  capGrpCols;
			Map<String, List < HashMap<String, String> >> groupingMap = new HashMap<String, List < HashMap<String, String> >>();
			if(reportsVb.getTotalRows() <= reportsVb.getMaxRecords() && (groupingCols != null && groupingCols.size() > 0)) {
				switch(groupingCols.size()) {
					case 1:
						groupingMap = dataLst.stream().collect(
								Collectors.groupingBy(
								m -> (m.get(groupingCols.get(0))) == null ? 
										"" :grpColNames[0]+": " + m.get(groupingCols.get(0))
								));
						break;
					case 2:
						groupingMap = dataLst.stream().collect(
								Collectors.groupingBy(
								m -> (m.get(groupingCols.get(0))
										+" >> "+m.get(groupingCols.get(1))) == null ? 
										"" :grpColNames[0]+": " + m.get(groupingCols.get(0))
										+" >> "+grpColNames[1]+": " +m.get(groupingCols.get(1))
								));
						break;
					case 3:
						groupingMap = dataLst.stream().collect(
								Collectors.groupingBy(
								m -> (m.get(groupingCols.get(0))
										+" >> "+m.get(groupingCols.get(1))
										+" >> "+m.get(groupingCols.get(2))) == null ? 
										"" :grpColNames[0]+": " + m.get(groupingCols.get(0))
										+" >> "+grpColNames[1]+": " +m.get(groupingCols.get(1))
										+" >> "+grpColNames[2]+": " +m.get(groupingCols.get(2))
								));
						break;
					case 4:
						groupingMap = dataLst.stream().collect(
								Collectors.groupingBy(
								m -> (m.get(groupingCols.get(0))
										+" >> "+m.get(groupingCols.get(1))
										+" >> "+m.get(groupingCols.get(2))
										+" >> "+m.get(groupingCols.get(3))) == null ? 
										"" :grpColNames[0]+": " + m.get(groupingCols.get(0))
										+" >> "+grpColNames[1]+": " +m.get(groupingCols.get(1))
										+" >> "+grpColNames[2]+": " +m.get(groupingCols.get(2))
										+" >> "+grpColNames[2]+": " +m.get(groupingCols.get(3))
								));
						break;
					case 5:
						groupingMap = dataLst.stream().collect(
								Collectors.groupingBy(
								m -> (m.get(groupingCols.get(0))
										+" >> "+m.get(groupingCols.get(1))
										+" >> "+m.get(groupingCols.get(2))
										+" >> "+m.get(groupingCols.get(3))
										+" >> "+m.get(groupingCols.get(4))) == null ? 
										"" :grpColNames[0]+": " + m.get(groupingCols.get(0))
										+" >> "+grpColNames[1]+": " +m.get(groupingCols.get(1))
										+" >> "+grpColNames[2]+": " +m.get(groupingCols.get(2))
										+" >> "+grpColNames[2]+": " +m.get(groupingCols.get(3))
										+" >> "+grpColNames[2]+": " +m.get(groupingCols.get(4))
								));
						break;
				}
				Map<String, List < HashMap<String, String> >> sortedMap = new TreeMap<String, List < HashMap<String, String> >>();
				if (ValidationUtil.isValid(screenSortColumn)) {
					if (screenSortColumn.contains(groupingCols.get(0))) {
						String value = screenSortColumn.substring(9, screenSortColumn.length()).toUpperCase();
						String[] col = value.split(",");
						for (int i = 0; i < col.length; i++) {
							if (col[i].contains(groupingCols.get(0))) {
								String val = col[i];
								if (val.contains("DESC")) {
									sortedMap = new TreeMap<String, List<HashMap<String, String>>>(Collections.reverseOrder());
									sortedMap.putAll(groupingMap);
								} else {
									sortedMap = new TreeMap<String, List<HashMap<String, String>>>(groupingMap);
								}
							}
						}
					} else {
						sortedMap = new TreeMap<String, List<HashMap<String, String>>>(groupingMap);
					}
				} else {
					sortedMap = new TreeMap<String, List<HashMap<String, String>>>(groupingMap);
				}
					
					exceptionCode = pdfExportUtil.exportToPdfRAWithGroup(colHeaderslst, dataLst, reportsVb, assetFolderUrl,colTypes,currentUserId,totalLst,sortedMap,columnHeadersFinallst);
			}else {
					exceptionCode = pdfExportUtil.exportToPdfRA(colHeaderslst, dataLst, reportsVb, assetFolderUrl,colTypes,currentUserId,totalLst,columnHeadersFinallst);
			}
			//logger.info("Pdf Export Data Write End["+reportsVb.getReportId()+":"+reportsVb.getSubReportId()+"]");
		}catch(Exception e) {
			e.printStackTrace();
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg(e.getMessage());
		}
		return exceptionCode;
	}
	public ExceptionCode exportMultiExcel(ReportsVb vObject) {
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			HashMap<String,ExceptionCode> resultMap = new HashMap<String,ExceptionCode>();
			ReportsVb reportVb = new ReportsVb();
			exceptionCode = getIntReportsDetail(vObject);
			List<ReportsVb> detailReportlst = (ArrayList<ReportsVb>)exceptionCode.getResponse();
			List<DataFetcher> threads = new ArrayList<DataFetcher>(detailReportlst.size());
			detailReportlst.forEach(reportsVb -> {
				reportsVb.setPromptValue1(vObject.getPromptValue1());
				reportsVb.setPromptValue2(vObject.getPromptValue2());
				reportsVb.setPromptValue3(vObject.getPromptValue3());
				reportsVb.setPromptValue4(vObject.getPromptValue4());
				reportsVb.setPromptValue5(vObject.getPromptValue5());
				reportsVb.setPromptValue6(vObject.getPromptValue6());
				reportsVb.setPromptValue7(vObject.getPromptValue7());
				reportsVb.setPromptValue8(vObject.getPromptValue8());
				reportsVb.setPromptValue9(vObject.getPromptValue9());
				reportsVb.setPromptValue10(vObject.getPromptValue10());
				reportsVb.getReportTitle();
				DataFetcher fetcher = new DataFetcher(reportsVb,resultMap);
				fetcher.setDaemon(true);
				fetcher.start();
				try {
						fetcher.join();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				threads.add(fetcher);
				
			});
			for(DataFetcher df:threads){
				int count = 0;
				if(!df.dataFetched){
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					count++;
					if(count > 150){
						count = 0;
						//logger.info("Data fetch in progress for the report :"+ df.toString());
						continue;
					}
				}
			}
			exceptionCode = exportMultiXls(vObject,resultMap);
		}catch(Exception e) {
			
		}
		return exceptionCode;
	}
	class DataFetcher extends Thread {
		boolean dataFetched = false;
		boolean errorOccured = false;
		String errorMsg = "";
		ExceptionCode exceptionCode;
		ReportsVb dObj = new ReportsVb();
		HashMap<String,ExceptionCode> resultMap = new HashMap<String,ExceptionCode>();
		public DataFetcher(ReportsVb reportsVb,HashMap<String,ExceptionCode> resultMap){
			this.dObj = reportsVb;
			this.resultMap = resultMap;
		}
		@Override
		public void run() {
			try{
				
				ExceptionCode exceptionCode = getReportDetails(dObj);
				if(exceptionCode.getErrorCode() == Constants.SUCCESSFUL_OPERATION) {
					if (ValidationUtil.isValid(exceptionCode.getResponse())) {
						ReportsVb subReportsVb = (ReportsVb) exceptionCode.getResponse();
						exceptionCode =  reportsDao.getResultData(subReportsVb);
						exceptionCode.setRequest(dObj.getReportTitle());
					}
				}
				resultMap.put(dObj.getCurrentLevel(), exceptionCode);
			}catch(RuntimeCustomException rex){
				dataFetched = true;
				errorOccured = true;
				exceptionCode = rex.getCode();
			}catch(Exception e){
				dataFetched = true;
				errorOccured = true;
				errorMsg = e.getMessage();
			}
		}
	}
	public ExceptionCode exportMultiXls(ReportsVb vObject,HashMap<String,ExceptionCode> resultMap){
		ExceptionCode exceptionCode = new ExceptionCode();
		ReportsVb reportsVb = new ReportsVb();
		try{
			VisionUsersVb visionUsersVb =CustomContextHolder.getContext();
			int currentUserId = visionUsersVb.getVisionId();
			SXSSFWorkbook workBook = new SXSSFWorkbook(500);
			SXSSFSheet sheet = null; 
			String filePath = System.getProperty("java.io.tmpdir");
			if(!ValidationUtil.isValid(filePath)){
				filePath = System.getenv("TMP");
			}
			if(ValidationUtil.isValid(filePath)){
				filePath = filePath + File.separator;
			}
			File lFile = new File(filePath+ValidationUtil.encode(vObject.getReportTitle())+"_"+currentUserId+".xlsx");
			if(lFile.exists())
				lFile.delete();
			lFile.createNewFile();
			int headerCnt = 0;
			String assetFolderUrl = servletContext.getRealPath("/WEB-INF/classes/images");
			sheet =(SXSSFSheet) workBook.createSheet("Report Detail");
			//workBook.createSheet(vObject.getReportTitle());
			Map<Integer, XSSFCellStyle> styls = ExcelExportUtil.createStyles(workBook, "");
			ExcelExportUtil.createPromptsPage(vObject, sheet, workBook, assetFolderUrl, styls, headerCnt);
			List<HashMap<String, String>> dataLst = new ArrayList<>();
			List<HashMap<String, String>> totalLst = new ArrayList<>();
			List<ColumnHeadersVb> colHeaderslst = new ArrayList<>();
			ReportsVb reportsStgs =  null;
			Map<String, ExceptionCode> sortedMap = resultMap.entrySet().stream()
		            .sorted(Map.Entry.comparingByKey())
		            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
		                    (oldValue, newValue) -> oldValue, HashMap::new));
			for (Map.Entry<String, ExceptionCode> entry : sortedMap.entrySet()) {
				ExceptionCode resultDataException = entry.getValue();
					reportsStgs = (ReportsVb) resultDataException.getResponse();
					String reportTitle = (String)resultDataException.getRequest();
					vObject.setScreenName(reportTitle);
				if(reportsStgs != null)	{
					dataLst = reportsStgs.getGridDataSet();
					totalLst = reportsStgs.getTotal();
					colHeaderslst = reportsStgs.getColumnHeaderslst();
				} else {
					dataLst = new ArrayList<>();
					totalLst = new ArrayList<>();
					colHeaderslst = new ArrayList<>();
				}
				workBook.createSheet(reportTitle);
				sheet = (SXSSFSheet) workBook.getSheet(reportTitle);
				List<String> colTypes = new ArrayList<String>();
				Map<Integer, Integer> columnWidths = new HashMap<Integer, Integer>(colHeaderslst.size());
				for (int loopCnt = 0; loopCnt < colHeaderslst.size(); loopCnt++) {
					columnWidths.put(Integer.valueOf(loopCnt), Integer.valueOf(-1));
					ColumnHeadersVb colHVb = colHeaderslst.get(loopCnt);
					if (colHVb.getColspan() <= 1) {
						colTypes.add(colHVb.getColType());
					}
				}
				//int headerCnt = 0;
				//logger.info("Report Export Excel Starts at: " + vObject.getReportId() + " : " + vObject.getReportTitle());
//				String assetFolderUrl = servletContext.getRealPath("/WEB-INF/classes/images");
				//String assetFolderUrl = commonDao.findVisionVariableValue("MDM_IMAGE_PATH");
				vObject.setMaker(currentUserId);
				getScreenDao().fetchMakerVerifierNames(vObject);
				boolean createHeadersAndFooters = true;
//				Map<Integer, XSSFCellStyle> styls = ExcelExportUtil.createStyles(workBook);
				//ExcelExportUtil.createPrompts(vObject, sheet, workBook, assetFolderUrl, styls, headerCnt);
				int  rowNum = 0;
				rowNum = ExcelExportUtil.writeHeadersRA(vObject, colHeaderslst, rowNum, sheet, styls, colTypes,
						columnWidths);
				rowNum = ExcelExportUtil.writeReportDataRA(workBook, vObject, colHeaderslst, dataLst, sheet, rowNum,
						styls, colTypes, columnWidths, false, assetFolderUrl);
				if (totalLst != null)
					rowNum = ExcelExportUtil.writeReportDataRA(workBook, vObject, colHeaderslst, totalLst, sheet,
							rowNum, styls, colTypes, columnWidths, true, assetFolderUrl);

				headerCnt = colTypes.size();
				int loopCount = 0;
				for (loopCount = 0; loopCount < headerCnt; loopCount++) {
					sheet.setColumnWidth(loopCount, columnWidths.get(loopCount));
				}
			}
			FileOutputStream fileOS = new FileOutputStream(lFile);
			workBook.write(fileOS);
			fileOS.flush();
			fileOS.close();
			//logger.info("Report Export Excel End at: " + vObject.getReportId()+" : "+vObject.getReportTitle());
			exceptionCode.setResponse(filePath);
			exceptionCode.setOtherInfo(vObject.getReportTitle()+"_"+currentUserId);
			exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
		}catch (Exception e) {
			e.printStackTrace();
			//logger.error("Report Export Excel Exception at: " + vObject.getReportId()+" : "+vObject.getReportTitle());
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
		}
		return exceptionCode;
	}
	public ExceptionCode exportMultiPdf(ReportsVb vObject) {
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			String assetFolderUrl = servletContext.getRealPath("/WEB-INF/classes/images");
			HashMap<String,ExceptionCode> resultMap = new HashMap<String,ExceptionCode>();
			ReportsVb reportVb = new ReportsVb();
			exceptionCode = getIntReportsDetail(vObject);
			List<ReportsVb> detailReportlst = (ArrayList<ReportsVb>)exceptionCode.getResponse();
			detailReportlst.get(0).getReportTitle();
			List<DataFetcher> threads = new ArrayList<DataFetcher>(detailReportlst.size());
			detailReportlst.forEach(reportsVb -> {
				reportsVb.setPromptValue1(vObject.getPromptValue1());
				reportsVb.setPromptValue2(vObject.getPromptValue2());
				reportsVb.setPromptValue3(vObject.getPromptValue3());
				reportsVb.setPromptValue4(vObject.getPromptValue4());
				reportsVb.setPromptValue5(vObject.getPromptValue5());
				reportsVb.setPromptValue6(vObject.getPromptValue6());
				reportsVb.setPromptValue7(vObject.getPromptValue7());
				reportsVb.setPromptValue8(vObject.getPromptValue8());
				reportsVb.setPromptValue9(vObject.getPromptValue9());
				reportsVb.setPromptValue10(vObject.getPromptValue10());
				reportsVb.getReportTitle();
				DataFetcher fetcher = new DataFetcher(reportsVb,resultMap);
				fetcher.setDaemon(true);
				fetcher.start();
				try {
						fetcher.join();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				threads.add(fetcher);
				
			});
			for(DataFetcher df:threads){
				int count = 0;
				if(!df.dataFetched){
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					count++;
					if(count > 150){
						count = 0;
						//logger.info("Data fetch in progress for the report :"+ df.toString());
						continue;
					}
				}
			}
			exceptionCode = pdfExportUtil.exportMultiReportPdf(vObject,resultMap,assetFolderUrl);
		}catch(Exception e) {
			e.printStackTrace();
		}
		return exceptionCode;
	}
	public ExceptionCode getTreePromptData(ReportFilterVb vObject) {
		List<PromptTreeVb> promptTree = null;
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			List<PrdQueryConfig> sourceQuerylst = reportsDao.getSqlQuery(vObject.getFilterSourceId());
			if (sourceQuerylst != null && sourceQuerylst.size() > 0) {
				PrdQueryConfig sourceQueryDet = sourceQuerylst.get(0);
				exceptionCode = reportsDao.getTreePromptData(vObject,sourceQueryDet);
				if(exceptionCode.getErrorCode() == Constants.SUCCESSFUL_OPERATION && exceptionCode.getResponse() != null) {
					promptTree = (List<PromptTreeVb>)exceptionCode.getResponse();
					promptTree = createPraentChildRelations(promptTree, vObject.getFilterString());
					exceptionCode.setResponse(promptTree);
					exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
					exceptionCode.setErrorMsg("successful operation");
				}
			} else {
				exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
				exceptionCode.setErrorMsg("Query not maintained for the DATA_REF_ID " + vObject.getFilterSourceId());
			}
		} catch (Exception e) {
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg(e.getMessage());
		}
		return exceptionCode;
	}
	public List<PromptTreeVb> createPraentChildRelations(List<PromptTreeVb> promptTreeList, String filterString) {
		DeepCopy<PromptTreeVb> deepCopy = new DeepCopy<PromptTreeVb>();
		List<PromptTreeVb> lResult = new ArrayList<PromptTreeVb>(0);
		List<PromptTreeVb> promptTreeListCopy = new CopyOnWriteArrayList<PromptTreeVb>(deepCopy.copyCollection(promptTreeList));
		//Top Roots are added.
		for(PromptTreeVb promptVb:promptTreeListCopy){
			if(promptVb.getField1().equalsIgnoreCase(promptVb.getField3())){
				lResult.add(promptVb);
				promptTreeListCopy.remove(promptVb);
			}
		}
		//For each top node add all child's and to that child's add sub child's recursively.
		for(PromptTreeVb promptVb:lResult){
			addChilds(promptVb,promptTreeListCopy);
		}
		//Get the sub tree from the filter string if filter string is not null.
		if(ValidationUtil.isValid(filterString)){
			lResult = getSubTreeFrom(filterString, lResult);
		}
		//set the empty lists to null. this is required for UI to display the leaf nodes properly.
		nullifyEmptyList(lResult);
		return lResult;
	}
	private void addChilds(PromptTreeVb vObject, List<PromptTreeVb> promptTreeListCopy) {
		for(PromptTreeVb promptTreeVb:promptTreeListCopy){
			if(vObject.getField1().equalsIgnoreCase(promptTreeVb.getField3())){
				if(vObject.getChildren() == null){
					vObject.setChildren(new ArrayList<PromptTreeVb>(0));
				}
				vObject.getChildren().add(promptTreeVb);
				addChilds(promptTreeVb, promptTreeListCopy);
			}
		}
	}
	private List<PromptTreeVb> getSubTreeFrom(String filterString, List<PromptTreeVb> result) {
		List<PromptTreeVb> lResult = new ArrayList<PromptTreeVb>(0);
		for(PromptTreeVb promptTreeVb:result){
			if(promptTreeVb.getField1().equalsIgnoreCase(filterString)){
				lResult.add(promptTreeVb);
				return lResult;
			}else if(promptTreeVb.getChildren() != null){
				lResult = getSubTreeFrom(filterString, promptTreeVb.getChildren());
				if(lResult != null && !lResult.isEmpty()) return lResult;
			}
		}
		return lResult;
	}
	private void nullifyEmptyList(List<PromptTreeVb> lResult){
		for(PromptTreeVb promptTreeVb:lResult){
			if(promptTreeVb.getChildren() != null){
				nullifyEmptyList(promptTreeVb.getChildren());
			}
			if(promptTreeVb.getChildren() != null && promptTreeVb.getChildren().isEmpty()){
				promptTreeVb.setChildren(null);
			}
		}
	}
	public ExceptionCode createCBReport(ReportsVb reportsVb){
		ExceptionCode exceptionCode = null;
		FileOutputStream fileOS = null;
		File lfile =  null;
		File lfileRs = null;
		String fileNames = "";
		String tmpFileName = "";
		String filesNameslst[] = null;
		ReportsVb vObject = new ReportsVb();
		PromptTreeVb promptTree = null;
		String destFilePath = System.getProperty("java.io.tmpdir");
		if(!ValidationUtil.isValid(destFilePath))
			destFilePath = System.getenv("TMP");
		if(ValidationUtil.isValid(destFilePath))
			destFilePath = destFilePath + File.separator;
		try{
			exceptionCode = getReportDetails(reportsVb);
			if(exceptionCode.getErrorCode() == Constants.SUCCESSFUL_OPERATION) {
				vObject = (ReportsVb) exceptionCode.getResponse();
				if(ValidationUtil.isValid(vObject))
					promptTree = vObject.getPromptTree();
			}
			if(promptTree == null || "1".equalsIgnoreCase(promptTree.getStatus())){
				exceptionCode.setErrorMsg("No Records Found");
				exceptionCode.setErrorCode(Constants.NO_RECORDS_FOUND);
				//exceptionCode.setOtherInfo(reportsVb);
				return exceptionCode;
			}
			lfile = createTemplateFile(reportsVb);	
			lfileRs = lfile;
			OPCPackage pkg = OPCPackage.open( new FileInputStream(lfile.getAbsolutePath()));
			XSSFWorkbook workbook = new XSSFWorkbook(pkg);
			
			XSSFCellStyle cs = workbook.createCellStyle();
			cs.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
		    cs.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		    XSSFFont font= workbook.createFont();
		    font.setColor(IndexedColors.WHITE.getIndex());
		    font.setBold(true);
		    cs.setFont(font);

		    long min = 0;
			long max = 1000;
			workbook.setForceFormulaRecalculation(true);
			List<RCReportFieldsVb> results = null;
			
			//logger.info("Get CB Report Data Begin["+reportsVb.getReportId()+"]Sub Report Id["+reportsVb.getSubReportId()+"]");
			results = reportsDao.getCBKReportData(reportsVb,promptTree, min, max);
			//logger.info("Get CB Report Data End["+reportsVb.getReportId()+"]Sub Report Id["+reportsVb.getSubReportId()+"]");
			
			String tabId = "";
			do{
				 for(RCReportFieldsVb result:results){
					String actualFile = lfile.getName();
					String fileName = actualFile.substring(0, actualFile.lastIndexOf("_"));
					if(ValidationUtil.isValid(result.getExcelFileName()) && !result.getExcelFileName().equalsIgnoreCase(fileName)){
						 //System.out.println(result.getExcelFileName()+" : "+fileName);
						FileOutputStream fileOS1 = new FileOutputStream(lfile);
						workbook.write(fileOS1);
						lfile = new File(destFilePath+result.getExcelFileName()+"_"+CustomContextHolder.getContext().getVisionId()+".xlsx");
						String filePath = lfile.getAbsolutePath();
						filePath = filePath.substring(0, filePath.indexOf(result.getExcelFileName()));
						if(filePath.contains("temp"))
							filePath = filePath.substring(0, filePath.indexOf("temp"));
						if(!lfile.exists())
							ExcelExportUtil.createTemplateFile(lfile);
						
						if(!tmpFileName.contains(result.getExcelFileName())){
							if(!ValidationUtil.isValid(fileNames)){
								fileNames = lfile.toString();
							}else{
								fileNames = fileNames+"#"+lfile.toString();	
							}
							if(!ValidationUtil.isValid(tmpFileName)){
								tmpFileName = result.getExcelFileName();
							}else{
								tmpFileName = tmpFileName+"#"+result.getExcelFileName();
							}
						}
						pkg = OPCPackage.open( new FileInputStream(lfile.getAbsolutePath()));
						workbook = new XSSFWorkbook(pkg);
						cs = workbook.createCellStyle();
						byte[] greenClr = {(byte) 0, (byte) 92, (byte) 140};
						XSSFColor greenXClor = new XSSFColor(greenClr);
						cs.setFillForegroundColor(greenXClor);
					    cs.setFillPattern(FillPatternType.SOLID_FOREGROUND);
					    font= workbook.createFont();
					    font.setColor(IndexedColors.WHITE.getIndex());
					    font.setBold(true);
					    cs.setFont(font);
					 }
					 int noOfsheets = workbook.getNumberOfSheets();
					 tabId = result.getTabelId();
					 if(Integer.parseInt(result.getTabelId()) > (noOfsheets-1)){
						workbook.createSheet(result.getTabelId());
					 }
					 Sheet sheet = workbook.getSheetAt(Integer.parseInt(result.getTabelId()));
					 Row row = null;
				     row = sheet.getRow(Integer.parseInt(result.getRowId())-1);
				     if(row == null){
				    	 row = sheet.createRow(Integer.parseInt(result.getRowId())-1);
				     }
				     Cell cell = row.getCell(Integer.parseInt(result.getColId()));
				     if(cell == null){
				    	cell = row.createCell(Integer.parseInt(result.getColId()));
				     }
				     if(cell == null || row == null){
						throw new RuntimeCustomException("Invalid Report data,Tab ID["+result.getTabelId()+"] Row Id["+Integer.parseInt(result.getRowId())+"], Col Id["+Integer.parseInt(result.getColId())+"] does not exists in template.");
					 }
					if(!ValidationUtil.isValid(result.getValue1())){
						
					}else if(("C".equalsIgnoreCase(result.getColType()) || "N".equalsIgnoreCase(result.getColType()))){
						cell.setCellValue(Double.parseDouble(result.getValue1()));
					}else if("F".equalsIgnoreCase(result.getColType())){
//						cell.setCellType(Cell.CELL_TYPE_FORMULA);
						cell.setCellFormula(result.getValue1());
					}else{
						cell.setCellValue(result.getValue1());
					}
					if(ValidationUtil.isValid(result.getSheetName())){
				    	workbook.setSheetName(Integer.parseInt(result.getTabelId()),result.getSheetName());
				    }
					if(ValidationUtil.isValid(result.getRowStyle())){
						/*byte[] greenClr = {(byte) 0, (byte) 92, (byte) 140};
						XSSFColor greenXClor = new XSSFColor(greenClr);*/
						if("FHT".equalsIgnoreCase(result.getRowStyle())){
							cell.setCellStyle(cs);
						}if("FHTF".equalsIgnoreCase(result.getRowStyle())){
							sheet.createFreezePane(0, Integer.parseInt(result.getRowId()));
							cell.setCellStyle(cs);
						}
					}
				}
				min = max;
				max += 1000;
				////System.out.println("min : "+min+" max : "+max);
				results = reportsDao.getCBKReportData(reportsVb,promptTree, min, max);
			}while(!results.isEmpty());
				fileOS = new FileOutputStream(lfile);
				workbook.write(fileOS);
			//add list of files to Zip
			String fileslst[] = fileNames.split("#");
			filesNameslst = tmpFileName.split("#");
			if(ValidationUtil.isValid(tmpFileName) && filesNameslst.length == 1){
				reportsVb.setTemplateId(filesNameslst[0]);
			}
			if(fileslst.length > 1){
				File f= new File(destFilePath+reportsVb.getReportTitle()+".zip");
				ZipOutputStream out = new ZipOutputStream(new FileOutputStream(f));
				for(int a = 0;a < fileslst.length;a++){
					FileInputStream fis = new FileInputStream(fileslst[a]);
					File f1 = new File(fileslst[a]);
					String tmpfileName = filesNameslst[a]+".xlsx";
					ZipEntry e = new ZipEntry(""+tmpfileName);
					out.putNextEntry(e);
					byte[] bytes = new byte[1024];
					int length;
					while ((length = fis.read(bytes)) >= 0) {
						out.write(bytes, 0, length);
					}
					fis.close();
					f1.delete();
				}
				out.closeEntry();
				out.close();
			}
			
		}catch (Exception e) {
			e.printStackTrace();
			//logger.error("Exception in CBK Report Generation", e);
			throw new RuntimeCustomException(e.getMessage());
		}finally{
			try{
				if(ValidationUtil.isValid(promptTree))
					reportsDao.callProcToCleanUpTables(promptTree);
				if(fileOS != null){
					fileOS.flush();
					fileOS.close();
					fileOS = null;
				}
			}catch (Exception ex){}
		}
		exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
		//exceptionCode =  CommonUtils.getResultObject("", 1, "", "");
		String fileName = reportsVb.getTemplateId().substring(0, reportsVb.getTemplateId().indexOf(".xlsx"));
		exceptionCode.setOtherInfo(fileName+"_"+CustomContextHolder.getContext().getVisionId());
		exceptionCode.setResponse(destFilePath);
		if(filesNameslst != null && filesNameslst.length > 1){
			exceptionCode.setRequest(reportsVb.getReportTitle());
			if(lfileRs.exists()){
				lfileRs.delete();
			}
		}
		return exceptionCode;
	}
	public File createTemplateFile(ReportsVb reportsVb){
		File lfile =  null;
		FileChannel source = null;
		FileChannel destination = null;
		try {
			String fileName = reportsVb.getTemplateId();
			fileName = ValidationUtil.encode(fileName.substring(0, fileName.indexOf(".xlsx")));
			String destFilePath = System.getProperty("java.io.tmpdir");
			if(!ValidationUtil.isValid(destFilePath)){
				destFilePath = System.getenv("TMP");
			}
			if(ValidationUtil.isValid(destFilePath)){
				destFilePath = destFilePath + File.separator;
			}
			lfile = new File(destFilePath+fileName+"_"+CustomContextHolder.getContext().getVisionId()+".xlsx");
			String filePath = lfile.getAbsolutePath();
			filePath = filePath.substring(0, filePath.indexOf(fileName));
			if(filePath.contains("temp")){
				filePath = filePath.substring(0, filePath.indexOf("temp"));
			}
			if(lfile.exists()){
				lfile.delete();
			}
			String templateFilePath = commonDao.findVisionVariableValue("PRD_CB_TEMPLATE_PATH");
			File lSourceFile = new File(templateFilePath+reportsVb.getTemplateId());
			if(!lSourceFile.exists()){
				throw new RuntimeCustomException("Invalid Report Configuration, Invalid file name or file does not exists @ "+lSourceFile);
			}
			source = new RandomAccessFile(lSourceFile,"rw").getChannel();
			destination = new RandomAccessFile(lfile,"rw").getChannel();
			long position = 0;
			long count = source.size();
			source.transferTo(position, count, destination);
	     }catch(Exception e){
	    	 throw new RuntimeCustomException("Invalid Report Configuration, Invalid file name or file does not exists");
	     }finally {
	    	 if(source != null) {
	    		 try{source.close();}catch(Exception ex){}
	    	 }
	    	 if(destination != null) {
	    		 try{destination.close();}catch(Exception ex){}
	    	 }
	    	 //logger.info("Template File Successfully Created");
	    }
		return lfile;
	}
	private ArrayList<ColumnHeadersVb> formColumnHeader(List<ColumnHeadersVb> orgColList,String hiddenColumn) {
		
		ArrayList<ColumnHeadersVb> updatedColList = new ArrayList<ColumnHeadersVb>();
		int maxHeaderRow = orgColList.stream().mapToInt(ColumnHeadersVb::getLabelRowNum).max().orElse(0);
		
		ColumnHeadersVb matchingObj = orgColList.stream().
				filter(p -> p.getDbColumnName().equalsIgnoreCase(hiddenColumn)).
				findAny().orElse(null);
		
		final int hiddenColNum = matchingObj.getLabelColNum();
		final int hiddenRowNum = matchingObj.getLabelRowNum();
		
		if(maxHeaderRow > 1) {
			int rowNum = hiddenRowNum-1;
			for(int rnum = rowNum;rowNum >= 1;rowNum--) {
				ColumnHeadersVb Obj = new ColumnHeadersVb();
				try {
					Obj = orgColList.stream().
							filter(p -> (p.getLabelRowNum()== rnum && p.getLabelColNum()<= hiddenColNum)).
							max(Comparator.comparingInt(ColumnHeadersVb::getLabelColNum)).
							get();
				}catch(Exception e) {
					
				}
				for(int i=0;i < orgColList.size();i++) {
					if(orgColList.get(i).equals(Obj)) {
						orgColList.get(i).setColspan(orgColList.get(i).getColspan()-1);
						orgColList.get(i).setNumericColumnNo(99);
						if(orgColList.get(i).getColspan() == 0) {
							orgColList.remove(i);
						}
					}
				}
			}
		}
		for (ColumnHeadersVb colHeaderVb : orgColList) {
			if(colHeaderVb.getLabelColNum() > hiddenColNum) {
				  colHeaderVb.setLabelColNum(colHeaderVb.getLabelColNum() - 1);
			}
			if(!colHeaderVb.equals(matchingObj))
				updatedColList.add(colHeaderVb);
			
		}
		return updatedColList; 
	}
	public ExceptionCode getReportFilterSourceValue(ReportFilterVb vObject) {
		//LinkedHashMap<String, String> filterSourceVal = new LinkedHashMap<String, String>();
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			if(!ValidationUtil.isValid(vObject.getDefaultValueId())) {
				vObject.setDefaultValueId(vObject.getFilterSourceId());
			}
			vObject.setDateCreation(String.valueOf(Math.abs(ThreadLocalRandom.current().nextInt())));
			List<PrdQueryConfig> sourceQuerylst = reportsDao.getSqlQuery(vObject.getDefaultValueId());
			if (sourceQuerylst != null && sourceQuerylst.size() > 0) {
				PrdQueryConfig sourceQueryDet = sourceQuerylst.get(0);
				if ("QUERY".equalsIgnoreCase(sourceQueryDet.getDataRefType())) {
					sourceQueryDet.setQueryProc(replaceFilterHashVariables(sourceQueryDet.getQueryProc(), vObject));
					exceptionCode = reportsDao.getReportPromptsFilterValue(sourceQueryDet,null);
				}else if ("PROCEDURE".equalsIgnoreCase(sourceQueryDet.getDataRefType())) {
					if(sourceQueryDet.getQueryProc().contains("#")) {
						sourceQueryDet.setQueryProc(replaceFilterHashVariables(sourceQueryDet.getQueryProc(), vObject));
						exceptionCode = reportsDao.getFilterPromptWithHashVar(vObject,sourceQueryDet,"COMBO");
					}else {
						exceptionCode = reportsDao.getComboPromptData(vObject,sourceQueryDet);	
					}
				}
			}
		} catch (Exception e) {
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg(e.getMessage());
		}
		return exceptionCode;
	}

	public ExceptionCode exportReportToCsv(ReportsVb vObject) {
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			String reportTitle = "";
			reportTitle = vObject.getReportTitle();
			String[] hiddenColumns = null;
			if (ValidationUtil.isValid(vObject.getColumnsToHide())) {
				hiddenColumns = vObject.getColumnsToHide().split("!@#");
			}
			exceptionCode = getReportDetails(vObject);
			if (exceptionCode.getErrorCode() == Constants.SUCCESSFUL_OPERATION) {
				if (ValidationUtil.isValid(exceptionCode.getResponse())) {
					vObject = (ReportsVb) exceptionCode.getResponse();
				}
			} else {
				return exceptionCode;
			}
			List<ColumnHeadersVb> colHeaderslst = vObject.getColumnHeaderslst();
			List<ColumnHeadersVb> updatedLst = new ArrayList();
			List<ColumnHeadersVb> columnHeadersFinallst = new ArrayList<ColumnHeadersVb>();
			colHeaderslst.forEach(colHeadersVb -> {
				if (colHeadersVb.getColspan() <= 1) {
					columnHeadersFinallst.add(colHeadersVb);
				}
			});
			for (ColumnHeadersVb columnHeadersVb : columnHeadersFinallst) {
				if (hiddenColumns != null) {
					for (int ctr = 0; ctr < hiddenColumns.length; ctr++) {
						if (columnHeadersVb.getDbColumnName().equalsIgnoreCase(hiddenColumns[ctr])) {
							updatedLst.add(columnHeadersVb);
							break;
						}
					}
				}
			}

			if (updatedLst != null && !updatedLst.isEmpty()) {
				columnHeadersFinallst.removeAll(updatedLst);
			}
			exceptionCode = reportsDao.getResultData(vObject);
			List<HashMap<String, String>> dataLst = null;
			List<HashMap<String, String>> totalLst = null;
			ReportsVb resultVb = new ReportsVb();
			if (exceptionCode.getErrorCode() == Constants.SUCCESSFUL_OPERATION) {
				if (ValidationUtil.isValid(exceptionCode.getResponse())) {
					resultVb = (ReportsVb) exceptionCode.getResponse();
					dataLst = resultVb.getGridDataSet();
					totalLst = resultVb.getTotal();
				}
			}
			vObject.setReportTitle(reportTitle);
			int currentUserId = CustomContextHolder.getContext().getVisionId();
			CreateCsv createCSV = new CreateCsv();
			String filePath = System.getProperty("java.io.tmpdir");
			if (!ValidationUtil.isValid(filePath)) {
				filePath = System.getenv("TMP");
			}
			if (ValidationUtil.isValid(filePath)) {
				filePath = filePath + File.separator;
			}
			FileWriter fw = null;
			fw = new FileWriter(
					filePath + ValidationUtil.encode(vObject.getReportTitle()) + "_" + currentUserId + ".csv");
			PrintWriter out = new PrintWriter(fw);
			int rowNum = 0;
			int ctr = 1;
			rowNum = createCSV.writeHeadersToCsv(columnHeadersFinallst, vObject, fw, rowNum, out);
			do {
				ctr++;
				rowNum = createCSV.writeDataToCsv(columnHeadersFinallst, dataLst, vObject, currentUserId, fw, rowNum,
						out);
				vObject.setCurrentPage(ctr);
				dataLst = new ArrayList();
				exceptionCode = reportsDao.getResultData(vObject);
				if (ValidationUtil.isValid(exceptionCode.getResponse())) {
					resultVb = (ReportsVb) exceptionCode.getResponse();
					dataLst = resultVb.getGridDataSet();
				}
			} while (dataLst != null && !dataLst.isEmpty());

			if (totalLst != null)
				rowNum = createCSV.writeDataToCsv(columnHeadersFinallst, totalLst, vObject, currentUserId, fw, rowNum,
						out);

			out.flush();
			out.close();
			fw.close();
			exceptionCode.setResponse(filePath);
			exceptionCode.setOtherInfo(vObject.getReportTitle() + "_" + currentUserId);
			exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
		} catch (Exception e) {
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg(e.getMessage());
		}
		return exceptionCode;
	}

	@SuppressWarnings("unchecked")
	public ExceptionCode getWidgetList() {
		ExceptionCode exceptionCode = new ExceptionCode();
		ArrayList collTemp = new ArrayList();
		ArrayList<ReportFilterVb> filterLst = new ArrayList<>();
		ReportsVb reportVb = new ReportsVb();
		try {

			String defaultPrompt = commonDao.findVisionVariableValue("PRD_WIDGET_FILTER_" + productName + "");
			ExceptionCode globalFilterExcep = reportFilterProcess(defaultPrompt);
			ArrayList<ReportFilterVb> globalFilterLst = (ArrayList<ReportFilterVb>) globalFilterExcep.getResponse();
			ArrayList<ReportsVb> widgetlst = (ArrayList<ReportsVb>) reportsDao.getWidgetList();
			if (widgetlst != null && widgetlst.size() > 0) {
				for (ReportsVb reportsVb : widgetlst) {
					StringJoiner filterPosition = new StringJoiner("-");
					if ("Y".equalsIgnoreCase(reportsVb.getFilterFlag())) {
						HashMap<String, Object> filterMapVal = new HashMap<String, Object>();
						ExceptionCode exceptionCodeTemp = new ExceptionCode();
						exceptionCodeTemp = reportFilterProcess(reportsVb.getFilterRefCode());
						if (exceptionCodeTemp.getResponse() != null
								&& exceptionCodeTemp.getErrorCode() == Constants.SUCCESSFUL_OPERATION) {
							filterLst = (ArrayList<ReportFilterVb>) exceptionCodeTemp.getResponse();
						}
						if (filterLst != null && !filterLst.isEmpty()) {
							filterLst.forEach(vObj -> {
								// Global Filter Identifier Start
								Boolean globalFlag = false;
								List<ReportFilterVb> matchlst = globalFilterLst.stream()
										.filter(n -> n.getFilterSourceId().equalsIgnoreCase(vObj.getFilterSourceId()))
										.collect(Collectors.toList());

								if (matchlst != null && !matchlst.isEmpty()) {
									vObj.setGlobalFilter(true);
								} else {
									vObj.setGlobalFilter(false);
								}
								// End
								if (!vObj.getGlobalFilter()) {
									ExceptionCode exceptionCodeFilter = getFilterSourceValue(vObj);
									if (exceptionCodeFilter.getResponse() != null)
										vObj.setFilterValueMap(
												(LinkedHashMap<String, String>) exceptionCodeFilter.getResponse());
								}
							});
						}
						reportsVb.setFilterPosition(filterPosition.toString());
						List<ReportFilterVb> filterLstNew = filterLst.stream().filter(n -> n.getGlobalFilter() == false)
								.collect(Collectors.toList());

						int ctr = 1;
						for (ReportFilterVb vObj : filterLstNew) {
							vObj.setFilterSeq(ctr);
							ctr++;
						}
						filterLst.forEach(vObj -> {
							// Global Filter and Local Filter Identifier Start
							Boolean globalFlag = false;
							List<ReportFilterVb> matchlst = globalFilterLst.stream()
									.filter(n -> n.getFilterSourceId().equalsIgnoreCase(vObj.getFilterSourceId()))
									.collect(Collectors.toList());

							List<ReportFilterVb> localMatchlst = filterLstNew.stream()
									.filter(n -> n.getFilterSourceId().equalsIgnoreCase(vObj.getFilterSourceId()))
									.collect(Collectors.toList());

							if (matchlst != null && !matchlst.isEmpty()) {
								filterPosition.add("G" + matchlst.get(0).getFilterSeq());
							} else if (localMatchlst != null && !localMatchlst.isEmpty()) {
								filterPosition.add("L" + localMatchlst.get(0).getFilterSeq());
							}
							// End
						});
						reportsVb.setFilterPosition(filterPosition.toString());
						reportsVb.setReportFilters(filterLstNew);
					}
				}
			}
			collTemp.add(widgetlst);
			collTemp.add(defaultPrompt);
			ArrayList userWidgetlst = (ArrayList<ReportsVb>) reportsDao.getUserWidgets();
			collTemp.add(userWidgetlst);
			ArrayList savedUserDefLst = (ArrayList<ReportsVb>) reportsDao.getSavedUserDefSetting(reportVb, false);
			collTemp.add(savedUserDefLst);
			exceptionCode.setResponse(collTemp);
		} catch (Exception e) {
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg(e.getMessage());
		}
		return exceptionCode;
	}

	public ExceptionCode deleteSavedWidget(ReportsVb vObject) {
		ExceptionCode exceptionCode = new ExceptionCode();
		int retVal = 0;
		try {
			if (reportsDao.userWidgetExists(vObject) > 0) {
				retVal = reportsDao.deleteWidgetExists(vObject);
				if (retVal == Constants.ERRONEOUS_OPERATION) {
					exceptionCode.setErrorCode(Constants.ATTEMPT_TO_DELETE_UNEXISTING_RECORD);
					exceptionCode.setErrorMsg("Attempt to delete non-existant record!!");
				} else {
					exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
					exceptionCode.setErrorMsg("User Preferance Widget Deleted Successfully");
				}
			}
		} catch (Exception e) {
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg(e.getMessage());
		}
		exceptionCode.setOtherInfo(vObject);
		return exceptionCode;
	}

	public ExceptionCode saveWidget(ReportsVb vObject) {
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			deleteSavedWidget(vObject);
			int retVal = reportsDao.saveWidget(vObject);
			if (retVal == Constants.ERRONEOUS_OPERATION) {
				exceptionCode.setErrorCode(Constants.ATTEMPT_TO_DELETE_UNEXISTING_RECORD);
				exceptionCode.setErrorMsg("Unable to save widgets.Contact Admin!!");
			} else {
				exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
				exceptionCode.setErrorMsg("User Preferance Widget Saved Successfully");
			}
		} catch (Exception e) {
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg(e.getMessage());
		}
		exceptionCode.setOtherInfo(vObject);
		return exceptionCode;
	}

	public ExceptionCode exportWidgetToPdf(String reportTitle, String promptLabel, String gridReportIds,
			String fileName, ArrayList<ReportsVb> reportslst) {
		ExceptionCode exceptionCode = new ExceptionCode();
		ReportsVb reportsVb = new ReportsVb();
		VisionUsersVb visionUsersVb = CustomContextHolder.getContext();
		try {
			reportsVb.setMaker(visionUsersVb.getVisionId());
			reportsVb.setMakerName(visionUsersVb.getUserName());
			reportsVb.setReportTitle(reportTitle);
			reportsVb.setPromptLabel(promptLabel);
			String assetFolderUrl = servletContext.getRealPath("/WEB-INF/classes/images");
			if (ValidationUtil.isValid(gridReportIds)) {
				// Write Code for Grid Export
			}

			String screenCapturedPath = commonDao.findVisionVariableValue("PRD_EXPORT_PATH");
			String capturedImage = screenCapturedPath + fileName + ".png";
			exceptionCode = pdfExportUtil.dashboardExportToPdf(capturedImage, reportsVb, assetFolderUrl, fileName,
					reportslst);
			File lFile = new File(capturedImage);
			if (lFile.exists()) {
				lFile.delete();
			}
		} catch (Exception e) {
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg(e.getMessage());
		}
		return exceptionCode;
	}

	public ExceptionCode saveReportUserDef(ReportsVb vObject) {
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			deleteSavedReportUserDef(vObject);
			if (vObject.getReportUserDeflst() != null && !vObject.getReportUserDeflst().isEmpty()) {
				vObject.getReportUserDeflst().forEach(userDefVb -> {
					int retVal = reportsDao.saveReportUserDef(userDefVb);
				});
			}
			exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
			exceptionCode.setErrorMsg("Widgets saved Successfully");
		} catch (Exception e) {
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg(e.getMessage());
		}
		exceptionCode.setOtherInfo(vObject);
		return exceptionCode;
	}

	public ExceptionCode deleteSavedReportUserDef(ReportsVb vObject) {
		ExceptionCode exceptionCode = new ExceptionCode();
		int retVal = 0;
		try {
			if (vObject.getReportUserDeflst() != null && vObject.getReportUserDeflst().size() > 0) {
				vObject.getReportUserDeflst().forEach(userDefVb -> {
					if (reportsDao.reportUserDefExists(userDefVb) > 0) {
						reportsDao.deleteReportUserDef(userDefVb);
					}
				});
			}
			exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
			exceptionCode.setErrorMsg("Widgets deleted Successfully");
			exceptionCode.setOtherInfo(vObject);
		} catch (Exception e) {
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg(e.getMessage());
		}
		return exceptionCode;
	}

	public ReportsVb replaceProcedurePrompt(ReportsVb vObject) {
		vObject.setPromptValue1(
				ValidationUtil.isValid(vObject.getPromptValue1()) ? vObject.getPromptValue1().replaceAll("','", "'',''")
						: vObject.getPromptValue1());
		vObject.setPromptValue2(
				ValidationUtil.isValid(vObject.getPromptValue2()) ? vObject.getPromptValue2().replaceAll("','", "'',''")
						: vObject.getPromptValue2());
		vObject.setPromptValue3(
				ValidationUtil.isValid(vObject.getPromptValue3()) ? vObject.getPromptValue3().replaceAll("','", "'',''")
						: vObject.getPromptValue3());
		vObject.setPromptValue4(
				ValidationUtil.isValid(vObject.getPromptValue4()) ? vObject.getPromptValue4().replaceAll("','", "'',''")
						: vObject.getPromptValue4());
		vObject.setPromptValue5(
				ValidationUtil.isValid(vObject.getPromptValue5()) ? vObject.getPromptValue5().replaceAll("','", "'',''")
						: vObject.getPromptValue5());
		vObject.setPromptValue6(
				ValidationUtil.isValid(vObject.getPromptValue6()) ? vObject.getPromptValue6().replaceAll("','", "'',''")
						: vObject.getPromptValue6());
		vObject.setPromptValue7(
				ValidationUtil.isValid(vObject.getPromptValue7()) ? vObject.getPromptValue7().replaceAll("','", "'',''")
						: vObject.getPromptValue7());
		vObject.setPromptValue8(
				ValidationUtil.isValid(vObject.getPromptValue8()) ? vObject.getPromptValue8().replaceAll("','", "'',''")
						: vObject.getPromptValue8());
		vObject.setPromptValue9(
				ValidationUtil.isValid(vObject.getPromptValue9()) ? vObject.getPromptValue9().replaceAll("','", "'',''")
						: vObject.getPromptValue9());
		vObject.setPromptValue10(ValidationUtil.isValid(vObject.getPromptValue10())
				? vObject.getPromptValue10().replaceAll("','", "'',''")
				: vObject.getPromptValue10());
		vObject.setDataFilter1(
				ValidationUtil.isValid(vObject.getDataFilter1()) ? vObject.getDataFilter1().replaceAll("','", "'',''")
						: vObject.getDataFilter1());
		vObject.setDataFilter2(
				ValidationUtil.isValid(vObject.getDataFilter2()) ? vObject.getDataFilter2().replaceAll("','", "'',''")
						: vObject.getDataFilter2());
		vObject.setDataFilter3(
				ValidationUtil.isValid(vObject.getDataFilter3()) ? vObject.getDataFilter3().replaceAll("','", "'',''")
						: vObject.getDataFilter3());
		vObject.setDataFilter4(
				ValidationUtil.isValid(vObject.getDataFilter4()) ? vObject.getDataFilter4().replaceAll("','", "'',''")
						: vObject.getDataFilter4());
		vObject.setDataFilter5(
				ValidationUtil.isValid(vObject.getDataFilter5()) ? vObject.getDataFilter5().replaceAll("','", "'',''")
						: vObject.getDataFilter5());
		vObject.setDataFilter6(
				ValidationUtil.isValid(vObject.getDataFilter6()) ? vObject.getDataFilter6().replaceAll("','", "'',''")
						: vObject.getDataFilter6());
		vObject.setDataFilter7(
				ValidationUtil.isValid(vObject.getDataFilter7()) ? vObject.getDataFilter7().replaceAll("','", "'',''")
						: vObject.getDataFilter7());
		vObject.setDataFilter8(
				ValidationUtil.isValid(vObject.getDataFilter8()) ? vObject.getDataFilter8().replaceAll("','", "'',''")
						: vObject.getDataFilter8());
		vObject.setDataFilter9(
				ValidationUtil.isValid(vObject.getDataFilter9()) ? vObject.getDataFilter9().replaceAll("','", "'',''")
						: vObject.getDataFilter9());
		vObject.setDataFilter10(
				ValidationUtil.isValid(vObject.getDataFilter10()) ? vObject.getDataFilter10().replaceAll("','", "'',''")
						: vObject.getDataFilter10());
		return vObject;
	}

	public ExceptionCode saveReportSettings(ReportUserDefVb vObject) {
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			if (reportsDao.reportUserDefExists(vObject) > 0)
				reportsDao.deleteReportUserDef(vObject);
			int retVal = reportsDao.saveReportUserDef(vObject);
			if (retVal == Constants.SUCCESSFUL_OPERATION) {
				exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
				exceptionCode.setErrorMsg("Report settings saved Successfully");
			}
		} catch (Exception e) {
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg(e.getMessage());
		}
		exceptionCode.setOtherInfo(vObject);
		return exceptionCode;
	}
}
