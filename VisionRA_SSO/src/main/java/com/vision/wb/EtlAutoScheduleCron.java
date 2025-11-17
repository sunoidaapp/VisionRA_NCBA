package com.vision.wb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.vision.dao.CommonApiDao;
import com.vision.dao.EtlPostingsDao;
import com.vision.dao.EtlServiceDao;
import com.vision.exception.ExceptionCode;
import com.vision.util.Constants;
import com.vision.util.ValidationUtil;
import com.vision.vb.CommonApiModel;
import com.vision.vb.EtlPostingsVb;

@Component
public class EtlAutoScheduleCron {

	public static Logger logger = LoggerFactory.getLogger(EtlScheduleCron.class);
	@Autowired
	EtlServiceDao etlServiceDao;
	@Autowired
	EtlPostingsDao etlPostingsDao;
	@Autowired
	CommonApiDao commonApiDao;
	@Value("${schedule.scheduleCron}")
	private String scheduleCron;
	/*
	 * @Value("${schedule.schedulerCronTime}") private String schedulerRunTime;
	 */

	@Scheduled(fixedRate = 3000)
	public void scheduleCron() {
		ExceptionCode exceptionCode = new ExceptionCode();
		ArrayList<EtlPostingsVb> scheduleLst = new ArrayList<EtlPostingsVb>();
		List<EtlPostingsVb> postingDetailLst = null;
		try {
			HashMap<String, String> scheduleControlMap = new HashMap<String, String>();
			String query = "SELECT AUTO_SCHEDULE_CRON_STATUS FROM RA_CRON_CONTROL WHERE CRON_TYPE='BLD' ";
			exceptionCode = commonApiDao.getCommonResultDataQuery(query);
			List controllst = (List) exceptionCode.getResponse();
			if (controllst != null && !controllst.isEmpty())
				scheduleControlMap = (HashMap<String, String>) controllst.get(0);
			String AutoScheduleCronRunStatus = scheduleControlMap.get("AUTO_SCHEDULE_CRON_STATUS");
			if ("Y".equalsIgnoreCase(AutoScheduleCronRunStatus) && "Y".equalsIgnoreCase(scheduleCron)) {
				scheduleLst = (ArrayList<EtlPostingsVb>) etlServiceDao.getScheduleList();
				for (EtlPostingsVb vObject : scheduleLst) {
					//System.out.println(new Date() + "Schedule Record Posting...");
					if ( ValidationUtil.isValid(vObject.getEventName()) && !"NA".equalsIgnoreCase(vObject.getEventName())) {
						CommonApiModel dObj = new CommonApiModel();
						dObj.setQueryId(vObject.getEventName());
						ExceptionCode exceptionCode1 = commonApiDao.getCommonResultDataFetch(dObj, null);
						if (exceptionCode1.getErrorCode() != Constants.SUCCESSFUL_OPERATION) {
							etlServiceDao.updateErrorStatusInEtlSchedule(vObject.getCountry(), vObject.getLeBook(),
									vObject.getExtractionFrequency(), vObject.getExtractionSequence());
							continue;
						} else {
							List readinesslst = (List) exceptionCode1.getResponse();
							if (readinesslst != null && readinesslst.size() > 0) {
								HashMap<String, String> readinessMap = new HashMap<String, String>();
								readinessMap = (HashMap<String, String>) readinesslst.get(0);
								String readiness = readinessMap.get("READINESS_FLAG");
								if (ValidationUtil.isValid(readiness) && "0".equalsIgnoreCase(readiness)) {
									//System.out.println(new Date() + "Readiness False!!");
									continue;
								}
							}
						}

					}
					vObject.setPostingType("I");
					postingDetailLst = etlPostingsDao.getQueryPopupResults(vObject);
					if (postingDetailLst == null || postingDetailLst.isEmpty()) {
						vObject.setPostingType("R");
						postingDetailLst = etlPostingsDao.getQueryPopupResults(vObject);
					}
					if (postingDetailLst != null && !postingDetailLst.isEmpty()) {
						for (int i = 0; i < postingDetailLst.size(); i++) {
							postingDetailLst.get(i).setExrtactionType("S");
							postingDetailLst.get(i).setPostingType(vObject.getPostingType());
						}
						exceptionCode = etlPostingsDao.doInsertApprRecord(postingDetailLst);
					}
					if (exceptionCode.getErrorCode() == Constants.SUCCESSFUL_OPERATION) {
						logger.info("Record Posted successfully");
					} else {
						logger.info("Error in ETL cron scheduler" + exceptionCode.getErrorMsg());
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
