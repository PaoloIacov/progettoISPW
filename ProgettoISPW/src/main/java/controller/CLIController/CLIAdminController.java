package controller.CLIController;

import model.dao.ProjectDAO;
import model.dao.UserDAO;
import model.domain.User;
import view.CLIView.CLIAdminView;

import java.sql.SQLException;
import java.util.List;

public class CLIAdminController {
    private final CLIAdminView adminView;
    private final UserDAO userDAO;
    private final ProjectDAO projectDAO;

    public CLIAdminController(CLIAdminView adminView, UserDAO userDAO, ProjectDAO projectDAO) {
        this.adminView = adminView;
        this.userDAO = userDAO;
        this.projectDAO = projectDAO;

        displayAdminMenu();
    }

    // Method to display the admin menu and handle user input
    public void displayAdminMenu() {
        while (true) {
            adminView.display();
            Integer choice = adminView.getUserChoice();

            if (choice == null) {
                adminView.showInvalidChoiceMessage();
                continue;
            }

            switch (choice) {
                case 1 -> viewAllUsers();
                case 2 -> addUser();
                case 3 -> deleteUser();
                case 4 -> createProject();
                case 5 -> deleteProject();
                case 6 -> adminView.goToProjectView();  // Navigate to Project Management
                case 7 -> {
                    adminView.close();
                    return;  // Exit the program
                }
                default -> adminView.showInvalidChoiceMessage();
            }
        }
    }

    // Method to display all users
    private void viewAllUsers() {
        try {
            List<User> users = userDAO.getAllUsers();
            adminView.displayUsers(users);
        } catch (SQLException e) {
            adminView.showError("Error retrieving users from the database.");
        }
    }

    // Method to add a new user
    private void addUser() {
        String[] userDetails = adminView.getNewUserDetails();
        if (userDetails == null) {
            adminView.showError("Failed to get user details.");
            return;
        }

        try {
            String username = userDetails[0];
            String password = userDetails[1];
            String firstName = userDetails[2];
            String lastName = userDetails[3];
            int role = Integer.parseInt(userDetails[4]);

            User newUser = new User(username, password, firstName, lastName, role);
            userDAO.addUser(newUser.getUsername(), newUser.getPassword(), newUser.getName(), newUser.getSurname(), newUser.getRole());
            adminView.showSuccess("User added successfully.");
        } catch (SQLException e) {
            adminView.showError("Error adding user to the database.");
        } catch (NumberFormatException e) {
            adminView.showError("Invalid role number.");
        }
    }

    // Method to delete a user
    private void deleteUser() {
        String usernameToDelete = adminView.getUserToDelete();
        if (usernameToDelete == null || usernameToDelete.isEmpty()) {
            adminView.showError("Invalid username.");
            return;
        }

        try {
            userDAO.deleteUser(usernameToDelete);
            adminView.showSuccess("User deleted successfully.");
        } catch (SQLException e) {
            adminView.showError("Error deleting user from the database.");
        }
    }

    // Method to create a new project
    private void createProject() {
        try {
            List<User> projectManagers = userDAO.getProjectManagers();  // Get all Project Managers
            if (projectManagers.isEmpty()) {
                adminView.showError("No available project managers.");
                return;
            }

            String[] projectDetails = adminView.getNewProjectDetails(projectManagers);
            if (projectDetails == null) {
                adminView.showError("Failed to get project details.");
                return;
            }

            String projectName = projectDetails[0];
            String projectDescription = projectDetails[1];
            String projectManagerUsername = projectDetails[2];

            projectDAO.addProject(projectName, projectDescription);
            projectDAO.addEmployeeToProject(projectName, projectManagerUsername);
            adminView.showSuccess("Project created successfully.");
        } catch (SQLException e) {
            adminView.showError("Error creating a new project.");
        }
    }

    // Method to delete a project
    private void deleteProject() {
        String projectNameToDelete = adminView.getProjectToDelete();
        if (projectNameToDelete == null || projectNameToDelete.isEmpty()) {
            adminView.showError("Invalid project name.");
            return;
        }

        try {
            projectDAO.deleteProject(projectNameToDelete);
            adminView.showSuccess("Project deleted successfully.");
        } catch (SQLException e) {
            adminView.showError("Error deleting project from the database.");
        }
    }
}
