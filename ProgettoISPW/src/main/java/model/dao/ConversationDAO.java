package model.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import model.domain.Conversation;
import model.domain.User;

public class ConversationDAO {

    private final Connection connection;

    public ConversationDAO(Connection connection) {
        this.connection = connection;
    }

    // Metodo generico per ottenere le conversazioni in base al ruolo dell'utente
    public List<Conversation> getConversationsForUser(String username, int role) throws SQLException {
        switch (role) {
            case 1:  //Employee
                // Se l'utente è un dipendente, usa getConversationsForEmployee
                List<Conversation> employeeConversations = getConversationsForEmployee(username);
                return employeeConversations;

            case 2, 3:  //Project Manager or Admin
                // Se l'utente è un project manager o un admin, usa getConversationsForProjectManager
                List<Conversation> managerConversations = getConversationsForProjectManager(username);
                return managerConversations;

            default:  // Ruolo non riconosciuto
                System.out.println("Role not found");
                // Se il ruolo non è riconosciuto, ritorna una lista vuota
                return new ArrayList<>();
        }
    }


    // Metodo per aggiungere una nuova conversazione
    public void addConversation(String description, String projectName) throws SQLException {
        String query = "INSERT INTO Conversation (description, projectName) VALUES (?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, description);
            pstmt.setString(2, projectName);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Metodo per eliminare una conversazione tramite ID
    public void deleteConversation(Long conversationId) throws SQLException {
        String deleteParticipationQuery = "DELETE FROM ConversationParticipation WHERE conversationID = ?";
        String deleteConversationQuery = "DELETE FROM Conversation WHERE ID = ?";

        try (PreparedStatement pstmtParticipation = connection.prepareStatement(deleteParticipationQuery);
             PreparedStatement pstmtConversation = connection.prepareStatement(deleteConversationQuery)) {

            // Elimina le righe figlio prima
            pstmtParticipation.setLong(1, conversationId);
            pstmtParticipation.executeUpdate();

            // Poi elimina la conversazione
            pstmtConversation.setLong(1, conversationId);
            pstmtConversation.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error deleting conversation with ID: " + conversationId);
            e.printStackTrace();
            throw e;
        }
    }


    // Metodo per ottenere le conversazioni di un dipendente dato il suo username
    // Metodo per ottenere le conversazioni per un dipendente
    private List<Conversation> getConversationsForEmployee(String username) throws SQLException {
        List<Conversation> conversations = new ArrayList<>();

        String query = "SELECT c.id, c.description, c.projectName " + "FROM Conversation c " + "JOIN ConversationParticipation cp ON c.id = cp.conversationID " + "WHERE cp.participant = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, username);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    // Usa il costruttore con 3 argomenti
                    Conversation conversation = new Conversation(
                            rs.getLong("id"),
                            rs.getString("description"),
                            rs.getString("projectName")
                    );
                    conversations.add(conversation);
                }
            }
        }

        return conversations;
    }

    public List<Conversation> getConversationsForProjectManager(String username) throws SQLException {
        List<Conversation> conversations = new ArrayList<>();

        String query = "SELECT c.ID, c.description, c.projectName " +
                "FROM Conversation c " +
                "JOIN ProjectAssignments pa ON c.projectName = pa.projectName " +
                "WHERE pa.username = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, username);  // Imposta correttamente il parametro per lo username del PM
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    // Crea un nuovo oggetto ConversationBean con i dati della query
                    Conversation conversation = new Conversation(
                            rs.getLong("ID"),
                            rs.getString("description"),
                            rs.getString("projectName")
                    );
                    conversations.add(conversation);
                }
            }
        }

        return conversations;
    }

    public List<Conversation> getConversationsForProject(String projectName) {
        List<Conversation> conversations = new ArrayList<>();
        String query = "SELECT ID, description, projectName FROM Conversation WHERE projectName = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, projectName);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Conversation conversation = new Conversation(
                            rs.getLong("ID"),
                            rs.getString("description"),
                            rs.getString("projectName")
                    );
                    conversations.add(conversation);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conversations;

    }

    public boolean isProjectExisting(String projectName) throws SQLException {
        String query = "SELECT COUNT(*) FROM project WHERE name = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, projectName);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;  // Se il conteggio è maggiore di 0, il progetto esiste
                }
            }
        }
        return false;
    }

    public void addEmployeeToConversation(Long conversationID, String username) throws SQLException {
        String query = "INSERT INTO ConversationParticipation (conversationID, participant) VALUES (?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setLong(1, conversationID);  // Imposta l'ID della conversazione
            pstmt.setString(2, username);  // Imposta lo username dell'utente
            pstmt.executeUpdate();  // Esegui l'inserimento
        } catch (SQLException e) {
            System.out.println("Error adding employee to conversation with ID: " + conversationID);
            e.printStackTrace();
            throw e;  // Rilancia l'eccezione per gestirla a livello superiore
        }
    }

    public void removeEmployeeFromConversation(Long conversationID, String username) {
        String query = "DELETE FROM ConversationParticipation WHERE conversationID = ? AND participant = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setLong(1, conversationID);
            pstmt.setString(2, username);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error removing employee from conversation with ID: " + conversationID);
            e.printStackTrace();
        }
    }

    public List<User> getEmployeesFromConversation(Long conversationID) {
        List<User> users = new ArrayList<>();
        String query = "SELECT u.username, u.password, u.name, u.surname, u.role " +
                "FROM User u " +
                "JOIN ConversationParticipation cp ON u.username = cp.participant " +
                "WHERE cp.conversationID = ? AND u.role = 1";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setLong(1, conversationID);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String username = rs.getString("username");
                    String password = rs.getString("password");
                    String name = rs.getString("name");
                    String surname = rs.getString("surname");
                    int role = rs.getInt("role");

                    User user = new User(username, password, name, surname, role);
                    users.add(user);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    public boolean isUserInConversation(Long conversationID, String username) {
        String query = "SELECT COUNT(*) FROM ConversationParticipation WHERE conversationID = ? AND participant = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setLong(1, conversationID);
            pstmt.setString(2, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;  // Se il conteggio è maggiore di 0, l'utente è presente
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();

        }
        return false;
    }
}


