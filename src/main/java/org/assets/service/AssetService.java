package org.assets.service;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.assets.entities.Asset;
import org.assets.dbConnection.DBUtil;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AssetService {

    // Add asset
    public void createAsset(Asset asset) {
        try (Connection conn = DBUtil.getConnection()) {
            String sql = "INSERT INTO assets(name, type, value, active) VALUES (?, ?, ?,?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, asset.getName());
            stmt.setString(2, asset.getType());
            stmt.setDouble(3, asset.getValue());
            stmt.setBoolean(4, true);
            stmt.executeUpdate();
            System.out.println("Asset inserted successfully!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // view all assets
    public void viewAssetsWithPagination(
            int page,
            int pageSize,
            Integer idStart,
            Integer idEnd,
            String nameFilter,
            String typeFilter,
            Double valueStart,
            Double valueEnd) {

        try (Connection conn = DBUtil.getConnection()) {

            StringBuilder sql = new StringBuilder("SELECT * FROM assets WHERE active = true");
            List<Object> params = new ArrayList<>();

            if (idStart != null) {
                sql.append(" AND id >= ?");
                params.add(idStart);
            }

            if (idEnd != null) {
                sql.append(" AND id <= ?");
                params.add(idEnd);
            }

            if (nameFilter != null && !nameFilter.isEmpty()) {
                sql.append(" AND name LIKE ?");
                params.add("%" + nameFilter + "%");
            }

            if (typeFilter != null && !typeFilter.isEmpty()) {
                sql.append(" AND type = ?");
                params.add(typeFilter);
            }

            if (valueStart != null) {
                sql.append(" AND value >= ?");
                params.add(valueStart);
            }

            if (valueEnd != null) {
                sql.append(" AND value <= ?");
                params.add(valueEnd);
            }

            sql.append(" LIMIT ? OFFSET ?");
            params.add(pageSize);
            params.add((page - 1) * pageSize);

            PreparedStatement stmt = conn.prepareStatement(sql.toString());

            // Set all parameters dynamically
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }

            ResultSet rs = stmt.executeQuery();

            boolean found = false;
            int row = 0;

            System.out.println("\n--- Page " + page + " ---");
            while (rs.next()) {
                found = true;
                row++;
                System.out.println(
                        row + " - " +
                                rs.getInt("id") + " | " +
                                rs.getString("name") + " | " +
                                rs.getString("type") + " | " +
                                rs.getDouble("value") + " | " +
                                rs.getBoolean("active")
                );
            }

            if (!found) {
                System.out.println("No assets found with the given filter on this page.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // view asset by id
    public Asset getAssetById(int id) {
        try (Connection conn = DBUtil.getConnection()) {
            String sql = "SELECT * FROM assets WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Asset(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("type"),
                        rs.getDouble("value"),
                        rs.getBoolean("active")
                );
            } else {
                System.out.println("No asset found with ID: " + id);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // update asset by id
    public void updateAsset(int id, String name, String type, Double value) {
        try (Connection conn = DBUtil.getConnection()) {
            String findSql = "SELECT * FROM assets WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(findSql);
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String currentName = rs.getString("name");
                String currentType = rs.getString("type");
                double currentValue = rs.getDouble("value");

                String finalName = (name == null || name.isBlank()) ? currentName : name;
                String finalType = (type == null || type.isBlank()) ? currentType : type;
                double finalVal = (value == null) ? currentValue : value;

                String sql = "UPDATE assets SET name = ?, type = ?, value = ? WHERE id = ?";
                PreparedStatement updateStmt = conn.prepareStatement(sql);
                updateStmt.setString(1, finalName);
                updateStmt.setString(2, finalType);
                updateStmt.setDouble(3, finalVal);
                updateStmt.setInt(4, id);

                int rows = updateStmt.executeUpdate();
                if (rows > 0) {
                    System.out.println("Asset updated successfully.");
                } else {
                    System.out.println("Asset not found.");
                }
            } else {
                System.out.println("Asset with ID " + id + " not found.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // delete asset
    public void deleteAsset(int id) {
        try (Connection conn = DBUtil.getConnection()) {
            String sql = "DELETE FROM assets WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Asset deleted successfully.");
            } else {
                System.out.println("Asset not found.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // upload asset from excel file
    public void uploadAssetsFromExcel(String filePath) throws IOException, SQLException {
        try (FileInputStream fis = new FileInputStream(filePath);
             Connection conn = DBUtil.getConnection()) {

            Workbook workbook = WorkbookFactory.create(fis);
            Sheet sheet = workbook.getSheetAt(0);

            String sql = "INSERT INTO assets(name, type, value, active) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // skip header

                String name = row.getCell(0).getStringCellValue();
                String type = row.getCell(1).getStringCellValue();
                double value = row.getCell(2).getNumericCellValue();
                boolean active = row.getCell(3).getBooleanCellValue();

                stmt.setString(1, name);
                stmt.setString(2, type);
                stmt.setDouble(3, value);
                stmt.setBoolean(4, active);
                stmt.addBatch();
            }

            stmt.executeBatch();
            workbook.close();
            System.out.println("Data uploaded from Excel file.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // export asset from excel file
    public void exportAssetsToExcel(String filePath) throws SQLException, IOException {
        try (Connection conn = DBUtil.getConnection()) {
            String sql = "SELECT * FROM assets";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Assets");

            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("Name");
            header.createCell(1).setCellValue("Type");
            header.createCell(2).setCellValue("Value");
            header.createCell(3).setCellValue("Active Status");

            int rowIndex = 1;
            while (rs.next()) {
                Row row = sheet.createRow(rowIndex++);
                row.createCell(0).setCellValue(rs.getString("name"));
                row.createCell(1).setCellValue(rs.getString("type"));
                row.createCell(2).setCellValue(rs.getDouble("value"));
                row.createCell(3).setCellValue(rs.getBoolean("active"));
            }

            try (FileOutputStream fos = new FileOutputStream(filePath)) {
                workbook.write(fos);
            }

            workbook.close();
            System.out.println("Data exported to Excel file.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Activate Asset
    public void activateAssets(int activateId) {
        try (Connection conn = DBUtil.getConnection()) {
            String sql = "UPDATE assets SET active = 1 WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, activateId);
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Asset Activated successfully.");
            } else {
                System.out.println("Asset not found.");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // Deactivate Asset
    public void deactivateAssets(int deactivateId) {
        try (Connection conn = DBUtil.getConnection()) {
            String sql = "UPDATE assets SET active = 0 WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, deactivateId);
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Asset Deactivated successfully.");
            } else {
                System.out.println("Asset not found.");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
