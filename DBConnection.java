package scholar;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DBConnection {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/scholarsyncdb";
    private static final String USER = "root";
    private static final String PASS = "";
    
    private static Connection connection = null;

    public static Connection getConnection() {
        if (connection == null) {
            try {
                // Register JDBC driver
                Class.forName("com.mysql.cj.jdbc.Driver");
                
                // Open a connection
                System.out.println("Connecting to database...");
                connection = DriverManager.getConnection(DB_URL, USER, PASS);
                
                if (connection != null) {
                    System.out.println("Successfully connected to the database!");
                }
                
            } catch (SQLException e) {
                System.out.println("Database Connection Failed!");
                System.out.println("Error: " + e.getMessage());
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                System.out.println("MySQL JDBC Driver not found.");
                System.out.println("Error: " + e.getMessage());
                e.printStackTrace();
            }
        }
        return connection;
    }

    private static void createTables() {
        try {
            Statement stmt = connection.createStatement();
            
            // Create users table
            stmt.execute(
                "CREATE TABLE IF NOT EXISTS users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "first_name TEXT NOT NULL," +
                "middle_name TEXT," +
                "last_name TEXT NOT NULL," +
                "email TEXT UNIQUE NOT NULL," +
                "password TEXT NOT NULL," +
                "school TEXT NOT NULL," +
                "track_strand TEXT NOT NULL," +
                "gpa REAL NOT NULL," +
                "monthly_income TEXT NOT NULL," +
                "college_course TEXT NOT NULL," +
                "student_id TEXT NOT NULL" +
                ")"
            );

            // Create scholarships table
            stmt.execute(
                "CREATE TABLE IF NOT EXISTS scholarships (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "title VARCHAR(255) UNIQUE NOT NULL," +
                "start_date DATE NOT NULL," +
                "end_date DATE NOT NULL," +
                "applicant_count INT DEFAULT 0," +
                "max_slots INT NOT NULL" +
                ") ENGINE=InnoDB;"
            );

            // Create applications table
            stmt.execute(
                "CREATE TABLE IF NOT EXISTS applications (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_email TEXT, " +
                "scholarship_title TEXT, " +
                "status TEXT DEFAULT 'PENDING', " +
                "notification_shown BOOLEAN DEFAULT FALSE, " +
                "application_date DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY (user_email) REFERENCES users(email), " +
                "FOREIGN KEY (scholarship_title) REFERENCES scholarships(title)" +
                ")"
            );

            // Insert default scholarships if none exist
            stmt.execute(
                "INSERT INTO scholarships (title, start_date, end_date, max_slots) VALUES " +
                "('GSIS Scholarship Program 2025', '2025-04-13', '2025-08-30', 5)," +
                "('DOST Scholarship 2025', '2025-05-01', '2025-09-15', 10)," +
                "('University Merit Scholarship', '2025-06-01', '2025-10-01', 8) " +
                "ON DUPLICATE KEY UPDATE title=title"
            );

        } catch (SQLException e) {
            System.err.println("Error creating tables");
            e.printStackTrace();
        }
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                connection = null;
                System.out.println("Database connection closed");
            }
        } catch (SQLException e) {
            System.out.println("Error closing connection");
            e.printStackTrace();
        }
    }

    public static Connection getCYonnection() {
        // TODO Auto-generated method stub
        return null;
    }
}
