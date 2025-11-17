package com.vision.wb;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.vision.dao.AbstractDao;
import com.vision.dao.AlphaSubTabDao;
import com.vision.dao.CommonDao;
import com.vision.dao.ConcessionBlLinkDao;
import com.vision.dao.NumSubTabDao;
import com.vision.exception.ExceptionCode;
import com.vision.util.CommonUtils;
import com.vision.util.Constants;
import com.vision.vb.AlphaSubTabVb;
import com.vision.vb.CommonVb;
import com.vision.vb.ConcessionBlLinkVb;

@Component
public class ConcessionBlLinkWb extends AbstractDynaWorkerBean<ConcessionBlLinkVb> {
	@Autowired
	private ConcessionBlLinkDao concessionBlLinkDao;
	@Autowired
	private NumSubTabDao numSubTabDao;
	@Autowired
	private AlphaSubTabDao alphaSubTabDao;
	@Autowired
	private CommonDao commonDao;

	public static Logger logger = LoggerFactory.getLogger(ConcessionBlLinkWb.class);

	public ArrayList getPageLoadValues(ConcessionBlLinkVb vObject) {
		List collTemp = null;
		ArrayList<Object> arrListLocal = new ArrayList<Object>();
		try {
			collTemp = numSubTabDao.findActiveNumSubTabsByNumTab(1);// status
			arrListLocal.add(collTemp);
			collTemp = numSubTabDao.findActiveNumSubTabsByNumTab(7);// record indicator
			arrListLocal.add(collTemp);
			collTemp = commonDao.getVisionBusinessDate();
			arrListLocal.add(collTemp);
			collTemp = alphaSubTabDao.findActiveAlphaSubTabsByAlphaTab(2013); // agg func
			arrListLocal.add(collTemp);
			List concessionActList = new ArrayList<>();
			List<AlphaSubTabVb> concessionList = concessionBlLinkDao.getConcessionIdList(vObject);
			for (AlphaSubTabVb vObj : concessionList) {
				String[] concession = vObj.getDescription().split("-");
				String id = concession[0];
				String desc = concession[1];
				vObj.setDescription(id);
				List<AlphaSubTabVb> activityList = concessionBlLinkDao.getActivityIdList(vObject,
						vObj.getDescription());
				vObj.setChildren(activityList);
				concessionActList.add(vObj);
				vObj.setDescription(id + "-" + desc);
			}
			arrListLocal.add(concessionActList);
			return arrListLocal;
		} catch (Exception ex) {
			ex.printStackTrace();
			//logger.error("Exception in getting the Page load values.", ex);
			return null;
		}
	}

	@Override
	protected AbstractDao<ConcessionBlLinkVb> getScreenDao() {
		return concessionBlLinkDao;
	}

	@Override
	protected void setAtNtValues(ConcessionBlLinkVb vObject) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void setVerifReqDeleteType(ConcessionBlLinkVb vObject) {
		ArrayList<CommonVb> lCommVbList = (ArrayList<CommonVb>) commonDao
				.findVerificationRequiredAndStaticDelete("RA_MST_BL_CONCESSIONS");
		vObject.setStaticDelete(lCommVbList.get(0).isStaticDelete());
		vObject.setVerificationRequired(false);
	}

	@Override
	public ExceptionCode getQueryResults(ConcessionBlLinkVb vObject) {
		int intStatus = 1;
		ExceptionCode exceptionCode = new ExceptionCode();
		setVerifReqDeleteType(vObject);
		List<ConcessionBlLinkVb> collTemp = concessionBlLinkDao.getQueryBlResults(vObject, intStatus);
		if (collTemp.size() == 0) {
			intStatus = 0;
			collTemp = concessionBlLinkDao.getQueryBlResults(vObject, intStatus);
		}
		if (collTemp.size() == 0) {
			exceptionCode = CommonUtils.getResultObject(getScreenDao().getServiceDesc(), 16, "Query", "");
			exceptionCode.setOtherInfo(vObject);
			return exceptionCode;
		}

		if (collTemp != null && collTemp.size() > 0) {
			LinkedHashMap<Integer, List<ConcessionBlLinkVb>> map = new LinkedHashMap<>();
			int maxPriority = collTemp.stream().mapToInt(n -> Integer.parseInt(n.getConcessionPriority())).max()
					.orElse(0);
			for (int i = 1; i <= maxPriority; i++) {
				ArrayList<ConcessionBlLinkVb> subPriorityList = new ArrayList();
				for (ConcessionBlLinkVb concessionLinkVb : collTemp) {
					if (i == Integer.parseInt(concessionLinkVb.getConcessionPriority())) {
						subPriorityList.add(concessionLinkVb);
					}
				}
				map.put(i, subPriorityList);
			}
			vObject.setSubPriorityMap(map);
		}
		exceptionCode.setResponse(vObject);
		exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
		exceptionCode.setErrorMsg("Successful operation");
		return exceptionCode;
	}
}


