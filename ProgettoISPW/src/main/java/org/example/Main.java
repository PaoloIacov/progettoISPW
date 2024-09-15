package org.example;

import controller.ApplicationController;
import model.dao.ConnectionFactory;

import java.sql.Connection;
import java.sql.SQLException;

public class Main {

    public static void main(String[] args) {
        try {
            // Creazione della connessione al database
            Connection connection = ConnectionFactory.getConnection();

            // Creazione dell'application controller
            ApplicationController appController = new ApplicationController(connection);

            // Avvia l'applicazione
            appController.start();
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error establishing connection to the database.");
        }
    }
}
