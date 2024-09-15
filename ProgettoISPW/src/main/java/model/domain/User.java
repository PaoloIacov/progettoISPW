package model.domain;

import java.io.Serializable;

public class User implements Serializable {

    private String username;
    private String password;
    private String name;
    private String surname;
    private int role; // 1 = Employee, 2 = ProjectManager, 3 = Admin

    public User() {}

    public User(String username, String password, String name, String surname, int role) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.surname = surname;
        this.role = role;
    }

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

}
