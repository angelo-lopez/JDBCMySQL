package com.romel;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLTimeoutException;

import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.File;

public class JDBCMySQLXMLInsert {

	public static void main(String[] args) {
		
		//The URL to the classicmodels schema in the local instance of MySQL server. 
		final String strDBURL = "jdbc:mysql://localhost:3306/classicmodels";
		
		//The username and password for MySQL Server authentication.
		String strUserName = "root";
		String strPassword = "mysqlcommunity2020";
		
		//Declare and initialize the connection and preparedstatement properties to null so they can be tested and closed
		//outside of the outer try-catch block.
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		
		//Sql statement to retrieve every record from the employees table.
		String sqlDisplayEmployees = "Select employeeNumber, Concat(lastName, \", \", firstName) As employee, extension, "
				+ "email, officeCode, reportsTo, jobTitle\n" + 
				"From employees";
		
		//Sql to insert a record to the employees table.
		String sqlInsertEmployees = "Insert Into employees (employeeNumber, lastName, firstName, extension, email, "
				+ "officeCode, reportsTo, jobTitle) " + 
				"Values (?, ?, ?, ?, ?, ?, ?, ?)";
		
		//The local path to the employees.xml file.
		String strXMLFile = "/users/kubi/documents/employees.xml";
		
		try {
			//Establish the database connection.
			connection = DriverManager.getConnection(strDBURL, strUserName, strPassword);
			System.out.println("Successfuly connected to -> " + strDBURL);
			System.out.println("Driver -> " + connection.getClass().getName());
			
			//Display a list of every employee.
			displayEmployees(connection, sqlDisplayEmployees);
			
			//Initialize the preparedStatement to insert an employee record.
			preparedStatement = connection.prepareStatement(sqlInsertEmployees);
			
			//Read the XML file (DOM) and navigate thru the elements and insert each record to the employees table.
			DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document document = documentBuilder.parse(new File(strXMLFile));
			document.normalize();
			
			NodeList nodeList = document.getElementsByTagName("employee");
			
			System.out.println("\nInserting records from " + strXMLFile + " ...");
			
			for(int i = 0; i < nodeList.getLength(); i ++) {
				Node node = nodeList.item(i);
				
				if(node.getNodeType() == Node.ELEMENT_NODE) {
					Element element = (Element) node;
					
					preparedStatement.setInt(1, Integer.parseInt(element.getAttribute("employeeNumber")));
					preparedStatement.setString(2, element.getElementsByTagName("lastName").item(0).getTextContent());
					preparedStatement.setString(3, element.getElementsByTagName("firstName").item(0).getTextContent());
					preparedStatement.setString(4, element.getElementsByTagName("extension").item(0).getTextContent());
					preparedStatement.setString(5, element.getElementsByTagName("email").item(0).getTextContent());
					preparedStatement.setString(6, element.getElementsByTagName("officeCode").item(0).getTextContent());
					preparedStatement.setInt(7, Integer.parseInt(element.getElementsByTagName("reportsTo").item(0)
							.getTextContent()));
					preparedStatement.setString(8, element.getElementsByTagName("jobTitle").item(0).getTextContent());
					
					//Add the record to be inserted to the batch.
					preparedStatement.addBatch();
				}
			}
			
			//Execute the batch update/insert.
			if(nodeList.getLength() > 0) {
				System.out.println("Number of records inserted -> " + preparedStatement.executeBatch().length);
			}
			
			//List all the records of the employee table.
			displayEmployees(connection, sqlDisplayEmployees);
		}
		catch(SQLTimeoutException sqltimeex) {
			System.out.println("Exception -> " +sqltimeex.getMessage());
		}
		catch(SQLException sqlex) {
			System.out.println("Exception -> " + sqlex.getMessage());
		}
		catch(Exception ex) {
			System.out.println("Exception - >" + ex.getMessage());
		}
		finally {
			//Close connection/resources.
			try {
				if(preparedStatement != null) {
					preparedStatement.close();
				}
				if(connection != null) {
					connection.close();
				}
				System.out.println("\nResources freed/released.");
			}
			catch(Exception ex) {
				System.out.println("Exception -> " + ex.getMessage());
			}
		}

	}
	
	/**
	 * Method to display every record from the employees table.
	 * @param connection -> Connection object.
	 * @param sqlSelectEmployees -> Sql statement that retrieves every record from the employees table.
	 */
	public static void displayEmployees(Connection connection, String sqlSelectEmployees) {
		//Declare and initialize the preparedstatement and resultset properties to null so they can be tested and closed
		//outside of the outer try-catch block.
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		
		try {
			preparedStatement = connection.prepareStatement(sqlSelectEmployees);
			resultSet = preparedStatement.executeQuery();
			
			System.out.println("\nList of Employees:\n");
			System.out.printf("%-20s %-30s %-12s %-35s %-20s %-20s %-20s\n", "Employee Number", "Employee", "Extension",
					"Email", "Office Code", "Reports To", "Job Title");
			System.out.println("--------------------------------------------------------------------------"
					+"--------------------------------------------------------------------------");
			
			int iRowCount = 0;//Count and store the number of results.
			
			//Iterate thru the resultset and display each record.
			while(resultSet.next()) {
				System.out.printf("%-20s %-30s %-12s %-35s %-20s %-20s %-20s\n", resultSet.getString(1), resultSet.getString(2), 
						resultSet.getString(3), resultSet.getString(4), resultSet.getString(5),
						resultSet.getString(6), resultSet.getString(7));
				iRowCount ++;
			}
			
			System.out.println("\nNumber of rows retrieved -> " + Integer.toString(iRowCount));
		}
		catch(Exception ex) {
			System.out.println("Exception -> " + ex.getMessage());
		}
		finally {
			//Release resources.
			try {
				if(resultSet != null) {
					resultSet.close();
				}
				if(preparedStatement != null) {
					preparedStatement.close();
				}
			}
			catch(Exception ex ) {
				System.out.println("Exception -> " + ex.getMessage());
			}
		}
	}

}
