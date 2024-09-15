package controller;

import model.domain.Credentials;
import model.domain.User;
import model.domain.Project;
import model.dao.ProjectDAO;
import model.dao.UserDAO;
import view.GraphicView.ProjectView;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class ProjectController {
    private final ProjectView projectView;
    private final ProjectDAO projectDAO;
    private final UserDAO userDAO;
    private final Credentials credentials;
    private final Connection connection;
    private String currentProjectName;

    public ProjectController(ProjectView projectView, ProjectDAO projectDAO, UserDAO userDAO, Credentials credentials, Connection connection) {
        this.projectView = projectView;
        this.projectDAO = projectDAO;
        this.userDAO = userDAO;
        this.credentials = credentials;
        this.connection = connection;

        loadProjectsForUser(credentials.getUsername());
        setupView();
    }

    private void setupView() {
        projectView.setAddProjectButtonListener(e -> setAddProjectDialog());
        projectView.setDeleteProjectButtonListener(e -> setDeleteProjectDialog());

        int role = credentials.getRole();
        boolean show = (role == 3);  // Only Project Manager or Admin can manage employees
        projectView.setAddButtonVisible(show);
        projectView.setDeleteButtonVisible(show);

        projectView.setAddEmployeeButtonListener(e -> setAddEmployeeDialog(currentProjectName));
        projectView.setDeleteEmployeeButtonListener(e -> setDeleteEmployeeDialog(currentProjectName));
    }

    private void loadProjectsForUser(String username) {
        try {
            List<Project> projects = projectDAO.getProjectsForUser(username);
            projectView.clearProjects();
            for (Project project : projects) {
                projectView.addProjectItem(
                        project.getProjectName(),
                        e -> selectProject(project.getProjectName()),
                        e -> handleAddEmployee(project),
                        e -> handleDeleteEmployee(project)
                );
            }
        } catch (SQLException e) {
            projectView.showError("Error loading projects.");
        }
    }

    public void loadAllProjects() {
        try {
            List<Project> projects = projectDAO.getAllProjects();
            projectView.clearProjects();
            for (Project project : projects) {
                projectView.addProjectItem(
                        project.getProjectName(),
                        e -> selectProject(project.getProjectName()),
                        e -> handleAddEmployee(project),
                        e -> handleDeleteEmployee(project)
                );
            }
        } catch (SQLException e) {
            projectView.showError("Error loading projects.");
        }
    }

    private void selectProject(String projectName) {
        this.currentProjectName = projectName;
            // Fetch the project description from the database
            String projectDescription = projectDAO.getProjectDescription(projectName);

            // Set the selected project name and description in the view
            projectView.setSelectedProjectName(projectName);
            projectView.setSelectedProjectDescription(projectDescription);

    }


    private void handleAddEmployee(Project project) {
        try {
            List<User> usersFromProject = userDAO.getEmployeesFromProject(project.getProjectName());
            List<String> employeeNames = usersFromProject.stream()
                    .map(User::getUsername)
                    .collect(Collectors.toList());

            String selectedUsername = projectView.showAddEmployeeDialog(employeeNames);
            if (selectedUsername != null) {
                setAddEmployeeDialog(selectedUsername);
            }
        } catch (SQLException ex) {
            projectView.showError("Error loading employees.");
        }
    }

    private void handleDeleteEmployee(Project project) {
        try {
            List<User> usersFromProject = projectDAO.getUsersFromProject(project.getProjectName());
            List<String> employeeNames = usersFromProject.stream()
                    .map(User::getUsername)
                    .collect(Collectors.toList());

            String selectedUsername = projectView.showDeleteEmployeeDialog(employeeNames);
            if (selectedUsername != null) {
                projectDAO.removeEmployeeFromProject(project.getProjectName(), selectedUsername);
                projectView.showSuccess("Employee removed successfully from the project.");
            }
        } catch (SQLException ex) {
            projectView.showError("Error loading employees.");
        }
    }

    public void setAddProjectDialog() {
        try {
            // Ottieni la lista dei project manager (ruolo = 2)
            List<User> projectManagers = userDAO.getProjectManagers();
            List<String> projectManagerNames = projectManagers.stream()
                    .map(User::getUsername)
                    .collect(Collectors.toList());

            // Mostra il dialogo per aggiungere un nuovo progetto
            String[] projectDetails = projectView.showAddProjectDialog(projectManagerNames);
            System.out.println(projectDetails);
            // Verifica che l'array contenga tutti i dettagli necessari
            if (projectDetails != null && projectDetails.length == 3) {
                String projectName = projectDetails[0];
                String projectDescription = projectDetails[1];
                String selectedProjectManager = projectDetails[2];
                System.out.println("Project name: " + projectName);
                System.out.println("Project description: " + projectDescription);
                System.out.println("Selected project manager: " + selectedProjectManager);


                if (projectDAO.isProjectExisting(projectName)) {
                    projectView.showError("Project already exists.");
                    return;
                }

                projectDAO.addProject(projectName, projectDescription);
                projectDAO.addEmployeeToProject(projectName, selectedProjectManager);
                projectView.showSuccess("Project added successfully.");
                loadProjectsForUser(credentials.getUsername());
            } else {
                projectView.showError("All fields are required. Please fill in all details.");
            }
        } catch (SQLException e) {
            projectView.showError("Error adding project.");
            e.printStackTrace();
        }
    }



    public void setDeleteProjectDialog() {
        try {
            List<Project> projects = projectDAO.getAllProjects();
            List<String> projectNames = projects.stream()
                    .map(Project::getProjectName)
                    .collect(Collectors.toList());

            String selectedProjectName = projectView.showDeleteProjectDialog(projectNames);
            if (selectedProjectName != null) {
                projectDAO.deleteProject(selectedProjectName);
                projectView.showSuccess("Project deleted successfully.");
                loadProjectsForUser(credentials.getUsername());
            }
        } catch (SQLException e) {
            projectView.showError("Error deleting project.");
            e.printStackTrace();
        }
    }

    public void setAddEmployeeDialog(String projectName) {
        try {
            // Verifica che il nome del progetto sia valido
            if (projectName == null || projectName.trim().isEmpty()) {
                projectView.showError("Please select a project first.");
                return;
            }

            // Ottieni la lista degli utenti dal progetto
            List<User> users = projectDAO.getUsersFromProject(projectName);
            if (users.isEmpty()) {
                projectView.showError("No employees found for this project.");
                return;
            }

            // Filtra solo gli utenti non già nel progetto
            List<String> employeeNames = users.stream()
                    .map(user -> user.getName() + " " + user.getSurname() + " (" + user.getUsername() + ")")
                    .collect(Collectors.toList());

            // Mostra il dialogo per aggiungere un dipendente
            String selectedEmployee = projectView.showAddEmployeeDialog(employeeNames);

            if (selectedEmployee != null) {
                // Estrai lo username dal formato del nome selezionato
                String username = selectedEmployee.substring(selectedEmployee.lastIndexOf("(") + 1, selectedEmployee.lastIndexOf(")")).trim();

                // Controlla se l'utente è già associato al progetto
                if (projectDAO.isUserInProject(projectName, username)) {
                    projectView.showError("Employee is already in the project.");
                    return;
                }

                // Aggiungi l'utente al progetto
                projectDAO.addEmployeeToProject(projectName, username);
                projectView.showSuccess("Employee added successfully to the project.");
            } else {
                projectView.showError("Please select a valid employee.");
            }

        } catch (SQLException e) {
            projectView.showError("Error retrieving employees for the project.");
            e.printStackTrace();
        }
    }


    public void setDeleteEmployeeDialog(String projectName) {
        try {
            if (projectName == null || projectName.trim().isEmpty()) {
                projectView.showError("Please select a project first.");
                return;
            }

            List<User> users = projectDAO.getUsersFromProject(projectName);
            if (users.isEmpty()) {
                projectView.showError("No employees found for this project.");
                return;
            }

            List<String> employeeNames = users.stream()
                    .map(user -> user.getName() + " " + user.getSurname() + " (" + user.getUsername() + ")")
                    .collect(Collectors.toList());

            String selectedEmployee = projectView.showDeleteEmployeeDialog(employeeNames);
            if (selectedEmployee != null) {
                String username = selectedEmployee.substring(selectedEmployee.lastIndexOf("(") + 1, selectedEmployee.lastIndexOf(")")).trim();
                projectDAO.removeEmployeeFromProject(projectName, username);
                projectView.showSuccess("Employee removed successfully from the project.");
            } else {
                projectView.showError("Please select a valid employee.");
            }

        } catch (SQLException e) {
            projectView.showError("Error retrieving employees for the project.");
            e.printStackTrace();
        }
    }




}
