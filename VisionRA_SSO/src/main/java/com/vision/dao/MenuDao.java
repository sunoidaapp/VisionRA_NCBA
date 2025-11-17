package com.vision.dao;

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
import com.vision.vb.MenuVb;

@Component
public class MenuDao extends AbstractDao<MenuVb> {

	@Value("${app.productName}")
	private String productName;
	@Value("${app.databaseType}")
	private String databaseType;

	public RowMapper getQueryPopupMapper() {
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				MenuVb menuVb = new MenuVb();
				menuVb.setMenuGroup(rs.getInt("MENU_GROUP"));
				menuVb.setMenuGroupName(rs.getString("Menu_Group_Name"));
				return menuVb;
			}
		};
		return mapper;
	}

	@Override
	protected RowMapper getMapper() {
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				MenuVb menuVb = new MenuVb();
				menuVb.setMenuProgram(rs.getString("MENU_PROGRAM"));
				menuVb.setMenuName(rs.getString("MENU_NAME"));
				menuVb.setMenuSequence(rs.getInt("MENU_SEQUENCE"));
				menuVb.setParentSequence(rs.getInt("PARENT_SEQUENCE"));
				menuVb.setSeparator(rs.getString("SEPARATOR"));
				menuVb.setMenuGroupNt(rs.getInt("MENU_GROUP_NT"));
				menuVb.setMenuGroup(rs.getInt("MENU_GROUP"));
				menuVb.setMenuStatusNt(rs.getInt("MENU_STATUS_NT"));
				menuVb.setMenuStatus(rs.getInt("MENU_STATUS"));
				menuVb.setMenuNodeAvailable(rs.getString("MENU_NODE_VISIBILITY"));
				menuVb.setRecordIndicatorNt(rs.getInt("RECORD_INDICATOR_NT"));
				menuVb.setRecordIndicator(rs.getInt("RECORD_INDICATOR"));
				menuVb.setMaker(rs.getInt("MAKER"));
				menuVb.setMakerName(rs.getString("MAKER_NAME"));
				menuVb.setVerifier(rs.getInt("VERIFIER"));
				menuVb.setVerifierName(rs.getString("VERIFIER_NAME"));
				menuVb.setInternalStatus(rs.getInt("INTERNAL_STATUS"));
				menuVb.setDateLastModified(rs.getString("DATE_LAST_MODIFIED"));
				menuVb.setDateCreation(rs.getString("DATE_CREATION"));
				return menuVb;
			}
		};
		return mapper;
	}

	public List<MenuVb> getQueryPopupResults(MenuVb dObj) {
		Vector<Object> params = new Vector<Object>();
		StringBuffer strBufApprove = new StringBuffer("Select Distinct TAppr.MENU_GROUP,"
				+ " (Select Num_SubTab_description from Num_sub_tab where Num_tab = 6002 and Num_sub_tab = Tappr.Menu_Group ) Menu_Group_Name "
				+ " From VISION_MENU_MDM TAppr ");

		String strWhereNotExists = new String(" Not Exists (Select 'X' From VISION_MENU_MDM_PEND TPend Where "
				+ "TAppr.MENU_PROGRAM = TPend.MENU_PROGRAM " + "And TAppr.MENU_GROUP = TPend.MENU_GROUP)");

		StringBuffer strBufPending = new StringBuffer("Select Distinct TPend.MENU_GROUP,"
				+ " (Select Num_SubTab_description from Num_sub_tab where Num_tab = 6002 and Num_sub_tab = TPend.Menu_Group ) Menu_Group_Name "
				+ " From VISION_MENU_MDM_PEND TPend ");

		try {
			if (ValidationUtil.isValid(dObj.getMenuProgram())) {
				params.addElement("%" + dObj.getMenuProgram().toUpperCase() + "%");
				CommonUtils.addToQuery("UPPER(TAppr.MENU_PROGRAM) like ?", strBufApprove);
				CommonUtils.addToQuery("UPPER(TPend.MENU_PROGRAM) like ?", strBufPending);
			}
			if (ValidationUtil.isValid(dObj.getMenuName())) {
				params.addElement("%" + dObj.getMenuName().toUpperCase() + "%");
				CommonUtils.addToQuery("UPPER(TAppr.MENU_NAME) like ?", strBufApprove);
				CommonUtils.addToQuery("UPPER(TPend.MENU_NAME) like ?", strBufPending);
			}
			if (dObj.getMenuSequence() != 0 && ValidationUtil.isValid(dObj.getMenuSequence())) {
				params.addElement(dObj.getMenuSequence());
				CommonUtils.addToQuery("TAppr.MENU_SEQUENCE = ?", strBufApprove);
				CommonUtils.addToQuery("TPend.MENU_SEQUENCE = ?", strBufPending);
			}
			if (dObj.getParentSequence() != 0 && ValidationUtil.isValid(dObj.getParentSequence())) {
				params.addElement(dObj.getParentSequence());
				CommonUtils.addToQuery("TAppr.PARENT_SEQUENCE = ?", strBufApprove);
				CommonUtils.addToQuery("TPend.PARENT_SEQUENCE = ?", strBufPending);
			}
			if (ValidationUtil.isValid(dObj.getMenuNodeAvailable())) {
				params.addElement("%" + dObj.getMenuNodeAvailable().toUpperCase() + "%");
				CommonUtils.addToQuery("UPPER(TAppr.MENU_NODE_VISIBILITY) like ?", strBufApprove);
				CommonUtils.addToQuery("UPPER(TPend.MENU_NODE_VISIBILITY) like ?", strBufPending);
			}
			if (ValidationUtil.isValid(dObj.getSeparator()) && !"-1".equalsIgnoreCase(dObj.getSeparator())) {
				params.addElement(dObj.getSeparator().toUpperCase());
				CommonUtils.addToQuery("UPPER(TAppr.SEPARATOR) like ?", strBufApprove);
				CommonUtils.addToQuery("UPPER(TPend.SEPARATOR) like ?", strBufPending);
			}
			if (dObj.getMenuGroup() != -1) {
				params.addElement(dObj.getMenuGroup());
				CommonUtils.addToQuery("TAppr.MENU_GROUP = ?", strBufApprove);
				CommonUtils.addToQuery("TPend.MENU_GROUP = ?", strBufPending);
			}
			if (dObj.getMenuStatus() != -1) {
				params.addElement(dObj.getMenuStatus());
				CommonUtils.addToQuery("TAppr.MENU_STATUS = ?", strBufApprove);
				CommonUtils.addToQuery("TPend.MENU_STATUS = ?", strBufPending);
			}
			if (dObj.getRecordIndicator() != -1) {
				if (dObj.getRecordIndicator() > 3) {
					params.addElement(new Integer(0));
					CommonUtils.addToQuery("TAppr.RECORD_INDICATOR > ?", strBufApprove);
					CommonUtils.addToQuery("TPend.RECORD_INDICATOR > ?", strBufPending);
				} else {
					params.addElement(new Integer(dObj.getRecordIndicator()));
					CommonUtils.addToQuery("TAppr.RECORD_INDICATOR = ?", strBufApprove);
					CommonUtils.addToQuery("TPend.RECORD_INDICATOR = ?", strBufPending);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			//logger.error(((strBufApprove == null) ? "strBufApprove is null" : strBufApprove.toString()));
			//logger.error("UNION");
			//logger.error(((strBufPending == null) ? "strBufPending is Null" : strBufPending.toString()));

			if (params != null)
				for (int i = 0; i < params.size(); i++)
					//logger.error("objParams[" + i + "]" + params.get(i).toString());
			return null;
		}

		String orderBy = " Order By MENU_GROUP";
		return getQueryPopupResults(dObj, strBufPending, strBufApprove, strWhereNotExists, orderBy, params,
				getQueryPopupMapper());
	}

	public List<MenuVb> getQueryResults(MenuVb dObj, int intStatus) {
		Vector<Object> params = new Vector<Object>();
		setServiceDefaults();
		StringBuffer strBufApprove = null;
		StringBuffer strBufPending = null;
		String strWhereNotExists = null;
		if ("ORACLE".equalsIgnoreCase(databaseType)) {
			strBufPending = new StringBuffer("Select TAppr.MENU_PROGRAM,TAppr.MENU_NAME, "
					+ "TAppr.MENU_SEQUENCE,TAppr.PARENT_SEQUENCE,TAppr.SEPARATOR,TAppr.MENU_GROUP_NT,TAppr.MENU_GROUP, "
					+ "TAppr.MENU_STATUS_NT,TAppr.MENU_STATUS,TAppr.MENU_NODE_VISIBILITY,TAppr.RECORD_INDICATOR_NT,TAppr.RECORD_INDICATOR, "
					+ " TAppr.MAKER,"
					+ " (SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = NVL(TAPPR.MAKER,0) ) MAKER_NAME,"
					+ " TAppr.VERIFIER,"
					+ " (SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = NVL(TAPPR.VERIFIER,0) ) VERIFIER_NAME,"
					+ " TAppr.INTERNAL_STATUS,To_Char(TAppr.DATE_LAST_MODIFIED, "
					+ " 'DD-MM-YYYY HH24:MI:SS') DATE_LAST_MODIFIED,To_Char(TAppr.DATE_CREATION, 'DD-MM-YYYY HH24:MI:SS') DATE_CREATION From VISION_MENU_MDM TAppr "
					+ "where TAppr.Menu_Sequence = TAppr.Parent_Sequence and TAppr.Separator <> 'Y' and (TAppr.APPLICATION_ACCESS LIKE '%"
					+ productName + "%') ");

			strWhereNotExists = new String(" Not Exists (Select 'X' From VISION_MENU_MDM_PEND TPend Where "
					+ "TAppr.MENU_PROGRAM = TPend.MENU_PROGRAM " + "And TAppr.MENU_GROUP = TPend.MENU_GROUP)");

			strBufPending = new StringBuffer("Select TPend.MENU_PROGRAM,TPend.MENU_NAME, "
					+ "TPend.MENU_SEQUENCE,TPend.PARENT_SEQUENCE,TPend.SEPARATOR,TPend.MENU_GROUP_NT,TPend.MENU_GROUP, "
					+ "TPend.MENU_STATUS_NT,TPend.MENU_STATUS,TPend.MENU_NODE_VISIBILITY,TPend.RECORD_INDICATOR_NT,TPend.RECORD_INDICATOR, "
					+ "TPend.MAKER,"
					+ " (SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = NVL(TPend.MAKER,0) ) MAKER_NAME,"
					+ " TPend.VERIFIER,"
					+ " (SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = NVL(TPend.VERIFIER,0) ) VERIFIER_NAME,"
					+ " TPend.INTERNAL_STATUS,To_Char(TPend.DATE_LAST_MODIFIED, "
					+ " 'DD-MM-YYYY HH24:MI:SS') DATE_LAST_MODIFIED,To_Char(TPend.DATE_CREATION, 'DD-MM-YYYY HH24:MI:SS') DATE_CREATION From VISION_MENU_MDM_PEND TPend "
					+ "where TPend.Menu_Sequence = TPend.Parent_Sequence and TPend.Separator <> 'Y' and (TPend.APPLICATION_ACCESS LIKE '%"
					+ productName + "%') ");
		} else if ("MSSQL".equalsIgnoreCase(databaseType)) {
			strBufPending = new StringBuffer("Select TAppr.MENU_PROGRAM,TAppr.MENU_NAME, "
					+ "TAppr.MENU_SEQUENCE,TAppr.PARENT_SEQUENCE,TAppr.SEPARATOR,TAppr.MENU_GROUP_NT,TAppr.MENU_GROUP, "
					+ "TAppr.MENU_STATUS_NT,TAppr.MENU_STATUS,TAppr.MENU_NODE_VISIBILITY,TAppr.RECORD_INDICATOR_NT,TAppr.RECORD_INDICATOR, "
					+ " TAppr.MAKER,"
					+ " (SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = NVL(TAPPR.MAKER,0) ) MAKER_NAME,"
					+ " TAppr.VERIFIER,"
					+ " (SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = NVL(TAPPR.VERIFIER,0) ) VERIFIER_NAME,"
					+ " TAppr.INTERNAL_STATUS,Format(TAppr.DATE_LAST_MODIFIED, 'dd-MM-yyyy HH:mm:ss') DATE_LAST_MODIFIED,"
					+ "Format(TAppr.DATE_CREATION, 'dd-MM-yyyy HH:mm:ss') DATE_CREATION From VISION_MENU_MDM TAppr "
					+ "where TAppr.Menu_Sequence = TAppr.Parent_Sequence and TAppr.Separator <> 'Y' and (TAppr.APPLICATION_ACCESS LIKE '%"
					+ productName + "%') ");

			strWhereNotExists = new String(" Not Exists (Select 'X' From VISION_MENU_MDM_PEND TPend Where "
					+ "TAppr.MENU_PROGRAM = TPend.MENU_PROGRAM " + "And TAppr.MENU_GROUP = TPend.MENU_GROUP)");

			strBufPending = new StringBuffer("Select TPend.MENU_PROGRAM,TPend.MENU_NAME, "
					+ "TPend.MENU_SEQUENCE,TPend.PARENT_SEQUENCE,TPend.SEPARATOR,TPend.MENU_GROUP_NT,TPend.MENU_GROUP, "
					+ "TPend.MENU_STATUS_NT,TPend.MENU_STATUS,TPend.MENU_NODE_VISIBILITY,TPend.RECORD_INDICATOR_NT,TPend.RECORD_INDICATOR, "
					+ "TPend.MAKER,"
					+ " (SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = NVL(TPend.MAKER,0) ) MAKER_NAME,"
					+ " TPend.VERIFIER,"
					+ " (SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = NVL(TPend.VERIFIER,0) ) VERIFIER_NAME,"
					+ " TPend.INTERNAL_STATUS,Format(TPend.DATE_LAST_MODIFIED, "
					+ " 'dd-MM-yyyy HH:mm:ss') DATE_LAST_MODIFIED,Format(TPend.DATE_CREATION, 'dd-MM-yyyy HH:mm:ss') DATE_CREATION From VISION_MENU_MDM_PEND TPend "
					+ "where TPend.Menu_Sequence = TPend.Parent_Sequence and TPend.Separator <> 'Y' and (TPend.APPLICATION_ACCESS LIKE '%"
					+ productName + "%') ");
		}
		try {
			if (ValidationUtil.isValid(dObj.getMenuProgram())) {
				params.addElement("%" + dObj.getMenuProgram().toUpperCase() + "%");
				CommonUtils.addToQuery("UPPER(TAppr.MENU_PROGRAM) like ?", strBufApprove);
				CommonUtils.addToQuery("UPPER(TPend.MENU_PROGRAM) like ?", strBufPending);
			}
			if (ValidationUtil.isValid(dObj.getMenuName())) {
				params.addElement("%" + dObj.getMenuName().toUpperCase() + "%");
				CommonUtils.addToQuery("UPPER(TAppr.MENU_NAME) like ?", strBufApprove);
				CommonUtils.addToQuery("UPPER(TPend.MENU_NAME) like ?", strBufPending);
			}
			if (dObj.getMenuSequence() != 0 && ValidationUtil.isValid(dObj.getMenuSequence())) {
				params.addElement(dObj.getMenuSequence());
				CommonUtils.addToQuery("TAppr.MENU_SEQUENCE = ?", strBufApprove);
				CommonUtils.addToQuery("TPend.MENU_SEQUENCE = ?", strBufPending);
			}
			if (dObj.getParentSequence() != 0 && ValidationUtil.isValid(dObj.getParentSequence())) {
				params.addElement(dObj.getParentSequence());
				CommonUtils.addToQuery("TAppr.PARENT_SEQUENCE = ?", strBufApprove);
				CommonUtils.addToQuery("TPend.PARENT_SEQUENCE = ?", strBufPending);
			}
			if (ValidationUtil.isValid(dObj.getMenuNodeAvailable())) {
				params.addElement("%" + dObj.getMenuNodeAvailable().toUpperCase() + "%");
				CommonUtils.addToQuery("UPPER(TAppr.MENU_NODE_VISIBILITY) like ?", strBufApprove);
				CommonUtils.addToQuery("UPPER(TPend.MENU_NODE_VISIBILITY) like ?", strBufPending);
			}
			if (ValidationUtil.isValid(dObj.getSeparator()) && !"-1".equalsIgnoreCase(dObj.getSeparator())) {
				params.addElement(dObj.getSeparator().toUpperCase());
				CommonUtils.addToQuery("UPPER(TAppr.SEPARATOR) like ?", strBufApprove);
				CommonUtils.addToQuery("UPPER(TPend.SEPARATOR) like ?", strBufPending);
			}
			if (dObj.getMenuGroup() != -1) {
				params.addElement(dObj.getMenuGroup());
				CommonUtils.addToQuery("TAppr.MENU_GROUP = ?", strBufApprove);
				CommonUtils.addToQuery("TPend.MENU_GROUP = ?", strBufPending);
			}
			if (dObj.getMenuStatus() != -1) {
				params.addElement(dObj.getMenuStatus());
				CommonUtils.addToQuery("TAppr.MENU_STATUS like ?", strBufApprove);
				CommonUtils.addToQuery("TPend.MENU_STATUS like ?", strBufPending);
			}
			// check if the column [RECORD_INDICATOR] should be included in the query
			if (dObj.getRecordIndicator() != -1) {
				if (dObj.getRecordIndicator() > 3) {
					params.addElement(new Integer(0));
					CommonUtils.addToQuery("TAppr.RECORD_INDICATOR > ?", strBufApprove);
					CommonUtils.addToQuery("TPend.RECORD_INDICATOR > ?", strBufPending);
				} else {
					params.addElement(new Integer(dObj.getRecordIndicator()));
					CommonUtils.addToQuery("TAppr.RECORD_INDICATOR = ?", strBufApprove);
					CommonUtils.addToQuery("TPend.RECORD_INDICATOR = ?", strBufPending);
				}
			}

			String orderBy = " Order By MENU_GROUP, MENU_SEQUENCE, MENU_PROGRAM";
			return getQueryPopupResults(dObj, strBufPending, strBufApprove, strWhereNotExists, orderBy, params);
		} catch (Exception ex) {
			ex.printStackTrace();
			//logger.error(((strBufApprove == null) ? "strBufApprove is null" : strBufApprove.toString()));
			//logger.error("UNION");
			//logger.error(((strBufPending == null) ? "strBufPending is Null" : strBufPending.toString()));

			/*if (params != null)
				for (int i = 0; i < params.size(); i++)*/
					//logger.error("objParams[" + i + "]" + params.get(i).toString());
			return null;
		}
	}

	public List<MenuVb> getQueryResultsSubMenu(MenuVb dObj, int intStatus){
		Vector<Object> params = new Vector<Object>();
		setServiceDefaults();
		StringBuffer strBufApprove = null;
		StringBuffer strBufPending = null;
		String strWhereNotExists = null;
		if("ORACLE".equalsIgnoreCase(databaseType)) {
		strBufApprove = new StringBuffer("Select TAppr.MENU_PROGRAM,TAppr.MENU_NAME, " + 
			"TAppr.MENU_SEQUENCE,TAppr.PARENT_SEQUENCE,TAppr.SEPARATOR,TAppr.MENU_GROUP_NT,TAppr.MENU_GROUP, " + 
			"TAppr.MENU_STATUS_NT,TAppr.MENU_STATUS,TAppr.MENU_NODE_VISIBILITY,TAppr.RECORD_INDICATOR_NT,TAppr.RECORD_INDICATOR, " + 
			"TAppr.MAKER,"+
			" (SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = NVL(TAPPR.MAKER,0) ) MAKER_NAME,"+
			" TAppr.VERIFIER,"+
			" (SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = NVL(TAPPR.VERIFIER,0) ) VERIFIER_NAME,"+
			" TAppr.INTERNAL_STATUS,To_Char(TAppr.DATE_LAST_MODIFIED, " + 
			" 'DD-MM-YYYY HH24:MI:SS') DATE_LAST_MODIFIED,To_Char(TAppr.DATE_CREATION, 'DD-MM-YYYY HH24:MI:SS') DATE_CREATION, "
			+ " TAppr.APPLICATION_ACCESS_AT,TAppr.APPLICATION_ACCESS From VISION_MENU_MDM TAppr "
			+ "where TAppr.Menu_Sequence <> TAppr.Parent_Sequence and TAppr.Separator <> 'Y' and (TAppr.APPLICATION_ACCESS LIKE '%"+productName+"%' ) " );

		strWhereNotExists = new String(" Not Exists (Select 'X' From VISION_MENU_MDM_PEND TPend Where " + 
			"TAppr.MENU_PROGRAM = TPend.MENU_PROGRAM " + 
			"And TAppr.MENU_GROUP = TPend.MENU_GROUP)");

		strBufPending = new StringBuffer("Select TPend.MENU_PROGRAM,TPend.MENU_NAME, " + 
			"TPend.MENU_SEQUENCE,TPend.PARENT_SEQUENCE,TPend.SEPARATOR,TPend.MENU_GROUP_NT,TPend.MENU_GROUP, " + 
			"TPend.MENU_STATUS_NT,TPend.MENU_STATUS,TPend.MENU_NODE_VISIBILITY,TPend.RECORD_INDICATOR_NT,TPend.RECORD_INDICATOR, " + 
			"TPend.MAKER,"+
			" (SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = NVL(TPend.MAKER,0) ) MAKER_NAME,"+
			" TPend.VERIFIER,"+
			" (SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = NVL(TPend.VERIFIER,0) ) VERIFIER_NAME,"+
			" TPend.INTERNAL_STATUS,To_Char(TPend.DATE_LAST_MODIFIED, " + 
			" 'DD-MM-YYYY HH24:MI:SS') DATE_LAST_MODIFIED,To_Char(TPend.DATE_CREATION, 'DD-MM-YYYY HH24:MI:SS') DATE_CREATION, "
			+ " TPend.APPLICATION_ACCESS_AT,TPend.APPLICATION_ACCESS From VISION_MENU_MDM_PEND TPend "
			+ "where TPend.Menu_Sequence <> TPend.Parent_Sequence and TPend.Separator <> 'Y' and (TPend.APPLICATION_ACCESS LIKE '%"+productName+"%' ) ");
		}else if("MSSQL".equalsIgnoreCase(databaseType)) {
			strBufApprove = new StringBuffer("Select TAppr.MENU_PROGRAM,TAppr.MENU_NAME, " + 
					"TAppr.MENU_SEQUENCE,TAppr.PARENT_SEQUENCE,TAppr.SEPARATOR,TAppr.MENU_GROUP_NT,TAppr.MENU_GROUP, " + 
					"TAppr.MENU_STATUS_NT,TAppr.MENU_STATUS,TAppr.MENU_NODE_VISIBILITY,TAppr.RECORD_INDICATOR_NT,TAppr.RECORD_INDICATOR, " + 
					"TAppr.MAKER,"+
					" (SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = NVL(TAPPR.MAKER,0) ) MAKER_NAME,"+
					" TAppr.VERIFIER,"+
					" (SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = NVL(TAPPR.VERIFIER,0) ) VERIFIER_NAME,"+
					" TAppr.INTERNAL_STATUS,Format(TAppr.DATE_LAST_MODIFIED, " + 
					" 'dd-MM-yyyy HH:mm:ss') DATE_LAST_MODIFIED,Format(TAppr.DATE_CREATION, 'dd-MM-yyyy HH:mm:ss') DATE_CREATION, "
					+ " TAppr.APPLICATION_ACCESS_AT,TAppr.APPLICATION_ACCESS From VISION_MENU_MDM TAppr "
					+ "where TAppr.Menu_Sequence <> TAppr.Parent_Sequence and TAppr.Separator <> 'Y' and (TAppr.APPLICATION_ACCESS LIKE '%"+productName+"%' ) " );

				strWhereNotExists = new String(" Not Exists (Select 'X' From VISION_MENU_MDM_PEND TPend Where " + 
					"TAppr.MENU_PROGRAM = TPend.MENU_PROGRAM " + 
					"And TAppr.MENU_GROUP = TPend.MENU_GROUP)");

				strBufPending = new StringBuffer("Select TPend.MENU_PROGRAM,TPend.MENU_NAME, " + 
					"TPend.MENU_SEQUENCE,TPend.PARENT_SEQUENCE,TPend.SEPARATOR,TPend.MENU_GROUP_NT,TPend.MENU_GROUP, " + 
					"TPend.MENU_STATUS_NT,TPend.MENU_STATUS,TPend.MENU_NODE_VISIBILITY,TPend.RECORD_INDICATOR_NT,TPend.RECORD_INDICATOR, " + 
					"TPend.MAKER,"+
					" (SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = NVL(TPend.MAKER,0) ) MAKER_NAME,"+
					" TPend.VERIFIER,"+
					" (SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = NVL(TPend.VERIFIER,0) ) VERIFIER_NAME,"+
					" TPend.INTERNAL_STATUS,Format(TPend.DATE_LAST_MODIFIED, " + 
					" 'dd-MM-yyyy HH:mm:ss') DATE_LAST_MODIFIED,Format(TPend.DATE_CREATION, 'dd-MM-yyyy HH:mm:ss') DATE_CREATION, "
					+ " TPend.APPLICATION_ACCESS_AT,TPend.APPLICATION_ACCESS From VISION_MENU_MDM_PEND TPend "
					+ "where TPend.Menu_Sequence <> TPend.Parent_Sequence and TPend.Separator <> 'Y' and (TPend.APPLICATION_ACCESS LIKE '%"+productName+"%' ) ");
		}
		try
		{
			if (dObj.getParentSequence() != 0 && ValidationUtil.isValid(dObj.getParentSequence()))
			{
				params.addElement(dObj.getParentSequence());
				CommonUtils.addToQuery("TAppr.PARENT_SEQUENCE = ?", strBufApprove);
				CommonUtils.addToQuery("TPend.PARENT_SEQUENCE = ?", strBufPending);
			}
			if (dObj.getMenuGroup()!=-1)
			{
				params.addElement(dObj.getMenuGroup());
				CommonUtils.addToQuery("TAppr.MENU_GROUP = ?", strBufApprove);
				CommonUtils.addToQuery("TPend.MENU_GROUP = ?", strBufPending);
			}
			String orderBy = " Order By MENU_GROUP, MENU_SEQUENCE, MENU_PROGRAM";
			return getQueryPopupResults(dObj,strBufPending, strBufApprove, strWhereNotExists, orderBy, params);
		}
		catch(Exception ex){
			ex.printStackTrace();
			//logger.error(((strBufApprove==null)? "strBufApprove is null":strBufApprove.toString()));
			//logger.error("UNION");
			//logger.error(((strBufPending==null)? "strBufPending is Null":strBufPending.toString()));

			/*if (params != null)
				for(int i=0 ; i< params.size(); i++)
					//logger.error("objParams[" + i + "]" + params.get(i).toString());
*/			return null;
		}
	}

	public List<MenuVb> getQueryResultsForReview(MenuVb dObj, int intStatus) {

		List<MenuVb> collTemp = null;
		final int intKeyFieldsCount = 2;
		StringBuffer strQueryAppr = null;
		StringBuffer strQueryPend = null;
		if ("ORACLE".equalsIgnoreCase(databaseType)) {
			strQueryAppr = new StringBuffer("Select TAppr.MENU_PROGRAM,TAppr.MENU_NAME, "
					+ "TAppr.MENU_SEQUENCE,TAppr.PARENT_SEQUENCE,TAppr.SEPARATOR,TAppr.MENU_GROUP_NT,TAppr.MENU_GROUP, "
					+ "TAppr.MENU_STATUS_NT,TAppr.MENU_STATUS,TAppr.MENU_NODE_VISIBILITY ,TAppr.RECORD_INDICATOR_NT,TAppr.RECORD_INDICATOR, "
					+ "TAppr.MAKER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = NVL(TAppr.MAKER,0) ) MAKER_NAME,TAppr.VERIFIER,"
					+ " (SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = NVL(TAppr.VERIFIER,0) ) VERIFIER_NAME,"
					+ " TAppr.INTERNAL_STATUS,To_Char(TAppr.DATE_LAST_MODIFIED, 'DD-MM-YYYY HH24:MI:SS') DATE_LAST_MODIFIED, "
					+ "To_Char(TAppr.DATE_CREATION, 'DD-MM-YYYY HH24:MI:SS') DATE_CREATION,TAppr.APPLICATION_ACCESS_AT,TAppr.APPLICATION_ACCESS "
					+ " From VISION_MENU_MDM TAppr "
					+ "Where TAppr.MENU_PROGRAM = ?  And TAppr.MENU_GROUP = ?  and TAppr.APPLICATION_ACCESS LIKE '%"
					+ productName + "%' ");

			strQueryPend = new StringBuffer("Select TPend.MENU_PROGRAM,TPend.MENU_NAME, "
					+ "TPend.MENU_SEQUENCE,TPend.PARENT_SEQUENCE,TPend.SEPARATOR,TPend.MENU_GROUP_NT,TPend.MENU_GROUP, "
					+ "TPend.MENU_STATUS_NT,TPend.MENU_STATUS,TPend.MENU_NODE_VISIBILITY ,TPend.RECORD_INDICATOR_NT,TPend.RECORD_INDICATOR, "
					+ "TPend.MAKER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = NVL(TPend.MAKER,0) ) MAKER_NAME,TPend.VERIFIER,"
					+ " (SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = NVL(TPend.VERIFIER,0) ) VERIFIER_NAME,"
					+ " TPend.INTERNAL_STATUS,To_Char(TPend.DATE_LAST_MODIFIED, 'DD-MM-YYYY HH24:MI:SS') DATE_LAST_MODIFIED,"
					+ "To_Char(TPend.DATE_CREATION, 'DD-MM-YYYY HH24:MI:SS') DATE_CREATION,TPend.APPLICATION_ACCESS_AT,TPend.APPLICATION_ACCESS "
					+ " From VISION_MENU_MDM_PEND TPend "
					+ " Where TPend.MENU_PROGRAM = ?  And TPend.MENU_GROUP = ? and TPend.APPLICATION_ACCESS LIKE '%"
					+ productName + "%' ");
		} else if ("MSSQL".equalsIgnoreCase(databaseType)) {
			strQueryAppr = new StringBuffer("Select TAppr.MENU_PROGRAM,TAppr.MENU_NAME, "
					+ "TAppr.MENU_SEQUENCE,TAppr.PARENT_SEQUENCE,TAppr.SEPARATOR,TAppr.MENU_GROUP_NT,TAppr.MENU_GROUP, "
					+ "TAppr.MENU_STATUS_NT,TAppr.MENU_STATUS,TAppr.MENU_NODE_VISIBILITY ,TAppr.RECORD_INDICATOR_NT,TAppr.RECORD_INDICATOR, "
					+ "TAppr.MAKER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = NVL(TAppr.MAKER,0) ) MAKER_NAME,TAppr.VERIFIER,"
					+ " (SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = NVL(TAppr.VERIFIER,0) ) VERIFIER_NAME,"
					+ " TAppr.INTERNAL_STATUS,Format(TAppr.DATE_LAST_MODIFIED, 'dd-MM-yyyy HH:mm:ss') DATE_LAST_MODIFIED, "
					+ "Format(TAppr.DATE_CREATION, 'dd-MM-yyyy HH:mm:ss') DATE_CREATION,TAppr.APPLICATION_ACCESS_AT,TAppr.APPLICATION_ACCESS "
					+ " From VISION_MENU_MDM TAppr "
					+ "Where TAppr.MENU_PROGRAM = ?  And TAppr.MENU_GROUP = ?  and TAppr.APPLICATION_ACCESS LIKE '%"
					+ productName + "%' ");

			strQueryPend = new StringBuffer("Select TPend.MENU_PROGRAM,TPend.MENU_NAME, "
					+ "TPend.MENU_SEQUENCE,TPend.PARENT_SEQUENCE,TPend.SEPARATOR,TPend.MENU_GROUP_NT,TPend.MENU_GROUP, "
					+ "TPend.MENU_STATUS_NT,TPend.MENU_STATUS,TPend.MENU_NODE_VISIBILITY ,TPend.RECORD_INDICATOR_NT,TPend.RECORD_INDICATOR, "
					+ "TPend.MAKER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = NVL(TPend.MAKER,0) ) MAKER_NAME,TPend.VERIFIER,"
					+ " (SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = NVL(TPend.VERIFIER,0) ) VERIFIER_NAME,"
					+ " TPend.INTERNAL_STATUS,Format(TPend.DATE_LAST_MODIFIED, 'dd-MM-yyyy HH:mm:ss') DATE_LAST_MODIFIED,"
					+ "Format(TPend.DATE_CREATION, 'dd-MM-yyyy HH:mm:ss') DATE_CREATION,TPend.APPLICATION_ACCESS_AT,TPend.APPLICATION_ACCESS "
					+ " From VISION_MENU_MDM_PEND TPend "
					+ " Where TPend.MENU_PROGRAM = ?  And TPend.MENU_GROUP = ? and TPend.APPLICATION_ACCESS LIKE '%"
					+ productName + "%' ");
		}

		Object objParams[] = new Object[intKeyFieldsCount];
		objParams[0] = new String(dObj.getMenuProgram());
		objParams[1] = new Integer(dObj.getMenuGroup());
		try {
			if (!dObj.isVerificationRequired()) {
				intStatus = 0;
			}
			if (intStatus == 0) {
				logger.info("Executing approved query");
				collTemp = getJdbcTemplate().query(strQueryAppr.toString(), objParams, getMapper());
			} else {
				logger.info("Executing pending query");
				collTemp = getJdbcTemplate().query(strQueryPend.toString(), objParams, getMapper());
			}
			return collTemp;
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("Error: getQueryResultsForReview Exception :   ");
			/*if (intStatus == 0)
				//logger.error(((strQueryAppr == null) ? "strQueryAppr is null" : strQueryAppr.toString()));
			else
				//logger.error(((strQueryPend == null) ? "strQueryPend is null" : strQueryPend.toString()));
*/
			return null;
		}
	}

	@Override
	protected List<MenuVb> selectApprovedRecord(MenuVb vObject) {
		return getQueryResultsForReview(vObject, Constants.STATUS_ZERO);
	}

	@Override
	protected List<MenuVb> doSelectPendingRecord(MenuVb vObject) {
		return getQueryResultsForReview(vObject, Constants.STATUS_PENDING);
	}

	@Override
	protected void setServiceDefaults() {
		serviceName = "Menu";
		serviceDesc = "Menu";
		tableName = "VISION_MENU_MDM";
		childTableName = "VISION_MENU_MDM";
		intCurrentUserId = CustomContextHolder.getContext().getVisionId();
	}

	@Override
	protected int getStatus(MenuVb records) {
		return records.getMenuStatus();
	}

	@Override
	protected void setStatus(MenuVb vObject, int status) {
		vObject.setMenuStatus(status);
	}

	@Override
	protected int doInsertionAppr(MenuVb vObject) {
		String query = "";
		if ("ORACLE".equalsIgnoreCase(databaseType)) {
			query = "Insert Into VISION_MENU_MDM( "
					+ "MENU_PROGRAM, MENU_NAME, MENU_SEQUENCE, PARENT_SEQUENCE, SEPARATOR, MENU_GROUP_NT, "
					+ " MENU_GROUP, MENU_STATUS_NT, MENU_STATUS, MENU_NODE_VISIBILITY, RECORD_INDICATOR_NT, RECORD_INDICATOR, "
					+ " MAKER, VERIFIER, INTERNAL_STATUS, DATE_LAST_MODIFIED, DATE_CREATION,APPLICATION_ACCESS_AT,APPLICATION_ACCESS) "
					+ " Values (?, ?, ?, ?, ?, ?, ?, ?, ?,?, ?, ?, ?, ?, ?, SysDate, SysDate, ?, ?)";
		} else if ("MSSQL".equalsIgnoreCase(databaseType)) {
			query = "Insert Into VISION_MENU_MDM( "
					+ "MENU_PROGRAM, MENU_NAME, MENU_SEQUENCE, PARENT_SEQUENCE, SEPARATOR, MENU_GROUP_NT, "
					+ " MENU_GROUP, MENU_STATUS_NT, MENU_STATUS, MENU_NODE_VISIBILITY, RECORD_INDICATOR_NT, RECORD_INDICATOR, "
					+ " MAKER, VERIFIER, INTERNAL_STATUS, DATE_LAST_MODIFIED, DATE_CREATION,APPLICATION_ACCESS_AT,APPLICATION_ACCESS) "
					+ " Values (?, ?, ?, ?, ?, ?, ?, ?, ?,?, ?, ?, ?, ?, ?, GetDate(), GetDate(), ?, ?)";
		}
		Object args[] = { vObject.getMenuProgram(), vObject.getMenuName(), vObject.getMenuSequence(),
				vObject.getParentSequence(), vObject.getSeparator(), vObject.getMenuGroupNt(), vObject.getMenuGroup(),
				vObject.getMenuStatusNt(), vObject.getMenuStatus(), vObject.getMenuNodeAvailable(),
				vObject.getRecordIndicatorNt(), vObject.getRecordIndicator(), vObject.getMaker(), vObject.getVerifier(),
				vObject.getInternalStatus(), vObject.getApplicationAccessAt(), productName };

		return getJdbcTemplate().update(query, args);
	}

	@Override
	protected int doInsertionPend(MenuVb vObject) {
		String query = "";
		if ("ORACLE".equalsIgnoreCase(databaseType)) {
			query = "Insert Into VISION_MENU_MDM_PEND( "
					+ "MENU_PROGRAM, MENU_NAME, MENU_SEQUENCE, PARENT_SEQUENCE, SEPARATOR, MENU_GROUP_NT, "
					+ " MENU_GROUP, MENU_STATUS_NT, MENU_STATUS, MENU_NODE_VISIBILITY, RECORD_INDICATOR_NT, RECORD_INDICATOR, "
					+ " MAKER, VERIFIER, INTERNAL_STATUS, DATE_LAST_MODIFIED, DATE_CREATION,APPLICATION_ACCESS_AT,APPLICATION_ACCESS) "
					+ " Values (?, ?, ?, ?, ?, ?, ?, ?, ?,?, ?, ?, ?, ?, ?, SysDate, SysDate, ?, ?)";
		} else if ("MSSQL".equalsIgnoreCase(databaseType)) {
			query = "Insert Into VISION_MENU_MDM_PEND( "
					+ "MENU_PROGRAM, MENU_NAME, MENU_SEQUENCE, PARENT_SEQUENCE, SEPARATOR, MENU_GROUP_NT, "
					+ " MENU_GROUP, MENU_STATUS_NT, MENU_STATUS, MENU_NODE_VISIBILITY, RECORD_INDICATOR_NT, RECORD_INDICATOR, "
					+ " MAKER, VERIFIER, INTERNAL_STATUS, DATE_LAST_MODIFIED, DATE_CREATION,APPLICATION_ACCESS_AT,APPLICATION_ACCESS) "
					+ " Values (?, ?, ?, ?, ?, ?, ?, ?, ?,?, ?, ?, ?, ?, ?, GetDate(), GetDate(), ?, ?)";
		}
		Object args[] = { vObject.getMenuProgram(), vObject.getMenuName(), vObject.getMenuSequence(),
				vObject.getParentSequence(), vObject.getSeparator(), vObject.getMenuGroupNt(), vObject.getMenuGroup(),
				vObject.getMenuStatusNt(), vObject.getMenuStatus(), vObject.getMenuNodeAvailable(),
				vObject.getRecordIndicatorNt(), vObject.getRecordIndicator(), vObject.getMaker(), vObject.getVerifier(),
				vObject.getInternalStatus(), vObject.getApplicationAccessAt(), productName };

		return getJdbcTemplate().update(query, args);
	}

	@Override
	protected int doInsertionPendWithDc(MenuVb vObject) {
		String query = "";
		if ("ORACLE".equalsIgnoreCase(databaseType)) {
			query = "Insert Into VISION_MENU_MDM_PEND( "
					+ "MENU_PROGRAM, MENU_NAME, MENU_SEQUENCE, PARENT_SEQUENCE, SEPARATOR, MENU_GROUP_NT, "
					+ " MENU_GROUP, MENU_STATUS_NT, MENU_STATUS, MENU_NODE_VISIBILITY, RECORD_INDICATOR_NT, RECORD_INDICATOR, "
					+ " MAKER, VERIFIER, INTERNAL_STATUS, DATE_LAST_MODIFIED, DATE_CREATION,APPLICATION_ACCESS_AT,APPLICATION_ACCESS) "
					+ "Values (?, ?, ?, ?, ?, ?, ?, ?,? , ?, ?, ?, ?, ?, ?, SysDate,  "
					+ "To_Date(?, 'DD-MM-YYYY HH24:MI:SS'), ?, ?)";
		} else if ("MSSQL".equalsIgnoreCase(databaseType)) {
			query = "Insert Into VISION_MENU_MDM_PEND( "
					+ "MENU_PROGRAM, MENU_NAME, MENU_SEQUENCE, PARENT_SEQUENCE, SEPARATOR, MENU_GROUP_NT, "
					+ " MENU_GROUP, MENU_STATUS_NT, MENU_STATUS, MENU_NODE_VISIBILITY, RECORD_INDICATOR_NT, RECORD_INDICATOR, "
					+ " MAKER, VERIFIER, INTERNAL_STATUS, DATE_LAST_MODIFIED, DATE_CREATION,APPLICATION_ACCESS_AT,APPLICATION_ACCESS) "
					+ "Values (?, ?, ?, ?, ?, ?, ?, ?,? , ?, ?, ?, ?, ?, ?, GetDate(),  "
					+ "CONVERT(datetime, ?, 103), ?, ?)";
		}
		Object args[] = { vObject.getMenuProgram(), vObject.getMenuName(), vObject.getMenuSequence(),
				vObject.getParentSequence(), vObject.getSeparator(), vObject.getMenuGroupNt(), vObject.getMenuGroup(),
				vObject.getMenuStatusNt(), vObject.getMenuStatus(), vObject.getMenuNodeAvailable(),
				vObject.getRecordIndicatorNt(), vObject.getRecordIndicator(), vObject.getMaker(), vObject.getVerifier(),
				vObject.getInternalStatus(), vObject.getDateCreation(), vObject.getApplicationAccessAt(), productName };

		return getJdbcTemplate().update(query, args);
	}

	@Override
	protected int doUpdateAppr(MenuVb vObject) {
		String query = "";
		if ("ORACLE".equalsIgnoreCase(databaseType)) {
			query = "Update VISION_MENU_MDM Set "
					+ "MENU_NAME = ?, MENU_SEQUENCE = ?, PARENT_SEQUENCE = ?, SEPARATOR = ?, MENU_GROUP_NT = ?, "
					+ " MENU_STATUS_NT = ?, MENU_STATUS = ?, MENU_NODE_VISIBILITY = ?, RECORD_INDICATOR_NT = ?, RECORD_INDICATOR = ?, "
					+ " MAKER = ?, VERIFIER = ?, INTERNAL_STATUS = ?, DATE_LAST_MODIFIED = SysDate "
					+ "Where MENU_PROGRAM = ? " + " And MENU_GROUP = ? ";
		} else if ("MSSQL".equalsIgnoreCase(databaseType)) {
			query = "Update VISION_MENU_MDM Set "
					+ "MENU_NAME = ?, MENU_SEQUENCE = ?, PARENT_SEQUENCE = ?, SEPARATOR = ?, MENU_GROUP_NT = ?, "
					+ " MENU_STATUS_NT = ?, MENU_STATUS = ?, MENU_NODE_VISIBILITY = ?, RECORD_INDICATOR_NT = ?, RECORD_INDICATOR = ?, "
					+ " MAKER = ?, VERIFIER = ?, INTERNAL_STATUS = ?, DATE_LAST_MODIFIED = GetDate() "
					+ "Where MENU_PROGRAM = ? " + " And MENU_GROUP = ? ";
		}
		Object args[] = { vObject.getMenuName(), vObject.getMenuSequence(), vObject.getParentSequence(),
				vObject.getSeparator(), vObject.getMenuGroupNt(), vObject.getMenuStatusNt(), vObject.getMenuStatus(),
				vObject.getMenuNodeAvailable(), vObject.getRecordIndicatorNt(), vObject.getRecordIndicator(),
				vObject.getMaker(), vObject.getVerifier(), vObject.getInternalStatus(), vObject.getMenuProgram(),
				vObject.getMenuGroup() };

		return getJdbcTemplate().update(query, args);
	}

	@Override
	protected int doUpdatePend(MenuVb vObject) {
		String query = "";
		if ("ORACLE".equalsIgnoreCase(databaseType)) {
			query = "Update VISION_MENU_MDM_PEND Set "
					+ "MENU_NAME = ?, MENU_SEQUENCE = ?, PARENT_SEQUENCE = ?, SEPARATOR = ?, MENU_GROUP_NT = ?,"
					+ "MENU_STATUS_NT = ?, MENU_STATUS = ?, MENU_NODE_VISIBILITY = ?, RECORD_INDICATOR_NT = ?, RECORD_INDICATOR = ?,"
					+ "MAKER = ?, VERIFIER = ?, INTERNAL_STATUS = ?, DATE_LAST_MODIFIED = SysDate "
					+ "Where MENU_PROGRAM = ? " + " And MENU_GROUP = ? ";
		} else if ("MSSQL".equalsIgnoreCase(databaseType)) {
			query = "Update VISION_MENU_MDM_PEND Set "
					+ "MENU_NAME = ?, MENU_SEQUENCE = ?, PARENT_SEQUENCE = ?, SEPARATOR = ?, MENU_GROUP_NT = ?,"
					+ "MENU_STATUS_NT = ?, MENU_STATUS = ?, MENU_NODE_VISIBILITY = ?, RECORD_INDICATOR_NT = ?, RECORD_INDICATOR = ?,"
					+ "MAKER = ?, VERIFIER = ?, INTERNAL_STATUS = ?, DATE_LAST_MODIFIED = GetDate() "
					+ "Where MENU_PROGRAM = ? " + " And MENU_GROUP = ? ";
		}
		Object args[] = { vObject.getMenuName(), vObject.getMenuSequence(), vObject.getParentSequence(),
				vObject.getSeparator(), vObject.getMenuGroupNt(), vObject.getMenuStatusNt(), vObject.getMenuStatus(),
				vObject.getMenuNodeAvailable(), vObject.getRecordIndicatorNt(), vObject.getRecordIndicator(),
				vObject.getMaker(), vObject.getVerifier(), vObject.getInternalStatus(), vObject.getMenuProgram(),
				vObject.getMenuGroup() };

		return getJdbcTemplate().update(query, args);
	}

	@Override
	protected int doDeleteAppr(MenuVb vObject) {
		String query = "Delete From VISION_MENU_MDM Where " + "MENU_PROGRAM = ? And " + "MENU_GROUP = ? ";

		Object args[] = { vObject.getMenuProgram(), vObject.getMenuGroup() };

		return getJdbcTemplate().update(query, args);
	}

	@Override
	protected int deletePendingRecord(MenuVb vObject) {
		String query = "Delete From VISION_MENU_MDM_PEND Where " + "MENU_PROGRAM = ? And " + "MENU_GROUP = ? ";

		Object args[] = { vObject.getMenuProgram(), vObject.getMenuGroup() };

		return getJdbcTemplate().update(query, args);
	}

	@Override
	protected String frameErrorMessage(MenuVb vObject, String strOperation) {
		// specify all the key fields and their values first
		String strErrMsg = new String("");
		try {
			strErrMsg = strErrMsg + " MENU_PROGRAM:" + vObject.getMenuProgram();
			strErrMsg = strErrMsg + " MENU_GROUP:" + vObject.getMenuGroup();
			// Now concatenate the error message that has been sent
			if ("Approve".equalsIgnoreCase(strOperation))
				strErrMsg = strErrMsg + " failed during approve Operation. Bulk Approval aborted !!";
			else
				strErrMsg = strErrMsg + " failed during reject Operation. Bulk Rejection aborted !!";
		} catch (Exception ex) {
			strErrorDesc = ex.getMessage();
			strErrMsg = strErrMsg + strErrorDesc;
			logger.error(strErrMsg, ex);
		}
		// Return back the error message string
		return strErrMsg;
	}

	@Override
	protected String getAuditString(MenuVb vObject) {
		final String auditDelimiter = vObject.getAuditDelimiter();
		final String auditDelimiterColVal = vObject.getAuditDelimiterColVal();
		StringBuffer strAudit = new StringBuffer("");
		try {
			if (ValidationUtil.isValid(vObject.getMenuProgram()))
				strAudit.append("MENU_PROGRAM" + auditDelimiterColVal + vObject.getMenuProgram().trim());
			else
				strAudit.append("MENU_PROGRAM" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);

			if (ValidationUtil.isValid(vObject.getMenuName()))
				strAudit.append("MENU_NAME" + auditDelimiterColVal + vObject.getMenuName().trim());
			else
				strAudit.append("MENU_NAME" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);

			strAudit.append("MENU_SEQUENCE" + auditDelimiterColVal + vObject.getMenuSequence());
			strAudit.append(auditDelimiter);
			strAudit.append("PARENT_SEQUENCE" + auditDelimiterColVal + vObject.getParentSequence());
			strAudit.append(auditDelimiter);

			if (ValidationUtil.isValid(vObject.getMenuNodeAvailable()))
				strAudit.append("MENU_NODE_VISIBILITY" + auditDelimiterColVal + vObject.getMenuNodeAvailable().trim());
			else
				strAudit.append("MENU_NODE_VISIBILITY" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);

			if (ValidationUtil.isValid(vObject.getSeparator()))
				strAudit.append("SEPARATOR" + auditDelimiterColVal + vObject.getSeparator().trim());
			else
				strAudit.append("SEPARATOR" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);

			strAudit.append("MENU_GROUP_NT" + auditDelimiterColVal + vObject.getMenuGroupNt());
			strAudit.append(auditDelimiter);
			strAudit.append("MENU_GROUP" + auditDelimiterColVal + vObject.getMenuGroup());
			strAudit.append(auditDelimiter);
			strAudit.append("MENU_STATUS_NT" + auditDelimiterColVal + vObject.getMenuStatusNt());
			strAudit.append(auditDelimiter);
			strAudit.append("MENU_STATUS" + auditDelimiterColVal + vObject.getMenuStatus());
			strAudit.append(auditDelimiter);
			strAudit.append("RECORD_INDICATOR_NT" + auditDelimiterColVal + vObject.getRecordIndicatorNt());
			strAudit.append(auditDelimiter);
			strAudit.append("RECORD_INDICATOR" + auditDelimiterColVal + vObject.getRecordIndicator());
			strAudit.append(auditDelimiter);
			strAudit.append("MAKER" + auditDelimiterColVal + vObject.getMaker());
			strAudit.append(auditDelimiter);
			strAudit.append("VERIFIER" + auditDelimiterColVal + vObject.getVerifier());
			strAudit.append(auditDelimiter);
			strAudit.append("INTERNAL_STATUS" + auditDelimiterColVal + vObject.getInternalStatus());
			strAudit.append(auditDelimiter);
			if (ValidationUtil.isValid(vObject.getDateLastModified()))
				strAudit.append("DATE_LAST_MODIFIED" + auditDelimiterColVal + vObject.getDateLastModified().trim());
			else
				strAudit.append("DATE_LAST_MODIFIED" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);

			if (ValidationUtil.isValid(vObject.getDateCreation()))
				strAudit.append("DATE_CREATION" + auditDelimiterColVal + vObject.getDateCreation().trim());
			else
				strAudit.append("DATE_CREATION" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);

		} catch (Exception ex) {
			strErrorDesc = ex.getMessage();
			strAudit = strAudit.append(strErrorDesc);
			ex.printStackTrace();
		}
		return strAudit.toString();
	}
}
