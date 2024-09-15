package controller;

import model.domain.Credentials;
import model.dao.*;
import view.GraphicView.ConversationView;
import view.GraphicView.ProjectView;
import view.GraphicView.LoginView;
import view.GraphicView.AdminView;
import view.View;

import java.sql.Connection;
import java.util.Stack;

public class ApplicationController {
    private final Connection connection;
    private final Stack<View> viewStack = new Stack<>(); // Stack per la navigazione
    Credentials loginCredentials;
    public ApplicationController(Connection connection) {
        this.connection = connection;
    }

    public void start() {
        LoginView loginView = new LoginView(this);
        LoginDAO loginDAO = new LoginDAO(connection);
        new LoginController(loginView, loginDAO, this);
        loginView.display();
        viewStack.push(loginView);
    }

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


    public void openConversationView(Credentials credentials) {
        ConversationView conversationView = new ConversationView(this);
        ConversationDAO conversationDAO = new ConversationDAO(connection);
        MessageDAO messageDAO = new MessageDAO(connection);
        ProjectDAO projectDAO = new ProjectDAO(connection);
        ConversationController conversationController = new ConversationController(conversationView, conversationDAO, messageDAO, projectDAO, credentials, connection);
        conversationController.loadConversationsForUser();
        conversationController.setupView();
        conversationView.display();
        viewStack.push(conversationView);
    }

    public void openProjectView(Credentials credentials) {
        ProjectView projectView = new ProjectView(this);
        ProjectDAO projectDAO = new ProjectDAO(connection);
        UserDAO userDAO = new UserDAO(connection);
        ProjectController projectController = new ProjectController(projectView, projectDAO, userDAO, credentials, connection);
        projectView.display();
        viewStack.push(projectView);
    }


    // Dentro ApplicationController:

    public void openConversationViewForProject(String projectName) {
        ConversationView conversationView = new ConversationView(this);
        if (projectName == null || projectName.trim().isEmpty()) {
            conversationView.showError("Project name is invalid.");
            return;
        }
        ConversationDAO conversationDAO = new ConversationDAO(connection);
        MessageDAO messageDAO = new MessageDAO(connection);
        ProjectDAO projectDAO = new ProjectDAO(connection);
        // Logica per aprire la vista di conversazione per il progetto specificato
        System.out.println("Opening conversation for project: " + projectName);
        ConversationController conversationController = new ConversationController(conversationView,conversationDAO, messageDAO, projectDAO, loginCredentials, connection);
        conversationController.loadConversationsForProject(projectName);
        conversationController.setupView();
        conversationView.display();
        viewStack.push(conversationView);
    }

    public void openAdminView() {
        AdminView adminView = new AdminView(this);
        ProjectDAO projectDAO = new ProjectDAO(connection);
        UserDAO userDAO = new UserDAO(connection);

        // Initialize AdminController with necessary DAOs and the view
        new AdminController(adminView, userDAO, loginCredentials, connection);

        // Display the Admin View
        adminView.display();

        // Push the current view onto the view stack
        viewStack.push(adminView);
    }

    public void openProjectViewForAdmin() {
        ProjectView projectView = new ProjectView(this);
        ProjectDAO projectDAO = new ProjectDAO(connection);
        UserDAO userDAO = new UserDAO(connection);
        ProjectController projectController = new ProjectController(projectView, projectDAO, userDAO, loginCredentials, connection);
        projectController.loadAllProjects();
        projectView.display();

        viewStack.push(projectView);
    }


    public void back() {
        if (viewStack.size() > 1) {
            View currentView = viewStack.pop();
            currentView.close();
            View previousView = viewStack.peek();
            previousView.display();
        }
    }
}
