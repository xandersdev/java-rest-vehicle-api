package com.xandersdev.vehicleapi.server;

import java.sql.SQLException;
import java.util.List;

/** Storage operations used by the HTTP resource. */
public interface VehicleRepository {
    List<Vehicle> findAll() throws SQLException;
    Vehicle find(int id) throws SQLException;
    boolean create(Vehicle vehicle) throws SQLException;
    boolean update(Vehicle vehicle) throws SQLException;
    boolean delete(int id) throws SQLException;
}
