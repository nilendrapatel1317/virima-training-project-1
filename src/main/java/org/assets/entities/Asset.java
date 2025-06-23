package org.assets.entities;

public class Asset {
    private int id;
    private String name;
    private String type;
    private double value;
    private boolean active;
    private java.sql.Timestamp createdAt;
    private java.sql.Timestamp updatedAt;
    private boolean isArchived;
    private boolean isDeleted;
    private String location;
    private String createdBy;
    private String updatedBy;

    public Asset(int id, String name, String type, double value, boolean active, java.sql.Timestamp createdAt, java.sql.Timestamp updatedAt, boolean isArchived, boolean isDeleted, String location, String createdBy, String updatedBy) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.value = value;
        this.active = active;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.isArchived = isArchived;
        this.isDeleted = isDeleted;
        this.location = location;
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
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
    public java.sql.Timestamp getCreatedAt() { return createdAt; }
    public java.sql.Timestamp getUpdatedAt() { return updatedAt; }
    public boolean isArchived() { return isArchived; }
    public boolean isDeleted() { return isDeleted; }
    public String getLocation() { return location; }
    public String getCreatedBy() { return createdBy; }
    public String getUpdatedBy() { return updatedBy; }

    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setType(String type) { this.type = type; }
    public void setValue(double value) { this.value = value; }
    public void setActive(boolean active) { this.active = active; }
    public void setCreatedAt(java.sql.Timestamp createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(java.sql.Timestamp updatedAt) { this.updatedAt = updatedAt; }
    public void setArchived(boolean archived) { isArchived = archived; }
    public void setDeleted(boolean deleted) { isDeleted = deleted; }
    public void setLocation(String location) { this.location = location; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }

    @Override
    public String toString() {
        return String.format("| %-2d | %-13s | %-11s | %-8.0f | %-10s | %-16s | %-19s | %-16s | %-19s | %-8s |",
                id, name, type, value, location, createdBy, String.valueOf(createdAt), updatedBy, String.valueOf(updatedAt), active ? "Active" : "Inactive");
    }

}


