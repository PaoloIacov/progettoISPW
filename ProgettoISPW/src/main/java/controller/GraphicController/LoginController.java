package controller.GraphicController;

import controller.ApplicationController.GraphicApplicationController;
import model.domain.Credentials;
import model.dao.LoginDAO;
import view.GraphicView.GraphicLoginView;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

public class LoginController {

    private final GraphicLoginView graphicLoginView;
    private final LoginDAO loginDAO;
    private final GraphicApplicationController applicationController; // ApplicationController per la gestione della navigazione

    public LoginController(GraphicLoginView graphicLoginView, LoginDAO loginDAO, GraphicApplicationController applicationController) {
        this.graphicLoginView = graphicLoginView;
        this.loginDAO = loginDAO;
        this.applicationController = applicationController;

        // Registra il listener per il pulsante di login
        this.graphicLoginView.setSubmitButtonListener(new LoginButtonListener());
    }

    // Listener per il pulsante di login
    private class LoginButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String username = graphicLoginView.getUsername();
            String password = graphicLoginView.getPassword();

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
                    graphicLoginView.showError("Invalid username or password.");
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                graphicLoginView.showError("Error during login. Please try again later.");
            }
        }
    }

}
