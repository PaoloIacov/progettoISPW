package controller;

import model.domain.Credentials;
import model.dao.LoginDAO;
import view.GraphicView.LoginView;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

public class LoginController {

    private final LoginView loginView;
    private final LoginDAO loginDAO;
    private final ApplicationController applicationController; // ApplicationController per la gestione della navigazione

    public LoginController(LoginView loginView, LoginDAO loginDAO, ApplicationController applicationController) {
        this.loginView = loginView;
        this.loginDAO = loginDAO;
        this.applicationController = applicationController;

        // Registra il listener per il pulsante di login
        this.loginView.setSubmitButtonListener(new LoginButtonListener());
    }

    // Listener per il pulsante di login
    private class LoginButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String username = loginView.getUsername();
            String password = loginView.getPassword();

            // Creazione di un oggetto CredentialsBean
            Credentials credentials = new Credentials();
            credentials.setUsername(username);
            credentials.setPassword(password);

            try {
                // Verifica le credenziali
                boolean isValid = loginDAO.validateCredentials(credentials);

                if (isValid) {
                    // Se il login ha successo, delega la navigazione all'ApplicationController
                    applicationController.onLoginSuccess(credentials);
                } else {
                    loginView.showError("Invalid username or password.");
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                loginView.showError("Error during login. Please try again later.");
            }
        }
    }

}
