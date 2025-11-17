package com.vision.util;

public class Test {

	public static void main(String[] args) {
		String orgValue = "20000234";
		String msg = "feespriority-add-failed-invalidcolumnalias:http://202.83.25.244:8001//ma.pleaseusealphanumericcharactersandunderscores.";
		int i = Integer.parseInt(orgValue);
		String value = String.format("%,d", i);
		String EXTRACTION_ENGINE = "RAH";
		String logPath = "D:\\\\RA_APP_V1\\\\Java_Execs\\\\Logs\\\\";
		String logFileName = "BLCARS0067_8_KE_01_.log";
		String execsPath ="E:\\Vulnear\\NCBA\\";
		String historyBuildJar = "RAHistoryBuild.jar";
		String COUNTRY = "KE";
		String LE_BOOK ="01";
		String formatBusinessDate ="24-JUN-2025";
		String EXTRACTION_FEED_ID ="BLCARS0067";
		String fulLogFileName = logPath+logFileName;
		String POSTING_SEQUENCE = "1";

		String callCommand = "java -Dservice.buildname=" + EXTRACTION_ENGINE
				+ "  -Dservice.LogPath=" + logPath + ""
				+ "  -Dservice.LogFileName=" + logFileName + "" + " -jar "
				+ execsPath + historyBuildJar + " " + COUNTRY + " " + LE_BOOK + " "
				+ formatBusinessDate + " " + EXTRACTION_FEED_ID + " Y "
				+ EXTRACTION_ENGINE + " " + fulLogFileName + "" + " "
				+ POSTING_SEQUENCE;
		// System.out.println(value);
		System.out.println(callCommand);
	}
	

	public static String StandardStringToHtmlString(String standardText) {
		

			// Check if the text contains "http" to assume it's a URL.
			
				// If not a URL, remove all non-alphanumeric characters
//				standardText = standardText.replaceAll("[^a-zA-Z0-9_]", "");
//			
//
//			// Convert remaining special characters for XML/HTML encoding
//			standardText = standardText.replaceAll("&;", "&amp");
//			standardText = standardText.replaceAll("<", "&lt;");
//			standardText = standardText.replaceAll(">", "&gt;");
//			standardText = standardText.replaceAll("\"", "&quot;");
//			standardText = standardText.replaceAll("'", "&apos;");
		standardText = standardText.replaceAll("[^a-zA-Z0-9_]", " ");
		standardText = standardText.replaceAll("&", "&amp;");
		standardText = standardText.replaceAll("<", "&lt;");
		standardText = standardText.replaceAll(">", "&gt;");
		standardText = standardText.replaceAll("\"", "&quot;");
		standardText = standardText.replaceAll("\'", "&apos;");
		standardText = standardText.replaceAll("\n", " ").replaceAll("\r", " ");
		standardText = standardText.trim().replaceAll(" +", " ");
		return standardText;
	

		
	}
}
