package com.vision.wb;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.vision.dao.CommonDao;
import com.vision.dao.EtlServiceDao;
import com.vision.exception.ExceptionCode;
import com.vision.util.Constants;
import com.vision.util.ValidationUtil;

@Component
public class EtlScheduleCron extends EtlSwitch {
	public static Logger logger = LoggerFactory.getLogger(EtlScheduleCron.class);
	FileWriter logfile = null;
	BufferedWriter bufferedWriter = null;
	@Autowired
	EtlSwitch etlSwitch;
	
	@Autowired
	EtlServiceDao etlServiceDao;
	
	@Autowired
	CommonDao commonDao;
	
	
	@Value("${schedule.genBuild}")
	private String genBuildFlag;
	
	// @Scheduled(fixedRate = 3000)
	public void EtlServiceCron() {
		String etlService = commonDao.findVisionVariableValue("RA_ETL_RUNSTATUS");
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			if("Y".equalsIgnoreCase(genBuildFlag) && "Y".equalsIgnoreCase(etlService)) {
					//System.out.println(new Date()+":ETL Max Thread Count["+etlServMaxThreadCnt+"]");
					//System.out.println(new Date()+":ETL Current Run Thread Count["+etlServRunThreadCnt+"]");
					if(etlServRunThreadCnt < etlServMaxThreadCnt) {
						etlServRunThreadCnt++;
						exceptionCode = etlServiceDao.getEtlPostngDetailGenBuild();
						if(exceptionCode.getErrorCode() == Constants.SUCCESSFUL_OPERATION) {
							//System.out.println(new Date()+"ETL Posting Data Fetch Completion");
							HashMap<String,String> postingData = (HashMap<String,String>)exceptionCode.getResponse();
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
							
							String logFileName = EXTRACTION_FEED_ID+"_"+POSTING_SEQUENCE+"_"+COUNTRY+"_"+LE_BOOK+"_"+POSTING_FT_DATE;
							String postingStatus = "I";
							
							etlServiceDao.doUpdatePostings(COUNTRY,LE_BOOK,EXTRACTION_FREQUENCY,EXTRACTION_SEQUENCE,EXTRACTION_FEED_ID,
									POSTING_SEQUENCE,BUSINESS_DATE,POSTING_DATE,postingStatus);
							
							etlServiceDao.doUpdatePostingsHistory(COUNTRY,LE_BOOK,EXTRACTION_FREQUENCY,EXTRACTION_SEQUENCE,EXTRACTION_FEED_ID,
									POSTING_SEQUENCE,BUSINESS_DATE,POSTING_DATE,postingStatus);
							
							String execsPath =  commonDao.findVisionVariableValue("RA_EXECS_PATH");
							String logPath =  commonDao.findVisionVariableValue("RA_SERV_LOGPATH");
							SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
						    Date date=df.parse(BUSINESS_DATE);
						    df=new SimpleDateFormat("dd-MMM-yyyy");
						    String formatBusinessDate = df.format(date);
							try {
								if("BLD".equalsIgnoreCase(EXTRACTION_ENGINE) || "RCY".equalsIgnoreCase(EXTRACTION_ENGINE) || "MST".equalsIgnoreCase(EXTRACTION_ENGINE)) {
									//System.out.println(new Date()+":ETL Posting Processing Build Engine Begin["+EXTRACTION_FEED_ID+"]");
									Process proc;
									proc = Runtime.getRuntime().exec(
											"java -jar "+execsPath+genericBuildServiceJar+" "+COUNTRY+" "+LE_BOOK+" "+EXTRACTION_FEED_ID+" "+POSTING_SEQUENCE+" "+formatBusinessDate+" Y "+logPath+" 9999 ");
									proc.waitFor();
									InputStream in = proc.getInputStream();
									InputStream err = proc.getErrorStream();
									byte b[]=new byte[in.available()];
									in.read(b,0,b.length);
								    if(ValidationUtil.isValid(new String(b))) {
								    	String status = new String(b);
								    	status = status.substring(status.indexOf(":")+1);
								    	if("0".equalsIgnoreCase(status.trim())) {
								    		postingStatus  = "C";	
								    	}else {
								    		postingStatus  = "E";
								    	}
								    }
								    //System.out.println(new Date()+":ETL Posting Processing Build Engine End["+EXTRACTION_FEED_ID+"]");
								}else if ("CHG".equalsIgnoreCase(EXTRACTION_ENGINE)) {
									//System.out.println(new Date()+":ETL Posting Processing Charge Engine Begin["+EXTRACTION_FEED_ID+"]");
									logWriter("**********************************************",logFileName,EXTRACTION_FEED_ID);
									logWriter("Charge Calculation Execution Started for Country["+COUNTRY+"]LEBook["+LE_BOOK+"]BusinessDate["+BUSINESS_DATE+"]",logFileName,EXTRACTION_FEED_ID);

									ExceptionCode exceptionCodeChg = etlServiceDao.chargeCalculationProcCall(COUNTRY,LE_BOOK,formatBusinessDate,EXTRACTION_FEED_ID);
									
									if(exceptionCodeChg.getErrorCode() == 0) {
										postingStatus = "C";
									}else {
										postingStatus = "E";
									}
									//System.out.println(new Date()+":ETL Posting Processing Charge Engine End["+EXTRACTION_FEED_ID+"]");
									writeCronAudit(COUNTRY,LE_BOOK,formatBusinessDate,EXTRACTION_FEED_ID,logFileName);
									logWriter(""+exceptionCodeChg.getErrorMsg(),logFileName,EXTRACTION_FEED_ID);
									logWriter("Charge Process Completion with Status ["+postingStatus+"]!!",logFileName,EXTRACTION_FEED_ID);
								}else if ("REC".equalsIgnoreCase(EXTRACTION_ENGINE)) {
									//System.out.println(new Date()+"ETL Posting Processing Recon Engine Begin["+EXTRACTION_FEED_ID+"]");
									logWriter("**********************************************",logFileName,EXTRACTION_FEED_ID);
									logWriter("Recon Execution Started for Country["+COUNTRY+"]LEBook["+LE_BOOK+"]BusinessDate["+BUSINESS_DATE+"]",logFileName,EXTRACTION_FEED_ID);

									ExceptionCode exceptionCodeChg = etlServiceDao.ReconProcCall(COUNTRY,LE_BOOK,formatBusinessDate,EXTRACTION_FEED_ID,EXTRACTION_ENGINE);
									
									if(exceptionCodeChg.getErrorCode() == 0) {
										postingStatus = "C";
									}else {
										postingStatus = "E";
									}
									//logger.info("ETL Posting Processing Recon Engine End["+EXTRACTION_FEED_ID+"]");
									writeCronAudit(COUNTRY,LE_BOOK,formatBusinessDate,EXTRACTION_FEED_ID,logFileName);
									logWriter(""+exceptionCodeChg.getErrorMsg(),logFileName,EXTRACTION_FEED_ID);
									logWriter("Recon Process Completion with Status ["+postingStatus+"]!!",logFileName,EXTRACTION_FEED_ID);
								}
						    	//Updating Status on History	
						    	etlServiceDao.doUpdatePostingsHistory(COUNTRY,LE_BOOK,EXTRACTION_FREQUENCY,EXTRACTION_SEQUENCE,EXTRACTION_FEED_ID,
										POSTING_SEQUENCE,BUSINESS_DATE,POSTING_DATE,postingStatus);
						    	//Delete Posting Table
						    	etlServiceDao.doDeletePostings(COUNTRY,LE_BOOK,EXTRACTION_FREQUENCY,EXTRACTION_SEQUENCE,EXTRACTION_FEED_ID,
										POSTING_SEQUENCE,BUSINESS_DATE,POSTING_DATE,postingStatus);
						    	//Update Log File
						    	etlServiceDao.updateLogFileName(COUNTRY,LE_BOOK,EXTRACTION_FREQUENCY,EXTRACTION_SEQUENCE,EXTRACTION_FEED_ID,
										POSTING_SEQUENCE,BUSINESS_DATE,POSTING_DATE,logFileName);
						    	
						    	if("E".equalsIgnoreCase(postingStatus)) {
						    		updateDependentFeed(COUNTRY,LE_BOOK,EXTRACTION_FREQUENCY,EXTRACTION_SEQUENCE,EXTRACTION_FEED_ID,
											POSTING_SEQUENCE,BUSINESS_DATE,POSTING_DATE);
						    	}
							   
							} catch (InterruptedException e) {
								//System.out.println(new Date()+"****************Error on RA ETL Service****************");
								//System.out.println(new Date()+e.getMessage());
							}
						}
					}
				}
			}catch (IOException e) {
				//System.out.println(new Date()+"****************Error on RA ETL Service****************");
				//System.out.println(e.getMessage());
				e.printStackTrace();
			} catch (ParseException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}finally {
				if(etlServRunThreadCnt > 0)
					etlServRunThreadCnt = etlServRunThreadCnt-1;
			}
		}
	public int updateDependentFeed(String COUNTRY,String LE_BOOK,String EXTRACTION_FREQUENCY,String EXTRACTION_SEQUENCE,String EXTRACTION_FEED_ID,
			String POSTING_SEQUENCE,String BUSINESS_DATE,String POSTING_DATE) {
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			exceptionCode = etlServiceDao.getEtlPostngDependentBuild(COUNTRY,LE_BOOK,EXTRACTION_FREQUENCY,EXTRACTION_SEQUENCE,EXTRACTION_FEED_ID,
					POSTING_SEQUENCE,BUSINESS_DATE,POSTING_DATE);
			if(exceptionCode.getErrorCode() == Constants.SUCCESSFUL_OPERATION) {
				ArrayList dependCol = (ArrayList)exceptionCode.getResponse();
				if(dependCol != null && !dependCol.isEmpty()) {
					for(int ctr =0;ctr < dependCol.size();ctr++) {
						HashMap<String,String> postingData = (HashMap<String,String>)dependCol.get(ctr);
						String depCOUNTRY = postingData.get("COUNTRY");
						String depLE_BOOK = postingData.get("LE_BOOK");
						String depEXTRACTION_FEED_ID = postingData.get("EXTRACTION_FEED_ID");
						String depEXTRACTION_FREQUENCY = postingData.get("EXTRACTION_FREQUENCY");
						String depEXTRACTION_SEQUENCE = postingData.get("EXTRACTION_SEQUENCE");
						String depPOSTING_SEQUENCE = postingData.get("POSTING_SEQUENCE");
						String depBUSINESS_DATE = postingData.get("BUSINESS_DATE");
						String depPOSTING_DATE = postingData.get("POSTING_DATE");
						String depPOSTING_FT_DATE = postingData.get("POSTING_FT_DATE");
						
						String logFileName = depEXTRACTION_FEED_ID+"_"+depPOSTING_SEQUENCE+"_"+depCOUNTRY+"_"+depLE_BOOK+"_"+depPOSTING_FT_DATE;
						
						etlServiceDao.doUpdateDependentFeed(depCOUNTRY,depLE_BOOK,depEXTRACTION_FREQUENCY,depEXTRACTION_SEQUENCE,depEXTRACTION_FEED_ID,
								depPOSTING_SEQUENCE,depBUSINESS_DATE,depPOSTING_DATE,"E");
	
						etlServiceDao.doDeleteDependentFeed(depCOUNTRY,depLE_BOOK,depEXTRACTION_FREQUENCY,depEXTRACTION_SEQUENCE,depEXTRACTION_FEED_ID,
								depPOSTING_SEQUENCE,depBUSINESS_DATE,depPOSTING_DATE);
						
						logWriter("**********************************************",logFileName,depEXTRACTION_FEED_ID);
						logWriter("Country["+depCOUNTRY+"]LEBook["+depLE_BOOK+"]Feed Id["+depEXTRACTION_FEED_ID+"]Posting Sequence["+depPOSTING_SEQUENCE+"]BusinessDate["+depBUSINESS_DATE+"] ERRORED!!",logFileName,depEXTRACTION_FEED_ID);
						logWriter("***The Feed Errored due to Dependenct Feed ["+EXTRACTION_FEED_ID+"] Errored!!***",logFileName,depEXTRACTION_FEED_ID);
						
						etlServiceDao.updateLogFileName(depCOUNTRY,depLE_BOOK,depEXTRACTION_FREQUENCY,depEXTRACTION_SEQUENCE,depEXTRACTION_FEED_ID,
								depPOSTING_SEQUENCE,depBUSINESS_DATE,depPOSTING_DATE,logFileName);
					}
				}
			}
		}catch(Exception e) {
			//System.out.println("ETL GenBuild Scheduler Service:"+e.getMessage());
		}
		return 0;
	}
	private void logWriter(String logString,String logFileName,String feedId) {
		String logPath =  commonDao.findVisionVariableValue("RA_SERV_LOGPATH");
		try {
			logfile = new FileWriter(logPath+logFileName+".log", true);
			bufferedWriter = new BufferedWriter(logfile);
			bufferedWriter.newLine();
			bufferedWriter.write(feedId+" : " + getCurrentDateTime() + " : " + logString);
			bufferedWriter.close();
			logfile.close();
		} catch (Exception e) {
			//System.out.println(e.getMessage());
		}
	}
	public String getCurrentDateTime() {
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		Date date = new Date();
		return dateFormat.format(date);
	}
	public void writeCronAudit(String country,String leBook,String businessDate,String feedId,String logFileName) {
		ArrayList<String> errorMsgslst  = etlServiceDao.getCronAudit(country,leBook,businessDate,feedId);
		if(errorMsgslst != null && !errorMsgslst.isEmpty()) {
			errorMsgslst.forEach(errorMsg -> {
				logWriter(errorMsg,logFileName,feedId);
			}); 
		}
		etlServiceDao.deleteChargeAudit(country,leBook,businessDate,feedId);
	}
}
