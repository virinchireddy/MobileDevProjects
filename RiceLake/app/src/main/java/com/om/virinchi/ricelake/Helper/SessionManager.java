package com.om.virinchi.ricelake.Helper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.om.virinchi.ricelake.Activites.LoginScreen;

import java.util.HashMap;

/**
 * Created by Virinchi on 10/15/2016.
 */
public class SessionManager {
    SharedPreferences pref;
    SharedPreferences.Editor editor;

    // Context
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Sharedpref file name
    private static final String PREF_NAME = "RiceLake";

    // All Shared Preferences Keys
    private static final String IS_LOGIN = "IsLoggedIn";
    // User name (make variable public to access from outside)
    public static final String authenticationToken = "authenticationToken";
    public static final String userId = "userId";
    public static final String password="password";
    public static final String userName = "UserName";
    public static final String userType = "UserType";
    // Constructor
    @SuppressLint("CommitPrefEdits")
    public SessionManager(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }
    /**
     * Create login session
     * */
    public void createLoginSession(String authenticationToken,String userId,String password, String userName,String userType){
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);

        // Storing name in pref
        editor.putString(SessionManager.authenticationToken, authenticationToken);
        editor.putString(SessionManager.userId, userId);
        editor.putString(SessionManager.password, password);
        editor.putString(SessionManager.userName, userName);
        editor.putString(SessionManager.userType, userType);
        // commit changes
        editor.commit();
    }
    public void updateLoginSession(String userId){

        editor.putString(SessionManager.userId, userId);
        editor.commit();
    }
    /**
     * Check login method wil check user login status
     * If false it will redirect user to login page
     * Else won't do anything
     * */
    public void checkLogin(){
        // Check login status
        if(!this.isLoggedIn()){
            // user is not logged in redirect him to Login Activity
            Intent i = new Intent(_context, LoginScreen.class);
            // Closing all the Activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // Staring Login Activity
            _context.startActivity(i);
        }

    }
    /**
     * Quick check for login
     * **/
    // Get Login State
    public boolean isLoggedIn(){
        return pref.getBoolean(IS_LOGIN, false);
    }
    /**
     * Get stored session data
     * */
    public HashMap<String,String> getUserDetails(){
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(authenticationToken, pref.getString(authenticationToken, null));
        user.put(userId, pref.getString(userId, null));
        user.put(password, pref.getString(password, null));
        user.put(userName, pref.getString(userName, null));
        user.put(userType,pref.getString(userType, null));
        // return user
        return user;
    }
    /**
     * Clear session details
     * */
    public void logoutUser(){
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();

        // After logout redirect user to Loing Activity
        Intent i = new Intent(_context, LoginScreen.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        _context.startActivity(i);

    }
}
