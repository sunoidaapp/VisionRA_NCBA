package com.vision.wb;

import java.text.ParseException;

public class Test {
	public static void main(String args[]) throws ParseException {
		/*
		 * String folderPath = "E:\\logs"; File directory = new File(folderPath);
		 * SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM"); Map<String,
		 * List<File>> mapFiles = new TreeMap<>();
		 * 
		 * if (directory.exists()) { File[] listFiles = directory.listFiles(); for (File
		 * listFile : listFiles) { List dateLst = new ArrayList<>(); String fileName =
		 * listFile.getName(); fileName = fileName.substring(0,
		 * fileName.lastIndexOf(".log")); String date =
		 * fileName.substring(fileName.length() - 10); for (File File : listFiles) { if
		 * (File.getName().contains(date)) { dateLst.add(File.getName());
		 * 
		 * }
		 * 
		 * } mapFiles.put(date, dateLst); } } mapFiles.entrySet().forEach(entry -> {
		 * //System.out.println(entry.getKey() + " " + entry.getValue()); });
		 */
		
		String OS = System.getProperty("os.name");
		if(OS.toUpperCase().contains("WINDOWS"))
			System.out.println("It is windows Server : "+OS);
		else if (OS.toUpperCase().contains("LINUX"))
			System.out.println("It is Linus Server : "+OS);
}
}
