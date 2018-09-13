package com.seleniumTraining;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;

public class firstSeleniumClass {
	
	
	public static Properties Object = null;
	public static Logger Add_Log = null;
	public static Properties Parameter = null;
	
	
	public static void main(String[] args) throws IOException, InterruptedException {
		
		System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir")+"//BrowserDrivers//chromedriver.exe");
		System.setProperty("webdriver.ie.driver", System.getProperty("user.dir")+"//BrowserDrivers//IEDriverServer.exe");
		
		
		Add_Log = Logger.getLogger("rootLogger");
		
		//Initialize Objects.properties file.
				Object = new Properties();
				FileInputStream fip;
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
		
		// TODO Auto-generated method stub
		 WebDriver driver=new ChromeDriver();
		 driver.get(Parameter.getProperty("YahooURL"));
		 Thread.sleep(1000);
		 WebElement element=driver.findElement(By.cssSelector(Object.getProperty("yahoomailLinkButton1")));
		 Thread.sleep(3000);
		 element.click();
		 Thread.sleep(3000);
		 Add_Log.info("Successfully executed the testcase");

		driver.close();
		 
		WebDriver driver1=new InternetExplorerDriver();
		driver1.get(Parameter.getProperty("YahooURL"));
		 Thread.sleep(1000);
		 WebElement element1=driver1.findElement(By.xpath(Object.getProperty("yahoomailLinkButton")));
		 Thread.sleep(3000);
		 element1.click();
		 Thread.sleep(3000);
		 Add_Log.info("Successfully executed the testcase");
		
		 driver1.close();

	}

}
