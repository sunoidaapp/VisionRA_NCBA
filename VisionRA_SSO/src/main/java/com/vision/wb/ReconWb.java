package com.vision.wb;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.StringJoiner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.vision.dao.AbstractDao;
import com.vision.dao.AlphaSubTabDao;
import com.vision.dao.CommonDao;
import com.vision.dao.NumSubTabDao;
import com.vision.dao.ReconActivityFilterDao;
import com.vision.dao.ReconColumnDao;
import com.vision.dao.ReconConfigHeaderDao;
import com.vision.dao.ReconTabRelationDao;
import com.vision.exception.ExceptionCode;
import com.vision.util.Constants;
import com.vision.util.ValidationUtil;
import com.vision.vb.AlphaSubTabVb;
import com.vision.vb.CommonVb;
import com.vision.vb.ReconActivityFilterVb;
import com.vision.vb.ReconHeaderVb;

import edu.emory.mathcs.backport.java.util.concurrent.CopyOnWriteArrayList;

@Component
public class ReconWb extends AbstractDynaWorkerBean<ReconHeaderVb> {

	@Autowired
	private AlphaSubTabDao alphaSubTabDao;
	@Autowired
	private NumSubTabDao numSubTabDao;
	@Autowired
	private CommonDao commonDao;
	@Autowired
	private ReconConfigHeaderDao reconConfigHeaderDao;
	@Autowired
	private ReconColumnDao reconColumnDao;
	@Autowired
	private ReconActivityFilterDao reconActivityFilterDao;
	@Autowired
	private ReconTabRelationDao reconTabRelationDao;
	@Value("${app.databaseType}")
	private String databaseType;

	public ArrayList getPageLoadValues() {
		List collTemp = null;
		List<AlphaSubTabVb> ruleId = null;
		ArrayList<Object> arrListLocal = new ArrayList<Object>();
		String defaultRuleId = "";
		try {
			collTemp = numSubTabDao.findActiveNumSubTabsByNumTab(1);// status
			arrListLocal.add(collTemp);
			collTemp = numSubTabDao.findActiveNumSubTabsByNumTab(7);// record indicator
			arrListLocal.add(collTemp);
			ruleId = alphaSubTabDao.findActiveAlphaSubTabsByAlphaTab(7008);// rule id
			arrListLocal.add(ruleId);
			collTemp = numSubTabDao.findActiveNumSubTabsByNumTab(41);// join type
			arrListLocal.add(collTemp);
			collTemp = alphaSubTabDao.findActiveAlphaSubTabsByAlphaTab(7401); // condition operation
			arrListLocal.add(collTemp);
			ReconHeaderVb reconHeaderVb = new ReconHeaderVb();
			ExceptionCode exceptionCode = new ExceptionCode();
			for(AlphaSubTabVb alphaSubTabVb:ruleId) {
				reconHeaderVb.setRuleId(alphaSubTabVb.getAlphaSubTab());
				reconHeaderVb.setActionType("QUERY");
				exceptionCode = getQueryResults(reconHeaderVb);
				if (exceptionCode.getErrorCode() != 0) {
					defaultRuleId = alphaSubTabVb.getAlphaSubTab();
					break;
				}
			}
			arrListLocal.add(defaultRuleId);
			collTemp = reconColumnDao.getQueryReconColumnsRepository();
			arrListLocal.add(collTemp);
			return arrListLocal;
		} catch (Exception ex) {
			//ex.printStackTrace();
			// //logger.error("Exception in getting the Page load values.", ex);
			return null;
		}
	}

	@Override
	protected void setAtNtValues(ReconHeaderVb vObject) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void setVerifReqDeleteType(ReconHeaderVb vObject) {
		ArrayList<CommonVb> lCommVbList = (ArrayList<CommonVb>) commonDao
				.findVerificationRequiredAndStaticDelete("RA_RECON_TAB");
		vObject.setStaticDelete(lCommVbList.get(0).isStaticDelete());
		vObject.setVerificationRequired(lCommVbList.get(0).isVerificationRequired());
	}

