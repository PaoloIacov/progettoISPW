package model.dao;

import model.domain.Project;
import model.domain.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProjectDAO {

    private final Connection connection;

    // Costruttore che prende la connessione come argomento
    public ProjectDAO(Connection connection) {
        this.connection = connection;
    }

    // Metodo che restituisce i progetti per uno specifico utente
    public List<Project> getProjectsForUser(String username) throws SQLException {
        List<Project> projects = new ArrayList<>();
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
                            Project project = new Project(projectName, projectDescription);
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

    public List<Project> getAllProjects() throws SQLException {
        List<Project> projects = new ArrayList<>();

        String query = "SELECT name, description FROM Project";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    String projectName = resultSet.getString("name");
                    String projectDescription = resultSet.getString("description");
                    Project project = new Project(projectName, projectDescription);
                    projects.add(project);
                }
            }
        }

        return projects;
    }

    public void addProject(String projectName, String projectDescription) throws SQLException {
        String query = "INSERT INTO Project (name, description) VALUES (?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, projectName);
            statement.setString(2, projectDescription);
            statement.executeUpdate();
        }
    }

    public void addEmployeeToProject(String projectName, String username) throws SQLException {
        String query = "INSERT INTO ProjectAssignments (projectName, username) VALUES (?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, projectName);
            statement.setString(2, username);
            statement.executeUpdate();
        }
    }

    public void deleteProject(String projectName) throws SQLException {
        // First, delete all conversations related to the project
        String deleteConversationsQuery = "DELETE FROM Conversation WHERE projectName = ?";

        try (PreparedStatement pstmtConversations = connection.prepareStatement(deleteConversationsQuery)) {
            pstmtConversations.setString(1, projectName);
            pstmtConversations.executeUpdate();
        }

        // Now, delete the project itself
        String deleteProjectQuery = "DELETE FROM Project WHERE name = ?";
        try (PreparedStatement pstmtProject = connection.prepareStatement(deleteProjectQuery)) {
            pstmtProject.setString(1, projectName);
            pstmtProject.executeUpdate();
        }
    }


    public void removeEmployeeFromProject(String projectName, String username) throws SQLException {
        String query = "DELETE FROM ProjectAssignments WHERE projectName = ? AND username = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, projectName);
            statement.setString(2, username);
            statement.executeUpdate();
        }
    }

    public boolean isProjectExisting(String projectName) throws SQLException {
        String query = "SELECT COUNT(*) FROM Project WHERE name = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, projectName);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    public List<User> getUsersFromProject(String projectName) {
        List<User> users = new ArrayList<>();
        String query = "SELECT u.username, u.password, u.name, u.surname, u.role " +
                "FROM User u " +
                "JOIN ProjectAssignments pa ON u.username = pa.username " +
                "WHERE pa.projectName = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, projectName);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    String username = resultSet.getString("username");
                    String password = resultSet.getString("password");
                    String name = resultSet.getString("name");
                    String surname = resultSet.getString("surname");
                    int role = resultSet.getInt("role");

                    User user = new User(username, password, name, surname, role);
                    users.add(user);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return users;
    }

    public boolean isUserInProject(String projectName, String username) {
        String query = "SELECT COUNT(*) FROM ProjectAssignments WHERE projectName = ? AND username = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, projectName);
            statement.setString(2, username);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public String getProjectDescription(String projectName) {
        String query = "SELECT description FROM Project WHERE name = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, projectName);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString("description");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
