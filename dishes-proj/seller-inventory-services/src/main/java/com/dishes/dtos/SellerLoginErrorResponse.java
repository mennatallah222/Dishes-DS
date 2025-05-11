package com.dishes.dtos;

import com.dishes.interfaces.SellerLoginResponse;

public class SellerLoginErrorResponse implements SellerLoginResponse {
    private boolean serviceAvailable=true;
    private String error;
    public boolean isServiceAvailable() { return serviceAvailable; }
    public String getError() { return error; }
    public void setServiceAvailable(boolean serviceAvailable) {
        this.serviceAvailable = serviceAvailable;
    }
    public void setError(String error) {
        this.error = error;
    }
}
