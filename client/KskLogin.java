import java.io.*;
import java.net.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.event.*;
import javax.swing.event.*;
import java.awt.*;
import java.util.*;
import javax.sound.sampled.*;
public class KskLogin extends JFrame implements ActionListener
 {
  JButton b0;JTextField tf1=new JTextField();JTextField tf2=new JTextField();Socket s;String x;String entry;String list[];String server_address;
  GridLayout gl=new GridLayout(2,2);
  JLabel jl1=new JLabel("  Name :");
  JLabel jl2=new JLabel("  Password :");
  ObjectInputStream oin;DataInputStream din;DefaultListModel model;
  DataOutputStream dout;
  JMenuBar mb=new JMenuBar();
  JMenu jm1=new JMenu("Settings");
  
  JMenuItem m1=new JMenuItem("Sign up?");
  JMenuItem m2=new JMenuItem("Change Password");
  JMenuItem m3=new JMenuItem("Delete Account");
  JMenuItem m4=new JMenuItem("Server Settings");
  JPanel jp=new JPanel();
  KskLogin()
  {
    super("INvOLVE v1.1");
    jp.setLayout(gl);
    jp.add(jl1);jp.add(tf1);
    jp.add(jl2);jp.add(tf2);
    b0=new JButton("Sign In");
    b0.addActionListener(this);
	m4.addActionListener(this);
	m1.addActionListener(this);
	m2.addActionListener(this);
	m3.addActionListener(this);
    b0.setBackground(Color.YELLOW);
    jm1.add(m1);
    jm1.add(m2);
    jm1.add(m3);
    jm1.add(m4);
    mb.add(jm1);
    getRootPane().setDefaultButton(b0);
    add(mb,BorderLayout.NORTH);
    add(b0,BorderLayout.SOUTH);
    add(jp,BorderLayout.CENTER);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setVisible(true);
    setSize(350,150);
  }
 public void actionPerformed(ActionEvent e)
 {
 try
   {
	   if((e.getActionCommand()).equals("Server Settings"))  
		{
	     new ServerSettings(this);
	     s=new Socket(server_address,1993);
		 dout=new DataOutputStream(s.getOutputStream());
         din=new DataInputStream(s.getInputStream());
		 dout.writeUTF("Server Settings");dout.flush();
		}
		else
	    {
	       FileReader fr=new FileReader("server_settings.txt");
	       BufferedReader br=new BufferedReader(fr);
	       String sa="";
	        while(sa!=null)
		     {sa=br.readLine();
              if(sa!=null)
		      server_address=sa;
	         }
	        System.out.println(server_address);
		    s=new Socket(server_address,1993);
		    dout=new DataOutputStream(s.getOutputStream());
            din=new DataInputStream(s.getInputStream());
		}
		
		
		if((e.getActionCommand()).equals("Sign up?"))
		{
		   dout.writeUTF("SignUp");dout.flush();
       		new SignUp(this,s,dout,din);
		}
		if((e.getActionCommand()).equals("Change Password"))
		{
		   dout.writeUTF("ChangePassword");dout.flush();
       		new ChangePassword(this,s,dout,din);
		}
		if((e.getActionCommand()).equals("Delete Account"))
		{
		   dout.writeUTF("DeleteAccount");dout.flush();
       		new DeleteAccount(this,s,dout,din);
		}
		if(e.getSource()==b0)
		{
                dout.writeUTF("SignIn");dout.flush();
                x=tf1.getText();
                dout.writeUTF(x);dout.flush();
                dout.writeUTF(tf2.getText());dout.flush();
                entry=din.readUTF();System.out.println(entry);
                if(entry.equals("true"))
                {
                  oin=new ObjectInputStream(s.getInputStream()); 
                  model=(DefaultListModel)oin.readObject(); 
                }
                  else
                   {tf1.setText("Invalid Entry!");tf2.setText("Try Again!");}
			    if(entry.equals("true"))
                 {
                  new KskClient(this,s,model,"INvOLVE v1.1 :: "+x);
                 }
        }  
   }		
   catch(Exception ex){System.out.println("Oops!! Wrong address of server!");}
  
 }
 
 /*  
  public static void main(String... s)
  {
   new KskLogin();
  }*/
}
   
   class ServerSettings extends JDialog implements ActionListener
{
 JLabel jl=new JLabel("Enter Server IP-Address Below:");
 JButton ok=new JButton("Ok");
 JTextField ip;
 KskLogin login;

