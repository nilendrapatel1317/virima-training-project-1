package org.assets.entities;

public class Asset {
    private int id;
    private String name;
    private String type;
    private String description;
    private String category;
    private String department;
    private String model;
    private String serialNumber;
    private double originalValue;
    private double purchasedValue;
    private String location;
    private String createdBy;
    private java.sql.Timestamp createdAt;
    private String updatedBy;
    private java.sql.Timestamp updatedAt;
    private int updateCount;
    private boolean isArchived;
    private boolean isDeleted;

    public Asset(int id, String name, String type, String description, String category, String department, String model, String serialNumber, double originalValue, double purchasedValue, String location, String createdBy, java.sql.Timestamp createdAt, String updatedBy, java.sql.Timestamp updatedAt, int updateCount, boolean isArchived, boolean isDeleted) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.description = description;
        this.category = category;
        this.department = department;
        this.model = model;
        this.serialNumber = serialNumber;
        this.originalValue = originalValue;
        this.purchasedValue = purchasedValue;
        this.location = location;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.updatedBy = updatedBy;
        this.updatedAt = updatedAt;
        this.updateCount = updateCount;
        this.isArchived = isArchived;
        this.isDeleted = isDeleted;
    }

    public Asset(String name, String type, String description, String category, String department, String model, String serialNumber, double originalValue, double purchasedValue, String location, String createdBy) {
        this.name = name;
        this.type = type;
        this.description = description;
        this.category = category;
        this.department = department;
        this.model = model;
        this.serialNumber = serialNumber;
        this.originalValue = originalValue;
        this.purchasedValue = purchasedValue;
        this.location = location;
        this.createdBy = createdBy;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public double getOriginalValue() {
        return originalValue;
    }

    public void setOriginalValue(double originalValue) {
        this.originalValue = originalValue;
    }

    public double getPurchasedValue() {
        return purchasedValue;
    }

    public void setPurchasedValue(double purchasedValue) {
        this.purchasedValue = purchasedValue;
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

    public java.sql.Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(java.sql.Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public java.sql.Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(java.sql.Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    public int getUpdateCount() {
        return updateCount;
    }

    public void setUpdateCount(int updateCount) {
        this.updateCount = updateCount;
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

    @Override
    public String toString() {
        return String.format("| %-2d | %-13s | %-11s | %-15s | %-10s | %-10s | %-10s | %-15s | %-12.2f | %-12.2f | %-10s | %-10s | %-20s | %-10s | %-20s | %-10d | %-9s | %-9s |",
                id, name, type, description, category, department, model, serialNumber, originalValue, purchasedValue, location, createdBy, String.valueOf(createdAt), updatedBy, String.valueOf(updatedAt), updateCount, isArchived, isDeleted);
    }

}


