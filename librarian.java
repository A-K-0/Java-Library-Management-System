import java.sql.*;
import java.util.Scanner;


public class librarian {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/library";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Aditya1218";

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in);
             Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement()) {
    
            createTables(stmt);
    
            int choice;
            
                boolean result = check(conn, scanner);
            do {

                if (result) {
                    displayMenu();
                    System.out.print("Enter your choice: ");
                    choice = scanner.nextInt();
    
                    switch (choice) {
                        case 1:
                            addBook(conn, scanner);
                            break;
                        case 2:
                            removeBook(conn,scanner);
                            break;
                        case 3:
                            listMembers(conn);
                            break;
                        case 4:
                            listBooks(conn,scanner);
                            break;
                        case 0:
                            System.out.println("Goodbye!");
                            break;
                        default:
                            System.out.println("Invalid choice. Please try again.");
                    }
                } else {
                    System.out.println("Incorrect Username or Password");
                    choice = -1; // Set choice to -1 to exit the loop if authentication fails
                }
                
            }while (choice > 0);
    
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private static void displayMenu() {
        System.out.println("Library Management System");
        System.out.println("1. Add Book");
        System.out.println("2. Remove Book");
        System.out.println("3. List Members");
        System.out.println("4. List Books");
        System.out.println("0. Exit");
    }
    
    
    private static void createTables(Statement stmt) throws SQLException {
        // Create tables if they don't exist
        stmt.execute("CREATE DATABASE IF NOT EXISTS library");
        stmt.execute("USE library");
        stmt.execute("CREATE TABLE IF NOT EXISTS ADMIN (id INT AUTO_INCREMENT PRIMARY KEY, NAME VARCHAR(255),PASSWORD VARCHAR(225), PHNO VARCHAR(10))");
        stmt.execute("CREATE TABLE IF NOT EXISTS books (id INT AUTO_INCREMENT PRIMARY KEY, title VARCHAR(255) primary key, author VARCHAR(255),copies int)");
        stmt.execute("CREATE TABLE IF NOT EXISTS members (id INT AUTO_INCREMENT PRIMARY KEY, StudentName VARCHAR(50), EnNo INT UNIQUE, Password VARCHAR(10) UNIQUE)");
        stmt.execute("CREATE TABLE IF NOT EXISTS transactions (id INT AUTO_INCREMENT PRIMARY KEY, book_id INT, member_id INT, borrowed_date DATE, returned_date DATE)");
    }
    private static boolean check(Connection conn, Scanner scanner) throws SQLException {
        System.out.println("Enter your username = ");
        String username = scanner.next();
        System.out.println("Enter your password =");
        String password = scanner.next();
    
        PreparedStatement ps = conn.prepareStatement("SELECT * FROM ADMIN WHERE NAME = ? AND PASSWORD = ?");
        ps.setString(1, username);
        ps.setString(2, password);
    
        ResultSet rs = ps.executeQuery();
        return rs.next();  // Return true if the result set has at least one row, indicating a match.
    }
    

    private static void addBook(Connection conn, Scanner scanner) throws SQLException {
        System.out.print("Enter book title: ");
        String title = scanner.next();
        System.out.print("Enter book author: ");
        String author = scanner.next();
        System.out.print("Enter book copies: ");
        int copies = scanner.nextInt();
        System.out.print("Enter SCHOOL: ");
        String SCHOOL = scanner.next();

        String tableName;
    if ("SOLS".equals(SCHOOL)) {
        tableName = "SOLS";
    } else if ("SOET".equals(SCHOOL)) {
        tableName = "SOET";
    } else if ("SOL".equals(SCHOOL)) {
        tableName = "SOL";
    } else if ("SOM".equals(SCHOOL)) {
        tableName = "SOM";
    }
    else if ("soet".equals(SCHOOL)) {
        tableName = "SOET";
    }else if ("sol".equals(SCHOOL)) {
        tableName = "SOL";
    }else if ("som".equals(SCHOOL)) {
        tableName = "SOM";
    }
    else if ("sols".equals(SCHOOL)) {
        tableName = "SOM";
    } 
    else {
        System.out.print("Invalid category");
        return; // Exit the method if the category is invalid
    }

    PreparedStatement ps = conn.prepareStatement("INSERT INTO " + tableName + " (title, author, COPIES) VALUES (?, ?, ? ) " +
                        "ON DUPLICATE KEY UPDATE COPIES = COPIES + VALUES(COPIES)");
        ps.setString(1, title);
        ps.setString(2, author);
        ps.setInt(3, copies);
        ps.executeUpdate();
        System.out.println("Book added successfully.");
    }

    private static void removeBook(Connection conn, Scanner scanner) {
        try {
            System.out.print("Enter book title to remove: ");
            String titleToRemove = scanner.next();
            System.out.print("Enter the author of the book: ");
            String author = scanner.next();
            System.out.print("Enter SCHOOL: ");
            String school = scanner.next();
    
            String tableName;
            if ("SOLS".equals(school)) {
                tableName = "SOLS";
            } else if ("SOET".equals(school)) {
                tableName = "SOET";
            } else if ("SOL".equals(school)) {
                tableName = "SOL";
            } else if ("SOM".equals(school)) {
                tableName = "SOM";
            } else {
                System.out.print("Invalid category");
                return; // Exit the method if the category is invalid
            }
    
            // Use a prepared statement to remove the book by title
            String deleteQuery = "DELETE FROM " + tableName + " WHERE title = ? AND author = ?";
            try (PreparedStatement ps = conn.prepareStatement(deleteQuery)) {
                ps.setString(1, titleToRemove);
                ps.setString(2, author);
                int rowsAffected = ps.executeUpdate();
    
                if (rowsAffected > 0) {
                    System.out.println("Book removed successfully.");
                } else {
                    System.out.println("Book with title '" + titleToRemove + "' not found in " + tableName);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Handle the exception according to your application's needs
        }
    }
    
    private static void listBooks(Connection conn , Scanner scanner) throws SQLException {
        System.out.println("List of Books:");
        System.out.println("Enter the SCHOOL");
        String SCHOOL = scanner.next();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM "+ SCHOOL);
        while (rs.next()) {
            int id = rs.getInt("id");
            String title = rs.getString("title");
            String author = rs.getString("author");
            int copies = rs.getInt("copies");
            System.out.println(id + ". "+ title+"  ||  "+ author +"  ||  "+ copies +"  ||  ");
        }
    }

    private static void listMembers(Connection conn) throws SQLException {
        System.out.println("List of Members:");
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM members");
        while (rs.next()) {
            int id = rs.getInt("EnNo");
            String Studentname = rs.getString("Studentname");
            System.out.println(id + " || " + Studentname);
        }
    }
}