package net.usenet_client.utils;


import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;

public class UserFileManager {
    
    /*don't let anyone instantiate this class*/
    private UserFileManager(){
    }
    
    public static User readUser(String userID) {
        User user = null;
        try {
            FileInputStream fis = new FileInputStream("var/" + userID + ".txt");
            ObjectInputStream ois = new ObjectInputStream(fis);
            user = (User)ois.readObject();
            ois.close();
        } catch (IOException | ClassNotFoundException e) {          
            return null;    /*file for that user was not found*/
        }
        return user;
    }
    
    public static void writeUser(User user) {
        try {
            FileOutputStream fos = new FileOutputStream("var/" + user.getID() + ".txt");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(user);
            oos.flush();
            oos.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
