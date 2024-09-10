package model.dao;

import model.bean.ProjectBean;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.dao.UserDAO;

public class ProjectDAO {

    private final Connection connection;

    // Costruttore che prende la connessione come argomento
    public ProjectDAO(Connection connection) {
        this.connection = connection;
    }

    // Metodo che restituisce i progetti per uno specifico utente
    public List<ProjectBean> getProjectsForUser(String username) throws SQLException {
        List<ProjectBean> projects = new ArrayList<>();
        UserDAO userDAO = new UserDAO(connection);

        // Ottieni il ruolo dell'utente utilizzando il metodo getUserRole di UserDAO
        int role = userDAO.getUserRole(username);

        switch (role) {
            case 3:  // Ruolo admin (3)
                // Se l'utente è un admin, restituisci tutti i progetti
                return getAllProjects();

            case 2:  // Ruolo project manager (2)
                // Se l'utente è un project manager, restituisci solo i progetti assegnati
                String query = "SELECT p.name, p.description " +
                        "FROM Project p " +
                        "JOIN ProjectAssignments pa ON p.name = pa.projectName " +
                        "WHERE pa.username = ?";

                try (PreparedStatement statement = connection.prepareStatement(query)) {
                    statement.setString(1, username);  // Usa il parametro username

                    try (ResultSet resultSet = statement.executeQuery()) {
                        while (resultSet.next()) {
                            String projectName = resultSet.getString("name");
                            String projectDescription = resultSet.getString("description");
                            ProjectBean project = new ProjectBean(projectName, projectDescription);
                            projects.add(project);
                        }
                    }
                }
                break;

                default:
                    System.out.println("Ruolo non supportato per l'utente: " + username);
                    break;
            }

            return projects;
        }

    public List<ProjectBean> getAllProjects() throws SQLException {
        List<ProjectBean> projects = new ArrayList<>();

        String query = "SELECT name, description FROM Project";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    String projectName = resultSet.getString("name");
                    String projectDescription = resultSet.getString("description");
                    ProjectBean project = new ProjectBean(projectName, projectDescription);
                    projects.add(project);
                }
            }
        }

        return projects;
    }

}
