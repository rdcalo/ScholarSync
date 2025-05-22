package scholar;

import java.awt.*;
import java.awt.event.*;
import javax.swing.border.*;
import javax.swing.*;
import java.util.HashSet;
import java.util.Set;
import java.sql.*;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.text.SimpleAttributeSet;
import java.util.Arrays;

public class MyFrame extends JFrame implements ActionListener {

    private JButton loginButton;
    private JButton registerButton;
    private User currentUser;
    private UserDAO userDAO;
    // Color scheme matching dashboard
    private Color primaryColor = new Color(25, 118, 210);    // Primary blue
    private Color secondaryColor = new Color(66, 165, 245);  // Secondary blue
    private Color backgroundColor = new Color(248, 250, 253); // Light background
    private Color textColor = new Color(30, 41, 59);         // Dark text
    private Color subtitleColor = new Color(100, 116, 139);  // Subtitle gray
    private Color cardBackground = new Color(255, 255, 255); // White for cards
    private Color borderColor = new Color(226, 232, 240);    // Border color
    private Color greenStatus = new Color(34, 197, 94);      // Green for "Open" status
    // Static set to track shown congratulatory messages
    private static Set<String> shownCongratulations = new HashSet<>();

    public MyFrame(User user) {
        this.currentUser = user;
        this.userDAO = new UserDAO();

        this.setTitle("ScholarSync");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(1366, 768);
        this.setLocationRelativeTo(null);
        this.setLayout(new BorderLayout());
        this.setBackground(backgroundColor);

        // Create and add all UI components first
        JPanel mainContainer = new JPanel(new BorderLayout(0, 0));
        mainContainer.setBackground(backgroundColor);
        mainContainer.setBorder(null);

        JPanel sidebar = createSidebar();
        sidebar.setBorder(new ShadowBorder());
        mainContainer.add(sidebar, BorderLayout.WEST);

        JPanel contentArea = createContentArea();
        contentArea.setBorder(new ShadowBorder());
        mainContainer.add(contentArea, BorderLayout.CENTER);

        this.add(mainContainer);
        
        // Make the main frame visible first
        this.setVisible(true);

        // If user is logged in, show dialogs after a short delay
        if (currentUser != null) {
            // Use Timer to show dialogs after main frame is visible
            Timer timer = new Timer(500, e -> {
                ((Timer) e.getSource()).stop(); // Stop the timer
                showScoringSystemExplanation();
                checkAcceptedScholarships();
            });
            timer.setRepeats(false);
            timer.start();
        }
    }

