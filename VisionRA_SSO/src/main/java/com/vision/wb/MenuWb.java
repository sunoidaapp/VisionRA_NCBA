package com.vision.wb;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.vision.dao.AbstractDao;
import com.vision.dao.AlphaSubTabDao;
import com.vision.dao.CommonDao;
import com.vision.dao.MenuDao;
import com.vision.dao.NumSubTabDao;
import com.vision.exception.ExceptionCode;
import com.vision.util.CommonUtils;
import com.vision.vb.MenuVb;
import com.vision.vb.NumSubTabVb;
import com.vision.vb.ReviewResultVb;

@Component
public class MenuWb extends AbstractDynaWorkerBean<MenuVb>{
	@Autowired
	private MenuDao menuDao;
	@Autowired
	private NumSubTabDao numSubTabDao;
	@Autowired
	private AlphaSubTabDao alphaSubTabDao;
	@Autowired
	private CommonDao commonDao;
	
	public static Logger logger = LoggerFactory.getLogger(MenuWb.class);
	
	public ArrayList getPageLoadValues(){
		List collTemp = null;
		ArrayList<Object> arrListLocal = new ArrayList<Object>();
		try{
			collTemp = numSubTabDao.findActiveNumSubTabsByNumTab(1);
			arrListLocal.add(collTemp);
			collTemp = numSubTabDao.findActiveNumSubTabsByNumTab(7);
			arrListLocal.add(collTemp);
			collTemp = numSubTabDao.findActiveNumSubTabsByNumTab(6002);
			arrListLocal.add(collTemp);
			collTemp = commonDao.findVerificationRequiredAndStaticDelete("VISION_MENU_MDM");
			arrListLocal.add(collTemp);
			collTemp = commonDao.getAvailableNodesLst();
			arrListLocal.add(collTemp);
			collTemp = alphaSubTabDao.findActiveAlphaSubTabsByAlphaTab(5000);
			arrListLocal.add(collTemp);
			return arrListLocal;
		}catch(Exception ex){
			ex.printStackTrace();
			//logger.error("Exception in getting the Page load values.", ex);
			return null;
		}
	}
	
