package  BlueFrame;

public class BlueServer
{
private java.util.Vector deviceList =null;
private static ServerThread serverThread;
private static BlueServerUser blueServerUser = null;

private BlueServer(BlueServerUser blueServerUser)
{
this.blueServerUser= blueServerUser;
}

public static BlueServer getInstance(BlueServerUser blueServerUser)
{
 return new BlueServer(blueServerUser);
}

public static void startServer() throws java.io.IOException
{
 serverThread = new ServerThread(blueServerUser);
}

public static void stopServer() throws java.io.IOException
{
 serverThread.stopServerThread();
}

public boolean isConnected() throws java.io.IOException
{
  return serverThread.isConnected();
}

public java.util.Vector getRemotDevices(boolean alwaysAsk) throws java.io.IOException
{

  serverThread.populateDeviceList(alwaysAsk);
  return serverThread.getDevicesList();

}


public void sayString(String string) throws java.io.IOException
{
 serverThread.send(string);
}



}


class ServerThread extends Thread

{

private static javax.microedition.io.StreamConnectionNotifier streamConnectionNotifier = null;

private static javax.microedition.io.StreamConnection connection = null;

private String sentString = null;

private String receivedString =null;

private javax.bluetooth.LocalDevice localDevice = null;

private java.io.DataInputStream dataInputStream = null;

private java.io.DataOutputStream dataOutputStream = null;

private static java.util.Vector devicesList = null;

private static boolean connected = false;

private BlueServerUser blueServerUser = null;

private IOThreadServer  ioThreadServer =null;
public ServerThread(BlueServerUser blueServerUser)
{
super("BlueServerBeta");
this.blueServerUser = blueServerUser;
start();
}

public javax.microedition.io.StreamConnectionNotifier getSreamConnectionNotifier()
{
 return streamConnectionNotifier;
}

public void run()
{

System.out.println("BlueServerBeta");
connected = false;              

   try{

           javax.bluetooth.UUID uuid = new javax.bluetooth.UUID("27012f0c68af4fbf8dbe6bbaf7aa432a",false);
 
           String name = "BlueServer";                       //the name of the service
           String url  =  "btspp://localhost:" + uuid         //the service url
                                + ";name=" + name 
                                + ";authenticate=false;encrypt=false;";
    
            try {

            localDevice = javax.bluetooth.LocalDevice.getLocalDevice();



            localDevice.setDiscoverable(javax.bluetooth.DiscoveryAgent.GIAC);

            try{
             streamConnectionNotifier = (javax.microedition.io.StreamConnectionNotifier)javax.microedition.io.Connector.open(url);


             
            }catch(java.lang.Exception exception)
            {
                System.out.println("Exception in Connector.open"+exception);

                streamConnectionNotifier.close();
            }

            try{
            System.out.println("Waiting for incoming connection...");
            connection = streamConnectionNotifier.acceptAndOpen();

            }catch(java.lang.Exception exception)
            {
                exception.printStackTrace();
                connection.close();
                streamConnectionNotifier.close();
            }
            try{
            System.out.println("Client Connected..."+javax.bluetooth.RemoteDevice.getRemoteDevice(connection).getFriendlyName(false));
            }catch(java.lang.Exception exception)
            {
                System.out.println("Problem getting name");
            }
            System.out.println("Client Connected...");
            dataInputStream   = new java.io.DataInputStream(connection.openInputStream());
            dataOutputStream = new java.io.DataOutputStream(connection.openOutputStream());   
            connected = true;
            blueServerUser.clientConnected(); 
              try
                {
                    System.out.println("IOThread Section...");
                    ioThreadServer = new IOThreadServer(blueServerUser);
                }
                catch(Exception exception4)
                {
                    System.out.println("Serious Error: ");
                    exception4.printStackTrace();
                }
                ioThreadServer.setInputOutputStream(dataInputStream, dataOutputStream);
                ioThreadServer.startThread();                                                              
                  
        }
       catch (java.lang.Exception  e) {
            e.printStackTrace();
            System.out.println("Exception Occured: " + e.toString());
            try{
               connection.close();
               streamConnectionNotifier.close();
            
            }
            catch(java.lang.Exception exception)
            {
                
            }
        } }catch(java.lang.Exception exception)
        {
         System.out.println(exception);
        }
        connected = true;

}


public void stopServerThread() throws java.io.IOException
{
connected = false;
ioThreadServer.stopThread();
if(connection==null)
{
  streamConnectionNotifier.close();

}
else
{
dataOutputStream.flush();
dataInputStream.close();
dataOutputStream.close();
connection.close();
streamConnectionNotifier.close();
}

blueServerUser.clientConnectionClosed();
}

public void send(String string) throws java.io.IOException
{
send(string,'\n');
}

public void send(String string,char lineTerminator) throws java.io.IOException
{
   dataOutputStream.writeChars(string+lineTerminator);
   dataOutputStream.flush();
}




public boolean isConnected()throws java.io.IOException
{
   return connected;
}


