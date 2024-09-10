package view;

import controller.ApplicationController;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

public class ConversationView extends JFrame implements View {

    private JPanel conversationListPanel;
    private BackgroundPanel messagePanel;  // Usa BackgroundPanel per il pannello dei messaggi
    private JTextField messageInputField;
    private JButton sendButton;
    private JButton backButton;
    private transient ApplicationController applicationController;
    private Map<JButton, Long> conversationButtonMap = new HashMap<>();
    private Long currentConversationId;

    public ConversationView(ApplicationController applicationController) {
        this.applicationController = applicationController;
        setTitle("Conversation View");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());

        // Pannello sinistro: Lista delle conversazioni
        JPanel leftPanel = new JPanel();
        leftPanel.setBackground(new Color(30, 33, 40));
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setPreferredSize(new Dimension(300, 600));
        leftPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Pulsante "Back"
        backButton = new JButton("Back");
        backButton.addActionListener(e -> applicationController.back());
        leftPanel.add(backButton);

        // Lista di conversazioni
        conversationListPanel = new JPanel();
        conversationListPanel.setLayout(new BoxLayout(conversationListPanel, BoxLayout.Y_AXIS));
        conversationListPanel.setBackground(new Color(30, 33, 40));
        JScrollPane conversationScrollPane = new JScrollPane(conversationListPanel);
        conversationScrollPane.setBorder(null);
        leftPanel.add(conversationScrollPane);

        // Pannello destro: Messaggi
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BorderLayout());

        // Barra superiore con dettagli della conversazione
        JPanel conversationHeader = new JPanel();
        conversationHeader.setBackground(Color.WHITE);
        conversationHeader.setLayout(new BoxLayout(conversationHeader, BoxLayout.X_AXIS));
        conversationHeader.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel conversationTitle = new JLabel("Select a conversation");
        conversationTitle.setFont(new Font("Arial", Font.BOLD, 18));
        conversationHeader.add(conversationTitle);

        rightPanel.add(conversationHeader, BorderLayout.NORTH);

        // Pannello dei messaggi con sfondo
        messagePanel = new BackgroundPanel("background.jpg");
        messagePanel.setLayout(new BoxLayout(messagePanel, BoxLayout.Y_AXIS));
        JScrollPane messageScrollPane = new JScrollPane(messagePanel);
        messageScrollPane.setBorder(null);
        rightPanel.add(messageScrollPane, BorderLayout.CENTER);

        // Pannello inferiore per l'inserimento del messaggio
        JPanel messageInputPanel = new JPanel();
        messageInputPanel.setLayout(new BorderLayout());
        messageInputPanel.setBackground(Color.WHITE);
        messageInputPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        messageInputField = new JTextField("");
        sendButton = new JButton("Send");

        messageInputPanel.add(messageInputField, BorderLayout.CENTER);
        messageInputPanel.add(sendButton, BorderLayout.EAST);

        rightPanel.add(messageInputPanel, BorderLayout.SOUTH);

        // Aggiunta dei pannelli alla finestra
        add(leftPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.CENTER);

        setVisible(true);
    }

    // Metodo per aggiungere una conversazione alla lista delle conversazioni
    public void addConversationItem(String id, String title, String projectName, ActionListener selectActionListener) {
        JPanel conversationItem = new JPanel();
        conversationItem.setLayout(new BoxLayout(conversationItem, BoxLayout.X_AXIS));
        conversationItem.setBackground(new Color(42, 46, 54));
        conversationItem.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        conversationItem.setBorder(new EmptyBorder(5, 5, 5, 5));

        JLabel titleLabel = new JLabel("<html><b>" + title + "</b><br/><small>" + projectName + "</small></html>");
        titleLabel.setForeground(Color.WHITE);
        JButton selectButton = new JButton("Select");
        selectButton.setBackground(Color.LIGHT_GRAY);

        Long conversationId = Long.parseLong(id);
        conversationButtonMap.put(selectButton, conversationId);

        // Imposta il listener di selezione per ogni conversazione
        selectButton.addActionListener(selectActionListener);

        conversationItem.add(titleLabel);
        conversationItem.add(Box.createHorizontalGlue());
        conversationItem.add(selectButton);

        conversationListPanel.add(conversationItem);
        conversationListPanel.add(Box.createRigidArea(new Dimension(0, 5))); // Spaziatura

        conversationListPanel.revalidate();
        conversationListPanel.repaint();
    }

    public Long getCurrentConversationId() {
        return currentConversationId;
    }

    public void setCurrentConversationId(Long currentConversationId) {
        this.currentConversationId = currentConversationId;
    }

    public void addMessageItem(String sender, String message, boolean isReceived) {
        JPanel messageItem = new JPanel();
        messageItem.setLayout(new BoxLayout(messageItem, BoxLayout.Y_AXIS));
        messageItem.setBackground(new Color(255, 255, 255, 150));
        messageItem.setBorder(new EmptyBorder(10, 10, 10, 10));
        messageItem.setMaximumSize(new Dimension(600, 60));

        JLabel messageLabel = new JLabel("<html><b>" + sender + ":</b><br/><p style='width: 200px;'>" + message + "</p></html>");
        messageLabel.setFont(new Font("Arial", Font.PLAIN, 14));

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

    public void clearMessages() {
        messagePanel.removeAll();
        refresh();
    }

    public void clearConversations() {
        conversationListPanel.removeAll();
        conversationButtonMap.clear();
        refresh();
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
