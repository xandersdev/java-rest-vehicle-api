package com.xandersdev.vehicleapi.client;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

/** Interactive command-line client for the vehicle API. */
public final class ClientApp {
    private ClientApp() {}

    public static void main(String[] args) {
        String baseUrl = System.getenv().getOrDefault("API_URL", "http://localhost:8080/api/vehicles");
        VehicleApiClient api = new VehicleApiClient(baseUrl);
        try (Scanner input = new Scanner(System.in)) {
            while (true) {
                System.out.println("\n1. Add vehicle  2. List vehicles  3. Find vehicle");
                System.out.println("4. Update vehicle  5. Delete vehicle  6. Exit");
                String option = prompt(input, "Choose an option");
                if ("6".equals(option)) return;
                try {
                    switch (option) {
                        case "1" -> System.out.println("Created: " + api.create(readVehicle(input, null)));
                        case "2" -> api.findAll().forEach(System.out::println);
                        case "3" -> System.out.println(api.find(readInt(input, "Vehicle ID")));
                        case "4" -> {
                            int id = readInt(input, "Vehicle ID to update");
                            System.out.println("Updated: " + api.update(readVehicle(input, id)));
                        }
                        case "5" -> {
                            int id = readInt(input, "Vehicle ID to delete");
                            api.delete(id);
                            System.out.println("Vehicle deleted.");
                        }
                        default -> System.out.println("Choose a number from 1 to 6.");
                    }
                } catch (IOException | IllegalArgumentException exception) {
                    System.err.println(exception.getMessage());
                } catch (InterruptedException exception) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }
    }

    private static Vehicle readVehicle(Scanner input, Integer existingId) {
        Vehicle vehicle = new Vehicle();
        vehicle.id = existingId == null ? readInt(input, "Vehicle ID") : existingId;
        vehicle.brand = prompt(input, "Brand");
        vehicle.model = prompt(input, "Model");
        vehicle.type = prompt(input, "Type (optional)");
        vehicle.color = prompt(input, "Color (optional)");
        vehicle.engine = prompt(input, "Engine (optional)");
        String displacement = prompt(input, "Displacement in cc (optional)");
        vehicle.displacement = displacement.isBlank() ? null : Integer.valueOf(displacement);
        vehicle.date = prompt(input, "Date (YYYY-MM-DD)");
        try { LocalDate.parse(vehicle.date); }
        catch (DateTimeParseException exception) { throw new IllegalArgumentException("Use YYYY-MM-DD for the date."); }
        vehicle.registration = prompt(input, "Registration");
        vehicle.extras = prompt(input, "Extras (optional)");
        vehicle.price = new BigDecimal(prompt(input, "Price"));
        return vehicle;
    }

    private static int readInt(Scanner input, String label) {
        return Integer.parseInt(prompt(input, label));
    }

    private static String prompt(Scanner input, String label) {
        System.out.print(label + ": ");
        return input.nextLine().trim();
    }
}
