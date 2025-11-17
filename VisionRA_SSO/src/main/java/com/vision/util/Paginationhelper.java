/**
 * 
 */
package com.vision.util;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

/**
 * @author kiran-kumar.karra
 *
 */
public class Paginationhelper<E> {

	public static Logger logger = LoggerFactory.getLogger(Paginationhelper.class);
	// Show a record number of 
	private long numPerPage; 
	// Record total 
	private long totalRows; 
	// Total number of pages 
	private long totalPages; 
	// Current Page 
	private long currentPage;
	// Start line number 
	private long startIndex;
	// End of line number 
	private long lastIndex;
	
	public long getCurrentPage () { 
		return currentPage; 
	}

	public void setCurrentPage(long currentPage){ 
			this.currentPage = currentPage; 
	}

	public long getNumPerPage(){
		return numPerPage; 
	}

	public void setNumPerPage (long numPerPage){ 
			this.numPerPage = numPerPage; 
	}

	public long getTotalPages (){
		return totalPages; 
	}
	// Calculate the total number of pages 
	public void setTotalPages () {
		if (totalRows% numPerPage == 0) {
			this.totalPages = (totalRows / numPerPage); 
		}else{ 
			this.totalPages = ((totalRows / numPerPage) + 1); 
		}
	}

	public long getTotalRows () {
		return totalRows; 
	} 

	public void setTotalRows (long totalRows) {
		this.totalRows = totalRows; 
	} 
	
	public long getStartIndex () {
		return startIndex; 
	} 
	
	public void setStartIndex () {
		this.startIndex = (currentPage -1) * numPerPage;
	} 
	
	public long getLastIndex () {
		return lastIndex; 
	} 
	
	// End when calculating the index 
	public void setLastIndex () {
		if (totalRows <numPerPage) {
			this.lastIndex = totalRows;
		} else if ((totalRows% numPerPage == 0) || (totalRows% numPerPage != 0 && currentPage<totalPages)) {
			this.lastIndex  = currentPage * numPerPage; 
		}else if (totalRows% numPerPage != 0 && currentPage == totalPages){// last page 
				this.lastIndex = totalRows;
		}
	}
	
	public List<E> fetchPage( final JdbcTemplate jt, final String sqlFetchRows, 
			final Object args[], final int pageNo, final int pageSize, final RowMapper rowMapper) {
		List<E> result = null;
		// Set several records per page 
		setNumPerPage(pageSize); 
		// Set up to show the number of pages 
		setCurrentPage(pageNo);
		// Total number of records 
		StringBuffer totalSQL = new StringBuffer ( "SELECT count (1) FROM ("); 
		totalSQL.append (sqlFetchRows);
		totalSQL.append ( ") totalTable");
		if (args == null) {
			setTotalRows(jt.queryForObject(totalSQL.toString(), Integer.class));
		} else {
			setTotalRows(jt.queryForObject(totalSQL.toString(), args, Integer.class));
		}
		if(getTotalRows()<=0)
			return new ArrayList<E>(0);
		if(getTotalRows() <= getNumPerPage()){
			setCurrentPage(1);
		}
		// Calculate the total number of pages
		setTotalPages();
		// Number of start-up firms 
		setStartIndex();
		// End of line number 
		setLastIndex();
		// Oracle database structure paging statement 
		StringBuffer paginationSQL = new StringBuffer ( "SELECT * FROM (SELECT * FROM (");
		paginationSQL.append (sqlFetchRows); 
		paginationSQL.append ( ") TEMP WHERE NUM <="+lastIndex+") T1 WHERE NUM>"+startIndex+" ORDER BY NUM"); 
		if(args == null){
			result =  jt.query(paginationSQL.toString(),rowMapper);
		}else{
			result = jt.query(paginationSQL.toString(),args,rowMapper);
		}
//		//logger.info("Time taken to execute query ["+paginationSQL.toString()+"] is "+ (System.currentTimeMillis()-currentTime)+" ms." );
		return result;
	}
	public List<E> fetchPage( final JdbcTemplate jt, final String sqlFetchRows,
			final Object args[], final int pageNo, final int pageSize, final Long totalRows, final RowMapper rowMapper) {

		List<E> result = null;
		// Set several records per page 
		setNumPerPage(pageSize); 
		// Set up to show the number of pages 
		setCurrentPage(pageNo); 
		// Total number of records 
		setTotalRows(totalRows);
		// Calculate the total number of pages 
		setTotalPages();
		// Number of start-up firms 
		setStartIndex ();
		// End of line number 
		setLastIndex ();
		// Oracle database structure paging statement 
		StringBuffer paginationSQL = new StringBuffer ( "SELECT * FROM (SELECT * FROM (");
		paginationSQL.append (sqlFetchRows); 
		paginationSQL.append ( ") TEMP WHERE NUM <="+lastIndex+") T1 WHERE NUM>"+startIndex+" ORDER BY NUM"); 
		long currentTime = System.currentTimeMillis();
		if(args == null){
			result =  jt.query(paginationSQL.toString(),rowMapper);
		}else{
			result =  jt.query(paginationSQL.toString(),args,rowMapper);
		}
		////logger.info("Time taken to execute query ["+paginationSQL.toString()+"] is "+ (System.currentTimeMillis()-currentTime)+" ms." );
		return result;
	}
	public String reportFetchPage(String sqlPaginationQuery,final int pageNo, final int recordsPerPage,final int totalRows) {
		setNumPerPage(recordsPerPage);
		setCurrentPage(pageNo);
		setTotalRows(totalRows);
		// Calculate the total number of pages
		setTotalPages();
		// Number of start-up firms 
		setStartIndex();
		// End of line number 
		setLastIndex();
		
		// Oracle database structure paging statement 
		StringBuffer paginationSQL = new StringBuffer ( "SELECT * FROM (SELECT * FROM (");
		paginationSQL.append (sqlPaginationQuery); 
		paginationSQL.append ( ") TEMP WHERE NUM <="+lastIndex+") T1 WHERE NUM>"+startIndex+" ORDER BY NUM"); 
		return paginationSQL.toString();
	}
}