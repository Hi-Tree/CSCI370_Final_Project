package surveyJson;

import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.json.*;

import surveyDB.DbConnection;

public class SurveyJson {

	private JSONObject surveyJson;
	private JSONArray questionArray;
	private Connection con;

	/*
	 * default constructor which will create a empty JSON object
	 */
	public SurveyJson() {
		surveyJson = new JSONObject();
		questionArray = new JSONArray();
	}

	/*
	 * Parameterized constructor which will create a JSON object and initialize the
	 * surveyJson object.
	 */
	public SurveyJson(int user_Id, int surv_Id, String surv_Title, String des) {
		surveyJson = new JSONObject();
		questionArray = new JSONArray();
		surveyJson.put("User_Id", user_Id);
		surveyJson.put("Survey_Id", surv_Id);
		surveyJson.put("Survey_Title", surv_Title);
		surveyJson.put("Survey_Desc", des);
		surveyJson.put("Question_List", questionArray);
	}

	/*
	 * This method will create a question Object for a question that has q_Type = 3.
	 * The integer 3 means that the question type is a short response question.
	 */
	static public JSONObject createQuestionObj(int q_Id, String q_Desc) {
		JSONObject question = new JSONObject();
		question.put("QID", q_Id);
		question.put("Q_Desc", q_Desc);
		question.put("Q_TypeId", 3);
		return question;
	}

	/*
	 * This method will create a question Object for a question that has q_Type = 1
	 * or q_Type = 2. The integer value 1 means the question is Multiple Choice, and
	 * the integer value 2 means that the questions is multiple answer
	 */
	static public JSONObject createQuestionObj(int q_Id, String q_Desc, int q_TypeId, JSONArray choiceArr) {
		JSONObject question = new JSONObject();
		question.put("QID", q_Id);
		question.put("Q_Desc", q_Desc);
		question.put("Q_TypeId", q_TypeId);
		question.put("Q_Choices", choiceArr);
		return question;
	}

	/*
	 * This method will create the choice Object which stores information about a
	 * particular choice for a question
	 */
	static public JSONObject createChoiceObj(int qc_Id, String c_Desc, String c_Val) {
		JSONObject choiceObj = new JSONObject();
		choiceObj.put("QCID", qc_Id);
		choiceObj.put("choiceVal", c_Val);
		choiceObj.put("cDesc", c_Desc);
		return choiceObj;
	}

	/*
	 * This method will create the choice array for a particular (multiple choice /
	 * multiple answer question)
	 */
	static public JSONArray createChoicesArray() {
		JSONArray choicesArr = new JSONArray();
		return choicesArr;
	}

	/*
	 * This method will add choices to the array that the user passed in as a
	 * parameter
	 */
	static public void addToChoicesArr(JSONArray c_Arr, JSONObject c_Obj) throws Exception {
		if (c_Obj.has("QCID") && c_Obj.has("choiceVal") && c_Obj.has("cDesc")) {
			c_Arr.put(c_Obj);
		} else {
			throw new Exception("The object passed in is not a choice Object!");
		}
	}

	/*
	 * This method will add question objects (That already have all of their fields
	 * with values assigned to them) to the questionArray.
	 * 
	 */
	public void addQuestion(JSONObject q_Obj) throws Exception {
		// must check to ensure that q_Obj is filled in
		if (q_Obj.has("QID") && q_Obj.has("Q_Desc")) {// first checks to make sure it has these values
			questionArray.put(q_Obj);
			System.out.println("The size of the question array is now: " + questionArray.length());
		} else {
			throw new Exception("The object passes in is not a question Object");
		}
	}

	/*
	 * connectToDB - This method will create a connection with the database. If a
	 * connection cannot be established the code will print out the Exception
	 * message.
	 */

	private void connectToDB() throws FileNotFoundException, SQLException, Exception {
		Connection connect;
		String[] loginCreds = DbConnection.getLoginCredentials();
		connect = DbConnection.initializeDBConnection(loginCreds[0], loginCreds[1], loginCreds[2]);
		con = connect;
	}

