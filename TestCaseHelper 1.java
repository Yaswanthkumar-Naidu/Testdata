package api_utilities.api_helpers;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.aventstack.extentreports.ExtentTest;

import api_utilities.api_common.ExcelUtil;
import api_utilities.models.APIReportModel;
import api_utilities.models.APITestCaseModel;
import api_utilities.models.APITestStepModel;
import api_utilities.reports.ExcelReportGenerator;
import api_utilities.reports.ExtentReport;
import api_utilities.test_settings.APITestSettings;

public class TestCaseHelper {

	private static final Logger logger =LoggerFactory.getLogger(TestCaseHelper.class.getName());
	
	public void getTestCaseDetails(String fileName,String sheetName)
	{
		
		ArrayList<String> whereClause = new ArrayList<>();
		whereClause.add("Execute::Yes");
		Map<String, List<String>> tcDetails = ExcelUtil.fetchWithCondition(fileName,sheetName, whereClause);
		ArrayList<APITestCaseModel> apiTestCaseModels = new ArrayList<>();
		String apiName="APIName";
        String testcases = "TestCase";
		if(!tcDetails.isEmpty())
		{if(!tcDetails.get(testcases).isEmpty())
			{
				int tcSize=tcDetails.get(testcases).size();
				for(int i=0;i<tcSize;i++)
				{
					APITestCaseModel apiTestCaseModel = new APITestCaseModel();
					apiTestCaseModel.setTestCase(tcDetails.get(testcases).get(i));
					apiTestCaseModel.setTestCaseDescription(tcDetails.get("TestCaseDescription").get(i));
					apiTestCaseModel.setUserStory(tcDetails.get("UserStory").get(i));
					apiTestCaseModel.setDirectory(tcDetails.get("Directory").get(i));
					apiTestCaseModel.setTestCaseFilePath(APITestSettings.getApiTestSettings().getApiTestCaseDirectory() + File.separator + apiTestCaseModel.getDirectory() + File.separator + apiTestCaseModel.getTestCase() + APITestSettings.getApiTestSettings().getExcelSheetExtension());
					apiTestCaseModels.add(apiTestCaseModel);
					APITestSettings.getApiTcInfo().put( apiTestCaseModel.getTestCase(), apiTestCaseModel);
				}
			}
			else
			{
				logger.info("Record Count is 0 for  ==> {}" , fileName);
			}

		}
		else
		{
			logger.info("Incorrect Data Sheet ==> {}", fileName);
		}
		HashMap<String, ArrayList<APITestStepModel>> apiTcData= new HashMap<>();

		for(APITestCaseModel  apiTestCaseModel :apiTestCaseModels)
		{
			ArrayList<String> whereClauseTC = new ArrayList<>();
			whereClauseTC.add("Execute::Yes");

			Map<String, List<String>> tsDetails = ExcelUtil.fetchWithCondition(apiTestCaseModel.getTestCaseFilePath(),APITestSettings.getApiTestSettings().getApiTestCaseSheetName(), whereClauseTC);

			ArrayList<APITestStepModel> testStepModels= new ArrayList<>();

			if(!tsDetails.isEmpty())
			{

				if(!tsDetails.get(apiName).isEmpty())
				{
					for(int i=0;i<tsDetails.get(apiName).size();i++)
					{
						APITestStepModel apiTestStepModel = new APITestStepModel();
						apiTestStepModel.setModule(tsDetails.get("Module").get(i));
						apiTestStepModel.setApiName(tsDetails.get(apiName).get(i));
						apiTestStepModel.setFilePath(APITestSettings.getApiTestSettings().getApiDirectory() + File.separator + tsDetails.get("FileName").get(i));
						apiTestStepModel.setStartIndexforIteration(Integer.parseInt(tsDetails.get("StartIndexforIteration").get(i)));
						int iterationCount= Integer.parseInt(tsDetails.get("IterationCount").get(i));
						apiTestStepModel.setEndIndexforIteration(apiTestStepModel.getStartIndexforIteration() + iterationCount -1);
						testStepModels.add(apiTestStepModel);
					}
				}
				else
				{
					logger.info("Record Count is 0 for  ==> {}" , apiTestCaseModel.getTestCaseFilePath());
				}
			}
			else
			{
				logger.info("Incorrect Data Sheet ==> {}", apiTestCaseModel.getTestCaseFilePath());
			}
			apiTcData.put(apiTestCaseModel.getTestCase(), testStepModels);
		}
		APITestSettings.setApiTcExecData(apiTcData);
	}
	public void executeTestCases() throws InterruptedException
	{
		ExtentReport extentReport = new ExtentReport();
		ExcelReportGenerator report = new ExcelReportGenerator();
		for(String  TestCase :APITestSettings.getApiTcExecData().keySet())
		{

			APITestCaseModel apiTestCaseModel = APITestSettings.getApiTcInfo().get(TestCase);
			ExtentTest extentTest=extentReport.startTest(TestCase, apiTestCaseModel.getDirectory(), apiTestCaseModel.getUserStory(), TestCase);
			ArrayList<APITestStepModel> aPITestStepModels=APITestSettings.getApiTcExecData().get(TestCase);			
			for(APITestStepModel apiTestStepModel :aPITestStepModels)
			{
				if(apiTestStepModel.getStartIndexforIteration()==apiTestStepModel.getEndIndexforIteration())
				{
					APIController apiController = new APIController();
					report.startExcelReport();
					List<APIReportModel> reportData=apiController.executeAPI(TestCase,apiTestStepModel.getModule(),apiTestStepModel.getApiName(),APITestSettings.getBrowser(), String.valueOf(apiTestStepModel.getStartIndexforIteration()),apiTestStepModel.getFilePath());
					extentReport.addTestStep(extentTest, apiTestStepModel.getModule(), apiTestStepModel.getApiName(),String.valueOf(apiTestStepModel.getStartIndexforIteration()), reportData);
					report.addTestStep(reportData,apiTestStepModel.getApiName());
				}
				else
				{
					for(int i=apiTestStepModel.getStartIndexforIteration();i<=apiTestStepModel.getEndIndexforIteration();i++)
					{
						APIController apiController = new APIController();
						ArrayList<APIReportModel> reportData=(ArrayList<APIReportModel>) apiController.executeAPI(TestCase,apiTestStepModel.getModule(),apiTestStepModel.getApiName(),APITestSettings.getBrowser(), String.valueOf(i),apiTestStepModel.getFilePath());
						extentReport.addTestStep(extentTest, apiTestStepModel.getModule(), apiTestStepModel.getApiName(),String.valueOf(i), reportData);		
						report.addTestStep(reportData,apiTestStepModel.getApiName());
					}
				}
			}		
		}
	}
}