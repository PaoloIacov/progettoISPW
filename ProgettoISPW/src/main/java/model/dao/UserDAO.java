package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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

    public void addUser(String username, String password, int role, String name, String surname) throws SQLException {
        String query = "INSERT INTO User (username, password, role, name, surname) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);
            statement.setString(2, password);
            statement.setInt(3, role);
            statement.setString(4, name);
            statement.setString(5, surname);
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

}