    private class ShadowBorder extends AbstractBorder {
        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(0, 0, 0, 20));
            g2.fillRoundRect(x + 2, y + 2, width - 4, height - 4, 10, 10);
            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(4, 4, 4, 4);
        }
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(200, 0));
        sidebar.setBackground(primaryColor);
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(BorderFactory.createEmptyBorder(15, 15, 25, 15));

        // Logo Panel with vertical centering
        JPanel logoPanel = new JPanel();
        logoPanel.setLayout(new GridBagLayout());
        logoPanel.setOpaque(false);
        logoPanel.setMaximumSize(new Dimension(200, 45));
        logoPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 15, 0));
        
        JLabel logoLabel = new JLabel("ScholarSync");
        logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        logoLabel.setForeground(Color.WHITE);
        logoPanel.add(logoLabel);

        if (currentUser != null) {
            // User Info Panel
            JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
            userPanel.setOpaque(false);
            userPanel.setMaximumSize(new Dimension(200, 60));
            userPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 30, 0));

            JLabel welcomeLabel = new JLabel("Welcome back,");
            welcomeLabel.setForeground(Color.WHITE);
            welcomeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            
            JPanel welcomeWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
            welcomeWrapper.setOpaque(false);
            welcomeWrapper.add(welcomeLabel);
            
            JLabel nameLabel = new JLabel(currentUser.getFirstName());
            nameLabel.setForeground(Color.WHITE);
            nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
            
            JPanel nameWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
            nameWrapper.setOpaque(false);
            nameWrapper.add(nameLabel);
            
            userPanel.setLayout(new BoxLayout(userPanel, BoxLayout.Y_AXIS));
            userPanel.add(welcomeWrapper);
            userPanel.add(Box.createRigidArea(new Dimension(0, 5)));
            userPanel.add(nameWrapper);

            // Navigation Menu - only Dashboard and Logout
            JPanel navPanel = new JPanel();
            navPanel.setOpaque(false);
            navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.Y_AXIS));
            navPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
            navPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

            // Only add Dashboard and Logout buttons
            String[] menuItems = {"Dashboard", "Logout"};
            for (String item : menuItems) {
                JButton navButton = createNavButton(item);
                if (navButton != null) {
                    JPanel buttonWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
                    buttonWrapper.setOpaque(false);
                    buttonWrapper.setMaximumSize(new Dimension(200, 35));
                    buttonWrapper.add(navButton);
                    navPanel.add(buttonWrapper);
                    navPanel.add(Box.createRigidArea(new Dimension(0, 8)));
                }
            }

            sidebar.add(logoPanel);
            sidebar.add(userPanel);
            sidebar.add(navPanel);
        } else {
            // Auth Buttons Panel
            JPanel authPanel = new JPanel();
            authPanel.setLayout(new BoxLayout(authPanel, BoxLayout.Y_AXIS));
            authPanel.setOpaque(false);
            authPanel.setMaximumSize(new Dimension(200, 100));
            authPanel.setBorder(BorderFactory.createEmptyBorder(30, 0, 0, 0));
            authPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

            loginButton = createAuthButton("Login");
            registerButton = createAuthButton("Create Account");

            JPanel loginWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
            loginWrapper.setOpaque(false);
            loginWrapper.add(loginButton);

            JPanel registerWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
            registerWrapper.setOpaque(false);
            registerWrapper.add(registerButton);

            authPanel.add(loginWrapper);
            authPanel.add(Box.createRigidArea(new Dimension(0, 8)));
            authPanel.add(registerWrapper);

            sidebar.add(logoPanel);
            sidebar.add(authPanel);
        }

        return sidebar;
    }

    private JButton createAuthButton(String text) {
        JButton button = new JButton(text);
        button.setMaximumSize(new Dimension(170, 35));
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(primaryColor);
        button.setBorderPainted(text.equals("Create Account"));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);

        if (text.equals("Create Account")) {
            button.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));
            button.setContentAreaFilled(false);
        }

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                if (text.equals("Create Account")) {
                    button.setBackground(new Color(255, 255, 255, 30));
                    button.setContentAreaFilled(true);
                } else {
                    button.setBackground(secondaryColor);
                }
            }
            public void mouseExited(MouseEvent e) {
                if (text.equals("Create Account")) {
                    button.setContentAreaFilled(false);
                } else {
                    button.setBackground(primaryColor);
                }
            }
        });

        button.addActionListener(this);
        return button;
    }

    private JButton createNavButton(String text) {
        // Only create buttons for Dashboard and Logout
        if (!text.equals("Dashboard") && !text.equals("Logout")) {
            return null;
        }

        JButton button = new JButton();
        button.setPreferredSize(new Dimension(160, 35));
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setForeground(Color.WHITE);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Create panel for content
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout());
        contentPanel.setOpaque(false);
        
        JLabel textLabel = new JLabel(text);
        textLabel.setFont(button.getFont());
        textLabel.setForeground(Color.WHITE);
        
        if (text.equals("Dashboard")) {
            JLabel arrowLabel = new JLabel("›");
            arrowLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
            arrowLabel.setForeground(Color.WHITE);
            contentPanel.add(arrowLabel, BorderLayout.EAST);
        }
        
        contentPanel.add(textLabel, BorderLayout.CENTER);
        button.add(contentPanel);

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(secondaryColor);
                button.setContentAreaFilled(true);
            }

            public void mouseExited(MouseEvent e) {
                button.setContentAreaFilled(false);
            }
        });

        button.addActionListener(e -> {
            switch (text) {
                case "Dashboard":
                    dispose();
                    new dashboard(currentUser);
                    break;
                case "Logout":
                    dispose();
                    new login();
                    break;
            }
        });

        return button;
    }

    private JPanel createContentArea() {
        JPanel contentArea = new JPanel(new BorderLayout(0, 25));
        contentArea.setBackground(backgroundColor);
        contentArea.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        // Header
        JPanel header = new JPanel(new BorderLayout(20, 0));
        header.setBackground(backgroundColor);
        
        // Title and subtitle
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBackground(backgroundColor);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 25, 0));
        
        JLabel titleLabel = new JLabel("Available Scholarships");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setForeground(textColor);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel subtitleLabel = new JLabel("Find and apply for scholarships that match your profile");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        subtitleLabel.setForeground(subtitleColor);
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        titlePanel.add(titleLabel);
        titlePanel.add(Box.createRigidArea(new Dimension(0, 8)));
        titlePanel.add(subtitleLabel);

        // Filter button
        JButton filterButton = new JButton("Filter");
        filterButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        filterButton.setForeground(Color.WHITE);
        filterButton.setBackground(primaryColor);
        filterButton.setBorderPainted(false);
        filterButton.setFocusPainted(false);
        filterButton.setPreferredSize(new Dimension(100, 35));
        filterButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        filterButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                filterButton.setBackground(secondaryColor);
            }
            public void mouseExited(MouseEvent e) {
                filterButton.setBackground(primaryColor);
            }
        });

        header.add(titlePanel, BorderLayout.WEST);
        header.add(filterButton, BorderLayout.EAST);

        // Add separator
        JSeparator separator = new JSeparator();
        separator.setForeground(borderColor);
        separator.setBackground(borderColor);
        
        JPanel headerContainer = new JPanel(new BorderLayout());
        headerContainer.setBackground(backgroundColor);
        headerContainer.add(header, BorderLayout.CENTER);
        headerContainer.add(separator, BorderLayout.SOUTH);
        
        contentArea.add(headerContainer, BorderLayout.NORTH);

        // Scholarships Grid
        JPanel scholarshipsGrid = new JPanel(new GridLayout(0, 2, 25, 25));
        scholarshipsGrid.setBackground(backgroundColor);
        
        // Add scholarship cards
        addScholarshipCards(scholarshipsGrid);
        
        // Wrap in scroll pane
        JScrollPane scrollPane = new JScrollPane(scholarshipsGrid);
        scrollPane.setBorder(null);
        scrollPane.setBackground(backgroundColor);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        contentArea.add(scrollPane, BorderLayout.CENTER);

        return contentArea;
    }

    private void addScholarshipCards(JPanel container) {
        // Get scholarship data from database
        String sql = "SELECT title, start_date, end_date, applicant_count, max_slots FROM scholarships";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                container.add(createScholarshipCard(
                    rs.getString("title"),
                    rs.getDate("start_date").toString(),
                    rs.getDate("end_date").toString(),
                    rs.getInt("applicant_count"),
                    rs.getInt("max_slots")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Add sample data if database connection fails
            for (int i = 0; i < 4; i++) {
                container.add(createScholarshipCard(
                    "GSI's Scholarship Program 2025",
                    "June 13, 2025",
                    "July 30, 2025",
                    125,
                    5
                ));
            }
        }
    }

    private JPanel createScholarshipCard(String title, String startDate, String endDate, 
                                       int applicantCount, int maxSlots) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(cardBackground);
        card.setBorder(BorderFactory.createCompoundBorder(
            new ShadowBorder(),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        // Title
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(textColor);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(titleLabel);
        card.add(Box.createRigidArea(new Dimension(0, 10)));

        // Application Period
        JLabel periodLabel = new JLabel("Application Period: " + startDate + " - " + endDate);
        periodLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        periodLabel.setForeground(subtitleColor);
        periodLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(periodLabel);
        card.add(Box.createRigidArea(new Dimension(0, 10)));

        // Status
        JLabel statusLabel = new JLabel("Status: Open");
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        statusLabel.setForeground(greenStatus);
        statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(statusLabel);

        // Stats Panel
        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        statsPanel.setBackground(cardBackground);
        statsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Get the count of accepted applicants from the database
        int acceptedCount = 0;
        try {
            Connection conn = DBConnection.getConnection();
            String sql = "SELECT COUNT(*) as accepted FROM applications WHERE scholarship_title = ? AND status = 'ACCEPTED'";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, title);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                acceptedCount = rs.getInt("accepted");
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        JLabel applicantsLabel = new JLabel(applicantCount + " applicants");
        JLabel slotsLabel = new JLabel(acceptedCount + "/" + maxSlots + " Accepted Applicants");
        applicantsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        slotsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        applicantsLabel.setForeground(textColor);
        slotsLabel.setForeground(textColor);

        statsPanel.add(applicantsLabel);
        statsPanel.add(slotsLabel);

        card.add(Box.createRigidArea(new Dimension(0, 15)));
        card.add(statsPanel);

        // Tags Panel
        JPanel tagsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        tagsPanel.setBackground(cardBackground);
        tagsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        String[] tags = {"Merit-based", "Full Tuition", "International"};
        for (String tag : tags) {
            JLabel tagLabel = new JLabel(tag);
            tagLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            tagLabel.setForeground(subtitleColor);
            tagLabel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)
            ));
            tagsPanel.add(tagLabel);
        }

        card.add(Box.createRigidArea(new Dimension(0, 15)));
        card.add(tagsPanel);

        // Buttons Panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttonsPanel.setBackground(cardBackground);
        buttonsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // View Description Button
        JButton viewDescriptionButton = new JButton("View Description");
        viewDescriptionButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        viewDescriptionButton.setForeground(Color.WHITE);
        viewDescriptionButton.setBackground(primaryColor);
        viewDescriptionButton.setBorderPainted(false);
        viewDescriptionButton.setFocusPainted(false);
        viewDescriptionButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        viewDescriptionButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                viewDescriptionButton.setBackground(secondaryColor);
            }
            public void mouseExited(MouseEvent e) {
                viewDescriptionButton.setBackground(primaryColor);
            }
        });

        viewDescriptionButton.addActionListener(e -> showScholarshipDetails(title));

        // Apply Now Button
        JButton actionButton = new JButton("Apply Now");
        actionButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        actionButton.setForeground(Color.WHITE);
        actionButton.setBackground(primaryColor);
        actionButton.setBorderPainted(false);
        actionButton.setFocusPainted(false);
        actionButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        actionButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                actionButton.setBackground(secondaryColor);
            }
            public void mouseExited(MouseEvent e) {
                actionButton.setBackground(primaryColor);
            }
        });

        actionButton.addActionListener(e -> handleScholarshipApplication(title, applicantCount, maxSlots));

        // Add buttons to panel
        buttonsPanel.add(viewDescriptionButton);
        buttonsPanel.add(actionButton);

        card.add(Box.createRigidArea(new Dimension(0, 15)));
        card.add(buttonsPanel);

        return card;
    }

    private void showScholarshipDetails(String scholarshipTitle) {
        // Static descriptions and benefits for each scholarship
        String description = "";
        String benefits = "";

        if (scholarshipTitle.equals("Engineering Excellence Scholarship 2025")) {
            description = "The Engineering Excellence Scholarship is designed for outstanding students pursuing engineering degrees. This prestigious program aims to nurture the next generation of innovative engineers who will shape the future of technology and infrastructure. Open to students with strong academic performance in mathematics and physics.\n\nIdeal candidates should demonstrate exceptional problem-solving abilities, innovative thinking, and a passion for engineering. This scholarship supports students across all engineering disciplines including Civil, Mechanical, Electrical, Chemical, and Computer Engineering.";
            benefits = "• Full tuition coverage for the entire academic year\n• Monthly stipend of ₱15,000\n• Annual book allowance of ₱10,000\n• Research grant opportunity worth ₱50,000\n• Internship placement with top engineering firms\n• Access to exclusive engineering workshops and seminars\n• Mentorship program with industry professionals\n• Priority access to laboratory facilities\n• Conference attendance sponsorship";
        } 
        else if (scholarshipTitle.equals("Computer Science Innovation Grant 2025")) {
            description = "The Computer Science Innovation Grant supports exceptional students in computer science and related fields. This program focuses on developing future tech leaders who will drive digital innovation and advancement. Ideal for students passionate about programming, artificial intelligence, and software development.\n\nWe seek candidates who demonstrate creativity in coding, strong analytical skills, and the potential to contribute to the advancement of technology. This scholarship is perfect for students interested in AI, Machine Learning, Software Engineering, or Data Science.";
            benefits = "• Full academic scholarship\n• Monthly allowance of ₱12,000\n• Latest laptop and development tools\n• Cloud computing credits worth ₱100,000\n• Access to premium online learning platforms\n• Priority internship opportunities with tech companies\n• Conference attendance sponsorship\n• Industry certification vouchers\n• Access to high-performance computing resources\n• Networking events with tech industry leaders";
        }
        else if (scholarshipTitle.equals("Mathematics and Data Science Merit Award")) {
            description = "The Mathematics and Data Science Merit Award recognizes talented students in mathematics, statistics, and data science. This scholarship program aims to cultivate analytical minds who will excel in the growing field of data science and mathematical modeling.\n\nWe're looking for students who show exceptional mathematical ability, statistical understanding, and a keen interest in data analysis. Perfect for future data scientists, statisticians, and mathematical researchers.";
            benefits = "• Full tuition coverage\n• Monthly stipend of ₱13,000\n• Statistical software licenses\n• Data science boot camp participation\n• Research funding opportunities\n• Access to high-performance computing resources\n• Professional society memberships\n• International conference travel grant\n• Advanced analytics tools and subscriptions\n• Collaboration opportunities with research institutions";
        }

        // Create and configure the dialog
        JDialog dialog = new JDialog(this, scholarshipTitle, true);
        dialog.setLayout(new BorderLayout(0, 20));
        dialog.getContentPane().setBackground(backgroundColor);
        dialog.setSize(600, 700);  // Increased height from 500 to 700
        dialog.setLocationRelativeTo(this);

        // Content panel with padding
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(backgroundColor);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        // Description section
        JLabel descriptionTitle = new JLabel("Description", SwingConstants.CENTER);
        descriptionTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        descriptionTitle.setForeground(textColor);
        descriptionTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextArea descriptionText = new JTextArea(description);
        descriptionText.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        descriptionText.setForeground(textColor);
        descriptionText.setLineWrap(true);
        descriptionText.setWrapStyleWord(true);
        descriptionText.setEditable(false);
        descriptionText.setBackground(backgroundColor);
        descriptionText.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        descriptionText.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Benefits section
        JLabel benefitsTitle = new JLabel("Benefits", SwingConstants.CENTER);
        benefitsTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        benefitsTitle.setForeground(textColor);
        benefitsTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextArea benefitsText = new JTextArea(benefits);
        benefitsText.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        benefitsText.setForeground(textColor);
        benefitsText.setLineWrap(true);
        benefitsText.setWrapStyleWord(true);
        benefitsText.setEditable(false);
        benefitsText.setBackground(backgroundColor);
        benefitsText.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        benefitsText.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Add components to content panel
        contentPanel.add(descriptionTitle);
        contentPanel.add(descriptionText);
        contentPanel.add(benefitsTitle);
        contentPanel.add(benefitsText);

        // Close button
        JButton closeButton = new JButton("Close");
        closeButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        closeButton.setForeground(Color.WHITE);
        closeButton.setBackground(primaryColor);
        closeButton.setBorderPainted(false);
        closeButton.setFocusPainted(false);
        closeButton.addActionListener(e -> dialog.dispose());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(backgroundColor);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        buttonPanel.add(closeButton);

        // Add components to dialog
        dialog.add(contentPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private void handleScholarshipApplication(String scholarshipTitle, int currentApplicants, int maxSlots) {
        if (currentUser == null) {
            JOptionPane.showMessageDialog(this,
                "Please login to apply for scholarships.",
                "Login Required",
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Check if user has already applied
        if (userDAO.hasApplied(currentUser.getEmail(), scholarshipTitle)) {
            JOptionPane.showMessageDialog(this,
                "You have already applied for this scholarship.",
                "Already Applied",
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Show confirmation dialog
        int choice = JOptionPane.showConfirmDialog(this,
            "Would you like to apply for " + scholarshipTitle + "?",
            "Confirm Application",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);

        if (choice == JOptionPane.YES_OPTION) {
            // Update the database
            if (userDAO.applyForScholarship(currentUser.getEmail(), scholarshipTitle)) {
                JOptionPane.showMessageDialog(this,
                    "Successfully applied for " + scholarshipTitle + "!\nGood luck!",
                    "Application Submitted",
                    JOptionPane.INFORMATION_MESSAGE);
                
                // Refresh the cards
                Container parent = this.getContentPane();
                for (Component comp : parent.getComponents()) {
                    if (comp instanceof JPanel) {
                        JPanel mainContainer = (JPanel) comp;
                        for (Component innerComp : mainContainer.getComponents()) {
                            if (innerComp instanceof JPanel) {
                                JPanel contentArea = (JPanel) innerComp;
                                if (contentArea.getLayout() instanceof BorderLayout) {
                                    for (Component content : contentArea.getComponents()) {
                                        if (content instanceof JScrollPane) {
                                            JScrollPane scrollPane = (JScrollPane) content;
                                            JViewport viewport = scrollPane.getViewport();
                                            if (viewport.getView() instanceof JPanel) {
                                                JPanel grid = (JPanel) viewport.getView();
                                                grid.removeAll();
                                                addScholarshipCards(grid);
                                                grid.revalidate();
                                                grid.repaint();
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this,
                    "Failed to submit application. Please try again.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showScoringSystemExplanation() {
        // Create and configure the dialog
        JDialog dialog = new JDialog(this, "ScholarSync Ranking System", true);
        dialog.setLayout(new BorderLayout(0, 20));
        dialog.getContentPane().setBackground(backgroundColor);
        dialog.setSize(800, 800);  // Increased size
        dialog.setLocationRelativeTo(this);

        // Content panel with padding
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(backgroundColor);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        // Welcome Title
        JLabel welcomeLabel = new JLabel("Welcome to ScholarSync!");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        welcomeLabel.setForeground(textColor);
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        welcomeLabel.setMaximumSize(new Dimension(Integer.MAX_VALUE, welcomeLabel.getPreferredSize().height));

        // Introduction Panel
        JPanel introPanel = new JPanel();
        introPanel.setLayout(new BoxLayout(introPanel, BoxLayout.Y_AXIS));
        introPanel.setBackground(backgroundColor);
        introPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        introPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        String introText = "<html><div style='text-align: center; width: 500px;'>" +
            "Let us explain how our scholarship ranking system works. We've designed a fair scoring system " +
            "that looks at your academic performance, financial situation, and educational background." +
            "</div></html>";
        JLabel introLabel = new JLabel(introText);
        introLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        introLabel.setForeground(textColor);
        introLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        introPanel.add(introLabel);

        // Scoring Components Title
        JLabel componentsLabel = new JLabel("How Your Score is Calculated");
        componentsLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        componentsLabel.setForeground(textColor);
        componentsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        componentsLabel.setHorizontalAlignment(SwingConstants.CENTER);
        componentsLabel.setMaximumSize(new Dimension(Integer.MAX_VALUE, componentsLabel.getPreferredSize().height));

        // Main Components Panel
        JPanel mainComponentsPanel = new JPanel();
        mainComponentsPanel.setLayout(new BoxLayout(mainComponentsPanel, BoxLayout.Y_AXIS));
        mainComponentsPanel.setBackground(backgroundColor);
        mainComponentsPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        mainComponentsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Academic Performance Section
        String academicText = "<html><div style='width: 500px;'>" +
            "<b>1. Academic Performance (50% of your score)</b><br>" +
            "• We use your GPA directly - the higher your grades, the better your score!<br>" +
            "• For example: If your GPA is 95, you get 95 points for this section<br><br>" +
            
            "<b>2. Financial Need (30% of your score)</b><br>" +
            "We consider your family's monthly income:<br>" +
            "• Below ₱10,000: 100 points (Highest Priority)<br>" +
            "• Below ₱30,000: 80 points<br>" +
            "• Below ₱70,000: 60 points<br>" +
            "• Below ₱100,000: 40 points<br>" +
            "• ₱100,000 and above: 20 points<br><br>" +
            
            "<b>3. Academic Track/Strand (20% of your score)</b><br>" +
            "As our scholarships focus on STEM fields, we prioritize students from the STEM track:<br>" +
            "• STEM: 100 points (Highest Priority - Best suited for our programs)<br>" +
            "• ABM: 90 points<br>" +
            "• HUMSS: 85 points<br>" +
            "• GAS: 80 points<br>" +
            "• TVL: 75 points<br>" +
            "• Other tracks: 70 points" +
            "</div></html>";

        JLabel academicLabel = new JLabel(academicText);
        academicLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        academicLabel.setForeground(textColor);
        academicLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainComponentsPanel.add(academicLabel);

        // Final Score Panel
        JPanel finalScorePanel = new JPanel();
        finalScorePanel.setLayout(new BoxLayout(finalScorePanel, BoxLayout.Y_AXIS));
        finalScorePanel.setBackground(backgroundColor);
        finalScorePanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        finalScorePanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        String finalScoreText = "<html><div style='text-align: center; width: 500px;'>" +
            "<b>Your Final Score</b><br><br>" +
            "We combine all these scores to give you a final score from 0 to 100:<br><br>" +
            "• 90-100: Excellent Chance of Selection<br>" +
            "• 80-89: Very Good Chance<br>" +
            "• 70-79: Good Chance<br>" +
            "• 60-69: Fair Chance<br>" +
            "• Below 60: Limited Chance<br><br>" +
            "The higher your score, the better your chances of being selected!" +
            "</div></html>";

        JLabel finalScoreLabel = new JLabel(finalScoreText);
        finalScoreLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        finalScoreLabel.setForeground(textColor);
        finalScoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        finalScorePanel.add(finalScoreLabel);

        // Add all components
        contentPanel.add(welcomeLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        contentPanel.add(introPanel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        contentPanel.add(componentsLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        contentPanel.add(mainComponentsPanel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        contentPanel.add(finalScorePanel);

        // Wrap in scroll pane in case content is too long
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBackground(backgroundColor);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);  // Prevent horizontal scrolling

        // I Understand button
        JButton understandButton = new JButton("I Understand");
        understandButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        understandButton.setForeground(Color.WHITE);
        understandButton.setBackground(primaryColor);
        understandButton.setBorderPainted(false);
        understandButton.setFocusPainted(false);
        understandButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        understandButton.setPreferredSize(new Dimension(150, 40));
        
        understandButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                understandButton.setBackground(secondaryColor);
            }
            public void mouseExited(MouseEvent e) {
                understandButton.setBackground(primaryColor);
            }
        });
        
        understandButton.addActionListener(e -> dialog.dispose());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(backgroundColor);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        buttonPanel.add(understandButton);

        // Add panels to dialog
        dialog.add(scrollPane, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private void styleTextArea(JTextArea textArea, boolean centerAlign) {
        textArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textArea.setForeground(textColor);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setEditable(false);
        textArea.setBackground(backgroundColor);
        
        // Center alignment if specified
        if (centerAlign) {
            // Create a style with center alignment
            StyledDocument doc = new DefaultStyledDocument();
            SimpleAttributeSet center = new SimpleAttributeSet();
            StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
            doc.setParagraphAttributes(0, doc.getLength(), center, false);
            
            // Convert JTextArea to JTextPane for center alignment
            JTextPane textPane = new JTextPane(doc);
            textPane.setText(textArea.getText());
            textPane.setFont(textArea.getFont());
            textPane.setForeground(textArea.getForeground());
            textPane.setBackground(textArea.getBackground());
            textPane.setEditable(false);
            textPane.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
            textPane.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            // Replace the text area with the text pane in the parent container
            Container parent = textArea.getParent();
            if (parent != null) {
                int index = Arrays.asList(parent.getComponents()).indexOf(textArea);
                parent.remove(textArea);
                parent.add(textPane, index);
            }
        } else {
            textArea.setAlignmentX(Component.LEFT_ALIGNMENT);
        }
        
        textArea.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
    }

    private void checkAcceptedScholarships() {
        try {
            Connection conn = DBConnection.getConnection();
            String sql = "SELECT s.title FROM scholarships s " +
                        "JOIN applications a ON s.title = a.scholarship_title " +
                        "WHERE a.user_email = ? AND a.status = 'ACCEPTED'";
            
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, currentUser.getEmail());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String scholarshipTitle = rs.getString("title");
                // Only show congratulations if not shown before
                String key = currentUser.getEmail() + "-" + scholarshipTitle;
                if (!shownCongratulations.contains(key)) {
                    showCongratulationsDialog(scholarshipTitle);
                    // Add to shown set
                    shownCongratulations.add(key);
                }
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void showCongratulationsDialog(String scholarshipTitle) {
        // Create and configure the dialog
        JDialog dialog = new JDialog(this, "Congratulations! 🎉", true);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(backgroundColor);
        dialog.setSize(600, 400); // Reduced height since we have fewer steps

        // Main scroll pane container
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(backgroundColor);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        // Congratulations Icon/Emoji
        JLabel emojiLabel = new JLabel("🎓");
        emojiLabel.setFont(new Font("Segoe UI", Font.PLAIN, 48));
        emojiLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(emojiLabel);
        mainPanel.add(Box.createVerticalStrut(20));

        // Title
        JLabel titleLabel = new JLabel("Fantastic News!");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createVerticalStrut(20));

        // Scholarship name
        JLabel scholarshipLabel = new JLabel(scholarshipTitle);
        scholarshipLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        scholarshipLabel.setForeground(primaryColor);
        scholarshipLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(scholarshipLabel);
        mainPanel.add(Box.createVerticalStrut(10));

        // Status
        JLabel statusLabel = new JLabel("has been ACCEPTED!");
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        statusLabel.setForeground(greenStatus);
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(statusLabel);
        mainPanel.add(Box.createVerticalStrut(20));

        // Message
        String message = "<html><div style='text-align: center; width: 400px;'>" +
            "This is a significant achievement and recognition of your hard work and potential. " +
            "We believe in your abilities and are excited to support your academic journey." +
            "</div></html>";
        JLabel messageLabel = new JLabel(message);
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        messageLabel.setForeground(textColor);
        messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(messageLabel);
        mainPanel.add(Box.createVerticalStrut(30));

        // Next Steps Section
        JLabel nextStepsTitle = new JLabel("Next Steps:");
        nextStepsTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        nextStepsTitle.setForeground(primaryColor);
        nextStepsTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(nextStepsTitle);
        mainPanel.add(Box.createVerticalStrut(10));

        // Next steps
        String[] steps = {
            "• Please check your email for important information regarding your scholarship",
            "• Submit all required documents for verification within 5 working days"
        };

        for (String step : steps) {
            JLabel stepLabel = new JLabel(step);
            stepLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            stepLabel.setForeground(textColor);
            stepLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            mainPanel.add(stepLabel);
            mainPanel.add(Box.createVerticalStrut(5));
        }

        // Scroll Pane
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(backgroundColor);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));

        JButton continueButton = new JButton("Continue");
        continueButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        continueButton.setForeground(Color.WHITE);
        continueButton.setBackground(primaryColor);
        continueButton.setBorderPainted(false);
        continueButton.setFocusPainted(false);
        continueButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        continueButton.setPreferredSize(new Dimension(150, 40));
        
        continueButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                continueButton.setBackground(secondaryColor);
            }
            public void mouseExited(MouseEvent e) {
                continueButton.setBackground(primaryColor);
            }
        });
        
        continueButton.addActionListener(e -> dialog.dispose());
        buttonPanel.add(continueButton);

        // Add components to dialog
        dialog.add(scrollPane, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        // Center the dialog relative to the parent frame
        dialog.setLocationRelativeTo(this);
        
        // Show dialog
        dialog.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == loginButton) {
            this.dispose();
            new login();
        } else if (e.getSource() == registerButton) {
            this.dispose();
            new register();
        }
    }
}
	