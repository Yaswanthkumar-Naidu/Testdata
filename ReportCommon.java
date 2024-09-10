  package report_utilities.common;

  import org.openqa.selenium.WebDriver;

  import org.slf4j.Logger;
  import org.slf4j.LoggerFactory;

import report_utilities.constants.ReportContants;
import report_utilities.model.TestCaseParam;
import report_utilities.model.TestStepModel;
import report_utilities.test_result_model.BrowserResult;
import report_utilities.test_result_model.ModuleResult;
import report_utilities.test_result_model.TestCaseResult;
import report_utilities.extent_model.ExtentUtilities;
import report_utilities.extent_model.PageDetails;
import report_utilities.extent_model.TestCaseDetails;
import report_utilities.extent_model.TestStepDetails;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.Duration;
  import java.time.LocalDateTime;

  import java.time.format.DateTimeFormatter;
  import java.time.temporal.ChronoUnit;
  import java.util.*;
  import java.util.stream.Collectors;

public class ReportCommon
{
	static final Logger logger = LoggerFactory.getLogger(ReportCommon.class.getName());

	ScreenshotCommon screenshotCommon = new ScreenshotCommon();
	ExtentUtilities extentUtilities = new ExtentUtilities();
	
	public void logTestStepDetails(WebDriver drrc, TestCaseParam testCaseParam, String testStepName, String testStepDescription,PageDetails pageDetails, LocalDateTime startTime,String status) throws IOException
	{
	LocalDateTime  endTime = LocalDateTime.now();
		
		TestStepModel testStepModel = new TestStepModel();
		
        String screenShotData="";
		
		if(status.equalsIgnoreCase("PASS"))
		{
			screenShotData=screenshotCommon.captureScreenShot(drrc, testCaseParam.getTestCaseName());
		}
		else if(status.equalsIgnoreCase("DONE"))
		{
			if(ReportContants.isScreenShotforAllPass())
			{
			screenShotData=screenshotCommon.captureScreenShot(drrc, testCaseParam.getTestCaseName());
			}
		}
		
		else if(status.equalsIgnoreCase("FAIL"))
		{
			screenShotData=screenshotCommon.captureScreenShot(drrc, testCaseParam.getTestCaseName());
		}
		
		if(testStepName.equalsIgnoreCase("Click --> null")) {
			String[] parts = testStepName.split(" --> ");
			testStepName = parts[0];
		}else if(testStepName.contains("--null")) {
			String[] parts = testStepName.split("--null");
			testStepName = parts[0];
		}
		testStepName=getActionName( pageDetails.getPageActionName(),testStepName);
		testStepDescription=getActionDesc(pageDetails.getPageActionDescription(),testStepDescription );
	
			
			testStepModel=testStepModel.addTestStepDetails(testStepName, testStepDescription, startTime, endTime,
					status,screenShotData);

			TestCaseParam temptestCaseParam=getTempTestCaseParam(testCaseParam);
			extentUtilities.log(temptestCaseParam, testStepModel);

	}

	public void logExceptionDetails(WebDriver drex, TestCaseParam testCaseParam, String testStepName, String testStepDescription, LocalDateTime startTime) throws IOException
	{
		TestStepModel testStepModel = new TestStepModel();

		testStepModel=testStepModel.addTestStepDetails(testStepName, testStepDescription, startTime, LocalDateTime.now(),
			"Fail", screenshotCommon.captureScreenShot(drex, testCaseParam.getTestCaseName()));
		TestCaseParam temptestCaseParam=getTempTestCaseParam(testCaseParam);
		extentUtilities.log(temptestCaseParam, testStepModel);

	}
	
	public void logExceptionDetails(WebDriver drex, TestCaseParam testCaseParam, String testStepName, String testStepDescription, LocalDateTime startTime,Exception ex) throws IOException
	{
		TestStepModel testStepModel = new TestStepModel();
		
		StringBuilder stack= new StringBuilder();
		for(StackTraceElement s: ex.getStackTrace()) {
			stack = (stack.append(s)).append(";");
		}
		
		testStepModel.setTestStepName(testStepName);
	    testStepModel.setTestStepDescription(testStepDescription);
	    testStepModel.setStartTime(startTime);
	    testStepModel.setEndTime(LocalDateTime.now());
	    testStepModel.setTestStepStatus("Fail");
	    testStepModel.setScreenShotData(screenshotCommon.captureScreenShot(drex, testCaseParam.getTestCaseName()));
	    testStepModel.setErrorMessage(ex.getMessage());
	    testStepModel.setErrorDetails(stack.toString());
		TestCaseParam temptestCaseParam=getTempTestCaseParam(testCaseParam);
		extentUtilities.log(temptestCaseParam, testStepModel);

	}
	
