package com.dishes.dto;

public class AuthResponse {
    private boolean success;
    private String message;
    private String token;
    private Long expiresIn;

    public AuthResponse(boolean success, String message, String token, Long expiresIn) {
        this.success = success;
        this.message = message;
        this.token = token;
        this.expiresIn=expiresIn;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Long expiresIn) {
        this.expiresIn = expiresIn;
    }
}