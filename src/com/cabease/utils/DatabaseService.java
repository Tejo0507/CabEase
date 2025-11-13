package com.cabease.utils;
import com.cabease.models.User;
import com.cabease.models.Cab;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
public class DatabaseService {
    private static final String DB_URL = "jdbc:h2:./cabease_db";
    private static final String DB_USER = "sa";
    private static final String DB_PASSWORD = "";
    public static Connection getConnection() throws SQLException {
        String url = System.getenv("SPRING_DATASOURCE_URL");
        if (stringIsNullOrEmpty(url)) {
            url = System.getProperty("spring.datasource.url");
        }
        if (stringIsNullOrEmpty(url)) {
            url = DB_URL;
        }
        String user = System.getenv("SPRING_DATASOURCE_USERNAME");
        if (stringIsNullOrEmpty(user)) {
            user = System.getProperty("spring.datasource.username");
        }
        if (stringIsNullOrEmpty(user)) {
            user = DB_USER;
        }
        String password = System.getenv("SPRING_DATASOURCE_PASSWORD");
        if (stringIsNullOrEmpty(password)) {
            password = System.getProperty("spring.datasource.password");
        }
        if (password == null) {
            password = DB_PASSWORD;
        }
        return DriverManager.getConnection(url, user, password);
    }
    private static boolean stringIsNullOrEmpty(String s) {
        return s == null || s.trim().isEmpty();
    }
    public static void initializeDatabase() {
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            String usersSql = "CREATE TABLE IF NOT EXISTS users (" +
                         "id INT AUTO_INCREMENT PRIMARY KEY," +
                         "username VARCHAR(255) NOT NULL UNIQUE," +
                         "password VARCHAR(255) NOT NULL," +
                         "email VARCHAR(255) NOT NULL UNIQUE)";
            stmt.execute(usersSql);
            String cabsSql = "CREATE TABLE IF NOT EXISTS cabs (" +
                         "id INT AUTO_INCREMENT PRIMARY KEY," +
                         "cabNumber VARCHAR(255) NOT NULL UNIQUE," +
                         "model VARCHAR(255) NOT NULL," +
                         "driverName VARCHAR(255) NOT NULL," +
                         "driverContact VARCHAR(255) NOT NULL," +
                         "isAvailable BOOLEAN NOT NULL)";
            stmt.execute(cabsSql);
            String bookingsSql = "CREATE TABLE IF NOT EXISTS bookings (" +
                         "id INT AUTO_INCREMENT PRIMARY KEY," +
                         "userId INT NOT NULL," +
                         "cabId INT NOT NULL," +
                         "source VARCHAR(255) NOT NULL," +
                         "destination VARCHAR(255) NOT NULL," +
                         "bookingTime TIMESTAMP NOT NULL," +
                         "status VARCHAR(255) NOT NULL," +
                         "FOREIGN KEY (userId) REFERENCES users(id)," +
                         "FOREIGN KEY (cabId) REFERENCES cabs(id))";
            stmt.execute(bookingsSql);
            String clearBookingsSql = "DELETE FROM bookings";
            stmt.execute(clearBookingsSql);
            String clearCabsSql = "DELETE FROM cabs";
            stmt.execute(clearCabsSql);
            insertSampleCabs();
        } catch (SQLException e) {
            System.out.println("Database initialization failed. Error: " + e.getMessage());
        }
    }
    public void saveUser(User user) {
        String sql = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getEmail());
            pstmt.executeUpdate();
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                user.setId(rs.getInt(1));
            }
            System.out.println("User '" + user.getUsername() + "' saved to the database.");
        } catch (SQLException e) {
            System.out.println("Error saving user. Error: " + e.getMessage());
        }
    }
    public java.util.List<User> getAllUsers() {
        String sql = "SELECT * FROM users";
        java.util.List<User> users = new java.util.ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                User user = new User(rs.getString("username"), rs.getString("password"), rs.getString("email"));
                user.setId(rs.getInt("id"));
                users.add(user);
            }
        } catch (SQLException e) {
            System.out.println("Error getting users. Error: " + e.getMessage());
        }
        return users;
    }
    public User getUserByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        User user = null;
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                user = new User(rs.getString("username"), rs.getString("password"), rs.getString("email"));
                user.setId(rs.getInt("id"));
            }
        } catch (SQLException e) {
            System.out.println("Error getting user. Error: " + e.getMessage());
        }
        return user;
    }
    public Cab getCabByCabNumber(String cabNumber) {
        String sql = "SELECT * FROM cabs WHERE cabNumber = ?";
        Cab cab = null;
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, cabNumber);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                cab = new Cab(rs.getString("cabNumber"), rs.getString("model"), rs.getBoolean("isAvailable"), rs.getString("driverName"), rs.getString("driverContact"));
                cab.setId(rs.getInt("id"));
            }
        } catch (SQLException e) {
            System.out.println("Error getting cab. Error: " + e.getMessage());
        }
        return cab;
    }
    public java.util.List<Cab> getAvailableCabs(String type) {
        String sql = "SELECT * FROM cabs WHERE isAvailable = true";
        if (type != null && !type.isEmpty()) {
            sql += " AND model = ?";
        }
        java.util.List<Cab> cabs = new java.util.ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            if (type != null && !type.isEmpty()) {
                pstmt.setString(1, type);
            }
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Cab cab = new Cab(rs.getString("cabNumber"), rs.getString("model"), rs.getBoolean("isAvailable"), rs.getString("driverName"), rs.getString("driverContact"));
                cab.setId(rs.getInt("id"));
                cabs.add(cab);
            }
            System.out.println("Retrieved " + cabs.size() + " available cabs.");
        } catch (SQLException e) {
            System.out.println("Error getting available cabs. Error: " + e.getMessage());
        }
        return cabs;
    }
    public Cab getCabById(int id) {
        String sql = "SELECT * FROM cabs WHERE id = ?";
        Cab cab = null;
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                cab = new Cab(rs.getString("cabNumber"), rs.getString("model"), rs.getBoolean("isAvailable"), rs.getString("driverName"), rs.getString("driverContact"));
                cab.setId(rs.getInt("id"));
            }
        } catch (SQLException e) {
            System.out.println("Error getting cab. Error: " + e.getMessage());
        }
        return cab;
    }
    public static void insertSampleCabs() {
        String sql = "INSERT INTO cabs (id, cabNumber, model, driverName, driverContact, isAvailable) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, 1);
            pstmt.setString(2, "TN01AB1234");
            pstmt.setString(3, "Sedan");
            pstmt.setString(4, "Ramesh Kumar");
            pstmt.setString(5, "9876543210");
            pstmt.setBoolean(6, true);
            pstmt.addBatch();
            pstmt.setInt(1, 2);
            pstmt.setString(2, "TN02CD5678");
            pstmt.setString(3, "SUV");
            pstmt.setString(4, "Sita Sharma");
            pstmt.setString(5, "8765432109");
            pstmt.setBoolean(6, true);
            pstmt.addBatch();
            pstmt.setInt(1, 3);
            pstmt.setString(2, "TN03EF9012");
            pstmt.setString(3, "Hatchback");
            pstmt.setString(4, "Amit Patel");
            pstmt.setString(5, "7654321098");
            pstmt.setBoolean(6, false);
            pstmt.addBatch();
            pstmt.setInt(1, 4);
            pstmt.setString(2, "TN04GH3456");
            pstmt.setString(3, "Sedan");
            pstmt.setString(4, "Vijay Singh");
            pstmt.setString(5, "9123456780");
            pstmt.setBoolean(6, true);
            pstmt.addBatch();
            pstmt.setInt(1, 5);
            pstmt.setString(2, "TN05IJ7890");
            pstmt.setString(3, "SUV");
            pstmt.setString(4, "Anjali Mehta");
            pstmt.setString(5, "9988776655");
            pstmt.setBoolean(6, true);
            pstmt.addBatch();
            pstmt.setInt(1, 6);
            pstmt.setString(2, "TN06KL1234");
            pstmt.setString(3, "Hatchback");
            pstmt.setString(4, "Rajesh Khanna");
            pstmt.setString(5, "8877665544");
            pstmt.setBoolean(6, true);
            pstmt.addBatch();
            pstmt.setInt(1, 7);
            pstmt.setString(2, "TN07MN5678");
            pstmt.setString(3, "Sedan");
            pstmt.setString(4, "Pooja Sharma");
            pstmt.setString(5, "7766554433");
            pstmt.setBoolean(6, true);
            pstmt.addBatch();
            pstmt.setInt(1, 8);
            pstmt.setString(2, "TN08OP9012");
            pstmt.setString(3, "SUV");
            pstmt.setString(4, "Manoj Tiwari");
            pstmt.setString(5, "6655443322");
            pstmt.setBoolean(6, true);
            pstmt.addBatch();
            pstmt.setInt(1, 9);
            pstmt.setString(2, "TN09QR3456");
            pstmt.setString(3, "Hatchback");
            pstmt.setString(4, "Sunita Verma");
            pstmt.setString(5, "5544332211");
            pstmt.setBoolean(6, true);
            pstmt.addBatch();
            pstmt.setInt(1, 10);
            pstmt.setString(2, "TN10ST7890");
            pstmt.setString(3, "Sedan");
            pstmt.setString(4, "Ravi Shankar");
            pstmt.setString(5, "4433221100");
            pstmt.setBoolean(6, true);
            pstmt.addBatch();
            pstmt.setInt(1, 11);
            pstmt.setString(2, "TN11UV1234");
            pstmt.setString(3, "SUV");
            pstmt.setString(4, "Neha Gupta");
            pstmt.setString(5, "3322110099");
            pstmt.setBoolean(6, true);
            pstmt.addBatch();
            pstmt.setInt(1, 12);
            pstmt.setString(2, "TN12WX5678");
            pstmt.setString(3, "Hatchback");
            pstmt.setString(4, "Arjun Kapoor");
            pstmt.setString(5, "2211009988");
            pstmt.setBoolean(6, true);
            pstmt.addBatch();
            pstmt.setInt(1, 13);
            pstmt.setString(2, "TN13YZ9012");
            pstmt.setString(3, "Sedan");
            pstmt.setString(4, "Kiran Rao");
            pstmt.setString(5, "1100998877");
            pstmt.setBoolean(6, true);
            pstmt.addBatch();
            pstmt.setInt(1, 14);
            pstmt.setString(2, "TN14AB3456");
            pstmt.setString(3, "SUV");
            pstmt.setString(4, "Deepak Joshi");
            pstmt.setString(5, "0099887766");
            pstmt.setBoolean(6, true);
            pstmt.addBatch();
            pstmt.setInt(1, 15);
            pstmt.setString(2, "TN15CD7890");
            pstmt.setString(3, "Hatchback");
            pstmt.setString(4, "Meera Nair");
            pstmt.setString(5, "9988776655");
            pstmt.setBoolean(6, true);
            pstmt.addBatch();
            pstmt.setInt(1, 16);
            pstmt.setString(2, "TN16EF1234");
            pstmt.setString(3, "Sedan");
            pstmt.setString(4, "Vikas Malhotra");
            pstmt.setString(5, "8877665544");
            pstmt.setBoolean(6, true);
            pstmt.addBatch();
            pstmt.setInt(1, 17);
            pstmt.setString(2, "TN17GH5678");
            pstmt.setString(3, "SUV");
            pstmt.setString(4, "Anita Desai");
            pstmt.setString(5, "7766554433");
            pstmt.setBoolean(6, true);
            pstmt.addBatch();
            pstmt.setInt(1, 18);
            pstmt.setString(2, "TN18IJ9012");
            pstmt.setString(3, "Hatchback");
            pstmt.setString(4, "Rohit Shetty");
            pstmt.setString(5, "6655443322");
            pstmt.setBoolean(6, true);
            pstmt.addBatch();
            pstmt.setInt(1, 19);
            pstmt.setString(2, "TN19KL3456");
            pstmt.setString(3, "Sedan");
            pstmt.setString(4, "Priya Singh");
            pstmt.setString(5, "5544332211");
            pstmt.setBoolean(6, true);
            pstmt.addBatch();
            pstmt.setInt(1, 20);
            pstmt.setString(2, "TN20MN7890");
            pstmt.setString(3, "SUV");
            pstmt.setString(4, "Amitabh Bachchan");
            pstmt.setString(5, "4433221100");
            pstmt.setBoolean(6, true);
            pstmt.addBatch();
            pstmt.executeBatch();
            System.out.println("Sample cabs with IDs inserted successfully.");
        } catch (SQLException e) {
            System.out.println("Error inserting sample cabs with IDs. Error: " + e.getMessage());
        }
    }
    public static void insertMultipleCabs(List<Cab> cabs) {
        String sql = "INSERT INTO cabs (cabNumber, model, driverName, driverContact, isAvailable) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (Cab cab : cabs) {
                pstmt.setString(1, cab.getCabNumber());
                pstmt.setString(2, cab.getModel());
                pstmt.setString(3, cab.getDriverName());
                pstmt.setString(4, cab.getDriverContact());
                pstmt.setBoolean(5, cab.isAvailable());
                pstmt.addBatch();
            }
            pstmt.executeBatch();
            System.out.println("Cabs added successfully.");
        } catch (SQLException e) {
            System.out.println("Error adding cabs. Error: " + e.getMessage());
        }
    }
    public static void showAllCabs() {
        String sql = "SELECT * FROM cabs";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();
            System.out.println("\n--- All Cabs ---");
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("id") + ", Cab Number: " + rs.getString("cabNumber") + ", Model: " + rs.getString("model") + ", Driver: " + rs.getString("driverName") + ", Contact: " + rs.getString("driverContact") + ", Available: " + rs.getBoolean("isAvailable"));
            }
            System.out.println("----------------------");
        } catch (SQLException e) {
            System.out.println("Error retrieving all cabs. Error: " + e.getMessage());
        }
    }
}
