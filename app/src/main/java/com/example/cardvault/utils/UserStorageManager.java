package com.example.cardvault.utils;

import android.content.Context;

import com.example.cardvault.models.UserModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class UserStorageManager {
    private static final String USERS_FILE_NAME = "users.json";
    private final Context context;

    public UserStorageManager(Context context) {
        this.context = context.getApplicationContext();
    }

    public synchronized List<UserModel> getAllUsers() {
        List<UserModel> userList = new ArrayList<>();
        File file = new File(context.getFilesDir(), USERS_FILE_NAME);
        if (!file.exists()) {
            return userList;
        }

        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] data = new byte[(int) file.length()];
            int bytesRead = fis.read(data);
            if (bytesRead > 0) {
                String jsonStr = new String(data, 0, bytesRead);
                JSONArray jsonArray = new JSONArray(jsonStr);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    userList.add(UserModel.fromJSONObject(obj));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return userList;
    }

    public synchronized UserModel getUserByEmail(String email) {
        if (email == null) return null;
        for (UserModel user : getAllUsers()) {
            if (email.equalsIgnoreCase(user.getEmail())) {
                return user;
            }
        }
        return null;
    }

    public synchronized UserModel getUserByUsername(String username) {
        if (username == null) return null;
        for (UserModel user : getAllUsers()) {
            if (username.equalsIgnoreCase(user.getUsername())) {
                return user;
            }
        }
        return null;
    }

    public synchronized UserModel getUserByEmailOrUsername(String identifier) {
        if (identifier == null) return null;
        for (UserModel user : getAllUsers()) {
            if (identifier.equalsIgnoreCase(user.getEmail()) || identifier.equalsIgnoreCase(user.getUsername())) {
                return user;
            }
        }
        return null;
    }

    public synchronized boolean isEmailTaken(String email) {
        return getUserByEmail(email) != null;
    }

    public synchronized boolean isUsernameTaken(String username) {
        return getUserByUsername(username) != null;
    }

    public synchronized boolean registerUser(UserModel newUser) {
        if (isEmailTaken(newUser.getEmail()) || isUsernameTaken(newUser.getUsername())) {
            return false; // Email or username already registered
        }
        List<UserModel> users = getAllUsers();
        users.add(newUser);
        return writeUsersToFile(users);
    }

    public synchronized boolean validateUser(String identifier, String rawPassword) {
        UserModel user = getUserByEmailOrUsername(identifier);
        if (user == null) return false;
        String hashedInput = SecurityUtils.hashPassword(rawPassword);
        return hashedInput.equals(user.getPasswordHash());
    }

    public synchronized boolean validateSecurityAnswer(String identifier, String rawAnswer) {
        UserModel user = getUserByEmailOrUsername(identifier);
        if (user == null || rawAnswer == null || rawAnswer.trim().isEmpty()) {
            return false;
        }

        String savedAnswerHash = user.getSecurityAnswerHash();
        if (savedAnswerHash == null || savedAnswerHash.isEmpty()) {
            // For legacy demonstration accounts created before security questions were introduced
            return true;
        }

        String normalizedAnswer = rawAnswer.trim().toLowerCase();
        String hashedInput = SecurityUtils.hashPassword(normalizedAnswer);
        return hashedInput.equals(savedAnswerHash);
    }

    public synchronized boolean updateUserProfile(String identifier, String newFullName) {
        List<UserModel> users = getAllUsers();
        boolean updated = false;
        for (UserModel user : users) {
            if (identifier.equalsIgnoreCase(user.getEmail()) || identifier.equalsIgnoreCase(user.getUsername())) {
                user.setFullName(newFullName);
                updated = true;
                break;
            }
        }
        if (updated) {
            return writeUsersToFile(users);
        }
        return false;
    }

    public synchronized boolean resetPassword(String identifier, String newRawPassword) {
        List<UserModel> users = getAllUsers();
        boolean updated = false;
        String newHash = SecurityUtils.hashPassword(newRawPassword);
        for (UserModel user : users) {
            if (identifier.equalsIgnoreCase(user.getEmail()) || identifier.equalsIgnoreCase(user.getUsername())) {
                user.setPasswordHash(newHash);
                updated = true;
                break;
            }
        }
        if (updated) {
            return writeUsersToFile(users);
        }
        return false;
    }

    private boolean writeUsersToFile(List<UserModel> users) {
        JSONArray array = new JSONArray();
        for (UserModel user : users) {
            try {
                array.put(user.toJSONObject());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        File file = new File(context.getFilesDir(), USERS_FILE_NAME);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(array.toString().getBytes());
            fos.flush();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}