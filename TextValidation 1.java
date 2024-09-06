package api_utilities.api_helpers;

public class TextValidation {

	public String getTextCheckResponse(String jsonResponse,String expectedResponse)
	{
		
		if(jsonResponse.contains(expectedResponse))
		{
			return expectedResponse;
		}
		else
		{
			return jsonResponse;
		}
		
	}
	
}
