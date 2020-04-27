# JDBCMySQL
DBCMySQLXMLInsert Class:

Reads the contents of an xml file and inserts the elements to a table named "employees".

Schema: classicmodels
Database: MySQL Community Server

Console output :

![Console output](https://raw.githubusercontent.com/angelo-lopez/JDBCMySQL/master/Screenshot%202020-04-25%20at%2023.25.50.png)

employees.xml :

![employees.xml](https://raw.githubusercontent.com/angelo-lopez/JDBCMySQL/master/Screenshot%202020-04-25%20at%2023.31.48.png)

employees table structure :

Columns:

employeeNumber - int PK<br/>
lastName - varchar(50)<br/>
firstName - varchar(50)<br/>
extension - varchar(10)<br/>
email - varchar(100)<br/>
officeCode - varchar(10)<br/>
reportsTo - int<br/>
jobTitle - varchar(50)<br/>
