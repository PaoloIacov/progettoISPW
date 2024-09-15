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

public class GraphicConversationView extends JFrame implements View {

    private JPanel conversationListPanel;
    private BackgroundPanel messagePanel;  // Panel for messages with background
    private JTextField messageInputField;
    private JButton sendButton;
    private JButton addConversationButton;
    private JButton deleteConversationButton;
    private transient GraphicApplicationController applicationController;
    private Map<JButton, Long> conversationButtonMap = new HashMap<>();
    private Map<JButton, Long> addEmployeeButtonMap = new HashMap<>();
    private Map<JButton, Long> deleteEmployeeButtonMap = new HashMap<>();
    private String selectedProjectName;
    private Long currentConversationId;
    private JComboBox<String> employeeComboBox;
    private String textFont = "Arial";

    public GraphicConversationView(GraphicApplicationController applicationController) {
        this.applicationController = applicationController;
        initializeUI();
    }

    public GraphicConversationView(GraphicApplicationController applicationController, String selectedProjectName) {
        this.applicationController = applicationController;
        this.selectedProjectName = selectedProjectName;
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Conversation View");
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

        conversationListPanel = new JPanel();
        conversationListPanel.setLayout(new BoxLayout(conversationListPanel, BoxLayout.Y_AXIS));
        conversationListPanel.setBackground(new Color(30, 33, 40));
        JScrollPane conversationScrollPane = new JScrollPane(conversationListPanel);
        conversationScrollPane.setBorder(null);
        leftPanel.add(conversationScrollPane);

        return leftPanel;
    }

    private JPanel createRightPanel() {
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BorderLayout());

        JPanel conversationHeader = createConversationHeader();
        rightPanel.add(conversationHeader, BorderLayout.NORTH);

        messagePanel = new BackgroundPanel("background.jpg");
        messagePanel.setLayout(new BoxLayout(messagePanel, BoxLayout.Y_AXIS));
        JScrollPane messageScrollPane = new JScrollPane(messagePanel);
        messageScrollPane.setBorder(null);
        rightPanel.add(messageScrollPane, BorderLayout.CENTER);

        JPanel messageInputPanel = createMessageInputPanel();
        rightPanel.add(messageInputPanel, BorderLayout.SOUTH);

