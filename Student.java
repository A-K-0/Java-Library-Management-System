import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;


public class Student {

    private static String[] MEMBERS = {"ADITYA", "MOHNIT","BHARGAVI","PAYAL"};
    private static String[] PASS = {"HELLO", "NAMASTE","HOLA","NAMASKAR"};
    private static int[] ENROLL_NO = {220568, 220670,220671,220672};

    private static final String DB_URL = "jdbc:mysql://localhost:3306/library";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Aditya1218";

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in);
             Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement()) {
    
            createTables(stmt);
    
            int choice;
            do {
                    displayMenu();
                    System.out.print("Enter your choice: ");
                    choice = scanner.nextInt();
    
                    switch (choice) {
                        case 1:
                             Borrowbook(conn, scanner);
                            break;
                        case 2:
                            returnBook(conn, scanner);
                            break;
                        case 3:
                            listBooks(conn,scanner);
                            break;
                        case 0:
                            System.out.println("Goodbye!");
                            break;
                        default:
                            System.out.println("Invalid choice. Please try again.");
                    }
                
            }while (choice != 0);
    
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
private static void displayMenu() 
{
        System.out.println("Library Management System");
        System.out.println("1. Borrow Book");
        System.out.println("2. Return Book");
        System.out.println("3. List Books");
        System.out.println("0. Exit");
}

private static void createTables(Statement stmt) throws SQLException {
    stmt.execute("USE library");
    stmt.execute("CREATE TABLE IF NOT EXISTS members (id INT AUTO_INCREMENT PRIMARY KEY, StudentName VARCHAR(50), EnNo INT, Password VARCHAR(10))");
    stmt.execute("CREATE TABLE IF NOT EXISTS transactions (id INT AUTO_INCREMENT PRIMARY KEY, book_id INT,bookname varchar(50), ENROLL INT ,SCHOOL VARCHAR (4), borrowed_date DATE, returned_date DATE)");
}


private static void returnBook(Connection conn, Scanner scanner) throws SQLException {
    System.out.print("Enter your username: ");
    String username = scanner.next();
    System.out.print("Enter your Enrollment No: ");
    int enroll = scanner.nextInt();
    System.out.print("Enter your password: ");
    String password = scanner.next();

    
    for (int i = 0; i < MEMBERS.length; i++) {
        if (MEMBERS[i].equalsIgnoreCase(username) && PASS[i].equals(password) && ENROLL_NO[i] == enroll) {
           
            PreparedStatement retrievePs = conn.prepareStatement("SELECT * FROM transactions WHERE ENROLL = ? AND returned_date IS NULL");
            retrievePs.setInt(1, enroll);
            ResultSet resultSet = retrievePs.executeQuery();

            
            if (!resultSet.next()) {
                System.out.println("You have no borrowed books.");
                return;
            }

           
            System.out.println("Borrowed Books:");
            do {
                System.out.println("ID: " + resultSet.getInt("book_id") + ", Name: " + resultSet.getString("bookname") +
                        ", School: " + resultSet.getString("SCHOOL") + ", Borrowed Date: " + resultSet.getDate("borrowed_date"));
            } while (resultSet.next());

            System.out.print("Enter the ID of the book you want to return: ");
            int bookId = scanner.nextInt();

            
            scanner.nextLine();

            System.out.print("Enter the name of the book you want to return: ");
            String bname = scanner.nextLine();

            System.out.print("Enter the name of the School of the book: ");
            String SCL = scanner.nextLine().toLowerCase();

            PreparedStatement deletePs = conn.prepareStatement("UPDATE transactions SET returned_date = CURDATE() WHERE book_id = ? AND ENROLL = ? AND bookname = ?");
            deletePs.setInt(1, bookId);
            deletePs.setInt(2, enroll);
            deletePs.setString(3, bname);
            int updatedRows = deletePs.executeUpdate();

      if (updatedRows > 0) {
    
    
     PreparedStatement updatePs = conn.prepareStatement("UPDATE " + SCL + " SET copies = copies + 1 WHERE id = ?");
     updatePs.setInt(1, bookId);
     int updatedCopies = updatePs.executeUpdate();

     if (updatedCopies > 0) {
        
        System.out.println("Book returned successfully.");
        writeReturnedBookInfo(bookId, bname, enroll, SCL);
    } else {
        System.out.println("Failed to update copies in the SCHOOL table.");
     }
     } else {
     System.out.println("Book not found in your borrowed list.");
     }

    return;} 
    

     System.out.println("Invalid Credentials");
        
    
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



    private static void Borrowbook(Connection conn, Scanner scanner) throws SQLException {
        System.out.print("Enter your username: ");
        String username = scanner.next();
        System.out.print("Enter your Enrollment No: ");
        int enroll = scanner.nextInt();
        System.out.print("Enter your password: ");
        String password = scanner.next();
    
        
        for (int i = 0; i < MEMBERS.length; i++) {
            if (MEMBERS[i].equalsIgnoreCase(username) && PASS[i].equals(password) && ENROLL_NO[i]==enroll) {
                
                listBooks(conn, scanner);
    
                System.out.print("Enter the ID of the book you want to borrow: ");
                int bookId = scanner.nextInt();
                System.out.print("Enter the Name of the book you want to borrow: ");
                String bookname = scanner.nextLine().toLowerCase(); 
                bookname = scanner.nextLine().toLowerCase();
                System.out.print("Enter the SCHOOL of the book: ");
                String SCHOOLNAME = scanner.next().toLowerCase();

                if (hasBorrowedBook(conn, enroll, bookId)) {
                    System.out.println("You have already borrowed this book. Cannot borrow the same book again.");
                    return; 
                }
    
              PreparedStatement ps = conn.prepareStatement("INSERT INTO transactions (book_id, bookname, ENROLL, SCHOOL , borrowed_date) VALUES (?, ?, ?, ?, CURDATE())");
                ps.setInt(1, bookId);
                ps.setString(2, bookname);
                ps.setInt(3, enroll);
                ps.setString(4, SCHOOLNAME);
                ps.executeUpdate();
    
                
                PreparedStatement updatePs = conn.prepareStatement("UPDATE " + SCHOOLNAME + " SET copies = copies - 1 WHERE id = ?");
                updatePs.setInt(1, bookId);
                updatePs.executeUpdate();
    
               
                writeBorrowedBookInfo(bookId, bookname, enroll, SCHOOLNAME);
    
                System.out.println("Book borrowed successfully.");
                return; 
            }
        }
    
       
        System.out.println("Invalid Credentials");
    }



    private static void writeBorrowedBookInfo(int bookId, String bookname, int enroll, String SCHOOL) {

            String fileName = "borrowed_books.txt";
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true))) {
                
                String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                String info = "Book ID: " + bookId + " | Book Name: " + bookname + " | EnNo: " + enroll +
                " | SCHOOL: " + SCHOOL + " | Borrowed Date: " + timeStamp;
                writer.write(info);
                writer.newLine();
            }
         catch (IOException e) {
            System.out.println("There are no such books");
        }
    }


    private static void writeReturnedBookInfo(int bookId, String bookname, int enroll, String SCHOOL) {

            String fileName = "returned_books.txt";
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true))) {
                
                String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                String info = "Book ID: " + bookId + " | Book Name: " + bookname + " | EnNo: " + enroll +
                " | SCHOOL: " + SCHOOL + " | Borrowed Date: " + timeStamp;
                writer.write(info);
                writer.newLine();
            }
         catch (IOException e) {

            System.out.println("There are no books like this to be returned");
        }
    }



    private static boolean hasBorrowedBook(Connection conn, int enroll, int bookId) throws SQLException {
       
        PreparedStatement checkPs = conn.prepareStatement("SELECT * FROM transactions WHERE ENROLL = ? AND book_id = ? AND returned_date IS NULL");
        checkPs.setInt(1, enroll);
        checkPs.setInt(2, bookId);
        ResultSet resultSet = checkPs.executeQuery();
        return resultSet.next();
    }


    




 {
    
}}