 public ServerSettings(KskLogin login)
 {
  super(login,"Server Settings",true);
  this.login=login;
  ip=new JTextField();
  ok.setBackground(Color.RED);
  ok.addActionListener(this);
  add(jl,BorderLayout.NORTH);
  add(ip,BorderLayout.CENTER);
  add(ok,BorderLayout.SOUTH);
  setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
  setSize(250,100);
  setVisible(true);
  }
 public void actionPerformed(ActionEvent e)
 {
	 try
	 {
             String ipaddress=ip.getText();System.out.println(ipaddress);
             FileWriter fw=new FileWriter("server_settings.txt");
	         BufferedWriter bw=new BufferedWriter(fw);
	         bw.write(ipaddress);bw.flush();
			 fw.close();
             setVisible(false);
     }
	 catch(Exception ex){}
 }
}

class SignUp extends JDialog implements ActionListener
{
 JLabel jl1=new JLabel("Name :");
 JLabel jl2=new JLabel("UserName :");
 JLabel jl3=new JLabel("Password :");
 JLabel jl4=new JLabel("Confirm Password :");
 JPanel jp=new JPanel();
 JButton ok=new JButton("Sign Up!");
 JTextField tf1,tf2,tf3,tf4;
 String name,uname,pass,cpass;
 KskLogin login;Socket s;DataOutputStream dout;DataInputStream din;
 String check;
 public SignUp(KskLogin login,Socket s,DataOutputStream dout,DataInputStream din)
 {
  super(login,"Sign Up",true);
  this.login=login;
  this.s=s;
  this.dout=dout;
  this.din=din;
  tf1=new JTextField();
  tf2=new JTextField();
  tf3=new JTextField();
  tf4=new JTextField();
  jp.setLayout(new GridLayout(4,2));
  jp.add(jl1);jp.add(tf1);
  jp.add(jl2);jp.add(tf2);
  jp.add(jl3);jp.add(tf3);
  jp.add(jl4);jp.add(tf4); 
  ok.setBackground(Color.GREEN);
  ok.addActionListener(this);
  add(jp,BorderLayout.CENTER);
  add(ok,BorderLayout.SOUTH);
  setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
  addWindowListener(new Exit(dout));
  setSize(450,180);
  setVisible(true);
  }
  public void actionPerformed(ActionEvent e)
  {
   try{
	    dout.writeUTF("NOTEXIT");dout.flush();
	   name=tf1.getText();uname=tf2.getText();pass=tf3.getText();cpass=tf4.getText();
	   if(pass.equals(cpass))
	    {
			dout.writeUTF(name);dout.writeUTF(uname);dout.writeUTF(pass);
			check= din.readUTF();
			System.out.println(check);
           if(check.equals("false"))
              tf2.setText("UserName Already Exist");
           else
              setVisible(false);	
	    }
       else
        {tf3.setText("Password Not Matching");tf4.setText("Password Not Matching");}
        
      }
	  catch(Exception ex){System.out.println(ex);}
  }
 }
 
 class DeleteAccount extends JDialog implements ActionListener
{
 JLabel jl2=new JLabel("UserName :");
 JLabel jl3=new JLabel("Password :");
 JLabel jl4=new JLabel("Confirm Password :");
 JPanel jp=new JPanel();
 JButton ok=new JButton("Confirm Delete!");
 JTextField tf2,tf3,tf4;
 String uname,pass,cpass;
 KskLogin login;Socket s;DataOutputStream dout;DataInputStream din;
 public DeleteAccount(KskLogin login,Socket s,DataOutputStream dout,DataInputStream din)
 {
  super(login,"Delete Account",true);
  this.login=login;
  this.s=s;
  this.dout=dout;
  this.din=din;
  tf2=new JTextField();
  tf3=new JTextField();
  tf4=new JTextField();
  jp.setLayout(new GridLayout(3,2));
  jp.add(jl2);jp.add(tf2);
  jp.add(jl3);jp.add(tf3);
  jp.add(jl4);jp.add(tf4); 
  ok.setBackground(Color.BLUE);
  ok.addActionListener(this);
  add(jp,BorderLayout.CENTER);
  add(ok,BorderLayout.SOUTH);
  setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
  addWindowListener(new Exit(dout));
  setSize(450,150);
  setVisible(true);
  }
  public void actionPerformed(ActionEvent e)
  {
   try{
	   	dout.writeUTF("NOTEXIT");dout.flush();
	   uname=tf2.getText();pass=tf3.getText();cpass=tf4.getText();
	   if(pass.equals(cpass))
	    {
			dout.writeUTF(uname);dout.writeUTF(pass);setVisible(false);
	    }
       else
        {tf3.setText("Password Not Matching");tf4.setText("Password Not Matching");}
        
      }
	  catch(Exception ex){}
  }
 }
 class ChangePassword extends JDialog implements ActionListener
{
 JLabel jl2=new JLabel("UserName :");
 JLabel jl3=new JLabel("Password :");
 JLabel jl4=new JLabel("New Password :");
 JLabel jl5=new JLabel("Confirm Password :");
 JPanel jp=new JPanel();
 JButton ok=new JButton("Change Password!");
 JTextField tf2,tf3,tf4,tf5;
 String uname,pass,npass,cpass;
 KskLogin login;Socket s;DataOutputStream dout;DataInputStream din;
 public ChangePassword(KskLogin login,Socket s,DataOutputStream dout,DataInputStream din)
 {
  super(login,"Change Password",true);
  this.login=login;
  this.s=s;
  this.dout=dout;
  this.din=din;
  tf2=new JTextField();
  tf3=new JTextField();
  tf4=new JTextField();
  tf5=new JTextField();
  jp.setLayout(new GridLayout(4,2));
  jp.add(jl2);jp.add(tf2);
  jp.add(jl3);jp.add(tf3);
  jp.add(jl4);jp.add(tf4); 
  jp.add(jl5);jp.add(tf5);
  ok.setBackground(Color.GRAY);
  ok.addActionListener(this);
  add(jp,BorderLayout.CENTER);
  add(ok,BorderLayout.SOUTH);
  setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
  addWindowListener(new Exit(dout));
  setSize(450,180);
  setVisible(true);
  }
  public void actionPerformed(ActionEvent e)
  {
   try{
	   	dout.writeUTF("NOTEXIT");dout.flush();
	   uname=tf2.getText();pass=tf3.getText();npass=tf4.getText();cpass=tf5.getText();
	   if(npass.equals(cpass))
	    {
			dout.writeUTF(uname);dout.writeUTF(pass);dout.writeUTF(npass);setVisible(false);
	    }
       else
        {tf4.setText("Password Not Matching");tf5.setText("Password Not Matching");}
        
      }
	  catch(Exception ex){}
  }
 }
 
