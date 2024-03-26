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
    public static void main(String[] args) {
        try ( Scanner scanner = new Scanner(System.in)) {
            System.out.println("Welcome to CMSCA3 System");

            UserProfile currentUser = null;
            while (currentUser == null) {
                System.out.println("Choose user profile:");
                System.out.println("1. Admin");
                System.out.println("2. Office");
                System.out.println("3. Lecture");
                System.out.print("Enter your choice (1, 2, or 3): ");
                int choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1:
                        currentUser = authenticateUser(scanner, "admin");
                        break;
                    case 2:
                        currentUser = authenticateUser(scanner, "office");
                        break;
                    case 3:
                        currentUser = authenticateUser(scanner, "lecture");
                        break;
                    default:
                        System.out.println("Invalid choice!");
                }

                if (currentUser == null) {
                    System.out.println("Login failed. Please try again.");
                }
            }

            System.out.println("Login successful! Proceeding with the system...");
            try ( Connection connection = DriverManager.getConnection(JDBC_URL + DATABASE_NAME, USER, PASSWORD);  Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
                createTables(statement); // Create tables if they don't exist
                boolean running = true;
                while (running) {
                    System.out.println("Choose an action:");
                    System.out.println("1. Insert data");
                    System.out.println("2. Access reports");
                    System.out.println("3. Exit");
                    System.out.print("Enter your choice (1, 2, or 3): ");
                    int actionChoice = scanner.nextInt();
                    scanner.nextLine();
                    switch (actionChoice) {
                        case 1:
                            insertData(scanner, statement);
                            break;
                        case 2:
                            accessReport(scanner, connection, statement);
                            break;
                        case 3:
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
        System.out.println("Table course_report created successfully");

        String createStudentReportSql = "CREATE TABLE IF NOT EXISTS student_report ("
                + "student_name VARCHAR(255), "
                + "student_number VARCHAR(20), "
                + "programme VARCHAR(255), "
                + "current_modules TEXT, "
                + "completed_modules TEXT, "
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

    private static void insertDataIntoCourseReport(Statement statement, Scanner scanner) throws SQLException {
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

    private static void accessReport(Scanner scanner, Connection connection, Statement statement) throws SQLException {
        System.out.println("Choose a report to access:");
        System.out.println("1. Course Report");
        System.out.println("2. Student Report");
        System.out.println("3. Lecturer Report");
        System.out.print("Enter your choice (1, 2, or 3): ");
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

    private static class UserProfile {

        private final String username;
        private final String password;

        public UserProfile(String username, String password) {
            this.username = username;
            this.password = password;
        }

        public String getUsername() {
            return username;
        }

        public String getPassword() {
            return password;
        }
    }

    private static UserProfile authenticateAdmin(Scanner scanner) {
        System.out.println("Admin login:");
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        // Add new username and password here
        if (("admin1".equals(username) && "java1".equals(password))
                || ("admin2".equals(username) && "java2".equals(password))
                || ("admin3".equals(username) && "java3".equals(password))) {
            return new UserProfile(username, password);
        } else {
            return null;
        }
    }

    private static UserProfile authenticateOffice(Scanner scanner) {
        System.out.println("Office login:");
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        // Dummy validation, replace with actual logic
        if (("office1".equals(username) && "java1".equals(password))
                || ("office2".equals(username) && "java2".equals(password))
                || ("office3".equals(username) && "java3".equals(password))) {
            return new UserProfile(username, password);
        } else {
            return null;
        }
    }

    private static UserProfile authenticateLecture(Scanner scanner) {
        System.out.println("Lecture login:");
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        // Dummy validation, replace with actual logic
        if (("lecture1".equals(username) && "java1".equals(password))
                || ("lecture2".equals(username) && "java2".equals(password))
                || ("lecture3".equals(username) && "java3".equals(password))) {
            return new UserProfile(username, password);
        } else {
            return null;
        }
    }

    private static UserProfile authenticateUser(Scanner scanner, String profileType) {
        switch (profileType.toLowerCase()) {
            case "admin":
                return authenticateAdmin(scanner);
            case "office":
                return authenticateOffice(scanner);
            case "lecture":
                return authenticateLecture(scanner);
            default:
                System.out.println("Invalid profile type!");
                return null;
        }
    }

}
