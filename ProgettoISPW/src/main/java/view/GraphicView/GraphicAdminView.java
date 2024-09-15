package view.GraphicView;

import controller.ApplicationController.GraphicApplicationController;
import view.View;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GraphicAdminView extends JFrame implements View {

    private JPanel userListPanel;// Panel for displaying user details
    private JButton addUserButton;
    private JButton deleteUserButton;
    private transient GraphicApplicationController applicationController;
    private Map<JButton, String> userButtonMap = new HashMap<>();
    private Map<JButton, String> addUserButtonMap = new HashMap<>();
    private Map<JButton, String> deleteUserButtonMap = new HashMap<>();
    private String selectedUsername;
    private String textFont = "Arial";

    public GraphicAdminView(GraphicApplicationController applicationController) {
        this.applicationController = applicationController;
        initializeUI();
    }

    private void initializeUI() {
        setTitle("User View");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel leftPanel = createLeftPanel();
        JPanel rightPanel = createRightPanel();

        add(leftPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.CENTER);
        setVisible(true);
    }

    private JPanel createLeftPanel() {
        JPanel leftPanel = new JPanel();
        leftPanel.setBackground(new Color(30, 33, 40));
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setPreferredSize(new Dimension(300, 600));
        leftPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        userListPanel = new JPanel();
        userListPanel.setLayout(new BoxLayout(userListPanel, BoxLayout.Y_AXIS));
        userListPanel.setBackground(new Color(30, 33, 40));
        JScrollPane userScrollPane = new JScrollPane(userListPanel);
        userScrollPane.setBorder(null);
        leftPanel.add(userScrollPane);

        return leftPanel;
    }

    private JPanel createRightPanel() {
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BorderLayout());

        JPanel userHeader = createUserHeader();
        rightPanel.add(userHeader, BorderLayout.NORTH);

        BackgroundPanel userInfoPanel = new BackgroundPanel("background.jpg");
        userInfoPanel.setLayout(new BoxLayout(userInfoPanel, BoxLayout.Y_AXIS));
        userInfoPanel.setBorder(new EmptyBorder(20, 20, 20, 20)); // Margins for better layout

        userInfoPanel.removeAll();


        if (selectedUsername != null && !selectedUsername.trim().isEmpty()) {

            JLabel userNameLabel = new JLabel("<html><h1>Username: " + selectedUsername + "</h1></html>");
            userNameLabel.setForeground(Color.BLACK);
            userNameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            userInfoPanel.add(userNameLabel);

            userInfoPanel.add(Box.createRigidArea(new Dimension(0, 20)));

            // Here you can add more details for the selected user, such as roles, projects, etc.

            // Add the button "Manage User Details"
            JButton manageUserButton = new JButton("Manage User Details");
            manageUserButton.setAlignmentX(Component.CENTER_ALIGNMENT);
            manageUserButton.setPreferredSize(new Dimension(200, 40));

            // Set the button listener to call the appropriate method in ApplicationController
            manageUserButton.addActionListener(e -> applicationController.openAdminView());
            userInfoPanel.add(Box.createVerticalGlue()); // This will push the button to the bottom
            userInfoPanel.add(manageUserButton);

            rightPanel.add(userInfoPanel, BorderLayout.CENTER);
        }

        // Refresh the panel display
        userInfoPanel.revalidate();
        userInfoPanel.repaint();

        JButton goToProjectsButton = new JButton("Go to Projects");
        goToProjectsButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        goToProjectsButton.setPreferredSize(new Dimension(200, 40));
        goToProjectsButton.setMaximumSize(new Dimension(200, 40));


        goToProjectsButton.addActionListener(e -> applicationController.openProjectViewForAdmin());

        userInfoPanel.add(Box.createRigidArea(new Dimension(0, 20))); // Spazio tra pulsanti
        userInfoPanel.add(goToProjectsButton);

        userInfoPanel.revalidate();
        userInfoPanel.repaint();

        // Scroll pane
        JScrollPane messageScrollPane = new JScrollPane(userInfoPanel);
        messageScrollPane.setBorder(null);
        rightPanel.add(messageScrollPane, BorderLayout.CENTER);

        return rightPanel;
    }

    private JPanel createUserHeader() {
        JPanel userHeader = new JPanel(new GridBagLayout());
        userHeader.setBackground(Color.WHITE);
        userHeader.setBorder(new EmptyBorder(10, 10, 10, 10));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 5, 0, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        addUserButton = new JButton("Add User");
        addUserButton.setPreferredSize(new Dimension(120, 30));
        gbc.gridx = 0;
        userHeader.add(addUserButton, gbc);

        deleteUserButton = new JButton("Delete User");
        deleteUserButton.setPreferredSize(new Dimension(120, 30));
        gbc.gridx = 1;
        userHeader.add(deleteUserButton, gbc);

        JButton backButton = new JButton("Back");
        backButton.setPreferredSize(new Dimension(120, 30));
        gbc.gridx = 2;
        userHeader.add(backButton, gbc);
        backButton.addActionListener(e -> back());

        return userHeader;
    }

    public void addUserItem(String username, ActionListener selectActionListener) {
        JPanel userItem = new JPanel();
        userItem.setLayout(new BoxLayout(userItem, BoxLayout.Y_AXIS));
        userItem.setBackground(new Color(42, 46, 54));
        userItem.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        userItem.setBorder(new EmptyBorder(5, 5, 5, 5));

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.X_AXIS));
        topPanel.setBackground(new Color(42, 46, 54));
        topPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        JLabel titleLabel = new JLabel("<html><b>" + username + "</b></html>");
        titleLabel.setForeground(Color.WHITE);

        JButton selectButton = new JButton("Select");
        selectButton.setBackground(Color.LIGHT_GRAY);
        userButtonMap.put(selectButton, username);
        selectButton.addActionListener(e -> {
            selectActionListener.actionPerformed(e);
            setSelectedUsername(username);
        });

        topPanel.add(titleLabel);
        topPanel.add(Box.createHorizontalGlue());
        topPanel.add(selectButton);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.X_AXIS));
        bottomPanel.setBackground(new Color(42, 46, 54));
        bottomPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        userItem.add(topPanel);
        userItem.add(bottomPanel);

        userListPanel.add(userItem);
        userListPanel.add(Box.createRigidArea(new Dimension(0, 5)));

        userListPanel.revalidate();
        userListPanel.repaint();
    }

    public void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public void showSuccess(String message) {
        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    public void clearUsers() {
        userListPanel.removeAll();
        userButtonMap.clear();
        refresh();
    }

    public void setSelectedUsername(String username) {
        this.selectedUsername = username;
    }

    public void setAddUserButtonListener(ActionListener listener) {
        addUserButton.addActionListener(listener);
    }

    public void setDeleteUserButtonListener(ActionListener listener) {
        deleteUserButton.addActionListener(listener);
    }

    public String[] showAddUserDialog() {
        JPanel panel = new JPanel(new GridLayout(0, 1));

        // Username Field
        panel.add(new JLabel("Enter username:"));
        JTextField usernameField = new JTextField();
        panel.add(usernameField);

        // Password Field
        panel.add(new JLabel("Enter password:"));
        JPasswordField passwordField = new JPasswordField();  // Using JPasswordField for passwords
        panel.add(passwordField);

        // First Name Field
        panel.add(new JLabel("Enter first name:"));
        JTextField firstNameField = new JTextField();
        panel.add(firstNameField);

        // Last Name Field
        panel.add(new JLabel("Enter last name:"));
        JTextField lastNameField = new JTextField();
        panel.add(lastNameField);

        // Role ComboBox
        panel.add(new JLabel("Select role:"));
        JComboBox<String> roleComboBox = new JComboBox<>(new String[]{"Employee", "Project Manager", "Admin"});
        panel.add(roleComboBox);

        // Show dialog
        int result = JOptionPane.showConfirmDialog(
                this,
                panel,
                "Add New User",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();  // Convert password field to String
            String firstName = firstNameField.getText().trim();
            String lastName = lastNameField.getText().trim();
            String role = (String) roleComboBox.getSelectedItem();  // Get selected role

            if (username.isEmpty() || password.isEmpty() || firstName.isEmpty() || lastName.isEmpty()) {
                showError("Username, password, first name, and last name cannot be empty.");
                return null;
            }

            return new String[]{username, password, firstName, lastName, role};  // Return all inputs
        }

        return null;  // Return null if canceled
    }


    public String showDeleteUserDialog(List<String> usernames) {
        JComboBox<String> userComboBox = new JComboBox<>(usernames.toArray(new String[0]));

        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Select user to delete:"));
        panel.add(userComboBox);

        int result = JOptionPane.showConfirmDialog(
                this,
                panel,
                "Delete User",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            return (String) userComboBox.getSelectedItem();
        }
        return null;
    }

    public void setAddButtonVisible(boolean visible) {
        addUserButton.setVisible(visible);
    }

    public void setDeleteButtonVisible(boolean visible) {
        deleteUserButton.setVisible(visible);
    }

    @Override
    public void display() {
        setVisible(true);
    }

    @Override
    public void close() {
        dispose();
    }

    @Override
    public void refresh() {
        revalidate();
        repaint();
    }

    @Override
    public void back() {
        applicationController.back();
    }
}
