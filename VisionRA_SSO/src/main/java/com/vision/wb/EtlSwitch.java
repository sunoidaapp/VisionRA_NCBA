package com.vision.wb;

import org.springframework.stereotype.Component;

@Component
public class EtlSwitch {
	public boolean etlService = true;
	public int etlServMaxThreadCnt = 1;
	public int etlServRunThreadCnt = 0;
	public String genericBuildServiceJar = "RA_GenBuildExe.jar";
	public String chargeEngineJar = "RAChargeEngine.jar";
	public String taxEngineJar = "RATaxEngine.jar";
	public String concessionEngineJar = "RAConcessionEngine.jar";
	public String reconEngineJar = "RARecon.jar";
	public String taxReconEngineJar = "RATaxRecon.jar";
	public String historyBuildJar = "RAHistoryBuild.jar";
	
	public boolean buildService = true;
	public int buildServMaxThreadCnt = 1;
	public int buildServRunThreadCnt = 0;
	public String adfBuildServiceJar = "ADFAcq.jar";
}

