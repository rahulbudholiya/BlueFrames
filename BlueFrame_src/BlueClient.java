
package BlueFrame;

/**
 *
 * @author rahul
 */
public class BlueClient
{
private BlueClientUser blueClientUser = null;
private static Client client = null;
private java.util.Vector deviceList;
public static final int INQUIRY_ERROR =1;
public static final int INQUIRY_COMPLETED =2;
public static final int INQUIRY_TERMINATED =3;

public static final int SERVICE_SEARCH_COMPLETED =101;
public static final int SERVICE_SEARCH_TERMINATED =703;
public static final int SERVICE_SEARCH_NO_RECORDS = 908;
public static final int SERVICE_SEARCH_ERROR =506;
public static final int SERVICE_SEARCH_DEVICE_NOT_REACHABLE =593;
public static final int SERVICE_SEARCH_TRANSFER_SERVICE_NOT_FOUND = 800;
public static char seperator ='\n';
public void setSeperator(char seperator)
{
this.seperator = seperator;
}
public String getErrorMessage()
{
 return client.getErrorMessage();
}
private BlueClient(BlueClientUser blueClientUser)
{
 this.blueClientUser = blueClientUser;
}

public void inquiryCompleted(int message)
{
if(INQUIRY_COMPLETED == message)
{
 deviceList = client.getDeviceList();
  blueClientUser.inquiryCompleted(INQUIRY_COMPLETED);
}
if(INQUIRY_ERROR == message)
{
  blueClientUser.inquiryCompleted(INQUIRY_ERROR);
}
if(INQUIRY_TERMINATED == message)
{
  blueClientUser.inquiryCompleted(INQUIRY_TERMINATED);
}
}



public void serviceSearchCompleted(int message)
{
if(SERVICE_SEARCH_COMPLETED == message)
{
blueClientUser.serviceSearchCompleted(SERVICE_SEARCH_COMPLETED);
}
if(SERVICE_SEARCH_TERMINATED == message)
{
blueClientUser.serviceSearchCompleted(SERVICE_SEARCH_TERMINATED);
}
if(SERVICE_SEARCH_NO_RECORDS == message)
{
blueClientUser.serviceSearchCompleted(SERVICE_SEARCH_NO_RECORDS);
}
if(SERVICE_SEARCH_ERROR == message)
{
blueClientUser.serviceSearchCompleted(SERVICE_SEARCH_ERROR);
}
if(SERVICE_SEARCH_DEVICE_NOT_REACHABLE  == message)
{
blueClientUser.serviceSearchCompleted(SERVICE_SEARCH_DEVICE_NOT_REACHABLE);
}

if(SERVICE_SEARCH_TRANSFER_SERVICE_NOT_FOUND == message)
{
blueClientUser.serviceSearchCompleted(SERVICE_SEARCH_TRANSFER_SERVICE_NOT_FOUND);
}

if(SERVICE_SEARCH_TERMINATED == message)
{
 blueClientUser.serviceSearchCompleted(SERVICE_SEARCH_TERMINATED);
}


}


public void populateDeviceList(boolean alwaysDiscover) throws java.io.IOException
{
 if(alwaysDiscover)
  {
   deviceList = null;
  }

 if(deviceList == null)
  {
    deviceList = new java.util.Vector();
    client.findDevice();
    while(client.isDeviceDiscoveryRunning());
  }

  
}
public java.util.Vector getDeviceList()
{
 deviceList = client.getDeviceList();
 return deviceList;

}



public static BlueClient getInstance(BlueClientUser blueClientUser)
{
 BlueClient bl = new BlueClient(blueClientUser);

 if(client == null)
 {
  client = new Client(bl);
  client.setBlueClientUser(blueClientUser);
 }
 return bl;
}

public void connectToServer(javax.bluetooth.RemoteDevice device) throws java.io.IOException
{
client.findServices(device);

while(client.isServiceSearchRunning());

client.openConnection();
}


public void disconnectFromServer() throws java.io.IOException
{
client.closeConnection();
}


public void sayString(String string) throws java.io.IOException
{
client.sendString(string+'\n');
}

/*public String receiveString() throws java.io.IOException
{
  client.setSeperator(seperator);
  return client.getString();
}*/

}


//---------------------Client Code-----------------------


class Client implements javax.bluetooth.DiscoveryListener
{

 private javax.bluetooth.LocalDevice localDevice =null;
 private boolean deviceDiscoveryRunning = false;
 private boolean serviceSearchRunning =false;
 private java.util.Vector devicesList = null;
 private BlueClient blueClient= null;
 private java.util.Vector services = null;
 private javax.bluetooth.UUID[] uuid = null;
 private int currentDevice = 0;
 private java.util.Vector selectedDevices = null;
 private java.io.DataInputStream dataInputStream =null;
 private java.io.DataOutputStream dataOutputStream = null;
 private javax.microedition.io.StreamConnection connection = null;
 private char seperator = '\n';
 private BlueClientUser blueClientUser = null;
 private IOThread ioThread;

    public String getErrorMessage()
    {
        return ioThread.getErrorMessage();
    }

    public void setBlueClientUser(BlueClientUser blueClientUser)
    {
        this.blueClientUser = blueClientUser;
    }


 public boolean isDeviceDiscoveryRunning()
 {
     return this.deviceDiscoveryRunning;
 }
 public boolean isServiceSearchRunning()
 {
     return this.serviceSearchRunning;
 }

 public void setSeperator(char seperator)
 {
  this.seperator = seperator;
 }

 public void setCurrentDevice(int currentDevice)
 {
  this.currentDevice = currentDevice;
 }

