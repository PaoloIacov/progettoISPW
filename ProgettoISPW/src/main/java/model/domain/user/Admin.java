package model.domain.user;

public class Admin implements User {
    private String name;
    private String surname;
    private String role;

    public Admin(String name, String surname, String role) {
        this.name = name;
        this.surname = surname;
        this.role = role;
    }

    @Override
    public void login() {
        System.out.println("Login effettuato con successo!");
    }

    @Override
    public void logout() {
        System.out.println("Logout effettuato con successo!");
    }

    @Override
    public void displayDashboard() {
        System.out.println("Benvenuto, " + name + " " + surname + ". Questo è il tuo pannello di controllo.");
    }
}
