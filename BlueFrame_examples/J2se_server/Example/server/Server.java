package server;
import BlueFrame.*;

public class Server
{
public static void main(java.lang.String arg[])
{
try{



      BlueFrame.BlueServer blueServer = BlueServer.getInstance();
      blueServer.startServer();
      while(!blueServer.isConnected());

      System.out.println("Maya " +blueServer.getString());
      System.out.println("\nEnter A String To Send");
      int num=0;
      String str = "";
      while(num!='\n')
      {
       num = System.in.read();
       str +=(char)num;
      }
      blueServer.sayString("Dath.Davil: "+str);
       blueServer.stopServer();
   }catch(java.io.IOException ioException)
    {
      System.out.println(ioException);
    }
    catch(java.lang.Exception exception)
    {
      System.out.println(exception);
    }
}
}
