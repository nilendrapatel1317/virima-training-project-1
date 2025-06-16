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
            System.out.println("3. View Asset by ID");
            System.out.println("4. Update Asset");
            System.out.println("5. Delete Asset");
            System.out.println("6. Upload Assets from File");
            System.out.println("7. Export Assets to File");
            System.out.println("8. Activate Asset");
            System.out.println("9. Deactivate Asset");
            System.out.print("Choose: ");
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
                    Integer idStart = null, idEnd = null;
                    String nameFilter = null, typeFilter = null;
                    Double valueStart = null, valueEnd = null;

                    System.out.println("Choose filter option:");
                    System.out.println("1) Filter by ID Range");
                    System.out.println("2) Filter by Name");
                    System.out.println("3) Filter by Type");
                    System.out.println("4) Filter by Value Range");
                    System.out.println("5) No Filter");
                    System.out.print("Enter your choice: ");
                    int filterChoice = sc.nextInt();
                    sc.nextLine(); // consume newline

                    switch (filterChoice) {
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

                    // Ask for pagination details
                    System.out.print("Enter page number (press Enter for default 1): ");
                    String pageInput = sc.nextLine();
                    int page = pageInput.isEmpty() ? 1 : Integer.parseInt(pageInput);

                    System.out.print("Enter number of records per page (press Enter for default 10): ");
                    String sizeInput = sc.nextLine();
                    int size = sizeInput.isEmpty() ? 10 : Integer.parseInt(sizeInput);

                    service.viewAssetsWithPagination(
                            page, size, idStart, idEnd, nameFilter, typeFilter, valueStart, valueEnd
                    );
                    break;


                case 3:
                    System.out.print("Enter Asset ID: ");
                    int id = sc.nextInt();
                    Asset found = service.getAssetById(id);
                    if (found != null) System.out.println(found);
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
                    sc.nextLine();
                    System.out.print("Enter Excel file path to upload: ");
                    String upath = sc.nextLine();
                    service.uploadAssetsFromExcel(upath);
                    break;

                case 7:
                    sc.nextLine();
                    System.out.print("Enter Excel file path to export: ");
                    String epath = sc.nextLine();
                    service.exportAssetsToExcel(epath);
                    break;

                case 8:
                    System.out.print("Enter ID to activate asset: ");
                    int activateId = sc.nextInt();
                    service.activateAssets(activateId);
                    break;

                case 9:
                    System.out.print("Enter ID to deactivate asset: ");
                    int deactivateId = sc.nextInt();
                    service.deactivateAssets(deactivateId);
                    break;


                default:
                    System.out.println("Invalid choice!");
            }
        }
    }
}
