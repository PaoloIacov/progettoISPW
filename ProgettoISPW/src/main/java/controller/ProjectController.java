package controller;

import model.bean.ProjectBean;
import model.dao.ProjectDAO;
import view.ProjectView;
import model.bean.CredentialsBean;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.List;

public class ProjectController {

    private final ProjectView projectView;
    private final ProjectDAO projectDAO;
    private final CredentialsBean credentials;
    private final ApplicationController applicationController;

    public ProjectController(ProjectView projectView, ProjectDAO projectDAO, CredentialsBean credentials, ApplicationController applicationController) {
        this.projectView = projectView;
        this.projectDAO = projectDAO;
        this.credentials = credentials;
        this.applicationController = applicationController;

        projectView.setConfirmButtonListener(new ConfirmButtonListener());
        loadProjects();
    }

    private void loadProjects() {
        try {
            List<ProjectBean> projects = projectDAO.getProjectsForUser(credentials.getUsername());
            for (ProjectBean project : projects) {
                projectView.addProjectItem(project.getProjectName(), project.getProjectDescription());
            }
        } catch (SQLException e) {
            projectView.showError("Error loading projects.");
            e.printStackTrace();
        }
    }

    private class ConfirmButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String selectedProject = projectView.getSelectedProjectName();
            if (selectedProject != null) {
                applicationController.openConversationView(credentials);
            } else {
                projectView.showError("Please select a project.");
            }
        }
    }
}
