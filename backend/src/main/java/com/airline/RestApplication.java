package com.airline;

import org.glassfish.jersey.media.multipart.MultiPartFeature;
import com.airline.service.FlightBookingResource;
import com.airline.handlers.LoggingFilter;
import com.airline.handlers.SecurityFilter;
import com.airline.handlers.EntityNotFoundExceptionMapper;
import com.airline.handlers.GenericExceptionMapper;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

@ApplicationPath("/api")
public class RestApplication extends Application {
    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<>();
        classes.add(MultiPartFeature.class);
        classes.add(FlightBookingResource.class);
        classes.add(LoggingFilter.class);
        classes.add(SecurityFilter.class);
        classes.add(EntityNotFoundExceptionMapper.class);
        classes.add(GenericExceptionMapper.class);
        return classes;
    }
}
