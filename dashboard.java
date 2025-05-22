package scholar;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

public class dashboard extends JFrame {
    private UserDAO userDAO;
    private User currentUser;
    private Color primaryColor = new Color(25, 118, 210);    // Primary blue
    private Color secondaryColor = new Color(66, 165, 245);  // Secondary blue
    private Color backgroundColor = new Color(248, 250, 253); // Light background
    private Color textColor = new Color(33, 33, 33);         // Dark text
    private Color lightTextColor = new Color(107, 114, 128); // Gray text

    public dashboard(User loggedInUser) {
        userDAO = new UserDAO();
        this.currentUser = loggedInUser;

        setTitle("ScholarSync Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1366, 768);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Main Container
        JPanel mainContainer = new JPanel(new BorderLayout(20, 0));
        mainContainer.setBackground(backgroundColor);

        // Sidebar
        JPanel sidebar = createSidebar();
        mainContainer.add(sidebar, BorderLayout.WEST);

        // Content Area with shadow
        JPanel contentArea = createContentArea();
        contentArea.setBorder(new ShadowBorder());
        mainContainer.add(contentArea, BorderLayout.CENTER);

        add(mainContainer);
        setVisible(true);
    }

    // Custom shadow border class
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
        sidebar.setPreferredSize(new Dimension(200, 768));
        sidebar.setBackground(primaryColor);
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(BorderFactory.createEmptyBorder(25, 15, 25, 15));

        // Logo Panel
        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        logoPanel.setOpaque(false);
        logoPanel.setMaximumSize(new Dimension(200, 45));
        
        JLabel logoLabel = new JLabel("ScholarSync");
        logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        logoLabel.setForeground(Color.WHITE);
        logoPanel.add(logoLabel);

        // User Info Panel
        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        userPanel.setOpaque(false);
        userPanel.setMaximumSize(new Dimension(200, 60));
        userPanel.setBorder(BorderFactory.createEmptyBorder(30, 0, 30, 0));

        JLabel welcomeLabel = new JLabel("Welcome, " + currentUser.getFirstName());
        welcomeLabel.setForeground(Color.WHITE);
        welcomeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        userPanel.add(welcomeLabel);

        // Navigation
        String[] navItems = {"Return to Home"};
        JPanel navPanel = new JPanel();
        navPanel.setOpaque(false);
        navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.Y_AXIS));
        navPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        navPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        for (String item : navItems) {
            JPanel buttonWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
            buttonWrapper.setOpaque(false);
            buttonWrapper.setMaximumSize(new Dimension(200, 35));
            
            JButton navButton = createNavButton(item);
            buttonWrapper.add(navButton);
            navPanel.add(buttonWrapper);
            navPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        }

        sidebar.add(logoPanel);
        sidebar.add(userPanel);
        sidebar.add(navPanel);

        return sidebar;
    }

    private JButton createNavButton(String text) {
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
        
        JLabel arrowLabel = new JLabel("›");
        arrowLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        arrowLabel.setForeground(Color.WHITE);
        
        contentPanel.add(textLabel, BorderLayout.CENTER);
        contentPanel.add(arrowLabel, BorderLayout.EAST);
        
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
            if (text.equals("Return to Home")) {
                dispose();
                new MyFrame(currentUser);
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
        titlePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 25, 0));  // Added bottom padding
        
        JLabel titleLabel = new JLabel("My Dashboard");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));  // Increased from 28
        titleLabel.setForeground(new Color(30, 41, 59));  // Darker, more professional color
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel subtitleLabel = new JLabel("View your profile and application statistics");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15));  // Increased from 14
        subtitleLabel.setForeground(new Color(100, 116, 139));  // Softer subtitle color
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        titlePanel.add(titleLabel);
        titlePanel.add(Box.createRigidArea(new Dimension(0, 8)));  // Increased spacing between title and subtitle
        titlePanel.add(subtitleLabel);
        
        header.add(titlePanel, BorderLayout.WEST);
        
        // Add a subtle separator
        JSeparator separator = new JSeparator();
        separator.setForeground(new Color(226, 232, 240));  // Light gray color
        separator.setBackground(new Color(226, 232, 240));
        
        JPanel headerContainer = new JPanel(new BorderLayout());
        headerContainer.setBackground(backgroundColor);
        headerContainer.add(header, BorderLayout.CENTER);
        headerContainer.add(separator, BorderLayout.SOUTH);
        
        contentArea.add(headerContainer, BorderLayout.NORTH);

        // Main Content
        JPanel mainContent = new JPanel();
        mainContent.setBackground(backgroundColor);
        mainContent.setLayout(new BoxLayout(mainContent, BoxLayout.Y_AXIS));

        // Profile Section
        JPanel profileSection = createProfileSection();
        mainContent.add(profileSection);
        mainContent.add(Box.createRigidArea(new Dimension(0, 25)));

        // Stats Section
        JPanel statsSection = createStatsSection();
        mainContent.add(statsSection);

        JScrollPane scrollPane = new JScrollPane(mainContent);
        scrollPane.setBorder(null);
        scrollPane.setBackground(backgroundColor);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        contentArea.add(scrollPane, BorderLayout.CENTER);

        return contentArea;
    }

    private JPanel createProfileSection() {
        JPanel section = new JPanel();
        section.setLayout(new BorderLayout(20, 20));
        section.setBackground(Color.WHITE);
        section.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(new Color(226, 232, 240), 8),
            BorderFactory.createEmptyBorder(30, 35, 30, 35)
        ));

        // Section title
        JLabel sectionTitle = new JLabel("Profile Information");
        sectionTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        sectionTitle.setForeground(textColor);
        
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setBackground(Color.WHITE);
        titlePanel.add(sectionTitle);
        section.add(titlePanel, BorderLayout.NORTH);

        // Profile Info Grid
        JPanel infoGrid = new JPanel(new GridLayout(3, 2, 50, 30));
        infoGrid.setBackground(Color.WHITE);

        String[][] info = {
            {"Full Name", currentUser.getFirstName() + " " + currentUser.getLastName()},
            {"Email", currentUser.getEmail()},
            {"School", currentUser.getSchool()},
            {"Course", currentUser.getCollegeCourse()},
            {"GPA", String.valueOf(currentUser.getGpa())},
            {"Student ID", currentUser.getStudentId()}
        };

        for (String[] item : info) {
            JPanel fieldPanel = new JPanel(new BorderLayout(0, 10));
            fieldPanel.setBackground(Color.WHITE);

            JLabel label = new JLabel(item[0]);
            label.setFont(new Font("Segoe UI", Font.BOLD, 15));
            label.setForeground(new Color(107, 114, 128));
            
            JLabel value = new JLabel(item[1]);
            value.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            value.setForeground(textColor);
            
            fieldPanel.add(label, BorderLayout.NORTH);
            fieldPanel.add(value, BorderLayout.CENTER);
            
            infoGrid.add(fieldPanel);
        }

        // Wrap infoGrid in a panel with padding
        JPanel infoWrapper = new JPanel(new BorderLayout());
        infoWrapper.setBackground(Color.WHITE);
        infoWrapper.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        infoWrapper.add(infoGrid, BorderLayout.CENTER);
        
        section.add(infoWrapper, BorderLayout.CENTER);
        return section;
    }

    private JPanel createStatsSection() {
        JPanel section = new JPanel(new GridLayout(1, 3, 30, 0));
        section.setPreferredSize(new Dimension(0, 200));
        section.setOpaque(false);

        String[][] stats = {
            {"Applied Scholarships", "1", "Total scholarships you've applied to"},
            {"Pending Applications", "1", "Applications awaiting review"},
            {"Approved Applications", "0", "Successfully approved applications"}
        };

        for (String[] stat : stats) {
            section.add(createStatCard(stat[0], stat[1], stat[2]));
        }

        return section;
    }

    private JPanel createStatCard(String title, String value, String description) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                
                g2.setColor(new Color(226, 232, 240));
                g2.setStroke(new BasicStroke(1.0f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                
                g2.dispose();
            }
        };
        card.setLayout(new BorderLayout(0, 10));
        card.setBorder(BorderFactory.createEmptyBorder(30, 35, 30, 35));
        card.setBackground(Color.WHITE);

        // Value
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 42));
        valueLabel.setForeground(textColor);
        valueLabel.setHorizontalAlignment(SwingConstants.LEFT);

        // Title and Description Panel
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setBackground(Color.WHITE);
        textPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Title
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 17));
        titleLabel.setForeground(lightTextColor);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Description (with HTML wrapping for proper text wrapping)
        JLabel descLabel = new JLabel("<html><div style='width:200px'>" + description + "</div></html>");
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        descLabel.setForeground(lightTextColor);
        descLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        textPanel.add(titleLabel);
        textPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        textPanel.add(descLabel);

        // Main panel to hold value and text
        JPanel mainPanel = new JPanel(new BorderLayout(0, 15));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.add(valueLabel, BorderLayout.NORTH);
        mainPanel.add(textPanel, BorderLayout.CENTER);

        card.add(mainPanel, BorderLayout.CENTER);
        return card;
    }

    // Custom rounded border for cards
    private class RoundedBorder extends AbstractBorder {
        private Color color;
        private int radius;
        
        RoundedBorder(Color color, int radius) {
            this.color = color;
            this.radius = radius;
        }
        
        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.drawRoundRect(x, y, width-1, height-1, radius, radius);
            g2.dispose();
        }
        
        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(4, 4, 4, 4);
        }
    }
}
