package view.CLIView;

import controller.ApplicationController.CLIApplicationController;
import model.domain.Conversation;
import model.domain.Message;
import view.View;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class CLIConversationView implements View {

    private BufferedReader reader;
    private CLIApplicationController applicationController;

    public CLIConversationView(CLIApplicationController applicationController) {
        this.applicationController = applicationController;
        reader = new BufferedReader(new InputStreamReader(System.in));
    }

    // Show the list of conversations
    public void showConversations(List<Conversation> conversations) {
        System.out.println("=== Conversations ===");
        if (conversations.isEmpty()) {
            System.out.println("No conversations available.");
        } else {
            for (Conversation conversation : conversations) {
                System.out.println("ID: " + conversation.getConversationId() + " | Description: " + conversation.getDescription());
            }
        }
    }

    // Choose an action for Project Managers and Admins
    public Long chooseAction(int userRole) throws IOException {
        System.out.println("1. View conversation messages");
        if (userRole == 2 || userRole == 3) {
            System.out.println("2. Add a conversation");
            System.out.println("3. Delete a conversation");
            System.out.println("4. Add a user to a conversation");
            System.out.println("5. Remove a user from a conversation");
            System.out.println("6. Go back");
            System.out.println("7. Exit");
        } else {
            System.out.println("2. Go back");
            System.out.println("3. Exit");
        }
        System.out.print("Choose an action: ");
        return Long.parseLong(reader.readLine());
    }

    // Get the selected conversation ID from the user
    public Long getSelectedConversationId() throws IOException {
        System.out.print("Insert the conversation ID to see the messages: ");
        return Long.parseLong(reader.readLine());
    }

    // Show messages of a conversation
    public void showConversationMessages(List<Message> messages) {
        System.out.println("=== Messages ===");
        if (messages.isEmpty()) {
            System.out.println("No messages in the conversation.");
        } else {
            for (Message message : messages) {
                System.out.println(message.getSenderUsername() + ": " + message.getContent());
            }
        }
    }

    public int showMessageOptionsMenu() {
        System.out.println("1. Send a message");
        System.out.println("2. Go back");
        System.out.println("3. Exit");
        try {
            return Integer.parseInt(reader.readLine());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public String getMessageContentInput() throws IOException {
        System.out.print("Insert the message content: ");
        return reader.readLine();
    }

    // Show an error message
    public void showError(String message) {
        System.out.println("Error: " + message);
    }

    // Show a success message
    public void showSuccess(String message) {
        System.out.println("Success: " + message);
    }

    @Override
    public void display() {
        // Not needed for Conversation
    }

    @Override
    public void close() {
        try {
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void refresh() {
        // Not needed for Conversation
    }

    public void back() {
        applicationController.back();
    }

    // Methods for roles 2 (Project Manager) and 3 (Admin)

    // Method to add a conversation
    public String getNewConversationDescription() throws IOException {
        System.out.print("Enter the description for the new conversation: ");
        return reader.readLine();
    }

    // Method to delete a conversation
    public Long getConversationIdToDelete() throws IOException {
        System.out.print("Enter the ID of the conversation you want to delete: ");
        return Long.parseLong(reader.readLine());
    }

    // Method to add a user to a conversation
    public String[] getUserDetailsToAddToConversation() throws IOException {
        System.out.print("Enter the conversation ID to add a user to: ");
        String conversationId = reader.readLine();
        System.out.print("Enter the username to add to the conversation: ");
        String username = reader.readLine();
        return new String[]{conversationId, username};
    }

    // Method to remove a user from a conversation
    public String[] getUserDetailsToRemoveFromConversation() throws IOException {
        System.out.print("Enter the conversation ID to remove a user from: ");
        String conversationId = reader.readLine();
        System.out.print("Enter the username to remove from the conversation: ");
        String username = reader.readLine();
        return new String[]{conversationId, username};
    }

    public String getUserInput(String prompt) throws IOException {
        System.out.print(prompt);
        return reader.readLine().trim();
    }

    public String getProjectNameForNewConversation() throws IOException {
        System.out.print("Enter the project name for the new conversation: ");
        return reader.readLine();
    }
}
