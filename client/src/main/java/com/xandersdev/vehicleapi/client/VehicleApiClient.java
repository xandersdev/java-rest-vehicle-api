package com.xandersdev.vehicleapi.client;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;

/** Small HTTP client for the vehicle collection and item endpoints. */
public final class VehicleApiClient {
    private static final Type VEHICLE_LIST = new TypeToken<List<Vehicle>>() {}.getType();
    private final HttpClient http = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();
    private final Gson gson = new Gson();
    private final URI baseUri;

    public VehicleApiClient(String baseUrl) {
        this.baseUri = URI.create(baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl);
    }

    public List<Vehicle> findAll() throws IOException, InterruptedException {
        return gson.fromJson(send("GET", "", null).body(), VEHICLE_LIST);
    }

    public Vehicle find(int id) throws IOException, InterruptedException {
        return gson.fromJson(send("GET", "/" + id, null).body(), Vehicle.class);
    }

    public Vehicle create(Vehicle vehicle) throws IOException, InterruptedException {
        return gson.fromJson(send("POST", "", vehicle).body(), Vehicle.class);
    }

    public Vehicle update(Vehicle vehicle) throws IOException, InterruptedException {
        return gson.fromJson(send("PUT", "/" + vehicle.id, vehicle).body(), Vehicle.class);
    }

    public void delete(int id) throws IOException, InterruptedException {
        send("DELETE", "/" + id, null);
    }

    private HttpResponse<String> send(String method, String suffix, Vehicle vehicle)
            throws IOException, InterruptedException {
        HttpRequest.BodyPublisher body = vehicle == null
                ? HttpRequest.BodyPublishers.noBody()
                : HttpRequest.BodyPublishers.ofString(gson.toJson(vehicle), StandardCharsets.UTF_8);
        HttpRequest request = HttpRequest.newBuilder(baseUri.resolve(baseUri.getPath() + suffix))
                .timeout(Duration.ofSeconds(15))
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .method(method, body).build();
        HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new IOException("API returned HTTP " + response.statusCode() + ": " + response.body());
        }
        return response;
    }
}
