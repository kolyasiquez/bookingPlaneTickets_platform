package com.airline.handlers;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

@Provider
public class LoggingFilter implements ContainerRequestFilter, ContainerResponseFilter {

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        System.out.println("\n--- Inbound REST Request ---");
        System.out.println("Method: " + requestContext.getMethod());
        System.out.println("Path: " + requestContext.getUriInfo().getRequestUri().toString());
        System.out.println("-----------------------------\n");
    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
        System.out.println("\n--- Outbound REST Response ---");
        System.out.println("Status: " + responseContext.getStatus());
        System.out.println("------------------------------\n");
    }
}
