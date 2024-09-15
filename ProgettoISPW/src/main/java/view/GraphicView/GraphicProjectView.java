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

public class GraphicProjectView extends JFrame implements View {

    private JPanel projectListPanel;
    private BackgroundPanel projectInfoPanel;  // Panel for messages with background
    private JButton addProjectButton;
    private JButton deleteProjectButton;
    private transient GraphicApplicationController applicationController;
    private Map<JButton, String> projectButtonMap = new HashMap<>();
    private Map<JButton, String> addEmployeeButtonMap = new HashMap<>();
    private Map<JButton, String> deleteEmployeeButtonMap = new HashMap<>();
    private String selectedProjectName;
    private String selectedProjectDescription;
    private JComboBox<String> employeeComboBox;
    private String textFont = "Arial";

    public GraphicProjectView(GraphicApplicationController applicationController) {
        this.applicationController = applicationController;
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Project View");
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

        projectListPanel = new JPanel();
        projectListPanel.setLayout(new BoxLayout(projectListPanel, BoxLayout.Y_AXIS));
        projectListPanel.setBackground(new Color(30, 33, 40));
        JScrollPane projectScrollPane = new JScrollPane(projectListPanel);
        projectScrollPane.setBorder(null);
        leftPanel.add(projectScrollPane);

        return leftPanel;
    }

    private JPanel createRightPanel() {
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BorderLayout());

        JPanel projectHeader = createProjectHeader();
        rightPanel.add(projectHeader, BorderLayout.NORTH);

        // Initialize projectInfoPanel using BackgroundPanel
        projectInfoPanel = new BackgroundPanel("background.jpg");
        projectInfoPanel.setLayout(new BoxLayout(projectInfoPanel, BoxLayout.Y_AXIS));
        projectInfoPanel.setBorder(new EmptyBorder(20, 20, 20, 20)); // Margins for better layout

        // Check if a project is selected
        if (selectedProjectName != null && !selectedProjectName.trim().isEmpty()) {
            projectInfoPanel.removeAll();  // Clear previous content if any

            // Add project name
            JLabel projectNameLabel = new JLabel("<html><h1>Project Name: " + selectedProjectName + "</h1></html>");
            projectNameLabel.setForeground(Color.BLACK);
            projectNameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            projectInfoPanel.add(projectNameLabel);

            projectInfoPanel.add(Box.createRigidArea(new Dimension(0, 20)));

            JLabel projectDescriptionLabel = new JLabel("<html><p style='width: 300px;'>Project Description: " + selectedProjectDescription + "</p></html>");
            projectDescriptionLabel.setForeground(Color.BLACK);
            projectDescriptionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            projectInfoPanel.add(projectDescriptionLabel);

            projectInfoPanel.add(Box.createRigidArea(new Dimension(0, 20)));

            JButton openConversationButton = new JButton("Open conversation for this project");
            openConversationButton.setAlignmentX(Component.CENTER_ALIGNMENT);
            openConversationButton.addActionListener(e -> applicationController.openConversationViewForProject(selectedProjectName));
            projectInfoPanel.add(openConversationButton);
        } else {
            // If no project is selected, clear the panel or show a placeholder
            projectInfoPanel.removeAll();
            JLabel noProjectSelectedLabel = new JLabel("<html><h2>No project selected</h2></html>");
            noProjectSelectedLabel.setForeground(Color.WHITE);
            noProjectSelectedLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            projectInfoPanel.add(noProjectSelectedLabel);
        }

        rightPanel.add(projectInfoPanel, BorderLayout.CENTER);
        // Refresh the projectInfoPanel view
        projectInfoPanel.revalidate();
        projectInfoPanel.repaint();

        // Add projectInfoPanel to the scroll pane
        JScrollPane messageScrollPane = new JScrollPane(projectInfoPanel);
        messageScrollPane.setBorder(null);
        rightPanel.add(messageScrollPane, BorderLayout.CENTER);

