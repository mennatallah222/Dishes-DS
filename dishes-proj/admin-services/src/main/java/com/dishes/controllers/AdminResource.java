package com.dishes.controllers;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.ArrayList;
import java.util.List;

import com.dishes.dtos.CompanyCreationResult;
import com.dishes.dtos.CompanyDTO;
import com.dishes.entities.Admin;
import com.dishes.services.AdminServiceBean;

@Path("/admin")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AdminResource {

    @Inject
    private AdminServiceBean adminService;

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

    //This method is a placholder for now to get the customers' all-data from the 3rd service in the project
    // @GET
    // @Path("/customers")
    // @RolesAllowed("ADMIN")
    // public Response listCustomers() {

    // }

    @GET
    @Path("/get-companies")
    @RolesAllowed("ADMIN")
    public Response listCompanyReps() {
        return Response.ok(adminService.listCompanyReps()).build();
    }

}
