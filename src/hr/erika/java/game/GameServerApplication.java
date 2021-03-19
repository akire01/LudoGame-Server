/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.erika.java.game;

import hr.erika.java.chat.ChatServer;
import hr.erika.java.networking.ServerThread;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author ER
 */
public class GameServerApplication {
 
    private static final String PROPERTIES_FILE = "config.properties";
    private static final String CLIENT_PORT = "CLIENT_PORT";
     private static final String NUMBER_OF_PLAYERS = "NUMBER_OF_PLAYERS";
    private static final Properties PROPERTIES = new Properties();
    
    private ServerSocket server;
    private String colors[] = new String[]{"YELLOW", "GREEN", "BLUE", "RED"};
    int currentIndex;
    
    private Vector<ServerThread> listeners = new Vector<>();
    
    private boolean gameStarted = false;
    
    //for Chat
    private ChatServer chatServer;
        
    static {
        try {
            PROPERTIES.load(new FileInputStream(PROPERTIES_FILE));
        } catch (IOException ex) {
            Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public GameServerApplication() {
        
        try{
        server = new ServerSocket(Integer.valueOf(PROPERTIES.getProperty(CLIENT_PORT)));
        System.out.println("Waiting for the client request on " + Integer.valueOf(PROPERTIES.getProperty(CLIENT_PORT)).toString());
        currentIndex = 0;

        chatServer = new ChatServer(this);

        while(true){

            if (listeners.size() < Integer.valueOf(PROPERTIES.getProperty(NUMBER_OF_PLAYERS))) {

                Socket socket = server.accept();
                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                ServerThread serverThread = new ServerThread(this, socket, ois, oos);
                serverThread.setDaemon(true);
                listeners.add(serverThread);
                serverThread.start();
                System.out.println("New client connected");
                serverThread.sendObject(colors[currentIndex]);
                currentIndex++;
                
            } else if(!gameStarted) {
                System.out.println("Game starting with " + listeners.size() + " players.");
                sendToAll(listeners.size(), null);
                gameStarted = true;
            }
        }
        
        }
        catch(Exception e){
            System.out.println(e);
        }
        
        System.out.println("END");
    }
    
    public void sendToAll(Object object, ServerThread sender) {
        listeners.forEach(list -> {
            try {
                if (!list.equals(sender)) {
                    list.sendObject(object);
                }          
            } catch (IOException ex) {
                Logger.getLogger(GameServerApplication.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }

     
    
    public static void main(String[] args) {
        new GameServerApplication();
    }
         
}


