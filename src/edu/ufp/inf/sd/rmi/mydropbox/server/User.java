/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.ufp.inf.sd.rmi.mydropbox.server;

import java.util.HashMap;

/**
 * Class para criar um utilizador
 * @author João Campelo e Tiago Costa
 */
public class User {

    private String uname;
    private String pword;
    private HashMap<String, String> pathFolder = new HashMap<>();

    /**
     *
     * @param uname - nome de utilizador
     * @param pword - password
     */
    public User(String uname, String pword) {
        this.uname = uname;
        this.pword = pword;
    }

    @Override
    public String toString() {
        return "User{" + "uname=" + uname + ", pword=" + pword + '}';
    }

    /**
     * @return the uname
     */
    public String getUname() {
        return uname;
    }

    /**
     * @param uname the uname to set
     */
    public void setUname(String uname) {
        this.uname = uname;
    }

    /**
     * @return the pword
     */
    public String getPword() {
        return pword;
    }

    /**
     * @param pword the pword to set
     */
    public void setPword(String pword) {
        this.pword = pword;
    }

    /**
     *
     * @return
     */
    public HashMap<String, String> getPathFolder() {
        return pathFolder;
    }

    /**
     *
     * @param pathFolder
     */
    public void setPathFolder(HashMap<String, String> pathFolder) {
        this.pathFolder = pathFolder;
    }
}
