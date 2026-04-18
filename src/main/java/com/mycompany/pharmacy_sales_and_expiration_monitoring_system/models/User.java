package com.mycompany.pharmacy_sales_and_expiration_monitoring_system.models;

public class User {
    private int id;
    private String username;
    private String password;
    private String role;
    private String securityQuestion;
    private String securityAnswer;

    public User(int id, String username, String password, String role, String securityQuestion, String securityAnswer) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
        this.securityQuestion = securityQuestion;
        this.securityAnswer = securityAnswer;
    }

    public User(String username, String password, String role, String securityQuestion, String securityAnswer) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.securityQuestion = securityQuestion;
        this.securityAnswer = securityAnswer;
    }

    public User(int id, String username, String password, String role) {
        this(id, username, password, role, null, null);
    }

    public User(String username, String password, String role) {
        this(username, password, role, null, null);
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getSecurityQuestion() {
        return securityQuestion;
    }

    public void setSecurityQuestion(String securityQuestion) {
        this.securityQuestion = securityQuestion;
    }

    public String getSecurityAnswer() {
        return securityAnswer;
    }

    public void setSecurityAnswer(String securityAnswer) {
        this.securityAnswer = securityAnswer;
    }
}
