package com.dishes.services;

import java.util.List;

import com.dishes.dto.OrderFailedEvent;
import com.dishes.entities.OrderFailure;

import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class OrderFailureServiceImpl implements OrderFailureService {
    @PersistenceContext(unitName = "userPU")
    private EntityManager em;

    @Override
    public void saveFailure(OrderFailedEvent event) {
        try {
            OrderFailure failure = new OrderFailure();
            failure.setOrderId(event.getOrderId());
            failure.setCustomerId(event.getCustomerId());
            failure.setTotalAmount(event.getTotalAmount());
            failure.setFailureReason(event.getFailureReason());
            failure.setTimestamp(event.getTimestamp());
            em.persist(failure);
        }
        catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        }
    }

    @Override
    public List<OrderFailure> getAllFailures() {
        return em.createQuery("SELECT f FROM OrderFailure f", OrderFailure.class).getResultList();
    }
}