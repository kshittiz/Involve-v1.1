import java.io.*;
import java.util.*;
import java.net.*;
import javax.swing.*;
import java.sql.*;
public class KskChatServer
{
 ObjectOutputStream os;Boolean b;
 ServerSocket ss;ArrayList al=new ArrayList();Socket s;int i=0;FileWriter fwp;ResultSet result;
 DefaultListModel model=new DefaultListModel();String entry;String pname;String password;int flag=0;String choice;
 int l=0;
 String socname[]=new String[20];
public KskChatServer()
{
 try
 {
  Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
      Connection c=DriverManager.getConnection("jdbc:odbc:login","ksk","ruleout");
  Statement state=c.createStatement();
  FileWriter fw=new FileWriter("chat.txt");
 ss=new ServerSocket(1993);
  while(true)
   {
     s=ss.accept();
      System.out.println("client connected!");
     
      DataInputStream din=new DataInputStream(s.getInputStream());
      DataOutputStream dout=new DataOutputStream(s.getOutputStream());
	  choice=din.readUTF();
	  System.out.println(choice);
	  if(choice.equals("SignIn"))
	  {
        entry=din.readUTF();
        password=din.readUTF();
     
        result=state.executeQuery("select Username from login where Password='"+password+"'");
       
         while(result.next()) //check for login password
         {
         
        if((result.getString(1)).equals(entry))
           {flag=1;break;}
        
         }
         if(flag==1)           //check for redundant entry
          {  if(model.contains(entry))
               b=false; 
             else
               b=true;
           }
         else
           b=false;
	   
            if(b==false)
                 dout.writeUTF("false");
             else
             {     dout.writeUTF("true");
                   pname="privatechat"+entry+".txt";
                   fwp=new FileWriter(pname);
                   model.addElement(entry);
                   al.add(s);
                   System.out.println(s);
				   os=new ObjectOutputStream(s.getOutputStream());
                   os.writeObject(model);
                   new ServerThread(entry,s,al,this).start();
                   i++;
                   l++;
              }
              b=true;flag=0;
              
	  }//end of signin
	   if(choice.equals("SignUp"))
	   {
		   String exit=din.readUTF();
		   if(!exit.equals("EXIT"))
		   {
		 String name,uname,pass;int check=1;
		 while(check==1)
		 {
		 name=din.readUTF();uname=din.readUTF();pass=din.readUTF();
		 System.out.println("signup query");
		 result=state.executeQuery("select Username from login");
		 while(result.next()) //check for redundant username
         {   
           if((result.getString(1)).equals(uname))
		   {flag=1;break;}
	     }
		 if(flag==1)
		 {dout.writeUTF("false");dout.flush();}
		 else
		  { dout.writeUTF("true");dout.flush();
	        state.executeUpdate("INSERT INTO login VALUES('"+uname+"','"+pass+"','"+name+"')");
			check=0;
		  }
		  flag=0;
		 }
		   }
	   }//end of signup
		 
	   if(choice.equals("ChangePassword"))
	   {
		   String exit=din.readUTF();
		   if(!exit.equals("EXIT"))
		   {
		 String uname,pass,npass;
		 uname=din.readUTF();pass=din.readUTF();npass=din.readUTF();
		 System.out.println("ChangePassword query");
		 state.executeUpdate("UPDATE login set Password='"+npass+"' where Username='"+uname+"' AND Password='"+pass+"'");
		   }
	   }//end of change password
	   if(choice.equals("DeleteAccount"))
	   {
		   String exit=din.readUTF();
		   if(!exit.equals("EXIT"))
		   {
		 String uname,pass;
		 uname=din.readUTF();pass=din.readUTF();
		 System.out.println("DeleteAccount query");
		 state.executeUpdate("DELETE * from login where Password='"+pass+"' AND Username='"+uname+"'");
		   }
	   }//end of DeleteAccount
    } //end of while
   
   }//end of try
 catch(Exception e){System.out.println(e);}
}
public static void main(String... s)
 {
   System.out.println("SERVER STARTED SUCCESSFULLY!");
  new KskChatServer();
 }
}

