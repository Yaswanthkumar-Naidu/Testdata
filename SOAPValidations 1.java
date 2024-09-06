package api_utilities.api_helpers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.skyscreamer.jsonassert.JSONCompare;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.skyscreamer.jsonassert.JSONCompareResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import api_utilities.models.APIReportModel;
import io.restassured.path.xml.XmlPath;
import io.restassured.response.Response;

public class SOAPValidations {

	private static final Logger logger =LoggerFactory.getLogger(SOAPValidations.class.getName());
	

	
	 public List<APIReportModel> validateAPIPostResponse(APIReportModel apiReportModel, String expectedResponse)
	 {
		ArrayList<APIReportModel> aPIReportModels= new ArrayList<>();
		APIReportModel aPIReportModel= new APIReportModel();
		aPIReportModel.setBoolResponseStringValidation(true);
		String expectedJson = "";
		String actualJson ="";
		
		try 
		{
		JSONCompareResult compareJSON=JSONCompare.compareJSON(expectedResponse, apiReportModel.getResponse(), JSONCompareMode.STRICT);
		if(compareJSON.passed())
		{
			aPIReportModel.setTestStepResult("PASS");
		}
		else
		{
			aPIReportModel.setTestStepResult("FAIL");
		}
		
		aPIReportModel.setExpectedResponse(expectedJson);
		aPIReportModel.setActualResponse(actualJson);
		aPIReportModel.setUrl(apiReportModel.getUrl());
		aPIReportModel.setRequest(apiReportModel.getRequest());
		aPIReportModel.setResponse(apiReportModel.getResponse());

		} 
		catch (JSONException e) 
		{
				logger.info(e.getMessage());
				logger.info(Arrays.toString(e.getStackTrace()));
			logger.info("Failed to validate the JSON Response");
		}
		aPIReportModels.add(aPIReportModel);
		return aPIReportModels;
		
		
	}
	
	 public List<APIReportModel> validateXPath(APIReportModel apiReportModel, Map<String,String> xPath) 
	 {
		ArrayList<APIReportModel> aPIReportModels= new ArrayList<>();
		try 
		{
			if(!xPath.isEmpty())
			{
				for(Map.Entry<String,String>  enrty : xPath.entrySet())
				{
					String key = enrty.getKey();
					String expectedResponse=enrty.getValue();
					APIReportModel aPIReportModel= new APIReportModel();
					aPIReportModel.setBoolXMLJSONValidation(true);
					
					XmlPath xmlPath = new XmlPath(apiReportModel.getActualResponse());
					String actualResponse="";
				try
					{
						aPIReportModel.setUrl(apiReportModel.getUrl());
						aPIReportModel.setRequest(apiReportModel.getRequest());
						aPIReportModel.setResponse(apiReportModel.getResponse());

						if(key.startsWith("TEXTCHECK::"))
						{
							TextValidation textValidation= new TextValidation();
							actualResponse=textValidation.getTextCheckResponse(apiReportModel.getResponse(),expectedResponse);
							aPIReportModel.setXpathJsonKey(key+expectedResponse);
							aPIReportModel.setExpectedResponse(expectedResponse);
							aPIReportModel.setActualResponse(actualResponse);
						}
						
						else
						{
						
						actualResponse= xmlPath.get(key);
						
						aPIReportModel.setXpathJsonKey(key);
						aPIReportModel.setExpectedResponse(expectedResponse);
						aPIReportModel.setActualResponse(actualResponse);
						}
					}
					catch(Exception ex)
					{
						logger.info(actualResponse);
					}


					if(!aPIReportModel.getActualResponse().equals(aPIReportModel.getExpectedResponse()))
					{
						aPIReportModel.setTestStepResult("FAIL");
					
					}
					else
					{
						aPIReportModel.setTestStepResult("PASS");

					}
					aPIReportModels.add(aPIReportModel);
				}
			}
			
		

		} 
		catch (Exception e) 
		{
			logger.info("Exception is {}",e.getMessage());
			logger.info(Arrays.toString(e.getStackTrace()));
			logger.info("Failed to validate the JSON Response");
		}
		return aPIReportModels;				  
	}

	 
	 public Map<String,String>  storeXPath(String xmlResponse, Map<String,String> xpath)
	 {
		
		 HashMap<String,String> sessionData= new HashMap<>();
		try 
		{
			if(!xpath.isEmpty())
			{
				for(Map.Entry<String,String>  enrty : xpath.entrySet())
				{
				 
					String key = enrty.getKey();
					String variableName=enrty.getValue();
					
					XmlPath xPath = new XmlPath(xmlResponse);
					String actualValue= xPath.getString(key);
					
					sessionData.put(variableName, actualValue);
				}
			}
		
		} 
		catch (Exception e) 
		{
				logger.info("Exception is {}",e.getMessage());
				logger.info(Arrays.toString(e.getStackTrace()));

		}
	
	 return sessionData;
	 }



	 
	 public String[] fetchData(String getData)                                             // Function to get data from excel and store each node separately                            
	{
		
		//String getData = Driver.dataValue;                              // Fetching API response from excel to getData variable
		String[] tempArr ;
		String[] childElements = new String[1000];
		int k =0;
		int j=0;
		
		if(getData.contains(";"))                                       // if condition to check if the getData string has a semi-colon or not.  
		{
		    String[] childData = getData.split(";");                    // to split based getData string on semi-colon
				
		    for (int i =0; i <childData.length;i++)                     // business logic to further split all the strings based on "|" symbol  
		    {
			    if(childData[i].contains("|"))                          
			    {
			        tempArr = childData[i].split("\\|");
			
			        for ( j=0 ; j<tempArr.length;j++ )                  // additional for loop to ensure childElements string is appended, not over-written 
			        {
				       childElements[k+j] = tempArr[j];				
			        }
			        k= k+j;
						
		        }
			
			    else                                                     
			    {
			        childElements[k] = childData[i];	
			        k++;
			    }			
		     }
		}
		
		else                                                            // when there is only 1 child string in getData ==> no semi-colon 
		{
			childElements = getData.split("\\|");
		}
		
		return childElements;
	}
	
