/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.erika.java.networking;

import hr.erika.java.game.GameServerApplication;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ER
 */
public class ServerThread extends Thread {
    
    private final ObjectInputStream ois;
    private final ObjectOutputStream oos;
    private final Socket socket;
    private final GameServerApplication gameServerApplication;

    public ServerThread(GameServerApplication gameServerApplication, Socket socket, ObjectInputStream ois, ObjectOutputStream oos) {
        this.gameServerApplication = gameServerApplication;
        this.socket = socket;
        this.ois = ois;
        this.oos = oos;
    }

    @Override
    public void run() {
        while (true) {
            try {
                    gameServerApplication.sendToAll(ois.readObject(), this);
            } catch (IOException ex) {
                Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void sendObject(Object object) throws IOException {
        oos.writeObject(object);
    }
        
  
}
