package controller.CLIController;

import controller.ApplicationController.ApplicationController;
import controller.ApplicationController.CLIApplicationController;
import model.domain.Credentials;
import model.dao.LoginDAO;
import view.CLIView.CLILoginView;

import java.io.IOException;
import java.sql.SQLException;

public class CLILoginController {

    private final CLILoginView loginView;
    private final LoginDAO loginDAO;
    private final CLIApplicationController applicationController;  // ApplicationController to handle navigation

    public CLILoginController(CLILoginView loginView, LoginDAO loginDAO, CLIApplicationController applicationController) {
        this.loginView = loginView;
        this.loginDAO = loginDAO;
        this.applicationController = applicationController;
    }

    // Start the CLI login process
    public void start() {
        loginView.showLoginScreen();// Display the login screen
        try {
            // Get user input for credentials
            Credentials credentials = loginView.getCredentialsInput();

            // Validate the credentials using LoginDAO
            boolean isValid = loginDAO.validateCredentials(credentials);

            if (isValid) {
                // On successful login, handle navigation to the appropriate view
                applicationController.onLoginSuccess(credentials);
            } else {
                // Show error if credentials are invalid
                loginView.showLoginError("Invalid username or password.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            loginView.showError("Error during login. Please try again later.");
        } catch (IOException e) {
            loginView.showError("Error reading input. Please try again.");
        }
    }
}
