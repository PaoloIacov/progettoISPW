package controller;

import model.domain.Conversation;
import model.domain.Credentials;
import model.domain.Message;
import model.domain.User;
import model.dao.ConversationDAO;
import model.dao.MessageDAO;
import model.dao.ProjectDAO;
import model.dao.UserDAO;
import view.GraphicView.ConversationView;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class ConversationController {
    private final ConversationView conversationView;
    private final ConversationDAO conversationDAO;
    private final MessageDAO messageDAO;
    private final ProjectDAO projectDAO;
    private final Credentials credentials;
    private final Connection connection;
    private Long currentConversationId;

    public ConversationController(ConversationView conversationView, ConversationDAO conversationDAO, MessageDAO messageDAO, ProjectDAO projectDAO, Credentials credentials, Connection connection) {
        this.conversationView = conversationView;
        this.conversationDAO = conversationDAO;
        this.messageDAO = messageDAO;
        this.projectDAO = projectDAO;
        this.credentials = credentials;
        this.connection = connection;

        loadConversationsForProject(conversationView.getSelectedProjectName());
        setupView();
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

    public void setupView() {

        conversationView.setAddConversationButtonListener(e -> setAddConversationDialog());
        conversationView.setDeleteConversationButtonListener(e -> setDeleteConversationDialog());


        int role = credentials.getRole();
        boolean show = (role == 2 || role == 3);
        conversationView.setAddButtonVisible(show);
        conversationView.setDeleteButtonVisible(show);


        conversationView.setAddEmployeeButtonListener(e -> setAddEmployeeDialog(conversationView.getSelectedProjectName(), conversationView.getCurrentConversationID()));
        conversationView.setDeleteEmployeeButtonListener(e -> setDeleteEmployeeDialog(conversationView.getSelectedUsername(), conversationView.getCurrentConversationID()));


        conversationView.setAddEmployeeButtonVisible(show);
        conversationView.setDeleteEmployeeButtonVisible(show);
    }

    public void loadConversationsForUser() {
        try {
            UserDAO userDAO = new UserDAO(connection);
            List<Conversation> conversations = conversationDAO.getConversationsForUser(credentials.getUsername(), credentials.getRole());
            conversationView.clearConversations();
            for (Conversation conversation : conversations) {
                conversationView.addConversationItem(
                        conversation.getConversationId(),
                        conversation.getDescription(),
                        conversation.getProjectName(),
                        e -> selectConversation(conversation.getConversationId()),
                        e -> handleAddEmployee(conversation, userDAO),
                        e -> handleDeleteEmployee(conversation, userDAO)
                );
            }
        } catch (SQLException e) {
            conversationView.showError("Error loading conversations.");
        }
    }

    public void loadConversationsForProject(String projectName) {

            UserDAO userDAO = new UserDAO(connection);
            List<Conversation> conversations = conversationDAO.getConversationsForProject(projectName);

            conversationView.clearConversations();

            for (Conversation conversation : conversations) {
                conversationView.addConversationItem(
                        conversation.getConversationId(),
                        conversation.getDescription(),
                        conversation.getProjectName(),
                        e -> selectConversation(conversation.getConversationId()),
                        e -> handleAddEmployee(conversation, userDAO),
                        e -> handleDeleteEmployee(conversation, userDAO)
                );
            }

            conversationView.refresh();

    }

    private void handleAddEmployee(Conversation conversation, UserDAO userDAO) {
        try {
            List<User> usersFromProject = userDAO.getEmployeesFromProject(conversation.getProjectName());
            List<String> employeeNames = usersFromProject.stream()
                    .map(User::getUsername)
                    .collect(Collectors.toList());

            String selectedUsername = conversationView.showAddEmployeeDialog(employeeNames);
            if (selectedUsername != null) {
                setAddEmployeeDialog(selectedUsername, conversation.getConversationId());
            }
        } catch (SQLException ex) {
            System.out.println(ex);
            conversationView.showError("Error loading employees.");
        }
    }

    private void handleDeleteEmployee(Conversation conversation, UserDAO userDAO) {
        try {
            List<User> usersFromConversation = userDAO.getEmployeesFromConversation(conversation.getConversationId());
            List<String> employeeNames = usersFromConversation.stream()
                    .map(User::getUsername)
                    .collect(Collectors.toList());

            String selectedUsername = conversationView.showDeleteEmployeeDialog(employeeNames);
            if (selectedUsername != null) {
                setDeleteEmployeeDialog(selectedUsername, conversation.getConversationId());
                conversationDAO.removeEmployeeFromConversation(conversation.getConversationId(), selectedUsername);
            }
        } catch (SQLException ex) {
            System.out.println(ex);
            conversationView.showError("Error loading employees.");
        }
    }

    private void selectConversation(Long conversationId) {
        this.currentConversationId = conversationId;
        loadMessages(conversationId);
    }

    private void loadMessages(Long conversationId) {
        try {
            List<Message> messages = messageDAO.getMessagesByConversationId(conversationId);
            conversationView.clearMessages();
            for (Message message : messages) {
                conversationView.addMessageItem(message.getSenderUsername(), message.getContent(), !message.getSenderUsername().equals(credentials.getUsername()));
            }
        } catch (SQLException e) {
            conversationView.showError("Error loading messages.");
        }
    }

    private void sendMessage(String messageContent) {
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

    public void setAddConversationDialog() {
        try {
            String description = conversationView.showAddConversationDialog();
            if (description != null && !description.trim().isEmpty()) {
                String selectedProjectName = conversationView.getSelectedProjectName();

                if (selectedProjectName == null || selectedProjectName.trim().isEmpty()) {
                    conversationView.showError("Please select a valid project.");
                    return;
                }

                if (!conversationDAO.isProjectExisting(selectedProjectName)) {
                    conversationView.showError("Selected project does not exist.");
                    return;
                }

                conversationDAO.addConversation(description, selectedProjectName);
                conversationView.showSuccess("Conversation added successfully.");
                loadConversationsForProject(selectedProjectName);
            } else {
                conversationView.showError("Description cannot be empty.");
            }
        } catch (SQLException e) {
            conversationView.showError("Error adding conversation.");
            e.printStackTrace();
        }
    }

    public void setDeleteConversationDialog() {
        try {
            String selectedProjectName = conversationView.getSelectedProjectName();
            if (selectedProjectName == null || selectedProjectName.trim().isEmpty()) {
                conversationView.showError("Please select a project first.");
                return;
            }

            List<Conversation> conversations = conversationDAO.getConversationsForProject(selectedProjectName);
            List<String> conversationDescriptions = conversations.stream()
                    .map(Conversation::getDescription)
                    .collect(Collectors.toList());

            String selectedDescription = conversationView.showDeleteConversationDialog(conversationDescriptions);
            if (selectedDescription != null) {
                Conversation conversationToDelete = conversations.stream()
                        .filter(conversation -> conversation.getDescription().equals(selectedDescription))
                        .findFirst()
                        .orElse(null);

                if (conversationToDelete != null) {
                    conversationDAO.deleteConversation(conversationToDelete.getConversationId());
                    conversationView.showSuccess("Conversation deleted successfully.");
                    loadConversationsForProject(selectedProjectName);
                }
            }
        } catch (SQLException e) {
            conversationView.showError("Error retrieving or deleting conversation.");
            e.printStackTrace();
        }
    }

    public void setAddEmployeeDialog(String projectName, Long conversationID) {
        try {
            if (conversationID == null) {
                conversationView.showError("Please select a conversation first.");
                return;
            }

            List<User> users = projectDAO.getUsersFromProject(projectName);
            if (users.isEmpty()) {
                conversationView.showError("No employees found for this project.");
                return;
            }

            List<String> employeeNames = users.stream()
                    .map(user -> user.getName() + " " + user.getSurname() + " (" + user.getUsername() + ")")
                    .collect(Collectors.toList());

            String selectedEmployee = conversationView.showAddEmployeeDialog(employeeNames);

            if (selectedEmployee != null) {
                if (conversationDAO.isUserInConversation(conversationID, selectedEmployee)) {
                    conversationView.showError("Employee is already in the conversation.");
                    return;
                }
                String username = selectedEmployee.substring(selectedEmployee.lastIndexOf("(") + 1, selectedEmployee.lastIndexOf(")")).trim();
                conversationDAO.addEmployeeToConversation(conversationID, username);
                conversationView.showSuccess("Employee added successfully to the conversation.");
            } else {
                conversationView.showError("Please select a valid employee.");
            }

        } catch (SQLException e) {
            conversationView.showError("Error retrieving employees for the project.");
            e.printStackTrace();
        }
    }

    public void setDeleteEmployeeDialog(String username, Long conversationId) {
        if (conversationId == null) {
            System.out.println(username + " " + conversationId);
            conversationView.showError("Invalid selection for deletion.");
            return;
        }

        List<User> users = conversationDAO.getEmployeesFromConversation(conversationId);
        List<String> employeeNames = users.stream()
                .map(user -> user.getName() + " " + user.getSurname() + " (" + user.getUsername() + ")")
                .collect(Collectors.toList());

        String selectedEmployee = conversationView.showDeleteEmployeeDialog(employeeNames);
        if (selectedEmployee == null) {
            conversationView.showError("Please select a valid employee.");
            return;
        }
        conversationDAO.removeEmployeeFromConversation(conversationId, username);
        conversationView.showSuccess("Employee removed successfully.");
    }
}
