package com.vision.wb;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class javacall {
	public static void main(String[] args) {
		Process proc;
		String cmd = "java -jar E:\\\\Java_Exces\\\\Jar_files\\\\RA_GenBuildExe.jar CD 01 EOD_ETL_START 1 09-Aug-2025 Y E:\\\\Java_Exces\\\\Logs\\\\EOD_ETL_START_1_CD_01_2025-08-20.log 9999";
		try {
			proc = Runtime.getRuntime().exec(cmd);
			BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()));
			while ((reader.readLine()) != null) {
			}
//			StringBuilder output = new StringBuilder();
//			try (BufferedReader errorReader = new BufferedReader(new InputStreamReader(proc.getErrorStream()))) {
//				String errLine;
//				while ((errLine = errorReader.readLine()) != null) {
//					output.append("ERR: ").append(errLine).append(System.lineSeparator());
//				}
//			}
			proc.waitFor();
			int outputStatus = proc.exitValue();
//			System.out.println("Error Stream : "+output);
			System.out.println(outputStatus);
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
