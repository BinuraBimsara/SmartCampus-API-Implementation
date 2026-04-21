package com.smartcampus;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import java.net.URI;

public class Main {
    // Base URI the Grizzly HTTP server will listen on
    public static final String BASE_URI = "http://localhost:8080/";

    public static HttpServer startServer() {
        // Create a resource config that scans for JAX-RS resources and providers
        final RestApplication rc = new RestApplication();

        // Create and start a new instance of grizzly http server
        return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);
    }

    public static void main(String[] args) {
        try {
            final HttpServer server = startServer();
            System.out.println("---------------------------------------------------------");
            System.out.println("Smart Campus API successfully started.");
            System.out.println("Access the Discovery Endpoint at: " + BASE_URI + "api/v1");
            System.out.println("Hit ENTER (in the terminal) to stop the server...");
            System.out.println("---------------------------------------------------------");
            System.in.read();
            server.shutdownNow();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
