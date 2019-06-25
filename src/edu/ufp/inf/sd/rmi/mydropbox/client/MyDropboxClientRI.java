/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.ufp.inf.sd.rmi.mydropbox.client;

import edu.ufp.inf.sd.rmi.mydropbox.server.MyDropboxSessionRI;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *
 * @author João Campelo e Tiago Costa
 */
public interface MyDropboxClientRI extends Remote {

    public void sendMessage(String message) throws RemoteException;

    public void shareGroup(MyDropboxSessionRI myDropboxSessionRI, String groupOwner, String username) throws RemoteException;

    public void listFiles(MyDropboxSessionRI myDropboxSessionRI, String groupName, String username) throws RemoteException;

    public void update(String opcao, MyDropboxSessionRI myDropboxSessionRI, String groupName, String filePath, String fileName) throws RemoteException, IOException;

    public void watchFolder(String folderPath, MyDropboxClientRI myDropboxClientRI, MyDropboxSessionRI myDropboxSessionRI, String groupName, String username) throws RemoteException, IOException, InterruptedException;

    public void deleteFile(String filePath) throws RemoteException;

    public void downloadFileClient(byte[] data, String filename, String filePath) throws RemoteException, FileNotFoundException, IOException;

    public void uploadFileClient(String filePath, MyDropboxSessionRI myDropboxSessionRI, String groupName) throws RemoteException, FileNotFoundException, IOException;

    public void createFolderOnClient(String folderPath) throws RemoteException;

}
