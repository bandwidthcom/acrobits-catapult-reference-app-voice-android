package com.bandwidth.androidreference.utils;


import android.content.Context;
import android.content.SharedPreferences;

import com.bandwidth.androidreference.R;
import com.bandwidth.androidreference.data.Credentials;
import com.bandwidth.androidreference.data.Endpoint;
import com.bandwidth.androidreference.data.User;

public class SaveManager {

    private static final String USERNAME_KEY = "USERNAME_KEY";
    private static final String PASSWORD_KEY = "PASSWORD_KEY";
    private static final String NUMBER_KEY = "NUMBER_KEY";
    private static final String ID_KEY = "ID_KEY";
    private static final String DOMAIN_ID_KEY = "DOMAIN_ID_KEY";
    private static final String NAME_KEY = "NAME_KEY";
    private static final String APPLICATION_ID_KEY = "APPLICATION_ID_KEY";
    private static final String SIP_URI_KEY = "SIP_URI_KEY";
    private static final String CRED_USERNAME_KEY = "CRED_USERNAME_KEY";
    private static final String CRED_REALM_KEY = "CRED_REALM_KEY";

    private static String getString(Context context, String key, String defaultValue) {
        SharedPreferences sharedPref = context.getSharedPreferences(
                context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        return sharedPref.getString(key, defaultValue);
    }

    private static void setString(Context context, String key, String value) {
        SharedPreferences sharedPref = context.getSharedPreferences(
                context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, value);
        editor.commit();
    }

    private static void remove(Context context, String key) {
        SharedPreferences sharedPref = context.getSharedPreferences(
                context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.remove(key);
        editor.commit();
    }

    public static String getUsername(Context context) {
        return getString(context, USERNAME_KEY, null);
    }

    public static void setUsername(Context context, String username) {
        setString(context, USERNAME_KEY, username);
    }

    public static String getPassword(Context context) {
        return getString(context, PASSWORD_KEY, null);
    }

    public static void setPassword(Context context, String password) {
        setString(context, PASSWORD_KEY, password);
    }

    public static String getNumber(Context context) {
        return getString(context, NUMBER_KEY, null);
    }

    public static void setNumber(Context context, String number) {
        setString(context, NUMBER_KEY, number);
    }

    public static String getId(Context context) {
        return getString(context, ID_KEY, null);
    }

    public static void setId(Context context, String id) {
        setString(context, ID_KEY, id);
    }

    public static String getName(Context context) {
        return getString(context, NAME_KEY, null);
    }

    public static void setName(Context context, String name) {
        setString(context, NAME_KEY, name);
    }

    public static String getApplicationId(Context context) {
        return getString(context, APPLICATION_ID_KEY, null);
    }

    public static void setApplicationId(Context context, String applicationId) {
        setString(context, APPLICATION_ID_KEY, applicationId);
    }

    public static String getDomainId(Context context) {
        return getString(context, DOMAIN_ID_KEY, null);
    }

    public static void setDomainId(Context context, String domainId) {
        setString(context, DOMAIN_ID_KEY, domainId);
    }

    public static String getSipUri(Context context) {
        return getString(context, SIP_URI_KEY, null);
    }

    public static void setSipUri(Context context, String sipUri) {
        setString(context, SIP_URI_KEY, sipUri);
    }

    public static String getCredUsername(Context context) {
        return getString(context, CRED_USERNAME_KEY, null);
    }

    public static void setCredUsername(Context context, String credUsername) {
        setString(context, CRED_USERNAME_KEY, credUsername);
    }

    public static String getRealm(Context context) {
        return getString(context, CRED_REALM_KEY, null);
    }

    public static void setRealm(Context context, String realm) {
        setString(context, CRED_REALM_KEY, realm);
    }

    public static void saveUser(Context context, User user) {
        SaveManager.setUsername(context, user.getUserName());
        SaveManager.setNumber(context, user.getPhoneNumber());
        SaveManager.setId(context, user.getEndpoint().getId());
        SaveManager.setDomainId(context, user.getEndpoint().getDomainId());
        SaveManager.setName(context, user.getEndpoint().getName());
        SaveManager.setApplicationId(context, user.getEndpoint().getApplicationId());
        SaveManager.setSipUri(context, user.getEndpoint().getSipUri());
        SaveManager.setCredUsername(context, user.getEndpoint().getCredentials().getUsername());
        SaveManager.setPassword(context, user.getEndpoint().getCredentials().getPassword());
        SaveManager.setRealm(context, user.getEndpoint().getCredentials().getRealm());
    }

    public static User getUser(Context context) {
        User user = null;
        String username = SaveManager.getUsername(context);
        if (username != null) {
            user = new User();
            user.setUserName(username);
            user.setPhoneNumber(SaveManager.getNumber(context));
            Credentials credentials = new Credentials();
            credentials.setUsername(SaveManager.getCredUsername(context));
            credentials.setPassword(SaveManager.getPassword(context));
            credentials.setRealm(SaveManager.getRealm(context));
            Endpoint endpoint = new Endpoint();
            endpoint.setCredentials(credentials);
            endpoint.setId(SaveManager.getId(context));
            endpoint.setDomainId(SaveManager.getDomainId(context));
            endpoint.setName(SaveManager.getName(context));
            endpoint.setSipUri(SaveManager.getSipUri(context));
            user.setEndpoint(endpoint);
        }
        return user;
    }

    public static void removeUser(Context context) {
        remove(context, USERNAME_KEY);
        remove(context, NUMBER_KEY);
        remove(context, ID_KEY);
        remove(context, DOMAIN_ID_KEY);
        remove(context, NAME_KEY);
        remove(context, APPLICATION_ID_KEY);
        remove(context, SIP_URI_KEY);
        remove(context, CRED_USERNAME_KEY);
        remove(context, PASSWORD_KEY);
        remove(context, CRED_REALM_KEY);
    }
}