    public void logverificationdetailsLabel(TestCaseParam testCaseParam, String testStepName, String testStepDescription, LocalDateTime startTime, String status, String expectedResponse , String actualResponse)
    {
          TestStepModel testStepModel = new TestStepModel();
    testStepModel=testStepModel.addVerificationStep(testStepName, testStepDescription, startTime, LocalDateTime.now(),
                       status, expectedResponse, actualResponse);
          TestCaseParam temptestCaseParam=getTempTestCaseParam(testCaseParam);
          extentUtilities.log(temptestCaseParam, testStepModel);
    }

	
	public void logExceptionDetails(TestCaseParam testCaseParam, String testStepName, LocalDateTime startTime,Exception ex)
	{
		TestStepModel testStepModel = new TestStepModel();
		testStepModel=testStepModel.addTestStepErrorDetails(testStepName,  startTime, LocalDateTime.now(),
			"Fail","",ex.getMessage(),Arrays.toString(ex.getStackTrace()));
		TestCaseParam temptestCaseParam=getTempTestCaseParam(testCaseParam);
		extentUtilities.log(temptestCaseParam, testStepModel);

	}
	
	public void logDBExceptionDetails(TestCaseParam testCaseParam, String testStepName, String testStepDescription, LocalDateTime startTimes,Exception ex)
	{
		TestStepModel testStepModel = new TestStepModel();
		
		  testStepModel.setTestStepName(testStepName);
		    testStepModel.setTestStepDescription(testStepDescription);
		    testStepModel.setStartTime(startTimes);
		    testStepModel.setEndTime(LocalDateTime.now());
		    testStepModel.setTestStepStatus("Fail");
		    testStepModel.setScreenShotData("");
		    testStepModel.setErrorMessage(ex.getMessage());
		    testStepModel.setErrorDetails(Arrays.toString(ex.getStackTrace()));
		
		TestCaseParam temptestCaseParam=getTempTestCaseParam(testCaseParam);
		extentUtilities.log(temptestCaseParam, testStepModel);

	}
	

	public void logVerificationDetails(WebDriver driverVD, TestCaseParam testCaseParam, String testStepName,  LocalDateTime startTime,String status, String expectedResponse , String actualResponse) throws IOException
{
		TestStepModel testStepModel = new TestStepModel();
		testStepModel=testStepModel.addVerificationStep(testStepName,  startTime, LocalDateTime.now(),
				status, screenshotCommon.captureScreenShot(driverVD, testCaseParam.getTestCaseName()),expectedResponse,actualResponse);
		TestCaseParam temptestCaseParam=getTempTestCaseParam(testCaseParam);
		extentUtilities.log(temptestCaseParam, testStepModel);


	}
	
	public void logDBVerificationDetails(TestCaseParam testCaseParam, String testStepName, LocalDateTime startTime,String status, String expectedResponse , String actualResponse)
	{
		TestStepModel testStepModel = new TestStepModel();
		testStepModel=testStepModel.addVerificationStep(testStepName,  startTime, LocalDateTime.now(),
				status, "",expectedResponse,actualResponse);
		TestCaseParam temptestCaseParam=getTempTestCaseParam(testCaseParam);
		extentUtilities.log(temptestCaseParam, testStepModel);


	}
	
	
	public void logAPIModule( TestCaseParam testCaseParam, String apidetails)
	{
		TestStepModel testStepModel = new TestStepModel();
		testStepModel=testStepModel.addModuleData(apidetails);
		TestCaseParam temptestCaseParam=getTempTestCaseParam(testCaseParam);
		extentUtilities.log(temptestCaseParam, testStepModel);


	}

