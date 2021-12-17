package javasurvey;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import SESurvey.SurveyExtIns;




@WebServlet("/survey")
public class survey extends HttpServlet{
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
			}
			
			if(!res.get(u)[0].equals(res.get(u+1)[0])){
				u+=1;
			}
		}
	    
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
		Connection c = SurveyExtIns.DBConnection();
	    JSONObject jo = SurveyExtIns.SurveyTest(c, surveyID, usrID);
	    
		long lines = 0;
	      try (BufferedReader reader = new BufferedReader(new FileReader(context.getRealPath(filename)))) {
	          while (reader.readLine() != null) lines++;
	      } catch (IOException e) {
	          e.printStackTrace();
	      }
	      
	      if(lines>2) {
	    	  File file2 = new File(context.getRealPath(filename));
	    	  try {
				SurveyExtIns.ResponseInsert(file2, c);
				response.getWriter().print("Thank you ");
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	      }
	      
		  if(request.getParameter("1") != null) {
	      response.getWriter().print(request.getParameter("1").split("-")[0]+" Was Submitted!");
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
				if(type.equals("Text")) {
					response.getWriter().print("<textarea rows='3' cols='30' name='"+QCH.get("QCID")+"'></textarea>" +"<br>");
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
        
       
	}
	
}


































		/*///////////////////////////////////////////////////////////////////////////////////
		BufferedReader br = new BufferedReader( new InputStreamReader(getServletContext().getResourceAsStream("/WEB-INF/tester.json")));
		try {
			Object obj2 = new JSONParser().parse(br);
			JSONObject jo2 = (JSONObject) obj2;
			response.getWriter().println(jo2.get("Title"));
			response.getWriter().println(usrID);
			br.close();
			
		} catch (IOException | ParseException e1) {
			// TODO Auto-generated catch block
			response.getWriter().println(e1.getMessage());
		}
		
		////////////////////////////////////////////////////////////////////////////////////
	    ////////////////////////////////////////////////////////////////////////////////////
		
		Object obj;
		try {
			obj = new JSONParser().parse(new InputStreamReader(getServletContext().getResourceAsStream("/WEB-INF/tester.json")));
			JSONObject jo = (JSONObject) obj;
			response.getWriter().println(jo.get("Title"));
		} catch (IOException | ParseException e) {
			// TODO Auto-generated catch block
			response.getWriter().println(e.getMessage());
		}
		
		
		*////////////////////////////////////////////////////////////////////////////////////
		//String surveyID = request.getParameter("surveyID");
		//String usrID = "Hello World";
		 //response.getWriter().println("Current user: " + usrID);
		//response.getWriter().println("Survey In Progress: "+surveyID);
		//getServletContext().getRequestDispatcher("/index.jsp").forward(request, response);		
