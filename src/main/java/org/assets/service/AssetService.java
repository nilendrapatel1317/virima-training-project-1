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

    // Helper: Check if asset exists (all fields except id)
    private boolean assetExists(Connection conn, String name, String type, String description, String category, String department, String model, String serialNumber, double originalValue, double purchasedValue, String location, String createdBy, Timestamp createdAt, String updatedBy, Timestamp updatedAt, int updateCount, boolean isArchived, boolean isDeleted) throws SQLException {
        String sql = "SELECT COUNT(*) FROM assets WHERE name=? AND type=? AND description=? AND category=? AND department=? AND model=? AND serialNumber=? AND originalValue=? AND purchasedValue=? AND location=? AND createdBy=? AND createdAt=? AND updatedBy=? AND updatedAt=? AND updateCount=? AND isArchived=? AND isDeleted=?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.setString(2, type);
            stmt.setString(3, description);
            stmt.setString(4, category);
            stmt.setString(5, department);
            stmt.setString(6, model);
            stmt.setString(7, serialNumber);
            stmt.setDouble(8, originalValue);
            stmt.setDouble(9, purchasedValue);
            stmt.setString(10, location);
            stmt.setString(11, createdBy);
            stmt.setTimestamp(12, createdAt);
            stmt.setString(13, updatedBy);
            stmt.setTimestamp(14, updatedAt);
            stmt.setInt(15, updateCount);
            stmt.setBoolean(16, isArchived);
            stmt.setBoolean(17, isDeleted);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }

    // 1. Add Asset
    public void createAsset(Asset asset) {
        try (Connection conn = DBUtil.getConnection()) {
            String sql = "INSERT INTO assets(name, type, description, category, department, model, serialNumber, originalValue, purchasedValue, location, createdBy, createdAt, updatedBy, updatedAt, updateCount, isArchived, isDeleted) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, asset.getName());
            stmt.setString(2, asset.getType());
            stmt.setString(3, asset.getDescription());
            stmt.setString(4, asset.getCategory());
            stmt.setString(5, asset.getDepartment());
            stmt.setString(6, asset.getModel());
            stmt.setString(7, asset.getSerialNumber());
            stmt.setDouble(8, asset.getOriginalValue());
            stmt.setDouble(9, asset.getPurchasedValue());
            stmt.setString(10, asset.getLocation());
            stmt.setString(11, asset.getCreatedBy());
            stmt.setTimestamp(12, asset.getCreatedAt());
            stmt.setString(13, asset.getUpdatedBy());
            stmt.setTimestamp(14, asset.getUpdatedAt());
            stmt.setInt(15, asset.getUpdateCount());
            stmt.setBoolean(16, asset.isArchived());
            stmt.setBoolean(17, asset.isDeleted());
            stmt.executeUpdate();
            successMsg("Asset inserted successfully");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 2. View All Assets with Filters and Pagination
    public void viewAssetsWithPagination(int page, int pageSize, Integer idStart, Integer idEnd, String nameFilter, String typeFilter, Double originalValueStart, Double originalValueEnd) {
        try (Connection conn = DBUtil.getConnection()) {
            StringBuilder sql = new StringBuilder("SELECT * FROM assets WHERE isArchived = false AND isDeleted = false");
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
                sql.append(" AND type LIKE ?");
                params.add("%" + typeFilter + "%");
            }
            if (originalValueStart != null) {
                sql.append(" AND originalValue >= ?");
                params.add(originalValueStart);
            }
            if (originalValueEnd != null) {
                sql.append(" AND originalValue <= ?");
                params.add(originalValueEnd);
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
            table.add(new String[]{"ID", "Name", "Type", "Description", "Category", "Department", "Model", "SerialNumber", "OriginalValue", "PurchasedValue", "Location", "CreatedBy", "CreatedAt", "UpdatedBy", "UpdatedAt", "UpdateCount", "isArchived", "isDeleted"});
            while (rs.next()) {
                found = true;
                table.add(new String[]{
                        String.valueOf(rs.getInt("id")),
                        rs.getString("name"),
                        rs.getString("type"),
                        rs.getString("description"),
                        rs.getString("category"),
                        rs.getString("department"),
                        rs.getString("model"),
                        rs.getString("serialNumber"),
                        String.valueOf(rs.getDouble("originalValue")),
                        String.valueOf(rs.getDouble("purchasedValue")),
                        rs.getString("location"),
                        rs.getString("createdBy"),
                        String.valueOf(rs.getTimestamp("createdAt")),
                        rs.getString("updatedBy"),
                        String.valueOf(rs.getTimestamp("updatedAt")),
                        String.valueOf(rs.getInt("updateCount")),
                        String.valueOf(rs.getBoolean("isArchived")),
                        String.valueOf(rs.getBoolean("isDeleted"))
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
                if (rs.getBoolean("isDeleted")) {
                    System.out.println("\tAsset is deleted. You can't watch it.");
                    return null;
                }
                if (rs.getBoolean("isArchived")) {
                    System.out.println("\tAsset is archived. First unarchive it.");
                    return null;
                }
                return new Asset(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("type"),
                        rs.getString("description"),
                        rs.getString("category"),
                        rs.getString("department"),
                        rs.getString("model"),
                        rs.getString("serialNumber"),
                        rs.getDouble("originalValue"),
                        rs.getDouble("purchasedValue"),
                        rs.getString("location"),
                        rs.getString("createdBy"),
                        rs.getTimestamp("createdAt"),
                        rs.getString("updatedBy"),
                        rs.getTimestamp("updatedAt"),
                        rs.getInt("updateCount"),
                        rs.getBoolean("isArchived"),
                        rs.getBoolean("isDeleted")
                );
            } else {
                System.out.println("\tNo asset found with ID: " + id);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // 4. Update Asset
    public void updateAsset(int id, String name, String type, String description, String category, String department, String model, String serialNumber, Double originalValue, Double purchasedValue, String location, String createdBy, java.sql.Timestamp createdAt, String updatedBy, java.sql.Timestamp updatedAt, Integer updateCount, Boolean isArchived, Boolean isDeleted) {
        try (Connection conn = DBUtil.getConnection()) {
            String findSql = "SELECT * FROM assets WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(findSql);
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String finalName = (name == null || name.isBlank()) ? rs.getString("name") : name;
                String finalType = (type == null || type.isBlank()) ? rs.getString("type") : type;
                String finalDescription = (description == null || description.isBlank()) ? rs.getString("description") : description;
                String finalCategory = (category == null || category.isBlank()) ? rs.getString("category") : category;
                String finalDepartment = (department == null || department.isBlank()) ? rs.getString("department") : department;
                String finalModel = (model == null || model.isBlank()) ? rs.getString("model") : model;
                String finalSerialNumber = (serialNumber == null || serialNumber.isBlank()) ? rs.getString("serialNumber") : serialNumber;
                double finalOriginalValue = (originalValue == null) ? rs.getDouble("originalValue") : originalValue;
                double finalPurchasedValue = (purchasedValue == null) ? rs.getDouble("purchasedValue") : purchasedValue;
                String finalLocation = (location == null || location.isBlank()) ? rs.getString("location") : location;
                String finalCreatedBy = (createdBy == null || createdBy.isBlank()) ? rs.getString("createdBy") : createdBy;
                java.sql.Timestamp finalCreatedAt = (createdAt == null) ? rs.getTimestamp("createdAt") : createdAt;
                String finalUpdatedBy = (updatedBy == null || updatedBy.isBlank()) ? rs.getString("updatedBy") : updatedBy;
                java.sql.Timestamp finalUpdatedAt = (updatedAt == null) ? new java.sql.Timestamp(System.currentTimeMillis()) : updatedAt;
                int finalUpdateCount = (updateCount == null) ? rs.getInt("updateCount") : updateCount;
                boolean finalIsArchived = (isArchived == null) ? rs.getBoolean("isArchived") : isArchived;
                boolean finalIsDeleted = (isDeleted == null) ? rs.getBoolean("isDeleted") : isDeleted;
                String sql = "UPDATE assets SET name = ?, type = ?, description = ?, category = ?, department = ?, model = ?, serialNumber = ?, originalValue = ?, purchasedValue = ?, location = ?, createdBy = ?, createdAt = ?, updatedBy = ?, updatedAt = ?, updateCount = updateCount + 1, isArchived = ?, isDeleted = ? WHERE id = ?";
                PreparedStatement updateStmt = conn.prepareStatement(sql);
                updateStmt.setString(1, finalName);
                updateStmt.setString(2, finalType);
                updateStmt.setString(3, finalDescription);
                updateStmt.setString(4, finalCategory);
                updateStmt.setString(5, finalDepartment);
                updateStmt.setString(6, finalModel);
                updateStmt.setString(7, finalSerialNumber);
                updateStmt.setDouble(8, finalOriginalValue);
                updateStmt.setDouble(9, finalPurchasedValue);
                updateStmt.setString(10, finalLocation);
                updateStmt.setString(11, finalCreatedBy);
                updateStmt.setTimestamp(12, finalCreatedAt);
                updateStmt.setString(13, finalUpdatedBy);
                updateStmt.setTimestamp(14, finalUpdatedAt);
                updateStmt.setBoolean(15, finalIsArchived);
                updateStmt.setBoolean(16, finalIsDeleted);
                updateStmt.setInt(17, id);
                int rows = updateStmt.executeUpdate();
                if (rows > 0) {
                    successMsg("Asset updated successfully");
                    Asset found = getAssetById(id);
                    System.out.println("\n\tUpdated Asset Details.");
                    if (found != null) {
                        java.util.List<String[]> table = new java.util.ArrayList<>();
                        table.add(new String[]{"ID", "Name", "Type", "Description", "Category", "Department", "Model", "SerialNumber", "OriginalValue", "PurchasedValue", "Location", "CreatedBy", "CreatedAt", "UpdatedBy", "UpdatedAt", "UpdateCount", "isArchived", "isDeleted"});
                        table.add(new String[]{
                                String.valueOf(found.getId()),
                                found.getName(),
                                found.getType(),
                                found.getDescription(),
                                found.getCategory(),
                                found.getDepartment(),
                                found.getModel(),
                                found.getSerialNumber(),
                                String.valueOf(found.getOriginalValue()),
                                String.valueOf(found.getPurchasedValue()),
                                found.getLocation(),
                                found.getCreatedBy(),
                                String.valueOf(found.getCreatedAt()),
                                found.getUpdatedBy(),
                                String.valueOf(found.getUpdatedAt()),
                                String.valueOf(found.getUpdateCount()),
                                String.valueOf(found.isArchived()),
                                String.valueOf(found.isDeleted())
                        });
                        printTable(table);
                    }
                } else {
                    System.out.println("\tAsset not found.");
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
            String sql = "UPDATE assets SET isDeleted = 1 , isArchived = 0 WHERE id = ?";
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
                if (rs.getBoolean("isArchived")) {
                    System.out.println("\n\tAsset is already archived.");
                    return;
                }
            } else {
                System.out.println("\tAsset not found.");
                return;
            }
            String sql = "UPDATE assets SET isArchived = 1 WHERE id = ?";
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
            String sql = "UPDATE assets SET isArchived = 0 WHERE id = ?";
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
        int duplicateCount = 0;
        int newCount = 0;
        try (FileInputStream fis = new FileInputStream(filePath); Connection conn = DBUtil.getConnection()) {
            Workbook workbook = WorkbookFactory.create(fis);
            Sheet sheet = workbook.getSheetAt(0);
            String sql = "INSERT INTO assets(name, type, description, category, department, model, serialNumber, originalValue, purchasedValue, location, createdBy, createdAt, updatedBy, updatedAt, updateCount, isArchived, isDeleted) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;
                String name = getCellStringValue(row, 1);
                String type = getCellStringValue(row, 2);
                String description = getCellStringValue(row, 3);
                String category = getCellStringValue(row, 4);
                String department = getCellStringValue(row, 5);
                String model = getCellStringValue(row, 6);
                String serialNumber = getCellStringValue(row, 7);
                double originalValue = getCellStringValue(row, 8).isEmpty() ? 0.0 : Double.parseDouble(getCellStringValue(row, 8));
                double purchasedValue = getCellStringValue(row, 9).isEmpty() ? 0.0 : Double.parseDouble(getCellStringValue(row, 9));
                String location = getCellStringValue(row, 10);
                String createdBy = getCellStringValue(row, 11);
                java.sql.Timestamp createdAt = getCellStringValue(row, 12).isEmpty() ? new java.sql.Timestamp(System.currentTimeMillis()) : java.sql.Timestamp.valueOf(getCellStringValue(row, 12));
                String updatedBy = getCellStringValue(row, 13);
                java.sql.Timestamp updatedAt = getCellStringValue(row, 14).isEmpty() ? new java.sql.Timestamp(System.currentTimeMillis()) : java.sql.Timestamp.valueOf(getCellStringValue(row, 14));
                int updateCount = getCellStringValue(row, 15).isEmpty() ? 0 : Integer.parseInt(getCellStringValue(row, 15));
                boolean isArchived = getCellStringValue(row, 16).isEmpty() ? false : Boolean.parseBoolean(getCellStringValue(row, 16));
                boolean isDeleted = getCellStringValue(row, 17).isEmpty() ? false : Boolean.parseBoolean(getCellStringValue(row, 17));
                if (assetExists(conn, name, type, description, category, department, model, serialNumber, originalValue, purchasedValue, location, createdBy, createdAt, updatedBy, updatedAt, updateCount, isArchived, isDeleted)) {
                    duplicateCount++;
                    continue;
                }
                stmt.setString(1, name);
                stmt.setString(2, type);
                stmt.setString(3, description);
                stmt.setString(4, category);
                stmt.setString(5, department);
                stmt.setString(6, model);
                stmt.setString(7, serialNumber);
                stmt.setDouble(8, originalValue);
                stmt.setDouble(9, purchasedValue);
                stmt.setString(10, location);
                stmt.setString(11, createdBy);
                stmt.setTimestamp(12, createdAt);
                stmt.setString(13, updatedBy);
                stmt.setTimestamp(14, updatedAt);
                stmt.setInt(15, updateCount);
                stmt.setBoolean(16, isArchived);
                stmt.setBoolean(17, isDeleted);
                stmt.addBatch();
                newCount++;
            }
            stmt.executeBatch();
            workbook.close();
            successMsg("File imported: " + duplicateCount + " duplicate(s), " + newCount + " new record(s)");
        } catch (java.io.FileNotFoundException fnfe) {
            System.out.println("\n\t❌ File not found. Please check the path and try again.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 7. Export to Excel
    public void exportAssetsToExcel(String filePath) throws SQLException, IOException {
        int exportCount = 0;
        try (Connection conn = DBUtil.getConnection()) {
            String sql = "SELECT * FROM assets WHERE isDeleted = 0 AND isArchived = 0";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Assets");

            Row header = sheet.createRow(0);
            String[] headers = {"ID", "Name", "Type", "Description", "Category", "Department", "Model", "SerialNumber", "OriginalValue", "PurchasedValue", "Location", "CreatedBy", "CreatedAt", "UpdatedBy", "UpdatedAt", "UpdateCount", "isArchived", "isDeleted"};

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
                exportCount++;
                Cell cell0 = row.createCell(0);
                cell0.setCellValue(rs.getInt("id"));
                cell0.setCellStyle(cellStyle);

                Cell cell1 = row.createCell(1);
                cell1.setCellValue(rs.getString("name"));
                cell1.setCellStyle(cellStyle);

                Cell cell2 = row.createCell(2);
                cell2.setCellValue(rs.getString("type"));
                cell2.setCellStyle(cellStyle);
                
                Cell cell3 = row.createCell(3);
                cell3.setCellValue(rs.getString("description"));
                cell3.setCellStyle(cellStyle);

                Cell cell4 = row.createCell(4);
                cell4.setCellValue(rs.getString("category"));
                cell4.setCellStyle(cellStyle);

                Cell cell5 = row.createCell(5);
                cell5.setCellValue(rs.getString("department"));
                cell5.setCellStyle(cellStyle);

                Cell cell6 = row.createCell(6);
                cell6.setCellValue(rs.getString("model"));
                cell6.setCellStyle(cellStyle);

                Cell cell7 = row.createCell(7);
                cell7.setCellValue(rs.getString("serialNumber"));
                cell7.setCellStyle(cellStyle);

                Cell cell8 = row.createCell(8);
                cell8.setCellValue(rs.getDouble("originalValue"));
                cell8.setCellStyle(cellStyle);

                Cell cell9 = row.createCell(9);
                cell9.setCellValue(rs.getDouble("purchasedValue"));
                cell9.setCellStyle(cellStyle);

                Cell cell10 = row.createCell(10);
                cell10.setCellValue(rs.getString("location"));
                cell10.setCellStyle(cellStyle);

                Cell cell11 = row.createCell(11);
                cell11.setCellValue(rs.getString("createdBy"));
                cell11.setCellStyle(cellStyle);

                Cell cell12 = row.createCell(12);
                cell12.setCellValue(String.valueOf(rs.getTimestamp("createdAt")));
                cell12.setCellStyle(dateStyle);

                Cell cell13 = row.createCell(13);
                cell13.setCellValue(rs.getString("updatedBy"));
                cell13.setCellStyle(cellStyle);

                Cell cell14 = row.createCell(14);
                cell14.setCellValue(String.valueOf(rs.getTimestamp("updatedAt")));
                cell14.setCellStyle(dateStyle);

                Cell cell15 = row.createCell(15);
                cell15.setCellValue(rs.getInt("updateCount"));
                cell15.setCellStyle(cellStyle);

                Cell cell16 = row.createCell(16);
                cell16.setCellValue(rs.getBoolean("isArchived"));
                cell16.setCellStyle(cellStyle);

                Cell cell17 = row.createCell(17);
                cell17.setCellValue(rs.getBoolean("isDeleted"));
                cell17.setCellStyle(cellStyle);
            }

            // Auto-size all columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            try (FileOutputStream fos = new FileOutputStream(filePath)) {
                workbook.write(fos);
            }
            workbook.close();
            successMsg("File exported: " + exportCount + " total record(s)!");
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
                        rs.getBoolean("isArchived") ? "Archived" : "Unarchived"
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
