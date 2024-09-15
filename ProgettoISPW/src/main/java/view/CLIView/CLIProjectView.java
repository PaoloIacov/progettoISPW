package view.CLIView;

import controller.ApplicationController.ApplicationController;
import controller.ApplicationController.CLIApplicationController;
import model.domain.Project;
import view.View;


import java.util.List;
import java.util.Scanner;

public class CLIProjectView implements View {
    private CLIApplicationController applicationController;

    private final Scanner scanner;

    public CLIProjectView(CLIApplicationController applicationController) {
        this.applicationController = applicationController;
        scanner = new Scanner(System.in);
    }



    // Method to display the list of projects with their name and description
    public void displayProjects(List<Project> projects) {
        System.out.println("=== Your Projects ===");
        if (projects.isEmpty()) {
            System.out.println("No projects available.");
        } else {
            for (Project project : projects) {
                System.out.println("Name: " + project.getProjectName());
                System.out.println("Description: " + project.getProjectDescription());
                System.out.println("-----------------------------------");
            }
        }
    }

    public Integer showProjectOptions() {
        System.out.println("What would you like to do?");
        System.out.println("1. Select a project to view details.");
        System.out.println("2. Exit.");
        System.out.print("Enter your choice (1-2): ");

        String input = scanner.nextLine();
        return parseInput(input);
    }


    // Method to get the name of the selected project from the user
    public String getSelectedProjectName() {
        System.out.print("Enter the name of the project you want to view: ");
        return scanner.nextLine().trim();
    }

    // Method to display detailed information of a selected project
    public int displayProjectDetails(Project project) {
        System.out.println("=== Project Details ===");
        System.out.println("Name: " + project.getProjectName());
        System.out.println("Description: " + project.getProjectDescription());
        System.out.println("-----------------------------------");
        System.out.println("1) Open project conversations");
        System.out.println("2) Quit");

        String input = scanner.nextLine();
        return parseInput(input);
    }

    // Utility method to parse input for an integer
    private Integer parseInput(String input) {
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a valid number.");
            return null;
        }
    }

    // Method to show a success message
    public void showSuccess(String message) {
        System.out.println("Success: " + message);
    }

    // Method to show an error message
    public void showError(String message) {
        System.out.println("Error: " + message);
    }

    public void goToConversationView(String selectedProjectName) {
        applicationController.openConversationViewForProject(selectedProjectName);
    }

    @Override
    public void display() {
        //This is not needed for this view
    }

    @Override
    public void close() {
        scanner.close();
    }

    @Override
    public void refresh() {
        //This is not needed for this view
    }

    @Override
    public void back() {
        applicationController.back();
    }
}

