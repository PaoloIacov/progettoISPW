package controller.CLIController;

import model.domain.Conversation;
import model.domain.Credentials;
import model.domain.Message;
import model.dao.ConversationDAO;
import model.dao.MessageDAO;
import view.CLIView.CLIConversationView;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class CLIConversationController {

    private final CLIConversationView conversationView;
    private final ConversationDAO conversationDAO;
    private final MessageDAO messageDAO;
    private final Credentials credentials;

    public CLIConversationController(CLIConversationView conversationView, ConversationDAO conversationDAO, MessageDAO messageDAO, Credentials credentials) {
        this.conversationView = conversationView;
        this.conversationDAO = conversationDAO;
        this.messageDAO = messageDAO;
        this.credentials = credentials;

        int role = credentials.getRole();
        switch (role) {
            case 1 -> loadConversationsForUser(); // Employee
            case 2, 3 -> manageConversationsForManagers(); // Project Manager or Admin
            default -> System.out.println("Role not supported.");
        }
    }

    // Load conversations for the user
    public void loadConversationsForUser() {
        try {
            List<Conversation> conversations = conversationDAO.getConversationsForUser(credentials.getUsername(), credentials.getRole());
            conversationView.showConversations(conversations);

            int role = credentials.getRole();
            Long action = conversationView.chooseAction(credentials.getRole());
            switch (role) {
                case 1 -> handleEmployeeAction(action);
                case 2, 3 -> handleManagerAction(action);
                default -> conversationView.showError("Role not supported.");
            }


        } catch (SQLException | IOException e) {
            conversationView.showError("Error loading conversations.");
            e.printStackTrace();
        }
    }

    // Load conversations for a project (for Project Managers and Admins)
    public void loadConversationsForProject(String projectName) {
        try {
            List<Conversation> conversations = conversationDAO.getConversationsForProject(projectName);
            conversationView.showConversations(conversations);

            Long action = conversationView.chooseAction(credentials.getRole());

            switch (action.intValue()) {
                case 1 -> { // View conversation messages
                    Long selectedConversationId = conversationView.getSelectedConversationId();
                    loadMessagesForConversation(selectedConversationId);
                }
                case 2 -> { // Go back
                    conversationView.showSuccess("Going back....");
                    conversationView.back();
                }
                case 3 -> { // Quit
                    conversationView.showSuccess("You chose to quit.");
                    System.exit(0);
                }
                default -> conversationView.showError("Action invalid.");
            }

        } catch (IOException e) {
            conversationView.showError("Error loading conversations.");
            e.printStackTrace();
        }
    }

    // Load messages for a specific conversation
    private void loadMessagesForConversation(Long conversationId) {
        try {
            List<Message> messages = messageDAO.getMessagesByConversationId(conversationId);
            conversationView.showConversationMessages(messages);

            handleUserChoice(conversationId);

        } catch (SQLException | IOException e) {
            conversationView.showError("Error loading messages.");
            e.printStackTrace();
        }
    }

    private void handleUserChoice(Long conversationId) throws IOException {
        int choice = conversationView.showMessageOptionsMenu();
        switch (choice) {
            case 1 -> sendMessage(conversationId);  // Option to send a message
            case 2 -> loadConversationsForUser(); // Option to go back to conversation list
            case 3 -> System.exit(0);  // Option to quit
            default -> {
                conversationView.showError("Invalid option. Please try again.");
                handleUserChoice(conversationId);
            }
        }
    }

    private void sendMessage(Long conversationId) {
        try {
            String messageContent = conversationView.getMessageContentInput();
            messageDAO.addMessage(conversationId, credentials.getUsername(), messageContent);
            conversationView.showConversationMessages(messageDAO.getMessagesByConversationId(conversationId));  // Reload messages after sending
            handleUserChoice(conversationId);  // Return to options menu after sending a message
        } catch (SQLException | IOException e) {
            conversationView.showError("Error sending the message.");
            e.printStackTrace();
        }
    }

    // Manage conversations for Project Managers and Admins
    private void manageConversationsForManagers() {
        loadConversationsForUser(); // Initially load conversations
        showManagerOptions(); // Show additional options for managing conversations
    }

    // Display additional options for Project Managers and Admins
    private void showManagerOptions() {
        try {
            System.out.println("Additional Options:");
            System.out.println("1. Add Conversation");
            System.out.println("2. Delete Conversation");
            System.out.println("3. Add User to Conversation");
            System.out.println("4. Remove User from Conversation");
            System.out.println("5. Exit");

            int choice = Integer.parseInt(conversationView.getUserInput("Choose an option: "));

            switch (choice) {
                case 1 -> addConversation();
                case 2 -> deleteConversation();
                case 3 -> addUserToConversation();
                case 4 -> removeUserFromConversation();
                case 5 -> System.exit(0);
                default -> {
                    conversationView.showError("Invalid choice.");
                    showManagerOptions(); // Show the options again
                }
            }
        } catch (IOException e) {
            conversationView.showError("Error reading input.");
        }
    }

    public int handleEmployeeAction(Long action) {
        switch (action.intValue()) {
            case 1 -> loadConversationsForUser();
            case 2 -> System.exit(0);
            default -> conversationView.showError("Invalid option selected.");
        }
        return 0;
    }

    public int handleManagerAction(Long action) throws IOException {
        switch (action.intValue()) {
            case 1 -> loadConversationsForProject(conversationView.getProjectNameForNewConversation());
            case 2 -> addConversation();
            case 3 -> deleteConversation();
            case 4 -> addUserToConversation();
            case 5 -> removeUserFromConversation();
            case 6 -> System.exit(0);
            default -> conversationView.showError("Invalid option selected.");
        }
        return 0;
    }

    private void addConversation() throws IOException {
        // Ask user for conversation description and project name
        String description = conversationView.getNewConversationDescription();
        String projectName = conversationView.getProjectNameForNewConversation();

        try {
            // Add conversation using DAO with both description and projectName
            conversationDAO.addConversation(description, projectName);
            conversationView.showSuccess("Conversation added successfully.");
            loadConversationsForUser(); // Reload conversations
        } catch (SQLException e) {
            conversationView.showError("Error adding conversation.");
        }
    }

    private void deleteConversation() throws IOException {
        Long conversationId = conversationView.getConversationIdToDelete();
        try {
            conversationDAO.deleteConversation(conversationId);
            conversationView.showSuccess("Conversation deleted successfully.");
            loadConversationsForUser(); // Reload conversations
        } catch (SQLException e) {
            conversationView.showError("Error deleting conversation.");
        }
    }

    private void addUserToConversation() throws IOException {
        String[] details = conversationView.getUserDetailsToAddToConversation();
        Long conversationId = Long.parseLong(details[0]);
        String username = details[1];
        try {
            conversationDAO.addEmployeeToConversation(conversationId, username);
            conversationView.showSuccess("User added to conversation successfully.");
        } catch (SQLException e) {
            conversationView.showError("Error adding user to conversation.");
        }
    }

    private void removeUserFromConversation() throws IOException {
        String[] details = conversationView.getUserDetailsToRemoveFromConversation();
        Long conversationId = Long.parseLong(details[0]);
        String username = details[1];
            conversationDAO.removeEmployeeFromConversation(conversationId, username);
            conversationView.showSuccess("User removed from conversation successfully.");

    }
}
