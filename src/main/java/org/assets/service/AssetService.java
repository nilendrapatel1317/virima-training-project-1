package org.assets.service;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.assets.entities.Asset;
import org.assets.util.DBUtil;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AssetService {

    public static void successMsg(String msg) {
        for (int i = 0; i < msg.length() + 1; i++) {
            if (i == 0)
                System.out.print("\n\t");
            System.out.print("*");
        }
        System.out.println();
        System.out.println("\t" + msg + "!");
        for (int i = 0; i < msg.length() + 1; i++) {
            if (i == 0)
                System.out.print("\t");
            System.out.print("*");
        }
        System.out.println();
    }

    private static boolean isDeleted(ResultSet rs) throws SQLException {
        if (rs.getBoolean("isDeleted")) {
            System.out.println("\n\tAsset is deleted. You can't perform any operation.");
            return true;
        }
        return false;
    }

    private static boolean isActive(ResultSet rs) throws SQLException {
        if (!rs.getBoolean("active")) {
            System.out.println("\n\tAsset is inactive. First make it active.");
            return true;
        }
        return false;
    }

    private static boolean isArchived(ResultSet rs) throws SQLException {
        if (rs.getBoolean("isArchived")) {
            System.out.println("\n\tAsset is archived. First unarchive it.");
            return true;
        }
        return false;
    }

    // Utility to print a table with dynamic column widths
    public static void printTable(List<String[]> rows) {
        if (rows == null || rows.isEmpty())
            return;
        int cols = rows.get(0).length;
        int[] maxWidths = new int[cols];
        // Find max width for each column
        for (String[] row : rows) {
            for (int i = 0; i < cols; i++) {
                if (row[i] != null) {
                    maxWidths[i] = Math.max(maxWidths[i], row[i].length());
                }
            }
        }
        // Build format string
        StringBuilder format = new StringBuilder();
        StringBuilder separator = new StringBuilder();
        for (int w : maxWidths) {
            format.append("| %-" + w + "s ");
            separator.append("+");
            for (int i = 0; i < w + 2; i++)
                separator.append("-");
        }
        format.append("|\n");
        separator.insert(0, "\t");
        separator.append("+\n");
        // Print header, separator, and rows
        System.out.print(separator);
        System.out.printf("\t" + format.toString(), (Object[]) rows.get(0));
        System.out.print(separator);
        for (int i = 1; i < rows.size(); i++) {
            System.out.printf("\t" + format.toString(), (Object[]) rows.get(i));
        }
        System.out.print(separator);
    }

    // Helper to safely get cell value as String
    private static String getCellStringValue(Row row, int cellIndex) {
        if (row.getCell(cellIndex) == null)
            return "";
        switch (row.getCell(cellIndex).getCellType()) {
            case STRING:
                return row.getCell(cellIndex).getStringCellValue();
            case NUMERIC:
                double d = row.getCell(cellIndex).getNumericCellValue();
                if (d == (long) d)
                    return String.valueOf((long) d); // for integer-like numbers
                return String.valueOf(d);
            case BOOLEAN:
                return String.valueOf(row.getCell(cellIndex).getBooleanCellValue());
            case FORMULA:
                return row.getCell(cellIndex).getCellFormula();
            case BLANK:
                return "";
            default:
                return row.getCell(cellIndex).toString();
        }
    }

    // 1. Add Asset
    public void createAsset(Asset asset) {
        try (Connection conn = DBUtil.getConnection()) {
            String sql = "INSERT INTO assets(name, type, value, active, createdAt, updatedAt, isArchived, isDeleted, location, createdBy, updatedBy) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, asset.getName());
            stmt.setString(2, asset.getType());
            stmt.setDouble(3, asset.getValue());
            stmt.setBoolean(4, asset.isActive());
            stmt.setTimestamp(5, asset.getCreatedAt());
            stmt.setTimestamp(6, asset.getUpdatedAt());
            stmt.setBoolean(7, asset.isArchived());
            stmt.setBoolean(8, asset.isDeleted());
            stmt.setString(9, asset.getLocation());
            stmt.setString(10, asset.getCreatedBy());
            stmt.setString(11, asset.getUpdatedBy());
            stmt.executeUpdate();
            successMsg("Asset inserted successfully");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 2. View All Assets with Filters and Pagination
    public void viewAssetsWithPagination(int page, int pageSize, Integer idStart, Integer idEnd, String nameFilter,
                                         String typeFilter, Double valueStart, Double valueEnd) {
        try (Connection conn = DBUtil.getConnection()) {
            StringBuilder sql = new StringBuilder(
                    "SELECT * FROM assets WHERE active = true AND isArchived = false AND isDeleted = false");
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
                sql.append(" AND type Like ?");
                params.add("%" + typeFilter + "%");
            }
            if (valueStart != null) {
                sql.append(" AND value >= ?");
                params.add(valueStart);
            }
            if (valueEnd != null) {
                sql.append(" AND value <= ?");
                params.add(valueEnd);
            }
            sql.append(" ORDER BY id DESC LIMIT ? OFFSET ?");
            params.add(pageSize);
            params.add((page - 1) * pageSize);
            PreparedStatement stmt = conn.prepareStatement(sql.toString());
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }
            ResultSet rs = stmt.executeQuery();

            boolean found = false;
            List<String[]> table = new ArrayList<>();
            // Header
            table.add(new String[]{"ID", "Name", "Type", "Value", "Location", "CreatedBy", "CreatedAt", "UpdatedBy",
                    "UpdatedAt", "Status"});
            while (rs.next()) {
                found = true;
                table.add(new String[]{
                        String.valueOf(rs.getInt("id")),
                        rs.getString("name"),
                        rs.getString("type"),
                        String.valueOf(rs.getDouble("value")),
                        rs.getString("location"),
                        rs.getString("createdBy"),
                        String.valueOf(rs.getTimestamp("createdAt")),
                        rs.getString("updatedBy"),
                        String.valueOf(rs.getTimestamp("updatedAt")),
                        rs.getBoolean("active") ? "Active" : "Inactive"
                });
            }
            System.out.println("\t--- Page " + page + " ---");
            if (found) {
                printTable(table);
            } else {
                System.out.println("\tNo assets found with the given filter on this page.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 3. View Single Asset by ID
    public Asset getAssetById(int id) {
        try (Connection conn = DBUtil.getConnection()) {
            String sql = "SELECT * FROM assets WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                if (isDeleted(rs))
                    return null;
                if (isArchived(rs))
                    return null;
                if (isActive(rs))
                    return null;
                return new Asset(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("type"),
                        rs.getDouble("value"),
                        rs.getBoolean("active"),
                        rs.getTimestamp("createdAt"),
                        rs.getTimestamp("updatedAt"),
                        rs.getBoolean("isArchived"),
                        rs.getBoolean("isDeleted"),
                        rs.getString("location"),
                        rs.getString("createdBy"),
                        rs.getString("updatedBy"));
            } else {
                System.out.println("\tNo asset found with ID: " + id);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // 4. Update Asset
    public void updateAsset(int id, String name, String type, Double value, Boolean active,
                            java.sql.Timestamp createdAt, java.sql.Timestamp updatedAt, Boolean isArchived, Boolean isDeleted,
                            String location, String createdBy, String updatedBy) {
        try (Connection conn = DBUtil.getConnection()) {
            String findSql = "SELECT * FROM assets WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(findSql);
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                if (isDeleted(rs)) return;
                if (isArchived(rs)) return;
                if (isActive(rs)) return;
                String finalName = (name == null || name.isBlank()) ? rs.getString("name") : name;
                String finalType = (type == null || type.isBlank()) ? rs.getString("type") : type;
                double finalVal = (value == null) ? rs.getDouble("value") : value;
                boolean finalActive = (active == null) ? rs.getBoolean("active") : active;
                java.sql.Timestamp finalCreatedAt = (createdAt == null) ? rs.getTimestamp("createdAt") : createdAt;
                java.sql.Timestamp finalUpdatedAt = (updatedAt == null)
                        ? new java.sql.Timestamp(System.currentTimeMillis())
                        : updatedAt;
                boolean finalIsArchived = (isArchived == null) ? rs.getBoolean("isArchived") : isArchived;
                boolean finalIsDeleted = (isDeleted == null) ? rs.getBoolean("isDeleted") : isDeleted;
                String finalLocation = (location == null || location.isBlank()) ? rs.getString("location") : location;
                String finalCreatedBy = (createdBy == null || createdBy.isBlank()) ? rs.getString("createdBy")
                        : createdBy;
                String finalUpdatedBy = (updatedBy == null || updatedBy.isBlank()) ? rs.getString("updatedBy")
                        : updatedBy;
                if (finalVal < 0) {
                    System.out.println("\n\tAsset value can not be negative.");
                } else {
                    String sql = "UPDATE assets SET name = ?, type = ?, value = ?, active = ?, createdAt = ?, updatedAt = ?, isArchived = ?, isDeleted = ?, location = ?, createdBy = ?, updatedBy = ? WHERE id = ?";
                    PreparedStatement updateStmt = conn.prepareStatement(sql);
                    updateStmt.setString(1, finalName);
                    updateStmt.setString(2, finalType);
                    updateStmt.setDouble(3, finalVal);
                    updateStmt.setBoolean(4, finalActive);
                    updateStmt.setTimestamp(5, finalCreatedAt);
                    updateStmt.setTimestamp(6, finalUpdatedAt);
                    updateStmt.setBoolean(7, finalIsArchived);
                    updateStmt.setBoolean(8, finalIsDeleted);
                    updateStmt.setString(9, finalLocation);
                    updateStmt.setString(10, finalCreatedBy);
                    updateStmt.setString(11, finalUpdatedBy);
                    updateStmt.setInt(12, id);
                    int rows = updateStmt.executeUpdate();
                    if (rows > 0) {
                        successMsg("Asset updated successfully");
                        Asset found = getAssetById(id);
                        System.out.println("\n\tUpdated Asset Details.");
                        if (found != null) {
                            java.util.List<String[]> table = new java.util.ArrayList<>();
                            table.add(new String[]{"ID", "Name", "Type", "Value", "Location", "CreatedBy", "CreatedAt", "UpdatedBy", "UpdatedAt", "Status"});
                            table.add(new String[]{
                                String.valueOf(found.getId()),
                                found.getName(),
                                found.getType(),
                                String.valueOf(found.getValue()),
                                found.getLocation(),
                                found.getCreatedBy(),
                                String.valueOf(found.getCreatedAt()),
                                found.getUpdatedBy(),
                                String.valueOf(found.getUpdatedAt()),
                                found.isActive() ? "Active" : "Inactive"
                            });
                            printTable(table);
                        }
                    } else {
                        System.out.println("\tAsset not found.");
                    }
                }
            } else {
                System.out.println("\tAsset with ID " + id + " not found.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 5. Delete Asset
    public void deleteAsset(int id) {
        try (Connection conn = DBUtil.getConnection()) {
            String checkSql = "SELECT isDeleted FROM assets WHERE id = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setInt(1, id);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next()) {
                if (rs.getBoolean("isDeleted")) {
                    System.out.println("\n\tAsset is already deleted.");
                    return;
                }
            } else {
                System.out.println("\tAsset not found.");
                return;
            }
            String sql = "UPDATE assets SET isDeleted = 1 , active = 0 , isArchived = 0 WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                successMsg("Asset deleted successfully");
            } else {
                System.out.println("\tAsset not found.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 6. Asset Status - Activate
    public void activateAssets(int activateId) throws InterruptedException {
        try (Connection conn = DBUtil.getConnection()) {
            String checkSql = "SELECT * FROM assets WHERE id = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setInt(1, activateId);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next()) {
                if (isDeleted(rs))
                    return; // check asset is deleted or not
//                if (isArchived(rs))
//                    return;
                if (rs.getBoolean("active")) {
                    System.out.println("\n\tAsset is already active.");
                    return;
                }
            } else {
                System.out.println("\tAsset not found.");
                return;
            }
            String sql = "UPDATE assets SET active = 1 , isArchived = 0 WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, activateId);
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                successMsg("Asset Activated successfully");
            } else {
                System.out.println("\tAsset not found.");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // 6. Asset Status - Deactivate
    public void deactivateAssets(int deactivateId) throws InterruptedException {
        try (Connection conn = DBUtil.getConnection()) {
            String checkSql = "SELECT * FROM assets WHERE id = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setInt(1, deactivateId);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next()) {
                if (isDeleted(rs))
                    return; // check asset is deleted or not
                if (!rs.getBoolean("active")) {
                    System.out.println("\n\tAsset is already inactive.");
                    return;
                }
            } else {
                System.out.println("\tAsset not found.");
                return;
            }
            String sql = "UPDATE assets SET active = 0 , isArchived = 1 WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, deactivateId);
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                successMsg("Asset Deactivated successfully");
            } else {
                System.out.println("\tAsset not found.");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // 6. Asset Status - Archive
    public void archiveAssets(int archiveId) throws InterruptedException {
        try (Connection conn = DBUtil.getConnection()) {
            String checkSql = "SELECT * FROM assets WHERE id = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setInt(1, archiveId);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next()) {
                if (isDeleted(rs))
                    return; // check asset is deleted or not
//                if (isActive(rs))
//                    return;
                if (rs.getBoolean("isArchived")) {
                    System.out.println("\n\tAsset is already archived.");
                    return;
                }
            } else {
                System.out.println("\tAsset not found.");
                return;
            }
            String sql = "UPDATE assets SET isArchived = 1 , active = 0 WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, archiveId);
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                successMsg("Asset Archived successfully");
            } else {
                System.out.println("\tAsset not found.");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // 6. Asset Status - Unarchive
    public void unarchiveAssets(int unarchiveId) throws InterruptedException {
        try (Connection conn = DBUtil.getConnection()) {
            String checkSql = "SELECT * FROM assets WHERE id = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setInt(1, unarchiveId);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next()) {
                if (isDeleted(rs))
                    return; // check asset is deleted or not
                if (!rs.getBoolean("isArchived")) {
                    System.out.println("\n\tAsset is already unarchived.");
                    return;
                }
            } else {
                System.out.println("\tAsset not found.");
                return;
            }
            String sql = "UPDATE assets SET isArchived = 0 , active = 1 WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, unarchiveId);
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                successMsg("Asset Unarchived successfully");
            } else {
                System.out.println("\tAsset not found.");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // 7. Import from Excel
    public void uploadAssetsFromExcel(String filePath) {
        try (FileInputStream fis = new FileInputStream(filePath); Connection conn = DBUtil.getConnection()) {
            Workbook workbook = WorkbookFactory.create(fis);
            Sheet sheet = workbook.getSheetAt(0);
            String sql = "INSERT INTO assets(name, type, value, location, createdBy, createdAt, updatedBy, updatedAt, active, isArchived, isDeleted) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            for (Row row : sheet) {
                if (row.getRowNum() == 0)
                    continue;
                String name = getCellStringValue(row, 1);
                String type = getCellStringValue(row, 2);
                double value = getCellStringValue(row, 3).isEmpty() ? 0.0
                        : Double.parseDouble(getCellStringValue(row, 3));
                String location = getCellStringValue(row, 4);
                String createdBy = getCellStringValue(row, 5);
                java.sql.Timestamp createdAt = getCellStringValue(row, 6).isEmpty()
                        ? new java.sql.Timestamp(System.currentTimeMillis())
                        : java.sql.Timestamp.valueOf(getCellStringValue(row, 6));
                String updatedBy = getCellStringValue(row, 7);
                java.sql.Timestamp updatedAt = getCellStringValue(row, 8).isEmpty()
                        ? new java.sql.Timestamp(System.currentTimeMillis())
                        : java.sql.Timestamp.valueOf(getCellStringValue(row, 8));
                boolean active = getCellStringValue(row, 9).isEmpty() ? true
                        : Boolean.parseBoolean(getCellStringValue(row, 9));
                boolean isArchived = getCellStringValue(row, 10).isEmpty() ? false
                        : Boolean.parseBoolean(getCellStringValue(row, 10));
                boolean isDeleted = getCellStringValue(row, 11).isEmpty() ? false
                        : Boolean.parseBoolean(getCellStringValue(row, 11));
                stmt.setString(1, name);
                stmt.setString(2, type);
                stmt.setDouble(3, value);
                stmt.setString(4, location);
                stmt.setString(5, createdBy);
                stmt.setTimestamp(6, createdAt);
                stmt.setString(7, updatedBy);
                stmt.setTimestamp(8, updatedAt);
                stmt.setBoolean(9, active);
                stmt.setBoolean(10, isArchived);
                stmt.setBoolean(11, isDeleted);
                stmt.addBatch();
            }
            stmt.executeBatch();
            workbook.close();
            successMsg("Data uploaded successfully");
        } catch (java.io.FileNotFoundException fnfe) {
            System.out.println("\n\t❌ File not found. Please check the path and try again.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 7. Export to Excel
    public void exportAssetsToExcel(String filePath) throws SQLException, IOException {
        try (Connection conn = DBUtil.getConnection()) {
            String sql = "SELECT * FROM assets WHERE active = 1 AND isDeleted = 0 AND isArchived = 0";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Assets");

            // Create header row
            Row header = sheet.createRow(0);
            String[] headers = {
                    "ID", "Name", "Type", "Value", "Location",
                    "CreatedBy", "CreatedAt", "UpdatedBy", "UpdatedAt",
                    "isActive", "isArchived", "isDeleted"
            };

            // Create header font, style, background color
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setFontHeightInPoints((short) 12);

            CellStyle headerCellStyle = workbook.createCellStyle();
            headerCellStyle.setFont(headerFont);
            headerCellStyle.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());
            headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerCellStyle.setAlignment(HorizontalAlignment.CENTER);
            headerCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            headerCellStyle.setBorderBottom(BorderStyle.THIN);
            headerCellStyle.setBorderTop(BorderStyle.THIN);
            headerCellStyle.setBorderLeft(BorderStyle.THIN);
            headerCellStyle.setBorderRight(BorderStyle.THIN);

            for (int i = 0; i < headers.length; i++) {
                Cell cell = header.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerCellStyle);
            }

            // Create common cell style with borders
            CellStyle cellStyle = workbook.createCellStyle();
            cellStyle.setBorderBottom(BorderStyle.THIN);
            cellStyle.setBorderTop(BorderStyle.THIN);
            cellStyle.setBorderLeft(BorderStyle.THIN);
            cellStyle.setBorderRight(BorderStyle.THIN);
            cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

            // Create date format style
            CellStyle dateStyle = workbook.createCellStyle();
            CreationHelper createHelper = workbook.getCreationHelper();
            dateStyle.setDataFormat(createHelper.createDataFormat().getFormat("yyyy-mm-dd hh:mm:ss"));
            dateStyle.setBorderBottom(BorderStyle.THIN);
            dateStyle.setBorderTop(BorderStyle.THIN);
            dateStyle.setBorderLeft(BorderStyle.THIN);
            dateStyle.setBorderRight(BorderStyle.THIN);
            dateStyle.setVerticalAlignment(VerticalAlignment.CENTER);

            int rowIndex = 1;
            while (rs.next()) {
                Row row = sheet.createRow(rowIndex++);

                Cell cell0 = row.createCell(0);
                cell0.setCellValue(rs.getInt("id"));
                cell0.setCellStyle(dateStyle);

                Cell cell1 = row.createCell(1);
                cell1.setCellValue(rs.getString("name"));
                cell1.setCellStyle(dateStyle);

                Cell cell2 = row.createCell(2);
                cell2.setCellValue(rs.getString("type"));
                cell2.setCellStyle(dateStyle);

                Cell cell3 = row.createCell(3);
                cell3.setCellValue(rs.getDouble("value"));
                cell3.setCellStyle(dateStyle);

                Cell cell4 = row.createCell(4);
                cell4.setCellValue(rs.getString("location"));
                cell4.setCellStyle(dateStyle);

                Cell cell5 = row.createCell(5);
                cell5.setCellValue(rs.getString("createdBy"));
                cell5.setCellStyle(dateStyle);

                Cell cell6 = row.createCell(6);
                cell6.setCellValue(rs.getTimestamp("createdAt"));
                cell6.setCellStyle(dateStyle);

                Cell cell7 = row.createCell(7);
                cell7.setCellValue(rs.getString("updatedBy"));
                cell7.setCellStyle(dateStyle);

                Cell cell8 = row.createCell(8);
                cell8.setCellValue(rs.getTimestamp("updatedAt"));
                cell8.setCellStyle(dateStyle);

                Cell cell9 = row.createCell(9);
                cell9.setCellValue(rs.getBoolean("active"));
                cell9.setCellStyle(dateStyle);

                Cell cell10 = row.createCell(10);
                cell10.setCellValue(rs.getBoolean("isArchived"));
                cell10.setCellStyle(dateStyle);

                Cell cell11 = row.createCell(11);
                cell11.setCellValue(rs.getBoolean("isDeleted"));
                cell11.setCellStyle(dateStyle);
            }

            // Auto-size all columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // Write to file
            try (FileOutputStream fos = new FileOutputStream(filePath)) {
                workbook.write(fos);
            }

            workbook.close();
            successMsg("Data exported to Excel file");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void viewArchivedAssets() {
        try (Connection conn = DBUtil.getConnection()) {
            String sql = "SELECT * FROM assets WHERE isArchived = 1 AND active = 0 ORDER BY id DESC";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            java.util.List<String[]> table = new java.util.ArrayList<>();
            table.add(new String[]{"ID", "Name", "Type", "Value", "Location", "CreatedBy", "CreatedAt", "UpdatedBy", "UpdatedAt", "Status"});
            boolean found = false;
            while (rs.next()) {
                found = true;
                table.add(new String[]{
                    String.valueOf(rs.getInt("id")),
                    rs.getString("name"),
                    rs.getString("type"),
                    String.valueOf(rs.getDouble("value")),
                    rs.getString("location"),
                    rs.getString("createdBy"),
                    String.valueOf(rs.getTimestamp("createdAt")),
                    rs.getString("updatedBy"),
                    String.valueOf(rs.getTimestamp("updatedAt")),
                    rs.getBoolean("active") ? "Active" : "Inactive"
                });
            }
            if (found) {
                printTable(table);
            } else {
                System.out.println("\tNo archived assets found.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void viewDeletedAssets() {
        try (Connection conn = DBUtil.getConnection()) {
            String sql = "SELECT * FROM assets WHERE isDeleted = 1 ORDER BY id DESC";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            java.util.List<String[]> table = new java.util.ArrayList<>();
            table.add(new String[]{"ID", "Name", "Type", "Value", "Location", "CreatedBy", "CreatedAt", "UpdatedBy", "UpdatedAt", "Status"});
            boolean found = false;
            while (rs.next()) {
                found = true;
                table.add(new String[]{
                    String.valueOf(rs.getInt("id")),
                    rs.getString("name"),
                    rs.getString("type"),
                    String.valueOf(rs.getDouble("value")),
                    rs.getString("location"),
                    rs.getString("createdBy"),
                    String.valueOf(rs.getTimestamp("createdAt")),
                    rs.getString("updatedBy"),
                    String.valueOf(rs.getTimestamp("updatedAt")),
                    rs.getBoolean("active") ? "Active" : "Inactive"
                });
            }
            if (found) {
                printTable(table);
            } else {
                System.out.println("\tNo deleted assets found.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