		public void logAPIDetails(TestCaseParam testCaseParam, String testStepName, LocalDateTime startTime,String status, String url, String request, String response) throws IOException
	{
	LocalDateTime  endTime = LocalDateTime.now();
	
	ArrayList<String > fileData = new ArrayList<>();
	String separatorLine = "\"********************************************************************************************************\"";
	fileData.add(separatorLine);
	fileData.add("*********************************************************URL********************************************");
	fileData.add(separatorLine);
	fileData.add(url);
	fileData.add(separatorLine);
	fileData.add("*********************************************************Request********************************************");
	fileData.add(separatorLine);

	fileData.add(request);

	fileData.add(separatorLine);
	fileData.add("*********************************************************Response********************************************");
	fileData.add(separatorLine);

	fileData.add(response);
		
		TestStepModel testStepModel = new TestStepModel();
		
        String screenShotData="";
		
		if(status.equalsIgnoreCase("PASS"))
		{
			 screenShotData=screenshotCommon.createTextFileArrayList(fileData, testStepName);
		}
		else if(status.equalsIgnoreCase("DONE"))
		{
			if(ReportContants.isScreenShotforAllPass())
			{
				  screenShotData=screenshotCommon.createTextFileArrayList(fileData, testStepName);
			}
		}
		
		else if(status.equalsIgnoreCase("FAIL"))
		{
			 screenShotData=screenshotCommon.createTextFileArrayList(fileData, testStepName);
		}
	
			
			testStepModel=testStepModel.addTestStepDetails(testStepName, testStepName, startTime, endTime,
					status,screenShotData);

			TestCaseParam temptestCaseParam=getTempTestCaseParam(testCaseParam);
			extentUtilities.log(temptestCaseParam, testStepModel);

	}


	
    public void logAPITestStep(TestCaseParam testCaseParam, String testStepName, String testStepDescription, LocalDateTime startTime,String status, String expectedResponse , String actualResponse)
    {
    	

		TestStepModel testStepModel = new TestStepModel();
          
		testStepModel=testStepModel.addAPITestStep(testStepName, testStepDescription, startTime,LocalDateTime.now(),status,
                       expectedResponse,actualResponse);
          

          TestCaseParam temptestCaseParam=getTempTestCaseParam(testCaseParam);
  		extentUtilities.log(temptestCaseParam, testStepModel);
    }


		

	
	public void verifyTableData(WebDriver driver, TestCaseParam testCaseParam, String testStepName, LocalDateTime startTime, String status, String[][]expectedTableData , String[][] actualTableData) throws IOException
	{
		TestStepModel testStepModel = new TestStepModel();
		testStepModel=testStepModel.verifyTableData(testStepName,  startTime, LocalDateTime.now(),
				status, screenshotCommon.captureScreenShot(driver, testCaseParam.getTestCaseName()),expectedTableData,actualTableData);
		TestCaseParam temptestCaseParam=getTempTestCaseParam(testCaseParam);
		extentUtilities.log(temptestCaseParam, testStepModel);

	}

	
	public void logTableData(WebDriver driver, TestCaseParam testCaseParam, String testStepName, String testStepDescription, LocalDateTime startTime,String status,String[][]stepTableData) throws IOException
	{
		TestStepModel testStepModel = new TestStepModel();
		testStepModel=testStepModel.addTableData(testStepName, testStepDescription, startTime, LocalDateTime.now(),
				status, screenshotCommon.captureScreenShot(driver, testCaseParam.getTestCaseName()),stepTableData);
		TestCaseParam temptestCaseParam=getTempTestCaseParam(testCaseParam);
		extentUtilities.log(temptestCaseParam, testStepModel);

	}


	
	public void logScreenDetails(TestCaseParam testCaseParam, String screenName)
	{
		TestStepModel testStepModel = new TestStepModel();
		testStepModel=testStepModel.addScreenData(screenName);
		TestCaseParam temptestCaseParam=getTempTestCaseParam(testCaseParam);
		extentUtilities.log(temptestCaseParam, testStepModel);
	}

	public void logModuleDetails( TestCaseParam testCaseParam, String moduleName)
	{
		TestStepModel testStepModel = new TestStepModel();
		testStepModel=testStepModel.addModuleData(moduleName);
		TestCaseParam temptestCaseParam=getTempTestCaseParam(testCaseParam);
		extentUtilities.log(temptestCaseParam, testStepModel);


	}
	
	
	
	public void logPrereqDetails( TestCaseParam testCaseParam, String tcPrereq)
	{
		TestStepModel testStepModel = new TestStepModel();
		testStepModel=testStepModel.addMultiTCData(tcPrereq);
		extentUtilities.log(testCaseParam, testStepModel);

	}

	
	public void logModuleAndScreenDetails( TestCaseParam testCaseParam, String moduleName, String screenname)
	{
		TestStepModel testStepModel = new TestStepModel();
		testStepModel=testStepModel.addModuleScreenData(moduleName,screenname);
		TestCaseParam temptestCaseParam=getTempTestCaseParam(testCaseParam);
		extentUtilities.log(temptestCaseParam, testStepModel);

	}
	
