package controller;

import model.bean.CredentialsBean;
import model.dao.ConnectionFactory;
import model.dao.ConversationDAO;
import model.dao.MessageDAO;
import model.dao.ProjectDAO;
import view.ConversationView;
import view.ProjectView;
import view.LoginView;
import model.dao.LoginDAO;
import controller.exceptions.DatabaseConnectionException;
import view.View;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Stack;

public class ApplicationController {

    private final Connection connection;
    private final Stack<View> viewStack = new Stack<>();  // Stack per la navigazione

    public ApplicationController() {
        try {
            this.connection = ConnectionFactory.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DatabaseConnectionException("Error connecting to the database.", e);
        }
    }

    public void start() {
        // Inizializza e mostra la schermata di login
        LoginView loginView = new LoginView(this);
        LoginDAO loginDAO = new LoginDAO(connection);
        new LoginController(loginView, loginDAO, this);  // Passa l'ApplicationController
        loginView.display();
        viewStack.push(loginView);
    }

    public void onLoginSuccess(CredentialsBean credentials) {
        int role = credentials.getRole();
        switch (role) {
            case 1:
                openConversationView(credentials); // Dipendente
                break;
            case 2, 3:
                openProjectView(credentials); // Project Manager
                break;
            default:
                showError("Role not supported.");
                System.out.println("Role not supported.");
                break;
        }
    }

    public void openConversationView(CredentialsBean credentials) {
        ConversationView conversationView = new ConversationView(this);
        ConversationDAO conversationDAO = new ConversationDAO(connection);
        MessageDAO messageDAO = new MessageDAO(connection);
        new ConversationController(conversationView, conversationDAO, messageDAO, credentials);
        conversationView.display();
        viewStack.push(conversationView);
    }

    public void openProjectView(CredentialsBean credentials) {
        ProjectView projectView = new ProjectView(this);
        ProjectDAO projectDAO = new ProjectDAO(connection);
        new ProjectController(projectView, projectDAO, credentials, this);
        projectView.display();
        viewStack.push(projectView);
    }

    // Metodo per gestire il pulsante "Back"
    public void back() {
        if (viewStack.size() > 1) {
            View currentView = viewStack.pop();
            currentView.close();

            View previousView = viewStack.peek();
            previousView.display();
        }
    }

    public void showError(String message) {
        viewStack.peek().showError(message);
    }
}
