package view.CLIView;

import model.domain.Credentials;
import view.View;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class CLILoginView implements View{

    private BufferedReader reader;

    public CLILoginView() {
        reader = new BufferedReader(new InputStreamReader(System.in));
    }

    public void showLoginScreen() {
        System.out.println("=== Login ===");
    }

    public Credentials getCredentialsInput() throws IOException {
        System.out.print("Insert username: ");
        String username = reader.readLine();
        System.out.print("Insert password: ");
        String password = reader.readLine();

        return new Credentials(username, password);
    }

    public void showLoginSuccess(Credentials credentials) {
        System.out.println("Login successfull, welcome " + credentials.getUsername() + ". Identified as: " + credentials.getRole());
    }

    public void showLoginError(String errorMessage) {
        System.out.println("Errore di login: " + errorMessage);
    }

    public void showError(String errorMessage) {
        System.out.println("Errore: " + errorMessage);
    }

    @Override
    public void display() {
        showLoginScreen();

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
        //Not needed for Login
    }

    public void back() {
        //Not needed for Login
    }
}
