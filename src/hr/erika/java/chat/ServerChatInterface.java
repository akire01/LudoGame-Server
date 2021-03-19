/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.erika.java.chat;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *
 * @author ER
 */
public interface ServerChatInterface extends Remote {
    
    void send(String message) throws RemoteException;
}
