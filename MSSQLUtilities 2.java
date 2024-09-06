package api_utilities.api_helpers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.*;

import api_utilities.test_settings.APISessionData;


public class MSSQLUtilities {
	private static final Logger logger =LoggerFactory.getLogger(MSSQLUtilities.class.getName()); 	
	
	public static String readDBFrameworkDate() {
	    String date = "";
	    String query = "SELECT GetDate();";

	    try (Connection con = DriverManager.getConnection(APISessionData.envModel.getConnectionString());
	         Statement stmt = con.createStatement();
	         ResultSet rs = stmt.executeQuery(query)) {

	        ArrayList<String> queryData = new ArrayList<>();
	        while (rs.next()) {
	            queryData.add(rs.getString(1));
	        }

	        if (!queryData.isEmpty() && queryData.get(0) != null) {
	            date = queryData.get(0);
	        }
	    } catch (SQLException e) {
	        logger.error("Error while reading DB framework date", e);
	    }

	    return date;
	}


	
	public String readDataDBValidation(String connectionString, String query) {
	    String data = "";

	    try (Connection con = DriverManager.getConnection(connectionString);
	         Statement stmt = con.createStatement();
	         ResultSet rs = stmt.executeQuery(query)) {

	        ArrayList<String> queryData = new ArrayList<>();
	        while (rs.next()) {
	            queryData.add(rs.getString(1));
	        }

	        if (!queryData.isEmpty()) {
	            data = queryData.get(0);
	        }
	    } catch (SQLException e) {
	        logger.error("Error while reading data from database", e);
	    }

	    return data;
	}


	
	
	public void insertQueries(String connectionString, String tableName, List<String> headers, List<ArrayList<String>> queries) {
	   
	    try (Connection con = DriverManager.getConnection(connectionString)) {
	        if (con != null) {
	            logger.info("Connection Successful!");
	        }
	        
	        StringBuilder columnNameList =  new StringBuilder();
	        int j = 1;
	        for (String columnName : headers) {
	            if (j == 1) {
	            	columnNameList.append(columnName);
	            } else {
	            	columnNameList.append(",").append(columnName);
	            }
	            j++;
	        }
	        String newColumnNames = columnNameList.toString();

	        for (ArrayList<String> rowData : queries) {
	        	
	        	StringBuilder query =  new StringBuilder();
	        	
	        	query.append("INSERT INTO").append(tableName).append(" ").append(newColumnNames).append(" Values (");
	        	
	            for (int i = 0; i < rowData.size(); i++) {
	                if (i > 0) {
	                    query.append(", ");
	                } 
	                query.append("'").append(rowData.get(i)).append("'");
	            }
	            query.append(");");
	            
	            String querys = query.toString();
	            querys = querys.replace("'getDate()'","getDate()");
	            
	            try (Statement stmt = con.createStatement()) {
	                
	            	stmt.execute(querys);
	                logger.info("Report Inserted into DB");
	            }
	        }
	    } catch (Exception e) {
	        logger.error("Error while inserting queries"+ e.getMessage(), e);
	    }
	}
}