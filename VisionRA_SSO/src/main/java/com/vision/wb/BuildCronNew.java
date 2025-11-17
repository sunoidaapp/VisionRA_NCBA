package com.vision.wb;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPCmd;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.vision.dao.CommonDao;
import com.vision.dao.EtlServiceDao;
import com.vision.exception.ExceptionCode;
import com.vision.util.Constants;
import com.vision.util.ValidationUtil;

@Component
public class BuildCronNew extends EtlSwitch {
	FileWriter logfile = null;
	BufferedWriter bufferedWriter = null;
	@Autowired
	EtlSwitch etlSwitch;

	@Autowired
	EtlServiceDao etlServiceDao;

	@Autowired
	CommonDao commonDao;

	@Value("${ftp.hostName}")
	private String ftpHost;

	@Value("${ftp.userName}")
	private String ftpUserName;

	@Value("${ftp.password}")
	private String ftpPassword;

	@Value("${ftp.serverType}")
	private String ftpServerType;

	@Value("${ftp.securedFtp}")
	private String ftpSecuredFtp;

	@Value("${schedule.adfBuild}")
	private String adfBuildFlag;

	@Scheduled(fixedRate = 30000)
	public void adfBuildCron() {
		ExceptionCode exceptionCode = new ExceptionCode();
		Boolean processControlDataPresent = false;
		try {
			if ("Y".equalsIgnoreCase(adfBuildFlag) && buildService) {
				if (buildServRunThreadCnt < buildServMaxThreadCnt) {
					buildServRunThreadCnt++;
					// //System.out.println("RA-ADF ENGINE RUNNING!!!");
					exceptionCode = etlServiceDao.getEtlPostngDetailAdfBuild();

					if (exceptionCode.getErrorCode() == Constants.SUCCESSFUL_OPERATION) {

						HashMap<String, String> postingData = (HashMap<String, String>) exceptionCode.getResponse();

						String COUNTRY = postingData.get("COUNTRY");
						String LE_BOOK = postingData.get("LE_BOOK");
						String EXTRACTION_FEED_ID = postingData.get("EXTRACTION_FEED_ID");
						String EXTRACTION_FREQUENCY = postingData.get("EXTRACTION_FREQUENCY");
						String EXTRACTION_SEQUENCE = postingData.get("EXTRACTION_SEQUENCE");
						String POSTING_SEQUENCE = postingData.get("POSTING_SEQUENCE");
						String BUSINESS_DATE = postingData.get("BUSINESS_DATE");
						String POSTING_DATE = postingData.get("POSTING_DATE");
						String logFileName = EXTRACTION_FEED_ID + "_" + POSTING_SEQUENCE + "_" + COUNTRY + "_" + LE_BOOK
								+ "_" + BUSINESS_DATE;
						String postingStatus = "I";

						logWriter("RA-ADF Process Started for Country[" + COUNTRY + "]LEBook[" + LE_BOOK
								+ "]PostingSeq[" + POSTING_SEQUENCE + "]", logFileName, EXTRACTION_FEED_ID);

						finalServiceProcess(COUNTRY, LE_BOOK, EXTRACTION_FREQUENCY, EXTRACTION_SEQUENCE,
								EXTRACTION_FEED_ID, POSTING_SEQUENCE, BUSINESS_DATE, POSTING_DATE, postingStatus,
								logFileName, false);

						logWriter("Inserting ADF Process Control Data ", logFileName, EXTRACTION_FEED_ID);

						ExceptionCode exceptionCpdeAdf = new ExceptionCode();
						try {
							exceptionCpdeAdf = etlServiceDao.doSelectAdfAcquisition(COUNTRY, LE_BOOK,
									EXTRACTION_FEED_ID, POSTING_SEQUENCE, BUSINESS_DATE);
						} catch (Exception e) {
							finalServiceProcess(COUNTRY, LE_BOOK, EXTRACTION_FREQUENCY, EXTRACTION_SEQUENCE,
									EXTRACTION_FEED_ID, POSTING_SEQUENCE, BUSINESS_DATE, POSTING_DATE, "E", logFileName,
									true);
						}

						HashMap<String, String> processCtrlDataMap = new HashMap<String, String>();
						if (exceptionCpdeAdf.getErrorCode() == Constants.SUCCESSFUL_OPERATION) {
							if (ValidationUtil.isValid(exceptionCode.getResponse())) {
								processCtrlDataMap = (HashMap<String, String>) exceptionCpdeAdf.getResponse();
								String PREACTIVITY_SCRIPTS = processCtrlDataMap.get("PREACTIVITY_SCRIPTS");
								if (ValidationUtil.isValid(PREACTIVITY_SCRIPTS)) {
									logWriter("Pre-Activity Scripts execution started ", logFileName,
											EXTRACTION_FEED_ID);
									PREACTIVITY_SCRIPTS = etlServiceDao.replaceHashPrompts(processCtrlDataMap,
											POSTING_SEQUENCE, BUSINESS_DATE, PREACTIVITY_SCRIPTS);
									etlServiceDao.executePreactivityScript(PREACTIVITY_SCRIPTS);
									logWriter("Pre-Activity Scripts execution completed ", logFileName,
											EXTRACTION_FEED_ID);
								}
								processControlDataPresent = true;
							}

							logWriter("ADF Process Control Data Insertion Completed ", logFileName, EXTRACTION_FEED_ID);

							String execsPath = commonDao.findVisionVariableValue("RA_ADF_PATH");

							logWriter("Calling ADF JAR ", logFileName, EXTRACTION_FEED_ID);

							try {
								Process proc;
								proc = Runtime.getRuntime().exec("java -jar " + execsPath + adfBuildServiceJar + " "
										+ COUNTRY + " " + LE_BOOK + " " + BUSINESS_DATE + " " + EXTRACTION_FEED_ID);
								proc.waitFor();
								InputStream in = proc.getInputStream();
								InputStream err = proc.getErrorStream();
								byte b[] = new byte[in.available()];
								in.read(b, 0, b.length);
								logWriter("ADF JAR Process Completion!! ", logFileName, EXTRACTION_FEED_ID);
								if (ValidationUtil.isValid(new String(b))) {
									String status = new String(b);
									status = status.substring(status.indexOf(":") + 1);
									logWriter("ADF Process Status[" + status + "] ", logFileName, EXTRACTION_FEED_ID);
									if ("0".equalsIgnoreCase(status.trim())) {
										ExceptionCode pException = new ExceptionCode();
										if (processControlDataPresent) {
											logWriter("Processing File to insert Target script ", logFileName,
													EXTRACTION_FEED_ID);
											pException = processDataFile(processCtrlDataMap);
											if (pException.getErrorCode() == Constants.SUCCESSFUL_OPERATION) {
												logWriter("Target Scripts insertion Success!! ", logFileName,
														EXTRACTION_FEED_ID);
												logWriter("********************************* ", logFileName,
														EXTRACTION_FEED_ID);
												logWriter("Total Records inserted[" + pException.getResponse() + "] ",
														logFileName, EXTRACTION_FEED_ID);
												logWriter("********************************* ", logFileName,
														EXTRACTION_FEED_ID);
												postingStatus = "C";
											} else {
												logWriter("Target Scripts insertion Failed!!["
														+ pException.getErrorMsg() + "] ", logFileName,
														EXTRACTION_FEED_ID);
												postingStatus = "E";
											}
										} else {
											logWriter("Adf process Control Data not available ", logFileName,
													EXTRACTION_FEED_ID);
											logWriter("Ignoring Target insertion Process !! ", logFileName,
													EXTRACTION_FEED_ID);
											postingStatus = "E";
										}
									} else {
										postingStatus = "E";
									}
									logWriter("RA Posting Updated ", logFileName, EXTRACTION_FEED_ID);

									finalServiceProcess(COUNTRY, LE_BOOK, EXTRACTION_FREQUENCY, EXTRACTION_SEQUENCE,
											EXTRACTION_FEED_ID, POSTING_SEQUENCE, BUSINESS_DATE, POSTING_DATE,
											postingStatus, logFileName, true);

									logWriter("Process Completed!!! ", logFileName, EXTRACTION_FEED_ID);
								}
							} catch (InterruptedException e) {
								logWriter("****************Error on RA BUILD Service****************", logFileName,
										EXTRACTION_FEED_ID);
								logWriter("" + e.getMessage(), logFileName, EXTRACTION_FEED_ID);
							}
						} else {
							logWriter("ADF Process Control Data Insertion Failed ", logFileName, EXTRACTION_FEED_ID);
							logWriter("" + exceptionCpdeAdf.getErrorMsg(), logFileName, EXTRACTION_FEED_ID);
							postingStatus = "E";
							finalServiceProcess(COUNTRY, LE_BOOK, EXTRACTION_FREQUENCY, EXTRACTION_SEQUENCE,
									EXTRACTION_FEED_ID, POSTING_SEQUENCE, BUSINESS_DATE, POSTING_DATE, postingStatus,
									logFileName, true);
						}
					}
				}
			}
		} catch (IOException e) {
			//System.out.println("****************Error on RA BUILD Service****************");
			//System.out.println(e.getMessage());
			e.printStackTrace();
		} finally {
			buildServRunThreadCnt--;
		}
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
			//System.out.println(e.getMessage());
		}
	}

	public String getCurrentDateTime() {
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		Date date = new Date();
		return dateFormat.format(date);
	}

	public ExceptionCode processDataFile(HashMap<String, String> adfProcessMap) {
		String adfDataFilePath = commonDao.findVisionVariableValue("ADF_FILE_UPLOAD_PATH");
		String adfDelimiter = commonDao.findVisionVariableValue("ADF_DELIMITER");
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			String filePattern = adfProcessMap.get("FILE_PATTERN") + ".dat";
			String targetScriptType = adfProcessMap.get("TARGET_SCRIPT_TYPE");
			String targetServerScript = adfProcessMap.get("TARGET_SERVER_SCRIPTS");
			String dataFile = "" + adfDataFilePath + filePattern;
			String moveFileToDbServ = commonDao.findVisionVariableValue("RA_ADF_MVFILE");
			if ("Y".equalsIgnoreCase(moveFileToDbServ)) {
				if ("true".equalsIgnoreCase(ftpSecuredFtp)) {
					sftpConnect(dataFile, adfDataFilePath, filePattern);
				} else {
					ftpConnect(dataFile, adfDataFilePath, filePattern);
				}
			}
			if (ValidationUtil.isValid(targetServerScript)) {
				if ("SQL".equalsIgnoreCase(targetScriptType)) {
					exceptionCode = etlServiceDao.sqlBulkInsert(targetServerScript, adfDelimiter, dataFile);
				} else if ("SQLLOADER".equalsIgnoreCase(targetScriptType)) {
					exceptionCode = etlServiceDao.sqlLoader(targetServerScript, adfDelimiter, adfDataFilePath,
							filePattern);
				}
			} else {
				exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
				exceptionCode.setErrorMsg("Targer Script not Available to Process !!");
			}
		} catch (Exception e) {
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg(e.getMessage());
		}
		return exceptionCode;
	}

	public void finalServiceProcess(String COUNTRY, String LE_BOOK, String EXTRACTION_FREQUENCY,
			String EXTRACTION_SEQUENCE, String EXTRACTION_FEED_ID, String POSTING_SEQUENCE, String BUSINESS_DATE,
			String POSTING_DATE, String postingStatus, String logFileName, Boolean isCompleted) {
		try {

			etlServiceDao.doUpdatePostings(COUNTRY, LE_BOOK, EXTRACTION_FREQUENCY, EXTRACTION_SEQUENCE,
					EXTRACTION_FEED_ID, POSTING_SEQUENCE, BUSINESS_DATE, POSTING_DATE, postingStatus);

			etlServiceDao.doUpdatePostingsHistory(COUNTRY, LE_BOOK, EXTRACTION_FREQUENCY, EXTRACTION_SEQUENCE,
					EXTRACTION_FEED_ID, POSTING_SEQUENCE, BUSINESS_DATE, POSTING_DATE, postingStatus);

			if (isCompleted) {
				etlServiceDao.updateLogFileName(COUNTRY, LE_BOOK, EXTRACTION_FREQUENCY, EXTRACTION_SEQUENCE,
						EXTRACTION_FEED_ID, POSTING_SEQUENCE, BUSINESS_DATE, POSTING_DATE, logFileName);

				etlServiceDao.doDeletePostings(COUNTRY, LE_BOOK, EXTRACTION_FREQUENCY, EXTRACTION_SEQUENCE,
						EXTRACTION_FEED_ID, POSTING_SEQUENCE, BUSINESS_DATE, POSTING_DATE, postingStatus);
				etlServiceDao.deleteProcessControlDataExists(COUNTRY, LE_BOOK, EXTRACTION_FEED_ID, BUSINESS_DATE);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void ftpConnect(String dataFileLoc, String remotePath, String fileName) throws InterruptedException {
		// remotePath = "\\ADF_Upload_Dir";
		String server = ftpHost;
		int port = 21;
		String user = ftpUserName;
		String pass = ftpPassword;
		FTPClient ftpClient = new FTPClient();
		try {
			InputStream in = new FileInputStream(dataFileLoc);
			ftpClient.connect(server, port);
			ftpClient.sendCommand(FTPCmd.USER, user);
			ftpClient.sendCommand(FTPCmd.PASS, pass);
			ftpClient.sendCommand(FTPCmd.CWD, remotePath);
			ftpClient.storeFile(fileName, in);
			in.close(); // close the io streams
			ftpClient.abort();
		} catch (IOException ex) {
			System.err.println(ex);
		}
	}

	public void sftpConnect(String dataFileLoc, String remotePath, String fileName) {
		try {
			InputStream in = new FileInputStream("" + dataFileLoc);
			JSch jsch = new JSch();
			Session session = jsch.getSession(ftpUserName, ftpHost);
			{
				session.setPassword(ftpPassword);
			}
			// session.setConfig("StrictHostKeyChecking", "no");
			session.connect();
			Channel channel = session.openChannel("sftp");
			channel.connect();
			ChannelSftp sftpChannel = (ChannelSftp) channel;
			sftpChannel.cd(remotePath);
			sftpChannel.put(in, fileName);
			sftpChannel.exit();
			session.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
