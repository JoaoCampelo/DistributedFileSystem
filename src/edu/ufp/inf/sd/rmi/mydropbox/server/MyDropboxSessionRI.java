/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.ufp.inf.sd.rmi.mydropbox.server;

import edu.ufp.inf.sd.rmi.mydropbox.client.MyDropboxClientRI;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *
 * @author João Campelo e Tiago Costa
 */
public interface MyDropboxSessionRI extends Remote {

    public void attachGroup(MyDropboxClientRI clientRI, String groupName, MyDropboxSessionRI session, String clientName) throws RemoteException;

    public void dettachGroup(String groupName, String username) throws RemoteException;

    public void setState(String opcao, String groupName, String fileName, String username) throws RemoteException, FileNotFoundException, IOException;

    public void logout() throws RemoteException;

    public void shareGroup(String username, String groupOwner) throws RemoteException;

    public void listFiles(String groupName, String username, MyDropboxClientRI myDropboxClientRI) throws RemoteException;

    public void downloadFileServer(String fileName, MyDropboxClientRI myDropboxClientRI, String groupName, String filePath) throws RemoteException, FileNotFoundException, IOException;

    public void uploadFileServer(byte[] data, String filename, String groupName) throws RemoteException, FileNotFoundException, IOException;

    public void deleteFile(String fileName, String groupName) throws RemoteException;

    public void createFolderOnServer(String groupName, String fileName) throws RemoteException;
}
