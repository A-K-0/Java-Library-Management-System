import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.Scanner;

public class OfflineLibrary {


    private static final String DB_URL = "jdbc:mysql://localhost:3306/library";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Aditya1218";

    public static void main(String[] args) throws SQLException{
        try (Scanner scanner = new Scanner(System.in);
             Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement()) {

            int choice;
            do {
                displayMenu();
                System.out.print("Enter your choice: ");
                choice = scanner.nextInt();

                switch (choice) {
                    case 1:
                        readBooks(conn ,scanner);
                        break;
                    case 2:
                        listBooks(conn, scanner);
                        break;
                    case 0:
                        System.out.println("Goodbye!");
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }

            } while (choice != 0);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private static void displayMenu() 

{
        System.out.println("     ");
        System.out.println("      ");
        System.out.println("Offline Free Library");
        System.out.println("1. Read a Book");
        System.out.println("2. List Books");
        System.out.println("0. Exit");
}

private static void listBooks(Connection conn, Scanner scanner) throws SQLException {
     System.out.println("               ");
    System.out.println("List of Books:");
    System.out.println("Enter the SCHOOL");
    String SCHOOL = scanner.next();

    scanner.nextLine();

    Statement stmt = conn.createStatement();
    ResultSet rs = stmt.executeQuery("SELECT * FROM " + SCHOOL);
    while (rs.next()) {
        int id = rs.getInt("id");
        String title = rs.getString("title");
        String author = rs.getString("author");
        System.out.println(id + ". " + title + "  ||  " + author + "  ||  ");
    }
}

private static void readBooks(Connection conn, Scanner scanner) throws SQLException {

    listBooks(conn, scanner);
     System.out.println("                         ");
    System.out.println("Enter the BOOK");
    String filename = scanner.nextLine().toLowerCase() + ".txt";

    try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
        System.out.println("Content of " + filename + ":");
        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }
    } catch (IOException e) {
        e.printStackTrace();
    }
}

    


}




