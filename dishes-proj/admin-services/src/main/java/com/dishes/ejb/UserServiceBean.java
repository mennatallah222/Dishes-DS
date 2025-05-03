package com.dishes.ejb;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Stateless
public class UserServiceBean {
    @PersistenceContext
    private EntityManager em;

    
}