class ServerThread extends Thread
{
 Socket s;String name,pname,grpname;int control=0,cont=0;int id;Socket scp;DataOutputStream dout,doutp;DefaultListModel gmodel;
 ArrayList al;KskChatServer k;ObjectOutputStream os;FileWriter fw,fwp,fwg;String title;
 ObjectInputStream oin;String groupmem;
 ServerThread(String name,Socket s,ArrayList al,KskChatServer k)
 { super(name);
   this.name=name;
   this.s=s;
   this.al=al;
   this.k=k;
   //this.fw=fw; 
 }
  
 public void run()
 {
  String dat=Thread.currentThread().getName()+":";String data,da="",da1="",s1="";
  
 try
  {
   
   DataInputStream din=new DataInputStream(s.getInputStream());
   do
    {
    fw=new FileWriter("chat.txt",true);
   
    
    data=din.readUTF();//reads data in encrypted format
    System.out.println(data);
    if(data.equals("LoggedOut!!"))
       {// System.out.println(name+"activated logout");
           int pos=k.model.indexOf(name);
           k.model.remove(pos);    
            tellAll2(name);
       }
    if(data.equals("fileSharing!@#$%^&*"))
        {
			s1=data;	
			if((din.readUTF()).equals("NOTEXIT"))
			{
          String recievers=din.readUTF();System.out.println(recievers);
		  StringTokenizer st=new StringTokenizer(recievers);
		  while(st.hasMoreTokens())
              {
                 String x=st.nextToken();System.out.println(x);
				 if(k.model.contains(x))
				 { id=k.model.indexOf(x);
                   scp=(Socket)al.get(id);		
                   os=new ObjectOutputStream(scp.getOutputStream());
                   os.writeObject(k.model);				   
				    doutp=new DataOutputStream(scp.getOutputStream());
				   doutp.writeUTF("ready_download$");
                     doutp.flush();
					 ShareFiles.transferFiles(s,scp);
                   System.out.println("Files Transferred");
				  
			  }
		      }    
			}		   
        }
		if(data.equals("AudioMessage!@#$%^&*"))
        {
			s1=data;
		   String play=din.readUTF();
		  if(play.equals("NOTPLAY"))
		  {
			  System.out.println("notplay"+play);
	          String audio_name=din.readUTF();
              String recievers=din.readUTF();
		 
				 if(k.model.contains(recievers))
				 { id=k.model.indexOf(recievers);
                   scp=(Socket)al.get(id);		
                   os=new ObjectOutputStream(scp.getOutputStream());
                   os.writeObject(k.model);				   
				    doutp=new DataOutputStream(scp.getOutputStream());
				   doutp.writeUTF("ready_Audio$");doutp.flush();
				   doutp.writeUTF(audio_name);doutp.flush();
				   System.out.println("Files Transfer started");
					 ShareAudioFiles.transferMessage(s,scp);
                   System.out.println("Files Transferred");
				  
				 }  
		  }		   
        }
    if(data.equals("activatepublic!@#$%^&*"))
        {
          s1=data;
          data="Activated Announcement Mode!";
         }
   
    if(data.equals("activateprivate!@#$%^&*"))
        {
          s1=data;da=dat+"Nothing To Announce!`";
         fw.write(da);
         fw.close();         
          tellAll();
        }
    if(data.equals("valueChanged!@#$%^&*"))
        {
			 System.out.println("server value changed");
		    oin=new ObjectInputStream(s.getInputStream());
			gmodel=(DefaultListModel)oin.readObject();
			data=din.readUTF();
			System.out.println(s1);System.out.println(data);
			
        }
    if(data.equals("deletegroup!@#$%^&*"))
        {
          
		  oin=new ObjectInputStream(s.getInputStream());
			gmodel=(DefaultListModel)oin.readObject();
			 data=din.readUTF();
				 
				  System.out.println(data+"server");
				     
	                 StringTokenizer st1=new StringTokenizer(data,":");
			         String title=st1.nextToken();//skip title of group
			         groupmem=st1.nextToken();
					 System.out.println(groupmem);
					 StringTokenizer st=new StringTokenizer(groupmem,",{}");
					 String tempgrpmem="";
                         while(st.hasMoreTokens())
                             {   
						       String x=st.nextToken();System.out.println(x);System.out.println(dat);
						         if(!x.equals(Thread.currentThread().getName()))
							     tempgrpmem=tempgrpmem+x+",";
							 }	     
							   System.out.println(tempgrpmem);
								char temp[]=tempgrpmem.toCharArray();
								tempgrpmem="";
								for(int i=0;i<temp.length-1;i++)
									tempgrpmem=tempgrpmem+temp[i];
								
								 st=new StringTokenizer(tempgrpmem,",");	 
								 System.out.println("chutiye"+tempgrpmem);
								 while(st.hasMoreTokens())
                              {
								 String x=st.nextToken();
								 id=k.model.indexOf(x);
                                 scp=(Socket)al.get(id);//System.out.println(scp);
				                 os=new ObjectOutputStream(scp.getOutputStream());
                                 os.writeObject(k.model);
				                 doutp=new DataOutputStream(scp.getOutputStream());
				                 doutp.writeUTF("?"+data);
                                 doutp.flush();
								 doutp.writeUTF(title+":{"+tempgrpmem+"}");
                                 doutp.flush();
							  }
								
			
					 
	 }
    if(data.equals("activategroup!@#$%^&*"))
        {
          s1=data; 
		  oin=new ObjectInputStream(s.getInputStream());
			gmodel=(DefaultListModel)oin.readObject();
        }
    
    if(s1.equals("activatepublic!@#$%^&*"))
      {
         
        if(!data.equals("LoggedOut!!")) 
         {
         da=dat+data+"`";
         fw.write(da);
         fw.close();         
          tellAll();
          }  
      
      }


       if(s1.equals("activateprivate!@#$%^&*"))
       { 
         if(!data.equals("LoggedOut!!")) 
         {
        if(k.model.contains(data))
         {
          
          pname="privatechat"+data+".txt";
          id=k.model.indexOf(data);//System.out.println(data);
           scp=(Socket)al.get(id);System.out.println(scp);
         doutp=new DataOutputStream(scp.getOutputStream());
              //data="Wants to talk in private with you!"; 
            control=1;
          }
         
       if(!data.equals("LoggedOut!!") && (control==1)) 
         {
           os=new ObjectOutputStream(scp.getOutputStream());
           os.writeObject(k.model);
          fwp=new FileWriter(pname,true);
          da1="#"+dat+data+"`";
           System.out.println(da1);
         fwp.write(da1);
         fwp.close();
           String ss="";int l=0;char c; 
   
            FileReader fr=new FileReader(pname);
        
           while((l=fr.read())!=-1)
             {
              // System.out.println("enter");
               c=(char)l;
               if(c!='`')
                 ss=ss+c;
               else
                 ss=ss+"\n";
             }
          System.out.println(ss);
          fr.close();
         
         doutp.writeUTF(ss);
         doutp.flush();
               
        } 
          
         
       }//closing logout if
    } //closing private if 
  if(!data.equals("creategroup!@#$%^&*"))
        {
     if(s1.equals("activategroup!@#$%^&*"))
       { 
         
         if(!data.equals("LoggedOut!!")) 
         { 
	       
           if(gmodel.contains(data))
          {
          
          grpname="groupchat"+data+".txt";
          id=gmodel.indexOf(data);//System.out.println("data---------------------");
           
			StringTokenizer st1=new StringTokenizer(data,":");
			String title=st1.nextToken();//skip title of group
			groupmem=st1.nextToken();System.out.println(groupmem+"---");//token containing group members
            
            cont=1;
           }
         
       if(!data.equals("LoggedOut!!") && (cont==1)) 
         {
          //System.out.println("------------");
          fwg=new FileWriter(grpname,true);
          da1=">"+dat+data+"`";
          // System.out.println("------------");
          fwg.write(da1);
          fwg.close();
           String ss="";int l=0;char c; 
   
            FileReader fr=new FileReader(grpname);
        
           while((l=fr.read())!=-1)
             {
              // System.out.println("enter");
               c=(char)l;
               if(c!='`')
                 ss=ss+c;
               else
                 ss=ss+"\n";
             }
          //System.out.println(ss);
          fr.close();
		   StringTokenizer st=new StringTokenizer(groupmem,",{}");
         while(st.hasMoreTokens())
           {
                 String x=st.nextToken();//System.out.println(x);
                id=k.model.indexOf(x);
                scp=(Socket)al.get(id);//System.out.println(scp);
				os=new ObjectOutputStream(scp.getOutputStream());
                os.writeObject(k.model);
				doutp=new DataOutputStream(scp.getOutputStream());
				doutp.writeUTF(ss);
                doutp.flush();
		   }
         
               
         } //over if(!data.equals("LoggedOut!!") && (control==1))
	   }//end of logout
     }//close groupchat
	}
if(data.equals("creategroup!@#$%^&*"))
        {
			 
           String exit=din.readUTF();
		   if(!exit.equals("EXIT"))
		   {
			String gtitle=din.readUTF();//System.out.println("server"+gtitle);
			StringTokenizer st1=new StringTokenizer(gtitle,":");
			String title=st1.nextToken();//skip title of group
			groupmem=st1.nextToken();//System.out.println(groupmem);//token containing group members
           StringTokenizer st=new StringTokenizer(groupmem,",{}");
              while(st.hasMoreTokens())
              {
                 String x=st.nextToken();//System.out.println(x);
                id=k.model.indexOf(x);
                scp=(Socket)al.get(id);//System.out.println(scp);
				os=new ObjectOutputStream(scp.getOutputStream());
                os.writeObject(k.model);
                doutp=new DataOutputStream(scp.getOutputStream());
                doutp.writeUTF(gtitle);//System.out.println(gtitle);
				doutp.flush();
             }
		 }
       }
	  
    }while(!data.equals("LoggedOut!!"));
       
     
   }//end try
    catch(Exception e){System.out.println("1:"+e);}
   
  }
    
