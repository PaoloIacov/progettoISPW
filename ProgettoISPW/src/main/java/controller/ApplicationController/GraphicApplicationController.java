package controller.ApplicationController;

import controller.GraphicController.AdminController;
import controller.GraphicController.ConversationController;
import controller.GraphicController.LoginController;
import controller.GraphicController.ProjectController;
import model.domain.Credentials;
import model.dao.*;
import view.GraphicView.GraphicConversationView;
import view.GraphicView.GraphicProjectView;
import view.GraphicView.GraphicLoginView;
import view.GraphicView.GraphicAdminView;
import view.View;

import java.sql.Connection;
import java.util.Stack;

public class GraphicApplicationController implements ApplicationController {
    private final Connection connection;
    private final Stack<View> viewStack = new Stack<>(); // Stack per la navigazione
    Credentials loginCredentials;

    public GraphicApplicationController(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void start() {
        GraphicLoginView graphicLoginView = new GraphicLoginView(this);
        LoginDAO loginDAO = new LoginDAO(connection);
        new LoginController(graphicLoginView, loginDAO, this);
        graphicLoginView.display();
        viewStack.push(graphicLoginView);
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
    public void openConversationView(Credentials credentials) {
        GraphicConversationView graphicConversationView = new GraphicConversationView(this);
        ConversationDAO conversationDAO = new ConversationDAO(connection);
        MessageDAO messageDAO = new MessageDAO(connection);
        ProjectDAO projectDAO = new ProjectDAO(connection);
        ConversationController conversationController = new ConversationController(graphicConversationView, conversationDAO, messageDAO, projectDAO, credentials, connection);
        conversationController.loadConversationsForUser();
        conversationController.setupView();
        graphicConversationView.display();
        viewStack.push(graphicConversationView);
    }

    @Override
    public void openProjectView(Credentials credentials) {
        GraphicProjectView graphicProjectView = new GraphicProjectView(this);
        ProjectDAO projectDAO = new ProjectDAO(connection);
        UserDAO userDAO = new UserDAO(connection);
        ProjectController projectController = new ProjectController(graphicProjectView, projectDAO, userDAO, credentials, connection);
        graphicProjectView.display();
        viewStack.push(graphicProjectView);
    }



    @Override
    public void openConversationViewForProject(String projectName) {
        GraphicConversationView graphicConversationView = new GraphicConversationView(this);
        if (projectName == null || projectName.trim().isEmpty()) {
            graphicConversationView.showError("Project name is invalid.");
            return;
        }
        ConversationDAO conversationDAO = new ConversationDAO(connection);
        MessageDAO messageDAO = new MessageDAO(connection);
        ProjectDAO projectDAO = new ProjectDAO(connection);
        // Logica per aprire la vista di conversazione per il progetto specificato
        System.out.println("Opening conversation for project: " + projectName);
        ConversationController conversationController = new ConversationController(graphicConversationView,conversationDAO, messageDAO, projectDAO, loginCredentials, connection);
        conversationController.loadConversationsForProject(projectName);
        conversationController.setupView();
        graphicConversationView.display();
        viewStack.push(graphicConversationView);
    }

    @Override
    public void openAdminView() {
        GraphicAdminView graphicAdminView = new GraphicAdminView(this);
        ProjectDAO projectDAO = new ProjectDAO(connection);
        UserDAO userDAO = new UserDAO(connection);

        // Initialize AdminController with necessary DAOs and the view
        new AdminController(graphicAdminView, userDAO, loginCredentials, connection);

        // Display the Admin View
        graphicAdminView.display();

        // Push the current view onto the view stack
        viewStack.push(graphicAdminView);
    }

    @Override
    public void openProjectViewForAdmin() {
        GraphicProjectView graphicProjectView = new GraphicProjectView(this);
        ProjectDAO projectDAO = new ProjectDAO(connection);
        UserDAO userDAO = new UserDAO(connection);
        ProjectController projectController = new ProjectController(graphicProjectView, projectDAO, userDAO, loginCredentials, connection);
        projectController.loadAllProjects();
        graphicProjectView.display();

        viewStack.push(graphicProjectView);
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
