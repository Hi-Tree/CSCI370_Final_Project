package surveyDB;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class DbConnection {
		
		//This method establishes a connection to a mysql database, it assumes port 3306
		//This will only work for my personal database called "SERVLETUPLOADS"
		//possibly adjust this later
		public static Connection initializeDBConnection(String user, String pass, String ip) throws SQLException, ClassNotFoundException {
			String url = "jdbc:mysql://" + ip + ":3306" + "/surveysystem";
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection connect = DriverManager.getConnection(url,user,pass);
			System.out.println("returning " + connect);
			return connect;
		}
		
		/*
		* This method reads a file with the database login credentials
		* inorder for anyone to use this, a folder called "servletCredentials must exist
		* in the c drives beginning path's, and  the file with the login credentials must be 
		* inside the folder "servletCredentials". The text file must be named "DBLoginInfo.txt", and
		* the format of the text file is one line with the following: user_Name:password:ipAddress.
		* userName - is the user name to the mysql database account
		* password - is the password to the mysql database account
		* ipAddress - is the ip address to the mysql database
		*/
		public static String[] getLoginCredentials() throws FileNotFoundException, Exception{
			//format must be: username:password:ipAddress
			File inputFile = new File("C:/servletCredentials/DBLoginInfo.txt");
			Scanner scnr = new Scanner(inputFile);
			String data = scnr.nextLine();
			scnr.close();
			String[] sp = data.split(":");
			if(sp.length != 3)
				throw new Exception("The servlet failed to login to the database");
			return sp;
		}
		
}

