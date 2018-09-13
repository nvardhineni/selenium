package com.FivePointThree;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchSessionException;
import org.openqa.selenium.StaleElementReferenceException;
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

public class CSR extends FivePointThreeBase {
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
	
	PDFTextStripper pdfStripper = null;
	PDDocument pdDoc = null;
	String parsedText = null;
	List<String> Results = new ArrayList<String>();
	File recentlyDownloadedFile;
	WebElement data;
	boolean headerExists=false;

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
	public void TestCase(String username, String password, String mfaUsername, String mfaPassword, String applicationId, String segmentNum, String csrValue) throws InterruptedException{

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
			boolean edn=false;
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
			Thread.sleep(10000);
			try{
				Thread.sleep(10000);
				WebElement policiesTab = (new WebDriverWait(driver, 40))
					.until(ExpectedConditions.presenceOfElementLocated(By.xpath(Object.getProperty("policies"))));
			policiesTab.click();
			}
			catch(Exception e){WebElement policiesTab = (new WebDriverWait(driver, 40))
					.until(ExpectedConditions.presenceOfElementLocated(By.xpath(Object.getProperty("policies"))));
			policiesTab.click();
			}
			
			Thread.sleep(1000);
	
	
		int index=0;
		
			WebElement policiesDiv= getElementByXPath("apppoliciesDiv");
			//WebElement HealthcarePoliciestable=policiesDiv.findElement(By.xpath(Object.getProperty("Healthcarepolicies1sttable")));
            List<WebElement> policiesTable=policiesDiv.findElements(By.tagName("table"));
            System.out.println(policiesTable.size());
            int columnNum=0;
			for(int i=0;i<policiesTable.size();i++)
			{
				
				List<WebElement> policiesTabledata=policiesTable.get(i).findElements(By.tagName("td"));
				List<WebElement> policiesTableheader=policiesTable.get(i).findElements(By.tagName("th"));
				for(int j=0;j<policiesTabledata.size();j++)
				{
					//System.out.println(policiesTabledata.get(j).getText());
					if(policiesTabledata.get(j).getText().contains(segmentNum))
					{
						System.out.println("Segment number matches");
						policiesTabledata.get(j).findElement(By.tagName("a")).click();
						Thread.sleep(1000);
						
					
					}
				}
					if(i==2)
					{
						System.out.println("reading the second table to check CSR Values");
						
						List<WebElement> policiesTablerows=policiesTable.get(i).findElements(By.tagName("tr"));
						for(int k=0;k<policiesTableheader.size();k++)
						{
							if(policiesTableheader.get(k).getText().contains(parameter.getProperty("CSRcolumn")))
							{
								columnNum=k;
								System.out.println("CSR column header value is:"+columnNum);
							}
							
						}
						for(int l=1;l<policiesTablerows.size();l++)
						{
							System.out.println(policiesTablerows.size()+ "number of rows");
							List<WebElement> policiesTabledata1=policiesTablerows.get(l).findElements(By.tagName("td"));
							System.out.println("number of columns:" +policiesTabledata1.size());
							System.out.println("csr value:" +policiesTabledata1.get(2).getText());
							action.clickAndHold(policiesTabledata1.get(columnNum).findElement(By.tagName("span"))).build().perform();;
							Thread.sleep(1000);
							screenshot.pageScreenshot();
							action.moveToElement(policiesTabledata1.get(columnNum).findElement(By.tagName("span")));
							screenshot.pageScreenshot();
							action.release();
							highLightElement(policiesTabledata1.get(columnNum));
							screenshot.pageScreenshot();
							System.out.println(policiesTabledata1.get(columnNum).getText());
							unhighlightElement(policiesTabledata1.get(columnNum));
							Result=policiesTabledata1.get(columnNum).getText();
							if(!policiesTabledata1.get(columnNum).getText().contains(csrValue))
							{
								Testfail=true;
							}
						}
					}
					
				}
		
				

			

             WebElement apptab1=Wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(Object.getProperty("applicationtabletab"))));
 			apptab1.click();
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






