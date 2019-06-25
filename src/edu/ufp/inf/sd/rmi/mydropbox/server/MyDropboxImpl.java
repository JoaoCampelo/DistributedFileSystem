package edu.ufp.inf.sd.rmi.mydropbox.server;

import edu.ufp.inf.sd.rmi.mydropbox.client.MyDropboxClientRI;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author João Campelo e Tiago Costa
 */
public class MyDropboxImpl extends UnicastRemoteObject implements MyDropboxRI {

    public static String PATH_USERS = "C:/data/users/";
    public static String PATH_GROUPS = "C:/data/groups/";
    public static String PATH_CLIENTFOLDERONSERVER = "C:/data/server/";

    private HashMap<String, MyDropboxSessionRI> sessions;
    private MyDropboxServer server;

    public MyDropboxImpl(MyDropboxServer server) throws RemoteException {
        this.sessions = new HashMap<>();
        this.server = server;
    }

    public HashMap<String, MyDropboxSessionRI> getSessions() {
        return sessions;
    }

    /**
     * Função para registar um utilizador
     *
     * @param client - Stub para o cliente
     * @param username - nome de utilizador
     * @param password - password
     * @throws RemoteException
     */
    @Override
    public void register(MyDropboxClientRI client, String username, String password) throws RemoteException {
        String user_path = PATH_USERS + username + ".txt";
        File f = new File(user_path);

        if (f.exists()) {
            client.sendMessage("Este utilizador já existe!");
            System.out.println("Server (MyDropBoxImpl) - register(): Registo falhado, o utilizador " + username + "já existe.");
            return;
        }

        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(new File(user_path)));
            bw.write(password);

            File clientFolderOnServer = new File(PATH_CLIENTFOLDERONSERVER + username + "/");
            clientFolderOnServer.mkdir();

            createGroups(username, password);
            server.getGroups().clear();
            server.getGroup().clear();
            server.loadGroups();

