package cmsca3;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.Scanner;
import java.awt.Desktop;
import java.io.File;

public class CMSCA3 {

    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/";
    private static final String DATABASE_NAME = "CMSCA3";
    private static final String USER = "root";
    private static final String PASSWORD = "Carrapato1";

    @SuppressWarnings("CallToPrintStackTrace")
    private static String authenticateUser(Scanner scanner) {
        try {
            Connection connection = DriverManager.getConnection(JDBC_URL + DATABASE_NAME, USER, PASSWORD);
            Statement statement = connection.createStatement();

            createTables(statement);
            insertUserCredentials(statement);

            System.out.println("Select user (only 'Admin' is available):");
            System.out.print("Enter 'Admin': ");
            String selectedUser = scanner.nextLine().trim();
            if (!selectedUser.equalsIgnoreCase("Admin")) {
                System.out.println("Invalid user. Only 'Admin' is allowed.");
                return null;
            }

            System.out.println("Enter username:");
            String username = scanner.nextLine();
            System.out.println("Enter password:");
            String password = scanner.nextLine();

            System.out.println("Entered username: " + username);
            System.out.println("Entered password: " + password);

            ResultSet resultSet = statement.executeQuery("SELECT * FROM user_credentials WHERE username = '" + username + "' AND password = '" + password + "'");
            if (resultSet.next()) {

                String permissions = resultSet.getString("permissions");
                System.out.println("Permissions retrieved from result set: " + permissions);

                System.out.println("Rows found in result set.");
                System.out.println("Authentication successful. Welcome, Admin!");
                return resultSet.getString("permissions");
            } else {
                if (username.equals("admin") && password.equals("java")) {
                    System.out.println("No rows found in result set.");
                    System.out.println("Authentication successful. Welcome, Admin!");
                    return "create_tables,insert_data,view_reports,view_table,export_csv,export_txt";
                } else {
                    System.out.println("Authentication failed. Invalid username or password.");
                    return null;
                }
            }
        } catch (SQLException e) {
            System.out.println("An error occurred during authentication: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    @SuppressWarnings("CallToPrintStackTrace")
    public static void main(String[] args) {
        try ( Scanner scanner = new Scanner(System.in)) {
            System.out.println("Welcome to CMSCA3 System");
            System.out.println("Proceeding with the system...");

            try ( Connection connection = DriverManager.getConnection(JDBC_URL + DATABASE_NAME, USER, PASSWORD);  Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE)) {

                createTables(statement);
                insertUserCredentials(statement);

                String permissions = authenticateUser(scanner);
                if (permissions == null) {
                    System.out.println("Authentication failed. Exiting CMSCA3 System...");
                    return;
                }

                boolean running = true;
                while (running) {
                    System.out.println("Choose an action:");
                    System.out.println("1. Insert data");
                    System.out.println("2. Access reports");
                    System.out.println("3. Delete data");
                    System.out.println("4. Exit");
                    System.out.print("Enter your choice (1, 2, 3, or 4): ");
                    int actionChoice = scanner.nextInt();
                    scanner.nextLine();

                    switch (actionChoice) {
                        case 1:
                            if (permissions.contains("insert_data")) {
                                insertData(scanner, statement);
                            } else {
                                System.out.println("You do not have permission to insert data.");
                            }
                            break;
                        case 2:
                            if (permissions.contains("view_reports")) {
                                accessReport(scanner, connection, statement);
                            } else {
                                System.out.println("You do not have permission to access reports.");
                            }
                            break;
                        case 3:
                            if (permissions.contains("delete_data")) {
                                deleteUserData(statement, scanner);
                            } else {
                                System.out.println("You do not have permission to delete data.");
                            }
                            break;
                        case 4:
                            System.out.println("Exiting CMSCA3 System...");
                            running = false;
                            break;
                        default:
                            System.out.println("Invalid choice!");
                    }
                }
            } catch (SQLException e) {
                System.out.println("An error occurred: " + e.getMessage());
                e.printStackTrace();
            }
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

        String createStudentReportSql = "CREATE TABLE IF NOT EXISTS student_report ("
                + "student_name VARCHAR(255), "
                + "student_number VARCHAR(20), "
                + "programme VARCHAR(255), "
                + "current_modules TEXT, "
                + "completed_modules TEXT, "
                + "modules_to_repeat TEXT)";
        statement.executeUpdate(createStudentReportSql);

        String createLecturerReportSql = "CREATE TABLE IF NOT EXISTS lecturer_report ("
                + "lecturer_name VARCHAR(255), "
                + "role VARCHAR(50), "
                + "modules_taught TEXT, "
                + "student_count INT, "
                + "classes_teachable TEXT)";
        statement.executeUpdate(createLecturerReportSql);

        String createUserTableSql = "CREATE TABLE IF NOT EXISTS user_credentials ("
                + "username VARCHAR(255) PRIMARY KEY, "
                + "password VARCHAR(255), "
                + "permissions VARCHAR(255))";
        statement.executeUpdate(createUserTableSql);

    }

    @SuppressWarnings("CallToPrintStackTrace")
    private static void insertUserCredentials(Statement statement) {
        try {
            ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) FROM user_credentials");
            resultSet.next();
            int rowCount = resultSet.getInt(1);
            if (rowCount > 0) {
                return;
            }

            String[][] usersAndPasswordsAndPermissions = {
                {"admin", "java", "create_tables,insert_data,view_reports,view_table,export_csv,export_txt,delete_data"},
                {"office", "java2", "view_reports,view_table,export_csv,insert_data,export_txt,delete_data"},
                {"lecture", "java3", "insert_data,view_reports,view_table,export_csv,export_txt,delete_data"}
            };

            for (String[] userAndPasswordAndPermissions : usersAndPasswordsAndPermissions) {
                String username = userAndPasswordAndPermissions[0];
                String password = userAndPasswordAndPermissions[1];
                String permissions = userAndPasswordAndPermissions[2];
                String insertSql = "INSERT INTO user_credentials (username, password, permissions) VALUES ('"
                        + username + "', '" + password + "', '" + permissions + "')";
                statement.executeUpdate(insertSql);
                System.out.println();
                //System.out.println("Data inserted into user_credentials table successfully");
                System.out.println();
            }
        } catch (SQLException e) {
            System.out.println("An error occurred while inserting data into user_credentials table: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void insertDataIntoCourseReport(Statement statement, Scanner scanner) throws SQLException {

        System.out.println();
        System.out.println("Enter module name:");
        String moduleName = scanner.nextLine();
        System.out.println("Enter programme:");
        String programme = scanner.nextLine();
        System.out.println("Enter enrolled students:");
        int enrolledStudents = scanner.nextInt();
        scanner.nextLine();
        System.out.println("Enter lecturer:");
        String lecturer = scanner.nextLine();
        System.out.println("Enter room:");
        String room = scanner.nextLine();
        System.out.println();

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
        String completedModules = scanner.nextLine();
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

    private static void insertDataIntoUserCredentials(Statement statement, Scanner scanner) throws SQLException {

        System.out.println("Enter username:");
        String username = scanner.nextLine();
        System.out.println("Enter password:");
        String password = scanner.nextLine();
        System.out.println("Enter permissions separated by coma and without spaces: create_tables,insert_data,view_reports,view_table,export_csv,export_txt");
        String permissions = scanner.nextLine();

        ResultSet resultSet = statement.executeQuery("SELECT * FROM user_credentials WHERE username = '" + username + "'");
        if (resultSet.next()) {
            System.out.println("Username already exists. Data not inserted.");
        } else {

            String insertSql = "INSERT INTO user_credentials (username, password,permissions) VALUES ('"
                    + username + "', '" + password + "', '" + permissions + "')";
            statement.executeUpdate(insertSql);
            System.out.println("Data inserted into user_credentials table successfully");
        }

    }

    private static void accessReport(Scanner scanner, Connection connection, Statement statement) throws SQLException {

        System.out.println("Choose a report to access:");
        System.out.println("1. Course Report");
        System.out.println("2. Student Report");
        System.out.println("3. Lecturer Report");
        System.out.println("4. User Credentials Report");
        System.out.print("Enter your choice (1, 2, 3, or 4): ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 1:
                displayReport(connection, statement, "course_report", scanner);
                break;
            case 2:
                displayReport(connection, statement, "student_report", scanner);
                break;
            case 3:
                displayReport(connection, statement, "lecturer_report", scanner);
                break;
            case 4:
                displayUserCredentials(connection, statement, scanner);
                break;
            default:
                System.out.println("Invalid choice!");
        }

    }

    private static void displayUserCredentials(Connection connection, Statement statement, Scanner scanner) throws SQLException {

        System.out.println("Choose output format for User Credentials Report:");
        System.out.println("A. Console");
        System.out.println("B. CSV File");
        System.out.println("C. TXT File");
        System.out.print("Enter your choice (A, B, or C): ");
        String choice = scanner.nextLine().toUpperCase();

        switch (choice) {
            case "A":
                displayTable(connection, statement, "user_credentials");
                break;
            case "B":
                exportToCSV(connection, statement, "user_credentials");
                break;
            case "C":
                exportToTXT(connection, statement, "user_credentials");
                break;
            default:
                System.out.println("Invalid choice!");
        }

    }

    private static void displayTable(Connection connection, Statement statement, String tableName) throws SQLException {

        String query = "SELECT * FROM " + tableName;
        ResultSet resultSet = statement.executeQuery(query);

        ResultSetMetaData metaData = resultSet.getMetaData();
        int columnCount = metaData.getColumnCount();

        int[] columnWidths = new int[columnCount];

        for (int i = 1; i <= columnCount; i++) {
            columnWidths[i - 1] = metaData.getColumnName(i).length();
        }

        LinkedList<String[]> rows = new LinkedList<>();

        while (resultSet.next()) {
            String[] row = new String[columnCount];
            for (int i = 1; i <= columnCount; i++) {
                String value = resultSet.getString(i);
                row[i - 1] = value;
                if (value != null && value.length() > columnWidths[i - 1]) {
                    columnWidths[i - 1] = value.length();
                }
            }
            rows.add(row);
        }

        for (int i = 1; i <= columnCount; i++) {
            System.out.printf("%-" + (columnWidths[i - 1] + 2) + "s", metaData.getColumnName(i));
        }
        System.out.println();

        for (String[] row : rows) {
            for (int i = 0; i < columnCount; i++) {
                System.out.printf("%-" + (columnWidths[i] + 2) + "s", row[i]);
            }
            System.out.println();
        }

    }

    private static void insertData(Scanner scanner, Statement statement) throws SQLException {

        System.out.println("Choose a table to insert data into:");
        System.out.println("1. Course Report");
        System.out.println("2. Student Report");
        System.out.println("3. Lecturer Report");
        System.out.println("4. User Credentials");
        System.out.print("Enter your choice (1, 2, 3, or 4): ");
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
            case 4:
                insertDataIntoUserCredentials(statement, scanner);
                break;
            default:
                System.out.println("Invalid choice!");
        }

    }

    private static void deleteUserData(Statement statement, Scanner scanner) throws SQLException {
        System.out.println("Enter username:");
        String username = scanner.nextLine();
        System.out.println("Enter password:");
        String password = scanner.nextLine();

        String deleteSql = "DELETE FROM user_credentials WHERE username = '" + username + "' AND password = '" + password + "'";
        int rowsDeleted = statement.executeUpdate(deleteSql);
        if (rowsDeleted > 0) {
            System.out.println("User credentials deleted successfully.");
        } else {
            System.out.println("No matching user credentials found to delete.");
        }
    }

    private static void displayReport(Connection connection, Statement statement, String tableName, Scanner scanner) throws SQLException {

        System.out.println("Choose output format:");
        System.out.println("A. Console");
        System.out.println("B. CSV File");
        System.out.println("C. TXT File");
        System.out.print("Enter your choice (A, B, or C): ");
        String choice = scanner.nextLine().toUpperCase();

        switch (choice) {
            case "A":
                displayTable(connection, statement, tableName);
                break;
            case "B":
                exportToCSV(connection, statement, tableName);
                break;
            case "C":
                exportToTXT(connection, statement, tableName);
                break;
            default:
                System.out.println("Invalid choice!");
        }

    }

    @SuppressWarnings("CallToPrintStackTrace")
    private static void exportToCSV(Connection connection, Statement statement, String tableName) throws SQLException {

        try ( FileWriter writer = new FileWriter(tableName + ".csv")) {
            ResultSet resultSet = statement.executeQuery("SELECT * FROM " + tableName);
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();

            for (int i = 1; i <= columnCount; i++) {
                writer.append(metaData.getColumnName(i));
                if (i < columnCount) {
                    writer.append(",");
                }
            }
            writer.append("\n");

            while (resultSet.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    writer.append(resultSet.getString(i));
                    if (i < columnCount) {
                        writer.append(",");
                    }
                }
                writer.append("\n");
            }

            System.out.println("Data exported to " + tableName + ".csv successfully");

            if (Desktop.isDesktopSupported()) {
                try {
                    File file = new File(tableName + ".csv");
                    Desktop.getDesktop().open(file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("Opening files in NetBeans is not supported on this platform.");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @SuppressWarnings("CallToPrintStackTrace")
    private static void exportToTXT(Connection connection, Statement statement, String tableName) throws SQLException {

        try ( FileWriter writer = new FileWriter(tableName + ".txt")) {
            ResultSet resultSet = statement.executeQuery("SELECT * FROM " + tableName);
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();

            for (int i = 1; i <= columnCount; i++) {
                writer.append(metaData.getColumnName(i));
                if (i < columnCount) {
                    writer.append("\t");
                }
            }
            writer.append("\n");

            while (resultSet.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    writer.append(resultSet.getString(i));
                    if (i < columnCount) {
                        writer.append("\t");
                    }
                }
                writer.append("\n");
            }

            System.out.println("Data exported to " + tableName + ".txt successfully");

            if (Desktop.isDesktopSupported()) {
                try {
                    File file = new File(tableName + ".txt");
                    Desktop.getDesktop().open(file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("Opening files in NetBeans is not supported on this platform.");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
