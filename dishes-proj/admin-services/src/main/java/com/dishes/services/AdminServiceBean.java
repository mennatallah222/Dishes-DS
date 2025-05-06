package com.dishes.services;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.dishes.dtos.CompanyCreationResult;
import com.dishes.dtos.CompanyDTO;
import com.dishes.dtos.CredentialsMessage;
import com.dishes.dtos.UserDTO;
import com.dishes.entities.*;
import com.dishes.rabbitmq.RabbitMQProducer;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;

@Stateless
public class AdminServiceBean {

    @PersistenceContext(unitName = "userPU")
    private EntityManager em;
    @Inject
    private RabbitMQProducer rabbitMQProducer;
    

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


    public CompanyCreationResult createCompanyReps(List<CompanyDTO> companyDTOs) {
        List<CompanyDTO> createdReps = new ArrayList<>();
        List<String> skippedMsgs = new ArrayList<>();
        for (CompanyDTO dto : companyDTOs) {
            try{
                boolean isExisting=em.createQuery("select count(c) from CompanyRep c where c.companyName= :name", Long.class)
                .setParameter("name", dto.getCompanyName()).getSingleResult()>0;
                if(isExisting){
                    skippedMsgs.add(dto.getCompanyName()+" already exists");
                    continue;
                }
                CompanyRep rep = new CompanyRep();
                rep.setName(dto.getCompanyName());
                rep.setCompanyName(dto.getCompanyName());
                rep.setEmail(dto.getEmail());
                rep.setPassword(UUID.randomUUID().toString().substring(0, 8));
                em.persist(rep);

                CompanyDTO companyDTO=new CompanyDTO(rep.getCompanyName(), rep.getEmail(), rep.getPassword());
                createdReps.add(companyDTO);

                //to send the email to the company rep
                CredentialsMessage msg = new CredentialsMessage();
                msg.setCompanyName(rep.getCompanyName());
                msg.setEmail(rep.getEmail());
                msg.setPassword(rep.getPassword());
                rabbitMQProducer.sendCredentialsMessage(msg);
            }
            catch (Exception e){
                skippedMsgs.add("Error creating company: " + dto.getCompanyName() + " (" + e.getMessage() + ")");
                e.printStackTrace();
            }
        }

        return new CompanyCreationResult(createdReps, skippedMsgs);
    }


    public List<UserDTO> listCompanyReps() {
        return em.createQuery("SELECT new com.dishes.dtos.UserDTO(u.name, u.email) FROM CompanyRep u", UserDTO.class)
                .getResultList();
    }
}
