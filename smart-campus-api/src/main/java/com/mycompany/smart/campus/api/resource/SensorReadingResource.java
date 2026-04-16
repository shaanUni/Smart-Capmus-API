package com.mycompany.smart.campus.api.resource;

import com.mycompany.smart.campus.api.exception.SensorUnavailableException;
import com.mycompany.smart.campus.api.model.Sensor;
import com.mycompany.smart.campus.api.model.SensorReading;
import com.mycompany.smart.campus.api.store.DataStore;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorReadingResource {

    private final String sensorId;

    public SensorReadingResource(String sensorId) {
        this.sensorId = sensorId;
    }

    @GET
    public List<SensorReading> getReadings() {
        if (!DataStore.SENSORS.containsKey(sensorId)) {
            throw new NotFoundException("Sensor not found: " + sensorId);
        }

        return new ArrayList<>(DataStore.READINGS.getOrDefault(sensorId, new ArrayList<>()));
    }

    @POST
    public Response addReading(SensorReading reading) {
        Sensor sensor = DataStore.SENSORS.get(sensorId);

        if (sensor == null) {
            throw new NotFoundException("Sensor not found: " + sensorId);
        }

        if ("MAINTENANCE".equalsIgnoreCase(sensor.getStatus())) {
            throw new SensorUnavailableException(
                    "Sensor " + sensorId + " is in MAINTENANCE and cannot accept readings."
            );
        }

        if (reading == null) {
            throw new WebApplicationException("Reading body is required.", Response.Status.BAD_REQUEST);
        }

        if (reading.getId() == null || reading.getId().isBlank()) {
            reading.setId(UUID.randomUUID().toString());
        }

        if (reading.getTimestamp() == 0L) {
            reading.setTimestamp(System.currentTimeMillis());
        }

        DataStore.READINGS
                .computeIfAbsent(sensorId, key -> new CopyOnWriteArrayList<>())
                .add(reading);

        sensor.setCurrentValue(reading.getValue());

        return Response.status(Response.Status.CREATED).entity(reading).build();
    }
}