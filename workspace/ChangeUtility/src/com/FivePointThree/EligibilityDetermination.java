package com.FivePointThree;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchSessionException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.SkipException;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import com.Utility.CULogin;
import com.Utility.Read_XLS;
import com.Utility.ScreenshotUtility;
import com.Utility.SuiteUtility;

import atu.testrecorder.ATUTestRecorder;
import atu.testrecorder.exceptions.ATUTestRecorderException;

public class EligibilityDetermination extends FivePointThreeBase {
	Read_XLS FilePath = null;	
	String SheetName = null;
	String TestCaseName = null;	
	String ToRunColumnNameTestCase = null;
	String ToRunColumnNameTestData = null;
	String TestDataToRun[]=null;
	static boolean TestCasePass=true;
	static int DataSet=-1;	
	static boolean Testskip=false;
	static boolean Testfail=false;
	SoftAssert s_assert =null;
	String Result;
	int rowNum = -1;
	String failingColumns;
	ATUTestRecorder recorder;
	ScreenshotUtility screenshot=new ScreenshotUtility();
	


	@BeforeTest
	public void checkCaseToRun() throws IOException, ATUTestRecorderException {
		//Called init() function from SuiteBase class to Initialize .xls Files
		init();	
		//To set TestLogin.xls file's path In FilePath Variable.
		FilePath = TestLoginExcel2;		
		TestCaseName = this.getClass().getSimpleName();	
		//SheetName to check CaseToRun flag against test case.
		SheetName = "TestCasesList";
		//Name of column In TestCasesList Excel sheet.
		ToRunColumnNameTestCase = "CaseToRun";
		//Name of column In Test Case Data sheets.
		ToRunColumnNameTestData = "DataToRun";
		//Below given syntax will insert log in applog.log file.
		Add_Log.info(TestCaseName+" : Execution started.");

		//To check test case's CaseToRun = Y or N In related excel sheet.
		//If CaseToRun = N or blank, Test case will skip execution. Else it will be executed.
		if(!SuiteUtility.checkToRunUtility(FilePath, SheetName,ToRunColumnNameTestCase,TestCaseName)){	
			Add_Log.info(TestCaseName+" : CaseToRun = N for So Skipping Execution.");
			//To report result as skip for test cases In TestCasesList sheet.
			SuiteUtility.WriteResultUtility(FilePath, SheetName, "Pass/Fail/Skip", TestCaseName, "SKIP");
			//To throw skip exception for this test case.
			throw new SkipException(TestCaseName+"'s CaseToRun Flag Is 'N' Or Blank. So Skipping Execution Of "+TestCaseName);
		}
		//To retrieve DataToRun flags of all data set lines from related test data sheet.
		TestDataToRun = SuiteUtility.checkToRunUtilityOfData(FilePath, TestCaseName, ToRunColumnNameTestData, rowNum);
		DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy__hh_mm_ssaa");
		Date date = new Date();



		if(parameter.getProperty("recordSuite").equalsIgnoreCase("yes")){
			//create object for recorder
			recorder = new ATUTestRecorder(System.getProperty("user.dir")+"//TestVideos//", "TestVideo-"+TestCaseName+"-"+dateFormat.format(date), false);

			//start recording 
			recorder.start();
		}

		//To Initialize browser.
		boolean yesSession = false;
		while(!yesSession){
			yesSession = true;

			try {
				loadWebBrowser();
				//To go to website
				driver.get(parameter.getProperty("siteURL"));	
			}catch(NoSuchSessionException e){
				yesSession = false;
			}
			if(yesSession){
				break;
			}
		}
	}

