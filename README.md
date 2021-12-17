# CSCI370_Final_Project



Folder: Documentation

In the documentations folder you will find all the information about limitations, specification and burndown charts of each sprint.

Folder: Documentation/mySQLINFO

In the mySQLINFO folder you will find schema design, constraint information and the mySQL repository 

Folder: Servlet
In the Servlet folder you will find the Java Servlet Dynamic Web Project which contains the currently running script on 
http://52.15.52.238:8080/SurveyRetrieve/survey?usrID=151&surveyID=987

Folder: IndividualWork
Contains the the project disjointed. All individual work done by Cindy, Meri and Hugo.

Folder IndividualWork/HugosWork.zip
  ConnectToDB class: This is the class that is in charge of inserting and generating entries of UserSurveyID. As well as connecting to the SQL Repository. Unfortunately we weren't able to fully implemented in the current version of the project. We had issues in the final phase of testing so we had to leave it out for now.
 
  sql_repo.xml: Location of all SQL queries needed for our project.

SurveyExtIns.jar
  This jar is the jar used in Meri's servlet to extract a survey and insert a survey response. Does not use the Repository for queries
  
SurveyExtIns2.jar
  This jar is the jar NOT used in Meri's servlet. Suppose to use the repository xml to extract or insert information but when exported to jar, class does not seem to access the xml it is extracted with.
  
Folder IndividualWork/CindyJuanSurveyProject.zip
eclipse project that contains project code that also uses Hugo's query repository to execute certain queries.
  DBConnection() - requires no parameters to return a Connection that connects to Meri's Database.
  
  ExpDateCheck - method takes in a SurveyID and UserID String. Returns a Boolean value true for when Expiration date hasn't past the current date and false for id no survey user id was found or if the expiration date is past the current date
  
  SurveyTest - method takes in a connection, SurveyID and UserID string. Returns a JSONObject containing the survey extracted from the database based on the Survey ID and User ID given
  
  ResponseInsert - takes in a txt file with the contents formatted like SurveyID#UserID#QID#TypeID#Response to insert the required information into the database. Does not return anything and does not insert information if there already exists a response entry based on the given userID and QID from the txt file.


