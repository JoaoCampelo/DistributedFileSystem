/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.ufp.inf.sd.rmi.mydropbox.server;

import edu.ufp.inf.sd.rmi.mydropbox.client.MyDropboxClientRI;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author João Campelo e Tiago Costa
 */
public class Group {

    private String name;
    private ArrayList<User> users;
    private HashMap<String, MyDropboxClientRI> clients;
    private HashMap<String, MyDropboxSessionRI> sessions;

    /**
     *
     * @param name - nome do grupo
     */
    public Group(String name) {
        this.name = name;
        this.users = new ArrayList<>();
        this.clients = new HashMap<>();
        this.sessions = new HashMap<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<User> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<User> users) {
        this.users = users;
    }

    public HashMap<String, MyDropboxClientRI> getClients() {
        return clients;
    }

    public void setClients(HashMap<String, MyDropboxClientRI> clients) {
        this.clients = clients;
    }

    public HashMap<String, MyDropboxSessionRI> getSessions() {
        return sessions;
    }

    public void setSessions(HashMap<String, MyDropboxSessionRI> sessions) {
        this.sessions = sessions;
    }
}
