package com.smartcampus.store;

import com.smartcampus.models.Room;
import com.smartcampus.models.Sensor;
import com.smartcampus.models.SensorReading;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A central, thread-safe, in-memory data store.
 * Acts as our database, utilizing ConcurrentHashMap to prevent thread-safety
 * (concurrency) issues given the JAX-RS default per-request lifecycle.
 */
public class DataStore {
    
    // In-memory collections to act as our mock database
    private static final Map<String, Room> rooms = new ConcurrentHashMap<>();
    private static final Map<String, Sensor> sensors = new ConcurrentHashMap<>();
    private static final Map<String, List<SensorReading>> sensorReadings = new ConcurrentHashMap<>();

    // Expose Maps and Lists for controllers/resources to query
    public static Map<String, Room> getRooms() {
        return rooms;
    }

    public static Map<String, Sensor> getSensors() {
        return sensors;
    }

    public static Map<String, List<SensorReading>> getSensorReadings() {
        return sensorReadings;
    }

    // Optional: Include dummy data for easy testing during development
    static {
        Room lib = new Room("LIB-301", "Library Quiet Study", 50);
        rooms.put(lib.getId(), lib);

        Sensor temp = new Sensor("TEMP-001", "Temperature", "ACTIVE", "LIB-301");
        temp.setCurrentValue(22.5);
        sensors.put(temp.getId(), temp);
        
        lib.getSensorIds().add("TEMP-001");
        sensorReadings.put("TEMP-001", new ArrayList<>());
        sensorReadings.get("TEMP-001").add(new SensorReading(22.5));
    }
}
