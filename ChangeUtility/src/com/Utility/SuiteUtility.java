package com.Utility;

public class SuiteUtility {	
	
	public static boolean checkToRunUtility(Read_XLS xls, String sheetName, String ToRun, String testSuite){
				
		boolean Flag = false;		
		if(xls.retrieveToRunFlag(sheetName,ToRun,testSuite).equalsIgnoreCase("y")){
			Flag = true;
		}
		else{
			Flag = false;
		}
		return Flag;		
	}
	
	public static String[] checkToRunUtilityOfData(Read_XLS xls, String sheetName, String ColName, int rowNum){		
		return xls.retrieveToRunFlagTestData(sheetName,ColName, rowNum);		 	
	}
 
	public static Object[][] GetTestDataUtility(Read_XLS xls, String sheetName, int rowNumber){
	 	return xls.retrieveTestData(sheetName, rowNumber);	
	}
 
	public static boolean WriteResultUtility(Read_XLS xls, String sheetName, String ColName, int rowNum, String Result){			
		return xls.writeResult(sheetName, ColName, rowNum, Result);		 	
	}
 
	public static boolean WriteResultUtility(Read_XLS xls, String sheetName, String ColName, String rowName, String Result){			
		return xls.writeResult(sheetName, ColName, rowName, Result);		 	
	}
	
	public static boolean HighlightFailingColumns(Read_XLS xls, String sheetName, String ColumnNames, int rowNum){
		return xls.highlightFailingColumns(sheetName, ColumnNames, rowNum);
	}
}

