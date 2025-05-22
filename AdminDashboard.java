package scholar;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AdminDashboard extends JFrame {
    private JTable applicantsTable;
    private DefaultTableModel tableModel;
    private UserDAO userDAO;
    private Color primaryColor = new Color(52, 152, 219);
    private Color secondaryColor = new Color(41, 128, 185);
    private Color backgroundColor = new Color(245, 247, 250);
    private JPanel applicationsPanel;
    private Timer refreshTimer;
    private JPanel statsPanel;

    public AdminDashboard() {
        userDAO = new UserDAO();
        
        setTitle("ScholarSync Admin Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1366, 768);
        setLocationRelativeTo(null);
        
        // Main container
        JPanel mainContainer = new JPanel(new BorderLayout());
        mainContainer.setBackground(backgroundColor);
        
        // Create and add components
        mainContainer.add(createHeader(), BorderLayout.NORTH);
        mainContainer.add(createMainContent(), BorderLayout.CENTER);
        
        add(mainContainer);
        
        // Start refresh timer (refresh every 30 seconds)
        refreshTimer = new Timer(30000, e -> refreshApplications());
        refreshTimer.start();
        
        setVisible(true);
    }
    
    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(primaryColor);
        header.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JLabel titleLabel = new JLabel("Admin Dashboard");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);

        JButton logoutButton = new JButton("Logout");
        styleButton(logoutButton);
        logoutButton.addActionListener(e -> {
            dispose();
            new login();
        });

        header.add(titleLabel, BorderLayout.WEST);
        header.add(logoutButton, BorderLayout.EAST);

        return header;
    }
    
    private JPanel createMainContent() {
        JPanel mainContent = new JPanel(new BorderLayout(20, 20));
        mainContent.setBackground(backgroundColor);
        mainContent.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // Create statsPanel as a class field so we can update it
        statsPanel = new JPanel(new GridLayout(1, 4, 20, 0));
        statsPanel.setBackground(backgroundColor);
        updateStatsPanel(); // Initial update
        mainContent.add(statsPanel, BorderLayout.NORTH);

        // Applications Panel with horizontal scrolling
        applicationsPanel = new JPanel();
        applicationsPanel.setLayout(new BoxLayout(applicationsPanel, BoxLayout.Y_AXIS));
        applicationsPanel.setBackground(backgroundColor);

        // Create a container panel for horizontal scrolling
        JPanel scrollContainer = new JPanel(new BorderLayout());
        scrollContainer.setBackground(backgroundColor);
        scrollContainer.add(applicationsPanel, BorderLayout.CENTER);

        // Wrap in scroll pane with horizontal scrollbar
        JScrollPane scrollPane = new JScrollPane(scrollContainer);
        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(16);
        
        mainContent.add(scrollPane, BorderLayout.CENTER);

        // Initial load of applications
        refreshApplications();

        return mainContent;
    }
    
    private void updateStatsPanel() {
        statsPanel.removeAll();
        
        // Total Applications
        addStatCard(statsPanel, "Total Applications", String.valueOf(getTotalApplications()));
        
        // Pending Applications
        addStatCard(statsPanel, "Pending Applications", String.valueOf(getPendingApplications()));
        
        // Accepted Applications
        addStatCard(statsPanel, "Accepted Applications", String.valueOf(getAcceptedApplications()));
        
        // Rejected Applications
        addStatCard(statsPanel, "Rejected Applications", String.valueOf(getRejectedApplications()));

        statsPanel.revalidate();
        statsPanel.repaint();
    }
    
    private void addStatCard(JPanel container, String title, String value) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(230, 230, 230)),
            BorderFactory.createEmptyBorder(20, 25, 20, 25)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        titleLabel.setForeground(new Color(100, 100, 100));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        valueLabel.setForeground(new Color(33, 33, 33));
        valueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(titleLabel);
        card.add(Box.createRigidArea(new Dimension(0, 10)));
        card.add(valueLabel);

        container.add(card);
    }
    
    private void refreshApplications() {
        applicationsPanel.removeAll();
        
        // Add header with proper spacing
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        headerPanel.setBackground(backgroundColor);
        headerPanel.setPreferredSize(new Dimension(1300, 40));

        // Create headers with fixed widths matching the content
        String[] headers = {"Rank", "Scholarship", "Applicant", "GWA", "Priority Score", "Assessment", "Status", "Actions"};
        int[] widths = {50, 200, 200, 100, 100, 150, 100, 200};  // Match content widths

        for (int i = 0; i < headers.length; i++) {
            JLabel headerLabel = new JLabel(headers[i], SwingConstants.CENTER);
            headerLabel.setPreferredSize(new Dimension(widths[i], 25));
            headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
            headerLabel.setForeground(Color.black);
            
            // Add left padding to first column
            if (i == 0) {
                headerLabel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
            }
            
            headerPanel.add(headerLabel);
        }
        
        applicationsPanel.add(headerPanel);
        applicationsPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Get all scholarships and their applications
        List<String> scholarships = getScholarshipTitles();
        for (String scholarship : scholarships) {
            List<Map<String, Object>> applications = userDAO.getApplicationsForScholarship(scholarship);
            
            // Sort applications by priority score (highest first)
            applications.sort((a, b) -> Double.compare(
                (Double) b.get("priorityScore"), 
                (Double) a.get("priorityScore")
            ));

            // Add rank to each application
            int rank = 1;
            for (Map<String, Object> application : applications) {
                application.put("rank", rank++);
                JPanel applicationRow = createApplicationRow(scholarship, application);
                applicationsPanel.add(applicationRow);
                applicationsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            }
        }

        applicationsPanel.revalidate();
        applicationsPanel.repaint();
    }
    
    private JPanel createApplicationRow(String scholarship, Map<String, Object> application) {
        // Use FlowLayout with left alignment and proper spacing
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        row.setBackground(Color.WHITE);
        row.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(230, 230, 230)),
            BorderFactory.createEmptyBorder(15, 0, 15, 0)  // Remove horizontal padding, handled by FlowLayout
        ));
        row.setPreferredSize(new Dimension(1300, 60));

        // Rank (fixed width)
        JPanel rankContainer = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        rankContainer.setBackground(Color.WHITE);
        rankContainer.setPreferredSize(new Dimension(50, 25));
        JLabel rankLabel = new JLabel("#" + application.get("rank"), SwingConstants.CENTER);
        rankLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        rankContainer.add(rankLabel);
        
        // Scholarship Title (fixed width)
        JLabel scholarshipLabel = new JLabel(scholarship, SwingConstants.LEFT);
        scholarshipLabel.setPreferredSize(new Dimension(200, 25));
        scholarshipLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));

        // Applicant Name (fixed width)
        String applicantName = application.get("firstName") + " " + application.get("lastName");
        JLabel applicantLabel = new JLabel(applicantName, SwingConstants.LEFT);
        applicantLabel.setPreferredSize(new Dimension(200, 25));
        applicantLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        // GPA (fixed width)
        JLabel gpaLabel = new JLabel(String.format("%.2f", application.get("gpa")), SwingConstants.CENTER);
        gpaLabel.setPreferredSize(new Dimension(100, 25));
        gpaLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        // Priority Score (fixed width)
        JLabel scoreLabel = new JLabel(String.format("%.2f", application.get("priorityScore")), SwingConstants.CENTER);
        scoreLabel.setPreferredSize(new Dimension(100, 25));
        scoreLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        styleScoreLabel(scoreLabel, (Double) application.get("priorityScore"));

        // Assessment (fixed width)
        JLabel assessmentLabel = new JLabel((String) application.get("assessment"), SwingConstants.CENTER);
        assessmentLabel.setPreferredSize(new Dimension(150, 25));
        assessmentLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        styleAssessmentLabel(assessmentLabel);

        // Status (fixed width)
        JLabel statusLabel = new JLabel((String) application.get("status"), SwingConstants.CENTER);
        statusLabel.setPreferredSize(new Dimension(100, 25));
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        styleStatusLabel(statusLabel);

        // Action Buttons Panel (fixed width)
        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        actionsPanel.setBackground(Color.WHITE);
        actionsPanel.setPreferredSize(new Dimension(200, 25));

        if ("PENDING".equals(application.get("status"))) {
            JButton acceptButton = new JButton("Accept");
            JButton rejectButton = new JButton("Reject");
            
            Dimension buttonSize = new Dimension(80, 25);
            acceptButton.setPreferredSize(buttonSize);
            rejectButton.setPreferredSize(buttonSize);
            
            styleActionButton(acceptButton, true);
            styleActionButton(rejectButton, false);

            acceptButton.addActionListener(e -> updateApplicationStatus(scholarship, 
                (String)application.get("email"), "ACCEPTED", statusLabel));
            rejectButton.addActionListener(e -> updateApplicationStatus(scholarship, 
                (String)application.get("email"), "REJECTED", statusLabel));

            actionsPanel.add(acceptButton);
            actionsPanel.add(rejectButton);
        }

        // Add all components with proper spacing
        row.add(Box.createHorizontalStrut(20));  // Left padding
        row.add(rankContainer);
        row.add(scholarshipLabel);
        row.add(applicantLabel);
        row.add(gpaLabel);
        row.add(scoreLabel);
        row.add(assessmentLabel);
        row.add(statusLabel);
        row.add(actionsPanel);

        return row;
    }
    
    private void styleStatusLabel(JLabel label) {
        label.setOpaque(true);
        label.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        
        switch (label.getText()) {
            case "PENDING":
                label.setBackground(new Color(255, 244, 230));
                label.setForeground(new Color(255, 159, 67));
                break;
            case "ACCEPTED":
                label.setBackground(new Color(231, 255, 236));
                label.setForeground(new Color(46, 204, 113));
                break;
            case "REJECTED":
                label.setBackground(new Color(255, 230, 230));
                label.setForeground(new Color(231, 76, 60));
                break;
        }
    }
    
    private void styleActionButton(JButton button, boolean isAccept) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);

        if (isAccept) {
            button.setBackground(new Color(46, 204, 113));
            button.setForeground(Color.WHITE);
        } else {
            button.setBackground(new Color(231, 76, 60));
            button.setForeground(Color.WHITE);
        }

        // Add hover effect
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                if (isAccept) {
                    button.setBackground(new Color(39, 174, 96));
                } else {
                    button.setBackground(new Color(192, 57, 43));
                }
            }
            public void mouseExited(MouseEvent e) {
                if (isAccept) {
                    button.setBackground(new Color(46, 204, 113));
                } else {
                    button.setBackground(new Color(231, 76, 60));
                }
            }
        });
    }
    
    private void styleButton(JButton button) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(secondaryColor);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(100, 35));

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(36, 113, 163));
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(secondaryColor);
            }
        });
    }
    
    private void styleScoreLabel(JLabel label, double score) {
        label.setOpaque(true);
        label.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        
        if (score >= 90) {
            label.setBackground(new Color(231, 255, 236));
            label.setForeground(new Color(46, 204, 113));
        } else if (score >= 80) {
            label.setBackground(new Color(255, 248, 230));
            label.setForeground(new Color(241, 196, 15));
        } else if (score >= 70) {
            label.setBackground(new Color(255, 244, 230));
            label.setForeground(new Color(255, 159, 67));
        } else {
            label.setBackground(new Color(255, 230, 230));
            label.setForeground(new Color(231, 76, 60));
        }
    }
    
    private void styleAssessmentLabel(JLabel label) {
        label.setOpaque(true);
        label.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        
        switch (label.getText()) {
            case "Excellent Candidate":
                label.setBackground(new Color(231, 255, 236));
                label.setForeground(new Color(46, 204, 113));
                break;
            case "Strong Candidate":
                label.setBackground(new Color(255, 248, 230));
                label.setForeground(new Color(241, 196, 15));
                break;
            case "Good Candidate":
                label.setBackground(new Color(255, 244, 230));
                label.setForeground(new Color(255, 159, 67));
                break;
            default:
                label.setBackground(new Color(255, 230, 230));
                label.setForeground(new Color(231, 76, 60));
                break;
        }
    }
    
    private void updateApplicationStatus(String scholarship, String email, String status, JLabel statusLabel) {
        if (userDAO.updateApplicationStatus(scholarship, email, status)) {
            statusLabel.setText(status);
            styleStatusLabel(statusLabel);
            
            // Update both the statistics and applications panels
            updateStatsPanel();
            refreshApplications();
            
            // Show success message
            String message = status.equals("ACCEPTED") ? 
                "Application accepted successfully!" : 
                "Application rejected successfully!";
            JOptionPane.showMessageDialog(this, 
                message,
                "Status Updated",
                JOptionPane.INFORMATION_MESSAGE);
        } else {
            // Show error message if update fails
            JOptionPane.showMessageDialog(this,
                "Failed to update application status. Please try again.",
                "Update Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private int getTotalApplications() {
        return userDAO.getTotalApplicationCount();
    }
    
    private int getPendingApplications() {
        return userDAO.getApplicationCountByStatus("PENDING");
    }
    
    private int getAcceptedApplications() {
        return userDAO.getApplicationCountByStatus("ACCEPTED");
    }
    
    private int getRejectedApplications() {
        return userDAO.getApplicationCountByStatus("REJECTED");
    }
    
    private List<String> getScholarshipTitles() {
        return userDAO.getAllScholarshipTitles();
    }
    
    @Override
    public void dispose() {
        refreshTimer.stop();
        super.dispose();
    }
} 