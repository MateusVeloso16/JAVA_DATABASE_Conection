package cmsca3;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class CMSCA3 {

    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/";
    private static final String USER = "root";
    private static final String PASSWORD = "Carrapato1";

    public static void main(String[] args) {
        try {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
            } catch (ClassNotFoundException e1) {
            try {
                    Class.forName("com.mysql.jdbc.Driver");
            } catch (ClassNotFoundException e2) {
                    // Handle exception if neither class is found
                }
            }


            try (Connection connection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
                 Statement statement = connection.createStatement()) {

                String sql = "CREATE DATABASE IF NOT EXISTS CMSCA3";
                statement.executeUpdate(sql);
                System.out.println("Database CMSCA3 created successfully");
            }
        } catch (SQLException e) {
            
        }
    }
}
