package com.seleniumTraining;

import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import com.utility.Cookies;
import com.utility.ScreenshotUtility;

import org.testng.annotations.BeforeMethod;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
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
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.AfterTest;

public class Amazoncookies {

	Properties Object = null;
	Logger Add_Log = null;
	Properties Parameter = null;
	FileInputStream fip;
	WebDriver driver = null;
	SoftAssert S_Assert = new SoftAssert();
	Cookies cookie;
	ScreenshotUtility screenshot;
	String itemName=null;

	@BeforeClass
	public void beforeClass() throws IOException, AWTException {
		System.setProperty("webdriver.chrome.driver",
				System.getProperty("user.dir") + "//BrowserDrivers//chromedriver.exe");

		Add_Log = Logger.getLogger("rootLogger");
		//cookie = new Cookies(driver, Add_Log);
		// Initialize Objects.properties file.
		Object = new Properties();

		try {
			fip = new FileInputStream(System.getProperty("user.dir") + "//src//com//properties//objects.properties");
			Object.load(fip);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Initialize Objects.properties file.
		Parameter = new Properties();

		try {
			fip = new FileInputStream(System.getProperty("user.dir") + "//src//com//properties//parameter.properties");
			Parameter.load(fip);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		Add_Log.info("Objects.properties file loaded successfully.");
	}

	@BeforeMethod
	public void beforeMethod() {
		driver = new ChromeDriver();
		cookie = new Cookies(driver, Add_Log);
		Add_Log.info("Chrome driver has been Instantiated");
		screenshot=new ScreenshotUtility(Parameter,driver);
	}

	@BeforeTest
	public void beforeTest() {

	}

	@Test(priority=1)
	public void Login() throws AWTException, InterruptedException {
		driver.get(Parameter.getProperty("signInURL"));
		WebElement Email=driver.findElement(By.xpath(Object.getProperty("SigninEmailAdd")));
		Email.sendKeys(Parameter.getProperty("Username"));

		WebElement pwd=driver.findElement(By.xpath(Object.getProperty("SignInpwd")));
		pwd.sendKeys(Parameter.getProperty("password"));

		WebElement submit=driver.findElement(By.xpath(Object.getProperty("SigninSubmit")));
		submit.click();
		cookie.Storingcookie();
		
		Robot r=new Robot();

		String CurrentURL=driver.getCurrentUrl();
		Add_Log.info(CurrentURL);
		/* String SearchURL=Parameter.getProperty("SearchPageURL");
	 if(SearchURL.equals(CurrentURL))
	 {
		 Add_Log.info("successfully signed in");

	   }
	 else
	 {
		  Add_Log.info("not Signed in");
	 }*/
		WebElement Search=driver.findElement(By.xpath(Object.getProperty("SearchTextbox")));
		Search.sendKeys(Parameter.getProperty("SearchKeyword"));

		r.keyPress(KeyEvent.VK_ENTER);
		r.keyRelease(KeyEvent.VK_ENTER);
		Thread.sleep(1000);
		WebElement Item=driver.findElement(By.xpath(Object.getProperty("Item")));
		 itemName=Item.getText();
		Add_Log.info(itemName);
		WebElement Addtocart=driver.findElement(By.xpath(Object.getProperty("AddToCart")));
		
		Addtocart.click();
		
		screenshot.pageScreenshot();
		
		
		
		

	}
	@Test(priority=2)
	public void Search() throws AWTException, InterruptedException {
		driver.get(Parameter.getProperty("AmazonURL"));
		cookie.Loadcookies(driver);
		driver.get(Parameter.getProperty("AmazonURL"));
		WebElement Cart=driver.findElement(By.xpath(Object.getProperty("cart")));
		Cart.click();
		Thread.sleep(3000);
		//WebElement ItemName=driver.findElement(By.xpath(Object.getProperty("ItemName")));
		WebElement ItemName = (new WebDriverWait(driver, 40))
				  .until(ExpectedConditions.presenceOfElementLocated(By.xpath(Object.getProperty("ItemName")))); 
		String brandname=ItemName.getText();
		Add_Log.info(brandname);
		if(itemName.equals(brandname))
		{
			Add_Log.info("Correct item in the cart");
		}
		else
		{
			Add_Log.info("Incorrect item in the cart");
		}
		
		
	}

	@AfterMethod
	public void afterMethod() {
		driver.close();
		// driver.navigate().back();
		Add_Log.info("Driver is closed");
	}

	@AfterTest
	public void afterTest() {
		driver.quit();
	}

}
