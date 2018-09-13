package com.TestSuiteBase;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.SecureRandom;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Point;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.Utility.Read_XLS;
import com.google.common.base.Function;

public class SuiteBase {	
	//public static Read_XLS TestSuiteListExcel=null;
	
	public static Read_XLS TestSuiteListExcel=null;
	public static Read_XLS TestLoginExcel=null;
	public static Read_XLS TestLoginExcel2=null;
	public static Read_XLS TestLoginExcel3=null;
	
	
	public static Logger Add_Log = null;
	public boolean BrowseralreadyLoaded=false;
	public static Properties parameter = null;
	public static Properties Object = null;
	public static WebDriver driver=null;
	public static WebDriver ExistingchromeBrowser;
	public static WebDriver ExistingmozillaBrowser;
	public static WebDriver ExistingIEBrowser;
	private String elementToHighlight = null;
	
	protected static By title = By.xpath("//title");
	private int defaultTimeout = 10000;
	 private static final Random RANDOM = new SecureRandom();
	
	public void init() throws IOException{
		//To Initialize logger service.
		Add_Log = Logger.getLogger("rootLogger");				
				
		//Please change file's path strings bellow If you have stored them at location other than bellow.
		//Initializing Test Suite List(TestSuiteList.xls) File Path Using Constructor Of Read_XLS Utility Class.
		//TestSuiteListExcel = new Read_XLS(System.getProperty("user.dir")+"\\src\\com\\stta\\ExcelFiles\\TestSuiteList.xls");
		
		TestSuiteListExcel = new Read_XLS(System.getProperty("user.dir")+"\\src\\com\\ExcelFiles\\TestSuiteList.xls");
		TestLoginExcel = new Read_XLS(System.getProperty("user.dir")+"\\src\\com\\ExcelFiles\\FourPointSeven.xls");
		TestLoginExcel2 = new Read_XLS(System.getProperty("user.dir")+"\\src\\com\\ExcelFiles\\FivePointThree.xls");
		TestLoginExcel3 = new Read_XLS(System.getProperty("user.dir")+"\\src\\com\\ExcelFiles\\FourPointOne.xls");
		
		//Bellow given syntax will Insert log In applog.log file.
		Add_Log.info("All Excel Files Initialised successfully.");
		
		//Initialize Param.properties file.
		parameter = new Properties();
		FileInputStream fip = new FileInputStream(System.getProperty("user.dir")+"//src//com//Properties//parameter.properties");
		parameter.load(fip);
		Add_Log.info("parameter.properties file loaded successfully.");		
	
		//Initialize Objects.properties file.
		Object = new Properties();
		fip = new FileInputStream(System.getProperty("user.dir")+"//src//com//Properties//object.properties");
		Object.load(fip);
		Add_Log.info("object.properties file loaded successfully.");
	}
	
