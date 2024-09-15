package view.CLIView;

import controller.ApplicationController.ApplicationController;
import model.domain.User;
import java.util.List;
import java.util.Scanner;
import view.View;

public class CLIAdminView implements View {
    private final ApplicationController applicationController;
    private final Scanner scanner;

    public CLIAdminView(ApplicationController applicationController) {
        this.applicationController = applicationController;
        scanner = new Scanner(System.in);
    }

    public void showAdminMenu() {
        System.out.println("=== Admin Menu ===");
        System.out.println("1) View all users");
        System.out.println("2) Add a new user");
        System.out.println("3) Delete a user");
        System.out.println("4) Create a new project");
        System.out.println("5) Delete a project");
        System.out.println("6) Go to Project Management");
        System.out.println("7) Quit");
        System.out.print("Enter your choice (1-7): ");
    }

    public Integer getUserChoice() {
        String input = scanner.nextLine();
        return parseInput(input);
    }

    public void displayUsers(List<User> users) {
        System.out.println("=== All Users ===");
        if (users.isEmpty()) {
            System.out.println("No users found.");
        } else {
            for (User user : users) {
                System.out.println("Username: " + user.getUsername());
                System.out.println("Role: " + user.getRole());
                System.out.println("-----------------------------------");
            }
        }
    }

    public String[] getNewUserDetails() {
        System.out.print("Enter username: ");
        String username = scanner.nextLine().trim();

        System.out.print("Enter password: ");
        String password = scanner.nextLine().trim();

        System.out.print("Enter first name: ");
        String firstName = scanner.nextLine().trim();

        System.out.print("Enter last name: ");
        String lastName = scanner.nextLine().trim();

        System.out.println("Enter role number:");
        System.out.println("1) Employee");
        System.out.println("2) Project Manager");
        System.out.println("3) Admin");

        String role = scanner.nextLine().trim();

        return new String[]{username, password, firstName, lastName, role};
    }

    public String getUserToDelete() {
        System.out.print("Enter the username of the user to delete: ");
        return scanner.nextLine().trim();
    }

    // Method to get new project details and assign a Project Manager
    public String[] getNewProjectDetails(List<User> projectManagers) {
        System.out.print("Enter project name: ");
        String projectName = scanner.nextLine().trim();

        System.out.print("Enter project description: ");
        String projectDescription = scanner.nextLine().trim();

        System.out.println("Choose a Project Manager from the list below:");
        for (int i = 0; i < projectManagers.size(); i++) {
            User pm = projectManagers.get(i);
            System.out.println((i + 1) + ") " + pm.getUsername());
        }
        System.out.print("Enter the number corresponding to the Project Manager: ");
        int pmChoice = Integer.parseInt(scanner.nextLine().trim());

        if (pmChoice < 1 || pmChoice > projectManagers.size()) {
            showError("Invalid choice for Project Manager.");
            return null;
        }

        String projectManagerUsername = projectManagers.get(pmChoice - 1).getUsername();

        return new String[]{projectName, projectDescription, projectManagerUsername};
    }

    // Method to get the name of the project to delete
    public String getProjectToDelete() {
        System.out.print("Enter the name of the project to delete: ");
        return scanner.nextLine().trim();
    }

    public void showSuccess(String message) {
        System.out.println("Success: " + message);
    }

    public void showError(String message) {
        System.out.println("Error: " + message);
    }

    public void showInvalidChoiceMessage() {
        System.out.println("Invalid choice. Please enter a valid option.");
    }

    public void close() {
        scanner.close();
    }

    private Integer parseInput(String input) {
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a valid number.");
            return null;
        }
    }

    public void goToProjectView() {
        applicationController.openProjectViewForAdmin();
    }

    @Override
    public void display() {
        showAdminMenu();
    }

    @Override
    public void refresh() {
        // Not needed for Admin
    }

    @Override
    public void back() {
        applicationController.back();
    }
}
