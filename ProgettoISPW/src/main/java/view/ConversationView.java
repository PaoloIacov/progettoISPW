package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

public class ConversationView extends JFrame implements View {

    private JPanel conversationListPanel;
    private BackgroundPanel messagePanel;  // Usa BackgroundPanel invece di JPanel
    private JTextField messageInputField;
    private JButton sendButton;

    // Attributo per memorizzare l'ID della conversazione corrente
    private Long currentConversationId;

    // Mappa per tracciare i pulsanti di conversazione e i loro ID
    private Map<JButton, Long> conversationButtonMap = new HashMap<>();

    public ConversationView() {
        // Impostazioni della finestra principale
        setTitle("Conversation View");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);  // Centra la finestra

        // Layout principale
        setLayout(new BorderLayout());

        // Pannello sinistro: Lista delle conversazioni
        JPanel leftPanel = new JPanel();
        leftPanel.setBackground(new Color(30, 33, 40)); // Colore di sfondo scuro
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setPreferredSize(new Dimension(300, 600));
        leftPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Lista di conversazioni
        conversationListPanel = new JPanel();
        conversationListPanel.setLayout(new BoxLayout(conversationListPanel, BoxLayout.Y_AXIS));
        conversationListPanel.setBackground(new Color(30, 33, 40));

        // Scroll per il pannello delle conversazioni
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
        messagePanel = new BackgroundPanel("background.jpg");  // Sostituisci JPanel con BackgroundPanel
        messagePanel.setLayout(new BoxLayout(messagePanel, BoxLayout.Y_AXIS));

        // Scroll per il pannello dei messaggi
        JScrollPane messageScrollPane = new JScrollPane(messagePanel);
        messageScrollPane.setBorder(null);
        rightPanel.add(messageScrollPane, BorderLayout.CENTER);

        // Pannello inferiore per l'inserimento del messaggio
        JPanel messageInputPanel = new JPanel();
        messageInputPanel.setLayout(new BorderLayout());
        messageInputPanel.setBackground(Color.WHITE);
        messageInputPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        messageInputField = new JTextField("New message");
        sendButton = new JButton("Send");

        messageInputPanel.add(messageInputField, BorderLayout.CENTER);
        messageInputPanel.add(sendButton, BorderLayout.EAST);

        rightPanel.add(messageInputPanel, BorderLayout.SOUTH);

        // Aggiunta dei pannelli alla finestra
        add(leftPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.CENTER);

        // Visualizza la finestra
        setVisible(true);
    }

    // Classe interna per gestire il pannello con immagine di sfondo
    class BackgroundPanel extends JPanel {
        private Image backgroundImage;

        public BackgroundPanel(String imagePath) {
            // Carica l'immagine di sfondo
            ImageIcon icon = createImageIcon(imagePath);
            if (icon != null) {
                this.backgroundImage = icon.getImage();
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (backgroundImage != null) {
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        }
    }

    // Metodo per caricare un'immagine dal classpath
    private ImageIcon createImageIcon(String path) {
        java.net.URL imgURL = getClass().getClassLoader().getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }

    // Metodo per aggiungere una conversazione alla lista delle conversazioni
    public void addConversationItem(String id, String title, String projectName) {
        JPanel conversationItem = new JPanel();
        conversationItem.setLayout(new BoxLayout(conversationItem, BoxLayout.X_AXIS));
        conversationItem.setBackground(new Color(42, 46, 54));
        conversationItem.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        conversationItem.setBorder(new EmptyBorder(5, 5, 5, 5));

        JLabel titleLabel = new JLabel("<html><b>" + title + "</b><br/><small>" + projectName + "</small></html>");
        titleLabel.setForeground(Color.WHITE);
        JButton selectButton = new JButton("Select");
        selectButton.setBackground(Color.LIGHT_GRAY);

        // Mappa il pulsante all'ID della conversazione
        Long conversationId = Long.parseLong(id);
        conversationButtonMap.put(selectButton, conversationId);

        // Aggiungi listener per selezionare la conversazione
        selectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                currentConversationId = conversationButtonMap.get(selectButton);  // Aggiorna l'ID corrente
                System.out.println("Selected Conversation ID: " + currentConversationId);
                // Carica i messaggi della conversazione selezionata (chiama il controller)
                // conversationController.loadMessages(currentConversationId);
            }
        });

        conversationItem.add(titleLabel);
        conversationItem.add(Box.createHorizontalGlue());
        conversationItem.add(selectButton);

        // Aggiunge l'elemento della conversazione al pannello della lista
        conversationListPanel.add(conversationItem);
        conversationListPanel.add(Box.createRigidArea(new Dimension(0, 5))); // Spaziatura

        // Aggiorna la GUI
        conversationListPanel.revalidate();
        conversationListPanel.repaint();
    }


    // Metodo per ottenere l'ID della conversazione corrente
    public Long getCurrentConversationId() {
        return currentConversationId;
    }

    // Metodo per aggiungere un messaggio al pannello della conversazione
    public void addMessageItem(String sender, String message, boolean isReceived) {
        JPanel messageItem = new JPanel();
        messageItem.setLayout(new BoxLayout(messageItem, BoxLayout.Y_AXIS));
        messageItem.setBackground(new Color(255, 255, 255, 150));  // Sfondo semi-trasparente per contrasto
        messageItem.setBorder(new EmptyBorder(10, 10, 10, 10));
        messageItem.setMaximumSize(new Dimension(600, 60));

        JLabel messageLabel = new JLabel("<html><p style='width: 150px;'>" + message + "</p></html>");
        messageLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        if (isReceived) {
            messageLabel.setForeground(Color.BLACK);
            messageItem.setAlignmentX(Component.LEFT_ALIGNMENT);
        } else {
            messageLabel.setForeground(Color.BLUE);
            messageItem.setAlignmentX(Component.RIGHT_ALIGNMENT);
        }

        messageItem.add(messageLabel);
        messagePanel.add(messageItem);
        messagePanel.add(Box.createRigidArea(new Dimension(0, 10))); // Spaziatura

        refresh();
    }

    // Metodo per ottenere il testo del messaggio inserito
    public String getMessageInput() {
        return messageInputField.getText();
    }

    // Metodo per resettare il campo di input del messaggio
    public void resetMessageInput() {
        messageInputField.setText("");
    }

    // Metodo per aggiungere un listener al pulsante di invio
    public void addSendButtonListener(ActionListener listener) {
        sendButton.addActionListener(listener);
    }

    // Metodo per mostrare un messaggio di errore
    public void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    // Metodo per cancellare tutti i messaggi dal pannello della conversazione
    public void clearMessages() {
        messagePanel.removeAll(); // Rimuove tutti i componenti dal pannello dei messaggi
        refresh(); // Rinfresca la vista per aggiornare il pannello vuoto
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
}
