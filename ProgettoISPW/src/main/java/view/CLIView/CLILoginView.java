package view.CLIView;

import model.domain.Credentials;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class CLILoginView {

    private BufferedReader reader;
    private ActionListener submitButtonListener;  // Listener for submit button

    public CLILoginView() {
        reader = new BufferedReader(new InputStreamReader(System.in));
    }

    public void showLoginScreen() {
        System.out.println("=== Login ===");
    }

    public void display() {
        showLoginScreen();
        try {
            getCredentialsInput();
        } catch (IOException e) {
            showError("Error reading input. Please try again.");
        }
    }

    public void getCredentialsInput() throws IOException {
        System.out.print("Enter your username: ");
        String username = reader.readLine();
        System.out.print("Enter your password: ");
        String password = reader.readLine();

        // Trigger actionPerformed as if a button is clicked
        if (submitButtonListener != null) {
            submitButtonListener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, null) {
                @Override
                public String getActionCommand() {
                    return "SUBMIT";
                }
            });
        }
    }

    public void showLoginSuccess(Credentials credentials) {
        System.out.println("Login successful! Welcome, " + credentials.getUsername() + ". You are identified as: " + credentials.getRole());
    }

    public void showLoginError(String errorMessage) {
        System.out.println("Login error: " + errorMessage);
    }

    public void showError(String errorMessage) {
        System.out.println("Error: " + errorMessage);
    }

    // Methods to interact with the controller

    public String getUsername() throws IOException {
        System.out.print("Username: ");
        return reader.readLine();
    }

    public String getPassword() throws IOException {
        System.out.print("Password: ");
        return reader.readLine();
    }

    public void setSubmitButtonListener(ActionListener listener) {
        this.submitButtonListener = listener;
    }
}
