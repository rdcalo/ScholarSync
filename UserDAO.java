	package scholar;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserDAO {
    
    private Connection conn;

    public UserDAO() {
        conn = DBConnection.getConnection();
    }

    // Register a new user
    public boolean registerUser(User user) {
        String sql = "INSERT INTO users (first_name, middle_name, last_name, email, password, " +
                     "school, track_strand, gpa, monthly_income, college_course, student_id) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            
            // Set values for prepared statement
            pstmt.setString(1, user.getFirstName());
            pstmt.setString(2, user.getMiddleName());
            pstmt.setString(3, user.getLastName());
            pstmt.setString(4, user.getEmail());
            pstmt.setString(5, user.getPassword()); // In a real app, use password hashing
            pstmt.setString(6, user.getSchool());
            pstmt.setString(7, user.getTrackStrand());
            pstmt.setDouble(8, user.getGpa());
            pstmt.setString(9, user.getMonthlyIncome());
            pstmt.setString(10, user.getCollegeCourse());
            pstmt.setString(11, user.getStudentId());
            
            // Execute the query
            int rowsAffected = pstmt.executeUpdate();
            return (rowsAffected > 0);
            
        } catch (SQLException e) {
            System.err.println("Error registering user: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    // Check if email already exists
    public boolean emailExists(String email) {
        String sql = "SELECT COUNT(*) FROM users WHERE email = ?";
        
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            
        } catch (SQLException e) {
            System.err.println("Error checking email existence: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    // Verify login credentials
    public User login(String email, String password) {
        String sql = "SELECT * FROM users WHERE email = ? AND password = ?";
        
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            
            pstmt.setString(1, email);
            pstmt.setString(2, password); // In a real app, use password hashing
            
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                User user = new User();
                user.setFirstName(rs.getString("first_name"));
                user.setMiddleName(rs.getString("middle_name"));
                user.setLastName(rs.getString("last_name"));
                user.setEmail(rs.getString("email"));
                user.setPassword(rs.getString("password"));
                user.setSchool(rs.getString("school"));
                user.setTrackStrand(rs.getString("track_strand"));
                user.setGpa(rs.getDouble("gpa"));
                user.setMonthlyIncome(rs.getString("monthly_income"));
                user.setCollegeCourse(rs.getString("college_course"));
                user.setStudentId(rs.getString("student_id"));
                return user;
            }
            
        } catch (SQLException e) {
            System.err.println("Error during login: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null; // Return null if login fails
    }
    
    // Get user by email
    public User getUserByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";
        
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setFirstName(rs.getString("first_name"));
                user.setMiddleName(rs.getString("middle_name"));
                user.setLastName(rs.getString("last_name"));
                user.setEmail(rs.getString("email"));
                user.setPassword(rs.getString("password"));
                user.setSchool(rs.getString("school"));
                user.setTrackStrand(rs.getString("track_strand"));
                user.setGpa(rs.getDouble("gpa"));
                user.setMonthlyIncome(rs.getString("monthly_income"));
                user.setCollegeCourse(rs.getString("college_course"));
                user.setStudentId(rs.getString("student_id"));
                return user;
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting user by email: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }

    public boolean hasApplied(String email, String scholarshipTitle) {
        String sql = "SELECT COUNT(*) FROM applications WHERE user_email = ? AND scholarship_title = ?";
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, email);
            stmt.setString(2, scholarshipTitle);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean applyForScholarship(String email, String scholarshipTitle) {
        // First check if the application table exists, if not create it
        createApplicationTableIfNotExists();

        String sql = "INSERT INTO applications (user_email, scholarship_title, application_date, status) VALUES (?, ?, ?, ?)";
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, email);
            stmt.setString(2, scholarshipTitle);
            stmt.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
            stmt.setString(4, "PENDING");
            
            int result = stmt.executeUpdate();
            
            if (result > 0) {
                // Update the applicant count in scholarships table
                updateScholarshipApplicantCount(scholarshipTitle);
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void createApplicationTableIfNotExists() {
        String sql = "CREATE TABLE IF NOT EXISTS applications (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "user_email VARCHAR(255) NOT NULL," +
                    "scholarship_title VARCHAR(255) NOT NULL," +
                    "application_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    "status VARCHAR(50) NOT NULL," +
                    "FOREIGN KEY (user_email) REFERENCES users(email)," +
                    "FOREIGN KEY (scholarship_title) REFERENCES scholarships(title)" +
                    ") ENGINE=InnoDB;";
        try {
            Statement stmt = conn.createStatement();
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateScholarshipApplicantCount(String scholarshipTitle) {
        String sql = "UPDATE scholarships SET applicant_count = applicant_count + 1 WHERE title = ?";
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, scholarshipTitle);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Map<String, Object>> getApplicationsForScholarship(String scholarshipTitle) {
        List<Map<String, Object>> applications = new ArrayList<>();
        String sql = "SELECT a.*, u.first_name, u.last_name, u.gpa, u.track_strand, u.monthly_income " +
                    "FROM applications a " +
                    "JOIN users u ON a.user_email = u.email " +
                    "WHERE a.scholarship_title = ? " +
                    "ORDER BY a.application_date DESC";  // Temporarily order by application date
        
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, scholarshipTitle);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Map<String, Object> application = new HashMap<>();
                String email = rs.getString("user_email");
                User user = getUserByEmail(email);
                
                // Calculate priority score
                double priorityScore = ScoreCalculator.calculatePriorityScore(user);
                String assessment = ScoreCalculator.getScoreAssessment(priorityScore);
                
                application.put("email", email);
                application.put("firstName", rs.getString("first_name"));
                application.put("lastName", rs.getString("last_name"));
                application.put("gpa", rs.getDouble("gpa"));
                application.put("trackStrand", rs.getString("track_strand"));
                application.put("monthlyIncome", rs.getString("monthly_income"));
                application.put("status", rs.getString("status"));
                application.put("applicationDate", rs.getTimestamp("application_date"));
                application.put("priorityScore", priorityScore);
                application.put("assessment", assessment);
                
                applications.add(application);
            }

            // Sort applications by priority score (highest first)
            applications.sort((a, b) -> Double.compare(
                (Double) b.get("priorityScore"), 
                (Double) a.get("priorityScore")
            ));

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return applications;
    }

    public int getApplicationCount(String scholarshipTitle) {
        String sql = "SELECT COUNT(*) FROM applications WHERE scholarship_title = ?";
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, scholarshipTitle);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public boolean updateApplicationStatus(String scholarship, String email, String status) {
        String sql = "UPDATE applications SET status = ? WHERE scholarship_title = ? AND user_email = ?";
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, status);
            stmt.setString(2, scholarship);
            stmt.setString(3, email);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public int getTotalApplicationCount() {
        String sql = "SELECT COUNT(*) FROM applications";
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getApplicationCountByStatus(String status) {
        String sql = "SELECT COUNT(*) FROM applications WHERE status = ?";
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, status);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public List<String> getAllScholarshipTitles() {
        List<String> titles = new ArrayList<>();
        String sql = "SELECT title FROM scholarships";
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                titles.add(rs.getString("title"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return titles;
    }

    // Add new method to submit application with scoring
    public boolean submitApplication(String userEmail, String scholarshipTitle) {
        // First check if the user has already applied
        String checkSql = "SELECT COUNT(*) FROM applications WHERE user_email = ? AND scholarship_title = ?";
        
        try {
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setString(1, userEmail);
            checkStmt.setString(2, scholarshipTitle);
            ResultSet rs = checkStmt.executeQuery();
            
            if (rs.next() && rs.getInt(1) > 0) {
                return false; // Already applied
            }
            
            // Get user data for scoring
            User user = getUserByEmail(userEmail);
            double priorityScore = ScoreCalculator.calculatePriorityScore(user);
            
            // Insert application with priority score
            String insertSql = "INSERT INTO applications (user_email, scholarship_title, status, priority_score) " +
                             "VALUES (?, ?, 'PENDING', ?)";
            
            PreparedStatement insertStmt = conn.prepareStatement(insertSql);
            insertStmt.setString(1, userEmail);
            insertStmt.setString(2, scholarshipTitle);
            insertStmt.setDouble(3, priorityScore);
            
            int rowsAffected = insertStmt.executeUpdate();
            
            if (rowsAffected > 0) {
                // Update applicant count in scholarships table
                updateScholarshipApplicantCount(scholarshipTitle, true);
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Helper method to update applicant count
    private void updateScholarshipApplicantCount(String scholarshipTitle, boolean increment) {
        String sql = "UPDATE scholarships SET applicant_count = applicant_count " + 
                    (increment ? "+ 1" : "- 1") + 
                    " WHERE title = ?";
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, scholarshipTitle);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}