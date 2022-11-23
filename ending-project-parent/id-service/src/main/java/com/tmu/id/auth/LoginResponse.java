package com.tmu.id.auth;

public class LoginResponse {

    private String accessToken;

    private String tokenType = "bearer";

    public LoginResponse(String accessToken){ this.accessToken = accessToken; }

    public String getAccessToken(){ return accessToken; }

    public void setAccessToken(String accessToken){ this.accessToken = accessToken; }

    public String getTokenType(){ return tokenType; }

    public void setTokenType(String tokenType){ this.tokenType = tokenType; }

}

