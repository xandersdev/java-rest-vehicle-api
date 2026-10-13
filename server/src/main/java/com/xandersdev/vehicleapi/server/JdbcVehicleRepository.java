package com.xandersdev.vehicleapi.server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/** MySQL-backed inventory with parameterized SQL and short-lived connections. */
public final class JdbcVehicleRepository implements VehicleRepository {
    private static final String COLUMNS = "id, brand, model, type, color, engine, displacement, vehicle_date, registration, extras, price";
    private final String url;
    private final String user;
    private final String password;

    public JdbcVehicleRepository(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
    }

    private Connection connect() throws SQLException { return DriverManager.getConnection(url, user, password); }

    @Override public List<Vehicle> findAll() throws SQLException {
        List<Vehicle> vehicles = new ArrayList<>();
        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement("SELECT " + COLUMNS + " FROM vehicles ORDER BY id");
             ResultSet rows = statement.executeQuery()) {
            while (rows.next()) vehicles.add(read(rows));
        }
        return vehicles;
    }

    @Override public Vehicle find(int id) throws SQLException {
        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement("SELECT " + COLUMNS + " FROM vehicles WHERE id = ?")) {
            statement.setInt(1, id);
            try (ResultSet rows = statement.executeQuery()) {
                return rows.next() ? read(rows) : null;
            }
        }
    }

    @Override public boolean create(Vehicle vehicle) throws SQLException {
        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement(
                 "INSERT INTO vehicles (" + COLUMNS + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
            bind(statement, vehicle);
            return statement.executeUpdate() == 1;
        }
    }

    @Override public boolean update(Vehicle vehicle) throws SQLException {
        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement(
                 "UPDATE vehicles SET brand=?, model=?, type=?, color=?, engine=?, displacement=?, vehicle_date=?, registration=?, extras=?, price=? WHERE id=?")) {
            statement.setString(1, vehicle.brand);
            statement.setString(2, vehicle.model);
            statement.setString(3, vehicle.type);
            statement.setString(4, vehicle.color);
            statement.setString(5, vehicle.engine);
            statement.setObject(6, vehicle.displacement);
            statement.setDate(7, java.sql.Date.valueOf(vehicle.date));
            statement.setString(8, vehicle.registration);
            statement.setString(9, vehicle.extras);
            statement.setBigDecimal(10, vehicle.price);
            statement.setInt(11, vehicle.id);
            return statement.executeUpdate() == 1;
        }
    }

    @Override public boolean delete(int id) throws SQLException {
        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement("DELETE FROM vehicles WHERE id = ?")) {
            statement.setInt(1, id);
            return statement.executeUpdate() == 1;
        }
    }

    private static void bind(PreparedStatement statement, Vehicle vehicle) throws SQLException {
        statement.setInt(1, vehicle.id);
        statement.setString(2, vehicle.brand);
        statement.setString(3, vehicle.model);
        statement.setString(4, vehicle.type);
        statement.setString(5, vehicle.color);
        statement.setString(6, vehicle.engine);
        statement.setObject(7, vehicle.displacement);
        statement.setDate(8, java.sql.Date.valueOf(vehicle.date));
        statement.setString(9, vehicle.registration);
        statement.setString(10, vehicle.extras);
        statement.setBigDecimal(11, vehicle.price);
    }

    private static Vehicle read(ResultSet rows) throws SQLException {
        Vehicle vehicle = new Vehicle();
        vehicle.id = rows.getInt("id");
        vehicle.brand = rows.getString("brand");
        vehicle.model = rows.getString("model");
        vehicle.type = rows.getString("type");
        vehicle.color = rows.getString("color");
        vehicle.engine = rows.getString("engine");
        vehicle.displacement = (Integer) rows.getObject("displacement");
        vehicle.date = rows.getDate("vehicle_date").toLocalDate().toString();
        vehicle.registration = rows.getString("registration");
        vehicle.extras = rows.getString("extras");
        vehicle.price = rows.getBigDecimal("price");
        return vehicle;
    }
}
