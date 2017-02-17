/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nparq.server;

import java.io.IOException;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author bernardovieira
 */
public class NparqServer {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        ConnectionUDP connection = null;
        try
        {
            connection = new ConnectionUDP(5600);
        }
        catch (SocketException ex)
        {
            Logger.getLogger(NparqServer.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(1);
        }
        catch (IOException ex)
        {
            Logger.getLogger(NparqServer.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Client disconnected!");
            System.exit(1);
        }
        
        connection.close();
    }
    
}
