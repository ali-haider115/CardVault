package com.example.cardvault.models;

import org.json.JSONException;
import org.json.JSONObject;

public class UserModel {
    private String username;
    private String email;
    private String passwordHash;
    private String fullName;
    private String securityQuestion;
    private String securityAnswerHash;

    public UserModel() {}

    public UserModel(String username, String email, String passwordHash, String fullName, String securityQuestion, String securityAnswerHash) {
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.fullName = fullName;
        this.securityQuestion = securityQuestion != null ? securityQuestion : "What was the name of your first pet?";
        this.securityAnswerHash = securityAnswerHash != null ? securityAnswerHash : "";
    }

    public UserModel(String username, String email, String passwordHash, String fullName) {
        this(username, email, passwordHash, fullName, "What was the name of your first pet?", "");
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getSecurityQuestion() { return securityQuestion; }
    public void setSecurityQuestion(String securityQuestion) { this.securityQuestion = securityQuestion; }

    public String getSecurityAnswerHash() { return securityAnswerHash; }
    public void setSecurityAnswerHash(String securityAnswerHash) { this.securityAnswerHash = securityAnswerHash; }

    public JSONObject toJSONObject() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("username", username);
        json.put("email", email);
        json.put("passwordHash", passwordHash);
        json.put("fullName", fullName);
        json.put("securityQuestion", securityQuestion);
        json.put("securityAnswerHash", securityAnswerHash);
        return json;
    }

    public static UserModel fromJSONObject(JSONObject json) throws JSONException {
        return new UserModel(
                json.optString("username", ""),
                json.optString("email", ""),
                json.optString("passwordHash", ""),
                json.optString("fullName", ""),
                json.optString("securityQuestion", "What was the name of your first pet?"),
                json.optString("securityAnswerHash", "")
        );
    }
}