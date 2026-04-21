package com.smartcampus;

import org.glassfish.jersey.server.ResourceConfig;
import jakarta.ws.rs.ApplicationPath;

@ApplicationPath("/api/v1")
public class RestApplication extends ResourceConfig {
    public RestApplication() {
        // Tells Jersey where to scan for our resources (Controllers), providers, filters, etc.
        packages("com.smartcampus");
    }
}
