package api_utilities.api_helpers;



import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import api_utilities.models.APIModel;
import api_utilities.test_settings.APISessionData;

public class DBValidation {
	private static final Logger logger =LoggerFactory.getLogger(DBValidation.class.getName());


	public String validateDBStatus(APIModel iModel)
	{
		String actualCount="";
		try 
		{
			
			MSSQLUtilities sql = new MSSQLUtilities();
			actualCount = sql.readDataDBValidation(APISessionData.envModel.getConnectionString(), iModel.getDBQuery());
			
		} 
		catch (Exception e) 
		{
		
			logger.info("Exception is {}", e.getMessage());
			logger.info(Arrays.toString(e.getStackTrace()));

		}
		return actualCount;				  
	}
}
