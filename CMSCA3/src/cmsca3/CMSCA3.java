package cmsca3;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class CMSCA3 {

    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/";
    private static final String DATABASE_NAME = "CMSCA3";
    private static final String USER = "root";
    private static final String PASSWORD = "Carrapato1";

    public static void main(String[] args) {
        try ( Scanner scanner = new Scanner(System.in)) {

            try ( Connection connection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);  Statement statement = connection.createStatement()) {

                String createDatabaseSql = "CREATE DATABASE IF NOT EXISTS " + DATABASE_NAME;
                statement.executeUpdate(createDatabaseSql);
                System.out.println("Database " + DATABASE_NAME + " created successfully");

                String useDatabaseSql = "USE " + DATABASE_NAME;
                statement.executeUpdate(useDatabaseSql);

                createTables(statement);

                System.out.println("Choose an action:");
                System.out.println("1. Insert data into a table");
                System.out.println("2. Access report");
                System.out.print("Enter your choice (1 or 2): ");
                int choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1:
                        insertData(scanner, statement);
                        break;
                    case 2:
                        accessReport(scanner, statement);
                        break;
                    default:
                        System.out.println("Invalid choice!");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void createTables(Statement statement) throws SQLException {
        // Create course_report table
        String createCourseReportSql = "CREATE TABLE IF NOT EXISTS course_report ("
                + "module_name VARCHAR(255), "
                + "programme VARCHAR(255), "
                + "enrolled_students INT, "
                + "lecturer VARCHAR(255), "
                + "room VARCHAR(255))";
        statement.executeUpdate(createCourseReportSql);
        System.out.println("Table course_report created successfully");

        // Create student_report table
        String createStudentReportSql = "CREATE TABLE IF NOT EXISTS student_report ("
                + "student_name VARCHAR(255), "
                + "student_number VARCHAR(20), "
                + "programme VARCHAR(255), "
                + "current_modules TEXT, "
                + "completed_modules TEXT, "
                + "modules_to_repeat TEXT)";
        statement.executeUpdate(createStudentReportSql);
        System.out.println("Table student_report created successfully");

        // Create lecturer_report table
        String createLecturerReportSql = "CREATE TABLE IF NOT EXISTS lecturer_report ("
                + "lecturer_name VARCHAR(255), "
                + "role VARCHAR(50), "
                + "modules_taught TEXT, "
                + "student_count INT, "
                + "classes_teachable TEXT)";
        statement.executeUpdate(createLecturerReportSql);
        System.out.println("Table lecturer_report created successfully");
    }

    private static void insertDataIntoCourseReport(Statement statement, Scanner scanner) throws SQLException {
        System.out.println("Enter module name:");
        String moduleName = scanner.nextLine();
        System.out.println("Enter programme:");
        String programme = scanner.nextLine();
        System.out.println("Enter enrolled students:");
        int enrolledStudents = scanner.nextInt();
        scanner.nextLine(); // Consume newline
        System.out.println("Enter lecturer:");
        String lecturer = scanner.nextLine();
        System.out.println("Enter room:");
        String room = scanner.nextLine();

        String insertSql = "INSERT INTO course_report (module_name, programme, enrolled_students, lecturer, room) VALUES ('"
                + moduleName + "', '" + programme + "', " + enrolledStudents + ", '" + lecturer + "', '" + room + "')";
        statement.executeUpdate(insertSql);
        System.out.println("Data inserted into course_report table successfully");
    }

    private static void insertDataIntoStudentReport(Statement statement, Scanner scanner) throws SQLException {
        System.out.println("Enter student name:");
        String studentName = scanner.nextLine();
        System.out.println("Enter student number:");
        String studentNumber = scanner.nextLine();
        System.out.println("Enter programme:");
        String programme = scanner.nextLine();
        System.out.println("Enter current modules:");
        String currentModules = scanner.nextLine();
        System.out.println("Enter completed modules:");
        String completedModules = scanner.nextLine(); // Change to 'completed_modules'
        System.out.println("Enter modules to repeat:");
        String modulesToRepeat = scanner.nextLine();

        String insertSql = "INSERT INTO student_report (student_name, student_number, programme, current_modules, completed_modules, modules_to_repeat) VALUES ('"
                + studentName + "', '" + studentNumber + "', '" + programme + "', '" + currentModules + "', '" + completedModules + "', '" + modulesToRepeat + "')";
        statement.executeUpdate(insertSql);
        System.out.println("Data inserted into student_report table successfully");
    }

    private static void insertDataIntoLecturerReport(Statement statement, Scanner scanner) throws SQLException {
        System.out.println("Enter lecturer name:");
        String lecturerName = scanner.nextLine();
        System.out.println("Enter role:");
        String role = scanner.nextLine();
        System.out.println("Enter modules taught:");
        String modulesTaught = scanner.nextLine();
        System.out.println("Enter student count:");
        int studentCount = scanner.nextInt();
        scanner.nextLine();
        System.out.println("Enter classes teachable:");
        String classesTeachable = scanner.nextLine();

        String insertSql = "INSERT INTO lecturer_report (lecturer_name, role, modules_taught, student_count, classes_teachable) VALUES ('"
                + lecturerName + "', '" + role + "', '" + modulesTaught + "', " + studentCount + ", '" + classesTeachable + "')";
        statement.executeUpdate(insertSql);
        System.out.println("Data inserted into lecturer_report table successfully");
    }

    private static void accessReport(Scanner scanner, Statement statement) throws SQLException {
        System.out.println("Choose a report to access:");
        System.out.println("1. Course Report");
        System.out.println("2. Student Report");
        System.out.println("3. Lecturer Report");
        System.out.print("Enter your choice (1, 2, or 3): ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 1:
                displayTable(statement, "course_report");
                break;
            case 2:
                displayTable(statement, "student_report");
                break;
            case 3:
                displayTable(statement, "lecturer_report");
                break;
            default:
                System.out.println("Invalid choice!");
        }
    }

    private static void displayTable(Statement statement, String tableName) throws SQLException {
        String query = "SELECT * FROM " + tableName;
        ResultSet resultSet = statement.executeQuery(query);

        ResultSetMetaData metaData = resultSet.getMetaData();
        int columnCount = metaData.getColumnCount();
        for (int i = 1; i <= columnCount; i++) {
            System.out.print(metaData.getColumnName(i) + "\t");
        }
        System.out.println();

        while (resultSet.next()) {
            for (int i = 1; i <= columnCount; i++) {
                System.out.print(resultSet.getString(i) + "\t");
            }
            System.out.println();
        }
    }

    private static void insertData(Scanner scanner, Statement statement) throws SQLException {
        System.out.println("Choose a table to insert data into:");
        System.out.println("1. Course Report");
        System.out.println("2. Student Report");
        System.out.println("3. Lecturer Report");
        System.out.print("Enter your choice (1, 2, or 3): ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 1:
                insertDataIntoCourseReport(statement, scanner);
                break;
            case 2:
                insertDataIntoStudentReport(statement, scanner);
                break;
            case 3:
                insertDataIntoLecturerReport(statement, scanner);
                break;
            default:
                System.out.println("Invalid choice!");
        }
    }

}
