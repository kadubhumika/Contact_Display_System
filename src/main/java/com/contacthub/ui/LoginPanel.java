package com.contacthub.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class LoginPanel extends JPanel {


    public LoginPanel(MainFrame frame) {
        // Center the card using GridBagLayout
        setLayout(new GridBagLayout());
        setBackground(new Color(245, 247, 250));

        // Create the "Card" container
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(170, 100, 189), 5),
                new EmptyBorder(30, 40, 30, 40)
        ));

        // Title
        JLabel title = new JLabel("Welcome Back");
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setForeground(new Color(50, 50, 50));

        // Inputs
        JTextField email = createStyledTextField("Email");
        JPasswordField password = createStyledPasswordField("Password");

        // Login Button
        JButton loginBtn = new JButton("Login");
        loginBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        loginBtn.setBackground(new Color(79, 70, 229)); // Modern Indigo
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setFocusPainted(false);
        loginBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
        loginBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Signup Link Button
        JButton signupBtn = new JButton("Don't have an account? Sign up");
        signupBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        signupBtn.setContentAreaFilled(false);
        signupBtn.setBorderPainted(false);
        signupBtn.setForeground(new Color(62, 61, 61));
        signupBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Logic (Kept exactly the same)
        loginBtn.addActionListener(e -> {
            new Thread(() -> {
                try {
                    // FIXED URL
                    URL url = new URL("http://localhost:8080/auth/login");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json");
                    conn.setDoOutput(true);

                    String emailText = email.getText().trim();
                    String passwordText = new String(password.getPassword()).trim();
                    String json = String.format("{\"email\":\"%s\", \"password\":\"%s\"}", emailText, passwordText);

                    try (OutputStream os = conn.getOutputStream()) {
                        os.write(json.getBytes("utf-8"));
                    }

                    int status = conn.getResponseCode();
                    InputStream stream = (status >= 400) ? conn.getErrorStream() : conn.getInputStream();

                    if (stream == null) {
                        throw new RuntimeException("No response from server. Check backend URL or server.");
                    }

                    BufferedReader br = new BufferedReader(new InputStreamReader(stream));

                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) response.append(line);

                    SwingUtilities.invokeLater(() -> {
                        if (status >= 400) {
                            JOptionPane.showMessageDialog(this, "Login Failed: " + response.toString());
                        } else {
                            Session.token = response.toString();
                            frame.switchPanel(new DashboardPanel(frame, emailText));
                        }
                    });

                } catch (Exception ex) {
                    ex.printStackTrace();
                    SwingUtilities.invokeLater(() ->
                            JOptionPane.showMessageDialog(this, "Login Failed (Connection Error)"));
                }
            }).start();
        });


        signupBtn.addActionListener(e -> {
            frame.switchPanel(new SignupPanel(frame));
        });

        // Assembly
        card.add(title);
        card.add(Box.createRigidArea(new Dimension(0, 25)));
        card.add(email);
        card.add(Box.createRigidArea(new Dimension(0, 15)));
        card.add(password);
        card.add(Box.createRigidArea(new Dimension(0, 25)));
        card.add(loginBtn);
        card.add(Box.createRigidArea(new Dimension(0, 10)));
        card.add(signupBtn);

        add(card);
    }

    // Helper methods for consistent styling
    private JTextField createStyledTextField(String title) {
        JTextField field = new JTextField();
        field.setMaximumSize(new Dimension(300, 55));
        field.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)), title));
        return field;
    }

    private JPasswordField createStyledPasswordField(String title) {
        JPasswordField field = new JPasswordField();
        field.setMaximumSize(new Dimension(300, 55));
        field.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)), title));
        return field;
    }
}
