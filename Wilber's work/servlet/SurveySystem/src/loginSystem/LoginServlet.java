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

import surveyUser.surveyUser;
import surveyLogin.UserLogin;
/**
 * Servlet implementation class LoginServlet
 */
@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public LoginServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.setContentType("text/html");
		//1)take in user input
		HttpSession session = request.getSession();
		System.out.println("SessionId = " + session.getId());
		String userInput = request.getParameter("phoneNumber");
		surveyUser user = new surveyUser();
		user.setPhoneNumber(userInput);
		UserLogin login = new UserLogin(user);
		//2)check if user input exists in the database
		System.out.println("Checking to see if the phone exists...");
		if(login.checkInfoExists()) { 
			//2a) if the user input exists, send the otp code to sign in
			System.out.println("Phone number " + user.getPhoneNumber() + "exists, checking if its verified");
			if(login.checkIfVerified()) {
				System.out.println("Phone number is verified, for:");
				user.printUser();
				login.sendSmsCode();
				session.setAttribute("userPhone", user.getPhoneNumber());
				session.setAttribute("userId", user.getUserId());
				//take user to the verification page
				response.sendRedirect("http://52.23.169.99:80/SurveySystem/LoginVerify.html");
			}else {
				//user infomation is not verified, send them to verify phone number page
				writeToResponse(response, "The phone number " + user.getPhoneNumber() + " is not verified");
			}
			
		}
		else {
			//Phone number entered does not have an account created, redirect user to the register page.
			response.sendRedirect("index.html");
		}
			//2b) if the user input does not exist OR the phone numner is not verified
				//2b.a) If the number does not exist, tell prompt the user to go register.
				//2b.b) If the number does exist but is not verified.
					//.
		
		
		//doGet(request, response);
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
}
