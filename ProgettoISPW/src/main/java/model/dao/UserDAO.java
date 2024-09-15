package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.domain.User;

public class UserDAO {

    private final Connection connection;

    public UserDAO(Connection connection) {
        this.connection = connection;
    }

    public int getUserRole(String username) throws SQLException {
        String query = "SELECT role FROM User WHERE username = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("role");  // Supponendo che la colonna `role` sia di tipo INT
                } else {
                    throw new SQLException("User not found.");
                }
            }
        }
    }

    public boolean validateCredentials(String username, String password) throws SQLException {
        String query = "SELECT COUNT(*) FROM User WHERE username = ? AND password = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);
            statement.setString(2, password);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0;  // Se COUNT(*) > 0, le credenziali sono valide
                }
                return false;
            }
        }
    }


    public List<User> getEmployeesFromProject(String projectName) throws SQLException {
        List<User> users = new ArrayList<>();
        String query = "SELECT u.username, u.password, u.name, u.surname, u.role " +
                "FROM User u " +
                "JOIN ProjectAssignments pa ON u.username = pa.username " +
                "WHERE pa.projectName = ? AND u.role = 1";

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
        }
        return users;
    }

    public List<User> getEmployeesFromConversation(Long conversationId) throws SQLException {
        List<User> users = new ArrayList<>();
        String query = "SELECT u.username, u.password, u.name, u.surname, u.role " +
                "FROM User u " +
                "JOIN ConversationParticipation cp ON u.username = cp.participant " +
                "WHERE cp.conversationID = ? AND u.role = 1";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, conversationId);
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
        }
        return users;
    }

    public List<User> getAllUsers() throws SQLException {
        List<User> users = new ArrayList<>();
        String query = "SELECT username, password, name, surname, role FROM User";

        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
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
        return users;
    }

    public void addUser(String username, String password, String name, String surname, int role) throws SQLException {
        String query = "INSERT INTO User (username, password, name, surname, role) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);
            statement.setString(2, password);
            statement.setString(3, name);
            statement.setString(4, surname);
            statement.setInt(5, role);
            statement.executeUpdate();
        }
    }

    public void deleteUser(String username) throws SQLException {
        String query = "DELETE FROM User WHERE username = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);
            statement.executeUpdate();
        }
    }

    public boolean isUserExisting(String username) throws SQLException {
        String query = "SELECT COUNT(*) FROM User WHERE username = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    public List<User> getProjectManagers() throws SQLException {
        List<User> users = new ArrayList<>();
        String query = "SELECT username, password, name, surname, role FROM User WHERE role = 2";

        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
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
        return users;
    }

}
