package nparq.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class ConnectionUDP
{    
    public static final int MAX_DPACK_SIZE = 256;
    private final DatagramSocket socket;
    private MyDatabase database;
    
    public ConnectionUDP(int listeningPort) throws SocketException, IOException
    {
        database = new MyDatabase();
        socket = new DatagramSocket(listeningPort);
        DatagramPacket packet = new DatagramPacket(new byte[MAX_DPACK_SIZE], MAX_DPACK_SIZE);
        DatagramPacket packet_send;
        String input_sock;
        String host_adress;
        int host_port;
        
        do
        {
            socket.receive(packet);
            
            input_sock = new String(packet.getData(), 0, packet.getLength());
            host_adress = packet.getAddress().getHostAddress();
            host_port = packet.getPort();
            
            JSONParser parser = new JSONParser();
            try
            {
                JSONObject json = (JSONObject) parser.parse(input_sock);
                if(json.get("type").equals("search"))
                {
                    ArrayList<String> wants = (ArrayList<String>) json.get("wants");
                    ArrayList<JSONObject> wres =
                            database.search((String)json.get("city_name"), wants);
                    //

                    packet_send = new DatagramPacket(
                            wres.toString().getBytes(), wres.toString().length(),
                            InetAddress.getByName(host_adress), host_port);
                    socket.send(packet_send);
                }
                else if(json.get("type").equals("new"))
                {
                    String name = (String) json.get("name");
                    ArrayList arr = (ArrayList) json.get("contains");
                    if(!database.find(name))
                    {
                        JSONObject njson = new JSONObject();
                        njson.put("city", json.get("city"));
                        njson.put("name", json.get("name"));
                        njson.put("contains", json.get("contains"));
                    }
                    //
                }
                else if(json.get("type").equals("vote"))
                {
                    long ref = (long) json.get("ref");
                    database.vote(ref, (boolean)json.get("vote"));
                }
            }
            catch (ParseException ex)
            {
                Logger.getLogger(ConnectionUDP.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        } while(true);
    }
    
    public void close()
    {
        socket.close();
    }
    
}