            client.sendMessage("Registado com sucesso!");
            System.out.println("Server (MyDropBoxImpl) - register(): Utilizador " + username + " registado com sucesso.");
        } catch (IOException e) {
            Logger.getLogger(MyDropboxImpl.class.getName()).log(Level.SEVERE, null, e);
        } finally {
            try {
                if (bw != null) {
                    bw.close();
                }
            } catch (IOException e) {
                Logger.getLogger(MyDropboxImpl.class.getName()).log(Level.SEVERE, null, e);
            }
        }
    }

    /**
     * Função para fazer login. nesta função tambem sao criadas pastas no
     * utilizador se este ainda nao as possuir, liga os utilizadores aos grupos
     * a que pertencem e faz a ligação para um threa para monitorizar pastas
     *
     * @param client - stub para um cliente
     * @param username - nome de utilizador
     * @param password - palavra pass
     * @return Sessao para um cliente
     * @throws RemoteException
     */
    @Override
    public MyDropboxSessionRI login(MyDropboxClientRI client, String username, String password) throws RemoteException {
        String user_path = PATH_USERS + username + ".txt";
        File f = new File(user_path);

        BufferedReader br = null;
        if (f.exists()) {
            try {
                br = new BufferedReader(new FileReader(new File(user_path)));
                String file_password = br.readLine();
                String folderPath;
                if (file_password.compareTo(password) == 0) {
                    User user = new User(username, password);
                    server.getUsers().put(password, user);
                    MyDropboxSessionRI session = new MyDropboxSessionImpl(this, user, this.server);
                    this.sessions.put(username, session);
                    client.sendMessage("Login efetuado com sucesso!!!");
                    System.out.println("Server (MyDropBoxImpl) - login(): " + username);

                    for (int i = 0; i < server.getGroups().size(); i++) {
                        Group g = server.getGroups().get(i);
                        for (int j = 0; j < g.getUsers().size(); j++) {
                            if (g.getUsers().get(j).getUname().compareTo(username) == 0) {
                                session.attachGroup(client, g.getName(), session, username);
                                if (g.getName().compareTo(username) == 0) {
                                    folderPath = "C:/data/" + g.getName() + "/";
                                    File clientFolder = new File(folderPath);
                                    if (!clientFolder.exists()) {
                                        clientFolder.mkdir();
                                    }
                                    server.getUsers().get(username).getPathFolder().put(g.getName(), folderPath);
                                } else {
                                    folderPath = "C:/data/" + username + "_" + g.getName() + "/";
                                    File clientFolder = new File(folderPath);
                                    if (!clientFolder.exists()) {
                                        clientFolder.mkdir();
                                    }
                                    server.getUsers().get(username).getPathFolder().put(g.getName(), folderPath);
                                }

                                File folderServer = new File(PATH_CLIENTFOLDERONSERVER + g.getName());
                                File arrayFiles[] = folderServer.listFiles();
                                for (File arrayFile : arrayFiles) {
                                    File fileUser = new File(folderPath + arrayFile.getName());
                                    if (!fileUser.exists()) {
                                        if (arrayFile.isFile()) {
                                            session.downloadFileServer(arrayFile.getName(), client, g.getName(), folderPath + arrayFile.getName());
                                        } else if (arrayFile.isDirectory()) {
                                            client.createFolderOnClient(folderPath + arrayFile.getName());
                                        }
                                    }
                                }

                                client.sendMessage("A pasta \"" + folderPath + "\" está a ser vigiada.");
                                client.watchFolder(folderPath, client, session, g.getName(), username);
                            }
                        }
                    }
                    return session;
                }
            } catch (FileNotFoundException ex) {
                Logger.getLogger(MyDropboxImpl.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException | InterruptedException ex) {
                Logger.getLogger(MyDropboxImpl.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    if (br != null) {
                        br.close();
                    }
                } catch (IOException ex) {
                    Logger.getLogger(MyDropboxImpl.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        client.sendMessage("Username ou password incorrectos!");
        System.out.println("Server (MyDropBoxImpl) - login(): Login falhado por " + username);
        return null;
    }

    /**
     * Função para remover uma sessao
     *
     * @param username - nome de utilizador
     * @param myDropboxSession - sessao a que o utilizador pertence
     */
    protected void removeSession(String username) {
        this.sessions.remove(username);
        this.server.getUsers().remove(username);
        System.out.println("Server (MyDropBoxImpl) - logout(): " + username);
    }

    /**
     * Função para criar um grupo quanto um utilizador faz um registo
     *
     * @param name - nome de um grupo / utilizador
     * @param password - palavra pass do utilizador
     * @throws RemoteException
     */
    public void createGroups(String name, String password) throws RemoteException {
        server.getGroups().add(new Group(name));
        joinGroup(name, password);
    }

    /**
     * Função para juntar o utilizador que criou o grupo ao proprio grupo
     *
     * @param name - nome de utilizador/grupo
     * @param password - palavra pass do utilizador
     * @throws RemoteException
     */
    public synchronized void joinGroup(String name, String password) throws RemoteException {
        server.getGroups().stream().filter((group) -> (group.getName().compareTo(name) == 0)).forEachOrdered((group) -> {
            group.getUsers().add(new User(name, password));
        });
        saveGroups();
    }

    /**
     * Função para gravar os grupos para um ficheiro de texto
     */
    public void saveGroups() {
        File f = new File(PATH_GROUPS);
        File arrayFiles[] = f.listFiles();
        String group_path;

        for (File arrayFile : arrayFiles) {
            arrayFile.delete();
        }

        for (int i = 0; i < server.getGroups().size(); i++) {
            String saveUsers = "";
            group_path = PATH_GROUPS + server.getGroups().get(i).getName() + ".txt";
            Group g = server.getGroups().get(i);
            for (int j = 0; j < server.getGroups().get(i).getUsers().size(); j++) {
                saveUsers = saveUsers + g.getUsers().get(j).getUname() + "\n";
            }

            BufferedWriter bw = null;
            try {
                bw = new BufferedWriter(new FileWriter(new File(group_path)));
                bw.write(saveUsers);
            } catch (IOException ex) {
                Logger.getLogger(MyDropboxSessionImpl.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    if (bw != null) {
                        bw.close();
                    }
                } catch (IOException e) {
                    Logger.getLogger(MyDropboxImpl.class.getName()).log(Level.SEVERE, null, e);
                }
            }
        }
    }
}
