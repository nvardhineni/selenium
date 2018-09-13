package com.FourPointSix;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchSessionException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
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

public class EligibilityResultsTable extends FourPointSixBase {
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
	//String percentageValue=null;
	PDFTextStripper pdfStripper = null;
	PDDocument pdDoc = null;
	String parsedText = null;
	List<String> Results = new ArrayList<String>();
	File recentlyDownloadedFile;

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
	public void TestCase(String username, String password, String mfaUsername, String mfaPassword, String applicationId) throws InterruptedException{

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
			
			getElementByXPath("Notices&Documents").click();	
			Wait.until(ExpectedConditions.visibilityOf(getElementByXPath("olderNotices"))).click();
			WebElement oldernoticestable=getElementByXPath("oldernoticestable");
			List<WebElement> currentrows=oldernoticestable.findElements(By.tagName("tr"));
			List<WebElement> currentdata=currentrows.get(1).findElements(By.tagName("td"));
			WebElement eligibilityNotices=currentdata.get(0).findElement(By.tagName("a"));
			
			eligibilityNotices.click();
			Thread.sleep(5000);
			//getElementByXPath("eligibilitypdf").click();
			//currentdata.get(0).click();
			 System.out.println(eligibilityNotices.getText());
//			eligibilityPdf.click();
			boolean isFileFound=false;
			int waitCounter=0;
		    while(!isFileFound)
		    {
		    	System.out.println(parameter.getProperty("downloadPath"));
		    recentlyDownloadedFile=getLatestFilefromDir(parameter.getProperty("downloadPath"));
		    System.out.println("recently dowloaded file name:"+recentlyDownloadedFile.getName());
		    System.out.println(recentlyDownloadedFile);
		   
			Thread.sleep(1000);
			
		    if((recentlyDownloadedFile.exists())&&(recentlyDownloadedFile.getName().contains("EligibilityResultsNotice")))
		    {
		    	 try {
						Robot robot = new Robot();
						robot.keyPress(KeyEvent.VK_CONTROL);
						robot.keyPress(KeyEvent.VK_T);
						robot.keyRelease(KeyEvent.VK_CONTROL);
						robot.keyRelease(KeyEvent.VK_T);
					} catch (AWTException e) {
						e.printStackTrace();
					}
		    	System.out.println("Entered the loop");
		    	Wait.until(ExpectedConditions.numberOfWindowsToBe(2));
		    		 List<String> windowHandles = new ArrayList<String>(driver.getWindowHandles());
					driver.switchTo().window(windowHandles.get(1));
			
					driver.get(recentlyDownloadedFile.getAbsolutePath().concat("#page=2&&zoom=50"));
		            Thread.sleep(1000);
		             screenshot.pageScreenshot();
		             Thread.sleep(5000);
			             
		             pdDoc = PDDocument.load(new File(recentlyDownloadedFile.getAbsolutePath()));
		             
				    	System.out.println(recentlyDownloadedFile.getAbsolutePath());
				    	pdfStripper = new PDFTextStripper();
					//cosDoc = parser.getDocument();
					pdfStripper = new PDFTextStripper();
					
					pdfStripper.setStartPage(1);
					pdfStripper.setEndPage(2);	
					parsedText = pdfStripper.getText(pdDoc);
				
						if (pdDoc != null)
							pdDoc.close();
				System.out.println("+++++++++++++++++");
				System.out.println(parsedText);
				System.out.println("+++++++++++++++++");
		             
		             try {
							driver.close();
						}catch(WebDriverException e){
							driver.switchTo().alert().accept();
						}
		             
		            
		driver.switchTo().window(windowHandles.get(0));
		 try {
				Robot robot = new Robot();
				robot.keyPress(KeyEvent.VK_CONTROL);
				robot.keyPress(KeyEvent.VK_T);
				robot.keyRelease(KeyEvent.VK_CONTROL);
				robot.keyRelease(KeyEvent.VK_T);
			} catch (AWTException e) {
				e.printStackTrace();
			}
 	System.out.println("Entered the loop");
 	Wait.until(ExpectedConditions.numberOfWindowsToBe(2));
 		 List<String> windowHandles1 = new ArrayList<String>(driver.getWindowHandles());
			driver.switchTo().window(windowHandles1.get(1));
			
			driver.get(recentlyDownloadedFile.getAbsolutePath().concat("#page=1&&zoom=50"));
         Thread.sleep(1000);
          screenshot.pageScreenshot();
          Thread.sleep(5000);
          try {
				driver.close();
			}catch(WebDriverException e){
				driver.switchTo().alert().accept();
			}
          isFileFound=true;
          driver.switchTo().window(windowHandles.get(0));
		    }
       
		    else
		    {
		    	isFileFound=false;
		    	System.out.println("Download to be completed");
		    }
		    Thread.sleep(10000);
		    waitCounter++;
		    if(waitCounter>25)
		    {
		    	isFileFound=true;
		    	System.out.println("File Not Downloaded");
		    }
		    
		     }
		    if(recentlyDownloadedFile.exists())
            {
		    	recentlyDownloadedFile.delete();
            }
		    try{
				driver.switchTo().frame(getElementByXPath("iFrameConditionsPage"));
			}
			catch(NullPointerException e)
			{
				driver.switchTo().frame(getElementByXPath("iFrameConditionsPage2"));
			}
		   WebElement eligibilityXml=currentdata.get(1).findElement(By.tagName("a"));
		   eligibilityXml.click();
			screenshot.pageScreenshot();
		//	eligibilityXml.click();
			boolean isFileFound1=false;
			int waitCounter1=0;
		    while(!isFileFound1)
		    {
		    	System.out.println(parameter.getProperty("downloadPath"));
		   recentlyDownloadedFile=getLatestFilefromDir(parameter.getProperty("downloadPath"));
		    System.out.println("recently dowloaded file name:"+recentlyDownloadedFile.getName());
		    System.out.println(recentlyDownloadedFile);
		   
			Thread.sleep(5000);
		    if(recentlyDownloadedFile.getName().contains("xml")&&recentlyDownloadedFile.exists())
		    {
		    	 try {
						Robot robot = new Robot();
						robot.keyPress(KeyEvent.VK_CONTROL);
						robot.keyPress(KeyEvent.VK_T);
						robot.keyRelease(KeyEvent.VK_CONTROL);
						robot.keyRelease(KeyEvent.VK_T);
					} catch (AWTException e) {
						e.printStackTrace();
					}
				Wait.until(ExpectedConditions.numberOfWindowsToBe(2));
		    		 List<String> windowHandles = new ArrayList<String>(driver.getWindowHandles());
					driver.switchTo().window(windowHandles.get(1));
					
					driver.get(recentlyDownloadedFile.getAbsolutePath());
		            Thread.sleep(10000);
		             screenshot.pageScreenshot();
		             
		             try {
							driver.close();
						}catch(WebDriverException e){
							driver.switchTo().alert().accept();
						}
		           
		             
		                  
		isFileFound1=true;
		
		driver.switchTo().window(windowHandles.get(0));
		  DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
          DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
          Document doc = dBuilder.parse(recentlyDownloadedFile);
          doc.getDocumentElement().normalize();
          System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
          NodeList nList = doc.getElementsByTagName("mcu:result");
          System.out.println("----------------------------");
          System.out.println(nList.getLength());
          for (int temp = 0; temp < nList.getLength(); temp++) {
         	 Node nNode = nList.item(temp);
         	 System.out.println("\nCurrent Element :" + nNode.getNodeName());
         	
         		 if (nNode.getNodeType() == Node.ELEMENT_NODE) {
         			 Element eElement = (Element) nNode;
         			 System.out.println("Results :" 
         					 + eElement.getTextContent());
         			 Results.add(" "+eElement.getTextContent());
         			
         					 
         		 }
         	 }
		    }
       
		    else
		    {
		    	isFileFound1=false;
		    	System.out.println("Download to be completed");
		    }
		    Thread.sleep(10000);
		    waitCounter1++;
		    if(waitCounter1>25)
		    {
		    	isFileFound1=true;
		    	System.out.println("File Not Downloaded");
		    }
		    
		    }
		    if(recentlyDownloadedFile.exists())
            {
            	recentlyDownloadedFile.delete();
            }
		    String parsedText1 = StringUtils.normalizeSpace(parsedText);
            parsedText1 = parsedText1.replace((char) 160, ' ');
            parsedText1 = StringUtils.deleteWhitespace(parsedText1);

		    for(String s : Results) { 
		    	System.out.println(s);
		    	s = s.replace((char) 160, ' ');
                s = StringUtils.normalizeSpace(s);
                s = StringUtils.deleteWhitespace(s);

		    	System.out.println(parsedText1);
		  // 	Assert.assertTrue(parsedText.contains(s));
		      if(parsedText1.contains(s)) { 
		    	  System.out.println("pdf has the text required");
					Testfail=false; 
		       } else { 
		    	   Testfail=true;
		       } 
		    } 
		    

		    try{
				driver.switchTo().frame(getElementByXPath("iFrameConditionsPage"));
			}
			catch(NullPointerException e)
			{
				driver.switchTo().frame(getElementByXPath("iFrameConditionsPage2"));
			}
			getElementByXPath("applicationtabletab").click();
			getElementByXPath("Unlock").click();
			Thread.sleep(3000);
			getElementByXPath("popupUnlockYes").click();
			Thread.sleep(2000);
			driver.switchTo().defaultContent();
			getElementByXPath("AccountLogout").click();
			Thread.sleep(1000);
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
