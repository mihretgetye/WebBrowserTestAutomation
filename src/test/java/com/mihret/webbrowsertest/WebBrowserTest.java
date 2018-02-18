package com.mihret.webbrowsertest;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.StaleElementReferenceException;

public class WebBrowserTest {
	
	private WebDriver webDriver;
	private String urlUnderTest;
	private String driver = "";
	private String pathToChromeBinary;
	String driverExePath;
	
	public boolean findAndTakeAction(By by, String typeOfAction, String text) {
        boolean succeeded = false;
        for (int i = 0; i < 2; i++){
        	try {
        		
        		if (typeOfAction.equalsIgnoreCase("click")){
        			 webDriver.findElement(by).click();
        		}
        		else if (typeOfAction.equalsIgnoreCase("clear")){
        			webDriver.findElement(by).clear();
        		}
        		else if (typeOfAction.equalsIgnoreCase("sendKeys")){
        			webDriver.findElement(by).sendKeys(text);
        		}
        		else if (typeOfAction.equalsIgnoreCase("selectByVisibleText")) {
        			Select select = new Select(webDriver.findElement(by));
        			select.selectByVisibleText(text);
        		}
        		
                succeeded = true;
                break;
            } catch(StaleElementReferenceException e) {
            }
        }
        return succeeded;
	}
	
	
	@Before
	public void setUp() throws Exception {
		ClassLoader classLoader = getClass().getClassLoader();
		Properties configProperties = new Properties();
		try {
		    configProperties.load(classLoader.getResourceAsStream("config.properties"));
		    driverExePath = configProperties.getProperty("driverExePath", "");
		    urlUnderTest = configProperties.getProperty("urlUnderTest", "");
		    driver = configProperties.getProperty("driver");
		    pathToChromeBinary = configProperties.getProperty("chromeBinaryPath", "");
		} 
		catch (IOException ex) {
		    ex.printStackTrace();
		}
		if (driverExePath.isEmpty()){
			System.out.println("No web driver executable path is provided. Thus, it is assumed that the relevant web driver executable path is added to the system PATH");
		}
		if (driver.equalsIgnoreCase("firefox")){
			if (!driverExePath.isEmpty()){
				//System.setProperty("webdriver.firefox.marionette", driverExePath);
				System.setProperty("webdriver.gecko.driver", driverExePath);
				System.setProperty("webdriver.firefox.marionette", "false");
				//DesiredCapabilities capabilities = DesiredCapabilities.firefox();
				//capabilities.setCapability("marionette", true);
				//WebDriver driver = new FirefoxDriver(capabilities);
			}
			webDriver = new FirefoxDriver();
			//webDriver = new FirefoxDriver(capabilities);
		}
		else if (driver.equalsIgnoreCase("chrome")){
			if (!driverExePath.isEmpty()){
				System.setProperty("webdriver.chrome.driver", driverExePath);
			}
			ChromeOptions options = new ChromeOptions();
			if (!pathToChromeBinary.isEmpty()){
				options.setBinary(pathToChromeBinary);
			}
			options.addArguments("--start-maximized");
			webDriver = new ChromeDriver(options);
		}

		if (urlUnderTest.isEmpty()) {
			System.out.println("No url is provided. Thus default url which is: 'https://www.amazon.com' will be used.");
			urlUnderTest = "https://www.amazon.com";
		}

		webDriver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
    }

	@Test
	public void testWebBrowser() throws Exception {
		webDriver.get(urlUnderTest);
		By id = By.id("twotabsearchtextbox");
		findAndTakeAction(id, "clear", "");
		By searchId = By.id("twotabsearchtextbox");
		findAndTakeAction(searchId, "sendKeys", "Nikon");
		By css = By.cssSelector("input.nav-input");
		findAndTakeAction(css, "click", "");
		By sortId = By.id("sort");
		findAndTakeAction(sortId, "selectByVisibleText", "Price: High to Low");
		Thread.sleep(10000);
		By by = By.xpath("//li[@id='result_1']/div/div/div/div[2]/div/div/a/h2");
		findAndTakeAction(by, "click", "");
		String secondProduct = webDriver.getTitle();
		String msgUponFailure = "The product topic of the second product does not contain the text 'Nikon D3X'";
		//assertTrue(msgUponFailure, secondProduct.contains("Nikon D3X"));
		assertFalse(msgUponFailure, secondProduct.contains("Nikon D3X"));
		
	}
	
	@After
	public void tearDown() throws Exception {
		webDriver.quit();
	}

}
