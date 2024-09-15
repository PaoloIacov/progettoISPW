package controller.ApplicationController;

import controller.CLIController.CLIAdminController;
import controller.CLIController.CLIConversationController;
import controller.CLIController.CLILoginController;
import controller.CLIController.CLIProjectController;
import model.dao.*;
import model.domain.Credentials;
import model.domain.User;
import view.CLIView.CLIAdminView;
import view.CLIView.CLIConversationView;
import view.CLIView.CLILoginView;
import java.sql.Connection;
import java.util.Stack;

import view.CLIView.CLIProjectView;
import view.View;

public class CLIApplicationController implements ApplicationController {
    private final Connection connection;
    private final Stack<View> viewStack = new Stack<>(); // Stack per la navigazione
    Credentials loginCredentials;

    public CLIApplicationController(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void start() {
        CLILoginView loginView = new CLILoginView();
        LoginDAO loginDAO = new LoginDAO(connection);
        CLILoginController loginController = new CLILoginController(loginView, loginDAO, this);
        loginController.start();
        viewStack.push(loginView);
    }

    @Override
    public void onLoginSuccess(Credentials credentials) {
        int role = credentials.getRole();
        loginCredentials = credentials;
        switch (role) {
            case 1 -> openConversationView(credentials); // Employee
            case 2 -> openProjectView(credentials);      // Project Manager
            case 3 -> openAdminView();                   // Admin
            default -> System.out.println("Role not supported.");
        }
    }

    @Override
    public void openProjectView(Credentials credentials) {
        CLIProjectView projectView = new CLIProjectView(this);
        ProjectDAO projectDAO = new ProjectDAO(connection);
        UserDAO userDAO = new UserDAO(connection);
        CLIProjectController projectController = new CLIProjectController(projectView, projectDAO, userDAO, credentials, connection);
        projectController.manageProjects();

    }

    @Override
    public void openProjectViewForAdmin() {
        CLIProjectView projectView = new CLIProjectView(this);
        ProjectDAO projectDAO = new ProjectDAO(connection);
        UserDAO userDAO = new UserDAO(connection);
        CLIProjectController projectController = new CLIProjectController(projectView, projectDAO, userDAO, loginCredentials, connection);
        projectController.getAllProject();
    }

    @Override
    public void openConversationView(Credentials credentials) {
        CLIConversationView conversationView = new CLIConversationView(this);
        ConversationDAO conversationDAO = new ConversationDAO(connection);
        MessageDAO messageDAO = new MessageDAO(connection);
        CLIConversationController conversationController = new CLIConversationController(conversationView, conversationDAO, messageDAO, credentials);
        conversationController.loadConversationsForUser();
    }

    @Override
    public void openConversationViewForProject(String projectName) {
        CLIConversationView conversationView = new CLIConversationView(this);
        ConversationDAO conversationDAO = new ConversationDAO(connection);
        MessageDAO messageDAO = new MessageDAO(connection);
        CLIConversationController conversationController = new CLIConversationController(conversationView, conversationDAO, messageDAO, loginCredentials);
        System.out.println("a01" + projectName);
        conversationController.loadConversationsForProject(projectName);
    }

    @Override
    public void openAdminView() {
        CLIAdminView adminView = new CLIAdminView(this);
        UserDAO userDAO = new UserDAO(connection);
        ProjectDAO projectDAO = new ProjectDAO(connection);
        CLIAdminController adminController = new CLIAdminController(adminView, userDAO, projectDAO);
        adminController.displayAdminMenu();

    }

    @Override
    public void back() {
        if (viewStack.size() > 1) {
            View currentView = viewStack.pop();
            currentView.close();
            View previousView = viewStack.peek();
            previousView.display();
        }
    }
}
