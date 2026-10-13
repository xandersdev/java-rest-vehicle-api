package com.xandersdev.vehicleapi.server;

/** Starts the local REST API in demo or MySQL mode. */
public final class ServerApp {
    private ServerApp() {}

    public static void main(String[] args) throws Exception {
        boolean demo = args.length == 1 && "--demo".equals(args[0]);
        if (args.length > (demo ? 1 : 0)) {
            System.err.println("Usage: ServerApp [--demo]");
            System.exit(2);
        }
        VehicleRepository repository = demo
                ? new InMemoryVehicleRepository()
                : new JdbcVehicleRepository(
                    setting("DB_URL", "jdbc:mysql://localhost:3306/vehicle_api"),
                    setting("DB_USER", "root"), setting("DB_PASSWORD", ""));
        int port = Integer.parseInt(setting("API_PORT", "8080"));
        VehicleHttpServer server = new VehicleHttpServer(port, repository);
        server.start();
        System.out.println("Vehicle API running at http://localhost:" + server.port()
                + "/api/vehicles (" + (demo ? "demo" : "MySQL") + " mode)");
    }

    private static String setting(String name, String fallback) {
        String value = System.getenv(name);
        return value == null || value.isBlank() ? fallback : value;
    }
}
