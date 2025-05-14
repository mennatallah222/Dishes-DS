package com.dishes.controllers;

import java.util.HashSet;
import java.util.Set;

import com.dishes.configs.CORSFilter;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

@ApplicationPath("/api")
public class RestApplication extends Application {
    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<>();
        classes.add(CORSFilter.class);
        classes.add(PreflightResource.class);
         classes.add(AdminResource.class);
         classes.add(ConfigsResource.class);
        return classes;
    }
}