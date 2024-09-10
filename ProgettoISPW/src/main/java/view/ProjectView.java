package view;

import controller.ApplicationController;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

public class ProjectView extends JFrame implements View {

    private JPanel projectListPanel;
    private JButton confirmButton;
    private JLabel projectDetailsLabel;
    private transient ApplicationController applicationController; // Per la navigazione

    private Map<JButton, String> projectButtonMap = new HashMap<>(); // Mappa per tracciare i pulsanti dei progetti e i loro nomi

    private String selectedProjectName; // Nome del progetto selezionato

    public ProjectView(ApplicationController applicationController) {
        this.applicationController = applicationController; // Inizializzazione dell'application controller

        // Impostazioni della finestra principale
        setTitle("Project View");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);  // Centra la finestra

        // Layout principale
        setLayout(new BorderLayout());

        // Pannello sinistro: Lista dei progetti
        JPanel leftPanel = new JPanel();
        leftPanel.setBackground(new Color(30, 33, 40)); // Colore di sfondo scuro
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setPreferredSize(new Dimension(300, 600));
        leftPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Lista di progetti
        projectListPanel = new JPanel();
        projectListPanel.setLayout(new BoxLayout(projectListPanel, BoxLayout.Y_AXIS));
        projectListPanel.setBackground(new Color(30, 33, 40));

        // Scroll per il pannello dei progetti
        JScrollPane projectScrollPane = new JScrollPane(projectListPanel);
        projectScrollPane.setBorder(null);
        leftPanel.add(projectScrollPane);

        // Pannello destro: Dettagli del progetto con sfondo
        BackgroundPanel rightPanel = new BackgroundPanel("background.jpg"); // Usa BackgroundPanel per mostrare l'immagine di sfondo
        rightPanel.setLayout(new BorderLayout());

        // Dettagli del progetto selezionato
        projectDetailsLabel = new JLabel("Select a project to see details");
        projectDetailsLabel.setFont(new Font("Arial", Font.BOLD, 18));
        projectDetailsLabel.setForeground(Color.WHITE);
        projectDetailsLabel.setBorder(new EmptyBorder(10, 10, 10, 10));
        rightPanel.add(projectDetailsLabel, BorderLayout.NORTH);

        // Pulsante di conferma
        confirmButton = new JButton("Open Conversation");
        confirmButton.setEnabled(false); // Disabilitato fino a quando un progetto non viene selezionato
        rightPanel.add(confirmButton, BorderLayout.SOUTH);

        // Aggiunta dei pannelli alla finestra
        add(leftPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.CENTER);

        // Visualizza la finestra
        setVisible(true);
    }

    // Classe interna per gestire il pannello con immagine di sfondo
    private class BackgroundPanel extends JPanel {
        private transient Image backgroundImage;

        public BackgroundPanel(String imagePath) {
            // Carica l'immagine di sfondo
            ImageIcon icon = createImageIcon(imagePath);
            if (icon != null) {
                this.backgroundImage = icon.getImage();
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (backgroundImage != null) {
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        }

        private ImageIcon createImageIcon(String path) {
            java.net.URL imgURL = getClass().getClassLoader().getResource(path);
            if (imgURL != null) {
                return new ImageIcon(imgURL);
            } else {
                System.err.println("Couldn't find file: " + path);
                return null;
            }
        }
    }

    // Metodo per aggiungere un progetto alla lista dei progetti
    public void addProjectItem(String projectName, String projectDescription) {
        JButton projectButton = new JButton(projectName);
        projectButton.setBackground(new Color(42, 46, 54));
        projectButton.setForeground(Color.BLACK);
        projectButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        projectButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        // Mappa il pulsante al nome del progetto
        projectButtonMap.put(projectButton, projectName);

        projectButton.addActionListener(e -> {
            // Aggiorna i dettagli del progetto selezionato
            selectedProjectName = projectName; // Aggiorna il progetto selezionato
            projectDetailsLabel.setText("<html><h2>" + projectName + "</h2><p>" + projectDescription + "</p></html>");
            confirmButton.setEnabled(true); // Abilita il pulsante di conferma
        });

        projectListPanel.add(projectButton);
        projectListPanel.add(Box.createRigidArea(new Dimension(0, 5))); // Spaziatura
        refresh();
    }

    // Metodo per impostare il listener del pulsante di conferma
    public void setConfirmButtonListener(ActionListener listener) {
        confirmButton.addActionListener(listener);
    }

    // Metodo per ottenere il nome del progetto selezionato
    public String getSelectedProjectName() {
        return selectedProjectName;
    }

    // Metodo per mostrare un messaggio di errore
    public void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
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
        applicationController.back(); // Gestisce la navigazione indietro tramite ApplicationController
    }

    // Metodo per svuotare la lista dei progetti
    public void clearProjects() {
        projectListPanel.removeAll();
        refresh();
    }
}
