import java.io.*;
import java.util.*;
import java.net.*;
import javax.sound.sampled.*;
import org.xiph.speex.*;
class ShareAudioFiles
 {
    static int messageSize;
    static ByteArrayOutputStream out;

  
    static void captureAudio(final DataOutputStream dos) 
      {
        
        try 
         {  
		    messageSize=0;
			out=new ByteArrayOutputStream();
            final AudioFormat format = getFormat();
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
            final TargetDataLine line = (TargetDataLine)AudioSystem.getLine(info);
            line.open(format);
            line.start();
            Runnable runner = new Runnable() 
              {
                 /*int bufferSize = (int)format.getSampleRate() * format.getFrameSize();
                 
                 byte buffer[] = new byte[bufferSize];*/
                 
                 public void run() 
                  {
                    System.out.println("Capture thread started");
                    try 
                     { 
                       SpeexEncoder encoder = new SpeexEncoder();
                       encoder.init(1,1,8000,1);
                       int bufferSize = encoder.getFrameSize()*2; 
                       byte buffer[] = new byte[bufferSize*2];

                       while (KskClient.running)
                        {
                          int count = line.read(buffer, 0, bufferSize);
                          encoder.processData(buffer, 0, bufferSize);
                          int encoded = encoder.getProcessedData(buffer, 0);
                          byte[]encoded_data=new byte[encoded]; 
                          System.arraycopy(buffer, 0, encoded_data, 0, encoded);
                          if (count > 0)
                           {
                             out.write(encoded_data, 0, encoded); 
                             messageSize+=encoded;System.out.println(messageSize);
                             out.flush();
                           }
                        }
                       out.close();
                       sendMessage(out,dos);
					   line.drain();
					   line.close();
                     } 
                    catch (IOException e) 
                     {      
                       System.err.println("I/O problems: " + e);
                       System.exit(-1);
                     }
                   }
               };
   
            Thread captureThread = new Thread(runner);
            captureThread.start();
            
          } 
          catch (Exception e) 
           {
             System.err.println("capture:Line unavailable: " + e);
             System.exit(-2);
           }
          
      }

   static void sendMessage(ByteArrayOutputStream out,DataOutputStream dos)
      {
        byte buf[];
        
        try
         {
           buf=out.toByteArray();
           System.out.println(buf.length);
           dos.writeInt(messageSize);
           dos.flush();
           dos.write(buf,0,messageSize);
           dos.flush();
           System.out.println(messageSize);
           System.out.println("Message sent...");
         }
        catch(Exception e){}
      }
   

   static void recieveMessage(DataInputStream dis,String audio_name)
      {    
        try 
         {
            int encoded=15,n=0;final int bufferSize=640;  
            messageSize=dis.readInt();
         
            byte[]encodedAudio = new byte[messageSize];
            dis.read(encodedAudio,0,messageSize);

            byte[]b=new byte[bufferSize];
            System.out.println(encodedAudio.length);
            ByteArrayInputStream bis=new ByteArrayInputStream(encodedAudio);
            ByteArrayOutputStream bout=new ByteArrayOutputStream();
            SpeexDecoder decoder = new SpeexDecoder();
            decoder.init(1,8000,1,false);
            
           while((n=bis.read(b,0,encoded))!=-1)
            {
              decoder.processData(b, 0, encoded);
              byte[] buf = new byte[decoder.getProcessedDataByteSize()];
              int decoded = decoder.getProcessedData(buf, 0);
              bout.write(buf,0,decoded);
              bout.flush();
            }

           byte[]audio=bout.toByteArray();
            playAudio(audio);
            System.out.println(audio.length);
           // path of the wav file
           String recievedirec=System.getProperty("user.dir");
           recievedirec=recievedirec+"\\Audio\\"+audio_name+".wav";
           final File wavFile = new File(recievedirec);
 
           // format of audio file
           final AudioFileFormat.Type fileType = AudioFileFormat.Type.WAVE;
        
           InputStream input = new ByteArrayInputStream(audio);
           final AudioFormat format = getFormat();
           final AudioInputStream ais = new AudioInputStream(input, format,audio.length / format.getFrameSize());    
           System.out.println(audio.length / format.getFrameSize());

           DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
           final SourceDataLine line = (SourceDataLine)AudioSystem.getLine(info);
           line.open(format);
           line.start();
           AudioSystem.write(ais, fileType, wavFile);
           
           

           Runnable runner = new Runnable() 
             {
                //int bufferSize = (int) format.getSampleRate() * format.getFrameSize();
                byte buffer[] = new byte[bufferSize*2];
 
                public void run() 
                 {
                    try 
                     {
                       int count,i=0;
                       while ((count = ais.read(buffer, 0, bufferSize)) != -1) 
                        {
                           if (count > 0) 
                            { System.out.println(count+" "+i++);
                              line.write(buffer, 0, count);
                            }
                         }
                       line.drain();
                       line.close();
                     } 
                    catch (IOException e) 
                     {
                       System.err.println("I/O problems: " + e);
                       System.exit(-3);
                     }
                  }
              };
           Thread playThread = new Thread(runner);
           playThread.start();
         } 
       catch (Exception e) 
        {
          System.err.println("reciever:Line unavailable: " + e);
          System.exit(-4);
        } 
    }    
	static AudioFormat getFormat() 
    {
      float sampleRate = 8000;
      int sampleSizeInBits = 16;
      int channels = 1;
      boolean signed = true;
      boolean bigEndian = false;
      return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
     }
private static void playAudio(byte[]audio) {
    try {
      
      InputStream input = 
        new ByteArrayInputStream(audio);
      final AudioFormat format = getFormat();
      final AudioInputStream ais = 
        new AudioInputStream(input, format, 
        audio.length / format.getFrameSize());
      DataLine.Info info = new DataLine.Info(
        SourceDataLine.class, format);
      final SourceDataLine line = (SourceDataLine)
        AudioSystem.getLine(info);
      line.open(format);
      line.start();

      Runnable runner = new Runnable() {
        int bufferSize = (int) format.getSampleRate() 
          * format.getFrameSize();
        byte buffer[] = new byte[bufferSize];
 
        public void run() {
          try {
            int count;
            while ((count = ais.read(
                buffer, 0, buffer.length)) != -1) {
              if (count > 0) {
                line.write(buffer, 0, count);
              }
            }
            line.drain();
            line.close();
          } catch (IOException e) {
            System.err.println("I/O problems: " + e);
            System.exit(-3);
          }
        }
      };
      Thread playThread = new Thread(runner);
      playThread.start();
    } catch (LineUnavailableException e) {
      System.err.println("Line unavailable: " + e);
      System.exit(-4);
    } 
}      
}

  

