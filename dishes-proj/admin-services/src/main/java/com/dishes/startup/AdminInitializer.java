package com.dishes.startup;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.Lock;
import jakarta.ejb.LockType;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

import java.math.BigDecimal;

import org.jboss.logging.Logger;

import com.dishes.entities.Admin;
@Startup
@Singleton
public class AdminInitializer {
    @PersistenceContext(unitName = "userPU")
    private EntityManager em;

    private BigDecimal minimumOrderCharge;
    @PostConstruct
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void init() {
        try {
            TypedQuery<Admin> query = em.createQuery(
                "SELECT a FROM Admin a WHERE a.email = :email", Admin.class);
            query.setParameter("email", "superadmin@dishes.com");
            
            if (query.getResultList().isEmpty()) {
                Admin admin = new Admin();
                admin.setEmail("superadmin@dishes.com");
                admin.setPassword("123");
                admin.setName("Super Administrator");
                em.persist(admin);
                Logger.getLogger(getClass()).info("Created default admin user");
            }
            minimumOrderCharge=new BigDecimal("50");
        }
        catch (Exception e) {
            Logger.getLogger(getClass()).error("Admin initialization failed", e);
            throw e;
        }
    }

    @Lock(LockType.READ)
    public BigDecimal getMinimumOrderCharge() {
        return minimumOrderCharge;
    }
    
    @Lock(LockType.WRITE)
    public void setMinimumOrderCharge(BigDecimal newCharge) {
        this.minimumOrderCharge = newCharge;
    }
}