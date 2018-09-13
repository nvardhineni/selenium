package com.fourpointthree;

import java.io.IOException;

import org.testng.SkipException;
import org.testng.annotations.BeforeSuite;

import com.TestSuiteBase.SuiteBase;
import com.Utility.Read_XLS;
import com.Utility.SuiteUtility;

public class LoginBase extends SuiteBase {

	Read_XLS FilePath = null;
	String SheetName = null;
	String SuiteName = null;
	String ToRunColumnName = null;	
	
	
	//This function will be executed before LoginAdmin's test cases to check SuiteToRun flag.
	@BeforeSuite
	public void checkSuiteToRun() throws IOException {		
		//Called init() function from SuiteBase class to Initialize .xls Files
		init();			
		//To set TestSuiteList.xls file's path In FilePath Variable.
		FilePath = TestSuiteListExcel;
		SheetName = "SuitesList";
		SuiteName = "fourpointthree";
		ToRunColumnName = "SuiteToRun";
		
		//Below given syntax will Insert log In applog.log file.
		Add_Log.info("Execution started for LoginAdminBase.");
		
		//If SuiteToRun !== "y" then TestLogin will be skipped from execution.
		if(!SuiteUtility.checkToRunUtility(FilePath, SheetName,ToRunColumnName,SuiteName)){	
			Add_Log.info("SuiteToRun = N for "+SuiteName+" So Skipping Execution.");
			//To report LoginAdmin as 'Skipped' In SuitesList sheet of TestSuiteList.xls If SuiteToRun = N.
			SuiteUtility.WriteResultUtility(FilePath, SheetName, "Skipped/Executed", SuiteName, "Skipped");
			//It will throw SkipException to skip test suite's execution and suite will be marked as skipped In testng report.
			throw new SkipException(SuiteName+"'s SuiteToRun Flag Is 'N' Or Blank. So Skipping Execution Of "+SuiteName);
		}
		//To report LoginAdmin as 'Executed' In SuitesList sheet of TestSuiteList.xls If SuiteToRun = Y.
		SuiteUtility.WriteResultUtility(FilePath, SheetName, "Skipped/Executed", SuiteName, "Executed");
		
	}
	
}
