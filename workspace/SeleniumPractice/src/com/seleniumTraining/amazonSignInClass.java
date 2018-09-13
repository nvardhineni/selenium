package com.seleniumTraining;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.AfterTest;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.Color;

public class amazonSignInClass {
	
	Properties Object = null;
	Logger Add_Log = null;
	Properties Parameter = null;
	FileInputStream fip;
	WebDriver driver=null;
	SoftAssert S_Assert=new SoftAssert();

	@BeforeClass
	public void beforeClass() throws IOException
	{
		System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir")+"//BrowserDrivers//chromedriver.exe");
		System.setProperty("webdriver.ie.driver", System.getProperty("user.dir")+"//BrowserDrivers//IEDriverServer.exe");
		Add_Log = Logger.getLogger("rootLogger");
		//Initialize Objects.properties file.
		Object = new Properties();

		try {
			fip = new FileInputStream(System.getProperty("user.dir")+"//src//com//properties//objects.properties");
			Object.load(fip);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//Initialize Objects.properties file.
		Parameter = new Properties();

		try {
			fip = new FileInputStream(System.getProperty("user.dir")+"//src//com//properties//parameter.properties");
			Parameter.load(fip);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Add_Log.info("Objects.properties file loaded successfully.");
	}

	@BeforeMethod
	public void beforeMethod()
	{
		driver=new ChromeDriver();
		Add_Log.info("Chrome driver has been Instantiated");
	}
	@BeforeTest
	public void beforeTest() {
	}
//	@Test(priority=3)
	public void ExistingCustomer() throws InterruptedException 
	{
		driver.get(Parameter.getProperty("AmazonURL"));
		//  WebElement Account = (new WebDriverWait(driver, 10))
		//  .until(ExpectedConditions.presenceOfElementLocated(By.xpath(Object.getProperty("Account&Lists")))); 
		WebElement Account=driver.findElement(By.xpath(Object.getProperty("Account&Lists")));
		Thread.sleep(1000);
		
		Actions action=new Actions(driver);
		action.moveToElement(Account).perform();;
		Add_Log.info("finding sigin element");
		 WebElement SignIn = (new WebDriverWait(driver, 30))
					  .until(ExpectedConditions.presenceOfElementLocated(By.xpath(Object.getProperty("SignIn")))); 
		//WebElement SignIn=driver.findElement(By.xpath(Object.getProperty("SignIn")));
		Add_Log.info("found sigin element");
		//action.moveToElement(SignIn).build().perform();
		SignIn.click();
	
		
		Add_Log.info("Amazon signin is clicked");
		Thread.sleep(3000);
		if((driver.getCurrentUrl()).contentEquals(Parameter.getProperty("signInURL")))
		{
			Add_Log.info("Amazon signin page is loaded");
		}
		else
		{
			Add_Log.info("Amazon signin page is not loaded");
		}
		Add_Log.info("Successfully executed the ExistingCustomer Testcase");
	}
	@Test(priority=1)
	public void newCustomer() throws InterruptedException 
	{
		driver.get(Parameter.getProperty("AmazonURL"));
		Thread.sleep(3000);
		WebElement Account=driver.findElement(By.xpath(Object.getProperty("Account&Lists")));
		Thread.sleep(3000);
		Actions action=new Actions(driver);
		action.moveToElement(Account).perform();
		WebElement NewCustomer=driver.findElement(By.xpath(Object.getProperty("StartHere")));
		// WebElement NewCustomer = (new WebDriverWait(driver, 30))
				//  .until(ExpectedConditions.presenceOfElementLocated(By.xpath(Object.getProperty("StartHere")))); 
		 //NewCustomer.click();
		action.moveToElement(NewCustomer);
		action.click().perform();
		if((driver.getCurrentUrl()).contentEquals(Parameter.getProperty("NewAccountSignUpURL")))
		{
			Add_Log.info("Amazon signup page is loaded");
		}
		else
		{
			Add_Log.info("Amazon signup page is not loaded");
		}
		Add_Log.info("Successfully executed the NewCustomer Testcase");
	}
	//@Test(priority=2)
	public void CustomerSignUp() throws InterruptedException 
	{
		driver.get(Parameter.getProperty("NewAccountSignUpURL"));
		Thread.sleep(3000);
		//To Check if the Username has the Autofocus
		WebElement name=driver.findElement(By.xpath(Object.getProperty("UserName")));
		String classes = name.getAttribute("class");
		S_Assert.assertTrue(classes.contains(Object.getProperty("focusClassForUsername")));
		//To Check the error is thrown when the username textbox is empty after submit
		WebElement submit=driver.findElement(By.xpath(Object.getProperty("Submitbutton")));
		submit.click();
		if(name.getText().isEmpty())
		{
			WebElement alertforName=driver.findElement(By.xpath(Object.getProperty("AlertforName")));

			if(alertforName.getText().equals(Object.getProperty("AlertforUsername")))
			{
				Add_Log.info("Successfully displayed the error message");
			}
			String classes1 = name.getAttribute("class");
			S_Assert.assertTrue(classes1.contains(Object.getProperty("errorClassForInputText")));
		}

		//To Check the error is thrown when the email textbox is empty after submit

		WebElement email=driver.findElement(By.xpath(Object.getProperty("emailAdd")));
		Add_Log.info("Successfully clicked the email");
		String classes2 = email.getAttribute("class");
		S_Assert.assertTrue(classes2.contains(Object.getProperty("errorClassForInputText")));
		S_Assert.assertAll();
		Add_Log.info("Successfully executed the NewCustomer Testcase");
	}
 public void Storingcookie()
 {
	 File file = new File("Cookies.data");							
     try		
     {		
         // Delete old file if exists
			file.delete();		
         file.createNewFile();			
         FileWriter fileWrite = new FileWriter(file);							
         BufferedWriter Bwrite = new BufferedWriter(fileWrite);							
         // loop for getting the cookie information 		
         for(Cookie ck : driver.manage().getCookies())							
         {		
             Bwrite.write((ck.getName()+";"+ck.getValue()+";"+ck.getDomain()+";"+ck.getPath()+";"+ck.getExpiry()+";"+ck.isSecure()));																									
             Bwrite.newLine();			
     }		
         Bwrite.flush();			
         Bwrite.close();			
         fileWrite.close();			
     }catch(Exception ex)					
     {		
         ex.printStackTrace();			
     }		
 }
 public void Loadcookies()
 {
	 try{
		 File file = new File("Cookies.data");							
     FileReader fileReader = new FileReader(file);							
     BufferedReader Buffreader = new BufferedReader(fileReader);							
     String strline;			
     while((strline=Buffreader.readLine())!=null){									
     StringTokenizer token = new StringTokenizer(strline,";");									
     while(token.hasMoreTokens()){					
     String name = token.nextToken();					
     String value = token.nextToken();					
     String domain = token.nextToken();					
     String path = token.nextToken();					
     Date expiry = null;					
     		
     String val;			
     if(!(val=token.nextToken()).equals("null"))
		{		
     	expiry = new Date(val);					
     }		
     Boolean isSecure = new Boolean(token.nextToken()).								
     booleanValue();		
     Cookie ck = new Cookie(name,value,domain,path,expiry,isSecure);																	
     driver.manage().addCookie(ck); // This will add the stored cookie to your current session					
     }		
     }
 }
     catch(Exception ex){					
     ex.printStackTrace();			
     }		
 }
	@AfterMethod
	public void afterMethod()
	{
		driver.close();
		// driver.navigate().back();
		Add_Log.info("Driver is closed");
	}

	@AfterTest
	public void afterTest() {
		// driver.quit();
	}
}
