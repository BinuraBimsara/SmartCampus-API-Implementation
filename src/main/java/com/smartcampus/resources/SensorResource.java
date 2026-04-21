package com.smartcampus.resources;

import com.smartcampus.exceptions.LinkedResourceNotFoundException;
import com.smartcampus.models.Room;
import com.smartcampus.models.Sensor;
import com.smartcampus.store.DataStore;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller/Resource mapping to /api/v1/sensors path
 */
@Path("/sensors")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorResource {

    // GET /api/v1/sensors: Get all sensors, optionally filtered by type
    @GET
    public Response getSensors(@QueryParam("type") String type) {
        List<Sensor> allSensors = new ArrayList<>(DataStore.getSensors().values());
        
        // Filter by Query Parameter if provided
        if (type != null && !type.trim().isEmpty()) {
            List<Sensor> filteredSensors = allSensors.stream()
                    .filter(s -> type.equalsIgnoreCase(s.getType()))
                    .collect(Collectors.toList());
            return Response.ok(filteredSensors).build();
        }
        
        return Response.ok(allSensors).build();
    }

    // POST /api/v1/sensors: Register a new sensor
    @POST
    public Response createSensor(Sensor newSensor) {
        if (newSensor == null || newSensor.getId() == null || newSensor.getRoomId() == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"Sensor ID and roomId are required fields.\"}")
                    .build();
        }
        
        // Avoid duplicates
        if (DataStore.getSensors().containsKey(newSensor.getId())) {
             return Response.status(Response.Status.CONFLICT)
                    .entity("{\"error\": \"Sensor with ID '" + newSensor.getId() + "' already exists.\"}")
                    .build();
        }

        // Dependency Validation Constraint
        Room targetRoom = DataStore.getRooms().get(newSensor.getRoomId());
        if (targetRoom == null) {
             // Throws custom 422 Unprocessable Entity
             throw new LinkedResourceNotFoundException("Validation Failed: The 'roomId' (" + newSensor.getRoomId() + ") does not exist.");
        }

        // Setup newly registered sensor
        newSensor.setCurrentValue(0.0); // No readings yet
        DataStore.getSensors().put(newSensor.getId(), newSensor);
        
        // Maintain bi-directional relationship: Add sensor ID to Room
        targetRoom.getSensorIds().add(newSensor.getId());
        
        // Initialize an empty reading list for this sensor
        DataStore.getSensorReadings().putIfAbsent(newSensor.getId(), new ArrayList<>());

        java.net.URI location = jakarta.ws.rs.core.UriBuilder.fromResource(SensorResource.class)
                .path(newSensor.getId())
                .build();

        return Response.created(location)
                .entity(newSensor)
                .build();
    }
    
    // GET /api/v1/sensors/{sensorId} : Simple metadata fetch for a specific sensor (Bonus utility)
    @GET
    @Path("/{sensorId}")
    public Response getSensorById(@PathParam("sensorId") String sensorId) {
        Sensor sensor = DataStore.getSensors().get(sensorId);
        if (sensor == null) {
            return Response.status(Response.Status.NOT_FOUND)
                     .entity("{\"error\": \"Sensor not found.\"}")
                     .build();
        }
        return Response.ok(sensor).build();
    }

    /**
     * Sub-Resource Locator for nested `/api/v1/sensors/{sensorId}/readings`
     * (Task: Part 4.1 "The Sub-Resource Locator Pattern")
     */
    @Path("/{sensorId}/readings")
    public SensorReadingResource getReadingResource(@PathParam("sensorId") String sensorId) {
        // Instantiate and delegate to the specialized SensorReadingResource, 
        // passing down the required `{sensorId}` context to bind it logically.
        return new SensorReadingResource(sensorId);
    }
}
