package com.Utility;

import java.awt.AWTException;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MouseInfo;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import com.TestSuiteBase.SuiteBase;


public class ScreenshotUtility implements ITestListener {	
	
	//This method will execute before starting of Test suite.
	public void onStart(ITestContext tr) {	
		
	}

	//This method will execute, Once the Test suite is finished.
	public void onFinish(ITestContext tr) {
		
	}

	//This method will execute only when the test is pass.
	public void onTestSuccess(ITestResult tr) {
		//If screenShotOnPass = yes, call captureScreenShot.
		if(SuiteBase.parameter.getProperty("screenShotOnPass").equalsIgnoreCase("yes"))
		{
			captureScreenShot(tr,"pass");
		}
	}

	//This method will execute only on the event of fail test.
	public void onTestFailure(ITestResult tr) {		
		//If screenShotOnFail = yes, call captureScreenShot.
		if(SuiteBase.parameter.getProperty("screenShotOnFail").equalsIgnoreCase("yes"))
		{
			captureScreenShot(tr,"fail");
		}
	}

	// This method will execute before the main test start (@Test)
	public void onTestStart(ITestResult tr) {
		
	}

	// This method will execute only if any of the main test(@Test) get skipped
	public void onTestSkipped(ITestResult tr) {		
	}
	
	public void onTestFailedButWithinSuccessPercentage(ITestResult tr) {
	}
	
	//Function to capture screenshot.
	public static void captureScreenShot(ITestResult result, String status){	
		String destDir = "";
		String passfailMethod = result.getMethod().getRealClass().getSimpleName() + "." + result.getMethod().getMethodName();
		//To capture screenshot.
		File scrFile = ((TakesScreenshot) SuiteBase.driver).getScreenshotAs(OutputType.FILE);
		DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy__hh_mm_ssaa");
		//If status = fail then set folder name "screenshots/Failures"
		if(status.equalsIgnoreCase("fail")){
			destDir = "screenshots/Failures";
		}
		//If status = pass then set folder name "screenshots/Success"
		else if (status.equalsIgnoreCase("pass")){
			destDir = "screenshots/Success";
		}

		//To create folder to store screenshots
		new File(destDir).mkdirs();
		//Set file name with combination of test class name + date time.
		String destFile = passfailMethod+" - "+dateFormat.format(new Date()) + ".png";

		try {
			//Store file at destination folder location
			FileUtils.copyFile(scrFile, new File(destDir + "/" + destFile));
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//Function to capture browser screenshot.
	public void pageScreenshot(){	
		String className = getCallerClassName();
		className = className.replace("com.", "");
		String[] subdirs = className.split("\\.");
		String destDir = "";
		//String passfailMethod = className;
		//To capture screenshot.
		File scrFile = ((TakesScreenshot) SuiteBase.driver).getScreenshotAs(OutputType.FILE);
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd-hh_mm_ssaa");
		destDir = "screenshots/"+ subdirs[0] + "/" + subdirs[1];
		//To create folder to store screenshots
		new File(destDir).mkdirs();
		//Set file name with combination of test class name + date time.
		String destFile = subdirs[1] +" - "+dateFormat.format(new Date()) + ".png";

		try {
			//Store file at destination folder location
			FileUtils.copyFile(scrFile, new File(destDir + "/" + destFile));
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	} 

	public static String getCallerClassName() { 
		StackTraceElement[] stElements = Thread.currentThread().getStackTrace();
		for (int i=1; i<stElements.length; i++) {
			StackTraceElement ste = stElements[i];
			if (!ste.getClassName().equals(ScreenshotUtility.class.getName()) && ste.getClassName().indexOf("java.lang.Thread")!=0) {
				return ste.getClassName();
			}
		}
		return null;
	}
}