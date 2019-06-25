/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.ufp.inf.sd.rmi.mydropbox.server;

import edu.ufp.inf.sd.rmi.mydropbox.client.MyDropboxClientRI;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author João Campelo e Tiago Costa
 */
public class WatchServiceThread implements Runnable {

    private WatchService watchService;
    private String folderPath;
    private String groupName;
    private MyDropboxClientRI myDropboxClientRI;
    private MyDropboxSessionRI myDropboxSessionRI;
    private String username;

    public WatchServiceThread(WatchService watchService, String folderPath, MyDropboxClientRI myDropboxClientRI, MyDropboxSessionRI myDropboxSessionRI, String groupName, String username) {
        this.watchService = watchService;
        this.folderPath = folderPath;
        this.groupName = groupName;
        this.myDropboxClientRI = myDropboxClientRI;
        this.myDropboxSessionRI = myDropboxSessionRI;
        this.username = username;
    }

    /**
     * Função para executar o thread que vai estar a escuta nas pastas as quais
     * o cliente esta ligado
     */
    @Override
    public void run() {
        while (true) {
            WatchKey watchKey = null;
            try {
                watchKey = watchService.take();
            } catch (InterruptedException e) {
                System.out.println("java.lang.InterruptedException while getting the key" + e.getMessage());
            }
            // perform relevant action based on the event that is occurred.
            if (watchKey != null) {
                for (WatchEvent event : watchKey.pollEvents()) {
                    try {
                        handleEvent(event);
                    } catch (IOException e) {
                        WatchEvent<Path> pathWatchEvent = (WatchEvent<Path>) event;
                        Path filename = pathWatchEvent.context();
                        File clientFolderOnServer = new File(folderPath + filename.toString() + "/");
                        if (clientFolderOnServer.isDirectory()) {
                            try {
                                myDropboxSessionRI.createFolderOnServer(groupName, filename.toString());
                                myDropboxSessionRI.setState("3", groupName, filename.toString(), username);
                            } catch (RemoteException ex) {
                                Logger.getLogger(WatchServiceThread.class.getName()).log(Level.SEVERE, null, ex);
                            } catch (IOException ex) {
                                Logger.getLogger(WatchServiceThread.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }

                        try {
                            Thread.sleep(1000);
                            myDropboxClientRI.watchFolder(folderPath, myDropboxClientRI, myDropboxSessionRI, groupName, username);
                        } catch (InterruptedException | IOException ex) {
                            Logger.getLogger(WatchServiceThread.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }
        }
    }

    private synchronized void handleEvent(WatchEvent eventType) throws IOException {
        //get event name.
        String event = eventType.kind().name();
        //downcasting event type to get the context holding the filename.
        WatchEvent<Path> pathWatchEvent = (WatchEvent<Path>) eventType;
        Path filename = pathWatchEvent.context();
        if (event.equals(ENTRY_MODIFY.name())) {
            myDropboxClientRI.uploadFileClient(folderPath + filename.toString(), myDropboxSessionRI, groupName);
            myDropboxSessionRI.setState("1", groupName, filename.getFileName().toString(), username);
        } else if (event.equals(ENTRY_DELETE.name())) {
            myDropboxSessionRI.deleteFile(filename.getFileName().toString(), groupName);
            myDropboxSessionRI.setState("2", groupName, filename.getFileName().toString(), username);
        } else if (event.equals(ENTRY_CREATE.name())) {
            myDropboxClientRI.uploadFileClient(folderPath + filename.toString(), myDropboxSessionRI, groupName);
            myDropboxSessionRI.setState("1", groupName, filename.getFileName().toString(), username);
        }

        try {
            Thread.sleep(1000);
            myDropboxClientRI.watchFolder(folderPath, myDropboxClientRI, myDropboxSessionRI, groupName, username);
        } catch (RemoteException | InterruptedException ex) {
            Logger.getLogger(WatchServiceThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
