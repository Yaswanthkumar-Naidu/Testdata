package cares.cwds.salesforce.pom;

import java.io.IOException;
import java.time.LocalDateTime;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.PageFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codoid.products.exception.FilloException;

import cares.cwds.salesforce.common.utilities.ExcelUtils;
import cares.cwds.salesforce.common.utilities.Util;
import cares.cwds.salesforce.constants.ModuleConstants;
import cares.cwds.salesforce.constants.ScreenConstants;
import cares.cwds.salesforce.web.utilities.GenericLocators;
import cares.cwds.salesforce.web.utilities.Webkeywords;
import reportutilities.common.ReportCommon;
import reportutilities.extentmodel.PageDetails;
import reportutilities.model.TestCaseParam;
import testsettings.TestRunSettings;
import uitests.testng.common.CommonOperations;

public class ValidatePerson {

	private static final Logger logger =LoggerFactory.getLogger(ServicesCategory.class.getName());
	private WebDriver driver;
	ReportCommon exceptionDetails = new ReportCommon();
	Util util = new Util();
	GenericLocators genericLocators = null;

	String moduleName = ModuleConstants.COMMON;
	String screenName = ScreenConstants.VALIDATEPERSON;
	public ValidatePerson(){ }
	
	public ValidatePerson(WebDriver wDriver,TestCaseParam testCaseParam) 
	{
		initializePage(wDriver,testCaseParam);
	}
	
	public void initializePage(WebDriver wDriver,TestCaseParam testCaseParam) 
    {
    	 driver = wDriver;
         PageFactory.initElements(driver, this);
         ReportCommon testStepLogDetails = new ReportCommon(); 
         testStepLogDetails.logModuleAndScreenDetails(testCaseParam, moduleName, screenName);
         genericLocators = new GenericLocators(wDriver);
    }
	
	@FindBy(how = How.XPATH, using = "//p[@title='Screening ID']//parent::div//a")
	public WebElement clickScreeningIDLink;
	
	
	public void navigateToValidatePerson(TestCaseParam testCaseParam, String iteration)throws IOException  {
		PageDetails action = new PageDetails();
		LocalDateTime startTime= LocalDateTime.now();
		action.setPageActionName("Navigate to Validate Person");
		action.setPageActionDescription("Navigate to Validate Person");
		try {
			ExcelUtils.loadDataForTestScript("ValidatePerson", testCaseParam.getTestNGTestMethodName(), iteration);
			String clickValidatePersonTab = TestRunSettings.getExcelValue("ValidatePersonTab");
		
			Webkeywords.instance().click(driver, genericLocators.button(driver, "Validate Person"),clickValidatePersonTab, testCaseParam,action);
			
		}catch (Exception e) {
			logger.error("Failed == {} ", action.getPageActionDescription());
			exceptionDetails.logExceptionDetails(driver, testCaseParam, action.getPageActionName(), action.getPageActionDescription(), startTime,e);
		}
		
	}
		
	public void validatePersonDetails(TestCaseParam testCaseParam, String iteration) throws IOException, InterruptedException, FilloException  {
		PageDetails action = new PageDetails();
		LocalDateTime startTime= LocalDateTime.now();
		action.setPageActionName("Validate Person");
		action.setPageActionDescription("Validate Person");
		
		try {
			ExcelUtils.loadDataForTestScript("ValidatePerson", testCaseParam.getTestNGTestMethodName(), iteration);
			
			String searchBtn = TestRunSettings.getExcelValue("SearchBtn");
			String sexAtBirth = TestRunSettings.getExcelValue("SexAtBirth");
			String newPersonBtn = TestRunSettings.getExcelValue("NewPersonBtn");
			String validatePersonName = TestRunSettings.getExcelValue("ValidatePersonName");
			
			Webkeywords.instance().click(driver, genericLocators.button(driver, "Search"), searchBtn, testCaseParam, action);
			Webkeywords.instance().selectValueInputDropdown(driver,sexAtBirth,"Sex at Birth",testCaseParam,action);
			Webkeywords.instance().click(driver, genericLocators.button(driver, "New Person"),newPersonBtn, testCaseParam, action);
			Webkeywords.instance().verifyTextDisplayed(driver, genericLocators.textOnPage(driver, "Validate Person Name"),validatePersonName, testCaseParam, action);
			
		} catch (Exception e) {
				logger.error("Failed == {} ", action.getPageActionDescription());
				exceptionDetails.logExceptionDetails(driver, testCaseParam, action.getPageActionName(), action.getPageActionDescription(), startTime,e);
			}
	}
}
