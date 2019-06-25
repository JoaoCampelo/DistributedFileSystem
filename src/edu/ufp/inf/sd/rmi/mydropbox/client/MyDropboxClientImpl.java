/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.ufp.inf.sd.rmi.mydropbox.client;

import edu.ufp.inf.sd.rmi.mydropbox.server.MyDropboxRI;
import edu.ufp.inf.sd.rmi.mydropbox.server.MyDropboxSessionRI;
import edu.ufp.inf.sd.rmi.mydropbox.server.WatchServiceThread;
import edu.ufp.inf.sd.rmi.util.threading.ThreadPool;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import java.nio.file.WatchService;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author João Campelo e Tiago Costa
 */
public class MyDropboxClientImpl implements MyDropboxClientRI {

    private MyDropboxClient myDropboxClient;
    private MyDropboxRI myRI;
    private String user;

    private ThreadPool tp;

    MyDropboxClientImpl(MyDropboxClient myDropboxClient, MyDropboxRI myRI) {
        this.myDropboxClient = myDropboxClient;
        this.myRI = myRI;
        this.tp = new ThreadPool(100);
        exportObjectMethod();
    }

    private void exportObjectMethod() {
        try {
            UnicastRemoteObject.exportObject(this, 0);
        } catch (RemoteException e) {
            System.out.println("Client (MyDropboxClientImpl): " + e.getMessage());
        }
    }

    public MyDropboxClient getMyDropboxClient() {
        return myDropboxClient;
    }

    public void setMyDropboxClient(MyDropboxClient myDropboxClient) {
        this.myDropboxClient = myDropboxClient;
    }

    public MyDropboxRI getMyRI() {
        return myRI;
    }

    public void setMyRI(MyDropboxRI myRI) {
        this.myRI = myRI;
    }

    public void setUser(String user) {
        this.user = user;
    }

    /**
     * Função que chama a funçao de registar utilizadores
     *
     * @param username - nome de utilizador
     * @param password - palavra pass
     * @throws RemoteException
     */
    public void register(String username, String password) throws RemoteException {
        this.myRI.register(this, username, password);
    }

    /**
     * Função que chama a funçao de fazer login
     *
     * @param username - nome de utilizador
     * @param password - palavra pass
     * @return retorna uma sessao
     * @throws RemoteException
     */
    public MyDropboxSessionRI login(String username, String password) throws RemoteException {
        MyDropboxSessionRI myDropboxSessionRI = this.myRI.login(this, username, password);
        return myDropboxSessionRI;
    }

