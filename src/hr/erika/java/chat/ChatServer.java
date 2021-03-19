/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.erika.java.chat;

import hr.erika.java.game.GameServerApplication;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ER
 */
public class ChatServer {

    private static final String RMI_CLIENT = "client";
    private static final String RMI_SERVER = "server";
    private static final int REMOTE_PORT = 1099;
    
    private ServerChatInterface serverChatInterface;
    private List<ServerChatInterface> clients = new ArrayList<>();
    private Registry registry;
    
    private GameServerApplication game;
    private ExecutorService executorService;
    private Lock lock  = new ReentrantLock();

    public ChatServer(GameServerApplication game)  {
        this.game = game;
        executorService = Executors.newCachedThreadPool();
        publishServer();
        waitForClients();
    }
    
    private void publishServer() {
       serverChatInterface = new ServerChatInterface() {
            @Override
            public void send(String message) throws RemoteException {
              
                ChatFileLogger cfl = new ChatFileLogger(lock, message);
                executorService.execute(cfl);
                
                for (ServerChatInterface client : clients) {
                    client.send(message);
                }
            }
        };
        try {

            registry = LocateRegistry.createRegistry(REMOTE_PORT);
            ServerChatInterface stub = (ServerChatInterface) UnicastRemoteObject.exportObject(serverChatInterface, RANDOM_PORT_HINT);
            registry.rebind(RMI_SERVER, stub);

        } catch (RemoteException ex) {
            Logger.getLogger(ChatServer.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    
    private static final int RANDOM_PORT_HINT = 0;
    
    
    private void waitForClients() {
      Thread thread = new Thread(() -> {
            while (true) {
                try {
                     ServerChatInterface client = (ServerChatInterface) registry.lookup(RMI_CLIENT);
                     if(!clients.contains(client)) {
                        clients.add(client);
                     }
                } catch (RemoteException | NotBoundException ex) {
                    System.out.println("waiting for client");
                }
                //System.out.println(clients);

                try {
                    Thread.sleep(5000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(ChatServer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        });
        thread.setDaemon(true);
        thread.start();
    }
}
