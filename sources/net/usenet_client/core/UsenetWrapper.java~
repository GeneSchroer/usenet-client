package net.usenet_client.core;

import java.lang.String;
import net.usenet_client.utils.TCPWrapper;

/* What could go wrong? */
import java.net.UnknownHostException;
import java.io.IOException;

public class UsenetWrapper extends TCPWrapper {
  public static final int BUFFER_SIZE = 512;

  public UsenetWrapper( String hostname, short port ) throws
    UnknownHostException, IOException {
    super( hostname, port );
  }

  public boolean login( String username ) {
    String response;
    char[] buffer;
    int bufferSize;

    buffer      = new String( "LOGIN " + username + " USENET/0.8.1" ).toCharArray( );
    bufferSize  = buffer.length;

    send( buffer, bufferSize );

    response = new String( );
    buffer   = new char[ BUFFER_SIZE ];

    try {
      while( ( bufferSize = recv( buffer, BUFFER_SIZE ) ) != -1 )
        response += new String( buffer );
    } catch( IOException ex ) {
      ex.printStackTrace( );
    }

    /* parse server response */

    /* surpress error for not having a return statement */
    return true;
  }
}
