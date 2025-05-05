package com.dishes.services;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Stateless
public class UserServiceBean {
    @PersistenceContext
    private EntityManager em;

    
}