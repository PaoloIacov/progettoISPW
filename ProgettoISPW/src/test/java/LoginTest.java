import controller.LoginController;
import model.dao.LoginDAO;
import model.bean.CredentialsBean;
import model.dao.ConnectionFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import view.LoginView;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

public class LoginTest {

    private Connection connection;
    private LoginDAO loginDAO;
    private LoginController loginController;
    private LoginView loginView;

    @BeforeEach
    public void setUp() throws SQLException {
        // Crea una connessione reale al database
        connection = ConnectionFactory.getConnection();

        // Inizializza il DAO con la connessione reale
        loginDAO = new LoginDAO(connection);

        // Inizializza la vista di login
        loginView = new LoginView();

        // Crea il LoginController
        loginController = new LoginController(loginView, connection);
    }

    @AfterEach
    public void tearDown() throws SQLException {
        // Chiudi la connessione dopo ogni test
        ConnectionFactory.closeConnection(connection);
    }

    @Test
    public void testLoginSuccess() throws SQLException {
        // Simula le credenziali corrette inserite tramite la vista
        CredentialsBean credentials = new CredentialsBean("admin", "admin");

        // Simula l'input dell'utente
        loginView.getUsernameField().setText("admin");
        loginView.getPasswordField().setText("admin");

        // Verifica che il login sia riuscito
        assertTrue(loginDAO.validateCredentials(credentials), "Login fallito con credenziali corrette");
        assertEquals(3, credentials.getRole(), "Il ruolo non è corretto"); // Esempio: ruolo admin = 3
    }

    @Test
    public void testLoginFailure() throws SQLException {

        // Simula credenziali errate
        CredentialsBean credentials = new CredentialsBean("wrongUser", "wrongPass");

        // Simula l'input dell'utente
        loginView.getUsernameField().setText("wrongUser");
        loginView.getPasswordField().setText("wrongPass");

        // Verifica che il login sia fallito
        assertFalse(loginDAO.validateCredentials(credentials), "Il login dovrebbe fallire con credenziali errate");
    }
}
