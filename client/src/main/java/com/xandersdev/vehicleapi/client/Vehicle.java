package com.xandersdev.vehicleapi.client;

import java.math.BigDecimal;

/** JSON representation of one vehicle returned by the API. */
public final class Vehicle {
    public int id;
    public String brand;
    public String model;
    public String type;
    public String color;
    public String engine;
    public Integer displacement;
    public String date;
    public String registration;
    public String extras;
    public BigDecimal price;

    @Override public String toString() {
        return "Vehicle #" + id + " | " + brand + " " + model + " | " + registration
                + " | " + price + " | " + date + " | " + color + " | " + type
                + " | " + engine + " | " + displacement + " cc | " + extras;
    }
}
