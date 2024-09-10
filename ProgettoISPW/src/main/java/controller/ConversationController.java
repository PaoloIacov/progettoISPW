package controller;

import model.bean.CredentialsBean;
import model.bean.MessageBean;
import model.dao.ConversationDAO;
import model.dao.MessageDAO;
import view.ConversationView;

import java.sql.SQLException;
import java.util.List;

public class ConversationController {

    private final ConversationView conversationView;
    private final ConversationDAO conversationDAO;
    private final MessageDAO messageDAO;
    private final CredentialsBean credentials;
    private Long currentConversationId;

    public ConversationController(ConversationView conversationView, ConversationDAO conversationDAO, MessageDAO messageDAO, CredentialsBean credentials) {
        this.conversationView = conversationView;
        this.conversationDAO = conversationDAO;
        this.messageDAO = messageDAO;
        this.credentials = credentials;

        loadConversations();
        setupSendButtonListener();
    }

    private void setupSendButtonListener() {
        conversationView.setSendButtonListener(e -> {
            String messageContent = conversationView.getMessageInput();
            if (!messageContent.trim().isEmpty()) {
                sendMessage(messageContent);
                conversationView.resetMessageInput();
            } else {
                conversationView.showError("Message cannot be empty.");
            }
        });
    }

    public void loadConversations() {
        try {
            List<String[]> conversations = conversationDAO.getConversations(credentials.getUsername(), credentials.getRole());
            conversationView.clearConversations();
            for (String[] conversation : conversations) {
                String id = conversation[0];
                String title = conversation[1];
                String projectName = conversation[2];
                conversationView.addConversationItem(id, title, projectName, e -> selectConversation(Long.parseLong(id)));
            }
        } catch (SQLException e) {
            conversationView.showError("Error loading conversations.");
        }
    }

    private void selectConversation(Long conversationId) {
        this.currentConversationId = conversationId;
        loadMessages(conversationId);
    }

    public void loadMessages(Long conversationId) {
        try {
            List<MessageBean> messages = messageDAO.getMessagesByConversationId(conversationId);
            conversationView.clearMessages();
            for (MessageBean message : messages) {
                conversationView.addMessageItem(message.getSenderUsername(), message.getContent(), !message.getSenderUsername().equals(credentials.getUsername()));
            }
        } catch (SQLException e) {
            conversationView.showError("Error loading messages.");
        }
    }

    public void sendMessage(String messageContent) {
        if (currentConversationId == null) {
            conversationView.showError("Please select a conversation first.");
            return;
        }

        try {
            messageDAO.addMessage(currentConversationId, credentials.getUsername(), messageContent);
            conversationView.addMessageItem(credentials.getUsername(), messageContent, false);
        } catch (SQLException e) {
            conversationView.showError("Error sending message. Please try again.");
        }
    }
}
