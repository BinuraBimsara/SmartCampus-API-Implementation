package com.smartcampus.resources;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.HashMap;
import java.util.Map;

/**
 * Discovery Endpoint
 * As required by Part 1, Task 2: Root endpoint providing API metadata and Hypermedia links.
 */
@Path("/")
public class DiscoveryResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDiscoveryInfo() {
        // Create an Object (Hashmap) for JSON Serialization
        Map<String, Object> discoveryData = new HashMap<>();

        // 1. Generic Meta Data
        discoveryData.put("api_name", "Smart Campus Sensor & Room Management API");
        discoveryData.put("version", "1.0.0");
        discoveryData.put("admin_contact", "admin@smartcampus.uni.edu");

        // 2. HATEOAS / Hypermedia Links mapping primary resource collections
        Map<String, String> links = new HashMap<>();
        links.put("rooms", "/api/v1/rooms");
        links.put("sensors", "/api/v1/sensors");

        discoveryData.put("links", links);

        return Response.ok(discoveryData).build();
    }
}
