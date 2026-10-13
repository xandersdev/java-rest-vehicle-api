package com.xandersdev.vehicleapi.server;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Exercises CRUD and error responses through the real HTTP listener. */
class VehicleHttpServerTest {
    private VehicleHttpServer server;
    private final HttpClient http = HttpClient.newHttpClient();
    private String base;

    @BeforeEach void start() throws Exception {
        server = new VehicleHttpServer(0, new InMemoryVehicleRepository());
        server.start();
        base = "http://localhost:" + server.port() + "/api/vehicles";
    }

    @AfterEach void stop() { server.stop(); }

    @Test void supportsCrudAndRejectsMismatchedIds() throws Exception {
        String vehicle = """
                {"id":1,"brand":"Opel","model":"Corsa","date":"2020-01-10",
                 "registration":"1234ABC","price":14000.00}
                """;
        assertEquals(201, request("POST", "", vehicle).statusCode());
        assertEquals(409, request("POST", "", vehicle).statusCode());
        assertEquals(409, request("POST", "", vehicle.replace("\"id\":1", "\"id\":2")).statusCode());
        assertTrue(request("GET", "", null).body().contains("Corsa"));
        assertEquals(200, request("GET", "/1", null).statusCode());
        assertEquals(400, request("PUT", "/2", vehicle).statusCode());
        assertEquals(200, request("PUT", "/1", vehicle.replace("Corsa", "Astra")).statusCode());
        assertTrue(request("GET", "/1", null).body().contains("Astra"));
        assertEquals(204, request("DELETE", "/1", null).statusCode());
        assertEquals(404, request("GET", "/1", null).statusCode());
        assertEquals(404, request("DELETE", "/1", null).statusCode());
    }

    @Test void validatesDateAndMethod() throws Exception {
        String invalid = """
                {"id":1,"brand":"Opel","model":"Corsa","date":"yesterday",
                 "registration":"1234ABC","price":14000.00}
                """;
        assertEquals(400, request("POST", "", invalid).statusCode());
        assertEquals(405, request("PATCH", "", "{}").statusCode());
    }

    @Test void servesTheBrowserInterface() throws Exception {
        String origin = "http://localhost:" + server.port();
        HttpResponse<String> page = http.send(
                HttpRequest.newBuilder(URI.create(origin + "/")).GET().build(),
                HttpResponse.BodyHandlers.ofString());
        assertEquals(200, page.statusCode());
        assertTrue(page.headers().firstValue("Content-Type").orElse("").contains("text/html"));
        assertTrue(page.body().contains("Vehicle inventory"));
        assertTrue(page.body().contains("/api/vehicles"));
        assertTrue(page.body().contains("/assets/styles.css"));
        assertTrue(page.body().contains("/assets/garage-editorial.webp"));
        assertEquals(404, http.send(
                HttpRequest.newBuilder(URI.create(origin + "/missing")).GET().build(),
                HttpResponse.BodyHandlers.ofString()).statusCode());
        HttpResponse<String> styles = http.send(
                HttpRequest.newBuilder(URI.create(origin + "/assets/styles.css")).GET().build(),
                HttpResponse.BodyHandlers.ofString());
        assertEquals(200, styles.statusCode());
        assertTrue(styles.headers().firstValue("Content-Type").orElse("").contains("text/css"));
        assertTrue(styles.body().contains(".dialog"));
        HttpResponse<byte[]> image = http.send(
                HttpRequest.newBuilder(URI.create(origin + "/assets/garage-editorial.webp")).GET().build(),
                HttpResponse.BodyHandlers.ofByteArray());
        assertEquals(200, image.statusCode());
        assertEquals("image/webp", image.headers().firstValue("Content-Type").orElse(""));
        assertTrue(image.body().length > 100_000);
    }

    private HttpResponse<String> request(String method, String suffix, String body) throws Exception {
        HttpRequest request = HttpRequest.newBuilder(URI.create(base + suffix))
                .header("Content-Type", "application/json")
                .method(method, body == null ? HttpRequest.BodyPublishers.noBody()
                        : HttpRequest.BodyPublishers.ofString(body)).build();
        return http.send(request, HttpResponse.BodyHandlers.ofString());
    }
}
