package net.usenet_client.core;
//package net.usenet_client.core;

//import net.usenet_client.utils.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.UnknownHostException;

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
                		/* sleep(1) */
                		//this.run();
                		} catch( Exception e ) {
                			e.printStackTrace();
                		}
                	}
                } else if (cmds[0].equals("help")) {
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
                } else if (cmds[0].equals("logout")) {
                    usenetWrapper.logout();
                    System.out.println("Goodbye!");
                    br.close();
                    return;
                } else {
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
        String response = "";
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
        response = usenetWrapper.receiveResponse();
        lines = response.split("\n");
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
                    	groupsAction(groups, cmd, "SUBSCRIBE ");
                    	break;
                	case 'u':
                    	groupsAction(groups, cmd, "UNSUBSCRIBE ");
                    	break;
                	case 'n':                   
                    	for (i = 0; i < ngroups && j < groups.length; i++){
                        	System.out.println((j + 1) + ". "+ groups[j]);
                        	j++; /*keeps track of current array position*/                       
                    	}
                    	break;
                	case 'q':
                    	//for (i = 0; i < groups.length; i++) /*print all groups*/
                        	//System.out.println((i + 1) + ". "+ groups[i]);                 
                    	return;
                    default:
                    	System.out.println(cmd + " is not a valid option");
                    	break;
            	}
            }
        } while (true);        
    }

    private void subscribedGroups(String n) throws IOException{
        int ngroups = 5;
        char option = '\0';

        try {
            if (n != null) 
                ngroups = Integer.parseInt(n);
        } catch (NumberFormatException ex) {
            System.out.println("Invalid number of groups.");
            return;
        }

        String cmd = "";

        do {
            System.out.print("sg>");
            cmd = br.readLine();
            if (cmd != null){
            option = cmd.toLowerCase().charAt(0);
        
            	switch (option) {
                	case 'u':
                		break;
                	case 'n':
                		break;
                	case 'q':
                		System.out.println("");// print all groups
                		return;
                	default:
                   	   	System.out.println("Not a valid option");
                   	   	break;
            	}
            }
        } while (option != 'q'); 
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

            System.out.print("rg>");

            cmd = br.readLine().toLowerCase();
           
            if (cmd.equals("r")) {

            } else if (cmd.equals("n")) {

            } else if (cmd.equals("p")) {

            } else if (cmd.equals("q")) {
                return;
            } else {
                // implement two sub-sub-commands
                try {
                    n = Integer.parseInt(cmd);
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
    
    private void groupsAction(String[] lines, String groups, String req) {

        String[] args = groups.split(" ");
        String line;
        int i, v;
        
        for (i = 1; i < args.length; i++) {
            try {
                v = Integer.parseInt(args[i]);
                if (v < 1 || v > lines.length) {
                    System.out.println("Invalid group index.");
                    break;
                }
                line = lines[v - 1];
                line = line.substring(line.indexOf(')') + 2);
                usenetWrapper.request((req + line));
            } catch (NumberFormatException ex) {
                System.out.println("Invalid group index.");
                break;
            } catch (InvalidUserIDException ex) {
                System.out.println(ex.getMessage());
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
