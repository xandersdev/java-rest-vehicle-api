package com.xandersdev.vehicleapi.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.sql.SQLException;

/** An empty, temporary inventory for trying the API without MySQL. */
public final class InMemoryVehicleRepository implements VehicleRepository {
    private final Map<Integer, Vehicle> vehicles = new HashMap<>();

    @Override public synchronized List<Vehicle> findAll() { return new ArrayList<>(vehicles.values()); }
    @Override public synchronized Vehicle find(int id) { return vehicles.get(id); }
    @Override public synchronized boolean create(Vehicle vehicle) throws SQLException {
        if (vehicles.containsKey(vehicle.id)) return false;
        ensureUniqueRegistration(vehicle);
        vehicles.put(vehicle.id, vehicle);
        return true;
    }
    @Override public synchronized boolean update(Vehicle vehicle) throws SQLException {
        if (!vehicles.containsKey(vehicle.id)) return false;
        ensureUniqueRegistration(vehicle);
        vehicles.put(vehicle.id, vehicle);
        return true;
    }
    @Override public synchronized boolean delete(int id) { return vehicles.remove(id) != null; }

    private void ensureUniqueRegistration(Vehicle vehicle) throws SQLException {
        for (Vehicle existing : vehicles.values()) {
            if (existing.id != vehicle.id
                    && existing.registration.equalsIgnoreCase(vehicle.registration)) {
                throw new SQLException("Duplicate registration", "23000");
            }
        }
    }
}
