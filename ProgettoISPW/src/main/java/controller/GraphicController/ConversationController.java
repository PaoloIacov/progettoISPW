package controller.GraphicController;

import model.domain.Conversation;
import model.domain.Credentials;
import model.domain.Message;
import model.domain.User;
import model.dao.ConversationDAO;
import model.dao.MessageDAO;
import model.dao.ProjectDAO;
import model.dao.UserDAO;
import view.GraphicView.GraphicConversationView;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class ConversationController {
    private final GraphicConversationView graphicConversationView;
    private final ConversationDAO conversationDAO;
    private final MessageDAO messageDAO;
    private final ProjectDAO projectDAO;
    private final Credentials credentials;
    private final Connection connection;
    private Long currentConversationId;

    public ConversationController(GraphicConversationView graphicConversationView, ConversationDAO conversationDAO, MessageDAO messageDAO, ProjectDAO projectDAO, Credentials credentials, Connection connection) {
        this.graphicConversationView = graphicConversationView;
        this.conversationDAO = conversationDAO;
        this.messageDAO = messageDAO;
        this.projectDAO = projectDAO;
        this.credentials = credentials;
        this.connection = connection;

        loadConversationsForProject(graphicConversationView.getSelectedProjectName());
        setupView();
        setupSendButtonListener();
    }

    private void setupSendButtonListener() {
        graphicConversationView.setSendButtonListener(e -> {
            String messageContent = graphicConversationView.getMessageInput();
            if (!messageContent.trim().isEmpty()) {
                sendMessage(messageContent);
                graphicConversationView.resetMessageInput();
            } else {
                graphicConversationView.showError("Message cannot be empty.");
            }
        });
    }

    public void setupView() {

        graphicConversationView.setAddConversationButtonListener(e -> setAddConversationDialog());
        graphicConversationView.setDeleteConversationButtonListener(e -> setDeleteConversationDialog());


        int role = credentials.getRole();
        boolean show = (role == 2 || role == 3);
        graphicConversationView.setAddButtonVisible(show);
        graphicConversationView.setDeleteButtonVisible(show);


        graphicConversationView.setAddEmployeeButtonListener(e -> setAddEmployeeDialog(graphicConversationView.getSelectedProjectName(), graphicConversationView.getCurrentConversationID()));
        graphicConversationView.setDeleteEmployeeButtonListener(e -> setDeleteEmployeeDialog(graphicConversationView.getSelectedUsername(), graphicConversationView.getCurrentConversationID()));


        graphicConversationView.setAddEmployeeButtonVisible(show);
        graphicConversationView.setDeleteEmployeeButtonVisible(show);
    }

    public void loadConversationsForUser() {
        try {
            UserDAO userDAO = new UserDAO(connection);
            List<Conversation> conversations = conversationDAO.getConversationsForUser(credentials.getUsername(), credentials.getRole());
            graphicConversationView.clearConversations();
            for (Conversation conversation : conversations) {
                graphicConversationView.addConversationItem(
                        conversation.getConversationId(),
                        conversation.getDescription(),
                        conversation.getProjectName(),
                        e -> selectConversation(conversation.getConversationId()),
                        e -> handleAddEmployee(conversation, userDAO),
                        e -> handleDeleteEmployee(conversation, userDAO)
                );
            }
        } catch (SQLException e) {
            graphicConversationView.showError("Error loading conversations.");
        }
    }

    public void loadConversationsForProject(String projectName) {

            UserDAO userDAO = new UserDAO(connection);
            List<Conversation> conversations = conversationDAO.getConversationsForProject(projectName);

            graphicConversationView.clearConversations();

            for (Conversation conversation : conversations) {
                graphicConversationView.addConversationItem(
                        conversation.getConversationId(),
                        conversation.getDescription(),
                        conversation.getProjectName(),
                        e -> selectConversation(conversation.getConversationId()),
                        e -> handleAddEmployee(conversation, userDAO),
                        e -> handleDeleteEmployee(conversation, userDAO)
                );
            }

            graphicConversationView.refresh();

    }

    private void handleAddEmployee(Conversation conversation, UserDAO userDAO) {
        try {
            List<User> usersFromProject = userDAO.getEmployeesFromProject(conversation.getProjectName());
            List<String> employeeNames = usersFromProject.stream()
                    .map(User::getUsername)
                    .collect(Collectors.toList());

            String selectedUsername = graphicConversationView.showAddEmployeeDialog(employeeNames);
            if (selectedUsername != null) {
                setAddEmployeeDialog(selectedUsername, conversation.getConversationId());
            }
        } catch (SQLException ex) {
            System.out.println(ex);
            graphicConversationView.showError("Error loading employees.");
        }
    }

    private void handleDeleteEmployee(Conversation conversation, UserDAO userDAO) {
        try {
            List<User> usersFromConversation = userDAO.getEmployeesFromConversation(conversation.getConversationId());
            List<String> employeeNames = usersFromConversation.stream()
                    .map(User::getUsername)
                    .collect(Collectors.toList());

            String selectedUsername = graphicConversationView.showDeleteEmployeeDialog(employeeNames);
            if (selectedUsername != null) {
                setDeleteEmployeeDialog(selectedUsername, conversation.getConversationId());
                conversationDAO.removeEmployeeFromConversation(conversation.getConversationId(), selectedUsername);
            }
        } catch (SQLException ex) {
            System.out.println(ex);
            graphicConversationView.showError("Error loading employees.");
        }
    }

    private void selectConversation(Long conversationId) {
        this.currentConversationId = conversationId;
        loadMessages(conversationId);
    }

    private void loadMessages(Long conversationId) {
        try {
            List<Message> messages = messageDAO.getMessagesByConversationId(conversationId);
            graphicConversationView.clearMessages();
            for (Message message : messages) {
                graphicConversationView.addMessageItem(message.getSenderUsername(), message.getContent(), !message.getSenderUsername().equals(credentials.getUsername()));
            }
        } catch (SQLException e) {
            graphicConversationView.showError("Error loading messages.");
        }
    }

    private void sendMessage(String messageContent) {
        if (currentConversationId == null) {
            graphicConversationView.showError("Please select a conversation first.");
            return;
        }

        try {
            messageDAO.addMessage(currentConversationId, credentials.getUsername(), messageContent);
            graphicConversationView.addMessageItem(credentials.getUsername(), messageContent, false);
        } catch (SQLException e) {
            graphicConversationView.showError("Error sending message. Please try again.");
        }
    }

    public void setAddConversationDialog() {
        try {
            String description = graphicConversationView.showAddConversationDialog();
            if (description != null && !description.trim().isEmpty()) {
                String selectedProjectName = graphicConversationView.getSelectedProjectName();

                if (selectedProjectName == null || selectedProjectName.trim().isEmpty()) {
                    graphicConversationView.showError("Please select a valid project.");
                    return;
                }

                if (!conversationDAO.isProjectExisting(selectedProjectName)) {
                    graphicConversationView.showError("Selected project does not exist.");
                    return;
                }

                conversationDAO.addConversation(description, selectedProjectName);
                graphicConversationView.showSuccess("Conversation added successfully.");
                loadConversationsForProject(selectedProjectName);
            } else {
                graphicConversationView.showError("Description cannot be empty.");
            }
        } catch (SQLException e) {
            graphicConversationView.showError("Error adding conversation.");
            e.printStackTrace();
        }
    }

    public void setDeleteConversationDialog() {
        try {
            String selectedProjectName = graphicConversationView.getSelectedProjectName();
            if (selectedProjectName == null || selectedProjectName.trim().isEmpty()) {
                graphicConversationView.showError("Please select a project first.");
                return;
            }

            List<Conversation> conversations = conversationDAO.getConversationsForProject(selectedProjectName);
            List<String> conversationDescriptions = conversations.stream()
                    .map(Conversation::getDescription)
                    .collect(Collectors.toList());

            String selectedDescription = graphicConversationView.showDeleteConversationDialog(conversationDescriptions);
            if (selectedDescription != null) {
                Conversation conversationToDelete = conversations.stream()
                        .filter(conversation -> conversation.getDescription().equals(selectedDescription))
                        .findFirst()
                        .orElse(null);

                if (conversationToDelete != null) {
                    conversationDAO.deleteConversation(conversationToDelete.getConversationId());
                    graphicConversationView.showSuccess("Conversation deleted successfully.");
                    loadConversationsForProject(selectedProjectName);
                }
            }
        } catch (SQLException e) {
            graphicConversationView.showError("Error retrieving or deleting conversation.");
            e.printStackTrace();
        }
    }

    public void setAddEmployeeDialog(String projectName, Long conversationID) {
        try {
            if (conversationID == null) {
                graphicConversationView.showError("Please select a conversation first.");
                return;
            }

            List<User> users = projectDAO.getUsersFromProject(projectName);
            if (users.isEmpty()) {
                graphicConversationView.showError("No employees found for this project.");
                return;
            }

            List<String> employeeNames = users.stream()
                    .map(user -> user.getName() + " " + user.getSurname() + " (" + user.getUsername() + ")")
                    .collect(Collectors.toList());

            String selectedEmployee = graphicConversationView.showAddEmployeeDialog(employeeNames);

            if (selectedEmployee != null) {
                if (conversationDAO.isUserInConversation(conversationID, selectedEmployee)) {
                    graphicConversationView.showError("Employee is already in the conversation.");
                    return;
                }
                String username = selectedEmployee.substring(selectedEmployee.lastIndexOf("(") + 1, selectedEmployee.lastIndexOf(")")).trim();
                conversationDAO.addEmployeeToConversation(conversationID, username);
                graphicConversationView.showSuccess("Employee added successfully to the conversation.");
            } else {
                graphicConversationView.showError("Please select a valid employee.");
            }

        } catch (SQLException e) {
            graphicConversationView.showError("Error retrieving employees for the project.");
            e.printStackTrace();
        }
    }

    public void setDeleteEmployeeDialog(String username, Long conversationId) {
        if (conversationId == null) {
            System.out.println(username + " " + conversationId);
            graphicConversationView.showError("Invalid selection for deletion.");
            return;
        }

        List<User> users = conversationDAO.getEmployeesFromConversation(conversationId);
        List<String> employeeNames = users.stream()
                .map(user -> user.getName() + " " + user.getSurname() + " (" + user.getUsername() + ")")
                .collect(Collectors.toList());

        String selectedEmployee = graphicConversationView.showDeleteEmployeeDialog(employeeNames);
        if (selectedEmployee == null) {
            graphicConversationView.showError("Please select a valid employee.");
            return;
        }
        conversationDAO.removeEmployeeFromConversation(conversationId, username);
        graphicConversationView.showSuccess("Employee removed successfully.");
    }
}
