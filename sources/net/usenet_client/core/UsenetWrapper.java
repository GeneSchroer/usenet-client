package net.usenet_client.core;
import net.usenet_client.utils.*;

import java.lang.String;
import java.net.SocketException;
/* What could go wrong? */
import java.net.UnknownHostException;
import java.io.IOException;


public class UsenetWrapper extends TCPWrapper {
	String version = "USENET/0.8.1";

    public static final int BUFFER_SIZE = 512;

    public UsenetWrapper(String hostname, short port) throws
            UnknownHostException, IOException {
        super(hostname, port);
    }

    public boolean login(String username) {
        String response = "";
        char[] buffer;
        int bufferSize;
        int code;

        /* */
        buffer = new String("LOGIN " + username + " " + "USENET/0.8.1").toCharArray();
        bufferSize = buffer.length;

        send(buffer, bufferSize);

        response = new String();
        buffer = new char[BUFFER_SIZE];

        try {
            /*while ((*/bufferSize = recv(buffer, BUFFER_SIZE);/*) > -1) {*/
                response += new String(buffer);
            //}
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        System.out.println(response);
        String[] loginResponse = response.split(" ");
        /* parse server response */
        code = Integer.parseInt(loginResponse[1]);

        if (code == 710) {
            return true;
        }

        return false;
    }

    public void logout() {
        char[] buffer = new String("LOGOUT "+ version).toCharArray();
        int bufferSize = buffer.length;
        send(buffer, bufferSize);
    }

    public void sendRequest(String s) {

        String response = "";
        char[] buffer;
        int bufferSize;
        int code;

        buffer = new String(s + " " + version).toCharArray();
        bufferSize = buffer.length;

        send(buffer, bufferSize);

        response = new String(buffer);

        
        /* parse server response */
        //code = Integer.parseInt(response.substring(13, 16));
        
    }
    
    public String receiveResponse() throws InvalidUserIDException{
    	String response = "";
    	char[] buffer = new char[BUFFER_SIZE];
    	int code;
    	try {
			recv(buffer, BUFFER_SIZE);
			response = new String(buffer);
			code = Integer.parseInt(response.split(" ")[1]);
			if (code == 830)
	            throw new InvalidUserIDException("User is already subscribed");
	        
	        if (code == 840)
	            throw new InvalidUserIDException("User is already unsubscribed");
    	} catch (IOException e) {
			System.out.print("Error: Disconnected from server.");
			return null;
		} 
    	
    	return response;
    }
    
    public void request(String s) throws InvalidUserIDException{
        char[] buffer;
        int bufferSize;
        int code;

        buffer = new String(s + " " + version).toCharArray();
        bufferSize = buffer.length;

        send(buffer, bufferSize);

        String response= new String(buffer);
            
        /* parse server response */
        code = Integer.parseInt(response.substring(13, 16));

        
        

    }

}
