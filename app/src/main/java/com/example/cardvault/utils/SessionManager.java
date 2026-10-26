package com.example.cardvault.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "card_vault_prefs";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    private static final String KEY_USER_EMAIL = "user_email";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_FULL_NAME = "user_full_name";
    private static final String KEY_PROFILE_PIC_PATH = "user_profile_pic_path";

    private final SharedPreferences prefs;

    public SessionManager(Context context) {
        this.prefs = context.getApplicationContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void createLoginSession(String email, String username) {
        createLoginSession(email, username, username);
    }

    public void createLoginSession(String email, String username, String fullName) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putString(KEY_USER_EMAIL, email);
        editor.putString(KEY_USER_NAME, username);
        editor.putString(KEY_FULL_NAME, fullName != null && !fullName.isEmpty() ? fullName : username);
        editor.apply();
    }

    public boolean isLoggedIn() {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public String getUserEmail() {
        return prefs.getString(KEY_USER_EMAIL, "");
    }

    public String getUsername() {
        return prefs.getString(KEY_USER_NAME, "User");
    }

    public String getFullName() {
        return prefs.getString(KEY_FULL_NAME, getUsername());
    }

    public void updateFullName(String fullName) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_FULL_NAME, fullName);
        editor.apply();
    }

    public String getProfilePicPath() {
        return prefs.getString(KEY_PROFILE_PIC_PATH, "");
    }

    public void updateProfilePicPath(String path) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_PROFILE_PIC_PATH, path);
        editor.apply();
    }

    public void logoutUser() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();
    }
}