package edu.ufp.inf.sd.rmi.mydropbox.server;

import static edu.ufp.inf.sd.rmi.mydropbox.server.MyDropboxImpl.PATH_GROUPS;
import static edu.ufp.inf.sd.rmi.mydropbox.server.MyDropboxImpl.PATH_USERS;
import edu.ufp.inf.sd.rmi.util.rmisetup.SetupContextRMI;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 *
 * @author João Campelo e Tiago Costa
 */
public class MyDropboxServer {

    private SetupContextRMI contextRMI;
    private MyDropboxRI myRI;

    private ArrayList<Group> groups = new ArrayList<>();
    private HashMap<String, Group> group = new HashMap<>();
    private HashMap<String, User> users = new HashMap<>();

    public static void main(String[] args) {
        if (args != null && args.length < 3) {
            System.exit(-1);
        } else {
            MyDropboxServer srv = new MyDropboxServer(args);
            srv.rebindService();
            srv.loadGroups();
        }
    }

    public MyDropboxServer(String args[]) {
        try {
            String registryIP = args[0];
            String registryPort = args[1];
            String serviceName = args[2];
            contextRMI = new SetupContextRMI(this.getClass(), registryIP, registryPort, new String[]{serviceName});
        } catch (RemoteException e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, e);
        }
    }

    public ArrayList<Group> getGroups() {
        return groups;
    }

    public HashMap<String, Group> getGroup() {
        return group;
    }

    public HashMap<String, User> getUsers() {
        return users;
    }

    public void setUsers(HashMap<String, User> users) {
        this.users = users;
    }
    
    private void rebindService() {
        try {
            Registry registry = contextRMI.getRegistry();
            if (registry != null) {
                myRI = new MyDropboxImpl(this);
                String serviceUrl = contextRMI.getServicesUrl(0);
                registry.rebind(serviceUrl, myRI);
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.INFO, "registry not bound (check IPs). :(");
            }
        } catch (RemoteException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Função para carregar os grupos dos ficheiros para a memoria
     */
    public void loadGroups() {
        File f = new File(PATH_GROUPS);
        File arrayFiles[] = f.listFiles();
        for (int i = 0; i < arrayFiles.length; i++) {
            File file = arrayFiles[i];
            String[] parts = file.getName().split(Pattern.quote("."));
            Group g = new Group(parts[0]);
            groups.add(g);

            BufferedReader br = null;
            BufferedReader pw = null;

            try {
                br = new BufferedReader(new FileReader(new File(PATH_GROUPS + file.getName())));
                String groupUser;
                while ((groupUser = br.readLine()) != null) {
                    pw = new BufferedReader(new FileReader(new File(PATH_USERS + groupUser + ".txt")));
                    g.getUsers().add(new User(groupUser, pw.readLine()));
                    group.put(g.getName(), g);
                }
            } catch (FileNotFoundException ex) {
                Logger.getLogger(MyDropboxImpl.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(MyDropboxImpl.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    if (br != null) {
                        br.close();
                    }
                    if (pw != null) {
                        pw.close();
                    }
                } catch (IOException e) {
                    Logger.getLogger(MyDropboxImpl.class.getName()).log(Level.SEVERE, null, e);
                }
            }
        }
    }
    
    /**
     * função para listar os grupos existentes
     */
    public void listGroups() {
        for (int i = 0; i < getGroups().size(); i++) {
            Group g = getGroups().get(i);
            System.out.println(g.getName());
            for (int j = 0; j < g.getUsers().size(); j++) {
                System.out.println(g.getUsers().get(j).getUname());
            }
        }
    }
}
