package com.example.domain.aggregate;


public class Tokens {

    private String jwtToken;
    private User user;

    public String getJwtToken() {
        return jwtToken;
    }

    public void setJwtToken(String jwtToken) {
        this.jwtToken = jwtToken;
    }

    public void setUser(User user) {
        this.user = user;
    }

}
