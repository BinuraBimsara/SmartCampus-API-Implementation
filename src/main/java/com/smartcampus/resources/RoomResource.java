package com.smartcampus.resources;

import com.smartcampus.exceptions.RoomNotEmptyException;
import com.smartcampus.models.Room;
import com.smartcampus.store.DataStore;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller/Resource mapping to /api/v1/rooms path
 */
@Path("/rooms")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RoomResource {

    // GET /api/v1/rooms : Comprehensive list of all rooms
    @GET
    public Response getAllRooms() {
        List<Room> roomList = new ArrayList<>(DataStore.getRooms().values());
        return Response.ok(roomList).build();
    }

    // POST /api/v1/rooms : Enable creation of new rooms
    @POST
    public Response createRoom(Room newRoom) {
        if (newRoom == null || newRoom.getId() == null || newRoom.getId().trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"Room ID is required.\"}")
                    .build();
        }

        // Avoid duplicates
        if (DataStore.getRooms().containsKey(newRoom.getId())) {
             return Response.status(Response.Status.CONFLICT)
                    .entity("{\"error\": \"Room with ID '" + newRoom.getId() + "' already exists.\"}")
                    .build();
        }
        
        DataStore.getRooms().put(newRoom.getId(), newRoom);
        
        // Add Location header for 201 Created response
        java.net.URI location = jakarta.ws.rs.core.UriBuilder.fromResource(RoomResource.class)
                                .path(newRoom.getId())
                                .build();
                                
        return Response.created(location)
                .entity(newRoom)
                .build();
    }

    // GET /api/v1/rooms/{roomId} : Details for a specific room
    @GET
    @Path("/{roomId}")
    public Response getRoomById(@PathParam("roomId") String roomId) {
        Room room = DataStore.getRooms().get(roomId);
        if (room == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\": \"Room with ID '" + roomId + "' not found.\"}")
                    .build();
        }
        return Response.ok(room).build();
    }

    // DELETE /api/v1/rooms/{roomId} : Deleting a room with Safety Logic Constraint (409 Conflict)
    @DELETE
    @Path("/{roomId}")
    public Response deleteRoom(@PathParam("roomId") String roomId) {
        Room room = DataStore.getRooms().get(roomId);
        
        // Idempotency: If room doesn't exist, simply return 404
        if (room == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\": \"Room with ID '" + roomId + "' already deleted or not found.\"}")
                    .build();
        }
        
        // Business Logic Constraint: The room cannot be deleted if it still has active sensors.
        // It must throw the custom Exception that maps to HTTP 409.
        if (room.getSensorIds() != null && !room.getSensorIds().isEmpty()) {
             throw new RoomNotEmptyException("Room cannot be decommissioned because it contains assigned sensors. Remove sensors before deleting.");
        }

        // Safe to delete
        DataStore.getRooms().remove(roomId);
        return Response.noContent().build(); 
    }
}
