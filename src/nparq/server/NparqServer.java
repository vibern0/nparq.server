/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nparq.server;

import java.io.IOException;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONObject;

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
        MyDatabase database = new MyDatabase();
        
        /*JSONObject json = new JSONObject();
        json.put("city", "Coimbra");
        json.put("name", "Choupal");
        ArrayList<String> jcontains = new ArrayList<>();
        jcontains.add("wc");
        jcontains.add("parquelanche");
        jcontains.add("calmo");
        jcontains.add("rio");
        json.put("contains", jcontains);
        
        database.add(json);*/
        
        ArrayList<String> arr = new ArrayList<>();
        arr.add("wc");
        ArrayList<JSONObject> marr = database.search("Coimbra", null);
        
        
        /*java.util.Date today = new java.util.Date();
        java.sql.Timestamp timee = new java.sql.Timestamp(today.getTime());
        System.out.println(timee.getTime());
        
        System.out.println(((java.util.Date)marr.get(0).get("ref")).getTime());*/
        
        System.out.println((long)marr.get(0).get("ref"));
        
        /*database.vote((long)marr.get(0).get("ref"), true);
        database.vote((long)marr.get(0).get("ref"), true);
        database.vote((long)marr.get(0).get("ref"), false);*/
        
        for(JSONObject e : marr)
        {
            System.out.println(e.toJSONString());
        }
        
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