class KskClient extends JDialog implements ActionListener,ListSelectionListener
{
  Socket s;
  ObjectInputStream oin;DefaultListModel model;int out=0; int deletecontrol=0;
  DefaultListModel gmodel=new DefaultListModel();String groupmem="";String namelist[];String title="";String deletetitle="";
  DataInputStream din;String list[];String sx,s2,s1="";JList jL,gjL;JScrollPane sp,sp0,sp1,sp2,sp3;
  DataOutputStream dout;FlowLayout fl=new FlowLayout();
  JPanel jp0=new JPanel();JPanel jp1=new JPanel();JPanel jp2=new JPanel();JPanel jp3=new JPanel();JPanel jp4=new JPanel();
  JTextField tf=new JTextField();
  JTextArea ta=new JTextArea("Announcement Window");JTextArea ta1=new JTextArea("Private Chat Window.....");JTextArea ta2=new JTextArea("Group Chat Window.....");
  JLabel jl,jl1;Border bo=BorderFactory.createTitledBorder("");
  Border bev=BorderFactory.createLoweredBevelBorder();
  String t="<html><FONT COLOR=BLUE>People Online</FONT></html>";
  String t1="<html><FONT COLOR=ORANGE>Active Group</FONT></html>";
  String txt="<html><FONT COLOR=BLUE>Designed By.......</FONT>"+"<FONT COLOR=RED>KsK</FONT></html>";

