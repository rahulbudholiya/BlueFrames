// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3)

package BlueFrame;


public interface BlueServerUser
{

    public abstract void stringReceived(String s);

    public abstract void clientConnected();

    public abstract void clientConnectionClosed();
}