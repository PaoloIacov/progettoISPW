package controller.CLIController;

import model.dao.ProjectDAO;
import model.dao.UserDAO;
import model.domain.Credentials;
import model.domain.Project;
import view.CLIView.CLIProjectView;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class CLIProjectController {

    private final CLIProjectView projectView;
    private final ProjectDAO projectDAO;
    private final UserDAO userDAO;
    private final Credentials credentials;
    private final Connection connection;


    public CLIProjectController(CLIProjectView projectView, ProjectDAO projectDAO, UserDAO userDAO, Credentials credentials, Connection connection) {
        this.projectView = projectView;
        this.projectDAO = projectDAO;
        this.userDAO = userDAO;
        this.credentials = credentials;
        this.connection = connection;


    }

    // Main method to start the project management workflow
    public void manageProjects() {
        try {
            // Load projects for the current user
            List<Project> projects = projectDAO.getProjectsForUser(credentials.getUsername());
            projectView.displayProjects(projects);

            // Show project options
            Integer action = projectView.showProjectOptions();
            if (action != null) {
                handleAction(action, projects);
            }
        } catch (SQLException e) {
            projectView.showError("Error loading projects: " + e.getMessage());
        }
    }

    public void getAllProject() {
        try {
            List<Project> projects = projectDAO.getAllProjects();
            projectView.displayProjects(projects);

            Integer action = projectView.showProjectOptions();
            if (action != null) {
                handleAction(action, projects);
            }
        } catch (SQLException e) {
            projectView.showError("Error loading projects: " + e.getMessage());
        }


    }

    // Method to handle user actions for project management
    private void handleAction(int action, List<Project> projects) {
        switch (action) {
            case 1:
                selectProject(projects);
                break;
            case 2:
                projectView.showSuccess("Exiting.");
                System.exit(0);
                break;
            default:
                projectView.showError("Invalid option selected.");
                manageProjects();
                break;
        }
    }

    // Method to select a project to view its details
    private void selectProject(List<Project> projects) {
        String selectedProjectName = projectView.getSelectedProjectName();
        Project selectedProject = findProjectByName(projects, selectedProjectName);

        if (selectedProject != null) {
            handleProjectDetailsAction(selectedProject);
        } else {
            projectView.showError("Project not found.");
            selectProject(projects); // Ask the user again to select a project
        }
    }

    private void handleProjectDetailsAction(Project selectedProject) {
        Integer action = projectView.displayProjectDetails(selectedProject);
        if (action != null) {
            switch (action) {
                case 1:
                    // Use CLIApplicationController to open the conversation view for the selected project
                    projectView.goToConversationView(selectedProject.getProjectName());
                    System.out.println("Opening project conversations... for " + selectedProject.getProjectName());
                    break;
                case 2:
                    projectView.showSuccess("Exiting.");
                    System.exit(0);
                    break;
                default:
                    projectView.showError("Invalid option selected.");
                    handleProjectDetailsAction(selectedProject);
                    break;
            }
        }
    }

    private Project findProjectByName(List<Project> projects, String projectName) {
        return projects.stream()
                .filter(project -> project.getProjectName().equalsIgnoreCase(projectName))
                .findFirst()
                .orElse(null);
    }
}

