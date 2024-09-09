package org.example;

import controller.LoginController;
import model.dao.ConnectionFactory;
import view.LoginView;

import javax.swing.*;
import java.sql.Connection;
import java.sql.SQLException;

public class Main {

    public static void main(String[] args) {
        // Inizializzazione dell'applicazione
        SwingUtilities.invokeLater(() -> {
            try {
                // Creazione della connessione al database
                Connection connection = ConnectionFactory.getConnection();

                // Creazione della LoginView
                LoginView loginView = new LoginView();

                // Creazione del LoginController con la vista di login e la connessione al database
                LoginController loginController = new LoginController(loginView, connection);

                // Visualizza la vista di login
                loginView.display();

            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error initializing the application: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Unexpected error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}

