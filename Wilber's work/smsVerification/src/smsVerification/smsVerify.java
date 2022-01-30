package smsVerification;

import java.util.*;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import surveyDB.DbConnection;
import java.sql.Connection;


public class smsVerify {
	
	// Find your Account SID and Auth Token at twilio.com/console
    // and set the environment variables. See http://twil.io/secure

    //public static final String ACCOUNT_SID = System.getenv(acctSID);
    //public static final String AUTH_TOKEN = System.getenv(authToken);

	//for now until i set up environment variables
	private static String acctSID = "AC60e3d9db60d0c90b56a2256a1d19a4de";
	private static String authToken = "cedf81564e449c7b27c4373e6f2f327c";
	
		/*
		* sendVerification - This method will send the registration code to 
		* a user, whose phone number is passed in as a parameter. Since the project states 
		* we can only support US phone numbers, The +1 will be hardcoded.
		*/
		static public void sendVerification(String userNumber,String code) {
			Twilio.init(acctSID, authToken);
	        Message message = Message.creator(
	                new com.twilio.type.PhoneNumber("+1" + userNumber),
	                new com.twilio.type.PhoneNumber("+12057970921"),
	                "Survey System registration code is: " + code)
	            .create();

	        System.out.println(message.getSid());
		}
		
}