    /**
     * Funçao para chamar a função de fazer logout
     *
     * @param myDropboxSessionRI - stub para um cliente
     */
    public void logout(MyDropboxSessionRI myDropboxSessionRI) {
        try {
            myDropboxSessionRI.logout();
        } catch (RemoteException ex) {
            Logger.getLogger(MyDropboxClientImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Função usada pelo servidor para enviar mensagens aos utilizadores
     *
     * @param message - mensagem enviada pelo servidor
     * @throws RemoteException
     */
    @Override
    public void sendMessage(String message) throws RemoteException {
        System.out.println("Client (MyDropboxClient) - sendMessage: " + message);
    }

    /**
     * Funçao que chama a função para adicionar utilizadores a um grupo
     *
     * @param myDropboxSessionRI - stub para uma sessao de um cliente
     * @param groupOwner - nome do dono de um grupo
     * @param username - nome do utilizador a ser adicionado ao grupo
     * @throws RemoteException
     */
    @Override
    public void shareGroup(MyDropboxSessionRI myDropboxSessionRI, String groupOwner, String username) throws RemoteException {
        myDropboxSessionRI.shareGroup(username, groupOwner);
    }

    /**
     * Função que pede ao servidor para listar os ficheiros de um grupo ao qual
     * o utilizador tem acesso
     *
     * @param myDropboxSessionRI - stub para uma sessao
     * @param groupName - nome de um grupo
     * @param username - nome de um utilizador
     * @throws RemoteException
     */
    @Override
    public void listFiles(MyDropboxSessionRI myDropboxSessionRI, String groupName, String username) throws RemoteException {
        myDropboxSessionRI.listFiles(groupName, username, this);
    }

    /**
     * Função para executar açoes nos grupos de um utilizador, aqui decidimos se
     * um ficheiro vai ser enviado para o servidor ou eliminado
     *
     * @param opcao - opção para saber qual a açao a executar
     * @param myDropboxSessionRI - stub para uma sessao
     * @param groupName - nome de um grupo
     * @param filePath - caminho para um ficheiro
     * @param fileName - nome do ficheiro a ser manipulado
     * @param username - nome de utilizador
     * @throws RemoteException
     * @throws IOException
     */
    @Override
    public void update(String opcao, MyDropboxSessionRI myDropboxSessionRI, String groupName, String filePath, String fileName) throws RemoteException, IOException {
        switch (opcao) {
            case "1":
                myDropboxSessionRI.downloadFileServer(fileName, this, groupName, filePath);
                break;
            case "2":
                this.deleteFile(filePath);
                break;
            case "3":
                this.createFolderOnClient(filePath);
                break;
        }
    }

    /**
     * função onde vai ser invocada uma thread para vigiar uma pasta
     *
     * @param folderPath - caminho para uma pasta
     * @param myDropboxClientRI - stub para um cliente
     * @param myDropboxSessionRI - stub para uma sessao
     * @param groupName - nome de um grupo/pasta
     * @param username - nome de um utilizador
     * @throws RemoteException
     * @throws IOException
     * @throws InterruptedException
     */
    @Override
    public void watchFolder(String folderPath, MyDropboxClientRI myDropboxClientRI, MyDropboxSessionRI myDropboxSessionRI, String groupName, String username) throws RemoteException, IOException, InterruptedException {
        //folder to watch.
        Path folderPathToWatchForUpdate = Paths.get(folderPath);

        if (folderPathToWatchForUpdate != null) {
            //create watchservice object
            WatchService watchService = folderPathToWatchForUpdate.getFileSystem().newWatchService();
            //start thread by passing the watchservice.
            WatchServiceThread watchServiceHandlerThread = new WatchServiceThread(watchService, folderPath, myDropboxClientRI, myDropboxSessionRI, groupName, username);
            Thread watcherThread = new Thread(watchServiceHandlerThread);
            this.tp.execute(watcherThread);

            //register a folder to watch and the events associated.
            folderPathToWatchForUpdate.register(watchService, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
            watcherThread.join();
        }
    }

    /**
     * Função que elimina um ficheiro na pasta do lado do cliente
     *
     * @param filePath - caminho para um ficheiro
     * @param myDropboxSessionRI - stub para uma sessao
     * @param groupName - nome de um grupo
     * @throws RemoteException
     */
    @Override
    public void deleteFile(String filePath) throws RemoteException {
        File fileToBeDeleted = new File(filePath);
        if (fileToBeDeleted.exists()) {
            fileToBeDeleted.delete();
        }
    }

    /**
     * Função para fazer o download de um ficheiro do servidor para o cliente
     *
     * @param data - conteudo de um ficheiro
     * @param filename - nome do ficheiro
     * @param groupName - nome do grupo
     * @param filePath - caminho para um ficheiro
     * @throws RemoteException
     * @throws FileNotFoundException
     * @throws IOException
     */
    @Override
    public void downloadFileClient(byte[] data, String filename, String filePath) throws RemoteException, FileNotFoundException, IOException {
        try (FileOutputStream file = new FileOutputStream(filePath); DataOutputStream dataOut = new DataOutputStream(file)) {
            dataOut.write(data);
        }
    }

    /**
     * função para fazer upload de um ficheiro do cliente para o servidor
     *
     * @param filePath - caminho para um ficheiro
     * @param myDropboxSessionRI - stub para uma sessao
     * @param groupName - nome de um grupo
     * @throws RemoteException
     * @throws FileNotFoundException
     * @throws IOException
     */
    @Override
    public void uploadFileClient(String filePath, MyDropboxSessionRI myDropboxSessionRI, String groupName) throws RemoteException, FileNotFoundException, IOException {
        Path inputPath = Paths.get(filePath);
        String absoluteFilePath = inputPath.toAbsolutePath().toString();

        try (FileInputStream file = new FileInputStream(absoluteFilePath); DataInputStream dataIn = new DataInputStream(file)) {
            int count = file.available();
            byte[] bs = new byte[count];
            dataIn.read(bs);

            File f = new File(absoluteFilePath);
            myDropboxSessionRI.uploadFileServer(bs, f.getName(), groupName);
        }
    }

    @Override
    public void createFolderOnClient(String folderPath) throws RemoteException {
        File clientFolderOnClient = new File(folderPath);
        if (!clientFolderOnClient.exists()) {
            clientFolderOnClient.mkdir();
        }
    }
}