 public java.util.Vector getDeviceList()
 {
  return devicesList;
 }

public Client(BlueClient blueClient)
{
 this.blueClient = blueClient;
}

public void findDevice() throws java.io.IOException
{
  this.deviceDiscoveryRunning =true;
  localDevice = javax.bluetooth.LocalDevice.getLocalDevice();
  javax.bluetooth.DiscoveryAgent discoveryAgent = localDevice.getDiscoveryAgent();
  discoveryAgent.startInquiry(javax.bluetooth.DiscoveryAgent.GIAC,this);
}


public void findServices(javax.bluetooth.RemoteDevice remoteDevice) throws java.io.IOException
{
 this.serviceSearchRunning =true;
 uuid = new javax.bluetooth.UUID[1];
 uuid[0] = new javax.bluetooth.UUID("27012f0c68af4fbf8dbe6bbaf7aa432a",false);
 localDevice = javax.bluetooth.LocalDevice.getLocalDevice();
 javax.bluetooth.DiscoveryAgent discoveryAgent  = localDevice.getDiscoveryAgent();
 discoveryAgent.searchServices(null,uuid,remoteDevice,this);
}

public void deviceDiscovered(javax.bluetooth.RemoteDevice remoteDevice,javax.bluetooth.DeviceClass deviceClass)
{
if(devicesList == null)
{
 devicesList = new java.util.Vector();
}
 devicesList.addElement(remoteDevice);
}

public void servicesDiscovered(int transID,javax.bluetooth.ServiceRecord[] serviceRecords)
{

if(services == null)
{
 services = new java.util.Vector();
}

if(selectedDevices == null)
{
 selectedDevices = new java.util.Vector();
}

for(int i=0; i<serviceRecords.length; i++)
{
 services.addElement(serviceRecords[i]);
}
try{
 selectedDevices.addElement(((javax.bluetooth.RemoteDevice)devicesList.elementAt(currentDevice)).getFriendlyName(false));
 }catch(java.io.IOException ioException)
 {
 ioException.printStackTrace();
 }
}

public void serviceSearchCompleted(int tranID,int respCode)
{   

switch(respCode)
{

 case javax.bluetooth.DiscoveryListener.SERVICE_SEARCH_COMPLETED:
 if(services.size() > 0)
 {
  blueClient.serviceSearchCompleted(101);
 }
 else
 {
  blueClient.serviceSearchCompleted(800);
 }

 break;
 case javax.bluetooth.DiscoveryListener.SERVICE_SEARCH_DEVICE_NOT_REACHABLE:
 blueClient.serviceSearchCompleted(593);
 break;
 case javax.bluetooth.DiscoveryListener.SERVICE_SEARCH_ERROR:
 blueClient.serviceSearchCompleted(506);
 break;
 case javax.bluetooth.DiscoveryListener.SERVICE_SEARCH_NO_RECORDS:
 blueClient.serviceSearchCompleted(908);
 break;
 case javax.bluetooth.DiscoveryListener.SERVICE_SEARCH_TERMINATED:
 blueClient.serviceSearchCompleted(703);
 break;

}
this.serviceSearchRunning = false;
}


public void inquiryCompleted(int param)
{
 switch(param)
 {
  case javax.bluetooth.DiscoveryListener.INQUIRY_COMPLETED:
  blueClient.inquiryCompleted(1);
  break;
  case javax.bluetooth.DiscoveryListener.INQUIRY_ERROR:
  blueClient.inquiryCompleted(2);
  break;
  case javax.bluetooth.DiscoveryListener.INQUIRY_TERMINATED:
  blueClient.inquiryCompleted(3);
  break;
 }
 this.deviceDiscoveryRunning =false;
}

public void openConnection() throws java.io.IOException
{
 javax.bluetooth.ServiceRecord serviceRecord =(javax.bluetooth.ServiceRecord) services.elementAt(0);
 String url = serviceRecord.getConnectionURL(javax.bluetooth.ServiceRecord.NOAUTHENTICATE_NOENCRYPT,false);
 connection = (javax.microedition.io.StreamConnection)javax.microedition.io.Connector.open(url);
 dataInputStream = new java.io.DataInputStream(connection.openDataInputStream());
 dataOutputStream = new java.io.DataOutputStream(connection.openDataOutputStream());
 ioThread = new IOThread(blueClientUser);
 ioThread.setInputOutputStream(dataInputStream, dataOutputStream);
 ioThread.startThread();
// throw new java.io.IOException("Erorr in THread");
}

public void sendString(String string) throws java.io.IOException
{
dataOutputStream.writeChars(string);
dataOutputStream.flush();
}

/*public String getString() throws java.io.IOException
{
String retValue  ="";
char c=' ';

while(((c= dataInputStream.readChar())>0) && (c!=seperator))
{
  retValue += c;
}
return retValue;
}*/


public void closeConnection() throws java.io.IOException
{
 ioThread.stopThread();
 dataOutputStream.close();
 dataInputStream.close();
 connection.close();

}

}

class IOThread extends java.lang.Thread
{
 
    private char seperator ='\n';
    private boolean threadStop = false;
    private java.io.DataInputStream in = null;
    private java.io.DataOutputStream out = null;
    private String errorMessage = "";
    private BlueClientUser blueClientUser= null;
    public void setBlueClientUser(BlueClientUser blueClientUser)
    {
     this.blueClientUser = blueClientUser;
    }

    public String getErrorMessage()
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

    public IOThread(BlueClientUser blueclientuser)
    {
        super("IOThread");
        blueClientUser = blueclientuser;
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
                    {
                     cmd = cmd + c;
                    }
               }
               if(!threadStop)
               {
                 blueClientUser.stringReceived(cmd);

               }
               cmd = "";
                
            }

       }catch(java.io.IOException ioException)
       {
          ioException.printStackTrace();
       }
                
    }

    public void stopThread()
    {
        threadStop = true;
    }
   
}

