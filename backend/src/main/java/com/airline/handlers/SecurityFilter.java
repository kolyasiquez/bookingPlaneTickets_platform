package com.airline.handlers;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.Base64;

@Provider
@Priority(Priorities.AUTHENTICATION)
public class SecurityFilter implements ContainerRequestFilter {

    private static final String REALM = "Airline Realm";
    private static final String ALLOWED_USERNAME = "admin";
    private static final String ALLOWED_PASSWORD = "admin123";

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String path = requestContext.getUriInfo().getPath();
        if (path.contains("application.wadl")) {
            return;
        }

        String authHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Basic ")) {
            abortWithUnauthorized(requestContext);
            return;
        }

        try {
            String base64Credentials = authHeader.substring("Basic ".length()).trim();
            byte[] decodedBytes = Base64.getDecoder().decode(base64Credentials);
            String credentials = new String(decodedBytes, java.nio.charset.StandardCharsets.UTF_8);

            final String[] values = credentials.split(":", 2);
            if (values.length != 2) {
                abortWithUnauthorized(requestContext);
                return;
            }

            String username = values[0];
            String password = values[1];

            if (!ALLOWED_USERNAME.equals(username) || !ALLOWED_PASSWORD.equals(password)) {
                abortWithUnauthorized(requestContext);
            }
        } catch (Exception e) {
            abortWithUnauthorized(requestContext);
        }
    }

    private void abortWithUnauthorized(ContainerRequestContext requestContext) {
        requestContext.abortWith(
                Response.status(Response.Status.UNAUTHORIZED)
                        .header(HttpHeaders.WWW_AUTHENTICATE, "Basic realm=\"" + REALM + "\"")
                        .entity("{\"error\": \"Unauthorized - Basic Authentication Required\"}")
                        .type("application/json")
                        .build()
        );
    }
}
