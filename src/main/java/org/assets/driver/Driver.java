package org.assets.driver;

import org.assets.entities.Asset;
import org.assets.service.AssetService;
import org.assets.util.InputUtil;

import java.io.FileNotFoundException;

import static org.assets.service.AssetService.printTable;
import static org.assets.service.AssetService.successMsg;

public class Driver {
    public static void main(String[] args) {
        AssetService service = new AssetService();
        successMsg("Asset Management System");

        while (true) {
            try {
                System.out.println("\n-------- Main Menu --------");
                System.out.println("0. Exit");
                System.out.println("1. Add Asset");
                System.out.println("2. View All Assets");
                System.out.println("3. View Single Asset by ID");
                System.out.println("4. Update Asset");
                System.out.println("5. Delete Asset");
                System.out.println("6. Archived Asset");
                System.out.println("7. Import / Export");
                System.out.println("8. View Archived / Deleted Asset");
                int choice = InputUtil.getInt("Enter your choice: ");

                switch (choice) {
                    case 0:
                        System.out.println("\n\tExiting...");
                        Thread.sleep(2000);
                        successMsg("Thanks For Using Assets Management System Application");
                        return;

                    case 1:
                        System.out.println("\n\tNew Asset Details");
                        String name = InputUtil.getString("\tEnter name: ");
                        String type = InputUtil.getString("\tEnter type: ");
                        String description = InputUtil.getOptionalString("\tEnter description: ");
                        String category = InputUtil.getOptionalString("\tEnter category: ");
                        String department = InputUtil.getOptionalString("\tEnter department: ");
                        String model = InputUtil.getOptionalString("\tEnter model: ");
                        String serialNumber = InputUtil.getOptionalString("\tEnter serial number: ");
                        double originalValue = InputUtil.getDouble("\tEnter original value: ");
                        double purchasedValue = InputUtil.getDouble("\tEnter purchased value: ");
                        String location = InputUtil.getOptionalString("\tEnter location: ");
                        String createdBy = InputUtil.getOptionalString("\tEnter created by: ");
                        java.sql.Timestamp now = new java.sql.Timestamp(System.currentTimeMillis());
                        int updateCount = 0;
                        boolean isArchived = false;
                        boolean isDeleted = false;
                        Asset asset = new Asset(0, name, type, description, category, department, model, serialNumber, originalValue, purchasedValue, location, createdBy, now, createdBy, now, updateCount, isArchived, isDeleted);
                        service.createAsset(asset);
                        break;

                    case 2:
                        filterLoop:
                        while (true) {
                            Integer idStart = null, idEnd = null;
                            String nameFilter = null, typeFilter = null;
                            Double originalValueStart = null, originalValueEnd = null;

                            System.out.println("\n\tChoose filter option:");
                            System.out.println("\t0) Go Back");
                            System.out.println("\t1) Filter by ID Range");
                            System.out.println("\t2) Filter by Name");
                            System.out.println("\t3) Filter by Type");
                            System.out.println("\t4) Filter by Original Value Range");
                            System.out.println("\t5) No Filter (all active, unarchived, not deleted)");
                            int filterChoice = InputUtil.getInt("\tEnter your choice: ");

                            switch (filterChoice) {
                                case 0:
                                    break filterLoop;
                                case 1:
                                    String idStartInput = InputUtil.getOptionalString("\tEnter start ID (or press Enter to skip): ");
                                    if (!idStartInput.isEmpty()) idStart = Integer.parseInt(idStartInput);

                                    String idEndInput = InputUtil.getOptionalString("\tEnter end ID (or press Enter to skip): ");
                                    if (!idEndInput.isEmpty()) idEnd = Integer.parseInt(idEndInput);
                                    break;

                                case 2:
                                    nameFilter = InputUtil.getOptionalString("\tEnter name to search: ");
                                    break;

                                case 3:
                                    typeFilter = InputUtil.getOptionalString("\tEnter asset type to search: ");
                                    break;

                                case 4:
                                    String valStartInput = InputUtil.getOptionalString("\tEnter start original value (or press Enter to skip): ");
                                    if (!valStartInput.isEmpty()) originalValueStart = Double.parseDouble(valStartInput);

                                    String valEndInput = InputUtil.getOptionalString("\tEnter end original value (or press Enter to skip): ");
                                    if (!valEndInput.isEmpty()) originalValueEnd = Double.parseDouble(valEndInput);
                                    break;

                                case 5:
                                    break;

                                default:
                                    System.out.println("\tInvalid option. No filter applied.");
                            }

                            String pageInput = InputUtil.getOptionalString("\tEnter page number (press Enter for default 1): ");
                            int page = pageInput.isEmpty() ? 1 : Integer.parseInt(pageInput);

                            String sizeInput = InputUtil.getOptionalString("\tEnter number of records per page (press Enter for default 10): ");
                            int size = sizeInput.isEmpty() ? 10 : Integer.parseInt(sizeInput);

                            service.viewAssetsWithPagination(page, size, idStart, idEnd, nameFilter, typeFilter, originalValueStart, originalValueEnd);
                        }
                        break;

                    case 3:
                        int id = InputUtil.getInt("\n\tEnter Asset ID: ");
                        Asset found = service.getAssetById(id);
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
                        break;

                    case 4:
                        int uid = InputUtil.getInt("\n\tEnter ID to update: ");
                        Asset existingAsset = service.getAssetById(uid);
                        if (existingAsset == null) {
                            System.out.println("\tAsset with ID " + uid + " not found.");
                            break;
                        }
                        if (existingAsset != null) {
                            System.out.println("\n\tCurrent Asset Details: ");
                            java.util.List<String[]> table = new java.util.ArrayList<>();
                            table.add(new String[]{"ID", "Name", "Type", "Description", "Category", "Department", "Model", "SerialNumber", "OriginalValue", "PurchasedValue", "Location", "CreatedBy", "CreatedAt", "UpdatedBy", "UpdatedAt", "UpdateCount", "isArchived", "isDeleted"});
                            table.add(new String[]{
                                String.valueOf(existingAsset.getId()),
                                existingAsset.getName(),
                                existingAsset.getType(),
                                existingAsset.getDescription(),
                                existingAsset.getCategory(),
                                existingAsset.getDepartment(),
                                existingAsset.getModel(),
                                existingAsset.getSerialNumber(),
                                String.valueOf(existingAsset.getOriginalValue()),
                                String.valueOf(existingAsset.getPurchasedValue()),
                                existingAsset.getLocation(),
                                existingAsset.getCreatedBy(),
                                String.valueOf(existingAsset.getCreatedAt()),
                                existingAsset.getUpdatedBy(),
                                String.valueOf(existingAsset.getUpdatedAt()),
                                String.valueOf(existingAsset.getUpdateCount()),
                                String.valueOf(existingAsset.isArchived()),
                                String.valueOf(existingAsset.isDeleted())
                            });
                            printTable(table);
                        }
                        System.out.println("\n\tNew Asset Details: (leave blank to keep same)");
                        String uname = InputUtil.getOptionalString("\tEnter new name: ");
                        String utype = InputUtil.getOptionalString("\tEnter new type: ");
                        String udesc = InputUtil.getOptionalString("\tEnter new description: ");
                        String ucat = InputUtil.getOptionalString("\tEnter new category: ");
                        String udept = InputUtil.getOptionalString("\tEnter new department: ");
                        String umodel = InputUtil.getOptionalString("\tEnter new model: ");
                        String userial = InputUtil.getOptionalString("\tEnter new serial number: ");
                        String uorigValStr = InputUtil.getOptionalString("\tEnter new original value: ");
                        Double uorigVal = uorigValStr.isEmpty() ? null : Double.parseDouble(uorigValStr);
                        String upurchValStr = InputUtil.getOptionalString("\tEnter new purchased value: ");
                        Double upurchVal = upurchValStr.isEmpty() ? null : Double.parseDouble(upurchValStr);
                        String ulocation = InputUtil.getOptionalString("\tEnter new location: ");
                        String ucreatedBy = InputUtil.getOptionalString("\tEnter new created by: ");
                        String uupdatedBy = InputUtil.getOptionalString("\tEnter new updated by: ");
                        java.sql.Timestamp uupdatedAt = new java.sql.Timestamp(System.currentTimeMillis());
                        String uarchivedStr = InputUtil.getOptionalString("\tIs archived? (true/false): ");
                        Boolean uarchived = uarchivedStr.isEmpty() ? null : Boolean.parseBoolean(uarchivedStr);
                        String udeletedStr = InputUtil.getOptionalString("\tIs deleted? (true/false): ");
                        Boolean udeleted = udeletedStr.isEmpty() ? null : Boolean.parseBoolean(udeletedStr);
                        service.updateAsset(uid, uname, utype, udesc, ucat, udept, umodel, userial, uorigVal, upurchVal, ulocation, ucreatedBy, null, uupdatedBy, uupdatedAt, null, uarchived, udeleted);
                        break;

                    case 5:
                        int did = InputUtil.getInt("\n\tEnter ID to delete: ");
                        service.deleteAsset(did);
                        break;


                    case 6:
                        archivedLoop:
                        while (true) {
                            System.out.println("\n\tChoose operation option:");
                            System.out.println("\t0) Go Back");
                            System.out.println("\t1) Archive asset");
                            System.out.println("\t2) Unarchive asset");
                            int archiveChoice = InputUtil.getInt("\tEnter your choice: ");

                            switch (archiveChoice) {
                                case 0:
                                    break archivedLoop;
                                case 1:
                                    int archiveId = InputUtil.getInt("\tEnter ID to archive asset: ");
                                    service.archiveAssets(archiveId);
                                    break;
                                case 2:
                                    int unarchiveId = InputUtil.getInt("\tEnter ID to unarchive asset: ");
                                    service.unarchiveAssets(unarchiveId);
                                    break;
                                default:
                                    System.out.println("\tInvalid option.");
                            }
                        }
                        break;
                    case 7:
                        ioLoop:
                        while (true) {
                            System.out.println("\n\tChoose operation option:");
                            System.out.println("\t0) Go Back");
                            System.out.println("\t1) Import data from excel (.xlsx) file");
                            System.out.println("\t2) Export data to excel (.xlsx) file");
                            int ioChoice = InputUtil.getInt("\tEnter your choice: ");

                            switch (ioChoice) {
                                case 0:
                                    break ioLoop;
                                case 1:
                                    String upath = InputUtil.getString("\tEnter Excel file path to upload: ");
                                    if (!(upath.charAt(0) >= '0') || !(upath.charAt(0) <= '9')) {
                                        String uExtension = upath.substring(upath.length() - 4, upath.length());
                                        if (uExtension.equals("xlsx")) {
                                            service.uploadAssetsFromExcel(upath);
                                        } else {
                                            System.out.println("\n\t❌ Invalid extension (Use .xlsx)");
                                        }
                                    } else {
                                        System.out.println("\n\t❌ Path or File name can not start with number");
                                    }

                                    break;
                                case 2:
                                    String epath = InputUtil.getString("\tEnter Excel file path to export: ");
                                    if (!(epath.charAt(0) >= '0') || !(epath.charAt(0) <= '9')) {
                                        String eExtension = epath.substring(epath.length() - 4, epath.length());
                                        if (eExtension.equals("xlsx")) {
                                            service.exportAssetsToExcel(epath);
                                        } else {
                                            System.out.println("\n\t❌ Invalid extension (Use .xlsx)");
                                        }
                                    } else {
                                        System.out.println("\n\t❌ Path or File name can not start with number");
                                    }

                                    break;
                                default:
                                    System.out.println("\tInvalid option.");
                            }
                        }
                        break;
                    case 8:
                        viewArchiveLoop:
                        while (true) {
                            System.out.println("\n\tView archived / deleted assets");
                            System.out.println("\t0. Go back");
                            System.out.println("\t1. View All Archived assets");
                            System.out.println("\t2. View All Deleted assets");
                            int archiveChoice = InputUtil.getInt("\tEnter your choice: ");
                            switch (archiveChoice) {
                                case 0:
                                    break viewArchiveLoop;
                                case 1:
                                    service.viewArchivedAssets();
                                    break;
                                case 2:
                                    service.viewDeletedAssets();
                                    break;
                                default:
                                    System.out.println("\tInvalid option.");
                            }
                        }
                        break;

                    default:
                        System.out.println("Invalid choice!");
                }
            } catch (Exception e) {
                System.out.println("\n🚨 Error: " + e.getMessage());
                // Optional: e.printStackTrace();
            }
        }
    }
}