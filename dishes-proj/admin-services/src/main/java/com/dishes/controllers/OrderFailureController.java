package com.dishes.controllers;

import com.dishes.dtos.events.OrderFailedEvent;
import com.dishes.rabbitmq.RabbitMQProducer;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/failures")
public class OrderFailureController {

    @Inject
    RabbitMQProducer eventProducer; 

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response publishFailure(OrderFailedEvent event) {
        eventProducer.sendPaymentFailureEvent(event);
        return Response.ok("Published failure event to RabbitMQ").build();
    }
}
