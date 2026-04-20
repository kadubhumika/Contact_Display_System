package com.contacthub.ui;

import com.contacthub.ui.ContactBox;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;

public class ContactDetailPanel extends JPanel {

    public ContactDetailPanel(MainFrame frame, ContactBox c) {

        setLayout(new BorderLayout());

        // 🔝 TOP BAR
        JPanel top = new JPanel(new BorderLayout());

        JButton back = new JButton("←");
        JButton fav = new JButton("★");
        fav.setForeground(Color.GRAY);
        fav.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Added to Favorites ⭐");
        });
        JButton more = new JButton("⋮");
        JButton edit = new JButton("Edit");

        top.add(edit, BorderLayout.SOUTH);
        JButton uploadPic = new JButton("Upload Pic");
        add(uploadPic, BorderLayout.SOUTH);
        uploadPic.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int option = fileChooser.showOpenDialog(this);

            if (option == JFileChooser.APPROVE_OPTION) {
                try {
                    File file = fileChooser.getSelectedFile();

                    URL url = new URL("http://localhost:8080/contacts/upload/" + c.getId());
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Authorization", "Bearer " + Session.token);
                    conn.setDoOutput(true);

                    String boundary = "----WebKitFormBoundary";
                    conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

                    OutputStream os = conn.getOutputStream();

                    os.write(("--" + boundary + "\r\n").getBytes());
                    os.write(("Content-Disposition: form-data; name=\"file\"; filename=\"" + file.getName() + "\"\r\n").getBytes());
                    os.write(("Content-Type: application/octet-stream\r\n\r\n").getBytes());

                    Files.copy(file.toPath(), os);
                    os.write(("\r\n--" + boundary + "--\r\n").getBytes());

                    os.flush();

                    JOptionPane.showMessageDialog(this, "Uploaded!");

                    DashboardPanel d = new DashboardPanel(frame, "");
                    frame.switchPanel(d);
                    d.reloadContacts();

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        edit.addActionListener(e -> {
            String newName = JOptionPane.showInputDialog("Enter new name", c.getName());

            try {
                URL url = new URL("http://localhost:8080/contacts/" + c.getId());
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                conn.setRequestMethod("PUT");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Authorization", "Bearer " + Session.token);
                conn.setDoOutput(true);

                String json = "{ \"name\":\"" + newName + "\", \"phone\":\"" + c.getPhone() + "\", \"email\":\"" + c.getEmail() + "\" }";

                conn.getOutputStream().write(json.getBytes());

                int res = conn.getResponseCode();

                if(res == 200){
                    JOptionPane.showMessageDialog(this, "Updated");
                    frame.switchPanel(new DashboardPanel(frame, ""));
                } else {
                    JOptionPane.showMessageDialog(this, "Update failed");
                }
                DashboardPanel d = new DashboardPanel(frame, "");
                frame.switchPanel(d);
                d.reloadContacts();

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        top.add(back, BorderLayout.WEST);
        top.add(fav, BorderLayout.CENTER);

        top.add(more, BorderLayout.EAST);


        // 📄 DETAILS
        JTextArea info = new JTextArea(
                "Name: " + c.getName() +
                        "\nPhone: " + c.getPhone() +
                        "\nEmail: " + c.getEmail()
        );
        fav.addActionListener(e -> {
            try {
                URL url = new URL("http://localhost:8080/contacts/favorite/" + c.getId());
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                conn.setRequestMethod("PUT");
                conn.setRequestProperty("Authorization", "Bearer " + Session.token);

                if (conn.getResponseCode() == 200) {

                    if (fav.getForeground() == Color.BLUE) {
                        fav.setForeground(Color.GRAY);
                    } else {
                        fav.setForeground(Color.BLUE);
                    }
                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        more.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Delete contact?");
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    URL url = new URL("http://localhost:8080/contacts/" + c.getId());
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                    conn.setRequestMethod("DELETE");
                    conn.setRequestProperty("Authorization", "Bearer " + Session.token);

                    if (conn.getResponseCode() == 200) {
                        JOptionPane.showMessageDialog(this, "Deleted");
                        frame.switchPanel(new DashboardPanel(frame, ""));
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        // 👉 BACK ACTION
        back.addActionListener(e -> {
            frame.switchPanel(new DashboardPanel(frame, ""));
        });

        add(top, BorderLayout.NORTH);
        add(info, BorderLayout.CENTER);
    }
}