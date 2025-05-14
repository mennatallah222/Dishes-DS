package com.dishes.controllers;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.math.BigDecimal;
import com.dishes.startup.AdminInitializer;
@Path("/configs")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ConfigsResource {

    @Inject
    private AdminInitializer adminInitializer;

    @GET
    @Path("/min-order-charge")
    public Response getMinimumOrderCharge() {
        BigDecimal currentCharge = adminInitializer.getMinimumOrderCharge();
        return Response.ok(currentCharge).build();
    }
    
    @POST
    @Path("/min-order-charge")
    public Response setMinimumOrderCharge(@QueryParam("charge") BigDecimal newCharge) {
        if (newCharge == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                   .entity("Charge parameter is required")
                   .build();
        }
        
        if (newCharge.compareTo(BigDecimal.ZERO) < 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                   .entity("Minimum charge cannot be negative")
                   .build();
        }
        
        adminInitializer.setMinimumOrderCharge(newCharge);
        return Response.ok()
               .entity("Minimum charge updated to: " + newCharge)
               .build();
    }
}