	public void logPDFDetails(TestCaseParam testCaseParam, String testStepName, String testStepDescription, LocalDateTime startTime,String status, String results)
	{
			TestStepModel testStepModel = new TestStepModel();
			testStepModel=testStepModel.addVerificationStepPDF(testStepName, testStepDescription, startTime, LocalDateTime.now(),
					status,results);
			TestCaseParam temptestCaseParam=getTempTestCaseParam(testCaseParam);
			extentUtilities.logPdf(temptestCaseParam, testStepModel);


		}
	public void logPDFDetailsPass(TestCaseParam testCaseParam, String testStepName, String testStepDescription, LocalDateTime startTime,String status)
	{
			TestStepModel testStepModel = new TestStepModel();
			testStepModel=testStepModel.addVerificationStepPDFPass(testStepName, testStepDescription, startTime, LocalDateTime.now(),
					status);
			TestCaseParam temptestCaseParam=getTempTestCaseParam(testCaseParam);
			extentUtilities.logPdf(temptestCaseParam, testStepModel);


		}

	
	
	
	public TestCaseParam getTempTestCaseParam(TestCaseParam testCaseParam)
	{
		TestCaseParam temptestCaseParam= new TestCaseParam();
		temptestCaseParam.setTestCaseDescription(testCaseParam.getTestCaseDescription());
		temptestCaseParam.setTestCaseName(testCaseParam.getTestCaseName());
		temptestCaseParam.setBrowser(testCaseParam.getBrowser());
		temptestCaseParam.setIteration(testCaseParam.getIteration());
		temptestCaseParam.setModuleName(testCaseParam.getModuleName());
		temptestCaseParam.setTestCaseType(testCaseParam.getTestCaseType());
		
		
			if(temptestCaseParam.getTestCaseType()==TestCaseParam.TestCaseType.PREREQ) 
			{
				temptestCaseParam.setTestCaseName(testCaseParam.getTestCaseName().replace("_Prereq", ""));
			}
			
			return temptestCaseParam;
	}

	public void calculateTestCaseResults(List<Map<UUID, TestCaseDetails>> testCaseRepository) throws FileNotFoundException {
	    try {
	        if (testCaseRepository != null) {
	            for (Map<UUID, TestCaseDetails> dictTC : testCaseRepository) {
	                TestCaseDetails testCaseDetails = dictTC.values().stream().findFirst().orElse(null);
	                if (testCaseDetails != null) {
	                    TestCaseResult testCaseResult = createTestCaseResult(testCaseDetails);
	                    ReportContants.getTestcaseResults().add(testCaseResult);
	                }
	            }
	        }
	    } catch (Exception e) {
	        logger.info(e.getMessage());
	        logger.info(Arrays.toString(e.getStackTrace()));
	        throw new FileNotFoundException();
	    }
	}

	private TestCaseResult createTestCaseResult(TestCaseDetails testCaseDetails) {
	    TestCaseResult testCaseResult = new TestCaseResult();
	    setBasicDetails(testCaseResult, testCaseDetails);
	    setTestCaseStatus(testCaseResult, testCaseDetails.getStepDetails());
	    setDuration(testCaseResult, testCaseDetails.getStepDetails());
	    return testCaseResult;
	}

	private void setBasicDetails(TestCaseResult testCaseResult, TestCaseDetails testCaseDetails) {
	    testCaseResult.setTestcaseName(testCaseDetails.testCaseName);
	    testCaseResult.setTestcaseDescription(testCaseDetails.testCaseDescription);
	    testCaseResult.setModule(testCaseDetails.module);
	    testCaseResult.setBrowser(testCaseDetails.browser);
	    testCaseResult.setTestcaseCategory(testCaseDetails.testCaseCategory);
	    testCaseResult.setCaseNumber(testCaseDetails.caseNumber);
	    testCaseResult.setApplicationNumber(testCaseDetails.applicationNumber);
	}

	private void setTestCaseStatus(TestCaseResult testCaseResult, List<TestStepDetails> testStepDetails) {
	    boolean tcFailed = testStepDetails.stream().anyMatch(x -> x.extentStatus.name().toUpperCase().contains("FAIL"));
	    testCaseResult.setTestcaseStatus(tcFailed ? "Fail" : "Pass");
	}

