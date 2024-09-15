package model.dao;

import model.domain.Credentials;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginDAO {

    private Connection connection;

    public LoginDAO(Connection connection) {
        this.connection = connection;
    }

    // Metodo per validare le credenziali
    public boolean validateCredentials(Credentials credentials) throws SQLException {
        String query = "SELECT username,password,name,surname,role FROM User WHERE username = ? AND password = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, credentials.getUsername());
            stmt.setString(2, credentials.getPassword());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // Se le credenziali sono valide, imposta il ruolo
                    credentials.setRole(rs.getInt("role"));
                    return true;
                }
                return false;
            }
        }
    }

    // Metodo per chiudere la connessione
    public void closeConnection() {
        if (connection != null) {
            try {
                if (!connection.isClosed()) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
//Miglioramenti possibili: hashing della password in SHA-256 e creazione di un token di sessione