  String txt1="Enter Your Message Here:";
  
  String help="<html><FONT COLOR=RED>Involve Help........</FONT><br>1:Home screen buttons are easy to use buttons for instantly performing various functions like create group,share files or start chat.<br>2:Group Chat menu can be used to start group chat or delete any specific group.<br>3:Help menu can be used to access Help or to know about Involve 1.1.</html>";
  String about="<html><FONT COLOR=GREEN>Involve v1.1<br>This is an instant messaging and media sharing application designed specically for Intera-college communication among various professors of college.</FONT><br><br>"+"<FONT COLOR=RED>Designed By Kshittiz Kumar,Lalit Rajput and Amit Harjani.</FONT></html>";
  JMenuBar mb=new JMenuBar();
  JMenu grpchat=new JMenu("Group Chat");JMenu hlp=new JMenu("Help");
  JMenuItem itm1=new JMenuItem("Start Group Chat");JMenuItem itm2=new JMenuItem("Delete Group");
  JMenuItem itm4=new JMenuItem("View Help");JMenuItem itm5=new JMenuItem("About Involve");
  
  
  Color color = new Color(20,10,30,120);
        
  static boolean running;
  JButton b1,b2,b3,b4,gc,am,fs;
  KskLogin kl;
  public KskClient(KskLogin kl,Socket s,DefaultListModel model,String title)
 {
  super(kl,title,true);
  this.model=model;this.s=s;this.kl=kl;
  jp4.setLayout(new GridLayout(3,1));
  jp0.setLayout(new GridLayout(2,1));
  
  Border obor=BorderFactory.createTitledBorder(bev,t);
  Border gbor=BorderFactory.createTitledBorder(bev,t1);
  
  jl1=new JLabel(txt1,JLabel.CENTER);jl1.setBorder(bo);
  jl=new JLabel(txt,JLabel.CENTER);jl.setBorder(bo); 

  grpchat.add(itm1); grpchat.add(itm2);
  hlp.add(itm4);hlp.add(itm5);
  mb.add(grpchat);mb.add(hlp);
  jp0.add(mb);mb.setBackground(color);

  gc=new JButton("Create Group");am=new JButton("Audio Message");fs=new JButton("Share Files");
  gc.addActionListener(this);am.addActionListener(this);fs.addActionListener(this);
  b1=new JButton("One-To-One Chat");b2=new JButton("Announcements");b3=new JButton("Send");b4=new JButton("Quit Chat!");
  b1.addActionListener(this);  b2.addActionListener(this);  b3.addActionListener(this);b4.addActionListener(this);
  itm1.addActionListener(this);itm2.addActionListener(this);itm4.addActionListener(this);itm5.addActionListener(this);
  b1.setBackground(Color.ORANGE);b2.setBackground(Color.WHITE);b3.setBackground(Color.GREEN);b4.setBackground(Color.RED);
  gc.setBackground(Color.ORANGE);am.setBackground(Color.GREEN);fs.setBackground(Color.GREEN);
  jp1.add(gc);
  jp1.add(b1);jp1.add(b2);jp1.add(fs);jp1.add(am);jp1.add(b4);
  jp0.add(jp1);
  add(jp0,BorderLayout.NORTH);jp2.setLayout(new GridLayout(3,1));
  jp2.add(jl1);jp2.add(tf);jp2.add(b3);//jp2.add(jl);
  add(jp2,BorderLayout.SOUTH);
  sp0=new JScrollPane(ta2);
  sp1=new JScrollPane(ta); sp2=new JScrollPane(ta1);
  jp4.add(sp1);
  jp4.add(sp2);
  jp4.add(sp0); 
  add(jp4,BorderLayout.CENTER);
  jp3.setLayout(new BorderLayout());
  
  jL=new JList(model);jL.setBorder(obor);
  jL.addListSelectionListener(this);
  gjL=new JList(gmodel);gjL.setBorder(gbor);
  gjL.addListSelectionListener(this);
  getRootPane().setDefaultButton(b3);
 
   try
   {
     din=new DataInputStream(s.getInputStream());
     dout=new DataOutputStream(s.getOutputStream()); 
     new ClientThread(din,this).start();  
     System.out.println("Client Thread Started");
    }
    catch(Exception e){}
   
    jL.setVisibleRowCount(5);
    sp=new JScrollPane(jL);
    
    gjL.setVisibleRowCount(5);
    sp3=new JScrollPane(gjL);
    
    jp3.add(sp,BorderLayout.NORTH);
    jp3.add(sp3,BorderLayout.CENTER);
    add(jp3,BorderLayout.EAST);
    
  setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
   setSize(740,650);
  setVisible(true); 
  }

