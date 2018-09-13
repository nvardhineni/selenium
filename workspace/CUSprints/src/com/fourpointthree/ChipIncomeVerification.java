package com.fourpointthree;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchSessionException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.Wait;
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

public class ChipIncomeVerification extends LoginBase {
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
		FilePath = TestLoginExcel;		
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
//		boolean yesSession = false;
//		while(!yesSession){
//			yesSession = true;
//
//			try {
//				loadWebBrowser();
//				//To go to website
//				driver.get(parameter.getProperty("siteURL"));	
//			}catch(NoSuchSessionException e){
//				yesSession = false;
//			}
//			if(yesSession){
//				break;
//			}
//		}
	}

	@Test(dataProvider="LoginData")
	public void TestCase(String username, String password, String mfaUsername, String mfaPassword, String applicationId, String income, String monthlyIncome, String reason) throws InterruptedException{

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
//			CULogin Login= new CULogin();
//			Login.Credentials(username, password, mfaUsername, mfaPassword);
//			WebElement Marketplace=getElementByXPath("marketplaceCUlink");
//			Marketplace.click();
//			getElementByXPath("cmsApplication").click();
//
//			driver.manage().window().maximize();
//			try{
//				driver.switchTo().frame(getElementByXPath("iFrameConditionsPage"));
//			}
//			catch(NullPointerException e)
//			{
//			driver.switchTo().frame(getElementByXPath("iFrameConditionsPage2"));
//		   }
			WebElement appId = (new WebDriverWait(driver, 40))
					.until(ExpectedConditions.presenceOfElementLocated(By.xpath(Object.getProperty("applicationid")))); 
			appId.clear();
			appId.sendKeys(applicationId);

			Wait.until(ExpectedConditions.visibilityOf(getElementByXPath("applicationSearch"))).click();
				

				Wait.until(ExpectedConditions.visibilityOf(getElementByXPath("Subsidycalculatortab"))).click();
				WebElement subsidyTable=getElementByXPath("IncomeTable");
			//	List<WebElement> headers=subsidyTable.findElements(By.tagName("th"));
				int incomeColumn=1;
				

				List<WebElement> rows=subsidyTable.findElements(By.tagName("tr"));
//                    for(int i=0;i<headers.size();i++)
//			     	{
//					String  headerText=headers.get(i).getText();
//					if(headerText.equalsIgnoreCase("Annual Income"))
//					{
//						incomeColumn=i;
//					}
				
//					if(headerText.equalsIgnoreCase("Monthly Income"))
//					{
//						monthlyincomeColumn=i;
//						//System.out.println("Monthly income column number is:"+monthlyincomeColumn );
//					}
//					if(headerText.equalsIgnoreCase("Reason"))
//					{
//						reasonColumn=i;
//					}
//
//				}

				int j=1;
				
				List<WebElement> data=rows.get(j).findElements(By.tagName("td"));

				WebElement annualIncome=data.get(incomeColumn).findElement(By.tagName("div"));
				annualIncome.click();
				annualIncome.clear();
				annualIncome.sendKeys(income);
				Thread.sleep(1000);
				int timeout=2000;
				new WebDriverWait(driver, timeout).ignoring(StaleElementReferenceException.class).until(ExpectedConditions.elementToBeClickable(getElementByXPath("monthlyIncome"))).click();
			    new WebDriverWait(driver, timeout).ignoring(StaleElementReferenceException.class).until(ExpectedConditions.elementToBeClickable(getElementByXPath("monthlyIncome"))).clear();
			    new WebDriverWait(driver, timeout).ignoring(StaleElementReferenceException.class).until(ExpectedConditions.elementToBeClickable(getElementByXPath("monthlyIncome"))).sendKeys(monthlyIncome);	
				new WebDriverWait(driver, timeout).ignoring(StaleElementReferenceException.class).until(ExpectedConditions.elementToBeClickable(getElementByXPath("subsidyreasonstextbox"))).click();
				new WebDriverWait(driver, timeout).ignoring(StaleElementReferenceException.class).until(ExpectedConditions.elementToBeClickable(getElementByXPath("subsidyreasonstextbox"))).sendKeys(reason);
//				int attempts1 = 0;
//				while(attempts1 < 3) {
//					try {
//						WebElement monthlyIndividualIncome=getElementByXPath("monthlyIncome");
//						monthlyIndividualIncome.click();
//						monthlyIndividualIncome.clear();
//						monthlyIndividualIncome.sendKeys(monthlyIncome);
//
//						break;
//					} catch(StaleElementReferenceException e) {
//						System.out.println("State element reference exception 1");
//						//e.printStackTrace();
//					}
//					attempts1++;
//				}
//

//				}
				getElementByXPath("IncomeRecalculate").click();
				WebElement subsidyResults=null;
			
//						do
//						{
							 //subsidyResults=Wait.until(ExpectedConditions.visibilityOf(getElementByXPath("SubsidyResults")));
							 subsidyResults= new WebDriverWait(driver, timeout).ignoring(TimeoutException.class).until(ExpectedConditions.visibilityOf(getElementByXPath("SubsidyResults")));
//						}while(subsidyResults.getText().isEmpty());
					
				
				screenshot.pageScreenshot();
				getElementByXPath("applicationtabletab").click();
				WebElement sep=Wait.until(ExpectedConditions.visibilityOf(getElementByXPath("AddSEP")));
				sep.click();
				Wait.until(ExpectedConditions.visibilityOf(getElementByXPath("SEPConfirmationDiv")));
				//screenshot
				screenshot.pageScreenshot();
				getElementByXPath("AddSEPConfirm").click();

				Wait.until(ExpectedConditions.elementToBeClickable(getElementByXPath("ReviewFFMChanges"))).click();
				Wait.until(ExpectedConditions.visibilityOf(getElementByXPath("FFMconfirmationDiv")));
				//screenshot
				screenshot.pageScreenshot();
				Wait.until(ExpectedConditions.visibilityOf(getElementByXPath("confirmReviewFFMChangesButton"))).click();

				Wait.until(ExpectedConditions.visibilityOf(getElementByXPath("FFMChangesSuccess")));
				//screenshot
				screenshot.pageScreenshot();
				getElementByXPath("Notices&Documents").click();	
				Wait.until(ExpectedConditions.visibilityOf(getElementByXPath("currentNotices"))).click();
				WebElement currentnoticestable=getElementByXPath("currentnoticestable");
				List<WebElement> currentrows=currentnoticestable.findElements(By.tagName("tr"));
				List<WebElement> currentheaders=currentnoticestable.findElements(By.tagName("th"));
				int currentheadercolumn=0;
				for(int k=0;k<currentheaders.size();k++)
				{
					if(currentheaders.get(k).getText().replaceAll("\\n", "").contains("Download PDF"))
					{
						currentheadercolumn=k;
						//System.out.println("column number;"+k);
					}
				}
				int l=1;
				List<WebElement> currentdata=currentrows.get(l).findElements(By.tagName("td"));
				screenshot.pageScreenshot();
				System.out.println(currentrows.size());
				System.out.println(currentdata.get(currentheadercolumn).getText());
				String date=currentdata.get(currentheadercolumn).getText();
				//	Date todaysDate=date();
				String date2=date.replaceAll("\\[","").replaceAll("\\]","");
				//System.out.println(date2);

				String[] texts = date2.split("PDF ");
				String text = texts[1].substring(0, 10);
				date = text;
				System.out.println("file is updated on "+date);

				String todaysdate=date();
			//	System.out.println(todaysdate);

				if(todaysdate.equals(date))
				{
					System.out.println("dates are  same");
				}
				else
				{
					System.out.println("dates are not the same");
					Testfail=true;
				}

			
			getElementByXPath("applicationtabletab").click();
			Thread.sleep(1000);
			getElementByXPath("Unlock").click();
			Thread.sleep(3000);
			getElementByXPath("popupUnlockYes").click();
			Thread.sleep(2000);
		//	driver.switchTo().defaultContent();
			
		}
		catch(Exception e)
		{
			System.out.println("exception caught");
			e.printStackTrace();
			Testfail=true;
			s_assert.fail();

		}
		if(Testfail)
		{
			s_assert.assertAll();
		}

	}

	//This data provider method will return 4 column's data one by one in every Iteration.
	@DataProvider
	public Object[][] LoginData(){
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
			// enter actual result
			System.out.println("Result is :"+Result);
			SuiteUtility.WriteResultUtility(FilePath, TestCaseName, "Actual Result", DataSet+1, Result);
		}
		//At last make both flags as false for next data set.
		Testskip=false;
		Testfail=false;
	}

	//To report result as pass or fail for test cases In TestCasesList sheet.
	@AfterTest
	public void closeBrowser() throws ATUTestRecorderException {
//		//To Close the web browser at the end of test.
//		closeWebBrowser();

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
