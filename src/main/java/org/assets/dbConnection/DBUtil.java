package org.assets.dbConnection;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBUtil {
    private static final String URL = "jdbc:mysql://localhost:3306/virima?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASS = "root";

    public static Connection getConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(URL, USER, PASS);
            return conn;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
