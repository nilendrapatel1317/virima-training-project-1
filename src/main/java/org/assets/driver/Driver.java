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
            System.out.println("\n--- Asset Management ---");
            System.out.println("1. Add Asset");
            System.out.println("2. View Assets");
            System.out.println("3. Get Asset by ID");
            System.out.println("4. Update Asset");
            System.out.println("5. Delete Asset");
            System.out.println("6. Upload Assets from File");
            System.out.println("7. Export Assets to File");
            System.out.println("8. Exit");
            System.out.print("Choose: ");
            int choice = sc.nextInt();

            switch (choice) {
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
                    service.viewAssets();
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
                    System.out.println("Exiting...");
                    System.out.println("Thanks For Using Assets Management System Application !");
                    return;

                default:
                    System.out.println("Invalid choice!");
            }
        }
    }
}
