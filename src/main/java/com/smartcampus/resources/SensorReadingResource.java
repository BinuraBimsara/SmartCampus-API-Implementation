package com.smartcampus.resources;

import com.smartcampus.exceptions.SensorUnavailableException;
import com.smartcampus.models.Sensor;
import com.smartcampus.models.SensorReading;
import com.smartcampus.store.DataStore;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.UUID;

/**
 * Dedicated Child / Sub-Resource for managing SensorReadings logically
 * nested under a specific context `{sensorId}`.
 * NO @Path annotation! It's resolved via the parent SensorResource!
 */
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorReadingResource {

    // Internal tracker of the parent's context passed by Sub-Resource Locator
    private final String activeSensorId;

    // Constructor initialized by SensorResource locator
    public SensorReadingResource(String sensorId) {
        this.activeSensorId = sensorId;
    }

    // GET /api/v1/sensors/{sensorId}/readings : Historical Data Fetch
    @GET
    public Response getReadings() {
        // Ensure sensor exists first
        if (!DataStore.getSensors().containsKey(activeSensorId)) {
             return Response.status(Response.Status.NOT_FOUND)
                     .entity("{\"error\": \"Sensor context '" + activeSensorId + "' not found.\"}")
                     .build();
        }

        List<SensorReading> history = DataStore.getSensorReadings().get(activeSensorId);
        return Response.ok(history).build();
    }

    // POST /api/v1/sensors/{sensorId}/readings : Append new reading
    @POST
    public Response addReading(SensorReading newReading) {
        Sensor parentSensor = DataStore.getSensors().get(activeSensorId);

        // Does Sensor exist?
        if (parentSensor == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\": \"Sensor not found. Cannot append reading.\"}")
                    .build();
        }

        // State Constraint (403 Forbidden mapping)
        // If a sensor is physically disconnected and marked "MAINTENANCE" or "OFFLINE"
        if ("MAINTENANCE".equalsIgnoreCase(parentSensor.getStatus()) || 
            "OFFLINE".equalsIgnoreCase(parentSensor.getStatus())) {
            
             throw new SensorUnavailableException("Status is currently '" + parentSensor.getStatus() + "'. New readings rejected.");
        }

        // Populate system-defined variables safely
        newReading.setId(UUID.randomUUID().toString());
        newReading.setTimestamp(System.currentTimeMillis());

        // Save reading to History list
        DataStore.getSensorReadings().get(activeSensorId).add(newReading);

        // Business Logic: Side Effect Requirements (Part 4.2)
        // Ensure that parent Sensor object correctly updates its `currentValue` cache!
        parentSensor.setCurrentValue(newReading.getValue());

        return Response.status(Response.Status.CREATED)
                .entity(newReading)
                .build();
    }
}
