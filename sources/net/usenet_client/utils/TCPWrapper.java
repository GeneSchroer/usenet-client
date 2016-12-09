package cse310client;

import java.lang.String;
import java.net.Socket;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.PrintWriter;

/* What could possibly go wrong? */
import java.net.UnknownHostException;
import java.io.IOException;

/**
 * TCPWrapper is the utility for handling inet family sockets.
 * Since <code>java.net.Socket</code> does not provide a class with methods
 * for send and receiving data, there was a motivation behind the creating
 * of such a class that can ease the development process.
 *
 * @author Gene Schroer
 * @author Maryia Maskaliova
 * @author Rimack Zelnick
 * @version %I%
 * @since 0.7.1
 */
public class TCPWrapper extends Socket {
  protected BufferedReader reader;
  protected PrintWriter writer;

  /**
   * Establishes an open TCP connection to a designated hostname and port.
   *
   * @param hostname  the host name, or null for the loopback address.
   * @param port  the port number.
   * @throws UnknownHostException  if the IP address of the host could not
   * be determined.
   * @throws IOException  if an I/O error occurs when creating the
   * socket.
   * @since 0.7.1
   */
  public TCPWrapper( String hostname, short port ) throws
    UnknownHostException, IOException {
    super( hostname, port );

    InputStreamReader stream;

    stream = new InputStreamReader( getInputStream( ) );
    reader = new BufferedReader( stream );
    writer = new PrintWriter( getOutputStream( ) );
  }

  /**
   * Sends a buffer of data over an established TCP connection.
   * @param buffer  the buffer containing the data to be sent.
   * @param size  the size of the buffer.
   */
  public void send( char[] buffer, int size ) {
    writer.write( buffer, 0, size );
    writer.flush( );
  }

  /**
   * Receives a buffer of data over an established TCP connection.
   * @param buffer  the buffer where to store the incoming data.
   * @param size  the size of the buffer in order to prevent buffer
   * overflow.
   * @return the number of bytes received over the network.
   * @throws IOException If an I/O error occurs.
   */
  public int recv( char[] buffer, int size ) throws IOException {
    return reader.read( buffer, 0, size );
  }
}