package com.dishes.startup;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import com.dishes.entities.Admin;

@Startup
@Singleton
public class AdminInitializer {

    @PersistenceContext(unitName = "userPU")
    private EntityManager em;

    @PostConstruct
    public void init() {
        try {
            Long count = em.createQuery("SELECT COUNT(a) FROM Admin a WHERE a.email = :email", Long.class)
                    .setParameter("email", "superadmin@dishes.com")
                    .getSingleResult();

            if (count == 0) {
                Admin admin = new Admin();
                admin.setEmail("superadmin@dishes.com");
                admin.setPassword("123");
                em.persist(admin);
                System.out.println("Super admin is created");
            }
            else{
                System.out.println("Super admin already exists");
            }

        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
