package model.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ConversationDAO {

    private final Connection connection;

    public ConversationDAO(Connection connection) {
        this.connection = connection;
    }

    // Metodo generico per ottenere le conversazioni in base al ruolo dell'utente
    public List<String[]> getConversations(String username, int role) throws SQLException {
        switch (role) {
            case 1:  //Employee
                System.out.println("Employee");
                // Se l'utente è un dipendente, usa getConversationsForEmployee
                List<String[]> employeeConversations = getConversationsForEmployee(username);
                System.out.println(employeeConversations);
                return employeeConversations;

            case 2, 3:  //Project Manager or Admin
                System.out.println("Project Manager or Admin");
                // Se l'utente è un project manager o un admin, usa getConversationsForProjectManager
                List<String[]> managerConversations = getConversationsForProjectManager(username);
                System.out.println(managerConversations);
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
        }
    }

    // Metodo per eliminare una conversazione tramite ID
    public void deleteConversation(long conversationId) throws SQLException {
        String query = "DELETE FROM Conversation WHERE ID = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setLong(1, conversationId);
            pstmt.executeUpdate();
        }
    }

    // Metodo per ottenere le conversazioni di un dipendente dato il suo username
    public List<String[]> getConversationsForEmployee(String username) throws SQLException {
        List<String[]> conversations = new ArrayList<>();
        String query = "SELECT c.ID, c.description, c.projectName " +
                "FROM Conversation c " +
                "JOIN ConversationParticipation cp ON c.ID = cp.ConversationID " +
                "WHERE cp.participant = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String[] conversation = {
                            rs.getString("ID"),
                            rs.getString("description"),
                            rs.getString("projectName")
                    };
                    conversations.add(conversation);
                }
            }
        }
        System.out.println(conversations);
        return conversations;
    }

    // Metodo per ottenere le conversazioni relative ai progetti assegnati a un project manager
    public List<String[]> getConversationsForProjectManager(String pmUsername) throws SQLException {
        List<String[]> conversations = new ArrayList<>();
        String query = "SELECT c.ID, c.description, c.projectName " +
                "FROM Conversation c " +
                "JOIN ProjectAssignments pa ON c.projectName = pa.projectName " +
                "WHERE pa.username = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, pmUsername);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String[] conversation = {
                            rs.getString("ID"),
                            rs.getString("description"),
                            rs.getString("projectName")
                    };
                    conversations.add(conversation);
                }
            }
        }
        System.out.println(conversations);
        return conversations;
    }
}
