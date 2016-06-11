import java.io.*;
import java.util.*;
import java.net.*;

class ShareFiles
 { static void transferFiles(Socket clientSocket,Socket contactSocket)
  {
     String flag="";
     int numberOfFiles,n=0;
     byte[]buf = new byte[4092];
     try
      {
         DataInputStream dis=new DataInputStream(new BufferedInputStream(clientSocket.getInputStream()));
         DataOutputStream dos=new DataOutputStream(new BufferedOutputStream(contactSocket.getOutputStream()));
        
         dos.writeUTF("file");
         dos.flush();
         numberOfFiles=dis.readInt();
		 long fileSizes[]=new long[numberOfFiles];
         dos.writeInt(numberOfFiles); 
         dos.flush();
         //transfer data from input stream of client to output stream of contact
         for(int i=0;i<numberOfFiles;i++)
          {
			 long x=dis.readLong();
			 fileSizes[i]=x;
            dos.writeLong(x);
            dos.flush();
          }

         for(int i=0;i<numberOfFiles;i++)
         {
           dos.writeUTF(dis.readUTF());
           dos.flush();
         }

         System.out.println("Transfering Files");
		 for(int i=0;i<numberOfFiles;i++)
         {
          // while((n =dis.read(buf)) != -1)
			while(fileSizes[i] > 0 && (n = dis.read(buf, 0, (int)Math.min(buf.length, fileSizes[i]))) != -1) 
            {
             dos.write(buf,0,n);
             dos.flush();
		     fileSizes[i]-=n;
		    //System.out.println(n);
            }
		 }
		System.out.println("end");
		//dos.close();
      }
    catch(Exception e){System.out.println(e);}
  }     
}    