package com.example.smartpantrymanager.model;

import java.io.Serializable;

public class Pantryitem implements Serializable {
    private long id;
    private String name;
    private double quantity;
    private String unit;
    private String expiryDate;

    public Pantryitem(long id, String name, double quantity, String unit, String expiryDate) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
        this.expiryDate = expiryDate;
    }

    public Pantryitem(String name, double quantity, String unit, String expiryDate) {
        this(-1, name, quantity, unit, expiryDate);
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getName() { return name; }
        public double getQuantity() { return quantity; }
        public String getUnit() { return unit; }
        public String getExpiryDate() { return expiryDate; }
    }
