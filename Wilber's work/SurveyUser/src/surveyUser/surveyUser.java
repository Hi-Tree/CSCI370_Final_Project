package surveyUser;

public class surveyUser {
	
	private String name, email, phoneNumber, user_Id, status, role;
	
	//default constructor, sets all user information to default values
	//for now it does not take into consideration the role or status
	public surveyUser() {
		name = "";
		email = null;
		phoneNumber = null;
		status = "unverified";
		role = null;
	}
	
	//Parameterized constructor for unregistered user who has no information stored. An unregistered user is someone who doesn't have a 
	//user_id in the database associated with their email and phone number and their phone number is not verified.
	//sets all user information to 
	//the values entered from the parameters
	//MIGHT NEED TO BE CHANGED!
	public surveyUser(String fullName, String emailAddress, String phoneNum){
		name = fullName;
		email = emailAddress;
		phoneNumber = phoneNum;
		user_Id = null;
		status = "unverified";
		role = null;
	}
	
	
	//returns the user's name
	public String getName() {
		return name;
	}
	
	//returns the user's email
	public String getEmail() {
		return email;
	}
	
	//returns the user's PhoneNumber
	public String getPhoneNumber() {
		return phoneNumber;
	}
	
	public String getUserId() {
		return user_Id;
	}
	
	public String getStatus() {
		return status;
	}
	
	//might not be necessary
	public String getRole() {
		return role;
	}
	
	public void setName(String userName) {
		name = userName;
	}
	
	public void setEmail(String userEmail) {
		email = userEmail;
	}
	

	public void setPhoneNumber(String userNumber) {
		phoneNumber = userNumber;
	}
	//Only user when registering a new user and 
	//assigning a User_ID
	public void setUserId(String id) {
		user_Id = id;
	}
	
	public void setStatus(String stat) {
		status = stat;
	}
	
	public void setRole(String userRole) {
		role = userRole;
	}
	
	public void printUser() {
		System.out.println("\n name: "+ name + "\n Email: "+ email +"\n Phone Number: " + phoneNumber 
		+ "\n user_Id: " + user_Id  + "\n status: " + status + "\n Role: " + role + "\n-----");
	}
	
	

}
