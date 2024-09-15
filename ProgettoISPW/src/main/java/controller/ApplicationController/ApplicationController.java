package controller.ApplicationController;

import model.domain.Credentials;

public interface ApplicationController {
    void start(); // Method to start the application
    void onLoginSuccess(Credentials credentials); // Handle successful login
    void openProjectView(Credentials credentials);// Open Project View
    void openProjectViewForAdmin(); // Open Project View for Admin
    void openConversationView(Credentials credentials);// Open Conversation View
    void openConversationViewForProject(String projectName); // Open Conversation View for Project
    void openAdminView();// Open Admin View
    void back();// Go back to the previous view
}
