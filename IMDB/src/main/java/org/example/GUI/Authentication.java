package org.example.GUI;

import org.example.IMDB;
import org.example.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Authentication extends JPanel {
    private final JTextField emailField;
    private final JPasswordField passwordField;

    public Authentication(CardLayout cardLayout, Container container) {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        JLabel usernameLabel = new JLabel("Email: ");
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(usernameLabel, gbc);

        emailField = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 0;
        add(emailField, gbc);

        JLabel passwordLabel = new JLabel("Password: ");
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(passwordLabel, gbc);

        passwordField = new JPasswordField(20);
        gbc.gridx = 1;
        gbc.gridy = 1;
        add(passwordField, gbc);

        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String email = emailField.getText();
                char[] password = passwordField.getPassword();

                boolean ok = false;
                for (User user : IMDB.getInstance().getUsers()) {
                    if (user.info.getCredentials().getEmail().equals(email) && user.info.getCredentials().getPassword().equals(new String(password))) {
                        IMDB.getInstance().loggedInUser = user;
                        ok = true;
                        break;
                    }
                }
                if (ok) {
                    IMDB.getInstance().initPanels();
                    passwordField.setText("");
                    emailField.setText("");
                    cardLayout.show(container, "MainPage");
                } else {
                    JOptionPane.showMessageDialog(Authentication.this, "Invalid credentials!", "Error", JOptionPane.ERROR_MESSAGE);
                    passwordField.setText("");
                }
            }
        });

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(loginButton, gbc);
    }
}