  public void actionPerformed(ActionEvent e)
  {
  try
  {
   
  if(e.getSource()==b2 )
    {dout.writeUTF("activatepublic!@#$%^&*");dout.flush();}
  if(e.getSource()==b1 ) 
   {dout.writeUTF("activateprivate!@#$%^&*");sx="activateprivate!@#$%^&*";ta.setText("You Activated One-To-One Chat Mode!");
    dout.flush();}
  if(e.getSource()==b3)
   {
    s1=tf.getText(); dout.writeUTF(s1);
    dout.flush();
   }
   
  if(e.getSource()==b4)
   {dout.writeUTF("LoggedOut!!");out=1;dout.flush();}
    
  if(e.getSource()==gc)
  {
    dout.writeUTF("creategroup!@#$%^&*");dout.flush();
	 new Groupchat(this);
   
  }
  if(e.getSource()==fs)
  {
	 fs.setBackground(Color.GREEN);
	dout.writeUTF("fileSharing!@#$%^&*");dout.flush();
	new Filesharing(this);
   
  }
  if(e.getSource()==am)
  {
	 am.setBackground(Color.GREEN);
	dout.writeUTF("AudioMessage!@#$%^&*");dout.flush();
	new AudioMessage(this); 
  }
  
  if((e.getActionCommand()).equals("Start Group Chat"))  
  {
   dout.writeUTF("activategroup!@#$%^&*");sx="activategroup!@#$%^&*";ta2.setText("You Activated Group Chat Mode!");
    dout.flush();
	ObjectOutputStream os=new ObjectOutputStream(s.getOutputStream()); 
    os.writeObject(gmodel);
  }
if((e.getActionCommand()).equals("Delete Group"))  
  {
   dout.writeUTF("deletegroup!@#$%^&*");sx="deletegroup!@#$%^&*";ta2.setText("Select Group to Delete it!");
    dout.flush();
	deletecontrol=1;
  }
  if((e.getActionCommand()).equals("View Help"))
                    {
                        JDialog jd=new JDialog(this,"Help",true);
                        
                        JLabel l=new JLabel(help,JLabel.LEFT);
                         jd.add(l,BorderLayout.CENTER);
                         jd.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                         jd.setSize(500,150);
                         jd.setVisible(true);
                    }   
 if((e.getActionCommand()).equals("About Involve"))
                   {
                        JDialog jd=new JDialog(this,"About",true);
                        JLabel l=new JLabel(about,JLabel.LEFT);
                        jd.add(l,BorderLayout.CENTER);
                        jd.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                         jd.setSize(500,120);
                         jd.setVisible(true);    
                   }  

 }
   
  catch(Exception iox)
  {}
 }
  public void valueChanged(ListSelectionEvent le)
  {
    if(le.getSource()==jL)
   {
       try
       {
        
   int id=jL.getSelectedIndex();
   String name=(String)model.getElementAt(id);
   System.out.println(id);
   if(id!=-1 && sx.equals("activateprivate!@#$%^&*") && le.getValueIsAdjusting())
        { 
          dout.writeUTF(name);
         ta.setText("You can talk to "+name+" Privately");
        }  
        }
       catch(Exception iox)
          {}

    }
      if(le.getSource()==gjL)
   {
     try
      {
        int id=gjL.getSelectedIndex();
        String name=(String)gmodel.getElementAt(id);deletetitle=name;
        if(id!=-1 && sx.equals("activategroup!@#$%^&*") && le.getValueIsAdjusting())
        { 
          dout.writeUTF("valueChanged!@#$%^&*");System.out.println("client value change");
		  ObjectOutputStream os=new ObjectOutputStream(s.getOutputStream()); 
          os.writeObject(gmodel);
          ta.setText("You can Start Your Group Chat!");
		  dout.writeUTF(name);dout.flush();System.out.println("client value change");
         }

	 if(id!=-1 && deletecontrol==1 && sx.equals("deletegroup!@#$%^&*") && le.getValueIsAdjusting())
		  {
			  ObjectOutputStream os=new ObjectOutputStream(s.getOutputStream()); 
              os.writeObject(gmodel);
		      dout.writeUTF(name);dout.flush();
			  gmodel.remove(gmodel.indexOf(deletetitle));
			  
		  deletecontrol=0;
		  }	
 
       }
      catch(Exception iox)
          {}

   }
  }