	/*
	 * This method will store the Survey Id, Survey Title, Survey Description, and
	 * the Id of the user who created it.
	 */
	private void store_User_Survey_Id_Title_Desc() {
		String querySurvey = "INSERT INTO Survey (Survey_ID, Title, Description, User_ID) " + "VALUES (?,?,?,?)";
		try {
			PreparedStatement stmt = con.prepareStatement(querySurvey);
			stmt.setInt(1, (int) surveyJson.get("Survey_Id"));
			stmt.setString(2, (String) surveyJson.get("Survey_Title"));
			stmt.setString(3, (String) surveyJson.get("Survey_Desc"));
			stmt.setInt(4, (int) surveyJson.get("User_Id"));
			stmt.execute();
			stmt.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	
	/*
	 * This method will store all the question objects in the question array.
	 * This method stores QID, Qdescription, Survey_ID, Type_ID.
	 */
	private void storeQuestions() {
		String queryQuestion = "INSERT INTO Question (QID, Qdescription, Survey_ID, Type_ID) "
				+ "VALUES (?,?,?,?)";
		try {
			for(int i = 0; i < questionArray.length(); i++) {
				JSONObject currentQuestion = questionArray.getJSONObject(i);
				PreparedStatement stmt = con.prepareStatement(queryQuestion);
				stmt.setInt(1, (int)currentQuestion.get("QID"));
				stmt.setString(2, (String)currentQuestion.get("Q_Desc"));
				stmt.setInt(3, (int) surveyJson.get("Survey_Id"));
				stmt.setInt(4, (int)currentQuestion.get("Q_TypeId"));
				stmt.execute();
				if((int)currentQuestion.get("Q_TypeId") == 1 || 
						(int)currentQuestion.get("Q_TypeId") == 2) {
					storeChoices(currentQuestion.getJSONArray("Q_Choices"), (int)currentQuestion.get("QID"));
				}
				stmt.close();
			}//closes for loop
		}catch(Exception ex) {
			ex.printStackTrace();
		}
		
	}
	
	
	/*
	 * This method stores the choices for the current question with q_Id
	 */
	private void storeChoices(JSONArray c_Obj, int q_Id) {
		String queryChoices = "INSERT INTO QuestionChoices (QCID, QID, Choice_val, Descrip) "
				+ "VALUES (?,?,?,?)";
		try {
			for(int i = 0; i < c_Obj.length(); i++) {
				JSONObject currentChoice = c_Obj.getJSONObject(i);
				PreparedStatement stmt = con.prepareStatement(queryChoices);
				stmt.setInt(1, (int)currentChoice.get("QCID"));
				stmt.setInt(2, q_Id);
				stmt.setString(3, (String)currentChoice.get("choiceVal"));
				stmt.setString(4, (String)currentChoice.get("cDesc"));
				stmt.execute();
				stmt.close();
			}//closes for loop
		}catch(Exception ex) {
			ex.printStackTrace();
		}
		
	}

	/*
	 * Stores the survey into the database
	 */
	public void storeSurvey() {
		try {
			connectToDB();
			store_User_Survey_Id_Title_Desc();
			storeQuestions();
			con.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/*
	 * Prints the survey as a Json object This was used for testing purposes
	 */
	public void printSurveyJson() {
		System.out.println(surveyJson.toString(4));
	}

	/* this was used for testing the methods 
	public static void main(String[] args) {
		// testing
		JSONObject surveyJson = new JSONObject();// Json Object of the entire survey
		JSONObject question = new JSONObject();// A Question Object, which stores a specific questions information
		JSONArray questionArray = new JSONArray(); // array used to store all the question objects
		JSONArray choices = new JSONArray(); // array used to Store the multiple choices
		surveyJson.put("UserId", "123");
		surveyJson.put("SurveyId", "001");
		surveyJson.put("SurveyTile", "Test JSON Object");
		surveyJson.put("SurveyDescription", "TESTING");

		// testing question object
		question.put("QID", "1");
		question.put("qDesc", "Question Description");
		question.put("qType", "Multiple Choice");

		// testing choices object
		choices.put("Choices 1");
		choices.put("Choices 2");
		choices.put("Choices 3");
		choices.put("Choices 4");

		question.put("qChoices", choices);
		questionArray.put(question);

		// testing questionArray object
		JSONObject question2 = new JSONObject();
		question2.put("QID", "2");
		question2.put("qDesc", "Question Description 2");
		question2.put("qType", "Multiple Answer");

		question2.put("qChoices", choices);
		questionArray.put(question2);

		surveyJson.put("questionList", questionArray);
		System.out.println(surveyJson.toString(4));

		SurveyJson survey = new SurveyJson(436, 456, "Testing SurveyJson Title", "Testing SurveyJson Des");

		JSONObject c1 = SurveyJson.createChoiceObj(1, "Wilber", "A");
		JSONObject c2 = SurveyJson.createChoiceObj(2, "Max", "B");
		JSONObject c3 = SurveyJson.createChoiceObj(3, "Milo", "C");
		JSONObject c4 = SurveyJson.createChoiceObj(4, "Pizza", "D");
		JSONObject c5 = SurveyJson.createChoiceObj(5, "Pasta", "E");
		JSONObject c6 = SurveyJson.createChoiceObj(6, "Mac & Cheese", "F");
		JSONArray cArr = SurveyJson.createChoicesArray();
		JSONArray cArr2 = SurveyJson.createChoicesArray();
		
		JSONObject q1 = SurveyJson.createQuestionObj(1, "What is your name?", 1, cArr);
		JSONObject q3 = SurveyJson.createQuestionObj(3, "What is your Favorite Food?", 2, cArr2);
		System.out.println(q1.toString(4));
		JSONObject q2 = SurveyJson.createQuestionObj(2, "Time?");

		try {
			SurveyJson.addToChoicesArr(cArr, c1);
			SurveyJson.addToChoicesArr(cArr, c2);
			SurveyJson.addToChoicesArr(cArr, c3);
			SurveyJson.addToChoicesArr(cArr2, c4);
			SurveyJson.addToChoicesArr(cArr2, c5);
			SurveyJson.addToChoicesArr(cArr2, c6);

			survey.addQuestion(q1);
			survey.addQuestion(q2);
			survey.addQuestion(q3);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		// JSONArray q = SurveyJson.createChoiceObj(choices);
		// System.out.println(sur.toString(4));
		System.out.println("Testing");
		survey.printSurveyJson();

		survey.storeSurvey();

	}
	*/

}
