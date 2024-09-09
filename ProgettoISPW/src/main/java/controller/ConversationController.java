package controller;

import model.bean.CredentialsBean;
import model.dao.ConversationDAO;
import model.dao.MessageDAO;
import view.ConversationView;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.List;

public class ConversationController {

    private final ConversationView conversationView;
    private final ConversationDAO conversationDAO;
    private final MessageDAO messageDAO;
    private final CredentialsBean credentials; // Aggiungi le credenziali dell'utente

    // Modifica del costruttore per accettare 3 parametri
    public ConversationController(ConversationView conversationView, ConversationDAO conversationDAO, MessageDAO messageDAO, CredentialsBean credentials) {
        this.conversationView = conversationView;
        this.conversationDAO = conversationDAO;
        this.messageDAO = messageDAO;
        this.credentials = credentials; // Memorizza le credenziali dell'utente

        // Carica le conversazioni esistenti e aggiorna la vista
        loadConversations();

        // Aggiungi il listener per il pulsante di invio
        this.conversationView.addSendButtonListener(new SendButtonListener());
    }

    // Metodo per caricare le conversazioni dal database e aggiornare la vista
    private void loadConversations() {
        try {
            // Ottieni lo username e il ruolo dall'oggetto CredentialsBean
            String username = credentials.getUsername();
            int role = credentials.getRole();

            // Log per debug
            System.out.println("Loading conversations for user: " + username + " with role: " + role);

            // Ottieni le conversazioni chiamando il metodo getConversations del DAO
            List<String[]> conversations = conversationDAO.getConversations(username, role);

            // Log del numero di conversazioni caricate
            System.out.println("Number of conversations loaded: " + conversations.size());

            // Aggiungi le conversazioni alla vista
            for (String[] conversation : conversations) {
                String id = conversation[0];  // ID della conversazione
                String title = conversation[1];  // Assumendo che la descrizione della conversazione sia il titolo da mostrare
                String projectName = conversation[2];

                // Log per ogni conversazione aggiunta alla vista
                System.out.println("Adding conversation to view: ID=" + id + ", Title=" + title + ", ProjectName=" + projectName);

                conversationView.addConversationItem(id, title, projectName);  // Aggiungi l'ID della conversazione per la selezione
            }

            // Se nessuna conversazione viene caricata, mostra un messaggio
            if (conversations.isEmpty()) {
                System.out.println("No conversations available for user: " + username);
                conversationView.showError("No conversations found.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            conversationView.showError("Error loading conversations.");
            System.err.println("SQLException occurred while loading conversations: " + e.getMessage());
        }
    }



    // Listener per il pulsante di invio
    private class SendButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String message = conversationView.getMessageInput();
            Long conversationId = conversationView.getCurrentConversationId();  // Ottieni l'ID della conversazione corrente
            String senderUsername = credentials.getUsername();  // Usa lo username dell'utente corrente

            if (!message.isEmpty()) {
                try {
                    // Aggiungi il messaggio al database con i 3 parametri richiesti
                    messageDAO.addMessage(conversationId, senderUsername, message);
                    conversationView.addMessageItem(senderUsername, message, false);
                    conversationView.resetMessageInput();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    conversationView.showError("Error sending message.");
                }
            }
        }
    }

    // Metodo per caricare i messaggi della conversazione selezionata
    public void loadMessages(Long conversationId) {
        try {
            // Pulisci il pannello dei messaggi prima di caricarne di nuovi
            conversationView.clearMessages();

            List<String[]> messages = messageDAO.getMessagesByConversationId(conversationId);
            for (String[] message : messages) {
                String sender = message[2];
                String content = message[3];
                boolean isReceived = !sender.equals(credentials.getUsername()); // Determina se è un messaggio ricevuto o inviato
                conversationView.addMessageItem(sender, content, isReceived);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            conversationView.showError("Error loading messages.");
        }
    }
}
