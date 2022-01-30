package SurveyCreation;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import surveyJson.SurveyJson;

import org.json.*;

/*
 * Servlet implementation class CreateSurvey
 */
@WebServlet("/CreateSurvey")
public class CreateSurvey extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public CreateSurvey() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}
	
	//creates a 3 digit id for the survey
	//This method should not be called until we can verify 
	//that the user has filled in all the necessary
	//information.
	public String genSurveyId() {
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
	
	
	
	private static String genChoiceVal(){
        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 2;
        Random random = new Random();

        String generatedString = random.ints(leftLimit, rightLimit + 1)
        .limit(targetStringLength)
        .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
            .toString();
            
        return generatedString;

    }
	

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.setContentType("text/html");
		HttpSession session = request.getSession(false);
		System.out.println(session);
		if (session != null) {
			int userNum = Integer.valueOf((String)session.getAttribute("userId"));
			int sId = Integer.valueOf(genSurveyId());
			String surveyTitle = request.getParameter("surveyTitleName");
			String surveyDesc = request.getParameter("surveyDescName");
			SurveyJson survey = new SurveyJson(userNum,  sId, surveyTitle, surveyDesc);
			ArrayList<String> questions = new ArrayList<String>();
			ArrayList<String> choices = new ArrayList<String>();
			Enumeration<String> paramNames = request.getParameterNames();
			while (paramNames.hasMoreElements()) {
				String paramName = paramNames.nextElement();
				if(checkForQC(paramName)) {//if question or choice
					String[] currentParamNameSplit = paramName.split("-");
					if(currentParamNameSplit.length == 3) {//question
						questions.add(paramName);
					}else {//choice
						choices.add(paramName);
					}
				}
				
			}
				
			int i = 0;
			for(int j = 0; j < questions.size(); j++) {
				String currentQ = questions.get(j);
				JSONArray c_ArrayJson = SurveyJson.createChoicesArray();
				//choices array for the current Question
				while(i < choices.size() && (currentQ.charAt(2) == choices.get(i).charAt(0))) {
					String[] choicesSplitUp = choices.get(i).split("-");
					int qcid = Integer.valueOf(choicesSplitUp[1]);
					//int qcid = choices.get(i).charAt(2) - '0';
					String c_Desc = request.getParameter(choices.get(i));
					JSONObject c_Obj = SurveyJson.createChoiceObj(qcid, c_Desc, genChoiceVal());
					c_Obj.toString(4);
					try {
					SurveyJson.addToChoicesArr(c_ArrayJson, c_Obj);
					i++;
					}catch(Exception ex) {
						ex.printStackTrace();
					}
				}
				
				//create Question Obj
				//int qid = currentQ.charAt(2)-'0';
				String[] questionsSplitUp = questions.get(j).split("-");
				int qid = Integer.valueOf(questionsSplitUp[1]);
				String qDesc = request.getParameter(currentQ);
				if(currentQ.charAt(4) == '1' || currentQ.charAt(4) == '2') {//mc/ma
					JSONObject q_Obj = SurveyJson.createQuestionObj(qid, qDesc, currentQ.charAt(4) - '0', c_ArrayJson);
					q_Obj.toString(4);
					try {
					survey.addQuestion(q_Obj);
					}catch(Exception ex) {
						ex.printStackTrace();
					}
				}
				else {//response
					JSONObject q_Obj = SurveyJson.createQuestionObj(qid, qDesc);
					try {
					survey.addQuestion(q_Obj);
					}catch(Exception ex) {
						ex.printStackTrace();
					}
				}
			}//closes entire foor loop
			//JSONObject currentQuestion = new JSONObject(choices.get(i).charAt(2), );
			
			System.out.println("---------Survey Json BELOW------------");
			survey.printSurveyJson();
			survey.storeSurvey();
			System.out.println("TESTING NEW CHOICE VAL LENGTH: ");
			response.sendRedirect("http://52.23.169.99:80/SurveySystem/survey?usrID="+ userNum +"&surveyID="+ sId);
		}else {
			writeToResponseHtml(response, "<h1>Error, Please click the following link to Login"
					+ "</h1> <a href=http://52.23.169.99:80/SurveySystem/login.html>Login Page</a>");
		}

		// doGet(request, response);
	}
	
	//returns true if the paramName passes is not a question or question choice
	private boolean checkForQC(String paramName) {
		if(!paramName.contains("-")) {
			return false;
		}
		return true;
	}
	
	private void writeToResponse(HttpServletResponse resp, String results) throws IOException {
		PrintWriter writer = new PrintWriter(resp.getOutputStream());
		resp.setContentType("text/plain");

		if (results.isEmpty()) {
			writer.write("No results found.");
		} else {
			writer.write(results);
		}
		writer.close();
	}

	private void writeToResponseHtml(HttpServletResponse resp, String results) throws IOException {
		PrintWriter writer = new PrintWriter(resp.getOutputStream());
		resp.setContentType("text/html");
		if (results.isEmpty()) {
			writer.write("No results found.");
		} else {
			writer.write(results);
		}
		writer.close();
	}

}
