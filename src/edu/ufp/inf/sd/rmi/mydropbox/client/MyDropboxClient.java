package edu.ufp.inf.sd.rmi.mydropbox.client;

import edu.ufp.inf.sd.rmi.mydropbox.server.MyDropboxRI;
import edu.ufp.inf.sd.rmi.util.rmisetup.SetupContextRMI;

import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import edu.ufp.inf.sd.rmi.mydropbox.server.MyDropboxSessionRI;

public class MyDropboxClient {

    private SetupContextRMI contextRMI;
    private MyDropboxRI myRI;
    private MyDropboxSessionRI dropboxSessionRI;
    private MyDropboxClientImpl observer = null;

    private String user;

    public static void main(String[] args) {
        if (args != null && args.length < 2) {
            System.exit(-1);
        } else {
            MyDropboxClient clt = new MyDropboxClient(args);
            clt.lookupService();
            clt.initObserver();
            clt.playService();
        }
    }

    public MyDropboxClient(String[] args) {
        try {
            String registryIP = args[0];
            String registryPort = args[1];
            String serviceName = args[2];
            contextRMI = new SetupContextRMI(this.getClass(), registryIP, registryPort, new String[]{serviceName});
        } catch (RemoteException e) {
            Logger.getLogger(MyDropboxClient.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    private Remote lookupService() {
        try {
            Registry registry = contextRMI.getRegistry();
            if (registry != null) {
                String serviceUrl = contextRMI.getServicesUrl(0);
                myRI = (MyDropboxRI) registry.lookup(serviceUrl);
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.INFO, "registry not bound (check IPs). :(");
            }
        } catch (RemoteException | NotBoundException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }
        return myRI;
    }

    /**
     * Funçao para inicializar um observer
     */
    private void initObserver() {
        try {
            observer = new MyDropboxClientImpl(this, myRI);
        } catch (Exception e) {
            Logger.getLogger(MyDropboxClientImpl.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    /**
     * Funçao para mostrar um meno de login/registo
     */
    private void playService() {

        Scanner sca = new Scanner(System.in);
        String opcaoEntrada = null, username, password;
        do {
            try {
                System.out.println("\t\t -----> MyDropbox <-----\n");
                System.out.println(" [1] -> LOGIN\n");
                System.out.println(" [2] -> REGISTAR\n");
                System.out.println(" [S] -> SAIR\n");
                System.out.print("OPÇÃO: ");
                opcaoEntrada = sca.nextLine();
                switch (opcaoEntrada) {
                    case "1":
                        System.out.print("Username: ");
                        username = sca.nextLine();
                        System.out.print("Password: ");
                        password = sca.nextLine();

                        dropboxSessionRI = observer.login(username, password);
                        if (dropboxSessionRI != null) {
                            this.user = username;
                            observer.setUser(user);
                            menuLogado();
                        }
                        break;
                    case "2":
                        System.out.print("Username: ");
                        username = sca.nextLine();
                        System.out.print("Password: ");
                        password = sca.nextLine();
                        observer.register(username, password);
                        break;
                    case "s":
                    case "S":
                        System.exit(0);
                        break;
                    default:
                        System.out.println("Opção Errada!!!\n");
                }
            } catch (RemoteException ex) {
                Logger.getLogger(MyDropboxClient.class.getName()).log(Level.SEVERE, null, ex);
            }
        } while (!"s".equals(opcaoEntrada) && !"S".equals(opcaoEntrada));
    }

    /**
     * Funçao para mostrar um meno depois de fazermos login
     */
    private void menuLogado() {
        Scanner sca = new Scanner(System.in);
        String opcao = null, username, groupName;
        do {
            try {
                System.out.println("\t\t -----> MyDropbox <-----\n");
                System.out.println(" [1] -> PARTILHAR GRUPO\n");
                System.out.println(" [2] -> LISTAR FICHEIROS\n");
                System.out.println(" [S] -> SAIR\n");
                System.out.print("OPÇÃO: ");
                opcao = sca.nextLine();
                switch (opcao) {
                    case "1":
                        
                        System.out.print("Nome do utilizador que vai ter acesso ao grupo: ");
                        username = sca.nextLine();
                        observer.shareGroup(dropboxSessionRI, this.user, username);
                        break;
                    case "2":
                        System.out.print("Nome do grupo que pretende listar os ficheiros (Tem de pertencer ao grupo): ");
                        groupName = sca.nextLine();
                        observer.listFiles(dropboxSessionRI, groupName, user);
                        break;
                    case "s":
                    case "S":
                        observer.logout(dropboxSessionRI);
                        break;
                    default:
                        System.out.println("Opção Errada!!!\n");
                }
            } catch (RemoteException ex) {
                Logger.getLogger(MyDropboxClient.class.getName()).log(Level.SEVERE, null, ex);
            }
        } while (!"s".equals(opcao) && !"S".equals(opcao));
    }
}
