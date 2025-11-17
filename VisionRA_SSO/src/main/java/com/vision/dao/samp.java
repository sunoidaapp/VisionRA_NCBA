package com.vision.dao;

import com.vision.exception.ExceptionCode;

public class samp {

	public static void main(String[] args) {
		String errormsg = "org.postgresql.util.PSQLException: Unterminated string literal started at position 2,176 in SQL CREATE TABLE TEST444 AS (				CREATE TABLE TMP_9005_25462 AS (Select ROW_NUMBER() OVER ( ORDER BY\r\n"
				+ "   DEAL_TYPE ) num, rowtemp.* from(Select TA1.*  FROM (\r\n"
				+ "SELECT\r\n"
				+ "   T1.ACCOUNT_OFFICER || ' - ' || (\r\n"
				+ "      SELECT AO_NAME\r\n"
				+ "      FROM ACCOUNT_OFFICERS\r\n"
				+ "      WHERE COUNTRY = T1.COUNTRY\r\n"
				+ "        AND LE_BOOK = T1.LE_BOOK\r\n"
				+ "        AND ACCOUNT_OFFICER = T1.ACCOUNT_OFFICER\r\n"
				+ "   ) AS ACCOUNT_OFFICER,\r\n"
				+ " \r\n"
				+ "   (SELECT Alpha_subtab_description\r\n"
				+ "    FROM Alpha_sub_tab\r\n"
				+ "    WHERE Alpha_tab = 6009\r\n"
				+ "      AND Alpha_sub_tab = T1.Deal_type) AS Deal_Type,\r\n"
				+ " \r\n"
				+ "   CASE\r\n"
				+ "      WHEN T1.Deal_Type = 'PROD' THEN T5.Alpha_subtab_description\r\n"
				+ "      ELSE T11.Alpha_subtab_description\r\n"
				+ "   END AS Product_Service,\r\n"
				+ " \r\n"
				+ "   (SELECT Alpha_subtab_description\r\n"
				+ "    FROM Alpha_sub_tab\r\n"
				+ "    WHERE Alpha_tab = T1.Deal_Stage_at\r\n"
				+ "      AND Alpha_sub_tab = T1.Deal_Stage) AS Deal_Stage,\r\n"
				+ " \r\n"
				+ "   COUNT(T1.Deal_Id) AS No_Of_Deals,\r\n"
				+ "   SUM(COALESCE(T1.DEAL_APPROX_VALUE, 0)) AS DEAL_APPROX_VALUE\r\n"
				+ " \r\n"
				+ "FROM\r\n"
				+ "   Deal_Entry T1\r\n"
				+ "   LEFT JOIN Alpha_sub_tab T5\r\n"
				+ "      ON T5.Alpha_tab = T1.Product_Basket_At\r\n"
				+ "      AND T5.Alpha_sub_tab = T1.Product_Basket\r\n"
				+ "   LEFT JOIN Alpha_sub_tab T11\r\n"
				+ "      ON T11.Alpha_tab = T1.Service_Type_at\r\n"
				+ "      AND T11.Alpha_sub_tab = T1.Service_Type\r\n"
				+ "   JOIN Mdm_Customer MC\r\n"
				+ "      ON T1.country = MC.country\r\n"
				+ "      AND T1.le_book = MC.le_Book\r\n"
				+ "      AND T1.cif_ID = MC.Cif_Id\r\n"
				+ "   JOIN Vision_Users VU\r\n"
				+ "      ON VU.vision_ID = '9005'\r\n"
				+ " \r\n"
				+ "WHERE\r\n"
				+ "   (\r\n"
				+ "   'ALL' = ANY(regexp_split_to_array(NULLIF('ALL', '), ','))\r\n"
				+ "   OR T1.ACCOUNT_OFFICER = ANY(regexp_split_to_array(NULLIF('ALL', '), ','))\r\n"
				+ ")\r\n"
				+ " \r\n"
				+ "   AND T1.DEAL_DATE::date BETWEEN TO_DATE('5','10','15','20','25','NA', 'DD-MM-YYYY')\r\n"
				+ "		                  AND TO_DATE('19-Aug-2025', 'DD-MM-YYYY')\r\n"
				+ " \r\n"
				+ "   AND (\r\n"
				+ "      'ALL' = ANY(regexp_split_to_array(NULLIF('20-Aug-2025', ''), ','))\r\n"
				+ "      OR T1.Deal_Stage = ANY(regexp_split_to_array(NULLIF('20-Aug-2025', '), ','))\r\n"
				+ "   )\r\n"
				+ " \r\n"
				+ "   AND (\r\n"
				+ "      MC.Vision_ouc = VU.ouc_Attribute\r\n"
				+ "      OR COALESCE(VU.ouc_Attribute, 'ALL') = 'ALL'\r\n"
				+ "   )\r\n"
				+ "   AND (\r\n"
				+ "      MC.Vision_Sbu = VU.Sbu_Code\r\n"
				+ "      OR COALESCE(VU.Sbu_Code, 'ALL') = 'ALL'\r\n"
				+ "   )\r\n"
				+ "   AND (\r\n"
				+ "      'ALL' = ANY(regexp_split_to_array(NULLIF('', '), ','))\r\n"
				+ "      OR MC.Vision_Sbu = ANY(regexp_split_to_array(NULLIF('', '), ','))\r\n"
				+ "   )\r\n"
				+ " \r\n"
				+ "GROUP BY\r\n"
				+ "   T1.COUNTRY,\r\n"
				+ "   T1.LE_BOOK,\r\n"
				+ "   T1.ACCOUNT_OFFICER,\r\n"
				+ "   T1.Deal_type,\r\n"
				+ "   T1.Deal_Stage,\r\n"
				+ "   T5.Alpha_subtab_description,\r\n"
				+ "   T11.Alpha_subtab_description,\r\n"
				+ "   T1.Deal_Stage_at,\r\n"
				+ "   T1.Product_Basket\r\n"
				+ " \r\n"
				+ ") TA1) rowtemp )\r\n"
				+ "). Expected  char\r\n"
				+ " ";
		ExceptionCode ex = new ExceptionCode();
		ex.setErrorMsg(errormsg);

//		System.out.println(ex.getErrorMsg());
		try {
		    throw new Exception(errormsg);
		} catch (Exception e) {
		    System.out.println(parseErrorMsg(e));
		}

	}
	public static String parseErrorMsg(Exception exception) {
	    if (exception == null || exception.getMessage() == null) {
	        return "Unknown error occurred";
	    }

	    String errorMsg = exception.getMessage();

	    // ✅ 1. Remove any SQL statements in [ ... ] (if any)
	    String sqlPattern = "\\[(.*?)\\b(SELECT|INSERT|UPDATE|DELETE)\\b(.*?)\\]";
	    errorMsg = errorMsg.replaceAll(sqlPattern, "");

	    // ✅ 2. Known Oracle error codes
	    String[] oracleErrorCodes = {
	        "ORA-", "ORA-00928:", "ORA-00942:", "ORA-00998:", "ORA-01400:", "ORA-01722:", "ORA-04098:",
	        "ORA-01810:", "ORA-01840:", "ORA-01843:", "ORA-20001:", "ORA-20002:", "ORA-20003:", "ORA-20004:",
	        "ORA-20005:", "ORA-20006:", "ORA-20007:", "ORA-20008:", "ORA-20009:", "ORA-20010:", "ORA-20011:",
	        "ORA-20012:", "ORA-20013:", "ORA-20014:", "ORA-20015:", "ORA-20016:", "ORA-20017:", "ORA-20018:",
	        "ORA-20019:", "ORA-20020:", "ORA-20021:", "ORA-20022:", "ORA-20023:", "ORA-20024:", "ORA-20025:",
	        "ORA-20102:", "ORA-20105:", "ORA-01422:", "ORA-06502:", "ORA-20082:", "ORA-20030:", "ORA-20034:",
	        "ORA-20043:", "ORA-20111:", "ORA-06512:", "ORA-04088:", "ORA-06552:", "ORA-00001:"
	    };

	    // ✅ 3. Known MSSQL error codes
	    String[] mssqlErrorCodes = {
	        "SQLServerException:", "Violation of PRIMARY KEY", "Violation of UNIQUE KEY",
	        "Cannot insert the value NULL", "String or binary data would be truncated",
	        "Arithmetic overflow error", "Divide by zero error"
	    };

	    // ✅ 4. Known PostgreSQL/MySQL error markers
	    String[] genericErrorMarkers = {
	        "PSQLException:", "MySQLSyntaxErrorException:", "Duplicate entry",
	        "Data truncation", "Cannot add or update a child row", "foreign key constraint fails"
	    };

	    // ✅ 5. Try to sanitize error message
	    errorMsg = sanitizeError(errorMsg, oracleErrorCodes);
	    errorMsg = sanitizeError(errorMsg, mssqlErrorCodes);
	    errorMsg = sanitizeError(errorMsg, genericErrorMarkers);

	    // ✅ 6. Final cleanup: Remove line numbers, stack traces, etc.
	    errorMsg = errorMsg.replaceAll("ORA-06512:.*", ""); // Oracle trace line
	    errorMsg = errorMsg.replaceAll("(?i)at\\s+.*", ""); // Java stack trace
	    errorMsg = errorMsg.replaceAll("\\s+", " ").trim();

	    if (errorMsg.isEmpty()) {
	        return "Database error occurred. Please contact support.";
	    }

	    return errorMsg;
	}

	private static String sanitizeError(String errorMsg, String[] errorCodes) {
	    for (String code : errorCodes) {
	        if (errorMsg.contains(code)) {
	            int start = errorMsg.indexOf(code);
	            String cleaned = errorMsg.substring(start).split("\\n")[0]; // first line only

	            // ✅ Remove SQL dump between "in SQL ... )" but keep trailing message
	            cleaned = cleaned.replaceAll("\\s+in SQL.*?\\)\\s*", ") ");

	            return cleaned.trim();
	        }
	    }
	    return errorMsg;
	}

}
