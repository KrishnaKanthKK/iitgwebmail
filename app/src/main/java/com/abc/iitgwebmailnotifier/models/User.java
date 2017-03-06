package com.abc.iitgwebmailnotifier.models;

/**
 * Created by aarkay0602 on 13/2/17.
 */


public class User {
    private String username;

    private String password;

    private String server;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }
}
