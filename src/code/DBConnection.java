package code;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.*;


import javax.servlet.http.HttpServletRequest;

import org.json.JSONObject;

/* This class manage the data base connection and builds the response for
   each query and also have a few others utilities */

public class DBConnection {
	
    private Connection connection;
	String json = "";
	int status = 1;
	
	/* This function connect to the date base and executes the query provided 
	 * the reason for do both actions in the same function is to optimize 
	 * (normally when a db connection is set is to execute a query), i made this 
	 * function a long time ago back at my university days that's why i don't use JSONObject 
	 * library, i tried to be the more independent from third party jars as possible */ 
	@SuppressWarnings("unused")
	int execSql(String query){
		this.json = "";
		status = 0;
			try {
				this.connection = null;
				Class.forName("org.postgresql.Driver");
				this.connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/inventory","postgres","masterkey");
				Statement stmt = this.connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
				ResultSet rs = null;
				if(query.toUpperCase().contains("SELECT")){
					rs = stmt.executeQuery(query);
					ResultSetMetaData rsmd = rs.getMetaData();
					rs.last();
					int rows = rs.getRow();
					rs.beforeFirst();
					int columns = rsmd.getColumnCount();
					for(int i = 0;rs.next(); i++){
						String _user = "";
						for(int j = 0; j < columns ; j++){						
							if(_user.equals("")){
								if(rs.getObject(j+1) == null) {									
									_user += ",\""+rsmd.getColumnName(j+1) + "\":\""+null+"\"";																				
								} else {									
									_user += "\""+rsmd.getColumnName(j+1) + "\":\""+rs.getObject(j+1).toString()+"\"";																			
								}							
							} else {
								if(rs.getObject(j+1) == null) {									
									_user += ",\""+rsmd.getColumnName(j+1) + "\":\""+null+"\"";																										
								} else {									
									_user += ",\""+rsmd.getColumnName(j+1) + "\":\""+rs.getObject(j+1).toString()+"\"";																																
								}							
							}
						}//for de j
						if(json.equals("")){
							json += "{"+_user+"}";
					
						}else{
							json += ",{"+_user+"}";							
						
						}
						if(json.length() > 0){
							status = 1;
						}else if (json.length() == 0) {
							status = 0;
						}
						if(query.toUpperCase().contains("SELECT") && query.toUpperCase().contains("WHERE")){
							status = 1;
						}						
					}			
				} else if(query.toUpperCase().contains("INSERT") || query.toUpperCase().contains("UPDATE") || query.toUpperCase().contains("DELETE")){
					status = stmt.executeUpdate(query);
					
				} else {
					status = 0;					
				}
			}catch (ClassNotFoundException | SQLException e) {	            
				e.printStackTrace();
			}
			return status;
	}
	
	/* this function returns the JSON embedded in the buffer reader from
	 * HttpServletRequest of each servlet method i put this here in order to re-use code
	 * ( found this function on internet ) */
	JSONObject retrieveJson (HttpServletRequest request) throws IOException {
		StringBuilder sb = new StringBuilder();
	    BufferedReader reader = request.getReader();
	    try {
	        String line;
	        while ((line = reader.readLine()) != null) {
	            sb.append(line);
	        }
	    } finally {
	        reader.close();
	    }
	    JSONObject jsonR = new JSONObject(sb.toString());
		return jsonR;
		
	}
	
	/* This function just returns the data from the query */
	String getData(){
		return this.json.replaceAll("\n", "");
	}
	
	/* This function just clear any data stored from previous request */
	void clearData(){
		this.json = "";
	}
	
	/* both functions just do as the name indicates, returns simple or double quoted Strings
	 * that are previously specified, i use this functions to insert quotes easily in strings 
	 * to executes queries*/
	String simpleQuoted(String value){		
		if(value == null || value.equals("undefined")) {
			return null;
		}else {
			return "'"+value+"'";
		}		
	}
	
	String doubleQuoted(String value){
		if(value == null || value.equals("undefined")) {
			return null;
		}else {
			return "\""+value+"\"";
		}
	}
	
	/* 
	 * Evalues a String for empty, null or undefined values */
	public boolean checkString(String var) {
		if(var != null && !var.isEmpty() && !var.equals("undefined")) {
			return true;
		}else {
			return false;
		}				
	}
	
	/* checks if some value already exist in a table */
	public boolean checkValue(String table_name, String column_name, String value) {
		if(execSql("SELECT "+column_name+" FROM "+table_name+" WHERE "+column_name+"="+simpleQuoted(value)) == 1) {
    		return true;
    	}else {
    		return false;
    	}
	}
	
	/* checks if the session is vali*/
	public boolean checkSession(String sessionId) {
		if(!sessionId.equals("null") && sessionId != null) {
			return true;
		}else {
			return false;
		}
	}
}