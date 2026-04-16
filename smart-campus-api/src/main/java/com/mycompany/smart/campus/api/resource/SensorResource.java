package com.mycompany.smart.campus.api.resource;

import com.mycompany.smart.campus.api.exception.LinkedResourceNotFoundException;
import com.mycompany.smart.campus.api.model.Room;
import com.mycompany.smart.campus.api.model.Sensor;
import com.mycompany.smart.campus.api.store.DataStore;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/sensors")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorResource {

    @GET
    public List<Sensor> getSensors(@QueryParam("type") String type) {
        List<Sensor> sensors = new ArrayList<>(DataStore.SENSORS.values());

        if (type == null || type.isBlank()) {
            return sensors;
        }

        return sensors.stream()
                .filter(sensor -> sensor.getType() != null
                        && sensor.getType().equalsIgnoreCase(type))
                .collect(Collectors.toList());
    }

    @POST
    public Response createSensor(Sensor sensor) {
        if (sensor == null) {
            throw new WebApplicationException("Request body is required.", Response.Status.BAD_REQUEST);
        }

        if (sensor.getId() == null || sensor.getId().isBlank()) {
            throw new WebApplicationException("Sensor id is required.", Response.Status.BAD_REQUEST);
        }

        if (sensor.getType() == null || sensor.getType().isBlank()) {
            throw new WebApplicationException("Sensor type is required.", Response.Status.BAD_REQUEST);
        }

        if (sensor.getRoomId() == null || sensor.getRoomId().isBlank()) {
            throw new WebApplicationException("roomId is required.", Response.Status.BAD_REQUEST);
        }

        if (DataStore.SENSORS.containsKey(sensor.getId())) {
            throw new WebApplicationException("A sensor with that id already exists.", Response.Status.CONFLICT);
        }

        Room room = DataStore.ROOMS.get(sensor.getRoomId());
        if (room == null) {
            throw new LinkedResourceNotFoundException(
                    "Cannot create sensor because room does not exist: " + sensor.getRoomId()
            );
        }

        if (sensor.getStatus() == null || sensor.getStatus().isBlank()) {
            sensor.setStatus("ACTIVE");
        }

        DataStore.SENSORS.put(sensor.getId(), sensor);
        room.getSensorIds().add(sensor.getId());
        DataStore.READINGS.put(sensor.getId(), new CopyOnWriteArrayList<>());

        return Response.status(Response.Status.CREATED).entity(sensor).build();
    }

    @GET
    @Path("/{sensorId}")
    public Sensor getSensorById(@PathParam("sensorId") String sensorId) {
        Sensor sensor = DataStore.SENSORS.get(sensorId);

        if (sensor == null) {
            throw new NotFoundException("Sensor not found: " + sensorId);
        }

        return sensor;
    }

    @Path("/{sensorId}/readings")
    public SensorReadingResource getSensorReadingResource(@PathParam("sensorId") String sensorId) {
        if (!DataStore.SENSORS.containsKey(sensorId)) {
            throw new NotFoundException("Sensor not found: " + sensorId);
        }

        return new SensorReadingResource(sensorId);
    }
}