package com.contacthub.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class SignupPanel extends JPanel {
    public SignupPanel(MainFrame frame) {
        // Use GridBagLayout to center the "card" in the middle of the screen
        setLayout(new GridBagLayout());
        setBackground(new Color(245, 245, 245)); // Light gray background

        // --- Create the "Card" ---
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(90, 150, 196), 5, true), // Rounded edge
                new EmptyBorder(30, 40, 30, 40) // Padding inside the card
        ));
        card.setPreferredSize(new Dimension(350, 450));

        // --- Components ---
        JLabel title = new JLabel("SIGNUP");
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setForeground(new Color(50, 50, 50));

        JTextField email = createStyledField("Email");
        JPasswordField password = createStyledPasswordField("Password");

        JButton signupBtn = new JButton("Signup");
        stylePrimaryButton(signupBtn);

        JButton loginBtn = new JButton("Already have an account? Login");
        styleGhostButton(loginBtn);

        // --- Logic (Kept same as original) ---
        signupBtn.addActionListener(e -> {
            new Thread(() -> {
                try {
                    URL url = new URL("http://localhost:8080/auth/signup");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json");
                    conn.setDoOutput(true);

                    String emailText = email.getText().trim();
                    String passwordText = new String(password.getPassword()).trim();

                    String json = String.format(
                            "{\"email\":\"%s\", \"password\":\"%s\"}",
                            emailText, passwordText
                    );

                    try (OutputStream os = conn.getOutputStream()) {
                        os.write(json.getBytes("utf-8"));
                    }

                    int status = conn.getResponseCode();

                    InputStream stream = (status >= 400)
                            ? conn.getErrorStream()
                            : conn.getInputStream();

                    BufferedReader br = new BufferedReader(new InputStreamReader(stream));

                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        response.append(line);
                    }

                    SwingUtilities.invokeLater(() -> {
                        if (status >= 400) {
                            JOptionPane.showMessageDialog(this,
                                    "Signup Failed: " + response.toString());
                        } else {
                            JOptionPane.showMessageDialog(this,
                                    "Signup Successful!");
                            frame.switchPanel(new LoginPanel(frame));
                        }
                    });

                } catch (Exception ex) {
                    ex.printStackTrace();
                    SwingUtilities.invokeLater(() ->
                            JOptionPane.showMessageDialog(this,
                                    "Signup Failed (Connection Error)"));
                }
            }).start();
        });

        loginBtn.addActionListener(e -> {
            frame.switchPanel(new LoginPanel(frame));
        });

        // --- Assembly ---
        card.add(title);
        card.add(Box.createRigidArea(new Dimension(0, 30)));
        card.add(email);
        card.add(Box.createRigidArea(new Dimension(0, 15)));
        card.add(password);
        card.add(Box.createRigidArea(new Dimension(0, 25)));
        card.add(signupBtn);
        card.add(Box.createRigidArea(new Dimension(0, 10)));
        card.add(loginBtn);

        add(card); // Add the centered card to the main panel
    }

    // Helper to create modern looking inputs
    private JTextField createStyledField(String placeholder) {
        JTextField field = new JTextField();
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        field.setBorder(BorderFactory.createTitledBorder(placeholder));
        return field;
    }

    private JPasswordField createStyledPasswordField(String placeholder) {
        JPasswordField field = new JPasswordField();
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        field.setBorder(BorderFactory.createTitledBorder(placeholder));
        return field;
    }

    private void stylePrimaryButton(JButton btn) {
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btn.setBackground(new Color(63, 81, 181)); // Modern Blue
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("SansSerif", Font.BOLD, 14));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void styleGhostButton(JButton btn) {
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setForeground(new Color(100, 100, 100));
        btn.setFont(new Font("SansSerif", Font.PLAIN, 12));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
}
