/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.erika.java.chat;

/**
 *
 * @author ER
 */
public class ChatLoggerConfiguration {
    
    public static final String filePath = System.getProperty("user.home") + "/Desktop/chatLog.txt";
    
    public static boolean isWriting;
    
    static {    
        isWriting = false;     
    }

}
