package net.usenet_client.core;
//package net.usenet_client.core;

//import net.usenet_client.utils.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Date;

import net.usenet_client.utils.User;
import net.usenet_client.utils.UserFileManager;

/*
 */
public class CommandLineInterface implements Runnable{

    static UsenetWrapper usenetWrapper;
    static BufferedReader br = new BufferedReader(new InputStreamReader(
            System.in));
    String userID, response;
    boolean loggedIn = false;
    User someone;

    public void go(String[] args) {

        short port = 0;

        if (args.length != 2) {
            System.out.println("Usage: Usenet <hostname> <port>");
            return;
        }

        try {
            port = (short) Integer.parseInt(args[1]);
            usenetWrapper = new UsenetWrapper(args[0], port);
        } catch (UnknownHostException ex) {
            System.out.println("Unknown host: " + args[0]);
            return;
        } catch (IOException ex) {
            ex.printStackTrace();
            return;
        } catch (NumberFormatException ex) {
            System.out.println("Invalid port number: " + args[1]);
            return;
        }

        try {
        } catch( Exception e ) {
          e.printStackTrace();
        }

        String cmd = "";

        while (true) {
            try {
                System.out.print("USENET>");

                cmd = br.readLine().toLowerCase();
                String[] cmds = cmd.split(" ");

                if (cmds[0].equals("login")) {
                	if(loggedIn){
                		System.out.println("You are already logged in.");
                	}
                	else if(!usenetWrapper.login(cmds[1])){
                		System.out.println("Invalid UserID");
                	}
                	else{
                		System.out.println("Logged in");
                		userID = cmds[1];
                		response = "";
                		loggedIn = true;
                		try {
                      Thread.sleep( 100 );
                      new Thread( this ).start();
                		} catch( Exception e ) {
                			e.printStackTrace();
                		}
                	}

                  someone = UserFileManager.readUser( userID );

                  if( someone == null ) {
                    someone = new User( userID );
                    UserFileManager.writeUser( someone );
                  }
                } else if (cmds[0].equals("logout")) {
                    usenetWrapper.logout();
                    System.out.println("Goodbye!");
                    br.close();
                    return;
                }else if (cmds[0].equals("help")) {
                    printHelp();
                } else if(!loggedIn){
                	System.out.println("You have not logged in.");
                	
            	} else if (cmds[0].equals("ag")) {
                    if(cmds.length == 2)
                        allGroups(cmds[1]);
                    else
                        allGroups(null);
                } else if (cmds[0].equals("sg")) {
                    if(cmds.length == 2)
                        subscribedGroups(cmds[1]);
                    else
                        subscribedGroups(null);
                } else if (cmds[0].equals("rg")) {
                    if(cmds.length != 2 && cmds.length != 3)
                        System.out.println("Invalid arguments for rg.");
                    else
                        readGroup(cmds);
                }  else {
                    System.out.println("Unknown command: " + cmds[0]);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void printHelp() {
        System.out.println("Usage: \n\tlogin USERNAME \n\thelp \n\tlogout");
        System.out.println("\n\tag [N]: lists the names of all existing discussion groups"
                + "\n\t\ts: subscribe to groups \n\t\tu: unsubscribe"
                + " \n\t\tn: lists the next N discussion groups"
                + "\n\t\tq: displays all groups and exits from the ag command");
        System.out.println("\n\tsg [N]: lists the names of subscribed groups"
                + "\n\t\tu: unsubscribe \n\t\tn: lists the next N discussion groups"
                + "\n\t\tq: displays all groups and exits from the sg command");
        System.out.println("\n\trg GNAME [N]: displays all posts in the group"
                + "\n\t\t[id]: the post within the list of N posts to display"
                + "\n\t\t\tn: displays at most N more lines"
                + "\n\t\t\tq: quit displaying the post content");
        System.out.println("\n\t\tr: marks a post as read"
                + "\n\t\tn: lists the next N posts"
                + "\n\t\tp: post to group \n\t\tq: exit from rg command");
    }

    private void allGroups(String n) throws IOException{

        int i, j, v, ngroups = 5;
        char option = '\0';
        String request = "";
        String cmd = "";
        String line;
        String[] groups, lines;

        try {
            if (n != null) 
                ngroups = Integer.parseInt(n);
        } catch (NumberFormatException ex) {
            System.out.println("Invalid number of groups.");
            return;
        }
        
        usenetWrapper.sendRequest("GROUP");
        response = "";
        
        try {
          Thread.sleep( 250 );
        } catch( InterruptedException ex ) {
          ex.printStackTrace( );
        }

        lines = response.split("\n\n");
        lines = lines[0].split("\n");
        groups = new String[lines.length - 5];
        
        for(i = 0; i< groups.length;i++){
        	groups[i] = lines[i+5];
        	
        }
        
        j = 0;
        for (i = 0; i < ngroups && j < groups.length; i++){
            System.out.println((j + 1) + ". "+ groups[j]);
            j++; /*keeps track of current array position*/
        }
       
        do {
            System.out.print("ag>");
            cmd = br.readLine(); 
            if (cmd != null && !cmd.equals("")){
            	option = cmd.toLowerCase().charAt(0);

            	switch (option) {
                	case 's':
                    	groupsAction(groups, cmd, "SUBSCRIBE");
                    	break;
                	case 'u':
                    	groupsAction(groups, cmd, "UNSUBSCRIBE");
                    	break;
                	case 'n':                   
                    	for (i = 0; i < ngroups && j < groups.length; i++){
                        	System.out.println((j + 1) + ". "+ groups[j]);
                        	j++; /*keeps track of current array position*/ 
                    	}
                    	break;
                	case 'q':
                    	return;
                    default:
                    	System.out.println(cmd + " is not a valid option");
                    	break;
            	}
            }
        } while (true);        
    }

    private void subscribedGroups(String n) throws IOException{
    	int i, j, v, ngroups = 5;
        char option = '\0';
        String request = "";
        String cmd = "";
        String line;
        String[] groups, lines, subscribedGroups;
        int s = 0;
        
        
        try {
            if (n != null) 
                ngroups = Integer.parseInt(n);
        } catch (NumberFormatException ex) {
            System.out.println("Invalid number of groups.");
            return;
        }

        usenetWrapper.sendRequest("GROUP");
        response = "";

        try {
          Thread.sleep( 250 );
        } catch (InterruptedException e) {
          e.printStackTrace();
        }

        lines = response.split("\n\n");
        lines = lines[0].split("\n");
        groups = new String[lines.length - 5];
        subscribedGroups = new String[s];
        
        for(i = 0; i< groups.length;i++){
        	groups[i] = lines[i+5];
        }
    
        String[] parse, temp;
        
        for(i = 0; i< groups.length; i++){
        	parse = groups[i].split(" ");
        	if(parse[1].equals("s,")){
        		temp = subscribedGroups;
        		subscribedGroups = new String[s+1];
        		for(j = 0; j<temp.length; ++j){
        			subscribedGroups[j] = temp[j];
        		}
        		subscribedGroups[s++] = groups[i];
        	}
        }
        
        
        j = 0;
        for (i = 0; i < ngroups && j < subscribedGroups.length; i++){
            System.out.println((j + 1) + ". "+ subscribedGroups[j]);
            j++; /*keeps track of current array position*/
        }
        do {
            System.out.print("sg>");
            cmd = br.readLine();    
            if (cmd != null && !cmd.equals("")){
            	option = cmd.toLowerCase().charAt(0);

            	switch (option) {
                	case 'u':
                    	groupsAction(groups, cmd, "UNSUBSCRIBE");
                    	break;
                	case 'n':                   
                    	for (i = 0; i < ngroups && j < subscribedGroups.length; i++){
                        	System.out.println((j + 1) + ". "+ subscribedGroups[j]);
                        	j++; /*keeps track of current array position*/                       
                    	}
                    	break;
                	case 'q':
                    	return;
                    default:
                    	System.out.println(cmd + " is not a valid option");
                    	break;
            	}
            }
        } while (true);    
        
    }

    
    
    private void readGroup(String[] arr) throws IOException {
        int n;
        int nposts = 5;
        String gname = arr[1];

        try {
            if (arr.length == 3) {
                nposts = Integer.parseInt(arr[2]);
            }
            else if(arr.length != 2){
            	System.out.println("Invalid number of arguments");
            	return;
            }
        } catch (NumberFormatException ex) {
            System.out.println("Invalid number of groups.");
            return;
        }


        String cmd = "";

        while (true) {
            String header, r, req;
            String[] payload = new String[5];
            int index = 0;

            req = "LIST " + gname + " USENET/0.8.1\n";
            response = "";
            usenetWrapper.send( req.toCharArray( ), req.length( ) );

            try {
              Thread.sleep( 250 );
            } catch( InterruptedException ex ) {
              ex.printStackTrace( );
            }

            r = response;

            if( r.split("\n\n").length > 0 ) {
              header = r.split( "\n\n" )[ 0 ];
              payload = r.split( "\n\n" );
              payload = payload[ 1 ].split( "\n" );
            } else {
              payload = null;
            }

            if( nposts > payload.length )
              nposts = payload.length;

            if( payload != null ) {
              for( int i = 0; i < payload.length; ++i ) {
                String postHash = "nohash";
                int temp = payload[ i ].indexOf( "," );

                if( temp != -1 )
                  postHash = payload[ i ].substring ( 2, temp );
                else
                  continue;

                if( someone.isPostRead( gname + "." + postHash ) ) {
                  char []temp_arr;
                  temp_arr = payload[ i ].toCharArray( );
                  temp_arr[ payload[ i ].indexOf( "N" ) ] = 'R';
                  payload[ i ] = new String( temp_arr );
                }

                System.out.println( ( i + 1 ) + ". " + payload[ i ] );
              }
            }

            System.out.print("rg>");

            cmd = br.readLine().toLowerCase();

            String []cmdargs = cmd.split(" ");
            int post=0;
            if (cmdargs[0].equals("r")) {
            	String postHash;
            	
            	if(cmdargs.length < 2){
            		System.out.println("Error: Too few arguments");
            	}
            	for(int i = 1; i < cmdargs.length; ++i){
            		try{
            			post = Integer.parseInt(cmdargs[i]);
            		}catch(NumberFormatException e){
            			System.out.println("Error: " + cmdargs[i] + " is not a number.");
            		}

            		postHash= payload[post-1].split(" ")[1].split(",")[0];
                someone.readPost( gname + "." + postHash );
                UserFileManager.writeUser( someone );
            	}
            	
            }  else if (cmd.equals("n")) {
              int i;

              for( i = index; i < payload.length; ++i )
                System.out.println( ( i + 1 ) + ". " + payload[ i ] );

              index += nposts;

              if( payload.length >= index )
                return;

            } else if (cmd.equals("p")) {
              String content, subject;
              int k;

              System.out.println( "Please select your subject: " );
              subject = br.readLine();

              Date t = new Date( );
              System.out.println( "Write your message ( TAB when you're done ):" );
              content = new String( ""
                  + "Group: " + gname + "\n"
                  + "Author: " + userID + "\n"
                  + String.format( "Date: %ta, %tb %td %tH:%tM:%tS %tZ %tY\n",
                    t, t, t, t, t, t, t, t )
                  );

              while( ( k = br.read( ) )!= '\t' )
                content += ( char )k; 

              req = "POST " + gname + " USENET/0.8.1\n"
                + "post-subject:" + subject + "\n"
                + "#-bytes:" + content.length( ) + "\n"
                + "line-count:" + content.split( "\n" ).length + "\n\n"
                + content;

              response = "";
              usenetWrapper.send( req.toCharArray( ), req.length( ) );

              try {
                Thread.sleep( 250 );
              } catch( InterruptedException ex ) {
                ex.printStackTrace( );
              }

              r = response;

              req = r.split( " " )[ 1 ];
              if( req.equals( "910" ) )
                System.out.println( "Message Sucessfully posted!" );
              else
                System.out.println( "Message could not be posted" );
            } else if (cmd.equals("q")) {
              return;
            } else {
                // implement two sub-sub-commands
                try {
                  String messageId, postHash;
                  String []msg;
                  int nLines, i;

                  n = Integer.parseInt(cmd);
                  messageId = gname + "."
                    + payload[ n - 1 ].substring( 2, 
                       payload[ n - 1 ].indexOf( ',' ) );

                  response = "";
                  req = "READ " + messageId + " USENET/0.8.1\n\n";
                  usenetWrapper.send( req.toCharArray( ), req.length( ) );

                  try {
                    Thread.sleep( 250 );
                  } catch( InterruptedException ex ) {
                    ex.printStackTrace( );
                  }

                  r = response;

                  msg = r.split( "\r\n\r\n" );
                  msg = msg[1].split( "\n" );
                  nLines = msg.length;
                  cmd = "n";
                  i = 0;

                  postHash = payload[n-1].split(" ")[1].split(",")[0];
                  someone.readPost( gname + "." + postHash );
                  UserFileManager.writeUser( someone );

                  while( cmd.equals( "n" ) && i < nLines) {
                    for(; i < i + nposts && i < nLines; ++i ) {
                      System.out.println( msg[ i ] );
                    }
                  }

                  System.out.print( "rg>" );
                  cmd = br.readLine( ).toLowerCase( );
                } catch (NumberFormatException ex) {
                    System.out.println("Invalid number.");
                    continue;
                }
                if (n < 1 || n > nposts) {
                    System.out.println("Invalid number of posts.");
                    continue;
                }
            }
        }
    }
    
    /**
     * 
     * @param groupList List of groups that is currently available.
     * @param cmd The command line arguments. Example: s 1 2 5
     * @param req Subscribe or Unsubscribe
     */
    private void groupsAction(String[] groupList, String cmd, String req) {

    	if (!req.equals("SUBSCRIBE") && !req.equals("UNSUBSCRIBE")){
    		System.out.println("Error: req parameter is invalid.");
    		return;
    	}
        String[] args = cmd.split(" ");
        String line;
        String[] parseLine;
        String group;
        int i, v;
        for (i = 1; i < args.length; i++) {
            try {
            	/* Parse the current argument and see if it's an integer */
                v = Integer.parseInt(args[i]);
                /* for right now, return if the entire command line is not valid */
                if (v < 1 || v > groupList.length) {
                    System.out.println("Invalid group index.");
                    return;
                }
                /* Select the chosen line in the groupList */
                line = groupList[v - 1];
                parseLine = line.split(" ");
              
                group = parseLine[3];
                
                usenetWrapper.sendRequest((req + " " + group));
                if(req.equals("SUBSCRIBE")){
                    System.out.println("Subscribed to " + group);	
                }
                else if(req.equals("UNSUBSCRIBE")){
                	System.out.println("Unsubscribed from " + group);
                }
            } catch (NumberFormatException ex) {
                System.out.println("Error: Invalid group index.");
                return;
            } catch (IndexOutOfBoundsException ex){
            	System.out.println("Error: Number not in the list.");
            	return;
            }
        }
    }

   // public String parseMsg(char[] raw){
    	
    	
   // }
    
	@Override
	public void run() {
		char[] raw_response = new char[512];
		
		try{
			while((usenetWrapper.recv(raw_response, 512) ) != -1){
				response += new String(raw_response);
			}
		}catch(IOException e){
			e.printStackTrace();
		}
	}
}
