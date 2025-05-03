package com.dishes.rest;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import com.dishes.ejb.AdminServiceBean;
import com.dishes.entities.Admin;

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
            return Response.ok(admin).build(); //return token
        }
        else{
            return Response.status(Response.Status.UNAUTHORIZED).entity("Invalid credentials").build();
        }
    }

    @POST
    @Path("/company-reps")
    @RolesAllowed("ADMIN")
    public String createCompanyRep(@QueryParam("company") String companyName) {
        return adminService.createCompanyRep(companyName).getPassword();
    }
}
