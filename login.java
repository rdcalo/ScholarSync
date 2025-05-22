package scholar;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.geom.RoundRectangle2D;

public class login extends JFrame {
    private JTextField userField;
    private JPasswordField passwordField;
    private UserDAO userDAO;
    private Color primaryColor = new Color(25, 118, 210);
    private Color secondaryColor = new Color(66, 165, 245);
    private Color backgroundColor = new Color(248, 250, 253);
    private Color textColor = new Color(33, 33, 33);
    private Color inputBorderColor = new Color(224, 224, 224);
    private Color inputFocusBorderColor = new Color(25, 118, 210);
    private Color buttonHoverColor = new Color(21, 101, 192);

    public login() {
        userDAO = new UserDAO();

        setTitle("ScholarSync Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1366, 768);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        setBackground(backgroundColor);

        // Main Container
        JPanel mainContainer = new JPanel(new BorderLayout());
        mainContainer.setBackground(backgroundColor);

        // Left Panel (Logo and Welcome Message)
        JPanel leftPanel = createLeftPanel();
        mainContainer.add(leftPanel, BorderLayout.WEST);

        // Right Panel (Login Form)
        JPanel rightPanel = createRightPanel();
        mainContainer.add(rightPanel, BorderLayout.CENTER);

        add(mainContainer);
        setVisible(true);
    }

    private JPanel createLeftPanel() {
        JPanel leftPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                
                // Create gradient paint
                GradientPaint gradient = new GradientPaint(
                    0, 0, primaryColor,
                    getWidth(), getHeight(), secondaryColor
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        leftPanel.setPreferredSize(new Dimension(683, 768));
        leftPanel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(10, 0, 10, 0);

        // Logo
        JLabel logoLabel = new JLabel("Scholar Sync");
        logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 52));
        logoLabel.setForeground(Color.WHITE);
        leftPanel.add(logoLabel, gbc);

        // Slogan
        JLabel sloganMsg = new JLabel("Your path to academic success");
        JLabel subSloganMsg = new JLabel("Welcome Back!");
        sloganMsg.setFont(new Font("Segoe UI", Font.ITALIC, 28));
        subSloganMsg.setFont(new Font("Segoe UI", Font.ITALIC, 16));
        sloganMsg.setForeground(new Color(255, 255, 255, 220));
        subSloganMsg.setForeground(new Color(255, 255, 255, 180));
        leftPanel.add(sloganMsg, gbc);
        leftPanel.add(subSloganMsg, gbc);

        return leftPanel;
    }

