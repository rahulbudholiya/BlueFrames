import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;
import javax.microedition.io.*;
import javax.bluetooth.*;
import java.io.*;
import BlueFrame.*;
public class EchoClient extends MIDlet 
   implements CommandListener,BlueClientUser  {
    List main_list,dev_list;
    Command exit,ok;
    TextBox cmd;
    Display display;
    java.util.Vector devices,services;
    int currentDevice = 0;       //used as an indicator to the divce quried for
                                 //the echo server
    BlueClient blueClient = null;
    
    public void startApp()
     {

    main_list = new List("Echo Server",Choice.IMPLICIT);   //the main menu
    dev_list  = new List("Select Device",Choice.IMPLICIT); //the list of devices
    cmd       = new TextBox("Text to echo","",120,TextField.ANY);
    exit      = new Command("Exit",Command.EXIT,1);
    ok        = new Command("Send",Command.OK,1);
    display   = Display.getDisplay(this);

    main_list.addCommand(exit);
    main_list.setCommandListener(this);
    dev_list.addCommand(exit);
    dev_list.setCommandListener(this);
    cmd.addCommand(ok);
    cmd.addCommand(exit);
    cmd.setCommandListener(this);
    
    main_list.append("Find Echo Server",null);
    display.setCurrent(main_list);
        
    }
public void commandAction(Command com, Displayable dis) {
    if (com == exit){                                              //exit triggered from the main form
    try{
              blueClient.disconnectFromServer();
        }
        catch(java.lang.Exception e)
        {
          this.do_alert(e.toString(), Alert.FOREVER);
        }
        destroyApp(false);
        notifyDestroyed();
    }
    if (com == List.SELECT_COMMAND){
        if (dis == main_list){                                     //select triggered from the main from
            if (main_list.getSelectedIndex() >= 0){                //find devices
            try{
            do_alert("Searching for devices...", Alert.FOREVER);
            blueClient = BlueClient.getInstance(this);

            blueClient.populateDeviceList(true);
            devices = blueClient.getDeviceList();

                for(int i=0; i<devices.size(); i++)
                {
                 dev_list.append(((javax.bluetooth.RemoteDevice)devices.elementAt(i)).getFriendlyName(false),null);

                }
                display.setCurrent(dev_list);
                }catch(java.lang.Exception exception)
                {
                 do_alert("Error C"+exception.toString(),Alert.FOREVER);
                }

            }
        }
        if (dis == dev_list){                                      //select triggered from the device list
            try {
                blueClient.connectToServer(((javax.bluetooth.RemoteDevice)devices.elementAt(0)));
                display.setCurrent(cmd);                             //Show the textbox

            } catch (Exception e) {this.do_alert(e.toString(), 4000);}
    
        }
     }
    if(com == ok){                                                  //the user is sending a command
        try{
          blueClient.sayString("I am Connected"+'\n');
          String str = blueClient.receiveString();
          this.do_alert(str, Alert.FOREVER);

        } catch (Exception e) {this.do_alert(e.toString(), Alert.FOREVER);} 
    }
}


public void inquiryCompleted(int message){
}
    
public void serviceSearchCompleted(int message) {
}

public void do_alert(String msg,int time_out){
	    if (display.getCurrent() instanceof Alert ){
        ((Alert)display.getCurrent()).setString(msg);
        ((Alert)display.getCurrent()).setTimeout(time_out);
    }else{
        Alert alert = new Alert("Bluetooth");
        alert.setString(msg);
        alert.setTimeout(time_out);
        display.setCurrent(alert);
    }
}

public void pauseApp() {}

public void destroyApp(boolean unconditional) {}

}
