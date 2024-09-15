package view.GraphicView;

import javax.swing.*;
import java.awt.*;

public class BackgroundPanel extends JPanel {
    private transient Image backgroundImage;

    public BackgroundPanel(String imagePath) {
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
