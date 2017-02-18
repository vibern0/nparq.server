package nparq.server;

import java.io.IOException;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NparqServer
{
    public static void main(String[] args)
    {
        ConnectionUDP connection = null;
        
        //
        
        MyDatabase mdata = new MyDatabase();
        mdata.search("Coimbra", null, null);
        
        //
        
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
