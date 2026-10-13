package com.xandersdev.vehicleapi.server;

import java.math.BigDecimal;

/** A vehicle record shared through the API and stored in the inventory. */
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
}
