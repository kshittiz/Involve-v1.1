import java.io.*;
import java.util.*;
import java.net.*;

class ShareFiles
 {
     
   static void uploadFile(ArrayList<File>selectedFiles,KskClient client,DataInputStream dis,DataOutputStream dos)
    {
       int n = 0,numberOfFiles=selectedFiles.size();
       byte[]buf = new byte[4092];
      
       try {
        
        System.out.println(selectedFiles.size());

        //write file count
        dos.writeInt(numberOfFiles);
        dos.flush();
        
        //write size of each file
        for(int i=0;i<numberOfFiles;i++)
         {
            dos.writeLong(selectedFiles.get(i).length());
            dos.flush();
         }

        //write file names 
        for(int i = 0 ; i < numberOfFiles;i++)
        {
            dos.writeUTF(selectedFiles.get(i).getName());
            dos.flush();
        }

        
        System.out.println("Sending Files...");
        
        //outer loop, executes one for each file
        for(int i =0; i < numberOfFiles; i++)
         {

            System.out.println(selectedFiles.get(i).getName());
            
            //create new fileinputstream for each file
            FileInputStream fis = new FileInputStream(selectedFiles.get(i));

            //write file to dos
            while((n =fis.read(buf)) != -1)
            {
                dos.write(buf,0,n);
                dos.flush();
				//System.out.println(n);
            }
			
			//System.out.println(n);
         }
       System.out.println("Files Sent");
      // dos.close();
        
    } catch (IOException e) {System.out.println(e);}
  }

 static void recieveFiles(DataInputStream dis)
   {
        int n = 0;
        byte[]buf = new byte[4092];
     
        try {
        
        //read the file count from the client
        int numberOfFiles = dis.readInt();
        ArrayList<File>files = new ArrayList<File>(numberOfFiles);
        System.out.println("Number of Files to be received: " +numberOfFiles);
        
        long fileSizes[]=new long[numberOfFiles];

        for(int i=0;i<numberOfFiles;i++)
         {
            fileSizes[i]=dis.readLong();
         }

        //read file names, add files to arraylist
        for(int i = 0; i< numberOfFiles;i++)
        {
            File file = new File(dis.readUTF());
            files.add(file);
        }

        //outer loop, executes one for each file
        for(int i = 0; i < files.size();i++)
         {

            System.out.println("Receiving file: " + files.get(i).getName());
            
            //create a new fileoutputstream for each new file
			String recievedirec=System.getProperty("user.dir");
            recievedirec=recievedirec+"\\Files\\"+files.get(i).getName();
            File f=new File(recievedirec);
            FileOutputStream fos = new FileOutputStream(f);
            
            //read file
            while(fileSizes[i] > 0 && (n = dis.read(buf, 0, (int)Math.min(buf.length, fileSizes[i]))) != -1)
             {
                fos.write(buf,0,n);
                fileSizes[i]-=n;
                fos.flush();
				//System.out.println(n);
             }
			// System.out.println(n);
            System.out.println("file recieved");
			fos.close();
        } 
      }catch(Exception e){}
    }


}