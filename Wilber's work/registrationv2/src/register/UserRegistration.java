package register;

import java.util.*;
import surveyDB.DbConnection;

import java.io.FileNotFoundException;
import java.sql.*;
import java.util.Random;
import surveyUser.surveyUser;
import smsVerification.smsVerify;
//CHANGE NAME TO: REGISTRATION CLASS
public class UserRegistration {

	// method for inserting a registering user
	private surveyUser user;
	// create the dbTransaction Object which will store the information need
	// to begin any insertion, deletetion, and retrieval.
	public UserRegistration() {
		user = new surveyUser();
	}
	
	// This user should have a userID and phone number stored
	public UserRegistration(surveyUser survUser) {
		user = survUser;
	}

	/* MUST ADD CASE WHEN IT KEEPS GENNING ID UNTIL THE IT GIVES A RESULT THAT IS NOT ALREADY IN USE
	 * genUserId - a function which will randomly generate a string which represents
	 * a userId. The length of this string will be of length 3
	 *
	 * @return A string which represents a userId code
	 */
	public static String genUserId() {
		String str = "0123456789";
		Random rand = new Random();
		char[] code = new char[3];

		for (int i = 0; i < 3; i++) {
			char c = str.charAt(rand.nextInt(str.length()));
			while(i == 0 && c == '0') { //since this id will later be casted to integer, its important that the id does not start with a 0
				 c = str.charAt(rand.nextInt(str.length()));
			}
			code[i] = c;
		}
		return String.valueOf(code);
	}
	
	//generates a 6 digit sms otp used for verifying a phone number
	static public String generateCode() {
		String str = "0123456789";
		Random rand = new Random();
		char[] code = new char[6];
	
		for(int i = 0; i < 6; i++) {
			char c = str.charAt(rand.nextInt(str.length()));
			while(i == 0 && c == '0') { //since this id will later be casted to integer, its important that the id does not start with a 0
				 c = str.charAt(rand.nextInt(str.length()));
			}
			code[i] = c; 
		}
		return String.valueOf(code);
	}

	public void sendSmsCode() {
		//store the code
		try {
		Connection con = connectToDB();
		String query = "INSERT INTO Usr_verify (User_ID, Reg_code)"
				+ " VALUES (?, ?)";
		String smsCode = generateCode();
		PreparedStatement statement = con.prepareStatement(query);
		statement.setString(1, user.getUserId());
		statement.setString(2, smsCode);
		statement.execute();
		statement.close();
		con.close();
		//send the code
		smsVerify.sendVerification(user.getPhoneNumber(), smsCode);
		}catch(Exception ex) {
			System.out.println(ex.getMessage());
		}
		
	}
	// This method will begin the registration process for a user
	// it will insert the users phone numnber, name, email and verification status
	// as "no".
	// For the sake of this project we only use Id's of length 3
	// make this a boolean. Return true when Successful and return false when failed
	public void registerUser() {

		// check if this person is in the database already!
		if (checkInfoExists()) {
			System.out.println("The email/phone number is already in use");
		} else {
			System.out.println("----\nRegistering...\n----");
			String queryInsertion = "INSERT INTO Usr_Info (User_ID, Name, Email, status, phone) "
					+ "VALUES (?, ?, ?, ?, ?)";
			String newId = genUserId();
			user.setUserId(newId);
			try {
				Connection con = connectToDB();
				PreparedStatement statement = con.prepareStatement(queryInsertion);
				statement.setInt(1, Integer.parseInt(user.getUserId()));
				statement.setString(2, user.getName());
				statement.setString(3, user.getEmail());
				statement.setString(4, user.getStatus());
				statement.setString(5, user.getPhoneNumber());
				statement.execute();
				sendSmsCode();
				con.close();
				statement.close();
				print();
				System.out.println("Successfully stored the user");
			} catch (Exception ex) {
				System.out.println("----\nThe following Error has occured: \n" + ex.getMessage() + "----\n");
			}
		}
	}
	
