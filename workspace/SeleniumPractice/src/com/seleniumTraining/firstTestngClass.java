package com.seleniumTraining;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;

public class firstTestngClass {
	
	Properties Object = null;
	Logger Add_Log = null;
	 Properties Parameter = null;
	 FileInputStream fip;
	 WebDriver driver=null;
	 
	
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
	

  @Test
  public void f() throws InterruptedException 
  {
	   
	  driver.get(Parameter.getProperty("YahooURL"));
		 Thread.sleep(1000);
		 WebElement element=driver.findElement(By.cssSelector(Object.getProperty("yahoomailLinkButton1")));
		 Thread.sleep(3000);
		 element.click();
		 Thread.sleep(3000);
		 Add_Log.info("Successfully executed the testcase");

	  
	  
  }
  @BeforeMethod
  public void beforeMethod()
  {
	   driver=new ChromeDriver();
	   Add_Log.info("Chrome driver has been Instantiated");
  }

  @AfterMethod
  public void afterMethod() {
  }

  @BeforeTest
  public void beforeTest() {
  }

  @AfterTest
  public void afterTest() {
	  driver.close();
  }

}
