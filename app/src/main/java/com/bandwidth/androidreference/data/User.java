package com.bandwidth.androidreference.data;

import android.content.Context;

import com.bandwidth.androidreference.Credentials;
import com.bandwidth.androidreference.SaveManager;

public class User {
    private String username;
    private String number;
    private Endpoint endpoint;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Endpoint getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(Endpoint endpoint) {
        this.endpoint = endpoint;
    }

}