   public void tellAll2(String name)throws IOException
 {
   Iterator i=al.iterator(); 
    while(i.hasNext())
    {
        Socket sc=(Socket)i.next();
         os=new ObjectOutputStream(sc.getOutputStream());
         os.writeObject(k.model);
         DataOutputStream dout=new DataOutputStream(sc.getOutputStream());
         dout.writeUTF(name);
         dout.flush();
        // dout.writeUTF("LoggedOut!!");
        // dout.flush();
    
    }
  }
 
  
 public void tellAll()throws IOException
 {
   Iterator i=al.iterator();String s1="";int l=0;char c; 
   
   FileReader fr=new FileReader("chat.txt");
        
           while((l=fr.read())!=-1)
            {
              // System.out.println("enter");
               c=(char)l;
               if(c!='`')
                 s1=s1+c;
               else
                 s1=s1+"\n";
            }
         //System.out.println(s1);
           update(s1);
 fr.close();
 }

 public void update(String s1)throws IOException
   {
      Iterator i=al.iterator();
              while(i.hasNext())
                     {
                   
                      Socket x=(Socket)i.next();
                      os=new ObjectOutputStream(x.getOutputStream());
                      os.writeObject(k.model);
                     DataOutputStream dout=new DataOutputStream(x.getOutputStream());
                    //System.out.println(s1);
                    dout.writeUTF(s1);
                    dout.flush();
                      }
   }
  
    
 } 
        
      
    
      
      
