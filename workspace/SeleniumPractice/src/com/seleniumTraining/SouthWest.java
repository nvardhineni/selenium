package com.seleniumTraining;

import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterClass;

public class SouthWest {
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
		driver.get(Parameter.getProperty("Southwest"));
	}
	@BeforeTest
	public void beforeTest() {
	}
	@Test(priority=1)
	public void ExistingCustomer() throws InterruptedException 
	{
		//driver.get(Parameter.getProperty("Southwest"));
		//  WebElement Account = (new WebDriverWait(driver, 10))
		//  .until(ExpectedConditions.presenceOfElementLocated(By.xpath(Object.getProperty("Account&Lists")))); 
		driver.findElement(By.xpath(Object.getProperty("FlightTab"))).click();
		driver.findElement(By.xpath(Object.getProperty("Twoway"))).click();
		Thread.sleep(100);
		driver.findElement(By.xpath(Object.getProperty("originToSendKeys"))).sendKeys("bwi");
		driver.findElement(By.xpath(Object.getProperty("destToSendKeys"))).sendKeys("DAL");
		driver.findElement(By.xpath(Object.getProperty("OriginDate"))).click();
		driver.findElement(By.xpath(Object.getProperty("OriginCalendar"))).click();
		driver.findElement(By.xpath(Object.getProperty("DestDate"))).click();
		driver.findElement(By.xpath(Object.getProperty("destCalendar"))).click();
		driver.findElement(By.xpath(Object.getProperty("Guests"))).click();
		driver.findElement(By.xpath(Object.getProperty("guestsAdd"))).click();
		driver.findElement(By.xpath(Object.getProperty("Seniors"))).click();
		driver.findElement(By.xpath(Object.getProperty("Seniorsadd"))).click();
		
		driver.findElement(By.xpath(Object.getProperty("Search"))).click();
		Thread.sleep(1000);
	    WebElement PriceChart=driver.findElement(By.xpath(Object.getProperty("priceTable")));
	    List<WebElement> datarows= PriceChart.findElements(By.xpath(Object.getProperty("numberofrows")));
		System.out.println("no.of rows in the table:" +datarows.size());
		int minValue=0;
		int index = 0;
		//int i=0;
		for(int i=0;i<datarows.size();i++)
		{
			List<WebElement> price = datarows.get(i).findElements(By.tagName("td"));
			String columnValue = price.get(price.size() - 1).getText();
			System.out.println("i is " + i + " and column value is " + columnValue);
			if(!columnValue.trim().contains("Sold Out"))
			{

				String Price=columnValue.replace("$", "");
				int Pricecolumn=Integer.parseInt(Price);

				if(Pricecolumn<minValue || minValue == 0)
				{
					System.out.println("replacing mminValue " + minValue + " with priceColumn " + Pricecolumn + " in index " + i );
					minValue=Pricecolumn;
					index = i;
				}
			}

		}
		
		System.out.println("row number" +  index +" has the lowest price of:" + minValue );
		List<WebElement> price = datarows.get(index).findElements(By.tagName("td"));
		price.get(price.size() - 1).click();
	
   
		
		
		Add_Log.info("Successfully executed the  Testcase");
		
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