	public void validateDataElements(Response responsePost, String[] childElements)                                                        // function to validate the individual nodes    
	{                                                                                                       // derived from fetchData() method

   		int index = -1;                                                                                 // index to fetch the position of node in .json file 
		    
			for (int i =0; i<childElements.length && childElements[i] != null; i++)                         // Business logic to validate every node
			{
			    String[] seperateFromHash = childElements[i].split("#");
			    
			    if(seperateFromHash.length == 3)
			    {
			 
			    	if(index == -1)                                                                             // condition to ensure child number is given correctly
	                {
	                	logger.info("Invalid input! Must mention the child string number correctly!");   
	                	                                                                              // loop skips directly to next iteration
	                }
			    	
                  String nestedTemp = seperateFromHash[0] + "[" + index + "]";			    	
			    	Map<String, String> nestedData = responsePost.jsonPath().getMap(nestedTemp);
			    	
			    	if((nestedData.get(seperateFromHash[1])).equalsIgnoreCase(seperateFromHash[2]))
			    	{
			    		logger.info("Child Element {} : Validation is PASSED!", seperateFromHash[1]);
	                }
	                else
	                {
	                   	logger.info("Child Element {} : Validation is FAILED!", seperateFromHash[1]);
	                }
			    	
			    }
			     
			    String elementField = seperateFromHash[0];    
              
              if(elementField.equalsIgnoreCase("Child"))                                                  // condition to set value of index variable
              {
              	index = Integer.parseInt(seperateFromHash[1]) - 1 ;
              	                                                                            // loop skips directly to next iteration 
              }
              
              if(index == -1)                                                                             // condition to ensure child number is given correctly
              {
              	logger.info("Invalid input! Must mention the child string number correctly!");   
              	                                                                            // loop skips directly to next iteration
              }
              
              String temp =  seperateFromHash[0] + "[" + index + "]";                                     // temporary variable to achieve format "node[index]"
              
              String dataPointValue = responsePost.jsonPath().getString(temp);                                // fetching the node value
                              			    
              if(dataPointValue.equalsIgnoreCase(seperateFromHash[1]))                                    // validating the fetched node value
              {
              	logger.info("Child number {}  for Child Element {} Validation is PASSED!",(index + 1),seperateFromHash[0]);
              }
              else
              {
            		logger.info("Child number {}  for Child Element {} Validation is Failed!",(index + 1),seperateFromHash[0]);
                    
              }
              
           } 

	 }

}
