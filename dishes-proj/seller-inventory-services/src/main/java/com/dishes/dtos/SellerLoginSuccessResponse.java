package com.dishes.dtos;

import com.dishes.interfaces.SellerLoginResponse;

public class SellerLoginSuccessResponse implements SellerLoginResponse{
    private String token;
    private String sellerID;
    private String companyName;
    

    public String getToken() {
        return token;
    }
    public void setToken(String token) {
        this.token = token;
    }
    public String getSellerID() {
        return sellerID;
    }
    public void setSellerID(String sellerID) {
        this.sellerID = sellerID;
    }
    public String getCompanyName() {
        return companyName;
    }
    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }
    
}
