/**
 *
 * @author rahul
 */

package BlueFrame;


public interface BlueServerUser
{

    public abstract void stringReceived(String s);

    public abstract void clientConnected();

    public abstract void clientConnectionClosed();
}