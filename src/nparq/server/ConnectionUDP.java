package nparq.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bson.Document;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class ConnectionUDP
{    
    public static final int MAX_DPACK_SIZE = 1024;
    private final DatagramSocket socket;
    private MyDatabase database;
    TransferImages transfer;
    
    public ConnectionUDP(int listeningPort) throws SocketException, IOException
    {
        transfer = new TransferImages();
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
                System.out.println(json.get("type"));
                if(json.get("type").equals("search"))
                {
                    System.out.println("SEARCH" + input_sock);
                    ArrayList<String> wants = (ArrayList<String>) json.get("wants");
                    ArrayList<String> nwants = (ArrayList<String>) json.get("nwants");
                    JSONArray wres =
                            database.search((String)json.get("city_name"),
                                    wants, nwants);
                    //

                    packet_send = new DatagramPacket(
                            wres.toJSONString().getBytes(), wres.toJSONString().length(),
                            InetAddress.getByName(host_adress), host_port);
                    socket.send(packet_send);
                    System.out.println(wres.toJSONString());
                    System.out.println("search-done");
                }
                else if(json.get("type").equals("see"))
                {
                    final String h_adress = host_adress;
                    final int h_port = host_port;
                    new Thread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            try
                            {
                                DatagramPacket packet_send;
                                Document doc = database.find((long)json.get("ref"));
                                packet_send = new DatagramPacket(
                                        doc.toJson().getBytes(), doc.toJson().length(),
                                        InetAddress.getByName(h_adress), h_port);
                                System.out.println("abc" + doc.toJson());
                                socket.send(packet_send);
                                System.out.println("a");
                                
                                JSONParser iparser = new JSONParser();
                                JSONObject ijson = (JSONObject) iparser.parse(doc.toJson());
                                
                                transfer.upload((String)ijson.get("photo"));
                                System.out.println("b " + (String)ijson.get("photo"));
                            }
                            catch (IOException ex)
                            {
                                Logger.getLogger(ConnectionUDP.class.getName()).
                                        log(Level.SEVERE, null, ex);
                            } catch (ParseException ex) {
                                Logger.getLogger(ConnectionUDP.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    }).start();
                }
                else if(json.get("type").equals("new"))
                {
                    String name = (String) json.get("name");
                    ArrayList<String> arr = (ArrayList<String>) json.get("contains");
                    if(!database.find(name))
                    {
                        long tmm = transfer.download();
                        
                        JSONObject njson = new JSONObject();
                        njson.put("city", json.get("city"));
                        njson.put("name", json.get("name"));
                        njson.put("photo", tmm + ".jpg");
                        njson.put("lat", json.get("lat"));
                        njson.put("long", json.get("long"));
                        njson.put("contains", arr);
                        database.add(njson);
                        System.out.println("Novo local!" + name);
                    }
                    //
                }
                /*else if(json.get("type").equals("pnew"))
                {
                    transfer.download();
                }*/
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
