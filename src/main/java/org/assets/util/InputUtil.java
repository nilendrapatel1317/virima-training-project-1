package org.assets.util;

import java.util.InputMismatchException;
import java.util.Scanner;

public class InputUtil {
    private static final Scanner sc = new Scanner(System.in);

    public static int getInt(String message) {
        while (true) {
            System.out.print(message);
            try {
                return Integer.parseInt(sc.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("❌ Please enter a valid integer.");
            }
        }
    }

    public static double getDouble(String message) {
        while (true) {
            System.out.print(message);
            try {
                return Double.parseDouble(sc.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("❌ Please enter a valid number.");
            }
        }
    }

    public static String getString(String message) {
        while (true) {
            System.out.print(message);
            String input = sc.nextLine().trim();
            if (input.length() <= 0) {
                System.out.println("❌ Value can not be empty.");
            } else {
                return input;
            }
        }
    }

    // Allows empty input (for optional/skippable fields)
    public static String getOptionalString(String message) {
        System.out.print(message);
        return sc.nextLine().trim();
    }

    public static boolean getBoolean(String message) {
        while (true) {
            System.out.print(message + " (true/false): ");
            String input = sc.nextLine().trim().toLowerCase();
            if (input.equals("true") || input.equals("false")) {
                return Boolean.parseBoolean(input);
            } else {
                System.out.println("❌ Please enter true or false.");
            }
        }
    }
}
