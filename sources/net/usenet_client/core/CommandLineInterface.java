package net.usenet_client.core;
//package net.usenet_client.core;

//import net.usenet_client.utils.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Date;

/*
 */
public class CommandLineInterface implements Runnable{

    static UsenetWrapper usenetWrapper;
    static BufferedReader br = new BufferedReader(new InputStreamReader(
            System.in));
    String userID, response;
    boolean loggedIn = false;
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
            String header, payload, req;
            String[] lines;
            int index = 0;

            req = "LIST " + gname + " USENET/0.8.1\n";
            response = "";
            usenetWrapper.send( req.toCharArray( ), req.length( ) );

            try {
              Thread.sleep( 250 );
            } catch( InterruptedException ex ) {
              ex.printStackTrace( );
            }

            header = response.split( "\n" )[ 0 ];
           
            lines = Arrays.copyOfRange(response.split("\n"), 1, response.split("\n").length-1);
           // System.out.println("Payload = " + payload);
            //System.out.println("Lines = " + lines);
            for( int i = index; i < nposts; ++i )
              if( i < lines.length )
                System.out.println( ( i + 1 ) + ". " + lines[ i ] );

            System.out.print("rg>");

            cmd = br.readLine().toLowerCase();

            if (cmd.equals("r")) {
            } else if (cmd.equals("n")) {
              int i;
              for( i = index; i < lines.length; ++i )
                if( i < lines.length )
                  System.out.println( ( i + 1 ) + ". " + lines[ i ] );

              index += nposts;

              if( lines.length == i )
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

              req = response.split( " " )[ 1 ];
              if( req.equals( "910" ) )
                System.out.println( "Message Sucessfully posted!" );
              else
                System.out.println( "Message could not be posted" );
            } else if (cmd.equals("q")) {
                return;
            } else {
                // implement two sub-sub-commands
                try {
                  String messageId;
                  int k, j;

                  n = Integer.parseInt(cmd);
                  messageId = gname + "." + lines[ n - 1 ].substring( 2, 7 );

                  response = "";
                  req = "READ " + messageId + " USENET/0.8.1\n\n";
                  usenetWrapper.send( req.toCharArray( ), req.length( ) );

                  try {
                    Thread.sleep( 2000 );
                  } catch( InterruptedException ex ) {
                    ex.printStackTrace( );
                  }

                  System.out.println( response );
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
        String response;
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