// package com.dishes.rabbitmq;

// import com.dishes.dtos.events.OrderFailedEvent;
// import com.dishes.entities.OrderFailure;
// import com.fasterxml.jackson.databind.ObjectMapper;

// import jakarta.ejb.ActivationConfigProperty;
// import jakarta.ejb.MessageDriven;
// import jakarta.inject.Inject;
// import jakarta.jms.Message;
// import jakarta.jms.MessageListener;
// import jakarta.jms.TextMessage;
// import jakarta.persistence.EntityManager;
// import jakarta.persistence.PersistenceContext;

// @MessageDriven(activationConfig = {
//     @ActivationConfigProperty(
//         propertyName = "destinationLookup",
//         propertyValue = "java:/jms/queue/PaymentFailedQueue"),
//     @ActivationConfigProperty(
//         propertyName = "destinationType",
//         propertyValue = "jakarta.jms.Queue")
// })
// public class OrderFailureMDB implements MessageListener {
    
//     @PersistenceContext
//     private EntityManager em;
    
//     @Inject
//     private ObjectMapper objectMapper;

//     @Override
//     public void onMessage(Message message) {
//         try {
//             if (message instanceof TextMessage) {
//                 TextMessage textMessage = (TextMessage) message;
//                 String jsonPayload = textMessage.getText();
//                 OrderFailedEvent event = objectMapper.readValue(jsonPayload, OrderFailedEvent.class);
                
//                 OrderFailure failure = new OrderFailure();
//                 failure.setOrderId(event.getOrderId());
//                 failure.setCustomerId(event.getCustomerId());
//                 failure.setAmount(event.getTotalAmount());
//                 failure.setReason(event.getReason());
//                 failure.setTimestamp(event.getTimestamp());
//                 em.persist(failure);
//                 em.flush();
//                 System.out.println("Persisted payment failure: " + failure.getId());
//             }
//         }
//         catch (Exception e) {
//             System.err.println("Error processing payment failure message:");
//             e.printStackTrace();
//         }
//     }
// }