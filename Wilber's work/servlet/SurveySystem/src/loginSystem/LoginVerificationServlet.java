package loginSystem;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import surveyLogin.UserLogin;
import surveyUser.surveyUser;

/**
 * Servlet implementation class LoginVerificationServlet
 */
@WebServlet("/LoginVerificationServlet")
public class LoginVerificationServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public LoginVerificationServlet() {
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
		HttpSession session = request.getSession(false);
		System.out.println(session);

		if (session != null) {// after user sends login
			String userInput = request.getParameter("sms-code");
			System.out.println("Testing User Input: " + userInput);
			surveyUser user = new surveyUser();
			user.setUserId((String) session.getAttribute("userId"));
			user.setPhoneNumber((String) session.getAttribute("userPhone"));
			UserLogin login = new UserLogin(user);
			if (login.verifyOTPCode(userInput)) {// if the codes match, send the user to survey creation
				login.deleteOTPCode();
				response.sendRedirect("http://52.23.169.99:80/SurveySystem/CreateSurvey.html");
			} else {
				login.deleteOTPCode();
				writeToResponseHtml(response,
						"You failed to enter the correct OTP, pleese try again <a href=http://52.23.169.99:80/SurveySystem/login.html>Login</a>");
			}
		} else {// send user to login page
			writeToResponseHtml(response, "<h1>Error, Please click the following link to Login"
					+ "</h1> <a href=http://52.23.169.99:80/SurveySystem/login.html>Login Page</a>");
		}
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
