package com.vision.wb;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.nio.channels.FileChannel;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPConnectionClosedException;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.poi.poifs.filesystem.FileMagic;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.jasypt.encryption.StringEncryptor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.multipart.MultipartFile;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.jcraft.jsch.UserInfo;
import com.vision.authentication.CustomContextHolder;
import com.vision.dao.AbstractDao;
import com.vision.dao.CommonDao;
import com.vision.dao.VisionUploadDao;
import com.vision.download.ExportXlsServlet;
import com.vision.exception.ExceptionCode;
import com.vision.exception.RuntimeCustomException;
import com.vision.util.CommonUtils;
import com.vision.util.Constants;
import com.vision.util.DeepCopy;
import com.vision.util.ValidationUtil;
import com.vision.vb.FileInfoVb;
import com.vision.vb.LoadXlVb;
import com.vision.vb.VisionUploadVb;

import edu.emory.mathcs.backport.java.util.Arrays;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class VisionUploadWb extends AbstractDynaWorkerBean<VisionUploadVb> implements ApplicationContextAware {

	@Autowired
	private VisionUploadDao visionUploadDao;

	@Autowired
	private CommonDao commonDao;

	@Autowired
	private ExportXlsServlet exportXlsServlet;

	@Value("${ftp.hostName}")
	private String defaultHostName;

	@Value("${ftp.userName}")
	private String defaultUserName;

	@Value("${ftp.password}")
	private String defaultPassword;

	@Value("${ftp.xlUploadhostName}")
	private String defaultXlUploadhostName;

	@Value("${ftp.xlUploaduserName}")
	private String defaultXlUploaduserName;

	@Value("${ftp.xlUploadpassword}")
	private String defaultXlUploadpassword;

	private String hostName;
	private String userName;
	private String password;
	private String xlUploadhostName;
	private String xlUploaduserName;
	private String xlUploadpassword;

	@Value("${ftp.knownHostsFileName}")
	private String knownHostsFileName;

	private String cbDir;
	private int blockSize;
	private String uploadDir;
	private String uploadDirExl;
	private String downloadDir;
	private String buildLogsDir;
	private String timezoneId;
	private String dateFormate;
	private String serverType;
	private String scriptDir;
	private String feedLogsDir;
	private int fileType;
	private char prompt;
	private boolean securedFtp = true;
	private String fileExtension;
	private String connectorUploadDir;
	private String connectorADUploadDir;
	private int uploadFileChkIntervel = 30;
	protected int retVal = 0;
	private static final String SERVICE_NAME = "Vision Upload";
	private static final int DIR_TYPE_DOWNLOAD = 1;
	private WebApplicationContext webApplicationContext = null;

	@Autowired
	@Qualifier("jasyptStringEncryptor")
	private StringEncryptor jasyptEncryptor;

	public VisionUploadDao getVisionUploadDao() {
		return visionUploadDao;
	}

	public void setVisionUploadDao(VisionUploadDao visionUploadDao) {
		this.visionUploadDao = visionUploadDao;
	}

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getConnectorUploadDir() {
		return connectorUploadDir;
	}

	public void setConnectorUploadDir(String connectorUploadDir) {
		this.connectorUploadDir = connectorUploadDir;
	}

	public String getConnectorADUploadDir() {
		return connectorADUploadDir;
	}

	public void setConnectorADUploadDir(String connectorADUploadDir) {
		this.connectorADUploadDir = connectorADUploadDir;
	}

	public String getUploadDir() {
		return uploadDir;
	}

	public void setUploadDir(String uploadDir) {
		this.uploadDir = uploadDir;
	}

	public String getDownloadDir() {
		return downloadDir;
	}

	public void setDownloadDir(String downloadDir) {
		this.downloadDir = downloadDir;
	}

	public int getBlockSize() {
		return blockSize;
	}

	public void setBlockSize(int blockSize) {
		this.blockSize = blockSize;
	}

	public String getTimezoneId() {
		return timezoneId;
	}

	public void setTimezoneId(String timezoneId) {
		this.timezoneId = timezoneId;
	}

	public String getDateFormate() {
		return dateFormate;
	}

	public void setDateFormate(String dateFormate) {
		this.dateFormate = dateFormate;
	}

	public String getServerType() {
		return serverType;
	}

	public void setServerType(String serverType) {
		this.serverType = serverType;
	}

	public String getBuildLogsDir() {
		return buildLogsDir;
	}

	public void setBuildLogsDir(String buildLogsDir) {
		this.buildLogsDir = buildLogsDir;
	}

	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.webApplicationContext = (WebApplicationContext) applicationContext;
	}

	public String getScriptDir() {
		return scriptDir;
	}

	public void setScriptDir(String scriptDir) {
		this.scriptDir = scriptDir;
	}

	public boolean isSecuredFtp() {
		return securedFtp;
	}

	public void setSecuredFtp(boolean securedFtp) {
		this.securedFtp = securedFtp;
	}

	public WebApplicationContext getWebApplicationContext() {
		return webApplicationContext;
	}

	public void setWebApplicationContext(WebApplicationContext webApplicationContext) {
		this.webApplicationContext = webApplicationContext;
	}

	public String getKnownHostsFileName() {
		return knownHostsFileName;
	}

	public void setKnownHostsFileName(String knownHostsFileName) {
		this.knownHostsFileName = knownHostsFileName;
	}

	public char getPrompt() {
		return prompt;
	}

	public void setPrompt(char prompt) {
		this.prompt = prompt;
	}

	public int getFileType() {
		return fileType;
	}

	public void setFileType(int fileType) {
		this.fileType = fileType;
	}

	public String getFileExtension() {
		return fileExtension;
	}

	public void setFileExtension(String fileExtension) {
		this.fileExtension = fileExtension;
	}

	public String getXlUploadhostName() {
		return xlUploadhostName;
	}

	public void setXlUploadhostName(String xlUploadhostName) {
		this.xlUploadhostName = xlUploadhostName;
	}

	public String getXlUploaduserName() {
		return xlUploaduserName;
	}

	public void setXlUploaduserName(String xlUploaduserName) {
		this.xlUploaduserName = xlUploaduserName;
	}

	public String getXlUploadpassword() {
		return xlUploadpassword;
	}

	public void setXlUploadpassword(String xlUploadpassword) {
		this.xlUploadpassword = xlUploadpassword;
	}

	public String getUploadDirExl() {
		return uploadDirExl;
	}

	public void setUploadDirExl(String uploadDirExl) {
		this.uploadDirExl = uploadDirExl;
	}

	public class MyUserInfo implements UserInfo {
		public String getPassword() {
			return password;
		}

		public boolean promptYesNo(String str) {
			return false;
		}

		public String getPassphrase() {
			return null;
		}

		public boolean promptPassphrase(String message) {
			return true;
		}

		public boolean promptPassword(String message) {
			return false;
		}

		public void showMessage(String message) {
			return;
		}
	}

	public ExceptionCode doUpload(String fileName, byte[] data) {
		ExceptionCode exceptionCode = null;
		try {
			setUploadDownloadDirFromDB();
			fileName = fileName.toUpperCase(); // Changes done Done by Deepak
												// for Bank One
			String fileExtension = "xlsx";
			String uploadDir = "";
			uploadDir = getUploadDir();
			fileName = fileName.toUpperCase();
			fileName = fileName.substring(0, fileName.lastIndexOf('.')) + "_"
					+ CustomContextHolder.getContext().getVisionId() + "." + fileExtension + "";
			// System.out.println("fileName:" + fileName);
			if ('/' != uploadDir.charAt(uploadDir.length() - 1)) {
				fileName = uploadDir + "\\" + fileName;
				// System.out.println("******fileName:" + fileName);
			} else {
				fileName = uploadDir + fileName;
				// System.out.println("#####fileName:" + fileName);
			}
			File lFile = new File(fileName);
			if (lFile.exists()) {
				lFile.delete();
			}
			lFile.createNewFile();
			// System.out.println("fileName :" + fileName);
			FileOutputStream fout = new FileOutputStream(fileName);
			fout.write(data);
			fout.close();
		} catch (FileNotFoundException e) {
			exceptionCode = CommonUtils.getResultObject(SERVICE_NAME, Constants.FILE_UPLOAD_REMOTE_ERROR, "Upload", "");
			;
			exceptionCode.setResponse(fileName);
			throw new RuntimeCustomException(exceptionCode);
		} catch (IOException e) {
			exceptionCode = CommonUtils.getResultObject(SERVICE_NAME, Constants.FILE_UPLOAD_REMOTE_ERROR, "Upload", "");
			;
			exceptionCode.setResponse(fileName);
			throw new RuntimeCustomException(exceptionCode);
		} catch (Exception e) {
			exceptionCode = CommonUtils.getResultObject(SERVICE_NAME, Constants.FILE_UPLOAD_REMOTE_ERROR, "Upload", "");
			;
			exceptionCode.setResponse(fileName);
			throw new RuntimeCustomException(exceptionCode);
		}
		exceptionCode = CommonUtils.getResultObject(SERVICE_NAME, Constants.SUCCESSFUL_OPERATION, "Upload", "");
		exceptionCode.setOtherInfo(fileName);
		return exceptionCode;
	}

	public ExceptionCode getQueryResults(VisionUploadVb vObject) {
		setVerifReqDeleteType(vObject);
		ExceptionCode exceptionCode = new ExceptionCode();
		exceptionCode = doValidate(vObject);
		if (exceptionCode.getErrorCode() == Constants.ERRONEOUS_OPERATION) {
			return exceptionCode;
		}
		List<VisionUploadVb> collTemp = getScreenDao().getQueryResults(vObject, 1);
		doSetDesctiptionsAfterQuery(collTemp);
		if (collTemp != null && !collTemp.isEmpty())
			exceptionCode = CommonUtils.getResultObject(SERVICE_NAME, Constants.SUCCESSFUL_OPERATION, "Query", "");
		else
			exceptionCode = CommonUtils.getResultObject(getScreenDao().getServiceDesc(), Constants.NO_RECORDS_FOUND,
					"Query", "");
		exceptionCode.setOtherInfo(vObject);
		exceptionCode.setResponse(collTemp);
		return exceptionCode;
	}

	public ArrayList getPageLoadValues() {
		List collTemp = null;
		ArrayList<Object> arrListLocal = new ArrayList<Object>();
		try {
			collTemp = getNumSubTabDao().findActiveNumSubTabsByNumTab(10);
			arrListLocal.add(collTemp);
			String refresh = commonDao.findVisionVariableValue("RA_UPLOAD_AUTO_RFERESH");
			arrListLocal.add(refresh);
			String refreshTime = commonDao.findVisionVariableValue("RA_UPLOAD_AUTO_RFERESH_TIME");
			arrListLocal.add(refreshTime);
			return arrListLocal;
		} catch (Exception ex) {
			ex.printStackTrace();
			// logger.error("Exception in getting the Page load values.", ex);
			return null;
		}
	}

	public ExceptionCode listFilesFromFtpServer(int dirType, String orderBy, VisionUploadVb visionUploadVb) {
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			exceptionCode = doValidate(visionUploadVb);
			if (exceptionCode.getErrorCode() == Constants.ERRONEOUS_OPERATION) {
				return exceptionCode;
			}
			TreeMap<String, Set<File>> mapFiles = new TreeMap<>();
			String formatedDate = "";
			setUploadDownloadDirFromDB();
			File directory = new File(downloadDir);
			if (directory.exists()) {
				File[] listFiles = directory.listFiles();
//				List fileLst = new ArrayList<>();
				Arrays.sort(listFiles, Comparator.comparingLong(File::lastModified).reversed());
				for (File listFile : listFiles) {
					Set fileLst = new LinkedHashSet();
					String fileName = listFile.getName();
					if (!listFile.getName().endsWith(".err"))
						continue;

					if ("DATE".equalsIgnoreCase(orderBy)) {
						if (fileName.contains("VISION_UPLOAD"))
							continue;
						fileName = fileName.substring(0, fileName.lastIndexOf(".err"));
						String date = "";
						String fileNameStr = "";
						for (File file : listFiles) {
							if (file.getName().contains("VISION_UPLOAD"))
								continue;
							date = fileName.substring(fileName.length() - 10);
							fileNameStr = file.getName();

							if (fileNameStr.contains(date)) {
								fileLst.add(fileNameStr);
							}
						}
						formatedDate = LocalDate.parse(date).format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));

						if (mapFiles.get(date) == null)
							mapFiles.put(date, fileLst);

					} else if ("TABLE".equalsIgnoreCase(orderBy)) {
						Map<String, List<File>> mapFiles1 = new TreeMap<>();
						String tableName = "";
						int lastIndex;
						int secondLastIndex;
						int thirdLastIndex;
						String nameFile = "";
						nameFile = fileName;
						fileName = fileName.substring(0, fileName.lastIndexOf(".err"));
						if (fileName.equalsIgnoreCase("VISION_UPLOAD")) {
							tableName = fileName;

							fileLst.add(nameFile);
							// mapFiles.put(tableName, fileLst);
						} else {
							lastIndex = fileName.lastIndexOf("_");
							secondLastIndex = fileName.lastIndexOf("_", lastIndex - 1);
							thirdLastIndex = fileName.lastIndexOf("_", secondLastIndex - 1);
							tableName = fileName.substring(0, thirdLastIndex);
							for (File file : listFiles) {
								fileName = file.getName().substring(0, file.getName().lastIndexOf(".err"));
								if (fileName.equalsIgnoreCase("VISION_UPLOAD"))
									continue;
								nameFile = file.getName();
								lastIndex = file.getName().lastIndexOf("_");
								secondLastIndex = file.getName().lastIndexOf("_", lastIndex - 1);
								thirdLastIndex = file.getName().lastIndexOf("_", secondLastIndex - 1);
								String nameOfFile = file.getName().substring(0, thirdLastIndex);
								if (nameOfFile.equalsIgnoreCase(tableName)) {
									fileLst.add(nameFile);
								}

							}
							// mapFiles.put(tableName, fileLst);
						}
						if (mapFiles.get(tableName) == null)
							mapFiles.put(tableName, fileLst);
					}
				}
				if ("DATE".equalsIgnoreCase(orderBy)) {
					Map<Object, Object> sortedMap = mapFiles.entrySet().stream()
							.sorted(Map.Entry.comparingByKey(Comparator.reverseOrder()))
							.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
									(oldValue, newValue) -> oldValue, LinkedHashMap::new));

					LinkedHashMap<String, Set<File>> mapFiles2 = new LinkedHashMap<>();
					int ctr = 1;
					for (Entry<Object, Object> mapFiles1 : sortedMap.entrySet()) {
						formatedDate = LocalDate.parse((CharSequence) mapFiles1.getKey())
								.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
						if (ctr == 1) {
							Set set1 = (Set) mapFiles1.getValue();
							set1.add("VISION_UPLOAD.err");
							mapFiles2.put(formatedDate, (Set<File>) set1);
						} else {
							mapFiles2.put(formatedDate, (Set<File>) mapFiles1.getValue());
						}
						ctr++;
					}

					exceptionCode.setResponse(mapFiles2);
				} else {
					exceptionCode.setResponse(mapFiles);
				}
			}
			exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
			exceptionCode.setErrorMsg("");
		} catch (Exception e) {
			e.printStackTrace();
			exceptionCode = CommonUtils.getResultObject(SERVICE_NAME, Constants.FILE_UPLOAD_REMOTE_ERROR, "Download",
					"");
			throw new RuntimeCustomException(exceptionCode);
		}
		return exceptionCode;
	}

	public ExceptionCode listTableUploadLogs(int dirType, String orderBy) {
		ExceptionCode exceptionCode = new ExceptionCode();
		List<FileInfoVb> lFileList = new ArrayList<FileInfoVb>();
		File directoryPath = null;
		try {
			setUploadDownloadDirFromDB();
			// System.out.println("dirType : " + dirType);
			if (dirType == DIR_TYPE_DOWNLOAD) {
				// System.out.println("downloadDir " + downloadDir);
				directoryPath = new File("" + downloadDir);
			} else if (dirType == 4) {
				// System.out.println("feedLogsDir " + feedLogsDir);
				directoryPath = new File("" + feedLogsDir);
			} else {
				// System.out.println("buildLogsDir " + buildLogsDir);
				directoryPath = new File("" + buildLogsDir);
			}
			File contents[] = directoryPath.listFiles();

			if (contents != null && contents.length > 0) {
				// System.out.println(contents.length);
				for (File fileName : contents) {
					FileInfoVb fileInfoVb = new FileInfoVb();
					fileInfoVb.setName(fileName.getName());
					fileInfoVb.setSize(formatFileSize(fileName.length()));
					// System.out.println("BEFORE Format : " + fileName.lastModified());
					SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
					// System.out.println("After Format : " + sdf.format(fileName.lastModified()));
					fileInfoVb.setDate(sdf.format(fileName.lastModified()));
					lFileList.add(fileInfoVb);

					/*
					 * if ((fileInfoVb.getName().startsWith("VISION_UPLOAD") &&
					 * StringUtils.countOccurrencesOf(fileInfoVb.getName(), "_") > 0)) { Calendar
					 * cal = new GregorianCalendar(); int month = cal.get(Calendar.MONTH) + 1; int
					 * year = cal.get(Calendar.YEAR); int day = cal.get(Calendar.DAY_OF_MONTH);
					 * fileInfoVb.setDate(CommonUtils.getFixedLength(String. valueOf(day), "0", 2) +
					 * "-" + CommonUtils.getFixedLength(String.valueOf(month), "0", 2) + "-" +
					 * year); lFileList.add(fileInfoVb); } else if
					 * (StringUtils.countOccurrencesOf(fileInfoVb.getName(), "-") > 0) {
					 * lFileList.add(fileInfoVb);
					 * 
					 * }
					 */

				}
			} else {
				exceptionCode = CommonUtils.getResultObject(SERVICE_NAME, Constants.FILE_UPLOAD_REMOTE_ERROR,
						"Download", "");
				// throw new RuntimeCustomException(exceptionCode);
			}

			exceptionCode = CommonUtils.getResultObject(SERVICE_NAME, Constants.SUCCESSFUL_OPERATION, "Download", "");
			exceptionCode.setResponse(createParentChildRelations(lFileList, orderBy, dirType));
		} catch (FileNotFoundException e) {
			exceptionCode = CommonUtils.getResultObject(SERVICE_NAME, Constants.FILE_UPLOAD_REMOTE_ERROR, "Download",
					"");
			// throw new RuntimeCustomException(exceptionCode);
		} catch (Exception e) {
			e.printStackTrace();
			exceptionCode = CommonUtils.getResultObject(SERVICE_NAME, Constants.FILE_UPLOAD_REMOTE_ERROR, "Download",
					"");

			// throw new RuntimeCustomException(exceptionCode);
		}
		return exceptionCode;
	}

	private String formatFileSize(long numSize) {
		String strReturn;
		BigDecimal lSize = BigDecimal.valueOf((numSize)).divide(BigDecimal.valueOf(1024), 1, BigDecimal.ROUND_HALF_UP);
		if (lSize.floatValue() <= 0) {
			lSize = BigDecimal.valueOf(1);
		}
		strReturn = lSize + " KB";
		if (lSize.floatValue() > 1024) {
			lSize = lSize.divide(BigDecimal.valueOf(1024), 1, BigDecimal.ROUND_HALF_UP);
			strReturn = lSize + " MB";
			if (lSize.floatValue() > 1024) {
				lSize = lSize.divide(BigDecimal.valueOf(1024), 1, BigDecimal.ROUND_HALF_UP);
				strReturn = lSize + " GB";
			}
		}
		return strReturn;
	}

	private String formatDate1(FTPFile fileName) {
		String fileName1 = fileName.getName();
		String year = fileName1.substring(fileName1.length() - 14, fileName1.length() - 10);
		String month = fileName1.substring(fileName1.length() - 9, fileName1.length() - 7);
		String day = fileName1.substring(fileName1.length() - 6, fileName1.length() - 4);
		return CommonUtils.getFixedLength(day, "0", 2) + "-" + CommonUtils.getFixedLength(month, "0", 2) + "-" + year;
	}

	private FTPClient getConnection() throws IOException {
		FTPClient ftpClient = new FTPClient();
		FTPClientConfig conf = new FTPClientConfig(getServerType());
		conf.setServerTimeZoneId(getTimezoneId());
		ftpClient.configure(conf);
		return ftpClient;
	}

	public void setUploadDownloadDirFromDB() {
		String uploadLogFilePath = System.getenv("VISION_XLUPL_LOG_FILE_PATH");
		if (uploadLogFilePath != null && !uploadLogFilePath.isEmpty()) {
			downloadDir = uploadLogFilePath;
		}
		String uploadDataFilePath = System.getenv("VISION_XLUPL_DATA_FILE_PATH");
		if (uploadDataFilePath != null && !uploadDataFilePath.isEmpty()) {
			uploadDir = uploadDataFilePath;
		}
	}

	public ExceptionCode insertRecord(ExceptionCode pRequestCode, List<VisionUploadVb> vObjects) {
		ExceptionCode exceptionCode = null;
		DeepCopy<VisionUploadVb> deepCopy = new DeepCopy<VisionUploadVb>();
		List<VisionUploadVb> clonedObject = null;
		VisionUploadVb vObject = null;
		try {
			exceptionCode = doValidate(vObjects.get(0));
			if (exceptionCode.getErrorCode() == Constants.ERRONEOUS_OPERATION) {
				return exceptionCode;
			}
			setAtNtValues(vObjects);
			vObject = (VisionUploadVb) pRequestCode.getOtherInfo();
			setVerifReqDeleteType(vObject);
			clonedObject = deepCopy.copyCollection(vObjects);
			doFormateData(vObjects);
			exceptionCode = visionUploadDao.doInsertRecord(vObjects);
			// exceptionCode = doValidate(vObject, vObjects);
			if (exceptionCode != null && exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION) {
				exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
				return exceptionCode;
			} else {
				exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
				exceptionCode.setErrorMsg("Vision Upload Add Successful");
			}
//			exceptionCode = getScreenDao().doInsertRecord(vObjects);
			exceptionCode.setOtherInfo(vObject);
			exceptionCode.setResponse(vObjects);
			return exceptionCode;
		} catch (RuntimeCustomException rex) {
			// logger.error("Insert Exception " + rex.getCode().getErrorMsg());
			exceptionCode = rex.getCode();
			exceptionCode.setResponse(clonedObject);
			exceptionCode.setOtherInfo(vObject);
			return exceptionCode;
		}
	}

	private BufferedInputStream downloadFilesFromFTP(String pFileNames, int dirType) {
		BufferedInputStream bufferedInputStream = null;
		TelnetConnection telnetConnection = null;
		String[] fileNames = pFileNames.split(" @- ");
		setUploadDownloadDirFromDB();
		FTPClient ftpClient = null;
		try {
			ftpClient = getConnection();
			ftpClient.connect(getHostName());
			boolean response = ftpClient.login(getUserName(), getPassword());
			if (!response) {
				ftpClient.disconnect();
				throw new FTPConnectionClosedException("Unable to connect to Remote Computer");
			}
			ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
			if (dirType == DIR_TYPE_DOWNLOAD)
				response = ftpClient.changeWorkingDirectory(downloadDir);
			else
				response = ftpClient.changeWorkingDirectory(buildLogsDir);
			if (!response) {
				ftpClient.disconnect();
				throw new FTPConnectionClosedException("Unable to connect to Remote Computer");
			}
			if (fileNames.length == 1) {
				bufferedInputStream = new BufferedInputStream(ftpClient.retrieveFileStream(fileNames[0]));
			} else if (fileNames.length > 1) {
				telnetConnection = new TelnetConnection(hostName, userName, password, prompt);
				telnetConnection.connect();
				if (dirType == DIR_TYPE_DOWNLOAD)
					telnetConnection.sendCommand("cd " + downloadDir);
				else
					telnetConnection.sendCommand("cd " + buildLogsDir);
				// for(int i=0;i<fileNames.length;i++){
				for (int i = 0; i < fileNames.length; i++) {
					telnetConnection.sendCommand("echo " + fileNames[i] + " >> example.txt");
				}
				telnetConnection.sendCommand("tar cvf logs.tar `cat example.txt`");
				telnetConnection.sendCommand("rm example.txt");
				ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
				if (dirType == DIR_TYPE_DOWNLOAD)
					response = ftpClient.changeWorkingDirectory(downloadDir);
				else
					response = ftpClient.changeWorkingDirectory(buildLogsDir);
				if (!response) {
					ftpClient.disconnect();
					throw new FTPConnectionClosedException();
				}
				bufferedInputStream = new BufferedInputStream(ftpClient.retrieveFileStream("logs.tar"));
			} else {
				throw new FileNotFoundException("File not found on the Server.");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (ftpClient != null && ftpClient.isConnected()) {
				try {
					ftpClient.disconnect();
				} catch (IOException e) {
					e.printStackTrace();
				}
				ftpClient = null;
			}
			if (telnetConnection != null && telnetConnection.isConnected()) {
				telnetConnection.disconnect();
			}
		}
		return bufferedInputStream;
	}

	public ExceptionCode downloadFilesFromSFTP(String pFileNames, int dirType) {
		ExceptionCode exceptionCode = new ExceptionCode();
		BufferedInputStream bufferedInputStream = null;
		ByteArrayOutputStream out = null;
		OutputStream outputStream = null;
		String[] fileNames = pFileNames.split(" @- ");
		setUploadDownloadDirFromDB();
		Session session = null;
		try {
			String filePath = System.getProperty("java.io.tmpdir");
			if (!ValidationUtil.isValid(filePath)) {
				filePath = System.getenv("TMP");
			}
			if (ValidationUtil.isValid(filePath)) {
				filePath = filePath + File.separator;
			}
			// System.out.print("File Path:" + filePath);

			JSch jsch = new JSch();
			jsch.setKnownHosts(getKnownHostsFileName());
			session = jsch.getSession(getUserName(), getHostName());
			{ // "interactive" version // can selectively update specified known_hosts file
				// need to implement UserInfo interface
				// MyUserInfo is a swing implementation provided in
				// examples/Sftp.java in the JSch dist
				UserInfo ui = new MyUserInfo();
				session.setUserInfo(ui); // OR non-interactive version. Relies in host key being in known-hosts file
				session.setPassword(getPassword());
			}
			Properties config = new Properties();
			config.put("StrictHostKeyChecking", "no");
			session.setConfig(config);
			session.connect();
			Channel channel = session.openChannel("sftp");
			channel.connect();
			ChannelSftp sftpChannel = (ChannelSftp) channel;
			if (dirType == DIR_TYPE_DOWNLOAD)
				sftpChannel.cd(downloadDir);
			else
				sftpChannel.cd(buildLogsDir);
			if (fileNames.length == 1) {

				InputStream ins = sftpChannel.get(fileNames[0]);
				bufferedInputStream = new BufferedInputStream(new ByteArrayInputStream(ins.readAllBytes()));
				File lFile = new File(filePath + fileNames[0]);
				FileOutputStream fout = new FileOutputStream(lFile);
				int bit = 4096;
				while ((bit) >= 0) {
					bit = bufferedInputStream.read();
					fout.write(bit);
				}
				fout.close();

			} else if (fileNames.length > 1) {
				FileOutputStream fos = new FileOutputStream(filePath + File.separator + "logs.zip");
				ZipOutputStream zipOut = new ZipOutputStream(new BufferedOutputStream(fos));

//				FileOutputStream fos = new FileOutputStream(filePath + File.separator + "logs.zip");
//				ZipOutputStream zipOut = new ZipOutputStream(new BufferedOutputStream(fos));

				// for(int i=0;i<fileNames.length;i++){
				for (int i = 0; i < fileNames.length; i++) {
					InputStream ins = sftpChannel.get(fileNames[i]);
					bufferedInputStream = new BufferedInputStream(new ByteArrayInputStream(ins.readAllBytes()));
					File lFile = new File(filePath + fileNames[i]);
					FileOutputStream fout = new FileOutputStream(lFile);
					int bit = 4096;
					while ((bit) >= 0) {
						bit = bufferedInputStream.read();
						fout.write(bit);
					}
					fout.close();
					FileInputStream fis = new FileInputStream(lFile);
					ZipEntry ze = new ZipEntry(fileNames[i]);
					zipOut.putNextEntry(ze);
					byte[] tmp = new byte[4 * 1024];
					int size = 0;
					while ((size = fis.read(tmp)) != -1) {
						zipOut.write(tmp, 0, size);
					}
					fis.close();
					if (lFile.exists())
						lFile.delete();
				}
				zipOut.close();
				exceptionCode.setResponse(filePath);
				exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
				exceptionCode.setErrorMsg("Log File Downloaded");

			} else {
				throw new FileNotFoundException("File not found on the Server.");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session != null && session.isConnected()) {
				session.disconnect();
			}
		}
		return exceptionCode;
	}

	protected ExceptionCode doValidate(VisionUploadVb pObject, List<VisionUploadVb> vObjects) {
		ExceptionCode exceptionCode = null;
		long currentUser = CustomContextHolder.getContext().getVisionId();
		FTPClient ftpClient;
		try {
			if (isSecuredFtp()) {
				return doValidateSFtp(pObject, vObjects);
			}
			ftpClient = getConnection();
			ftpClient.connect(getHostName());
			boolean response = ftpClient.login(getUserName(), getPassword());
			if (!response) {
				ftpClient.disconnect();
				exceptionCode = CommonUtils.getResultObject(SERVICE_NAME, Constants.FILE_UPLOAD_REMOTE_ERROR, "Add",
						"");
				throw new RuntimeCustomException(exceptionCode);
			}
			ftpClient.setFileType(fileType);
			response = ftpClient.changeWorkingDirectory(uploadDir);
			if (!response) {
				ftpClient.disconnect();
				exceptionCode = CommonUtils.getResultObject(SERVICE_NAME, Constants.FILE_UPLOAD_REMOTE_ERROR, "Add",
						"");
				throw new RuntimeCustomException(exceptionCode);
			}
			long maxIntervel = 0;
			for (VisionUploadVb lVUploadVb : vObjects) {
				if (lVUploadVb.isChecked()) {
					setVerifReqDeleteType(lVUploadVb);
					int countOfUplTables = getVisionUploadDao().getCountOfUploadTables(lVUploadVb);
					lVUploadVb.setMaker(currentUser);
					if (countOfUplTables <= 0) {
						String strErrorDesc = "No sufficient privileges to upload for the Table["
								+ lVUploadVb.getTableName() + "]";
						exceptionCode = CommonUtils.getResultObject(SERVICE_NAME, Constants.WE_HAVE_ERROR_DESCRIPTION,
								"Insert", strErrorDesc);
						throw new RuntimeCustomException(exceptionCode);
					}
					FTPFile file = isFileExists(lVUploadVb.getFileName().toUpperCase() + "_" + currentUser + ".txt",
							ftpClient);
					if (file == null) {
						String strErrorDesc = lVUploadVb.getFileName()
								+ ".txt does not exists. Please upload the file first.";
						exceptionCode = CommonUtils.getResultObject(SERVICE_NAME, Constants.WE_HAVE_ERROR_DESCRIPTION,
								"Insert", strErrorDesc);
						throw new RuntimeCustomException(exceptionCode);
					}
					maxIntervel = Math.max(
							(((System.currentTimeMillis() / 1000) - file.getTimestamp().getTimeInMillis()) / 60),
							maxIntervel);
				}
			}
			if (maxIntervel >= uploadFileChkIntervel && pObject.getCheckUploadInterval() == false) {
				String strErrorDesc = "Upload file(s) is more than " + uploadFileChkIntervel
						+ " mins old. Do you want to continue with upload?";
				exceptionCode = CommonUtils.getResultObject(SERVICE_NAME, Constants.WE_HAVE_ERROR_DESCRIPTION, "Insert",
						strErrorDesc);
				throw new RuntimeCustomException(exceptionCode);
			}
			ftpClient.disconnect();
		} catch (IOException e) {
			e.printStackTrace();
			exceptionCode = CommonUtils.getResultObject(SERVICE_NAME, Constants.FILE_UPLOAD_REMOTE_ERROR, "Add", "");
			throw new RuntimeCustomException(exceptionCode);
		}
		return exceptionCode;
	}

	protected ExceptionCode doValidateSFtp(VisionUploadVb pObject, List<VisionUploadVb> vObjects) {
		ExceptionCode exceptionCode = null;
		long currentUser = CustomContextHolder.getContext().getVisionId();
		try {
			JSch jsch = new JSch();
			jsch.setKnownHosts(getKnownHostsFileName());
			Session session = jsch.getSession(getUserName(), getHostName());
			{ // "interactive" version // can selectively update specified known_hosts file
				// need to implement UserInfo interface
				// MyUserInfo is a swing implementation provided in
				// examples/Sftp.java in the JSch dist
				UserInfo ui = new MyUserInfo();
				session.setUserInfo(ui); // OR non-interactive version. Relies in host key being in known-hosts file
				session.setPassword(getPassword());
			}
			session.setConfig("StrictHostKeyChecking", "no");
			session.connect();
			Channel channel = session.openChannel("sftp");
			channel.connect();
			ChannelSftp sftpChannel = (ChannelSftp) channel;
			sftpChannel.cd(uploadDir);
			long maxIntervel = 0;
			for (VisionUploadVb lVUploadVb : vObjects) {
				if (lVUploadVb.isChecked()) {
					setVerifReqDeleteType(lVUploadVb);
					int countOfUplTables = getVisionUploadDao().getCountOfUploadTables(lVUploadVb);
					lVUploadVb.setMaker(currentUser);
					if (countOfUplTables <= 0) {
						String strErrorDesc = "No sufficient privileges to upload for the Table["
								+ lVUploadVb.getTableName() + "]";
						exceptionCode = CommonUtils.getResultObject(SERVICE_NAME, Constants.WE_HAVE_ERROR_DESCRIPTION,
								"Insert", strErrorDesc);
						throw new RuntimeCustomException(exceptionCode);
					}
					ChannelSftp.LsEntry entry = isFileExists(
							lVUploadVb.getFileName().toUpperCase() + "_" + currentUser + ".txt", sftpChannel);
					if (entry == null) {
						String strErrorDesc = lVUploadVb.getFileName()
								+ ".txt does not exists. Please upload the file first.";
						exceptionCode = CommonUtils.getResultObject(SERVICE_NAME, Constants.WE_HAVE_ERROR_DESCRIPTION,
								"Insert", strErrorDesc);
						throw new RuntimeCustomException(exceptionCode);
					}
					maxIntervel = Math.max((((System.currentTimeMillis() / 1000) - entry.getAttrs().getMTime()) / 60),
							maxIntervel);
				}
			}
			if (maxIntervel >= uploadFileChkIntervel && pObject.getCheckUploadInterval() == false) {
				String strErrorDesc = "Upload file(s) is more than " + uploadFileChkIntervel
						+ " mins old. Do you want to continue with upload?";
				exceptionCode = new ExceptionCode();
				exceptionCode.setErrorSevr("W");
				exceptionCode.setErrorCode(Constants.WE_HAVE_ERROR_DESCRIPTION);
				exceptionCode.setErrorMsg(strErrorDesc);
				throw new RuntimeCustomException(exceptionCode);
			}
			sftpChannel.disconnect();
			session.disconnect();
		} catch (SftpException e) {
			e.printStackTrace();
			exceptionCode = CommonUtils.getResultObject(SERVICE_NAME, Constants.FILE_UPLOAD_REMOTE_ERROR, "Add", "");
			;
			throw new RuntimeCustomException(exceptionCode);
		} catch (JSchException e) {
			e.printStackTrace();
			exceptionCode = CommonUtils.getResultObject(SERVICE_NAME, Constants.FILE_UPLOAD_REMOTE_ERROR, "Add", "");
			;
			throw new RuntimeCustomException(exceptionCode);
		}
		return exceptionCode;
	}

	private List<FileInfoVb> createParentChildRelations(List<FileInfoVb> legalTreeList, String orderBy, int dirType)
			throws IOException {
		List<FileInfoVb> lResult = new ArrayList<FileInfoVb>(0);
		Set set = new HashSet<FileInfoVb>();
		// Top Roots are added.
		for (FileInfoVb fileVb : legalTreeList) {
			if ("Date".equalsIgnoreCase(orderBy)) {
				// String date = fileVb.getName().substring(fileVb.getName().length()-14,
				// fileVb.getName().length()-4);
				if (fileVb.getDate() != null && fileVb.getDate() != "") {
					set.add(fileVb.getDate());
				}
			} else {
				String fileNeme = "";
				if (dirType == 2) {
					int cout = StringUtils.countOccurrencesOf(fileVb.getName(), "_");
					if (cout == 0) {
						int cout1 = StringUtils.countOccurrencesOf(fileVb.getName(), "-");
						if (cout1 == 0)
							fileNeme = fileVb.getName().substring(0, fileVb.getName().length() - 4);
						else
							fileNeme = fileVb.getName().substring(0, fileVb.getName().length() - 15);
					} else if (cout > 1)
						fileNeme = fileVb.getName().substring(0, fileVb.getName().length() - 21);
				} else {
					int cout = StringUtils.countOccurrencesOf(fileVb.getName(), "_");
					if (cout == 1) {
						fileNeme = fileVb.getName().substring(0, fileVb.getName().length() - 4);
					} else if (cout > 1) {
						fileNeme = fileVb.getName().substring(0, fileVb.getName().length() - 20);
					}
				}
				if (fileNeme != null && fileNeme.length() > 0) {
					set.add(fileNeme);
				}
			}
		}
		for (Iterator iterator = set.iterator(); iterator.hasNext();) {
			String date = (String) iterator.next();
			FileInfoVb fileVb = new FileInfoVb();
			fileVb.setDescription(date);
			lResult.add(fileVb);
		}
		// For each top node add all child's and to that child's add sub child's
		// recursively.
		for (FileInfoVb legalVb : lResult) {
			addChilds(legalVb, legalTreeList, orderBy, dirType);
		}
		if ("Date".equalsIgnoreCase(orderBy)) {
			final SimpleDateFormat dtF = new SimpleDateFormat("dd-MM-yyyy");
			// set the empty lists to null. this is required for UI to display the leaf
			// nodes properly.
			Collections.sort(lResult, new Comparator<FileInfoVb>() {
				public int compare(FileInfoVb m1, FileInfoVb m2) {
					try {
						return dtF.parse(m1.getDescription()).compareTo(dtF.parse(m2.getDescription()));
					} catch (ParseException e) {
						return 0;
					}
				}
			});
			Collections.reverse(lResult);
		} else {
			Collections.sort(lResult, new Comparator<FileInfoVb>() {
				public int compare(FileInfoVb m1, FileInfoVb m2) {
					return m1.getDescription().compareTo(m2.getDescription());
				}
			});
		}
		return lResult;
	}

	public void addChilds(FileInfoVb vObject, List<FileInfoVb> legalTreeListCopy, String orderBy, int dirType) {
		for (FileInfoVb fileTreeVb : legalTreeListCopy) {
			if ("Date".equalsIgnoreCase(orderBy)) {
				// String date =
				// fileTreeVb.getName().substring(fileTreeVb.getName().length()-14,
				// fileTreeVb.getName().length()-4);
				if (vObject.getDescription().equalsIgnoreCase(fileTreeVb.getDate())) {
					if (vObject.getChildren() == null) {
						vObject.setChildren(new ArrayList<FileInfoVb>(0));
					}
					fileTreeVb.setDescription(fileTreeVb.getName());
					vObject.getChildren().add(fileTreeVb);
				}
			} else {
				String fileNeme = "";
				if (dirType == 2) {
					int cout = StringUtils.countOccurrencesOf(fileTreeVb.getName(), "_");
					if (cout == 0) {
						int cout1 = StringUtils.countOccurrencesOf(fileTreeVb.getName(), "-");
						if (cout1 == 0)
							fileNeme = fileTreeVb.getName().substring(0, fileTreeVb.getName().length() - 4);
						else
							fileNeme = fileTreeVb.getName().substring(0, fileTreeVb.getName().length() - 15);
					} else if (cout > 1)
						fileNeme = fileTreeVb.getName().substring(0, fileTreeVb.getName().length() - 21);
				} else {
					int cout = StringUtils.countOccurrencesOf(fileTreeVb.getName(), "_");
					if (cout == 1) {
						fileNeme = fileTreeVb.getName().substring(0, fileTreeVb.getName().length() - 4);
					} else if (cout > 1) {
						fileNeme = fileTreeVb.getName().substring(0, fileTreeVb.getName().length() - 20);
					}
				}
				if (vObject.getDescription().equalsIgnoreCase(fileNeme)) {
					if (vObject.getChildren() == null) {
						vObject.setChildren(new ArrayList<FileInfoVb>(0));
					}
					fileTreeVb.setDescription(fileTreeVb.getName());
					vObject.getChildren().add(fileTreeVb);
				}
			}
		}
	}

	public ExceptionCode fileDownload(int dirType, String fileNames, String strBuildNumber, String strBuild,
			String fileExtension, VisionUploadVb vObject) {
		setUploadDownloadDirFromDB();
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			try {
				exceptionCode = doValidate(vObject);
				if (exceptionCode.getErrorCode() == Constants.ERRONEOUS_OPERATION) {
					return exceptionCode;
				}
				String filePath = System.getProperty("java.io.tmpdir");
				if (!ValidationUtil.isValid(filePath)) {
					filePath = System.getenv("TMP");
				}
				if (ValidationUtil.isValid(filePath)) {
					filePath = filePath + File.separator;
				}
				BufferedInputStream in = null;
				File file = null;
				FileOutputStream fos = null;
				String fName = "";
				int number = 0;
				// fileNames = fileNames.substring(0,fileNames.length()-4);
				String[] arrFileNames = fileNames.split(" @- ");
				if (arrFileNames != null && arrFileNames.length > 0) {
					downloadFilesInWindows(arrFileNames, dirType);
					if (arrFileNames.length != 1)
						number = 2;
					else
						number = 1;
				} else {
					throw new FileNotFoundException("File not found on the file Path :  " + filePath);
				}
				exceptionCode = CommonUtils.getResultObject("", 1, "", "");
				exceptionCode.setRequest(fName);
				exceptionCode.setResponse(number);

			} catch (Exception e) {
				exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
				exceptionCode.setErrorMsg(e.getMessage());
				e.printStackTrace();
			}
			return exceptionCode;
		} catch (Exception e) {
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg(e.getMessage());
			e.printStackTrace();
		}
		return exceptionCode;
	}

	public BufferedInputStream getLogFile(FileInfoVb fileInfoVb) {
		FTPClient ftpClient = null;
		BufferedInputStream input = null;
		try {
			setUploadDownloadDirFromDB();
			if (isSecuredFtp()) {
				return getLogFileFromSFTP(fileInfoVb);
			}
			ftpClient = getConnection();
			ftpClient.connect(getHostName());
			boolean response = ftpClient.login(getUserName(), getPassword());
			if (!response) {
				// logger.error("Unable to login to the FTP Server.");
				return null;
			}
			response = ftpClient.changeWorkingDirectory(buildLogsDir);
			if (!response) {
				// logger.error("Unable to login to the FTP Server.");
				return null;
			}
			ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
			input = new BufferedInputStream(ftpClient.retrieveFileStream(fileInfoVb.getName()));
			if (input == null) {
				input = new BufferedInputStream(ftpClient.retrieveFileStream("BUILDCRON.log"));
				fileInfoVb.setName("BUILDCRON.log");
			}
		} catch (Exception e) {
			e.printStackTrace();
			// logger.error("Unable to login to the FTP Server.");
			return null;
		} finally {
			if (ftpClient != null)
				try {
					ftpClient.disconnect();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		return input;
	}

	public BufferedInputStream getLogFileFromSFTP(FileInfoVb fileInfoVb) {
		BufferedInputStream input = null;
		Session session = null;
		try {
			setUploadDownloadDirFromDB();
			JSch jsch = new JSch();
			jsch.setKnownHosts(getKnownHostsFileName());
			session = jsch.getSession(getUserName(), getHostName());
			{ // "interactive" version // can selectively update specified known_hosts file
				// need to implement UserInfo interface
				// MyUserInfo is a swing implementation provided in
				// examples/Sftp.java in the JSch dist
				UserInfo ui = new MyUserInfo();
				session.setUserInfo(ui); // OR non-interactive version. Relies in host key being in known-hosts file
				session.setPassword(getPassword());
			}
			session.setConfig("StrictHostKeyChecking", "no");
			session.connect();
			Channel channel = session.openChannel("sftp");
			channel.connect();
			ChannelSftp sftpChannel = (ChannelSftp) channel;
			sftpChannel.cd(buildLogsDir);
			try {
				InputStream ins = sftpChannel.get(fileInfoVb.getName());
				input = new BufferedInputStream(new ByteArrayInputStream(ins.readAllBytes()));
			} catch (SftpException ex) {
				try {
					if (input == null) {
						InputStream ins = sftpChannel.get("BUILDCRON.log");
						input = new BufferedInputStream(new ByteArrayInputStream(ins.readAllBytes()));
						fileInfoVb.setName("BUILDCRON.log");
					}
				} catch (SftpException ex1) {

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			// logger.error("Unable to login to the FTP Server.");
			return null;
		} finally {
			if (session != null)
				session.disconnect();
		}
		return input;
	}

	public BufferedInputStream getLogFilesFromSFTP(FileInfoVb fileInfoVb, String command) {
		BufferedInputStream input = null;
		Session session = null;
		Channel shellChannel = null;
		try {
			setUploadDownloadDirFromDB();
			JSch jsch = new JSch();
			jsch.setKnownHosts(getKnownHostsFileName());
			session = jsch.getSession(getUserName(), getHostName());
			{ // "interactive" version // can selectively update specified known_hosts file
				// need to implement UserInfo interface
				// MyUserInfo is a swing implementation provided in
				// examples/Sftp.java in the JSch dist
				UserInfo ui = new MyUserInfo();
				session.setUserInfo(ui); // OR non-interactive version. Relies in host key being in known-hosts file
				session.setPassword(getPassword());
			}
			session.setConfig("StrictHostKeyChecking", "no");
			session.connect();

			shellChannel = session.openChannel("shell");
			OutputStream inputstream_for_the_channel = shellChannel.getOutputStream();
			PrintStream commander = new PrintStream(inputstream_for_the_channel, true);
			shellChannel.connect();
			commander.println("cd " + buildLogsDir);
			commander.println("tar -cvf " + fileInfoVb.getName() + " " + command);
			commander.println("exit");
			commander.close();
			do {
				Thread.sleep(1000);
			} while (!shellChannel.isEOF());
			Channel channel = session.openChannel("sftp");
			channel.connect();
			ChannelSftp sftpChannel = (ChannelSftp) channel;
			sftpChannel.cd(buildLogsDir);
			try {
				InputStream ins = sftpChannel.get(fileInfoVb.getName());
				input = new BufferedInputStream(new ByteArrayInputStream(ins.readAllBytes()));
			} catch (SftpException exp) {
				try {
					InputStream ins = sftpChannel.get("BUILDCRON.log");
					input = new BufferedInputStream(new ByteArrayInputStream(ins.readAllBytes()));
					fileInfoVb.setName("BUILDCRON.log");
				} catch (SftpException ex) {

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			// logger.error("Unable to login to the FTP Server.");
			return null;
		} finally {
			if (session != null)
				session.disconnect();
		}
		return input;
	}

	public BufferedInputStream getLogFiles(FileInfoVb fileInfoVb, String command) {
		FTPClient ftpClient = null;
		TelnetConnection telnetConnection = null;
		BufferedInputStream input = null;
		try {
			setUploadDownloadDirFromDB();
			if (isSecuredFtp()) {
				return getLogFilesFromSFTP(fileInfoVb, command);
			}
			telnetConnection = new TelnetConnection(hostName, userName, password, prompt);
			telnetConnection.connect();
			telnetConnection.sendCommand("cd " + buildLogsDir);
			telnetConnection.sendCommand("tar -cvf " + fileInfoVb.getName() + " " + command);
			ftpClient = getConnection();
			ftpClient.connect(getHostName());
			boolean response = ftpClient.login(getUserName(), getPassword());
			if (!response) {
				// logger.error("Unable to login to the FTP Server.");
				return null;
			}
			response = ftpClient.changeWorkingDirectory(buildLogsDir);
			if (!response) {
				// logger.error("Unable to login to the FTP Server.");
				return null;
			}
			ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
			input = new BufferedInputStream(ftpClient.retrieveFileStream(fileInfoVb.getName()));
			if (input == null) {
				input = new BufferedInputStream(ftpClient.retrieveFileStream("BUILDCRON.log"));
				fileInfoVb.setName("BUILDCRON.log");
			}
		} catch (Exception e) {
			e.printStackTrace();
			// logger.error("Unable to login to the FTP Server.");
			return null;
		} finally {
			if (ftpClient != null)
				try {
					ftpClient.disconnect();
				} catch (IOException e) {
					e.printStackTrace();
				}
			if (telnetConnection != null && telnetConnection.isConnected()) {
				telnetConnection.disconnect();
			}
		}
		return input;
	}

	private FTPFile isFileExists(String strFileName, FTPClient ftpClient) throws IOException {
		FTPFile[] tmp = ftpClient.listFiles(strFileName);
		if (tmp != null && tmp.length > 0)
			return tmp[0];
		return null;
	}

	private ChannelSftp.LsEntry isFileExists(String strFileName, ChannelSftp ftpClient) throws SftpException {
		try {
			Vector<ChannelSftp.LsEntry> lvec = ftpClient.ls(strFileName);
			return (lvec != null && lvec.size() > 0) ? lvec.get(0) : null;
		} catch (SftpException e) {
		}
		return null;
	}

	@Override
	protected AbstractDao<VisionUploadVb> getScreenDao() {
		return visionUploadDao;
	}

	@Override
	protected void setAtNtValues(VisionUploadVb object) {
		object.setUploadStatusNt(10);
	}

	@Override
	protected void setVerifReqDeleteType(VisionUploadVb object) {
		object.setVerificationRequired(false);
		object.setStaticDelete(false);
	}

	public ExceptionCode doTemplateUpload(String fileName, byte[] data) {
		ExceptionCode exceptionCode = null;
		File lfile = null;
		FileChannel source = null;
		FileChannel destination = null;
		try {
			setUploadDownloadDirFromDB();
			if (securedFtp) {
				JSch jsch = new JSch();
				jsch.setKnownHosts(getKnownHostsFileName());
				Session session = jsch.getSession(getUserName(), getHostName());
				{ // "interactive" version // can selectively update specified known_hosts file
					// need to implement UserInfo interface
					// MyUserInfo is a swing implementation provided in
					// examples/Sftp.java in the JSch dist
					UserInfo ui = new MyUserInfo();
					session.setUserInfo(ui); // OR non-interactive version. Relies in host key being in known-hosts file
					session.setPassword(getPassword());
				}
				session.setConfig("StrictHostKeyChecking", "no");
				session.connect();
				Channel channel = session.openChannel("sftp");
				channel.connect();
				ChannelSftp sftpChannel = (ChannelSftp) channel;
				fileName = fileName.toUpperCase();
				sftpChannel.cd(cbDir);
				InputStream in = new ByteArrayInputStream(data);
				sftpChannel.put(in, fileName);
				sftpChannel.exit();
				channel = session.openChannel("shell");
				OutputStream inputstream_for_the_channel = channel.getOutputStream();
				PrintStream commander = new PrintStream(inputstream_for_the_channel, true);
				channel.connect();
				commander.println("cd " + scriptDir);
				StringBuilder cmd = new StringBuilder();
				commander.println(cmd);
				commander.println("exit");
				commander.close();
				do {
					Thread.sleep(1000);
				} while (!channel.isEOF());
				session.disconnect();
			} else {
				FTPClient ftpClient = getConnection();
				ftpClient.connect(getHostName());
				boolean response = ftpClient.login(getUserName(), getPassword());
				if (!response) {
					ftpClient.disconnect();
					exceptionCode = CommonUtils.getResultObject(SERVICE_NAME, Constants.FILE_UPLOAD_REMOTE_ERROR,
							"Upload", "");
					;
					exceptionCode.setResponse(fileName);
					throw new RuntimeCustomException(exceptionCode);
				}
				ftpClient.setFileType(fileType);
				response = ftpClient.changeWorkingDirectory(cbDir);
				if (!response) {
					ftpClient.disconnect();
					exceptionCode = CommonUtils.getResultObject(SERVICE_NAME, Constants.FILE_UPLOAD_REMOTE_ERROR,
							"Upload", "");
					;
					exceptionCode.setResponse(fileName);
					throw new RuntimeCustomException(exceptionCode);
				}
				fileName = fileName.toUpperCase();
				fileName = fileName.substring(0, fileName.lastIndexOf('.')) + "_"
						+ CustomContextHolder.getContext().getVisionId() + "." + getFileExtension() + "";
				InputStream in = new ByteArrayInputStream(data);
				ftpClient.storeFile(fileName, in);
				in.close(); // close the io streams
				ftpClient.disconnect();
			}
		} catch (FileNotFoundException e) {
			exceptionCode = CommonUtils.getResultObject(SERVICE_NAME, Constants.FILE_UPLOAD_REMOTE_ERROR, "Upload", "");
			;
			exceptionCode.setResponse(fileName);
			throw new RuntimeCustomException(exceptionCode);
		} catch (IOException e) {
			exceptionCode = CommonUtils.getResultObject(SERVICE_NAME, Constants.FILE_UPLOAD_REMOTE_ERROR, "Upload", "");
			;
			exceptionCode.setResponse(fileName);
			throw new RuntimeCustomException(exceptionCode);
		} catch (Exception e) {
			exceptionCode = CommonUtils.getResultObject(SERVICE_NAME, Constants.FILE_UPLOAD_REMOTE_ERROR, "Upload", "");
			;
			exceptionCode.setResponse(fileName);
			throw new RuntimeCustomException(exceptionCode);
		}
		exceptionCode = CommonUtils.getResultObject(SERVICE_NAME, Constants.SUCCESSFUL_OPERATION, "Upload", "");
		exceptionCode.setOtherInfo(fileName);
		return exceptionCode;
	}

	public long getDateTimeInMS(String date, String formate) {
		SimpleDateFormat lFormat = new SimpleDateFormat(formate);
		try {
			Date lDate = lFormat.parse(date);
			return lDate.getTime();
		} catch (Exception e) {
			return System.currentTimeMillis();
		}
	}

	public ExceptionCode listFilesFromConnectors(String macroVar) {
		ExceptionCode exceptionCode = null;
		try {
			setUploadDownloadDirFromDB();
			SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

			/* Files From ConnectorsAD */
			File[] files = new File(connectorADUploadDir).listFiles();
			List<FileInfoVb> collTemp = new ArrayList<FileInfoVb>();
			if (ValidationUtil.isValid(files)) {
				for (File file : files) {
					String fileName = file.getName();
					if (fileName.indexOf(
							"_" + CustomContextHolder.getContext().getVisionId() + "_" + macroVar + "_") != -1) {
						FileInfoVb fileInfoVb = new FileInfoVb();
						fileInfoVb.setName(file.getName());
						fileInfoVb.setSize(formatFileSize(file.length()));
						fileInfoVb.setDate(sdf.format(file.lastModified()));
						fileInfoVb.setExtension(fileName.substring(fileName.lastIndexOf(".") + 1));
						fileInfoVb.setGroupBy("connectorADUploadDir");
						collTemp.add(fileInfoVb);
					}
				}
			}

			/* Files From ConnectorsData */
			File[] filesData = new File(connectorUploadDir).listFiles();
			if (ValidationUtil.isValid(filesData)) {
				for (File file : filesData) {
					String fileName1 = file.getName();
					if (fileName1.indexOf(
							"_" + CustomContextHolder.getContext().getVisionId() + "_" + macroVar + "_") != -1) {
						FileInfoVb fileInfoVb = new FileInfoVb();
						fileInfoVb.setName(file.getName());
						fileInfoVb.setSize(formatFileSize(file.length()));
						fileInfoVb.setDate(sdf.format(file.lastModified()));
						fileInfoVb.setExtension(fileName1.substring(fileName1.lastIndexOf(".") + 1));
						fileInfoVb.setGroupBy("connectorUploadDir");
						collTemp.add(fileInfoVb);
					}
				}
			}
			exceptionCode = CommonUtils.getResultObject(SERVICE_NAME, Constants.SUCCESSFUL_OPERATION,
					"List Files Success", "");
			exceptionCode.setResponse(createParentChildRelations(collTemp, "Date", 1));
		} catch (Exception e) {
			e.printStackTrace();
			exceptionCode = CommonUtils.getResultObject(SERVICE_NAME, Constants.FILE_UPLOAD_REMOTE_ERROR, "Download",
					"");
			throw new RuntimeCustomException(exceptionCode);
		}
		return exceptionCode;
	}

	public ExceptionCode fileDownloadFromConnector(FileInfoVb fileInfoVb, String connectorDir) {
		ExceptionCode exceptionCode = null;
		File file = null;
		FileOutputStream fos = null;
		BufferedInputStream bufferedInputStream = null;
		String searchDir = "";
		try {
			String fileName = fileInfoVb.getName();
			setUploadDownloadDirFromDB();
			String filePath = System.getProperty("java.io.tmpdir");
			file = new File(filePath + fileName);
			fos = new FileOutputStream(file);
			if (connectorDir.equalsIgnoreCase("connectorUploadDir")) {
				searchDir = connectorUploadDir + fileName;
			} else {
				searchDir = connectorADUploadDir + fileName;

			}
			InputStream ins = new FileInputStream(searchDir);
			bufferedInputStream = new BufferedInputStream(new ByteArrayInputStream(ins.readAllBytes()));

			int bit = 4096;
			while ((bit) >= 0) {
				bit = bufferedInputStream.read();
				fos.write(bit);
			}
			bufferedInputStream.close();
			fos.close();
			exceptionCode = CommonUtils.getResultObject(SERVICE_NAME, Constants.SUCCESSFUL_OPERATION, "Sucess", "");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return exceptionCode;
	}

	public HttpServletResponse setExportXlsServlet(HttpServletRequest request, HttpServletResponse response) {
		try {
			exportXlsServlet.doPost(request, response);
			return response;
		} catch (Exception e) {
			throw new RuntimeCustomException(e.getMessage());
		}
	}

	public ExceptionCode xmlToJson(MultipartFile[] files) throws IOException {
		ExceptionCode exceptionCode = null;
		try {
			for (MultipartFile uploadedFile : files) {
				String content = new String(uploadedFile.getBytes(), "UTF-8");
				exceptionCode = CommonUtils.XmlToJson(content);
			}
		} catch (RuntimeCustomException rex) {
			exceptionCode = CommonUtils.getResultObject("xmlToJson", Constants.WE_HAVE_ERROR_DESCRIPTION,
					"XML To JSON Conversion", rex.getMessage());
			exceptionCode.setResponse("");
			return exceptionCode;
		}
		return exceptionCode;
	}

	public ExceptionCode doloadingExcel(MultipartFile files, VisionUploadVb visionUploadVb) {
		ExceptionCode exceptionCode = new ExceptionCode();
		List<LoadXlVb> collTemp = new ArrayList<>();

		exceptionCode = doValidate(visionUploadVb);
		if (exceptionCode.getErrorCode() == Constants.ERRONEOUS_OPERATION) {
			return exceptionCode;
		}

		try (Workbook workbook = WorkbookFactory.create(files.getInputStream())) {
			int numberOfSheets = workbook.getNumberOfSheets();
			for (int i = 0; i < numberOfSheets; i++) {
				LoadXlVb loadXlVb = new LoadXlVb();
				Sheet sheet = workbook.getSheetAt(i);
				String sheetName = sheet.getSheetName();
				int numberOfRows = sheet.getLastRowNum() + 1;
				loadXlVb.setNumberOfSheets(i + 1);
				loadXlVb.setSheetName(sheetName);
				loadXlVb.setNumberOfRows(numberOfRows);
				collTemp.add(loadXlVb);
			}
			// System.out.println(collTemp.toString());
			exceptionCode.setResponse(collTemp);
			exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
			exceptionCode.setErrorMsg("Loaded Successful");
		} catch (Exception e) {
			e.printStackTrace();
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg("File is not in Valid Format");

		}
		return exceptionCode;
	}

	public boolean isExcel(InputStream inputStream) throws IOException {
		if (!inputStream.markSupported()) {
			inputStream = new BufferedInputStream(inputStream);
		}

		inputStream.mark(10); // FileMagic needs some bytes to peek the stream
		FileMagic fileMagic = FileMagic.valueOf(inputStream);
		inputStream.reset();

		return fileMagic == FileMagic.OLE2 || fileMagic == FileMagic.OOXML;
	}

	public void downloadFilesInWindows(String[] logFile, int dirType) {
		ByteArrayOutputStream out = null;
		OutputStream outputStream = null;
		try {
			String tmpFilePath = System.getProperty("java.io.tmpdir");
			String filePath = "";
			if (dirType == DIR_TYPE_DOWNLOAD)
				filePath = downloadDir;
			else
				filePath = "";

			if (logFile.length > 1) {
				FileOutputStream fos = new FileOutputStream(tmpFilePath + File.separator + "logs.zip");
				ZipOutputStream zipOut = new ZipOutputStream(new BufferedOutputStream(fos));
				for (int i = 0; i < logFile.length; i++) {
					FileInputStream fis = new FileInputStream(filePath + File.separator + logFile[i]);
					ZipEntry ze = new ZipEntry(logFile[i]);
					zipOut.putNextEntry(ze);
					byte[] tmp = new byte[4 * 1024];
					int size = 0;
					while ((size = fis.read(tmp)) != -1) {
						zipOut.write(tmp, 0, size);
					}
					fis.close();
				}
				zipOut.close();
				fos.close();
			} else {
				/*
				 * File lfile = new File(tmpFilePath + File.separator + "logs.zip"); if
				 * (lfile.exists()) { lfile.delete(); }
				 */
				File my_file = new File(filePath + File.separator + logFile[0]);
				out = new ByteArrayOutputStream();
				FileInputStream in = new FileInputStream(my_file);
				out = new ByteArrayOutputStream();
				my_file = new File(tmpFilePath + File.separator + logFile[0]);
				if (my_file.exists()) {
					my_file.delete();
				}
				my_file.createNewFile();
				outputStream = new FileOutputStream(tmpFilePath + File.separator + logFile[0]);
				int length = logFile[0].length();
				int bufferSize = 1024;
				byte[] buffer = new byte[bufferSize];
				while ((length = in.read(buffer)) != -1) {
					out.write(buffer, 0, length);
				}
				out.writeTo(outputStream);
				outputStream.flush();
				outputStream.close();
				out.flush();
				out.close();
				in.close();

			}
		} catch (Exception e) {
			e.printStackTrace();

		}
		// return bufferedInputStream;
	}

	public ExceptionCode deleteRecord(VisionUploadVb vObject) {
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			exceptionCode = doValidate(vObject);
			if (exceptionCode.getErrorCode() == Constants.ERRONEOUS_OPERATION) {
				return exceptionCode;
			}

			List<VisionUploadVb> collTemp = visionUploadDao.getQueryDetails(vObject, Constants.STATUS_ZERO);
			if (collTemp != null && collTemp.size() > 0) {
				retVal = visionUploadDao.doDeleteAppr(vObject);
				exceptionCode.setErrorCode(retVal);
				exceptionCode.setErrorMsg("Vision Upload Delete Successful");
				exceptionCode.setOtherInfo(vObject);
			} else {
				exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
				exceptionCode.setErrorMsg("No Record Found To Delete");
				exceptionCode.setOtherInfo(vObject);
			}
			return exceptionCode;
		} catch (RuntimeCustomException rex) {
			// logger.error("Delete Exception " + rex.getCode().getErrorMsg());
			// logger.error( ((vObject==null)? "vObject is Null":vObject.toString()));
			exceptionCode = rex.getCode();
			exceptionCode.setOtherInfo(vObject);
			return exceptionCode;
		}
	}

	public ExceptionCode doValidate(VisionUploadVb vObject) {
		ExceptionCode exceptionCode = new ExceptionCode();
		String operation = vObject.getActionType();
		String srtRestriction = getCommonDao().getRestrictionsByUsers("VisionUpload", operation);
		if (!"Y".equalsIgnoreCase(srtRestriction)) {
			exceptionCode = new ExceptionCode();
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg(operation + " " + Constants.userRestrictionMsg);
			return exceptionCode;
		}
		exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
		return exceptionCode;
	}

}