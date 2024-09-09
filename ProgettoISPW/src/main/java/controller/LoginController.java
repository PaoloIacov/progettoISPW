package controller;

import model.bean.CredentialsBean;
import model.dao.LoginDAO;
import model.dao.ConversationDAO;
import model.dao.MessageDAO;
import view.LoginView;
import view.ConversationView;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.SQLException;

public class LoginController {

    private final LoginView loginView;
    private final LoginDAO loginDAO;
    private final Connection connection;

    public LoginController(LoginView loginView, Connection connection) {
        this.loginView = loginView;
        this.connection = connection;
        this.loginDAO = new LoginDAO(connection);

        // Aggiungi il listener per il pulsante di login
        this.loginView.addSubmitListener(new LoginButtonListener());
    }

    // Listener per il pulsante di login
    private class LoginButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String username = loginView.getUsername();
            String password = loginView.getPassword();

            if (!username.isEmpty() && !password.isEmpty()) {
                try {
                    CredentialsBean credentials = new CredentialsBean(username, password);
                    if (loginDAO.validateCredentials(credentials)) {
                        loginView.showSuccess("Login successful!");
                        openConversationScreen(credentials);
                    } else {
                        loginView.showError("Invalid username or password.");
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    loginView.showError("An error occurred while logging in.");
                }
            } else {
                loginView.showError("Username and password cannot be empty.");
            }
        }
    }

    // Metodo per aprire la schermata delle conversazioni dopo un login riuscito
    private void openConversationScreen(CredentialsBean credentials) {
        // Chiudi la vista di login
        loginView.close();

        // Esegui l'apertura della ConversationPage in un nuovo thread dell'Event Dispatch
        javax.swing.SwingUtilities.invokeLater(() -> {
            // Crea la vista e il DAO per le conversazioni
            ConversationView conversationView = new ConversationView();
            ConversationDAO conversationDAO = new ConversationDAO(connection);
            MessageDAO messageDAO = new MessageDAO(connection);

            // Crea il controller per le conversazioni passando anche le credenziali
            ConversationController conversationController = new ConversationController(conversationView, conversationDAO, messageDAO, credentials);

            // Mostra la vista della conversazione
            conversationView.display();
        });
    }

}
