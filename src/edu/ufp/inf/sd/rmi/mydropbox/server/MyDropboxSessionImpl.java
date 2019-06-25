/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.ufp.inf.sd.rmi.mydropbox.server;

import edu.ufp.inf.sd.rmi.mydropbox.client.MyDropboxClientRI;
import static edu.ufp.inf.sd.rmi.mydropbox.server.MyDropboxImpl.PATH_CLIENTFOLDERONSERVER;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Set;

/**
 *
 * @author João Campelo e Tiago Costa
 */
public class MyDropboxSessionImpl implements MyDropboxSessionRI {

    protected MyDropboxImpl myDropbox;
    protected MyDropboxServer server;
    private User user;

    public MyDropboxSessionImpl(MyDropboxImpl myDropbox, User user, MyDropboxServer server) throws RemoteException {
        this.myDropbox = myDropbox;
        this.server = server;
        this.user = user;
        this.exportObject();
    }

    private void exportObject() throws RemoteException {
        UnicastRemoteObject.exportObject((Remote) this, 0);
    }

    /**
     *
     * @return
     */
    public User getUser() {
        return user;
    }

    /**
     *
     * @param user
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * Função usada para adicionar clientes a um groupo/pasta
     *
     * @param clientRI - Stub do cliente que se ligou ao servidor
     * @param groupName - Nome do grupo
     * @param session - Sessão a que um utilizador esta ligado
     * @throws RemoteException
     */
    @Override
    public void attachGroup(MyDropboxClientRI clientRI, String groupName, MyDropboxSessionRI session, String clientName) throws RemoteException {
        server.getGroup().get(groupName).getClients().put(clientName, clientRI);
        server.getGroup().get(groupName).getSessions().put(clientName, session);
    }

    /**
     * Função usada para desligar um cliente de um grupo quando este faz logout
     *
     * @param clientRI - Stub do cliente que se ligou ao servidor
     * @param groupName - Nome do grupo
     * @param session - Sessão a que um utilizador esta ligado
     * @throws RemoteException
     */
    @Override
    public void dettachGroup(String groupName, String username) throws RemoteException {
        server.getGroup().get(groupName).getClients().remove(username);
        server.getGroup().get(groupName).getSessions().remove(username);
    }

    /**
     * Função para espalhar uma açao para todos os utilizadores ligados a um
     * grupo
     *
     * @param opcao - variavel para defenir a ação a realizar, 1 significa que
     * vai fazer upload de um ficheiro, 2 significa que vai eliminar
     * @param groupName - nome de um grupo para espalhar a atualização
     * @param fileName - nome do ficheiro ao qual vai ser realizada a açao
     * @param username - nome de um utilizador
     * @throws RemoteException
     * @throws FileNotFoundException
     * @throws IOException
     */
    @Override
    public void setState(String opcao, String groupName, String fileName, String username) throws RemoteException, FileNotFoundException, IOException {
        String filePath;
        Group g = server.getGroup().get(groupName);

        Set<String> users = server.getUsers().keySet();
        for (String u : users) {
            if (u.compareTo(username) != 0) {
                filePath = server.getUsers().get(u).getPathFolder().get(groupName);
                g.getClients().get(u).update(opcao, g.getSessions().get(u), groupName, filePath + fileName, fileName);
            }
        }
    }

    /**
     *
     * @param myDropboxClientRI - Stub de um cliente ligado no servidor
     * @throws RemoteException
     */
    @Override
    public void logout() throws RemoteException {
        Set<String> groupNames = this.user.getPathFolder().keySet();
        for (String groupName : groupNames) {
            this.dettachGroup(groupName, this.user.getUname());
        }
        this.myDropbox.removeSession(this.user.getUname());
    }

