package org.example.GUI.Menu;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.example.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UsersPanel extends JPanel {
    private JList<User> usersList;
    private DefaultListModel<User> listModel;
    private List<User> users;
    private JPanel userPanel = new JPanel(new BorderLayout());
    CardLayout layout = new CardLayout();
    JPanel cards = new JPanel(layout);

    public UsersPanel(CardLayout cardLayout, JPanel cardPanel) {
        setLayout(new GridBagLayout());
        users = IMDB.getInstance().getUsers();

        listModel = new DefaultListModel<>();
        updateUsersList();

        // Creating a border for the panel
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(10, 10, 10, 10),
                BorderFactory.createLineBorder(Color.BLACK)
        ));

        JPanel centeredPanel = new JPanel(new BorderLayout());
        centeredPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        cards.add(centeredPanel, "main");
        cards.add(userPanel, "user");

        layout.show(cards, "main");
        usersList = new JList<>(listModel);
        usersList.setFixedCellHeight(50);
        usersList.setToolTipText("");

        // Display user information + hover to get full information
        usersList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel renderer = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                User user = (User) value;

                String text = "<" + user.type + "> " + user.username;
                renderer.setText(text);
                renderer.setToolTipText(text);

                return renderer;
            }
        });

        // Buttons
        JButton addButton = new JButton("Add");
        addButton.addActionListener(e -> {
            userPanel.removeAll();
            JPanel newActorPanel = createUserPanel();
            userPanel.add(newActorPanel);
            userPanel.revalidate();
            userPanel.repaint();
            layout.show(cards, "user");
        });

        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(e -> {
            int selectedIndex = usersList.getSelectedIndex();
            if (selectedIndex != -1) {
                int dialogResult = JOptionPane.showConfirmDialog(null,
                        "Are you sure you want to delete this user?", "Confirmation", JOptionPane.YES_NO_OPTION);
                if (dialogResult == JOptionPane.YES_OPTION) {
                    User selectedUser = usersList.getSelectedValue();

                    // Delete requests
                    Iterator<Request> iterator = IMDB.getInstance().getRequests().iterator();
                    while (iterator.hasNext()) {
                        Request request = iterator.next();
                        if (request.requesterUsername.equals(selectedUser.username)) {
                            // Delete requests from staff
                            for (User u : IMDB.getInstance().getUsers()) {
                                if (u instanceof Staff<?>) {
                                    ((Staff<?>) u).requests.remove(request);
                                }
                            }
                            // Delete requests from main db
                            iterator.remove();
                        }
                    }

                    // Delete ratings
                    for (Production production : IMDB.getInstance().getProductions()) {
                        for (Rating rating : production.ratings) {
                            if (rating.username.equals(selectedUser.username)) {
                                production.ratings.remove(rating);
                                break;
                            }
                        }
                    }

                    // Delete user
                    ((Admin) IMDB.getInstance().loggedInUser).removeUser(selectedUser);
                    updateUsersList();
                }
            } else {
                JOptionPane.showMessageDialog(null, "Please select a user to delete!");
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);

        centeredPanel.add(buttonPanel, BorderLayout.NORTH);

        JButton backButton = new JButton("Back");
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(cardPanel, "MainMenu");
            }
        });

        JScrollPane scrollPane = new JScrollPane(usersList);
        scrollPane.setPreferredSize(new Dimension(250, 300));
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        centeredPanel.add(scrollPane, BorderLayout.CENTER);
        centeredPanel.add(backButton, BorderLayout.SOUTH);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;

        add(cards, gbc);
    }

    public void updateUsersList() {
        listModel.clear();

        for (User user : users) {
            listModel.addElement(user);
        }
    }
    private JPanel createUserPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setPreferredSize(new Dimension(300, 200));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Type
        String[] types = {"Regular", "Contributor", "Admin"};
        JComboBox<String> typeField = new JComboBox<>(types);
        JLabel typeLabel = new JLabel("Type");
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(typeLabel, gbc);
        gbc.gridx = 1;
        panel.add(typeField, gbc);

        // Name
        JLabel nameLabel = new JLabel("Name:");
        JTextField nameField = new JTextField(15);
        nameLabel.setPreferredSize(new Dimension(100, 20));
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(nameLabel, gbc);
        gbc.gridx = 1;
        panel.add(nameField, gbc);

        // Age
        JLabel ageLabel = new JLabel("Age:");
        SpinnerModel ageModel = new SpinnerNumberModel(1, 1, 150, 1);
        JSpinner ageSpinner = new JSpinner(ageModel);
        ageLabel.setPreferredSize(new Dimension(100, 20));
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(ageLabel, gbc);
        gbc.gridx = 1;
        panel.add(ageSpinner, gbc);

        // Country
        JLabel countryLabel = new JLabel("Country:");
        JTextField countryField = new JTextField(15);
        countryLabel.setPreferredSize(new Dimension(100, 20));
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(countryLabel, gbc);
        gbc.gridx = 1;
        panel.add(countryField, gbc);

        // Gender
        String[] genders = {"M", "F", "N"};
        JComboBox<String> genderField = new JComboBox<>(genders);
        JLabel genderLabel = new JLabel("Gender");
        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(genderLabel, gbc);
        gbc.gridx = 1;
        panel.add(genderField, gbc);

        // Birthdate
        JLabel dateLabel = new JLabel("Birthdate:");
        SpinnerModel dateModel = new SpinnerDateModel();
        JSpinner dateSpinner = new JSpinner(dateModel);
        ageLabel.setPreferredSize(new Dimension(100, 20));
        gbc.gridx = 0;
        gbc.gridy = 5;
        panel.add(dateLabel, gbc);
        gbc.gridx = 1;
        panel.add(dateSpinner, gbc);

        // Email
        JLabel emailLabel = new JLabel("Email:");
        JTextField emailField = new JTextField(15);
        emailLabel.setPreferredSize(new Dimension(100, 20));
        gbc.gridx = 0;
        gbc.gridy = 6;
        panel.add(emailLabel, gbc);
        gbc.gridx = 1;
        panel.add(emailField, gbc);

        // Password
        JLabel passwordLabel = new JLabel("Password:");
        JTextField passwordField = new JTextField(15);
        passwordLabel.setPreferredSize(new Dimension(100, 20));
        Random random = new Random();
        passwordField.setText(generatePassword(random.nextInt(11) + 10));
        gbc.gridx = 0;
        gbc.gridy = 7;
        panel.add(passwordLabel, gbc);
        gbc.gridx = 1;
        panel.add(passwordField, gbc);

        // Submit
        JButton submitButton = new JButton("Submit");
        submitButton.addActionListener(e -> {
            User.Information.InformationBuilder builder = new User.Information.InformationBuilder();
            builder.name(nameField.getText());
            AccountType type = AccountType.valueOf((String) typeField.getSelectedItem());
            builder.country(countryField.getText());
            builder.gender(((String) genderField.getSelectedItem()).charAt(0));

            Date date = (Date) dateSpinner.getValue();
            builder.birth_date(LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault()).withNano(0));
            builder.credentials(new Credentials(emailField.getText(), passwordField.getText()));

            boolean validData = true;
            Object value = ageSpinner.getValue();
            boolean ok = true;
            if (value instanceof Integer) {
                builder.age((int) value);
                if ((int) value <= 0) {
                    ok = false;
                }
            } else {
                ok = false;
            }
            if (!ok) {
                validData = false;
                JOptionPane.showMessageDialog(null, "Invalid age!", "Error", JOptionPane.ERROR_MESSAGE);
            }
            if (validData) {
                if (!checkEmail(emailField.getText())) {
                    validData = false;
                    JOptionPane.showMessageDialog(null, "Invalid email!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
            if (validData) {
                User.Information information = builder.build();
                UserFactory<?> userFactory = new UserFactory<>();
                User new_user = userFactory.createUser(type, information, generateUsername(information.name), 0,
                        new ArrayList<>(), new TreeSet<>(), new ArrayList<>(), new TreeSet<>());
                ((Admin) IMDB.getInstance().loggedInUser).addUser(new_user);
                updateUsersList();
                JOptionPane.showMessageDialog(null, "User created successfully!");
                layout.show(cards, "main");
            }
        });
        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(submitButton, gbc);

        JButton backButton = new JButton("Back");
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                layout.show(cards, "main");
            }
        });
        gbc.gridy = 9;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(backButton, gbc);

        return panel;
    }
    private String generateUsername(String name) {
        String[] nameParts = name.toLowerCase().split(" ");
        String username = String.join("_", nameParts);

        Random random = new Random();
        while (true) {
            int randomNumber = random.nextInt(9900) + 100;
            username += "_" + randomNumber;
            boolean ok = true;
            for (User user : IMDB.getInstance().getUsers()) {
                if (user.username.equals(username)) {
                    ok = false;
                    break;
                }
            }
            if (ok) {
                break;
            }
        }

        return username;
    }
    private String generatePassword(int length) {
        String upperCaseChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lowerCaseChars = "abcdefghijklmnopqrstuvwxyz";
        String numbers = "0123456789";
        String specialChars = "!@#$%^&*()-_=+[]{}|;:,.<>?";

        String allChars = upperCaseChars + lowerCaseChars + numbers + specialChars;

        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder();

        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(allChars.length());
            password.append(allChars.charAt(randomIndex));
        }

        return password.toString();
    }
    private boolean checkEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}
