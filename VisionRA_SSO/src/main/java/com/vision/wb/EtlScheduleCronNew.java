package com.vision.wb;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.vision.controller.CommonApiController;
import com.vision.dao.CommonApiDao;
import com.vision.dao.CommonDao;
import com.vision.dao.EtlServiceDao;
import com.vision.exception.ExceptionCode;
import com.vision.util.Constants;
import com.vision.util.ValidationUtil;

@Component
@EnableAsync
public class EtlScheduleCronNew extends EtlSwitch {

	private final CommonApiController commonApiController;

	public static Logger logger = LoggerFactory.getLogger(EtlScheduleCron.class);
	FileWriter logfile = null;
	BufferedWriter bufferedWriter = null;
	@Autowired
	EtlSwitch etlSwitch;

	@Autowired
	EtlServiceDao etlServiceDao;

	@Autowired
	CommonDao commonDao;
	@Autowired
	CommonApiDao commonApiDao;

	@Value("${app.databaseType}")
	private String databaseType;

	@Value("${schedule.genBuild}")
	private String genBuildFlag;

	@Value("${app.clientName}")
	private String clientName;

	EtlScheduleCronNew(CommonApiController commonApiController) {
		this.commonApiController = commonApiController;
	}

	@Scheduled(fixedRate = 3000)
	@Async
	public void EtlServiceCron() {
		ExceptionCode exceptionCode = new ExceptionCode();
		HashMap<String, String> scheduleControlMap = new HashMap<String, String>();
		String query = "SELECT CRON_TYPE,MAX_THREAD,RUN_THREAD,CRON_SCHEDULE_TIME,CRON_RUN_STATUS FROM RA_CRON_CONTROL WHERE CRON_TYPE='BLD' ";
		try {
			exceptionCode = commonApiDao.getCommonResultDataQuery(query);
			List controllst = (List) exceptionCode.getResponse();
			scheduleControlMap = (HashMap<String, String>) controllst.get(0);
			etlServMaxThreadCnt = Integer.parseInt(scheduleControlMap.get("MAX_THREAD"));
			etlServRunThreadCnt = Integer.parseInt(scheduleControlMap.get("RUN_THREAD"));
			String cronScheduleTime = scheduleControlMap.get("CRON_SCHEDULE_TIME");
			String cronRunStatus = scheduleControlMap.get("CRON_RUN_STATUS");

			if ("Y".equalsIgnoreCase(genBuildFlag) && "Y".equalsIgnoreCase(cronRunStatus)) {
				if (etlServRunThreadCnt < etlServMaxThreadCnt) {
					exceptionCode = etlServiceDao.getEtlPostngHeaderGenBuild();
					if (exceptionCode.getErrorCode() == Constants.SUCCESSFUL_OPERATION) {
						etlServRunThreadCnt++;
						etlServiceDao.raCronUpdate("BLD", etlServRunThreadCnt);

						HashMap<String, String> postingHeaderData = (HashMap<String, String>) exceptionCode
								.getResponse();
						String country = postingHeaderData.get("COUNTRY");
						String leBook = postingHeaderData.get("LE_BOOK");
						String frequency = postingHeaderData.get("EXTRACTION_FREQUENCY");
						String extractionSequence = postingHeaderData.get("EXTRACTION_SEQUENCE");
						String businessDate = postingHeaderData.get("BUSINESS_DATE");
						String submitType = postingHeaderData.get("SUBMIT_TYPE");
						int cnt = 0;
						for (;;) {

							int inProgressCnt = etlServiceDao.checkInProgressFeed(country, leBook, frequency,
									extractionSequence, businessDate);
							if (inProgressCnt > 1) {
								// System.out.println(new Date() + "In Progress is more than 1[" + inProgressCnt
								// + "]");
								continue;
							}
							exceptionCode = etlServiceDao.getEtlPostngDetailGenBuildNew(country, leBook, frequency,
									extractionSequence, businessDate);

							if (exceptionCode.getErrorCode() == Constants.SUCCESSFUL_OPERATION) {
								// GET DATE TIME

								// System.out.println(new Date() + "ETL Posting Data Fetch Completion");

								HashMap<String, String> postingData = (HashMap<String, String>) exceptionCode
										.getResponse();
								String COUNTRY = postingData.get("COUNTRY");
								String LE_BOOK = postingData.get("LE_BOOK");
								String EXTRACTION_FEED_ID = postingData.get("EXTRACTION_FEED_ID");
								String EXTRACTION_FREQUENCY = postingData.get("EXTRACTION_FREQUENCY");
								String EXTRACTION_SEQUENCE = postingData.get("EXTRACTION_SEQUENCE");
								String POSTING_SEQUENCE = postingData.get("POSTING_SEQUENCE");
								String BUSINESS_DATE = postingData.get("BUSINESS_DATE");
								String POSTING_DATE = postingData.get("POSTING_DATE");
								String POSTING_FT_DATE = postingData.get("POSTING_FT_DATE");
								String EXTRACTION_ENGINE = postingData.get("EXTRACTION_ENGINE");

								String logFileName = EXTRACTION_FEED_ID + "_" + POSTING_SEQUENCE + "_" + COUNTRY + "_"
										+ LE_BOOK + "_" + POSTING_FT_DATE;
								String postingStatus = "I";
								etlServiceDao.etlHeaderStatusUpdate(postingStatus, COUNTRY, LE_BOOK,
										EXTRACTION_SEQUENCE, EXTRACTION_FREQUENCY, cnt, businessDate);
								cnt++;
								etlServiceDao.doUpdatePostings(COUNTRY, LE_BOOK, EXTRACTION_FREQUENCY,
										EXTRACTION_SEQUENCE, EXTRACTION_FEED_ID, POSTING_SEQUENCE, BUSINESS_DATE,
										POSTING_DATE, postingStatus);

								etlServiceDao.doUpdatePostingsHistory(COUNTRY, LE_BOOK, EXTRACTION_FREQUENCY,
										EXTRACTION_SEQUENCE, EXTRACTION_FEED_ID, POSTING_SEQUENCE, BUSINESS_DATE,
										POSTING_DATE, postingStatus);

								String execsPath = commonDao.findVisionVariableValue("RA_EXECS_PATH");
								String logPath = commonDao.findVisionVariableValue("RA_SERV_LOGPATH");
								SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
								Date date = df.parse(BUSINESS_DATE);
								df = new SimpleDateFormat("dd-MMM-yyyy");
								String formatBusinessDate = df.format(date);
								String fulLogFileName = logPath + logFileName + ".log";
								File file = new File(fulLogFileName);
								if (!file.exists())
									file.createNewFile();

								etlServiceDao.updateLogFileName(COUNTRY, LE_BOOK, EXTRACTION_FREQUENCY,
										EXTRACTION_SEQUENCE, EXTRACTION_FEED_ID, POSTING_SEQUENCE, BUSINESS_DATE,
										POSTING_DATE, logFileName);
								try {
									if ("BLD".equalsIgnoreCase(EXTRACTION_ENGINE)
											|| "RCY".equalsIgnoreCase(EXTRACTION_ENGINE)
											|| "MST".equalsIgnoreCase(EXTRACTION_ENGINE)) {
										try {
//											genBuildCall();
//											System.out.println();

											String passingParam = "java -jar " + execsPath + genericBuildServiceJar
													+ " " + COUNTRY + " " + LE_BOOK + " " + EXTRACTION_FEED_ID + " "
													+ POSTING_SEQUENCE + " " + formatBusinessDate + " Y "
													+ fulLogFileName + " 9999 ";
											logger.info("Passing Param:" + passingParam);
											System.out.println(passingParam);

											List<String> commandParts = Arrays.asList(passingParam.split(" "));

											// Final platform-independent execution
											ProcessBuilder pb = new ProcessBuilder(commandParts);
											pb.redirectErrorStream(true);
											Process proc = pb.start();

											BufferedReader reader = new BufferedReader(
													new InputStreamReader(proc.getInputStream()));
											String line;
											while ((line = reader.readLine()) != null) {
												System.out.println(line);
											}
											int exitCode = proc.waitFor();

											if (exitCode == 0) {
												postingStatus = "C";
											} else {
												postingStatus = "E";
											}
											/*
											 * InputStream in = proc.getInputStream(); InputStream err =
											 * proc.getErrorStream(); byte b[] = new byte[in.available()]; in.read(b, 0,
											 * b.length); if (ValidationUtil.isValid(new String(b))) { String status =
											 * new String(b); status = status.substring(status.indexOf(":") + 1); if
											 * ("0".equalsIgnoreCase(status.trim())) { postingStatus = "C"; } else {
											 * postingStatus = "E"; } } else { postingStatus = "E"; }
											 */
										} catch (Exception e) {
											// System.out.println("Exception on Generic Build:" + e.getMessage());
											e.printStackTrace();
											postingStatus = "E";
										}
										// System.out.println(new Date() + ":ETL Posting Processing Build Engine End["
//												+ EXTRACTION_FEED_ID + "]");
									} else if ("CHG".equalsIgnoreCase(EXTRACTION_ENGINE)
											|| "PRECONC".equalsIgnoreCase(EXTRACTION_ENGINE)) {
										// System.out.println(new Date() + ":ETL Posting Processing Charge Engine
										// Begin["
//												+ EXTRACTION_FEED_ID + "]");
										chargeEngineJar = ValidationUtil
												.isValid(commonDao.findVisionVariableValue("RA_CHARGE_ENGINE"))
														? commonDao.findVisionVariableValue("RA_CHARGE_ENGINE")
														: chargeEngineJar;

										String passingParam = "";

										passingParam = "java -jar " + execsPath + chargeEngineJar + " " + COUNTRY + " "
												+ LE_BOOK + " " + formatBusinessDate + " " + EXTRACTION_FEED_ID + " Y "
												+ fulLogFileName + " " + POSTING_SEQUENCE + " " + EXTRACTION_ENGINE;
										System.out.println(passingParam);

										/*
										 * String passingParam = "java -jar " + "D:\\RA_Execs\\" + chargeEngineJar + "
										 * " + "KE" + " " + "01" + " " + "01-JUN-2022" + " " + "TEST"+ " Y ";
										 */

										List<String> commandParts = Arrays.asList(passingParam.split(" "));

										// Final platform-independent execution
										ProcessBuilder pb = new ProcessBuilder(commandParts);
										pb.redirectErrorStream(true);
										Process proc = pb.start();

										BufferedReader reader = new BufferedReader(
												new InputStreamReader(proc.getInputStream()));
										String line;
										while ((line = reader.readLine()) != null) {
											System.out.println(line);
										}
										int exitCode = proc.waitFor();
										if (exitCode == 0) {
											postingStatus = "C";
										} else {
											postingStatus = "E";
										}
										/*
										 * writeCronAudit(COUNTRY, LE_BOOK, formatBusinessDate, EXTRACTION_FEED_ID,
										 * logFileName);
										 */
									} else if ("CONC".equalsIgnoreCase(EXTRACTION_ENGINE)) {
										// System.out.println(new Date() + ":ETL Posting Processing Concession Engine
										// Begin["
//												+ EXTRACTION_FEED_ID + "]");
										chargeEngineJar = ValidationUtil
												.isValid(commonDao.findVisionVariableValue("RA_CONCESSION_ENGINE"))
														? commonDao.findVisionVariableValue("RA_CONCESSION_ENGINE")
														: concessionEngineJar;

										String passingParam = "java -jar " + execsPath + chargeEngineJar + " " + COUNTRY
												+ " " + LE_BOOK + " " + formatBusinessDate + " " + EXTRACTION_FEED_ID
												+ " Y " + fulLogFileName + " " + POSTING_SEQUENCE;

										/*
										 * String passingParam = "java -jar " + "D:\\RA_Execs\\" + chargeEngineJar + "
										 * " + "KE" + " " + "01" + " " + "01-JUN-2022" + " " + "TEST"+ " Y ";
										 */

										List<String> commandParts = Arrays.asList(passingParam.split(" "));
										ProcessBuilder pb = new ProcessBuilder(commandParts);
										pb.redirectErrorStream(true);
										Process proc = pb.start();

										BufferedReader reader = new BufferedReader(
												new InputStreamReader(proc.getInputStream()));
										String line;
										while ((line = reader.readLine()) != null) {
											System.out.println(line);
										}
										int exitCode = proc.waitFor();

										if (exitCode == 0) {
											postingStatus = "C";
										} else {
											postingStatus = "E";
										}
										/*
										 * writeCronAudit(COUNTRY, LE_BOOK, formatBusinessDate, EXTRACTION_FEED_ID,
										 * logFileName);
										 */
									} else if ("REC".equalsIgnoreCase(EXTRACTION_ENGINE)
											|| "RCV".equalsIgnoreCase(EXTRACTION_ENGINE)
											|| "RCB".equalsIgnoreCase(EXTRACTION_ENGINE)) {
										String extractionEngineDesc = "Recovery";
										if ("REC".equalsIgnoreCase(EXTRACTION_ENGINE)
												|| "RCB".equalsIgnoreCase(EXTRACTION_ENGINE)) {
											extractionEngineDesc = "Recon";
										}
										logWriter(
												"*************************************************************************",
												logFileName, EXTRACTION_FEED_ID);
										logWriter(
												"" + extractionEngineDesc + " Process Started for Country[" + COUNTRY
														+ "]LEBook[" + LE_BOOK + "]BusinessDate[" + BUSINESS_DATE
														+ "]Extraction Type[" + EXTRACTION_ENGINE + "]",
												logFileName, EXTRACTION_FEED_ID);
										logWriter(
												"*************************************************************************",
												logFileName, EXTRACTION_FEED_ID);
										reconEngineJar = ValidationUtil
												.isValid(commonDao.findVisionVariableValue("RA_RECON_ENGINE"))
														? commonDao.findVisionVariableValue("RA_RECON_ENGINE")
														: reconEngineJar;
										String callCommand = "java -jar " + execsPath + reconEngineJar + " " + COUNTRY
												+ " " + LE_BOOK + " " + formatBusinessDate + " " + EXTRACTION_FEED_ID
												+ " Y " + EXTRACTION_ENGINE + " " + fulLogFileName + " "
												+ POSTING_SEQUENCE;
										
										
										List<String> commandParts = Arrays.asList(callCommand.split(" "));

										// Final platform-independent execution
										ProcessBuilder pb = new ProcessBuilder(commandParts);
										pb.redirectErrorStream(true);
										Process proc = pb.start();

										BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()));
										String line;
										while ((line = reader.readLine()) != null) {
										    System.out.println(line);
										}
										int exitCode = proc.waitFor();

										if (exitCode == 0) {
											postingStatus = "C";
										} else {
											postingStatus = "E";
										}
									} else if ("TAX".equalsIgnoreCase(EXTRACTION_ENGINE)) {
										taxEngineJar = ValidationUtil
												.isValid(commonDao.findVisionVariableValue("RA_TAX_ENGINE"))
														? commonDao.findVisionVariableValue("RA_TAX_ENGINE")
														: taxEngineJar;

										
										String callCommand ="java -jar " + execsPath + taxEngineJar + " " + COUNTRY + " "
												+ LE_BOOK + " " + formatBusinessDate + " " + EXTRACTION_FEED_ID
												+ " Y " + fulLogFileName;
										List<String> commandParts = Arrays.asList(callCommand.split(" "));

										// Final platform-independent execution
										ProcessBuilder pb = new ProcessBuilder(commandParts);
										pb.redirectErrorStream(true);
										Process proc = pb.start();

										BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()));
										String line;
										while ((line = reader.readLine()) != null) {
										    System.out.println(line);
										}
										int exitCode = proc.waitFor();

										if (exitCode == 0) {
											postingStatus = "C";
										} else {
											postingStatus = "E";
										}
									} else if ("TREC".equalsIgnoreCase(EXTRACTION_ENGINE)
											|| "TRCV".equalsIgnoreCase(EXTRACTION_ENGINE)
											|| "TRCB".equalsIgnoreCase(EXTRACTION_ENGINE)) {

										logWriter("**********************************************", logFileName,
												EXTRACTION_FEED_ID);
										logWriter(
												"Tax Recon Execution Started for Country[" + COUNTRY + "]LEBook["
														+ LE_BOOK + "]BusinessDate[" + BUSINESS_DATE + "]",
												logFileName, EXTRACTION_FEED_ID);
										taxReconEngineJar = ValidationUtil
												.isValid(commonDao.findVisionVariableValue("RA_TAX_RECON_ENGINE"))
														? commonDao.findVisionVariableValue("RA_TAX_RECON_ENGINE")
														: taxReconEngineJar;
										String callCommand = "java -jar " + execsPath + taxReconEngineJar + " "
												+ COUNTRY + " " + LE_BOOK + " " + formatBusinessDate + " "
												+ EXTRACTION_FEED_ID + " Y " + EXTRACTION_ENGINE + " " + fulLogFileName
												+ " " + POSTING_SEQUENCE;

										List<String> commandParts = Arrays.asList(callCommand.split(" "));

										// Final platform-independent execution
										ProcessBuilder pb = new ProcessBuilder(commandParts);
										pb.redirectErrorStream(true);
										Process proc = pb.start();

										BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()));
										String line;
										while ((line = reader.readLine()) != null) {
										    System.out.println(line);
										}
										int exitCode = proc.waitFor();


										if (exitCode == 0) {
											postingStatus = "C";
										} else {
											postingStatus = "E";
										}
										logWriter("Tax Recon Process Completion with Status [" + postingStatus + "]!!",
												logFileName, EXTRACTION_FEED_ID);
									} else if ("RAD".equalsIgnoreCase(EXTRACTION_ENGINE)
											|| "RAM".equalsIgnoreCase(EXTRACTION_ENGINE)
											|| "RAH".equalsIgnoreCase(EXTRACTION_ENGINE)
											|| "RAPY".equalsIgnoreCase(EXTRACTION_ENGINE)
											|| "RADM".equalsIgnoreCase(EXTRACTION_ENGINE)) {

										logWriter("**********************************************", logFileName,
												EXTRACTION_FEED_ID);
										logWriter(
												"History Build Execution Started for Country[" + COUNTRY + "]LEBook["
														+ LE_BOOK + "]BusinessDate[" + BUSINESS_DATE + "]",
												logFileName, EXTRACTION_FEED_ID);
										historyBuildJar = ValidationUtil
												.isValid(commonDao.findVisionVariableValue("RA_HISTORY_BUILD_ENGINE"))
														? commonDao.findVisionVariableValue("RA_HISTORY_BUILD_ENGINE")
														: historyBuildJar;

										String callCommand = "java -Dservice.buildname=" + EXTRACTION_ENGINE
												+ "  -Dservice.LogPath=" + logPath + "" + "  -Dservice.LogFileName="
												+ logFileName+".log" + "" + " -jar " + execsPath + historyBuildJar + " "
												+ COUNTRY + " " + LE_BOOK + " " + formatBusinessDate + " "
												+ EXTRACTION_FEED_ID + " Y " + EXTRACTION_ENGINE + " " + fulLogFileName
												+ "" + " " + POSTING_SEQUENCE;
										List<String> commandParts = Arrays.asList(callCommand.split(" "));
										ProcessBuilder pb = new ProcessBuilder(commandParts);
										pb.redirectErrorStream(true);
										Process proc = pb.start();

										BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()));
										String line;
										while ((line = reader.readLine()) != null) {
										    System.out.println(line);
										}
										int exitCode = proc.waitFor();

										if (exitCode == 0) {
											postingStatus = "C";
										} else {
											postingStatus = "E";
										}
										logWriter("Tax Recon Process Completion with Status [" + postingStatus + "]!!",
												logFileName, EXTRACTION_FEED_ID);
									}
									// Updating Status on History
									etlServiceDao.doUpdatePostingsHistory(COUNTRY, LE_BOOK, EXTRACTION_FREQUENCY,
											EXTRACTION_SEQUENCE, EXTRACTION_FEED_ID, POSTING_SEQUENCE, BUSINESS_DATE,
											POSTING_DATE, postingStatus);