 public static java.util.Vector getDevicesList()
 {
  return devicesList;
 }

 public static void populateDeviceList(boolean alwaysAsk) throws java.io.IOException
 {
   if(alwaysAsk)
   {
      devicesList = null;
   }

   if(devicesList==null)
   {
         devicesList = new java.util.Vector();
     
        final Object inquiryCompletedEvent = new Object();

        devicesList.clear();

        javax.bluetooth.DiscoveryListener listener = new javax.bluetooth.DiscoveryListener() {

            public void deviceDiscovered(javax.bluetooth.RemoteDevice btDevice, javax.bluetooth.DeviceClass cod) 
            {
                System.out.println("Device " + btDevice.getBluetoothAddress() + " found");

                devicesList.addElement(btDevice);
                try {
                    System.out.println("     name " + btDevice.getFriendlyName(false));
                } catch (java.io.IOException cantGetDeviceName) {System.out.println(cantGetDeviceName);
                }
            }

            public void inquiryCompleted(int discType) {

                synchronized(inquiryCompletedEvent){

                    inquiryCompletedEvent.notifyAll();

                }
            }

            public void serviceSearchCompleted(int transID, int respCode) {
            }

            public void servicesDiscovered(int transID, javax.bluetooth.ServiceRecord[] servRecord) {
            }
        };

        synchronized(inquiryCompletedEvent)  {

            boolean started = javax.bluetooth.LocalDevice.getLocalDevice().getDiscoveryAgent().startInquiry(javax.bluetooth.DiscoveryAgent.GIAC, listener);

            if (started) {
               
                try{
                inquiryCompletedEvent.wait();
                }catch(java.lang.Exception exception)
                {}
                System.out.println(devicesList.size() +  " device(s) found");
            }
        }
    
   }

 }



}


class IOThreadServer extends Thread
{
    private char seperator = '\n';
    private boolean threadStop = false;
    private java.io.DataInputStream in;
    private java.io.DataOutputStream out;
    private String errorMessage = "";
    private BlueServerUser blueServerUser = null;

    public String getErorrMessage()
    {
        return errorMessage;
    }

    public void startThread()
    {
        start();
    }

    public void setSeperator(char c)
    {
        seperator = c;
    }

    public void setInputOutputStream(java.io.DataInputStream datainputstream, java.io.DataOutputStream dataoutputstream)
    {
        out = dataoutputstream;
        in = datainputstream;
    }

    public IOThreadServer(BlueServerUser blueserverUser)
    {
        super("IOThread");
        this.blueServerUser = blueserverUser;
    }

    public void run()
    {
            

            String cmd = "";
    try{

            while(!threadStop)
            {

               char c;

               while (((c = in.readChar()) > 0) && (c!=seperator)  && !threadStop)
               {
                    if(c!=0)
                   cmd = cmd + c;
               }

               blueServerUser.stringReceived(cmd);
               cmd ="";
                
            }

       }catch(java.lang.Exception Exception)
       {
          System.out.println("Connection Closed.");
          blueServerUser.clientConnectionClosed();
       }

         
       

    }

    public void stopThread()
    {
        threadStop = true;
    }


    
}



