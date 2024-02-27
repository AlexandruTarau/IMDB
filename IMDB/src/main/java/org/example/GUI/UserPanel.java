package org.example.GUI;

import org.example.IMDB;
import org.example.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class UserPanel extends JPanel {
    JTextArea userInfoTextArea = new JTextArea();
    public UserPanel(CardLayout cardLayout, JPanel cardPanel) {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        userInfoTextArea.setPreferredSize(new Dimension(300, 200));
        userInfoTextArea.setEditable(false);
        userInfoTextArea.setLineWrap(true);
        userInfoTextArea.setWrapStyleWord(true);

        updateUser();

        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new JScrollPane(userInfoTextArea), gbc);

        JButton backButton = new JButton("Back");
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(cardPanel, "MainPage");
            }
        });
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(backButton, gbc);
    }
    public void updateUser() {
        User user = IMDB.getInstance().loggedInUser;
        userInfoTextArea.setText("");
        userInfoTextArea.append("Type: " + user.type + "\n");
        userInfoTextArea.append("Username: " + user.username + "\n");
        userInfoTextArea.append("Experience: " + (user.experience == -1 ? "-" : user.experience) + "\n");

        userInfoTextArea.append("\nINFO\n");
        userInfoTextArea.append("Name: " + user.info.name + "\n");
        userInfoTextArea.append("Age: " + user.info.age + "\n");
        userInfoTextArea.append("Country: " + user.info.country + "\n");
        userInfoTextArea.append("Gender: " + user.info.gender + "\n");
        userInfoTextArea.append("Birthdate: " + user.info.getBirth_date() + "\n");
    }
}