 public void createGroup(String title,String namelist[])
 {
  try{
   System.out.println("createGroup funct"+title);
   dout.writeUTF("NOTEXIT");dout.flush();
   this.namelist=namelist;
      for(int i=0;i<namelist.length;i++)
       {
          if(i!=namelist.length-1)
            groupmem=groupmem+namelist[i]+",";
          else
            groupmem=groupmem+namelist[i];
        }
    this.title="~"+title+":{"+groupmem+"}";
	groupmem="";
	dout.writeUTF(this.title);
    dout.flush();
   //gmodel.addElement(tem);
	 }catch(Exception iox){System.out.println(iox);}
   }
  
}




class ClientThread extends Thread
 {
   DataInputStream din;KskClient k;ObjectInputStream oin;DefaultListModel mod=new DefaultListModel();int p=0;
   boolean flag=true;char ar[];
   ClientThread(DataInputStream din,KskClient k)
     {
       this.din=din;this.k=k;
     }

  public void run()
   {
      System.out.println("Started Successfully");
      String s2=" ";
     do
      {
        try
        {
             int p=k.model.indexOf(k.model.lastElement())+1;
          
             oin=new ObjectInputStream(k.s.getInputStream()); 
            mod=(DefaultListModel)oin.readObject();
            
          
            for(int i=p;i<mod.getSize();i++)
             k.model.addElement(mod.getElementAt(i));
             
      
           s2=din.readUTF();
           ar=s2.toCharArray();System.out.println(s2);
		   String del="";
		   for(int i=1;i<s2.length();i++)
			 del=del+ar[i];  
           String check=""+ar[0];
           //System.out.println(ar[0]);
           if(k.model.contains(s2))
           {
            //System.out.println(s2);
           int pos=k.model.indexOf(s2);//System.out.println(pos);
             String x=(String)k.model.get(pos);
              k.model.remove(pos);
             if(x.equals(k.kl.x))
               flag=false;
           }
           else if(check.equals("#"))
            k.ta1.setText(s2);

           else if(check.equals("?"))
		   {k.gmodel.remove(k.gmodel.indexOf(del));
	         k.gmodel.addElement(din.readUTF());
		   }

           else if(check.equals("~"))
		   {k.gmodel.addElement(s2);k.title=s2;}

		   else if(check.equals(">"))
            k.ta2.setText(s2);

		   else if(s2.equals("ready_download$"))
		   {
                    String ping=din.readUTF();
                    if(ping.equals("file"))
                       ShareFiles.recieveFiles(k.din);
				   k.ta.setText("You have recieved few files!");
				   k.fs.setBackground(Color.BLUE);
				  
		   }
		   else if(s2.equals("ready_Audio$"))
		   {
                    String audio_name=din.readUTF();System.out.println(audio_name);
                       ShareAudioFiles.recieveMessage(k.din,audio_name);
				   k.ta.setText("You have recieved few Audio Messages!");
				   k.am.setBackground(Color.BLUE);
				  
		   }
           else
           k.ta.setText(s2);   
         }
        catch(Exception e)
         {}
      }while(flag && k.out!=1);//!s2.equals("LoggedOut!!")
      System.out.println("Client Thread Ended!");
     }
 }


