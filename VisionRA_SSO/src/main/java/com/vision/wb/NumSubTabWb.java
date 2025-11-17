package com.vision.wb;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.vision.dao.AbstractDao;
import com.vision.dao.NumSubTabDao;
import com.vision.exception.RuntimeCustomException;
import com.vision.util.CommonUtils;
import com.vision.vb.CommonVb;
import com.vision.vb.NumSubTabVb;
import com.vision.vb.ReviewResultVb;

@Component
public class NumSubTabWb extends AbstractDynaWorkerBean<NumSubTabVb> {

	@Autowired
	NumSubTabDao numSubTabDao;
	
	@Override
	protected AbstractDao<NumSubTabVb> getScreenDao() {
		return numSubTabDao;
	}
	
	@Override
	protected void setAtNtValues(NumSubTabVb object) {
		object.setRecordIndicatorNt(7);
		object.setNumSubTabStatusNt(1);
	}

	@Override
	protected void setVerifReqDeleteType(NumSubTabVb object) {
		ArrayList<CommonVb> lCommVbList =(ArrayList<CommonVb>) getCommonDao().findVerificationRequiredAndStaticDelete("NUM_TAB");
		object.setStaticDelete(lCommVbList.get(0).isStaticDelete());
		object.setVerificationRequired(lCommVbList.get(0).isVerificationRequired());
	}
	
	public List<NumSubTabVb> findActiveNumSubTabsByNumTab(int pNumTab) {
		try {
			return numSubTabDao.findActiveNumSubTabsByNumTab(pNumTab);
		}catch(Exception e) {
			throw new RuntimeCustomException(e.getMessage());
		}
	}
	
	public List<NumSubTabVb> findActiveNumSubTabsByNumTabCols(int pNumTab, String columns) {
		try {
			return numSubTabDao.findActiveNumSubTabsByNumTabCols(pNumTab, columns);
		}catch(Exception e) {
			throw new RuntimeCustomException(e.getMessage());
		}
	}
	public ArrayList getPageLoadValues(){
		List collTemp = null;
		ArrayList<Object> arrListLocal = new ArrayList<Object>();
		try{
			collTemp = getNumSubTabDao().findActiveNumSubTabsByNumTab(1);
			arrListLocal.add(collTemp);
			collTemp = getNumSubTabDao().findActiveNumSubTabsByNumTab(7);
			arrListLocal.add(collTemp);
			return arrListLocal;
		}catch(Exception ex){
			//ex.printStackTrace();
			//logger.error("Exception in getting the Page load values.", ex);
			return null;
		}
	}
	@Override
	protected List<ReviewResultVb> transformToReviewResults(List<NumSubTabVb> approvedCollection, List<NumSubTabVb> pendingCollection) {
		ResourceBundle rsb = CommonUtils.getResourceManger();
		ArrayList collTemp = getPageLoadValues();
		if(pendingCollection != null)
			getScreenDao().fetchMakerVerifierNames(pendingCollection.get(0));
		if(approvedCollection != null)
			getScreenDao().fetchMakerVerifierNames(approvedCollection.get(0));
		ArrayList<ReviewResultVb> lResult = new ArrayList<ReviewResultVb>();
		ReviewResultVb lTab = new ReviewResultVb(rsb.getString("numTab"),(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getNumTab() == -1?"":String.valueOf(pendingCollection.get(0).getNumTab()),
				(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getNumTab() == -1?"":String.valueOf(approvedCollection.get(0).getNumTab()), !(approvedCollection.get(0).getNumTab() == pendingCollection.get(0).getNumTab()));
		lResult.add(lTab);
		ReviewResultVb lSubTab = new ReviewResultVb(rsb.getString("subTab"),(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getNumSubTab() == -1?"":String.valueOf(pendingCollection.get(0).getNumSubTab()),
				(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getNumSubTab() == -1?"":String.valueOf(approvedCollection.get(0).getNumSubTab()), !(approvedCollection.get(0).getNumSubTab() == pendingCollection.get(0).getNumSubTab()));
		lResult.add(lSubTab);
		ReviewResultVb lDescription = new ReviewResultVb(rsb.getString("description"),(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getDescription(),
				(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getDescription(), !(approvedCollection.get(0).getDescription().equals(pendingCollection.get(0).getDescription())));
		lResult.add(lDescription);
		ReviewResultVb lStatus = new ReviewResultVb(rsb.getString("status"),(pendingCollection == null || pendingCollection.isEmpty())?"":getNtDescription((List<NumSubTabVb>) collTemp.get(0),pendingCollection.get(0).getNumSubTabStatus()),
				(approvedCollection == null || approvedCollection.isEmpty())?"":getNtDescription((List<NumSubTabVb>) collTemp.get(0),approvedCollection.get(0).getNumSubTabStatus()), !(approvedCollection.get(0).getNumSubTabStatus() == pendingCollection.get(0).getNumSubTabStatus()));
		lResult.add(lStatus);
		ReviewResultVb lRecordIndicator = new ReviewResultVb(rsb.getString("recordIndicator"),(pendingCollection == null || pendingCollection.isEmpty())?"":getNtDescription((List<NumSubTabVb>) collTemp.get(1), pendingCollection.get(0).getRecordIndicator()),
				(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getRecordIndicator() == -1?"":getNtDescription((List<NumSubTabVb>) collTemp.get(1), approvedCollection.get(0).getRecordIndicator()), !(approvedCollection.get(0).getRecordIndicator() == pendingCollection.get(0).getRecordIndicator()));
		lResult.add(lRecordIndicator);
		ReviewResultVb lMaker = new ReviewResultVb(rsb.getString("maker"),(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getMaker() == 0?"":pendingCollection.get(0).getMakerName(),
				(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getMaker() == 0?"":approvedCollection.get(0).getMakerName(), !(approvedCollection.get(0).getMaker() == pendingCollection.get(0).getMaker()));
		lResult.add(lMaker);
		ReviewResultVb lVerifier = new ReviewResultVb(rsb.getString("verifier"),(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getVerifier() == 0?"":pendingCollection.get(0).getVerifierName(),
				(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getVerifier() == 0?"":approvedCollection.get(0).getVerifierName(), !(approvedCollection.get(0).getVerifier() == pendingCollection.get(0).getVerifier()));
		lResult.add(lVerifier);
		ReviewResultVb lDateLastModified = new ReviewResultVb(rsb.getString("dateLastModified"),(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getDateLastModified(),
				(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getDateLastModified(), !(approvedCollection.get(0).getDateLastModified().equals(pendingCollection.get(0).getDateLastModified())));
		lResult.add(lDateLastModified);
		ReviewResultVb lDateCreation = new ReviewResultVb(rsb.getString("dateCreation"),(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getDateCreation(),
				(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getDateCreation(), !(approvedCollection.get(0).getDateCreation().equals(pendingCollection.get(0).getDateCreation())));
		lResult.add(lDateCreation);
		return lResult;
	}
}