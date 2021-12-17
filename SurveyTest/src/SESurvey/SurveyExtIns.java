package SESurvey;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.Scanner;
import connectionPackage.ConnectToDB;
import org.json.JSONObject;

public class SurveyExtIns{
	//creating a connection to DB before hugos code was imported
	public static Connection DBConnection () {
		Connection connect = null;
		String ConnectionHost = "3.82.210.103";
		try {
		Class.forName("com.mysql.cj.jdbc.Driver");
		connect = DriverManager.getConnection("jdbc:mysql://" + ConnectionHost + ":3306/MERIDB?"+ "user=CS370&password=HelloCat1");
		System.out.println("Connection to Database Successful!");
		}catch(Exception e){
			try {
				throw e;
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
		return connect;
	}
	public static boolean ExpDateCheck(String S_ID, String User_ID) {
		ConnectToDB ctdb = new ConnectToDB("3.82.210.103:3306", "MERIDB", "CS370", "HelloCat1");
		String Result;
		int year, month, day;
		Calendar date = Calendar.getInstance();
		Calendar today = Calendar.getInstance();
		try {
			Result = ctdb.executeSelectedQuery("getExpirationDate", new String[] {S_ID, User_ID});
			if (Result != "not found") {
				//===============================================================================
//				query1 = "Select year(Expiration_date)\r\n" + 
//						"from UserSurvey \r\n" + 
//						"where Survey_ID = '" + S_ID + "' AND User_ID = '" + User_ID + "'";
//				PS = connect.prepareStatement(query1);
//				result = PS.executeQuery();
//				result.next();
				year = Integer.parseInt(ctdb.executeSelectedQuery("getYearofExpiration", new String[] {S_ID, User_ID}));
				//===============================================================================
//				query1 = "Select month(Expiration_date)\r\n" + 
//						"from UserSurvey \r\n" + 
//						"where Survey_ID = '" + S_ID + "' AND User_ID = '" + User_ID + "'";
//				PS = connect.prepareStatement(query1);
//				result = PS.executeQuery();
//				result.next();
				month = Integer.parseInt(ctdb.executeSelectedQuery("getMonthofExpiration", new String[] {S_ID, User_ID})) - 1;
				//================================================================================
//				query1 = "Select day(Expiration_date)\r\n" + 
//						 "from UserSurvey \r\n" + 
//						 "where Survey_ID = '" + S_ID + "' AND User_ID = '" + User_ID + "'";
//				PS = connect.prepareStatement(query1);
//				result = PS.executeQuery();
//				result.next();
				day = Integer.parseInt(ctdb.executeSelectedQuery("getDayofExpiration", new String[] {S_ID, User_ID}));
				date.set(year,month,day);
		}else {
			System.out.println("User Survey Not found.");
			return false;
		}

		//Checks if current date is past the Expiration date
		if (date.compareTo(today) < 0) {
			System.out.println("Sorry this survey has expired.");
			return false;
		}
		
		}catch (Exception e) {
			try {
				throw e;
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
		
		return true;
		}
	
	public static JSONObject SurveyTest(Connection connect, String S_ID, String User_ID) { 
		PreparedStatement PS = null;
		ConnectToDB ctdb = new ConnectToDB("3.82.210.103:3306", "MERIDB", "CS370", "HelloCat1");
		ResultSet result;
		ResultSet result2;
		JSONObject Survey1 = new JSONObject();
		try {
			//Retrieves Survey Questions, Question answer type, and Question choices from Database 
			int questionNum = 1;
			int qcNum = 1;
			JSONObject reference;
			JSONObject reference2;
			Survey1.put("SurveyID", S_ID);
			Survey1.put("UserID", User_ID);
			//================================================
//			String query1 = "Select Title\r\n" + 
//			"From Survey\r\n" +
//			"where Survey_ID = '" + S_ID + "' AND User_ID = '" + User_ID + "'";
//			PS = connect.prepareStatement(query1);
//			result = PS.executeQuery();
//			result.next();
			Survey1.put("SurveyTitle", ctdb.executeSelectedQuery("getSurveyTitle", new String[] {S_ID, User_ID}));
			//==================================================
//			query1 = "Select Description\r\n" + 
//			"From Survey\r\n" +
//			"where Survey_ID = '" + S_ID + "' AND User_ID = '" + User_ID + "'";
//			PS = connect.prepareStatement(query1);
//			result = PS.executeQuery();
//			result.next();
			Survey1.put("Survey Description", ctdb.executeSelectedQuery("getSurveyDescription", new String[] {S_ID, User_ID}));
			//=================================================
			
			String query1 = "Select QID\r\n" + 
					"from Question\r\n" +
					"where Survey_ID = '" + S_ID +"'";
			PS = connect.prepareStatement(query1);
			result = PS.executeQuery();
			while (result.next()) {
				Survey1.put("Question " + questionNum, new JSONObject());
				reference = Survey1.getJSONObject("Question " + questionNum);
				reference.put("QID", result.getInt(1));
				
				//==========================================================
//				query1 = "Select Qdescription\r\n" + 
//				"from Question\r\n" + 
//				"Where QID = '" + reference.get("QID")+"'";
//				PS = connect.prepareStatement(query1);
//				result2 = PS.executeQuery();
//				result2.next();
				reference.put("Question Description", ctdb.executeSelectedQuery("getQDescription", new String[] {Integer.toString(reference.getInt("QID"))}));
				//===========================================================
				
				query1 = "Select QCID\r\n" + 
						"from QuestionChoices\r\n" + 
						"Where QID = '"+ reference.get("QID")+"'";
				PS = connect.prepareStatement(query1);
				result2 = PS.executeQuery();
				while(result2.next()) {
					reference.put("QC" + qcNum, new JSONObject());
					reference2 = reference.getJSONObject("QC" + qcNum);
					reference2.put("QCID", result2.getInt(1));
					//============================================================================
//					query1 = "Select Type_ID\r\n" + 
//					"from Question\r\n" + 
//					"Where QID = '" + reference.get("QID")+"'";
//					PS = connect.prepareStatement(query1);
//					result3 = PS.executeQuery();
//					result3.next();
					reference2.put("TypeID", ctdb.executeSelectedQuery("getQuestionTypeID", new String[] {Integer.toString(reference.getInt("QID"))}));

					
					//=========================================================
//					query1 = "Select Type_def\r\n" + 
//					"from ResponceType\r\n" + 
//					"Where Type_ID = '"+ reference2.get("TypeID")+"'";
//					PS = connect.prepareStatement(query1);
//					result3 = PS.executeQuery();
//					result3.next();
					reference2.put("TypeDef", ctdb.executeSelectedQuery("getTypeDefinition", new String[] {Integer.toString(reference2.getInt("TypeID"))}));
					
					//==========================================================
//					query1 = "Select Choice_val\r\n" + 
//					"from QuestionChoices\r\n" + 
//					"Where QCID = '"+ reference2.get("QCID")+"'";
//					PS = connect.prepareStatement(query1);
//					result3 = PS.executeQuery();
//					result3.next();
					reference2.put("ChoiceVal", ctdb.executeSelectedQuery("getQuestionChoiceValue", new String[] {Integer.toString(reference2.getInt("QCID"))}));
					
					//============================================================
//					query1 = "Select Descrip\r\n" + 
//					"from QuestionChoices\r\n" + 
//					"Where QCID = '"+ reference2.get("QCID")+"'";
//					PS = connect.prepareStatement(query1);
//					result3 = PS.executeQuery();
//					result3.next();
					reference2.put("QCDescrip", ctdb.executeSelectedQuery("getDescrip", new String[] {Integer.toString(reference2.getInt("QCID"))}));
					qcNum++;
				}
				qcNum = 1;
				questionNum++;	
			}

		}catch(Exception e){
			try {
				throw e;
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
		return Survey1;
		
	}
	public static void ResponseInsert(File file) throws SQLException {
		try {
			ConnectToDB ctdb = new ConnectToDB("3.82.210.103:3306", "MERIDB", "CS370", "HelloCat1");
			String Result, QRID, QID, User_ID,Type_ID, Responce;
			Scanner scanner = new Scanner(file);
			while (scanner.hasNextLine()) {
				String[] line = scanner.nextLine().split("-");
				QRID = line[0]+line[1]+line[2];
				QID = line[2];
				User_ID = line[1];
				Type_ID = line[3];
				Responce = line[4];
//				query1 = "Select QRID from QResponce\r\n" + 
//				"where User_ID= '"+ User_ID +"' and QID = '"+QID+"'";
//				PS = connect.prepareStatement(query1);
//				result = PS.executeQuery();
				Result = ctdb.executeSelectedQuery("getQRID", new String[] {User_ID,QID});
				if(Result == "not found") {
					if(Integer.parseInt(Type_ID) == 3) {
//						query1 = "insert into QResponce (QRID, QID, User_ID, Text)\r\n" + 
//						"values (?,?,?,?)";
//						PS = connect.prepareStatement(query1);
//						PS.setInt(1, Integer.parseInt(QRID));
//						PS.setInt(2, Integer.parseInt(QID));
//						PS.setInt(3, Integer.parseInt(User_ID));
//						PS.setString(4, Responce);
//						PS.execute();
						Result = ctdb.executeSelectedQuery("insertToQResponceText", new String[] {QRID,QID,User_ID,Responce});
						System.out.println("insert Successful");
					
					
					}else {
//						query1 = "insert into QResponce (QRID, QID, User_ID, Responce_val)\r\n" + 
//						"values (?,?,?,?)";
//	
//						PS = connect.prepareStatement(query1);
//						PS.setInt(1, Integer.parseInt(QRID));
//						PS.setInt(2, Integer.parseInt(QID));
//						PS.setInt(3, Integer.parseInt(User_ID));
//						PS.setString(4, Responce);
//						PS.execute();
						Result = ctdb.executeSelectedQuery("insertToQResponceVal", new String[] {QRID,QID,User_ID,Responce});
						System.out.println("insert Successful");
					}
				}else {
					System.out.println("Response already exists");
				}
				
				
			}
			scanner.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
//	public static void main(String[] args) {
//		SurveyExtIns Survey = new SurveyExtIns();
//		Connection connect = Survey.DBConnection();
//		String S_ID = "987";
//		String User_ID = "151";	
//		if(Survey.ExpDateCheck(S_ID, User_ID)) {
//			JSONObject Survey1 = Survey.SurveyTest(connect,S_ID, User_ID);
//			try {
//				File jsonfile = new File("C:\\Users\\cindy\\Desktop\\eclipse\\JSONSurvey.txt");
//				System.out.println("File created Successfully");
//				FileWriter file = new FileWriter(jsonfile);
//				file.write(Survey1.toString(5));
//				System.out.println("JSON added successfully");
//				try {
//					Survey.ResponseInsert(new File("C:\\Users\\cindy\\Desktop\\eclipse\\responceexample.txt"));
//				} catch (SQLException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				file.close();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
//		
//	}
}
