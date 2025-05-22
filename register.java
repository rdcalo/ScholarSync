package scholar;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.basic.BasicComboPopup;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import javax.swing.DefaultListCellRenderer;

public class register extends JFrame {
    private JTextField[] fields;
    private UserDAO userDAO;
    private Color primaryColor = new Color(25, 118, 210);
    private Color secondaryColor = new Color(66, 165, 245);
    private Color backgroundColor = new Color(248, 250, 253);
    private Color textColor = new Color(33, 33, 33);
    private Color inputBorderColor = new Color(224, 224, 224);
    private Color inputFocusBorderColor = new Color(25, 118, 210);
    private Color buttonHoverColor = new Color(21, 101, 192);

    public register() {
        userDAO = new UserDAO();
    
        setTitle("ScholarSync Registration");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1366, 768);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Main Container
        JPanel mainContainer = new JPanel(new BorderLayout());
        mainContainer.setBackground(backgroundColor);

        // Left Panel (Logo and Welcome Message)
        JPanel leftPanel = createLeftPanel();
        mainContainer.add(leftPanel, BorderLayout.WEST);

        // Right Panel (Registration Form)
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
        JLabel logoLabel = new JLabel("ScholarSync");
        logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 52));
        logoLabel.setForeground(Color.WHITE);
        leftPanel.add(logoLabel, gbc);

        // Slogan
        JLabel sloganMsg = new JLabel("Your path to academic success");
        JLabel subSloganMsg = new JLabel("Create your account");
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

        // Form Container with Shadow
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
        formPanel.setBorder(BorderFactory.createEmptyBorder(40, 80, 40, 80));
        formPanel.setPreferredSize(new Dimension(1200, 800));

        // Form Title
        JLabel titleLabel = new JLabel("Registration Form");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setForeground(textColor);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        formPanel.add(titleLabel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 40)));

        // Create a panel for the form fields with GridBagLayout for better organization
        JPanel fieldsPanel = new JPanel(new GridBagLayout());
        fieldsPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(12, 40, 12, 40); // Slightly reduced vertical spacing
        gbc.weightx = 1.0;

        // Form Fields
        String[] labels = {
            "First Name", "Middle Name", "Last Name",
            "Email", "Password", "Confirm Password",
            "School", "Track and Strand", "GWA",
            "Monthly Family Income", "College Course", "Student ID"
        };

        fields = new JTextField[labels.length];
        JComboBox<String> incomeBox = null;

        // Organize fields in a 2x6 grid
        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = i % 2;
            gbc.gridy = i / 2;

            JPanel fieldContainer = new JPanel();
            fieldContainer.setLayout(new BoxLayout(fieldContainer, BoxLayout.Y_AXIS));
            fieldContainer.setBackground(Color.WHITE);

            JLabel label = new JLabel(labels[i]);
            label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            label.setForeground(textColor);
            label.setAlignmentX(Component.LEFT_ALIGNMENT);
            fieldContainer.add(label);
            fieldContainer.add(Box.createRigidArea(new Dimension(0, 5)));

            if (labels[i].toLowerCase().contains("password")) {
                JPasswordField passField = new JPasswordField();
                styleField(passField);
                fieldContainer.add(passField);
                fields[i] = passField;
            } else if (labels[i].equals("Monthly Family Income")) {
                incomeBox = new JComboBox<>(new String[]{
                    "< ₱10,000", "< ₱30,000", "< ₱70,000", "< ₱100,000", "₱100,000 above"
                });
                styleComboBox(incomeBox);
                
                // Center the label
                label.setHorizontalAlignment(JLabel.CENTER);
                label.setAlignmentX(Component.CENTER_ALIGNMENT);
                
                // Create a panel to hold the centered label and combo box
                JPanel incomePanel = new JPanel();
                incomePanel.setLayout(new BoxLayout(incomePanel, BoxLayout.Y_AXIS));
                incomePanel.setBackground(Color.WHITE);
                incomePanel.add(label);
                incomePanel.add(Box.createRigidArea(new Dimension(0, 5)));
                incomePanel.add(incomeBox);
                
                fieldContainer.removeAll();
                fieldContainer.add(incomePanel);
                
                JTextField dummy = new JTextField();
                dummy.setVisible(false);
                dummy.setName("incomeBox");
                dummy.putClientProperty("combo", incomeBox);
                fields[i] = dummy;
            } else {
                JTextField textField = new JTextField();
                styleField(textField);
                fieldContainer.add(textField);
                fields[i] = textField;
            }

            fieldsPanel.add(fieldContainer, gbc);
        }

        formPanel.add(fieldsPanel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 30))); // Reduced spacing before buttons

        // Buttons Panel
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 30, 10)); // Increased spacing between buttons
        buttonsPanel.setBackground(Color.WHITE);
        buttonsPanel.setMaximumSize(new Dimension(800, 70));
        buttonsPanel.setPreferredSize(new Dimension(800, 70));

        JButton registerButton = createStyledButton("Register", true);
        JButton backButton = createStyledButton("Back to Login", false);

        // Create a container for the buttons to ensure they stay together
        JPanel buttonContainer = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonContainer.setBackground(Color.WHITE);
        buttonContainer.setPreferredSize(new Dimension(500, 50));
        buttonContainer.add(registerButton);
        buttonContainer.add(backButton);
        
        buttonsPanel.add(buttonContainer);
        formPanel.add(buttonsPanel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Error Label
        JLabel errorLabel = new JLabel("");
        errorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        errorLabel.setForeground(new Color(211, 47, 47));
        errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        formPanel.add(errorLabel);

        // Add form panel to right panel with adjusted constraints
        GridBagConstraints formConstraints = new GridBagConstraints();
        formConstraints.gridwidth = GridBagConstraints.REMAINDER;
        formConstraints.anchor = GridBagConstraints.CENTER;
        formConstraints.insets = new Insets(0, 40, 0, 40);
        rightPanel.add(formPanel, formConstraints);

        // Register Button Action
        registerButton.addActionListener(e -> {
            boolean registrationSuccess = validateAndRegister();
            if (registrationSuccess) {
                JOptionPane.showMessageDialog(this, "Registration successful! You can now login.");
                dispose();
                new login();
            }
        });

        // Back Button Action
        backButton.addActionListener(e -> {
            dispose();
            new login();
        });

        return rightPanel;
    }

    private void styleField(JTextField field) {
        field.setMaximumSize(new Dimension(450, 45));
        field.setPreferredSize(new Dimension(450, 45));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setHorizontalAlignment(JTextField.LEFT);
        
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
    }

    private void styleComboBox(JComboBox<String> comboBox) {
        comboBox.setMaximumSize(new Dimension(450, 45));
        comboBox.setPreferredSize(new Dimension(450, 45));
        comboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        Border roundedBorder = new LineBorder(inputBorderColor, 1, true);
        Border paddingBorder = BorderFactory.createEmptyBorder(5, 10, 5, 10);
        comboBox.setBorder(BorderFactory.createCompoundBorder(roundedBorder, paddingBorder));
        
        comboBox.setBackground(Color.WHITE);
        
        // Add a custom renderer to center the text
        DefaultListCellRenderer listRenderer = new DefaultListCellRenderer();
        listRenderer.setHorizontalAlignment(DefaultListCellRenderer.CENTER);
        comboBox.setRenderer(listRenderer);
        
        // Style the combo box popup
        Object child = comboBox.getAccessibleContext().getAccessibleChild(0);
        BasicComboPopup popup = (BasicComboPopup)child;
        JList list = popup.getList();
        list.setSelectionBackground(primaryColor);
        list.setSelectionForeground(Color.WHITE);
        
        // Center text in the popup list as well
        list.setCellRenderer(listRenderer);
    }

    private JButton createStyledButton(String text, boolean isPrimary) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (isPrimary) {
                    if (getModel().isPressed()) {
                        g2.setColor(buttonHoverColor);
                    } else if (getModel().isRollover()) {
                        g2.setColor(buttonHoverColor);
                    } else {
                        g2.setColor(primaryColor);
                    }
                } else {
                    if (getModel().isPressed()) {
                        g2.setColor(new Color(240, 240, 240));
                    } else if (getModel().isRollover()) {
                        g2.setColor(new Color(245, 245, 245));
                    } else {
                        g2.setColor(Color.WHITE);
                    }
                }
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 8, 8));
                
                if (!isPrimary) {
                    g2.setColor(primaryColor);
                    g2.setStroke(new BasicStroke(1.5f));
                    g2.draw(new RoundRectangle2D.Double(1, 1, getWidth() - 2, getHeight() - 2, 8, 8));
                }
                
                g2.dispose();
                super.paintComponent(g);
            }
        };
        
        // Reduced button size
        button.setMaximumSize(new Dimension(150, 35));
        button.setPreferredSize(new Dimension(150, 35));
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(isPrimary ? Color.WHITE : primaryColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        return button;
    }

    private boolean validateAndRegister() {
        // Get values from all fields
        String[] fieldValues = new String[fields.length];
        boolean hasEmptyField = false;
        
        for (int i = 0; i < fields.length; i++) {
            if (fields[i] instanceof JPasswordField) {
                fieldValues[i] = new String(((JPasswordField) fields[i]).getPassword()).trim();
            } else if (fields[i].getName() != null && fields[i].getName().equals("incomeBox")) {
                JComboBox<String> combo = (JComboBox<String>) fields[i].getClientProperty("combo");
                fieldValues[i] = (String) combo.getSelectedItem();
                if (combo.getSelectedIndex() == -1) {
                    hasEmptyField = true;
                    break;
                }
            } else {
                fieldValues[i] = fields[i].getText().trim();
                if (fieldValues[i].isEmpty() && i != 1) { // Middle name can be empty
                    hasEmptyField = true;
                    break;
                }
            }
        }
        
        if (hasEmptyField) {
            JOptionPane.showMessageDialog(this, "Please fill out all required fields.", 
                                        "Missing Information", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        // Validate email format
        String email = fieldValues[3];
        String emailPattern = "^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$";
        if (!email.matches(emailPattern)) {
            JOptionPane.showMessageDialog(this, "Please enter a valid email address.", 
                                        "Invalid Email", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        // Check if email already exists
        if (userDAO.emailExists(email)) {
            JOptionPane.showMessageDialog(this, "This email is already registered.", 
                                        "Email Exists", JOptionPane.ERROR_MESSAGE);
            return false;	
        }
        
        // Validate password match
        if (!fieldValues[4].equals(fieldValues[5])) {
            JOptionPane.showMessageDialog(this, "Passwords do not match!", 
                                        "Password Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        // Validate GPA
        double gpa;
        try {
            gpa = Double.parseDouble(fieldValues[8]);
            if (gpa < 0.0 || gpa > 100.0) {
                JOptionPane.showMessageDialog(this, "GWA must be between 0.0 and 100.0", 
                                            "Invalid GPA", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "GWA must be a number", 
                                        "Invalid GWA", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Show Honor Pledge and Data Privacy Agreement
        String honorPledge = """
            HONOR PLEDGE AND DATA PRIVACY AGREEMENT

            By clicking "I Agree," I solemnly declare that:

            1. TRUTHFULNESS OF INFORMATION
               • All information I have provided in my scholarship application is true, accurate, and complete
               • All supporting documents submitted are genuine and unaltered
               • My academic and financial information is accurately represented

            2. DATA PRIVACY CONSENT
               • I authorize ScholarSync to collect, process, and store my personal information
               • I understand this data will be used for:
                 - Scholarship application evaluation
                 - Academic verification
                 - Financial assessment
                 - Communication regarding my application

            3. ACKNOWLEDGMENT OF CONSEQUENCES
            I understand that:
               • Providing false information will result in immediate disqualification
               • If discovered after scholarship award:
                 - Immediate termination of scholarship
                 - Requirement to repay any received benefits
                 - Possible academic disciplinary action
               • My application may be subject to verification with relevant institutions

            4. DATA PROTECTION RIGHTS
            I acknowledge that:
               • I have the right to access my personal data
               • I can request corrections to my information
               • I can be informed of how my data is being used
               • My data will be protected according to data privacy laws
            """;

        // Create a styled text area for the honor pledge
        JTextArea textArea = new JTextArea(honorPledge);
        textArea.setEditable(false);
        textArea.setWrapStyleWord(true);
        textArea.setLineWrap(true);
        textArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textArea.setBackground(new Color(248, 250, 253));
        textArea.setMargin(new Insets(10, 10, 10, 10));

        // Create a scroll pane for the text area
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(600, 400));

        // Create custom button text
        Object[] options = {"I Agree", "I Decline"};

        // Show the custom dialog
        int choice = JOptionPane.showOptionDialog(
            this,
            scrollPane,
            "Honor Pledge & Data Privacy Agreement",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.PLAIN_MESSAGE,
            null,
            options,
            options[0]
        );

        if (choice != JOptionPane.YES_OPTION) {
            return false;
        }
        
        // Create User object
        User user = new User(
            fieldValues[0],  // First Name
            fieldValues[1],  // Middle Name
            fieldValues[2],  // Last Name
            fieldValues[3],  // Email
            fieldValues[4],  // Password
            fieldValues[6],  // School
            fieldValues[7],  // Track and Strand
            gpa,            // GPA
            fieldValues[9], // Monthly Income
            fieldValues[10], // College Course
            fieldValues[11]  // Student ID
        );
        
        // Register the user
        return userDAO.registerUser(user);
    }
}