	@Test(dataProvider="SEPFields")
	public void TestCase(String username, String password, String mfaUsername, String mfaPassword, String applicationId, String income, String reason, String reasonText ) throws InterruptedException{

		DataSet++;

		Result = "";
		failingColumns = "";

		//Created object of testng SoftAssert class.
		s_assert = new SoftAssert();

		//If found DataToRun = "N" for data set then execution will be skipped for that data set.
		if(!TestDataToRun[DataSet].equalsIgnoreCase("Y")){
			//If DataToRun = "N", Set Testskip=true.
			Testskip=true;
			throw new SkipException("DataToRun for row number "+DataSet+" Is No or Blank. So Skipping Its Execution.");
		}

		try {
			WebDriverWait Wait = new WebDriverWait(driver, 10);
			Actions action=new Actions(driver);
			CULogin Login= new CULogin();
			System.out.println("driver=" + driver);
			Login.Credentials(username, password, mfaUsername, mfaPassword);
			WebElement Marketplace=getElementByXPath("marketplaceCUlink");
			Marketplace.click();
			getElementByXPath("cmsApplication").click();
			//	driver.manage().window().maximize();
			try{
				driver.switchTo().frame(getElementByXPath("iFrameConditionsPage"));
			}
			catch(NullPointerException e)
			{
				driver.switchTo().frame(getElementByXPath("iFrameConditionsPage2"));
			}
			WebElement appId = (new WebDriverWait(driver, 40))
					.until(ExpectedConditions.presenceOfElementLocated(By.xpath(Object.getProperty("applicationid")))); 

			appId.sendKeys(applicationId);
			getElementByXPath("applicationSearch").click();
			Thread.sleep(1000);
			WebElement appdiv;
			try{
				 appdiv= Wait.until(ExpectedConditions.visibilityOf(getElementByXPath("applicationDiv")));
			}
			catch(NullPointerException e)
			{
				 appdiv= Wait.until(ExpectedConditions.visibilityOf(getElementByXPath("applicationDiv")));
			}
			WebElement appTable=appdiv.findElement(By.tagName("table"));
            List<WebElement> rows=appTable.findElements(By.tagName("tr"));
			
			
			for(int i=1;i<rows.size();i++)
			{
				WebElement data=rows.get(2).findElement(By.tagName("td"));
				data.findElement(By.tagName("a")).click();
				highLightElement(data);
				screenshot.pageScreenshot();
				unhighlightElement(data);
				
				
			}
			WebElement eligibilitiesTable;
			try{
			 eligibilitiesTable=Wait.until(ExpectedConditions.visibilityOf(getElementByXPath("EligibilityDeterminationtable")));
			}
			catch(NullPointerException e)
			{
				 eligibilitiesTable=Wait.until(ExpectedConditions.visibilityOf(getElementByXPath("EligibilityDeterminationtable")));
			}
			List<WebElement> Eheaders=eligibilitiesTable.findElements(By.tagName("th"));
			List<WebElement> Erows=eligibilitiesTable.findElements(By.tagName("tr"));
			
			for(int i=1;i<Erows.size();i++)
				{
				
				List<WebElement> Edata=Erows.get(i).findElements(By.tagName("td"));
				for(int j=0;j<Edata.size();j++)
				{
				
					WebElement edata=Edata.get(j);
					
//					System.out.println(edata.getText());
					//System.out.println("column number:"+j);
					if(edata.getText().trim().contains("Income"))
					{
						WebElement edata9=Edata.get(9);
						edata9.findElement(By.tagName("a")).click();
						List<WebElement> Erows1=eligibilitiesTable.findElements(By.tagName("tr"));
						List<WebElement> Eincome=Erows1.get(i+1).findElements(By.tagName("td"));
						Eincome.get(0).findElement(By.tagName("input")).sendKeys(income);
						Eincome.get(1).findElement(By.tagName("input")).sendKeys(reason);
						Eincome.get(2).findElement(By.tagName("span")).click();;
						
						
					}
					
					
				}	
				}
			
			
			getElementByXPath("saveandreruneligibility").click();
			if(income.equals(parameter.getProperty("incomen")))
			{
			WebElement saveSuccess=Wait.until(ExpectedConditions.visibilityOf(getElementByXPath("IncomeChangeFailure")));
			highLightElement(saveSuccess);
			screenshot.pageScreenshot();
			unhighlightElement(saveSuccess);
			System.out.println(saveSuccess.getText());
			Result=saveSuccess.getText();
			if(!saveSuccess.getText().contains(parameter.getProperty("incomeEligibilityN")))
			{
				Testfail=true;
			}
			}
			if(income.equals(parameter.getProperty("incomey")))
			{
				WebElement saveSuccess=Wait.until(ExpectedConditions.visibilityOf(getElementByXPath("IncomeChangeFailure")));
				System.out.println(saveSuccess.getText());
				highLightElement(saveSuccess);
				screenshot.pageScreenshot();
				unhighlightElement(saveSuccess);
				Result=saveSuccess.getText();
				if(!saveSuccess.getText().contains(parameter.getProperty("incomeEligibilityY")))
				{
					Testfail=true;
				}
				
			}
//			Wait.until(ExpectedConditions.elementToBeClickable(getElementByXPath("ReviewFFMchanges"))).click();;
//			Wait.until(ExpectedConditions.visibilityOf(getElementByXPath("personalFFMDiv")));
//			getElementByXPath("confirmReviewFFMChangesButton").click();
//			Thread.sleep(10000);
//			WebElement ffmSuccess=Wait.until(ExpectedConditions.visibilityOf(getElementByXPath("FFMChangesSuccess")));
//			System.out.println(ffmSuccess.getText());
//			if(!ffmSuccess.getText().contains(parameter.getProperty("FFMSuccess")))
//			{
//				Testfail=true;
//			}
//			WebElement appdiv1;
//			
//			try{
//				 appdiv1= Wait.until(ExpectedConditions.visibilityOf(getElementByXPath("applicationDiv")));
//			}
//			catch(NullPointerException e)
//			{
//				 appdiv1= Wait.until(ExpectedConditions.visibilityOf(getElementByXPath("applicationDiv")));
//			}
//			WebElement appTable1=appdiv1.findElement(By.tagName("table"));
//           List<WebElement> rows1=appTable1.findElements(By.tagName("tr"));
//			
//			
//			for(int i=1;i<rows1.size();i++)
//			{
//				WebElement data=rows1.get(2).findElement(By.tagName("td"));
//				highLightElement(data);
//				screenshot.pageScreenshot();
//				unhighlightElement(data);
//				data.findElement(By.tagName("a")).click();
//				
//				
//			}
//			WebElement eligibilitiesTable1;
//			try{
//			 eligibilitiesTable1=Wait.until(ExpectedConditions.visibilityOf(getElementByXPath("EligibilityDeterminationtable")));
//			}
//			catch(NullPointerException e)
//			{
//				 eligibilitiesTable1=Wait.until(ExpectedConditions.visibilityOf(getElementByXPath("EligibilityDeterminationtable")));
//			}
//			List<WebElement> Eheaders1=eligibilitiesTable1.findElements(By.tagName("th"));
//			List<WebElement> Erows1=eligibilitiesTable1.findElements(By.tagName("tr"));
//			
//			for(int i=1;i<Erows1.size();i++)
//				{
//				
//				List<WebElement> Edata1=Erows1.get(i).findElements(By.tagName("td"));
//				for(int j=0;j<Edata1.size();j++)
//				{
//				
//					WebElement edataA=Edata1.get(j);
//					
//					//System.out.println(edataA.getText());
//					//System.out.println("column number:"+j);
//					if(edataA.getText().trim().contains("Income"))
//					{
//						WebElement edata3=Edata1.get(2);
//						highLightElement(edata3);
//						screenshot.pageScreenshot();
//						unhighlightElement(edata3);
//						Result=edata3.getText();
//						System.out.println(Result);
//						
//					}
//					
//					
//					
//				}	
//				
//				
//			
//				}
//			if(!Integer.valueOf(Result).equals(Integer.valueOf(reasonText)))
//			{
//				Testfail=true;
//			}
			getElementByXPath("personalgotoapplicationpage").click();
			//Wait.until(ExpectedConditions.visibilityOf(getElementByXPath("applicationtabletab"));
			Thread.sleep(4000);
			Wait.until(ExpectedConditions.visibilityOf(getElementByXPath("Unlock"))).click();
			Thread.sleep(3000);
			getElementByXPath("popupUnlockYes").click();
			Thread.sleep(2000);
			driver.switchTo().defaultContent();
			getElementByXPath("AccountLogout").click();
		}
		
		catch(Exception e)
		{
			e.printStackTrace();
			Testfail=true;

		}
		if(Testfail)
		{
			s_assert.assertAll();
		}


	}
	//This data provider method will return 4 column's data one by one in every Iteration.
	@DataProvider
	public Object[][] SEPFields(){
		//To retrieve data from Data 1 Column,Data 2 Column,Data 3 Column and Expected Result column of Campaign data Sheet.
		//Last two columns (DataToRun and Pass/Fail/Skip) are ignored programatically when reading test data.
		return SuiteUtility.GetTestDataUtility(FilePath, TestCaseName, rowNum);
	}
	//@AfterMethod method will be executed after execution of @Test method every time.
	@AfterMethod
	public void reporterDataResults(){	
		if(Testskip){
			Add_Log.info(TestCaseName+" : Reporting test data set line "+(DataSet+1)+" as SKIP In excel.");
			//If found Testskip = true, Result will be reported as SKIP against data set line In excel sheet.
			SuiteUtility.WriteResultUtility(FilePath, TestCaseName, "Pass/Fail/Skip", DataSet+1, "SKIP");
		}
		else if(Testfail){
			Add_Log.info(TestCaseName+" : Reporting test data set line "+(DataSet+1)+" as FAIL In excel.");
			//To make object reference null after reporting in report.
			s_assert = null;
			//Set TestCasePass = false to report test case as fail in excel sheet.
			TestCasePass=false;	
			//If found Testfail = true, Result will be reported as FAIL against data set line In excel sheet.
			SuiteUtility.WriteResultUtility(FilePath, TestCaseName, "Pass/Fail/Skip", DataSet+1, "FAIL");	
			// enter actual result
			SuiteUtility.WriteResultUtility(FilePath, TestCaseName, "Actual Result", DataSet+1, Result);	

			// highlight failing columns
			SuiteUtility.HighlightFailingColumns(FilePath, TestCaseName, failingColumns, DataSet+1);
		}
		else{
			Add_Log.info(TestCaseName+" : Reporting test data set line "+(DataSet+1)+" as PASS In excel.");
			//If found Testskip = false and Testfail = false, Result will be reported as PASS against data set line in excel sheet.
			SuiteUtility.WriteResultUtility(FilePath, TestCaseName, "Pass/Fail/Skip", DataSet+1, "PASS");
			//SuiteUtility.WriteResultUtility(FilePath, TestCaseName, "Chip Percentage", DataSet+1, percentageValue);
			// enter actual result
			System.out.println(Result);
			SuiteUtility.WriteResultUtility(FilePath, TestCaseName, "Actual Result", DataSet+1, Result);
		}
		//At last make both flags as false for next data set.
		Testskip=false;
		Testfail=false;
	}

	//To report result as pass or fail for test cases In TestCasesList sheet.
	@AfterTest
	public void closeBrowser() throws ATUTestRecorderException {

			//	To Close the web browser at the end of test.
				closeWebBrowser();

		if(parameter.getProperty("recordSuite").equalsIgnoreCase("yes")){
			//stop recording
			recorder.stop();
		}

		if(TestCasePass){
			Add_Log.info(TestCaseName+" : Reporting test case as PASS In excel.");
			SuiteUtility.WriteResultUtility(FilePath, SheetName, "Pass/Fail/Skip", TestCaseName, "PASS");
		}
		else{
			Add_Log.info(TestCaseName+" : Reporting test case as FAIL In excel.");
			SuiteUtility.WriteResultUtility(FilePath, SheetName, "Pass/Fail/Skip", TestCaseName, "FAIL");

		}
	}
}




