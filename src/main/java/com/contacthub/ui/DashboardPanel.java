package com.contacthub.ui;

import com.contacthub.ui.ContactBox;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;



public class DashboardPanel extends JPanel {


    DefaultListModel<ContactBox> model = new DefaultListModel<>();
    JList<ContactBox> contactList = new JList<>(model);

    List<ContactBox> allContacts = new ArrayList<>();



    public DashboardPanel(MainFrame frame, String email) {


        setLayout(new BorderLayout());

        JPanel top = new JPanel(new BorderLayout());
        JPanel letters = new JPanel(new GridLayout(26,1));
        JLabel title = new JLabel("CONTACTS");
        title.setFont(new Font("Arial", Font.BOLD, 20));
        JButton menu = new JButton("⋮");
        menu.addActionListener(e -> {
            JPopupMenu popup = new JPopupMenu();

            JMenuItem createGroup = new JMenuItem("Create Group");
            JMenuItem showGroups = new JMenuItem("Show Groups");

            popup.add(createGroup);
            popup.add(showGroups);

            popup.show(menu, 0, menu.getHeight());

            //  create group
            createGroup.addActionListener(ev -> {
                JTextField groupName = new JTextField();

                DefaultListModel<ContactBox> dialogModel = new DefaultListModel<>();
                for (ContactBox c : allContacts) dialogModel.addElement(c);

                JList<ContactBox> list = new JList<>(dialogModel);
                list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

                list.addMouseListener(new MouseAdapter() {
                    public void mousePressed(MouseEvent e) {
                        int index = list.locationToIndex(e.getPoint());
                        if (list.isSelectedIndex(index)) {
                            list.removeSelectionInterval(index, index);
                        } else {
                            list.addSelectionInterval(index, index);
                        }
                    }
                });

                JScrollPane scroll = new JScrollPane(list);



                Object[] msg = {
                        "Group Name:", groupName,
                        "Select Contacts:", scroll
                };

                int option = JOptionPane.showConfirmDialog(this, msg, "Create Group", JOptionPane.OK_CANCEL_OPTION);

                if(option == JOptionPane.OK_OPTION){

                    try{

                        URL url = new URL("http://localhost:8080/groups/create-group");
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                        conn.setRequestMethod("POST");
                        conn.setRequestProperty("Content-Type","application/json");
                        conn.setRequestProperty("Authorization","Bearer "+Session.token);
                        conn.setDoOutput(true);

                        int[] selected = list.getSelectedIndices();

                        StringBuilder membersJson = new StringBuilder("[");
                        for(int i=0; i<selected.length; i++){
                            ContactBox c = dialogModel.getElementAt(selected[i]);
                            membersJson.append(c.getId());
                            if(i != selected.length-1) membersJson.append(",");
                        }
                        membersJson.append("]");


                        String finalJson = "{ \"name\":\""+groupName.getText()+"\", \"members\":"+membersJson+" }";

                        conn.getOutputStream().write(finalJson.getBytes());

                        int res = conn.getResponseCode();

                        if(res == 200){
                            JOptionPane.showMessageDialog(this,"Group Created ✅");
                            loadGroups();   
                        } else {
                            JOptionPane.showMessageDialog(this,"Failed ❌");
                        }

                    }catch(Exception ex){
                        ex.printStackTrace();
                    }
                }
            });



            showGroups.addActionListener(ev -> loadGroups());

        });


        top.add(title, BorderLayout.WEST);
        top.add(menu, BorderLayout.EAST);
        JButton favListBtn = new JButton("⭐");
        top.add(favListBtn, BorderLayout.CENTER);

        favListBtn.addActionListener(e -> loadFavorites());


        JTextField search = new JTextField();

        search.setBorder(BorderFactory.createTitledBorder("Search"));
        search.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                String text = search.getText().toLowerCase();

                DefaultListModel<ContactBox> filtered = new DefaultListModel<>();

                for (int i = 0; i < model.size(); i++) {
                    ContactBox c = model.get(i);
                    if (c.getName().toLowerCase().contains(text)) {
                        filtered.addElement(c);
                    }
                }

                contactList.setModel(filtered);
            }
        });

        JScrollPane scroll = new JScrollPane(contactList);
        contactList.setCellRenderer((list, value, index, isSelected, cellHasFocus) -> {

            JPanel panel = new JPanel(new BorderLayout());
            panel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
            panel.setBackground(isSelected ? new Color(220,235,255) : Color.WHITE);

            // LEFT IMAGE
            JLabel pic = new JLabel();
            pic.setPreferredSize(new Dimension(50,50));

            try {
                String url = (value.getProfilePic() != null && !value.getProfilePic().isEmpty())
                        ? "http://localhost:8080/contacts/image/" + value.getProfilePic()
                        : "https://ui-avatars.com/api/?name=" + value.getName();

                URL imageUrl = new URL(url);
                Image image = Toolkit.getDefaultToolkit().createImage(imageUrl);

                Image scaled = image.getScaledInstance(50, 50, Image.SCALE_SMOOTH);
                pic.setIcon(new ImageIcon(scaled));

            } catch (Exception e) {
                pic.setText("👤");
            }

            // NAME
            JLabel name = new JLabel(value.getName());
            name.setFont(new Font("SansSerif",Font.BOLD,16));

            panel.add(pic,BorderLayout.WEST);
            panel.add(name,BorderLayout.CENTER);

            return panel;
        });

        JButton addBtn = new JButton("+");


        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.add(addBtn);



        add(top, BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout());
        center.add(search, BorderLayout.NORTH);
        center.add(scroll, BorderLayout.CENTER);

        add(center, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);
        for(char ch='A'; ch<='Z'; ch++){
            char letter = ch;
            JButton btn = new JButton(String.valueOf(letter));

            btn.addActionListener(e -> filterByLetter(String.valueOf(letter)));

            letters.add(btn);
        }

        add(letters, BorderLayout.WEST);

        // dummy



        addBtn.addActionListener(e -> {
            new AddContactDialog(frame, model,this);
        });

        contactList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {

                int index = contactList.getSelectedIndex();

                if (index >= 0) {
                    ContactBox c = contactList.getModel().getElementAt(index);


                    frame.switchPanel(new ContactDetailPanel(frame, c));
                }
            }
        });
        loadContacts();
    }
    public void reloadContacts() {
        loadContacts();
    }
    private void loadGroups() {
        new Thread(() -> {
            try {
                URL url = new URL("http://localhost:8080/groups/show-group");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                conn.setRequestMethod("GET");
                conn.setRequestProperty("Authorization", "Bearer " + Session.token);

                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                StringBuilder response = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) response.append(line);

                SwingUtilities.invokeLater(() -> {

                    String json = response.toString();
                    String[] parts = json.split("\\{\"id\":");

                    DefaultListModel<String> groupModel = new DefaultListModel<>();

                    for (String part : parts) {
                        if (part.contains("name\":\"")) {
                            String name = extractValue(part, "name");
                            Long id = Long.parseLong(part.split(",")[0]);

                            groupModel.addElement(id + ":" + name);
                        }
                    }

                    JList<String> list = new JList<>(groupModel);

                    list.addMouseListener(new MouseAdapter() {
                        public void mouseClicked(MouseEvent e) {
                            String selected = list.getSelectedValue();
                            Long groupId = Long.parseLong(selected.split(":")[0]);

                            loadGroupContacts(groupId);
                        }
                    });

                    JOptionPane.showMessageDialog(this, new JScrollPane(list), "Groups", JOptionPane.PLAIN_MESSAGE);

                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
    private void loadGroupContacts(Long groupId) {
        new Thread(() -> {
            try {
                URL url = new URL("http://localhost:8080/groups/" + groupId + "/contacts");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                conn.setRequestMethod("GET");
                conn.setRequestProperty("Authorization", "Bearer " + Session.token);

                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();

                String line;
                while ((line = br.readLine()) != null) response.append(line);

                SwingUtilities.invokeLater(() -> {
                    model.clear();

                    String[] parts = response.toString().split("\\{\"id\":");

                    for (String part : parts) {
                        if (part.contains("name\":\"")) {
                            Long id = Long.parseLong(part.split(",")[0]);
                            String name = extractValue(part, "name");
                            String phone = extractValue(part, "phone");
                            String email = extractValue(part, "email");
                            String pic = extractValue(part, "profilePic");

                            model.addElement(new ContactBox(id, name, phone, email,pic));
                        }
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
    private void loadFavorites() {
        new Thread(() -> {
            try {
                URL url = new URL("http://localhost:8080/contacts/favorites");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                conn.setRequestMethod("GET");
                conn.setRequestProperty("Authorization", "Bearer " + Session.token);

                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) response.append(line);

                SwingUtilities.invokeLater(() -> {
                    model.clear();
                    allContacts.clear();

                    String json = response.toString();

                    String[] parts = json.split("\\{\"id\":");

                    for (String part : parts) {
                        if (part.contains("name\":\"")) {

                            Long id = Long.parseLong(part.split(",")[0]);
                            String name = extractValue(part, "name");
                            String phone = extractValue(part, "phone");
                            String email = extractValue(part, "email");
                            String pic = extractValue(part, "profilePic");

                            ContactBox c = new ContactBox(id, name, phone, email,pic);

                            allContacts.add(c);
                            model.addElement(c);
                        }
                    }
                });

            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }).start();
    }



    private void loadContacts() {
        new Thread(() -> { // Run in background so UI doesn't freeze
            try {
                URL url = new URL("http://localhost:8080/contacts/list-all");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Authorization", "Bearer " + Session.token);

                if (conn.getResponseCode() == 200) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) response.append(line);

                    // Use SwingUtilities to update the UI safely
                    SwingUtilities.invokeLater(() -> {
                        model.clear();
                        allContacts.clear();

                        String json = response.toString();

                        String[] parts = json.split("\\{\"id\":");

                        for (String part : parts) {
                            if (part.contains("name\":\"")) {

                                Long id = Long.parseLong(part.split(",")[0]);
                                String name = extractValue(part, "name");
                                String phone = extractValue(part, "phone");
                                String email = extractValue(part, "email");
                                String pic = extractValue(part, "profilePic");

                                ContactBox c = new ContactBox(id, name, phone, email,pic);

                                allContacts.add(c);
                                model.addElement(c);
                            }
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }


    // Helper method to grab values from the JSON string manually
    private String extractValue(String block, String key) {
        try {
            String search = "\"" + key + "\":\"";
            int start = block.indexOf(search) + search.length();
            int end = block.indexOf("\"", start);
            return block.substring(start, end);
        } catch (Exception e) { return "Unknown"; }
    }

    private void filterByLetter(String letter) {

        DefaultListModel<ContactBox> filtered = new DefaultListModel<>();

        for (ContactBox c : allContacts) {
            if (c.getName().toUpperCase().startsWith(letter)) {
                filtered.addElement(c);
            }
        }

        contactList.setModel(filtered);
    }

}