        return rightPanel;
    }


    private void updateProjectInfoPanel() {
        projectInfoPanel.removeAll();

        if (selectedProjectName != null && !selectedProjectName.trim().isEmpty()) {
            // Aggiungi il nome del progetto
            JLabel projectNameLabel = new JLabel("<html><h1>Project Name: " + selectedProjectName + "</h1></html>");
            projectNameLabel.setForeground(Color.WHITE);
            projectNameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            projectInfoPanel.add(projectNameLabel);

            projectInfoPanel.add(Box.createRigidArea(new Dimension(0, 20)));

            // Aggiungi la descrizione del progetto
            JLabel projectDescriptionLabel = new JLabel("<html><p style='width: 300px;'>Project Description: " + selectedProjectDescription + "</p></html>");
            projectDescriptionLabel.setForeground(Color.WHITE);
            projectDescriptionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            projectInfoPanel.add(projectDescriptionLabel);

            projectInfoPanel.add(Box.createRigidArea(new Dimension(0, 20)));

            // Aggiungi il pulsante "Open conversation for this project"
            JButton openConversationButton = new JButton("Open conversation for this project");
            openConversationButton.setAlignmentX(Component.CENTER_ALIGNMENT);
            openConversationButton.setPreferredSize(new Dimension(300, 50));
            openConversationButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

            openConversationButton.addActionListener(e -> applicationController.openConversationViewForProject(selectedProjectName));
            projectInfoPanel.add(openConversationButton);
        }

        // Aggiorna la visualizzazione del pannello
        projectInfoPanel.revalidate();
        projectInfoPanel.repaint();
    }

    private JPanel createProjectHeader() {
        JPanel projectHeader = new JPanel(new GridBagLayout());
        projectHeader.setBackground(Color.WHITE);
        projectHeader.setBorder(new EmptyBorder(10, 10, 10, 10));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 5, 0, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        addProjectButton = new JButton("Add Project");
        addProjectButton.setPreferredSize(new Dimension(120, 30));
        gbc.gridx = 0;
        projectHeader.add(addProjectButton, gbc);

        deleteProjectButton = new JButton("Delete Project");
        deleteProjectButton.setPreferredSize(new Dimension(120, 30));
        gbc.gridx = 1;
        projectHeader.add(deleteProjectButton, gbc);

        JButton backButton = new JButton("Back");
        backButton.setPreferredSize(new Dimension(120, 30));
        gbc.gridx = 2;
        projectHeader.add(backButton, gbc);
        backButton.addActionListener(e -> back());

        return projectHeader;
    }

    public void addProjectItem(String projectName, ActionListener selectActionListener, ActionListener addEmployeeListener, ActionListener deleteEmployeeListener) {
        JPanel projectItem = new JPanel();
        projectItem.setLayout(new BoxLayout(projectItem, BoxLayout.Y_AXIS));
        projectItem.setBackground(new Color(42, 46, 54));
        projectItem.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        projectItem.setBorder(new EmptyBorder(5, 5, 5, 5));

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.X_AXIS));
        topPanel.setBackground(new Color(42, 46, 54));
        topPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        JLabel titleLabel = new JLabel("<html><b>" + projectName + "</b></html>");
        titleLabel.setForeground(Color.WHITE);

        JButton selectButton = new JButton("Select");
        selectButton.setBackground(Color.LIGHT_GRAY);
        projectButtonMap.put(selectButton, projectName);
        selectButton.addActionListener(e -> {
            setSelectedProjectName(projectName);
            setSelectedProjectDescription(selectedProjectDescription);
            selectActionListener.actionPerformed(e);
            updateProjectInfoPanel();
        });

        JButton addEmployeeButton = new JButton("Add Employee");
        addEmployeeButton.setBackground(Color.LIGHT_GRAY);
        addEmployeeButton.setEnabled(false);  // Initially disabled
        addEmployeeButtonMap.put(addEmployeeButton, projectName);
        addEmployeeButton.addActionListener(e -> {
            setSelectedProjectName(projectName);
            addEmployeeListener.actionPerformed(e);
        });

        JButton deleteEmployeeButton = new JButton("Delete Employee");
        deleteEmployeeButton.setBackground(Color.LIGHT_GRAY);
        deleteEmployeeButton.setEnabled(false);  // Initially disabled
        deleteEmployeeButtonMap.put(deleteEmployeeButton, projectName);
        deleteEmployeeButton.addActionListener(e -> {
            setSelectedProjectName(projectName);
            deleteEmployeeListener.actionPerformed(e);
        });

        selectButton.addActionListener(e -> {
            selectActionListener.actionPerformed(e);
            setSelectedProjectName(projectName);

            addEmployeeButtonMap.keySet().forEach(button -> button.setEnabled(false));
            deleteEmployeeButtonMap.keySet().forEach(button -> button.setEnabled(false));

            addEmployeeButton.setEnabled(true);
            deleteEmployeeButton.setEnabled(true);
        });

        topPanel.add(titleLabel);
        topPanel.add(Box.createHorizontalGlue());
        topPanel.add(selectButton);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.X_AXIS));
        bottomPanel.setBackground(new Color(42, 46, 54));
        bottomPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        bottomPanel.add(addEmployeeButton);
        bottomPanel.add(deleteEmployeeButton);

        projectItem.add(topPanel);
        projectItem.add(bottomPanel);

        projectListPanel.add(projectItem);
        projectListPanel.add(Box.createRigidArea(new Dimension(0, 5)));

        projectListPanel.revalidate();
        projectListPanel.repaint();
    }

    public void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public void showSuccess(String message) {
        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    public void clearMessages() {
        projectInfoPanel.removeAll();
        refresh();
    }

    public void clearProjects() {
        projectListPanel.removeAll();
        projectButtonMap.clear();
        refresh();
    }

    public void setSelectedProjectName(String projectName) {
        this.selectedProjectName = projectName;
    }

    public void setSelectedProjectDescription(String projectDescription) {
        this.selectedProjectDescription = projectDescription;
    }

    public String getSelectedUsername() {
        if (employeeComboBox != null) {
            return (String) employeeComboBox.getSelectedItem();
        }
        return null;
    }

    public void setAddProjectButtonListener(ActionListener listener) {
        addProjectButton.addActionListener(listener);
    }

    public void setDeleteProjectButtonListener(ActionListener listener) {
        deleteProjectButton.addActionListener(listener);
    }

    public void setAddEmployeeButtonListener(ActionListener listener) {
        addEmployeeButtonMap.keySet().forEach(button -> button.addActionListener(listener));
    }

    public void setDeleteEmployeeButtonListener(ActionListener listener) {
        deleteEmployeeButtonMap.keySet().forEach(button -> button.addActionListener(listener));
    }

    public String[] showAddProjectDialog(List<String> projectManagerNames) {
        JPanel panel = new JPanel(new GridLayout(0, 1));

        // Campo per il nome del progetto
        panel.add(new JLabel("Enter project name:"));
        JTextField projectNameField = new JTextField();
        panel.add(projectNameField);

        // Campo per la descrizione del progetto, con altezza maggiore per mostrare più testo
        panel.add(new JLabel("Enter project description:"));
        JTextArea projectDescriptionArea = new JTextArea(5, 20);  // Text area con più righe
        projectDescriptionArea.setLineWrap(true);
        projectDescriptionArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(projectDescriptionArea);
        panel.add(scrollPane);

        // ComboBox per selezionare il Project Manager
        panel.add(new JLabel("Select Project Manager:"));
        JComboBox<String> projectManagerComboBox = new JComboBox<>(projectManagerNames.toArray(new String[0]));
        panel.add(projectManagerComboBox);

        int result = JOptionPane.showConfirmDialog(
                this,
                panel,
                "Create New Project",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            String projectName = projectNameField.getText().trim();
            String projectDescription = projectDescriptionArea.getText().trim();
            String selectedProjectManager = (String) projectManagerComboBox.getSelectedItem();  // Ottieni il valore selezionato dal ComboBox

            if (projectName.isEmpty() || projectDescription.isEmpty()) {
                showError("Project name and description cannot be empty.");
                return null;
            }

            return new String[]{projectName, projectDescription, selectedProjectManager};  // Ritorna tutti e tre i valori
        }

        return null;
    }



    public String showDeleteProjectDialog(List<String> projectNames) {
        JComboBox<String> projectComboBox = new JComboBox<>(projectNames.toArray(new String[0]));

        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Select project to delete:"));
        panel.add(projectComboBox);

        int result = JOptionPane.showConfirmDialog(
                this,
                panel,
                "Delete Project",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            return (String) projectComboBox.getSelectedItem();
        }
        return null;
    }

    public String showAddEmployeeDialog(List<String> employeeNames) {
        employeeComboBox = new JComboBox<>(employeeNames.toArray(new String[0]));

        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Select employee to Add:"));
        panel.add(employeeComboBox);

        int result = JOptionPane.showConfirmDialog(
                this,
                panel,
                "Add Employee",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            return getSelectedUsername();
        }
        return null;
    }

    public String showDeleteEmployeeDialog(List<String> employeeNames) {
        employeeComboBox = new JComboBox<>(employeeNames.toArray(new String[0]));

        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Select employee to Delete:"));
        panel.add(employeeComboBox);

        int result = JOptionPane.showConfirmDialog(
                this,
                panel,
                "Delete Employee",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            return getSelectedUsername();
        }
        return null;
    }

    public void setSelectedProjectName(String projectName, String projectDescription) {
        this.selectedProjectName = projectName;
        this.selectedProjectDescription = projectDescription;
    }

    public void setAddButtonVisible(boolean visible) {
        addProjectButton.setVisible(visible);
    }

    public void setDeleteButtonVisible(boolean visible) {
        deleteProjectButton.setVisible(visible);
    }

    public void setAddEmployeeButtonVisible(boolean visible) {
        for (JButton button : addEmployeeButtonMap.keySet()) {
            button.setVisible(visible);
        }
    }

    public void setDeleteEmployeeButtonVisible(boolean visible) {
        for (JButton button : deleteEmployeeButtonMap.keySet()) {
            button.setVisible(visible);
        }
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
