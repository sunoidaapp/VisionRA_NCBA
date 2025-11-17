package com.vision.wb;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.vision.dao.CommonDao;
import com.vision.dao.FolderPurgeDao;
import com.vision.util.ValidationUtil;
import com.vision.vb.FolderPurgeVb;
@Component
public class FolderPurge {
	@Autowired
	FolderPurgeDao folderPurgeDao;
	
	@Autowired
	CommonDao commonDao; 

	@Scheduled(fixedRate=20000)
	public void deleteFiles() {
		FolderPurgeVb folderPurgeVb = new FolderPurgeVb();
		String purgeFlag = commonDao.findVisionVariableValue("RA_FOLDER_PURGE");
		try {
			if ("Y".equalsIgnoreCase(purgeFlag)) {
				List<FolderPurgeVb> purgeList = new ArrayList<FolderPurgeVb>();
				int cnt = 0;
				purgeList = folderPurgeDao.getPurgeDetails(folderPurgeVb);
				ZipOutputStream zipOut = null;
				if (purgeList != null && purgeList.size() > 0) {
					for (FolderPurgeVb purgefile : purgeList) {
						cnt = 0;
						File directory = new File(purgefile.getFolderPath());
						if ("Y".equalsIgnoreCase(purgefile.getArchiveFlag())) {
							FileOutputStream fos = new FileOutputStream(purgefile.getArchivePath() + "\\"
									+ purgefile.getArchiveName() + "_" + LocalDate.now() + ".zip");
							zipOut = new ZipOutputStream(new BufferedOutputStream(fos));
						}
						if (directory.exists()) {
							File[] listFiles = directory.listFiles();
							for (File listFile : listFiles) {
								LocalDate fileDate = Instant.ofEpochMilli(listFile.lastModified()).atZone(ZoneId.systemDefault()).toLocalDate();
								LocalDate purgeDate = LocalDate.now().minusDays(purgefile.getPurgeDays());
								if (fileDate.isBefore(purgeDate)) {
									if (ValidationUtil.isValid(purgefile.getFilePattern())) {
										if ("Y".equalsIgnoreCase(purgefile.getArchiveFlag()) && listFile.getName().startsWith(purgefile.getFilePattern())) {
											FileInputStream fis = new FileInputStream(listFile);
											ZipEntry ze = new ZipEntry(listFile.getName());
											zipOut.putNextEntry(ze);
											byte[] tmp = new byte[4 * 1024];
											int size = 0;
											while ((size = fis.read(tmp)) != -1) {
												zipOut.write(tmp, 0, size);
											}
											// zipOut.flush();
											fis.close();
										}
										if (listFile.getName().startsWith(purgefile.getFilePattern())) {
											if (listFile.delete())
												cnt = cnt + 1;
										}
									} else {
										if ("Y".equalsIgnoreCase(purgefile.getArchiveFlag())) {
											FileInputStream fis = new FileInputStream(listFile);
											ZipEntry ze = new ZipEntry(listFile.getName());
											//System.out.println("Zipping the file: " + listFile.getName());
											zipOut.putNextEntry(ze);
											byte[] tmp = new byte[4 * 1024];
											int size = 0;
											while ((size = fis.read(tmp)) != -1) {
												zipOut.write(tmp, 0, size);
											}
											// zipOut.flush();
											fis.close();
										}

										if (listFile.delete())
											cnt = cnt + 1;
									}
								}
							}
							if (zipOut != null)
								zipOut.close();
						}
						if (cnt > 0) {
							purgefile.setNumberOfPurgeFiles(cnt);
							folderPurgeDao.updatePurgeDetails(purgefile);
						}
					}

				}
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
}