									etlServiceDao.doUpdatePostings(COUNTRY, LE_BOOK, EXTRACTION_FREQUENCY,
											EXTRACTION_SEQUENCE, EXTRACTION_FEED_ID, POSTING_SEQUENCE, BUSINESS_DATE,
											POSTING_DATE, postingStatus);

									// Update Log File
									etlServiceDao.updateLogFileName(COUNTRY, LE_BOOK, EXTRACTION_FREQUENCY,
											EXTRACTION_SEQUENCE, EXTRACTION_FEED_ID, POSTING_SEQUENCE, BUSINESS_DATE,
											POSTING_DATE, logFileName);

									etlServiceDao.updateLogFileNameHistory(COUNTRY, LE_BOOK, EXTRACTION_FREQUENCY,
											EXTRACTION_SEQUENCE, EXTRACTION_FEED_ID, POSTING_SEQUENCE, BUSINESS_DATE,
											POSTING_DATE, logFileName);

									if ("E".equalsIgnoreCase(postingStatus)) {
										updateDependentFeed(COUNTRY, LE_BOOK, EXTRACTION_FREQUENCY, EXTRACTION_SEQUENCE,
												EXTRACTION_FEED_ID, POSTING_SEQUENCE, BUSINESS_DATE, POSTING_DATE);
									}
								} catch (Exception e) {
								}
							} else {
								// System.out.println(new Date() + "Extraction Completed for [" + country + "]["
								// + leBook
//										+ "][" + extractionSequence + "][" + businessDate + "]");
								String sql = "";
								if ("ORACLE".equalsIgnoreCase(databaseType)) {
									sql = "select * from ( select POSTED_STATUS  from RA_TRN_POSTING WHERE COUNTRY='"
											+ country + "' AND LE_BOOK='" + leBook + "' AND EXTRACTION_SEQUENCE='"
											+ extractionSequence + "' AND EXTRACTION_FREQUENCY='" + frequency + "' "
											+ "AND BUSINESS_DATE = '" + businessDate + "' )pivot ( "
											+ " count(*) for POSTED_STATUS in ( 'P' PENDING, 'E' ERRORED, 'T' TERMINATED,'C' COMPLETED,'I' INPROGRESS))";
								} else if ("MSSQL".equalsIgnoreCase(databaseType)) {
									sql = "select P AS PENDING, E AS ERRORED, T AS TERMINATED, C AS COMPLETED, I AS INPROGRESS "
											+ "from   ( select POSTED_STATUS from RA_TRN_POSTING  WHERE                            "
											+ "         COUNTRY = '" + country + "'  AND LE_BOOK = '" + leBook
											+ "'                                         "
											+ "         AND EXTRACTION_SEQUENCE = '" + extractionSequence
											+ "'   AND EXTRACTION_FREQUENCY = '" + frequency + "'             "
											+ "         AND BUSINESS_DATE = '" + businessDate
											+ "' )  t1                                    "
											+ "   pivot (                                                                          "
											+ "   count(POSTED_STATUS)  for POSTED_STATUS in  (P,E,T,C,I)) t                       ";

								}

								exceptionCode = commonApiDao.getCommonResultDataQuery(sql);
								List statuslst = (List) exceptionCode.getResponse();
								HashMap<String, String> cntMap = (HashMap<String, String>) statuslst.get(0);
								// System.out.println(new Date() + "Updating Overall Status[" + country + "][" +
								// leBook
//										+ "][" + extractionSequence + "][" + businessDate + "]");
								String etlRunStatus = "";
								if (Integer.parseInt(cntMap.get("PENDING")) == 0) {
									if (Integer.parseInt(cntMap.get("INPROGRESS")) == 0) {
										if (Integer.parseInt(cntMap.get("TERMINATED")) != 0) {
											etlRunStatus = "T";
											etlServiceDao.etlHeaderStatusUpdate(etlRunStatus, country, leBook,
													extractionSequence, frequency, cnt, businessDate);
										} else if (Integer.parseInt(cntMap.get("ERRORED")) != 0) {
											etlRunStatus = "E";
											etlServiceDao.etlHeaderStatusUpdate(etlRunStatus, country, leBook,
													extractionSequence, frequency, cnt, businessDate);
										} else if (Integer.parseInt(cntMap.get("COMPLETED")) != 0) {
											etlRunStatus = "C";
											etlServiceDao.etlHeaderStatusUpdate(etlRunStatus, country, leBook,
													extractionSequence, frequency, cnt, businessDate);
										}
									}
								}
								// System.out.println(new Date() + "Thread Release by Process[" + country + "]["
								// + leBook
//										+ "][" + extractionSequence + "][" + businessDate + "]");
								etlServiceDao.raCronUpdate("BLD", etlServRunThreadCnt - 1);
								if ("S".equalsIgnoreCase(submitType)) {
									etlServiceDao.updateEtlSchedule(country, leBook, frequency, extractionSequence,
											etlRunStatus);
								}
								break;
							}
						}
					}
				}
			}
		} catch (IOException e) {
			// System.out.println(new Date() + "****************Error on RA ETL
			// Service****************");
//			System.out.println(e.getMessage());
			e.printStackTrace();
		} catch (ParseException e1) {
			e1.printStackTrace();
		} catch (Exception e) {
			etlServiceDao.raCronUpdate("BLD", etlServRunThreadCnt - 1);
		} finally {
			if (etlServRunThreadCnt > 0)
				etlServRunThreadCnt = etlServRunThreadCnt - 1;
		}
	}

	public int updateDependentFeed(String COUNTRY, String LE_BOOK, String EXTRACTION_FREQUENCY,
			String EXTRACTION_SEQUENCE, String EXTRACTION_FEED_ID, String POSTING_SEQUENCE, String BUSINESS_DATE,
			String POSTING_DATE) {
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			exceptionCode = etlServiceDao.getEtlPostngDependentBuild(COUNTRY, LE_BOOK, EXTRACTION_FREQUENCY,
					EXTRACTION_SEQUENCE, EXTRACTION_FEED_ID, POSTING_SEQUENCE, BUSINESS_DATE, POSTING_DATE);
			if (exceptionCode.getErrorCode() == Constants.SUCCESSFUL_OPERATION) {
				ArrayList dependCol = (ArrayList) exceptionCode.getResponse();
				if (dependCol != null && !dependCol.isEmpty()) {
					for (int ctr = 0; ctr < dependCol.size(); ctr++) {
						HashMap<String, String> postingData = (HashMap<String, String>) dependCol.get(ctr);
						String depCOUNTRY = postingData.get("COUNTRY");
						String depLE_BOOK = postingData.get("LE_BOOK");
						String depEXTRACTION_FEED_ID = postingData.get("EXTRACTION_FEED_ID");
						String depEXTRACTION_FREQUENCY = postingData.get("EXTRACTION_FREQUENCY");
						String depEXTRACTION_SEQUENCE = postingData.get("EXTRACTION_SEQUENCE");
						String depPOSTING_SEQUENCE = postingData.get("POSTING_SEQUENCE");
						String depBUSINESS_DATE = postingData.get("BUSINESS_DATE");
						String depPOSTING_DATE = postingData.get("POSTING_DATE");
						String depPOSTING_FT_DATE = postingData.get("POSTING_FT_DATE");

						String logFileName = depEXTRACTION_FEED_ID + "_" + depPOSTING_SEQUENCE + "_" + depCOUNTRY + "_"
								+ depLE_BOOK + "_" + depPOSTING_FT_DATE;

						etlServiceDao.doUpdateDependentFeed(depCOUNTRY, depLE_BOOK, depEXTRACTION_FREQUENCY,
								depEXTRACTION_SEQUENCE, depEXTRACTION_FEED_ID, depPOSTING_SEQUENCE, depBUSINESS_DATE,
								depPOSTING_DATE, "E");

						etlServiceDao.doDeleteDependentFeed(depCOUNTRY, depLE_BOOK, depEXTRACTION_FREQUENCY,
								depEXTRACTION_SEQUENCE, depEXTRACTION_FEED_ID, depPOSTING_SEQUENCE, depBUSINESS_DATE,
								depPOSTING_DATE);

						logWriter("**********************************************", logFileName, depEXTRACTION_FEED_ID);
						logWriter("Country[" + depCOUNTRY + "]LEBook[" + depLE_BOOK + "]Feed Id["
								+ depEXTRACTION_FEED_ID + "]Posting Sequence[" + depPOSTING_SEQUENCE + "]BusinessDate["
								+ depBUSINESS_DATE + "] ERRORED!!", logFileName, depEXTRACTION_FEED_ID);
						logWriter(
								"***The Feed Errored due to Dependenct Feed [" + EXTRACTION_FEED_ID + "] Errored!!***",
								logFileName, depEXTRACTION_FEED_ID);

						etlServiceDao.updateLogFileName(depCOUNTRY, depLE_BOOK, depEXTRACTION_FREQUENCY,
								depEXTRACTION_SEQUENCE, depEXTRACTION_FEED_ID, depPOSTING_SEQUENCE, depBUSINESS_DATE,
								depPOSTING_DATE, logFileName);
					}
				}
			}
		} catch (Exception e) {
			// System.out.println("ETL GenBuild Scheduler Service:" + e.getMessage());
			e.printStackTrace();
		}
		return 0;
	}

	private void logWriter(String logString, String logFileName, String feedId) {
		String logPath = commonDao.findVisionVariableValue("RA_SERV_LOGPATH");
		try {
			logfile = new FileWriter(logPath + logFileName + ".log", true);
			bufferedWriter = new BufferedWriter(logfile);
			bufferedWriter.newLine();
			bufferedWriter.write(feedId + " : " + getCurrentDateTime() + " : " + logString);
			bufferedWriter.close();
			logfile.close();
		} catch (Exception e) {
			// System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}

	public String getCurrentDateTime() {
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		Date date = new Date();
		return dateFormat.format(date);
	}

	public void writeCronAudit(String country, String leBook, String businessDate, String feedId, String logFileName) {
		ArrayList<String> errorMsgslst = etlServiceDao.getCronAudit(country, leBook, businessDate, feedId);
		if (errorMsgslst != null && !errorMsgslst.isEmpty()) {
			errorMsgslst.forEach(errorMsg -> {
				logWriter(errorMsg, logFileName, feedId);
			});
		}
		etlServiceDao.deleteChargeAudit(country, leBook, businessDate, feedId);
	}

}
