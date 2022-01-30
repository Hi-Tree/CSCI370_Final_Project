package registerToSys;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import register.UserRegistration;
import surveyUser.surveyUser;

import javax.servlet.http.Cookie;

/**
 * Servlet implementation class UserVerification
 */
@WebServlet("/UserVerification")
public class UserVerificationServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public UserVerificationServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.setContentType("text/html");

		// retrieve the cookie which contains the user's Id
		HttpSession session = request.getSession(false);
		if (session != null) {
			String userInput = request.getParameter("sms-code");
			String name = (String)session.getAttribute("userName");
			String phone = (String)session.getAttribute("userNum");
			String email = (String)session.getAttribute("userEmail");
			surveyUser user = new surveyUser(name, email, phone); 
			user.setUserId((String) session.getAttribute("userId"));
			UserRegistration register = new UserRegistration(user);
			// Get input from user and compare it to the code in the db
			if (register.verifyRegCode(userInput)) {// if true, then user is verified
				// change status to verified and delete the the token.
				register.successfulVerification();
				writeToResponseHtml(response, "<h1>You have successfully registered and verified into the System! Click on the link below to "
						+ "login</h1> "
						+ "<a href=http://52.23.169.99:80/SurveySystem/login.html>Login Page</a>");
			} else {// user input is invalid, they must request a new sms code
					// delete the token and prompt the user if they want another code sent to them
				register.deleteOTPCode();
				register.sendSmsCode();
				response.sendRedirect("http://52.23.169.99:80/SurveySystem/registerVerify.html");
			}
		}else {
			writeToResponseHtml(response, "<h1>Error, Please click the following link to register"
					+ "</h1> <a href=http://52.23.169.99:80/SurveySystem/index.html>Registration Page</a>");
		}
		// doGet(request, response);
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
