package com.Utility;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.asserts.SoftAssert;

import com.TestSuiteBase.SuiteBase;

public class CULogin extends SuiteBase {
	
	
	public void Credentials(String username, String password, String mfaUsername, String mfaPassword) throws InterruptedException{
	
		
		WebDriverWait wait = new WebDriverWait(driver, 10);
		ScreenshotUtility screenshot = new ScreenshotUtility();
		screenshot.pageScreenshot();
		boolean loginIsOkay = false;
		while(!loginIsOkay){
			loginIsOkay = true;
			try {
				getElementByXPath("cmsuserid").sendKeys(username);
				//getElementByXPath("nextButton").click();
				getElementByXPath("cmsuserpwd").sendKeys(password);
				selectFromDropdown("cmsMfaDevicetype", "Email");
				//getElementByXPath("sendEmail").click();
				Thread.sleep(7000);
				String code = "";
				do{
                    getElementByXPath("cmssendMFAcode").click();
                    Thread.sleep(2000);
			
                   }while(!getElementByXPath("cmsEmailSuccess").getText().contains(parameter.getProperty("emailSuccess")));
					Robot robot;

					try {
						robot = new Robot();
						robot.keyPress(KeyEvent.VK_CONTROL);
						robot.keyPress(KeyEvent.VK_T);
						robot.keyRelease(KeyEvent.VK_CONTROL);
						robot.keyRelease(KeyEvent.VK_T);
					} catch (AWTException e) {
						e.printStackTrace();
					}
					Thread.sleep(1000);
					
					wait.until(ExpectedConditions.numberOfWindowsToBe(2));
					List<String> windowHandles = new ArrayList<String>(driver.getWindowHandles());
					System.out.println(windowHandles.size());
					driver.switchTo().window(windowHandles.get(1));
					driver.get(parameter.getProperty("gmailURL"));
					if(getElementByXPath("gmailSignInNavBar")!=null){
						getElementByXPath("gmailSignInNavBar").click();
					}
//					try {
//						getElementByXPath("gmailUsername").sendKeys(mfaUsername);
//						getElementByXPath("gmailNextButton").click();
//						getElementByXPath("gmailPwd").sendKeys(mfaPassword);
//						if(getElementByXPath("gmailStaySignedIn").isSelected()){
//							getElementByXPath("gmailStaySignedIn").click();
//						}
//						getElementByXPath("gmailSignInButton").click();
//					}catch(Exception e){
						if(getElementByXPath("gmailUsername1")!= null){
							getElementByXPath("gmailUsername1").sendKeys(mfaUsername);
							getElementByXPath("gmailNextButton1").click();
						}
						getElementByXPath("gmailPwd1").sendKeys(mfaPassword);
						wait.until(ExpectedConditions.elementToBeClickable(getElementByXPath("gmailSignInButton1")));
						//}
				  try{ getElementByXPath("gmailSignInButton1").click();
					
				  }
				  catch(Exception e)
				  {
					  getElementByXPath("gmailSignInButton1").click();
				  }
					
					boolean unreadEmail = false;
					while(!unreadEmail){
						Thread.sleep(5000);
					//	unreadEmail = true;
						try {
							List<WebElement> unreademails = driver.findElements(By.xpath("//*[@class='zF']"));
							if(unreademails.size()>0){
								for(int i=0;i<unreademails.size();i++){
									if(unreademails.get(i).getText().contains(parameter.getProperty("donotreply"))){
										unreadEmail = true;
									}
								}
							}
						}catch(Exception e){
							unreadEmail = false;
						}
						if(unreadEmail){
							break;
						}
					}
					WebElement email = null;
					boolean isNotStale = false;
					while(!isNotStale){
						isNotStale = true;
						try{
							do{	

								//List<WebElement> a = driver.findElements(By.xpath("//*[@class='yW']/span"));
								List<WebElement> b = driver.findElements(By.xpath("//*[@class='y6']"));
								List<String> bs = new ArrayList<String>();
								for(int i=0;i<b.size();i++){
									bs.add(b.get(i).getText());
								}
								Thread.sleep(2000);
								
								email = b.get(0);
								//System.out.println(b.get(0).getText());
								if(email.getText().contains(parameter.getProperty("mfaSecurityCode")))
								{
									//System.out.println(b.get(0).getText());
									Thread.sleep(2000);
									wait.until(ExpectedConditions.visibilityOf(email));
									email.click();
									break;
								}
								else
								{
									getElementByXPath("gmailrefresh").click();
								}
							}while(!email.getText().contains(parameter.getProperty("mfaSecurityCode")));
						}
						catch(Exception e){
							isNotStale = false;
						}
						if(isNotStale){
							break;
						}
					}
					Thread.sleep(2000);
					WebElement maindiv = getElementByXPath("recentEmailDiv");
					List<WebElement> divelements = maindiv.findElements(By.xpath(Object.getProperty("divList")));
					scrollDown();
					WebElement div = divelements.get(divelements.size()-1);

					//	System.out.println(div.getText());
							String[] texts = div.getText().split("for MFA is ");
							System.out.println(texts[1]);
							String text = texts[1].substring(0, 6);
							code = text;
					System.out.println("Security code is:"+ code);
					getElementByXPath("gmailDelete").click();
					getElementByXPath("gmailTryToLogoutButton").click();
					Thread.sleep(1000);
					//WebElement signOut=wait.until(ExpectedConditions.visibilityOf(getElementByXPath("gmailSignOutButton")));
					getElementByXPath("gmailSignOutButton").click();
					//signOut.click();	
					//driver.close();
					try {
						System.out.println("trying to close the driver");
						Thread.sleep(1000);
						driver.close();
					}catch(WebDriverException e){
						System.out.println("unable to close the driver");
						driver.switchTo().alert().accept();
						//driver.close();
					}
					driver.switchTo().window(windowHandles.get(0));
					Thread.sleep(1000);
					getElementByXPath("cmsMFASecuritycode").sendKeys(code);
					Thread.sleep(500);
					boolean isChecked=getElementByXPath("terms&conditions1").isSelected();
					System.out.println(isChecked);
					if(!isChecked)
						{
						  getElementByXPath("terms&conditions").click();
						}
					
					getElementByXPath("cmslogin").click();
					Thread.sleep(3000);
					if(checkIfElementIsNull("errorMessage") != false){
						loginIsOkay = false;
					}
				
			}catch(Exception e){
				e.printStackTrace();
				loginIsOkay = false;
			}
			if(loginIsOkay){
				break;
			}
		}
			Thread.sleep(4000);

	}
	
}

