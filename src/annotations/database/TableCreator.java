package annotations.database;

import java.lang.annotation.*;
import java.lang.reflect.*;
import java.sql.*;
import java.util.*;
//import com.mysql.jc.jdbc.*;
//import com.mysql.jdbc.PreparedStatement;

public class TableCreator {
	public static void main(String[] args) throws Exception{
		String[] arg = {"annotations.database.Member", 
						"annotations.database.Member2",
						"annotations.database.Constraints",
				 		"annotations.database.DBTable"};
		System.out.println(Class.forName("annotations.database.Member"));
		for(String className : arg) {
			Class<?> cl = Class.forName(className);
			//System.out.println("here");
			//use getAnnotation(DBTable.class) to find out weather this class have @DBTable 
			DBTable dbTable = cl.getAnnotation(DBTable.class);
			if(dbTable == null) {
				System.out.println("No DBTable annotations in class " + className);
				continue;
			}
			String tableName = dbTable.name();
			
			//if the name is empty, use the class name
			if(tableName.length() < 1) tableName = cl.getName().toUpperCase();
			
			List<String> columnDefs = new ArrayList<String>();
			for(Field field : cl.getDeclaredFields()) {
				String columnName = null;
				Annotation[] anns = field.getDeclaredAnnotations();
				
				if(anns.length < 1) continue;	//not a dbtable columns
				
				if(anns[0] instanceof SQLInteger) {
					SQLInteger sInt = (SQLInteger) anns[0];
					//use field name if name is not specified
					if(sInt.name().length() < 1) columnName = field.getName().toUpperCase();
					else columnName = sInt.name();
					columnDefs.add(columnName + " INT" + getConstraints(sInt.constraints()));		
				}
				if(anns[0] instanceof SQLString) {
					SQLString sString = (SQLString) anns[0];
					//use field name if name not specified
					if(sString.name().length() < 1) columnName = field.getName().toUpperCase();
					else columnName = field.getName();
					columnDefs.add(columnName + " VARCHAR(" + sString.value() + ")" + getConstraints(sString.constraints()));
				}
			}
			StringBuilder createCommand = new StringBuilder("CREATE TABLE " + tableName + "(");
			for(String columDef : columnDefs) {
				createCommand.append("\n    " + columDef + ",");
			}
			//remove trailing comma
			String tableCreate = createCommand.substring(0, createCommand.length() - 1) + ");";
			writeToDatabase(tableCreate);
		}
	}
	private static String getConstraints(Constraints con) {
		String constraints = "";
		if(!con.allowNull()) constraints += "NOT NULL";
		if(con.primaryKey()) constraints += "PRIMARY KEY";
		if(con.unique()) constraints += "UNIQUE";
		return constraints;
	}
	private static void writeToDatabase(String command) {
		//drive
		System.out.println(command);
		String driver = "com.mysql.cj.jdbc.Driver";
		
		//ARN: Establishing SSL connection without server's identity
		//update version will ask weather use SSL connection. add&&useSSL=true
		String url = "jdbc:mysql://localhost:3306/java_comment";
		//user and password
		String username = "root";
		String password = "************";
		Connection conn = null;
		try {
			Class.forName(driver);	//CLASS LOADER
			//get connection
			conn = DriverManager.getConnection(url, username, password);
			//create a statement
			Statement st = conn.createStatement();
			//do sql command.executeUpdate to zen shan gai or executeQuery to cha
			st.executeUpdate(command);
			//close connection
			conn.close();
			
		} catch(ClassNotFoundException e) {
			e.printStackTrace();
	    } catch(SQLException e) {
	        e.printStackTrace();
	    }
	}
}












