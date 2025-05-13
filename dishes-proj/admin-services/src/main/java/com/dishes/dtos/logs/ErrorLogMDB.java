// package com.dishes.dtos.logs;


// import org.jboss.logging.Logger;

// import jakarta.ejb.ActivationConfigProperty;
// import jakarta.ejb.MessageDriven;
// import jakarta.jms.JMSException;
// import jakarta.jms.Message;
// import jakarta.jms.MessageListener;

// @MessageDriven(activationConfig = {
//     @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "jakarta.jms.Topic"),
//     @ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "topic/logExchange"),
//     @ActivationConfigProperty(propertyName = "messageSelector", propertyValue = "severity='ERROR"),
// })
// public class ErrorLogMDB implements MessageListener{

//     private static final Logger LOG=Logger.getLogger(ErrorLogMDB.class);
//     @Override
//     public void onMessage(Message message) {
//         try{
//             String errMsg=message.getBody(String.class);
//             LOG.error(""+errMsg);
//         }
//         catch (JMSException je) {
//             LOG.error("Error in processing the log msg: "+je);
//         }
//     }
    
// }
