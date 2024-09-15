package view.GraphicView;

import controller.ApplicationController.GraphicApplicationController;
import view.View;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;

public class GraphicLoginView extends JFrame implements View {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton submitButton;
    private JLabel forgotPasswordLabel;

    public GraphicLoginView(GraphicApplicationController applicationController) {
        // Impostazioni della finestra principale
        setTitle("Login Page");
        setSize(400, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);  // Centra la finestra sullo schermo

        // Pannello principale con layout assoluto e sfondo
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                ImageIcon backgroundImage = createImageIcon("background.jpg");
                if (backgroundImage != null) {
                    g.drawImage(backgroundImage.getImage(), 0, 0, getWidth(), getHeight(), this);
                }
            }
        };
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new EmptyBorder(50, 20, 50, 20));
        mainPanel.setOpaque(false);

        // Icona utente
        ImageIcon userIconImage = createImageIcon("userIcon.png");
        if (userIconImage != null) {
            Image scaledUserIcon = userIconImage.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
            JLabel userIcon = new JLabel(new ImageIcon(scaledUserIcon));
            userIcon.setAlignmentX(Component.CENTER_ALIGNMENT);
            mainPanel.add(userIcon);
            mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        } else {
            System.err.println("Icon not found.");
        }

        // Campo username
        usernameField = createTextField("Username");
        mainPanel.add(usernameField);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Campo password
        passwordField = createPasswordField("Password");
        mainPanel.add(passwordField);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Pulsante Submit
        submitButton = new JButton("SUBMIT");
        submitButton.setFocusPainted(false);
        submitButton.setBackground(new Color(34, 177, 76));
        submitButton.setForeground(Color.BLACK);
        submitButton.setFont(new Font("Arial", Font.BOLD, 16));
        submitButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        submitButton.setMaximumSize(new Dimension(200, 40));
        mainPanel.add(submitButton);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Collegamento "Forgot Password?" (TBI)
        forgotPasswordLabel = new JLabel("Forgot Password?");
        forgotPasswordLabel.setForeground(Color.WHITE);
        forgotPasswordLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        forgotPasswordLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(forgotPasswordLabel);

        // Aggiunta del pannello principale al frame
        setContentPane(mainPanel);
        setVisible(true);
    }

    @Override
    public void display() {
        setVisible(true);
    }

    @Override
    public void close() {
        dispose();
    }

    @Override
    public void refresh() {
        revalidate();
        repaint();
    }

    @Override
    public void back() {
        // Non necessario per la schermata di login
    }

    // Metodo per creare un JTextField con un placeholder
    private JTextField createTextField(String placeholder) {
        JTextField textField = new JTextField(15);
        textField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
                BorderFactory.createEmptyBorder(5, 15, 5, 15)));
        textField.setText(placeholder);
        textField.setForeground(Color.GRAY);
        textField.addFocusListener(new java.awt.event.FocusAdapter() {

            @Override
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (textField.getText().equals(placeholder)) {
                    textField.setText("");
                    textField.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (textField.getText().isEmpty()) {
                    textField.setForeground(Color.GRAY);
                    textField.setText(placeholder);
                }
            }
        });
        return textField;
    }

    // Metodo per creare un JPasswordField con un placeholder
    private JPasswordField createPasswordField(String placeholder) {
        JPasswordField phPasswordField = new JPasswordField(15);
        phPasswordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        phPasswordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
                BorderFactory.createEmptyBorder(5, 15, 5, 15)));
        phPasswordField.setEchoChar((char) 0);  // Mostra il placeholder
        phPasswordField.setText(placeholder);
        phPasswordField.setForeground(Color.GRAY);
        phPasswordField.addFocusListener(new java.awt.event.FocusAdapter() {

            @Override
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (new String(phPasswordField.getPassword()).equals(placeholder)) {
                    phPasswordField.setText("");
                    phPasswordField.setForeground(Color.BLACK);
                    phPasswordField.setEchoChar('•');  // Mostra il carattere di mascheramento della password
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (new String(phPasswordField.getPassword()).isEmpty()) {
                    phPasswordField.setForeground(Color.GRAY);
                    phPasswordField.setText(placeholder);
                    phPasswordField.setEchoChar((char) 0);  // Mostra il placeholder di nuovo
                }
            }
        });
        return phPasswordField;
    }

    // Metodo per caricare un'immagine dal classpath
    private ImageIcon createImageIcon(String path) {
        java.net.URL imgURL = getClass().getClassLoader().getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }

    // Metodi per ottenere l'input dell'utente
    public String getUsername() {
        return usernameField.getText();
    }

    public String getPassword() {
        return new String(passwordField.getPassword());
    }

    // Metodo per aggiungere un listener al pulsante submit
    public void setSubmitButtonListener(ActionListener listener) {
        submitButton.addActionListener(listener);
    }

    // Metodi per mostrare messaggi
    @Override
    public void showError(String message) {
        JOptionPane.showMessageDialog(this, message);
    }

    public void showSuccess(String message) {
        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }
}