    /**
     * Funçao para dar permição a um utilizador para ter acesso a um grupo
     *
     * @param username - utilizador a ser adicionado ao grupo
     * @param groupOwner - dono do grupo
     * @throws RemoteException
     */
    @Override
    public void shareGroup(String username, String groupOwner) throws RemoteException {
        User u = new User(username, "");
        server.getGroup().get(groupOwner).getUsers().add(u);
        myDropbox.saveGroups();
        server.getGroups().clear();
        server.getGroup().clear();
        server.loadGroups();
    }

    /**
     * Funçao para listar os ficheiros de um grupo ao qual um utilizador tem
     * acesso
     *
     * @param groupName - nome de um grupo ao qual o utilizador esta ligado
     * @param username - nome do utilizador
     * @param myDropboxClientRI - Stub para o client
     * @throws RemoteException
     */
    @Override
    public void listFiles(String groupName, String username, MyDropboxClientRI myDropboxClientRI) throws RemoteException {
        for (User groupUser : server.getGroup().get(groupName).getUsers()) {
            if (groupUser.getUname().compareTo(username) == 0) {
                File f = new File(PATH_CLIENTFOLDERONSERVER + groupName + "/");
                File arrayFiles[] = f.listFiles();
                if (arrayFiles.length > 0) {
                    for (File arrayFile : arrayFiles) {
                        myDropboxClientRI.sendMessage(arrayFile.getName());
                    }
                    return;
                } else {
                    myDropboxClientRI.sendMessage("Ainda não existem ficheiros nesta pasta!");
                    return;
                }
            }
        }
        myDropboxClientRI.sendMessage("Não tem permição para ver os ficheiros deste grupo!");
    }

    /**
     * Função para fazer download de um ficheiro do servidor para o client
     *
     * @param filename - nome de um ficheiro
     * @param myDropboxClientRI - Stub do client
     * @param groupName - nome do grupo/pasta onde o ficheiro se encontra
     * @param filePath - caminho para o ficheiro
     * @throws RemoteException
     * @throws FileNotFoundException
     * @throws IOException
     */
    @Override
    public void downloadFileServer(String filename, MyDropboxClientRI myDropboxClientRI, String groupName, String filePath) throws RemoteException, FileNotFoundException, IOException {
        Path inputPath = Paths.get(PATH_CLIENTFOLDERONSERVER + groupName + "/" + filename);
        String absoluteFilePath = inputPath.toAbsolutePath().toString();

        try (FileInputStream file = new FileInputStream(absoluteFilePath); DataInputStream dataIn = new DataInputStream(file)) {
            int count = file.available();
            byte[] bs = new byte[count];
            dataIn.read(bs);

            File f = new File(absoluteFilePath);
            myDropboxClientRI.downloadFileClient(bs, f.getName(), filePath);
        }
    }

    /**
     * Função para fazer upload de um ficheiro do cliente para o servidor
     *
     * @param data - conteudo de um ficheiro
     * @param filename - nome do ficheiro
     * @param groupName - nome do grupo/pasta onde o ficheiro se encontra
     * @throws RemoteException
     * @throws FileNotFoundException
     * @throws IOException
     */
    @Override
    public void uploadFileServer(byte[] data, String filename, String groupName) throws RemoteException, FileNotFoundException, IOException {
        try (FileOutputStream file = new FileOutputStream(PATH_CLIENTFOLDERONSERVER + groupName + "/" + filename); DataOutputStream dataOut = new DataOutputStream(file)) {
            dataOut.write(data);
        }
    }

    @Override
    public void deleteFile(String fileName, String groupName) throws RemoteException {
        File fileToBeDeleted = new File(PATH_CLIENTFOLDERONSERVER + groupName + "/" + fileName);
        if (fileToBeDeleted.exists()) {
            fileToBeDeleted.delete();
        }
    }

    @Override
    public void createFolderOnServer(String groupName, String fileName) throws RemoteException {
        File clientFolderOnServer = new File(PATH_CLIENTFOLDERONSERVER + groupName + "/" + fileName);
        clientFolderOnServer.mkdir();
    }
}
