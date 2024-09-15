package org.example;

import controller.ApplicationController.CLIApplicationController;
import controller.ApplicationController.GraphicApplicationController;
import model.dao.ConnectionFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        try {
            // Create a connection to the database
            Connection connection = ConnectionFactory.getConnection();

            // Prompt the user to choose between CLI or GUI
            System.out.println("Choose your interface:");
            System.out.println("1. Command Line Interface (CLI)");
            System.out.println("2. Graphical User Interface (GUI)");
            System.out.print("Enter your choice (1 or 2): ");

            Scanner scanner = new Scanner(System.in);
            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    // Use CLI version of the application
                    CLIApplicationController cliApplicationController = new CLIApplicationController(connection);
                    cliApplicationController.start();
                    break;
                case 2:
                    // Use GUI version of the application
                    GraphicApplicationController graphicApplicationController = new GraphicApplicationController(connection);
                    graphicApplicationController.start();
                    break;
                default:
                    System.out.println("Invalid choice. Please restart the application and choose 1 or 2.");
                    break;
            }

            scanner.close();
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error establishing connection to the database.");
        }
    }
}

