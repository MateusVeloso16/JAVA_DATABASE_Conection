package cmsca3;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class CMSCA3 {

    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/";
    private static final String DATABASE_NAME = "CMSCA3";
    private static final String USER = "root";
    private static final String PASSWORD = "Carrapato1";

    public static void main(String[] args) {
        try {
            try (Connection connection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
                 Statement statement = connection.createStatement()) {

                String createDatabaseSql = "CREATE DATABASE IF NOT EXISTS " + DATABASE_NAME;
                statement.executeUpdate(createDatabaseSql);
                System.out.println("Database " + DATABASE_NAME + " created successfully");

                String useDatabaseSql = "USE " + DATABASE_NAME;
                statement.executeUpdate(useDatabaseSql);

                createTables(statement);
            }
        } catch (SQLException e) {
            e.printStackTrace(); 
        }
    }

    private static void createTables(Statement statement) throws SQLException {
        String createCourseReportSql = "CREATE TABLE IF NOT EXISTS course_report ("
                + "module_name VARCHAR(255), "
                + "programme VARCHAR(255), "
                + "enrolled_students INT, "
                + "lecturer VARCHAR(255), "
                + "room VARCHAR(255))";
        statement.executeUpdate(createCourseReportSql);
        System.out.println("Table course_report created successfully");

        String createStudentReportSql = "CREATE TABLE IF NOT EXISTS student_report ("
                + "student_name VARCHAR(255), "
                + "student_number VARCHAR(20), "
                + "programme VARCHAR(255), "
                + "current_modules TEXT, "
                + "completed_modules_and_grades TEXT, "
                + "modules_to_repeat TEXT)";
        statement.executeUpdate(createStudentReportSql);
        System.out.println("Table student_report created successfully");

        String createLecturerReportSql = "CREATE TABLE IF NOT EXISTS lecturer_report ("
                + "lecturer_name VARCHAR(255), "
                + "role VARCHAR(50), "
                + "modules_taught TEXT, "
                + "student_count INT, "
                + "classes_teachable TEXT)";
        statement.executeUpdate(createLecturerReportSql);
        System.out.println("Table lecturer_report created successfully");
    }
}
