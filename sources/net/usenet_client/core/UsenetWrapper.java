package cse310client;
//package net.usenet_client.core;

import java.lang.String;
//import net.usenet_client.utils.TCPWrapper;

/* What could go wrong? */
import java.net.UnknownHostException;
import java.io.IOException;

public class UsenetWrapper extends TCPWrapper {

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

        buffer = new String("LOGIN " + username + " USENET/0.8.1").toCharArray();
        bufferSize = buffer.length;

        send(buffer, bufferSize);

        response = new String();
        buffer = new char[BUFFER_SIZE];

        try {
            while ((bufferSize = recv(buffer, BUFFER_SIZE)) != -1) {
                response += new String(buffer);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        String[] loginResponse = response.split(" ");
        /* parse server response */
        code = Integer.parseInt(loginResponse[1]);

        if (code == 710) {
            return true;
        }

        return false;
    }

    public void logout() {
        char[] buffer = new String("LOGOUT USENET/0.8.1").toCharArray();
        int bufferSize = buffer.length;
        send(buffer, bufferSize);
    }

    public void sendRequest(String s) {

        String response = "";
        char[] buffer;
        int bufferSize;
        int code;

        buffer = new String(s + " USENET/0.8.1").toCharArray();
        bufferSize = buffer.length;

        send(buffer, bufferSize);

        response = new String(buffer);

        
        /* parse server response */
        //code = Integer.parseInt(response.substring(13, 16));
        
    }
    
    public void request(String s) throws InvalidUserIDException{
        char[] buffer;
        int bufferSize;
        int code;

        buffer = new String(s + " USENET/0.8.1").toCharArray();
        bufferSize = buffer.length;

        send(buffer, bufferSize);

        String response= new String(buffer);
            
        /* parse server response */
        code = Integer.parseInt(response.substring(13, 16));

        if (code == 830)
            throw new InvalidUserIDException("User is already subscribed");
        
        if (code == 840)
            throw new InvalidUserIDException("User is already unsubscribed");
        

    }
}
