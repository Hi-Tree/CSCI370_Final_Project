package surveyRetrieve;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;

import javax.servlet.ServletContext;


import org.json.JSONObject;

import SESurvey.SurveyExtIns;
import surveyDB.DbConnection;


//This is Meri Khurshudyancode for the survey retrieval. I added this to show
//the survey after it has been created
/**
 * Servlet implementation class JavaSurvey
 */
@WebServlet("/survey")
public class JavaSurvey extends HttpServlet{
	public void doGet(HttpServletRequest request, HttpServletResponse response)
				throws ServletException, IOException {
		
		response.setContentType("text/html");
		String usrID = request.getParameter("usrID");
		String surveyID = request.getParameter("surveyID");
		
		
		//////////////////////////////////////////////// Entering into txt file //////////////////////////////////////
		
		String filename = "/WEB-INF/results.txt";
		ServletContext context = getServletContext();
		File file = new File(context.getRealPath(filename));
		BufferedWriter writer = new BufferedWriter(new FileWriter(file, false));
        
		/////////////////////////////////////// Requesting all of the parmeters from submit /////////////////////////
       
        ArrayList<String[]> res = new ArrayList<String[]>();
		Enumeration<String> parameterNames = request.getParameterNames();
		while (parameterNames.hasMoreElements()) {
			String paramName = parameterNames.nextElement();
			String[] paramValues = request.getParameterValues(paramName);
			for (int y = 0; y < paramValues.length; y++) {
				String paramValue = paramValues[y];
				String[] param = {paramName.replaceAll("-","#"), paramValue.replaceAll("-","#")};
				res.add(param);
			}
		}
	    int u = 0;
		while(u<res.size()-1) {
			if(res.get(u)[0].equals(res.get(u+1)[0])){
				res.get(u)[1] = res.get(u)[1] +"|"+ res.get(u+1)[1].substring(res.get(u+1)[1].lastIndexOf('#') + 1);
				res.remove(u+1);
				int test = u+1;
			}
			if( ((u + 1) != res.size()) && (!res.get(u)[0].equals(res.get(u+1)[0])) ){
				u+=1;
			}
		}
	    
		
		//writes survey taker's reponses down
		for(int k = 0; k < res.size(); k++) {
			writer.write(res.get(k)[1]);
			writer.write("\n");
		}
		writer.close();
	    
	    //////////////////////////////////////////////// DISPLAY TXT FILE ///////////////////////////////////////////
		
	      /*  InputStream is = context.getResourceAsStream(filename);
	        if (is != null) {
	            InputStreamReader isr = new InputStreamReader(is);
	            BufferedReader reader = new BufferedReader(isr);
	            PrintWriter writers = response.getWriter();
	            String text;
	            while ((text = reader.readLine()) != null) {
	                writers.println(text + "</br>");
	            }
	        }
	        
		*/
		///////////////////////////////////////////// Insert to DB ////////////////////////////////////////////////////
		try {
		Connection c = connectToDB();
	    JSONObject jo = SurveyExtIns.SurveyTest(c, surveyID, usrID);
	    
		long lines = 0;
	      try (BufferedReader reader = new BufferedReader(new FileReader(context.getRealPath(filename)))) {
	          while (reader.readLine() != null) lines++;
	      } catch (IOException e) {
	          e.printStackTrace();
	      }
	      
	      if(lines>2) {
	    	  File file2 = new File(context.getRealPath(filename));
	    	  System.out.println("FILE2 TEST:" + file2.getAbsolutePath());
	    	  try {
				SurveyExtIns.ResponseInsert(file2, c);
				response.getWriter().print("Thank you ");
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	      }
	      
		  if(request.getParameter("1") != null) {
			  String responses = "";
			  for(int i = 0;i<res.size();i++) {responses = responses + res.get(i)[1];}
	      response.getWriter().print(request.getParameter("1").split("-")[0]+" Was Submitted! Here are the survey responses \n\n" + responses);
		  }
	    
        
		int i = 1;
		int nums = jo.names().length() - 4;//number of actual questions
		while(i <= nums) {
			int j = 1;
			response.getWriter().print("<form method='get' action='survey'>");//starts at beginning of question
			response.getWriter().print("Question "+Integer.toString(i)+"<br>");
			JSONObject QE = ((JSONObject)jo.get("Question "+Integer.toString(i)));
			String Q = (String)QE.get("Question Description");
			response.getWriter().print(Q+"<br>");
			response.getWriter().print("<br>");
			response.getWriter().print("<form method='get' action='survey'>");//starts in beginning of question
			while(j <= ((JSONObject)jo.get("Question "+Integer.toString(i))).names().length() - 2) {
				JSONObject QCH = (JSONObject)((JSONObject)jo.get("Question "+Integer.toString(i))).get("QC"+Integer.toString(j));
				String type = (String)QCH.get("TypeDef");
				if(type.equals("Radio")) {
					response.getWriter().print("<input type='radio' id='"+QCH.get("QCID")+"' name='"+Integer.toString(i)+"' value='"+String.valueOf(surveyID)+"-"+String.valueOf(usrID)+"-"+String.valueOf(QE.get("QID"))+"-"+String.valueOf(QCH.get("TypeID"))+"-"+QCH.get("ChoiceVal")+"'/>");
					response.getWriter().print("<label for='"+QCH.get("QCID")+"'>"+QCH.get("QCDescrip")+"</label><br>");	
				}
			    if(type.equals("Checkbox")) {
					response.getWriter().print("<input type='checkbox' name='"+Integer.toString(i)+"' value='"+String.valueOf(surveyID)+"-"+String.valueOf(usrID)+"-"+String.valueOf(QE.get("QID"))+"-"+String.valueOf(QCH.get("TypeID"))+"-"+QCH.get("ChoiceVal")+"'/>"+QCH.get("QCDescrip")+"<br>");
				}
				if(j == ((JSONObject)jo.get("Question "+Integer.toString(i))).names().length() - 2){
					response.getWriter().print("<br><br>");
				}
				j +=1;
			}
			i += 1;
		}
	
		response.getWriter().print("<input type='submit' value='submit' />");
		response.getWriter().print("</form>");
		}catch(Exception ex) {
			ex.printStackTrace();
		}
        
       
	}
	
	
	private Connection connectToDB() throws FileNotFoundException, SQLException, Exception {
		Connection connect;
		String[] loginCreds = DbConnection.getLoginCredentials();
		connect = DbConnection.initializeDBConnection(loginCreds[0], loginCreds[1], loginCreds[2]);
		return connect;
	}
	
}



