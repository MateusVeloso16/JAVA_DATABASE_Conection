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

public class CMSCA3 {         //Link to GitHub:  https://github.com/MateusVeloso16/CMSCA3.git

    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/";
    private static final String DATABASE_NAME = "CMSCA3";
    private static final String USER = "root";
    private static final String PASSWORD = "Carrapato1";

    @SuppressWarnings("CallToPrintStackTrace")
    // This method is responsible for authenticating a user
private static String authenticateUser(Scanner scanner) {
    try {
        // Establishing a connection to the database
        Connection connection = DriverManager.getConnection(JDBC_URL + DATABASE_NAME, USER, PASSWORD);
        // Creating a statement object for executing SQL queries
        Statement statement = connection.createStatement();

        // Creating necessary tables in the database if they don't exist
        createTables(statement);
        // Inserting user credentials into the database if not already present
        insertUserCredentials(statement);

        // Prompting the user to select a user (only 'Admin' is available in this case)
        System.out.println("Select user (only 'Admin' is available):");
        System.out.print("Enter 'Admin': ");
        String selectedUser = scanner.nextLine().trim();
        // Checking if the selected user is 'Admin'
        if (!selectedUser.equalsIgnoreCase("Admin")) {
            System.out.println("Invalid user. Only 'Admin' is allowed.");
            return null;
        }

        // Prompting the user to enter username and password
        System.out.println("Enter username:");
        String username = scanner.nextLine();
        System.out.println("Enter password:");
        String password = scanner.nextLine();

        // Printing the entered username and password
        System.out.println("Entered username: " + username);
        System.out.println("Entered password: " + password);

        // Querying the database to check if the entered credentials are valid
        ResultSet resultSet = statement.executeQuery("SELECT * FROM user_credentials WHERE username = '" + username + "' AND password = '" + password + "'");
        // If the result set is not empty, the user credentials are valid
        if (resultSet.next()) {
            // Retrieving permissions for the authenticated user
            String permissions = resultSet.getString("permissions");
            System.out.println("Permissions retrieved from result set: " + permissions);
            // Authentication successful message
            System.out.println("Rows found in result set.");
            System.out.println("Authentication successful. Welcome, Admin!");
            return resultSet.getString("permissions");
        } else {
            // If no matching user credentials found, checking if it's the default 'Admin' credentials
            if (username.equals("admin") && password.equals("java")) {
                // Authentication successful for default 'Admin' credentials
                System.out.println("No rows found in result set.");
                System.out.println("Authentication successful. Welcome, Admin!");
                // Returning default permissions for 'Admin'
                return "create_tables,insert_data,view_reports,view_table,export_csv,export_txt";
            } else {
                // Authentication failed message for invalid credentials
                System.out.println("Authentication failed. Invalid username or password.");
                return null;
            }
        }
    } catch (SQLException e) {
        // Exception handling for SQL errors during authentication
        System.out.println("An error occurred during authentication: " + e.getMessage());
        e.printStackTrace();
        return null;
    }
}