	private void setDuration(TestCaseResult testCaseResult, List<TestStepDetails> testStepDetails) {
	    testCaseResult.setStartTime(testStepDetails.get(0).startTime);
	    testCaseResult.setEndTime(testStepDetails.get(testStepDetails.size() - 1).endTime);

	    long totalSeconds = ChronoUnit.SECONDS.between(testCaseResult.getStartTime().toLocalTime(), testCaseResult.getEndTime().toLocalTime());
	    int totalSecs = (int) totalSeconds;

	    String duration = String.format("%02d:%02d:%02d", totalSecs / 3600, (totalSecs % 3600) / 60, totalSecs % 60);
	    testCaseResult.setDuration(duration);
	}
	
	
	public void calculateModuleResults()
	{
		ModuleResult moduleResult= new ModuleResult();
		Map<String, List<TestCaseResult>> moduleStatus =
				ReportContants.getTestcaseResults().stream().
				collect(Collectors.groupingBy(TestCaseResult::getModule));

for (Map.Entry<String, List<TestCaseResult>> entry : moduleStatus.entrySet()) {
int total = entry.getValue().size();
long passCount = entry.getValue().stream().filter(e -> e.getTestcaseStatus().equalsIgnoreCase("Pass")).count();
long failCount = entry.getValue().stream().filter(e -> e.getTestcaseStatus().equalsIgnoreCase("Fail")).count();
moduleResult.setModule(entry.getKey());
moduleResult.setTcTotalCount(total);
moduleResult.setTcPassCount((int) passCount);
moduleResult.setTcFailCount((int) failCount);
moduleResult.setTcSkippedCount((int) (total-(passCount+failCount)));
ReportContants.getModuleResults().add(moduleResult);

}
}

	
	public void calculateBrowserResults()
	{
		BrowserResult browserResult= new BrowserResult();
		Map<String, List<TestCaseResult>> browserStatus = 
				ReportContants.getTestcaseResults().stream().
				collect(Collectors.groupingBy(TestCaseResult::getBrowser));

for (Map.Entry<String, List<TestCaseResult>> entry : browserStatus.entrySet()) {
int total = entry.getValue().size();
long passCount = entry.getValue().stream().filter(e -> e.getTestcaseStatus().equalsIgnoreCase("Pass")).count();
long failCount = entry.getValue().stream().filter(e -> e.getTestcaseStatus().equalsIgnoreCase("Fail")).count();
browserResult.setBrowser(entry.getKey());
browserResult.setTcTotalCount(total);
browserResult.setTcPassCount((int) passCount);
browserResult.setTcFailCount((int) failCount);
browserResult.setTcSkippedCount((int) (total-(passCount+failCount)));
ReportContants.getBrowserResults().add(browserResult);

}
}

public String getActionName(String pageStepName, String actionName)
{
	
	if(pageStepName.equals(""))
	{
		return actionName;
	}
	else
	{
		return pageStepName + "  ====>  "+ actionName;
	}
}


public String getActionDesc(String pageStepDesc, String actionDesc)
{
	
	if(pageStepDesc.equals(""))
	{
		return actionDesc;
	}
	else
	{
		return pageStepDesc + "  ====>  "+ actionDesc;
	}
}

public String getDuration(LocalDateTime startTime, LocalDateTime endTime)
{
	int totalSecs = endTime.getSecond() - startTime.getSecond();
	
	
	int hours = totalSecs / 3600;
	int minutes = (totalSecs % 3600) / 60;
	int seconds = totalSecs % 60;
	
	String strhours = String.valueOf(hours);
	String strminutes = String.valueOf(minutes);
	String strseconds = String.valueOf(seconds);

	if(strhours.length()==1)
	{
		strhours="0"+strhours;
	}

	if(strminutes.length()==1)
	{
		strminutes="0"+strminutes;
	}

	if(strseconds.length()==1)
	{
		strseconds="0"+strseconds;
	}


	return strhours +":" + strminutes+ ":" + strseconds;

}

public  String convertLocalDateTimetoSQLDateTime(LocalDateTime localdateTime)
{
	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	return localdateTime.format(formatter);
	

}

public String getTimeDifference(LocalDateTime startTime, LocalDateTime endTime)



{

Duration duration = Duration.between(startTime, endTime);

long seconds = duration.getSeconds();



	return String.format("%02d:%02d:%02d", seconds / 3600, (seconds % 3600) / 60, seconds % 60);



}

}