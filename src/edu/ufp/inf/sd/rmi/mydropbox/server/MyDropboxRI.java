package edu.ufp.inf.sd.rmi.mydropbox.server;

import edu.ufp.inf.sd.rmi.mydropbox.client.MyDropboxClientRI;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface MyDropboxRI extends Remote {

    public void register(MyDropboxClientRI client, String username, String password) throws RemoteException;

    public MyDropboxSessionRI login(MyDropboxClientRI client, String username, String password) throws RemoteException;
}