    // This is the main method where the program execution starts
    @SuppressWarnings("CallToPrintStackTrace")
    public static void main(String[] args) {
    try (Scanner scanner = new Scanner(System.in)) {
        // Printing welcome message
        System.out.println("Welcome to CMSCA3 System");
        System.out.println("Proceeding with the system...");

        try (Connection connection = DriverManager.getConnection(JDBC_URL + DATABASE_NAME, USER, PASSWORD);
             Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE)) {

            // Creating necessary tables in the database if they don't exist
            createTables(statement);
            // Inserting user credentials into the database if not already present
            insertUserCredentials(statement);

            // Authenticating the user and retrieving permissions
            String permissions = authenticateUser(scanner);
            // If authentication failed, exiting the system
            if (permissions == null) {
                System.out.println("Authentication failed. Exiting CMSCA3 System...");
                return;
            }

            // Handling user actions in a loop until user chooses to exit
            boolean running = true;
            while (running) {
                // Prompting user to choose an action
                System.out.println("Choose an action:");
                System.out.println("1. Insert data");
                System.out.println("2. Access reports");
                System.out.println("3. Delete data");
                System.out.println("4. Exit");
                System.out.print("Enter your choice (1, 2, 3, or 4): ");
                int actionChoice = scanner.nextInt();
                scanner.nextLine();

                // Switch case to handle different user actions
                switch (actionChoice) {
                    case 1:
                        // If user has permission to insert data, insert data
                        if (permissions.contains("insert_data")) {
                            insertData(scanner, statement);
                        } else {
                            // Otherwise, inform user about lack of permission
                            System.out.println("You do not have permission to insert data.");
                        }
                        break;
                    case 2:
                        // If user has permission to access reports, access reports
                        if (permissions.contains("view_reports")) {
                            accessReport(scanner, connection, statement);
                        } else {
                            // Otherwise, inform user about lack of permission
                            System.out.println("You do not have permission to access reports.");
                        }
                        break;
                    case 3:
                        // If user has permission to delete data, delete data
                        if (permissions.contains("delete_data")) {
                            deleteUserData(statement, scanner);
                        } else {
                            // Otherwise, inform user about lack of permission
                            System.out.println("You do not have permission to delete data.");
                        }
                        break;
                    case 4:
                        // Exiting the system if user chooses to exit
                        System.out.println("Exiting CMSCA3 System...");
                        running = false;
                        break;
                    default:
                        // Informing user about invalid choice
                        System.out.println("Invalid choice!");
                }
            }
        } catch (SQLException e) {
            // Exception handling for SQL errors
            System.out.println("An error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }
}


    // This method creates necessary tables in the database if they don't exist
private static void createTables(Statement statement) throws SQLException {
    // SQL query to create the course_report table
    String createCourseReportSql = "CREATE TABLE IF NOT EXISTS course_report ("
            + "module_name VARCHAR(255), "
            + "programme VARCHAR(255), "
            + "enrolled_students INT, "
            + "lecturer VARCHAR(255), "
            + "room VARCHAR(255))";
    // Executing the SQL query to create the course_report table
    statement.executeUpdate(createCourseReportSql);

    // SQL query to create the student_report table
    String createStudentReportSql = "CREATE TABLE IF NOT EXISTS student_report ("
            + "student_name VARCHAR(255), "
            + "student_number VARCHAR(20), "
            + "programme VARCHAR(255), "
            + "current_modules TEXT, "
            + "completed_modules TEXT, "
            + "modules_to_repeat TEXT)";
    // Executing the SQL query to create the student_report table
    statement.executeUpdate(createStudentReportSql);

    // SQL query to create the lecturer_report table
    String createLecturerReportSql = "CREATE TABLE IF NOT EXISTS lecturer_report ("
            + "lecturer_name VARCHAR(255), "
            + "role VARCHAR(50), "
            + "modules_taught TEXT, "
            + "student_count INT, "
            + "classes_teachable TEXT)";
    // Executing the SQL query to create the lecturer_report table
    statement.executeUpdate(createLecturerReportSql);

    // SQL query to create the user_credentials table
    String createUserTableSql = "CREATE TABLE IF NOT EXISTS user_credentials ("
            + "username VARCHAR(255) PRIMARY KEY, "
            + "password VARCHAR(255), "
            + "permissions VARCHAR(255))";
    // Executing the SQL query to create the user_credentials table
    statement.executeUpdate(createUserTableSql);
}


    // This method inserts initial user credentials into the database if no records exist
@SuppressWarnings("CallToPrintStackTrace")
private static void insertUserCredentials(Statement statement) {
    try {
        // Querying the database to get the count of records in the user_credentials table
        ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) FROM user_credentials");
        resultSet.next();
        int rowCount = resultSet.getInt(1);
        // If there are existing records, return without inserting new ones
        if (rowCount > 0) {
            return;
        }

        // Array containing initial user credentials and permissions
        String[][] usersAndPasswordsAndPermissions = {
            {"admin", "java", "create_tables,insert_data,view_reports,view_table,export_csv,export_txt,delete_data"},
            {"office", "java2", "view_reports,view_table,export_csv,insert_data,export_txt,delete_data"},
            {"lecture", "java3", "insert_data,view_reports,view_table,export_csv,export_txt,delete_data"}
        };

        // Iterating through the array and inserting each user's credentials into the database
        for (String[] userAndPasswordAndPermissions : usersAndPasswordsAndPermissions) {
            String username = userAndPasswordAndPermissions[0];
            String password = userAndPasswordAndPermissions[1];
            String permissions = userAndPasswordAndPermissions[2];
            // Constructing the SQL insert query
            String insertSql = "INSERT INTO user_credentials (username, password, permissions) VALUES ('"
                    + username + "', '" + password + "', '" + permissions + "')";
            // Executing the SQL insert query
            statement.executeUpdate(insertSql);
            // Printing a blank line for formatting purposes
            System.out.println();
            // Printing a message (commented out) indicating successful insertion (optional)
            // System.out.println("Data inserted into user_credentials table successfully");
            System.out.println();
        }
    } catch (SQLException e) {
        // Handling SQL exceptions if any occur during the insertion process
        System.out.println("An error occurred while inserting data into user_credentials table: " + e.getMessage());
        e.printStackTrace();
    }
}


    // This method inserts data into the course_report table of the database
private static void insertDataIntoCourseReport(Statement statement, Scanner scanner) throws SQLException {
    // Prompting the user to enter module name
    System.out.println();
    System.out.println("Enter module name:");
    // Reading the module name input from the user
    String moduleName = scanner.nextLine();
    // Prompting the user to enter programme
    System.out.println("Enter programme:");
    // Reading the programme input from the user
    String programme = scanner.nextLine();
    // Prompting the user to enter enrolled students count
    System.out.println("Enter enrolled students:");
    // Reading the enrolled students count input from the user
    int enrolledStudents = scanner.nextInt();
    scanner.nextLine(); // Consume the newline character
    // Prompting the user to enter lecturer
    System.out.println("Enter lecturer:");
    // Reading the lecturer input from the user
    String lecturer = scanner.nextLine();
    // Prompting the user to enter room
    System.out.println("Enter room:");
    // Reading the room input from the user
    String room = scanner.nextLine();
    System.out.println();

    // Constructing the SQL insert query with the input data
    String insertSql = "INSERT INTO course_report (module_name, programme, enrolled_students, lecturer, room) VALUES ('"
            + moduleName + "', '" + programme + "', " + enrolledStudents + ", '" + lecturer + "', '" + room + "')";
    // Executing the SQL insert query to insert the data into the course_report table
    statement.executeUpdate(insertSql);
    // Printing a message indicating successful insertion into the course_report table
    System.out.println("Data inserted into course_report table successfully");
}


    // This method inserts data into the student_report table of the database
private static void insertDataIntoStudentReport(Statement statement, Scanner scanner) throws SQLException {
    // Prompting the user to enter student name
    System.out.println("Enter student name:");
    // Reading the student name input from the user
    String studentName = scanner.nextLine();
    // Prompting the user to enter student number
    System.out.println("Enter student number:");
    // Reading the student number input from the user
    String studentNumber = scanner.nextLine();
    // Prompting the user to enter programme
    System.out.println("Enter programme:");
    // Reading the programme input from the user
    String programme = scanner.nextLine();
    // Prompting the user to enter current modules
    System.out.println("Enter current modules:");
    // Reading the current modules input from the user
    String currentModules = scanner.nextLine();
    // Prompting the user to enter completed modules
    System.out.println("Enter completed modules:");
    // Reading the completed modules input from the user
    String completedModules = scanner.nextLine();
    // Prompting the user to enter modules to repeat
    System.out.println("Enter modules to repeat:");
    // Reading the modules to repeat input from the user
    String modulesToRepeat = scanner.nextLine();

    // Constructing the SQL insert query with the input data
    String insertSql = "INSERT INTO student_report (student_name, student_number, programme, current_modules, completed_modules, modules_to_repeat) VALUES ('"
            + studentName + "', '" + studentNumber + "', '" + programme + "', '" + currentModules + "', '" + completedModules + "', '" + modulesToRepeat + "')";
    // Executing the SQL insert query to insert the data into the student_report table
    statement.executeUpdate(insertSql);
    // Printing a message indicating successful insertion into the student_report table
    System.out.println("Data inserted into student_report table successfully");
}


    // This method inserts data into the lecturer_report table of the database
private static void insertDataIntoLecturerReport(Statement statement, Scanner scanner) throws SQLException {
    // Prompting the user to enter lecturer name
    System.out.println("Enter lecturer name:");
    // Reading the lecturer name input from the user
    String lecturerName = scanner.nextLine();
    // Prompting the user to enter role
    System.out.println("Enter role:");
    // Reading the role input from the user
    String role = scanner.nextLine();
    // Prompting the user to enter modules taught
    System.out.println("Enter modules taught:");
    // Reading the modules taught input from the user
    String modulesTaught = scanner.nextLine();
    // Prompting the user to enter student count
    System.out.println("Enter student count:");
    // Reading the student count input from the user
    int studentCount = scanner.nextInt();
    scanner.nextLine(); // Consume newline character after reading int
    // Prompting the user to enter classes teachable
    System.out.println("Enter classes teachable:");
    // Reading the classes teachable input from the user
    String classesTeachable = scanner.nextLine();

    // Constructing the SQL insert query with the input data
    String insertSql = "INSERT INTO lecturer_report (lecturer_name, role, modules_taught, student_count, classes_teachable) VALUES ('"
            + lecturerName + "', '" + role + "', '" + modulesTaught + "', " + studentCount + ", '" + classesTeachable + "')";
    // Executing the SQL insert query to insert the data into the lecturer_report table
    statement.executeUpdate(insertSql);
    // Printing a message indicating successful insertion into the lecturer_report table
    System.out.println("Data inserted into lecturer_report table successfully");
}


    // This method inserts data into the user_credentials table of the database
private static void insertDataIntoUserCredentials(Statement statement, Scanner scanner) throws SQLException {
    // Prompting the user to enter username
    System.out.println("Enter username:");
    // Reading the username input from the user
    String username = scanner.nextLine();
    // Prompting the user to enter password
    System.out.println("Enter password:");
    // Reading the password input from the user
    String password = scanner.nextLine();
    // Prompting the user to enter permissions separated by commas and without spaces
    System.out.println("Enter permissions separated by comma and without spaces: create_tables,insert_data,view_reports,view_table,export_csv,export_txt");
    // Reading the permissions input from the user
    String permissions = scanner.nextLine();

    // Executing a query to check if the username already exists in the user_credentials table
    ResultSet resultSet = statement.executeQuery("SELECT * FROM user_credentials WHERE username = '" + username + "'");
    // Checking if the username already exists in the database
    if (resultSet.next()) {
        // If the username exists, printing a message indicating that the data was not inserted
        System.out.println("Username already exists. Data not inserted.");
    } else {
        // If the username does not exist, constructing the SQL insert query with the input data
        String insertSql = "INSERT INTO user_credentials (username, password, permissions) VALUES ('"
                + username + "', '" + password + "', '" + permissions + "')";
        // Executing the SQL insert query to insert the data into the user_credentials table
        statement.executeUpdate(insertSql);
        // Printing a message indicating successful insertion into the user_credentials table
        System.out.println("Data inserted into user_credentials table successfully");
    }
}


    // This method allows the user to access different types of reports
private static void accessReport(Scanner scanner, Connection connection, Statement statement) throws SQLException {
    // Displaying the options for accessing reports
    System.out.println("Choose a report to access:");
    System.out.println("1. Course Report");
    System.out.println("2. Student Report");
    System.out.println("3. Lecturer Report");
    System.out.println("4. User Credentials Report");
    System.out.print("Enter your choice (1, 2, 3, or 4): ");
    // Reading the user's choice
    int choice = scanner.nextInt();
    scanner.nextLine();

    // Switching based on the user's choice
    switch (choice) {
        case 1:
            // If the user chooses Course Report, call the displayReport method for course_report
            displayReport(connection, statement, "course_report", scanner);
            break;
        case 2:
            // If the user chooses Student Report, call the displayReport method for student_report
            displayReport(connection, statement, "student_report", scanner);
            break;
        case 3:
            // If the user chooses Lecturer Report, call the displayReport method for lecturer_report
            displayReport(connection, statement, "lecturer_report", scanner);
            break;
        case 4:
            // If the user chooses User Credentials Report, call the displayUserCredentials method
            displayUserCredentials(connection, statement, scanner);
            break;
        default:
            // If the user enters an invalid choice, display an error message
            System.out.println("Invalid choice!");
    }
}


    // This method allows the user to choose the output format for the User Credentials Report
private static void displayUserCredentials(Connection connection, Statement statement, Scanner scanner) throws SQLException {
    // Displaying the options for the output format
    System.out.println("Choose output format for User Credentials Report:");
    System.out.println("A. Console");
    System.out.println("B. CSV File");
    System.out.println("C. TXT File");
    System.out.print("Enter your choice (A, B, or C): ");
    // Reading the user's choice and converting it to uppercase for case-insensitive comparison
    String choice = scanner.nextLine().toUpperCase();

    // Switching based on the user's choice
    switch (choice) {
        case "A":
            // If the user chooses Console, display the user_credentials table in the console
            displayTable(connection, statement, "user_credentials");
            break;
        case "B":
            // If the user chooses CSV File, export the user_credentials table to a CSV file
            exportToCSV(connection, statement, "user_credentials");
            break;
        case "C":
            // If the user chooses TXT File, export the user_credentials table to a TXT file
            exportToTXT(connection, statement, "user_credentials");
            break;
        default:
            // If the user enters an invalid choice, display an error message
            System.out.println("Invalid choice!");
    }
}


    // This method displays the contents of a table in a tabular format
private static void displayTable(Connection connection, Statement statement, String tableName) throws SQLException {
    // Constructing the query to select all rows from the specified table
    String query = "SELECT * FROM " + tableName;
    // Executing the query and obtaining the result set
    ResultSet resultSet = statement.executeQuery(query);

    // Retrieving metadata about the result set
    ResultSetMetaData metaData = resultSet.getMetaData();
    // Getting the number of columns in the result set
    int columnCount = metaData.getColumnCount();

    // Initializing an array to store the maximum width of each column
    int[] columnWidths = new int[columnCount];

    // Iterating over each column to determine the maximum width of each column
    for (int i = 1; i <= columnCount; i++) {
        columnWidths[i - 1] = metaData.getColumnName(i).length();
    }

    // Creating a linked list to store the rows of the result set
    LinkedList<String[]> rows = new LinkedList<>();

    // Iterating over each row in the result set
    while (resultSet.next()) {
        // Creating an array to store the values of the current row
        String[] row = new String[columnCount];
        // Iterating over each column in the row
        for (int i = 1; i <= columnCount; i++) {
            // Getting the value of the current column in the row
            String value = resultSet.getString(i);
            // Storing the value in the row array
            row[i - 1] = value;
            // Updating the maximum width of the column if necessary
            if (value != null && value.length() > columnWidths[i - 1]) {
                columnWidths[i - 1] = value.length();
            }
        }
        // Adding the row to the list of rows
        rows.add(row);
    }

    // Displaying the column names
    for (int i = 1; i <= columnCount; i++) {
        System.out.printf("%-" + (columnWidths[i - 1] + 2) + "s", metaData.getColumnName(i));
    }
    System.out.println();

    // Displaying the rows of the result set
    for (String[] row : rows) {
        for (int i = 0; i < columnCount; i++) {
            System.out.printf("%-" + (columnWidths[i] + 2) + "s", row[i]);
        }
        System.out.println();
    }
}


    // This method prompts the user to choose a table to insert data into and calls the corresponding insertion method
private static void insertData(Scanner scanner, Statement statement) throws SQLException {
    // Displaying the options for choosing a table to insert data into
    System.out.println("Choose a table to insert data into:");
    System.out.println("1. Course Report");
    System.out.println("2. Student Report");
    System.out.println("3. Lecturer Report");
    System.out.println("4. User Credentials");
    System.out.print("Enter your choice (1, 2, 3, or 4): ");
    // Reading the user's choice
    int choice = scanner.nextInt();
    scanner.nextLine();

    // Switching based on the user's choice
    switch (choice) {
        case 1:
            // If the user chooses option 1, call the method to insert data into the course report table
            insertDataIntoCourseReport(statement, scanner);
            break;
        case 2:
            // If the user chooses option 2, call the method to insert data into the student report table
            insertDataIntoStudentReport(statement, scanner);
            break;
        case 3:
            // If the user chooses option 3, call the method to insert data into the lecturer report table
            insertDataIntoLecturerReport(statement, scanner);
            break;
        case 4:
            // If the user chooses option 4, call the method to insert data into the user credentials table
            insertDataIntoUserCredentials(statement, scanner);
            break;
        default:
            // If the user enters an invalid choice, display an error message
            System.out.println("Invalid choice!");
    }
}


    // This method allows the deletion of user credentials from the database based on provided username and password
private static void deleteUserData(Statement statement, Scanner scanner) throws SQLException {
    // Prompting the user to enter username and password for the user credentials to be deleted
    System.out.println("Enter username:");
    String username = scanner.nextLine();
    System.out.println("Enter password:");
    String password = scanner.nextLine();

    // Constructing the SQL query to delete user credentials with the provided username and password
    String deleteSql = "DELETE FROM user_credentials WHERE username = '" + username + "' AND password = '" + password + "'";
    // Executing the delete query and storing the number of rows affected
    int rowsDeleted = statement.executeUpdate(deleteSql);
    // Checking if any rows were deleted
    if (rowsDeleted > 0) {
        // If rows were deleted, indicating successful deletion
        System.out.println("User credentials deleted successfully.");
    } else {
        // If no matching user credentials were found to delete
        System.out.println("No matching user credentials found to delete.");
    }
}


    // This method allows the user to choose the output format for displaying a report
private static void displayReport(Connection connection, Statement statement, String tableName, Scanner scanner) throws SQLException {
    // Prompting the user to choose the output format
    System.out.println("Choose output format:");
    System.out.println("A. Console");
    System.out.println("B. CSV File");
    System.out.println("C. TXT File");
    System.out.print("Enter your choice (A, B, or C): ");
    // Reading the user's choice and converting it to uppercase
    String choice = scanner.nextLine().toUpperCase();

    // Switching based on the user's choice
    switch (choice) {
        case "A":
            // If the user chooses console output, display the report in the console
            displayTable(connection, statement, tableName);
            break;
        case "B":
            // If the user chooses CSV output, export the report to a CSV file
            exportToCSV(connection, statement, tableName);
            break;
        case "C":
            // If the user chooses TXT output, export the report to a TXT file
            exportToTXT(connection, statement, tableName);
            break;
        default:
            // If the user enters an invalid choice
            System.out.println("Invalid choice!");
    }
}


    @SuppressWarnings("CallToPrintStackTrace")
private static void exportToCSV(Connection connection, Statement statement, String tableName) throws SQLException {
    try (FileWriter writer = new FileWriter(tableName + ".csv")) {
        // Executing the SQL query to fetch data from the specified table
        ResultSet resultSet = statement.executeQuery("SELECT * FROM " + tableName);
        // Getting metadata about the ResultSet (e.g., column names)
        ResultSetMetaData metaData = resultSet.getMetaData();
        int columnCount = metaData.getColumnCount();

        // Writing column names to the CSV file
        for (int i = 1; i <= columnCount; i++) {
            writer.append(metaData.getColumnName(i));
            if (i < columnCount) {
                writer.append(",");
            }
        }
        writer.append("\n");

        // Writing data rows to the CSV file
        while (resultSet.next()) {
            for (int i = 1; i <= columnCount; i++) {
                writer.append(resultSet.getString(i));
                if (i < columnCount) {
                    writer.append(",");
                }
            }
            writer.append("\n");
        }

        // Printing a success message after exporting data
        System.out.println("Data exported to " + tableName + ".csv successfully");

        // Opening the CSV file with the default application, if supported
        if (Desktop.isDesktopSupported()) {
            try {
                File file = new File(tableName + ".csv");
                Desktop.getDesktop().open(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            // If the desktop is not supported, printing a message indicating that
            System.out.println("Opening files in NetBeans is not supported on this platform.");
        }
    } catch (IOException e) {
        // Handling IOExceptions by printing the stack trace
        e.printStackTrace();
    }
}

@SuppressWarnings("CallToPrintStackTrace")
private static void exportToTXT(Connection connection, Statement statement, String tableName) throws SQLException {
    try (FileWriter writer = new FileWriter(tableName + ".txt")) {
        // Executing the SQL query to fetch data from the specified table
        ResultSet resultSet = statement.executeQuery("SELECT * FROM " + tableName);
        // Getting metadata about the ResultSet (e.g., column names)
        ResultSetMetaData metaData = resultSet.getMetaData();
        int columnCount = metaData.getColumnCount();

        // Writing column names to the TXT file
        for (int i = 1; i <= columnCount; i++) {
            writer.append(metaData.getColumnName(i));
            if (i < columnCount) {
                writer.append("\t");
            }
        }
        writer.append("\n");

        // Writing data rows to the TXT file
        while (resultSet.next()) {
            for (int i = 1; i <= columnCount; i++) {
                writer.append(resultSet.getString(i));
                if (i < columnCount) {
                    writer.append("\t");
                }
            }
            writer.append("\n");
        }

        // Printing a success message after exporting data
        System.out.println("Data exported to " + tableName + ".txt successfully");

        // Opening the TXT file with the default application, if supported
        if (Desktop.isDesktopSupported()) {
            try {
                File file = new File(tableName + ".txt");
                Desktop.getDesktop().open(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            // If the desktop is not supported, printing a message indicating that
            System.out.println("Opening files in NetBeans is not supported on this platform.");
        }
    } catch (IOException e) {
        // Handling IOExceptions by printing the stack trace
        e.printStackTrace();
    }
}


}
