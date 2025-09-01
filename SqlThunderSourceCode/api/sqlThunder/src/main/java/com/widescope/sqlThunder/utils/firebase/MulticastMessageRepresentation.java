package com.widescope.sqlThunder.utils.firebase;

import java.util.List;

public class MulticastMessageRepresentation {

    private String data;
    private List<String> registrationTokens;

    public String getData() {
        return data;
    }
    public void setData(String data) {
        this.data = data;
    }
    public List<String> getRegistrationTokens() {
        return registrationTokens;
    }
    public void setRegistrationTokens(List<String> registrationTokens) {
        this.registrationTokens = registrationTokens;
    }
}