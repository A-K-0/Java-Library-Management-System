import java.sql.*;
import java.util.Scanner;


public class lib {
    private static String[] usernames = {"BMLadmin1", "BMLadmin2"};
    private static String[] passwords = {"password1", "password2"};
    private static String[] MEMBERS = {"ADITYA", "MOHNIT","BHARGAVI","PAYAL"};
    private static String[] ENROLL_NO = {"220568", "220670","220671","220672"};
    
    private static final String DB_URL = "jdbc:mysql://localhost:3306/library";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Aditya1218";

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in);
             Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement()) {
    
            createTables(stmt);
    
            int choice;
            
                boolean result = check(scanner);
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
                            listMembers();
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
                    choice = -1;
                }
                
            }while (choice > 0);
    
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private static boolean check(Scanner scanner) {
        System.out.println("Enter your username = ");
        String username = scanner.next();
        System.out.println("Enter your password =");
        String password = scanner.next();

       
        for (int i = 0; i < usernames.length; i++) {
            if (usernames[i].equals(username) && passwords[i].equals(password)) {
                return true; 
            }
        }

        return false;
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
        stmt.execute("USE library");
        stmt.execute("CREATE TABLE IF NOT EXISTS members (id INT AUTO_INCREMENT PRIMARY KEY, StudentName VARCHAR(50), EnNo INT UNIQUE, Password VARCHAR(10) UNIQUE)");
        stmt.execute("CREATE TABLE IF NOT EXISTS transactions (id INT AUTO_INCREMENT PRIMARY KEY, book_id INT,bookname VARCHAR(255), ENROLL INT, borrowed_date DATE, returned_date DATE)");
    }
    

    private static void addBook(Connection conn, Scanner scanner) throws SQLException {
        System.out.print("Enter book title: ");
        String bookTitle = scanner.nextLine(); 
        bookTitle = scanner.nextLine(); 
        System.out.print("Enter book author: ");
        String author = scanner.nextLine();
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
        }else if ("soet".equals(SCHOOL)) {
            tableName = "SOET";
        }else if ("sol".equals(SCHOOL)) {
            tableName = "SOL";
        }else if ("som".equals(SCHOOL)) {
            tableName = "SOM";
        }else if ("sols".equals(SCHOOL)) {
            tableName = "SOM";
        } 
        else {
            System.out.print("Invalid category");
            return; 
        }

    PreparedStatement ps = conn.prepareStatement("INSERT INTO " + tableName + " (title, author, COPIES) VALUES (?, ?, ? ) " +
                        "ON DUPLICATE KEY UPDATE COPIES = COPIES + VALUES(COPIES)");
        ps.setString(1, bookTitle);
        ps.setString(2, author);
        ps.setInt(3, copies);
        ps.executeUpdate();
        System.out.println("Book added successfully.");
    }

    private static void removeBook(Connection conn, Scanner scanner) {
        try {
            System.out.print("Enter book title to remove: ");
            String titleToRemove = scanner.nextLine(); 
            titleToRemove = scanner.nextLine();
            System.out.print("Enter the author of the book: ");
            String author = scanner.nextLine();
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
            } else if ("som".equals(school)) {
                tableName = "SOM";
                } else if ("soet".equals(school)) {
                tableName = "SOET";
                } else if ("sol".equals(school)) {
                tableName = "SOL";
                } else if ("sols".equals(school)) {
                tableName = "SOLS";
            } else {
                System.out.print("Invalid category");
                return; 
            }
    
        
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
            e.printStackTrace(); 
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

    private static void listMembers() {
        System.out.println("List of Members:");
        for (int i = 0; i < MEMBERS.length; i++)
        {
            System.out.println("name: " + MEMBERS[i] + " || enrollment no:" + ENROLL_NO[i]);
        }
    }
    
}