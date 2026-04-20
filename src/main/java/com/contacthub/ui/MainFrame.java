package com.contacthub.ui;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    public MainFrame() {
        setTitle("Contact Hub");
        setSize(450, 650); // Slightly wider for better card proportions
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Center the window on the screen
        setLocationRelativeTo(null);

        // Optional: Set a modern Look and Feel (System default)
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Start with the Login screen
        switchPanel(new LoginPanel(this));

        setVisible(true);
    }

    /**
     * Smoothly switches between panels
     */
    public void switchPanel(JPanel panel) {
        setContentPane(panel);
        revalidate();
        repaint();
    }

    public static void main(String[] args) {
        // Run on the Event Dispatch Thread for thread-safety
        SwingUtilities.invokeLater(() -> {
            new MainFrame();
        });
    }
}