    private JPanel createRightPanel() {
        JPanel rightPanel = new JPanel();
        rightPanel.setBackground(backgroundColor);
        rightPanel.setLayout(new GridBagLayout());

        // Login Form Container with Shadow
        JPanel formPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Paint shadow
                g2.setColor(new Color(0, 0, 0, 20));
                g2.fillRoundRect(3, 3, getWidth() - 6, getHeight() - 6, 20, 20);
                
                // Paint panel background
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth() - 3, getHeight() - 3, 20, 20);
                g2.dispose();
            }
        };
        
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createEmptyBorder(40, 50, 40, 50));
        formPanel.setPreferredSize(new Dimension(450, 500));

        // Login Header
        JLabel loginHeader = new JLabel("Log in");
        loginHeader.setFont(new Font("Segoe UI", Font.BOLD, 32));
        loginHeader.setForeground(textColor);
        loginHeader.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setMaximumSize(new Dimension(450, 45));
        headerPanel.add(loginHeader);
        formPanel.add(headerPanel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 40)));

        // Email Field
        JLabel emailLabel = new JLabel("Email");
        emailLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        emailLabel.setForeground(textColor);
        emailLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel emailLabelPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        emailLabelPanel.setBackground(Color.WHITE);
        emailLabelPanel.setMaximumSize(new Dimension(450, 20));
        emailLabelPanel.add(emailLabel);
        formPanel.add(emailLabelPanel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 8)));

        userField = createStyledTextField();
        JPanel emailFieldPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        emailFieldPanel.setBackground(Color.WHITE);
        emailFieldPanel.setMaximumSize(new Dimension(450, 45));
        emailFieldPanel.add(userField);
        formPanel.add(emailFieldPanel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 24)));

        // Password Field
        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordLabel.setForeground(textColor);
        passwordLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel passwordLabelPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        passwordLabelPanel.setBackground(Color.WHITE);
        passwordLabelPanel.setMaximumSize(new Dimension(450, 20));
        passwordLabelPanel.add(passwordLabel);
        formPanel.add(passwordLabelPanel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 8)));

        passwordField = createStyledPasswordField();
        JPanel passwordFieldPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        passwordFieldPanel.setBackground(Color.WHITE);
        passwordFieldPanel.setMaximumSize(new Dimension(450, 45));
        passwordFieldPanel.add(passwordField);
        formPanel.add(passwordFieldPanel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 32)));

        // Login Button
        JButton loginButton = createStyledButton("Login");
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setMaximumSize(new Dimension(450, 45));
        buttonPanel.add(loginButton);
        formPanel.add(buttonPanel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 24)));

        // Register Link
        JPanel registerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        registerPanel.setBackground(Color.WHITE);
        registerPanel.setMaximumSize(new Dimension(450, 30));
        
        JLabel registerText = new JLabel("Don't have an account? ");
        registerText.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        registerText.setForeground(textColor);
        
        JLabel registerLink = new JLabel("Register here");
        registerLink.setFont(new Font("Segoe UI", Font.BOLD, 14));
        registerLink.setForeground(primaryColor);
        registerLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        registerPanel.add(registerText);
        registerPanel.add(registerLink);
        formPanel.add(registerPanel);

        // Error Label
        JLabel errorLabel = new JLabel("");
        errorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        errorLabel.setForeground(new Color(211, 47, 47));
        errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel errorPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        errorPanel.setBackground(Color.WHITE);
        errorPanel.setMaximumSize(new Dimension(450, 20));
        errorPanel.add(errorLabel);
        formPanel.add(errorPanel);

        // Add form panel to right panel with some padding
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(0, 40, 0, 40);
        rightPanel.add(formPanel, gbc);

        // Login Button Action
        loginButton.addActionListener(e -> {
            String email = userField.getText();
            String password = new String(passwordField.getPassword());

            if (email.isEmpty() || password.isEmpty()) {
                errorLabel.setText("Please enter your email and password");
                return;
            }

            // Check for admin login
            if (Main.isAdmin(email, password)) {
                dispose();
                new AdminDashboard();
                return;
            }

            // Regular user login
            User user = userDAO.login(email, password);

            if (user != null) {
                dispose();
                new MyFrame(user);
            } else {
                errorLabel.setText("Invalid email or password");
            }
        });

        // Register Link Action
        registerLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                dispose();
                new register();
            }
            
            @Override
            public void mouseEntered(MouseEvent e) {
                registerLink.setForeground(buttonHoverColor);
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                registerLink.setForeground(primaryColor);
            }
        });

        return rightPanel;
    }

    private JTextField createStyledTextField() {
        JTextField field = new JTextField();
        field.setMaximumSize(new Dimension(350, 45));
        field.setPreferredSize(new Dimension(350, 45));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        Border roundedBorder = new LineBorder(inputBorderColor, 1, true);
        Border paddingBorder = BorderFactory.createEmptyBorder(5, 15, 5, 15);
        field.setBorder(BorderFactory.createCompoundBorder(roundedBorder, paddingBorder));
        
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(inputFocusBorderColor, 2, true),
                    paddingBorder
                ));
            }
            
            @Override
            public void focusLost(FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(roundedBorder, paddingBorder));
            }
        });
        
        return field;
    }

    private JPasswordField createStyledPasswordField() {
        JPasswordField field = new JPasswordField();
        field.setMaximumSize(new Dimension(350, 45));
        field.setPreferredSize(new Dimension(350, 45));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        Border roundedBorder = new LineBorder(inputBorderColor, 1, true);
        Border paddingBorder = BorderFactory.createEmptyBorder(5, 15, 5, 15);
        field.setBorder(BorderFactory.createCompoundBorder(roundedBorder, paddingBorder));
        
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(inputFocusBorderColor, 2, true),
                    paddingBorder
                ));
            }
            
            @Override
            public void focusLost(FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(roundedBorder, paddingBorder));
            }
        });
        
        return field;
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (getModel().isPressed()) {
                    g2.setColor(buttonHoverColor);
                } else if (getModel().isRollover()) {
                    g2.setColor(buttonHoverColor);
                } else {
                    g2.setColor(primaryColor);
                }
                
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 10, 10));
                g2.dispose();
                
                super.paintComponent(g);
            }
        };
        
        button.setMaximumSize(new Dimension(350, 45));
        button.setPreferredSize(new Dimension(350, 45));
        button.setFont(new Font("Segoe UI", Font.BOLD, 15));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        return button;
    }
}
