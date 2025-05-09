package com.dishes.controllers;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.ArrayList;
import java.util.List;

import com.dishes.dtos.CompanyCreationResult;
import com.dishes.dtos.CompanyDTO;
import com.dishes.dtos.SellerLoginRequest;
import com.dishes.dtos.SellerResponse;
import com.dishes.entities.Admin;
import com.dishes.services.AdminServiceBean;
import com.dishes.services.CustomerServiceBean;

@Path("/admin")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AdminResource {

    @Inject
    private AdminServiceBean adminService;
    @Inject
    private CustomerServiceBean customerService;

    @POST
    @Path("/login")
    public Response login(Admin credentials) {
        Admin admin = adminService.authenticate(credentials.getEmail(), credentials.getPassword());
        if (admin != null) {
            return Response.ok(admin).build(); //to-return-token
        }
        else{
            return Response.status(Response.Status.UNAUTHORIZED).entity("Invalid credentials").build();
        }
    }

    @POST
    @Path("/add-companies")
    @RolesAllowed("ADMIN")
    public Response createCompanyReps(List<CompanyDTO> companyDTOs) {
        CompanyCreationResult result= adminService.createCompanyReps(companyDTOs);
        List<String> output=new ArrayList<>(result.getCreatedReps().stream()
            .map(r -> r.getCompanyName() + " (" + r.getEmail() + "): " + r.getPassword())
            .toList());
        if(!result.getSkippedMessages().isEmpty()){
            output.add("Already existing companies: "+String.join(", ", result.getSkippedMessages()));
        }
        return Response.ok(output).build();
    }

    @GET
    @Path("/customers")
    @RolesAllowed("ADMIN")
    public Response listCustomers() {
        try{
            return Response.ok(customerService.getAllCustomers()).build();
        }
        catch(Exception e){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error fetching the customers: "+e.getMessage()).build();
        }
    }

    @GET
    @Path("/get-companies")
    @RolesAllowed("ADMIN")
    public Response listCompanyReps() {
        return Response.ok(adminService.listCompanyReps()).build();
    }

    @POST
    @Path("/seller/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response loginSeller(@Valid @NotNull SellerLoginRequest req){
        SellerResponse seller=adminService.authenticateSeller(req.getEmail(), req.getCompanyName(), req.getPassword());
        if(seller==null){
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        return Response.ok(seller).build();
    }
}
