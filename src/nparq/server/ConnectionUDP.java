/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nparq.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import org.json.simple.JSONObject;

/**
 *
 * @author bernardovieira
 */
public class ConnectionUDP
{    
    public static final int MAX_DPACK_SIZE = 256;
    private final DatagramSocket socket;
    
    public ConnectionUDP(int listeningPort) throws SocketException, IOException
    {
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
            
            if(input_sock.equals("search"))
            {
                JSONObject obj = new JSONObject();
                obj.put("name", "foo");
                obj.put("location", "bar");
                //
                packet_send = new DatagramPacket(
                        obj.toString().getBytes(), obj.toString().length(),
                        InetAddress.getByName(host_adress), host_port);
                socket.send(packet_send);
            }
            
        } while(true);
    }
    
    public void close()
    {
        socket.close();
    }
    
}
