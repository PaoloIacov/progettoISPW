package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.sql.ResultSet;

public class MessageDAO {

    private final Connection connection;

    public MessageDAO(Connection connection) {
        this.connection = connection;
    }

    // Metodo per aggiungere un nuovo messaggio
    public void addMessage(long conversationId, String senderUsername, String content) throws SQLException {
        String query = "INSERT INTO Message (conversationID, senderUsername, content) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setLong(1, conversationId);
            pstmt.setString(2, senderUsername);
            pstmt.setString(3, content);
            pstmt.executeUpdate();
        }
    }

    // Metodo per ottenere tutti i messaggi di una conversazione specifica
    public List<String[]> getMessagesByConversationId(long conversationId) throws SQLException {
        List<String[]> messages = new ArrayList<>();
        String query = "SELECT ID, conversationID, senderUsername, content, datetime FROM Message WHERE conversationID = ? ORDER BY datetime ASC";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setLong(1, conversationId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String[] message = {
                            rs.getString("ID"),
                            rs.getString("conversationID"),
                            rs.getString("senderUsername"),
                            rs.getString("content"),
                            rs.getString("datetime")
                    };
                    messages.add(message);
                }
            }
        }
        return messages;
    }
}