	public void loadWebBrowser(){
		//Check If any previous webdriver browser Instance Is exist then run new test In that existing webdriver browser Instance.
			if(parameter.getProperty("testBrowser").equalsIgnoreCase("Mozilla") && ExistingmozillaBrowser!=null){
				System.setProperty("webdriver.gecko.driver", System.getProperty("user.dir")+"//BrowserDrivers//geckodriver.exe");
				driver = ExistingmozillaBrowser;
				return;
			}else if(parameter.getProperty("testBrowser").equalsIgnoreCase("chrome") && ExistingchromeBrowser!=null){
				System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir")+"//BrowserDrivers//chromedriver.exe");
				driver = ExistingchromeBrowser;
				return;
			}else if(parameter.getProperty("testBrowser").equalsIgnoreCase("IE") && ExistingIEBrowser!=null){
				System.setProperty("webdriver.ie.driver", System.getProperty("user.dir")+"//BrowserDrivers//IEDriverServer.exe");
				driver = ExistingIEBrowser;
				return;
			}		
		
		
			if(parameter.getProperty("testBrowser").equalsIgnoreCase("Mozilla")){
				//To Load Firefox driver Instance.
				System.setProperty("webdriver.gecko.driver", System.getProperty("user.dir")+"//BrowserDrivers//geckodriver.exe");
				driver = new FirefoxDriver();
				ExistingmozillaBrowser=driver;
				Add_Log.info("Firefox Driver Instance loaded successfully.");
				
			}else if(parameter.getProperty("testBrowser").equalsIgnoreCase("Chrome")){
				//To Load Chrome driver Instance.
				System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir")+"//BrowserDrivers//chromedriver.exe");
				String currentDir = System.getProperty("user.dir");
		        System.out.println("Current dir using System:" +currentDir);
				
				HashMap<String, Object> chromePrefs = new HashMap<String, Object>();
		        chromePrefs.put("profile.default_content_settings.popups", 0);
     	        chromePrefs.put("profile.default_content_setting_values.notifications", 2);
				chromePrefs.put("download.prompt_for_download", "false");
				
				/* Set file save to directory. */
				chromePrefs.put("download.default_directory",parameter.getProperty("downloadPath"));
				chromePrefs.put("safebrowsing.enabled", "true"); 
		        ChromeOptions options = new ChromeOptions();
		        options.addArguments("--start-maximized");
		        options.addArguments("--browser.download.folderList=2");
		        options.addArguments("--browser.helperApps.neverAsk.saveToDisk=application/pdf");
		        
		        //chromePrefs.put("plugins.plugins_disabled", new String[] {
		        //	    "Chrome PDF Viewer"
		        //	});
		       // options.setExperimentalOption("excludeSwitches",Arrays.asList("test-type"));
		       // options.addArguments("--disable-notifications");
		       options.setExperimentalOption("prefs", chromePrefs);
		       // driver=new ChromeDriver(options);
		        DesiredCapabilities cap = DesiredCapabilities.chrome();
		        cap.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
		        cap.setCapability(ChromeOptions.CAPABILITY, options);
		        cap.setBrowserName(DesiredCapabilities.chrome().getBrowserName());
				driver = new ChromeDriver(cap);
				ExistingchromeBrowser=driver;
				Add_Log.info("Chrome Driver Instance loaded successfully.");
				
			}else if(parameter.getProperty("testBrowser").equalsIgnoreCase("IE")){
				//To Load IE driver Instance.
				System.setProperty("webdriver.ie.driver", System.getProperty("user.dir")+"//BrowserDrivers//IEDriverServer.exe");
				DesiredCapabilities cap = new DesiredCapabilities();
				cap.setCapability(InternetExplorerDriver.IE_ENSURE_CLEAN_SESSION, true);
				cap.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS,true);
				cap.setCapability("nativeEvents", true);
				cap.setCapability("ignoreProtectedModeSettings",1);
				cap.setCapability("IntroduceInstabilityByIgnoringProtectedModeSettings",true);
				cap.setCapability("nativeEvents",true);
				cap.setCapability("browserFocus",true);
				cap.setCapability("ignoreZoomSetting", true);
				cap.setCapability("requireWindowFocus","true");
				driver = new InternetExplorerDriver();
				ExistingIEBrowser=driver;
				Add_Log.info("IE Driver Instance loaded successfully.");
				
			}			
			driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
			//driver.manage().timeouts().pageLoadTimeout(10, TimeUnit.SECONDS);
			driver.manage().window().maximize();			
	}
	
	public void closeWebBrowser(){
		try {
			Thread.sleep(3000);
			driver.close();
			driver.quit();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		//null browser Instance when close.
		ExistingchromeBrowser=null;
		ExistingmozillaBrowser=null;
		ExistingIEBrowser=null;
		
	}
	
	//getElementByXPath function for static xpath
	public WebElement getElementByXPath(String Key){
		try{
			//This block will find element using Key value from web page and return It.
			return driver.findElement(By.xpath(Object.getProperty(Key)));
		}catch(Throwable t){
			//If element not found on page then It will return null.
			Add_Log.debug("Object not found for key --"+Key);
			return null;
		}
	}
	
	//getElementByXPath function for static xpath
	public WebElement getElementByPassingXPath(String Key){
		try{
			//This block will find element using Key value from web page and return It.
			return driver.findElement(By.xpath(Key));
		}catch(Throwable t){
			//If element not found on page then It will return null.
			Add_Log.debug("Object not found for key --"+Key);
			return null;
		}
	}
	
	//getElementByXPath function for dynamic xpath
	public WebElement getElementByXPath(String Key1, int val, String key2){
		try{
			//This block will find element using values of Key1, val and key2 from web page and return It.
			return driver.findElement(By.xpath(Object.getProperty(Key1)+val+Object.getProperty(key2)));
		}catch(Throwable t){
			//If element not found on page then It will return null.
			Add_Log.debug("Object not found for custom xpath");
			return null;
		}
	}
	
	//Call this function to locate element by ID locator.
	public WebElement getElementByID(String Key){
		try{
			return driver.findElement(By.id(Object.getProperty(Key)));
		}catch(Throwable t){
			Add_Log.debug("Object not found for key --"+Key);
			return null;
		}
	}
	
	//Call this function to locate element by Name Locator.
	public WebElement getElementByName(String Key){
		try{
			return driver.findElement(By.name(Object.getProperty(Key)));
		}catch(Throwable t){
			Add_Log.debug("Object not found for key --"+Key);
			return null;
		}
	}
	
	//Call this function to locate element by cssSelector Locator.
	public WebElement getElementByCSS(String Key){
		try{
			return driver.findElement(By.cssSelector(Object.getProperty(Key)));
		}catch(Throwable t){
			Add_Log.debug("Object not found for key --"+Key);
			return null;
		}
	}
	
	//Call this function to locate element by ClassName Locator.
	public WebElement getElementByClass(String Key){
		try{
			return driver.findElement(By.className(Object.getProperty(Key)));
		}catch(Throwable t){
			Add_Log.debug("Object not found for key --"+Key);
			return null;
		}
	}
	
	//Call this function to locate element by tagName Locator.
	public WebElement getElementByTagName(String Key){
		try{
			return driver.findElement(By.tagName(Object.getProperty(Key)));
		}catch(Throwable t){
			Add_Log.debug("Object not found for key --"+Key);
			return null;
		}
	}
	
	//Call this function to locate element by link text Locator.
	public WebElement getElementBylinkText(String Key){
		try{
			return driver.findElement(By.linkText(Object.getProperty(Key)));
		}catch(Throwable t){
			Add_Log.debug("Object not found for key --"+Key);
			return null;
		}
	}
	
	//Call this function to locate element by link text Locator.
		public WebElement getElementByPassingText(String Key){
			try{
				return driver.findElement(By.linkText(Key));
			}catch(Throwable t){
				Add_Log.debug("Object not found for key --"+Key);
				return null;
			}
		}
	
	//Call this function to locate element by partial link text Locator.
	public WebElement getElementBypLinkText(String Key){
		try{
			return driver.findElement(By.partialLinkText(Object.getProperty(Key)));
		}catch(Throwable t){
			Add_Log.debug("Object not found for key --"+Key);
			return null;
		}
	}
	
	// Fetch element by
	public WebElement fetchElement(By by) {
	    return driver.findElement(by);
	}
	
	// Finds the element by the given selector
	protected WebElement find(By by)
	{
		long startTime = System.currentTimeMillis();
		
		while(driver.findElements(by).size() == 0)
		{
			if (System.currentTimeMillis() - startTime > defaultTimeout)
			{
                String errMsg = String.format(
						"Could not find element %s after %d seconds.",
						by,
						defaultTimeout);
				throw new NoSuchElementException(errMsg);
			}
		}
		return driver.findElement(by);
	}
	
	//  Gets everything inside the html tags for the given selector.
	protected String getInnerHtml(By by)
	{
		return find(by).getAttribute("innerHTML");
	}
	
	// Gets the title of the current page.
	public String getTitle()
	{
		return getInnerHtml(title);
	}
	
	// Gets the URL of the current page.
	public String getUrl()
	{
		return driver.getCurrentUrl();
	}
	
	// Finds all elements by the given selector.
	protected Collection<WebElement> findAll(WebElement webElement, String key)
	{
		By by = By.xpath(Object.getProperty(key));
		return driver.findElements(by);
	}
	
	public WebElement fluentWait(final By locator) {
	    Wait<WebDriver> wait = new FluentWait<WebDriver>(driver)
	            .withTimeout(30, TimeUnit.SECONDS)
	            .pollingEvery(5, TimeUnit.SECONDS)
	            .ignoring(NoSuchElementException.class);

	    WebElement foo = wait.until(new Function<WebDriver, WebElement>() {
	        public WebElement apply(WebDriver driver) {
	            return driver.findElement(locator);
	        }
	    });

	    return  foo;
	};
	
	public WebElement fluentWaitforElement(WebElement element, int timoutSec, int pollingSec) {

	    FluentWait<WebDriver> fWait = new FluentWait<WebDriver>(driver).withTimeout(timoutSec, TimeUnit.SECONDS)
	    .pollingEvery(pollingSec, TimeUnit.SECONDS)
	    .ignoring(NoSuchElementException.class, TimeoutException.class);

	    for (int i = 0; i < 2; i++) {
	        try {
	            fWait.until(ExpectedConditions.visibilityOf(element));
	            fWait.until(ExpectedConditions.elementToBeClickable(element));
	        } 
	        catch (Exception e) {

	            System.out.println("Element Not found trying again - " + element.toString().substring(70));
	            e.printStackTrace();
	        }
	    }

	    return element;
	}
	
	// wait until element is visible.
	protected void waitUntil(String key){
		WebDriverWait wait = new WebDriverWait(driver, 10);
		wait.until(ExpectedConditions.visibilityOfElementLocated((By)getElementByXPath(key)));
	}

	// Pauses play until a given element becomes visible.
	protected void waitForElementToExist(String key)
	{
		WebElement webElement = getElementByXPath(key);
		waitForElementToExist(webElement, key, defaultTimeout);
	}

	// Pauses play until a given element becomes visible.
	protected void waitForElementToExist(WebElement webElement, String key, long timeout)
	{
		long startTime = System.currentTimeMillis();
		
		while(findAll(webElement, key).size() == 0)
		{
			if (System.currentTimeMillis() - startTime > timeout)
			{
				Assert.fail(String.format("Could not find element '%s' after %s seconds",
						webElement.toString(),
						timeout / 1000));
			}
		}
	}
	
	// click on element
	public void clickElement(String key){
		WebElement element = getElementByXPath(key);

		Actions actions = new Actions(driver);

		actions.moveToElement(element).click().perform();
	}
	
	public void stubbornWait(final WebElement element){
		// Waiting 30 seconds for an element to be present on the page, checking
		// for its presence once every 5 seconds.
		Wait<WebDriver> stubbornWait = new FluentWait<WebDriver>(driver)
		    .withTimeout(30, TimeUnit.SECONDS)
		    .pollingEvery(5, TimeUnit.SECONDS)
		    .ignoring(NoSuchElementException.class)
		    .ignoring(StaleElementReferenceException.class);
		
		stubbornWait.until(new Function<WebDriver, WebElement>() {
		    public WebElement apply(WebDriver driver) {
		        return element;
		    }
		});
	}
	
	// hover over element
	public void hoverOverElement(String key) throws InterruptedException{
		if(parameter.getProperty("testBrowser").equalsIgnoreCase("mozilla") ){
			mozillaHoverOverElement(key);
			//return hover;
		}
		By by = By.xpath(Object.getProperty(key));
		Actions action = new Actions(driver);
		WebElement element = driver.findElement(by);
		action.moveToElement(element);
		action.build().perform();
	}
	
	public void hoverOverElement(WebElement element) throws InterruptedException{
		if(parameter.getProperty("testBrowser").equalsIgnoreCase("mozilla") ){
			mozillaHoverOverElement(element);
			//return hover;
		}
		Actions action = new Actions(driver);
		action.moveToElement(element).build().perform();
		Thread.sleep(2000);
	}
	
	// hover over element for mozilla
	private boolean mozillaHoverOverElement(String key) {
		By by = By.xpath(Object.getProperty(key));
		WebElement element = driver.findElement(by);
		Point p = element.getLocation();
        int x = p.getX();
        int y = p.getY();

        Dimension d = element.getSize();
        int h = d.getHeight();
        int w = d.getWidth();

        Robot robot = null;
        try {
			robot = new Robot();
			robot.mouseMove(x + (w / 2), y + (h / 2) + 70);
	        robot.mousePress(InputEvent.BUTTON1_MASK);
			return true;
		} catch (AWTException e) {
			e.printStackTrace();
			return false;
		}
        
	}
	
	// hover over element for mozilla
		private boolean mozillaHoverOverElement(WebElement element) {
			Point p = element.getLocation();
	        int x = p.getX();
	        int y = p.getY();

	        Dimension d = element.getSize();
	        int h = d.getHeight();
	        int w = d.getWidth();

	        Robot robot = null;
	        try {
				robot = new Robot();
				robot.mouseMove(x + (w / 2), y + (h / 2) + 70);
		        robot.mousePress(InputEvent.BUTTON1_MASK);
				return true;
			} catch (AWTException e) {
				e.printStackTrace();
				return false;
			}
	        
		}

	// selects from dropdown by visible text
	public void selectFromDropdown(String dropdownKey, String visibleText){
		Select select = new Select(getElementByXPath(dropdownKey));
		
		select.selectByVisibleText(visibleText);
	}
	
	// selects multiple from dropdown by visible text
	public void multipleSelectFromDropdown(String dropdownKey, String visibleText){
		Select select = new Select(getElementByXPath(dropdownKey));
		String[] options = seperateByCommas(visibleText);
		Robot robot = null;
		try {
			robot = new Robot();
		} catch (AWTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		robot.keyPress(KeyEvent.VK_CONTROL);
		for(int i=0;i<options.length;i++){
			select.selectByVisibleText(options[i]);
		}
		robot.keyRelease(KeyEvent.VK_CONTROL);

	}
	
	public void scrollDown(){
		JavascriptExecutor jse = (JavascriptExecutor)driver;
		jse.executeScript("window.scrollBy(0,250)", "");
	}
	
	public void scrollUp(){
		JavascriptExecutor jse = (JavascriptExecutor)driver;
		jse.executeScript("window.scrollBy(0,-250)", "");
	}
	
	public boolean checkIfFileDownloaded(String fileName){
		boolean flag = false;
		File dir = new File(parameter.getProperty("downloadPath"));
		File[] files = dir.listFiles();
		
		if (files == null || files.length == 0) {
	        flag = false;
	    }
		while(!flag){
			for (int i = 0; i < files.length; i++) {
				System.out.println(i + ": " + files[i].getName());
		    	if(files[i].getName().contains(fileName)) {
		    		flag=true;
		    		File file =files[i];
		    		file.delete();
		    		break;
		    	}
		    }
		}
		return flag;
	}
	
	public boolean checkIfElementIsNull(String element){
		WebElement elem = null;
		elem = getElementByXPath(element);
		if(elem == null){
			return false;
		}else {
			return true;
		}
		
	} 
	
	public String modifyDateLayout(String dateFormat, String inputDate) throws ParseException{
		Date date = new SimpleDateFormat(dateFormat).parse(inputDate);
	    return new SimpleDateFormat("MM/dd/yyyy").format(date);
	}
	
	// modify date layout
	public String modifyDateLayout(String inputDate) throws ParseException{
	    Date date = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy").parse(inputDate);
	    return new SimpleDateFormat("MM/dd/yyyy").format(date);
	}
	
	// modify time layout
	public String modifyTimeLayout(String inputDate) throws ParseException{
	    Date date = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy").parse(inputDate);
	    System.out.println(new SimpleDateFormat("hh a").format(date));
	    return new SimpleDateFormat("h a").format(date);
	}
	
	// modify date layout for date pickers
	public String modifyDateLayoutForDatePicker(String inputDate) throws ParseException{
	    Date date = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy").parse(inputDate);
	    return new SimpleDateFormat("MMMM,dd,yyyy").format(date);
	}
	
	// change time format from 10:00:00 AM to 10 AM
	public String changeTimeFormat(String time){
		String regex;
		if(time.contains("A")){
			regex = "\\A";
		} else {
			regex = "\\P";
		}
		String[] parts = time.split(regex);
		String first = parts[0];
		String last = time.substring(time.length() - 2);
		String timeString = first + " " + last;
		System.out.println(timeString);
		return timeString;
	}
	
	// separate by commas
	public String[] seperateByCommas(String array){
		String[] parts = array.split("\\,");
		return parts;
	}
	
	// remove spaces and asterix
	public String removeSpacesAndAsterix(String word){
		word = word.replace("\n", "");
		word = word.replace("*", "");
		System.out.println(word);
		return word;
	}
	
	protected void highlightLabel(String element){
		unhighlightLastHighlightedElement(1);
		
		if(element.isEmpty()){
			return;
		} else if(element.contains(",")){
			elementToHighlight = element;
			String[] elements = seperateByCommas(element);
			for(int i=0;i<elements.length;i++){
				WebElement elem = getElementByXPath(elements[i]);
				JavascriptExecutor js = (JavascriptExecutor)driver;
			    js.executeScript("arguments[0].setAttribute('style', 'background: yellow; border: 2px solid red;');", elem);
			    js.executeScript("arguments[0].setAttribute('style','border: solid 2px black');", elem); 
			}
		} else {
			elementToHighlight = element;
			WebElement elem = getElementByXPath(element);
			JavascriptExecutor js = (JavascriptExecutor)driver;
		    js.executeScript("arguments[0].setAttribute('style', 'background: yellow; border: 2px solid red;');", elem);
		    js.executeScript("arguments[0].setAttribute('style','border: solid 2px black');", elem); 
		}
	}
	
	public void highLightElement(WebElement element){
		JavascriptExecutor js = (JavascriptExecutor)driver;
		js.executeScript("arguments[0].setAttribute('style','border: solid 2px black');", element); 
	}
	public void unhighlightElement(WebElement element) { 
		  JavascriptExecutor js=(JavascriptExecutor) driver;
		  js.executeScript("arguments[0].setAttribute('style','border: 0');", element);  
		 }
	
	protected void highlightTextBox(String element) {
		unhighlightLastHighlightedElement(2);
		
		if(element.isEmpty()){
			return;
		} else if(element.contains(",")){
			elementToHighlight = element;
			String[] elements = seperateByCommas(element);
			for(int i=0;i<elements.length;i++){
				WebElement elem = getElementByXPath(elements[i]);
				JavascriptExecutor js = (JavascriptExecutor)driver;
			    js.executeScript("arguments[0].setAttribute('style', 'background: yellow; border: 2px solid red;');", elem);
			    js.executeScript("arguments[0].setAttribute('style','border: solid 2px black');", elem); 
			}
		} else {
			elementToHighlight = element;
			WebElement elem = getElementByXPath(element);
			JavascriptExecutor js = (JavascriptExecutor)driver;
		    js.executeScript("arguments[0].setAttribute('style', 'background: yellow; border: 2px solid red;');", elem);
		    js.executeScript("arguments[0].setAttribute('style','border: solid 2px black');", elem); 
		}
	}

	public void unhighlightLastHighlightedElement(int number) {
		
		if(elementToHighlight != null){
			try {
				
				if(elementToHighlight.contains(",")){
					String[] elements = seperateByCommas(elementToHighlight);
					for(int i=0;i<elements.length;i++){
						WebElement elem = getElementByXPath(elements[i]);
						JavascriptExecutor js = (JavascriptExecutor)driver;
						switch(number){
						case 1:
							js.executeScript("arguments[0].setAttribute('style','border: 0');", elem); 
							break;
						case 2:
							js.executeScript("arguments[0].setAttribute('style','border: solid 1px grey');", elem); 
							break;
						}
					}
				} else {
					WebElement elem = getElementByXPath(elementToHighlight);
					JavascriptExecutor js = (JavascriptExecutor)driver;
					switch(number){
					case 1:
						js.executeScript("arguments[0].setAttribute('style','border: 0');", elem); 
						break;
					case 2:
						js.executeScript("arguments[0].setAttribute('style','border: solid 1px grey');", elem); 
						break;
					}
				}
				
			}catch(StaleElementReferenceException ignored){
				ignored.printStackTrace();
			} finally {
				elementToHighlight = null;
			}
		}
	}
	
	// Generates random text of given length using letters and digits
	public String generateRandomStringByLength(int length) {
		StringBuilder sb = new StringBuilder(length);
		for (int i = 0; i < length; i++) {
			int c = RANDOM.nextInt(62);
			if (c <= 9) {
				sb.append(String.valueOf(c));
			} else if (c < 36) {
				sb.append((char) ('a' + c - 10));
			} else {
				sb.append((char) ('A' + c - 36));
			}
		}
		return sb.toString();
	}
	
	// multiply given string to given length
	public String multiplyStringToDesiredLength(String longTextAndLength){
		String[] textAndLength = seperateByCommas(longTextAndLength);
		int length = Integer.parseInt(textAndLength[1]);
		String longText = textAndLength[0];
		String text = longText;
		while(text.length()<length){
			text+=longText;
		}
		return text;
	}
	
	// makes 4.0 a 4
	public String splitAfterDecimal(String number){
		String mainChapterNumber = number.split("\\.", 2)[0];
		return mainChapterNumber;
	}
	
	// scrolls element into view
	public void scrollIntoView(WebElement element){
		JavascriptExecutor je = (JavascriptExecutor) driver;
		je.executeScript("arguments[0].scrollIntoView(true);",element);
	}
	
	public void openInNewTab(String url){
		Robot robot;
		
		try {
			robot = new Robot();
			robot.keyPress(KeyEvent.VK_CONTROL);
			robot.keyPress(KeyEvent.VK_T);
			robot.keyRelease(KeyEvent.VK_CONTROL);
			robot.keyRelease(KeyEvent.VK_T);
		} catch (AWTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		WebDriverWait wait = new WebDriverWait(driver, 5);
		wait.until(ExpectedConditions.numberOfWindowsToBe(2));
		List<String> windowHandles = new ArrayList<String>(driver.getWindowHandles());
		driver.switchTo().window(windowHandles.get(1));
		driver.get(url);
	}
	public String date()
	{
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date date1 = new Date();
		System.out.println("today's date is"+dateFormat.format(date1));
		String dateinstringformat=dateFormat.format(date1);
		return dateinstringformat;

	}
	 public static boolean isValidDate(String inDate) {
		    SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
		   // dateFormat.setLenient(false);
		    try {
		      dateFormat.parse(inDate);
		    } catch (ParseException pe) {
		      return false;
		    }
		    return true;
		  }
	 public String percentage(String text)
	 {
         Pattern pattern = Pattern.compile("(\\d{1,2})\\.(\\d{2})\\%");
         Matcher matcher = pattern.matcher(text);

         if (matcher.find())
         {
               // System.out.println(">>>>>>>"+matcher.group()+"<<<<<");
                return matcher.group(); 
         }
         else {
             return null;
         }

	 }
	 public File getLatestFilefromDir(String dirPath){
		    File dir = new File(dirPath);
		    File[] files = dir.listFiles();
		    if (files == null || files.length == 0) {
		    
		        System.out.println("no files");
		        return null;
		    }

		    File lastModifiedFile = files[0];
		    System.out.println(files.length);
		    for (int i = 1; i < files.length; i++) {
		       if (lastModifiedFile.lastModified() < files[i].lastModified()) {
		           lastModifiedFile = files[i];
		       }
		    }
		    
		    return lastModifiedFile;
		}
	

}
