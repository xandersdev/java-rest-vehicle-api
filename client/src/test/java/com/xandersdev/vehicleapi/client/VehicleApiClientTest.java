package com.xandersdev.vehicleapi.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

/** Verifies item paths, JSON responses and API error propagation. */
class VehicleApiClientTest {
    @Test void usesItemRouteAndReportsHttpErrors() throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress("localhost", 0), 0);
        server.createContext("/api/vehicles", exchange -> {
            String path = exchange.getRequestURI().getPath();
            int status = path.equals("/api/vehicles/7") ? 200 : 404;
            String json = status == 200
                    ? "{\"id\":7,\"brand\":\"Ford\",\"model\":\"Focus\",\"date\":\"2020-01-10\",\"registration\":\"1234ABC\",\"price\":14000}"
                    : "{\"error\":\"Vehicle not found\"}";
            byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(status, bytes.length);
            exchange.getResponseBody().write(bytes);
            exchange.close();
        });
        server.start();
        try {
            VehicleApiClient client = new VehicleApiClient(
                    "http://localhost:" + server.getAddress().getPort() + "/api/vehicles");
            assertEquals("Ford", client.find(7).brand);
            IOException error = assertThrows(IOException.class, () -> client.find(8));
            assertTrue(error.getMessage().contains("HTTP 404"));
        } finally {
            server.stop(0);
        }
    }
}