	@Override
	protected AbstractDao<ReconHeaderVb> getScreenDao() {
		return reconConfigHeaderDao;
	}

	public ExceptionCode getReconTableName(ReconHeaderVb vObject) {
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			exceptionCode= doValidate(vObject);
			if(exceptionCode.getErrorCode() == Constants.ERRONEOUS_OPERATION){
				return exceptionCode;
			}
			List<ReconHeaderVb> collTemp = reconConfigHeaderDao.getReconTableName(vObject);
			exceptionCode.setResponse(collTemp);
			exceptionCode.setErrorMsg("Recon Tables Fetched");
			exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
		} catch (Exception e) {
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			String errorMsg = reconConfigHeaderDao.parseErrorMsg(e);
			exceptionCode.setErrorMsg(errorMsg);
		}
		return exceptionCode;
	}

	public ExceptionCode getReconColName(List<ReconHeaderVb> tableNamesList) {
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			exceptionCode= doValidate(tableNamesList.get(0));
			if(exceptionCode.getErrorCode() == Constants.ERRONEOUS_OPERATION){
				return exceptionCode;
			}
			ArrayList<ReconHeaderVb> tableColList = new ArrayList<>();
			if(tableNamesList != null && !tableNamesList.isEmpty()) {
				for (ReconHeaderVb tableName : tableNamesList) {
					List<ReconHeaderVb> colNamesList = new ArrayList<>();
					colNamesList = reconConfigHeaderDao.getReconColName(tableName);
					if (colNamesList != null && colNamesList.size() > 0) {
						if("ORACLE".equalsIgnoreCase(databaseType)) {
							ReconHeaderVb reconHeaderVb = new ReconHeaderVb();
							reconHeaderVb.setColName("ROWID");
							reconHeaderVb.setColAliasName("ROWID");
							reconHeaderVb.setDataType("T");
							reconHeaderVb.setColId(colNamesList.size()+1);
							colNamesList.add(reconHeaderVb);
						}
						tableName.setChildren(colNamesList);
					}
					tableColList.add(tableName);
				}
			}
			if (tableColList != null && tableColList.size() > 0) {
				exceptionCode.setResponse(tableColList);
				exceptionCode.setErrorMsg("Recon Columns Fetched");
				exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
			}
		} catch (Exception e) {
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			String errorMsg = reconConfigHeaderDao.parseErrorMsg(e);
			exceptionCode.setErrorMsg(errorMsg);
		}
		return exceptionCode;
	}

	@Override
	public ExceptionCode getQueryResults(ReconHeaderVb vObject) {
		int intStatus = 1;
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			exceptionCode= doValidate(vObject);
			if(exceptionCode.getErrorCode() == Constants.ERRONEOUS_OPERATION){
				return exceptionCode;
			}
			setVerifReqDeleteType(vObject);
			Boolean pendFlag = true;
			List<ReconHeaderVb> selectionCriteriaLst = new ArrayList<>();
			List<ReconActivityFilterVb> relationJoinsLst = new ArrayList<>();
			List<ReconActivityFilterVb> relationFiltersLst = new ArrayList<>();
			List<ReconActivityFilterVb> relationLst = new ArrayList<>();
			List<ReconActivityFilterVb> filterLst = new ArrayList<>();
			List<ReconHeaderVb> children = new ArrayList<>();
			ArrayList<ReconHeaderVb> tableLst = reconConfigHeaderDao.getAllQueryReconTables(vObject, intStatus);
			vObject.setTableNamesList(tableLst);

			if (tableLst.size() == 0) {
				intStatus = 0;
				tableLst = reconConfigHeaderDao.getAllQueryReconTables(vObject, intStatus);
				vObject.setTableNamesList(tableLst);
				pendFlag = false;
			}

			if (tableLst.size() == 0) {
//				exceptionCode = CommonUtils.getResultObject(getScreenDao().getServiceDesc(), 16, "Query", "");
				exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
				exceptionCode.setResponse(vObject);
				exceptionCode.setOtherInfo(vObject);
				return exceptionCode;
			} else {

				for (ReconHeaderVb reconHeaderVb : tableLst) {
					children = reconConfigHeaderDao.getReconColName(reconHeaderVb);
					children.forEach(columns ->{
						columns.setTableId(reconHeaderVb.getTableId());
					});
					if (children != null && children.size() > 0) {
						reconHeaderVb.setChildren(children);
					}
				}
				filterLst = reconActivityFilterDao.getQueryReconRelationFilter(vObject, intStatus,"FILTER");
				selectionCriteriaLst = reconColumnDao.getQueryReconColumns(vObject, intStatus);
				relationFiltersLst = reconTabRelationDao.getQueryReconTabRelation(vObject, intStatus,true);
				//relationJoinsLst =  reconTabRelationDao.getQueryReconTabRelation(vObject, intStatus,false);
				relationJoinsLst = reconTabRelationDao.getLhsAndRhsTableId(vObject.getRuleId(),intStatus);
				if(relationJoinsLst != null && relationJoinsLst.size() > 0) {
					for(ReconActivityFilterVb reconActivityFilterVb : relationJoinsLst) {
						relationLst = reconActivityFilterDao.getQueryReconActFilterRec(reconActivityFilterVb, intStatus);
						if(relationLst != null && relationLst.size() > 0) {
							reconActivityFilterVb.setJoinsLst(relationLst);
						}
					}
				}
				
				if(filterLst != null && filterLst.size() > 0) {
					vObject.setFiltersLst(filterLst);
				}
				
				if (selectionCriteriaLst != null && selectionCriteriaLst.size() > 0) {
					vObject.setSelectionCriteriaLst(selectionCriteriaLst);
				}
//				if (relationJoinsLst != null && relationJoinsLst.size() > 0) {
					if(relationFiltersLst != null && !relationFiltersLst.isEmpty()) {
						for(ReconActivityFilterVb filterVb :relationFiltersLst) {
							int count = 0;
							for(ReconActivityFilterVb joinsVb:relationJoinsLst) {
								if(filterVb.getFromTableId() == joinsVb.getFromTableId() && filterVb.getToTableId() == joinsVb.getToTableId()) 
									relationJoinsLst.get(count).setJoinType(filterVb.getJoinType());
								count++;
							}
						}
//					}
					vObject.setRelationJoinsLst(relationJoinsLst);
				}
				
				if(relationFiltersLst != null && relationFiltersLst.size() > 0) {
					vObject.setJoinString2(relationFiltersLst.get(0).getJoinString2());
					vObject.setFilterConditon1(relationFiltersLst.get(0).getFilterConditon1());
				}
				vObject.setRecordIndicator(tableLst.get(0).getRecordIndicator());
				vObject.setRecordIndicatorDesc(tableLst.get(0).getRecordIndicatorDesc());
				vObject.setMaker(tableLst.get(0).getMaker());
				vObject.setVerifier(tableLst.get(0).getVerifier());
				vObject.setMakerName(tableLst.get(0).getMakerName());
				vObject.setVerifierName(tableLst.get(0).getVerifierName());
				vObject.setReconStatus(tableLst.get(0).getReconStatus());
				vObject.setReconStatusDesc(tableLst.get(0).getReconStatusDesc());
				vObject.setDateCreation(tableLst.get(0).getDateCreation());
				vObject.setDateLastModified(tableLst.get(0).getDateLastModified());
			}
			exceptionCode.setOtherInfo(vObject);
			exceptionCode.setResponse(vObject);
			exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
			exceptionCode.setErrorMsg("Successful operation");
		} catch(Exception e) {
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			String errorMsg = reconConfigHeaderDao.parseErrorMsg(e);
			exceptionCode.setErrorMsg(errorMsg);
		}
		return exceptionCode;

	}

	public ExceptionCode showQuery(ReconHeaderVb vObject) {
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			exceptionCode = doValidate(vObject);
			if(exceptionCode.getErrorCode() == Constants.ERRONEOUS_OPERATION){
				return exceptionCode;
			}
			List<ReconHeaderVb> tableNamesList = new CopyOnWriteArrayList(vObject.getTableNamesList());
			ArrayList<ReconHeaderVb> criteriaLst =  new ArrayList<>();
			List<ReconHeaderVb> selCriteriaLst = new CopyOnWriteArrayList(vObject.getSelectionCriteriaLst());
			HashMap<Integer,String> tableIdMap = new HashMap<Integer,String>();
			ArrayList<ReconHeaderVb> orderByList =  new ArrayList<>();
			StringJoiner tableNameStr = new StringJoiner(",");
			
			if (tableNamesList != null && tableNamesList.size() > 0) {
				for (ReconHeaderVb tableName : tableNamesList) {
					if (selCriteriaLst != null && selCriteriaLst.size() > 0) {
						for (ReconHeaderVb columns : selCriteriaLst) {
							if (columns.getTableId() == tableName.getTableId() && ValidationUtil.isValid(columns.getColType()) && "C".equals(columns.getColType())) {
								columns.setTableName(tableName.getTableName());
								columns.setAliasName(tableName.getAliasName());
								criteriaLst.add(columns);
								selCriteriaLst.remove(columns);
							} else if(ValidationUtil.isValid(columns.getColType()) && "E".equals(columns.getColType())){
								criteriaLst.add(columns);
								selCriteriaLst.remove(columns);
							}
						}
					}
					tableIdMap.put(tableName.getTableId(), tableName.getTableName()+" "+tableName.getAliasName());
					tableNameStr.add(tableName.getTableName()+" "+tableName.getAliasName());
				}
			}
			criteriaLst.sort(Comparator.comparingInt(ReconHeaderVb::getColId));
			
			StringJoiner selectColumns = new StringJoiner(",");
			StringJoiner groupByColumns = new StringJoiner(",");
			StringJoiner orderByColumns = new StringJoiner(",");
			
			if(criteriaLst != null && criteriaLst.size() > 0) {
				criteriaLst.forEach(n -> {
					String colStr = "";
					if(ValidationUtil.isValid(n.getColType()) && "E".equals(n.getColType())){
						colStr = n.getColName()+" "+n.getColAliasName();
					}else if(ValidationUtil.isValid(n.getAggFunc()) && !n.getAggFunc().equals("NA")) {
						if(n.getAggFunc().equalsIgnoreCase("COUNT(Distinct)")) {
							n.setAggFunc("COUNT(Distinct ");
							colStr =n.getAggFunc()+n.getAliasName()+"."+n.getColName()+") "+n.getColAliasName();
						}else {
							colStr = n.getAggFunc()+"("+n.getAliasName()+"."+n.getColName()+") "+n.getColAliasName();
						}
					}else {
						colStr = n.getAliasName()+"."+n.getColName()+" "+n.getColAliasName();
					}
					selectColumns.add(colStr);
					if (ValidationUtil.isValid(n.getGroupBy()) && n.getGroupBy().equals("Y")) {
						if ("E".equalsIgnoreCase(n.getColType())) {
							groupByColumns.add(n.getColName());
						} else {
							groupByColumns.add(n.getAliasName() + "." + n.getColName());
						}
					}
					if (ValidationUtil.isValid(n.getOrderBy())) {
						orderByList.add(n);
//						orderByColumns.add(n.getAliasName()+"."+n.getColName());
					}
				});
				orderByList.sort(Comparator.comparingInt(n -> Integer.parseInt(n.getOrderBy())));
				if (orderByList != null & orderByList.size() > 0) {
					for (ReconHeaderVb orderBy : orderByList) {
						if ("E".equalsIgnoreCase(orderBy.getColType())) {
							orderByColumns.add(orderBy.getColAliasName());
						} else {
							orderByColumns.add(orderBy.getAliasName() + "." + orderBy.getColName());
						}
					}
				}
			}
			boolean condApply = false;
			String[] relationId = new String[10];
			String[] relationCondition = new String[10];
			int ctr = 0;
			StringBuffer whereCond = new StringBuffer(" ");
			StringJoiner relationJoiner = new StringJoiner(",");
			ArrayList<TableRelations> tableRellst = new ArrayList<TableRelations>();
			if(vObject.getRelationJoinsLst() != null && !vObject.getRelationJoinsLst().isEmpty()) {
				for(ReconActivityFilterVb relVb : vObject.getRelationJoinsLst()) {
					condApply = false;
					String fromTable = tableIdMap.get(relVb.getFromTableId());
					String toTable = tableIdMap.get(relVb.getToTableId());
					if(!ValidationUtil.isValid(fromTable) || !ValidationUtil.isValid(toTable))
						continue;
					
					String joinType = "";
					String joinStrings = (ValidationUtil.isValid(relVb.getJoinString1()) ? relVb.getJoinString1() : "")
							+ (ValidationUtil.isValid(relVb.getJoinString2()) ? " AND "+relVb.getJoinString2()+"":"")
							+ (ValidationUtil.isValid(relVb.getJoinString3()) ? " AND "+relVb.getJoinString3()+"":"")
							+ (ValidationUtil.isValid(relVb.getJoinString4()) ? " AND "+relVb.getJoinString4()+"":"")
							+ (ValidationUtil.isValid(relVb.getJoinString5()) ? " AND "+relVb.getJoinString5()+"":"");
					
					switch (relVb.getJoinType()) {
						case 2:
							joinType = "LEFT JOIN";
							break;
						case 3:
							joinType= "RIGHT JOIN";
							break;
						case 4:
							joinType= "FULL OUTER JOIN";
							break;	
						default:
							joinType="INNER JOIN";
					}
					TableRelations tableRelations = new TableRelations();
					tableRelations.setFromTableId(fromTable);
					tableRelations.setToTableId(toTable);
					tableRelations.setFromTable(relVb.getFromTableId());
					tableRelations.setToTable(relVb.getToTableId());
					tableRelations.setJoinString(joinStrings);
					tableRelations.setJoinType(joinType);
					exceptionCode = getTableRelationsList(tableRellst,tableRelations);
					if(exceptionCode.getErrorCode() != Constants.ERRONEOUS_OPERATION) {
						tableRellst = (ArrayList<TableRelations>)exceptionCode.getRequest();
					}
				}
			}
			if(tableRellst != null && tableRellst.size() > 0) {
				for(TableRelations relVb : tableRellst) {
					for(int ct = 0;ct < relationId.length;ct++) {
						if(ValidationUtil.isValid(relationId[ct])) {
							if(relationId[ct].contains("_"+relVb.getFromTableId()) 
									|| relationId[ct].contains(relVb.getFromTableId()+"_")){
								relationId[ct] = relationId[ct]+";"+relVb.getFromTableId()+"_"+relVb.getToTableId();
								relationCondition[ct] = relationCondition[ct]+" "+relVb.getJoinType()+" "+relVb.getToTableId() +" ON ("+relVb.getJoinString()+")";
								condApply = true;
								break;
							}
						}
					}
					//ctr++;
					if(!condApply) {
						relationId[ctr] = relationId[ctr]+";"+relVb.getFromTableId()+"_"+relVb.getToTableId();
						relationCondition[ctr] = relVb.getFromTableId()+" "+relVb.getJoinType()+" "+relVb.getToTableId()+" ON ("+relVb.getJoinString()+")";
					}
				}	
			}
			String filterCondition = (ValidationUtil.isValid(vObject.getFilterConditon1()) ? vObject.getFilterConditon1() : "");
			if(ValidationUtil.isValid(filterCondition)){
				if(ValidationUtil.isValid(whereCond.toString())){
					whereCond.append(" AND ");
				}
				whereCond .append(" "+filterCondition);
			}
			if(ValidationUtil.isValid(whereCond.toString()) && "AND ".equalsIgnoreCase(whereCond.toString().substring(whereCond.toString().length()-4, whereCond.toString().length()))){
				String cnd= whereCond.toString().substring(0, whereCond.toString().length()-4);
				whereCond.setLength(0);
				whereCond.append(cnd);
			}

			for(String relCond : relationCondition) {
				if(ValidationUtil.isValid(relCond))
					relationJoiner.add(relCond);
			}
			String finalSelQuery = "";
			if(ValidationUtil.isValid(relationJoiner.toString())){
				if(ValidationUtil.isValid(whereCond.toString()))
					finalSelQuery = "SELECT "+selectColumns.toString()+" FROM "+relationJoiner.toString()+" WHERE "+whereCond.toString()+"";
				else 
					finalSelQuery = "SELECT "+selectColumns.toString()+" FROM "+relationJoiner.toString()+" ";	
			}else {
				if(ValidationUtil.isValid(whereCond.toString()))
					finalSelQuery = "SELECT "+selectColumns.toString()+" FROM "+tableNameStr.toString()+" WHERE "+whereCond.toString()+"";
				else 
					finalSelQuery = "SELECT "+selectColumns.toString()+" FROM "+tableNameStr.toString()+" ";
			}
			if(ValidationUtil.isValid(groupByColumns.toString())) {
				finalSelQuery = finalSelQuery+" GROUP BY "+groupByColumns;
			}
			if(ValidationUtil.isValid(orderByColumns.toString())) {
				finalSelQuery = finalSelQuery+" ORDER BY "+orderByColumns;
			}
			exceptionCode.setRequest(tableRellst);
			exceptionCode.setResponse(finalSelQuery);
			exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
			exceptionCode.setErrorMsg("Successful operation");
		} catch(Exception e) {
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			String errorMsg = reconConfigHeaderDao.parseErrorMsg(e);
			exceptionCode.setErrorMsg(errorMsg);
		}
		return exceptionCode;
	}
	public ExceptionCode getTableRelationsList(ArrayList<TableRelations> tableRellst,TableRelations tableRelations) {
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			int fromTableIndex = 0;
			int toTableIndex = 0;
			if(tableRellst != null && !tableRellst.isEmpty()) {
				//From Table - Check 
				for(int idx = 0;idx < tableRellst.size();idx++) {
					if(tableRellst.get(idx).getFromTableId().equals(tableRelations.getFromTableId())) {
						fromTableIndex = idx+1;
					}else if(tableRellst.get(idx).getToTableId().equals(tableRelations.getFromTableId())) {
						fromTableIndex = idx+1;
					}
				}
				//To Table - Check 
				for(int idx = 0;idx < tableRellst.size();idx++) {
					if(tableRellst.get(idx).getFromTableId().equals(tableRelations.getToTableId())) {
						toTableIndex = idx+1;
					}else if(tableRellst.get(idx).getToTableId().equals(tableRelations.getToTableId())) {
						toTableIndex = idx+1;
					}
				}
			}
			if(fromTableIndex == 0 || toTableIndex == 0) {
				tableRellst.add(tableRelations);	
			}else {
				if(fromTableIndex < toTableIndex) {
					String relationJoinString = tableRellst.get(toTableIndex-1).getJoinString();
					relationJoinString = relationJoinString+" AND "+tableRelations.getJoinString();
					tableRellst.get(toTableIndex-1).setJoinString(relationJoinString);
				}else {
					String relationJoinString = tableRellst.get(fromTableIndex-1).getJoinString();
					relationJoinString = relationJoinString+" AND "+tableRelations.getJoinString();
					tableRellst.get(fromTableIndex-1).setJoinString(relationJoinString);	
				}
			}
			exceptionCode.setRequest(tableRellst);
			exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
		} catch(Exception e) {
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			String errorMsg = reconConfigHeaderDao.parseErrorMsg(e);
			exceptionCode.setErrorMsg(errorMsg);
		}
		return exceptionCode;
	}
	class TableRelations{
		String fromTableId = "";
		String toTableId = "";
		int fromTable;
		int toTable;
		String joinString = "";
		String joinType = "INNER JOIN";
		
		public String getFromTableId() {
			return fromTableId;
		}
		public void setFromTableId(String fromTableId) {
			this.fromTableId = fromTableId;
		}
		public String getToTableId() {
			return toTableId;
		}
		public void setToTableId(String toTableId) {
			this.toTableId = toTableId;
		}
		public String getJoinString() {
			return joinString;
		}
		public void setJoinString(String joinString) {
			this.joinString = joinString;
		}
		public String getJoinType() {
			return joinType;
		}
		public void setJoinType(String joinType) {
			this.joinType = joinType;
		}
		public int getFromTable() {
			return fromTable;
		}
		public void setFromTable(int fromTable) {
			this.fromTable = fromTable;
		}
		public int getToTable() {
			return toTable;
		}
		public void setToTable(int toTable) {
			this.toTable = toTable;
		}
	}
	public ExceptionCode getToTableId(ReconHeaderVb vObject) {
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			exceptionCode = doValidate(vObject);
			if(exceptionCode.getErrorCode() == Constants.ERRONEOUS_OPERATION){
				return exceptionCode;
			}
			List<AlphaSubTabVb> ruleIdsLst = reconConfigHeaderDao.getRuleIdLstforCopy();
			exceptionCode.setResponse(ruleIdsLst);
			exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
		} catch(Exception e) {
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			String errorMsg = reconConfigHeaderDao.parseErrorMsg(e);
			exceptionCode.setErrorMsg(errorMsg);
		}
		return exceptionCode;
	}

	public ExceptionCode validateQuery(ReconHeaderVb vObject) {
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			exceptionCode = doValidate(vObject);
			if(exceptionCode.getErrorCode() == Constants.ERRONEOUS_OPERATION){
				return exceptionCode;
			}
			boolean validSql = vObject.getQuery().toUpperCase().startsWith("SELECT");
			if(!validSql) {
				exceptionCode.setErrorMsg("The provided sql statement is not Valid select query");
				exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
				return exceptionCode;
			} 
			exceptionCode = reconConfigHeaderDao.checkValidQuery(vObject.getQuery());
			if(exceptionCode.getErrorCode() == Constants.SUCCESSFUL_OPERATION) {
				exceptionCode.setErrorMsg("Query Validation Success");
			}
		} catch(Exception e) {
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			String errorMsg = reconConfigHeaderDao.parseErrorMsg(e);
			exceptionCode.setErrorMsg(errorMsg);
		}
		return exceptionCode;
	}
	public ExceptionCode doValidate(ReconHeaderVb vObject){
		ExceptionCode exceptionCode = new ExceptionCode();
		String operation = vObject.getActionType();
		String srtRestriction = getCommonDao().getRestrictionsByUsers("ReconConfig", operation);
		if(!"Y".equalsIgnoreCase(srtRestriction)) {
			exceptionCode = new ExceptionCode();
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg(operation +" "+Constants.userRestrictionMsg);
			return exceptionCode;
		}
		exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
		return exceptionCode;
	}
	protected ExceptionCode doValidate(List<ReconHeaderVb> vObjects) {
		ExceptionCode exceptionCode = new ExceptionCode();
		ReconHeaderVb vObject = vObjects.get(0);
		String operation = vObject.getActionType();
		String srtRestriction = getCommonDao().getRestrictionsByUsers("ReconConfig", operation);
		if(!"Y".equalsIgnoreCase(srtRestriction)) {
			exceptionCode = new ExceptionCode();
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg(operation +" "+Constants.userRestrictionMsg);
			return exceptionCode;
		}
		exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
		return exceptionCode;
	
	}
}

