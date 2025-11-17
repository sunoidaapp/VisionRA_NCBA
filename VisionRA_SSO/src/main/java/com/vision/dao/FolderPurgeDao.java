package com.vision.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.vision.util.ValidationUtil;
import com.vision.vb.CommonVb;
import com.vision.vb.FolderPurgeVb;
@Component
public class FolderPurgeDao extends AbstractDao<CommonVb>{
	@Value("${app.databaseType}")
	private String databaseType; 
	@Autowired
	CommonDao commonDao;
	
	public List<FolderPurgeVb> getPurgeDetails(FolderPurgeVb dObj) {
		String query = "";
		List<FolderPurgeVb> collTemp = null;
		try {
		query = " SELECT PURGE_TYPE, FOLDER_PATH,FILE_PATTERN,TABLE_NAME,PURGE_DAYS,	LAST_PURGE_DATE	,"
				+" NO_OF_PURGE_FILES,NO_OF_PURGE_TABLE,ARCHIVE_FLAG,ARCHIVE_NAME,ARCHIVE_PATH FROM RA_PURGE_POLICY Where PURGE_TYPE = 'F' ";
		collTemp = getJdbcTemplate().query(query,getPurgeDetailsMapper());
		return collTemp;
		}catch(Exception ex){
			logger.error("Exception while getting purge Detail...!!");
			return null;
		}
	}
	private RowMapper getPurgeDetailsMapper(){
		RowMapper mapper = new RowMapper(){
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				FolderPurgeVb vObject = new FolderPurgeVb();
				vObject.setPurgeType(rs.getString("PURGE_TYPE"));
				vObject.setFolderPath(rs.getString("FOLDER_PATH"));
				vObject.setFilePattern(rs.getString("FILE_PATTERN"));
				vObject.setTableName(rs.getString("TABLE_NAME"));
				vObject.setPurgeDays(rs.getInt("PURGE_DAYS"));
				vObject.setLastPurgeDate(rs.getString("LAST_PURGE_DATE"));
				vObject.setNumberOfPurgeFiles(rs.getInt("NO_OF_PURGE_FILES"));
				vObject.setNumberOfPurgeTables(rs.getInt("NO_OF_PURGE_TABLE"));
				vObject.setArchiveFlag(rs.getString("ARCHIVE_FLAG"));
				vObject.setArchiveName(rs.getString("ARCHIVE_NAME"));
				vObject.setArchivePath(rs.getString("ARCHIVE_PATH"));
				return vObject;
			}
		};
		return mapper;
	}
	public int updatePurgeDetails(FolderPurgeVb vObject) {
		String query = "";
			query = "UPDATE RA_PURGE_POLICY SET LAST_PURGE_DATE = "+getDbFunction("SYSDATE")+" , NO_OF_PURGE_FILES = ? "
					+ " WHERE PURGE_TYPE = ? AND FOLDER_PATH = ? AND ISNULL(FILE_PATTERN,'NA') = ? AND PURGE_DAYS = ?";
		Object args[] = { vObject.getNumberOfPurgeFiles(),vObject.getPurgeType(),
				vObject.getFolderPath(),ValidationUtil.isValid(vObject.getFilePattern())?vObject.getFilePattern():"NA",
				vObject.getPurgeDays()};
		return getJdbcTemplate().update(query, args);
	}
}