        return rightPanel;
    }

    private JPanel createConversationHeader() {
        JPanel conversationHeader = new JPanel(new GridBagLayout());
        conversationHeader.setBackground(Color.WHITE);
        conversationHeader.setBorder(new EmptyBorder(10, 10, 10, 10));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 5, 0, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        addConversationButton = new JButton("Add Conversation");
        addConversationButton.setPreferredSize(new Dimension(120, 30));
        gbc.gridx = 0;
        conversationHeader.add(addConversationButton, gbc);

        deleteConversationButton = new JButton("Delete Conversation");
        deleteConversationButton.setPreferredSize(new Dimension(120, 30));
        gbc.gridx = 1;
        conversationHeader.add(deleteConversationButton, gbc);

        JButton backButton = new JButton("Back");
        backButton.setPreferredSize(new Dimension(120, 30));
        gbc.gridx = 2;
        conversationHeader.add(backButton, gbc);
        backButton.addActionListener(e -> back());

        return conversationHeader;
    }

    private JPanel createMessageInputPanel() {
        JPanel messageInputPanel = new JPanel();
        messageInputPanel.setLayout(new BorderLayout());
        messageInputPanel.setBackground(Color.WHITE);
        messageInputPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        messageInputField = new JTextField("");
        sendButton = new JButton("Send");

        messageInputPanel.add(messageInputField, BorderLayout.CENTER);
        messageInputPanel.add(sendButton, BorderLayout.EAST);

        return messageInputPanel;
    }

    public void addConversationItem(Long id, String title, String projectName, ActionListener selectActionListener, ActionListener addEmployeeListener, ActionListener deleteEmployeeListener) {
        JPanel conversationItem = new JPanel();
        conversationItem.setLayout(new BoxLayout(conversationItem, BoxLayout.Y_AXIS)); // Cambiato in Y_AXIS per avere top e bottom panel verticali
        conversationItem.setBackground(new Color(42, 46, 54));
        conversationItem.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        conversationItem.setBorder(new EmptyBorder(5, 5, 5, 5));

        // Top Panel: Titolo della conversazione e pulsante "Select"
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.X_AXIS));
        topPanel.setBackground(new Color(42, 46, 54));
        topPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        JLabel titleLabel = new JLabel("<html><b>" + title + "</b><br/><small>" + projectName + "</small></html>");
        titleLabel.setForeground(Color.WHITE);

        JButton selectButton = new JButton("Select");
        selectButton.setBackground(Color.LIGHT_GRAY);
        conversationButtonMap.put(selectButton, id);

        JButton addEmployeeButton = new JButton("Add Employee");
        addEmployeeButton.setBackground(Color.LIGHT_GRAY);
        addEmployeeButton.setEnabled(false);  // Inizialmente disabilitato
        addEmployeeButtonMap.put(addEmployeeButton, id);
        addEmployeeButton.addActionListener(e -> {
            setCurrentConversationID(id);
            addEmployeeListener.actionPerformed(e);
        });

        JButton deleteEmployeeButton = new JButton("Delete Employee");
        deleteEmployeeButton.setBackground(Color.LIGHT_GRAY);
        deleteEmployeeButton.setEnabled(false);  // Inizialmente disabilitato
        deleteEmployeeButtonMap.put(deleteEmployeeButton, id);
        deleteEmployeeButton.addActionListener(e -> {
            setCurrentConversationID(id);
            deleteEmployeeListener.actionPerformed(e);
        });

        // Imposta il listener di selezione per ogni conversazione
        selectButton.addActionListener(e -> {
            selectActionListener.actionPerformed(e);
            setCurrentConversationID(id);  // Imposta l'ID della conversazione corrente

            // Disabilita tutti i pulsanti "Add Employee" e "Delete Employee"
            addEmployeeButtonMap.keySet().forEach(button -> button.setEnabled(false));
            deleteEmployeeButtonMap.keySet().forEach(button -> button.setEnabled(false));

            // Abilita solo i pulsanti "Add Employee" e "Delete Employee" per questa conversazione
            addEmployeeButton.setEnabled(true);
            deleteEmployeeButton.setEnabled(true);
        });

        topPanel.add(titleLabel);
        topPanel.add(Box.createHorizontalGlue());
        topPanel.add(selectButton);

        // Bottom Panel: Pulsanti "Add Employee" e "Delete Employee"
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.X_AXIS));
        bottomPanel.setBackground(new Color(42, 46, 54));
        bottomPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        bottomPanel.add(addEmployeeButton);
        bottomPanel.add(deleteEmployeeButton);

        // Aggiungi i pannelli al conversationItem
        conversationItem.add(topPanel);
        conversationItem.add(bottomPanel);

        conversationListPanel.add(conversationItem);
        conversationListPanel.add(Box.createRigidArea(new Dimension(0, 5))); // Spaziatura

        conversationListPanel.revalidate();
        conversationListPanel.repaint();
    }




    public void addMessageItem(String sender, String message, boolean isReceived) {
        JPanel messageItem = new JPanel();
        messageItem.setLayout(new BoxLayout(messageItem, BoxLayout.Y_AXIS));
        messageItem.setBackground(new Color(255, 255, 255, 150));
        messageItem.setBorder(new EmptyBorder(10, 10, 10, 10));
        messageItem.setMaximumSize(new Dimension(600, 60));

        JLabel messageLabel = new JLabel("<html><b>" + sender + ":</b><br/><p style='width: 200px;'>" + message + "</p></html>");
        messageLabel.setFont(new Font(textFont, Font.PLAIN, 14));

        if (isReceived) {
            messageLabel.setForeground(Color.BLACK);
            messageItem.setAlignmentX(Component.RIGHT_ALIGNMENT);
        } else {
            messageLabel.setForeground(Color.BLUE);
            messageItem.setAlignmentX(Component.LEFT_ALIGNMENT);
        }

        messageItem.add(messageLabel);
        messagePanel.add(messageItem);
        messagePanel.add(Box.createRigidArea(new Dimension(0, 10)));

        refresh();
    }

    public String getMessageInput() {
        return messageInputField.getText();
    }

    public void resetMessageInput() {
        messageInputField.setText("");
    }

    public void setSendButtonListener(ActionListener listener) {
        sendButton.addActionListener(listener);
    }

    public void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public void showSuccess(String message) {
        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    public void clearMessages() {
        messagePanel.removeAll();
        refresh();
    }

    public void clearConversations() {
        conversationListPanel.removeAll();
        conversationButtonMap.clear();
        refresh();
    }

    public String getSelectedProjectName() {
        return selectedProjectName;
    }

    public void setCurrentConversationID(Long conversationId) {
        this.currentConversationId = conversationId;
    }

    public Long getCurrentConversationID() {
        return currentConversationId;
    }

    public String getSelectedUsername() {
        if (employeeComboBox != null) {
            return (String) employeeComboBox.getSelectedItem();
        }
        return null;
    }

    public void setAddConversationButtonListener(ActionListener listener) {
        addConversationButton.addActionListener(listener);
    }

    public void setDeleteConversationButtonListener(ActionListener listener) {
        deleteConversationButton.addActionListener(listener);
    }

    public void setAddEmployeeButtonListener(ActionListener listener) {
        addEmployeeButtonMap.keySet().forEach(button -> button.addActionListener(listener));
    }

    public void setDeleteEmployeeButtonListener(ActionListener listener) {
        deleteEmployeeButtonMap.keySet().forEach(button -> button.addActionListener(listener));
    }

    public String showAddConversationDialog() {
        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Enter conversation description:"));
        JTextField descriptionField = new JTextField();
        panel.add(descriptionField);

        int result = JOptionPane.showConfirmDialog(
                this,
                panel,
                "Create New Conversation",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            String description = descriptionField.getText().trim();

            if (description.isEmpty()) {
                showError("Description cannot be empty.");
                return null;
            }

            return description;
        }

        return null;
    }

    public String showDeleteConversationDialog(List<String> conversationDescriptions) {
        JComboBox<String> conversationComboBox = new JComboBox<>(conversationDescriptions.toArray(new String[0]));

        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Select conversation to delete:"));
        panel.add(conversationComboBox);

        int result = JOptionPane.showConfirmDialog(
                this,
                panel,
                "Delete Conversation",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            return (String) conversationComboBox.getSelectedItem();
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

    public void setAddButtonVisible(boolean visible) {
        addConversationButton.setVisible(visible);
    }

    public void setDeleteButtonVisible(boolean visible) {
        deleteConversationButton.setVisible(visible);
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
