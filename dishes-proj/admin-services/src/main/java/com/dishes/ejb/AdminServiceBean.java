package com.dishes.ejb;

import java.util.UUID;

import com.dishes.entities.*;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;

@Stateless
public class AdminServiceBean {

    @PersistenceContext(unitName = "userPU")
    private EntityManager em;

    public Admin authenticate(String email, String password) {
        try {
            Admin admin = em.createQuery("SELECT a FROM Admin a WHERE a.email = :email", Admin.class)
                    .setParameter("email", email)
                    .getSingleResult();

            if (admin != null && admin.getPassword().equals(password)) {
                return admin;
            }
        }
        catch (NoResultException e) {
            return null;
        }
        return null;
    }

    public CompanyRep createCompanyRep(String companyName) {
        CompanyRep rep = new CompanyRep();
        rep.setCompanyName(companyName);
        rep.setPassword(UUID.randomUUID().toString().substring(0, 8));
        // Save to DB (inject EntityManager like in UserServiceBean)
        return rep;
    }
}
