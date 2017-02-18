package nparq.server;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Calendar;

public class TransferImages
{
    private final ServerSocket ss;
    public TransferImages() throws IOException
    {
        ss = new ServerSocket(3434);
    }
    
    public void upload(String file_name) throws IOException
    {
        Socket socket = ss.accept();
        InputStream in = new FileInputStream(file_name);
        OutputStream out = socket.getOutputStream();
        copy(in, out);
        out.close();
        in.close();
    }
    
    public void download() throws IOException
    {
        Calendar calendar = Calendar.getInstance();
        java.util.Date now = calendar.getTime();
        java.sql.Timestamp currentTimestamp = new java.sql.Timestamp(now.getTime());
        long tmm = (long)currentTimestamp.getTime();
        //
        Socket socket = ss.accept();
        InputStream in = socket.getInputStream();
        OutputStream out = new FileOutputStream(tmm + ".jpg");
        copy(in, out);
        out.close();
        in.close();
    }
 
    private void copy(InputStream in, OutputStream out) throws IOException
    {
        byte[] buf = new byte[8192];
        int len = 0;
        while ((len = in.read(buf)) != -1)
        {
            out.write(buf, 0, len);
        }
    }
}
