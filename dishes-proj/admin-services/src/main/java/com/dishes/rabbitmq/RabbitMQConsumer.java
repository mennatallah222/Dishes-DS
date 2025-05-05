package com.dishes.rabbitmq;

import com.rabbitmq.client.*;  // Correct RabbitMQ import

import com.dishes.dtos.CredentialsMessage;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class RabbitMQConsumer {

    private static final String EXCHANGE_NAME = "credentials_exchange";
    private static final String ROUTING_KEY = "company.credentials";
    private static final String QUEUE_NAME = "credentials_queue";

    public static void main(String[] args) throws Exception {
        com.rabbitmq.client.ConnectionFactory factory = new com.rabbitmq.client.ConnectionFactory();
        factory.setHost("localhost");
        factory.setUsername("guest");
        factory.setPassword("guest");

        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {

            channel.exchangeDeclare(EXCHANGE_NAME, "direct", true);
            channel.queueDeclare(QUEUE_NAME, true, false, false, null);
            channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, ROUTING_KEY);

            System.out.println("Waiting for credential messages...");

            DeliverCallback deliverCallback = (_, delivery) -> {
                String json = new String(delivery.getBody(), StandardCharsets.UTF_8);
                System.out.println("Received credentials: " + json);

                ObjectMapper mapper = new ObjectMapper();
                CredentialsMessage msg = mapper.readValue(json, CredentialsMessage.class);
                sendEmail(msg.getEmail(), msg.getCompanyName(), msg.getPassword());
            };
            channel.basicConsume(QUEUE_NAME, true, deliverCallback, _ -> {});
            Thread.sleep(Long.MAX_VALUE);
        }
    }

    private static void sendEmail(String to, String companyName, String password) {
        Properties emailProps = new Properties();
        try (InputStream input = new FileInputStream("email.properties")) {
            emailProps.load(input);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        final String host = emailProps.getProperty("mail.host");
        final String from = emailProps.getProperty("mail.username");
        final String username = emailProps.getProperty("mail.username");
        final String emailPassword = emailProps.getProperty("mail.password");

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", "587");
        props.put("mail.debug", "true");
        //creates the session and authenticates
        Session session = Session.getInstance(props, new GmailAuthenticator(username, emailPassword));

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject("Company Account Credentials");
            message.setText("Dear " +to+ ", \nYour company account credentials are: \n Your username: "+ companyName + "\n\nYour password is: " + password);

            Transport.send(message);
            System.out.println("Email sent to " + to);

        }
        catch (MessagingException e) {
            e.printStackTrace();
        }
    }
    static class GmailAuthenticator extends Authenticator {
        private final String username;
        private final String password;

        public GmailAuthenticator(String username, String password) {
            this.username = username;
            this.password = password;
        }

        @Override
        protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(username, password);
        }
    }
}