	//returns true if the registration codes match
	//returns false if the registration codes don't match
	public boolean verifyRegCode(String userInputCode) {				
		String query = "SELECT Reg_Code FROM Usr_verify "
				+ "WHERE User_Id = ?";
		String codeInDB = "abcdef"; //random dummy string
		try {
		Connection con = connectToDB();
		PreparedStatement statement = con.prepareStatement(query);
		ResultSet rs;
		statement.setString(1, user.getUserId());
		rs = statement.executeQuery();
		//if/else statement used for testing
		if(rs.next()) {
			codeInDB = rs.getString("Reg_Code");
		}
		else {
			System.out.println("No active registration code was found");
		}
		con.close();
		statement.close();
		rs.close();
		}catch(Exception ex) {
			System.out.println(ex.getMessage());
		}
		
		return (userInputCode.equals(codeInDB));
	}
	
	//changes the status to "verified" and deletes the OTP
	public void successfulVerification() {
		String query = "UPDATE Usr_Info SET status = ?"
				+ "WHERE User_Id = ?";
		try {
			Connection con = connectToDB();
			PreparedStatement statement = con.prepareStatement(query);
			statement.setString(1, "verified");
			statement.setString(2, user.getUserId());
			statement.execute();
			deleteOTPCode();
			con.close();
			statement.close();
		}catch(Exception ex) {
			System.out.println(ex.getMessage());
		}
	}
	
	//deletes the row which contains the verfification code
	public void deleteOTPCode() {
		String query = "DELETE FROM Usr_verify "
				+ "WHERE User_Id = ?";
		try {
			Connection con = connectToDB();
			PreparedStatement statement = con.prepareStatement(query);
			statement.setString(1, user.getUserId());
			statement.execute();
			con.close();
			statement.close();
		}catch(Exception ex) {
			System.out.println(ex.getMessage());
		}
	}
	
	

	//checks if phone number or email is already has registered before
	//returns true if the info is taken/exists
	//returns false if the info doesn't exist
	//consider returning an integer which represents what is taken
	public boolean checkInfoExists() {
		String[] email_Phone = { "EMAIL", "Phone" };
		try {
			for (int j = 0; j < 2; j++) {
				String querySearch = "SELECT * FROM Usr_Info " + "WHERE " + email_Phone[j] + "= ?";
				Connection con = connectToDB();
				PreparedStatement statement = con.prepareStatement(querySearch);
				if (j == 0) // 0 checks email
					statement.setString(1, user.getEmail());
				else // 1 checks phone
					statement.setString(1, user.getPhoneNumber());
				System.out.println("Query: " + statement.toString());
				ResultSet rs = statement.executeQuery();
				if (!rs.next()) {// user does not exist
					if (j == 0)
						System.out.println("The email " + user.getEmail() + " is not being used");
					else
						System.out.println("The Phone number " + user.getPhoneNumber() + " is not being used");
				} else {// user exists
					System.out.println("---\nUsed by User ID: " + rs.getString(1));
					if (j == 0)
						System.out.println("The email " + user.getEmail() + " is taken\n---");
					else
						System.out.println("The Phone number " + user.getPhoneNumber() + " is taken\n---");
					
					return true;
				}
				con.close();
				statement.close();
				rs.close();
			}
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}
		System.out.println("No user exists with " + user.getPhoneNumber() + ", and " + user.getEmail());
		return false;
	}
	
	
	

	/*
	 * connectToDB - This method will create a connection with the database. If a
	 * connection cannot be established the code will print out the Exception
	 * message.
	 */

	private Connection connectToDB() throws FileNotFoundException, SQLException, Exception {
		Connection connect;
		String[] loginCreds = DbConnection.getLoginCredentials();
		connect = DbConnection.initializeDBConnection(loginCreds[0], loginCreds[1], loginCreds[2]);
		return connect;
	}
	
	public void print() {
		user.printUser();
	}

	/*
	public static void main(String[] args) {

		// surveyUser user1 = new surveyUser("Wilber Claudio",
		// "WilberClaudio@gmail.com", "5165906332", "123", "unverified");

		surveyUser user1 = new surveyUser();
		user1.setUserId("119");
		user1.setPhoneNumber("5165906332");
		UserRegistration test = new UserRegistration(user1);
		//test.deleteOTPCode();
		test.sendSmsCode();
		
		//System.out.println("Checking to see if " + user1.getPhoneNumber() + " or " + user1.getEmail() + " is in use");
		//test.checkInfoExists();
	}
	*/
}
