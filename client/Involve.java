import javax.swing.JFrame;
import java.awt.*;
import java.io.File;
class Involve extends JFrame
{
Logo l=new Logo();
Involve()
{
super("Involve v1.1");
add(l,BorderLayout.CENTER);
setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
setSize(345,250); 
setLocation(450,250);
setVisible(true);
  try
   {
   Thread.sleep(2000);
   setVisible(false);
   new KskLogin();
   }catch(Exception e){System.out.println(e);}
}
 public static void main(String... s)
 {
  new Involve();
  }
}
class Logo extends Canvas
{   Image i;
    Logo()
	{  
	       Toolkit t=Toolkit.getDefaultToolkit();
		   File  f=new File(System.getProperty("user.dir"));
           i=t.getImage(f.getAbsolutePath()+"\\involve.png");
	}
	public void paint(Graphics g)
 {
 g.drawImage(i,3,0,345,213,this);
 }
}