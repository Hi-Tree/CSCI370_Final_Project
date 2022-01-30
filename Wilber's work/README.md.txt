In Wilber's Work Folder you will find the following folders:

registrationv2-  This java project was exported to create the jar file called "registerUser.jar". This java project allows
The survey system to register a user and send them an sms code to verify and also check to verify if the code
entered by the user is valid.


smsVerification- This java project was exported to create "smsSender.jar", this project is the one responsible for using
the twilio api to send text messaged to a user. This jar is used in both registrationv2 and surveyLogin.

Survey- This java project was exported to create "SurveyJson.jar", this project will create a Json object and store 
the entire survey as a json object. Once a complete survey json object is create this java project is the one responsible for 
storing the entire survey into the database.

surveyDB- This java project was exported to create "DBconnection.jar", this project will read the database credentials from 
the servletCredentials folder and it has a method that will return a connection to the mysql database.

surveyLogin- This java project was exported to create "UserLogin.jar", this project will handle sending the one time pass code 
to the user when trying to log in and also verifying their input

SurveySystem- the entire surveysystem with the registration servlets, the login servlets, and the survey creation servlets.

SurveyUser- This java project was exported to create "surveyUser", and is used to define a user object, to make accessing user information such as email, phone, name, userId, much easier. 


 If you would like to access the currently running servlet please go to and register and create a survey at the link below.
 
 http://52.23.169.99/SurveySystem/login.html
  
  
 However just be sure that the survey table is completely empty before making a survey.
 you may do this by logging into mysql with the following:
 
 mysql -u remoteuser -p -h 18.189.27.226
 
The database being used it called "surveysystem"