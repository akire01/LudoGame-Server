/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.erika.java.chat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.locks.Lock;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ER
 */
public class ChatFileLogger implements Runnable {

    String message;
    Lock lock;

    public ChatFileLogger(Lock l, String m) {
        message = m;
        lock = l;
    }

    @Override
    public void run() {
        writeInFile();
    }

    private void writeInFile() {

        synchronized (lock) {
            while (ChatLoggerConfiguration.isWriting) {
                
                System.out.println(
                        ZonedDateTime.now().toLocalTime().truncatedTo(ChronoUnit.SECONDS)
                        +" Waiting to write message: "
                        + message
                       );
                
                try {
                    lock.wait();
                } catch (InterruptedException ex) {
                    Logger.getLogger(ChatFileLogger.class.getName()).
                            log(Level.SEVERE, null, ex);
                }
            }

            ChatLoggerConfiguration.isWriting = true;
            
              try {
                // Odgoda za testiranje
                lock.wait(2000);
            } catch (InterruptedException ex) {
                Logger.getLogger(ChatFileLogger.class.getName()).
                        log(Level.SEVERE, null, ex);
            }
            

            try {          
                message = message + "\n";
                Files.write(Paths.get(ChatLoggerConfiguration.filePath),
                     message.getBytes(), StandardOpenOption.APPEND);
            } catch (IOException ex) {
                Logger.getLogger(ChatFileLogger.class.getName()).
                        log(Level.SEVERE, null, ex);
            }

            System.out.println(
                    ZonedDateTime.now().toLocalTime().truncatedTo(ChronoUnit.SECONDS)
                    + " Finished writing message: "  
                    + message);
            ChatLoggerConfiguration.isWriting = false;
            
            lock.notifyAll();
        }

    }

}
