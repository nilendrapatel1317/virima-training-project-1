package org.assets.driver;

import org.assets.entities.Asset;
import org.assets.service.AssetService;
import org.assets.util.InputUtil;
import java.io.FileNotFoundException;

public class Driver {
    public static void main(String[] args) {
        AssetService service = new AssetService();
        System.out.println("\n********** ASSET MANAGEMENT SYSTEM 7**********");
        System.out.println("*********************************************");

        while (true) {
            try {
                System.out.println("\n-------- Main Menu --------");
                System.out.println("0. Exit");
                System.out.println("1. Add Asset");
                System.out.println("2. View All Assets");
                System.out.println("3. View Single Asset by ID");
                System.out.println("4. Update Asset");
                System.out.println("5. Delete Asset");
                System.out.println("6. Asset Status");
                System.out.println("7. Import / Export");
                int choice = InputUtil.getInt("Enter your choice: ");

                switch (choice) {
                    case 0:
                        System.out.println("\nExiting...");
                        Thread.sleep(2000);
                        System.out.println("\n*******************************************************");
                        System.out.println("Thanks For Using Assets Management System Application !");
                        System.out.println("*******************************************************");
                        return;

                    case 1:
                        System.out.println("\n\tNew Asset Details");
                        String name = InputUtil.getString("\tEnter name: ");
                        String type = InputUtil.getString("\tEnter type: ");
                        double value = InputUtil.getDouble("\tEnter value: ");
                        Asset asset = new Asset(name, type, value);
                        service.createAsset(asset);
                        break;

                    case 2:
                        filterLoop:
                        while (true) {
                            Integer idStart = null, idEnd = null;
                            String nameFilter = null, typeFilter = null;
                            Double valueStart = null, valueEnd = null;

                            System.out.println("\n\tChoose filter option:");
                            System.out.println("\t0) Go Back");
                            System.out.println("\t1) Filter by ID Range");
                            System.out.println("\t2) Filter by Name");
                            System.out.println("\t3) Filter by Type");
                            System.out.println("\t4) Filter by Value Range");
                            System.out.println("\t5) No Filter (only Active)");
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
                                    String valStartInput = InputUtil.getOptionalString("\tEnter start value (or press Enter to skip): ");
                                    if (!valStartInput.isEmpty()) valueStart = Double.parseDouble(valStartInput);

                                    String valEndInput = InputUtil.getOptionalString("\tEnter end value (or press Enter to skip): ");
                                    if (!valEndInput.isEmpty()) valueEnd = Double.parseDouble(valEndInput);
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

                            service.viewAssetsWithPagination(page, size, idStart, idEnd, nameFilter, typeFilter, valueStart, valueEnd);
                        }
                        break;

                    case 3:
                        int id = InputUtil.getInt("\n\tEnter Asset ID: ");
                        Asset found = service.getAssetById(id);
                        if (found != null) {
                            System.out.printf("\t+----+---------------+-------------+----------+----------+%n");
                            System.out.printf("\t| ID | Name          | Type        | Value    | Status   |%n");
                            System.out.printf("\t+----+---------------+-------------+----------+----------+%n");
                            System.out.println("\t" + found);
                            System.out.printf("\t+----+---------------+-------------+----------+----------+%n");
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
                            System.out.printf("\t+----+---------------+-------------+----------+----------+%n");
                            System.out.printf("\t| ID | Name          | Type        | Value    | Status   |%n");
                            System.out.printf("\t+----+---------------+-------------+----------+----------+%n");
                            System.out.println("\t" + existingAsset);
                            System.out.printf("\t+----+---------------+-------------+----------+----------+%n");
                        }

                        System.out.println("\n\tNew Asset Details: ");
                        String uname = InputUtil.getOptionalString("\tEnter new name (leave blank to keep same): ");
                        String utype = InputUtil.getOptionalString("\tEnter new type (leave blank to keep same): ");
                        double uval = InputUtil.getDouble("\tEnter new value (or -1 to keep same): ");
                        Double finalValue = (uval == -1) ? null : uval;

                        service.updateAsset(uid, uname, utype, finalValue);
                        break;

                    case 5:
                        int did = InputUtil.getInt("\n\tEnter ID to delete: ");
                        service.deleteAsset(did);
                        break;

                    case 6:
                        statusLoop:
                        while (true) {
                            System.out.println("\n\tChoose operation option:");
                            System.out.println("\t0) Go Back");
                            System.out.println("\t1) Activate asset");
                            System.out.println("\t2) Deactivate asset");
                            int statusChoice = InputUtil.getInt("\tEnter your choice: ");

                            switch (statusChoice) {
                                case 0:
                                    break statusLoop;
                                case 1:
                                    int activateId = InputUtil.getInt("\tEnter ID to activate asset: ");
                                    service.activateAssets(activateId);
                                    break;
                                case 2:
                                    int deactivateId = InputUtil.getInt("\tEnter ID to deactivate asset: ");
                                    service.deactivateAssets(deactivateId);
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