package org.assets.driver;

import org.assets.entities.Asset;
import org.assets.service.AssetService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Scanner;

public class Driver {
    public static void main(String[] args) throws SQLException, IOException {
        AssetService service = new AssetService();
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\n--- Asset Management System ---");
            System.out.println("0. Exit");
            System.out.println("1. Add Asset");
            System.out.println("2. View All Assets");
            System.out.println("3. View Single Asset");
            System.out.println("4. Update Asset");
            System.out.println("5. Delete Asset");
            System.out.println("6. Asset Status");
            System.out.println("7. Import / Export");
            System.out.print("Enter your choice: ");
            int choice = sc.nextInt();

            switch (choice) {
                case 0:
                    System.out.println("Exiting...");
                    System.out.println("Thanks For Using Assets Management System Application !");
                    return;

                case 1:
                    sc.nextLine();
                    System.out.print("Enter name: ");
                    String name = sc.nextLine();
                    System.out.print("Enter type: ");
                    String type = sc.nextLine();
                    System.out.print("Enter value: ");
                    double value = sc.nextDouble();
                    Asset asset = new Asset(name, type, value);
                    service.createAsset(asset);
                    break;

                case 2:
                    filterLoop:
                    while (true) {
                        Integer idStart = null, idEnd = null;
                        String nameFilter = null, typeFilter = null;
                        Double valueStart = null, valueEnd = null;

                        System.out.println("\tChoose filter option:");
                        System.out.println("\t0) Go Back");
                        System.out.println("\t1) Filter by ID Range");
                        System.out.println("\t2) Filter by Name");
                        System.out.println("\t3) Filter by Type");
                        System.out.println("\t4) Filter by Value Range");
                        System.out.println("\t5) No Filter");
                        System.out.print("\tEnter your choice: ");
                        int filterChoice = sc.nextInt();
                        sc.nextLine();

                        switch (filterChoice) {
                            case 0:
                                break filterLoop;
                            case 1:
                                System.out.print("Enter start ID (or press Enter to skip): ");
                                String idStartInput = sc.nextLine();
                                if (!idStartInput.isEmpty()) idStart = Integer.parseInt(idStartInput);

                                System.out.print("Enter end ID (or press Enter to skip): ");
                                String idEndInput = sc.nextLine();
                                if (!idEndInput.isEmpty()) idEnd = Integer.parseInt(idEndInput);
                                break;

                            case 2:
                                System.out.print("Enter name to search: ");
                                nameFilter = sc.nextLine();
                                break;

                            case 3:
                                System.out.print("Enter asset type to search: ");
                                typeFilter = sc.nextLine();
                                break;

                            case 4:
                                System.out.print("Enter start value (or press Enter to skip): ");
                                String valStartInput = sc.nextLine();
                                if (!valStartInput.isEmpty()) valueStart = Double.parseDouble(valStartInput);

                                System.out.print("Enter end value (or press Enter to skip): ");
                                String valEndInput = sc.nextLine();
                                if (!valEndInput.isEmpty()) valueEnd = Double.parseDouble(valEndInput);
                                break;

                            case 5:
                                // No filters
                                break;

                            default:
                                System.out.println("Invalid option. No filter applied.");
                        }

                        System.out.print("Enter page number (press Enter for default 1): ");
                        String pageInput = sc.nextLine();
                        int page = pageInput.isEmpty() ? 1 : Integer.parseInt(pageInput);

                        System.out.print("Enter number of records per page (press Enter for default 10): ");
                        String sizeInput = sc.nextLine();
                        int size = sizeInput.isEmpty() ? 10 : Integer.parseInt(sizeInput);

                        service.viewAssetsWithPagination(page, size, idStart, idEnd, nameFilter, typeFilter, valueStart, valueEnd);
                    }
                    break;

                case 3:
                    assetSearchLoop:
                    while (true) {
                        System.out.println("\tChoose search option:");
                        System.out.println("\t0) Go Back");
                        System.out.println("\t1) Search by ID");
                        System.out.println("\t2) Search by Name");
                        System.out.print("\tEnter your choice: ");
                        int searchChoice = sc.nextInt();
                        sc.nextLine();

                        switch (searchChoice) {
                            case 0:
                                break assetSearchLoop;
                            case 1:
                                System.out.print("Enter Asset ID: ");
                                int id = sc.nextInt();
                                Asset found = service.getAssetById(id);
                                if (found != null) System.out.println(found);
                                break;
                            case 2:
                                System.out.print("Enter Asset Name: ");
                                String searchName = sc.nextLine();
                                service.getAssetsByName(searchName);
                                break;
                            default:
                                System.out.println("Invalid option.");
                        }
                    }
                    break;

                case 4:
                    System.out.print("Enter ID to update: ");
                    int uid = sc.nextInt();
                    sc.nextLine();

                    Asset existingAsset = service.getAssetById(uid);
                    if (existingAsset == null) {
                        System.out.println("Asset with ID " + uid + " not found.");
                        break;
                    }

                    System.out.println("Current Asset Details: " + existingAsset);

                    System.out.print("Enter new name (leave blank to keep same): ");
                    String uname = sc.nextLine();
                    System.out.print("Enter new type (leave blank to keep same): ");
                    String utype = sc.nextLine();
                    System.out.print("Enter new value (or -1 to keep same): ");
                    double uval = sc.nextDouble();
                    Double finalValue = (uval == -1) ? null : uval;

                    service.updateAsset(uid, uname, utype, finalValue);
                    break;

                case 5:
                    System.out.print("Enter ID to delete: ");
                    int did = sc.nextInt();
                    service.deleteAsset(did);
                    break;

                case 6:
                    statusLoop:
                    while (true) {
                        System.out.println("\tChoose operation option:");
                        System.out.println("\t0) Go Back");
                        System.out.println("\t1) Activate asset");
                        System.out.println("\t2) Deactivate asset");
                        System.out.print("\tEnter your choice: ");
                        int statusChoice = sc.nextInt();

                        switch (statusChoice) {
                            case 0:
                                break statusLoop;
                            case 1:
                                System.out.print("Enter ID to activate asset: ");
                                int activateId = sc.nextInt();
                                service.activateAssets(activateId);
                                break;
                            case 2:
                                System.out.print("Enter ID to deactivate asset: ");
                                int deactivateId = sc.nextInt();
                                service.deactivateAssets(deactivateId);
                                break;
                            default:
                                System.out.println("Invalid option.");
                        }
                    }
                    break;

                case 7:
                    ioLoop:
                    while (true) {
                        System.out.println("\tChoose operation option:");
                        System.out.println("\t0) Go Back");
                        System.out.println("\t1) Import data from excel file");
                        System.out.println("\t2) Export data to excel file");
                        System.out.print("\tEnter your choice: ");
                        int ioChoice = sc.nextInt();
                        sc.nextLine();

                        switch (ioChoice) {
                            case 0:
                                break ioLoop;
                            case 1:
                                System.out.print("Enter Excel file path to upload: ");
                                String upath = sc.nextLine();
                                service.uploadAssetsFromExcel(upath);
                                break;
                            case 2:
                                System.out.print("Enter Excel file path to export: ");
                                String epath = sc.nextLine();
                                service.exportAssetsToExcel(epath);
                                break;
                            default:
                                System.out.println("Invalid option.");
                        }
                    }
                    break;

                default:
                    System.out.println("Invalid choice!");
            }
        }
    }
}
