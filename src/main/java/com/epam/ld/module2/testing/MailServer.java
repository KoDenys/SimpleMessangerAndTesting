package com.epam.ld.module2.testing;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Mail server class.
 */
public class MailServer {

    /**
     * Send notification.
     *
     * @param client  the client
     * @param messageContent the message content
     */
    public void send(Client client, String messageContent) {
        if(client.getOutputFileName()==null){
            sendToConsole(messageContent);
        }
        else{
            sendToFile(client, messageContent);
        }
    }

    public void sendToConsole(String messageContent){
        System.out.println(messageContent);
    }

    public void sendToFile(Client client, String messageContent){
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(client.getOutputFileName()))){
            writer.write(messageContent);
        }
        catch (IOException ex){
            ex.printStackTrace();
        }
    }
}
