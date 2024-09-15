package controller;

import model.domain.Credentials;
import model.domain.User;
import model.dao.UserDAO;
import view.GraphicView.AdminView;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class AdminController {
    private final AdminView adminView;
    private final UserDAO userDAO;
    private final Credentials credentials;
    private final Connection connection;

    public AdminController(AdminView adminView, UserDAO userDAO, Credentials credentials, Connection connection) {
        this.adminView = adminView;
        this.userDAO = userDAO;
        this.credentials = credentials;
        this.connection = connection;

        loadUsers();
        setupView();
    }

    private void setupView() {
        adminView.setAddUserButtonListener(e -> setAddUserDialog());
        adminView.setDeleteUserButtonListener(e -> setDeleteUserDialog());

        int role = credentials.getRole();
        boolean show = (role == 3);  // Only Admin can manage users
        adminView.setAddButtonVisible(show);
        adminView.setDeleteButtonVisible(show);
    }

    private void loadUsers() {
        try {
            List<User> users = userDAO.getAllUsers(); // Load all users
            adminView.clearUsers();
            for (User user : users) {
                adminView.addUserItem(
                        user.getUsername(),
                        e -> selectUser(user.getUsername())
                );
            }
        } catch (SQLException e) {
            adminView.showError("Error loading users.");
        }
    }

    private void selectUser(String username) {
        String currentUsername = username;
    }

    public void setAddUserDialog() {
        try {
            String[] userDetails = adminView.showAddUserDialog();
            String username = userDetails[0];
            String password = userDetails[1];
            String name = userDetails[2];
            String surname = userDetails[3];
            switch (userDetails[4]) {
                case "Employee" -> userDetails[4] = "1";
                case "Project Manager" -> userDetails[4] = "2";
                case "Admin" -> userDetails[4] = "3";
                default -> userDetails[4] = "k";
            }
            int role = Integer.parseInt(userDetails[4]);

            if (username != null && !username.trim().isEmpty()) {
                if (userDAO.isUserExisting(username)) {
                    adminView.showError("User already exists.");
                    return;
                }

                userDAO.addUser(username, password, name, surname, role);
                adminView.showSuccess("User added successfully.");
                loadUsers();  // Reload users to include the new one
            } else {
                adminView.showError("Username and role cannot be empty.");
            }
        } catch (SQLException e) {
            adminView.showError("Error adding user.");
            e.printStackTrace();
        }
    }

    public void setDeleteUserDialog() {
        try {
            List<User> users = userDAO.getAllUsers();
            List<String> usernames = users.stream()
                    .map(User::getUsername)
                    .collect(Collectors.toList());

            String selectedUsername = adminView.showDeleteUserDialog(usernames);
            if (selectedUsername != null) {
                userDAO.deleteUser(selectedUsername);
                adminView.showSuccess("User deleted successfully.");
                loadUsers();
            }
        } catch (SQLException e) {
            adminView.showError("Error deleting user.");
            e.printStackTrace();
        }
    }
}
