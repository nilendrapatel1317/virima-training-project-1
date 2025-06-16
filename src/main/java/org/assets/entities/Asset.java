package org.assets.entities;

public class Asset {
    private int id;
    private String name;
    private String type;
    private double value;

    public Asset(int id, String name, String type, double value) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.value = value;
    }

    public Asset(String name, String type, double value) {
        this.name = name;
        this.type = type;
        this.value = value;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getType() { return type; }
    public double getValue() { return value; }

    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setType(String type) { this.type = type; }
    public void setValue(double value) { this.value = value; }

    @Override
    public String toString() {
        return id + " | " + name + " | " + type + " | " + value;
    }
}
