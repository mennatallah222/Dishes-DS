package com.dishes.configs;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import com.fasterxml.jackson.databind.ObjectMapper;

@ApplicationScoped
public class ObjectMapperProducer {

    @Produces
    public ObjectMapper createObjectMapper() {
        return new ObjectMapper();
    }
}
