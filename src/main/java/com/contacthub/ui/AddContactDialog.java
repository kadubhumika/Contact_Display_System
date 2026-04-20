package com.contacthub.ui;

import javax.swing.*;
import java.awt.*;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class AddContactDialog extends JDialog {

    public AddContactDialog(JFrame frame, DefaultListModel<ContactBox> model,DashboardPanel dashboard) {
        setTitle("Add Contact");
        setSize(300, 250);
        setLayout(new GridLayout(4, 1));

        JTextField name = new JTextField();
        name.setBorder(BorderFactory.createTitledBorder("Name"));

        JTextField phone = new JTextField();
        phone.setBorder(BorderFactory.createTitledBorder("Phone"));

        JTextField email = new JTextField();
        email.setBorder(BorderFactory.createTitledBorder("Email"));

        JButton save = new JButton("Save");

        save.addActionListener(e -> {
            try {
                URL url = new URL("http://localhost:8080/contacts/save");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Authorization", "Bearer " + Session.token);
                conn.setDoOutput(true);

                String json = "{ \"name\":\"" + name.getText() + "\", " +
                        "\"phone\":\"" + phone.getText() + "\", " +
                        "\"email\":\"" + email.getText() + "\" }";

                OutputStream os = conn.getOutputStream();
                os.write(json.getBytes());
                os.flush();

                if (conn.getResponseCode() == 200) {
                    JOptionPane.showMessageDialog(this, "Saved!");
                    dashboard.reloadContacts();// 🔥 IMPORTANT
                    dispose();
                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        add(name);
        add(phone);
        add(email);
        add(save);

        setVisible(true);
    }
}