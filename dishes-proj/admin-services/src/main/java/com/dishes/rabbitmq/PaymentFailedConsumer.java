package com.dishes.rabbitmq;


import com.dishes.dto.OrderFailedEvent;
import com.dishes.services.OrderFailureService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.*;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import jakarta.ejb.EJB;
import jakarta.ejb.Singleton;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;


@Singleton
public class PaymentFailedConsumer {

    private static final String EXCHANGE_NAME="PaymentFailed";
    private static final String QUEUE_NAME="payment.failed.queue";
    private static final String ROUTING_KEY="PaymentFailed";

    @EJB
    private OrderFailureService orderFailureService;


    public void startConsumer() throws IOException, TimeoutException{
        ConnectionFactory f=new ConnectionFactory();
        f.setHost("localhost");
        f.setUsername("guest");
        f.setPassword("guest");

        Connection c=f.newConnection();
        Channel ch=c.createChannel();
        ch.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT, true, false, null);
        ch.queueDeclare(QUEUE_NAME, true, false, false, null);
        ch.queueBind(QUEUE_NAME, EXCHANGE_NAME, ROUTING_KEY);
        System.out.println(" [*] Waiting for payment failed messages...");

        DeliverCallback dc=(_, delivery)->{
            String msg=new String(delivery.getBody(), StandardCharsets.UTF_8);
            System.out.println(" [*] Recieved payment failed msg: '"+msg+"'");

            ObjectMapper om=new ObjectMapper();
            om.registerModule(new JavaTimeModule());
            try{
                OrderFailedEvent e=om.readValue(msg, OrderFailedEvent.class);
                orderFailureService.saveFailure(e);
            }
            catch(Exception e){
                e.printStackTrace();
            }
        };
        ch.basicConsume(QUEUE_NAME, true, dc, _->{});
    }
}