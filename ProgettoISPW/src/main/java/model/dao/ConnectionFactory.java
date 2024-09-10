package model.dao;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConnectionFactory {

    private ConnectionFactory() {}

    private static Properties properties;

    // Blocco statico per caricare le proprietà al momento del caricamento della classe
    static {
        try (InputStream input = ConnectionFactory.class.getClassLoader().getResourceAsStream("database.properties")) {
            properties = new Properties();
            if (input == null) {
                throw new IOException("File database.properties non trovato.");
            }
            properties.load(input);
        } catch (IOException e) {
            e.printStackTrace();
            throw new ExceptionInInitializerError("Errore durante il caricamento delle proprietà del database.");
        }
    }

    // Metodo per ottenere la connessione generica
    public static Connection getConnection() throws SQLException {
        String connectionUrl = properties.getProperty("CONNECTION_URL");
        String user = properties.getProperty("LOGIN_USER");
        String pass = properties.getProperty("LOGIN_PASS");

        return DriverManager.getConnection(connectionUrl, user, pass);
    }

    // Metodo per ottenere la connessione basata su un ruolo intero
    public static Connection getConnectionByRole(int role) throws SQLException {
        String connectionUrl = properties.getProperty("CONNECTION_URL");

        // Determinazione delle credenziali in base al ruolo (1 = Employee, 2 = Project Manager, 3 = Admin)
        String userKey;
        String passKey;

        switch (role) {
            case 1:
                userKey = "EMPLOYEE_USER";
                passKey = "EMPLOYEE_PASS";
                break;
            case 2:
                userKey = "PROJECT_MANAGER_USER";
                passKey = "PROJECT_MANAGER_PASS";
                break;
            case 3:
                userKey = "ADMIN_USER";
                passKey = "ADMIN_PASS";
                break;
            default:
                throw new SQLException("Non valid role: " + role);
        }

        // Recupero delle credenziali dal file properties
        String user = properties.getProperty(userKey);
        String pass = properties.getProperty(passKey);

        if (user == null || pass == null) {
            throw new SQLException("Credentials not found: " + role);
        }

        // Restituzione della connessione basata sul ruolo
        return DriverManager.getConnection(connectionUrl, user, pass);
    }

    // Metodo per chiudere la connessione
    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                if (!connection.isClosed()) {
                    connection.close();
                    System.out.println("Connection close.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("Error closing the connection.");
            }
        }
    }
}
