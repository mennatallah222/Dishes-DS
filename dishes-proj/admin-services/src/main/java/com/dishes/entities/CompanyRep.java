package com.dishes.entities;

import jakarta.persistence.Entity;

@Entity
public class CompanyRep extends User {
    private String companyName;
    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }
}