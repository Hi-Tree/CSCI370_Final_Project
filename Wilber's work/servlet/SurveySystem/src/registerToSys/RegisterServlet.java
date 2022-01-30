package registerToSys;

import java.awt.image.BufferedImage;
import javax.imageio.*;
import java.io.InputStream;//added
import java.sql.*;//added
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Scanner;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

import surveyUser.surveyUser;


import register.UserRegistration;


@WebServlet("/RegisterServlet")
/*
 * @MultipartConfig(fileSizeThreshold = 1024 * 1024 * 10, // 10 MB maxFileSize =
 * 1024 * 1024 * 50, // 50 MB maxRequestSize = 1024 * 1024 * 100) // 100 MB
 */
public class RegisterServlet extends HttpServlet {

	private static final long serialVersionUID = 205242440643911308L;
	/**
	 * Directory where uploaded files will be saved, its relative to the web
	 * application directory.
	 */
	
	public RegisterServlet() {
		super();
	}
	/*
	// Method to handle GET method request.
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// Set response content type
		response.setContentType("text/html");

		PrintWriter out = response.getWriter();
		String title = "Reading All Form Parameters";
		String docType = "<!doctype html public \"-//w3c//dtd html 4.0 " + "transitional//en\">\n";

		out.println(docType + "<html>\n" + "<head><title>" + title + "</title></head>\n"
				+ "<body bgcolor = \"#f0f0f0\">\n" + "<h1 align = \"center\">" + title + "</h1>\n"
				+ "<table width = \"100%\" border = \"1\" align = \"center\">\n" + "<tr bgcolor = \"#949494\">\n"
				+ "<th>Param Name</th>" + "<th>Param Value(s)</th>\n" + "</tr>\n");

		JSONArray Survey_Results = new JSONArray();
		Enumeration paramNames = request.getParameterNames();
		while (paramNames.hasMoreElements()) {
			String paramName = (String) paramNames.nextElement();
			out.print("<tr><td>" + paramName + "</td>\n<td>");
			String[] paramValues = request.getParameterValues(paramName);
			for (int i = 0; i < paramValues.length; i++) {
				System.out.println("Question " + i + ", response: " + paramValues[i]);
				JSONObject q = new JSONObject();
				q.put(Integer.toString(i), paramValues[i]);
				Survey_Results.add(q);
				System.out.println(Survey_Results.size());
			}

			// Read single valued data
			if (paramValues.length == 1) {
				String paramValue = paramValues[0];
				if (paramValue.length() == 0)
					out.println("<i>No Value</i>");
				else
					out.println(paramValue);
			} else {
				// Read multiple valued data
				out.println("<ul>");

				for (int i = 0; i < paramValues.length; i++) {
					out.println("<li>" + paramValues[i]);
				}
				out.println("</ul>");
			}
		}
		out.println("</tr>\n</table>\n</body></html>");

		System.out.println("\nPrinting the JSONObject below\n");
		printJSON(Survey_Results);

	}// closes doGet method
*/
	// Method to handle POST method request.
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("text/html");
		String userName = request.getParameter("fullName");
		System.out.println("userName is :" + userName);
		String userEmail = request.getParameter("emailAddress");
		System.out.println("userEmail is :" + userEmail);
		String userPhone = request.getParameter("phoneNumber");
		System.out.println("userPhone is :" + userPhone);
		surveyUser user = new surveyUser(userName, userEmail, userPhone);
		UserRegistration register = new UserRegistration(user);
		register.registerUser();
		user.printUser();
		HttpSession session=request.getSession();
		session.setAttribute("userId", user.getUserId());
		session.setAttribute("userName", userName);
		session.setAttribute("userEmail", userEmail);
		session.setAttribute("userNum", userPhone);
		response.sendRedirect("http://52.23.169.99:80/SurveySystem/registerVerify.html");
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

}
