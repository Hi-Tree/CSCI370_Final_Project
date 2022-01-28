package surveyLogin;

import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;
import java.util.Scanner;

import smsVerification.smsVerify;
import surveyDB.DbConnection;
import surveyUser.surveyUser;

public class UserLogin {

	surveyUser user;

	public UserLogin(surveyUser us) {
		user = us;
	}

	// generates a 6 digit sms otp used for verifying a phone number
	static public String generateCode() {
		String str = "0123456789";
		Random rand = new Random();
		char[] code = new char[6];

		for (int i = 0; i < 6; i++) {
			char c = str.charAt(rand.nextInt(str.length()));
			while (i == 0 && c == '0') { // since this id will later be casted to integer, its important that the id
											// does not start with a 0
				c = str.charAt(rand.nextInt(str.length()));
			}
			code[i] = c;
		}
		return String.valueOf(code);
	}

	public void sendSmsCode() {
		// store the code
		try {
			Connection con = connectToDB();
			String query = "INSERT INTO Usr_verify (User_ID, Reg_code)" + " VALUES (?, ?)";
			String smsCode = generateCode();
			PreparedStatement statement = con.prepareStatement(query);
			statement.setString(1, user.getUserId());
			statement.setString(2, smsCode);
			statement.execute();
			statement.close();
			con.close();
			// send the code
			smsVerify.sendVerification(user.getPhoneNumber(), smsCode);
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}

	}

	// returns true if the registration codes match
	// returns false if the registration codes don't match
	public boolean verifyOTPCode(String userInputCode) {
		String query = "SELECT Reg_Code FROM Usr_verify " + "WHERE User_Id = ?";
		String codeInDB = "abcdef"; // random dummy string
		try {
			Connection con = connectToDB();
			PreparedStatement statement = con.prepareStatement(query);
			ResultSet rs;
			statement.setString(1, user.getUserId());
			rs = statement.executeQuery();
			// if/else statement used for testing
			if (rs.next()) {
				codeInDB = rs.getString("Reg_Code");
				System.out.println("Comparing " + userInputCode + ", vs real code " + codeInDB);
			} else {
				System.out.println("Error: There is no OTP code in the database!");
			}
			con.close();
			statement.close();
			rs.close();
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}

		return (userInputCode.equals(codeInDB));
	}

	// deletes the row which contains the verfification code
	public void deleteOTPCode() {
		String query = "DELETE FROM Usr_verify " + "WHERE User_Id = ?";
		try {
			Connection con = connectToDB();
			PreparedStatement statement = con.prepareStatement(query);
			statement.setString(1, user.getUserId());
			statement.execute();
			con.close();
			statement.close();
			System.out.println("Finished deleting Code for user " + user.getUserId());
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}
	}

	public boolean checkIfVerified() {
		String query = "SELECT * FROM Usr_Info WHERE Phone = ?";
		String userStatus = "unverified";
		try {
			Connection con = connectToDB();
			PreparedStatement statement = con.prepareStatement(query);
			ResultSet rs;
			statement.setString(1, user.getPhoneNumber());
			rs = statement.executeQuery();
			System.out.println("Query2: " + statement.toString());
			if (rs.next()) {
				System.out.println("--------UserId: " + rs.getString("User_Id") + "\nName: " + rs.getString("Name")
				+ "\nstatus: " + rs.getString("status"));
				System.out.println("user is verified");
				userStatus = rs.getString("status");
				user.setUserId(rs.getString("User_Id"));
			} else {
				System.out.println("user is not verified");
			}
			con.close();
			statement.close();
			rs.close();
		} catch (Exception ex) {
			System.out.println("verify error: " + ex.getMessage());
		}
		System.out.println("the status is: " + userStatus);
		return (userStatus.equals("verified"));

	}

	// checks if phone number or email is already has registered before
	// returns true if the info is taken/exists
	// returns false if the info doesn't exist
	// consider returning an integer which represents what is taken
	public boolean checkInfoExists() {
		try {
			String querySearch = "SELECT * FROM Usr_Info " + "WHERE Phone" + "= ?";
			Connection con = connectToDB();
			PreparedStatement statement = con.prepareStatement(querySearch);
			statement.setString(1, user.getPhoneNumber());
			System.out.println("Query2: " + statement.toString());
			ResultSet rs = statement.executeQuery();
			if (!rs.next()) {// Phone number does not exist
				System.out.println("The Phone number " + user.getPhoneNumber()
						+ " is not a registered phone number in the system");
			} else {// Phone number exists
				System.out.println("---\nUsed by User ID: " + rs.getString(1));
				return true;
			}
			con.close();
			statement.close();
			rs.close();
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}
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
/*
	public static void main(String args[]) {
		surveyUser user = new surveyUser();
		user.setPhoneNumber("5167826591");
		UserLogin test = new UserLogin(user);
		//2)check if user input exists in the database
		if(test.checkInfoExists()) {
		//2a) if the user input exists, send the otp code to sign in
			//...
			if(test.checkIfVerified()) {
			//test.sendSmsCode();
			Scanner scnr = new Scanner(System.in);
			String codeInput="";
			System.out.println("Enter the verification code: ");
			codeInput = scnr.nextLine();
			System.out.println("The user entered : " + codeInput);
			if(test.verifyOTPCode(codeInput))
				System.out.println("The codes match!");
			else
				System.out.println("The codes do not match!!!");
			}
			else {
				System.out.println("The phone number: " + user.getPhoneNumber() + " is not verified");
			}
		}
		else {
			System.out.println("The phone number: " + user.getPhoneNumber() + " does not exists in database");
		}
		
		//2b) if the user input does not exist OR the phone numner is not verified
			//2b.a) If the number does not exist, tell prompt the user to go register.
			//2b.b) If the number does exist but is not verified.
	

	}
*/
}
