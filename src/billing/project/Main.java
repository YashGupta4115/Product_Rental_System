package billing.project;
import java.sql.*;

public class Main {
    public static String url, uname, pass;
    
    static {
        url = "jdbc:mysql://localhost:3306/";
        uname = "root";
        pass = "Yash@123";
    }

    public static void databaseInitializer() {
        try (Connection conn = DriverManager.getConnection(url, uname, pass);
             Statement stmt = conn.createStatement()) {

            // Check if the database exists; if not, create it
            String checkDatabase = "CREATE DATABASE IF NOT EXISTS productRentalSystem";
            stmt.executeUpdate(checkDatabase);
            System.out.println("Database created or already exists.");

            // Now connect to the created or existing productRentalSystem database
            url = "jdbc:mysql://localhost:3306/productRentalSystem";
            try (Connection dbConn = DriverManager.getConnection(url, uname, pass);
                 Statement dbStmt = dbConn.createStatement()) {

                String createItemsTable = "CREATE TABLE IF NOT EXISTS items (" +
                                          "item_id INT NOT NULL, " +
                                          "item_name VARCHAR(255), " +
                                          "amount DOUBLE, " +
                                          "total_available INT, " +
                                          "available INT, " +
                                          "PRIMARY KEY (item_id)" +
                                          ");";
                
                String createClientTable = "CREATE TABLE IF NOT EXISTS client (" +
                                           "client_id INT NOT NULL, " +
                                           "name VARCHAR(255), " +
                                           "PRIMARY KEY (client_id)" +
                                           ");";
                
                String createPhoneTable = "CREATE TABLE IF NOT EXISTS phone (" +
                                          "client_phone INT NOT NULL, " +
                                          "client_id INT, " +
                                          "PRIMARY KEY (client_phone), " +
                                          "FOREIGN KEY (client_id) REFERENCES client(client_id)" +
                                          ");";
                
                String createOrderTable = "CREATE TABLE IF NOT EXISTS orders (" +
                                          "order_id INT NOT NULL, " +
                                          "client_id INT, " +
                                          "order_date DATE, " +
                                          "event_date DATE, " +
                                          "items_amt DOUBLE, " +
                                          "transport_and_human_capital_cost DOUBLE, " +
                                          "amt_paid DOUBLE, " +
                                          "PRIMARY KEY (order_id), " +
                                          "FOREIGN KEY (client_id) REFERENCES client(client_id)" +
                                          ");";
                
                String createItemsBookedTable = "CREATE TABLE IF NOT EXISTS items_booked (" +
                                                "order_id INT, " +
                                                "client_id INT, " +
                                                "item_id INT, " +
                                                "item_qnt INT, " +
                                                "FOREIGN KEY (order_id) REFERENCES orders(order_id), " +
                                                "FOREIGN KEY (client_id) REFERENCES client(client_id), " +
                                                "FOREIGN KEY (item_id) REFERENCES items(item_id)" +
                                                ");";

                dbStmt.executeUpdate(createItemsTable);
                dbStmt.executeUpdate(createClientTable);
                dbStmt.executeUpdate(createPhoneTable);
                dbStmt.executeUpdate(createOrderTable);
                dbStmt.executeUpdate(createItemsBookedTable);

                System.out.println("Tables created or already exist.");
            }

        } catch (SQLException e) {
            System.out.println("Error creating database or tables:");
            e.printStackTrace();
        }
    }

    Main() {
        databaseInitializer();
        new OptionMain();
        new updateItems();
    }
}
