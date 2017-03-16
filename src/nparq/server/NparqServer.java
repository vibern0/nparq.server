package nparq.server;

import java.io.IOException;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bson.Document;
import org.json.simple.JSONObject;

public class NparqServer
{
    public static void main(String[] args)
    {
        ConnectionUDP connection = null;
        
        //
        
        MyDatabase mdata = new MyDatabase();
        /*JSONObject obj = new JSONObject();
        obj.put("city", "Lisboa");
        obj.put("name", "Jardim Quinta da paz");
        obj.put("lat", 38.770446);
        obj.put("long", -9.175640);
        obj.put("photo", "no_photo");
        ArrayList<String> arr = new ArrayList<>();
        arr.add("lago");
        arr.add("calmo");
        obj.put("contains", arr);
        mdata.add(obj);
        
        
        //MyDatabase mdata = new MyDatabase();
        obj = new JSONObject();
        obj.put("city", "Coimbra");
        obj.put("name", "Choupal");
        obj.put("lat", 40.222254);
        obj.put("long", -8.443894);
        obj.put("photo", "no_photo");
        arr = new ArrayList<>();
        arr.add("wc");
        obj.put("contains", arr);
        mdata.add(obj);*/
        
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
