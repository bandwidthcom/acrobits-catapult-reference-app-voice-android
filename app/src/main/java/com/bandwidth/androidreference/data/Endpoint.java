package com.bandwidth.androidreference.data;

import com.bandwidth.androidreference.Credentials;

public class Endpoint {
    private String id;
    private boolean enabled;
    private String domainId;
    private String name;
    private String applicationId;
    private String sipUri;
    private Credentials credentials;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getDomainId() {
        return domainId;
    }

    public void setDomainId(String domainId) {
        this.domainId = domainId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public String getSipUri() {
        return sipUri;
    }

    public void setSipUri(String sipUri) {
        this.sipUri = sipUri;
    }

    public Credentials getCredentials() {
        return credentials;
    }

    public void setCredentials(Credentials credentials) {
        this.credentials = credentials;
    }
}
