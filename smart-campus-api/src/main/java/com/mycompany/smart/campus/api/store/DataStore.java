package com.mycompany.smart.campus.api.store;

import com.mycompany.smart.campus.api.model.Room;
import com.mycompany.smart.campus.api.model.Sensor;
import com.mycompany.smart.campus.api.model.SensorReading;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public final class DataStore {

    public static final Map<String, Room> ROOMS = new ConcurrentHashMap<>();
    public static final Map<String, Sensor> SENSORS = new ConcurrentHashMap<>();
    public static final Map<String, List<SensorReading>> READINGS = new ConcurrentHashMap<>();

    private DataStore() {
    }

    static {
        Room room1 = new Room("LIB-301", "Library Quiet Study", 40);
        Room room2 = new Room("ENG-101", "Engineering Lab", 25);

        ROOMS.put(room1.getId(), room1);
        ROOMS.put(room2.getId(), room2);

        READINGS.put("TEMP-001", new CopyOnWriteArrayList<>());
    }
}