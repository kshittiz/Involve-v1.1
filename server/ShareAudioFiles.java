import java.io.*;
import java.util.*;
import java.net.*;
import javax.sound.sampled.*;


class ShareAudioFiles
 {
   
    static int messageSize;
    static ByteArrayOutputStream out=new ByteArrayOutputStream();
 static void transferMessage(Socket clientSocket,Socket contactSocket)
  {
     String flag="";
     int count,n=0;
     try
      {
         DataInputStream dis=new DataInputStream(new BufferedInputStream(clientSocket.getInputStream()));
         DataOutputStream dos=new DataOutputStream(new BufferedOutputStream(contactSocket.getOutputStream()));    
         System.out.println("Transfering Message");
		 messageSize=dis.readInt();
		 System.out.println(messageSize);
         byte[]buf=new byte[messageSize];
         dis.read(buf,0,messageSize);
         dos.writeInt(messageSize);
         dos.flush();
         dos.write(buf,0,messageSize);
         dos.flush();
		  System.out.println("end Transfering Message");
      }
    catch(Exception e){System.out.println("bak"+e);}
  }         
}