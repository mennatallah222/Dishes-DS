package com.dishes.clients;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.Collections;
import java.util.List;

import com.dishes.dtos.UserDTO;

@ApplicationScoped
public class CustomerServiceClient {
    private final String CUSTOMER_SERVICE_URL="http://localhost:8081/api/customers/getCustomers";
    
    public List<UserDTO> getAllCustomers(){
        Client c=ClientBuilder.newClient();
        try{
            Response res=c.target(CUSTOMER_SERVICE_URL).request(MediaType.APPLICATION_JSON).get();
            if(res.getStatus()==200){
                return res.readEntity(new GenericType<List<UserDTO>>(){});
            }
            return Collections.emptyList();
        }
        finally{
            c.close();
        }
    }
}