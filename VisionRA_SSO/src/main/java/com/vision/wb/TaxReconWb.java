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
import com.vision.dao.TaxReconActivityFilterDao;
import com.vision.dao.TaxReconColumnDao;
import com.vision.dao.TaxReconConfigHeaderDao;
import com.vision.dao.TaxReconTabRelationDao;
import com.vision.exception.ExceptionCode;
import com.vision.util.Constants;
import com.vision.util.ValidationUtil;
import com.vision.vb.AlphaSubTabVb;
import com.vision.vb.CommonVb;
import com.vision.vb.TaxReconActivityFilterVb;
import com.vision.vb.TaxReconHeaderVb;

import edu.emory.mathcs.backport.java.util.concurrent.CopyOnWriteArrayList;

@Component
public class TaxReconWb extends AbstractDynaWorkerBean<TaxReconHeaderVb> {

	@Autowired
	private AlphaSubTabDao alphaSubTabDao;
	@Autowired
	private NumSubTabDao numSubTabDao;
	@Autowired
	private CommonDao commonDao;
	@Autowired
	private TaxReconConfigHeaderDao taxReconConfigHeaderDao;
	@Autowired
	private TaxReconColumnDao taxReconColumnDao;
	@Autowired
	private TaxReconActivityFilterDao taxReconActivityFilterDao;
	@Autowired
	private TaxReconTabRelationDao taxReconTabRelationDao;
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
			TaxReconHeaderVb TaxReconHeaderVb = new TaxReconHeaderVb();
			ExceptionCode exceptionCode = new ExceptionCode();
			for(AlphaSubTabVb alphaSubTabVb:ruleId) {
				TaxReconHeaderVb.setRuleId(alphaSubTabVb.getAlphaSubTab());
				TaxReconHeaderVb.setActionType("QUERY");
				exceptionCode = getQueryResults(TaxReconHeaderVb);
				if (exceptionCode.getErrorCode() != 0) {
					defaultRuleId = alphaSubTabVb.getAlphaSubTab();
					break;
				}
			}
			arrListLocal.add(defaultRuleId);
			collTemp = taxReconColumnDao.getQueryReconColumnsRepository();
			arrListLocal.add(collTemp);
			return arrListLocal;
		} catch (Exception ex) {
			//ex.printStackTrace();
			// //logger.error("Exception in getting the Page load values.", ex);
			return null;
		}
	}

	@Override
	protected void setAtNtValues(TaxReconHeaderVb vObject) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void setVerifReqDeleteType(TaxReconHeaderVb vObject) {
		ArrayList<CommonVb> lCommVbList = (ArrayList<CommonVb>) commonDao
				.findVerificationRequiredAndStaticDelete("RA_RECON_TAB");
		vObject.setStaticDelete(lCommVbList.get(0).isStaticDelete());
		vObject.setVerificationRequired(lCommVbList.get(0).isVerificationRequired());
	}

	@Override
	protected AbstractDao<TaxReconHeaderVb> getScreenDao() {
		return taxReconConfigHeaderDao;
	}

	public ExceptionCode getReconTableName(TaxReconHeaderVb vObject) {
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			exceptionCode= doValidate(vObject);
			if(exceptionCode.getErrorCode() == Constants.ERRONEOUS_OPERATION){
				return exceptionCode;
			}
			List<TaxReconHeaderVb> collTemp = taxReconConfigHeaderDao.getReconTableName(vObject);
			exceptionCode.setResponse(collTemp);
			exceptionCode.setErrorMsg("Recon Tables Fetched");
			exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
		} catch (Exception e) {
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			String errorMsg = taxReconConfigHeaderDao.parseErrorMsg(e);
			exceptionCode.setErrorMsg(errorMsg);
		}
		return exceptionCode;
	}

	public ExceptionCode getReconColName(List<TaxReconHeaderVb> tableNamesList) {
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			exceptionCode= doValidate(tableNamesList.get(0));
			if(exceptionCode.getErrorCode() == Constants.ERRONEOUS_OPERATION){
				return exceptionCode;
			}
			ArrayList<TaxReconHeaderVb> tableColList = new ArrayList<>();
			if(tableNamesList != null && !tableNamesList.isEmpty()) {
				for (TaxReconHeaderVb tableName : tableNamesList) {
					List<TaxReconHeaderVb> colNamesList = new ArrayList<>();
					colNamesList = taxReconConfigHeaderDao.getReconColName(tableName);
					if (colNamesList != null && colNamesList.size() > 0) {
						if("ORACLE".equalsIgnoreCase(databaseType)) {
							TaxReconHeaderVb TaxReconHeaderVb = new TaxReconHeaderVb();
							TaxReconHeaderVb.setColName("ROWID");
							TaxReconHeaderVb.setColAliasName("ROWID");
							TaxReconHeaderVb.setDataType("T");
							TaxReconHeaderVb.setColId(colNamesList.size()+1);
							colNamesList.add(TaxReconHeaderVb);
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
			String errorMsg = taxReconConfigHeaderDao.parseErrorMsg(e);
			exceptionCode.setErrorMsg(errorMsg);
		}
		return exceptionCode;
	}

	@Override
	public ExceptionCode getQueryResults(TaxReconHeaderVb vObject) {
		int intStatus = 1;
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			exceptionCode= doValidate(vObject);
			if(exceptionCode.getErrorCode() == Constants.ERRONEOUS_OPERATION){
				return exceptionCode;
			}
			setVerifReqDeleteType(vObject);
			Boolean pendFlag = true;
			List<TaxReconHeaderVb> selectionCriteriaLst = new ArrayList<>();
			List<TaxReconActivityFilterVb> relationJoinsLst = new ArrayList<>();
			List<TaxReconActivityFilterVb> relationFiltersLst = new ArrayList<>();
			List<TaxReconActivityFilterVb> relationLst = new ArrayList<>();
			List<TaxReconActivityFilterVb> filterLst = new ArrayList<>();
			List<TaxReconHeaderVb> children = new ArrayList<>();
			ArrayList<TaxReconHeaderVb> tableLst = taxReconConfigHeaderDao.getAllQueryReconTables(vObject, intStatus);
			vObject.setTableNamesList(tableLst);

			if (tableLst.size() == 0) {
				intStatus = 0;
				tableLst = taxReconConfigHeaderDao.getAllQueryReconTables(vObject, intStatus);
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

				for (TaxReconHeaderVb TaxReconHeaderVb : tableLst) {
					children = taxReconConfigHeaderDao.getReconColName(TaxReconHeaderVb);
					children.forEach(columns ->{
						columns.setTableId(TaxReconHeaderVb.getTableId());
					});
					if (children != null && children.size() > 0) {
						TaxReconHeaderVb.setChildren(children);
					}
				}
				filterLst = taxReconActivityFilterDao.getQueryReconRelationFilter(vObject, intStatus,"FILTER");
				selectionCriteriaLst = taxReconColumnDao.getQueryReconColumns(vObject, intStatus);
				relationFiltersLst = taxReconTabRelationDao.getQueryReconTabRelation(vObject, intStatus,true);
				//relationJoinsLst =  taxReconTabRelationDao.getQueryReconTabRelation(vObject, intStatus,false);
				relationJoinsLst = taxReconTabRelationDao.getLhsAndRhsTableId(vObject.getRuleId(),intStatus);
				if(relationJoinsLst != null && relationJoinsLst.size() > 0) {
					for(TaxReconActivityFilterVb TaxReconActivityFilterVb : relationJoinsLst) {
						relationLst = taxReconActivityFilterDao.getQueryReconActFilterRec(TaxReconActivityFilterVb, intStatus);
						if(relationLst != null && relationLst.size() > 0) {
							TaxReconActivityFilterVb.setJoinsLst(relationLst);
						}
					}
				}
				
				if(filterLst != null && filterLst.size() > 0) {
					vObject.setFiltersLst(filterLst);
				}
				
				if (selectionCriteriaLst != null && selectionCriteriaLst.size() > 0) {
					vObject.setSelectionCriteriaLst(selectionCriteriaLst);
				}
				if (relationJoinsLst != null && relationJoinsLst.size() > 0) {
					if(relationFiltersLst != null && !relationFiltersLst.isEmpty()) {
						for(TaxReconActivityFilterVb filterVb :relationFiltersLst) {
							int count = 0;
							for(TaxReconActivityFilterVb joinsVb:relationJoinsLst) {
								if(filterVb.getFromTableId() == joinsVb.getFromTableId() && filterVb.getToTableId() == joinsVb.getToTableId()) 
									relationJoinsLst.get(count).setJoinType(filterVb.getJoinType());
								count++;
							}
						}
					}
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
			String errorMsg = taxReconConfigHeaderDao.parseErrorMsg(e);
			exceptionCode.setErrorMsg(errorMsg);
		}
		return exceptionCode;

	}

	public ExceptionCode showQuery(TaxReconHeaderVb vObject) {
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			exceptionCode = doValidate(vObject);
			if(exceptionCode.getErrorCode() == Constants.ERRONEOUS_OPERATION){
				return exceptionCode;
			}
			List<TaxReconHeaderVb> tableNamesList = new CopyOnWriteArrayList(vObject.getTableNamesList());
			ArrayList<TaxReconHeaderVb> criteriaLst =  new ArrayList<>();
			List<TaxReconHeaderVb> selCriteriaLst = new CopyOnWriteArrayList(vObject.getSelectionCriteriaLst());
			HashMap<Integer,String> tableIdMap = new HashMap<Integer,String>();
			ArrayList<TaxReconHeaderVb> orderByList =  new ArrayList<>();
			StringJoiner tableNameStr = new StringJoiner(",");
			
			if (tableNamesList != null && tableNamesList.size() > 0) {
				for (TaxReconHeaderVb tableName : tableNamesList) {
					if (selCriteriaLst != null && selCriteriaLst.size() > 0) {
						for (TaxReconHeaderVb columns : selCriteriaLst) {
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
			criteriaLst.sort(Comparator.comparingInt(TaxReconHeaderVb::getColId));
			
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
					for (TaxReconHeaderVb orderBy : orderByList) {
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
				for(TaxReconActivityFilterVb relVb : vObject.getRelationJoinsLst()) {
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
								if(relationId[ct].contains("_"+relVb.getToTableId()) 
										|| relationId[ct].contains(relVb.getToTableId()+"_")){
									
								}else {
									
								}
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
			String errorMsg = taxReconConfigHeaderDao.parseErrorMsg(e);
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
			String errorMsg = taxReconConfigHeaderDao.parseErrorMsg(e);
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
	public ExceptionCode getToTableId(TaxReconHeaderVb vObject) {
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			exceptionCode = doValidate(vObject);
			if(exceptionCode.getErrorCode() == Constants.ERRONEOUS_OPERATION){
				return exceptionCode;
			}
			List<AlphaSubTabVb> ruleIdsLst = taxReconConfigHeaderDao.getRuleIdLstforCopy();
			exceptionCode.setResponse(ruleIdsLst);
			exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
		} catch(Exception e) {
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			String errorMsg = taxReconConfigHeaderDao.parseErrorMsg(e);
			exceptionCode.setErrorMsg(errorMsg);
		}
		return exceptionCode;
	}

	public ExceptionCode validateQuery(TaxReconHeaderVb vObject) {
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
			exceptionCode = taxReconConfigHeaderDao.checkValidQuery(vObject.getQuery());
			if(exceptionCode.getErrorCode() == Constants.SUCCESSFUL_OPERATION) {
				exceptionCode.setErrorMsg("Query Validation Success");
			}
		} catch(Exception e) {
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			String errorMsg = taxReconConfigHeaderDao.parseErrorMsg(e);
			exceptionCode.setErrorMsg(errorMsg);
		}
		return exceptionCode;
	}
	public ExceptionCode doValidate(TaxReconHeaderVb vObject){
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
	protected ExceptionCode doValidate(List<TaxReconHeaderVb> vObjects) {
		ExceptionCode exceptionCode = new ExceptionCode();
		TaxReconHeaderVb vObject = vObjects.get(0);
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

