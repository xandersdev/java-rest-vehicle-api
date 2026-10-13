package com.xandersdev.vehicleapi.server;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Map;
import java.util.Objects;

/** Maps vehicle collection and item URLs to repository operations. */
public final class VehicleHttpServer {
    private static final String PATH = "/api/vehicles";
    private static final Map<String, String> ASSETS = Map.of(
            "styles.css", "text/css; charset=UTF-8",
            "garage-editorial.webp", "image/webp",
            "fonts/Newsreader.ttf", "font/ttf",
            "fonts/Barlow-Regular.ttf", "font/ttf",
            "fonts/Barlow-SemiBold.ttf", "font/ttf");
    private final HttpServer server;
    private final VehicleRepository repository;
    private final Gson gson = new Gson();

    public VehicleHttpServer(int port, VehicleRepository repository) throws IOException {
        this.repository = Objects.requireNonNull(repository);
        server = HttpServer.create(new InetSocketAddress("localhost", port), 0);
        server.createContext(PATH, this::handle);
        server.createContext("/assets/", this::handleAsset);
        server.createContext("/", this::handlePage);
    }

    public void start() { server.start(); }
    public void stop() { server.stop(0); }
    public int port() { return server.getAddress().getPort(); }

    private void handleAsset(HttpExchange exchange) throws IOException {
        try {
            String path = exchange.getRequestURI().getPath();
            String name = path.substring("/assets/".length());
            String contentType = ASSETS.get(name);
            if (contentType == null) {
                respond(exchange, 404, error("Asset not found"));
                return;
            }
            if (!"GET".equals(exchange.getRequestMethod())) {
                methodNotAllowed(exchange, "GET");
                return;
            }
            try (InputStream asset = VehicleHttpServer.class.getResourceAsStream("/web/assets/" + name)) {
                if (asset == null) {
                    respond(exchange, 500, error("Asset is unavailable"));
                    return;
                }
                byte[] bytes = asset.readAllBytes();
                exchange.getResponseHeaders().set("Content-Type", contentType);
                exchange.getResponseHeaders().set("Cache-Control", "public, max-age=86400");
                exchange.sendResponseHeaders(200, bytes.length);
                exchange.getResponseBody().write(bytes);
            }
        } finally {
            exchange.close();
        }
    }

    private void handlePage(HttpExchange exchange) throws IOException {
        try {
            String path = exchange.getRequestURI().getPath();
            if (!"/".equals(path) && !"/index.html".equals(path)) {
                respond(exchange, 404, error("Route not found"));
                return;
            }
            if (!"GET".equals(exchange.getRequestMethod())) {
                methodNotAllowed(exchange, "GET");
                return;
            }
            try (InputStream page = VehicleHttpServer.class.getResourceAsStream("/web/index.html")) {
                if (page == null) {
                    respond(exchange, 500, error("Interface is unavailable"));
                    return;
                }
                byte[] bytes = page.readAllBytes();
                exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
                exchange.getResponseHeaders().set("Cache-Control", "no-store");
                exchange.sendResponseHeaders(200, bytes.length);
                exchange.getResponseBody().write(bytes);
            }
        } finally {
            exchange.close();
        }
    }

    private void handle(HttpExchange exchange) throws IOException {
        try {
            route(exchange);
        } catch (IllegalArgumentException | JsonParseException exception) {
            respond(exchange, 400, error(exception.getMessage()));
        } catch (SQLException exception) {
            int status = "23000".equals(exception.getSQLState()) ? 409 : 500;
            respond(exchange, status, error(status == 409 ? "Vehicle ID or registration already exists" : "Database error"));
        } catch (RuntimeException exception) {
            respond(exchange, 500, error("Unexpected server error"));
        } finally {
            exchange.close();
        }
    }

    private void route(HttpExchange exchange) throws IOException, SQLException {
        String path = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod();
        if (PATH.equals(path)) {
            switch (method) {
                case "GET" -> respond(exchange, 200, gson.toJson(repository.findAll()));
                case "POST" -> {
                    Vehicle vehicle = readVehicle(exchange);
                    if (repository.create(vehicle)) respond(exchange, 201, gson.toJson(vehicle));
                    else respond(exchange, 409, error("Vehicle ID already exists"));
                }
                default -> methodNotAllowed(exchange, "GET, POST");
            }
            return;
        }
        if (!path.startsWith(PATH + "/") || path.indexOf('/', PATH.length() + 1) >= 0) {
            respond(exchange, 404, error("Route not found"));
            return;
        }
        int id;
        try {
            id = Integer.parseInt(path.substring(PATH.length() + 1));
            if (id <= 0) throw new NumberFormatException();
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Vehicle ID must be a positive integer");
        }
        switch (method) {
            case "GET" -> {
                Vehicle vehicle = repository.find(id);
                respond(exchange, vehicle == null ? 404 : 200,
                        vehicle == null ? error("Vehicle not found") : gson.toJson(vehicle));
            }
            case "PUT" -> {
                Vehicle vehicle = readVehicle(exchange);
                if (vehicle.id != id) throw new IllegalArgumentException("Body ID must match URL ID");
                boolean updated = repository.update(vehicle);
                respond(exchange, updated ? 200 : 404,
                        updated ? gson.toJson(vehicle) : error("Vehicle not found"));
            }
            case "DELETE" -> respond(exchange, repository.delete(id) ? 204 : 404,
                    error("Vehicle not found"));
            default -> methodNotAllowed(exchange, "GET, PUT, DELETE");
        }
    }

    private Vehicle readVehicle(HttpExchange exchange) throws IOException {
        if (!String.valueOf(exchange.getRequestHeaders().getFirst("Content-Type"))
                .toLowerCase().startsWith("application/json")) {
            throw new IllegalArgumentException("Content-Type must be application/json");
        }
        byte[] bytes = exchange.getRequestBody().readNBytes(65537);
        if (bytes.length > 65536) throw new IllegalArgumentException("Request body is too large");
        Vehicle vehicle = gson.fromJson(new String(bytes, StandardCharsets.UTF_8), Vehicle.class);
        if (vehicle == null || vehicle.id <= 0 || blank(vehicle.brand) || blank(vehicle.model)
                || blank(vehicle.date) || blank(vehicle.registration) || vehicle.price == null
                || vehicle.price.signum() < 0 || vehicle.displacement != null && vehicle.displacement < 0) {
            throw new IllegalArgumentException("ID, brand, model, date, registration and non-negative price are required");
        }
        try { LocalDate.parse(vehicle.date); }
        catch (DateTimeParseException exception) { throw new IllegalArgumentException("Date must use YYYY-MM-DD"); }
        return vehicle;
    }

    private static boolean blank(String value) { return value == null || value.isBlank(); }
    private String error(String message) { return gson.toJson(new ErrorResponse(message)); }

    private void methodNotAllowed(HttpExchange exchange, String allowed) throws IOException {
        exchange.getResponseHeaders().set("Allow", allowed);
        respond(exchange, 405, error("Method not allowed"));
    }

    private void respond(HttpExchange exchange, int status, String json) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        if (status == 204) {
            exchange.sendResponseHeaders(status, -1);
            return;
        }
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(status, bytes.length);
        exchange.getResponseBody().write(bytes);
    }

    private record ErrorResponse(String error) {}
}
