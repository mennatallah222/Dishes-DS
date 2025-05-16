package com.dishes.services;

import java.util.List;

import com.dishes.entities.Log;

import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class ErrorLogServiceImpl implements ErrorLogService {
    @PersistenceContext(unitName = "userPU")
    private EntityManager em;

    @Override
    public void saveError(Log event) {
        try {
            em.persist(event);
        }
        catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        }
    }

    @Override
    public List<Log> getAllErrors() {
        return em.createQuery("SELECT f FROM Log f", Log.class).getResultList();
    }
}