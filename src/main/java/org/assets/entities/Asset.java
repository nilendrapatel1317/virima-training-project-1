
package org.assets.entities;

public class Asset {
    private int id;
    private String name;
    private String type;
    private double value;
    private boolean active;

    public Asset(int id, String name, String type, double value, boolean active) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.value = value;
        this.active = active;
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
    public boolean isActive() { return active; }

    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setType(String type) { this.type = type; }
    public void setValue(double value) { this.value = value; }
    public void setActive(boolean active) { this.active = active; }

    @Override
    public String toString() {
        return id + " | " + name + " | " + type + " | " + value  + " | " + (active);
    }
}


