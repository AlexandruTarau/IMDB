package org.example.GUI.Menu;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class NotificationsPanel extends JPanel {
    private final List<String> notifications;
    public NotificationsPanel(CardLayout cardLayout, JPanel cardPanel, List<String> notifications) {
        this.notifications = notifications;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setPreferredSize(new Dimension(300, 400));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        displayNotifications();

        JButton backButton = new JButton("Back");
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(cardPanel, "MainMenu");
            }
        });
        add(backButton, BorderLayout.WEST);
    }

    private void displayNotifications() {
        for (String notification : notifications) {
            JLabel label = new JLabel(notification);
            label.setAlignmentX(Component.CENTER_ALIGNMENT);
            add(label);
        }
    }
}