class Groupchat extends JDialog implements ActionListener,ListSelectionListener
{
 KskClient client;JScrollPane sp;
 JButton ok=new JButton("Ok");
 JTextField name=new JTextField();String namelist[];
 JList jl;DefaultListModel model;
 String title;
 Groupchat(KskClient client)
 {
  super(client,"Create Group",true);
  this.client=client;
  Border nbor=BorderFactory.createTitledBorder(client.bev,"Group Name");
  Border gbor=BorderFactory.createTitledBorder(client.bev,client.t);
  
  model=client.model;
  jl=new JList(model);
  jl.addListSelectionListener(this);
  jl.setBorder(gbor);
  sp=new JScrollPane(jl);
  
  ok.setBackground(Color.GREEN);
  ok.addActionListener(this);
  name.setBorder(nbor);
  add(name,BorderLayout.NORTH);
  add(sp,BorderLayout.CENTER);
  add(ok,BorderLayout.SOUTH);
  setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
  addWindowListener(new Exit(client.dout));
  setSize(250,300);
  setVisible(true);
  }
 public void actionPerformed(ActionEvent e)
 {
  title=name.getText();
  client.createGroup(title,namelist);
  setVisible(false);
  }
 public void valueChanged(ListSelectionEvent le)
  {
   
     try
      {
        
   int idx[]=jl.getSelectedIndices();
   namelist=new String[idx.length];
   for(int i=0;i<idx.length;i++)
   namelist[i]=(String)model.getElementAt(idx[i]);

   //System.out.println("Func over");
    
      }
    catch(Exception iox)
     {}
  }
   
    
  
}

  class Filesharing extends JDialog implements ActionListener,ListSelectionListener
{
 KskClient client;JScrollPane sp;File f[];int x,idx[];String recievers="";
 JButton browse=new JButton("Browse Files");
 JButton upload=new JButton("Upload");
 JTextField filelist=new JTextField();String namelist[];
 JList jl;DefaultListModel model;
 JPanel jp=new JPanel(new BorderLayout());
 DataInputStream din;DataOutputStream dout;
 Filesharing(KskClient client)
 {
  super(client,"File Sharing",true);
  this.client=client;
  Border nbor=BorderFactory.createTitledBorder(client.bev,"Selected Files");
  Border gbor=BorderFactory.createTitledBorder(client.bev,client.t);
  din=client.din;
  dout=client.dout;
  model=client.model;
  jl=new JList(model);
  jl.addListSelectionListener(this);
  jl.setBorder(gbor);
  sp=new JScrollPane(jl);
  
  browse.setBackground(Color.GRAY);
  browse.addActionListener(this);
  upload.setBackground(Color.GREEN);
  upload.addActionListener(this);
  filelist.setBorder(nbor);
  jp.add(filelist,BorderLayout.CENTER);
  jp.add(browse,BorderLayout.SOUTH);
  add(sp,BorderLayout.CENTER);
  add(jp,BorderLayout.NORTH);
  add(upload,BorderLayout.SOUTH);
  setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
  addWindowListener(new Exit(dout));
  setSize(250,300);
  setVisible(true);
  }
 public void actionPerformed(ActionEvent e)
 {
    try{
		
    if(e.getSource()==browse)
     { 
          JFileChooser jfc=new JFileChooser();
          jfc.setMultiSelectionEnabled(true);
          jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
          x=jfc.showOpenDialog(null);
          if(x==JFileChooser.APPROVE_OPTION)
           { f=jfc.getSelectedFiles();
             String t=""; 
                 for(int i=0;i<f.length;i++)
                 {t=t+f[i].getPath()+", "; filelist.setText(t);}
			 
           }
     }
	 if(e.getSource()==upload)
     { 
          dout.writeUTF("NOTEXIT");
          for(int i=0;i<namelist.length;i++)
			  recievers=recievers+namelist[i]+" ";
		  System.out.println(recievers);
		  dout.writeUTF(recievers);
		  dout.flush();
		  for(int i=0;i<idx.length;i++)
		  ShareFiles.uploadFile(new ArrayList<File>(Arrays.asList(f)),client,din,dout);
		  setVisible(false);
		  client.ta.setText("Files Sent Successfully");
     }
	}catch(Exception ex){}
  }
 public void valueChanged(ListSelectionEvent le)
  {
   
     try
      {
        
   idx=jl.getSelectedIndices();
   namelist=new String[idx.length];
   for(int i=0;i<idx.length;i++)
   namelist[i]=(String)model.getElementAt(idx[i]);

   //System.out.println("Func over");
    
      }
    catch(Exception iox)
     {}
  }
   
    
  
}
class AudioMessage extends JDialog implements ActionListener,ListSelectionListener
{
 KskClient client;JScrollPane sp;File f;int x,idx,check=0,wait=1;String recievers="";String audio_name;
 JButton start=new JButton("Start");
 JButton stop=new JButton("Stop");
 JButton play=new JButton("Play Audio Messages");
 JLabel label=new JLabel("Title : ");
 JTextField title=new JTextField();String namelist[];
 JList jl;DefaultListModel model;
 JPanel jp=new JPanel(new BorderLayout());
 DataInputStream din;DataOutputStream dout;
 AudioMessage(KskClient client)
 {
  super(client,"Audio Message",true);
  this.client=client;
  din=client.din;
  dout=client.dout;
  model=client.model;
  jl=new JList(model);
  jl.addListSelectionListener(this);
  Border gbor=BorderFactory.createTitledBorder(client.bev,client.t);
  jl.setBorder(gbor);
  sp=new JScrollPane(jl);
  jp.add(label,BorderLayout.WEST);jp.add(title,BorderLayout.CENTER);
  start.setBackground(Color.GRAY);
  start.addActionListener(this);
  stop.setBackground(Color.RED);
  stop.addActionListener(this);
  play.setBackground(Color.ORANGE);
  play.addActionListener(this);
  add(play,BorderLayout.SOUTH);
  add(sp,BorderLayout.CENTER);
  add(start,BorderLayout.WEST);
  add(stop,BorderLayout.EAST);
  add(jp,BorderLayout.NORTH);
  setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
  addWindowListener(new Exit(dout));
  setSize(300,200);
  setVisible(true);
  }
 public void actionPerformed(ActionEvent e)
 {
 try{
   if(check==1)
   { 
      if(wait==1)
	  {
       if(e.getSource()==start)
         {  dout.writeUTF("NOTPLAY");dout.flush();
	        start.setBackground(Color.BLUE);
		      System.out.println(recievers);
			  audio_name=title.getText();
			  dout.writeUTF(audio_name);
		      dout.flush();
		      dout.writeUTF(recievers);
		      dout.flush();
			  client.running=true;
			  System.out.println("capture started");
              ShareAudioFiles.captureAudio(dout);
         }
	    wait=0;
	  }
	 
	  if(e.getSource()==stop)
        {    wait=1;
	        start.setBackground(Color.GRAY);
            client.running=false;
			setVisible(false);
       }   
   }
    if(e.getSource()==play)
         { 
      try {
		   dout.writeUTF("PLAY");dout.flush();
		   String recievedirec=System.getProperty("user.dir");
           recievedirec=recievedirec+"\\Audio";
		   JFileChooser jfc=new JFileChooser(recievedirec);
          jfc.setMultiSelectionEnabled(false);
          jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
          x=jfc.showOpenDialog(null);
          if(x==JFileChooser.APPROVE_OPTION)
           f=jfc.getSelectedFile();
        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(f.getAbsoluteFile());
        Clip clip = AudioSystem.getClip();
        clip.open(audioInputStream);
        clip.start();
		  setVisible(false);
          } catch(Exception ex) {}
		 }
 }catch(Exception ex){System.out.println(ex);}
}
 public void valueChanged(ListSelectionEvent le)
  {
   check=1;
     try
      {
        
   idx=jl.getSelectedIndex();
   //namelist=new String[idx.length];
   //for(int i=0;i<idx.length;i++)
    //namelist[]=(String)model.getElementAt(idx[i]);
    recievers=(String)model.getElementAt(idx);

   //System.out.println("Func over");
    
      }
    catch(Exception iox)
     {}
  }
   
    
  
}
  
class Exit extends WindowAdapter
{
    DataOutputStream dout;
	Exit(DataOutputStream dout)
	{
		this.dout=dout;
	}
	 public void windowClosing(WindowEvent e)
	 {
		  try{
		   dout.writeUTF("EXIT");dout.flush();
		   System.out.println("EXIT");
		  }catch(Exception ex){}
	 }
}

