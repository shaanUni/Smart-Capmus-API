package com.mycompany.smart.campus.api.resource;

import com.mycompany.smart.campus.api.exception.RoomNotEmptyException;
import com.mycompany.smart.campus.api.model.Room;
import com.mycompany.smart.campus.api.store.DataStore;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/rooms")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RoomResource {

    @GET
    public List<Room> getAllRooms() {
        return new ArrayList<>(DataStore.ROOMS.values());
    }

    @POST
    public Response createRoom(Room room) {
        if (room == null || room.getId() == null || room.getId().isBlank()) {
            throw new WebApplicationException("Room id is required.", Response.Status.BAD_REQUEST);
        }

        if (DataStore.ROOMS.containsKey(room.getId())) {
            throw new WebApplicationException("A room with that id already exists.", Response.Status.CONFLICT);
        }

        if (room.getSensorIds() == null) {
            room.setSensorIds(new ArrayList<>());
        }

        DataStore.ROOMS.put(room.getId(), room);
        return Response.status(Response.Status.CREATED).entity(room).build();
    }

    @GET
    @Path("/{roomId}")
    public Room getRoomById(@PathParam("roomId") String roomId) {
        Room room = DataStore.ROOMS.get(roomId);

        if (room == null) {
            throw new NotFoundException("Room not found: " + roomId);
        }

        return room;
    }

    @DELETE
    @Path("/{roomId}")
    public Response deleteRoom(@PathParam("roomId") String roomId) {
        Room room = DataStore.ROOMS.get(roomId);

        if (room == null) {
            throw new NotFoundException("Room not found: " + roomId);
        }

        if (room.getSensorIds() != null && !room.getSensorIds().isEmpty()) {
            throw new RoomNotEmptyException("Room " + roomId + " cannot be deleted because it still has sensors assigned.");
        }

        DataStore.ROOMS.remove(roomId);
        return Response.noContent().build();
    }
}