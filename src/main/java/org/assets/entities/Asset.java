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
    private int updateCount;

    public Asset(int id, String name, String type, double value, boolean active, java.sql.Timestamp createdAt, java.sql.Timestamp updatedAt, boolean isArchived, boolean isDeleted, String location, String createdBy, String updatedBy, int updateCount) {
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
        this.updateCount = updateCount;
    }

    public Asset(String name, String type, double value) {
        this.name = name;
        this.type = type;
        this.value = value;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public java.sql.Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(java.sql.Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public java.sql.Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(java.sql.Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    public boolean isArchived() {
        return isArchived;
    }

    public void setArchived(boolean archived) {
        isArchived = archived;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public int getUpdateCount() {
        return updateCount;
    }

    public void setUpdateCount(int updateCount) {
        this.updateCount = updateCount;
    }

    @Override
    public String toString() {
        return String.format("| %-2d | %-13s | %-11s | %-8.0f | %-8s | %-20s | %-20s | %-9s | %-9s | %-14s | %-14s | %-10d |",
                id, name, type, value, active ? "Active" : "Inactive", String.valueOf(createdAt), String.valueOf(updatedAt), isArchived, isDeleted, location, createdBy, updatedBy, updateCount);
    }

}


