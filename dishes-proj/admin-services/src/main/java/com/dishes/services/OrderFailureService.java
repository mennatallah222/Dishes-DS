package com.dishes.services;

import java.util.List;

import com.dishes.entities.OrderFailure;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Stateless
public class OrderFailureService {
    @PersistenceContext
    private EntityManager em;
    
    public List<OrderFailure> getAllFailures() {
        return em.createQuery("SELECT f FROM OrderFailure f ORDER BY f.timestamp DESC", OrderFailure.class).getResultList();
    }
}