	@Override
	protected List<ReviewResultVb> transformToReviewResults(List<MenuVb> approvedCollection, List<MenuVb> pendingCollection) {
		/*ResourceBundle rsb = CommonUtils.getResourceManger();*/
		ArrayList collTemp = getPageLoadValues();
		if(pendingCollection != null)
			getScreenDao().fetchMakerVerifierNames(pendingCollection.get(0));
		if(approvedCollection != null)
			getScreenDao().fetchMakerVerifierNames(approvedCollection.get(0));
		ArrayList<ReviewResultVb> lResult = new ArrayList<ReviewResultVb>();
		ReviewResultVb lMenuGroup = new ReviewResultVb("Menu Group",(pendingCollection == null || pendingCollection.isEmpty())?"":getNtDescription((List<NumSubTabVb>) collTemp.get(2),pendingCollection.get(0).getMenuGroup()),
				(approvedCollection == null || approvedCollection.isEmpty())?"":getNtDescription((List<NumSubTabVb>) collTemp.get(2),approvedCollection.get(0).getMenuGroup()));
		lResult.add(lMenuGroup);
		ReviewResultVb lMenuSequence = new ReviewResultVb("Menu Sequence",(pendingCollection == null || pendingCollection.isEmpty())?"":String.valueOf(pendingCollection.get(0).getMenuSequence()),
				(approvedCollection == null || approvedCollection.isEmpty())?"":String.valueOf(approvedCollection.get(0).getMenuSequence()));
		lResult.add(lMenuSequence);
		ReviewResultVb lParentSequence = new ReviewResultVb("Parent Sequence",(pendingCollection == null || pendingCollection.isEmpty())?"":String.valueOf(pendingCollection.get(0).getParentSequence()),
				(approvedCollection == null || approvedCollection.isEmpty())?"":String.valueOf(approvedCollection.get(0).getParentSequence()));
		lResult.add(lParentSequence);		
		ReviewResultVb lMenuProgram = new ReviewResultVb("Menu Program",(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getMenuProgram(),
				(approvedCollection == null || approvedCollection.isEmpty())?"":String.valueOf(approvedCollection.get(0).getMenuProgram()));
		lResult.add(lMenuProgram);
		ReviewResultVb lMenuName = new ReviewResultVb("Menu Name",(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getMenuName(),
				(approvedCollection == null || approvedCollection.isEmpty())?"":String.valueOf(approvedCollection.get(0).getMenuName()));
		lResult.add(lMenuName);
		
		ReviewResultVb lMenuNodeAvailable = new ReviewResultVb("Menu Node Available",(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getMenuNodeAvailable(),
				(approvedCollection == null || approvedCollection.isEmpty())?"":String.valueOf(approvedCollection.get(0).getMenuNodeAvailable()));
		lResult.add(lMenuNodeAvailable);

		ReviewResultVb lSeparator = new ReviewResultVb("Separator","Y".equalsIgnoreCase(pendingCollection.get(0).getSeparator())?"Yes":"No",
				("Y".equalsIgnoreCase(approvedCollection.get(0).getSeparator())?"Yes":"No"));
		lResult.add(lSeparator);
		ReviewResultVb lMenuStatus = new ReviewResultVb("Status",(pendingCollection == null || pendingCollection.isEmpty())?"":getNtDescription((List<NumSubTabVb>) collTemp.get(0),pendingCollection.get(0).getMenuStatus()),
				(approvedCollection == null || approvedCollection.isEmpty())?"":getNtDescription((List<NumSubTabVb>) collTemp.get(0),approvedCollection.get(0).getMenuStatus()));
		lResult.add(lMenuStatus);
		ReviewResultVb lRecordIndicator = new ReviewResultVb("RecordIndicator",(pendingCollection == null || pendingCollection.isEmpty())?"":getNtDescription((List<NumSubTabVb>) collTemp.get(1),pendingCollection.get(0).getRecordIndicator()),
				(approvedCollection == null || approvedCollection.isEmpty())?"":getNtDescription((List<NumSubTabVb>) collTemp.get(1),approvedCollection.get(0).getRecordIndicator()));
		lResult.add(lRecordIndicator);
		ReviewResultVb lMaker = new ReviewResultVb("Maker",(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getMaker() == 0?"":pendingCollection.get(0).getMakerName(),
				(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getMaker() == 0?"":approvedCollection.get(0).getMakerName());
		lResult.add(lMaker);
		ReviewResultVb lVerifier = new ReviewResultVb("Verifier",(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getVerifier() == 0?"":pendingCollection.get(0).getVerifierName(),
				(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getVerifier() == 0?"":approvedCollection.get(0).getVerifierName());
		lResult.add(lVerifier);
		ReviewResultVb lDateLastModified = new ReviewResultVb("Date Last Modified",(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getDateLastModified(),
				(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getDateLastModified());
		lResult.add(lDateLastModified);
		ReviewResultVb lDateCreation = new ReviewResultVb("Date Creation",(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getDateCreation(),
				(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getDateCreation());
		lResult.add(lDateCreation);
		return lResult;
	}
	public MenuDao getMenuDao() {
		return menuDao;
	}

	public void setMenuDao(MenuDao menuDao) {
		this.menuDao = menuDao;
	}

	@Override
	protected void setAtNtValues(MenuVb vObject) {
		vObject.setMenuGroupNt(176);
		vObject.setMenuStatusNt(1);
		vObject.setRecordIndicatorNt(7);
	}

	@Override
	protected void setVerifReqDeleteType(MenuVb vObject) {
		vObject.setStaticDelete(false);
		vObject.setVerificationRequired(false);
		
	}
	@Override
	protected AbstractDao<MenuVb> getScreenDao() {
		return menuDao;
	}
	public ExceptionCode getQueryResults(MenuVb vObject){
		int intStatus = 1;
		setVerifReqDeleteType(vObject);
		List<MenuVb> collTemp = menuDao.getQueryResults(vObject,intStatus);
		if(collTemp.size() == 0){
			ExceptionCode exceptionCode = CommonUtils.getResultObject(getScreenDao().getServiceDesc(), 16, "Query", "");
			exceptionCode.setOtherInfo(vObject);
			return exceptionCode;
		}else{
			ArrayList<MenuVb> menuList = new ArrayList<MenuVb>(); 
			for(MenuVb vObj : collTemp) {
				vObject.setParentSequence(vObj.getParentSequence());
				vObject.setMenuGroup(vObj.getMenuGroup());
				vObject.setTotalRows(0);
				List<MenuVb> subMenulst = menuDao.getQueryResultsSubMenu(vObject, intStatus);
				vObj.setSubMenulst(subMenulst);
				menuList.add(vObj);
			}
			doSetDesctiptionsAfterQuery(menuList);
			doSetDesctiptionsAfterQuery(vObject);
			ExceptionCode exceptionCode = CommonUtils.getResultObject(getScreenDao().getServiceDesc(), 1, "Query", "");
			exceptionCode.setOtherInfo(vObject);
			exceptionCode.setResponse(menuList);
			return exceptionCode;
		}
	}
}
