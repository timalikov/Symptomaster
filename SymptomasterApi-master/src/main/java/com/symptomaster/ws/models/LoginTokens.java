package com.symptomaster.ws.models;

import com.google.gson.annotations.Expose;

public class LoginTokens {
    @Expose
    private String authToken;
    @Expose
    private String historyKey;

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public String getHistoryKey() {
        return historyKey;
    }

    public void setHistoryKey(String historyKey) {
        this.historyKey = historyKey;
    }
}
