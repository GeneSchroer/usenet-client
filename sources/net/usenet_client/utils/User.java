package net.usenet_client.utils;


import java.io.Serializable;
import java.util.ArrayList;

public class User implements Serializable{
    
    private static final long serialVersionUID = 5L;
    private String userID;
    private ArrayList<String> subscribedGroups = new ArrayList<String>();
    private ArrayList<String> postsRead = new ArrayList<String>();

    public User(){
    }
    
    public User(String userID){
        this.userID=userID;
    }
    
    public String getID(){
        return userID;
    }
    
    public void readPost(String postID){
        postsRead.add(postID);
    }
    
    public void subscribeGroup(String gname){
        subscribedGroups.add(gname);
    }
    
    public void unsubscribeGroup(String gname) {
        for (String v : subscribedGroups) {
            if (v.equals(gname)) {
                subscribedGroups.remove(v);
                return;
            }
        }
    }
}